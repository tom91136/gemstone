package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.isa.KernelSymbol
import net.kurobako.gemstone.isa.LinkageName
import net.kurobako.gemstone.isa.ProgramSymbols
import net.kurobako.gemstone.isa.spirv.v1_6.ISA
import net.kurobako.gemstone.isa.spirv.v1_6.Validation
import net.kurobako.gemstone.isa.spirv.v1_6.VirtualMachine as SPIRVMachine
import net.kurobako.gemstone.vm.KernelTask
import net.kurobako.gemstone.vm.Memory
import net.kurobako.gemstone.vm.Result
import net.kurobako.gemstone.vm.TaskControl

import java.util.concurrent.atomic.AtomicBoolean

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

  private final class Program(machine: SPIRVMachine)(loaded: machine.LoadedProgram) extends RuntimeBackend.Program {
    private val closed = AtomicBoolean()

    override def symbols: Either[RuntimeBackend.Error, ProgramSymbols] = open {
      val kernels = machine.kernelNames(loaded).map(name => KernelSymbol(name, LinkageName(name.value)))
      Right(ProgramSymbols(kernels, Vec.empty))
    }

    override def kernel(name: KernelName): Either[RuntimeBackend.Error, KernelMetadata] =
      open(machine.kernel(loaded, name).left.map(error => RuntimeBackend.Error.Invalid(error.message)))

    override val globals: Option[RuntimeBackend.Program.Globals] = None

    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl
    ): Either[RuntimeBackend.Error, Result] =
      open(machine.execute(loaded, task, memory, control).left.map(RuntimeBackend.Error.Execution(_)))

    override def close(): Unit =
      if (closed.compareAndSet(false, true)) machine.unload(loaded)

    private def open[A](operation: => Either[RuntimeBackend.Error, A]): Either[RuntimeBackend.Error, A] =
      if (closed.get()) Left(RuntimeBackend.Error.Failed("SPIR-V program is closed")) else operation
  }
}
