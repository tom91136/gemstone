package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.Suite

class CallBarrierSuite extends Suite {
  test("an arrival survives returning while another call waits for the same CTA barrier") {
    val kernel = compileKernel(""".version 8.0
      |.target sm_80
      |.address_size 64
      |.func produce(.param .u64 buffer) {
      | .reg .u32 %r0;
      | .reg .u64 %out, %offset;
      | ld.param.u64 %out, [buffer];
      | mov.u32 %r0, %tid.x;
      | mul.wide.u32 %offset, %r0, 4;
      | add.u64 %out, %out, %offset;
      | add.u32 %r0, %r0, 100;
      | st.global.u32 [%out+4], %r0;
      | st.local.u32 [8], 99;
      | bar.arrive 0, 64;
      | ret;
      |}
      |.func wait_for_producer() {
      | bar.sync 0, 64;
      | ret;
      |}
      |.func (.param .u32 answer) consume(.param .u64 buffer) {
      | .reg .u32 %r0, %flag;
      | .reg .u64 %out, %offset;
      | .reg .pred %pending;
      | ld.param.u64 %out, [buffer];
      |poll:
      | ld.volatile.global.u32 %flag, [%out];
      | setp.eq.u32 %pending, %flag, 0;
      | @%pending bra poll;
      | call wait_for_producer;
      | mov.u32 %r0, %tid.x;
      | sub.u32 %r0, %r0, 32;
      | mul.wide.u32 %offset, %r0, 4;
      | add.u64 %out, %out, %offset;
      | ld.global.u32 %r0, [%out+4];
      | st.param.u32 [answer], %r0;
      | ret;
      |}
      |.visible .entry cross_call(.param .u64 output) {
      | .reg .u32 %lane, %r0, %result;
      | .reg .u64 %out, %offset;
      | .reg .pred %producer, %leader;
      | .param .u64 argument;
      | .param .u32 returned;
      | ld.param.u64 %out, [output];
      | st.param.u64 [argument], %out;
      | st.local.u32 [8], 17;
      | mov.u32 %r0, 888;
      | mov.u32 %lane, %tid.x;
      | setp.lt.u32 %producer, %lane, 32;
      | @%producer bra produce_path;
      | call (returned), consume, (argument);
      | ld.param.u32 %result, [returned];
      | bra join;
      |produce_path:
      | call produce, (argument);
      | setp.eq.u32 %leader, %lane, 0;
      | @%leader st.volatile.global.u32 [%out], 1;
      | ld.local.u32 %result, [8];
      | add.u32 %result, %result, %r0;
      |join:
      | bar.sync 1;
      | mul.wide.u32 %offset, %lane, 4;
      | add.u64 %out, %out, %offset;
      | st.global.u32 [%out+132], %result;
      | ret;
      |}""".stripMargin)
    val memory = DeviceMemory(132 + 64 * 4)
    assertEquals(
      execute(
        kernel,
        1,
        64,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(
      Vec.tabulate(64)(lane => memory.readInt32(memory.start.offset(132 + lane.toLong * 4)).toOption.get),
      Vec.tabulate(64)(lane => if (lane < 32) 905 else lane - 32 + 100)
    )
  }

  test("a counted reduction synchronises separate function frames and returns their results") {
    val kernel = compileKernel(""".version 8.0
      |.target sm_80
      |.address_size 64
      |.func (.param .u32 answer) first() {
      | .reg .pred %p;
      | .reg .u32 %value;
      | setp.eq.u32 %p, 0, 1;
      | bar.red.or.pred %p, 0, 64, %p;
      | selp.u32 %value, 1, 0, %p;
      | st.param.u32 [answer], %value;
      | ret;
      |}
      |.func (.param .u32 answer) second() {
      | .reg .pred %q;
      | .reg .u32 %lane, %value;
      | mov.u32 %lane, %tid.x;
      | setp.eq.u32 %q, %lane, 37;
      | bar.red.or.pred %q, 0, 64, %q;
      | selp.u32 %value, 1, 0, %q;
      | st.param.u32 [answer], %value;
      | ret;
      |}
      |.visible .entry reduction(.param .u64 output) {
      | .reg .u32 %lane, %value;
      | .reg .u64 %out, %offset;
      | .reg .pred %first;
      | .param .u32 returned;
      | mov.u32 %lane, %tid.x;
      | setp.lt.u32 %first, %lane, 32;
      | @%first bra first_path;
      | call (returned), second;
      | bra join;
      |first_path:
      | call (returned), first;
      |join:
      | ld.param.u32 %value, [returned];
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
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(
      Vec.tabulate(64)(lane => memory.readInt32(memory.start.offset(lane.toLong * 4)).toOption.get),
      Vec.fill(64)(1)
    )
  }

  test("exit terminates lanes through nested calls and releases the remaining CTA") {
    val kernel = compileKernel(""".version 8.0
      |.target sm_80
      |.address_size 64
      |.func terminate() { exit; }
      |.func nested() { call terminate; ret; }
      |.visible .entry exiting(.param .u64 output) {
      | .reg .u32 %lane;
      | .reg .u64 %out, %offset;
      | .reg .pred %first;
      | mov.u32 %lane, %tid.x;
      | setp.lt.u32 %first, %lane, 32;
      | @!%first bra join;
      | call nested;
      |join:
      | bar.sync 0;
      | ld.param.u64 %out, [output];
      | mul.wide.u32 %offset, %lane, 4;
      | add.u64 %out, %out, %offset;
      | st.global.u32 [%out], 1;
      | ret;
      |}""".stripMargin)
    val memory = DeviceMemory(64 * 4)
    assertEquals(
      execute(
        kernel,
        1,
        64,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(
      Vec.tabulate(64)(lane => memory.readInt32(memory.start.offset(lane.toLong * 4)).toOption.get),
      Vec.tabulate(64)(lane => if (lane < 32) 0 else 1)
    )
  }
}
