package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

class VMRegistersSuite extends munit.FunSuite {
  test("lowered general, uniform, temporary, barrier and reduction banks occupy distinct dense slots") {
    val slots = Vec.range[0, 255].map(VMRegisters.general(_)) ++
      Vec.range[0, 255].map(VMRegisters.uniform(_)) ++
      Vec.tabulate(VMRegisters.TemporaryCount)(VMRegisters.temporary(_)) ++
      Vec.range[0, 16].map(VMRegisters.barrier(_)) ++
      Vec.tabulate(4)(VMRegisters.reduction(_))
    assertEquals(slots.map(_.raw).distinct.size, slots.size, "physical banks and temporaries must not alias")
  }

  test("physical predicates, temporary predicates and compiler flags do not alias") {
    val slots = Vec.range[0, 7].map(index => VMRegisters.predicate(index = index, uniform = false)) ++
      Vec.range[0, 7].map(index => VMRegisters.predicate(index = index, uniform = true)) ++
      Vec.tabulate(VMRegisters.TemporaryCount)(VMRegisters.temporaryPredicate(_)) ++
      Vec.from(VMRegisters.Predicate.values).map(_.slot)
    assertEquals(slots.map(_.raw).distinct.size, slots.size)
  }
}
