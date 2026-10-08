package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import scala.annotation.targetName
private object VMIR {
  type VectorRegisterIndex = Int in 0 ~ 255
  type ScalarRegisterIndex = Int in 0 ~ 127

  extension (index: VectorRegisterIndex) {
    def offsetBy(words: Int >= 0): VectorRegisterIndex = {
      require(words.raw <= 255 - index.raw, "register span exceeds the register bank")
      (index.raw + words.raw).unsafe[VectorRegisterIndex]
    }
  }

  extension (index: ScalarRegisterIndex) {
    @targetName("offsetScalarRegister")
    def offsetBy(words: Int >= 0): ScalarRegisterIndex = {
      require(words.raw <= 127 - index.raw, "scalar register span exceeds the register bank")
      (index.raw + words.raw).unsafe[ScalarRegisterIndex]
    }
  }

  enum ScalarBranchCondition {
    case Always, SccZero, SccNonZero, VccZero, VccNonZero, ExecZero, ExecNonZero
  }

  enum InstructionSource {
    case Scalar(index: ScalarRegisterIndex)
    case VectorRegister(index: VectorRegisterIndex)
    case Constant(value: Long)
    case Floating(value: Double)
    case Condition(value: ScalarBranchCondition)

    /** A literal dword embedded in the instruction stream.
      *
      * Literals share the raw integer representation of [[Constant]], but are kept distinct because floating-point
      * instructions promote them from binary32 to their operand width rather than treating the dword as an integer
      * value.
      */
    case Literal(value: Long)

    /** The enclosing [[Instruction.VDPP]]'s source register as routed to the current lane. */
    case DPP

    /** The high 16 bits of `source`, as GFX11 VOP3 OP_SEL selects for a 16-bit operand. */
    case HighHalf(source: InstructionSource)
  }

  /** DPP16 routes through a control with row and bank write masks; DPP8 selects within each group of eight lanes. */
  enum DPPControl {
    case DPP16(control: Int, rowMask: Int, bankMask: Int, boundControl: Boolean)
    case DPP8(selectors: Int)
  }

  /** VOP3 OMOD scaling of a floating-point result. */
  enum OutputScale(val factor: Double) {
    case Multiply2 extends OutputScale(2.0)
    case Multiply4 extends OutputScale(4.0)
    case Divide2   extends OutputScale(0.5)
  }

  object SourceModifiers {
    val Unmodified: SourceModifiers = SourceModifiers(0, 0)
  }

  /** VOP3 absolute-value and negation bits, bit `i` for source `i`, applied to a source's sign in that order. */
  case class SourceModifiers(absolute: Int, negate: Int) {
    def apply(bits: Long, source: Int, width: Int in (16, 32, 64)): Long = {
      val sign      = 1L << (width.raw - 1)
      val magnitude = if ((absolute & (1 << source)) != 0) bits & ~sign else bits
      if ((negate & (1 << source)) != 0) magnitude ^ sign else magnitude
    }
  }

  case class SubDwordSources(
      selectionA: SubDwordSelection,
      selectionB: SubDwordSelection,
      signExtendA: Boolean,
      signExtendB: Boolean
  )

  enum RegisterBank {
    case Vector, Accumulator
  }

  /** Per-lane memory operands; byte offsets are signed. */
  enum MemoryAddress {

    /** A 64-bit vector address, or a scalar base plus a 32-bit vector offset. */
    case Global(vector: VectorRegisterIndex, scalar: Option[ScalarRegisterIndex], offset: Int)

    /** A 64-bit vector address routed by aperture to LDS, private or global memory. */
    case Flat(vector: VectorRegisterIndex, offset: Int)

    /** A byte offset into each lane's private segment. */
    case Scratch(vector: Option[VectorRegisterIndex], scalar: Option[ScalarRegisterIndex], offset: Int)

    /** An offset into the buffer a resource descriptor names; out-of-range lanes read zero and drop stores. */
    case Buffer(
        vector: Option[VectorRegisterIndex],
        resource: ScalarRegisterIndex,
        scalar: Option[ScalarRegisterIndex],
        offset: Int
    )

    /** A 32-bit LDS address. */
    case Local(vector: VectorRegisterIndex, offset: Int)
  }

  /** Where loaded data lands: whole registers, or one 16-bit half preserving the other. */
  enum LoadPlacement {
    case Full, Low, High
  }

  enum PackedF32Operation {
    case Add, Multiply, Fma
  }

  /** The new EXEC mask from the explicit source and the previous mask. */
  enum SaveExecOperation {
    case And, AndNot, Or, Xor

    def apply(source: Long, previous: Long): Long = this match {
      case And    => source & previous
      case AndNot => source & ~previous
      case Or     => source | previous
      case Xor    => source ^ previous
    }
  }

  enum ScalarUnaryOperation {
    case U32FromF32, F32FromU32, TruncateF32, AbsoluteI32, SignExtendI8, SignExtendI16, CountBits32, CountBits64,
      ReverseBits32
    case Not32, Not64, FirstBitHighI32, FirstBitHighI64, FirstBitHighU32, FirstBitHighU64, ClearBit32, SetBit32
  }

  enum ScalarBinaryOperation {
    case AddF32, SubtractF32, MultiplyF32, And32, Or32, MultiplyI32, MultiplyHighU32, MultiplyHighI32, AddI32,
      SubtractI32
    case ShiftLeft32, ShiftLeftTwoAndAddU32, ShiftRightArithmetic32, BitFieldExtractU32, BitFieldExtractI32,
      ShiftRight32
    case AndNot32, OrNot32, Nor32
    case PackLowLow16, PackLowHigh16, PackHighHigh16, AddU32, AddCarryU32, SubtractU32, SubtractBorrowU32, Xor32
  }

  enum ScalarMinMaxOperation {
    case MinimumU32, MaximumU32, MinimumI32, MaximumI32
  }

  enum ScalarBinary64Operation {
    case Multiply, AddNoCarry, SubtractNoCarry
    case And, AndNot, Or, OrNot, Nor, Xor, ShiftLeft, ShiftRight, ShiftRightArithmetic, BitFieldExtractI64
  }

  enum ScalarCompareOperation {
    case EqualI32, NotEqualI32, GreaterI32, GreaterOrEqualI32, LessI32, LessOrEqualI32
    case EqualU32, NotEqualU32, GreaterU32, GreaterOrEqualU32, LessU32, LessOrEqualU32
    case EqualU64, NotEqualU64, BitSet32, BitClear32
  }

  enum VectorBitFindOperation {
    case FirstBitLow32, FirstBitHighI32, FirstBitHighU32
  }

  enum VectorConvertOperation {
    case FrexpExponentF64, FrexpExponentF32, FrexpMantissaF32, FrexpMantissaF64
    case ReciprocalF32, ReciprocalF64, TruncateF32, FloorF32, FractionF32, RoundNearestEvenF32
    case TruncateF64, FloorF64, FractionF64, RoundNearestEvenF64
    case ReciprocalSquareRootF32, ReciprocalSquareRootF64, SquareRootF32, ExponentBase2F32
    case SineF32, CosineF32, LogarithmBase2F32
    case I32FromF32, U32FromF32, I32FromF64, U32FromF64
    case F32FromUByte0, F32FromUByte1, F32FromUByte2, F32FromUByte3
    case F32FromF64, F32FromF16, F16FromF32, I16FromF16
    case F64FromI32, F64FromU32, F64FromF32, F32FromI32, F32FromU32
  }

  enum VectorMadF32Operation {
    case MultiplyAdd, FusedMultiplyAdd
  }

  enum DualVectorOperation {
    case Move, AddF32, SubtractF32, ReverseSubtractF32, MultiplyF32, FmaF32, MinimumF32, MaximumF32
    case ConditionalMask, AddU32, ShiftLeft32, And32
  }

  case class DualVector(
      operation: DualVectorOperation,
      dest: VectorRegisterIndex,
      left: InstructionSource,
      right: InstructionSource,
      addend: InstructionSource
  )

  enum VectorBinaryOperation {
    case AddU32, MultiplyLowU32, MultiplyHighU32, MultiplyHighI32
    case ShiftRightArithmeticI32, ShiftRightArithmeticI16, ShiftLeft32, ShiftRight32, ShiftLeft16, ShiftRight16
    case AddU16, And32, Or32, Xor32, SubtractU16, SubtractU32, SubtractReverseU32
    case SubtractReverseF32, MultiplyU24, MultiplyHighU24, MultiplyI24, MultiplyHighI24, MultiplyLowU16
    case MinimumU32, MaximumU32, MinimumI32, MinimumI16, MinimumU16, MinimumF32, MinimumF16
    case MaximumI32, MaximumI16, MaximumU16, MaximumF32, MaximumF16
    case LoadExponentF32, AddF32, MultiplyF32, MultiplyAccumulateF32, AddF16, MultiplyF16
    case FusedMultiplyAccumulateF16, SubtractF32
    case AddCarryU32, AddWithCarryU32, SubtractCarryU32, SubtractWithBorrowU32
  }

  enum VectorSubDwordOperation {
    case ConditionalMask32
    case AddU32, AddU16, AddF16, AddCarryU32, AddWithCarryU32
    case And32, Or32, Xor32
    case ShiftLeft16, ShiftLeft32, ShiftRight16, ShiftRight32
    case MaximumI16, MaximumF16, MinimumI16, MinimumU16, MinimumF16
    case MultiplyLowU16, MultiplyF16, MultiplyU24, MultiplyI24
    case SubtractU16, SubtractU32, SubtractCarryU32, SubtractWithBorrowU32
    case SubtractReverseU32, SubtractReverseCarryU32, SubtractReverseWithBorrowU32
  }

  enum VectorBinary64Operation {
    case LoadExponent, Add, Multiply, Maximum, Minimum
  }

  enum VectorPackedBinaryOperation {
    case AddU16, AddF16, MultiplyLowU16, MultiplyF16, MaximumF16, MinimumF16
    case MaximumI16, MaximumU16, SubtractI16, ShiftRight16
  }

  enum VectorTernaryOperation {
    case Add3U32, Or3B32, Xor3B32
    case Maximum3I32, Maximum3U32, Minimum3I32, Minimum3F32, Minimum3U32
    case Median3I16, Median3I32, Median3U16, Median3U32
    case Maximum3I16, Maximum3U16, Minimum3I16, Minimum3U16
    case MultiplyAddU16, FusedMultiplyAddF16, DivisionFusedMultiplyAddF32
    case MultiplyAddLegacyU16, MultiplyAddU24, MultiplyAddI24
    case AlignBit32, BitFieldInsert32, AndOr32, BitFieldExtractI32, BitFieldExtractU32
    case XorAddU32, AddShiftLeftU32, ShiftLeftAddU32, ShiftLeftOr32, PermuteBytes32
  }

  enum VectorComparisonKind {
    case Signed16, Signed32, Signed64, Unsigned16, Unsigned32, Unsigned64
    case Float16, Float32, Float64, ClassFloat32, ClassFloat64
  }

  enum VectorComparisonRelation {
    case Equal, NotEqual, OrderedNotEqual, Less, LessOrEqual, Greater, GreaterOrEqual
    case NotLess, NotLessOrEqual, NotGreater, NotGreaterOrEqual
    case Ordered, Unordered, NotLessOrGreater, Class
  }

  case class VectorComparison(
      kind: VectorComparisonKind,
      relation: VectorComparisonRelation,
      updateExec: Boolean
  )

  enum Instruction {

    case SLoad(
        dest: ScalarRegisterIndex,
        count: Int in (1, 2, 3, 4, 8, 16),
        base: ScalarRegisterIndex,
        offset: Long,
        scalarOffset: Option[ScalarRegisterIndex],
        elementBytes: Int in (1, 2, 4) = 4
    )
    case SMove(dest: ScalarRegisterIndex, src: InstructionSource)
    case SMultiplyImmediate(dest: ScalarRegisterIndex, value: Int)
    case SMove64(dest: ScalarRegisterIndex, src: InstructionSource)

    /** Set a hardware register (for example the flat-scratch base). */
    case SSetReg(hwreg: Int, src: ScalarRegisterIndex)

    /** Write the low `size` bits of `src` into MODE from bit `offset`. */
    case SSetMode(offset: Int in 0 ~ 31, size: Int in 1 ~ 32, src: InstructionSource)

    /** Read `size` bits of MODE from bit `offset` into `dest`. */
    case SGetMode(dest: ScalarRegisterIndex, offset: Int in 0 ~ 31, size: Int in 1 ~ 32)
    case SGetPc(dest: ScalarRegisterIndex, offset: Int)
    case SSetPc(src: InstructionSource)
    case SSwapPc(dest: ScalarRegisterIndex, src: InstructionSource)

    /** Save the current EXEC mask and update EXEC with a 32- or 64-bit mask. */
    case SSaveExec(
        operation: SaveExecOperation,
        dest: ScalarRegisterIndex,
        src: InstructionSource,
        width: Int in (32, 64)
    )
    case SUnary(operation: ScalarUnaryOperation, dest: ScalarRegisterIndex, src: InstructionSource)
    case SReciprocalF32(dest: ScalarRegisterIndex, src: InstructionSource, modifiers: SourceModifiers, clamp: Boolean)
    case SFmaF32(dest: ScalarRegisterIndex, srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource)
    case SBinary(
        operation: ScalarBinaryOperation,
        dest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource
    )
    case SMinMax(
        operation: ScalarMinMaxOperation,
        dest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource
    )
    case SBinary64(
        operation: ScalarBinary64Operation,
        dest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource
    )
    case SSetGprIdxOn
    case SCSelect(dest: ScalarRegisterIndex, srcA: InstructionSource, srcB: InstructionSource, width: Int in (32, 64))
    case SAddImmediate(dest: ScalarRegisterIndex, immediate: Int)
    case SFloatCompare(operation: VectorComparison, srcA: InstructionSource, srcB: InstructionSource)
    case SCmp(operation: ScalarCompareOperation, srcA: InstructionSource, srcB: InstructionSource)
    case VMove(dest: VectorRegisterIndex, src: InstructionSource)
    case VDual(left: DualVector, right: DualVector)
    case VMove64(dest: VectorRegisterIndex, src: InstructionSource)
    case VPackedF32(
        operation: PackedF32Operation,
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        lowSelection: Int,
        highSelection: Int,
        negateLow: Int,
        negateHigh: Int
    )
    case VBitOp3(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        table: Int,
        half: Boolean
    )
    case VAccumulatorRead(dest: VectorRegisterIndex, src: VectorRegisterIndex)
    case VAccumulatorWrite(dest: VectorRegisterIndex, src: InstructionSource)
    case VAccumulatorMove(dest: VectorRegisterIndex, src: VectorRegisterIndex)
    case VMatrixF32(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        size: Int in (4, 16, 32),
        blockBroadcast: Int where Between[0, 4],
        blockId: Int where Between[0, 15],
        accumulatorMask: Int where Between[0, 7],
        broadcast: Int where Between[0, 7]
    )
    case VWaveMatrixF16(
        dest: VectorRegisterIndex,
        srcA: VectorRegisterIndex,
        srcB: VectorRegisterIndex,
        srcC: InstructionSource,
        halfResult: Boolean,
        high: Boolean,
        negateLow: Int,
        negateHigh: Int
    )
    case VPackedMove32(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        highA: Boolean,
        highB: Boolean
    )

    /** Read a VGPR through the scalar M0 register (v_movrels_b32). */
    case VMoveRelative(dest: VectorRegisterIndex, src: InstructionSource)

    /** Write a VGPR through the scalar M0 register (v_movreld_b32). */
    case VMoveRelativeDestination(dest: VectorRegisterIndex, src: InstructionSource)
    case VNot(dest: VectorRegisterIndex, src: InstructionSource)
    case VSwap(dest: VectorRegisterIndex, src: InstructionSource)
    case VBitReverse(dest: VectorRegisterIndex, src: InstructionSource)
    case VBitFind(operation: VectorBitFindOperation, dest: VectorRegisterIndex, src: InstructionSource)
    case VConvert(
        operation: VectorConvertOperation,
        dest: VectorRegisterIndex,
        src: InstructionSource,
        modifiers: SourceModifiers = SourceModifiers.Unmodified,
        clamp: Boolean = false
    )

    /** Executes `ignored` while MODE makes the hardware ignore OMOD; otherwise executes `applied`, which omits the
      * clamp, then scales its `width`-bit result in `dest` and clamps.
      */
    case VOutputModified(
        ignored: Instruction,
        applied: Instruction,
        dest: VectorRegisterIndex,
        scale: OutputScale,
        width: Int in (16, 32, 64),
        clamp: Boolean
    )

    /** Executes `operation`, whose 16-bit result lands in the low half of `dest`, then moves it into the half `high`
      * names while the other half keeps its previous value.
      */
    case VHalfResult(operation: Instruction, dest: VectorRegisterIndex, high: Boolean)

    /** Executes `operation`, then clamps its `width`-bit floating-point result in `dest` to [0, 1]. */
    case VClampResult(operation: Instruction, dest: VectorRegisterIndex, width: Int in (16, 32, 64))

    /** Executes `operation`, whose source 0 is [[InstructionSource.DPP]], on the lanes `control` enables. */
    case VDPP(operation: Instruction, src: VectorRegisterIndex, control: DPPControl, fetchInactive: Boolean)
    case VCndMask(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        condition: InstructionSource,
        modifiers: SourceModifiers = SourceModifiers.Unmodified
    )
    case VReadFirstLane(dest: ScalarRegisterIndex, src: InstructionSource)
    case VReadLane(dest: ScalarRegisterIndex, src: InstructionSource, lane: InstructionSource)
    case VWriteLane(dest: VectorRegisterIndex, src: InstructionSource, lane: InstructionSource)
    case VMaskBitCount(dest: VectorRegisterIndex, mask: InstructionSource, base: InstructionSource, high: Boolean)
    case VBitCount(dest: VectorRegisterIndex, src: InstructionSource, base: InstructionSource)
    case VBinary(
        operation: VectorBinaryOperation,
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        clamp: Boolean,
        modifiers: SourceModifiers = SourceModifiers.Unmodified
    )

    /** Two packed f16 products accumulated into an f32 destination. */
    case VDot2F32F16(dest: VectorRegisterIndex, srcA: InstructionSource, srcB: InstructionSource, accumulate: Boolean)

    /** Four unsigned byte products accumulated into a u32 destination. */
    case VDot4U32U8(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource
    )
    case VTrigPreopF64(
        dest: VectorRegisterIndex,
        src: InstructionSource,
        segment: InstructionSource,
        modifiers: SourceModifiers
    )

    case VBinary64(
        operation: VectorBinary64Operation,
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        modifiers: SourceModifiers
    )
    case VBinarySubDword(
        operation: VectorSubDwordOperation,
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        destSelection: SubDwordSelection,
        destUnused: SubDwordDestinationUnused,
        srcASelection: SubDwordSelection,
        srcBSelection: SubDwordSelection,
        srcASignExtend: Boolean,
        srcBSignExtend: Boolean
    )
    case VPackedBinary(
        operation: VectorPackedBinaryOperation,
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcAHighForHigh: Boolean,
        srcBHighForHigh: Boolean
    )

    /** Packs the binary16 halves `opSelection` picks (bit `i` takes source `i`'s high half). */
    case VPackB32F16(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        modifiers: SourceModifiers = SourceModifiers.Unmodified,
        opSelection: Int = 0,
        clamp: Boolean = false
    )
    case VPackedFmaF16(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        lowSelection: Int,
        highSelection: Int,
        negateLow: Int,
        negateHigh: Int,
        clamp: Boolean
    )

    /** FMA with mixed f32/f16 inputs and an f16 result in one half of the destination VGPR.
      *
      * `opSelectionHi` selects which inputs are converted from f16; for those inputs `opSelection` selects the low or
      * high half of the source VGPR. The source modifier bit convention is the FP16 input convention, where bit 0 is
      * negate and bit 1 is absolute (rather than packed neg_hi).
      */
    case VFmaMixF16(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        opSelection: Int,
        opSelectionHi: Int,
        negate: Int,
        absolute: Int,
        clamp: Boolean,
        high: Boolean,
        fused: Boolean = true
    )

    /** FMA with mixed f32/f16 inputs and an f32 result; the unfused V_MAD_MIX rounds the product first. */
    case VFmaMixF32(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        opSelection: Int,
        opSelectionHi: Int,
        negate: Int,
        absolute: Int,
        clamp: Boolean,
        fused: Boolean = true
    )
    case VAddCarry(
        dest: VectorRegisterIndex,
        carryDest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        carryIn: Option[InstructionSource],
        clamp: Boolean = false
    )
    case VSubCarry(
        dest: VectorRegisterIndex,
        carryDest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        carryIn: Option[InstructionSource],
        clamp: Boolean = false
    )
    case VSubReverseCarry(
        dest: VectorRegisterIndex,
        carryDest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        carryIn: Option[InstructionSource],
        clamp: Boolean = false
    )
    case VMadF32(
        operation: VectorMadF32Operation,
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        modifiers: SourceModifiers,
        clamp: Boolean
    )
    case VMadF64(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        modifiers: SourceModifiers
    )

    /** Floating-point operations honour `modifiers`; `clamp` saturates the integer multiply-adds. */
    case VTernary(
        operation: VectorTernaryOperation,
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        modifiers: SourceModifiers = SourceModifiers.Unmodified,
        clamp: Boolean = false
    )
    case VMadU64U32(
        dest: VectorRegisterIndex,
        carryDest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        clamp: Boolean = false
    )
    case VMadI64I32(
        dest: VectorRegisterIndex,
        carryDest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        clamp: Boolean = false
    )

    /** Writes the lane mask to `dest`, or to EXEC for a compare-and-update; SDWA forms compare selected sub-dwords. */
    case VCompare(
        operation: VectorComparison,
        dest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        modifiers: SourceModifiers = SourceModifiers.Unmodified,
        selection: Option[SubDwordSources] = None
    )
    case VDivScaleF32(
        dest: VectorRegisterIndex,
        conditionDest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        modifiers: SourceModifiers = SourceModifiers.Unmodified
    )
    case VDivScaleF64(
        dest: VectorRegisterIndex,
        conditionDest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        modifiers: SourceModifiers = SourceModifiers.Unmodified
    )
    case VDivFmasF64(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        modifiers: SourceModifiers = SourceModifiers.Unmodified
    )
    case VDivFixupF32(
        dest: VectorRegisterIndex,
        quotient: InstructionSource,
        denominator: InstructionSource,
        numerator: InstructionSource,
        absolute: Int,
        negate: Int
    )
    case VDivFixupF64(
        dest: VectorRegisterIndex,
        quotient: InstructionSource,
        denominator: InstructionSource,
        numerator: InstructionSource,
        absolute: Int,
        negate: Int
    )
    case VShiftLeft64(dest: VectorRegisterIndex, amount: InstructionSource, src: InstructionSource)
    case VShiftLeftAdd64(
        dest: VectorRegisterIndex,
        src: InstructionSource,
        amount: InstructionSource,
        addend: InstructionSource
    )
    case VShiftRight64(dest: VectorRegisterIndex, amount: InstructionSource, src: InstructionSource)
    case VShiftRightArithmetic64(dest: VectorRegisterIndex, amount: InstructionSource, src: InstructionSource)
    case CacheInvalidate
    case CacheWritebackInvalidate
    case Load(
        dest: VectorRegisterIndex,
        address: MemoryAddress,
        bytes: Int in 1 ~ 16,
        signed: Boolean,
        placement: LoadPlacement,
        bank: RegisterBank = RegisterBank.Vector,
        coherent: Boolean = false
    )

    /** Stores the low `bytes` of `src`, or of its high half when `high`. */
    case Store(
        src: VectorRegisterIndex,
        address: MemoryAddress,
        bytes: Int in 1 ~ 16,
        high: Boolean,
        bank: RegisterBank = RegisterBank.Vector
    )
    case Atomic(
        operation: AtomicOperation,
        dest: VectorRegisterIndex,
        address: MemoryAddress,
        src: VectorRegisterIndex,
        bytes: Int in (4, 8),
        returnValue: Boolean
    )

    /** Swaps in `data` when memory equals the comparand in the following registers. */
    case CompareSwap(
        dest: VectorRegisterIndex,
        address: MemoryAddress,
        data: VectorRegisterIndex,
        bytes: Int in (4, 8),
        returnValue: Boolean
    )

    /** LDS read2: two elements from `address + first` and `address + second` into consecutive registers. */
    case LoadPair(
        dest: VectorRegisterIndex,
        address: VectorRegisterIndex,
        bytes: Int in (4, 8),
        first: Int,
        second: Int
    )
    case StorePair(
        address: VectorRegisterIndex,
        srcA: VectorRegisterIndex,
        srcB: VectorRegisterIndex,
        bytes: Int in (4, 8),
        first: Int,
        second: Int
    )
    case DSPermute(dest: VectorRegisterIndex, address: VectorRegisterIndex, src: VectorRegisterIndex, offset: Int)
    case DSBPermute(dest: VectorRegisterIndex, address: VectorRegisterIndex, src: VectorRegisterIndex, offset: Int)
    case DSSwizzle(dest: VectorRegisterIndex, src: VectorRegisterIndex, control: Int)
    case BranchRelative(condition: ScalarBranchCondition, displacement: Int)
    case Branch(condition: ScalarBranchCondition, target: Int)
    case Barrier
    case BarrierSignal(id: InstructionSource, isFirst: Boolean)
    case BarrierWait(id: Int)
    case Wait
    case End
    case Trap(code: Int)
  }

  class Kernel(
      val source: CodeObjectKernel,
      val instructions: Vec[Instruction],
      val instructionIndices: Map[Int, Int],
      val instructionOffsets: Vec[Int],
      val entryInstruction: Int
  )
}
