package net.kurobako.gemstone.semantics

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.testing.Suite
import scala.util.Random

class AlgebraIntegersSuite extends Suite {
  private val booleans = Vec(false, true)

  private val values = {
    val random = Random(0x5eedL)
    Vec(0L, 1L, -1L, 2L, 0x7fL, 0x80L, 0xffL, 0x7fffffffL, 0x80000000L, 0xffffffffL, Long.MinValue, Long.MaxValue) ++
      Vec.fill(24)(random.nextLong())
  }

  private def unsigned(value: Long, width: Int): BigInt = BigInt(value) & ((BigInt(1) << width) - 1)
  private def signed(value: Long, width: Int): BigInt   = {
    val bits = unsigned(value, width)
    if (bits.testBit(width - 1)) bits - (BigInt(1) << width) else bits
  }
  private def read(value: Long, width: Int, isSigned: Boolean): BigInt =
    if (isSigned) signed(value, width) else unsigned(value, width)

  test("extend and field match the bit helpers") {
    Vec.range[1, 65].foreach { width =>
      values.foreach { value =>
        assertEquals(Execute.extend(value, width, signed = true), Bits.signExtend(value, width))
        assertEquals(Execute.extend(value, width, signed = false), Bits.truncate(value, width))
        (0 until 64).foreach { offset =>
          assertEquals(
            Execute.field(value, offset, width, signed = true),
            Bits.signedField(value, offset, width)
          )
          assertEquals(
            Execute.field(value, offset, width, signed = false),
            Bits.unsignedField(value, offset, width)
          )
        }
      }
    }
  }

  test("low masks and inserted fields set exactly the field's bits") {
    assertEquals(Execute.lowMask(0L), 0L)
    Vec.range[1, 65].foreach(count => assertEquals(Execute.lowMask(count.toLong), Bits.mask(count)))
    for {
      base   <- values.take(8)
      value  <- values.take(8)
      offset <- 0 until 64
      count  <- 0 to 64 - offset
    } {
      val mask = if (count == 0) 0L else Bits.mask(count.unsafe[Int in 1 ~ 64]) << offset
      assertEquals(
        Execute.insertField(base, value, offset.toLong, count.toLong),
        (base & ~mask) | ((value << offset) & mask)
      )
    }
    for {
      base   <- values.take(8)
      value  <- values.take(8)
      offset <- 0 until 32
      count  <- 1 to 32 - offset
    } {
      val mask = Bits.mask(count.unsafe[Int in 1 ~ 64]) << offset
      assertEquals(
        Execute.placeField(base, value, offset, count.unsafe[Int in 1 ~ 64], 32),
        (base & 0xffffffffL & ~mask) | ((value << offset) & mask)
      )
    }
  }

  test("quotients and extrema read the operands at the width") {
    Vec.range[1, 65].foreach { width =>
      for {
        left     <- values
        right    <- values
        isSigned <- booleans
      } {
        val (a, b)                   = (read(left, width, isSigned), read(right, width, isSigned))
        def low(value: BigInt): Long = (value & Bits.mask(width)).toLong
        assertEquals(
          Execute.quotient(left, right, width, isSigned, remainder = false),
          if (b == 0) 0L else low(a / b)
        )
        assertEquals(
          Execute.quotient(left, right, width, isSigned, remainder = true),
          if (b == 0) 0L else low(a % b)
        )
        val (least, greatest) = (a.min(b), a.max(b))
        assertEquals(
          Execute.extremum(left, right, width, isSigned, maximum = false),
          if (isSigned) low(least) else least.toLong
        )
        assertEquals(
          Execute.extremum(left, right, width, isSigned, maximum = true),
          if (isSigned) low(greatest) else greatest.toLong
        )
      }
    }
  }

  test("clamps bound signed values") {
    values.foreach { value =>
      assertEquals(Execute.clamp(value, lowest = -5L, highest = 7L), value.max(-5L).min(7L))
      Vec.range[1, 65].foreach { width =>
        val lowest = -1L << (width.raw - 1)
        assertEquals(Execute.clampSigned(value, width), value.max(lowest).min(~lowest))
      }
      Vec.range[1, 64].foreach(width =>
        assertEquals(
          Execute.clampUnsigned(value, width),
          value.max(0L).min(Bits.mask(width.widen[Int in 1 ~ 64]))
        )
      )
    }
  }

  test("saturating sums and differences match the reference arithmetic") {
    Vec.range[1, 65].foreach { width =>
      for {
        left     <- values
        right    <- values
        isSigned <- booleans
      } {
        assertEquals(
          Execute.addSaturated(left, right, width, isSigned),
          IntegerArithmetic.addSaturated(left, right, width, isSigned),
          s"$left + $right at $width"
        )
        assertEquals(
          Execute.subtractSaturated(left, right, width, isSigned),
          IntegerArithmetic.subtractSaturated(left, right, width, isSigned),
          s"$left - $right at $width"
        )
      }
    }
  }

  test("a saturated 64-bit sum clamps an exact sum to the width") {
    Vec.range[1, 65].foreach { width =>
      for {
        accumulator <- values
        sum         <- values
      } {
        val left   = Bits.signExtend(accumulator, width)
        val result = left + sum
        val exact  = BigInt(left) + BigInt(sum)
        val lowest = -(BigInt(1) << (width.raw - 1))
        val bound  = exact.max(lowest).min(-lowest - 1).toLong
        assertEquals(Execute.saturated((left ^ result) & (sum ^ result), left, result, width), bound)
      }
    }
  }

  test("absolute differences are exact at the width") {
    Vec.range[1, 64].foreach { width =>
      for {
        left     <- values
        right    <- values
        isSigned <- booleans
      } {
        val difference = (read(left, width, isSigned) - read(right, width, isSigned)).abs
        assertEquals(
          Execute.absoluteDifference(left, right, width.widen[Int in 1 ~ 64], isSigned),
          difference.toLong
        )
      }
    }
  }

  test("dot products sum the extended lane products") {
    def reference(sum: Long, a: Long, b: Long, width: Int, lanes: Int, sa: Boolean, sb: Boolean): Long =
      (0 until lanes).foldLeft(BigInt(sum)) { (total, lane) =>
        total + read(a >>> (lane * width), width, sa) * read(b >>> (lane * width), width, sb)
      }.toLong
    for {
      sum <- values.take(6)
      a   <- values
      b   <- values
    } {
      assertEquals(
        Execute.dot(sum, a, b, width = 8, lanes = 4, leftSigned = true, rightSigned = false),
        reference(sum, a, b, width = 8, lanes = 4, sa = true, sb = false)
      )
      assertEquals(
        Execute.dot(sum, a, b, width = 4, lanes = 8, leftSigned = false, rightSigned = true),
        reference(sum, a, b, width = 4, lanes = 8, sa = false, sb = true)
      )
      assertEquals(
        Execute.dot(sum, a, b, width = 16, lanes = 2, leftSigned = true, rightSigned = true),
        reference(sum, a, b, width = 16, lanes = 2, sa = true, sb = true)
      )
      assertEquals(
        Execute.products(a, b, width = 8, lanes = 4, leftSigned = false, rightSigned = false),
        reference(0L, a, b, width = 8, lanes = 4, sa = false, sb = false)
      )
    }
  }

  test("bit searches find the highest and lowest set bits") {
    Vec.range[1, 65].foreach { width =>
      values.foreach { value =>
        val bits   = unsigned(value, width)
        val search = if (signed(value, width) < 0) bits ^ ((BigInt(1) << width) - 1) else bits
        Vec((bits, false), (search, true)).foreach { (searched, isSigned) =>
          val highest = if (searched == 0) -1L else (searched.bitLength - 1).toLong
          assertEquals(
            Execute.findMostSignificant(value, width, isSigned, fromTop = false, none = -1L),
            highest
          )
          assertEquals(
            Execute.findMostSignificant(value, width, isSigned, fromTop = true, none = -1L),
            if (searched == 0) -1L else width.raw - 1L - highest
          )
        }
        assertEquals(
          Execute.findLeastSignificant(value, width, none = -1L),
          if (bits == 0) -1L else bits.lowestSetBit.toLong
        )
      }
    }
  }
}
