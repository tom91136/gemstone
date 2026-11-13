package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

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
}
