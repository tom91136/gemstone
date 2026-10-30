package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.Event.*

class PrintRendererSuite extends Suite {
  private def render(format: String, arguments: PrintArgument*): Either[String, String] =
    PrintRenderer.render(Print(format, Vec.from(arguments)), maxOutputChars = 1024 * 1024)

  test("renders the integer, text, and floating conversions") {
    assertEquals(render("value=%d", PrintArgument.Integer(Vec(42L), 32)), Right("value=42"))
    assertEquals(render("%s", PrintArgument.Text("hi")), Right("hi"))
    assertEquals(render("%x", PrintArgument.Integer(Vec(255L), 32)), Right("ff"))
    assertEquals(render("%08x", PrintArgument.Integer(Vec(255L), 32)), Right("000000ff"))
    assertEquals(
      render("%f", PrintArgument.Floating(Vec(java.lang.Double.doubleToRawLongBits(1.5)), 64)),
      Right("1.500000")
    )
  }

  test("expands vector lanes and escapes percent") {
    assertEquals(
      render("%v4d", PrintArgument.Integer(Vec(1L, 2L, 3L, 4L), 32)),
      Right("1,2,3,4")
    )
    assertEquals(render("100%%"), Right("100%"))
  }

  test("uppercase fixed-point conversion preserves flags, precision, and special values") {
    def floating(value: Double): PrintArgument =
      PrintArgument.Floating(Vec(java.lang.Double.doubleToRawLongBits(value)), 64)
    assertEquals(render("%+08.2F", floating(1.5)), Right("+0001.50"))
    assertEquals(render("%.1F", floating(-0.0)), Right("-0.0"))
    assertEquals(render("%F", floating(Double.NaN)), Right("NAN"))
    assertEquals(render("%F", floating(Double.NegativeInfinity)), Right("-INF"))
  }

  test("renders half precision and hex floats") {
    assertEquals(render("%f", PrintArgument.Floating(Vec(0x3c00L), 16)), Right("1.000000"))
    assertEquals(
      render("%a", PrintArgument.Floating(Vec(java.lang.Double.doubleToRawLongBits(1.0)), 64)),
      Right("0x1p+0")
    )
  }

  test("rejects exhausted arguments and unsupported conversions") {
    assert(render("%d").isLeft)
    assert(render("%q", PrintArgument.Integer(Vec(1L), 32)).isLeft)
    assert(render(
      "%d",
      PrintArgument.Integer(Vec(1L), 32),
      PrintArgument.Integer(Vec(2L), 32)
    ).isLeft)
  }

  test("the caller controls the output limit, including vector separators") {
    assertEquals(PrintRenderer.render(Print("1234", Vec.empty), 4), Right("1234"))
    assert(PrintRenderer.render(Print("12345", Vec.empty), 4).isLeft)
    assert(PrintRenderer.render(Print("%v2d", Vec(PrintArgument.Integer(Vec(12L, 34L), 32))), 4).isLeft)
    assert(PrintRenderer.render(Print("%5d", Vec(PrintArgument.Integer(Vec(1L), 32))), 4).isLeft)
    assert(PrintRenderer.render(Print("%.5f", Vec(PrintArgument.Floating(Vec(0L), 64))), 4).isLeft)
  }

  test("oversized widths are errors and long format sequences are stack safe") {
    for (format <- List("%99999999999999999d", "%.99999999999999999f", "%v99999999999999999d"))
      assert(render(format, PrintArgument.Integer(Vec(1L), 32)).isLeft)
    val count = 20000
    assertEquals(
      PrintRenderer.render(Print("%d" * count, Vec.fill(count)(PrintArgument.Integer(Vec(1L), 32))), 20000),
      Right("1" * count)
    )
  }

  test("integer precision follows C padding and suppresses zero digits") {
    for (
      (format, expected) <- Seq(
        "[%.0u]"   -> "[]",
        "[%.u]"    -> "[]",
        "[%5.0u]"  -> "[     ]",
        "[%-5.0u]" -> "[     ]",
        "[%05.0u]" -> "[     ]",
        "[%#.0o]"  -> "[0]",
        "[%#.0x]"  -> "[]",
        "[%+.0d]"  -> "[+]",
        "[% .0d]"  -> "[ ]",
        "[%.0i]"   -> "[]"
      )
    ) assertEquals(render(format, PrintArgument.Integer(Vec(0L), 32)), Right(expected), format)
    assertEquals(render("[%.0v4u]", PrintArgument.Integer(Vec(0L, 1L, 0L, 42L), 32)), Right("[,1,,42]"))
    assertEquals(render("[%+.0hhd]", PrintArgument.Integer(Vec(256L), 32)), Right("[+]"))
    assertEquals(render("%u", PrintArgument.Integer(Vec(-1L), 64)), Right("18446744073709551615"))
    assertEquals(render("%d", PrintArgument.Integer(Vec(Long.MinValue), 64)), Right("-9223372036854775808"))
    assertEquals(render("%#x", PrintArgument.Integer(Vec(Long.MinValue), 64)), Right("0x8000000000000000"))
    assertEquals(render("%08.4x", PrintArgument.Integer(Vec(255L), 32)), Right("    00ff"))
  }

  for (
    (format, value, expected) <- Seq(
      ("%0f", 0.6, "0.600000"),
      ("%4G", 0.0000023, "2.3E-06"),
      ("%g", 1.0, "1"),
      ("%#g", 1.0, "1.00000"),
      ("%8g", 0.023, "   0.023"),
      ("%f", Double.PositiveInfinity, "inf"),
      ("%F", Double.PositiveInfinity, "INF"),
      ("%08f", Double.NegativeInfinity, "    -inf"),
      ("%f", Double.NaN, "nan"),
      ("%F", Double.NaN, "NAN"),
      ("%.0f", 2.5, "2"),
      ("%.2f", 2.675, "2.67"),
      ("%+.1f", -0.0, "-0.0"),
      ("%.1a", 1.5, "0x1.8p+0"),
      ("%.13A", -65818.078125, "-0X1.011A140000000P+16"),
      ("%.1a", 1.03125, "0x1.0p+0"),
      ("%.1a", 1.09375, "0x1.2p+0"),
      ("%#.0a", 1.0, "0x1.p+0"),
      ("%.3g", 999.9, "1e+03")
    )
  ) test(s"C floating conversion $format of $value") {
    assertEquals(
      render(format, PrintArgument.Floating(Vec(java.lang.Double.doubleToRawLongBits(value)), 64)),
      Right(expected)
    )
  }

}
