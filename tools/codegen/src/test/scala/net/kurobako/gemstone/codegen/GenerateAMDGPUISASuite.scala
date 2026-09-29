package net.kurobako.gemstone.codegen

class GenerateAMDGPUISASuite extends munit.FunSuite {
  test("reject a directory which is not a prepared reference view") {
    generator.inDirectory { directory =>
      intercept[IllegalArgumentException](GenerateAMDGPUISA.generate(directory))
    }
  }

  test("metadata catalog JSON interns strings and schemas with stable offsets") {
    import GenerateAMDGPUISA.*
    val operands = Vector(Operand("vdst", "VGPR", input = false, output = true, implicitOperand = false))
    val metadata = Vector(
      Vector(Instruction("v_add_f32", "GFX6", "VOP2", 17, operands)),
      Vector(Instruction("v_add_f32", "RDNA3", "VOP2", 17, operands))
    )
    val json    = ujson.read(GenerateAMDGPUISA.renderMetadataJson(metadata))
    val strings = json("strings").arr.map(_.str).toVector
    assertEquals(strings, strings.sorted)
    assertEquals(strings.toSet, Set("RDNA3", "GFX6", "VOP2", "VGPR", "vdst"))
    val schemas = json("schemas").arr.map(_.arr.map { operand =>
      val values = operand.arr
      (values(0).num.toInt, values(1).num.toInt, values(2).bool)
    }.toVector).toVector
    assertEquals(schemas, Vector(Vector((strings.indexOf("vdst"), strings.indexOf("VGPR"), false))))
    assertEquals(json("encodings").arr.size, 2)
    assertEquals(json("encodings").arr.map(row => strings(row("isa").num.toInt)).toVector, Vector("GFX6", "RDNA3"))
    assert(json("encodings").arr.forall(_.obj.keySet == Set("isa", "format", "opcode", "schema")))
    assertEquals(
      json("metadata").arr.map(entry => entry.arr.map(_.num.toInt).toVector).toVector,
      Vector(Vector(0, 1), Vector(1, 1))
    )
  }
}
