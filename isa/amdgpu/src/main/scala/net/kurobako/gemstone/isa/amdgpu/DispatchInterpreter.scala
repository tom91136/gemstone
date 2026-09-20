package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*
import ExecutableInstruction.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.binary.Bits
import net.kurobako.gemstone.binary.BitWidth
import net.kurobako.gemstone.binary.Binary16
import net.kurobako.gemstone.binary.Binary32
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.MemoryValueByteWidth

import cats.syntax.either.*
import cats.syntax.traverse.*

import java.math.BigInteger
import java.util.concurrent.locks.ReentrantLock
import scala.collection.immutable.IntMap

object DispatchInterpreter {
  private val KernargAddress                                     = 0x100000000000L
  private val DispatchAddress                                    = 0x200000000000L
  private val AllocationBase                                     = 0x400000000000L
  private val AllocationSpan                                     = 0x000100000000L
  private val AdvanceMarker                                      = Int.MinValue
  private val AdvanceStep: Either[ExecutionFailure, Option[Int]] = Right(Some(AdvanceMarker))

  /** Convert a lane-wise operation's success to the shared advance marker. Keeping this path allocation-free matters
    * for memory and DS instructions, which otherwise create a fresh `Some(index + 1)` for every instruction.
    */
  private def advanceAfter(
      result: Either[ExecutionFailure, Unit]
  ): Either[ExecutionFailure, Option[Int]] = result match {
    case Left(error) => Left(error)
    case Right(_)    => AdvanceStep
  }
  // Decoder membership tables are immutable and shared. Keeping them out of
  // instruction-level methods avoids rebuilding a HashSet for every decoded
  // instruction in a code object.
  private val SupportedSOP2Opcodes = Set(
    "s_and_b32",
    "s_or_b32",
    "s_nor_b32",
    "s_mul_i32",
    "s_mul_hi_u32",
    "s_mul_hi_i32",
    "s_add_i32",
    "s_sub_i32",
    "s_add_u32",
    "s_addc_u32",
    "s_sub_u32",
    "s_subb_u32",
    "s_ashr_i32",
    "s_xor_b32",
    "s_lshl_b32",
    "s_lshr_b32",
    "s_min_u32",
    "s_max_u32",
    "s_min_i32",
    "s_max_i32",
    "s_andn2_b32",
    "s_orn2_b32",
    "s_pack_ll_b32_b16",
    "s_pack_lh_b32_b16",
    "s_pack_hh_b32_b16",
    "s_bfe_u32",
    "s_bfe_i32",
    "s_bfe_i64",
    "s_and_b64",
    "s_andn2_b64",
    "s_or_b64",
    "s_nor_b64",
    "s_orn2_b64",
    "s_xor_b64",
    "s_lshl_b64",
    "s_lshr_b64",
    "s_ashr_i64",
    "s_cselect_b32",
    "s_cselect_b64"
  )
  private val SupportedSOPCOpcodes = Set(
    "s_cmp_eq_u32",
    "s_cmp_eq_u64",
    "s_cmp_gt_i32",
    "s_cmp_gt_u32",
    "s_cmp_ge_u32",
    "s_cmp_le_u32",
    "s_cmp_eq_i32",
    "s_cmp_lg_i32",
    "s_cmp_ge_i32",
    "s_cmp_le_i32",
    "s_cmp_lt_u32",
    "s_cmp_lg_u32",
    "s_cmp_lg_u64",
    "s_cmp_lt_i32",
    "s_bitcmp0_b32",
    "s_bitcmp1_b32"
  )
  private val SupportedVCompareOpcodes = Set(
    "v_cmp_eq_u32",
    "v_cmp_eq_f32",
    "v_cmp_eq_f16",
    "v_cmp_neq_f16",
    "v_cmp_lg_f16",
    "v_cmp_lt_f16",
    "v_cmp_le_f16",
    "v_cmp_gt_f16",
    "v_cmp_ge_f16",
    "v_cmp_nlt_f16",
    "v_cmp_nle_f16",
    "v_cmp_ngt_f16",
    "v_cmp_nge_f16",
    "v_cmp_o_f16",
    "v_cmp_u_f16",
    "v_cmp_eq_u64",
    "v_cmp_class_f32",
    "v_cmp_class_f64",
    "v_cmp_eq_i16",
    "v_cmp_eq_u16",
    "v_cmp_lg_u16",
    "v_cmp_lg_i16",
    "v_cmp_ne_i16",
    "v_cmp_ne_u16",
    "v_cmp_lg_u32",
    "v_cmp_lg_u64",
    "v_cmp_lg_f32",
    "v_cmp_eq_f64",
    "v_cmp_lt_f32",
    "v_cmp_gt_i16",
    "v_cmp_gt_u16",
    "v_cmp_le_u16",
    "v_cmp_ge_i16",
    "v_cmp_ge_u16",
    "v_cmp_ge_f32",
    "v_cmp_le_f32",
    "v_cmp_lt_i16",
    "v_cmp_le_i16",
    "v_cmp_lt_u16",
    "v_cmp_le_i64",
    "v_cmp_ge_i64",
    "v_cmp_ngt_f32",
    "v_cmp_nlt_f32",
    "v_cmp_nle_f32",
    "v_cmp_nge_f32",
    "v_cmp_o_f32",
    "v_cmp_u_f32",
    "v_cmp_neq_f64",
    "v_cmp_o_f64",
    "v_cmp_ngt_f64",
    "v_cmp_nge_f64",
    "v_cmp_nle_f64",
    "v_cmp_neq_f32",
    "v_cmp_gt_f32",
    "v_cmp_gt_f64",
    "v_cmp_ge_f64",
    "v_cmp_le_f64",
    "v_cmp_lg_f64",
    "v_cmp_u_f64",
    "v_cmp_nlg_f64",
    "v_cmp_ne_u32",
    "v_cmp_ne_u64",
    "v_cmp_ge_u32",
    "v_cmp_ge_u64",
    "v_cmp_gt_i32",
    "v_cmp_ge_i32",
    "v_cmp_gt_i64",
    "v_cmp_gt_u32",
    "v_cmp_gt_u64",
    "v_cmp_le_i32",
    "v_cmp_le_u32",
    "v_cmp_le_u64",
    "v_cmp_lt_i32",
    "v_cmp_lt_i64",
    "v_cmp_lt_f64",
    "v_cmp_nlt_f64",
    "v_cmp_lt_u32",
    "v_cmp_lt_u64"
  )
  private val SupportedVCompareSDWAOpcodes = Set(
    "v_cmp_eq_u32",
    "v_cmp_eq_f32",
    "v_cmp_eq_f16",
    "v_cmp_neq_f16",
    "v_cmp_lg_f16",
    "v_cmp_lt_f16",
    "v_cmp_le_f16",
    "v_cmp_gt_f16",
    "v_cmp_ge_f16",
    "v_cmp_nlt_f16",
    "v_cmp_nle_f16",
    "v_cmp_ngt_f16",
    "v_cmp_nge_f16",
    "v_cmp_o_f16",
    "v_cmp_u_f16",
    "v_cmp_ne_u32",
    "v_cmp_lg_u32",
    "v_cmp_gt_f32",
    "v_cmp_ge_f32",
    "v_cmp_nge_f32",
    "v_cmp_le_f32",
    "v_cmp_ngt_f32",
    "v_cmp_nlt_f32",
    "v_cmp_gt_i32",
    "v_cmp_gt_u32",
    "v_cmp_ge_i32",
    "v_cmp_ge_i16",
    "v_cmp_ge_u32",
    "v_cmp_lt_u16",
    "v_cmp_le_i32",
    "v_cmp_lt_i32",
    "v_cmp_lt_u32",
    "v_cmp_le_u32",
    "v_cmp_eq_i16",
    "v_cmp_eq_u16",
    "v_cmp_lg_i16",
    "v_cmp_lg_u16",
    "v_cmp_ne_i16",
    "v_cmp_ne_u16",
    "v_cmp_gt_i16",
    "v_cmp_gt_u16",
    "v_cmp_ge_u16",
    "v_cmp_lt_i16",
    "v_cmp_lt_u16",
    "v_cmp_le_i16",
    "v_cmp_le_u16"
  )
  private val SupportedVOP3SourceModifierOpcodes = Set(
    "v_cndmask_b32",
    "v_cndmask_b32_e64",
    "v_ldexp_f32",
    "v_ldexp_f64",
    "v_rcp_f32",
    "v_frexp_mant_f64",
    "v_max_f64",
    "v_max_f64_e64",
    "v_min_f64",
    "v_min_f64_e64",
    "v_pk_add_u16",
    "v_pk_mul_lo_u16",
    "v_pk_sub_i16",
    "v_pk_lshrrev_b16",
    "v_add_f32",
    "v_sub_f32",
    "v_subrev_f32",
    "v_mul_f32",
    "v_add_f64",
    "v_add_f64_e64",
    "v_mul_f64",
    "v_mul_f64_e64",
    "v_mad_f32",
    "v_fma_f32",
    "v_fmac_f32",
    "v_fma_f16",
    "v_pk_fma_f16",
    "v_fma_mix_f32",
    "v_fma_mixlo_f16",
    "v_fma_mixhi_f16",
    "v_pk_add_f16",
    "v_pk_mul_f16",
    "v_div_scale_f32",
    "v_div_fixup_f32",
    "v_pk_max_f16",
    "v_pk_min_f16",
    "v_pack_b32_f16",
    "v_fma_f64",
    "v_fma_f64_e64",
    "v_div_scale_f64",
    "v_div_fmas_f64",
    "v_div_fmas_f64_e64",
    "v_div_fixup_f64",
    "v_div_fixup_f64_e64",
    "v_max_f32",
    "v_min_f32",
    "v_trig_preop_f64"
  )
  private val EmulatedCallNameFragments = Set(
    "logIf",
    "logId",
    "casinhf",
    "casinhE",
    "cacosf",
    "cacosE",
    "catanf",
    "catanE",
    "cacoshf",
    "cacoshE",
    "catanhf",
    "catanhE",
    "csinhf",
    "ccoshf",
    "ctanhf",
    "csinhE",
    "ccoshE",
    "ctanhE",
    "expIf",
    "expId"
  )
  private val SDWAVOP2Opcodes = Set(
    "v_add_u32",
    "v_add_nc_u32",
    "v_add_co_u32",
    "v_addc_co_u32",
    "v_sub_nc_u32",
    "v_add_u16",
    "v_and_b32",
    "v_lshlrev_b32",
    "v_lshlrev_b16",
    "v_lshrrev_b32",
    "v_lshrrev_b16",
    "v_max_i16",
    "v_max_f16",
    "v_min_i16",
    "v_min_f16",
    "v_min_u16",
    "v_mul_lo_u16",
    "v_add_f16",
    "v_mul_f16",
    "v_mul_u32_u24",
    "v_or_b32",
    "v_xor_b32",
    "v_cndmask_b32",
    "v_sub_u16",
    "v_sub_co_u32",
    "v_subb_co_u32",
    "v_subrev_co_u32",
    "v_subbrev_co_u32",
    "v_mul_i32_i24"
  )
  // The 1200-bit fixed-point representation used by the GFX9
  // v_trig_preop_f64 instruction for extracting a 53-bit chunk of 2/pi.
  private val TwoOverPi1200 = BigInteger(
    "a2f9836e4e441529fc2757d1f534ddc0db6295993c439041fe5163abdebbc561b7246e3a424dd2e006492eea09d1921cfe1deb1cb129a73ee88235f52ebb4484e99c7026b45f7e413991d639835339f49c845f8bbdf9283b1ff897ffde05980fef2f118b5a0a6d1f6d367ecf27cb09b74f463f669e5fea2d7527bac7ebe5f17b3d0739f78a5292ea6bfb5fb11f8d5d0856033046fc7b",
    16
  )
  private val TrigPreopMask = BigInteger.ONE.shiftLeft(53).subtract(BigInteger.ONE)

  private[amdgpu] def singleDSOffset(word: Long): Int                       = Bits.low16(word).toInt
  private[amdgpu] def unsignedMultiplyHigh32(left: Long, right: Long): Long =
    Bits.high32(Bits.low32(left) * Bits.low32(right))
  private[amdgpu] def signedMultiplyHigh32(left: Long, right: Long): Long =
    Bits.high32(Bits.low32(left).toInt.toLong * Bits.low32(right).toInt.toLong)
  private[amdgpu] def unsignedMultiplyHigh24(left: Long, right: Long): Long =
    Bits.high32((left & 0xffffffL) * (right & 0xffffffL))
  private[amdgpu] def signedMultiplyImmediate(register: Long, immediate: Int): (Long, Boolean) = {
    val product  = register.toInt.toLong * immediate.toLong
    val overflow = product < Int.MinValue.toLong || product > Int.MaxValue.toLong
    (Bits.low32(product), overflow)
  }
  private[amdgpu] def unsignedSubtract32(left: Long, right: Long): (Long, Boolean) = {
    val unsignedLeft  = Bits.low32(left)
    val unsignedRight = Bits.low32(right)
    // AMD names the VCC result "carry", but V_SUB*_CO exposes the borrow
    // bit: it is set when the unsigned minuend is smaller than the
    // subtrahend.  V_SUBB then consumes that bit as its borrow-in.
    (Bits.low32(unsignedLeft - unsignedRight), unsignedLeft < unsignedRight)
  }
  private[amdgpu] def unsignedSubtractWithBorrow32(left: Long, right: Long, borrowIn: Boolean): (Long, Boolean) = {
    val unsignedLeft  = Bits.low32(left)
    val unsignedRight = Bits.low32(right) + (if (borrowIn) 1L else 0L)
    (Bits.low32(unsignedLeft - unsignedRight), unsignedLeft < unsignedRight)
  }
  private[amdgpu] def unsignedMultiply24(left: Long, right: Long): Long =
    Bits.low32((left & 0xffffffL) * (right & 0xffffffL))
  private[amdgpu] def unsignedMultiplyAdd24(left: Long, right: Long, addend: Long): Long =
    Bits.low32(unsignedMultiply24(left, right) + Bits.low32(addend))
  private[amdgpu] def unsignedSubtractReverse32(left: Long, right: Long): Long =
    Bits.low32(Bits.low32(right) - Bits.low32(left))
  private[amdgpu] def unsignedMin32(left: Long, right: Long): Long =
    math.min(Bits.low32(left), Bits.low32(right))
  private[amdgpu] def scalarMinMax(opcode: String, left: Long, right: Long): (Long, Boolean) = {
    val left32  = Bits.low32(left)
    val right32 = Bits.low32(right)
    opcode match {
      case "s_min_u32" => (math.min(left32, right32), left32 < right32)
      case "s_max_u32" => (math.max(left32, right32), left32 > right32)
      case "s_min_i32" => (Bits.low32(math.min(left32.toInt, right32.toInt).toLong), left32.toInt < right32.toInt)
      case "s_max_i32" => (Bits.low32(math.max(left32.toInt, right32.toInt).toLong), left32.toInt > right32.toInt)
    }
  }
  private[amdgpu] def mergeD16(existing: Long, value: Long, high: Boolean): Long =
    if (high) (Bits.low32(existing) & 0x0000ffffL) | (Bits.low16(value) << 16)
    else (Bits.low32(existing) & 0xffff0000L) | Bits.low16(value)
  private[amdgpu] def andn2SaveExecMask(previous: Long, source: Long): Long =
    // SAVEEXEC keeps the explicit source as ANDN2's first operand and the
    // previous EXEC mask as its second operand: source & ~previous.
    source & ~previous
  private[amdgpu] def signedFirstBitHigh32(value: Long): Long = {
    val signed = Bits.low32(value).toInt
    if (signed == 0 || signed == -1) 0xffffffffL
    else if (signed >= 0) Integer.numberOfLeadingZeros(signed).toLong
    else Integer.numberOfLeadingZeros(~signed).toLong
  }
  private[amdgpu] def firstBitHigh32(value: Long): Long = {
    val unsigned = Bits.low32(value).toInt
    if (unsigned == 0) 0xffffffffL else Integer.numberOfLeadingZeros(unsigned).toLong
  }
  private[amdgpu] def floatClassMask(value: Float): Long = {
    val bits     = IEEE754.floatToBinary32(value).value
    val exponent = (bits >>> 23) & 0xff
    val fraction = bits & 0x7fffff
    val negative = (bits & 0x80000000L) != 0L
    val classBit =
      if (exponent == 0xff && fraction != 0) if ((fraction & 0x400000) == 0) 0 else 1
      else if (exponent == 0xff) if (negative) 2 else 9
      else if (exponent == 0 && fraction == 0) if (negative) 5 else 6
      else if (exponent == 0) if (negative) 4 else 7
      else if (negative) 3 else 8
    1L << classBit
  }
  private[amdgpu] def clampF32(value: Float): Float =
    if (value.isNaN) value else math.max(0.0f, math.min(1.0f, value))
  private[amdgpu] def trigF32(opcode: String, value: Float): Float = opcode match {
    // AMD's scalarized sin/cos instructions use turns, not radians: one
    // instruction unit is 2*pi radians.  HIP kernels commonly multiply
    // radians by 1/(2*pi) before reaching these instructions.
    case "v_sin_f32" => math.sin(2.0 * math.Pi * value.toDouble).toFloat
    case "v_cos_f32" => math.cos(2.0 * math.Pi * value.toDouble).toFloat
  }
  private[amdgpu] def packedFmaF16(
      srcA: Long,
      srcB: Long,
      srcC: Long,
      lowSelection: Int,
      highSelection: Int,
      negateLow: Int,
      negateHigh: Int,
      clamp: Boolean
  ): Long = {
    def half(source: Long, high: Boolean): Float =
      IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(if (high) source >>> 16 else source)))
    def calculate(selection: Int, negate: Int): Long = {
      def operand(source: Long, index: Int): Float = {
        val value = half(source, (selection & (1 << index)) != 0)
        if ((negate & (1 << index)) != 0) -value else value
      }
      val result  = Math.fma(operand(srcA, 0), operand(srcB, 1), operand(srcC, 2))
      val clamped = if (clamp) clampF32(result) else result
      IEEE754.floatToBinary16(clamped).value
    }
    calculate(lowSelection, negateLow) | (calculate(highSelection, negateHigh) << 16)
  }
  private[amdgpu] def fmaMixF16(
      existing: Long,
      srcA: Long,
      srcB: Long,
      srcC: Long,
      opSelection: Int,
      opSelectionHi: Int,
      negate: Int,
      absolute: Int,
      clamp: Boolean,
      high: Boolean
  ): Long = {
    def operand(source: Long, index: Int): Float = {
      val convertFromHalf = (opSelectionHi & (1 << index)) != 0
      val selectHigh      = (opSelection & (1 << index)) != 0
      val value           =
        if (convertFromHalf)
          IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(if (selectHigh) source >>> 16 else source)))
        else IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source)))
      val absoluteValue = if ((absolute & (1 << index)) != 0) math.abs(value) else value
      if ((negate & (1 << index)) != 0) -absoluteValue else absoluteValue
    }
    val rounded = IEEE754.floatToBinary16(Math.fma(operand(srcA, 0), operand(srcB, 1), operand(srcC, 2)))
    val value   =
      if (clamp) {
        val converted = IEEE754.binary16ToFloat(rounded)
        IEEE754.floatToBinary16(math.max(0.0f, math.min(1.0f, converted)))
      } else rounded
    mergeD16(existing, value.value, high)
  }
  private[amdgpu] def fmaMixF32(
      srcA: Long,
      srcB: Long,
      srcC: Long,
      opSelection: Int,
      opSelectionHi: Int,
      negate: Int,
      absolute: Int,
      clamp: Boolean
  ): Long = {
    def operand(source: Long, index: Int): Float = {
      val convertFromHalf = (opSelectionHi & (1 << index)) != 0
      val selectHigh      = (opSelection & (1 << index)) != 0
      val value           =
        if (convertFromHalf)
          IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(if (selectHigh) source >>> 16 else source)))
        else IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source)))
      val absoluteValue = if ((absolute & (1 << index)) != 0) math.abs(value) else value
      if ((negate & (1 << index)) != 0) -absoluteValue else absoluteValue
    }
    val result = Math.fma(operand(srcA, 0), operand(srcB, 1), operand(srcC, 2))
    IEEE754.floatToBinary32(if (clamp) clampF32(result) else result).value
  }
  private[amdgpu] def dot2F32F16(left: Long, right: Long, accumulator: Long, accumulate: Boolean): Binary32 = {
    val leftLow   = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left)))
    val leftHigh  = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left >>> 16)))
    val rightLow  = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right)))
    val rightHigh = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right >>> 16)))
    val products  = Math.fma(leftLow, rightLow, leftHigh * rightHigh)
    val result    =
      if (accumulate) products + IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(accumulator))) else products
    IEEE754.floatToBinary32(result)
  }
  // Four packed lanes are accumulated without creating an intermediate collection.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private[amdgpu] def dot4U32U8(left: Long, right: Long, accumulator: Long): Long = {
    var result = Bits.low32(accumulator)
    var index  = 0
    while (index < 4) {
      val leftByte  = (left >>> (index * 8)) & 0xffL
      val rightByte = (right >>> (index * 8)) & 0xffL
      result = Bits.low32(result + leftByte * rightByte)
      index += 1
    }
    result
  }

  /** AMD's unsigned float-to-dword conversion returns the truncated magnitude for negative inputs.
    *
    * LLVM uses this defined device behavior while lowering signed float-to-64-bit conversions: it restores the sign
    * with a subsequent XOR/subtract sequence. Converting a negative JVM Float directly to Long would instead produce
    * the two's-complement low dword, which breaks that sequence (for example, -3.0f would feed 0xfffffffd instead of
    * 3).
    */
  private[amdgpu] def unsignedFloatToInt32(value: Float): Long =
    Bits.low32((if (value < 0.0f) -value else value).toLong)
  private[amdgpu] def doubleClassMask(value: Double): Long = {
    val bits     = IEEE754.doubleToBinary64(value)
    val exponent = (bits >>> 52) & 0x7ffL
    val fraction = bits & 0x000fffffffffffffL
    val negative = bits < 0L
    val classBit =
      if (exponent == 0x7ffL && fraction != 0L) if ((fraction & 0x0008000000000000L) == 0L) 0 else 1
      else if (exponent == 0x7ffL) if (negative) 2 else 9
      else if (exponent == 0L && fraction == 0L) if (negative) 5 else 6
      else if (exponent == 0L) if (negative) 4 else 7
      else if (negative) 3 else 8
    1L << classBit
  }
  private[amdgpu] def signedMultiply24(left: Long, right: Long): Long = {
    def signed(value: Long): Long = {
      val bits = value & 0xffffffL
      if ((bits & 0x800000L) == 0L) bits else bits | ~0xffffffL
    }
    Bits.low32(signed(left) * signed(right))
  }
  private[amdgpu] def signedMultiplyHigh24(left: Long, right: Long): Long = {
    def signed(value: Long): Long = {
      val bits = value & 0xffffffL
      if ((bits & 0x800000L) == 0L) bits else bits | ~0xffffffL
    }
    Bits.low32((signed(left) * signed(right)) >> 32)
  }
  private[amdgpu] def fmaF32(opcode: String, leftBits: Long, rightBits: Long, addendBits: Long): Binary32 = {
    val left   = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftBits)))
    val right  = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(rightBits)))
    val addend = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(addendBits)))
    val result = if (opcode == "v_fma_f32") Math.fma(left, right, addend) else left * right + addend
    IEEE754.floatToBinary32(result)
  }
  private[amdgpu] def bitReverse32(value: Long): Long =
    Bits.reverseBits(value, BitWidth(32))
  private[amdgpu] def minimumF16(left: Long, right: Long): Binary16 =
    IEEE754.floatToBinary16(
      math.min(
        IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))),
        IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right)))
      )
    )
  private[amdgpu] def compareF16(opcode: String, left: Long, right: Long): Boolean = {
    val a = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left)))
    val b = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right)))
    opcode match {
      case "v_cmp_eq_f16"                   => a == b
      case "v_cmp_neq_f16" | "v_cmp_lg_f16" => a != b
      case "v_cmp_lt_f16"                   => a < b
      case "v_cmp_le_f16"                   => a <= b
      case "v_cmp_gt_f16"                   => a > b
      case "v_cmp_ge_f16"                   => a >= b
      case "v_cmp_nlt_f16"                  => !(a < b)
      case "v_cmp_nle_f16"                  => !(a <= b)
      case "v_cmp_ngt_f16"                  => !(a > b)
      case "v_cmp_nge_f16"                  => !(a >= b)
      case "v_cmp_o_f16"                    => !a.isNaN && !b.isNaN
      case "v_cmp_u_f16"                    => a.isNaN || b.isNaN
    }
  }
  private[amdgpu] def floatingLiteralToDouble(raw: Long): Double =
    IEEE754.binary64ToDouble(Bits.join32(0L, Bits.low32(raw)))
  private[amdgpu] def bpermuteSourceLane(address: Long): Int =
    bpermuteSourceLane(address, 0L)
  private[amdgpu] def bpermuteSourceLane(address: Long, offset: Long): Int =
    (((address + offset) >>> 2) & 63L).toInt
  private[amdgpu] def permuteDestinationLane(address: Long, offset: Long, waveSize: WaveSize): Int =
    (((address + offset) >>> 2) & (waveSize.value - 1L)).toInt
  private[amdgpu] def dppQuadSourceLane(lane: Int, control: Int): Int =
    (lane & ~3) | ((control >>> ((lane & 3) * 2)) & 3)
  private[amdgpu] def dppSourceLane(lane: Int, control: Int): Option[Int] =
    if (control <= 0xff) Some(dppQuadSourceLane(lane, control))
    else if (control >= 0x100 && control <= 0x10f) {
      val source = (lane & 15) + (control & 15)
      Option.when(source < 16)((lane & ~15) | source)
    } else if (control >= 0x110 && control <= 0x11f) {
      val source = (lane & 15) - (control & 15)
      Option.when(source >= 0)((lane & ~15) | source)
    } else if (control >= 0x121 && control <= 0x12f) {
      val rowBase = lane & ~15
      Some(rowBase | (((lane & 15) - (control & 15)) & 15))
    } else control match {
      case 0x130 => Option.when(lane < 63)(lane + 1)
      case 0x134 => Some((lane + 1) & 63)
      case 0x138 => Option.when(lane > 0)(lane - 1)
      case 0x13c => Some((lane - 1) & 63)
      case 0x142 => Option.when(((lane / 16) & 1) == 1)((lane / 16 - 1) * 16 + 15)
      case 0x143 => Option.when(lane / 16 >= 2)(31)
      case _     => None
    }
  private[amdgpu] def dppLaneEnabled(lane: Int, rowMask: Int, bankMask: Int): Boolean =
    (rowMask & (1 << (lane / 16))) != 0 && (bankMask & (1 << (lane & 3))) != 0
  private[amdgpu] def waveConditionMask(mask: Long, lanes: LaneCount): Long =
    if (lanes.value <= 32) Bits.low32(mask) else mask
  private[amdgpu] def mbCount(mask: Long, executionMask: Long, lane: Int, high: Boolean, base: Long): Long = {
    val laneInHalf = lane & 31
    val belowLane  = if (laneInHalf == 0) 0L else (1L << laneInHalf) - 1L
    val relevant   =
      if (high) {
        if (lane < 32) 0L else Bits.high32(executionMask) & belowLane
      } else {
        val belowLow = if (lane < 32) belowLane else Bits.Mask32
        executionMask & belowLow
      }
    Bits.low32(base + java.lang.Integer.bitCount((mask & relevant).toInt).toLong)
  }

  final class PreparedKernel private[DispatchInterpreter] (
      val kernel: CodeObjectKernel,
      private[DispatchInterpreter] val operations: Vec[DecodedInstruction],
      private[DispatchInterpreter] val executable: Array[ExecutableInstruction],
      private[DispatchInterpreter] val instructionIndices: Map[Int, Int],
      private[DispatchInterpreter] val instructionOffsets: Vec[Int],
      private[DispatchInterpreter] val entryInstruction: Int
  )

  private final case class Decoded(operation: DecodedInstruction, size: Int)
  private final case class DecodedProgram(
      operations: Vec[DecodedInstruction],
      instructionIndices: Map[Int, Int],
      instructionOffsets: Vec[Int]
  )
  final case class GlobalBinding(
      address: Long,
      size: Int :| Positive0,
      allocation: AllocationId,
      offset: Long :| Positive0,
      allocationSize: Long :| Positive
  )
  private final case class AllocationRegion(
      base: Long,
      id: AllocationId,
      offset: Long,
      span: Long = AllocationSpan,
      alias: Boolean = false
  )
  private val EmptyAllocationRegions = Array.empty[AllocationRegion]
  // Lane-wise register updates are an allocation-sensitive interpreter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def executeVMove(
      dest: Int,
      src: InstructionSource,
      scalar: Array[Long],
      vector: VectorRegisters,
      carry: LaneBits,
      active: LaneBits,
      lanes: LaneCount
  ): Unit = {
    val destination = vector(dest)
    src match {
      case InstructionSource.VectorRegister(index) =>
        val source = vector(index)
        var lane   = 0
        while (lane < lanes.value) {
          if (active(lane)) destination(lane) = Bits.low32(source(lane))
          lane += 1
        }
      case other =>
        val value = other match {
          case InstructionSource.Scalar(106)       => Bits.low32(carry.register)
          case InstructionSource.Scalar(107)       => Bits.high32(carry.register)
          case InstructionSource.Scalar(126)       => Bits.low32(active.register)
          case InstructionSource.Scalar(127)       => Bits.high32(active.register)
          case InstructionSource.Scalar(index)     => scalar(index)
          case InstructionSource.Constant(raw)     => raw
          case InstructionSource.Literal(raw)      => raw
          case InstructionSource.VectorRegister(_) => 0L
        }
        var lane = 0
        while (lane < lanes.value) {
          if (active(lane)) destination(lane) = Bits.low32(value)
          lane += 1
        }
    }
  }
  // Lane-wise packed register updates are an allocation-sensitive interpreter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def executePackedVMove(
      instruction: ExecutableInstruction,
      scalar: Array[Long],
      vector: VectorRegisters,
      carry: LaneBits,
      active: LaneBits,
      lanes: LaneCount
  ): Unit = {
    val destination = vector(ExecutableInstruction.vMoveDestination(instruction))
    val sourceValue = ExecutableInstruction.vMoveSourceValue(instruction)
    ExecutableInstruction.vMoveSourceKind(instruction) match {
      case 1 =>
        val source = vector(sourceValue.toInt)
        var lane   = 0
        while (lane < lanes.value) {
          if (active(lane)) destination(lane) = Bits.low32(source(lane))
          lane += 1
        }
      case kind =>
        val value = kind match {
          case 0 => sourceValue.toInt match {
              case 106   => Bits.low32(carry.register)
              case 107   => Bits.high32(carry.register)
              case 126   => Bits.low32(active.register)
              case 127   => Bits.high32(active.register)
              case index => scalar(index)
            }
          case 2 => sourceValue
        }
        var lane = 0
        while (lane < lanes.value) {
          if (active(lane)) destination(lane) = Bits.low32(value)
          lane += 1
        }
    }
  }
  // Null is the allocation-free first-failure sentinel in this measured packed-memory loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def executePackedGlobalLoad(
      instruction: ExecutableInstruction,
      index: Int,
      kernel: CodeObjectKernel,
      scalar: Array[Long],
      vector: VectorRegisters,
      active: LaneBits,
      lanes: LaneCount,
      kernarg: Array[Byte],
      dispatchBytes: Array[Byte],
      allocations: Array[AllocationRegion],
      memory: ExecutionMemory,
      traceByteSort: Boolean
  ): ExecutionFailure | Null = {
    val dest                             = ExecutableInstruction.memoryFirstRegister(instruction)
    val address                          = ExecutableInstruction.memorySecondRegister(instruction)
    val scalarAddress                    = ExecutableInstruction.memoryScalarAddress(instruction)
    val byteCount                        = ExecutableInstruction.memoryBytes(instruction)
    val offset                           = ExecutableInstruction.memoryOffset(instruction)
    var lane                             = 0
    var failure: ExecutionFailure | Null = null
    while (lane < lanes.value && failure == null) {
      if (active(lane)) {
        val base = scalarAddress
          .map(register => pair(scalar, register) + Bits.low32(vector(address)(lane)))
          .getOrElse(pair(vector, address, lane))
        val guestAddress = base + signed13(offset)
        readValue(
          guestAddress,
          byteCount,
          kernarg,
          dispatchBytes,
          kernel.readOnlyAddress,
          kernel.readOnly,
          kernel.globalMemory,
          allocations,
          memory
        ) match {
          case Left(error)  => failure = error
          case Right(value) =>
            writeVectorValue(vector, dest, lane, value, byteCount.value)
            if (AMDGPUEnvironment.trace("loads") && lane < 8)
              scribe.debug(
                s"Gemstone AMDGPU packed global load index=$index kernel=${kernel.name} lane=$lane base=0x${base.toHexString} addressReg=$address scalarAddress=$scalarAddress address=0x${guestAddress.toHexString} bytes=${byteCount.value} dest=$dest value=0x${value.toHexString}"
              )
            if (traceByteSort && lane == 0)
              scribe.debug(
                s"Gemstone AMDGPU byte-sort GlobalLoad index=$index dest=$dest address=0x${guestAddress.toHexString} bytes=${byteCount.value} value=0x${vector(dest)(lane).toHexString}"
              )
        }
      }
      lane += 1
    }
    failure
  }

  // Null is the allocation-free first-failure sentinel in this measured packed-memory loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def executePackedGlobalStore(
      instruction: ExecutableInstruction,
      index: Int,
      kernel: CodeObjectKernel,
      scalar: Array[Long],
      vector: VectorRegisters,
      active: LaneBits,
      lanes: LaneCount,
      allocations: Array[AllocationRegion],
      memory: ExecutionMemory
  ): ExecutionFailure | Null = {
    val address                          = ExecutableInstruction.memoryFirstRegister(instruction)
    val src                              = ExecutableInstruction.memorySecondRegister(instruction)
    val scalarAddress                    = ExecutableInstruction.memoryScalarAddress(instruction)
    val byteCount                        = ExecutableInstruction.memoryBytes(instruction)
    val offset                           = ExecutableInstruction.memoryOffset(instruction)
    val traceStores                      = AMDGPUEnvironment.trace("stores")
    var lane                             = 0
    var failure: ExecutionFailure | Null = null
    while (lane < lanes.value && failure == null) {
      if (active(lane)) {
        val base = scalarAddress
          .map(register => pair(scalar, register) + Bits.low32(vector(address)(lane)))
          .getOrElse(pair(vector, address, lane))
        val guestAddress = base + signed13(offset)
        if (
          traceStores && lane < 16 &&
          (kernel.name.contains("wrapped_scan") || kernel.name.contains("set_operations") ||
            AMDGPUEnvironment.trace("all-stores"))
        )
          scribe.debug(
            s"Gemstone AMDGPU global store index=$index lane=$lane kernel=${kernel.name} address=0x${guestAddress.toHexString} bytes=$byteCount src=$src values=${vectorBytes(vector, src, lane, byteCount.value).map(
                v => f"0x${v & 0xff}%02x"
              ).mkString("[", ",", "]")}"
          )
        val stored = writeValue(
          guestAddress,
          if (byteCount.value <= 4) vector(src)(lane) else pair(vector, src, lane),
          byteCount,
          allocations,
          memory,
          kernel.globalMemory
        )
        stored match {
          case Left(error) => failure = error
          case Right(())   => ()
        }
      }
      lane += 1
    }
    failure
  }

  private def packedSource(
      descriptor: Int,
      lane: Int,
      scalar: Array[Long],
      vector: VectorRegisters,
      carry: LaneBits,
      active: LaneBits
  ): Long =
    if ((descriptor & 0x100) != 0) vector(descriptor & 0xff)(lane)
    else descriptor match {
      case 106   => Bits.low32(carry.register)
      case 107   => Bits.high32(carry.register)
      case 126   => Bits.low32(active.register)
      case 127   => Bits.high32(active.register)
      case index => scalar(index)
    }

  // Packed arithmetic walks active lanes directly in the interpreter hot path.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def executePackedVBinary(
      instruction: ExecutableInstruction,
      index: Int,
      scalar: Array[Long],
      vector: VectorRegisters,
      carry: LaneBits,
      active: LaneBits,
      lanes: LaneCount,
      traceLog: Boolean,
      traceByteSort: Boolean
  ): Unit = {
    val opcode      = ExecutableInstruction.binaryOpcode(instruction)
    val dest        = ExecutableInstruction.binaryDestination(instruction)
    val srcA        = ExecutableInstruction.binarySourceA(instruction)
    val srcB        = ExecutableInstruction.binarySourceB(instruction)
    val clamp       = ExecutableInstruction.binaryClamp(instruction)
    val destination = vector(dest)

    // rocBLAS GEMM kernels use v_fmac_f32 for the inner product.  Keep this
    // common operation on a straight-line path: the general opcode match is
    // still used for every other VOP2 operation, while GEMM avoids repeating
    // the same 20-way dispatch for every active lane.
    if (opcode == ExecutableInstruction.VBinary.MacF32) {
      var lane = 0
      while (lane < lanes.value) {
        if (active(lane)) {
          val left     = Bits.low32(packedSource(srcA, lane, scalar, vector, carry, active))
          val right    = Bits.low32(packedSource(srcB, lane, scalar, vector, carry, active))
          val previous = Bits.low32(destination(lane))
          destination(lane) = IEEE754.floatToBinary32(
            IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) * IEEE754.binary32ToFloat(
              Binary32.fromLow(Bits.low32(right))
            ) +
              IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(previous)))
          ).value
          if (traceLog && lane == 0)
            scribe.debug(
              s"Gemstone AMDGPU log packed-binary opcode=v_fmac_f32 dest=$dest left=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left)))} right=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))} result=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(destination(lane))))}"
            )
        }
        lane += 1
      }
    } else {
      var lane = 0
      while (lane < lanes.value) {
        if (active(lane)) {
          val left     = Bits.low32(packedSource(srcA, lane, scalar, vector, carry, active))
          val right    = Bits.low32(packedSource(srcB, lane, scalar, vector, carry, active))
          val previous = Bits.low32(destination(lane))
          opcode match {
            case ExecutableInstruction.VBinary.AddU32 =>
              destination(lane) = if (clamp) math.min(Bits.Mask32, left + right) else Bits.low32(left + right)
            case ExecutableInstruction.VBinary.AddU16 =>
              destination(lane) = Bits.low16(left + right)
            case ExecutableInstruction.VBinary.MulLoU16 =>
              destination(lane) = Bits.low16(Bits.low16(left) * Bits.low16(right))
            case ExecutableInstruction.VBinary.LshlB16 =>
              destination(lane) = Bits.low16(right << (left.toInt & 15))
            case ExecutableInstruction.VBinary.LshrB16 =>
              destination(lane) = Bits.low16(right >>> (left.toInt & 15))
            case ExecutableInstruction.VBinary.AshrI16 =>
              destination(lane) = Bits.low16(Bits.low16(right).toShort >> (left.toInt & 15))
            case ExecutableInstruction.VBinary.AshrI32 =>
              destination(lane) = Bits.low32(Bits.low32(right).toInt >> (left.toInt & 31))
            case ExecutableInstruction.VBinary.MulLoU32 => destination(lane) = Bits.low32(left * right)
            case ExecutableInstruction.VBinary.MulHiU32 => destination(lane) = unsignedMultiplyHigh32(left, right)
            case ExecutableInstruction.VBinary.AndB32   => destination(lane) = left & right
            case ExecutableInstruction.VBinary.OrB32    => destination(lane) = left | right
            case ExecutableInstruction.VBinary.XorB32   => destination(lane) = left ^ right
            case ExecutableInstruction.VBinary.LshlB32  => destination(lane) = Bits.low32(right << (left.toInt & 31))
            case ExecutableInstruction.VBinary.LshrB32  => destination(lane) = right >>> (left.toInt & 31)
            case ExecutableInstruction.VBinary.SubU32   =>
              destination(lane) = if (clamp) math.max(0L, left - right) else Bits.low32(left - right)
            case ExecutableInstruction.VBinary.SubrevU32 =>
              destination(lane) = if (clamp) math.max(0L, right - left) else unsignedSubtractReverse32(left, right)
            case ExecutableInstruction.VBinary.AddF32 =>
              destination(lane) = IEEE754.floatToBinary32(
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) + IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(right))
                )
              ).value
            case ExecutableInstruction.VBinary.MulF32 =>
              destination(lane) = IEEE754.floatToBinary32(
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) * IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(right))
                )
              ).value
            case ExecutableInstruction.VBinary.MacF32 =>
              destination(lane) = IEEE754.floatToBinary32(
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) * IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(right))
                ) +
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(previous)))
              ).value
            case ExecutableInstruction.VBinary.SubF32 =>
              destination(lane) = IEEE754.floatToBinary32(
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) - IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(right))
                )
              ).value
            case ExecutableInstruction.VBinary.MinF32 =>
              destination(lane) = IEEE754.floatToBinary32(
                math.min(
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))),
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))
                )
              ).value
            case ExecutableInstruction.VBinary.MaxF32 =>
              destination(lane) = IEEE754.floatToBinary32(
                math.max(
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))),
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))
                )
              ).value
            case ExecutableInstruction.VBinary.AddF16 =>
              destination(lane) = IEEE754.floatToBinary16(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) + IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(right))
                )
              ).value
            case ExecutableInstruction.VBinary.MulF16 =>
              destination(lane) = IEEE754.floatToBinary16(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) * IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(right))
                )
              ).value
            case ExecutableInstruction.VBinary.MacF16 =>
              destination(lane) = IEEE754.floatToBinary16(
                Math.fma(
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))),
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right))),
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(previous)))
                )
              ).value
            case ExecutableInstruction.VBinary.MinF16 => destination(lane) = minimumF16(left, right).value
            case ExecutableInstruction.VBinary.MaxF16 =>
              destination(lane) = IEEE754.floatToBinary16(
                math.max(
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))),
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right)))
                )
              ).value
          }
          if (traceLog && lane == 0)
            scribe.debug(
              s"Gemstone AMDGPU log packed-binary opcode=${ExecutableInstruction.VBinary.opcodeName(opcode)} dest=$dest left=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left)))} right=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))} result=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(destination(lane))))}"
            )
          if (traceByteSort && lane == 0 && index >= 540 && index <= 620)
            scribe.debug(
              s"Gemstone AMDGPU byte-sort packed VBinary index=$index opcode=${ExecutableInstruction.VBinary.opcodeName(opcode)} dest=$dest left=0x${left.toHexString} right=0x${right.toHexString} result=0x${destination(lane).toHexString}"
            )
          if (
            AMDGPUEnvironment.trace("reduce") && lane == 0 &&
            ExecutableInstruction.VBinary.opcodeName(opcode) == "v_add_u32"
          )
            scribe.debug(
              s"Gemstone AMDGPU reduce packed add index=$index dest=$dest left=0x${left.toHexString} right=0x${right.toHexString} result=0x${destination(lane).toHexString}"
            )
        }
        lane += 1
      }
    }
  }
  def validate(code: Vec[Byte], target: Target): Either[String, Unit] =
    decodeProgram(code, target).map(_ => ())

  /** Decode one instruction for decoder-focused tests without exposing the executable model publicly. */
  private[amdgpu] def decodeInstruction(
      code: Vec[Byte],
      target: Target
  ): Either[String, DecodedInstruction] =
    Either.cond(code.size >= 4, (), "truncated instruction at byte 0")
      .flatMap(_ => decode(Vec.toArray(code), 0, target).map(_.operation))

  private def selfContained(program: DecodedProgram): Boolean = {
    def constant(source: InstructionSource): Option[Long] = source match {
      case InstructionSource.Constant(value) => Some(value)
      case InstructionSource.Literal(value)  => Some(value)
      case _                                 => None
    }
    def relativeJump(index: Int, source: InstructionSource): Option[Int] =
      if (index < 4) None
      else (program.operations.slice(index - 4, index), source) match {
        // A common long-branch sequence computes PC + a constant without changing its address space.
        // Prove its destination; do not rewrite the instructions or assume unknown indirect targets.
        // Restrict the proof to ordinary SGPRs, excluding the VCC/EXEC register aliases.
        case (
              Vec(
                DecodedInstruction.SGetPc(base, offset),
                DecodedInstruction.SBinary("s_add_i32", temporary, left, right),
                DecodedInstruction.SBinary(
                  "s_add_u32",
                  low,
                  InstructionSource.Scalar(lowSource),
                  InstructionSource.Scalar(addend)
                ),
                DecodedInstruction.SBinary("s_addc_u32", high, InstructionSource.Scalar(highSource), zero)
              ),
              InstructionSource.Scalar(jump)
            )
            if base >= 0 && base < 105 && temporary >= 0 && temporary < 106 &&
              base == jump && low == base && lowSource == base && high == base + 1 && highSource == high &&
              addend == temporary && temporary != base && temporary != high && constant(zero).contains(0L) =>
          for {
            a <- constant(left)
            b <- constant(right)
            target = offset.toLong + 4L + Bits.low32(a + b)
            instruction <-
              Option.when(target <= Int.MaxValue.toLong)(target.toInt).flatMap(program.instructionIndices.get)
          } yield instruction
        case _ => None
      }
    val jumps = program.operations.zipWithIndex.collect {
      case (DecodedInstruction.SSetPc(source), index) => index -> relativeJump(index, source)
    }
    val destinations = program.operations.collect { case DecodedInstruction.Branch(_, target) => target } ++
      jumps.flatMap(_._2)
    !program.operations.exists {
      case _: DecodedInstruction.SSwapPc | _: DecodedInstruction.BranchRelative => true
      case _                                                                    => false
    } && jumps.forall { case (index, target) =>
      target.nonEmpty && !destinations.exists(destination => destination > index - 4 && destination <= index)
    }
  }

  def prepare(kernel: CodeObjectKernel): Either[ExecutionFailure, PreparedKernel] = {
    val linkedText     = kernel.textAddress != 0L && kernel.text.nonEmpty
    val programCode    = if (linkedText) kernel.text else kernel.code
    val functionRanges =
      if (linkedText) linkedTextRanges(kernel.textAddress, kernel.text, kernel.functionSizes)
      else Vec.empty[(Int, Int)]
    val decoded = if (linkedText) {
      val entrySize  = math.min(kernel.code.size, kernel.functionSizes.getOrElse(kernel.codeAddress, kernel.code.size))
      val entryRange = linkedTextRanges(kernel.textAddress, kernel.text, Map(kernel.codeAddress -> entrySize))
      decodeProgram(programCode, kernel.target, entryRange, deferExternalBranches = true).flatMap { program =>
        // Decode unrelated functions only when the entry can transfer outside its own range.
        if (selfContained(program)) Right(program)
        else decodeProgram(programCode, kernel.target, functionRanges)
      }
    } else decodeProgram(programCode, kernel.target, functionRanges)
    decoded
      .leftMap(detail => ExecutionFailure.Unsupported(detail))
      .flatMap { program =>
        val entryOffset = if (linkedText) kernel.codeAddress - kernel.textAddress else 0L
        val entryIndex  =
          if (entryOffset == 0L) Some(0)
          else if (entryOffset < 0L || entryOffset > Int.MaxValue.toLong) None
          else program.instructionIndices.get(entryOffset.toInt)
        entryIndex.toRight(
          ExecutionFailure.InvalidRequest(
            f"AMDGPU kernel ${kernel.name} entry 0x${kernel.codeAddress}%X is not an instruction in .text"
          )
        ).map { entry =>
          PreparedKernel(
            kernel,
            program.operations,
            ExecutableInstruction.compile(program.operations),
            program.instructionIndices,
            program.instructionOffsets,
            entry
          )
        }
      }
  }

  def execute(
      prepared: PreparedKernel,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    execute(prepared, dispatch, memory, control, Vec.empty)

  def execute(
      prepared: PreparedKernel,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl,
      globalBindings: Vec[GlobalBinding]
  ): Either[ExecutionFailure, Unit] =
    for {
      kernargSize <- effectiveKernargSize(prepared.kernel, dispatch.arguments)
        .leftMap(detail => ExecutionFailure.InvalidRequest(detail))
      addresses = memory.allocationAddresses
      _ <- validateAddressAliases(prepared.kernel, kernargSize, addresses)
        .leftMap(detail => ExecutionFailure.InvalidRequest(detail))
      packed <- packArguments(dispatch.arguments, kernargSize, globalBindings, addresses)
        .leftMap(detail => ExecutionFailure.InvalidRequest(detail))
      (kernarg, allocations) = packed
      _ <- runDispatch(
        prepared.kernel,
        prepared.operations,
        prepared.executable,
        prepared.instructionIndices,
        prepared.instructionOffsets,
        prepared.entryInstruction,
        kernarg,
        allocations,
        dispatch,
        memory,
        control
      )
    } yield ()

  private[amdgpu] def effectiveKernargSize(
      kernel: CodeObjectKernel,
      arguments: Vec[KernelArgument]
  ): Either[String, Int] =
    if (kernel.kernargSize != 0 || arguments.isEmpty || kernel.codeAddress == 0L) Right(kernel.kernargSize)
    else
      arguments.foldLeft[Either[String, Long]](Right(0L)) { (total, argument) =>
        total.flatMap { current =>
          val size = argument match {
            case KernelArgument.Scalar(bytes)    => Right(bytes.length.toLong)
            case KernelArgument.Allocation(_, _) => Right(8L)
            case KernelArgument.Local(_)         => Left("gfx900 local kernargs are not implemented")
          }
          size.flatMap(value =>
            Either.cond(current <= Int.MaxValue.toLong - value, current + value, "kernel arguments are too large")
          )
        }
      }.map(_.toInt)

  def execute(
      kernel: CodeObjectKernel,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl = ExecutionControl.default
  ): Either[ExecutionFailure, Unit] = prepare(kernel).flatMap(execute(_, dispatch, memory, control))

  // Validate and coalesce linked text ranges in one ordered scan.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def linkedTextRanges(base: Long, code: Vec[Byte], sizes: Map[Long, Int]): Vec[(Int, Int)] = {
    val bytes  = Vec.toArray(code)
    val length = bytes.length
    val ranges = sizes.iterator.flatMap { case (address, size) =>
      val relative = address - base
      if (relative >= 0L && relative < length.toLong && size > 0) {
        val bounded = math.min(size.toLong, length.toLong - relative)
        Option.when(bounded > 0L)((relative.toInt, bounded.toInt))
      } else None
    }.toVec.sortBy(_._1)
    if (ranges.isEmpty) Vec.empty
    else {
      val normalized = Vec.newBuilder[(Int, Int)]
      var index      = 0
      while (index < ranges.size) {
        val (start, size) = ranges(index)
        val nextStart     = if (index + 1 < ranges.size) ranges(index + 1)._1 else length
        var end           = math.min(start + size, nextStart)
        // Inferred ELF function sizes commonly include the alignment gap up
        // to the next symbol. Drop complete all-zero words from the tail so
        // a gap cannot be mistaken for an instruction.
        while (
          end - start >= 4 && bytes(end - 4) == 0 && bytes(end - 3) == 0 && bytes(end - 2) == 0 && bytes(end - 1) == 0
        )
          end -= 4
        if (end > start) normalized += start -> (end - start)
        index += 1
      }
      normalized.result()
    }
  }

  // Decode variable-width instructions while retaining the first malformed range.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def decodeProgram(
      code: Vec[Byte],
      target: Target,
      functionRanges: Vec[(Int, Int)] = Vec.empty,
      deferExternalBranches: Boolean = false
  ): Either[String, DecodedProgram] = {
    val bytes      = Vec.toArray(code)
    val ranges     = if (functionRanges.nonEmpty) functionRanges else Vec(0 -> bytes.length)
    val result     = Vec.newBuilder[(Int, DecodedInstruction)]
    var rangeIndex = 0
    var failure    = Option.empty[String]
    while (rangeIndex < ranges.length && failure.isEmpty) {
      val rangeStart = ranges(rangeIndex)._1
      val rangeEnd   = math.min(bytes.length, rangeStart + ranges(rangeIndex)._2)
      var offset     = rangeStart
      while (offset < rangeEnd && failure.isEmpty)
        if (offset > bytes.length - 4 || offset > rangeEnd - 4) {
          // A function's inferred size can include a short alignment tail.
          // Accept it only when every remaining byte is zero; arbitrary
          // truncated/non-instruction bytes must still fail validation.
          val padding = (offset until rangeEnd).forall(bytes(_) == 0)
          if (padding) offset = rangeEnd
          else failure = Some(s"truncated instruction at byte $offset")
        } else
          decode(bytes, offset, target) match {
            case Right(decoded) if offset + decoded.size <= rangeEnd =>
              result += offset -> decoded.operation
              offset += decoded.size
            case Right(_) =>
              failure = Some(s"instruction at byte $offset exceeds function range")
            case Left(_) if (offset until rangeEnd).forall(bytes(_) == 0) =>
              // Linkers commonly place an all-zero alignment gap after a
              // symbol whose size was inferred from the next symbol. Do not
              // interpret it as code, but never skip non-zero bytes.
              offset = rangeEnd
            case Left(error) => failure = Some(s"byte $offset: $error")
          }
      rangeIndex += 1
    }
    failure.toLeft(Vec.from(result.result())).flatMap { decoded =>
      // Byte offsets are dense integer keys; IntMap avoids the boxed tuple and
      // generic hash-map builder previously allocated for every decoded code
      // object while retaining the same immutable lookup contract.
      val indices = IntMap.from(decoded.iterator.zipWithIndex.map { case ((offset, _), index) => offset -> index })
      decoded.map { case (offset, operation) =>
        operation match {
          case DecodedInstruction.BranchRelative(condition, displacement) =>
            val targetOffset = offset.toLong + 4L + displacement.toLong * 4L
            val target = Option.when(targetOffset >= 0L && targetOffset <= Int.MaxValue.toLong)(targetOffset.toInt)
              .flatMap(indices.get)
            target match {
              case Some(index)                   => Right(DecodedInstruction.Branch(condition, index))
              case None if deferExternalBranches => Right(operation)
              case None => Left(s"branch at byte $offset targets non-instruction byte $targetOffset")
            }
          case other => Right(other)
        }
      }.sequence.map(operations => DecodedProgram(operations, indices, decoded.map(_._1)))
    }
  }

  private def decode(bytes: Array[Byte], offset: Int, target: Target): Either[String, Decoded] = {
    val word = unsignedInt(bytes, offset)
    // GFX6--9 use the 0x30 SMEM prefix; GFX10+ moved the same scalar-memory
    // encoding to 0x3d.  The operand fields and literal layout are otherwise
    // identical for the subset interpreted here.
    if ((word >>> 26) == 0x30L || (word >>> 26) == 0x3dL) decodeSMEM(bytes, offset, word, target)
    else if ((word >>> 23) == 0x17dL) decodeSOP1(bytes, offset, word, target)
    else if ((word >>> 23) == 0x17eL) decodeSOPC(bytes, offset, word, target)
    else if ((word >>> 23) == 0x17fL) decodeSOPP(word, target)
    else if ((word >>> 28) == 0xbL) decodeSOPK(word, target)
    else if ((word >>> 30) == 2L) decodeSOP2(bytes, offset, word, target)
    else if ((word >>> 26) == 0x33L) decodeVOP3P(bytes, offset, word, target)
    else if ((word >>> 26) == 0x34L || (word >>> 26) == 0x35L) decodeVOP3(bytes, offset, word, target)
    else if ((word >>> 26) == 0x36L) decodeDS(bytes, offset, word, target)
    else if ((word >>> 26) == 0x37L) decodeGlobalOrFlat(bytes, offset, word, target)
    else if ((word >>> 26) == 0x38L) decodeMUBUF(bytes, offset, word, target)
    else if ((word >>> 25) == 0x3fL) decodeVOP1(bytes, offset, word, target)
    else if ((word >>> 25) == 0x3eL) decodeVOPC(bytes, offset, word, target)
    else decodeVOP2(bytes, offset, word, target)
  }

  private def decodeSMEM(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _      <- requireBytes(bytes, offset, 8)
      opcode <- opcodeName("SMEM", ((word >>> 18) & 0xffL).toInt, target)
      count  <- opcode match {
        case "s_load_dword"    => Right(1)
        case "s_load_dwordx2"  => Right(2)
        case "s_load_dwordx4"  => Right(4)
        case "s_load_dwordx8"  => Right(8)
        case "s_load_dwordx16" => Right(16)
        case other             => Left(s"$other SMEM execution is not implemented")
      }
      // GFX10+ moved the immediate/SGPR offset selector out of the first
      // dword.  The high seven bits of the second dword carry SOFFSET; the
      // null-register encoding (0x7d on pre-GFX11 targets) denotes an
      // immediate offset.  GFX6--9 retain the explicit IMM bit in dword 0.
      second    = unsignedInt(bytes, offset + 4)
      immediate = target.family match {
        case Family.GFX10 | Family.GFX11 | Family.GFX12 => ((second >>> 25) & 0x7fL) == 0x7dL
        case _                                          => (word & (1L << 17)) != 0L
      }
    } yield Decoded(
      DecodedInstruction.SLoad(
        ((word >>> 6) & 0x7fL).toInt,
        count,
        ((word & 0x3fL) * 2L).toInt,
        if (immediate) (second & 0x1fffffL).toInt else 0,
        Option.when(!immediate)((second >>> 25 & 0x7fL).toInt)
      ),
      8
    )

  private def decodeSOP1(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      opcode <- opcodeName("SOP1", ((word >>> 8) & 0xffL).toInt, target)
      literal = Option.when((word & 0xffL) == 255L)(())
      _   <- requireBytes(bytes, offset, if (literal.nonEmpty) 8 else 4)
      src <- scalarSource(
        (word & 0xffL).toInt,
        literal.map(_ => unsignedInt(bytes, offset + 4))
      )
      operation <- opcode match {
        case "s_mov_b32"  => Right(DecodedInstruction.SMove(((word >>> 16) & 0x7fL).toInt, src))
        case "s_mov_b64"  => Right(DecodedInstruction.SMove64(((word >>> 16) & 0x7fL).toInt, src))
        case "s_cmov_b32" =>
          val dest = ((word >>> 16) & 0x7fL).toInt
          Right(DecodedInstruction.SCSelect(dest, src, InstructionSource.Scalar(dest), 32))
        case "s_cmov_b64" =>
          val dest = ((word >>> 16) & 0x7fL).toInt
          Right(DecodedInstruction.SCSelect(dest, src, InstructionSource.Scalar(dest), 64))
        case "s_getpc_b64"        => Right(DecodedInstruction.SGetPc(((word >>> 16) & 0x7fL).toInt, offset))
        case "s_setpc_b64"        => Right(DecodedInstruction.SSetPc(src))
        case "s_swappc_b64"       => Right(DecodedInstruction.SSwapPc(((word >>> 16) & 0x7fL).toInt, src))
        case "s_and_saveexec_b32" =>
          Right(DecodedInstruction.SAndSaveExec(((word >>> 16) & 0x7fL).toInt, src, 32))
        case "s_and_saveexec_b64" =>
          Right(DecodedInstruction.SAndSaveExec(((word >>> 16) & 0x7fL).toInt, src, 64))
        case "s_andn2_saveexec_b32" =>
          Right(DecodedInstruction.SAndn2SaveExec(((word >>> 16) & 0x7fL).toInt, src, 32))
        case "s_andn2_saveexec_b64" =>
          Right(DecodedInstruction.SAndn2SaveExec(((word >>> 16) & 0x7fL).toInt, src, 64))
        case "s_or_saveexec_b32" =>
          Right(DecodedInstruction.SOrSaveExec(((word >>> 16) & 0x7fL).toInt, src, 32))
        case "s_or_saveexec_b64" =>
          Right(DecodedInstruction.SOrSaveExec(((word >>> 16) & 0x7fL).toInt, src, 64))
        case "s_xor_saveexec_b32" =>
          Right(DecodedInstruction.SXorSaveExec(((word >>> 16) & 0x7fL).toInt, src, 32))
        case "s_xor_saveexec_b64" =>
          Right(DecodedInstruction.SXorSaveExec(((word >>> 16) & 0x7fL).toInt, src, 64))
        case "s_abs_i32" | "s_sext_i32_i8" | "s_sext_i32_i16" | "s_bcnt1_i32_b32" | "s_bcnt1_i32_b64" | "s_brev_b32" | "s_not_b32" | "s_not_b64" |
            "s_flbit_i32" | "s_bitset0_b32" | "s_bitset1_b32" =>
          Right(DecodedInstruction.SUnary(opcode, ((word >>> 16) & 0x7fL).toInt, src))
        case other => Left(s"$other SOP1 execution is not implemented")
      }
    } yield Decoded(operation, if (literal.nonEmpty) 8 else 4)

  private def decodeSOP2(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = (word & 0xffL).toInt
    val srcBEncoding = ((word >>> 8) & 0xffL).toInt
    val hasLiteral   = srcAEncoding == 255 || srcBEncoding == 255
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral) 8 else 4)
      opcode <- opcodeName("SOP2", ((word >>> 23) & 0x7fL).toInt, target)
      literal = Option.when(hasLiteral)(unsignedInt(bytes, offset + 4))
      srcA      <- scalarSource(srcAEncoding, literal)
      srcB      <- scalarSource(srcBEncoding, literal)
      operation <- Either.cond(
        SupportedSOP2Opcodes.contains(opcode),
        if (opcode == "s_cselect_b32")
          DecodedInstruction.SCSelect(((word >>> 16) & 0x7fL).toInt, srcA, srcB, 32)
        else if (opcode == "s_cselect_b64")
          DecodedInstruction.SCSelect(((word >>> 16) & 0x7fL).toInt, srcA, srcB, 64)
        else if (opcode.endsWith("_b64") || opcode == "s_ashr_i64" || opcode == "s_bfe_i64")
          DecodedInstruction.SBinary64(opcode, ((word >>> 16) & 0x7fL).toInt, srcA, srcB)
        else DecodedInstruction.SBinary(opcode, ((word >>> 16) & 0x7fL).toInt, srcA, srcB),
        s"$opcode SOP2 execution is not implemented"
      )
    } yield Decoded(operation, if (hasLiteral) 8 else 4)
  }

  private def decodeSOPK(word: Long, target: Target): Either[String, Decoded] =
    opcodeName("SOPK", ((word >>> 23) & 0x1fL).toInt, target).flatMap {
      // Memory and LDS operations finish synchronously before the next instruction.
      case "s_waitcnt_vscnt" | "s_waitcnt_lgkmcnt" => Right(Decoded(DecodedInstruction.Wait, 4))
      case "s_movk_i32"                            =>
        val bits      = Bits.low16(word).toInt
        val immediate = if ((bits & 0x8000) == 0) bits else bits - 0x10000
        Right(Decoded(DecodedInstruction.SMoveImmediate(((word >>> 16) & 0x7fL).toInt, immediate), 4))
      case "s_mulk_i32" =>
        val bits      = Bits.low16(word).toInt
        val immediate = if ((bits & 0x8000) == 0) bits else bits - 0x10000
        Right(Decoded(DecodedInstruction.SMultiplyImmediate(((word >>> 16) & 0x7fL).toInt, immediate), 4))
      case "s_addk_i32" =>
        val bits      = Bits.low16(word).toInt
        val immediate = if ((bits & 0x8000) == 0) bits else bits - 0x10000
        Right(Decoded(DecodedInstruction.SAddImmediate(((word >>> 16) & 0x7fL).toInt, immediate), 4))
      case "s_setreg_b32" | "s_setreg_imm32_b32" =>
        // Hardware register writes (notably FLAT_SCR_LO/HI) configure the
        // physical wave, while the virtual machine models scratch storage
        // directly per lane. Retain the decoded operation so compiler-
        // generated prologues execute deterministically as a no-op.
        Right(
          Decoded(
            DecodedInstruction.SSetReg(Bits.low16(word).toInt, ((word >>> 16) & 0x7fL).toInt),
            4
          )
        )
      case compare @ ("s_cmpk_eq_i32" | "s_cmpk_lg_i32" | "s_cmpk_gt_i32" | "s_cmpk_lt_i32" | "s_cmpk_eq_u32" | "s_cmpk_lg_u32" | "s_cmpk_gt_u32" | "s_cmpk_ge_u32" | "s_cmpk_lt_u32" | "s_cmpk_le_u32") =>
        val bits      = Bits.low16(word).toInt
        val immediate = if ((bits & 0x8000) == 0) bits else bits - 0x10000
        Right(Decoded(DecodedInstruction.SCmpImmediate(compare, ((word >>> 16) & 0x7fL).toInt, immediate), 4))
      case other => Left(s"$other SOPK execution is not implemented")
    }

  private def decodeSOPC(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = (word & 0xffL).toInt
    val srcBEncoding = ((word >>> 8) & 0xffL).toInt
    val hasLiteral   = srcAEncoding == 255 || srcBEncoding == 255
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral) 8 else 4)
      opcode <- opcodeName("SOPC", ((word >>> 16) & 0x7fL).toInt, target)
      literal = Option.when(hasLiteral)(unsignedInt(bytes, offset + 4))
      srcA      <- scalarSource(srcAEncoding, literal)
      srcB      <- scalarSource(srcBEncoding, literal)
      operation <- opcode match {
        case supported
            if SupportedSOPCOpcodes.contains(supported) =>
          Right(DecodedInstruction.SCmp(supported, srcA, srcB))
        case "s_set_gpr_idx_on" =>
          // The interpreter has no hardware GPR-index mode. Keep this
          // compiler-generated mode transition explicit, while preserving
          // ordinary register addressing for the virtual execution model.
          Right(DecodedInstruction.SSetGprIdxOn)
        case other => Left(s"$other SOPC execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral) 8 else 4)
  }

  private def decodeSOPP(word: Long, target: Target): Either[String, Decoded] =
    opcodeName("SOPP", ((word >>> 16) & 0x7fL).toInt, target).flatMap {
      case "s_waitcnt" | "s_waitcnt_vscnt" => Right(Decoded(DecodedInstruction.Wait, 4))
      case "s_nop" | "s_inst_prefetch"     => Right(Decoded(DecodedInstruction.Wait, 4))
      // `s_clause` only constrains the hardware scheduler's clause grouping;
      // it has no observable effect in the deterministic virtual machine.
      case "s_clause" => Right(Decoded(DecodedInstruction.Wait, 4))
      // Wave priority only affects physical scheduling; virtual execution is
      // deterministic and therefore treats the hint as a no-op.
      case "s_setprio" => Right(Decoded(DecodedInstruction.Wait, 4))
      case "s_barrier" => Right(Decoded(DecodedInstruction.Barrier, 4))
      case "s_endpgm"  => Right(Decoded(DecodedInstruction.End, 4))
      // LLVM emits s_code_end as instruction-cache padding at the end of a
      // linked GFX10+ .text section.  It is not a program terminator (the
      // kernel's s_endpgm performs that job); treating the padding marker as
      // a no-op lets us index the whole linked text without executing it.
      case "s_code_end"       => Right(Decoded(DecodedInstruction.Wait, 4))
      case "s_branch"         => branch(word, ScalarBranchCondition.Always)
      case "s_cbranch_scc0"   => branch(word, ScalarBranchCondition.SccZero)
      case "s_cbranch_scc1"   => branch(word, ScalarBranchCondition.SccNonZero)
      case "s_cbranch_vccz"   => branch(word, ScalarBranchCondition.VccZero)
      case "s_cbranch_vccnz"  => branch(word, ScalarBranchCondition.VccNonZero)
      case "s_cbranch_execz"  => branch(word, ScalarBranchCondition.ExecZero)
      case "s_cbranch_execnz" => branch(word, ScalarBranchCondition.ExecNonZero)
      case "s_sleep"          => Right(Decoded(DecodedInstruction.Wait, 4))
      case other              => Left(s"$other SOPP execution is not implemented")
    }

  private def branch(word: Long, condition: ScalarBranchCondition): Either[String, Decoded] = {
    val immediate    = Bits.low16(word).toInt
    val displacement = if ((immediate & 0x8000) == 0) immediate else immediate - 0x10000
    Right(Decoded(DecodedInstruction.BranchRelative(condition, displacement), 4))
  }

  private def decodeVOP2(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = (word & 0x1ffL).toInt
    val opcodeResult = opcodeName("VOP2", ((word >>> 25) & 0x7fL).toInt, target)
    val hasLiteral   = srcAEncoding == 255 || opcodeResult.exists(opcode =>
      opcode == "v_madmk_f32" || opcode == "v_fmamk_f32" || opcode == "v_fmaak_f32"
    )
    val hasSDWA = srcAEncoding == 249
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral || hasSDWA) 8 else 4)
      opcode <- opcodeResult.leftMap(error => s"$error (word=0x${word.toHexString})")
      extension = Option.when(hasSDWA)(unsignedInt(bytes, offset + 4))
      srcA <- extension match {
        case Some(sdwa) =>
          val encoding = (sdwa & 0xffL).toInt
          Right(
            if ((sdwa & (1L << 23)) != 0L) InstructionSource.Scalar(encoding)
            else InstructionSource.VectorRegister(encoding)
          )
        case None => vectorSource(
            srcAEncoding,
            Option.when(hasLiteral)(unsignedInt(bytes, offset + 4))
          )
      }
      // VOP2's SRC1 field is a VGPR index in the ordinary encoding.  It is
      // only allowed to name an SGPR when the SDWA extension explicitly
      // selects scalar source mode.
      srcB = extension match {
        case Some(sdwa) if (sdwa & 0x80000000L) != 0L => InstructionSource.Scalar(((word >>> 9) & 0xffL).toInt)
        case _                                        => InstructionSource.VectorRegister(((word >>> 9) & 0xffL).toInt)
      }
      literal = Option.when(hasLiteral)(unsignedInt(bytes, offset + 4))
      dest    = ((word >>> 17) & 0xffL).toInt
      operation <- opcode match {
        case supported if hasSDWA && SDWAVOP2Opcodes.contains(supported) =>
          for {
            sdwa <- extension.toRight("missing SDWA extension word")
            _    <- Either.cond(
              (sdwa & 0x30300000L) == 0L,
              (),
              s"$supported SDWA source modifiers are not implemented"
            )
            _ <- Either.cond(
              ((sdwa >>> 13) & 0x7L) == 0L,
              (),
              s"$supported SDWA destination clamp modifiers are not implemented"
            )
            destSelection <- subDwordSelection(((sdwa >>> 8) & 0x7L).toInt)
            destUnused    <- subDwordDestinationUnused(((sdwa >>> 11) & 0x3L).toInt)
            srcASelection <- subDwordSelection(((sdwa >>> 16) & 0x7L).toInt)
            srcBSelection <- subDwordSelection(((sdwa >>> 24) & 0x7L).toInt)
          } yield DecodedInstruction.VBinarySubDword(
            supported,
            dest,
            srcA,
            srcB,
            destSelection,
            destUnused,
            srcASelection,
            srcBSelection,
            (sdwa & (1L << 19)) != 0L,
            (sdwa & (1L << 27)) != 0L
          )
        case unsupported if hasSDWA =>
          Left(s"$unsupported SDWA execution is not implemented")
        case "v_madmk_f32" | "v_fmamk_f32" =>
          literal.map(value => DecodedInstruction.VMadMkF32(dest, srcA, value, srcB))
            .toRight(s"$opcode is missing its literal operand")
        case "v_fmaak_f32" =>
          literal.map(value => DecodedInstruction.VMadAkF32(dest, srcA, srcB, value))
            .toRight(s"$opcode is missing its literal operand")
        case "v_dot2_f32_f16" =>
          Right(DecodedInstruction.VDot2F32F16(dest, srcA, srcB, accumulate = false))
        case "v_dot2c_f32_f16" =>
          Right(DecodedInstruction.VDot2F32F16(dest, srcA, srcB, accumulate = true))
        case "v_mov_b32"     => Right(DecodedInstruction.VMove(dest, srcA))
        case "v_not_b32"     => Right(DecodedInstruction.VNot(dest, srcA))
        case "v_cndmask_b32" =>
          Right(DecodedInstruction.VCndMask(
            dest,
            srcA,
            srcB,
            InstructionSource.Scalar(106),
            false,
            false,
            false,
            false
          ))
        case "v_add_co_u32" =>
          Right(DecodedInstruction.VAddCarry(dest, 106, srcA, srcB, None))
        case "v_add_co_ci_u32" =>
          Right(DecodedInstruction.VAddCarry(dest, 106, srcA, srcB, Some(InstructionSource.Scalar(106))))
        case "v_addc_co_u32" =>
          Right(DecodedInstruction.VAddCarry(dest, 106, srcA, srcB, Some(InstructionSource.Scalar(106))))
        case "v_sub_co_u32" =>
          Right(DecodedInstruction.VSubCarry(dest, 106, srcA, srcB, None))
        case "v_sub_co_ci_u32" =>
          Right(DecodedInstruction.VSubCarry(dest, 106, srcA, srcB, Some(InstructionSource.Scalar(106))))
        case "v_subb_co_u32" =>
          Right(DecodedInstruction.VSubCarry(dest, 106, srcA, srcB, Some(InstructionSource.Scalar(106))))
        case "v_subrev_co_u32" =>
          Right(DecodedInstruction.VSubReverseCarry(dest, 106, srcA, srcB, None))
        case "v_subbrev_co_u32" | "v_subrev_co_ci_u32" =>
          Right(DecodedInstruction.VSubReverseCarry(dest, 106, srcA, srcB, Some(InstructionSource.Scalar(106))))
        case "v_add_nc_u32"    => Right(DecodedInstruction.VBinary("v_add_u32", dest, srcA, srcB, clamp = false))
        case "v_sub_nc_u32"    => Right(DecodedInstruction.VBinary("v_sub_u32", dest, srcA, srcB, clamp = false))
        case "v_subrev_nc_u32" => Right(DecodedInstruction.VBinary("v_subrev_u32", dest, srcA, srcB, clamp = false))
        case "v_add_u32" |
            "v_ashrrev_i32" | "v_ashrrev_i16" | "v_lshlrev_b32" | "v_lshrrev_b32" | "v_add_u16" | "v_and_b32" | "v_or_b32" |
            "v_lshlrev_b16" | "v_lshrrev_b16" | "v_xor_b32" | "v_sub_u16" | "v_sub_u32" | "v_subrev_u32" | "v_subrev_f32" | "v_mul_u32_u24" | "v_mul_hi_u32_u24" |
            "v_mul_i32_i24" | "v_mul_hi_i32_i24" | "v_min_u32" | "v_min_i32" | "v_min_i16" | "v_min_u16" | "v_min_f16" | "v_min_f32" | "v_max_u32" | "v_max_i32" | "v_max_i16" | "v_max_u16" | "v_max_f16" | "v_max_f32" | "v_mul_f32" | "v_add_f32" | "v_add_f16" | "v_sub_f32" | "v_mul_lo_u16" | "v_mul_f16" | "v_mac_f32" | "v_fmac_f32" | "v_fmac_f16" | "v_add_nc_u16" =>
          Right(DecodedInstruction.VBinary(
            if (opcode == "v_fmac_f32" || opcode == "v_add_nc_u16")
              if (opcode == "v_fmac_f32") "v_mac_f32" else "v_add_u16"
            else opcode,
            dest,
            srcA,
            srcB,
            clamp = false
          ))
        case other =>
          Left(s"$other VOP2 execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral || hasSDWA) 8 else 4)
  }

  private def decodeVOP1(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val sourceEncoding = (word & 0x1ffL).toInt
    val hasLiteral     = sourceEncoding == 255
    val hasDPP         = sourceEncoding == 250
    val hasSDWA        = sourceEncoding == 249
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral || hasDPP || hasSDWA) 8 else 4)
      opcode <- opcodeName("VOP1", ((word >>> 9) & 0xffL).toInt, target)
      extension = Option.when(hasDPP || hasSDWA)(unsignedInt(bytes, offset + 4))
      src <- extension match {
        case Some(sdwa) if hasSDWA =>
          val encoding = (sdwa & 0xffL).toInt
          Right(if ((sdwa & (1L << 23)) != 0L) InstructionSource.Scalar(encoding)
          else InstructionSource.VectorRegister(encoding))
        case Some(dpp) => Right(InstructionSource.VectorRegister((dpp & 0xffL).toInt))
        case None      => vectorSource(
            sourceEncoding,
            Option.when(hasLiteral)(unsignedInt(bytes, offset + 4))
          )
      }
      operation <- opcode match {
        case "v_mov_b32" if hasDPP =>
          val dpp     = extension.get
          val control = ((dpp >>> 8) & 0x1ffL).toInt
          for {
            _ <- Either.cond(
              control <= 0xff || (control >= 0x100 && control <= 0x13f) || control == 0x142 || control == 0x143,
              (),
              s"v_mov_b32 DPP control $control is not implemented"
            )
            _ <- Either.cond(
              (dpp & 0x00f00000L) == 0L,
              (),
              "v_mov_b32 DPP source modifiers are not implemented"
            )
          } yield DecodedInstruction.VMoveDPP(
            ((word >>> 17) & 0xffL).toInt,
            (dpp & 0xffL).toInt,
            control,
            ((dpp >>> 28) & 0xfL).toInt,
            ((dpp >>> 24) & 0xfL).toInt,
            (dpp & (1L << 19)) != 0L,
            (dpp & (1L << 18)) != 0L
          )
        case unsupported if hasDPP =>
          Left(s"$unsupported DPP execution is not implemented")
        case "v_mov_b32"     => Right(DecodedInstruction.VMove(((word >>> 17) & 0xffL).toInt, src))
        case "v_movrels_b32" => Right(DecodedInstruction.VMoveRelative(((word >>> 17) & 0xffL).toInt, src))
        case "v_movreld_b32" => Right(DecodedInstruction.VMoveRelativeDestination(((word >>> 17) & 0xffL).toInt, src))
        case "v_not_b32"     => Right(DecodedInstruction.VNot(((word >>> 17) & 0xffL).toInt, src))
        case "v_swap_b32"    => Right(DecodedInstruction.VSwap(((word >>> 17) & 0xffL).toInt, src))
        case "v_bfrev_b32" | "v_bfrev_b32_e32" =>
          Right(DecodedInstruction.VBitReverse(((word >>> 17) & 0xffL).toInt, src))
        case "v_ffbh_i32" | "v_ffbh_u32" | "v_ffbl_b32" =>
          Right(DecodedInstruction.VBitFind(opcode, ((word >>> 17) & 0xffL).toInt, src))
        case "v_frexp_exp_i32_f64" =>
          Right(DecodedInstruction.VConvert(opcode, ((word >>> 17) & 0xffL).toInt, src))
        case "v_frexp_exp_i32_f32" =>
          Right(DecodedInstruction.VConvert(opcode, ((word >>> 17) & 0xffL).toInt, src))
        case "v_frexp_mant_f32" =>
          Right(DecodedInstruction.VConvert(opcode, ((word >>> 17) & 0xffL).toInt, src))
        case "v_cvt_f32_i32" | "v_cvt_f32_u32" | "v_cvt_f32_f64" | "v_cvt_f64_i32" | "v_cvt_f64_u32" | "v_cvt_f64_f32" | "v_cvt_i32_f32" | "v_cvt_u32_f32" | "v_cvt_u32_f64" |
            "v_cvt_f32_ubyte0" | "v_cvt_f32_ubyte1" | "v_cvt_f32_ubyte2" | "v_cvt_f32_ubyte3" | "v_cvt_i32_f64" |
            "v_rcp_iflag_f32" | "v_rcp_f32" | "v_rcp_legacy_f32" | "v_rcp_f64" | "v_rsq_f32" | "v_rsq_f64" | "v_sqrt_f32" | "v_exp_f32" | "v_sin_f32" | "v_cos_f32" |
            "v_trunc_f32" | "v_floor_f32" | "v_rndne_f32" | "v_trunc_f64" | "v_floor_f64" | "v_fract_f64" | "v_rndne_f64" =>
          Right(DecodedInstruction.VConvert(opcode, ((word >>> 17) & 0xffL).toInt, src))
        case "v_cvt_f32_f16" | "v_cvt_f16_f32" | "v_cvt_i16_f16" =>
          Right(DecodedInstruction.VConvert(opcode, ((word >>> 17) & 0xffL).toInt, src))
        case "v_log_f32" | "v_log_legacy_f32" | "v_frexp_mant_f64" =>
          Right(DecodedInstruction.VConvert(opcode, ((word >>> 17) & 0xffL).toInt, src))
        case "v_readfirstlane_b32" =>
          Right(DecodedInstruction.VReadFirstLane(((word >>> 17) & 0x7fL).toInt, src))
        case other => Left(s"$other VOP1 execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral || hasDPP || hasSDWA) 8 else 4)
  }

  private def decodeVOPC(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = (word & 0x1ffL).toInt
    val hasLiteral   = srcAEncoding == 255
    val hasSDWA      = srcAEncoding == 249
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral || hasSDWA) 8 else 4)
      opcode <- opcodeName("VOPC", ((word >>> 17) & 0xffL).toInt, target)
      extension = Option.when(hasSDWA)(unsignedInt(bytes, offset + 4))
      srcA <- extension match {
        case Some(sdwa) if (sdwa & (1L << 23)) != 0L => Right(InstructionSource.Scalar((sdwa & 0xffL).toInt))
        case Some(sdwa)                              => Right(InstructionSource.VectorRegister((sdwa & 0xffL).toInt))
        case None                                    => vectorSource(
            srcAEncoding,
            Option.when(hasLiteral)(unsignedInt(bytes, offset + 4))
          )
      }
      // In VOPC, SRC1 is always a VGPR in the normal encoding.  Unlike
      // SRC0, its 8-bit field does not use the scalar/constant source table.
      srcB = extension match {
        case Some(sdwa) if (sdwa & 0x80000000L) != 0L => InstructionSource.Scalar(((word >>> 9) & 0xffL).toInt)
        case _                                        => InstructionSource.VectorRegister(((word >>> 9) & 0xffL).toInt)
      }
      destination = extension.flatMap(sdwa => Option.when((sdwa & (1L << 15)) != 0L)(((sdwa >>> 8) & 0x7fL).toInt))
      _ <- Either.cond(
        SupportedVCompareOpcodes.contains(opcode.replace("v_cmpx_", "v_cmp_")),
        (),
        s"$opcode VOPC execution is not implemented"
      )
      operation <- if (hasSDWA) {
        for {
          sdwa          <- extension.toRight("missing SDWA extension word")
          srcASelection <- subDwordSelection(((sdwa >>> 16) & 0x7L).toInt)
          srcBSelection <- subDwordSelection(((sdwa >>> 24) & 0x7L).toInt)
          _             <- Either.cond(
            (sdwa & 0x00300000L) == 0L,
            (),
            s"$opcode SDWA source modifiers are not implemented"
          )
          _ <- Either.cond(
            SupportedVCompareSDWAOpcodes.contains(opcode),
            (),
            s"$opcode SDWA execution is not implemented"
          )
        } yield DecodedInstruction.VCompareSubDword(
          opcode,
          srcA,
          srcB,
          srcASelection,
          srcBSelection,
          (sdwa & (1L << 19)) != 0L,
          (sdwa & (1L << 27)) != 0L,
          destination
        )
      } else Right(DecodedInstruction.VCompare(opcode, srcA, srcB))
    } yield Decoded(operation, if (hasLiteral || hasSDWA) 8 else 4)
  }

  private def decodeVOP3(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      opcodeId = ((word >>> 16) & 0x3ffL).toInt
      // GFX9 VOP3-encoded compares reuse the VOPC opcode space, but the
      // generated metadata only lists their compact VOPC form.
      opcode <- opcodeName("VOP3", opcodeId, target).orElse(opcodeName("VOPC", opcodeId, target))
      second       = unsignedInt(bytes, offset + 4)
      srcAEncoding = (second & 0x1ffL).toInt
      srcBEncoding = ((second >>> 9) & 0x1ffL).toInt
      srcCEncoding = ((second >>> 18) & 0x1ffL).toInt
      hasLiteral   = srcAEncoding == 255 || srcBEncoding == 255 || srcCEncoding == 255
      _ <- requireBytes(bytes, offset, if (hasLiteral) 12 else 8)
      literal = Option.when(hasLiteral)(unsignedInt(bytes, offset + 8))
      srcA <- vectorSource(srcAEncoding, literal).leftMap(error =>
        s"$error in VOP3 word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcB <- vectorSource(srcBEncoding, literal).leftMap(error =>
        s"$error in VOP3 word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcC <- vectorSource(srcCEncoding, literal).leftMap(error =>
        s"$error in VOP3 word 0x${word.toHexString}/0x${second.toHexString}"
      )
      _ <- Either.cond(
        (second >>> 27) == 0L || opcode.startsWith("v_cmp_") || opcode.startsWith("v_cmpx_") ||
          SupportedVOP3SourceModifierOpcodes.contains(opcode),
        (),
        s"$opcode source modifiers are not implemented"
      )
      operation <- opcode match {
        case "v_lshlrev_b64" | "v_lshlrev_b64_e64" =>
          Right(DecodedInstruction.VShiftLeft64((word & 0xffL).toInt, srcA, srcB))
        case "v_lshrrev_b64" | "v_lshrrev_b64_e64" =>
          Right(DecodedInstruction.VShiftRight64((word & 0xffL).toInt, srcA, srcB))
        case "v_ashrrev_i64" | "v_ashrrev_i64_e64" =>
          Right(DecodedInstruction.VShiftRightArithmetic64((word & 0xffL).toInt, srcA, srcB))
        case "v_rcp_f32" =>
          Right(DecodedInstruction.VUnaryF32Modified(
            opcode,
            (word & 0xffL).toInt,
            srcA,
            (word & 0x100L) != 0L,
            (second & (1L << 29)) != 0L,
            (word & (1L << 15)) != 0L
          ))
        case "v_frexp_mant_f64" =>
          Right(DecodedInstruction.VUnaryF64Modified(
            opcode,
            (word & 0xffL).toInt,
            srcA,
            (word & 0x100L) != 0L,
            (second & (1L << 29)) != 0L
          ))
        case "v_trig_preop_f64" =>
          Right(DecodedInstruction.VTrigPreopF64(
            (word & 0xffL).toInt,
            srcA,
            srcB,
            (word & 0x100L) != 0L,
            (second & (1L << 29)) != 0L
          ))
        case "v_fma_f64" | "v_fma_f64_e64" =>
          Right(DecodedInstruction.VMadF64(
            (word & 0xffL).toInt,
            srcA,
            srcB,
            srcC,
            (word & 0x100L) != 0L,
            (word & 0x200L) != 0L,
            (word & 0x400L) != 0L,
            (second & (1L << 29)) != 0L,
            (second & (1L << 30)) != 0L,
            (second & (1L << 31)) != 0L
          ))
        case "v_div_fmas_f64" | "v_div_fmas_f64_e64" =>
          Right(DecodedInstruction.VDivFmasF64((word & 0xffL).toInt, srcA, srcB, srcC))
        case "v_div_fixup_f64" | "v_div_fixup_f64_e64" =>
          Right(DecodedInstruction.VDivFixupF64(
            (word & 0xffL).toInt,
            srcA,
            srcB,
            srcC,
            ((word >>> 8) & 0x7L).toInt,
            ((second >>> 29) & 0x7L).toInt
          ))
        case "v_div_scale_f64" | "v_div_scale_f64_e64" =>
          Right(DecodedInstruction.VDivScaleF64(
            (word & 0xffL).toInt,
            ((word >>> 8) & 0x7fL).toInt,
            srcA,
            srcB,
            srcC
          ))
        case "v_ldexp_f64" =>
          Right(DecodedInstruction.VBinary64Modified(
            opcode,
            (word & 0xffL).toInt,
            srcA,
            srcB,
            (word & 0x100L) != 0L,
            (word & 0x200L) != 0L,
            (second & (1L << 29)) != 0L,
            (second & (1L << 30)) != 0L
          ))
        case "v_mul_f64" | "v_mul_f64_e64" =>
          Right(DecodedInstruction.VBinary64Modified(
            opcode,
            (word & 0xffL).toInt,
            srcA,
            srcB,
            (word & 0x100L) != 0L,
            (word & 0x200L) != 0L,
            (second & (1L << 29)) != 0L,
            (second & (1L << 30)) != 0L
          ))
        case "v_max_f64" | "v_max_f64_e64" | "v_min_f64" | "v_min_f64_e64" =>
          Right(DecodedInstruction.VBinary64Modified(
            opcode,
            (word & 0xffL).toInt,
            srcA,
            srcB,
            (word & 0x100L) != 0L,
            (word & 0x200L) != 0L,
            (second & (1L << 29)) != 0L,
            (second & (1L << 30)) != 0L
          ))
        case "v_mbcnt_lo_u32_b32" | "v_mbcnt_lo_u32_b32_e64" =>
          Right(DecodedInstruction.VMaskBitCount((word & 0xffL).toInt, srcA, srcB, false))
        case "v_mbcnt_hi_u32_b32" | "v_mbcnt_hi_u32_b32_e64" =>
          Right(DecodedInstruction.VMaskBitCount((word & 0xffL).toInt, srcA, srcB, true))
        case "v_readlane_b32" | "v_readlane_b32_e64" =>
          Right(DecodedInstruction.VReadLane((word & 0xffL).toInt, srcA, srcB))
        case "v_writelane_b32" | "v_writelane_b32_e64" =>
          Right(DecodedInstruction.VWriteLane((word & 0xffL).toInt, srcA, srcB))
        case "v_add_u32" | "v_add_nc_u32" | "v_add_nc_u16" | "v_sub_nc_u16" | "v_mul_lo_u16" | "v_ashrrev_i16" | "v_ashrrev_i32" | "v_lshlrev_b32" | "v_lshlrev_b32_e64" | "v_lshrrev_b32" | "v_lshlrev_b16" | "v_lshrrev_b16" | "v_mul_lo_u32" | "v_mul_hi_u32" | "v_mul_hi_i32" | "v_sub_u32" | "v_sub_nc_u32" | "v_subrev_u32" | "v_subrev_nc_u32" | "v_min_i16" | "v_min_u16" | "v_max_i16" | "v_max_u16" =>
          Right(DecodedInstruction.VBinary(
            opcode match {
              case "v_add_nc_u32"    => "v_add_u32"
              case "v_sub_nc_u32"    => "v_sub_u32"
              case "v_subrev_nc_u32" => "v_subrev_u32"
              case "v_add_nc_u16"    => "v_add_u16"
              case "v_sub_nc_u16"    => "v_sub_u16"
              case other             => other
            },
            (word & 0xffL).toInt,
            srcA,
            srcB,
            // VOP3's clamp modifier is encoded in DWORD0[15], not in the
            // source/modifier DWORD1.
            clamp = (word & (1L << 15)) != 0L
          ))
        case "v_cndmask_b32" | "v_cndmask_b32_e64" => srcC match {
            case InstructionSource.Scalar(_) =>
              Right(DecodedInstruction.VCndMask(
                (word & 0xffL).toInt,
                srcA,
                srcB,
                srcC,
                (word & 0x100L) != 0L,
                (word & 0x200L) != 0L,
                (second & (1L << 29)) != 0L,
                (second & (1L << 30)) != 0L
              ))
            case _ => Left(s"$opcode condition is not a scalar mask")
          }
        case "v_add_co_u32" =>
          Right(DecodedInstruction.VAddCarry((word & 0xffL).toInt, ((word >>> 8) & 0x7fL).toInt, srcA, srcB, None))
        case "v_add_co_ci_u32" =>
          Right(DecodedInstruction.VAddCarry(
            (word & 0xffL).toInt,
            ((word >>> 8) & 0x7fL).toInt,
            srcA,
            srcB,
            Some(srcC)
          ))
        case "v_addc_co_u32" =>
          Right(DecodedInstruction.VAddCarry(
            (word & 0xffL).toInt,
            ((word >>> 8) & 0x7fL).toInt,
            srcA,
            srcB,
            Some(srcC)
          ))
        case "v_sub_co_u32" =>
          Right(DecodedInstruction.VSubCarry((word & 0xffL).toInt, ((word >>> 8) & 0x7fL).toInt, srcA, srcB, None))
        case "v_subb_co_u32" =>
          Right(DecodedInstruction.VSubCarry(
            (word & 0xffL).toInt,
            ((word >>> 8) & 0x7fL).toInt,
            srcA,
            srcB,
            Some(srcC)
          ))
        case "v_sub_co_ci_u32" =>
          Right(DecodedInstruction.VSubCarry(
            (word & 0xffL).toInt,
            ((word >>> 8) & 0x7fL).toInt,
            srcA,
            srcB,
            Some(srcC)
          ))
        case "v_subrev_co_u32" =>
          Right(DecodedInstruction.VSubReverseCarry(
            (word & 0xffL).toInt,
            ((word >>> 8) & 0x7fL).toInt,
            srcA,
            srcB,
            None
          ))
        case "v_subbrev_co_u32" | "v_subrev_co_ci_u32" =>
          Right(DecodedInstruction.VSubReverseCarry(
            (word & 0xffL).toInt,
            ((word >>> 8) & 0x7fL).toInt,
            srcA,
            srcB,
            Some(srcC)
          ))
        case "v_mad_f32" | "v_fma_f32" =>
          Right(DecodedInstruction.VMadF32(
            opcode,
            (word & 0xffL).toInt,
            srcA,
            srcB,
            srcC,
            (word & 0x100L) != 0L,
            (word & 0x200L) != 0L,
            (word & 0x400L) != 0L,
            (second & (1L << 29)) != 0L,
            (second & (1L << 30)) != 0L,
            (second & (1L << 31)) != 0L,
            (word & (1L << 15)) != 0L
          ))
        case "v_fmac_f32" =>
          // FMAC is the accumulator form: the implicit addend is the
          // destination VGPR, which the VBinary execution path preserves.
          Right(DecodedInstruction.VBinary("v_mac_f32", (word & 0xffL).toInt, srcA, srcB, clamp = false))
        case "v_div_fixup_f32" =>
          Right(DecodedInstruction.VDivFixupF32(
            (word & 0xffL).toInt,
            srcA,
            srcB,
            srcC,
            ((word >>> 8) & 0x7L).toInt,
            ((second >>> 29) & 0x7L).toInt
          ))
        case "v_bcnt_u32_b32" | "v_bcnt_u32_b32_e64" =>
          Right(DecodedInstruction.VBitCount((word & 0xffL).toInt, srcA, srcB))
        case "v_pk_fma_f16" =>
          Right(DecodedInstruction.VPackedFmaF16(
            (word & 0xffL).toInt,
            srcA,
            srcB,
            srcC,
            ((word >>> 11) & 0x7L).toInt,
            (((second >>> 27) & 0x3L) | ((word >>> 12) & 0x4L)).toInt,
            ((second >>> 29) & 0x7L).toInt,
            ((word >>> 8) & 0x7L).toInt,
            (word & (1L << 15)) != 0L
          ))
        case "v_add3_u32" | "v_or3_b32" | "v_xor3_b32" | "v_fma_f16" | "v_div_fmas_f32" | "v_mad_legacy_u16" | "v_mad_u16" | "v_mad_u32_u24" | "v_alignbit_b32" | "v_bfi_b32" | "v_med3_i32" | "v_min3_f32" |
            "v_max3_i16" | "v_max3_u16" | "v_min3_i16" | "v_min3_u16" |
            "v_max3_i32" | "v_max3_u32" | "v_min3_i32" | "v_min3_u32" |
            "v_and_or_b32" | "v_bfe_i32" | "v_bfe_u32" | "v_xad_u32" | "v_mad_i32_i24" | "v_add_lshl_u32" | "v_lshl_add_u32" | "v_lshl_or_b32" | "v_perm_b32" =>
          Right(DecodedInstruction.VTernary(opcode, (word & 0xffL).toInt, srcA, srcB, srcC))
        case "v_pack_b32_f16" =>
          Right(DecodedInstruction.VPackB32F16((word & 0xffL).toInt, srcA, srcB))
        case packed @ ("v_pk_add_u16" | "v_pk_add_f16" | "v_pk_mul_lo_u16" | "v_pk_mul_f16" | "v_pk_max_f16" | "v_pk_min_f16" | "v_pk_sub_i16" | "v_pk_lshrrev_b16") =>
          Either.cond(
            (second >>> 29) == 0L,
            DecodedInstruction.VPackedBinary(
              packed,
              (word & 0xffL).toInt,
              srcA,
              srcB,
              (second & (1L << 27)) != 0L,
              (second & (1L << 28)) != 0L
            ),
            s"$packed packed modifiers are not implemented"
          )
        case "v_add_f64" | "v_add_f64_e64" =>
          Right(DecodedInstruction.VBinary64Modified(
            opcode,
            (word & 0xffL).toInt,
            srcA,
            srcB,
            (word & 0x100L) != 0L,
            (word & 0x200L) != 0L,
            (second & (1L << 29)) != 0L,
            (second & (1L << 30)) != 0L
          ))
        case "v_mad_u64_u32" =>
          Right(DecodedInstruction.VMadU64U32((word & 0xffL).toInt, ((word >>> 8) & 0x7fL).toInt, srcA, srcB, srcC))
        case "v_mad_i64_i32" =>
          Right(DecodedInstruction.VMadI64I32((word & 0xffL).toInt, ((word >>> 8) & 0x7fL).toInt, srcA, srcB, srcC))
        case "v_div_scale_f32" =>
          Right(DecodedInstruction.VDivScaleF32(
            (word & 0xffL).toInt,
            ((word >>> 8) & 0x7fL).toInt,
            srcA,
            srcB,
            srcC
          ))
        case "v_add_f32" | "v_sub_f32" | "v_subrev_f32" | "v_mul_f32" | "v_ldexp_f32" | "v_max_f32" | "v_min_f32" =>
          Right(DecodedInstruction.VBinaryF32Modified(
            opcode,
            (word & 0xffL).toInt,
            srcA,
            srcB,
            (word & 0x100L) != 0L,
            (word & 0x200L) != 0L,
            (second & (1L << 29)) != 0L,
            (second & (1L << 30)) != 0L,
            (word & (1L << 15)) != 0L
          ))
        case compare
            if SupportedVCompareOpcodes.contains(compare.replace("v_cmpx_", "v_cmp_")) =>
          Right(DecodedInstruction.VCompareTo(
            (word & 0x7fL).toInt,
            compare,
            srcA,
            srcB,
            (word & 0x100L) != 0L,
            (word & 0x200L) != 0L,
            (second & (1L << 29)) != 0L,
            (second & (1L << 30)) != 0L
          ))
        case other => Left(s"$other VOP3 execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral) 12 else 8)

  /** Decode the packed 16-bit VOP3P encoding used by RDNA instructions such as `v_pk_lshrrev_b16`. VOP3P has the same
    * two-dword source layout as VOP3, but its opcode is selected from the packed-vector opcode space and the packed
    * lane selectors live in DWORD1 bits 27--28.
    */
  private def decodeVOP3P(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val opcodeId = ((word >>> 16) & 0x3ffL).toInt
    for {
      _ <- requireBytes(bytes, offset, 8)
      second       = unsignedInt(bytes, offset + 4)
      srcAEncoding = (second & 0x1ffL).toInt
      srcBEncoding = ((second >>> 9) & 0x1ffL).toInt
      srcCEncoding = ((second >>> 18) & 0x1ffL).toInt
      hasLiteral   = srcAEncoding == 255 || srcBEncoding == 255 || srcCEncoding == 255
      _      <- requireBytes(bytes, offset, if (hasLiteral) 12 else 8)
      opcode <- opcodeName("VOP3P", opcodeId, target)
        .orElse(opcodeName("VOP3", opcodeId, target))
      literal = Option.when(hasLiteral)(unsignedInt(bytes, offset + 8))
      srcA <- vectorSource(srcAEncoding, literal).leftMap(error =>
        s"$error in VOP3P word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcB <- vectorSource(srcBEncoding, literal).leftMap(error =>
        s"$error in VOP3P word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcC <- vectorSource(srcCEncoding, literal).leftMap(error =>
        s"$error in VOP3P word 0x${word.toHexString}/0x${second.toHexString}"
      )
      operation <- opcode match {
        case "v_pk_fma_f16" =>
          Right(DecodedInstruction.VPackedFmaF16(
            (word & 0xffL).toInt,
            srcA,
            srcB,
            srcC,
            ((word >>> 11) & 0x7L).toInt,
            (((second >>> 27) & 0x3L) | ((word >>> 12) & 0x4L)).toInt,
            ((second >>> 29) & 0x7L).toInt,
            ((word >>> 8) & 0x7L).toInt,
            (word & (1L << 15)) != 0L
          ))
        case "v_fma_mixlo_f16" | "v_fma_mixhi_f16" =>
          Right(DecodedInstruction.VFmaMixF16(
            (word & 0xffL).toInt,
            srcA,
            srcB,
            srcC,
            ((word >>> 11) & 0x7L).toInt,
            (((second >>> 27) & 0x3L) | ((word >>> 12) & 0x4L)).toInt,
            ((second >>> 29) & 0x7L).toInt,
            ((word >>> 8) & 0x7L).toInt,
            (word & (1L << 15)) != 0L,
            opcode == "v_fma_mixhi_f16"
          ))
        case "v_fma_mix_f32" =>
          Right(DecodedInstruction.VFmaMixF32(
            (word & 0xffL).toInt,
            srcA,
            srcB,
            srcC,
            ((word >>> 11) & 0x7L).toInt,
            (((second >>> 27) & 0x3L) | ((word >>> 12) & 0x4L)).toInt,
            ((second >>> 29) & 0x7L).toInt,
            ((word >>> 8) & 0x7L).toInt,
            (word & (1L << 15)) != 0L
          ))
        case "v_dot4_u32_u8" =>
          Right(DecodedInstruction.VDot4U32U8(
            (word & 0xffL).toInt,
            srcA,
            srcB,
            srcC
          ))
        case packed @ (
              "v_pk_add_u16" | "v_pk_add_f16" | "v_pk_mul_lo_u16" | "v_pk_mul_f16" |
              "v_pk_max_f16" | "v_pk_min_f16" | "v_pk_max_i16" | "v_pk_max_u16" | "v_pk_sub_i16" |
              "v_pk_lshrrev_b16"
            ) =>
          Either.cond(
            (second >>> 29) == 0L,
            DecodedInstruction.VPackedBinary(
              packed,
              (word & 0xffL).toInt,
              srcA,
              srcB,
              (second & (1L << 27)) != 0L,
              (second & (1L << 28)) != 0L
            ),
            s"$packed packed modifiers are not implemented"
          )
        case other => Left(s"$other VOP3P execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral) 12 else 8)
  }

  private def decodeDS(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _ <- requireBytes(bytes, offset, 8)
      // GCN/CDNA place GDS at bit 16 and OP at 17:24. RDNA moved both
      // fields one bit upward while retaining the two eight-bit offsets.
      opcodeShift = target.family match {
        case Family.GFX10 | Family.GFX11 | Family.GFX12 => 18
        case _                                          => 17
      }
      rawOpcode = ((word >>> opcodeShift) & 0xffL).toInt
      opcode <- opcodeName("DS", rawOpcode, target).leftMap(_ =>
        s"unknown DS opcode $rawOpcode for ${target.name} word=0x${word.toHexString}"
      )
      _ <- Either.cond((word & (1L << (opcodeShift - 1))) == 0L, (), s"$opcode GDS execution is not implemented")
      second   = unsignedInt(bytes, offset + 4)
      address  = (second & 0xffL).toInt
      dataA    = ((second >>> 8) & 0xffL).toInt
      dataB    = ((second >>> 16) & 0xffL).toInt
      dest     = ((second >>> 24) & 0xffL).toInt
      offsetA  = (word & 0xffL).toInt
      offsetB  = ((word >>> 8) & 0xffL).toInt
      dsOffset = singleDSOffset(word)
      operation <- opcode match {
        case "ds_permute_b32"  => Right(DecodedInstruction.DSPermute(dest, address, dataA, dsOffset))
        case "ds_bpermute_b32" => Right(DecodedInstruction.DSBPermute(dest, address, dataA, dsOffset))
        // DS_SWIZZLE uses the address field as its source VGPR.  Unlike the
        // ordinary DS read/write forms, the data field is reserved and is
        // encoded as zero by clang/LLVM.
        case "ds_swizzle_b32" => Right(DecodedInstruction.DSSwizzle(dest, address, dsOffset))
        case atomic @ (
              "ds_add_u32" | "ds_sub_u32" | "ds_inc_u32" | "ds_dec_u32" |
              "ds_min_i32" | "ds_max_i32" | "ds_min_u32" | "ds_max_u32" |
              "ds_and_rtn_b32" | "ds_or_rtn_b32" | "ds_xor_rtn_b32" |
              "ds_and_rtn_b64" | "ds_or_rtn_b64" | "ds_xor_rtn_b64" |
              "ds_add_rtn_u32" | "ds_add_rtn_u64" | "ds_sub_rtn_u32" | "ds_sub_rtn_u64" |
              "ds_inc_rtn_u32" | "ds_inc_rtn_u64" | "ds_dec_rtn_u32" | "ds_dec_rtn_u64" |
              "ds_min_rtn_i32" | "ds_min_rtn_i64" | "ds_min_rtn_u32" | "ds_min_rtn_u64" |
              "ds_max_rtn_i32" | "ds_max_rtn_i64" | "ds_max_rtn_u32" | "ds_max_rtn_u64" |
              "ds_add_rtn_f32" | "ds_add_rtn_f64" | "ds_min_rtn_f32" | "ds_min_rtn_f64" |
              "ds_max_rtn_f32" | "ds_max_rtn_f64"
            ) =>
          Right(DecodedInstruction.DSAtomic(
            atomic,
            dest,
            address,
            dataA,
            if (atomic.endsWith("64")) 8 else 4,
            dsOffset
          ))
        case "ds_write_b8"         => Right(DecodedInstruction.DSWrite(address, dataA, None, 1, dsOffset, None))
        case "ds_write_b16"        => Right(DecodedInstruction.DSWrite(address, dataA, None, 2, dsOffset, None))
        case "ds_write_b32"        => Right(DecodedInstruction.DSWrite(address, dataA, None, 4, dsOffset, None))
        case "ds_write_b64"        => Right(DecodedInstruction.DSWrite(address, dataA, None, 8, dsOffset, None))
        case "ds_write_b96"        => Right(DecodedInstruction.DSWrite(address, dataA, None, 12, dsOffset, None))
        case "ds_write_b128"       => Right(DecodedInstruction.DSWrite(address, dataA, None, 16, dsOffset, None))
        case "ds_write_b8_d16_hi"  => Right(DecodedInstruction.DSWriteD16HighByte(address, dataA, dsOffset))
        case "ds_write_b16_d16_hi" => Right(DecodedInstruction.DSWriteD16High(address, dataA, dsOffset))
        case "ds_write2_b32"       =>
          Right(DecodedInstruction.DSWrite(address, dataA, Some(dataB), 4, offsetA * 4, Some(offsetB * 4)))
        case "ds_write2_b64" =>
          Right(DecodedInstruction.DSWrite(address, dataA, Some(dataB), 8, offsetA * 8, Some(offsetB * 8)))
        case "ds_write2st64_b32" =>
          Right(DecodedInstruction.DSWrite(address, dataA, Some(dataB), 4, offsetA * 64 * 4, Some(offsetB * 64 * 4)))
        case "ds_write2st64_b64" =>
          Right(DecodedInstruction.DSWrite(address, dataA, Some(dataB), 8, offsetA * 64 * 8, Some(offsetB * 64 * 8)))
        case "ds_read_u8"  => Right(DecodedInstruction.DSRead(dest, address, 1, dsOffset, None))
        case "ds_read_u16" => Right(DecodedInstruction.DSRead(dest, address, 2, dsOffset, None))
        case "ds_read_i8"  => Right(DecodedInstruction.DSSignedRead(dest, address, 1, dsOffset, None))
        case "ds_read_i16" => Right(DecodedInstruction.DSSignedRead(dest, address, 2, dsOffset, None))
        case d16 @ ("ds_read_u8_d16" | "ds_read_u8_d16_hi" | "ds_read_u16_d16" | "ds_read_u16_d16_hi") =>
          Right(DecodedInstruction.DSReadD16(
            dest,
            address,
            if (d16.contains("u8")) 1 else 2,
            dsOffset,
            d16.endsWith("_hi")
          ))
        case d16 @ ("ds_read_i8_d16" | "ds_read_i8_d16_hi" | "ds_read_i16_d16" | "ds_read_i16_d16_hi") =>
          Right(DecodedInstruction.DSSignedReadD16(
            dest,
            address,
            if (d16.contains("i8")) 1 else 2,
            dsOffset,
            d16.endsWith("_hi")
          ))
        case "ds_read_b32"      => Right(DecodedInstruction.DSRead(dest, address, 4, dsOffset, None))
        case "ds_read_b96"      => Right(DecodedInstruction.DSRead(dest, address, 12, dsOffset, None))
        case "ds_read_b128"     => Right(DecodedInstruction.DSRead(dest, address, 16, dsOffset, None))
        case "ds_read_b64"      => Right(DecodedInstruction.DSRead(dest, address, 8, dsOffset, None))
        case "ds_read2_b32"     => Right(DecodedInstruction.DSRead(dest, address, 4, offsetA * 4, Some(offsetB * 4)))
        case "ds_read2_b64"     => Right(DecodedInstruction.DSRead(dest, address, 8, offsetA * 8, Some(offsetB * 8)))
        case "ds_read2st64_b32" =>
          Right(DecodedInstruction.DSRead(dest, address, 4, offsetA * 64 * 4, Some(offsetB * 64 * 4)))
        case "ds_read2st64_b64" =>
          Right(DecodedInstruction.DSRead(dest, address, 8, offsetA * 64 * 8, Some(offsetB * 64 * 8)))
        case other => Left(s"$other DS execution is not implemented")
      }
    } yield Decoded(operation, 8)

  private def decodeGlobalOrFlat(
      bytes: Array[Byte],
      offset: Int,
      rawWord: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _ <- requireBytes(bytes, offset, 8)
      // RDNA encodes the VMEM coherence/address-mode controls in the bit that
      // was the high offset bit on GFX9.  Keep the raw word for mode
      // selection, but clear that control bit before extracting the offset.
      word      = if (target.family == Family.GFX9) rawWord else rawWord & ~0x1000L
      rawOffset = word & (if (target.family == Family.GFX9) 0x1fffL else 0xfffL)
      // GFX10+ uses bit 12 for VMEM controls, leaving a signed 12-bit
      // displacement. Normalize negative values into the 13-bit form used
      // by the common execution path; GFX9 already carries signed 13 bits.
      memoryOffset = if (target.family == Family.GFX9) rawOffset.toInt
      else if ((rawOffset & 0x800L) != 0L) (rawOffset | 0x1000L).toInt
      else rawOffset.toInt
      second        = unsignedInt(bytes, offset + 4)
      scalarAddress = ((second >>> 16) & 0x7fL).toInt
      operation <- if ((word & 0x8000L) != 0L) {
        // GFX10 encodes the absent scalar address as 0x7d (the disassembler
        // prints it as `null`); older encodings use 0x7f.  Neither is an
        // actual SGPR pair for the flat/global operations we interpret.
        val scalar = Option.when(scalarAddress != 0x7d && scalarAddress != 0x7e && scalarAddress != 0x7f)(scalarAddress)
        opcodeName("GLOBAL", ((word >>> 18) & 0x7fL).toInt, target).flatMap {
          case "global_load_dword" =>
            Right(DecodedInstruction.GlobalLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              4,
              memoryOffset
            ))
          case "global_load_dwordx2" =>
            Right(DecodedInstruction.GlobalLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              8,
              memoryOffset
            ))
          case "global_load_dwordx3" =>
            Right(DecodedInstruction.GlobalLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              12,
              memoryOffset
            ))
          case "global_load_dwordx4" =>
            Right(DecodedInstruction.GlobalLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              16,
              memoryOffset
            ))
          case "global_load_ubyte" =>
            Right(DecodedInstruction.GlobalLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              1,
              memoryOffset
            ))
          case "global_load_sbyte" =>
            Right(DecodedInstruction.GlobalLoadSigned(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              1,
              memoryOffset
            ))
          case "global_load_sshort" =>
            Right(DecodedInstruction.GlobalLoadSigned(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              2,
              memoryOffset
            ))
          case d16 @ ("global_load_short_d16" | "global_load_short_d16_hi") =>
            Right(DecodedInstruction.GlobalLoadD16(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              memoryOffset,
              d16.endsWith("_hi")
            ))
          case "global_load_ushort" =>
            Right(DecodedInstruction.GlobalLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              2,
              memoryOffset
            ))
          case "global_store_dword" =>
            Right(DecodedInstruction.GlobalStore(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              4,
              memoryOffset
            ))
          case "global_store_dwordx2" =>
            Right(DecodedInstruction.GlobalStore(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              8,
              memoryOffset
            ))
          case "global_store_dwordx3" =>
            Right(DecodedInstruction.GlobalStore(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              12,
              memoryOffset
            ))
          case "global_store_dwordx4" =>
            Right(DecodedInstruction.GlobalStore(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              16,
              memoryOffset
            ))
          case "global_store_byte" =>
            Right(DecodedInstruction.GlobalStore(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              1,
              memoryOffset
            ))
          case "global_store_short" =>
            Right(DecodedInstruction.GlobalStore(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              2,
              memoryOffset
            ))
          case "global_atomic_add" =>
            Right(DecodedInstruction.GlobalAtomicAdd(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              4,
              memoryOffset,
              (word & 0x10000L) != 0L
            ))
          case "global_atomic_add_x2" =>
            Right(DecodedInstruction.GlobalAtomicAdd(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              8,
              memoryOffset,
              (word & 0x10000L) != 0L
            ))
          case "global_atomic_inc" =>
            Right(DecodedInstruction.GlobalAtomicInc(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              4,
              memoryOffset,
              (word & 0x10000L) != 0L
            ))
          case "global_atomic_inc_x2" =>
            Right(DecodedInstruction.GlobalAtomicInc(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              8,
              memoryOffset,
              (word & 0x10000L) != 0L
            ))
          case "global_atomic_cmpswap" | "global_atomic_cmpswap_b32" =>
            Right(DecodedInstruction.GlobalAtomicCompareSwap(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt + 1,
              ((second >>> 8) & 0xffL).toInt,
              4,
              memoryOffset,
              (word & 0x10000L) != 0L
            ))
          case "global_atomic_cmpswap_x2" =>
            Right(DecodedInstruction.GlobalAtomicCompareSwap(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt + 2,
              ((second >>> 8) & 0xffL).toInt,
              8,
              memoryOffset,
              (word & 0x10000L) != 0L
            ))
          case d16 @ ("global_store_byte_d16_hi" | "global_store_short_d16_hi") =>
            Right(DecodedInstruction.GlobalStoreD16High(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              if (d16.contains("byte")) 1 else 2,
              memoryOffset
            ))
          case other => Left(s"$other GLOBAL execution is not implemented")
        }
      } else if (scalarAddress == 0 || scalarAddress == 0x7d || scalarAddress == 0x7e || scalarAddress == 0x7f) {
        opcodeName("FLAT", ((word >>> 18) & 0x7fL).toInt, target).flatMap {
          case "flat_load_ubyte" =>
            Right(DecodedInstruction.FlatLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              1,
              memoryOffset
            ))
          case "flat_load_sbyte" =>
            Right(DecodedInstruction.FlatLoadSigned(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              1,
              memoryOffset
            ))
          case d16 @ ("flat_load_short_d16" | "flat_load_short_d16_hi") =>
            Right(DecodedInstruction.VFlatLoadD16(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              memoryOffset,
              d16.endsWith("_hi")
            ))
          case "flat_load_sshort" =>
            Right(DecodedInstruction.FlatLoadSigned(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              2,
              memoryOffset
            ))
          case "flat_load_ushort" =>
            Right(DecodedInstruction.FlatLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              2,
              memoryOffset
            ))
          case "flat_load_dword" =>
            Right(DecodedInstruction.FlatLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              4,
              memoryOffset
            ))
          case "flat_load_dwordx2" =>
            Right(DecodedInstruction.FlatLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              8,
              memoryOffset
            ))
          case "flat_load_dwordx3" =>
            Right(DecodedInstruction.FlatLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              12,
              memoryOffset
            ))
          case "flat_load_dwordx4" =>
            Right(DecodedInstruction.FlatLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              16,
              memoryOffset
            ))
          case "flat_store_short" =>
            Right(DecodedInstruction.FlatStore(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              2,
              memoryOffset
            ))
          case "flat_store_byte" =>
            Right(DecodedInstruction.FlatStore(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              1,
              memoryOffset
            ))
          case d16 @ ("flat_store_byte_d16_hi" | "flat_store_short_d16_hi") =>
            Right(DecodedInstruction.FlatStoreD16High(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              if (d16.contains("byte")) 1 else 2,
              memoryOffset
            ))
          case "flat_store_dword" =>
            Right(DecodedInstruction.FlatStore(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              4,
              memoryOffset
            ))
          case "flat_store_dwordx2" =>
            Right(DecodedInstruction.FlatStore(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              8,
              memoryOffset
            ))
          case "flat_store_dwordx3" =>
            Right(DecodedInstruction.FlatStore(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              12,
              memoryOffset
            ))
          case "flat_store_dwordx4" =>
            Right(DecodedInstruction.FlatStore(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              16,
              (word & 0x1fffL).toInt
            ))
          case "flat_atomic_cmpswap" | "flat_atomic_cmpswap_b32" =>
            Right(DecodedInstruction.GlobalAtomicCompareSwap(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              None,
              ((second >>> 8) & 0xffL).toInt + 1,
              ((second >>> 8) & 0xffL).toInt,
              4,
              (word & 0x1fffL).toInt,
              (word & 0x10000L) != 0L
            ))
          case "flat_atomic_cmpswap_x2" =>
            Right(DecodedInstruction.GlobalAtomicCompareSwap(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              None,
              ((second >>> 8) & 0xffL).toInt + 2,
              ((second >>> 8) & 0xffL).toInt,
              8,
              (word & 0x1fffL).toInt,
              (word & 0x10000L) != 0L
            ))
          case other => Left(s"$other FLAT execution is not implemented")
        }
      } else Left(s"VMEM scalar address s[$scalarAddress:${scalarAddress + 1}] is not implemented")
    } yield Decoded(operation, 8)

  private def decodeMUBUF(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _ <- requireBytes(bytes, offset, 8)
      second = unsignedInt(bytes, offset + 4)
      opcode <- opcodeName("MUBUF", ((word >>> 18) & 0x7fL).toInt, target)
      _      <- Either.cond(
        (word & 0x3f000L) == 0L || (word & 0x3f000L) == 0x1000L,
        (),
        s"$opcode buffer addressing/modifiers are not implemented"
      )
      address      = Option.when((word & 0x3000L) != 0L)((second & 0xffL).toInt)
      dest         = ((second >>> 8) & 0xffL).toInt
      src          = ((second >>> 8) & 0xffL).toInt
      resource     = ((second >>> 16) & 0x1fL).toInt * 4
      soffset      = ((second >>> 24) & 0xffL).toInt
      scalarOffset = Option.when(soffset != 0x80)(soffset)
      immediate    = (word & 0xfffL).toInt
      operation <- opcode match {
        case "buffer_wbinvl1" | "buffer_wbinvl1_sc" | "buffer_wbinvl1_vol" |
            "buffer_gl0_inv" | "buffer_gl1_inv" =>
          Right(DecodedInstruction.BufferCacheInvalidate(opcode))
        case "buffer_load_dword" =>
          Right(DecodedInstruction.BufferLoad(dest, address, resource, scalarOffset, 4, immediate))
        case "buffer_load_dwordx2" =>
          Right(DecodedInstruction.BufferLoad(dest, address, resource, scalarOffset, 8, immediate))
        case "buffer_load_dwordx4" =>
          Right(DecodedInstruction.BufferLoad(dest, address, resource, scalarOffset, 16, immediate))
        case "buffer_load_short_d16" =>
          Right(DecodedInstruction.BufferLoadD16(dest, address, resource, scalarOffset, 2, immediate, high = false))
        case "buffer_load_short_d16_hi" =>
          Right(DecodedInstruction.BufferLoadD16(dest, address, resource, scalarOffset, 2, immediate, high = true))
        case "buffer_store_short" =>
          Right(DecodedInstruction.BufferStore(address, resource, scalarOffset, src, 2, immediate))
        case "buffer_store_short_d16_hi" =>
          Right(DecodedInstruction.BufferStoreD16High(address, resource, scalarOffset, src, 2, immediate))
        case "buffer_store_dword" =>
          Right(DecodedInstruction.BufferStore(address, resource, scalarOffset, src, 4, immediate))
        case "buffer_store_dwordx2" =>
          Right(DecodedInstruction.BufferStore(address, resource, scalarOffset, src, 8, immediate))
        case "buffer_store_dwordx4" =>
          Right(DecodedInstruction.BufferStore(address, resource, scalarOffset, src, 16, immediate))
        case other => Left(s"$other MUBUF execution is not implemented")
      }
    } yield Decoded(operation, 8)

  private def opcodeName(format: String, opcode: Int, target: Target): Either[String, String] =
    resolveOpcodeName(format, opcode, target)

  private def resolveOpcodeName(format: String, opcode: Int, target: Target): Either[String, String] = {
    val names = OpcodeIndex.names(format, opcode, target)
    // RDNA's FLAT_GLBL encoding is shared by the flat and global mnemonics.
    // Once the decoder has selected the address mode, retain only the
    // corresponding mnemonic family; otherwise an otherwise valid opcode is
    // reported as ambiguous (for example flat_store_dword/global_store_dword).
    val modeNames = format match {
      case "GLOBAL" => names.filter(_.startsWith("global_"))
      case "FLAT"   => names.filter(_.startsWith("flat_"))
      case _        => names
    }
    modeNames match {
      case Vec(name)                                                 => Right(name)
      case Vec() if format == "VOP3" && target.family == Family.GFX9 =>
        Map(
          16  -> "v_cmp_class_f32",
          18  -> "v_cmp_class_f64",
          257 -> "v_add_f32",
          258 -> "v_sub_f32",
          259 -> "v_subrev_f32",
          260 -> "v_mul_f32",
          261 -> "v_mul_f32",
          71  -> "v_cmp_o_f32",
          72  -> "v_cmp_u_f32",
          202 -> "v_cmp_eq_u32",
          66  -> "v_cmp_eq_f32",
          67  -> "v_cmp_le_f32",
          98  -> "v_cmp_eq_f64",
          204 -> "v_cmp_gt_u32",
          205 -> "v_cmp_ne_u32",
          201 -> "v_cmp_lt_u32",
          225 -> "v_cmp_lt_i64",
          233 -> "v_cmp_lt_u64",
          235 -> "v_cmp_le_u64",
          238 -> "v_cmp_ge_u64",
          173 -> "v_cmp_ne_u16",
          174 -> "v_cmp_ge_u16",
          228 -> "v_cmp_gt_i64",
          109 -> "v_cmp_neq_f64",
          110 -> "v_cmp_nlt_f64",
          108 -> "v_cmp_nle_f64",
          77  -> "v_cmp_neq_f32",
          65  -> "v_cmp_lt_f32",
          68  -> "v_cmp_gt_f32",
          70  -> "v_cmp_ge_f32",
          97  -> "v_cmp_lt_f64",
          101 -> "v_cmp_lg_f64",
          105 -> "v_cmp_nge_f64",
          106 -> "v_cmp_nlg_f64",
          107 -> "v_cmp_ngt_f64",
          103 -> "v_cmp_o_f64",
          104 -> "v_cmp_u_f64",
          99  -> "v_cmp_le_f64",
          100 -> "v_cmp_gt_f64",
          75  -> "v_cmp_ngt_f32",
          76  -> "v_cmp_nle_f32",
          78  -> "v_cmp_nlt_f32",
          193 -> "v_cmp_lt_i32",
          196 -> "v_cmp_gt_i32",
          198 -> "v_cmp_ge_i32",
          161 -> "v_cmp_lt_i16",
          166 -> "v_cmp_ge_i16",
          163 -> "v_cmp_le_i16",
          164 -> "v_cmp_gt_i16",
          170 -> "v_cmp_eq_u16",
          169 -> "v_cmp_lt_u16",
          171 -> "v_cmp_le_u16",
          172 -> "v_cmp_gt_u16",
          236 -> "v_cmp_gt_u64",
          203 -> "v_cmp_le_u32",
          234 -> "v_cmp_eq_u64",
          230 -> "v_cmp_ge_i64",
          195 -> "v_cmp_le_i32",
          206 -> "v_cmp_ge_u32",
          227 -> "v_cmp_le_i64",
          237 -> "v_cmp_ne_u64",
          456 -> "v_bfe_u32",
          354 -> "v_rcp_f32",
          369 -> "v_frexp_mant_f64",
          642 -> "v_min_f64",
          643 -> "v_max_f64",
          266 -> "v_min_f32",
          267 -> "v_max_f32",
          309 -> "v_sub_u32",
          282 -> "v_sub_co_u32",
          283 -> "v_subrev_co_u32",
          284 -> "v_addc_co_u32",
          285 -> "v_subb_co_u32",
          286 -> "v_subbrev_co_u32",
          272 -> "v_lshrrev_b32",
          298 -> "v_lshlrev_b16",
          308 -> "v_add_u32",
          45  -> "v_cmp_neq_f16",
          34  -> "v_cmp_eq_f16",
          73  -> "v_cmp_nge_f32",
          102 -> "v_cmp_ge_f64",
          910 -> "v_pk_fma_f16",
          911 -> "v_pk_add_f16",
          912 -> "v_pk_mul_f16",
          914 -> "v_pk_max_f16",
          913 -> "v_pk_min_f16",
          256 -> "v_cndmask_b32_e64",
          274 -> "v_lshlrev_b32",
          281 -> "v_add_co_u32",
          284 -> "v_addc_co_u32",
          493 -> "v_perm_b32",
          509 -> "v_lshl_add_u32",
          510 -> "v_add_lshl_u32",
          512 -> "v_lshl_or_b32",
          651 -> "v_and_or_b32",
          458 -> "v_bfi_b32",
          897 -> "v_pk_mul_lo_u16",
          899 -> "v_pk_sub_i16",
          906 -> "v_pk_add_u16",
          640 -> "v_add_f64",
          658 -> "v_trig_preop_f64",
          901 -> "v_pk_lshrrev_b16"
        ).get(opcode).toRight(s"unknown $format opcode $opcode for ${target.name}")
      case Vec() => Left(s"unknown $format opcode $opcode for ${target.name}")
      case aliases if aliases.toSet == Set("v_mul_lo_i32", "v_mul_lo_u32") =>
        Right("v_mul_lo_u32")
      case _ => Left(s"ambiguous $format opcode $opcode for ${target.name}: ${names.mkString(", ")}")
    }
  }

  private def scalarSource(encoding: Int, literal: Option[Long]): Either[String, InstructionSource] =
    if (encoding <= 127) Right(InstructionSource.Scalar(encoding))
    // The virtual execution model has no hardware private-segment base. The
    // compiler still materializes the aperture registers while setting up the
    // scratch descriptor; their values are intentionally zero in the
    // interpreter's flat virtual address space.
    else if (encoding >= 235 && encoding <= 239) Right(InstructionSource.Constant(0L))
    else inlineConstant(encoding, literal)

  private def vectorSource(encoding: Int, literal: Option[Long]): Either[String, InstructionSource] =
    if (encoding >= 256) Right(InstructionSource.VectorRegister(encoding - 256))
    else scalarSource(encoding, literal)

  private def inlineConstant(encoding: Int, literal: Option[Long]): Either[String, InstructionSource] =
    if (encoding >= 128 && encoding <= 192) Right(InstructionSource.Constant(encoding - 128L))
    else if (encoding >= 193 && encoding <= 208) Right(InstructionSource.Constant(192L - encoding))
    else if (encoding >= 240 && encoding <= 248)
      Right(
        InstructionSource.Constant(
          Vec(
            0x3f000000L,
            0xbf000000L,
            0x3f800000L,
            0xbf800000L,
            0x40000000L,
            0xc0000000L,
            0x40800000L,
            0xc0800000L,
            0x3e22f983L
          )(encoding - 240)
        )
      )
    else if (encoding == 255) literal.map(raw => InstructionSource.Literal(raw)).toRight("missing literal dword")
    else Left(s"source encoding $encoding is not implemented")

  private def subDwordSelection(encoding: Int): Either[String, SubDwordSelection] =
    SubDwordSelection.values.lift(encoding).toRight(s"unknown SDWA selection $encoding")

  private def subDwordDestinationUnused(encoding: Int): Either[String, SubDwordDestinationUnused] =
    encoding match {
      case 0 => Right(SubDwordDestinationUnused.Pad)
      case 1 => Right(SubDwordDestinationUnused.SignExtend)
      case 2 => Right(SubDwordDestinationUnused.Preserve)
      case _ => Left(s"unknown SDWA destination-unused mode $encoding")
    }

  /** Validate once, before dispatch. Explicit native ranges must never shadow ELF or interpreter addresses. */
  private def validateAddressAliases(
      kernel: CodeObjectKernel,
      kernargSize: Int,
      addresses: Vec[AllocationAddress]
  ): Either[String, Unit] = {
    def overlaps(base: Long, size: Long, other: Long, otherSize: Long): Boolean =
      size > 0 && otherSize > 0 &&
        (if (base >= other) base - other < otherSize else other - base < size)
    val invalid = addresses.iterator.filter(_.address.nonEmpty).find { allocation =>
      val base = allocation.address.get.value
      allocation.size <= 0 || base <= 0 || base > Long.MaxValue - allocation.size ||
      overlaps(base, allocation.size, KernargAddress, kernargSize.toLong) ||
      overlaps(base, allocation.size, DispatchAddress, 64L) ||
      overlaps(base, allocation.size, kernel.readOnlyAddress, kernel.readOnly.length.toLong) ||
      overlaps(base, allocation.size, kernel.codeAddress, kernel.code.size.toLong) ||
      overlaps(base, allocation.size, kernel.textAddress, kernel.text.size.toLong) ||
      kernel.globalMemory.exists { case (address, bytes) =>
        overlaps(base, allocation.size, address, bytes.length.toLong)
      } ||
      addresses.exists { other =>
        val slot = other.allocation.value - 1L
        (slot <= (Long.MaxValue - AllocationBase) / AllocationSpan &&
          overlaps(base, allocation.size, AllocationBase + slot * AllocationSpan, AllocationSpan)) ||
        (other.allocation != allocation.allocation && other.address.exists(address =>
          overlaps(base, allocation.size, address.value, other.size)
        ))
      }
    }
    invalid.toLeft(()).left.map(allocation =>
      s"allocation ${allocation.allocation.value} address alias is invalid or overlaps an AMDGPU address range"
    )
  }

  // Kernarg packing assigns compact allocation regions and fails at the first invalid argument.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def packArguments(
      arguments: Vec[KernelArgument],
      kernargSize: Int,
      globalBindings: Vec[GlobalBinding],
      addresses: Vec[AllocationAddress]
  ): Either[String, (Array[Byte], Array[AllocationRegion])] = {
    val packed        = Array.ofDim[Byte](kernargSize)
    val regionStorage =
      if (arguments.isEmpty) EmptyAllocationRegions else Array.ofDim[AllocationRegion](arguments.length)
    var regionCount   = 0
    var packedOffset  = 0
    var argumentIndex = 0
    var failure       = Option.empty[String]
    while (argumentIndex < arguments.length && failure.isEmpty) {
      arguments(argumentIndex) match {
        case KernelArgument.Scalar(bytes) =>
          if (packedOffset == kernargSize) failure = Some(
            s"kernel descriptor requires $kernargSize kernarg bytes, received trailing argument $argumentIndex"
          )
          else if (packedOffset > kernargSize - bytes.length)
            failure = Some(s"kernel descriptor requires $kernargSize kernarg bytes, received more than $kernargSize")
          else {
            val _ = bytes.copyToArray(packed, packedOffset, bytes.length)
            packedOffset += bytes.length
          }
        case KernelArgument.Allocation(id, offset) =>
          if (packedOffset == kernargSize) failure = Some(
            s"kernel descriptor requires $kernargSize kernarg bytes, received trailing argument $argumentIndex"
          )
          else if (offset < 0L || offset >= AllocationSpan)
            failure = Some(s"allocation offset $offset is outside the interpreter address span")
          else if (packedOffset > kernargSize - 8)
            failure = Some(s"kernel descriptor requires $kernargSize kernarg bytes, received more than $kernargSize")
          else {
            var regionIndex = 0
            while (regionIndex < regionCount && regionStorage(regionIndex).id != id) regionIndex += 1
            if (regionIndex == regionCount) {
              // Device pointers can outlive a dispatch: kernels commonly
              // write a pointer into an allocation and consume it in a later
              // dispatch.  The virtual address therefore must be derived from
              // the stable allocation identity, rather than the position of
              // the allocation in this dispatch's argument list.
              val slot = id.value - 1L
              if (slot > (Long.MaxValue - AllocationBase) / AllocationSpan)
                failure = Some(s"allocation ID ${id.value} is outside the interpreter address span")
              else {
                regionStorage(regionIndex) = globalBindings.find(_.allocation == id) match {
                  case Some(binding) =>
                    AllocationRegion(
                      binding.address - binding.offset,
                      id,
                      0L,
                      (binding.allocationSize: Long).refineUnsafe[Positive0]
                    )
                  case None =>
                    addresses.find(_.allocation == id).flatMap(value => value.address.map(value -> _)) match {
                      case Some((allocation, address)) => AllocationRegion(
                          address.value,
                          id,
                          0L,
                          (allocation.size: Long).refineUnsafe[Positive0]
                        )
                      case None => AllocationRegion(AllocationBase + slot * AllocationSpan, id, 0L)
                    }
                }
                regionCount += 1
              }
            }
            if (failure.isEmpty) {
              LittleEndian.write(
                regionStorage(regionIndex).base + offset,
                packed,
                packedOffset,
                ScalarByteWidth(8)
              )
              packedOffset += 8
            }
          }
        case KernelArgument.Local(_) => failure = Some("gfx900 local kernargs are not implemented")
      }
      argumentIndex += 1
    }
    failure match {
      case Some(message)                       => Left(message)
      case None if packedOffset != kernargSize =>
        Left(s"kernel descriptor requires $kernargSize kernarg bytes, received $packedOffset")
      case None =>
        val allocations =
          if (regionCount == 0) EmptyAllocationRegions
          else if (regionCount == regionStorage.length) regionStorage
          else java.util.Arrays.copyOf(regionStorage, regionCount)
        if (AMDGPUEnvironment.trace("arguments"))
          scribe.debug(
            s"Gemstone AMDGPU arguments allocations=${allocations.map(_.id.value).toVec} args=${arguments.map {
                case KernelArgument.Allocation(id, offset) => s"alloc:${id.value}+$offset"
                case KernelArgument.Scalar(bytes)          => s"scalar:${bytes.length}"
                case KernelArgument.Local(size)            => s"local:$size"
              }}"
          )
        val bound = globalBindings.map(binding =>
          AllocationRegion(
            binding.address,
            binding.allocation,
            binding.offset,
            binding.size.toLong.refineUnsafe[Positive0],
            alias = true
          )
        ) ++ addresses.flatMap(allocation =>
          allocation.address.map(address =>
            AllocationRegion(
              address.value,
              allocation.allocation,
              0L,
              (allocation.size: Long).refineUnsafe[Positive0],
              alias = true
            )
          )
        )
        Right(packed -> (if (bound.isEmpty) allocations else Vec.toArray(bound) ++ allocations))
    }
  }

  // Workgroups advance through the shared bounded scheduler without materializing a result collection.
  private def runDispatch(
      kernel: CodeObjectKernel,
      operations: Vec[DecodedInstruction],
      executable: Array[ExecutableInstruction],
      instructionIndices: Map[Int, Int],
      instructionOffsets: Vec[Int],
      entryInstruction: Int,
      kernarg: Array[Byte],
      allocations: Array[AllocationRegion],
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    for {
      localCount <- dispatch.geometry.groupSize.volumeInt
        .leftMap(detail => ExecutionFailure.InvalidRequest(detail))
      groupCount <- dispatch.geometry.groupCount.volume
        .leftMap(detail => ExecutionFailure.InvalidRequest(detail))
      _ <- {
        if (AMDGPUEnvironment.trace("dispatch"))
          scribe.debug(
            s"Gemstone AMDGPU dispatch requested=${dispatch.kernelName} loaded=${kernel.name} global=${dispatch.geometry.globalExtent} groups=${dispatch.geometry.groupCount} group=${dispatch.geometry.groupSize}"
          )
        val dispatchBytes = Array.ofDim[Byte](64)
        LittleEndian.write(dispatch.geometry.groupSize.x.toLong, dispatchBytes, 4, ScalarByteWidth(2))
        LittleEndian.write(dispatch.geometry.groupSize.y.toLong, dispatchBytes, 6, ScalarByteWidth(2))
        LittleEndian.write(dispatch.geometry.groupSize.z.toLong, dispatchBytes, 8, ScalarByteWidth(2))
        LittleEndian.write(dispatch.geometry.globalExtent.x.toLong, dispatchBytes, 12, ScalarByteWidth(4))
        LittleEndian.write(dispatch.geometry.globalExtent.y.toLong, dispatchBytes, 16, ScalarByteWidth(4))
        LittleEndian.write(dispatch.geometry.globalExtent.z.toLong, dispatchBytes, 20, ScalarByteWidth(4))
        LittleEndian.write(dispatch.privateSegmentBytes.toLong, dispatchBytes, 24, ScalarByteWidth(4))
        LittleEndian.write(dispatch.groupSegmentBytes.toLong, dispatchBytes, 28, ScalarByteWidth(4))
        def runGroupIndex(index: Long :| Positive0): Either[ExecutionFailure, Unit] =
          dispatch.geometry.groupCoordinates(index)
            .leftMap(detail => ExecutionFailure.InvalidRequest(detail))
            .fold(
              error => Left(error),
              group =>
                runGroup(
                  kernel,
                  operations,
                  executable,
                  instructionIndices,
                  instructionOffsets,
                  entryInstruction,
                  kernarg,
                  dispatchBytes,
                  allocations,
                  dispatch,
                  localCount,
                  group,
                  memory,
                  control
                )
            )
        val refinedGroupCount = (groupCount: Long).refineUnsafe[Positive0]
        val parallelGroups    = control.parallelism.shouldParallel(
          refinedGroupCount,
          localCount.toLong.refineUnsafe[Positive0]
        )
        val result  = control.parallelism.forEach(refinedGroupCount, parallel = parallelGroups)(runGroupIndex(_))
        val failure = result.left.toOption
        failure.foreach { error =>
          if (AMDGPUEnvironment.trace("dispatch"))
            scribe.debug(s"Gemstone AMDGPU dispatch failure kernel=${kernel.name}: ${error.message}")
        }
        failure.toLeft(())
      }
    } yield ()

  // Wave rendezvous is a measured mutable state machine; null marks absent wave state and the first failure.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null", "scalafix:GemstoneNoAssume"))
  private def runGroup(
      kernel: CodeObjectKernel,
      operations: Vec[DecodedInstruction],
      executable: Array[ExecutableInstruction],
      instructionIndices: Map[Int, Int],
      instructionOffsets: Vec[Int],
      entryInstruction: Int,
      kernarg: Array[Byte],
      dispatchBytes: Array[Byte],
      allocations: Array[AllocationRegion],
      dispatch: KernelDispatch,
      localCount: Int,
      group: Coordinates,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] = {
    val lds = Array.ofDim[Byte](effectiveGroupSegmentSize(kernel.groupSegmentSize, dispatch.groupSegmentBytes))
    // GFX6-9 code objects use wave64; RDNA and later objects normally select
    // wave32.  The wave width is part of the executable target, not a global
    // property of the interpreter, and controls both lane partitioning and
    // EXEC/VCC mask width.
    val waveSize: WaveSize = if (AMDGPUEnvironment.containsKey("GEMSTONE_AMDGPU_WAVE64")) WaveSize(64)
    else kernel.target.family match {
      case Family.GFX6 | Family.GFX7 | Family.GFX8 | Family.GFX9 | Family.CDNA1 | Family.CDNA2 | Family.CDNA3 | Family.CDNA4 |
          Family.CDNA5 => WaveSize(64)
      case _ => WaveSize(32)
    }
    if (AMDGPUEnvironment.trace("wave"))
      scribe.debug(
        s"Gemstone AMDGPU wave kernel=${kernel.name} target=${kernel.target} properties=${kernel.kernelCodeProperties} waveSize=$waveSize localCount=$localCount entry=$entryInstruction"
      )
    val waveCount = (localCount + waveSize.value - 1) / waveSize.value
    val states    = Array.ofDim[WaveState | Null](waveCount)
    // A wave can leave the kernel before another wave reaches a workgroup
    // barrier.  That is legal for barriers in divergent control flow when
    // the remaining waves eventually arrive at the same hardware barrier;
    // retain the completion state so an ended wave is not restarted on the
    // next barrier-release round.
    val finished                         = Array.fill(waveCount)(false)
    val maskGlobalBounds                 = AMDGPUEnvironment.containsKey("GEMSTONE_AMDGPU_INITIAL_EXEC_GLOBAL")
    var complete                         = false
    var failure: ExecutionFailure | Null = null
    while (!complete && failure == null) {
      var waiting   = 0
      var live      = 0
      var waveIndex = 0
      while (waveIndex < waveCount && failure == null) {
        if (!finished(waveIndex)) {
          live += 1
          val start = waveIndex * waveSize.value
          // The enclosing wave loop guarantees start < localCount; waveSize is 32 or 64.
          val lanes         = LaneCount.assume(math.min(waveSize.value, localCount - start))
          val initialActive =
            if (!maskGlobalBounds) Right(Bits.mask(lanes.bitWidth))
            else {
              var active                                     = 0L
              var lane                                       = 0
              var coordinateFailure: ExecutionFailure | Null = null
              while (lane < lanes.value && coordinateFailure == null) {
                dispatch.geometry.localCoordinates(start + lane) match {
                  case Left(detail) => coordinateFailure = ExecutionFailure.InvalidRequest(detail)
                  case Right(local) =>
                    dispatch.geometry.globalCoordinatesLong(group, local) match {
                      case Left(detail)  => coordinateFailure = ExecutionFailure.InvalidRequest(detail)
                      case Right(global) =>
                        if (dispatch.geometry.containsGlobal(global)) active |= 1L << lane
                    }
                }
                lane += 1
              }
              if (coordinateFailure == null) Right(active) else Left(coordinateFailure)
            }
          initialActive match {
            case Left(error)       => failure = error
            case Right(activeMask) =>
              runWave(
                kernel,
                operations,
                executable,
                instructionIndices,
                instructionOffsets,
                entryInstruction,
                kernarg,
                dispatchBytes,
                allocations,
                dispatch.geometry,
                group,
                start,
                lanes,
                waveSize,
                lds,
                activeMask,
                states(waveIndex),
                memory,
                control
              ) match {
                case Left(error)                        => failure = error
                case Right(WaveBoundary.Barrier(state)) =>
                  states(waveIndex) = state
                  waiting += 1
                case Right(WaveBoundary.End) =>
                  states(waveIndex) = null
                  finished(waveIndex) = true
              }
          }
        }
        waveIndex += 1
      }
      if (failure == null)
        if (live == 0 || waiting == 0) complete = true
    }
    if (failure == null) Right(()) else Left(failure)
  }

  // Register, PC and barrier state are updated directly in the measured wave interpreter loop; null avoids allocating
  // checkpoint and optional-state wrappers for every instruction.
  @SuppressWarnings(Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.null",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneNoAssume"
  ))
  private def runWave(
      kernel: CodeObjectKernel,
      operations: Vec[DecodedInstruction],
      executable: Array[ExecutableInstruction],
      instructionIndices: Map[Int, Int],
      instructionOffsets: Vec[Int],
      entryInstruction: Int,
      kernarg: Array[Byte],
      dispatchBytes: Array[Byte],
      allocations: Array[AllocationRegion],
      geometry: LaunchGeometry,
      group: Coordinates,
      localStart: Int,
      lanes: LaneCount,
      waveSize: WaveSize,
      lds: Array[Byte],
      initialActive: Long,
      initial: WaveState | Null,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, WaveBoundary] = {
    val groupX          = group.x
    val groupY          = group.y
    val groupZ          = group.z
    val groupWidth      = geometry.groupSize.x
    val groupHeight     = geometry.groupSize.y
    val groupDepth      = geometry.groupSize.z
    val laneCoordinates = Array.ofDim[Coordinates](lanes.value)
    var coordinateLane  = 0
    while (coordinateLane < lanes.value) {
      // localStart + coordinateLane is a member of this wave and therefore within the validated group volume.
      geometry.localCoordinates(localStart + coordinateLane) match {
        case Left(detail) => return Left(ExecutionFailure.InvalidRequest(detail))
        case Right(local) => laneCoordinates(coordinateLane) = local
      }
      coordinateLane += 1
    }
    val scalar = initial match {
      case null  => Array.fill[Long](128)(0L)
      case state => state.scalar
    }
    val vector = initial match {
      case null  => VectorRegisters(lanes)
      case state => state.vector
    }
    val carry = initial match {
      // A vector comparison defines only the wave's lane bits of VCC. On a
      // wave32 target the compiler keeps scratch values in vcc_hi (s107), so
      // the upper half has to survive a comparison while scalar reads and
      // writes of s106/s107 still round-trip through the full register.
      case null  => LaneBits(lanes, 0L)
      case state => state.carry
    }
    val active = initial match {
      case null  => LaneBits(lanes, initialActive)
      case state => state.active
    }
    var scalarCarry = initial match {
      case null  => false
      case state => state.scalarCarry
    }
    val scratch = initial match {
      // The 4096-byte floor proves the per-lane allocation is positive.
      case null  => ScratchMemory(lanes, math.max(4096, kernel.privateSegmentSize))
      case state => state.scratch
    }
    val codeBase     = if (kernel.textAddress != 0L && kernel.text.nonEmpty) kernel.textAddress else kernel.codeAddress
    val traceF64     = AMDGPUEnvironment.trace("f64") && kernel.name.contains("basic_arithmetic")
    val traceControl = AMDGPUEnvironment.trace("control")
    val traceSetControl =
      AMDGPUEnvironment.trace("set-control") && kernel.name.contains("set_operations")
    val traceSetOps  = AMDGPUEnvironment.trace("set-ops") && kernel.name.contains("set_operations")
    val traceSetTail =
      AMDGPUEnvironment.trace("set-tail") && kernel.name.contains("set_operations")
    val traceLog      = AMDGPUEnvironment.trace("log") && kernel.name.contains("log_functor")
    val traceSqrt     = AMDGPUEnvironment.trace("sqrt") && kernel.name.contains("sqrt_functor")
    val traceLds      = AMDGPUEnvironment.trace("lds")
    val traceSetLds   = AMDGPUEnvironment.trace("set-lds") && kernel.name.contains("set_operations")
    val traceByteSort =
      AMDGPUEnvironment.trace("byte-sort") && kernel.name.contains("radix_sort_block_sort")
    val traceByteSortCompare =
      AMDGPUEnvironment.trace("byte-compare") && kernel.name.contains("radix_sort_block_sort")
    val traceByteSortRank =
      AMDGPUEnvironment.trace("byte-rank") && kernel.name.contains("radix_sort_block_sort")
    val traceMergeRank =
      AMDGPUEnvironment.trace("merge-rank") && kernel.name.contains("wrapped_merge_sort") &&
        localStart == 0 && lanes.value == waveSize.value
    var nextUserSgpr                           = 0
    def enabled(value: Int, bit: Int): Boolean = (value & (1 << bit)) != 0
    def reserve(count: Int): Int               = {
      val start = nextUserSgpr
      nextUserSgpr += count
      start
    }
    if (initial == null) {
      if (enabled(kernel.kernelCodeProperties, 0)) nextUserSgpr += 4
      if (enabled(kernel.kernelCodeProperties, 1)) setPair(scalar, reserve(2), DispatchAddress)
      if (enabled(kernel.kernelCodeProperties, 2)) setPair(scalar, reserve(2), 0L)
      if (enabled(kernel.kernelCodeProperties, 3)) setPair(scalar, reserve(2), KernargAddress)
      if (enabled(kernel.kernelCodeProperties, 4)) setPair(scalar, reserve(2), 0L)
      if (enabled(kernel.kernelCodeProperties, 5)) setPair(scalar, reserve(2), 0L)
      if (enabled(kernel.kernelCodeProperties, 6)) scalar(reserve(1)) = kernel.privateSegmentSize.toLong
      if (enabled(kernel.computePgmRsrc2, 7)) scalar(reserve(1)) = Bits.unsigned32(groupX)
      if (enabled(kernel.computePgmRsrc2, 8)) scalar(reserve(1)) = Bits.unsigned32(groupY)
      if (enabled(kernel.computePgmRsrc2, 9)) scalar(reserve(1)) = Bits.unsigned32(groupZ)
      if (enabled(kernel.computePgmRsrc2, 10)) scalar(reserve(1)) = 0L
      val enabledWorkItemIds = (kernel.computePgmRsrc2 >>> 11) & 0x3
      (0 until lanes.value).foreach { lane =>
        val local = laneCoordinates(lane)
        vector(0)(lane) = Bits.unsigned32(local.x)
        if (enabledWorkItemIds >= 1) vector(1)(lane) = Bits.unsigned32(local.y)
        if (enabledWorkItemIds >= 2) vector(2)(lane) = Bits.unsigned32(local.z)
      }
      if (traceControl && kernel.name.contains("wrapped_transform_config"))
        scribe.debug(
          s"Gemstone AMDGPU transform entry kernel=${kernel.name} properties=${kernel.kernelCodeProperties} rsrc2=${kernel.computePgmRsrc2} nextUserSgpr=$nextUserSgpr scalar=${scalar.take(16).mkString("[", ",", "]")} group=($groupX,$groupY,$groupZ) localStart=$localStart lanes=$lanes"
        )
      if (AMDGPUEnvironment.trace("wave"))
        scribe.debug(
          s"Gemstone AMDGPU wave entry kernel=${kernel.name} localStart=$localStart lanes=$lanes groupWidth=$groupWidth vector0=${(0 until math.min(4, lanes.value)).map(
              lane => vector(0)(lane)
            ).mkString(",")}"
        )
      if (
        AMDGPUEnvironment.trace("merge-entry") &&
        kernel.name.contains("wrapped_merge_sort") && localStart == 0
      )
        scribe.debug(
          s"Gemstone AMDGPU merge entry scalar=${scalar.take(32).mkString("[", ",", "]")} group=($groupX,$groupY,$groupZ) lanes=$lanes"
        )
    }

    def booleanLaneMask(flags: Array[Boolean]): Long = {
      var mask = 0L
      var lane = 0
      while (lane < flags.length) {
        if (flags(lane)) mask |= 1L << lane
        lane += 1
      }
      mask
    }

    def laneMask(flags: LaneBits): Long = flags.bits

    // Mask-producing VALU instructions write a complete wave mask.  Unlike
    // ordinary predicated VGPR writes, VOPC/VOP3 compare and carry results
    // clear bits for lanes that are inactive in EXEC.
    def updateLaneMask(destination: Int, result: Long): Unit =
      // VOP carry-producing instructions encode a discarded mask result as
      // the null SGPR (s[125:126]).  Do not read/write that pseudo-register:
      // s126 is the live EXEC mask in the emulator.
      if (destination != 125) {
        if (waveSize.value <= 32) {
          writeScalar32(destination, Bits.low32(result))
        } else {
          writeScalar64(destination, result)
        }
      }

    def saveExecSource(src: InstructionSource, width: Int): Long =
      if (width == 32) Bits.low32(sourceScalar64(src)) else sourceScalar64(src)

    def saveExecWrite(destination: Int, value: Long, width: Int): Unit =
      if (width == 32) writeScalar32(destination, value) else writeScalar64(destination, value)

    def source(value: InstructionSource, lane: Int): Long = value match {
      case InstructionSource.Scalar(106)           => Bits.low32(carry.register)
      case InstructionSource.Scalar(107)           => Bits.high32(carry.register)
      case InstructionSource.Scalar(126)           => Bits.low32(active.register)
      case InstructionSource.Scalar(127)           => Bits.high32(active.register)
      case InstructionSource.Scalar(index)         => scalar(index)
      case InstructionSource.VectorRegister(index) => vector(index)(lane)
      case InstructionSource.Constant(raw)         => raw
      case InstructionSource.Literal(raw)          => raw
    }

    def sourceScalar64(value: InstructionSource): Long = value match {
      case InstructionSource.Scalar(106)       => carry.register
      case InstructionSource.Scalar(107)       => Bits.high32(carry.register)
      case InstructionSource.Scalar(126)       => active.register
      case InstructionSource.Scalar(127)       => Bits.high32(active.register)
      case InstructionSource.Scalar(index)     => pair(scalar, index)
      case InstructionSource.Constant(raw)     => raw
      case InstructionSource.Literal(raw)      => raw
      case InstructionSource.VectorRegister(_) => 0L
    }

    def writeScalar64(dest: Int, value: Long): Unit = dest match {
      case 106 => carry.register = value
      case 126 => active.register = value
      case _   => setPair(scalar, dest, value)
    }

    def writeScalar32(dest: Int, value: Long): Unit = dest match {
      case 106 => carry.register = (carry.register & 0xffffffff00000000L) | Bits.low32(value)
      case 107 =>
        carry.register = (carry.register & 0xffffffffL) | (Bits.low32(value) << 32)
      case 126 => active.register = (active.register & 0xffffffff00000000L) | Bits.low32(value)
      case 127 => active.register = (active.register & 0xffffffffL) | (Bits.low32(value) << 32)
      case _   => scalar(dest) = Bits.low32(value)
    }

    def foreachLane(operation: Int => Unit): Unit = {
      var lane = 0
      while (lane < lanes.value) {
        operation(lane)
        lane += 1
      }
    }

    def foreachActive(operation: Int => Unit): Unit = {
      var lane = 0
      while (lane < lanes.value) {
        if (active(lane)) operation(lane)
        lane += 1
      }
    }

    def programCounter(index: Int): Long = codeBase + instructionOffsets(index).toLong

    def programIndex(address: Long): Option[Int] = {
      val relative = address - codeBase
      if (relative < 0L || relative > Int.MaxValue.toLong) None
      else instructionIndices.get(relative.toInt)
    }

    // Generic FLAT operations can target the workgroup's local segment as
    // well as global or private memory.  The AMDHSA compiler represents a
    // local pointer as a low 32-bit segment address in the generated helper
    // functions (the hardware applies the local aperture); model that
    // aperture explicitly before falling back to per-lane scratch or the
    // dispatch allocation map.
    def flatLoad(lane: Int, address: Long, size: Int): Either[ExecutionFailure, Vec[Byte]] =
      if (address >= 0L && address <= lds.length.toLong - size.toLong)
        readLDS(lds, address, size).map(Vec.unsafeFromArray)
      else if (address >= 0L && address <= scratch(lane).length.toLong - size.toLong)
        Right(Vec.unsafeFromArray(java.util.Arrays.copyOfRange(scratch(lane), address.toInt, address.toInt + size)))
      else
        readBytes(
          address,
          size,
          kernarg,
          dispatchBytes,
          kernel.readOnlyAddress,
          kernel.readOnly,
          kernel.globalMemory,
          allocations,
          memory
        )

    def flatStore(lane: Int, address: Long, bytes: Array[Byte]): Either[ExecutionFailure, Unit] =
      if (address >= 0L && address <= lds.length.toLong - bytes.length)
        writeLDS(lds, address, bytes)
      else if (address >= 0L && address <= scratch(lane).length.toLong - bytes.length) {
        Array.copy(bytes, 0, scratch(lane), address.toInt, bytes.length)
        Right(())
      } else writeBytes(address, bytes, allocations, memory, kernel.globalMemory)

    def isEmulatedCall(name: String): Boolean =
      name == "__ockl_get_group_id" || name == "__ockl_get_local_size" || name == "__ockl_get_local_id" ||
        EmulatedCallNameFragments.exists(name.contains(_))

    def executeBufferStore(
        index: Int,
        address: Option[Int],
        resource: Int,
        scalarOffset: Option[Int],
        src: Int,
        byteCount: Int :| Interval.Closed[1, 16],
        offset: Int,
        highByteCount: Option[ScalarByteWidth]
    ): Either[ExecutionFailure, Option[Int]] = {
      def storedBytes(lane: Int): Array[Byte] = highByteCount match {
        case Some(width) => LittleEndian.toArray(vector(src)(lane) >>> 16, width)
        case None        => vectorBytes(vector, src, lane, byteCount)
      }

      def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
        if (lane == lanes.value) Right(())
        else if (!active(lane)) storeLane(lane + 1)
        else {
          val start =
            address.map(register => Bits.low32(vector(register)(lane))).getOrElse(0L) + scalarOffset.fold(0L)(
              register => Bits.low32(scalar(register))
            ) + offset.toLong
          val base  = sourceScalar64(InstructionSource.Scalar(resource)) & 0x0000ffffffffffffL
          val range = Bits.low32(scalar(resource + 2))
          if (AMDGPUEnvironment.trace("all-stores") && lane < 4)
            scribe.debug(
              s"Gemstone AMDGPU buffer store index=$index kernel=${kernel.name} lane=$lane start=$start base=0x${base.toHexString} src=$src value=0x${vector(src)(lane).toHexString}"
            )
          if (base != 0L && (start < 0L || start > range - byteCount.toLong)) storeLane(lane + 1)
          else if (base != 0L)
            writeBytes(base + start, storedBytes(lane), allocations, memory, kernel.globalMemory)
              .flatMap(_ => storeLane(lane + 1))
          else if (start < 0L || start > scratch(lane).length.toLong - byteCount.toLong)
            Left(ExecutionFailure.Failed(
              s"buffer store ${operations(index)} at instruction $index lane $lane offset $start size $byteCount exceeds private scratch ${scratch(lane).length}"
            ))
          else {
            val bytes = storedBytes(lane)
            Array.copy(bytes, 0, scratch(lane), start.toInt, byteCount)
            storeLane(lane + 1)
          }
        }

      advanceAfter(storeLane(0))
    }

    // Keep the large decoded instruction switch out of the packed dispatcher: the JVM
    // otherwise interprets even packed operations because the method exceeds its JIT size limit.
    def stepDecoded(index: Int): Either[ExecutionFailure, Option[Int]] = operations(index) match {
      case DecodedInstruction.End =>
        if (traceControl)
          scribe.debug(s"Gemstone AMDGPU control end index=$index active=${laneMask(active)}")
        Right(None)
      case DecodedInstruction.Wait                      => AdvanceStep
      case DecodedInstruction.SSetGprIdxOn              => AdvanceStep
      case DecodedInstruction.Barrier                   => Right(Some(-1))
      case DecodedInstruction.Branch(condition, target) =>
        val taken = condition match {
          case ScalarBranchCondition.Always     => true
          case ScalarBranchCondition.SccZero    => !scalarCarry
          case ScalarBranchCondition.SccNonZero => scalarCarry
          case ScalarBranchCondition.VccZero    =>
            (laneMask(carry) & Bits.mask(lanes.bitWidth)) == 0L
          case ScalarBranchCondition.VccNonZero =>
            (laneMask(carry) & Bits.mask(lanes.bitWidth)) != 0L
          case ScalarBranchCondition.ExecZero    => active.isEmpty
          case ScalarBranchCondition.ExecNonZero => active.nonEmpty
        }
        if (traceControl)
          scribe.debug(
            s"Gemstone AMDGPU control branch index=$index target=$target condition=$condition taken=$taken active=${laneMask(active)} scc=$scalarCarry vcc=${laneMask(carry)}"
          )
        Right(Some(if (taken) target else index + 1))
      case DecodedInstruction.BranchRelative(_, _) =>
        Left(ExecutionFailure.Failed("unresolved AMDGPU branch reached execution"))
      case DecodedInstruction.SLoad(dest, count, base, offset, scalarOffset) =>
        val address = pair(scalar, base) + offset.toLong + scalarOffset.fold(0L)(register => scalar(register))
        readBytes(
          address,
          count * 4,
          kernarg,
          dispatchBytes,
          kernel.readOnlyAddress,
          kernel.readOnly,
          kernel.globalMemory,
          allocations,
          memory
        ) match {
          case Left(error)  => Left(error)
          case Right(bytes) =>
            (0 until count).foreach { register =>
              scalar(dest + register) = unsignedInt(bytes, register * 4)
            }
            if (
              AMDGPUEnvironment.trace("merge-input") &&
              kernel.name.contains("wrapped_merge_sort") && localStart == 0 && index >= 1304 && index <= 1304
            )
              scribe.debug(
                s"Gemstone AMDGPU merge input sload index=$index base=$base offset=$offset address=0x${address.toHexString} dest=$dest values=${(0 until count).map(
                    register => scalar(dest + register)
                  ).mkString("[", ",", "]")}"
              )
            if (traceControl) {
              val loaded = (0 until count).map(register => scalar(dest + register))
              scribe.debug(
                s"Gemstone AMDGPU control sload dest=$dest count=$count base=$base offset=$offset values=$loaded"
              )
            }
            Right(Some(index + 1))
        }
      case DecodedInstruction.SMove(dest, src) =>
        writeScalar32(dest, source(src, 0))
        if (traceControl && (dest == 18 || dest == 19))
          scribe.debug(s"Gemstone AMDGPU control smove dest=$dest src=$src value=${scalar(dest)}")
        if (traceSetControl)
          scribe.debug(
            s"Gemstone AMDGPU set smove kernel=${kernel.name} localStart=$localStart index=$index dest=$dest src=$src value=0x${scalar(dest).toHexString}"
          )
        AdvanceStep
      case DecodedInstruction.SMoveImmediate(dest, value) =>
        writeScalar32(dest, Bits.unsigned32(value))
        if (traceSetControl)
          scribe.debug(
            s"Gemstone AMDGPU set smovei kernel=${kernel.name} localStart=$localStart index=$index dest=$dest value=0x${scalar(dest).toHexString}"
          )
        AdvanceStep
      case DecodedInstruction.SMultiplyImmediate(dest, value) =>
        val (result, overflow) = signedMultiplyImmediate(scalar(dest), value)
        scalar(dest) = result
        scalarCarry = overflow
        AdvanceStep
      case DecodedInstruction.SAddImmediate(dest, value) =>
        val result = scalar(dest) + value.toLong
        writeScalar32(dest, result)
        scalarCarry = result > Bits.Mask32 || result < 0L
        AdvanceStep
      case DecodedInstruction.SMove64(dest, src) =>
        writeScalar64(dest, sourceScalar64(src))
        AdvanceStep
      case DecodedInstruction.SSetReg(_, _) =>
        AdvanceStep
      case DecodedInstruction.SGetPc(dest, offset) =>
        writeScalar64(dest, programCounter(index) + 4L)
        AdvanceStep
      case DecodedInstruction.SSetPc(src) =>
        val target = sourceScalar64(src)
        programIndex(target)
          .map(index => Right(Some(index)))
          .getOrElse(Left(ExecutionFailure.Failed(
            f"AMDGPU setpc target 0x$target%X is not an instruction"
          )))
      case DecodedInstruction.SSwapPc(dest, src) =>
        val target      = sourceScalar64(src)
        val targetIndex = programIndex(target)
        val targetName  = kernel.functionTargets.get(target)
        if (!targetName.exists(isEmulatedCall(_)) && targetIndex.nonEmpty) {
          // Device calls and returns use the same hardware primitive:
          // save the next instruction address in the destination pair,
          // then jump to the source pair. A callee returns by swapping
          // its saved address back into the PC, so the scalar register
          // state itself is the call frame and no host stack is needed.
          writeScalar64(dest, programCounter(index) + 4L)
          Right(Some(targetIndex.get))
        } else {
          def complexSqrt(real: Double, imaginary: Double): (Double, Double) = {
            val magnitude     = math.hypot(real, imaginary)
            val rootReal      = math.sqrt((magnitude + real) * 0.5)
            val rootImaginary = math.copySign(math.sqrt((magnitude - real) * 0.5), imaginary)
            (rootReal, rootImaginary)
          }
          def complexLog(real: Double, imaginary: Double): (Double, Double) =
            (math.log(math.hypot(real, imaginary)), math.atan2(imaginary, real))
          def complexMultiply(
              leftReal: Double,
              leftImaginary: Double,
              rightReal: Double,
              rightImaginary: Double
          ): (Double, Double) =
            (
              leftReal * rightReal - leftImaginary * rightImaginary,
              leftReal * rightImaginary + leftImaginary * rightReal
            )
          def complexAcos(real: Double, imaginary: Double): (Double, Double) = {
            val square = complexMultiply(real, imaginary, real, imaginary)
            val root   = complexSqrt(1.0 - square._1, -square._2)
            val logged = complexLog(-imaginary + root._1, real + root._2)
            (math.Pi * 0.5 - logged._2, logged._1)
          }
          def complexAtan(real: Double, imaginary: Double): (Double, Double) = {
            val izReal      = -imaginary
            val izImaginary = real
            val left        = complexLog(1.0 + izReal, izImaginary)
            val right       = complexLog(1.0 - izReal, -izImaginary)
            ((left._2 - right._2) * 0.5, (right._1 - left._1) * 0.5)
          }
          def complexAcosh(real: Double, imaginary: Double): (Double, Double) = {
            val left    = complexSqrt(real + 1.0, imaginary)
            val right   = complexSqrt(real - 1.0, imaginary)
            val product = complexMultiply(left._1, left._2, right._1, right._2)
            complexLog(real + product._1, imaginary + product._2)
          }
          def complexAtanh(real: Double, imaginary: Double): (Double, Double) = {
            val left  = complexLog(1.0 + real, imaginary)
            val right = complexLog(1.0 - real, -imaginary)
            ((left._1 - right._1) * 0.5, (left._2 - right._2) * 0.5)
          }
          kernel.functionTargets.get(target) match {
            case Some(name) if name.contains("logIf") =>
              foreachActive { lane =>
                val real      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(0)(lane)))).toDouble
                val imaginary = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(1)(lane)))).toDouble
                vector(0)(lane) = IEEE754.floatToBinary32(math.log(math.hypot(real, imaginary)).toFloat).value
                vector(1)(lane) = IEEE754.floatToBinary32(math.atan2(imaginary, real).toFloat).value
              }
              AdvanceStep
            case Some(name) if name.contains("logId") =>
              foreachActive { lane =>
                val real          = IEEE754.binary64ToDouble(pair(vector, 0, lane))
                val imaginary     = IEEE754.binary64ToDouble(pair(vector, 2, lane))
                val realBits      = IEEE754.doubleToBinary64(math.log(math.hypot(real, imaginary)))
                val imaginaryBits = IEEE754.doubleToBinary64(math.atan2(imaginary, real))
                vector(0)(lane) = Bits.low32(realBits)
                vector(1)(lane) = Bits.high32(realBits)
                vector(2)(lane) = Bits.low32(imaginaryBits)
                vector(3)(lane) = Bits.high32(imaginaryBits)
              }
              AdvanceStep
            case Some(name) if name.contains("casinhf") =>
              foreachActive { lane =>
                val real             = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(0)(lane)))).toDouble
                val imaginary        = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(1)(lane)))).toDouble
                val squareReal       = real * real - imaginary * imaginary + 1.0
                val squareImaginary  = 2.0 * real * imaginary
                val magnitude        = math.hypot(squareReal, squareImaginary)
                val rootReal         = math.sqrt((magnitude + squareReal) * 0.5)
                val rootImaginary    = math.copySign(math.sqrt((magnitude - squareReal) * 0.5), squareImaginary)
                val shiftedReal      = real + rootReal
                val shiftedImaginary = imaginary + rootImaginary
                vector(0)(lane) =
                  IEEE754.floatToBinary32(math.log(math.hypot(shiftedReal, shiftedImaginary)).toFloat).value
                vector(1)(lane) = IEEE754.floatToBinary32(math.atan2(shiftedImaginary, shiftedReal).toFloat).value
              }
              AdvanceStep
            case Some(name) if name.contains("casinhE") =>
              foreachActive { lane =>
                val real             = IEEE754.binary64ToDouble(pair(vector, 0, lane))
                val imaginary        = IEEE754.binary64ToDouble(pair(vector, 2, lane))
                val squareReal       = real * real - imaginary * imaginary + 1.0
                val squareImaginary  = 2.0 * real * imaginary
                val magnitude        = math.hypot(squareReal, squareImaginary)
                val rootReal         = math.sqrt((magnitude + squareReal) * 0.5)
                val rootImaginary    = math.copySign(math.sqrt((magnitude - squareReal) * 0.5), squareImaginary)
                val shiftedReal      = real + rootReal
                val shiftedImaginary = imaginary + rootImaginary
                val resultReal       = math.log(math.hypot(shiftedReal, shiftedImaginary))
                val resultImaginary  = math.atan2(shiftedImaginary, shiftedReal)
                val realBits         = IEEE754.doubleToBinary64(resultReal)
                val imaginaryBits    = IEEE754.doubleToBinary64(resultImaginary)
                vector(0)(lane) = Bits.low32(realBits)
                vector(1)(lane) = Bits.high32(realBits)
                vector(2)(lane) = Bits.low32(imaginaryBits)
                vector(3)(lane) = Bits.high32(imaginaryBits)
              }
              AdvanceStep
            case Some(name) if name.contains("cacosf") =>
              foreachActive { lane =>
                val real      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(0)(lane)))).toDouble
                val imaginary = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(1)(lane)))).toDouble
                val result    = complexAcos(real, imaginary)
                vector(0)(lane) = IEEE754.floatToBinary32(result._1.toFloat).value
                vector(1)(lane) = IEEE754.floatToBinary32(result._2.toFloat).value
              }
              AdvanceStep
            case Some(name) if name.contains("cacosE") =>
              foreachActive { lane =>
                val real          = IEEE754.binary64ToDouble(pair(vector, 0, lane))
                val imaginary     = IEEE754.binary64ToDouble(pair(vector, 2, lane))
                val result        = complexAcos(real, imaginary)
                val realBits      = IEEE754.doubleToBinary64(result._1)
                val imaginaryBits = IEEE754.doubleToBinary64(result._2)
                if (AMDGPUEnvironment.trace("calls") && lane == 0)
                  scribe.debug(
                    s"Gemstone AMDGPU cacosE input=($real,$imaginary) result=${result._1},${result._2}"
                  )
                vector(0)(lane) = Bits.low32(realBits)
                vector(1)(lane) = Bits.high32(realBits)
                vector(2)(lane) = Bits.low32(imaginaryBits)
                vector(3)(lane) = Bits.high32(imaginaryBits)
              }
              AdvanceStep
            case Some(name) if name.contains("catanf") =>
              foreachActive { lane =>
                val real      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(0)(lane)))).toDouble
                val imaginary = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(1)(lane)))).toDouble
                val result    = complexAtan(real, imaginary)
                vector(0)(lane) = IEEE754.floatToBinary32(result._1.toFloat).value
                vector(1)(lane) = IEEE754.floatToBinary32(result._2.toFloat).value
              }
              AdvanceStep
            case Some(name) if name.contains("catanE") =>
              foreachActive { lane =>
                val real          = IEEE754.binary64ToDouble(pair(vector, 0, lane))
                val imaginary     = IEEE754.binary64ToDouble(pair(vector, 2, lane))
                val result        = complexAtan(real, imaginary)
                val realBits      = IEEE754.doubleToBinary64(result._1)
                val imaginaryBits = IEEE754.doubleToBinary64(result._2)
                vector(0)(lane) = Bits.low32(realBits)
                vector(1)(lane) = Bits.high32(realBits)
                vector(2)(lane) = Bits.low32(imaginaryBits)
                vector(3)(lane) = Bits.high32(imaginaryBits)
              }
              AdvanceStep
            case Some(name) if name.contains("cacoshf") =>
              foreachActive { lane =>
                val real      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(0)(lane)))).toDouble
                val imaginary = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(1)(lane)))).toDouble
                val result    = complexAcosh(real, imaginary)
                vector(0)(lane) = IEEE754.floatToBinary32(result._1.toFloat).value
                vector(1)(lane) = IEEE754.floatToBinary32(result._2.toFloat).value
              }
              AdvanceStep
            case Some(name) if name.contains("cacoshE") =>
              foreachActive { lane =>
                val real          = IEEE754.binary64ToDouble(pair(vector, 0, lane))
                val imaginary     = IEEE754.binary64ToDouble(pair(vector, 2, lane))
                val result        = complexAcosh(real, imaginary)
                val realBits      = IEEE754.doubleToBinary64(result._1)
                val imaginaryBits = IEEE754.doubleToBinary64(result._2)
                vector(0)(lane) = Bits.low32(realBits)
                vector(1)(lane) = Bits.high32(realBits)
                vector(2)(lane) = Bits.low32(imaginaryBits)
                vector(3)(lane) = Bits.high32(imaginaryBits)
              }
              AdvanceStep
            case Some(name) if name.contains("catanhf") =>
              foreachActive { lane =>
                val real      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(0)(lane)))).toDouble
                val imaginary = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(1)(lane)))).toDouble
                val result    = complexAtanh(real, imaginary)
                vector(0)(lane) = IEEE754.floatToBinary32(result._1.toFloat).value
                vector(1)(lane) = IEEE754.floatToBinary32(result._2.toFloat).value
              }
              AdvanceStep
            case Some(name) if name.contains("catanhE") =>
              foreachActive { lane =>
                val real          = IEEE754.binary64ToDouble(pair(vector, 0, lane))
                val imaginary     = IEEE754.binary64ToDouble(pair(vector, 2, lane))
                val result        = complexAtanh(real, imaginary)
                val realBits      = IEEE754.doubleToBinary64(result._1)
                val imaginaryBits = IEEE754.doubleToBinary64(result._2)
                vector(0)(lane) = Bits.low32(realBits)
                vector(1)(lane) = Bits.high32(realBits)
                vector(2)(lane) = Bits.low32(imaginaryBits)
                vector(3)(lane) = Bits.high32(imaginaryBits)
              }
              AdvanceStep
            case Some(name) if name.contains("csinhf") =>
              foreachActive { lane =>
                val real            = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(0)(lane)))).toDouble
                val imaginary       = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(1)(lane)))).toDouble
                val resultReal      = math.sinh(real) * math.cos(imaginary)
                val resultImaginary = math.cosh(real) * math.sin(imaginary)
                vector(0)(lane) = IEEE754.floatToBinary32(resultReal.toFloat).value
                vector(1)(lane) = IEEE754.floatToBinary32(resultImaginary.toFloat).value
              }
              AdvanceStep
            case Some(name) if name.contains("ccoshf") =>
              foreachActive { lane =>
                val real            = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(0)(lane)))).toDouble
                val imaginary       = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(1)(lane)))).toDouble
                val resultReal      = math.cosh(real) * math.cos(imaginary)
                val resultImaginary = math.sinh(real) * math.sin(imaginary)
                vector(0)(lane) = IEEE754.floatToBinary32(resultReal.toFloat).value
                vector(1)(lane) = IEEE754.floatToBinary32(resultImaginary.toFloat).value
              }
              AdvanceStep
            case Some(name) if name.contains("ctanhf") =>
              foreachActive { lane =>
                val real            = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(0)(lane)))).toDouble
                val imaginary       = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(1)(lane)))).toDouble
                val denominator     = math.cosh(2.0 * real) + math.cos(2.0 * imaginary)
                val resultReal      = math.sinh(2.0 * real) / denominator
                val resultImaginary = math.sin(2.0 * imaginary) / denominator
                vector(0)(lane) = IEEE754.floatToBinary32(resultReal.toFloat).value
                vector(1)(lane) = IEEE754.floatToBinary32(resultImaginary.toFloat).value
              }
              AdvanceStep
            case Some(name) if name.contains("csinhE") =>
              foreachActive { lane =>
                val real            = IEEE754.binary64ToDouble(pair(vector, 0, lane))
                val imaginary       = IEEE754.binary64ToDouble(pair(vector, 2, lane))
                val resultReal      = math.sinh(real) * math.cos(imaginary)
                val resultImaginary = math.cosh(real) * math.sin(imaginary)
                val realBits        = IEEE754.doubleToBinary64(resultReal)
                val imaginaryBits   = IEEE754.doubleToBinary64(resultImaginary)
                vector(0)(lane) = Bits.low32(realBits)
                vector(1)(lane) = Bits.high32(realBits)
                vector(2)(lane) = Bits.low32(imaginaryBits)
                vector(3)(lane) = Bits.high32(imaginaryBits)
              }
              AdvanceStep
            case Some(name) if name.contains("ccoshE") =>
              foreachActive { lane =>
                val real            = IEEE754.binary64ToDouble(pair(vector, 0, lane))
                val imaginary       = IEEE754.binary64ToDouble(pair(vector, 2, lane))
                val resultReal      = math.cosh(real) * math.cos(imaginary)
                val resultImaginary = math.sinh(real) * math.sin(imaginary)
                val realBits        = IEEE754.doubleToBinary64(resultReal)
                val imaginaryBits   = IEEE754.doubleToBinary64(resultImaginary)
                vector(0)(lane) = Bits.low32(realBits)
                vector(1)(lane) = Bits.high32(realBits)
                vector(2)(lane) = Bits.low32(imaginaryBits)
                vector(3)(lane) = Bits.high32(imaginaryBits)
              }
              AdvanceStep
            case Some(name) if name.contains("ctanhE") =>
              foreachActive { lane =>
                val real            = IEEE754.binary64ToDouble(pair(vector, 0, lane))
                val imaginary       = IEEE754.binary64ToDouble(pair(vector, 2, lane))
                val denominator     = math.cosh(2.0 * real) + math.cos(2.0 * imaginary)
                val resultReal      = math.sinh(2.0 * real) / denominator
                val resultImaginary = math.sin(2.0 * imaginary) / denominator
                val realBits        = IEEE754.doubleToBinary64(resultReal)
                val imaginaryBits   = IEEE754.doubleToBinary64(resultImaginary)
                vector(0)(lane) = Bits.low32(realBits)
                vector(1)(lane) = Bits.high32(realBits)
                vector(2)(lane) = Bits.low32(imaginaryBits)
                vector(3)(lane) = Bits.high32(imaginaryBits)
              }
              AdvanceStep
            case Some(name) if name.contains("expIf") =>
              if (AMDGPUEnvironment.trace("calls")) {
                val real      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(0)(0))))
                val imaginary = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(1)(0))))
                scribe.debug(s"Gemstone AMDGPU call name=$name real=$real imaginary=$imaginary")
              }
              foreachActive { lane =>
                val real      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(0)(lane)))).toDouble
                val imaginary = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(1)(lane)))).toDouble
                val scale     = math.exp(real)
                vector(0)(lane) = IEEE754.floatToBinary32((scale * math.cos(imaginary)).toFloat).value
                vector(1)(lane) = IEEE754.floatToBinary32((scale * math.sin(imaginary)).toFloat).value
              }
              AdvanceStep
            case Some(name) if name.contains("expId") =>
              foreachActive { lane =>
                val real          = IEEE754.binary64ToDouble(pair(vector, 0, lane))
                val imaginary     = IEEE754.binary64ToDouble(pair(vector, 2, lane))
                val scale         = math.exp(real)
                val realBits      = IEEE754.doubleToBinary64(scale * math.cos(imaginary))
                val imaginaryBits = IEEE754.doubleToBinary64(scale * math.sin(imaginary))
                vector(0)(lane) = Bits.low32(realBits)
                vector(1)(lane) = Bits.high32(realBits)
                vector(2)(lane) = Bits.low32(imaginaryBits)
                vector(3)(lane) = Bits.high32(imaginaryBits)
              }
              AdvanceStep
            case Some(name)
                if name == "__ockl_get_group_id" || name == "__ockl_get_local_size" || name == "__ockl_get_local_id" =>
              foreachActive { lane =>
                val dimension = Bits.low32(vector(0)(lane)).toInt
                val value     = name match {
                  case "__ockl_get_group_id" => dimension match {
                      case 0 => groupX
                      case 1 => groupY
                      case 2 => groupZ
                      case _ => 0
                    }
                  case "__ockl_get_local_size" => dimension match {
                      case 0 => groupWidth
                      case 1 => groupHeight
                      case 2 => groupDepth
                      case _ => 0
                    }
                  case "__ockl_get_local_id" =>
                    val local = laneCoordinates(lane)
                    dimension match {
                      case 0 => local.x
                      case 1 => local.y
                      case 2 => local.z
                      case _ => 0
                    }
                  case _ => 0
                }
                vector(0)(lane) = Bits.unsigned32(value)
              }
              AdvanceStep
            case Some(name) =>
              Left(ExecutionFailure.Unsupported(s"AMDGPU call to $name is not implemented"))
            case None =>
              Left(ExecutionFailure.Failed(
                f"AMDGPU call target 0x$target%X is not present in the code object"
              ))
          }
        }
      case DecodedInstruction.SAndSaveExec(dest, src, width) =>
        val previous    = laneMask(active)
        val sourceValue = saveExecSource(src, width)
        val result      = if (width == 32) Bits.low32(previous) & sourceValue else previous & sourceValue
        saveExecWrite(dest, previous, width)
        saveExecWrite(126, result, width)
        scalarCarry = result != 0L
        if (traceMergeRank && index >= 1548 && index <= 1625)
          scribe.debug(
            s"Gemstone AMDGPU merge rank saveexec index=$index dest=$dest src=$src previous=0x${previous.toHexString} source=0x${sourceValue.toHexString} result=0x${result.toHexString}"
          )
        if (traceControl) scribe.debug(
          s"Gemstone AMDGPU control and dest=$dest src=$src previous=$previous source=$sourceValue result=$result"
        )
        if (traceSetControl)
          scribe.debug(
            s"Gemstone AMDGPU set and kernel=${kernel.name} localStart=$localStart index=$index dest=$dest src=$src width=$width previous=0x${previous.toHexString} source=0x${sourceValue.toHexString} result=0x${result.toHexString}"
          )
        AdvanceStep
      case DecodedInstruction.SAndn2SaveExec(dest, src, width) =>
        val previous    = laneMask(active)
        val sourceValue = saveExecSource(src, width)
        // The explicit source is ANDN2's first operand and the old
        // EXEC mask is its second operand; the old mask is saved to
        // dest before EXEC is replaced.
        val result = if (width == 32) Bits.low32(andn2SaveExecMask(Bits.low32(previous), sourceValue))
        else andn2SaveExecMask(previous, sourceValue)
        saveExecWrite(dest, previous, width)
        saveExecWrite(126, result, width)
        scalarCarry = result != 0L
        if (traceControl) scribe.debug(
          s"Gemstone AMDGPU control andn2 dest=$dest src=$src previous=$previous source=$sourceValue result=$result"
        )
        if (traceSetControl)
          scribe.debug(
            s"Gemstone AMDGPU set andn2 kernel=${kernel.name} localStart=$localStart index=$index dest=$dest src=$src width=$width previous=0x${previous.toHexString} source=0x${sourceValue.toHexString} result=0x${result.toHexString}"
          )
        AdvanceStep
      case DecodedInstruction.SOrSaveExec(dest, src, width) =>
        val previous = laneMask(active)
        val result   = if (width == 32) Bits.low32(previous) | saveExecSource(src, width)
        else previous | saveExecSource(src, width)
        saveExecWrite(dest, previous, width)
        saveExecWrite(126, result, width)
        scalarCarry = result != 0L
        AdvanceStep
      case DecodedInstruction.SXorSaveExec(dest, src, width) =>
        val previous = laneMask(active)
        val result   = if (width == 32) Bits.low32(previous) ^ saveExecSource(src, width)
        else previous ^ saveExecSource(src, width)
        saveExecWrite(dest, previous, width)
        saveExecWrite(126, result, width)
        scalarCarry = result != 0L
        AdvanceStep
      case DecodedInstruction.SUnary(opcode, dest, src) =>
        val result = opcode match {
          case "s_abs_i32"       => Bits.low32(math.abs(Bits.low32(source(src, 0)).toInt).toLong)
          case "s_sext_i32_i8"   => Bits.low32(Bits.signExtend(source(src, 0) & 0xffL, BitWidth(8)))
          case "s_sext_i32_i16"  => Bits.low32(Bits.signExtend(source(src, 0) & 0xffffL, BitWidth(16)))
          case "s_bcnt1_i32_b32" => Bits.bitCount(source(src, 0), BitWidth(32)).toLong
          case "s_bcnt1_i32_b64" => Bits.bitCount(sourceScalar64(src), BitWidth(64)).toLong
          case "s_brev_b32"      => bitReverse32(source(src, 0))
          case "s_not_b32"       => Bits.low32(~source(src, 0))
          case "s_not_b64"       => ~sourceScalar64(src)
          case "s_flbit_i32"     => signedFirstBitHigh32(source(src, 0))
          case "s_bitset0_b32"   => Bits.low32(scalar(dest) & ~(1L << (source(src, 0).toInt & 31)))
          case "s_bitset1_b32"   => Bits.low32(scalar(dest) | (1L << (source(src, 0).toInt & 31)))
        }
        if (opcode == "s_not_b64") writeScalar64(dest, result) else writeScalar32(dest, result)
        if (opcode == "s_not_b32" || opcode == "s_not_b64") scalarCarry = result != 0L
        if (traceControl && (dest == 18 || dest == 19))
          scribe.debug(s"Gemstone AMDGPU control sunary opcode=$opcode dest=$dest src=$src value=$result")
        AdvanceStep
      case DecodedInstruction.SBinary(opcode, dest, srcA, srcB) =>
        val left   = Bits.low32(source(srcA, 0))
        val right  = Bits.low32(source(srcB, 0))
        val result = opcode match {
          case "s_and_b32" =>
            val value = left & right
            scalarCarry = value != 0L
            value
          case "s_or_b32" =>
            val value = left | right
            scalarCarry = value != 0L
            value
          case "s_mul_i32"    => left * right
          case "s_mul_hi_u32" => unsignedMultiplyHigh32(left, right)
          case "s_mul_hi_i32" => signedMultiplyHigh32(left, right)
          case "s_add_i32"    =>
            val sum = Bits.low32(left + right)
            scalarCarry = ((~(left ^ right) & (left ^ sum)) & 0x80000000L) != 0L
            sum
          case "s_sub_i32" =>
            val difference = Bits.low32(left - right)
            scalarCarry = (((left ^ right) & (left ^ difference)) & 0x80000000L) != 0L
            difference
          case "s_lshl_b32" => left << (right.toInt & 31)
          case "s_ashr_i32" => Bits.unsigned32(left.toInt >> (right.toInt & 31))
          case "s_bfe_u32"  =>
            val shift = (right & 0x3fL).toInt
            val width = ((right >>> 16) & 0x3fL).toInt
            if (width >= 32) Bits.low32(left >>> shift)
            else if (width == 0) 0L else Bits.low32((left >>> shift) & ((1L << width) - 1L))
          case "s_bfe_i32" =>
            val shift = (right & 0x3fL).toInt
            val width = ((right >>> 16) & 0x3fL).toInt
            if (width >= 32) Bits.low32(Bits.signExtend(left >>> shift, BitWidth(32)))
            else if (width == 0) 0L
            else {
              val bitWidth = BitWidth.assume(width)
              Bits.low32(Bits.signExtend((left >>> shift) & ((1L << width) - 1L), bitWidth))
            }
          case "s_lshr_b32"                                                     => left >>> (right.toInt & 31)
          case minMax @ ("s_min_u32" | "s_max_u32" | "s_min_i32" | "s_max_i32") =>
            val (selected, sourceZeroWon) = scalarMinMax(minMax, left, right)
            scalarCarry = sourceZeroWon
            selected
          case "s_andn2_b32" =>
            val value = left & ~right
            scalarCarry = value != 0L
            value
          case "s_orn2_b32" =>
            val value = Bits.low32(left | ~right)
            scalarCarry = value != 0L
            value
          case "s_nor_b32" =>
            val value = Bits.low32(~(left | right))
            scalarCarry = value != 0L
            value
          case "s_pack_ll_b32_b16" => Bits.low16(left) | (Bits.low16(right) << 16)
          case "s_pack_lh_b32_b16" => Bits.low16(left) | (Bits.low16(right >>> 16) << 16)
          case "s_pack_hh_b32_b16" => (Bits.low16(left >>> 16)) | (Bits.low16(right >>> 16) << 16)
          case "s_add_u32"         =>
            val sum = left + right
            scalarCarry = sum > Bits.Mask32
            sum
          case "s_addc_u32" =>
            val sum = left + right + (if (scalarCarry) 1L else 0L)
            scalarCarry = sum > Bits.Mask32
            sum
          case "s_sub_u32" =>
            scalarCarry = left >= right
            left - right
          case "s_subb_u32" =>
            val borrow = if (scalarCarry) 0L else 1L
            scalarCarry = left >= right + borrow
            left - right - borrow
          case "s_xor_b32" =>
            val value = left ^ right
            scalarCarry = value != 0L
            value
        }
        writeScalar32(dest, result)
        if (traceSetControl)
          scribe.debug(
            s"Gemstone AMDGPU set sbinary kernel=${kernel.name} localStart=$localStart index=$index opcode=$opcode dest=$dest srcA=$srcA srcB=$srcB left=0x${left.toHexString} right=0x${right.toHexString} result=0x${scalar(dest).toHexString} scc=$scalarCarry"
          )
        AdvanceStep
      case DecodedInstruction.SBinary64(opcode, dest, srcA, srcB) =>
        val left   = sourceScalar64(srcA)
        val right  = sourceScalar64(srcB)
        val result = opcode match {
          case "s_and_b64"   => left & right
          case "s_andn2_b64" => left & ~right
          case "s_or_b64"    => left | right
          case "s_orn2_b64"  => left | ~right
          case "s_nor_b64"   => ~(left | right)
          case "s_xor_b64"   => left ^ right
          case "s_lshl_b64"  => left << (right.toInt & 63)
          case "s_lshr_b64"  => left >>> (right.toInt & 63)
          case "s_ashr_i64"  => left >> (right.toInt & 63)
          case "s_bfe_i64"   =>
            val shift = (right & 0x3fL).toInt
            val width = ((right >>> 16) & 0x3fL).toInt
            if (width >= 64) Bits.signExtend(left >>> shift, BitWidth(64))
            else if (width == 0) 0L
            else {
              val bitWidth = BitWidth.assume(width)
              Bits.signExtend((left >>> shift) & Bits.mask(bitWidth), bitWidth)
            }
        }
        writeScalar64(dest, result)
        scalarCarry = result != 0L
        if (traceMergeRank && index >= 1548 && index <= 1625)
          scribe.debug(
            s"Gemstone AMDGPU merge rank mask index=$index opcode=$opcode dest=$dest srcA=$srcA srcB=$srcB left=0x${left.toHexString} right=0x${right.toHexString} result=0x${result.toHexString} active=0x${laneMask(active).toHexString}"
          )
        if (traceControl)
          scribe.debug(
            s"Gemstone AMDGPU control sbinary64 opcode=$opcode dest=$dest srcA=$srcA left=$left srcB=$srcB right=$right result=$result active=${laneMask(active)} vcc=${laneMask(carry)}"
          )
        AdvanceStep
      case DecodedInstruction.SCSelect(dest, srcA, srcB, width) =>
        val selected = if (scalarCarry) srcA else srcB
        if (width == 64) writeScalar64(dest, sourceScalar64(selected))
        else writeScalar32(dest, source(selected, 0))
        AdvanceStep
      case DecodedInstruction.SCmpEqI32(src, immediate) =>
        scalarCarry = scalar(src).toInt == immediate
        AdvanceStep
      case DecodedInstruction.SCmpImmediate(opcode, src, immediate) =>
        scalarCarry = opcode match {
          case "s_cmpk_eq_i32" => scalar(src).toInt == immediate
          case "s_cmpk_lg_i32" => scalar(src).toInt != immediate
          case "s_cmpk_gt_i32" => scalar(src).toInt > immediate
          case "s_cmpk_lt_i32" => scalar(src).toInt < immediate
          case "s_cmpk_eq_u32" => Bits.unsigned32(Bits.low32(scalar(src)).toInt) == Bits.unsigned32(immediate)
          case "s_cmpk_lg_u32" => Bits.unsigned32(Bits.low32(scalar(src)).toInt) != Bits.unsigned32(immediate)
          case "s_cmpk_gt_u32" => Bits.unsigned32(Bits.low32(scalar(src)).toInt) > Bits.unsigned32(immediate)
          case "s_cmpk_ge_u32" => Bits.unsigned32(Bits.low32(scalar(src)).toInt) >= Bits.unsigned32(immediate)
          case "s_cmpk_lt_u32" => Bits.unsigned32(Bits.low32(scalar(src)).toInt) < Bits.unsigned32(immediate)
          case "s_cmpk_le_u32" => Bits.unsigned32(Bits.low32(scalar(src)).toInt) <= Bits.unsigned32(immediate)
        }
        AdvanceStep
      case DecodedInstruction.SCmp(opcode, srcA, srcB) =>
        val left32  = Bits.low32(source(srcA, 0))
        val right32 = Bits.low32(source(srcB, 0))
        scalarCarry = opcode match {
          case "s_cmp_eq_u32"  => left32 == right32
          case "s_cmp_gt_i32"  => left32.toInt > right32.toInt
          case "s_cmp_gt_u32"  => left32 > right32
          case "s_cmp_ge_u32"  => left32 >= right32
          case "s_cmp_le_u32"  => left32 <= right32
          case "s_cmp_eq_i32"  => left32.toInt == right32.toInt
          case "s_cmp_lg_i32"  => left32.toInt != right32.toInt
          case "s_cmp_ge_i32"  => left32.toInt >= right32.toInt
          case "s_cmp_le_i32"  => left32.toInt <= right32.toInt
          case "s_cmp_lt_u32"  => left32 < right32
          case "s_cmp_lg_u32"  => left32 != right32
          case "s_cmp_lt_i32"  => left32.toInt < right32.toInt
          case "s_bitcmp1_b32" => ((left32 >>> (right32.toInt & 31)) & 1L) != 0L
          case "s_bitcmp0_b32" => ((left32 >>> (right32.toInt & 31)) & 1L) == 0L
          case "s_cmp_eq_u64"  => sourceScalar64(srcA) == sourceScalar64(srcB)
          case "s_cmp_lg_u64"  => sourceScalar64(srcA) != sourceScalar64(srcB)
        }
        AdvanceStep
      case DecodedInstruction.VMove(dest, src) =>
        executeVMove(dest, src, scalar, vector, carry, active, lanes)
        AdvanceStep
      case DecodedInstruction.VMoveRelative(dest, InstructionSource.VectorRegister(src)) =>
        val indexed = src + (Bits.low32(scalar(124)) & 0xffL).toInt
        if (indexed < 0 || indexed >= 256)
          Left(ExecutionFailure.Failed(s"v_movrels_b32 indexed VGPR $indexed is out of range"))
        else {
          val sourceValues = vector(indexed)
          foreachActive(lane => vector(dest)(lane) = Bits.low32(sourceValues(lane)))
          if (AMDGPUEnvironment.trace("movrel") && localStart == 0)
            scribe.debug(
              s"Gemstone AMDGPU movrels kernel=${kernel.name} index=$index dest=$dest src=$src m0=${Bits.low32(scalar(124))} indexed=$indexed v0=0x${Bits.low32(sourceValues(0)).toHexString}"
            )
          AdvanceStep
        }
      case DecodedInstruction.VMoveRelative(_, source) =>
        Left(ExecutionFailure.Failed(
          s"v_movrels_b32 requires a vector-register source, found $source"
        ))
      case DecodedInstruction.VMoveRelativeDestination(dest, src) =>
        val indexed = dest + (Bits.low32(scalar(124)) & 0xffL).toInt
        if (indexed < 0 || indexed >= 256)
          Left(ExecutionFailure.Failed(s"v_movreld_b32 indexed VGPR $indexed is out of range"))
        else {
          foreachActive(lane => vector(indexed)(lane) = Bits.low32(source(src, lane)))
          if (AMDGPUEnvironment.trace("movrel") && localStart == 0)
            scribe.debug(
              s"Gemstone AMDGPU movreld kernel=${kernel.name} index=$index dest=$dest src=$src m0=${Bits.low32(scalar(124))} indexed=$indexed v0=0x${Bits.low32(source(src, 0)).toHexString}"
            )
          AdvanceStep
        }
      case DecodedInstruction.VNot(dest, src) =>
        foreachActive(lane => vector(dest)(lane) = Bits.low32(~source(src, lane)))
        AdvanceStep
      case DecodedInstruction.VSwap(dest, InstructionSource.VectorRegister(src)) =>
        foreachActive { lane =>
          val destinationValue = vector(dest)(lane)
          vector(dest)(lane) = Bits.low32(vector(src)(lane))
          vector(src)(lane) = Bits.low32(destinationValue)
        }
        AdvanceStep
      case DecodedInstruction.VSwap(_, _) =>
        Left(ExecutionFailure.Failed("v_swap_b32 requires a vector-register source"))
      case DecodedInstruction.VBitReverse(dest, src) =>
        foreachActive(lane => vector(dest)(lane) = bitReverse32(source(src, lane)))
        AdvanceStep
      case DecodedInstruction.VBitFind(opcode, dest, src) =>
        foreachActive { lane =>
          val value  = Bits.low32(source(src, lane)).toInt
          val result = if (opcode == "v_ffbl_b32") {
            if (value == 0) -1L else Integer.numberOfTrailingZeros(value).toLong
          } else if (opcode == "v_ffbh_i32") {
            signedFirstBitHigh32(value)
          } else {
            // FFBH counts zero bits from the MSB to the first set
            // bit.  It is therefore CLZ-like, not the bit index of
            // the highest set bit.  Both forms use -1 for zero.
            firstBitHigh32(value)
          }
          vector(dest)(lane) = Bits.unsigned32(result.toInt)
        }
        AdvanceStep
      case DecodedInstruction.VConvert(opcode, dest, src) =>
        foreachActive { lane =>
          val raw = Bits.low32(source(src, lane)).toInt
          if (opcode == "v_frexp_exp_i32_f64") {
            val value = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
            vector(dest)(lane) = Bits.unsigned32(
              if (value == 0.0 || value.isNaN || value.isInfinite) 0 else Math.getExponent(value) + 1
            )
          } else if (opcode == "v_frexp_exp_i32_f32") {
            val value = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            vector(dest)(lane) = Bits.unsigned32(
              if (value == 0.0f || value.isNaN || value.isInfinite) 0 else Math.getExponent(value) + 1
            )
          } else if (opcode == "v_frexp_mant_f32") {
            val value    = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            val mantissa =
              if (value == 0.0f || value.isNaN || value.isInfinite) value
              else Math.scalb(value, -Math.getExponent(value) - 1)
            vector(dest)(lane) = IEEE754.floatToBinary32(mantissa).value
          } else if (opcode == "v_frexp_mant_f64") {
            val value    = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
            val mantissa =
              if (value == 0.0 || value.isNaN || value.isInfinite) value
              else Math.scalb(value, -Math.getExponent(value) - 1)
            val bits = IEEE754.doubleToBinary64(mantissa)
            vector(dest)(lane) = Bits.low32(bits)
            vector(dest + 1)(lane) = Bits.high32(bits)
          } else if (opcode == "v_rcp_iflag_f32" || opcode == "v_rcp_f32") {
            val value = 1.0f / IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            vector(dest)(lane) = IEEE754.floatToBinary32(value).value
          } else if (opcode == "v_rcp_legacy_f32") {
            val value = 1.0f / IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            vector(dest)(lane) = IEEE754.floatToBinary32(value).value
          } else if (opcode == "v_rcp_f64") {
            val value = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
            val bits  = IEEE754.doubleToBinary64(1.0 / value)
            vector(dest)(lane) = Bits.low32(bits)
            vector(dest + 1)(lane) = Bits.high32(bits)
          } else if (opcode == "v_trunc_f32" || opcode == "v_floor_f32" || opcode == "v_rndne_f32") {
            val value  = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            val result =
              if (opcode == "v_trunc_f32") {
                if (value >= 0.0f) math.floor(value.toDouble) else math.ceil(value.toDouble)
              } else if (opcode == "v_floor_f32") math.floor(value.toDouble)
              else Math.rint(value.toDouble)
            vector(dest)(lane) = IEEE754.floatToBinary32(result.toFloat).value
          } else if (opcode == "v_trunc_f64" || opcode == "v_floor_f64" || opcode == "v_fract_f64") {
            val value  = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
            val result =
              if (opcode == "v_trunc_f64") {
                if (value >= 0.0) math.floor(value) else math.ceil(value)
              } else if (opcode == "v_floor_f64") math.floor(value)
              else value - math.floor(value)
            val bits = IEEE754.doubleToBinary64(result)
            vector(dest)(lane) = Bits.low32(bits)
            vector(dest + 1)(lane) = Bits.high32(bits)
          } else if (opcode == "v_rndne_f64") {
            val value = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
            val bits  = IEEE754.doubleToBinary64(Math.rint(value))
            vector(dest)(lane) = Bits.low32(bits)
            vector(dest + 1)(lane) = Bits.high32(bits)
          } else if (opcode == "v_rsq_f32") {
            val value = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            vector(dest)(lane) = IEEE754.floatToBinary32((1.0 / math.sqrt(value.toDouble)).toFloat).value
          } else if (opcode == "v_rsq_f64") {
            val value = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
            val bits  = IEEE754.doubleToBinary64(1.0 / math.sqrt(value))
            vector(dest)(lane) = Bits.low32(bits)
            vector(dest + 1)(lane) = Bits.high32(bits)
          } else if (opcode == "v_sqrt_f32") {
            val value  = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            val result = math.sqrt(value.toDouble).toFloat
            vector(dest)(lane) = IEEE754.floatToBinary32(result).value
            if (traceSqrt && lane == 0)
              scribe.debug(s"Gemstone AMDGPU sqrt unary dest=$dest src=$src input=$value result=$result")
          } else if (opcode == "v_exp_f32") {
            val value = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            vector(dest)(lane) = IEEE754.floatToBinary32(math.pow(2.0, value.toDouble).toFloat).value
          } else if (opcode == "v_sin_f32") {
            val value = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            vector(dest)(lane) = IEEE754.floatToBinary32(trigF32(opcode, value)).value
          } else if (opcode == "v_cos_f32") {
            val value = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            vector(dest)(lane) = IEEE754.floatToBinary32(trigF32(opcode, value)).value
          } else if (opcode == "v_log_f32" || opcode == "v_log_legacy_f32") {
            val value = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            vector(dest)(lane) = IEEE754.floatToBinary32((math.log(value.toDouble) / math.log(2.0)).toFloat).value
            if (traceLog && lane == 0)
              scribe.debug(
                s"Gemstone AMDGPU log convert opcode=$opcode dest=$dest src=$src input=$value result=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(vector(dest)(lane))))}"
              )
          } else if (opcode == "v_trunc_f32") {
            val value = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            vector(dest)(lane) =
              IEEE754.floatToBinary32(if (value.isNaN || value.isInfinite) value else value.toInt.toFloat).value
          } else if (opcode == "v_rndne_f32") {
            val value = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            vector(dest)(lane) = IEEE754.floatToBinary32(Math.rint(value.toDouble).toFloat).value
          } else if (opcode == "v_cvt_i32_f32") {
            vector(dest)(lane) =
              Bits.unsigned32(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane)))).toInt)
          } else if (opcode == "v_cvt_u32_f32") {
            vector(dest)(lane) =
              unsignedFloatToInt32(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane)))))
          } else if (opcode == "v_cvt_i32_f64") {
            val value = IEEE754.binary64ToDouble(source64(src, lane, scalar, vector)).toInt
            vector(dest)(lane) = Bits.unsigned32(value)
          } else if (opcode == "v_cvt_u32_f64") {
            val value = IEEE754.binary64ToDouble(source64(src, lane, scalar, vector)).toLong
            vector(dest)(lane) = Bits.low32(value)
          } else if (opcode.startsWith("v_cvt_f32_ubyte")) {
            val shift = opcode.last - '0'
            val value = ((raw >>> (shift * 8)) & 0xff).toFloat
            vector(dest)(lane) = IEEE754.floatToBinary32(value).value
          } else if (opcode == "v_cvt_f32_f64") {
            val value = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector)).toFloat
            vector(dest)(lane) = IEEE754.floatToBinary32(value).value
          } else if (opcode == "v_cvt_f32_f16") {
            val value = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(raw & 0xffffL)))
            vector(dest)(lane) = IEEE754.floatToBinary32(value).value
          } else if (opcode == "v_cvt_f16_f32") {
            val value = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
            vector(dest)(lane) = IEEE754.floatToBinary16(value).value
          } else if (opcode == "v_cvt_i16_f16") {
            val value = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(raw & 0xffffL)))
            vector(dest)(lane) = Bits.low16(value.toInt.toLong)
          } else if (opcode == "v_cvt_f64_i32" || opcode == "v_cvt_f64_u32" || opcode == "v_cvt_f64_f32") {
            val value =
              if (opcode == "v_cvt_f64_i32") raw.toDouble
              else if (opcode == "v_cvt_f64_u32") Integer.toUnsignedLong(raw).toDouble
              else IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane)))).toDouble
            val bits = IEEE754.doubleToBinary64(value)
            vector(dest)(lane) = Bits.low32(bits)
            vector(dest + 1)(lane) = Bits.high32(bits)
          } else {
            val value = if (opcode == "v_cvt_f32_i32") raw.toFloat else Integer.toUnsignedLong(raw).toFloat
            vector(dest)(lane) = IEEE754.floatToBinary32(value).value
          }
        }
        AdvanceStep
      case DecodedInstruction.VMoveDPP(dest, src, control, rowMask, bankMask, boundControl, fetchInactive) =>
        val sourceValues = vector(src).clone()
        if (
          AMDGPUEnvironment.trace(
            "reduce"
          ) && kernel.name.contains("wrapped_reduce") && localStart == 0 && index < 300
        )
          scribe.debug(
            s"Gemstone AMDGPU reduce dpp index=$index dest=$dest src=$src control=0x${control.toHexString} " +
              s"rowMask=0x${rowMask.toHexString} bankMask=0x${bankMask.toHexString} " +
              s"v0=${sourceValues.take(math.min(4, lanes.value)).mkString(",")}"
          )
        foreachActive { lane =>
          if (dppLaneEnabled(lane, rowMask, bankMask)) {
            dppSourceLane(lane, control) match {
              case Some(sourceLane) if sourceLane < lanes.value =>
                vector(dest)(lane) =
                  if (fetchInactive || active(sourceLane)) Bits.low32(sourceValues(sourceLane)) else 0L
              case _ if boundControl => vector(dest)(lane) = 0L
              case _                 => ()
            }
            if (traceByteSortRank && lane == 0)
              scribe.debug(
                s"Gemstone AMDGPU byte-sort dpp index=$index control=0x${control.toHexString} src=$src dest=$dest source=0x${sourceValues(lane).toHexString} result=0x${vector(dest)(lane).toHexString}"
              )
          }
        }
        AdvanceStep
      case DecodedInstruction.VCndMask(dest, srcA, srcB, condition, absA, absB, negateA, negateB) =>
        // Wave32 conditions occupy one SGPR. Wave64 conditions occupy an
        // SGPR pair; truncating that pair makes lanes 32-63 always select A.
        val conditionMask = waveConditionMask(sourceScalar64(condition), lanes)
        foreachActive { lane =>
          val selectB       = (conditionMask & (1L << lane)) != 0L
          val selected      = if (selectB) srcB else srcA
          val selectedValue = Bits.low32(source(selected, lane))
          val absolute      = if (selectB) absB else absA
          val negate        = if (selectB) negateB else negateA
          val modified      =
            (if (absolute) selectedValue & 0x7fffffffL else selectedValue) ^
              (if (negate) 0x80000000L else 0L)
          vector(dest)(lane) = Bits.low32(modified)
          if (
            traceSetOps && kernel.name.contains(
              "set_operations"
            ) && localStart == 0 && index >= 2148 && index <= 2163 && lane < 2
          )
            scribe.debug(
              s"Gemstone AMDGPU set cndmask kernel=${kernel.name} index=$index lane=$lane dest=$dest srcA=$srcA srcB=$srcB condition=$condition conditionMask=0x${conditionMask.toHexString} selectB=$selectB a=0x${Bits.low32(source(srcA, lane)).toHexString} b=0x${Bits.low32(source(srcB, lane)).toHexString} result=0x${vector(dest)(lane).toHexString}"
            )
          if (
            AMDGPUEnvironment.trace("merge-select") &&
            lane == 0 && kernel.name.contains("wrapped_merge_sort") && index >= 1550 && index <= 1625
          )
            scribe.debug(
              s"Gemstone AMDGPU merge select index=$index dest=$dest srcA=$srcA srcB=$srcB condition=$conditionMask active=${active(lane)} a=0x${Bits.low32(source(srcA, lane)).toHexString} b=0x${Bits.low32(source(srcB, lane)).toHexString} result=0x${vector(dest)(lane).toHexString}"
            )
          if (
            AMDGPUEnvironment.trace("merge-stage") &&
            lane == 0 && kernel.name.contains("wrapped_merge_sort") && index >= 1400 && index <= 1494
          )
            scribe.debug(
              s"Gemstone AMDGPU merge stage select index=$index dest=$dest condition=0x${conditionMask.toHexString} a=0x${Bits.low32(source(srcA, lane)).toHexString} b=0x${Bits.low32(source(srcB, lane)).toHexString} result=0x${vector(dest)(lane).toHexString} v1=0x${vector(1)(lane).toHexString} v2=0x${vector(2)(lane).toHexString} v3=0x${vector(3)(lane).toHexString} v4=0x${vector(4)(lane).toHexString} v5=0x${vector(5)(lane).toHexString} v6=0x${vector(6)(lane).toHexString} v7=0x${vector(7)(lane).toHexString} v8=0x${vector(8)(lane).toHexString} v9=0x${vector(9)(lane).toHexString} v10=0x${vector(10)(lane).toHexString} v11=0x${vector(11)(lane).toHexString} v12=0x${vector(12)(lane).toHexString}"
            )
          if (traceLog && lane == 0)
            scribe.debug(
              s"Gemstone AMDGPU log cndmask dest=$dest srcA=$srcA srcB=$srcB condition=$conditionMask selectB=$selectB value=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(modified)))}"
            )
        }
        AdvanceStep
      case DecodedInstruction.VReadFirstLane(dest, src) =>
        active.first match {
          case None       => Left(ExecutionFailure.Failed("v_readfirstlane_b32 has an empty EXEC mask"))
          case Some(lane) =>
            scalar(dest) = Bits.low32(source(src, lane.value))
            Right(Some(index + 1))
        }
      case DecodedInstruction.VReadLane(dest, src, lane) =>
        val selected = source(lane, 0).toInt
        if (selected < 0 || selected >= lanes.value)
          Left(ExecutionFailure.Failed("v_readlane_b32 selected an invalid lane"))
        else {
          scalar(dest) = Bits.low32(source(src, selected))
          AdvanceStep
        }
      case DecodedInstruction.VWriteLane(dest, src, lane) =>
        val selected = source(lane, 0).toInt
        if (selected < 0 || selected >= lanes.value)
          Left(ExecutionFailure.Failed("v_writelane_b32 selected an invalid lane"))
        else {
          vector(dest)(selected) = Bits.low32(source(src, selected))
          AdvanceStep
        }
      case DecodedInstruction.VMaskBitCount(dest, mask, base, high) =>
        val executionMask =
          if (AMDGPUEnvironment.containsKey("GEMSTONE_AMDGPU_MBCNT_UNMASKED")) -1L else laneMask(active)
        if (AMDGPUEnvironment.trace("set-mask") && kernel.name.contains("set_operations") && localStart == 0)
          scribe.debug(
            s"Gemstone AMDGPU set mbcnt pre kernel=${kernel.name} index=$index dest=$dest mask=$mask base=$base high=$high active=0x${laneMask(active).toHexString} mask0=0x${source(mask, 0).toHexString} base0=0x${source(base, 0).toHexString} v0=0x${vector(dest)(0).toHexString}"
          )
        foreachActive { lane =>
          vector(dest)(lane) = mbCount(source(mask, lane), executionMask, lane, high, source(base, lane))
          if (traceByteSortRank && lane == 0)
            scribe.debug(
              s"Gemstone AMDGPU byte-sort mbcnt index=$index high=$high mask=0x${source(mask, lane).toHexString} base=0x${source(base, lane).toHexString} result=0x${vector(dest)(lane).toHexString}"
            )
        }
        AdvanceStep
      case DecodedInstruction.VBitCount(dest, src, base) =>
        foreachActive { lane =>
          vector(dest)(lane) =
            Bits.low32(java.lang.Integer.bitCount(Bits.low32(source(
              src,
              lane
            )).toInt).toLong + Bits.low32(source(
              base,
              lane
            )))
        }
        AdvanceStep
      case DecodedInstruction.VMadMkF32(dest, srcA, multiplier, addend) =>
        foreachActive { lane =>
          val left  = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcA, lane))))
          val scale = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(multiplier)))
          val right = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(addend, lane))))
          vector(dest)(lane) = IEEE754.floatToBinary32(left * scale + right).value
        }
        AdvanceStep
      case DecodedInstruction.VMadAkF32(dest, srcA, srcB, addend) =>
        foreachActive { lane =>
          val left    = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcA, lane))))
          val right   = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcB, lane))))
          val literal = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(addend)))
          // MADAK is the literal-addend form of MAD.  Unlike FMA it
          // rounds the product before adding the literal.
          vector(dest)(lane) = IEEE754.floatToBinary32(left * right + literal).value
        }
        AdvanceStep
      case DecodedInstruction.VMadF32(
            opcode,
            dest,
            srcA,
            srcB,
            srcC,
            absA,
            absB,
            absC,
            negateA,
            negateB,
            negateC,
            clamp
          ) =>
        foreachActive { lane =>
          val left           = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcA, lane))))
          val right          = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcB, lane))))
          val addend         = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcC, lane))))
          val preparedLeft   = if (absA) math.abs(left) else left
          val preparedRight  = if (absB) math.abs(right) else right
          val preparedAddend = if (absC) math.abs(addend) else addend
          val productLeft    = if (negateA) -preparedLeft else preparedLeft
          val productRight   = if (negateB) -preparedRight else preparedRight
          val productAddend  = if (negateC) -preparedAddend else preparedAddend
          val result         =
            if (opcode == "v_fma_f32") Math.fma(productLeft, productRight, productAddend)
            else productLeft * productRight + productAddend
          vector(dest)(lane) = IEEE754.floatToBinary32(if (clamp) clampF32(result) else result).value
          if (traceSqrt && lane == 0)
            scribe.debug(
              s"Gemstone AMDGPU sqrt mad dest=$dest left=$left right=$right addend=$addend result=$result"
            )
        }
        AdvanceStep
      case DecodedInstruction.VMadF64(dest, srcA, srcB, srcC, absA, absB, absC, negateA, negateB, negateC) =>
        foreachActive { lane =>
          val rawLeft        = IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector))
          val rawRight       = IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
          val rawAddend      = IEEE754.binary64ToDouble(sourceFloating64(srcC, lane, scalar, vector))
          val preparedLeft   = if (absA) math.abs(rawLeft) else rawLeft
          val preparedRight  = if (absB) math.abs(rawRight) else rawRight
          val preparedAddend = if (absC) math.abs(rawAddend) else rawAddend
          val left           = if (negateA) -preparedLeft else preparedLeft
          val right          = if (negateB) -preparedRight else preparedRight
          val addend         = if (negateC) -preparedAddend else preparedAddend
          val result         = Math.fma(left, right, addend)
          val bits           = IEEE754.doubleToBinary64(result)
          vector(dest)(lane) = Bits.low32(bits)
          vector(dest + 1)(lane) = Bits.high32(bits)
          if (traceF64 && lane == 0)
            scribe.debug(
              s"Gemstone AMDGPU f64 mad dest=$dest left=$left right=$right addend=$addend result=$result"
            )
        }
        AdvanceStep
      case DecodedInstruction.VUnaryF32Modified(opcode, dest, src, absolute, negate, clamp) =>
        foreachActive { lane =>
          val raw      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
          val prepared = if (absolute) math.abs(raw) else raw
          val value    = if (negate) -prepared else prepared
          val result   = opcode match {
            case "v_rcp_f32" => 1.0f / value
          }
          vector(dest)(lane) = IEEE754.floatToBinary32(if (clamp) clampF32(result) else result).value
        }
        AdvanceStep
      case DecodedInstruction.VUnaryF64Modified(opcode, dest, src, absolute, negate) =>
        foreachActive { lane =>
          val raw      = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
          val prepared = if (absolute) math.abs(raw) else raw
          val value    = if (negate) -prepared else prepared
          val mantissa =
            if (value == 0.0 || value.isNaN || value.isInfinite) value
            else Math.scalb(value, -Math.getExponent(value) - 1)
          val bits = IEEE754.doubleToBinary64(mantissa)
          vector(dest)(lane) = Bits.low32(bits)
          vector(dest + 1)(lane) = Bits.high32(bits)
          if (traceF64 && lane == 0)
            scribe.debug(s"Gemstone AMDGPU f64 unary opcode=$opcode dest=$dest raw=$raw result=$mantissa")
        }
        AdvanceStep
      case DecodedInstruction.VTrigPreopF64(dest, src, segment, absolute, negate) =>
        foreachActive { lane =>
          val raw      = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
          val prepared = if (absolute) math.abs(raw) else raw
          val value    = if (negate) -prepared else prepared
          val bits     = IEEE754.doubleToBinary64(value)
          val exponent = (bits >>> 52) & 0x7ffL
          var shift    = (source(segment, lane).toInt & 0x1f) * 53
          if (exponent > 1077L) shift += (exponent - 1077L).toInt
          val chunk = TwoOverPi1200.shiftLeft(shift).and(TrigPreopMask).doubleValue()
          var scale = -53 - shift
          if (exponent >= 1968L) scale += 128
          val result     = Math.scalb(chunk, scale)
          val resultBits = IEEE754.doubleToBinary64(result)
          vector(dest)(lane) = Bits.low32(resultBits)
          vector(dest + 1)(lane) = Bits.high32(resultBits)
        }
        AdvanceStep
      case DecodedInstruction.VBinaryF32Modified(
            opcode,
            dest,
            srcA,
            srcB,
            absA,
            absB,
            negateA,
            negateB,
            clamp
          ) =>
        foreachActive { lane =>
          val rawA = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcA, lane))))
          val rawB = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcB, lane))))
          // VOP3 source modifiers apply absolute value before negation.
          val preparedA = if (absA) math.abs(rawA) else rawA
          val preparedB = if (absB) math.abs(rawB) else rawB
          val valueA    = if (negateA) -preparedA else preparedA
          val valueB    = if (negateB) -preparedB else preparedB
          val result    = opcode match {
            case "v_add_f32"    => valueA + valueB
            case "v_sub_f32"    => valueA - valueB
            case "v_subrev_f32" => valueB - valueA
            case "v_mul_f32"    => valueA * valueB
            case "v_ldexp_f32"  => Math.scalb(valueA, source(srcB, lane).toInt)
            case "v_max_f32"    => math.max(valueA, valueB)
            case "v_min_f32"    => math.min(valueA, valueB)
          }
          vector(dest)(lane) = IEEE754.floatToBinary32(if (clamp) clampF32(result) else result).value
        }
        AdvanceStep
      case DecodedInstruction.VDivScaleF32(dest, conditionDest, srcA, srcB, srcC) =>
        val conditions                      = Array.fill(lanes.value)(false)
        def denormal(value: Float): Boolean =
          value != 0.0f && !value.isNaN && !value.isInfinite && math.abs(value) < java.lang.Float.MIN_NORMAL
        foreachActive { lane =>
          val s0     = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcA, lane))))
          val s1     = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcB, lane))))
          val s2     = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcC, lane))))
          var result = s0
          if (s1 == 0.0f || s2 == 0.0f) {
            result = Float.NaN
          } else if (Math.getExponent(s2) - Math.getExponent(s1) >= 96) {
            conditions(lane) = true
            if (s0 == s1) result = Math.scalb(s0, 64)
          } else if (denormal(s1)) {
            result = Math.scalb(s0, 64)
          } else if (denormal(1.0f / s1) && denormal(s2 / s1)) {
            conditions(lane) = true
            if (s0 == s1) result = Math.scalb(s0, 64)
          } else if (denormal(1.0f / s1)) {
            result = Math.scalb(s0, -64)
          } else if (denormal(s2 / s1)) {
            conditions(lane) = true
            if (s0 == s2) result = Math.scalb(s0, 64)
          } else if (Math.getExponent(s2) <= 23 - 127) {
            result = Math.scalb(s0, 64)
          }
          vector(dest)(lane) = IEEE754.floatToBinary32(result).value
          if (traceSqrt && lane == 0)
            scribe.debug(
              s"Gemstone AMDGPU sqrt divscale dest=$dest s0=$s0 s1=$s1 s2=$s2 result=$result condition=${conditions(lane)}"
            )
        }
        if (waveSize.value <= 32) writeScalar32(conditionDest, booleanLaneMask(conditions))
        else writeScalar64(conditionDest, booleanLaneMask(conditions))
        AdvanceStep
      case DecodedInstruction.VDivScaleF64(dest, conditionDest, srcA, srcB, srcC) =>
        val conditions                       = Array.fill(lanes.value)(false)
        def denormal(value: Double): Boolean =
          value != 0.0 && !value.isNaN && !value.isInfinite && math.abs(value) < java.lang.Double.MIN_NORMAL
        foreachActive { lane =>
          val s0     = IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector))
          val s1     = IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
          val s2     = IEEE754.binary64ToDouble(sourceFloating64(srcC, lane, scalar, vector))
          var result = s0
          if (s1 == 0.0 || s2 == 0.0) {
            result = Double.NaN
          } else if (Math.getExponent(s2) - Math.getExponent(s1) >= 768) {
            conditions(lane) = true
            if (s0 == s1) result = Math.scalb(s0, 128)
          } else if (denormal(s1)) {
            result = Math.scalb(s0, 128)
          } else if (denormal(1.0 / s1) && denormal(s2 / s1)) {
            conditions(lane) = true
            if (s0 == s1) result = Math.scalb(s0, 128)
          } else if (denormal(1.0 / s1)) {
            result = Math.scalb(s0, -128)
          } else if (denormal(s2 / s1)) {
            conditions(lane) = true
            if (s0 == s2) result = Math.scalb(s0, 128)
          } else if (Math.getExponent(s2) <= 53 - 1023) {
            result = Math.scalb(s0, 128)
          }
          val bits = IEEE754.doubleToBinary64(result)
          vector(dest)(lane) = Bits.low32(bits)
          vector(dest + 1)(lane) = Bits.high32(bits)
          if (traceF64 && lane == 0)
            scribe.debug(
              s"Gemstone AMDGPU f64 divscale dest=$dest s0=$s0 s1=$s1 s2=$s2 result=$result condition=${conditions(lane)}"
            )
        }
        if (waveSize.value <= 32) writeScalar32(conditionDest, booleanLaneMask(conditions))
        else writeScalar64(conditionDest, booleanLaneMask(conditions))
        AdvanceStep
      case DecodedInstruction.VDivFmasF64(dest, srcA, srcB, srcC) =>
        foreachActive { lane =>
          val left   = IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector))
          val right  = IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
          val addend = IEEE754.binary64ToDouble(sourceFloating64(srcC, lane, scalar, vector))
          val result = Math.fma(left, right, addend)
          val scaled = (laneMask(carry) & (1L << lane)) != 0L
          val bits   = IEEE754.doubleToBinary64(if (scaled) Math.scalb(result, 64) else result)
          vector(dest)(lane) = Bits.low32(bits)
          vector(dest + 1)(lane) = Bits.high32(bits)
          if (traceF64 && lane == 0)
            scribe.debug(
              s"Gemstone AMDGPU f64 divfmas dest=$dest left=$left right=$right addend=$addend result=$result scaled=$scaled"
            )
        }
        AdvanceStep
      case DecodedInstruction.VDivFixupF32(dest, quotient, denominator, numerator, absolute, negate) =>
        foreachActive { lane =>
          def operand(value: InstructionSource, index: Int): Float = {
            val raw      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(value, lane))))
            val prepared = if ((absolute & (1 << index)) != 0) math.abs(raw) else raw
            if ((negate & (1 << index)) != 0) -prepared else prepared
          }
          val q      = operand(quotient, 0)
          val d      = operand(denominator, 1)
          val n      = operand(numerator, 2)
          val result =
            if (n.isNaN || d.isNaN || (n.isInfinite && d.isInfinite) || (n == 0.0f && d == 0.0f)) Float.NaN
            else if (d == 0.0f || n.isInfinite) Math.copySign(
              Float.PositiveInfinity,
              if (((IEEE754.floatToBinary32(n).value ^ IEEE754.floatToBinary32(d).value) & 0x80000000L) != 0L) -1.0f
              else 1.0f
            )
            else if (d.isInfinite || n == 0.0f) Math.copySign(
              0.0f,
              if (((IEEE754.floatToBinary32(n).value ^ IEEE754.floatToBinary32(d).value) & 0x80000000L) != 0L) -1.0f
              else 1.0f
            )
            else q
          vector(dest)(lane) = IEEE754.floatToBinary32(result).value
          if (traceSqrt && lane == 0)
            scribe.debug(s"Gemstone AMDGPU sqrt divfixup dest=$dest q=$q d=$d n=$n result=$result")
        }
        AdvanceStep
      case DecodedInstruction.VDivFixupF64(dest, quotient, denominator, numerator, absolute, negate) =>
        foreachActive { lane =>
          def operand(value: InstructionSource, index: Int): Double = {
            val raw      = IEEE754.binary64ToDouble(sourceFloating64(value, lane, scalar, vector))
            val prepared = if ((absolute & (1 << index)) != 0) math.abs(raw) else raw
            if ((negate & (1 << index)) != 0) -prepared else prepared
          }
          val q    = operand(quotient, 0)
          val d    = operand(denominator, 1)
          val n    = operand(numerator, 2)
          val sign =
            if ((IEEE754.doubleToBinary64(n) ^ IEEE754.doubleToBinary64(d)) < 0L)
              -1.0
            else 1.0
          val result =
            if (n.isNaN || d.isNaN || (n.isInfinite && d.isInfinite) || (n == 0.0 && d == 0.0)) Double.NaN
            else if (d == 0.0 || n.isInfinite) Math.copySign(Double.PositiveInfinity, sign)
            else if (d.isInfinite || n == 0.0) Math.copySign(0.0, sign)
            else q
          val bits = IEEE754.doubleToBinary64(result)
          vector(dest)(lane) = Bits.low32(bits)
          vector(dest + 1)(lane) = Bits.high32(bits)
          if (traceF64 && lane == 0)
            scribe.debug(s"Gemstone AMDGPU f64 divfixup dest=$dest q=$q d=$d n=$n result=$result")
        }
        AdvanceStep
      case DecodedInstruction.VBinary(opcode, dest, srcA, srcB, clamp) =>
        foreachActive { lane =>
          val left     = Bits.low32(source(srcA, lane))
          val right    = Bits.low32(source(srcB, lane))
          val previous = Bits.low32(vector(dest)(lane))
          opcode match {
            case "v_add_u32" =>
              vector(dest)(lane) = if (clamp) math.min(Bits.Mask32, left + right) else Bits.low32(left + right)
            case "v_mul_lo_u32"  => vector(dest)(lane) = Bits.low32(left * right)
            case "v_mul_hi_u32"  => vector(dest)(lane) = unsignedMultiplyHigh32(left, right)
            case "v_mul_hi_i32"  => vector(dest)(lane) = signedMultiplyHigh32(left, right)
            case "v_ashrrev_i32" =>
              vector(dest)(lane) = Bits.unsigned32(right.toInt >> (left.toInt & 31))
            case "v_ashrrev_i16" =>
              val signed = (Bits.low16(right).toInt << 16) >> 16
              vector(dest)(lane) = Bits.low16(signed >> (left.toInt & 15))
            case "v_lshlrev_b32" => vector(dest)(lane) = Bits.low32(right << (left.toInt & 31))
            case "v_lshrrev_b32" => vector(dest)(lane) = right >>> (left.toInt & 31)
            case "v_lshrrev_b16" => vector(dest)(lane) = Bits.low16(right >>> (left.toInt & 15))
            case "v_add_u16"     => vector(dest)(lane) = Bits.low16(left + right)
            case "v_and_b32"     => vector(dest)(lane) = left & right
            case "v_or_b32"      => vector(dest)(lane) = left | right
            case "v_xor_b32"     => vector(dest)(lane) = left ^ right
            case "v_lshlrev_b16" => vector(dest)(lane) = Bits.low16(Bits.low16(right) << (left.toInt & 15))
            case "v_sub_u16"     => vector(dest)(lane) = Bits.low16(left - right)
            case "v_sub_u32"     =>
              vector(dest)(lane) = if (clamp) math.max(0L, left - right) else Bits.low32(left - right)
            case "v_subrev_u32" =>
              vector(dest)(lane) =
                if (clamp) math.max(0L, right - left) else unsignedSubtractReverse32(left, right)
            case "v_subrev_f32" => vector(dest)(lane) =
                IEEE754.floatToBinary32(IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(right))
                ) - IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left)))).value
            case "v_mul_u32_u24"    => vector(dest)(lane) = unsignedMultiply24(left, right)
            case "v_mul_hi_u32_u24" => vector(dest)(lane) = unsignedMultiplyHigh24(left, right)
            case "v_mul_i32_i24"    => vector(dest)(lane) = signedMultiply24(left, right)
            case "v_mul_hi_i32_i24" => vector(dest)(lane) = signedMultiplyHigh24(left, right)
            case "v_mul_lo_u16"     => vector(dest)(lane) = Bits.low16(left * right)
            case "v_min_u32"        => vector(dest)(lane) = math.min(left, right)
            case "v_max_u32"        => vector(dest)(lane) = math.max(left, right)
            case "v_min_i32"        => vector(dest)(lane) = Bits.low32(math.min(left.toInt, right.toInt).toLong)
            case "v_min_i16"        => vector(dest)(lane) =
                Bits.low16(math.min(
                  Bits.signExtend(Bits.low16(left), BitWidth(16)),
                  Bits.signExtend(Bits.low16(right), BitWidth(16))
                ))
            case "v_min_u16" => vector(dest)(lane) = Bits.low16(math.min(Bits.low16(left), Bits.low16(right)))
            case "v_min_f32" =>
              vector(dest)(lane) =
                IEEE754.floatToBinary32(math.min(
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))),
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))
                )).value
            case "v_min_f16" => vector(dest)(lane) = minimumF16(left, right).value
            case "v_max_i32" => vector(dest)(lane) = math.max(left.toInt, right.toInt).toLong & Bits.Mask32
            case "v_max_i16" => vector(dest)(lane) =
                Bits.low16(math.max(
                  Bits.signExtend(Bits.low16(left), BitWidth(16)),
                  Bits.signExtend(Bits.low16(right), BitWidth(16))
                ))
            case "v_max_u16" => vector(dest)(lane) = Bits.low16(math.max(Bits.low16(left), Bits.low16(right)))
            case "v_max_f32" =>
              vector(dest)(lane) =
                IEEE754.floatToBinary32(math.max(
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))),
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))
                )).value
            case "v_max_f16" =>
              vector(dest)(lane) = IEEE754.floatToBinary16(
                math.max(
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))),
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right)))
                )
              ).value
            case "v_ldexp_f32" =>
              vector(dest)(lane) =
                IEEE754.floatToBinary32(Math.scalb(
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))),
                  right.toInt
                )).value
            case "v_add_f32" =>
              val result = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) + IEEE754.binary32ToFloat(
                Binary32.fromLow(Bits.low32(right))
              )
              vector(dest)(lane) = IEEE754.floatToBinary32(result).value
              if (traceLog && lane == 0)
                scribe.debug(
                  s"Gemstone AMDGPU log binary opcode=$opcode dest=$dest left=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left)))} right=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))} result=$result"
                )
            case "v_mul_f32" =>
              val result = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) * IEEE754.binary32ToFloat(
                Binary32.fromLow(Bits.low32(right))
              )
              vector(dest)(lane) = IEEE754.floatToBinary32(result).value
              if (traceLog && lane == 0)
                scribe.debug(
                  s"Gemstone AMDGPU log binary opcode=$opcode dest=$dest left=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left)))} right=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))} result=$result"
                )
            case "v_mac_f32" =>
              val result = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) * IEEE754.binary32ToFloat(
                Binary32.fromLow(Bits.low32(right))
              ) +
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(previous)))
              vector(dest)(lane) = IEEE754.floatToBinary32(result).value
            case "v_add_f16" =>
              val result =
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) + IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(right))
                )
              vector(dest)(lane) = IEEE754.floatToBinary16(result).value
            case "v_mul_f16" =>
              val result =
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) * IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(right))
                )
              vector(dest)(lane) = IEEE754.floatToBinary16(result).value
            case "v_fmac_f16" =>
              vector(dest)(lane) = IEEE754.floatToBinary16(
                Math.fma(
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))),
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right))),
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(previous)))
                )
              ).value
            case "v_sub_f32" =>
              val result = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) - IEEE754.binary32ToFloat(
                Binary32.fromLow(Bits.low32(right))
              )
              vector(dest)(lane) = IEEE754.floatToBinary32(result).value
              if (traceLog && lane == 0)
                scribe.debug(
                  s"Gemstone AMDGPU log binary opcode=$opcode dest=$dest left=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left)))} right=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))} result=$result"
                )
            case "v_add_co_u32" =>
              val sum = left + right
              vector(dest)(lane) = Bits.low32(sum)
              carry(lane) = sum > Bits.Mask32
            case "v_addc_co_u32" =>
              val sum = left + right + (if (carry(lane)) 1L else 0L)
              vector(dest)(lane) = Bits.low32(sum)
              carry(lane) = sum > Bits.Mask32
            case "v_sub_co_u32" =>
              val (difference, borrow) = unsignedSubtract32(left, right)
              vector(dest)(lane) = difference
              carry(lane) = borrow
            case "v_subb_co_u32" =>
              val (difference, borrow) = unsignedSubtractWithBorrow32(left, right, carry(lane))
              vector(dest)(lane) = difference
              carry(lane) = borrow
          }
          if (traceByteSort && lane == 0 && index >= 540 && index <= 620)
            scribe.debug(
              s"Gemstone AMDGPU byte-sort VBinary index=$index opcode=$opcode dest=$dest left=0x${left.toHexString} right=0x${right.toHexString} result=0x${vector(dest)(lane).toHexString}"
            )
          if (
            AMDGPUEnvironment.trace("reduce") &&
            kernel.name.contains("wrapped_reduce") && lane == 0 &&
            opcode == "v_add_u32"
          )
            scribe.debug(
              s"Gemstone AMDGPU reduce add index=$index dest=$dest left=0x${left.toHexString} right=0x${right.toHexString} result=0x${vector(dest)(lane).toHexString} localStart=$localStart decoded=${operations(index)}"
            )
        }
        AdvanceStep
      case DecodedInstruction.VDot2F32F16(dest, srcA, srcB, accumulate) =>
        foreachActive { lane =>
          vector(dest)(lane) = dot2F32F16(
            Bits.low32(source(srcA, lane)),
            Bits.low32(source(srcB, lane)),
            vector(dest)(lane),
            accumulate
          ).value
        }
        AdvanceStep
      case DecodedInstruction.VDot4U32U8(dest, srcA, srcB, srcC) =>
        foreachActive { lane =>
          vector(dest)(lane) = dot4U32U8(
            source(srcA, lane),
            source(srcB, lane),
            source(srcC, lane)
          )
        }
        AdvanceStep
      case DecodedInstruction.VBinary64(opcode, dest, srcA, srcB) =>
        foreachActive { lane =>
          val value    = IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector))
          val exponent = source(srcB, lane).toInt
          val result   = opcode match {
            case "v_ldexp_f64"                 => Math.scalb(value, exponent)
            case "v_mul_f64" | "v_mul_f64_e64" =>
              value * IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
          }
          val bits = IEEE754.doubleToBinary64(result)
          vector(dest)(lane) = Bits.low32(bits)
          vector(dest + 1)(lane) = Bits.high32(bits)
          if (traceF64 && lane == 0)
            scribe.debug(
              s"Gemstone AMDGPU f64 binary opcode=$opcode dest=$dest value=$value result=$result"
            )
        }
        AdvanceStep
      case DecodedInstruction.VBinary64Modified(opcode, dest, srcA, srcB, absA, absB, negateA, negateB) =>
        foreachActive { lane =>
          val rawA      = IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector))
          val rawB      = IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
          val preparedA = if (absA) math.abs(rawA) else rawA
          val preparedB = if (absB) math.abs(rawB) else rawB
          val valueA    = if (negateA) -preparedA else preparedA
          val valueB    = if (negateB) -preparedB else preparedB
          val result    = opcode match {
            case "v_ldexp_f64"                 => Math.scalb(valueA, source(srcB, lane).toInt)
            case "v_add_f64" | "v_add_f64_e64" => valueA + valueB
            case "v_mul_f64" | "v_mul_f64_e64" => valueA * valueB
            case "v_max_f64" | "v_max_f64_e64" => math.max(valueA, valueB)
            case "v_min_f64" | "v_min_f64_e64" => math.min(valueA, valueB)
          }
          val bits = IEEE754.doubleToBinary64(result)
          vector(dest)(lane) = Bits.low32(bits)
          vector(dest + 1)(lane) = Bits.high32(bits)
          if (traceF64 && lane == 0)
            scribe.debug(
              s"Gemstone AMDGPU f64 binary-mod opcode=$opcode dest=$dest rawA=$rawA rawB=$rawB result=$result"
            )
        }
        AdvanceStep
      case DecodedInstruction.VBinarySubDword(
            opcode,
            dest,
            srcA,
            srcB,
            destSelection,
            destUnused,
            srcASelection,
            srcBSelection,
            srcASignExtend,
            srcBSignExtend
          ) =>
        foreachActive { lane =>
          val left   = selectSubDword(source(srcA, lane), srcASelection, srcASignExtend)
          val right  = selectSubDword(source(srcB, lane), srcBSelection, srcBSignExtend)
          val result = opcode match {
            case "v_cndmask_b32" =>
              val conditionMask = Bits.low32(source(InstructionSource.Scalar(106), 0))
              if ((conditionMask & (1L << lane)) != 0L) right else left
            case "v_add_u32" | "v_add_nc_u32" => Bits.low32(left + right)
            case "v_add_u16"                  => Bits.low16(left + right)
            case "v_add_f16"                  =>
              IEEE754.floatToBinary16(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) + IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(right))
                )
              ).value
            case "v_add_co_u32" =>
              val sum = left + right
              carry(lane) = sum > Bits.Mask32
              Bits.low32(sum)
            case "v_addc_co_u32" =>
              val sum = left + right + (if (carry(lane)) 1L else 0L)
              carry(lane) = sum > Bits.Mask32
              Bits.low32(sum)
            case "v_and_b32"     => left & right
            case "v_lshlrev_b16" => Bits.low16(Bits.low16(right) << (left.toInt & 15))
            case "v_lshlrev_b32" => Bits.low32(right << (left.toInt & 31))
            case "v_lshrrev_b32" => right >>> (left.toInt & 31)
            case "v_lshrrev_b16" => Bits.low16(right >>> (left.toInt & 15))
            case "v_max_i16"     => Bits.low16(math.max(left, right))
            case "v_max_f16"     =>
              IEEE754.floatToBinary16(
                math.max(
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))),
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right)))
                )
              ).value
            case "v_min_i16"    => Bits.low16(math.min(left, right))
            case "v_min_f16"    => minimumF16(left, right).value
            case "v_min_u16"    => Bits.low16(math.min(Bits.low16(left), Bits.low16(right)))
            case "v_mul_lo_u16" => Bits.low16(left * right)
            case "v_mul_f16"    =>
              val result =
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) * IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(right))
                )
              IEEE754.floatToBinary16(result).value
            case "v_mul_u32_u24" => unsignedMultiply24(left, right)
            case "v_mul_i32_i24" => signedMultiply24(left, right)
            case "v_or_b32"      => left | right
            case "v_xor_b32"     => left ^ right
            case "v_sub_u16"     => Bits.low16(left - right)
            case "v_sub_nc_u32"  => Bits.low32(left - right)
            case "v_sub_co_u32"  =>
              val (difference, borrow) = unsignedSubtract32(left, right)
              carry(lane) = borrow
              difference
            case "v_subb_co_u32" =>
              val (difference, borrow) = unsignedSubtractWithBorrow32(left, right, carry(lane))
              carry(lane) = borrow
              difference
            case "v_subrev_co_u32" =>
              val (difference, borrow) = unsignedSubtract32(right, left)
              carry(lane) = borrow
              difference
            case "v_subbrev_co_u32" =>
              val (difference, borrow) = unsignedSubtractWithBorrow32(right, left, carry(lane))
              carry(lane) = borrow
              difference
          }
          vector(dest)(lane) = placeSubDword(vector(dest)(lane), result, destSelection, destUnused)
        }
        AdvanceStep
      case DecodedInstruction.VPackedBinary(opcode, dest, srcA, srcB, srcAHighForHigh, srcBHighForHigh) =>
        foreachActive { lane =>
          val left                              = Bits.low32(source(srcA, lane))
          val right                             = Bits.low32(source(srcB, lane))
          def calculate(a: Long, b: Long): Long = opcode match {
            case "v_pk_add_u16" => Bits.low16(a + b)
            case "v_pk_add_f16" =>
              Bits.low16(IEEE754.floatToBinary16(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(a))) + IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(b))
                )
              ).value)
            case "v_pk_mul_lo_u16" => Bits.low16(a * b)
            case "v_pk_mul_f16"    =>
              Bits.low16(IEEE754.floatToBinary16(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(a))) * IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(b))
                )
              ).value)
            case "v_pk_max_f16" =>
              Bits.low16(IEEE754.floatToBinary16(math.max(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(a))),
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(b)))
              )).value)
            case "v_pk_min_f16" =>
              Bits.low16(IEEE754.floatToBinary16(math.min(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(a))),
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(b)))
              )).value)
            case "v_pk_max_i16" =>
              if (Bits.signExtend(Bits.low16(a), BitWidth(16)) >= Bits.signExtend(Bits.low16(b), BitWidth(16)))
                Bits.low16(a)
              else Bits.low16(b)
            case "v_pk_max_u16"     => math.max(Bits.low16(a), Bits.low16(b))
            case "v_pk_sub_i16"     => Bits.low16(a - b)
            case "v_pk_lshrrev_b16" => Bits.low16(b >>> (a.toInt & 15))
          }
          val low  = calculate(Bits.low16(left), Bits.low16(right))
          val high = calculate(
            if (srcAHighForHigh) left >>> 16 else Bits.low16(left),
            if (srcBHighForHigh) right >>> 16 else Bits.low16(right)
          )
          vector(dest)(lane) = low | (high << 16)
        }
        AdvanceStep
      case DecodedInstruction.VPackB32F16(dest, srcA, srcB) =>
        foreachActive { lane =>
          val low  = IEEE754.floatToBinary16(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcA, lane)))))
          val high = IEEE754.floatToBinary16(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcB, lane)))))
          vector(dest)(lane) = low.value | (high.value << 16)
        }
        AdvanceStep
      case DecodedInstruction.VPackedFmaF16(
            dest,
            srcA,
            srcB,
            srcC,
            lowSelection,
            highSelection,
            negateLow,
            negateHigh,
            clamp
          ) =>
        foreachActive { lane =>
          vector(dest)(lane) = packedFmaF16(
            Bits.low32(source(srcA, lane)),
            Bits.low32(source(srcB, lane)),
            Bits.low32(source(srcC, lane)),
            lowSelection,
            highSelection,
            negateLow,
            negateHigh,
            clamp
          )
        }
        AdvanceStep
      case DecodedInstruction.VFmaMixF16(
            dest,
            srcA,
            srcB,
            srcC,
            opSelection,
            opSelectionHi,
            negate,
            absolute,
            clamp,
            high
          ) =>
        foreachActive { lane =>
          vector(dest)(lane) = fmaMixF16(
            vector(dest)(lane),
            Bits.low32(source(srcA, lane)),
            Bits.low32(source(srcB, lane)),
            Bits.low32(source(srcC, lane)),
            opSelection,
            opSelectionHi,
            negate,
            absolute,
            clamp,
            high
          )
        }
        AdvanceStep
      case DecodedInstruction.VFmaMixF32(
            dest,
            srcA,
            srcB,
            srcC,
            opSelection,
            opSelectionHi,
            negate,
            absolute,
            clamp
          ) =>
        foreachActive { lane =>
          vector(dest)(lane) = fmaMixF32(
            Bits.low32(source(srcA, lane)),
            Bits.low32(source(srcB, lane)),
            Bits.low32(source(srcC, lane)),
            opSelection,
            opSelectionHi,
            negate,
            absolute,
            clamp
          )
        }
        AdvanceStep
      case DecodedInstruction.VAddCarry(dest, carryDest, srcA, srcB, carryIn) =>
        val outputCarry = Array.fill(lanes.value)(false)
        foreachActive { lane =>
          val left     = Bits.low32(source(srcA, lane))
          val right    = Bits.low32(source(srcB, lane))
          val carryBit = carryIn.exists(mask => (sourceScalar64(mask) & (1L << lane)) != 0L)
          val sum      = left + right + (if (carryBit) 1L else 0L)
          vector(dest)(lane) = Bits.low32(sum)
          outputCarry(lane) = sum > Bits.Mask32
        }
        val carryMask = booleanLaneMask(outputCarry)
        updateLaneMask(carryDest, carryMask)
        AdvanceStep
      case DecodedInstruction.VSubCarry(dest, carryDest, srcA, srcB, carryIn) =>
        val outputCarry = Array.fill(lanes.value)(false)
        foreachActive { lane =>
          val left                 = Bits.low32(source(srcA, lane))
          val right                = Bits.low32(source(srcB, lane))
          val borrowIn             = carryIn.exists(mask => (sourceScalar64(mask) & (1L << lane)) != 0L)
          val (difference, borrow) =
            if (carryIn.isDefined) unsignedSubtractWithBorrow32(left, right, borrowIn)
            else unsignedSubtract32(left, right)
          vector(dest)(lane) = difference
          outputCarry(lane) = borrow
        }
        val carryMask = booleanLaneMask(outputCarry)
        updateLaneMask(carryDest, carryMask)
        AdvanceStep
      case DecodedInstruction.VSubReverseCarry(dest, carryDest, srcA, srcB, carryIn) =>
        val outputCarry = Array.fill(lanes.value)(false)
        foreachActive { lane =>
          val left                 = Bits.low32(source(srcA, lane))
          val right                = Bits.low32(source(srcB, lane))
          val borrowIn             = carryIn.exists(mask => (sourceScalar64(mask) & (1L << lane)) != 0L)
          val (difference, borrow) =
            if (carryIn.isDefined) unsignedSubtractWithBorrow32(right, left, borrowIn)
            else unsignedSubtract32(right, left)
          vector(dest)(lane) = difference
          outputCarry(lane) = borrow
        }
        val carryMask = booleanLaneMask(outputCarry)
        updateLaneMask(carryDest, carryMask)
        AdvanceStep
      case DecodedInstruction.VTernary(opcode, dest, srcA, srcB, srcC) =>
        foreachActive { lane =>
          val left   = Bits.low32(source(srcA, lane))
          val right  = Bits.low32(source(srcB, lane))
          val addend = Bits.low32(source(srcC, lane))
          vector(dest)(lane) = Bits.low32(
            opcode match {
              case "v_add3_u32" => left + right + addend
              case "v_or3_b32"  => left | right | addend
              case "v_xor3_b32" => left ^ right ^ addend
              case "v_max3_i32" => math.max(math.max(left.toInt, right.toInt), addend.toInt).toLong
              case "v_max3_u32" => math.max(math.max(left, right), addend)
              case "v_min3_i32" => math.min(math.min(left.toInt, right.toInt), addend.toInt).toLong
              case "v_min3_f32" => IEEE754.floatToBinary32(math.min(
                  math.min(
                    IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))),
                    IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))
                  ),
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(addend)))
                )).value
              case "v_min3_u32" => math.min(math.min(left, right), addend)
              case "v_med3_i32" =>
                val a = left.toInt
                val b = right.toInt
                val c = addend.toInt
                math.max(math.min(a, b), math.min(math.max(a, b), c)).toLong
              case "v_max3_i16" => Bits.low16(math.max(
                  math.max(
                    Bits.signExtend(Bits.low16(left), BitWidth(16)),
                    Bits.signExtend(Bits.low16(right), BitWidth(16))
                  ),
                  Bits.signExtend(Bits.low16(addend), BitWidth(16))
                ))
              case "v_max3_u16" =>
                Bits.low16(math.max(math.max(Bits.low16(left), Bits.low16(right)), Bits.low16(addend)))
              case "v_min3_i16" => Bits.low16(math.min(
                  math.min(
                    Bits.signExtend(Bits.low16(left), BitWidth(16)),
                    Bits.signExtend(Bits.low16(right), BitWidth(16))
                  ),
                  Bits.signExtend(Bits.low16(addend), BitWidth(16))
                ))
              case "v_min3_u16" =>
                Bits.low16(math.min(math.min(Bits.low16(left), Bits.low16(right)), Bits.low16(addend)))
              case "v_mad_u16" => Bits.low16(Bits.low16(left) * Bits.low16(right) + Bits.low16(addend))
              case "v_fma_f32" => IEEE754.floatToBinary32(
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) * IEEE754.binary32ToFloat(
                    Binary32.fromLow(Bits.low32(right))
                  ) + IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(
                    addend
                  )))
                ).value
              case "v_fma_f16" =>
                IEEE754.floatToBinary16(
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) * IEEE754.binary16ToFloat(
                    Binary16.fromLow(Bits.low16(right))
                  ) +
                    IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(addend)))
                ).value
              case "v_div_fmas_f32" =>
                val result =
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) * IEEE754.binary32ToFloat(
                    Binary32.fromLow(Bits.low32(right))
                  ) + IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(
                    addend
                  )))
                val scaled      = (laneMask(carry) & (1L << lane)) != 0L
                val finalResult = if (scaled) Math.scalb(result, 32) else result
                if (traceSqrt && lane == 0)
                  scribe.debug(
                    s"Gemstone AMDGPU sqrt divfmas dest=$dest left=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left)))} right=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))} addend=${IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(addend)))} result=$finalResult scaled=$scaled"
                  )
                IEEE754.floatToBinary32(finalResult).value
              case "v_mad_legacy_u16" => Bits.low16(left) * Bits.low16(right) + Bits.low16(addend)
              case "v_mad_u32_u24"    => unsignedMultiplyAdd24(left, right, addend)
              case "v_mad_i32_i24"    => Bits.low32(signedMultiply24(left, right) + addend)
              // ALIGNBIT treats SRC0 as the high dword and SRC1 as
              // the low dword of the 64-bit shift window.  join32
              // takes its low word first.
              case "v_alignbit_b32" =>
                val result = Bits.join32(right, left) >>> (addend.toInt & 31)
                result
              case "v_bfi_b32"    => (left & right) | (~left & addend)
              case "v_and_or_b32" => (left & right) | addend
              case "v_bfe_i32"    =>
                val width = addend.toInt & 31
                if (width == 0) 0L
                else {
                  val mask = (1L << width) - 1L
                  Bits.low32(Bits.signExtend((left >>> (right.toInt & 31)) & mask, BitWidth.assume(width)))
                }
              case "v_bfe_u32" =>
                val width = addend.toInt & 31
                if (width == 0) 0L else Bits.low32(left >>> (right.toInt & 31)) & ((1L << width) - 1L)
              case "v_xad_u32"      => Bits.low32((left ^ right) + addend)
              case "v_add_lshl_u32" => (left + right) << (addend.toInt & 31)
              case "v_lshl_add_u32" => (left << (right.toInt & 31)) + addend
              case "v_lshl_or_b32"  => (left << (right.toInt & 31)) | addend
              case "v_perm_b32"     => permuteBytes(left, right, addend)
            }
          )
        }
        AdvanceStep
      case DecodedInstruction.VMadU64U32(dest, carryDest, srcA, srcB, srcC) =>
        val madCarry = Array.fill(lanes.value)(false)
        foreachActive { lane =>
          val rawA    = source(srcA, lane)
          val rawB    = source(srcB, lane)
          val addend  = source64(srcC, lane, scalar, vector)
          val product = Bits.low32(rawA) * Bits.low32(rawB)
          val sum     = product + addend
          vector(dest)(lane) = Bits.low32(sum)
          vector(dest + 1)(lane) = Bits.high32(sum)
          madCarry(lane) = java.lang.Long.compareUnsigned(sum, product) < 0
          if (
            AMDGPUEnvironment.trace("merge-rank-reads") &&
            lane == 0 && localStart == 0 && kernel.name.contains("wrapped_merge_sort") &&
            Set(1557, 1563).contains(index)
          )
            scribe.debug(
              s"Gemstone AMDGPU merge rank mad index=$index dest=$dest carryDest=$carryDest srcA=$srcA srcB=$srcB srcC=$srcC rawA=0x${rawA.toHexString} rawB=0x${rawB.toHexString} addend=0x${addend.toHexString} result=0x${sum.toHexString} carry=${madCarry(lane)}"
            )
        }
        updateLaneMask(carryDest, booleanLaneMask(madCarry))
        AdvanceStep
      case DecodedInstruction.VMadI64I32(dest, carryDest, srcA, srcB, srcC) =>
        val madCarry = Array.fill(lanes.value)(false)
        foreachActive { lane =>
          val rawA    = source(srcA, lane)
          val rawB    = source(srcB, lane)
          val addend  = source64(srcC, lane, scalar, vector)
          val left    = Bits.signExtend(Bits.low32(rawA), BitWidth(32))
          val right   = Bits.signExtend(Bits.low32(rawB), BitWidth(32))
          val product = left * right
          val sum     = product + addend
          vector(dest)(lane) = Bits.low32(sum)
          vector(dest + 1)(lane) = Bits.high32(sum)
          // The carry result is the carry out of the two-word addition,
          // using the two's-complement bit representation of the signed
          // product, just as the hardware's vcc_out does.
          madCarry(lane) = java.lang.Long.compareUnsigned(sum, product) < 0
        }
        updateLaneMask(carryDest, booleanLaneMask(madCarry))
        AdvanceStep
      case DecodedInstruction.VCompare(opcode, srcA, srcB) =>
        val comparisonOpcode = opcode.replace("v_cmpx_", "v_cmp_")
        val compareAndExec   = opcode.startsWith("v_cmpx_")
        var resultMask       = 0L
        foreachLane { lane =>
          if (active(lane)) {
            val leftLow  = Bits.low32(source(srcA, lane))
            val rightLow = Bits.low32(source(srcB, lane))
            def left64   = source64(srcA, lane, scalar, vector)
            def right64  = source64(srcB, lane, scalar, vector)
            val compared = comparisonOpcode match {
              case "v_cmp_eq_u32" => leftLow == rightLow
              case "v_cmp_eq_u64" => left64 == right64
              case "v_cmp_eq_f32" => IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(leftLow))
                ) == IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(rightLow)))
              case "v_cmp_eq_f16" | "v_cmp_neq_f16" | "v_cmp_lg_f16" | "v_cmp_lt_f16" |
                  "v_cmp_le_f16" | "v_cmp_gt_f16" | "v_cmp_ge_f16" | "v_cmp_nlt_f16" |
                  "v_cmp_nle_f16" | "v_cmp_ngt_f16" | "v_cmp_nge_f16" | "v_cmp_o_f16" | "v_cmp_u_f16" =>
                compareF16(opcode, leftLow, rightLow)
              case "v_cmp_class_f32" =>
                (floatClassMask(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow)))) & rightLow) != 0L
              case "v_cmp_class_f64" =>
                (doubleClassMask(IEEE754.binary64ToDouble(sourceFloating64(
                  srcA,
                  lane,
                  scalar,
                  vector
                ))) & rightLow) != 0L
              case "v_cmp_eq_i16" => Bits.low16(leftLow) == Bits.low16(rightLow)
              case "v_cmp_eq_u16" => Bits.low16(leftLow) == Bits.low16(rightLow)
              case "v_cmp_lg_u16" => Bits.low16(leftLow) != Bits.low16(rightLow)
              case "v_cmp_lg_i16" => Bits.low16(leftLow) != Bits.low16(rightLow)
              case "v_cmp_lg_u32" => leftLow != rightLow
              case "v_cmp_lg_u64" => left64 != right64
              case "v_cmp_eq_f64" =>
                IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) ==
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
              case "v_cmp_neq_f64" =>
                IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) !=
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
              case "v_cmp_o_f64" =>
                val left  = IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector))
                val right = IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
                !left.isNaN && !right.isNaN
              case "v_cmp_ngt_f64" =>
                !(IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) >
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector)))
              case "v_cmp_nge_f64" =>
                !(IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) >=
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector)))
              case "v_cmp_nlg_f64" =>
                !(IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) <
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))) &&
                !(IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) >
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector)))
              case "v_cmp_nle_f64" =>
                !(IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) <=
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector)))
              case "v_cmp_neq_f32" =>
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))) != IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                )
              case "v_cmp_lg_f32" =>
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))) != IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                )
              case "v_cmp_u_f32" =>
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))).isNaN || IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                ).isNaN
              case "v_cmp_gt_f64" =>
                IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) >
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
              case "v_cmp_ge_f64" =>
                IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) >=
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
              case "v_cmp_le_f64" =>
                IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) <=
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
              case "v_cmp_lg_f64" =>
                IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) !=
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
              case "v_cmp_u_f64" =>
                val left  = IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector))
                val right = IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
                left.isNaN || right.isNaN
              case "v_cmp_lt_f32" =>
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))) < IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                )
              case "v_cmp_gt_f32" =>
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))) > IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                )
              case "v_cmp_ge_f32" =>
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))) >= IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                )
              case "v_cmp_le_f32" =>
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))) <= IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                )
              case "v_cmp_ngt_f32" =>
                !(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))) > IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                ))
              case "v_cmp_nge_f32" =>
                !(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))) >= IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                ))
              case "v_cmp_lt_f64" =>
                IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) <
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
              case "v_cmp_nlt_f64" =>
                !(IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector)) <
                  IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector)))
              case "v_cmp_gt_i16" =>
                Bits.signExtend(Bits.low16(leftLow), BitWidth(16)) > Bits.signExtend(Bits.low16(rightLow), BitWidth(16))
              case "v_cmp_gt_u16" => Bits.low16(leftLow) > Bits.low16(rightLow)
              case "v_cmp_le_u16" => Bits.low16(leftLow) <= Bits.low16(rightLow)
              case "v_cmp_ge_i16" =>
                Bits.signExtend(
                  Bits.low16(leftLow),
                  BitWidth(16)
                ) >= Bits.signExtend(Bits.low16(rightLow), BitWidth(16))
              case "v_cmp_ge_u16" => Bits.low16(leftLow) >= Bits.low16(rightLow)
              case "v_cmp_lt_i16" =>
                Bits.signExtend(Bits.low16(leftLow), BitWidth(16)) < Bits.signExtend(Bits.low16(rightLow), BitWidth(16))
              case "v_cmp_lt_u16" => Bits.low16(leftLow) < Bits.low16(rightLow)
              case "v_cmp_le_i16" =>
                Bits.signExtend(
                  Bits.low16(leftLow),
                  BitWidth(16)
                ) <= Bits.signExtend(Bits.low16(rightLow), BitWidth(16))
              case "v_cmp_nlt_f32" =>
                !(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))) < IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                ))
              case "v_cmp_nle_f32" =>
                !(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))) <= IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                ))
              case "v_cmp_o_f32" =>
                !IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftLow))).isNaN && !IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(rightLow))
                ).isNaN
              case "v_cmp_ne_u32" => leftLow != rightLow
              case "v_cmp_ne_u16" => Bits.low16(leftLow) != Bits.low16(rightLow)
              case "v_cmp_ne_u64" => left64 != right64
              case "v_cmp_ge_u32" => leftLow >= rightLow
              case "v_cmp_ge_u64" => java.lang.Long.compareUnsigned(left64, right64) >= 0
              case "v_cmp_gt_i32" => leftLow.toInt > rightLow.toInt
              case "v_cmp_ge_i32" => leftLow.toInt >= rightLow.toInt
              case "v_cmp_gt_i64" => left64 > right64
              case "v_cmp_ge_i64" => left64 >= right64
              case "v_cmp_gt_u32" => leftLow > rightLow
              case "v_cmp_gt_u64" => java.lang.Long.compareUnsigned(left64, right64) > 0
              case "v_cmp_le_i32" => leftLow.toInt <= rightLow.toInt
              case "v_cmp_le_i64" => left64 <= right64
              case "v_cmp_le_u32" => leftLow <= rightLow
              case "v_cmp_le_u64" => java.lang.Long.compareUnsigned(left64, right64) <= 0
              case "v_cmp_lt_i32" => leftLow.toInt < rightLow.toInt
              case "v_cmp_lt_i64" => left64 < right64
              case "v_cmp_lt_u32" => leftLow < rightLow
              case "v_cmp_lt_u64" => java.lang.Long.compareUnsigned(left64, right64) < 0
            }
            if (compared) resultMask |= 1L << lane
            if (traceByteSortCompare && lane == 0)
              scribe.debug(
                s"Gemstone AMDGPU byte-sort compare index=$index opcode=$opcode srcA=$srcA srcB=$srcB left=0x${source(srcA, lane).toHexString} right=0x${source(srcB, lane).toHexString} result=$compared active=${active(lane)}"
              )
          } else {
            // Inactive lanes contribute zero to the complete mask.
          }
        }
        if (traceByteSortCompare)
          scribe.debug(
            s"Gemstone AMDGPU byte-sort compare-mask index=$index opcode=$opcode mask=0x${resultMask.toHexString}"
          )
        // Both compare forms materialize a complete result mask.
        if (!compareAndExec) carry.bits = resultMask
        if (compareAndExec) active.bits = resultMask
        if (traceControl && kernel.name.contains("fasten_main"))
          scribe.debug(
            s"Gemstone AMDGPU miniBUDE VCompare index=$index opcode=$opcode mask=0x${resultMask.toHexString} active=0x${laneMask(active).toHexString}"
          )
        AdvanceStep
      case DecodedInstruction.VCompareSubDword(
            opcode,
            srcA,
            srcB,
            srcASelection,
            srcBSelection,
            srcASignExtend,
            srcBSignExtend,
            destination
          ) =>
        val result = Array.fill(lanes.value)(false)
        foreachActive { lane =>
          val left  = selectSubDword(source(srcA, lane), srcASelection, srcASignExtend)
          val right = selectSubDword(source(srcB, lane), srcBSelection, srcBSignExtend)
          result(lane) = opcode match {
            case "v_cmp_eq_u32" => Bits.low32(left) == Bits.low32(right)
            case "v_cmp_eq_f16" | "v_cmp_neq_f16" | "v_cmp_lg_f16" | "v_cmp_lt_f16" |
                "v_cmp_le_f16" | "v_cmp_gt_f16" | "v_cmp_ge_f16" | "v_cmp_nlt_f16" |
                "v_cmp_nle_f16" | "v_cmp_ngt_f16" | "v_cmp_nge_f16" | "v_cmp_o_f16" | "v_cmp_u_f16" =>
              compareF16(opcode, left, right)
            case "v_cmp_eq_f32" =>
              IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) == IEEE754.binary32ToFloat(
                Binary32.fromLow(Bits.low32(right))
              )
            case "v_cmp_eq_i16" | "v_cmp_eq_u16" => Bits.low16(left) == Bits.low16(right)
            case "v_cmp_lg_i16" | "v_cmp_lg_u16" | "v_cmp_ne_i16" | "v_cmp_ne_u16" =>
              Bits.low16(left) != Bits.low16(right)
            case "v_cmp_lg_u32" | "v_cmp_ne_u32" => Bits.low32(left) != Bits.low32(right)
            case "v_cmp_gt_i32"                  => Bits.low32(left).toInt > Bits.low32(right).toInt
            case "v_cmp_gt_u32"                  => Bits.low32(left) > Bits.low32(right)
            case "v_cmp_gt_f32"                  =>
              IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) > IEEE754.binary32ToFloat(
                Binary32.fromLow(Bits.low32(right))
              )
            case "v_cmp_ge_i32" => Bits.low32(left).toInt >= Bits.low32(right).toInt
            case "v_cmp_ge_u32" => Bits.low32(left) >= Bits.low32(right)
            case "v_cmp_gt_i16" =>
              Bits.signExtend(Bits.low16(left), BitWidth(16)) > Bits.signExtend(Bits.low16(right), BitWidth(16))
            case "v_cmp_gt_u16" => Bits.low16(left) > Bits.low16(right)
            case "v_cmp_ge_i16" =>
              Bits.signExtend(Bits.low16(left), BitWidth(16)) >= Bits.signExtend(Bits.low16(right), BitWidth(16))
            case "v_cmp_ge_u16" => Bits.low16(left) >= Bits.low16(right)
            case "v_cmp_lt_i16" =>
              Bits.signExtend(Bits.low16(left), BitWidth(16)) < Bits.signExtend(Bits.low16(right), BitWidth(16))
            case "v_cmp_lt_u16" => Bits.low16(left) < Bits.low16(right)
            case "v_cmp_le_i16" =>
              Bits.signExtend(Bits.low16(left), BitWidth(16)) <= Bits.signExtend(Bits.low16(right), BitWidth(16))
            case "v_cmp_le_u16" => Bits.low16(left) <= Bits.low16(right)
            case "v_cmp_lt_i32" => Bits.low32(left).toInt < Bits.low32(right).toInt
            case "v_cmp_lt_u32" => Bits.low32(left) < Bits.low32(right)
            case "v_cmp_le_i32" => Bits.low32(left).toInt <= Bits.low32(right).toInt
            case "v_cmp_le_u32" => Bits.low32(left) <= Bits.low32(right)
          }
        }
        destination match {
          case Some(register) =>
            updateLaneMask(register, booleanLaneMask(result))
          case None =>
            // SDWA VOPC instructions targeting VCC have the same
            // complete-mask write semantics as their explicit SGPR
            // form: inactive EXEC lanes are cleared, not preserved.
            carry.bits = booleanLaneMask(result)
        }
        if (traceControl && kernel.name.contains("wrapped_transform_config"))
          scribe.debug(
            s"Gemstone AMDGPU transform VOPC index=$index opcode=$opcode result=${booleanLaneMask(result)} active=${laneMask(active)}"
          )
        AdvanceStep
      case DecodedInstruction.VCompareTo(dest, opcode, srcA, srcB, absA, absB, negateA, negateB) =>
        val comparisonOpcode = opcode.replace("v_cmpx_", "v_cmp_")
        val needsFloat32     = comparisonOpcode.endsWith("_f32")
        val needsFloat64     = comparisonOpcode.endsWith("_f64")
        var resultMask       = 0L
        var lane             = 0
        while (lane < lanes.value) {
          if (active(lane)) {
            def modifiedFloat(sourceRegister: InstructionSource, absolute: Boolean, negate: Boolean): Float = {
              val raw      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(sourceRegister, lane))))
              val prepared = if (absolute) math.abs(raw) else raw
              if (negate) -prepared else prepared
            }
            def modifiedDouble(
                sourceRegister: InstructionSource,
                absolute: Boolean,
                negate: Boolean
            ): Double = {
              val raw      = IEEE754.binary64ToDouble(sourceFloating64(sourceRegister, lane, scalar, vector))
              val prepared = if (absolute) math.abs(raw) else raw
              if (negate) -prepared else prepared
            }
            val leftFloat   = if (needsFloat32 || traceControl) modifiedFloat(srcA, absA, negateA) else 0.0f
            val rightFloat  = if (needsFloat32 || traceControl) modifiedFloat(srcB, absB, negateB) else 0.0f
            val leftDouble  = if (needsFloat64) modifiedDouble(srcA, absA, negateA) else 0.0
            val rightDouble = if (needsFloat64) modifiedDouble(srcB, absB, negateB) else 0.0
            val laneResult  = comparisonOpcode match {
              case "v_cmp_eq_u32" =>
                Bits.low32(source(srcA, lane)) == Bits.low32(source(srcB, lane))
              case "v_cmp_eq_u64" =>
                source64(srcA, lane, scalar, vector) == source64(srcB, lane, scalar, vector)
              case "v_cmp_class_f32" =>
                (floatClassMask(leftFloat) & Bits.low32(source(srcB, lane))) != 0L
              case "v_cmp_class_f64" =>
                (doubleClassMask(leftDouble) & Bits.low32(source(srcB, lane))) != 0L
              case "v_cmp_eq_i16" | "v_cmp_eq_u16" =>
                Bits.low16(source(srcA, lane)) == Bits.low16(source(srcB, lane))
              case "v_cmp_eq_f16" | "v_cmp_neq_f16" | "v_cmp_lg_f16" | "v_cmp_lt_f16" |
                  "v_cmp_le_f16" | "v_cmp_gt_f16" | "v_cmp_ge_f16" | "v_cmp_nlt_f16" |
                  "v_cmp_nle_f16" | "v_cmp_ngt_f16" | "v_cmp_nge_f16" | "v_cmp_o_f16" | "v_cmp_u_f16" =>
                compareF16(opcode, source(srcA, lane), source(srcB, lane))
              case "v_cmp_eq_f32" =>
                leftFloat == rightFloat
              case "v_cmp_eq_f64" =>
                leftDouble == rightDouble
              case "v_cmp_ngt_f64" =>
                !(leftDouble > rightDouble)
              case "v_cmp_nge_f64" =>
                !(leftDouble >= rightDouble)
              case "v_cmp_nlg_f64" =>
                !(leftDouble < rightDouble) && !(leftDouble > rightDouble)
              case "v_cmp_nle_f64" =>
                !(leftDouble <= rightDouble)
              case "v_cmp_nlt_f64" =>
                !(leftDouble < rightDouble)
              case "v_cmp_le_f64" =>
                leftDouble <= rightDouble
              case "v_cmp_o_f64" =>
                !leftDouble.isNaN && !rightDouble.isNaN
              case "v_cmp_u_f64" =>
                leftDouble.isNaN || rightDouble.isNaN
              case "v_cmp_gt_u32" =>
                Bits.low32(source(srcA, lane)) > Bits.low32(source(srcB, lane))
              case "v_cmp_gt_i32" =>
                Bits.low32(source(srcA, lane)).toInt > Bits.low32(source(srcB, lane)).toInt
              case "v_cmp_gt_f32" =>
                leftFloat > rightFloat
              case "v_cmp_ge_f32" =>
                leftFloat >= rightFloat
              case "v_cmp_le_f32" =>
                leftFloat <= rightFloat
              case "v_cmp_ngt_f32" =>
                !(leftFloat > rightFloat)
              case "v_cmp_nge_f32" =>
                !(leftFloat >= rightFloat)
              case "v_cmp_nle_f32" =>
                !(leftFloat <= rightFloat)
              case "v_cmp_nlt_f32"
                  if (kernel.name.contains("log_functor") || kernel.name.contains(
                    "log10_functor"
                  )) && (index == 992 || index == 993) =>
                !(rightFloat < leftFloat)
              case "v_cmp_nlt_f32" =>
                !(leftFloat < rightFloat)
              case "v_cmp_o_f32" =>
                !leftFloat.isNaN && !rightFloat.isNaN
              case "v_cmp_u_f32" =>
                leftFloat.isNaN || rightFloat.isNaN
              case "v_cmp_ne_u32" =>
                Bits.low32(source(srcA, lane)) != Bits.low32(source(srcB, lane))
              case "v_cmp_ne_u16" =>
                Bits.low16(source(srcA, lane)) != Bits.low16(source(srcB, lane))
              case "v_cmp_ne_u64" =>
                source64(srcA, lane, scalar, vector) != source64(srcB, lane, scalar, vector)
              case "v_cmp_lg_i16" | "v_cmp_ne_i16" =>
                Bits.low16(source(srcA, lane)) != Bits.low16(source(srcB, lane))
              case "v_cmp_gt_i16" =>
                Bits.signExtend(
                  Bits.low16(source(srcA, lane)),
                  BitWidth(16)
                ) > Bits.signExtend(Bits.low16(source(srcB, lane)), BitWidth(16))
              case "v_cmp_gt_u16" =>
                Bits.low16(source(srcA, lane)) > Bits.low16(source(srcB, lane))
              case "v_cmp_ge_i16" =>
                Bits.signExtend(
                  Bits.low16(source(srcA, lane)),
                  BitWidth(16)
                ) >= Bits.signExtend(Bits.low16(source(srcB, lane)), BitWidth(16))
              case "v_cmp_ge_u32" =>
                Bits.low32(source(srcA, lane)) >= Bits.low32(source(srcB, lane))
              case "v_cmp_ge_i32" =>
                Bits.low32(source(srcA, lane)).toInt >= Bits.low32(source(srcB, lane)).toInt
              case "v_cmp_ge_i64" =>
                source64(srcA, lane, scalar, vector) >= source64(srcB, lane, scalar, vector)
              case "v_cmp_lt_u16" =>
                Bits.low16(source(srcA, lane)) < Bits.low16(source(srcB, lane))
              case "v_cmp_ge_u16" =>
                Bits.low16(source(srcA, lane)) >= Bits.low16(source(srcB, lane))
              case "v_cmp_lt_i16" =>
                Bits.signExtend(
                  Bits.low16(source(srcA, lane)),
                  BitWidth(16)
                ) < Bits.signExtend(Bits.low16(source(srcB, lane)), BitWidth(16))
              case "v_cmp_le_i16" =>
                Bits.signExtend(
                  Bits.low16(source(srcA, lane)),
                  BitWidth(16)
                ) <= Bits.signExtend(Bits.low16(source(srcB, lane)), BitWidth(16))
              case "v_cmp_le_u16" =>
                Bits.low16(source(srcA, lane)) <= Bits.low16(source(srcB, lane))
              case "v_cmp_gt_i64" =>
                source64(srcA, lane, scalar, vector) > source64(srcB, lane, scalar, vector)
              case "v_cmp_gt_u64" =>
                java.lang.Long.compareUnsigned(
                  source64(srcA, lane, scalar, vector),
                  source64(srcB, lane, scalar, vector)
                ) > 0
              case "v_cmp_neq_f64" =>
                leftDouble != rightDouble
              case "v_cmp_gt_f64" =>
                leftDouble > rightDouble
              case "v_cmp_ge_f64" =>
                leftDouble >= rightDouble
              case "v_cmp_lg_f64" =>
                leftDouble != rightDouble
              case "v_cmp_neq_f32" =>
                leftFloat != rightFloat
              case "v_cmp_lg_f32" =>
                leftFloat != rightFloat
              case "v_cmp_lt_f32" =>
                leftFloat < rightFloat
              case "v_cmp_lt_f64" =>
                leftDouble < rightDouble
              case "v_cmp_lt_i32" =>
                Bits.low32(source(srcA, lane)).toInt < Bits.low32(source(srcB, lane)).toInt
              case "v_cmp_le_i32" =>
                Bits.low32(source(srcA, lane)).toInt <= Bits.low32(source(srcB, lane)).toInt
              case "v_cmp_lt_u32" =>
                Bits.low32(source(srcA, lane)) < Bits.low32(source(srcB, lane))
              case "v_cmp_le_u32" =>
                Bits.low32(source(srcA, lane)) <= Bits.low32(source(srcB, lane))
              case "v_cmp_lt_i64" =>
                source64(srcA, lane, scalar, vector) < source64(srcB, lane, scalar, vector)
              case "v_cmp_le_i64" =>
                source64(srcA, lane, scalar, vector) <= source64(srcB, lane, scalar, vector)
              case "v_cmp_le_u64" =>
                java.lang.Long.compareUnsigned(
                  source64(srcA, lane, scalar, vector),
                  source64(srcB, lane, scalar, vector)
                ) <= 0
              case "v_cmp_ge_u64" =>
                java.lang.Long.compareUnsigned(
                  source64(srcA, lane, scalar, vector),
                  source64(srcB, lane, scalar, vector)
                ) >= 0
              case "v_cmp_lt_u64" =>
                java.lang.Long.compareUnsigned(
                  source64(srcA, lane, scalar, vector),
                  source64(srcB, lane, scalar, vector)
                ) < 0
            }
            if (
              traceSetOps && kernel.name.contains(
                "set_operations"
              ) && localStart == 0 && index >= 1833 && index <= 1835 && lane < 4
            )
              scribe.debug(
                s"Gemstone AMDGPU set compare3 kernel=${kernel.name} index=$index lane=$lane opcode=$opcode srcA=$srcA srcB=$srcB left=0x${source(srcA, lane).toHexString} right=0x${source(srcB, lane).toHexString} result=$laneResult active=${active(lane)}"
              )
            if (laneResult) resultMask |= 1L << lane
            if (
              AMDGPUEnvironment.trace("set-compare") &&
              kernel.name.contains("set_operations") && lane < 4
            )
              scribe.debug(
                s"Gemstone AMDGPU set compare index=$index lane=$lane opcode=$opcode srcA=$srcA srcB=$srcB left=0x${source(srcA, lane).toHexString} right=0x${source(srcB, lane).toHexString} result=$laneResult active=${active(lane)}"
              )
            if (traceControl && lane == 0)
              scribe.debug(
                s"Gemstone AMDGPU control compare kernel=${kernel.name} index=$index opcode=$opcode srcA=$leftFloat srcB=$rightFloat rawA=0x${source(srcA, lane).toHexString} rawB=0x${source(srcB, lane).toHexString} result=$laneResult active=${active(lane)}"
              )
            if (traceByteSortCompare && lane == 0)
              scribe.debug(
                s"Gemstone AMDGPU byte-sort compare3 index=$index opcode=$opcode srcA=$srcA srcB=$srcB left=0x${source(srcA, lane).toHexString} right=0x${source(srcB, lane).toHexString} result=$laneResult active=${active(lane)}"
              )
          }
          lane += 1
        }
        val activeMask = laneMask(active)
        // The VOP3 compare destination is significant: it may be an
        // ordinary scalar mask register or one of the VCC/EXEC
        // pseudo-registers.  Preserve the encoded destination for
        // ordinary compares, while cmpx always replaces EXEC.
        if (opcode.startsWith("v_cmpx_")) active.bits = resultMask
        else updateLaneMask(dest, resultMask)
        val compareMask =
          (sourceScalar64(InstructionSource.Scalar(dest)) & ~activeMask) | (resultMask & activeMask)
        if (
          AMDGPUEnvironment.trace("merge-compare-detail") &&
          laneMask(active) != 0L && kernel.name.contains("wrapped_merge_sort") &&
          index >= 1530 && index <= 1625
        )
          scribe.debug(
            s"Gemstone AMDGPU merge compare detail index=$index opcode=$opcode dest=$dest active=0x${activeMask.toHexString} result=0x${resultMask.toHexString} lane0=${active(0)} left=0x${Bits.low32(source(srcA, 0)).toHexString} right=0x${Bits.low32(source(srcB, 0)).toHexString}"
          )
        if (
          AMDGPUEnvironment.trace("merge-stage") &&
          laneMask(active) != 0L && kernel.name.contains("wrapped_merge_sort") && index >= 1400 && index <= 1494
        )
          scribe.debug(
            s"Gemstone AMDGPU merge stage compare index=$index opcode=$opcode dest=$dest active=0x${activeMask.toHexString} result=0x${resultMask.toHexString} written=0x${compareMask.toHexString} v1=0x${vector(1)(0).toHexString} v2=0x${vector(2)(0).toHexString} v3=0x${vector(3)(0).toHexString} v4=0x${vector(4)(0).toHexString} v5=0x${vector(5)(0).toHexString} v6=0x${vector(6)(0).toHexString} v7=0x${vector(7)(0).toHexString} v8=0x${vector(8)(0).toHexString} v9=0x${vector(9)(0).toHexString} v10=0x${vector(10)(0).toHexString} v11=0x${vector(11)(0).toHexString} v12=0x${vector(12)(0).toHexString}"
          )
        if (
          AMDGPUEnvironment.trace("merge-select") &&
          kernel.name.contains("wrapped_merge_sort") && index >= 1550 && index <= 1625
        )
          scribe.debug(
            s"Gemstone AMDGPU merge compare index=$index opcode=$opcode dest=$dest active=0x${activeMask.toHexString} result=0x${resultMask.toHexString} written=0x${compareMask.toHexString}"
          )
        if (traceByteSortCompare)
          scribe.debug(
            s"Gemstone AMDGPU byte-sort compare3-mask index=$index opcode=$opcode dest=$dest mask=0x${resultMask.toHexString}"
          )
        if (traceControl)
          scribe.debug(
            s"Gemstone AMDGPU transform compare index=$index opcode=$opcode dest=$dest result=$resultMask active=${laneMask(active)}"
          )
        if (traceSetControl)
          scribe.debug(
            s"Gemstone AMDGPU set compare-mask kernel=${kernel.name} localStart=$localStart index=$index opcode=$opcode dest=$dest result=0x${resultMask.toHexString} active=0x${laneMask(active).toHexString} carry=0x${laneMask(carry).toHexString} srcA0=0x${source(srcA, 0).toHexString} srcB0=0x${source(srcB, 0).toHexString} srcA1=0x${source(srcA, math.min(1, lanes.value - 1)).toHexString} srcB1=0x${source(srcB, math.min(1, lanes.value - 1)).toHexString}"
          )
        AdvanceStep
      case DecodedInstruction.VShiftLeft64(dest, amount, src) =>
        foreachActive { lane =>
          val sourceLow  = Bits.low32(source(src, lane))
          val sourceHigh = src match {
            case InstructionSource.VectorRegister(register) => Bits.low32(vector(register + 1)(lane))
            case InstructionSource.Scalar(register)         => Bits.low32(scalar(register + 1))
            case InstructionSource.Constant(_)              => 0L
            case InstructionSource.Literal(_)               => 0L
          }
          val shifted = Bits.join32(sourceLow, sourceHigh) << (source(amount, lane).toInt & 63)
          vector(dest)(lane) = Bits.low32(shifted)
          vector(dest + 1)(lane) = Bits.high32(shifted)
        }
        AdvanceStep
      case DecodedInstruction.VShiftRight64(dest, amount, src) =>
        foreachActive { lane =>
          val shifted = source64(src, lane, scalar, vector) >>> (source(amount, lane).toInt & 63)
          vector(dest)(lane) = Bits.low32(shifted)
          vector(dest + 1)(lane) = Bits.high32(shifted)
        }
        AdvanceStep
      case DecodedInstruction.VShiftRightArithmetic64(dest, amount, src) =>
        foreachActive { lane =>
          val shifted = source64(src, lane, scalar, vector) >> (source(amount, lane).toInt & 63)
          vector(dest)(lane) = Bits.low32(shifted)
          vector(dest + 1)(lane) = Bits.high32(shifted)
        }
        AdvanceStep
      case DecodedInstruction.VFloat64Add(dest, srcA, srcB, negateB) =>
        foreachActive { lane =>
          val left   = IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector))
          val rawB   = IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
          val right  = if (negateB) -rawB else rawB
          val result = IEEE754.doubleToBinary64(left + right)
          vector(dest)(lane) = Bits.low32(result)
          vector(dest + 1)(lane) = Bits.high32(result)
        }
        AdvanceStep
      case DecodedInstruction.DSReadD16(dest, addressRegister, byteCount, offset, high) =>
        val scalarByteCount                                     = ScalarByteWidth.assume(byteCount)
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val base         = Bits.low32(vector(addressRegister)(lane))
            val sourceOffset = checkedLdsOffset(lds, base + offset.toLong, byteCount)
            if (sourceOffset == InvalidLdsOffset)
              Left(ldsFailure(base + offset.toLong, byteCount))
            else {
              val loaded = LittleEndian.decode(lds, sourceOffset, scalarByteCount)
              vector(dest)(lane) =
                if (high) (vector(dest)(lane) & 0x0000ffffL) | (loaded << 16)
                else (vector(dest)(lane) & 0xffff0000L) | loaded
              loadLane(lane + 1)
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.DSSignedReadD16(dest, addressRegister, byteCount, offset, high) =>
        val scalarByteCount                                     = ScalarByteWidth.assume(byteCount)
        val bitWidth                                            = BitWidth.assume(byteCount * 8)
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val base         = Bits.low32(vector(addressRegister)(lane))
            val sourceOffset = checkedLdsOffset(lds, base + offset.toLong, byteCount)
            if (sourceOffset == InvalidLdsOffset)
              Left(ldsFailure(base + offset.toLong, byteCount))
            else {
              val loaded =
                Bits.low16(
                  Bits.signExtend(LittleEndian.decode(lds, sourceOffset, scalarByteCount), bitWidth)
                )
              vector(dest)(lane) =
                if (high) (vector(dest)(lane) & 0x0000ffffL) | (loaded << 16)
                else (vector(dest)(lane) & 0xffff0000L) | loaded
              loadLane(lane + 1)
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.DSRead(dest, addressRegister, byteCount, offsetA, offsetB) =>
        // DS_READ can transfer 16 bytes, while trace previews intentionally show only the first scalar word.
        val previewWidth                                        = ScalarByteWidth.assume(math.min(8, byteCount))
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val base  = Bits.low32(vector(addressRegister)(lane))
            val first = checkedLdsOffset(lds, base + offsetA.toLong, byteCount)
            if (first == InvalidLdsOffset)
              Left(ldsFailure(base + offsetA.toLong, byteCount))
            else {
              val second = offsetB match {
                case None         => InvalidLdsOffset
                case Some(offset) => checkedLdsOffset(lds, base + offset.toLong, byteCount)
              }
              if (offsetB.nonEmpty && second == InvalidLdsOffset)
                Left(ldsFailure(base + offsetB.get.toLong, byteCount))
              else {
                writeVectorBytes(vector, dest, lane, lds, first, byteCount)
                if (second != InvalidLdsOffset)
                  writeVectorBytes(vector, dest + (byteCount + 3) / 4, lane, lds, second, byteCount)
                if (traceLds && dest == 22 && addressRegister == 22)
                  scribe.debug(
                    s"Gemstone AMDGPU LDS read index=$index lane=$lane addressRegister=$addressRegister base=0x${base.toHexString} bytes=$byteCount value=0x${LittleEndian.decode(lds, first, previewWidth).toHexString}"
                  )
                if (traceSetLds && lane < 4)
                  scribe.debug(
                    s"Gemstone AMDGPU set LDS read kernel=${kernel.name} localStart=$localStart index=$index lane=$lane dest=$dest addressRegister=$addressRegister base=0x${base.toHexString} offsetA=$offsetA offsetB=${offsetB.getOrElse(-1)} bytes=$byteCount first=0x${LittleEndian.decode(lds, first, previewWidth).toHexString} second=${
                        if (second == InvalidLdsOffset) "-"
                        else LittleEndian.decode(lds, second, previewWidth).toHexString
                      }"
                  )
                if (
                  AMDGPUEnvironment.trace("scan-lds") &&
                  kernel.name.contains("wrapped_scan") && lane < 8
                )
                  scribe.debug(
                    s"Gemstone AMDGPU scan LDS read index=$index lane=$lane dest=$dest base=0x${base.toHexString} offsetA=$offsetA bytes=$byteCount value=0x${LittleEndian.decode(lds, first, previewWidth).toHexString}"
                  )
                if (traceByteSort && lane == 0 && index >= 540)
                  scribe.debug(
                    s"Gemstone AMDGPU byte-sort LDS read index=$index dest=$dest base=0x${base.toHexString} bytes=$byteCount value=0x${LittleEndian.decode(lds, first, previewWidth).toHexString}"
                  )
                if (
                  AMDGPUEnvironment.trace("merge-reads") &&
                  lane == 0 && kernel.name.contains("wrapped_merge_sort") && index >= 2050
                )
                  scribe.debug(
                    s"Gemstone AMDGPU merge LDS read index=$index dest=$dest base=0x${base.toHexString} offsetA=$offsetA bytes=$byteCount value=0x${LittleEndian.decode(lds, first, previewWidth).toHexString}"
                  )
                if (
                  AMDGPUEnvironment.trace("merge-rank-reads") &&
                  lane == 0 && localStart == 0 && kernel.name.contains("wrapped_merge_sort") &&
                  index >= 1400 && index <= 1625
                )
                  scribe.debug(
                    s"Gemstone AMDGPU merge rank read index=$index dest=$dest addressRegister=$addressRegister base=0x${base.toHexString} offsetA=$offsetA bytes=$byteCount value=0x${LittleEndian.decode(lds, first, previewWidth).toHexString} v1=0x${vector(1)(lane).toHexString} v2=0x${vector(2)(lane).toHexString} v3=0x${vector(3)(lane).toHexString} v7=0x${vector(7)(lane).toHexString} v8=0x${vector(8)(lane).toHexString} v9=0x${vector(9)(lane).toHexString} v10=0x${vector(10)(lane).toHexString} v13=0x${vector(13)(lane).toHexString} v14=0x${vector(14)(lane).toHexString} v23=0x${vector(23)(lane).toHexString}"
                  )
                if (
                  AMDGPUEnvironment.trace("merge-input") &&
                  kernel.name.contains("wrapped_merge_sort") && localStart == 0 && lane < 8 &&
                  (index >= 1300 && index <= 1302)
                )
                  scribe.debug(
                    s"Gemstone AMDGPU merge input read index=$index localStart=$localStart lane=$lane dest=$dest address=0x${base.toHexString} offsetA=$offsetA offsetB=${offsetB.getOrElse(-1)} value=0x${LittleEndian.decode(lds, first, previewWidth).toHexString} vdest=0x${vector(dest)(lane).toHexString} vdestNext=0x${vector(dest + (byteCount + 3) / 4)(lane).toHexString}"
                  )
                loadLane(lane + 1)
              }
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.DSSignedRead(dest, addressRegister, byteCount, offsetA, offsetB) =>
        val scalarByteCount                                     = ScalarByteWidth.assume(byteCount)
        val bitWidth                                            = BitWidth.assume(byteCount * 8)
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val base  = Bits.low32(vector(addressRegister)(lane))
            val first = checkedLdsOffset(lds, base + offsetA.toLong, byteCount)
            if (first == InvalidLdsOffset)
              Left(ldsFailure(base + offsetA.toLong, byteCount))
            else {
              val second = offsetB match {
                case None         => InvalidLdsOffset
                case Some(offset) => checkedLdsOffset(lds, base + offset.toLong, byteCount)
              }
              if (offsetB.nonEmpty && second == InvalidLdsOffset)
                Left(ldsFailure(base + offsetB.get.toLong, byteCount))
              else {
                writeVectorValue(
                  vector,
                  dest,
                  lane,
                  Bits.signExtend(LittleEndian.decode(lds, first, scalarByteCount), bitWidth),
                  4
                )
                if (second != InvalidLdsOffset)
                  writeVectorValue(
                    vector,
                    dest + 1,
                    lane,
                    Bits.signExtend(LittleEndian.decode(lds, second, scalarByteCount), bitWidth),
                    4
                  )
                loadLane(lane + 1)
              }
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.DSPermute(dest, addressRegister, src, offset) =>
        // DS_PERMUTE is the forward (scatter) counterpart of
        // DS_BPERMUTE: each active source lane writes its value to
        // the lane selected by its byte address.  Snapshot both
        // operands because the destination may alias the source or
        // address register.  EXEC gates the source lanes, not the
        // destination lane: a source can scatter into a lane that is
        // currently inactive.  Preserve old values for destinations
        // that receive no active source.
        val addresses = vector(addressRegister).clone()
        val sources   = vector(src).clone()
        val result    = vector(dest).clone()
        foreachActive { lane =>
          val destinationLane = permuteDestinationLane(addresses(lane), offset.toLong, waveSize)
          if (destinationLane < lanes.value)
            result(destinationLane) = Bits.low32(sources(lane))
        }
        foreachLane { lane => vector(dest)(lane) = result(lane) }
        AdvanceStep
      case DecodedInstruction.DSBPermute(dest, addressRegister, src, offset) =>
        val addresses = vector(addressRegister).clone()
        val sources   = vector(src).clone()
        foreachActive { lane =>
          // DS_BPERMUTE addresses are byte offsets formed by adding
          // the instruction's immediate offset to the per-lane
          // address operand.  Omitting the offset makes e.g.
          // `offset:124` read lane 0 instead of lane 31, dropping
          // the final cross-wave reduction contribution.
          val sourceLane = bpermuteSourceLane(addresses(lane), offset.toLong)
          vector(dest)(lane) = if (sourceLane < lanes.value) Bits.low32(sources(sourceLane)) else 0L
        }
        AdvanceStep
      case DecodedInstruction.DSSwizzle(dest, src, control) =>
        // DS_SWIZZLE packs three five-bit lane masks in the 16-bit
        // offset: AND[4:0], OR[9:5], and XOR[14:10].  The
        // ``BROADCAST,32,15`` encoding is 0x01e0, so OR is 15 after
        // shifting by five.  Snapshot the source register first
        // because the instruction permits in-place swizzles.
        val sources = vector(src).clone()
        val andMask = control & 0x1f
        val orMask  = (control >>> 5) & 0x1f
        val xorMask = (control >>> 10) & 0x1f
        foreachActive { lane =>
          val sourceLane = ((lane & andMask) | orMask) ^ xorMask
          vector(dest)(lane) =
            if (sourceLane >= 0 && sourceLane < lanes.value) Bits.low32(sources(sourceLane)) else 0L
        }
        AdvanceStep
      case DecodedInstruction.DSAtomic(opcode, dest, addressRegister, src, byteCount, offset) =>
        val scalarByteCount                                       = ScalarByteWidth.assume(byteCount)
        def atomicLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) atomicLane(lane + 1)
          else {
            val base = Bits.low32(vector(addressRegister)(lane))
            readLDS(lds, base + offset.toLong, byteCount).flatMap { bytes =>
              val previous = LittleEndian.decode(bytes, 0, scalarByteCount)
              val operand  = if (byteCount == 8)
                (vector(src)(lane) & 0xffffffffL) | ((vector(src + 1)(lane) & 0xffffffffL) << 32)
              else Bits.low32(vector(src)(lane))
              val updated = opcode match {
                case "ds_add_u32" | "ds_add_rtn_u32" => Bits.low32(previous + operand)
                case "ds_sub_u32" | "ds_sub_rtn_u32" => Bits.low32(previous - operand)
                case "ds_inc_u32" | "ds_inc_rtn_u32" => Bits.low32(previous + 1L)
                case "ds_dec_u32" | "ds_dec_rtn_u32" => Bits.low32(previous - 1L)
                case "ds_min_i32"                    => math.min(previous.toInt, operand.toInt).toLong & 0xffffffffL
                case "ds_max_i32"                    => math.max(previous.toInt, operand.toInt).toLong & 0xffffffffL
                case "ds_min_u32"                    => math.min(Bits.low32(previous), Bits.low32(operand))
                case "ds_max_u32"                    => math.max(Bits.low32(previous), Bits.low32(operand))
                case "ds_add_rtn_u64"                => previous + operand
                case "ds_sub_rtn_u64"                => previous - operand
                case "ds_inc_rtn_u64"                => previous + 1L
                case "ds_dec_rtn_u64"                => previous - 1L
                case "ds_and_rtn_b32"                => Bits.low32(previous) & Bits.low32(operand)
                case "ds_or_rtn_b32"                 => Bits.low32(previous) | Bits.low32(operand)
                case "ds_xor_rtn_b32"                => Bits.low32(previous) ^ Bits.low32(operand)
                case "ds_and_rtn_b64"                => previous & operand
                case "ds_or_rtn_b64"                 => previous | operand
                case "ds_xor_rtn_b64"                => previous ^ operand
                case "ds_min_rtn_i32"                => math.min(previous.toInt, operand.toInt).toLong & 0xffffffffL
                case "ds_max_rtn_i32"                => math.max(previous.toInt, operand.toInt).toLong & 0xffffffffL
                case "ds_min_rtn_u32"                => math.min(Bits.low32(previous), Bits.low32(operand))
                case "ds_max_rtn_u32"                => math.max(Bits.low32(previous), Bits.low32(operand))
                case "ds_min_rtn_i64"                => math.min(previous, operand)
                case "ds_max_rtn_i64"                => math.max(previous, operand)
                case "ds_min_rtn_u64"                =>
                  if (java.lang.Long.compareUnsigned(previous, operand) <= 0) previous else operand
                case "ds_max_rtn_u64" =>
                  if (java.lang.Long.compareUnsigned(previous, operand) >= 0) previous else operand
                case "ds_add_rtn_f32" =>
                  IEEE754.floatToBinary32(
                    IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(previous.toInt))) + IEEE754.binary32ToFloat(
                      Binary32.fromLow(Bits.low32(
                        operand.toInt
                      ))
                    )
                  ).value.toLong & 0xffffffffL
                case "ds_min_rtn_f32" =>
                  IEEE754.floatToBinary32(
                    math.min(
                      IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(previous.toInt))),
                      IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(operand.toInt)))
                    )
                  ).value.toLong & 0xffffffffL
                case "ds_max_rtn_f32" =>
                  IEEE754.floatToBinary32(
                    math.max(
                      IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(previous.toInt))),
                      IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(operand.toInt)))
                    )
                  ).value.toLong & 0xffffffffL
                case "ds_add_rtn_f64" =>
                  IEEE754.doubleToBinary64(
                    IEEE754.binary64ToDouble(previous) + IEEE754.binary64ToDouble(operand)
                  )
                case "ds_min_rtn_f64" =>
                  IEEE754.doubleToBinary64(
                    math.min(
                      IEEE754.binary64ToDouble(previous),
                      IEEE754.binary64ToDouble(operand)
                    )
                  )
                case "ds_max_rtn_f64" =>
                  IEEE754.doubleToBinary64(
                    math.max(
                      IEEE754.binary64ToDouble(previous),
                      IEEE754.binary64ToDouble(operand)
                    )
                  )
              }
              if (opcode.contains("_rtn_"))
                writeVectorBytes(vector, dest, lane, LittleEndian.toArray(previous, scalarByteCount))
              writeLDS(lds, base + offset.toLong, LittleEndian.toArray(updated, scalarByteCount)).flatMap { _ =>
                if (traceSetLds && lane < 4)
                  scribe.debug(
                    s"Gemstone AMDGPU set LDS atomic kernel=${kernel.name} index=$index lane=$lane opcode=$opcode dest=$dest addressRegister=$addressRegister base=0x${base.toHexString} offset=$offset bytes=$byteCount previous=0x${previous.toHexString} operand=0x${operand.toHexString} updated=0x${updated.toHexString}"
                  )
                atomicLane(lane + 1)
              }
            }
          }
        advanceAfter(atomicLane(0))
      case DecodedInstruction.DSWrite(addressRegister, srcA, srcB, byteCount, offsetA, offsetB) =>
        val previewWidth                                         = ScalarByteWidth.assume(math.min(4, byteCount))
        def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) storeLane(lane + 1)
          else {
            val base = Bits.low32(vector(addressRegister)(lane))
            for {
              firstAddress = base + offsetA.toLong
              firstBytes   = vectorBytes(vector, srcA, lane, byteCount)
              _ <- writeLDS(lds, firstAddress, firstBytes).map { _ =>
                if (traceSetLds && lane < 4)
                  scribe.debug(
                    s"Gemstone AMDGPU set LDS write kernel=${kernel.name} localStart=$localStart index=$index lane=$lane addressRegister=$addressRegister base=0x${base.toHexString} offset=$offsetA bytes=$byteCount address=0x${firstAddress.toHexString} src=$srcA value=0x${LittleEndian.decode(firstBytes, 0, previewWidth).toHexString}"
                  )
                if (
                  AMDGPUEnvironment.trace("merge-lds") &&
                  lane == 0 && kernel.name.contains("wrapped_merge_sort") && firstAddress < 0x1000L
                )
                  scribe.debug(
                    s"Gemstone AMDGPU merge LDS write index=$index address=0x${firstAddress.toHexString} bytes=$byteCount value=0x${LittleEndian.decode(firstBytes, 0, previewWidth).toHexString}"
                  )
                if (
                  AMDGPUEnvironment.trace("merge-lds-detail") &&
                  kernel.name.contains("wrapped_merge_sort") && localStart == 0 && lane < 8 &&
                  ((index >= 1190 && index <= 1205) || (index >= 1358 && index <= 1386) ||
                    (index >= 1480 && index <= 1520) || (index >= 1625 && index <= 1645))
                )
                  scribe.debug(
                    s"Gemstone AMDGPU merge LDS detail index=$index lane=$lane active=${active(lane)} address=0x${firstAddress.toHexString} bytes=$byteCount src=$srcA v0=0x${vector(srcA)(lane).toHexString} v1=0x${vector(srcA + 1)(lane).toHexString} v2=0x${vector(srcA + 2)(lane).toHexString} v3=0x${vector(srcA + 3)(lane).toHexString} v4=0x${vector(srcA + 4)(lane).toHexString}"
                  )
                if (
                  AMDGPUEnvironment.trace("merge-input") &&
                  kernel.name.contains("wrapped_merge_sort") && localStart == 0 && lane < 8 &&
                  ((index >= 1066 && index <= 1082) || (index >= 1194 && index <= 1203) ||
                    (index >= 1272 && index <= 1295))
                )
                  scribe.debug(
                    s"Gemstone AMDGPU merge input LDS index=$index localStart=$localStart lane=$lane active=${active(lane)} address=0x${firstAddress.toHexString} bytes=$byteCount src=$srcA value=0x${LittleEndian.decode(firstBytes, 0, previewWidth).toHexString} v0=0x${vector(0)(lane).toHexString} v1=0x${vector(1)(lane).toHexString} v3=0x${vector(3)(lane).toHexString} v4=0x${vector(4)(lane).toHexString} v5=0x${vector(5)(lane).toHexString} v7=0x${vector(7)(lane).toHexString} v9=0x${vector(9)(lane).toHexString} v13=0x${vector(13)(lane).toHexString} v14=0x${vector(14)(lane).toHexString} v17=0x${vector(17)(lane).toHexString} v18=0x${vector(18)(lane).toHexString} v19=0x${vector(19)(lane).toHexString} v20=0x${vector(20)(lane).toHexString}"
                  )
                if (traceByteSort && lane == 0 && index >= 200 && index <= 562)
                  scribe.debug(
                    s"Gemstone AMDGPU byte-sort rank-write localStart=$localStart index=$index address=0x${firstAddress.toHexString} bytes=$byteCount src=$srcA value=0x${vector(srcA)(lane).toHexString} v11=0x${vector(11)(lane).toHexString} v12=0x${vector(12)(lane).toHexString} v13=0x${vector(13)(lane).toHexString} v14=0x${vector(14)(lane).toHexString} v17=0x${vector(17)(lane).toHexString} v29=0x${vector(29)(lane).toHexString} v25=0x${vector(25)(lane).toHexString} v28=0x${vector(28)(lane).toHexString} v31=0x${vector(31)(lane).toHexString} v34=0x${vector(34)(lane).toHexString}"
                  )
                if (traceByteSort && firstAddress == 0xb20L)
                  scribe.debug(
                    s"Gemstone AMDGPU byte-sort counter-write localStart=$localStart index=$index lane=$lane address=0x${firstAddress.toHexString} bytes=$byteCount src=$srcA value=0x${vector(srcA)(lane).toHexString}"
                  )
                if (traceByteSortRank && lane == 0 && index >= 550 && index <= 562)
                  scribe.debug(
                    s"Gemstone AMDGPU byte-sort final-write index=$index addressRegister=$addressRegister address=0x${firstAddress.toHexString} src=$srcA value=0x${vector(srcA)(lane).toHexString} v1=0x${vector(1)(lane).toHexString} v2=0x${vector(2)(lane).toHexString} v3=0x${vector(3)(lane).toHexString} v4=0x${vector(4)(lane).toHexString} v10=0x${vector(10)(lane).toHexString} v25=0x${vector(25)(lane).toHexString} v26=0x${vector(26)(lane).toHexString} v27=0x${vector(27)(lane).toHexString} v28=0x${vector(28)(lane).toHexString} v34=0x${vector(34)(lane).toHexString} v35=0x${vector(35)(lane).toHexString} v36=0x${vector(36)(lane).toHexString} v37=0x${vector(37)(lane).toHexString}"
                  )
                if (traceLds && firstAddress >= 0x0f00L && firstAddress < 0x1010L)
                  scribe.debug(
                    s"Gemstone AMDGPU LDS write index=$index lane=$lane addressRegister=$addressRegister base=0x${base.toHexString} address=0x${firstAddress.toHexString} bytes=$byteCount src=$srcA value=${vector(srcA)(lane)}"
                  )
                if (traceByteSort && (lane == 0 || firstAddress < 0x100L))
                  scribe.debug(
                    s"Gemstone AMDGPU byte-sort LDS write index=$index lane=$lane address=0x${firstAddress.toHexString} bytes=$byteCount src=$srcA value=0x${vector(srcA)(lane).toHexString}"
                  )
                ()
              }.leftMap { error =>
                if (traceLds) scribe.debug(
                  s"Gemstone AMDGPU LDS write index=$index op=${operations.lift(index)} lane=$lane addressRegister=$addressRegister base=0x${base.toHexString} address=0x${firstAddress.toHexString} bytes=$byteCount length=${lds.length} src=$srcA value=${vector(srcA)(lane)} v22=0x${vector(22)(lane).toHexString} v23=0x${vector(23)(lane).toHexString} v24=0x${vector(24)(lane).toHexString} v25=0x${vector(25)(lane).toHexString}"
                )
                error
              }
              _ =
                if (
                  AMDGPUEnvironment.trace("scan-lds") &&
                  kernel.name.contains("wrapped_scan") && lane < 8
                )
                  scribe.debug(
                    s"Gemstone AMDGPU scan LDS write index=$index lane=$lane address=0x${firstAddress.toHexString} bytes=$byteCount src=$srcA value=0x${vector(srcA)(lane).toHexString}"
                  )
              _ <- (srcB, offsetB) match {
                case (Some(register), Some(offset)) =>
                  val address = base + offset.toLong
                  val bytes   = vectorBytes(vector, register, lane, byteCount)
                  writeLDS(lds, address, bytes).map { _ =>
                    if (
                      AMDGPUEnvironment.trace("merge-lds") &&
                      lane == 0 && kernel.name.contains("wrapped_merge_sort") && address < 0x1000L
                    )
                      scribe.debug(
                        s"Gemstone AMDGPU merge LDS write index=$index address=0x${address.toHexString} bytes=$byteCount value=0x${LittleEndian.decode(bytes, 0, previewWidth).toHexString}"
                      )
                    if (traceByteSort && lane == 0 && index >= 200 && index <= 562)
                      scribe.debug(
                        s"Gemstone AMDGPU byte-sort rank-write localStart=$localStart index=$index address=0x${address.toHexString} bytes=$byteCount src=$register value=0x${vector(register)(lane).toHexString}"
                      )
                    if (traceByteSort && address == 0xb20L)
                      scribe.debug(
                        s"Gemstone AMDGPU byte-sort counter-write localStart=$localStart index=$index lane=$lane address=0x${address.toHexString} bytes=$byteCount src=$register value=0x${vector(register)(lane).toHexString}"
                      )
                    if (traceByteSort && (lane == 0 || address < 0x100L))
                      scribe.debug(
                        s"Gemstone AMDGPU byte-sort LDS write index=$index lane=$lane address=0x${address.toHexString} bytes=$byteCount src=$register value=0x${vector(register)(lane).toHexString}"
                      )
                    ()
                  }.leftMap { error =>
                    if (traceLds) scribe.debug(
                      s"Gemstone AMDGPU LDS write index=$index op=${operations.lift(index)} lane=$lane addressRegister=$addressRegister base=0x${base.toHexString} address=0x${address.toHexString} bytes=$byteCount length=${lds.length} src=$register value=${vector(register)(lane)}"
                    )
                    error
                  }
                case (None, None) => Right(())
                case _            => Left(ExecutionFailure.Failed("malformed paired DS write"))
              }
              _ <- storeLane(lane + 1)
            } yield ()
          }
        advanceAfter(storeLane(0))
      case DecodedInstruction.DSWriteD16High(addressRegister, src, offset) =>
        def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) storeLane(lane + 1)
          else {
            val base    = Bits.low32(vector(addressRegister)(lane))
            val value   = vector(src)(lane) >>> 16
            val address = base + offset.toLong
            writeLDS(lds, address, LittleEndian.toArray(value, ScalarByteWidth(2))).leftMap { error =>
              if (traceLds) scribe.debug(
                s"Gemstone AMDGPU LDS write index=$index lane=$lane address=0x${address.toHexString} bytes=2 length=${lds.length} src=$src value=$value"
              )
              error
            }.flatMap(_ => storeLane(lane + 1))
          }
        advanceAfter(storeLane(0))
      case DecodedInstruction.DSWriteD16HighByte(addressRegister, src, offset) =>
        def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) storeLane(lane + 1)
          else {
            val base    = Bits.low32(vector(addressRegister)(lane))
            val value   = vector(src)(lane) >>> 16
            val address = base + offset.toLong
            writeLDS(lds, address, LittleEndian.toArray(value, ScalarByteWidth(1))).leftMap { error =>
              if (traceLds) scribe.debug(
                s"Gemstone AMDGPU LDS write index=$index lane=$lane address=0x${address.toHexString} bytes=1 length=${lds.length} src=$src value=$value"
              )
              error
            }.flatMap(_ => storeLane(lane + 1))
          }
        advanceAfter(storeLane(0))
      case DecodedInstruction.GlobalLoad(dest, addressRegister, scalarAddress, byteCount, offset) =>
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val address = scalarAddress
              .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
              .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
            val loaded =
              if (byteCount <= 8)
                readValue(
                  address,
                  MemoryValueByteWidth.assume(byteCount),
                  kernarg,
                  dispatchBytes,
                  kernel.readOnlyAddress,
                  kernel.readOnly,
                  kernel.globalMemory,
                  allocations,
                  memory
                ).map { value =>
                  writeVectorValue(vector, dest, lane, value, byteCount)
                }
              else
                readBytes(
                  address,
                  byteCount,
                  kernarg,
                  dispatchBytes,
                  kernel.readOnlyAddress,
                  kernel.readOnly,
                  kernel.globalMemory,
                  allocations,
                  memory
                ).map(bytes => writeVectorBytes(vector, dest, lane, bytes))
            loaded.flatMap { _ =>
              if (AMDGPUEnvironment.trace("loads") && lane < 8)
                scribe.debug(
                  s"Gemstone AMDGPU global load index=$index lane=$lane kernel=${kernel.name} address=0x${address.toHexString} bytes=$byteCount dest=$dest v0=0x${vector(dest)(
                      lane
                    ).toHexString} v1=0x${(if (byteCount > 4) vector(dest + 1)(lane)
                                           else 0L).toHexString} v2=0x${(if (byteCount > 8) vector(dest + 2)(lane)
                                                                         else 0L).toHexString} v3=0x${(if (
                                                                                                         byteCount > 12
                                                                                                       ) vector(dest + 3)(lane)
                                                                                                       else 0L).toHexString}"
                )
              if (traceByteSort && lane == 0)
                scribe.debug(
                  s"Gemstone AMDGPU byte-sort GlobalLoad index=$index dest=$dest address=0x${address.toHexString} bytes=$byteCount value=0x${vector(dest)(lane).toHexString}"
                )
              loadLane(lane + 1)
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.GlobalLoadSigned(dest, addressRegister, scalarAddress, byteCount, offset) =>
        val scalarByteCount                                     = ScalarByteWidth.assume(byteCount)
        val bitWidth                                            = BitWidth.assume(byteCount * 8)
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val address = scalarAddress
              .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
              .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
            readBytes(
              address,
              byteCount,
              kernarg,
              dispatchBytes,
              kernel.readOnlyAddress,
              kernel.readOnly,
              kernel.globalMemory,
              allocations,
              memory
            ) match {
              case Left(error)  => Left(error)
              case Right(bytes) =>
                val value = Bits.signExtend(LittleEndian.decode(bytes, 0, scalarByteCount), bitWidth)
                vector(dest)(lane) = Bits.low32(value)
                loadLane(lane + 1)
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.GlobalLoadD16(dest, addressRegister, scalarAddress, offset, high) =>
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val address = scalarAddress
              .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
              .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
            readBytes(
              address,
              2,
              kernarg,
              dispatchBytes,
              kernel.readOnlyAddress,
              kernel.readOnly,
              kernel.globalMemory,
              allocations,
              memory
            ) match {
              case Left(error)  => Left(error)
              case Right(bytes) =>
                val loaded = LittleEndian.decode(bytes, 0, ScalarByteWidth(2))
                vector(dest)(lane) =
                  if (high) (vector(dest)(lane) & 0x0000ffffL) | (loaded << 16)
                  else (vector(dest)(lane) & 0xffff0000L) | loaded
                loadLane(lane + 1)
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.GlobalStore(addressRegister, scalarAddress, src, byteCount, offset) =>
        def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) storeLane(lane + 1)
          else {
            val address = scalarAddress
              .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
              .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
            if (AMDGPUEnvironment.trace("stores") && lane < 8)
              scribe.debug(
                s"Gemstone AMDGPU global store index=$index lane=$lane kernel=${kernel.name} address=0x${address.toHexString} bytes=$byteCount src=$src v0=0x${vector(src)(
                    lane
                  ).toHexString} v1=0x${(if (byteCount > 4) vector(src + 1)(lane)
                                         else 0L).toHexString} v2=0x${(if (byteCount > 8) vector(src + 2)(lane)
                                                                       else 0L).toHexString} v3=0x${(if (
                                                                                                       byteCount > 12
                                                                                                     ) vector(src + 3)(lane)
                                                                                                     else 0L).toHexString}"
              )
            val stored =
              if (byteCount <= 8)
                writeValue(
                  address,
                  if (byteCount <= 4) vector(src)(lane) else pair(vector, src, lane),
                  MemoryValueByteWidth.assume(byteCount),
                  allocations,
                  memory,
                  kernel.globalMemory
                )
              else
                writeBytes(address, vectorBytes(vector, src, lane, byteCount), allocations, memory, kernel.globalMemory)
            stored match {
              case Left(error) => Left(error)
              case Right(_)    => storeLane(lane + 1)
            }
          }
        advanceAfter(storeLane(0))
      case DecodedInstruction.GlobalAtomicAdd(
            dest,
            addressRegister,
            scalarAddress,
            src,
            byteCount,
            offset,
            returnValue
          ) =>
        if (AMDGPUEnvironment.trace("set-mask") && kernel.name.contains("set_operations") && localStart == 0)
          scribe.debug(
            s"Gemstone AMDGPU set atomic-add pre kernel=${kernel.name} index=$index dest=$dest address=$addressRegister scalarAddress=$scalarAddress src=$src bytes=$byteCount offset=$offset active=0x${laneMask(active).toHexString} addr0=0x${pair(vector, addressRegister, 0).toHexString} src0=0x${vector(src)(0).toHexString}"
          )
        def atomicLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) atomicLane(lane + 1)
          else {
            val address = scalarAddress
              .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
              .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
            val operand = if (byteCount == 8) pair(vector, src, lane) else Bits.low32(vector(src)(lane))
            atomicModifyValue(
              address,
              MemoryValueByteWidth.assume(byteCount),
              allocations,
              memory,
              kernel.globalMemory,
              kernel.globalMemoryLocks
            ) { oldValue =>
              if (byteCount == 8) oldValue + operand else Bits.low32(oldValue + operand)
            } match {
              case Left(error)     => Left(error)
              case Right(oldValue) =>
                if (returnValue) {
                  if (byteCount == 8) {
                    vector(dest)(lane) = Bits.low32(oldValue)
                    vector(dest + 1)(lane) = Bits.high32(oldValue)
                  } else vector(dest)(lane) = Bits.low32(oldValue)
                }
                atomicLane(lane + 1)
            }
          }
        advanceAfter(atomicLane(0))
      case DecodedInstruction.GlobalAtomicInc(
            dest,
            addressRegister,
            scalarAddress,
            src,
            byteCount,
            offset,
            returnValue
          ) =>
        def atomicLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) atomicLane(lane + 1)
          else {
            val address = scalarAddress
              .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
              .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
            val limit = if (byteCount == 8) pair(vector, src, lane) else Bits.low32(vector(src)(lane))
            atomicModifyValue(
              address,
              MemoryValueByteWidth.assume(byteCount),
              allocations,
              memory,
              kernel.globalMemory,
              kernel.globalMemoryLocks
            ) { oldValue =>
              if (java.lang.Long.compareUnsigned(oldValue, limit) >= 0) 0L
              else if (byteCount == 8) oldValue + 1L
              else Bits.low32(oldValue + 1L)
            } match {
              case Left(error)     => Left(error)
              case Right(oldValue) =>
                if (returnValue) {
                  if (byteCount == 8) {
                    vector(dest)(lane) = Bits.low32(oldValue)
                    vector(dest + 1)(lane) = Bits.high32(oldValue)
                  } else vector(dest)(lane) = Bits.low32(oldValue)
                }
                atomicLane(lane + 1)
            }
          }
        advanceAfter(atomicLane(0))
      case DecodedInstruction.GlobalAtomicCompareSwap(
            dest,
            addressRegister,
            scalarAddress,
            compareSrc,
            swapSrc,
            byteCount,
            offset,
            returnValue
          ) =>
        def atomicLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) atomicLane(lane + 1)
          else {
            val address = scalarAddress
              .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
              .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
            val (compare, swap) =
              if (byteCount == 8) (pair(vector, compareSrc, lane), pair(vector, swapSrc, lane))
              else (Bits.low32(vector(compareSrc)(lane)), Bits.low32(vector(swapSrc)(lane)))
            atomicModifyValue(
              address,
              MemoryValueByteWidth.assume(byteCount),
              allocations,
              memory,
              kernel.globalMemory,
              kernel.globalMemoryLocks
            ) { oldValue =>
              if (oldValue == compare) swap else oldValue
            } match {
              case Left(error)     => Left(error)
              case Right(oldValue) =>
                if (returnValue) {
                  if (byteCount == 8) {
                    vector(dest)(lane) = Bits.low32(oldValue)
                    vector(dest + 1)(lane) = Bits.high32(oldValue)
                  } else vector(dest)(lane) = Bits.low32(oldValue)
                }
                atomicLane(lane + 1)
            }
          }
        advanceAfter(atomicLane(0))
      case DecodedInstruction.GlobalStoreD16High(addressRegister, scalarAddress, src, byteCount, offset) =>
        val scalarByteCount                                      = ScalarByteWidth.assume(byteCount)
        def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) storeLane(lane + 1)
          else {
            val address = scalarAddress
              .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
              .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
            val bytes = LittleEndian.toArray(vector(src)(lane) >>> 16, scalarByteCount)
            writeBytes(address, bytes, allocations, memory, kernel.globalMemory) match {
              case Left(error) => Left(error)
              case Right(_)    => storeLane(lane + 1)
            }
          }
        advanceAfter(storeLane(0))
      case DecodedInstruction.FlatLoad(dest, addressRegister, byteCount, offset) =>
        val previewWidth                                        = ScalarByteWidth.assume(math.min(4, byteCount))
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val address = pair(vector, addressRegister, lane) + flatOffset(offset)
            val bytes   = flatLoad(lane, address, byteCount)
            bytes match {
              case Left(error)  => Left(error)
              case Right(bytes) =>
                writeVectorBytes(vector, dest, lane, bytes)
                if (AMDGPUEnvironment.trace("loads") && lane < 8)
                  scribe.debug(
                    s"Gemstone AMDGPU flat load index=$index kernel=${kernel.name} address=0x${address.toHexString} bytes=$byteCount dest=$dest v0=0x${vector(dest)(
                        lane
                      ).toHexString} v1=0x${(if (byteCount > 4) vector(dest + 1)(lane)
                                             else 0L).toHexString} v2=0x${(if (byteCount > 8) vector(dest + 2)(lane)
                                                                           else 0L).toHexString} v3=0x${(if (
                                                                                                           byteCount > 12
                                                                                                         ) vector(dest + 3)(lane)
                                                                                                         else 0L).toHexString}"
                  )
                if (
                  AMDGPUEnvironment.trace("merge-loads") &&
                  lane < 8 && kernel.name.contains("wrapped_merge_sort")
                )
                  scribe.debug(
                    s"Gemstone AMDGPU merge flat load index=$index lane=$lane dest=$dest address=0x${address.toHexString} bytes=$byteCount value=0x${LittleEndian.decode(bytes, 0, previewWidth).toHexString}"
                  )
                if (
                  AMDGPUEnvironment.trace("merge-input") &&
                  lane < 8 && kernel.name.contains("wrapped_merge_sort") &&
                  ((index >= 1244 && index <= 1268) || (index >= 1304 && index <= 1316))
                )
                  scribe.debug(
                    s"Gemstone AMDGPU merge input load index=$index localStart=$localStart lane=$lane dest=$dest address=0x${address.toHexString} bytes=$byteCount value=0x${LittleEndian.decode(bytes, 0, previewWidth).toHexString}"
                  )
                loadLane(lane + 1)
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.FlatLoadSigned(dest, addressRegister, byteCount, offset) =>
        val scalarByteCount                                     = ScalarByteWidth.assume(byteCount)
        val bitWidth                                            = BitWidth.assume(byteCount * 8)
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val address = pair(vector, addressRegister, lane) + flatOffset(offset)
            val bytes   = flatLoad(lane, address, byteCount)
            bytes match {
              case Left(error)  => Left(error)
              case Right(bytes) =>
                val value = Bits.signExtend(LittleEndian.decode(bytes, 0, scalarByteCount), bitWidth)
                vector(dest)(lane) = Bits.low32(value)
                loadLane(lane + 1)
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.VFlatLoadD16(dest, addressRegister, offset, high) =>
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val address = pair(vector, addressRegister, lane) + flatOffset(offset)
            val bytes   = flatLoad(lane, address, 2)
            bytes match {
              case Left(error)        => Left(error)
              case Right(loadedBytes) =>
                val loaded = LittleEndian.decode(loadedBytes, 0, ScalarByteWidth(2))
                vector(dest)(lane) =
                  if (high) (vector(dest)(lane) & 0x0000ffffL) | (loaded << 16)
                  else (vector(dest)(lane) & 0xffff0000L) | loaded
                loadLane(lane + 1)
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.FlatStore(addressRegister, src, byteCount, offset) =>
        def storeLane(lane: Int): Either[ExecutionFailure, Unit] = {
          if (
            AMDGPUEnvironment.trace("stores") &&
            (lane == 0 || (kernel.name.contains("wrapped_merge_sort") && lane < 8))
          )
            scribe.debug(
              "Gemstone AMDGPU store entry kernel=" + kernel.name + " active0=" + active(0) + " lanes=" + lanes
            )
          if (lane == lanes.value) Right(())
          else if (!active(lane)) storeLane(lane + 1)
          else {
            val address = pair(vector, addressRegister, lane) + flatOffset(offset)
            val bytes   = vectorBytes(vector, src, lane, byteCount)
            if (
              AMDGPUEnvironment.trace("stores") &&
              (lane == 0 || (kernel.name.contains("wrapped_merge_sort") && lane < 8))
            )
              scribe.debug(
                "Gemstone AMDGPU store index=" + index + " kernel=" + kernel.name + " src=" + src + " bytes=" + bytes.length + " address=0x" + java.lang.Long.toHexString(
                  address
                )
              )
            if (
              AMDGPUEnvironment.trace("stores") &&
              (lane == 0 || (kernel.name.contains("wrapped_merge_sort") && lane < 8))
            )
              scribe.debug("Gemstone AMDGPU store values first=" + vector(src)(
                lane
              ) + " second=" + (if (byteCount > 4) vector(src + 1)(lane)
                                else 0L) + " third=" + (if (byteCount > 8) vector(src + 2)(lane)
                                                        else 0L) + " fourth=" + (if (byteCount > 12)
                                                                                   vector(src + 3)(lane)
                                                                                 else 0L))
            flatStore(lane, address, bytes) match {
              case Left(error) => Left(error)
              case Right(_)    => storeLane(lane + 1)
            }
          }
        }
        advanceAfter(storeLane(0))
      case DecodedInstruction.FlatStoreD16High(addressRegister, src, byteCount, offset) =>
        val scalarByteCount                                      = ScalarByteWidth.assume(byteCount)
        def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) storeLane(lane + 1)
          else {
            val address = pair(vector, addressRegister, lane) + flatOffset(offset)
            flatStore(lane, address, LittleEndian.toArray(vector(src)(lane) >>> 16, scalarByteCount)) match {
              case Left(error) => Left(error)
              case Right(_)    => storeLane(lane + 1)
            }
          }
        advanceAfter(storeLane(0))
      case DecodedInstruction.BufferCacheInvalidate(_) =>
        // Cache invalidation has no observable effect in the CPU-backed emulator.
        AdvanceStep
      case DecodedInstruction.BufferLoad(dest, address, resource, scalarOffset, byteCount, offset) =>
        val previewWidth                                        = ScalarByteWidth.assume(math.min(8, byteCount))
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val start =
              address.map(register => Bits.low32(vector(register)(lane))).getOrElse(0L) + scalarOffset.fold(0L)(
                register => Bits.low32(scalar(register))
              ) + offset.toLong
            val base  = sourceScalar64(InstructionSource.Scalar(resource)) & 0x0000ffffffffffffL
            val range = Bits.low32(scalar(resource + 2))
            if (base != 0L && (start < 0L || start > range - byteCount.toLong)) {
              writeVectorBytes(vector, dest, lane, Array.fill(byteCount)(0.toByte))
              loadLane(lane + 1)
            } else if (base != 0L)
              readBytes(
                base + start,
                byteCount,
                kernarg,
                dispatchBytes,
                kernel.readOnlyAddress,
                kernel.readOnly,
                kernel.globalMemory,
                allocations,
                memory
              ) match {
                case Left(error)  => Left(error)
                case Right(bytes) =>
                  writeVectorBytes(vector, dest, lane, bytes)
                  if (AMDGPUEnvironment.trace("loads") && lane < 8)
                    scribe.debug(
                      s"Gemstone AMDGPU buffer load index=$index kernel=${kernel.name} lane=$lane start=$start base=0x${base.toHexString} bytes=$byteCount dest=$dest value=0x${LittleEndian.decode(bytes, 0, previewWidth).toHexString}"
                    )
                  loadLane(lane + 1)
              }
            else if (start < 0L || start > scratch(lane).length.toLong - byteCount.toLong)
              Left(ExecutionFailure.Failed(
                s"buffer load ${operations(index)} at instruction $index lane $lane offset $start size $byteCount exceeds private scratch ${scratch(lane).length}"
              ))
            else {
              val bytes = java.util.Arrays.copyOfRange(scratch(lane), start.toInt, start.toInt + byteCount)
              writeVectorBytes(vector, dest, lane, bytes)
              if (AMDGPUEnvironment.trace("loads") && lane < 8)
                scribe.debug(
                  s"Gemstone AMDGPU buffer load index=$index kernel=${kernel.name} lane=$lane start=$start bytes=$byteCount dest=$dest value=0x${LittleEndian.decode(bytes, 0, previewWidth).toHexString}"
                )
              loadLane(lane + 1)
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.BufferLoadD16(dest, address, resource, scalarOffset, byteCount, offset, high) =>
        val scalarByteCount                                     = ScalarByteWidth.assume(byteCount)
        def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) loadLane(lane + 1)
          else {
            val start =
              address.map(register => Bits.low32(vector(register)(lane))).getOrElse(0L) + scalarOffset.fold(0L)(
                register => Bits.low32(scalar(register))
              ) + offset.toLong
            val base   = sourceScalar64(InstructionSource.Scalar(resource)) & 0x0000ffffffffffffL
            val range  = Bits.low32(scalar(resource + 2))
            val loaded =
              if (base != 0L && (start < 0L || start > range - byteCount.toLong))
                Right(Vec.fill(byteCount)(0.toByte))
              else if (base != 0L)
                readBytes(
                  base + start,
                  byteCount,
                  kernarg,
                  dispatchBytes,
                  kernel.readOnlyAddress,
                  kernel.readOnly,
                  kernel.globalMemory,
                  allocations,
                  memory
                )
              else if (start < 0L || start > scratch(lane).length.toLong - byteCount.toLong)
                Left(ExecutionFailure.Failed(
                  s"buffer D16 load ${operations(index)} at instruction $index lane $lane offset $start size $byteCount exceeds private scratch ${scratch(lane).length}"
                ))
              else Right(Vec.unsafeFromArray(java.util.Arrays.copyOfRange(
                scratch(lane),
                start.toInt,
                start.toInt + byteCount
              )))
            loaded match {
              case Left(error)  => Left(error)
              case Right(bytes) =>
                val value = Bits.low16(LittleEndian.decode(bytes, 0, scalarByteCount))
                vector(dest)(lane) = mergeD16(vector(dest)(lane), value, high)
                loadLane(lane + 1)
            }
          }
        advanceAfter(loadLane(0))
      case DecodedInstruction.BufferStore(address, resource, scalarOffset, src, byteCount, offset) =>
        executeBufferStore(index, address, resource, scalarOffset, src, byteCount, offset, None)
      case DecodedInstruction.BufferStoreD16High(address, resource, scalarOffset, src, byteCount, offset) =>
        executeBufferStore(
          index,
          address,
          resource,
          scalarOffset,
          src,
          byteCount.assume[Interval.Closed[1, 16]],
          offset,
          Some(ScalarByteWidth.assume(byteCount))
        )
    }

    def step(index: Int): Either[ExecutionFailure, Option[Int]] =
      if (index < 0 || index >= operations.length)
        Left(ExecutionFailure.Failed("AMDGPU kernel reached the end without s_endpgm"))
      else {
        val instruction = executable(index)
        if (traceSetControl && index >= 1780 && index <= 1875 && localStart == 0)
          scribe.debug(s"Gemstone AMDGPU set instruction index=$index decoded=${operations(index)}")
        if (
          traceSetOps && localStart == 0 &&
          ((index <= 120) || (index >= 780 && index <= 860) || (index >= 2120 && index <= 2170) ||
            (traceSetTail && index >= 59000))
        )
          scribe.debug(
            s"Gemstone AMDGPU set op kernel=${kernel.name} group=$groupX,$groupY,$groupZ localStart=$localStart index=$index decoded=${operations(index)} active=0x${laneMask(active).toHexString} carry=0x${laneMask(carry).toHexString} v0=0x${vector(0)(0).toHexString} v1=0x${vector(1)(0).toHexString} v2=0x${vector(2)(0).toHexString} v3=0x${vector(3)(0).toHexString} v4=0x${vector(4)(0).toHexString} v5=0x${vector(5)(0).toHexString} v6=0x${vector(6)(0).toHexString} v7=0x${vector(7)(0).toHexString} v8=0x${vector(8)(0).toHexString} v9=0x${vector(9)(0).toHexString}"
          )
        if (traceSetOps && localStart == 0 && index >= 1825 && index <= 1868)
          (0 until math.min(lanes.value, 4)).foreach { lane =>
            scribe.debug(
              s"Gemstone AMDGPU set lookback kernel=${kernel.name} localStart=$localStart index=$index lane=$lane decoded=${operations(index)} active=${active(lane)} carry=${carry(lane)} v3=0x${vector(3)(lane).toHexString} v7=0x${vector(7)(lane).toHexString} v8=0x${vector(8)(lane).toHexString} v9=0x${vector(9)(lane).toHexString} v23=0x${vector(23)(lane).toHexString} v24=0x${vector(24)(lane).toHexString} v25=0x${vector(25)(lane).toHexString} v26=0x${vector(26)(lane).toHexString} v27=0x${vector(27)(lane).toHexString} v28=0x${vector(28)(lane).toHexString} v29=0x${vector(29)(lane).toHexString} v34=0x${vector(34)(lane).toHexString} v35=0x${vector(35)(lane).toHexString} v36=0x${vector(36)(lane).toHexString} v37=0x${vector(37)(lane).toHexString} v38=0x${vector(38)(lane).toHexString} v39=0x${vector(39)(lane).toHexString} v40=0x${vector(40)(lane).toHexString} v41=0x${vector(41)(lane).toHexString}"
            )
          }
        ExecutableInstruction.tag(instruction) match {
          case ExecutableInstruction.VMoveTag =>
            executePackedVMove(instruction, scalar, vector, carry, active, lanes)
            AdvanceStep
          case ExecutableInstruction.GlobalLoadTag =>
            executePackedGlobalLoad(
              instruction,
              index,
              kernel,
              scalar,
              vector,
              active,
              lanes,
              kernarg,
              dispatchBytes,
              allocations,
              memory,
              traceByteSort
            ) match {
              case null                      => AdvanceStep
              case failure: ExecutionFailure => Left(failure)
            }
          case ExecutableInstruction.GlobalStoreTag =>
            executePackedGlobalStore(
              instruction,
              index,
              kernel,
              scalar,
              vector,
              active,
              lanes,
              allocations,
              memory
            ) match {
              case null                      => AdvanceStep
              case failure: ExecutionFailure => Left(failure)
            }
          case ExecutableInstruction.VBinaryTag =>
            executePackedVBinary(
              instruction,
              index,
              scalar,
              vector,
              carry,
              active,
              lanes,
              traceLog,
              traceByteSort
            )
            AdvanceStep
          case ExecutableInstruction.FallbackTag => stepDecoded(index)
          case other                             => Left(ExecutionFailure.Failed(
              s"unknown packed AMDGPU instruction tag $other at $index"
            ))
        }
      }

    var instructionIndex                  = if (initial == null) entryInstruction else initial.nextInstruction
    var running                           = true
    var reachedBarrier                    = false
    var failure: Option[ExecutionFailure] = None
    var pendingInstructionCost            = 0L

    def checkpoint(cost: Int): ExecutionFailure | Null = {
      def flush(): ExecutionFailure | Null =
        if (pendingInstructionCost == 0L) null
        else
          control.checkpoint(math.min(pendingInstructionCost, Int.MaxValue.toLong).toInt.assume[Positive]) match {
            case Left(fault) => ExecutionFailure.Failed(fault.message)
            case Right(())   =>
              pendingInstructionCost = math.max(0L, pendingInstructionCost - Int.MaxValue.toLong)
              if (pendingInstructionCost == 0L) null else flush()
          }

      // Deferred charges cannot share unreserved credits between concurrent waves.
      if (control.parallelism.parallelism > 1) {
        if (cost == 0) null
        else control.checkpoint(cost.assume[Positive]) match {
          case Left(fault) => ExecutionFailure.Failed(fault.message)
          case Right(())   => null
        }
      } else if (cost == 0) flush()
      else if (pendingInstructionCost == 0L && control.cancellation.isCancelled)
        ExecutionFailure.Failed(ExecutionControlFailure.Cancelled.message)
      else if (pendingInstructionCost.toLong + cost.toLong > control.remainingInstructions) {
        val flushFailure = flush()
        if (flushFailure != null) flushFailure
        else
          control.checkpoint(cost.assume[Positive]) match {
            case Left(fault) => ExecutionFailure.Failed(fault.message)
            case Right(())   => null
          }
      } else {
        pendingInstructionCost += cost
        if (pendingInstructionCost >= control.limits.quantum) flush() else null
      }
    }

    while (running && failure.isEmpty)
      checkpoint(math.max(1, active.count)) match {
        case fault: ExecutionFailure => failure = Some(fault)
        case null                    =>
          val stepResult = step(instructionIndex)
          if (
            AMDGPUEnvironment.trace("merge-state") &&
            localStart == 0 && kernel.name.contains("wrapped_merge_sort") &&
            instructionIndex >= 1380 && instructionIndex <= 1500
          )
            scribe.debug(
              s"Gemstone AMDGPU merge state index=$instructionIndex active=0x${laneMask(active).toHexString} s4=0x${pair(scalar, 4).toHexString} s6=0x${pair(scalar, 6).toHexString} s8=0x${pair(scalar, 8).toHexString} s10=0x${pair(scalar, 10).toHexString} v1=0x${vector(1)(0).toHexString} v2=0x${vector(2)(0).toHexString} v3=0x${vector(3)(0).toHexString} v4=0x${vector(4)(0).toHexString} v5=0x${vector(5)(0).toHexString} v6=0x${vector(6)(0).toHexString} v7=0x${vector(7)(0).toHexString} v8=0x${vector(8)(0).toHexString} v9=0x${vector(9)(0).toHexString} v10=0x${vector(10)(0).toHexString} v11=0x${vector(11)(0).toHexString} v12=0x${vector(12)(0).toHexString} v13=0x${vector(13)(0).toHexString} v14=0x${vector(14)(0).toHexString} v21=0x${vector(21)(0).toHexString} v22=0x${vector(22)(0).toHexString} v24=0x${vector(24)(0).toHexString} v25=0x${vector(25)(0).toHexString} v26=0x${vector(26)(0).toHexString} lanes_v3_v4_v6_v24_v25=${(0 until math.min(8, lanes.value)).map(
                  lane =>
                    f"(${vector(3)(lane)}%08x,${vector(4)(lane)}%08x,${vector(6)(lane)}%08x,${vector(24)(lane)}%08x,${vector(25)(lane)}%08x)"
                ).mkString("[", ",", "]")}"
            )
          stepResult match {
            case Left(error)     => failure = Some(error)
            case Right(None)     => running = false
            case Right(Some(-1)) =>
              reachedBarrier = true
              running = false
              instructionIndex += 1
            case Right(Some(AdvanceMarker)) => instructionIndex += 1
            case Right(Some(index))         => instructionIndex = index
          }
      }
    if (failure.isEmpty) checkpoint(0) match {
      case fault: ExecutionFailure => failure = Some(fault)
      case null                    => ()
    }
    failure match {
      case Some(error)            => Left(error)
      case None if reachedBarrier =>
        Right(WaveBoundary.Barrier(
          WaveState(scalar, vector, carry, active, scalarCarry, scratch, instructionIndex)
        ))
      case None =>
        Right(WaveBoundary.End)
    }
  }

  // These memory-instruction helpers consume the allocation-free null sentinel returned by the hot region lookup.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:GemstoneNoAssume"))
  private def readBytes(
      address: Long,
      size: Int,
      kernarg: Array[Byte],
      dispatch: Array[Byte],
      readOnlyAddress: Long,
      readOnly: Array[Byte],
      globalMemory: Array[(Long, Array[Byte])],
      allocations: Array[AllocationRegion],
      memory: ExecutionMemory
  ): Either[ExecutionFailure, Vec[Byte]] = {
    val binding = aliasedRegion(address, allocations)
    if (binding != null)
      memory.read(
        binding.id,
        (binding.offset + address - binding.base).assume[Positive0],
        size.assume[Positive0]
      )
    else localRead(address, size, KernargAddress, kernarg)
      .orElse(localRead(address, size, DispatchAddress, dispatch))
      .orElse(localRead(address, size, readOnlyAddress, readOnly))
      .orElse(globalRead(address, size, globalMemory)) match {
      case Some(bytes) => Right(Vec.unsafeFromArray(bytes))
      case None        =>
        resolve(address, allocations) match {
          case Left(error)               => Left(error)
          case Right((region, relative)) =>
            memory.read(
              region.id,
              (region.offset + relative).assume[Positive0],
              size.assume[Positive0]
            ).leftMap { error =>
              ExecutionFailure.Failed(
                s"AMDGPU memory read address=0x${address.toHexString} size=$size allocation=${region.id.value} offset=${region.offset + relative}: ${error.message}"
              )
            } match {
              case Left(error)   => Left(error)
              case Right(result) =>
                if (AMDGPUEnvironment.trace("memory-bytes") && size >= 8)
                  scribe.debug(s"Gemstone AMDGPU memory read address=0x${address.toHexString} bytes=${result.map(b =>
                      f"0x${b & 0xff}%02x"
                    ).mkString("[", ",", "]")}")
                if (AMDGPUEnvironment.trace("loads") && size == 4)
                  scribe.debug(
                    s"Gemstone AMDGPU resolved read address=0x${address.toHexString} allocation=${region.id.value} relative=$relative value=0x${LittleEndian.decode(result, 0, ScalarByteWidth(4)).toHexString}"
                  )
                Right(result)
            }
        }
    }

  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:GemstoneNoAssume"))
  private def readValue(
      address: Long,
      size: MemoryValueByteWidth,
      kernarg: Array[Byte],
      dispatch: Array[Byte],
      readOnlyAddress: Long,
      readOnly: Array[Byte],
      globalMemory: Array[(Long, Array[Byte])],
      allocations: Array[AllocationRegion],
      memory: ExecutionMemory
  ): Either[ExecutionFailure, Long] = {
    val scalarSize = size.scalarByteWidth
    val binding    = aliasedRegion(address, allocations)
    if (binding != null)
      memory.readValue(binding.id, (binding.offset + address - binding.base).assume[Positive0], size)
    else {
      val kernargOffset = address - KernargAddress
      if (ByteRange.contains(kernarg.length, kernargOffset, size.value.toLong))
        Right(LittleEndian.decode(kernarg, kernargOffset.toInt, scalarSize))
      else {
        val dispatchOffset = address - DispatchAddress
        if (ByteRange.contains(dispatch.length, dispatchOffset, size.value.toLong))
          Right(LittleEndian.decode(dispatch, dispatchOffset.toInt, scalarSize))
        else {
          val readOnlyOffset = address - readOnlyAddress
          if (ByteRange.contains(readOnly.length, readOnlyOffset, size.value.toLong))
            Right(LittleEndian.decode(readOnly, readOnlyOffset.toInt, scalarSize))
          else {
            globalRead(address, size.value, globalMemory).map(bytes =>
              LittleEndian.decode(bytes, 0, scalarSize)
            ).toRight(
              ExecutionFailure.Failed(s"unmapped AMDGPU address=0x${address.toHexString}")
            ).orElse {
              val region = resolveRegion(address, allocations)
              if (region == null)
                Left(ExecutionFailure.Failed(s"unmapped AMDGPU address=0x${address.toHexString}"))
              else {
                val offset = (region.offset + address - region.base).assume[Positive0]
                memory.readValue(region.id, offset, size) match {
                  case Left(error) =>
                    Left(ExecutionFailure.Failed(
                      s"AMDGPU memory read address=0x${address.toHexString} size=$size allocation=${region.id.value} offset=$offset: ${error.message}"
                    ))
                  case success => success
                }
              }
            }
          }
        }
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:GemstoneNoAssume"))
  private def writeBytes(
      address: Long,
      bytes: Array[Byte],
      allocations: Array[AllocationRegion],
      memory: ExecutionMemory,
      globalMemory: Array[(Long, Array[Byte])]
  ): Either[ExecutionFailure, Unit] = {
    val binding = aliasedRegion(address, allocations)
    if (binding != null)
      memory.write(
        binding.id,
        (binding.offset + address - binding.base).assume[Positive0],
        Vec.unsafeFromArray(bytes)
      )
    else globalWrite(address, bytes, globalMemory) match {
      case Some(_) => Right(())
      case None    =>
        resolve(address, allocations) match {
          case Left(error)               => Left(error)
          case Right((region, relative)) =>
            val result = memory.write(
              region.id,
              (region.offset + relative).assume[Positive0],
              Vec.unsafeFromArray(bytes)
            )
            if (AMDGPUEnvironment.trace("memory-bytes") && bytes.length >= 8)
              scribe.debug(
                s"Gemstone AMDGPU memory write address=0x${address.toHexString} bytes=${bytes.map(b =>
                    f"0x${b & 0xff}%02x"
                  ).mkString("[", ",", "]")}"
              )
            result.leftMap { error =>
              ExecutionFailure.Failed(
                s"AMDGPU memory write address=0x${address.toHexString} size=${bytes.length} allocation=${region.id.value} offset=${region.offset + relative}: ${error.message}"
              )
            }
        }
    }

  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:GemstoneNoAssume"))
  private def writeValue(
      address: Long,
      value: Long,
      size: MemoryValueByteWidth,
      allocations: Array[AllocationRegion],
      memory: ExecutionMemory,
      globalMemory: Array[(Long, Array[Byte])]
  ): Either[ExecutionFailure, Unit] = {
    val scalarSize = size.scalarByteWidth
    val binding    = aliasedRegion(address, allocations)
    if (binding != null)
      memory.writeValue(binding.id, (binding.offset + address - binding.base).assume[Positive0], value, size)
    else {
      val bytes = LittleEndian.toArray(value, scalarSize)
      globalWrite(address, bytes, globalMemory) match {
        case Some(_) => Right(())
        case None    =>
          val region = resolveRegion(address, allocations)
          if (region == null)
            Left(ExecutionFailure.Failed(s"unmapped AMDGPU address=0x${address.toHexString}"))
          else {
            val offset = (region.offset + address - region.base).assume[Positive0]
            memory.writeValue(region.id, offset, value, size) match {
              case Left(error) =>
                Left(ExecutionFailure.Failed(
                  s"AMDGPU memory write address=0x${address.toHexString} size=$size allocation=${region.id.value} offset=$offset: ${error.message}"
                ))
              case success => success
            }
          }
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:GemstoneNoAssume"))
  private def atomicModifyValue(
      address: Long,
      size: MemoryValueByteWidth,
      allocations: Array[AllocationRegion],
      memory: ExecutionMemory,
      globalMemory: Array[(Long, Array[Byte])],
      globalMemoryLocks: Array[ReentrantLock]
  )(update: Long => Long): Either[ExecutionFailure, Long] = {
    val scalarSize = size.scalarByteWidth
    val binding    = aliasedRegion(address, allocations)
    if (binding != null)
      memory.atomicModifyValue(
        binding.id,
        (binding.offset + address - binding.base).assume[Positive0],
        size
      )(update)
    else {
      val local = globalMemory.iterator.zip(globalMemoryLocks.iterator).find { case ((base, bytes), _) =>
        address >= base && address - base <= bytes.length.toLong - size.value.toLong
      }
      local match {
        case Some(((base, bytes), lock)) =>
          lock.withLock {
            val offset   = (address - base).toInt
            val previous = LittleEndian.decode(bytes, offset, scalarSize)
            LittleEndian.write(update(previous), bytes, offset, scalarSize)
            Right(previous)
          }
        case None =>
          val region = resolveRegion(address, allocations)
          if (region == null)
            Left(ExecutionFailure.Failed(s"unmapped AMDGPU address=0x${address.toHexString}"))
          else {
            val offset = (region.offset + address - region.base).assume[Positive0]
            memory.atomicModifyValue(region.id, offset, size)(update).left.map { error =>
              ExecutionFailure.Failed(
                s"AMDGPU atomic address=0x${address.toHexString} size=$size allocation=${region.id.value} offset=$offset: ${error.message}"
              )
            }
          }
      }
    }
  }

  private def globalRead(address: Long, size: Int, regions: Array[(Long, Array[Byte])]): Option[Array[Byte]] =
    regions.iterator.collectFirst {
      case (base, bytes) if address >= base && address - base <= bytes.length.toLong - size.toLong =>
        java.util.Arrays.copyOfRange(bytes, (address - base).toInt, (address - base).toInt + size)
    }

  private def globalWrite(address: Long, value: Array[Byte], regions: Array[(Long, Array[Byte])]): Option[Unit] =
    regions.iterator.find { case (base, bytes) =>
      address >= base && address - base <= bytes.length.toLong - value.length.toLong
    } match {
      case Some((base, bytes)) =>
        Array.copy(value, 0, bytes, (address - base).toInt, value.length)
        Some(())
      case None => None
    }

  // Null is the allocation-free miss sentinel in this memory-instruction hot path.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def aliasedRegion(address: Long, allocations: Array[AllocationRegion]): AllocationRegion | Null = {
    var index                           = 0
    var result: AllocationRegion | Null = null
    while (index < allocations.length && allocations(index).alias && result == null) {
      val region = allocations(index)
      if (address >= region.base && address - region.base < region.span) result = region
      index += 1
    }
    result
  }

  // Null is the allocation-free miss sentinel in this memory-instruction hot path.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def resolveRegion(address: Long, allocations: Array[AllocationRegion]): AllocationRegion | Null = {
    var index                           = 0
    var result: AllocationRegion | Null = null
    while (index < allocations.length && result == null) {
      val region = allocations(index)
      if (address >= region.base && address - region.base < region.span) result = region
      index += 1
    }
    // Device pointers are stable across dispatches.  A kernel may therefore
    // dereference a pointer loaded from an earlier dispatch even though that
    // allocation is not present in this dispatch's kernarg list.  Recover the
    // identity encoded by the interpreter's virtual address span so the
    // backing ExecutionMemory can report the real allocation/range error.
    if (result == null && address >= AllocationBase) {
      val relative = address - AllocationBase
      val slot     = relative / AllocationSpan
      if (slot < Long.MaxValue) {
        result = AllocationRegion(
          AllocationBase + slot * AllocationSpan,
          AllocationId.applyUnsafe(slot + 1L),
          0L
        )
      }
    }
    result
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def resolve(
      address: Long,
      allocations: Array[AllocationRegion]
  ): Either[ExecutionFailure, (AllocationRegion, Long)] = {
    val region = resolveRegion(address, allocations)
    if (region == null)
      Left(ExecutionFailure.Failed(s"unmapped AMDGPU address=0x${address.toHexString}"))
    else Right(region -> (address - region.base))
  }

  private def localRead(address: Long, size: Int, base: Long, bytes: Array[Byte]): Option[Array[Byte]] = {
    val offset = address - base
    Option.when(ByteRange.contains(bytes.length, offset, size.toLong))(
      java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size)
    )
  }

  private def requireBytes(bytes: Array[Byte], offset: Int, size: Int): Either[String, Unit] =
    Either.cond(
      ByteRange.contains(bytes.length, offset.toLong, size.toLong),
      (),
      s"truncated $size-byte instruction"
    )

  private def unsignedInt(bytes: Vec[Byte], offset: Int): Long =
    LittleEndian.decode(bytes, offset, ScalarByteWidth(4))

  private def unsignedInt(bytes: Array[Byte], offset: Int): Long =
    LittleEndian.decode(bytes, offset, ScalarByteWidth(4))

  private def pair(registers: Array[Long], first: Int): Long =
    Bits.join32(registers(first), registers(first + 1))

  private def pair(registers: VectorRegisters, first: Int, lane: Int): Long =
    Bits.join32(registers(first)(lane), registers(first + 1)(lane))

  private def setPair(registers: Array[Long], first: Int, value: Long): Unit = {
    registers(first) = Bits.low32(value)
    registers(first + 1) = Bits.high32(value)
  }

  private def selectSubDword(value: Long, selection: SubDwordSelection, signExtend: Boolean): Long = {
    val mask     = Bits.mask(selection.width)
    val selected = (value >>> selection.shift) & mask
    if (signExtend) Bits.signExtend(selected, selection.width) else selected
  }

  private def placeSubDword(
      existing: Long,
      value: Long,
      selection: SubDwordSelection,
      unused: SubDwordDestinationUnused
  ): Long = {
    val mask     = Bits.mask(selection.width) << selection.shift
    val selected = (value << selection.shift) & mask
    unused match {
      case SubDwordDestinationUnused.Pad        => Bits.low32(selected)
      case SubDwordDestinationUnused.Preserve   => Bits.low32((existing & ~mask) | selected)
      case SubDwordDestinationUnused.SignExtend =>
        val endMask =
          if (selection.upperBit.value == 32) Bits.Mask32
          else Bits.mask(selection.upperBit)
        val upper =
          if ((selected & (1L << (selection.upperBit.value - 1))) != 0L) Bits.Mask32 & ~endMask
          else 0L
        Bits.low32(selected | upper)
    }
  }

  private[amdgpu] def permuteBytes(srcA: Long, srcB: Long, selectors: Long): Long = {
    val sources = Bits.join32(srcB, srcA)
    (0 until 4).foldLeft(0L) { (result, byte) =>
      // V_PERM uses the full four-bit selector.  Values 0..7 address the
      // concatenated second/first sources; selectors 8..15 produce zero
      // (in particular, 0xc is the canonical zero selector used by LLVM).
      val selector = ((selectors >>> (byte * 8)) & 0xfL).toInt
      if (selector < 8)
        result | (((sources >>> (selector * 8)) & 0xffL) << (byte * 8))
      else result
    }
  }

  private[amdgpu] def effectiveGroupSegmentSize(fixed: Int, dynamic: Int): Int =
    Math.addExact(fixed, dynamic)

  private[amdgpu] def ldsOffset(address: Long): Long = address & 0xffffL

  // A negative offset is impossible for a valid LDS byte range.  Returning a
  // primitive sentinel keeps the per-lane DS read path allocation-free; the
  // detailed failure is constructed only for an invalid guest access.
  private inline val InvalidLdsOffset = -1

  private def checkedLdsOffset(lds: Array[Byte], address: Long, size: Int): Int = {
    val offset = ldsOffset(address)
    if (ByteRange.contains(lds.length, offset, size.toLong)) offset.toInt else InvalidLdsOffset
  }

  private def ldsFailure(address: Long, size: Int): ExecutionFailure = {
    val offset = ldsOffset(address)
    ExecutionFailure.Failed(
      f"out-of-bounds AMDGPU LDS read at 0x$address%x (offset 0x$offset%x, $size bytes)"
    )
  }

  private def readLDS(lds: Array[Byte], address: Long, size: Int): Either[ExecutionFailure, Array[Byte]] = {
    val offset = checkedLdsOffset(lds, address, size)
    if (offset == InvalidLdsOffset) Left(ldsFailure(address, size))
    else Right(java.util.Arrays.copyOfRange(lds, offset, offset + size))
  }

  private def writeLDS(lds: Array[Byte], address: Long, bytes: Array[Byte]): Either[ExecutionFailure, Unit] = {
    val offset = ldsOffset(address)
    Either.cond(
      ByteRange.contains(lds.length, offset, bytes.length.toLong), {
        Array.copy(bytes, 0, lds, offset.toInt, bytes.length)
        ()
      },
      ExecutionFailure.Failed(
        f"out-of-bounds AMDGPU LDS write at 0x$address%x (offset 0x$offset%x, ${bytes.length} bytes)"
      )
    )
  }

  private def vectorBytes(
      registers: VectorRegisters,
      first: Int,
      lane: Int,
      size: Int
  ): Array[Byte] =
    Array.tabulate(size) { byte =>
      val register = first + byte / 4
      ((registers(register)(lane) >>> ((byte % 4) * 8)) & 0xffL).toByte
    }

  /** Consume the owned memory snapshot directly; copying it into another array adds no isolation. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneNoAssume"))
  private def writeVectorBytes(
      registers: VectorRegisters,
      first: Int,
      lane: Int,
      bytes: Vec[Byte]
  ): Unit = {
    var offset   = 0
    var register = first
    while (offset < bytes.size) {
      val width = math.min(4, bytes.size - offset)
      // The loop guard proves at least one byte remains; each register consumes at most four bytes.
      registers(register)(lane) = LittleEndian.decode(
        bytes,
        offset,
        ScalarByteWidth.assume(width)
      )
      offset += width
      register += 1
    }
  }

  private def writeVectorBytes(
      registers: VectorRegisters,
      first: Int,
      lane: Int,
      bytes: Array[Byte]
  ): Unit = writeVectorBytes(registers, first, lane, bytes, 0, bytes.length)

  // Pack complete VGPR words in one pass on the memory hot path.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def writeVectorBytes(
      registers: VectorRegisters,
      first: Int,
      lane: Int,
      bytes: Array[Byte],
      sourceOffset: Int,
      size: Int
  ): Unit = {
    // VGPRs are dword-addressed.  Packing one complete dword at a time
    // avoids a division/modulo and a read-modify-write for every byte while
    // preserving the old partial-dword behavior (unused high bits are zero).
    var offset   = 0
    var register = first
    while (offset + 4 <= size) {
      registers(register)(lane) =
        (bytes(sourceOffset + offset).toLong & 0xffL) |
          ((bytes(sourceOffset + offset + 1).toLong & 0xffL) << 8) |
          ((bytes(sourceOffset + offset + 2).toLong & 0xffL) << 16) |
          ((bytes(sourceOffset + offset + 3).toLong & 0xffL) << 24)
      offset += 4
      register += 1
    }
    if (offset < size) {
      var value = 0L
      var shift = 0
      while (offset + shift < size) {
        value |= (bytes(sourceOffset + offset + shift).toLong & 0xffL) << (shift * 8)
        shift += 1
      }
      registers(register)(lane) = value
    }
  }

  private def writeVectorValue(
      registers: VectorRegisters,
      first: Int,
      lane: Int,
      value: Long,
      size: Int
  ): Unit = {
    registers(first)(lane) = Bits.low32(value)
    if (size > 4) registers(first + 1)(lane) = Bits.high32(value)
  }

  private def source64(
      source: InstructionSource,
      lane: Int,
      scalar: Array[Long],
      vector: VectorRegisters
  ): Long = source match {
    case InstructionSource.Scalar(index)         => pair(scalar, index)
    case InstructionSource.VectorRegister(index) => pair(vector, index, lane)
    case InstructionSource.Constant(raw)         => raw
    case InstructionSource.Literal(raw)          => raw
  }

  private def sourceFloating64(
      source: InstructionSource,
      lane: Int,
      scalar: Array[Long],
      vector: VectorRegisters
  ): Long = source match {
    case InstructionSource.Literal(raw) =>
      IEEE754.doubleToBinary64(floatingLiteralToDouble(raw))
    case InstructionSource.Constant(raw) =>
      val value = raw match {
        case 0x3f000000L => 0.5
        case 0xbf000000L => -0.5
        case 0x3f800000L => 1.0
        case 0xbf800000L => -1.0
        case 0x40000000L => 2.0
        case 0xc0000000L => -2.0
        case 0x40800000L => 4.0
        case 0xc0800000L => -4.0
        case 0x3e22f983L => 0.15915494
        case _           => raw.toDouble
      }
      IEEE754.doubleToBinary64(value)
    case _ => source64(source, lane, scalar, vector)
  }

  private def signed13(value: Int): Long = Bits.signExtend(value.toLong, BitWidth(13))

  // FLAT uses a 12-bit unsigned byte offset on GFX10+ (the encoded bit 12
  // is ignored), unlike GLOBAL/SCRATCH which use a signed 13-bit offset.
  // FLAT offsets are signed 12-bit byte displacements.  Keeping the encoded
  // immediate as an unsigned mask makes reverse iterators (and any pointer
  // arithmetic that walks backwards from an end iterator) wrap by +4096
  // instead of stepping back by the requested number of bytes.
  private def flatOffset(value: Int): Long = Bits.signExtend(value.toLong, BitWidth(12))
}
