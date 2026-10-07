package net.kurobako.gemstone.binary

import Widths.*
import net.kurobako.gemstone.constraint.*
import scala.compiletime.testing.typeCheckErrors

class WidthPropagationSuite extends munit.FunSuite {
  test("runtime bit extraction retains its guaranteed bounds") {
    assertEquals(
      typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def decoded(bytes: Array[Byte]): Int in 0 ~ 65535 = LittleEndian.decodeUInt16(bytes, 0)
      def byte(value: Long): Int in 0 ~ 255 = Bits.low8Int(value)
      def short(value: Long): Int in 0 ~ 65535 = Bits.low16Int(value)
      def count(value: Long): Int in 0 ~ 64 = Bits.bitCount(value, 64)
      def field(value: Long): Int >= 0 = Bits.unsignedIntField(value, 0, 31)
    """),
      Nil
    )
  }

  test("floating bit widths retain finite membership") {
    for (width <- Seq[Int in (16, 32, 64)](16, 32, 64)) {
      val bytes: Int in (2, 4, 8) = width.toFloatByteWidth
      assertEquals(bytes.raw * 8, width.raw)
    }
  }
}
