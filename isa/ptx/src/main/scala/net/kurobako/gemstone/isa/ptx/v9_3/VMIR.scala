package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Constraint.{IntBetween, OneOf}

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}
import scala.compiletime.{constValue, error}

import Program.*

import net.kurobako.gemstone.NonEmptyVec
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.Constraint.MultipleIn
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.vm.LaunchGeometry.Axis

private[v9_3] object VMIR {

  opaque type BitWidth = Int
  object BitWidth {
    inline def either(value: Int): Either[String, BitWidth] =
      if (value >= 8 && value <= 64 && value % 8 == 0) Right(value)
      else Left(s"bit width must be a multiple of 8 between 8 and 64: $value")

    inline def unsafe(value: Int): BitWidth =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Int & Singleton](inline value: A): BitWidth =
      inline if (constValue[A] >= 8 && constValue[A] <= 64 && constValue[A] % 8 == 0) value
      else error("bit width must be a multiple of 8 between 8 and 64")

    extension (width: BitWidth) {
      inline def raw: Int                                           = width
      private[v9_3] def asBitWidth: IntBetween[1, 64]               = IntBetween.unsafe[1, 64](width)
      private[v9_3] def asByteAlignedBitWidth: MultipleIn[0, 64, 8] = MultipleIn.unsafe(width)
      private[v9_3] def asFloatingPointWidth: OneOf[(16, 32, 64)]   = OneOf.unsafe[(16, 32, 64)](width)
    }
  }

  opaque type FloatWidth = Int
  object FloatWidth {
    inline def either(value: Int): Either[String, FloatWidth] =
      if (value == 16 || value == 32 || value == 64) Right(value)
      else Left(s"float width must be 16, 32, or 64: $value")

    inline def unsafe(value: Int): FloatWidth =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Int & Singleton](inline value: A): FloatWidth =
      inline if (constValue[A] == 16 || constValue[A] == 32 || constValue[A] == 64) value
      else error("float width must be 16, 32, or 64")

    extension (width: FloatWidth) {
      inline def raw: Int                                           = width
      private[v9_3] def asBitWidth: IntBetween[1, 64]               = IntBetween.unsafe[1, 64](width)
      private[v9_3] def asByteAlignedBitWidth: MultipleIn[0, 64, 8] = MultipleIn.unsafe(width)
    }
  }

  opaque type PartWidth = Int
  object PartWidth {
    inline def either(value: Int): Either[String, PartWidth] =
      if (value == 16 || value == 32) Right(value) else Left(s"part width must be 16 or 32: $value")

    inline def unsafe(value: Int): PartWidth =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Int & Singleton](inline value: A): PartWidth =
      inline if (constValue[A] == 16 || constValue[A] == 32) value else error("part width must be 16 or 32")

    extension (width: PartWidth) {
      inline def raw: Int                             = width
      private[v9_3] def asBitWidth: IntBetween[1, 64] = IntBetween.unsafe[1, 64](width)
      private[v9_3] def combined: BitWidth            = BitWidth.unsafe(width * 2)
    }
  }

  enum Value {
    case Register(name: NonEmptyString)
    case RegisterSlot(index: Int)
    case Predicate(name: NonEmptyString)
    case PredicateSlot(index: Int)
    case Function(name: NonEmptyString)
    case Symbol(name: NonEmptyString)
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
    case MultiplyWideSigned16
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

  private def atomicBinaryOpcode(operation: IntegerOperation, width: BitWidth): String = operation match {
    case IntegerOperation.Maximum(false) => s"max.u$width"
    case IntegerOperation.BitwiseAnd     => s"and.b$width"
    case IntegerOperation.BitwiseOr      => s"or.b$width"
    case IntegerOperation.BitwiseXor     => s"xor.b$width"
    case other                           => s"binary.$other"
  }

  /** Memory space selected by a PTX atomic: explicit global/shared, or generic resolved from the address. */
  enum AtomicSpace {
    case Generic, Global, Shared

    def qualifier: String = this match {
      case Generic => ""
      case Global  => "global."
      case Shared  => "shared."
    }
  }

  enum Instruction(private val opcodeText: String) {
    final val opcode: NonEmptyString = NonEmptyString.unsafe(opcodeText)

    case LoadParameter(
        destinations: Vec[NonEmptyString],
        parameter: NonEmptyString,
        offset: NonNegative[Int],
        elementWidth: BitWidth,
        signed: Boolean
    ) extends Instruction(s"ld.param.${if (signed) "s" else "u"}$elementWidth")

    case StoreParameter(parameter: NonEmptyString, offset: NonNegative[Int], source: Value, elementWidth: BitWidth)
        extends Instruction(s"st.param.u$elementWidth")

    case LoadParameterIndirect(destination: NonEmptyString, address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.param.${if (signed) "s" else "u"}$width")

    case LoadParameterIndirectVector(
        destinations: Vec[NonEmptyString],
        address: Value,
        width: BitWidth,
        signed: Boolean
    ) extends Instruction(s"ld.param.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case Move(destination: NonEmptyString, source: Value, width: BitWidth)
        extends Instruction(s"mov.u$width")

    case MovePredicate(destination: NonEmptyString, source: Value) extends Instruction("mov.pred")

    case MoveSplit(destinations: Vec[NonEmptyString], source: Value, partWidth: PartWidth)
        extends Instruction(s"mov.b${partWidth * 2}")

    case MoveCombine(destination: NonEmptyString, sources: Vec[Value], partWidth: PartWidth)
        extends Instruction(s"mov.b${partWidth * 2}")

    case IntegerUnary(operation: IntegerUnaryOperation, destination: NonEmptyString, source: Value, width: BitWidth)
        extends Instruction(operation match {
          case IntegerUnaryOperation.Negate           => s"neg.b$width"
          case IntegerUnaryOperation.Absolute         => s"abs.b$width"
          case IntegerUnaryOperation.BitwiseNot       => s"not.b$width"
          case IntegerUnaryOperation.PopulationCount  => s"popc.b$width"
          case IntegerUnaryOperation.LeadingZeroCount => s"clz.b$width"
          case IntegerUnaryOperation.BitReverse       => s"brev.b$width"
        })

    case BitFind(
        destination: NonEmptyString,
        source: Value,
        width: BitWidth,
        signed: Boolean,
        shiftAmount: Boolean
    ) extends Instruction(s"bfind.${if (shiftAmount) "shiftamt." else ""}${if (signed) "s" else "u"}$width")

    case DotProduct4(
        destination: NonEmptyString,
        left: Value,
        right: Value,
        addend: Value,
        leftSigned: Boolean,
        rightSigned: Boolean
    ) extends Instruction(s"dp4a.${if (leftSigned) "s" else "u"}32.${if (rightSigned) "s" else "u"}32")

    case IntegerBinary(
        operation: IntegerOperation,
        destination: NonEmptyString,
        left: Value,
        right: Value,
        width: BitWidth
    ) extends Instruction(
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
            case IntegerOperation.MultiplyWideSigned16   => "mul.wide.s16"
            case IntegerOperation.MultiplyWideUnsigned32 => "mul.wide.u32"
            case IntegerOperation.MultiplyWideSigned32   => "mul.wide.s32"
            case IntegerOperation.Minimum(signed)        => s"min.${if (signed) "s" else "u"}$width"
            case IntegerOperation.Maximum(signed)        => s"max.${if (signed) "s" else "u"}$width"
            case IntegerOperation.ShiftLeft              => s"shl.b$width"
            case IntegerOperation.ShiftRightLogical      => s"shr.u$width"
            case IntegerOperation.ShiftRightArithmetic   => s"shr.s$width"
          }
        )

    case FunnelShift(
        destination: NonEmptyString,
        low: Value,
        high: Value,
        amount: Value,
        left: Boolean,
        clamp: Boolean
    ) extends Instruction(s"shf.${if (left) "l" else "r"}.${if (clamp) "clamp" else "wrap"}.b32")

    case IntegerAddWithCarry(
        destination: NonEmptyString,
        left: Value,
        right: Value,
        width: BitWidth,
        carryIn: Boolean,
        carryOut: Boolean
    ) extends Instruction(s"add${if (carryIn) "c" else ""}${if (carryOut) ".cc" else ""}.u$width")

    case IntegerSubWithBorrow(
        destination: NonEmptyString,
        left: Value,
        right: Value,
        width: BitWidth,
        borrowIn: Boolean,
        borrowOut: Boolean
    ) extends Instruction(s"sub${if (borrowIn) "c" else ""}${if (borrowOut) ".cc" else ""}.u$width")

    case MultiplyAdd(
        destination: NonEmptyString,
        left: Value,
        right: Value,
        addend: Value,
        operation: IntegerMultiplyAddOperation
    ) extends Instruction(operation match {
          case IntegerMultiplyAddOperation.Low(operandWidth, _, _, _)  => s"mad.lo.u$operandWidth"
          case IntegerMultiplyAddOperation.High(operandWidth, _, _, _) => s"mad.hi.u$operandWidth"
        })

    case FloatBinary(
        operation: FloatOperation,
        destination: NonEmptyString,
        left: Value,
        right: Value,
        width: FloatWidth
    ) extends Instruction(s"${operation.toString.map(_.toLower)}.f$width")

    case FloatBinaryPacked(
        operation: FloatOperation,
        destination: NonEmptyString,
        left: Value,
        right: Value,
        bf16: Boolean
    ) extends Instruction(
          s"${operation.toString.map(_.toLower)}.${if (bf16) "bf16x2" else "f16x2"}"
        )

    case FloatUnary(operation: FloatUnaryOperation, destination: NonEmptyString, source: Value, width: FloatWidth)
        extends Instruction(operation match {
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
        destination: NonEmptyString,
        left: Value,
        right: Value,
        addend: Value,
        width: FloatWidth
    ) extends Instruction(s"fma.f$width")

    case FusedMultiplyAddBf16(destination: NonEmptyString, left: Value, right: Value, addend: Value)
        extends Instruction("fma.rn.bf16")

    case FusedMultiplyAddPacked(
        destination: NonEmptyString,
        left: Value,
        right: Value,
        addend: Value,
        bf16: Boolean
    ) extends Instruction(s"fma.rn.${if (bf16) "bf16x2" else "f16x2"}")

    case ShuffleDown(
        destination: NonEmptyString,
        validity: Option[NonEmptyString],
        source: Value,
        delta: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends Instruction(s"shfl.sync.down.b$width")

    case ShuffleUp(
        destination: NonEmptyString,
        validity: Option[NonEmptyString],
        source: Value,
        delta: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends Instruction(s"shfl.sync.up.b$width")

    case ShuffleIndex(
        destination: NonEmptyString,
        validity: Option[NonEmptyString],
        source: Value,
        index: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends Instruction(s"shfl.sync.idx.b$width")

    case ShuffleBfly(
        destination: NonEmptyString,
        validity: Option[NonEmptyString],
        source: Value,
        index: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends Instruction(s"shfl.sync.bfly.b$width")

    case VoteBallot(destination: NonEmptyString, predicate: NonEmptyString, memberMask: Value)
        extends Instruction("vote.sync.ballot.b32")

    case VotePredicate(
        destination: NonEmptyString,
        predicate: NonEmptyString,
        memberMask: Value,
        operation: VoteOperation
    ) extends Instruction(s"vote.sync.${operation.toString.map(_.toLower)}.pred")

    case BitFieldExtract(
        destination: NonEmptyString,
        source: Value,
        start: Value,
        widthValue: Value,
        width: BitWidth,
        signed: Boolean
    ) extends Instruction(s"bfe.${if (signed) "s" else "u"}$width")

    case BitFieldInsert(
        destination: NonEmptyString,
        insertValue: Value,
        baseValue: Value,
        bitPosition: Value,
        bitLength: Value,
        width: BitWidth
    ) extends Instruction(s"bfi.b$width")

    case BitMask(destination: NonEmptyString, start: Value, widthValue: Value, mode: BitMaskMode)
        extends Instruction(s"bmsk.${mode.mnemonic}.b32")

    case BytePermute(
        destination: NonEmptyString,
        left: Value,
        right: Value,
        control: Value,
        mode: BytePermuteMode
    ) extends Instruction(s"prmt.b32${if (mode == BytePermuteMode.Default) "" else s".${mode.suffix}"}")

    case Select(
        destination: NonEmptyString,
        whenTrue: Value,
        whenFalse: Value,
        predicate: NonEmptyString,
        width: BitWidth
    ) extends Instruction(s"selp.b$width")

    case Convert(
        destination: NonEmptyString,
        source: Value,
        sourceType: ConversionType,
        destinationType: ConversionType,
        rounding: Option[ConversionRounding]
    ) extends Instruction(
          s"cvt.${rounding.map(_.mnemonic + ".").getOrElse("")}${destinationType.mnemonic}.${sourceType.mnemonic}"
        )

    case LoadGlobal(destination: NonEmptyString, address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.global.${if (signed) "s" else "u"}$width")

    case LoadGeneric(destination: NonEmptyString, address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.generic.${if (signed) "s" else "u"}$width")

    case LoadGlobalVector(destinations: Vec[NonEmptyString], address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.global.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case LoadGenericVector(destinations: Vec[NonEmptyString], address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.generic.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case StoreGlobal(address: Value, source: Value, width: BitWidth)
        extends Instruction(s"st.global.u$width")

    case StoreGeneric(address: Value, source: Value, width: BitWidth)
        extends Instruction(s"st.generic.u$width")

    case StoreGenericVector(address: Value, sources: Vec[Value], width: BitWidth)
        extends Instruction(s"st.generic.v${sources.size}.u$width")

    case LoadShared(destination: NonEmptyString, address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.shared.${if (signed) "s" else "u"}$width")

    case LoadSharedVector(destinations: Vec[NonEmptyString], address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.shared.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case LoadLocal(destination: NonEmptyString, address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.local.${if (signed) "s" else "u"}$width")

    case LoadLocalVector(destinations: Vec[NonEmptyString], address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.local.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case StoreShared(address: Value, source: Value, width: BitWidth)
        extends Instruction(s"st.shared.u$width")

    case StoreSharedVector(address: Value, sources: Vec[Value], width: BitWidth)
        extends Instruction(s"st.shared.v${sources.size}.u$width")

    case StoreLocal(address: Value, source: Value, width: BitWidth)
        extends Instruction(s"st.local.u$width")

    case StoreLocalVector(address: Value, sources: Vec[Value], width: BitWidth)
        extends Instruction(s"st.local.v${sources.size}.u$width")

    /** Synchronous model of the PTX asynchronous shared-memory copy. The emulator applies the copy at this instruction;
      * commit/wait operations below remain ordered no-ops because there is no separate copy engine.
      */
    case AsyncCopy(destination: Value, source: Value, size: Positive[Int])
        extends Instruction("cp.async")

    case AsyncCommit extends Instruction("cp.async.commit_group")

    case AsyncWait extends Instruction("cp.async.wait_group")

    /** Cache prefetch hints have no architecturally visible effect in the emulator; preserve them as ordered no-ops so
      * generated kernels remain loadable and predication is still honoured.
      */
    case Prefetch extends Instruction("prefetch")

    case SetPredicate(
        destination: NonEmptyString,
        comparison: Comparison,
        kind: Char,
        left: Value,
        right: Value,
        width: BitWidth
    ) extends Instruction("setp")

    case PredicateCompare(
        destination: NonEmptyString,
        comparison: Comparison,
        left: NonEmptyString,
        right: NonEmptyString
    ) extends Instruction("setp.pred")

    case PredicateUnary(destination: NonEmptyString, source: NonEmptyString)
        extends Instruction("not.pred")

    case PredicateBinary(
        operation: PredicateOperation,
        destination: NonEmptyString,
        left: NonEmptyString,
        right: NonEmptyString
    ) extends Instruction(s"${operation.toString.map(_.toLower)}.pred")

    case Branch(target: Pc) extends Instruction("bra")

    case BarrierSync(id: NonNegative[Int]) extends Instruction("bar.sync")

    case BarrierReduce(
        destination: NonEmptyString,
        id: NonNegative[Int],
        predicate: NonEmptyString,
        operation: PredicateOperation
    ) extends Instruction(s"bar.red.${operation.toString.map(_.toLower)}.pred")

    case AtomicAdd(destination: NonEmptyString, address: Value, source: Value, width: BitWidth, space: AtomicSpace)
        extends Instruction(s"atom.${space.qualifier}add.u$width")

    case AtomicCompareExchange(
        destination: NonEmptyString,
        address: Value,
        comparator: Value,
        source: Value,
        width: BitWidth,
        space: AtomicSpace
    ) extends Instruction(s"atom.${space.qualifier}cas.b$width")

    case AtomicExchange(
        destination: NonEmptyString,
        address: Value,
        source: Value,
        width: BitWidth,
        space: AtomicSpace
    ) extends Instruction(s"atom.${space.qualifier}exch.b$width")

    case AtomicBinary(
        destination: NonEmptyString,
        address: Value,
        source: Value,
        width: BitWidth,
        space: AtomicSpace,
        operation: IntegerOperation
    ) extends Instruction(
          s"atom.${space.qualifier}${atomicBinaryOpcode(operation, width)}"
        )

    case AtomicMin(destination: NonEmptyString, address: Value, source: Value, width: BitWidth, space: AtomicSpace)
        extends Instruction(s"atom.${space.qualifier}min.u$width")

    case AtomicAddFloat(destination: NonEmptyString, address: Value, source: Value, width: FloatWidth)
        extends Instruction(s"atom.global.add.f$width")

    case WarpReduce(
        destination: NonEmptyString,
        source: Value,
        memberMask: Value,
        operation: IntegerOperation,
        width: BitWidth
    ) extends Instruction("redux.sync")

    case MemoryBarrier extends Instruction("membar")

    case WarpBarrier(memberMask: Value) extends Instruction("bar.warp.sync")

    case Call(function: Value, returnParameters: Vec[NonEmptyString], parameters: Vec[NonEmptyString])
        extends Instruction("call")

    case Trap extends Instruction("trap")

    case Return extends Instruction("ret")
  }

  case class ExecutionPredicate(name: NonEmptyString, negated: Boolean)

  case class PredicatedInstruction(instruction: Instruction, predication: Option[ExecutionPredicate]) {
    def opcode: NonEmptyString = instruction.opcode
  }

  case class KernelParameter(name: NonEmptyString, widthBytes: Positive[Int], pointer: Boolean = false)
  case class Argument(bytes: NonEmptyVec[Byte])

  case class DeviceFunction(
      name: NonEmptyString,
      parameters: Vec[NonEmptyString],
      parameterWidths: Vec[Positive[Int]],
      parameterOffsets: Vec[NonNegative[Int]],
      returnParameters: Vec[NonEmptyString],
      program: ExecutableProgram[PredicatedInstruction],
      sharedSymbols: Map[NonEmptyString, Long]
  )

  object Argument {
    def fromLong(value: Long): Argument =
      Argument(NonEmptyVec.unsafeFromVec(LittleEndian.toVec(value, 8)))
  }

  case class Kernel(
      name: NonEmptyString,
      parameters: Vec[KernelParameter],
      program: ExecutableProgram[PredicatedInstruction],
      functions: Map[NonEmptyString, DeviceFunction] = Map.empty,
      sharedSymbols: Map[NonEmptyString, Long] = Map.empty,
      globalSymbols: Map[NonEmptyString, GuestAddress] = Map.empty,
      registerChunkCount: Int = 96
  ) {
    def instructions: Vec[Instruction] = program.instructions.map(_.instruction)
  }

  opaque type Pc = Int
  object Pc {
    inline def either(value: Int): Either[String, Pc] =
      if (value >= 0) Right(value) else Left(s"program counter must be positive or zero: $value")

    inline def unsafe(value: Int): Pc =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Int & Singleton](inline value: A): Pc =
      inline if (constValue[A] >= 0) value else error("program counter must be positive or zero")

    val Entry: Pc = 0

    extension (pc: Pc) {
      inline def raw: Int  = pc
      def next: Option[Pc] =
        Option.when(pc != Int.MaxValue)(Pc.unsafe(pc + 1))
    }
  }

  case class ExecutableProgram[Instruction](instructions: Vec[Instruction]) {
    def size: Int = instructions.size

    def at(pc: Pc): Option[Instruction] = instructions.lift(pc)
  }

}
