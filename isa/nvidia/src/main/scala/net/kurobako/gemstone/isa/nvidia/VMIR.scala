package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

object VMIR {

  enum ConvergenceAction { case Setup, Synchronize, Break }

  extension (width: Int where (Between[8, 64] & MultipleOf[8])) {
    def byteCount: Int where Between[1, 8] = (width.raw / 8).unsafe[Int where Between[1, 8]]
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

  enum AddThreeCarryMode {
    case None
    case Produce
    case Consume
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

  enum BitMaskMode(val mnemonic: NonEmpty[String]) {
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

  enum NumericFormat(val width: Int where (Between[8, 64] & MultipleOf[8])) {
    case Bits(bits: Int in (8, 16, 32, 64))     extends NumericFormat(bits.widen)
    case Signed(bits: Int in (8, 16, 32, 64))   extends NumericFormat(bits.widen)
    case Unsigned(bits: Int in (8, 16, 32, 64)) extends NumericFormat(bits.widen)
    case IEEE(bits: Int in (16, 32, 64))        extends NumericFormat(bits.widen)
    case BF16                                   extends NumericFormat(16)
  }

  enum ConversionType(val mnemonic: NonEmpty[String], val format: NumericFormat) {
    inline def width: Int where (Between[8, 64] & MultipleOf[8]) = format.width
    case B8   extends ConversionType("b8", NumericFormat.Bits(8))
    case S8   extends ConversionType("s8", NumericFormat.Signed(8))
    case U8   extends ConversionType("u8", NumericFormat.Unsigned(8))
    case B16  extends ConversionType("b16", NumericFormat.Bits(16))
    case S16  extends ConversionType("s16", NumericFormat.Signed(16))
    case U16  extends ConversionType("u16", NumericFormat.Unsigned(16))
    case F16  extends ConversionType("f16", NumericFormat.IEEE(16))
    case BF16 extends ConversionType("bf16", NumericFormat.BF16)
    case B32  extends ConversionType("b32", NumericFormat.Bits(32))
    case S32  extends ConversionType("s32", NumericFormat.Signed(32))
    case U32  extends ConversionType("u32", NumericFormat.Unsigned(32))
    case F32  extends ConversionType("f32", NumericFormat.IEEE(32))
    case B64  extends ConversionType("b64", NumericFormat.Bits(64))
    case S64  extends ConversionType("s64", NumericFormat.Signed(64))
    case U64  extends ConversionType("u64", NumericFormat.Unsigned(64))
    case F64  extends ConversionType("f64", NumericFormat.IEEE(64))
  }

  enum ConversionRounding(val mnemonic: NonEmpty[String]) {
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

  case class FloatControl(
      flushSubnormal: Boolean = false,
      saturate: Boolean = false,
      propagateNaN: Boolean = false,
      absolute: Boolean = false,
      xorSign: Boolean = false
  )

  enum FloatOperation {
    case Add
    case Subtract
    case Multiply
    case Divide
    case Minimum
    case Maximum
    case MinimumNumber
    case MaximumNumber
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
    case IntegerOperation.Minimum(signed) => s"min.${if (signed) "s" else "u"}$width"
    case IntegerOperation.Maximum(signed) => s"max.${if (signed) "s" else "u"}$width"
    case IntegerOperation.BitwiseAnd      => s"and.b$width"
    case IntegerOperation.BitwiseOr       => s"or.b$width"
    case IntegerOperation.BitwiseXor      => s"xor.b$width"
    case other                            => s"binary.$other"
  }

  /** Memory space selected by an atomic: explicit global/shared, or generic resolved from the address. */
  enum AtomicSpace {
    case Generic, Global, Shared

    def qualifier: String = this match {
      case Generic => ""
      case Global  => "global."
      case Shared  => "shared."
    }
  }

  /** Memory domain and visibility carried by ordinary load/store instructions. */
  enum MemoryAccess(val qualifier: NonEmpty[String]) {
    case Global(system: Boolean)  extends MemoryAccess(if (system) "relaxed.sys.global" else "global")
    case Generic(system: Boolean) extends MemoryAccess(if (system) "relaxed.sys" else "generic")
    case Shared                   extends MemoryAccess("shared")
    case Local                    extends MemoryAccess("local")

    def generic: Boolean = this match {
      case Generic(_) => true
      case _          => false
    }

    def systemScoped: Boolean = this match {
      case Global(true) | Generic(true) => true
      case _                            => false
    }

    def sharedOrLocal: Boolean = this match {
      case Shared | Local => true
      case _              => false
    }

    def coherentVectorCandidate: Boolean = this match {
      case Global(false) | Generic(false) => true
      case _                              => false
    }
  }

  enum Instruction(val opcode: NonEmpty[String]) {

    case Tensor(operation: VMTensor.Operation, mnemonic: NonEmpty[String]) extends Instruction(mnemonic)

    case ConvertTF32(destination: NonEmpty[String], source: Value) extends Instruction("cvt.rna.tf32.f32")

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

    case IntegerAddThree(
        destination: NonEmpty[String],
        first: Value,
        second: Value,
        third: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        carryMode: AddThreeCarryMode,
        negateFirst: Boolean,
        negateSecond: Boolean,
        negateThird: Boolean
    ) extends Instruction(s"iadd3.$width")

    case WriteConditionFlag(flag: VMConditionCodes.Flag, value: Value)
        extends Instruction(s"condition.write.$flag")

    case ReadConditionFlag(destination: NonEmpty[String], flag: VMConditionCodes.Flag, predicate: Boolean)
        extends Instruction(s"condition.read.$flag")

    case InvalidateConditionFlag(flag: VMConditionCodes.Flag)
        extends Instruction(s"condition.invalidate.$flag")

    case InvalidateAddThreeCarry extends Instruction("iadd3.carry.invalidate")

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
        width: Int in (16, 32, 64),
        control: FloatControl = FloatControl()
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
        width: Int in (16, 32, 64),
        control: FloatControl = FloatControl()
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
        width: Int in (16, 32, 64),
        control: FloatControl = FloatControl(),
        rounding: ConversionRounding = ConversionRounding.RN
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

    case Load(
        destination: NonEmpty[String],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean,
        access: MemoryAccess
    ) extends Instruction({
          val signedness = if (signed) "s" else "u"
          s"ld.${access.qualifier}.$signedness$width"
        })
    case LoadVector(
        destinations: Vec[NonEmpty[String]],
        address: Value,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        signed: Boolean,
        access: MemoryAccess
    ) extends Instruction(
          s"ld.${access.qualifier}.v${destinations.size}.${if (signed) "s" else "u"}$width"
        )

    case Store(address: Value, source: Value, width: Int where (Between[8, 64] & MultipleOf[8]), access: MemoryAccess)
        extends Instruction(s"st.${access.qualifier}.u$width")
    case StoreVector(
        address: Value,
        sources: Vec[Value],
        width: Int where (Between[8, 64] & MultipleOf[8]),
        access: MemoryAccess
    ) extends Instruction(s"st.${access.qualifier}.v${sources.size}.u$width")

    /** Synchronous model of an asynchronous shared-memory copy. The emulator applies the copy at this instruction;
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
        format: NumericFormat,
        left: Value,
        right: Value,
        flushSubnormal: Boolean = false
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

    case Convergence(slot: Int, action: ConvergenceAction) extends Instruction("sass.convergence")
    case SASSReturnPush(address: Int)                      extends Instruction("sass.return.push")
    case SASSReturnPop(destination: NonEmpty[String])      extends Instruction("sass.return.pop")
    case WarpBarrier(memberMask: Value)                    extends Instruction("bar.warp.sync")

    case Call(function: Value, returnParameters: Vec[NonEmpty[String]], parameters: Vec[NonEmpty[String]])
        extends Instruction("call")

    case Trap extends Instruction("trap")

    case Return extends Instruction("ret")
  }

  case class ExecutionPredicate(name: NonEmpty[String], negated: Boolean)

  case class PredicatedInstruction(instruction: Instruction, predication: Option[ExecutionPredicate])

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
      registerChunkCount: Int = 96,
      isaName: NonEmpty[String] = "PTX"
  )

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
