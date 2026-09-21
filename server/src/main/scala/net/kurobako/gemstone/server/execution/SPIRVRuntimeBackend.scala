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

final class SPIRVRuntimeBackend(environment: Validation.Environment) extends RuntimeBackend {
  override val encoding: ProgramEncoding =
    ProgramEncoding.from("spirv-binary").fold(message => throw IllegalStateException(message), identity)

  override def open(services: MachineServices): BackendSession = Session(environment, services)

  private final class Session(environment: Validation.Environment, services: MachineServices) extends BackendSession {
    private val closed  = AtomicBoolean()
    private val machine = SPIRVMachine(environment, services.observer, services.scheduler)

    override def load(image: Vec[Byte]): Either[RuntimeError, RuntimeProgram] =
      if (closed.get()) Left(RuntimeError.Lifecycle("SPIR-V backend session is closed"))
      else
        for {
          module <- ISA.decode(image).left.map(error => RuntimeError.Decode(error.message))
          loaded <- machine.load(module).left.map(error => RuntimeError.Load(error.message))
        } yield Program(machine)(loaded)

    override def close(): Unit = closed.set(true)
  }

  private final class Program(machine: SPIRVMachine)(loaded: machine.LoadedProgram) extends RuntimeProgram {
    private val closed = AtomicBoolean()

    override def symbols: Either[RuntimeError, ProgramSymbols] = open {
      val kernels = machine.kernelNames(loaded).map(name => KernelSymbol(name, LinkageName(name.value)))
      Right(ProgramSymbols(kernels, Vec.empty))
    }

    override def kernel(name: KernelName): Either[RuntimeError, KernelMetadata] =
      open(machine.kernel(loaded, name).left.map(error => RuntimeError.Load(error.message)))

    override val globals: Option[RuntimeProgram.Globals] = None

    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl
    ): Either[RuntimeError, Result] =
      open(machine.execute(loaded, task, memory, control).left.map(RuntimeError.Execution(_)))

    override def close(): Unit =
      if (closed.compareAndSet(false, true)) machine.unload(loaded)

    private def open[A](operation: => Either[RuntimeError, A]): Either[RuntimeError, A] =
      if (closed.get()) Left(RuntimeError.Lifecycle("SPIR-V program is closed")) else operation
  }
}
