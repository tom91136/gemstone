package net.kurobako.gemstone.flow

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class WindowsSuite extends Suite {
  // 0 a = x      block 0
  // 1 b = a
  // 2 c = b
  // 3 branch 5   (rejected)
  // 4 d = c      block 1
  // 5 e = c      block 2, a branch target
  // 6 use e
  private val graph = ControlFlowGraph(
    Vec(Some(Vec(1)), Some(Vec(2)), Some(Vec(3)), Some(Vec(4, 5)), Some(Vec(5)), Some(Vec(6)), Some(Vec.empty[Int])),
    0
  )
  private val uses = Vec(
    RegisterUse(Vec("x"), Vec("a")),
    RegisterUse(Vec("a"), Vec("b")),
    RegisterUse(Vec("b"), Vec("c")),
    RegisterUse(Vec.empty, Vec.empty),
    RegisterUse(Vec("c"), Vec("d")),
    RegisterUse(Vec("c"), Vec("e")),
    RegisterUse(Vec("e"), Vec.empty)
  )
  private val liveness = Liveness.of(graph, uses)

  private def windows(size: Int, rejected: Set[Int]): Vec[(Int, Int, Int, Set[String], Set[String])] =
    Windows.of(graph, liveness, rejected, size).map(w => (w.block, w.first, w.last, w.liveIn, w.liveOut))

  test("a window stops before a rejected instruction and carries its live sets") {
    assertEquals(
      windows(8, Set(3)),
      Vec(
        (0, 0, 2, Set("x"), Set("c")),
        (1, 4, 4, Set("c"), Set("c")),
        (2, 5, 6, Set("c"), Set.empty[String])
      )
    )
  }

  test("a window never crosses a block boundary") {
    val found = windows(8, Set.empty)
    assertEquals(found.map(w => (w._2, w._3)), Vec((0, 3), (4, 4), (5, 6)))
  }

  test("a block longer than the size yields every window of that size") {
    assertEquals(windows(2, Set(3)).map(w => (w._2, w._3)), Vec((0, 1), (1, 2), (4, 4), (5, 6)))
  }

  test("a block of rejected instructions yields no window") {
    assertEquals(windows(8, Set(0, 1, 2, 3, 4, 5, 6)).size, 0)
  }
}
