package net.kurobako.gemstone.codegen

class GenerateAMDGPUISASuite extends munit.FunSuite {
  test("processor wave defaults follow explicit and inherited LLVM features") {
    def refs(names: String*): ujson.Arr = ujson.Arr.from(names.map(name => ujson.Obj("def" -> name)))
    val processors                      = ujson.Obj.from(Vector(
      "gfx1036" -> "Modern",
      "gfx1200" -> "Modern",
      "gfx1250" -> "FeatureWavefrontSize32",
      "gfx1251" -> "FeatureWavefrontSize32",
      "gfx90a"  -> "Legacy"
    ).map { case (name, feature) => name -> ujson.Obj("Name" -> name, "Features" -> refs(feature)) })
    val tables = ujson.Obj(
      "Processor"                     -> processors,
      "GCNSubtargetFeatureGeneration" -> ujson.Obj("Modern" -> ujson.Obj(), "Legacy" -> ujson.Obj()),
      "SubtargetFeature"              -> ujson.Obj(
        "Modern"                 -> ujson.Obj("Implies" -> refs()),
        "Legacy"                 -> ujson.Obj("Implies" -> refs("FeatureWavefrontSize64")),
        "FeatureWavefrontSize32" -> ujson.Obj("Implies" -> refs()),
        "FeatureWavefrontSize64" -> ujson.Obj("Implies" -> refs())
      )
    )
    val (scala, native) = GenerateAMDGPUISA.renderTargetDefaults(tables)
    for (source <- Vector(scala, native)) {
      for (name <- Vector("gfx1036", "gfx1200", "gfx12", "gfx1250", "gfx1251"))
        assert(source.contains(s"\"$name\""))
      assert(!source.contains("\"gfx90a\""))
    }
    tables("Processor") = ujson.Obj()
    intercept[IllegalArgumentException](GenerateAMDGPUISA.renderTargetDefaults(tables))
  }

  test("kernel descriptor property masks come from LLVM bit ranges") {
    val rendered = GenerateAMDGPUISA.renderKernelCodeProperties(
      "KERNEL_CODE_PROPERTY(ENABLE_WAVEFRONT_SIZE32, 10, 1), KERNEL_CODE_PROPERTY(RESERVED0, 7, 3)"
    )
    assert(rendered.contains("val ENABLE_WAVEFRONT_SIZE32: Int = 0x400"))
    assert(!rendered.contains("RESERVED0"))
    intercept[IllegalArgumentException](GenerateAMDGPUISA.renderKernelCodeProperties(""))
  }

  test("ELF machine names and flag mask come from the LLVM table") {
    val rendered = GenerateAMDGPUISA.renderELFMachines(
      """EF_AMDGPU_MACH = 0x0ff,
        |X(0x45, EF_AMDGPU_MACH_AMDGCN_GFX1036, "gfx1036")
        |X(0x01, EF_AMDGPU_MACH_R600_R600, "r600")
        |X(0x51, EF_AMDGPU_MACH_AMDGCN_GFX9_GENERIC, "gfx9-generic")
        |""".stripMargin
    )
    assert(rendered.contains("val Mask: Int = 0x0ff"))
    assert(rendered.contains("0x45 -> \"gfx1036\""))
    assert(rendered.contains("0x51 -> \"gfx9-generic\""))
    assert(!rendered.contains("r600"))
    intercept[IllegalArgumentException](GenerateAMDGPUISA.renderELFMachines("EF_AMDGPU_MACH = 0x0ff,"))
  }

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
      Vector(Instruction("v_add_f32", "RDNA3", "VOP2", 17, operands, Vector("VALU", "ARITHMETIC"), Vector("gfx1100")))
    )
    val json    = ujson.read(GenerateAMDGPUISA.renderMetadataJson(metadata))
    val strings = json("strings").arr.map(_.str).toVector
    assertEquals(strings, strings.sorted)
    assertEquals(
      strings.toSet,
      Set("RDNA3", "GFX6", "VOP2", "VGPR", "vdst", "NUM_F32", "VALU", "ARITHMETIC", "gfx1100")
    )
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
    assertEquals(json("encodings")(0).obj.keySet.toSet, Set("isa", "format", "opcode", "schema", "groups"))
    assertEquals(json("encodings")(1)("processors").arr.map(id => strings(id.num.toInt)).toVector, Vector("gfx1100"))
    assertEquals(
      json("metadata").arr.map(entry => entry.arr.map(_.num.toInt).toVector).toVector,
      Vector(Vector(0, 1), Vector(1, 1))
    )
  }
}
