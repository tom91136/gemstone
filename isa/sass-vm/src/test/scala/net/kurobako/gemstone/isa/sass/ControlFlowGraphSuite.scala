package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.*

class ControlFlowGraphSuite extends Suite {
  test("modern spin loop is a self-looping block between its entry and exit") {
    val function = load(89, Fixture.listing("spinLockSm89"), "spin").function
    val flow     = SASSControlFlowGraph.of(89, function)
    assertEquals(flow.blocks.map(block => (block.first, block.last)), Vec((0, 5), (6, 10), (11, 22)))
    assertEquals(flow.blocks(1).successors, Vec(1, 2))
    assert(!flow.blocks(1).open)
    assertEquals(flow.blocks(2).successors, Vec.empty[Int])
    assertEquals(flow.blockOf(6), 1)
    assertEquals(flow.loops.map(loop => (loop.header, loop.body, loop.latches)), Vec((1, Vec(1), Vec(1))))
    assert(flow.reducible)
    assert(flow.dominates(dominator = 0, block = 2))
    assertEquals(flow.immediateDominator(2), Some(1))
  }
}
