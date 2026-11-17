package net.kurobako.gemstone.flow

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class LivenessSuite extends Suite {
  private def use(reads: Vec[String], writes: Vec[String], units: Map[String, Int] = Map.empty): RegisterUse =
    RegisterUse(reads, writes, units)

  // 0: v0 = ..           block 0
  // 1: s0 = ..
  // 2: v1 = v1 + v0      block 1, a loop on itself
  // 3: s0 = s0 + 1
  // 4: branch 2 if s0
  // 5: store v1          block 2
  private val graph = ControlFlowGraph(
    Vec(Some(Vec(1)), Some(Vec(2)), Some(Vec(3)), Some(Vec(4)), Some(Vec(2, 5)), Some(Vec.empty[Int])),
    0
  )
  private val uses = Vec(
    use(Vec.empty, Vec("v0")),
    use(Vec.empty, Vec("s0")),
    use(Vec("v1", "v0"), Vec("v1")),
    use(Vec("s0"), Vec("s0")),
    use(Vec.empty, Vec.empty),
    use(Vec("v1"), Vec.empty)
  )

  test("a loop keeps its carried registers live around the back edge") {
    val live = Liveness.of(graph, uses)
    assertEquals(live.liveIn(0), Set("v1"))
    assertEquals(live.liveOut(0), Set("v0", "v1"))
    assertEquals(live.liveIn(2), Set("v0", "v1", "s0"))
    assertEquals(live.liveOut(2), Set("v0", "v1", "s0"))
    assertEquals(live.liveIn(3), Set("v0", "v1", "s0"))
    assertEquals(live.liveOut(4), Set("v0", "v1", "s0"))
    assertEquals(live.liveIn(5), Set("v1"))
    assertEquals(live.liveOut(5), Set.empty[String])
  }

  test("a write ends the live range of the register it overwrites") {
    val live = Liveness.of(graph, uses)
    assertEquals(live.liveIn(1), Set("v0", "v1"))
    assertEquals(live.liveOut(1), Set("v0", "v1", "s0"))
  }

  test("register ranges and widths cover each 32-bit unit they span") {
    val flow  = ControlFlowGraph(Vec(Some(Vec(1)), Some(Vec(2)), Some(Vec.empty[Int])), 0)
    val pairs = Vec(
      use(Vec.empty, Vec("s[2:3]")),
      use(Vec("s2"), Vec("R4"), Map("R4" -> 2)),
      use(Vec("R5", "s3", "P0"), Vec.empty)
    )
    val live = Liveness.of(flow, pairs)
    assertEquals(live.liveIn(1), Set("s2", "s3", "P0"))
    assertEquals(live.liveOut(0), Set("s2", "s3", "P0"))
    assertEquals(live.liveOut(1), Set("s3", "P0", "R5"))
    assertEquals(live.liveIn(2), Set("s3", "P0", "R5"))
    assertEquals(live.liveIn(0), Set("P0"))
  }

  test("an open block keeps every register the kernel names live out") {
    val flow = ControlFlowGraph(Vec(Some(Vec(1)), None, Some(Vec(3)), None), 0)
    val some =
      Vec(use(Vec.empty, Vec("v0")), use(Vec.empty, Vec.empty), use(Vec("v2"), Vec.empty), use(Vec.empty, Vec.empty))
    val live = Liveness.of(flow, some)
    assertEquals(live.liveOut(1), Set("v0", "v2"))
    assertEquals(live.liveOut(0), Set("v0", "v2"))
  }
}
