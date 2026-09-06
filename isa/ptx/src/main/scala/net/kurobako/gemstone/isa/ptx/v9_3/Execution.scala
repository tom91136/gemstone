package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.binary.Bits.byteCount
import net.kurobako.gemstone.binary.Bits.low32
import net.kurobako.gemstone.binary.Bits.signExtend
import net.kurobako.gemstone.binary.Bits.truncate
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AddressMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.trace.Trace
import net.kurobako.gemstone.trace.TraceEvent

import cats.syntax.all.*

import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; architectural state
import scala.collection.mutable.ArrayBuffer // scalafix:ok GemstoneRuntimeCollections.mutable-collection; scheduler queue

enum Value {
  case Register(name: String)
  case RegisterSlot(index: Int)
  case Predicate(name: String)
  case PredicateSlot(index: Int)
  case Function(name: String)
  case Symbol(name: String)
  case Immediate(value: Long)
  case Address(base: Value, offset: Long)
  case LaneId
  case ActiveMask
  case LaneMaskGreaterEqual
  case LaneMaskGreater
  case LaneMaskLessEqual
  case LaneMaskLess
  case LaneMaskEqual
  case ThreadId(axis: Axis)
  case BlockId(axis: Axis)
  case BlockDim(axis: Axis)
  case GridDim(axis: Axis)
}

enum IntegerOperation {
  case Add
  case Subtract
  case BitwiseOr
  case BitwiseXor
  case BitwiseAnd
  case MultiplyLow
  case MultiplyHigh(signed: Boolean)
  case Divide(signed: Boolean)
  case Remainder(signed: Boolean)
  case MultiplyWideUnsigned16
  case MultiplyWideUnsigned32
  case MultiplyWideSigned32
  case Minimum(signed: Boolean)
  case Maximum(signed: Boolean)
  case ShiftLeft
  case ShiftRightLogical
  case ShiftRightArithmetic
}

enum IntegerUnaryOperation {
  case Negate
  case Absolute
  case BitwiseNot
  case PopulationCount
  case LeadingZeroCount
  case BitReverse
}

enum PredicateOperation {
  case And
  case Or
  case Xor
}

enum VoteOperation {
  case All
  case Any
  case Uniform
}

enum Comparison {
  case Ordered
  case Unordered
  case Equal
  case EqualUnordered
  case NotEqual
  case NotEqualUnordered
  case LessThan
  case LessThanUnordered
  case LessThanOrEqual
  case LessThanOrEqualUnordered
  case GreaterThan
  case GreaterThanUnordered
  case GreaterThanOrEqual
  case GreaterThanOrEqualUnordered
}

enum FloatOperation {
  case Add
  case Subtract
  case Multiply
  case Divide
  case Minimum
  case Maximum
  case CopySign
}

enum FloatUnaryOperation {
  case Negate
  case Absolute
  case Reciprocal
  case SquareRoot
  case ReciprocalSquareRoot
  case Exponent2
  case Log2
  case Sine
  case Cosine
  case Tanh
}

enum IntegerMultiplyAddOperation {
  case Low(operandWidth: Int, resultWidth: Int, carryIn: Boolean, carryOut: Boolean)
  case High(operandWidth: Int, resultWidth: Int, carryIn: Boolean, carryOut: Boolean)
}

enum ExecutableInstruction(val opcode: String) {
  case LoadParameter(
      destinations: Col[String],
      parameter: String,
      offset: Int,
      elementWidth: Int,
      signed: Boolean
  ) extends ExecutableInstruction(s"ld.param.${if (signed) "s" else "u"}$elementWidth")

  case StoreParameter(parameter: String, offset: Int, source: Value, elementWidth: Int)
      extends ExecutableInstruction(s"st.param.u$elementWidth")

  case LoadParameterIndirect(destination: String, address: Value, width: Int, signed: Boolean)
      extends ExecutableInstruction(s"ld.param.${if (signed) "s" else "u"}$width")

  case LoadParameterIndirectVector(destinations: Col[String], address: Value, width: Int, signed: Boolean)
      extends ExecutableInstruction(s"ld.param.v${destinations.size}.${if (signed) "s" else "u"}$width")

  case Move(destination: String, source: Value, width: Int) extends ExecutableInstruction(s"mov.u$width")

  case MovePredicate(destination: String, source: Value) extends ExecutableInstruction("mov.pred")

  case MoveSplit(destinations: Col[String], source: Value, partWidth: Int)
      extends ExecutableInstruction(s"mov.b${partWidth * 2}")

  case MoveCombine(destination: String, sources: Col[Value], partWidth: Int)
      extends ExecutableInstruction(s"mov.b${partWidth * 2}")

  case IntegerUnary(operation: IntegerUnaryOperation, destination: String, source: Value, width: Int)
      extends ExecutableInstruction(operation match {
        case IntegerUnaryOperation.Negate           => s"neg.b$width"
        case IntegerUnaryOperation.Absolute         => s"abs.b$width"
        case IntegerUnaryOperation.BitwiseNot       => s"not.b$width"
        case IntegerUnaryOperation.PopulationCount  => s"popc.b$width"
        case IntegerUnaryOperation.LeadingZeroCount => s"clz.b$width"
        case IntegerUnaryOperation.BitReverse       => s"brev.b$width"
      })

  case BitFind(
      destination: String,
      source: Value,
      width: Int,
      signed: Boolean,
      shiftAmount: Boolean
  ) extends ExecutableInstruction(s"bfind.${if (shiftAmount) "shiftamt." else ""}${if (signed) "s" else "u"}$width")

  case DotProduct4(
      destination: String,
      left: Value,
      right: Value,
      addend: Value,
      leftSigned: Boolean,
      rightSigned: Boolean
  ) extends ExecutableInstruction(s"dp4a.${if (leftSigned) "s" else "u"}32.${if (rightSigned) "s" else "u"}32")

  case IntegerBinary(
      operation: IntegerOperation,
      destination: String,
      left: Value,
      right: Value,
      width: Int
  ) extends ExecutableInstruction(
        operation match {
          case IntegerOperation.Add                    => s"add.u$width"
          case IntegerOperation.Subtract               => s"sub.u$width"
          case IntegerOperation.BitwiseOr              => s"or.b$width"
          case IntegerOperation.BitwiseAnd             => s"and.b$width"
          case IntegerOperation.BitwiseXor             => s"xor.b$width"
          case IntegerOperation.MultiplyLow            => s"mul.lo.u$width"
          case IntegerOperation.MultiplyHigh(signed)   => s"mul.hi.${if (signed) "s" else "u"}$width"
          case IntegerOperation.Divide(signed)         => s"div.${if (signed) "s" else "u"}$width"
          case IntegerOperation.Remainder(signed)      => s"rem.${if (signed) "s" else "u"}$width"
          case IntegerOperation.MultiplyWideUnsigned16 => "mul.wide.u16"
          case IntegerOperation.MultiplyWideUnsigned32 => "mul.wide.u32"
          case IntegerOperation.MultiplyWideSigned32   => "mul.wide.s32"
          case IntegerOperation.Minimum(signed)        => s"min.${if (signed) "s" else "u"}$width"
          case IntegerOperation.Maximum(signed)        => s"max.${if (signed) "s" else "u"}$width"
          case IntegerOperation.ShiftLeft              => s"shl.b$width"
          case IntegerOperation.ShiftRightLogical      => s"shr.u$width"
          case IntegerOperation.ShiftRightArithmetic   => s"shr.s$width"
        }
      )

  case IntegerAddWithCarry(
      destination: String,
      left: Value,
      right: Value,
      width: Int,
      carryIn: Boolean,
      carryOut: Boolean
  ) extends ExecutableInstruction(s"add${if (carryIn) "c" else ""}${if (carryOut) ".cc" else ""}.u$width")

  case IntegerSubWithBorrow(
      destination: String,
      left: Value,
      right: Value,
      width: Int,
      borrowIn: Boolean,
      borrowOut: Boolean
  ) extends ExecutableInstruction(s"sub${if (borrowIn) "c" else ""}${if (borrowOut) ".cc" else ""}.u$width")

  case MultiplyAdd(
      destination: String,
      left: Value,
      right: Value,
      addend: Value,
      operation: IntegerMultiplyAddOperation
  ) extends ExecutableInstruction(operation match {
        case IntegerMultiplyAddOperation.Low(operandWidth, _, _, _)  => s"mad.lo.u$operandWidth"
        case IntegerMultiplyAddOperation.High(operandWidth, _, _, _) => s"mad.hi.u$operandWidth"
      })

  case FloatBinary(
      operation: FloatOperation,
      destination: String,
      left: Value,
      right: Value,
      width: Int
  ) extends ExecutableInstruction(s"${operation.toString.toLowerCase}.f$width")

  case FloatBinaryPacked(
      operation: FloatOperation,
      destination: String,
      left: Value,
      right: Value,
      bf16: Boolean
  ) extends ExecutableInstruction(s"${operation.toString.toLowerCase}.${if (bf16) "bf16x2" else "f16x2"}")

  case FloatUnary(operation: FloatUnaryOperation, destination: String, source: Value, width: Int)
      extends ExecutableInstruction(operation match {
        case FloatUnaryOperation.Negate               => s"neg.f$width"
        case FloatUnaryOperation.Absolute             => s"abs.f$width"
        case FloatUnaryOperation.Reciprocal           => s"rcp.f$width"
        case FloatUnaryOperation.SquareRoot           => s"sqrt.f$width"
        case FloatUnaryOperation.ReciprocalSquareRoot => s"rsqrt.f$width"
        case FloatUnaryOperation.Exponent2            => s"ex2.f$width"
        case FloatUnaryOperation.Log2                 => s"lg2.f$width"
        case FloatUnaryOperation.Sine                 => s"sin.f$width"
        case FloatUnaryOperation.Cosine               => s"cos.f$width"
        case FloatUnaryOperation.Tanh                 => s"tanh.f$width"
      })

  case FusedMultiplyAdd(destination: String, left: Value, right: Value, addend: Value, width: Int)
      extends ExecutableInstruction(s"fma.f$width")

  case FusedMultiplyAddBf16(destination: String, left: Value, right: Value, addend: Value)
      extends ExecutableInstruction("fma.rn.bf16")

  case FusedMultiplyAddPacked(
      destination: String,
      left: Value,
      right: Value,
      addend: Value,
      bf16: Boolean
  ) extends ExecutableInstruction(s"fma.rn.${if (bf16) "bf16x2" else "f16x2"}")

  case ShuffleDown(
      destination: String,
      validity: Option[String],
      source: Value,
      delta: Value,
      clamp: Value,
      memberMask: Value,
      width: Int
  ) extends ExecutableInstruction(s"shfl.sync.down.b$width")

  case ShuffleUp(
      destination: String,
      validity: Option[String],
      source: Value,
      delta: Value,
      clamp: Value,
      memberMask: Value,
      width: Int
  ) extends ExecutableInstruction(s"shfl.sync.up.b$width")

  case ShuffleIndex(
      destination: String,
      validity: Option[String],
      source: Value,
      index: Value,
      clamp: Value,
      memberMask: Value,
      width: Int
  ) extends ExecutableInstruction(s"shfl.sync.idx.b$width")

  case ShuffleBfly(
      destination: String,
      validity: Option[String],
      source: Value,
      index: Value,
      clamp: Value,
      memberMask: Value,
      width: Int
  ) extends ExecutableInstruction(s"shfl.sync.bfly.b$width")

  case VoteBallot(destination: String, predicate: String, memberMask: Value)
      extends ExecutableInstruction("vote.sync.ballot.b32")

  case VotePredicate(destination: String, predicate: String, memberMask: Value, operation: VoteOperation)
      extends ExecutableInstruction(s"vote.sync.${operation.toString.toLowerCase}.pred")

  case BitFieldExtract(
      destination: String,
      source: Value,
      start: Value,
      widthValue: Value,
      width: Int,
      signed: Boolean
  ) extends ExecutableInstruction(s"bfe.${if (signed) "s" else "u"}$width")

  case BitFieldInsert(
      destination: String,
      insertValue: Value,
      baseValue: Value,
      bitPosition: Value,
      bitLength: Value,
      width: Int
  ) extends ExecutableInstruction(s"bfi.b$width")

  case BitMask(destination: String, start: Value, widthValue: Value, mode: String)
      extends ExecutableInstruction(s"bmsk.$mode.b32")

  case BytePermute(
      destination: String,
      left: Value,
      right: Value,
      control: Value,
      mode: String
  ) extends ExecutableInstruction(s"prmt.b32${if (mode.isEmpty) "" else s".$mode"}")

  case Select(destination: String, whenTrue: Value, whenFalse: Value, predicate: String, width: Int)
      extends ExecutableInstruction(s"selp.b$width")

  case Convert(
      destination: String,
      source: Value,
      sourceType: String,
      destinationType: String,
      rounding: Option[String]
  ) extends ExecutableInstruction(s"cvt.${rounding.map(_ + ".").getOrElse("")}$destinationType.$sourceType")

  case LoadGlobal(destination: String, address: Value, width: Int, signed: Boolean)
      extends ExecutableInstruction(s"ld.global.${if (signed) "s" else "u"}$width")

  case LoadGeneric(destination: String, address: Value, width: Int, signed: Boolean)
      extends ExecutableInstruction(s"ld.generic.${if (signed) "s" else "u"}$width")

  case DiscardGlobalLoad(address: Value, width: Int)
      extends ExecutableInstruction(s"ld.global.nc.u$width")

  case LoadGlobalVector(destinations: Col[String], address: Value, width: Int, signed: Boolean)
      extends ExecutableInstruction(s"ld.global.v${destinations.size}.${if (signed) "s" else "u"}$width")

  case LoadGenericVector(destinations: Col[String], address: Value, width: Int, signed: Boolean)
      extends ExecutableInstruction(s"ld.generic.v${destinations.size}.${if (signed) "s" else "u"}$width")

  case StoreGlobal(address: Value, source: Value, width: Int) extends ExecutableInstruction(s"st.global.u$width")

  case StoreGeneric(address: Value, source: Value, width: Int) extends ExecutableInstruction(s"st.generic.u$width")

  case StoreGlobalVector(address: Value, sources: Col[String], width: Int)
      extends ExecutableInstruction(s"st.global.v${sources.size}.u$width")

  case StoreGenericVector(address: Value, sources: Col[String], width: Int)
      extends ExecutableInstruction(s"st.generic.v${sources.size}.u$width")

  case LoadShared(destination: String, address: Value, width: Int, signed: Boolean)
      extends ExecutableInstruction(s"ld.shared.${if (signed) "s" else "u"}$width")

  case LoadSharedVector(destinations: Col[String], address: Value, width: Int, signed: Boolean)
      extends ExecutableInstruction(s"ld.shared.v${destinations.size}.${if (signed) "s" else "u"}$width")

  case LoadLocal(destination: String, address: Value, width: Int, signed: Boolean)
      extends ExecutableInstruction(s"ld.local.${if (signed) "s" else "u"}$width")

  case LoadLocalVector(destinations: Col[String], address: Value, width: Int, signed: Boolean)
      extends ExecutableInstruction(s"ld.local.v${destinations.size}.${if (signed) "s" else "u"}$width")

  case StoreShared(address: Value, source: Value, width: Int)
      extends ExecutableInstruction(s"st.shared.u$width")

  case StoreSharedVector(address: Value, sources: Col[String], width: Int)
      extends ExecutableInstruction(s"st.shared.v${sources.size}.u$width")

  case StoreLocal(address: Value, source: Value, width: Int)
      extends ExecutableInstruction(s"st.local.u$width")

  case StoreLocalVector(address: Value, sources: Col[String], width: Int)
      extends ExecutableInstruction(s"st.local.v${sources.size}.u$width")

  /** Synchronous model of the PTX asynchronous shared-memory copy. The emulator applies the copy at this instruction;
    * commit/wait operations below remain ordered no-ops because there is no separate copy engine.
    */
  case AsyncCopy(destination: Value, source: Value, size: Int)
      extends ExecutableInstruction("cp.async")

  case AsyncCommit extends ExecutableInstruction("cp.async.commit_group")

  case AsyncWait extends ExecutableInstruction("cp.async.wait_group")

  /** Cache prefetch hints have no architecturally visible effect in the emulator; preserve them as ordered no-ops so
    * generated kernels remain loadable and predication is still honoured.
    */
  case Prefetch extends ExecutableInstruction("prefetch")

  case SetPredicate(
      destination: String,
      comparison: Comparison,
      kind: Char,
      left: Value,
      right: Value,
      width: Int
  ) extends ExecutableInstruction("setp")

  case PredicateCompare(destination: String, comparison: Comparison, left: String, right: String)
      extends ExecutableInstruction("setp.pred")

  case PredicateUnary(destination: String, source: String)
      extends ExecutableInstruction("not.pred")

  case PredicateBinary(
      operation: PredicateOperation,
      destination: String,
      left: String,
      right: String
  ) extends ExecutableInstruction(s"${operation.toString.toLowerCase}.pred")

  case Branch(target: Pc) extends ExecutableInstruction("bra")

  case BarrierSync(id: Int) extends ExecutableInstruction("bar.sync")

  case BarrierReduce(destination: String, id: Int, predicate: String, operation: PredicateOperation)
      extends ExecutableInstruction(s"bar.red.${operation.toString.toLowerCase}.pred")

  case AtomicAdd(destination: String, address: Value, source: Value, width: Int, global: Boolean)
      extends ExecutableInstruction(s"atom.${if (global) "global" else "shared"}.add.u$width")

  case AtomicMin(destination: String, address: Value, source: Value, width: Int, global: Boolean)
      extends ExecutableInstruction(s"atom.${if (global) "global" else "shared"}.min.u$width")

  case AtomicAddFloat(destination: String, address: Value, source: Value, width: Int)
      extends ExecutableInstruction(s"atom.global.add.f$width")

  case WarpReduce(
      destination: String,
      source: Value,
      memberMask: Value,
      operation: IntegerOperation,
      width: Int
  ) extends ExecutableInstruction("redux.sync")

  case MemoryBarrier extends ExecutableInstruction("membar")

  case WarpBarrier(memberMask: Value) extends ExecutableInstruction("bar.warp.sync")

  case Call(function: Value, returnParameters: Col[String], parameters: Col[String])
      extends ExecutableInstruction("call")

  case Trap extends ExecutableInstruction("trap")

  case Return extends ExecutableInstruction("ret")
}

final case class ExecutionPredicate(name: String, negated: Boolean)

final case class PredicatedInstruction(instruction: ExecutableInstruction, predication: Option[ExecutionPredicate]) {
  def opcode: String = instruction.opcode
}

final case class KernelParameter(name: String, widthBytes: Int)
final case class Argument(bytes: Col[Byte])

final case class DeviceFunction(
    name: String,
    parameters: Col[String],
    parameterWidths: Col[Int],
    parameterOffsets: Col[Int],
    returnParameters: Col[String],
    program: ExecutableProgram[PredicatedInstruction],
    sharedSymbols: Map[String, Long]
)

object Argument {
  def apply(value: Long): Argument =
    Argument(LittleEndian.toCol(value, 8))
}

final case class Kernel(
    name: String,
    parameters: Col[KernelParameter],
    program: ExecutableProgram[PredicatedInstruction],
    functions: Map[String, DeviceFunction] = Map.empty,
    sharedSymbols: Map[String, Long] = Map.empty,
    globalSymbols: Map[String, Long] = Map.empty
) {
  def instructions: Col[ExecutableInstruction] = program.instructions.map(_.instruction)
}

enum LoweringError {
  case Invalid(detail: String)
  case Unsupported(instruction: String, detail: String)

  def message: String = this match {
    case Invalid(detail)                  => detail
    case Unsupported(instruction, detail) => s"unsupported instruction: $instruction ($detail)"
  }
}

@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection"
  )
)
object Lowering {
  def lower(entry: Statement.Entry): Either[LoweringError, Kernel] = lower(entry, Col.empty, Map.empty)

  def parameters(entry: Statement.Entry): Either[LoweringError, Col[KernelParameter]] =
    lowerParameters(entry.parameters)

  def lower(entry: Statement.Entry, functions: Col[Statement.Function]): Either[LoweringError, Kernel] =
    lower(entry, functions, Map.empty)

  def lower(
      entry: Statement.Entry,
      functions: Col[Statement.Function],
      globalSymbols: Map[String, Long]
  ): Either[LoweringError, Kernel] = lower(entry, functions, globalSymbols, Map.empty)

  def lower(
      entry: Statement.Entry,
      functions: Col[Statement.Function],
      globalSymbols: Map[String, Long],
      additionalSharedSymbols: Map[String, Long]
  ): Either[LoweringError, Kernel] =
    lower(entry, functions, globalSymbols, additionalSharedSymbols, Col.empty)

  def lower(
      entry: Statement.Entry,
      functions: Col[Statement.Function],
      globalSymbols: Map[String, Long],
      additionalSharedSymbols: Map[String, Long],
      additionalEntries: Col[Statement.Entry]
  ): Either[LoweringError, Kernel] = {
    val Statement.Entry(_, name, parameters, body) = entry
    val functionNames = functions.iterator.map(_.name).toSet ++ additionalEntries.iterator.map(_.name).toSet
    for {
      loweredParameters <- lowerParameters(parameters)
      loweredProgram    <- lowerProgram(body, functionNames)
      loweredFunctions  <- lowerReachableFunctions(loweredProgram, functions, additionalEntries, functionNames)
    } yield Kernel(
      name,
      loweredParameters,
      loweredProgram,
      loweredFunctions,
      sharedSymbols(body) ++ additionalSharedSymbols,
      globalSymbols
    )
  }

  private def lowerReachableFunctions(
      program: ExecutableProgram[PredicatedInstruction],
      functions: Col[Statement.Function],
      entries: Col[Statement.Entry],
      functionNames: Set[String]
  ): Either[LoweringError, Map[String, DeviceFunction]] = {
    val functionDefinitions = functions.iterator.map(function => function.name -> function).toMap
    val entryDefinitions    = entries.iterator.map(entry => entry.name -> entry).toMap
    val pending             = mutable.Queue.empty[String]
    val lowered             = mutable.Map.empty[String, DeviceFunction]

    def enqueueCalls(value: ExecutableProgram[PredicatedInstruction]): Unit =
      value.instructions.foreach {
        case PredicatedInstruction(ExecutableInstruction.Call(Value.Function(function), _, _), _) =>
          if (!lowered.contains(function) && !pending.contains(function)) pending.enqueue(function)
        case _ => ()
      }

    def enqueueAddressTakenFunctions(value: ExecutableProgram[PredicatedInstruction]): Unit =
      value.instructions.foreach {
        case PredicatedInstruction(ExecutableInstruction.Move(_, Value.Function(function), _), _) =>
          if (!lowered.contains(function) && !pending.contains(function)) pending.enqueue(function)
        case _ => ()
      }

    enqueueCalls(program)
    enqueueAddressTakenFunctions(program)
    while (pending.nonEmpty) {
      val name = pending.dequeue()
      functionDefinitions.get(name) match {
        case Some(function) if function.body.nonEmpty =>
          lowerFunction(function, functionNames) match {
            case Left(error)  => return Left(error)
            case Right(value) =>
              lowered(name) = value
              enqueueCalls(value.program)
              enqueueAddressTakenFunctions(value.program)
          }
        case Some(_) => ()
        case None    => entryDefinitions.get(name) match {
            case Some(entry) =>
              lowerEntryFunction(entry, functionNames) match {
                case Left(error)  => return Left(error)
                case Right(value) =>
                  lowered(name) = value
                  enqueueCalls(value.program)
                  enqueueAddressTakenFunctions(value.program)
              }
            case None => ()
          }
      }
    }
    Right(lowered.toMap)
  }

  private def lowerProgram(
      body: Col[Statement],
      functionNames: Set[String]
  ): Either[LoweringError, ExecutableProgram[PredicatedInstruction]] = {
    val flat = flattenScoped(body) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val labels           = mutable.Map.empty[String, Pc]
    var instructionIndex = 0
    var statementIndex   = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Label(label) =>
          if (labels.contains(label)) return Left(LoweringError.Invalid(s"duplicate label: $label"))
          labels(label) = Pc.unsafe(instructionIndex)
        case Statement.Instruction(_)                                       => instructionIndex += 1
        case Statement.UnsupportedInstruction(opcode, operands, mods, pred) =>
          val qualified = (opcode +: mods.map(_.name)).mkString(".")
          val rendered  = pred.map(value => s"${Printer.render(value)} ").getOrElse("") +
            qualified + (if (operands.isEmpty) "" else operands.map(Printer.render).mkString(" ", ", ", "")) + ";"
          return Left(LoweringError.Unsupported(rendered, "instruction is not implemented"))
        case _ => ()
      }
      statementIndex += 1
    }

    val resolvedLabels = labels.toMap
    val lowered        = Col.newBuilder[PredicatedInstruction]
    statementIndex = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Instruction(instruction) =>
          lowerInstruction(instruction, resolvedLabels, functionNames) match {
            case Left(error) =>
              val contextual = error match {
                case LoweringError.Invalid(detail) =>
                  LoweringError.Invalid(s"${Printer.render(instruction)}: $detail")
                case LoweringError.Unsupported(_, detail) =>
                  LoweringError.Unsupported(Printer.render(instruction), detail)
              }
              return Left(contextual)
            case Right(value) => lowered += value
          }
        case _ => ()
      }
      statementIndex += 1
    }
    Right(ExecutableProgram(lowered.result()))
  }

  private def lowerFunction(
      function: Statement.Function,
      functionNames: Set[String]
  ): Either[LoweringError, DeviceFunction] = {
    val Statement.Function(_, name, returnParameters, parameters, body) = function
    body match {
      case None             => Left(LoweringError.Invalid(s"function $name has no body"))
      case Some(statements) =>
        for {
          _                 <- lowerParameters(returnParameters)
          loweredParameters <- lowerParameters(parameters)
          program           <- lowerProgram(statements, functionNames)
        } yield DeviceFunction(
          name,
          parameters.map(_.name),
          loweredParameters.map(_.widthBytes),
          parameterOffsets(parameters, loweredParameters),
          returnParameters.map(_.name),
          program,
          sharedSymbols(statements)
        )
    }
  }

  private def lowerEntryFunction(
      entry: Statement.Entry,
      functionNames: Set[String]
  ): Either[LoweringError, DeviceFunction] = {
    val Statement.Entry(_, name, parameters, body) = entry
    for {
      loweredParameters <- lowerParameters(parameters)
      program           <- lowerProgram(body, functionNames)
    } yield DeviceFunction(
      name,
      parameters.map(_.name),
      loweredParameters.map(_.widthBytes),
      parameterOffsets(parameters, loweredParameters),
      Col.empty,
      program,
      sharedSymbols(body)
    )
  }

  private def parameterOffsets(
      declarations: Col[Parameter],
      parameters: Col[KernelParameter]
  ): Col[Int] = {
    val result = Col.newBuilder[Int]
    var offset = 0L
    declarations.zip(parameters).foreach { case (declaration, parameter) =>
      val qualifiers        = declaration.qualifiers
      val explicitAlignment = qualifiers.sliding(2).collectFirst {
        case Col("align", value) => value.toIntOption
      }.flatten
      val alignment = explicitAlignment.getOrElse(parameter.widthBytes)
      offset = (offset + alignment.toLong - 1L) & -alignment.toLong
      result += Math.toIntExact(offset)
      offset = Math.addExact(offset, parameter.widthBytes.toLong)
    }
    result.result()
  }

  private def lowerParameters(parameters: Col[Parameter]): Either[LoweringError, Col[KernelParameter]] = {
    val result = Col.newBuilder[KernelParameter]
    var index  = 0
    while (index < parameters.length) {
      val parameter = parameters(index)
      val widths    = parameter.qualifiers.collect {
        case "u8" | "s8" | "b8"            => 1
        case "u16" | "s16" | "b16"         => 2
        case "u32" | "s32" | "b32" | "f32" => 4
        case "u64" | "s64" | "b64" | "f64" => 8
      }
      widths.lastOption match {
        case Some(width) =>
          val elements = parameter.dimensions.foldLeft[Either[LoweringError, Long]](Right(1L)) { (current, value) =>
            for {
              count   <- current
              element <- immediate(value)
              product <-
                if (element > 0 && count <= Int.MaxValue.toLong / element) Right(count * element)
                else invalid(s"kernel parameter ${parameter.name} has an invalid array dimension: $element")
            } yield product
          }
          elements match {
            case Left(error)  => return Left(error)
            case Right(count) => result += KernelParameter(parameter.name, Math.toIntExact(width.toLong * count))
          }
        case None => return Left(LoweringError.Invalid(s"kernel parameter ${parameter.name} has no scalar width"))
      }
      index += 1
    }
    Right(result.result())
  }

  /** Flatten PTX blocks while alpha-renaming registers declared in nested scopes. */
  private def flattenScoped(statements: Col[Statement]): Either[LoweringError, Col[Statement]] = {
    var nextScope = 0

    def registerNames(operand: Operand): Iterator[String] = operand match {
      case Operand.Register(name, _)    => Iterator.single(name)
      case Operand.VectorLiteral(items) => items.iterator.flatMap(registerNames)
      case Operand.Parenthesized(items) => items.iterator.flatMap(registerNames)
      case _                            => Iterator.empty
    }

    def declaredRegisters(body: Col[Statement]): Col[String] =
      Col.toCol(body.iterator.flatMap {
        case Statement.Directive(path, arguments, _)
            if path == Col("reg") || path.headOption.contains("reg") =>
          arguments.iterator.flatMap(argument => registerNames(argument.operand))
        case _ => Iterator.empty
      }).distinct

    def renameOperand(operand: Operand, names: Map[String, String]): Operand = operand match {
      case Operand.Register(name, part)  => Operand.Register(names.getOrElse(name, name), part)
      case Operand.VectorLiteral(items)  => Operand.VectorLiteral(items.map(renameOperand(_, names)))
      case Operand.Parenthesized(items)  => Operand.Parenthesized(items.map(renameOperand(_, names)))
      case Operand.Address(items)        => Operand.Address(items.map(renameOperand(_, names)))
      case Operand.Index(base, index)    => Operand.Index(renameOperand(base, names), renameOperand(index, names))
      case Operand.Add(left, right)      => Operand.Add(renameOperand(left, names), renameOperand(right, names))
      case Operand.Subtract(left, right) =>
        Operand.Subtract(renameOperand(left, names), renameOperand(right, names))
      case Operand.WithPredicate(base, predicate) =>
        Operand.WithPredicate(renameOperand(base, names), renameOperand(predicate, names))
      case Operand.Negate(value) => Operand.Negate(renameOperand(value, names))
      case value                 => value
    }

    def renameInstruction(instruction: Instruction, names: Map[String, String]): Either[LoweringError, Instruction] = {
      val operands = instruction.operands.map(renameOperand(_, names))
      val pred     = instruction.pred.map(value => value.copy(operand = renameOperand(value.operand, names)))
      Instruction.decode(instruction.opcode, operands, instruction.mods, pred).leftMap(error =>
        LoweringError.Invalid(s"failed to preserve scoped instruction ${Printer.render(instruction)}: $error")
      )
    }

    def visit(body: Col[Statement], names: Map[String, String]): Either[LoweringError, Col[Statement]] = {
      val result = Col.newBuilder[Statement]
      var index  = 0
      while (index < body.size) {
        body(index) match {
          case Statement.Block(nested) =>
            val scope = nextScope
            nextScope += 1
            val scopedNames = declaredRegisters(nested).foldLeft(names) { (current, name) =>
              current.updated(name, s"$name$$scope$scope")
            }
            visit(nested, scopedNames) match {
              case Left(error)  => return Left(error)
              case Right(value) => value.foreach(result += _)
            }
          case Statement.Instruction(instruction) =>
            renameInstruction(instruction, names) match {
              case Left(error)  => return Left(error)
              case Right(value) => result += Statement.Instruction(value)
            }
          case Statement.UnsupportedInstruction(opcode, operands, mods, pred) =>
            val renamedOperands = operands.map(renameOperand(_, names))
            val renamedPred     = pred.map(value => value.copy(operand = renameOperand(value.operand, names)))
            result += Statement.UnsupportedInstruction(opcode, renamedOperands, mods, renamedPred)
          case value => result += value
        }
        index += 1
      }
      Right(result.result())
    }

    visit(statements, Map.empty)
  }

  private def flatten(statements: Col[Statement]): Col[Statement] = statements.flatMap {
    case Statement.Block(body) => flatten(body)
    case value                 => Col(value)
  }

  private def lowerInstruction(
      instruction: Instruction,
      labels: Map[String, Pc],
      functionNames: Set[String]
  ): Either[LoweringError, PredicatedInstruction] = {
    val lowered = instruction match {
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.param.") && isDirectParameterAddress(address) =>
        for {
          destinationNames   <- registerNames(destination)
          parameterAndOffset <- parameterAddress(address)
          width              <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadParameter(
          destinationNames,
          parameterAndOffset._1,
          parameterAndOffset._2,
          width,
          instruction.qualifiedOpcode.contains(".s")
        )
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.param.") =>
        for {
          parameterAndOffset <- parameterAddress(address)
          sourceValue        <- value(source)
          width              <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreParameter(
          parameterAndOffset._1,
          parameterAndOffset._2,
          sourceValue,
          width
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.param.") =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield
          if (destination.isInstanceOf[Operand.VectorLiteral])
            ExecutableInstruction.LoadParameterIndirectVector(
              destinationNames,
              addressValue,
              width,
              instruction.qualifiedOpcode.contains(".s")
            )
          else
            ExecutableInstruction.LoadParameterIndirect(
              destinationNames.head,
              addressValue,
              width,
              instruction.qualifiedOpcode.contains(".s")
            )
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.pred" =>
        for {
          destinationName <- predicateRegisterName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.MovePredicate(destinationName, sourceValue)
      case Instruction.Activemask(destination, _, _) =>
        registerName(destination).map(destinationName =>
          ExecutableInstruction.Move(destinationName, Value.ActiveMask, 32)
        )
      case Instruction.Mov(destination, source, _, _)
          if Set("mov.u32", "mov.s32", "mov.b32").contains(instruction.qualifiedOpcode) &&
            !destination.isInstanceOf[Operand.VectorLiteral] &&
            !source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 32)
      case Instruction.Mov(destination, source, modifiers, _)
          if Set("mov.u8", "mov.s8", "mov.b8", "mov.u16", "mov.s16", "mov.b16").contains(instruction.qualifiedOpcode) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, width)
      case Instruction.Mov(destination, source, _, _)
          if (instruction.qualifiedOpcode == "mov.u64" || instruction.qualifiedOpcode == "mov.b64") &&
            destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- splitRegisterNames(destination)
          sourceValue      <- value(source)
          _                <- requirePair(destinationNames, "mov.b64 destination")
        } yield ExecutableInstruction.MoveSplit(destinationNames, sourceValue, 32)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.b32" && destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- splitRegisterNames(destination)
          sourceValue      <- value(source)
          _                <- requirePair(destinationNames, "mov.b32 destination")
        } yield ExecutableInstruction.MoveSplit(destinationNames, sourceValue, 16)
      case Instruction.Mov(destination, source, _, _)
          if (instruction.qualifiedOpcode == "mov.u64" || instruction.qualifiedOpcode == "mov.b64") &&
            source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationName <- registerName(destination)
          sourceValues    <- vectorValues(source)
          _               <- requirePair(sourceValues, "mov.b64 source")
        } yield ExecutableInstruction.MoveCombine(destinationName, sourceValues, 32)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.b32" && source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationName <- registerName(destination)
          sourceValues    <- vectorValues(source)
          _               <- requirePair(sourceValues, "mov.b32 source")
        } yield ExecutableInstruction.MoveCombine(destinationName, sourceValues, 16)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.u64" || instruction.qualifiedOpcode == "mov.b64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- functionValue(source, functionNames, promoteUnknown = false)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.f32" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 32)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.f64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Neg(destination, source, modifiers, _) if !instruction.qualifiedOpcode.contains(".f") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.IntegerUnary(IntegerUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Neg(destination, source, modifiers, _) if instruction.qualifiedOpcode.contains(".f") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- floatWidth(modifiers)
        } yield ExecutableInstruction.FloatUnary(FloatUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Abs(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("abs.f") =>
        floatUnary(FloatUnaryOperation.Absolute, destination, source, modifiers)
      case Instruction.Abs(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("abs.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.IntegerUnary(
          IntegerUnaryOperation.Absolute,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.RcpApproxFtzF64(destination, source, modifiers, _) =>
        floatUnary(FloatUnaryOperation.Reciprocal, destination, source, modifiers, Some(64))
      case Instruction.Rcp(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("rcp.") =>
        floatUnary(FloatUnaryOperation.Reciprocal, destination, source, modifiers)
      case Instruction.RsqrtApproxFtzF64(destination, source, modifiers, _) =>
        floatUnary(FloatUnaryOperation.ReciprocalSquareRoot, destination, source, modifiers, Some(64))
      case Instruction.Rsqrt(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("rsqrt.") =>
        floatUnary(FloatUnaryOperation.ReciprocalSquareRoot, destination, source, modifiers)
      case Instruction.Sqrt(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sqrt.") =>
        floatUnary(FloatUnaryOperation.SquareRoot, destination, source, modifiers)
      case Instruction.Ex2(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ex2.") =>
        floatUnary(FloatUnaryOperation.Exponent2, destination, source, modifiers)
      case Instruction.Lg2(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("lg2.") =>
        floatUnary(FloatUnaryOperation.Log2, destination, source, modifiers)
      case Instruction.Sin(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sin.") =>
        floatUnary(FloatUnaryOperation.Sine, destination, source, modifiers)
      case Instruction.Cos(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("cos.") =>
        floatUnary(FloatUnaryOperation.Cosine, destination, source, modifiers)
      case Instruction.Tanh(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("tanh.") =>
        floatUnary(FloatUnaryOperation.Tanh, destination, source, modifiers)
      case Instruction.Not(destination, source, modifiers, _) =>
        if (instruction.qualifiedOpcode == "not.pred")
          for {
            destinationName <- predicateRegisterName(destination)
            sourceName      <- predicateRegisterName(source)
          } yield ExecutableInstruction.PredicateUnary(destinationName, sourceName)
        else
          for {
            destinationName <- registerName(destination)
            sourceValue     <- value(source)
            width           <- integerWidth(modifiers)
          } yield ExecutableInstruction.IntegerUnary(
            IntegerUnaryOperation.BitwiseNot,
            destinationName,
            sourceValue,
            width
          )
      case Instruction.Popc(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("popc.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.IntegerUnary(
          IntegerUnaryOperation.PopulationCount,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.Clz(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("clz.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.IntegerUnary(
          IntegerUnaryOperation.LeadingZeroCount,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.Bfind(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("bfind.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.BitFind(
          destinationName,
          sourceValue,
          width,
          instruction.qualifiedOpcode.contains(".s"),
          instruction.qualifiedOpcode.contains(".shiftamt.")
        )
      case Instruction.Dp4a(destination, left, right, addend, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("dp4a.") =>
        val types = modifiers.map(_.name).filter(Set("s32", "u32"))
        if (types.size != 2) invalid(s"expected two dp4a operand types: ${instruction.qualifiedOpcode}")
        else
          for {
            destinationName <- registerName(destination)
            leftValue       <- value(left)
            rightValue      <- value(right)
            addendValue     <- value(addend)
          } yield ExecutableInstruction.DotProduct4(
            destinationName,
            leftValue,
            rightValue,
            addendValue,
            types.head == "s32",
            types(1) == "s32"
          )
      case Instruction.Brev(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("brev.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.IntegerUnary(
          IntegerUnaryOperation.BitReverse,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.u32" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield ExecutableInstruction.IntegerBinary(
          IntegerOperation.MultiplyWideUnsigned32,
          destinationName,
          leftValue,
          rightValue,
          64
        )
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.s32" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield ExecutableInstruction.IntegerBinary(
          IntegerOperation.MultiplyWideSigned32,
          destinationName,
          leftValue,
          rightValue,
          64
        )
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.u16" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield ExecutableInstruction.IntegerBinary(
          IntegerOperation.MultiplyWideUnsigned16,
          destinationName,
          leftValue,
          rightValue,
          32
        )
      case Instruction.Div(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.endsWith(".f16") ||
            instruction.qualifiedOpcode.endsWith(".f32") ||
            instruction.qualifiedOpcode.endsWith(".f64") =>
        floatBinary(FloatOperation.Divide, destination, left, right, modifiers)
      case Instruction.Div(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("div.") =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Divide(instruction.qualifiedOpcode.contains(".s")),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Rem(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("rem.") =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Remainder(instruction.qualifiedOpcode.contains(".s")),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Mul(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("mul.lo.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.MultiplyLow, _, destination, left, right))
      case Instruction.Mul(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("mul.hi.") =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.MultiplyHigh(instruction.qualifiedOpcode.contains(".s")),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Mul(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        packedFloatBinary(
          FloatOperation.Multiply,
          destination,
          left,
          right,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Mul(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("mul.") &&
            (instruction.qualifiedOpcode.endsWith(".f16") ||
              instruction.qualifiedOpcode.endsWith(".f32") ||
              instruction.qualifiedOpcode.endsWith(".f64")) =>
        floatBinary(FloatOperation.Multiply, destination, left, right, modifiers)
      case Instruction.CopySign(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("copysign.") =>
        floatBinary(FloatOperation.CopySign, destination, left, right, modifiers)
      case Instruction.Fma(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
        } yield ExecutableInstruction.FusedMultiplyAddPacked(
          destinationName,
          leftValue,
          rightValue,
          addendValue,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Fma(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode.endsWith(".bf16") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
        } yield ExecutableInstruction.FusedMultiplyAddBf16(destinationName, leftValue, rightValue, addendValue)
      case Instruction.Fma(destination, left, right, addend, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("fma.") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width           <- floatWidth(modifiers)
        } yield ExecutableInstruction.FusedMultiplyAdd(destinationName, leftValue, rightValue, addendValue, width)
      case Instruction.Shl(destination, left, right, _, _) if instruction.qualifiedOpcode == "shl.b32" =>
        integerBinary(IntegerOperation.ShiftLeft, 32, destination, left, right)
      case Instruction.Shl(destination, left, right, modifiers, _) if instruction.qualifiedOpcode.startsWith("shl.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.ShiftLeft, _, destination, left, right))
      case Instruction.Shr(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("shr.u") || instruction.qualifiedOpcode.startsWith("shr.b") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.ShiftRightLogical, _, destination, left, right))
      case Instruction.Shr(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("shr.s") =>
        integerWidth(modifiers).flatMap(integerBinary(
          IntegerOperation.ShiftRightArithmetic,
          _,
          destination,
          left,
          right
        ))
      case Instruction.ShflSync(destination, source, delta, clamp, memberMask, modifiers, _)
          if Set("shfl.sync.down.b32", "shfl.sync.down.b64").contains(instruction.qualifiedOpcode) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          deltaValue      <- value(delta)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.ShuffleDown(
          destinationInfo._1,
          destinationInfo._2,
          sourceValue,
          deltaValue,
          clampValue,
          memberMaskValue,
          width
        )
      case Instruction.ShflSync(destination, source, delta, clamp, memberMask, modifiers, _)
          if Set("shfl.sync.up.b32", "shfl.sync.up.b64").contains(instruction.qualifiedOpcode) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          deltaValue      <- value(delta)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.ShuffleUp(
          destinationInfo._1,
          destinationInfo._2,
          sourceValue,
          deltaValue,
          clampValue,
          memberMaskValue,
          width
        )
      case Instruction.ShflSync(destination, source, index, clamp, memberMask, modifiers, _)
          if Set("shfl.sync.idx.b32", "shfl.sync.idx.b64").contains(instruction.qualifiedOpcode) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          indexValue      <- value(index)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.ShuffleIndex(
          destinationInfo._1,
          destinationInfo._2,
          sourceValue,
          indexValue,
          clampValue,
          memberMaskValue,
          width
        )
      case Instruction.ShflSync(destination, source, index, clamp, memberMask, modifiers, _)
          if Set("shfl.sync.bfly.b32", "shfl.sync.bfly.b64").contains(instruction.qualifiedOpcode) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          indexValue      <- value(index)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.ShuffleBfly(
          destinationInfo._1,
          destinationInfo._2,
          sourceValue,
          indexValue,
          clampValue,
          memberMaskValue,
          width
        )
      case Instruction.Vote(destination, predicate, _, _) if instruction.qualifiedOpcode == "vote.ballot.b32" =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
        } yield ExecutableInstruction.VoteBallot(destinationName, predicateName, Value.Immediate(0xffffffffL))
      case Instruction.VoteSync(destination, predicate, memberMask, _, _)
          if instruction.qualifiedOpcode == "vote.sync.ballot.b32" =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
          memberMaskValue <- value(memberMask)
        } yield ExecutableInstruction.VoteBallot(destinationName, predicateName, memberMaskValue)
      case Instruction.VoteSync(destination, predicate, memberMask, _, _)
          if instruction.qualifiedOpcode == "vote.sync.all.pred" ||
            instruction.qualifiedOpcode == "vote.sync.any.pred" ||
            instruction.qualifiedOpcode == "vote.sync.uni.pred" =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
          memberMaskValue <- value(memberMask)
          operation = if (instruction.qualifiedOpcode.endsWith(".all.pred")) VoteOperation.All
          else if (instruction.qualifiedOpcode.endsWith(".any.pred")) VoteOperation.Any
          else VoteOperation.Uniform
        } yield ExecutableInstruction.VotePredicate(destinationName, predicateName, memberMaskValue, operation)
      case Instruction.Bfe(destination, source, start, widthValue, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("bfe.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          startValue      <- value(start)
          widthOperand    <- value(widthValue)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.BitFieldExtract(
          destinationName,
          sourceValue,
          startValue,
          widthOperand,
          width,
          instruction.qualifiedOpcode.contains(".s")
        )
      case Instruction.Bfi(destination, insertValue, baseValue, bitPosition, bitLength, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("bfi.") =>
        for {
          destinationName <- registerName(destination)
          insertOperand   <- value(insertValue)
          baseOperand     <- value(baseValue)
          positionOperand <- value(bitPosition)
          lengthOperand   <- value(bitLength)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.BitFieldInsert(
          destinationName,
          insertOperand,
          baseOperand,
          positionOperand,
          lengthOperand,
          width
        )
      case Instruction.Bmsk(destination, start, widthValue, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("bmsk.") =>
        for {
          destinationName <- registerName(destination)
          startValue      <- value(start)
          widthOperand    <- value(widthValue)
          mode            <- modifiers.map(_.name).find(name => name == "clamp" || name == "wrap")
            .toRight(LoweringError.Invalid("bmsk requires .clamp or .wrap"))
          _ <- modifiers.map(_.name).find(_.startsWith("b32")).toRight(
            LoweringError.Invalid("bmsk requires a .b32 width")
          )
        } yield ExecutableInstruction.BitMask(destinationName, startValue, widthOperand, mode)
      case Instruction.Prmt(destination, left, right, control, _, _)
          if instruction.qualifiedOpcode.startsWith("prmt.b32") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          controlValue    <- value(control)
          mode = instruction.qualifiedOpcode.split("\\.").drop(2).headOption.getOrElse("")
        } yield ExecutableInstruction.BytePermute(
          destinationName,
          leftValue,
          rightValue,
          controlValue,
          mode
        )
      case Instruction.Mad(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode.contains(".lo.") &&
            (instruction.qualifiedOpcode.endsWith(".s32") || instruction.qualifiedOpcode.endsWith(".u32") ||
              instruction.qualifiedOpcode.endsWith(".s64") || instruction.qualifiedOpcode.endsWith(".u64")) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.MultiplyAdd(
          destinationName,
          leftValue,
          rightValue,
          addendValue,
          IntegerMultiplyAddOperation.Low(
            width,
            width,
            carryIn = false,
            carryOut = instruction.qualifiedOpcode.contains(".cc.")
          )
        )
      case Instruction.MadCc(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode.contains(".lo.") &&
            (instruction.qualifiedOpcode.endsWith(".s32") || instruction.qualifiedOpcode.endsWith(".u32") ||
              instruction.qualifiedOpcode.endsWith(".s64") || instruction.qualifiedOpcode.endsWith(".u64")) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.MultiplyAdd(
          destinationName,
          leftValue,
          rightValue,
          addendValue,
          IntegerMultiplyAddOperation.Low(width, width, carryIn = false, carryOut = true)
        )
      case Instruction.Mad(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode.contains(".wide.") && instruction.qualifiedOpcode.endsWith(".u32") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
        } yield ExecutableInstruction.MultiplyAdd(
          destinationName,
          leftValue,
          rightValue,
          addendValue,
          IntegerMultiplyAddOperation.Low(32, 64, carryIn = false, carryOut = false)
        )
      case Instruction.Madc(destination, left, right, addend, _, _)
          if (instruction.qualifiedOpcode.contains(".lo.") || instruction.qualifiedOpcode.contains(".hi.")) &&
            (instruction.qualifiedOpcode.endsWith(".s32") || instruction.qualifiedOpcode.endsWith(".u32") ||
              instruction.qualifiedOpcode.endsWith(".s64") || instruction.qualifiedOpcode.endsWith(".u64")) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width     = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
          operation = if (instruction.qualifiedOpcode.contains(".hi."))
            IntegerMultiplyAddOperation.High(
              width,
              width,
              carryIn = true,
              carryOut = instruction.qualifiedOpcode.contains(".cc.")
            )
          else IntegerMultiplyAddOperation.Low(
            width,
            width,
            carryIn = true,
            carryOut = instruction.qualifiedOpcode.contains(".cc.")
          )
        } yield ExecutableInstruction.MultiplyAdd(destinationName, leftValue, rightValue, addendValue, operation)
      case Instruction.AddCc(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".u32") || instruction.qualifiedOpcode.endsWith(".s32") ||
            instruction.qualifiedOpcode.endsWith(".u64") || instruction.qualifiedOpcode.endsWith(".s64") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.IntegerAddWithCarry(
          destinationName,
          leftValue,
          rightValue,
          width,
          carryIn = false,
          carryOut = true
        )
      case Instruction.Addc(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".u32") || instruction.qualifiedOpcode.endsWith(".s32") ||
            instruction.qualifiedOpcode.endsWith(".u64") || instruction.qualifiedOpcode.endsWith(".s64") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.IntegerAddWithCarry(
          destinationName,
          leftValue,
          rightValue,
          width,
          carryIn = true,
          carryOut = instruction.qualifiedOpcode.contains(".cc.")
        )
      case Instruction.SubCc(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".u32") || instruction.qualifiedOpcode.endsWith(".s32") ||
            instruction.qualifiedOpcode.endsWith(".u64") || instruction.qualifiedOpcode.endsWith(".s64") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.IntegerSubWithBorrow(
          destinationName,
          leftValue,
          rightValue,
          width,
          borrowIn = false,
          borrowOut = true
        )
      case Instruction.Subc(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".u32") || instruction.qualifiedOpcode.endsWith(".s32") ||
            instruction.qualifiedOpcode.endsWith(".u64") || instruction.qualifiedOpcode.endsWith(".s64") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.IntegerSubWithBorrow(
          destinationName,
          leftValue,
          rightValue,
          width,
          borrowIn = true,
          borrowOut = instruction.qualifiedOpcode.contains(".cc.")
        )
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "add.u32" || instruction.qualifiedOpcode == "add.s32" =>
        integerBinary(IntegerOperation.Add, 32, destination, left, right)
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "add.u64" || instruction.qualifiedOpcode == "add.s64" =>
        integerBinary(IntegerOperation.Add, 64, destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "sub.u32" || instruction.qualifiedOpcode == "sub.s32" =>
        integerBinary(IntegerOperation.Subtract, 32, destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "sub.u64" || instruction.qualifiedOpcode == "sub.s64" =>
        integerBinary(IntegerOperation.Subtract, 64, destination, left, right)
      case Instruction.And(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "and.pred" =>
        predicateBinary(PredicateOperation.And, destination, left, right)
      case Instruction.Or(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "or.pred" =>
        predicateBinary(PredicateOperation.Or, destination, left, right)
      case Instruction.Xor(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "xor.pred" =>
        predicateBinary(PredicateOperation.Xor, destination, left, right)
      case Instruction.And(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("and.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.BitwiseAnd, _, destination, left, right))
      case Instruction.Or(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("or.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.BitwiseOr, _, destination, left, right))
      case Instruction.Xor(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("xor.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.BitwiseXor, _, destination, left, right))
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        packedFloatBinary(
          FloatOperation.Add,
          destination,
          left,
          right,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Add(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("add.") &&
            (instruction.qualifiedOpcode.endsWith(".f16") ||
              instruction.qualifiedOpcode.endsWith(".f32") ||
              instruction.qualifiedOpcode.endsWith(".f64")) =>
        floatBinary(FloatOperation.Add, destination, left, right, modifiers)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        packedFloatBinary(
          FloatOperation.Subtract,
          destination,
          left,
          right,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Sub(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sub.") &&
            (instruction.qualifiedOpcode.endsWith(".f16") ||
              instruction.qualifiedOpcode.endsWith(".f32") ||
              instruction.qualifiedOpcode.endsWith(".f64")) =>
        floatBinary(FloatOperation.Subtract, destination, left, right, modifiers)
      case Instruction.Add(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("add.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.Add, _, destination, left, right))
      case Instruction.Sub(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sub.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.Subtract, _, destination, left, right))
      case Instruction.Min(destination, left, right, _, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        packedFloatBinary(
          FloatOperation.Minimum,
          destination,
          left,
          right,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Min(destination, left, right, _, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("min.f") =>
        floatBinary(FloatOperation.Minimum, destination, left, right, modifiers)
      case Instruction.Max(destination, left, right, _, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        packedFloatBinary(
          FloatOperation.Maximum,
          destination,
          left,
          right,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Max(destination, left, right, _, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("max.f") =>
        floatBinary(FloatOperation.Maximum, destination, left, right, modifiers)
      case Instruction.Min(destination, left, right, _, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("min.") =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Minimum(instruction.qualifiedOpcode.contains(".s")),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Max(destination, left, right, _, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("max.") =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Maximum(instruction.qualifiedOpcode.contains(".s")),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Cvta(destination, source, _, _)
          if instruction.qualifiedOpcode == "cvta.global.u64" || instruction.qualifiedOpcode == "cvta.to.global.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvta(destination, source, _, _) if instruction.qualifiedOpcode == "cvta.local.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvta(destination, source, _, _) if instruction.qualifiedOpcode == "cvta.to.local.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvta(destination, source, _, _)
          if instruction.qualifiedOpcode == "cvta.shared.u64" || instruction.qualifiedOpcode == "cvta.to.shared.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvt(destination, source, None, None, modifiers, _) =>
        val types = modifiers.map(_.name).filter(isScalarType)
        if (types.size < 2) invalid(s"expected conversion source and destination types: ${instruction.qualifiedOpcode}")
        else
          for {
            destinationName <- registerName(destination)
            sourceValue     <- value(source)
          } yield ExecutableInstruction.Convert(
            destinationName,
            sourceValue,
            types.last,
            types(types.size - 2),
            modifiers.map(_.name).find(name => Set("rn", "rni", "rz", "rzi", "rm", "rmi", "rp", "rpi").contains(name))
          )
      case Instruction.Selp(destination, whenTrue, whenFalse, predicate, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("selp.") =>
        for {
          destinationName <- registerName(destination)
          falseValue      <- value(whenFalse)
          trueValue       <- value(whenTrue)
          predicateName   <- registerName(predicate)
          width           <- scalarWidth(modifiers)
        } yield ExecutableInstruction.Select(destinationName, trueValue, falseValue, predicateName, width)
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.matches("ld\\.(?:[usb]\\d+|f\\d+)") &&
            !destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGeneric(
          destinationName,
          addressValue,
          width,
          instruction.qualifiedOpcode.contains(".s")
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") &&
            !instruction.qualifiedOpcode.contains(".param.") &&
            !instruction.qualifiedOpcode.contains(".shared.") &&
            !instruction.qualifiedOpcode.contains(".local.") &&
            destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGenericVector(
          destinationNames,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".global.") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGlobal(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") &&
            !instruction.qualifiedOpcode.contains(".param.") &&
            !instruction.qualifiedOpcode.contains(".shared.") &&
            !instruction.qualifiedOpcode.contains(".local.") &&
            !instruction.qualifiedOpcode.contains(".global.") &&
            !destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGlobal(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.LdGlobalNc(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.global.nc.") && destination == Operand.Symbol("datum") =>
        for {
          addressValue <- addressValue(address)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGlobal(
          "datum",
          addressValue,
          width,
          instruction.qualifiedOpcode.contains(".s")
        )
      case Instruction.LdGlobalNc(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith(
            "ld.global.nc."
          ) && destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGlobalVector(
          destinationNames,
          addressValue,
          width,
          instruction.qualifiedOpcode.contains(".s")
        )
      case Instruction.LdGlobalNc(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.global.nc.") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGlobal(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.matches("st\\.(?:[usb]\\d+|f\\d+)") &&
            !source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreGeneric(addressValue, sourceValue, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") &&
            !instruction.qualifiedOpcode.contains(".shared.") &&
            !instruction.qualifiedOpcode.contains(".local.") &&
            source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- registerNames(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreGenericVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") &&
            !instruction.qualifiedOpcode.contains(".shared.") &&
            !instruction.qualifiedOpcode.contains(".local.") &&
            !source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreGlobal(addressValue, sourceValue, width)
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".shared.") &&
            destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadSharedVector(
          destinationNames,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".shared.") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadShared(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".shared.") &&
            source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- registerNames(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreSharedVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".shared.") =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreShared(addressValue, sourceValue, width)
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".local.") &&
            destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadLocalVector(
          destinationNames,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".local.") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadLocal(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".local.") &&
            source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- registerNames(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreLocalVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".local.") =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreLocal(addressValue, sourceValue, width)
      case Instruction.CpAsync(destination, source, copySize, _, _, _, _) =>
        for {
          destinationValue <- addressValue(destination)
          sourceValue      <- addressValue(source)
          size             <- immediate(copySize).flatMap { value =>
            if (Set(4L, 8L, 16L).contains(value)) Right(value.toInt)
            else invalid(s"cp.async copy size must be 4, 8, or 16 bytes, found $value")
          }
        } yield ExecutableInstruction.AsyncCopy(destinationValue, sourceValue, size)
      case Instruction.CpAsyncCommitGroup(_, _)  => Right(ExecutableInstruction.AsyncCommit)
      case Instruction.CpAsyncWaitAll(_, _)      => Right(ExecutableInstruction.AsyncWait)
      case Instruction.CpAsyncWaitGroup(_, _, _) => Right(ExecutableInstruction.AsyncWait)
      case Instruction.ReduxSync(destination, source, memberMask, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("redux.sync.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
          operation       <-
            if (instruction.qualifiedOpcode.contains(".add.")) Right(IntegerOperation.Add)
            else if (instruction.qualifiedOpcode.contains(".min."))
              Right(IntegerOperation.Minimum(instruction.qualifiedOpcode.contains(".s")))
            else if (instruction.qualifiedOpcode.contains(".max."))
              Right(IntegerOperation.Maximum(instruction.qualifiedOpcode.contains(".s")))
            else invalid(s"unsupported redux.sync operation ${instruction.qualifiedOpcode}")
        } yield ExecutableInstruction.WarpReduce(destinationName, sourceValue, memberMaskValue, operation, width)
      case Instruction.Atom(destination, address, source, None, _, _)
          if instruction.qualifiedOpcode.matches("atom\\.global\\.add\\.u(?:32|64)") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          sourceValue     <- value(source)
          width = if (instruction.qualifiedOpcode.endsWith(".u64")) 64 else 32
        } yield ExecutableInstruction.AtomicAdd(destinationName, addressValue, sourceValue, width, global = true)
      case Instruction.Atom(destination, address, source, None, _, _)
          if instruction.qualifiedOpcode.matches("atom\\.shared\\.add\\.u(?:32|64)") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          sourceValue     <- value(source)
          width = if (instruction.qualifiedOpcode.endsWith(".u64")) 64 else 32
        } yield ExecutableInstruction.AtomicAdd(destinationName, addressValue, sourceValue, width, global = false)
      case Instruction.Atom(destination, address, source, None, _, _)
          if instruction.qualifiedOpcode.matches("atom\\.(?:shared|global)\\.min\\.u(?:32|64)") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          sourceValue     <- value(source)
          width = if (instruction.qualifiedOpcode.endsWith(".u64")) 64 else 32
        } yield ExecutableInstruction.AtomicMin(
          destinationName,
          addressValue,
          sourceValue,
          width,
          global = instruction.qualifiedOpcode.startsWith("atom.global.")
        )
      case Instruction.Atom(destination, address, source, None, _, _)
          if instruction.qualifiedOpcode == "atom.global.add.f32" || instruction.qualifiedOpcode == "atom.global.add.f64" =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          sourceValue     <- value(source)
          width = if (instruction.qualifiedOpcode.endsWith(".f64")) 64 else 32
        } yield ExecutableInstruction.AtomicAddFloat(destinationName, addressValue, sourceValue, width)
      case Instruction.Setp(destination, left, right, None, modifiers, _) =>
        lowerSetPredicate(destination, left, right, modifiers)
      case Instruction.Bar(destination, Some(barrier), Some(predicate), None, _, _)
          if instruction.qualifiedOpcode == "bar.red.and.pred" || instruction.qualifiedOpcode == "bar.red.or.pred" =>
        for {
          destinationName <- predicateRegisterName(destination)
          barrierId       <- immediate(barrier)
          predicateName   <- predicateRegisterName(predicate)
          operation = if (instruction.qualifiedOpcode.endsWith(".and.pred")) PredicateOperation.And
          else PredicateOperation.Or
          checkedBarrierId <-
            if (barrierId >= 0L && barrierId <= Int.MaxValue.toLong) Right(barrierId.toInt)
            else invalid(s"barrier id is outside Int range: $barrierId")
        } yield ExecutableInstruction.BarrierReduce(destinationName, checkedBarrierId, predicateName, operation)
      case Instruction.Bra(target, _, _)
          if instruction.qualifiedOpcode == "bra" || instruction.qualifiedOpcode == "bra.uni" =>
        target match {
          case Operand.Symbol(label) => labels.get(label).toRight(LoweringError.Invalid(s"unknown label: $label"))
              .map(ExecutableInstruction.Branch.apply)
          case other => invalid(s"expected branch label, found ${Printer.render(other)}")
        }
      case Instruction.Bar(_, _, _, _, _, Some(_)) if instruction.qualifiedOpcode == "bar.sync" =>
        invalid("bar.sync must not be predicated in the supported execution profile")
      case Instruction.Bar(barrier, None, None, None, _, _) if instruction.qualifiedOpcode == "bar.sync" =>
        immediate(barrier).flatMap { value =>
          if (value >= 0L && value <= Int.MaxValue.toLong) Right(ExecutableInstruction.BarrierSync(value.toInt))
          else invalid(s"barrier id is outside Int range: $value")
        }
      case Instruction.Membar(_, _) if instruction.qualifiedOpcode.startsWith("membar.") =>
        Right(ExecutableInstruction.MemoryBarrier)
      case Instruction.Prefetch(_, _, _) =>
        Right(ExecutableInstruction.Prefetch)
      case Instruction.NanoSleep(_, _, _) =>
        // nanosleep is a scheduling hint; it has no observable architectural
        // effect in the deterministic emulator.
        Right(ExecutableInstruction.Prefetch)
      case Instruction.BarWarpSync(memberMask, _, _) =>
        value(memberMask).map(ExecutableInstruction.WarpBarrier.apply)
      case Instruction.Exit(_, _)                                        => Right(ExecutableInstruction.Return)
      case Instruction.Ret(_, _)                                         => Right(ExecutableInstruction.Return)
      case Instruction.Trap(_, _)                                        => Right(ExecutableInstruction.Trap)
      case Instruction.Call(returnParameters, function, params, _, _, _) =>
        for {
          functionValue  <- functionValue(function, functionNames, promoteUnknown = true)
          returnNames    <- callParameters(returnParameters)
          parameterNames <- callParameters(params)
        } yield ExecutableInstruction.Call(functionValue, returnNames, parameterNames)
      case _ =>
        Left(LoweringError.Unsupported(Printer.render(instruction), "no handwritten semantic family"))
    }
    for {
      operation <- lowered
      predicate <- lowerPredicate(instruction.pred)
    } yield PredicatedInstruction(operation, predicate)
  }

  private def integerBinary(
      operation: IntegerOperation,
      width: Int,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- predicateRegisterName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
    } yield ExecutableInstruction.IntegerBinary(operation, destinationName, leftValue, rightValue, width)

  private def floatBinary(
      operation: FloatOperation,
      destination: Operand,
      left: Operand,
      right: Operand,
      modifiers: Col[Modifier]
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
      width           <- integerWidth(modifiers)
    } yield ExecutableInstruction.FloatBinary(operation, destinationName, leftValue, rightValue, width)

  private def packedFloatBinary(
      operation: FloatOperation,
      destination: Operand,
      left: Operand,
      right: Operand,
      bf16: Boolean
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
    } yield ExecutableInstruction.FloatBinaryPacked(operation, destinationName, leftValue, rightValue, bf16)

  private def floatUnary(
      operation: FloatUnaryOperation,
      destination: Operand,
      source: Operand,
      modifiers: Col[Modifier],
      fixedWidth: Option[Int] = None
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- registerName(destination)
      sourceValue     <- value(source)
      width           <- fixedWidth.fold(floatWidth(modifiers))(Right.apply)
    } yield ExecutableInstruction.FloatUnary(operation, destinationName, sourceValue, width)

  private def lowerSetPredicate(
      destination: Operand,
      left: Operand,
      right: Operand,
      instructionModifiers: Col[Modifier]
  ): Either[LoweringError, ExecutableInstruction] = {
    val modifiers   = instructionModifiers.map(_.name)
    val instruction = ("setp" +: modifiers).mkString(".")
    val comparison  = modifiers.collectFirst(Function.unlift {
      case "num" => Some(Comparison.Ordered)
      case "nan" => Some(Comparison.Unordered)
      case "eq"  => Some(Comparison.Equal)
      case "equ" => Some(Comparison.EqualUnordered)
      case "ne"  => Some(Comparison.NotEqual)
      case "neu" => Some(Comparison.NotEqualUnordered)
      case "lt"  => Some(Comparison.LessThan)
      case "ltu" => Some(Comparison.LessThanUnordered)
      case "le"  => Some(Comparison.LessThanOrEqual)
      case "leu" => Some(Comparison.LessThanOrEqualUnordered)
      case "gt"  => Some(Comparison.GreaterThan)
      case "gtu" => Some(Comparison.GreaterThanUnordered)
      case "ge"  => Some(Comparison.GreaterThanOrEqual)
      case "geu" => Some(Comparison.GreaterThanOrEqualUnordered)
      case _     => None
    })
    val kind = modifiers.collectFirst {
      case "u8"   => 'u' -> 8
      case "s8"   => 's' -> 8
      case "u16"  => 'u' -> 16
      case "s16"  => 's' -> 16
      case "u32"  => 'u' -> 32
      case "s32"  => 's' -> 32
      case "u64"  => 'u' -> 64
      case "s64"  => 's' -> 64
      case "b8"   => 'u' -> 8
      case "b16"  => 'u' -> 16
      case "b32"  => 'u' -> 32
      case "b64"  => 'u' -> 64
      case "f16"  => 'f' -> 16
      case "f32"  => 'f' -> 32
      case "f64"  => 'f' -> 64
      case "bf16" => 'b' -> 16
      case "pred" => 'p' -> 1
    }
    for {
      predicate       <- comparison.toRight(LoweringError.Invalid(s"unsupported setp comparison: $instruction"))
      kindAndWidth    <- kind.toRight(LoweringError.Invalid(s"unsupported setp type: $instruction"))
      destinationName <- predicateRegisterName(destination)
      lowered         <-
        if (kindAndWidth._1 == 'p')
          for {
            leftName  <- predicateRegisterName(left)
            rightName <- predicateRegisterName(right)
          } yield ExecutableInstruction.PredicateCompare(destinationName, predicate, leftName, rightName)
        else
          for {
            leftValue  <- value(left)
            rightValue <- value(right)
          } yield ExecutableInstruction.SetPredicate(
            destinationName,
            predicate,
            kindAndWidth._1,
            leftValue,
            rightValue,
            kindAndWidth._2
          )
    } yield lowered
  }

  private def lowerPredicate(value: Option[Predication]): Either[LoweringError, Option[ExecutionPredicate]] =
    value match {
      case None                                => Right(None)
      case Some(Predication(negated, operand)) =>
        predicateRegisterName(operand).map(name => Some(ExecutionPredicate(name, negated)))
    }

  private def predicateRegisterName(operand: Operand): Either[LoweringError, String] = operand match {
    case Operand.Register(name, None) => Right(name)
    case Operand.Symbol(name)         => Right(name)
    case other                        => invalid(s"expected predicate register, found ${Printer.render(other)}")
  }

  private def registerName(operand: Operand): Either[LoweringError, String] = operand match {
    case Operand.Register(name, None) => Right(name)
    case Operand.Symbol(name)         => Right(name)
    case other                        => invalid(s"expected register, found ${Printer.render(other)}")
  }

  private def valueRegisterName(operand: Operand): Either[LoweringError, String] = operand match {
    case Operand.Register(name, None)                     => Right(name)
    case Operand.Symbol("datum")                          => Right("datum")
    case Operand.Symbol(name) if isBareRegisterName(name) => Right(name)
    case other => invalid(s"expected register, found ${Printer.render(other)}")
  }

  private def valueDestination(operand: Operand): Either[LoweringError, (String, Option[String])] = operand match {
    case Operand.WithPredicate(base, predicate) =>
      for {
        destinationName <- valueRegisterName(base)
        predicateName   <- predicateRegisterName(predicate)
      } yield destinationName -> Some(predicateName)
    case other => valueRegisterName(other).map(_ -> None)
  }

  private def registerNames(operand: Operand): Either[LoweringError, Col[String]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[LoweringError, Col[String]]](Right(Col.empty)) { (current, item) =>
        for {
          names <- current
          name  <- registerName(item)
        } yield names :+ name
      }
    case other => registerName(other).map(Col(_))
  }

  private def vectorValues(operand: Operand): Either[LoweringError, Col[Value]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[LoweringError, Col[Value]]](Right(Col.empty)) { (current, item) =>
        for {
          values <- current
          value  <- value(item)
        } yield values :+ value
      }
    case other => value(other).map(Col(_))
  }

  private def splitRegisterNames(operand: Operand): Either[LoweringError, Col[String]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[LoweringError, Col[String]]](Right(Col.empty)) { (current, item) =>
        for {
          names <- current
          name  <- item match {
            case Operand.Symbol("_") => Right("_")
            case other               => registerName(other)
          }
        } yield names :+ name
      }
    case other => registerName(other).map(Col(_))
  }

  private def requirePair[A](names: Col[A], detail: String): Either[LoweringError, Unit] =
    if (names.size == 2) Right(()) else invalid(s"$detail must contain two registers")

  private def predicateBinary(
      operation: PredicateOperation,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- predicateRegisterName(destination)
      leftName        <- predicateRegisterName(left)
      rightName       <- predicateRegisterName(right)
    } yield ExecutableInstruction.PredicateBinary(operation, destinationName, leftName, rightName)

  private def callParameters(operand: Option[Operand]): Either[LoweringError, Col[String]] = operand match {
    case None        => Right(Col.empty)
    case Some(value) => callParameters(value)
  }

  private def callParameters(operand: Operand): Either[LoweringError, Col[String]] = operand match {
    case Operand.Symbol(name)         => Right(Col(name))
    case Operand.Parenthesized(items) =>
      items.foldLeft[Either[LoweringError, Col[String]]](Right(Col.empty)) { (current, item) =>
        for {
          names <- current
          value <- callParameters(item)
        } yield names ++ value
      }
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[LoweringError, Col[String]]](Right(Col.empty)) { (current, item) =>
        for {
          names <- current
          value <- callParameters(item)
        } yield names ++ value
      }
    case other => invalid(s"expected call parameter names, found ${Printer.render(other)}")
  }

  private def functionValue(
      operand: Operand,
      functionNames: Set[String],
      promoteUnknown: Boolean
  ): Either[LoweringError, Value] = operand match {
    case Operand.Symbol(name) if promoteUnknown || functionNames.contains(name) => Right(Value.Function(name))
    case Operand.Symbol(name)                                                   => Right(Value.Symbol(name))
    case other                                                                  => value(other)
  }

  private def sharedSymbols(body: Col[Statement]): Map[String, Long] = {
    val declarations = flatten(body).collect {
      case Statement.Directive(path, arguments, _) if path == Col("shared") =>
        arguments.iterator.flatMap(argument =>
          argument.operand match {
            case Operand.Symbol(name)                   => Iterator.single(name)
            case Operand.Index(Operand.Symbol(name), _) => Iterator.single(name)
            case _                                      => Iterator.empty
          }
        )
    }.flatten.distinct
    declarations.zipWithIndex.map { case (name, index) => name -> (0x10000000L + index.toLong * 0x100000L) }.toMap
  }

  private def addressValue(operand: Operand): Either[LoweringError, Value] = operand match {
    case Operand.Address(Col(single)) => valueWithOffset(single)
    case other                        => invalid(s"expected address operand, found ${Printer.render(other)}")
  }

  private def parameterAddress(operand: Operand): Either[LoweringError, (String, Int)] = operand match {
    case Operand.Address(Col(Operand.Symbol(name)))                      => Right(name -> 0)
    case Operand.Address(Col(Operand.Add(Operand.Symbol(name), offset))) =>
      immediate(offset).flatMap(value =>
        if (value >= 0L && value <= Int.MaxValue.toLong) Right(name -> value.toInt)
        else invalid(s"parameter offset is outside Int range: $value")
      )
    case other => invalid(s"expected parameter address, found ${Printer.render(other)}")
  }

  private def isDirectParameterAddress(operand: Operand): Boolean = operand match {
    case Operand.Address(Col(Operand.Symbol(_)))                 => true
    case Operand.Address(Col(Operand.Add(Operand.Symbol(_), _))) => true
    case _                                                       => false
  }

  private def valueWithOffset(operand: Operand): Either[LoweringError, Value] = operand match {
    case Operand.Add(base, offset) =>
      for {
        baseValue   <- value(base)
        offsetValue <- immediate(offset)
      } yield Value.Address(baseValue, offsetValue)
    case Operand.Subtract(base, offset) =>
      for {
        baseValue   <- value(base)
        offsetValue <- immediate(offset)
      } yield Value.Address(baseValue, -offsetValue)
    case Operand.Symbol(name) => Right(Value.Symbol(name))
    case other                => value(other)
  }

  private def integerWidth(modifiers: Col[Modifier]): Either[LoweringError, Int] =
    modifiers.reverseIterator.collectFirst {
      case Modifier(name) if isScalarType(name) => scalarTypeWidth(name)
    }.filter(Set(8, 16, 32, 64).contains).toRight(
      LoweringError.Invalid(
        s"expected an 8, 16, 32, or 64-bit integer type in ${modifiers.map(_.name).mkString(".")}"
      )
    )

  private def floatWidth(modifiers: Col[Modifier]): Either[LoweringError, Int] =
    modifiers.reverseIterator.collectFirst {
      case Modifier(name) if name == "f16" || name == "f32" || name == "f64" => name.drop(1).toInt
    }.toRight(LoweringError.Invalid(s"expected an f16, f32, or f64 type in ${modifiers.map(_.name).mkString(".")}"))

  private def scalarWidth(modifiers: Col[Modifier]): Either[LoweringError, Int] =
    modifiers.reverseIterator.collectFirst {
      case Modifier(name) if isScalarType(name) => scalarTypeWidth(name)
    }.filter(Set(8, 16, 32, 64).contains).toRight(
      LoweringError.Invalid(s"expected an 8, 16, 32, or 64-bit scalar type in ${modifiers.map(_.name).mkString(".")}")
    )

  private def isScalarType(name: String): Boolean =
    name == "bf16" || (name.length > 1 && "usbf".contains(name.head) && name.tail.forall(_.isDigit))

  private def scalarTypeWidth(name: String): Int = if (name == "bf16") 16 else name.drop(1).toInt

  private def value(operand: Operand): Either[LoweringError, Value] = operand match {
    case Operand.Register("%tid.x", None)                               => Right(Value.ThreadId(Axis.X))
    case Operand.Register("%tid.y", None)                               => Right(Value.ThreadId(Axis.Y))
    case Operand.Register("%tid.z", None)                               => Right(Value.ThreadId(Axis.Z))
    case Operand.Register("%ctaid.x", None)                             => Right(Value.BlockId(Axis.X))
    case Operand.Register("%ctaid.y", None)                             => Right(Value.BlockId(Axis.Y))
    case Operand.Register("%ctaid.z", None)                             => Right(Value.BlockId(Axis.Z))
    case Operand.Register("%ntid.x", None)                              => Right(Value.BlockDim(Axis.X))
    case Operand.Register("%ntid.y", None)                              => Right(Value.BlockDim(Axis.Y))
    case Operand.Register("%ntid.z", None)                              => Right(Value.BlockDim(Axis.Z))
    case Operand.Register("%nctaid.x", None)                            => Right(Value.GridDim(Axis.X))
    case Operand.Register("%nctaid.y", None)                            => Right(Value.GridDim(Axis.Y))
    case Operand.Register("%nctaid.z", None)                            => Right(Value.GridDim(Axis.Z))
    case Operand.Register("%laneid", None)                              => Right(Value.LaneId)
    case Operand.Register("%lanemask_ge", None)                         => Right(Value.LaneMaskGreaterEqual)
    case Operand.Register("%lanemask_gt", None)                         => Right(Value.LaneMaskGreater)
    case Operand.Register("%lanemask_le", None)                         => Right(Value.LaneMaskLessEqual)
    case Operand.Register("%lanemask_lt", None)                         => Right(Value.LaneMaskLess)
    case Operand.Register("%lanemask_eq", None)                         => Right(Value.LaneMaskEqual)
    case Operand.Register(name, None) if PredicateFile.index(name) >= 0 =>
      Right(Value.PredicateSlot(PredicateFile.index(name)))
    case Operand.Register(name, None) if RegisterFile.index(name) >= 0 =>
      Right(Value.RegisterSlot(RegisterFile.index(name)))
    case Operand.Register(name, None)                           => Right(Value.Register(name))
    case Operand.Symbol("datum")                                => Right(Value.Register("datum"))
    case Operand.Symbol(name) if PredicateFile.index(name) >= 0 => Right(Value.PredicateSlot(PredicateFile.index(name)))
    case Operand.Symbol(name) if isBareRegisterName(name) && RegisterFile.index(s"%$name") >= 0 =>
      Right(Value.RegisterSlot(RegisterFile.index(s"%$name")))
    case Operand.Symbol(name) if isBareRegisterName(name) => Right(Value.Register(name))
    case Operand.Symbol(name)                             => Right(Value.Symbol(name))
    case other: Operand.Immediate                         => immediate(other).map(Value.Immediate.apply)
    case Operand.Negate(value)                            => immediate(value).map(number => Value.Immediate(-number))
    case other => invalid(s"expected register or immediate, found ${Printer.render(other)}")
  }

  private def immediate(operand: Operand): Either[LoweringError, Long] = operand match {
    case Operand.Negate(value)  => immediate(value).map(number => -number)
    case Operand.Immediate(raw) =>
      val signless       = raw.dropWhile(character => character == '+' || character == '-')
      val sign           = if (raw.startsWith("-")) -1 else 1
      val suffixStripped = signless.reverse.dropWhile(character => "uUlL".contains(character)).reverse
      val parsed         =
        try
          Some(
            if (suffixStripped.startsWith("0x") || suffixStripped.startsWith("0X"))
              BigInt(suffixStripped.drop(2), 16) * sign
            else if (suffixStripped.startsWith("0b") || suffixStripped.startsWith("0B"))
              BigInt(suffixStripped.drop(2), 2) * sign
            else if (suffixStripped.startsWith("0f") || suffixStripped.startsWith("0F"))
              BigInt(suffixStripped.drop(2), 16) * sign
            else if (suffixStripped.startsWith("0d") || suffixStripped.startsWith("0D"))
              BigInt(suffixStripped.drop(2), 16) * sign
            else BigInt(suffixStripped) * sign
          )
        catch {
          case _: NumberFormatException => None
        }
      parsed match {
        case Some(number) if number.isValidLong                        => Right(number.longValue)
        case Some(number) if number >= 0 && number < (BigInt(1) << 64) => Right(number.longValue)
        case Some(_) => invalid(s"integer immediate is outside 64-bit range: $raw")
        case None    => invalid(s"expected integer immediate, found $raw")
      }
    case other => invalid(s"expected integer immediate, found ${Printer.render(other)}")
  }

  private def isBareRegisterName(name: String): Boolean =
    name.matches("(?:r|rd|p)\\d*|lo|hi")

  private def isSignedScalarOpcode(opcode: String): Boolean =
    opcode.endsWith(".s8") || opcode.endsWith(".s16") || opcode.endsWith(".s32") || opcode.endsWith(".s64")

  private def invalid[A](detail: String): Either[LoweringError, A] = Left(LoweringError.Invalid(detail))
}

private enum StateWrite extends Write {
  case Register(lane: Int, name: String, value: Long)
  case Carry(lane: Int, value: Boolean)
  case FunctionRegister(lane: Int, name: String, value: String)
  case Predicate(lane: Int, name: String, value: Boolean)
  case Parameter(lane: Int, name: String, value: Long)
  case ParameterBytes(lane: Int, name: String, offset: Int, width: Int, value: Long)
  case FunctionParameter(lane: Int, name: String, value: String)
  case ParameterBuffer(address: Long, width: Int, value: Long)
  case Shared(lane: Int, address: Long, width: Int, value: Long)
  case Local(lane: Int, address: Long, width: Int, value: Long)
  case Memory(lane: Int, address: GuestAddress, width: Int, value: Long)
  case AtomicSharedAdd(lane: Int, destination: String, address: Long, width: Int, value: Long)
  case AtomicSharedMin(lane: Int, destination: String, address: Long, width: Int, value: Long)
  case AtomicMemoryAdd(lane: Int, destination: String, address: GuestAddress, width: Int, value: Long)
  case AtomicMemoryMin(lane: Int, destination: String, address: GuestAddress, width: Int, value: Long)
  case AtomicMemoryAddFloat(lane: Int, destination: String, address: GuestAddress, width: Int, value: Long)
}

/** Dense storage for the register names emitted by nvcc. PTX names are stable for a kernel, so parsing the small
  * numeric register suffix on every access is cheaper than allocating and hashing a String in a mutable map.
  */
@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:GemstoneRuntimeCollections.mutable-collection",
    "scalafix:GemstoneNoExplicitNew.explicit-new"
  )
)
private final class RegisterFile {
  private val values = new Array[Array[Long]](96)
  private val named  = mutable.Map.empty[String, Long]

  def get(name: String): Long = slot(name) match {
    case Some(index) => getSlot(index)
    case None        => named.getOrElse(name, 0L)
  }

  def getSlot(index: Int): Long = {
    val chunk = values(index >>> 6)
    if (chunk == null) 0L else chunk(index & 63)
  }

  def apply(name: String): Long = get(name)

  def update(name: String, value: Long): Unit = slot(name) match {
    case Some(index) => updateSlot(index, value)
    case None        => named(name) = value
  }

  def updateSlot(index: Int, value: Long): Unit = {
    val chunkIndex = index >>> 6
    var chunk      = values(chunkIndex)
    if (chunk == null) {
      chunk = Array.ofDim[Long](64)
      values(chunkIndex) = chunk
    }
    chunk(index & 63) = value
  }

  private def slot(name: String): Option[Int] = {
    val value = RegisterFile.index(name)
    if (value < 0) None else Some(value)
  }
}

@SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:GemstoneNoExplicitNew.explicit-new"))
private object RegisterFile {
  private val indexCache = new java.util.concurrent.ConcurrentHashMap[String, Integer]()

  def index(name: String): Int = {
    val cached = indexCache.get(name)
    if (cached != null) return cached.intValue
    val canonical        = if (name.startsWith("%")) name else s"%$name"
    val (prefix, offset) =
      if (canonical.startsWith("%rd")) 3 -> 0
      else if (canonical.startsWith("%rs")) 3 -> 1024
      else if (canonical.startsWith("%r")) 2 -> 2048
      else if (canonical.startsWith("%fd")) 3 -> 3072
      else if (canonical.startsWith("%f")) 2 -> 4096
      else if (canonical.startsWith("%p")) 2 -> 5120
      else -1                                -> 0
    val index =
      if (prefix < 0 || canonical.length == prefix || !canonical.substring(prefix).forall(_.isDigit)) -1
      else canonical.substring(prefix).toIntOption.filter(value => value >= 0 && value < 1024).fold(-1)(_ + offset)
    val _ = indexCache.putIfAbsent(name, Integer.valueOf(index))
    index
  }

  def name(index: Int): String =
    if (index >= 0 && index < 1024) s"%rd$index"
    else if (index < 2048) s"%rs${index - 1024}"
    else if (index < 3072) s"%r${index - 2048}"
    else if (index < 4096) s"%fd${index - 3072}"
    else if (index < 5120) s"%f${index - 4096}"
    else if (index < 6144) s"%p${index - 5120}"
    else s"%r$index"
}

@SuppressWarnings(Array("scalafix:GemstoneRuntimeCollections.mutable-collection"))
private final class PredicateFile {
  private val values  = Array.ofDim[Boolean](1024)
  private val defined = Array.ofDim[Boolean](1024)
  private val named   = mutable.Map.empty[String, Boolean]

  def get(name: String): Option[Boolean] = predicateSlot(name) match {
    case Some(index) => if (defined(index)) Some(values(index)) else None
    case None        => named.get(name)
  }

  def getSlot(index: Int): Option[Boolean] = if (defined(index)) Some(values(index)) else None

  def apply(name: String): Boolean = get(name).getOrElse(false)

  def update(name: String, value: Boolean): Unit = predicateSlot(name) match {
    case Some(index) =>
      values(index) = value
      defined(index) = true
    case None => named(name) = value
  }

  def updateSlot(index: Int, value: Boolean): Unit = {
    values(index) = value
    defined(index) = true
  }

  private def predicateSlot(name: String): Option[Int] = {
    val index = PredicateFile.index(name)
    if (index < 0) None else Some(index)
  }
}

private object PredicateFile {
  def index(name: String): Int =
    if (name.startsWith("%p") && name.length > 2 && name.substring(2).forall(_.isDigit))
      name.substring(2).toIntOption.filter(value => value >= 0 && value < 1024).getOrElse(-1)
    else -1
}

@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:GemstoneRuntimeCollections.mutable-collection",
    "scalafix:GemstoneNoExplicitNew.explicit-new"
  )
)
private final class BlockState(val laneCount: Int, initialLive: LaneMask) {
  val registers: Array[RegisterFile]                        = Array.fill(laneCount)(new RegisterFile)
  val functionRegisters: Array[mutable.Map[String, String]] =
    Array.fill(laneCount)(mutable.Map.empty[String, String])
  val predicates: Array[PredicateFile]                       = Array.fill(laneCount)(new PredicateFile)
  val parameters: Array[mutable.Map[String, Long]]           = Array.fill(laneCount)(mutable.Map.empty[String, Long])
  val functionParameters: Array[mutable.Map[String, String]] =
    Array.fill(laneCount)(mutable.Map.empty[String, String])
  val parameterBytes: Array[mutable.Map[String, mutable.Map[Int, Byte]]] =
    Array.fill(laneCount)(mutable.Map.empty[String, mutable.Map[Int, Byte]])
  val carry: Array[Boolean]                            = Array.fill(laneCount)(false)
  val parameterBuffers: mutable.Map[Long, Array[Byte]] = mutable.Map.empty
  val parameterAddresses: mutable.Map[String, Long]    = mutable.Map.empty
  private var nextParameterBuffer                      = 0x700000000L
  private val shared: mutable.Map[Long, Byte]          = mutable.Map.empty
  private val local: Array[mutable.Map[Long, Byte]]    = Array.fill(laneCount)(mutable.Map.empty[Long, Byte])
  var live: LaneMask                                   = initialLive

  def readShared(address: Long, width: Int): Long =
    LittleEndian.decode(byteCount(width))(index => shared.getOrElse(address + index.toLong, 0.toByte))

  def writeShared(address: Long, width: Int, value: Long): Unit =
    LittleEndian.write(value, byteCount(width)) { (index, byte) => shared(address + index.toLong) = byte }

  def readLocal(lane: Int, address: Long, width: Int): Long =
    LittleEndian.decode(byteCount(width))(index => local(lane).getOrElse(address + index.toLong, 0.toByte))

  def writeLocal(lane: Int, address: Long, width: Int, value: Long): Unit =
    LittleEndian.write(value, byteCount(width)) { (index, byte) => local(lane)(address + index.toLong) = byte }

  def writeParameterBytes(lane: Int, name: String, offset: Int, width: Int, value: Long): Unit = {
    val bytes = parameterBytes(lane).getOrElseUpdate(name, mutable.Map.empty)
    LittleEndian.write(value, byteCount(width)) { (index, byte) => bytes(offset + index) = byte }
  }

  def readParameterBytes(lane: Int, name: String, offset: Int, size: Int): Long =
    LittleEndian.decode(size) { index =>
      parameterBytes(lane).get(name).flatMap(_.get(offset + index)).getOrElse(0.toByte)
    }

  def allocateParameterBuffer(size: Int): Long = {
    val base = nextParameterBuffer
    parameterBuffers(base) = Array.ofDim[Byte](Math.max(size, 1))
    nextParameterBuffer = Math.addExact(base, Math.max(size, 1).toLong + 0x100L)
    base
  }

  def bindParameterBuffer(name: String, bytes: Array[Byte]): Unit = {
    val base = allocateParameterBuffer(bytes.length)
    Array.copy(bytes, 0, parameterBuffers(base), 0, bytes.length)
    parameterAddresses(name) = base
  }

  def importParameterBuffers(buffers: Map[Long, Array[Byte]]): Unit =
    buffers.foreach { case (address, bytes) =>
      parameterBuffers(address) = bytes
      nextParameterBuffer = Math.max(
        nextParameterBuffer,
        Math.addExact(address, Math.max(bytes.length, 1).toLong + 0x100L)
      )
    }

  def parameterBufferRange(address: Long, size: Int): Option[(Array[Byte], Int)] =
    parameterBuffers.iterator.collectFirst {
      case (base, bytes)
          if address >= base && address - base >= 0L && address - base <= bytes.length.toLong - size.toLong =>
        bytes -> (address - base).toInt
    }

  def readParameterBuffer(address: Long, size: Int): Option[Long] =
    parameterBufferRange(address, size).map { case (bytes, offset) => LittleEndian.decode(bytes, offset, size) }

  def readParameterBufferBytes(address: Long, size: Int): Option[Array[Byte]] =
    parameterBufferRange(address, size).map { case (bytes, offset) =>
      java.util.Arrays.copyOfRange(bytes, offset, offset + size)
    }
}

private final class ExecutionContext(
    val kernel: Kernel,
    val block: Int,
    val group: Coordinates,
    val geometry: LaunchGeometry,
    val state: BlockState,
    val pc: Pc,
    val active: LaneMask,
    val arguments: Map[String, Argument],
    val memory: AddressMemory,
    val control: ExecutionControl
) {
  def read(lane: Int, value: Value): Either[ExecutionFault, Long] = value match {
    // A PTX register read after a divergent path is unspecified.  Hardware
    // nevertheless carries a value for it, and CUDA reductions commonly use
    // such lanes only as inactive shuffle participants.  Use a deterministic
    // zero for that unspecified value so the active lane's identity is stable.
    case Value.Register(name)       => Right(state.registers(lane).get(name))
    case Value.RegisterSlot(index)  => Right(state.registers(lane).getSlot(index))
    case Value.Predicate(name)      => Right(if (state.predicates(lane).get(name).getOrElse(false)) 1L else 0L)
    case Value.PredicateSlot(index) => Right(if (state.predicates(lane).getSlot(index).getOrElse(false)) 1L else 0L)
    case Value.Function(name)       => Left(ExecutionFault.InvalidState(s"function symbol $name is not numeric"))
    case Value.Symbol(name)         =>
      state.parameterAddresses.get(
        name
      ).orElse(kernel.sharedSymbols.get(name)).orElse(kernel.globalSymbols.get(name)).toRight(
        ExecutionFault.InvalidState(s"unknown shared symbol $name")
      )
    case Value.Immediate(immediate)  => Right(immediate)
    case Value.Address(base, offset) => read(lane, base).map(_ + offset)
    case Value.LaneId                => Right((lane & 31).toLong)
    case Value.ActiveMask            => Right(active.warpBits(lane))
    case Value.LaneMaskGreaterEqual  => Right((0xffffffffL << (lane & 31)) & 0xffffffffL)
    case Value.LaneMaskGreater       => Right((0xffffffffL << ((lane & 31) + 1)) & 0xffffffffL)
    case Value.LaneMaskLessEqual     => Right((1L << ((lane & 31) + 1)) - 1L)
    case Value.LaneMaskLess          => Right((1L << (lane & 31)) - 1L)
    case Value.LaneMaskEqual         => Right(1L << (lane & 31))
    case Value.ThreadId(axis)        => Right(axis.select(geometry.localCoordinates(lane)).toLong)
    case Value.BlockId(axis)         => Right(axis.select(group).toLong)
    case Value.BlockDim(axis)        => Right(axis.select(geometry.groupSize).toLong)
    case Value.GridDim(axis)         => Right(axis.select(geometry.groupCount).toLong)
  }

  def functionName(lane: Int, value: Value): Either[ExecutionFault, String] = value match {
    case Value.Function(name) => Right(name)
    case Value.Register(name) => state.functionRegisters(lane).get(name).toRight(
        ExecutionFault.UndefinedRegister(name, pc, lane)
      )
    case Value.RegisterSlot(index) =>
      val name = RegisterFile.name(index)
      state.functionRegisters(lane).get(name).toRight(
        ExecutionFault.UndefinedRegister(name, pc, lane)
      )
    case other => Left(ExecutionFault.InvalidState(s"expected a function value, found $other"))
  }
}

@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection",
    "scalafix:GemstoneNoExplicitNew.explicit-new"
  )
)
private object Semantics {
  extension (instruction: ExecutableInstruction) {
    def execute(context: ExecutionContext, effects: Effects[StateWrite]): Either[ExecutionFault, Step] =
      if (executeFast(instruction, context)) Right(Step.Advance)
      else instruction match {
        case ExecutableInstruction.LoadParameter(destinations, parameter, offset, elementWidth, signed) =>
          forEveryLane(context) { lane =>
            val elementBytes = elementWidth / 8
            context.state.parameters(lane).get(parameter) match {
              case Some(value) if offset == 0 && destinations.size == 1 =>
                effects.stage(StateWrite.Register(
                  lane,
                  destinations.head,
                  loadBits(value, elementWidth, signed)
                ))
                Right(())
              case Some(_) =>
                Left(ExecutionFault.InvalidState(s"unsupported local parameter load: $parameter+$offset"))
              case None =>
                context.arguments.get(parameter).toRight(
                  ExecutionFault.InvalidState(s"missing kernel argument $parameter")
                ).flatMap { argument =>
                  if (offset < 0 || offset > argument.bytes.length - elementBytes * destinations.size)
                    Left(ExecutionFault.InvalidState(
                      s"parameter $parameter load at $offset exceeds ${argument.bytes.length} bytes"
                    ))
                  else {
                    destinations.zipWithIndex.foreach { case (destination, index) =>
                      val value = LittleEndian.decode(argument.bytes, offset + index * elementBytes, elementBytes)
                      effects.stage(StateWrite.Register(lane, destination, loadBits(value, elementWidth, signed)))
                    }
                    Right(())
                  }
                }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadParameterIndirect(destination, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              context.state.readParameterBuffer(rawAddress, byteCount(width)) match {
                case Some(loaded) =>
                  Right(effects.stage(StateWrite.Register(lane, destination, loadBits(loaded, width, signed))))
                case None if isSharedAddress(rawAddress) =>
                  Right(effects.stage(StateWrite.Register(
                    lane,
                    destination,
                    loadBits(context.state.readShared(rawAddress, width), width, signed)
                  )))
                case None if isLocalAddress(rawAddress) =>
                  Right(effects.stage(StateWrite.Register(
                    lane,
                    destination,
                    loadBits(context.state.readLocal(lane, rawAddress, width), width, signed)
                  )))
                case None =>
                  context.memory
                    .readLittleEndian(GuestAddress(rawAddress), byteCount(width))
                    .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                    .map(loaded =>
                      effects.stage(StateWrite.Register(
                        lane,
                        destination,
                        loadBits(loaded, width, signed)
                      ))
                    )
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadParameterIndirectVector(destinations, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              destinations.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (destination, index)) =>
                  result.flatMap { _ =>
                    val elementAddress = rawAddress + index.toLong * byteCount(width).toLong
                    context.state.readParameterBuffer(elementAddress, byteCount(width)) match {
                      case Some(loaded) =>
                        Right(effects.stage(StateWrite.Register(lane, destination, loadBits(loaded, width, signed))))
                      case None if isSharedAddress(elementAddress) =>
                        Right(effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(context.state.readShared(elementAddress, width), width, signed)
                        )))
                      case None if isLocalAddress(elementAddress) =>
                        Right(effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(context.state.readLocal(lane, elementAddress, width), width, signed)
                        )))
                      case None =>
                        context.memory
                          .readLittleEndian(GuestAddress(elementAddress), byteCount(width))
                          .leftMap(error =>
                            ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}")
                          )
                          .map(loaded =>
                            effects.stage(StateWrite.Register(
                              lane,
                              destination,
                              loadBits(loaded, width, signed)
                            ))
                          )
                    }
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreParameter(parameter, offset, source, elementWidth) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              val stored = truncate(value, elementWidth)
              effects.stage(StateWrite.ParameterBytes(lane, parameter, offset, elementWidth, stored))
              if (offset == 0)
                effects.stage(StateWrite.Parameter(lane, parameter, stored))
              source match {
                case Value.Register(name) if context.state.functionRegisters(lane).contains(name) =>
                  effects.stage(StateWrite.FunctionParameter(
                    lane,
                    parameter,
                    context.state.functionRegisters(lane)(name)
                  ))
                case Value.RegisterSlot(index) =>
                  val name = RegisterFile.name(index)
                  context.state.functionRegisters(lane).get(name).foreach(function =>
                    effects.stage(StateWrite.FunctionParameter(lane, parameter, function))
                  )
                case _ => ()
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Move(destination, source, width) =>
          source match {
            case Value.Function(name) =>
              forEveryLane(context) { lane =>
                effects.stage(StateWrite.FunctionRegister(lane, destination, name))
                Right(())
              }.map(_ => Step.Advance)
            case _ =>
              forEveryLane(context) { lane =>
                context.read(lane, source).map(value =>
                  effects.stage(StateWrite.Register(lane, destination, truncate(value, width)))
                )
              }.map(_ => Step.Advance)
          }
        case ExecutableInstruction.MovePredicate(destination, source) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map(value =>
              effects.stage(StateWrite.Predicate(lane, destination, value != 0L))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.MoveSplit(destinations, source, partWidth) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              if (destinations(0) != "_")
                effects.stage(StateWrite.Register(lane, destinations(0), truncate(value, partWidth)))
              if (destinations(1) != "_")
                effects.stage(StateWrite.Register(lane, destinations(1), truncate(value >>> partWidth, partWidth)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.MoveCombine(destination, sources, partWidth) =>
          forEveryLane(context) { lane =>
            for {
              low  <- context.read(lane, sources(0))
              high <- context.read(lane, sources(1))
            } yield effects.stage(StateWrite.Register(
              lane,
              destination,
              truncate((low & truncate(-1L, partWidth)) | (truncate(high, partWidth) << partWidth), partWidth * 2)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.IntegerUnary(operation, destination, source, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              val result = operation match {
                case IntegerUnaryOperation.Negate          => -value
                case IntegerUnaryOperation.Absolute        => Math.abs(signExtend(truncate(value, width), width))
                case IntegerUnaryOperation.BitwiseNot      => ~value
                case IntegerUnaryOperation.PopulationCount =>
                  if (width == 32) Integer.bitCount((value & 0xffffffffL).toInt).toLong
                  else java.lang.Long.bitCount(value).toLong
                case IntegerUnaryOperation.LeadingZeroCount =>
                  if (width == 32) Integer.numberOfLeadingZeros((value & 0xffffffffL).toInt).toLong
                  else java.lang.Long.numberOfLeadingZeros(value).toLong
                case IntegerUnaryOperation.BitReverse =>
                  reverseBits(value, width)
              }
              effects.stage(StateWrite.Register(lane, destination, truncate(result, width)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.BitFind(destination, source, width, signed, shiftAmount) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              effects.stage(StateWrite.Register(
                lane,
                destination,
                bitFind(value, width, signed, shiftAmount)
              ))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.DotProduct4(destination, left, right, addend, leftSigned, rightSigned) =>
          forEveryLane(context) { lane =>
            for {
              leftValue   <- context.read(lane, left)
              rightValue  <- context.read(lane, right)
              addendValue <- context.read(lane, addend)
            } yield effects.stage(StateWrite.Register(
              lane,
              destination,
              dotProduct4(leftValue, rightValue, addendValue, leftSigned, rightSigned)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.IntegerAddWithCarry(destination, left, right, width, carryIn, carryOut) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield {
              val (result, nextCarry) =
                integerAddWithCarry(leftValue, rightValue, width, carryIn, context.state.carry(lane))
              effects.stage(StateWrite.Register(lane, destination, result))
              if (carryOut) effects.stage(StateWrite.Carry(lane, nextCarry))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.IntegerSubWithBorrow(destination, left, right, width, borrowIn, borrowOut) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield {
              val (result, nextBorrow) =
                integerSubWithBorrow(leftValue, rightValue, width, borrowIn, context.state.carry(lane))
              effects.stage(StateWrite.Register(lane, destination, result))
              if (borrowOut) effects.stage(StateWrite.Carry(lane, nextBorrow))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.IntegerBinary(operation, destination, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield {
              val result = operation match {
                case IntegerOperation.Add                    => leftValue + rightValue
                case IntegerOperation.Subtract               => leftValue - rightValue
                case IntegerOperation.BitwiseOr              => leftValue | rightValue
                case IntegerOperation.BitwiseAnd             => leftValue & rightValue
                case IntegerOperation.BitwiseXor             => leftValue ^ rightValue
                case IntegerOperation.MultiplyLow            => leftValue * rightValue
                case IntegerOperation.MultiplyHigh(signed)   => multiplyHigh(leftValue, rightValue, width, signed)
                case IntegerOperation.Divide(signed)         => divideInteger(leftValue, rightValue, width, signed)
                case IntegerOperation.Remainder(signed)      => remainderInteger(leftValue, rightValue, width, signed)
                case IntegerOperation.MultiplyWideUnsigned16 =>
                  (leftValue & 0xffffL) * (rightValue & 0xffffL)
                case IntegerOperation.MultiplyWideUnsigned32 =>
                  low32(leftValue) * low32(rightValue)
                case IntegerOperation.MultiplyWideSigned32 => leftValue.toInt.toLong * rightValue.toInt.toLong
                case IntegerOperation.Minimum(signed)      =>
                  if (signed) Math.min(signExtend(leftValue, width), signExtend(rightValue, width))
                  else if (width == 64) {
                    if (java.lang.Long.compareUnsigned(leftValue, rightValue) <= 0) leftValue else rightValue
                  } else Math.min(truncate(leftValue, width), truncate(rightValue, width))
                case IntegerOperation.Maximum(signed) =>
                  if (signed) Math.max(signExtend(leftValue, width), signExtend(rightValue, width))
                  else if (width == 64) {
                    if (java.lang.Long.compareUnsigned(leftValue, rightValue) >= 0) leftValue else rightValue
                  } else Math.max(truncate(leftValue, width), truncate(rightValue, width))
                case IntegerOperation.ShiftLeft         => leftValue << (rightValue & (width - 1).toLong).toInt
                case IntegerOperation.ShiftRightLogical =>
                  if (width == 32) ((leftValue & 0xffffffffL) >>> (rightValue & 31L).toInt).toLong
                  else (leftValue >>> (rightValue & 63L).toInt).toLong
                case IntegerOperation.ShiftRightArithmetic =>
                  if (width == 32) (leftValue.toInt >> (rightValue & 31L).toInt).toLong
                  else (leftValue >> (rightValue & 63L).toInt).toLong
              }
              effects.stage(StateWrite.Register(lane, destination, truncate(result, width)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Convert(destination, source, sourceType, destinationType, rounding) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              effects.stage(StateWrite.Register(
                lane,
                destination,
                convert(value, sourceType, destinationType, rounding)
              ))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.MultiplyAdd(destination, left, right, addend, operation) =>
          forEveryLane(context) { lane =>
            for {
              leftValue   <- context.read(lane, left)
              rightValue  <- context.read(lane, right)
              addendValue <- context.read(lane, addend)
            } yield {
              val (result, carry) =
                integerMultiplyAdd(leftValue, rightValue, addendValue, operation, context.state.carry(lane))
              effects.stage(StateWrite.Register(lane, destination, result))
              operation match {
                case IntegerMultiplyAddOperation.Low(_, _, _, true) | IntegerMultiplyAddOperation.High(_, _, _, true) =>
                  effects.stage(StateWrite.Carry(lane, carry))
                case _ => ()
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FloatBinary(operation, destination, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftBits  <- context.read(lane, left)
              rightBits <- context.read(lane, right)
            } yield {
              val result =
                if (width == 16) {
                  val leftValue  = IEEE754.binary16ToFloat(leftBits)
                  val rightValue = IEEE754.binary16ToFloat(rightBits)
                  val value      = operation match {
                    case FloatOperation.Add      => leftValue + rightValue
                    case FloatOperation.Subtract => leftValue - rightValue
                    case FloatOperation.Multiply => leftValue * rightValue
                    case FloatOperation.Divide   => leftValue / rightValue
                    case FloatOperation.Minimum  => Math.min(leftValue, rightValue)
                    case FloatOperation.Maximum  => Math.max(leftValue, rightValue)
                    case FloatOperation.CopySign => Math.copySign(rightValue, leftValue)
                  }
                  IEEE754.floatToBinary16(value)
                } else if (width == 32) {
                  val leftValue  = IEEE754.binary32ToFloat(leftBits)
                  val rightValue = IEEE754.binary32ToFloat(rightBits)
                  val value      = operation match {
                    case FloatOperation.Add      => leftValue + rightValue
                    case FloatOperation.Subtract => leftValue - rightValue
                    case FloatOperation.Multiply => leftValue * rightValue
                    case FloatOperation.Divide   => leftValue / rightValue
                    case FloatOperation.Minimum  => Math.min(leftValue, rightValue)
                    case FloatOperation.Maximum  => Math.max(leftValue, rightValue)
                    // PTX's operand order is copysign(a, b) = sign(a) * abs(b).
                    // This is the reverse of java.lang.Math.copySign's argument order.
                    case FloatOperation.CopySign => Math.copySign(rightValue, leftValue)
                  }
                  IEEE754.floatToBinary32(value)
                } else {
                  val leftValue  = IEEE754.binary64ToDouble(leftBits)
                  val rightValue = IEEE754.binary64ToDouble(rightBits)
                  val value      = operation match {
                    case FloatOperation.Add      => leftValue + rightValue
                    case FloatOperation.Subtract => leftValue - rightValue
                    case FloatOperation.Multiply => leftValue * rightValue
                    case FloatOperation.Divide   => leftValue / rightValue
                    case FloatOperation.Minimum  => Math.min(leftValue, rightValue)
                    case FloatOperation.Maximum  => Math.max(leftValue, rightValue)
                    case FloatOperation.CopySign => Math.copySign(rightValue, leftValue)
                  }
                  IEEE754.doubleToBinary64(value)
                }
              effects.stage(StateWrite.Register(lane, destination, result))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FloatBinaryPacked(operation, destination, left, right, bf16) =>
          forEveryLane(context) { lane =>
            for {
              leftBits  <- context.read(lane, left)
              rightBits <- context.read(lane, right)
            } yield effects.stage(
              StateWrite.Register(lane, destination, packedBinaryBits(operation, leftBits, rightBits, bf16))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FloatUnary(operation, destination, source, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              effects.stage(StateWrite.Register(lane, destination, floatUnaryBits(operation, value, width)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FusedMultiplyAdd(destination, left, right, addend, width) =>
          forEveryLane(context) { lane =>
            for {
              leftBits   <- context.read(lane, left)
              rightBits  <- context.read(lane, right)
              addendBits <- context.read(lane, addend)
            } yield {
              val result = if (width == 16) {
                val value = Math.fma(
                  IEEE754.binary16ToFloat(leftBits),
                  IEEE754.binary16ToFloat(rightBits),
                  IEEE754.binary16ToFloat(addendBits)
                )
                IEEE754.floatToBinary16(value)
              } else if (width == 32) {
                val value = Math.fma(
                  IEEE754.binary32ToFloat(leftBits),
                  IEEE754.binary32ToFloat(rightBits),
                  IEEE754.binary32ToFloat(addendBits)
                )
                IEEE754.floatToBinary32(value)
              } else {
                val value = Math.fma(
                  IEEE754.binary64ToDouble(leftBits),
                  IEEE754.binary64ToDouble(rightBits),
                  IEEE754.binary64ToDouble(addendBits)
                )
                IEEE754.doubleToBinary64(value)
              }
              effects.stage(StateWrite.Register(lane, destination, result))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FusedMultiplyAddPacked(destination, left, right, addend, bf16) =>
          forEveryLane(context) { lane =>
            for {
              leftBits   <- context.read(lane, left)
              rightBits  <- context.read(lane, right)
              addendBits <- context.read(lane, addend)
            } yield effects.stage(
              StateWrite.Register(lane, destination, packedFmaBits(leftBits, rightBits, addendBits, bf16))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FusedMultiplyAddBf16(destination, left, right, addend) =>
          forEveryLane(context) { lane =>
            for {
              leftBits   <- context.read(lane, left)
              rightBits  <- context.read(lane, right)
              addendBits <- context.read(lane, addend)
            } yield effects.stage(
              StateWrite.Register(lane, destination, bf16FmaBits(leftBits, rightBits, addendBits))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.ShuffleDown(destination, validity, source, delta, clamp, _, width) =>
          forEveryLane(context) { lane =>
            for {
              deltaValue <- context.read(lane, delta)
              clampValue <- context.read(lane, clamp)
              distance   = (deltaValue & 31L).toInt
              clampLane  = shuffleClamp(clampValue)
              sourceLane = lane + distance
              laneInWarp = lane & 31
              valid      = laneInWarp + distance <= clampLane && sourceLane < context.state.laneCount
              targetLane = if (valid) sourceLane else lane
              value <- if (targetLane < context.state.laneCount)
                context.read(targetLane, source)
              else Right(0L)
            } yield {
              effects.stage(StateWrite.Register(lane, destination, truncate(value, width)))
              validity.foreach(name => effects.stage(StateWrite.Predicate(lane, name, valid)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.ShuffleUp(destination, validity, source, delta, clamp, _, width) =>
          forEveryLane(context) { lane =>
            for {
              deltaValue <- context.read(lane, delta)
              clampValue <- context.read(lane, clamp)
              distance   = (deltaValue & 31L).toInt
              clampLane  = shuffleClamp(clampValue)
              laneInWarp = lane & 31
              valid      = laneInWarp >= distance && laneInWarp - distance >= clampLane
              targetLane = if (valid) lane - distance else lane
              value <- if (targetLane < context.state.laneCount)
                context.read(targetLane, source)
              else Right(0L)
            } yield {
              effects.stage(StateWrite.Register(lane, destination, truncate(value, width)))
              validity.foreach(name => effects.stage(StateWrite.Predicate(lane, name, valid)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.ShuffleIndex(destination, validity, source, index, clamp, _, width) =>
          forEveryLane(context) { lane =>
            for {
              indexValue <- context.read(lane, index)
              clampValue <- context.read(lane, clamp)
              laneInWarp   = lane & 31
              requested    = (indexValue & 31L).toInt
              targetInWarp = Math.min(requested, shuffleClamp(clampValue))
              targetLane   = (lane - laneInWarp) + targetInWarp
              valid        = requested <= shuffleClamp(clampValue) && targetLane < context.state.laneCount
              value <- context.read(if (valid) targetLane else lane, source)
            } yield {
              effects.stage(StateWrite.Register(lane, destination, truncate(value, width)))
              validity.foreach(name => effects.stage(StateWrite.Predicate(lane, name, valid)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.ShuffleBfly(destination, validity, source, index, clamp, _, width) =>
          forEveryLane(context) { lane =>
            for {
              indexValue <- context.read(lane, index)
              clampValue <- context.read(lane, clamp)
              laneInWarp = lane & 31
              bflyLane   = laneInWarp ^ (indexValue & 31L).toInt
              segmask    = ((clampValue >>> 8) & 31L).toInt
              clampLane  = (clampValue & 31L).toInt
              maxLane    = (laneInWarp & segmask) | (clampLane & ~segmask)
              valid      = bflyLane <= maxLane
              targetLane = (lane - laneInWarp) + bflyLane
              value <- context.read(if (valid && targetLane < context.state.laneCount) targetLane else lane, source)
            } yield {
              effects.stage(StateWrite.Register(lane, destination, truncate(value, width)))
              validity.foreach(name => effects.stage(StateWrite.Predicate(lane, name, valid)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.VoteBallot(destination, predicate, memberMask) =>
          forEveryLane(context) { lane =>
            context.read(lane, memberMask).flatMap { maskValue =>
              val warpBase = lane & ~31
              (0 until 32).foldLeft[Either[ExecutionFault, Long]](Right(0L)) {
                case (result, laneInWarp) =>
                  val candidate = warpBase + laneInWarp
                  if (
                    (maskValue & (1L << laneInWarp)) == 0L || !context.active.contains(candidate) ||
                    candidate >= context.state.laneCount
                  ) result
                  else
                    result.flatMap { ballot =>
                      context.state.predicates(candidate).get(predicate).toRight(
                        ExecutionFault.UndefinedRegister(predicate, context.pc, candidate)
                      ).map { selected =>
                        if (selected) ballot | (1L << laneInWarp) else ballot
                      }
                    }
              }.map(ballot => effects.stage(StateWrite.Register(lane, destination, truncate(ballot, 32))))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.VotePredicate(destination, predicate, memberMask, operation) =>
          forEveryLane(context) { lane =>
            context.read(lane, memberMask).flatMap { maskValue =>
              val warpBase = lane & ~31
              (0 until 32).foldLeft[Either[ExecutionFault, Col[Boolean]]](Right(Col.empty)) {
                case (result, laneInWarp) =>
                  val candidate = warpBase + laneInWarp
                  if (
                    (maskValue & (1L << laneInWarp)) == 0L || !context.active.contains(candidate) ||
                    candidate >= context.state.laneCount
                  ) result
                  else
                    result.flatMap { values =>
                      context.state.predicates(candidate).get(predicate).toRight(
                        ExecutionFault.UndefinedRegister(predicate, context.pc, candidate)
                      ).map(values :+ _)
                    }
              }.map { values =>
                val selected = operation match {
                  case VoteOperation.All     => values.nonEmpty && values.forall(identity)
                  case VoteOperation.Any     => values.exists(identity)
                  case VoteOperation.Uniform => values.distinct.size <= 1
                }
                effects.stage(StateWrite.Predicate(lane, destination, selected))
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.BitFieldExtract(destination, source, start, widthValue, width, signed) =>
          forEveryLane(context) { lane =>
            for {
              sourceValue <- context.read(lane, source)
              startValue  <- context.read(lane, start)
              countValue  <- context.read(lane, widthValue)
            } yield {
              val result = extractBits(sourceValue, startValue, countValue, width, signed)
              effects.stage(StateWrite.Register(lane, destination, result))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.BitFieldInsert(destination, insertValue, baseValue, bitPosition, bitLength, width) =>
          forEveryLane(context) { lane =>
            for {
              insert <- context.read(lane, insertValue)
              base   <- context.read(lane, baseValue)
              start  <- context.read(lane, bitPosition)
              count  <- context.read(lane, bitLength)
            } yield effects.stage(StateWrite.Register(lane, destination, insertBits(insert, base, start, count, width)))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.BitMask(destination, start, widthValue, mode) =>
          forEveryLane(context) { lane =>
            for {
              startValue <- context.read(lane, start)
              countValue <- context.read(lane, widthValue)
            } yield effects.stage(StateWrite.Register(
              lane,
              destination,
              bitMask(startValue, countValue, mode)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.BytePermute(destination, left, right, control, mode) =>
          forEveryLane(context) { lane =>
            for {
              leftValue    <- context.read(lane, left)
              rightValue   <- context.read(lane, right)
              controlValue <- context.read(lane, control)
            } yield effects.stage(StateWrite.Register(
              lane,
              destination,
              permuteBytes(leftValue, rightValue, controlValue, mode)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Select(destination, whenTrue, whenFalse, predicate, width) =>
          forEveryLane(context) { lane =>
            for {
              trueValue  <- context.read(lane, whenTrue)
              falseValue <- context.read(lane, whenFalse)
              condition  <- context.state.predicates(lane).get(predicate).toRight(
                ExecutionFault.UndefinedRegister(predicate, context.pc, lane)
              )
            } yield effects.stage(StateWrite.Register(
              lane,
              destination,
              truncate(if (condition) trueValue else falseValue, width)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadGlobal(destination, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              context.state.readParameterBuffer(rawAddress, byteCount(width)) match {
                case Some(loaded) =>
                  Right(effects.stage(StateWrite.Register(lane, destination, loadBits(loaded, width, signed))))
                case None =>
                  context.memory
                    .readLittleEndian(GuestAddress(rawAddress), byteCount(width))
                    .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                    .map { loaded =>
                      effects.stage(StateWrite.Register(
                        lane,
                        destination,
                        loadBits(loaded, width, signed)
                      ))
                    }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadGeneric(destination, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              context.state.readParameterBuffer(rawAddress, byteCount(width)) match {
                case Some(value) => Right(effects.stage(StateWrite.Register(
                    lane,
                    destination,
                    loadBits(value, width, signed)
                  )))
                case None if isSharedAddress(rawAddress) =>
                  Right(effects.stage(StateWrite.Register(
                    lane,
                    destination,
                    loadBits(context.state.readShared(rawAddress, width), width, signed)
                  )))
                case None if isLocalAddress(rawAddress) =>
                  Right(effects.stage(StateWrite.Register(
                    lane,
                    destination,
                    loadBits(context.state.readLocal(lane, rawAddress, width), width, signed)
                  )))
                case None =>
                  context.memory
                    .readLittleEndian(GuestAddress(rawAddress), byteCount(width))
                    .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                    .map(value =>
                      effects.stage(StateWrite.Register(
                        lane,
                        destination,
                        loadBits(value, width, signed)
                      ))
                    )
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.DiscardGlobalLoad(address, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              if (context.state.readParameterBuffer(rawAddress, byteCount(width)).nonEmpty) Right(())
              else
                context.memory
                  .readLittleEndian(GuestAddress(rawAddress), byteCount(width))
                  .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                  .map(_ => ())
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadGlobalVector(destinations, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              destinations.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (destination, index)) =>
                  result.flatMap { _ =>
                    context.memory
                      .readLittleEndian(
                        GuestAddress(rawAddress + index.toLong * byteCount(width).toLong),
                        byteCount(width)
                      )
                      .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                      .map(value =>
                        effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(value, width, signed)
                        ))
                      )
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadGenericVector(destinations, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              destinations.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (destination, index)) =>
                  result.flatMap { _ =>
                    val elementAddress = rawAddress + index.toLong * byteCount(width).toLong
                    context.state.readParameterBuffer(elementAddress, byteCount(width)) match {
                      case Some(value) => Right(effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(value, width, signed)
                        )))
                      case None if isSharedAddress(elementAddress) =>
                        Right(effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(context.state.readShared(elementAddress, width), width, signed)
                        )))
                      case None if isLocalAddress(elementAddress) =>
                        Right(effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(context.state.readLocal(lane, elementAddress, width), width, signed)
                        )))
                      case None =>
                        context.memory
                          .readLittleEndian(GuestAddress(elementAddress), byteCount(width))
                          .leftMap(error =>
                            ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}")
                          )
                          .map(value =>
                            effects.stage(StateWrite.Register(
                              lane,
                              destination,
                              loadBits(value, width, signed)
                            ))
                          )
                    }
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreGlobal(address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress <- context.read(lane, address)
              value      <- context.read(lane, source)
            } yield
              if (context.state.parameterBufferRange(rawAddress, byteCount(width)).nonEmpty)
                effects.stage(StateWrite.ParameterBuffer(rawAddress, width, value))
              else effects.stage(StateWrite.Memory(lane, GuestAddress(rawAddress), width, value))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreGeneric(address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress <- context.read(lane, address)
              value      <- context.read(lane, source)
            } yield
              if (context.state.parameterBufferRange(rawAddress, byteCount(width)).nonEmpty)
                effects.stage(StateWrite.ParameterBuffer(rawAddress, width, value))
              else if (isSharedAddress(rawAddress)) effects.stage(StateWrite.Shared(lane, rawAddress, width, value))
              else if (isLocalAddress(rawAddress)) effects.stage(StateWrite.Local(lane, rawAddress, width, value))
              else effects.stage(StateWrite.Memory(lane, GuestAddress(rawAddress), width, value))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreGlobalVector(address, sources, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              sources.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (source, index)) =>
                  result.flatMap { _ =>
                    context.read(lane, Value.Register(source)).map(value =>
                      effects.stage(StateWrite.Memory(
                        lane,
                        GuestAddress(rawAddress + index.toLong * byteCount(width).toLong),
                        width,
                        value
                      ))
                    )
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreGenericVector(address, sources, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              sources.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (source, index)) =>
                  result.flatMap { _ =>
                    context.read(lane, Value.Register(source)).map { value =>
                      val elementAddress = rawAddress + index.toLong * byteCount(width).toLong
                      if (isSharedAddress(elementAddress))
                        effects.stage(StateWrite.Shared(lane, elementAddress, width, value))
                      else if (isLocalAddress(elementAddress))
                        effects.stage(StateWrite.Local(lane, elementAddress, width, value))
                      else
                        effects.stage(StateWrite.Memory(lane, GuestAddress(elementAddress), width, value))
                    }
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadShared(destination, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).map(rawAddress =>
              effects.stage(StateWrite.Register(
                lane,
                destination,
                loadBits(context.state.readShared(rawAddress, width), width, signed)
              ))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadSharedVector(destinations, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).map { rawAddress =>
              destinations.zipWithIndex.foreach { case (destination, index) =>
                effects.stage(StateWrite.Register(
                  lane,
                  destination,
                  loadBits(
                    context.state.readShared(rawAddress + index.toLong * byteCount(width), width),
                    width,
                    signed
                  )
                ))
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadLocal(destination, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).map(rawAddress =>
              effects.stage(StateWrite.Register(
                lane,
                destination,
                loadBits(context.state.readLocal(lane, rawAddress, width), width, signed)
              ))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadLocalVector(destinations, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).map { rawAddress =>
              destinations.zipWithIndex.foreach { case (destination, index) =>
                effects.stage(StateWrite.Register(
                  lane,
                  destination,
                  loadBits(
                    context.state.readLocal(lane, rawAddress + index.toLong * byteCount(width), width),
                    width,
                    signed
                  )
                ))
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreShared(address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress <- context.read(lane, address)
              value      <- context.read(lane, source)
            } yield effects.stage(StateWrite.Shared(lane, rawAddress, width, value))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreSharedVector(address, sources, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              sources.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (source, index)) =>
                  result.flatMap { _ =>
                    context.read(lane, Value.Register(source)).map(value =>
                      effects.stage(StateWrite.Shared(
                        lane,
                        rawAddress + index.toLong * byteCount(width),
                        width,
                        value
                      ))
                    )
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreLocal(address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress <- context.read(lane, address)
              value      <- context.read(lane, source)
            } yield effects.stage(StateWrite.Local(lane, rawAddress, width, value))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreLocalVector(address, sources, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              sources.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (source, index)) =>
                  result.flatMap { _ =>
                    context.read(lane, Value.Register(source)).map(value =>
                      effects.stage(StateWrite.Local(
                        lane,
                        rawAddress + index.toLong * byteCount(width),
                        width,
                        value
                      ))
                    )
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.AsyncCopy(destination, source, size) =>
          forEveryLane(context) { lane =>
            for {
              destinationAddress <- context.read(lane, destination)
              sourceAddress      <- context.read(lane, source)
              _                  <- (0 until size by 8).foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, offset) =>
                  result.flatMap { _ =>
                    val width = Math.min(8, size - offset) * 8
                    context.memory
                      .readLittleEndian(GuestAddress(sourceAddress + offset.toLong), byteCount(width))
                      .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                      .map(value =>
                        effects.stage(StateWrite.Shared(
                          lane,
                          destinationAddress + offset.toLong,
                          width,
                          value
                        ))
                      )
                  }
              }
            } yield ()
          }.map(_ => Step.Advance)
        case ExecutableInstruction.AsyncCommit | ExecutableInstruction.AsyncWait                 => Right(Step.Advance)
        case ExecutableInstruction.WarpReduce(destination, source, memberMask, operation, width) =>
          val values                          = mutable.Map.empty[Int, Long]
          val masks                           = mutable.Map.empty[Int, Long]
          var failure: Option[ExecutionFault] = None
          context.active.foreachLane { lane =>
            if (failure.isEmpty)
              context.read(lane, source).flatMap(value =>
                context.read(lane, memberMask).map { mask =>
                  values(lane) = value
                  masks(lane) = mask
                }
              ).left.foreach(error => failure = Some(error))
          }
          failure match {
            case Some(error) => Left(error)
            case None        =>
              context.active.foreachLane { lane =>
                val warpBase = lane & ~31
                val mask     = masks.getOrElse(lane, 0L)
                val members  = Col.toCol(values.iterator.collect {
                  case (candidate, value)
                      if (candidate & ~31) == warpBase && (mask & (1L << (candidate & 31))) != 0L => value
                })
                val reduced = members.foldLeft(0L) { (accumulator, value) =>
                  operation match {
                    case IntegerOperation.Add                       => truncate(accumulator + value, width)
                    case IntegerOperation.Minimum(signed) if signed =>
                      Math.min(accumulator.toInt.toLong, value.toInt.toLong)
                    case IntegerOperation.Minimum(_) =>
                      if (java.lang.Long.compareUnsigned(accumulator, value) < 0) accumulator else value
                    case IntegerOperation.Maximum(signed) if signed =>
                      Math.max(accumulator.toInt.toLong, value.toInt.toLong)
                    case IntegerOperation.Maximum(_) =>
                      if (java.lang.Long.compareUnsigned(accumulator, value) > 0) accumulator else value
                    case _ => accumulator
                  }
                }
                effects.stage(StateWrite.Register(lane, destination, truncate(reduced, width)))
              }
              Right(Step.Advance)
          }
        case ExecutableInstruction.AtomicAdd(destination, address, source, width, global) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress  <- context.read(lane, address)
              sourceValue <- context.read(lane, source)
            } yield
              if (global)
                effects.stage(StateWrite.AtomicMemoryAdd(
                  lane,
                  destination,
                  GuestAddress(rawAddress),
                  width,
                  sourceValue
                ))
              else effects.stage(StateWrite.AtomicSharedAdd(lane, destination, rawAddress, width, sourceValue))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.AtomicMin(destination, address, source, width, global) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress  <- context.read(lane, address)
              sourceValue <- context.read(lane, source)
            } yield
              if (global)
                effects.stage(StateWrite.AtomicMemoryMin(
                  lane,
                  destination,
                  GuestAddress(rawAddress),
                  width,
                  sourceValue
                ))
              else effects.stage(StateWrite.AtomicSharedMin(lane, destination, rawAddress, width, sourceValue))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.AtomicAddFloat(destination, address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress  <- context.read(lane, address)
              sourceValue <- context.read(lane, source)
            } yield effects.stage(StateWrite.AtomicMemoryAddFloat(
              lane,
              destination,
              GuestAddress(rawAddress),
              width,
              sourceValue
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.SetPredicate(destination, comparison, kind, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield effects.stage(
              StateWrite.Predicate(lane, destination, compare(leftValue, rightValue, comparison, kind, width))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.PredicateCompare(destination, comparison, left, right) =>
          forEveryLane(context) { lane =>
            for {
              leftValue <- context.state.predicates(lane).get(left).toRight(
                ExecutionFault.UndefinedRegister(left, context.pc, lane)
              )
              rightValue <- context.state.predicates(lane).get(right).toRight(
                ExecutionFault.UndefinedRegister(right, context.pc, lane)
              )
            } yield effects.stage(StateWrite.Predicate(
              lane,
              destination,
              comparePredicates(leftValue, rightValue, comparison)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.PredicateUnary(destination, source) =>
          forEveryLane(context) { lane =>
            context.state.predicates(lane).get(source).toRight(
              ExecutionFault.UndefinedRegister(source, context.pc, lane)
            ).map(value => effects.stage(StateWrite.Predicate(lane, destination, !value)))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.PredicateBinary(operation, destination, left, right) =>
          forEveryLane(context) { lane =>
            for {
              leftValue <- context.state.predicates(lane).get(left).toRight(
                ExecutionFault.UndefinedRegister(left, context.pc, lane)
              )
              rightValue <- context.state.predicates(lane).get(right).toRight(
                ExecutionFault.UndefinedRegister(right, context.pc, lane)
              )
            } yield effects.stage(StateWrite.Predicate(
              lane,
              destination,
              operation match {
                case PredicateOperation.And => leftValue && rightValue
                case PredicateOperation.Or  => leftValue || rightValue
                case PredicateOperation.Xor => leftValue ^ rightValue
              }
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Branch(target)  => Right(Step.Jump(target))
        case ExecutableInstruction.BarrierSync(id) =>
          // A workgroup barrier is identified by its hardware barrier slot, not
          // by the static PC at which a divergent cohort happens to arrive.
          Right(Step.Suspend(WaitKey(WaitScope.Workgroup, id.toLong, 0L)))
        case ExecutableInstruction.BarrierReduce(_, id, _, _) =>
          Right(Step.Suspend(WaitKey(WaitScope.Workgroup, id.toLong, 0L)))
        case ExecutableInstruction.WarpBarrier(memberMask) =>
          var firstLane = -1
          context.active.foreachLane { lane => if (firstLane < 0) firstLane = lane }
          context.read(firstLane, memberMask).flatMap { memberMaskValue =>
            var mismatch = false
            context.active.foreachLane { lane =>
              if (!mismatch) {
                context.read(lane, memberMask) match {
                  case Right(value) if value == memberMaskValue => ()
                  case _                                        => mismatch = true
                }
              }
            }
            if (mismatch)
              Left(ExecutionFault.InvalidState("bar.warp.sync member mask differs across active lanes"))
            else
              Right(Step.Suspend(WaitKey(WaitScope.Warp, memberMaskValue, context.pc.value.toLong)))
          }
        case ExecutableInstruction.MemoryBarrier                              => Right(Step.Advance)
        case ExecutableInstruction.Prefetch                                   => Right(Step.Advance)
        case ExecutableInstruction.Call(function, returnParameter, parameter) =>
          forEveryLane(context) { lane =>
            context.functionName(lane, function).flatMap { name =>
              executeBuiltin(context, lane, name, returnParameter, parameter).getOrElse {
                context.kernel.functions.get(name).toRight(
                  ExecutionFault.InvalidState(s"missing device function $name")
                ).flatMap(deviceFunction =>
                  executeFunction(context, lane, deviceFunction, parameter, returnParameter)
                )
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Trap =>
          Left(ExecutionFault.ArchitecturalTrap("ptx", 0L, context.pc))
        case ExecutableInstruction.Return => Right(Step.Exit(context.active))
      }
  }

  /** The common nvcc path is a dense register-only instruction stream. Keep the checked, staged path below for memory
    * and rendezvous operations, but avoid allocating an Either and a StateWrite for every lane of an arithmetic
    * instruction. Reads still happen before the destination is changed, matching the staged path's aliasing rule.
    */
  private def executeFast(
      instruction: ExecutableInstruction,
      context: ExecutionContext
  ): Boolean = instruction match {
    case ExecutableInstruction.Move(destination, Value.Function(function), _) =>
      context.active.foreachLane(lane => context.state.functionRegisters(lane)(destination) = function)
      true
    case ExecutableInstruction.Move(destination, source, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane(lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          truncate(readFast(context, lane, source), width)
        )
      )
      true
    case ExecutableInstruction.MovePredicate(destination, source) =>
      val destinationIndex = PredicateFile.index(destination)
      context.active.foreachLane(lane =>
        writePredicate(
          context.state.predicates(lane),
          destination,
          destinationIndex,
          readFast(context, lane, source) != 0L
        )
      )
      true
    case ExecutableInstruction.MoveSplit(destinations, source, partWidth) =>
      val lowIndex  = if (destinations(0) == "_") -1 else RegisterFile.index(destinations(0))
      val highIndex = if (destinations(1) == "_") -1 else RegisterFile.index(destinations(1))
      context.active.foreachLane { lane =>
        val value = readFast(context, lane, source)
        if (destinations(0) != "_")
          writeRegister(context.state.registers(lane), destinations(0), lowIndex, truncate(value, partWidth))
        if (destinations(1) != "_")
          writeRegister(
            context.state.registers(lane),
            destinations(1),
            highIndex,
            truncate(value >>> partWidth, partWidth)
          )
      }
      true
    case ExecutableInstruction.MoveCombine(destination, sources, partWidth) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val low  = readFast(context, lane, sources(0))
        val high = readFast(context, lane, sources(1))
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          truncate((low & truncate(-1L, partWidth)) | (truncate(high, partWidth) << partWidth), partWidth * 2)
        )
      }
      true
    case ExecutableInstruction.IntegerUnary(operation, destination, source, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val value  = readFast(context, lane, source)
        val result = operation match {
          case IntegerUnaryOperation.Negate          => -value
          case IntegerUnaryOperation.Absolute        => Math.abs(signExtend(truncate(value, width), width))
          case IntegerUnaryOperation.BitwiseNot      => ~value
          case IntegerUnaryOperation.PopulationCount =>
            if (width == 32) Integer.bitCount((value & 0xffffffffL).toInt).toLong
            else java.lang.Long.bitCount(value).toLong
          case IntegerUnaryOperation.LeadingZeroCount =>
            if (width == 32) Integer.numberOfLeadingZeros((value & 0xffffffffL).toInt).toLong
            else java.lang.Long.numberOfLeadingZeros(value).toLong
          case IntegerUnaryOperation.BitReverse => reverseBits(value, width)
        }
        writeRegister(context.state.registers(lane), destination, destinationIndex, truncate(result, width))
      }
      true
    case ExecutableInstruction.BitFind(destination, source, width, signed, shiftAmount) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          bitFind(readFast(context, lane, source), width, signed, shiftAmount)
        )
      }
      true
    case ExecutableInstruction.DotProduct4(destination, left, right, addend, leftSigned, rightSigned) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          dotProduct4(
            readFast(context, lane, left),
            readFast(context, lane, right),
            readFast(context, lane, addend),
            leftSigned,
            rightSigned
          )
        )
      }
      true
    case ExecutableInstruction.IntegerAddWithCarry(destination, left, right, width, carryIn, carryOut) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val (result, nextCarry) = integerAddWithCarry(
          readFast(context, lane, left),
          readFast(context, lane, right),
          width,
          carryIn,
          context.state.carry(lane)
        )
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
        if (carryOut) context.state.carry(lane) = nextCarry
      }
      true
    case ExecutableInstruction.IntegerSubWithBorrow(destination, left, right, width, borrowIn, borrowOut) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val (result, nextBorrow) = integerSubWithBorrow(
          readFast(context, lane, left),
          readFast(context, lane, right),
          width,
          borrowIn,
          context.state.carry(lane)
        )
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
        if (borrowOut) context.state.carry(lane) = nextBorrow
      }
      true
    case ExecutableInstruction.IntegerBinary(operation, destination, left, right, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val leftValue  = readFast(context, lane, left)
        val rightValue = readFast(context, lane, right)
        val result     = operation match {
          case IntegerOperation.Add                    => leftValue + rightValue
          case IntegerOperation.Subtract               => leftValue - rightValue
          case IntegerOperation.BitwiseOr              => leftValue | rightValue
          case IntegerOperation.BitwiseAnd             => leftValue & rightValue
          case IntegerOperation.BitwiseXor             => leftValue ^ rightValue
          case IntegerOperation.MultiplyLow            => leftValue * rightValue
          case IntegerOperation.MultiplyHigh(signed)   => multiplyHigh(leftValue, rightValue, width, signed)
          case IntegerOperation.Divide(signed)         => divideInteger(leftValue, rightValue, width, signed)
          case IntegerOperation.Remainder(signed)      => remainderInteger(leftValue, rightValue, width, signed)
          case IntegerOperation.MultiplyWideUnsigned16 =>
            (leftValue & 0xffffL) * (rightValue & 0xffffL)
          case IntegerOperation.MultiplyWideUnsigned32 => low32(leftValue) * low32(rightValue)
          case IntegerOperation.MultiplyWideSigned32   => leftValue.toInt.toLong * rightValue.toInt.toLong
          case IntegerOperation.Minimum(signed)        =>
            if (signed) Math.min(signExtend(leftValue, width), signExtend(rightValue, width))
            else if (width == 64) {
              if (java.lang.Long.compareUnsigned(leftValue, rightValue) <= 0) leftValue else rightValue
            } else Math.min(truncate(leftValue, width), truncate(rightValue, width))
          case IntegerOperation.Maximum(signed) =>
            if (signed) Math.max(signExtend(leftValue, width), signExtend(rightValue, width))
            else if (width == 64) {
              if (java.lang.Long.compareUnsigned(leftValue, rightValue) >= 0) leftValue else rightValue
            } else Math.max(truncate(leftValue, width), truncate(rightValue, width))
          case IntegerOperation.ShiftLeft         => leftValue << (rightValue & (width - 1).toLong).toInt
          case IntegerOperation.ShiftRightLogical =>
            if (width == 32) (leftValue & 0xffffffffL) >>> (rightValue & 31L).toInt
            else leftValue >>> (rightValue & 63L).toInt
          case IntegerOperation.ShiftRightArithmetic =>
            if (width == 32) (leftValue.toInt >> (rightValue & 31L).toInt).toLong
            else leftValue >> (rightValue & 63L).toInt
        }
        writeRegister(context.state.registers(lane), destination, destinationIndex, truncate(result, width))
      }
      true
    case ExecutableInstruction.Convert(destination, source, sourceType, destinationType, rounding) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane(lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          convert(readFast(context, lane, source), sourceType, destinationType, rounding)
        )
      )
      true
    case ExecutableInstruction.MultiplyAdd(destination, left, right, addend, operation) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val (result, carry) = integerMultiplyAdd(
          readFast(context, lane, left),
          readFast(context, lane, right),
          readFast(context, lane, addend),
          operation,
          context.state.carry(lane)
        )
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
        operation match {
          case IntegerMultiplyAddOperation.Low(_, _, _, true) | IntegerMultiplyAddOperation.High(_, _, _, true) =>
            context.state.carry(lane) = carry
          case _ => ()
        }
      }
      true
    case ExecutableInstruction.BitFieldInsert(destination, insertValue, baseValue, bitPosition, bitLength, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          insertBits(
            readFast(context, lane, insertValue),
            readFast(context, lane, baseValue),
            readFast(context, lane, bitPosition),
            readFast(context, lane, bitLength),
            width
          )
        )
      }
      true
    case ExecutableInstruction.BitMask(destination, start, widthValue, mode) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          bitMask(readFast(context, lane, start), readFast(context, lane, widthValue), mode)
        )
      }
      true
    case ExecutableInstruction.BytePermute(destination, left, right, control, mode) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          permuteBytes(
            readFast(context, lane, left),
            readFast(context, lane, right),
            readFast(context, lane, control),
            mode
          )
        )
      }
      true
    case ExecutableInstruction.FloatBinary(operation, destination, left, right, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val leftBits  = readFast(context, lane, left)
        val rightBits = readFast(context, lane, right)
        val result    = if (width == 16) {
          val value = operation match {
            case FloatOperation.Add      => IEEE754.binary16ToFloat(leftBits) + IEEE754.binary16ToFloat(rightBits)
            case FloatOperation.Subtract => IEEE754.binary16ToFloat(leftBits) - IEEE754.binary16ToFloat(rightBits)
            case FloatOperation.Multiply => IEEE754.binary16ToFloat(leftBits) * IEEE754.binary16ToFloat(rightBits)
            case FloatOperation.Divide   => IEEE754.binary16ToFloat(leftBits) / IEEE754.binary16ToFloat(rightBits)
            case FloatOperation.Minimum  =>
              Math.min(IEEE754.binary16ToFloat(leftBits), IEEE754.binary16ToFloat(rightBits))
            case FloatOperation.Maximum =>
              Math.max(IEEE754.binary16ToFloat(leftBits), IEEE754.binary16ToFloat(rightBits))
            case FloatOperation.CopySign =>
              Math.copySign(IEEE754.binary16ToFloat(rightBits), IEEE754.binary16ToFloat(leftBits))
          }
          IEEE754.floatToBinary16(value)
        } else if (width == 32) {
          val value = operation match {
            case FloatOperation.Add      => IEEE754.binary32ToFloat(leftBits) + IEEE754.binary32ToFloat(rightBits)
            case FloatOperation.Subtract => IEEE754.binary32ToFloat(leftBits) - IEEE754.binary32ToFloat(rightBits)
            case FloatOperation.Multiply => IEEE754.binary32ToFloat(leftBits) * IEEE754.binary32ToFloat(rightBits)
            case FloatOperation.Divide   => IEEE754.binary32ToFloat(leftBits) / IEEE754.binary32ToFloat(rightBits)
            case FloatOperation.Minimum  =>
              Math.min(IEEE754.binary32ToFloat(leftBits), IEEE754.binary32ToFloat(rightBits))
            case FloatOperation.Maximum =>
              Math.max(IEEE754.binary32ToFloat(leftBits), IEEE754.binary32ToFloat(rightBits))
            case FloatOperation.CopySign =>
              Math.copySign(IEEE754.binary32ToFloat(rightBits), IEEE754.binary32ToFloat(leftBits))
          }
          IEEE754.floatToBinary32(value)
        } else {
          val value = operation match {
            case FloatOperation.Add      => IEEE754.binary64ToDouble(leftBits) + IEEE754.binary64ToDouble(rightBits)
            case FloatOperation.Subtract => IEEE754.binary64ToDouble(leftBits) - IEEE754.binary64ToDouble(rightBits)
            case FloatOperation.Multiply => IEEE754.binary64ToDouble(leftBits) * IEEE754.binary64ToDouble(rightBits)
            case FloatOperation.Divide   => IEEE754.binary64ToDouble(leftBits) / IEEE754.binary64ToDouble(rightBits)
            case FloatOperation.Minimum  =>
              Math.min(IEEE754.binary64ToDouble(leftBits), IEEE754.binary64ToDouble(rightBits))
            case FloatOperation.Maximum =>
              Math.max(IEEE754.binary64ToDouble(leftBits), IEEE754.binary64ToDouble(rightBits))
            case FloatOperation.CopySign =>
              Math.copySign(IEEE754.binary64ToDouble(rightBits), IEEE754.binary64ToDouble(leftBits))
          }
          IEEE754.doubleToBinary64(value)
        }
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
      }
      true
    case ExecutableInstruction.FloatBinaryPacked(operation, destination, left, right, bf16) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          packedBinaryBits(operation, readFast(context, lane, left), readFast(context, lane, right), bf16)
        )
      }
      true
    case ExecutableInstruction.FloatUnary(operation, destination, source, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val sourceValue = readFast(context, lane, source)
        val result      = floatUnaryBits(operation, sourceValue, width)
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
      }
      true
    case ExecutableInstruction.FusedMultiplyAdd(destination, left, right, addend, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val leftBits   = readFast(context, lane, left)
        val rightBits  = readFast(context, lane, right)
        val addendBits = readFast(context, lane, addend)
        val result     = if (width == 16)
          IEEE754.floatToBinary16(
            Math.fma(
              IEEE754.binary16ToFloat(leftBits),
              IEEE754.binary16ToFloat(rightBits),
              IEEE754.binary16ToFloat(addendBits)
            )
          )
        else if (width == 32)
          IEEE754.floatToBinary32(
            Math.fma(
              IEEE754.binary32ToFloat(leftBits),
              IEEE754.binary32ToFloat(rightBits),
              IEEE754.binary32ToFloat(addendBits)
            )
          )
        else
          IEEE754.doubleToBinary64(
            Math.fma(
              IEEE754.binary64ToDouble(leftBits),
              IEEE754.binary64ToDouble(rightBits),
              IEEE754.binary64ToDouble(addendBits)
            )
          )
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
      }
      true
    case ExecutableInstruction.FusedMultiplyAddPacked(destination, left, right, addend, bf16) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          packedFmaBits(
            readFast(context, lane, left),
            readFast(context, lane, right),
            readFast(context, lane, addend),
            bf16
          )
        )
      }
      true
    case ExecutableInstruction.FusedMultiplyAddBf16(destination, left, right, addend) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          bf16FmaBits(
            readFast(context, lane, left),
            readFast(context, lane, right),
            readFast(context, lane, addend)
          )
        )
      }
      true
    case ExecutableInstruction.SetPredicate(destination, comparison, kind, left, right, width) =>
      val destinationIndex = PredicateFile.index(destination)
      context.active.foreachLane { lane =>
        val value = compare(
          readFast(context, lane, left),
          readFast(context, lane, right),
          comparison,
          kind,
          width
        )
        writePredicate(context.state.predicates(lane), destination, destinationIndex, value)
      }
      true
    case ExecutableInstruction.PredicateCompare(destination, comparison, left, right) =>
      val destinationIndex = PredicateFile.index(destination)
      val leftIndex        = PredicateFile.index(left)
      val rightIndex       = PredicateFile.index(right)
      context.active.foreachLane { lane =>
        val leftValue  = readPredicateFast(context.state.predicates(lane), left, leftIndex)
        val rightValue = readPredicateFast(context.state.predicates(lane), right, rightIndex)
        writePredicate(
          context.state.predicates(lane),
          destination,
          destinationIndex,
          comparePredicates(leftValue, rightValue, comparison)
        )
      }
      true
    case ExecutableInstruction.PredicateUnary(destination, source) =>
      val destinationIndex = PredicateFile.index(destination)
      val sourceIndex      = PredicateFile.index(source)
      context.active.foreachLane { lane =>
        writePredicate(
          context.state.predicates(lane),
          destination,
          destinationIndex,
          !readPredicateFast(context.state.predicates(lane), source, sourceIndex)
        )
      }
      true
    case ExecutableInstruction.PredicateBinary(operation, destination, left, right) =>
      val destinationIndex = PredicateFile.index(destination)
      val leftIndex        = PredicateFile.index(left)
      val rightIndex       = PredicateFile.index(right)
      context.active.foreachLane { lane =>
        val leftValue  = readPredicateFast(context.state.predicates(lane), left, leftIndex)
        val rightValue = readPredicateFast(context.state.predicates(lane), right, rightIndex)
        writePredicate(
          context.state.predicates(lane),
          destination,
          destinationIndex,
          operation match {
            case PredicateOperation.And => leftValue && rightValue
            case PredicateOperation.Or  => leftValue || rightValue
            case PredicateOperation.Xor => leftValue ^ rightValue
          }
        )
      }
      true
    case ExecutableInstruction.Select(destination, whenTrue, whenFalse, predicate, width) =>
      val destinationIndex = RegisterFile.index(destination)
      val predicateIndex   = PredicateFile.index(predicate)
      context.active.foreachLane { lane =>
        val selected = if (readPredicateFast(context.state.predicates(lane), predicate, predicateIndex))
          readFast(context, lane, whenTrue)
        else readFast(context, lane, whenFalse)
        writeRegister(context.state.registers(lane), destination, destinationIndex, truncate(selected, width))
      }
      true
    case _ => false
  }

  private def writeRegister(registers: RegisterFile, name: String, index: Int, value: Long): Unit =
    if (index >= 0) registers.updateSlot(index, value) else registers.update(name, value)

  private def writePredicate(predicates: PredicateFile, name: String, index: Int, value: Boolean): Unit =
    if (index >= 0) predicates.updateSlot(index, value) else predicates.update(name, value)

  private def readPredicateFast(predicates: PredicateFile, name: String, index: Int): Boolean =
    if (index >= 0) predicates.getSlot(index).getOrElse(false) else predicates.get(name).getOrElse(false)

  private def executeBuiltin(
      context: ExecutionContext,
      lane: Int,
      name: String,
      returnParameters: Col[String],
      parameters: Col[String]
  ): Option[Either[ExecutionFault, Unit]] = {
    def parameter(index: Int, width: Int = 8): Long =
      if (index < parameters.size)
        context.state.parameters(lane).getOrElse(
          parameters(index),
          context.state.readParameterBytes(lane, parameters(index), 0, width)
        )
      else 0L

    def returnValue(value: Long): Unit =
      returnParameters.headOption.foreach(name => context.state.parameters(lane)(name) = value)

    def writeLocalPointer(index: Int, value: Long): Unit =
      if (index < parameters.size) context.state.writeLocal(lane, parameter(index), 32, value)

    name match {
      case "cudaGetDevice" =>
        writeLocalPointer(0, 0L)
        returnValue(0L)
        Some(Right(()))
      case "cudaDeviceGetAttribute" =>
        val attribute = parameter(1, 4).toInt
        writeLocalPointer(0, deviceAttribute(attribute).toLong)
        returnValue(0L)
        Some(Right(()))
      case "cudaOccupancyMaxActiveBlocksPerMultiprocessor" |
          "cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags" |
          "__cuda_syscall_cnpMaxActiveBlocksPerMultiprocessorWithFlags" =>
        writeLocalPointer(0, 1L)
        returnValue(0L)
        Some(Right(()))
      case "cudaGetLastError" | "cudaPeekAtLastError" | "__cudaDeviceSynchronizeDeprecationAvoidance" =>
        returnValue(0L)
        Some(Right(()))
      case "cudaGetErrorName" | "cudaGetErrorString" | "vprintf" =>
        returnValue(0L)
        Some(Right(()))
      case "cudaGetParameterBuffer" =>
        // The first argument is the requested alignment; the second is the
        // number of bytes needed for the child kernel's argument packet.
        val size   = Math.max(1L, parameter(1)).min(Int.MaxValue.toLong).toInt
        val buffer = context.state.allocateParameterBuffer(size)
        returnValue(buffer)
        Some(Right(()))
      case "malloc" =>
        context.memory match {
          case memory: net.kurobako.gemstone.memory.DynamicAddressMemory =>
            returnValue(memory.allocate(parameter(0)))
            Some(Right(()))
          case _ => Some(Left(ExecutionFault.InvalidState("device malloc requires dynamic address memory")))
        }
      case "free" =>
        context.memory match {
          case memory: net.kurobako.gemstone.memory.DynamicAddressMemory =>
            memory.free(parameter(0))
            Some(Right(()))
          case _ => Some(Left(ExecutionFault.InvalidState("device free requires dynamic address memory")))
        }
      case "cudaLaunchDevice" =>
        Some(executeDeviceLaunch(context, lane, returnParameters, parameters, parameter))
      case _ => None
    }
  }

  private def deviceAttribute(attribute: Int): Int = attribute match {
    case 1 | 2 | 3 | 39 => 1024
    case 4              => 64
    case 5              => Int.MaxValue
    case 6 | 7          => 65535
    case 8              => 49152
    case 10             => 32
    case 16             => 1
    case 41             => 1
    case 75             => 7
    case _              => 0
  }

  private def executeDeviceLaunch(
      context: ExecutionContext,
      lane: Int,
      returnParameters: Col[String],
      parameters: Col[String],
      parameter: (Int, Int) => Long
  ): Either[ExecutionFault, Unit] =
    if (parameters.size < 6)
      Left(ExecutionFault.InvalidState(s"cudaLaunchDevice expects six parameters, found ${parameters.size}"))
    else {
      val target = context.state.functionParameters(lane).get(parameters(0))
      val child  = target.flatMap(context.kernel.functions.get).toRight(
        ExecutionFault.InvalidState(s"cudaLaunchDevice target is not a lowered entry: ${target.getOrElse("<unknown>")}")
      )
      child.flatMap { function =>
        val buffer = parameter(1, 8)
        val grid   =
          (0 until 3).map(index => context.state.readParameterBytes(lane, parameters(2), index * 4, 4) & 0xffffffffL)
        val block =
          (0 until 3).map(index => context.state.readParameterBytes(lane, parameters(3), index * 4, 4) & 0xffffffffL)
        val childArguments                          = mutable.Map.empty[String, Argument]
        var argumentFailure: Option[ExecutionFault] = None
        var index                                   = 0
        while (index < function.parameters.size && argumentFailure.isEmpty) {
          val width  = function.parameterWidths(index)
          val offset = function.parameterOffsets(index)
          context.state.readParameterBufferBytes(buffer + offset.toLong, width) match {
            case Some(bytes) => childArguments(function.parameters(index)) = Argument(Col.unsafeFromArray(bytes))
            case None        => argumentFailure = Some(ExecutionFault.InvalidState(
                s"cudaLaunchDevice parameter buffer 0x${java.lang.Long.toUnsignedString(buffer, 16)} is out of range"
              ))
          }
          index += 1
        }
        argumentFailure.toLeft {
          val geometry = for {
            groupCount <- Dimensions.create(grid(0).toInt, grid(1).toInt, grid(2).toInt)
            groupSize  <- Dimensions.create(block(0).toInt, block(1).toInt, block(2).toInt)
            value      <- LaunchGeometry.create(groupSize, groupCount)
          } yield value
          geometry.leftMap(error => ExecutionFault.InvalidState(error)).flatMap { value =>
            val result = Interpreter.execute(
              Kernel(
                function.name,
                function.parameters.zip(function.parameterWidths).map { case (name, width) =>
                  KernelParameter(name, width)
                },
                function.program,
                context.kernel.functions,
                context.kernel.sharedSymbols ++ function.sharedSymbols,
                context.kernel.globalSymbols
              ),
              value,
              childArguments.toMap,
              context.memory,
              context.control,
              captureTrace = false,
              initialParameterBuffers = context.state.parameterBuffers.toMap
            )
            result.leftMap { error =>
              ExecutionFault.InvalidState(error.message)
            }
          }
        }
      }.map(_ => returnParameters.headOption.foreach(name => context.state.parameters(lane)(name) = 0L))
    }

  private def readFast(context: ExecutionContext, lane: Int, value: Value): Long = value match {
    case Value.Register(name)       => context.state.registers(lane)(name)
    case Value.RegisterSlot(index)  => context.state.registers(lane).getSlot(index)
    case Value.Predicate(name)      => if (context.state.predicates(lane).get(name).getOrElse(false)) 1L else 0L
    case Value.PredicateSlot(index) => if (context.state.predicates(lane).getSlot(index).getOrElse(false)) 1L else 0L
    case Value.Symbol(name)         =>
      context.state.parameterAddresses.get(name)
        .orElse(context.kernel.sharedSymbols.get(name))
        .orElse(context.kernel.globalSymbols.get(name))
        .getOrElse(0L)
    case Value.Immediate(immediate)  => immediate
    case Value.Address(base, offset) => readFast(context, lane, base) + offset
    case Value.LaneId                => (lane & 31).toLong
    case Value.ActiveMask            => context.active.warpBits(lane)
    case Value.LaneMaskGreaterEqual  => (0xffffffffL << (lane & 31)) & 0xffffffffL
    case Value.LaneMaskGreater       => (0xffffffffL << ((lane & 31) + 1)) & 0xffffffffL
    case Value.LaneMaskLessEqual     => (1L << ((lane & 31) + 1)) - 1L
    case Value.LaneMaskLess          => (1L << (lane & 31)) - 1L
    case Value.LaneMaskEqual         => 1L << (lane & 31)
    case Value.ThreadId(axis)        => axis.select(context.geometry.localCoordinates(lane)).toLong
    case Value.BlockId(axis)         => axis.select(context.group).toLong
    case Value.BlockDim(axis)        => axis.select(context.geometry.groupSize).toLong
    case Value.GridDim(axis)         => axis.select(context.geometry.groupCount).toLong
    case Value.Function(name)        => throw IllegalStateException(s"function symbol $name is not numeric")
  }

  private def executeFunction(
      caller: ExecutionContext,
      callerLane: Int,
      function: DeviceFunction,
      parameters: Col[String],
      returnParameters: Col[String]
  ): Either[ExecutionFault, Unit] = {
    val oneLane = LaneMask.all(1).toOption.getOrElse(LaneMask.Empty)
    val state   = BlockState(1, oneLane)
    val input   = if (parameters.size != function.parameters.size) {
      Left(ExecutionFault.InvalidState(
        s"device function ${function.name} expects ${function.parameters.size} parameters, found ${parameters.size}"
      ))
    } else {
      parameters.zip(function.parameters).foldLeft[Either[ExecutionFault, Unit]](Right(())) {
        case (current, (callerName, functionName)) =>
          current.flatMap { _ =>
            caller.state.parameters(callerLane).get(callerName).toRight(
              ExecutionFault.InvalidState(s"missing call parameter $callerName")
            ).map(value => state.parameters(0)(functionName) = value)
          }
      }
    }
    input match {
      case Left(fault) => Left(fault)
      case Right(())   =>
        var pc      = Pc.Entry
        var running = true
        val effects = Effects[StateWrite]
        while (running && pc.value < function.program.instructions.length) {
          caller.control.checkpoint() match {
            case Left(fault) => return Left(fault)
            case Right(())   => ()
          }
          val predicated = function.program.instructions(pc.value)
          val active     = predicated.predication match {
            case None            => oneLane
            case Some(predicate) =>
              state.predicates(0).get(predicate.name) match {
                case Some(value) if value != predicate.negated => oneLane
                case Some(_)                                   => LaneMask.Empty
                case None => return Left(ExecutionFault.UndefinedRegister(predicate.name, pc, 0))
              }
          }
          val functionContext = ExecutionContext(
            caller.kernel.copy(sharedSymbols = caller.kernel.sharedSymbols ++ function.sharedSymbols),
            caller.block,
            caller.group,
            caller.geometry,
            state,
            pc,
            active,
            Map.empty,
            caller.memory,
            caller.control
          )
          effects.clear()
          val outcome =
            if (active.isEmpty) Right(Step.Advance)
            else predicated.instruction.execute(functionContext, effects)
          outcome match {
            case Left(fault) => return Left(fault)
            case Right(step) =>
              effects.commit(validateWrite(caller.memory, pc), applyWrite(state, caller.memory)) match {
                case Left(fault) => return Left(fault)
                case Right(())   => ()
              }
              step match {
                case Step.Advance =>
                  pc.next match {
                    case Right(next) => pc = next
                    case Left(_)     => return Left(ExecutionFault.InvalidProgram("function pc overflow"))
                  }
                case Step.Exit(_)        => running = false
                case Step.Jump(target)   => pc = target
                case Step.Split(_, _, _) =>
                  return Left(
                    ExecutionFault.InvalidState(s"unsupported control split in device function ${function.name}")
                  )
                case Step.Suspend(_) =>
                  return Left(ExecutionFault.InvalidState(s"unsupported barrier in device function ${function.name}"))
                case Step.Trap(fault) => return Left(fault)
              }
          }
        }
        if (returnParameters.size != function.returnParameters.size) {
          return Left(ExecutionFault.InvalidState(
            s"device function ${function.name} returns ${function.returnParameters.size} parameters, found ${returnParameters.size}"
          ))
        }
        returnParameters.zip(function.returnParameters).foreach { case (callerName, functionName) =>
          state.parameters(0).get(functionName).foreach { value =>
            caller.state.parameters(callerLane)(callerName) = value
          }
        }
        Right(())
    }
  }

  private def validateWrite(memory: AddressMemory, pc: Pc)(write: StateWrite): Either[ExecutionFault, Unit] =
    write match {
      case StateWrite.Memory(lane, address, width, _) =>
        val result = memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
        result
      case StateWrite.AtomicMemoryAdd(lane, _, address, width, _) =>
        memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case StateWrite.AtomicMemoryMin(lane, _, address, width, _) =>
        memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case StateWrite.AtomicMemoryAddFloat(lane, _, address, width, _) =>
        memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case _ => Right(())
    }

  private def applyWrite(state: BlockState, memory: AddressMemory)(write: StateWrite): Unit = write match {
    case StateWrite.Register(lane, name, value)                      => state.registers(lane)(name) = value
    case StateWrite.Carry(lane, value)                               => state.carry(lane) = value
    case StateWrite.FunctionRegister(lane, name, value)              => state.functionRegisters(lane)(name) = value
    case StateWrite.Predicate(lane, name, value)                     => state.predicates(lane)(name) = value
    case StateWrite.Parameter(lane, name, value)                     => state.parameters(lane)(name) = value
    case StateWrite.ParameterBytes(lane, name, offset, width, value) =>
      state.writeParameterBytes(lane, name, offset, width, value)
    case StateWrite.FunctionParameter(lane, name, value)   => state.functionParameters(lane)(name) = value
    case StateWrite.ParameterBuffer(address, width, value) =>
      state.parameterBufferRange(address, byteCount(width)).foreach { case (bytes, offset) =>
        LittleEndian.write(value, bytes, offset, byteCount(width))
      }
    case StateWrite.Shared(_, address, width, value)   => state.writeShared(address, width, value)
    case StateWrite.Local(lane, address, width, value) => state.writeLocal(lane, address, width, value)
    case StateWrite.Memory(_, address, width, value)   =>
      memory.writeLittleEndian(address, value, byteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
    case StateWrite.AtomicSharedAdd(lane, destination, address, width, value) =>
      val previous = state.readShared(address, width)
      state.registers(lane)(destination) = truncate(previous, width)
      state.writeShared(address, width, previous + value)
    case StateWrite.AtomicSharedMin(lane, destination, address, width, value) =>
      val previous = state.readShared(address, width)
      state.registers(lane)(destination) = truncate(previous, width)
      val mask   = if (width >= 64) -1L else (1L << width) - 1L
      val result = Math.min(previous & mask, value & mask)
      state.writeShared(address, width, result)
    case StateWrite.AtomicMemoryAdd(lane, destination, address, width, value) =>
      val previous = memory.readLittleEndian(address, byteCount(width)).fold(
        error =>
          throw IllegalStateException(error.message),
        identity
      )
      state.registers(lane)(destination) = truncate(previous, width)
      memory.writeLittleEndian(address, previous + value, byteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
    case StateWrite.AtomicMemoryMin(lane, destination, address, width, value) =>
      val previous = memory.readLittleEndian(address, byteCount(width)).fold(
        error => throw IllegalStateException(error.message),
        identity
      )
      state.registers(lane)(destination) = truncate(previous, width)
      val mask   = if (width >= 64) -1L else (1L << width) - 1L
      val result = Math.min(previous & mask, value & mask)
      memory.writeLittleEndian(address, result, byteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
    case StateWrite.AtomicMemoryAddFloat(lane, destination, address, width, value) =>
      val previous = memory.readLittleEndian(address, byteCount(width)).fold(
        error => throw IllegalStateException(error.message),
        identity
      )
      val result = if (width == 32)
        IEEE754.floatToBinary32(IEEE754.binary32ToFloat(previous) + IEEE754.binary32ToFloat(value))
      else IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(previous) + IEEE754.binary64ToDouble(value))
      state.registers(lane)(destination) = truncate(previous, width)
      memory.writeLittleEndian(address, result, byteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
  }

  private def forEveryLane(context: ExecutionContext)(
      function: Int => Either[ExecutionFault, Unit]
  ): Either[ExecutionFault, Unit] = {
    var failure: Option[ExecutionFault] = None
    context.active.foreachLane { lane =>
      if (failure.isEmpty) function(lane).left.foreach(fault => failure = Some(fault))
    }
    failure.toLeft(())
  }

  // The emulator keeps per-lane local storage in the low virtual-address
  // range; host-visible CUDA allocations are projected above 4 GiB.
  private def isLocalAddress(address: Long): Boolean =
    java.lang.Long.compareUnsigned(address, 0x100000000L) < 0 && !isSharedAddress(address)

  private def isSharedAddress(address: Long): Boolean =
    java.lang.Long.compareUnsigned(address, 0x10000000L) >= 0 &&
      java.lang.Long.compareUnsigned(address, 0x40000000L) < 0

  private def floatUnaryBits(operation: FloatUnaryOperation, bits: Long, width: Int): Long =
    if (width == 16) {
      val halfBits = truncate(bits, 16)
      operation match {
        case FloatUnaryOperation.Negate   => halfBits ^ 0x8000L
        case FloatUnaryOperation.Absolute => halfBits & 0x7fffL
        case _                            =>
          val value  = IEEE754.binary16ToFloat(halfBits)
          val result = operation match {
            case FloatUnaryOperation.Negate               => -value
            case FloatUnaryOperation.Absolute             => Math.abs(value)
            case FloatUnaryOperation.Reciprocal           => 1.0f / value
            case FloatUnaryOperation.SquareRoot           => Math.sqrt(value.toDouble).toFloat
            case FloatUnaryOperation.ReciprocalSquareRoot => (1.0 / Math.sqrt(value.toDouble)).toFloat
            case FloatUnaryOperation.Exponent2            => Math.pow(2.0, value.toDouble).toFloat
            case FloatUnaryOperation.Log2                 => (Math.log(value.toDouble) / Math.log(2.0)).toFloat
            case FloatUnaryOperation.Sine                 => Math.sin(value.toDouble).toFloat
            case FloatUnaryOperation.Cosine               => Math.cos(value.toDouble).toFloat
            case FloatUnaryOperation.Tanh                 => Math.tanh(value.toDouble).toFloat
          }
          IEEE754.floatToBinary16(result)
      }
    } else if (width == 32) {
      val value  = IEEE754.binary32ToFloat(bits)
      val result = operation match {
        case FloatUnaryOperation.Negate               => return bits ^ (1L << 31)
        case FloatUnaryOperation.Absolute             => Math.abs(value)
        case FloatUnaryOperation.Reciprocal           => 1.0f / value
        case FloatUnaryOperation.SquareRoot           => Math.sqrt(value.toDouble).toFloat
        case FloatUnaryOperation.ReciprocalSquareRoot => (1.0 / Math.sqrt(value.toDouble)).toFloat
        case FloatUnaryOperation.Exponent2            => Math.pow(2.0, value.toDouble).toFloat
        case FloatUnaryOperation.Log2                 => (Math.log(value.toDouble) / Math.log(2.0)).toFloat
        case FloatUnaryOperation.Sine                 => Math.sin(value.toDouble).toFloat
        case FloatUnaryOperation.Cosine               => Math.cos(value.toDouble).toFloat
        case FloatUnaryOperation.Tanh                 => Math.tanh(value.toDouble).toFloat
      }
      IEEE754.floatToBinary32(result)
    } else {
      val value  = IEEE754.binary64ToDouble(bits)
      val result = operation match {
        case FloatUnaryOperation.Negate               => return bits ^ (1L << 63)
        case FloatUnaryOperation.Absolute             => Math.abs(value)
        case FloatUnaryOperation.Reciprocal           => 1.0 / value
        case FloatUnaryOperation.SquareRoot           => Math.sqrt(value)
        case FloatUnaryOperation.ReciprocalSquareRoot => 1.0 / Math.sqrt(value)
        case FloatUnaryOperation.Exponent2            => Math.pow(2.0, value)
        case FloatUnaryOperation.Log2                 => Math.log(value) / Math.log(2.0)
        case FloatUnaryOperation.Sine                 => Math.sin(value)
        case FloatUnaryOperation.Cosine               => Math.cos(value)
        case FloatUnaryOperation.Tanh                 => Math.tanh(value)
      }
      IEEE754.doubleToBinary64(result)
    }

  private def packedFloatValue(bits: Long, bf16: Boolean): Float =
    if (bf16) IEEE754.binary32ToFloat((truncate(bits, 16) << 16) & 0xffffffffL)
    else IEEE754.binary16ToFloat(bits)

  private def packedFloatBits(value: Float, bf16: Boolean): Long =
    if (bf16) bfloat16FromFloat(value, Some("rn")) else IEEE754.floatToBinary16(value)

  private def packedFmaBits(left: Long, right: Long, addend: Long, bf16: Boolean): Long = {
    val low = packedFloatBits(
      Math.fma(
        packedFloatValue(left, bf16),
        packedFloatValue(right, bf16),
        packedFloatValue(addend, bf16)
      ),
      bf16
    )
    val high = packedFloatBits(
      Math.fma(
        packedFloatValue(left >>> 16, bf16),
        packedFloatValue(right >>> 16, bf16),
        packedFloatValue(addend >>> 16, bf16)
      ),
      bf16
    )
    low | (high << 16)
  }

  private def packedBinaryBits(operation: FloatOperation, left: Long, right: Long, bf16: Boolean): Long = {
    def applyOperation(leftValue: Float, rightValue: Float): Float = operation match {
      case FloatOperation.Add      => leftValue + rightValue
      case FloatOperation.Subtract => leftValue - rightValue
      case FloatOperation.Multiply => leftValue * rightValue
      case FloatOperation.Divide   => leftValue / rightValue
      case FloatOperation.Minimum  => Math.min(leftValue, rightValue)
      case FloatOperation.Maximum  => Math.max(leftValue, rightValue)
      case FloatOperation.CopySign => Math.copySign(rightValue, leftValue)
    }
    val low  = packedFloatBits(applyOperation(packedFloatValue(left, bf16), packedFloatValue(right, bf16)), bf16)
    val high = packedFloatBits(
      applyOperation(packedFloatValue(left >>> 16, bf16), packedFloatValue(right >>> 16, bf16)),
      bf16
    )
    low | (high << 16)
  }

  private def bf16FmaBits(left: Long, right: Long, addend: Long): Long =
    bfloat16FromFloat(
      Math.fma(
        IEEE754.binary32ToFloat((truncate(left, 16) << 16) & 0xffffffffL),
        IEEE754.binary32ToFloat((truncate(right, 16) << 16) & 0xffffffffL),
        IEEE754.binary32ToFloat((truncate(addend, 16) << 16) & 0xffffffffL)
      ),
      Some("rn")
    )

  private def loadBits(value: Long, width: Int, signed: Boolean): Long = {
    val bits = truncate(value, width)
    if (signed) signExtend(bits, width) else bits
  }

  private def reverseBits(value: Long, width: Int): Long = {
    var result = 0L
    var index  = 0
    while (index < width) {
      if (((value >>> index) & 1L) != 0L) result |= 1L << (width - index - 1)
      index += 1
    }
    truncate(result, width)
  }

  private def bitFind(value: Long, width: Int, signed: Boolean, shiftAmount: Boolean): Long = {
    val bits   = truncate(value, width)
    val search =
      if (signed && ((bits >>> (width - 1)) & 1L) != 0L) truncate(~bits, width)
      else bits
    val position =
      if (search == 0L) -1
      else if (width == 32) 31 - Integer.numberOfLeadingZeros(search.toInt)
      else 63 - java.lang.Long.numberOfLeadingZeros(search)
    if (position < 0) 0xffffffffL
    else if (shiftAmount) (width - 1 - position).toLong
    else position.toLong
  }

  private def dotProduct4(
      left: Long,
      right: Long,
      addend: Long,
      leftSigned: Boolean,
      rightSigned: Boolean
  ): Long = {
    var result = signExtend(truncate(addend, 32), 32)
    var index  = 0
    while (index < 4) {
      val leftByte   = (left >>> (index * 8)) & 0xffL
      val rightByte  = (right >>> (index * 8)) & 0xffL
      val leftValue  = if (leftSigned && leftByte >= 0x80L) leftByte - 0x100L else leftByte
      val rightValue = if (rightSigned && rightByte >= 0x80L) rightByte - 0x100L else rightByte
      result += leftValue * rightValue
      index += 1
    }
    truncate(result, 32)
  }

  private def shuffleClamp(value: Long): Int =
    Math.min(value & 0xffffffffL, 31L).toInt

  private def extractBits(source: Long, start: Long, count: Long, width: Int, signed: Boolean): Long = {
    val sourceBits = truncate(source, width)
    val bitStart   = (start & 0xffL).toInt
    val bitCount   = (count & 0xffL).toInt
    if (bitStart >= width || bitCount == 0) 0L
    else {
      val extractedWidth = Math.min(bitCount, width - bitStart)
      val mask           = if (extractedWidth == 64) -1L else (1L << extractedWidth) - 1L
      val extracted      = (sourceBits >>> bitStart) & mask
      truncate(if (signed) signExtend(extracted, extractedWidth) else extracted, width)
    }
  }

  private def bitMask(start: Long, count: Long, mode: String): Long = {
    val rawStart         = start & 0xffffffffL
    val rawCount         = count & 0xffffffffL
    val start1           = rawStart & 0x1fL
    val count1           = rawCount & 0x1fL
    val positionOverflow = mode == "clamp" && rawStart >= 32L
    val widthOverflow    = mode == "clamp" && rawCount >= 32L
    val sum              = start1 + count1
    val mask0            = if (positionOverflow) 0L else truncate(-1L << start1.toInt, 32)
    val mask1            =
      if (sum >= 32L || positionOverflow || widthOverflow) 0L
      else if (count1 == 0L) 0xffffffffL
      else truncate(-1L << sum.toInt, 32)
    truncate(mask0 & ~mask1, 32)
  }

  private def multiplyHigh(left: Long, right: Long, width: Int, signed: Boolean): Long = {
    val leftValue =
      if (signed) BigInt(signExtend(left, width)) else BigInt(java.lang.Long.toUnsignedString(truncate(left, width)))
    val rightValue =
      if (signed) BigInt(signExtend(right, width)) else BigInt(java.lang.Long.toUnsignedString(truncate(right, width)))
    ((leftValue * rightValue) >> width).longValue
  }

  private def integerMultiplyAdd(
      left: Long,
      right: Long,
      addend: Long,
      operation: IntegerMultiplyAddOperation,
      carryIn: Boolean
  ): (Long, Boolean) = operation match {
    case IntegerMultiplyAddOperation.Low(operandWidth, resultWidth, usesCarry, _) =>
      val leftValue   = BigInt(java.lang.Long.toUnsignedString(truncate(left, operandWidth)))
      val rightValue  = BigInt(java.lang.Long.toUnsignedString(truncate(right, operandWidth)))
      val addendValue = BigInt(java.lang.Long.toUnsignedString(truncate(addend, resultWidth)))
      val sum         = leftValue * rightValue + addendValue + (if (usesCarry && carryIn) 1 else 0)
      (truncate(sum.longValue, resultWidth), (sum >> resultWidth) != 0)
    case IntegerMultiplyAddOperation.High(operandWidth, resultWidth, usesCarry, _) =>
      val leftValue   = BigInt(java.lang.Long.toUnsignedString(truncate(left, operandWidth)))
      val rightValue  = BigInt(java.lang.Long.toUnsignedString(truncate(right, operandWidth)))
      val addendValue = BigInt(java.lang.Long.toUnsignedString(truncate(addend, resultWidth)))
      val sum         = (leftValue * rightValue >> operandWidth) + addendValue + (if (usesCarry && carryIn) 1 else 0)
      (truncate(sum.longValue, resultWidth), (sum >> resultWidth) != 0)
  }

  private def integerAddWithCarry(
      left: Long,
      right: Long,
      width: Int,
      usesCarry: Boolean,
      carryIn: Boolean
  ): (Long, Boolean) = {
    val sum =
      BigInt(java.lang.Long.toUnsignedString(truncate(left, width))) +
        BigInt(java.lang.Long.toUnsignedString(truncate(right, width))) +
        (if (usesCarry && carryIn) 1 else 0)
    (truncate(sum.longValue, width), (sum >> width) != 0)
  }

  private def integerSubWithBorrow(
      left: Long,
      right: Long,
      width: Int,
      usesBorrow: Boolean,
      borrowIn: Boolean
  ): (Long, Boolean) = {
    val difference =
      BigInt(java.lang.Long.toUnsignedString(truncate(left, width))) -
        BigInt(java.lang.Long.toUnsignedString(truncate(right, width))) -
        (if (usesBorrow && borrowIn) 1 else 0)
    (truncate(difference.longValue, width), difference < 0)
  }

  private def insertBits(insert: Long, base: Long, start: Long, count: Long, width: Int): Long = {
    val bitStart = (start & 0xffL).toInt
    val bitCount = (count & 0xffL).toInt
    if (bitCount == 0 || bitStart >= width) truncate(base, width)
    else {
      val insertedWidth = Math.min(bitCount, width - bitStart)
      val fieldMask     = if (insertedWidth == 64) -1L else ((1L << insertedWidth) - 1L) << bitStart
      truncate((base & ~fieldMask) | ((insert & truncate(-1L, insertedWidth)) << bitStart), width)
    }
  }

  private def permuteBytes(left: Long, right: Long, control: Long, mode: String): Long = {
    val source = Array.tabulate(8) { index =>
      val value = if (index < 4) left else right
      ((value >>> (8 * (index & 3))) & 0xffL).toInt
    }
    val selector      = (control & 3L).toInt
    val selectedBytes = mode match {
      case ""     => Array.tabulate(4)(index => ((control >>> (4 * index)) & 7L).toInt)
      case "f4e"  => Array.tabulate(4)(index => (selector + index) & 7)
      case "b4e"  => Array.tabulate(4)(index => (selector - index) & 7)
      case "rc8"  => Array.fill(4)(selector)
      case "ecl"  => Array.tabulate(4)(index => Math.max(index, selector))
      case "ecr"  => Array.tabulate(4)(index => Math.min(index, selector))
      case "rc16" => Array.tabulate(4)(index => ((selector & 1) * 2) + (index & 1))
      case other  => throw new IllegalArgumentException(s"unsupported prmt mode: $other")
    }
    val result = (0 until 4).foldLeft(0L) { (bits, index) =>
      val selection  = selectedBytes(index)
      val selected   = source(selection)
      val signExtend = mode.isEmpty && ((control >>> (4 * index)) & 8L) != 0L
      val byte       = if (signExtend && (selected & 0x80) != 0) 0xff else selected
      bits | (byte.toLong << (8 * index))
    }
    truncate(result, 32)
  }

  private def divideInteger(left: Long, right: Long, width: Int, signed: Boolean): Long = {
    val leftBits  = truncate(left, width)
    val rightBits = truncate(right, width)
    if (rightBits == 0L) 0L
    else if (signed) {
      val dividend = signExtend(leftBits, width)
      val divisor  = signExtend(rightBits, width)
      if (dividend == Long.MinValue && divisor == -1L) Long.MinValue
      else dividend / divisor
    } else if (width == 64) java.lang.Long.divideUnsigned(leftBits, rightBits)
    else leftBits / rightBits
  }

  private def remainderInteger(left: Long, right: Long, width: Int, signed: Boolean): Long = {
    val leftBits  = truncate(left, width)
    val rightBits = truncate(right, width)
    if (rightBits == 0L) 0L
    else if (signed) {
      val dividend = signExtend(leftBits, width)
      val divisor  = signExtend(rightBits, width)
      if (dividend == Long.MinValue && divisor == -1L) 0L
      else dividend % divisor
    } else if (width == 64) java.lang.Long.remainderUnsigned(leftBits, rightBits)
    else leftBits % rightBits
  }

  private def compare(
      left: Long,
      right: Long,
      comparison: Comparison,
      kind: Char,
      width: Int
  ): Boolean = {
    val leftValue          = truncate(left, width)
    val rightValue         = truncate(right, width)
    val (order, unordered) =
      if (kind == 'f' || kind == 'b') {
        val leftFloat =
          if (kind == 'b') IEEE754.binary32ToFloat((leftValue << 16) & 0xffffffffL).toDouble
          else IEEE754.toDouble(leftValue, width)
        val rightFloat =
          if (kind == 'b') IEEE754.binary32ToFloat((rightValue << 16) & 0xffffffffL).toDouble
          else IEEE754.toDouble(rightValue, width)
        val order = if (leftFloat < rightFloat) -1 else if (leftFloat > rightFloat) 1 else 0
        order -> (leftFloat.isNaN || rightFloat.isNaN)
      } else {
        val integerOrder =
          if (kind == 's') java.lang.Long.compare(signExtend(leftValue, width), signExtend(rightValue, width))
          else if (width == 64) java.lang.Long.compareUnsigned(leftValue, rightValue)
          else java.lang.Long.compare(leftValue, rightValue)
        integerOrder -> false
      }
    comparison match {
      case Comparison.Ordered                     => !unordered
      case Comparison.Unordered                   => unordered
      case Comparison.Equal                       => !unordered && order == 0
      case Comparison.EqualUnordered              => unordered || order == 0
      case Comparison.NotEqual                    => !unordered && order != 0
      case Comparison.NotEqualUnordered           => unordered || order != 0
      case Comparison.LessThan                    => !unordered && order < 0
      case Comparison.LessThanUnordered           => unordered || order < 0
      case Comparison.LessThanOrEqual             => !unordered && order <= 0
      case Comparison.LessThanOrEqualUnordered    => unordered || order <= 0
      case Comparison.GreaterThan                 => !unordered && order > 0
      case Comparison.GreaterThanUnordered        => unordered || order > 0
      case Comparison.GreaterThanOrEqual          => !unordered && order >= 0
      case Comparison.GreaterThanOrEqualUnordered => unordered || order >= 0
    }
  }

  private def comparePredicates(left: Boolean, right: Boolean, comparison: Comparison): Boolean = comparison match {
    case Comparison.Ordered | Comparison.Unordered                              => false
    case Comparison.Equal | Comparison.EqualUnordered                           => left == right
    case Comparison.NotEqual | Comparison.NotEqualUnordered                     => left != right
    case Comparison.LessThan | Comparison.LessThanUnordered                     => !left && right
    case Comparison.LessThanOrEqual | Comparison.LessThanOrEqualUnordered       => !left || right
    case Comparison.GreaterThan | Comparison.GreaterThanUnordered               => left && !right
    case Comparison.GreaterThanOrEqual | Comparison.GreaterThanOrEqualUnordered => left || !right
  }

  private def convert(value: Long, sourceType: String, destinationType: String, rounding: Option[String]): Long = {
    val sourceWidth         = conversionTypeWidth(sourceType)
    val destinationWidth    = conversionTypeWidth(destinationType)
    val sourceIsBf16        = sourceType == "bf16"
    val destinationIsBf16   = destinationType == "bf16"
    val sourceKind          = if (sourceIsBf16) 'f' else sourceType.head
    val destinationKind     = if (destinationIsBf16) 'f' else destinationType.head
    lazy val floatingSource =
      if (sourceIsBf16) IEEE754.binary32ToFloat(truncate(value, 16) << 16).toDouble
      else IEEE754.toDouble(value, sourceWidth)

    if (destinationIsBf16) {
      val floating =
        if (sourceKind == 'f') floatingSource
        else {
          val sourceBits = truncate(value, sourceWidth)
          val integer    = if (sourceKind == 's') signExtend(sourceBits, sourceWidth) else sourceBits
          if (sourceKind == 'u' && sourceWidth == 64 && integer < 0)
            BigInt(java.lang.Long.toUnsignedString(integer)).toDouble
          else integer.toDouble
        }
      return bfloat16FromFloat(floating.toFloat, rounding)
    }
    if (sourceKind == 'f') {
      val floating = floatingSource
      if (destinationKind == 'f') {
        // The `i` variants round the floating-point value to an integral
        // value before converting it.  CUDA's device math uses
        // `cvt.rzi.f32.f32` for range reduction; treating it as an ordinary
        // same-width conversion leaves the fractional part in place and
        // changes the scale used by sinhf/coshf.
        val integral = rounding match {
          case Some("rzi") => Some(if (floating < 0.0) Math.ceil(floating) else Math.floor(floating))
          case Some("rmi") => Some(Math.floor(floating))
          case Some("rpi") => Some(Math.ceil(floating))
          case _           => None
        }
        IEEE754.fromDouble(integral.getOrElse(floating), destinationWidth)
      } else {
        val rounded = rounding match {
          case Some("rn") | Some("rni") => Math.rint(floating)
          case Some("rm") | Some("rmi") => Math.floor(floating)
          case Some("rp") | Some("rpi") => Math.ceil(floating)
          case _                        => if (floating < 0) Math.ceil(floating) else Math.floor(floating)
        }
        truncate(rounded.toLong, destinationWidth)
      }
    } else {
      val sourceBits = truncate(value, sourceWidth)
      val integer    =
        if (sourceKind == 's') signExtend(sourceBits, sourceWidth)
        else sourceBits
      if (destinationKind == 'f') {
        val floating =
          if (sourceKind == 'u' && sourceWidth == 64 && integer < 0)
            BigInt(java.lang.Long.toUnsignedString(integer)).toDouble
          else integer.toDouble
        IEEE754.fromDouble(floating, destinationWidth)
      } else truncate(integer, destinationWidth)
    }
  }

  private def bfloat16FromFloat(value: Float, rounding: Option[String]): Long = {
    val bits    = IEEE754.floatToBinary32(value)
    val upper   = (bits >>> 16) & 0xffffL
    val lower   = bits & 0xffffL
    val rounded = rounding match {
      case Some("rz") | Some("rzi")                                         => upper
      case Some("rm") | Some("rmi") if value < 0.0f && lower != 0L          => upper + 1L
      case Some("rp") | Some("rpi") if value > 0.0f && lower != 0L          => upper + 1L
      case _ if lower > 0x8000L || (lower == 0x8000L && (upper & 1L) != 0L) => upper + 1L
      case _                                                                => upper
    }
    truncate(rounded, 16)
  }

  private def conversionTypeWidth(name: String): Int = if (name == "bf16") 16 else name.drop(1).toInt
}

final case class ExecutionError(message: String)
final case class ExecutionResult(trace: Trace)

@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection"
  )
)
object Interpreter {
  private final case class Cohort(pc: Pc, lanes: LaneMask)
  private final case class BarrierReduction(
      destination: String,
      predicate: String,
      operation: PredicateOperation,
      pc: Pc
  )
  private final case class Waiting(cohort: Cohort, key: WaitKey, reduction: Option[BarrierReduction] = None)

  private def containsAtomics(executable: Array[ExecutableInstruction]): Boolean = executable.exists {
    case _: ExecutableInstruction.AtomicAdd      => true
    case _: ExecutableInstruction.AtomicMin      => true
    case _: ExecutableInstruction.AtomicAddFloat => true
    case _                                       => false
  }

  def execute(
      kernel: Kernel,
      geometry: LaunchGeometry,
      arguments: Map[String, Argument],
      memory: AddressMemory,
      control: ExecutionControl = ExecutionControl.default,
      captureTrace: Boolean = true,
      initialParameterBuffers: Map[Long, Array[Byte]] = Map.empty,
      blockIndices: Option[Col[Int]] = None
  ): Either[ExecutionError, ExecutionResult] = {
    val laneCount = geometry.groupSize.volumeInt match {
      case Left(message) => return Left(ExecutionError(message))
      case Right(value)  => value
    }
    val groupCount = geometry.groupCount.volumeInt match {
      case Left(message) => return Left(ExecutionError(message))
      case Right(value)  => value
    }
    val allLanes = LaneMask.all(laneCount) match {
      case Left(fault)  => return Left(ExecutionError(fault.message))
      case Right(value) => value
    }
    var trace                                              = Trace.empty
    var step                                               = 0L
    def runBlock(block: Int): Either[ExecutionError, Unit] =
      if (block < 0 || block >= groupCount) Left(ExecutionError(s"selected PTX block is outside the launch: $block"))
      else {
        val group = geometry.groupCoordinates(block.toLong)
        val live  = allLanes.filterLanes(lane =>
          geometry.containsGlobal(geometry.globalCoordinatesLong(group, geometry.localCoordinates(lane)))
        )
        executeBlock(
          kernel,
          block,
          group,
          geometry,
          live,
          arguments,
          memory,
          control,
          captureTrace,
          trace,
          step,
          initialParameterBuffers
        ) match {
          case Left(fault)                  => Left(ExecutionError(fault.message))
          case Right((nextTrace, nextStep)) =>
            trace = nextTrace
            step = nextStep
            Right(())
        }
      }
    val selectedBlocks = blockIndices.getOrElse(Col.from(0 until groupCount))
    val parallelBlocks = !captureTrace &&
      control.parallelism.shouldParallel(selectedBlocks.size.toLong, laneCount.toLong) &&
      !containsAtomics(kernel.program.instructions.toArray.map(_.instruction))
    if (parallelBlocks) {
      val result = control.parallelism.parallelFor(selectedBlocks.size.toLong) { index =>
        runBlock(selectedBlocks(index.toInt)).left.map(error => ExecutionFailure.Failed(error.message))
      }
      result match {
        case Left(error) => return Left(ExecutionError(error.message))
        case Right(())   => ()
      }
    } else {
      var index = 0
      while (index < selectedBlocks.size) {
        runBlock(selectedBlocks(index)) match {
          case Left(error) => return Left(error)
          case Right(_)    => ()
        }
        index += 1
      }
    }
    Right(ExecutionResult(trace))
  }

  private def executeBlock(
      kernel: Kernel,
      block: Int,
      group: Coordinates,
      geometry: LaunchGeometry,
      initialLive: LaneMask,
      arguments: Map[String, Argument],
      memory: AddressMemory,
      control: ExecutionControl,
      captureTrace: Boolean,
      initialTrace: Trace,
      initialStep: Long,
      initialParameterBuffers: Map[Long, Array[Byte]]
  ): Either[ExecutionFault, (Trace, Long)] = {
    import Semantics.*

    val laneCount = geometry.groupSize.volumeInt.fold(message => throw IllegalArgumentException(message), identity)
    val state     = BlockState(laneCount, initialLive)
    state.importParameterBuffers(initialParameterBuffers)
    kernel.parameters.foreach { parameter =>
      arguments.get(parameter.name).foreach(argument =>
        state.bindParameterBuffer(parameter.name, Col.toArray(argument.bytes))
      )
    }
    val ready                                  = ArrayBuffer.empty[Cohort]
    val waiting                                = ArrayBuffer.empty[Waiting]
    val barrierMasks                           = mutable.Map.empty[WaitKey, LaneMask]
    val warpBarrierMasks                       = mutable.Map.empty[WaitKey, Long]
    val effects                                = Effects[StateWrite]
    var trace                                  = initialTrace
    var traceStep                              = initialStep
    def enqueue(pc: Pc, lanes: LaneMask): Unit =
      if (lanes.nonEmpty) {
        val existing = ready.indexWhere(_.pc == pc)
        if (existing < 0) ready += Cohort(pc, lanes)
        else ready(existing) = Cohort(pc, ready(existing).lanes.union(lanes))
      }

    enqueue(Pc.Entry, state.live)

    def warpMemberMask(instruction: ExecutableInstruction): Option[Value] = instruction match {
      case ExecutableInstruction.ShuffleDown(_, _, _, _, _, memberMask, _)  => Some(memberMask)
      case ExecutableInstruction.ShuffleUp(_, _, _, _, _, memberMask, _)    => Some(memberMask)
      case ExecutableInstruction.ShuffleIndex(_, _, _, _, _, memberMask, _) => Some(memberMask)
      case ExecutableInstruction.ShuffleBfly(_, _, _, _, _, memberMask, _)  => Some(memberMask)
      case ExecutableInstruction.VoteBallot(_, _, memberMask)               => Some(memberMask)
      case ExecutableInstruction.VotePredicate(_, _, memberMask, _)         => Some(memberMask)
      case ExecutableInstruction.WarpReduce(_, _, memberMask, _, _)         => Some(memberMask)
      case _                                                                => None
    }

    def synchronizeWarpInstruction(
        executable: PredicatedInstruction,
        cohort: Cohort
    ): Either[ExecutionFault, (LaneMask, LaneMask)] = warpMemberMask(executable.instruction) match {
      case None             => Right(cohort.lanes -> LaneMask.Empty)
      case Some(memberMask) =>
        val context =
          ExecutionContext(kernel, block, group, geometry, state, cohort.pc, cohort.lanes, arguments, memory, control)
        val masks                           = mutable.Map.empty[Int, Long]
        var failure: Option[ExecutionFault] = None
        cohort.lanes.foreachLane { lane =>
          if (failure.isEmpty) {
            context.read(lane, memberMask) match {
              case Left(fault)  => failure = Some(fault)
              case Right(value) => masks(lane) = value
            }
          }
        }
        failure match {
          case Some(fault) => Left(fault)
          case None        =>
            var runnable = LaneMask.Empty
            var delayed  = LaneMask.Empty
            masks.foreach { case (lane, maskValue) =>
              val warpBase = lane & ~31
              val present  = cohort.lanes.filterLanes(candidate => (candidate & ~31) == warpBase)
              val expected = state.live.filterLanes { candidate =>
                (candidate & ~31) == warpBase &&
                ((maskValue & 0xffffffffL) & (1L << (candidate & 31))) != 0L
              }
              if (expected.subtract(present).nonEmpty)
                delayed = delayed.union(LaneMask.one(lane).fold(_ => LaneMask.Empty, identity))
              else runnable = runnable.union(LaneMask.one(lane).fold(_ => LaneMask.Empty, identity))
            }
            Right(runnable -> delayed)
        }
    }
    def advance(pc: Pc, lanes: LaneMask): Either[ExecutionFault, Unit] = pc.next.map(next => enqueue(next, lanes))

    def releaseBarrier(key: WaitKey): Either[ExecutionFault, Unit] = {
      val arrived  = barrierMasks.getOrElse(key, LaneMask.Empty)
      val expected = key.scope match {
        case WaitScope.Workgroup => state.live
        case WaitScope.Warp      =>
          val warpBase   = key.id.toInt
          val memberMask = warpBarrierMasks.getOrElse(key, -1L)
          state.live.filterLanes { lane =>
            (lane & ~31) == warpBase &&
            ((memberMask & 0xffffffffL) & (1L << (lane & 31))) != 0L
          }
        case _ => state.live
      }
      if (arrived.intersect(expected) == expected) {
        val released = Col.from(waiting.filter(_.key == key))
        waiting.filterInPlace(_.key != key)
        val _ = barrierMasks.remove(key)
        val _ = warpBarrierMasks.remove(key)
        released.iterator.flatMap(_.reduction).take(1).foreach { reduction =>
          var reduced: Either[ExecutionFault, Boolean] = Right(reduction.operation match {
            case PredicateOperation.And => true
            case PredicateOperation.Or  => false
            case PredicateOperation.Xor => false
          })
          arrived.foreachLane { lane =>
            reduced = reduced.flatMap { current =>
              state.predicates(lane).get(reduction.predicate).toRight(
                ExecutionFault.UndefinedRegister(reduction.predicate, reduction.pc, lane)
              ).map { value =>
                reduction.operation match {
                  case PredicateOperation.And => current && value
                  case PredicateOperation.Or  => current || value
                  case PredicateOperation.Xor => current ^ value
                }
              }
            }
          }
          reduced.foreach { value =>
            arrived.foreachLane(lane => state.predicates(lane)(reduction.destination) = value)
          }
        }
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
      if (ready.isEmpty) {
        val waits = waiting.map(value => s"${value.key.scope}:${value.key.id}@${value.key.phase}").distinct.sorted
        return Left(ExecutionFault.Deadlock(waits.mkString(", ")))
      }
      var cohort     = ready.remove(0)
      val executable = kernel.program.at(cohort.pc) match {
        case Left(fault)  => return Left(fault)
        case Right(value) => value
      }
      if (warpMemberMask(executable.instruction).nonEmpty) {
        var readyIndex = 0
        while (readyIndex < ready.size)
          if (ready(readyIndex).pc == cohort.pc) {
            cohort = cohort.copy(lanes = cohort.lanes.union(ready.remove(readyIndex).lanes))
          } else readyIndex += 1
      }
      synchronizeWarpInstruction(executable, cohort) match {
        case Left(fault)                => return Left(fault)
        case Right((runnable, delayed)) =>
          enqueue(cohort.pc, delayed)
          cohort = cohort.copy(lanes = runnable)
      }
      if (cohort.lanes.nonEmpty) {
        control.checkpoint(cohort.lanes.size) match {
          case Left(fault) => return Left(fault)
          case Right(())   => ()
        }
        var active                                   = cohort.lanes
        var predicateFailure: Option[ExecutionFault] = None
        val predicateIndex = executable.predication.map(predicate => PredicateFile.index(predicate.name)).getOrElse(-1)
        executable.predication.foreach { predicate =>
          active = cohort.lanes.filterLanes { lane =>
            val value = if (predicateIndex >= 0) state.predicates(lane).getSlot(predicateIndex)
            else
              state.predicates(lane).get(predicate.name)
            value match {
              case Some(predicateValue) => predicateValue != predicate.negated
              case None                 =>
                predicateFailure = Some(ExecutionFault.UndefinedRegister(predicate.name, cohort.pc, lane))
                false
            }
          }
        }
        predicateFailure match {
          case Some(fault) => return Left(fault)
          case None        => ()
        }
        val skipped = if (executable.predication.isEmpty) LaneMask.Empty else cohort.lanes.subtract(active)
        if (captureTrace)
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

        val context =
          ExecutionContext(kernel, block, group, geometry, state, cohort.pc, active, arguments, memory, control)

        effects.clear()
        val outcome =
          if (active.isEmpty) Right(Step.Advance)
          else executable.instruction.execute(context, effects)
        outcome match {
          case Left(fault) =>
            return Left(fault)
          case Right(Step.Trap(fault)) => return Left(fault)
          case Right(control)          =>
            val writes = if (captureTrace) effects.snapshot else Col.empty[StateWrite]
            effects.commit(validateWrite(memory, cohort.pc), applyWrite(state, memory)) match {
              case Left(fault) =>
                return Left(fault)
              case Right(()) =>
                if (captureTrace)
                  writes.foreach {
                    case StateWrite.Memory(lane, address, _, value) =>
                      trace = trace.append(TraceEvent.OutputWrite(traceStep, block, lane, address.value, value.toInt))
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
                val reduction = executable.instruction match {
                  case ExecutableInstruction.BarrierReduce(destination, _, predicate, operation) =>
                    Some(BarrierReduction(destination, predicate, operation, cohort.pc))
                  case _ => None
                }
                key.scope match {
                  case WaitScope.Warp =>
                    val lanesByWarp = mutable.Map.empty[Int, LaneMask]
                    active.foreachLane { lane =>
                      val warpBase = lane & ~31
                      lanesByWarp(warpBase) = lanesByWarp.getOrElse(warpBase, LaneMask.Empty).union(
                        LaneMask.one(lane).fold(fault => throw IllegalStateException(fault.message), identity)
                      )
                    }
                    val warps     = Col.from(lanesByWarp).sortBy(_._1)
                    var warpIndex = 0
                    while (warpIndex < warps.size) {
                      val (warpBase, warpLanes) = warps(warpIndex)
                      val warpKey               = WaitKey(WaitScope.Warp, warpBase.toLong, key.phase)
                      waiting += Waiting(cohort.copy(lanes = warpLanes), warpKey, reduction)
                      warpBarrierMasks(warpKey) = key.id
                      barrierMasks(warpKey) = barrierMasks.getOrElse(warpKey, LaneMask.Empty).union(warpLanes)
                      releaseBarrier(warpKey) match {
                        case Left(fault) => return Left(fault)
                        case Right(())   => ()
                      }
                      warpIndex += 1
                    }
                  case _ =>
                    waiting += Waiting(cohort.copy(lanes = active), key, reduction)
                    barrierMasks(key) = barrierMasks.getOrElse(key, LaneMask.Empty).union(active)
                    releaseBarrier(key) match {
                      case Left(fault) => return Left(fault)
                      case Right(())   => ()
                    }
                }
              case Step.Exit(mask) =>
                state.live = state.live.subtract(mask)
                advance(cohort.pc, skipped) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
                val keys     = Col.from(barrierMasks.keys)
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

  private def validateWrite(memory: AddressMemory, pc: Pc)(write: StateWrite): Either[ExecutionFault, Unit] =
    write match {
      case StateWrite.Memory(lane, address, width, _) =>
        val result = memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
        result
      case StateWrite.AtomicMemoryAdd(lane, _, address, width, _) =>
        memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case StateWrite.AtomicMemoryMin(lane, _, address, width, _) =>
        memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case StateWrite.AtomicMemoryAddFloat(lane, _, address, width, _) =>
        memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case _ => Right(())
    }

  private def applyWrite(state: BlockState, memory: AddressMemory)(write: StateWrite): Unit = write match {
    case StateWrite.Register(lane, name, value)                      => state.registers(lane)(name) = value
    case StateWrite.Carry(lane, value)                               => state.carry(lane) = value
    case StateWrite.FunctionRegister(lane, name, value)              => state.functionRegisters(lane)(name) = value
    case StateWrite.Predicate(lane, name, value)                     => state.predicates(lane)(name) = value
    case StateWrite.Parameter(lane, name, value)                     => state.parameters(lane)(name) = value
    case StateWrite.ParameterBytes(lane, name, offset, width, value) =>
      state.writeParameterBytes(lane, name, offset, width, value)
    case StateWrite.FunctionParameter(lane, name, value)   => state.functionParameters(lane)(name) = value
    case StateWrite.ParameterBuffer(address, width, value) =>
      state.parameterBufferRange(address, byteCount(width)).foreach { case (bytes, offset) =>
        LittleEndian.write(value, bytes, offset, byteCount(width))
      }
    case StateWrite.Shared(_, address, width, value)   => state.writeShared(address, width, value)
    case StateWrite.Local(lane, address, width, value) => state.writeLocal(lane, address, width, value)
    case StateWrite.Memory(_, address, width, value)   =>
      memory
        .writeLittleEndian(address, value, byteCount(width))
        .left
        .foreach(error => throw IllegalStateException(error.message))
    case StateWrite.AtomicSharedAdd(lane, destination, address, width, value) =>
      val previous = state.readShared(address, width)
      state.registers(lane)(destination) = truncate(previous, width)
      state.writeShared(address, width, previous + value)
    case StateWrite.AtomicSharedMin(lane, destination, address, width, value) =>
      val previous = state.readShared(address, width)
      state.registers(lane)(destination) = truncate(previous, width)
      val mask   = if (width >= 64) -1L else (1L << width) - 1L
      val result = Math.min(previous & mask, value & mask)
      state.writeShared(address, width, result)
    case StateWrite.AtomicMemoryAdd(lane, destination, address, width, value) =>
      val previous = memory.readLittleEndian(address, byteCount(width)).fold(
        error =>
          throw IllegalStateException(error.message),
        identity
      )
      state.registers(lane)(destination) = truncate(previous, width)
      memory.writeLittleEndian(address, previous + value, byteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
    case StateWrite.AtomicMemoryMin(lane, destination, address, width, value) =>
      val previous = memory.readLittleEndian(address, byteCount(width)).fold(
        error => throw IllegalStateException(error.message),
        identity
      )
      state.registers(lane)(destination) = truncate(previous, width)
      val mask   = if (width >= 64) -1L else (1L << width) - 1L
      val result = Math.min(previous & mask, value & mask)
      memory.writeLittleEndian(address, result, byteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
    case StateWrite.AtomicMemoryAddFloat(lane, destination, address, width, value) =>
      val previous = memory.readLittleEndian(address, byteCount(width)).fold(
        error => throw IllegalStateException(error.message),
        identity
      )
      val result = if (width == 32)
        IEEE754.floatToBinary32(IEEE754.binary32ToFloat(previous) + IEEE754.binary32ToFloat(value))
      else IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(previous) + IEEE754.binary64ToDouble(value))
      state.registers(lane)(destination) = truncate(previous, width)
      memory.writeLittleEndian(address, result, byteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
  }

}
