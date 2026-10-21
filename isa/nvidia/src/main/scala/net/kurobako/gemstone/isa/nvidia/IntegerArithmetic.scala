package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.annotation.targetName

/** NVIDIA integer operations that combine many fields of their operands, as functions of the operands' bits. */
private[isa] object IntegerArithmetic {

  /** The legacy IADD.SAT: each of `lanes` 16-bit lanes of `left` and `right`, extended as signed values, added and
    * clamped to the signed range of a lane, with the carry out of the low lane's addition added to the high lane. The
    * lanes of `right` are negated exactly where `negate`, so that the negation of -32768 saturates in the sum rather
    * than wrapping to -32768.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def packedSaturatingAdd(left: UInt[32], right: UInt[32], lanes: Int in (2, 4), negate: Boolean): UInt[32] = {
    val bits                             = 32 / lanes
    val mask                             = (1L << bits) - 1L
    val low                              = -(1L << (bits - 1))
    val high                             = (1L << (bits - 1)) - 1L
    def lane(word: UInt[32], index: Int) = {
      val field = (word.raw >>> (index * bits)) & mask
      if ((field >>> (bits - 1)) != 0L) field - (1L << bits) else field
    }
    var result = 0L
    var carry  = 0L
    var index  = 0
    while (index < lanes) {
      val a   = lane(left, index)
      val b   = lane(right, index)
      val sum = a + (if (negate) -b else b) + carry
      result |= (math.min(math.max(sum, low), high) & mask) << (index * bits)
      if (index == 0) {
        // the carry out of the low lane's addition, as the sixteen-bit adder that produces the sum reports it
        val effective = (if (negate) -b else b) & mask
        carry = if ((a & mask) + effective > mask) 1L else 0L
      }
      index += 1
    }
    UInt[32](result)
  }

  /** The SIMD video operations: each of `lanes` 16-bit or 8-bit lanes of `left` and `right`, extended as their
    * signedness says, combined by `operation` and clamped to the result's lane range where `saturate`. The lanes are
    * packed into the result or, where `accumulate`, summed onto `addend`. A source whose `elements` name elements of
    * the register pair reads them instead of the lanes of its own word.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:PublicSignatures.primitive-run"))
  def simd(
      left: UInt[32],
      right: UInt[32],
      addend: UInt[32],
      operation: VideoOperation,
      lanes: Int in (2, 4),
      leftSigned: Boolean,
      rightSigned: Boolean,
      resultSigned: Boolean,
      saturate: Boolean,
      accumulate: Boolean,
      leftElements: Option[VideoElements],
      rightElements: Option[VideoElements]
  ): UInt[32] = {
    val bits                                              = 32 / lanes
    val mask                                              = (1L << bits) - 1L
    def lane(word: UInt[32], index: Int, signed: Boolean) = {
      val field = (word.raw >>> (index * bits)) & mask
      if (signed && (field >>> (bits - 1)) != 0L) field - (1L << bits) else field
    }
    // the element of the register pair a source's selector names, extended as its signedness says
    def element(elements: VideoElements, index: Int, signed: Boolean) = {
      val shift = elements.indices(index) * elements.bits
      val word  = if (shift < 32) left else right
      val field = (word.raw >>> (shift % 32)) & ((1L << elements.bits) - 1L)
      if (signed && (field >>> (elements.bits - 1)) != 0L) field - (1L << elements.bits) else field
    }
    val (low, high) = if (resultSigned) (-(1L << (bits - 1)), (1L << (bits - 1)) - 1L) else (0L, mask)
    var result      = if (accumulate) addend.raw else 0L
    var index       = 0
    while (index < lanes) {
      val a     = leftElements.fold(lane(left, index, leftSigned))(found => element(found, index, leftSigned))
      val b     = rightElements.fold(lane(right, index, rightSigned))(found => element(found, index, rightSigned))
      val value = operation match {
        case VideoOperation.Add                => a + b
        case VideoOperation.Subtract           => a - b
        case VideoOperation.Average            => if (a + b >= 0L) (a + b + 1L) >> 1 else (a + b) >> 1
        case VideoOperation.AbsoluteDifference => math.abs(a - b)
        case VideoOperation.Minimum            => math.min(a, b)
        case VideoOperation.Maximum            => math.max(a, b)
        case VideoOperation.ShiftLeft          => a << (b & 31L)
        case VideoOperation.ShiftRight         => a >> (b & 31L)
        case VideoOperation.Equal              => if (a == b) 1L else 0L
        case VideoOperation.NotEqual           => if (a != b) 1L else 0L
        case VideoOperation.Less               => if (a < b) 1L else 0L
        case VideoOperation.LessEqual          => if (a <= b) 1L else 0L
        case VideoOperation.Greater            => if (a > b) 1L else 0L
        case VideoOperation.GreaterEqual       => if (a >= b) 1L else 0L
      }
      val clamped = if (saturate) math.min(math.max(value, low), high) else value
      result = if (accumulate) result + clamped else result | ((clamped & mask) << (index * bits))
      index += 1
    }
    UInt[32](result)
  }

  /** `value` rounded to nearest even in OCP E4M3, finite values beyond its range saturated to its largest and negative
    * ones, where `relu`, zero; NaN is 0x7f.
    */
  def toE4M3(value: Float32, relu: Boolean): Float8E4M3 =
    Float8E4M3(UInt[8](float8(value.toDouble, Minifloat.E4M3, relu)))

  @targetName("halfToE4M3")
  def toE4M3(value: Float16, relu: Boolean): Float8E4M3 =
    Float8E4M3(UInt[8](float8(value.toDouble, Minifloat.E4M3, relu)))

  /** `value` rounded to nearest even in OCP E5M2, values beyond its finite range, infinities included, saturated to its
    * largest and negative ones, where `relu`, zero; NaN is 0x7f, whatever its sign.
    */
  def toE5M2(value: Float32, relu: Boolean): Float8E5M2 =
    Float8E5M2(UInt[8](float8(value.toDouble, Minifloat.E5M2, relu)))

  @targetName("halfToE5M2")
  def toE5M2(value: Float16, relu: Boolean): Float8E5M2 =
    Float8E5M2(UInt[8](float8(value.toDouble, Minifloat.E5M2, relu)))

  /** `value` as binary16, which holds every E4M3 value; NaN is the canonical binary16 NaN. */
  def fromE4M3(value: Float8E4M3): Float16 = half(Minifloat.E4M3.toDouble(value.bits.raw))

  /** `value` as binary16, which holds every E5M2 value; NaN is the canonical binary16 NaN. */
  def fromE5M2(value: Float8E5M2): Float16 = half(Minifloat.E5M2.toDouble(value.bits.raw))

  private def half(value: Double): Float16 =
    FloatingPoint[16](if (value.isNaN) VMArithmetic.CanonicalNaN16 else IEEE754.fromDouble(value, 16))

  private def float8(value: Double, format: Minifloat, relu: Boolean): Long =
    if (value.isNaN) 0x7fL
    else if (relu && (value < 0.0 || (value == 0.0 && 1.0 / value < 0.0))) 0L
    else format.fromDouble(value, saturate = true)

  /** `cvt.rna.tf32.f32`, as ptxas lowers it from sm_80: the binary32 bits rounded at their 13th bit to nearest with
    * ties away, NaN payloads truncated rather than rounded; `saturate` takes what lands on the top exponent one TF32
    * step lower, so infinities become the largest finite value.
    */
  def toTensorFloat32Away(value: Float32, saturate: Boolean): UInt[32] = {
    val bits      = value.bits.raw
    val magnitude = bits & 0x7fffffffL
    val rounded   =
      if (magnitude >= 0x7f800000L) bits & 0xffffe000L
      else
        (bits & 0x80000000L) |
          (Minifloat.TensorFloat32.fromDouble(
            value.toDouble,
            IEEE754.Rounding.NearestAway
          ) << 13 & 0x7fffffffL)
    UInt[32](if (saturate && (rounded & 0x7f800000L) == 0x7f800000L) rounded - 0x2000L else rounded)
  }

  /** `cvt.rn` and `cvt.rz` to TF32: binary32 rounded to the format, clamped at zero where `relu` and to the largest
    * finite value where `saturate`; NaN is the canonical binary32 NaN.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def toTensorFloat32(value: Float32, towardZero: Boolean, relu: Boolean, saturate: Boolean): UInt[32] = {
    val exact = value.toDouble
    UInt[32](
      if (exact.isNaN) VMArithmetic.CanonicalNaN32
      else if (relu && (exact < 0.0 || (exact == 0.0 && 1.0 / exact < 0.0))) 0L
      else if (exact.isInfinite && !saturate) value.bits.raw
      else
        Minifloat.TensorFloat32.fromDouble(
          exact,
          if (towardZero) IEEE754.Rounding.TowardZero else IEEE754.Rounding.NearestEven,
          saturate
        ) << 13
    )
  }

  /** `cvt.rs`: the binary32 `value` rounded to `format` toward zero, or away from it where the random bits of `bits`,
    * added below the discarded bits, carry out of them: 13 for binary16, 8 for E2M1 and 16 otherwise. Clamped at zero
    * where `relu`; a magnitude rounded beyond the largest finite one is that one where `saturate`, which the narrower
    * formats always are, and infinity otherwise.
    */
  def toStochastic(
      value: Float32,
      bits: UInt[16],
      format: StochasticFormat,
      relu: Boolean,
      saturate: Boolean
  ): UInt[16] = {
    val exact = value.toDouble
    val code  = format match {
      case StochasticFormat.Half =>
        stochastic(
          exact = exact,
          bits = bits.raw,
          random = 13,
          width = 16,
          maximum = 0x7bffL,
          nan = VMArithmetic.CanonicalNaN16,
          relu = relu,
          saturate = saturate
        )(
          IEEE754.fromDouble(_, 16, IEEE754.Rounding.TowardZero),
          IEEE754.toDouble(_, 16)
        )
      case StochasticFormat.BFloat16 =>
        stochastic(
          exact = exact,
          bits = bits.raw,
          random = 16,
          width = 16,
          maximum = Minifloat.BFloat16.maximum,
          nan = 0x7fffL,
          relu = relu,
          saturate = saturate
        )(
          Minifloat.BFloat16.fromDouble(_, IEEE754.Rounding.TowardZero),
          Minifloat.BFloat16.toDouble(_)
        )
      case StochasticFormat.E4M3 => narrow(exact, bits.raw, 16, Minifloat.E4M3, 0x7fL, relu)
      case StochasticFormat.E5M2 => narrow(exact, bits.raw, 16, Minifloat.E5M2, 0x7fL, relu)
      case StochasticFormat.E2M3 => narrow(exact, bits.raw, 16, Minifloat.E2M3, Minifloat.E2M3.maximum, relu)
      case StochasticFormat.E3M2 => narrow(exact, bits.raw, 16, Minifloat.E3M2, Minifloat.E3M2.maximum, relu)
      case StochasticFormat.E2M1 => narrow(exact, bits.raw, 8, Minifloat.E2M1, Minifloat.E2M1.maximum, relu)
    }
    UInt[16](code)
  }

  private def narrow(exact: Double, bits: Long, random: Int, format: Minifloat, nan: Long, relu: Boolean): Long =
    stochastic(exact, bits, random, format.width, format.maximum, nan, relu, saturate = true)(
      format.fromDouble(_, IEEE754.Rounding.TowardZero, saturate = true),
      format.toDouble(_)
    )

  private inline def stochastic(
      exact: Double,
      bits: Long,
      random: Int,
      width: Int in 1 ~ 32,
      maximum: Long,
      nan: Long,
      relu: Boolean,
      saturate: Boolean
  )(inline truncate: Double => Long, inline decode: Long => Double): Long = {
    val sign      = 1L << (width - 1)
    val negative  = exact < 0.0 || (exact == 0.0 && 1.0 / exact < 0.0)
    val magnitude = Math.abs(exact)
    if (exact.isNaN) nan
    else if (relu && negative) 0L
    else {
      // the step above the largest finite magnitude continues its binade, where the format would put infinity
      val lower = if (magnitude.isInfinite) decode(maximum) else decode(truncate(magnitude))
      val code  = if (magnitude.isInfinite) maximum else truncate(magnitude)
      val step  = if (code == maximum) lower - decode(maximum - 1L) else decode(code + 1L) - lower
      val carry = Math.scalb((bits & ((1L << random) - 1L)).toDouble, -random)
      val away  = magnitude >= lower + step || (magnitude > lower && (magnitude - lower) / step + carry >= 1.0)
      val found =
        if (!away) code
        else if (code != maximum) code + 1L
        else if (saturate) maximum
        else maximum + 1L
      (if (negative) sign else 0L) | found
    }
  }

  /** A scalar video operation: the selected parts of `left` and `right` extended to 33 bits, the operation's 34-bit
    * result clamped to the range of the destination part where `saturate`, then merged into the part of `addend` that
    * `merge` names or, writing the whole word, combined with `addend` by `secondary`. Shift amounts are unsigned and
    * wrap at 32 where `wrap` or clamp to 32 otherwise.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def video(
      left: UInt[32],
      right: UInt[32],
      addend: UInt[32],
      operation: VideoOperation,
      leftSelect: VideoSelect,
      rightSelect: VideoSelect,
      leftSigned: Boolean,
      rightSigned: Boolean,
      resultSigned: Boolean,
      saturate: Boolean,
      secondary: VideoSecondary,
      merge: VideoSelect,
      wrap: Boolean
  ): UInt[32] = {
    val shift = operation == VideoOperation.ShiftLeft || operation == VideoOperation.ShiftRight
    val a     = part(left.raw, leftSelect, leftSigned)
    val b     = part(right.raw, rightSelect, rightSigned && !shift)
    val count = if (wrap) b & 31L else Math.min(b, 32L)
    val value = operation match {
      case VideoOperation.Add                => a + b
      case VideoOperation.Subtract           => a - b
      case VideoOperation.Average            => (a + b + 1L) >> 1
      case VideoOperation.AbsoluteDifference => Math.abs(a - b)
      case VideoOperation.Minimum            => Math.min(a, b)
      case VideoOperation.Maximum            => Math.max(a, b)
      case VideoOperation.ShiftLeft          => a << count
      case VideoOperation.ShiftRight         => a >> count
      case VideoOperation.Equal              => if (a == b) 1L else 0L
      case VideoOperation.NotEqual           => if (a != b) 1L else 0L
      case VideoOperation.Less               => if (a < b) 1L else 0L
      case VideoOperation.LessEqual          => if (a <= b) 1L else 0L
      case VideoOperation.Greater            => if (a > b) 1L else 0L
      case VideoOperation.GreaterEqual       => if (a >= b) 1L else 0L
    }
    val intermediate = (value << 30) >> 30
    val bits         = merge.bits
    val clamped      =
      if (!saturate) intermediate
      else if (resultSigned) Math.min(Math.max(intermediate, -(1L << (bits - 1))), (1L << (bits - 1)) - 1L)
      else Math.min(Math.max(intermediate, 0L), (1L << bits) - 1L)
    val c = if (resultSigned) addend.raw.toInt.toLong else addend.raw
    UInt[32](
      if (merge != VideoSelect.Word) {
        val mask = ((1L << bits) - 1L) << merge.offset
        ((clamped << merge.offset) & mask) | (addend.raw & ~mask)
      } else
        secondary match {
          case VideoSecondary.Plain   => clamped
          case VideoSecondary.Add     => clamped + c
          case VideoSecondary.Minimum => Math.min(clamped, c)
          case VideoSecondary.Maximum => Math.max(clamped, c)
        }
    )
  }

  /** `vmad`: the product of the selected parts of `left` and `right`, negated where `negateProduct`, plus `addend`,
    * negated where `negateAddend`, and one where `plusOne`, over 128 bits, then shifted right by `shift` and, where
    * `saturate`, clamped to the 32-bit range of the result's signedness.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def videoMultiplyAdd(
      left: UInt[32],
      right: UInt[32],
      addend: UInt[32],
      leftSelect: VideoSelect,
      rightSelect: VideoSelect,
      leftSigned: Boolean,
      rightSigned: Boolean,
      negateProduct: Boolean,
      negateAddend: Boolean,
      addendSigned: Boolean,
      plusOne: Boolean,
      shift: Int in (0, 7, 15),
      saturate: Boolean
  ): UInt[32] = {
    val signed      = leftSigned || rightSigned || negateProduct || negateAddend
    val a           = part(left.raw, leftSelect, leftSigned)
    val b           = part(right.raw, rightSelect, rightSigned)
    val negated     = !plusOne && negateProduct
    val productLow  = if (negated) ~(a * b) else a * b
    val productHigh = if (negated) ~Math.multiplyHigh(a, b) else Math.multiplyHigh(a, b)
    val c           = if (!plusOne && !negateProduct && negateAddend) ~addend.raw & 0xffffffffL else addend.raw
    // the hardware adds the addend sign-extended where it says so
    val extended  = if (addendSigned || signed) c.toInt.toLong else c
    val carryIn   = if (plusOne || negateProduct || negateAddend) 1L else 0L
    val addedLow  = productLow + extended
    val addedHigh = productHigh + (extended >> 63) +
      (if (java.lang.Long.compareUnsigned(addedLow, productLow) < 0) 1L else 0L)
    val sumLow  = addedLow + carryIn
    val sumHigh = addedHigh + (if (carryIn == 1L && sumLow == 0L) 1L else 0L)
    val result  = if (shift == 0) sumLow else (sumLow >>> shift) | (sumHigh << (64 - shift))
    UInt[32](
      if (!saturate) result
      else if (signed) Math.min(Math.max(result, Int.MinValue.toLong), Int.MaxValue.toLong)
      else if (java.lang.Long.compareUnsigned(result, 0xffffffffL) > 0) 0xffffffffL
      else result
    )
  }

  private def part(word: Long, select: VideoSelect, signed: Boolean): Long = {
    val field = (word >>> select.offset) & ((1L << select.bits) - 1L)
    if (signed && (field >>> (select.bits - 1)) != 0L) field - (1L << select.bits) else field
  }

  /** Whether binary32 `value` is not negative for `slct`: NaN is, either zero is not, and a subnormal flushes to zero
    * where `flush`.
    */
  def nonNegative(value: Float32, flush: Boolean): Bool = {
    val bits      = value.bits.raw
    val magnitude = bits & 0x7fffffffL
    magnitude <= 0x7f800000L && ((bits & 0x80000000L) == 0L || magnitude == 0L || (flush && magnitude < 0x00800000L))
  }

  /** `testp`: whether binary32 `value` has `property`; zeros are normal. */
  def test(value: Float32, property: FloatProperty): Bool =
    has(value.bits.raw, fraction = 23, exponentBits = 8, property = property)

  @targetName("testDouble")
  def test(value: Float64, property: FloatProperty): Bool =
    has(value.bits.raw, fraction = 52, exponentBits = 11, property = property)

  private def has(
      bits: Long,
      fraction: Int in (23, 52),
      exponentBits: Int in (8, 11),
      property: FloatProperty
  ): Boolean = {
    val exponent = (bits >>> fraction) & ((1L << exponentBits) - 1L)
    val mantissa = bits & ((1L << fraction) - 1L)
    val special  = exponent == (1L << exponentBits) - 1L
    property match {
      case FloatProperty.Finite     => !special
      case FloatProperty.Infinite   => special && mantissa == 0L
      case FloatProperty.Number     => !special || mantissa == 0L
      case FloatProperty.NotANumber => special && mantissa != 0L
      case FloatProperty.Normal     => !special && (exponent != 0L || mantissa == 0L)
      case FloatProperty.Subnormal  => exponent == 0L && mantissa != 0L
    }
  }

  /** `value` when `condition` holds, otherwise 0: the result of `set` and of each half of its packed forms. */
  def truth(condition: Bool, value: Long): UInt[32] = UInt[32](if (condition) value else 0L)

  /** Whether the generic `address` falls in the window of `space`; global memory is every other address. */
  def within(address: UInt[64], space: AddressKind): Bool = space match {
    case AddressKind.Shared   => VMAddressSpace.isShared(address.raw)
    case AddressKind.Local    => VMAddressSpace.isLocal(address.raw)
    case AddressKind.Constant => VMAddressSpace.isConstant(address.raw)
    case AddressKind.Global   =>
      !VMAddressSpace.isShared(address.raw) && !VMAddressSpace.isLocal(address.raw) &&
      !VMAddressSpace.isConstant(address.raw)
  }

  /** The low 64 bits of the carry-less product of `left` and `right`, as `clmad.lo` takes it. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def carrylessLow(left: UInt[64], right: UInt[64]): UInt[64] = {
    var product = 0L
    var bit     = 0
    while (bit < 64) {
      if (((left.raw >>> bit) & 1L) != 0L) product ^= right.raw << bit
      bit += 1
    }
    UInt[64](product)
  }

  /** The high 64 bits of the carry-less product of `left` and `right`, as `clmad.hi` takes it. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def carrylessHigh(left: UInt[64], right: UInt[64]): UInt[64] = {
    var product = 0L
    var bit     = 1
    while (bit < 64) {
      if (((left.raw >>> bit) & 1L) != 0L) product ^= right.raw >>> (64 - bit)
      bit += 1
    }
    UInt[64](product)
  }

  private def minifloat(format: NarrowFormat): Option[Minifloat] = format match {
    case NarrowFormat.E4M3  => Some(Minifloat.E4M3)
    case NarrowFormat.E5M2  => Some(Minifloat.E5M2)
    case NarrowFormat.E2M3  => Some(Minifloat.E2M3)
    case NarrowFormat.E3M2  => Some(Minifloat.E3M2)
    case NarrowFormat.E2M1  => Some(Minifloat.E2M1)
    case NarrowFormat.UE8M0 => Some(Minifloat.E8M0)
    case NarrowFormat.S2F6  => None
  }

  private def scaleOf(byte: Long): Double = Minifloat.E8M0.toDouble(byte & 0xffL)

  /** `value` in `format`: NaN is the format's NaN or, for formats without one, its largest positive value; a negative
    * value is zero where `relu`, except in the unsigned UE8M0, which encodes the rounded magnitude; beyond the largest
    * finite magnitude it saturates where `saturate`.
    */
  private def narrowed(
      value: Double,
      format: NarrowFormat,
      rounding: IEEE754.Rounding,
      relu: Boolean,
      saturate: Boolean
  )
      : Long = {
    val negative = value < 0.0 || (value == 0.0 && 1.0 / value < 0.0)
    minifloat(format) match {
      case Some(target) =>
        if (value.isNaN) target.nan
        else if (format == NarrowFormat.UE8M0) target.fromDouble(Math.abs(value), rounding, saturate)
        else if (relu && negative) 0L
        else target.fromDouble(value, rounding, saturate)
      case None =>
        if (value.isNaN) 0x7fL
        else if (relu && negative) 0L
        else {
          val fixed = IEEE754.integral(Math.scalb(value, 6), rounding)
          Math.max(-128.0, Math.min(127.0, fixed)).toLong & 0xffL
        }
    }
  }

  private def packed(format: NarrowFormat, high: Long, low: Long): UInt[16] =
    UInt[16](if (format == NarrowFormat.E2M1) (high << 4) | low else (high << 8) | low)

  /** `cvt` of binary32 `high` and `low` to a pair of `format` values, S2F6 ones divided by their `scale` byte. */
  def narrowFloats(
      high: Float32,
      low: Float32,
      scale: UInt[16],
      format: NarrowFormat,
      mode: ConversionRounding,
      relu: Boolean,
      saturate: Boolean
  ): UInt[16] = {
    def divided(value: Double, byte: Long) = if (format == NarrowFormat.S2F6) value / scaleOf(byte) else value
    packed(
      format,
      narrowed(divided(high.toDouble, scale.raw >>> 8), format, VMArithmetic.roundingMode(mode), relu, saturate),
      narrowed(divided(low.toDouble, scale.raw), format, VMArithmetic.roundingMode(mode), relu, saturate)
    )
  }

  /** `cvt` of the binary16 or, where `bf16`, bfloat16 halves of `pair` to a pair of `format` values. */
  def narrowHalves(
      pair: UInt[32],
      scale: UInt[16],
      format: NarrowFormat,
      bf16: Boolean,
      mode: ConversionRounding,
      relu: Boolean,
      saturate: Boolean
  ): UInt[16] = {
    def value(half: Long, byte: Long) = {
      val exact = if (bf16) Minifloat.BFloat16.toDouble(half & 0xffffL) else IEEE754.toDouble(half & 0xffffL, 16)
      if (format == NarrowFormat.S2F6) exact / scaleOf(byte) else exact
    }
    packed(
      format,
      narrowed(value(pair.raw >>> 16, scale.raw >>> 8), format, VMArithmetic.roundingMode(mode), relu, saturate),
      narrowed(value(pair.raw, scale.raw), format, VMArithmetic.roundingMode(mode), relu, saturate)
    )
  }

  /** `cvt` of a pair of `format` values to binary16 or, where `bf16`, bfloat16 halves, each multiplied by its `scale`
    * byte.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def widen(
      pair: UInt[16],
      scale: UInt[16],
      format: NarrowFormat,
      bf16: Boolean,
      relu: Boolean,
      saturate: Boolean
  ): UInt[32] = {
    val (high, low) = if (format == NarrowFormat.E2M1) (pair.raw >>> 4 & 0xfL, pair.raw & 0xfL)
    else (pair.raw >>> 8 & 0xffL, pair.raw & 0xffL)
    def value(code: Long, byte: Long) = {
      val exact  = minifloat(format).fold(code.toByte.toDouble / 64.0)(_.toDouble(code))
      val scaled = exact * scaleOf(byte)
      if (relu && (scaled < 0.0 || (scaled == 0.0 && 1.0 / scaled < 0.0))) 0.0 else scaled
    }
    def encoded(value: Double) =
      if (value.isNaN) if (bf16) 0x7fffL else VMArithmetic.CanonicalNaN16
      else if (bf16) Minifloat.BFloat16.fromDouble(value, saturate = saturate)
      else IEEE754.fromDouble(value, 16)
    UInt[32]((encoded(value(high, scale.raw >>> 8)) << 16) | encoded(value(low, scale.raw)))
  }

  /** `fns`: the position of the `offset`th set bit of `mask` from `base`, counting up for a positive offset and down
    * for a negative one, or all ones where there is none.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def findNthSet(mask: UInt[32], base: UInt[32], offset: UInt[32]): UInt[32] = {
    val bits  = mask.raw
    val steps = offset.raw.toInt.toLong
    val step  = if (steps >= 0L) 1 else -1
    if (steps == 0L) UInt[32](if (((bits >>> (base.raw & 31L)) & 1L) != 0L) base.raw else 0xffffffffL)
    else {
      var position = base.raw.toInt
      var count    = math.abs(steps) - 1L
      var found    = 0xffffffffL
      while (found == 0xffffffffL && position >= 0 && position < 32) {
        if (((bits >>> position) & 1L) != 0L) {
          if (count == 0L) found = position.toLong else count -= 1L
        }
        position += step
      }
      UInt[32](found)
    }
  }
}
