package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.testing.*

class ArchitectureSuite extends Suite {

  test("every decoder target has a fully sourced architecture") {
    Target.all.foreach { target =>
      val name         = target.name.raw
      val architecture = Architecture.named(name).getOrElse(fail(s"no architecture for $name"))
      assertEquals(ArchitectureChecks.problems(architecture), Vec.empty[String])
    }
  }

  test("unlisted processors are not decoder targets") {
    val names = Target.all.map(_.name.raw)
    assert(!Vec("gfx800", "gfx1001").exists(names.contains))
  }

  test("sibling processors are decoder targets") {
    val names = Target.all.map(_.name.raw).toSet
    assert(Vec("gfx805", "gfx902", "gfx909", "gfx1154", "gfx1171", "gfx1172").forall(names.contains))
  }
}
