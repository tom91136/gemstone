package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import programs.*
import scala.compiletime.testing.typeCheckErrors

class NumericFormatSuite extends munit.FunSuite {
  private val Flags = Vec(false, true)

  private val IntegralRoundings: Vec[ConversionRounding] =
    Vec(ConversionRounding.RNI, ConversionRounding.RZI, ConversionRounding.RMI, ConversionRounding.RPI)

  private def run(instructions: Vec[Instruction]): Long = {
    val memory  = RegionMemory(AllocationId(1) -> 8)
    val program =
      instructions ++ Vec(
        Instruction.Store(immediate(RegionMemory.Base), register(0), 64, MemoryAccess.Global(false)),
        Instruction.Return
      )
    assertEquals(execute(program, memory), Right(()))
    LittleEndian.decode(memory.bytes(AllocationId(1)), 0, 8)
  }

  test("scalar extrema retain their NaN policy when floating controls are introduced") {
    val formats: Vec[(Int in (16, 32, 64), Long, Long)] = Vec(
      (16, 0x7e01L, 0x3c00L),
      (32, 0x7fc01234L, 0x3f800000L),
      (64, 0x7ff8000000001234L, 0x3ff0000000000000L)
    )
    for {
      (width, nan, one)            <- formats
      (left, right)                <- Vec(nan -> one, one -> nan)
      (operation, numberPreferred) <- Vec(
        FloatOperation.Minimum       -> false,
        FloatOperation.Maximum       -> false,
        FloatOperation.MinimumNumber -> true,
        FloatOperation.MaximumNumber -> true
      )
      propagate <- Flags
    } {
      val result = run(Vec(Instruction.FloatBinary(
        operation,
        r(0),
        Value.Immediate(left),
        Value.Immediate(right),
        width,
        FloatControl(propagateNaN = propagate)
      )))
      if (numberPreferred && !propagate) assertEquals(result, one)
      else assert(IEEE754.toDouble(result, width).isNaN)
    }
  }

  test("IEEE and integer format constructors reject invalid widths at the type boundary") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.isa.nvidia.VMIR.*
      NumericFormat.IEEE(8)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.isa.nvidia.VMIR.*
      NumericFormat.Unsigned(24)
    """).nonEmpty)
  }

  test("conversions preserve signedness, IEEE versus BF16 decoding, and directed rounding") {
    val cases = Vec(
      (0x3c00L, NumericFormat.IEEE(16), NumericFormat.IEEE(32), None, 0x3f800000L),
      (0x3f80L, NumericFormat.IEEE(16), NumericFormat.IEEE(32), None, 0x3ff00000L),
      (0x3f80L, NumericFormat.BF16, NumericFormat.IEEE(32), None, 0x3f800000L),
      (0x80000000L, NumericFormat.IEEE(32), NumericFormat.BF16, None, 0x8000L),
      (0x3f808000L, NumericFormat.IEEE(32), NumericFormat.BF16, Some(ConversionRounding.RN), 0x3f80L),
      (0x3f808000L, NumericFormat.IEEE(32), NumericFormat.BF16, Some(ConversionRounding.RP), 0x3f81L),
      (0xffL, NumericFormat.Signed(8), NumericFormat.Signed(32), None, 0xffffffffL),
      (0xffL, NumericFormat.Unsigned(8), NumericFormat.IEEE(32), None, 0x437f0000L),
      (-1L, NumericFormat.Signed(64), NumericFormat.IEEE(32), None, 0xbf800000L),
      (-1L, NumericFormat.Bits(64), NumericFormat.IEEE(32), None, 0xbf800000L),
      (-1L, NumericFormat.Unsigned(64), NumericFormat.IEEE(32), Some(ConversionRounding.RN), 0x5f800000L),
      (-1L, NumericFormat.Unsigned(64), NumericFormat.IEEE(32), Some(ConversionRounding.RZ), 0x5f7fffffL),
      (0x3fc00000L, NumericFormat.IEEE(32), NumericFormat.IEEE(32), Some(ConversionRounding.RNI), 0x40000000L),
      (0x3fc00000L, NumericFormat.IEEE(32), NumericFormat.IEEE(32), Some(ConversionRounding.RZI), 0x3f800000L),
      (0x3ff0000010000000L, NumericFormat.IEEE(64), NumericFormat.IEEE(32), Some(ConversionRounding.RN), 0x3f800000L),
      (0x3ff0000010000000L, NumericFormat.IEEE(64), NumericFormat.IEEE(32), Some(ConversionRounding.RP), 0x3f800001L)
    )
    cases.foreach { case (value, source, destination, rounding, expected) =>
      assertEquals(
        run(Vec(Instruction.Convert(r(0), Value.Immediate(value), source, destination, rounding))),
        expected,
        clues(value, source, destination, rounding)
      )
    }
  }

  test("comparisons preserve unordered NaNs, signed zero, integer widths and unsigned ordering") {
    val cases = Vec(
      (NumericFormat.IEEE(16), 0x7f80L, 0x3c00L, Comparison.Ordered, false),
      (NumericFormat.BF16, 0x7f80L, 0x3f80L, Comparison.Ordered, true),
      (NumericFormat.BF16, 0x7fc1L, 0x3f80L, Comparison.NotEqual, false),
      (NumericFormat.IEEE(32), 0x7fc00001L, 0L, Comparison.NotEqualUnordered, true),
      (NumericFormat.IEEE(64), 0x7ff8000000000001L, 0L, Comparison.LessThan, false),
      (NumericFormat.IEEE(16), 0x8000L, 0L, Comparison.Equal, true),
      (NumericFormat.IEEE(32), 0x80000000L, 0L, Comparison.Equal, true),
      (NumericFormat.IEEE(64), Long.MinValue, 0L, Comparison.Equal, true),
      (NumericFormat.BF16, 0x8000L, 0L, Comparison.Equal, true),
      (NumericFormat.Signed(8), 0xffL, 1L, Comparison.LessThan, true),
      (NumericFormat.Unsigned(8), 0x1ffL, 0xffL, Comparison.Equal, true),
      (NumericFormat.Bits(16), 0xffffL, 0L, Comparison.GreaterThan, true),
      (NumericFormat.Unsigned(64), -1L, 0L, Comparison.GreaterThan, true),
      (NumericFormat.Signed(64), -1L, 0L, Comparison.GreaterThan, false)
    )
    cases.foreach { case (numericType, left, right, comparison, expected) =>
      val result = run(Vec(
        Instruction.SetPredicate(p(0), comparison, numericType, Value.Immediate(left), Value.Immediate(right)),
        Instruction.Select(r(0), Value.Immediate(1), Value.Immediate(0), p(0), 64)
      ))
      assertEquals(result, if (expected) 1L else 0L, clues(numericType, left, right, comparison))
    }
  }

  test("integral floating conversion preserves NaN for every IEEE destination") {
    Vec(NumericFormat.IEEE(16), NumericFormat.IEEE(32), NumericFormat.IEEE(64)).foreach { destination =>
      IntegralRoundings.foreach { rounding =>
        val result = run(Vec(
          Instruction.Convert(r(1), Value.Immediate(-1L), NumericFormat.IEEE(32), destination, Some(rounding)),
          Instruction.SetPredicate(
            p(0),
            Comparison.Unordered,
            destination,
            register(1),
            Value.Immediate(0)
          ),
          Instruction.Select(r(0), Value.Immediate(1), Value.Immediate(0), p(0), 64)
        ))
        assertEquals(result, 1L, clues(destination, rounding))
      }
    }
  }
}
