package net.kurobako.gemstone.isa.spirv

import VMInvocation.*
import VMMemory.*
import net.kurobako.gemstone.vm.*

/** The lowering of image sampling, queries and texel access to ops that request them of the machine. */
private object ImageLowering {

  def lower: PartialFunction[Instruction, Op] = {
    case instruction if ImageSemantics.handles(instruction) => ImageOp(instruction)
  }

  private class ImageOp(instruction: Instruction) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ImageSemantics.image(InvocationMachine(execution))(instruction)
  }
}
