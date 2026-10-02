package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object BmovFixtures {
  val bmovClearSm70ProbeCudaSource: String =
    """extern "C" __global__ void divergent_loop(unsigned *out, unsigned limit) {
      |    unsigned tid = threadIdx.x;
      |    unsigned value = 0;
      |    while (value < limit) {
      |        if ((tid & 1u) != 0u) break;
      |        ++value;
      |    }
      |    unsigned second = limit + 2u;
      |    while (value < second) {
      |        if ((tid & 2u) != 0u) break;
      |        ++value;
      |    }
      |    out[tid] = value;
      |}
      |""".stripMargin

  val bmovClearSm70ProbeSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.divergent_loop      --------------------------
      |	.section	.text.divergent_loop,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	128
      |        .global         divergent_loop
      |        .type           divergent_loop,@function
      |        .size           divergent_loop,(.L_x_7 - divergent_loop)
      |        .other          divergent_loop,@"STO_CUDA_ENTRY STV_DEFAULT"
      |divergent_loop:
      |.text.divergent_loop:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;           /* 0x00000a00ff017624 */
      |                                                                                     /* 0x000fc400078e00ff */
      |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                     /* 0x000000fffffff389 */
      |                                                                                     /* 0x000fe200000e00ff */
      |        /*0020*/                   S2R R2, SR_TID.X ;                                /* 0x0000000000027919 */
      |                                                                                     /* 0x000e220000002100 */
      |        /*0030*/                   ISETP.NE.AND P0, PT, RZ, c[0x0][0x168], PT ;      /* 0x00005a00ff007a0c */
      |                                                                                     /* 0x000fe20003f05270 */
      |        /*0040*/                   IMAD.MOV.U32 R4, RZ, RZ, c[0x0][0x168] ;          /* 0x00005a00ff047624 */
      |                                                                                     /* 0x000fe200078e00ff */
      |        /*0050*/                   BMOV.32.CLEAR RZ, B0 ;                            /* 0x0000000000ff7355 */
      |                                                                                     /* 0x000fe20000100000 */
      |        /*0060*/                   BSSY B0, `(.L_x_0) ;                              /* 0x000000c000007945 */
      |                                                                                     /* 0x000fe20003800000 */
      |        /*0070*/                   IMAD.MOV.U32 R5, RZ, RZ, RZ ;                     /* 0x000000ffff057224 */
      |                                                                                     /* 0x000fe400078e00ff */
      |        /*0080*/                   IADD3 R4, R4, 0x2, RZ ;                           /* 0x0000000204047810 */
      |                                                                                     /* 0x000fe40007ffe0ff */
      |        /*0090*/                   LOP3.LUT R0, R2, 0x1, RZ, 0xc0, !PT ;             /* 0x0000000102007812 */
      |                                                                                     /* 0x001fc800078ec0ff */
      |        /*00a0*/                   ISETP.EQ.U32.OR P0, PT, R0, 0x1, !P0 ;            /* 0x000000010000780c */
      |                                                                                     /* 0x000fe40004702470 */
      |        /*00b0*/                   SHF.R.U32.HI R0, RZ, 0x1, R2 ;                    /* 0x00000001ff007819 */
      |                                                                                     /* 0x000fc80000011602 */
      |        /*00c0*/                   LOP3.LUT R0, R0, 0x1, RZ, 0xc0, !PT ;             /* 0x0000000100007812 */
      |                                                                                     /* 0x000fcc00078ec0ff */
      |        /*00d0*/               @P0 BRA `(.L_x_1) ;                                   /* 0x0000004000000947 */
      |                                                                                     /* 0x000fea0003800000 */
      |.L_x_2:
      |        /*00e0*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                     /* 0x000000fffffff389 */
      |                                                                                     /* 0x000fe200000e00ff */
      |        /*00f0*/                   IADD3 R5, R5, 0x1, RZ ;                           /* 0x0000000105057810 */
      |                                                                                     /* 0x000fc80007ffe0ff */
      |        /*0100*/                   ISETP.GE.U32.AND P0, PT, R5, c[0x0][0x168], PT ;  /* 0x00005a0005007a0c */
      |                                                                                     /* 0x000fd80003f06070 */
      |        /*0110*/              @!P0 BRA `(.L_x_2) ;                                   /* 0xffffffc000008947 */
      |                                                                                     /* 0x000fea000383ffff */
      |.L_x_1:
      |        /*0120*/                   BSYNC B0 ;                                        /* 0x0000000000007941 */
      |                                                                                     /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0130*/                   ISETP.GE.U32.AND P0, PT, R5, R4, PT ;             /* 0x000000040500720c */
      |                                                                                     /* 0x000fe20003f06070 */
      |        /*0140*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x4 ;                    /* 0x00000004ff037424 */
      |                                                                                     /* 0x000fe200078e00ff */
      |        /*0150*/                   BMOV.32.CLEAR RZ, B0 ;                            /* 0x0000000000ff7355 */
      |                                                                                     /* 0x000fe20000100000 */
      |        /*0160*/                   BSSY B0, `(.L_x_3) ;                              /* 0x0000008000007945 */
      |                                                                                     /* 0x000fe20003800000 */
      |        /*0170*/                   ISETP.EQ.U32.OR P0, PT, R0, 0x1, P0 ;             /* 0x000000010000780c */
      |                                                                                     /* 0x000fe20000702470 */
      |        /*0180*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;         /* 0x0000580002027625 */
      |                                                                                     /* 0x000fd600078e0003 */
      |        /*0190*/               @P0 BRA `(.L_x_4) ;                                   /* 0x0000004000000947 */
      |                                                                                     /* 0x000fea0003800000 */
      |.L_x_5:
      |        /*01a0*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                     /* 0x000000fffffff389 */
      |                                                                                     /* 0x000fe200000e00ff */
      |        /*01b0*/                   IADD3 R5, R5, 0x1, RZ ;                           /* 0x0000000105057810 */
      |                                                                                     /* 0x000fc80007ffe0ff */
      |        /*01c0*/                   ISETP.GE.U32.AND P0, PT, R5, R4, PT ;             /* 0x000000040500720c */
      |                                                                                     /* 0x000fd80003f06070 */
      |        /*01d0*/              @!P0 BRA `(.L_x_5) ;                                   /* 0xffffffc000008947 */
      |                                                                                     /* 0x000fea000383ffff */
      |.L_x_4:
      |        /*01e0*/                   BSYNC B0 ;                                        /* 0x0000000000007941 */
      |                                                                                     /* 0x000fea0003800000 */
      |.L_x_3:
      |        /*01f0*/                   STG.E.SYS [R2], R5 ;                              /* 0x0000000502007386 */
      |                                                                                     /* 0x000fe2000010e900 */
      |        /*0200*/                   EXIT ;                                            /* 0x000000000000794d */
      |                                                                                     /* 0x000fea0003800000 */
      |.L_x_6:
      |        /*0210*/                   BRA `(.L_x_6);                                    /* 0xfffffff000007947 */
      |                                                                                     /* 0x000fc0000383ffff */
      |        /*0220*/                   NOP;                                              /* 0x0000000000007918 */
      |                                                                                     /* 0x000fc00000000000 */
      |        /*0230*/                   NOP;                                              /* 0x0000000000007918 */
      |                                                                                     /* 0x000fc00000000000 */
      |        /*0240*/                   NOP;                                              /* 0x0000000000007918 */
      |                                                                                     /* 0x000fc00000000000 */
      |        /*0250*/                   NOP;                                              /* 0x0000000000007918 */
      |                                                                                     /* 0x000fc00000000000 */
      |        /*0260*/                   NOP;                                              /* 0x0000000000007918 */
      |                                                                                     /* 0x000fc00000000000 */
      |        /*0270*/                   NOP;                                              /* 0x0000000000007918 */
      |                                                                                     /* 0x000fc00000000000 */
      |.L_x_7:
      |""".stripMargin

  val bmovClearSm75ProbeSm75: String =
    """	.target	sm_75
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.divergent_loop      --------------------------
      |	.section	.text.divergent_loop,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	128
      |        .global         divergent_loop
      |        .type           divergent_loop,@function
      |        .size           divergent_loop,(.L_x_7 - divergent_loop)
      |        .other          divergent_loop,@"STO_CUDA_ENTRY STV_DEFAULT"
      |divergent_loop:
      |.text.divergent_loop:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
      |        /*0010*/                   S2R R2, SR_TID.X ;
      |        /*0020*/                   ISETP.NE.AND P0, PT, RZ, c[0x0][0x168], PT ;
      |        /*0030*/                   BMOV.32.CLEAR RZ, B0 ;
      |        /*0040*/                   BSSY B0, `(.L_x_0) ;
      |        /*0050*/                   IMAD.MOV.U32 R5, RZ, RZ, RZ ;
      |        /*0060*/                   LOP3.LUT R0, R2, 0x1, RZ, 0xc0, !PT ;
      |        /*0070*/                   ISETP.EQ.U32.OR P0, PT, R0, 0x1, !P0 ;
      |        /*0080*/               @P0 BRA `(.L_x_1) ;
      |        /*0090*/                   IMAD.MOV.U32 R5, RZ, RZ, RZ ;
      |.L_x_2:
      |        /*00a0*/                   IADD3 R5, R5, 0x1, RZ ;
      |        /*00b0*/                   ISETP.GE.U32.AND P0, PT, R5, c[0x0][0x168], PT ;
      |        /*00c0*/              @!P0 BRA `(.L_x_2) ;
      |.L_x_1:
      |        /*00d0*/                   BSYNC B0 ;
      |.L_x_0:
      |        /*00e0*/                   ULDC UR4, c[0x0][0x168] ;
      |        /*00f0*/                   SHF.R.U32.HI R0, RZ, 0x1, R2 ;
      |        /*0100*/                   UIADD3 UR4, UR4, 0x2, URZ ;
      |        /*0110*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x4 ;
      |        /*0120*/                   BMOV.32.CLEAR RZ, B0 ;
      |        /*0130*/                   LOP3.LUT R0, R0, 0x1, RZ, 0xc0, !PT ;
      |        /*0140*/                   BSSY B0, `(.L_x_3) ;
      |        /*0150*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;
      |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R5, UR4, PT ;
      |        /*0170*/                   ISETP.EQ.U32.OR P0, PT, R0, 0x1, P0 ;
      |        /*0180*/               @P0 BRA `(.L_x_4) ;
      |.L_x_5:
      |        /*0190*/                   IADD3 R5, R5, 0x1, RZ ;
      |        /*01a0*/                   ISETP.GE.U32.AND P0, PT, R5, UR4, PT ;
      |        /*01b0*/              @!P0 BRA `(.L_x_5) ;
      |.L_x_4:
      |        /*01c0*/                   BSYNC B0 ;
      |.L_x_3:
      |        /*01d0*/                   STG.E.SYS [R2], R5 ;
      |        /*01e0*/                   EXIT ;
      |.L_x_6:
      |        /*01f0*/                   BRA `(.L_x_6);
      |.L_x_7:
      |""".stripMargin

  val bmovClearSm75ProbeSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.divergent_loop      --------------------------
      |	.section	.text.divergent_loop,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	128
      |        .global         divergent_loop
      |        .type           divergent_loop,@function
      |        .size           divergent_loop,(.L_x_7 - divergent_loop)
      |        .other          divergent_loop,@"STO_CUDA_ENTRY STV_DEFAULT"
      |divergent_loop:
      |.text.divergent_loop:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
      |        /*0010*/                   S2R R2, SR_TID.X ;
      |        /*0020*/                   ISETP.NE.AND P0, PT, RZ, c[0x0][0x168], PT ;
      |        /*0030*/                   BSSY B0, `(.L_x_0) ;
      |        /*0040*/                   IMAD.MOV.U32 R5, RZ, RZ, RZ ;
      |        /*0050*/                   LOP3.LUT R0, R2, 0x1, RZ, 0xc0, !PT ;
      |        /*0060*/                   ISETP.EQ.U32.OR P0, PT, R0, 0x1, !P0 ;
      |        /*0070*/               @P0 BRA `(.L_x_1) ;
      |        /*0080*/                   IMAD.MOV.U32 R5, RZ, RZ, RZ ;
      |.L_x_2:
      |        /*0090*/                   IADD3 R5, R5, 0x1, RZ ;
      |        /*00a0*/                   ISETP.GE.U32.AND P0, PT, R5, c[0x0][0x168], PT ;
      |        /*00b0*/              @!P0 BRA `(.L_x_2) ;
      |.L_x_1:
      |        /*00c0*/                   BSYNC B0 ;
      |.L_x_0:
      |        /*00d0*/                   ULDC UR4, c[0x0][0x168] ;
      |        /*00e0*/                   SHF.R.U32.HI R0, RZ, 0x1, R2 ;
      |        /*00f0*/                   UIADD3 UR4, UR4, 0x2, URZ ;
      |        /*0100*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x4 ;
      |        /*0110*/                   ULDC.64 UR6, c[0x0][0x118] ;
      |        /*0120*/                   LOP3.LUT R0, R0, 0x1, RZ, 0xc0, !PT ;
      |        /*0130*/                   BSSY B0, `(.L_x_3) ;
      |        /*0140*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;
      |        /*0150*/                   ISETP.GE.U32.AND P0, PT, R5, UR4, PT ;
      |        /*0160*/                   ISETP.EQ.U32.OR P0, PT, R0, 0x1, P0 ;
      |        /*0170*/               @P0 BRA `(.L_x_4) ;
      |.L_x_5:
      |        /*0180*/                   IADD3 R5, R5, 0x1, RZ ;
      |        /*0190*/                   ISETP.GE.U32.AND P0, PT, R5, UR4, PT ;
      |        /*01a0*/              @!P0 BRA `(.L_x_5) ;
      |.L_x_4:
      |        /*01b0*/                   BSYNC B0 ;
      |.L_x_3:
      |        /*01c0*/                   STG.E [R2.64], R5 ;
      |        /*01d0*/                   EXIT ;
      |.L_x_6:
      |        /*01e0*/                   BRA `(.L_x_6);
      |        /*01f0*/                   NOP;
      |        /*0200*/                   NOP;
      |        /*0210*/                   NOP;
      |        /*0220*/                   NOP;
      |        /*0230*/                   NOP;
      |        /*0240*/                   NOP;
      |        /*0250*/                   NOP;
      |        /*0260*/                   NOP;
      |        /*0270*/                   NOP;
      |.L_x_7:
      |""".stripMargin

}

class BmovFixtures extends NvidiaFixtureSuite {
  import BmovFixtures.*

  for (
    (name, sm, source, expected) <- Vec(
      ("bmovClearSm75ProbeSm75", 75, bmovClearSm70ProbeCudaSource, bmovClearSm75ProbeSm75),
      ("bmovClearSm75ProbeSm89", 89, bmovClearSm70ProbeCudaSource, bmovClearSm75ProbeSm89)
    )
  ) reproduces(name)(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec("input.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm input.cu -o kernel.cubin",
      "nvdisasm -c -ndf kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = expected
  )

  reproduces("bmovClearSm70ProbeSm70")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "probe.cu" -> bmovClearSm70ProbeCudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -O2 -arch=sm_70 -cubin probe.cu -o probe-sm70.cubin",
      "nvdisasm -c -hex probe-sm70.cubin > probe-sm70.sass"
    ),
    output = "probe-sm70.sass",
    expected = bmovClearSm70ProbeSm70
  )

}
