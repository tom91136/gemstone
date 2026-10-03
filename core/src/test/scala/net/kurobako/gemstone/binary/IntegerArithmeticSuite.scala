package net.kurobako.gemstone.binary

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.util.Random

class IntegerArithmeticSuite extends munit.FunSuite {
  test("primitive carry, borrow, high products and saturation match arbitrary-precision arithmetic") {
    val random = Random(0x1937L)
    val edges  = Vec(0L, 1L, -1L, Long.MinValue, Long.MaxValue, 0xffffffffL, 0x100000000L)
    val inputs = edges.flatMap(a => edges.flatMap(b => edges.map(c => (a, b, c)))) ++
      Vec.fill(400)((random.nextLong(), random.nextLong(), random.nextLong()))
    Vec.range[1, 65].foreach { width =>
      val modulus                       = BigInt(1) << width.raw
      val mask                          = modulus - 1
      def unsigned(value: Long): BigInt = BigInt(value) & mask
      def signed(value: Long): BigInt   = {
        val bits = unsigned(value)
        if (bits.testBit(width.raw - 1)) bits - modulus else bits
      }
      inputs.foreach { (a, b, c) =>
        for (carry <- Vec(false, true)) {
          val sum        = unsigned(a) + unsigned(b) + (if (carry) 1 else 0)
          val difference = unsigned(a) - unsigned(b) - (if (carry) 1 else 0)
          assertEquals(IntegerArithmetic.addUnsigned(a, b, carry, width), ((sum & mask).toLong, sum >= modulus))
          assertEquals(
            IntegerArithmetic.subtractUnsigned(a, b, carry, width),
            ((difference & mask).toLong, difference < 0)
          )
        }
        for (isSigned <- Vec(false, true)) {
          val left                           = if (isSigned) signed(a) else unsigned(a)
          val right                          = if (isSigned) signed(b) else unsigned(b)
          val addend                         = if (isSigned) signed(c) else unsigned(c)
          val minimum                        = if (isSigned) -(modulus >> 1) else BigInt(0)
          val maximum                        = if (isSigned) (modulus >> 1) - 1 else mask
          def saturated(value: BigInt): Long = value.max(minimum).min(maximum).toLong
          assertEquals(IntegerArithmetic.multiplyHigh(a, b, width, isSigned), ((left * right) >> width.raw).toLong)
          assertEquals(IntegerArithmetic.addSaturated(a, b, width, isSigned), saturated(left + right))
          assertEquals(IntegerArithmetic.subtractSaturated(a, b, width, isSigned), saturated(left - right))
          assertEquals(
            IntegerArithmetic.multiplyAddSaturated(a, b, c, width, isSigned),
            saturated(left * right + addend)
          )
        }
      }
    }
  }
}
