package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

class VMRegistersSuite extends munit.FunSuite {
  for (layout <- Vec(VMRegisters.Layout.Full, VMRegisters.Layout(24, 0), VMRegisters.Layout(32, 8)))
    test(
      s"lowered banks occupy distinct dense slots with ${layout.generalCount} general and ${layout.uniformCount} uniform"
    ) {
      val slots = Vec.range[0, 255].filter(_.raw < layout.generalCount.raw).map(layout.general(_)) ++
        Vec.range[0, 255].filter(_.raw < layout.uniformCount.raw).map(layout.uniform(_)) ++
        Vec.tabulate(VMRegisters.TemporaryCount)(layout.temporary(_)) ++
        Vec.range[0, 16].map(layout.barrier(_)) ++
        Vec.tabulate(4)(layout.reduction(_))
      assertEquals(slots.map(_.raw).distinct.size, slots.size, "physical banks and temporaries must not alias")
      assertEquals(slots.map(_.raw).max + 1, layout.registerCount(4).raw)
    }

  test("physical predicates, temporary predicates and compiler flags do not alias") {
    val slots = Vec.range[0, 7].map(index => VMRegisters.predicate(index = index, uniform = false)) ++
      Vec.range[0, 7].map(index => VMRegisters.predicate(index = index, uniform = true)) ++
      Vec.tabulate(VMRegisters.TemporaryCount)(VMRegisters.temporaryPredicate(_)) ++
      Vec.from(VMRegisters.Predicate.values).map(_.slot)
    assertEquals(slots.map(_.raw).distinct.size, slots.size)
  }
}
