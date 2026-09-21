package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.{KernelMetadata, KernelName}
import net.kurobako.gemstone.vm.{Event, Fault, KernelTask, Memory, Observer, Result, Scheduler, TaskControl}

final class VirtualMachine(
    environment: Validation.Environment,
    override val observer: Observer[Event],
    override val scheduler: Scheduler
) extends net.kurobako.gemstone.vm.VirtualMachine[Module] {
  override type LoadedProgram = VMLoadedProgram

  override def load(program: Module): Either[Fault, LoadedProgram] =
    VMCompiler.compile(program, environment).map(VMLoadedProgram(_))

  override def execute(
      program: LoadedProgram,
      task: KernelTask,
      memory: Memory,
      control: TaskControl
  ): Either[Fault, Result] =
    VMInterpreter.execute(program, task, memory, control, scheduler, observer)
      .map(_ => Result(control.usedInstructions))

  def kernelNames(program: LoadedProgram): Vec[KernelName] =
    VMInterpreter.kernelNames(program)

  def kernel(
      program: LoadedProgram,
      name: KernelName
  ): Either[Fault, KernelMetadata] =
    VMInterpreter.kernelMetadata(program, name)

  override def unload(program: LoadedProgram): Unit = ()
}
