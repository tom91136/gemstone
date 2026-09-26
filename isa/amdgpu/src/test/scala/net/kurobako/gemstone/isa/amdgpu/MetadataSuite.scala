package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import Metadata.*

final class MetadataSuite extends munit.FunSuite {
  test("generate normalized ACO and official XML instruction metadata") {
    assert(MesaACORecordCount >= 3500)
    assert(AMDXMLRecordCount >= 40000)
    assert(MetadataRecordCount >= 43000)
    assertEquals(allEncodings.size, MetadataRecordCount)

    val definition = Instruction.Definition.forOpcode("v_add_f32").head
    val old        = encodingsFor(definition, Family.GFX6)
    assert(old.exists(info => info.source == MetadataSource.MesaACO && info.format.raw == "VOP2"))

    val current = encodingsFor(definition, Family.GFX11)
    assert(current.exists(info => info.source == MetadataSource.AMDXML && info.operands.nonEmpty))
  }

  test("every instruction definition exposes metadata, an opcode lookup, and decoder forms") {
    Instruction.definitions.foreach { definition =>
      assert(definition.meta.encodings.nonEmpty, definition.toString)
      assertEquals(Instruction.Definition.forOpcode(definition.opcode.raw).contains(definition), true)
      assert(definition.decoder.operandCounts.nonEmpty)
    }
  }
}
