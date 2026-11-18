package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Block
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Edges
import net.kurobako.gemstone.vm.RegisterUse
import scala.annotation.tailrec
import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.Random

object RewriteSearch {

  case class Config(seed: Long, budget: Int, temperature: Double, states: Int, limit: Int)

  object Config {
    val default: Config = Config(seed = 1L, budget = 64, temperature = 1.0, states = 8, limit = 16)
  }

  case class Measure(cycles: Double, source: Architecture.Source)

  case class Rewrite(
      window: Window,
      original: Vec[String],
      rewrite: Vec[String],
      before: Measure,
      after: Measure,
      source: Architecture.Source,
      evidence: Proof.Report,
      reason: String,
      shift: Int
  ) {

    /** `shift` is how many bytes the code after the window moves; the cycles do not include what that does to it. */
    def line: String =
      f"${after.cycles}%.2f from ${before.cycles}%.2f cycles (${source.kind}), shifts $shift bytes: " +
        s"${rewrite.mkString("; ")} for ${original.mkString("; ")} ($reason)"
  }

  case class Report(
      window: Window,
      seed: Long,
      budget: Int,
      evaluated: Int,
      candidates: Int,
      wrong: Int,
      before: Measure,
      rewrites: Vec[Rewrite]
  )

  private enum Distance derives CanEqual {
    case Bits(bits: Long)
    case Refused(reason: String)
  }

  private case class Written(values: Map[String, Long], written: Set[String])

  def amdgpu(
      image: Vec[Byte],
      function: String,
      targetName: String,
      window: Window,
      launch: AMDGPUBlockCost.Launch,
      config: Config = Config.default
  ): Either[String, Report] = for {
    cost   <- AMDGPUBlockCost.of(image, function, targetName, launch)
    ready  <- search(image, function, targetName, window, config)
    report <- ready.run(cost)
  } yield report

  def prove(image: Vec[Byte], function: String, targetName: String, window: Window, candidate: Vec[String])
      : Either[String, Proof.Result] =
    search(image, function, targetName, window, Config.default).flatMap(_.evidence(candidate))

  def lines(label: String, window: Window, result: Either[String, Report]): Vec[String] = {
    val head = s"[rewrite] $label ${window.first}..${window.last}"
    result match {
      case Left(reason)  => Vec(s"$head skipped: $reason")
      case Right(report) =>
        Vec(
          s"$head live-out=${window.liveOut.toVec.sorted.mkString(",")} candidates=${report.candidates} " +
            s"wrong=${report.wrong} rewrites=${report.rewrites.size}"
        ) ++ report.rewrites.map(rewrite => s"[rewrite]   ${rewrite.line}")
    }
  }

  private def search(
      image: Vec[Byte],
      function: String,
      targetName: String,
      window: Window,
      config: Config
  ): Either[String, Search] = for {
    target <- targetName.refine[Target].left.map(_.raw)
    found  <- AMDGPUModel.staticCode(image, function)
    moves  <- AMDGPUMoves.of(image, function, targetName, window)
    parsed <- each(found._3.zip(found._2))((text, use) => instruction(text).map(_ -> use))
    block = found._1.blocks(window.block)
    ready <- Search.of(target, window, block, parsed, moves, config)
  } yield ready

  private def instruction(text: String): Either[String, Instruction] =
    Parser.parse(text).left.map(_.diagnostic.raw).flatMap(_.statements match {
      case Vec(Program.Statement.Instruction(value)) => Right(value)
      case _                                         => Left(s"not a single instruction: $text")
    })

  private def each[A, B: ClassTag](values: Vec[A])(f: A => Either[String, B]): Either[String, Vec[B]] =
    values.foldLeft[Either[String, Vec[B]]](Right(Vec.empty[B]))((found, value) =>
      found.flatMap(done => f(value).map(done :+ _))
    )

  private final class Search(
      target: Target,
      window: Window,
      block: Block,
      parsed: Vec[(Instruction, RegisterUse)],
      moves: AMDGPUMoves,
      config: Config
  ) {
    private val executor = Equivalence.vm(AMDGPUReference)
    private val waveSize = TargetDefaults.waveSize(target)
    private val at       = window.first - block.first
    private val length   = window.last - window.first + 1

    private val blockInstructions = parsed.slice(block.first, block.last + 1).map(_._1)
    private val blockUses         = parsed.slice(block.first, block.last + 1).map(_._2)
    private val original          = blockInstructions.slice(at, at + length)
    private val originalUses      = blockUses.slice(at, at + length)

    private val masks: Set[String] = Set(ImplicitState.Exec) ++ ImplicitState.of(OperandKind.`VCC`)

    private def units(name: String, width: Int): Vec[String] =
      if (masks(name)) ImplicitState.units(name, waveSize) else Liveness.units(name, width)

    private def units(use: RegisterUse, names: Vec[String]): Vec[String] =
      names.flatMap(name => units(name, use.unitsOf(name)))

    private def steps(sequence: Vec[Instruction]): Either[String, Vec[Equivalence.Step]] =
      each(sequence)(AMDGPUForms.of(target, _))

    private def liveOut(steps: Vec[Equivalence.Step]): Equivalence.LiveOut = {
      val written = originalUses.flatMap(use => units(use, use.writes)).toSet
      val live    = window.liveOut.intersect(written)
      val named   = steps.flatMap(step => step.kase.definition.results.flatMap(result => step.results(result.name)))
      Equivalence.LiveOut(
        named.distinct.filter(live.contains),
        memory = steps.exists(_.kase.definition.contexts.exists(_.tpe != LaneMasks.contextType))
      )
    }

    private val active = ImplicitState.units(ImplicitState.Exec, waveSize).toSet

    private def sample(name: String, tpe: Type, run: Int): Long =
      if (active(name)) tpe.represent(-1L)
      else {
        val mixed = config.seed ^ (name.hashCode.toLong * 0x9e3779b97f4a7c15L) ^ run.toLong
        Edges.sample(tpe, Random(mixed))
      }

    private def execute(
        sequence: Vec[Equivalence.Step],
        initial: Map[String, Long],
        types: Map[String, Type],
        harness: AMDGPUHarness
    ): Either[String, Written] =
      sequence.foldLeft[Either[String, Written]](Right(Written(initial, Set.empty))) { (found, step) =>
        found.flatMap(known =>
          executor(step.kase, step.inputs(known.values), harness).map { outputs =>
            val named = step.outputs(outputs).map((location, value) => location -> types(location).represent(value))
            Written(known.values ++ named, known.written ++ named.map(_._1))
          }
        )
      }

    private def changed[K, V](left: Map[K, V], right: Map[K, V])(using CanEqual[V, V]): Long =
      (left.keySet ++ right.keySet).count(key => left.get(key) != right.get(key)).toLong

    private def memory(expected: AMDGPUHarness, obtained: AMDGPUHarness, liveOut: Equivalence.LiveOut): Long =
      if (!liveOut.memory) 0L
      else {
        val (expectedGlobal, expectedShared, expectedVectors) = expected.writes
        val (obtainedGlobal, obtainedShared, obtainedVectors) = obtained.writes
        8L * (changed(expectedGlobal, obtainedGlobal) + changed(expectedShared, obtainedShared)) +
          32L * changed(expectedVectors, obtainedVectors)
      }

    private def differing(
        types: Map[String, Type],
        liveOut: Equivalence.LiveOut,
        expected: Written,
        obtained: Written
    ): Long =
      liveOut.locations.map { location =>
        val tpe = types(location)
        (expected.written.contains(location), obtained.written.contains(location)) match {
          case (true, true) =>
            val (left, right) = (expected.values(location), obtained.values(location))
            if (Edges.same(tpe)(left, right)) 0L else java.lang.Long.bitCount(left ^ right).toLong.min(tpe.bits.toLong)
          case _ => tpe.bits.toLong
        }
      }.sum

    private def distance(
        left: Vec[Equivalence.Step],
        right: Vec[Equivalence.Step],
        liveOut: Equivalence.LiveOut
    ): Distance =
      Equivalence.crossLane(left).orElse(Equivalence.crossLane(right)).toLeft(()).flatMap(_ =>
        Equivalence.validate(left.iterator ++ right.iterator, liveOut)
      ) match {
        case Left(reason) => Distance.Refused(reason)
        case Right(types) =>
          val names = types.keys.toVec.sorted
          val runs  = (0 until config.states).foldLeft[Either[String, Long]](Right(0L)) { (total, run) =>
            total.flatMap { sum =>
              val sampled = names.map(name => name -> sample(name, types(name), run)).toMap

              val expectedHarness = AMDGPUReference.harness(run.toLong)
              val obtainedHarness = AMDGPUReference.harness(run.toLong)
              (execute(left, sampled, types, expectedHarness), execute(right, sampled, types, obtainedHarness)) match {
                case (Right(expected), Right(obtained)) =>
                  Right(sum + differing(types, liveOut, expected, obtained) +
                    memory(expectedHarness, obtainedHarness, liveOut))
                case (Left(reason), _) => Left(reason)
                case (_, Left(reason)) => Left(reason)
              }
            }
          }
          runs.fold(Distance.Refused(_), Distance.Bits(_))
      }

    private def text(sequence: Vec[Instruction]): Vec[String] = sequence.map(Printer.render(_))

    private[check] def evidence(candidate: Vec[String]): Either[String, Proof.Result] = for {
      originalSteps <- steps(original)
      parsed        <- each(candidate)(instruction)
      right         <- steps(parsed)
    } yield Proof.prove(
      AMDGPUReference,
      executor,
      (originalSteps ++ right).flatMap(_.lane.cases),
      originalSteps,
      right,
      liveOut(originalSteps)
    )

    private case class Walk(
        state: Vec[Instruction],
        moves: AMDGPUMoves,
        cycles: Double,
        distance: Long,
        evaluated: Int
    )

    def run(cost: AMDGPUBlockCost): Either[String, Report] = for {
      originalSteps <- steps(original)
      whole         <- cost.cost(window, text(original))
    } yield {
      val out     = liveOut(originalSteps)
      val reason  = if (out.locations.isEmpty) "no write is live out" else "live-out writes proved equal"
      val before  = Measure(whole.cycles, whole.source)
      val random  = Random(config.seed)
      val costs   = mutable.Map.empty[String, Option[AMDGPUBlockCost.Cost]]
      val seen    = mutable.LinkedHashMap.empty[String, (Vec[Instruction], Long, AMDGPUBlockCost.Cost)]
      val deleted = AMDGPUMoves.Candidate(AMDGPUMoves.Kind.Delete, Vec.empty, Vec.empty)

      def visit(walk: Walk, candidate: AMDGPUMoves.Candidate): Walk = {
        val key   = text(candidate.instructions).mkString("; ")
        val found = for {
          bits <- steps(candidate.instructions).flatMap(right =>
            distance(originalSteps, right, out) match {
              case Distance.Bits(bits)   => Right(bits)
              case Distance.Refused(why) => Left(why)
            }
          ).toOption
          after <- costs.getOrElseUpdate(key, cost.cost(window, text(candidate.instructions)).toOption)
        } yield (bits, after)
        found.fold(walk.copy(evaluated = walk.evaluated + 1)) { (bits, after) =>
          if (!seen.contains(key)) seen.update(key, (candidate.instructions, bits, after))
          val value    = bits.toDouble + after.cycles
          val current  = walk.distance.toDouble + walk.cycles
          val accepted = value <= current || random.nextDouble() < math.exp((current - value) / config.temperature)
          if (accepted)
            Walk(
              candidate.instructions,
              walk.moves.from(candidate.instructions).getOrElse(walk.moves),
              after.cycles,
              bits,
              walk.evaluated + 1
            )
          else walk.copy(evaluated = walk.evaluated + 1)
        }
      }

      @tailrec
      def walk(iteration: Int, current: Walk): Walk =
        if (iteration == config.budget) current
        else {
          val pool       = current.moves.propose(random.nextLong(), config.limit)
          val considered =
            if (iteration == 0) pool :+ deleted
            else if (pool.isEmpty) Vec.empty[AMDGPUMoves.Candidate]
            else Vec(pool(random.nextInt(pool.size)))
          walk(iteration + 1, considered.foldLeft(current)(visit))
        }

      val start    = Walk(original, moves, before.cycles, 0L, 0)
      val walked   = if (out.locations.isEmpty) visit(start, deleted) else walk(0, start)
      val cheaper  = seen.values.filter((_, bits, after) => bits == 0L && after.cycles < before.cycles).toVec
      val lane     = originalSteps.flatMap(_.lane.cases)
      val rewrites = cheaper.flatMap { (sequence, _, after) =>
        steps(sequence).toOption.flatMap { right =>
          Proof.prove(AMDGPUReference, executor, lane ++ right.flatMap(_.lane.cases), originalSteps, right, out) match {
            case Proof.Result.Proved(evidence) =>
              val source = Architecture.Source.weakest(before.source, after.source)
              Some(
                Rewrite(
                  window,
                  text(original),
                  text(sequence),
                  before,
                  Measure(after.cycles, after.source),
                  source,
                  evidence,
                  reason,
                  after.shift
                )
              )
            case _ => None
          }
        }
      }
      Report(
        window,
        config.seed,
        config.budget,
        walked.evaluated,
        seen.size,
        seen.values.count(_._2 > 0L),
        before,
        rewrites
      )
    }
  }

  private object Search {
    def of(
        target: Target,
        window: Window,
        block: Block,
        parsed: Vec[(Instruction, RegisterUse)],
        moves: AMDGPUMoves,
        config: Config
    ): Either[String, Search] = Either.cond(
      block.first <= window.first && window.last <= block.last,
      new Search(target, window, block, parsed, moves, config),
      s"the window ${window.first}..${window.last} is not inside the block ${block.first}..${block.last}"
    )
  }
}
