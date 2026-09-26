package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*
import VMIR.*
import VirtualMachine.*
import VMLoadedProgram.GlobalBinding

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.Bits
import net.kurobako.gemstone.binary.BitWidth
import net.kurobako.gemstone.binary.Binary16
import net.kurobako.gemstone.binary.Binary32
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.memory.MemoryValueByteWidth
import net.kurobako.gemstone.vm.{AddressSpace, Fault, KernelTask, Memory, MemoryRegion, Scheduler, TaskControl}
import net.kurobako.gemstone.vm.LaunchGeometry
import net.kurobako.gemstone.vm.LaunchGeometry.Coordinates

import cats.syntax.either.*

import java.math.BigInteger
import java.util.concurrent.locks.ReentrantLock

private[amdgpu] object VMInterpreter {
  private val KernargAddress                          = 0x100000000000L
  private val DispatchAddress                         = 0x200000000000L
  private val AdvanceMarker                           = Int.MinValue
  private val AdvanceStep: Either[Fault, Option[Int]] = Right(Some(AdvanceMarker))

  private enum WaveBoundary {
    case Barrier(state: WaveState)
    case End
  }

  /** Convert a lane-wise operation's success to the shared advance marker. Keeping this path allocation-free matters
    * for memory and DS instructions, which otherwise create a fresh `Some(index + 1)` for every instruction.
    */
  private def advanceAfter(
      result: Either[Fault, Unit]
  ): Either[Fault, Option[Int]] = result match {
    case Left(error) => Left(error)
    case Right(_)    => AdvanceStep
  }
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
  // The 1200-bit fixed-point representation used by the GFX9
  // v_trig_preop_f64 instruction for extracting a 53-bit chunk of 2/pi.
  private val TwoOverPi1200 = BigInteger(
    "a2f9836e4e441529fc2757d1f534ddc0db6295993c439041fe5163abdebbc561b7246e3a424dd2e006492eea09d1921cfe1deb1cb129a73ee88235f52ebb4484e99c7026b45f7e413991d639835339f49c845f8bbdf9283b1ff897ffde05980fef2f118b5a0a6d1f6d367ecf27cb09b74f463f669e5fea2d7527bac7ebe5f17b3d0739f78a5292ea6bfb5fb11f8d5d0856033046fc7b",
    16
  )
  private val TrigPreopMask = BigInteger.ONE.shiftLeft(53).subtract(BigInteger.ONE)

  private[amdgpu] def unsignedMultiplyHigh32(left: Long, right: Long): Long =
    Bits.high32(Bits.low32(left) * Bits.low32(right))
  private[amdgpu] def signedMultiplyHigh32(left: Long, right: Long): Long =
    Bits.high32(Bits.signed32(left).toLong * Bits.signed32(right).toLong)
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
  private[amdgpu] def scalarMinMax(
      operation: ScalarMinMaxOperation,
      left: Long,
      right: Long
  ): (Long, Boolean) = {
    val left32  = Bits.low32(left)
    val right32 = Bits.low32(right)
    operation match {
      case ScalarMinMaxOperation.MinimumU32 => (math.min(left32, right32), left32 < right32)
      case ScalarMinMaxOperation.MaximumU32 => (math.max(left32, right32), left32 > right32)
      case ScalarMinMaxOperation.MinimumI32 =>
        (Bits.low32(math.min(left32.toInt, right32.toInt).toLong), left32.toInt < right32.toInt)
      case ScalarMinMaxOperation.MaximumI32 =>
        (Bits.low32(math.max(left32.toInt, right32.toInt).toLong), left32.toInt > right32.toInt)
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
    val signed = Bits.signed32(value)
    if (signed == 0 || signed == -1) 0xffffffffL
    else if (signed >= 0) Integer.numberOfLeadingZeros(signed).toLong
    else Integer.numberOfLeadingZeros(~signed).toLong
  }
  private[amdgpu] def firstBitHigh32(value: Long): Long = {
    val unsigned = Bits.signed32(value)
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
  private[amdgpu] def trigF32(operation: VectorConvertOperation, value: Float): Float = operation match {
    // AMD's scalarized sin/cos instructions use turns, not radians: one
    // instruction unit is 2*pi radians.  HIP kernels commonly multiply
    // radians by 1/(2*pi) before reaching these instructions.
    case VectorConvertOperation.SineF32   => math.sin(2.0 * math.Pi * value.toDouble).toFloat
    case VectorConvertOperation.CosineF32 => math.cos(2.0 * math.Pi * value.toDouble).toFloat
    case _ => throw IllegalArgumentException(s"$operation is not a trigonometric operation")
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
  private[amdgpu] def divisionFixupF32(quotient: Float, denominator: Float, numerator: Float): Float = {
    val negative =
      ((java.lang.Float.floatToRawIntBits(numerator) ^
        java.lang.Float.floatToRawIntBits(denominator)) & 0x80000000) != 0
    val sign = if (negative) -1.0f else 1.0f
    if (
      numerator.isNaN || denominator.isNaN || (numerator.isInfinite && denominator.isInfinite) ||
      (numerator == 0.0f && denominator == 0.0f)
    ) Float.NaN
    else if (denominator == 0.0f || numerator.isInfinite) Math.copySign(Float.PositiveInfinity, sign)
    else if (denominator.isInfinite || numerator == 0.0f) Math.copySign(0.0f, sign)
    else if (quotient.isNaN) numerator / denominator
    else Math.copySign(math.abs(quotient), sign)
  }

  private[amdgpu] def fmaF32(
      operation: VectorMadF32Operation,
      leftBits: Long,
      rightBits: Long,
      addendBits: Long
  ): Binary32 = {
    val left   = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftBits)))
    val right  = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(rightBits)))
    val addend = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(addendBits)))
    val result = operation match {
      case VectorMadF32Operation.FusedMultiplyAdd => Math.fma(left, right, addend)
      case VectorMadF32Operation.MultiplyAdd      => left * right + addend
    }
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
  private def comparisonResult(
      relation: VectorComparisonRelation,
      less: Boolean,
      equal: Boolean,
      greater: Boolean,
      unordered: Boolean
  ): Boolean = relation match {
    case VectorComparisonRelation.Equal             => equal
    case VectorComparisonRelation.NotEqual          => !equal
    case VectorComparisonRelation.Less              => less
    case VectorComparisonRelation.LessOrEqual       => less || equal
    case VectorComparisonRelation.Greater           => greater
    case VectorComparisonRelation.GreaterOrEqual    => greater || equal
    case VectorComparisonRelation.NotLess           => !less
    case VectorComparisonRelation.NotLessOrEqual    => !(less || equal)
    case VectorComparisonRelation.NotGreater        => !greater
    case VectorComparisonRelation.NotGreaterOrEqual => !(greater || equal)
    case VectorComparisonRelation.Ordered           => !unordered
    case VectorComparisonRelation.Unordered         => unordered
    case VectorComparisonRelation.NotLessOrGreater  => !less && !greater
    case VectorComparisonRelation.Class             => false
  }

  private[amdgpu] def compare(operation: VectorComparison, left: Long, right: Long): Boolean = {
    def integer(leftValue: Long, rightValue: Long): Boolean = {
      val ordering = java.lang.Long.compare(leftValue, rightValue)
      comparisonResult(operation.relation, ordering < 0, ordering == 0, ordering > 0, unordered = false)
    }
    def unsigned(leftValue: Long, rightValue: Long): Boolean = {
      val ordering = java.lang.Long.compareUnsigned(leftValue, rightValue)
      comparisonResult(operation.relation, ordering < 0, ordering == 0, ordering > 0, unordered = false)
    }
    def floating(leftValue: Double, rightValue: Double): Boolean =
      comparisonResult(
        operation.relation,
        leftValue < rightValue,
        leftValue == rightValue,
        leftValue > rightValue,
        leftValue.isNaN || rightValue.isNaN
      )

    operation.kind match {
      case VectorComparisonKind.Signed16 =>
        integer(Bits.signExtend(Bits.low16(left), BitWidth(16)), Bits.signExtend(Bits.low16(right), BitWidth(16)))
      case VectorComparisonKind.Signed32 =>
        integer(Bits.signExtend(Bits.low32(left), BitWidth(32)), Bits.signExtend(Bits.low32(right), BitWidth(32)))
      case VectorComparisonKind.Signed64   => integer(left, right)
      case VectorComparisonKind.Unsigned16 => unsigned(Bits.low16(left), Bits.low16(right))
      case VectorComparisonKind.Unsigned32 => unsigned(Bits.low32(left), Bits.low32(right))
      case VectorComparisonKind.Unsigned64 => unsigned(left, right)
      case VectorComparisonKind.Float16    =>
        floating(
          IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))).toDouble,
          IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right))).toDouble
        )
      case VectorComparisonKind.Float32 =>
        floating(
          IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))).toDouble,
          IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right))).toDouble
        )
      case VectorComparisonKind.Float64 =>
        floating(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right))
      case VectorComparisonKind.ClassFloat32 =>
        (floatClassMask(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left)))) & Bits.low32(right)) != 0L
      case VectorComparisonKind.ClassFloat64 =>
        (doubleClassMask(IEEE754.binary64ToDouble(left)) & Bits.low32(right)) != 0L
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

  private final class MemoryContext(delegate: Memory, addresses: AddressSpace)
      extends Memory {
    override def flushHostWrites(): Either[Fault, Unit]                  = delegate.flushHostWrites()
    override def invalidateHostReads(): Unit                             = delegate.invalidateHostReads()
    private def complete[A](read: => Either[Fault, A]): Either[Fault, A] = read match {
      case Left(Fault.PendingMemory) => delegate.flushHostReads().flatMap(_ => complete(read))
      case result                    => result
    }
    override def read(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[Fault, Vec[Byte]] = complete(delegate.read(region, offset, size))

    override def write(
        region: MemoryRegion,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] = delegate.write(region, offset, bytes)

    override def readValue(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: MemoryValueByteWidth
    ): Either[Fault, Long] = complete(delegate.readValue(region, offset, size))

    override def writeValue(
        region: MemoryRegion,
        offset: Long :| Positive0,
        value: Long,
        size: MemoryValueByteWidth
    ): Either[Fault, Unit] = delegate.writeValue(region, offset, value, size)

    override def atomicModifyValue(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: MemoryValueByteWidth
    )(update: Long => Long): Either[Fault, Long] =
      complete(delegate.atomicModifyValue(region, offset, size)(update))

    def prepareReads(ranges: Vec[(GuestAddress, Int :| Positive0)]): Either[Fault, Unit] =
      addresses.prepareReads(ranges)

    def read(address: GuestAddress, size: Int :| Positive0): Either[Fault, Vec[Byte]] =
      complete(addresses.read(delegate, address, size))

    def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
      addresses.write(delegate, address, bytes)

    def readValue(address: GuestAddress, size: MemoryValueByteWidth): Either[Fault, Long] =
      complete(addresses.readValue(delegate, address, size))

    def writeValue(address: GuestAddress, value: Long, size: MemoryValueByteWidth): Either[Fault, Unit] =
      addresses.writeValue(delegate, address, value, size)

    def atomicModifyValue(address: GuestAddress, size: MemoryValueByteWidth)(
        update: Long => Long
    ): Either[Fault, Long] = addresses.atomicModifyValue(delegate, address, size)(update)
  }
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
          case InstructionSource.Scalar(Decoding.VccLo)  => Bits.low32(carry.register)
          case InstructionSource.Scalar(Decoding.VccHi)  => Bits.high32(carry.register)
          case InstructionSource.Scalar(Decoding.ExecLo) => Bits.low32(active.register)
          case InstructionSource.Scalar(Decoding.ExecHi) => Bits.high32(active.register)
          case InstructionSource.Scalar(index)           => scalar(index)
          case InstructionSource.Constant(raw)           => raw
          case InstructionSource.Literal(raw)            => raw
          case InstructionSource.VectorRegister(_)       => 0L
        }
        var lane = 0
        while (lane < lanes.value) {
          if (active(lane)) destination(lane) = Bits.low32(value)
          lane += 1
        }
    }
  }
  def execute(
      prepared: Kernel,
      dispatch: KernelTask,
      memory: Memory,
      addresses: AddressSpace,
      control: TaskControl,
      scheduler: Scheduler,
      globalBindings: Vec[GlobalBinding]
  ): Either[Fault, Unit] =
    for {
      kernargSize <- effectiveKernargSize(prepared.source, dispatch.arguments)
        .leftMap(detail => Fault.InvalidTask(detail))
      kernarg <- packArguments(dispatch.arguments, kernargSize, addresses, globalBindings)
      context = MemoryContext(memory, addresses)
      _ <- runDispatch(
        prepared.source,
        prepared.instructions,
        prepared.instructionIndices,
        prepared.instructionOffsets,
        prepared.entryInstruction,
        kernarg,
        Vec.toArray(globalBindings),
        dispatch,
        context,
        control,
        scheduler
      )
    } yield ()

  private[amdgpu] def effectiveKernargSize(
      kernel: CodeObjectKernel,
      arguments: Vec[KernelTask.Argument]
  ): Either[String, Int] =
    if (kernel.kernargSize != 0 || arguments.isEmpty || kernel.codeAddress == 0L) Right(kernel.kernargSize)
    else
      arguments.foldLeft[Either[String, Int]](Right(0)) { (total, argument) =>
        total.flatMap { current =>
          val size = argument match {
            case KernelTask.Argument.Scalar(bytes) => Right(bytes.length)
            case KernelTask.Argument.Memory(_, _)  => Right(8)
            case KernelTask.Argument.Local(_)      => Left("gfx900 local kernargs are not implemented")
          }
          size.flatMap(value =>
            Either.cond(current <= Int.MaxValue - value, current + value, "kernel arguments are too large")
          )
        }
      }

  // Kernarg packing resolves VM memory references through the runtime-owned address space.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def packArguments(
      arguments: Vec[KernelTask.Argument],
      kernargSize: Int,
      addresses: AddressSpace,
      globalBindings: Vec[GlobalBinding]
  ): Either[Fault, Array[Byte]] = {
    val packed        = Array.ofDim[Byte](kernargSize)
    var packedOffset  = 0
    var argumentIndex = 0
    var failure       = Option.empty[Fault]
    while (argumentIndex < arguments.length && failure.isEmpty) {
      arguments(argumentIndex) match {
        case KernelTask.Argument.Scalar(bytes) =>
          if (packedOffset == kernargSize) failure = Some(Fault.InvalidTask(
            s"kernel descriptor requires $kernargSize kernarg bytes, received trailing argument $argumentIndex"
          ))
          else if (packedOffset > kernargSize - bytes.length)
            failure = Some(Fault.InvalidTask(
              s"kernel descriptor requires $kernargSize kernarg bytes, received more than $kernargSize"
            ))
          else {
            val _ = bytes.copyToArray(packed, packedOffset, bytes.length)
            packedOffset += bytes.length
          }
        case KernelTask.Argument.Memory(region, offset) =>
          if (packedOffset == kernargSize) failure = Some(Fault.InvalidTask(
            s"kernel descriptor requires $kernargSize kernarg bytes, received trailing argument $argumentIndex"
          ))
          else if (packedOffset > kernargSize - 8)
            failure = Some(Fault.InvalidTask(
              s"kernel descriptor requires $kernargSize kernarg bytes, received more than $kernargSize"
            ))
          else argumentAddress(region, offset, addresses, globalBindings) match {
            case Left(error)    => failure = Some(error)
            case Right(address) =>
              LittleEndian.write(address.value, packed, packedOffset, ScalarByteWidth.Qword)
              packedOffset += 8
          }
        case KernelTask.Argument.Local(_) =>
          failure = Some(Fault.Unsupported("gfx900 local kernargs are not implemented"))
      }
      argumentIndex += 1
    }
    failure match {
      case Some(error)                         => Left(error)
      case None if packedOffset != kernargSize =>
        Left(Fault.InvalidTask(
          s"kernel descriptor requires $kernargSize kernarg bytes, received $packedOffset"
        ))
      case None => Right(packed)
    }
  }

  private def argumentAddress(
      region: MemoryRegion,
      offset: Long :| Positive0,
      addresses: AddressSpace,
      globalBindings: Vec[GlobalBinding]
  ): Either[Fault, GuestAddress] =
    globalBindings.find(binding =>
      binding.allocation == region &&
        offset >= binding.offset &&
        offset - binding.offset <= binding.size.toLong
    ) match {
      case Some(binding) => Right(GuestAddress(binding.address + offset - binding.offset))
      case None          => addresses.address(region, offset)
    }

  // Workgroups advance through the shared bounded scheduler without materializing a result collection.
  private def runDispatch(
      kernel: CodeObjectKernel,
      operations: Vec[Instruction],
      instructionIndices: Map[Int, Int],
      instructionOffsets: Vec[Int],
      entryInstruction: Int,
      kernarg: Array[Byte],
      allocations: Array[GlobalBinding],
      dispatch: KernelTask,
      memory: Memory,
      control: TaskControl,
      scheduler: Scheduler
  ): Either[Fault, Unit] =
    for {
      localCount <- dispatch.geometry.groupSize.volumeInt
        .leftMap(detail => Fault.InvalidTask(detail))
      groupCount <- dispatch.geometry.groupCount.volume
        .leftMap(detail => Fault.InvalidTask(detail))
      _ <- {
        val dispatchBytes = Array.ofDim[Byte](64)
        LittleEndian.write(dispatch.geometry.groupSize.x.toLong, dispatchBytes, 4, ScalarByteWidth.Word)
        LittleEndian.write(dispatch.geometry.groupSize.y.toLong, dispatchBytes, 6, ScalarByteWidth.Word)
        LittleEndian.write(dispatch.geometry.groupSize.z.toLong, dispatchBytes, 8, ScalarByteWidth.Word)
        LittleEndian.write(dispatch.geometry.globalExtent.x.toLong, dispatchBytes, 12, ScalarByteWidth.Dword)
        LittleEndian.write(dispatch.geometry.globalExtent.y.toLong, dispatchBytes, 16, ScalarByteWidth.Dword)
        LittleEndian.write(dispatch.geometry.globalExtent.z.toLong, dispatchBytes, 20, ScalarByteWidth.Dword)
        LittleEndian.write(dispatch.privateSegmentBytes.toLong, dispatchBytes, 24, ScalarByteWidth.Dword)
        LittleEndian.write(dispatch.groupSegmentBytes.toLong, dispatchBytes, 28, ScalarByteWidth.Dword)
        def runGroupIndex(index: Long :| Positive0): Either[Fault, Unit] =
          dispatch.geometry.groupCoordinates(index)
            .leftMap(detail => Fault.InvalidTask(detail))
            .fold(
              error => Left(error),
              group =>
                runGroup(
                  kernel,
                  operations,
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
                  control,
                  scheduler
                )
            )
        val refinedGroupCount = (groupCount: Long).refineUnsafe[Positive0]
        val parallelGroups    = scheduler.shouldParallel(
          refinedGroupCount,
          localCount.toLong.refineUnsafe[Positive0]
        )
        scheduler.forEach(refinedGroupCount, parallel = parallelGroups)(runGroupIndex(_))
      }
    } yield ()

  // Wave rendezvous is a measured mutable state machine; null marks absent wave state and the first failure.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null", "scalafix:GemstoneNoAssume"))
  private def runGroup(
      kernel: CodeObjectKernel,
      operations: Vec[Instruction],
      instructionIndices: Map[Int, Int],
      instructionOffsets: Vec[Int],
      entryInstruction: Int,
      kernarg: Array[Byte],
      dispatchBytes: Array[Byte],
      allocations: Array[GlobalBinding],
      dispatch: KernelTask,
      localCount: Int,
      group: Coordinates,
      memory: Memory,
      control: TaskControl,
      scheduler: Scheduler
  ): Either[Fault, Unit] = {
    val lds = Array.ofDim[Byte](effectiveGroupSegmentSize(kernel.groupSegmentSize, dispatch.groupSegmentBytes))
    // GFX6-9 code objects use wave64; RDNA and later objects normally select
    // wave32.  The wave width is part of the executable target, not a global
    // property of the interpreter, and controls both lane partitioning and
    // EXEC/VCC mask width.
    val waveSize: WaveSize = kernel.target.family match {
      case Family.GFX6 | Family.GFX7 | Family.GFX8 | Family.GFX9 | Family.CDNA1 | Family.CDNA2 | Family.CDNA3 | Family.CDNA4 |
          Family.CDNA5 => WaveSize(64)
      case _ => WaveSize(32)
    }
    val waveCount = (localCount + waveSize.value - 1) / waveSize.value
    val states    = Array.ofDim[WaveState | Null](waveCount)
    // A wave can leave the kernel before another wave reaches a workgroup
    // barrier.  That is legal for barriers in divergent control flow when
    // the remaining waves eventually arrive at the same hardware barrier;
    // retain the completion state so an ended wave is not restarted on the
    // next barrier-release round.
    val finished              = Array.fill(waveCount)(false)
    var complete              = false
    var failure: Fault | Null = null
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
          val initialActive = {
            var active                          = 0L
            var lane                            = 0
            var coordinateFailure: Fault | Null = null
            while (lane < lanes.value && coordinateFailure == null) {
              dispatch.geometry.localCoordinates(start + lane) match {
                case Left(detail) => coordinateFailure = Fault.InvalidTask(detail)
                case Right(local) =>
                  dispatch.geometry.globalCoordinatesLong(group, local) match {
                    case Left(detail)  => coordinateFailure = Fault.InvalidTask(detail)
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
                control,
                scheduler
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
      operations: Vec[Instruction],
      instructionIndices: Map[Int, Int],
      instructionOffsets: Vec[Int],
      entryInstruction: Int,
      kernarg: Array[Byte],
      dispatchBytes: Array[Byte],
      allocations: Array[GlobalBinding],
      geometry: LaunchGeometry,
      group: Coordinates,
      localStart: Int,
      lanes: LaneCount,
      waveSize: WaveSize,
      lds: Array[Byte],
      initialActive: Long,
      initial: WaveState | Null,
      memory: Memory,
      control: TaskControl,
      scheduler: Scheduler
  ): Either[Fault, WaveBoundary] = {
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
        case Left(detail) => return Left(Fault.InvalidTask(detail))
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
    val maskAccumulator = Array.ofDim[Long](1)
    val codeBase     = if (kernel.textAddress != 0L && kernel.text.nonEmpty) kernel.textAddress else kernel.codeAddress
    var nextUserSgpr = 0
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
      if (destination != Decoding.NullSgpr) {
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
      case InstructionSource.Scalar(Decoding.VccLo)  => Bits.low32(carry.register)
      case InstructionSource.Scalar(Decoding.VccHi)  => Bits.high32(carry.register)
      case InstructionSource.Scalar(Decoding.ExecLo) => Bits.low32(active.register)
      case InstructionSource.Scalar(Decoding.ExecHi) => Bits.high32(active.register)
      case InstructionSource.Scalar(index)           => scalar(index)
      case InstructionSource.VectorRegister(index)   => vector(index)(lane)
      case InstructionSource.Constant(raw)           => raw
      case InstructionSource.Literal(raw)            => raw
    }

    def sourceScalar64(value: InstructionSource): Long = value match {
      case InstructionSource.Scalar(Decoding.VccLo)  => carry.register
      case InstructionSource.Scalar(Decoding.VccHi)  => Bits.high32(carry.register)
      case InstructionSource.Scalar(Decoding.ExecLo) => active.register
      case InstructionSource.Scalar(Decoding.ExecHi) => Bits.high32(active.register)
      case InstructionSource.Scalar(index)           => pair(scalar, index)
      case InstructionSource.Constant(raw)           => raw
      case InstructionSource.Literal(raw)            => raw
      case InstructionSource.VectorRegister(_)       => 0L
    }

    def writeScalar64(dest: Int, value: Long): Unit = dest match {
      case Decoding.VccLo  => carry.register = value
      case Decoding.ExecLo => active.register = value
      case _               => setPair(scalar, dest, value)
    }

    def writeScalar32(dest: Int, value: Long): Unit = dest match {
      case Decoding.VccLo => carry.register = (carry.register & 0xffffffff00000000L) | Bits.low32(value)
      case Decoding.VccHi =>
        carry.register = (carry.register & 0xffffffffL) | (Bits.low32(value) << 32)
      case Decoding.ExecLo => active.register = (active.register & 0xffffffff00000000L) | Bits.low32(value)
      case Decoding.ExecHi => active.register = (active.register & 0xffffffffL) | (Bits.low32(value) << 32)
      case _               => scalar(dest) = Bits.low32(value)
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
    def immutableAddress(address: Long, size: Int): Boolean =
      ByteRange.contains(kernarg.length, address - KernargAddress, size.toLong) ||
        ByteRange.contains(dispatchBytes.length, address - DispatchAddress, size.toLong) ||
        ByteRange.contains(kernel.readOnly.length, address - kernel.readOnlyAddress, size.toLong)

    def flatLoad(lane: Int, address: Long, size: Int): Either[Fault, Vec[Byte]] =
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

    def flatStore(lane: Int, address: Long, bytes: Array[Byte]): Either[Fault, Unit] =
      if (address >= 0L && address <= lds.length.toLong - bytes.length)
        writeLDS(lds, address, bytes)
      else if (address >= 0L && address <= scratch(lane).length.toLong - bytes.length) {
        Array.copy(bytes, 0, scratch(lane), address.toInt, bytes.length)
        Right(())
      } else writeBytes(address, bytes, allocations, memory, kernel.globalMemory)

    def isEmulatedCall(name: String): Boolean =
      name == "__ockl_get_group_id" || name == "__ockl_get_local_size" || name == "__ockl_get_local_id" ||
        EmulatedCallNameFragments.exists(name.contains(_))

    def bufferOffset(
        lane: Int,
        address: Option[Int],
        scalarOffset: Option[Int],
        offset: Int,
        privateScratch: Boolean
    ): Long = {
      val vectorOffset      = address.map(register => Bits.low32(vector(register)(lane))).getOrElse(0L)
      val scalarOffsetBytes = scalarOffset.fold(0L)(register => Bits.low32(scalar(register)))
      // Scratch V# scalar offsets address wave-interleaved backing memory.
      // Convert that portion to a per-lane private byte offset; VGPR and
      // instruction offsets already name bytes within one work-item's scratch.
      vectorOffset + (if (privateScratch) scalarOffsetBytes / waveSize.value else scalarOffsetBytes) + offset.toLong
    }

    def executeBufferStore(
        index: Int,
        address: Option[Int],
        resource: Int,
        scalarOffset: Option[Int],
        src: Int,
        byteCount: Int :| Interval.Closed[1, 16],
        offset: Int,
        highByteCount: Option[ScalarByteWidth]
    ): Either[Fault, Option[Int]] = {
      def storedBytes(lane: Int): Array[Byte] = highByteCount match {
        case Some(width) => LittleEndian.toArray(vector(src)(lane) >>> 16, width)
        case None        => vectorBytes(vector, src, lane, byteCount)
      }

      def storeLane(lane: Int): Either[Fault, Unit] =
        if (lane == lanes.value) Right(())
        else if (!active(lane)) storeLane(lane + 1)
        else {
          val base  = sourceScalar64(InstructionSource.Scalar(resource)) & 0x0000ffffffffffffL
          val start = bufferOffset(lane, address, scalarOffset, offset, base == 0L)
          val range = Bits.low32(scalar(resource + 2))
          if (base != 0L && (start < 0L || start > range - byteCount.toLong)) storeLane(lane + 1)
          else if (base != 0L)
            writeBytes(base + start, storedBytes(lane), allocations, memory, kernel.globalMemory)
              .flatMap(_ => storeLane(lane + 1))
          else if (start < 0L || start > scratch(lane).length.toLong - byteCount.toLong)
            Left(Fault.Backend(
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

    // Split the instruction switch into JIT-sized methods. The complete switch is too large for C2 to compile.
    def stepInstruction(index: Int): Either[Fault, Option[Int]] = operations(index) match {
      case Instruction.End        => memory.flushHostWrites().map(_ => None)
      case Instruction.Trap(code) =>
        Left(Fault.Backend(f"AMDGPU s_trap $code%d at pc 0x${programCounter(index)}%x"))
      case Instruction.Wait                      => memory.flushHostWrites().flatMap(_ => AdvanceStep)
      case Instruction.SSetGprIdxOn              => AdvanceStep
      case Instruction.Barrier                   => Right(Some(-1))
      case Instruction.Branch(condition, target) =>
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
        Right(Some(if (taken) target else index + 1))
      case Instruction.BranchRelative(_, _) =>
        Left(Fault.Backend("unresolved AMDGPU branch reached execution"))
      case Instruction.SLoad(dest, count, base, offset, scalarOffset) =>
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
            Right(Some(index + 1))
        }
      case Instruction.SMove(dest, src) =>
        writeScalar32(dest, source(src, 0))
        AdvanceStep
      case Instruction.SMoveImmediate(dest, value) =>
        writeScalar32(dest, Bits.unsigned32(value))
        AdvanceStep
      case Instruction.SMultiplyImmediate(dest, value) =>
        val (result, overflow) = signedMultiplyImmediate(scalar(dest), value)
        scalar(dest) = result
        scalarCarry = overflow
        AdvanceStep
      case Instruction.SAddImmediate(dest, value) =>
        val result = scalar(dest) + value.toLong
        writeScalar32(dest, result)
        scalarCarry = result > Bits.Mask32 || result < 0L
        AdvanceStep
      case Instruction.SMove64(dest, src) =>
        writeScalar64(dest, sourceScalar64(src))
        AdvanceStep
      case Instruction.SSetReg(_, _) =>
        AdvanceStep
      case Instruction.SGetPc(dest, offset) =>
        writeScalar64(dest, programCounter(index) + 4L)
        AdvanceStep
      case Instruction.SSetPc(src) =>
        val target = sourceScalar64(src)
        programIndex(target)
          .map(index => Right(Some(index)))
          .getOrElse(Left(Fault.Backend(
            f"AMDGPU setpc target 0x$target%X is not an instruction"
          )))
      case Instruction.SSwapPc(dest, src) =>
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
                val dimension = Bits.signed32(vector(0)(lane))
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
              Left(Fault.Unsupported(s"AMDGPU call to $name is not implemented"))
            case None =>
              Left(Fault.Backend(
                f"AMDGPU call target 0x$target%X is not present in the code object"
              ))
          }
        }
      case Instruction.SAndSaveExec(dest, src, width) =>
        val previous    = laneMask(active)
        val sourceValue = saveExecSource(src, width)
        val result      = if (width == 32) Bits.low32(previous) & sourceValue else previous & sourceValue
        saveExecWrite(dest, previous, width)
        saveExecWrite(Decoding.ExecLo, result, width)
        scalarCarry = result != 0L
        AdvanceStep
      case Instruction.SAndn2SaveExec(dest, src, width) =>
        val previous    = laneMask(active)
        val sourceValue = saveExecSource(src, width)
        // The explicit source is ANDN2's first operand and the old
        // EXEC mask is its second operand; the old mask is saved to
        // dest before EXEC is replaced.
        val result = if (width == 32) Bits.low32(andn2SaveExecMask(Bits.low32(previous), sourceValue))
        else andn2SaveExecMask(previous, sourceValue)
        saveExecWrite(dest, previous, width)
        saveExecWrite(Decoding.ExecLo, result, width)
        scalarCarry = result != 0L
        AdvanceStep
      case Instruction.SOrSaveExec(dest, src, width) =>
        val previous = laneMask(active)
        val result   = if (width == 32) Bits.low32(previous) | saveExecSource(src, width)
        else previous | saveExecSource(src, width)
        saveExecWrite(dest, previous, width)
        saveExecWrite(Decoding.ExecLo, result, width)
        scalarCarry = result != 0L
        AdvanceStep
      case Instruction.SXorSaveExec(dest, src, width) =>
        val previous = laneMask(active)
        val result   = if (width == 32) Bits.low32(previous) ^ saveExecSource(src, width)
        else previous ^ saveExecSource(src, width)
        saveExecWrite(dest, previous, width)
        saveExecWrite(Decoding.ExecLo, result, width)
        scalarCarry = result != 0L
        AdvanceStep
      case Instruction.SUnary(operation, dest, src) =>
        val result = operation match {
          case ScalarUnaryOperation.AbsoluteI32  => Bits.low32(math.abs(Bits.low32(source(src, 0)).toInt).toLong)
          case ScalarUnaryOperation.SignExtendI8 =>
            Bits.low32(Bits.signExtend(source(src, 0) & 0xffL, BitWidth(8)))
          case ScalarUnaryOperation.SignExtendI16 =>
            Bits.low32(Bits.signExtend(source(src, 0) & 0xffffL, BitWidth(16)))
          case ScalarUnaryOperation.CountBits32     => Bits.bitCount(source(src, 0), BitWidth(32)).toLong
          case ScalarUnaryOperation.CountBits64     => Bits.bitCount(sourceScalar64(src), BitWidth(64)).toLong
          case ScalarUnaryOperation.ReverseBits32   => bitReverse32(source(src, 0))
          case ScalarUnaryOperation.Not32           => Bits.low32(~source(src, 0))
          case ScalarUnaryOperation.Not64           => ~sourceScalar64(src)
          case ScalarUnaryOperation.FirstBitHighI32 => signedFirstBitHigh32(source(src, 0))
          case ScalarUnaryOperation.ClearBit32      =>
            Bits.low32(scalar(dest) & ~(1L << (source(src, 0).toInt & 31)))
          case ScalarUnaryOperation.SetBit32 =>
            Bits.low32(scalar(dest) | (1L << (source(src, 0).toInt & 31)))
        }
        if (operation == ScalarUnaryOperation.Not64) writeScalar64(dest, result) else writeScalar32(dest, result)
        if (operation == ScalarUnaryOperation.Not32 || operation == ScalarUnaryOperation.Not64)
          scalarCarry = result != 0L
        AdvanceStep
      case other => stepScalarAndVector(index, other)
    }

    def stepScalarAndVector(index: Int, instruction: Instruction): Either[Fault, Option[Int]] = instruction match {
      case Instruction.SBinary(operation, dest, srcA, srcB) =>
        val left   = Bits.low32(source(srcA, 0))
        val right  = Bits.low32(source(srcB, 0))
        val result = operation match {
          case ScalarBinaryOperation.And32 =>
            val value = left & right
            scalarCarry = value != 0L
            value
          case ScalarBinaryOperation.Or32 =>
            val value = left | right
            scalarCarry = value != 0L
            value
          case ScalarBinaryOperation.MultiplyI32     => left * right
          case ScalarBinaryOperation.MultiplyHighU32 => unsignedMultiplyHigh32(left, right)
          case ScalarBinaryOperation.MultiplyHighI32 => signedMultiplyHigh32(left, right)
          case ScalarBinaryOperation.AddI32          =>
            val sum = Bits.low32(left + right)
            scalarCarry = ((~(left ^ right) & (left ^ sum)) & 0x80000000L) != 0L
            sum
          case ScalarBinaryOperation.SubtractI32 =>
            val difference = Bits.low32(left - right)
            scalarCarry = (((left ^ right) & (left ^ difference)) & 0x80000000L) != 0L
            difference
          case ScalarBinaryOperation.ShiftLeft32            => left << (right.toInt & 31)
          case ScalarBinaryOperation.ShiftLeftTwoAndAddU32  => (left << 2) + right
          case ScalarBinaryOperation.ShiftRightArithmetic32 => Bits.unsigned32(left.toInt >> (right.toInt & 31))
          case ScalarBinaryOperation.BitFieldExtractU32     =>
            val shift = (right & 0x3fL).toInt
            val width = ((right >>> 16) & 0x3fL).toInt
            if (width >= 32) Bits.low32(left >>> shift)
            else if (width == 0) 0L else Bits.low32((left >>> shift) & ((1L << width) - 1L))
          case ScalarBinaryOperation.BitFieldExtractI32 =>
            val shift = (right & 0x3fL).toInt
            val width = ((right >>> 16) & 0x3fL).toInt
            if (width >= 32) Bits.low32(Bits.signExtend(left >>> shift, BitWidth(32)))
            else if (width == 0) 0L
            else {
              val bitWidth = BitWidth.assume(width)
              Bits.low32(Bits.signExtend((left >>> shift) & ((1L << width) - 1L), bitWidth))
            }
          case ScalarBinaryOperation.ShiftRight32 => left >>> (right.toInt & 31)
          case ScalarBinaryOperation.AndNot32     =>
            val value = left & ~right
            scalarCarry = value != 0L
            value
          case ScalarBinaryOperation.OrNot32 =>
            val value = Bits.low32(left | ~right)
            scalarCarry = value != 0L
            value
          case ScalarBinaryOperation.Nor32 =>
            val value = Bits.low32(~(left | right))
            scalarCarry = value != 0L
            value
          case ScalarBinaryOperation.PackLowLow16   => Bits.low16(left) | (Bits.low16(right) << 16)
          case ScalarBinaryOperation.PackLowHigh16  => Bits.low16(left) | (Bits.low16(right >>> 16) << 16)
          case ScalarBinaryOperation.PackHighHigh16 =>
            (Bits.low16(left >>> 16)) | (Bits.low16(right >>> 16) << 16)
          case ScalarBinaryOperation.AddU32 =>
            val sum = left + right
            scalarCarry = sum > Bits.Mask32
            sum
          case ScalarBinaryOperation.AddCarryU32 =>
            val sum = left + right + (if (scalarCarry) 1L else 0L)
            scalarCarry = sum > Bits.Mask32
            sum
          case ScalarBinaryOperation.SubtractU32 =>
            scalarCarry = left >= right
            left - right
          case ScalarBinaryOperation.SubtractBorrowU32 =>
            val borrow = if (scalarCarry) 0L else 1L
            scalarCarry = left >= right + borrow
            left - right - borrow
          case ScalarBinaryOperation.Xor32 =>
            val value = left ^ right
            scalarCarry = value != 0L
            value
        }
        writeScalar32(dest, result)
        AdvanceStep
      case Instruction.SMinMax(operation, dest, srcA, srcB) =>
        val (selected, sourceZeroWon) = scalarMinMax(operation, source(srcA, 0), source(srcB, 0))
        writeScalar32(dest, selected)
        scalarCarry = sourceZeroWon
        AdvanceStep
      case Instruction.SBinary64(operation, dest, srcA, srcB) =>
        val left   = sourceScalar64(srcA)
        val right  = sourceScalar64(srcB)
        val result = operation match {
          case ScalarBinary64Operation.And                  => left & right
          case ScalarBinary64Operation.AndNot               => left & ~right
          case ScalarBinary64Operation.Or                   => left | right
          case ScalarBinary64Operation.OrNot                => left | ~right
          case ScalarBinary64Operation.Nor                  => ~(left | right)
          case ScalarBinary64Operation.Xor                  => left ^ right
          case ScalarBinary64Operation.ShiftLeft            => left << (right.toInt & 63)
          case ScalarBinary64Operation.ShiftRight           => left >>> (right.toInt & 63)
          case ScalarBinary64Operation.ShiftRightArithmetic => left >> (right.toInt & 63)
          case ScalarBinary64Operation.BitFieldExtractI64   =>
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
        AdvanceStep
      case Instruction.SCSelect(dest, srcA, srcB, width) =>
        val selected = if (scalarCarry) srcA else srcB
        if (width == 64) writeScalar64(dest, sourceScalar64(selected))
        else writeScalar32(dest, source(selected, 0))
        AdvanceStep
      case Instruction.SCmpImmediate(operation, src, immediate) =>
        val value = scalar(src)
        scalarCarry = operation match {
          case ScalarImmediateCompareOperation.EqualI32          => value.toInt == immediate
          case ScalarImmediateCompareOperation.NotEqualI32       => value.toInt != immediate
          case ScalarImmediateCompareOperation.GreaterI32        => value.toInt > immediate
          case ScalarImmediateCompareOperation.LessI32           => value.toInt < immediate
          case ScalarImmediateCompareOperation.EqualU32          => Bits.low32(value) == Bits.unsigned32(immediate)
          case ScalarImmediateCompareOperation.NotEqualU32       => Bits.low32(value) != Bits.unsigned32(immediate)
          case ScalarImmediateCompareOperation.GreaterU32        => Bits.low32(value) > Bits.unsigned32(immediate)
          case ScalarImmediateCompareOperation.GreaterOrEqualU32 =>
            Bits.low32(value) >= Bits.unsigned32(immediate)
          case ScalarImmediateCompareOperation.LessU32        => Bits.low32(value) < Bits.unsigned32(immediate)
          case ScalarImmediateCompareOperation.LessOrEqualU32 =>
            Bits.low32(value) <= Bits.unsigned32(immediate)
        }
        AdvanceStep
      case Instruction.SCmp(operation, srcA, srcB) =>
        val left32  = Bits.low32(source(srcA, 0))
        val right32 = Bits.low32(source(srcB, 0))
        scalarCarry = operation match {
          case ScalarCompareOperation.EqualU32          => left32 == right32
          case ScalarCompareOperation.GreaterI32        => left32.toInt > right32.toInt
          case ScalarCompareOperation.GreaterU32        => left32 > right32
          case ScalarCompareOperation.GreaterOrEqualU32 => left32 >= right32
          case ScalarCompareOperation.LessOrEqualU32    => left32 <= right32
          case ScalarCompareOperation.EqualI32          => left32.toInt == right32.toInt
          case ScalarCompareOperation.NotEqualI32       => left32.toInt != right32.toInt
          case ScalarCompareOperation.GreaterOrEqualI32 => left32.toInt >= right32.toInt
          case ScalarCompareOperation.LessOrEqualI32    => left32.toInt <= right32.toInt
          case ScalarCompareOperation.LessU32           => left32 < right32
          case ScalarCompareOperation.NotEqualU32       => left32 != right32
          case ScalarCompareOperation.LessI32           => left32.toInt < right32.toInt
          case ScalarCompareOperation.BitSet32          => Bits.isSet(left32, right32.toInt & 31)
          case ScalarCompareOperation.BitClear32        => !Bits.isSet(left32, right32.toInt & 31)
          case ScalarCompareOperation.EqualU64          => sourceScalar64(srcA) == sourceScalar64(srcB)
          case ScalarCompareOperation.NotEqualU64       => sourceScalar64(srcA) != sourceScalar64(srcB)
        }
        AdvanceStep
      case Instruction.VMove(dest, src) =>
        executeVMove(dest, src, scalar, vector, carry, active, lanes)
        AdvanceStep
      case Instruction.VMoveRelative(dest, InstructionSource.VectorRegister(src)) =>
        val indexed = src + (Bits.low32(scalar(Decoding.M0)) & 0xffL).toInt
        if (indexed < 0 || indexed >= 256)
          Left(Fault.Backend(s"v_movrels_b32 indexed VGPR $indexed is out of range"))
        else {
          val sourceValues = vector(indexed)
          foreachActive(lane => vector(dest)(lane) = Bits.low32(sourceValues(lane)))
          AdvanceStep
        }
      case Instruction.VMoveRelative(_, source) =>
        Left(Fault.Backend(
          s"v_movrels_b32 requires a vector-register source, found $source"
        ))
      case Instruction.VMoveRelativeDestination(dest, src) =>
        val indexed = dest + (Bits.low32(scalar(Decoding.M0)) & 0xffL).toInt
        if (indexed < 0 || indexed >= 256)
          Left(Fault.Backend(s"v_movreld_b32 indexed VGPR $indexed is out of range"))
        else {
          foreachActive(lane => vector(indexed)(lane) = Bits.low32(source(src, lane)))
          AdvanceStep
        }
      case Instruction.VNot(dest, src) =>
        foreachActive(lane => vector(dest)(lane) = Bits.low32(~source(src, lane)))
        AdvanceStep
      case Instruction.VSwap(dest, InstructionSource.VectorRegister(src)) =>
        foreachActive { lane =>
          val destinationValue = vector(dest)(lane)
          vector(dest)(lane) = Bits.low32(vector(src)(lane))
          vector(src)(lane) = Bits.low32(destinationValue)
        }
        AdvanceStep
      case Instruction.VSwap(_, _) =>
        Left(Fault.Backend("v_swap_b32 requires a vector-register source"))
      case Instruction.VBitReverse(dest, src) =>
        foreachActive(lane => vector(dest)(lane) = bitReverse32(source(src, lane)))
        AdvanceStep
      case Instruction.VBitFind(operation, dest, src) =>
        foreachActive { lane =>
          val value  = Bits.signed32(source(src, lane))
          val result = operation match {
            case VectorBitFindOperation.FirstBitLow32 =>
              if (value == 0) -1L else Integer.numberOfTrailingZeros(value).toLong
            case VectorBitFindOperation.FirstBitHighI32 => signedFirstBitHigh32(value)
            // FFBH counts zero bits from the MSB to the first set bit. It is
            // therefore CLZ-like, not the bit index of the highest set bit.
            case VectorBitFindOperation.FirstBitHighU32 => firstBitHigh32(value)
          }
          vector(dest)(lane) = Bits.unsigned32(result.toInt)
        }
        AdvanceStep
      case Instruction.VConvert(operation, dest, src) =>
        foreachActive { lane =>
          val raw     = Bits.signed32(source(src, lane))
          val float32 = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
          def writeFloat32(value: Float): Unit  = vector(dest)(lane) = IEEE754.floatToBinary32(value).value
          def writeFloat64(value: Double): Unit = {
            val bits = IEEE754.doubleToBinary64(value)
            vector(dest)(lane) = Bits.low32(bits)
            vector(dest + 1)(lane) = Bits.high32(bits)
          }
          operation match {
            case VectorConvertOperation.FrexpExponentF64 =>
              val value = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
              vector(dest)(lane) = Bits.unsigned32(
                if (value == 0.0 || value.isNaN || value.isInfinite) 0 else Math.getExponent(value) + 1
              )
            case VectorConvertOperation.FrexpExponentF32 =>
              vector(dest)(lane) = Bits.unsigned32(
                if (float32 == 0.0f || float32.isNaN || float32.isInfinite) 0 else Math.getExponent(float32) + 1
              )
            case VectorConvertOperation.FrexpMantissaF32 =>
              writeFloat32(
                if (float32 == 0.0f || float32.isNaN || float32.isInfinite) float32
                else Math.scalb(float32, -Math.getExponent(float32) - 1)
              )
            case VectorConvertOperation.FrexpMantissaF64 =>
              val value = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
              writeFloat64(
                if (value == 0.0 || value.isNaN || value.isInfinite) value
                else Math.scalb(value, -Math.getExponent(value) - 1)
              )
            case VectorConvertOperation.ReciprocalF32 => writeFloat32(1.0f / float32)
            case VectorConvertOperation.ReciprocalF64 =>
              writeFloat64(1.0 / IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector)))
            case VectorConvertOperation.TruncateF32 =>
              writeFloat32((if (float32 >= 0.0f) math.floor(float32.toDouble) else math.ceil(float32.toDouble)).toFloat)
            case VectorConvertOperation.FloorF32            => writeFloat32(math.floor(float32.toDouble).toFloat)
            case VectorConvertOperation.RoundNearestEvenF32 => writeFloat32(Math.rint(float32.toDouble).toFloat)
            case VectorConvertOperation.TruncateF64         =>
              val value = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
              writeFloat64(if (value >= 0.0) math.floor(value) else math.ceil(value))
            case VectorConvertOperation.FloorF64 =>
              writeFloat64(math.floor(IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))))
            case VectorConvertOperation.FractionF64 =>
              val value = IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))
              writeFloat64(value - math.floor(value))
            case VectorConvertOperation.RoundNearestEvenF64 =>
              writeFloat64(Math.rint(IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector))))
            case VectorConvertOperation.ReciprocalSquareRootF32 =>
              writeFloat32((1.0 / math.sqrt(float32.toDouble)).toFloat)
            case VectorConvertOperation.ReciprocalSquareRootF64 =>
              writeFloat64(
                1.0 / math.sqrt(IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector)))
              )
            case VectorConvertOperation.SquareRootF32    => writeFloat32(math.sqrt(float32.toDouble).toFloat)
            case VectorConvertOperation.ExponentBase2F32 => writeFloat32(math.pow(2.0, float32.toDouble).toFloat)
            case trig @ (VectorConvertOperation.SineF32 | VectorConvertOperation.CosineF32) =>
              writeFloat32(trigF32(trig, float32))
            case VectorConvertOperation.LogarithmBase2F32 =>
              writeFloat32((math.log(float32.toDouble) / math.log(2.0)).toFloat)
            case VectorConvertOperation.I32FromF32 => vector(dest)(lane) = Bits.unsigned32(float32.toInt)
            case VectorConvertOperation.U32FromF32 => vector(dest)(lane) = unsignedFloatToInt32(float32)
            case VectorConvertOperation.I32FromF64 =>
              vector(dest)(lane) = Bits.unsigned32(IEEE754.binary64ToDouble(source64(src, lane, scalar, vector)).toInt)
            case VectorConvertOperation.U32FromF64 =>
              vector(dest)(lane) = Bits.low32(IEEE754.binary64ToDouble(source64(src, lane, scalar, vector)).toLong)
            case byte @ (
                  VectorConvertOperation.F32FromUByte0 | VectorConvertOperation.F32FromUByte1 |
                  VectorConvertOperation.F32FromUByte2 | VectorConvertOperation.F32FromUByte3
                ) =>
              val index = byte.ordinal - VectorConvertOperation.F32FromUByte0.ordinal
              writeFloat32(((raw >>> (index * 8)) & 0xff).toFloat)
            case VectorConvertOperation.F32FromF64 =>
              writeFloat32(IEEE754.binary64ToDouble(sourceFloating64(src, lane, scalar, vector)).toFloat)
            case VectorConvertOperation.F32FromF16 =>
              writeFloat32(IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(raw))))
            case VectorConvertOperation.F16FromF32 => vector(dest)(lane) = IEEE754.floatToBinary16(float32).value
            case VectorConvertOperation.I16FromF16 =>
              vector(dest)(lane) = Bits.low16(IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(raw))).toInt.toLong)
            case VectorConvertOperation.F64FromI32 => writeFloat64(raw.toDouble)
            case VectorConvertOperation.F64FromU32 => writeFloat64(Integer.toUnsignedLong(raw).toDouble)
            case VectorConvertOperation.F64FromF32 => writeFloat64(float32.toDouble)
            case VectorConvertOperation.F32FromI32 => writeFloat32(raw.toFloat)
            case VectorConvertOperation.F32FromU32 => writeFloat32(Integer.toUnsignedLong(raw).toFloat)
          }
        }
        AdvanceStep
      case Instruction.VMoveDPP(dest, src, control, rowMask, bankMask, boundControl, fetchInactive) =>
        val sourceValues = vector(src).clone()
        foreachActive { lane =>
          if (dppLaneEnabled(lane, rowMask, bankMask)) {
            dppSourceLane(lane, control) match {
              case Some(sourceLane) if sourceLane < lanes.value =>
                vector(dest)(lane) =
                  if (fetchInactive || active(sourceLane)) Bits.low32(sourceValues(sourceLane)) else 0L
              case _ if boundControl => vector(dest)(lane) = 0L
              case _                 => ()
            }
          }
        }
        AdvanceStep
      case Instruction.VCndMask(dest, srcA, srcB, condition, absA, absB, negateA, negateB) =>
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
        }
        AdvanceStep
      case other => stepVectorInstruction(index, other)
    }

    def stepVectorInstruction(index: Int, instruction: Instruction): Either[Fault, Option[Int]] = instruction match {
      case Instruction.VReadFirstLane(dest, src) =>
        active.first match {
          case None       => Left(Fault.Backend("v_readfirstlane_b32 has an empty EXEC mask"))
          case Some(lane) =>
            scalar(dest) = Bits.low32(source(src, lane.value))
            Right(Some(index + 1))
        }
      case Instruction.VReadLane(dest, src, lane) =>
        val selected = source(lane, 0).toInt
        if (selected < 0 || selected >= lanes.value)
          Left(Fault.Backend("v_readlane_b32 selected an invalid lane"))
        else {
          scalar(dest) = Bits.low32(source(src, selected))
          AdvanceStep
        }
      case Instruction.VWriteLane(dest, src, lane) =>
        val selected = source(lane, 0).toInt
        if (selected < 0 || selected >= lanes.value)
          Left(Fault.Backend("v_writelane_b32 selected an invalid lane"))
        else {
          vector(dest)(selected) = Bits.low32(source(src, selected))
          AdvanceStep
        }
      case Instruction.VMaskBitCount(dest, mask, base, high) =>
        val executionMask = laneMask(active)
        foreachActive { lane =>
          vector(dest)(lane) = mbCount(source(mask, lane), executionMask, lane, high, source(base, lane))
        }
        AdvanceStep
      case Instruction.VBitCount(dest, src, base) =>
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
      case Instruction.VMadMkF32(dest, srcA, multiplier, addend) =>
        foreachActive { lane =>
          val left  = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcA, lane))))
          val scale = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(multiplier)))
          val right = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(addend, lane))))
          vector(dest)(lane) = IEEE754.floatToBinary32(left * scale + right).value
        }
        AdvanceStep
      case Instruction.VMadAkF32(dest, srcA, srcB, addend) =>
        foreachActive { lane =>
          val left    = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcA, lane))))
          val right   = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcB, lane))))
          val literal = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(addend)))
          // MADAK is the literal-addend form of MAD.  Unlike FMA it
          // rounds the product before adding the literal.
          vector(dest)(lane) = IEEE754.floatToBinary32(left * right + literal).value
        }
        AdvanceStep
      case Instruction.VMadF32(
            operation,
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
            if (operation == VectorMadF32Operation.FusedMultiplyAdd)
              Math.fma(productLeft, productRight, productAddend)
            else productLeft * productRight + productAddend
          vector(dest)(lane) = IEEE754.floatToBinary32(if (clamp) clampF32(result) else result).value
        }
        AdvanceStep
      case Instruction.VMadF64(dest, srcA, srcB, srcC, absA, absB, absC, negateA, negateB, negateC) =>
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
        }
        AdvanceStep
      case Instruction.VUnaryF32Modified(dest, src, absolute, negate, clamp) =>
        foreachActive { lane =>
          val raw      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
          val prepared = if (absolute) math.abs(raw) else raw
          val value    = if (negate) -prepared else prepared
          val result   = 1.0f / value
          vector(dest)(lane) = IEEE754.floatToBinary32(if (clamp) clampF32(result) else result).value
        }
        AdvanceStep
      case Instruction.VFrexpMantF32Modified(dest, src, absolute, negate, clamp) =>
        foreachActive { lane =>
          val raw      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
          val prepared = if (absolute) math.abs(raw) else raw
          val value    = if (negate) -prepared else prepared
          val mantissa =
            if (value == 0.0f || value.isNaN || value.isInfinite) value
            else Math.scalb(value, -Math.getExponent(value) - 1)
          vector(dest)(lane) = IEEE754.floatToBinary32(if (clamp) clampF32(mantissa) else mantissa).value
        }
        AdvanceStep
      case Instruction.VCvtF64F32Modified(dest, src, absolute, negate, clamp) =>
        foreachActive { lane =>
          val raw      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(src, lane))))
          val prepared = if (absolute) math.abs(raw) else raw
          val value    = if (negate) -prepared else prepared
          val result   = value.toDouble
          val bits     = IEEE754.doubleToBinary64(if (clamp) math.max(0.0, math.min(1.0, result)) else result)
          vector(dest)(lane) = Bits.low32(bits)
          vector(dest + 1)(lane) = Bits.high32(bits)
        }
        AdvanceStep
      case Instruction.VUnaryF64Modified(dest, src, absolute, negate) =>
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
        }
        AdvanceStep
      case Instruction.VTrigPreopF64(dest, src, segment, absolute, negate) =>
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
      case Instruction.VBinaryF32Modified(
            operation,
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
          val result    = operation match {
            case VectorF32BinaryOperation.Add             => valueA + valueB
            case VectorF32BinaryOperation.Subtract        => valueA - valueB
            case VectorF32BinaryOperation.ReverseSubtract => valueB - valueA
            case VectorF32BinaryOperation.Multiply        => valueA * valueB
            case VectorF32BinaryOperation.LoadExponent    => Math.scalb(valueA, source(srcB, lane).toInt)
            case VectorF32BinaryOperation.Maximum         => math.max(valueA, valueB)
            case VectorF32BinaryOperation.Minimum         => math.min(valueA, valueB)
          }
          vector(dest)(lane) = IEEE754.floatToBinary32(if (clamp) clampF32(result) else result).value
        }
        AdvanceStep
      case Instruction.VDivScaleF32(dest, conditionDest, srcA, srcB, srcC) =>
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
        }
        if (waveSize.value <= 32) writeScalar32(conditionDest, booleanLaneMask(conditions))
        else writeScalar64(conditionDest, booleanLaneMask(conditions))
        AdvanceStep
      case Instruction.VDivScaleF64(dest, conditionDest, srcA, srcB, srcC) =>
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
        }
        if (waveSize.value <= 32) writeScalar32(conditionDest, booleanLaneMask(conditions))
        else writeScalar64(conditionDest, booleanLaneMask(conditions))
        AdvanceStep
      case Instruction.VDivFmasF64(dest, srcA, srcB, srcC) =>
        foreachActive { lane =>
          val left   = IEEE754.binary64ToDouble(sourceFloating64(srcA, lane, scalar, vector))
          val right  = IEEE754.binary64ToDouble(sourceFloating64(srcB, lane, scalar, vector))
          val addend = IEEE754.binary64ToDouble(sourceFloating64(srcC, lane, scalar, vector))
          val result = Math.fma(left, right, addend)
          val scaled = (laneMask(carry) & (1L << lane)) != 0L
          val bits   = IEEE754.doubleToBinary64(if (scaled) Math.scalb(result, 64) else result)
          vector(dest)(lane) = Bits.low32(bits)
          vector(dest + 1)(lane) = Bits.high32(bits)
        }
        AdvanceStep
      case Instruction.VDivFixupF32(dest, quotient, denominator, numerator, absolute, negate) =>
        foreachActive { lane =>
          def operand(value: InstructionSource, index: Int): Float = {
            val raw      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(value, lane))))
            val prepared = if ((absolute & (1 << index)) != 0) math.abs(raw) else raw
            if ((negate & (1 << index)) != 0) -prepared else prepared
          }
          val q      = operand(quotient, 0)
          val d      = operand(denominator, 1)
          val n      = operand(numerator, 2)
          val result = divisionFixupF32(q, d, n)
          vector(dest)(lane) = IEEE754.floatToBinary32(result).value
        }
        AdvanceStep
      case Instruction.VDivFixupF64(dest, quotient, denominator, numerator, absolute, negate) =>
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
        }
        AdvanceStep
      case Instruction.VBinary(operation, dest, srcA, srcB, clamp) =>
        foreachActive { lane =>
          val left     = Bits.low32(source(srcA, lane))
          val right    = Bits.low32(source(srcB, lane))
          val previous = Bits.low32(vector(dest)(lane))
          operation match {
            case VectorBinaryOperation.AddU32 =>
              vector(dest)(lane) = if (clamp) math.min(Bits.Mask32, left + right) else Bits.low32(left + right)
            case VectorBinaryOperation.MultiplyLowU32  => vector(dest)(lane) = Bits.low32(left * right)
            case VectorBinaryOperation.MultiplyHighU32 => vector(dest)(lane) = unsignedMultiplyHigh32(left, right)
            case VectorBinaryOperation.MultiplyHighI32 => vector(dest)(lane) = signedMultiplyHigh32(left, right)
            case VectorBinaryOperation.ShiftRightArithmeticI32 =>
              vector(dest)(lane) = Bits.unsigned32(right.toInt >> (left.toInt & 31))
            case VectorBinaryOperation.ShiftRightArithmeticI16 =>
              val signed = (Bits.low16Int(right) << 16) >> 16
              vector(dest)(lane) = Bits.low16(signed >> (left.toInt & 15))
            case VectorBinaryOperation.ShiftLeft32  => vector(dest)(lane) = Bits.low32(right << (left.toInt & 31))
            case VectorBinaryOperation.ShiftRight32 => vector(dest)(lane) = right >>> (left.toInt & 31)
            case VectorBinaryOperation.ShiftRight16 => vector(dest)(lane) = Bits.low16(right >>> (left.toInt & 15))
            case VectorBinaryOperation.AddU16       => vector(dest)(lane) = Bits.low16(left + right)
            case VectorBinaryOperation.And32        => vector(dest)(lane) = left & right
            case VectorBinaryOperation.Or32         => vector(dest)(lane) = left | right
            case VectorBinaryOperation.Xor32        => vector(dest)(lane) = left ^ right
            case VectorBinaryOperation.ShiftLeft16  =>
              vector(dest)(lane) = Bits.low16(Bits.low16(right) << (left.toInt & 15))
            case VectorBinaryOperation.SubtractU16 => vector(dest)(lane) = Bits.low16(left - right)
            case VectorBinaryOperation.SubtractU32 =>
              vector(dest)(lane) = if (clamp) math.max(0L, left - right) else Bits.low32(left - right)
            case VectorBinaryOperation.SubtractReverseU32 =>
              vector(dest)(lane) =
                if (clamp) math.max(0L, right - left) else unsignedSubtractReverse32(left, right)
            case VectorBinaryOperation.SubtractReverseF32 => vector(dest)(lane) =
                IEEE754.floatToBinary32(IEEE754.binary32ToFloat(
                  Binary32.fromLow(Bits.low32(right))
                ) - IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left)))).value
            case VectorBinaryOperation.MultiplyU24     => vector(dest)(lane) = unsignedMultiply24(left, right)
            case VectorBinaryOperation.MultiplyHighU24 => vector(dest)(lane) = unsignedMultiplyHigh24(left, right)
            case VectorBinaryOperation.MultiplyI24     => vector(dest)(lane) = signedMultiply24(left, right)
            case VectorBinaryOperation.MultiplyHighI24 => vector(dest)(lane) = signedMultiplyHigh24(left, right)
            case VectorBinaryOperation.MultiplyLowU16  => vector(dest)(lane) = Bits.low16(left * right)
            case VectorBinaryOperation.MinimumU32      => vector(dest)(lane) = math.min(left, right)
            case VectorBinaryOperation.MaximumU32      => vector(dest)(lane) = math.max(left, right)
            case VectorBinaryOperation.MinimumI32      =>
              vector(dest)(lane) = Bits.low32(math.min(left.toInt, right.toInt).toLong)
            case VectorBinaryOperation.MinimumI16 => vector(dest)(lane) =
                Bits.low16(math.min(
                  Bits.signExtend(Bits.low16(left), BitWidth(16)),
                  Bits.signExtend(Bits.low16(right), BitWidth(16))
                ))
            case VectorBinaryOperation.MinimumU16 =>
              vector(dest)(lane) = Bits.low16(math.min(Bits.low16(left), Bits.low16(right)))
            case VectorBinaryOperation.MinimumF32 =>
              vector(dest)(lane) =
                IEEE754.floatToBinary32(math.min(
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))),
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))
                )).value
            case VectorBinaryOperation.MinimumF16 => vector(dest)(lane) = minimumF16(left, right).value
            case VectorBinaryOperation.MaximumI32 =>
              vector(dest)(lane) = math.max(left.toInt, right.toInt).toLong & Bits.Mask32
            case VectorBinaryOperation.MaximumI16 => vector(dest)(lane) =
                Bits.low16(math.max(
                  Bits.signExtend(Bits.low16(left), BitWidth(16)),
                  Bits.signExtend(Bits.low16(right), BitWidth(16))
                ))
            case VectorBinaryOperation.MaximumU16 =>
              vector(dest)(lane) = Bits.low16(math.max(Bits.low16(left), Bits.low16(right)))
            case VectorBinaryOperation.MaximumF32 =>
              vector(dest)(lane) =
                IEEE754.floatToBinary32(math.max(
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))),
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))
                )).value
            case VectorBinaryOperation.MaximumF16 =>
              vector(dest)(lane) = IEEE754.floatToBinary16(
                math.max(
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))),
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right)))
                )
              ).value
            case VectorBinaryOperation.LoadExponentF32 =>
              vector(dest)(lane) =
                IEEE754.floatToBinary32(Math.scalb(
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))),
                  right.toInt
                )).value
            case VectorBinaryOperation.AddF32 =>
              val result = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) + IEEE754.binary32ToFloat(
                Binary32.fromLow(Bits.low32(right))
              )
              vector(dest)(lane) = IEEE754.floatToBinary32(result).value
            case VectorBinaryOperation.MultiplyF32 =>
              val result = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) * IEEE754.binary32ToFloat(
                Binary32.fromLow(Bits.low32(right))
              )
              vector(dest)(lane) = IEEE754.floatToBinary32(result).value
            case VectorBinaryOperation.MultiplyAccumulateF32 =>
              val result = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) * IEEE754.binary32ToFloat(
                Binary32.fromLow(Bits.low32(right))
              ) +
                IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(previous)))
              vector(dest)(lane) = IEEE754.floatToBinary32(result).value
            case VectorBinaryOperation.AddF16 =>
              val result =
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) + IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(right))
                )
              vector(dest)(lane) = IEEE754.floatToBinary16(result).value
            case VectorBinaryOperation.MultiplyF16 =>
              val result =
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) * IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(right))
                )
              vector(dest)(lane) = IEEE754.floatToBinary16(result).value
            case VectorBinaryOperation.FusedMultiplyAccumulateF16 =>
              vector(dest)(lane) = IEEE754.floatToBinary16(
                Math.fma(
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))),
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right))),
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(previous)))
                )
              ).value
            case VectorBinaryOperation.SubtractF32 =>
              val result = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) - IEEE754.binary32ToFloat(
                Binary32.fromLow(Bits.low32(right))
              )
              vector(dest)(lane) = IEEE754.floatToBinary32(result).value
            case VectorBinaryOperation.AddCarryU32 =>
              val sum = left + right
              vector(dest)(lane) = Bits.low32(sum)
              carry(lane) = sum > Bits.Mask32
            case VectorBinaryOperation.AddWithCarryU32 =>
              val sum = left + right + (if (carry(lane)) 1L else 0L)
              vector(dest)(lane) = Bits.low32(sum)
              carry(lane) = sum > Bits.Mask32
            case VectorBinaryOperation.SubtractCarryU32 =>
              val (difference, borrow) = unsignedSubtract32(left, right)
              vector(dest)(lane) = difference
              carry(lane) = borrow
            case VectorBinaryOperation.SubtractWithBorrowU32 =>
              val (difference, borrow) = unsignedSubtractWithBorrow32(left, right, carry(lane))
              vector(dest)(lane) = difference
              carry(lane) = borrow
          }
        }
        AdvanceStep
      case Instruction.VDot2F32F16(dest, srcA, srcB, accumulate) =>
        foreachActive { lane =>
          vector(dest)(lane) = dot2F32F16(
            Bits.low32(source(srcA, lane)),
            Bits.low32(source(srcB, lane)),
            vector(dest)(lane),
            accumulate
          ).value
        }
        AdvanceStep
      case Instruction.VDot4U32U8(dest, srcA, srcB, srcC) =>
        foreachActive { lane =>
          vector(dest)(lane) = dot4U32U8(
            source(srcA, lane),
            source(srcB, lane),
            source(srcC, lane)
          )
        }
        AdvanceStep
      case Instruction.VBinary64Modified(operation, dest, srcA, srcB, absA, absB, negateA, negateB) =>
        val destinationLow  = vector(dest)
        val destinationHigh = vector(dest + 1)
        val sourceARegister = srcA match {
          case InstructionSource.VectorRegister(index) => index
          case _                                       => -1
        }
        val sourceBRegister = srcB match {
          case InstructionSource.VectorRegister(index) => index
          case _                                       => -1
        }
        val invariantA =
          if (sourceARegister >= 0) 0L else sourceFloating64(srcA, 0, scalar, vector)
        val invariantB =
          if (sourceBRegister >= 0) 0L else sourceFloating64(srcB, 0, scalar, vector)
        var lane = 0
        while (lane < lanes.value) {
          if (active(lane)) {
            val bitsA     = if (sourceARegister >= 0) pair(vector, sourceARegister, lane) else invariantA
            val bitsB     = if (sourceBRegister >= 0) pair(vector, sourceBRegister, lane) else invariantB
            val rawA      = IEEE754.binary64ToDouble(bitsA)
            val rawB      = IEEE754.binary64ToDouble(bitsB)
            val preparedA = if (absA) math.abs(rawA) else rawA
            val preparedB = if (absB) math.abs(rawB) else rawB
            val valueA    = if (negateA) -preparedA else preparedA
            val valueB    = if (negateB) -preparedB else preparedB
            val result    = operation match {
              case VectorBinary64Operation.LoadExponent => Math.scalb(valueA, source(srcB, lane).toInt)
              case VectorBinary64Operation.Add          => valueA + valueB
              case VectorBinary64Operation.Multiply     => valueA * valueB
              case VectorBinary64Operation.Maximum      => math.max(valueA, valueB)
              case VectorBinary64Operation.Minimum      => math.min(valueA, valueB)
            }
            val bits = IEEE754.doubleToBinary64(result)
            destinationLow(lane) = Bits.low32(bits)
            destinationHigh(lane) = Bits.high32(bits)
          }
          lane += 1
        }
        AdvanceStep
      case Instruction.VBinarySubDword(
            operation,
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
          val result = operation match {
            case VectorSubDwordOperation.ConditionalMask32 =>
              val conditionMask = Bits.low32(source(InstructionSource.Scalar(Decoding.VccLo), 0))
              if ((conditionMask & (1L << lane)) != 0L) right else left
            case VectorSubDwordOperation.AddU32 => Bits.low32(left + right)
            case VectorSubDwordOperation.AddU16 => Bits.low16(left + right)
            case VectorSubDwordOperation.AddF16 =>
              IEEE754.floatToBinary16(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) + IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(right))
                )
              ).value
            case VectorSubDwordOperation.AddCarryU32 =>
              val sum = left + right
              carry(lane) = sum > Bits.Mask32
              Bits.low32(sum)
            case VectorSubDwordOperation.AddWithCarryU32 =>
              val sum = left + right + (if (carry(lane)) 1L else 0L)
              carry(lane) = sum > Bits.Mask32
              Bits.low32(sum)
            case VectorSubDwordOperation.And32        => left & right
            case VectorSubDwordOperation.ShiftLeft16  => Bits.low16(Bits.low16(right) << (left.toInt & 15))
            case VectorSubDwordOperation.ShiftLeft32  => Bits.low32(right << (left.toInt & 31))
            case VectorSubDwordOperation.ShiftRight32 => right >>> (left.toInt & 31)
            case VectorSubDwordOperation.ShiftRight16 => Bits.low16(right >>> (left.toInt & 15))
            case VectorSubDwordOperation.MaximumI16   => Bits.low16(math.max(left, right))
            case VectorSubDwordOperation.MaximumF16   =>
              IEEE754.floatToBinary16(
                math.max(
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))),
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right)))
                )
              ).value
            case VectorSubDwordOperation.MinimumI16     => Bits.low16(math.min(left, right))
            case VectorSubDwordOperation.MinimumF16     => minimumF16(left, right).value
            case VectorSubDwordOperation.MinimumU16     => Bits.low16(math.min(Bits.low16(left), Bits.low16(right)))
            case VectorSubDwordOperation.MultiplyLowU16 => Bits.low16(left * right)
            case VectorSubDwordOperation.MultiplyF16    =>
              val result =
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) * IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(right))
                )
              IEEE754.floatToBinary16(result).value
            case VectorSubDwordOperation.MultiplyU24      => unsignedMultiply24(left, right)
            case VectorSubDwordOperation.MultiplyI24      => signedMultiply24(left, right)
            case VectorSubDwordOperation.Or32             => left | right
            case VectorSubDwordOperation.Xor32            => left ^ right
            case VectorSubDwordOperation.SubtractU16      => Bits.low16(left - right)
            case VectorSubDwordOperation.SubtractU32      => Bits.low32(left - right)
            case VectorSubDwordOperation.SubtractCarryU32 =>
              val (difference, borrow) = unsignedSubtract32(left, right)
              carry(lane) = borrow
              difference
            case VectorSubDwordOperation.SubtractWithBorrowU32 =>
              val (difference, borrow) = unsignedSubtractWithBorrow32(left, right, carry(lane))
              carry(lane) = borrow
              difference
            case VectorSubDwordOperation.SubtractReverseU32 =>
              val (difference, borrow) = unsignedSubtract32(right, left)
              carry(lane) = borrow
              difference
            case VectorSubDwordOperation.SubtractReverseWithBorrowU32 =>
              val (difference, borrow) = unsignedSubtractWithBorrow32(right, left, carry(lane))
              carry(lane) = borrow
              difference
          }
          vector(dest)(lane) = placeSubDword(vector(dest)(lane), result, destSelection, destUnused)
        }
        AdvanceStep
      case Instruction.VPackedBinary(operation, dest, srcA, srcB, srcAHighForHigh, srcBHighForHigh) =>
        foreachActive { lane =>
          val left                              = Bits.low32(source(srcA, lane))
          val right                             = Bits.low32(source(srcB, lane))
          def calculate(a: Long, b: Long): Long = operation match {
            case VectorPackedBinaryOperation.AddU16 => Bits.low16(a + b)
            case VectorPackedBinaryOperation.AddF16 =>
              Bits.low16(IEEE754.floatToBinary16(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(a))) + IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(b))
                )
              ).value)
            case VectorPackedBinaryOperation.MultiplyLowU16 => Bits.low16(a * b)
            case VectorPackedBinaryOperation.MultiplyF16    =>
              Bits.low16(IEEE754.floatToBinary16(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(a))) * IEEE754.binary16ToFloat(
                  Binary16.fromLow(Bits.low16(b))
                )
              ).value)
            case VectorPackedBinaryOperation.MaximumF16 =>
              Bits.low16(IEEE754.floatToBinary16(math.max(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(a))),
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(b)))
              )).value)
            case VectorPackedBinaryOperation.MinimumF16 =>
              Bits.low16(IEEE754.floatToBinary16(math.min(
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(a))),
                IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(b)))
              )).value)
            case VectorPackedBinaryOperation.MaximumI16 =>
              if (Bits.signExtend(Bits.low16(a), BitWidth(16)) >= Bits.signExtend(Bits.low16(b), BitWidth(16)))
                Bits.low16(a)
              else Bits.low16(b)
            case VectorPackedBinaryOperation.MaximumU16   => math.max(Bits.low16(a), Bits.low16(b))
            case VectorPackedBinaryOperation.SubtractI16  => Bits.low16(a - b)
            case VectorPackedBinaryOperation.ShiftRight16 => Bits.low16(b >>> (a.toInt & 15))
          }
          val low  = calculate(Bits.low16(left), Bits.low16(right))
          val high = calculate(
            if (srcAHighForHigh) left >>> 16 else Bits.low16(left),
            if (srcBHighForHigh) right >>> 16 else Bits.low16(right)
          )
          vector(dest)(lane) = low | (high << 16)
        }
        AdvanceStep
      case Instruction.VPackB32F16(dest, srcA, srcB) =>
        foreachActive { lane =>
          val low  = IEEE754.floatToBinary16(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcA, lane)))))
          val high = IEEE754.floatToBinary16(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(srcB, lane)))))
          vector(dest)(lane) = low.value | (high.value << 16)
        }
        AdvanceStep
      case Instruction.VPackedFmaF16(
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
      case Instruction.VFmaMixF16(
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
      case Instruction.VFmaMixF32(
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
      case Instruction.VAddCarry(dest, carryDest, srcA, srcB, carryIn) =>
        val inputCarry = carryIn match {
          case Some(mask) => sourceScalar64(mask)
          case None       => 0L
        }
        maskAccumulator(0) = 0L
        foreachActive { lane =>
          val left     = Bits.low32(source(srcA, lane))
          val right    = Bits.low32(source(srcB, lane))
          val carryBit = (inputCarry & (1L << lane)) != 0L
          val sum      = left + right + (if (carryBit) 1L else 0L)
          vector(dest)(lane) = Bits.low32(sum)
          if (sum > Bits.Mask32) maskAccumulator(0) |= 1L << lane
        }
        updateLaneMask(carryDest, maskAccumulator(0))
        AdvanceStep
      case Instruction.VSubCarry(dest, carryDest, srcA, srcB, carryIn) =>
        val inputCarry = carryIn match {
          case Some(mask) => sourceScalar64(mask)
          case None       => 0L
        }
        val outputCarry = Array.fill(lanes.value)(false)
        foreachActive { lane =>
          val left                 = Bits.low32(source(srcA, lane))
          val right                = Bits.low32(source(srcB, lane))
          val borrowIn             = (inputCarry & (1L << lane)) != 0L
          val (difference, borrow) =
            if (carryIn.isDefined) unsignedSubtractWithBorrow32(left, right, borrowIn)
            else unsignedSubtract32(left, right)
          vector(dest)(lane) = difference
          outputCarry(lane) = borrow
        }
        val carryMask = booleanLaneMask(outputCarry)
        updateLaneMask(carryDest, carryMask)
        AdvanceStep
      case Instruction.VSubReverseCarry(dest, carryDest, srcA, srcB, carryIn) =>
        val inputCarry = carryIn match {
          case Some(mask) => sourceScalar64(mask)
          case None       => 0L
        }
        val outputCarry = Array.fill(lanes.value)(false)
        foreachActive { lane =>
          val left                 = Bits.low32(source(srcA, lane))
          val right                = Bits.low32(source(srcB, lane))
          val borrowIn             = (inputCarry & (1L << lane)) != 0L
          val (difference, borrow) =
            if (carryIn.isDefined) unsignedSubtractWithBorrow32(right, left, borrowIn)
            else unsignedSubtract32(right, left)
          vector(dest)(lane) = difference
          outputCarry(lane) = borrow
        }
        val carryMask = booleanLaneMask(outputCarry)
        updateLaneMask(carryDest, carryMask)
        AdvanceStep
      case Instruction.VTernary(operation, dest, srcA, srcB, srcC) =>
        foreachActive { lane =>
          val left   = Bits.low32(source(srcA, lane))
          val right  = Bits.low32(source(srcB, lane))
          val addend = Bits.low32(source(srcC, lane))
          vector(dest)(lane) = Bits.low32(
            operation match {
              case VectorTernaryOperation.Add3U32     => left + right + addend
              case VectorTernaryOperation.Or3B32      => left | right | addend
              case VectorTernaryOperation.Xor3B32     => left ^ right ^ addend
              case VectorTernaryOperation.Maximum3I32 =>
                math.max(math.max(left.toInt, right.toInt), addend.toInt).toLong
              case VectorTernaryOperation.Maximum3U32 => math.max(math.max(left, right), addend)
              case VectorTernaryOperation.Minimum3I32 =>
                math.min(math.min(left.toInt, right.toInt), addend.toInt).toLong
              case VectorTernaryOperation.Minimum3F32 => IEEE754.floatToBinary32(math.min(
                  math.min(
                    IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))),
                    IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right)))
                  ),
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(addend)))
                )).value
              case VectorTernaryOperation.Minimum3U32 => math.min(math.min(left, right), addend)
              case VectorTernaryOperation.Median3I32  =>
                val a = left.toInt
                val b = right.toInt
                val c = addend.toInt
                math.max(math.min(a, b), math.min(math.max(a, b), c)).toLong
              case VectorTernaryOperation.Median3U32 =>
                val a = Bits.low32(left)
                val b = Bits.low32(right)
                val c = Bits.low32(addend)
                math.max(math.min(a, b), math.min(math.max(a, b), c))
              case VectorTernaryOperation.Median3I16 =>
                val a = Bits.signExtend(Bits.low16(left), BitWidth(16))
                val b = Bits.signExtend(Bits.low16(right), BitWidth(16))
                val c = Bits.signExtend(Bits.low16(addend), BitWidth(16))
                Bits.low16(math.max(math.min(a, b), math.min(math.max(a, b), c)))
              case VectorTernaryOperation.Median3U16 =>
                val a = Bits.low16(left)
                val b = Bits.low16(right)
                val c = Bits.low16(addend)
                Bits.low16(math.max(math.min(a, b), math.min(math.max(a, b), c)))
              case VectorTernaryOperation.Maximum3I16 => Bits.low16(math.max(
                  math.max(
                    Bits.signExtend(Bits.low16(left), BitWidth(16)),
                    Bits.signExtend(Bits.low16(right), BitWidth(16))
                  ),
                  Bits.signExtend(Bits.low16(addend), BitWidth(16))
                ))
              case VectorTernaryOperation.Maximum3U16 =>
                Bits.low16(math.max(math.max(Bits.low16(left), Bits.low16(right)), Bits.low16(addend)))
              case VectorTernaryOperation.Minimum3I16 => Bits.low16(math.min(
                  math.min(
                    Bits.signExtend(Bits.low16(left), BitWidth(16)),
                    Bits.signExtend(Bits.low16(right), BitWidth(16))
                  ),
                  Bits.signExtend(Bits.low16(addend), BitWidth(16))
                ))
              case VectorTernaryOperation.Minimum3U16 =>
                Bits.low16(math.min(math.min(Bits.low16(left), Bits.low16(right)), Bits.low16(addend)))
              case VectorTernaryOperation.MultiplyAddU16 =>
                Bits.low16(Bits.low16(left) * Bits.low16(right) + Bits.low16(addend))
              case VectorTernaryOperation.FusedMultiplyAddF32 => IEEE754.floatToBinary32(
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) * IEEE754.binary32ToFloat(
                    Binary32.fromLow(Bits.low32(right))
                  ) + IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(
                    addend
                  )))
                ).value
              case VectorTernaryOperation.FusedMultiplyAddF16 =>
                IEEE754.floatToBinary16(
                  IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))) * IEEE754.binary16ToFloat(
                    Binary16.fromLow(Bits.low16(right))
                  ) +
                    IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(addend)))
                ).value
              case VectorTernaryOperation.DivisionFusedMultiplyAddF32 =>
                val result =
                  IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))) * IEEE754.binary32ToFloat(
                    Binary32.fromLow(Bits.low32(right))
                  ) + IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(
                    addend
                  )))
                val scaled      = (laneMask(carry) & (1L << lane)) != 0L
                val finalResult = if (scaled) Math.scalb(result, 32) else result
                IEEE754.floatToBinary32(finalResult).value
              case VectorTernaryOperation.MultiplyAddLegacyU16 =>
                Bits.low16(left) * Bits.low16(right) + Bits.low16(addend)
              case VectorTernaryOperation.MultiplyAddU24 => unsignedMultiplyAdd24(left, right, addend)
              case VectorTernaryOperation.MultiplyAddI24 => Bits.low32(signedMultiply24(left, right) + addend)
              // ALIGNBIT treats SRC0 as the high dword and SRC1 as
              // the low dword of the 64-bit shift window.  join32
              // takes its low word first.
              case VectorTernaryOperation.AlignBit32 =>
                val result = Bits.join32(right, left) >>> (addend.toInt & 31)
                result
              case VectorTernaryOperation.BitFieldInsert32   => (left & right) | (~left & addend)
              case VectorTernaryOperation.AndOr32            => (left & right) | addend
              case VectorTernaryOperation.BitFieldExtractI32 =>
                val width = addend.toInt & 31
                if (width == 0) 0L
                else {
                  val mask = (1L << width) - 1L
                  Bits.low32(Bits.signExtend((left >>> (right.toInt & 31)) & mask, BitWidth.assume(width)))
                }
              case VectorTernaryOperation.BitFieldExtractU32 =>
                val width = addend.toInt & 31
                if (width == 0) 0L else Bits.low32(left >>> (right.toInt & 31)) & ((1L << width) - 1L)
              case VectorTernaryOperation.XorAddU32       => Bits.low32((left ^ right) + addend)
              case VectorTernaryOperation.AddShiftLeftU32 => (left + right) << (addend.toInt & 31)
              case VectorTernaryOperation.ShiftLeftAddU32 => (left << (right.toInt & 31)) + addend
              case VectorTernaryOperation.ShiftLeftOr32   => (left << (right.toInt & 31)) | addend
              case VectorTernaryOperation.PermuteBytes32  => permuteBytes(left, right, addend)
            }
          )
        }
        AdvanceStep
      case Instruction.VMadU64U32(dest, carryDest, srcA, srcB, srcC) =>
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
        }
        updateLaneMask(carryDest, booleanLaneMask(madCarry))
        AdvanceStep
      case Instruction.VMadI64I32(dest, carryDest, srcA, srcB, srcC) =>
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
      case other => stepCompareAndMemory(index, other)
    }

    // Only collect addresses that this instruction will actually read. Local apertures,
    // code-object globals and descriptor out-of-bounds lanes never reach the host pump.
    def prepareVectorReads(instruction: Instruction): Either[Fault, Unit] = {
      def global(register: Int, scalarRegister: Option[Int], offset: Int)(lane: Int): Option[Long] =
        Some(scalarRegister.map(r => pair(scalar, r) + Bits.low32(vector(register)(lane)))
          .getOrElse(pair(vector, register, lane)) + signed13(offset))
      def flat(register: Int, offset: Int, size: Int)(lane: Int): Option[Long] = {
        val address = pair(vector, register, lane) + flatOffset(offset)
        Option.unless(ByteRange.contains(lds.length, address, size.toLong) ||
          ByteRange.contains(scratch(lane).length, address, size.toLong))(address)
      }
      def buffer(
          register: Option[Int],
          resource: Int,
          scalarOffset: Option[Int],
          offset: Int,
          size: Int
      )(lane: Int): Option[Long] = {
        val base  = sourceScalar64(InstructionSource.Scalar(resource)) & 0x0000ffffffffffffL
        val start = bufferOffset(lane, register, scalarOffset, offset, base == 0L)
        Option.when(
          base != 0L && (start >= 0L && start <= Bits.low32(scalar(resource + 2)) - size.toLong)
        )(base + start)
      }
      def prepare(size: Int, addressOf: Int => Option[Long]): Either[Fault, Unit] = memory match {
        case context: MemoryContext =>
          val ranges = Vec.from((0 until lanes.value).filter(active(_)).flatMap(addressOf).filter { address =>
            aliasedRegion(address, allocations) == null &&
            !ByteRange.contains(kernarg.length, address - KernargAddress, size.toLong) &&
            !ByteRange.contains(dispatchBytes.length, address - DispatchAddress, size.toLong) &&
            !ByteRange.contains(kernel.readOnly.length, address - kernel.readOnlyAddress, size.toLong) &&
            globalRegionIndex(address, size, kernel.globalMemory) < 0
          }.map(address => (GuestAddress(address), size.refineUnsafe[Positive0])))
          if (ranges.isEmpty) Right(()) else context.prepareReads(ranges)
        case _ => Right(())
      }
      instruction match {
        case Instruction.GlobalLoad(_, address, scalarAddress, size, offset, false) =>
          prepare(size, global(address, scalarAddress, offset))
        case Instruction.GlobalLoadSigned(_, address, scalarAddress, size, offset) =>
          prepare(size, global(address, scalarAddress, offset))
        case Instruction.GlobalLoadD16(_, address, scalarAddress, offset, _) =>
          prepare(2, global(address, scalarAddress, offset))
        case Instruction.FlatLoad(_, address, size, offset, false) => prepare(size, flat(address, offset, size))
        case Instruction.FlatLoadSigned(_, address, size, offset)  => prepare(size, flat(address, offset, size))
        case Instruction.VFlatLoadD16(_, address, offset, _)       => prepare(2, flat(address, offset, 2))
        case Instruction.BufferLoad(_, address, resource, scalarOffset, size, offset) =>
          prepare(size, buffer(address, resource, scalarOffset, offset, size))
        case Instruction.BufferLoadD16(_, address, resource, scalarOffset, size, offset, _) =>
          prepare(size, buffer(address, resource, scalarOffset, offset, size))
        case _ => Right(())
      }
    }

    def stepCompareAndMemory(index: Int, instruction: Instruction): Either[Fault, Option[Int]] =
      prepareVectorReads(instruction).flatMap(_ => stepPreparedMemory(index, instruction))

    def stepPreparedMemory(index: Int, instruction: Instruction): Either[Fault, Option[Int]] = {
      def runGlobalAtomic(
          dest: Int,
          addressRegister: Int,
          scalarAddress: Option[Int],
          byteCount: Int,
          offset: Int,
          returnValue: Boolean
      )(update: (Int, Long) => Long): Either[Fault, Unit] = {
        def atomicLane(lane: Int): Either[Fault, Unit] =
          if (lane == lanes.value) Right(())
          else if (!active(lane)) atomicLane(lane + 1)
          else {
            val address = scalarAddress
              .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
              .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
            atomicModifyValue(
              address,
              MemoryValueByteWidth.assume(byteCount),
              allocations,
              memory,
              kernel.globalMemory,
              kernel.globalMemoryLocks
            ) { oldValue =>
              val updated = update(lane, oldValue)
              if (byteCount == 8) updated else Bits.low32(updated)
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
        atomicLane(0)
      }

      instruction match {
        case Instruction.VCompare(operation, srcA, srcB) =>
          def operand(sourceRegister: InstructionSource, lane: Int, right: Boolean): Long = operation.kind match {
            case VectorComparisonKind.Signed64 | VectorComparisonKind.Unsigned64 =>
              source64(sourceRegister, lane, scalar, vector)
            case VectorComparisonKind.ClassFloat64 if right =>
              source(sourceRegister, lane)
            case VectorComparisonKind.Float64 | VectorComparisonKind.ClassFloat64 =>
              sourceFloating64(sourceRegister, lane, scalar, vector)
            case _ => source(sourceRegister, lane)
          }
          var resultMask = 0L
          foreachLane { lane =>
            if (
              active(lane) && compare(operation, operand(srcA, lane, right = false), operand(srcB, lane, right = true))
            )
              resultMask |= 1L << lane
          }
          if (operation.updateExec) active.bits = resultMask else carry.bits = resultMask
          AdvanceStep
        case Instruction.VCompareSubDword(
              operation,
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
            result(lane) = compare(operation, left, right)
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
          AdvanceStep
        case Instruction.VCompareTo(dest, operation, srcA, srcB, absA, absB, negateA, negateB) =>
          def operand(
              sourceRegister: InstructionSource,
              lane: Int,
              absolute: Boolean,
              negate: Boolean,
              right: Boolean
          ): Long = operation.kind match {
            case VectorComparisonKind.Float16 =>
              val raw      = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(source(sourceRegister, lane))))
              val prepared = if (absolute) math.abs(raw) else raw
              IEEE754.floatToBinary16(if (negate) -prepared else prepared).value
            case VectorComparisonKind.ClassFloat32 if right =>
              source(sourceRegister, lane)
            case VectorComparisonKind.Float32 | VectorComparisonKind.ClassFloat32 =>
              val raw      = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source(sourceRegister, lane))))
              val prepared = if (absolute) math.abs(raw) else raw
              IEEE754.floatToBinary32(if (negate) -prepared else prepared).value
            case VectorComparisonKind.ClassFloat64 if right =>
              source(sourceRegister, lane)
            case VectorComparisonKind.Float64 | VectorComparisonKind.ClassFloat64 =>
              val raw      = IEEE754.binary64ToDouble(sourceFloating64(sourceRegister, lane, scalar, vector))
              val prepared = if (absolute) math.abs(raw) else raw
              IEEE754.doubleToBinary64(if (negate) -prepared else prepared)
            case VectorComparisonKind.Signed64 | VectorComparisonKind.Unsigned64 =>
              source64(sourceRegister, lane, scalar, vector)
            case _ => source(sourceRegister, lane)
          }
          var resultMask = 0L
          foreachLane { lane =>
            if (
              active(lane) && compare(
                operation,
                operand(srcA, lane, absA, negateA, right = false),
                operand(srcB, lane, absB, negateB, right = true)
              )
            ) resultMask |= 1L << lane
          }
          if (operation.updateExec) active.bits = resultMask
          else updateLaneMask(dest, resultMask)
          AdvanceStep
        case Instruction.VShiftLeft64(dest, amount, src) =>
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
        case Instruction.VShiftRight64(dest, amount, src) =>
          foreachActive { lane =>
            val shifted = source64(src, lane, scalar, vector) >>> (source(amount, lane).toInt & 63)
            vector(dest)(lane) = Bits.low32(shifted)
            vector(dest + 1)(lane) = Bits.high32(shifted)
          }
          AdvanceStep
        case Instruction.VShiftRightArithmetic64(dest, amount, src) =>
          foreachActive { lane =>
            val shifted = source64(src, lane, scalar, vector) >> (source(amount, lane).toInt & 63)
            vector(dest)(lane) = Bits.low32(shifted)
            vector(dest + 1)(lane) = Bits.high32(shifted)
          }
          AdvanceStep
        case Instruction.DSReadD16(dest, addressRegister, byteCount, offset, high) =>
          val scalarByteCount                          = ScalarByteWidth.assume(byteCount)
          def loadLane(lane: Int): Either[Fault, Unit] =
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
        case Instruction.DSSignedReadD16(dest, addressRegister, byteCount, offset, high) =>
          val scalarByteCount                          = ScalarByteWidth.assume(byteCount)
          val bitWidth                                 = BitWidth.assume(byteCount * 8)
          def loadLane(lane: Int): Either[Fault, Unit] =
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
        case Instruction.DSRead(dest, addressRegister, byteCount, offsetA, offsetB) =>
          def loadLane(lane: Int): Either[Fault, Unit] =
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
                  loadLane(lane + 1)
                }
              }
            }
          advanceAfter(loadLane(0))
        case Instruction.DSSignedRead(dest, addressRegister, byteCount, offsetA, offsetB) =>
          val scalarByteCount                          = ScalarByteWidth.assume(byteCount)
          val bitWidth                                 = BitWidth.assume(byteCount * 8)
          def loadLane(lane: Int): Either[Fault, Unit] =
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
        case Instruction.DSPermute(dest, addressRegister, src, offset) =>
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
        case Instruction.DSBPermute(dest, addressRegister, src, offset) =>
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
        case Instruction.DSSwizzle(dest, src, control) =>
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
        case Instruction.DSAtomic(operation, dest, addressRegister, src, byteCount, offset, returnValue) =>
          val scalarByteCount                            = ScalarByteWidth.assume(byteCount)
          def atomicLane(lane: Int): Either[Fault, Unit] =
            if (lane == lanes.value) Right(())
            else if (!active(lane)) atomicLane(lane + 1)
            else {
              val base = Bits.low32(vector(addressRegister)(lane))
              readLDS(lds, base + offset.toLong, byteCount).flatMap { bytes =>
                val previous = LittleEndian.decode(bytes, 0, scalarByteCount)
                val operand  = if (byteCount == 8)
                  (vector(src)(lane) & 0xffffffffL) | ((vector(src + 1)(lane) & 0xffffffffL) << 32)
                else Bits.low32(vector(src)(lane))
                val updated = operation match {
                  case DSAtomicOperation.AddU32       => Bits.low32(previous + operand)
                  case DSAtomicOperation.SubtractU32  => Bits.low32(previous - operand)
                  case DSAtomicOperation.IncrementU32 => Bits.low32(previous + 1L)
                  case DSAtomicOperation.DecrementU32 => Bits.low32(previous - 1L)
                  case DSAtomicOperation.MinimumI32   => math.min(previous.toInt, operand.toInt).toLong & Bits.Mask32
                  case DSAtomicOperation.MaximumI32   => math.max(previous.toInt, operand.toInt).toLong & Bits.Mask32
                  case DSAtomicOperation.MinimumU32   => math.min(Bits.low32(previous), Bits.low32(operand))
                  case DSAtomicOperation.MaximumU32   => math.max(Bits.low32(previous), Bits.low32(operand))
                  case DSAtomicOperation.AddU64       => previous + operand
                  case DSAtomicOperation.SubtractU64  => previous - operand
                  case DSAtomicOperation.IncrementU64 => previous + 1L
                  case DSAtomicOperation.DecrementU64 => previous - 1L
                  case DSAtomicOperation.And32        => Bits.low32(previous) & Bits.low32(operand)
                  case DSAtomicOperation.Or32         => Bits.low32(previous) | Bits.low32(operand)
                  case DSAtomicOperation.Xor32        => Bits.low32(previous) ^ Bits.low32(operand)
                  case DSAtomicOperation.And64        => previous & operand
                  case DSAtomicOperation.Or64         => previous | operand
                  case DSAtomicOperation.Xor64        => previous ^ operand
                  case DSAtomicOperation.MinimumI64   => math.min(previous, operand)
                  case DSAtomicOperation.MaximumI64   => math.max(previous, operand)
                  case DSAtomicOperation.MinimumU64   =>
                    if (java.lang.Long.compareUnsigned(previous, operand) <= 0) previous else operand
                  case DSAtomicOperation.MaximumU64 =>
                    if (java.lang.Long.compareUnsigned(previous, operand) >= 0) previous else operand
                  case DSAtomicOperation.AddF32 =>
                    IEEE754.floatToBinary32(
                      IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(previous.toInt))) + IEEE754.binary32ToFloat(
                        Binary32.fromLow(Bits.low32(
                          operand.toInt
                        ))
                      )
                    ).value.toLong & 0xffffffffL
                  case DSAtomicOperation.MinimumF32 =>
                    IEEE754.floatToBinary32(
                      math.min(
                        IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(previous.toInt))),
                        IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(operand.toInt)))
                      )
                    ).value.toLong & 0xffffffffL
                  case DSAtomicOperation.MaximumF32 =>
                    IEEE754.floatToBinary32(
                      math.max(
                        IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(previous.toInt))),
                        IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(operand.toInt)))
                      )
                    ).value.toLong & 0xffffffffL
                  case DSAtomicOperation.AddF64 =>
                    IEEE754.doubleToBinary64(
                      IEEE754.binary64ToDouble(previous) + IEEE754.binary64ToDouble(operand)
                    )
                  case DSAtomicOperation.MinimumF64 =>
                    IEEE754.doubleToBinary64(
                      math.min(
                        IEEE754.binary64ToDouble(previous),
                        IEEE754.binary64ToDouble(operand)
                      )
                    )
                  case DSAtomicOperation.MaximumF64 =>
                    IEEE754.doubleToBinary64(
                      math.max(
                        IEEE754.binary64ToDouble(previous),
                        IEEE754.binary64ToDouble(operand)
                      )
                    )
                }
                if (returnValue)
                  writeVectorBytes(vector, dest, lane, LittleEndian.toArray(previous, scalarByteCount))
                writeLDS(lds, base + offset.toLong, LittleEndian.toArray(updated, scalarByteCount))
                  .flatMap(_ => atomicLane(lane + 1))
              }
            }
          advanceAfter(atomicLane(0))
        case Instruction.DSWrite(addressRegister, srcA, srcB, byteCount, offsetA, offsetB) =>
          def storeLane(lane: Int): Either[Fault, Unit] =
            if (lane == lanes.value) Right(())
            else if (!active(lane)) storeLane(lane + 1)
            else {
              val base = Bits.low32(vector(addressRegister)(lane))
              for {
                firstAddress = base + offsetA.toLong
                firstBytes   = vectorBytes(vector, srcA, lane, byteCount)
                _ <- writeLDS(lds, firstAddress, firstBytes)
                _ <- (srcB, offsetB) match {
                  case (Some(register), Some(offset)) =>
                    val address = base + offset.toLong
                    val bytes   = vectorBytes(vector, register, lane, byteCount)
                    writeLDS(lds, address, bytes)
                  case (None, None) => Right(())
                  case _            => Left(Fault.Backend("malformed paired DS write"))
                }
                _ <- storeLane(lane + 1)
              } yield ()
            }
          advanceAfter(storeLane(0))
        case Instruction.DSWriteD16High(addressRegister, src, offset) =>
          def storeLane(lane: Int): Either[Fault, Unit] =
            if (lane == lanes.value) Right(())
            else if (!active(lane)) storeLane(lane + 1)
            else {
              val base    = Bits.low32(vector(addressRegister)(lane))
              val value   = vector(src)(lane) >>> 16
              val address = base + offset.toLong
              writeLDS(lds, address, LittleEndian.toArray(value, ScalarByteWidth.Word))
                .flatMap(_ => storeLane(lane + 1))
            }
          advanceAfter(storeLane(0))
        case Instruction.DSWriteD16HighByte(addressRegister, src, offset) =>
          def storeLane(lane: Int): Either[Fault, Unit] =
            if (lane == lanes.value) Right(())
            else if (!active(lane)) storeLane(lane + 1)
            else {
              val base    = Bits.low32(vector(addressRegister)(lane))
              val value   = vector(src)(lane) >>> 16
              val address = base + offset.toLong
              writeLDS(lds, address, LittleEndian.toArray(value, ScalarByteWidth.Byte))
                .flatMap(_ => storeLane(lane + 1))
            }
          advanceAfter(storeLane(0))
        case Instruction.GlobalLoad(dest, addressRegister, scalarAddress, byteCount, offset, coherent) =>
          val scalarBase = scalarAddress match {
            case Some(register) => pair(scalar, register)
            case None           => 0L
          }
          val scalarWidth           = if (byteCount <= 8) MemoryValueByteWidth.assume(byteCount) else null
          val displacement          = signed13(offset)
          var lane                  = 0
          var failure: Fault | Null = null
          while (lane < lanes.value && failure == null) {
            if (active(lane)) {
              val address =
                if (scalarAddress.isDefined) scalarBase + Bits.low32(vector(addressRegister)(lane)) + displacement
                else pair(vector, addressRegister, lane) + displacement
              if (scalarWidth != null)
                (if (coherent && (byteCount == 4 || byteCount == 8) && !immutableAddress(address, byteCount))
                   atomicModifyValue(
                     address,
                     scalarWidth.nn,
                     allocations,
                     memory,
                     kernel.globalMemory,
                     kernel.globalMemoryLocks
                   )(identity)
                 else readValue(
                   address,
                   scalarWidth.nn,
                   kernarg,
                   dispatchBytes,
                   kernel.readOnlyAddress,
                   kernel.readOnly,
                   kernel.globalMemory,
                   allocations,
                   memory
                 )) match {
                  case Left(error)  => failure = error
                  case Right(value) => writeVectorValue(vector, dest, lane, value, byteCount)
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
                ) match {
                  case Left(error)  => failure = error
                  case Right(bytes) => writeVectorBytes(vector, dest, lane, bytes)
                }
            }
            lane += 1
          }
          advanceAfter(if (failure == null) Right(()) else Left(failure.nn))
        case Instruction.GlobalLoadSigned(dest, addressRegister, scalarAddress, byteCount, offset) =>
          val scalarByteCount                          = ScalarByteWidth.assume(byteCount)
          val bitWidth                                 = BitWidth.assume(byteCount * 8)
          def loadLane(lane: Int): Either[Fault, Unit] =
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
        case Instruction.GlobalLoadD16(dest, addressRegister, scalarAddress, offset, high) =>
          def loadLane(lane: Int): Either[Fault, Unit] =
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
                  val loaded = LittleEndian.decode(bytes, 0, ScalarByteWidth.Word)
                  vector(dest)(lane) =
                    if (high) (vector(dest)(lane) & 0x0000ffffL) | (loaded << 16)
                    else (vector(dest)(lane) & 0xffff0000L) | loaded
                  loadLane(lane + 1)
              }
            }
          advanceAfter(loadLane(0))
        case Instruction.GlobalStore(addressRegister, scalarAddress, src, byteCount, offset) =>
          def storeLane(lane: Int): Either[Fault, Unit] =
            if (lane == lanes.value) Right(())
            else if (!active(lane)) storeLane(lane + 1)
            else {
              val address = scalarAddress
                .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
                .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
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
                  writeBytes(
                    address,
                    vectorBytes(vector, src, lane, byteCount),
                    allocations,
                    memory,
                    kernel.globalMemory
                  )
              stored match {
                case Left(error) => Left(error)
                case Right(_)    => storeLane(lane + 1)
              }
            }
          advanceAfter(storeLane(0))
        case Instruction.GlobalAtomicAdd(dest, addressRegister, scalarAddress, src, byteCount, offset, returnValue) =>
          advanceAfter(runGlobalAtomic(dest, addressRegister, scalarAddress, byteCount, offset, returnValue) {
            (lane, oldValue) =>
              val operand = if (byteCount == 8) pair(vector, src, lane) else Bits.low32(vector(src)(lane))
              oldValue + operand
          })
        case Instruction.GlobalAtomicSwap(dest, addressRegister, scalarAddress, src, byteCount, offset, returnValue) =>
          advanceAfter(runGlobalAtomic(dest, addressRegister, scalarAddress, byteCount, offset, returnValue) {
            (lane, _) =>
              if (byteCount == 8) pair(vector, src, lane) else Bits.low32(vector(src)(lane))
          })
        case Instruction.GlobalAtomicBinary(
              operation,
              dest,
              addressRegister,
              scalarAddress,
              src,
              byteCount,
              offset,
              returnValue
            ) =>
          advanceAfter(runGlobalAtomic(dest, addressRegister, scalarAddress, byteCount, offset, returnValue) {
            (lane, oldValue) =>
              val operand = if (byteCount == 8) pair(vector, src, lane) else Bits.low32(vector(src)(lane))
              operation match {
                case AtomicBinaryOperation.Add             => oldValue + operand
                case AtomicBinaryOperation.Subtract        => oldValue - operand
                case AtomicBinaryOperation.MinimumUnsigned =>
                  if (java.lang.Long.compareUnsigned(oldValue, operand) <= 0) oldValue else operand
                case AtomicBinaryOperation.MaximumUnsigned =>
                  if (java.lang.Long.compareUnsigned(oldValue, operand) >= 0) oldValue else operand
                case AtomicBinaryOperation.MinimumSigned =>
                  if (byteCount == 8) Math.min(oldValue, operand)
                  else if (oldValue.toInt <= operand.toInt) oldValue else operand
                case AtomicBinaryOperation.MaximumSigned =>
                  if (byteCount == 8) Math.max(oldValue, operand)
                  else if (oldValue.toInt >= operand.toInt) oldValue else operand
                case AtomicBinaryOperation.And => oldValue & operand
                case AtomicBinaryOperation.Or  => oldValue | operand
                case AtomicBinaryOperation.Xor => oldValue ^ operand
              }
          })
        case Instruction.GlobalAtomicInc(dest, addressRegister, scalarAddress, src, byteCount, offset, returnValue) =>
          advanceAfter(runGlobalAtomic(dest, addressRegister, scalarAddress, byteCount, offset, returnValue) {
            (lane, oldValue) =>
              val limit = if (byteCount == 8) pair(vector, src, lane) else Bits.low32(vector(src)(lane))
              if (java.lang.Long.compareUnsigned(oldValue, limit) >= 0) 0L else oldValue + 1L
          })
        case Instruction.GlobalAtomicCompareSwap(
              dest,
              addressRegister,
              scalarAddress,
              compareSrc,
              swapSrc,
              byteCount,
              offset,
              returnValue
            ) =>
          advanceAfter(runGlobalAtomic(dest, addressRegister, scalarAddress, byteCount, offset, returnValue) {
            (lane, oldValue) =>
              val (compare, swap) =
                if (byteCount == 8) (pair(vector, compareSrc, lane), pair(vector, swapSrc, lane))
                else (Bits.low32(vector(compareSrc)(lane)), Bits.low32(vector(swapSrc)(lane)))
              if (oldValue == compare) swap else oldValue
          })
        case Instruction.GlobalStoreD16High(addressRegister, scalarAddress, src, byteCount, offset) =>
          val scalarByteCount                           = ScalarByteWidth.assume(byteCount)
          def storeLane(lane: Int): Either[Fault, Unit] =
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
        case Instruction.FlatLoad(dest, addressRegister, byteCount, offset, coherent) =>
          def loadLane(lane: Int): Either[Fault, Unit] =
            if (lane == lanes.value) Right(())
            else if (!active(lane)) loadLane(lane + 1)
            else {
              val address = pair(vector, addressRegister, lane) + flatOffset(offset)
              val bytes   =
                if (
                  coherent && (byteCount == 4 || byteCount == 8) && !immutableAddress(address, byteCount) &&
                  !ByteRange.contains(lds.length, address, byteCount.toLong) &&
                  !ByteRange.contains(scratch(lane).length, address, byteCount.toLong)
                )
                  atomicModifyValue(
                    address,
                    MemoryValueByteWidth.assume(byteCount),
                    allocations,
                    memory,
                    kernel.globalMemory,
                    kernel.globalMemoryLocks
                  )(identity)
                    .map(value => LittleEndian.toVec(value, ScalarByteWidth.assume(byteCount)))
                else flatLoad(lane, address, byteCount)
              bytes match {
                case Left(error)  => Left(error)
                case Right(bytes) =>
                  writeVectorBytes(vector, dest, lane, bytes)
                  loadLane(lane + 1)
              }
            }
          advanceAfter(loadLane(0))
        case Instruction.FlatLoadSigned(dest, addressRegister, byteCount, offset) =>
          val scalarByteCount                          = ScalarByteWidth.assume(byteCount)
          val bitWidth                                 = BitWidth.assume(byteCount * 8)
          def loadLane(lane: Int): Either[Fault, Unit] =
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
        case Instruction.VFlatLoadD16(dest, addressRegister, offset, high) =>
          def loadLane(lane: Int): Either[Fault, Unit] =
            if (lane == lanes.value) Right(())
            else if (!active(lane)) loadLane(lane + 1)
            else {
              val address = pair(vector, addressRegister, lane) + flatOffset(offset)
              val bytes   = flatLoad(lane, address, 2)
              bytes match {
                case Left(error)        => Left(error)
                case Right(loadedBytes) =>
                  val loaded = LittleEndian.decode(loadedBytes, 0, ScalarByteWidth.Word)
                  vector(dest)(lane) =
                    if (high) (vector(dest)(lane) & 0x0000ffffL) | (loaded << 16)
                    else (vector(dest)(lane) & 0xffff0000L) | loaded
                  loadLane(lane + 1)
              }
            }
          advanceAfter(loadLane(0))
        case Instruction.FlatStore(addressRegister, src, byteCount, offset) =>
          def storeLane(lane: Int): Either[Fault, Unit] =
            if (lane == lanes.value) Right(())
            else if (!active(lane)) storeLane(lane + 1)
            else {
              val address = pair(vector, addressRegister, lane) + flatOffset(offset)
              val bytes   = vectorBytes(vector, src, lane, byteCount)
              flatStore(lane, address, bytes) match {
                case Left(error) => Left(error)
                case Right(_)    => storeLane(lane + 1)
              }
            }
          advanceAfter(storeLane(0))
        case Instruction.FlatStoreD16High(addressRegister, src, byteCount, offset) =>
          val scalarByteCount                           = ScalarByteWidth.assume(byteCount)
          def storeLane(lane: Int): Either[Fault, Unit] =
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
        case Instruction.BufferCacheInvalidate =>
          // A following ordinary load must not reuse bytes cached before acquire.
          memory.invalidateHostReads()
          AdvanceStep
        case Instruction.BufferLoad(dest, address, resource, scalarOffset, byteCount, offset) =>
          def loadLane(lane: Int): Either[Fault, Unit] =
            if (lane == lanes.value) Right(())
            else if (!active(lane)) loadLane(lane + 1)
            else {
              val base  = sourceScalar64(InstructionSource.Scalar(resource)) & 0x0000ffffffffffffL
              val start = bufferOffset(lane, address, scalarOffset, offset, base == 0L)
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
                    loadLane(lane + 1)
                }
              else if (start < 0L || start > scratch(lane).length.toLong - byteCount.toLong)
                Left(Fault.Backend(
                  s"buffer load ${operations(index)} at instruction $index lane $lane offset $start size $byteCount exceeds private scratch ${scratch(lane).length}"
                ))
              else {
                val bytes = java.util.Arrays.copyOfRange(scratch(lane), start.toInt, start.toInt + byteCount)
                writeVectorBytes(vector, dest, lane, bytes)
                loadLane(lane + 1)
              }
            }
          advanceAfter(loadLane(0))
        case Instruction.BufferLoadD16(dest, address, resource, scalarOffset, byteCount, offset, high) =>
          val scalarByteCount                          = ScalarByteWidth.assume(byteCount)
          def loadLane(lane: Int): Either[Fault, Unit] =
            if (lane == lanes.value) Right(())
            else if (!active(lane)) loadLane(lane + 1)
            else {
              val base   = sourceScalar64(InstructionSource.Scalar(resource)) & 0x0000ffffffffffffL
              val start  = bufferOffset(lane, address, scalarOffset, offset, base == 0L)
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
                  Left(Fault.Backend(
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
        case Instruction.BufferStore(address, resource, scalarOffset, src, byteCount, offset) =>
          executeBufferStore(index, address, resource, scalarOffset, src, byteCount, offset, None)
        case Instruction.BufferStoreD16High(address, resource, scalarOffset, src, byteCount, offset) =>
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
        case other => Left(Fault.Unsupported(s"unhandled AMDGPU instruction $other"))
      }
    }

    def step(index: Int): Either[Fault, Option[Int]] = stepInstruction(index)

    var instructionIndex       = if (initial == null) entryInstruction else initial.nextInstruction
    var running                = true
    var reachedBarrier         = false
    var failure: Option[Fault] = None
    var pendingInstructionCost = 0L

    def checkpoint(cost: Int): Fault | Null = {
      def flush(): Fault | Null =
        if (pendingInstructionCost == 0L) null
        else
          control.checkpoint(math.min(pendingInstructionCost, Int.MaxValue.toLong).toInt.assume[Positive]) match {
            case Left(fault) => fault
            case Right(())   =>
              pendingInstructionCost = math.max(0L, pendingInstructionCost - Int.MaxValue.toLong)
              if (pendingInstructionCost == 0L) null else flush()
          }

      // Deferred charges cannot share unreserved credits between concurrent waves.
      if (scheduler.parallelism > 1) {
        if (cost == 0) null
        else control.checkpoint(cost.assume[Positive]) match {
          case Left(fault) => fault
          case Right(())   => null
        }
      } else if (cost == 0) flush()
      else if (pendingInstructionCost == 0L && control.cancellation.isCancelled)
        Fault.Cancelled
      else if (pendingInstructionCost.toLong + cost.toLong > control.remainingInstructions) {
        val flushFailure = flush()
        if (flushFailure != null) flushFailure
        else
          control.checkpoint(cost.assume[Positive]) match {
            case Left(fault) => fault
            case Right(())   => null
          }
      } else {
        pendingInstructionCost += cost
        if (pendingInstructionCost >= control.limits.quantum) flush() else null
      }
    }

    while (running && failure.isEmpty)
      checkpoint(math.max(1, active.count)) match {
        case fault: Fault => failure = Some(fault)
        case null         =>
          val stepResult = step(instructionIndex)
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
      case fault: Fault => failure = Some(fault)
      case null         => ()
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

  // These memory-instruction helpers consume the allocation-free null sentinel returned by boundGlobal.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:GemstoneNoAssume"))
  private def readBytes(
      address: Long,
      size: Int,
      kernarg: Array[Byte],
      dispatch: Array[Byte],
      readOnlyAddress: Long,
      readOnly: Array[Byte],
      globalMemory: Array[(Long, Array[Byte])],
      allocations: Array[GlobalBinding],
      memory: Memory
  ): Either[Fault, Vec[Byte]] = {
    val binding = aliasedRegion(address, allocations)
    if (binding != null)
      memory.read(
        binding.allocation,
        (binding.offset + address - binding.address).assume[Positive0],
        size.assume[Positive0]
      )
    else localRead(address, size, KernargAddress, kernarg)
      .orElse(localRead(address, size, DispatchAddress, dispatch))
      .orElse(localRead(address, size, readOnlyAddress, readOnly))
      .orElse(globalRead(address, size, globalMemory)) match {
      case Some(bytes) => Right(Vec.unsafeFromArray(bytes))
      case None        =>
        memory match {
          case context: MemoryContext =>
            context.read(GuestAddress(address), size.assume[Positive0]).leftMap { error =>
              Fault.Memory(s"AMDGPU memory read address=0x${address.toHexString} size=$size: ${error.message}")
            }
          case _ =>
            Left(Fault.Backend("AMDGPU memory context has no address space"))
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
      allocations: Array[GlobalBinding],
      memory: Memory
  ): Either[Fault, Long] = {
    val scalarSize = size.scalarByteWidth
    val binding    = aliasedRegion(address, allocations)
    if (binding != null)
      memory.readValue(binding.allocation, (binding.offset + address - binding.address).assume[Positive0], size)
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
            val globalIndex = globalRegionIndex(address, size.value, globalMemory)
            if (globalIndex >= 0) {
              val (base, bytes) = globalMemory(globalIndex)
              Right(LittleEndian.decode(bytes, (address - base).toInt, scalarSize))
            } else memory match {
              case context: MemoryContext =>
                context.readValue(GuestAddress(address), size).leftMap(error =>
                  Fault.Memory(s"AMDGPU memory read address=0x${address.toHexString} size=$size: ${error.message}")
                )
              case _ =>
                Left(Fault.Backend("AMDGPU memory context has no address space"))
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
      allocations: Array[GlobalBinding],
      memory: Memory,
      globalMemory: Array[(Long, Array[Byte])]
  ): Either[Fault, Unit] = {
    val binding = aliasedRegion(address, allocations)
    if (binding != null)
      memory.write(
        binding.allocation,
        (binding.offset + address - binding.address).assume[Positive0],
        Vec.unsafeFromArray(bytes)
      )
    else globalWrite(address, bytes, globalMemory) match {
      case Some(_) => Right(())
      case None    =>
        memory match {
          case context: MemoryContext =>
            context.write(GuestAddress(address), Vec.unsafeFromArray(bytes)).leftMap { error =>
              Fault.Memory(
                s"AMDGPU memory write address=0x${address.toHexString} size=${bytes.length}: ${error.message}"
              )
            }
          case _ =>
            Left(Fault.Backend("AMDGPU memory context has no address space"))
        }
    }

  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:GemstoneNoAssume"))
  private def writeValue(
      address: Long,
      value: Long,
      size: MemoryValueByteWidth,
      allocations: Array[GlobalBinding],
      memory: Memory,
      globalMemory: Array[(Long, Array[Byte])]
  ): Either[Fault, Unit] = {
    val scalarSize = size.scalarByteWidth
    val binding    = aliasedRegion(address, allocations)
    if (binding != null)
      memory.writeValue(binding.allocation, (binding.offset + address - binding.address).assume[Positive0], value, size)
    else {
      val globalIndex = globalRegionIndex(address, size.value, globalMemory)
      if (globalIndex >= 0) {
        val (base, bytes) = globalMemory(globalIndex)
        LittleEndian.write(value, bytes, (address - base).toInt, scalarSize)
        Right(())
      } else memory match {
        case context: MemoryContext =>
          context.writeValue(GuestAddress(address), value, size).leftMap(error =>
            Fault.Memory(s"AMDGPU memory write address=0x${address.toHexString} size=$size: ${error.message}")
          )
        case _ =>
          Left(Fault.Backend("AMDGPU memory context has no address space"))
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:GemstoneNoAssume"))
  private def atomicModifyValue(
      address: Long,
      size: MemoryValueByteWidth,
      allocations: Array[GlobalBinding],
      memory: Memory,
      globalMemory: Array[(Long, Array[Byte])],
      globalMemoryLocks: Array[ReentrantLock]
  )(update: Long => Long): Either[Fault, Long] = {
    val scalarSize = size.scalarByteWidth
    val binding    = aliasedRegion(address, allocations)
    if (binding != null)
      memory.atomicModifyValue(
        binding.allocation,
        (binding.offset + address - binding.address).assume[Positive0],
        size
      )(update)
    else {
      val globalIndex = globalRegionIndex(address, size.value, globalMemory)
      if (globalIndex >= 0) {
        val (base, bytes) = globalMemory(globalIndex)
        globalMemoryLocks(globalIndex).withLock {
          val offset   = (address - base).toInt
          val previous = LittleEndian.decode(bytes, offset, scalarSize)
          LittleEndian.write(update(previous), bytes, offset, scalarSize)
          Right(previous)
        }
      } else memory match {
        case context: MemoryContext =>
          context.atomicModifyValue(GuestAddress(address), size)(update).left.map { error =>
            Fault.Memory(s"AMDGPU atomic address=0x${address.toHexString} size=$size: ${error.message}")
          }
        case _ =>
          Left(Fault.Backend("AMDGPU memory context has no address space"))
      }
    }
  }

  private def globalRead(address: Long, size: Int, regions: Array[(Long, Array[Byte])]): Option[Array[Byte]] = {
    val index = globalRegionIndex(address, size, regions)
    if (index < 0) None
    else {
      val (base, bytes) = regions(index)
      val offset        = address - base
      Some(java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size))
    }
  }

  private def globalWrite(address: Long, value: Array[Byte], regions: Array[(Long, Array[Byte])]): Option[Unit] = {
    val index = globalRegionIndex(address, value.length, regions)
    if (index < 0) None
    else {
      val (base, bytes) = regions(index)
      val offset        = address - base
      Array.copy(value, 0, bytes, offset.toInt, value.length)
      Some(())
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def globalRegionIndex(address: Long, size: Int, regions: Array[(Long, Array[Byte])]): Int = {
    var index = 0
    var found = -1
    while (index < regions.length && found < 0) {
      val (base, bytes) = regions(index)
      val offset        = address - base
      if (address >= base && offset <= bytes.length.toLong - size.toLong) found = index
      index += 1
    }
    found
  }

  // Null is the allocation-free miss sentinel in this memory-instruction hot path.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def aliasedRegion(address: Long, allocations: Array[GlobalBinding]): GlobalBinding | Null = {
    var index                        = 0
    var result: GlobalBinding | Null = null
    while (index < allocations.length && result == null) {
      val region = allocations(index)
      if (address >= region.address && address - region.address < region.size) result = region
      index += 1
    }
    result
  }

  private def localRead(address: Long, size: Int, base: Long, bytes: Array[Byte]): Option[Array[Byte]] = {
    val offset = address - base
    Option.when(ByteRange.contains(bytes.length, offset, size.toLong))(
      java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size)
    )
  }

  private def unsignedInt(bytes: Vec[Byte], offset: Int): Long =
    LittleEndian.decode(bytes, offset, ScalarByteWidth.Dword)

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

  private def ldsFailure(address: Long, size: Int): Fault = {
    val offset = ldsOffset(address)
    Fault.Memory(
      f"out-of-bounds AMDGPU LDS read at 0x$address%x (offset 0x$offset%x, $size bytes)"
    )
  }

  private def readLDS(lds: Array[Byte], address: Long, size: Int): Either[Fault, Array[Byte]] = {
    val offset = checkedLdsOffset(lds, address, size)
    if (offset == InvalidLdsOffset) Left(ldsFailure(address, size))
    else Right(java.util.Arrays.copyOfRange(lds, offset, offset + size))
  }

  private def writeLDS(lds: Array[Byte], address: Long, bytes: Array[Byte]): Either[Fault, Unit] = {
    val offset = ldsOffset(address)
    Either.cond(
      ByteRange.contains(lds.length, offset, bytes.length.toLong), {
        Array.copy(bytes, 0, lds, offset.toInt, bytes.length)
        ()
      },
      Fault.Memory(
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
      val index = Decoding.InlineFloatingConstants.indexOf(raw)
      IEEE754.doubleToBinary64(if (index >= 0) Decoding.InlineFloatingValues(index) else raw.toDouble)
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
