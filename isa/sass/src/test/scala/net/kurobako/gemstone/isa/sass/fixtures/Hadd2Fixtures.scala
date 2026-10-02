package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object Hadd2Fixtures {
  val hadd2F32Sm75ProbeCudaSource: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |extern "C" __global__ void probe(uint32_t *out, const uint32_t *in) {
      |  unsigned i = blockIdx.x;
      |  __half2 x = *reinterpret_cast<const __half2 *>(in + i);
      |  __half2 z = __halves2half2(__float2half(-0.0f), __float2half(-0.0f));
      |  float2 r = __half22float2(__hadd2(x, z));
      |  out[2*i] = __float_as_uint(r.x);
      |  out[2*i+1] = __float_as_uint(r.y);
      |}
      |""".stripMargin

  val hadd2F32ToHalfSm75ProbeCudaSource: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |extern "C" __global__ void probe(uint32_t *out, const uint32_t *in) {
      |  unsigned i = blockIdx.x;
      |  float x = __uint_as_float(in[i]);
      |  __half2 converted = __float2half2_rn(x);
      |  __half2 negativeZero = __halves2half2(__ushort_as_half(0x8000), __ushort_as_half(0x8000));
      |  __half2 result = __hadd2(converted, negativeZero);
      |  out[i] = *reinterpret_cast<uint32_t *>(&result);
      |}
      |""".stripMargin

  val hadd2SelectCudaSource: String =
    """#include <cuda_fp16.h>
      |extern "C" __global__ void hadd2_selected_low(unsigned *out, const unsigned *a, const unsigned *b) {
      |  unsigned i = threadIdx.x;
      |  union U { unsigned u; half2 h; } x, y, z;
      |  x.u = a[i]; y.u = b[i];
      |  z.h = __hadd2(__halves2half2(__low2half(x.h), __low2half(x.h)),
      |                __halves2half2(__low2half(y.h), __low2half(y.h)));
      |  out[i] = z.u;
      |}
      |extern "C" __global__ void hadd2_selected_high(unsigned *out, const unsigned *a, const unsigned *b) {
      |  unsigned i = threadIdx.x;
      |  union U { unsigned u; half2 h; } x, y, z;
      |  x.u = a[i]; y.u = b[i];
      |  z.h = __hadd2(__halves2half2(__high2half(x.h), __high2half(x.h)),
      |                __halves2half2(__high2half(y.h), __high2half(y.h)));
      |  out[i] = z.u;
      |}
      |""".stripMargin

  val hadd2Sm89CudaSource: String =
    """#include <cuda_fp16.h>
      |extern "C" __global__ void convert_half(float *out, const __half2 *in) {
      |  unsigned i = blockIdx.x;
      |  __half2 value = in[i];
      |  out[2 * i] = __low2float(value);
      |  out[2 * i + 1] = __high2float(value);
      |}
      |""".stripMargin

  val hadd2F32Sm75Probe: String =
    """	.target	sm_75
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=5"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
      |                                                                              /* 0x000fe40000000f00 */
      |        /*0010*/                   S2UR UR6, SR_CTAID.X ;                     /* 0x00000000000679c3 */
      |                                                                              /* 0x000e220000002500 */
      |        /*0020*/                   UMOV UR7, 0x4 ;                            /* 0x0000000400077882 */
      |                                                                              /* 0x000fe40000000000 */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x168] ;               /* 0x00005a0000047ab9 */
      |                                                                              /* 0x000fe40000000a00 */
      |        /*0040*/                   UIMAD.WIDE.U32 UR4, UR6, UR7, UR4 ;        /* 0x00000007060472a5 */
      |                                                                              /* 0x001fd2000f8e0004 */
      |        /*0050*/                   LDG.E.CONSTANT.SYS R0, [UR4] ;             /* 0x00000004ff007981 */
      |                                                                              /* 0x000ea2000c1e6900 */
      |        /*0060*/                   USHF.L.U32 UR6, UR6, 0x1, URZ ;            /* 0x0000000106067899 */
      |                                                                              /* 0x000fe4000800063f */
      |        /*0070*/                   ULDC.64 UR4, c[0x0][0x160] ;               /* 0x0000580000047ab9 */
      |                                                                              /* 0x000fe40000000a00 */
      |        /*0080*/                   UIMAD.WIDE.U32 UR4, UR6, UR7, UR4 ;        /* 0x00000007060472a5 */
      |                                                                              /* 0x000fe2000f8e0004 */
      |        /*0090*/                   HADD2 R0, R0, -RZ.H0_H0 ;                  /* 0xa00000ff00007230 */
      |                                                                              /* 0x004fcc0000000000 */
      |        /*00a0*/                   HADD2.F32 R2, R0.reuse.H0_H0, -RZ.H0_H0 ;  /* 0xa00000ff00027230 */
      |                                                                              /* 0x040fe40000004800 */
      |        /*00b0*/                   HADD2.F32 R0, R0.H1_H1, -RZ.H0_H0 ;        /* 0xa00000ff00007230 */
      |                                                                              /* 0x000fcc0000004c00 */
      |        /*00c0*/                   STG.E.SYS [UR4], R2 ;                      /* 0x00000002ff007986 */
      |                                                                              /* 0x000fe8000c10e904 */
      |        /*00d0*/                   STG.E.SYS [UR4+0x4], R0 ;                  /* 0x00000400ff007986 */
      |                                                                              /* 0x000fe2000c10e904 */
      |        /*00e0*/                   EXIT ;                                     /* 0x000000000000794d */
      |                                                                              /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00f0*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |.L_x_1:
      |""".stripMargin

  val hadd2F32ToHalfSm75ProbeSm75Patched: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM75 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM75)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=5"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;               /* 0x00000a0000017a02 */
      |                                                                        /* 0x000fc40000000f00 */
      |        /*0010*/                   S2UR UR6, SR_CTAID.X ;               /* 0x00000000000679c3 */
      |                                                                        /* 0x000e220000002500 */
      |        /*0020*/                   UMOV UR7, 0x4 ;                      /* 0x0000000400077882 */
      |                                                                        /* 0x000fe40000000000 */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x168] ;         /* 0x00005a0000047ab9 */
      |                                                                        /* 0x000fe40000000a00 */
      |        /*0040*/                   UIMAD.WIDE.U32 UR4, UR6, UR7, UR4 ;  /* 0x00000007060472a5 */
      |                                                                        /* 0x001fd2000f8e0004 */
      |        /*0050*/                   LDG.E.SYS R0, [UR4] ;                /* 0x00000004ff007981 */
      |                                                                        /* 0x000ea2000c1ee900 */
      |        /*0060*/                   ULDC.64 UR4, c[0x0][0x160] ;         /* 0x0000580000047ab9 */
      |                                                                        /* 0x000fe40000000a00 */
      |        /*0070*/                   UIMAD.WIDE.U32 UR4, UR6, UR7, UR4 ;  /* 0x00000007060472a5 */
      |                                                                        /* 0x000fe2000f8e0004 */
      |        /*0080*/                   HADD2 R0, R0.F32, -RZ.H0_H0 ;        /* 0xa00000ff00007230 */
      |                                                                        /* 0x004e240000000400 */
      |        /*0090*/                   NOP ;                                /* 0x0000000000007918 */
      |                                                                        /* 0x001fd00000000800 */
      |        /*00a0*/                   STG.E.SYS [UR4], R0 ;                /* 0x00000000ff007986 */
      |                                                                        /* 0x000fe2000c10e904 */
      |        /*00b0*/                   EXIT ;                               /* 0x000000000000794d */
      |                                                                        /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00c0*/                   BRA `(.L_x_0);                       /* 0xfffffff000007947 */
      |                                                                        /* 0x000fc0000383ffff */
      |        /*00d0*/                   NOP;                                 /* 0x0000000000007918 */
      |                                                                        /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                 /* 0x0000000000007918 */
      |                                                                        /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                 /* 0x0000000000007918 */
      |                                                                        /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hadd2SelectedHighSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hadd2_selected_high --------------------------
      |	.section	.text.hadd2_selected_high,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hadd2_selected_high
      |        .type           hadd2_selected_high,@function
      |        .size           hadd2_selected_high,(.L_x_1 - hadd2_selected_high)
      |        .other          hadd2_selected_high,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hadd2_selected_high:
      |.text.hadd2_selected_high:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
      |                                                                                          /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
      |                                                                                          /* 0x000e220000002100 */
      |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
      |                                                                                          /* 0x000fe20000000f00 */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
      |                                                                                          /* 0x000fc80000000a00 */
      |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
      |                                                                                          /* 0x001fc800078e0007 */
      |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
      |                                                                                          /* 0x0c0fe400078e0007 */
      |        /*0060*/                   LDG.E.U16.CONSTANT R2, [R2.64+0x2] ;                   /* 0x0000020402027981 */
      |                                                                                          /* 0x000ea8000c1e9500 */
      |        /*0070*/                   LDG.E.U16.CONSTANT R5, [R4.64+0x2] ;                   /* 0x0000020404057981 */
      |                                                                                          /* 0x000ea2000c1e9500 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HADD2 R9, R2.H0_H0, R5.H0_H0 ;                         /* 0x2000000502097230 */
      |                                                                                          /* 0x004fca0000000800 */
      |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
      |                                                                                          /* 0x000fe2000c101904 */
      |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
      |                                                                                          /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00c0*/                   BRA `(.L_x_0);                                         /* 0xfffffff000007947 */
      |                                                                                          /* 0x000fc0000383ffff */
      |        /*00d0*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0100*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0110*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hadd2SelectedLowSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hadd2_selected_low  --------------------------
      |	.section	.text.hadd2_selected_low,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hadd2_selected_low
      |        .type           hadd2_selected_low,@function
      |        .size           hadd2_selected_low,(.L_x_1 - hadd2_selected_low)
      |        .other          hadd2_selected_low,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hadd2_selected_low:
      |.text.hadd2_selected_low:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
      |                                                                                          /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
      |                                                                                          /* 0x000e220000002100 */
      |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
      |                                                                                          /* 0x000fe20000000f00 */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
      |                                                                                          /* 0x000fc80000000a00 */
      |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
      |                                                                                          /* 0x001fc800078e0007 */
      |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
      |                                                                                          /* 0x0c0fe400078e0007 */
      |        /*0060*/                   LDG.E.U16.CONSTANT R2, [R2.64] ;                       /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e9500 */
      |        /*0070*/                   LDG.E.U16.CONSTANT R5, [R4.64] ;                       /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e9500 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HADD2 R9, R2.H0_H0, R5.H0_H0 ;                         /* 0x2000000502097230 */
      |                                                                                          /* 0x004fca0000000800 */
      |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
      |                                                                                          /* 0x000fe2000c101904 */
      |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
      |                                                                                          /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00c0*/                   BRA `(.L_x_0);                                         /* 0xfffffff000007947 */
      |                                                                                          /* 0x000fc0000383ffff */
      |        /*00d0*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0100*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0110*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hadd2Sm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.convert_half        --------------------------
      |	.section	.text.convert_half,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         convert_half
      |        .type           convert_half,@function
      |        .size           convert_half,(.L_x_1 - convert_half)
      |        .other          convert_half,@"STO_CUDA_ENTRY STV_DEFAULT"
      |convert_half:
      |.text.convert_half:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;
      |        /*0010*/                   S2R R4, SR_CTAID.X ;
      |        /*0020*/                   MOV R5, 0x4 ;
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R5, c[0x0][0x168] ;
      |        /*0050*/                   LDG.E.CONSTANT R2, [R2.64] ;
      |        /*0060*/                   SHF.L.U32 R4, R4, 0x1, RZ ;
      |        /*0070*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;
      |        /*0080*/                   HADD2.F32 R7, -RZ, R2.reuse.H0_H0 ;
      |        /*0090*/                   HADD2.F32 R9, -RZ, R2.H1_H1 ;
      |        /*00a0*/                   STG.E [R4.64], R7 ;
      |        /*00b0*/                   STG.E [R4.64+0x4], R9 ;
      |        /*00c0*/                   EXIT ;
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0);
      |        /*00e0*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*0100*/                   NOP;
      |        /*0110*/                   NOP;
      |        /*0120*/                   NOP;
      |        /*0130*/                   NOP;
      |        /*0140*/                   NOP;
      |        /*0150*/                   NOP;
      |        /*0160*/                   NOP;
      |        /*0170*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class Hadd2Fixtures extends NvidiaFixtureSuite {
  import Hadd2Fixtures.*

  reproduces("hadd2F32Sm75Probe")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "probe.cu" -> hadd2F32Sm75ProbeCudaSource
    ),
    script = Vec(
      "nvcc -ccbin g++-11 -arch=sm_75 -lineinfo -cubin probe.cu -o probe.cubin",
      "nvdisasm -c -ndf -hex probe.cubin > probe.sass"
    ),
    output = "probe.sass",
    expected = hadd2F32Sm75Probe
  )

  reproduces("hadd2F32ToHalfSm75ProbeSm75Patched")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("13.2.78")),
    inputs = Vec(
      "probe.cu" -> hadd2F32ToHalfSm75ProbeCudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -arch=sm_75 -cubin probe.cu -o probe-sm75.cubin",
      """perl -0777 - probe-sm75.cubin > probe-sm75-patched.cubin <<'PERL'
      |$x=<>;
      |sub once { my($x,$old,$new)=@_;
      |die "unexpected/missing bytes\n" unless index($$x,$old)>=0 && index($$x,$old)==rindex($$x,$old);
      |$$x =~ s/\Q$old\E/$new/;
      |} once(\$x,pack("H*","04730000000000000008200000244e00"),pack("H*","30720000ff0000a00004000000244e00"));
      |once(\$x,pack("H*","30720200ff0000a00008000000d01f00"),pack("H*","18790000000000000008000000d01f00"));
      |once(\$x,pack("H*","867900ff0200000004e9100c00e20f00"),pack("H*","867900ff0000000004e9100c00e20f00"));
      |print $x
      |PERL""".stripMargin,
      "nvdisasm -c -ndf -hex probe-sm75-patched.cubin > probe-sm75-patched.sass"
    ),
    output = "probe-sm75-patched.sass",
    expected = hadd2F32ToHalfSm75ProbeSm75Patched
  )

  reproduces("hadd2SelectedHighSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hadd2-select.cu" -> hadd2SelectCudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hadd2-select.cu -o hadd2-select.cubin",
      """fun_index=$(readelf -Ws hadd2-select.cubin | awk '$NF=="hadd2_selected_high"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$fun_index" hadd2-select.cubin > hadd2-selected-high-sm89.sass""".stripMargin
    ),
    output = "hadd2-selected-high-sm89.sass",
    expected = hadd2SelectedHighSm89
  )

  reproduces("hadd2SelectedLowSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hadd2-select.cu" -> hadd2SelectCudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hadd2-select.cu -o hadd2-select.cubin",
      """fun_index=$(readelf -Ws hadd2-select.cubin | awk '$NF=="hadd2_selected_low"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$fun_index" hadd2-select.cubin > hadd2-selected-low-sm89.sass""".stripMargin
    ),
    output = "hadd2-selected-low-sm89.sass",
    expected = hadd2SelectedLowSm89
  )

  reproduces("hadd2Sm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hadd2-sm89.cu" -> hadd2Sm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hadd2-sm89.cu -o hadd2-sm89.cubin",
      "nvdisasm -c -ndf hadd2-sm89.cubin > hadd2-sm89.sass"
    ),
    output = "hadd2-sm89.sass",
    expected = hadd2Sm89
  )

}
