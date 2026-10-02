package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object GenericMemoryFixtures {
  val legacyGenericAddressProbePtxSource: String =
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

  val legacyGenericMemoryExtensionsByteGenericPtxSource: String =
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

  val legacyGenericMemoryExtensionsCompoundSpacesPtxSource: String =
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

  val legacyGenericMemoryExtensionsVectorGenericPtxSource: String =
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

  val uniformAddressSm86ProbeCudaSource: String =
    """extern "C" __global__ void probe(unsigned *out, const unsigned *in, unsigned uniformOffset, unsigned high) {
      |  __shared__ unsigned data[32];
      |  unsigned tid = threadIdx.x;
      |  unsigned address = (unsigned)__cvta_generic_to_shared(&data[tid]);
      |  const unsigned *global_ptr = in + tid;
      |  asm volatile("cp.async.ca.shared.global [%0], [%1], 4;" :: "r"(address), "l"(global_ptr));
      |  asm volatile("cp.async.commit_group; cp.async.wait_all;");
      |  out[tid] = data[tid];
      |  if (uniformOffset == 0x12345678u) out[tid] = 0;
      |  if (high == 0x12345678u) out[tid] = 0;
      |}
      |""".stripMargin

  val legacyGenericAddressGlobalLoadSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.global_load         --------------------------
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
      |.L_x_0:
      |        /*0078*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericAddressGlobalLoadSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.global_load         --------------------------
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
      |.L_x_0:
      |        /*0078*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericAddressGlobalStoreSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.global_store        --------------------------
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
      |.L_x_0:
      |        /*0070*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0078*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericAddressGlobalStoreSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.global_store        --------------------------
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
      |.L_x_0:
      |        /*0070*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0078*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericAddressLocalLoadSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.local_load          --------------------------
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
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
      |                                                                  /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericAddressLocalLoadSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.local_load          --------------------------
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
      |.L_x_0:
      |        /*0098*/                   BRA `(.L_x_0) ;                 /* 0xe2400fffff87000f */
      |                                                                   /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                            /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                            /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                            /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericAddressLocalStoreSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.local_store         --------------------------
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
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
      |                                                                  /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericAddressLocalStoreSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.local_store         --------------------------
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
      |.L_x_0:
      |        /*0098*/                   BRA `(.L_x_0) ;                 /* 0xe2400fffff87000f */
      |                                                                   /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                            /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                            /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                            /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericAddressSharedLoadSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.shared_load         --------------------------
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
      |""".stripMargin

  val legacyGenericAddressSharedLoadSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.shared_load         --------------------------
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
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
      |                                                                  /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericAddressSharedStoreSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.shared_store        --------------------------
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
      |""".stripMargin

  val legacyGenericAddressSharedStoreSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.shared_store        --------------------------
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
      |""".stripMargin

  val legacyGenericMemoryExtensionsByteGlobalS8Sm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.global_s8           --------------------------
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
      |""".stripMargin

  val legacyGenericMemoryExtensionsByteGlobalS8Sm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.global_s8           --------------------------
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
      |""".stripMargin

  val legacyGenericMemoryExtensionsByteGlobalU8BarrierSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.global_u8_barrier   --------------------------
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
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;                      /* 0xe2400fffff87000f */
      |        /*00d8*/                   NOP;                                 /* 0x50b0000000070f00 */
      |                                                                        /* 0x001f8000fc0007e0 */
      |        /*00e8*/                   NOP;                                 /* 0x50b0000000070f00 */
      |        /*00f0*/                   NOP;                                 /* 0x50b0000000070f00 */
      |        /*00f8*/                   NOP;                                 /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsByteGlobalU8BarrierSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.global_u8_barrier   --------------------------
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
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;                  /* 0xe2400fffff87000f */
      |        /*00d8*/                   NOP;                             /* 0x50b0000000070f00 */
      |                                                                    /* 0x001f8000fc0007e0 */
      |        /*00e8*/                   NOP;                             /* 0x50b0000000070f00 */
      |        /*00f0*/                   NOP;                             /* 0x50b0000000070f00 */
      |        /*00f8*/                   NOP;                             /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsByteGlobalU8Sm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.global_u8           --------------------------
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
      |""".stripMargin

  val legacyGenericMemoryExtensionsByteGlobalU8Sm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.global_u8           --------------------------
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
      |""".stripMargin

  val legacyGenericMemoryExtensionsByteLocalU8Sm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.local_u8            --------------------------
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
      |""".stripMargin

  val legacyGenericMemoryExtensionsByteLocalU8Sm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.local_u8            --------------------------
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
      |.L_x_0:
      |        /*0110*/                   BRA `(.L_x_0) ;                   /* 0xe2400fffff87000f */
      |        /*0118*/                   NOP;                              /* 0x50b0000000070f00 */
      |                                                                     /* 0x001f8000fc0007e0 */
      |        /*0128*/                   NOP;                              /* 0x50b0000000070f00 */
      |        /*0130*/                   NOP;                              /* 0x50b0000000070f00 */
      |        /*0138*/                   NOP;                              /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsByteSharedU8Sm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.shared_u8           --------------------------
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
      |.L_x_0:
      |        /*00f8*/                   BRA `(.L_x_0) ;                  /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsByteSharedU8Sm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.shared_u8           --------------------------
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
      |.L_x_0:
      |        /*0110*/                   BRA `(.L_x_0) ;                  /* 0xe2400fffff87000f */
      |        /*0118*/                   NOP;                             /* 0x50b0000000070f00 */
      |                                                                    /* 0x001f8000fc0007e0 */
      |        /*0128*/                   NOP;                             /* 0x50b0000000070f00 */
      |        /*0130*/                   NOP;                             /* 0x50b0000000070f00 */
      |        /*0138*/                   NOP;                             /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsCompoundGlobalSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.compound_global     --------------------------
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
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
      |                                                                  /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsCompoundGlobalSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.compound_global     --------------------------
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
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
      |                                                                  /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsCompoundLocalSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.compound_local      --------------------------
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
      |.L_x_0:
      |        /*00b0*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsCompoundLocalSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.compound_local      --------------------------
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
      |.L_x_0:
      |        /*00b8*/                   BRA `(.L_x_0) ;                 /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsCompoundSharedSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.compound_shared     --------------------------
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
      |""".stripMargin

  val legacyGenericMemoryExtensionsCompoundSharedSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.compound_shared     --------------------------
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
      |.L_x_0:
      |        /*00b0*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsLoadV2U32Sm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.load_v2_u32         --------------------------
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
      |.L_x_0:
      |        /*0078*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsLoadV2U32Sm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.load_v2_u32         --------------------------
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
      |.L_x_0:
      |        /*0078*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsLoadV2U64Sm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.load_v2_u64         --------------------------
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
      |.L_x_0:
      |        /*0078*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsLoadV2U64Sm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.load_v2_u64         --------------------------
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
      |.L_x_0:
      |        /*0078*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsLoadV4U32Sm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.load_v4_u32         --------------------------
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
      |.L_x_0:
      |        /*0078*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsLoadV4U32Sm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.load_v4_u32         --------------------------
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
      |.L_x_0:
      |        /*0078*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsRepeatedStoreThenLoadSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.repeated_store_then_load --------------------------
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
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
      |                                                                  /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsRepeatedStoreThenLoadSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.repeated_store_then_load --------------------------
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
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
      |                                                                  /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsStoreV2U32Sm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.store_v2_u32        --------------------------
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
      |.L_x_0:
      |        /*0078*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsStoreV2U32Sm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.store_v2_u32        --------------------------
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
      |.L_x_0:
      |        /*0078*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsStoreV2U64Sm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.store_v2_u64        --------------------------
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
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
      |                                                                  /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsStoreV2U64Sm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.store_v2_u64        --------------------------
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
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
      |                                                                  /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsStoreV4U32Sm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.store_v4_u32        --------------------------
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
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
      |                                                                  /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyGenericMemoryExtensionsStoreV4U32Sm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.store_v4_u32        --------------------------
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
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0) ;                /* 0xe2400fffff87000f */
      |        /*0098*/                   NOP;                           /* 0x50b0000000070f00 */
      |                                                                  /* 0x001f8000fc0007e0 */
      |        /*00a8*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP;                           /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP;                           /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val uniformAddressSm86Patched: String =
    """	.target	sm_86
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   ULDC.64 UR6, c[0x0][0x170] ;
      |        /*0010*/                   S2R R6, SR_TID.X ;
      |        /*0020*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x4 ;
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0040*/                   MOV R4, 0x12345678 ;
      |        /*0050*/                   ISETP.NE.AND P0, PT, R4, c[0x0][0x174], PT ;
      |        /*0060*/                   ISETP.EQ.OR P0, PT, R4, c[0x0][0x170], !P0 ;
      |        /*0070*/                   SHF.L.U32 R7, R6.reuse, 0x2, RZ ;
      |        /*0080*/                   IMAD.WIDE.U32 R2, R6, R5, c[0x0][0x168] ;
      |        /*0090*/                   IMAD.WIDE.U32 R4, R6, R5, c[0x0][0x160] ;
      |        /*00a0*/                   LDGSTS.E [R7], [R2.64+UR6] ;
      |        /*00b0*/                   LDGDEPBAR ;
      |        /*00c0*/                   LDGDEPBAR ;
      |        /*00d0*/                   DEPBAR.LE SB0, 0x0 ;
      |        /*00e0*/                   LDS R0, [R6.X4] ;
      |        /*00f0*/                   SEL R9, R0, RZ, !P0 ;
      |        /*0100*/                   STG.E [R4.64], R9 ;
      |        /*0110*/                   EXIT ;
      |.L_x_0:
      |        /*0120*/                   BRA `(.L_x_0);
      |        /*0130*/                   NOP;
      |        /*0140*/                   NOP;
      |        /*0150*/                   NOP;
      |        /*0160*/                   NOP;
      |        /*0170*/                   NOP;
      |        /*0180*/                   NOP;
      |        /*0190*/                   NOP;
      |        /*01a0*/                   NOP;
      |        /*01b0*/                   NOP;
      |        /*01c0*/                   NOP;
      |        /*01d0*/                   NOP;
      |        /*01e0*/                   NOP;
      |        /*01f0*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class GenericMemoryFixtures extends NvidiaFixtureSuite {
  import GenericMemoryFixtures.*

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericAddressGlobalLoadSm50",
        50,
        legacyGenericAddressProbePtxSource,
        legacyGenericAddressGlobalLoadSm50
      ),
      ("legacyGenericAddressGlobalLoadSm60", 60, legacyGenericAddressProbePtxSource, legacyGenericAddressGlobalLoadSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o probe-sm$sm.cubin probe.ptx",
      s"nvdisasm -c -ndf -hex -fun 13 probe-sm$sm.cubin > global-load-sm$sm.sass"
    ),
    output = s"global-load-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericAddressGlobalStoreSm50",
        50,
        legacyGenericAddressProbePtxSource,
        legacyGenericAddressGlobalStoreSm50
      ),
      (
        "legacyGenericAddressGlobalStoreSm60",
        60,
        legacyGenericAddressProbePtxSource,
        legacyGenericAddressGlobalStoreSm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o probe-sm$sm.cubin probe.ptx",
      s"nvdisasm -c -ndf -hex -fun 11 probe-sm$sm.cubin > global-store-sm$sm.sass"
    ),
    output = s"global-store-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("legacyGenericAddressLocalLoadSm50", 50, legacyGenericAddressProbePtxSource, legacyGenericAddressLocalLoadSm50),
      ("legacyGenericAddressLocalLoadSm60", 60, legacyGenericAddressProbePtxSource, legacyGenericAddressLocalLoadSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o probe-sm$sm.cubin probe.ptx",
      s"nvdisasm -c -ndf -hex -fun 3 probe-sm$sm.cubin > local-load-sm$sm.sass"
    ),
    output = s"local-load-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericAddressLocalStoreSm50",
        50,
        legacyGenericAddressProbePtxSource,
        legacyGenericAddressLocalStoreSm50
      ),
      ("legacyGenericAddressLocalStoreSm60", 60, legacyGenericAddressProbePtxSource, legacyGenericAddressLocalStoreSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o probe-sm$sm.cubin probe.ptx",
      s"nvdisasm -c -ndf -hex -fun 1 probe-sm$sm.cubin > local-store-sm$sm.sass"
    ),
    output = s"local-store-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericAddressSharedLoadSm50",
        50,
        legacyGenericAddressProbePtxSource,
        legacyGenericAddressSharedLoadSm50
      ),
      ("legacyGenericAddressSharedLoadSm60", 60, legacyGenericAddressProbePtxSource, legacyGenericAddressSharedLoadSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o probe-sm$sm.cubin probe.ptx",
      s"nvdisasm -c -ndf -hex -fun 8 probe-sm$sm.cubin > shared-load-sm$sm.sass"
    ),
    output = s"shared-load-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericAddressSharedStoreSm50",
        50,
        legacyGenericAddressProbePtxSource,
        legacyGenericAddressSharedStoreSm50
      ),
      (
        "legacyGenericAddressSharedStoreSm60",
        60,
        legacyGenericAddressProbePtxSource,
        legacyGenericAddressSharedStoreSm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o probe-sm$sm.cubin probe.ptx",
      s"nvdisasm -c -ndf -hex -fun 5 probe-sm$sm.cubin > shared-store-sm$sm.sass"
    ),
    output = s"shared-store-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsByteGlobalS8Sm50",
        50,
        legacyGenericMemoryExtensionsByteGenericPtxSource,
        legacyGenericMemoryExtensionsByteGlobalS8Sm50
      ),
      (
        "legacyGenericMemoryExtensionsByteGlobalS8Sm60",
        60,
        legacyGenericMemoryExtensionsByteGenericPtxSource,
        legacyGenericMemoryExtensionsByteGlobalS8Sm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("byte-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o byte-generic-sm$sm.cubin byte-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 8 byte-generic-sm$sm.cubin > byte-global-s8-sm$sm.sass"
    ),
    output = s"byte-global-s8-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsByteGlobalU8BarrierSm50",
        50,
        legacyGenericMemoryExtensionsByteGenericPtxSource,
        legacyGenericMemoryExtensionsByteGlobalU8BarrierSm50
      ),
      (
        "legacyGenericMemoryExtensionsByteGlobalU8BarrierSm60",
        60,
        legacyGenericMemoryExtensionsByteGenericPtxSource,
        legacyGenericMemoryExtensionsByteGlobalU8BarrierSm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("byte-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o byte-generic-sm$sm.cubin byte-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 6 byte-generic-sm$sm.cubin > byte-global-u8-barrier-sm$sm.sass"
    ),
    output = s"byte-global-u8-barrier-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsByteGlobalU8Sm50",
        50,
        legacyGenericMemoryExtensionsByteGenericPtxSource,
        legacyGenericMemoryExtensionsByteGlobalU8Sm50
      ),
      (
        "legacyGenericMemoryExtensionsByteGlobalU8Sm60",
        60,
        legacyGenericMemoryExtensionsByteGenericPtxSource,
        legacyGenericMemoryExtensionsByteGlobalU8Sm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("byte-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o byte-generic-sm$sm.cubin byte-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 10 byte-generic-sm$sm.cubin > byte-global-u8-sm$sm.sass"
    ),
    output = s"byte-global-u8-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsByteLocalU8Sm50",
        50,
        legacyGenericMemoryExtensionsByteGenericPtxSource,
        legacyGenericMemoryExtensionsByteLocalU8Sm50
      ),
      (
        "legacyGenericMemoryExtensionsByteLocalU8Sm60",
        60,
        legacyGenericMemoryExtensionsByteGenericPtxSource,
        legacyGenericMemoryExtensionsByteLocalU8Sm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("byte-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o byte-generic-sm$sm.cubin byte-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 1 byte-generic-sm$sm.cubin > byte-local-u8-sm$sm.sass"
    ),
    output = s"byte-local-u8-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsByteSharedU8Sm50",
        50,
        legacyGenericMemoryExtensionsByteGenericPtxSource,
        legacyGenericMemoryExtensionsByteSharedU8Sm50
      ),
      (
        "legacyGenericMemoryExtensionsByteSharedU8Sm60",
        60,
        legacyGenericMemoryExtensionsByteGenericPtxSource,
        legacyGenericMemoryExtensionsByteSharedU8Sm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("byte-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o byte-generic-sm$sm.cubin byte-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 3 byte-generic-sm$sm.cubin > byte-shared-u8-sm$sm.sass"
    ),
    output = s"byte-shared-u8-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsCompoundGlobalSm50",
        50,
        legacyGenericMemoryExtensionsCompoundSpacesPtxSource,
        legacyGenericMemoryExtensionsCompoundGlobalSm50
      ),
      (
        "legacyGenericMemoryExtensionsCompoundGlobalSm60",
        60,
        legacyGenericMemoryExtensionsCompoundSpacesPtxSource,
        legacyGenericMemoryExtensionsCompoundGlobalSm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("compound-spaces.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o compound-spaces-sm$sm.cubin compound-spaces.ptx",
      s"nvdisasm -c -ndf -hex -fun 6 compound-spaces-sm$sm.cubin > compound-global-sm$sm.sass"
    ),
    output = s"compound-global-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsCompoundLocalSm50",
        50,
        legacyGenericMemoryExtensionsCompoundSpacesPtxSource,
        legacyGenericMemoryExtensionsCompoundLocalSm50
      ),
      (
        "legacyGenericMemoryExtensionsCompoundLocalSm60",
        60,
        legacyGenericMemoryExtensionsCompoundSpacesPtxSource,
        legacyGenericMemoryExtensionsCompoundLocalSm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("compound-spaces.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o compound-spaces-sm$sm.cubin compound-spaces.ptx",
      s"nvdisasm -c -ndf -hex -fun 1 compound-spaces-sm$sm.cubin > compound-local-sm$sm.sass"
    ),
    output = s"compound-local-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsCompoundSharedSm50",
        50,
        legacyGenericMemoryExtensionsCompoundSpacesPtxSource,
        legacyGenericMemoryExtensionsCompoundSharedSm50
      ),
      (
        "legacyGenericMemoryExtensionsCompoundSharedSm60",
        60,
        legacyGenericMemoryExtensionsCompoundSpacesPtxSource,
        legacyGenericMemoryExtensionsCompoundSharedSm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("compound-spaces.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o compound-spaces-sm$sm.cubin compound-spaces.ptx",
      s"nvdisasm -c -ndf -hex -fun 3 compound-spaces-sm$sm.cubin > compound-shared-sm$sm.sass"
    ),
    output = s"compound-shared-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsLoadV2U32Sm50",
        50,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsLoadV2U32Sm50
      ),
      (
        "legacyGenericMemoryExtensionsLoadV2U32Sm60",
        60,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsLoadV2U32Sm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("vector-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o vector-generic-sm$sm.cubin vector-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 11 vector-generic-sm$sm.cubin > load-v2-u32-sm$sm.sass"
    ),
    output = s"load-v2-u32-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsLoadV2U64Sm50",
        50,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsLoadV2U64Sm50
      ),
      (
        "legacyGenericMemoryExtensionsLoadV2U64Sm60",
        60,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsLoadV2U64Sm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("vector-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o vector-generic-sm$sm.cubin vector-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 9 vector-generic-sm$sm.cubin > load-v2-u64-sm$sm.sass"
    ),
    output = s"load-v2-u64-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsLoadV4U32Sm50",
        50,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsLoadV4U32Sm50
      ),
      (
        "legacyGenericMemoryExtensionsLoadV4U32Sm60",
        60,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsLoadV4U32Sm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("vector-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o vector-generic-sm$sm.cubin vector-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 7 vector-generic-sm$sm.cubin > load-v4-u32-sm$sm.sass"
    ),
    output = s"load-v4-u32-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsRepeatedStoreThenLoadSm50",
        50,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsRepeatedStoreThenLoadSm50
      ),
      (
        "legacyGenericMemoryExtensionsRepeatedStoreThenLoadSm60",
        60,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsRepeatedStoreThenLoadSm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("vector-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o vector-generic-sm$sm.cubin vector-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 5 vector-generic-sm$sm.cubin > repeated-store-then-load-sm$sm.sass"
    ),
    output = s"repeated-store-then-load-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsStoreV2U32Sm50",
        50,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsStoreV2U32Sm50
      ),
      (
        "legacyGenericMemoryExtensionsStoreV2U32Sm60",
        60,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsStoreV2U32Sm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("vector-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o vector-generic-sm$sm.cubin vector-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 17 vector-generic-sm$sm.cubin > store-v2-u32-sm$sm.sass"
    ),
    output = s"store-v2-u32-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsStoreV2U64Sm50",
        50,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsStoreV2U64Sm50
      ),
      (
        "legacyGenericMemoryExtensionsStoreV2U64Sm60",
        60,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsStoreV2U64Sm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("vector-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o vector-generic-sm$sm.cubin vector-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 15 vector-generic-sm$sm.cubin > store-v2-u64-sm$sm.sass"
    ),
    output = s"store-v2-u64-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "legacyGenericMemoryExtensionsStoreV4U32Sm50",
        50,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsStoreV4U32Sm50
      ),
      (
        "legacyGenericMemoryExtensionsStoreV4U32Sm60",
        60,
        legacyGenericMemoryExtensionsVectorGenericPtxSource,
        legacyGenericMemoryExtensionsStoreV4U32Sm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("vector-generic.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm -o vector-generic-sm$sm.cubin vector-generic.ptx",
      s"nvdisasm -c -ndf -hex -fun 13 vector-generic-sm$sm.cubin > store-v4-u32-sm$sm.sass"
    ),
    output = s"store-v4-u32-sm$sm.sass",
    expected = expected
  )

  reproduces("uniformAddressSm86Patched")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "probe.cu" -> uniformAddressSm86ProbeCudaSource
    ),
    script = Vec(
      "nvcc -ccbin g++-11 -arch=sm_86 -cubin -o original.cubin probe.cu",
      "nvdisasm -c -ndf -hex original.cubin > original.sass",
      "readelf -SW original.cubin | awk '$2 == \".text.probe\" { print \"0x\" $5; exit }' > section-offset.txt",
      "section_offset=$(cat section-offset.txt)",
      "cp original.cubin patched.cubin",
      "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0x0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"00000a00ff017624000fe400078e00ff\"",
      "printf '%b' '\\0271\\0172\\0006\\0000\\0000\\0134\\0000\\0000\\0000\\0012\\0000\\0000\\0000\\0344\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0x0)) conv=notrunc status=none",
      "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0xa0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"0000000002077fae000fe8000b921844\"",
      "printf '%b' '\\0256\\0177\\0007\\0002\\0000\\0000\\0000\\0000\\0106\\0010\\0222\\0013\\0000\\0350\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0xa0)) conv=notrunc status=none",
      "nvdisasm -c -ndf patched.cubin > patched.sass"
    ),
    output = "patched.sass",
    expected = uniformAddressSm86Patched
  )

}
