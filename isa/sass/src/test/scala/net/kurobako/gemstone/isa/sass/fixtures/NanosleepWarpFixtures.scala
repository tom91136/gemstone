package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object NanosleepWarpFixtures {
  val nanosleepWarpSm75ProbeCudaSource: String =
    """extern "C" __global__ void nanosleep_dynamic(unsigned *out, unsigned delay) {
      |    unsigned tid = threadIdx.x;
      |    __nanosleep(delay);
      |    if ((tid & 1u) != 0u) __nanosleep(delay + 1u);
      |    out[tid] = tid + 1u;
      |}
      |extern "C" __global__ void nanosleep_immediates(unsigned *out) {
      |    unsigned tid = threadIdx.x;
      |    __nanosleep(0u);
      |    __nanosleep(1u);
      |    __nanosleep(450u);
      |    __nanosleep(0xffffffffu);
      |    out[tid] = tid + 1u;
      |}
      |extern "C" __global__ void nanosleep_predicated(unsigned *out, unsigned delay) {
      |    unsigned tid = threadIdx.x;
      |    asm volatile("{ .reg .pred p; setp.ne.u32 p, %0, 0; @p nanosleep.u32 %1; }" :: "r"(tid), "r"(delay));
      |    out[tid] = tid + 1u;
      |}
      |""".stripMargin

  val nanosleepWarpSm75ProbeSm75NanosleepDynamic: String =
    """	.target	sm_75
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.nanosleep_dynamic   --------------------------
      |	.section	.text.nanosleep_dynamic,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	128
      |        .global         nanosleep_dynamic
      |        .type           nanosleep_dynamic,@function
      |        .size           nanosleep_dynamic,(.L_x_3 - nanosleep_dynamic)
      |        .other          nanosleep_dynamic,@"STO_CUDA_ENTRY STV_DEFAULT"
      |nanosleep_dynamic:
      |.text.nanosleep_dynamic:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;      /* 0x00000a00ff017624 */
      |                                                                                /* 0x000fe400078e00ff */
      |        /*0010*/                   S2R R2, SR_TID.X ;                           /* 0x0000000000027919 */
      |                                                                                /* 0x000e220000002100 */
      |        /*0020*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x4 ;               /* 0x00000004ff037424 */
      |                                                                                /* 0x000fe200078e00ff */
      |        /*0030*/                   BMOV.32.CLEAR RZ, B0 ;                       /* 0x0000000000ff7355 */
      |                                                                                /* 0x000fe20000100000 */
      |        /*0040*/                   NANOSLEEP.WARP c[0x0][0x168] ;               /* 0x00005a0000007b5d */
      |                                                                                /* 0x000fe20003a00000 */
      |        /*0050*/                   BSSY B0, `(.L_x_0) ;                         /* 0x000000a000007945 */
      |                                                                                /* 0x000fe20003800000 */
      |        /*0060*/                   LOP3.LUT R0, R2.reuse, 0x1, RZ, 0xc0, !PT ;  /* 0x0000000102007812 */
      |                                                                                /* 0x041fe400078ec0ff */
      |        /*0070*/                   IADD3 R5, R2.reuse, 0x1, RZ ;                /* 0x0000000102057810 */
      |                                                                                /* 0x040fe20007ffe0ff */
      |        /*0080*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;    /* 0x0000580002027625 */
      |                                                                                /* 0x000fe200078e0003 */
      |        /*0090*/                   ISETP.NE.U32.AND P0, PT, R0, 0x1, PT ;       /* 0x000000010000780c */
      |                                                                                /* 0x000fd80003f05070 */
      |        /*00a0*/               @P0 BRA `(.L_x_1) ;                              /* 0x0000004000000947 */
      |                                                                                /* 0x000fea0003800000 */
      |        /*00b0*/                   IMAD.MOV.U32 R0, RZ, RZ, c[0x0][0x168] ;     /* 0x00005a00ff007624 */
      |                                                                                /* 0x000fca00078e00ff */
      |        /*00c0*/                   IADD3 R0, R0, 0x1, RZ ;                      /* 0x0000000100007810 */
      |                                                                                /* 0x000fcc0007ffe0ff */
      |        /*00d0*/                   NANOSLEEP.WARP R0 ;                          /* 0x000000000000735d */
      |                                                                                /* 0x000fe20003a00000 */
      |        /*00e0*/                   YIELD ;                                      /* 0x0000000000007946 */
      |                                                                                /* 0x000fe40003800000 */
      |.L_x_1:
      |        /*00f0*/                   BSYNC B0 ;                                   /* 0x0000000000007941 */
      |                                                                                /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0100*/                   STG.E.SYS [R2], R5 ;                         /* 0x0000000502007386 */
      |                                                                                /* 0x000fe2000010e900 */
      |        /*0110*/                   EXIT ;                                       /* 0x000000000000794d */
      |                                                                                /* 0x000fea0003800000 */
      |.L_x_2:
      |        /*0120*/                   BRA `(.L_x_2);                               /* 0xfffffff000007947 */
      |                                                                                /* 0x000fc0000383ffff */
      |        /*0130*/                   NOP;                                         /* 0x0000000000007918 */
      |                                                                                /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                         /* 0x0000000000007918 */
      |                                                                                /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                         /* 0x0000000000007918 */
      |                                                                                /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                         /* 0x0000000000007918 */
      |                                                                                /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                         /* 0x0000000000007918 */
      |                                                                                /* 0x000fc00000000000 */
      |.L_x_3:
      |""".stripMargin

  val nanosleepWarpSm75ProbeSm75NanosleepImmediates: String =
    """	.target	sm_75
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.nanosleep_immediates --------------------------
      |	.section	.text.nanosleep_immediates,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	128
      |        .global         nanosleep_immediates
      |        .type           nanosleep_immediates,@function
      |        .size           nanosleep_immediates,(.L_x_1 - nanosleep_immediates)
      |        .other          nanosleep_immediates,@"STO_CUDA_ENTRY STV_DEFAULT"
      |nanosleep_immediates:
      |.text.nanosleep_immediates:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
      |                                                                              /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R2, SR_TID.X ;                         /* 0x0000000000027919 */
      |                                                                              /* 0x000e220000002100 */
      |        /*0020*/                   MOV R3, 0x4 ;                              /* 0x0000000400037802 */
      |                                                                              /* 0x000fe20000000f00 */
      |        /*0030*/                   NANOSLEEP.WARP RZ ;                        /* 0x000000ff0000735d */
      |                                                                              /* 0x000fe20003a00000 */
      |        /*0040*/                   NANOSLEEP.WARP 0x1 ;                       /* 0x000000010000795d */
      |                                                                              /* 0x000fe20003a00000 */
      |        /*0050*/                   NANOSLEEP.WARP 0x1c2 ;                     /* 0x000001c20000795d */
      |                                                                              /* 0x000fe20003a00000 */
      |        /*0060*/                   NANOSLEEP.WARP 0xffffffff ;                /* 0xffffffff0000795d */
      |                                                                              /* 0x000fe20003a00000 */
      |        /*0070*/                   IADD3 R5, R2.reuse, 0x1, RZ ;              /* 0x0000000102057810 */
      |                                                                              /* 0x041fe20007ffe0ff */
      |        /*0080*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;  /* 0x0000580002027625 */
      |                                                                              /* 0x000fd000078e0003 */
      |        /*0090*/                   STG.E.SYS [R2], R5 ;                       /* 0x0000000502007386 */
      |                                                                              /* 0x000fe2000010e900 */
      |        /*00a0*/                   EXIT ;                                     /* 0x000000000000794d */
      |                                                                              /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00b0*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
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

  val nanosleepWarpSm75ProbeSm75NanosleepPredicated: String =
    """	.target	sm_75
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.nanosleep_predicated --------------------------
      |	.section	.text.nanosleep_predicated,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	128
      |        .global         nanosleep_predicated
      |        .type           nanosleep_predicated,@function
      |        .size           nanosleep_predicated,(.L_x_3 - nanosleep_predicated)
      |        .other          nanosleep_predicated,@"STO_CUDA_ENTRY STV_DEFAULT"
      |nanosleep_predicated:
      |.text.nanosleep_predicated:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;      /* 0x00000a00ff017624 */
      |                                                                                /* 0x000fe400078e00ff */
      |        /*0010*/                   S2R R2, SR_TID.X ;                           /* 0x0000000000027919 */
      |                                                                                /* 0x000e220000002100 */
      |        /*0020*/                   MOV R3, 0x4 ;                                /* 0x0000000400037802 */
      |                                                                                /* 0x000fe20000000f00 */
      |        /*0030*/                   BMOV.32.CLEAR RZ, B0 ;                       /* 0x0000000000ff7355 */
      |                                                                                /* 0x000fe20000100000 */
      |        /*0040*/                   BSSY B0, `(.L_x_0) ;                         /* 0x0000007000007945 */
      |                                                                                /* 0x000fe20003800000 */
      |        /*0050*/                   ISETP.NE.U32.AND P0, PT, R2.reuse, RZ, PT ;  /* 0x000000ff0200720c */
      |                                                                                /* 0x041fe40003f05070 */
      |        /*0060*/                   IADD3 R5, R2.reuse, 0x1, RZ ;                /* 0x0000000102057810 */
      |                                                                                /* 0x040fe20007ffe0ff */
      |        /*0070*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;    /* 0x0000580002027625 */
      |                                                                                /* 0x000fd200078e0003 */
      |        /*0080*/              @!P0 BRA `(.L_x_1) ;                              /* 0x0000002000008947 */
      |                                                                                /* 0x000fea0003800000 */
      |        /*0090*/                   NANOSLEEP.WARP c[0x0][0x168] ;               /* 0x00005a0000007b5d */
      |                                                                                /* 0x000fe20003a00000 */
      |        /*00a0*/                   YIELD ;                                      /* 0x0000000000007946 */
      |                                                                                /* 0x000fe40003800000 */
      |.L_x_1:
      |        /*00b0*/                   BSYNC B0 ;                                   /* 0x0000000000007941 */
      |                                                                                /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00c0*/                   STG.E.SYS [R2], R5 ;                         /* 0x0000000502007386 */
      |                                                                                /* 0x000fe2000010e900 */
      |        /*00d0*/                   EXIT ;                                       /* 0x000000000000794d */
      |                                                                                /* 0x000fea0003800000 */
      |.L_x_2:
      |        /*00e0*/                   BRA `(.L_x_2);                               /* 0xfffffff000007947 */
      |                                                                                /* 0x000fc0000383ffff */
      |        /*00f0*/                   NOP;                                         /* 0x0000000000007918 */
      |                                                                                /* 0x000fc00000000000 */
      |.L_x_3:
      |""".stripMargin

}

class NanosleepWarpFixtures extends NvidiaFixtureSuite {
  import NanosleepWarpFixtures.*

  reproduces("nanosleepWarpSm75ProbeSm75NanosleepDynamic")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.cu" -> nanosleepWarpSm75ProbeCudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_75 input.cu -o kernel.cubin",
      "symbol=$(readelf -sW kernel.cubin | awk '$NF == \"nanosleep_dynamic\" {sub(\":\", \"\", $1); print $1; exit}')",
      "nvdisasm -c -ndf -hex -fun \"$symbol\" kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = nanosleepWarpSm75ProbeSm75NanosleepDynamic
  )

  reproduces("nanosleepWarpSm75ProbeSm75NanosleepImmediates")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.cu" -> nanosleepWarpSm75ProbeCudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_75 input.cu -o kernel.cubin",
      "symbol=$(readelf -sW kernel.cubin | awk '$NF == \"nanosleep_immediates\" {sub(\":\", \"\", $1); print $1; exit}')",
      "nvdisasm -c -ndf -hex -fun \"$symbol\" kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = nanosleepWarpSm75ProbeSm75NanosleepImmediates
  )

  reproduces("nanosleepWarpSm75ProbeSm75NanosleepPredicated")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.cu" -> nanosleepWarpSm75ProbeCudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_75 input.cu -o kernel.cubin",
      "symbol=$(readelf -sW kernel.cubin | awk '$NF == \"nanosleep_predicated\" {sub(\":\", \"\", $1); print $1; exit}')",
      "nvdisasm -c -ndf -hex -fun \"$symbol\" kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = nanosleepWarpSm75ProbeSm75NanosleepPredicated
  )

}
