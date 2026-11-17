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
import net.kurobako.gemstone.profile.Pipe
import net.kurobako.gemstone.profile.PipelineSimulator
import net.kurobako.gemstone.profile.ProfileReport
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Edges
import net.kurobako.gemstone.vm.RegisterUse
import scala.annotation.tailrec
import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.Random
import scala.util.Try

/** Cycles are one wave's issue and latency floor: the model has no occupancy and no memory traffic. */
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
      evidence: Proof.Report
  )

  case class Report(
      window: Window,
      seed: Long,
      budget: Int,
      evaluated: Int,
      candidates: Int,
      wrong: Int,
      rewrites: Vec[Rewrite]
  )

  private enum Distance derives CanEqual {
    case Bits(bits: Long)
    case Refused(reason: String)
  }

  private case class Written(values: Map[String, Long], written: Set[String])

  private val Placeholder = 0x1234

  private val Register = raw"([sva])(\d+)".r
  private val Span     = raw"([sva])\[(\d+):(\d+)\]".r

  def amdgpu(image: Vec[Byte], function: String, targetName: String, window: Window, config: Config = Config.default)
      : Either[String, Report] =
    search(image, function, targetName, window, config).flatMap(_.run())

  def prove(image: Vec[Byte], function: String, targetName: String, window: Window, candidate: Vec[String])
      : Either[String, Proof.Result] =
    search(image, function, targetName, window, Config.default).flatMap(_.evidence(candidate))

  private def search(
      image: Vec[Byte],
      function: String,
      targetName: String,
      window: Window,
      config: Config
  ): Either[String, Search] = for {
    target       <- targetName.refine[Target].left.map(_.raw)
    architecture <- Architecture.named(targetName).toRight(s"no pipeline model for $targetName")
    found        <- AMDGPUModel.staticCode(image, function)
    moves        <- AMDGPUMoves.of(image, function, targetName, window)
    parsed       <- each(found._3.zip(found._2))((text, use) => instruction(text).map(_ -> use))
    block = found._1.blocks(window.block)
    ready <- Search.of(target, architecture, window, block, parsed, moves, config)
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

  private def pipe(writes: String): Option[Pipe] =
    if (writes.contains("WriteDouble")) Some(Pipe.Fp64)
    else if (writes.contains("WriteQuarterRate32")) Some(Pipe.Mufu)
    else if (writes.contains("Write32Bit") || writes.contains("WriteTrans32") || writes.contains("WriteFloatFMA"))
      Some(Pipe.Fma)
    else if (writes.contains("WriteIntMul")) Some(Pipe.Alu)
    else None

  private def location(name: String): Boolean = name match {
    case Register(_, _) | Span(_, _, _) => true
    case state                          => ImplicitState.names.contains(state)
  }

  private def literals(instruction: Instruction): Map[String, Long] =
    instruction.operands.flatMap {
      case Operand.IntegerLiteral(value, _)                                          => named(value)
      case Operand.Expression(text) if text.startsWith("lit(") && text.endsWith(")") =>
        val inner = text.slice("lit(".length, text.length - 1)
        numeric(inner).toVec.flatMap(named)
      case _ => Vec.empty[(String, Long)]
    }.toMap

  private def numeric(text: String): Option[BigInt] =
    Try(BigInt(text.stripPrefix("0x"), if (text.startsWith("0x")) 16 else 10)).toOption

  private def named(value: BigInt): Vec[(String, Long)] =
    Vec(s"0x${value.toString(16)}" -> value.toLong, value.toString -> value.toLong).distinct

  private final class Search(
      target: Target,
      architecture: Architecture,
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
    private val backEdge          = block.successors.contains(window.block)

    private val definitions = mutable.Map.empty[String, Either[String, Equivalence.Step]]

    private val masks: Set[String] = Set(ImplicitState.Exec) ++ ImplicitState.of(OperandKind.`VCC`)

    private def units(name: String, width: Int): Vec[String] =
      if (masks(name)) ImplicitState.units(name, waveSize) else Liveness.units(name, width)

    private def units(use: RegisterUse, names: Vec[String]): Vec[String] =
      names.flatMap(name => units(name, use.unitsOf(name)))

    private def step(instruction: Instruction): Either[String, Equivalence.Step] = {
      val inline = instruction.operands.zipWithIndex.collectFirst {
        case (Operand.Expression(text), index) if text.startsWith("lit(") && text.endsWith(")") =>
          numeric(text.slice("lit(".length, text.length - 1)).map(value => index -> value)
      }.flatten
      val spelled = inline.fold[Either[String, Instruction]](Right(instruction))((index, _) =>
        Instruction.decode(
          instruction.definition,
          instruction.operands.updated(index, Operand.IntegerLiteral(BigInt(Placeholder), 16)),
          instruction.mods
        ).left.map(reason => s"${Printer.render(instruction)}: $reason")
      )
      val literals = RewriteSearch.literals(instruction) ++ inline.map((_, value) =>
        s"0x${Placeholder.toHexString}" -> value.toLong
      )
      for {
        spelling <- spelled
        text = Printer.render(spelling)
        definition <-
          InstructionSemantics.describe(target, text, Map.empty, Vec.empty).left.map(reason => s"$text: $reason")
        parameters <- each(definition.parameters.map { parameter =>
          val name = parameter.name
          if (location(name)) Right(name -> Equivalence.Operand.Location(name))
          else literals.get(name).toRight(s"$text binds $name, neither a location nor a literal").map(value =>
            name -> Equivalence.Operand.Immediate(value)
          )
        }.toVec)(identity)
      } yield Equivalence.Step(
        Case(text, Some(target.raw), definition, Lookup.Spellings(Map.empty)),
        parameters.toMap,
        definition.results.map((name, _, _) => name -> name).toMap
      )
    }

    private def steps(sequence: Vec[Instruction]): Either[String, Vec[Equivalence.Step]] =
      each(sequence)(instruction => definitions.getOrElseUpdate(Printer.render(instruction), step(instruction)))

    private def liveOut(steps: Vec[Equivalence.Step]): Equivalence.LiveOut = {
      val written = originalUses.flatMap(use => units(use, use.writes)).toSet
      val live    = window.liveOut.intersect(written)
      val named   = steps.flatMap(_.kase.definition.results.collect {
        case (name, tpe, _) if units(name, (tpe.bits + 31) / 32).exists(live.contains) => name
      })
      Equivalence.LiveOut(named.distinct, memory = steps.exists(_.kase.definition.contexts.nonEmpty))
    }

    private def sample(name: String, tpe: Type, run: Int): Long =
      if (name == ImplicitState.Exec) -1L
      else {
        val mixed = config.seed ^ (name.hashCode.toLong * 0x9e3779b97f4a7c15L) ^ run.toLong
        Edges.sample(tpe, Random(mixed))
      }

    private def execute(
        sequence: Vec[Equivalence.Step],
        initial: Map[String, Long],
        harness: AMDGPUHarness
    ): Either[String, Written] =
      sequence.foldLeft[Either[String, Written]](Right(Written(initial, Set.empty))) { (found, step) =>
        found.flatMap { known =>
          val values = step.kase.definition.parameters.map((name, _) =>
            step.parameters(name) match {
              case Equivalence.Operand.Location(location) => known.values(location)
              case Equivalence.Operand.Immediate(value)   => value
            }
          )
          executor(step.kase, values, harness).map { outputs =>
            val named = step.kase.definition.results.map((name, _, _) => name).zip(outputs)
              .map((name, value) => step.results(name) -> value)
            Written(known.values ++ named, known.written ++ named.map(_._1))
          }
        }
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
      Equivalence.validate(left.iterator ++ right.iterator, liveOut) match {
        case Left(reason) => Distance.Refused(reason)
        case Right(types) =>
          val names = types.keys.toVec.sorted
          val runs  = (0 until config.states).foldLeft[Either[String, Long]](Right(0L)) { (total, run) =>
            total.flatMap { sum =>
              val sampled = names.map(name => name -> sample(name, types(name), run)).toMap

              val expectedHarness = AMDGPUReference.harness(run.toLong)
              val obtainedHarness = AMDGPUReference.harness(run.toLong)
              (execute(left, sampled, expectedHarness), execute(right, sampled, obtainedHarness)) match {
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

    private def op(instruction: Instruction, use: RegisterUse, backEdge: Boolean): PipelineSimulator.Op = {
      val mnemonic = instruction.opcode.raw
      val lds      = instruction.mods.exists(_.name == "lds")
      val counting = Option.when(mnemonic == "s_nop")(instruction.operands.headOption.collect {
        case Operand.IntegerLiteral(value, _) => value.toInt
      }.getOrElse(0))
      val timing = Scheduling.timing(target, mnemonic, lds)
      val source =
        if (timing.isDefined) Architecture.Source.Estimate(ProfileReport.modelledPlanning._2)
        else Architecture.Source.Estimate("no LLVM scheduling-model record; one issue slot")
      PipelineSimulator.Op(
        pipe = timing.flatMap(value => pipe(value.writes)),
        lanes = waveSize,
        reads = units(use, use.reads),
        writes = units(use, use.writes),
        latency = Architecture.Sourced(counting.fold(timing.fold(1)(_.latency))(_ => 0), source),
        issue = counting.fold(timing.fold(1)(_.cycles))(_ + 1),
        stall = None,
        traffic = PipelineSimulator.Traffic.none,
        backEdge = backEdge,
        rate = None,
        span = None
      )
    }

    private def measure(sequence: Vec[Instruction], used: Vec[RegisterUse]): Measure = {
      val instructions = blockInstructions.patch(at, sequence, length)
      val uses         = blockUses.patch(at, used, length)
      val ops          = instructions.zip(uses).zipWithIndex.map { case ((instruction, use), index) =>
        op(instruction, use, backEdge && index == instructions.size - 1)
      }
      val block = PipelineSimulator.simulate(architecture, None, PipelineSimulator.Stream.repeat(ops))
      Measure(block.cyclesPerIteration, block.source)
    }

    private def text(sequence: Vec[Instruction]): Vec[String] = sequence.map(Printer.render(_))

    private[check] def evidence(candidate: Vec[String]): Either[String, Proof.Result] = for {
      originalSteps <- steps(original)
      parsed        <- each(candidate)(instruction)
      right         <- steps(parsed)
    } yield Proof.prove(
      AMDGPUReference,
      executor,
      originalSteps.map(_.kase) ++ right.map(_.kase),
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

    def run(): Either[String, Report] = steps(original).flatMap { originalSteps =>
      val out = liveOut(originalSteps)
      if (out.locations.isEmpty)
        Left("the window leaves none of its writes live out, so every candidate is trivially equivalent")
      else {
        val before = measure(original, originalUses)
        val random = Random(config.seed)
        val seen   = mutable.LinkedHashMap.empty[String, (Vec[Instruction], Long, Measure)]

        def visit(walk: Walk, candidate: AMDGPUMoves.Candidate): Walk = {
          val bits = steps(candidate.instructions).flatMap(right =>
            distance(originalSteps, right, out) match {
              case Distance.Bits(bits)   => Right(bits)
              case Distance.Refused(wby) => Left(wby)
            }
          ).toOption
          bits.fold(walk.copy(evaluated = walk.evaluated + 1)) { bits =>
            val after = measure(candidate.instructions, candidate.uses)
            seen.getOrElseUpdate(text(candidate.instructions).mkString("; "), (candidate.instructions, bits, after))
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
              if (iteration == 0) pool
              else if (pool.isEmpty) Vec.empty[AMDGPUMoves.Candidate]
              else Vec(pool(random.nextInt(pool.size)))
            walk(iteration + 1, considered.foldLeft(current)(visit))
          }

        val walked   = walk(0, Walk(original, moves, before.cycles, 0L, 0))
        val cheaper  = seen.values.filter((_, bits, after) => bits == 0L && after.cycles < before.cycles).toVec
        val lane     = originalSteps.map(_.kase)
        val rewrites = cheaper.flatMap { (sequence, _, after) =>
          steps(sequence).toOption.flatMap { right =>
            Proof.prove(AMDGPUReference, executor, lane ++ right.map(_.kase), originalSteps, right, out) match {
              case Proof.Result.Proved(evidence) =>
                val source = Architecture.Source.weakest(before.source, after.source)
                Some(Rewrite(window, text(original), text(sequence), before, after, source, evidence))
              case _ => None
            }
          }
        }
        Right(
          Report(
            window,
            config.seed,
            config.budget,
            walked.evaluated,
            seen.size,
            seen.values.count(_._2 > 0L),
            rewrites
          )
        )
      }
    }
  }

  private object Search {
    def of(
        target: Target,
        architecture: Architecture,
        window: Window,
        block: Block,
        parsed: Vec[(Instruction, RegisterUse)],
        moves: AMDGPUMoves,
        config: Config
    ): Either[String, Search] = Either.cond(
      block.first <= window.first && window.last <= block.last,
      new Search(target, architecture, window, block, parsed, moves, config),
      s"the window ${window.first}..${window.last} is not inside the block ${block.first}..${block.last}"
    )
  }
}
