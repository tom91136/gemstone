package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ConcurrencyScheduleSuite extends Suite {

  private def run(schedule: ConcurrencySchedule, operations: Vec[Instruction]): Long = {
    val memory = RegionMemory()
    val id     = AllocationId(1)
    memory.allocate(id, 64, GuestAddress(0x1000L))

    val kernel = irKernel(operations, codeKernel(target = Target("gfx900")))
    val task   = KernelTask.validated(
      "race_kernel",
      Dimensions(128, 1, 1),
      Dimensions.One,
      Vec.empty
    ).value
    val context = TaskContext(memory, TaskControl(), _ => Right(()), schedule = schedule)
    assertEquals(
      VMInterpreter.execute(kernel, KernelLaunch(task, context, Workgroups.Sequential), Vec.empty),
      Right(())
    )
    val bytes = memory.read(id, 0L, 4).value
    LittleEndian.decodeInt32(bytes, 0)
  }

  private def lastWriter(schedule: ConcurrencySchedule): Long =
    run(
      schedule,
      Vec(
        Instruction.VMove(0, InstructionSource.Constant(0)),
        Instruction.VMove(1, InstructionSource.Constant(0)),
        Instruction.SReadWaveId(0),
        Instruction.VMove(2, InstructionSource.Scalar(0)),
        Instruction.Store(
          src = 2,
          address = MemoryAddress.Global(vector = 0, scalar = None, offset = 0x1000),
          bytes = 4,
          high = false
        ),
        Instruction.End
      )
    )

  private def orderEncoding(schedule: ConcurrencySchedule): Long =
    run(
      schedule,
      Vec(
        Instruction.VMove(3, InstructionSource.Constant(0)),
        Instruction.Load(0, MemoryAddress.Global(3, None, 0x1000), 4, false, LoadPlacement.Full),
        Instruction.SReadWaveId(0),
        Instruction.VMove(1, InstructionSource.Scalar(0)),
        Instruction.VBinary(
          VectorBinaryOperation.MultiplyLowU32,
          0,
          InstructionSource.VectorRegister(0),
          InstructionSource.Constant(4),
          false
        ),
        Instruction.VBinary(
          VectorBinaryOperation.AddU32,
          0,
          InstructionSource.VectorRegister(0),
          InstructionSource.VectorRegister(1),
          false
        ),
        Instruction.Store(
          src = 0,
          address = MemoryAddress.Global(vector = 3, scalar = None, offset = 0x1000),
          bytes = 4,
          high = false
        ),
        Instruction.End
      )
    )

  test("wave order in workgroup changes with seed and reproduces under same seed and fixed default") {
    val fixed          = lastWriter(ConcurrencySchedule.Fixed)
    val resSeed0       = lastWriter(ConcurrencySchedule.Random(0L))
    val repeated       = ConcurrencySchedule.Random(1L)
    val resSeed1       = lastWriter(repeated)
    val resSeed1Repeat = lastWriter(repeated)

    assertEquals(resSeed1, resSeed1Repeat)
    assertNotEquals(resSeed0, resSeed1)
    assert(fixed == resSeed0 || fixed == resSeed1)
  }

  test("one seeded schedule repeats the same launch in one process") {
    val schedule = ConcurrencySchedule.Random(1L)
    val first    = orderEncoding(schedule)
    val repeats  = (1 to 8).map(_ => orderEncoding(schedule))
    assert(repeats.forall(_ == first), s"launch order changed between launches: $first then $repeats")
  }
}
