package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*
import scala.util.Random

class MinMaxSuite extends munit.FunSuite {
  private def hex(bits: Long): String = s"0x${java.lang.Long.toHexString(bits)}"

  test("CUDA 11.8 sm_35 extended IMNMX implements signed and unsigned 64-bit min/max") {
    Vec(
      (
        "minmax_probe",
        "minmaxSm35",
        true,
        Vec(
          (0x00000001ffffffffL, 0x0000000200000000L),
          (0x0000000200000000L, 0x00000001ffffffffL),
          (0x00000002ffffffffL, 0x0000000200000001L),
          (0x0000000100000001L, 0x00000001ffffffffL),
          (0L, -1L),
          (-1L, Long.MinValue),
          (Long.MaxValue, Long.MinValue)
        )
      ),
      (
        "minmax_signed_probe",
        "minmaxSignedSm35",
        false,
        Vec(
          (Long.MinValue, Long.MaxValue),
          (Long.MaxValue, Long.MinValue),
          (-1L, 0L),
          (0L, -1L),
          (Long.MinValue, -1L),
          (0x0000000200000001L, 0x00000001ffffffffL),
          (0x0000000100000001L, 0x00000001ffffffffL)
        )
      )
    ).foreach { (function, listing, unsigned, vectors) =>
      val kernel = load(35, Fixture.listing(listing), function)
      vectors.foreach { (a, b) =>
        val ordered = if (unsigned) java.lang.Long.compareUnsigned(a, b) <= 0 else a <= b
        assertEquals(
          kernel.launch(1)(Arg.zeros(16), Arg.u64(a), Arg.u64(b)).words(argument = 0, width = 8),
          if (ordered) Vec(a, b) else Vec(b, a),
          s"$function a=$a b=$b"
        )
      }
    }
  }

  test("Kepler VMNMX preserves signedness and secondary min, max and accumulation") {
    val kernel = load(35, Fixture.listing("videoMinMaxSm35"), "video_minmax")
    assertEquals(kernel.function.instructions.count(_.mnemonic.raw == "VMNMX"), 12)
    Vec(
      (1, 2, 3),
      (3, 2, 1),
      (-1, 1, 0),
      (Int.MinValue, Int.MaxValue, -1),
      (Int.MaxValue, Int.MaxValue, 1),
      (Int.MinValue, Int.MinValue, -1),
      (-1, -2, -3)
    ).foreach { (a, b, c) =>
      val expected = Vec(false, true).flatMap { unsigned =>
        def widen(value: Int): Long = if (unsigned) value.toLong & 0xffffffffL else value.toLong
        Vec(math.min(widen(a), widen(b)), math.max(widen(a), widen(b))).flatMap(value =>
          Vec(math.min(value, widen(c)), math.max(value, widen(c)), value + widen(c)).map(_ & 0xffffffffL)
        )
      }
      assertEquals(
        kernel.launch(1)(Arg.zeros(48), Arg.u32(a), Arg.u32(b), Arg.u32(c)).words(0),
        expected,
        s"a=$a b=$b c=$c"
      )
    }
  }

  private val nestedCases = {
    val boundaries = Vec(
      Int.MinValue,
      Int.MinValue + 1,
      -16777217,
      -65537,
      -65536,
      -1,
      0,
      1,
      65535,
      65536,
      16777217,
      Int.MaxValue - 1,
      Int.MaxValue
    )
    val random   = Random(0x51a7L)
    val unpadded =
      (for {
        a <- boundaries
        b <- boundaries
        c <- boundaries
      } yield (a, b, c)) ++
        Vec.fill(10000)((random.nextInt(), random.nextInt(), random.nextInt()))
    unpadded ++ Vec.fill((128 - unpadded.size % 128) % 128)((0, 0, 0))
  }

  test("CUDA 11.8 sm_50/sm_60 signed nested VMNMX kernels match scalar min/max") {
    def nested(kernel: Kernel): Run = kernel.launch(128, nestedCases.size / 128)(
      Arg.Buffer(words(nestedCases.map(_._1.toLong))),
      Arg.Buffer(words(nestedCases.map(_._2.toLong))),
      Arg.Buffer(words(nestedCases.map(_._3.toLong))),
      Arg.zeros(nestedCases.size * 4)
    )
    for (sm <- Vec(50, 60))
      Vec(
        ("nested_min", "videoMinMaxNestedMinSm50", Vec("MIN"), (a: Int, b: Int, c: Int) => math.min(math.min(a, b), c)),
        (
          "nested_max",
          "videoMinMaxNestedMaxSm50",
          Vec("MX", "MAX"),
          (a: Int, b: Int, c: Int) => math.max(math.max(a, b), c)
        )
      ).foreach { (function, listing, modifiers, expected) =>
        val kernel = load(sm, Fixture.listing(listing), function)
        val vmnmx  = kernel.function.instructions.filter(_.mnemonic.raw == "VMNMX")
        assertEquals(vmnmx.map(_.modifiers.map(_.name)), Vec(modifiers), s"sm_$sm $function")
        assertEquals(vmnmx.head.operands.size, 4, s"sm_$sm $function")
        assertEquals(nested(kernel).words(3).map(_.toInt), nestedCases.map(expected.tupled), s"sm_$sm $function")
        if (function == "nested_min") {
          val instruction = vmnmx.head
          val sentinel    = 0x13579bdf
          val initialise  = Listing.program(
            s"MOV32I ${OperandPrinter.print(instruction.operands.head).value}, 0x${sentinel.toHexString}"
          )
            .value.instructions.head.copy(address = (instruction.address.raw - 1).unsafe[Int >= 0])
          val guarded =
            kernel.copy(functions = Vec(kernel.function.copy(instructions = kernel.function.instructions.flatMap { i =>
              if (i eq instruction) Vec(initialise, i.rewrite(guard = Parser.parse("@!PT NOP;").value.guard))
              else Vec(i)
            })))
          assertEquals(
            nested(guarded).words(3).map(_.toInt),
            Vec.fill(nestedCases.size)(sentinel),
            s"sm_$sm false VMNMX guard"
          )
        }
      }
  }

  for {
    sm                             <- Vec(100, 103)
    (modifier, selector, expected) <- Vec(
      ("", "PT", Int.MinValue),
      ("", "!PT", Int.MaxValue),
      (".U32", "PT", Int.MaxValue),
      (".U32", "!PT", -1)
    )
  }
    test(s"sm$sm VIMNMX3$modifier selector=$selector retains signed boundaries and an aliased third source") {
      check(
        sm,
        s"MOV R4, 0x80000000; UMOV UR6, 0x7fffffff; MOV R5, 0xffffffff; VIMNMX3$modifier R5, R4, UR6, R5, $selector; " +
          "LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R5; EXIT",
        Vec(expected)
      )
    }

  for {
    propagate <- Vec(false, true)
    minimum   <- Vec(false, true)
  }
    test(s"floating min/max NaN propagation=$propagate minimum=$minimum") {
      Vec(
        (Float.NaN, 2.0f),
        (2.0f, Float.NaN),
        (Float.NaN, Float.NaN),
        (-0.0f, 0.0f),
        (3.0f, -2.0f),
        (Float.PositiveInfinity, 2.0f)
      )
        .foreach { (a, b) =>
          val numeric = if (a.isNaN) b else if (b.isNaN) a else if (minimum) Math.min(a, b) else Math.max(a, b)
          // SASS emits the canonical all-mantissa-bits NaN
          val expected =
            if (numeric.isNaN || (propagate && (a.isNaN || b.isNaN))) 0x7fffffff
            else java.lang.Float.floatToRawIntBits(numeric)
          check(
            90,
            s"LDC.64 R10, c[0x0][0x160]; MOV R4, ${hex(java.lang.Float.floatToRawIntBits(a).toLong & 0xffffffffL)}; " +
              s"UMOV UR6, ${hex(java.lang.Float.floatToRawIntBits(b).toLong & 0xffffffffL)}; " +
              s"${if (propagate) "FMNMX.NAN" else "FMNMX"} R4, R4, UR6, ${
                  if (minimum) "PT" else "!PT"
                }; STG.E [R10], R4; EXIT",
            Vec(expected)
          )
        }
    }

  private val fmnmx = load(86, Fixture.listing("fmnmxSm86"), "probe")

  private val truePredicate = Operand.Predicate(None, uniform = false, negated = false)

  test("compiler-produced sm_86 FMNMX implements min/max, zero ties, and NaN selection") {
    val cases = NumericCases.f32Extrema
    val run   = fmnmx.launch(cases.size)(
      Arg.zeros(cases.size * 8),
      Arg.Buffer(words(cases.map(_._1))),
      Arg.Buffer(words(cases.map(_._2)))
    )
    assertEquals(run.words(0), cases.flatMap((_, _, minimum, maximum) => Vec(minimum, maximum)))
  }

  test("FMNMX preserves read-before-write when the destination aliases a source") {
    val nop     = Parser.parse("NOP;").value
    val aliased = fmnmx.map { instruction =>
      val opcode = instruction.mnemonic.raw
      if (opcode == "FMNMX" && instruction.operands.lastOption.contains(truePredicate))
        instruction.rewrite(operands = instruction.operands.updated(0, OperandParser.parse("R4").value))
      else if (opcode == "FMNMX") instruction.rewrite(nop)
      else if (opcode == "STG" && instruction.address.raw == 0xf0)
        instruction.rewrite(operands = OperandParser.parseMany("[R2.64], R4").value)
      else if (opcode == "STG") instruction.rewrite(nop)
      else instruction
    }
    assertEquals(
      aliased.launch(1)(Arg.zeros(8), Arg.words(0x40000000L), Arg.words(0x3f800000L)).words(0).head,
      0x3f800000L
    )
  }

  test("FMNMX fails closed for unmeasured modifier and selector forms") {
    def minimum(f: DecodedInstruction => DecodedInstruction): Kernel =
      fmnmx.map(i => if (i.mnemonic.raw == "FMNMX" && i.operands.lastOption.contains(truePredicate)) f(i) else i)
    assert(minimum(i => i.rewrite(modifiers = i.modifiers :+ Instruction.Modifier("FTZ"))).lower(8, 8, 8)
      .left.exists(_.contains("unsupported modifiers")))
    assert(minimum(i =>
      i.rewrite(operands = i.operands.updated(3, Operand.Predicate(Some(0), uniform = false, negated = false)))
    )
      .lower(8, 8, 8).left.exists(_.contains("FMNMX selector must be PT or !PT")))
  }

  test("compiler-produced sm_89 DSETP.MAX/MIN preserve NaN and signed-zero selection") {
    def double(bits: Long): Double = java.lang.Double.longBitsToDouble(bits)
    def bits(value: Double): Long  = java.lang.Double.doubleToRawLongBits(value)
    val signalling                 = 0xfff00000000000bcL
    val cases                      = Vec(
      (3.0, 2.0, 3.0, 2.0),
      (-2.0, -3.0, -2.0, -3.0),
      (0.0, -0.0, 0.0, -0.0),
      (-0.0, 0.0, 0.0, -0.0),
      (double(0x7ff8000000001234L), 5.0, 5.0, 5.0),
      (5.0, double(0x7ff8000000004321L), 5.0, 5.0),
      (Double.PositiveInfinity, 42.0, Double.PositiveInfinity, 42.0),
      (Double.NegativeInfinity, 42.0, 42.0, Double.NegativeInfinity),
      (
        double(0x7ff80000000000a5L),
        double(signalling),
        double(signalling | 0x0008000000000000L),
        double(signalling | 0x0008000000000000L)
      )
    )
    val run = load(89, Fixture.listing("extremaSm89"), "extrema").launch(16)(
      Arg.Buffer(words(cases.map(c => bits(c._1)), 8)),
      Arg.Buffer(words(cases.map(c => bits(c._2)), 8)),
      Arg.zeros(cases.size * 8),
      Arg.zeros(cases.size * 8)
    )
    assertEquals(run.words(argument = 2, width = 8), cases.map(c => bits(c._3)))
    assertEquals(run.words(argument = 3, width = 8), cases.map(c => bits(c._4)))
  }

  for {
    minimum <- Vec(false, true)
    values  <- Vec(
      (3.0, 2.0),
      (-0.0, 0.0),
      (Double.NaN, 2.0),
      (2.0, Double.NaN),
      (Double.NaN, Double.NaN),
      (Double.PositiveInfinity, 2.0)
    )
  }
    test(s"double min/max preserves pairs and numeric NaN handling: $values minimum=$minimum") {
      val (a, b)   = values
      val expected = java.lang.Double.doubleToRawLongBits(if (a.isNaN) b
      else if (b.isNaN) a else if (minimum) Math.min(a, b) else Math.max(a, b))
      val (ra, rb) = (java.lang.Double.doubleToRawLongBits(a), java.lang.Double.doubleToRawLongBits(b))
      check(
        35,
        s"LDC.64 R10, c[0x0][0x160]; MOV R4, ${hex(ra & 0xffffffffL)}; MOV R5, ${hex(ra >>> 32)}; MOV R6, ${hex(rb &
            0xffffffffL)}; " +
          s"MOV R7, ${hex(rb >>> 32)}; DMNMX R4, R4, R6, ${if (minimum) "PT" else "!PT"}; STG.E.64 [R10], R4; EXIT",
        Vec(expected.toInt, (expected >>> 32).toInt)
      )
    }

  for {
    unsigned <- Vec(false, true)
    minimum  <- Vec(false, true)
  }
    test(s"64-bit integer min/max retains high words unsigned=$unsigned minimum=$minimum") {
      val value = if (unsigned == minimum) 1L else Long.MinValue
      check(
        120,
        "LDC.64 R10, c[0x0][0x160]; MOV R4, 0; MOV R5, 0x80000000; MOV R6, 1; MOV R7, 0; " +
          s"IMNMX.${if (unsigned) "U64" else "S64"} R4, R4, R6, ${
              if (minimum) "PT" else "!PT"
            }, !PT; STG.E.64 [R10], R4; EXIT",
        Vec(value.toInt, (value >>> 32).toInt)
      )
    }

  for {
    unsigned <- Vec(false, true)
    minimum  <- Vec(false, true)
    input    <- Vec(Int.MaxValue, -1)
  }
    test(s"Hopper fused add/minmax preserves wrapping addition unsigned=$unsigned minimum=$minimum input=$input") {
      val sum  = input + 1
      val less = if (unsigned) java.lang.Integer.compareUnsigned(sum, 1) < 0 else sum < 1
      check(
        90,
        s"LDC.64 R10, c[0x0][0x160]; MOV R4, 0x${input.toHexString}; MOV R5, 1; " +
          s"VIADDMNMX${if (unsigned) ".U32" else ""} R4, R4, 1, R5, ${
              if (minimum) "PT" else "!PT"
            }; STG.E [R10], R4; EXIT",
        Vec(if (less == minimum) sum else 1)
      )
    }

  for {
    signed  <- Vec(false, true)
    value   <- Vec(Long.MinValue, -1L, 0L, 3583L, 3585L, Long.MaxValue)
    minimum <- Vec(false, true)
  }
    test(s"sm_120 uniform 64-bit min/max signed=$signed value=$value minimum=$minimum") {
      val compare  = if (signed) java.lang.Long.compare(value, 3584L) else java.lang.Long.compareUnsigned(value, 3584L)
      val expected = if ((compare <= 0) == minimum) value else 3584L
      check(
        120,
        s"UMOV UR20, ${value.toInt}; UMOV UR21, ${(value >>> 32).toInt}; " +
          s"UIMNMX.${if (signed) "S64" else "U64"} UR20, UR20, 3584, ${if (minimum) "UPT" else "!UPT"}, !UPT; " +
          s"MOV R6, UR20; MOV R7, UR21; LOP.XOR R7, R7, ${(expected >>> 32).toInt}; LOP.XOR R6, R6, ${expected.toInt}; " +
          "LOP.OR R6, R6, R7; LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R6; EXIT",
        Vec(0)
      )
    }
}
