package net.kurobako.gemstone.semantics

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.Suite

class AlgebraSuite extends Suite {
  private val values =
    Seq(0L, 1L, -1L, 2L, 0x80L, Long.MinValue, Long.MaxValue, 0x8877665544332211L, 0x0123456789abcdefL)

  private def unsigned(value: Long): BigInt = BigInt(value) & ((BigInt(1) << 64) - 1)

  test("trailing zeros count from the least significant bit up to the width") {
    (1 to 64).foreach { width =>
      val bits = width.refine[Int in 1 ~ 64].fold(message => fail(message), identity)
      values.foreach { value =>
        val digits = java.lang.Long.toBinaryString(value).reverse.padTo(64, '0').take(width)
        assertEquals(Execute.trailingZeros(value, bits), digits.takeWhile(_ == '0').length.toLong, s"$value $width")
      }
    }
  }

  test("minimum and maximum order the operands signed or unsigned") {
    for {
      left  <- values
      right <- values
    } {
      assertEquals(Execute.minimum(left, right, signed = true), if (BigInt(left) <= BigInt(right)) left else right)
      assertEquals(Execute.maximum(left, right, signed = true), if (BigInt(left) >= BigInt(right)) left else right)
      assertEquals(
        Execute.minimum(left, right, signed = false),
        if (unsigned(left) <= unsigned(right)) left else right
      )
      assertEquals(
        Execute.maximum(left, right, signed = false),
        if (unsigned(left) >= unsigned(right)) left else right
      )
    }
  }

  test("bit-field extraction takes the field above the offset and extends it") {
    for {
      value  <- values
      offset <- 0 until 64
      count  <- 0 to 64
    } {
      val modulus = BigInt(1) << count
      val field   = (unsigned(value) >> offset) % modulus
      val signed  = (BigInt(value) >> offset).mod(modulus)
      assertEquals(Execute.bitFieldExtract(value, offset.toLong, count.toLong, signed = false), field.toLong)
      assertEquals(
        Execute.bitFieldExtract(value, offset.toLong, count.toLong, signed = true),
        (if (count > 0 && signed.testBit(count - 1)) signed - modulus else signed).toLong,
        s"$value $offset $count"
      )
    }
  }
}
