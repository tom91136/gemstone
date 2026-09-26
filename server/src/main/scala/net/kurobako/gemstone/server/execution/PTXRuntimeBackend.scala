package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.{NonNegativeLong, PositiveLong, Vec}
import net.kurobako.gemstone.isa.GlobalName
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.isa.ProgramSymbols
import net.kurobako.gemstone.isa.ptx.v9_3.Parser
import net.kurobako.gemstone.isa.ptx.v9_3.VirtualMachine as PTXMachine
import net.kurobako.gemstone.vm.AddressSpace
import net.kurobako.gemstone.vm.Memory
import net.kurobako.gemstone.vm.MemoryRegion

import java.nio.charset.StandardCharsets

object PTXRuntimeBackend extends RuntimeBackend {
  override val encoding: String = "ptx-text"

  override def load(
      services: RuntimeBackend.Services,
      image: Vec[Byte]
  ): Either[RuntimeBackend.Error, RuntimeBackend.Program] = {
    val machine = PTXMachine(services.observer, services.scheduler)
    for {
      program <- Parser.decode(String(Vec.toArray(image), StandardCharsets.UTF_8))
        .left.map(error => RuntimeBackend.Error.Invalid(error.message))
      loaded <- machine.load(program).left.map(error => RuntimeBackend.Error.Invalid(error.message))
    } yield Program(machine)(loaded)
  }

  private final class Program(machine: PTXMachine)(loaded: machine.LoadedProgram)
      extends MachineProgram[PTXMachine](machine, loaded, "PTX program is closed") {
    override def symbols: Either[RuntimeBackend.Error, ProgramSymbols] =
      open(machine.symbols(loaded).left.map(error => RuntimeBackend.Error.Invalid(error.message)))

    override def kernel(name: KernelName): Either[RuntimeBackend.Error, KernelMetadata] =
      open(machine.kernel(loaded, name).left.map(error => RuntimeBackend.Error.Invalid(error.message)))

    override val globals: Option[RuntimeBackend.Program.Globals] = Some(Globals(machine)(loaded))
  }

  private final class Globals(machine: PTXMachine)(loaded: machine.LoadedProgram)
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
    ): Either[RuntimeBackend.Error, Unit] = memory match {
      case addresses: AddressSpace =>
        machine.global(loaded, name).left.map(_ =>
          RuntimeBackend.Error.NotFound(s"PTX global '${name.raw}' is not present")
        )
          .flatMap(_ =>
            machine.bindGlobal(loaded, name, region, offset, regionSize, addresses)
              .left.map(RuntimeBackend.Error.Execution(_))
          )
      case _ => Left(RuntimeBackend.Error.Unsupported("PTX global binding requires address-space capable memory"))
    }

    override def write(
        name: GlobalName,
        offset: NonNegativeLong,
        bytes: Vec[Byte]
    ): Either[RuntimeBackend.Error, Unit] = Left(RuntimeBackend.Error.Unsupported("PTX globals are not writable"))
  }
}
