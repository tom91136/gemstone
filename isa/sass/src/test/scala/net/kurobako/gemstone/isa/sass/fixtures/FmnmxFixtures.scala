package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object FmnmxFixtures {
  val fmnmxSm86ProbePtxSource: String =
    """.version 8.0
      |.target sm_86
      |.address_size 64
      |.visible .entry probe(.param .u64 output, .param .u64 input_a, .param .u64 input_b) {
      |.reg .b32 %r<2>;
      |.reg .b64 %rd<4>;
      |.reg .f32 %f<4>;
      |.reg .pred %p;
      |ld.param.u64 %rd0, [output];
      |ld.param.u64 %rd1, [input_a];
      |ld.param.u64 %rd2, [input_b];
      |mov.u32 %r0, %tid.x;
      |mad.wide.u32 %rd3, %r0, 4, %rd1;
      |ld.global.b32 %r1, [%rd3];
      |mov.b32 %f0, %r1;
      |mad.wide.u32 %rd3, %r0, 4, %rd2;
      |ld.global.b32 %r1, [%rd3];
      |mov.b32 %f1, %r1;
      |min.f32 %f2, %f0, %f1;
      |max.f32 %f3, %f0, %f1;
      |mad.wide.u32 %rd3, %r0, 8, %rd0;
      |st.global.b32 [%rd3], %f2;
      |add.u64 %rd3, %rd3, 4;
      |st.global.b32 [%rd3], %f3;
      |ret;
      |}
      |""".stripMargin

  val fmnmxSm86Probe: String =
    """	.target	sm_86
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=14"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;             /* 0x00000a00ff017624 */
      |                                                                                       /* 0x000fe400078e00ff */
      |        /*0010*/                   S2R R3, SR_TID.X ;                                  /* 0x0000000000037919 */
      |                                                                                       /* 0x000e220000002100 */
      |        /*0020*/                   ULDC.64 UR4, c[0x0][0x118] ;                        /* 0x0000460000047ab9 */
      |                                                                                       /* 0x000fe20000000a00 */
      |        /*0030*/                   IMAD.SHL.U32 R6, R3.reuse, 0x4, RZ ;                /* 0x0000000403067824 */
      |                                                                                       /* 0x041fe400078e00ff */
      |        /*0040*/                   IMAD.HI.U32 R0, R3, 0x4, RZ ;                       /* 0x0000000403007827 */
      |                                                                                       /* 0x000fc600078e00ff */
      |        /*0050*/                   IADD3 R4, P0, R6.reuse, c[0x0][0x168], RZ ;         /* 0x00005a0006047a10 */
      |                                                                                       /* 0x040fe40007f1e0ff */
      |        /*0060*/                   IADD3 R6, P1, R6, c[0x0][0x170], RZ ;               /* 0x00005c0006067a10 */
      |                                                                                       /* 0x000fe40007f3e0ff */
      |        /*0070*/                   IADD3.X R5, R0.reuse, c[0x0][0x16c], RZ, P0, !PT ;  /* 0x00005b0000057a10 */
      |                                                                                       /* 0x040fe400007fe4ff */
      |        /*0080*/                   IADD3.X R7, R0, c[0x0][0x174], RZ, P1, !PT ;        /* 0x00005d0000077a10 */
      |                                                                                       /* 0x000fc60000ffe4ff */
      |        /*0090*/                   LDG.E R4, [R4.64] ;                                 /* 0x0000000404047981 */
      |                                                                                       /* 0x000ea8000c1e1900 */
      |        /*00a0*/                   LDG.E R7, [R6.64] ;                                 /* 0x0000000406077981 */
      |                                                                                       /* 0x000ea2000c1e1900 */
      |        /*00b0*/                   LEA R2, P0, R3, c[0x0][0x160], 0x3 ;                /* 0x0000580003027a11 */
      |                                                                                       /* 0x000fc800078018ff */
      |        /*00c0*/                   LEA.HI.X R3, R3, c[0x0][0x164], RZ, 0x3, P0 ;       /* 0x0000590003037a11 */
      |                                                                                       /* 0x000fe400000f1cff */
      |        /*00d0*/                   FMNMX R9, R4.reuse, R7.reuse, PT ;                  /* 0x0000000704097209 */
      |                                                                                       /* 0x0c4fe40003800000 */
      |        /*00e0*/                   FMNMX R11, R4, R7, !PT ;                            /* 0x00000007040b7209 */
      |                                                                                       /* 0x000fc60007800000 */
      |        /*00f0*/                   STG.E [R2.64], R9 ;                                 /* 0x0000000902007986 */
      |                                                                                       /* 0x000fe8000c101904 */
      |        /*0100*/                   STG.E [R2.64+0x4], R11 ;                            /* 0x0000040b02007986 */
      |                                                                                       /* 0x000fe2000c101904 */
      |        /*0110*/                   EXIT ;                                              /* 0x000000000000794d */
      |                                                                                       /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0120*/                   BRA `(.L_x_0);                                      /* 0xfffffff000007947 */
      |                                                                                       /* 0x000fc0000383ffff */
      |        /*0130*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*0180*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*0190*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*01a0*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

}

class FmnmxFixtures extends NvidiaFixtureSuite {
  import FmnmxFixtures.*

  reproduces("fmnmxSm86Probe")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.ptx" -> fmnmxSm86ProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_86 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = fmnmxSm86Probe
  )

}
