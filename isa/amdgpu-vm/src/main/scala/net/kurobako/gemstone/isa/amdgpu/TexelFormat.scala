package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** The data formats each generation encodes in resource descriptors and typed instructions, and the conversions between
  * a texel's channels and the 32- or 16-bit values registers hold.
  *
  * Conversions follow RDNA2 hardware: float channels narrow to binary16 and the small floats toward zero, saturating
  * finite overflow, normalised channels narrow to binary16 to nearest, normalised stores round half away from zero,
  * scaled stores convert as their normalised counterparts, integer stores saturate, and 16-bit integer results saturate
  * wider channels.
  */
private[isa] object TexelFormat {
  import TexelLayout.*
  import TexelNumber.*

  val Invalid: TexelFormat = TexelFormat(TexelLayout.Invalid, Unorm)

  /** The destination select of `component`: 0, 1, or 4 to 7 for the x to w channels. */
  inline def select(word3: Long, component: Int): Int = ((word3 >>> (3 * component)) & 7L).toInt

  /** GFX6-GFX9 data formats by their 4-bit DATA_FORMAT (buffers) or the low values of the 6-bit image field. */
  private val dataLayouts: Vec[TexelLayout] = Vec(
    TexelLayout.Invalid,
    X8,
    X16,
    X8Y8,
    X32,
    X16Y16,
    Z10Y11X11,
    Z11Y11X10,
    W10Z10Y10X2,
    W2Z10Y10X10,
    X8Y8Z8W8,
    X32Y32,
    X16Y16Z16W16,
    X32Y32Z32,
    X32Y32Z32W32,
    TexelLayout.Invalid,
    Z5Y6X5,
    W1Z5Y5X5,
    W5Z5Y5X1,
    W4Z4Y4X4
  )

  private val numbers: Vec[Option[TexelNumber]] =
    Vec(
      Some(Unorm),
      Some(Snorm),
      Some(Uscaled),
      Some(Sscaled),
      Some(Uint),
      Some(Sint),
      None,
      Some(Float),
      None,
      Some(Srgb)
    )

  /** A GFX8 or GFX9 data and numeric format pair. */
  def legacy(data: Int, numeric: Int): TexelFormat = (for {
    layout <- dataLayouts.lift(data)
    number <- numbers.lift(numeric).flatten
  } yield TexelFormat(layout, number)).getOrElse(Invalid)

  private def entries(layout: TexelLayout, kinds: TexelNumber*): Vec[TexelFormat] =
    Vec.from(kinds).map(TexelFormat(layout, _))

  private val integerKinds = Vec(Unorm, Snorm, Uscaled, Sscaled, Uint, Sint)

  private val unifiedPrefix: Vec[TexelFormat] = Vec(Invalid) ++
    entries(X8, integerKinds*) ++ entries(X16, (integerKinds :+ Float)*) ++ entries(X8Y8, integerKinds*) ++
    entries(X32, Uint, Sint, Float) ++ entries(X16Y16, (integerKinds :+ Float)*)

  /** GFX10 unified formats 0-77, which buffers and images share. */
  private val unifiedGFX10: Vec[TexelFormat] = unifiedPrefix ++
    entries(Z10Y11X11, (integerKinds :+ Float)*) ++ entries(Z11Y11X10, (integerKinds :+ Float)*) ++
    entries(W10Z10Y10X2, integerKinds*) ++ entries(W2Z10Y10X10, integerKinds*) ++ entries(X8Y8Z8W8, integerKinds*) ++
    entries(X32Y32, Uint, Sint, Float) ++ entries(X16Y16Z16W16, (integerKinds :+ Float)*) ++
    entries(X32Y32Z32, Uint, Sint, Float) ++ entries(X32Y32Z32W32, Uint, Sint, Float)

  /** GFX11 and GFX12 unified formats 0-63, without the small integer float layouts and two scaled 10-bit kinds. */
  private val unifiedGFX11: Vec[TexelFormat] = unifiedPrefix ++ entries(Z10Y11X11, Float) ++
    entries(Z11Y11X10, Float) ++ entries(W10Z10Y10X2, Unorm, Snorm, Uint, Sint) ++
    entries(W2Z10Y10X10, integerKinds*) ++ entries(X8Y8Z8W8, integerKinds*) ++ entries(X32Y32, Uint, Sint, Float) ++
    entries(X16Y16Z16W16, (integerKinds :+ Float)*) ++ entries(X32Y32Z32, Uint, Sint, Float) ++
    entries(X32Y32Z32W32, Uint, Sint, Float)

  /** Image-only GFX10 formats from 128, and GFX11 ones from 64, that compute images use. */
  private val imageGFX10: Map[Int, TexelFormat] = Map(
    128 -> TexelFormat(X8, Srgb),
    129 -> TexelFormat(X8Y8, Srgb),
    130 -> TexelFormat(X8Y8Z8W8, Srgb),
    133 -> TexelFormat(Z5Y6X5, Unorm),
    134 -> TexelFormat(W1Z5Y5X5, Unorm),
    135 -> TexelFormat(W5Z5Y5X1, Unorm),
    136 -> TexelFormat(W4Z4Y4X4, Unorm),
    137 -> TexelFormat(Y4X4, Unorm),
    140 -> TexelFormat(X32, Float)
  )

  private val imageGFX11: Map[Int, TexelFormat] = Map(
    64 -> TexelFormat(X8, Srgb),
    65 -> TexelFormat(X8Y8, Srgb),
    66 -> TexelFormat(X8Y8Z8W8, Srgb),
    68 -> TexelFormat(Z5Y6X5, Unorm),
    69 -> TexelFormat(W1Z5Y5X5, Unorm),
    70 -> TexelFormat(W5Z5Y5X1, Unorm),
    71 -> TexelFormat(W4Z4Y4X4, Unorm),
    72 -> TexelFormat(Y4X4, Unorm),
    75 -> TexelFormat(X32, Float)
  )

  /** A unified format as `generation` numbers it, in a buffer or image descriptor or a typed instruction. */
  def unified(generation: ResourceGeneration, code: Int): TexelFormat = generation match {
    case ResourceGeneration.GFX10 => unifiedGFX10.lift(code).orElse(imageGFX10.get(code)).getOrElse(Invalid)
    case _                        => unifiedGFX11.lift(code).orElse(imageGFX11.get(code)).getOrElse(Invalid)
  }

  /** The format a typed buffer instruction's FORMAT, or DFMT and NFMT, fields name. */
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def instruction(generation: ResourceGeneration, format: Int, data: Int, numeric: Int): TexelFormat =
    generation match {
      case ResourceGeneration.GFX8 | ResourceGeneration.GFX9 => legacy(data, numeric)
      case _                                                 => unified(generation, format)
    }

  private def maximum(width: Int): Long = (1L << width) - 1L

  private def signedMaximum(width: Int): Long = (1L << (width - 1)) - 1L

  private def float(value: Float): Long = IEEE754.floatToBinary32(value).toLong

  private def single(bits: Long): Float = IEEE754.binary32ToFloat(Bits.low32(bits))

  private def smallFloat(fraction: Int in (5, 6)): FloatFormat =
    if (fraction == 6) Minifloat.E5M6 else Minifloat.E5M5

  /** `value` truncated to an unsigned float with `fraction` fraction bits; negatives become zero, finite overflow the
    * largest finite value, and infinity infinity.
    */
  private def toSmallFloat(value: Double, fraction: Int in (5, 6)): Long =
    smallFloat(fraction).fromDouble(
      value,
      if (value.isInfinite) IEEE754.Rounding.NearestEven else IEEE754.Rounding.TowardZero
    )

  /** Channel `bits`, `width` wide, as the 32-bit value a register receives. */
  def read(number: TexelNumber, width: Int in 1 ~ 32, bits: Long): Long =
    number match {
      case Unorm   => float(Normalised.decodeUnsigned(bits, width).toFloat)
      case Snorm   => float(Normalised.decodeSigned(bits, width.unsafe[Int in 2 ~ 32]).toFloat)
      case Uscaled => float(bits.toFloat)
      case Sscaled => float(Bits.signExtend(bits, width).toFloat)
      case Uint    => Bits.low32(bits).toLong
      case Sint    => Bits.low32(Bits.signExtend(bits, width)).toLong
      case Srgb    => float(SrgbTransfer.toLinear(Normalised.decodeUnsigned(bits, width)).toFloat)
      case Float   =>
        width.raw match {
          case 32 => bits
          case 16 => float(IEEE754.binary16ToFloat(Bits.low16(bits)))
          case 11 => float(Minifloat.E5M6.toDouble(bits).toFloat)
          case 10 => float(Minifloat.E5M5.toDouble(bits).toFloat)
          case _  => 0L
        }
    }

  /** A 32-bit read narrowed to the 16 bits a D16 register half receives. */
  def narrow(number: TexelNumber, value: Long): Long = number match {
    case Uint  => Bits.low32(value).toLong.min(0xffffL)
    case Sint  => Bits.low16(Bits.signed32(value).toLong.max(-0x8000L).min(0x7fffL)).toLong
    case Float => IEEE754.fromDouble(single(value).toDouble, 16, IEEE754.Rounding.TowardZero)
    case _     => IEEE754.fromDouble(single(value).toDouble, 16)
  }

  /** A 16-bit D16 register half widened to the 32-bit value a store converts. */
  def widen(number: TexelNumber, value: Long): Long = number match {
    case Uint => Bits.low16(value).toLong
    case Sint => Bits.low32(Bits.signExtend(value, 16)).toLong
    case _    => float(IEEE754.binary16ToFloat(Bits.low16(value)))
  }

  /** The `width` channel bits a store writes for a register's 32-bit `value`. */
  def write(number: TexelNumber, width: Int in 1 ~ 32, value: Long): Long =
    number match {
      case Unorm | Uscaled => Normalised.encodeUnsigned(single(value).toDouble, width)
      case Snorm | Sscaled => Normalised.encodeSigned(single(value).toDouble, width.unsafe[Int in 2 ~ 32])
      case Srgb            =>
        val linear = single(value)
        if (linear.isNaN) 0L
        else
          Normalised.encodeUnsigned(
            SrgbTransfer.fromLinear(linear.toDouble.max(0.0).min(1.0)).toFloat.toDouble,
            width
          )
      case Uint => Bits.low32(value).toLong.min(maximum(width))
      case Sint =>
        Bits.truncate(Bits.signed32(value).toLong.max(-signedMaximum(width) - 1L).min(signedMaximum(width)), width)
      case Float =>
        width.raw match {
          case 32 => Bits.low32(value).toLong
          case 16 => IEEE754.fromDouble(single(value).toDouble, 16, IEEE754.Rounding.TowardZero)
          case 11 => toSmallFloat(single(value).toDouble, 6)
          case 10 => toSmallFloat(single(value).toDouble, 5)
          case _  => 0L
        }
    }

  /** The constant a SEL_1 destination select reads: integral one or 1.0. */
  def one(format: TexelFormat, d16: Boolean): Long =
    if (format.integral) 1L else if (d16) 0x3c00L else 0x3f800000L

  /** What destination select `select` reads of the texel `low` and `high` hold: SEL_1 one, a channel select its channel
    * converted, as the 32-bit value a register receives or the 16 bits a D16 half does when `half`, and other selects,
    * and every channel of a format execution does not convert, zero. A texel outside its resource reads as zero bits.
    */
  def component(format: TexelFormat, select: Int, low: Long, high: Long, half: Boolean): Long =
    if (select == 1) one(format, half)
    else if (select < 4 || !format.valid) 0L
    else {
      val layout  = format.layout
      val index   = (select - 4) % layout.channels
      val element = read(format.number, layout.widths(index), channel(layout, index, low, high))
      if (half) narrow(format.number, element) else element
    }

  /** Bits `part` * 64 onwards of the texel a store writes for the 32-bit `components` x to w: each channel takes the
    * component whose select names it, converted when `converted`, or else what its own select reads.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def packed(format: TexelFormat, selects: Long, components: Array[Long], converted: Boolean, part: Int): Long = {
    val layout = format.layout
    var low    = 0L
    var high   = 0L
    var index  = 0
    while (index < layout.channels) {
      var source = 0
      while (source < 4 && select(selects, source) != 4 + index) source += 1
      val chosen = select(selects, index)
      val value  =
        if (source < 4) components(source)
        else if (chosen == 1) one(format, d16 = false)
        else 0L
      val width = layout.widths(index)
      val bits  = if (converted) write(format.number, width, value) else value & ((1L << width) - 1L)
      val shift = layout.offsets(index)
      low |= VMArithmetic.placedLow(bits, shift)
      high |= VMArithmetic.placedHigh(bits, shift, width)
      index += 1
    }
    if (part == 0) low else high
  }

  /** [[component]] of a format an instruction names. */
  def converted(
      layout: TexelLayout,
      number: TexelNumber,
      select: UInt[32],
      low: UInt[64],
      high: UInt[64],
      half: Boolean
  ): UInt[32] = fromRaw[UInt[32]](
    component(
      TexelFormat(layout, number),
      toRaw[UInt[32]](select).toInt,
      toRaw[UInt[64]](low),
      toRaw[UInt[64]](high),
      half
    )
  )

  /** [[packed]] for a format an instruction names, through the destination selects dword 3 of a buffer descriptor
    * holds.
    */
  def pack(
      layout: TexelLayout,
      number: TexelNumber,
      word3: UInt[32],
      x: UInt[32],
      y: UInt[32],
      z: UInt[32],
      w: UInt[32],
      part: Int
  ): UInt[64] = fromRaw[UInt[64]](
    packed(
      TexelFormat(layout, number),
      toRaw[UInt[32]](word3),
      Array(
        toRaw[UInt[32]](x),
        toRaw[UInt[32]](y),
        toRaw[UInt[32]](z),
        toRaw[UInt[32]](w)
      ),
      converted = true,
      part
    )
  )

  /** [[widen]] of a 16-bit register half. */
  def widened(number: TexelNumber, value: UInt[16]): UInt[32] =
    fromRaw[UInt[32]](widen(number, toRaw[UInt[16]](value)))

  /** Channel `channel` of the `layout.bits` texel `texel` held little-endian in `low` and `high`. */
  def channel(layout: TexelLayout, channel: Int, low: Long, high: Long): Long = {
    val offset = layout.offsets(channel)
    val width  = layout.widths(channel)
    val word   = if (offset >= 64) high >>> (offset - 64) else low >>> offset
    word & maximum(width)
  }
}

/** A buffer or image data format: its layout and how each channel reads. */
private[isa] case class TexelFormat(layout: TexelLayout, number: TexelNumber) {
  def valid: Boolean = layout != TexelLayout.Invalid && layout != TexelLayout.SharedExponent

  def integral: Boolean = number == TexelNumber.Uint || number == TexelNumber.Sint
}
