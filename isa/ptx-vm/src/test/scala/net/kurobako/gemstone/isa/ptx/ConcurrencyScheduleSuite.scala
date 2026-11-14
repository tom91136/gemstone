package net.kurobako.gemstone.isa.ptx

import execution.*
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.ptx.asm.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable
import scala.util.Using

class ConcurrencyScheduleSuite extends Suite {

  private val kernelSource =
    """.version 7.0
      |.target sm_70
      |.address_size 64
      |.visible .entry race_kernel(.param .u64 out) {
      |  .reg .u64 %out_addr;
      |  .reg .u32 %tid;
      |  .reg .pred %p;
      |  ld.param.u64 %out_addr, [out];
      |  mov.u32 %tid, %tid.x;
      |  setp.ge.u32 %p, %tid, 32;
      |  @%p bra WARP1;
      |WARP0:
      |  st.global.u32 [%out_addr], 100;
      |  bra.uni FINISH;
      |WARP1:
      |  st.global.u32 [%out_addr], 200;
      |  bra.uni FINISH;
      |FINISH:
      |  ret;
      |}""".stripMargin

  private val orderSource =
    """.version 7.0
      |.target sm_70
      |.address_size 64
      |.visible .entry order_kernel(.param .u64 out) {
      |  .reg .u64 %out_addr;
      |  .reg .u32 %tid, %value, %next;
      |  .reg .pred %p;
      |  ld.param.u64 %out_addr, [out];
      |  mov.u32 %tid, %tid.x;
      |  setp.ge.u32 %p, %tid, 32;
      |  @%p bra WARP1;
      |WARP0:
      |  ld.global.u32 %value, [%out_addr];
      |  mul.lo.u32 %next, %value, 2;
      |  st.global.u32 [%out_addr], %next;
      |  bra.uni FINISH;
      |WARP1:
      |  ld.global.u32 %value, [%out_addr];
      |  mul.lo.u32 %next, %value, 2;
      |  add.u32 %next, %next, 1;
      |  st.global.u32 [%out_addr], %next;
      |  bra.uni FINISH;
      |FINISH:
      |  ret;
      |}""".stripMargin

  private def runWithSchedule(schedule: ConcurrencySchedule): Int =
    run(kernelSource, schedule)

  private def run(source: String, schedule: ConcurrencySchedule): Int = {
    val kernel = compileKernel(source)
    val memory = DeviceMemory(64)
    val result = execute(
      kernel,
      blocks = 1,
      threads = 64,
      arguments = Map(("out": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory = memory,
      schedule = schedule
    )
    assertEquals(result, Right(()))
    memory.readInt32(memory.start).toOption.get
  }

  test("cohort order in workgroup changes with seed and reproduces under same seed and fixed default") {
    val fixed          = runWithSchedule(ConcurrencySchedule.Fixed)
    val resSeed0       = runWithSchedule(ConcurrencySchedule.Random(0L))
    val repeated       = ConcurrencySchedule.Random(1L)
    val resSeed1       = runWithSchedule(repeated)
    val resSeed1Repeat = runWithSchedule(repeated)

    assertEquals(resSeed1, resSeed1Repeat)
    assertNotEquals(resSeed0, resSeed1)
    assert(fixed == resSeed0 || fixed == resSeed1)
  }

  test("one seeded schedule repeats the same launch in one process") {
    val schedule = ConcurrencySchedule.Random(1L)
    val first    = run(orderSource, schedule)
    val repeats  = (1 to 8).map(_ => run(orderSource, schedule))
    assert(repeats.forall(_ == first), s"launch order changed between launches: $first then $repeats")
  }

  private val workgroupOrderSource =
    """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry wg_order(.param .u64 out) {
      |  .reg .b32 %r<4>;
      |  .reg .b64 %rd<2>;
      |  ld.param.u64 %rd0, [out];
      |  mov.u32 %r0, %ctaid.x;
      |  ld.global.u32 %r1, [%rd0];
      |  mul.lo.u32 %r2, %r1, 3;
      |  add.u32 %r2, %r2, %r0;
      |  st.global.u32 [%rd0], %r2;
      |  ret;
      |}""".stripMargin

  private val spinSource =
    """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry wg_wait(.param .u64 out) {
      |  .reg .b32 %r<4>;
      |  .reg .b64 %rd<2>;
      |  .reg .pred %p<2>;
      |  ld.param.u64 %rd0, [out];
      |  mov.u32 %r0, %ctaid.x;
      |  setp.ne.u32 %p0, %r0, 0;
      |  @%p0 bra SET;
      |WAIT:
      |  ld.global.u32 %r1, [%rd0];
      |  setp.eq.u32 %p1, %r1, 1;
      |  @!%p1 bra WAIT;
      |  ld.global.u32 %r2, [%rd0+4];
      |  add.u32 %r2, %r2, 1;
      |  st.global.u32 [%rd0+4], %r2;
      |  bra DONE;
      |SET:
      |  mov.u32 %r3, 1;
      |  st.global.u32 [%rd0], %r3;
      |DONE:
      |  ret;
      |}""".stripMargin

  private def launch(
      source: String,
      blocks: Int > 0,
      threads: Int > 0,
      schedule: ConcurrencySchedule,
      cooperative: Boolean = false,
      workgroups: Workgroups = Workgroups.Sequential,
      limits: TaskControl.Limits = TaskControl.Limits.Default,
      initial: Int = 0
  ): (Either[Fault, Unit], DeviceMemory) = {
    val kernel = compileKernel(source)
    val memory = DeviceMemory(4096)
    assertEquals(memory.writeInt32(memory.start, initial), Right(()))
    val result = execute(
      kernel,
      blocks,
      threads,
      Map(("out": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory,
      TaskControl(limits),
      workgroups,
      cooperative = cooperative,
      schedule = schedule
    )
    (result, memory)
  }

  private def workgroupOrder(schedule: ConcurrencySchedule): Int =
    launch(workgroupOrderSource, 2, 1, schedule, initial = 1)._2.ints(0, 1).head

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
    val source = """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry grid_sync(.param .u64 out) {
      |  .reg .pred %p<4>;
      |  .reg .b32 %r<16>;
      |  .reg .b64 %rd<10>;
      |  ld.param.u64 %rd0, [out];
      |  mov.u32 %r0, %envreg2;
      |  mov.u32 %r1, %envreg1;
      |  cvt.u64.u32 %rd1, %r0;
      |  cvt.u64.u32 %rd2, %r1;
      |  shl.b64 %rd2, %rd2, 32;
      |  or.b64 %rd1, %rd1, %rd2;
      |  mov.u32 %r2, %ctaid.x;
      |  mov.u32 %r3, %ntid.x;
      |  mov.u32 %r4, %tid.x;
      |  mov.u32 %r5, %nctaid.x;
      |  mad.lo.u32 %r6, %r2, %r3, %r4;
      |  mul.lo.u32 %r7, %r5, %r3;
      |  mul.wide.u32 %rd3, %r6, 4;
      |  add.u64 %rd4, %rd0, %rd3;
      |  mov.u32 %r8, 1;
      |  st.global.u32 [%rd4], %r8;
      |  add.u64 %rd5, %rd1, 4;
      |  setp.ne.u32 %p1, %r4, 0;
      |  setp.eq.u32 %p2, %r2, 0;
      |  sub.u32 %r9, 0x80000001, %r5;
      |  selp.u32 %r10, %r9, 1, %p2;
      |  bar.sync 0;
      |  @%p1 bra AFTER;
      |  atom.add.release.gpu.u32 %r11, [%rd5], %r10;
      |SPIN:
      |  ld.acquire.gpu.u32 %r12, [%rd5];
      |  xor.b32 %r13, %r12, %r11;
      |  setp.gt.s32 %p3, %r13, -1;
      |  @%p3 bra SPIN;
      |AFTER:
      |  bar.sync 0;
      |  add.u32 %r14, %r6, 1;
      |  rem.u32 %r14, %r14, %r7;
      |  mul.wide.u32 %rd6, %r14, 4;
      |  add.u64 %rd7, %rd0, %rd6;
      |  ld.global.u32 %r15, [%rd7];
      |  ld.global.u32 %r8, [%rd4];
      |  add.u32 %r8, %r8, %r15;
      |  add.u64 %rd8, %rd4, 800;
      |  st.global.u32 [%rd8], %r8;
      |  ret;
      |}""".stripMargin
    for (seed <- 0L to 3L) {
      val (result, memory) = launch(source, 3, 8, ConcurrencySchedule.Random(seed), cooperative = true)
      assertEquals(result, Right(()))
      assertEquals(memory.words(800L, 24), Vec.fill(24)(2L))
    }
  }

  test("a workgroup spinning on a flag another sets completes under several seeds") {
    for (seed <- 0L to 3L) {
      val (result, memory) = launch(spinSource, 2, 1, ConcurrencySchedule.Random(seed))
      assertEquals(result, Right(()))
      assertEquals(memory.ints(4L, 1), Vec(1))
    }
  }

  test("ReplayDevice reproduces a seeded launch") {
    Using.resource(WorkerPool(1, minGroups = 1L, minWorkItems = 1L)) { pool =>
      val memory  = DeviceMemory(4096)
      val backend = PTXBackend(
        workgroups = pool,
        text = instruction => Some(Printer.render(instruction))
      )
      val task = KernelTask.validated(
        "wg_wait",
        Dimensions(1, 1, 1),
        Dimensions(2, 1, 1),
        Vec(KernelTask.Argument.Memory(AllocationId(1L), 0L))
      ).value
      val replay = ReplayDevice(
        BackendRegistry(Vec(backend)),
        memory,
        TaskControl.Limits(2000000L),
        ConcurrencySchedule.Random(7L)
      )
      val checkpoints = mutable.ArrayBuffer.empty[ReplayDevice.Checkpoint]
      val whole       = replay.launch(
        ProgramFormat.PTXText,
        Vec.from(spinSource.getBytes(StandardCharsets.UTF_8)),
        task,
        (checkpoint, _, _) => Right(checkpoints += checkpoint).map(_ => ())
      )
      assertEquals(whole.map(_.size), Right(0))
      val finished = memory.ints(4L, 1)
      assertEquals(finished, Vec(1))

      val resumed = replay.resume(checkpoints(10), (_, _, _) => Right(()))
      assertEquals(resumed.map(_.size), Right(0))
      assertEquals(memory.ints(4L, 1), finished)
    }
  }
}
