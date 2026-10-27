package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.render.*

/** SPIR-V tensor layout, view and tensor transfer semantics, as requests to a [[SPIRVMachine]]. */
private object TensorAddressingSemantics {

  private val opcodes = Set(
    "OpCreateTensorLayoutNV",
    "OpTensorLayoutSetDimensionNV",
    "OpTensorLayoutSetStrideNV",
    "OpTensorLayoutSetBlockSizeNV",
    "OpTensorLayoutSliceNV",
    "OpTensorLayoutSetClampValueNV",
    "OpCreateTensorViewNV",
    "OpTensorViewSetDimensionNV",
    "OpTensorViewSetStrideNV",
    "OpTensorViewSetClipNV",
    "OpCooperativeMatrixLoadTensorNV",
    "OpCooperativeMatrixStoreTensorNV"
  )

  /** Whether `instruction` builds or updates a tensor layout or view, or transfers a matrix through one. */
  def transfers(instruction: Instruction): Boolean = opcodes(instruction.opcode.raw)

  inline def tensorAddressing[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(instruction: Instruction): F[Unit] =
    machine.tensorAddressing(instruction)

  /** The forms the explorer shows: layouts and views are handles to the VM's state. */
  def render(instruction: Instruction, machine: SPIRVMachineRender): Option[Term.Steps] =
    Option.when(transfers(instruction))(tensorAddressing(machine)(instruction))
}
