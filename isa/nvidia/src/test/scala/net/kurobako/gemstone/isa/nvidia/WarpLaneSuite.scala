package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*

class WarpLaneSuite extends munit.FunSuite {
  test("fragment layouts accept a statically bounded warp lane") {
    val fragment = VMIR.Matrix.Fragment(
      VMIR.Matrix.Shape(8, 8, 4),
      VMIR.Matrix.Role.A,
      VMIR.Matrix.Element.F16,
      wmma = false
    )

    assertEquals(fragment.partition(WarpLane.values(31)).raw, 3)
    assertEquals(fragment.coordinate(WarpLane.values(31), 0), (7: Int >= 0, 0: Int >= 0))
  }
}
