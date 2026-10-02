package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object Lop3PredicateFixtures {
  val lop3PredicateOriginalSm70PtxSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry probe(.param .u64 output, .param .u64 input) {
      |.reg .b32 r<16>;
      |.reg .b64 d<5>;
      |.reg .pred p<4>;
      |ld.param.u64 d0,[output];
      |ld.param.u64 d1,[input];
      |mov.u32 r0,%tid.x;
      |mul.wide.u32 d2,r0,16;
      |add.u64 d0,d0,d2;
      |add.u64 d1,d1,d2;
      |ld.global.v4.u32 {r1,r2,r3,r4},[d1];
      |setp.ne.u32 p0,r4,0;
      |lop3.b32 r5,r1,r2,r3,0xc0;
      |setp.ne.or.u32 p1,r5,0,p0;
      |setp.ne.and.u32 p2,r5,0,p0;
      |setp.ne.or.u32 p3,r5,0,!p0;
      |selp.u32 r6,1,0,p1;
      |selp.u32 r7,1,0,p2;
      |selp.u32 r8,1,0,p3;
      |st.global.v4.u32 [d0],{r5,r6,r7,r8};
      |ret;
      |}
      |""".stripMargin

  val lop3PredicateProbePtxSource: String =
    """.version 8.2
      |.target sm_75
      |.address_size 64
      |.visible .entry probe(.param .u64 output, .param .u64 input) {
      |.reg .b32 r<16>;
      |.reg .b64 d<5>;
      |.reg .pred p<4>;
      |ld.param.u64 d0,[output];
      |ld.param.u64 d1,[input];
      |mov.u32 r0,%tid.x;
      |mul.wide.u32 d2,r0,16;
      |add.u64 d0,d0,d2;
      |add.u64 d1,d1,d2;
      |ld.global.v4.u32 {r1,r2,r3,r4},[d1];
      |setp.ne.u32 p0,r4,0;
      |lop3.b32 r5,r1,r2,r3,0xc0;
      |lop3.or.b32 _|p1,r1,r2,r3,0xc0,p0;
      |lop3.and.b32 _|p2,r1,r2,r3,0xc0,p0;
      |not.pred p3,p0;
      |lop3.or.b32 _|p3,r1,r2,r3,0xc0,p3;
      |selp.u32 r6,1,0,p1;
      |selp.u32 r7,1,0,p2;
      |selp.u32 r8,1,0,p3;
      |st.global.v4.u32 [d0],{r5,r6,r7,r8};
      |ret;
      |}
      |""".stripMargin

  val lop3PredicateProbeSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
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
      |                                                                                       /* 0x000fc400078e00ff */
      |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                       /* 0x000000fffffff389 */
      |                                                                                       /* 0x000fe200000e00ff */
      |        /*0020*/                   S2R R2, SR_TID.X ;                                  /* 0x0000000000027919 */
      |                                                                                       /* 0x000e220000002100 */
      |        /*0030*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x10 ;                     /* 0x00000010ff037424 */
      |                                                                                       /* 0x000fc800078e00ff */
      |        /*0040*/                   IMAD.WIDE.U32 R4, R2, R3, c[0x0][0x168] ;           /* 0x00005a0002047625 */
      |                                                                                       /* 0x001fd000078e0003 */
      |        /*0050*/                   LDG.E.128.SYS R4, [R4] ;                            /* 0x0000000004047381 */
      |                                                                                       /* 0x000ea200001eed00 */
      |        /*0060*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;           /* 0x0000580002027625 */
      |                                                                                       /* 0x000fe200078e0003 */
      |        /*0070*/                   LOP3.LUT R8, R4, R5, R6, 0xc0, !PT ;                /* 0x0000000504087212 */
      |                                                                                       /* 0x004fe400078ec006 */
      |        /*0080*/                   ISETP.NE.U32.AND P0, PT, R7, RZ, PT ;               /* 0x000000ff0700720c */
      |                                                                                       /* 0x000fc80003f05070 */
      |        /*0090*/                   LOP3.LUT P2, RZ, R4.reuse, R5, R6, 0xc0, P0 ;       /* 0x0000000504ff7212 */
      |                                                                                       /* 0x040fe4000004c006 */
      |        /*00a0*/                   LOP3.LUT.PAND P1, RZ, R4.reuse, R5, R6, 0xc0, P0 ;  /* 0x0000000504ff7212 */
      |                                                                                       /* 0x040fe4000003c006 */
      |        /*00b0*/                   LOP3.LUT P0, RZ, R4, R5, R6, 0xc0, !P0 ;            /* 0x0000000504ff7212 */
      |                                                                                       /* 0x000fe4000400c006 */
      |        /*00c0*/                   SEL R9, RZ, 0x1, !P2 ;                              /* 0x00000001ff097807 */
      |                                                                                       /* 0x000fe40005000000 */
      |        /*00d0*/                   SEL R10, RZ, 0x1, !P1 ;                             /* 0x00000001ff0a7807 */
      |                                                                                       /* 0x000fe40004800000 */
      |        /*00e0*/                   SEL R11, RZ, 0x1, !P0 ;                             /* 0x00000001ff0b7807 */
      |                                                                                       /* 0x000fd00004000000 */
      |        /*00f0*/                   STG.E.128.SYS [R2], R8 ;                            /* 0x0000000802007386 */
      |                                                                                       /* 0x000fe2000010ed00 */
      |        /*0100*/                   EXIT ;                                              /* 0x000000000000794d */
      |                                                                                       /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0110*/                   BRA `(.L_x_0);                                      /* 0xfffffff000007947 */
      |                                                                                       /* 0x000fc0000383ffff */
      |        /*0120*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
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
      |.L_x_1:
      |""".stripMargin

  val lop3PredicateProbeSm75: String =
    """	.target	sm_75
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
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;                         /* 0x00000a00ff017624 */
      |                                                                                                   /* 0x000fe400078e00ff */
      |        /*0010*/                   S2R R2, SR_TID.X ;                                              /* 0x0000000000027919 */
      |                                                                                                   /* 0x000e220000002100 */
      |        /*0020*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x10 ;                                 /* 0x00000010ff037424 */
      |                                                                                                   /* 0x000fc800078e00ff */
      |        /*0030*/                   IMAD.WIDE.U32 R4, R2, R3, c[0x0][0x168] ;                       /* 0x00005a0002047625 */
      |                                                                                                   /* 0x001fd000078e0003 */
      |        /*0040*/                   LDG.E.128.SYS R4, [R4] ;                                        /* 0x0000000004047381 */
      |                                                                                                   /* 0x000ea200001eed00 */
      |        /*0050*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;                       /* 0x0000580002027625 */
      |                                                                                                   /* 0x000fe200078e0003 */
      |        /*0060*/                   ISETP.NE.U32.AND P0, PT, R7, RZ, PT ;                           /* 0x000000ff0700720c */
      |                                                                                                   /* 0x004fe40003f05070 */
      |        /*0070*/                   LOP3.LUT R8, R4.reuse, R5.reuse, R6.reuse, 0xc0, !PT ;          /* 0x0000000504087212 */
      |                                                                                                   /* 0x1c0fe400078ec006 */
      |        /*0080*/                   LOP3.LUT P2, RZ, R4.reuse, R5.reuse, R6.reuse, 0xc0, P0 ;       /* 0x0000000504ff7212 */
      |                                                                                                   /* 0x1c0fe4000004c006 */
      |        /*0090*/                   LOP3.LUT.PAND P1, RZ, R4.reuse, R5.reuse, R6.reuse, 0xc0, P0 ;  /* 0x0000000504ff7212 */
      |                                                                                                   /* 0x1c0fe4000003c006 */
      |        /*00a0*/                   LOP3.LUT P0, RZ, R4, R5, R6, 0xc0, !P0 ;                        /* 0x0000000504ff7212 */
      |                                                                                                   /* 0x000fc4000400c006 */
      |        /*00b0*/                   SEL R9, RZ, 0x1, !P2 ;                                          /* 0x00000001ff097807 */
      |                                                                                                   /* 0x000fe40005000000 */
      |        /*00c0*/                   SEL R10, RZ, 0x1, !P1 ;                                         /* 0x00000001ff0a7807 */
      |                                                                                                   /* 0x000fe40004800000 */
      |        /*00d0*/                   SEL R11, RZ, 0x1, !P0 ;                                         /* 0x00000001ff0b7807 */
      |                                                                                                   /* 0x000fd00004000000 */
      |        /*00e0*/                   STG.E.128.SYS [R2], R8 ;                                        /* 0x0000000802007386 */
      |                                                                                                   /* 0x000fe2000010ed00 */
      |        /*00f0*/                   EXIT ;                                                          /* 0x000000000000794d */
      |                                                                                                   /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0100*/                   BRA `(.L_x_0);                                                  /* 0xfffffff000007947 */
      |                                                                                                   /* 0x000fc0000383ffff */
      |        /*0110*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  private def lop3PredicateProbeForTarget(sm: Int): String =
    s"""	.target	sm_$sm
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
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;                         /* 0x00000a00ff017624 */
      |                                                                                                   /* 0x000fe400078e00ff */
      |        /*0010*/                   S2R R2, SR_TID.X ;                                              /* 0x0000000000027919 */
      |                                                                                                   /* 0x000e220000002100 */
      |        /*0020*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x10 ;                                 /* 0x00000010ff037424 */
      |                                                                                                   /* 0x000fe200078e00ff */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                                    /* 0x0000460000047ab9 */
      |                                                                                                   /* 0x000fc60000000a00 */
      |        /*0040*/                   IMAD.WIDE.U32 R4, R2, R3, c[0x0][0x168] ;                       /* 0x00005a0002047625 */
      |                                                                                                   /* 0x001fcc00078e0003 */
      |        /*0050*/                   LDG.E.128 R4, [R4.64] ;                                         /* 0x0000000404047981 */
      |                                                                                                   /* 0x000ea2000c1e1d00 */
      |        /*0060*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;                       /* 0x0000580002027625 */
      |                                                                                                   /* 0x000fe200078e0003 */
      |        /*0070*/                   ISETP.NE.U32.AND P0, PT, R7, RZ, PT ;                           /* 0x000000ff0700720c */
      |                                                                                                   /* 0x004fe40003f05070 */
      |        /*0080*/                   LOP3.LUT R8, R4.reuse, R5.reuse, R6.reuse, 0xc0, !PT ;          /* 0x0000000504087212 */
      |                                                                                                   /* 0x1c0fe400078ec006 */
      |        /*0090*/                   LOP3.LUT P2, RZ, R4.reuse, R5.reuse, R6.reuse, 0xc0, P0 ;       /* 0x0000000504ff7212 */
      |                                                                                                   /* 0x1c0fe4000004c006 */
      |        /*00a0*/                   LOP3.LUT.PAND P1, RZ, R4.reuse, R5.reuse, R6.reuse, 0xc0, P0 ;  /* 0x0000000504ff7212 */
      |                                                                                                   /* 0x1c0fe4000003c006 */
      |        /*00b0*/                   LOP3.LUT P0, RZ, R4, R5, R6, 0xc0, !P0 ;                        /* 0x0000000504ff7212 */
      |                                                                                                   /* 0x000fc4000400c006 */
      |        /*00c0*/                   SEL R9, RZ, 0x1, !P2 ;                                          /* 0x00000001ff097807 */
      |                                                                                                   /* 0x000fe40005000000 */
      |        /*00d0*/                   SEL R10, RZ, 0x1, !P1 ;                                         /* 0x00000001ff0a7807 */
      |                                                                                                   /* 0x000fe40004800000 */
      |        /*00e0*/                   SEL R11, RZ, 0x1, !P0 ;                                         /* 0x00000001ff0b7807 */
      |                                                                                                   /* 0x000fca0004000000 */
      |        /*00f0*/                   STG.E.128 [R2.64], R8 ;                                         /* 0x0000000802007986 */
      |                                                                                                   /* 0x000fe2000c101d04 */
      |        /*0100*/                   EXIT ;                                                          /* 0x000000000000794d */
      |                                                                                                   /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0110*/                   BRA `(.L_x_0);                                                  /* 0xfffffff000007947 */
      |                                                                                                   /* 0x000fc0000383ffff */
      |        /*0120*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0180*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0190*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01a0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val lop3PredicateProbeSm80: String = lop3PredicateProbeForTarget(80)

  val lop3PredicateProbeSm86: String = lop3PredicateProbeForTarget(86)

  val lop3PredicateProbeSm89: String = lop3PredicateProbeForTarget(89)

}

class Lop3PredicateFixtures extends NvidiaFixtureSuite {
  import Lop3PredicateFixtures.*

  reproduces("lop3PredicateProbeSm70")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "original-sm70.ptx" -> lop3PredicateOriginalSm70PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_70 original-sm70.ptx -o original-sm70.cubin",
      """cp original-sm70.cubin probe-sm70.cubin
      |text_offset=$(readelf -SW original-sm70.cubin | awk '{for(i=1;i<=NF;i++) if($i==".text.probe"){print "0x"$(i+3);exit}}')
      |for spec in '0x90 0c720008ff0000007054740000e40f04 1272ff040500000006c0040000e40f04' '0xa0 0c720008ff0000007050720000e40f04 1272ff040500000006c0030000e40f04' '0xb0 0c720008ff0000007054700400e40f00 1272ff040500000006c0000400e40f00'; do
      |  set -- $spec; off=$1; old=$2; new=$3
      |  test "$(od -An -tx1 -N16 -j $((text_offset+off)) original-sm70.cubin | tr -d ' \n')" = "$old"
      |  perl -e 'print pack("H*",shift)' "$new" | dd of=probe-sm70.cubin bs=1 seek=$((text_offset+off)) conv=notrunc status=none
      |done""".stripMargin,
      "nvdisasm -c -ndf -hex probe-sm70.cubin > probe-sm70.sass"
    ),
    output = "probe-sm70.sass",
    expected = lop3PredicateProbeSm70
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("lop3PredicateProbeSm75", 75, lop3PredicateProbePtxSource, lop3PredicateProbeSm75),
      ("lop3PredicateProbeSm80", 80, lop3PredicateProbePtxSource, lop3PredicateProbeSm80),
      ("lop3PredicateProbeSm86", 86, lop3PredicateProbePtxSource, lop3PredicateProbeSm86),
      ("lop3PredicateProbeSm89", 89, lop3PredicateProbePtxSource, lop3PredicateProbeSm89)
    )
  ) reproduces(name)(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec("probe.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm probe.ptx -o probe-sm$sm.cubin",
      s"nvdisasm -c -ndf -hex probe-sm$sm.cubin > probe-sm$sm.sass"
    ),
    output = s"probe-sm$sm.sass",
    expected = expected
  )

}
