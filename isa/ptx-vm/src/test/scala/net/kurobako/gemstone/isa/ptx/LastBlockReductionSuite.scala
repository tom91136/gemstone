package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*

class LastBlockReductionSuite extends Suite {
  private val source = """.version 8.0
    |.target sm_80
    |.address_size 64
    |.visible .entry last_block(.param .u64 output, .param .u64 counter, .param .u64 partials) {
    | .reg .pred %p, %last, %reduced;
    | .reg .b32 %tid, %block, %blocks, %value, %old, %sum, %index;
    | .reg .b64 %out, %cnt, %part, %address, %offset;
    | ld.param.u64 %out, [output];
    | ld.param.u64 %cnt, [counter];
    | ld.param.u64 %part, [partials];
    | mov.u32 %tid, %tid.x;
    | mov.u32 %block, %ctaid.x;
    | mov.u32 %blocks, %nctaid.x;
    | setp.ne.u32 %p, %tid, 0;
    | @%p bra publish;
    | add.u32 %value, %block, 1;
    | mul.wide.u32 %offset, %block, 4;
    | add.u64 %address, %part, %offset;
    | st.global.b32 [%address], %value;
    | membar.gl;
    |publish:
    | bar.sync 0;
    | mov.pred %last, 0;
    | @%p bra reduce;
    | mov.b32 %value, 1;
    | atom.add.global.relaxed.gpu.u32 %old, [%cnt], %value;
    | add.s32 %old, %old, 1;
    | setp.eq.b32 %last, %old, %blocks;
    |reduce:
    | bar.red.or.pred %reduced, 0, %last;
    | not.pred %last, %reduced;
    | @%last bra done;
    | mov.b32 %sum, 0;
    | mov.b32 %index, 0;
    |sum:
    | mul.wide.u32 %offset, %index, 4;
    | add.u64 %address, %part, %offset;
    | ld.global.b32 %value, [%address];
    | add.s32 %sum, %sum, %value;
    | add.s32 %index, %index, 1;
    | setp.lt.u32 %last, %index, %blocks;
    | @%last bra sum;
    | st.global.b32 [%out], %sum;
    | mov.b32 %value, 0;
    | st.global.b32 [%cnt], %value;
    |done:
    | ret;
    |}
    |""".stripMargin

  for (blocks <- Vec[Int > 0](1, 4)) test(s"last block of $blocks blocks sums the published partials") {
    val kernel   = compileKernel(source)
    val memory   = DeviceMemory(64, GuestAddress(0x10000L))
    val start    = memory.start
    val partials = start.offset(16L)
    assertEquals(
      execution.execute(
        kernel,
        blocks,
        32,
        Map(
          ("output": NonEmpty[String])   -> Argument.fromLong(start.raw),
          ("counter": NonEmpty[String])  -> Argument.fromLong(start.offset(8L).raw),
          ("partials": NonEmpty[String]) -> Argument.fromLong(partials.raw)
        ),
        memory
      ),
      Right(())
    )
    val expected: Int = blocks * (blocks + 1) / 2
    assertEquals(memory.readInt32(start).toOption.get, expected)
    assertEquals(memory.readInt32(start.offset(8L)).toOption.get, 0)
  }
}
