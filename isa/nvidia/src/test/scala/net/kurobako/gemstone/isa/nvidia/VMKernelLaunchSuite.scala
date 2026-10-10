package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class VMKernelLaunchSuite extends munit.FunSuite {
  private val global = MemoryAccess.Global(system = false)

  private def kernel(parameters: Vec[KernelParameter], instructions: Vec[Instruction]): Kernel =
    Kernel(
      Entry(
        Routine("launch_test", ExecutableProgram(instructions.map(PredicatedInstruction(_, None))), 0, 0),
        parameters
      ),
      "SASS"
    )

  private def task(
      arguments: Vec[KernelTask.Argument],
      relocations: Vec[KernelTask.PointerRelocation]
  ): KernelTask =
    KernelTask.validated(
      "launch_test",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      arguments,
      pointerRelocations = relocations
    ).fold(message => fail(message.raw), identity)

  private def load(destination: Int, offset: Long, width: ScalarBitWidth): Instruction =
    Instruction.Load(
      programs.r(destination),
      Value.Immediate(VMAddressSpace.ParameterBase + offset),
      width,
      signed = false,
      MemoryAccess.Generic(false)
    )

  private def store(address: Int, source: Int, width: ScalarBitWidth): Instruction =
    Instruction.Store(programs.register(address), programs.register(source), width, global)

  private def run(
      executable: Kernel,
      arguments: Vec[KernelTask.Argument],
      relocations: Vec[KernelTask.PointerRelocation],
      memory: RegionMemory
  ): Either[Fault, Unit] =
    VMKernelLaunch.execute(
      executable,
      task(arguments, relocations),
      memory,
      TaskControl(),
      Workgroups.Sequential
    )

  test("shared launch binds scalar and memory arguments for interpreter parameter loads") {
    val program = kernel(
      Vec(KernelParameter("value", 0, 4), KernelParameter("out", 8, 8, pointer = true)),
      Vec(
        load(0, 0, 32),
        load(1, 8, 64),
        store(address = 1, source = 0, 32),
        Instruction.Return
      )
    )
    val memory = RegionMemory()
    val output = memory.allocate(4L)
    val scalar = KernelTask.Argument.Scalar(LittleEndian.toVec(0x12345678L, 4).unsafe)
    val result = run(program, Vec(scalar, KernelTask.Argument.Memory(output, 0L)), Vec.empty, memory)
    assertEquals(result, Right(()))
    assertEquals(memory.getInt(output, 0), 0x12345678)
  }

  test("shared launch applies valid argument relocation and rejects a relocation past the scalar") {
    val program = kernel(
      Vec(
        KernelParameter("pointer", 0, 8, pointer = true),
        KernelParameter("value", 8, 4),
        KernelParameter("out", 16, 8, pointer = true)
      ),
      Vec(
        load(0, 0, 64),
        load(1, 8, 32),
        load(2, 16, 64),
        store(address = 0, source = 1, 32),
        store(address = 2, source = 0, 64),
        Instruction.Return
      )
    )
    val memory          = RegionMemory()
    val target          = memory.allocate(8L)
    val output          = memory.allocate(8L)
    val pointer         = KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 8).unsafe)
    val value           = KernelTask.Argument.Scalar(LittleEndian.toVec(0x76543210L, 4).unsafe)
    val out             = KernelTask.Argument.Memory(output, 0L)
    val validRelocation = Vec(KernelTask.PointerRelocation(
      KernelTask.RelocationSource.Argument(0),
      0L,
      target,
      4L
    ))
    assertEquals(run(program, Vec(pointer, value, out), validRelocation, memory), Right(()))
    assertEquals(Vec(memory.getInt(target, 0), memory.getInt(target, 4)), Vec(0, 0x76543210))
    assertEquals(memory.readValue(output, 0L, 8: Int in 1 ~ 8), memory.address(target, 4L).map(_.raw))

    val invalidRelocation = validRelocation.map(_.copy(sourceOffset = 4L))
    val failure           = run(program, Vec(pointer, value, out), invalidRelocation, memory)
    assert(failure.left.exists {
      case Fault.InvalidRequest(message) => message.raw.contains("pointer relocation exceeds kernel argument 0")
      case _                             => false
    })
  }

  test("shared launch retains argument count, scalar width, and memory width validation") {
    val scalarProgram = kernel(
      Vec(KernelParameter("value", 0, 4)),
      Vec(Instruction.Return)
    )
    val localProgram = kernel(
      Vec(KernelParameter("scratch", 0, 8)),
      Vec(Instruction.Return)
    )
    val memory = RegionMemory()
    val region = memory.allocate(8L)
    val scalar = KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 8).unsafe)

    val missing = run(scalarProgram, Vec.empty, Vec.empty, memory)
    assert(missing.left.exists {
      case Fault.InvalidRequest(message) => message.raw.contains("expects 1 arguments, found 0")
      case _                             => false
    })

    val wrongScalarWidth = run(scalarProgram, Vec(scalar), Vec.empty, memory)
    assert(wrongScalarWidth.left.exists {
      case Fault.InvalidRequest(message) => message.raw.contains("expects 4 bytes, found 8")
      case _                             => false
    })

    val wrongMemoryWidth = run(scalarProgram, Vec(KernelTask.Argument.Memory(region, 0L)), Vec.empty, memory)
    assert(wrongMemoryWidth.left.exists {
      case Fault.InvalidRequest(message) => message.raw.contains("requires an eight-byte SASS parameter")
      case _                             => false
    })

    val unsupportedLocal = run(
      localProgram,
      Vec(KernelTask.Argument.Local(8L)),
      Vec.empty,
      memory
    )
    assert(unsupportedLocal.left.exists {
      case Fault.Unsupported(message) => message.raw.contains("local argument scratch")
      case _                          => false
    })
  }
}
