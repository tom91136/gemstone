package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMMemory.*
import net.kurobako.gemstone.vm.*

/** The lowering of pipe operations to ops that request them of the machine. */
private object PipeLowering {

  def lower: PartialFunction[Instruction, Op] = {
    case instruction if PipeSemantics.transfers(instruction) => PipeOp(instruction)
    case Instruction.CommitReadPipe(_, _, _, _) | Instruction.CommitWritePipe(_, _, _, _) |
        Instruction.GroupCommitReadPipe(_, _, _, _, _) | Instruction.GroupCommitWritePipe(_, _, _, _, _) => Next
  }

  private class PipeOp(instruction: Instruction) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      PipeSemantics.pipe(InvocationMachine(execution))(instruction)
  }
}
