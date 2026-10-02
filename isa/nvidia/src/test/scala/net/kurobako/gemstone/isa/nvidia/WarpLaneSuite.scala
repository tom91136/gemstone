package net.kurobako.gemstone.isa.nvidia

class WarpLaneSuite extends munit.FunSuite {
  test("warp lanes retain the complete zero-based NVIDIA lane range") {
    assertEquals(WarpLane.values.size, 32)
    assertEquals(WarpLane.values.head.raw, 0)
    assertEquals(WarpLane.values.last.raw, 31)
  }

  test("fragment layouts accept a statically bounded warp lane") {
    val fragment = VMTensor.Fragment(
      VMTensor.Shape(8, 8, 4),
      VMTensor.Role.A,
      VMTensor.Element.F16,
      wmma = false
    )

    assertEquals(fragment.partition(WarpLane.values(31)), 3)
    assertEquals(fragment.coordinate(WarpLane.values(31), 0), (7, 0))
  }
}
