package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.*

class VMRegistersSuite extends munit.FunSuite {
  test("lowered general, uniform and temporary banks occupy distinct dense slots") {
    val physicalCount = RegisterBank.capacity(RegisterBank.R).get - 1
    val names         = Vec.tabulate(physicalCount)(VMRegisters.general(_)) ++
      Vec.tabulate(physicalCount)(VMRegisters.uniform(_)) ++
      Vec.tabulate(VMRegisters.TemporaryCount)(index => VMRegisters.temporary(index = index, predicate = false))
    val slots = names.map(VMRegisterFile.index(_))
    assert(slots.forall(_ >= 0), "SASS registers must use dense storage")
    assertEquals(slots.distinct.size, slots.size, "physical banks and temporaries must not alias")
  }

  test("physical predicates, temporary predicates and compiler flags do not alias") {
    val names = Vec.tabulate(VMRegisters.PhysicalPredicateCount)(index =>
      VMRegisters.predicate(index = index, uniform = false)
    ) ++
      Vec.tabulate(VMRegisters.PhysicalPredicateCount)(index => VMRegisters.predicate(index = index, uniform = true)) ++
      Vec.tabulate(VMRegisters.TemporaryCount)(index => VMRegisters.temporary(index = index, predicate = true)) ++
      Vec.from(VMRegisters.Predicate.values).map(_.name)
    val slots = names.map(VMPredicateFile.index(_))
    assert(slots.forall(_ >= 0), "SASS predicates must use dense storage")
    assertEquals(slots.distinct.size, slots.size)
  }
}
