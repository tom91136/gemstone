package net.kurobako.gemstone.isa.sass.fixtures

object LogicFixtures {
  val bitfieldCuda: String =
    """#include <stdint.h>
      |extern "C" __global__ void bitfield_probe(uint32_t *out, uint32_t x, uint32_t base, uint32_t insert, uint32_t pos, uint32_t width) {
      |  uint32_t u, rev, r, f; int32_t s;
      |  asm volatile("bfe.u32 %0,%1,%2,%3;" : "=r"(u) : "r"(x), "r"(pos), "r"(width));
      |  asm volatile("bfe.s32 %0,%1,%2,%3;" : "=r"(s) : "r"((int32_t)x), "r"(pos), "r"(width));
      |  asm volatile("bfi.b32 %0,%1,%2,%3,%4;" : "=r"(f) : "r"(insert), "r"(base), "r"(pos), "r"(width));
      |  asm volatile("brev.b32 %0,%2; bfe.u32 %1,%0,0,32;" : "=r"(rev), "=r"(r) : "r"(x));
      |  out[0]=u;out[1]=(uint32_t)s;out[2]=f;out[3]=r;
      |}
      |""".stripMargin

  def logicPtx(sm: Int): String =
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

  val lop3PredicatePtx: String =
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

  val lop3PredicateSm70Ptx: String =
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

  val sgxtPtx: String =
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

  private val lop3PredicateSm80: String =
    """//--------------------- .text.probe               --------------------------
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
      |.L_x_1:
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "bitfieldSm35" ->
      """//--------------------- .text.bitfield_probe      --------------------------
        |	.section	.text.bitfield_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         bitfield_probe
        |        .type           bitfield_probe,@function
        |        .size           bitfield_probe,(.L_x_1 - bitfield_probe)
        |        .other          bitfield_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |bitfield_probe:
        |.text.bitfield_probe:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R0, c[0x0][0x158];
        |        /*0018*/                   MOV32I R4, 0x808;
        |        /*0020*/                   MOV R5, c[0x0][0x148];
        |        /*0028*/                   MOV R2, c[0x0][0x140];
        |        /*0030*/                   MOV R6, c[0x0][0x150];
        |        /*0038*/                   BFI R0, R0, R4, c[0x0][0x154];
        |        /*0048*/                   MOV R3, c[0x0][0x144];
        |        /*0050*/                   BFE.U32.BREV R7, R5, 0x2000;
        |        /*0058*/                   BFE.U32 R4, R5, R0;
        |        /*0060*/                   ST.E [R2+0xc], R7;
        |        /*0068*/                   BFE R5, R5, R0;
        |        /*0070*/                   BFI R6, R6, R0, c[0x0][0x14c];
        |        /*0078*/                   ST.E [R2], R4;
        |        /*0088*/                   ST.E [R2+0x4], R5;
        |        /*0090*/                   ST.E [R2+0x8], R6;
        |        /*0098*/                   EXIT;
        |.L_x_1:
        |""".stripMargin,
    "logicPredicateSm50" ->
      """//--------------------- .text.predicate_probe     --------------------------
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
        |.L_x_1:
        |""".stripMargin,
    "logicPredicateZeroSm50Patched" ->
      """//--------------------- .text.predicate_probe     --------------------------
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
        |.L_x_1:
        |""".stripMargin,
    "logicSm50" ->
      """//--------------------- .text.logic_probe         --------------------------
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
        |.L_x_1:
        |""".stripMargin,
    "lop3PredicateSm70Patched" ->
      """//--------------------- .text.probe               --------------------------
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
        |.L_x_1:
        |""".stripMargin,
    "lop3PredicateSm75" ->
      """//--------------------- .text.probe               --------------------------
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
        |.L_x_1:
        |""".stripMargin,
    "lop3PredicateSm80"       -> lop3PredicateSm80,
    "lop3PredicateSm86"       -> lop3PredicateSm80,
    "lop3PredicateSm89"       -> lop3PredicateSm80,
    "sgxtRawWidthSm70Patched" ->
      """//--------------------- .text.sgxt_probe          --------------------------
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
        |.L_x_1:
        |""".stripMargin,
    "sgxtSm70" ->
      """//--------------------- .text.sgxt_probe          --------------------------
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
        |.L_x_1:
        |""".stripMargin
  )
}
