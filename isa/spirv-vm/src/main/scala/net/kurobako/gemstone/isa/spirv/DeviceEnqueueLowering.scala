package net.kurobako.gemstone.isa.spirv

import VMInvocation.*
import VMMemory.*
import net.kurobako.gemstone.vm.*

private object DeviceEnqueueLowering {

  def lower: PartialFunction[Instruction, Op] = {
    case instruction if DeviceEnqueueSemantics.transfers(instruction) => DeviceEnqueueOp(instruction)
  }

  private class DeviceEnqueueOp(instruction: Instruction) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      DeviceEnqueueSemantics.enqueue(InvocationMachine(execution))(instruction)
  }
}
