package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.LaunchGeometry.*

class FaultSuite extends Suite {
  private val location = Fault.Location("saxpy", None, Some(0x1cL), Some(Coordinates(1, 0, 0)), Some(3))

  test("a located fault keeps its kind, detail and equality, and reports where it arose") {
    val fault = Fault.Memory("out of bounds").at(location)
    assertEquals(fault, Fault.Memory("out of bounds"))
    assertEquals(fault.location, Some(location))
    assertEquals(Fault.Memory("out of bounds").location, None)
    fault match {
      case Fault.Memory(detail) => assertEquals(detail: String, "out of bounds")
      case other                => fail(s"unexpected $other")
    }
  }

  test("every fault kind carries a location except cancellation") {
    val faults = Vec(
      Fault.InvalidRequest("a"),
      Fault.NotFound("b"),
      Fault.Memory("c"),
      Fault.Unsupported("d"),
      Fault.EventHandling("e"),
      Fault.Backend("f"),
      Fault.InvalidProgram("g"),
      Fault.InvalidConfiguration("h"),
      Fault.OutOfResources("i"),
      Fault.Misaligned("j"),
      Fault.IllegalInstruction("k"),
      Fault.Trap("l"),
      Fault.Assertion("m"),
      Fault.Hang("n"),
      Fault.BudgetExceeded(10L)
    )
    faults.foreach(fault => assertEquals(fault.at(location).location, Some(location), fault))
    assertEquals(Fault.Cancelled.at(location).location, None)
  }

  test("the first location attached is the one kept") {
    val inner = Fault.Location("inner", Some("helper"), Some(4L), None, None)
    assertEquals(Fault.Unsupported("x").at(inner).at(location).location, Some(inner))
  }

  test("descriptions name the kernel, function, instruction, workgroup and lane that are known") {
    assertEquals(
      Fault.Memory("out of bounds").at(location).description,
      "out of bounds (kernel 'saxpy', instruction 0x1c, workgroup (1, 0, 0), lane 3)"
    )
    assertEquals(
      Fault.Memory("out of bounds").at(Fault.Location("saxpy", Some("helper"), None, None, None)).description,
      "out of bounds (kernel 'saxpy', function 'helper')"
    )
    assertEquals(Fault.Memory("out of bounds").description: String, "out of bounds")
  }
}
