package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.isa.nvidia.*

object PTXControlFlowFixture {
  private val source = """.version 8.0
    .target sm_80
    .address_size 64
    .visible .entry control(.param .u64 output, .param .u32 rounds) {
      .reg .u64 %rd<3>;
      .reg .u32 %r<8>;
      .reg .pred %p<3>;
      ld.param.u64 %rd0, [output];
      add.u64 %rd0, %rd0, 4;
      ld.param.u32 %r7, [rounds];
      mov.u32 %r0, %tid.x;
      mov.u32 %r1, %ctaid.x;
      mov.u32 %r2, %ntid.x;
      mad.lo.u32 %r3, %r1, %r2, %r0;
      mul.wide.u32 %rd1, %r3, 4;
      add.u64 %rd2, %rd0, %rd1;
      mov.u32 %r6, %r3;
      and.b32 %r4, %r0, 1;
      mov.u32 %r5, 0;
    loop:
      setp.eq.u32 %p0, %r4, 0;
      @%p0 bra even;
      add.u32 %r6, %r6, 2;
      bra joined;
    even:
      add.u32 %r6, %r6, 1;
    joined:
      setp.ne.u32 %p1, %r5, 0;
      @%p1 add.u32 %r6, %r6, 1;
      st.global.u32 [%rd2], %r6;
      bar.sync 0;
      add.u32 %r5, %r5, 1;
      setp.lt.u32 %p2, %r5, %r7;
      @%p2 bra loop;
      ret;
    }"""

  def compile(): VMIR.Kernel =
    VMCompiler.compile(source).fold(error => throw IllegalArgumentException(error.message), identity)
}
