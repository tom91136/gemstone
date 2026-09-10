package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*

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
