package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.render.*

/** SPIR-V device-side enqueue semantics, as requests to a [[SPIRVMachine]]. */
private object DeviceEnqueueSemantics {

  private val opcodes = Set(
    "OpGetDefaultQueue",
    "OpBuildNDRange",
    "OpEnqueueKernel",
    "OpEnqueueMarker",
    "OpRetainEvent",
    "OpReleaseEvent",
    "OpCreateUserEvent",
    "OpIsValidEvent",
    "OpSetUserEventStatus",
    "OpCaptureEventProfilingInfo",
    "OpGetKernelWorkGroupSize",
    "OpGetKernelPreferredWorkGroupSizeMultiple",
    "OpGetKernelMaxNumSubgroups",
    "OpGetKernelLocalSizeForSubgroupCount",
    "OpGetKernelNDrangeSubGroupCount",
    "OpGetKernelNDrangeMaxSubGroupSize"
  )

  /** Whether `instruction` enqueues work, manages events or queries kernels on the device queue. */
  def transfers(instruction: Instruction): Boolean = opcodes(instruction.opcode.raw)

  inline def enqueue[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(instruction: Instruction): F[Unit] =
    machine.deviceEnqueue(instruction)

  /** The forms the explorer shows: enqueues, event operations and kernel queries. */
  def render(instruction: Instruction, machine: SPIRVMachineRender): Option[Term.Steps] =
    Option.when(transfers(instruction))(enqueue(machine)(instruction))
}
