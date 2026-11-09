package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.testing.*

class GenerateTargetMemorySuite extends Suite {

  test("only targets with a reference card get a row, carrying its bus width and L2 bytes") {
    val rendered = GenerateTargetMemory.render(Architecture.all)
    assert(
      rendered.contains("""if (target == "sm_89") return TargetMemory{.busWidthBits = 128, .l2Bytes = 33554432};""")
    )
    assert(!rendered.contains("gfx1036"))
    assert(rendered.contains("return std::nullopt;"))
  }

  test("every target with memory values is rendered exactly once") {
    val rendered = GenerateTargetMemory.render(Architecture.all)
    val carded   = Architecture.all.filter(_.memory.isDefined).map(_.name)
    assertEquals(Vec.from(carded.map(name => rendered.split(s"\"$name\"", -1).length - 1).distinct), Vec(1))
  }
}
