package net.kurobako.gemstone.isa.spirv

import VMInvocation.*
import VMMemory.*
import net.kurobako.gemstone.vm.*

private object TensorAddressingLowering {

  def lower: PartialFunction[Instruction, Op] = {
    case instruction if TensorAddressingSemantics.transfers(instruction) => TensorAddressingOp(instruction)
  }

  private class TensorAddressingOp(instruction: Instruction) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      TensorAddressingSemantics.tensorAddressing(InvocationMachine(execution))(instruction)
  }
}
