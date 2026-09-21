package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.GlobalName
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.isa.ProgramSymbols
import net.kurobako.gemstone.isa.amdgpu.CodeObject
import net.kurobako.gemstone.isa.amdgpu.VirtualMachine as AMDGPUMachine
import net.kurobako.gemstone.vm.KernelTask
import net.kurobako.gemstone.vm.Memory
import net.kurobako.gemstone.vm.MemoryRegion
import net.kurobako.gemstone.vm.Result
import net.kurobako.gemstone.vm.TaskControl

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicBoolean

object HSARuntimeBackend extends RuntimeBackend {
  override val encoding: ProgramEncoding =
    ProgramEncoding.from("hsa-code-object").fold(message => throw IllegalStateException(message), identity)

  override def open(services: MachineServices): BackendSession = Session(services)

  private final class Session(services: MachineServices) extends BackendSession {
    private val closed  = AtomicBoolean()
    private val machine = AMDGPUMachine(services.observer, services.scheduler)

    override def load(image: Vec[Byte]): Either[RuntimeError, RuntimeProgram] =
      if (closed.get()) Left(RuntimeError.Lifecycle("HSA backend session is closed"))
      else
        for {
          program <- CodeObject.Decoder.decode(image).left.map(error => RuntimeError.Decode(error.message))
          loaded  <- machine.load(program).left.map(error => RuntimeError.Load(error.message))
        } yield Program(machine)(loaded)

    override def close(): Unit = closed.set(true)
  }

  private final class Program(machine: AMDGPUMachine)(loaded: machine.LoadedProgram) extends RuntimeProgram {
    private val closed = AtomicBoolean()

    override def symbols: Either[RuntimeError, ProgramSymbols] =
      open(machine.symbols(loaded).left.map(error => RuntimeError.Load(error.message)))

    override def kernel(name: KernelName): Either[RuntimeError, KernelMetadata] =
      open(machine.kernel(loaded, name).left.map(error => RuntimeError.Load(error.message)))

    override val globals: Option[RuntimeProgram.Globals] = Some(Globals(machine)(loaded))

    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl
    ): Either[RuntimeError, Result] =
      open(machine.execute(loaded, task, memory, control).left.map(RuntimeError.Execution(_)))

    override def close(): Unit =
      if (closed.compareAndSet(false, true)) machine.unload(loaded)

    private def open[A](operation: => Either[RuntimeError, A]): Either[RuntimeError, A] =
      if (closed.get()) Left(RuntimeError.Lifecycle("HSA program is closed")) else operation
  }

  private final class Globals(machine: AMDGPUMachine)(loaded: machine.LoadedProgram) extends RuntimeProgram.Globals {
    override def image(name: GlobalName): Either[RuntimeError, RuntimeProgram.Globals.Image] =
      machine.global(loaded, name).left.map(error => RuntimeError.Load(error.message))
        .map((size, initial) => RuntimeProgram.Globals.Image(size, initial))

    override def bind(
        name: GlobalName,
        region: MemoryRegion,
        offset: Long :| Positive0,
        regionSize: Long :| Positive,
        memory: Memory
    ): Either[RuntimeError, Unit] =
      machine.bindGlobal(loaded, name, region, offset, regionSize, memory).left.map(RuntimeError.Execution(_))

    override def write(
        name: GlobalName,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[RuntimeError, Unit] =
      machine.writeGlobal(loaded, name, offset, bytes).left.map(RuntimeError.Execution(_))
  }
}
