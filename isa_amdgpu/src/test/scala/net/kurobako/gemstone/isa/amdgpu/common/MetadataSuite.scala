package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.isa.amdgpu.common.generated.AMDXMLRecordCount
import net.kurobako.gemstone.isa.amdgpu.common.generated.MetadataRecordCount
import net.kurobako.gemstone.isa.amdgpu.common.generated.MesaACORecordCount

class MetadataSuite extends munit.FunSuite {
  test("generate normalized ACO and official XML instruction metadata") {
    assert(MesaACORecordCount >= 3500)
    assert(AMDXMLRecordCount >= 40000)
    assert(MetadataRecordCount >= 43000)
    assertEquals(allEncodings.size, MetadataRecordCount)

    val definition = InstructionDef.forOpcode("v_add_f32").head
    val old        = encodingsFor(definition, Generation.GFX6)
    assert(old.exists(info => info.source == MetadataSource.MesaACO && info.format == "VOP2"))

    val current = encodingsFor(definition, Generation.GFX11)
    assert(current.exists(info => info.source == MetadataSource.AMDXML && info.operands.nonEmpty))
  }

  test("every descriptor owns canonical metadata without a detached database lookup") {
    Instructions.foreach { definition =>
      assert(definition.meta.encodings.nonEmpty, definition.toString)
      assertEquals(InstructionDef.forOpcode(definition.opcode).contains(definition), true)
      assert(definition.decoder.operandCounts.nonEmpty)
    }
  }
}
