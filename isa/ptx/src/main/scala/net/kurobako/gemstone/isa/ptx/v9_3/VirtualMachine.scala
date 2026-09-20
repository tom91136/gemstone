package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import net.kurobako.gemstone.{NoNul, Vec}

import net.kurobako.gemstone.binary.{
  BitWidth as BinaryBitWidth,
  ByteAlignedBitWidth,
  FloatingPointWidth,
  LittleEndian,
  ScalarByteWidth
}
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AddressMemory
import net.kurobako.gemstone.memory.GuestAddress

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import cats.data.Chain

import scala.collection.mutable
import scala.reflect.ClassTag

final class VirtualMachine(
    kernel: VirtualMachine.Kernel,
    geometry: LaunchGeometry,
    arguments: Map[ParameterName, VirtualMachine.Argument],
    memory: AddressMemory,
    control: ExecutionControl = ExecutionControl.default,
    captureTrace: Boolean = true,
    initialParameterBuffers: Map[Long, Array[Byte]] = Map.empty,
    blockIndices: Option[Vec[Int]] = None
) {
  def execute(): Either[VirtualMachine.Error, VirtualMachine.Result] =
    VMInterpreter.execute(
      kernel,
      geometry,
      arguments,
      memory,
      control,
      captureTrace,
      initialParameterBuffers,
      blockIndices
    )
}

object VirtualMachine {

  final case class Trace private (private val eventChain: Chain[Trace.Event]) {
    def events: Vec[Trace.Event]          = Vec.from(eventChain.iterator)
    def append(event: Trace.Event): Trace = Trace(eventChain.append(event))
    def jsonLines: String                 = eventChain.iterator.map(_.toJSON).mkString("\n")
  }

  object Trace {
    enum EventType(val serialized: String) {
      case Instruction       extends EventType("instruction")
      case OutputWrite       extends EventType("output-write")
      case SchedulerDecision extends EventType("scheduler-decision")
    }

    enum Event(val step: Long, val eventType: EventType, fields: Vec[(String, String)]) {
      case Instruction(
          override val step: Long,
          kernel: KernelName,
          block: Int,
          lane: Int,
          pc: Pc,
          opcode: Opcode
      ) extends Event(
            step,
            EventType.Instruction,
            Vec(
              "kernel" -> kernel.value,
              "block"  -> block.toString,
              "lane"   -> lane.toString,
              "pc"     -> pc.value.toString,
              "opcode" -> opcode.value
            )
          )

      case OutputWrite(
          override val step: Long,
          block: Int,
          lane: Int,
          address: GuestAddress,
          value: Int
      ) extends Event(
            step,
            EventType.OutputWrite,
            Vec(
              "block"   -> block.toString,
              "lane"    -> lane.toString,
              "address" -> java.lang.Long.toUnsignedString(address.value),
              "value"   -> value.toString
            )
          )

      case SchedulerDecision(override val step: Long, block: Int, lane: Int)
          extends Event(
            step,
            EventType.SchedulerDecision,
            Vec(
              "block" -> block.toString,
              "lane"  -> lane.toString
            )
          )

      final def toJSON: String = {
        val payload = Vec("event" -> eventType.serialized, "step" -> step.toString) ++ fields
        payload.map { case (key, value) => s"\"${escape(key)}\":\"${escape(value)}\"" }.mkString(
          "{",
          ",",
          "}"
        )
      }
    }

    private def escape(value: String): String = value.flatMap {
      case '"'  => "\\\""
      case '\\' => "\\\\"
      case '\n' => "\\n"
      case '\r' => "\\r"
      case '\t' => "\\t"
      case char => char.toString
    }

    val empty: Trace = Trace(Chain.empty)
  }

  final case class Error(message: String)
  final case class Result(trace: Trace)

  def execute(
      kernel: Kernel,
      geometry: LaunchGeometry,
      arguments: Map[ParameterName, Argument],
      memory: AddressMemory,
      control: ExecutionControl = ExecutionControl.default,
      captureTrace: Boolean = true,
      initialParameterBuffers: Map[Long, Array[Byte]] = Map.empty,
      blockIndices: Option[Vec[Int]] = None
  ): Either[Error, Result] =
    VirtualMachine(
      kernel,
      geometry,
      arguments,
      memory,
      control,
      captureTrace,
      initialParameterBuffers,
      blockIndices
    ).execute()

  type BitWidth = BitWidth.T
  object BitWidth extends RefinedType[Int, Interval.Closed[8, 64] & Multiple[8]] {
    extension (width: BitWidth) {
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def asBitWidth: BinaryBitWidth = BinaryBitWidth.assume(width.value)
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def asByteAlignedBitWidth: ByteAlignedBitWidth = ByteAlignedBitWidth.assume(width.value)
    }
  }

  type FloatWidth = FloatWidth.T
  object FloatWidth extends RefinedType[Int, In[(16, 32, 64)]] {
    extension (width: FloatWidth) {
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def asBitWidth: BinaryBitWidth = BinaryBitWidth.assume(width.value)
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def asByteAlignedBitWidth: ByteAlignedBitWidth = ByteAlignedBitWidth.assume(width.value)
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def asFloatingPointWidth: FloatingPointWidth = FloatingPointWidth.assume(width.value)
    }
  }

  type PartWidth = PartWidth.T
  object PartWidth extends RefinedType[Int, In[(16, 32)]] {
    extension (width: PartWidth) {
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def asBitWidth: BinaryBitWidth = BinaryBitWidth.assume(width.value)

      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def combined: BitWidth = BitWidth.assume(width.value * 2)
    }
  }

  enum Value {
    case Register(name: RegisterName)
    case RegisterSlot(index: Int)
    case Predicate(name: RegisterName)
    case PredicateSlot(index: Int)
    case Function(name: FunctionName)
    case Symbol(name: SymbolName)
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

  enum BitMaskMode(val mnemonic: String) {
    case Clamp extends BitMaskMode("clamp")
    case Wrap  extends BitMaskMode("wrap")
  }

  enum BytePermuteMode(val suffix: String) {
    case Default extends BytePermuteMode("")
    case F4E     extends BytePermuteMode("f4e")
    case B4E     extends BytePermuteMode("b4e")
    case RC8     extends BytePermuteMode("rc8")
    case ECL     extends BytePermuteMode("ecl")
    case ECR     extends BytePermuteMode("ecr")
    case RC16    extends BytePermuteMode("rc16")
  }

  enum ConversionType(val mnemonic: String, val width: BitWidth, val kind: Char) {
    case B8   extends ConversionType("b8", BitWidth(8), 'b')
    case S8   extends ConversionType("s8", BitWidth(8), 's')
    case U8   extends ConversionType("u8", BitWidth(8), 'u')
    case B16  extends ConversionType("b16", BitWidth(16), 'b')
    case S16  extends ConversionType("s16", BitWidth(16), 's')
    case U16  extends ConversionType("u16", BitWidth(16), 'u')
    case F16  extends ConversionType("f16", BitWidth(16), 'f')
    case BF16 extends ConversionType("bf16", BitWidth(16), 'f')
    case B32  extends ConversionType("b32", BitWidth(32), 'b')
    case S32  extends ConversionType("s32", BitWidth(32), 's')
    case U32  extends ConversionType("u32", BitWidth(32), 'u')
    case F32  extends ConversionType("f32", BitWidth(32), 'f')
    case B64  extends ConversionType("b64", BitWidth(64), 'b')
    case S64  extends ConversionType("s64", BitWidth(64), 's')
    case U64  extends ConversionType("u64", BitWidth(64), 'u')
    case F64  extends ConversionType("f64", BitWidth(64), 'f')
  }

  enum ConversionRounding(val mnemonic: String) {
    case RN  extends ConversionRounding("rn")
    case RNI extends ConversionRounding("rni")
    case RZ  extends ConversionRounding("rz")
    case RZI extends ConversionRounding("rzi")
    case RM  extends ConversionRounding("rm")
    case RMI extends ConversionRounding("rmi")
    case RP  extends ConversionRounding("rp")
    case RPI extends ConversionRounding("rpi")
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
    case Low(operandWidth: BitWidth, resultWidth: BitWidth, carryIn: Boolean, carryOut: Boolean)
    case High(operandWidth: BitWidth, resultWidth: BitWidth, carryIn: Boolean, carryOut: Boolean)
  }

  enum ExecutableInstruction(private val opcodeText: String) {
    final val opcode: Opcode = Opcode.applyUnsafe(opcodeText)

    case LoadParameter(
        destinations: Vec[RegisterName],
        parameter: ParameterName,
        offset: Int :| Positive0,
        elementWidth: BitWidth,
        signed: Boolean
    ) extends ExecutableInstruction(s"ld.param.${if (signed) "s" else "u"}$elementWidth")

    case StoreParameter(parameter: ParameterName, offset: Int :| Positive0, source: Value, elementWidth: BitWidth)
        extends ExecutableInstruction(s"st.param.u$elementWidth")

    case LoadParameterIndirect(destination: RegisterName, address: Value, width: BitWidth, signed: Boolean)
        extends ExecutableInstruction(s"ld.param.${if (signed) "s" else "u"}$width")

    case LoadParameterIndirectVector(
        destinations: Vec[RegisterName],
        address: Value,
        width: BitWidth,
        signed: Boolean
    ) extends ExecutableInstruction(s"ld.param.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case Move(destination: RegisterName, source: Value, width: BitWidth)
        extends ExecutableInstruction(s"mov.u$width")

    case MovePredicate(destination: RegisterName, source: Value) extends ExecutableInstruction("mov.pred")

    case MoveSplit(destinations: Vec[RegisterName], source: Value, partWidth: PartWidth)
        extends ExecutableInstruction(s"mov.b${partWidth.value * 2}")

    case MoveCombine(destination: RegisterName, sources: Vec[Value], partWidth: PartWidth)
        extends ExecutableInstruction(s"mov.b${partWidth.value * 2}")

    case IntegerUnary(operation: IntegerUnaryOperation, destination: RegisterName, source: Value, width: BitWidth)
        extends ExecutableInstruction(operation match {
          case IntegerUnaryOperation.Negate           => s"neg.b$width"
          case IntegerUnaryOperation.Absolute         => s"abs.b$width"
          case IntegerUnaryOperation.BitwiseNot       => s"not.b$width"
          case IntegerUnaryOperation.PopulationCount  => s"popc.b$width"
          case IntegerUnaryOperation.LeadingZeroCount => s"clz.b$width"
          case IntegerUnaryOperation.BitReverse       => s"brev.b$width"
        })

    case BitFind(
        destination: RegisterName,
        source: Value,
        width: BitWidth,
        signed: Boolean,
        shiftAmount: Boolean
    ) extends ExecutableInstruction(s"bfind.${if (shiftAmount) "shiftamt." else ""}${if (signed) "s" else "u"}$width")

    case DotProduct4(
        destination: RegisterName,
        left: Value,
        right: Value,
        addend: Value,
        leftSigned: Boolean,
        rightSigned: Boolean
    ) extends ExecutableInstruction(s"dp4a.${if (leftSigned) "s" else "u"}32.${if (rightSigned) "s" else "u"}32")

    case IntegerBinary(
        operation: IntegerOperation,
        destination: RegisterName,
        left: Value,
        right: Value,
        width: BitWidth
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
        destination: RegisterName,
        left: Value,
        right: Value,
        width: BitWidth,
        carryIn: Boolean,
        carryOut: Boolean
    ) extends ExecutableInstruction(s"add${if (carryIn) "c" else ""}${if (carryOut) ".cc" else ""}.u$width")

    case IntegerSubWithBorrow(
        destination: RegisterName,
        left: Value,
        right: Value,
        width: BitWidth,
        borrowIn: Boolean,
        borrowOut: Boolean
    ) extends ExecutableInstruction(s"sub${if (borrowIn) "c" else ""}${if (borrowOut) ".cc" else ""}.u$width")

    case MultiplyAdd(
        destination: RegisterName,
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
        destination: RegisterName,
        left: Value,
        right: Value,
        width: FloatWidth
    ) extends ExecutableInstruction(s"${operation.toString.toLowerCase}.f$width")

    case FloatBinaryPacked(
        operation: FloatOperation,
        destination: RegisterName,
        left: Value,
        right: Value,
        bf16: Boolean
    ) extends ExecutableInstruction(s"${operation.toString.toLowerCase}.${if (bf16) "bf16x2" else "f16x2"}")

    case FloatUnary(operation: FloatUnaryOperation, destination: RegisterName, source: Value, width: FloatWidth)
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

    case FusedMultiplyAdd(
        destination: RegisterName,
        left: Value,
        right: Value,
        addend: Value,
        width: FloatWidth
    ) extends ExecutableInstruction(s"fma.f$width")

    case FusedMultiplyAddBf16(destination: RegisterName, left: Value, right: Value, addend: Value)
        extends ExecutableInstruction("fma.rn.bf16")

    case FusedMultiplyAddPacked(
        destination: RegisterName,
        left: Value,
        right: Value,
        addend: Value,
        bf16: Boolean
    ) extends ExecutableInstruction(s"fma.rn.${if (bf16) "bf16x2" else "f16x2"}")

    case ShuffleDown(
        destination: RegisterName,
        validity: Option[RegisterName],
        source: Value,
        delta: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends ExecutableInstruction(s"shfl.sync.down.b$width")

    case ShuffleUp(
        destination: RegisterName,
        validity: Option[RegisterName],
        source: Value,
        delta: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends ExecutableInstruction(s"shfl.sync.up.b$width")

    case ShuffleIndex(
        destination: RegisterName,
        validity: Option[RegisterName],
        source: Value,
        index: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends ExecutableInstruction(s"shfl.sync.idx.b$width")

    case ShuffleBfly(
        destination: RegisterName,
        validity: Option[RegisterName],
        source: Value,
        index: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends ExecutableInstruction(s"shfl.sync.bfly.b$width")

    case VoteBallot(destination: RegisterName, predicate: RegisterName, memberMask: Value)
        extends ExecutableInstruction("vote.sync.ballot.b32")

    case VotePredicate(
        destination: RegisterName,
        predicate: RegisterName,
        memberMask: Value,
        operation: VoteOperation
    ) extends ExecutableInstruction(s"vote.sync.${operation.toString.toLowerCase}.pred")

    case BitFieldExtract(
        destination: RegisterName,
        source: Value,
        start: Value,
        widthValue: Value,
        width: BitWidth,
        signed: Boolean
    ) extends ExecutableInstruction(s"bfe.${if (signed) "s" else "u"}$width")

    case BitFieldInsert(
        destination: RegisterName,
        insertValue: Value,
        baseValue: Value,
        bitPosition: Value,
        bitLength: Value,
        width: BitWidth
    ) extends ExecutableInstruction(s"bfi.b$width")

    case BitMask(destination: RegisterName, start: Value, widthValue: Value, mode: BitMaskMode)
        extends ExecutableInstruction(s"bmsk.${mode.mnemonic}.b32")

    case BytePermute(
        destination: RegisterName,
        left: Value,
        right: Value,
        control: Value,
        mode: BytePermuteMode
    ) extends ExecutableInstruction(s"prmt.b32${if (mode == BytePermuteMode.Default) "" else s".${mode.suffix}"}")

    case Select(
        destination: RegisterName,
        whenTrue: Value,
        whenFalse: Value,
        predicate: RegisterName,
        width: BitWidth
    ) extends ExecutableInstruction(s"selp.b$width")

    case Convert(
        destination: RegisterName,
        source: Value,
        sourceType: ConversionType,
        destinationType: ConversionType,
        rounding: Option[ConversionRounding]
    ) extends ExecutableInstruction(
          s"cvt.${rounding.map(_.mnemonic + ".").getOrElse("")}${destinationType.mnemonic}.${sourceType.mnemonic}"
        )

    case LoadGlobal(destination: RegisterName, address: Value, width: BitWidth, signed: Boolean)
        extends ExecutableInstruction(s"ld.global.${if (signed) "s" else "u"}$width")

    case LoadGeneric(destination: RegisterName, address: Value, width: BitWidth, signed: Boolean)
        extends ExecutableInstruction(s"ld.generic.${if (signed) "s" else "u"}$width")

    case DiscardGlobalLoad(address: Value, width: BitWidth)
        extends ExecutableInstruction(s"ld.global.nc.u$width")

    case LoadGlobalVector(destinations: Vec[RegisterName], address: Value, width: BitWidth, signed: Boolean)
        extends ExecutableInstruction(s"ld.global.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case LoadGenericVector(destinations: Vec[RegisterName], address: Value, width: BitWidth, signed: Boolean)
        extends ExecutableInstruction(s"ld.generic.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case StoreGlobal(address: Value, source: Value, width: BitWidth)
        extends ExecutableInstruction(s"st.global.u$width")

    case StoreGeneric(address: Value, source: Value, width: BitWidth)
        extends ExecutableInstruction(s"st.generic.u$width")

    case StoreGlobalVector(address: Value, sources: Vec[RegisterName], width: BitWidth)
        extends ExecutableInstruction(s"st.global.v${sources.size}.u$width")

    case StoreGenericVector(address: Value, sources: Vec[RegisterName], width: BitWidth)
        extends ExecutableInstruction(s"st.generic.v${sources.size}.u$width")

    case LoadShared(destination: RegisterName, address: Value, width: BitWidth, signed: Boolean)
        extends ExecutableInstruction(s"ld.shared.${if (signed) "s" else "u"}$width")

    case LoadSharedVector(destinations: Vec[RegisterName], address: Value, width: BitWidth, signed: Boolean)
        extends ExecutableInstruction(s"ld.shared.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case LoadLocal(destination: RegisterName, address: Value, width: BitWidth, signed: Boolean)
        extends ExecutableInstruction(s"ld.local.${if (signed) "s" else "u"}$width")

    case LoadLocalVector(destinations: Vec[RegisterName], address: Value, width: BitWidth, signed: Boolean)
        extends ExecutableInstruction(s"ld.local.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case StoreShared(address: Value, source: Value, width: BitWidth)
        extends ExecutableInstruction(s"st.shared.u$width")

    case StoreSharedVector(address: Value, sources: Vec[RegisterName], width: BitWidth)
        extends ExecutableInstruction(s"st.shared.v${sources.size}.u$width")

    case StoreLocal(address: Value, source: Value, width: BitWidth)
        extends ExecutableInstruction(s"st.local.u$width")

    case StoreLocalVector(address: Value, sources: Vec[RegisterName], width: BitWidth)
        extends ExecutableInstruction(s"st.local.v${sources.size}.u$width")

    /** Synchronous model of the PTX asynchronous shared-memory copy. The emulator applies the copy at this instruction;
      * commit/wait operations below remain ordered no-ops because there is no separate copy engine.
      */
    case AsyncCopy(destination: Value, source: Value, size: Int :| Positive)
        extends ExecutableInstruction("cp.async")

    case AsyncCommit extends ExecutableInstruction("cp.async.commit_group")

    case AsyncWait extends ExecutableInstruction("cp.async.wait_group")

    /** Cache prefetch hints have no architecturally visible effect in the emulator; preserve them as ordered no-ops so
      * generated kernels remain loadable and predication is still honoured.
      */
    case Prefetch extends ExecutableInstruction("prefetch")

    case SetPredicate(
        destination: RegisterName,
        comparison: Comparison,
        kind: Char,
        left: Value,
        right: Value,
        width: BitWidth
    ) extends ExecutableInstruction("setp")

    case PredicateCompare(
        destination: RegisterName,
        comparison: Comparison,
        left: RegisterName,
        right: RegisterName
    ) extends ExecutableInstruction("setp.pred")

    case PredicateUnary(destination: RegisterName, source: RegisterName)
        extends ExecutableInstruction("not.pred")

    case PredicateBinary(
        operation: PredicateOperation,
        destination: RegisterName,
        left: RegisterName,
        right: RegisterName
    ) extends ExecutableInstruction(s"${operation.toString.toLowerCase}.pred")

    case Branch(target: Pc) extends ExecutableInstruction("bra")

    case BarrierSync(id: Int :| Positive0) extends ExecutableInstruction("bar.sync")

    case BarrierReduce(
        destination: RegisterName,
        id: Int :| Positive0,
        predicate: RegisterName,
        operation: PredicateOperation
    ) extends ExecutableInstruction(s"bar.red.${operation.toString.toLowerCase}.pred")

    case AtomicAdd(destination: RegisterName, address: Value, source: Value, width: BitWidth, global: Boolean)
        extends ExecutableInstruction(s"atom.${if (global) "global" else "shared"}.add.u$width")

    case AtomicMin(destination: RegisterName, address: Value, source: Value, width: BitWidth, global: Boolean)
        extends ExecutableInstruction(s"atom.${if (global) "global" else "shared"}.min.u$width")

    case AtomicAddFloat(destination: RegisterName, address: Value, source: Value, width: FloatWidth)
        extends ExecutableInstruction(s"atom.global.add.f$width")

    case WarpReduce(
        destination: RegisterName,
        source: Value,
        memberMask: Value,
        operation: IntegerOperation,
        width: BitWidth
    ) extends ExecutableInstruction("redux.sync")

    case MemoryBarrier extends ExecutableInstruction("membar")

    case WarpBarrier(memberMask: Value) extends ExecutableInstruction("bar.warp.sync")

    case Call(function: Value, returnParameters: Vec[ParameterName], parameters: Vec[ParameterName])
        extends ExecutableInstruction("call")

    case Trap extends ExecutableInstruction("trap")

    case Return extends ExecutableInstruction("ret")
  }

  final case class ExecutionPredicate(name: RegisterName, negated: Boolean)

  final case class PredicatedInstruction(instruction: ExecutableInstruction, predication: Option[ExecutionPredicate]) {
    def opcode: Opcode = instruction.opcode
  }

  final case class KernelParameter(name: ParameterName, widthBytes: Int :| Positive)
  final case class Argument(bytes: Vec[Byte] :| MinLength[1])

  final case class DeviceFunction(
      name: FunctionName,
      parameters: Vec[ParameterName],
      parameterWidths: Vec[Int :| Positive],
      parameterOffsets: Vec[Int :| Positive0],
      returnParameters: Vec[ParameterName],
      program: ExecutableProgram[PredicatedInstruction],
      sharedSymbols: Map[SymbolName, Long]
  )

  object Argument {
    def apply(value: Long): Argument =
      Argument(LittleEndian.toVec(value, ScalarByteWidth(8)).refineUnsafe[MinLength[1]])
  }

  final case class Kernel(
      name: KernelName,
      parameters: Vec[KernelParameter],
      program: ExecutableProgram[PredicatedInstruction],
      functions: Map[FunctionName, DeviceFunction] = Map.empty,
      sharedSymbols: Map[SymbolName, Long] = Map.empty,
      globalSymbols: Map[SymbolName, GuestAddress] = Map.empty
  ) {
    def instructions: Vec[ExecutableInstruction] = program.instructions.map(_.instruction)
  }

  type Pc = Pc.T
  object Pc extends RefinedType[Int, Positive0] {
    val Entry: Pc = Pc(0)

    extension (pc: Pc) {
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      def next: Either[ExecutionFault, Pc] =
        if (pc.value == Int.MaxValue) Left(ExecutionFault.InvalidProgram("program counter overflow"))
        else Right(Pc.assume(pc.value + 1))
    }
  }

  private[v9_3] final class LaneMaskData(val words: Array[Long], val count: Int, val limit: Int) {
    override def equals(other: Any): Boolean = other match {
      case that: LaneMaskData => count == that.count && java.util.Arrays.equals(words, that.words)
      case _                  => false
    }

    override def hashCode(): Int = 31 * count + java.util.Arrays.hashCode(words)
  }

  opaque type LaneMask = LaneMaskData

  object LaneMask {
    val MaximumLanes    = 1024
    val WordCount       = MaximumLanes / java.lang.Long.SIZE
    val Empty: LaneMask = LaneMaskData(Array.fill(WordCount)(0L), 0, 0)

    // Lane masks are compact hot-path bitsets; these loops avoid intermediate collections.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def all(laneCount: Int): Either[ExecutionFault, LaneMask] =
      if (laneCount < 1 || laneCount > MaximumLanes)
        Left(ExecutionFault.InvalidProgram(
          s"lane count must be between 1 and $MaximumLanes, found $laneCount"
        ))
      else {
        val words = Array.fill(WordCount)(0L)
        var lane  = 0
        while (lane < laneCount) {
          words(lane >>> 6) |= 1L << (lane & 63)
          lane += 1
        }
        Right(LaneMaskData(words, laneCount, (laneCount + 63) >>> 6))
      }

    def one(lane: Int): Either[ExecutionFault, LaneMask] =
      if (lane < 0 || lane >= MaximumLanes)
        Left(ExecutionFault.InvalidProgram(
          s"lane index must be between 0 and ${MaximumLanes - 1}, found $lane"
        ))
      else {
        val words = Array.fill(WordCount)(0L)
        words(lane >>> 6) = 1L << (lane & 63)
        Right(LaneMaskData(words, 1, (lane >>> 6) + 1))
      }

    def fromBits(value: Long): LaneMask = {
      val words = Array.fill(WordCount)(0L)
      words(0) = value
      LaneMaskData(words, java.lang.Long.bitCount(value), if (value == 0L) 0 else 1)
    }

    extension (mask: LaneMask) {
      def isEmpty: Boolean             = mask.count == 0
      def nonEmpty: Boolean            = mask.count != 0
      def size: Int                    = mask.count
      def contains(lane: Int): Boolean =
        lane >= 0 && lane < MaximumLanes && (mask.words(lane >>> 6) & (1L << (lane & 63))) != 0L

      def warpBits(lane: Int): Long =
        if (lane < 0 || lane >= MaximumLanes) 0L
        else (mask.words(lane >>> 6) >>> (lane & 32)) & 0xffffffffL

      def intersect(other: LaneMask): LaneMask =
        if (mask.count == 0 || other.count == 0) Empty
        else combine(other, (left, right) => left & right)

      def union(other: LaneMask): LaneMask =
        if (mask.count == 0) other
        else if (other.count == 0) mask
        else combine(other, (left, right) => left | right)

      def subtract(other: LaneMask): LaneMask =
        if (mask.count == 0 || other.count == 0) mask
        else combine(other, (left, right) => left & ~right)

      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      inline def filterLanes(inline predicate: Int => Boolean): LaneMask =
        if (mask.count == 0) Empty
        else {
          val words = Array.fill(WordCount)(0L)
          var count = 0
          foreachLane { lane =>
            if (predicate(lane)) {
              words(lane >>> 6) |= 1L << (lane & 63)
              count += 1
            }
          }
          if (count == 0) Empty
          else {
            var limit = WordCount
            while (words(limit - 1) == 0L) limit -= 1
            LaneMaskData(words, count, limit)
          }
        }

      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      inline def foreachLane(inline function: Int => Unit): Unit = {
        var wordIndex = 0
        while (wordIndex < mask.limit) {
          var word = mask.words(wordIndex)
          while (word != 0L) {
            val bit = java.lang.Long.numberOfTrailingZeros(word)
            function((wordIndex << 6) + bit)
            word &= word - 1L
          }
          wordIndex += 1
        }
      }

      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      private def combine(other: LaneMask, operation: (Long, Long) => Long): LaneMask = {
        val words      = Array.fill(WordCount)(0L)
        var count      = 0
        var limit      = 0
        var index      = 0
        val inputLimit = Math.max(mask.limit, other.limit)
        while (index < inputLimit) {
          val word = operation(mask.words(index), other.words(index))
          words(index) = word
          count += java.lang.Long.bitCount(word)
          if (word != 0L) limit = index + 1
          index += 1
        }
        if (count == 0) Empty else LaneMaskData(words, count, limit)
      }
    }
  }

  final case class ExecutableProgram[Instruction](instructions: Vec[Instruction]) {
    def size: Int = instructions.size

    def at(pc: Pc): Either[ExecutionFault, Instruction] =
      instructions.lift(pc.value).toRight(
        ExecutionFault.InvalidProgram(
          s"program counter ${pc.value} is outside a ${instructions.size}-instruction program"
        )
      )
  }

  enum WaitScope {
    case Warp
    case Workgroup
    case Device
    case System
    case External
  }

  final case class WaitKey(scope: WaitScope, id: Long, phase: Long)

  enum ExecutionFault {
    case Unsupported(opcode: Opcode, detail: String)
    case InvalidProgram(detail: String)
    case InvalidState(detail: String)
    case UndefinedRegister(name: RegisterName, pc: Pc, lane: Int)
    case Memory(detail: String)
    case ArchitecturalTrap(isa: String :| (MinLength[1] & NoNul), code: Long, pc: Pc)
    case BudgetExceeded(limit: Long)
    case Cancelled
    case Deadlock(detail: String)

    def message: String = this match {
      case Unsupported(opcode, detail)       => s"unsupported instruction $opcode: $detail"
      case InvalidProgram(detail)            => s"invalid program: $detail"
      case InvalidState(detail)              => s"invalid execution state: $detail"
      case UndefinedRegister(name, pc, lane) => s"lane $lane: undefined register $name at pc ${pc.value}"
      case Memory(detail)                    => detail
      case ArchitecturalTrap(isa, code, pc)  => s"$isa trap $code at pc ${pc.value}"
      case BudgetExceeded(limit)             => s"instruction budget of $limit was exhausted"
      case Cancelled                         => "execution was cancelled"
      case Deadlock(detail)                  => s"execution deadlocked: $detail"
    }
  }

  object ExecutionFault {
    def fromControl(failure: ExecutionControlFailure): ExecutionFault = failure match {
      case ExecutionControlFailure.BudgetExceeded(limit) => ExecutionFault.BudgetExceeded(limit)
      case ExecutionControlFailure.Cancelled             => ExecutionFault.Cancelled
    }
  }

  enum Step {
    case Advance
    case Jump(target: Pc)
    case Split(taken: LaneMask, target: Pc, fallthrough: LaneMask)
    case Suspend(key: WaitKey)
    case Exit(mask: LaneMask)
    case Trap(fault: ExecutionFault)
  }

  private[v9_3] trait Write extends Product with Serializable

  private[v9_3] final class Effects[W <: Write: ClassTag] {
    private val pending = mutable.ArrayBuffer.empty[W]

    def stage(write: W): Unit = pending += write
    def isEmpty: Boolean      = pending.isEmpty
    def size: Int             = pending.size
    def snapshot: Vec[W]      = Vec.from(pending)
    def clear(): Unit         = pending.clear()

    @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
    def commit(
        validate: W => Either[ExecutionFault, Unit],
        applyWrite: W => Unit
    ): Either[ExecutionFault, Unit] = {
      var index = 0
      while (index < pending.size) {
        validate(pending(index)) match {
          case Left(fault) =>
            pending.clear()
            return Left(fault)
          case Right(()) => ()
        }
        index += 1
      }
      index = 0
      while (index < pending.size) {
        applyWrite(pending(index))
        index += 1
      }
      pending.clear()
      Right(())
    }
  }

}
