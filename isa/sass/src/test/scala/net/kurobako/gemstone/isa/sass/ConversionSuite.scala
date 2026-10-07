package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*

class ConversionSuite extends munit.FunSuite {
  private def opcode(instruction: DecodedInstruction, name: String, modifiers: String*): Boolean =
    instruction.mnemonic.raw == name && instruction.modifiers.map(_.name) == Vec.from(modifiers)

  private def rejects(
      kernel: Kernel,
      parameters: Int*
  )(f: PartialFunction[DecodedInstruction, DecodedInstruction]): Unit =
    assert(kernel.map(instruction => f.applyOrElse(instruction, identity)).lower(parameters*).isLeft)

  private val f2f =
    Vec(70, 75, 80, 86, 89).map(sm => sm -> load(sm, Fixture.listing(s"f2fF32F64Sm$sm"), "f2f_conversion_probe"))

  test("F2F narrowing and widening match the compiler fixtures and hardware reference values") {
    val narrow = NumericCases.f64ToF32
    val widen  = NumericCases.f32ToF64
    val count  = narrow.size.max(widen.size)
    for ((sm, kernel) <- f2f) {
      val run = kernel.launch(count)(
        Arg.zeros(count * 4),
        Arg.Buffer(words(narrow.map(_._1).padTo(count, 0L), 8)),
        Arg.zeros(count * 8),
        Arg.Buffer(words(widen.map(_._1).padTo(count, 0L))),
        Arg.u32(count)
      )
      assertEquals(run.words(0).take(narrow.size), narrow.map(_._2), s"sm_$sm")
      assertEquals(run.words(2, 8).take(widen.size), widen.map(_._2), s"sm_$sm")
    }
  }

  test("F2F rejects unverified rounding modifiers and architectures") {
    val kernel = f2f.last._2
    rejects(kernel, 8, 8, 8, 8, 4) {
      case instruction if opcode(instruction, "F2F", "F32", "F64") =>
        instruction.rewrite(modifiers = instruction.modifiers.updated(0, Instruction.Modifier("RZ")))
    }
    assert(kernel.copy(sm = 90).lower(8, 8, 8, 8, 4).isLeft)
  }

  private def f2fHalf(sm: Int) = load(sm, Fixture.listing(s"f2fF16F32Sm$sm"), "probe")

  private def narrowHalf(kernel: Kernel): Vec[Long] = {
    val inputs = NumericCases.f32ToF16.map(_._1)
    kernel.launch(1, inputs.size)(Arg.zeros(inputs.size * 4), Arg.Buffer(words(inputs))).words(0)
  }

  test("compiler-produced F2F.F16.F32 rounds, canonicalises NaNs, and zero-extends") {
    Vec(70, 75, 89).foreach(sm => assertEquals(narrowHalf(f2fHalf(sm)), NumericCases.f32ToF16.map(_._2), s"sm_$sm"))
  }

  test("F2F.F16.F32 lowering preserves generic predicate handling") {
    val kernel     = f2fHalf(75)
    val conversion = kernel.function.instructions.find(opcode(_, "F2F", "F16", "F32")).get
    val predicate  = Listing.program("ISETP.EQ.U32.AND P0, PT, RZ, RZ, PT").value.instructions.head
    val guarded = kernel.copy(functions = Vec(kernel.function.copy(instructions = kernel.function.instructions.flatMap {
      instruction =>
        if (instruction eq conversion) Vec(
          predicate.copy(address = (conversion.address.raw - 8).unsafe[Int >= 0]),
          instruction.rewrite(guard =
            Some(Instruction.Guard(Operand.Predicate(Some(0), uniform = false, negated = false)))
          )
        )
        else Vec(instruction)
    })))
    assertEquals(narrowHalf(guarded), NumericCases.f32ToF16.map(_._2))
  }

  private val i2fHalf = Vec(
    0            -> 0x0000L,
    1            -> 0x3c00L,
    -1           -> 0xbc00L,
    2            -> 0x4000L,
    -2           -> 0xc000L,
    1023         -> 0x63feL,
    1024         -> 0x6400L,
    2047         -> 0x67ffL,
    2048         -> 0x6800L,
    2049         -> 0x6800L,
    2050         -> 0x6801L,
    2051         -> 0x6802L,
    2052         -> 0x6802L,
    2053         -> 0x6802L,
    -2049        -> 0xe800L,
    -2051        -> 0xe802L,
    65504        -> 0x7bffL,
    65519        -> 0x7bffL,
    65520        -> 0x7c00L,
    -65504       -> 0xfbffL,
    16777217     -> 0x7c00L,
    -16777217    -> 0xfc00L,
    Int.MaxValue -> 0x7c00L,
    Int.MinValue -> 0xfc00L
  )

  test("compiler-produced I2F.F16 converts signed integers with RN and zero-extends the half result") {
    val kernel = load(80, Fixture.listing("i2fF16Sm80"), "i2f_f16_probe")
    val run    = kernel.launch(i2fHalf.size)(Arg.Buffer(words(i2fHalf.map(_._1.toLong))), Arg.zeros(i2fHalf.size * 4))
    assertEquals(run.words(1), i2fHalf.map(_._2))
  }

  test("compiler-produced I2F.F16 converts an immediate integer source") {
    val kernel = load(80, Fixture.listing("i2fF16ImmediateSm80"), "i2f_f16_immediate_probe")
    assertEquals(kernel.launch(4)(Arg.zeros(16)).words(0), Vec.fill(4)(0x3c00L))
  }

  test("I2F.F16 lowering rejects unverified rounding modifiers") {
    rejects(load(80, Fixture.listing("i2fF16Sm80"), "i2f_f16_probe"), 8, 8) {
      case instruction
          if instruction.mnemonic.raw == "I2F" && instruction.modifiers.headOption.exists(_.name == "F16") =>
        instruction.rewrite(modifiers = instruction.modifiers :+ Instruction.Modifier("RZ"))
    }
  }

  test("sm_86 I2FP and I2F preserve signed conversion rounding modes") {
    val kernel = load(86, Fixture.listing("i2fpSm86"), "convert")
    // binary32 bits for RN, RM, RP and RZ, including inexact conversion at 2^24
    Vec(
      (0, Vec(0, 0, 0, 0)),
      (1, Vec.fill(4)(0x3f800000)),
      (-1, Vec.fill(4)(0xbf800000)),
      (16777217, Vec(0x4b800000, 0x4b800000, 0x4b800001, 0x4b800000)),
      (-16777217, Vec(0xcb800000, 0xcb800001, 0xcb800000, 0xcb800000)),
      (Int.MaxValue, Vec(0x4f000000, 0x4effffff, 0x4f000000, 0x4effffff)),
      (Int.MinValue, Vec.fill(4)(0xcf000000))
    ).foreach { (input, expected) =>
      assertEquals(
        kernel.launch(1)(Arg.zeros(16), Arg.u32(input.toLong)).words(0).map(_.toInt),
        expected,
        s"input=$input"
      )
    }
  }

  private val packCases = Vec(
    (0x00000000L, 0x80000000L, 0x80000000L),
    (0x80000000L, 0x3f800000L, 0x3c008000L),
    (0x3f800000L, 0xc0000000L, 0xc0003c00L),
    (0xc0000000L, 0x477fe000L, 0x7bffc000L),
    (0x477fe000L, 0x477ff000L, 0x7c007bffL),
    (0x477ff000L, 0x33800000L, 0x00017c00L),
    (0x33800000L, 0x33000000L, 0x00000001L),
    (0x33000000L, 0x38800000L, 0x04000000L),
    (0x38800000L, 0x38000000L, 0x02000400L),
    (0x38000000L, 0x3f801000L, 0x3c000200L),
    (0x3f801000L, 0x3f803000L, 0x3c023c00L),
    (0x3f803000L, 0x7f800000L, 0x7c003c02L),
    (0x7f800000L, 0xff800000L, 0xfc007c00L),
    (0xff800000L, 0x7fc12345L, 0x7ffffc00L),
    (0x7fc12345L, 0xffc54321L, 0x7fff7fffL),
    (0xffc54321L, 0x7f812345L, 0x7fff7fffL),
    (0x7f812345L, 0xff812345L, 0x7fff7fffL),
    (0xff812345L, 0x40600000L, 0x43007fffL),
    (0x40600000L, 0xc0e80000L, 0xc7404300L),
    (0xc0e80000L, 0x00000000L, 0x0000c740L),
    (0x7f800001L, 0xff800001L, 0x7fff7fffL),
    (0x7fc12345L, 0x3f800000L, 0x3c007fffL),
    (0x3f800000L, 0xffc54321L, 0x7fff3c00L)
  )

  private val pack = load(89, Fixture.listing("f2fpPackAbSm89"), "f2fp_pack_ab_probe")

  private def packed(kernel: Kernel): Vec[Long] = kernel.launch(packCases.size)(
    Arg.Buffer(words(packCases.flatMap((first, second, _) => Vec(first, second)))),
    Arg.zeros(packCases.size * 4)
  ).words(1)

  test("compiler-produced F2FP.PACK_AB matches sm_89 lane packing, rounding, and NaN behaviour") {
    assertEquals(packed(pack), packCases.map(_._3))
  }

  test("modifierless F2FP.PACK_AB supports destination aliasing its second source") {
    val replacement = Parser.parse("F2FP.PACK_AB R7, RZ, R7;").value
    val aliased     =
      pack.map(instruction => if (instruction.mnemonic.raw == "F2FP") instruction.rewrite(replacement) else instruction)
    assertEquals(packed(aliased), packCases.map((_, _, expected) => (expected >>> 16) & 0xffffL))
  }

  test("F2FP.PACK_AB lowering rejects unverified modifier forms") {
    rejects(pack, 8, 8) {
      case instruction if instruction.mnemonic.raw == "F2FP" =>
        instruction.rewrite(modifiers = instruction.modifiers :+ Instruction.Modifier("SATFINITE"))
    }
  }

  for (sm <- Vec(70, 75, 80, 86, 89)) test(s"sm_$sm FRND matches nearest-even rounding and measured NaN bits") {
    val vectors = ConversionFixtures.frndVectors
    val run     = load(sm, Fixture.listing(s"frndSm$sm"), "probe")
      .launch(vectors.size)(Arg.zeros(vectors.size * 4), Arg.Buffer(words(vectors.map(_._1))))
    assertEquals(run.words(0), vectors.map(_._2))
  }

  private val narrowSigned = load(35, Fixture.listing("i2iS16S8Sm35"), "probe")

  test("unsupported I2I modifiers, destinations, and operands fail through the cubin compiler") {
    Vec("I2I.S16.S8.SAT R0, R0", "I2I.S16.S8 R0.CC, R0", "I2I.S16.S8 R0, 0xff", "I2I.S16.S8 R0, -R0").foreach { form =>
      val listing = Fixture.listing("i2iS16S8Sm35").replace("I2I.S16.S8 R0, R0", form)
      assert(load(35, listing, "probe").lower(8, 8).isLeft, form)
    }
  }

  test("compiler-produced sm_35 signed I2I widening and negation preserve signed 16-bit values") {
    Vec((-128, Short.MinValue.toInt), (-1, -1), (0, 0), (1, 1), (127, Short.MaxValue.toInt)).foreach { (byte, half) =>
      val input = Vec(byte.toByte, 0.toByte, half.toByte, (half >> 8).toByte)
      val bytes = narrowSigned.launch(1)(Arg.zeros(4), Arg.Buffer(input)).bytes(0)
      assertEquals(LittleEndian.decode(bytes, 0, 2), byte.toShort.toLong & 0xffffL)
      assertEquals(LittleEndian.decode(bytes, 2, 2), (-half).toShort.toLong & 0xffffL)
    }
  }

  test("compiler-produced sm_35 I2I CC chain reports a masked 48-bit nonzero test") {
    val kernel = load(35, Fixture.listing("i2iCcSm35"), "probe")
    Vec(
      0L                  -> 0L,
      1L                  -> 1L,
      0x800000000000L     -> 1L,
      0x0001000000000000L -> 0L,
      0xffffffffffffffffL -> 1L,
      0x0000000100000000L -> 1L,
      0xffff000000000000L -> 0L
    ).foreach { (input, expected) =>
      assertEquals(kernel.launch(1)(Arg.u64(input), Arg.zeros(4)).words(1), Vec(expected), f"input=$input%016x")
    }
  }

  test("integer conversion negates the absolute source before writing") {
    check(50, "MOV R4, -7; I2I.S32.S32 R4, -|R4|; LDC.64 R2, c[0x0][0x160]; STG.E [R2], R4; EXIT", Vec(-7))
  }

  for (sm <- Vec(35, 50, 60)) test(s"sm_$sm unsigned identity I2I copies register, zero, and immediate sources") {
    check(
      sm,
      "MOV32I R4, 0xfffffff9; I2I.U32.U32 R5, R4; I2I.U32.U32 R6, RZ; I2I.U32.U32 R7, 0x1800; " +
        "LDC.64 R2, c[0x0][0x160]; STG.E [R2], R5; STG.E [R2+0x4], R6; STG.E [R2+0x8], R7; EXIT",
      Vec(-7, 0, 0x1800),
      block = Some(1)
    )
  }

  for (value <- Vec(Int.MinValue, -7, 0)) {
    val prefix = s"LDC.64 R10, c[0x0][0x160]\nMOV R4, 0x${value.toHexString}"
    test(s"integer-to-float absolute source avoids signed overflow for $value") {
      check(
        50,
        s"$prefix\nI2F.F32.S32 R4, |R4|\nSTG.E [R10], R4\nEXIT",
        Vec(java.lang.Float.floatToRawIntBits(Math.abs(value.toLong).toFloat))
      )
    }
    test(s"legacy signed absolute conversion preserves 32-bit wrapping: $value") {
      check(50, s"$prefix\nI2I.S32.S32 R4, |R4|\nSTG.E [R10], R4\nEXIT", Vec(Math.abs(value)))
    }
  }

  test("uniform integer conversion stores both double-precision result words") {
    val bits = java.lang.Double.doubleToRawLongBits(-7.0)
    check(
      120,
      """LDC.64 R10, c[0x0][0x160]
        |UMOV UR8, -7
        |UI2F.F64.S32 UR4, UR8
        |MOV.64 R4, UR4
        |STG.E.64 [R10], R4
        |EXIT""".stripMargin,
      Vec(bits.toInt, (bits >>> 32).toInt)
    )
  }

  test("double conversion and absolute comparisons preserve negative integer inputs") {
    check(
      86,
      """MOV R0, -7
        |I2F.F64 R4, R0
        |DSETP.LT.AND P0, PT, |R4|, +INF, PT
        |F2I.S32.F64.TRUNC R6, R4
        |SEL R6, R6, 0, P0
        |LDC.64 R2, c[0x0][0x160]
        |STG.E [R2.64], R6
        |EXIT""".stripMargin,
      Vec(-7)
    )
  }
}
