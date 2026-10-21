package net.kurobako.gemstone.isa.amdgpu

/** The transcendental unit's reciprocal, reciprocal square root, square root, power of two, logarithm, sine and cosine,
  * as the hardware rounds them. This project recovered the datapath and its tables from hardware measurements.
  *
  * The leading 5 bits of the significand's fraction, 4 for the square root, select a segment, whose polynomial
  * `c0 + c1 * xl + xl^2 * (c2 + c3 * xl)` the unit evaluates on the next 24 bits `xl` through truncated products:
  *
  *   - `L = floor(round(c1 * xl, lsb) / 2^shift)`;
  *   - `F = c2 + rne(c3 * ((xl >> 4) + 1) / 2^12)` and `q = ((xl >> 7)^2) >> 10`;
  *   - `V = step * (c0 + L) + round(q * F, 23) / 2^23`,
  *
  * where `round(x, lsb)` rounds to nearest even at 24 significant bits but no finer than `2^lsb`. `V` holds the
  * result's leading 35 bits, or 36 for the root tables, whose second half serves the odd exponents with the operand
  * scaled into the binade above.
  *
  * Binary32 results round `V` less an eighth of their ulp to nearest even, except in the segment next to the result
  * binade's top, and read and write denormals as zeros. Binary64 results take `V` as their leading bits and truncate
  * denormal results. Where `V` reaches a power of two for an operand that is no power of two, the result steps one ulp
  * back toward the exact one, as the reciprocal of a significand of all ones does.
  *
  * The power of two truncates the operand's magnitude to 29 fraction bits, reading magnitudes under `2^-24` as zero. A
  * negative operand takes the one's complement of that fraction as its fraction above the next integer down. The
  * segment polynomial takes `L = floor(round(c1 * xl, 24) / 2^25)`, `F = rne(c2 * 2^8 + c3 * s, 8)` with `s = xl >> 6`
  * and `q = s^2 >> 12`, and `step = 256`, giving `V` in units of `2^-36`, which rounds to nearest even at 24 bits.
  * Results outside the normal range give infinity or +0 whatever MODE says.
  *
  * The base-2 logarithm splits the fraction into a 5-bit segment and an 18-bit offset `j`, and adds to the exponent
  * `B = c0 + 256 * floor(c1 * j / 2^19) + trunc(q * F, 25) / 2^20` in units of `2^-38`, with `q = j^2 >> 12` and
  * `F = floor((c2 + c3 * j) / 2^23)`, where `trunc(x, 25)` truncates the magnitude to 25 significant bits. The sum
  * rounds toward -infinity at 24 bits. The unit evaluates operands within `2^-5` above and `2^-6` below one on a
  * relative-precision path, which rounds correctly here. The datapath matches the hardware on 99.997% of all binary32
  * operands, every mismatch inside those two windows.
  *
  * The sine and cosine take their operand in turns. The fraction of the magnitude reduces by symmetry to a quarter
  * turn, whose segments of `2^-7` turns evaluate cubics fitted to the hardware's results in binary64, the quarter's
  * upper half counting back from the half turn, and truncate at 24 bits. Segment starts take the hardware's values. The
  * sine of a magnitude under `3 * 2^-13` is the operand times `0x1921faa * 2^-22` rounded to nearest even. The fitted
  * cubics stay within 0.3 ulp of the hardware's results, but for their resolution of `2^-40` next to the zero at the
  * half turn, matching them on 99.25% of all binary32 operands for the sine and 99.12% for the cosine; the binary16
  * forms built on them match on every operand.
  */
private[isa] object TranscendentalUnit {

  private val Infinity32   = 0x7f800000L
  private val DefaultNaN32 = 0xffc00000L
  private val Infinity64   = 0x7ff0000000000000L
  private val DefaultNaN64 = 0xfff8000000000000L
  private val Fraction64   = (1L << 52) - 1L

  private val SmallSineSlope = 0x1921faa / 4194304.0

  private inline def rne(value: Long, shift: Int): Long = {
    val quotient = value >> shift
    val rest     = value - (quotient << shift)
    val half     = 1L << (shift - 1)
    if (rest > half || (rest == half && (quotient & 1L) != 0L)) quotient + 1L else quotient
  }

  /** `value` rounded to nearest even at 24 significant bits, or at `2^lsb` where that is coarser. */
  private inline def rounded(value: Long, lsb: Int): Long = {
    val magnitude = math.abs(value)
    val at        = math.max(40 - java.lang.Long.numberOfLeadingZeros(magnitude), lsb)
    val result    = rne(magnitude, at) << at
    if (value < 0L) -result else result
  }

  /** `V` of the polynomial at `row`, whose linear term drops the `xl` bits of `ignored`. */
  private inline def evaluate(table: Array[Long], row: Int, xl: Long, step: Long, lsb: Int, shift: Int, ignored: Long)
      : Long = {
    val at        = row * 4
    val linear    = rounded(table(at + 1) * (xl & ~ignored), lsb) >> shift
    val s         = xl >> 7
    val q         = (s * s) >> 10
    val f         = table(at + 2) + rne(table(at + 3) * ((xl >> 4) + 1L), 12)
    val quadratic = rounded(q * f, 23) >> 23
    step * (table(at) + linear) + quadratic
  }

  private inline def reciprocalCore(segment: Int, xl: Long): Long =
    evaluate(reciprocalTable, segment, xl, step = 64L, lsb = 20, shift = 21, ignored = 0L)
  private inline def reciprocalSquareRootCore(odd: Int, segment: Int, xl: Long): Long = evaluate(
    reciprocalSquareRootTable,
    odd * 32 + segment,
    xl,
    step = 128L,
    lsb = 20,
    shift = 22,
    ignored = if (segment == 31) 0x30L else 0L
  )
  private inline def squareRootCore(odd: Int, segment: Int, xl: Long): Long =
    evaluate(squareRootTable, odd * 16 + segment, xl, step = 128L, lsb = 21, shift = 22, ignored = 0L)

  private inline def powerOfTwoCore(segment: Int, xl: Long): Long = {
    val at        = segment * 4
    val linear    = rounded(powerOfTwoTable(at + 1) * xl, 24) >> 25
    val s         = xl >> 6
    val q         = (s * s) >> 12
    val f         = rne((powerOfTwoTable(at + 2) << 8) + powerOfTwoTable(at + 3) * s, 8)
    val quadratic = rounded(q * f, 23) >> 23
    256L * (powerOfTwoTable(at) + linear) + quadratic
  }

  private inline def bitLength(value: Long): Int = 64 - java.lang.Long.numberOfLeadingZeros(value)

  private inline def logarithmCore(segment: Int, j: Long): Long = {
    val at        = if (segment == 1 && j >= (1L << 17)) 128 else segment * 4
    val f         = (logarithmTable(at + 2) + logarithmTable(at + 3) * j) >> 23
    val p         = ((j * j) >> 12) * f
    val magnitude = math.abs(p)
    val lsb       = math.max(bitLength(magnitude) - 25, 0)
    val quadratic = ((magnitude >> lsb) << lsb) >> 20
    logarithmTable(at) + 256L * ((logarithmTable(at + 1) * j) >> 19) + (if (p < 0L) -quadratic else quadratic)
  }

  /** The binary32 bits of `value * 2^-38` rounded toward -infinity. */
  private inline def floored(value: Long): Long = {
    val magnitude   = math.abs(value)
    val length      = bitLength(magnitude)
    val shift       = math.max(length - 24, 0)
    val truncated   = if (shift > 0) magnitude >> shift else magnitude << (24 - length)
    val significand = if (value < 0L && (magnitude & ((1L << shift) - 1L)) != 0L) truncated + 1L else truncated
    val carry       = (significand >> 24).toInt
    val sign        = if (value < 0L) 0x80000000L else 0L
    if (magnitude == 0L) 0L
    else sign | ((length - 39 + 127 + carry).toLong << 23) | ((significand >> carry) & 0x7fffffL)
  }

  /** The binary32 bits of the magnitude of `value * 2^-40`, truncated. */
  private inline def truncated(value: Long): Long = {
    val magnitude = math.abs(value)
    val length    = bitLength(magnitude)
    val m         = if (length > 24) magnitude >> (length - 24) else magnitude << (24 - length)
    if (magnitude == 0L) 0L else ((length - 41 + 127).toLong << 23) | (m & 0x7fffffL)
  }

  private inline def cubic(table: Array[Double], row: Int, offset: Long, full: Long): Long = {
    val a  = offset.toDouble / full.toDouble
    val at = row * 4
    truncated(math.floor(table(at) + a * (table(at + 1) + a * (table(at + 2) + a * table(at + 3)))).toLong)
  }

  /** The sine of `u` turns in [0, 2^-7): the offset times a quadratic in it, one per binade of the offset. */
  private def firstSineSegment(u: Double): Long = {
    val a      = math.max(math.floor(u * 2147483648.0).toLong, 1L)
    val length = bitLength(a)
    if (length < 7) 0L
    else {
      val at = (length - 7) * 3
      val x  = a.toDouble / 16777216.0
      val p  = firstSineTable(at) + x * (firstSineTable(at + 1) + x * firstSineTable(at + 2))
      truncated(math.floor(p * a.toDouble * 512.0).toLong)
    }
  }

  /** The sine of `u` turns in [2^-7, 1/4), the offset rounded half up into the segment. */
  private def risingSine(u: Double): Long = {
    val segment = math.floor(u * 128.0).toInt
    val scale   = if (segment == 1) 22 else 20
    val offset  = math.floor(u * (1L << (scale + 7)).toDouble + 0.5).toLong - (segment.toLong << scale)
    if (offset >= (1L << scale)) sineStarts(segment + 1)
    else if (offset == 0L) sineStarts(segment)
    else cubic(sineTable, segment, offset, 1L << scale)
  }

  /** The sine of `r` turns in [1/4, 1/2), from the segment counted back from the half turn. */
  private def fallingSine(r: Double): Long = {
    val segment = math.floor(r * 128.0).toInt
    val offset  = math.floor((r - segment / 128.0) * 134217728.0 + 0.5).toLong
    val mirror  = 63 - segment
    val back    = (1L << 20) - offset
    if (offset == 0L) fallingSineStarts(segment - 32)
    else if (mirror == 0) cubic(sineTable, 0, back * 16L, 1L << 24)
    else if (mirror == 1) cubic(sineTable, 1, back * 4L, 1L << 22)
    else if (mirror < 4) cubic(sineTable, mirror, back, 1L << 20)
    else cubic(fallingSineTable, mirror - 4, back, 1L << 20)
  }

  /** The binary32 bits of the sine of `t` turns in [0, 1). */
  private def sineCore(t: Double): Long = {
    val flip   = t >= 0.5
    val r      = if (flip) t - 0.5 else t
    val result = if (r < 0.0078125) firstSineSegment(r) else if (r >= 0.25) fallingSine(r) else risingSine(r)
    if (flip && result != 0L) result | 0x80000000L else result
  }

  /** The fraction of a finite binary32 magnitude. */
  private inline def turns(magnitude: Long): Double = {
    val x = java.lang.Float.intBitsToFloat(magnitude.toInt).toDouble
    x - math.floor(x)
  }

  /** The fraction of a binary64 operand, a denormal's normalised to an implicit leading one. */
  private inline def normalFraction(exponent: Int, fraction: Long): Long =
    if (exponent != 0) fraction else (fraction << (java.lang.Long.numberOfLeadingZeros(fraction) - 11)) & Fraction64
  private inline def normalExponent(exponent: Int, fraction: Long): Int =
    if (exponent != 0) exponent else 12 - java.lang.Long.numberOfLeadingZeros(fraction)

  /** The binary64 magnitude of the significand `wide`, in [2^52, 2^53), at the biased exponent; denormals truncate. */
  private inline def pack(biased: Int, wide: Long): Long =
    if (biased >= 0x7ff) Infinity64
    else if (biased > 0) (biased.toLong << 52) | (wide & Fraction64)
    else if (biased > -63) wide >>> (1 - biased)
    else 0L

  def reciprocal32(bits: Long): Long = {
    val sign     = bits & 0x80000000L
    val exponent = ((bits >> 23) & 0xffL).toInt
    val fraction = bits & 0x7fffffL
    if (exponent == 0) sign | Infinity32
    else if (exponent == 0xff) if (fraction == 0L) sign else bits | 0x400000L
    else {
      val segment = (fraction >> 18).toInt
      val v       = reciprocalCore(segment, (fraction & 0x3ffffL) << 6)
      val r       = rne(v - (if (segment == 0) 0L else 256L), 11)
      val biased  = 253L - exponent + (r >> 24)
      if (biased <= 0L) sign else sign | (biased << 23) | (r & 0x7fffffL)
    }
  }

  def reciprocalSquareRoot32(bits: Long): Long = {
    val sign     = bits & 0x80000000L
    val exponent = ((bits >> 23) & 0xffL).toInt
    val fraction = bits & 0x7fffffL
    if (exponent == 0) sign | Infinity32
    else if (exponent == 0xff) if (fraction != 0L) bits | 0x400000L else if (sign != 0L) DefaultNaN32 else 0L
    else if (sign != 0L) DefaultNaN32
    else {
      val k       = exponent - 127
      val odd     = k & 1
      val segment = (fraction >> 18).toInt
      val v       = reciprocalSquareRootCore(odd, segment, (fraction & 0x3ffffL) << 6)
      val r       = rne(v - (if (segment == 0) 0L else 512L), 12)
      val biased  = 126L - ((k - odd) >> 1) + (r >> 24)
      (biased << 23) | (r & 0x7fffffL)
    }
  }

  def squareRoot32(bits: Long): Long = {
    val sign     = bits & 0x80000000L
    val exponent = ((bits >> 23) & 0xffL).toInt
    val fraction = bits & 0x7fffffL
    if (exponent == 0) sign
    else if (exponent == 0xff) if (fraction != 0L) bits | 0x400000L else if (sign != 0L) DefaultNaN32 else Infinity32
    else if (sign != 0L) DefaultNaN32
    else {
      val k       = exponent - 127
      val odd     = k & 1
      val segment = (fraction >> 19).toInt
      val v       = squareRootCore(odd, segment, (fraction & 0x7ffffL) << 5)
      val r       = rne(v - (if (segment == 15) 0L else 512L), 12)
      val biased  = 127L + ((k - odd) >> 1) + (r >> 24)
      (biased << 23) | (r & 0x7fffffL)
    }
  }

  def powerOfTwo32(bits: Long): Long = {
    val negative = (bits & 0x80000000L) != 0L
    val exponent = ((bits >> 23) & 0xffL).toInt
    val fraction = bits & 0x7fffffL
    if (exponent == 0xff) if (fraction != 0L) bits | 0x400000L else if (negative) 0L else Infinity32
    else {
      val significand = if (exponent < 103) 0L else fraction | 0x800000L
      val shift       = math.min(exponent, 134) - 121
      val fixed       = if (shift >= 0) significand << shift else significand >> -shift
      val f           = (if (negative) ~fixed else fixed) & 0x1fffffffL
      val whole       = if (negative) -(fixed >> 29) - 1L else fixed >> 29
      val r           = rne(powerOfTwoCore((f >> 24).toInt, f & 0xffffffL), 13)
      val biased      = 127L + whole + (r >> 24)
      if (biased >= 0xffL) Infinity32 else if (biased <= 0L) 0L else (biased << 23) | (r & 0x7fffffL)
    }
  }

  def logarithm32(bits: Long): Long = {
    val negative = (bits & 0x80000000L) != 0L
    val exponent = ((bits >> 23) & 0xffL).toInt
    val fraction = bits & 0x7fffffL
    val segment  = (fraction >> 18).toInt
    if (exponent == 0xff) if (fraction != 0L) bits | 0x400000L else if (negative) DefaultNaN32 else Infinity32
    else if (exponent == 0) 0xff800000L
    else if (negative) DefaultNaN32
    else if ((exponent == 127 && segment == 0) || (exponent == 126 && segment == 31)) {
      val x = java.lang.Float.intBitsToFloat(bits.toInt).toDouble
      java.lang.Float.floatToRawIntBits((math.log(x) / math.log(2.0)).toFloat).toLong & 0xffffffffL
    } else floored(((exponent - 127).toLong << 38) + logarithmCore(segment, fraction & 0x3ffffL))
  }

  def sine32(bits: Long): Long = {
    val magnitude = bits & 0x7fffffffL
    if (magnitude >= Infinity32) if (magnitude != Infinity32) bits | 0x400000L else DefaultNaN32
    else if (magnitude == 0L) bits & 0x80000000L
    else {
      val t      = turns(magnitude)
      val result =
        if (magnitude < 0x39c00000L) java.lang.Float.floatToRawIntBits((t * SmallSineSlope).toFloat).toLong
        else sineCore(t)
      if ((bits & 0x80000000L) != 0L && result != 0L) result ^ 0x80000000L else result
    }
  }

  def cosine32(bits: Long): Long = {
    val magnitude = bits & 0x7fffffffL
    if (magnitude >= Infinity32) if (magnitude != Infinity32) bits | 0x400000L else DefaultNaN32
    else {
      val t = turns(magnitude) + 0.25
      sineCore(t - math.floor(t))
    }
  }

  def reciprocal64(bits: Long): Long = {
    val sign     = bits & Long.MinValue
    val exponent = ((bits >>> 52) & 0x7ffL).toInt
    val fraction = bits & Fraction64
    if (exponent == 0x7ff) if (fraction == 0L) sign else bits | (1L << 51)
    else if (exponent == 0 && fraction == 0L) sign | Infinity64
    else {
      val m      = normalFraction(exponent, fraction)
      val biased = 2045 - normalExponent(exponent, fraction)
      if (m == Fraction64) sign | (pack(biased, 1L << 52) + 1L)
      else {
        val v = reciprocalCore((m >>> 47).toInt, (m >>> 23) & 0xffffffL)
        if ((v >> 35) == 0L) sign | pack(biased, v << 18)
        else if (m == 0L) sign | pack(biased + 1, 1L << 52)
        else sign | pack(biased, (1L << 53) - 1L)
      }
    }
  }

  def reciprocalSquareRoot64(bits: Long): Long = {
    val sign     = bits & Long.MinValue
    val exponent = ((bits >>> 52) & 0x7ffL).toInt
    val fraction = bits & Fraction64
    if (exponent == 0x7ff) if (fraction != 0L) bits | (1L << 51) else if (sign != 0L) DefaultNaN64 else 0L
    else if (exponent == 0 && fraction == 0L) sign | Infinity64
    else if (sign != 0L) DefaultNaN64
    else {
      val m      = normalFraction(exponent, fraction)
      val k      = normalExponent(exponent, fraction) - 1023
      val odd    = k & 1
      val biased = 1022 - ((k - odd) >> 1)
      val v      = reciprocalSquareRootCore(odd, (m >>> 47).toInt, (m >>> 23) & 0xffffffL)
      if ((v >> 36) == 0L) pack(biased, v << 17)
      else if (m == 0L) pack(biased + 1, 1L << 52)
      else pack(biased, (1L << 53) - 1L)
    }
  }

  def squareRoot64(bits: Long): Long = {
    val sign     = bits & Long.MinValue
    val exponent = ((bits >>> 52) & 0x7ffL).toInt
    val fraction = bits & Fraction64
    if (exponent == 0x7ff) if (fraction != 0L) bits | (1L << 51) else if (sign != 0L) DefaultNaN64 else bits
    else if (exponent == 0 && fraction == 0L) bits
    else if (sign != 0L) DefaultNaN64
    else {
      val m   = normalFraction(exponent, fraction)
      val k   = normalExponent(exponent, fraction) - 1023
      val odd = k & 1
      val v   = squareRootCore(odd, (m >>> 48).toInt, (m >>> 24) & 0xffffffL)
      val top = (v >> 36).toInt
      pack(1023 + ((k - odd) >> 1) + top, if (top == 1) v << 16 else v << 17)
    }
  }

  private val reciprocalTable: Array[Long] = buildReciprocalTable()

  /** The reciprocal's coefficients, `c0`, `c1`, `c2` and `c3` of each segment in turn. */
  private def buildReciprocalTable(): Array[Long] = Array(
    536870912, -2097138, 16758272, -1925,
    520602100, -1971966, 15281408, -1705,
    505290268, -1857674, 13972992, -1516,
    490853404, -1753039, 12809984, -1352,
    477218588, -1657001, 11772416, -1210,
    464320788, -1568644, 10843904, -1086,
    452101820, -1487171, 10010624, -977,
    440509468, -1411884, 9260544, -882,
    429496732, -1342172, 8583424, -798,
    419021196, -1277499, 7970816, -724,
    409044508, -1217390, 7415296, -658,
    399531844, -1161426, 6909952, -599,
    390451572, -1109234, 6449664, -547,
    381774868, -1060483, 6029312, -501,
    373475420, -1014876, 5644800, -459,
    365529132, -972149, 5292288, -421,
    357913940, -932065, 4968448, -388,
    350609572, -894410, 4670464, -357,
    343597380, -858991, 4395776, -330,
    336860180, -825636, 4142336, -305,
    330382100, -794186, 3908096, -282,
    324148476, -764500, 3691008, -262,
    318145724, -736447, 3489792, -243,
    312361260, -709911, 3302912, -226,
    306783380, -684783, 3129088, -210,
    301401212, -660966, 2967296, -196,
    296204644, -638371, 2816512, -183,
    291184220, -616915, 2675712, -171,
    286331156, -596522, 2544128, -160,
    281637196, -577124, 2421248, -150,
    277094668, -558658, 2305792, -140,
    272696340, -541063, 2197760, -131
  )

  private val reciprocalSquareRootTable: Array[Long] = buildReciprocalSquareRootTable()

  /** The reciprocal square root's coefficients, laid out as the reciprocal's, the even exponents' 32 segments first. */
  private def buildReciprocalSquareRootTable(): Array[Long] = Array(
    536870912, -2097144, 12572160, -1212,
    528673932, -2002546, 11641856, -1090,
    520841292, -1914852, 10805248, -982,
    513346780, -1833376, 10050048, -890,
    506166748, -1757518, 9367040, -806,
    499279804, -1686752, 8747008, -734,
    492666540, -1620610, 8183296, -670,
    486309284, -1558680, 7668736, -612,
    480191940, -1500596, 7198720, -560,
    474299796, -1446032, 6768128, -514,
    468619348, -1394698, 6372352, -474,
    463138236, -1346330, 6008320, -436,
    457845052, -1300694, 5672960, -402,
    452729308, -1257578, 5363200, -372,
    447781292, -1216794, 5076480, -346,
    442992052, -1178168, 4810752, -320,
    438353268, -1141542, 4563968, -298,
    433857212, -1106776, 4335104, -278,
    429496732, -1073740, 4121600, -258,
    425265132, -1042314, 3922432, -242,
    421156196, -1012392, 3736576, -226,
    417164100, -983876, 3563008, -210,
    413283420, -956672, 3400192, -198,
    409509060, -930700, 3247616, -186,
    405836260, -905882, 3104768, -174,
    402260540, -882148, 2970624, -164,
    398777700, -859434, 2844160, -154,
    395383788, -837676, 2724864, -146,
    392075076, -816822, 2612736, -136,
    388848068, -796818, 2507264, -130,
    385699452, -777618, 2407424, -122,
    382626100, -759178, 2312704, -116,
    379625056, -1482904, 8889856, -856,
    373828916, -1416014, 8231936, -770,
    368290404, -1354004, 7640064, -694,
    362990988, -1296392, 7106560, -628,
    357913940, -1242752, 6623232, -570,
    353044140, -1192712, 6184960, -518,
    348367852, -1145944, 5786112, -472,
    343872588, -1102152, 5422592, -432,
    339546980, -1061082, 5090304, -396,
    335380596, -1022500, 4785664, -364,
    331363924, -986200, 4506112, -334,
    327488188, -951998, 4248576, -308,
    323745340, -919728, 4011520, -284,
    320127964, -889242, 3792384, -264,
    316629188, -860404, 3589632, -244,
    313242684, -833090, 3401728, -226,
    309962564, -807192, 3227136, -210,
    306783380, -782608, 3065344, -196,
    303700052, -759248, 2914304, -182,
    300707860, -737028, 2773504, -170,
    297802404, -715870, 2641920, -160,
    294979564, -695704, 2519040, -148,
    292235508, -676470, 2404352, -140,
    289566636, -658104, 2296320, -130,
    286969572, -640556, 2195456, -122,
    284441156, -623774, 2100224, -116,
    281978420, -607710, 2011136, -108,
    279578556, -592326, 1926656, -102,
    277238948, -577580, 1847808, -96,
    274957108, -563436, 1772544, -90,
    272730700, -549860, 1702400, -86,
    270557508, -536820, 1635328, -82
  )

  private val squareRootTable: Array[Long] = buildSquareRootTable()

  /** The square root's coefficients, laid out as the reciprocal's, the even exponents' 16 segments first. */
  private def buildSquareRootTable(): Array[Long] = Array(
    268435460, 2097143, -8376832, 948,
    276696932, 2034529, -7649792, 818,
    284718796, 1977208, -7022336, 712,
    292520756, 1924474, -6476032, 624,
    300119964, 1875745, -5996800, 550,
    307531452, 1830541, -5574144, 488,
    314768476, 1788454, -5198848, 436,
    321842804, 1749143, -4863744, 390,
    328764948, 1712315, -4563200, 352,
    335544324, 1677719, -4292352, 318,
    342189404, 1645139, -4047360, 290,
    348707884, 1614387, -3824640, 264,
    355106732, 1585296, -3621632, 240,
    361392292, 1557724, -3436032, 220,
    367570388, 1531542, -3265792, 204,
    373646336, 1506637, -3109120, 188,
    379625060, 2965809, -11846656, 1340,
    391308556, 2877259, -10818560, 1156,
    402653188, 2796194, -9931008, 1006,
    413686820, 2721617, -9158400, 882,
    424433724, 2652705, -8481024, 778,
    434915148, 2588776, -7883008, 692,
    445149844, 2529256, -7352320, 616,
    455154460, 2473662, -6878464, 554,
    464943852, 2421579, -6453248, 498,
    474531332, 2372654, -6070272, 450,
    483928900, 2326579, -5723648, 410,
    493147420, 2283088, -5409024, 372,
    502196756, 2241948, -5122048, 342,
    511085884, 2202954, -4859392, 312,
    519823028, 2165927, -4618752, 288,
    528415720, 2130707, -4397056, 266
  )

  private val powerOfTwoTable: Array[Long] = buildPowerOfTwoTable()

  /** The power of two's coefficients, laid out as the reciprocal's, `c0` in units of `2^-28`. */
  private def buildPowerOfTwoTable(): Array[Long] = Array(
    268435456, 11629082, 8060268, 58,
    274313427, 11883724, 8236765, 58,
    280320109, 12143944, 8417127, 60,
    286458319, 12409862, 8601438, 62,
    292730941, 12681602, 8789785, 62,
    299140913, 12959294, 8982256, 64,
    305691246, 13243066, 9178942, 66,
    312385013, 13533050, 9379934, 66,
    319225354, 13829386, 9585328, 68,
    326215479, 14132210, 9795219, 70,
    333358667, 14441666, 10009707, 72,
    340658273, 14757896, 10228891, 72,
    348117717, 15081052, 10452874, 74,
    355740503, 15411284, 10681762, 76,
    363530205, 15748748, 10915663, 78,
    371490480, 16093600, 11154685, 80,
    379625062, 16446004, 11398940, 82,
    387937769, 16806124, 11648545, 84,
    396432501, 17174132, 11903614, 84,
    405113242, 17550196, 12164270, 86,
    413984066, 17934494, 12430633, 88,
    423049137, 18327210, 12702828, 90,
    432312707, 18728522, 12980984, 92,
    441779122, 19138624, 13265230, 94,
    451452826, 19557706, 13555701, 96,
    461338356, 19985964, 13852532, 98,
    471440350, 20423598, 14155863, 100,
    481763549, 20870818, 14465836, 104,
    492312797, 21327828, 14782597, 106,
    503093043, 21794848, 15106293, 108,
    514109347, 22272092, 15437078, 110,
    525366876, 22759788, 15775106, 112
  )

  private val logarithmTable: Array[Long] = buildLogarithmTable()

  /** The logarithm's coefficients, laid out as the reciprocal's, `c0` in units of `2^-38`, then the second segment's
    * for offsets from `2^17`.
    */
  private def buildLogarithmTable(): Array[Long] = Array(
    3843L, 96817432, -101461664099200L, 7703424,
    12202963203L, 93883744, -95448467872128L, 7196208,
    24041604355L, 91122328, -89881754658944L, 6440064,
    35537048067L, 88518840, -84821461367224L, 5910132,
    46708631299L, 86060000, -80176655599946L, 5437299,
    57574104835L, 83734064, -75903289383688L, 5014168,
    68149799427L, 81530528, -71962597726720L, 4633665,
    78450767875L, 79440024, -68320961353824L, 4290664,
    88490925827L, 77454024, -64948791301316L, 3980348,
    98283150851L, 75564912, -61820402671776L, 3699880,
    107839397123L, 73765744, -58912609353400L, 3444856,
    117170767875L, 72050272, -56205135847744L, 3212384,
    126287606787L, 70412768, -53680081094360L, 3000264,
    135199554051L, 68848040, -51321557563664L, 2807024,
    143915622147L, 67351344, -49114916850320L, 2629240,
    152444227331L, 65918344, -47047770464964L, 2466857,
    160793272067L, 64545048, -45108400406720L, 2317504,
    168970161411L, 63227800, -43286470111966L, 2179819,
    176981846787L, 61963248, -41572887313340L, 2053443,
    184834874627L, 60748288, -39958936123208L, 1936152,
    192535407363L, 59580048, -38437091725600L, 1827320,
    200089254403L, 58455896, -37000525843216L, 1726376,
    207501900035L, 57373384, -35643056166288L, 1632888,
    214778525699L, 56330232, -34359204601182L, 1547042,
    221924032515L, 55324336, -33143284621136L, 1466372,
    228943066371L, 54353736, -31990606030112L, 1390544,
    235840021251L, 53416600, -30897083904160L, 1321584,
    242619074819L, 52511240, -29859002099264L, 1255436,
    249284189955L, 51636048, -28872074009700L, 1193876,
    255839132163L, 50789552, -27933288126288L, 1137588,
    262287484931L, 49970384, -27040522160856L, 1083560,
    268632660227L, 49177280, -26188340451368L, 1032952,
    12202985474L, 93882800, -95325977398754L, 6879954
  )

  private val sineTable: Array[Double] = buildSineTable()

  /** The sine's fitted cubics in the segment offset, in units of `2^-40`, laid out as the reciprocal's. */
  private def buildSineTable(): Array[Double] = Array(
    252.70019673201162, 53972169675.739586, 6267.863994227876, -21662944.426767256,
    53950447757.82614, 53907156737.65728, -65003427.27378021, -21630856.209834006,
    107770968261.52419, 53712322998.33772, -129883903.9765288, -21501667.626143906,
    161331836551.3024, 53388059708.081474, -194389080.28166965, -21360441.447251778,
    214504046048.4738, 52935204841.75997, -258521545.3877483, -21125847.843215935,
    267159499632.19623, 52354761460.26688, -321966095.8542104, -20864001.949110735,
    319171359002.65405, 51648209939.34853, -384628625.6954456, -20582839.346360035,
    370414291444.91833, 50817175453.90628, -446338361.146744, -20247978.400712065,
    420764879188.1106, 49863750542.7715, -506967293.9750367, -19872842.429752555,
    470101781831.15625, 48790339187.55024, -566539892.83477, -19342857.3482733,
    518306163490.79114, 47599226327.92919, -624643291.7417802, -18848643.07711347,
    565261930035.8431, 46293478045.04527, -681102668.1422383, -18356284.67030112,
    610855914982.204, 44876267834.95455, -736035044.1707042, -17758436.40429816,
    654978283103.3191, 43350868057.65742, -789255746.0117798, -17108047.134032875,
    697522777902.9084, 41721066933.05953, -840532840.1247722, -16436142.902821796,
    738386874937.6061, 39990793976.76292, -889836204.4448805, -15679083.325043984,
    777472157728.5634, 38164063640.02885, -936836260.5810336, -15017152.819059981,
    814684414412.4006, 36245471110.43303, -981818160.2016654, -14103420.026212549,
    849934001275.916, 34239562335.11308, -1024228488.6102785, -13326272.95353679,
    883136078993.004, 32151131025.69584, -1064207995.6165806, -12520937.148287032,
    914210563953.9144, 29985252455.99118, -1101607037.1825783, -11638550.151279002,
    943082612869.0314, 27747200911.84116, -1136401362.4042606, -10738232.961621732,
    969682724787.56, 25442285625.105946, -1168597485.4064407, -9676861.675319903,
    993946774308.0203, 23076051430.6286, -1197729289.0913045, -8846634.923250465,
    1015816334094.426, 20654239237.891598, -1224088202.8567023, -7855782.446456467,
    1035238681729.3649, 18182707689.678524, -1247535629.826885, -6849711.179599878,
    1052167089012.0056, 15667295244.293974, -1268051597.2071307, -5707879.65459252,
    1066560596768.6315, 13114250002.912245, -1285343161.4424212, -4724060.156200863,
    1078384864832.8105, 10529413487.977, -1299473867.8571053, -3768679.6158445445,
    1087611088266.7109, 7919395635.262397, -1310707963.7913532, -2598439.945694006,
    1094217182754.0825, 5290212555.361544, -1318558997.4806542, -1633576.0877109377,
    1098187157958.2408, 2648430813.796505, -1323478268.3721526, -474709.8079978381
  )

  private val fallingSineTable: Array[Double] = buildFallingSineTable()

  /** The fitted cubics of the quarter turn's upper half from the fifth segment back from the half turn. */
  private def buildFallingSineTable(): Array[Double] = Array(
    214504047280.78928, 52935188522.08876, -258486544.79298463, -21145366.828898616,
    267159500017.2212, 52354757784.94371, -321960107.1834537, -20865782.994278718,
    319171364340.77826, 51648176819.08306, -384573411.0430508, -20609496.793147225,
    370414290548.44543, 50817186986.93512, -446358808.8324714, -20237338.525924306,
    420764878795.52655, 49863757413.86222, -506981804.18711704, -19862662.959744807,
    470101782527.3414, 48790316153.30424, -566484149.8912197, -19374703.43676074,
    518306171573.2796, 47599155708.10881, -624511195.656773, -18916247.67485645,
    565261921645.7737, 46293530704.75672, -681178696.9598067, -18322957.237681307,
    610855914513.328, 44876289892.672844, -736074706.3311319, -17738606.033943642,
    654978282062.2219, 43350882617.7228, -789281125.550819, -17093579.007503074,
    697522777785.622, 41721071886.76024, -840534943.775333, -16436159.49751602,
    738386864289.518, 39990825448.63106, -889859057.9213235, -15674425.336667428,
    777472154278.8112, 38164082223.98636, -936917881.3410652, -14940696.205684245,
    814684410901.4773, 36245482846.0525, -981842646.0981402, -14086475.005692428,
    849933997488.9048, 34239585074.595104, -1024298902.1811589, -13273752.291652681,
    883136075694.0889, 32151145175.22542, -1064301870.6939042, -12424796.333430095,
    914210560779.3846, 29985262670.97168, -1101614851.2681813, -11646918.341983441,
    943082609306.0414, 27747207227.681107, -1136405013.100088, -10738166.707259571,
    969682721384.2281, 25442292991.086166, -1168613544.9956224, -9663465.44614743,
    993946770641.309, 23076064061.894096, -1197807664.8940425, -8762574.749064405,
    1015816330877.7867, 20654239447.392715, -1224076264.3429215, -7865457.1441586595,
    1035238678284.0378, 18182711520.15437, -1247531646.3791115, -6854683.826380052,
    1052167085508.6567, 15667276904.852684, -1267908777.2199667, -5844480.985326878,
    1066560593592.3326, 13114237612.406998, -1285334754.1577625, -4701505.096695613,
    1078384860691.1609, 10529431027.430899, -1299505861.4889274, -3751730.89842891,
    1087611084181.1458, 7919395837.851937, -1310626299.5136988, -2691788.353833263,
    1094217178653.4819, 5290229619.418122, -1318652509.136261, -1536769.7532503007,
    1098187154061.997, 2648444110.528022, -1323514459.0197566, -446433.62377616414
  )

  private val firstSineTable: Array[Double] = buildFirstSineTable()

  /** The first segment's quadratics, one per binade of the offset from `2^6`. */
  private def buildFirstSineTable(): Array[Double] = Array(
    6.283188104629517, 0.0, 0.0,
    6.283188104629517, 0.0, 0.0,
    6.283188056945801, 0.0, 0.0,
    6.283188056945801, 0.0, 0.0,
    6.283188056945801, 0.0, 0.0,
    6.283187726778772, 0.0032613409615991464, -9.095116647289975,
    6.283187789738664, 0.0008572245321173059, -1.1952992937097144,
    6.283187826181575, 0.00021985008887051848, -0.15327761190227937,
    6.28318726927387, 0.0009464039925190594, -0.34649568382177326,
    6.283187440483281, 0.0002725185861878076, -0.045335132569152316,
    6.283192710113259, -0.0017966159730393493, 0.1509511861490227,
    6.283188511438427, -0.0001305562207716587, 0.003334450906461115,
    6.2831876904786, 1.162688412205617e-5, -0.002736928565477994,
    6.283187848688629, -4.7125506032154014e-7, -0.0025103060672861887,
    6.283187454744304, 4.007198610826389e-6, -0.0025259583114782516,
    6.283187613015109, 2.023621968468865e-6, -0.0025270232842568453,
    6.28318781832919, -6.22241172529228e-8, -0.0025211515200273195,
    6.28318783551203, 4.707681513256372e-8, -0.0025215207545886036
  )

  private val sineStarts: Array[Long] = buildSineStarts()

  /** The sine at each segment start up to the quarter turn. */
  private def buildSineStarts(): Array[Long] = Array(
    0x00000000L, 0x3d48fb28L, 0x3dc8bd34L, 0x3e164082L, 0x3e47c5c0L, 0x3e78cfcaL, 0x3e94a031L, 0x3eac7cd3L,
    0x3ec3ef15L, 0x3edae880L, 0x3ef15ae9L, 0x3f039c3cL, 0x3f0e39d9L, 0x3f187fbfL, 0x3f226799L, 0x3f2beb49L,
    0x3f3504f4L, 0x3f3daefaL, 0x3f45e404L, 0x3f4d9f03L, 0x3f54db32L, 0x3f5b941bL, 0x3f61c598L, 0x3f676bd8L,
    0x3f6c835fL, 0x3f710908L, 0x3f74fa0bL, 0x3f7853f7L, 0x3f7b14bfL, 0x3f7d3aacL, 0x3f7ec46dL, 0x3f7fb10eL,
    0x3f800000L
  )

  private val fallingSineStarts: Array[Long] = buildFallingSineStarts()

  /** The sine at each segment start from the quarter turn, where the quarter's upper half differs. */
  private def buildFallingSineStarts(): Array[Long] = Array(
    0x3f800000L, 0x3f7fb10fL, 0x3f7ec46dL, 0x3f7d3aabL, 0x3f7b14beL, 0x3f7853f7L, 0x3f74fa0aL, 0x3f710908L,
    0x3f6c835eL, 0x3f676bd7L, 0x3f61c597L, 0x3f5b941aL, 0x3f54db31L, 0x3f4d9f02L, 0x3f45e403L, 0x3f3daef9L,
    0x3f3504f3L, 0x3f2beb49L, 0x3f226798L, 0x3f187fc0L, 0x3f0e39d9L, 0x3f039c3bL, 0x3ef15ae9L, 0x3edae87eL,
    0x3ec3ef13L, 0x3eac7cd3L, 0x3e94a031L, 0x3e78cfcdL, 0x3e47c5c3L, 0x3e164082L, 0x3dc8bd32L, 0x3d48fb37L
  )
}
