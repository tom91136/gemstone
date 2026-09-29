package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.Event.*

class PrintRendererSuite extends munit.FunSuite {
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
    assertEquals(render("%F", floating(Double.NegativeInfinity)), Right("-INFINITY"))
  }

  test("renders half precision and hex floats") {
    assertEquals(render("%f", PrintArgument.Floating(Vec(0x3c00L), 16)), Right("1.000000"))
    assertEquals(
      render("%a", PrintArgument.Floating(Vec(java.lang.Double.doubleToRawLongBits(1.0)), 64)),
      Right("0x1.0p+0")
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
}
