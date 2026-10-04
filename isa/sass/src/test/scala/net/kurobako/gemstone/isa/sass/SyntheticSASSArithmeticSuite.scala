package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*

class SyntheticSASSArithmeticSuite extends SyntheticSASSExecutionSuiteBase {
  Vec.nonEmpty(35, 50, 60).foreach { sm =>
    test(s"ISCADD32I shifts and adds a negative literal on sm_$sm") {
      executeSynthetic(
        Vec.nonEmpty(
          ("MOV", "R4,128", None),
          ("ISCADD32I", "R4,R4,-0x3f800000,23", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("STG.E", "[R2],R4", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(0x00800000),
        sm = sm
      )
    }
  }

  test("UFSEL selects a floating constant through a negated uniform predicate") {
    executeSynthetic(
      Vec.nonEmpty(
        ("UPLOP3.LUT", "UP0,UPT,UPT,UPT,UPT,0x80,0", None),
        ("UMOV", "UR4,0", None),
        ("UFSEL", "UR4,UR4,1,!UP0", None),
        ("MOV", "R6,UR4", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0x3f800000),
      sm = 120
    )
  }

  Vec.nonEmpty(35, 50, 60).foreach { sm =>
    test(s"BRA.CC.EQ observes the legacy floating zero flag on sm_$sm") {
      executeSynthetic(
        Vec.nonEmpty(
          ("MOV", "R4,0x7f800000", None),
          ("FSET.NEU.FTZ.AND", "RZ.CC,|R4|,|R4|,PT", None),
          ("BRA.CC.EQ", "0x50", None),
          ("MOV", "R6,0", None),
          ("BRA", "0x60", None),
          ("MOV", "R6,1", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("STG.E", "[R2],R6", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(1),
        sm = sm
      )
    }
  }

  test("integer conversion negates the absolute source before writing") {
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", "R4,-7", None),
        ("I2I.S32.S32", "R4,-|R4|", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(-7),
      sm = 50
    )
  }

  Vec.nonEmpty(50, 60).foreach { sm =>
    test(s"legacy LOP3.NZ produces the result predicate on sm_$sm") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("MOV", "R5,0", None),
          ("LOP3.NZ", "P0,RZ,R0,R5,RZ,0xf8", None),
          ("SEL", "R6,RZ,1,!P0", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2],R6", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(0, 1, 1, 1),
        sm = sm
      )
    }
  }

  test("UPLOP3 writes a uniform predicate from its LUT") {
    executeSynthetic(
      Vec.nonEmpty(
        ("UPLOP3.LUT", "UP0,UPT,UPT,UPT,UPT,0x80,0x8", None),
        ("MOV", "R6,7", Some("@UP0")),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(7),
      sm = 120
    )
  }

  for {
    unsigned <- Vec.nonEmpty(false, true)
    minimum  <- Vec.nonEmpty(false, true)
    input    <- Vec.nonEmpty(Int.MaxValue, -1)
  } test(s"Hopper fused add/minmax preserves wrapping addition unsigned=$unsigned minimum=$minimum input=$input") {
    val sum      = input + 1
    val less     = if (unsigned) java.lang.Integer.compareUnsigned(sum, 1) < 0 else sum < 1
    val expected = if (less == minimum) sum else 1
    val suffix   = if (unsigned) ".U32" else ""
    val choice   = if (minimum) "PT" else "!PT"
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", s"R4,0x${input.toHexString}", None),
        ("MOV", "R5,1", None),
        (s"VIADDMNMX$suffix", s"R4,R4,1,R5,$choice", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(expected),
      sm = 90
    )
  }

  for {
    minimum <- Vec.nonEmpty(false, true)
    values  <- Vec.nonEmpty(
      (3.0, 2.0),
      (-0.0, 0.0),
      (Double.NaN, 2.0),
      (2.0, Double.NaN),
      (Double.NaN, Double.NaN),
      (Double.PositiveInfinity, 2.0)
    )
  } test(s"double min/max preserves pairs and numeric NaN handling: $values minimum=$minimum") {
    val (a, b)   = values
    val expected = if (a.isNaN) b else if (b.isNaN) a else if (minimum) Math.min(a, b) else Math.max(a, b)
    val result   = java.lang.Double.doubleToRawLongBits(expected)
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", s"R4,0x${java.lang.Double.doubleToRawLongBits(a).toInt.toHexString}", None),
        ("MOV", s"R5,0x${(java.lang.Double.doubleToRawLongBits(a) >>> 32).toHexString}", None),
        ("MOV", s"R6,0x${java.lang.Double.doubleToRawLongBits(b).toInt.toHexString}", None),
        ("MOV", s"R7,0x${(java.lang.Double.doubleToRawLongBits(b) >>> 32).toHexString}", None),
        ("DMNMX", s"R4,R4,R6,${if (minimum) "PT" else "!PT"}", None),
        ("STG.E.64", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(result.toInt, (result >>> 32).toInt),
      sm = 35
    )
  }

  Vec.nonEmpty(35, 50, 60).foreach { sm =>
    test(s"legacy FCHK.DIVIDE exponent boundaries on sm_$sm") {
      Vec.nonEmpty(
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
        executeSynthetic(
          Vec.nonEmpty(
            ("MOV", s"R4,${a.toLong << 23}", None),
            ("MOV", s"R5,${b.toLong << 23}", None),
            ("FCHK.DIVIDE", "P0,R4,R5", None),
            ("SEL", "R6,RZ,1,!P0", None),
            ("LDC.64", "R2,c[0x0][0x160]", None),
            ("STG.E", "[R2],R6", None),
            ("EXIT", "", None)
          ),
          Vec.nonEmpty(expected),
          sm = sm
        )
      }
    }
  }

  for (value <- Vec.nonEmpty(Int.MinValue, -7, 0))
    test(s"integer-to-float absolute source avoids signed overflow for $value") {
      executeSynthetic(
        Vec.nonEmpty(
          ("LDC.64", "R10,c[0x0][0x160]", None),
          ("MOV", s"R4,0x${value.toHexString}", None),
          ("I2F.F32.S32", "R4,|R4|", None),
          ("STG.E", "[R10],R4", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(java.lang.Float.floatToRawIntBits(Math.abs(value.toLong).toFloat)),
        sm = 50
      )
    }

  for {
    comparison <- Vec.nonEmpty("LT", "GEU")
    condition  <- Vec.nonEmpty(-1.0f, 0.0f, Float.NaN)
  } test(s"floating compare/select applies $comparison to the condition $condition") {
    val selected = if (comparison == "LT") condition < 0.0f else condition >= 0.0f || condition.isNaN
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", s"R4,0x${java.lang.Float.floatToRawIntBits(condition).toHexString}", None),
        ("MOV", "R5,0x40000000", None),
        (s"FCMP.$comparison.FTZ", "R4,R5,1,R4", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(java.lang.Float.floatToRawIntBits(if (selected) 2.0f else 1.0f)),
      sm = 35
    )
  }

  for {
    unsigned <- Vec.nonEmpty(false, true)
    minimum  <- Vec.nonEmpty(false, true)
  } test(s"64-bit integer min/max retains high words unsigned=$unsigned minimum=$minimum") {
    val value = if (unsigned == minimum) 1L else Long.MinValue
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", "R4,0", None),
        ("MOV", "R5,0x80000000", None),
        ("MOV", "R6,1", None),
        ("MOV", "R7,0", None),
        (s"IMNMX.${if (unsigned) "U64" else "S64"}", s"R4,R4,R6,${if (minimum) "PT" else "!PT"},!PT", None),
        ("STG.E.64", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(value.toInt, (value >>> 32).toInt),
      sm = 120
    )
  }

  for (value <- Vec.nonEmpty(Int.MinValue, -7, 0))
    test(s"legacy signed absolute conversion preserves 32-bit wrapping: $value") {
      executeSynthetic(
        Vec.nonEmpty(
          ("LDC.64", "R10,c[0x0][0x160]", None),
          ("MOV", s"R4,0x${value.toHexString}", None),
          ("I2I.S32.S32", "R4,|R4|", None),
          ("STG.E", "[R10],R4", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(Math.abs(value)),
        sm = 50
      )
    }

  test("uniform integer conversion stores both double-precision result words") {
    val bits = java.lang.Double.doubleToRawLongBits(-7.0)
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("UMOV", "UR8,-7", None),
        ("UI2F.F64.S32", "UR4,UR8", None),
        ("MOV.64", "R4,UR4", None),
        ("STG.E.64", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(bits.toInt, (bits >>> 32).toInt),
      sm = 120
    )
  }

  test("Kepler compressed float immediates retain their IEEE value") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", "R0,0x3fc00000", None),
        ("FADD", "R4,R0,1", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0x40200000),
      sm = 35,
      rewrite = instruction =>
        instruction.operation match {
          case _: Instruction.Fadd =>
            val decoded = InstructionDecoder.decodeTyped(
              instruction.address,
              LittleEndian.toVec(0xc2c001fc001c0011L, 8).toArray,
              "SM35"
            ).fold(fail(_), identity)
            assertEquals(decoded.operands.last, Operand.FloatingBits(FloatEncoding.F32Imm, 32, BigInt(0x3f800000)))
            decoded
          case _ => instruction
        }
    )
  }

  test("UISETP.U64 compares both halves of uniform register pairs") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("UMOV", "UR4,0", None),
        ("UMOV", "UR5,1", None),
        ("UISETP.GT.U64.AND", "UP0,UPT,UR4,512,UPT", None),
        ("UMOV", "UR6,1", None),
        ("USEL", "UR6,UR6,URZ,UP0", None),
        ("UVIMNMX.U32", "UR6,UR6,URZ,!UPT", None),
        ("MOV", "R4,UR6", None),
        ("VIMNMX.S32", "R4,R4,-1,!PT", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(1),
      sm = 120
    )
  }

  test("UIADD3.64 reads aliased uniform pairs before storing its result") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("UMOV", "UR4,1", None),
        ("UMOV", "UR5,2", None),
        ("UMOV", "UR6,0", None),
        ("UMOV", "UR7,4", None),
        ("UIADD3.64", "UR6,-UR4,UR6,URZ", None),
        ("MOV.64", "R4,UR6", None),
        ("IADD.64", "R4,R4,UR4", None),
        ("STG.E.64", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0, 4),
      sm = 120
    )
  }

  test("IADD32I adds a negated register and a negative immediate with wrapping arithmetic") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", "R4,0x7fffffff", None),
        ("IADD32I", "R4,-R4,-257", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0x7fffff00),
      sm = 50
    )
  }

  test("decoded XMAD retains the unsigned first operand when the second operand is signed") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", "R0,0x8000", None),
        ("MOV", "R3,0xfffe0000", None),
        ("ISETP.EQ.AND", "P1,PT,RZ,RZ,PT", None),
        ("XMAD.U16.S16", "R3,R0,R3.H1,RZ", Some("@P1")),
        ("STG.E", "[R10],R3", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(-65536),
      sm = 50,
      rewrite = instruction =>
        instruction.operation match {
          case _: Instruction.Xmad =>
            val bytes = LittleEndian.toVec(0x5b027f8800310003L, 8).toArray ++ Array[Byte](0xfd.toByte, 7, 0)
            InstructionDecoder.decodeTyped(instruction.address, bytes, "SM50").fold(fail(_), identity)
          case _ => instruction
        }
    )
  }

  test("Hopper VIADD wraps 32-bit sums with aliased sources and respects predicates") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("MOV", "R4,0xffffff80", None),
        ("VIADD", "R4,R4,0x100", None),
        ("MOV", "R5,0x7fffffff", None),
        ("VIADD", "R5,R4,R5", None),
        ("VIADD", "R5,R5,-R4", None),
        ("ISETP.EQ.AND", "P0,PT,RZ,1,PT", None),
        ("VIADD", "R5,R5,1", Some("@P0")),
        ("VIADD", "R5,R5,1", Some("@!P0")),
        ("STG.E", "[R2.64],R5", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(Int.MinValue),
      sm = 90
    )
  }

  test("unsupported instructions preserve the kernel, architecture, offset and opcode in diagnostics") {
    val m      = CubinSuiteFixtures.module
    val result = ListingDecoder.parse("saxpy:\n/*0000*/ NOP;\n/*0010*/ NOP;\n/*0020*/ UNSUPPORTED;", "saxpy", m.sm.raw)
    assert(result.left.exists(_.contains(s"saxpy+0x20 (sm_${m.sm}): UNSUPPORTED")))
  }

  test("preserved vendor modifiers and operand qualifiers do not acquire execution semantics") {
    val module = CubinSuiteFixtures.module
    Vec.nonEmpty("MOV.FUTURE R0, R1;", "MOV R0, R1.FUTURE;", "MOV R0, XR1;").foreach { source =>
      val parsed   = Parser.parse(source)
      val rejected = parsed.fold(
        _ => true,
        operation => {
          val instruction = DecodedInstruction(0, operation, None, None)
          val function    = CubinSuiteFixtures.functions.head.copy(instructions = Vec.nonEmpty(instruction))
          SASSFixture.lower(module, module.kernels.head, Vec.nonEmpty(function)).isLeft
        }
      )
      assert(rejected, source)
    }
  }

  test("only the observed sm35 unordered-greater RZ.CC form supplies a zero flag") {
    val m           = CubinSuiteFixtures.module.copy(sm = 35)
    val instruction = SASSFixture.instruction(
      8,
      "FSET.GTU.AND",
      Vec.nonEmpty("RZ.CC", "|R3|", "R4", "PT"),
      None
    )
    val result =
      SASSFixture.lower(
        m,
        m.kernels.head,
        Vec.nonEmpty(CubinSuiteFixtures.functions.head.copy(instructions = Vec.nonEmpty(instruction)))
      )
    assert(result.isRight)
    val unsupported =
      instruction.rewrite(modifiers = Vec.nonEmpty(Instruction.Modifier("GT"), Instruction.Modifier("AND")))
    val rejected = SASSFixture.lower(
      m,
      m.kernels.head,
      Vec.nonEmpty(CubinSuiteFixtures.functions.head.copy(instructions = Vec.nonEmpty(unsupported)))
    )
    assert(rejected.left.exists(_.contains("FSET condition-code output is not implemented")))
    val newer = SASSFixture.lower(
      m.copy(sm = 86),
      m.kernels.head,
      Vec.nonEmpty(CubinSuiteFixtures.functions.head.copy(instructions = Vec.nonEmpty(instruction)))
    )
    assert(newer.left.exists(_.contains("FSET condition-code output is not implemented")))
  }

  test("sm35 FSET.GTU.CC drives CC.NEU for numeric, unordered, predicate-masked, and overwritten flags") {
    def check(
        a: String,
        b: String,
        predicate: String,
        expected: Int,
        overwrite: Boolean = false,
        immediateInfinity: Boolean = false,
        sm: Int = 35,
        operation: String = "FSET.GTU.AND"
    ): Unit = {
      val prefix = Vec.nonEmpty(
        ("MOV", s"R0,$a", None),
        ("MOV", s"R1,$b", None),
        ("ISETP.EQ.AND", s"P0,PT,RZ,${if (predicate == "PT") "RZ" else "1"},PT", None),
        (operation, s"RZ.CC,|R0|,|R1|,$predicate", None)
      ) ++
        (if (overwrite) Vec.nonEmpty(("FSET.GTU.AND", "RZ.CC,|R1|,|R0|,PT", None))
         else Vec.empty[(String, String, Option[String])])
      val branchIndex = prefix.size
      val yesIndex    = branchIndex + 3
      val endIndex    = branchIndex + 4
      val body        = prefix ++ Vec.nonEmpty(
        ("BRA", s"CC.NEU,0x${(yesIndex * 16).toHexString}", None),
        ("MOV", "R2,0", None),
        ("BRA", s"0x${(endIndex * 16).toHexString}", None),
        ("MOV", "R2,1", None),
        ("LDC.64", "R4,c[0x0][0x160]", None),
        ("STG.E", "[R4.64],R2", None),
        ("EXIT", "", None)
      )
      for (binary <- if (sm == 35) Vec.nonEmpty(false, true) else Vec.nonEmpty(false)) executeSynthetic(
        body,
        Vec.nonEmpty(expected),
        sm = sm,
        rewrite = instruction =>
          if (binary && instruction.mnemonic.raw == "BRA" && instruction.operands.size == 2) {
            val decoded = InstructionDecoder.decodeTyped(
              instruction.address,
              LittleEndian.toVec(0x1200000020200034L, 8).toArray,
              "SM35"
            ).fold(fail(_), identity)
            decoded.rewrite(operands = Vec.nonEmpty(instruction.operands.last), guard = None)
          } else if (immediateInfinity && instruction.mnemonic.raw == "FSET")
            instruction.rewrite(operands =
              instruction.operands.updated(
                2,
                Operand.FloatingBits(FloatEncoding.F32Imm, 32, BigInt(0x7f800000))
              )
            )
          else instruction
      )
    }
    Vec.nonEmpty(35, 50, 60).foreach { sm =>
      check("0x7fc00000", "0x7f800000", "PT", 1, immediateInfinity = true, sm = sm, operation = "FSET.NEU.FTZ.AND")
      check("0x7f800000", "0x7f800000", "PT", 0, immediateInfinity = true, sm = sm, operation = "FSET.NEU.FTZ.AND")
    }
    check("0x7fc00000", "0x7f800000", "PT", 1, immediateInfinity = true)
    check("0x7f800000", "0x7f800000", "PT", 0, immediateInfinity = true)
    check("0x40000000", "0x3f800000", "PT", 1)
    check("0x3f800000", "0x3f800000", "PT", 0)
    check("0x7fc00000", "0x3f800000", "PT", 1)
    check("0x3f800000", "0x7fc00000", "PT", 1)
    check("0x40000000", "0x3f800000", "P0", 0)
    check("0x40000000", "0x3f800000", "PT", 0, overwrite = true)
  }

  test("wide signed and unsigned IMAD preserve negative 32-bit operands") {
    def executeUnsigned(unsigned: Boolean, expected: NonEmpty[Vec[Int]]): Unit = {
      val opcode = if (unsigned) "IMAD.WIDE.U32" else "IMAD.WIDE"
      executeSynthetic(
        Vec.nonEmpty(
          ("MOV", "R0,0xffffffff", None),
          ("MOV", "R1,2", None),
          (opcode, "R2,R0,R1,RZ", None),
          ("LDC.64", "R4,c[0x0][0x160]", None),
          ("STG.E.64", "[R4.64],R2", None),
          ("EXIT", "", None)
        ),
        expected,
        sm = 89
      )
    }

    executeUnsigned(unsigned = false, Vec.nonEmpty(-2, -1))
    executeUnsigned(unsigned = true, Vec.nonEmpty(-2, 1))
  }

  test("carry, extended comparison, float negation and aliased predicates retain their inputs") {
    val body = Vec.nonEmpty(
      ("MOV", "R0,0xffffffff", None),
      ("MOV", "R8,8", None),
      ("MOV", "R9,4", None),
      ("MOV", "R10,2", None),
      ("IADD3", "R4,P0,R0,2,RZ", None),
      ("SEL", "R5,R8,0,P0", None),
      ("ISETP.NE.U32.AND", "P1,PT,R4,0,PT", None),
      ("ISETP.NE.AND.EX", "P1,PT,RZ,RZ,PT,P1", None),
      ("ISETP.EQ.AND", "P0,P1,RZ,RZ,P1", None),
      ("SEL", "R6,R9,0,P0", None),
      ("IADD3", "R5,R5,R6,RZ", None),
      ("MOV", "R6,0x3f800000", None),
      ("FMUL", "R6,-R6,2", None),
      ("FSETP.EQ.AND", "P0,PT,R6,-2,PT", None),
      ("SEL", "R6,R10,0,P0", None),
      ("IADD3", "R5,R5,R6,R4", None),
      ("LDC.64", "R2,c[0x0][0x160]", None),
      ("STG.E", "[R2.64],R5", None),
      ("EXIT", "", None)
    )
    executeSynthetic(body, Vec.nonEmpty(15))
  }

  test("LEA subtraction propagates the full 64-bit complement and zero-operand carry") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("IMAD", "R4,R0,17,RZ", None),
        ("MOV", "R5,RZ", None),
        ("MOV", "R6,32", None),
        ("MOV", "R7,0x42", None),
        ("LEA", "R8,P0,-R4,R6,2", None),
        ("LEA.HI.X", "R9,~R4,R7,R5,2,P0", None),
        ("IADD3", "R6,R8,R9,RZ", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(98, 29)
    )
  }

  test("predicate packing retains unselected bits and restores a selected byte") {
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", "R4,0x55", None),
        ("R2P", "PR,R4,0x7f", None),
        ("MOV", "R5,0xa50000ff", None),
        ("P2R.B1", "R5,R5,0x7f", None),
        ("R2P.PR", "RZ,0x7f", None),
        ("R2P", "PR,R5.reuse.B1,0x7f", None),
        ("P2R", "R6,R5,0x7f", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0xa50055d5)
    )
  }

  test("funnel shifts, two carry outputs and extended ordered comparison") {
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", "R0,0xffffffff", None),
        ("IADD3", "R4,P0,P1,R0,1,R0", None),
        ("IADD3.X", "R5,RZ,RZ,RZ,P0,P1", None),
        ("SHF.R.U64", "R6,RZ,31,R5", None),
        ("ISETP.GT.U32.AND", "P0,PT,R4,0,PT", None),
        ("ISETP.GT.U32.AND.EX", "P0,PT,R5,1,PT,P0", None),
        ("SEL", "R6,R6,0,P0", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(2)
    )
  }

  test("directed binary32 multiplication retains the exact product before rounding") {
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", "R0,0x3f800001", None),
        ("FMUL.RP", "R4,R0,R0", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0x3f800003)
    )
  }

  test("FFMA preserves each rounding direction for positive and negative results") {
    // The exact result lies three quarters of an ulp beyond +/-1.
    for {
      mode     <- Vec.nonEmpty("RN", "RM", "RP", "RZ")
      negative <- Vec.nonEmpty(false, true)
    } {
      val sign         = if (negative) 0x80000000 else 0
      val awayFromZero = mode == "RN" || mode == (if (negative) "RM" else "RP")
      executeSynthetic(
        Vec.nonEmpty(
          ("MOV", s"R0,0x${Integer.toHexString(0x3f800000 | sign)}", None),
          ("MOV", "R1,0x3f800000", None),
          ("MOV", s"R4,0x${Integer.toHexString(0x33c00000 | sign)}", None),
          (s"FFMA.$mode", "R4,R0,R1,R4", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty((if (awayFromZero) 0x3f800001 else 0x3f800000) | sign)
      )
    }
  }

  test("DFMA preserves each rounding direction for positive and negative results") {
    for {
      mode     <- Vec.nonEmpty("RN", "RM", "RP", "RZ")
      negative <- Vec.nonEmpty(false, true)
    } {
      val sign         = if (negative) 0x80000000 else 0
      val awayFromZero = mode == "RN" || mode == (if (negative) "RM" else "RP")
      executeSynthetic(
        Vec.nonEmpty(
          ("MOV", "R0,0", None),
          ("MOV", s"R1,0x${Integer.toHexString(0x3ff00000 | sign)}", None),
          ("MOV", "R4,0", None),
          ("MOV", "R5,0x3ff00000", None),
          ("MOV", "R6,0", None),
          ("MOV", s"R7,0x${Integer.toHexString(0x3ca80000 | sign)}", None),
          (s"DFMA.$mode", "R6,R0,R4,R6", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("STG.E.64", "[R2.64],R6", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(if (awayFromZero) 1 else 0, 0x3ff00000 | sign)
      )
    }
  }

  test("modern MUFU trigonometry uses cycles and preserves large integral periods") {
    for (
      (op, input, expected) <- Vec.nonEmpty(
        ("SIN", 0.25f, 1.0f),
        ("SIN", -0.25f, -1.0f),
        ("COS", 16777216.0f, 1.0f),
        ("SIN", -0.0f, -0.0f)
      )
    ) executeSynthetic(
      Vec.nonEmpty(
        ("MOV", s"R0,0x${Integer.toHexString(java.lang.Float.floatToRawIntBits(input))}", None),
        (s"MUFU.$op", "R4,R0", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(java.lang.Float.floatToRawIntBits(expected))
    )
  }

  test("signed warp extrema retain negative values across the direct SASS compiler") {
    Vec.nonEmpty("MIN" -> -16, "MAX" -> 15).foreach { case (operation, expected) =>
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("IADD3", "R4,R0,-16,RZ", None),
          (s"REDUX.$operation.S32", "R4,R4", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(_ => expected)
      )
    }
  }

  test("legacy IADD condition codes retain low-word ordering for extended bounds checks") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("IADD", "RZ.CC,-R0,16", None),
        ("ISETP.GT.U32.X.AND", "P0,PT,RZ,RZ,PT", None),
        ("MOV", "R8,1", None),
        ("SEL", "R4,R8,0,P0", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(16)(_ => 1).concatNonEmpty(Vec.tabulateNonEmpty(16)(_ => 0)),
      sm = 35
    )
  }

  test("FP64 reciprocal seeds consume the high word and compare as register pairs") {
    for ((operation, high) <- Vec.nonEmpty(("RCP64H", "0x40000000"), ("RSQ64H", "0x40100000")))
      executeSynthetic(
        Vec.nonEmpty(
          ("MOV", "R8,0", None),
          ("MOV", s"R9,$high", None),
          ("MOV", "R10,0", None),
          (s"MUFU.$operation", "R11,R9", None),
          ("DSETP.LT.AND", "P0,PT,R10,R8,PT", None),
          ("SEL", "R4,R11,0,P0", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(0x3fe00000)
      )
  }

  test("FP32 exceptional literals and absolute modifiers preserve their floating bits") {
    executeSynthetic(
      Vec.nonEmpty(
        ("FADD", "R4,RZ,+INF", None),
        ("FADD", "R5,RZ,-INF", None),
        ("FADD", "R6,RZ,|-INF|", None),
        ("MOV", "R9,0x7fc00000", None),
        ("FSETP.GTU.FTZ.AND", "P0,PT,|R9|,+INF,PT", None),
        ("MOV", "R8,1", None),
        ("SEL", "R7,R8,0,P0", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E.128", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0x7f800000, 0xff800000, 0x7f800000, 1)
    )
  }

  test("MUFU double high-word immediates accept numeric and raw binary operands") {
    for {
      encoded <- Vec.nonEmpty(false, true)
      value   <- Vec.nonEmpty(6.0, -6.0)
    }
      executeSynthetic(
        Vec.nonEmpty(
          ("LDC.64", "R4,c[0x0][0x160]", None),
          ("MUFU.RCP64H", s"R0,$value", None),
          ("STG.E", "[R4.64],R0", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty((java.lang.Double.doubleToRawLongBits(1.0 / value) >>> 32).toInt),
        rewrite = instruction =>
          if (encoded && instruction.mnemonic.raw == "MUFU")
            instruction.rewrite(operands =
              instruction.operands.updated(
                1,
                Operand.FloatingBits(
                  FloatEncoding.F64Imm,
                  64,
                  BigInt(java.lang.Double.doubleToRawLongBits(value)) & ((BigInt(1) << 64) - 1)
                )
              )
            )
          else instruction
      )
  }

  test("double NaN and numeric predicates preserve boolean combinations") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R4,c[0x0][0x160]", None),
        ("MOV", "R0,0", None),
        ("MOV", "R1,0x7ff80000", None),
        ("MOV", "R8,1", None),
        ("MOV", "R9,2", None),
        ("MOV", "R10,0", None),
        ("DSETP.NAN.AND", "P0,P1,R0,RZ,PT", None),
        ("SEL", "R6,R8,R10,P0", None),
        ("DSETP.NUM.AND", "P0,PT,RZ,RZ,P1", None),
        ("SEL", "R7,R9,R10,P0", None),
        ("IADD3", "R6,R6,R7,RZ", None),
        ("STG.E", "[R4.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(1)
    )
  }

  test("double conversion and absolute comparisons preserve negative integer inputs") {
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", "R0,-7", None),
        ("I2F.F64", "R4,R0", None),
        ("DSETP.LT.AND", "P0,PT,|R4|,+INF,PT", None),
        ("F2I.S32.F64.TRUNC", "R6,R4", None),
        ("SEL", "R6,R6,0,P0", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(-7)
    )
  }

  test("unapplied constant-bank relocations fail explicitly") {
    val m          = CubinSuiteFixtures.module
    val constant   = m.sections.find(_.name.startsWith(".nv.constant0")).get
    val relocation =
      Cubin.Section(m.sections.nonNegativeSize, ".rel" + constant.name, 9, 0, 16, 0, constant.index.raw, Vec.empty)
    assert(SASSFixture.lower(
      m.copy(sections = m.sections :+ relocation),
      m.kernels.head,
      CubinSuiteFixtures.functions
    ).left.exists(_.contains("relocations")))
  }
  for (condition <- Vec.nonEmpty(-1.0f, -0.0f, 0.0f, 1.0f, Float.NaN))
    test(s"FCMP default GT compares $condition against zero") {
      for (opcode <- Vec.nonEmpty("FCMP", "FCMP.FTZ")) executeSynthetic(
        body = Vec.nonEmpty(
          ("LDC.64", "R10,c[0x0][0x160]", None),
          ("MOV", s"R4,0x${java.lang.Float.floatToRawIntBits(condition).toHexString}", None),
          ("MOV", "R5,0x40000000", None),
          (opcode, "R4,R5,1,R4", None),
          ("STG.E", "[R10],R4", None),
          ("EXIT", "", None)
        ),
        expected = Vec.nonEmpty(java.lang.Float.floatToRawIntBits(if (condition > 0.0f) 2.0f else 1.0f)),
        sm = 35
      )
    }

  for (
    (opcode, bits, selected) <- Vec.nonEmpty(
      ("UFSETP.GEU.AND", 0x7fc00000, true),
      ("UFSETP.GEU.AND", 0xbf800000, false),
      ("UFSETP.GEU.AND", 0x3f800000, true),
      ("UFSETP.GT.FTZ.AND", 1, false)
    )
  ) test(s"$opcode uses uniform float operands and paired predicates for bits=$bits") {
    executeSynthetic(
      body = Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("UMOV", s"UR4,0x${Integer.toUnsignedString(bits, 16)}", None),
        (opcode, "UP0,UP1,UR4,URZ,UPT", None),
        ("UMOV", "UR7,7", None),
        ("USEL", "UR6,UR7,9,UP0", None),
        ("USEL", "UR8,UR7,9,UP1", None),
        ("MOV", "R4,UR6", None),
        ("MOV", "R5,UR8", None),
        ("STG.E", "[R10],R4", None),
        ("STG.E", "[R10+4],R5", None),
        ("EXIT", "", None)
      ),
      expected = if (selected) Vec.nonEmpty(7, 9) else Vec.nonEmpty(9, 7),
      sm = 120
    )
  }

}
