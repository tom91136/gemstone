package net.kurobako.gemstone.isa.sass.fixtures

object GenericMemoryFixtures {
  val genericAddressPtx: String =
    """.version 6.4
      |.target sm_50
      |.address_size 64
      |
      |// One generic-address operation per entry. The auxiliary LEA predicate is
      |// consumed by that same LD.E/ST.E and is intentionally not made observable.
      |.visible .entry global_load(.param .u64 p, .param .u64 out)
      |{
      | .reg .b64 %rd<4>; .reg .b32 %r<2>;
      | ld.param.u64 %rd1,[p]; ld.param.u64 %rd2,[out];
      | add.u64 %rd3,%rd1,4;
      | ld.u32 %r1,[%rd3];
      | st.global.u32 [%rd2],%r1;
      | ret;
      |}
      |.visible .entry global_store(.param .u64 p, .param .u32 value)
      |{
      | .reg .b64 %rd<3>; .reg .b32 %r<2>;
      | ld.param.u64 %rd1,[p]; ld.param.u32 %r1,[value];
      | add.u64 %rd2,%rd1,4;
      | st.u32 [%rd2],%r1;
      | ret;
      |}
      |
      |.visible .entry shared_load(.param .u64 out, .param .u32 value)
      |{
      | .shared .align 4 .b8 sh[16];
      | .reg .b64 %rd<4>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[out]; ld.param.u32 %r1,[value];
      | st.shared.u32 [sh+4],%r1;
      | cvta.shared.u64 %rd2,sh;
      | add.u64 %rd3,%rd2,4;
      | ld.u32 %r2,[%rd3];
      | st.global.u32 [%rd1],%r2;
      | ret;
      |}
      |.visible .entry shared_store(.param .u64 out, .param .u32 value)
      |{
      | .shared .align 4 .b8 sh[16];
      | .reg .b64 %rd<4>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[out]; ld.param.u32 %r1,[value];
      | cvta.shared.u64 %rd2,sh;
      | add.u64 %rd3,%rd2,4;
      | st.u32 [%rd3],%r1;
      | ld.shared.u32 %r2,[sh+4];
      | st.global.u32 [%rd1],%r2;
      | ret;
      |}
      |
      |.visible .entry local_load(.param .u64 out, .param .u32 value)
      |{
      | .local .align 4 .b8 loc[16];
      | .reg .b64 %rd<4>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[out]; ld.param.u32 %r1,[value];
      | st.local.u32 [loc+4],%r1;
      | cvta.local.u64 %rd2,loc;
      | add.u64 %rd3,%rd2,4;
      | ld.u32 %r2,[%rd3];
      | st.global.u32 [%rd1],%r2;
      | ret;
      |}
      |.visible .entry local_store(.param .u64 out, .param .u32 value)
      |{
      | .local .align 4 .b8 loc[16];
      | .reg .b64 %rd<4>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[out]; ld.param.u32 %r1,[value];
      | cvta.local.u64 %rd2,loc;
      | add.u64 %rd3,%rd2,4;
      | st.u32 [%rd3],%r1;
      | ld.local.u32 %r2,[loc+4];
      | st.global.u32 [%rd1],%r2;
      | ret;
      |}
      |""".stripMargin

  val genericBytePtx: String =
    """.version 6.4
      |.target sm_50
      |.address_size 64
      |
      |.visible .entry global_u8(.param .u64 p, .param .u32 value, .param .u64 out)
      |{
      | .reg .b64 %rd<4>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[p]; ld.param.u32 %r1,[value]; ld.param.u64 %rd2,[out];
      | add.u64 %rd3,%rd1,32;
      | st.cg.u8 [%rd3],%r1;
      | ld.cg.u8 %r2,[%rd3];
      | st.global.u32 [%rd2],%r2;
      | st.global.u32 [%rd2+4],%r1;
      | ret;
      |}
      |.visible .entry global_s8(.param .u64 p, .param .u32 value, .param .u64 out)
      |{
      | .reg .b64 %rd<4>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[p]; ld.param.u32 %r1,[value]; ld.param.u64 %rd2,[out];
      | add.u64 %rd3,%rd1,32;
      | st.cg.u8 [%rd3],%r1;
      | ld.cg.s8 %r2,[%rd3];
      | st.global.u32 [%rd2],%r2;
      | st.global.u32 [%rd2+4],%r1;
      | ret;
      |}
      |.visible .entry global_u8_barrier(.param .u64 p, .param .u32 value, .param .u64 out)
      |{
      | .reg .b64 %rd<4>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[p]; ld.param.u32 %r1,[value]; ld.param.u64 %rd2,[out];
      | add.u64 %rd3,%rd1,32;
      | st.cg.u8 [%rd3],%r1;
      | membar.cta;
      | ld.cg.u8 %r2,[%rd3];
      | st.global.u32 [%rd2],%r2;
      | st.global.u32 [%rd2+4],%r1;
      | ret;
      |}
      |.visible .entry shared_u8(.param .u64 out, .param .u32 value)
      |{
      | .shared .align 4 .b8 sh[16];
      | .reg .b64 %rd<4>; .reg .b32 %r<7>;
      | ld.param.u64 %rd1,[out]; ld.param.u32 %r1,[value];
      | mov.u32 %r2,0x5a; st.shared.u8 [sh+3],%r2;
      | mov.u32 %r2,0xa5; st.shared.u8 [sh+5],%r2;
      | st.shared.u8 [sh+4],%r1;
      | cvta.shared.u64 %rd2,sh;
      | add.u64 %rd3,%rd2,4;
      | st.cg.u8 [%rd3],%r1;
      | ld.cg.u8 %r3,[%rd3];
      | ld.shared.u8 %r4,[sh+3];
      | ld.shared.u8 %r6,[sh+5];
      | shl.b32 %r4,%r4,16;
      | shl.b32 %r3,%r3,8;
      | or.b32 %r3,%r3,%r4;
      | or.b32 %r3,%r3,%r6;
      | st.global.u32 [%rd1],%r3;
      | st.global.u32 [%rd1+4],%r1;
      | ret;
      |}
      |.visible .entry local_u8(.param .u64 out, .param .u32 value)
      |{
      | .local .align 4 .b8 loc[16];
      | .reg .b64 %rd<4>; .reg .b32 %r<7>;
      | ld.param.u64 %rd1,[out]; ld.param.u32 %r1,[value];
      | mov.u32 %r2,0x5a; st.local.u8 [loc+3],%r2;
      | mov.u32 %r2,0xa5; st.local.u8 [loc+5],%r2;
      | st.local.u8 [loc+4],%r1;
      | cvta.local.u64 %rd2,loc;
      | add.u64 %rd3,%rd2,4;
      | st.cg.u8 [%rd3],%r1;
      | ld.cg.u8 %r3,[%rd3];
      | ld.local.u8 %r4,[loc+3];
      | ld.local.u8 %r6,[loc+5];
      | shl.b32 %r4,%r4,16;
      | shl.b32 %r3,%r3,8;
      | or.b32 %r3,%r3,%r4;
      | or.b32 %r3,%r3,%r6;
      | st.global.u32 [%rd1],%r3;
      | st.global.u32 [%rd1+4],%r1;
      | ret;
      |}
      |""".stripMargin

  val genericCompoundPtx: String =
    """.version 6.4
      |.target sm_50
      |.address_size 64
      |
      |.visible .entry compound_global(.param .u64 p, .param .u64 out, .param .u32 value)
      |{
      | .reg .b64 %rd<4>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[p]; ld.param.u64 %rd2,[out]; ld.param.u32 %r1,[value];
      | add.u64 %rd3,%rd1,512;
      | st.cg.u32 [%rd3],%r1;
      | ld.cg.u32 %r2,[%rd3];
      | st.global.u32 [%rd2],%r2;
      | ret;
      |}
      |.visible .entry compound_shared(.param .u64 out, .param .u32 value)
      |{
      | .shared .align 4 .b8 sh[16];
      | .reg .b64 %rd<4>; .reg .b32 %r<4>;
      | ld.param.u64 %rd1,[out]; ld.param.u32 %r1,[value];
      | st.shared.u32 [sh+4],%r1;
      | cvta.shared.u64 %rd2,sh;
      | add.u64 %rd3,%rd2,4;
      | st.cg.u32 [%rd3],%r1;
      | ld.cg.u32 %r2,[%rd3];
      | ld.shared.u32 %r3,[sh+4];
      | add.u32 %r2,%r2,%r3;
      | st.global.u32 [%rd1],%r2;
      | ret;
      |}
      |.visible .entry compound_local(.param .u64 out, .param .u32 value)
      |{
      | .local .align 4 .b8 loc[16];
      | .reg .b64 %rd<4>; .reg .b32 %r<4>;
      | ld.param.u64 %rd1,[out]; ld.param.u32 %r1,[value];
      | st.local.u32 [loc+4],%r1;
      | cvta.local.u64 %rd2,loc;
      | add.u64 %rd3,%rd2,4;
      | st.cg.u32 [%rd3],%r1;
      | ld.cg.u32 %r2,[%rd3];
      | ld.local.u32 %r3,[loc+4];
      | add.u32 %r2,%r2,%r3;
      | st.global.u32 [%rd1],%r2;
      | ret;
      |}
      |""".stripMargin

  val genericVectorPtx: String =
    """.version 6.4
      |.target sm_50
      |.address_size 64
      |
      |.visible .entry store_v2_u32(.param .u64 p, .param .u32 a, .param .u32 b)
      |{
      | .reg .b64 %rd<4>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[p]; ld.param.u32 %r1,[a]; ld.param.u32 %r2,[b];
      | add.u64 %rd2,%rd1,512;
      | st.cg.v2.u32 [%rd2],{%r1,%r2};
      | ret;
      |}
      |.visible .entry store_v2_u64(.param .u64 p, .param .u64 a, .param .u64 b)
      |{
      | .reg .b64 %rd<6>;
      | ld.param.u64 %rd1,[p]; ld.param.u64 %rd2,[a]; ld.param.u64 %rd3,[b];
      | add.u64 %rd4,%rd1,512;
      | st.cg.v2.u64 [%rd4],{%rd2,%rd3};
      | ret;
      |}
      |.visible .entry store_v4_u32(.param .u64 p, .param .u32 a, .param .u32 b, .param .u32 c, .param .u32 d)
      |{
      | .reg .b64 %rd<4>; .reg .b32 %r<5>;
      | ld.param.u64 %rd1,[p]; ld.param.u32 %r1,[a]; ld.param.u32 %r2,[b];
      | ld.param.u32 %r3,[c]; ld.param.u32 %r4,[d];
      | add.u64 %rd2,%rd1,512;
      | st.cg.v4.u32 [%rd2],{%r1,%r2,%r3,%r4};
      | ret;
      |}
      |.visible .entry load_v2_u32(.param .u64 p, .param .u64 out)
      |{
      | .reg .b64 %rd<6>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[p]; ld.param.u64 %rd2,[out];
      | add.u64 %rd3,%rd1,512;
      | ld.cg.v2.u32 {%r1,%r2},[%rd3];
      | st.global.v2.u32 [%rd2],{%r1,%r2};
      | ret;
      |}
      |.visible .entry load_v2_u64(.param .u64 p, .param .u64 out)
      |{
      | .reg .b64 %rd<6>;
      | ld.param.u64 %rd1,[p]; ld.param.u64 %rd2,[out];
      | add.u64 %rd3,%rd1,512;
      | ld.cg.v2.u64 {%rd4,%rd5},[%rd3];
      | st.global.v2.u64 [%rd2],{%rd4,%rd5};
      | ret;
      |}
      |.visible .entry load_v4_u32(.param .u64 p, .param .u64 out)
      |{
      | .reg .b64 %rd<6>; .reg .b32 %r<5>;
      | ld.param.u64 %rd1,[p]; ld.param.u64 %rd2,[out];
      | add.u64 %rd3,%rd1,512;
      | ld.cg.v4.u32 {%r1,%r2,%r3,%r4},[%rd3];
      | st.global.v4.u32 [%rd2],{%r1,%r2,%r3,%r4};
      | ret;
      |}
      |.visible .entry repeated_store_then_load(.param .u64 p, .param .u32 value)
      |{
      | .reg .b64 %rd<6>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[p]; ld.param.u32 %r1,[value];
      | add.u64 %rd2,%rd1,512;
      | st.cg.u32 [%rd2],%r1;
      | ld.cg.u32 %r2,[%rd2];
      | st.global.u32 [%rd1],%r2;
      | ret;
      |}
      |.visible .entry repeated_load(.param .u64 p, .param .u64 out)
      |{
      | .reg .b64 %rd<6>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[p]; ld.param.u64 %rd2,[out];
      | add.u64 %rd3,%rd1,512;
      | ld.cg.u32 %r1,[%rd3];
      | ld.cg.u32 %r2,[%rd3+4];
      | st.global.u32 [%rd2],%r1;
      | st.global.u32 [%rd2+4],%r2;
      | ret;
      |}
      |.visible .entry repeated_store(.param .u64 p, .param .u32 a, .param .u32 b)
      |{
      | .reg .b64 %rd<4>; .reg .b32 %r<3>;
      | ld.param.u64 %rd1,[p]; ld.param.u32 %r1,[a]; ld.param.u32 %r2,[b];
      | add.u64 %rd2,%rd1,512;
      | st.cg.u32 [%rd2],%r1;
      | st.cg.u32 [%rd2],%r2;
      | ret;
      |}
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "genericAddressGlobalLoadSm50" ->
      """//--------------------- .text.global_load         --------------------------
        |	.section	.text.global_load,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         global_load
        |        .type           global_load,@function
        |        .size           global_load,(.L_x_1 - global_load)
        |        .other          global_load,@"STO_CUDA_ENTRY STV_DEFAULT"
        |global_load:
        |.text.global_load:
        |                                                                  /* 0x001fd400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001ec400fda207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   LD.E R4, [R4], P0 ;            /* 0x8090000000070404 */
        |                                                                  /* 0x041fc400fc4007f1 */
        |        /*0048*/                   MOV R2, c[0x0][0x148] ;        /* 0x4c98078005270002 */
        |        /*0050*/                   MOV R3, c[0x0][0x14c] ;        /* 0x4c98078005370003 */
        |        /*0058*/                   STG.E [R2], R4 ;               /* 0xeedc200000070204 */
        |                                                                  /* 0x001ffc00ffe007ed */
        |        /*0068*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0070*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericAddressGlobalLoadSm60" ->
      """//--------------------- .text.global_load         --------------------------
        |	.section	.text.global_load,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         global_load
        |        .type           global_load,@function
        |        .size           global_load,(.L_x_1 - global_load)
        |        .other          global_load,@"STO_CUDA_ENTRY STV_DEFAULT"
        |global_load:
        |.text.global_load:
        |                                                                  /* 0x001fd400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001ec400fda207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   LD.E R4, [R4], P0 ;            /* 0x8090000000070404 */
        |                                                                  /* 0x041fc400fc4007e1 */
        |        /*0048*/                   MOV R2, c[0x0][0x148] ;        /* 0x4c98078005270002 */
        |        /*0050*/                   MOV R3, c[0x0][0x14c] ;        /* 0x4c98078005370003 */
        |        /*0058*/                   STG.E [R2], R4 ;               /* 0xeedc200000070204 */
        |                                                                  /* 0x001ffc00ffe007ed */
        |        /*0068*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0070*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericAddressGlobalStoreSm50" ->
      """//--------------------- .text.global_store        --------------------------
        |	.section	.text.global_store,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=4"
        |	.align	32
        |        .global         global_store
        |        .type           global_store,@function
        |        .size           global_store,(.L_x_1 - global_store)
        |        .other          global_store,@"STO_CUDA_ENTRY STV_DEFAULT"
        |global_store:
        |.text.global_store:
        |                                                                  /* 0x001fd400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |                                                                  /* 0x001fb000fe2207f6 */
        |        /*0028*/                   LEA R2.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470002 */
        |        /*0030*/                   LEA.HI.X P0, R3, R0, RZ, R3 ;  /* 0x5bd801c00ff70003 */
        |        /*0038*/                   MOV R0, c[0x0][0x148] ;        /* 0x4c98078005270000 */
        |                                                                  /* 0x001f8800fde007f1 */
        |        /*0048*/                   ST.E [R2], R0, P0 ;            /* 0xa090000000070200 */
        |        /*0050*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0058*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*0068*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericAddressGlobalStoreSm60" ->
      """//--------------------- .text.global_store        --------------------------
        |	.section	.text.global_store,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=4"
        |	.align	32
        |        .global         global_store
        |        .type           global_store,@function
        |        .size           global_store,(.L_x_1 - global_store)
        |        .other          global_store,@"STO_CUDA_ENTRY STV_DEFAULT"
        |global_store:
        |.text.global_store:
        |                                                                  /* 0x001fd400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |                                                                  /* 0x001fb000fc2207f6 */
        |        /*0028*/                   LEA R2.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470002 */
        |        /*0030*/                   LEA.HI.X P0, R3, R0, RZ, R3 ;  /* 0x5bd801c00ff70003 */
        |        /*0038*/                   MOV R0, c[0x0][0x148] ;        /* 0x4c98078005270000 */
        |                                                                  /* 0x001f8800fde007f1 */
        |        /*0048*/                   ST.E [R2], R0, P0 ;            /* 0xa090000000070200 */
        |        /*0050*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0058*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*0068*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericAddressLocalLoadSm50" ->
      """//--------------------- .text.local_load          --------------------------
        |	.section	.text.local_load,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         local_load
        |        .type           local_load,@function
        |        .size           local_load,(.L_x_1 - local_load)
        |        .other          local_load,@"STO_CUDA_ENTRY STV_DEFAULT"
        |local_load:
        |.text.local_load:
        |                                                                  /* 0x001fc800fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   IADD32I R1, R1, -0x10 ;        /* 0x1c0fffffff070101 */
        |        /*0018*/                   MOV R4, c[0x0][0x148] ;        /* 0x4c98078005270004 */
        |                                                                  /* 0x081fd800fec007f3 */
        |        /*0028*/                   STL [R1+0x4], R4 ;             /* 0xef54000000470104 */
        |        /*0030*/                   LOP.OR R0, R1, c[0x0][0x4] ;   /* 0x4c47020000170100 */
        |        /*0038*/                   LEA R6.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470006 */
        |                                                                  /* 0x001f8400f62007ed */
        |        /*0048*/                   LEA.HI.X P0, R7, R0, RZ, RZ ;  /* 0x5bd87fc00ff70007 */
        |        /*0050*/                   LD.E R0, [R6], P0 ;            /* 0x8090000000070600 */
        |        /*0058*/                   MOV R2, c[0x0][0x140] ;        /* 0x4c98078005070002 */
        |                                                                  /* 0x001f9c20fe2007f2 */
        |        /*0068*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |        /*0070*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0078*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*0088*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericAddressLocalLoadSm60" ->
      """//--------------------- .text.local_load          --------------------------
        |	.section	.text.local_load,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         local_load
        |        .type           local_load,@function
        |        .size           local_load,(.L_x_1 - local_load)
        |        .other          local_load,@"STO_CUDA_ENTRY STV_DEFAULT"
        |local_load:
        |.text.local_load:
        |                                                                   /* 0x001fc800fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;          /* 0x4c98078000870001 */
        |        /*0010*/                   IADD32I R1, R1, -0x10 ;         /* 0x1c0fffffff070101 */
        |        /*0018*/                   MOV R4, c[0x0][0x148] ;         /* 0x4c98078005270004 */
        |                                                                   /* 0x001fc400fec007f3 */
        |        /*0028*/                   STL [R1+0x4], R4 ;              /* 0xef54000000470104 */
        |        /*0030*/                   IADD R0.CC, R1, c[0x0][0x4] ;   /* 0x4c10800000170100 */
        |        /*0038*/                   IADD.X R2, RZ, c[0x0][0x104] ;  /* 0x4c1008000417ff02 */
        |                                                                   /* 0x001ec400fda007e6 */
        |        /*0048*/                   LEA R6.CC, R0, 0x4 ;            /* 0x36d7800000470006 */
        |        /*0050*/                   LEA.HI.X P0, R7, R0, RZ, R2 ;   /* 0x5bd801400ff70007 */
        |        /*0058*/                   LD.E R0, [R6], P0 ;             /* 0x8090000000070600 */
        |                                                                   /* 0x041fc400fe4007f1 */
        |        /*0068*/                   MOV R2, c[0x0][0x140] ;         /* 0x4c98078005070002 */
        |        /*0070*/                   MOV R3, c[0x0][0x144] ;         /* 0x4c98078005170003 */
        |        /*0078*/                   STG.E [R2], R0 ;                /* 0xeedc200000070200 */
        |                                                                   /* 0x001ffc00ffe007e6 */
        |        /*0088*/                   NOP ;                           /* 0x50b0000000070f00 */
        |        /*0090*/                   EXIT ;                          /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericAddressLocalStoreSm50" ->
      """//--------------------- .text.local_store         --------------------------
        |	.section	.text.local_store,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         local_store
        |        .type           local_store,@function
        |        .size           local_store,(.L_x_1 - local_store)
        |        .other          local_store,@"STO_CUDA_ENTRY STV_DEFAULT"
        |local_store:
        |.text.local_store:
        |                                                                  /* 0x001fd400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   IADD32I R1, R1, -0x10 ;        /* 0x1c0fffffff070101 */
        |        /*0018*/                   MOV R6, c[0x0][0x148] ;        /* 0x4c98078005270006 */
        |                                                                  /* 0x001fb440fec007f6 */
        |        /*0028*/                   LOP.OR R0, R1, c[0x0][0x4] ;   /* 0x4c47020000170100 */
        |        /*0030*/                   LEA R4.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470004 */
        |        /*0038*/                   LEA.HI.X P0, R5, R0, RZ, RZ ;  /* 0x5bd87fc00ff70005 */
        |                                                                  /* 0x001f8400f62007f1 */
        |        /*0048*/                   ST.E [R4], R6, P0 ;            /* 0xa090000000070406 */
        |        /*0050*/                   LDL R0, [R1+0x4] ;             /* 0xef44000000470100 */
        |        /*0058*/                   MOV R2, c[0x0][0x140] ;        /* 0x4c98078005070002 */
        |                                                                  /* 0x001f9820fe2007f2 */
        |        /*0068*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |        /*0070*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0078*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*0088*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericAddressLocalStoreSm60" ->
      """//--------------------- .text.local_store         --------------------------
        |	.section	.text.local_store,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         local_store
        |        .type           local_store,@function
        |        .size           local_store,(.L_x_1 - local_store)
        |        .other          local_store,@"STO_CUDA_ENTRY STV_DEFAULT"
        |local_store:
        |.text.local_store:
        |                                                                   /* 0x001fd400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;          /* 0x4c98078000870001 */
        |        /*0010*/                   IADD32I R1, R1, -0x10 ;         /* 0x1c0fffffff070101 */
        |        /*0018*/                   MOV R6, c[0x0][0x148] ;         /* 0x4c98078005270006 */
        |                                                                   /* 0x081fd800fe2007f6 */
        |        /*0028*/                   IADD R0.CC, R1, c[0x0][0x4] ;   /* 0x4c10800000170100 */
        |        /*0030*/                   IADD.X R2, RZ, c[0x0][0x104] ;  /* 0x4c1008000417ff02 */
        |        /*0038*/                   LEA R4.CC, R0.reuse, 0x4 ;      /* 0x36d7800000470004 */
        |                                                                   /* 0x001ec400fe2007ed */
        |        /*0048*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;   /* 0x5bd801400ff70005 */
        |        /*0050*/                   ST.E [R4], R6, P0 ;             /* 0xa090000000070406 */
        |        /*0058*/                   LDL R0, [R1+0x4] ;              /* 0xef44000000470100 */
        |                                                                   /* 0x041fc400fe4007e1 */
        |        /*0068*/                   MOV R2, c[0x0][0x140] ;         /* 0x4c98078005070002 */
        |        /*0070*/                   MOV R3, c[0x0][0x144] ;         /* 0x4c98078005170003 */
        |        /*0078*/                   STG.E [R2], R0 ;                /* 0xeedc200000070200 */
        |                                                                   /* 0x001ffc00ffe007e5 */
        |        /*0088*/                   NOP ;                           /* 0x50b0000000070f00 */
        |        /*0090*/                   EXIT ;                          /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericAddressSharedLoadSm50" ->
      """//--------------------- .text.shared_load         --------------------------
        |	.section	.text.shared_load,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         shared_load
        |        .type           shared_load,@function
        |        .size           shared_load,(.L_x_1 - shared_load)
        |        .other          shared_load,@"STO_CUDA_ENTRY STV_DEFAULT"
        |shared_load:
        |.text.shared_load:
        |                                                                  /* 0x081fd800fec007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x0] ;          /* 0x4c98078000070000 */
        |        /*0018*/                   LEA R4.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470004 */
        |                                                                  /* 0x001fe000fe8007f1 */
        |        /*0028*/                   LEA.HI.X P0, R5, R0, RZ, RZ ;  /* 0x5bd87fc00ff70005 */
        |        /*0030*/                   MOV R0, c[0x0][0x148] ;        /* 0x4c98078005270000 */
        |        /*0038*/                   STS [0x4], R0 ;                /* 0xef5c00000047ff00 */
        |                                                                  /* 0x001fc800fc2007b1 */
        |        /*0048*/                   LD.E R4, [R4], P0 ;            /* 0x8090000000070404 */
        |        /*0050*/                   MOV R2, c[0x0][0x140] ;        /* 0x4c98078005070002 */
        |        /*0058*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |                                                                  /* 0x001ffc00fda107f1 */
        |        /*0068*/                   STG.E [R2], R4 ;               /* 0xeedc200000070204 */
        |        /*0070*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0078*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_0:
        |                                                                  /* 0x001f8000fc0007ff */
        |        /*0088*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff07000f */
        |        /*0090*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000fc0007e0 */
        |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    "genericAddressSharedLoadSm60" ->
      """//--------------------- .text.shared_load         --------------------------
        |	.section	.text.shared_load,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         shared_load
        |        .type           shared_load,@function
        |        .size           shared_load,(.L_x_1 - shared_load)
        |        .other          shared_load,@"STO_CUDA_ENTRY STV_DEFAULT"
        |shared_load:
        |.text.shared_load:
        |                                                                  /* 0x001fd400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x0] ;          /* 0x4c98078000070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x100] ;        /* 0x4c98078004070002 */
        |                                                                  /* 0x001f9000fc2207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   MOV R0, c[0x0][0x148] ;        /* 0x4c98078005270000 */
        |                                                                  /* 0x001f8400f62007f8 */
        |        /*0048*/                   STS [0x4], R0 ;                /* 0xef5c00000047ff00 */
        |        /*0050*/                   LD.E R4, [R4], P0 ;            /* 0x8090000000070404 */
        |        /*0058*/                   MOV R2, c[0x0][0x140] ;        /* 0x4c98078005070002 */
        |                                                                  /* 0x001fb420fe2007e2 */
        |        /*0068*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |        /*0070*/                   STG.E [R2], R4 ;               /* 0xeedc200000070204 */
        |        /*0078*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*0088*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericAddressSharedStoreSm50" ->
      """//--------------------- .text.shared_store        --------------------------
        |	.section	.text.shared_store,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         shared_store
        |        .type           shared_store,@function
        |        .size           shared_store,(.L_x_1 - shared_store)
        |        .other          shared_store,@"STO_CUDA_ENTRY STV_DEFAULT"
        |shared_store:
        |.text.shared_store:
        |                                                                  /* 0x001fc400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x0] ;          /* 0x4c98078000070000 */
        |        /*0018*/                   MOV R6, c[0x0][0x148] ;        /* 0x4c98078005270006 */
        |                                                                  /* 0x081fd800fe6007f1 */
        |        /*0028*/                   MOV R2, c[0x0][0x140] ;        /* 0x4c98078005070002 */
        |        /*0030*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |        /*0038*/                   LEA R4.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470004 */
        |                                                                  /* 0x001c7c00fe2007ed */
        |        /*0048*/                   LEA.HI.X P0, R5, R0, RZ, RZ ;  /* 0x5bd87fc00ff70005 */
        |        /*0050*/                   ST.E [R4], R6, P0 ;            /* 0xa090000000070406 */
        |        /*0058*/                   LDS.U.32 R0, [0x4] ;           /* 0xef4c10000047ff00 */
        |                                                                  /* 0x001ffc00fc200ff1 */
        |        /*0068*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0070*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0078*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_0:
        |                                                                  /* 0x001f8000fc0007ff */
        |        /*0088*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff07000f */
        |        /*0090*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000fc0007e0 */
        |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    "genericAddressSharedStoreSm60" ->
      """//--------------------- .text.shared_store        --------------------------
        |	.section	.text.shared_store,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         shared_store
        |        .type           shared_store,@function
        |        .size           shared_store,(.L_x_1 - shared_store)
        |        .other          shared_store,@"STO_CUDA_ENTRY STV_DEFAULT"
        |shared_store:
        |.text.shared_store:
        |                                                                  /* 0x001fd400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x0] ;          /* 0x4c98078000070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x100] ;        /* 0x4c98078004070002 */
        |                                                                  /* 0x001fb000fc2207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   MOV R6, c[0x0][0x148] ;        /* 0x4c98078005270006 */
        |                                                                  /* 0x001f8400e22007f1 */
        |        /*0048*/                   ST.E [R4], R6, P0 ;            /* 0xa090000000070406 */
        |        /*0050*/                   LDS.U.32 R0, [0x4] ;           /* 0xef4c10000047ff00 */
        |        /*0058*/                   MOV R2, c[0x0][0x140] ;        /* 0x4c98078005070002 */
        |                                                                  /* 0x001ffc01fe2007ff */
        |        /*0068*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |        /*0070*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0078*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_0:
        |                                                                  /* 0x001f8000fc0007ff */
        |        /*0088*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff07000f */
        |        /*0090*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000fc0007e0 */
        |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    "genericByteGlobalS8Sm50" ->
      """//--------------------- .text.global_s8           --------------------------
        |	.section	.text.global_s8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         global_s8
        |        .type           global_s8,@function
        |        .size           global_s8,(.L_x_1 - global_s8)
        |        .other          global_s8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |global_s8:
        |.text.global_s8:
        |                                                                  /* 0x001c4400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   LDC.U8 R6, c[0x0][0x148] ;     /* 0xef9000001487ff06 */
        |                                                                  /* 0x001fb440fec007f4 */
        |        /*0028*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |        /*0030*/                   LEA R4.CC, R0.reuse, 0x20 ;    /* 0x36d7800002070004 */
        |        /*0038*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |                                                                  /* 0x001f8400f6200ff1 */
        |        /*0048*/                   ST.E.CG.U8 [R4], R6, P0 ;      /* 0xa110000000070406 */
        |        /*0050*/                   LD.E.CG.S8 R0, [R4], P0 ;      /* 0x8130000000070400 */
        |        /*0058*/                   MOV R2, c[0x0][0x150] ;        /* 0x4c98078005470002 */
        |                                                                  /* 0x001fc400fe4007f1 */
        |        /*0068*/                   MOV R3, c[0x0][0x154] ;        /* 0x4c98078005570003 */
        |        /*0070*/                   MOV R7, c[0x0][0x148] ;        /* 0x4c98078005270007 */
        |        /*0078*/                   STG.E [R2+0x4], R7 ;           /* 0xeedc200000470207 */
        |                                                                  /* 0x001ffc00fd4107f1 */
        |        /*0088*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0090*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0098*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_0:
        |                                                                  /* 0x001f8000fc0007ff */
        |        /*00a8*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff07000f */
        |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    "genericByteGlobalS8Sm60" ->
      """//--------------------- .text.global_s8           --------------------------
        |	.section	.text.global_s8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         global_s8
        |        .type           global_s8,@function
        |        .size           global_s8,(.L_x_1 - global_s8)
        |        .other          global_s8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |global_s8:
        |.text.global_s8:
        |                                                                  /* 0x001c4400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   LDC.U8 R6, c[0x0][0x148] ;     /* 0xef9000001487ff06 */
        |                                                                  /* 0x001fb440fec007f4 */
        |        /*0028*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |        /*0030*/                   LEA R4.CC, R0.reuse, 0x20 ;    /* 0x36d7800002070004 */
        |        /*0038*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |                                                                  /* 0x001f8400f6200ff1 */
        |        /*0048*/                   ST.E.CG.U8 [R4], R6, P0 ;      /* 0xa110000000070406 */
        |        /*0050*/                   LD.E.CG.S8 R0, [R4], P0 ;      /* 0x8130000000070400 */
        |        /*0058*/                   MOV R2, c[0x0][0x150] ;        /* 0x4c98078005470002 */
        |                                                                  /* 0x001fc400fc4007e1 */
        |        /*0068*/                   MOV R3, c[0x0][0x154] ;        /* 0x4c98078005570003 */
        |        /*0070*/                   MOV R7, c[0x0][0x148] ;        /* 0x4c98078005270007 */
        |        /*0078*/                   STG.E [R2+0x4], R7 ;           /* 0xeedc200000470207 */
        |                                                                  /* 0x001ffc00fd4107f1 */
        |        /*0088*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0090*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0098*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_0:
        |                                                                  /* 0x001f8000fc0007ff */
        |        /*00a8*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff07000f */
        |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    "genericByteGlobalU8BarrierSm50" ->
      """//--------------------- .text.global_u8_barrier   --------------------------
        |	.section	.text.global_u8_barrier,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         global_u8_barrier
        |        .type           global_u8_barrier,@function
        |        .size           global_u8_barrier,(.L_x_1 - global_u8_barrier)
        |        .other          global_u8_barrier,@"STO_CUDA_ENTRY STV_DEFAULT"
        |global_u8_barrier:
        |.text.global_u8_barrier:
        |                                                                        /* 0x001fc400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;               /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;              /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R3, c[0x0][0x144] ;              /* 0x4c98078005170003 */
        |                                                                        /* 0x081fc440fec00714 */
        |        /*0028*/                   LDC.U8 R7, c[0x0][0x148] ;           /* 0xef9000001487ff07 */
        |        /*0030*/                   LEA R2.CC, R0.reuse, 0x20 ;          /* 0x36d7800002070002 */
        |        /*0038*/                   LEA.HI.X P0, R3, R0.reuse, RZ, R3 ;  /* 0x5bd801c00ff70003 */
        |                                                                        /* 0x0023c400fe0007ec */
        |        /*0048*/                   IADD32I R0.CC, R0, 0x20 ;            /* 0x1c10000002070000 */
        |        /*0050*/         {         IADD.X R5, RZ, c[0x0][0x144] ;       /* 0x4c1008000517ff05 */
        |        /*0058*/                   ST.E.CG.U8 [R2], R7, P0         }
        |                                                                        /* 0xa110000000070207 */
        |                                                                        /* 0x001ffc00fda207f6 */
        |        /*0068*/                   LEA R4.CC, R0.reuse, RZ ;            /* 0x5bd780000ff70004 */
        |        /*0070*/                   LEA.HI.X P1, R5, R0, RZ, R5 ;        /* 0x5bd902c00ff70005 */
        |        /*0078*/                   MEMBAR.CTA ;                         /* 0xef98000000070000 */
        |                                                                        /* 0x001fc400f6200ff0 */
        |        /*0088*/         {         MOV R2, c[0x0][0x150] ;              /* 0x4c98078005470002 */
        |        /*0090*/                   LD.E.CG.U8 R4, [R4], P1         }
        |                                                                        /* 0x8510000000070404 */
        |        /*0098*/                   MOV R3, c[0x0][0x154] ;              /* 0x4c98078005570003 */
        |                                                                        /* 0x041fc400fe2007f2 */
        |        /*00a8*/                   MOV R0, c[0x0][0x148] ;              /* 0x4c98078005270000 */
        |        /*00b0*/                   STG.E [R2+0x4], R0 ;                 /* 0xeedc200000470200 */
        |        /*00b8*/                   STG.E [R2], R4 ;                     /* 0xeedc200000070204 */
        |                                                                        /* 0x001f8000ffe007ff */
        |        /*00c8*/                   EXIT ;                               /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericByteGlobalU8BarrierSm60" ->
      """//--------------------- .text.global_u8_barrier   --------------------------
        |	.section	.text.global_u8_barrier,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         global_u8_barrier
        |        .type           global_u8_barrier,@function
        |        .size           global_u8_barrier,(.L_x_1 - global_u8_barrier)
        |        .other          global_u8_barrier,@"STO_CUDA_ENTRY STV_DEFAULT"
        |global_u8_barrier:
        |.text.global_u8_barrier:
        |                                                                    /* 0x001f8400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;           /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;          /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R3, c[0x0][0x144] ;          /* 0x4c98078005170003 */
        |                                                                    /* 0x001f8440fec00714 */
        |        /*0028*/                   LDC.U8 R7, c[0x0][0x148] ;       /* 0xef9000001487ff07 */
        |        /*0030*/                   LEA R2.CC, R0.reuse, 0x20 ;      /* 0x36d7800002070002 */
        |        /*0038*/                   LEA.HI.X P0, R3, R0, RZ, R3 ;    /* 0x5bd801c00ff70003 */
        |                                                                    /* 0x0023c400fe0007ec */
        |        /*0048*/                   IADD32I R0.CC, R0, 0x20 ;        /* 0x1c10000002070000 */
        |        /*0050*/         {         IADD.X R5, RZ, c[0x0][0x144] ;   /* 0x4c1008000517ff05 */
        |        /*0058*/                   ST.E.CG.U8 [R2], R7, P0         }
        |                                                                    /* 0xa110000000070207 */
        |                                                                    /* 0x001ffc00fda207f6 */
        |        /*0068*/                   LEA R4.CC, R0.reuse, RZ ;        /* 0x5bd780000ff70004 */
        |        /*0070*/                   LEA.HI.X P1, R5, R0, RZ, R5 ;    /* 0x5bd902c00ff70005 */
        |        /*0078*/                   MEMBAR.CTA ;                     /* 0xef98000000070000 */
        |                                                                    /* 0x001fc400f6200ff0 */
        |        /*0088*/         {         MOV R2, c[0x0][0x150] ;          /* 0x4c98078005470002 */
        |        /*0090*/                   LD.E.CG.U8 R4, [R4], P1         }
        |                                                                    /* 0x8510000000070404 */
        |        /*0098*/                   MOV R3, c[0x0][0x154] ;          /* 0x4c98078005570003 */
        |                                                                    /* 0x041fc400fe2007f2 */
        |        /*00a8*/                   MOV R0, c[0x0][0x148] ;          /* 0x4c98078005270000 */
        |        /*00b0*/                   STG.E [R2+0x4], R0 ;             /* 0xeedc200000470200 */
        |        /*00b8*/                   STG.E [R2], R4 ;                 /* 0xeedc200000070204 */
        |                                                                    /* 0x001f8000ffe007ff */
        |        /*00c8*/                   EXIT ;                           /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericByteGlobalU8Sm50" ->
      """//--------------------- .text.global_u8           --------------------------
        |	.section	.text.global_u8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         global_u8
        |        .type           global_u8,@function
        |        .size           global_u8,(.L_x_1 - global_u8)
        |        .other          global_u8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |global_u8:
        |.text.global_u8:
        |                                                                  /* 0x001c4400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   LDC.U8 R6, c[0x0][0x148] ;     /* 0xef9000001487ff06 */
        |                                                                  /* 0x001fb440fec007f4 */
        |        /*0028*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |        /*0030*/                   LEA R4.CC, R0.reuse, 0x20 ;    /* 0x36d7800002070004 */
        |        /*0038*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |                                                                  /* 0x001f8400f6200ff1 */
        |        /*0048*/                   ST.E.CG.U8 [R4], R6, P0 ;      /* 0xa110000000070406 */
        |        /*0050*/                   LD.E.CG.U8 R0, [R4], P0 ;      /* 0x8110000000070400 */
        |        /*0058*/                   MOV R2, c[0x0][0x150] ;        /* 0x4c98078005470002 */
        |                                                                  /* 0x001fc400fe4007f1 */
        |        /*0068*/                   MOV R3, c[0x0][0x154] ;        /* 0x4c98078005570003 */
        |        /*0070*/                   MOV R7, c[0x0][0x148] ;        /* 0x4c98078005270007 */
        |        /*0078*/                   STG.E [R2+0x4], R7 ;           /* 0xeedc200000470207 */
        |                                                                  /* 0x001ffc00fd4107f1 */
        |        /*0088*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0090*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0098*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_0:
        |                                                                  /* 0x001f8000fc0007ff */
        |        /*00a8*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff07000f */
        |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    "genericByteGlobalU8Sm60" ->
      """//--------------------- .text.global_u8           --------------------------
        |	.section	.text.global_u8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         global_u8
        |        .type           global_u8,@function
        |        .size           global_u8,(.L_x_1 - global_u8)
        |        .other          global_u8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |global_u8:
        |.text.global_u8:
        |                                                                  /* 0x001c4400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   LDC.U8 R6, c[0x0][0x148] ;     /* 0xef9000001487ff06 */
        |                                                                  /* 0x001fb440fec007f4 */
        |        /*0028*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |        /*0030*/                   LEA R4.CC, R0.reuse, 0x20 ;    /* 0x36d7800002070004 */
        |        /*0038*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |                                                                  /* 0x001f8400f6200ff1 */
        |        /*0048*/                   ST.E.CG.U8 [R4], R6, P0 ;      /* 0xa110000000070406 */
        |        /*0050*/                   LD.E.CG.U8 R0, [R4], P0 ;      /* 0x8110000000070400 */
        |        /*0058*/                   MOV R2, c[0x0][0x150] ;        /* 0x4c98078005470002 */
        |                                                                  /* 0x001fc400fc4007e1 */
        |        /*0068*/                   MOV R3, c[0x0][0x154] ;        /* 0x4c98078005570003 */
        |        /*0070*/                   MOV R7, c[0x0][0x148] ;        /* 0x4c98078005270007 */
        |        /*0078*/                   STG.E [R2+0x4], R7 ;           /* 0xeedc200000470207 */
        |                                                                  /* 0x001ffc00fd4107f1 */
        |        /*0088*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0090*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0098*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_0:
        |                                                                  /* 0x001f8000fc0007ff */
        |        /*00a8*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff07000f */
        |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    "genericByteLocalU8Sm50" ->
      """//--------------------- .text.local_u8            --------------------------
        |	.section	.text.local_u8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	32
        |        .global         local_u8
        |        .type           local_u8,@function
        |        .size           local_u8,(.L_x_1 - local_u8)
        |        .other          local_u8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |local_u8:
        |.text.local_u8:
        |                                                                     /* 0x001c4400fe0007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;            /* 0x4c98078000870001 */
        |        /*0010*/         {         IADD32I R1, R1, -0x10 ;           /* 0x1c0fffffff070101 */
        |        /*0018*/                   LDC.U8 R8, c[0x0][0x148]         }
        |                                                                     /* 0xef9000001487ff08 */
        |                                                                     /* 0x001fcc00fe0007f2 */
        |        /*0028*/                   MOV32I R4, 0x5a ;                 /* 0x0100000005a7f004 */
        |        /*0030*/         {         MOV32I R5, 0xa5 ;                 /* 0x010000000a57f005 */
        |        /*0038*/                   STL.U8 [R1+0x3], R4         }
        |                                                                     /* 0xef50000000370104 */
        |                                                                     /* 0x081fd800fec007f0 */
        |        /*0048*/         {         LOP.OR R0, R1, c[0x0][0x4] ;      /* 0x4c47020000170100 */
        |        /*0050*/                   STL.U8 [R1+0x5], R5         }
        |                                                                     /* 0xef50000000570105 */
        |        /*0058*/                   LEA R10.CC, R0.reuse, 0x4 ;       /* 0x36d780000047000a */
        |                                                                     /* 0x001fc401fda007f0 */
        |        /*0068*/         {         LEA.HI.X P0, R11, R0, RZ, RZ ;    /* 0x5bd87fc00ff7000b */
        |        /*0070*/                   STL.U8 [R1+0x4], R8         }
        |                                                                     /* 0xef50000000470108 */
        |        /*0078*/                   ST.E.CG.U8 [R10], R8, P0 ;        /* 0xa110000000070a08 */
        |                                                                     /* 0x001ec400f62007b1 */
        |        /*0088*/                   LD.E.CG.U8 R0, [R10], P0 ;        /* 0x8110000000070a00 */
        |        /*0090*/                   LDL.U8 R2, [R1+0x3] ;             /* 0xef40000000370102 */
        |        /*0098*/                   LDL.U8 R3, [R1+0x5] ;             /* 0xef40000000570103 */
        |                                                                     /* 0x001fc800fe2007e1 */
        |        /*00a8*/                   MOV R9, c[0x0][0x148] ;           /* 0x4c98078005270009 */
        |        /*00b0*/                   MOV R6, c[0x0][0x140] ;           /* 0x4c98078005070006 */
        |        /*00b8*/                   MOV R7, c[0x0][0x144] ;           /* 0x4c98078005170007 */
        |                                                                     /* 0x001fd820fe2007f1 */
        |        /*00c8*/                   STG.E [R6+0x4], R9 ;              /* 0xeedc200000470609 */
        |        /*00d0*/                   SHL R0, R0, 0x8 ;                 /* 0x3848000000870000 */
        |        /*00d8*/                   SHL R2, R2, 0x10 ;                /* 0x3848000001070202 */
        |                                                                     /* 0x001ffc00fe2007e2 */
        |        /*00e8*/                   LOP3.LUT R0, R3, R0, R2, 0xfe ;   /* 0x5be7010fe0070300 */
        |        /*00f0*/                   STG.E [R6], R0 ;                  /* 0xeedc200000070600 */
        |        /*00f8*/                   EXIT ;                            /* 0xe30000000007000f */
        |.L_x_0:
        |                                                                     /* 0x001f8000fc0007ff */
        |        /*0108*/                   BRA `(.L_x_0) ;                   /* 0xe2400fffff07000f */
        |        /*0110*/                   NOP;                              /* 0x50b0000000070f00 */
        |        /*0118*/                   NOP;                              /* 0x50b0000000070f00 */
        |                                                                     /* 0x001f8000fc0007e0 */
        |        /*0128*/                   NOP;                              /* 0x50b0000000070f00 */
        |        /*0130*/                   NOP;                              /* 0x50b0000000070f00 */
        |        /*0138*/                   NOP;                              /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    "genericByteLocalU8Sm60" ->
      """//--------------------- .text.local_u8            --------------------------
        |	.section	.text.local_u8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	32
        |        .global         local_u8
        |        .type           local_u8,@function
        |        .size           local_u8,(.L_x_1 - local_u8)
        |        .other          local_u8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |local_u8:
        |.text.local_u8:
        |                                                                     /* 0x001c5800fe0007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;            /* 0x4c98078000870001 */
        |        /*0010*/         {         IADD32I R1, R1, -0x10 ;           /* 0x1c0fffffff070101 */
        |        /*0018*/                   LDC.U8 R8, c[0x0][0x148]         }
        |                                                                     /* 0xef9000001487ff08 */
        |                                                                     /* 0x081fd800fe2007e6 */
        |        /*0028*/                   IADD R0.CC, R1, c[0x0][0x4] ;     /* 0x4c10800000170100 */
        |        /*0030*/                   IADD.X R2, RZ, c[0x0][0x104] ;    /* 0x4c1008000417ff02 */
        |        /*0038*/                   LEA R10.CC, R0.reuse, 0x4 ;       /* 0x36d780000047000a */
        |                                                                     /* 0x001f8801fe2007f0 */
        |        /*0048*/         {         LEA.HI.X P0, R11, R0, RZ, R2 ;    /* 0x5bd801400ff7000b */
        |        /*0050*/                   STL.U8 [R1+0x4], R8         }
        |                                                                     /* 0xef50000000470108 */
        |        /*0058*/                   MOV32I R4, 0x5a ;                 /* 0x0100000005a7f004 */
        |                                                                     /* 0x001fc000fe4007f0 */
        |        /*0068*/         {         MOV32I R5, 0xa5 ;                 /* 0x010000000a57f005 */
        |        /*0070*/                   STL.U8 [R1+0x3], R4         }
        |                                                                     /* 0xef50000000370104 */
        |        /*0078*/         {         MOV R9, c[0x0][0x148] ;           /* 0x4c98078005270009 */
        |                                                                     /* 0x001fc400fe0007f8 */
        |        /*0088*/                   STL.U8 [R1+0x5], R5         }
        |                                                                     /* 0xef50000000570105 */
        |        /*0090*/         {         MOV R6, c[0x0][0x140] ;           /* 0x4c98078005070006 */
        |        /*0098*/                   ST.E.CG.U8 [R10], R8, P0         }
        |                                                                     /* 0xa110000000070a08 */
        |                                                                     /* 0x001ec400f62007f0 */
        |        /*00a8*/         {         MOV R7, c[0x0][0x144] ;           /* 0x4c98078005170007 */
        |        /*00b0*/                   LD.E.CG.U8 R0, [R10], P0         }
        |                                                                     /* 0x8110000000070a00 */
        |        /*00b8*/                   LDL.U8 R2, [R1+0x3] ;             /* 0xef40000000370102 */
        |                                                                     /* 0x041f8400fe2007b1 */
        |        /*00c8*/                   LDL.U8 R3, [R1+0x5] ;             /* 0xef40000000570103 */
        |        /*00d0*/                   STG.E [R6+0x4], R9 ;              /* 0xeedc200000470609 */
        |        /*00d8*/                   SHL R0, R0, 0x8 ;                 /* 0x3848000000870000 */
        |                                                                     /* 0x001fc400fc4007e6 */
        |        /*00e8*/                   SHL R2, R2, 0x10 ;                /* 0x3848000001070202 */
        |        /*00f0*/                   LOP3.LUT R0, R3, R0, R2, 0xfe ;   /* 0x5be7010fe0070300 */
        |        /*00f8*/                   STG.E [R6], R0 ;                  /* 0xeedc200000070600 */
        |                                                                     /* 0x001f8000ffe007ff */
        |        /*0108*/                   EXIT ;                            /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericByteSharedU8Sm50" ->
      """//--------------------- .text.shared_u8           --------------------------
        |	.section	.text.shared_u8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	32
        |        .global         shared_u8
        |        .type           shared_u8,@function
        |        .size           shared_u8,(.L_x_1 - shared_u8)
        |        .other          shared_u8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |shared_u8:
        |.text.shared_u8:
        |                                                                    /* 0x001c4400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;           /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x0] ;            /* 0x4c98078000070000 */
        |        /*0018*/                   LDC.U8 R9, c[0x0][0x148] ;       /* 0xef9000001487ff09 */
        |                                                                    /* 0x0007d000fe0007f4 */
        |        /*0028*/                   MOV32I R3, 0x5a ;                /* 0x0100000005a7f003 */
        |        /*0030*/         {         MOV32I R8, 0xa5 ;                /* 0x010000000a57f008 */
        |        /*0038*/                   STS.U8 [0x3], R3         }
        |                                                                    /* 0xef5800000037ff03 */
        |                                                                    /* 0x001f8400fec207f0 */
        |        /*0048*/         {         LEA R4.CC, R0.reuse, 0x4 ;       /* 0x36d7800000470004 */
        |        /*0050*/                   STS.U8 [0x5], R8         }
        |                                                                    /* 0xef5800000057ff08 */
        |        /*0058*/                   LEA.HI.X P0, R5, R0, RZ, RZ ;    /* 0x5bd87fc00ff70005 */
        |                                                                    /* 0x001ec400fe200fec */
        |        /*0068*/                   STS.U8 [0x4], R9 ;               /* 0xef5800000047ff09 */
        |        /*0070*/                   ST.E.CG.U8 [R4], R9, P0 ;        /* 0xa110000000070409 */
        |        /*0078*/                   LD.E.CG.U8 R0, [R4], P0 ;        /* 0x8110000000070400 */
        |                                                                    /* 0x001d4400fe000711 */
        |        /*0088*/                   LDS.U.U8 R2, [0x3] ;             /* 0xef4810000037ff02 */
        |        /*0090*/         {         MOV R11, c[0x0][0x148] ;         /* 0x4c9807800527000b */
        |        /*0098*/                   LDS.U.U8 R6, [0x5]         }
        |                                                                    /* 0xef4810000057ff06 */
        |                                                                    /* 0x003fc420fc2017f1 */
        |        /*00a8*/                   MOV R3, c[0x0][0x144] ;          /* 0x4c98078005170003 */
        |        /*00b0*/                   SHL R7, R0, 0x8 ;                /* 0x3848000000870007 */
        |        /*00b8*/                   SHL R0, R2, 0x10 ;               /* 0x3848000001070200 */
        |                                                                    /* 0x009fc800fe6007f2 */
        |        /*00c8*/                   MOV R2, c[0x0][0x140] ;          /* 0x4c98078005070002 */
        |        /*00d0*/                   STG.E [R2+0x4], R11 ;            /* 0xeedc20000047020b */
        |        /*00d8*/                   LOP3.LUT R0, R6, R7, R0, 0xfe ;  /* 0x5be7000fe0770600 */
        |                                                                    /* 0x001ffc00ffe007f1 */
        |        /*00e8*/                   STG.E [R2], R0 ;                 /* 0xeedc200000070200 */
        |        /*00f0*/                   EXIT ;                           /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericByteSharedU8Sm60" ->
      """//--------------------- .text.shared_u8           --------------------------
        |	.section	.text.shared_u8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	32
        |        .global         shared_u8
        |        .type           shared_u8,@function
        |        .size           shared_u8,(.L_x_1 - shared_u8)
        |        .other          shared_u8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |shared_u8:
        |.text.shared_u8:
        |                                                                    /* 0x001c4400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;           /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x0] ;            /* 0x4c98078000070000 */
        |        /*0018*/                   LDC.U8 R9, c[0x0][0x148] ;       /* 0xef9000001487ff09 */
        |                                                                    /* 0x001f8440fec007f4 */
        |        /*0028*/                   MOV R2, c[0x0][0x100] ;          /* 0x4c98078004070002 */
        |        /*0030*/                   LEA R4.CC, R0.reuse, 0x4 ;       /* 0x36d7800000470004 */
        |        /*0038*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;    /* 0x5bd801400ff70005 */
        |                                                                    /* 0x0007d000fe0007e4 */
        |        /*0048*/                   MOV32I R3, 0x5a ;                /* 0x0100000005a7f003 */
        |        /*0050*/         {         MOV32I R8, 0xa5 ;                /* 0x010000000a57f008 */
        |        /*0058*/                   STS.U8 [0x3], R3         }
        |                                                                    /* 0xef5800000037ff03 */
        |                                                                    /* 0x005fc000fe2007f0 */
        |        /*0068*/         {         MOV R11, c[0x0][0x148] ;         /* 0x4c9807800527000b */
        |        /*0070*/                   STS.U8 [0x5], R8         }
        |                                                                    /* 0xef5800000057ff08 */
        |        /*0078*/         {         MOV R3, c[0x0][0x144] ;          /* 0x4c98078005170003 */
        |                                                                    /* 0x001ec400fe200ff3 */
        |        /*0088*/                   STS.U8 [0x4], R9         }
        |                                                                    /* 0xef5800000047ff09 */
        |        /*0090*/                   ST.E.CG.U8 [R4], R9, P0 ;        /* 0xa110000000070409 */
        |        /*0098*/                   LD.E.CG.U8 R0, [R4], P0 ;        /* 0x8110000000070400 */
        |                                                                    /* 0x041f8400e6200711 */
        |        /*00a8*/                   LDS.U.U8 R2, [0x3] ;             /* 0xef4810000037ff02 */
        |        /*00b0*/                   LDS.U.U8 R6, [0x5] ;             /* 0xef4810000057ff06 */
        |        /*00b8*/                   SHL R7, R0, 0x8 ;                /* 0x3848000000870007 */
        |                                                                    /* 0x001fcc00fc400fe1 */
        |        /*00c8*/                   SHL R0, R2, 0x10 ;               /* 0x3848000001070200 */
        |        /*00d0*/                   MOV R2, c[0x0][0x140] ;          /* 0x4c98078005070002 */
        |        /*00d8*/                   STG.E [R2+0x4], R11 ;            /* 0xeedc20000047020b */
        |                                                                    /* 0x001f9000fe2017e2 */
        |        /*00e8*/                   LOP3.LUT R0, R6, R7, R0, 0xfe ;  /* 0x5be7000fe0770600 */
        |        /*00f0*/                   STG.E [R2], R0 ;                 /* 0xeedc200000070200 */
        |        /*00f8*/                   NOP ;                            /* 0x50b0000000070f00 */
        |                                                                    /* 0x001f8000ffe007ff */
        |        /*0108*/                   EXIT ;                           /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericCompoundGlobalSm50" ->
      """//--------------------- .text.compound_global     --------------------------
        |	.section	.text.compound_global,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         compound_global
        |        .type           compound_global,@function
        |        .size           compound_global,(.L_x_1 - compound_global)
        |        .other          compound_global,@"STO_CUDA_ENTRY STV_DEFAULT"
        |compound_global:
        |.text.compound_global:
        |                                                                  /* 0x001fc400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001fb440fec007f4 */
        |        /*0028*/                   MOV R6, c[0x0][0x150] ;        /* 0x4c98078005470006 */
        |        /*0030*/                   LEA R4.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070004 */
        |        /*0038*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |                                                                  /* 0x001f8400f62007f1 */
        |        /*0048*/                   ST.E.CG [R4], R6, P0 ;         /* 0xa190000000070406 */
        |        /*0050*/                   LD.E.CG R0, [R4], P0 ;         /* 0x8190000000070400 */
        |        /*0058*/                   MOV R2, c[0x0][0x148] ;        /* 0x4c98078005270002 */
        |                                                                  /* 0x001fb020fe2007f2 */
        |        /*0068*/                   MOV R3, c[0x0][0x14c] ;        /* 0x4c98078005370003 */
        |        /*0070*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0078*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*0088*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericCompoundGlobalSm60" ->
      """//--------------------- .text.compound_global     --------------------------
        |	.section	.text.compound_global,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         compound_global
        |        .type           compound_global,@function
        |        .size           compound_global,(.L_x_1 - compound_global)
        |        .other          compound_global,@"STO_CUDA_ENTRY STV_DEFAULT"
        |compound_global:
        |.text.compound_global:
        |                                                                  /* 0x001fd400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001fb000fc2207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   MOV R6, c[0x0][0x150] ;        /* 0x4c98078005470006 */
        |                                                                  /* 0x001f8400f62007f1 */
        |        /*0048*/                   ST.E.CG [R4], R6, P0 ;         /* 0xa190000000070406 */
        |        /*0050*/                   LD.E.CG R0, [R4], P0 ;         /* 0x8190000000070400 */
        |        /*0058*/                   MOV R2, c[0x0][0x148] ;        /* 0x4c98078005270002 */
        |                                                                  /* 0x001fb020fe2007e2 */
        |        /*0068*/                   MOV R3, c[0x0][0x14c] ;        /* 0x4c98078005370003 */
        |        /*0070*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0078*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*0088*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericCompoundLocalSm50" ->
      """//--------------------- .text.compound_local      --------------------------
        |	.section	.text.compound_local,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         compound_local
        |        .type           compound_local,@function
        |        .size           compound_local,(.L_x_1 - compound_local)
        |        .other          compound_local,@"STO_CUDA_ENTRY STV_DEFAULT"
        |compound_local:
        |.text.compound_local:
        |                                                                  /* 0x001fc800fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   IADD32I R1, R1, -0x10 ;        /* 0x1c0fffffff070101 */
        |        /*0018*/                   MOV R3, c[0x0][0x148] ;        /* 0x4c98078005270003 */
        |                                                                  /* 0x081fd800fec007f3 */
        |        /*0028*/                   STL [R1+0x4], R3 ;             /* 0xef54000000470103 */
        |        /*0030*/                   LOP.OR R0, R1, c[0x0][0x4] ;   /* 0x4c47020000170100 */
        |        /*0038*/                   LEA R4.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470004 */
        |                                                                  /* 0x001ec400fe2007ed */
        |        /*0048*/                   LEA.HI.X P0, R5, R0, RZ, RZ ;  /* 0x5bd87fc00ff70005 */
        |        /*0050*/                   ST.E.CG [R4], R3, P0 ;         /* 0xa190000000070403 */
        |        /*0058*/                   LD.E.CG R0, [R4], P0 ;         /* 0x8190000000070400 */
        |                                                                  /* 0x001fc400fc2007b1 */
        |        /*0068*/                   LDL R2, [R1+0x4] ;             /* 0xef44000000470102 */
        |        /*0070*/                   MOV R6, c[0x0][0x140] ;        /* 0x4c98078005070006 */
        |        /*0078*/                   MOV R7, c[0x0][0x144] ;        /* 0x4c98078005170007 */
        |                                                                  /* 0x001f9000fe2107f2 */
        |        /*0088*/                   IADD R0, R0, R2 ;              /* 0x5c10000000270000 */
        |        /*0090*/                   STG.E [R6], R0 ;               /* 0xeedc200000070600 */
        |        /*0098*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*00a8*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericCompoundLocalSm60" ->
      """//--------------------- .text.compound_local      --------------------------
        |	.section	.text.compound_local,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         compound_local
        |        .type           compound_local,@function
        |        .size           compound_local,(.L_x_1 - compound_local)
        |        .other          compound_local,@"STO_CUDA_ENTRY STV_DEFAULT"
        |compound_local:
        |.text.compound_local:
        |                                                                   /* 0x001fc800fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;          /* 0x4c98078000870001 */
        |        /*0010*/                   IADD32I R1, R1, -0x10 ;         /* 0x1c0fffffff070101 */
        |        /*0018*/                   MOV R3, c[0x0][0x148] ;         /* 0x4c98078005270003 */
        |                                                                   /* 0x001fc400fec007f3 */
        |        /*0028*/                   STL [R1+0x4], R3 ;              /* 0xef54000000470103 */
        |        /*0030*/                   IADD R0.CC, R1, c[0x0][0x4] ;   /* 0x4c10800000170100 */
        |        /*0038*/                   IADD.X R2, RZ, c[0x0][0x104] ;  /* 0x4c1008000417ff02 */
        |                                                                   /* 0x001fc400fda007e6 */
        |        /*0048*/                   LEA R4.CC, R0, 0x4 ;            /* 0x36d7800000470004 */
        |        /*0050*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;   /* 0x5bd801400ff70005 */
        |        /*0058*/                   ST.E.CG [R4], R3, P0 ;          /* 0xa190000000070403 */
        |                                                                   /* 0x001fc400f62007b1 */
        |        /*0068*/                   LD.E.CG R0, [R4], P0 ;          /* 0x8190000000070400 */
        |        /*0070*/                   LDL R2, [R1+0x4] ;              /* 0xef44000000470102 */
        |        /*0078*/                   MOV R6, c[0x0][0x140] ;         /* 0x4c98078005070006 */
        |                                                                   /* 0x001fc420fc4007f1 */
        |        /*0088*/                   MOV R7, c[0x0][0x144] ;         /* 0x4c98078005170007 */
        |        /*0090*/                   IADD R0, R0, R2 ;               /* 0x5c10000000270000 */
        |        /*0098*/                   STG.E [R6], R0 ;                /* 0xeedc200000070600 */
        |                                                                   /* 0x001ffc00ffe007e3 */
        |        /*00a8*/                   NOP ;                           /* 0x50b0000000070f00 */
        |        /*00b0*/                   EXIT ;                          /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericCompoundSharedSm50" ->
      """//--------------------- .text.compound_shared     --------------------------
        |	.section	.text.compound_shared,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         compound_shared
        |        .type           compound_shared,@function
        |        .size           compound_shared,(.L_x_1 - compound_shared)
        |        .other          compound_shared,@"STO_CUDA_ENTRY STV_DEFAULT"
        |compound_shared:
        |.text.compound_shared:
        |                                                                  /* 0x001fd000fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x0] ;          /* 0x4c98078000070000 */
        |        /*0018*/                   MOV R7, c[0x0][0x148] ;        /* 0x4c98078005270007 */
        |                                                                  /* 0x001fb440fec007f1 */
        |        /*0028*/                   STS [0x4], R7 ;                /* 0xef5c00000047ff07 */
        |        /*0030*/                   LEA R4.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470004 */
        |        /*0038*/                   LEA.HI.X P0, R5, R0, RZ, RZ ;  /* 0x5bd87fc00ff70005 */
        |                                                                  /* 0x001c4400f62007f1 */
        |        /*0048*/                   ST.E.CG [R4], R7, P0 ;         /* 0xa190000000070407 */
        |        /*0050*/                   LD.E.CG R0, [R4], P0 ;         /* 0x8190000000070400 */
        |        /*0058*/                   LDS.U.32 R6, [0x4] ;           /* 0xef4c10000047ff06 */
        |                                                                  /* 0x043fc800fe2007e1 */
        |        /*0068*/                   MOV R2, c[0x0][0x140] ;        /* 0x4c98078005070002 */
        |        /*0070*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |        /*0078*/                   IADD R0, R0, R6 ;              /* 0x5c10000000670000 */
        |                                                                  /* 0x001ffc00fd4007f1 */
        |        /*0088*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0090*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0098*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_0:
        |                                                                  /* 0x001f8000fc0007ff */
        |        /*00a8*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff07000f */
        |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
        |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    "genericCompoundSharedSm60" ->
      """//--------------------- .text.compound_shared     --------------------------
        |	.section	.text.compound_shared,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         compound_shared
        |        .type           compound_shared,@function
        |        .size           compound_shared,(.L_x_1 - compound_shared)
        |        .other          compound_shared,@"STO_CUDA_ENTRY STV_DEFAULT"
        |compound_shared:
        |.text.compound_shared:
        |                                                                  /* 0x001fc400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x0] ;          /* 0x4c98078000070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x100] ;        /* 0x4c98078004070002 */
        |                                                                  /* 0x001fd840fe0007f4 */
        |        /*0028*/                   MOV R7, c[0x0][0x148] ;        /* 0x4c98078005270007 */
        |        /*0030*/         {         LEA R4.CC, R0.reuse, 0x4 ;     /* 0x36d7800000470004 */
        |        /*0038*/                   STS [0x4], R7         }
        |                                                                  /* 0xef5c00000047ff07 */
        |                                                                  /* 0x001ec400fe2007ed */
        |        /*0048*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0050*/                   ST.E.CG [R4], R7, P0 ;         /* 0xa190000000070407 */
        |        /*0058*/                   LD.E.CG R0, [R4], P0 ;         /* 0x8190000000070400 */
        |                                                                  /* 0x001fc400fc200711 */
        |        /*0068*/                   LDS.U.32 R6, [0x4] ;           /* 0xef4c10000047ff06 */
        |        /*0070*/                   MOV R2, c[0x0][0x140] ;        /* 0x4c98078005070002 */
        |        /*0078*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |                                                                  /* 0x001fa800fe210ff2 */
        |        /*0088*/                   IADD R0, R0, R6 ;              /* 0x5c10000000670000 */
        |        /*0090*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0098*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*00a8*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericLoadV2U32Sm50" ->
      """//--------------------- .text.load_v2_u32         --------------------------
        |	.section	.text.load_v2_u32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         load_v2_u32
        |        .type           load_v2_u32,@function
        |        .size           load_v2_u32,(.L_x_1 - load_v2_u32)
        |        .other          load_v2_u32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |load_v2_u32:
        |.text.load_v2_u32:
        |                                                                  /* 0x001fd400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001ec400fda207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   LD.E.CG.64 R4, [R4], P0 ;      /* 0x81b0000000070404 */
        |                                                                  /* 0x041fc400fc4007f1 */
        |        /*0048*/                   MOV R2, c[0x0][0x148] ;        /* 0x4c98078005270002 */
        |        /*0050*/                   MOV R3, c[0x0][0x14c] ;        /* 0x4c98078005370003 */
        |        /*0058*/                   STG.E.64 [R2], R4 ;            /* 0xeedd200000070204 */
        |                                                                  /* 0x001ffc00ffe007ed */
        |        /*0068*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0070*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericLoadV2U32Sm60" ->
      """//--------------------- .text.load_v2_u32         --------------------------
        |	.section	.text.load_v2_u32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         load_v2_u32
        |        .type           load_v2_u32,@function
        |        .size           load_v2_u32,(.L_x_1 - load_v2_u32)
        |        .other          load_v2_u32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |load_v2_u32:
        |.text.load_v2_u32:
        |                                                                  /* 0x001fd400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001ec400fda207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   LD.E.CG.64 R4, [R4], P0 ;      /* 0x81b0000000070404 */
        |                                                                  /* 0x041fc400fc4007e1 */
        |        /*0048*/                   MOV R2, c[0x0][0x148] ;        /* 0x4c98078005270002 */
        |        /*0050*/                   MOV R3, c[0x0][0x14c] ;        /* 0x4c98078005370003 */
        |        /*0058*/                   STG.E.64 [R2], R4 ;            /* 0xeedd200000070204 */
        |                                                                  /* 0x001ffc00ffe007ed */
        |        /*0068*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0070*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericLoadV2U64Sm50" ->
      """//--------------------- .text.load_v2_u64         --------------------------
        |	.section	.text.load_v2_u64,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         load_v2_u64
        |        .type           load_v2_u64,@function
        |        .size           load_v2_u64,(.L_x_1 - load_v2_u64)
        |        .other          load_v2_u64,@"STO_CUDA_ENTRY STV_DEFAULT"
        |load_v2_u64:
        |.text.load_v2_u64:
        |                                                                  /* 0x001fd400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001ec400fda207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   LD.E.CG.128 R4, [R4], P0 ;     /* 0x81d0000000070404 */
        |                                                                  /* 0x041fc400fc4007f1 */
        |        /*0048*/                   MOV R2, c[0x0][0x148] ;        /* 0x4c98078005270002 */
        |        /*0050*/                   MOV R3, c[0x0][0x14c] ;        /* 0x4c98078005370003 */
        |        /*0058*/                   STG.E.128 [R2], R4 ;           /* 0xeede200000070204 */
        |                                                                  /* 0x001ffc00ffe007ed */
        |        /*0068*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0070*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericLoadV2U64Sm60" ->
      """//--------------------- .text.load_v2_u64         --------------------------
        |	.section	.text.load_v2_u64,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         load_v2_u64
        |        .type           load_v2_u64,@function
        |        .size           load_v2_u64,(.L_x_1 - load_v2_u64)
        |        .other          load_v2_u64,@"STO_CUDA_ENTRY STV_DEFAULT"
        |load_v2_u64:
        |.text.load_v2_u64:
        |                                                                  /* 0x001fd400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001ec400fda207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   LD.E.CG.128 R4, [R4], P0 ;     /* 0x81d0000000070404 */
        |                                                                  /* 0x041fc400fc4007e1 */
        |        /*0048*/                   MOV R2, c[0x0][0x148] ;        /* 0x4c98078005270002 */
        |        /*0050*/                   MOV R3, c[0x0][0x14c] ;        /* 0x4c98078005370003 */
        |        /*0058*/                   STG.E.128 [R2], R4 ;           /* 0xeede200000070204 */
        |                                                                  /* 0x001ffc00ffe007ed */
        |        /*0068*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0070*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericLoadV4U32Sm50" ->
      """//--------------------- .text.load_v4_u32         --------------------------
        |	.section	.text.load_v4_u32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         load_v4_u32
        |        .type           load_v4_u32,@function
        |        .size           load_v4_u32,(.L_x_1 - load_v4_u32)
        |        .other          load_v4_u32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |load_v4_u32:
        |.text.load_v4_u32:
        |                                                                  /* 0x001fd400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001ec400fda207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   LD.E.CG.128 R4, [R4], P0 ;     /* 0x81d0000000070404 */
        |                                                                  /* 0x041fc400fc4007f1 */
        |        /*0048*/                   MOV R2, c[0x0][0x148] ;        /* 0x4c98078005270002 */
        |        /*0050*/                   MOV R3, c[0x0][0x14c] ;        /* 0x4c98078005370003 */
        |        /*0058*/                   STG.E.128 [R2], R4 ;           /* 0xeede200000070204 */
        |                                                                  /* 0x001ffc00ffe007ed */
        |        /*0068*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0070*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericLoadV4U32Sm60" ->
      """//--------------------- .text.load_v4_u32         --------------------------
        |	.section	.text.load_v4_u32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         load_v4_u32
        |        .type           load_v4_u32,@function
        |        .size           load_v4_u32,(.L_x_1 - load_v4_u32)
        |        .other          load_v4_u32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |load_v4_u32:
        |.text.load_v4_u32:
        |                                                                  /* 0x001fd400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001ec400fda207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   LD.E.CG.128 R4, [R4], P0 ;     /* 0x81d0000000070404 */
        |                                                                  /* 0x041fc400fc4007e1 */
        |        /*0048*/                   MOV R2, c[0x0][0x148] ;        /* 0x4c98078005270002 */
        |        /*0050*/                   MOV R3, c[0x0][0x14c] ;        /* 0x4c98078005370003 */
        |        /*0058*/                   STG.E.128 [R2], R4 ;           /* 0xeede200000070204 */
        |                                                                  /* 0x001ffc00ffe007ed */
        |        /*0068*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0070*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericRepeatedStoreThenLoadSm50" ->
      """//--------------------- .text.repeated_store_then_load --------------------------
        |	.section	.text.repeated_store_then_load,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         repeated_store_then_load
        |        .type           repeated_store_then_load,@function
        |        .size           repeated_store_then_load,(.L_x_1 - repeated_store_then_load)
        |        .other          repeated_store_then_load,@"STO_CUDA_ENTRY STV_DEFAULT"
        |repeated_store_then_load:
        |.text.repeated_store_then_load:
        |                                                                  /* 0x001fc400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001fb440fec007f4 */
        |        /*0028*/                   MOV R6, c[0x0][0x148] ;        /* 0x4c98078005270006 */
        |        /*0030*/                   LEA R4.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070004 */
        |        /*0038*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |                                                                  /* 0x001f8400f62007f1 */
        |        /*0048*/                   ST.E.CG [R4], R6, P0 ;         /* 0xa190000000070406 */
        |        /*0050*/                   LD.E.CG R0, [R4], P0 ;         /* 0x8190000000070400 */
        |        /*0058*/                   MOV R2, c[0x0][0x140] ;        /* 0x4c98078005070002 */
        |                                                                  /* 0x001fb020fe2007f2 */
        |        /*0068*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |        /*0070*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0078*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*0088*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericRepeatedStoreThenLoadSm60" ->
      """//--------------------- .text.repeated_store_then_load --------------------------
        |	.section	.text.repeated_store_then_load,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         repeated_store_then_load
        |        .type           repeated_store_then_load,@function
        |        .size           repeated_store_then_load,(.L_x_1 - repeated_store_then_load)
        |        .other          repeated_store_then_load,@"STO_CUDA_ENTRY STV_DEFAULT"
        |repeated_store_then_load:
        |.text.repeated_store_then_load:
        |                                                                  /* 0x001fd400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R2, c[0x0][0x144] ;        /* 0x4c98078005170002 */
        |                                                                  /* 0x001fb000fc2207f6 */
        |        /*0028*/                   LEA R4.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070004 */
        |        /*0030*/                   LEA.HI.X P0, R5, R0, RZ, R2 ;  /* 0x5bd801400ff70005 */
        |        /*0038*/                   MOV R6, c[0x0][0x148] ;        /* 0x4c98078005270006 */
        |                                                                  /* 0x001f8400f62007f1 */
        |        /*0048*/                   ST.E.CG [R4], R6, P0 ;         /* 0xa190000000070406 */
        |        /*0050*/                   LD.E.CG R0, [R4], P0 ;         /* 0x8190000000070400 */
        |        /*0058*/                   MOV R2, c[0x0][0x140] ;        /* 0x4c98078005070002 */
        |                                                                  /* 0x001fb020fe2007e2 */
        |        /*0068*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |        /*0070*/                   STG.E [R2], R0 ;               /* 0xeedc200000070200 */
        |        /*0078*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*0088*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericStoreV2U32Sm50" ->
      """//--------------------- .text.store_v2_u32        --------------------------
        |	.section	.text.store_v2_u32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         store_v2_u32
        |        .type           store_v2_u32,@function
        |        .size           store_v2_u32,(.L_x_1 - store_v2_u32)
        |        .other          store_v2_u32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |store_v2_u32:
        |.text.store_v2_u32:
        |                                                                  /* 0x001fc400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |                                                                  /* 0x081fd800fe6007f1 */
        |        /*0028*/                   MOV R4, c[0x0][0x148] ;        /* 0x4c98078005270004 */
        |        /*0030*/                   MOV R5, c[0x0][0x14c] ;        /* 0x4c98078005370005 */
        |        /*0038*/                   LEA R2.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070002 */
        |                                                                  /* 0x001fbc00fe2007ed */
        |        /*0048*/                   LEA.HI.X P0, R3, R0, RZ, R3 ;  /* 0x5bd801c00ff70003 */
        |        /*0050*/                   ST.E.CG.64 [R2], R4, P0 ;      /* 0xa1b0000000070204 */
        |        /*0058*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001ffc00ffe007e2 */
        |        /*0068*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0070*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericStoreV2U32Sm60" ->
      """//--------------------- .text.store_v2_u32        --------------------------
        |	.section	.text.store_v2_u32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         store_v2_u32
        |        .type           store_v2_u32,@function
        |        .size           store_v2_u32,(.L_x_1 - store_v2_u32)
        |        .other          store_v2_u32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |store_v2_u32:
        |.text.store_v2_u32:
        |                                                                  /* 0x001fd400fc2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |                                                                  /* 0x001f8400fc2207f6 */
        |        /*0028*/                   LEA R2.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070002 */
        |        /*0030*/                   LEA.HI.X P0, R3, R0, RZ, R3 ;  /* 0x5bd801c00ff70003 */
        |        /*0038*/                   MOV R4, c[0x0][0x148] ;        /* 0x4c98078005270004 */
        |                                                                  /* 0x001fbc00fe2007eb */
        |        /*0048*/                   MOV R5, c[0x0][0x14c] ;        /* 0x4c98078005370005 */
        |        /*0050*/                   ST.E.CG.64 [R2], R4, P0 ;      /* 0xa1b0000000070204 */
        |        /*0058*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001ffc00ffe007e2 */
        |        /*0068*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0070*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericStoreV2U64Sm50" ->
      """//--------------------- .text.store_v2_u64        --------------------------
        |	.section	.text.store_v2_u64,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         store_v2_u64
        |        .type           store_v2_u64,@function
        |        .size           store_v2_u64,(.L_x_1 - store_v2_u64)
        |        .other          store_v2_u64,@"STO_CUDA_ENTRY STV_DEFAULT"
        |store_v2_u64:
        |.text.store_v2_u64:
        |                                                                  /* 0x001fc400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |                                                                  /* 0x001fc400fe2007f1 */
        |        /*0028*/                   MOV R4, c[0x0][0x148] ;        /* 0x4c98078005270004 */
        |        /*0030*/                   MOV R5, c[0x0][0x14c] ;        /* 0x4c98078005370005 */
        |        /*0038*/                   MOV R6, c[0x0][0x150] ;        /* 0x4c98078005470006 */
        |                                                                  /* 0x001fb440fec007e1 */
        |        /*0048*/                   MOV R7, c[0x0][0x154] ;        /* 0x4c98078005570007 */
        |        /*0050*/                   LEA R2.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070002 */
        |        /*0058*/                   LEA.HI.X P0, R3, R0, RZ, R3 ;  /* 0x5bd801c00ff70003 */
        |                                                                  /* 0x001f8800fde007f1 */
        |        /*0068*/                   ST.E.CG.128 [R2], R4, P0 ;     /* 0xa1d0000000070204 */
        |        /*0070*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0078*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*0088*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "genericStoreV4U32Sm50" ->
      """//--------------------- .text.store_v4_u32        --------------------------
        |	.section	.text.store_v4_u32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         store_v4_u32
        |        .type           store_v4_u32,@function
        |        .size           store_v4_u32,(.L_x_1 - store_v4_u32)
        |        .other          store_v4_u32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |store_v4_u32:
        |.text.store_v4_u32:
        |                                                                  /* 0x001fc400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;         /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R0, c[0x0][0x140] ;        /* 0x4c98078005070000 */
        |        /*0018*/                   MOV R3, c[0x0][0x144] ;        /* 0x4c98078005170003 */
        |                                                                  /* 0x001fc400fe2007f1 */
        |        /*0028*/                   MOV R4, c[0x0][0x148] ;        /* 0x4c98078005270004 */
        |        /*0030*/                   MOV R5, c[0x0][0x14c] ;        /* 0x4c98078005370005 */
        |        /*0038*/                   MOV R6, c[0x0][0x150] ;        /* 0x4c98078005470006 */
        |                                                                  /* 0x001fb440fec007e1 */
        |        /*0048*/                   MOV R7, c[0x0][0x154] ;        /* 0x4c98078005570007 */
        |        /*0050*/                   LEA R2.CC, R0.reuse, 0x200 ;   /* 0x36d7800020070002 */
        |        /*0058*/                   LEA.HI.X P0, R3, R0, RZ, R3 ;  /* 0x5bd801c00ff70003 */
        |                                                                  /* 0x001f8800fde007f1 */
        |        /*0068*/                   ST.E.CG.128 [R2], R4, P0 ;     /* 0xa1d0000000070204 */
        |        /*0070*/                   NOP ;                          /* 0x50b0000000070f00 */
        |        /*0078*/                   NOP ;                          /* 0x50b0000000070f00 */
        |                                                                  /* 0x001f8000ffe007ff */
        |        /*0088*/                   EXIT ;                         /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin
  )
}
