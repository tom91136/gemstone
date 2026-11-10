package net.kurobako.gemstone.flow

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class ControlFlowGraphSuite extends Suite {
  private def to(targets: Int*): Option[Vec[Int]] = Some(Vec(targets*))

  private def graph(successors: Option[Vec[Int]]*): ControlFlowGraph = ControlFlowGraph(Vec(successors*), 0)

  private def loopFirsts(flow: ControlFlowGraph): Vec[(Int, Vec[Int], Vec[Int])] = flow.loops.map(loop =>
    (
      flow.blocks(loop.header).first,
      loop.body.map(flow.blocks(_).first),
      loop.latches.map(flow.blocks(_).first)
    )
  )

  test("straight-line code is one block without loops") {
    val flow = graph(to(1), to(2), to())
    assertEquals(flow.blocks, Vec(Block(0, 2, Vec.empty[Int], open = false)))
    assertEquals(flow.blockOf(2), 0)
    assertEquals(flow.loops, Vec.empty[Loop])
    assert(flow.reducible)
  }

  test("branches split blocks and a join is dominated by its branch") {
    val flow = graph(to(1, 2), to(3), to(3), to())
    assertEquals(flow.blocks.map(block => (block.first, block.last)), Vec((0, 0), (1, 1), (2, 2), (3, 3)))
    assertEquals(flow.immediateDominator(0), None)
    assertEquals(flow.immediateDominator(3), Some(0))
    assert(flow.dominates(dominator = 0, block = 3))
    assert(!flow.dominates(dominator = 1, block = 3))
    assert(flow.dominates(dominator = 3, block = 3))
  }

  test("nested reducible loops nest their bodies") {
    val flow = graph(to(1), to(2), to(3), to(2, 4), to(1, 5), to())
    assertEquals(loopFirsts(flow), Vec((1, Vec(1, 2, 4), Vec(4)), (2, Vec(2), Vec(2))))
    assert(flow.reducible)
    assert(flow.dominates(dominator = 1, block = 4))
    assert(!flow.dominates(dominator = 3, block = 2))
  }

  test("a self-loop is a natural loop of its own block") {
    val flow = graph(to(1), to(1, 2), to())
    assertEquals(loopFirsts(flow), Vec((1, Vec(1), Vec(1))))
    assert(flow.reducible)
  }

  test("back edges to one header merge into one loop") {
    val flow = graph(to(1), to(2, 3, 5), to(1), to(1, 4), to(), to())
    assertEquals(loopFirsts(flow), Vec((1, Vec(1, 2, 3), Vec(2, 3))))
  }

  test("a two-entry cycle is irreducible and has no natural loop") {
    val flow = graph(to(1, 2), to(2, 3), to(1, 3), to())
    assertEquals(flow.loops, Vec.empty[Loop])
    assert(!flow.reducible)
    assertEquals(flow.immediateDominator(1), Some(0))
    assertEquals(flow.immediateDominator(2), Some(0))
    assert(!flow.dominates(dominator = 1, block = 2))
    assert(!flow.dominates(dominator = 2, block = 1))
  }

  test("unreachable blocks have no dominators and join no loop") {
    val flow = graph(to(2), to(2), to())
    assert(flow.reachable(0))
    assert(!flow.reachable(1))
    assertEquals(flow.immediateDominator(1), None)
    assert(!flow.dominates(dominator = 1, block = 2))
    assert(flow.reducible)
    assertEquals(flow.loops, Vec.empty[Loop])
  }

  test("an unknown successor leaves its block open and out of the known edges") {
    val flow = graph(to(1, 2), None, to(0))
    assert(flow.blocks(1).open)
    assert(!flow.blocks(2).open)
    assert(flow.reachable(1))
    assertEquals(loopFirsts(flow), Vec((0, Vec(0, 2), Vec(2))))
  }
}
