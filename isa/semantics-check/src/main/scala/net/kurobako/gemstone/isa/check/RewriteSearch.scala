package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.isa.sass.DecodedInstruction
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

  case class Measure(cycles: Double, source: Architecture.Source, noise: Double = 0.0)

  private[check] def gain(before: Measure, after: Measure): Boolean =
    before.cycles - after.cycles > math.max(before.noise, after.noise)

  case class Rewrite(
      window: Window,
      original: Vec[String],
      candidate: Vec[String],
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
      rewrites: Vec[Rewrite],
      uncosted: Vec[(candidate: String, reason: String)]
  )

  private enum Distance derives CanEqual {
    case Bits(bits: Long)
    case Refused(reason: String)
  }

  private case class Written(values: Map[String, Long], written: Set[String])

  /** What the search takes of an instruction set: how it proves, names state and compares memory. */
  private trait Isa[I, H, L <: Lookup] {
    def reference: Reference[H, L]
    def executor: (Case, Vec[Long], H) => Either[String, Vec[Long]]
    def steps(sequence: Vec[I]): Either[String, Vec[Equivalence.Step]]
    def text(sequence: Vec[I]): Vec[String]

    /** The units a register of `width` is made of, as live-out sets name them. */
    def units(name: String, width: Int): Vec[String]

    /** The units a run holds at every one of its lanes. */
    def active: Set[String]

    /** The weighted count of memory locations on which two runs differ. */
    def memory(expected: H, obtained: H): Long
  }

  /** A source of one-move candidates, which follows the sequence the walk has moved to. */
  private trait Walker[I] {
    def propose(seed: Long, limit: Int): Vec[Moves.Candidate[I]]
    def from(sequence: Vec[I]): Option[Walker[I]]
  }

  /** The cycles of the region that holds a window once a sequence stands in the window's place. */
  private[check] type Costing[I] = (Window, Vec[I]) => Either[String, AMDGPUBlockCost.Cost]

  /** `cost` with each candidate held to at least the cost of the instructions it shares with `original`: adding
    * instructions cannot make a region cheaper.
    */
  private[check] def floored[I](text: Vec[I] => Vec[String], original: Vec[I])(cost: Costing[I]): Costing[I] =
    (window, candidate) =>
      cost(window, candidate).map { whole =>
        val kept = BlockRegion.common(text(original), text(candidate)).map((_, at) => candidate(at))
        if (kept.size == candidate.size) whole
        else
          cost(window, kept).fold(
            _ => whole,
            floor => if (floor.cycles > whole.cycles) floor.copy(shift = whole.shift) else whole
          )
      }

  private def changed[K, V](left: Map[K, V], right: Map[K, V])(using CanEqual[V, V]): Long =
    (left.keySet ++ right.keySet).count(key => left.get(key) != right.get(key)).toLong

  private final class AMDGPU(target: Target) extends Isa[Instruction, AMDGPUHarness, Lookup.Spellings] {
    private val waveSize = TargetDefaults.waveSize(target)
    private val masks    = Set(ImplicitState.Exec) ++ ImplicitState.of(OperandKind.`VCC`)

    val reference: Reference[AMDGPUHarness, Lookup.Spellings]                   = AMDGPUReference
    val executor: (Case, Vec[Long], AMDGPUHarness) => Either[String, Vec[Long]] = Equivalence.vm(AMDGPUReference)
    val active: Set[String] = ImplicitState.units(ImplicitState.Exec, waveSize).toSet

    def steps(sequence: Vec[Instruction]): Either[String, Vec[Equivalence.Step]] =
      each(sequence)(AMDGPUForms.of(target, _))

    def text(sequence: Vec[Instruction]): Vec[String] = sequence.map(Printer.render(_))

    def units(name: String, width: Int): Vec[String] =
      if (masks(name)) ImplicitState.units(name, waveSize) else Liveness.units(name, width)

    def memory(expected: AMDGPUHarness, obtained: AMDGPUHarness): Long = {
      val (expectedGlobal, expectedShared, expectedVectors) = expected.writes
      val (obtainedGlobal, obtainedShared, obtainedVectors) = obtained.writes
      8L * (changed(expectedGlobal, obtainedGlobal) + changed(expectedShared, obtainedShared)) +
        32L * changed(expectedVectors, obtainedVectors)
    }
  }

  private def walker(moves: AMDGPUMoves): Walker[Instruction] = new Walker[Instruction] {
    def propose(seed: Long, limit: Int): Vec[Moves.Candidate[Instruction]] = moves.propose(seed, limit)
    def from(sequence: Vec[Instruction]): Option[Walker[Instruction]]      = moves.from(sequence).toOption.map(walker)
  }

  private final class SASS(targetName: String) extends Isa[DecodedInstruction, NvidiaHarness, Lookup.Sass] {
    val reference: Reference[NvidiaHarness, Lookup.Sass]                        = SASSReference
    val executor: (Case, Vec[Long], NvidiaHarness) => Either[String, Vec[Long]] = Equivalence.vm(SASSReference)
    val active: Set[String]                                                     = Set.empty

    def steps(sequence: Vec[DecodedInstruction]): Either[String, Vec[Equivalence.Step]] =
      each(sequence)(instruction => SASSForms.of(targetName, instruction.operation))

    def text(sequence: Vec[DecodedInstruction]): Vec[String] =
      sequence.map(instruction =>
        net.kurobako.gemstone.isa.sass.asm.Printer.print(instruction.operation).getOrElse(instruction.mnemonic.raw)
      )

    def units(name: String, width: Int): Vec[String] = Liveness.units(name, width)

    def memory(expected: NvidiaHarness, obtained: NvidiaHarness): Long = {
      val (left, right) = (SASSReference.state(expected), SASSReference.state(obtained))
      8L * (changed(left.global, right.global) + changed(left.shared, right.shared))
    }
  }

  private def walker(moves: SASSMoves): Walker[DecodedInstruction] = new Walker[DecodedInstruction] {
    def propose(seed: Long, limit: Int): Vec[Moves.Candidate[DecodedInstruction]]   = moves.propose(seed, limit)
    def from(sequence: Vec[DecodedInstruction]): Option[Walker[DecodedInstruction]] =
      moves.from(sequence).toOption.map(walker)
  }

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
    report <- ready.run((at, sequence) => cost.cost(at, ready.isa.text(sequence)))
  } yield report

  def sass(
      image: Vec[Byte],
      function: String,
      targetName: String,
      window: Window,
      launch: AMDGPUBlockCost.Launch,
      config: Config = Config.default
  ): Either[String, Report] = for {
    cost  <- SASSBlockCost.of(image, function, targetName, launch)
    found <- SASSModel.staticCode(image, function)
    moves <- SASSMoves.of(image, function, targetName, window)
    held = found._3.slice(window.first, window.last + 1)
    uses = found._2.slice(window.first, window.last + 1)
    report <- new Search(SASS(targetName), window, held, uses, walker(moves), config).run(cost.cost)
  } yield report

  def prove(image: Vec[Byte], function: String, targetName: String, window: Window, candidate: Vec[String])
      : Either[String, Proof.Result] =
    search(image, function, targetName, window, Config.default).flatMap(ready =>
      each(candidate)(instruction).flatMap(ready.evidence)
    )

  def lines(label: String, window: Window, result: Either[String, Report]): Vec[String] = {
    val head = s"[rewrite] $label ${window.first}..${window.last}"
    result match {
      case Left(reason)  => Vec(s"$head skipped: $reason")
      case Right(report) =>
        Vec(
          s"$head live-out=${window.liveOut.toVec.sorted.mkString(",")} candidates=${report.candidates} " +
            s"wrong=${report.wrong} rewrites=${report.rewrites.size}"
        ) ++ report.rewrites.map(rewrite => s"[rewrite]   ${rewrite.line}") ++
          report.uncosted.groupMap(_.reason)(_.candidate).toVec.sortBy(_._1).map((reason, candidates) =>
            s"[rewrite]   not costed candidates=${candidates.size}: $reason"
          )
    }
  }

  private def search(
      image: Vec[Byte],
      function: String,
      targetName: String,
      window: Window,
      config: Config
  ): Either[String, Search[Instruction, AMDGPUHarness, Lookup.Spellings]] = for {
    target <- targetName.refine[Target].left.map(_.raw)
    found  <- AMDGPUModel.staticCode(image, function)
    moves  <- AMDGPUMoves.of(image, function, targetName, window)
    block = found._1.blocks(window.block)
    _ <- Either.cond(
      block.first <= window.first && window.last <= block.last,
      (),
      s"the window ${window.first}..${window.last} is not inside the block ${block.first}..${block.last}"
    )
    held <-
      each(found._3.zip(found._2).slice(window.first, window.last + 1))((text, use) => instruction(text).map(_ -> use))
  } yield new Search(AMDGPU(target), window, held.map(_._1), held.map(_._2), walker(moves), config)

  private def instruction(text: String): Either[String, Instruction] =
    Parser.parse(text).left.map(_.diagnostic.raw).flatMap(_.statements match {
      case Vec(Program.Statement.Instruction(value)) => Right(value)
      case _                                         => Left(s"not a single instruction: $text")
    })

  private def each[A, B: ClassTag](values: Vec[A])(f: A => Either[String, B]): Either[String, Vec[B]] =
    values.foldLeft[Either[String, Vec[B]]](Right(Vec.empty[B]))((found, value) =>
      found.flatMap(done => f(value).map(done :+ _))
    )

  private final class Search[I: ClassTag, H, L <: Lookup](
      val isa: Isa[I, H, L],
      window: Window,
      original: Vec[I],
      originalUses: Vec[RegisterUse],
      moves: Walker[I],
      config: Config
  ) {

    private def units(use: RegisterUse, names: Vec[String]): Vec[String] =
      names.flatMap(name => isa.units(name, use.unitsOf(name)))

    private def liveOut(steps: Vec[Equivalence.Step]): Equivalence.LiveOut = {
      val written = originalUses.flatMap(use => units(use, use.writes)).toSet
      val live    = window.liveOut.intersect(written)
      val named   = steps.flatMap(step => step.kase.definition.results.flatMap(result => step.results(result.name)))
      Equivalence.LiveOut(
        named.distinct.filter(live.contains),
        memory = steps.exists(_.kase.definition.contexts.exists(_.tpe != LaneMasks.contextType))
      )
    }

    private def sample(name: String, tpe: Type, run: Int): Long =
      if (isa.active(name)) tpe.represent(-1L)
      else {
        val mixed = config.seed ^ (name.hashCode.toLong * 0x9e3779b97f4a7c15L) ^ run.toLong
        Edges.sample(tpe, Random(mixed))
      }

    private def execute(
        sequence: Vec[Equivalence.Step],
        initial: Map[String, Long],
        types: Map[String, Type],
        harness: H
    ): Either[String, Written] =
      sequence.foldLeft[Either[String, Written]](Right(Written(initial, Set.empty))) { (found, step) =>
        found.flatMap(known =>
          isa.executor(step.kase, step.inputs(known.values), harness).map { outputs =>
            val named = step.outputs(outputs).map((location, value) => location -> types(location).represent(value))
            Written(known.values ++ named, known.written ++ named.map(_._1))
          }
        )
      }

    private def memory(expected: H, obtained: H, liveOut: Equivalence.LiveOut): Long =
      if (!liveOut.memory) 0L else isa.memory(expected, obtained)

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

              val expectedHarness = isa.reference.harness(run.toLong)
              val obtainedHarness = isa.reference.harness(run.toLong)
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

    private[check] def evidence(candidate: Vec[I]): Either[String, Proof.Result] = for {
      originalSteps <- isa.steps(original)
      right         <- isa.steps(candidate)
    } yield Proof.prove(
      isa.reference,
      isa.executor,
      (originalSteps ++ right).flatMap(_.lane.cases),
      originalSteps,
      right,
      liveOut(originalSteps)
    )

    private case class Walk(
        moves: Walker[I],
        cycles: Double,
        distance: Long,
        evaluated: Int
    )

    def run(priced: Costing[I]): Either[String, Report] = walked(floored(isa.text, original)(priced))

    private def walked(cost: Costing[I]): Either[String, Report] = for {
      originalSteps <- isa.steps(original)
      whole         <- cost(window, original)
    } yield {
      val out     = liveOut(originalSteps)
      val idle    = out.locations.isEmpty && !out.memory
      val reason  = if (idle) "no write is live out" else "live-out writes proved equal"
      val before  = Measure(whole.cycles, whole.source, whole.noise)
      val random  = Random(config.seed)
      val costs   = mutable.LinkedHashMap.empty[String, Either[String, AMDGPUBlockCost.Cost]]
      val seen    = mutable.LinkedHashMap.empty[String, (Vec[I], Long, AMDGPUBlockCost.Cost)]
      val deleted = Moves.Candidate(Moves.Kind.Delete, Vec.empty[I], Vec.empty[RegisterUse])

      def visit(walk: Walk, candidate: Moves.Candidate[I]): Walk = {
        val key   = isa.text(candidate.instructions).mkString("; ")
        val found = for {
          bits <- isa.steps(candidate.instructions).flatMap(right =>
            distance(originalSteps, right, out) match {
              case Distance.Bits(bits)   => Right(bits)
              case Distance.Refused(why) => Left(why)
            }
          ).toOption
          after <- costs.getOrElseUpdate(key, cost(window, candidate.instructions)).toOption
        } yield (bits, after)
        found.fold(walk.copy(evaluated = walk.evaluated + 1)) { (bits, after) =>
          if (!seen.contains(key)) seen.update(key, (candidate.instructions, bits, after))
          val value    = bits.toDouble + after.cycles
          val current  = walk.distance.toDouble + walk.cycles
          val accepted = value <= current || random.nextDouble() < math.exp((current - value) / config.temperature)
          if (accepted)
            Walk(
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
            else if (pool.isEmpty) Vec.empty[Moves.Candidate[I]]
            else Vec(pool(random.nextInt(pool.size)))
          walk(iteration + 1, considered.foldLeft(current)(visit))
        }

      val start   = Walk(moves, before.cycles, 0L, 0)
      val walked  = if (idle) visit(start, deleted) else walk(0, start)
      val cheaper = seen.values.filter((_, bits, after) =>
        bits == 0L && gain(before, Measure(after.cycles, after.source, after.noise))
      ).toVec
      val lane     = originalSteps.flatMap(_.lane.cases)
      val rewrites = cheaper.flatMap { (sequence, _, after) =>
        isa.steps(sequence).toOption.flatMap { right =>
          Proof.prove(
            isa.reference,
            isa.executor,
            lane ++ right.flatMap(_.lane.cases),
            originalSteps,
            right,
            out
          ) match {
            case Proof.Result.Proved(evidence) =>
              val source = Architecture.Source.weakest(before.source, after.source)
              Some(
                Rewrite(
                  window,
                  isa.text(original),
                  isa.text(sequence),
                  before,
                  Measure(after.cycles, after.source, after.noise),
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
        rewrites,
        costs.collect { case (candidate, Left(reason)) => (candidate = candidate, reason = reason) }.toVec
      )
    }
  }
}
