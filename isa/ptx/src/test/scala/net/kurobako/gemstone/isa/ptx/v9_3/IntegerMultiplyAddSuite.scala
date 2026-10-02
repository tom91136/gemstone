package net.kurobako.gemstone.isa.ptx.v9_3

import VMIR.*
import net.kurobako.gemstone.constraint.*

class IntegerMultiplyAddSuite extends munit.FunSuite {
  private val boundaryValues = Vector(
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

  private def unsigned(value: Long, width: Int where (Between[8, 64] & MultipleOf[8])): BigInt = {
    val bits = BigInt(java.lang.Long.toUnsignedString(value))
    bits & ((BigInt(1) << width.raw) - 1)
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
      case IntegerMultiplyAddOperation.High(operandWidth, resultWidth, usesCarry, _) =>
        (operandWidth, resultWidth, usesCarry, true)
    }
    val product  = unsigned(left, operandWidth) * unsigned(right, operandWidth)
    val selected = if (high) product >> operandWidth.raw else product
    val sum      = selected + unsigned(addend, resultWidth) + (if (usesCarry && carryIn) 1 else 0)
    val mask     = (BigInt(1) << resultWidth.raw) - 1
    (sum & mask).longValue -> ((sum >> resultWidth.raw) != 0)
  }

  test("primitive multiply-add matches unsigned wide arithmetic") {
    val operations = Vector(
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
        carryIn = true,
        carryOut = true
      ),
      IntegerMultiplyAddOperation.High(
        operandWidth = 64,
        resultWidth = 64,
        carryIn = true,
        carryOut = true
      )
    )
    val random = scala.util.Random(0x5eedL)
    val values = boundaryValues ++ Vector.fill(256)(random.nextLong())

    operations.foreach { operation =>
      values.indices.foreach { index =>
        val left    = values(index)
        val right   = values((index * 37 + 11) % values.size)
        val addend  = values((index * 73 + 19) % values.size)
        val carryIn = (index & 1) != 0
        assertEquals(
          VMSemantics.integerMultiplyAdd(left, right, addend, operation, carryIn),
          reference(left, right, addend, operation, carryIn),
          s"operation=$operation left=$left right=$right addend=$addend carryIn=$carryIn"
        )
      }
    }
  }
}
