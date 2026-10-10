package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as VMInstruction, Kernel as _, *}
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*

class FloatSuite extends munit.FunSuite {
  private def hex(bits: Int): String      = s"0x${Integer.toUnsignedString(bits, 16)}"
  private def float(value: Float): Int    = java.lang.Float.floatToRawIntBits(value)
  private def double(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)

  private def bits(exponent: Int, mantissa: Int = 0, negative: Boolean = false): Int =
    (if (negative) 0x80000000 else 0) | (exponent << 23) | mantissa

  // the measured slow-path predicate depends only on the biased exponents
  private def slowPath(a: Int, b: Int): Int = {
    val (ea, eb) = ((a >>> 23) & 0xff, (b >>> 23) & 0xff)
    if (ea < 25 || ea > 254 || eb < 2 || eb > 251 || ea - eb < -124 || ea - eb > 126) 1 else 0
  }

  private def fchk(kernel: Kernel, pairs: Vec[(Int, Int)]): Vec[Int] = kernel.launch(pairs.size)(
    Arg.Buffer(words(pairs.map(_._1.toLong & 0xffffffffL))),
    Arg.Buffer(words(pairs.map(_._2.toLong & 0xffffffffL))),
    Arg.zeros(pairs.size * 4)
  ).words(2).map(_.toInt)

  private val boundaryPairs: Vec[(Int, Int)] = {
    val exponents = Vec(0, 1, 2, 24, 25, 26, 127, 250, 251, 252, 253, 254, 255)
    val matrix    = for {
      (ea, i) <- exponents.zipWithIndex
      (eb, j) <- exponents.zipWithIndex
    } yield (
      bits(ea, (i * 0x10203 + j * 0x301) & 0x7fffff, (i & 1) != 0),
      bits(eb, (j * 0x507 + i * 0x10001) & 0x7fffff, (j & 1) != 0)
    )
    matrix ++ Vec(
      bits(0)                                   -> bits(0, negative = true),
      bits(exponent = 0, mantissa = 1)          -> bits(exponent = 0, mantissa = 0x7fffff, negative = true),
      bits(exponent = 127, mantissa = 0x400000) -> bits(exponent = 127, mantissa = 0x7fffff, negative = true),
      bits(255)                                 -> bits(exponent = 255, mantissa = 1),
      bits(exponent = 255, mantissa = 0x400000, negative = true) -> bits(exponent = 255, mantissa = 0x7fffff),
      bits(128)                                                  -> bits(2),
      bits(129)                                                  -> bits(2),
      bits(2)                                                    -> bits(126),
      bits(2)                                                    -> bits(127)
    )
  }

  private def fchkProbe(sm: Int): Kernel = load(sm, Fixture.listing(s"fchkSm${sm}Patched"), "probe")

  for (sm <- Vec(70, 75, 80, 86, 89))
    test(s"FCHK exponent boundaries, signs, zeros and NaNs for sm_$sm cubin") {
      // the compiler's SEL stores 1 when P0 is false
      assertEquals(fchk(fchkProbe(sm), boundaryPairs).map(1 - _), boundaryPairs.map(slowPath(_, _)))
    }

  test("sm70 and sm75 FCHK reject forms outside the measured register forms") {
    Vec(70, 75).foreach { sm =>
      val kernel   = fchkProbe(sm)
      val original = kernel.function.instructions.find(_.mnemonic.raw == "FCHK").get
      Vec(
        original.rewrite(guard = Some(Instruction.Guard(Operand.Predicate(Some(0), uniform = false, negated = false)))),
        original.rewrite(modifiers = original.modifiers :+ Instruction.Modifier("DIVIDE"))
      ).foreach(invalid => assert(kernel.map(i => if (i == original) invalid else i).lower(8, 8, 8).isLeft))
    }
    assert(Parser.parse("FCHK P0, 1, R5;").isLeft)
  }

  test("predicated FCHK may read and write the same predicate without changing the guard") {
    val kernel = program(
      89,
      """LDC.64 R10, c[0x0][0x160]
        |S2R R0, SR_TID.X
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |LDG.E R4, [R10.64]
        |LDC.64 R10, c[0x0][0x168]
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |LDG.E R5, [R10.64]
        |ISETP.NE.U32.AND P0, PT, R0, RZ, PT
        |@P0 FCHK P0, R4, R5
        |MOV R7, 1
        |SEL R6, R7, 0, P0
        |LDC.64 R10, c[0x0][0x170]
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |STG.E [R10.64], R6
        |EXIT""".stripMargin
    )
    val pairs = Vec(
      bits(127)                                 -> bits(127), // lane 0 has a false guard and must retain it
      bits(24)                                  -> bits(127),
      bits(127, negative = true)                -> bits(2),
      bits(exponent = 127, mantissa = 0x7fffff) -> bits(exponent = 127, mantissa = 0x400000),
      bits(exponent = 255, mantissa = 0x400000) -> bits(127)
    )
    assertEquals(fchk(kernel, pairs), 0 +: pairs.drop(1).map(slowPath(_, _)))
  }

  for (sm <- Vec(35, 50, 60))
    test(s"legacy FCHK.DIVIDE exponent boundaries on sm_$sm") {
      Vec(
        (24, 127, 1),
        (25, 127, 0),
        (254, 128, 0),
        (255, 127, 1),
        (127, 1, 1),
        (127, 2, 0),
        (127, 251, 0),
        (127, 252, 1),
        (25, 150, 1),
        (26, 150, 0),
        (200, 74, 0),
        (201, 74, 1)
      ).foreach { (a, b, expected) =>
        check(
          sm,
          s"MOV R4, ${a.toLong << 23}; MOV R5, ${b.toLong << 23}; FCHK.DIVIDE P0, R4, R5; SEL R6, RZ, 1, !P0; " +
            "LDC.64 R2, c[0x0][0x160]; STG.E [R2], R6; EXIT",
          Vec(expected)
        )
      }
    }

  private def fsel(encoding: Option[Long]): Kernel = program(86, "FSEL R3, RZ, +QNAN, P0; EXIT")
    .map(i => if (i.mnemonic.raw == "FSEL") i.copy(encodingWord = encoding.map(BigInt(_))) else i)

  private def selectedImmediates(kernel: Kernel, parameters: Int*): Set[Long] =
    kernel.instructions(parameters*).collect { case VMInstruction.Select(whenTrue = a, whenFalse = b) =>
      Vec(a, b)
    }.flatten.collect {
      case Value.Immediate(value) => value
    }.toSet

  test("FSEL carries encoded NaN payload bits through its raw register select") {
    assert(selectedImmediates(fsel(Some(0x7fc00000ff037808L)))(0x7fc00000L))
    assert(selectedImmediates(fsel(Some(0x7ff80000ff037808L)))(0x7ff80000L))
  }

  test("FSEL NaN immediates fail closed when disassembly did not provide instruction bits") {
    assert(fsel(None).lower().left.exists(_.contains("FSEL NaN immediate requires raw instruction encoding")))
  }

  test(
    "FSEL NaN immediate decoding rejects register encodings, mismatched payloads, and unverified operand placement"
  ) {
    assert(
      fsel(Some(0x7ff80000ff037208L)).lower().left.exists(_.contains("FSEL NaN source is not encoded as an immediate"))
    )
    assert(fsel(
      Some(0x3f800000ff037808L)
    ).lower().left.exists(_.contains("FSEL NaN spelling does not match encoded source bits")))
    assert(Parser.parse("FSEL R3, +QNAN, RZ, P0;").left.exists(_.contains("invalid operands for FSEL")))
  }

  test("compiler-produced sm_86 FSEL immediates preserve distinct NaN payloads in VM execution") {
    val kernel = load(86, Fixture.listing("fselImmediateSm86"), "probe")
    assert(Set(0x7ff80000L, 0x7fc00000L).subsetOf(selectedImmediates(kernel, 8)))
    assertEquals(kernel.launch(2)(Arg.zeros(16)).words(0), Vec(0x3f800000L, 0x3f800000L, 0x7ff80000L, 0x7fc00000L))
  }

  test("compiler-produced legacy DSET preserves ordered, unordered, boolean and BF comparisons") {
    val pairs = Vec(
      (1.0, 2.0),
      (2.0, 1.0),
      (1.0, 1.0),
      (-0.0, 0.0),
      (0.0, -0.0),
      (Double.NaN, 1.0),
      (1.0, Double.NaN),
      (Double.NaN, Double.NaN),
      (Double.PositiveInfinity, Double.PositiveInfinity),
      (Double.NegativeInfinity, Double.PositiveInfinity),
      (Double.PositiveInfinity, Double.NegativeInfinity),
      (java.lang.Double.MIN_VALUE, 0.0),
      (-java.lang.Double.MIN_VALUE, -0.0),
      (-1.0, -2.0)
    )
    val expected = pairs.zipWithIndex.flatMap { case ((a, b), index) =>
      val p = index % 2 == 0
      Vec(a != b, a == b, a < b, a.isNaN || b.isNaN || a >= b, a == b && p, a < b || p, (a != b) != !p)
        .map(flag => if (flag) 0xffffffffL else 0L) :+ (if (a >= b) 0x3f800000L else 0L)
    }
    val kernel = load(35, Fixture.listing("dsetSm35"), "probe")
    assertEquals(kernel.function.instructions.count(_.mnemonic.raw == "DSET"), 8)
    val run = kernel.launch(pairs.size)(
      Arg.zeros(pairs.size * 32),
      Arg.Buffer(words(pairs.flatMap((a, b) => Vec(double(a), double(b))), 8))
    )
    assertEquals(run.words(0), expected)
  }

  for (unordered <- Vec(false, true))
    test(s"FSET matches ordered and unordered comparison cases, unordered=$unordered") {
      val opcode = if (unordered) "FSET.NEU.AND" else "FSET.EQ.AND"
      NumericCases.f32Comparisons.foreach { (left, right, equal, notEqualUnordered) =>
        check(
          89,
          s"MOV R0, c[0x0][0x160]; MOV R1, c[0x0][0x164]; MOV R2, ${hex(left.toInt)}; MOV R3, ${hex(right.toInt)}; " +
            s"$opcode R4, R2, R3, PT; STG.E [R0.64], R4; EXIT",
          Vec(if (if (unordered) notEqualUnordered else equal) -1 else 0)
        )
      }
    }

  for (condition <- Vec(-1.0f, -0.0f, 0.0f, 1.0f, Float.NaN))
    test(s"FCMP default GT compares $condition against zero") {
      for (opcode <- Vec("FCMP", "FCMP.FTZ"))
        check(
          35,
          s"LDC.64 R10, c[0x0][0x160]; MOV R4, ${hex(float(condition))}; MOV R5, 0x40000000; $opcode R4, R5, 1, R4; STG.E [R10], R4; EXIT",
          Vec(float(if (condition > 0.0f) 2.0f else 1.0f))
        )
    }

  for {
    comparison <- Vec("LT", "GEU")
    condition  <- Vec(-1.0f, 0.0f, Float.NaN)
  }
    test(s"floating compare/select applies $comparison to the condition $condition") {
      val selected = if (comparison == "LT") condition < 0.0f else condition >= 0.0f || condition.isNaN
      check(
        35,
        s"LDC.64 R10, c[0x0][0x160]; MOV R4, ${hex(float(condition))}; MOV R5, 0x40000000; FCMP.$comparison.FTZ R4, R5, 1, R4; " +
          "STG.E [R10], R4; EXIT",
        Vec(float(if (selected) 2.0f else 1.0f))
      )
    }

  test("Kepler compressed float immediates retain their IEEE value") {
    check(
      35,
      "LDC.64 R10, c[0x0][0x160]; MOV R0, 0x3fc00000; FADD R4, R0, 1; STG.E [R10], R4; EXIT",
      Vec(0x40200000),
      rewrite = instruction =>
        instruction.operation match {
          case _: Instruction.Fadd =>
            val decoded = InstructionDecoder.decode(
              instruction.address,
              LittleEndian.toVec(0xc2c001fc001c0011L, 8).toArray,
              target(35)
            ).value
            assertEquals(decoded.operands.last, Operand.FloatingBits(FloatEncoding.F32Imm, 32, BigInt(0x3f800000)))
            decoded
          case _ => instruction
        }
    )
  }

  test("directed binary32 multiplication retains the exact product before rounding") {
    check(
      86,
      "MOV R0, 0x3f800001; FMUL.RP R4, R0, R0; LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R4; EXIT",
      Vec(0x3f800003)
    )
  }

  // the exact result lies three quarters of an ulp beyond +/-1
  private def roundingCases: Vec[(String, Boolean, Boolean)] =
    for {
      mode     <- Vec("RN", "RM", "RP", "RZ")
      negative <- Vec(false, true)
    } yield (mode, negative, mode == "RN" || mode == (if (negative) "RM" else "RP"))

  test("FFMA preserves each rounding direction for positive and negative results") {
    roundingCases.foreach { (mode, negative, away) =>
      val sign = if (negative) 0x80000000 else 0
      check(
        86,
        s"MOV R0, ${hex(0x3f800000 | sign)}; MOV R1, 0x3f800000; MOV R4, ${hex(0x33c00000 | sign)}; FFMA.$mode R4, R0, R1, R4; " +
          "LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R4; EXIT",
        Vec((if (away) 0x3f800001 else 0x3f800000) | sign)
      )
    }
  }

  test("DFMA preserves each rounding direction for positive and negative results") {
    roundingCases.foreach { (mode, negative, away) =>
      val sign = if (negative) 0x80000000 else 0
      check(
        86,
        s"MOV R0, 0; MOV R1, ${hex(0x3ff00000 |
            sign)}; MOV R4, 0; MOV R5, 0x3ff00000; MOV R6, 0; MOV R7, ${hex(0x3ca80000 | sign)}; " +
          s"DFMA.$mode R6, R0, R4, R6; LDC.64 R2, c[0x0][0x160]; STG.E.64 [R2.64], R6; EXIT",
        Vec(if (away) 1 else 0, 0x3ff00000 | sign)
      )
    }
  }

  test("DADD preserves each rounding direction for positive and negative results") {
    roundingCases.foreach { (mode, negative, away) =>
      val sign = if (negative) 0x80000000 else 0
      check(
        86,
        s"MOV R0, 0; MOV R1, ${hex(0x3ff00000 | sign)}; MOV R6, 0; MOV R7, ${hex(0x3ca80000 | sign)}; " +
          s"DADD.$mode R6, R0, R6; LDC.64 R2, c[0x0][0x160]; STG.E.64 [R2.64], R6; EXIT",
        Vec(if (away) 1 else 0, 0x3ff00000 | sign)
      )
    }
  }

  test("DMUL preserves each rounding direction for positive and negative results") {
    roundingCases.foreach { (mode, negative, _) =>
      val sign = if (negative) 0x80000000 else 0
      val away = mode == (if (negative) "RM" else "RP")
      check(
        86,
        s"MOV R0, 1; MOV R1, ${hex(0x3ff00000 | sign)}; MOV R4, 1; MOV R5, 0x3ff00000; " +
          s"DMUL.$mode R6, R0, R4; LDC.64 R2, c[0x0][0x160]; STG.E.64 [R2.64], R6; EXIT",
        Vec(if (away) 3 else 2, 0x3ff00000 | sign)
      )
    }
  }

  test("directed DADD and DMUL keep the sign of exact zero results") {
    for {
      (operation, high, mode, expected) <- Vec(
        ("DMUL", 0, "RM", 0),
        ("DMUL", 0x80000000, "RP", 0x80000000),
        ("DMUL", 0x80000000, "RZ", 0x80000000),
        ("DADD", 0xbff00000, "RM", 0x80000000),
        ("DADD", 0xbff00000, "RP", 0)
      )
    } check(
      86,
      s"MOV R0, 0; MOV R1, ${hex(high)}; MOV R4, 0; MOV R5, ${hex(if (operation == "DADD") 0x3ff00000 else 0)}; " +
        s"$operation.$mode R6, R0, R4; LDC.64 R2, c[0x0][0x160]; STG.E.64 [R2.64], R6; EXIT",
      Vec(0, expected)
    )
  }

  test("modern MUFU trigonometry uses cycles and preserves large integral periods") {
    Vec(("SIN", 0.25f, 1.0f), ("SIN", -0.25f, -1.0f), ("COS", 16777216.0f, 1.0f), ("SIN", -0.0f, -0.0f)).foreach {
      (operation, input, expected) =>
        check(
          86,
          s"MOV R0, ${hex(float(input))}; MUFU.$operation R4, R0; LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R4; EXIT",
          Vec(float(expected))
        )
    }
  }

  test("FP64 reciprocal seeds consume the high word and compare as register pairs") {
    Vec(("RCP64H", "0x40000000"), ("RSQ64H", "0x40100000")).foreach { (operation, high) =>
      check(
        86,
        s"MOV R8, 0; MOV R9, $high; MOV R10, 0; MUFU.$operation R11, R9; DSETP.LT.AND P0, PT, R10, R8, PT; SEL R4, R11, 0, P0; " +
          "LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R4; EXIT",
        Vec(0x3fe00000)
      )
    }
  }

  test("FP32 exceptional literals and absolute modifiers preserve their floating bits") {
    check(
      86,
      """FADD R4, RZ, +INF
        |FADD R5, RZ, -INF
        |FADD R6, RZ, |-INF|
        |MOV R9, 0x7fc00000
        |FSETP.GTU.FTZ.AND P0, PT, |R9|, +INF, PT
        |MOV R8, 1
        |SEL R7, R8, 0, P0
        |LDC.64 R2, c[0x0][0x160]
        |STG.E.128 [R2.64], R4
        |EXIT""".stripMargin,
      Vec(0x7f800000, 0xff800000, 0x7f800000, 1)
    )
  }

  test("MUFU double high-word immediates accept numeric and raw binary operands") {
    for {
      encoded <- Vec(false, true)
      value   <- Vec(6.0, -6.0)
    }
      check(
        86,
        s"LDC.64 R4, c[0x0][0x160]; MUFU.RCP64H R0, $value; STG.E [R4.64], R0; EXIT",
        Vec((double(1.0 / value) >>> 32).toInt),
        rewrite = instruction =>
          if (encoded && instruction.mnemonic.raw == "MUFU")
            instruction.rewrite(operands =
              instruction.operands.updated(
                1,
                Operand.FloatingBits(FloatEncoding.F64Imm, 64, BigInt(double(value)) & ((BigInt(1) << 64) - 1))
              )
            )
          else instruction
      )
  }

  test("double NaN and numeric predicates preserve boolean combinations") {
    check(
      86,
      """LDC.64 R4, c[0x0][0x160]
        |MOV R0, 0
        |MOV R1, 0x7ff80000
        |MOV R8, 1
        |MOV R9, 2
        |MOV R10, 0
        |DSETP.NAN.AND P0, P1, R0, RZ, PT
        |SEL R6, R8, R10, P0
        |DSETP.NUM.AND P0, PT, RZ, RZ, P1
        |SEL R7, R9, R10, P0
        |IADD3 R6, R6, R7, RZ
        |STG.E [R4.64], R6
        |EXIT""".stripMargin,
      Vec(1)
    )
  }

  for (
    (opcode, value, selected) <- Vec(
      ("UFSETP.GEU.AND", 0x7fc00000, true),
      ("UFSETP.GEU.AND", 0xbf800000, false),
      ("UFSETP.GEU.AND", 0x3f800000, true),
      ("UFSETP.GT.FTZ.AND", 1, false)
    )
  ) test(s"$opcode uses uniform float operands and paired predicates for bits=$value") {
    check(
      120,
      s"""LDC.64 R10, c[0x0][0x160]
         |UMOV UR4, ${hex(value)}
         |$opcode UP0, UP1, UR4, URZ, UPT
         |UMOV UR7, 7
         |USEL UR6, UR7, 9, UP0
         |USEL UR8, UR7, 9, UP1
         |MOV R4, UR6
         |MOV R5, UR8
         |STG.E [R10], R4
         |STG.E [R10+4], R5
         |EXIT""".stripMargin,
      if (selected) Vec(7, 9) else Vec(9, 7)
    )
  }

  // CUDA 13.2 ptxas: add.f32x2 with the first input's two words exchanged
  private val fadd2 = LittleEndian.toVec(0x000000080606724bL, 8) ++ LittleEndian.toVec(0x004fca0000020000L, 8)

  private def decoded(sm: Int, bytes: Vec[Byte] = fadd2): DecodedInstruction =
    InstructionDecoder.decode(0, bytes.toArray, target(sm)).value

  private def pair(
      left: String,
      right: String,
      expected: Vec[Int],
      values: Vec[Int] = Vec(0x3fa00000, 0x41280000, 0x40400000, 0xc0800000),
      opcode: String = "FADD2",
      sm: Int = 100,
      binary: Boolean = false
  ): Unit = check(
    sm,
    s"LDC.64 R2, c[0x0][0x160]; ${values.zipWithIndex.map((value, index) => s"MOV R${index + 6}, ${hex(value)}").mkString("; ")}; " +
      s"$opcode R6, $left, $right; STG.E.64 [R2.64], R6; EXIT",
    expected,
    rewrite = instruction =>
      if (binary && instruction.mnemonic.raw == "FADD2") instruction.copy(operation = decoded(sm).operation)
      else instruction
  )

  for (sm <- Vec(100, 103)) {
    test(s"sm$sm binary and assembly preserve pair swizzles and round trip") {
      val parsed = Parser.parse("FADD2 R6, R6.F32x2.LO_HI, R8.F32x2.HI_LO;").value
      assertEquals(decoded(sm).operation, parsed)
      assertEquals(
        InstructionDecoder.decode(0, Assembler.encode(parsed, target(sm), 0).value, target(sm)).value.operation,
        parsed
      )
    }
    for (binary <- Vec(false, true)) test(s"sm$sm FADD2 swaps first pair before aliased write binary=$binary") {
      pair("R6.F32x2.LO_HI", "R8.F32x2", Vec(0x41580000, 0xc0300000), sm = sm, binary = binary)
    }
    test(s"sm$sm executes MiniBUDE's binary FMUL2 with aliased operands") {
      // Clang MiniBUDE assembled by CUDA 13.3 ptxas: FMUL2 R10, R10.F32x2, R10.F32x2
      val operation =
        decoded(sm, LittleEndian.toVec(0x0000000a0a0a724aL, 8) ++ LittleEndian.toVec(0x000fe20000000000L, 8)).operation
      check(
        sm,
        "LDC.64 R2, c[0x0][0x160]; MOV R10, 0x3fa00000; MOV R11, 0xc1280000; FMUL2 R10, R10.F32x2, R10.F32x2; STG.E.64 [R2.64], R10; EXIT",
        Vec(0x3fc80000, 0x42dc8000),
        rewrite = instruction =>
          if (instruction.mnemonic.raw == "FMUL2") instruction.copy(operation = operation) else instruction
      )
    }
  }

  test("FADD2 normal and swapped second pair keep lanes independent") {
    pair("R6.F32x2.HI_LO", "R8.F32x2", Vec(0x40880000, 0x40d00000))
    pair("R6.F32x2", "R8.F32x2.LO_HI", Vec(0xc0300000, 0x41580000))
  }

  test("FMUL2 multiplies normal, swapped and broadcast operands before aliased writes") {
    pair("R6.F32x2", "R8.F32x2", Vec(0x40700000, 0xc2280000), opcode = "FMUL2")
    pair("R6.F32x2.LO_HI", "R8.F32x2", Vec(0x41fc0000, 0xc0a00000), opcode = "FMUL2")
    pair("R6.F32x2", "R9.F32", Vec(0xc0a00000, 0xc2280000), opcode = "FMUL2")
  }

  test("FADD2 scalar selectors broadcast one register without consuming its neighbour") {
    pair("R7.F32", "R8.F32x2", Vec(0x41580000, 0x40d00000))
    pair("R6.F32x2", "R9.F32", Vec(0xc0300000, 0x40d00000))
  }

  test("FADD2 FTZ applies independently to inputs and results") {
    val tiny = Vec(1, 0x80000001, 0x80000000, 0x80000000)
    pair("R6.F32x2", "R8.F32x2", Vec(1, 0x80000001), tiny)
    pair("R6.F32x2", "R8.F32x2", Vec(0, 0x80000000), tiny, "FADD2.FTZ")
    pair("R6.F32x2", "R8.F32x2", Vec(0x80000000, 0), Vec(0x00800000, 0x00800001, 0x80800001, 0x80800000), "FADD2.FTZ")
  }

  test("FADD2 preserves pair sign modifiers, immediate broadcasts and uniform zero") {
    pair("-R6.F32x2", "|R8.F32x2|", Vec(0x3fe00000, 0xc0d00000))
    pair("R6.F32x2", "2.0", Vec(0x40500000, 0x41480000))
    pair("R6.F32x2", "URZ.F32x2", Vec(0x3fa00000, 0x41280000))
  }

  test("FADD2 rejects misaligned, out-of-range pairs and unsupported rounding") {
    Vec(
      "FADD2 R7, R6.F32x2, R8.F32x2",
      "FADD2 R6, R7.F32x2, R8.F32x2",
      "FADD2 R6, R254.F32x2, R8.F32x2",
      "FADD2 R6, R6.F32x2, UR80.F32x2",
      "FADD2.RM R6, R6.F32x2, R8.F32x2"
    ).foreach(form => assert(program(100, s"$form; EXIT").lower().isLeft, form))
  }
}
