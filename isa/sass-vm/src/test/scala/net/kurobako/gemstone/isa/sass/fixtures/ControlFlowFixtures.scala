package net.kurobako.gemstone.isa.sass.fixtures

object ControlFlowFixtures {
  val bmovClearCuda: String =
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

  val callCuda: String =
    """__device__ __noinline__ unsigned inner(unsigned x) { return 3 * x + 1; }
      |__device__ __noinline__ unsigned outer(unsigned x) { return inner(x) + inner(x + 1); }
      |
      |extern "C" __global__ void calls(unsigned *output) {
      |  const unsigned lane = threadIdx.x;
      |  unsigned value = lane;
      |  if ((lane & 1) == 0) value = outer(value);
      |  output[blockIdx.x * blockDim.x + lane] = value;
      |}
      |""".stripMargin

  val callIndirectCuda: String =
    """__device__ __noinline__ unsigned twice(unsigned x) { return 2 * x; }
      |__device__ __noinline__ unsigned thrice(unsigned x) { return 3 * x; }
      |
      |extern "C" __global__ void indirect_call(unsigned *output) {
      |  const unsigned lane = threadIdx.x;
      |  unsigned (*function)(unsigned) = (lane & 1) ? twice : thrice;
      |  output[blockIdx.x * blockDim.x + lane] = function(lane);
      |}
      |""".stripMargin

  val clock64Cuda: String =
    """#include <stdint.h>
      |extern "C" __global__ void probe(uint32_t *out) {
      |  long long first = clock64();
      |  long long second = clock64();
      |  out[0] = second > first;
      |  out[1] = static_cast<uint32_t>(clock64() >> 32) >= static_cast<uint32_t>(first >> 32);
      |}
      |""".stripMargin

  val virtualIdCuda: String =
    """#include <stdint.h>
      |extern "C" __global__ void probe(uint32_t *out) {
      |  uint32_t smid, nsmid, warpid, nwarpid;
      |  asm volatile("mov.u32 %0, %%smid;" : "=r"(smid));
      |  asm volatile("mov.u32 %0, %%nsmid;" : "=r"(nsmid));
      |  asm volatile("mov.u32 %0, %%warpid;" : "=r"(warpid));
      |  asm volatile("mov.u32 %0, %%nwarpid;" : "=r"(nwarpid));
      |  uint32_t *o = out + threadIdx.x * 4;
      |  o[0] = smid;
      |  o[1] = nsmid;
      |  o[2] = warpid;
      |  o[3] = nwarpid;
      |}
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "virtualIdSm50" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |                                                                           /* 0x001c4400fe0007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;                  /* 0x4c98078000870001 */
        |        /*0010*/         {         MOV32I R7, 0x40 ;                       /* 0x010000000407f007 */
        |        /*0018*/                   S2R R2, SR_TID.X         }
        |                                                                           /* 0xf0c8000002170002 */
        |                                                                           /* 0x001df400ea200731 */
        |        /*0028*/                   S2R R0, SR_VIRTID ;                     /* 0xf0c8000000370000 */
        |        /*0030*/                   S2R R4, SR_VIRTCFG ;                    /* 0xf0c8000000270004 */
        |        /*0038*/                   S2R R5, SR_VIRTID ;                     /* 0xf0c8000000370005 */
        |                                                                           /* 0x001f8402fea00ff1 */
        |        /*0048*/                   SHL R2, R2, 0x2 ;                       /* 0x3848000000270202 */
        |        /*0050*/                   BFE.U32 R0, R0, 0x914 ;                 /* 0x3800000091470000 */
        |        /*0058*/                   SHR.U32 R3, R2, 0x1e ;                  /* 0x3828000001e70203 */
        |                                                                           /* 0x011fd004fe2007f1 */
        |        /*0068*/                   ISCADD R2.CC, R2, c[0x0][0x140], 0x2 ;  /* 0x4c18810005070202 */
        |        /*0070*/                   BFE.U32 R4, R4, 0x914 ;                 /* 0x3800000091470404 */
        |        /*0078*/                   BFE.U32 R5, R5, 0x608 ;                 /* 0x3800000060870505 */
        |                                                                           /* 0x001fc400fe2007f2 */
        |        /*0088*/                   IADD.X R3, R3, c[0x0][0x144] ;          /* 0x4c10080005170303 */
        |        /*0090*/                   STG.E [R2], R0 ;                        /* 0xeedc200000070200 */
        |        /*0098*/                   STG.E [R2+0x4], R4 ;                    /* 0xeedc200000470204 */
        |                                                                           /* 0x001fb800fe4007f1 */
        |        /*00a8*/                   STG.E [R2+0x8], R5 ;                    /* 0xeedc200000870205 */
        |        /*00b0*/                   STG.E [R2+0xc], R7 ;                    /* 0xeedc200000c70207 */
        |        /*00b8*/                   NOP ;                                   /* 0x50b0000000070f00 */
        |                                                                           /* 0x001f8000ffe007ff */
        |        /*00c8*/                   EXIT ;                                  /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "virtualIdSm60" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=11"
        |	.align	32
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |                                                                           /* 0x001c5800fe0007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;                  /* 0x4c98078000870001 */
        |        /*0010*/         {         MOV R3, c[0x0][0x108] ;                 /* 0x4c98078004270003 */
        |        /*0018*/                   S2R R0, SR_VIRTID         }
        |                                                                           /* 0xf0c8000000370000 */
        |                                                                           /* 0x001d4400e62007f0 */
        |        /*0028*/         {         ISETP.EQ.AND P0, PT, R3, RZ, PT ;       /* 0x5b6503800ff70307 */
        |        /*0030*/                   S2R R2, SR_VIRTID         }
        |                                                                           /* 0xf0c8000000370002 */
        |        /*0038*/                   S2R R4, SR_TID.X ;                      /* 0xf0c8000002170004 */
        |                                                                           /* 0x005fc400f3a00775 */
        |        /*0048*/                   S2R R7, SR_VIRTID ;                     /* 0xf0c8000000370007 */
        |        /*0050*/                   S2R R9, SR_VIRTCFG ;                    /* 0xf0c8000000270009 */
        |        /*0058*/                   BFE.U32 R2, R2, 0x708 ;                 /* 0x3800000070870202 */
        |                                                                           /* 0x011fc400fe2027e5 */
        |        /*0068*/                   SHL R4, R4, 0x2 ;                       /* 0x3848000000270404 */
        |        /*0070*/                   LOP32I.AND R3, R2, 0x2 ;                /* 0x0400000000270203 */
        |        /*0078*/                   BFE.U32 R7, R7, 0x708 ;                 /* 0x3800000070870707 */
        |                                                                           /* 0x001f8c40fe200ff1 */
        |        /*0088*/                   BFE.U32 R0, R0, 0x914 ;                 /* 0x3800000091470000 */
        |        /*0090*/                   SHR.U32 R10, R4.reuse, 0x1e ;           /* 0x3828000001e7040a */
        |        /*0098*/                   ISCADD R2.CC, R4, c[0x0][0x140], 0x2 ;  /* 0x4c18810005070402 */
        |                                                                           /* 0x001fc400fe2207f1 */
        |        /*00a8*/                   LOP32I.AND R8, R7.reuse, 0x1 ;          /* 0x0400000000170708 */
        |        /*00b0*/                   SHR.U32 R5, R3, 0x1 ;                   /* 0x3828000000170305 */
        |        /*00b8*/                   SHR.U32 R6, R7, 0x2 ;                   /* 0x3828000000270706 */
        |                                                                           /* 0x001f8800fe6087e1 */
        |        /*00c8*/                   BFE.U32 R4, R9, 0x914 ;                 /* 0x3800000091470904 */
        |        /*00d0*/                   IADD.X R3, R10, c[0x0][0x144] ;         /* 0x4c10080005170a03 */
        |        /*00d8*/              @!P0 ISCADD R0, R0, R5, 0x1 ;                /* 0x5c18008000580000 */
        |                                                                           /* 0x001fc000fe4007f0 */
        |        /*00e8*/         {         MOV32I R5, 0x40 ;                       /* 0x010000000407f005 */
        |        /*00f0*/                   STG.E [R2], R0         }
        |                                                                           /* 0xeedc200000070200 */
        |        /*00f8*/         {    @!P0 ISCADD R7, R6, R8, 0x1 ;                /* 0x5c18008000880607 */
        |                                                                           /* 0x001fc800fe0007f2 */
        |        /*0108*/                   STG.E [R2+0xc], R5         }
        |                                                                           /* 0xeedc200000c70205 */
        |        /*0110*/         {    @!P0 SHL R4, R4, 0x1 ;                       /* 0x3848000000180404 */
        |        /*0118*/                   STG.E [R2+0x8], R7         }
        |                                                                           /* 0xeedc200000870207 */
        |                                                                           /* 0x001ffc00ffe007f1 */
        |        /*0128*/                   STG.E [R2+0x4], R4 ;                    /* 0xeedc200000470204 */
        |        /*0130*/                   EXIT ;                                  /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "clock64Sm50" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |                                                                      /* 0x001fd800fec007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;             /* 0x4c98078000870001 */
        |        /*0010*/                   CS2R R0, SR_CLOCKHI ;              /* 0x50c8000005170000 */
        |        /*0018*/                   CS2R R3, SR_CLOCKLO ;              /* 0x50c8000005070003 */
        |                                                                      /* 0x001fd800fec007ed */
        |        /*0028*/                   ISETP.LT.AND P0, PT, R3, RZ, PT ;  /* 0x5b6303800ff70307 */
        |        /*0030*/              @!P0 CS2R R0, SR_CLOCKHI ;              /* 0x50c8000005180000 */
        |        /*0038*/                   CS2R R2, SR_CLOCKHI ;              /* 0x50c8000005170002 */
        |                                                                      /* 0x001fd800fda007f6 */
        |        /*0048*/                   CS2R R4, SR_CLOCKLO ;              /* 0x50c8000005070004 */
        |        /*0050*/                   ISETP.LT.AND P0, PT, R4, RZ, PT ;  /* 0x5b6303800ff70407 */
        |        /*0058*/              @!P0 CS2R R2, SR_CLOCKHI ;              /* 0x50c8000005180002 */
        |                                                                      /* 0x001fc400fea007f1 */
        |        /*0068*/                   IADD RZ.CC, -R3, R4 ;              /* 0x5c128000004703ff */
        |        /*0070*/                   MOV R3, c[0x0][0x144] ;            /* 0x4c98078005170003 */
        |        /*0078*/                   ISET.GT.X.AND R4, R2, R0, PT ;     /* 0x5b590b8000070204 */
        |                                                                      /* 0x0003d000fe4007f5 */
        |        /*0088*/                   MOV R2, c[0x0][0x140] ;            /* 0x4c98078005070002 */
        |        /*0090*/                   LOP32I.AND R4, R4, 0x1 ;           /* 0x0400000000170404 */
        |        /*0098*/                   STG.E [R2], R4 ;                   /* 0xeedc200000070204 */
        |                                                                      /* 0x001fb401fec007f6 */
        |        /*00a8*/                   CS2R R5, SR_CLOCKHI ;              /* 0x50c8000005170005 */
        |        /*00b0*/                   CS2R R4, SR_CLOCKLO ;              /* 0x50c8000005070004 */
        |        /*00b8*/                   ISETP.LT.AND P0, PT, R4, RZ, PT ;  /* 0x5b6303800ff70407 */
        |                                                                      /* 0x001fc800fec007f6 */
        |        /*00c8*/              @!P0 CS2R R5, SR_CLOCKHI ;              /* 0x50c8000005180005 */
        |        /*00d0*/                   ISET.GE.U32.AND R0, R5, R0, PT ;   /* 0x5b5c038000070500 */
        |        /*00d8*/                   LOP32I.AND R0, R0, 0x1 ;           /* 0x0400000000170000 */
        |                                                                      /* 0x001ffc00ffe007f1 */
        |        /*00e8*/                   STG.E [R2+0x4], R0 ;               /* 0xeedc200000470200 */
        |        /*00f0*/                   EXIT ;                             /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "clock64Sm60" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_7 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |                                                                                     /* 0x001fc400fec007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;                            /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R3, RZ ;                                      /* 0x5c9807800ff70003 */
        |.L_x_1:
        |        /*0018*/                   CS2R RZ, SR_CLOCKHI ;                             /* 0x50c80000051700ff */
        |                                                                                     /* 0x001f9800fe2007e6 */
        |        /*0028*/                   CS2R R0, SR_CLOCKHI ;                             /* 0x50c8000005170000 */
        |        /*0030*/                   CS2R RZ, SR_CLOCKLO ;                             /* 0x50c80000050700ff */
        |        /*0038*/                   CS2R R2, SR_CLOCKLO ;                             /* 0x50c8000005070002 */
        |                                                                                     /* 0x001fb400fcc007f1 */
        |        /*0048*/                   CS2R RZ, SR_CLOCKHI ;                             /* 0x50c80000051700ff */
        |        /*0050*/                   CS2R R5, SR_CLOCKHI ;                             /* 0x50c8000005170005 */
        |        /*0058*/                   ISETP.NE.U32.AND P0, PT, R0, R5, PT ;             /* 0x5b6a038000570007 */
        |                                                                                     /* 0x001fb400fec007fd */
        |        /*0068*/              @!P0 BRA `(.L_x_0) ;                                   /* 0xe24000000208000f */
        |        /*0070*/                   IADD32I R3, R3, 0x1 ;                             /* 0x1c00000000170303 */
        |        /*0078*/                   ISETP.GE.U32.AND P0, PT, R3, c[0x0][0x10c], PT ;  /* 0x4b6c038004370307 */
        |                                                                                     /* 0x001fc400fec007fd */
        |        /*0088*/              @!P0 BRA `(.L_x_1) ;                                   /* 0xe2400ffff888000f */
        |.L_x_0:
        |        /*0090*/                   MOV R4, RZ ;                                      /* 0x5c9807800ff70004 */
        |.L_x_3:
        |        /*0098*/                   CS2R RZ, SR_CLOCKHI ;                             /* 0x50c80000051700ff */
        |                                                                                     /* 0x001f9800fe2007e6 */
        |        /*00a8*/                   CS2R R0, SR_CLOCKHI ;                             /* 0x50c8000005170000 */
        |        /*00b0*/                   CS2R RZ, SR_CLOCKLO ;                             /* 0x50c80000050700ff */
        |        /*00b8*/                   CS2R R3, SR_CLOCKLO ;                             /* 0x50c8000005070003 */
        |                                                                                     /* 0x001fb400fcc007f1 */
        |        /*00c8*/                   CS2R RZ, SR_CLOCKHI ;                             /* 0x50c80000051700ff */
        |        /*00d0*/                   CS2R R6, SR_CLOCKHI ;                             /* 0x50c8000005170006 */
        |        /*00d8*/                   ISETP.NE.U32.AND P0, PT, R0, R6, PT ;             /* 0x5b6a038000670007 */
        |                                                                                     /* 0x001fb400fec007fd */
        |        /*00e8*/              @!P0 BRA `(.L_x_2) ;                                   /* 0xe24000000208000f */
        |        /*00f0*/                   IADD32I R4, R4, 0x1 ;                             /* 0x1c00000000170404 */
        |        /*00f8*/                   ISETP.GE.U32.AND P0, PT, R4, c[0x0][0x10c], PT ;  /* 0x4b6c038004370407 */
        |                                                                                     /* 0x001fc400fe2007fd */
        |        /*0108*/              @!P0 BRA `(.L_x_3) ;                                   /* 0xe2400ffff888000f */
        |.L_x_2:
        |        /*0110*/                   IADD RZ.CC, -R2, R3 ;                             /* 0x5c128000003702ff */
        |        /*0118*/                   MOV R2, c[0x0][0x140] ;                           /* 0x4c98078005070002 */
        |                                                                                     /* 0x001fd800fe6007f1 */
        |        /*0128*/                   MOV R3, c[0x0][0x144] ;                           /* 0x4c98078005170003 */
        |        /*0130*/                   MOV R4, RZ ;                                      /* 0x5c9807800ff70004 */
        |        /*0138*/                   ISET.GT.X.AND R0, R6, R5, PT ;                    /* 0x5b590b8000570600 */
        |                                                                                     /* 0x001fc4001e8007e2 */
        |        /*0148*/                   LOP32I.AND R0, R0, 0x1 ;                          /* 0x0400000000170000 */
        |        /*0150*/                   STG.E [R2], R0 ;                                  /* 0xeedc200000070200 */
        |.L_x_5:
        |        /*0158*/                   CS2R RZ, SR_CLOCKHI ;                             /* 0x50c80000051700ff */
        |                                                                                     /* 0x001f9800fe200fe6 */
        |        /*0168*/                   CS2R R0, SR_CLOCKHI ;                             /* 0x50c8000005170000 */
        |        /*0170*/                   CS2R RZ, SR_CLOCKHI ;                             /* 0x50c80000051700ff */
        |        /*0178*/                   CS2R R6, SR_CLOCKHI ;                             /* 0x50c8000005170006 */
        |                                                                                     /* 0x001fd800ffa007ed */
        |        /*0188*/                   ISETP.NE.U32.AND P0, PT, R0, R6, PT ;             /* 0x5b6a038000670007 */
        |        /*0190*/              @!P0 BRA `(.L_x_4) ;                                   /* 0xe24000000208000f */
        |        /*0198*/                   IADD32I R4, R4, 0x1 ;                             /* 0x1c00000000170404 */
        |                                                                                     /* 0x001f9800ffa007ed */
        |        /*01a8*/                   ISETP.GE.U32.AND P0, PT, R4, c[0x0][0x10c], PT ;  /* 0x4b6c038004370407 */
        |        /*01b0*/              @!P0 BRA `(.L_x_5) ;                                   /* 0xe2400ffffa08000f */
        |.L_x_4:
        |        /*01b8*/                   ISET.GE.U32.AND R0, R6, R5, PT ;                  /* 0x5b5c038000570600 */
        |                                                                                     /* 0x001ffc00fe2007e2 */
        |        /*01c8*/                   LOP32I.AND R0, R0, 0x1 ;                          /* 0x0400000000170000 */
        |        /*01d0*/                   STG.E [R2+0x4], R0 ;                              /* 0xeedc200000470200 */
        |        /*01d8*/                   EXIT ;                                            /* 0xe30000000007000f */
        |.L_x_6:
        |                                                                                     /* 0x001f8000fc0007ff */
        |        /*01e8*/                   BRA `(.L_x_6) ;                                   /* 0xe2400fffff07000f */
        |        /*01f0*/                   NOP;                                              /* 0x50b0000000070f00 */
        |        /*01f8*/                   NOP;                                              /* 0x50b0000000070f00 */
        |.L_x_7:
        |""".stripMargin,
    "bmovClearSm70" ->
      """//--------------------- .text.divergent_loop      --------------------------
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
        |.L_x_7:
        |""".stripMargin,
    "bmovClearSm75" ->
      """//--------------------- .text.divergent_loop      --------------------------
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
        |.L_x_7:
        |""".stripMargin,
    "callIndirectSm35" ->
      """//--------------------- .text.indirect_call       --------------------------
        |	.section	.text.indirect_call,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=5"
        |	.align	64
        |        .global         indirect_call
        |        .type           indirect_call,@function
        |        .size           indirect_call,(.L_x_3 - indirect_call)
        |        .other          indirect_call,@"STO_CUDA_ENTRY STV_DEFAULT"
        |indirect_call:
        |.text.indirect_call:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   S2R R2, SR_TID.X;
        |        /*0018*/                   LOP32I.AND R0, R2, 0x1;
        |        /*0020*/                   MOV R4, R2;
        |        /*0028*/                   ISETP.NE.U32.AND P0, PT, R0, 0x1, PT;
        |        /*0030*/                   MOV32I R0, 0xa8;
        |        /*0038*/              @!P0 MOV32I R0, 0x98;
        |        /*0048*/                   PRET `(.L_x_0);
        |        /*0050*/                   BRX R0 -0x58;
        |.L_x_0:
        |        /*0058*/                   S2R R3, SR_CTAID.X;
        |        /*0060*/                   IMAD R0, R3, c[0x0][0x28], R2;
        |        /*0068*/                   MOV32I R3, 0x4;
        |        /*0070*/                   ISCADD R2.CC, R0, c[0x0][0x140], 0x2;
        |        /*0078*/                   IMAD.U32.U32.HI.X R3, R0, R3, c[0x0][0x144];
        |        /*0088*/                   ST.E [R2], R4;
        |        /*0090*/                   EXIT;
        |        .type           $indirect_call$_Z5twicej,@function
        |        .size           $indirect_call$_Z5twicej,($indirect_call$_Z6thricej - $indirect_call$_Z5twicej)
        |$indirect_call$_Z5twicej:
        |        /*0098*/                   SHF.L R4, RZ, 0x1, R4;
        |        /*00a0*/                   RET;
        |        .type           $indirect_call$_Z6thricej,@function
        |        .size           $indirect_call$_Z6thricej,(.L_x_3 - $indirect_call$_Z6thricej)
        |$indirect_call$_Z6thricej:
        |        /*00a8*/                   IMUL32I R4, R4, 0x3;
        |        /*00b0*/                   RET;
        |.L_x_3:
        |""".stripMargin,
    "callSm35" ->
      """//--------------------- .text.calls               --------------------------
        |	.section	.text.calls,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=5"
        |	.align	64
        |        .global         calls
        |        .type           calls,@function
        |        .size           calls,(.L_x_3 - calls)
        |        .other          calls,@"STO_CUDA_ENTRY STV_DEFAULT"
        |calls:
        |.text.calls:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   S2R R2, SR_TID.X;
        |        /*0018*/                   SSY `(.L_x_0);
        |        /*0020*/                   LOP32I.AND R0, R2, 0x1;
        |        /*0028*/                   MOV R4, R2;
        |        /*0030*/                   ISETP.NE.U32.AND P0, PT, R0, 0x1, PT;
        |        /*0038*/              @!P0 NOP.S;
        |        /*0048*/                   CAL `($calls$_Z5outerj);
        |        /*0050*/                   NOP.S;
        |.L_x_0:
        |        /*0058*/                   S2R R3, SR_CTAID.X;
        |        /*0060*/                   IMAD R0, R3, c[0x0][0x28], R2;
        |        /*0068*/                   MOV32I R3, 0x4;
        |        /*0070*/                   ISCADD R2.CC, R0, c[0x0][0x140], 0x2;
        |        /*0078*/                   IMAD.U32.U32.HI.X R3, R0, R3, c[0x0][0x144];
        |        /*0088*/                   ST.E [R2], R4;
        |        /*0090*/                   EXIT;
        |        .type           $calls$_Z5innerj,@function
        |        .size           $calls$_Z5innerj,($calls$_Z5outerj - $calls$_Z5innerj)
        |$calls$_Z5innerj:
        |        /*0098*/                   MOV32I R4, 0x1;
        |        /*00a0*/                   IMAD R4, R3, 0x3, R4;
        |        /*00a8*/                   RET;
        |        .type           $calls$_Z5outerj,@function
        |        .size           $calls$_Z5outerj,(.L_x_3 - $calls$_Z5outerj)
        |$calls$_Z5outerj:
        |        /*00b0*/                   MOV R3, R2;
        |        /*00b8*/                   CAL `($calls$_Z5innerj);
        |        /*00c8*/                   MOV R0, R4;
        |        /*00d0*/                   IADD32I R3, R2, 0x1;
        |        /*00d8*/                   CAL `($calls$_Z5innerj);
        |        /*00e0*/                   IADD R4, R4, R0;
        |        /*00e8*/                   RET;
        |.L_x_3:
        |""".stripMargin,
    "callSm50" ->
      """//--------------------- .text.calls               --------------------------
        |	.section	.text.calls,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=5"
        |	.align	32
        |        .global         calls
        |        .type           calls,@function
        |        .size           calls,(.L_x_7 - calls)
        |        .other          calls,@"STO_CUDA_ENTRY STV_DEFAULT"
        |calls:
        |.text.calls:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   SSY `(.L_x_0) ;
        |        /*0028*/                   LOP32I.AND R0, R2, 0x1 ;
        |        /*0030*/                   ISETP.NE.U32.AND P0, PT, R0, 0x1, PT ;
        |.L_x_2:
        |        /*0038*/         {         MOV R0, R2 ;
        |        /*0048*/              @!P0 SYNC         }
        |        /*0050*/                   CAL `($calls$_Z5outerj) ;
        |.L_x_4:
        |        /*0058*/                   SYNC ;
        |.L_x_0:
        |        /*0068*/                   S2R R3, SR_CTAID.X ;
        |        /*0070*/                   XMAD R2, R3.reuse, c[0x0] [0x8], R2 ;
        |        /*0078*/                   XMAD.MRG R4, R3.reuse, c[0x0] [0x8].H1, RZ ;
        |        /*0088*/                   XMAD.PSL.CBCC R2, R3.H1, R4.H1, R2 ;
        |        /*0090*/                   SHR.U32 R3, R2.reuse, 0x1e ;
        |        /*0098*/                   ISCADD R2.CC, R2, c[0x0][0x140], 0x2 ;
        |        /*00a8*/                   IADD.X R3, R3, c[0x0][0x144] ;
        |        /*00b0*/                   STG.E [R2], R0 ;
        |        /*00b8*/                   EXIT ;
        |        .type           $calls$_Z5innerj,@function
        |        .size           $calls$_Z5innerj,($calls$_Z5outerj - $calls$_Z5innerj)
        |$calls$_Z5innerj:
        |        /*00c8*/                   MOV32I R3, 0x1 ;
        |        /*00d0*/                   XMAD R3, R4.reuse, 0x3, R3 ;
        |        /*00d8*/         {         XMAD.PSL R3, R4.H1, 0x3, R3 ;
        |        /*00e8*/                   RET         }
        |        .type           $calls$_Z5outerj,@function
        |        .size           $calls$_Z5outerj,(.L_x_7 - $calls$_Z5outerj)
        |$calls$_Z5outerj:
        |        /*00f0*/                   MOV R4, R2 ;
        |        /*00f8*/                   CAL `($calls$_Z5innerj) ;
        |        /*0108*/                   MOV R0, R3 ;
        |        /*0110*/                   IADD32I R4, R2, 0x1 ;
        |        /*0118*/                   CAL `($calls$_Z5innerj) ;
        |        /*0128*/         {         IADD R0, R3, R0 ;
        |        /*0130*/                   RET         }
        |.L_x_7:
        |""".stripMargin,
    "callSm60" ->
      """//--------------------- .text.calls               --------------------------
        |	.section	.text.calls,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=5"
        |	.align	32
        |        .global         calls
        |        .type           calls,@function
        |        .size           calls,(.L_x_7 - calls)
        |        .other          calls,@"STO_CUDA_ENTRY STV_DEFAULT"
        |calls:
        |.text.calls:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   SSY `(.L_x_0) ;
        |        /*0028*/                   LOP32I.AND R0, R2, 0x1 ;
        |        /*0030*/                   ISETP.NE.U32.AND P0, PT, R0, 0x1, PT ;
        |.L_x_2:
        |        /*0038*/         {         MOV R0, R2 ;
        |        /*0048*/              @!P0 SYNC         }
        |        /*0050*/                   CAL `($calls$_Z5outerj) ;
        |.L_x_4:
        |        /*0058*/                   SYNC ;
        |.L_x_0:
        |        /*0068*/                   S2R R3, SR_CTAID.X ;
        |        /*0070*/                   XMAD R2, R3, c[0x0] [0x8], R2 ;
        |        /*0078*/                   XMAD.MRG R4, R3, c[0x0] [0x8].H1, RZ ;
        |        /*0088*/                   XMAD.PSL.CBCC R2, R3.H1, R4.H1, R2 ;
        |        /*0090*/                   SHR.U32 R3, R2.reuse, 0x1e ;
        |        /*0098*/                   ISCADD R2.CC, R2, c[0x0][0x140], 0x2 ;
        |        /*00a8*/                   IADD.X R3, R3, c[0x0][0x144] ;
        |        /*00b0*/                   STG.E [R2], R0 ;
        |        /*00b8*/                   EXIT ;
        |        .type           $calls$_Z5innerj,@function
        |        .size           $calls$_Z5innerj,($calls$_Z5outerj - $calls$_Z5innerj)
        |$calls$_Z5innerj:
        |        /*00c8*/                   MOV32I R3, 0x1 ;
        |        /*00d0*/                   XMAD R3, R4.reuse, 0x3, R3 ;
        |        /*00d8*/         {         XMAD.PSL R3, R4.H1, 0x3, R3 ;
        |        /*00e8*/                   RET         }
        |        .type           $calls$_Z5outerj,@function
        |        .size           $calls$_Z5outerj,(.L_x_7 - $calls$_Z5outerj)
        |$calls$_Z5outerj:
        |        /*00f0*/                   MOV R4, R2 ;
        |        /*00f8*/                   CAL `($calls$_Z5innerj) ;
        |        /*0108*/                   MOV R0, R3 ;
        |        /*0110*/                   IADD32I R4, R2, 0x1 ;
        |        /*0118*/                   CAL `($calls$_Z5innerj) ;
        |        /*0128*/         {         IADD R0, R3, R0 ;
        |        /*0130*/                   RET         }
        |.L_x_7:
        |""".stripMargin
  )
}
