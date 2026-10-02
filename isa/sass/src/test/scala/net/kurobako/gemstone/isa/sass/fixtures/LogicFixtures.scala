package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object LogicFixtures {
  private def legacyLogicProbeForTarget(sm: Int): String =
    s""".version 7.8
      |.target sm_$sm
      |.address_size 64
      |.visible .entry logic_probe(.param .u64 out, .param .u32 a, .param .u32 b, .param .u32 c) {
      |.reg .u64 %out;
      |.reg .b32 %a, %b, %c, %and, %or, %xor, %pn, %pz, %po;
      |.reg .u32 %lane;
      |.reg .pred %nonzero, %zero, %orNonzero;
      |ld.param.u64 %out, [out];
      |ld.param.u32 %a, [a];
      |ld.param.u32 %b, [b];
      |ld.param.u32 %c, [c];
      |mov.u32 %lane, %tid.x;
      |add.u32 %b, %b, %lane;
      |add.u32 %a, %a, %lane;
      |and.b32 %and, %a, %b;
      |setp.ne.u32 %nonzero, %and, 0;
      |selp.u32 %pn, 17, 29, %nonzero;
      |setp.eq.u32 %zero, %and, 0;
      |selp.u32 %pz, 37, 41, %zero;
      |or.b32 %or, %a, %b;
      |setp.ne.u32 %orNonzero, %or, 0;
      |selp.u32 %po, 43, 47, %orNonzero;
      |lop3.b32 %xor, %a, %b, %c, 0xca;
      |st.global.u32 [%out], %and;
      |st.global.u32 [%out+4], %pn;
      |st.global.u32 [%out+8], %pz;
      |st.global.u32 [%out+12], %or;
      |st.global.u32 [%out+16], %po;
      |st.global.u32 [%out+20], %xor;
      |ret;
      |}
      |.visible .entry predicate_probe(.param .u64 out, .param .u32 a, .param .u32 b) {
      |.reg .u64 %out;
      |.reg .b32 %a, %b, %lane, %and, %or;
      |.reg .pred %p0, %p1, %p2;
      |ld.param.u64 %out, [out];
      |ld.param.u32 %a, [a];
      |ld.param.u32 %b, [b];
      |mov.u32 %lane, %tid.x;
      |add.u32 %a, %a, %lane;
      |add.u32 %b, %b, %lane;
      |and.b32 %and, %a, 3;
      |setp.ne.u32 %p0, %and, 0;
      |@%p0 st.global.u32 [%out], %a;
      |and.b32 %and, %a, 31;
      |setp.eq.u32 %p1, %and, 0;
      |@%p1 st.global.u32 [%out+4], %b;
      |or.b32 %or, %a, %b;
      |setp.ne.u32 %p2, %or, 0;
      |@%p2 st.global.u32 [%out+12], %b;
      |ret;
      |}
      |""".stripMargin

  val legacyLogicProbeSm50PtxSource: String = legacyLogicProbeForTarget(50)

  val legacyLogicProbeSm60PtxSource: String = legacyLogicProbeForTarget(60)

  val legacyLogicProbeSm50LogicProbe: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.logic_probe         --------------------------
      |	.section	.text.logic_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=11"
      |	.align	32
      |        .global         logic_probe
      |        .type           logic_probe,@function
      |        .size           logic_probe,(.L_x_1 - logic_probe)
      |        .other          logic_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |logic_probe:
      |.text.logic_probe:
      |                                                                               /* 0x001c4400fe0007f6 */
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;                      /* 0x4c98078000870001 */
      |        /*0010*/         {         MOV32I R7, 0x11 ;                           /* 0x010000000117f007 */
      |        /*0018*/                   S2R R5, SR_TID.X         }
      |                                                                               /* 0xf0c8000002170005 */
      |                                                                               /* 0x001fc400fe2007f1 */
      |        /*0028*/                   MOV32I R6, 0x25 ;                           /* 0x010000000257f006 */
      |        /*0030*/                   MOV32I R10, 0x2b ;                          /* 0x0100000002b7f00a */
      |        /*0038*/                   MOV R2, c[0x0][0x140] ;                     /* 0x4c98078005070002 */
      |                                                                               /* 0x001f9841fe2007ff */
      |        /*0048*/                   MOV R3, c[0x0][0x144] ;                     /* 0x4c98078005170003 */
      |        /*0050*/                   IADD R0, R5.reuse, c[0x0][0x14c] ;          /* 0x4c10000005370500 */
      |        /*0058*/                   IADD R5, R5, c[0x0][0x148] ;                /* 0x4c10000005270505 */
      |                                                                               /* 0x001fc840fe0607f2 */
      |        /*0068*/                   LOP.AND R4, R5.reuse, R0.reuse ;            /* 0x5c47000000070504 */
      |        /*0070*/         {         LOP.OR R9, R5.reuse, R0 ;                   /* 0x5c47020000070509 */
      |        /*0078*/                   STG.E [R2], R4         }
      |                                                                               /* 0xeedc200000070204 */
      |                                                                               /* 0x201fc000fe4007f0 */
      |        /*0088*/         {         LOP3.LUT R5, R5, c[0x0][0x150], R0, 0xac ;  /* 0x02ac000005470505 */
      |        /*0090*/                   STG.E [R2+0xc], R9         }
      |                                                                               /* 0xeedc200000c70209 */
      |        /*0098*/         {         ICMP.NE.U32 R7, R7, 0x1d, R4.reuse ;        /* 0x364a020001d70707 */
      |                                                                               /* 0x001fc800fe0007f2 */
      |        /*00a8*/                   STG.E [R2+0x14], R5         }
      |                                                                               /* 0xeedc200001470205 */
      |        /*00b0*/         {         ICMP.EQ.U32 R8, R6, 0x29, R4 ;              /* 0x3644020002970608 */
      |        /*00b8*/                   STG.E [R2+0x4], R7         }
      |                                                                               /* 0xeedc200000470207 */
      |                                                                               /* 0x001fc400fe4007f0 */
      |        /*00c8*/         {         ICMP.NE.U32 R0, R10, 0x2f, R9 ;             /* 0x364a048002f70a00 */
      |        /*00d0*/                   STG.E [R2+0x8], R8         }
      |                                                                               /* 0xeedc200000870208 */
      |        /*00d8*/                   STG.E [R2+0x10], R0 ;                       /* 0xeedc200001070200 */
      |                                                                               /* 0x001ffc00ffe007e4 */
      |        /*00e8*/                   NOP ;                                       /* 0x50b0000000070f00 */
      |        /*00f0*/                   EXIT ;                                      /* 0xe30000000007000f */
      |.L_x_0:
      |        /*00f8*/                   BRA `(.L_x_0) ;                             /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyLogicProbeSm50PredicateProbe: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.predicate_probe     --------------------------
      |	.section	.text.predicate_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=6"
      |	.align	32
      |        .global         predicate_probe
      |        .type           predicate_probe,@function
      |        .size           predicate_probe,(.L_x_1 - predicate_probe)
      |        .other          predicate_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |predicate_probe:
      |.text.predicate_probe:
      |                                                                        /* 0x001c4400fe0007f6 */
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;               /* 0x4c98078000870001 */
      |        /*0010*/         {         MOV R2, c[0x0][0x140] ;              /* 0x4c98078005070002 */
      |        /*0018*/                   S2R R5, SR_TID.X         }
      |                                                                        /* 0xf0c8000002170005 */
      |                                                                        /* 0x001fd441fe2007ff */
      |        /*0028*/                   MOV R3, c[0x0][0x144] ;              /* 0x4c98078005170003 */
      |        /*0030*/                   IADD R0, R5.reuse, c[0x0][0x148] ;   /* 0x4c10000005270500 */
      |        /*0038*/                   IADD R5, R5, c[0x0][0x14c] ;         /* 0x4c10000005370505 */
      |                                                                        /* 0x001fac40fe2207f1 */
      |        /*0048*/                   LOP.AND.NZ P0, RZ, R0.reuse, 0x3 ;   /* 0x38403000003700ff */
      |        /*0050*/                   LOP.AND.NZ P2, RZ, R0.reuse, 0x1f ;  /* 0x3842300001f700ff */
      |        /*0058*/                   LOP.OR.NZ P1, RZ, R0, R5 ;           /* 0x5c413200005700ff */
      |                                                                        /* 0x001fbc001e2000f1 */
      |        /*0068*/               @P0 STG.E [R2], R0 ;                     /* 0xeedc200000000200 */
      |        /*0070*/              @!P2 STG.E [R2+0x4], R5 ;                 /* 0xeedc2000004a0205 */
      |        /*0078*/                   NOP ;                                /* 0x50b0000000070f00 */
      |                                                                        /* 0x001fbc00fe2007fd */
      |        /*0088*/              @!P1 EXIT ;                               /* 0xe30000000009000f */
      |        /*0090*/                   STG.E [R2+0xc], R5 ;                 /* 0xeedc200000c70205 */
      |        /*0098*/                   NOP ;                                /* 0x50b0000000070f00 */
      |                                                                        /* 0x001f8c00fde007ef */
      |        /*00a8*/                   NOP ;                                /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP ;                                /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP ;                                /* 0x50b0000000070f00 */
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

  val legacyLogicProbeSm60LogicProbe: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.logic_probe         --------------------------
      |	.section	.text.logic_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=11"
      |	.align	32
      |        .global         logic_probe
      |        .type           logic_probe,@function
      |        .size           logic_probe,(.L_x_1 - logic_probe)
      |        .other          logic_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |logic_probe:
      |.text.logic_probe:
      |                                                                               /* 0x001c4400fe0007f6 */
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;                      /* 0x4c98078000870001 */
      |        /*0010*/         {         MOV32I R7, 0x11 ;                           /* 0x010000000117f007 */
      |        /*0018*/                   S2R R5, SR_TID.X         }
      |                                                                               /* 0xf0c8000002170005 */
      |                                                                               /* 0x001fc400fe2007f1 */
      |        /*0028*/                   MOV32I R6, 0x25 ;                           /* 0x010000000257f006 */
      |        /*0030*/                   MOV32I R10, 0x2b ;                          /* 0x0100000002b7f00a */
      |        /*0038*/                   MOV R2, c[0x0][0x140] ;                     /* 0x4c98078005070002 */
      |                                                                               /* 0x001f9841fe2007ff */
      |        /*0048*/                   MOV R3, c[0x0][0x144] ;                     /* 0x4c98078005170003 */
      |        /*0050*/                   IADD R0, R5.reuse, c[0x0][0x14c] ;          /* 0x4c10000005370500 */
      |        /*0058*/                   IADD R5, R5, c[0x0][0x148] ;                /* 0x4c10000005270505 */
      |                                                                               /* 0x001fc840fe0607f2 */
      |        /*0068*/                   LOP.AND R4, R5.reuse, R0.reuse ;            /* 0x5c47000000070504 */
      |        /*0070*/         {         LOP.OR R9, R5.reuse, R0 ;                   /* 0x5c47020000070509 */
      |        /*0078*/                   STG.E [R2], R4         }
      |                                                                               /* 0xeedc200000070204 */
      |                                                                               /* 0x201fc000fe4007f0 */
      |        /*0088*/         {         LOP3.LUT R5, R5, c[0x0][0x150], R0, 0xac ;  /* 0x02ac000005470505 */
      |        /*0090*/                   STG.E [R2+0xc], R9         }
      |                                                                               /* 0xeedc200000c70209 */
      |        /*0098*/         {         ICMP.NE.U32 R7, R7, 0x1d, R4.reuse ;        /* 0x364a020001d70707 */
      |                                                                               /* 0x001fc800fe0007f2 */
      |        /*00a8*/                   STG.E [R2+0x14], R5         }
      |                                                                               /* 0xeedc200001470205 */
      |        /*00b0*/         {         ICMP.EQ.U32 R8, R6, 0x29, R4 ;              /* 0x3644020002970608 */
      |        /*00b8*/                   STG.E [R2+0x4], R7         }
      |                                                                               /* 0xeedc200000470207 */
      |                                                                               /* 0x001fc400fe4007f0 */
      |        /*00c8*/         {         ICMP.NE.U32 R0, R10, 0x2f, R9 ;             /* 0x364a048002f70a00 */
      |        /*00d0*/                   STG.E [R2+0x8], R8         }
      |                                                                               /* 0xeedc200000870208 */
      |        /*00d8*/                   STG.E [R2+0x10], R0 ;                       /* 0xeedc200001070200 */
      |                                                                               /* 0x001ffc00ffe007e4 */
      |        /*00e8*/                   NOP ;                                       /* 0x50b0000000070f00 */
      |        /*00f0*/                   EXIT ;                                      /* 0xe30000000007000f */
      |.L_x_0:
      |        /*00f8*/                   BRA `(.L_x_0) ;                             /* 0xe2400fffff87000f */
      |.L_x_1:
      |""".stripMargin

  val legacyLogicProbeSm60PredicateProbe: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.predicate_probe     --------------------------
      |	.section	.text.predicate_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=6"
      |	.align	32
      |        .global         predicate_probe
      |        .type           predicate_probe,@function
      |        .size           predicate_probe,(.L_x_1 - predicate_probe)
      |        .other          predicate_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |predicate_probe:
      |.text.predicate_probe:
      |                                                                        /* 0x001c4400fe0007f6 */
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;               /* 0x4c98078000870001 */
      |        /*0010*/         {         MOV R2, c[0x0][0x140] ;              /* 0x4c98078005070002 */
      |        /*0018*/                   S2R R5, SR_TID.X         }
      |                                                                        /* 0xf0c8000002170005 */
      |                                                                        /* 0x001fd441fe2007ff */
      |        /*0028*/                   MOV R3, c[0x0][0x144] ;              /* 0x4c98078005170003 */
      |        /*0030*/                   IADD R0, R5.reuse, c[0x0][0x148] ;   /* 0x4c10000005270500 */
      |        /*0038*/                   IADD R5, R5, c[0x0][0x14c] ;         /* 0x4c10000005370505 */
      |                                                                        /* 0x001fac40fe2207f1 */
      |        /*0048*/                   LOP.AND.NZ P0, RZ, R0.reuse, 0x3 ;   /* 0x38403000003700ff */
      |        /*0050*/                   LOP.AND.NZ P2, RZ, R0.reuse, 0x1f ;  /* 0x3842300001f700ff */
      |        /*0058*/                   LOP.OR.NZ P1, RZ, R0, R5 ;           /* 0x5c413200005700ff */
      |                                                                        /* 0x001fbc001e2000f1 */
      |        /*0068*/               @P0 STG.E [R2], R0 ;                     /* 0xeedc200000000200 */
      |        /*0070*/              @!P2 STG.E [R2+0x4], R5 ;                 /* 0xeedc2000004a0205 */
      |        /*0078*/                   NOP ;                                /* 0x50b0000000070f00 */
      |                                                                        /* 0x001fbc00fe2007fd */
      |        /*0088*/              @!P1 EXIT ;                               /* 0xe30000000009000f */
      |        /*0090*/                   STG.E [R2+0xc], R5 ;                 /* 0xeedc200000c70205 */
      |        /*0098*/                   NOP ;                                /* 0x50b0000000070f00 */
      |                                                                        /* 0x001f8c00fde007ef */
      |        /*00a8*/                   NOP ;                                /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP ;                                /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP ;                                /* 0x50b0000000070f00 */
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

  val legacyLogicZeroSm50PredicateProbe: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.predicate_probe     --------------------------
      |	.section	.text.predicate_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=6"
      |	.align	32
      |        .global         predicate_probe
      |        .type           predicate_probe,@function
      |        .size           predicate_probe,(.L_x_1 - predicate_probe)
      |        .other          predicate_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |predicate_probe:
      |.text.predicate_probe:
      |                                                                       /* 0x001c4400fe0007f6 */
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;              /* 0x4c98078000870001 */
      |        /*0010*/         {         MOV R2, c[0x0][0x140] ;             /* 0x4c98078005070002 */
      |        /*0018*/                   S2R R5, SR_TID.X         }
      |                                                                       /* 0xf0c8000002170005 */
      |                                                                       /* 0x001fd441fe2007ff */
      |        /*0028*/                   MOV R3, c[0x0][0x144] ;             /* 0x4c98078005170003 */
      |        /*0030*/                   IADD R0, R5.reuse, c[0x0][0x148] ;  /* 0x4c10000005270500 */
      |        /*0038*/                   IADD R5, R5, c[0x0][0x14c] ;        /* 0x4c10000005370505 */
      |                                                                       /* 0x001fac40fe2207f1 */
      |        /*0048*/                   LOP.AND.NZ P0, RZ, R0.reuse, 0x3 ;  /* 0x38403000003700ff */
      |        /*0050*/                   LOP.AND.Z P2, R4, R0.reuse, 0x1f ;  /* 0x3842200001f70004 */
      |        /*0058*/                   LOP.OR.NZ P1, RZ, R0, R5 ;          /* 0x5c413200005700ff */
      |                                                                       /* 0x001fbc001e2000f1 */
      |        /*0068*/               @P0 STG.E [R2], R0 ;                    /* 0xeedc200000000200 */
      |        /*0070*/               @P2 STG.E [R2+0x4], R5 ;                /* 0xeedc200000420205 */
      |        /*0078*/                   STG.E [R2+0x8], R4 ;                /* 0xeedc200000870204 */
      |                                                                       /* 0x001fbc00fe2007fd */
      |        /*0088*/              @!P1 EXIT ;                              /* 0xe30000000009000f */
      |        /*0090*/                   STG.E [R2+0xc], R5 ;                /* 0xeedc200000c70205 */
      |        /*0098*/                   NOP ;                               /* 0x50b0000000070f00 */
      |                                                                       /* 0x001f8c00fde007ef */
      |        /*00a8*/                   NOP ;                               /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP ;                               /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP ;                               /* 0x50b0000000070f00 */
      |                                                                       /* 0x001f8000ffe007ff */
      |        /*00c8*/                   EXIT ;                              /* 0xe30000000007000f */
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;                     /* 0xe2400fffff87000f */
      |        /*00d8*/                   NOP;                                /* 0x50b0000000070f00 */
      |                                                                       /* 0x001f8000fc0007e0 */
      |        /*00e8*/                   NOP;                                /* 0x50b0000000070f00 */
      |        /*00f0*/                   NOP;                                /* 0x50b0000000070f00 */
      |        /*00f8*/                   NOP;                                /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val legacyLogicZeroSm60PredicateProbe: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.predicate_probe     --------------------------
      |	.section	.text.predicate_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=6"
      |	.align	32
      |        .global         predicate_probe
      |        .type           predicate_probe,@function
      |        .size           predicate_probe,(.L_x_1 - predicate_probe)
      |        .other          predicate_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |predicate_probe:
      |.text.predicate_probe:
      |                                                                       /* 0x001c4400fe0007f6 */
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;              /* 0x4c98078000870001 */
      |        /*0010*/         {         MOV R2, c[0x0][0x140] ;             /* 0x4c98078005070002 */
      |        /*0018*/                   S2R R5, SR_TID.X         }
      |                                                                       /* 0xf0c8000002170005 */
      |                                                                       /* 0x001fd441fe2007ff */
      |        /*0028*/                   MOV R3, c[0x0][0x144] ;             /* 0x4c98078005170003 */
      |        /*0030*/                   IADD R0, R5.reuse, c[0x0][0x148] ;  /* 0x4c10000005270500 */
      |        /*0038*/                   IADD R5, R5, c[0x0][0x14c] ;        /* 0x4c10000005370505 */
      |                                                                       /* 0x001fac40fe2207f1 */
      |        /*0048*/                   LOP.AND.NZ P0, RZ, R0.reuse, 0x3 ;  /* 0x38403000003700ff */
      |        /*0050*/                   LOP.AND.Z P2, R4, R0.reuse, 0x1f ;  /* 0x3842200001f70004 */
      |        /*0058*/                   LOP.OR.NZ P1, RZ, R0, R5 ;          /* 0x5c413200005700ff */
      |                                                                       /* 0x001fbc001e2000f1 */
      |        /*0068*/               @P0 STG.E [R2], R0 ;                    /* 0xeedc200000000200 */
      |        /*0070*/               @P2 STG.E [R2+0x4], R5 ;                /* 0xeedc200000420205 */
      |        /*0078*/                   STG.E [R2+0x8], R4 ;                /* 0xeedc200000870204 */
      |                                                                       /* 0x001fbc00fe2007fd */
      |        /*0088*/              @!P1 EXIT ;                              /* 0xe30000000009000f */
      |        /*0090*/                   STG.E [R2+0xc], R5 ;                /* 0xeedc200000c70205 */
      |        /*0098*/                   NOP ;                               /* 0x50b0000000070f00 */
      |                                                                       /* 0x001f8c00fde007ef */
      |        /*00a8*/                   NOP ;                               /* 0x50b0000000070f00 */
      |        /*00b0*/                   NOP ;                               /* 0x50b0000000070f00 */
      |        /*00b8*/                   NOP ;                               /* 0x50b0000000070f00 */
      |                                                                       /* 0x001f8000ffe007ff */
      |        /*00c8*/                   EXIT ;                              /* 0xe30000000007000f */
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;                     /* 0xe2400fffff87000f */
      |        /*00d8*/                   NOP;                                /* 0x50b0000000070f00 */
      |                                                                       /* 0x001f8000fc0007e0 */
      |        /*00e8*/                   NOP;                                /* 0x50b0000000070f00 */
      |        /*00f0*/                   NOP;                                /* 0x50b0000000070f00 */
      |        /*00f8*/                   NOP;                                /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

}

class LogicFixtures extends NvidiaFixtureSuite {
  import LogicFixtures.*

  reproduces("legacyLogicProbeSm50LogicProbe")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "probe-sm50.ptx" -> legacyLogicProbeSm50PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_50 probe-sm50.ptx -o probe-sm50.cubin",
      "symbol_index=$(readelf -sW probe-sm50.cubin | awk -v name=\"logic_probe\" '$NF == name { sub(\":\", \"\", $1); print $1; exit }')",
      "test -n \"$symbol_index\"",
      "nvdisasm -c -ndf -hex -fun \"$symbol_index\" probe-sm50.cubin > probe-sm50-logic_probe.sass"
    ),
    output = "probe-sm50-logic_probe.sass",
    expected = legacyLogicProbeSm50LogicProbe
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("legacyLogicProbeSm50PredicateProbe", 50, legacyLogicProbeSm50PtxSource, legacyLogicProbeSm50PredicateProbe),
      ("legacyLogicProbeSm60PredicateProbe", 60, legacyLogicProbeSm60PtxSource, legacyLogicProbeSm60PredicateProbe)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("input.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm input.ptx -o kernel.cubin",
      "symbol=$(readelf -sW kernel.cubin | awk '$NF == \"predicate_probe\" {sub(\":\", \"\", $1); print $1; exit}')",
      "nvdisasm -c -ndf -hex -fun \"$symbol\" kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = expected
  )

  reproduces("legacyLogicProbeSm60LogicProbe")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "probe-sm60.ptx" -> legacyLogicProbeSm60PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_60 probe-sm60.ptx -o probe-sm60.cubin",
      "symbol_index=$(readelf -sW probe-sm60.cubin | awk -v name=\"logic_probe\" '$NF == name { sub(\":\", \"\", $1); print $1; exit }')",
      "test -n \"$symbol_index\"",
      "nvdisasm -c -ndf -hex -fun \"$symbol_index\" probe-sm60.cubin > probe-sm60-logic_probe.sass"
    ),
    output = "probe-sm60-logic_probe.sass",
    expected = legacyLogicProbeSm60LogicProbe
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("legacyLogicZeroSm50PredicateProbe", 50, legacyLogicProbeSm50PtxSource, legacyLogicZeroSm50PredicateProbe),
      ("legacyLogicZeroSm60PredicateProbe", 60, legacyLogicProbeSm60PtxSource, legacyLogicZeroSm60PredicateProbe)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm probe.ptx -o probe.cubin",
      "base=$(readelf -SW probe.cubin | awk '{for(i=1;i<=NF;i++) if($i==\".text.predicate_probe\") print \"0x\" $(i+3)}')",
      "printf '\\004\\000\\367\\001\\000\\040\\102\\070' | dd of=probe.cubin bs=1 seek=$((base + 0x50)) conv=notrunc status=none",
      "printf '\\005\\002\\102\\000\\000\\040\\334\\356' | dd of=probe.cubin bs=1 seek=$((base + 0x70)) conv=notrunc status=none",
      "printf '\\004\\002\\207\\000\\000\\040\\334\\356' | dd of=probe.cubin bs=1 seek=$((base + 0x78)) conv=notrunc status=none",
      "symbol=$(readelf -sW probe.cubin | awk '$NF == \"predicate_probe\" {sub(\":\", \"\", $1); print $1; exit}')",
      "nvdisasm -c -ndf -hex -fun \"$symbol\" probe.cubin > zero.sass"
    ),
    output = "zero.sass",
    expected = expected
  )

}
