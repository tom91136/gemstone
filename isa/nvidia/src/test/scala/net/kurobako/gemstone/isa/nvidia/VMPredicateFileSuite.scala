package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class VMPredicateFileSuite extends munit.FunSuite {
  test("numbered predicate boundaries retain independent defined and value bits") {
    val predicates = VMPredicateFile()
    val indices    = Vec[VMPredicateFile.Slot](0, 1, 62, 63, 64, 65, 127, 128, 959, 960, 1022, 1023)
    indices.foreach(index => assertEquals(predicates.getSlot(index), None))
    indices.foreach { index =>
      predicates.updateSlot(index, false)
      assertEquals(predicates.getSlot(index), Some(false))
      predicates.updateSlot(index, true)
      assertEquals(predicates.get(s"%p$index"), Some(true))
    }
    assertEquals(predicates.getSlot(66), None)
    indices.foreach { index =>
      predicates.update(s"%p$index", false)
      assertEquals(predicates.getSlot(index), Some(false))
    }
    assertEquals(predicates.getSlot(66), None)
  }

  test("named predicates and dense predicates do not alias") {
    val predicates = VMPredicateFile()
    Vec[NonEmpty[String]]("%ready", "%p", "%p1024", "%p-1").foreach { name =>
      assertEquals(predicates.get(name), None)
      predicates.update(name, true)
    }
    assertEquals(predicates.getSlot(0), None)
    predicates.updateSlot(0, false)
    Vec[NonEmpty[String]]("%ready", "%p", "%p1024", "%p-1").foreach(name =>
      assertEquals(predicates.get(name), Some(true))
    )
    assertEquals(predicates.getSlot(0), Some(false))
  }
}
