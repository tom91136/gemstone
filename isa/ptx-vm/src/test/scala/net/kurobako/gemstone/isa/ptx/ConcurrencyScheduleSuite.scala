package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ConcurrencyScheduleSuite extends Suite with ConcurrencyScheduleKernels {

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

  test("a seeded launch keeps explicit 1x1x1 cluster addressing") {
    val source = """.version 8.0
      |.target sm_90
      |.address_size 64
      |.visible .entry explicit(.param .u64 out) {
      |  .reg .pred %p;
      |  .reg .b32 %r;
      |  .reg .b64 %rd;
      |  ld.param.u64 %rd, [out];
      |  mov.pred %p, %is_explicit_cluster;
      |  selp.u32 %r, 1, 0, %p;
      |  st.global.u32 [%rd], %r;
      |  ret;
      |}""".stripMargin
    val (result, memory) = launch(source, 1, 1, ConcurrencySchedule.Random(7L), cluster = Some(Dimensions.One))
    assertEquals(result, Right(()))
    assertEquals(memory.ints(0L, 1), Vec(1))
  }

}
