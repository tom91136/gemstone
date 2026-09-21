package net.kurobako.gemstone.vm

import net.kurobako.gemstone.isa.Program

trait VirtualMachine[P <: Program] {
  type LoadedProgram

  def observer: Observer[Event]
  def scheduler: Scheduler

  def load(program: P): Either[Fault, LoadedProgram]

  def execute(
      program: LoadedProgram,
      task: KernelTask,
      memory: Memory,
      control: TaskControl
  ): Either[Fault, Result]

  def unload(program: LoadedProgram): Unit
}
