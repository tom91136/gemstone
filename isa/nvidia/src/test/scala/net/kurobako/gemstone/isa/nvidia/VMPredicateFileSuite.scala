package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

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

  test("clearing and reusing predicate storage preserves undefined slots across both banks") {
    val predicates = VMPredicateFile()
    predicates.clear()
    Vec[VMPredicateFile.Slot](0, 63, 64, 127, 1023).foreach { index =>
      predicates.updateSlot(index, index.raw % 2 == 0)
    }
    predicates("%named") = true
    predicates.clear()
    Vec.range[0, 1024].foreach(index => assertEquals(predicates.getSlot(index), None))
    assertEquals(predicates.get("%named"), None)
    predicates.updateSlot(63, false)
    predicates.updateSlot(64, true)
    assertEquals(predicates.getSlot(63), Some(false))
    assertEquals(predicates.getSlot(64), Some(true))
    assertEquals(predicates.getSlot(0), None)
    assertEquals(predicates.getSlot(1023), None)
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

  test("predicate names stay case-sensitive across dense and named storage") {
    val predicates = VMPredicateFile()
    predicates("%p0") = false
    predicates("%P0") = true
    assertEquals(predicates.get("%p0"), Some(false))
    assertEquals(predicates.get("%P0"), Some(true))
    predicates.clear()
    assertEquals(predicates.get("%p0"), None)
    assertEquals(predicates.get("%P0"), None)
  }

}
