package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.flow.*
import net.kurobako.gemstone.testing.*

class AMDGPUControlFlowGraphSuite extends Suite {
  private def flowOf(operations: Vec[Instruction]): ControlFlowGraph =
    AMDGPUControlFlowGraph.of(irKernel(operations))

  test("a conditional back edge forms a loop over its header") {
    val flow = flowOf(Vec(
      Instruction.Wait,
      Instruction.Wait,
      Instruction.Branch(ScalarBranchCondition.SccNonZero, 1),
      Instruction.End
    ))
    assertEquals(flow.blocks.map(block => (block.first, block.last)), Vec((0, 0), (1, 2), (3, 3)))
    assertEquals(flow.loops.map(loop => (loop.header, loop.body, loop.latches)), Vec((1, Vec(1), Vec(1))))
    assert(flow.reducible)
  }

  test("trap and end terminate their block without successors") {
    val flow = flowOf(Vec(
      Instruction.Branch(ScalarBranchCondition.SccZero, 2),
      Instruction.Trap(7),
      Instruction.End
    ))
    assertEquals(
      flow.blocks.map(block => (block.first, block.last, block.successors, block.open)),
      Vec(
        (0, 0, Vec(2, 1), false),
        (1, 1, Vec.empty[Int], false),
        (2, 2, Vec.empty[Int], false)
      )
    )
    assertEquals(flow.immediateDominator(1), Some(0))
    assertEquals(flow.immediateDominator(2), Some(0))
    assertEquals(flow.loops, Vec.empty[Loop])
  }

  test("an unresolved relative branch leaves its block open and the next instruction unreached") {
    val flow = flowOf(Vec(Instruction.BranchRelative(ScalarBranchCondition.Always, 1), Instruction.End))
    assert(flow.blocks(0).open)
    assertEquals(flow.blocks(0).successors, Vec.empty[Int])
    assert(!flow.reachable(1))
  }
}
