package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.isa.amdgpu.common.generated.AmdXmlRecordCount
import net.kurobako.gemstone.isa.amdgpu.common.generated.MetadataRecordCount
import net.kurobako.gemstone.isa.amdgpu.common.generated.MesaAcoRecordCount

class MetadataSuite extends munit.FunSuite {
  test("generate normalized ACO and official XML instruction metadata") {
    assert(MesaAcoRecordCount >= 3500)
    assert(AmdXmlRecordCount >= 40000)
    assert(MetadataRecordCount >= 43000)
    assertEquals(allEncodings.size, MetadataRecordCount)

    val definition = InstructionDef.forOpcode("v_add_f32").head
    val old        = encodingsFor(definition, Generation.Gfx6)
    assert(old.exists(info => info.source == MetadataSource.MesaAco && info.format == "VOP2"))

    val current = encodingsFor(definition, Generation.Gfx11)
    assert(current.exists(info => info.source == MetadataSource.AmdXml && info.operands.nonEmpty))
  }

  test("every descriptor owns canonical metadata without a detached database lookup") {
    Instructions.foreach { definition =>
      assert(definition.meta.encodings.nonEmpty, definition.toString)
      assertEquals(InstructionDef.forOpcode(definition.opcode).contains(definition), true)
      assert(definition.decoder.operandCounts.nonEmpty)
    }
  }
}
