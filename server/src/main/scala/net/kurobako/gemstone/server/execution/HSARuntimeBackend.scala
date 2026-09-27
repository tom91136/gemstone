package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.rpc.ExecutionProtocol.ExecutionFailure
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.isa.ProgramSymbols
import net.kurobako.gemstone.isa.amdgpu.CodeObject
import net.kurobako.gemstone.isa.amdgpu.VirtualMachine as AMDGPUMachine
import net.kurobako.gemstone.vm.Memory
import net.kurobako.gemstone.vm.MemoryRegion

object HSARuntimeBackend extends RuntimeBackend {
  override val encoding: String = "hsa-code-object"

  override def load(
      services: RuntimeBackend.Services,
      image: Vec[Byte]
  ): Either[ExecutionFailure, RuntimeBackend.Program] = {
    val machine = AMDGPUMachine(services.observer, services.scheduler)
    for {
      program <- CodeObject.Decoder.decode(image).left.map(error => ExecutionFailure.InvalidRequest(error.message))
      loaded  <- machine.load(program).left.map(error => ExecutionFailure.InvalidRequest(error.message))
    } yield Program(machine)(loaded)
  }

  private final class Program(machine: AMDGPUMachine)(loaded: machine.LoadedProgram)
      extends MachineProgram[AMDGPUMachine](machine, loaded, "HSA program is closed") {
    override def symbols: Either[ExecutionFailure, ProgramSymbols] =
      open(machine.symbols(loaded).left.map(error => ExecutionFailure.InvalidRequest(error.message)))

    override def kernel(name: NonEmptyString): Either[ExecutionFailure, KernelMetadata] =
      open(machine.kernel(loaded, name).left.map(error => ExecutionFailure.InvalidRequest(error.message)))

    override val globals: Option[RuntimeBackend.Program.Globals] = Some(Globals(machine)(loaded))
  }

  private final class Globals(machine: AMDGPUMachine)(loaded: machine.LoadedProgram)
      extends RuntimeBackend.Program.Globals {
    override def image(name: NonEmptyString): Either[ExecutionFailure, RuntimeBackend.Program.Globals.Image] =
      machine.global(loaded, name).left.map(error => ExecutionFailure.InvalidRequest(error.message))
        .map((size, initial) => RuntimeBackend.Program.Globals.Image(size, initial))

    override def bind(
        name: NonEmptyString,
        region: MemoryRegion,
        offset: NonNegative[Long],
        regionSize: Positive[Long],
        memory: Memory
    ): Either[ExecutionFailure, Unit] =
      machine.bindGlobal(loaded, name, region, offset, regionSize, memory).left.map(ExecutionFailure.fromFault(_))

    override def write(
        name: NonEmptyString,
        offset: NonNegative[Long],
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] =
      machine.writeGlobal(loaded, name, offset, bytes).left.map(ExecutionFailure.fromFault(_))
  }
}
