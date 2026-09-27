package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.rpc.ExecutionProtocol.ExecutionFailure
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.Constraint.NonEmptyString
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
  ): Either[ExecutionFailure, RuntimeBackend.Program] = {
    val machine = PTXMachine(services.observer, services.scheduler)
    for {
      program <- Parser.decode(String(Vec.toArray(image), StandardCharsets.UTF_8))
        .left.map(error => ExecutionFailure.InvalidRequest(error.message))
      loaded <- machine.load(program).left.map(error => ExecutionFailure.InvalidRequest(error.message))
    } yield Program(machine)(loaded)
  }

  private final class Program(machine: PTXMachine)(loaded: machine.LoadedProgram)
      extends MachineProgram[PTXMachine](machine, loaded, "PTX program is closed") {
    override def symbols: Either[ExecutionFailure, ProgramSymbols] =
      open(machine.symbols(loaded).left.map(error => ExecutionFailure.InvalidRequest(error.message)))

    override def kernel(name: NonEmptyString): Either[ExecutionFailure, KernelMetadata] =
      open(machine.kernel(loaded, name).left.map(error => ExecutionFailure.InvalidRequest(error.message)))

    override val globals: Option[RuntimeBackend.Program.Globals] = Some(Globals(machine)(loaded))
  }

  private final class Globals(machine: PTXMachine)(loaded: machine.LoadedProgram)
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
    ): Either[ExecutionFailure, Unit] = memory match {
      case addresses: AddressSpace =>
        machine.global(loaded, name).left.map(_ =>
          ExecutionFailure.NotFound(s"PTX global '${name.raw}' is not present")
        )
          .flatMap(_ =>
            machine.bindGlobal(loaded, name, region, offset, regionSize, addresses)
              .left.map(ExecutionFailure.fromFault(_))
          )
      case _ => Left(ExecutionFailure.Unsupported("PTX global binding requires address-space capable memory"))
    }

    override def write(
        name: NonEmptyString,
        offset: NonNegative[Long],
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] = Left(ExecutionFailure.Unsupported("PTX globals are not writable"))
  }
}
