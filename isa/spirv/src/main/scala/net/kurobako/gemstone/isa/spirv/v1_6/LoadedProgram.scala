package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

object LoadedProgram {
  def load(module: Module, environment: Validation.Environment): Either[Fault, LoadedProgram] =
    VMCompiler.compile(module, environment).map(VMProgramRuntime(_))
}

/** A loaded SPIR-V program owns its compiled state, globals, and execution. */
trait LoadedProgram {
  def kernelNames: Vec[NonEmpty[String]]
  def symbols: Either[Fault, ProgramSymbols]
  def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata]
  def global(name: NonEmpty[String]): Either[Fault, GlobalImage]
  def bindGlobal(
      name: NonEmpty[String],
      region: AllocationId,
      offset: Long >= 0,
      regionSize: Long > 0,
      memory: Memory
  ): Either[Fault, Unit]
  def writeGlobal(name: NonEmpty[String], offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit]
  def bindFunctionPointerNamespace(id: Long): Either[Fault, Unit]
  def execute(
      task: KernelTask,
      memory: Memory,
      control: TaskControl,
      workgroups: Workgroups = Workgroups.Sequential,
      handler: Event => Either[Fault, Unit] = _ => Right(()),
      measurement: ExecutionMetrics => Unit = _ => ()
  ): Either[Fault, Unit]
}
