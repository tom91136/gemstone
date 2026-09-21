package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path
import scala.util.Using

final class GenerateAMDGPUISASuite extends munit.FunSuite {
  test("reject a directory which is not a prepared reference view") {
    given Using.Releasable[Path] = path => { val _ = Files.deleteIfExists(path); () }
    Using.resource(Files.createTempDirectory("gemstone-amdgpu-codegen-test")) { directory =>
      intercept[IllegalArgumentException](GenerateAMDGPUISA.generate(directory))
    }
  }

  test("metadata catalog JSON interns strings and schemas with stable offsets") {
    import GenerateAMDGPUISA.{Instruction, Operand}
    val operands = Vector(Operand("vdst", "VGPR", input = false, output = true, implicitOperand = false))
    val metadata = Vector(
      Vector(Instruction("v_add_f32", "GFX6", "gfx6", "VOP2", 17, operands, "MesaACO")),
      Vector(Instruction("v_add_f32", "GFX11", "gfx11", "VOP2", 17, operands, "AMDXML"))
    )
    val json    = ujson.read(GenerateAMDGPUISA.renderMetadataJson(metadata))
    val strings = json("strings").arr.map(_.str).toVector
    assertEquals(strings, strings.sorted)
    assertEquals(strings.toSet, Set("AMDXML", "GFX11", "GFX6", "MesaACO", "VOP2", "VGPR", "gfx11", "gfx6", "vdst"))
    val schemas = json("schemas").arr.map(_.arr.map { operand =>
      val values = operand.arr
      (values(0).num.toInt, values(1).num.toInt, values(2).bool)
    }.toVector).toVector
    assertEquals(schemas, Vector(Vector((strings.indexOf("vdst"), strings.indexOf("VGPR"), false))))
    assertEquals(json("encodings").arr.size, 2)
    assertEquals(
      json("metadata").arr.map(entry => entry.arr.map(_.num.toInt).toVector).toVector,
      Vector(Vector(0, 1), Vector(1, 1))
    )
  }
}
