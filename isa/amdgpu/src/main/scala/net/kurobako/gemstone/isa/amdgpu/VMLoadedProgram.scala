package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.{KernelMetadata, ProgramSymbols}
import net.kurobako.gemstone.isa.amdgpu.CodeObject.CodeObjectKernel
import net.kurobako.gemstone.vm.{AddressSpace, Fault, KernelTask, Memory, MemoryRegion, Scheduler, TaskControl}

private[amdgpu] final class VMLoadedProgram(
    resolveKernel: String => Either[Fault, CodeObjectKernel],
    describeKernel: String => Either[Fault, KernelMetadata],
    describeSymbols: () => Either[Fault, ProgramSymbols],
    describeGlobal: NonEmptyString => Either[Fault, (NonNegative[Long], Vec[Byte])],
    writeGlobalFn: (NonEmptyString, NonNegative[Long], Vec[Byte]) => Either[Fault, Unit],
    bindGlobalFn: (
        NonEmptyString,
        MemoryRegion,
        NonNegative[Long],
        Positive[Long],
        Memory
    ) => Either[Fault, Unit],
    bindings: () => Vec[VMLoadedProgram.GlobalBinding]
) {
  private val compiled = java.util.concurrent.ConcurrentHashMap[
    String,
    Either[Fault, VMIR.Kernel]
  ]()

  def symbols: Either[Fault, ProgramSymbols] = describeSymbols()

  def kernel(name: NonEmptyString): Either[Fault, KernelMetadata] = describeKernel(name.raw)

  def global(name: NonEmptyString): Either[Fault, (NonNegative[Long], Vec[Byte])] = describeGlobal(name)

  def execute(
      task: KernelTask,
      memory: Memory,
      addresses: AddressSpace,
      control: TaskControl,
      scheduler: Scheduler
  ): Either[Fault, Unit] = resolveKernel(task.kernelName.raw) match {
    case Left(error)   => Left(error)
    case Right(kernel) =>
      compiled.computeIfAbsent(kernel.name, _ => VMCompiler.compile(kernel)) match {
        case Left(error)    => Left(error)
        case Right(program) =>
          VMInterpreter.execute(program, task, memory, addresses, control, scheduler, bindings())
      }
  }

  def bindGlobal(
      name: NonEmptyString,
      allocation: MemoryRegion,
      offset: NonNegative[Long],
      allocationSize: Positive[Long],
      memory: Memory
  ): Either[Fault, Unit] = bindGlobalFn(name, allocation, offset, allocationSize, memory)

  def writeGlobal(
      name: NonEmptyString,
      offset: NonNegative[Long],
      bytes: Vec[Byte]
  ): Either[Fault, Unit] = writeGlobalFn(name, offset, bytes)
}

private[amdgpu] object VMLoadedProgram {
  case class GlobalBinding(
      address: Long,
      size: NonNegative[Int],
      allocation: MemoryRegion,
      offset: NonNegative[Long],
      allocationSize: Positive[Long]
  )
}
