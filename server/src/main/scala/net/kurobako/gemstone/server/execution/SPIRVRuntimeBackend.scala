package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.GlobalName
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.isa.KernelSymbol
import net.kurobako.gemstone.isa.LinkageName
import net.kurobako.gemstone.isa.ProgramSymbols
import net.kurobako.gemstone.isa.spirv.v1_6.ISA
import net.kurobako.gemstone.isa.spirv.v1_6.Validation
import net.kurobako.gemstone.isa.spirv.v1_6.VirtualMachine as SPIRVMachine
import net.kurobako.gemstone.vm.Memory
import net.kurobako.gemstone.vm.MemoryRegion

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class SPIRVRuntimeBackend(environment: Validation.Environment, override val encoding: String = "spirv-binary")
    extends RuntimeBackend {
  override def load(
      services: RuntimeBackend.Services,
      image: Vec[Byte]
  ): Either[RuntimeBackend.Error, RuntimeBackend.Program] = {
    val machine = SPIRVMachine(environment, services.observer, services.scheduler)
    for {
      module <- ISA.decode(image).left.map(error => RuntimeBackend.Error.Invalid(error.message))
      loaded <- machine.load(module).left.map(error => RuntimeBackend.Error.Invalid(error.message))
    } yield Program(machine)(loaded)
  }

  private final class Program(machine: SPIRVMachine)(loaded: machine.LoadedProgram)
      extends MachineProgram[SPIRVMachine](machine, loaded, "SPIR-V program is closed") {
    override def bindProgramId(id: Long): Unit = machine.bindProgramId(loaded, id)

    override def symbols: Either[RuntimeBackend.Error, ProgramSymbols] = open {
      val kernels = machine.kernelNames(loaded).map(name => KernelSymbol(name, LinkageName(name.value)))
      Right(ProgramSymbols(kernels, Vec.empty))
    }

    override def kernel(name: KernelName): Either[RuntimeBackend.Error, KernelMetadata] =
      open(machine.kernel(loaded, name).left.map(error => RuntimeBackend.Error.Invalid(error.message)))

    override val globals: Option[RuntimeBackend.Program.Globals] = Some(Globals(machine)(loaded))
  }

  private final class Globals(machine: SPIRVMachine)(loaded: machine.LoadedProgram)
      extends RuntimeBackend.Program.Globals {
    override def image(name: GlobalName): Either[RuntimeBackend.Error, RuntimeBackend.Program.Globals.Image] =
      machine.global(loaded, name).left.map(error => RuntimeBackend.Error.Invalid(error.message))
        .map((size, initial) => RuntimeBackend.Program.Globals.Image(size, initial))

    override def bind(
        name: GlobalName,
        region: MemoryRegion,
        offset: Long :| Positive0,
        regionSize: Long :| Positive,
        memory: Memory
    ): Either[RuntimeBackend.Error, Unit] =
      machine.bindGlobal(loaded, name, region, offset, regionSize, memory)
        .left.map(RuntimeBackend.Error.Execution(_))

    override def write(
        name: GlobalName,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[RuntimeBackend.Error, Unit] =
      machine.writeGlobal(loaded, name, offset, bytes).left.map(RuntimeBackend.Error.Execution(_))
  }
}
