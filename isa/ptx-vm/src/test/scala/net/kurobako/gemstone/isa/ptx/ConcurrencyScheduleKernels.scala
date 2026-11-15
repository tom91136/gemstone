package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

trait ConcurrencyScheduleKernels { self: Suite =>

  val workgroupOrderSource: String =
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

  val spinSource: String =
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

  def launch(
      source: String,
      blocks: Int > 0,
      threads: Int > 0,
      schedule: ConcurrencySchedule,
      cooperative: Boolean = false,
      workgroups: Workgroups = Workgroups.Sequential,
      limits: TaskControl.Limits = TaskControl.Limits.Default,
      cluster: Option[Dimensions] = None,
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
      cluster = cluster,
      schedule = schedule
    )
    (result, memory)
  }

  def workgroupOrder(schedule: ConcurrencySchedule): Int =
    launch(workgroupOrderSource, 2, 1, schedule, initial = 1)._2.ints(0, 1).head
}
