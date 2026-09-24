package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.{GlobalName, KernelMetadata, KernelName}
import net.kurobako.gemstone.vm.{
  Event,
  Fault,
  KernelTask,
  Memory,
  MemoryRegion,
  Observer,
  Result,
  Scheduler,
  TaskControl
}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class VirtualMachine(
    environment: Validation.Environment,
    override val observer: Observer[Event],
    override val scheduler: Scheduler
) extends net.kurobako.gemstone.vm.VirtualMachine[Module] {
  override type LoadedProgram = VMLoadedProgram

  override def load(program: Module): Either[Fault, LoadedProgram] =
    VMCompiler.compile(program, environment).map(VMLoadedProgram(_))

  def bindProgramId(program: LoadedProgram, id: Long): Unit = program.programId = id

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

  def global(program: LoadedProgram, name: GlobalName): Either[Fault, (Long :| Positive0, Vec[Byte])] =
    VMInterpreter.globalImage(program, name.value).map { case (_, bytes) =>
      bytes.length.toLong.refineUnsafe[Positive0] -> Vec.from(bytes)
    }

  def bindGlobal(
      program: LoadedProgram,
      name: GlobalName,
      region: MemoryRegion,
      offset: Long :| Positive0,
      regionSize: Long :| Positive,
      memory: Memory
  ): Either[Fault, Unit] =
    VMInterpreter.globalImage(program, name.value).flatMap { case (id, initial) =>
      if (offset > regionSize || initial.length.toLong > regionSize - offset)
        Left(Fault.Memory(s"SPIR-V global '$name' exceeds the bound allocation"))
      else memory.write(region, offset, Vec.from(initial)).map { _ =>
        val _ = program.boundGlobals.put(id, program.GlobalBinding(region, offset, memory))
        ()
      }
    }

  def writeGlobal(
      program: LoadedProgram,
      name: GlobalName,
      offset: Long :| Positive0,
      bytes: Vec[Byte]
  ): Either[Fault, Unit] =
    VMInterpreter.globalImage(program, name.value).flatMap { case (id, initial) =>
      program.boundGlobals.get(id).toRight(Fault.Memory(s"SPIR-V global '$name' is not bound")).flatMap { binding =>
        if (offset > initial.length || bytes.length > initial.length - offset)
          Left(Fault.Memory(s"SPIR-V global '$name' write exceeds its storage"))
        else binding.memory.write(binding.region, (binding.offset + offset).refineUnsafe[Positive0], bytes)
      }
    }

  override def unload(program: LoadedProgram): Unit = ()
}
