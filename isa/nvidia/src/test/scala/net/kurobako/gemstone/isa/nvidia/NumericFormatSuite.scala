package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.compiletime.testing.typeCheckErrors

class NumericFormatSuite extends munit.FunSuite {
  private def execute(instructions: Vec[Instruction]): Long = {
    val memory  = ArrayCheckedMemory.allocate(8)
    val program = instructions ++ Vec(
      Instruction.Store(
        Value.Immediate(memory.allocation.start.raw),
        Value.Register("%r0"),
        64,
        MemoryAccess.Global(false)
      ),
      Instruction.Return
    )
    val kernel   = Kernel("numeric", Vec.empty, ExecutableProgram(program.map(PredicatedInstruction(_, None))))
    val geometry = LaunchGeometry.forLaunch(Dimensions.One, Dimensions.One).toOption.get
    assertEquals(VMInterpreter.execute(kernel, geometry, Map.empty, memory), Right(()))
    memory.readLittleEndian(memory.allocation.start, 8).toOption.get
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
      propagate <- Vec(false, true)
    } {
      val result = execute(Vec(Instruction.FloatBinary(
        operation,
        "%r0",
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

  test("conversion formats retain IEEE widths and distinguish BF16") {
    val width: Int in (16, 32, 64) = ConversionType.F16.format match {
      case NumericFormat.IEEE(bits) => bits
      case other                    => fail(s"expected IEEE format, got $other")
    }
    assertEquals(width.raw, 16)
    assertEquals(ConversionType.BF16.format, NumericFormat.BF16)
    assert(!(ConversionType.F16.format eq ConversionType.BF16.format))
  }

  test("conversions preserve signedness, IEEE versus BF16 decoding, and directed rounding") {
    val cases = Vec(
      (0x3c00L, ConversionType.F16, ConversionType.F32, None, 0x3f800000L),
      (0x3f80L, ConversionType.F16, ConversionType.F32, None, 0x3ff00000L),
      (0x3f80L, ConversionType.BF16, ConversionType.F32, None, 0x3f800000L),
      (0x80000000L, ConversionType.F32, ConversionType.BF16, None, 0x8000L),
      (0x3f808000L, ConversionType.F32, ConversionType.BF16, Some(ConversionRounding.RN), 0x3f80L),
      (0x3f808000L, ConversionType.F32, ConversionType.BF16, Some(ConversionRounding.RP), 0x3f81L),
      (0xffL, ConversionType.S8, ConversionType.S32, None, 0xffffffffL),
      (0xffL, ConversionType.U8, ConversionType.F32, None, 0x437f0000L),
      (-1L, ConversionType.S64, ConversionType.F32, None, 0xbf800000L),
      (-1L, ConversionType.B64, ConversionType.F32, None, 0xbf800000L),
      (-1L, ConversionType.U64, ConversionType.F32, Some(ConversionRounding.RN), 0x5f800000L),
      (-1L, ConversionType.U64, ConversionType.F32, Some(ConversionRounding.RZ), 0x5f7fffffL),
      (0x3fc00000L, ConversionType.F32, ConversionType.F32, Some(ConversionRounding.RNI), 0x40000000L),
      (0x3fc00000L, ConversionType.F32, ConversionType.F32, Some(ConversionRounding.RZI), 0x3f800000L),
      (0x3ff0000010000000L, ConversionType.F64, ConversionType.F32, Some(ConversionRounding.RN), 0x3f800000L),
      (0x3ff0000010000000L, ConversionType.F64, ConversionType.F32, Some(ConversionRounding.RP), 0x3f800001L)
    )
    cases.foreach { case (value, source, destination, rounding, expected) =>
      assertEquals(
        execute(Vec(Instruction.Convert("%r0", Value.Immediate(value), source, destination, rounding))),
        expected,
        clues(value, source, destination, rounding)
      )
    }
  }

  test("comparisons preserve unordered NaNs, signed zero, integer widths and unsigned ordering") {
    val cases = Vec(
      (ConversionType.F16, 0x7f80L, 0x3c00L, Comparison.Ordered, false),
      (ConversionType.BF16, 0x7f80L, 0x3f80L, Comparison.Ordered, true),
      (ConversionType.BF16, 0x7fc1L, 0x3f80L, Comparison.NotEqual, false),
      (ConversionType.F32, 0x7fc00001L, 0L, Comparison.NotEqualUnordered, true),
      (ConversionType.F64, 0x7ff8000000000001L, 0L, Comparison.LessThan, false),
      (ConversionType.F16, 0x8000L, 0L, Comparison.Equal, true),
      (ConversionType.F32, 0x80000000L, 0L, Comparison.Equal, true),
      (ConversionType.F64, Long.MinValue, 0L, Comparison.Equal, true),
      (ConversionType.BF16, 0x8000L, 0L, Comparison.Equal, true),
      (ConversionType.S8, 0xffL, 1L, Comparison.LessThan, true),
      (ConversionType.U8, 0x1ffL, 0xffL, Comparison.Equal, true),
      (ConversionType.B16, 0xffffL, 0L, Comparison.GreaterThan, true),
      (ConversionType.U64, -1L, 0L, Comparison.GreaterThan, true),
      (ConversionType.S64, -1L, 0L, Comparison.GreaterThan, false)
    )
    cases.foreach { case (numericType, left, right, comparison, expected) =>
      val result = execute(Vec(
        Instruction.SetPredicate("%p0", comparison, numericType.format, Value.Immediate(left), Value.Immediate(right)),
        Instruction.Select("%r0", Value.Immediate(1), Value.Immediate(0), "%p0", 64)
      ))
      assertEquals(result, if (expected) 1L else 0L, clues(numericType, left, right, comparison))
    }
  }

  test("integral floating conversion preserves NaN for every IEEE destination") {
    Vec(ConversionType.F16, ConversionType.F32, ConversionType.F64).foreach { destination =>
      Vec(ConversionRounding.RNI, ConversionRounding.RZI, ConversionRounding.RMI, ConversionRounding.RPI).foreach {
        rounding =>
          val result = execute(Vec(
            Instruction.Convert("%f0", Value.Immediate(-1L), ConversionType.F32, destination, Some(rounding)),
            Instruction.SetPredicate(
              "%p0",
              Comparison.Unordered,
              destination.format,
              Value.Register("%f0"),
              Value.Immediate(0)
            ),
            Instruction.Select("%r0", Value.Immediate(1), Value.Immediate(0), "%p0", 64)
          ))
          assertEquals(result, 1L, clues(destination, rounding))
      }
    }
  }
}
