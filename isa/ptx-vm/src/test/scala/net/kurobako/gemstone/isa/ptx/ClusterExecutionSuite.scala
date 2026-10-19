package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.memory.*

class ClusterExecutionSuite extends munit.FunSuite {
  test("bulk stores repeat the byte mask in each 16-byte chunk and preserve other bytes") {
    val kernel = compileKernel(""".version 8.6
      |.target sm_100
      |.address_size 64
      |.visible .entry main(.param .u64 output) {
      | .shared .align 16 .b8 data[32];
      | .reg .u64 %out;
      | st.shared.u64 [data], 0x0706050403020100;
      | st.shared.u64 [data+8], 0x0f0e0d0c0b0a0908;
      | st.shared.u64 [data+16], 0x1716151413121110;
      | st.shared.u64 [data+24], 0x1f1e1d1c1b1a1918;
      | fence.proxy.async.shared::cta;
      | ld.param.u64 %out, [output];
      | cp.async.bulk.global.shared::cta.bulk_group.cp_mask [%out], [data], 32, 0x8001;
      | add.u64 %out, %out, 32;
      | cp.async.bulk.global.shared::cta.bulk_group [%out], [data], 32;
      | cp.async.bulk.commit_group;
      | cp.async.bulk.wait_group.read 0;
      | ret;
      |}""".stripMargin)
    val memory = DeviceMemory(64)
    for (offset <- 0 until 64)
      assertEquals(memory.writeInt8(memory.start.offset(offset.toLong), 127), Right(()))
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) ->
          Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    for (offset <- 0 until 64) {
      val expected = if (offset >= 32 || (offset & 15) == 0 || (offset & 15) == 15) (offset & 31).toByte else 127.toByte
      assertEquals(memory.readInt8(memory.start.offset(offset.toLong)), Right(expected))
    }
  }

  test("single-CTA cluster arrival is nonblocking across warps and phases are reusable") {
    val kernel = compileKernel(""".version 8.6
      |.target sm_100
      |.address_size 64
      |.visible .entry main(.param .u64 output) {
      | .shared .u32 signal;
      | .reg .pred %first;
      | .reg .u32 %lane, %value;
      | .reg .u64 %out, %offset;
      | mov.u32 %lane, %tid.x;
      | setp.lt.u32 %first, %lane, 32;
      | @!%first bra second;
      | barrier.cluster.arrive.relaxed;
      | st.shared.u32 [signal], 42;
      | bra join;
      |second:
      | ld.shared.u32 %value, [signal];
      | setp.ne.u32 %first, %value, 42;
      | @%first bra second;
      | barrier.cluster.arrive;
      |join:
      | barrier.cluster.wait;
      | barrier.cluster.arrive.release;
      | barrier.cluster.wait.acquire;
      | ld.shared.u32 %value, [signal];
      | ld.param.u64 %out, [output];
      | mul.wide.u32 %offset, %lane, 4;
      | add.u64 %out, %out, %offset;
      | st.global.u32 [%out], %value;
      | ret;
      |}""".stripMargin)
    val memory = DeviceMemory(64 * 4)
    assertEquals(
      execute(
        kernel,
        1,
        64,
        Map(("output": NonEmpty[String]) ->
          Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    for (lane <- 0 until 64)
      assertEquals(memory.readInt32(memory.start.offset(lane.toLong * 4)), Right(42))
  }

  test("failed cluster cancellation writes its response and completes the expected transaction") {
    val kernel = compileKernel(""".version 8.6
      |.target sm_100
      |.address_size 64
      |.visible .entry main(.param .u64 output) {
      | .shared .align 16 .b8 response[16];
      | .shared .align 8 .b64 barrier;
      | .reg .b128 %response;
      | .reg .u64 %low, %high, %token, %out;
      | .reg .u32 %result;
      | .reg .pred %done, %cancelled;
      | st.shared.u64 [response], 0xffffffffffffffff;
      | st.shared.u64 [response+8], 0xffffffffffffffff;
      | mbarrier.init.shared.b64 [barrier], 1;
      | clusterlaunchcontrol.try_cancel.async.shared::cta.mbarrier::complete_tx::bytes.b128 [response], [barrier];
      | mbarrier.arrive.expect_tx.release.cta.shared::cta.b64 %token, [barrier], 16;
      | mbarrier.try_wait.parity.shared::cta.b64 %done, [barrier], 0;
      | @!%done trap;
      | ld.shared.v2.b64 {%low, %high}, [response];
      | mov.b128 %response, {%low, %high};
      | clusterlaunchcontrol.query_cancel.is_canceled.pred.b128 %cancelled, %response;
      | @%cancelled clusterlaunchcontrol.query_cancel.get_first_ctaid::x.b32.b128 %result, %response;
      | @%cancelled clusterlaunchcontrol.query_cancel.get_first_ctaid::y.b32.b128 %result, %response;
      | @%cancelled clusterlaunchcontrol.query_cancel.get_first_ctaid::z.b32.b128 %result, %response;
      | selp.u32 %result, 1, 0, %cancelled;
      | ld.param.u64 %out, [output];
      | st.global.u32 [%out], %result;
      | ret;
      |}""".stripMargin)
    val memory = DeviceMemory(4)
    assertEquals(memory.writeInt32(memory.start, -1), Right(()))
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) ->
          Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.readInt32(memory.start), Right(0))
  }

  // ptxas 13.3 lowers the multicast form to UGETNEXTWORKID.BROADCAST on sm_100a
  test("multicast cluster cancellation writes every CTA's response and completes each CTA's mbarrier") {
    val kernel = compileKernel(AsyncCases.cancelPtx)
    val memory = DeviceMemory(2 * 32 * 4)
    assertEquals(
      execute(kernel, 2, 32, Map(("out": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)), memory),
      Right(())
    )
    for (word <- 0 until 64) assertEquals(memory.readInt32(memory.start.offset(word * 4L)), Right(2), word)
  }
}
