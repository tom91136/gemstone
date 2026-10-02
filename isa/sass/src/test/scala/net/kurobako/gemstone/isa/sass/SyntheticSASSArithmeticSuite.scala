package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*

class SyntheticSASSArithmeticSuite extends SyntheticSASSExecutionSuiteBase {
  test("unsupported instructions preserve the kernel, architecture, offset and opcode in diagnostics") {
    val m      = CubinSuiteFixtures.module
    val result = ListingDecoder.parse("saxpy:\n/*0000*/ NOP;\n/*0010*/ NOP;\n/*0020*/ UNSUPPORTED;", "saxpy", m.sm)
    assert(result.left.exists(_.contains(s"saxpy+0x20 (sm_${m.sm}): UNSUPPORTED")))
  }

  test("preserved vendor modifiers and operand qualifiers do not acquire execution semantics") {
    val module = CubinSuiteFixtures.module
    Vec("MOV.FUTURE R0, R1;", "MOV R0, R1.FUTURE;", "MOV R0, XR1;").foreach { source =>
      val parsed   = Parser.parse(source)
      val rejected = parsed.fold(
        _ => true,
        operation => {
          val instruction = DecodedInstruction(0, operation, None, None)
          val function    = CubinSuiteFixtures.functions.head.copy(instructions = Vec(instruction))
          SASSFixture.lower(module, module.kernels.head, Vec(function)).isLeft
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
      Vec("RZ.CC", "|R3|", "R4", "PT"),
      None
    )
    val result =
      SASSFixture.lower(m, m.kernels.head, Vec(CubinSuiteFixtures.functions.head.copy(instructions = Vec(instruction))))
    assert(result.isRight)
    val unsupported = instruction.rewrite(modifiers = Vec(Instruction.Modifier("GT"), Instruction.Modifier("AND")))
    val rejected    = SASSFixture.lower(
      m,
      m.kernels.head,
      Vec(CubinSuiteFixtures.functions.head.copy(instructions = Vec(unsupported)))
    )
    assert(rejected.left.exists(_.contains("FSET condition-code output is not implemented")))
    val newer = SASSFixture.lower(
      m.copy(sm = 86),
      m.kernels.head,
      Vec(CubinSuiteFixtures.functions.head.copy(instructions = Vec(instruction)))
    )
    assert(newer.left.exists(_.contains("FSET condition-code output is not implemented")))
  }

  test("sm35 FSET.GTU.CC drives CC.NEU for numeric, unordered, predicate-masked, and overwritten flags") {
    def check(a: String, b: String, predicate: String, expected: Int, overwrite: Boolean = false): Unit = {
      val prefix = Vec(
        ("MOV", s"R0,$a", None),
        ("MOV", s"R1,$b", None),
        ("ISETP.EQ.AND", s"P0,PT,RZ,${if (predicate == "PT") "RZ" else "1"},PT", None),
        ("FSET.GTU.AND", s"RZ.CC,|R0|,|R1|,$predicate", None)
      ) ++
        (if (overwrite) Vec(("FSET.GTU.AND", "RZ.CC,|R1|,|R0|,PT", None))
         else Vec.empty[(String, String, Option[String])])
      val branchIndex = prefix.size
      val yesIndex    = branchIndex + 3
      val endIndex    = branchIndex + 4
      val body        = prefix ++ Vec(
        ("BRA", s"CC.NEU,0x${(yesIndex * 16).toHexString}", None),
        ("MOV", "R2,0", None),
        ("BRA", s"0x${(endIndex * 16).toHexString}", None),
        ("MOV", "R2,1", None),
        ("LDC.64", "R4,c[0x0][0x160]", None),
        ("STG.E", "[R4.64],R2", None),
        ("EXIT", "", None)
      )
      executeSynthetic(body, Vec(expected), sm = 35)
    }
    check("0x40000000", "0x3f800000", "PT", 1)
    check("0x3f800000", "0x3f800000", "PT", 0)
    check("0x7fc00000", "0x3f800000", "PT", 1)
    check("0x3f800000", "0x7fc00000", "PT", 1)
    check("0x40000000", "0x3f800000", "P0", 0)
    check("0x40000000", "0x3f800000", "PT", 0, overwrite = true)
  }

  test("wide signed and unsigned IMAD preserve negative 32-bit operands") {
    def executeUnsigned(unsigned: Boolean, expected: Vec[Int]): Unit = {
      val opcode = if (unsigned) "IMAD.WIDE.U32" else "IMAD.WIDE"
      executeSynthetic(
        Vec(
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

    executeUnsigned(unsigned = false, Vec(-2, -1))
    executeUnsigned(unsigned = true, Vec(-2, 1))
  }

  test("carry, extended comparison, float negation and aliased predicates retain their inputs") {
    val body = Vec(
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
    executeSynthetic(body, Vec(15))
  }

  test("LEA subtraction propagates the full 64-bit complement and zero-operand carry") {
    executeSynthetic(
      Vec(
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
      Vec(98, 29)
    )
  }

  test("predicate packing retains unselected bits and restores a selected byte") {
    executeSynthetic(
      Vec(
        ("MOV", "R4,0x55", None),
        ("R2P", "PR,R4,0x7f", None),
        ("MOV", "R5,0xa50000ff", None),
        ("P2R.B1", "R5,R5,0x7f", None),
        ("R2P", "PR,RZ,0x7f", None),
        ("R2P", "PR,R5.reuse.B1,0x7f", None),
        ("P2R", "R6,R5,0x7f", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec(0xa50055d5)
    )
  }

  test("funnel shifts, two carry outputs and extended ordered comparison") {
    executeSynthetic(
      Vec(
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
      Vec(2)
    )
  }

  test("directed binary32 multiplication retains the exact product before rounding") {
    executeSynthetic(
      Vec(
        ("MOV", "R0,0x3f800001", None),
        ("FMUL.RP", "R4,R0,R0", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec(0x3f800003)
    )
  }

  test("FFMA preserves each rounding direction for positive and negative results") {
    // The exact result lies three quarters of an ulp beyond +/-1.
    for {
      mode     <- Vec("RN", "RM", "RP", "RZ")
      negative <- Vec(false, true)
    } {
      val sign         = if (negative) 0x80000000 else 0
      val awayFromZero = mode == "RN" || mode == (if (negative) "RM" else "RP")
      executeSynthetic(
        Vec(
          ("MOV", s"R0,0x${Integer.toHexString(0x3f800000 | sign)}", None),
          ("MOV", "R1,0x3f800000", None),
          ("MOV", s"R4,0x${Integer.toHexString(0x33c00000 | sign)}", None),
          (s"FFMA.$mode", "R4,R0,R1,R4", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec((if (awayFromZero) 0x3f800001 else 0x3f800000) | sign)
      )
    }
  }

  test("DFMA preserves each rounding direction for positive and negative results") {
    for {
      mode     <- Vec("RN", "RM", "RP", "RZ")
      negative <- Vec(false, true)
    } {
      val sign         = if (negative) 0x80000000 else 0
      val awayFromZero = mode == "RN" || mode == (if (negative) "RM" else "RP")
      executeSynthetic(
        Vec(
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
        Vec(if (awayFromZero) 1 else 0, 0x3ff00000 | sign)
      )
    }
  }

  test("modern MUFU trigonometry uses cycles and preserves large integral periods") {
    for (
      (op, input, expected) <- Vec(
        ("SIN", 0.25f, 1.0f),
        ("SIN", -0.25f, -1.0f),
        ("COS", 16777216.0f, 1.0f),
        ("SIN", -0.0f, -0.0f)
      )
    ) executeSynthetic(
      Vec(
        ("MOV", s"R0,0x${Integer.toHexString(java.lang.Float.floatToRawIntBits(input))}", None),
        (s"MUFU.$op", "R4,R0", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec(java.lang.Float.floatToRawIntBits(expected))
    )
  }

  test("signed warp extrema retain negative values across the direct SASS compiler") {
    Vec("MIN" -> -16, "MAX" -> 15).foreach { case (operation, expected) =>
      executeSynthetic(
        Vec(
          ("S2R", "R0,SR_TID.X", None),
          ("IADD3", "R4,R0,-16,RZ", None),
          (s"REDUX.$operation.S32", "R4,R4", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.fill(32)(expected)
      )
    }
  }

  test("legacy IADD condition codes retain low-word ordering for extended bounds checks") {
    executeSynthetic(
      Vec(
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
      Vec.fill(16)(1) ++ Vec.fill(16)(0),
      sm = 35
    )
  }

  test("FP64 reciprocal seeds consume the high word and compare as register pairs") {
    for ((operation, high) <- Vec(("RCP64H", "0x40000000"), ("RSQ64H", "0x40100000")))
      executeSynthetic(
        Vec(
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
        Vec(0x3fe00000)
      )
  }

  test("FP32 exceptional literals and absolute modifiers preserve their floating bits") {
    executeSynthetic(
      Vec(
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
      Vec(0x7f800000, 0xff800000, 0x7f800000, 1)
    )
  }

  test("MUFU double high-word immediates accept numeric and raw binary operands") {
    for {
      encoded <- Vec(false, true)
      value   <- Vec(6.0, -6.0)
    }
      executeSynthetic(
        Vec(
          ("LDC.64", "R4,c[0x0][0x160]", None),
          ("MUFU.RCP64H", s"R0,$value", None),
          ("STG.E", "[R4.64],R0", None),
          ("EXIT", "", None)
        ),
        Vec((java.lang.Double.doubleToRawLongBits(1.0 / value) >>> 32).toInt),
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
      Vec(
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
      Vec(1)
    )
  }

  test("double conversion and absolute comparisons preserve negative integer inputs") {
    executeSynthetic(
      Vec(
        ("MOV", "R0,-7", None),
        ("I2F.F64", "R4,R0", None),
        ("DSETP.LT.AND", "P0,PT,|R4|,+INF,PT", None),
        ("F2I.S32.F64.TRUNC", "R6,R4", None),
        ("SEL", "R6,R6,0,P0", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec(-7)
    )
  }

  test("unapplied constant-bank relocations fail explicitly") {
    val m          = CubinSuiteFixtures.module
    val constant   = m.sections.find(_.name.startsWith(".nv.constant0")).get
    val relocation = Cubin.Section(m.sections.size, ".rel" + constant.name, 9, 0, 16, 0, constant.index, Vec.empty)
    assert(SASSFixture.lower(
      m.copy(sections = m.sections :+ relocation),
      m.kernels.head,
      CubinSuiteFixtures.functions
    ).left.exists(_.contains("relocations")))
  }
}
