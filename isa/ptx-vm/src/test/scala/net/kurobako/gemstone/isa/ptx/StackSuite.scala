package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.testing.Suite

/** `alloca`, `stacksave` and `stackrestore` as nvcc 13.3 (`nvcc -O3 -arch=sm_89 -ptx`) emits them for `alloca` in
  * device code and around a `__forceinline__` callee that allocates, and clang 22 emits them for `__builtin_alloca`:
  * the pointer is a local address, used directly or through cvta.local.
  */
class StackSuite extends Suite {
  private def run(body: String, threads: Int, outputs: Int, functions: String = "")(using
      munit.Location
  ): Either[String, Vec[Long]] = {
    val source = s""".version 8.7
      |.target sm_89
      |.address_size 64
      |$functions
      |.visible .entry stack(.param .u64 output) {
      |  .reg .pred %p<4>;
      |  .reg .b32 %r<16>;
      |  .reg .b64 %rd<16>;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 %r0, %tid.x;
      |  $body
      |  ret;
      |}""".stripMargin
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message.raw), identity)
    val memory = DeviceMemory((threads * outputs * 8).unsafe[Int >= 0])
    execute(
      kernel,
      1,
      threads.unsafe[Int > 0],
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    ).left.map(_.message.raw).map(_ =>
      Vec.tabulate(threads * outputs)(index =>
        memory.readLittleEndian(memory.start.offset(index.toLong * 8), 8).toOption.get
      )
    )
  }

  test("alloca gives each lane its own aligned, disjoint allocations, through local and generic addresses") {
    val result = run(
      """mul.wide.u32 %rd1, %r0, 24;
        |add.u64 %rd1, %rd1, 8;
        |alloca.u64 %rd2, %rd1, 16;
        |alloca.u64 %rd3, 8;
        |cvta.local.u64 %rd4, %rd2;
        |st.local.u32 [%rd2], %r0;
        |st.local.u64 [%rd3], -1;
        |add.u32 %r1, %r0, 100;
        |st.u32 [%rd4+4], %r1;
        |ld.local.u32 %r2, [%rd2+4];
        |ld.u32 %r3, [%rd4];
        |sub.u64 %rd5, %rd2, %rd3;
        |and.b64 %rd6, %rd4, 15;
        |mul.wide.u32 %rd7, %r0, 32;
        |add.u64 %rd7, %rd0, %rd7;
        |cvt.u64.u32 %rd8, %r2;
        |st.global.u64 [%rd7], %rd8;
        |cvt.u64.u32 %rd8, %r3;
        |st.global.u64 [%rd7+8], %rd8;
        |st.global.u64 [%rd7+16], %rd5;
        |st.global.u64 [%rd7+24], %rd6;""".stripMargin,
      threads = 4,
      outputs = 4
    )
    assertEquals(
      result,
      Right(Vec.tabulate(4)(lane => Vec(lane + 100L, lane.toLong, 8L, 0L)).flatten)
    )
  }

  test("a callee's allocations leave the caller's intact and are released when it returns") {
    val result = run(
      """alloca.u64 %rd1, 64, 8;
        |st.local.u32 [%rd1], 7;
        |{ .param .b32 param0; st.param.b32 [param0], %r0; .param .b64 retval0;
        |call.uni (retval0), fill, (param0); ld.param.b64 %rd2, [retval0]; }
        |{ .param .b32 param0; st.param.b32 [param0], %r0; .param .b64 retval0;
        |call.uni (retval0), fill, (param0); ld.param.b64 %rd3, [retval0]; }
        |ld.local.u32 %r1, [%rd1];
        |stacksave.u64 %rd4;
        |sub.u64 %rd5, %rd4, %rd1;
        |mul.wide.u32 %rd6, %r0, 24;
        |add.u64 %rd6, %rd0, %rd6;
        |cvt.u64.u32 %rd7, %r1;
        |st.global.u64 [%rd6], %rd7;
        |sub.u64 %rd8, %rd2, %rd3;
        |st.global.u64 [%rd6+8], %rd8;
        |st.global.u64 [%rd6+16], %rd5;""".stripMargin,
      threads = 2,
      outputs = 3,
      """.func (.param .b64 sum) fill(.param .b32 value) {
        |  .reg .b32 %v;
        |  .reg .b64 %s;
        |  ld.param.u32 %v, [value];
        |  alloca.u64 %s, 4096, 16;
        |  st.local.u32 [%s], %v;
        |  st.local.u32 [%s+4092], %v;
        |  cvta.local.u64 %s, %s;
        |  st.param.b64 [sum], %s;
        |  ret;
        |}""".stripMargin
    )
    assertEquals(result, Right(Vec(7L, 0L, 0L, 7L, 0L, 0L)))
  }

  test("stackrestore releases what alloca took since stacksave, so a loop runs in bounded stack") {
    val result = run(
      """mov.u32 %r1, 0;
        |mov.u32 %r2, 0;
        |LOOP:
        |stacksave.u32 %r3;
        |alloca.u64 %rd1, 65536, 16;
        |st.local.u32 [%rd1+65532], %r1;
        |ld.local.u32 %r4, [%rd1+65532];
        |add.u32 %r2, %r2, %r4;
        |stackrestore.u32 %r3;
        |add.u32 %r1, %r1, 1;
        |setp.lt.u32 %p1, %r1, 64;
        |@%p1 bra LOOP;
        |cvt.u64.u32 %rd2, %r2;
        |st.global.u64 [%rd0], %rd2;""".stripMargin,
      threads = 1,
      outputs = 1
    )
    assertEquals(result, Right(Vec(64L * 63L / 2L)))
  }

  test("alloca beyond the frame's stack faults rather than overrunning static locals") {
    val result = run("alloca.u64 %rd1, 1048576; st.local.u32 [%rd1], 1;", threads = 1, outputs = 1)
    assert(result.left.exists(_.contains("stack")), result.toString)
  }
}
