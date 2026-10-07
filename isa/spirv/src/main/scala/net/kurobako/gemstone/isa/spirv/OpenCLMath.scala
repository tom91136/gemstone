package net.kurobako.gemstone.isa.spirv

/* Adapted from musl src/math/{erf,lgamma_r,tgamma,asinh,acosh,atanh}.c.
 * Copyright © 2005-2020 Rich Felker, et al.
 * Source mapping, upstream Sun notices, and the musl MIT licence are preserved
 * in the accompanying package resource LICENSE.opencl-math.
 */
/*
 * ====================================================
 * Copyright (C) 1993 by Sun Microsystems, Inc. All rights reserved.
 *
 * Developed at SunPro, a Sun Microsystems, Inc. business.
 * Permission to use, copy, modify, and distribute this
 * software is freely granted, provided that this notice
 * is preserved.
 * ====================================================
 */
/*
 * ====================================================
 * Copyright (C) 1993 by Sun Microsystems, Inc. All rights reserved.
 *
 * Developed at SunSoft, a Sun Microsystems, Inc. business.
 * Permission to use, copy, modify, and distribute this
 * software is freely granted, provided that this notice
 * is preserved.
 * ====================================================
 *
 */
private object OpenCLMath {
  // Binary64 kernels; the interpreter rounds their results to the destination
  // width. Piecewise rational approximations avoid cancellation in erfc tails
  // and around the positive zeros of lgamma. Gamma uses the scaled Lanczos
  // rational approximation and reflection, without exponentiating lgamma.
  private val erx            = 8.45062911510467529297e-01
  private val efx8           = 1.02703333676410069053e+00
  private val pp0            = 1.28379167095512558561e-01
  private val pp1            = -3.25042107247001499370e-01
  private val pp2            = -2.84817495755985104766e-02
  private val pp3            = -5.77027029648944159157e-03
  private val pp4            = -2.37630166566501626084e-05
  private val qq1            = 3.97917223959155352819e-01
  private val qq2            = 6.50222499887672944485e-02
  private val qq3            = 5.08130628187576562776e-03
  private val qq4            = 1.32494738004321644526e-04
  private val qq5            = -3.96022827877536812320e-06
  private val pa0            = -2.36211856075265944077e-03
  private val pa1            = 4.14856118683748331666e-01
  private val pa2            = -3.72207876035701323847e-01
  private val pa3            = 3.18346619901161753674e-01
  private val pa4            = -1.10894694282396677476e-01
  private val pa5            = 3.54783043256182359371e-02
  private val pa6            = -2.16637559486879084300e-03
  private val qa1            = 1.06420880400844228286e-01
  private val qa2            = 5.40397917702171048937e-01
  private val qa3            = 7.18286544141962662868e-02
  private val qa4            = 1.26171219808761642112e-01
  private val qa5            = 1.36370839120290507362e-02
  private val qa6            = 1.19844998467991074170e-02
  private val ra0            = -9.86494403484714822705e-03
  private val ra1            = -6.93858572707181764372e-01
  private val ra2            = -1.05586262253232909814e+01
  private val ra3            = -6.23753324503260060396e+01
  private val ra4            = -1.62396669462573470355e+02
  private val ra5            = -1.84605092906711035994e+02
  private val ra6            = -8.12874355063065934246e+01
  private val ra7            = -9.81432934416914548592e+00
  private val sa1            = 1.96512716674392571292e+01
  private val sa2            = 1.37657754143519042600e+02
  private val sa3            = 4.34565877475229228821e+02
  private val sa4            = 6.45387271733267880336e+02
  private val sa5            = 4.29008140027567833386e+02
  private val sa6            = 1.08635005541779435134e+02
  private val sa7            = 6.57024977031928170135e+00
  private val sa8            = -6.04244152148580987438e-02
  private val rb0            = -9.86494292470009928597e-03
  private val rb1            = -7.99283237680523006574e-01
  private val rb2            = -1.77579549177547519889e+01
  private val rb3            = -1.60636384855821916062e+02
  private val rb4            = -6.37566443368389627722e+02
  private val rb5            = -1.02509513161107724954e+03
  private val rb6            = -4.83519191608651397019e+02
  private val sb1            = 3.03380607434824582924e+01
  private val sb2            = 3.25792512996573918826e+02
  private val sb3            = 1.53672958608443695994e+03
  private val sb4            = 3.19985821950859553908e+03
  private val sb5            = 2.55305040643316442583e+03
  private val sb6            = 4.74528541206955367215e+02
  private val sb7            = -2.24409524465858183362e+01
  private val a0             = 7.72156649015328655494e-02
  private val a1             = 3.22467033424113591611e-01
  private val a2             = 6.73523010531292681824e-02
  private val a3             = 2.05808084325167332806e-02
  private val a4             = 7.38555086081402883957e-03
  private val a5             = 2.89051383673415629091e-03
  private val a6             = 1.19270763183362067845e-03
  private val a7             = 5.10069792153511336608e-04
  private val a8             = 2.20862790713908385557e-04
  private val a9             = 1.08011567247583939954e-04
  private val a10            = 2.52144565451257326939e-05
  private val a11            = 4.48640949618915160150e-05
  private val tc             = 1.46163214496836224576e+00
  private val tf             = -1.21486290535849611461e-01
  private val tt             = -3.63867699703950536541e-18
  private val t0             = 4.83836122723810047042e-01
  private val t1             = -1.47587722994593911752e-01
  private val t2             = 6.46249402391333854778e-02
  private val t3             = -3.27885410759859649565e-02
  private val t4             = 1.79706750811820387126e-02
  private val t5             = -1.03142241298341437450e-02
  private val t6             = 6.10053870246291332635e-03
  private val t7             = -3.68452016781138256760e-03
  private val t8             = 2.25964780900612472250e-03
  private val t9             = -1.40346469989232843813e-03
  private val t10            = 8.81081882437654011382e-04
  private val t11            = -5.38595305356740546715e-04
  private val t12            = 3.15632070903625950361e-04
  private val t13            = -3.12754168375120860518e-04
  private val t14            = 3.35529192635519073543e-04
  private val u0             = -7.72156649015328655494e-02
  private val u1             = 6.32827064025093366517e-01
  private val u2             = 1.45492250137234768737e+00
  private val u3             = 9.77717527963372745603e-01
  private val u4             = 2.28963728064692451092e-01
  private val u5             = 1.33810918536787660377e-02
  private val v1             = 2.45597793713041134822e+00
  private val v2             = 2.12848976379893395361e+00
  private val v3             = 7.69285150456672783825e-01
  private val v4             = 1.04222645593369134254e-01
  private val v5             = 3.21709242282423911810e-03
  private val s0             = -7.72156649015328655494e-02
  private val s1             = 2.14982415960608852501e-01
  private val s2             = 3.25778796408930981787e-01
  private val s3             = 1.46350472652464452805e-01
  private val s4             = 2.66422703033638609560e-02
  private val s5             = 1.84028451407337715652e-03
  private val s6             = 3.19475326584100867617e-05
  private val r1             = 1.39200533467621045958e+00
  private val r2             = 7.21935547567138069525e-01
  private val r3             = 1.71933865632803078993e-01
  private val r4             = 1.86459191715652901344e-02
  private val r5             = 7.77942496381893596434e-04
  private val r6             = 7.32668430744625636189e-06
  private val w0             = 4.18938533204672725052e-01
  private val w1             = 8.33333333333329678849e-02
  private val w2             = -2.77777777728775536470e-03
  private val w3             = 7.93650558643019558500e-04
  private val w4             = -5.95187557450339963135e-04
  private val w5             = 8.36339918996282139126e-04
  private val w6             = -1.63092934096575273989e-03
  private val gammaNumerator = Array[Double](
    23531376880.410759688572007674451636754734846804940,
    42919803642.649098768957899047001988850926355848959,
    35711959237.355668049440185451547166705960488635843,
    17921034426.037209699919755754458931112671403265390,
    6039542586.3520280050642916443072979210699388420708,
    1439720407.3117216736632230727949123939715485786772,
    248874557.86205415651146038641322942321632125127801,
    31426415.585400194380614231628318205362874684987640,
    2876370.6289353724412254090516208496135991145378768,
    186056.26539522349504029498971604569928220784236328,
    8071.6720023658162106380029022722506138218516325024,
    210.82427775157934587250973392071336271166969580291,
    2.5066282746310002701649081771338373386264310793408
  )
  private val gammaDenominator = Array[Double](0, 39916800, 120543840, 150917976, 105258076, 45995730, 13339535,
    2637558, 357423, 32670, 1925, 66, 1)
  private val factorials = Array[Double](
    1, 1, 2, 6, 24, 120, 720, 5040.0, 40320.0, 362880.0, 3628800.0, 39916800.0,
    479001600.0, 6227020800.0, 87178291200.0, 1307674368000.0, 20922789888000.0,
    355687428096000.0, 6402373705728000.0, 121645100408832000.0,
    2432902008176640000.0, 51090942171709440000.0, 1124000727777607680000.0
  )

  def asinh(x: Double): Double = {
    val a = math.abs(x)
    val y = if (a >= 67108864.0) math.log(a) + 0.693147180559945309417232121458176568
    else if (a >= 2.0) math.log(2.0 * a + 1.0 / (math.sqrt(a * a + 1.0) + a))
    else if (a >= 1.4901161193847656e-8) math.log1p(a + a * a / (math.sqrt(a * a + 1.0) + 1.0))
    else a
    math.copySign(y, x)
  }
  def acosh(x: Double): Double =
    if (x < 1.0) Double.NaN
    else if (x < 2.0) {
      val t = x - 1.0
      math.log1p(t + math.sqrt(t * t + 2.0 * t))
    } else if (x < 67108864.0) math.log(2.0 * x - 1.0 / (x + math.sqrt(x * x - 1.0)))
    else math.log(x) + 0.693147180559945309417232121458176568
  def atanh(x: Double): Double = {
    val a = math.abs(x)
    val y = if (a > 1.0) Double.NaN
    else if (a < 2.3283064365386963e-10) a
    else if (a < 0.5) 0.5 * math.log1p(2.0 * a + 2.0 * a * a / (1.0 - a))
    else 0.5 * math.log1p(2.0 * (a / (1.0 - a)))
    math.copySign(y, x)
  }

  private def highMagnitude(x: Double): Int = ((java.lang.Double.doubleToRawLongBits(x) >>> 32) & 0x7fffffffL).toInt
  private def negative(x: Double): Boolean  = java.lang.Double.doubleToRawLongBits(x) < 0L

  private def erfRatio(x: Double): Double = {
    val z = x * x
    val r = pp0 + z * (pp1 + z * (pp2 + z * (pp3 + z * pp4)))
    val s = 1.0 + z * (qq1 + z * (qq2 + z * (qq3 + z * (qq4 + z * qq5))))
    r / s
  }
  private def erfcPositive(x: Double): Double =
    if (x < 1.25) {
      val s = x - 1.0
      val p = pa0 +
        s *
        (pa1 + s * (pa2 + s * (pa3 + s * (pa4 + s * (pa5 + s * pa6)))))
      val q = 1.0 +
        s *
        (qa1 + s * (qa2 + s * (qa3 + s * (qa4 + s * (qa5 + s * qa6)))))
      1.0 - erx - p / q
    } else {
      val s      = 1.0 / (x * x)
      val (r, q) = if (highMagnitude(x) < 0x4006db6d) {
        (
          ra0 +
            s *
            (ra1 +
              s *
              (ra2 + s * (ra3 + s * (ra4 + s * (ra5 + s * (ra6 + s * ra7)))))),
          1.0 +
            s *
            (sa1 +
              s *
              (sa2 +
                s *
                (sa3 + s * (sa4 + s * (sa5 + s * (sa6 + s * (sa7 + s * sa8)))))))
        )
      } else {
        (
          rb0 +
            s *
            (rb1 + s * (rb2 + s * (rb3 + s * (rb4 + s * (rb5 + s * rb6))))),
          1.0 +
            s *
            (sb1 +
              s *
              (sb2 + s * (sb3 + s * (sb4 + s * (sb5 + s * (sb6 + s * sb7))))))
        )
      }
      val z = java.lang.Double.longBitsToDouble(java.lang.Double.doubleToRawLongBits(x) & 0xffffffff00000000L)
      math.exp(-z * z - 0.5625) * math.exp((z - x) * (z + x) + r / q) / x
    }
  def erf(x: Double): Double = {
    val a = math.abs(x)
    if (x.isNaN) Double.NaN
    else if (a < 0.84375) {
      if (a < 3.725290298461914e-9) 0.125 * (8.0 * x + efx8 * x)
      else x + x * erfRatio(x)
    } else if (a < 6.0) math.copySign(1.0 - erfcPositive(a), x)
    else math.copySign(1.0, x)
  }
  def erfc(x: Double): Double = {
    val a = math.abs(x)
    if (x.isNaN) Double.NaN
    else if (a < 0.84375) {
      if (a < 1.3877787807814457e-17) 1.0 - x
      else {
        val y = erfRatio(x)
        if (x < 0.25) 1.0 - (x + x * y) else 0.5 - (x - 0.5 + x * y)
      }
    } else if (a < 28.0) {
      val y = erfcPositive(a)
      if (negative(x)) 2.0 - y else y
    } else if (negative(x)) 2.0 else 0.0
  }

  // Reduce before multiplying by pi, preserving values close to integer poles.
  private def sinPiPositive(x: Double): Double = {
    val reduced  = 2.0 * (x * 0.5 - math.floor(x * 0.5))
    val quadrant = ((4.0 * reduced).toInt + 1) / 2
    val angle    = (reduced - quadrant * 0.5) * math.Pi
    quadrant match {
      case 1 => math.cos(angle)
      case 2 => math.sin(-angle)
      case 3 => -math.cos(angle)
      case _ => math.sin(angle)
    }
  }
  def gammaSign(x: Double): Int =
    if (negative(x) && math.abs(x) < 8.470329472543003e-22) -1
    else if (x < 0.0 && x != math.floor(x) && sinPiPositive(-x) > 0.0) -1
    else 1
  private def gammaSeries(x: Double): Double = {
    @scala.annotation.tailrec
    def ascending(i: Int, num: Double, den: Double): Double =
      if (i == gammaNumerator.length) num / den
      else ascending(i + 1, num / x + gammaNumerator(i), den / x + gammaDenominator(i))
    @scala.annotation.tailrec
    def descending(i: Int, num: Double, den: Double): Double =
      if (i < 0) num / den
      else descending(i - 1, num * x + gammaNumerator(i), den * x + gammaDenominator(i))
    if (x < 8.0) descending(gammaNumerator.length - 1, 0.0, 0.0)
    else ascending(0, 0.0, 0.0)
  }
  def tgamma(x: Double): Double = {
    val a = math.abs(x)
    if (x.isNaN || x == Double.NegativeInfinity) Double.NaN
    else if (x == Double.PositiveInfinity) Double.PositiveInfinity
    else if (a < 5.551115123125783e-17) 1.0 / x
    else if (x < 0.0 && x == math.floor(x)) Double.NaN
    else if (x > 0.0 && x == math.floor(x) && x <= factorials.length) factorials(x.toInt - 1)
    else if (a >= 184.0) {
      if (x > 0.0) Double.PositiveInfinity else math.copySign(0.0, gammaSign(x).toDouble)
    } else {
      val gmhalf    = 5.524680040776729583740234375
      val y         = a + gmhalf
      val error     = if (a > gmhalf) (y - a) - gmhalf else (y - gmhalf) - a
      val scaled    = gammaSeries(a) * math.exp(-y)
      val r         = if (x < 0.0) -math.Pi / (sinPiPositive(a) * a * scaled) else scaled
      val dy        = if (x < 0.0) -error else error
      val z         = if (x < 0.0) -(a - 0.5) else a - 0.5
      val corrected = r + dy * (gmhalf + 0.5) * r / y
      val power     = math.pow(y, 0.5 * z)
      corrected * power * power
    }
  }
  def lgamma(x: Double): Double = {
    val a = math.abs(x)
    if (x.isNaN) Double.NaN
    else if (a.isInfinity) Double.PositiveInfinity
    else if (a < 8.470329472543003e-22) -math.log(a)
    else if (x < 0.0 && x == math.floor(x)) Double.PositiveInfinity
    else {
      val result = logGammaPositive(a)
      if (x < 0.0) math.log(math.Pi / (math.abs(sinPiPositive(a)) * a)) - result else result
    }
  }
  private def logGammaPositive(x: Double): Double = {
    val ix = highMagnitude(x)
    if (x == 1.0 || x == 2.0) 0.0
    else if (x < 2.0) {
      val r           = if (ix <= 0x3feccccc) -math.log(x) else 0.0
      val (y, branch) =
        if (ix <= 0x3feccccc) {
          if (ix >= 0x3fe76944) (1.0 - x, 0)
          else if (ix >= 0x3fcda661) (x - (tc - 1.0), 1)
          else (x, 2)
        } else {
          if (ix >= 0x3ffbb4c3) (2.0 - x, 0)
          else if (ix >= 0x3ff3b4c4) (x - tc, 1)
          else (x - 1.0, 2)
        }
      branch match {
        case 0 =>
          val z  = y * y
          val p1 = a0 + z * (a2 + z * (a4 + z * (a6 + z * (a8 + z * a10))))
          val p2 = z *
            (a1 + z * (a3 + z * (a5 + z * (a7 + z * (a9 + z * a11)))))
          r + (y * p1 + p2 - 0.5 * y)
        case 1 =>
          val z  = y * y
          val w  = z * y
          val p1 = t0 + w * (t3 + w * (t6 + w * (t9 + w * t12)))
          val p2 = t1 + w * (t4 + w * (t7 + w * (t10 + w * t13)))
          val p3 = t2 + w * (t5 + w * (t8 + w * (t11 + w * t14)))
          r + (tf + (z * p1 - (tt - w * (p2 + y * p3))))
        case _ =>
          val p1 = y *
            (u0 + y * (u1 + y * (u2 + y * (u3 + y * (u4 + y * u5)))))
          val p2 = 1.0 + y * (v1 + y * (v2 + y * (v3 + y * (v4 + y * v5))))
          r + (-0.5 * y + p1 / p2)
      }
    } else if (x < 8.0) {
      val i = x.toInt
      val y = x - i
      val p = y *
        (s0 +
          y *
          (s1 + y * (s2 + y * (s3 + y * (s4 + y * (s5 + y * s6))))))
      val q = 1.0 +
        y *
        (r1 + y * (r2 + y * (r3 + y * (r4 + y * (r5 + y * r6)))))
      @scala.annotation.tailrec
      def product(k: Int, accumulated: Double): Double =
        if (k < 2) accumulated else product(k - 1, accumulated * (y + k))
      0.5 * y + p / q + math.log(product(i - 1, 1.0))
    } else if (x < 2.8823037615171174e17) {
      val z = 1.0 / x
      val y = z * z
      val w = w0 +
        z *
        (w1 + y * (w2 + y * (w3 + y * (w4 + y * (w5 + y * w6)))))
      (x - 0.5) * (math.log(x) - 1.0) + w
    } else x * (math.log(x) - 1.0)
  }
}
