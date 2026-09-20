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

  test("metadata catalog round-trips records and counts") {
    val operands = Vector(
      AMDGPUMetadataCatalogFormat.Operand(
        field = Some("VDST"),
        kind = "vector",
        sizeBits = 32,
        input = false,
        output = true,
        implicitOperand = false
      ),
      AMDGPUMetadataCatalogFormat.Operand(
        field = None,
        kind = "scalar",
        sizeBits = 64,
        input = true,
        output = false,
        implicitOperand = true
      )
    )
    val first = AMDGPUMetadataCatalogFormat.Encoding(
      family = "GFX9",
      target = "gfx9",
      format = "VOP2",
      opcode = 17,
      operands = operands,
      functionalGroup = Some("Vector ALU"),
      source = "AMDXML"
    )
    val second  = first.copy(family = "GFX10", target = "rdna1", source = "MesaACO")
    val catalog = AMDGPUMetadataCatalogFormat.Catalog(
      Vector(Vector(first, second), Vector(second.copy(opcode = 18, operands = operands)))
    )

    val encoded = AMDGPUMetadataCatalogFormat.encode(catalog)

    assertEquals(encoded.metadataCount, 2)
    assertEquals(encoded.encodingCount, 3)
    assertEquals(AMDGPUMetadataCatalogFormat.decode(encoded.payload), catalog)
  }
}
