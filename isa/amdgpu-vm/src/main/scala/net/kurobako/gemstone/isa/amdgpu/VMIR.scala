package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.domain.*
import net.kurobako.gemstone.vm.*
import scala.annotation.targetName
private object VMIR {
  type VectorRegisterIndex = Int in 0 ~ 255
  type ScalarRegisterIndex = Int in 0 ~ 127

  private def offsetIndex(index: Int, words: Int, last: Int, message: String): Int = {
    require(words <= last - index, message)
    index + words
  }

  extension (index: VectorRegisterIndex) {
    def offsetBy(words: Int >= 0): VectorRegisterIndex =
      offsetIndex(index, words, 255, "register span exceeds the register bank").unsafe[VectorRegisterIndex]
  }

  extension (index: ScalarRegisterIndex) {
    @targetName("offsetScalarRegister")
    def offsetBy(words: Int >= 0): ScalarRegisterIndex =
      offsetIndex(index, words, 127, "scalar register span exceeds the register bank").unsafe[ScalarRegisterIndex]
  }

  private def sourceRegister(isa: ISA, source: Src): Option[ScalarRegisterIndex] =
    Src.encode(isa, source).flatMap(_.toInt.refine[ScalarRegisterIndex].toOption)

  /** M0's SGPR: the code the source operand domain gives it, s124 until RDNA3 and s125 from there on. */
  def m0Register(isa: ISA): ScalarRegisterIndex =
    sourceRegister(isa, Src.M0).getOrElse(throw IllegalStateException(s"$isa has no M0 source"))

  /** The SGPR that reads zero and discards writes, where the source operand domain has one: from RDNA1 on. */
  def nullRegister(isa: ISA): Option[ScalarRegisterIndex] = sourceRegister(isa, Src.Null)

  enum ScalarBranchCondition derives CanEqual {
    case Always, SccZero, SccNonZero, VccZero, VccNonZero, ExecZero, ExecNonZero
  }

  enum InstructionSource derives CanEqual {
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

    /** The part of `source` an SDWA selection reads, shifted down and zero- or sign-extended to a dword. */
    case Selected(source: InstructionSource, selection: SubDwordSelection, signExtend: Boolean)
  }

  /** The bits of M0 that offset a VGPR operand: all of them for the relative moves of RDNA, the low byte for GFX9's
    * VGPR indexing and the relative moves before RDNA, and a ten-bit field of V_MOVRELSD_2_B32 and V_SWAPREL_B32 for
    * each of their two registers. An offset past the wave's VGPRs reads VGPR 0 and drops a write; the hardware bounds
    * it by the kernel's VGPR allocation, and the VM gives every wave all 256.
    */
  enum RelativeField(val shift: Int in (0, 16), val mask: Long) derives CanEqual {
    case Low         extends RelativeField(0, 0xffL)
    case Whole       extends RelativeField(0, 0xffffffffL)
    case Source      extends RelativeField(0, 0x3ffL)
    case Destination extends RelativeField(16, 0x3ffL)
  }

  /** DPP16 routes through a control with row and bank write masks; DPP8 selects within each group of eight lanes. */
  enum DPPControl {
    case DPP16(control: Int, rowMask: Int, bankMask: Int, boundControl: Boolean)
    case DPP8(selectors: Int)
  }

  /** VOP3 OMOD scaling of a floating-point result. */
  enum OutputScale(val factor: Double in (0.5, 2.0, 4.0)) {
    case Multiply2 extends OutputScale(2.0)
    case Multiply4 extends OutputScale(4.0)
    case Divide2   extends OutputScale(0.5)
  }

  object SourceModifiers {
    val Unmodified: SourceModifiers = SourceModifiers(absolute = 0, negate = 0)
  }

  /** VOP3 absolute-value and negation bits, bit `i` for source `i`, applied to a source's sign in that order. */
  case class SourceModifiers(absolute: Int, negate: Int) derives CanEqual {
    def apply(bits: Long, source: Int, width: Int in (16, 32, 64)): Long = {
      val sign      = 1L << (width - 1)
      val magnitude = if ((absolute & (1 << source)) != 0) bits & ~sign else bits
      if ((negate & (1 << source)) != 0) magnitude ^ sign else magnitude
    }
  }

  /** The E8M0 scales of a block-scaled matrix product: byte `selectionA` of `a` scales each row of A over each 32
    * elements of K, and byte `selectionB` of `b` each column of B.
    */
  case class MatrixScales(a: InstructionSource, selectionA: Int, b: InstructionSource, selectionB: Int)

  /** A matrix instruction's modifiers. `banks` bits 0, 1 and 2 place A, B and C/D in the accumulator registers.
    * `blockBroadcast` and `blockId` broadcast one block of A's lanes (CBSZ, ABID); `laneGroups` permutes B's lanes
    * (BLGP). `negateLow` and `negateHigh` bits 0 and 1 negate the elements of A and B in the low and high halves of
    * their registers, or whole; bit 2 of `negateLow` negates C and of `negateHigh` takes its absolute value. `high`
    * keeps gfx11's 16-bit C/D in the high halves, `indexSet` selects a sparse A's index set and `clamp` saturates.
    */
  case class MatrixControls(
      banks: Int = 0,
      blockBroadcast: Int = 0,
      blockId: Int = 0,
      laneGroups: Int = 0,
      negateLow: Int = 0,
      negateHigh: Int = 0,
      high: Boolean = false,
      indexSet: Int = 0,
      clamp: Boolean = false,
      scales: Option[MatrixScales] = None
  )

  case class SubDwordSources(
      selectionA: SubDwordSelection,
      selectionB: SubDwordSelection,
      signExtendA: Boolean,
      signExtendB: Boolean
  )

  enum RegisterBank derives CanEqual {
    case Vector, Accumulator
  }

  /** Per-lane memory operands; byte offsets are signed. */
  // Scratch carries a vector offset and a scalar base together in the SVE with SADDR hardware mode
  @SuppressWarnings(Array("baumkuchen:exclusive-options"))
  enum MemoryAddress {

    /** A 64-bit vector address, or a scalar base plus a 32-bit vector offset. */
    case Global(vector: VectorRegisterIndex, scalar: Option[ScalarRegisterIndex], offset: Int)

    /** A 64-bit vector address, or a scalar base plus a 32-bit vector offset, routed by aperture to LDS, private or
      * global memory.
      */
    case Flat(vector: VectorRegisterIndex, offset: Int, scalar: Option[ScalarRegisterIndex] = None)

    /** A byte offset into each lane's private segment. */
    case Scratch(vector: Option[VectorRegisterIndex], scalar: Option[ScalarRegisterIndex], offset: Int)

    /** An element of the buffer a resource descriptor names, by an index VGPR and offset by the `vector` VGPR, the
      * `scalar` SGPR or `scalarConstant` and `offset`; out-of-range lanes read zero and drop stores. A `wide` access,
      * GFX6 and GFX7's ADDR64, adds the `vector` pair to the descriptor's base unchecked.
      */
    case Buffer(
        vector: Option[VectorRegisterIndex],
        resource: ScalarRegisterIndex,
        scalar: Option[ScalarRegisterIndex],
        offset: Int,
        index: Option[VectorRegisterIndex] = None,
        scalarConstant: Int = 0,
        wide: Boolean = false
    )

    /** A 32-bit LDS address. */
    case Local(vector: VectorRegisterIndex, offset: Int)

    /** The texel of the image a resource descriptor names at an address; image atomics update it whole. */
    case Texel(address: ImageAddress)

    /** The LDS dword at M0[15:0] plus `offset` plus four bytes a lane, as the ADDTID forms address it. */
    case LaneLocal(offset: Int)

    /** The global dword at the SGPR pair `scalar`, or zero, plus `offset` plus four bytes a lane. */
    case LaneGlobal(scalar: Option[ScalarRegisterIndex], offset: Int)
  }

  /** A scalar memory access: the SGPR pair `base` plus `offset` plus the SGPR `scalarOffset` in global memory, or, when
    * `buffer`, that offset into the buffer the descriptor from `base` names, where elements past its end are not
    * accessed.
    */
  case class ScalarAddress(
      base: ScalarRegisterIndex,
      offset: Long,
      scalarOffset: Option[ScalarRegisterIndex],
      buffer: Boolean
  )

  /** Where loaded data lands: whole registers, or one 16-bit half preserving the other or, as ECC makes CDNA parts do,
    * zeroing it.
    */
  enum LoadPlacement derives CanEqual {
    case Full, Low, High, LowZeroingHigh, HighZeroingLow
  }

  /** How typed buffer and image components occupy registers: a dword each, two 16-bit halves to a register from the low
    * one, one half in the high half of the first register, or a half in the low bits of a register each.
    */
  enum Packing derives CanEqual {
    case Dword, Packed, High, Unpacked
  }

  /** The texel coordinates of an image access: the address words, consecutive or as the instruction lists them, two
    * 16-bit coordinates to a word under `a16`. The coordinates the dimension takes come first, then the mip level. GFX8
    * and GFX9 name no dimension, so the descriptor's type does.
    */
  case class ImageAddress(
      words: Vec[VectorRegisterIndex],
      resource: ScalarRegisterIndex,
      dimension: Option[ImageDimension],
      mip: Boolean,
      a16: Boolean
  )

  /** What an image instruction does with the texel it addresses. */
  enum ImageOperation derives CanEqual {

    /** Converts the texel through its format, or reads its channels' bits unconverted, sign-extended when `signed`. */
    case Load(converted: Boolean, signed: Boolean)
    case Store(converted: Boolean)

    /** The width, height, depth or layer count and level count of the mip level the address names. */
    case Dimensions
  }

  enum PackedF32Operation derives CanEqual {
    case Add, Multiply, Fma
  }

  /** How the new EXEC mask combines the explicit source with the previous mask. */
  enum SaveExecOperation derives CanEqual {
    case And, AndNot, Or, Xor, OrNot, Nand, Nor, Xnor, NotAnd, NotOr
  }

  /** The clocks a wave reads: the shader clock, its low 20 bits, and the 100 MHz real-time clock. */
  enum Clock derives CanEqual {
    case Shader, ShaderCycles, RealTime
  }

  enum ScalarUnaryOperation derives CanEqual {
    case U32FromF32, F32FromU32, TruncateF32, AbsoluteI32, SignExtendI8, SignExtendI16, CountBits32, CountBits64,
      ReverseBits32
    case Not32, Not64, FirstBitHighI32, FirstBitHighI64, FirstBitHighU32, FirstBitHighU64, ClearBit32, SetBit32
    case FirstZeroLow32, FirstZeroLow64, FirstOneLow32, FirstOneLow64, CountZeros32, CountZeros64, ReverseBits64
    case ClearBit64, SetBit64, QuadMask32, QuadMask64, WholeQuad32, WholeQuad64, BitReplicate
  }

  enum ScalarBinaryOperation derives CanEqual {
    case AddF32, SubtractF32, MultiplyF32, And32, Or32, MultiplyI32, MultiplyHighU32, MultiplyHighI32, AddI32,
      SubtractI32
    case ShiftLeft32, ShiftLeftTwoAndAddU32, ShiftRightArithmetic32, BitFieldExtractU32, BitFieldExtractI32,
      ShiftRight32
    case AndNot32, OrNot32, Nor32
    case PackLowLow16, PackLowHigh16, PackHighHigh16, AddU32, AddCarryU32, SubtractU32, SubtractBorrowU32, Xor32
    case Nand32, Xnor32, BitFieldMask32, ShiftLeftOneAndAddU32, ShiftLeftThreeAndAddU32, ShiftLeftFourAndAddU32
    case AbsoluteDifferenceI32, PackHighLow16
  }

  /** SALU binary32 and binary16 arithmetic; binary16 results clear the destination's high half. The propagating minimum
    * and maximum return NaN for a NaN operand and order -0 below +0; the others return the number.
    */
  enum ScalarFloatOperation derives CanEqual {
    case AddF16, SubtractF16, MultiplyF16, MinimumF16, MaximumF16, MinimumF32, MaximumF32
    case MinimumPropagateF16, MaximumPropagateF16, MinimumPropagateF32, MaximumPropagateF32
    case PackRoundTowardZeroF16
  }

  /** SALU conversions and roundings between binary32, binary16 and signed dwords. */
  enum ScalarFloatUnaryOperation derives CanEqual {
    case F32FromI32, I32FromF32, F16FromF32, F32FromF16, F32FromHighF16
    case CeilF32, FloorF32, RoundNearestEvenF32, CeilF16, FloorF16, TruncateF16, RoundNearestEvenF16
    case ExponentBase2F32, LogarithmBase2F32, ReciprocalSquareRootF32, SquareRootF32
    case ExponentBase2F16, LogarithmBase2F16, ReciprocalF16, ReciprocalSquareRootF16, SquareRootF16
  }

  enum ScalarMinMaxOperation derives CanEqual {
    case MinimumU32, MaximumU32, MinimumI32, MaximumI32
  }

  enum ScalarBinary64Operation derives CanEqual {
    case Multiply, AddNoCarry, SubtractNoCarry
    case And, AndNot, Or, OrNot, Nor, Xor, ShiftLeft, ShiftRight, ShiftRightArithmetic, BitFieldExtractI64
    case Nand, Xnor, BitFieldMask, BitFieldExtractU64
  }

  enum ScalarCompareOperation derives CanEqual {
    case EqualI32, NotEqualI32, GreaterI32, GreaterOrEqualI32, LessI32, LessOrEqualI32
    case EqualU32, NotEqualU32, GreaterU32, GreaterOrEqualU32, LessU32, LessOrEqualU32
    case EqualU64, NotEqualU64, BitSet32, BitClear32, BitSet64, BitClear64
  }

  enum VectorBitFindOperation derives CanEqual {
    case FirstBitLow32, FirstBitHighI32, FirstBitHighU32
  }

  enum VectorConvertOperation derives CanEqual {
    case FrexpExponentF64, FrexpExponentF32, FrexpMantissaF32, FrexpMantissaF64
    case ReciprocalF32, ReciprocalF64, TruncateF32, FloorF32, FractionF32, RoundNearestEvenF32
    case TruncateF64, FloorF64, FractionF64, RoundNearestEvenF64
    case ReciprocalSquareRootF32, ReciprocalSquareRootF64, SquareRootF32, ExponentBase2F32
    case SineF32, CosineF32, LogarithmBase2F32
    case I32FromF32, U32FromF32, I32FromF64, U32FromF64
    case F32FromUByte0, F32FromUByte1, F32FromUByte2, F32FromUByte3
    case F32FromF64, F32FromF16, F16FromF32, I16FromF16
    case F64FromI32, F64FromU32, F64FromF32, F32FromI32, F32FromU32
    case CeilF32, CeilF64, SquareRootF64, FloorI32FromF32, NearestI32FromF32
    case CeilF16, FloorF16, TruncateF16, RoundNearestEvenF16, FractionF16, SquareRootF16, ReciprocalF16
    case ReciprocalSquareRootF16, ExponentBase2F16, LogarithmBase2F16, SineF16, CosineF16
    case FrexpMantissaF16, FrexpExponentF16, F16FromI16, F16FromU16, U16FromF16
    case OffsetF32FromI4, SignedNormFromF16, UnsignedNormFromF16, SaturatePackU8FromI16
    case SaturatePackI4FromI8, SaturatePackU4FromU8
    case I32FromI16, U32FromU16, F32FromBF16

    /** GFX6 and GFX7's reciprocals, square-root reciprocals and logarithm bounding infinite results to the largest
      * finite value, and the DX9 reciprocals giving +0 for a zero and flushing infinite results to zero.
      */
    case ReciprocalClampF32, ReciprocalClampF64, ReciprocalSquareRootClampF32, ReciprocalSquareRootClampF64
    case LogarithmClampF32, ReciprocalLegacyF32, ReciprocalSquareRootLegacyF32

    /** GFX8's sine and cosine, which give +0 and 1 for finite operands beyond 256 turns. */
    case SineBoundedF32, CosineBoundedF32, SineBoundedF16, CosineBoundedF16

    /** One 8-bit float, or two from the low word as a binary32 pair, in CDNA3's FNUZ or the OCP formats. */
    case F32FromE4M3, F32FromE5M2, F32FromE4M3Fnuz, F32FromE5M2Fnuz
    case PairF32FromE4M3, PairF32FromE5M2, PairF32FromE4M3Fnuz, PairF32FromE5M2Fnuz

    /** One OCP 8-bit float widened exactly to binary16, or two from the low word packed into a dword. */
    case F16FromE4M3, F16FromE5M2, PackedF16FromE4M3, PackedF16FromE5M2

    /** gfx950's V_PRNG_B32, one step of its linear feedback shift register. */
    case PseudoRandom
  }

  /** The legacy forms multiply under DX9 rules, a zero times anything +0; the lighting multiply does too, unless its
    * third operand selects -max_float.
    */
  enum VectorMadF32Operation derives CanEqual {
    case MultiplyAdd, FusedMultiplyAdd
    case MultiplyAddLegacy, FusedMultiplyAddLegacy
    case MultiplyLighting
  }

  enum DualVectorOperation derives CanEqual {
    case Move, AddF32, SubtractF32, ReverseSubtractF32, MultiplyF32, FmaF32, MinimumF32, MaximumF32
    case ConditionalMask, AddU32, ShiftLeft32, And32
    case MultiplyLegacyF32, DotHalvesF32, DotAlignedHalvesF32, DotBFloat16F32, DotAlignedBFloat16F32
    case SubtractU32, MinimumI32, MaximumI32, ShiftRight32, ShiftRightArithmetic32
    case AddF64, MultiplyF64, FmaF64, MinimumF64, MaximumF64, BitOperation2

    /** Whether the half reads and writes register pairs. */
    def wide: Boolean = this match {
      case AddF64 | MultiplyF64 | FmaF64 | MinimumF64 | MaximumF64 => true
      case _                                                       => false
    }
  }

  /** One half of a dual-issued pair. VOPD3 adds `negate`, whose bits flip the sign of the float sources they name,
    * `condition`, the SGPR whose lane bits a select reads in place of VCC, and `table`, a bitop2 half's truth table.
    */
  case class DualVector(
      operation: DualVectorOperation,
      dest: VectorRegisterIndex,
      left: InstructionSource,
      right: InstructionSource,
      addend: InstructionSource,
      negate: Int = 0,
      condition: Option[InstructionSource] = None,
      table: Int = 0
  )

  enum VectorBinaryOperation derives CanEqual {
    case AddU32, MultiplyLowU32, MultiplyHighU32, MultiplyHighI32
    case ShiftRightArithmeticI32, ShiftRightArithmeticI16, ShiftLeft32, ShiftRight32, ShiftLeft16, ShiftRight16
    case AddU16, And32, Or32, Xor32, SubtractU16, SubtractReverseU16, SubtractU32, SubtractReverseU32
    case SubtractReverseF32, MultiplyU24, MultiplyHighU24, MultiplyI24, MultiplyHighI24, MultiplyLowU16
    case MinimumU32, MaximumU32, MinimumI32, MinimumI16, MinimumU16, MinimumF32, MinimumF16
    case MaximumI32, MaximumI16, MaximumU16, MaximumF32, MaximumF16
    case LoadExponentF32, AddF32, MultiplyF32, MultiplyAccumulateF32, AddF16, MultiplyF16
    case FusedMultiplyAccumulateF16, SubtractF32, SubtractF16, SubtractReverseF16, LoadExponentF16, Xnor32
    case PackRoundTowardZeroF16
    case AddCarryU32, AddWithCarryU32, SubtractCarryU32, SubtractWithBorrowU32
    case AddI32, SubtractI32, AddI16, SubtractI16, MultiplyLegacyF32
    case MinimumPropagateF32, MaximumPropagateF32, MinimumPropagateF16, MaximumPropagateF16
    case MinimumLegacyF32, MaximumLegacyF32
  }

  enum VectorSubDwordOperation derives CanEqual {
    case ConditionalMask32
    case AddU32, AddU16, AddF16, AddCarryU32, AddWithCarryU32
    case And32, Or32, Xor32
    case ShiftLeft16, ShiftLeft32, ShiftRight16, ShiftRight32
    case MaximumI16, MaximumF16, MinimumI16, MinimumU16, MinimumF16
    case MultiplyLowU16, MultiplyF16, MultiplyU24, MultiplyI24
    case SubtractU16, SubtractReverseU16, SubtractU32, SubtractCarryU32, SubtractWithBorrowU32
    case SubtractReverseU32, SubtractReverseCarryU32, SubtractReverseWithBorrowU32
  }

  enum VectorBinary64Operation derives CanEqual {
    case LoadExponent, Add, Multiply, Maximum, Minimum, MaximumPropagate, MinimumPropagate
    case AddU64, SubtractU64, MultiplyLowU64, MaximumI64, MaximumU64, MinimumI64, MinimumU64
  }

  enum VectorPackedBinaryOperation derives CanEqual {
    case AddU16, AddI16, AddF16, MultiplyLowU16, MultiplyF16, MaximumF16, MinimumF16
    case MaximumI16, MaximumU16, MinimumI16, MinimumU16, SubtractI16, SubtractU16
    case ShiftLeft16, ShiftRight16, ShiftRightArithmetic16, MaximumPropagateF16, MinimumPropagateF16
  }

  enum PackedTernaryOperation derives CanEqual {
    case FusedMultiplyAddF16, MultiplyAddU16, MultiplyAddI16, Maximum3PropagateF16, Minimum3PropagateF16
    case Maximum3F16, Minimum3F16, Maximum3I16, Maximum3U16, Minimum3I16, Minimum3U16
    case AddMaximumI16, AddMaximumU16, AddMinimumI16, AddMinimumU16
  }

  enum VectorTernaryOperation derives CanEqual {
    case Add3U32, Or3B32, Xor3B32
    case Maximum3I32, Maximum3U32, Minimum3I32, Minimum3F32, Minimum3U32
    case Median3I16, Median3I32, Median3U16, Median3U32
    case Maximum3I16, Maximum3U16, Minimum3I16, Minimum3U16
    case MultiplyAddU16, FusedMultiplyAddF16, DivisionFusedMultiplyAddF32
    case MultiplyAddLegacyU16, MultiplyAddU24, MultiplyAddI24
    case AlignBit32, BitFieldInsert32, AndOr32, BitFieldExtractI32, BitFieldExtractU32
    case XorAddU32, AddShiftLeftU32, ShiftLeftAddU32, ShiftLeftOr32, PermuteBytes32
    case AlignByte32, SumAbsoluteDifferenceU8, SumAbsoluteDifferenceHighU8, SumAbsoluteDifferenceU16
    case SumAbsoluteDifferenceU32, MaskedSumAbsoluteDifferenceU8
    case Maximum3F32, Median3F32, Maximum3F16, Minimum3F16, Median3F16
    case MaximumMinimumF32, MinimumMaximumF32, MaximumMinimumF16, MinimumMaximumF16
    case MaximumMinimumI32, MinimumMaximumI32, MaximumMinimumU32, MinimumMaximumU32
    case MultiplyAddU32U16, MultiplyAddI32I16, MultiplyAddI16, MultiplyAddF16, DivisionFixupF16
    case Minimum3PropagateF32, Maximum3PropagateF32, Minimum3PropagateF16, Maximum3PropagateF16
    case MaximumMinimumPropagateF32, MinimumMaximumPropagateF32, MaximumMinimumPropagateF16, MinimumMaximumPropagateF16
    case AddMaximumI32, AddMaximumU32, AddMinimumI32, AddMinimumU32, MultiplyAddU32

    /** gfx950's V_ASHR_PK_I8_I32 and V_ASHR_PK_U8_I32: SRC0 and SRC1 shifted by SRC2, saturated into two bytes. */
    case ShiftRightPackI8, ShiftRightPackU8
  }

  /** Media and packing operations on up to three dwords: byte interpolation, cube map coordinates, packing two dwords
    * or floats into saturated or normalised halves, packing a float into a byte and building a bit mask.
    */
  enum VectorMediaOperation derives CanEqual {
    case LerpU8, CubeIdF32, CubeScF32, CubeTcF32, CubeMaF32, PackByteF32
    case PackI16I32, PackU16U32, PackNormI16F32, PackNormU16F32, PackNormI16F16, PackNormU16F16, BitFieldMask32
    case PackF16F32, PackBF16F32

    /** Two binary32 values truncated and saturated to packed 16-bit integers. */
    case PackI16F32, PackU16F32
  }

  /** Read-modify-writes from the old value and two data operands, A and B: B minus the old value, the old value with
    * A's bits cleared and B's set, the old value wrapped down by A at or above it or up by B below it, B where the old
    * value equals A as a float, the old value less A clamped at zero or kept below A, and each dword of A without bit
    * 31 where that bit is set.
    */
  enum ExtendedAtomicOperation derives CanEqual {
    case ReverseSubtract, MaskOr, Wrap, FloatCompareSwap, SubtractClamp, ConditionalSubtract, ConditionalExchange
  }

  /** The row variable forms select each lane's source by the low nibble of its own VGPR rather than by its nibble of an
    * SGPR pair.
    */
  enum LanePermutation derives CanEqual {
    case Row, CrossRow, HalfWave
    case RowVariable, CrossRowVariable
  }

  /** What a GWS operation does to its resource: set its count, wait until the data plus one waves reach the barrier,
    * signal a semaphore once, by the data or once for every queued wave, or wait on a semaphore.
    */
  enum GlobalWaveSyncOperation derives CanEqual {
    case Init, Barrier, SemaphoreV, SemaphoreBulkRelease, SemaphoreReleaseAll, SemaphoreP
  }

  /** The packed elements a dot product multiplies: signed or unsigned bytes, shorts or nibbles, binary16 or bfloat16
    * halves, bytes or nibbles each source's NEG_LO bit makes signed into a signed sum, halves summed into a half of
    * their format, or the 8-bit floats of two formats.
    */
  enum DotOperation(val width: Int in (4, 8, 16), val signed: Boolean) derives CanEqual {
    case SignedBytes     extends DotOperation(8, signed = true)
    case UnsignedBytes   extends DotOperation(8, signed = false)
    case SignedShorts    extends DotOperation(16, signed = true)
    case UnsignedShorts  extends DotOperation(16, signed = false)
    case SignedNibbles   extends DotOperation(4, signed = true)
    case UnsignedNibbles extends DotOperation(4, signed = false)
    case Halves          extends DotOperation(16, signed = false)

    /** The V_DOT2 forms on RDNA3 and RDNA3.5, which sum through a fixed-point accumulator as [[Dots]] describes. */
    case AlignedHalves                   extends DotOperation(16, signed = false)
    case AlignedBFloat16Halves           extends DotOperation(16, signed = false)
    case AlignedHalvesToHalf             extends DotOperation(16, signed = false)
    case AlignedBFloat16HalvesToBFloat16 extends DotOperation(16, signed = false)
    case BFloat16Halves                  extends DotOperation(16, signed = false)

    case MixedBytes(signedA: Boolean, signedB: Boolean)         extends DotOperation(8, signed = true)
    case MixedNibbles(signedA: Boolean, signedB: Boolean)       extends DotOperation(4, signed = true)
    case HalvesToHalf                                           extends DotOperation(16, signed = false)
    case BFloat16HalvesToBFloat16                               extends DotOperation(16, signed = false)
    case EightBitFloats(formatA: Minifloat, formatB: Minifloat) extends DotOperation(8, signed = false)
  }

  /** How a transposing load routes elements: a lane reads `elements` elements of `bits` from its address, and takes its
    * element `e` from the lane of its group, of `groupMask + 1` lanes, offset by `offset(e)` plus its lane bits from
    * `laneShift`, under `laneMask`, times `laneScale`, at the element its lane bits under `elementMask` name. gfx950's
    * DS_READ_B64_TR_B16, _B8 and _B4 follow CK's and Triton's CDNA4 lane maps (CK `load_tile_transpose.hpp`, Triton
    * `TargetFeatures::queryLDSTransLoadParams`); DS_READ_B96_TR_B6 transposes its 16 6-bit elements as _B4 does its
    * nibbles; GFX12's wave32 GLOBAL_LOAD_TR_B128 and _B64 follow the lanes hipBLASLt's TensileLite reads WMMA operands
    * with.
    */
  enum TransposeLayout(
      val bits: Int in (4, 6, 8, 16),
      val elements: Int in (4, 8, 16),
      val groupMask: Int in (7, 15),
      val laneShift: Int in 0 ~ 3,
      val laneMask: Int in 0 ~ 3,
      val laneScale: Int in 0 ~ 4,
      val elementMask: Int in (3, 7, 15)
  ) derives CanEqual {
    case Rows16
        extends TransposeLayout(
          bits = 16,
          elements = 4,
          groupMask = 15,
          laneShift = 2,
          laneMask = 3,
          laneScale = 1,
          elementMask = 3
        )
    case Rows8
        extends TransposeLayout(
          bits = 8,
          elements = 8,
          groupMask = 15,
          laneShift = 3,
          laneMask = 1,
          laneScale = 1,
          elementMask = 7
        )
    case Rows4
        extends TransposeLayout(
          bits = 4,
          elements = 16,
          groupMask = 15,
          laneShift = 0,
          laneMask = 0,
          laneScale = 0,
          elementMask = 15
        )
    case Rows6
        extends TransposeLayout(
          bits = 6,
          elements = 16,
          groupMask = 15,
          laneShift = 0,
          laneMask = 0,
          laneScale = 0,
          elementMask = 15
        )
    case Blocks16
        extends TransposeLayout(
          bits = 16,
          elements = 8,
          groupMask = 7,
          laneShift = 0,
          laneMask = 0,
          laneScale = 0,
          elementMask = 7
        )
    case InterleavedBytes
        extends TransposeLayout(
          bits = 8,
          elements = 8,
          groupMask = 15,
          laneShift = 3,
          laneMask = 1,
          laneScale = 4,
          elementMask = 7
        )

    /** The bytes each lane reads. */
    def bytes: Int = bits * elements / 8

    /** Whether the lane map is known for wave32 alone, as GFX12's is. */
    def wave32: Boolean = this == Blocks16 || this == InterleavedBytes

    /** The lane, offset within its group, element `element` comes from beyond the routed lane bits. */
    def offset(element: Int): Int = this match {
      case Rows16           => 4 * element
      case Rows8            => 2 * element
      case InterleavedBytes => 8 * (element >> 2) + (element & 3)
      case _                => element
    }

    /** The lane and element that lane `lane`'s element `element` comes from. */
    def origin(lane: Int, element: Int): (lane: Int, element: Int) =
      ((lane & ~groupMask) + offset(element) + ((lane >> laneShift) & laneMask) * laneScale, lane & elementMask)
  }

  object ScaledConversion {

    /** The element layouts of a scaled conversion between wide floats and `format` floats: one element of the byte
      * OP_SEL[1:0] names into a binary32 dword or the half OP_SEL[3] names; two from the word OP_SEL[0] names, or the
      * byte OP_SEL[1:0] names for FP4, into a register pair of binary32 or a dword of halves; two, SRC0 and SRC1 or
      * SRC0's halves, into the word OP_SEL[3] or the FP4 byte OP_SEL[3:2] names; one, SRC0's or its half OP_SEL[0]
      * names, rounded by SRC1's seed into the byte OP_SEL[3:2] names; two FP4 elements so rounded, the second by the
      * seed V_PRNG_B32 advances; and 32 6-bit elements, six dwords, from or into 32 wide elements, SRC0's or, when
      * `interleaved`, SRC0's and SRC1's in turn, rounded by SRC1's advancing seed when `stochastic`.
      */
    enum Layout derives CanEqual {
      case Widen(format: Minifloat)
      case WidenPair(format: Minifloat)
      case NarrowPair(format: Minifloat)
      case NarrowStochastic(format: Minifloat)
      case NarrowStochasticPair
      case WidenBlock(format: Minifloat)
      case NarrowBlock(format: Minifloat, interleaved: Boolean, stochastic: Boolean)
    }
  }

  /** gfx950's scaled conversions between `side` floats and the narrow floats of `layout`. */
  case class ScaledConversion(side: Minifloats.Wide, layout: ScaledConversion.Layout) {

    /** The dwords the destination takes and SRC0, SRC1 and SRC2 give, none for a source the conversion leaves. */
    def dwords: (dest: Int, a: Int, b: Int, c: Int) = {
      val single = side == Minifloats.Wide.Single
      layout match {
        case ScaledConversion.Layout.Widen(_)                                => (1, 1, 1, 0)
        case ScaledConversion.Layout.WidenPair(_)                            => (if (single) 2 else 1, 1, 1, 0)
        case ScaledConversion.Layout.NarrowPair(_)                           => (1, 1, 1, if (single) 1 else 0)
        case ScaledConversion.Layout.NarrowStochastic(_)                     => (1, 1, 1, 1)
        case ScaledConversion.Layout.NarrowStochasticPair                    => (1, if (single) 2 else 1, 1, 1)
        case ScaledConversion.Layout.WidenBlock(_)                           => (if (single) 32 else 16, 6, 1, 0)
        case ScaledConversion.Layout.NarrowBlock(_, interleaved, stochastic) =>
          (
            6,
            if (single && !interleaved) 32 else 16,
            if (interleaved) 16 else 1,
            if (interleaved || stochastic) 1 else 0
          )
      }
    }
  }

  enum VectorComparisonKind derives CanEqual {
    case Signed16, Signed32, Signed64, Unsigned16, Unsigned32, Unsigned64
    case Float16, Float32, Float64, ClassFloat16, ClassFloat32, ClassFloat64
  }

  /** A floating-point comparison relation as a test on the operands, possibly swapped. */
  enum FloatTest derives CanEqual {
    case Less, LessOrEqual, Equal, NotEqual, OrderedNotEqual, NotLess, NotLessOrEqual, NotLessOrGreater, Ordered,
      Unordered, Never, Always
  }

  /** An integer comparison relation as a test on the operands, possibly swapped. */
  enum IntegerTest derives CanEqual {
    case Less, LessOrEqual, Equal, NotEqual, Always, Never
  }

  enum VectorComparisonRelation derives CanEqual {
    case Equal, NotEqual, OrderedNotEqual, Less, LessOrEqual, Greater, GreaterOrEqual
    case NotLess, NotLessOrEqual, NotGreater, NotGreaterOrEqual
    case Ordered, Unordered, NotLessOrGreater, Class, Never, Always

    def floatTest: FloatTest = this match {
      case Equal                              => FloatTest.Equal
      case NotEqual                           => FloatTest.NotEqual
      case OrderedNotEqual                    => FloatTest.OrderedNotEqual
      case Less | Greater                     => FloatTest.Less
      case LessOrEqual | GreaterOrEqual       => FloatTest.LessOrEqual
      case NotLess | NotGreater               => FloatTest.NotLess
      case NotLessOrEqual | NotGreaterOrEqual => FloatTest.NotLessOrEqual
      case Ordered                            => FloatTest.Ordered
      case Unordered                          => FloatTest.Unordered
      case NotLessOrGreater                   => FloatTest.NotLessOrGreater
      case Class | Never                      => FloatTest.Never
      case Always                             => FloatTest.Always
    }

    /** Whether the floating-point test reads the operands swapped. */
    def floatSwapped: Boolean = this match {
      case Greater | GreaterOrEqual | NotGreater | NotGreaterOrEqual => true
      case _                                                         => false
    }

    def integerTest: IntegerTest = this match {
      case Equal | NotLessOrGreater                            => IntegerTest.Equal
      case NotEqual | OrderedNotEqual                          => IntegerTest.NotEqual
      case Less | NotGreaterOrEqual | Greater | NotLessOrEqual => IntegerTest.Less
      case LessOrEqual | NotGreater | GreaterOrEqual | NotLess => IntegerTest.LessOrEqual
      case Ordered | Always                                    => IntegerTest.Always
      case Unordered | Class | Never                           => IntegerTest.Never
    }

    /** Whether the integer test reads the operands swapped. */
    def integerSwapped: Boolean = this match {
      case Greater | NotLessOrEqual | GreaterOrEqual | NotLess => true
      case _                                                   => false
    }
  }

  case class VectorComparison(
      kind: VectorComparisonKind,
      relation: VectorComparisonRelation,
      updateExec: Boolean
  ) {
    val floatTest: FloatTest     = relation.floatTest
    val floatSwapped: Boolean    = relation.floatSwapped
    val integerTest: IntegerTest = relation.integerTest
    val integerSwapped: Boolean  = relation.integerSwapped
  }

  enum Instruction derives CanEqual {

    case SLoad(
        dest: ScalarRegisterIndex,
        count: Int in (1, 2, 3, 4, 8, 16),
        base: ScalarRegisterIndex,
        offset: Long,
        scalarOffset: Option[ScalarRegisterIndex],
        elementBytes: Int in (1, 2, 4) = 4,
        signed: Boolean = false
    )
    case SMove(dest: ScalarRegisterIndex, src: InstructionSource)

    /** S_AND_NOT1_WREXEC (`notExec`, the source and not EXEC) and S_AND_NOT0_WREXEC (EXEC and not the source): the
      * result to EXEC and the destination, and whether it is non-zero to SCC.
      */
    case SWriteExec(notExec: Boolean, dest: ScalarRegisterIndex, src: InstructionSource, width: Int in (32, 64))

    /** S_MOVRELS, S_MOVRELD and S_MOVRELSD_2: `words` SGPRs from `src` to `dest`, the source offset by M0 when
      * `relativeSource` and the destination when `relativeDest`; with both, M0[9:0] offsets the source and M0[25:16]
      * the destination.
      */
    case SMoveRelative(
        dest: ScalarRegisterIndex,
        src: InstructionSource,
        words: Int in (1, 2),
        relativeSource: Boolean,
        relativeDest: Boolean
    )
    case SMultiplyImmediate(dest: ScalarRegisterIndex, value: Int)
    case SMove64(dest: ScalarRegisterIndex, src: InstructionSource)

    /** A write to a hardware register other than MODE, none of which a lane observes. */
    case SSetReg

    /** Write the low `size` bits of `src` into MODE from bit `offset`. */
    case SSetMode(offset: Int in 0 ~ 31, size: Int in 1 ~ 32, src: InstructionSource)

    /** Read `size` bits of MODE from bit `offset` into `dest`. */
    case SGetMode(dest: ScalarRegisterIndex, offset: Int in 0 ~ 31, size: Int in 1 ~ 32)
    case SGetPc(dest: ScalarRegisterIndex, offset: Int)

    /** Reads `clock` into `dest`, a register pair unless it is the 20-bit cycle count. */
    case SReadClock(dest: ScalarRegisterIndex, clock: Clock)

    /** S_GET_WAVEID_IN_WORKGROUP: the wave's index among its workgroup's waves, in the order they were created. */
    case SReadWaveId(dest: ScalarRegisterIndex)
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
    case SFmaF16(dest: ScalarRegisterIndex, srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource)
    case SFloat(
        operation: ScalarFloatOperation,
        dest: ScalarRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource
    )
    case SFloatUnary(operation: ScalarFloatUnaryOperation, dest: ScalarRegisterIndex, src: InstructionSource)
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

    /** Writes M0[7:0] from `index` and M0[15:12], the operands GFX9 VGPR indexing offsets, from `mode`, where given.
      * `enable` starts indexing mode, which [[SGprIndexOff]] ends; the compiler lowers the moves between to relative
      * ones.
      */
    case SGprIndex(index: Option[InstructionSource], mode: Option[Int], enable: Boolean)
    case SGprIndexOff
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
    case VMedia(
        operation: VectorMediaOperation,
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        modifiers: SourceModifiers
    )

    /** Four masked or plain sums of absolute byte differences between the dwords at byte offsets 0 to 3 of a register
      * pair and a reference dword, each added to a 16-bit half of a pair (or a dword of a quad, when `wide`).
      */
    case VQuadSad(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        masked: Boolean,
        wide: Boolean
    )

    /** V_PERMLANE16 and V_PERMLANEX16: each lane reads `src` from the lane its row's selector nibble in `low` or `high`
      * names, in its own row or, `crossed`, the other row of its half-wave; V_PERMLANE64 reads the other half-wave.
      */
    case VPermuteLanes(
        dest: VectorRegisterIndex,
        src: InstructionSource,
        low: InstructionSource,
        high: InstructionSource,
        mode: LanePermutation,
        fetchInactive: Boolean,
        boundControl: Boolean
    )

    /** gfx950's V_PERMLANE16_SWAP and V_PERMLANE32_SWAP: the lanes below each `rows`-lane boundary take `src` from the
      * `dest` of the lane `rows` above, which takes theirs; a lane it cannot read gives zero under bound control and
      * leaves the register otherwise.
      */
    case VPermlaneSwap(
        dest: VectorRegisterIndex,
        src: VectorRegisterIndex,
        rows: Int,
        fetchInactive: Boolean,
        boundControl: Boolean
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
    case VMatrix(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        shape: MatrixShape,
        controls: MatrixControls
    )
    case VPackedMove32(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        highA: Boolean,
        highB: Boolean
    )

    /** Read a VGPR through the scalar M0 register (v_movrels_b32), `field` of M0 offsetting `src`. */
    case VMoveRelative(dest: VectorRegisterIndex, src: InstructionSource, field: RelativeField)

    /** Write a VGPR `field` of M0 offsets (v_movreld_b32), reading one through `sourceField` of M0 too when it is set
      * (v_movrelsd_b32 and V_MOVRELSD_2_B32).
      */
    case VMoveRelativeDestination(
        dest: VectorRegisterIndex,
        src: InstructionSource,
        sourceField: Option[RelativeField],
        field: RelativeField
    )

    /** V_SWAPREL_B32: the VGPR `src` plus M0[9:0] and the VGPR `dest` plus M0[25:16] exchange their dwords, unless
      * either is beyond the last.
      */
    case VSwapRelative(dest: VectorRegisterIndex, src: VectorRegisterIndex)
    case VNot(dest: VectorRegisterIndex, src: InstructionSource)
    case VSwap(dest: VectorRegisterIndex, src: InstructionSource)

    /** V_SWAP_B16: the 16-bit halves `destHigh` and `srcHigh` name trade places. */
    case VSwapHalves(dest: VectorRegisterIndex, destHigh: Boolean, src: VectorRegisterIndex, srcHigh: Boolean)
    case VBitReverse(dest: VectorRegisterIndex, src: InstructionSource)
    case VBitFind(operation: VectorBitFindOperation, dest: VectorRegisterIndex, src: InstructionSource)
    case VConvert(
        operation: VectorConvertOperation,
        dest: VectorRegisterIndex,
        src: InstructionSource,
        modifiers: SourceModifiers = SourceModifiers.Unmodified,
        clamp: Boolean = false
    )

    /** V_CVT_PK_*8_F32, SRC0 and SRC1 into word `position` of `dest`, and V_CVT_SR_*8_F32, `stochastic`, SRC0 rounded
      * by SRC1's seed into byte `position`, converted to the 8-bit `format`; the other bytes keep their values.
      */
    case VConvertNarrow(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        format: Minifloat,
        stochastic: Boolean,
        position: Int in 0 ~ 3,
        modifiers: SourceModifiers
    )

    /** V_CVT_PK_*8_F16: the binary16 halves of SRC0, low half first, rounded to nearest even into the low word of
      * `dest` as bytes in the 8-bit `format`.
      */
    case VConvertPackedHalf(dest: VectorRegisterIndex, src: InstructionSource, format: Minifloat)

    /** A gfx950 V_CVT_SCALEF32 conversion (CDNA4 ISA, 12.11): `conversion` names which sources hold the elements, the
      * stochastic rounding seed and the scale, and the OP_SEL bits of `selection` the bytes or words they take.
      */
    case VScaledConvert(
        conversion: ScaledConversion,
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        selection: Int,
        modifiers: SourceModifiers
    )

    /** V_CVT_SR_F16_F32 and V_CVT_SR_BF16_F32: SRC0 rounded stochastically by SRC1's seed into the half `high` names.
      */
    case VConvertStochasticHalf(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        side: Minifloats.Wide,
        high: Boolean,
        modifiers: SourceModifiers
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

    /** Executes `operation`, then places its result in `dest` as an SDWA destination selection does, the other bits of
      * the previous dword padded, preserved or sign-extended as `unused` says. `relative` is the field of M0 that
      * offsets `dest`, for the relative moves that write through it.
      */
    case VSubDwordResult(
        operation: Instruction,
        dest: VectorRegisterIndex,
        selection: SubDwordSelection,
        unused: SubDwordDestinationUnused,
        relative: Option[RelativeField] = None
    )

    /** Executes `operation`, whose source 0 is [[InstructionSource.DPP]], on the lanes `control` enables; `wide` routes
      * the register pair from `src`, as CDNA2 onwards routes 64-bit operands under row_newbcast. `src` is in `bank`, as
      * V_ACCVGPR_MOV's is in the accumulation registers, and `relative` is the field of M0 that offsets it, as the
      * relative moves read it.
      */
    case VDPP(
        operation: Instruction,
        src: VectorRegisterIndex,
        control: DPPControl,
        fetchInactive: Boolean,
        wide: Boolean,
        bank: RegisterBank = RegisterBank.Vector,
        relative: Option[RelativeField] = None
    )
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

    /** The products of `operation`'s elements of SRC0 and SRC1 added to SRC2, saturated when `clamp`; the 8-bit float
      * products alone take `modifiers`, which only SRC2's bits set.
      */
    case VDot(
        operation: DotOperation,
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        clamp: Boolean,
        modifiers: SourceModifiers = SourceModifiers.Unmodified
    )

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

    /** Packed 16-bit halves, each source's half for each result half picked by OP_SEL and OP_SEL_HI; `clamp` saturates
      * integer results and clamps float results to [0, 1]. `negateLow` and `negateHigh` negate the halves of the
      * sources their bits name.
      */
    case VPackedBinary(
        operation: VectorPackedBinaryOperation,
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcAHighForHigh: Boolean,
        srcBHighForHigh: Boolean,
        srcAHighForLow: Boolean = false,
        srcBHighForLow: Boolean = false,
        clamp: Boolean = false,
        negateLow: Int = 0,
        negateHigh: Int = 0
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
    case VPackedTernary(
        dest: VectorRegisterIndex,
        srcA: InstructionSource,
        srcB: InstructionSource,
        srcC: InstructionSource,
        lowSelection: Int,
        highSelection: Int,
        negateLow: Int,
        negateHigh: Int,
        clamp: Boolean,
        operation: PackedTernaryOperation = PackedTernaryOperation.FusedMultiplyAddF16
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
        returnValue: Boolean,
        bank: RegisterBank = RegisterBank.Vector
    )

    /** A DS source-2 update of the LDS `bytes` at ADDR from those `displacement` dwords on or, without one, the signed
      * ADDR[31:17] dwords on; `reverse` subtracts the old value from the other.
      */
    case LocalSource2(
        operation: AtomicOperation,
        address: VectorRegisterIndex,
        displacement: Option[Int],
        bytes: Int in (4, 8),
        reverse: Boolean
    )

    /** Swaps in `data` when memory equals `comparand`. */
    case CompareSwap(
        dest: VectorRegisterIndex,
        address: MemoryAddress,
        data: VectorRegisterIndex,
        comparand: VectorRegisterIndex,
        bytes: Int in (4, 8),
        returnValue: Boolean,
        bank: RegisterBank = RegisterBank.Vector
    )

    /** An [[ExtendedAtomicOperation]] at `access`'s address on its comparand, A, and data, B. */
    case AtomicExtended(operation: ExtendedAtomicOperation, access: CompareSwap)

    /** DS_APPEND and DS_CONSUME: the LDS dword at `offset` counts the active lanes up or down; each active lane
      * receives the count before. Hardware addresses the counter by the offset alone, whatever M0 holds.
      */
    case LocalCounter(
        dest: VectorRegisterIndex,
        offset: Int,
        decrement: Boolean,
        bank: RegisterBank = RegisterBank.Vector
    )

    /** DS_WRXCHG2: two exchanges, `first` then `second`. */
    case ExchangePair(first: Atomic, second: Atomic)

    /** A GWS operation on the resource M0[21:16] plus `offset` names, with `data` from the first active lane. Only a
      * cooperative dispatch has GWS resources.
      */
    case GlobalWaveSync(
        name: String,
        operation: GlobalWaveSyncOperation,
        data: VectorRegisterIndex,
        offset: Int,
        bank: RegisterBank = RegisterBank.Vector
    )

    /** A transposing load: each lane reads `layout`'s bytes from its address, and each active lane takes the elements
      * `layout` routes to it from the lanes of its group.
      */
    case LoadTranspose(dest: VectorRegisterIndex, address: MemoryAddress, layout: TransposeLayout)

    /** LDS read2: two elements from `address + first` and `address + second` into consecutive registers. */
    case LoadPair(
        dest: VectorRegisterIndex,
        address: VectorRegisterIndex,
        bytes: Int in (4, 8),
        first: Int,
        second: Int,
        bank: RegisterBank = RegisterBank.Vector
    )
    case StorePair(
        address: VectorRegisterIndex,
        srcA: VectorRegisterIndex,
        srcB: VectorRegisterIndex,
        bytes: Int in (4, 8),
        first: Int,
        second: Int,
        bank: RegisterBank = RegisterBank.Vector
    )

    /** A typed buffer load of `components` channels converted from `format`, or the descriptor's format. */
    case FormatLoad(
        dest: VectorRegisterIndex,
        address: MemoryAddress,
        format: Option[TexelFormat],
        components: Int in 1 ~ 4,
        packing: Packing,
        bank: RegisterBank = RegisterBank.Vector
    )
    case FormatStore(
        src: VectorRegisterIndex,
        address: MemoryAddress,
        format: Option[TexelFormat],
        components: Int in 1 ~ 4,
        packing: Packing,
        bank: RegisterBank = RegisterBank.Vector
    )

    /** A buffer load into LDS at M0 plus `offset` plus the lane times the larger of four and `bytes`. */
    case LoadLocal(address: MemoryAddress, bytes: Int in 1 ~ 16, signed: Boolean, offset: Int)

    /** BUFFER_STORE_LDS_DWORD: the LDS dword at M0 plus `offset` plus four bytes a lane, stored through `address`. */
    case StoreLocal(address: MemoryAddress, offset: Int)

    /** A scalar load through a buffer descriptor; dwords past the buffer read zero. */
    case SBufferLoad(
        dest: ScalarRegisterIndex,
        count: Int in (1, 2, 3, 4, 8, 16),
        resource: ScalarRegisterIndex,
        offset: Long,
        scalarOffset: Option[ScalarRegisterIndex],
        elementBytes: Int in (1, 2, 4),
        signed: Boolean
    )

    /** An image access of the components `mask` enables, through the descriptor at `address.resource`. */
    case Image(
        operation: ImageOperation,
        data: VectorRegisterIndex,
        address: ImageAddress,
        mask: Int in 0 ~ 15,
        packing: Packing,
        bank: RegisterBank = RegisterBank.Vector
    )

    /** `access`, then the TFE status dword, zero as no access faults, in `status`. */
    case WithStatus(access: Instruction, status: VectorRegisterIndex)

    /** DS_PERMUTE and DS_BPERMUTE; with `halves`, as GFX10 onwards has them, an address names a lane of the 32-lane
      * half its lane is in.
      */
    case DSPermute(
        dest: VectorRegisterIndex,
        address: VectorRegisterIndex,
        src: VectorRegisterIndex,
        offset: Int,
        bank: RegisterBank = RegisterBank.Vector,
        halves: Boolean = false
    )
    case DSBPermute(
        dest: VectorRegisterIndex,
        address: VectorRegisterIndex,
        src: VectorRegisterIndex,
        offset: Int,
        bank: RegisterBank = RegisterBank.Vector,
        fetchInactive: Boolean = false,
        halves: Boolean = false
    )
    case DSSwizzle(
        dest: VectorRegisterIndex,
        src: VectorRegisterIndex,
        control: Int,
        bank: RegisterBank = RegisterBank.Vector
    )
    case BranchRelative(condition: ScalarBranchCondition, displacement: Int)

    /** S_SUBVECTOR_LOOP_BEGIN, or S_SUBVECTOR_LOOP_END unless `begin`, by `displacement` dwords, which
      * [[SubvectorLoop]] resolves.
      */
    case SubvectorLoopRelative(begin: Boolean, state: ScalarRegisterIndex, displacement: Int)

    /** A wave64's loop over the halves of its lanes, whose begin takes the instruction after its end as `target` and
      * whose end takes the first of its body. The begin skips the loop where EXEC is empty and otherwise, with lanes in
      * the low half, narrows EXEC to them and leaves the high half in `state`. The end then runs the high half, leaves
      * the low half in `state` and, after that pass, restores the low half of EXEC from it.
      */
    case SubvectorLoop(begin: Boolean, state: ScalarRegisterIndex, target: Int)

    /** S_CALL_B64: the address after it into `dest`, then a branch by `displacement` dwords, which [[Call]] resolves.
      */
    case CallRelative(dest: ScalarRegisterIndex, displacement: Int)
    case Call(dest: ScalarRegisterIndex, target: Int)

    /** Stops the wave, as S_SETHALT and a host interrupt do; nothing resumes it, so execution faults with `reason`. */
    case Halt(reason: String)
    case Branch(condition: ScalarBranchCondition, target: Int)
    case Barrier

    /** S_BARRIER_SIGNAL: `id` names the barrier or, `fromM0`, M0 holds it in bits [5:0] and a named barrier's new
      * expected count, where not zero, in bits [22:16].
      */
    case BarrierSignal(id: InstructionSource, isFirst: Boolean, fromM0: Boolean = false)
    case BarrierWait(id: Int)

    /** S_BARRIER_INIT: the named barrier `id` names, from M0's low bits where `fromM0`, expects as many arrivals as the
      * upper half of `m0`.
      */
    case BarrierInit(id: InstructionSource, fromM0: Boolean, m0: InstructionSource)

    /** S_BARRIER_JOIN: the named barrier `id` names becomes the one the wave's S_BARRIER_WAIT waits on. */
    case BarrierJoin(id: InstructionSource, fromM0: Boolean)

    /** S_BARRIER_LEAVE: drops the joined named barrier, setting SCC where its expected count reaches zero. */
    case BarrierLeave
    case Wait

    /** S_SLEEP: a wait during which the other workgroups of a cooperative dispatch run. */
    case Sleep
    case End
    case Trap(code: Int)

    /** S_STORE and S_BUFFER_STORE: `count` SGPRs from `src` to `address`. */
    case SStore(src: ScalarRegisterIndex, count: Int in (1, 2, 4), address: ScalarAddress)

    /** S_ATOMIC and S_BUFFER_ATOMIC: `operation` on the `bytes` at `address` with the SGPRs from `data`, which take the
      * old value when `returnValue`.
      */
    case SAtomic(
        operation: AtomicOperation,
        data: ScalarRegisterIndex,
        address: ScalarAddress,
        bytes: Int in (4, 8),
        returnValue: Boolean
    )

    /** S_ATOMIC_CMPSWAP and S_BUFFER_ATOMIC_CMPSWAP: the data in the SGPRs from `data` swapped in where memory equals
      * the comparand in those after it; the old value into the data SGPRs when `returnValue`.
      */
    case SCompareSwap(data: ScalarRegisterIndex, address: ScalarAddress, bytes: Int in (4, 8), returnValue: Boolean)

    /** S_CBRANCH_G_FORK: the lanes of EXEC in `mask` and those not, each run in turn, the fewer first, the others with
      * their address saved on the SGPR stack CSP indexes; all of EXEC branches to the address `target` holds or falls
      * through.
      */
    case ForkIndirect(mask: InstructionSource, target: InstructionSource)

    /** S_CBRANCH_I_FORK by `displacement` dwords, which [[Fork]] resolves. */
    case ForkRelative(mask: InstructionSource, displacement: Int)
    case Fork(mask: InstructionSource, target: Int)

    /** S_CBRANCH_JOIN: while CSP is above `saved`, pops the address and EXEC of the other side of a fork. */
    case Join(saved: InstructionSource)

    /** S_SETVSKIP: MODE's VSKIP from bit `bit` of `src`. */
    case SetVectorSkip(src: InstructionSource, bit: InstructionSource)
  }

  class Kernel(
      val source: CodeObjectKernel,
      val instructions: Vec[Instruction],
      val instructionIndices: Map[Int, Int],
      val instructionOffsets: Vec[Int],
      val entryInstruction: Int,
      // which instructions VSKIP skips, empty when the kernel never sets it
      val skipped: Vec[Boolean] = Vec.empty
  ) {
    val isa: ISA       = source.target.isa
    val m0: Int        = m0Register(isa)
    val discarded: Int = nullRegister(isa).fold(-1)(_.raw)
  }
}
