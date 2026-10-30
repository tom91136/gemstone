package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.Suite

/** Callee frames whose lanes have all returned hand their storage to later calls; these cover the ways a frame can
  * still be in use when some of its lanes return.
  */
class FrameReuseSuite extends Suite {
  private def run(source: String, threads: Int > 0, words: Int): Vec[Int] = {
    val memory = DeviceMemory(1024)
    val result = execute(
      compileKernel(source),
      1,
      threads,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assertEquals(result, Right(()))
    Vec.tabulate(words)(index => memory.readInt32(memory.start.offset(index.toLong * 4)).toOption.get)
  }

  private val header = """.version 8.0
    .target sm_80
    .address_size 64
    """

  // %stale is read before it is written, so storage reused from an earlier call must read as zero.
  private val work = """.func (.param .u32 result) work(.param .u32 input) {
      .reg .u32 %x, %stale, %i;
      .reg .pred %odd, %p;
      ld.param.u32 %x, [input];
      add.u32 %x, %x, %stale;
      and.b32 %i, %x, 1;
      setp.eq.u32 %odd, %i, 1;
      @%odd bra early;
      mov.u32 %i, 0;
    loop:
      add.u32 %x, %x, 2;
      add.u32 %i, %i, 1;
      setp.lt.u32 %p, %i, 5;
      @%p bra loop;
      mov.u32 %stale, 1000;
      st.param.u32 [result], %x;
      ret;
    early:
      mov.u32 %stale, 2000;
      st.param.u32 [result], %x;
      ret;
    }
    """

  private def callWork(input: String, output: String) =
    s"{ .param .u32 a; .param .u32 r; st.param.u32 [a], $input; call (r), work, (a); ld.param.u32 $output, [r]; }"

  private def expectedWork(input: Int) = if (input % 2 == 1) input else input + 10

  test("callee frames whose lanes exit leave no state for the next workgroup") {
    val source = header + """.func finish(.param .u64 slot) {
        .reg .u64 %slot;
        .reg .u32 %stale;
        ld.param.u64 %slot, [slot];
        st.global.u32 [%slot], %stale;
        mov.u32 %stale, 99;
        exit;
      }
      .visible .entry exiting(.param .u64 output) {
        .reg .u32 %tid, %block;
        .reg .u64 %out, %offset;
        ld.param.u64 %out, [output];
        mov.u32 %tid, %tid.x;
        mov.u32 %block, %ctaid.x;
        mad.lo.u32 %tid, %block, 32, %tid;
        mul.wide.u32 %offset, %tid, 4;
        add.u64 %out, %out, %offset;
        { .param .u64 a; st.param.u64 [a], %out; call finish, (a); }
        ret;
      }"""
    val memory = DeviceMemory(1024)
    (0 until 64).foreach(index => assertEquals(memory.writeInt32(memory.start.offset(index.toLong * 4), -1), Right(())))
    val result = execute(
      compileKernel(source),
      2,
      32,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assertEquals(result, Right(()))
    assertEquals(
      Vec.tabulate(64)(index => memory.readInt32(memory.start.offset(index.toLong * 4)).toOption.get),
      Vec.fill(64)(0)
    )
  }

  test("lanes returning from one callee at different times leave no state for the next call") {
    val source = header + work + s""".visible .entry divergent(.param .u64 output) {
        .reg .u32 %tid, %first, %second;
        .reg .u64 %out, %offset;
        ld.param.u64 %out, [output];
        mov.u32 %tid, %tid.x;
        ${callWork("%tid", "%first")}
        ${callWork("%tid", "%second")}
        mul.wide.u32 %offset, %tid, 8;
        add.u64 %out, %out, %offset;
        st.global.u32 [%out], %first;
        st.global.u32 [%out+4], %second;
        ret;
      }"""
    assertEquals(run(source, 64, 128), Vec.tabulate(128)(index => expectedWork(index / 2)))
  }

  test("recursion of different depths per lane reuses storage only of returned frames") {
    val source = header + """.func (.param .u32 result) sum(.param .u32 input) {
        .reg .u32 %n, %below, %stale;
        .reg .pred %done;
        ld.param.u32 %n, [input];
        add.u32 %n, %n, %stale;
        mov.u32 %stale, 77;
        setp.eq.u32 %done, %n, 0;
        @%done bra finish;
        sub.u32 %below, %n, 1;
        { .param .u32 a; .param .u32 r; st.param.u32 [a], %below; call (r), sum, (a); ld.param.u32 %below, [r]; }
        add.u32 %n, %n, %below;
      finish:
        st.param.u32 [result], %n;
        ret;
      }
      .visible .entry recursive(.param .u64 output) {
        .reg .u32 %tid, %depth, %first, %second;
        .reg .u64 %out, %offset;
        ld.param.u64 %out, [output];
        mov.u32 %tid, %tid.x;
        and.b32 %depth, %tid, 7;
        { .param .u32 a; .param .u32 r; st.param.u32 [a], %depth; call (r), sum, (a); ld.param.u32 %first, [r]; }
        { .param .u32 a; .param .u32 r; st.param.u32 [a], %depth; call (r), sum, (a); ld.param.u32 %second, [r]; }
        mul.wide.u32 %offset, %tid, 8;
        add.u64 %out, %out, %offset;
        st.global.u32 [%out], %first;
        st.global.u32 [%out+4], %second;
        ret;
      }"""
    assertEquals(
      run(source, 32, 64),
      Vec.tabulate(64) { index =>
        val depth = (index / 2) % 8
        depth * (depth + 1) / 2
      }
    )
  }

  test("a barrier inside a callee proceeds after another warp has already returned") {
    val source = header + """.func (.param .u32 result) pair(.param .u32 input) {
        .shared .align 4 .u32 slots[32];
        .reg .u32 %tid, %local, %other, %value, %stale;
        .reg .u64 %slot, %offset;
        .reg .pred %first;
        ld.param.u32 %value, [input];
        add.u32 %value, %value, %stale;
        mov.u32 %stale, 5;
        mov.u32 %tid, %tid.x;
        setp.lt.u32 %first, %tid, 32;
        @%first bra done;
        sub.u32 %local, %tid, 32;
        mul.wide.u32 %offset, %local, 4;
        mov.u64 %slot, slots;
        add.u64 %slot, %slot, %offset;
        st.shared.u32 [%slot], %tid;
        bar.sync 1, 32;
        xor.b32 %other, %local, 1;
        mul.wide.u32 %offset, %other, 4;
        mov.u64 %slot, slots;
        add.u64 %slot, %slot, %offset;
        ld.shared.u32 %value, [%slot];
        bar.sync 1, 32;
      done:
        st.param.u32 [result], %value;
        ret;
      }
      .visible .entry barrier(.param .u64 output) {
        .reg .u32 %tid, %first, %second;
        .reg .u64 %out, %offset;
        ld.param.u64 %out, [output];
        mov.u32 %tid, %tid.x;
        { .param .u32 a; .param .u32 r; st.param.u32 [a], 7; call (r), pair, (a); ld.param.u32 %first, [r]; }
        { .param .u32 a; .param .u32 r; st.param.u32 [a], 7; call (r), pair, (a); ld.param.u32 %second, [r]; }
        mul.wide.u32 %offset, %tid, 8;
        add.u64 %out, %out, %offset;
        st.global.u32 [%out], %first;
        st.global.u32 [%out+4], %second;
        ret;
      }"""
    assertEquals(run(source, 64, 128), Vec.tabulate(128)(index => if (index / 2 < 32) 7 else (index / 2) ^ 1))
  }

  test("warp-synchronous shuffles and votes inside callees see only the current call's values") {
    val source = header +
      """.func (.param .u32 result) collective(.param .u32 input) {
        .reg .u32 %x, %swapped, %ballot, %stale;
        .reg .pred %odd;
        ld.param.u32 %x, [input];
        add.u32 %x, %x, %stale;
        mov.u32 %stale, 9;
        shfl.sync.bfly.b32 %swapped, %x, 1, 31, -1;
        and.b32 %ballot, %x, 1;
        setp.eq.u32 %odd, %ballot, 1;
        vote.sync.ballot.b32 %ballot, %odd, -1;
        popc.b32 %ballot, %ballot;
        add.u32 %x, %swapped, %ballot;
        st.param.u32 [result], %x;
        ret;
      }
      .visible .entry collectives(.param .u64 output) {
        .reg .u32 %tid, %first, %second;
        .reg .u64 %out, %offset;
        ld.param.u64 %out, [output];
        mov.u32 %tid, %tid.x;
        { .param .u32 a; .param .u32 r; st.param.u32 [a], %tid; call (r), collective, (a); ld.param.u32 %first, [r]; }
        { .param .u32 a; .param .u32 r; st.param.u32 [a], %first; call (r), collective, (a); ld.param.u32 %second, [r]; }
        mul.wide.u32 %offset, %tid, 8;
        add.u64 %out, %out, %offset;
        st.global.u32 [%out], %first;
        st.global.u32 [%out+4], %second;
        ret;
      }"""
    val first  = Vec.tabulate(64)(lane => (lane ^ 1) + 16)
    val second = Vec.tabulate(64) { lane =>
      val warp = lane / 32 * 32
      first(lane ^ 1) + (warp until warp + 32).count(first(_) % 2 == 1)
    }
    assertEquals(
      run(source, 64, 128),
      Vec.tabulate(128)(index => if (index % 2 == 0) first(index / 2) else second(index / 2))
    )
  }

  test("device-launched children and their parent each reuse only their own callee storage") {
    val source = header + work +
      s""".extern .func (.param .b64 buffer) cudaGetParameterBuffer(.param .b64 align, .param .b64 size);
      .extern .func cudaLaunchDevice();
      .visible .entry parent(.param .u64 output) {
        .reg .u32 %tid, %first, %second;
        .reg .u64 %out, %offset, %function, %buffer;
        .reg .pred %leader;
        .param .b64 align, size, result, target, pointer, stream;
        .param .b32 shared;
        .param .align 4 .b8 grid[12], block[12];
        ld.param.u64 %out, [output];
        mov.u32 %tid, %tid.x;
        ${callWork("%tid", "%first")}
        setp.ne.u32 %leader, %tid, 0;
        @%leader bra after;
        st.param.b64 [align], 8;
        st.param.b64 [size], 8;
        call (result), cudaGetParameterBuffer, (align, size);
        ld.param.b64 %buffer, [result];
        st.u64 [%buffer], %out;
        mov.b64 %function, child;
        st.param.b64 [target], %function;
        st.param.b64 [pointer], %buffer;
        st.param.b64 [stream], 0;
        st.param.b32 [shared], 0;
        st.param.b32 [grid], 1;
        st.param.b32 [grid+4], 1;
        st.param.b32 [grid+8], 1;
        st.param.b32 [block], 32;
        st.param.b32 [block+4], 1;
        st.param.b32 [block+8], 1;
        call cudaLaunchDevice, (target, pointer, grid, block, shared, stream);
      after:
        ${callWork("%first", "%second")}
        mul.wide.u32 %offset, %tid, 8;
        add.u64 %out, %out, %offset;
        st.global.u32 [%out], %first;
        st.global.u32 [%out+4], %second;
        ret;
      }
      .visible .entry child(.param .u64 out) {
        .reg .u32 %tid, %value;
        .reg .u64 %out, %offset;
        ld.param.u64 %out, [out];
        mov.u32 %tid, %tid.x;
        add.u32 %tid, %tid, 100;
        ${callWork("%tid", "%value")}
        ${callWork("%value", "%value")}
        sub.u32 %tid, %tid, 100;
        mul.wide.u32 %offset, %tid, 4;
        add.u64 %out, %out, %offset;
        st.global.u32 [%out+256], %value;
        ret;
      }"""
    val words = run(source, 32, 96)
    assertEquals(
      words.take(64),
      Vec.tabulate(64)(index => if (index % 2 == 0) expectedWork(index / 2) else expectedWork(expectedWork(index / 2)))
    )
    assertEquals(words.drop(64), Vec.tabulate(32)(lane => expectedWork(expectedWork(lane + 100))))
  }
}
