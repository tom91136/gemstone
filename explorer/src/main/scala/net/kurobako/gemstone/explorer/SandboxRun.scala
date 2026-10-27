package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** One sandbox launch: what the VM issued, the register values before its last issue, the memory it ran against, and
  * the allocation ids its arguments named in parameter order. `spirv` keeps the facade's typed invocation evidence,
  * including the label and predecessor of every issue, which the generic trace does not carry.
  */
case class SandboxRun(
    program: SandboxProgram,
    kernel: NonEmpty[String],
    lines: Vec[ProgramLine],
    trace: Trace,
    registers: Vec[RegisterValue],
    memory: ArrayMemory,
    allocations: Vec[AllocationId],
    failure: Option[Fault],
    complete: Boolean,
    spirv: Option[SPIRVSandbox.Result] = None
) {

  /** The instruction addresses the launch issued. A lowered instruction can share one ISA address, so compare
    * [[pcs]].distinct for control flow.
    */
  def pcs: Vec[Long] = trace.steps.flatMap(_.location.instruction.map(_.raw))

  /** Bytes of the allocation parameter with `index`, or none when the kernel has fewer allocations. */
  def bytes(index: Int): Vec[Byte] = allocations.lift(index).fold(Vec.empty[Byte])(memory.bytes)

  /** SPIR-V issues with their function, label, predecessor and call depth; empty for the other ISAs. */
  def spirvSteps: Vec[SPIRVSandbox.Step] = spirv.fold(Vec.empty[SPIRVSandbox.Step])(_.steps)
}
