package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

private object VMIR {

  extension (width: Int where (Between[8, 64] & MultipleOf[8])) {
    def byteCount: Int where Between[1, 8]        = (width.raw / 8).unsafe[Int where Between[1, 8]]
    def asFloatingPointWidth: Int in (16, 32, 64) = width.raw.unsafe[Int in (16, 32, 64)]
  }
  extension (width: Int in (16, 32)) {
    def combined: Int where (Between[8, 64] & MultipleOf[8]) = (width.raw * 2).unsafe[Int where
      (Between[8, 64] & MultipleOf[8])]
  }

  enum Value {
    case Register(name: NonEmpty[String])
    case RegisterSlot(index: Int)
    case Predicate(name: NonEmpty[String])
    case PredicateSlot(index: Int)
    case Function(name: NonEmpty[String])
    case Symbol(name: NonEmpty[String])
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

  enum ConversionType(val mnemonic: String, val width: Int where (Between[8, 64] & MultipleOf[8]), val kind: Char) {
    case B8   extends ConversionType("b8", 8: Int where (Between[8, 64] & MultipleOf[8]), 'b')
    case S8   extends ConversionType("s8", 8: Int where (Between[8, 64] & MultipleOf[8]), 's')
    case U8   extends ConversionType("u8", 8: Int where (Between[8, 64] & MultipleOf[8]), 'u')
    case B16  extends ConversionType("b16", 16: Int where (Between[8, 64] & MultipleOf[8]), 'b')
    case S16  extends ConversionType("s16", 16: Int where (Between[8, 64] & MultipleOf[8]), 's')
    case U16  extends ConversionType("u16", 16: Int where (Between[8, 64] & MultipleOf[8]), 'u')
    case F16  extends ConversionType("f16", 16: Int where (Between[8, 64] & MultipleOf[8]), 'f')
    case BF16 extends ConversionType("bf16", 16: Int where (Between[8, 64] & MultipleOf[8]), 'f')
    case B32  extends ConversionType("b32", 32: Int where (Between[8, 64] & MultipleOf[8]), 'b')
    case S32  extends ConversionType("s32", 32: Int where (Between[8, 64] & MultipleOf[8]), 's')
    case U32  extends ConversionType("u32", 32: Int where (Between[8, 64] & MultipleOf[8]), 'u')
    case F32  extends ConversionType("f32", 32: Int where (Between[8, 64] & MultipleOf[8]), 'f')
    case B64  extends ConversionType("b64", 64: Int where (Between[8, 64] & MultipleOf[8]), 'b')
    case S64  extends ConversionType("s64", 64: Int where (Between[8, 64] & MultipleOf[8]), 's')
    case U64  extends ConversionType("u64", 64: Int where (Between[8, 64] & MultipleOf[8]), 'u')
    case F64  extends ConversionType("f64", 64: Int where (Between[8, 64] & MultipleOf[8]), 'f')
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
    case Low(
        operandWidth: Int where (Between[8, 64] & MultipleOf[8]),
        resultWidth: Int where (Between[8, 64] & MultipleOf[8]),
        carryIn: Boolean,
        carryOut: Boolean
    )
    case High(
        operandWidth: Int where (Between[8, 64] & MultipleOf[8]),
        resultWidth: Int where (Between[8, 64] & MultipleOf[8]),
        carryIn: Boolean,
        carryOut: Boolean
    )
  }

  private def atomicBinaryOpcode(
      operation: IntegerOperation,
      width: Int where (Between[8, 64] & MultipleOf[8])
  ): String = operation match {
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
    val opcode: NonEmpty[String] = opcodeText.unsafe[NonEmpty[String]]

    case LoadParameter(
        destinations: Vec[NonEmpty[String]],
        parameter: NonEmpty[String],
        offset: Int >= 0,
        elementWidth: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"ld.param.${if (signed) "s" else "u"}$elementWidth")

    case StoreParameter(
        parameter: NonEmpty[String],
        offset: Int >= 0,
        source: Value,
        elementWidth: Int where (Between[8, 64] & MultipleOf[8])
    ) extends Instruction(s"st.param.u$elementWidth")

    case LoadParameterIndirect(
        destination: NonEmpty[String],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"ld.param.${if (signed) "s" else "u"}$width")

    case LoadParameterIndirectVector(
        destinations: Vec[NonEmpty[String]],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"ld.param.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case Move(destination: NonEmpty[String], source: Value, width: Int where (Between[8, 64] & MultipleOf[8]))
        extends Instruction(s"mov.u$width")

    case MovePredicate(destination: NonEmpty[String], source: Value) extends Instruction("mov.pred")

    case MoveSplit(destinations: Vec[NonEmpty[String]], source: Value, partWidth: Int in (16, 32))
        extends Instruction(s"mov.b${partWidth.raw * 2}")

    case MoveCombine(destination: NonEmpty[String], sources: Vec[Value], partWidth: Int in (16, 32))
        extends Instruction(s"mov.b${partWidth.raw * 2}")

    case IntegerUnary(
        operation: IntegerUnaryOperation,
        destination: NonEmpty[String],
        source: Value,
        width: Int where (Between[8, 64] & MultipleOf[8])
    ) extends Instruction(operation match {
          case IntegerUnaryOperation.Negate           => s"neg.b$width"
          case IntegerUnaryOperation.Absolute         => s"abs.b$width"
          case IntegerUnaryOperation.BitwiseNot       => s"not.b$width"
          case IntegerUnaryOperation.PopulationCount  => s"popc.b$width"
          case IntegerUnaryOperation.LeadingZeroCount => s"clz.b$width"
          case IntegerUnaryOperation.BitReverse       => s"brev.b$width"
        })

    case BitFind(
        destination: NonEmpty[String],
        source: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean,
        shiftAmount: Boolean
    ) extends Instruction(s"bfind.${if (shiftAmount) "shiftamt." else ""}${if (signed) "s" else "u"}$width")

    case DotProduct4(
        destination: NonEmpty[String],
        left: Value,
        right: Value,
        addend: Value,
        leftSigned: Boolean,
        rightSigned: Boolean
    ) extends Instruction(s"dp4a.${if (leftSigned) "s" else "u"}32.${if (rightSigned) "s" else "u"}32")

    case IntegerBinary(
        operation: IntegerOperation,
        destination: NonEmpty[String],
        left: Value,
        right: Value,
        width: Int where (Between[8, 64] & MultipleOf[8])
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
        destination: NonEmpty[String],
        low: Value,
        high: Value,
        amount: Value,
        left: Boolean,
        clamp: Boolean
    ) extends Instruction(s"shf.${if (left) "l" else "r"}.${if (clamp) "clamp" else "wrap"}.b32")

    case IntegerAddWithCarry(
        destination: NonEmpty[String],
        left: Value,
        right: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        carryIn: Boolean,
        carryOut: Boolean
    ) extends Instruction(s"add${if (carryIn) "c" else ""}${if (carryOut) ".cc" else ""}.u$width")

    case IntegerSubWithBorrow(
        destination: NonEmpty[String],
        left: Value,
        right: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        borrowIn: Boolean,
        borrowOut: Boolean
    ) extends Instruction(s"sub${if (borrowIn) "c" else ""}${if (borrowOut) ".cc" else ""}.u$width")

    case MultiplyAdd(
        destination: NonEmpty[String],
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
        destination: NonEmpty[String],
        left: Value,
        right: Value,
        width: Int in (16, 32, 64)
    ) extends Instruction(s"${operation.toString.toLowerCaseRoot}.f$width")

    case FloatBinaryPacked(
        operation: FloatOperation,
        destination: NonEmpty[String],
        left: Value,
        right: Value,
        bf16: Boolean
    ) extends Instruction(
          s"${operation.toString.toLowerCaseRoot}.${if (bf16) "bf16x2" else "f16x2"}"
        )

    case FloatUnary(
        operation: FloatUnaryOperation,
        destination: NonEmpty[String],
        source: Value,
        width: Int in (16, 32, 64)
    ) extends Instruction(operation match {
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
        destination: NonEmpty[String],
        left: Value,
        right: Value,
        addend: Value,
        width: Int in (16, 32, 64)
    ) extends Instruction(s"fma.f$width")

    case FusedMultiplyAddBf16(destination: NonEmpty[String], left: Value, right: Value, addend: Value)
        extends Instruction("fma.rn.bf16")

    case FusedMultiplyAddPacked(
        destination: NonEmpty[String],
        left: Value,
        right: Value,
        addend: Value,
        bf16: Boolean,
        saturate: Boolean
    ) extends Instruction(s"fma.rn.${if (saturate) "sat." else ""}${if (bf16) "bf16x2" else "f16x2"}")

    case ShuffleDown(
        destination: NonEmpty[String],
        validity: Option[NonEmpty[String]],
        source: Value,
        delta: Value,
        clamp: Value,
        memberMask: Value,
        width: Int where (Between[8, 64] & MultipleOf[8])
    ) extends Instruction(s"shfl.sync.down.b$width")

    case ShuffleUp(
        destination: NonEmpty[String],
        validity: Option[NonEmpty[String]],
        source: Value,
        delta: Value,
        clamp: Value,
        memberMask: Value,
        width: Int where (Between[8, 64] & MultipleOf[8])
    ) extends Instruction(s"shfl.sync.up.b$width")

    case ShuffleIndex(
        destination: NonEmpty[String],
        validity: Option[NonEmpty[String]],
        source: Value,
        index: Value,
        clamp: Value,
        memberMask: Value,
        width: Int where (Between[8, 64] & MultipleOf[8])
    ) extends Instruction(s"shfl.sync.idx.b$width")

    case ShuffleBfly(
        destination: NonEmpty[String],
        validity: Option[NonEmpty[String]],
        source: Value,
        index: Value,
        clamp: Value,
        memberMask: Value,
        width: Int where (Between[8, 64] & MultipleOf[8])
    ) extends Instruction(s"shfl.sync.bfly.b$width")

    case VoteBallot(destination: NonEmpty[String], predicate: NonEmpty[String], memberMask: Value)
        extends Instruction("vote.sync.ballot.b32")

    case VotePredicate(
        destination: NonEmpty[String],
        predicate: NonEmpty[String],
        memberMask: Value,
        operation: VoteOperation
    ) extends Instruction(s"vote.sync.${operation.toString.toLowerCaseRoot}.pred")

    case BitFieldExtract(
        destination: NonEmpty[String],
        source: Value,
        start: Value,
        widthValue: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"bfe.${if (signed) "s" else "u"}$width")

    case BitFieldInsert(
        destination: NonEmpty[String],
        insertValue: Value,
        baseValue: Value,
        bitPosition: Value,
        bitLength: Value,
        width: Int where (Between[8, 64] & MultipleOf[8])
    ) extends Instruction(s"bfi.b$width")

    case BitMask(destination: NonEmpty[String], start: Value, widthValue: Value, mode: BitMaskMode)
        extends Instruction(s"bmsk.${mode.mnemonic}.b32")

    case BytePermute(
        destination: NonEmpty[String],
        left: Value,
        right: Value,
        control: Value,
        mode: BytePermuteMode
    ) extends Instruction(s"prmt.b32${if (mode == BytePermuteMode.Default) "" else s".${mode.suffix}"}")

    case Select(
        destination: NonEmpty[String],
        whenTrue: Value,
        whenFalse: Value,
        predicate: NonEmpty[String],
        width: Int where (Between[8, 64] & MultipleOf[8])
    ) extends Instruction(s"selp.b$width")

    case Convert(
        destination: NonEmpty[String],
        source: Value,
        sourceType: ConversionType,
        destinationType: ConversionType,
        rounding: Option[ConversionRounding]
    ) extends Instruction(
          s"cvt.${rounding.map(_.mnemonic + ".").getOrElse("")}${destinationType.mnemonic}.${sourceType.mnemonic}"
        )

    case LoadGlobal(
        destination: NonEmpty[String],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"ld.global.${if (signed) "s" else "u"}$width")

    case LoadGeneric(
        destination: NonEmpty[String],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"ld.generic.${if (signed) "s" else "u"}$width")

    case LoadGlobalVector(
        destinations: Vec[NonEmpty[String]],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"ld.global.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case LoadGenericVector(
        destinations: Vec[NonEmpty[String]],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"ld.generic.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case StoreGlobal(address: Value, source: Value, width: Int where (Between[8, 64] & MultipleOf[8]))
        extends Instruction(s"st.global.u$width")

    case StoreGeneric(address: Value, source: Value, width: Int where (Between[8, 64] & MultipleOf[8]))
        extends Instruction(s"st.generic.u$width")

    case StoreGenericVector(address: Value, sources: Vec[Value], width: Int where (Between[8, 64] & MultipleOf[8]))
        extends Instruction(s"st.generic.v${sources.size}.u$width")

    case LoadShared(
        destination: NonEmpty[String],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"ld.shared.${if (signed) "s" else "u"}$width")

    case LoadSharedVector(
        destinations: Vec[NonEmpty[String]],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"ld.shared.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case LoadLocal(
        destination: NonEmpty[String],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"ld.local.${if (signed) "s" else "u"}$width")

    case LoadLocalVector(
        destinations: Vec[NonEmpty[String]],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean
    ) extends Instruction(s"ld.local.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case StoreShared(address: Value, source: Value, width: Int where (Between[8, 64] & MultipleOf[8]))
        extends Instruction(s"st.shared.u$width")

    case StoreSharedVector(address: Value, sources: Vec[Value], width: Int where (Between[8, 64] & MultipleOf[8]))
        extends Instruction(s"st.shared.v${sources.size}.u$width")

    case StoreLocal(address: Value, source: Value, width: Int where (Between[8, 64] & MultipleOf[8]))
        extends Instruction(s"st.local.u$width")

    case StoreLocalVector(address: Value, sources: Vec[Value], width: Int where (Between[8, 64] & MultipleOf[8]))
        extends Instruction(s"st.local.v${sources.size}.u$width")

    /** Synchronous model of the PTX asynchronous shared-memory copy. The emulator applies the copy at this instruction;
      * commit/wait operations below remain ordered no-ops because there is no separate copy engine.
      */
    case AsyncCopy(destination: Value, source: Value, size: Int > 0)
        extends Instruction("cp.async")

    case AsyncCommit extends Instruction("cp.async.commit_group")

    case AsyncWait extends Instruction("cp.async.wait_group")

    /** Cache prefetch hints have no architecturally visible effect in the emulator; preserve them as ordered no-ops so
      * generated kernels remain loadable and predication is still honoured.
      */
    case Prefetch extends Instruction("prefetch")

    case SetPredicate(
        destination: NonEmpty[String],
        comparison: Comparison,
        kind: Char,
        left: Value,
        right: Value,
        width: Int where (Between[8, 64] & MultipleOf[8])
    ) extends Instruction("setp")

    case PredicateCompare(
        destination: NonEmpty[String],
        comparison: Comparison,
        left: NonEmpty[String],
        right: NonEmpty[String]
    ) extends Instruction("setp.pred")

    case PredicateUnary(destination: NonEmpty[String], source: NonEmpty[String])
        extends Instruction("not.pred")

    case PredicateBinary(
        operation: PredicateOperation,
        destination: NonEmpty[String],
        left: NonEmpty[String],
        right: NonEmpty[String]
    ) extends Instruction(s"${operation.toString.toLowerCaseRoot}.pred")

    case Branch(target: Pc) extends Instruction("bra")

    case BarrierSync(id: Int >= 0) extends Instruction("bar.sync")

    case BarrierReduce(
        destination: NonEmpty[String],
        id: Int >= 0,
        predicate: NonEmpty[String],
        operation: PredicateOperation
    ) extends Instruction(s"bar.red.${operation.toString.toLowerCaseRoot}.pred")

    case AtomicAdd(
        destination: NonEmpty[String],
        address: Value,
        source: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        space: AtomicSpace
    ) extends Instruction(s"atom.${space.qualifier}add.u$width")

    case AtomicCompareExchange(
        destination: NonEmpty[String],
        address: Value,
        comparator: Value,
        source: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        space: AtomicSpace
    ) extends Instruction(s"atom.${space.qualifier}cas.b$width")

    case AtomicExchange(
        destination: NonEmpty[String],
        address: Value,
        source: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        space: AtomicSpace
    ) extends Instruction(s"atom.${space.qualifier}exch.b$width")

    case AtomicBinary(
        destination: NonEmpty[String],
        address: Value,
        source: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        space: AtomicSpace,
        operation: IntegerOperation
    ) extends Instruction(
          s"atom.${space.qualifier}${atomicBinaryOpcode(operation, width)}"
        )

    case AtomicMin(
        destination: NonEmpty[String],
        address: Value,
        source: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        space: AtomicSpace
    ) extends Instruction(s"atom.${space.qualifier}min.u$width")

    case AtomicAddFloat(destination: NonEmpty[String], address: Value, source: Value, width: Int in (16, 32, 64))
        extends Instruction(s"atom.global.add.f$width")

    case WarpReduce(
        destination: NonEmpty[String],
        source: Value,
        memberMask: Value,
        operation: IntegerOperation,
        width: Int where (Between[8, 64] & MultipleOf[8])
    ) extends Instruction("redux.sync")

    case MemoryBarrier extends Instruction("membar")

    case WarpBarrier(memberMask: Value) extends Instruction("bar.warp.sync")

    case Call(function: Value, returnParameters: Vec[NonEmpty[String]], parameters: Vec[NonEmpty[String]])
        extends Instruction("call")

    case Trap extends Instruction("trap")

    case Return extends Instruction("ret")
  }

  case class ExecutionPredicate(name: NonEmpty[String], negated: Boolean)

  case class PredicatedInstruction(instruction: Instruction, predication: Option[ExecutionPredicate]) {
    def opcode: NonEmpty[String] = instruction.opcode
  }

  case class KernelParameter(name: NonEmpty[String], widthBytes: Int > 0, pointer: Boolean = false)
  object Argument {
    def fromLong(value: Long): Argument =
      Argument(LittleEndian.toVec(value, 8).unsafe)
  }

  case class DeviceFunction(
      name: NonEmpty[String],
      parameters: Vec[NonEmpty[String]],
      parameterWidths: Vec[Int > 0],
      parameterOffsets: Vec[Int >= 0],
      returnParameters: Vec[NonEmpty[String]],
      program: ExecutableProgram[PredicatedInstruction],
      sharedSymbols: Map[NonEmpty[String], Long]
  )

  case class Argument(bytes: NonEmpty[Vec[Byte]])

  case class Kernel(
      name: NonEmpty[String],
      parameters: Vec[KernelParameter],
      program: ExecutableProgram[PredicatedInstruction],
      functions: Map[NonEmpty[String], DeviceFunction] = Map.empty,
      sharedSymbols: Map[NonEmpty[String], Long] = Map.empty,
      globalSymbols: Map[NonEmpty[String], GuestAddress] = Map.empty,
      registerChunkCount: Int = 96
  ) {
    def instructions: Vec[Instruction] = program.instructions.map(_.instruction)
  }

  type Pc = Pc.Type
  object Pc extends Newtype[Int >= 0] {
    val Entry: Pc = Pc(0)
    extension (pc: Pc) {
      def toInt: Int       = pc.raw
      def next: Option[Pc] = Option.when(pc.toInt != Int.MaxValue)(Pc(pc.unwrap + 1))
    }
  }

  case class ExecutableProgram[Instruction](instructions: Vec[Instruction]) {
    def size: Int = instructions.size

    def at(pc: Pc): Option[Instruction] = instructions.lift(pc.toInt)
  }

}
