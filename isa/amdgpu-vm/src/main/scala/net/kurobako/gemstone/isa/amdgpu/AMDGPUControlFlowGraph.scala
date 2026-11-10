package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.flow.*

private object AMDGPUControlFlowGraph {
  def of(kernel: VMIR.Kernel): ControlFlowGraph = {
    val instructions                      = kernel.instructions
    val successors: Vec[Option[Vec[Int]]] = instructions.indices.toVec.map(successorsOf(instructions, _))
    ControlFlowGraph(successors, kernel.entryInstruction)
  }

  private def successorsOf(instructions: Vec[Instruction], index: Int): Option[Vec[Int]] = {
    val next = index + 1
    instructions(index) match {
      case Instruction.Branch(ScalarBranchCondition.Always, target)    => Some(Vec(target))
      case Instruction.Branch(_, target)                               => Some(Vec(target, next))
      case Instruction.Call(_, target)                                 => Some(Vec(target, next))
      case Instruction.Fork(_, target)                                 => Some(Vec(target, next))
      case Instruction.SubvectorLoop(_, _, target)                     => Some(Vec(target, next))
      case Instruction.Halt(_) | Instruction.End | Instruction.Trap(_) => Some(Vec.empty[Int])
      case Instruction.BranchRelative(_, _) | Instruction.CallRelative(_, _) | Instruction.ForkRelative(_, _) |
          Instruction.ForkIndirect(_, _) | Instruction.SubvectorLoopRelative(_, _, _) | Instruction.Join(_) |
          Instruction.SSetPc(_) | Instruction.SSwapPc(_, _) => None
      case _ => Some(Vec(next))
    }
  }
}
