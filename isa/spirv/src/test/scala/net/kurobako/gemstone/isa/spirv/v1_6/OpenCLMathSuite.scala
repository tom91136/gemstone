package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*

class OpenCLMathSuite extends munit.FunSuite {
  // Rounded binary64 references generated using mpmath 1.3.0 at 100 decimal
  // digits from the exact binary64 inputs. No reference library is needed to run.
  private def check(actual: Double, expected: Double, ulps: Double, clue: String): Unit =
    if (expected.isInfinity) assertEquals(actual, expected, clue)
    else assert(math.abs(actual - expected) <= ulps * math.ulp(expected), s"$clue: $actual != $expected")
  test("erf binary64 reference values and approximation boundaries") {
    val cases = Vector(
      (-30.0, -1.0),
      (-6.0, -1.0),
      (-3.0, -0.9999779095030014),
      (-1.25, -0.9229001282564583),
      (-1.0, -0.8427007929497149),
      (-0.84375, -0.7672256612323416),
      (-0.25, -0.27632639016823696),
      (-1e-20, -1.1283791670955125e-20),
      (-1e-300, -1.1283791670955126e-300),
      (-5e-324, -5e-324),
      (0.0, 0.0),
      (5e-324, 5e-324),
      (1e-300, 1.1283791670955126e-300),
      (1e-20, 1.1283791670955125e-20),
      (0.25, 0.27632639016823696),
      (0.6707806551, 0.657189697034567),
      (0.84375, 0.7672256612323416),
      (1.0, 0.8427007929497149),
      (1.25, 0.9229001282564583),
      (2.0, 0.9953222650189527),
      (2.857142857142857, 0.9999466876886117),
      (3.0, 0.9999779095030014),
      (6.0, 1.0),
      (9.0, 1.0),
      (20.0, 1.0),
      (26.0, 1.0),
      (26.6, 1.0),
      (27.0, 1.0),
      (27.2, 1.0),
      (28.0, 1.0),
      (30.0, 1.0)
    )
    cases.foreach { case (x, expected) =>
      check(OpenCLMath.erf(x), expected, 16.0, s"erf($x)")
    }
  }
  test("erfc binary64 reference values and approximation boundaries") {
    val cases = Vector(
      (-30.0, 2.0),
      (-6.0, 2.0),
      (-3.0, 1.9999779095030015),
      (-1.25, 1.9229001282564582),
      (-1.0, 1.8427007929497148),
      (-0.84375, 1.7672256612323416),
      (-0.25, 1.276326390168237),
      (-1e-20, 1.0),
      (-1e-300, 1.0),
      (-5e-324, 1.0),
      (0.0, 1.0),
      (5e-324, 1.0),
      (1e-300, 1.0),
      (1e-20, 1.0),
      (0.25, 0.7236736098317631),
      (0.6707806551, 0.3428103029654331),
      (0.84375, 0.23277433876765838),
      (1.0, 0.15729920705028513),
      (1.25, 0.07709987174354177),
      (2.0, 0.004677734981047266),
      (2.857142857142857, 5.3312311388322795e-05),
      (3.0, 2.209049699858544e-05),
      (6.0, 2.1519736712498913e-17),
      (9.0, 4.13703174651381e-37),
      (20.0, 5.395865611607901e-176),
      (26.0, 5.663192408856143e-296),
      (26.6, 1.088512588544227e-309),
      (27.0, 5.23705e-319),
      (27.2, 1e-323),
      (28.0, 0.0),
      (30.0, 0.0)
    )
    cases.foreach { case (x, expected) =>
      check(OpenCLMath.erfc(x), expected, 16.0, s"erfc($x)")
    }
  }
  test("tgamma binary64 reference values and approximation boundaries") {
    val cases = Vector(
      (-183.5, 0.0),
      (-177.5, 5e-324),
      (-172.5, -1.11978350329e-312),
      (-171.5, 1.9316265431712e-310),
      (-170.5, -3.3127395215386074e-308),
      (-150.5, -4.478447658150641e-264),
      (-30.125, -2.0183623868151272e-32),
      (-10.25, -6.780818043294673e-07),
      (-3.5, 0.2700882058522691),
      (-2.5, -0.9453087204829419),
      (-1.5, 2.363271801207355),
      (-0.5, -3.544907701811032),
      (-1e-20, -1e+20),
      (-1e-300, -9.999999999999999e+299),
      (5e-324, Double.PositiveInfinity),
      (1e-300, 9.999999999999999e+299),
      (1e-20, 1e+20),
      (0.1, 9.51350769866873),
      (0.5, 1.772453850905516),
      (0.7908631554, 1.1746536775853855),
      (1.0, 1.0),
      (1.0000000000000002, 0.9999999999999999),
      (0.9999999999999999, 1.0),
      (1.4616321449683622, 0.8856031944108887),
      (1.9999999999999998, 0.9999999999999999),
      (2.0, 1.0),
      (2.0000000000000004, 1.0000000000000002),
      (2.5, 1.329340388179137),
      (7.25, 1155.3810139199898),
      (8.0, 5040.0),
      (23.0, 1.1240007277776077e+21),
      (32.0, 8.222838654177922e+33),
      (100.0, 9.332621544394415e+155),
      (150.0, 3.80892263763057e+260),
      (170.0, 4.269068009004705e+304),
      (171.0, 7.257415615307999e+306),
      (171.624, 1.7942117599248104e+308),
      (172.0, Double.PositiveInfinity)
    )
    cases.foreach { case (x, expected) =>
      check(OpenCLMath.tgamma(x), expected, 16.0, s"tgamma($x)")
    }
  }
  test("lgamma binary64 reference values and approximation boundaries") {
    val cases = Vector(
      (-183.5, -775.3221615985234),
      (-177.5, -744.1314465173804),
      (-172.5, -718.2934136481563),
      (-171.5, -713.1430164116848),
      (-170.5, -707.9984331450788),
      (-150.5, -606.3831880689249),
      (-30.125, -72.98043649278745),
      (-10.25, -14.20399790093109),
      (-3.5, -1.309006684993042),
      (-2.5, -0.056243716497674054),
      (-1.5, 0.860047015376481),
      (-0.5, 1.2655121234846454),
      (-1e-20, 46.051701859880914),
      (-1e-300, 690.7755278982137),
      (5e-324, 744.4400719213812),
      (1e-300, 690.7755278982137),
      (1e-20, 46.051701859880914),
      (0.1, 2.252712651734206),
      (0.5, 0.5723649429247001),
      (0.7908631554, 0.1609733616704975),
      (1.0, 0.0),
      (1.0000000000000002, -1.2816762426960008e-16),
      (0.9999999999999999, 6.408381213480008e-17),
      (1.4616321449683622, -0.12148629053584961),
      (1.9999999999999998, -9.387698065543117e-17),
      (2.0, 0.0),
      (2.0000000000000004, 1.8775396131086244e-16),
      (2.5, 0.2846828704729192),
      (7.25, 7.0521854507385395),
      (8.0, 8.525161361065415),
      (23.0, 48.47118135183523),
      (32.0, 78.0922235533153),
      (100.0, 359.1342053695754),
      (150.0, 600.0094705553274),
      (170.0, 701.437263808737),
      (171.0, 706.5730622457874),
      (171.624, 709.780774436699),
      (172.0, 711.71472580229)
    )
    cases.foreach { case (x, expected) =>
      check(OpenCLMath.lgamma(x), expected, 16.0, s"lgamma($x)")
    }
  }
  test("special functions preserve signed zero, poles, NaNs and infinities") {
    for (f <- Seq(OpenCLMath.erf, OpenCLMath.erfc, OpenCLMath.tgamma, OpenCLMath.lgamma)) assert(f(Double.NaN).isNaN)
    assertEquals(java.lang.Double.doubleToRawLongBits(OpenCLMath.erf(-0.0)), Long.MinValue)
    assertEquals(OpenCLMath.erf(Double.PositiveInfinity), 1.0)
    assertEquals(OpenCLMath.erf(Double.NegativeInfinity), -1.0)
    assertEquals(OpenCLMath.erfc(0.0), 1.0)
    assertEquals(OpenCLMath.erfc(-0.0), 1.0)
    assertEquals(OpenCLMath.erfc(Double.PositiveInfinity), 0.0)
    assertEquals(OpenCLMath.erfc(Double.NegativeInfinity), 2.0)
    assertEquals(OpenCLMath.tgamma(0.0), Double.PositiveInfinity)
    assertEquals(OpenCLMath.tgamma(-0.0), Double.NegativeInfinity)
    assertEquals(OpenCLMath.tgamma(Double.PositiveInfinity), Double.PositiveInfinity)
    assert(OpenCLMath.tgamma(Double.NegativeInfinity).isNaN)
    for (x <- Seq(-1.0, -2.0, -170.0, -1e20)) {
      assert(OpenCLMath.tgamma(x).isNaN)
      assertEquals(OpenCLMath.lgamma(x), Double.PositiveInfinity)
    }
    assertEquals(OpenCLMath.lgamma(0.0), Double.PositiveInfinity)
    assertEquals(OpenCLMath.lgamma(-0.0), Double.PositiveInfinity)
    assertEquals(OpenCLMath.lgamma(Double.PositiveInfinity), Double.PositiveInfinity)
    assertEquals(OpenCLMath.lgamma(Double.NegativeInfinity), Double.PositiveInfinity)
    assertEquals(java.lang.Double.doubleToRawLongBits(OpenCLMath.tgamma(-184.5)), Long.MinValue)
    assertEquals(java.lang.Double.doubleToRawLongBits(OpenCLMath.tgamma(-185.5)), 0L)
  }
  test("gamma sign follows the reflection intervals and signed zero") {
    for (x <- Seq(0.0, 0.25, 2.0, -1.5, -3.5)) assertEquals(OpenCLMath.gammaSign(x), 1)
    for (x <- Seq(-0.0, -0.25, -2.5, -4.5)) assertEquals(OpenCLMath.gammaSign(x), -1)
  }
  test("asinh stable binary64 references") {
    val cases = Vector(
      (-1.7976931348623157e+308, -710.475860073944),
      (-67108864.0, -18.714973875118524),
      (-3.0, -1.8184464592320668),
      (-2.0, -1.4436354751788103),
      (-1.0, -0.881373587019543),
      (-0.5, -0.48121182505960347),
      (-0.1071176656, -0.10691386799122429),
      (-1e-20, -1e-20),
      (-5e-324, -5e-324),
      (0.0, 0.0),
      (5e-324, 5e-324),
      (1e-20, 1e-20),
      (0.1071176656, 0.10691386799122429),
      (0.125, 0.12467674692144275),
      (0.5, 0.48121182505960347),
      (1.0, 0.881373587019543),
      (2.0, 1.4436354751788103),
      (3.0, 1.8184464592320668),
      (67108864.0, 18.714973875118524),
      (1.7976931348623157e+308, 710.475860073944)
    )
    cases.foreach { case (x, expected) => check(OpenCLMath.asinh(x), expected, 4.0, s"asinh($x)") }
  }
  test("acosh stable binary64 references") {
    val cases = Vector(
      (1.0, 0.0),
      (1.0000000000000002, 2.1073424255447014e-08),
      (1.000001, 0.00141421344446382),
      (1.125, 0.4949329230945269),
      (1.5, 0.9624236501192069),
      (2.0, 1.3169578969248168),
      (1.9999999999999998, 1.3169578969248166),
      (2.0000000000000004, 1.316957896924817),
      (3.0, 1.762747174039086),
      (67108864.0, 18.714973875118524),
      (1.7976931348623157e+308, 710.475860073944)
    )
    cases.foreach { case (x, expected) => check(OpenCLMath.acosh(x), expected, 4.0, s"acosh($x)") }
  }
  test("atanh stable binary64 references") {
    val cases = Vector(
      (-0.9999999999999999, -18.714973875118524),
      (-0.75, -0.9729550745276566),
      (-0.5, -0.5493061443340549),
      (-0.125, -0.12565721414045303),
      (-1e-20, -1e-20),
      (-5e-324, -5e-324),
      (0.0, 0.0),
      (5e-324, 5e-324),
      (1e-20, 1e-20),
      (0.125, 0.12565721414045303),
      (0.5, 0.5493061443340549),
      (0.75, 0.9729550745276566),
      (0.9999999999999999, 18.714973875118524)
    )
    cases.foreach { case (x, expected) => check(OpenCLMath.atanh(x), expected, 4.0, s"atanh($x)") }
  }
  test("inverse hyperbolic domain, infinity and signed zero behavior") {
    for (f <- Seq(OpenCLMath.asinh, OpenCLMath.acosh, OpenCLMath.atanh)) assert(f(Double.NaN).isNaN)
    for (f <- Seq(OpenCLMath.asinh, OpenCLMath.atanh)) {
      assertEquals(java.lang.Double.doubleToRawLongBits(f(-0.0)), Long.MinValue)
      assertEquals(java.lang.Double.doubleToRawLongBits(f(0.0)), 0L)
    }
    assertEquals(OpenCLMath.asinh(Double.PositiveInfinity), Double.PositiveInfinity)
    assertEquals(OpenCLMath.asinh(Double.NegativeInfinity), Double.NegativeInfinity)
    assertEquals(OpenCLMath.acosh(Double.PositiveInfinity), Double.PositiveInfinity)
    for (x <- Seq(Double.NegativeInfinity, -1.0, -0.0, 0.0, 0.5, math.nextDown(1.0))) assert(OpenCLMath.acosh(x).isNaN)
    assertEquals(OpenCLMath.atanh(1.0), Double.PositiveInfinity)
    assertEquals(OpenCLMath.atanh(-1.0), Double.NegativeInfinity)
    for (x <- Seq(Double.NegativeInfinity, Double.PositiveInfinity, -2.0, 2.0)) assert(OpenCLMath.atanh(x).isNaN)
  }
  test("reflected lgamma retains absolute accuracy near negative zeros") {
    // Reflection subtracts similarly sized logarithms here. Check absolute
    // accuracy explicitly rather than claiming a uniform relative ULP bound.
    assertEqualsDouble(OpenCLMath.lgamma(-2.4557692717618287), 0.0019104497747237973, 4e-16)
    assertEqualsDouble(OpenCLMath.lgamma(-3.9546947985419485), -0.012334723943424433, 4e-16)
  }
  test("special math rounds accurately to binary32 and binary16") {
    val functions: Map[String, Double => Double] = Map(
      "erf"    -> OpenCLMath.erf,
      "erfc"   -> OpenCLMath.erfc,
      "tgamma" -> OpenCLMath.tgamma,
      "lgamma" -> OpenCLMath.lgamma,
      "asinh"  -> OpenCLMath.asinh,
      "acosh"  -> OpenCLMath.acosh,
      "atanh"  -> OpenCLMath.atanh
    )
    val cases = Vector(
      ("erf", -2.0, 0xbf7ecd71, 0xbbf6),
      ("erf", -0.669921875, 0xbf281511, 0xb941),
      ("erf", 0.125, 0x3e0faf0d, 0x307d),
      ("erf", 3.0, 0x3f7ffe8d, 0x3c00),
      ("erfc", -2.0, 0x3fff66b8, 0x3ffb),
      ("erfc", 0.669921875, 0x3eafd5df, 0x357f),
      ("erfc", 3.0, 0x37b94efb, 0x0173),
      ("erfc", 5.0, 0x2bd860d4, 0x0000),
      ("tgamma", -3.5, 0x3e8a4900, 0x3452),
      ("tgamma", 0.0999755859375, 0x4118413f, 0x48c2),
      ("tgamma", 0.7900390625, 0x3f967a61, 0x3cb4),
      ("tgamma", 9.5, 0x47e8fe3b, 0x7c00),
      ("lgamma", -3.5, 0xbfa78d88, 0xbd3c),
      ("lgamma", 0.0999755859375, 0x4010309d, 0x4082),
      ("lgamma", 0.7900390625, 0x3e25ab76, 0x312d),
      ("lgamma", 9.5, 0x413b0782, 0x49d8),
      ("asinh", -100.0, 0xc0a98c05, 0xc54c),
      ("asinh", -0.10699462890625, 0xbddab585, 0xaed6),
      ("asinh", 0.125, 0x3dff5686, 0x2ffb),
      ("asinh", 100.0, 0x40a98c05, 0x454c),
      ("acosh", 1.0, 0x00000000, 0x0000),
      ("acosh", 1.125, 0x3efd67d9, 0x37eb),
      ("acosh", 2.0, 0x3fa89214, 0x3d45),
      ("acosh", 100.0, 0x40a98b9c, 0x454c),
      ("atanh", -0.990234375, 0xc02a2601, 0xc151),
      ("atanh", -0.125, 0xbe00ac49, 0xb005),
      ("atanh", 0.5, 0x3f0c9f54, 0x3865),
      ("atanh", 0.990234375, 0x402a2601, 0x4151)
    )
    cases.foreach { case (name, x, expectedFloatBits, expectedHalfBits) =>
      val value    = functions(name)(x).toFloat
      val expected = java.lang.Float.intBitsToFloat(expectedFloatBits)
      assert(math.abs(value - expected) <= 4.0f * math.ulp(expected), s"$name($x) binary32")
      assertEquals(IEEE754.floatToBinary16(value).toInt, expectedHalfBits, s"$name($x) binary16")
    }
  }
}
