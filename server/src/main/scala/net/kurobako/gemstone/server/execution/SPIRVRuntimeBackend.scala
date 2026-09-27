package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.rpc.ExecutionProtocol.ExecutionFailure
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.isa.KernelSymbol
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.isa.ProgramSymbols
import net.kurobako.gemstone.isa.spirv.v1_6.ISA
import net.kurobako.gemstone.isa.spirv.v1_6.Validation
import net.kurobako.gemstone.isa.spirv.v1_6.VirtualMachine as SPIRVMachine
import net.kurobako.gemstone.vm.Memory
import net.kurobako.gemstone.vm.MemoryRegion

final class SPIRVRuntimeBackend(environment: Validation.Environment, override val encoding: String = "spirv-binary")
    extends RuntimeBackend {
  override def load(
      services: RuntimeBackend.Services,
      image: Vec[Byte]
  ): Either[ExecutionFailure, RuntimeBackend.Program] = {
    val machine = SPIRVMachine(environment, services.observer, services.scheduler)
    for {
      module <- ISA.decode(image).left.map(error => ExecutionFailure.InvalidRequest(error.message))
      loaded <- machine.load(module).left.map(error => ExecutionFailure.InvalidRequest(error.message))
    } yield Program(machine)(loaded)
  }

  private final class Program(machine: SPIRVMachine)(loaded: machine.LoadedProgram)
      extends MachineProgram[SPIRVMachine](machine, loaded, "SPIR-V program is closed") {
    override def bindProgramId(id: Long): Unit = machine.bindProgramId(loaded, id)

    override def symbols: Either[ExecutionFailure, ProgramSymbols] = open {
      val kernels = machine.kernelNames(loaded).map(name => KernelSymbol(name, name))
      Right(ProgramSymbols(kernels, Vec.empty))
    }

    override def kernel(name: NonEmptyString): Either[ExecutionFailure, KernelMetadata] =
      open(machine.kernel(loaded, name).left.map(error => ExecutionFailure.InvalidRequest(error.message)))

    override val globals: Option[RuntimeBackend.Program.Globals] = Some(Globals(machine)(loaded))
  }

  private final class Globals(machine: SPIRVMachine)(loaded: machine.LoadedProgram)
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
      machine.bindGlobal(loaded, name, region, offset, regionSize, memory)
        .left.map(ExecutionFailure.fromFault(_))

    override def write(
        name: NonEmptyString,
        offset: NonNegative[Long],
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] =
      machine.writeGlobal(loaded, name, offset, bytes).left.map(ExecutionFailure.fromFault(_))
  }
}
