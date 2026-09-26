package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.vm.Event.{Print, PrintArgument}

final class PrintRendererSuite extends munit.FunSuite {
  private def render(format: String, arguments: PrintArgument*): Either[String, String] =
    PrintRenderer.render(Print(format, Vec.from(arguments)))

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
}
