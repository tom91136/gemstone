package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PositiveLong, Vec}
import net.kurobako.gemstone.isa.{GlobalName, KernelMetadata, KernelName, ProgramSymbols}
import net.kurobako.gemstone.isa.amdgpu.CodeObject.CodeObjectKernel
import net.kurobako.gemstone.vm.{AddressSpace, Fault, KernelTask, Memory, MemoryRegion, Scheduler, TaskControl}

private[amdgpu] final class VMLoadedProgram(
    resolveKernel: String => Either[Fault, CodeObjectKernel],
    describeKernel: String => Either[Fault, KernelMetadata],
    describeSymbols: () => Either[Fault, ProgramSymbols],
    describeGlobal: GlobalName => Either[Fault, (NonNegativeLong, Vec[Byte])],
    writeGlobalFn: (GlobalName, NonNegativeLong, Vec[Byte]) => Either[Fault, Unit],
    bindGlobalFn: (
        GlobalName,
        MemoryRegion,
        NonNegativeLong,
        PositiveLong,
        Memory
    ) => Either[Fault, Unit],
    bindings: () => Vec[VMLoadedProgram.GlobalBinding]
) {
  private val compiled = java.util.concurrent.ConcurrentHashMap[
    String,
    Either[Fault, VMIR.Kernel]
  ]()

  def symbols: Either[Fault, ProgramSymbols] = describeSymbols()

  def kernel(name: KernelName): Either[Fault, KernelMetadata] = describeKernel(name.raw)

  def global(name: GlobalName): Either[Fault, (NonNegativeLong, Vec[Byte])] = describeGlobal(name)

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
      name: GlobalName,
      allocation: MemoryRegion,
      offset: NonNegativeLong,
      allocationSize: PositiveLong,
      memory: Memory
  ): Either[Fault, Unit] = bindGlobalFn(name, allocation, offset, allocationSize, memory)

  def writeGlobal(
      name: GlobalName,
      offset: NonNegativeLong,
      bytes: Vec[Byte]
  ): Either[Fault, Unit] = writeGlobalFn(name, offset, bytes)
}

private[amdgpu] object VMLoadedProgram {
  final case class GlobalBinding(
      address: Long,
      size: NonNegativeInt,
      allocation: MemoryRegion,
      offset: NonNegativeLong,
      allocationSize: PositiveLong
  )
}
