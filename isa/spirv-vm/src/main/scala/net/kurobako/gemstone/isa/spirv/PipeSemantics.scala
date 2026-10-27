package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.render.*

/** SPIR-V pipe semantics, as requests to a [[SPIRVMachine]]. */
private object PipeSemantics {

  private val transferOpcodes = Set(
    "OpWritePipe",
    "OpReadPipe",
    "OpReservedWritePipe",
    "OpReservedReadPipe",
    "OpReserveWritePipePackets",
    "OpReserveReadPipePackets",
    "OpGroupReserveWritePipePackets",
    "OpGroupReserveReadPipePackets",
    "OpIsValidReserveId",
    "OpGetNumPipePackets",
    "OpGetMaxPipePackets",
    "OpCreatePipeFromPipeStorage"
  )

  /** Whether `instruction` reads, writes, reserves in or queries a pipe. */
  def transfers(instruction: Instruction): Boolean = transferOpcodes(instruction.opcode.raw)

  inline def pipe[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(instruction: Instruction): F[Unit] =
    machine.pipe(instruction)

  /** The forms the explorer shows: packet transfers, reservations and queries. */
  def render(instruction: Instruction, machine: SPIRVMachineRender): Option[Term.Steps] =
    Option.when(transfers(instruction))(pipe(machine)(instruction))
}
