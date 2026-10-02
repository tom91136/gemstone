package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object FrndFixtures {
  val frndSm86ProbeSm70PtxSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry probe(.param .u64 output, .param .u64 input) {
      |  .reg .b32 r;
      |  .reg .b64 d<5>;
      |  .reg .f32 f<2>;
      |  ld.param.u64 d0, [output];
      |  ld.param.u64 d1, [input];
      |  mov.u32 r, %tid.x;
      |  mul.wide.u32 d2, r, 4;
      |  add.u64 d3, d1, d2;
      |  ld.global.f32 f0, [d3];
      |  cvt.rni.f32.f32 f1, f0;
      |  add.u64 d4, d0, d2;
      |  st.global.f32 [d4], f1;
      |  ret;
      |}
      |""".stripMargin

  private def frndSm86ProbeSource(sm: Int): String =
    s""".version 8.7
      |.target sm_$sm
      |.address_size 64
      |.visible .entry probe(.param .u64 output, .param .u64 input) {
      |  .reg .b32 r;
      |  .reg .b64 d<5>;
      |  .reg .f32 f<2>;
      |  ld.param.u64 d0, [output];
      |  ld.param.u64 d1, [input];
      |  mov.u32 r, %tid.x;
      |  mul.wide.u32 d2, r, 4;
      |  add.u64 d3, d1, d2;
      |  ld.global.f32 f0, [d3];
      |  cvt.rni.f32.f32 f1, f0;
      |  add.u64 d4, d0, d2;
      |  st.global.f32 [d4], f1;
      |  ret;
      |}
      |""".stripMargin

  val frndSm86ProbeSm75PtxSource: String = frndSm86ProbeSource(75)

  val frndSm86ProbeSm80PtxSource: String = frndSm86ProbeSource(80)

  val frndSm86ProbeSm89PtxSource: String = frndSm86ProbeSource(89)

  val frndSm86ProbePtxSource: String = frndSm86ProbeSource(86)

  val frndSm86ProbeSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
      |                                                                              /* 0x000fc40000000f00 */
      |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;              /* 0x000000fffffff389 */
      |                                                                              /* 0x000fe200000e00ff */
      |        /*0020*/                   S2R R4, SR_TID.X ;                         /* 0x0000000000047919 */
      |                                                                              /* 0x000e220000002100 */
      |        /*0030*/                   MOV R5, 0x4 ;                              /* 0x0000000400057802 */
      |                                                                              /* 0x000fca0000000f00 */
      |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R5, c[0x0][0x168] ;  /* 0x00005a0004027625 */
      |                                                                              /* 0x001fd000078e0005 */
      |        /*0050*/                   LDG.E.SYS R2, [R2] ;                       /* 0x0000000002027381 */
      |                                                                              /* 0x000ea200001ee900 */
      |        /*0060*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;  /* 0x0000580004047625 */
      |                                                                              /* 0x000fe200078e0005 */
      |        /*0070*/                   FRND R7, R2 ;                              /* 0x0000000200077307 */
      |                                                                              /* 0x004e2e0000201000 */
      |        /*0080*/                   STG.E.SYS [R4], R7 ;                       /* 0x0000000704007386 */
      |                                                                              /* 0x001fe2000010e900 */
      |        /*0090*/                   EXIT ;                                     /* 0x000000000000794d */
      |                                                                              /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00a0*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |        /*00b0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00c0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00d0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val frndSm86ProbeSm75: String =
    """	.target	sm_75
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
      |                                                                              /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R4, SR_TID.X ;                         /* 0x0000000000047919 */
      |                                                                              /* 0x000e220000002100 */
      |        /*0020*/                   MOV R5, 0x4 ;                              /* 0x0000000400057802 */
      |                                                                              /* 0x000fca0000000f00 */
      |        /*0030*/                   IMAD.WIDE.U32 R2, R4, R5, c[0x0][0x168] ;  /* 0x00005a0004027625 */
      |                                                                              /* 0x001fd000078e0005 */
      |        /*0040*/                   LDG.E.SYS R2, [R2] ;                       /* 0x0000000002027381 */
      |                                                                              /* 0x000ea200001ee900 */
      |        /*0050*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;  /* 0x0000580004047625 */
      |                                                                              /* 0x000fe200078e0005 */
      |        /*0060*/                   FRND R7, R2 ;                              /* 0x0000000200077307 */
      |                                                                              /* 0x004e2e0000201000 */
      |        /*0070*/                   STG.E.SYS [R4], R7 ;                       /* 0x0000000704007386 */
      |                                                                              /* 0x001fe2000010e900 */
      |        /*0080*/                   EXIT ;                                     /* 0x000000000000794d */
      |                                                                              /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0090*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |        /*00a0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00b0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00c0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00d0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val frndSm86ProbeSm80: String =
    """	.target	sm_80
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                             /* 0x00000a0000017a02 */
      |                                                                                      /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R4, SR_TID.X ;                                 /* 0x0000000000047919 */
      |                                                                                      /* 0x000e220000002100 */
      |        /*0020*/                   HFMA2.MMA R5, -RZ, RZ, 0, 2.384185791015625e-07 ;  /* 0x00000004ff057435 */
      |                                                                                      /* 0x000fe200000001ff */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                       /* 0x0000460000047ab9 */
      |                                                                                      /* 0x000fd20000000a00 */
      |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R5, c[0x0][0x168] ;          /* 0x00005a0004027625 */
      |                                                                                      /* 0x001fcc00078e0005 */
      |        /*0050*/                   LDG.E R2, [R2.64] ;                                /* 0x0000000402027981 */
      |                                                                                      /* 0x000ea2000c1e1900 */
      |        /*0060*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;          /* 0x0000580004047625 */
      |                                                                                      /* 0x000fe200078e0005 */
      |        /*0070*/                   FRND R7, R2 ;                                      /* 0x0000000200077307 */
      |                                                                                      /* 0x004e280000201000 */
      |        /*0080*/                   STG.E [R4.64], R7 ;                                /* 0x0000000704007986 */
      |                                                                                      /* 0x001fe2000c101904 */
      |        /*0090*/                   EXIT ;                                             /* 0x000000000000794d */
      |                                                                                      /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00a0*/                   BRA `(.L_x_0);                                     /* 0xfffffff000007947 */
      |                                                                                      /* 0x000fc0000383ffff */
      |        /*00b0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00c0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00d0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0100*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0110*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  private def frndSm86ProbeListing(sm: Int): String =
    s"""	.target	sm_$sm
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
      |                                                                              /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R4, SR_TID.X ;                         /* 0x0000000000047919 */
      |                                                                              /* 0x000e220000002100 */
      |        /*0020*/                   MOV R5, 0x4 ;                              /* 0x0000000400057802 */
      |                                                                              /* 0x000fe20000000f00 */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;               /* 0x0000460000047ab9 */
      |                                                                              /* 0x000fc80000000a00 */
      |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R5, c[0x0][0x168] ;  /* 0x00005a0004027625 */
      |                                                                              /* 0x001fcc00078e0005 */
      |        /*0050*/                   LDG.E R2, [R2.64] ;                        /* 0x0000000402027981 */
      |                                                                              /* 0x000ea2000c1e1900 */
      |        /*0060*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;  /* 0x0000580004047625 */
      |                                                                              /* 0x000fe200078e0005 */
      |        /*0070*/                   FRND R7, R2 ;                              /* 0x0000000200077307 */
      |                                                                              /* 0x004e280000201000 */
      |        /*0080*/                   STG.E [R4.64], R7 ;                        /* 0x0000000704007986 */
      |                                                                              /* 0x001fe2000c101904 */
      |        /*0090*/                   EXIT ;                                     /* 0x000000000000794d */
      |                                                                              /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00a0*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |        /*00b0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00c0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00d0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0100*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0110*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val frndSm86ProbeSm89: String = frndSm86ProbeListing(89)

  val frndSm86Probe: String = frndSm86ProbeListing(86)

  // Input/output bit patterns for floating-point rounding.
  val frndSm86VectorsVectors: String =
    """00000000 00000000
      |80000000 80000000
      |00000001 00000000
      |80000001 80000000
      |007fffff 00000000
      |807fffff 80000000
      |3effffff 00000000
      |3f000000 00000000
      |3f000001 3f800000
      |bf000000 80000000
      |bf000001 bf800000
      |3fc00000 40000000
      |40200000 40000000
      |bfc00000 c0000000
      |c0200000 c0000000
      |4affffff 4b000000
      |4b000000 4b000000
      |7f7fffff 7f7fffff
      |7f800000 7f800000
      |ff800000 ff800000
      |7fc12345 7fffffff
      |7f812345 7fffffff
      |ffc12345 7fffffff
      |ff812345 7fffffff
      |""".stripMargin

}

class FrndFixtures extends NvidiaFixtureSuite {
  import FrndFixtures.*

  reproduces("frndSm86ProbeSm70")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "input.ptx" -> frndSm86ProbeSm70PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_70 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = frndSm86ProbeSm70
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("frndSm86ProbeSm75", 75, frndSm86ProbeSm75PtxSource, frndSm86ProbeSm75),
      ("frndSm86ProbeSm80", 80, frndSm86ProbeSm80PtxSource, frndSm86ProbeSm80),
      ("frndSm86ProbeSm89", 89, frndSm86ProbeSm89PtxSource, frndSm86ProbeSm89),
      ("frndSm86Probe", 86, frndSm86ProbePtxSource, frndSm86Probe)
    )
  ) reproduces(name)(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec("input.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = expected
  )

}
