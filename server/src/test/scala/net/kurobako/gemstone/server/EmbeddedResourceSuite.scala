package net.kurobako.gemstone.server

import net.kurobako.gemstone.isa.amdgpu.*

class EmbeddedResourceSuite extends munit.FunSuite {
  test("native server includes AMDGPU instruction metadata") {
    val stream = Option(classOf[Metadata].getResourceAsStream("metadata.json"))
      .getOrElse(fail("missing AMDGPU metadata resource metadata.json"))
    stream.close()
  }
}
