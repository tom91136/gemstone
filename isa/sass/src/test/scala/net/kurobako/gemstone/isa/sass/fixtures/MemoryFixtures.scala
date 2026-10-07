package net.kurobako.gemstone.isa.sass.fixtures

object MemoryFixtures {
  val atomicAddCuda: String =
    """extern "C" __global__ void atomic_add(unsigned *counter, unsigned *previous) {
      |  previous[blockIdx.x * blockDim.x + threadIdx.x] = atomicAdd(counter, blockIdx.x + 1);
      |}
      |""".stripMargin

  val cacheInvalidatePtx: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry cache_probe(.param .u64 out, .param .u64 input) {
      |.reg .u64 %rd<3>;
      |.reg .u32 %r<3>;
      |ld.param.u64 %rd0,[out];
      |ld.param.u64 %rd1,[input];
      |atom.global.add.u32 %r0,[%rd1],7;
      |ld.global.u32 %r1,[%rd1];
      |st.global.u32 [%rd0],%r0;
      |st.global.u32 [%rd0+4],%r1;
      |ret;
      |}
      |""".stripMargin

  def stgU8UniformBasePtx(sm: Int): String =
    s""".version 8.7
      |.target sm_$sm
      |.address_size 64
      |.visible .entry stg_u8_sys_u32_ur_probe(
      | .param .u64 dummy0,
      | .param .u64 dummy1,
      | .param .u64 output,
      | .param .u32 count
      |) {
      | .reg .pred %p<2>;
      | .reg .b32 %r<6>;
      | .reg .b64 %rd<5>;
      | ld.param.u64 %rd1, [output];
      | cvta.to.global.u64 %rd2, %rd1;
      | ld.param.u32 %r1, [count];
      | mov.u32 %r2, %tid.x;
      | setp.ge.u32 %p1, %r2, %r1;
      | @%p1 bra DONE;
      | cvt.u64.u32 %rd3, %r2;
      | add.u64 %rd4, %rd2, %rd3;
      | mov.u32 %r5, %r2;
      | st.relaxed.sys.global.u8 [%rd4], %r5;
      |DONE:
      | ret;
      |}
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "atomicAddSm86" ->
      """//--------------------- .text.atomic_add          --------------------------
        |	.section	.text.atomic_add,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         atomic_add
        |        .type           atomic_add,@function
        |        .size           atomic_add,(.L_x_1 - atomic_add)
        |        .other          atomic_add,@"STO_CUDA_ENTRY STV_DEFAULT"
        |atomic_add:
        |.text.atomic_add:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R3, SR_LANEID ;
        |        /*0020*/                   VOTEU.ANY UR6, UPT, PT ;
        |        /*0030*/                   MOV R2, c[0x0][0x160] ;
        |        /*0040*/                   FLO.U32 R6, UR6 ;
        |        /*0050*/                   S2R R4, SR_CTAID.X ;
        |        /*0060*/                   UPOPC UR4, UR6 ;
        |        /*0070*/                   ISETP.EQ.U32.AND P0, PT, R6, R3, PT ;
        |        /*0080*/                   MOV R3, c[0x0][0x164] ;
        |        /*0090*/                   IADD3 R0, R4, 0x1, RZ ;
        |        /*00a0*/                   IMAD R9, R0, UR4, RZ ;
        |        /*00b0*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*00c0*/               @P0 ATOMG.E.ADD.STRONG.GPU PT, R3, [R2.64], R9 ;
        |        /*00d0*/                   MOV R11, 0x4 ;
        |        /*00e0*/                   S2R R8, SR_LTMASK ;
        |        /*00f0*/                   S2R R5, SR_TID.X ;
        |        /*0100*/                   LOP3.LUT R8, R8, UR6, RZ, 0xc0, !PT ;
        |        /*0110*/                   IMAD R4, R4, c[0x0][0x0], R5 ;
        |        /*0120*/                   POPC R8, R8 ;
        |        /*0130*/                   IMAD.WIDE.U32 R4, R4, R11, c[0x0][0x168] ;
        |        /*0140*/                   SHFL.IDX PT, R7, R3, R6, 0x1f ;
        |        /*0150*/                   IMAD R7, R0, R8, R7 ;
        |        /*0160*/                   STG.E [R4.64], R7 ;
        |        /*0170*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "cacheInvalidateSm35" ->
      """//--------------------- .text.cache_probe         --------------------------
        |	.section	.text.cache_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	64
        |        .global         cache_probe
        |        .type           cache_probe,@function
        |        .size           cache_probe,(.L_x_1 - cache_probe)
        |        .other          cache_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |cache_probe:
        |.text.cache_probe:
        |                                                                         /* 0x08a0109c108010c0 */
        |        /*0008*/                   MOV R1, c[0x0][0x44];                 /* 0x64c03c00089c0006 */
        |        /*0010*/                   VOTE.ANY R0, PT, PT;                  /* 0x86cf1c00001c0002 */
        |        /*0018*/                   MOV R2, c[0x0][0x148];                /* 0x64c03c00291c000a */
        |        /*0020*/                   S2R R3, SR_LANEID;                    /* 0x86400000001c000e */
        |        /*0028*/                   MOV R5, c[0x0][0x144];                /* 0x64c03c00289c0016 */
        |        /*0030*/                   FLO.U32 R8, R0;                       /* 0xe1800000001c0022 */
        |        /*0038*/                   POPC R4, R0, R0;                      /* 0xe0400000001c0012 */
        |                                                                         /* 0x08b810b810ac1080 */
        |        /*0048*/                   ISETP.EQ.U32.AND P0, PT, R8, R3, PT;  /* 0xdb201c00019c201e */
        |        /*0050*/                   IMUL32I R6, R4, 0x7;                  /* 0x2e000000039c101a */
        |        /*0058*/                   MOV R3, c[0x0][0x14c];                /* 0x64c03c00299c000e */
        |        /*0060*/               @P0 ATOM.E.ADD R6, [R2], R6;              /* 0x680800000300081a */
        |        /*0068*/                   S2R R4, SR_LTMASK;                    /* 0x864000001c9c0012 */
        |        /*0070*/                   CCTL.E.IV [R2];                       /* 0x7b800000001c0816 */
        |        /*0078*/                   LOP.AND R4, R4, R0;                   /* 0xe2000000001c1012 */
        |                                                                         /* 0x0880b810f4fc8010 */
        |        /*0088*/                   LD.E R7, [R2];                        /* 0xc4800000001c081c */
        |        /*0090*/                   POPC R9, R4, R4;                      /* 0xe0400000021c1026 */
        |        /*0098*/                   MOV R4, c[0x0][0x140];                /* 0x64c03c00281c0012 */
        |        /*00a0*/                   SHFL.IDX PT, R8, R6, R8, 0x1f;        /* 0x78b803e1041c1822 */
        |        /*00a8*/                   IMAD R0, R9, 0x7, R8;                 /* 0xa1082000039c2401 */
        |        /*00b0*/                   ST.E [R4+0x4], R7;                    /* 0xe4800000021c101c */
        |        /*00b8*/                   ST.E [R4], R0;                        /* 0xe4800000001c1000 */
        |                                                                         /* 0x08000000000000bc */
        |        /*00c8*/                   EXIT;                                 /* 0x18000000001c003c */
        |.L_x_1:
        |""".stripMargin,
    "stgU8UniformBaseSm75Patched" ->
      """//--------------------- .text.stg_u8_sys_u32_ur_probe --------------------------
        |	.section	.text.stg_u8_sys_u32_ur_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         stg_u8_sys_u32_ur_probe
        |        .type           stg_u8_sys_u32_ur_probe,@function
        |        .size           stg_u8_sys_u32_ur_probe,(.L_x_1 - stg_u8_sys_u32_ur_probe)
        |        .other          stg_u8_sys_u32_ur_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |stg_u8_sys_u32_ur_probe:
        |.text.stg_u8_sys_u32_ur_probe:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;           /* 0x00000a00ff017624 */
        |                                                                                     /* 0x000fe400078e00ff */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                /* 0x0000000000007919 */
        |                                                                                     /* 0x000e240000002100 */
        |        /*0020*/                   ISETP.GE.U32.AND P0, PT, R0, c[0x0][0x178], PT ;  /* 0x00005e0000007a0c */
        |                                                                                     /* 0x001fd80003f06070 */
        |        /*0030*/               @P0 EXIT ;                                            /* 0x000000000000094d */
        |                                                                                     /* 0x000fea0003800000 */
        |        /*0040*/                   MOV R3, R0 ;                                      /* 0x0000000000037202 */
        |                                                                                     /* 0x000fca0000000f00 */
        |        /*0050*/                   ULDC.64 UR4, c[0x0][0x170] ;                      /* 0x00005c0000047ab9 */
        |                                                                                     /* 0x000fd00000000a00 */
        |        /*0060*/                   STG.E.U8.SYS [R0.U32+UR4], R3 ;                   /* 0x0000000300007986 */
        |                                                                                     /* 0x000fe2000810e104 */
        |        /*0070*/                   EXIT ;                                            /* 0x000000000000794d */
        |                                                                                     /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "stgU8UniformBaseSm89" ->
      """//--------------------- .text.stg_u8_sys_u32_ur_probe --------------------------
        |	.section	.text.stg_u8_sys_u32_ur_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         stg_u8_sys_u32_ur_probe
        |        .type           stg_u8_sys_u32_ur_probe,@function
        |        .size           stg_u8_sys_u32_ur_probe,(.L_x_1 - stg_u8_sys_u32_ur_probe)
        |        .other          stg_u8_sys_u32_ur_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |stg_u8_sys_u32_ur_probe:
        |.text.stg_u8_sys_u32_ur_probe:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R5, SR_TID.X ;
        |        /*0020*/                   ISETP.GE.U32.AND P0, PT, R5, c[0x0][0x178], PT ;
        |        /*0030*/               @P0 EXIT ;
        |        /*0040*/                   IADD3 R2, P0, R5, c[0x0][0x170], RZ ;
        |        /*0050*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0060*/                   IMAD.X R3, RZ, RZ, c[0x0][0x174], P0 ;
        |        /*0070*/                   STG.E.U8.STRONG.SYS [R2.64], R5 ;
        |        /*0080*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin
  )
}
