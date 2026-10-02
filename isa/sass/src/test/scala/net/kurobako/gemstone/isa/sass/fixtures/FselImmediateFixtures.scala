package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object FselImmediateFixtures {
  val fselImmediateSm86ProbePtxSource: String =
    """.version 8.0
      |.target sm_86
      |.address_size 64
      |.visible .entry probe(.param .u64 output) {
      |.reg .b32 r<2>;
      |.reg .b64 d<2>;
      |.reg .f32 f<2>;
      |.reg .pred p;
      |ld.param.u64 d0,[output];
      |mov.u32 r0,%tid.x;
      |setp.eq.u32 p,r0,0;
      |selp.f32 f0,0f3f800000,0f7ff80000,p;
      |selp.f32 f1,0f3f800000,0f7fc00000,p;
      |mad.wide.u32 d1,r0,8,d0;
      |st.global.v2.f32 [d1],{f0,f1};
      |ret;
      |}
      |""".stripMargin

  val fselImmediateSm86Probe: String =
    """	.target	sm_86
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;        /* 0x00000a00ff017624 */
      |                                                                                  /* 0x000fe400078e00ff */
      |        /*0010*/                   S2R R0, SR_TID.X ;                             /* 0x0000000000007919 */
      |                                                                                  /* 0x000e220000002100 */
      |        /*0020*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x3f800000 ;          /* 0x3f800000ff057424 */
      |                                                                                  /* 0x000fe200078e00ff */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                   /* 0x0000460000047ab9 */
      |                                                                                  /* 0x000fe20000000a00 */
      |        /*0040*/                   ISETP.NE.U32.AND P0, PT, R0.reuse, RZ, PT ;    /* 0x000000ff0000720c */
      |                                                                                  /* 0x041fe40003f05070 */
      |        /*0050*/                   LEA R2, P1, R0.reuse, c[0x0][0x160], 0x3 ;     /* 0x0000580000027a11 */
      |                                                                                  /* 0x040fe400078218ff */
      |        /*0060*/                   FSEL R4, R5.reuse, +QNAN , !P0 ;               /* 0x7ff8000005047808 */
      |                                                                                  /* 0x040fe40004000000 */
      |        /*0070*/                   LEA.HI.X R3, R0, c[0x0][0x164], RZ, 0x3, P1 ;  /* 0x0000590000037a11 */
      |                                                                                  /* 0x000fe400008f1cff */
      |        /*0080*/                   FSEL R5, R5, +QNAN , !P0 ;                     /* 0x7fc0000005057808 */
      |                                                                                  /* 0x000fca0004000000 */
      |        /*0090*/                   STG.E.64 [R2.64], R4 ;                         /* 0x0000000402007986 */
      |                                                                                  /* 0x000fe2000c101b04 */
      |        /*00a0*/                   EXIT ;                                         /* 0x000000000000794d */
      |                                                                                  /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00b0*/                   BRA `(.L_x_0);                                 /* 0xfffffff000007947 */
      |                                                                                  /* 0x000fc0000383ffff */
      |        /*00c0*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |        /*00d0*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |        /*0100*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |        /*0110*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                           /* 0x0000000000007918 */
      |                                                                                  /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

}

class FselImmediateFixtures extends NvidiaFixtureSuite {
  import FselImmediateFixtures.*

  reproduces("fselImmediateSm86Probe")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.ptx" -> fselImmediateSm86ProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_86 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = fselImmediateSm86Probe
  )

}
