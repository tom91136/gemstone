package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.semantics.*

class IntegerMultiplyAddSuite extends munit.FunSuite {
  private val register = Register(0)
  private val operand  = Value.Immediate(0L)

  private val OperandWidths = Vec(16, 32, 64)
  private val Signs         = Vec(false, true)

  private val SaturatingHigh = IntegerMultiplyAddOperation.HighSum(width = 32, signed = true, saturate = true)

  private val SaturatingNarrowHigh = IntegerMultiplyAddOperation.Narrow(high = true, signed = true, saturate = true)

  /** The staged semantics with the carry out always computed. */
  private def staged(left: Long, right: Long, addend: Long, operation: IntegerMultiplyAddOperation, carryIn: Boolean)
      : (Long, Boolean) = {
    val probe   = Probe(left, right, addend, if (carryIn) 1L else 0L)
    val outputs = operation match {
      case IntegerMultiplyAddOperation.Low(operandWidth, resultWidth, usesCarry, _) =>
        IntegerSemantics.multiplyAddLowCarry(
          Execute,
          operandWidth.widen,
          resultWidth.widen,
          usesCarry,
          carryOut = true
        )(sinks = probe, destination = register, first = operand, second = operand, third = operand)
      case IntegerMultiplyAddOperation.High(operandWidth, resultWidth, _) =>
        IntegerSemantics.multiplyAddHigh(Execute, operandWidth.widen, resultWidth.widen, carryOut = true)(
          sinks = probe,
          destination = register,
          first = operand,
          second = operand,
          third = operand
        )
      case other => fail(s"$other has no carry")
    }
    (outputs.head, outputs(1) != 0L)
  }

  private val boundaryValues = Vec(
    0L,
    1L,
    2L,
    Int.MaxValue.toLong,
    0xffffffffL,
    Long.MaxValue,
    Long.MinValue,
    -2L,
    -1L
  )

  private def unsigned(value: Long, width: ScalarBitWidth): BigInt = {
    val bits = BigInt(java.lang.Long.toUnsignedString(value))
    bits & ((BigInt(1) << width) - 1)
  }

  private def reference(
      left: Long,
      right: Long,
      addend: Long,
      operation: IntegerMultiplyAddOperation,
      carryIn: Boolean
  ): (Long, Boolean) = {
    val (operandWidth, resultWidth, usesCarry, high) = operation match {
      case IntegerMultiplyAddOperation.Low(operandWidth, resultWidth, usesCarry, _) =>
        (operandWidth, resultWidth, usesCarry, false)
      case IntegerMultiplyAddOperation.High(operandWidth, resultWidth, _) =>
        (operandWidth, resultWidth, true, true)
      case other => fail(s"$other has no carry")
    }
    val product  = unsigned(left, operandWidth) * unsigned(right, operandWidth)
    val selected = if (high) product >> operandWidth else product
    val sum      = selected + unsigned(addend, resultWidth) + (if (usesCarry && carryIn) 1 else 0)
    val mask     = (BigInt(1) << resultWidth) - 1
    (sum & mask).toLong -> ((sum >> resultWidth) != 0)
  }

  test("staged multiply-add matches unsigned wide arithmetic") {
    val operations = Vec(
      IntegerMultiplyAddOperation.Low(
        operandWidth = 32,
        resultWidth = 32,
        carryIn = false,
        carryOut = false
      ),
      IntegerMultiplyAddOperation.Low(
        operandWidth = 32,
        resultWidth = 64,
        carryIn = false,
        carryOut = false
      ),
      IntegerMultiplyAddOperation.Low(
        operandWidth = 64,
        resultWidth = 64,
        carryIn = true,
        carryOut = true
      ),
      IntegerMultiplyAddOperation.High(
        operandWidth = 32,
        resultWidth = 32,
        carryOut = true
      ),
      IntegerMultiplyAddOperation.High(
        operandWidth = 64,
        resultWidth = 64,
        carryOut = true
      )
    )
    val random = scala.util.Random(0x5eedL)
    val values = boundaryValues ++ Vec.fill(256)(random.nextLong())

    operations.foreach { operation =>
      values.indices.foreach { index =>
        val left    = values(index)
        val right   = values((index * 37 + 11) % values.size)
        val addend  = values((index * 73 + 19) % values.size)
        val carryIn = (index & 1) != 0
        assertEquals(
          staged(left, right, addend, operation, carryIn),
          reference(left, right, addend, operation, carryIn),
          s"operation=$operation left=$left right=$right addend=$addend carryIn=$carryIn"
        )
      }
    }
  }

  private def exact(value: Long, width: Int, signed: Boolean): BigInt = {
    val masked = BigInt(java.lang.Long.toUnsignedString(value)) & ((BigInt(1) << width) - 1)
    if (signed && masked.testBit(width - 1)) masked - (BigInt(1) << width) else masked
  }

  private def low(value: BigInt, width: Int): Long = (value & ((BigInt(1) << width) - 1)).toLong

  private def clamp(value: BigInt): Long = value.max(BigInt(Int.MinValue)).min(BigInt(Int.MaxValue)).toLong &
    0xffffffffL

  // PTX ISA 9.7.1.4, 9.7.1.6 and 9.7.1.7: t = a * b; mad.hi adds c to t<2n-1..n>, mad.wide to t, mul24 and mad24 use
  // 24-bit operands and t<31..0> or t<47..16>; .sat clamps the signed 32-bit sum.
  test("mad.hi, mad.wide, mul24 and mad24 match the PTX ISA") {
    val random = scala.util.Random(0x24L)
    val values = boundaryValues ++ Vec(0x7fffffL, 0x800000L, 0xffffffL, 0x80000000L) ++ Vec.fill(128)(random.nextLong())
    def run(operation: IntegerMultiplyAddOperation, a: Long, b: Long, c: Long): Long = {
      val probe = Probe(a, b, c)
      (operation match {
        case IntegerMultiplyAddOperation.HighSum(width, signed, saturate) =>
          IntegerSemantics.multiplyAddHighSum(Execute, width.widen, signed, saturate)(
            probe,
            register,
            operand,
            operand,
            operand
          )
        case IntegerMultiplyAddOperation.Wide(width, signed) =>
          IntegerSemantics.multiplyAddWide(Execute, width.widen, signed)(probe, register, operand, operand, operand)
        case IntegerMultiplyAddOperation.Narrow(high, signed, saturate) =>
          IntegerSemantics.multiplyAddNarrow(
            Execute,
            high,
            signed,
            saturate
          )(probe, register, operand, operand, operand)
        case other => fail(s"unexpected $other")
      }).head
    }
    for {
      index <- values.indices
      a = values(index)
      b = values((index * 37 + 11) % values.size)
      c = values((index * 73 + 19) % values.size)
    } {
      for {
        width  <- OperandWidths
        signed <- Signs
      } {
        val product = exact(a, width, signed) * exact(b, width, signed)
        assertEquals(
          run(IntegerMultiplyAddOperation.HighSum(width.unsafe[ScalarBitWidth], signed, saturate = false), a, b, c),
          low((product >> width) + exact(c, width, signed), width),
          s"mad.hi width=$width signed=$signed $a $b $c"
        )
        if (width < 64)
          assertEquals(
            run(IntegerMultiplyAddOperation.Wide(width.unsafe[Int in (16, 32)], signed), a, b, c),
            low(product + exact(c, 2 * width, signed), 2 * width),
            s"mad.wide width=$width signed=$signed $a $b $c"
          )
      }
      val signedProduct = exact(a, 32, signed = true) * exact(b, 32, signed = true)
      assertEquals(
        run(SaturatingHigh, a, b, c),
        clamp((signedProduct >> 32) + exact(c, 32, signed = true)),
        s"mad.hi.sat.s32 $a $b $c"
      )
      for (signed <- Signs) {
        val narrow = exact(a, 24, signed) * exact(b, 24, signed)
        assertEquals(
          run(IntegerMultiplyAddOperation.Narrow(high = false, signed = signed, saturate = false), a, b, c),
          low(narrow + exact(c, 32, signed), 32),
          s"mad24.lo signed=$signed $a $b $c"
        )
        assertEquals(
          run(IntegerMultiplyAddOperation.Narrow(high = true, signed = signed, saturate = false), a, b, c),
          low((narrow >> 16) + exact(c, 32, signed), 32),
          s"mad24.hi signed=$signed $a $b $c"
        )
      }
      assertEquals(
        run(SaturatingNarrowHigh, a, b, c),
        clamp(
          exact(low((exact(a, 24, signed = true) * exact(b, 24, signed = true)) >> 16, 32), 32, signed = true) +
            exact(c, 32, signed = true)
        ),
        s"mad24.hi.sat.s32 $a $b $c"
      )
    }
  }
}
