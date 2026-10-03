package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class VMKernelLaunchSuite extends munit.FunSuite {
  private val global = MemoryAccess.Global(system = false)

  private def kernel(parameters: Vec[KernelParameter], instructions: Vec[Instruction]): Kernel =
    Kernel(
      "launch_test",
      parameters,
      ExecutableProgram(instructions.map(PredicatedInstruction(_, None))),
      isaName = "SASS"
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

  private def load(
      destination: NonEmpty[String],
      parameter: NonEmpty[String],
      width: Int where (Between[8, 64] & MultipleOf[8])
  ): Instruction =
    Instruction.LoadParameter(
      Vec(destination),
      parameter,
      0,
      width,
      signed = false
    )

  private def store(
      address: NonEmpty[String],
      source: NonEmpty[String],
      width: Int where (Between[8, 64] & MultipleOf[8])
  ): Instruction =
    Instruction.Store(
      Value.Register(address),
      Value.Register(source),
      width,
      global
    )

  private def run(
      executable: Kernel,
      arguments: Vec[KernelTask.Argument],
      relocations: Vec[KernelTask.PointerRelocation],
      memory: SASSFixture.Execution
  ): Either[Fault, Unit] =
    VMKernelLaunch.execute(executable, task(arguments, relocations), memory, TaskControl(), Workgroups.Sequential)

  test("shared launch binds scalar and memory arguments for interpreter parameter loads") {
    val program = kernel(
      Vec(KernelParameter("value", 4), KernelParameter("out", 8, pointer = true)),
      Vec(
        load("%r0", "value", 32),
        load("%r1", "out", 64),
        store("%r1", "%r0", 32),
        Instruction.Return
      )
    )
    val memory = SASSFixture.Execution()
    val output = memory.allocate(4L)
    val scalar = KernelTask.Argument.Scalar(LittleEndian.toVec(0x12345678L, 4))
    val result = run(program, Vec(scalar, KernelTask.Argument.Memory(output, 0L)), Vec.empty, memory)
    assertEquals(result, Right(()))
    assertEquals(memory.readWords(output, 1).head, 0x12345678L)
  }

  test("shared launch applies valid argument relocation and rejects a relocation past the scalar") {
    val program = kernel(
      Vec(
        KernelParameter("pointer", 8, pointer = true),
        KernelParameter("value", 4),
        KernelParameter("out", 8, pointer = true)
      ),
      Vec(
        load("%r0", "pointer", 64),
        load("%r1", "value", 32),
        load("%r2", "out", 64),
        store("%r0", "%r1", 32),
        store("%r2", "%r0", 64),
        Instruction.Return
      )
    )
    val memory          = SASSFixture.Execution()
    val target          = memory.allocate(8L)
    val output          = memory.allocate(8L)
    val pointer         = KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 8))
    val value           = KernelTask.Argument.Scalar(LittleEndian.toVec(0x76543210L, 4))
    val out             = KernelTask.Argument.Memory(output, 0L)
    val validRelocation = Vec(KernelTask.PointerRelocation(
      KernelTask.RelocationSource.Argument(0),
      0L,
      target,
      4L
    ))
    assertEquals(run(program, Vec(pointer, value, out), validRelocation, memory), Right(()))
    assertEquals(memory.readWords(target, 2), Vec(0L, 0x76543210L))
    assertEquals(memory.readValue(output, 0L, 8: Int in 1 ~ 8), memory.address(target, 4L).map(_.raw))

    val invalidRelocation = validRelocation.map(_.copy(sourceOffset = 4L))
    val failure           = run(program, Vec(pointer, value, out), invalidRelocation, memory)
    assert(failure.left.exists {
      case Fault.InvalidTask(message) => message.raw.contains("pointer relocation exceeds its SASS argument")
      case _                          => false
    })
  }

  test("shared launch retains argument count, scalar width, and memory width validation") {
    val scalarProgram = kernel(
      Vec(KernelParameter("value", 4)),
      Vec(Instruction.Return)
    )
    val localProgram = kernel(
      Vec(KernelParameter("scratch", 8)),
      Vec(Instruction.Return)
    )
    val memory = SASSFixture.Execution()
    val region = memory.allocate(8L)
    val scalar = KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 8))

    val missing = run(scalarProgram, Vec.empty, Vec.empty, memory)
    assert(missing.left.exists {
      case Fault.InvalidTask(message) => message.raw.contains("expects 1 arguments, found 0")
      case _                          => false
    })

    val wrongScalarWidth = run(scalarProgram, Vec(scalar), Vec.empty, memory)
    assert(wrongScalarWidth.left.exists {
      case Fault.InvalidTask(message) => message.raw.contains("expects 4 bytes, found 8")
      case _                          => false
    })

    val wrongMemoryWidth = run(scalarProgram, Vec(KernelTask.Argument.Memory(region, 0L)), Vec.empty, memory)
    assert(wrongMemoryWidth.left.exists {
      case Fault.InvalidTask(message) => message.raw.contains("requires an eight-byte SASS parameter")
      case _                          => false
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
