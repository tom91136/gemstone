package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.util.Random

class VideoOracleSuite extends munit.FunSuite {
  private val random = Random(7)
  private val Shifts = Seq[Int in (0, 7, 15)](0, 7, 15)

  private def field(word: Long, select: VideoSelect, signed: Boolean): BigInt = {
    val raw = (word >>> select.offset) & ((1L << select.bits) - 1L)
    BigInt(if (signed && (raw >>> (select.bits - 1)) != 0L) raw - (1L << select.bits) else raw)
  }

  private def oracle(
      left: Long,
      right: Long,
      addend: Long,
      leftSelect: VideoSelect,
      rightSelect: VideoSelect,
      leftSigned: Boolean,
      rightSigned: Boolean,
      negateProduct: Boolean,
      negateAddend: Boolean,
      plusOne: Boolean,
      shift: Int,
      saturate: Boolean
  ): Long = {
    val signed  = leftSigned || rightSigned || negateProduct || negateAddend
    val product = field(left, leftSelect, leftSigned) * field(right, rightSelect, rightSigned)
    val c       = if (!plusOne && !negateProduct && negateAddend) ~addend & 0xffffffffL else addend
    val sum = (if (!plusOne && negateProduct) ~product else product) + (if (signed) BigInt(c.toInt) else BigInt(c)) +
      (if (plusOne || negateProduct || negateAddend) 1 else 0)
    val result = (sum >> shift).toLong
    if (!saturate) result
    else if (signed) Math.min(Math.max(result, Int.MinValue.toLong), Int.MaxValue.toLong)
    else if (java.lang.Long.compareUnsigned(result, 0xffffffffL) > 0) 0xffffffffL
    else result
  }

  private def word(): Long = random.nextInt(4) match {
    case 0 => random.nextLong() & 0xffffffffL
    case 1 => Seq(0L, 1L, 0xffffffffL, 0x80000000L, 0x7fffffffL).apply(random.nextInt(5))
    case _ => random.nextInt().toLong & 0xffffffffL
  }

  test("vmad matches an exact reference over every select, sign, shift and flag") {
    val selects = VideoSelect.values.toSeq
    for (_ <- 0 until 60000) {
      val left, right, addend = word()
      val leftSelect          = selects(random.nextInt(selects.size))
      val rightSelect         = selects(random.nextInt(selects.size))
      val flags               = random.nextInt(1 << 6)
      val shift               = Shifts(random.nextInt(3))
      val expected            = oracle(
        left,
        right,
        addend,
        leftSelect,
        rightSelect,
        (flags & 1) != 0,
        (flags & 2) != 0,
        (flags & 4) != 0,
        (flags & 8) != 0,
        (flags & 16) != 0,
        shift,
        (flags & 32) != 0
      )
      val actual = IntegerArithmetic.videoMultiplyAdd(
        UInt[32](left),
        UInt[32](right),
        UInt[32](addend),
        leftSelect,
        rightSelect,
        (flags & 1) != 0,
        (flags & 2) != 0,
        (flags & 4) != 0,
        (flags & 8) != 0,
        (flags & 16) != 0,
        shift,
        (flags & 32) != 0
      )
      assertEquals(actual.raw, expected & 0xffffffffL, s"$left $right $addend $leftSelect $rightSelect $flags $shift")
    }
  }

  test("clmad takes the low and high words of the carry-less product") {
    for (_ <- 0 until 2000) {
      val left, right = random.nextLong()
      val wide        = BigInt(java.lang.Long.toUnsignedString(right))
      val product     = (0 until 64).foldLeft(BigInt(0))((sum, bit) =>
        if (((left >>> bit) & 1L) != 0L) sum ^ (wide << bit) else sum
      )
      assertEquals(IntegerArithmetic.carrylessLow(UInt[64](left), UInt[64](right)).raw, product.toLong)
      assertEquals(IntegerArithmetic.carrylessHigh(UInt[64](left), UInt[64](right)).raw, (product >> 64).toLong)
    }
  }
}
