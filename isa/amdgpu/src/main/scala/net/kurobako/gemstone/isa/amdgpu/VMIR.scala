package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
private object VMIR {

  enum ScalarBranchCondition {
    case Always, SccZero, SccNonZero, VccZero, VccNonZero, ExecZero, ExecNonZero
  }

  enum InstructionSource {
    case Scalar(index: Int)
    case VectorRegister(index: Int)
    case Constant(value: Long)

    /** A literal dword embedded in the instruction stream.
      *
      * Literals share the raw integer representation of [[Constant]], but are kept distinct because floating-point
      * instructions promote them from binary32 to their operand width rather than treating the dword as an integer
      * value.
      */
    case Literal(value: Long)
  }

  enum SubDwordSelection(
      val width: Int where Between[1, 64],
      val shift: Int where Between[0, 24],
      val upperBit: Int where Between[1, 64]
  ) {
    case Byte0 extends SubDwordSelection(8, 0: Int where Between[0, 24], 8)
    case Byte1 extends SubDwordSelection(8, 8: Int where Between[0, 24], 16)
    case Byte2 extends SubDwordSelection(8, 16: Int where Between[0, 24], 24)
    case Byte3 extends SubDwordSelection(8, 24: Int where Between[0, 24], 32)
    case Word0 extends SubDwordSelection(16, 0: Int where Between[0, 24], 16)
    case Word1 extends SubDwordSelection(16, 16: Int where Between[0, 24], 32)
    case DWord extends SubDwordSelection(32, 0: Int where Between[0, 24], 32)
  }

  enum SubDwordDestinationUnused {
    case Pad, SignExtend, Preserve
  }

  enum ScalarUnaryOperation {
    case AbsoluteI32, SignExtendI8, SignExtendI16, CountBits32, CountBits64, ReverseBits32
    case Not32, Not64, FirstBitHighI32, ClearBit32, SetBit32
  }

  enum ScalarBinaryOperation {
    case And32, Or32, MultiplyI32, MultiplyHighU32, MultiplyHighI32, AddI32, SubtractI32
    case ShiftLeft32, ShiftLeftTwoAndAddU32, ShiftRightArithmetic32, BitFieldExtractU32, BitFieldExtractI32,
      ShiftRight32
    case AndNot32, OrNot32, Nor32
    case PackLowLow16, PackLowHigh16, PackHighHigh16, AddU32, AddCarryU32, SubtractU32, SubtractBorrowU32, Xor32
  }

  enum ScalarMinMaxOperation {
    case MinimumU32, MaximumU32, MinimumI32, MaximumI32
  }

  enum ScalarBinary64Operation {
    case And, AndNot, Or, OrNot, Nor, Xor, ShiftLeft, ShiftRight, ShiftRightArithmetic, BitFieldExtractI64
  }

  enum AtomicBinaryOperation {
    case Add, Subtract, MinimumUnsigned, MaximumUnsigned, MinimumSigned, MaximumSigned, And, Or, Xor
  }

  enum ScalarCompareOperation {
    case EqualI32, NotEqualI32, GreaterI32, GreaterOrEqualI32, LessI32, LessOrEqualI32
    case EqualU32, NotEqualU32, GreaterU32, GreaterOrEqualU32, LessU32, LessOrEqualU32
    case EqualU64, NotEqualU64, BitSet32, BitClear32
  }

  enum ScalarImmediateCompareOperation {
    case EqualI32, NotEqualI32, GreaterI32, LessI32
    case EqualU32, NotEqualU32, GreaterU32, GreaterOrEqualU32, LessU32, LessOrEqualU32
  }

  enum VectorBitFindOperation {
    case FirstBitLow32, FirstBitHighI32, FirstBitHighU32
  }

  enum VectorConvertOperation {
    case FrexpExponentF64, FrexpExponentF32, FrexpMantissaF32, FrexpMantissaF64
    case ReciprocalF32, ReciprocalF64, TruncateF32, FloorF32, RoundNearestEvenF32
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

  enum VectorF32BinaryOperation {
    case Add, Subtract, ReverseSubtract, Multiply, LoadExponent, Maximum, Minimum
  }

  enum VectorSubDwordOperation {
    case ConditionalMask32
    case AddU32, AddU16, AddF16, AddCarryU32, AddWithCarryU32
    case And32, Or32, Xor32
    case ShiftLeft16, ShiftLeft32, ShiftRight16, ShiftRight32
    case MaximumI16, MaximumF16, MinimumI16, MinimumU16, MinimumF16
    case MultiplyLowU16, MultiplyF16, MultiplyU24, MultiplyI24
    case SubtractU16, SubtractU32, SubtractCarryU32, SubtractWithBorrowU32
    case SubtractReverseU32, SubtractReverseWithBorrowU32
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
    case MultiplyAddU16, FusedMultiplyAddF32, FusedMultiplyAddF16, DivisionFusedMultiplyAddF32
    case MultiplyAddLegacyU16, MultiplyAddU24, MultiplyAddI24
    case AlignBit32, BitFieldInsert32, AndOr32, BitFieldExtractI32, BitFieldExtractU32
    case XorAddU32, AddShiftLeftU32, ShiftLeftAddU32, ShiftLeftOr32, PermuteBytes32
  }

  enum DSAtomicOperation {
    case AddU32, SubtractU32, IncrementU32, DecrementU32
    case MinimumI32, MaximumI32, MinimumU32, MaximumU32
    case AddU64, SubtractU64, IncrementU64, DecrementU64
    case And32, Or32, Xor32, And64, Or64, Xor64
    case MinimumI64, MaximumI64, MinimumU64, MaximumU64
    case AddF32, MinimumF32, MaximumF32, AddF64, MinimumF64, MaximumF64
  }

  enum VectorComparisonKind {
    case Signed16, Signed32, Signed64, Unsigned16, Unsigned32, Unsigned64
    case Float16, Float32, Float64, ClassFloat32, ClassFloat64
  }

  enum VectorComparisonRelation {
    case Equal, NotEqual, Less, LessOrEqual, Greater, GreaterOrEqual
    case NotLess, NotLessOrEqual, NotGreater, NotGreaterOrEqual
    case Ordered, Unordered, NotLessOrGreater, Class
  }

  case class VectorComparison(
      kind: VectorComparisonKind,
      relation: VectorComparisonRelation,
      updateExec: Boolean
  )

  enum Instruction {

    case SLoad(dest: Int, count: Int in (1, 2, 4, 8, 16), base: Int, offset: Int, scalarOffset: Option[Int])
    case SMove(dest: Int, src: InstructionSource)
    case SMoveImmediate(dest: Int, value: Int)
    case SMultiplyImmediate(dest: Int, value: Int)
    case SMove64(dest: Int, src: InstructionSource)

    /** Set a hardware register (for example the flat-scratch base). */
    case SSetReg(hwreg: Int, src: Int)
    case SGetPc(dest: Int, offset: Int)
    case SSetPc(src: InstructionSource)
    case SSwapPc(dest: Int, src: InstructionSource)

    /** Save the current EXEC mask and update EXEC with a 32- or 64-bit mask. */
    case SAndSaveExec(dest: Int, src: InstructionSource, width: Int)
    case SAndn2SaveExec(dest: Int, src: InstructionSource, width: Int)
    case SOrSaveExec(dest: Int, src: InstructionSource, width: Int)
    case SXorSaveExec(dest: Int, src: InstructionSource, width: Int)
    case SUnary(operation: ScalarUnaryOperation, dest: Int, src: InstructionSource)
    case SBinary(operation: ScalarBinaryOperation, dest: Int, srcA: InstructionSource, srcB: InstructionSource)
    case SMinMax(operation: ScalarMinMaxOperation, dest: Int, srcA: InstructionSource, srcB: InstructionSource)
    case SBinary64(operation: ScalarBinary64Operation, dest: Int, srcA: InstructionSource, srcB: InstructionSource)
    case SSetGprIdxOn
    case SCSelect(dest: Int, srcA: InstructionSource, srcB: InstructionSource, width: Int)
    case SCmpImmediate(operation: ScalarImmediateCompareOperation, src: Int, immediate: Int)
    case SAddImmediate(dest: Int, immediate: Int)
    case SCmp(operation: ScalarCompareOperation, srcA: InstructionSource, srcB: InstructionSource)
    case VMove(dest: Int, src: InstructionSource)

    /** Read a VGPR through the scalar M0 register (v_movrels_b32). */
    case VMoveRelative(dest: Int, src: InstructionSource)

    /** Write a VGPR through the scalar M0 register (v_movreld_b32). */
    case VMoveRelativeDestination(dest: Int, src: InstructionSource)
    case VNot(dest: Int, src: InstructionSource)
    case VSwap(dest: Int, src: InstructionSource)
    case VBitReverse(dest: Int, src: InstructionSource)
    case VBitFind(operation: VectorBitFindOperation, dest: Int, src: InstructionSource)
    case VConvert(operation: VectorConvertOperation, dest: Int, src: InstructionSource)
    case VMoveDPP(
        dest: Int,
        src: Int,
        control: Int,
        rowMask: Int,
        bankMask: Int,
        boundControl: Boolean,
        fetchInactive: Boolean
    )
    case VCndMask(
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        condition: InstructionSource,
        absA: Boolean,
        absB: Boolean,
        negateA: Boolean,
        negateB: Boolean
    )
    case VReadFirstLane(dest: Int, src: InstructionSource)
    case VReadLane(dest: Int, src: InstructionSource, lane: InstructionSource)
    case VWriteLane(dest: Int, src: InstructionSource, lane: InstructionSource)
    case VMaskBitCount(dest: Int, mask: InstructionSource, base: InstructionSource, high: Boolean)
    case VBitCount(dest: Int, src: InstructionSource, base: InstructionSource)
    case VBinary(
        operation: VectorBinaryOperation,
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        clamp: Boolean
    )

    /** Two packed f16 products accumulated into an f32 destination. */
    case VDot2F32F16(dest: Int, srcA: InstructionSource, srcB: InstructionSource, accumulate: Boolean)

    /** Four unsigned byte products accumulated into a u32 destination. */
    case VDot4U32U8(dest: Int, srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource)
    case VBinaryF32Modified(
        operation: VectorF32BinaryOperation,
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        absA: Boolean,
        absB: Boolean,
        negateA: Boolean,
        negateB: Boolean,
        clamp: Boolean
    )
    case VUnaryF32Modified(
        dest: Int,
        src: InstructionSource,
        absolute: Boolean,
        negate: Boolean,
        clamp: Boolean
    )
    case VFrexpMantF32Modified(
        dest: Int,
        src: InstructionSource,
        absolute: Boolean,
        negate: Boolean,
        clamp: Boolean
    )
    case VCvtF64F32Modified(
        dest: Int,
        src: InstructionSource,
        absolute: Boolean,
        negate: Boolean,
        clamp: Boolean
    )
    case VUnaryF64Modified(
        dest: Int,
        src: InstructionSource,
        absolute: Boolean,
        negate: Boolean
    )
    case VTrigPreopF64(
        dest: Int,
        src: InstructionSource,
        segment: InstructionSource,
        absolute: Boolean,
        negate: Boolean
    )
    case VFlatLoadD16(dest: Int, address: Int, offset: Int, high: Boolean)
    case VMadMkF32(dest: Int, srcA: InstructionSource, multiplier: Long, addend: InstructionSource)

    /** Fused multiply-add with an inline literal addend: dst = srcA * srcB + literal. */
    case VMadAkF32(dest: Int, srcA: InstructionSource, srcB: InstructionSource, addend: Long)
    case VBinary64Modified(
        operation: VectorBinary64Operation,
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        absA: Boolean,
        absB: Boolean,
        negateA: Boolean,
        negateB: Boolean
    )
    case VBinarySubDword(
        operation: VectorSubDwordOperation,
        dest: Int,
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
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcAHighForHigh: Boolean,
        srcBHighForHigh: Boolean
    )
    case VPackB32F16(dest: Int, srcA: InstructionSource, srcB: InstructionSource)
    case VPackedFmaF16(
        dest: Int,
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
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        opSelection: Int,
        opSelectionHi: Int,
        negate: Int,
        absolute: Int,
        clamp: Boolean,
        high: Boolean
    )

    /** FMA with mixed f32/f16 inputs and an f32 result. */
    case VFmaMixF32(
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        opSelection: Int,
        opSelectionHi: Int,
        negate: Int,
        absolute: Int,
        clamp: Boolean
    )
    case VAddCarry(
        dest: Int,
        carryDest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        carryIn: Option[InstructionSource]
    )
    case VSubCarry(
        dest: Int,
        carryDest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        carryIn: Option[InstructionSource]
    )
    case VSubReverseCarry(
        dest: Int,
        carryDest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        carryIn: Option[InstructionSource]
    )
    case VMadF32(
        operation: VectorMadF32Operation,
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        absA: Boolean,
        absB: Boolean,
        absC: Boolean,
        negateA: Boolean,
        negateB: Boolean,
        negateC: Boolean,
        clamp: Boolean
    )
    case VMadF64(
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        absA: Boolean,
        absB: Boolean,
        absC: Boolean,
        negateA: Boolean,
        negateB: Boolean,
        negateC: Boolean
    )
    case VTernary(
        operation: VectorTernaryOperation,
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource
    )
    case VMadU64U32(
        dest: Int,
        carryDest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource
    )
    case VMadI64I32(
        dest: Int,
        carryDest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource
    )
    case VCompare(operation: VectorComparison, srcA: InstructionSource, srcB: InstructionSource)
    case VCompareSubDword(
        operation: VectorComparison,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcASelection: SubDwordSelection,
        srcBSelection: SubDwordSelection,
        srcASignExtend: Boolean,
        srcBSignExtend: Boolean,
        destination: Option[Int]
    )
    case VCompareTo(
        dest: Int,
        operation: VectorComparison,
        srcA: InstructionSource,
        srcB: InstructionSource,
        absA: Boolean,
        absB: Boolean,
        negateA: Boolean,
        negateB: Boolean
    )
    case VDivScaleF32(
        dest: Int,
        conditionDest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource
    )
    case VDivScaleF64(
        dest: Int,
        conditionDest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource
    )
    case VDivFmasF64(dest: Int, srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource)
    case VDivFixupF32(
        dest: Int,
        quotient: InstructionSource,
        denominator: InstructionSource,
        numerator: InstructionSource,
        absolute: Int,
        negate: Int
    )
    case VDivFixupF64(
        dest: Int,
        quotient: InstructionSource,
        denominator: InstructionSource,
        numerator: InstructionSource,
        absolute: Int,
        negate: Int
    )
    case VShiftLeft64(dest: Int, amount: InstructionSource, src: InstructionSource)
    case VShiftRight64(dest: Int, amount: InstructionSource, src: InstructionSource)
    case VShiftRightArithmetic64(dest: Int, amount: InstructionSource, src: InstructionSource)
    case GlobalLoad(
        dest: Int,
        address: Int,
        scalarAddress: Option[Int],
        bytes: Int where Between[1, 16],
        offset: Int,
        coherent: Boolean
    )
    case GlobalLoadSigned(
        dest: Int,
        address: Int,
        scalarAddress: Option[Int],
        bytes: Int where Between[1, 2],
        offset: Int
    )
    case GlobalLoadD16(dest: Int, address: Int, scalarAddress: Option[Int], offset: Int, high: Boolean)
    case GlobalStore(
        address: Int,
        scalarAddress: Option[Int],
        src: Int,
        bytes: Int where Between[1, 16],
        offset: Int
    )
    case GlobalStoreD16High(
        address: Int,
        scalarAddress: Option[Int],
        src: Int,
        bytes: Int where Between[1, 2],
        offset: Int
    )
    case GlobalAtomicAdd(
        dest: Int,
        address: Int,
        scalarAddress: Option[Int],
        src: Int,
        bytes: Int where Between[4, 8],
        offset: Int,
        returnValue: Boolean
    )
    case GlobalAtomicSwap(
        dest: Int,
        address: Int,
        scalarAddress: Option[Int],
        src: Int,
        bytes: Int where Between[4, 8],
        offset: Int,
        returnValue: Boolean
    )
    case GlobalAtomicBinary(
        operation: AtomicBinaryOperation,
        dest: Int,
        address: Int,
        scalarAddress: Option[Int],
        src: Int,
        bytes: Int where Between[4, 8],
        offset: Int,
        returnValue: Boolean
    )
    case GlobalAtomicInc(
        dest: Int,
        address: Int,
        scalarAddress: Option[Int],
        src: Int,
        bytes: Int where Between[4, 8],
        offset: Int,
        returnValue: Boolean
    )
    case GlobalAtomicCompareSwap(
        dest: Int,
        address: Int,
        scalarAddress: Option[Int],
        compareSrc: Int,
        swapSrc: Int,
        bytes: Int where Between[4, 8],
        offset: Int,
        returnValue: Boolean
    )
    case FlatLoad(dest: Int, address: Int, bytes: Int where Between[1, 16], offset: Int, coherent: Boolean = false)
    case FlatLoadSigned(dest: Int, address: Int, bytes: Int where Between[1, 2], offset: Int)
    case FlatStore(address: Int, src: Int, bytes: Int where Between[1, 16], offset: Int)
    case FlatStoreD16High(address: Int, src: Int, bytes: Int where Between[1, 2], offset: Int)
    case BufferCacheInvalidate
    case BufferLoad(
        dest: Int,
        address: Option[Int],
        resource: Int,
        scalarOffset: Option[Int],
        bytes: Int where Between[1, 16],
        offset: Int
    )
    case BufferLoadD16(
        dest: Int,
        address: Option[Int],
        resource: Int,
        scalarOffset: Option[Int],
        bytes: Int where Between[1, 2],
        offset: Int,
        high: Boolean
    )
    case BufferStore(
        address: Option[Int],
        resource: Int,
        scalarOffset: Option[Int],
        src: Int,
        bytes: Int where Between[1, 16],
        offset: Int
    )
    case BufferStoreD16High(
        address: Option[Int],
        resource: Int,
        scalarOffset: Option[Int],
        src: Int,
        bytes: Int where Between[1, 2],
        offset: Int
    )
    case DSRead(dest: Int, address: Int, bytes: Int where Between[1, 16], offsetA: Int, offsetB: Option[Int])
    case DSSignedRead(dest: Int, address: Int, bytes: Int where Between[1, 2], offsetA: Int, offsetB: Option[Int])
    case DSReadD16(dest: Int, address: Int, bytes: Int where Between[1, 2], offset: Int, high: Boolean)
    case DSSignedReadD16(dest: Int, address: Int, bytes: Int where Between[1, 2], offset: Int, high: Boolean)
    case DSPermute(dest: Int, address: Int, src: Int, offset: Int)
    case DSBPermute(dest: Int, address: Int, src: Int, offset: Int)
    case DSSwizzle(dest: Int, src: Int, control: Int)
    case DSAtomic(
        operation: DSAtomicOperation,
        dest: Int,
        address: Int,
        src: Int,
        bytes: Int where Between[4, 8],
        offset: Int,
        returnValue: Boolean
    )
    case DSWrite(
        address: Int,
        srcA: Int,
        srcB: Option[Int],
        bytes: Int where Between[1, 16],
        offsetA: Int,
        offsetB: Option[Int]
    )
    case DSWriteD16High(address: Int, src: Int, offset: Int)
    case DSWriteD16HighByte(address: Int, src: Int, offset: Int)
    case BranchRelative(condition: ScalarBranchCondition, displacement: Int)
    case Branch(condition: ScalarBranchCondition, target: Int)
    case Barrier
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
