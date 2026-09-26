package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.{NonNegativeLong, PositiveLong, Vec}
import net.kurobako.gemstone.isa.GlobalName
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.isa.KernelName
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
  ): Either[RuntimeBackend.Error, RuntimeBackend.Program] = {
    val machine = AMDGPUMachine(services.observer, services.scheduler)
    for {
      program <- CodeObject.Decoder.decode(image).left.map(error => RuntimeBackend.Error.Invalid(error.message))
      loaded  <- machine.load(program).left.map(error => RuntimeBackend.Error.Invalid(error.message))
    } yield Program(machine)(loaded)
  }

  private final class Program(machine: AMDGPUMachine)(loaded: machine.LoadedProgram)
      extends MachineProgram[AMDGPUMachine](machine, loaded, "HSA program is closed") {
    override def symbols: Either[RuntimeBackend.Error, ProgramSymbols] =
      open(machine.symbols(loaded).left.map(error => RuntimeBackend.Error.Invalid(error.message)))

    override def kernel(name: KernelName): Either[RuntimeBackend.Error, KernelMetadata] =
      open(machine.kernel(loaded, name).left.map(error => RuntimeBackend.Error.Invalid(error.message)))

    override val globals: Option[RuntimeBackend.Program.Globals] = Some(Globals(machine)(loaded))
  }

  private final class Globals(machine: AMDGPUMachine)(loaded: machine.LoadedProgram)
      extends RuntimeBackend.Program.Globals {
    override def image(name: GlobalName): Either[RuntimeBackend.Error, RuntimeBackend.Program.Globals.Image] =
      machine.global(loaded, name).left.map(error => RuntimeBackend.Error.Invalid(error.message))
        .map((size, initial) => RuntimeBackend.Program.Globals.Image(size, initial))

    override def bind(
        name: GlobalName,
        region: MemoryRegion,
        offset: NonNegativeLong,
        regionSize: PositiveLong,
        memory: Memory
    ): Either[RuntimeBackend.Error, Unit] =
      machine.bindGlobal(loaded, name, region, offset, regionSize, memory).left.map(RuntimeBackend.Error.Execution(_))

    override def write(
        name: GlobalName,
        offset: NonNegativeLong,
        bytes: Vec[Byte]
    ): Either[RuntimeBackend.Error, Unit] =
      machine.writeGlobal(loaded, name, offset, bytes).left.map(RuntimeBackend.Error.Execution(_))
  }
}
