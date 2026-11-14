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

  private def workgroupOrder(schedule: ConcurrencySchedule): Long = {
    val memory = RegionMemory()
    val id     = AllocationId(1)
    memory.allocate(id, 64, GuestAddress(0x1000L))
    memory.putInt(id, 0, 1)
    val operations = Vec(
      Instruction.VMove(0, InstructionSource.Constant(0)),
      Instruction.VMove(1, InstructionSource.Constant(0)),
      Instruction.Load(2, MemoryAddress.Global(0, None, 0x1000), 4, false, LoadPlacement.Full),
      Instruction.VMove(3, InstructionSource.Scalar(0)),
      Instruction.VBinary(
        VectorBinaryOperation.MultiplyLowU32,
        2,
        InstructionSource.VectorRegister(2),
        InstructionSource.Constant(3),
        false
      ),
      Instruction.VBinary(
        VectorBinaryOperation.AddU32,
        2,
        InstructionSource.VectorRegister(2),
        InstructionSource.VectorRegister(3),
        false
      ),
      Instruction.Store(
        src = 2,
        address = MemoryAddress.Global(vector = 0, scalar = None, offset = 0x1000),
        bytes = 4,
        high = false
      ),
      Instruction.End
    )
    val kernel = irKernel(operations, codeKernel(target = Target("gfx900"), computePgmRsrc2 = 0x380))
    val task   = KernelTask.validated(
      "wg_order",
      Dimensions(1, 1, 1),
      Dimensions(2, 1, 1),
      Vec.empty
    ).value
    assertEquals(executeKernel(kernel, task, memory, schedule = schedule), Right(()))
    LittleEndian.decodeInt32(memory.read(id, 0L, 4).value, 0)
  }

  test("workgroup order changes with seed and reproduces under the same seed") {
    val fixed    = workgroupOrder(ConcurrencySchedule.Fixed)
    val seed0    = workgroupOrder(ConcurrencySchedule.Random(0L))
    val repeated = ConcurrencySchedule.Random(1L)
    val seed1    = workgroupOrder(repeated)
    val again    = workgroupOrder(repeated)

    assertEquals(seed1, again)
    assertNotEquals(seed0, seed1)
    assert(fixed == seed0 || fixed == seed1)
  }

  test("a cooperative launch with a grid barrier completes under several seeds") {
    val text = """s_mov_b32 m0, 0
                  |v_mov_b32 v4, 1
                  |ds_gws_barrier v4 gds
                  |s_waitcnt vmcnt(0) expcnt(0) lgkmcnt(0)
                  |v_mov_b32 v2, 5
                  |global_store_dword v0, v2, s[2:3]
                  |s_endpgm
                  |""".stripMargin
    for (seed <- 0L to 3L) {
      val memory = RegionMemory()
      val out    = AllocationId(1L)
      memory.allocate(out, 64)
      assertEquals(
        CooperativeDispatchSuite.launch(
          text,
          groups = 2,
          cooperative = true,
          memory = memory,
          schedule = ConcurrencySchedule.Random(seed)
        ),
        Right(())
      )
      assertEquals(memory.getInt(out, 0), 5)
    }
  }

  test("a workgroup spinning on a flag another sets completes under several seeds") {
    for (seed <- 0L to 3L) {
      val memory = RegionMemory()
      val out    = AllocationId(1L)
      memory.allocate(out, 64)
      assertEquals(
        CooperativeDispatchSuite.launch(
          CooperativeDispatchSuite.spin,
          groups = 2,
          cooperative = false,
          memory = memory,
          schedule = ConcurrencySchedule.Random(seed)
        ),
        Right(())
      )
      assertEquals(memory.getInt(out, 4), 2)
    }
  }

  private class CountingRandom(seed: Long) extends ConcurrencySchedule.Random(seed) {
    var launchSchedules                         = 0
    override def forLaunch: ConcurrencySchedule = {
      launchSchedules += 1
      super.forLaunch
    }
  }

  test("the launch order schedule is made once per launch, not once per sweep") {
    val memory = RegionMemory()
    val out    = AllocationId(1L)
    memory.allocate(out, 64)
    val schedule = CountingRandom(7L)
    assertEquals(
      CooperativeDispatchSuite.launch(
        CooperativeDispatchSuite.spin,
        groups = 2,
        cooperative = false,
        memory = memory,
        schedule = schedule
      ),
      Right(())
    )
    assertEquals(memory.getInt(out, 4), 2)
    assertEquals(schedule.launchSchedules, 1)
  }

  test("a wave that blocks once then progresses is not reported as stalled") {
    val text = """s_get_waveid_in_workgroup s0
                 |s_barrier
                 |s_cmp_eq_u32 s0, 0
                 |s_cbranch_scc0 setter
                 |ds_gws_sema_p offset:1 gds
                 |s_waitcnt vmcnt(0) expcnt(0) lgkmcnt(0)
                 |s_mov_b32 s4, 100
                 |waiter:
                 |s_sub_u32 s4, s4, 1
                 |s_cmp_eq_u32 s4, 0
                 |s_cbranch_scc0 waiter
                 |s_endpgm
                 |setter:
                 |s_mov_b32 s5, 70
                 |delayer:
                 |s_sub_u32 s5, s5, 1
                 |s_cmp_eq_u32 s5, 0
                 |s_cbranch_scc0 delayer
                 |ds_gws_sema_v offset:1 gds
                 |s_waitcnt vmcnt(0) expcnt(0) lgkmcnt(0)
                 |s_endpgm
                 |""".stripMargin
    val memory = RegionMemory()
    val out    = AllocationId(1L)
    memory.allocate(out, 64)
    assertEquals(
      CooperativeDispatchSuite.launch(
        text,
        groups = 1,
        cooperative = true,
        memory = memory,
        schedule = ConcurrencySchedule.Random(7L),
        groupSize = 64,
        target = Target("gfx1010")
      ),
      Right(())
    )
  }

  test("a cooperative barrier that never completes is a hang") {
    val text = """s_cmp_eq_u32 s0, 0
                 |s_cbranch_scc0 exit
                 |s_mov_b32 m0, 0
                 |v_mov_b32 v4, 1
                 |ds_gws_barrier v4 gds
                 |s_waitcnt vmcnt(0) expcnt(0) lgkmcnt(0)
                 |exit:
                 |s_endpgm
                 |""".stripMargin
    val memory = RegionMemory()
    val out    = AllocationId(1L)
    memory.allocate(out, 64)
    assert(
      CooperativeDispatchSuite
        .launch(
          text,
          groups = 2,
          cooperative = true,
          memory = memory,
          schedule = ConcurrencySchedule.Random(7L)
        )
        .left
        .exists(error =>
          error.message.contains("Hang") || error.message.contains("hangs") || error.message.contains("waits")
        )
    )
  }
}
