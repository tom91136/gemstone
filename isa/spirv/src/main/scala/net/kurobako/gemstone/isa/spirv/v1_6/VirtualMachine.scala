package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelMetadata
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

  def kernelNames(program: LoadedProgram): Vec[NonEmptyString] =
    VMInterpreter.kernelNames(program)

  def kernel(
      program: LoadedProgram,
      name: NonEmptyString
  ): Either[Fault, KernelMetadata] =
    VMInterpreter.kernelMetadata(program, name)

  def global(program: LoadedProgram, name: NonEmptyString): Either[Fault, (NonNegative[Long], Vec[Byte])] =
    VMInterpreter.globalImage(program, name.raw).map { case (_, bytes) =>
      NonNegative.unsafe(bytes.length.toLong) -> Vec.from(bytes)
    }

  def bindGlobal(
      program: LoadedProgram,
      name: NonEmptyString,
      region: MemoryRegion,
      offset: NonNegative[Long],
      regionSize: Positive[Long],
      memory: Memory
  ): Either[Fault, Unit] =
    VMInterpreter.globalImage(program, name.raw).flatMap { case (id, initial) =>
      if (offset.raw > regionSize.raw || initial.length.toLong > (regionSize.widen[NonNegative[Long]] - offset).raw)
        Left(Fault.Memory(s"SPIR-V global '$name' exceeds the bound allocation"))
      else memory.write(region, offset, Vec.from(initial)).map { _ =>
        val _ = program.boundGlobals.put(id, program.GlobalBinding(region, offset.raw, memory))
        ()
      }
    }

  def writeGlobal(
      program: LoadedProgram,
      name: NonEmptyString,
      offset: NonNegative[Long],
      bytes: Vec[Byte]
  ): Either[Fault, Unit] =
    VMInterpreter.globalImage(program, name.raw).flatMap { case (id, initial) =>
      program.boundGlobals.get(id).toRight(Fault.Memory(s"SPIR-V global '$name' is not bound")).flatMap { binding =>
        if (offset.raw > initial.length || bytes.length > initial.length - offset.raw)
          Left(Fault.Memory(s"SPIR-V global '$name' write exceeds its storage"))
        else binding.memory.write(binding.region, NonNegative.unsafe(binding.offset + offset.raw), bytes)
      }
    }

  override def unload(program: LoadedProgram): Unit = ()
}
