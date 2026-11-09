package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.testing.*

class ArchitectureSuite extends Suite {

  test("every decoder target has a fully sourced architecture") {
    Target.all.foreach { target =>
      val name         = s"sm_${target.sm}"
      val architecture = Architecture.named(name).getOrElse(fail(s"no architecture for $name"))
      assertEquals(architecture.name, name)
      assertEquals(ArchitectureChecks.problems(architecture), Vec.empty[String])
    }
  }
}
