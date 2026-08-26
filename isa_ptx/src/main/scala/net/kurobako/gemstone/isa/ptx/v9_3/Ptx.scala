package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.CheckedMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.scheduler.LaunchShape
import net.kurobako.gemstone.trace.Trace
import net.kurobako.gemstone.trace.TraceEvent
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

enum PtxValue {
  case Register(name: String)
  case Immediate(value: Long)
  case ThreadIdX
  case BlockIdX
  case BlockDimX
}

enum PtxIntegerOperation {
  case Add
  case Subtract
  case MultiplyWideUnsigned32
  case ShiftLeft
}

enum PtxComparison {
  case Equal
  case NotEqual
  case LessThan
  case LessThanOrEqual
  case GreaterThan
  case GreaterThanOrEqual
}

sealed trait PtxInstruction extends Product with Serializable {
  def opcode: String
}

object PtxInstruction {
  final case class LoadParameter(destination: String, parameter: String, width: Int) extends PtxInstruction {
    override val opcode: String = s"ld.param.u$width"
  }

  final case class Move(destination: String, source: PtxValue, width: Int) extends PtxInstruction {
    override val opcode: String = s"mov.u$width"
  }

  final case class IntegerBinary(
      operation: PtxIntegerOperation,
      destination: String,
      left: PtxValue,
      right: PtxValue,
      width: Int
  ) extends PtxInstruction {
    override val opcode: String = operation match {
      case PtxIntegerOperation.Add                    => s"add.u$width"
      case PtxIntegerOperation.Subtract               => s"sub.u$width"
      case PtxIntegerOperation.MultiplyWideUnsigned32 => "mul.wide.u32"
      case PtxIntegerOperation.ShiftLeft              => s"shl.b$width"
    }
  }

  final case class MultiplyAddLow32(destination: String, left: PtxValue, right: PtxValue, addend: PtxValue)
      extends PtxInstruction {
    override val opcode: String = "mad.lo.s32"
  }

  final case class LoadGlobal32(destination: String, address: PtxValue) extends PtxInstruction {
    override val opcode: String = "ld.global.u32"
  }

  final case class StoreGlobal32(address: PtxValue, source: PtxValue) extends PtxInstruction {
    override val opcode: String = "st.global.u32"
  }

  final case class SetPredicate(
      destination: String,
      comparison: PtxComparison,
      signed: Boolean,
      left: PtxValue,
      right: PtxValue,
      width: Int
  ) extends PtxInstruction {
    override val opcode: String = "setp"
  }

  final case class Branch(target: Pc) extends PtxInstruction {
    override val opcode: String = "bra"
  }

  final case class BarrierSync(id: Int) extends PtxInstruction {
    override val opcode: String = "bar.sync"
  }

  case object Return extends PtxInstruction {
    override val opcode: String = "ret"
  }
}

final case class PtxPredicate(name: String, negated: Boolean)

final case class PtxExecutable(instruction: PtxInstruction, predication: Option[PtxPredicate]) {
  def opcode: String = instruction.opcode
}

final case class PtxParameter(name: String, widthBytes: Int)
final case class PtxArgument(value: Long)

final case class PtxKernel(name: String, parameters: Vector[PtxParameter], program: ExecutableProgram[PtxExecutable]) {
  def instructions: Vector[PtxInstruction] = program.instructions.map(_.instruction)
}

enum PtxLoweringError {
  case Invalid(detail: String)
  case Unsupported(instruction: String, detail: String)

  def message: String = this match {
    case Invalid(detail)                  => detail
    case Unsupported(instruction, detail) => s"unsupported instruction: $instruction ($detail)"
  }
}

final case class PtxParseError(line: Int, message: String)

object PtxLowerer {
  def lower(entry: Statement.Entry): Either[PtxLoweringError, PtxKernel] = {
    val Statement.Entry(_, name, parameters, body) = entry
    val flat                                       = flatten(body)
    val labels                                     = mutable.Map.empty[String, Pc]
    var instructionIndex                           = 0
    var statementIndex                             = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Label(label) =>
          if (labels.contains(label)) return Left(PtxLoweringError.Invalid(s"duplicate label: $label"))
          labels(label) = Pc.unsafe(instructionIndex)
        case Statement.Instruction(_) => instructionIndex += 1
        case _                        => ()
      }
      statementIndex += 1
    }

    val lowered = Vector.newBuilder[PtxExecutable]
    statementIndex = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Instruction(instruction) =>
          lowerInstruction(instruction, labels.toMap) match {
            case Left(error)  => return Left(error)
            case Right(value) => lowered += value
          }
        case _ => ()
      }
      statementIndex += 1
    }
    lowerParameters(parameters).map(loweredParameters =>
      PtxKernel(name, loweredParameters, ExecutableProgram(lowered.result()))
    )
  }

  private def lowerParameters(parameters: Vector[Parameter]): Either[PtxLoweringError, Vector[PtxParameter]] = {
    val result = Vector.newBuilder[PtxParameter]
    var index  = 0
    while (index < parameters.length) {
      val parameter = parameters(index)
      val widths    = parameter.qualifiers.collect {
        case "u8" | "s8" | "b8"            => 1
        case "u16" | "s16" | "b16"         => 2
        case "u32" | "s32" | "b32" | "f32" => 4
        case "u64" | "s64" | "b64" | "f64" => 8
      }
      if (parameter.dimensions.nonEmpty)
        return Left(PtxLoweringError.Unsupported(parameter.name, "array-valued kernel parameters"))
      widths.lastOption match {
        case Some(width) => result += PtxParameter(parameter.name, width)
        case None => return Left(PtxLoweringError.Invalid(s"kernel parameter ${parameter.name} has no scalar width"))
      }
      index += 1
    }
    Right(result.result())
  }

  private def flatten(statements: Vector[Statement]): Vector[Statement] = statements.flatMap {
    case Statement.Block(body) => flatten(body)
    case value                 => Vector(value)
  }

  private def lowerInstruction(
      instruction: Instruction,
      labels: Map[String, Pc]
  ): Either[PtxLoweringError, PtxExecutable] =
    KnownInstruction.fromSyntax(instruction) match {
      case Left(_)      => Left(PtxLoweringError.Unsupported(Printer.render(instruction), "unknown PTX opcode"))
      case Right(known) => lowerKnownInstruction(known, labels)
    }

  private def lowerKnownInstruction(
      instruction: KnownInstruction,
      labels: Map[String, Pc]
  ): Either[PtxLoweringError, PtxExecutable] = {
    val syntax   = instruction.syntax
    val operands = syntax.operands
    val mnemonic = syntax.mnemonic
    val lowered  = instruction match {
      case KnownInstruction.Ld(_) if (mnemonic == "ld.param.u32" || mnemonic == "ld.param.u64") && operands.size == 2 =>
        for {
          destination <- registerName(operands(0))
          parameter   <- addressSymbol(operands(1))
        } yield PtxInstruction.LoadParameter(
          destination,
          parameter,
          if (mnemonic.endsWith("32")) 32 else 64
        )
      case KnownInstruction.Mov(_) if (mnemonic == "mov.u32" || mnemonic == "mov.b32") && operands.size == 2 =>
        for {
          destination <- registerName(operands(0))
          source      <- value(operands(1))
        } yield PtxInstruction.Move(destination, source, 32)
      case KnownInstruction.Mov(_) if (mnemonic == "mov.u64" || mnemonic == "mov.b64") && operands.size == 2 =>
        for {
          destination <- registerName(operands(0))
          source      <- value(operands(1))
        } yield PtxInstruction.Move(destination, source, 64)
      case KnownInstruction.Mul(_) if mnemonic == "mul.wide.u32" && operands.size == 3 =>
        for {
          destination <- registerName(operands(0))
          left        <- value(operands(1))
          right       <- value(operands(2))
        } yield PtxInstruction.IntegerBinary(
          PtxIntegerOperation.MultiplyWideUnsigned32,
          destination,
          left,
          right,
          64
        )
      case KnownInstruction.Shl(_) if mnemonic == "shl.b32" && operands.size == 3 =>
        integerBinary(PtxIntegerOperation.ShiftLeft, 32, operands)
      case KnownInstruction.Mad(_) if mnemonic == "mad.lo.s32" && operands.size == 4 =>
        for {
          destination <- registerName(operands(0))
          left        <- value(operands(1))
          right       <- value(operands(2))
          addend      <- value(operands(3))
        } yield PtxInstruction.MultiplyAddLow32(destination, left, right, addend)
      case KnownInstruction.Add(_) if (mnemonic == "add.u32" || mnemonic == "add.s32") && operands.size == 3 =>
        integerBinary(PtxIntegerOperation.Add, 32, operands)
      case KnownInstruction.Add(_) if (mnemonic == "add.u64" || mnemonic == "add.s64") && operands.size == 3 =>
        integerBinary(PtxIntegerOperation.Add, 64, operands)
      case KnownInstruction.Sub(_) if (mnemonic == "sub.u32" || mnemonic == "sub.s32") && operands.size == 3 =>
        integerBinary(PtxIntegerOperation.Subtract, 32, operands)
      case KnownInstruction.Sub(_) if (mnemonic == "sub.u64" || mnemonic == "sub.s64") && operands.size == 3 =>
        integerBinary(PtxIntegerOperation.Subtract, 64, operands)
      case KnownInstruction.Ld(_) if mnemonic == "ld.global.u32" && operands.size == 2 =>
        for {
          destination <- registerName(operands(0))
          address     <- addressValue(operands(1))
        } yield PtxInstruction.LoadGlobal32(destination, address)
      case KnownInstruction.St(_) if mnemonic == "st.global.u32" && operands.size == 2 =>
        for {
          address <- addressValue(operands(0))
          source  <- value(operands(1))
        } yield PtxInstruction.StoreGlobal32(address, source)
      case KnownInstruction.Setp(_) if mnemonic.startsWith("setp.") && operands.size == 3 =>
        lowerSetPredicate(syntax, operands)
      case KnownInstruction.Bra(_) if (mnemonic == "bra" || mnemonic == "bra.uni") && operands.size == 1 =>
        operands.head match {
          case Operand.Symbol(label) => labels.get(label).toRight(PtxLoweringError.Invalid(s"unknown label: $label"))
              .map(PtxInstruction.Branch.apply)
          case other => invalid(s"expected branch label, found ${Printer.render(other)}")
        }
      case KnownInstruction.Bar(_) if mnemonic == "bar.sync" && syntax.predication.nonEmpty =>
        invalid("bar.sync must not be predicated in the supported execution profile")
      case KnownInstruction.Bar(_) if mnemonic == "bar.sync" && operands.size == 1 =>
        immediate(operands.head).flatMap { value =>
          if (value >= 0L && value <= Int.MaxValue.toLong) Right(PtxInstruction.BarrierSync(value.toInt))
          else invalid(s"barrier id is outside Int range: $value")
        }
      case KnownInstruction.Ret(_) if operands.isEmpty => Right(PtxInstruction.Return)
      case _ => Left(PtxLoweringError.Unsupported(Printer.render(syntax), "no handwritten semantic family"))
    }
    for {
      operation <- lowered
      predicate <- lowerPredicate(syntax.predication)
    } yield PtxExecutable(operation, predicate)
  }

  private def integerBinary(
      operation: PtxIntegerOperation,
      width: Int,
      operands: Vector[Operand]
  ): Either[PtxLoweringError, PtxInstruction] =
    for {
      destination <- registerName(operands(0))
      left        <- value(operands(1))
      right       <- value(operands(2))
    } yield PtxInstruction.IntegerBinary(operation, destination, left, right, width)

  private def lowerSetPredicate(
      instruction: Instruction,
      operands: Vector[Operand]
  ): Either[PtxLoweringError, PtxInstruction] = {
    val modifiers  = instruction.modifiers.map(_.name)
    val comparison = modifiers.collectFirst(Function.unlift {
      case "eq" => Some(PtxComparison.Equal)
      case "ne" => Some(PtxComparison.NotEqual)
      case "lt" => Some(PtxComparison.LessThan)
      case "le" => Some(PtxComparison.LessThanOrEqual)
      case "gt" => Some(PtxComparison.GreaterThan)
      case "ge" => Some(PtxComparison.GreaterThanOrEqual)
      case _    => None
    })
    val kind = modifiers.collectFirst {
      case "u32" => false -> 32
      case "s32" => true  -> 32
      case "u64" => false -> 64
      case "s64" => true  -> 64
    }
    for {
      predicate <- comparison.toRight(PtxLoweringError.Invalid(s"unsupported setp comparison: ${instruction.mnemonic}"))
      signedAndWidth <- kind.toRight(PtxLoweringError.Invalid(s"unsupported setp type: ${instruction.mnemonic}"))
      destination    <- registerName(operands(0))
      left           <- value(operands(1))
      right          <- value(operands(2))
    } yield PtxInstruction.SetPredicate(destination, predicate, signedAndWidth._1, left, right, signedAndWidth._2)
  }

  private def lowerPredicate(value: Option[Predication]): Either[PtxLoweringError, Option[PtxPredicate]] = value match {
    case None                                => Right(None)
    case Some(Predication(negated, operand)) => registerName(operand).map(name => Some(PtxPredicate(name, negated)))
  }

  private def registerName(operand: Operand): Either[PtxLoweringError, String] = operand match {
    case Operand.Register(name, None) => Right(name)
    case other                        => invalid(s"expected register, found ${Printer.render(other)}")
  }

  private def addressValue(operand: Operand): Either[PtxLoweringError, PtxValue] = operand match {
    case Operand.Address(Vector(single)) => value(single)
    case other                           => invalid(s"expected address operand, found ${Printer.render(other)}")
  }

  private def addressSymbol(operand: Operand): Either[PtxLoweringError, String] = operand match {
    case Operand.Address(Vector(Operand.Symbol(name))) => Right(name)
    case other => invalid(s"expected parameter address, found ${Printer.render(other)}")
  }

  private def value(operand: Operand): Either[PtxLoweringError, PtxValue] = operand match {
    case Operand.Register("%tid.x", None)   => Right(PtxValue.ThreadIdX)
    case Operand.Register("%ctaid.x", None) => Right(PtxValue.BlockIdX)
    case Operand.Register("%ntid.x", None)  => Right(PtxValue.BlockDimX)
    case Operand.Register(name, None)       => Right(PtxValue.Register(name))
    case other: Operand.Immediate           => immediate(other).map(PtxValue.Immediate.apply)
    case other => invalid(s"expected register or immediate, found ${Printer.render(other)}")
  }

  private def immediate(operand: Operand): Either[PtxLoweringError, Long] = operand match {
    case Operand.Immediate(raw) =>
      val signless = raw.dropWhile(character => character == '+' || character == '-')
      val sign     = if (raw.startsWith("-")) -1 else 1
      val parsed   =
        try
          Some(
            if (signless.startsWith("0x") || signless.startsWith("0X")) BigInt(signless.drop(2), 16) * sign
            else if (signless.startsWith("0b") || signless.startsWith("0B")) BigInt(signless.drop(2), 2) * sign
            else BigInt(raw)
          )
        catch {
          case _: NumberFormatException => None
        }
      parsed match {
        case Some(number) if number.isValidLong => Right(number.longValue)
        case Some(_)                            => invalid(s"integer immediate is outside 64-bit range: $raw")
        case None                               => invalid(s"expected integer immediate, found $raw")
      }
    case other => invalid(s"expected integer immediate, found ${Printer.render(other)}")
  }

  private def invalid[A](detail: String): Either[PtxLoweringError, A] = Left(PtxLoweringError.Invalid(detail))
}

object PtxParser {
  def parse(source: String): Either[PtxParseError, PtxKernel] =
    Parser.parse(source).left.map(error => PtxParseError(error.line, error.toString)).flatMap { program =>
      program.statements.collectFirst { case entry: Statement.Entry => entry } match {
        case None        => Left(PtxParseError(1, "missing .entry declaration"))
        case Some(entry) => PtxLowerer.lower(entry).left.map(error => PtxParseError(1, error.message))
      }
    }
}

private enum PtxWrite extends Write {
  case Register(lane: Int, name: String, value: Long)
  case Predicate(lane: Int, name: String, value: Boolean)
  case Memory32(lane: Int, address: GuestAddress, value: Int)
}

private final class PtxBlockState(val laneCount: Int) {
  val registers: Array[mutable.Map[String, Long]]     = Array.fill(laneCount)(mutable.Map.empty[String, Long])
  val predicates: Array[mutable.Map[String, Boolean]] = Array.fill(laneCount)(mutable.Map.empty[String, Boolean])
  var live: LaneMask                                  =
    LaneMask.all(laneCount).fold(fault => throw new IllegalArgumentException(fault.message), identity)
}

private final class PtxExecutionContext(
    val kernel: PtxKernel,
    val block: Int,
    val state: PtxBlockState,
    val pc: Pc,
    val active: LaneMask,
    val arguments: Map[String, PtxArgument],
    val memory: CheckedMemory
) {
  def read(lane: Int, value: PtxValue): Either[ExecutionFault, Long] = value match {
    case PtxValue.Register(name) => state.registers(lane).get(name)
        .toRight(ExecutionFault.UndefinedRegister(name, pc, lane))
    case PtxValue.Immediate(immediate) => Right(immediate)
    case PtxValue.ThreadIdX            => Right(lane.toLong)
    case PtxValue.BlockIdX             => Right(block.toLong)
    case PtxValue.BlockDimX            => Right(state.laneCount.toLong)
  }
}

private object PtxSemantics {
  extension (instruction: PtxInstruction) {
    def execute(context: PtxExecutionContext, effects: Effects[PtxWrite]): Either[ExecutionFault, Step] =
      instruction match {
        case PtxInstruction.LoadParameter(destination, parameter, width) =>
          context.arguments.get(parameter).toRight(
            ExecutionFault.InvalidState(s"missing kernel argument $parameter")
          ).map { address =>
            context.active.foreachLane(lane =>
              effects.stage(PtxWrite.Register(lane, destination, truncate(address.value, width)))
            )
            Step.Advance
          }
        case PtxInstruction.Move(destination, source, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map(value =>
              effects.stage(PtxWrite.Register(lane, destination, truncate(value, width)))
            )
          }.map(_ => Step.Advance)
        case PtxInstruction.IntegerBinary(operation, destination, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield {
              val result = operation match {
                case PtxIntegerOperation.Add                    => leftValue + rightValue
                case PtxIntegerOperation.Subtract               => leftValue - rightValue
                case PtxIntegerOperation.MultiplyWideUnsigned32 =>
                  (leftValue & 0xffffffffL) * (rightValue & 0xffffffffL)
                case PtxIntegerOperation.ShiftLeft => leftValue << (rightValue & 0x1fL).toInt
              }
              effects.stage(PtxWrite.Register(lane, destination, truncate(result, width)))
            }
          }.map(_ => Step.Advance)
        case PtxInstruction.MultiplyAddLow32(destination, left, right, addend) =>
          forEveryLane(context) { lane =>
            for {
              leftValue   <- context.read(lane, left)
              rightValue  <- context.read(lane, right)
              addendValue <- context.read(lane, addend)
            } yield effects.stage(
              PtxWrite.Register(lane, destination, truncate(leftValue * rightValue + addendValue, 32))
            )
          }.map(_ => Step.Advance)
        case PtxInstruction.LoadGlobal32(destination, address) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              context.memory.readInt32(GuestAddress(rawAddress)).left.map(error =>
                ExecutionFault.Memory(error.message)
              ).map {
                loaded => effects.stage(PtxWrite.Register(lane, destination, loaded.toLong & 0xffffffffL))
              }
            }
          }.map(_ => Step.Advance)
        case PtxInstruction.StoreGlobal32(address, source) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress <- context.read(lane, address)
              value      <- context.read(lane, source)
            } yield effects.stage(PtxWrite.Memory32(lane, GuestAddress(rawAddress), value.toInt))
          }.map(_ => Step.Advance)
        case PtxInstruction.SetPredicate(destination, comparison, signed, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield effects.stage(
              PtxWrite.Predicate(lane, destination, compare(leftValue, rightValue, comparison, signed, width))
            )
          }.map(_ => Step.Advance)
        case PtxInstruction.Branch(target)  => Right(Step.Jump(target))
        case PtxInstruction.BarrierSync(id) =>
          Right(Step.Suspend(WaitKey(WaitScope.Workgroup, id.toLong, context.pc.value.toLong)))
        case PtxInstruction.Return => Right(Step.Exit(context.active))
      }
  }

  private def forEveryLane(context: PtxExecutionContext)(
      function: Int => Either[ExecutionFault, Unit]
  ): Either[ExecutionFault, Unit] = {
    var failure: Option[ExecutionFault] = None
    context.active.foreachLane { lane =>
      if (failure.isEmpty) function(lane).left.foreach(fault => failure = Some(fault))
    }
    failure.toLeft(())
  }

  private def truncate(value: Long, width: Int): Long =
    if (width == 64) value else value & ((1L << width) - 1L)

  private def compare(
      left: Long,
      right: Long,
      comparison: PtxComparison,
      signed: Boolean,
      width: Int
  ): Boolean = {
    val leftValue  = truncate(left, width)
    val rightValue = truncate(right, width)
    val order      =
      if (signed) java.lang.Long.compare(signExtend(leftValue, width), signExtend(rightValue, width))
      else if (width == 64) java.lang.Long.compareUnsigned(leftValue, rightValue)
      else java.lang.Long.compare(leftValue, rightValue)
    comparison match {
      case PtxComparison.Equal              => leftValue == rightValue
      case PtxComparison.NotEqual           => leftValue != rightValue
      case PtxComparison.LessThan           => order < 0
      case PtxComparison.LessThanOrEqual    => order <= 0
      case PtxComparison.GreaterThan        => order > 0
      case PtxComparison.GreaterThanOrEqual => order >= 0
    }
  }

  private def signExtend(value: Long, width: Int): Long =
    if (width == 64) value else (value << (64 - width)) >> (64 - width)
}

final case class PtxExecutionError(message: String)
final case class PtxExecutionResult(trace: Trace)

object PtxInterpreter {
  private final case class Cohort(pc: Pc, lanes: LaneMask)
  private final case class Waiting(cohort: Cohort, key: WaitKey)

  def execute(
      kernel: PtxKernel,
      shape: LaunchShape,
      arguments: Map[String, PtxArgument],
      memory: CheckedMemory,
      limits: ExecutionLimits = ExecutionLimits.Default,
      cancellation: CancellationToken = CancellationToken.Never
  ): Either[PtxExecutionError, PtxExecutionResult] = {
    if (shape.threadsPerBlock > 64)
      return Left(PtxExecutionError(
        s"the initial PTX cohort engine supports at most 64 threads per block, found ${shape.threadsPerBlock}"
      ))
    if (limits.maxInstructions <= 0L || limits.quantum <= 0)
      return Left(PtxExecutionError(s"invalid execution limits: $limits"))
    val budget = ExecutionBudget(limits.maxInstructions) match {
      case Left(message) => return Left(PtxExecutionError(message))
      case Right(value)  => value
    }
    var trace = Trace.empty
    var step  = 0L
    var block = 0
    while (block < shape.blocks) {
      executeBlock(
        kernel,
        block,
        shape.threadsPerBlock,
        arguments,
        memory,
        limits,
        budget,
        cancellation,
        trace,
        step
      ) match {
        case Left(fault)                  => return Left(PtxExecutionError(fault.message))
        case Right((nextTrace, nextStep)) =>
          trace = nextTrace
          step = nextStep
      }
      block += 1
    }
    Right(PtxExecutionResult(trace))
  }

  private def executeBlock(
      kernel: PtxKernel,
      block: Int,
      laneCount: Int,
      arguments: Map[String, PtxArgument],
      memory: CheckedMemory,
      limits: ExecutionLimits,
      budget: ExecutionBudget,
      cancellation: CancellationToken,
      initialTrace: Trace,
      initialStep: Long
  ): Either[ExecutionFault, (Trace, Long)] = {
    import PtxSemantics.*

    val state        = new PtxBlockState(laneCount)
    val ready        = ArrayBuffer(Cohort(Pc.Entry, state.live))
    val waiting      = ArrayBuffer.empty[Waiting]
    val barrierMasks = mutable.Map.empty[WaitKey, LaneMask]
    val effects      = new Effects[PtxWrite]
    var trace        = initialTrace
    var traceStep    = initialStep
    var quantum      = 0

    def enqueue(pc: Pc, lanes: LaneMask): Unit =
      if (lanes.nonEmpty) {
        val existing = ready.indexWhere(_.pc == pc)
        if (existing < 0) ready += Cohort(pc, lanes)
        else ready(existing) = Cohort(pc, ready(existing).lanes.union(lanes))
      }

    def advance(pc: Pc, lanes: LaneMask): Either[ExecutionFault, Unit] = pc.next.map(next => enqueue(next, lanes))

    def releaseBarrier(key: WaitKey): Either[ExecutionFault, Unit] = {
      val arrived = barrierMasks.getOrElse(key, LaneMask.Empty)
      if (arrived.intersect(state.live) == state.live) {
        val released = waiting.filter(_.key == key).toVector
        waiting.filterInPlace(_.key != key)
        val _             = barrierMasks.remove(key)
        var releasedIndex = 0
        while (releasedIndex < released.size) {
          val releasedCohort = released(releasedIndex).cohort
          advance(releasedCohort.pc, releasedCohort.lanes) match {
            case Left(fault) => return Left(fault)
            case Right(())   => ()
          }
          releasedIndex += 1
        }
      }
      Right(())
    }

    while (state.live.nonEmpty) {
      if (cancellation.isCancelled) return Left(ExecutionFault.Cancelled)
      if (ready.isEmpty) {
        val waits = waiting.map(value => s"${value.key.scope}:${value.key.id}@${value.key.phase}").distinct.sorted
        return Left(ExecutionFault.Deadlock(waits.mkString(", ")))
      }
      val cohort = ready.remove(0)
      kernel.program.at(cohort.pc) match {
        case Left(fault)       => return Left(fault)
        case Right(executable) =>
          var budgetFailure: Option[ExecutionFault] = None
          cohort.lanes.foreachLane { _ =>
            if (budgetFailure.isEmpty) budget.consume().left.foreach(fault => budgetFailure = Some(fault))
          }
          budgetFailure match {
            case Some(fault) => return Left(fault)
            case None        => ()
          }
          quantum += cohort.lanes.size
          if (quantum >= limits.quantum) quantum = 0

          var active                                   = cohort.lanes
          var predicateFailure: Option[ExecutionFault] = None
          executable.predication.foreach { predicate =>
            var selected = LaneMask.Empty
            cohort.lanes.foreachLane { lane =>
              state.predicates(lane).get(predicate.name) match {
                case Some(predicateValue) if predicateValue != predicate.negated =>
                  selected = selected.union(LaneMask.one(lane).toOption.get)
                case Some(_) => ()
                case None => predicateFailure = Some(ExecutionFault.UndefinedRegister(predicate.name, cohort.pc, lane))
              }
            }
            active = selected
          }
          predicateFailure match {
            case Some(fault) => return Left(fault)
            case None        => ()
          }
          val skipped = cohort.lanes.subtract(active)
          cohort.lanes.foreachLane { lane =>
            trace = trace
              .append(TraceEvent.SchedulerDecision(traceStep, block, lane))
              .append(TraceEvent.Instruction(
                traceStep + 1L,
                kernel.name,
                block,
                lane,
                cohort.pc.value,
                executable.opcode
              ))
            traceStep += 2L
          }

          val context = new PtxExecutionContext(kernel, block, state, cohort.pc, active, arguments, memory)
          effects.clear()
          val outcome =
            if (active.isEmpty) Right(Step.Advance)
            else executable.instruction.execute(context, effects)
          outcome match {
            case Left(fault)             => return Left(fault)
            case Right(Step.Trap(fault)) => return Left(fault)
            case Right(control)          =>
              val writes = effects.snapshot
              effects.commit(validateWrite(memory), applyWrite(state, memory)) match {
                case Left(fault) => return Left(fault)
                case Right(())   =>
                  writes.foreach {
                    case PtxWrite.Memory32(lane, address, value) =>
                      trace = trace.append(TraceEvent.OutputWrite(traceStep, block, lane, address.value, value))
                      traceStep += 1L
                    case _ => ()
                  }
              }
              control match {
                case Step.Advance =>
                  advance(cohort.pc, cohort.lanes) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                case Step.Jump(target) =>
                  enqueue(target, active)
                  advance(cohort.pc, skipped) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                case Step.Split(taken, target, fallthrough) =>
                  enqueue(target, taken)
                  advance(cohort.pc, fallthrough) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                case Step.Suspend(key) =>
                  advance(cohort.pc, skipped) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                  waiting += Waiting(cohort.copy(lanes = active), key)
                  barrierMasks(key) = barrierMasks.getOrElse(key, LaneMask.Empty).union(active)
                  releaseBarrier(key) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                case Step.Exit(mask) =>
                  state.live = state.live.subtract(mask)
                  advance(cohort.pc, skipped) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                  val keys     = barrierMasks.keys.toVector
                  var keyIndex = 0
                  while (keyIndex < keys.size) {
                    releaseBarrier(keys(keyIndex)) match {
                      case Left(fault) => return Left(fault)
                      case Right(())   => ()
                    }
                    keyIndex += 1
                  }
                case Step.Trap(fault) => return Left(fault)
              }
          }
      }
    }
    Right(trace -> traceStep)
  }

  private def validateWrite(memory: CheckedMemory)(write: PtxWrite): Either[ExecutionFault, Unit] = write match {
    case PtxWrite.Memory32(_, address, _) =>
      memory.readInt32(address).left.map(error => ExecutionFault.Memory(error.message)).map(_ => ())
    case _ => Right(())
  }

  private def applyWrite(state: PtxBlockState, memory: CheckedMemory)(write: PtxWrite): Unit = write match {
    case PtxWrite.Register(lane, name, value)  => state.registers(lane)(name) = value
    case PtxWrite.Predicate(lane, name, value) => state.predicates(lane)(name) = value
    case PtxWrite.Memory32(_, address, value)  =>
      memory.writeInt32(address, value) match {
        case Left(error) => throw new IllegalStateException(error.message)
        case Right(())   => ()
      }
  }
}
