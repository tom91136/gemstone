package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.vm.{KernelTask, Memory, Result, TaskControl, VirtualMachine}

import java.util.concurrent.atomic.AtomicBoolean

/** Shared closed-flag, execute, and unload plumbing for the machine-backed backends. */
private[execution] abstract class MachineProgram[Machine <: VirtualMachine[?]](
    val machine: Machine,
    val loaded: machine.LoadedProgram,
    closedMessage: String
) extends RuntimeBackend.Program {
  private val closed = AtomicBoolean()

  protected def open[A](operation: => Either[RuntimeBackend.Error, A]): Either[RuntimeBackend.Error, A] =
    if (closed.get()) Left(RuntimeBackend.Error.Failed(closedMessage)) else operation

  override def execute(
      task: KernelTask,
      memory: Memory,
      control: TaskControl
  ): Either[RuntimeBackend.Error, Result] =
    open(machine.execute(loaded, task, memory, control).left.map(RuntimeBackend.Error.Execution(_)))

  override def close(): Unit = if (closed.compareAndSet(false, true)) machine.unload(loaded)
}
