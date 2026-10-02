package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object DsetFixtures {
  val dsetSm35ProbePtxSource: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry probe(.param .u64 output, .param .u64 inputs) {
      |.reg .b32 r<8>;
      |.reg .b64 d<4>;
      |.reg .f64 f<2>;
      |.reg .f32 v;
      |.reg .pred p;
      |ld.param.u64 d0,[output];
      |ld.param.u64 d1,[inputs];
      |mov.u32 r0,%tid.x;
      |mad.wide.u32 d2,r0,16,d1;
      |ld.global.v2.f64 {f0,f1},[d2];
      |and.b32 r7,r0,1;
      |setp.eq.u32 p,r7,0;
      |set.neu.u32.f64 r1,f0,f1;
      |set.eq.u32.f64 r2,f0,f1;
      |set.lt.u32.f64 r3,f0,f1;
      |set.geu.u32.f64 r4,f0,f1;
      |set.eq.and.u32.f64 r5,f0,f1,p;
      |set.lt.or.u32.f64 r6,f0,f1,p;
      |set.neu.xor.u32.f64 r7,f0,f1,!p;
      |set.ge.f32.f64 v,f0,f1;
      |mad.wide.u32 d3,r0,32,d0;
      |st.global.v4.u32 [d3],{r1,r2,r3,r4};
      |st.global.v2.u32 [d3+16],{r5,r6};
      |st.global.u32 [d3+24],r7;
      |st.global.f32 [d3+28],v;
      |ret;
      |}
      |""".stripMargin

  val dsetSm35ProbeSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=24"
      |	.align	64
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |                                                                                 /* 0x08a010b010a010ac */
      |        /*0008*/                   MOV R1, c[0x0][0x44];                         /* 0x64c03c00089c0006 */
      |        /*0010*/                   S2R R3, SR_TID.X;                             /* 0x86400000109c000e */
      |        /*0018*/                   MOV32I R0, 0x10;                              /* 0x74000000081fc002 */
      |        /*0020*/                   ISCADD R4.CC, R3, c[0x0][0x148], 0x4;         /* 0x60c41000291c0c12 */
      |        /*0028*/                   MOV32I R8, 0x20;                              /* 0x74000000101fc022 */
      |        /*0030*/                   IMAD.U32.U32.HI.X R5, R3, R0, c[0x0][0x14c];  /* 0x92100000299c0c16 */
      |        /*0038*/                   LOP32I.AND R0, R3, 0x1;                       /* 0x20000000009c0c00 */
      |                                                                                 /* 0x088c8c8cfcb08010 */
      |        /*0048*/                   LD.E.128 R4, [R4];                            /* 0xc6800000001c1010 */
      |        /*0050*/                   ISETP.NE.U32.AND P0, PT, R0, RZ, PT;          /* 0xdb501c007f9c001e */
      |        /*0058*/                   ISCADD R2.CC, R3, c[0x0][0x140], 0x5;         /* 0x60c41400281c0c0a */
      |        /*0060*/                   IMAD.U32.U32.HI.X R3, R3, R8, c[0x0][0x144];  /* 0x92102000289c0c0e */
      |        /*0068*/                   DSET.NEU.AND R12, R4, R6, PT;                 /* 0xc8681c00031c1032 */
      |        /*0070*/                   DSET.EQ.AND R13, R4, R6, PT;                  /* 0xc8101c00031c1036 */
      |        /*0078*/                   DSET.LT.AND R14, R4, R6, PT;                  /* 0xc8081c00031c103a */
      |                                                                                 /* 0x0880a48c108c8c8c */
      |        /*0088*/                   DSET.GEU.AND R15, R4, R6, PT;                 /* 0xc8701c00031c103e */
      |        /*0090*/                   DSET.EQ.AND R20, R4, R6, !P0;                 /* 0xc8102000031c1052 */
      |        /*0098*/                   DSET.LT.OR R21, R4, R6, !P0;                  /* 0xc8092000031c1056 */
      |        /*00a0*/                   ST.E.128 [R2], R12;                           /* 0xe6800000001c0830 */
      |        /*00a8*/                   DSET.NEU.XOR R22, R4, R6, P0;                 /* 0xc86a0000031c105a */
      |        /*00b0*/                   DSET.BF.GE.AND R23, R4, R6, PT;               /* 0xc8b01c00031c105e */
      |        /*00b8*/                   ST.E.128 [R2+0x10], R20;                      /* 0xe6800000081c0850 */
      |                                                                                 /* 0x08000000000000b8 */
      |        /*00c8*/                   EXIT;                                         /* 0x18000000001c003c */
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0);                                /* 0x12007ffffc1c003c */
      |        /*00d8*/                   NOP;                                          /* 0x85800000001c3c02 */
      |        /*00e0*/                   NOP;                                          /* 0x85800000001c3c02 */
      |        /*00e8*/                   NOP;                                          /* 0x85800000001c3c02 */
      |        /*00f0*/                   NOP;                                          /* 0x85800000001c3c02 */
      |        /*00f8*/                   NOP;                                          /* 0x85800000001c3c02 */
      |.L_x_1:
      |""".stripMargin

}

class DsetFixtures extends NvidiaFixtureSuite {
  import DsetFixtures.*

  reproduces("dsetSm35ProbeSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "input.ptx" -> dsetSm35ProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_35 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = dsetSm35ProbeSm35
  )

}
