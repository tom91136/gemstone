package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.{GlobalName, KernelMetadata, KernelName, ProgramSymbols}
import net.kurobako.gemstone.isa.amdgpu.CodeObject.CodeObjectKernel
import net.kurobako.gemstone.vm.{AddressSpace, Fault, KernelTask, Memory, MemoryRegion, Scheduler, TaskControl}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[amdgpu] final class VMLoadedProgram(
    resolveKernel: String => Either[Fault, CodeObjectKernel],
    describeKernel: String => Either[Fault, KernelMetadata],
    describeSymbols: () => Either[Fault, ProgramSymbols],
    describeGlobal: GlobalName => Either[Fault, (Long :| Positive0, Vec[Byte])],
    writeGlobalFn: (GlobalName, Long :| Positive0, Vec[Byte]) => Either[Fault, Unit],
    bindGlobalFn: (
        GlobalName,
        MemoryRegion,
        Long :| Positive0,
        Long :| Positive,
        Memory
    ) => Either[Fault, Unit],
    bindings: () => Vec[VMLoadedProgram.GlobalBinding]
) {
  private val compiled = java.util.concurrent.ConcurrentHashMap[
    String,
    Either[Fault, VMIR.Kernel]
  ]()

  def symbols: Either[Fault, ProgramSymbols] = describeSymbols()

  def kernel(name: KernelName): Either[Fault, KernelMetadata] = describeKernel(name.value)

  def global(name: GlobalName): Either[Fault, (Long :| Positive0, Vec[Byte])] = describeGlobal(name)

  def execute(
      task: KernelTask,
      memory: Memory,
      addresses: AddressSpace,
      control: TaskControl,
      scheduler: Scheduler
  ): Either[Fault, Unit] = resolveKernel(task.kernelName.value) match {
    case Left(error)   => Left(error)
    case Right(kernel) =>
      compiled.computeIfAbsent(kernel.name, _ => VMCompiler.compile(kernel)) match {
        case Left(error)    => Left(error)
        case Right(program) =>
          VMInterpreter.execute(program, task, memory, addresses, control, scheduler, bindings())
      }
  }

  def bindGlobal(
      name: GlobalName,
      allocation: MemoryRegion,
      offset: Long :| Positive0,
      allocationSize: Long :| Positive,
      memory: Memory
  ): Either[Fault, Unit] = bindGlobalFn(name, allocation, offset, allocationSize, memory)

  def writeGlobal(
      name: GlobalName,
      offset: Long :| Positive0,
      bytes: Vec[Byte]
  ): Either[Fault, Unit] = writeGlobalFn(name, offset, bytes)
}

private[amdgpu] object VMLoadedProgram {
  final case class GlobalBinding(
      address: Long,
      size: Int :| Positive0,
      allocation: MemoryRegion,
      offset: Long :| Positive0,
      allocationSize: Long :| Positive
  )
}
