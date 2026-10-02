package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object SgxtFixtures {
  val sgxtSm70ProbeSm70PtxSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry sgxt_probe(.param .u64 pOut, .param .u32 pValue, .param .u32 pWidth) {
      |  .reg .pred %p;
      |  .reg .b32 %r<8>;
      |  .reg .b64 %rd<2>;
      |  ld.param.u64 %rd0, [pOut];
      |  ld.param.u32 %r0, [pValue];
      |  ld.param.u32 %r1, [pWidth];
      |  mov.u32 %r2, %r1;
      |  mov.u32 %r4, %r0;
      |  bfe.u32 %r0, %r0, 0, %r1;
      |  bfe.u32 %r2, %r4, 0, %r2;
      |  bfe.s32 %r3, %r4, 0, %r1;
      |  bfe.u32 %r5, %r4, 0, 2;
      |  bfe.s32 %r6, %r4, 0, 3;
      |  st.global.u32 [%rd0], %r0;
      |  st.global.u32 [%rd0+4], %r2;
      |  st.global.u32 [%rd0+8], %r3;
      |  st.global.u32 [%rd0+12], %r5;
      |  st.global.u32 [%rd0+16], %r6;
      |  ret;
      |}
      |""".stripMargin

  val sgxtSm70ProbeRawWidthSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.sgxt_probe          --------------------------
      |	.section	.text.sgxt_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=14"
      |	.align	128
      |        .global         sgxt_probe
      |        .type           sgxt_probe,@function
      |        .size           sgxt_probe,(.L_x_1 - sgxt_probe)
      |        .other          sgxt_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |sgxt_probe:
      |.text.sgxt_probe:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;    /* 0x00000a00ff017624 */
      |                                                                              /* 0x000fc400078e00ff */
      |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;              /* 0x000000fffffff389 */
      |                                                                              /* 0x000fe200000e00ff */
      |        /*0020*/                   IMAD.MOV.U32 R12, RZ, RZ, c[0x0][0x16c] ;  /* 0x00005b00ff0c7624 */
      |                                                                              /* 0x000fe400078e00ff */
      |        /*0030*/                   IMAD.MOV.U32 R11, RZ, RZ, c[0x0][0x168] ;  /* 0x00005a00ff0b7624 */
      |                                                                              /* 0x000fe400078e00ff */
      |        /*0040*/                   IMAD.MOV.U32 R2, RZ, RZ, c[0x0][0x160] ;   /* 0x00005800ff027624 */
      |                                                                              /* 0x000fe200078e00ff */
      |        /*0050*/                   PRMT R0, R12, 0x7604, RZ ;                 /* 0x000076040c007816 */
      |                                                                              /* 0x000fe200000000ff */
      |        /*0060*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x164] ;   /* 0x00005900ff037624 */
      |                                                                              /* 0x000fe200078e00ff */
      |        /*0070*/                   SGXT.U32 R9, R11, 0x2 ;                    /* 0x000000020b09781a */
      |                                                                              /* 0x000fe40000000000 */
      |        /*0080*/                   PRMT R4, RZ, 0x4, R0.reuse ;               /* 0x00000004ff047816 */
      |                                                                              /* 0x100fe40000000000 */
      |        /*0090*/                   PRMT R6, RZ, 0x4, R0 ;                     /* 0x00000004ff067816 */
      |                                                                              /* 0x000fc40000000000 */
      |        /*00a0*/                   PRMT R5, RZ, 0x5, R0.reuse ;               /* 0x00000005ff057816 */
      |                                                                              /* 0x100fe40000000000 */
      |        /*00b0*/                   SHF.R.U32.HI R4, RZ, R4, R11.reuse ;       /* 0x00000004ff047219 */
      |                                                                              /* 0x100fe2000001160b */
      |        /*00c0*/                   STG.E.SYS [R2+0xc], R9 ;                   /* 0x00000c0902007386 */
      |                                                                              /* 0x000fe2000010e900 */
      |        /*00d0*/                   PRMT R7, RZ, 0x5, R0 ;                     /* 0x00000005ff077816 */
      |                                                                              /* 0x000fe40000000000 */
      |        /*00e0*/                   SHF.R.S32.HI R6, RZ, R6, R11 ;             /* 0x00000006ff067219 */
      |                                                                              /* 0x000fe4000001140b */
      |        /*00f0*/                   SGXT R11, R11, 0x3 ;                       /* 0x000000030b0b781a */
      |                                                                              /* 0x000fe40000000200 */
      |        /*0100*/                   SGXT.U32 R5, R4, R12 ;                     /* 0x0000000c0405721a */
      |                                                                              /* 0x000fc40000000000 */
      |        /*0110*/                   SGXT R7, R6, R12 ;                         /* 0x0000000c0607721a */
      |                                                                              /* 0x000fc80000000200 */
      |        /*0120*/                   STG.E.SYS [R2+0x10], R11 ;                 /* 0x0000100b02007386 */
      |                                                                              /* 0x000fe8000010e900 */
      |        /*0130*/                   STG.E.SYS [R2], R5 ;                       /* 0x0000000502007386 */
      |                                                                              /* 0x000fe8000010e900 */
      |        /*0140*/                   STG.E.SYS [R2+0x4], R5 ;                   /* 0x0000040502007386 */
      |                                                                              /* 0x000fe8000010e900 */
      |        /*0150*/                   STG.E.SYS [R2+0x8], R7 ;                   /* 0x0000080702007386 */
      |                                                                              /* 0x000fe2000010e900 */
      |        /*0160*/                   EXIT ;                                     /* 0x000000000000794d */
      |                                                                              /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0170*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |.L_x_1:
      |""".stripMargin

  val sgxtSm70ProbeSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.sgxt_probe          --------------------------
      |	.section	.text.sgxt_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=14"
      |	.align	128
      |        .global         sgxt_probe
      |        .type           sgxt_probe,@function
      |        .size           sgxt_probe,(.L_x_1 - sgxt_probe)
      |        .other          sgxt_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |sgxt_probe:
      |.text.sgxt_probe:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
      |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
      |        /*0020*/                   IMAD.MOV.U32 R0, RZ, RZ, c[0x0][0x16c] ;
      |        /*0030*/                   IMAD.MOV.U32 R11, RZ, RZ, c[0x0][0x168] ;
      |        /*0040*/                   IMAD.MOV.U32 R2, RZ, RZ, c[0x0][0x160] ;
      |        /*0050*/                   PRMT R0, R0, 0x7604, RZ ;
      |        /*0060*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x164] ;
      |        /*0070*/                   SGXT.U32 R9, R11, 0x2 ;
      |        /*0080*/                   PRMT R4, RZ, 0x4, R0.reuse ;
      |        /*0090*/                   PRMT R6, RZ, 0x4, R0 ;
      |        /*00a0*/                   PRMT R5, RZ, 0x5, R0.reuse ;
      |        /*00b0*/                   SHF.R.U32.HI R4, RZ, R4, R11.reuse ;
      |        /*00c0*/                   STG.E.SYS [R2+0xc], R9 ;
      |        /*00d0*/                   PRMT R7, RZ, 0x5, R0 ;
      |        /*00e0*/                   SHF.R.S32.HI R6, RZ, R6, R11 ;
      |        /*00f0*/                   SGXT R11, R11, 0x3 ;
      |        /*0100*/                   SGXT.U32 R5, R4, R5 ;
      |        /*0110*/                   SGXT R7, R6, R7 ;
      |        /*0120*/                   STG.E.SYS [R2+0x10], R11 ;
      |        /*0130*/                   STG.E.SYS [R2], R5 ;
      |        /*0140*/                   STG.E.SYS [R2+0x4], R5 ;
      |        /*0150*/                   STG.E.SYS [R2+0x8], R7 ;
      |        /*0160*/                   EXIT ;
      |.L_x_0:
      |        /*0170*/                   BRA `(.L_x_0);
      |.L_x_1:
      |""".stripMargin

}

class SgxtFixtures extends NvidiaFixtureSuite {
  import SgxtFixtures.*

  reproduces("sgxtSm70ProbeRawWidthSm70")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "probe-sm70.ptx" -> sgxtSm70ProbeSm70PtxSource
    ),
    script = Vec(
      "nvcc -ccbin g++-11 -cubin -arch=sm_70 probe-sm70.ptx -o probe-sm70.cubin",
      "text_offset=$(readelf -SW probe-sm70.cubin | awk '{for(i=1;i<=NF;i++) if($i==\".text.sgxt_probe\"){print \"0x\"$(i+3);exit}}'); perl -e '$b=hex(shift); local $/; $x=<>; for $p ([0x20,0xff<<16,12<<16],[0x50,0xff<<24,12<<24],[0x100,0xff<<32,12<<32],[0x110,0xff<<32,12<<32]) {$o=$b+$p->[0]; $w=unpack(\"Q<\",substr($x,$o,8)); substr($x,$o,8,pack(\"Q<\",($w & ~$p->[1]) | $p->[2]));} print $x' \"$text_offset\" probe-sm70.cubin > probe-raw-width-sm70.cubin",
      "nvdisasm -c -ndf -hex probe-raw-width-sm70.cubin > probe-raw-width-sm70.sass"
    ),
    output = "probe-raw-width-sm70.sass",
    expected = sgxtSm70ProbeRawWidthSm70
  )

  reproduces("sgxtSm70ProbeSm70")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "probe-sm70.ptx" -> sgxtSm70ProbeSm70PtxSource
    ),
    script = Vec(
      "nvcc -ccbin g++-11 -cubin -arch=sm_70 probe-sm70.ptx -o probe-sm70.cubin",
      "nvdisasm -c -ndf probe-sm70.cubin > probe-sm70.sass"
    ),
    output = "probe-sm70.sass",
    expected = sgxtSm70ProbeSm70
  )

}
