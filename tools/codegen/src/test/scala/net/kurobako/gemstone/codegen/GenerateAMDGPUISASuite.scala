package net.kurobako.gemstone.codegen

class GenerateAMDGPUISASuite extends munit.FunSuite {
  test("reject a directory which is not a prepared reference view") {
    generator.inDirectory { directory =>
      intercept[IllegalArgumentException](GenerateAMDGPUISA.generate(directory))
    }
  }

  test("LLVM enum extraction preserves explicit, implicit and aliased selector values") {
    val header =
      """namespace Other { enum Code : unsigned { VALUE = 999 }; } // namespace Other
      |namespace DPP {
      |enum First : unsigned { UNUSED = 42 };
      |enum Control : int64_t { FIRST = 0x100, NEXT, ALIAS = FIRST, AFTER_ALIAS, NEGATIVE = -1, HIGH = 1 << 7, MASK = HIGH | NEXT };
      |} // namespace DPP
      |""".stripMargin
    assertEquals(
      GenerateAMDGPUISA.enumValues(header, "DPP", "Control"),
      Vector(
        "FIRST"       -> 256,
        "NEXT"        -> 257,
        "ALIAS"       -> 256,
        "AFTER_ALIAS" -> 257,
        "NEGATIVE"    -> -1,
        "HIGH"        -> 128,
        "MASK"        -> 385
      )
    )
    intercept[IllegalArgumentException](GenerateAMDGPUISA.enumValues(header, "DPP", "Missing"))
  }

  test("LLVM enum extraction rejects unknown expressions instead of silently dropping machine values") {
    intercept[IllegalArgumentException](GenerateAMDGPUISA.enumValues(
      "namespace DPP { enum Control { FIRST = unsupported(1), NEXT }; } // namespace DPP",
      "DPP",
      "Control"
    ))
  }

  test("metadata catalog JSON interns strings and schemas with stable offsets") {
    import GenerateAMDGPUISA.*
    val operands = Vector(Operand(
      "vdst",
      "VGPR",
      input = false,
      output = true,
      implicitOperand = false,
      bits = 32,
      dataFormat = "NUM_F32"
    ))
    val metadata = Vector(
      Vector(Instruction("v_add_f32", "GFX6", "VOP2", 17, operands, Vector("VALU"))),
      Vector(Instruction("v_add_f32", "RDNA3", "VOP2", 17, operands, Vector("VALU", "ARITHMETIC")))
    )
    val json    = ujson.read(GenerateAMDGPUISA.renderMetadataJson(metadata))
    val strings = json("strings").arr.map(_.str).toVector
    assertEquals(strings, strings.sorted)
    assertEquals(strings.toSet, Set("RDNA3", "GFX6", "VOP2", "VGPR", "vdst", "NUM_F32", "VALU", "ARITHMETIC"))
    val schemas = json("schemas").arr.map(_.arr.map { operand =>
      val values = operand.arr
      (
        values(0).num.toInt,
        values(1).num.toInt,
        values(2).bool,
        values(3).num.toInt,
        values(4).num.toInt,
        values(5).bool
      )
    }.toVector).toVector
    assertEquals(
      schemas,
      Vector(Vector((strings.indexOf("vdst"), strings.indexOf("VGPR"), false, 32, strings.indexOf("NUM_F32"), true)))
    )
    assertEquals(json("encodings").arr.size, 2)
    assertEquals(json("encodings").arr.map(row => strings(row("isa").num.toInt)).toVector, Vector("GFX6", "RDNA3"))
    assertEquals(
      json("encodings").arr.map(row => row("groups").arr.map(id => strings(id.num.toInt)).toVector).toVector,
      Vector(Vector("VALU"), Vector("VALU", "ARITHMETIC"))
    )
    assert(json("encodings").arr.forall(_.obj.keySet == Set("isa", "format", "opcode", "schema", "groups")))
    assertEquals(
      json("metadata").arr.map(entry => entry.arr.map(_.num.toInt).toVector).toVector,
      Vector(Vector(0, 1), Vector(1, 1))
    )
  }
}
