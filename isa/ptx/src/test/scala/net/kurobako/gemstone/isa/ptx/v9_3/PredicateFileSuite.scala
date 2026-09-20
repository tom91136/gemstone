package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class PredicateFileSuite extends munit.FunSuite {
  test("numbered predicate boundaries retain independent defined and value bits") {
    val predicates = VMPredicateFile()
    val indices    = Seq(0, 1, 62, 63, 64, 65, 127, 128, 959, 960, 1022, 1023)
    indices.foreach(index => assertEquals(predicates.getSlot(index), None))
    indices.foreach { index =>
      predicates.updateSlot(index, false)
      assertEquals(predicates.getSlot(index), Some(false))
      predicates.updateSlot(index, true)
      assertEquals(predicates.get(RegisterName.applyUnsafe(s"%p$index")), Some(true))
    }
    assertEquals(predicates.getSlot(66), None)
    indices.foreach { index =>
      predicates.update(RegisterName.applyUnsafe(s"%p$index"), false)
      assertEquals(predicates.getSlot(index), Some(false))
    }
    assertEquals(predicates.getSlot(66), None)
  }

  test("named predicates and dense predicates do not alias") {
    val predicates = VMPredicateFile()
    Seq("%ready", "%p", "%p1024", "%p-1").foreach { name =>
      assertEquals(predicates.get(RegisterName.applyUnsafe(name)), None)
      predicates.update(RegisterName.applyUnsafe(name), true)
    }
    assertEquals(predicates.getSlot(0), None)
    predicates.updateSlot(0, false)
    Seq("%ready", "%p", "%p1024", "%p-1").foreach(name =>
      assertEquals(predicates.get(RegisterName.applyUnsafe(name)), Some(true))
    )
    assertEquals(predicates.getSlot(0), Some(false))
  }

  test("undefined predicate used by a vote still reports an execution fault") {
    val source   = """.version 8.0
      .target sm_80
      .address_size 64
      .visible .entry undefined_predicate() {
        .reg .pred %p<64>;
        .reg .b32 %r;
        vote.sync.ballot.b32 %r, %p63, 1;
        ret;
      }"""
    val kernel   = ISA.compile(source).fold(error => fail(error.message), value => value)
    val geometry = net.kurobako.gemstone.execution.LaunchGeometry.forLaunch(
      net.kurobako.gemstone.execution.Dimensions.One,
      net.kurobako.gemstone.execution.Dimensions.One
    ).toOption.get
    val result = VirtualMachine.execute(
      kernel,
      geometry,
      Map.empty,
      net.kurobako.gemstone.memory.ArrayCheckedMemory.allocate(0),
      captureTrace = false
    )
    assert(result.left.toOption.exists(_.message.contains("undefined register %p63")), result.toString)
  }

}
