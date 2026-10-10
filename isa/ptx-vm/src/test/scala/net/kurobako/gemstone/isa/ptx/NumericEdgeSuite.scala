package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

class NumericEdgeSuite extends munit.FunSuite {
  for {
    (opcode, operation) <- Vec(
      Opcode.Add -> ((a: Float, b: Float) => a + b),
      Opcode.Sub -> ((a: Float, b: Float) => a - b),
      Opcode.Mul -> ((a: Float, b: Float) => a * b)
    )
    flush <- Vec(false, true)
  }
    test(s"packed float32 ${opcode.name} rounds each half independently, flush=$flush") {
      val inputs = Vec(
        (1.25f, -4.0f, 2.5f, 10.0f),
        (-0.0f, -0.0f, -0.0f, 0.0f),
        (Float.PositiveInfinity, Float.NegativeInfinity, Float.NegativeInfinity, Float.PositiveInfinity),
        (java.lang.Float.MIN_VALUE, -java.lang.Float.MIN_VALUE, java.lang.Float.MIN_VALUE, -java.lang.Float.MIN_VALUE),
        (16777216.0f, -16777216.0f, 1.0f, -1.0f),
        (java.lang.Float.MIN_NORMAL, -java.lang.Float.MIN_NORMAL, 0.5f, 0.5f)
      )
      def bits(value: Float): Long            = java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL
      def pair(low: Float, high: Float): Long = bits(low) | (bits(high) << 32)
      def flushed(value: Float): Float        =
        if (flush && math.abs(value) < java.lang.Float.MIN_NORMAL) math.copySign(0.0f, value) else value
      val code = inputs.zipWithIndex.map { case ((a, b, c, d), index) =>
        s"${opcode.name}${
            if (flush) ".ftz" else ""
          }.f32x2 %result, ${pair(a, b)}, ${pair(c, d)}; st.global.b64 [%out+${index * 8}], %result;"
      }.mkString("\n")
      val kernel = compileKernel(s""".version 8.6
        |.target sm_100
        |.address_size 64
        |.visible .entry packed(.param .u64 output) {
        | .reg .b64 %out, %result;
        | ld.param.u64 %out, [output];
        | $code
        | ret;
        |}""".stripMargin)
      val memory = DeviceMemory(inputs.nonNegativeSize * 8)
      assertEquals(
        execute(
          kernel,
          1,
          1,
          Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
          memory
        ),
        Right(())
      )
      inputs.zipWithIndex.foreach { case ((a, b, c, d), index) =>
        Vec(
          flushed(operation(flushed(a), flushed(c))),
          flushed(operation(flushed(b), flushed(d)))
        ).zipWithIndex.foreach {
          (expected, half) =>
            val actualBits =
              memory.readInt32(memory.start.offset(index.toLong * 8 + half.toLong * 4)).toOption.get
            if (expected.isNaN) assert(java.lang.Float.intBitsToFloat(actualBits).isNaN)
            else assertEquals(actualBits, java.lang.Float.floatToRawIntBits(expected))
        }
      }
    }

  private def run(body: String, outputType: String, width: Int in 1 ~ 8): Long = {
    val source = s""".version 8.0
      |.target sm_89
      |.address_size 64
      |.visible .entry numeric(.param .u64 output) {
      | .reg .u64 %out;
      | .reg .b32 %a, %b;
      | .reg .b64 %wide, %other;
      | .reg .pred %p;
      | .reg .$outputType %result;
      | ld.param.u64 %out, [output];
      | $body
      | st.global.b${width.raw * 8} [%out], %result;
      | ret;
      |}
      |""".stripMargin
    val memory = DeviceMemory(8)
    memory.writeLittleEndian(
      memory.start,
      0xa5a5a5a5a5a5a5a5L,
      8
    ).fold(error => fail(error.toString), identity)
    execute(
      compileKernel(source),
      1,
      1,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
      .fold(error => fail(error.message), identity)
    memory.readLittleEndian(memory.start, width).fold(error => fail(error.toString), identity)
  }

  test("float conversions cover subnormals, halfway values, overflow, signed zeros and NaNs") {
    NumericCases.f64ToF32.foreach { (input, expected) =>
      val result = run(s"mov.b64 %wide, $input; cvt.rn.f32.f64 %result, %wide;", "f32", 4)
      assertEquals(result, expected, clues(input))
    }
    NumericCases.f32ToF64.foreach { (input, expected) =>
      val result = run(s"mov.b32 %a, $input; cvt.f64.f32 %result, %a;", "f64", 8)
      if ((expected & 0x7ff0000000000000L) == 0x7ff0000000000000L && (expected & 0x000fffffffffffffL) != 0)
        assert((result & 0x7ff0000000000000L) == 0x7ff0000000000000L && (result & 0x000fffffffffffffL) != 0)
      else assertEquals(result, expected, clues(input))
    }
    NumericCases.f32ToF16.foreach { (input, expected) =>
      val result = run(s"mov.b32 %a, $input; cvt.rn.f16.f32 %result, %a;", "b16", 2)
      assertEquals(result, expected, clues(input))
    }
  }

  for {
    (opcode, cases) <- Vec("add" -> NumericCases.packedAdd, "mul" -> NumericCases.packedMultiply)
    alias           <- Vec(false, true)
  }
    test(s"packed half $opcode preserves independent lanes and special values, alias=$alias") {
      cases.foreach { (left, right, expected) =>
        val destination = if (alias) "%a" else "%result"
        val copy        = if (alias) "mov.b32 %result, %a;" else ""
        val result      =
          run(s"mov.b32 %a, $left; mov.b32 %b, $right; $opcode.rn.f16x2 $destination, %a, %b; $copy", "b32", 4)
        assertEquals(result, expected, clues(left, right))
      }
    }

  for {
    maximum <- Vec(false, true)
    alias   <- Vec(false, true)
  }
    test(s"float extrema preserve NaN selection and zero signs, maximum=$maximum alias=$alias") {
      NumericCases.f32Extrema.foreach { (left, right, minimum, max) =>
        val opcode      = if (maximum) "max" else "min"
        val destination = if (alias) "%a" else "%result"
        val copy        = if (alias) "mov.b32 %result, %a;" else ""
        val result = run(s"mov.b32 %a, $left; mov.b32 %b, $right; $opcode.f32 $destination, %a, %b; $copy", "b32", 4)
        assertEquals(result, if (maximum) max else minimum, clues(left, right))
      }
    }

  for (opcode <- Vec("min", "max"))
    test(s"$opcode.NaN keeps its explicit NaN propagation policy") {
      NumericCases.f32Extrema.foreach { (left, right, minimum, maximum) =>
        val result   = run(s"mov.b32 %a, $left; mov.b32 %b, $right; $opcode.NaN.f32 %result, %a, %b;", "b32", 4)
        val hasNaN   = IEEE754.isNaN(left, 32) || IEEE754.isNaN(right, 32)
        val expected = if (hasNaN) VMArithmetic.CanonicalNaN32 else if (opcode == "min") minimum else maximum
        assertEquals(result, expected, clues(left, right))
      }
    }

  for (maximum <- Vec(false, true))
    test(s"binary64 extrema select numbers over NaNs and retain zero signs, maximum=$maximum") {
      val opcode = if (maximum) "max" else "min"
      val cases  = Vec(
        (0x7ff8000000000042L, 0x3ff0000000000000L, 0x3ff0000000000000L),
        (0x3ff0000000000000L, 0x7ff0000000000042L, 0x3ff0000000000000L),
        (0L, Long.MinValue, if (maximum) 0L else Long.MinValue),
        (Long.MinValue, 0L, if (maximum) 0L else Long.MinValue)
      )
      cases.foreach { (left, right, expected) =>
        assertEquals(
          run(s"mov.b64 %wide, $left; mov.b64 %other, $right; $opcode.f64 %result, %wide, %other;", "b64", 8),
          expected
        )
      }
    }

  for (maximum <- Vec(false, true))
    test(s"packed half extrema apply NaN selection separately to each lane, maximum=$maximum") {
      val opcode = if (maximum) "max" else "min"
      val body   = s"mov.b32 %a, 0x7e008000; mov.b32 %b, 0x3c000000; $opcode.f16x2 %result, %a, %b;"
      assertEquals(run(body, "b32", 4), if (maximum) 0x3c000000L else 0x3c008000L)
    }

  for (unordered <- Vec(false, true))
    test(s"float comparisons retain ordered and unordered NaN behaviour, unordered=$unordered") {
      NumericCases.f32Comparisons.foreach { (left, right, equal, notEqualUnordered) =>
        val comparison = if (unordered) "neu" else "eq"
        val result     = run(
          s"mov.b32 %a, $left; mov.b32 %b, $right; setp.$comparison.f32 %p, %a, %b; selp.u32 %result, 1, 0, %p;",
          "b32",
          4
        )
        assertEquals(result, if (if (unordered) notEqualUnordered else equal) 1L else 0L, clues(left, right))
      }
    }
}
