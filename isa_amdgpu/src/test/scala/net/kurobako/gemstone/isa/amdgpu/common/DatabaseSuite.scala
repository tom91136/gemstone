package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.isa.amdgpu.common.generated.Database

class DatabaseSuite extends munit.FunSuite {
  test("generate normalized ACO and official XML instruction metadata") {
    assert(Database.mesaAcoRecordCount >= 3500)
    assert(Database.amdXmlRecordCount >= 40000)
    assert(Database.instructions.size >= 43000)

    val old = Database.lookup("v_add_f32", Generation.Gfx6)
    assert(old.exists(info => info.source == MetadataSource.MesaAco && info.format == "VOP2"))

    val current = Database.lookup("v_add_f32", Generation.Gfx11)
    assert(current.exists(info => info.source == MetadataSource.AmdXml && info.operands.nonEmpty))
  }
}
