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

  val exitPredicateCuda: String =
    """extern "C" __global__ void exit_unless_end(unsigned *out, unsigned last) {
      |  unsigned lane = threadIdx.x;
      |  if (lane != 0 && lane != last) return;
      |  out[lane] = lane + 1;
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
        |""".stripMargin,
    "spinLockSm89" ->
      """//--------------------- .text.spin                --------------------------
        |	.section	.text.spin,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         spin
        |        .type           spin,@function
        |        .size           spin,(.L_x_3 - spin)
        |        .other          spin,@"STO_CUDA_ENTRY STV_DEFAULT"
        |spin:
        |.text.spin:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   BSSY B0, `(.L_x_0) ;
        |        /*0020*/                   IMAD.MOV.U32 R2, RZ, RZ, c[0x0][0x160] ;
        |        /*0030*/                   MOV R3, c[0x0][0x164] ;
        |        /*0040*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x1 ;
        |        /*0050*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |.L_x_1:
        |        /*0060*/                   MOV R4, RZ ;
        |        /*0070*/                   YIELD ;
        |        /*0080*/                   ATOMG.E.CAS.STRONG.GPU PT, R0, [R2], R4, R5 ;
        |        /*0090*/                   ISETP.NE.AND P0, PT, R0, RZ, PT ;
        |        /*00a0*/               @P0 BRA `(.L_x_1) ;
        |        /*00b0*/                   BSYNC B0 ;
        |.L_x_0:
        |        /*00c0*/                   IMAD.MOV.U32 R4, RZ, RZ, c[0x0][0x168] ;
        |        /*00d0*/                   MOV R5, c[0x0][0x16c] ;
        |        /*00e0*/                   LDG.E R0, [R4.64] ;
        |        /*00f0*/                   S2R R7, SR_TID.X ;
        |        /*0100*/                   IADD3 R7, R0, R7, RZ ;
        |        /*0110*/                   STG.E [R4.64], R7 ;
        |        /*0120*/                   MEMBAR.SC.GPU ;
        |        /*0130*/                   ERRBAR;
        |        /*0140*/                   CCTL.IVALL ;
        |        /*0150*/                   ATOMG.E.EXCH.STRONG.GPU PT, RZ, [R2.64], RZ ;
        |        /*0160*/                   EXIT ;
        |.L_x_3:
        |""".stripMargin,
    "nanosleepOneSm89" ->
      """//--------------------- .text._Z10sleepShapeILi0EEvPjS0_ --------------------------
        |	.section	.text._Z10sleepShapeILi0EEvPjS0_,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         _Z10sleepShapeILi0EEvPjS0_
        |        .type           _Z10sleepShapeILi0EEvPjS0_,@function
        |        .size           _Z10sleepShapeILi0EEvPjS0_,(.L_x_21 - _Z10sleepShapeILi0EEvPjS0_)
        |        .other          _Z10sleepShapeILi0EEvPjS0_,@"STO_CUDA_ENTRY STV_DEFAULT"
        |_Z10sleepShapeILi0EEvPjS0_:
        |.text._Z10sleepShapeILi0EEvPjS0_:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   BSSY B0, `(.L_x_13) ;
        |        /*0050*/                   LOP3.LUT R0, R2.reuse, 0x1, RZ, 0xc0, !PT ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;
        |        /*0070*/                   ISETP.NE.U32.AND P0, PT, R0, 0x1, PT ;
        |        /*0080*/               @P0 BRA `(.L_x_14) ;
        |        /*0090*/                   NANOSLEEP 0xf4240 ;
        |        /*00a0*/                   NOP ;
        |.L_x_14:
        |        /*00b0*/                   BSYNC B0 ;
        |.L_x_13:
        |        /*00c0*/                   VOTE.ANY R5, PT, PT ;
        |        /*00d0*/                   STG.E [R2.64], R5 ;
        |        /*00e0*/                   EXIT ;
        |.L_x_21:
        |""".stripMargin,
    "guardedYieldSm89" ->
      """//--------------------- .text._Z10yieldShapeILi0EEvPjS0_ --------------------------
        |	.section	.text._Z10yieldShapeILi0EEvPjS0_,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         _Z10yieldShapeILi0EEvPjS0_
        |        .type           _Z10yieldShapeILi0EEvPjS0_,@function
        |        .size           _Z10yieldShapeILi0EEvPjS0_,(.L_x_21 - _Z10yieldShapeILi0EEvPjS0_)
        |        .other          _Z10yieldShapeILi0EEvPjS0_,@"STO_CUDA_ENTRY STV_DEFAULT"
        |_Z10yieldShapeILi0EEvPjS0_:
        |.text._Z10yieldShapeILi0EEvPjS0_:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   BSSY B0, `(.L_x_13) ;
        |        /*0050*/                   LOP3.LUT R0, R2.reuse, 0x1, RZ, 0xc0, !PT ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;
        |        /*0070*/                   ISETP.EQ.U32.AND P0, PT, R0, 0x1, PT ;
        |        /*0080*/               @P0 YIELD ;
        |        /*0090*/                   BSYNC B0 ;
        |.L_x_13:
        |        /*00a0*/                   VOTE.ANY R5, PT, PT ;
        |        /*00b0*/                   STG.E [R2.64], R5 ;
        |        /*00c0*/                   EXIT ;
        |.L_x_21:
        |""".stripMargin,
    "spinMaskSm89" ->
      """//--------------------- .text.spin_mask           --------------------------
        |	.section	.text.spin_mask,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         spin_mask
        |        .type           spin_mask,@function
        |        .size           spin_mask,(.L_x_3 - spin_mask)
        |        .other          spin_mask,@"STO_CUDA_ENTRY STV_DEFAULT"
        |spin_mask:
        |.text.spin_mask:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   BSSY B0, `(.L_x_0) ;
        |        /*0020*/                   MOV R2, c[0x0][0x160] ;
        |        /*0030*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x164] ;
        |        /*0040*/                   MOV R5, 0x1 ;
        |        /*0050*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |.L_x_1:
        |        /*0060*/                   IMAD.MOV.U32 R4, RZ, RZ, RZ ;
        |        /*0070*/                   YIELD ;
        |        /*0080*/                   ATOMG.E.CAS.STRONG.GPU PT, R0, [R2], R4, R5 ;
        |        /*0090*/                   ISETP.NE.AND P0, PT, R0, RZ, PT ;
        |        /*00a0*/               @P0 BRA `(.L_x_1) ;
        |        /*00b0*/                   BSYNC B0 ;
        |.L_x_0:
        |        /*00c0*/                   S2R R11, SR_TID.X ;
        |        /*00d0*/                   MOV R4, 0x4 ;
        |        /*00e0*/                   IMAD.MOV.U32 R6, RZ, RZ, c[0x0][0x168] ;
        |        /*00f0*/                   VOTE.ANY R9, PT, PT ;
        |        /*0100*/                   MOV R7, c[0x0][0x16c] ;
        |        /*0110*/                   IMAD.WIDE.U32 R4, R11, R4, c[0x0][0x170] ;
        |        /*0120*/                   STG.E [R4.64], R9 ;
        |        /*0130*/                   LDG.E.STRONG.SYS R0, [R6.64] ;
        |        /*0140*/                   IMAD.IADD R11, R0, 0x1, R11 ;
        |        /*0150*/                   STG.E.STRONG.SYS [R6.64], R11 ;
        |        /*0160*/                   MEMBAR.SC.GPU ;
        |        /*0170*/                   ERRBAR;
        |        /*0180*/                   CCTL.IVALL ;
        |        /*0190*/                   ATOMG.E.EXCH.STRONG.GPU PT, RZ, [R2.64], RZ ;
        |        /*01a0*/                   EXIT ;
        |.L_x_3:
        |""".stripMargin,
    "exitPredicateSm75" ->
      """//--------------------- .text.exit_unless_end     --------------------------
        |	.section	.text.exit_unless_end,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         exit_unless_end
        |        .type           exit_unless_end,@function
        |        .size           exit_unless_end,(.L_x_1 - exit_unless_end)
        |        .other          exit_unless_end,@"STO_CUDA_ENTRY STV_DEFAULT"
        |exit_unless_end:
        |.text.exit_unless_end:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   ISETP.NE.AND P0, PT, R2.reuse, c[0x0][0x168], PT ;
        |        /*0030*/                   ISETP.NE.AND P1, PT, R2, RZ, PT ;
        |        /*0040*/               @P0 EXIT P1 ;
        |        /*0050*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x4 ;
        |        /*0060*/                   IADD3 R5, R2, 0x1, RZ ;
        |        /*0070*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;
        |        /*0080*/                   STG.E.SYS [R2], R5 ;
        |        /*0090*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "exitPredicateSm120" ->
      """//--------------------- .text.exit_unless_end     --------------------------
        |	.section	.text.exit_unless_end,"ax",@progbits
        |	.align	128
        |        .global         exit_unless_end
        |        .type           exit_unless_end,@function
        |        .size           exit_unless_end,(.L_x_1 - exit_unless_end)
        |        .other          exit_unless_end,@"STO_CUDA_ENTRY STV_DEFAULT"
        |exit_unless_end:
        |.text.exit_unless_end:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R7, SR_TID.X ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x388] ;
        |        /*0030*/                   ISETP.NE.U32.AND P0, PT, R7.reuse, UR4, PT ;
        |        /*0040*/                   ISETP.NE.U32.AND P1, PT, R7, RZ, PT ;
        |        /*0050*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0060*/               @P0 EXIT P1 ;
        |        /*0070*/                   LDC.64 R2, c[0x0][0x380] ;
        |        /*0080*/                   IADD R5, R7.reuse, 0x1 ;
        |        /*0090*/                   IMAD.WIDE.U32 R2, R7, 0x4, R2 ;
        |        /*00a0*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*00b0*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "lookbackRepeatedSm89" ->
      """//--------------------- .text._ZN48_GLOBAL__N__137ec495_10_control_cu_07573323_28358lookbackILNS_5ShapeE4EEEvPKjPj --------------------------
        |	.section	.text._ZN48_GLOBAL__N__137ec495_10_control_cu_07573323_28358lookbackILNS_5ShapeE4EEEvPKjPj,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         _ZN48_GLOBAL__N__137ec495_10_control_cu_07573323_28358lookbackILNS_5ShapeE4EEEvPKjPj
        |        .type           _ZN48_GLOBAL__N__137ec495_10_control_cu_07573323_28358lookbackILNS_5ShapeE4EEEvPKjPj,@function
        |        .size           _ZN48_GLOBAL__N__137ec495_10_control_cu_07573323_28358lookbackILNS_5ShapeE4EEEvPKjPj,(.L_x_97 - _ZN48_GLOBAL__N__137ec495_10_control_cu_07573323_28358lookbackILNS_5ShapeE4EEEvPKjPj)
        |        .other          _ZN48_GLOBAL__N__137ec495_10_control_cu_07573323_28358lookbackILNS_5ShapeE4EEEvPKjPj,@"STO_CUDA_ENTRY STV_DEFAULT"
        |_ZN48_GLOBAL__N__137ec495_10_control_cu_07573323_28358lookbackILNS_5ShapeE4EEEvPKjPj:
        |.text._ZN48_GLOBAL__N__137ec495_10_control_cu_07573323_28358lookbackILNS_5ShapeE4EEEvPKjPj:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   BSSY B0, `(.L_x_83) ;
        |        /*0030*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x8 ;
        |        /*0040*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0050*/                   IMAD.MOV.U32 R4, RZ, RZ, -0x1 ;
        |        /*0060*/                   IMAD.MOV.U32 R3, RZ, RZ, RZ ;
        |.L_x_88:
        |        /*0070*/                   ISETP.NE.AND P0, PT, R0, RZ, PT ;
        |        /*0080*/                   @!P0 BRA `(.L_x_84) ;
        |        /*0090*/                   IADD3 R7, R0, -0x1, RZ ;
        |        /*00a0*/                   BSSY B1, `(.L_x_85) ;
        |        /*00b0*/                   IMAD.WIDE.U32 R8, R7, 0x20, R2 ;
        |        /*00c0*/                   LEA R10, P0, R8, c[0x0][0x160], 0x2 ;
        |        /*00d0*/                   LEA.HI.X R11, R8, c[0x0][0x164], R9, 0x2, P0 ;
        |.L_x_86:
        |        /*00e0*/                   YIELD ;
        |        /*00f0*/                   MEMBAR.SC.CTA ;
        |        /*0100*/                   LDG.E.STRONG.SYS R0, [R10.64] ;
        |        /*0110*/                   ISETP.NE.AND P0, PT, R0, RZ, PT ;
        |        /*0120*/                   @!P0 BRA `(.L_x_86) ;
        |        /*0130*/                   BSYNC B1 ;
        |.L_x_85:
        |        /*0140*/                   REDUX.OR UR6, R4 ;
        |        /*0150*/                   R2UR P0, URZ, R4 ;
        |        /*0160*/                   LOP3.LUT P1, RZ, R0, 0x40000000, RZ, 0xc0, !PT ;
        |        /*0170*/                   @!P0 BRA.DIV UR6, `(.L_x_87) ;
        |        /*0180*/                   VOTE.ANY R4, PT, !P1 ;
        |.L_x_95:
        |        /*0190*/                   IMAD.MOV.U32 R0, RZ, RZ, R7 ;
        |        /*01a0*/                   @!P1 BRA `(.L_x_88) ;
        |.L_x_84:
        |        /*01b0*/                   BSYNC B0 ;
        |.L_x_83:
        |        /*01c0*/                   BSSY B0, `(.L_x_89) ;
        |        /*01d0*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x8 ;
        |        /*01e0*/                   IMAD.MOV.U32 R7, RZ, RZ, RZ ;
        |        /*01f0*/                   IMAD.MOV.U32 R9, RZ, RZ, -0x1 ;
        |.L_x_94:
        |        /*0200*/                   ISETP.NE.AND P0, PT, R0, RZ, PT ;
        |        /*0210*/                   @!P0 BRA `(.L_x_90) ;
        |        /*0220*/                   IADD3 R11, R0, -0x1, RZ ;
        |        /*0230*/                   BSSY B1, `(.L_x_91) ;
        |        /*0240*/                   IMAD.WIDE.U32 R4, R11, 0x20, R2 ;
        |        /*0250*/                   LEA R12, P0, R4, c[0x0][0x160], 0x2 ;
        |        /*0260*/                   LEA.HI.X R13, R4, c[0x0][0x164], R5, 0x2, P0 ;
        |.L_x_92:
        |        /*0270*/                   YIELD ;
        |        /*0280*/                   MEMBAR.SC.CTA ;
        |        /*0290*/                   LDG.E.STRONG.SYS R0, [R12.64] ;
        |        /*02a0*/                   ISETP.NE.AND P0, PT, R0, RZ, PT ;
        |        /*02b0*/                   @!P0 BRA `(.L_x_92) ;
        |        /*02c0*/                   BSYNC B1 ;
        |.L_x_91:
        |        /*02d0*/                   REDUX.OR UR6, R9 ;
        |        /*02e0*/                   R2UR P0, URZ, R9 ;
        |        /*02f0*/                   LOP3.LUT P1, RZ, R0.reuse, 0x40000000, RZ, 0xc0, !PT ;
        |        /*0300*/                   LOP3.LUT R0, R0, 0x3fffffff, RZ, 0xc0, !PT ;
        |        /*0310*/                   IMAD.IADD R7, R7, 0x1, R0 ;
        |        /*0320*/                   @!P0 BRA.DIV UR6, `(.L_x_93) ;
        |        /*0330*/                   VOTE.ANY R9, PT, !P1 ;
        |.L_x_96:
        |        /*0340*/                   IMAD.MOV.U32 R0, RZ, RZ, R11 ;
        |        /*0350*/                   @!P1 BRA `(.L_x_94) ;
        |.L_x_90:
        |        /*0360*/                   BSYNC B0 ;
        |.L_x_89:
        |        /*0370*/                   LEA R4, P0, R2, c[0x0][0x168], 0x2 ;
        |        /*0380*/                   LEA.HI.X R5, R2, c[0x0][0x16c], RZ, 0x2, P0 ;
        |        /*0390*/                   STG.E [R4.64], R7 ;
        |        /*03a0*/                   STG.E [R4.64+0x80], R9 ;
        |        /*03b0*/                   EXIT ;
        |.L_x_87:
        |        /*03c0*/                   IMAD.MOV.U32 R9, RZ, RZ, R4 ;
        |        /*03d0*/                   SEL R0, RZ, 0x1, P1 ;
        |        /*03e0*/                   MOV R4, 0x400 ;
        |        /*03f0*/                   CALL.REL.NOINC `($__internal_8_$__cuda_sm70_votesync_ballot) ;
        |        /*0400*/                   IMAD.MOV.U32 R4, RZ, RZ, R9 ;
        |        /*0410*/                   BRA `(.L_x_95) ;
        |.L_x_93:
        |        /*0420*/                   SEL R0, RZ, 0x1, P1 ;
        |        /*0430*/                   MOV R4, 0x450 ;
        |        /*0440*/                   CALL.REL.NOINC `($__internal_8_$__cuda_sm70_votesync_ballot) ;
        |        /*0450*/                   BRA `(.L_x_96) ;
        |$__internal_8_$__cuda_sm70_votesync_ballot:
        |        /*0460*/                   ISETP.NE.U32.AND P0, PT, R0, 0x1, PT ;
        |        /*0470*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x0 ;
        |        /*0480*/                   WARPSYNC R9 ;
        |        /*0490*/                   VOTE.ANY R0, PT, !P0 ;
        |        /*04a0*/                   LOP3.LUT R9, R0, R9, RZ, 0xc0, !PT ;
        |        /*04b0*/                   RET.REL.NODEC R4 `(_ZN48_GLOBAL__N__137ec495_10_control_cu_07573323_28358lookbackILNS_5ShapeE4EEEvPKjPj) ;
        |.L_x_97:
        |""".stripMargin,
    "integersSm86" ->
      """//--------------------- .text._ZN40_GLOBAL__N__abd92d82_11_integers_cu_main8integersEPjPyS0_S1_S0_ --------------------------
        |	.section	.text._ZN40_GLOBAL__N__abd92d82_11_integers_cu_main8integersEPjPyS0_S1_S0_,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=30"
        |	.align	128
        |.text._ZN40_GLOBAL__N__abd92d82_11_integers_cu_main8integersEPjPyS0_S1_S0_:
        |        .type           _ZN40_GLOBAL__N__abd92d82_11_integers_cu_main8integersEPjPyS0_S1_S0_,@function
        |        .size           _ZN40_GLOBAL__N__abd92d82_11_integers_cu_main8integersEPjPyS0_S1_S0_,(.L_x_27 - _ZN40_GLOBAL__N__abd92d82_11_integers_cu_main8integersEPjPyS0_S1_S0_)
        |        .other          _ZN40_GLOBAL__N__abd92d82_11_integers_cu_main8integersEPjPyS0_S1_S0_,@"STO_CUDA_ENTRY STV_DEFAULT"
        |_ZN40_GLOBAL__N__abd92d82_11_integers_cu_main8integersEPjPyS0_S1_S0_:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R9, SR_TID.X ;
        |        /*0020*/                   ULDC.64 UR6, c[0x0][0x118] ;
        |        /*0030*/                   ISETP.GT.U32.AND P2, PT, R9.reuse, 0x9, PT ;
        |        /*0040*/                   ISETP.GT.U32.AND P1, PT, R9, 0xd, PT ;
        |        /*0050*/              @!P2 IMAD.MOV.U32 R4, RZ, RZ, 0x8 ;
        |        /*0060*/              @!P1 IMAD.MOV.U32 R6, RZ, RZ, 0x4 ;
        |        /*0070*/              @!P2 IMAD.WIDE.U32 R4, R9, R4, c[0x0][0x168] ;
        |        /*0080*/              @!P1 IMAD.WIDE.U32 R6, R9, R6, c[0x0][0x160] ;
        |        /*0090*/              @!P2 LDG.E.64 R4, [R4.64+0x50] ;
        |        /*00a0*/              @!P1 LDG.E R2, [R6.64+0x38] ;
        |        /*00b0*/                   S2R R0, SR_CTAID.X ;
        |        /*00c0*/                   IMAD R3, R0, c[0x0][0x0], R9 ;
        |        /*00d0*/                   IMAD.WIDE.U32 R10, R3, 0x8421085, RZ ;
        |        /*00e0*/                   IMAD.IADD R13, R3, 0x1, -R11 ;
        |        /*00f0*/                   LEA.HI R13, R13, R11, RZ, 0x1f ;
        |        /*0100*/                   IMAD R11, R3, 0x25, RZ ;
        |        /*0110*/                   IMAD.WIDE.U32 R6, R11, 0x446f8657, RZ ;
        |        /*0120*/                   IMAD.IADD R6, R11, 0x1, -R7 ;
        |        /*0130*/                   S2R R16, SR_LANEID ;
        |        /*0140*/                   LEA.HI R6, R6, R7, RZ, 0x1f ;
        |        /*0150*/                   SHF.R.U32.HI R13, RZ, 0x4, R13 ;
        |        /*0160*/                   SHF.R.U32.HI R6, RZ, 0x6, R6 ;
        |        /*0170*/                   IMAD.MOV.U32 R17, RZ, RZ, -0x2 ;
        |        /*0180*/                   IADD3 R10, R3, 0x1, RZ ;
        |        /*0190*/                   IMAD R12, R13, -0x1f, R3 ;
        |        /*01a0*/                   IMAD R6, R6, -0x65, R11 ;
        |        /*01b0*/                   IMAD.MOV.U32 R8, RZ, RZ, 0x1 ;
        |        /*01c0*/                   VOTEU.ANY UR4, UPT, PT ;
        |        /*01d0*/                   IADD3 R11, R6, -0x32, RZ ;
        |        /*01e0*/                   REDUX.SUM UR8, R10 ;
        |        /*01f0*/                   SHF.L.W.U32.HI R12, R17, R12, 0xfffffffe ;
        |        /*0200*/                   UFLO.U32 UR5, UR4 ;
        |        /*0210*/                   SHF.L.W.U32 R14, R8, R3, RZ ;
        |        /*0220*/                   IMAD R20, R3, -0x61c8864f, RZ ;
        |        /*0230*/                   REDUX.MIN.S32 UR9, R11 ;
        |        /*0240*/                   ISETP.EQ.U32.AND P0, PT, R16, UR5, PT ;
        |        /*0250*/                   REDUX.MAX.S32 UR10, R11 ;
        |        /*0260*/                   REDUX UR11, R12 ;
        |        /*0270*/                   REDUX.OR UR12, R14 ;
        |        /*0280*/                   REDUX.XOR UR13, R3 ;
        |        /*0290*/                   REDUX.MIN UR14, R20 ;
        |        /*02a0*/                   REDUX.MAX UR5, R20 ;
        |        /*02b0*/                   UPOPC UR4, UR4 ;
        |        /*02c0*/                   IMAD.MOV.U32 R6, RZ, RZ, c[0x0][0x160] ;
        |        /*02d0*/                   IMAD.MOV.U32 R7, RZ, RZ, c[0x0][0x164] ;
        |        /*02e0*/                   IMAD.U32 R13, RZ, RZ, UR8 ;
        |        /*02f0*/                   IMAD R27, RZ, RZ, -UR4 ;
        |        /*0300*/                   IMAD.U32 R15, RZ, RZ, UR9 ;
        |        /*0310*/                   IMAD.U32 R19, RZ, RZ, UR10 ;
        |        /*0320*/                   IMAD.U32 R21, RZ, RZ, UR11 ;
        |        /*0330*/                   IMAD.U32 R23, RZ, RZ, UR12 ;
        |        /*0340*/                   IMAD.U32 R25, RZ, RZ, UR13 ;
        |        /*0350*/                   IMAD.MOV.U32 R18, RZ, RZ, 0x9 ;
        |        /*0360*/              @!P2 STS.64 [R9.X8+0x38], R4 ;
        |        /*0370*/              @!P1 STS [R9.X4], R2 ;
        |        /*0380*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0390*/                   IMAD.U32 R5, RZ, RZ, UR14 ;
        |        /*03a0*/               @P0 RED.E.ADD.STRONG.GPU [R6.64], R13 ;
        |        /*03b0*/               @P0 RED.E.ADD.STRONG.GPU [R6.64+0x4], R27 ;
        |        /*03c0*/               @P0 RED.E.MIN.S32.STRONG.GPU [R6.64+0x8], R15 ;
        |        /*03d0*/               @P0 RED.E.MAX.S32.STRONG.GPU [R6.64+0xc], R19 ;
        |        /*03e0*/               @P0 RED.E.AND.STRONG.GPU [R6.64+0x10], R21 ;
        |        /*03f0*/                   IMAD.U32 R27, RZ, RZ, UR5 ;
        |        /*0400*/               @P0 RED.E.OR.STRONG.GPU [R6.64+0x14], R23 ;
        |        /*0410*/               @P0 RED.E.XOR.STRONG.GPU [R6.64+0x18], R25 ;
        |        /*0420*/                   ATOMG.E.EXCH.STRONG.GPU PT, RZ, [R6.64+0x1c], R3 ;
        |        /*0430*/                   RED.E.INC.STRONG.GPU [R6.64+0x20], R18 ;
        |        /*0440*/                   RED.E.DEC.STRONG.GPU [R6.64+0x24], R18 ;
        |        /*0450*/               @P0 RED.E.MIN.STRONG.GPU [R6.64+0x28], R5 ;
        |        /*0460*/               @P0 RED.E.MAX.STRONG.GPU [R6.64+0x2c], R27 ;
        |        /*0470*/                   LDG.E R4, [R6.64+0x30] ;
        |        /*0480*/                   IMAD.MOV.U32 R13, RZ, RZ, 0x10 ;
        |        /*0490*/                   BSSY B0, `(.L_x_0) ;
        |        /*04a0*/                   IMAD.WIDE.U32 R12, R3, R13, c[0x0][0x180] ;
        |.L_x_1:
        |        /*04b0*/                   IADD3 R5, R4, 0x2, RZ ;
        |        /*04c0*/                   YIELD ;
        |        /*04d0*/                   ATOMG.E.CAS.STRONG.GPU PT, R5, [R6+0x30], R4, R5 ;
        |        /*04e0*/                   ISETP.NE.AND P0, PT, R5, R4, PT ;
        |        /*04f0*/                   IMAD.MOV.U32 R4, RZ, RZ, R5 ;
        |        /*0500*/               @P0 BRA `(.L_x_1) ;
        |        /*0510*/                   BSYNC B0 ;
        |.L_x_0:
        |        /*0520*/                   VOTEU.ANY UR4, UPT, PT ;
        |        /*0530*/                   FLO.U32 R25, UR4 ;
        |        /*0540*/                   POPC R23, UR4 ;
        |        /*0550*/                   ISETP.EQ.U32.AND P0, PT, R25, R16, PT ;
        |        /*0560*/               @P0 ATOMG.E.ADD.STRONG.GPU PT, R6, [R6.64+0x34], R23 ;
        |        /*0570*/                   IMAD.SHL.U32 R4, R3, 0x40000000, RZ ;
        |        /*0580*/                   SHF.R.U32.HI R5, RZ, 0x2, R3 ;
        |        /*0590*/                   IMAD.MOV.U32 R14, RZ, RZ, c[0x0][0x168] ;
        |        /*05a0*/                   S2R R19, SR_LTMASK ;
        |        /*05b0*/                   IMAD.MOV.U32 R15, RZ, RZ, c[0x0][0x16c] ;
        |        /*05c0*/                   LOP3.LUT R24, R19, UR4, RZ, 0xc0, !PT ;
        |        /*05d0*/                   POPC R24, R24 ;
        |        /*05e0*/                   SHFL.IDX PT, R21, R6, R25, 0x1f ;
        |        /*05f0*/                   IMAD.IADD R21, R21, 0x1, R24 ;
        |        /*0600*/                   STG.E [R12.64], R21 ;
        |        /*0610*/                   RED.E.ADD.64.STRONG.GPU [R14.64], R4 ;
        |        /*0620*/                   LDG.E.64 R22, [R14.64+0x38] ;
        |        /*0630*/                   BSSY B0, `(.L_x_2) ;
        |.L_x_3:
        |        /*0640*/                   IADD3 R6, P0, R22, 0x1, RZ ;
        |        /*0650*/                   IMAD.MOV.U32 R4, RZ, RZ, R22 ;
        |        /*0660*/                   YIELD ;
        |        /*0670*/                   IMAD.MOV.U32 R5, RZ, RZ, R23 ;
        |        /*0680*/                   IADD3.X R7, R23, 0x1, RZ, P0, !PT ;
        |        /*0690*/                   ATOMG.E.CAS.64.STRONG.GPU PT, R4, [R14+0x38], R4, R6 ;
        |        /*06a0*/                   ISETP.NE.U32.AND P0, PT, R4, R22, PT ;
        |        /*06b0*/                   IMAD.MOV.U32 R22, RZ, RZ, R4 ;
        |        /*06c0*/                   ISETP.NE.AND.EX P0, PT, R5, R23, PT, P0 ;
        |        /*06d0*/                   IMAD.MOV.U32 R23, RZ, RZ, R5 ;
        |        /*06e0*/               @P0 BRA `(.L_x_3) ;
        |        /*06f0*/                   BSYNC B0 ;
        |.L_x_2:
        |        /*0700*/                   IMAD.MOV.U32 R2, RZ, RZ, 0x1 ;
        |        /*0710*/                   ATOMG.E.EXCH.64.STRONG.GPU PT, R4, [R14.64+0x30], R2 ;
        |        /*0720*/                   IMAD.WIDE.U32 R6, R3.reuse, 0x4325c53f, RZ ;
        |        /*0730*/                   LOP3.LUT R23, R3, 0x3f, RZ, 0xc0, !PT ;
        |        /*0740*/                   IMAD.MOV.U32 R25, RZ, RZ, -0x1 ;
        |        /*0750*/                   SHF.R.U32.HI R7, RZ, 0x4, R7 ;
        |        /*0760*/                   IMAD.WIDE.U32 R20, R20, 0x20000000, RZ ;
        |        /*0770*/                   LOP3.LUT R20, R20, R3, RZ, 0x3c, !PT ;
        |        /*0780*/                   VOTEU.ANY UR4, UPT, PT ;
        |        /*0790*/                   UFLO.U32 UR5, UR4 ;
        |        /*07a0*/                   REDUX.XOR UR13, R9 ;
        |        /*07b0*/                   UPOPC UR4, UR4 ;
        |        /*07c0*/                   BSSY B0, `(.L_x_4) ;
        |        /*07d0*/                   IMAD R4, R7, -0x3d, R3 ;
        |        /*07e0*/                   STG.E [R12.64+0x4], R5 ;
        |        /*07f0*/                   IADD3 R6, -R4, 0x40, RZ ;
        |        /*0800*/                   RED.E.MIN.64.STRONG.GPU [R14.64+0x8], R20 ;
        |        /*0810*/                   SHF.L.U32 R7, R17, R4, RZ ;
        |        /*0820*/                   SHF.R.U32.HI R10, RZ, R6.reuse, R25 ;
        |        /*0830*/                   RED.E.MAX.64.STRONG.GPU [R14.64+0x10], R20 ;
        |        /*0840*/                   SHF.R.U64 R22, R17.reuse, R6, 0xffffffff ;
        |        /*0850*/                   SHF.L.U64.HI R25, R17, R4, 0xffffffff ;
        |        /*0860*/                   IMAD.WIDE.U32 R4, R9.reuse, 0x8421085, RZ ;
        |        /*0870*/                   IADD3 R22, P0, R22, R7, RZ ;
        |        /*0880*/                   SHF.L.U64.HI R7, R8.reuse, R23.reuse, RZ ;
        |        /*0890*/                   IMAD.IADD R4, R9.reuse, 0x1, -R5 ;
        |        /*08a0*/                   SHF.L.U32 R6, R8, R23, RZ ;
        |        /*08b0*/                   IMAD R13, R9, 0x25, RZ ;
        |        /*08c0*/                   IMAD.X R23, R10, 0x1, R25, P0 ;
        |        /*08d0*/                   LEA.HI R2, R4, R5, RZ, 0x1f ;
        |        /*08e0*/                   IMAD.WIDE.U32 R4, R13, 0x446f8657, RZ ;
        |        /*08f0*/                   SHF.L.W.U32 R8, R8, R9, RZ ;
        |        /*0900*/                   RED.E.AND.64.STRONG.GPU [R14.64+0x18], R22 ;
        |        /*0910*/                   SHF.R.U32.HI R2, RZ, 0x4, R2 ;
        |        /*0920*/                   MOV R10, RZ ;
        |        /*0930*/                   RED.E.OR.64.STRONG.GPU [R14.64+0x20], R6 ;
        |        /*0940*/                   IMAD R2, R2, -0x1f, R9 ;
        |        /*0950*/                   IADD3 R4, R9, 0x1, RZ ;
        |        /*0960*/                   REDUX.OR UR12, R8 ;
        |        /*0970*/                   ISETP.EQ.U32.AND P0, PT, R16, UR5, PT ;
        |        /*0980*/                   RED.E.XOR.64.STRONG.GPU [R14.64+0x28], R20 ;
        |        /*0990*/                   IMAD.IADD R7, R13, 0x1, -R5 ;
        |        /*09a0*/                   SHF.L.W.U32.HI R17, R17, R2, 0xfffffffe ;
        |        /*09b0*/                   REDUX.SUM UR8, R4 ;
        |        /*09c0*/                   RED.E.MIN.S64.STRONG.GPU [R14.64+0x40], R10 ;
        |        /*09d0*/                   LEA.HI R7, R7, R5, RZ, 0x1f ;
        |        /*09e0*/                   RED.E.MAX.S64.STRONG.GPU [R14.64+0x48], R10 ;
        |        /*09f0*/                   SHF.R.U32.HI R2, RZ, 0x6, R7 ;
        |        /*0a00*/                   IMAD R2, R2, -0x65, R13 ;
        |        /*0a10*/                   IADD3 R5, R2, -0x32, RZ ;
        |        /*0a20*/                   REDUX UR11, R17 ;
        |        /*0a30*/                   IMAD R2, R9, -0x61c8864f, RZ ;
        |        /*0a40*/                   REDUX.MIN.S32 UR9, R5 ;
        |        /*0a50*/                   REDUX.MAX.S32 UR10, R5 ;
        |        /*0a60*/                   REDUX.MIN UR14, R2 ;
        |        /*0a70*/                   REDUX.MAX UR5, R2 ;
        |        /*0a80*/                   IMAD.U32 R4, RZ, RZ, UR8 ;
        |        /*0a90*/                   IMAD R10, RZ, RZ, -UR4 ;
        |        /*0aa0*/                   IMAD.U32 R6, RZ, RZ, UR9 ;
        |        /*0ab0*/               @P0 ATOMS.ADD RZ, [RZ], R4 ;
        |        /*0ac0*/                   IMAD.U32 R8, RZ, RZ, UR11 ;
        |        /*0ad0*/                   IMAD.U32 R7, RZ, RZ, UR10 ;
        |        /*0ae0*/               @P0 ATOMS.ADD RZ, [0x4], R10 ;
        |        /*0af0*/                   IMAD.U32 R11, RZ, RZ, UR12 ;
        |        /*0b00*/                   IMAD.U32 R12, RZ, RZ, UR13 ;
        |        /*0b10*/               @P0 ATOMS.MIN.S32 RZ, [0x8], R6 ;
        |        /*0b20*/               @P0 ATOMS.MAX.S32 RZ, [0xc], R7 ;
        |        /*0b30*/                   IMAD.U32 R4, RZ, RZ, UR14 ;
        |        /*0b40*/               @P0 ATOMS.AND RZ, [0x10], R8 ;
        |        /*0b50*/                   IMAD.U32 R13, RZ, RZ, UR5 ;
        |        /*0b60*/               @P0 ATOMS.OR RZ, [0x14], R11 ;
        |        /*0b70*/               @P0 ATOMS.XOR RZ, [0x18], R12 ;
        |        /*0b80*/                   ATOMS.EXCH RZ, [0x1c], R9 ;
        |        /*0b90*/                   ATOMS.INC RZ, [0x20], R18 ;
        |        /*0ba0*/                   ATOMS.DEC RZ, [0x24], R18 ;
        |        /*0bb0*/               @P0 ATOMS.MIN RZ, [0x28], R4 ;
        |        /*0bc0*/               @P0 ATOMS.MAX RZ, [0x2c], R13 ;
        |        /*0bd0*/                   LDS R10, [0x30] ;
        |        /*0be0*/                   IMAD.SHL.U32 R6, R3, 0x4, RZ ;
        |        /*0bf0*/                   IMAD.MOV.U32 R7, RZ, RZ, 0x4 ;
        |        /*0c00*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x180] ;
        |.L_x_5:
        |        /*0c10*/                   IADD3 R11, R10, 0x2, RZ ;
        |        /*0c20*/                   YIELD ;
        |        /*0c30*/                   ATOMS.CAS R11, [0x30], R10, R11 ;
        |        /*0c40*/                   ISETP.NE.AND P0, PT, R11, R10, PT ;
        |        /*0c50*/                   IMAD.MOV.U32 R10, RZ, RZ, R11 ;
        |        /*0c60*/               @P0 BRA `(.L_x_5) ;
        |        /*0c70*/                   BSYNC B0 ;
        |.L_x_4:
        |        /*0c80*/                   VOTEU.ANY UR4, UPT, PT ;
        |        /*0c90*/                   BSSY B0, `(.L_x_6) ;
        |        /*0ca0*/                   FLO.U32 R13, UR4 ;
        |        /*0cb0*/                   LOP3.LUT R19, R19, UR4, RZ, 0xc0, !PT ;
        |        /*0cc0*/                   IMAD.WIDE.U32 R10, R9, 0x40000000, RZ ;
        |        /*0cd0*/                   POPC R4, UR4 ;
        |        /*0ce0*/                   ISETP.EQ.U32.AND P0, PT, R13, R16, PT ;
        |        /*0cf0*/                   POPC R8, R19 ;
        |        /*0d00*/               @P0 ATOMS.ADD R4, [0x34], R4 ;
        |        /*0d10*/                   SHFL.IDX PT, R3, R4, R13, 0x1f ;
        |        /*0d20*/                   IMAD.IADD R3, R3, 0x1, R8 ;
        |        /*0d30*/                   STG.E [R6.64+0x8], R3 ;
        |.L_x_7:
        |        /*0d40*/                   LDS.64 R12, [0x38] ;
        |        /*0d50*/                   IADD3 R14, P0, R10, R12, RZ ;
        |        /*0d60*/                   IMAD.X R15, R11, 0x1, R13, P0 ;
        |        /*0d70*/                   ATOMS.CAST.SPIN.64 R14, [0x38], R12, R14 ;
        |        /*0d80*/                   ISETP.EQ.U32.AND P0, PT, R14, 0x1, PT ;
        |        /*0d90*/                   ISETP.EQ.U32.AND.EX P0, PT, R15, RZ, PT, P0 ;
        |        /*0da0*/              @!P0 BRA `(.L_x_7) ;
        |        /*0db0*/                   BSYNC B0 ;
        |.L_x_6:
        |        /*0dc0*/                   LDS.64 R10, [0x70] ;
        |        /*0dd0*/                   BSSY B0, `(.L_x_8) ;
        |.L_x_9:
        |        /*0de0*/                   IADD3 R14, P0, R10, 0x1, RZ ;
        |        /*0df0*/                   IMAD.MOV.U32 R13, RZ, RZ, R11 ;
        |        /*0e00*/                   MOV R12, R10 ;
        |        /*0e10*/                   YIELD ;
        |        /*0e20*/                   IADD3.X R15, R11, 0x1, RZ, P0, !PT ;
        |        /*0e30*/                   ATOMS.CAS.64 R12, [0x70], R12, R14 ;
        |        /*0e40*/                   ISETP.NE.U32.AND P0, PT, R12, R10, PT ;
        |        /*0e50*/                   IMAD.MOV.U32 R10, RZ, RZ, R12 ;
        |        /*0e60*/                   ISETP.NE.AND.EX P0, PT, R13, R11, PT, P0 ;
        |        /*0e70*/                   IMAD.MOV.U32 R11, RZ, RZ, R13 ;
        |        /*0e80*/               @P0 BRA `(.L_x_9) ;
        |        /*0e90*/                   BSYNC B0 ;
        |.L_x_8:
        |        /*0ea0*/                   IMAD.MOV.U32 R8, RZ, RZ, 0x1 ;
        |        /*0eb0*/                   BSSY B0, `(.L_x_10) ;
        |        /*0ec0*/                   IMAD.WIDE.U32 R2, R2, 0x20000000, RZ ;
        |        /*0ed0*/                   ATOMS.EXCH.64 R10, [0x68], R8 ;
        |        /*0ee0*/                   IMAD.MOV.U32 R15, RZ, RZ, R3 ;
        |        /*0ef0*/                   LOP3.LUT R14, R2, R9, RZ, 0x3c, !PT ;
        |        /*0f00*/                   STG.E [R6.64+0xc], R11 ;
        |.L_x_12:
        |        /*0f10*/                   LDS.64 R12, [0x40] ;
        |        /*0f20*/                   ISETP.GE.U32.AND P0, PT, R14, R12, PT ;
        |        /*0f30*/                   ISETP.GE.U32.AND.EX P0, PT, R3, R13, PT, P0 ;
        |        /*0f40*/               @P0 BRA `(.L_x_11) ;
        |        /*0f50*/                   ATOMS.CAST.SPIN.64 R12, [0x40], R12, R14 ;
        |        /*0f60*/                   ISETP.EQ.U32.AND P0, PT, R12, 0x1, PT ;
        |        /*0f70*/                   ISETP.EQ.U32.AND.EX P0, PT, R13, RZ, PT, P0 ;
        |        /*0f80*/              @!P0 BRA `(.L_x_12) ;
        |.L_x_11:
        |        /*0f90*/                   BSYNC B0 ;
        |.L_x_10:
        |        /*0fa0*/                   BSSY B0, `(.L_x_13) ;
        |        /*0fb0*/                   IMAD.MOV.U32 R15, RZ, RZ, R3 ;
        |.L_x_15:
        |        /*0fc0*/                   LDS.64 R12, [0x48] ;
        |        /*0fd0*/                   ISETP.LE.U32.AND P0, PT, R14, R12, PT ;
        |        /*0fe0*/                   ISETP.LE.U32.AND.EX P0, PT, R3, R13, PT, P0 ;
        |        /*0ff0*/               @P0 BRA `(.L_x_14) ;
        |        /*1000*/                   ATOMS.CAST.SPIN.64 R12, [0x48], R12, R14 ;
        |        /*1010*/                   ISETP.EQ.U32.AND P0, PT, R12, 0x1, PT ;
        |        /*1020*/                   ISETP.EQ.U32.AND.EX P0, PT, R13, RZ, PT, P0 ;
        |        /*1030*/              @!P0 BRA `(.L_x_15) ;
        |.L_x_14:
        |        /*1040*/                   BSYNC B0 ;
        |.L_x_13:
        |        /*1050*/                   IMAD.WIDE.U32 R6, R9, 0x4325c53f, RZ ;
        |        /*1060*/                   BSSY B0, `(.L_x_16) ;
        |        /*1070*/                   IMAD.MOV.U32 R11, RZ, RZ, -0x2 ;
        |        /*1080*/                   SHF.R.U32.HI R4, RZ, 0x4, R7 ;
        |        /*1090*/                   IMAD.MOV.U32 R13, RZ, RZ, -0x1 ;
        |        /*10a0*/                   IMAD R4, R4, -0x3d, R9 ;
        |        /*10b0*/                   IADD3 R6, -R4, 0x40, RZ ;
        |        /*10c0*/                   SHF.L.U32 R8, R11.reuse, R4, RZ ;
        |        /*10d0*/                   SHF.R.U64 R7, R11.reuse, R6, 0xffffffff ;
        |        /*10e0*/                   SHF.L.U64.HI R4, R11, R4, 0xffffffff ;
        |        /*10f0*/                   IADD3 R7, P0, R7, R8, RZ ;
        |        /*1100*/                   SHF.R.U32.HI R11, RZ, R6, R13 ;
        |        /*1110*/                   IMAD.X R11, R11, 0x1, R4, P0 ;
        |.L_x_17:
        |        /*1120*/                   LDS.64 R12, [0x50] ;
        |        /*1130*/                   LOP3.LUT R14, R12, R7, RZ, 0xc0, !PT ;
        |        /*1140*/                   LOP3.LUT R15, R13, R11, RZ, 0xc0, !PT ;
        |        /*1150*/                   ATOMS.CAST.SPIN.64 R14, [0x50], R12, R14 ;
        |        /*1160*/                   ISETP.EQ.U32.AND P0, PT, R14, 0x1, PT ;
        |        /*1170*/                   ISETP.EQ.U32.AND.EX P0, PT, R15, RZ, PT, P0 ;
        |        /*1180*/              @!P0 BRA `(.L_x_17) ;
        |        /*1190*/                   BSYNC B0 ;
        |.L_x_16:
        |        /*11a0*/                   IMAD.MOV.U32 R7, RZ, RZ, 0x1 ;
        |        /*11b0*/                   LOP3.LUT R4, R9, 0x3f, RZ, 0xc0, !PT ;
        |        /*11c0*/                   BSSY B0, `(.L_x_18) ;
        |        /*11d0*/                   SHF.L.U64.HI R11, R7.reuse, R4.reuse, RZ ;
        |        /*11e0*/                   SHF.L.U32 R7, R7, R4, RZ ;
        |.L_x_19:
        |        /*11f0*/                   LDS.64 R12, [0x58] ;
        |        /*1200*/                   LOP3.LUT R14, R12, R7, RZ, 0xfc, !PT ;
        |        /*1210*/                   LOP3.LUT R15, R13, R11, RZ, 0xfc, !PT ;
        |        /*1220*/                   ATOMS.CAST.SPIN.64 R14, [0x58], R12, R14 ;
        |        /*1230*/                   ISETP.EQ.U32.AND P0, PT, R14, 0x1, PT ;
        |        /*1240*/                   ISETP.EQ.U32.AND.EX P0, PT, R15, RZ, PT, P0 ;
        |        /*1250*/              @!P0 BRA `(.L_x_19) ;
        |        /*1260*/                   BSYNC B0 ;
        |.L_x_18:
        |        /*1270*/                   BSSY B0, `(.L_x_20) ;
        |.L_x_21:
        |        /*1280*/                   LDS.64 R12, [0x60] ;
        |        /*1290*/                   LOP3.LUT R14, R12, R2, R9, 0x96, !PT ;
        |        /*12a0*/                   LOP3.LUT R15, R13, R3, RZ, 0x3c, !PT ;
        |        /*12b0*/                   ATOMS.CAST.SPIN.64 R14, [0x60], R12, R14 ;
        |        /*12c0*/                   ISETP.EQ.U32.AND P0, PT, R14, 0x1, PT ;
        |        /*12d0*/                   ISETP.EQ.U32.AND.EX P0, PT, R15, RZ, PT, P0 ;
        |        /*12e0*/              @!P0 BRA `(.L_x_21) ;
        |        /*12f0*/                   BSYNC B0 ;
        |.L_x_20:
        |        /*1300*/                   BSSY B0, `(.L_x_22) ;
        |        /*1310*/                   IMAD.MOV.U32 R14, RZ, RZ, RZ ;
        |        /*1320*/                   IMAD.MOV.U32 R15, RZ, RZ, R5 ;
        |.L_x_24:
        |        /*1330*/                   LDS.64 R12, [0x78] ;
        |        /*1340*/                   ISETP.GE.U32.AND P0, PT, RZ, R12, PT ;
        |        /*1350*/                   ISETP.GE.AND.EX P0, PT, R5, R13, PT, P0 ;
        |        /*1360*/               @P0 BRA `(.L_x_23) ;
        |        /*1370*/                   ATOMS.CAST.SPIN.64 R12, [0x78], R12, R14 ;
        |        /*1380*/                   ISETP.EQ.U32.AND P0, PT, R12, 0x1, PT ;
        |        /*1390*/                   ISETP.EQ.U32.AND.EX P0, PT, R13, RZ, PT, P0 ;
        |        /*13a0*/              @!P0 BRA `(.L_x_24) ;
        |.L_x_23:
        |        /*13b0*/                   BSYNC B0 ;
        |.L_x_22:
        |        /*13c0*/                   LDS.64 R12, [0x80] ;
        |        /*13d0*/                   ISETP.LE.U32.AND P0, PT, RZ, R12, PT ;
        |        /*13e0*/                   ISETP.LE.AND.EX P0, PT, R5, R13, PT, P0 ;
        |        /*13f0*/               @P0 BRA `(.L_x_25) ;
        |        /*1400*/                   ATOMS.CAST.SPIN.64 R12, [0x80], R12, R14 ;
        |        /*1410*/                   ISETP.EQ.U32.AND P0, PT, R12, 0x1, PT ;
        |        /*1420*/                   ISETP.EQ.U32.AND.EX P0, PT, R13, RZ, PT, P0 ;
        |        /*1430*/              @!P0 BRA `(.L_x_22) ;
        |.L_x_25:
        |        /*1440*/                   WARPSYNC 0xffffffff ;
        |        /*1450*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*1460*/              @!P1 IMAD.MOV.U32 R3, RZ, RZ, 0x4 ;
        |        /*1470*/              @!P1 IMAD R2, R0, 0xe, R9 ;
        |        /*1480*/              @!P1 IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x170] ;
        |        /*1490*/              @!P1 LDS R5, [R9.X4] ;
        |        /*14a0*/              @!P1 STG.E [R2.64], R5 ;
        |        /*14b0*/               @P2 EXIT ;
        |        /*14c0*/                   LDS.64 R2, [R9.X8+0x38] ;
        |        /*14d0*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x8 ;
        |        /*14e0*/                   IMAD R4, R0, 0xa, R9 ;
        |        /*14f0*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x178] ;
        |        /*1500*/                   STG.E.64 [R4.64], R2 ;
        |        /*1510*/                   EXIT ;
        |.L_x_27:
        |""".stripMargin
  )
}
