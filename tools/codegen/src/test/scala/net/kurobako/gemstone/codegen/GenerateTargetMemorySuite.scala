package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.testing.*

class GenerateTargetMemorySuite extends Suite {

  test("only targets with a reference card get a row, carrying its bus width and L2 bytes") {
    val rendered = GenerateTargetMemory.render(Architecture.all)
    assert(
      rendered.contains(
        """if (target == "sm_89") return TargetMemory{.busWidthBits = 128, .l2Bytes = 33554432, """ +
          """.persistingL2Bytes = 23068672, .persistingL2DefaultBytes = 6291456, .l2FetchBytes = 64, """ +
          """.architectureId = 400, .chipImplementation = 6, .chipRevision = 161};"""
      )
    )
    assert(!rendered.contains("gfx1036"))
    assert(rendered.contains("return std::nullopt;"))
  }

  test("the silicon identity is rendered only for the targets that carry one") {
    val rendered = GenerateTargetMemory.render(Architecture.all)
    assertEquals(rendered.split("\\.architectureId = ", -1).length - 1, 1)
    assert(rendered.contains("std::optional<int32_t> architectureId = std::nullopt;"))
    assert(rendered.contains("std::optional<int32_t> chipImplementation = std::nullopt;"))
    assert(rendered.contains("std::optional<int32_t> chipRevision = std::nullopt;"))
  }

  test("L2 controls are rendered only for the targets that carry them") {
    val rendered = GenerateTargetMemory.render(Architecture.all)
    assert(
      rendered.contains("""if (target == "sm_75") return TargetMemory{.busWidthBits = 256, .l2Bytes = 4194304};""")
    )
  }

  test("L2 fields default to unset, so rows without a control block still compile") {
    val rendered = GenerateTargetMemory.render(Architecture.all)
    assert(rendered.contains("std::optional<int32_t> persistingL2Bytes = std::nullopt;"))
    assert(rendered.contains("std::optional<int32_t> persistingL2DefaultBytes = std::nullopt;"))
    assert(rendered.contains("std::optional<int32_t> l2FetchBytes = std::nullopt;"))
  }

  test("every target with memory values is rendered exactly once") {
    val rendered = GenerateTargetMemory.render(Architecture.all)
    val carded   = Architecture.all.filter(_.memory.isDefined).map(_.name)
    assertEquals(Vec.from(carded.map(name => rendered.split(s"\"$name\"", -1).length - 1).distinct), Vec(1))
  }
}
