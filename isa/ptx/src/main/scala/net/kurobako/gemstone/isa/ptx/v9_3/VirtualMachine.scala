package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.NonNegativeLong
import net.kurobako.gemstone.PositiveLong
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.GlobalName
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.isa.ProgramSymbols
import net.kurobako.gemstone.vm.AddressSpace
import net.kurobako.gemstone.vm.Event
import net.kurobako.gemstone.vm.Fault
import net.kurobako.gemstone.vm.KernelTask
import net.kurobako.gemstone.vm.Memory
import net.kurobako.gemstone.vm.MemoryRegion
import net.kurobako.gemstone.vm.Observer
import net.kurobako.gemstone.vm.Result
import net.kurobako.gemstone.vm.Scheduler
import net.kurobako.gemstone.vm.TaskControl

final class VirtualMachine(
    override val observer: Observer[Event],
    override val scheduler: Scheduler
) extends net.kurobako.gemstone.vm.VirtualMachine[Program] {
  override type LoadedProgram = VMLoadedProgram

  override def load(program: Program): Either[Fault, LoadedProgram] =
    VMLoadedProgram.load(program)

  override def execute(
      program: LoadedProgram,
      task: KernelTask,
      memory: Memory,
      control: TaskControl
  ): Either[Fault, Result] =
    program.execute(task, memory, control, scheduler)

  def symbols(program: LoadedProgram): Either[Fault, ProgramSymbols] = program.symbols

  def kernel(program: LoadedProgram, name: KernelName): Either[Fault, KernelMetadata] =
    program.kernelMetadata(name)

  def global(
      program: LoadedProgram,
      name: GlobalName
  ): Either[Fault, (NonNegativeLong, Vec[Byte])] = program.globalImage(name)

  def bindGlobal(
      program: LoadedProgram,
      name: GlobalName,
      region: MemoryRegion,
      offset: NonNegativeLong,
      regionSize: PositiveLong,
      addresses: AddressSpace
  ): Either[Fault, Unit] = program.bindGlobal(name, region, offset, regionSize, addresses)

  override def unload(program: LoadedProgram): Unit = ()
}
