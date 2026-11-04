package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class CooperativeLaunchSuite extends Suite {
  private val blocks: Int > 0  = 5
  private val threads: Int > 0 = 40
  private val bytes: Int >= 0  = 1600

  private def gridSync(syncs: Int): String = {
    val arrive = (0 until syncs).map(index => s"""  bar.sync 0;
      |  @%p1 bra $$AFTER$index;
      |  atom.add.release.gpu.u32 %r11, [%rd5], %r10;
      |$$SPIN$index:
      |  ld.acquire.gpu.u32 %r12, [%rd5];
      |  xor.b32 %r13, %r12, %r11;
      |  setp.gt.s32 %p3, %r13, -1;
      |  @%p3 bra $$SPIN$index;
      |$$AFTER$index:
      |  bar.sync 0;""".stripMargin).mkString("\n")
    s""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry grid_sync(.param .u64 output) {
      |  .reg .pred %p<4>;
      |  .reg .b32 %r<16>;
      |  .reg .b64 %rd<10>;
      |  ld.param.u64 %rd0, [output];
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
      |  setp.eq.u64 %p0, %rd1, 0;
      |  @%p0 trap;
      |  add.u64 %rd5, %rd1, 4;
      |  setp.ne.u32 %p1, %r4, 0;
      |  setp.eq.u32 %p2, %r2, 0;
      |  sub.u32 %r9, 0x80000001, %r5;
      |  selp.u32 %r10, %r9, 1, %p2;
      |$arrive
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
      |}
      |""".stripMargin
  }

  private def launch(syncs: Int, cooperative: Boolean): (Either[Fault, Unit], Vec[Int]) = {
    val kernel = compileKernel(gridSync(syncs))
    val memory = DeviceMemory(bytes, GuestAddress(0x10000L))
    val result = execute(
      kernel,
      blocks,
      threads,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory,
      cooperative = cooperative
    )
    (
      result,
      Vec.tabulate(blocks * threads)(index => memory.readInt32(memory.start.offset(800L + index * 4L)).toOption.get)
    )
  }

  private def probeWorkspace(offset: Int >= 0): (Either[Fault, Unit], Vec[Int]) = {
    val kernel = compileKernel(s""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry workspace_probe(.param .u64 output) {
      |  .reg .b32 %r<4>;
      |  .reg .b64 %rd<8>;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 %r0, %envreg2;
      |  mov.u32 %r1, %envreg1;
      |  cvt.u64.u32 %rd1, %r0;
      |  cvt.u64.u32 %rd2, %r1;
      |  shl.b64 %rd2, %rd2, 32;
      |  or.b64 %rd1, %rd1, %rd2;
      |  add.u64 %rd1, %rd1, $offset;
      |  mov.u32 %r2, %tid.x;
      |  mul.wide.u32 %rd3, %r2, 4;
      |  add.u64 %rd4, %rd1, %rd3;
      |  ld.global.u32 %r3, [%rd4];
      |  add.u64 %rd5, %rd0, %rd3;
      |  st.global.u32 [%rd5], %r3;
      |  ret;
      |}
      |""".stripMargin)
    val memory = DeviceMemory(256, GuestAddress(0x10000L))
    val result = execute(
      kernel,
      1,
      64,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory,
      cooperative = true
    )
    (result, Vec.tabulate(64)(index => memory.readInt32(memory.start.offset(index.toLong * 4L)).toOption.get))
  }

  for (syncs <- Vec(1, 3))
    test(s"a grid barrier of a cooperative launch waits for every resident workgroup ($syncs barriers)") {
      val (result, values) = launch(syncs, cooperative = true)
      assertEquals(result, Right(()))
      assertEquals(values, Vec.fill(blocks * threads)(2))
    }

  test("the grid workspace is absent from a launch that is not cooperative") {
    assert(launch(1, cooperative = false)._1.isLeft)
  }

  test("the grid workspace is 256 zeroed bytes of a cooperative launch") {
    val (result, words) = probeWorkspace(0)
    assertEquals(result, Right(()))
    assertEquals(words, Vec.fill(64)(0))
  }

  test("the grid workspace ends at 256 bytes") {
    assert(probeWorkspace(256)._1.left.exists(_.message.contains("outside every allocation")))
  }
}
