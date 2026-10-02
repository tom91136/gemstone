package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object Hfma2Fixtures {
  val hfma2MmaSm89CudaSource: String =
    """extern "C" __global__ void hfma2_mma_probe(unsigned *output, const unsigned *a, const unsigned *c) {
      |  const unsigned index = threadIdx.x;
      |  const unsigned left = a[index];
      |  unsigned result = c[index];
      |  asm volatile("fma.rn.f16x2 %0, %1, %2, %0;" : "+r"(result) : "r"(left), "r"(0u));
      |  output[index] = result;
      |}
      |
      |extern "C" __global__ void hfma2_add_reference(unsigned *output, const unsigned *a, const unsigned *c) {
      |  const unsigned index = threadIdx.x;
      |  unsigned result;
      |  asm volatile("add.rn.f16x2 %0, %1, %2;" : "=r"(result) : "r"(a[index]), "r"(c[index]));
      |  output[index] = result;
      |}
      |""".stripMargin

  val hfma2SelectedSm89CudaSource: String =
    """#include <cuda_fp16.h>
      |
      |extern "C" __global__ void hfma2_selected(unsigned *output, const unsigned *left, const unsigned *right, const unsigned *addend) {
      |  const unsigned index = blockIdx.x * blockDim.x + threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(left + index);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(right + index);
      |  const __half2 low = __halves2half2(__low2half(a), __low2half(a));
      |  const __half2 high = __halves2half2(__high2half(b), __high2half(b));
      |  const __half2 c = *reinterpret_cast<const __half2 *>(addend + index);
      |  const __half2 result = __hfma2(low, high, c);
      |  reinterpret_cast<__half2 *>(output)[index] = result;
      |}
      |""".stripMargin

  val hfma2Sm89CudaSource: String =
    """#include <cuda_fp16.h>
      |
      |extern "C" __global__ void hfma2_vector(unsigned *output, const unsigned *left, const unsigned *right, const unsigned *addend) {
      |  const unsigned index = blockIdx.x * blockDim.x + threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(left + index);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(right + index);
      |  const __half2 c = *reinterpret_cast<const __half2 *>(addend + index);
      |  const __half2 result = __hfma2(a, b, c);
      |  reinterpret_cast<__half2 *>(output)[index] = result;
      |}
      |""".stripMargin

  val hfma2ConstantSm8938004000: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_mma_probe     --------------------------
      |	.section	.text.hfma2_mma_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hfma2_mma_probe
      |        .type           hfma2_mma_probe,@function
      |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
      |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_mma_probe:
      |.text.hfma2_mma_probe:
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
      |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, 0.5, 2 ;                        /* 0x38004000ff097435 */
      |                                                                                          /* 0x004fca00000001ff */
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

  val hfma2ConstantSm897c003c00: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_mma_probe     --------------------------
      |	.section	.text.hfma2_mma_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hfma2_mma_probe
      |        .type           hfma2_mma_probe,@function
      |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
      |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_mma_probe:
      |.text.hfma2_mma_probe:
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
      |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, +INF , 1 ;                      /* 0x7c003c00ff097435 */
      |                                                                                          /* 0x004fca00000001ff */
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

  val hfma2ConstantSm897c01fc01: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_mma_probe     --------------------------
      |	.section	.text.hfma2_mma_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hfma2_mma_probe
      |        .type           hfma2_mma_probe,@function
      |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
      |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_mma_probe:
      |.text.hfma2_mma_probe:
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
      |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, +SNAN , -SNAN  ;                /* 0x7c01fc01ff097435 */
      |                                                                                          /* 0x004fca00000001ff */
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

  val hfma2ConstantSm897e013c00: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_mma_probe     --------------------------
      |	.section	.text.hfma2_mma_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hfma2_mma_probe
      |        .type           hfma2_mma_probe,@function
      |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
      |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_mma_probe:
      |.text.hfma2_mma_probe:
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
      |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, +QNAN , 1 ;                     /* 0x7e013c00ff097435 */
      |                                                                                          /* 0x004fca00000001ff */
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

  val hfma2ConstantSm8980000000: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_mma_probe     --------------------------
      |	.section	.text.hfma2_mma_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hfma2_mma_probe
      |        .type           hfma2_mma_probe,@function
      |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
      |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_mma_probe:
      |.text.hfma2_mma_probe:
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
      |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, -0.0 , 0 ;                      /* 0x80000000ff097435 */
      |                                                                                          /* 0x004fca00000001ff */
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

  val hfma2ConstantSm89Fc007c00: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_mma_probe     --------------------------
      |	.section	.text.hfma2_mma_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hfma2_mma_probe
      |        .type           hfma2_mma_probe,@function
      |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
      |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_mma_probe:
      |.text.hfma2_mma_probe:
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
      |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, -INF , +INF  ;                  /* 0xfc007c00ff097435 */
      |                                                                                          /* 0x004fca00000001ff */
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

  val hfma2ConstantSm89Fe017e01: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_mma_probe     --------------------------
      |	.section	.text.hfma2_mma_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hfma2_mma_probe
      |        .type           hfma2_mma_probe,@function
      |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
      |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_mma_probe:
      |.text.hfma2_mma_probe:
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
      |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, -QNAN , +QNAN  ;                /* 0xfe017e01ff097435 */
      |                                                                                          /* 0x004fca00000001ff */
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

  val hfma2MmaAddReferenceSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_add_reference --------------------------
      |	.section	.text.hfma2_add_reference,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hfma2_add_reference
      |        .type           hfma2_add_reference,@function
      |        .size           hfma2_add_reference,(.L_x_1 - hfma2_add_reference)
      |        .other          hfma2_add_reference,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_add_reference:
      |.text.hfma2_add_reference:
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
      |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HADD2 R9, R2, R5 ;                                     /* 0x0000000502097230 */
      |                                                                                          /* 0x004fca0000000000 */
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

  val hfma2MmaAliasSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_mma_probe     --------------------------
      |	.section	.text.hfma2_mma_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hfma2_mma_probe
      |        .type           hfma2_mma_probe,@function
      |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
      |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_mma_probe:
      |.text.hfma2_mma_probe:
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
      |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HFMA2.MMA R5, R2, 1, 1, R5 ;                           /* 0x3c003c0002057835 */
      |                                                                                          /* 0x004fca0000000005 */
      |        /*00a0*/                   STG.E [R6.64], R5 ;                                    /* 0x0000000506007986 */
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

  val hfma2MmaNonaliasSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_mma_probe     --------------------------
      |	.section	.text.hfma2_mma_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hfma2_mma_probe
      |        .type           hfma2_mma_probe,@function
      |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
      |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_mma_probe:
      |.text.hfma2_mma_probe:
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
      |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HFMA2.MMA R9, R2, 1, 1, R5 ;                           /* 0x3c003c0002097835 */
      |                                                                                          /* 0x004fca0000000005 */
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

  val hfma2MmaZeroAliasSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_mma_probe     --------------------------
      |	.section	.text.hfma2_mma_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hfma2_mma_probe
      |        .type           hfma2_mma_probe,@function
      |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
      |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_mma_probe:
      |.text.hfma2_mma_probe:
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
      |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HFMA2.MMA R5, R2, RZ, R5 ;                             /* 0x200000ff02057235 */
      |                                                                                          /* 0x004fca0000000005 */
      |        /*00a0*/                   STG.E [R6.64], R5 ;                                    /* 0x0000000506007986 */
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

  val hfma2SelectedSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_selected      --------------------------
      |	.section	.text.hfma2_selected,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=14"
      |	.align	128
      |        .global         hfma2_selected
      |        .type           hfma2_selected,@function
      |        .size           hfma2_selected,(.L_x_1 - hfma2_selected)
      |        .other          hfma2_selected,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_selected:
      |.text.hfma2_selected:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;
      |        /*0010*/                   S2R R8, SR_CTAID.X ;
      |        /*0020*/                   MOV R9, 0x4 ;
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0040*/                   S2R R3, SR_TID.X ;
      |        /*0050*/                   IMAD R8, R8, c[0x0][0x0], R3 ;
      |        /*0060*/                   IMAD.WIDE.U32 R2, R8, R9, c[0x0][0x168] ;
      |        /*0070*/                   IMAD.WIDE.U32 R4, R8.reuse, R9.reuse, c[0x0][0x170] ;
      |        /*0080*/                   LDG.E.U16.CONSTANT R2, [R2.64] ;
      |        /*0090*/                   IMAD.WIDE.U32 R6, R8.reuse, R9.reuse, c[0x0][0x178] ;
      |        /*00a0*/                   LDG.E.U16.CONSTANT R5, [R4.64+0x2] ;
      |        /*00b0*/                   LDG.E.CONSTANT R6, [R6.64] ;
      |        /*00c0*/                   IMAD.WIDE.U32 R8, R8, R9, c[0x0][0x160] ;
      |        /*00d0*/                   HFMA2 R11, R2.H0_H0, R5.H0_H0, R6 ;
      |        /*00e0*/                   STG.E [R8.64], R11 ;
      |        /*00f0*/                   EXIT ;
      |.L_x_0:
      |        /*0100*/                   BRA `(.L_x_0);
      |        /*0110*/                   NOP;
      |        /*0120*/                   NOP;
      |        /*0130*/                   NOP;
      |        /*0140*/                   NOP;
      |        /*0150*/                   NOP;
      |        /*0160*/                   NOP;
      |        /*0170*/                   NOP;
      |        /*0180*/                   NOP;
      |        /*0190*/                   NOP;
      |        /*01a0*/                   NOP;
      |        /*01b0*/                   NOP;
      |        /*01c0*/                   NOP;
      |        /*01d0*/                   NOP;
      |        /*01e0*/                   NOP;
      |        /*01f0*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val hfma2Sm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hfma2_vector        --------------------------
      |	.section	.text.hfma2_vector,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=14"
      |	.align	128
      |        .global         hfma2_vector
      |        .type           hfma2_vector,@function
      |        .size           hfma2_vector,(.L_x_1 - hfma2_vector)
      |        .other          hfma2_vector,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hfma2_vector:
      |.text.hfma2_vector:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;
      |        /*0010*/                   S2R R8, SR_CTAID.X ;
      |        /*0020*/                   MOV R9, 0x4 ;
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0040*/                   S2R R3, SR_TID.X ;
      |        /*0050*/                   IMAD R8, R8, c[0x0][0x0], R3 ;
      |        /*0060*/                   IMAD.WIDE.U32 R2, R8, R9, c[0x0][0x168] ;
      |        /*0070*/                   IMAD.WIDE.U32 R4, R8.reuse, R9.reuse, c[0x0][0x170] ;
      |        /*0080*/                   LDG.E.CONSTANT R2, [R2.64] ;
      |        /*0090*/                   IMAD.WIDE.U32 R6, R8.reuse, R9.reuse, c[0x0][0x178] ;
      |        /*00a0*/                   LDG.E.CONSTANT R5, [R4.64] ;
      |        /*00b0*/                   LDG.E.CONSTANT R6, [R6.64] ;
      |        /*00c0*/                   IMAD.WIDE.U32 R8, R8, R9, c[0x0][0x160] ;
      |        /*00d0*/                   HFMA2 R11, R2, R5, R6 ;
      |        /*00e0*/                   STG.E [R8.64], R11 ;
      |        /*00f0*/                   EXIT ;
      |.L_x_0:
      |        /*0100*/                   BRA `(.L_x_0);
      |        /*0110*/                   NOP;
      |        /*0120*/                   NOP;
      |        /*0130*/                   NOP;
      |        /*0140*/                   NOP;
      |        /*0150*/                   NOP;
      |        /*0160*/                   NOP;
      |        /*0170*/                   NOP;
      |        /*0180*/                   NOP;
      |        /*0190*/                   NOP;
      |        /*01a0*/                   NOP;
      |        /*01b0*/                   NOP;
      |        /*01c0*/                   NOP;
      |        /*01d0*/                   NOP;
      |        /*01e0*/                   NOP;
      |        /*01f0*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val hfma2MmaZeroSm89CudaSource: String = hfma2MmaSm89CudaSource

}

class Hfma2Fixtures extends NvidiaFixtureSuite {
  import Hfma2Fixtures.*

  reproduces("hfma2ConstantSm8938004000")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-mma-zero-sm89.cu" -> hfma2MmaZeroSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-zero-sm89.cu -o hfma2-mma-zero-sm89.cubin",
      "perl -e '$x=do{local $/;<>}; $o=pack(\"H*\",\"31720902ff0000200500000000ca4f00\"); die \"baseline HFMA count\\n\" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o); $imm=hex(shift); $n=pack(\"Q<Q<\",(($imm<<32)|0xff097435),0x004fca00000001ff); $x =~ s/\\Q$o\\E/$n/; print $x' hfma2-mma-zero-sm89.cubin 0x38004000 > 38004000.cubin",
      """symbol=$(readelf -Ws 38004000.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$symbol" 38004000.cubin > 38004000.sass""".stripMargin
    ),
    output = "38004000.sass",
    expected = hfma2ConstantSm8938004000
  )

  reproduces("hfma2ConstantSm897c003c00")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-mma-zero-sm89.cu" -> hfma2MmaZeroSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-zero-sm89.cu -o hfma2-mma-zero-sm89.cubin",
      "perl -e '$x=do{local $/;<>}; $o=pack(\"H*\",\"31720902ff0000200500000000ca4f00\"); die \"baseline HFMA count\\n\" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o); $imm=hex(shift); $n=pack(\"Q<Q<\",(($imm<<32)|0xff097435),0x004fca00000001ff); $x =~ s/\\Q$o\\E/$n/; print $x' hfma2-mma-zero-sm89.cubin 0x7c003c00 > 7c003c00.cubin",
      """symbol=$(readelf -Ws 7c003c00.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$symbol" 7c003c00.cubin > 7c003c00.sass""".stripMargin
    ),
    output = "7c003c00.sass",
    expected = hfma2ConstantSm897c003c00
  )

  reproduces("hfma2ConstantSm897c01fc01")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-mma-zero-sm89.cu" -> hfma2MmaZeroSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-zero-sm89.cu -o hfma2-mma-zero-sm89.cubin",
      "perl -e '$x=do{local $/;<>}; $o=pack(\"H*\",\"31720902ff0000200500000000ca4f00\"); die \"baseline HFMA count\\n\" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o); $imm=hex(shift); $n=pack(\"Q<Q<\",(($imm<<32)|0xff097435),0x004fca00000001ff); $x =~ s/\\Q$o\\E/$n/; print $x' hfma2-mma-zero-sm89.cubin 0x7c01fc01 > 7c01fc01.cubin",
      """symbol=$(readelf -Ws 7c01fc01.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$symbol" 7c01fc01.cubin > 7c01fc01.sass""".stripMargin
    ),
    output = "7c01fc01.sass",
    expected = hfma2ConstantSm897c01fc01
  )

  reproduces("hfma2ConstantSm897e013c00")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-mma-zero-sm89.cu" -> hfma2MmaZeroSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-zero-sm89.cu -o hfma2-mma-zero-sm89.cubin",
      "perl -e '$x=do{local $/;<>}; $o=pack(\"H*\",\"31720902ff0000200500000000ca4f00\"); die \"baseline HFMA count\\n\" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o); $imm=hex(shift); $n=pack(\"Q<Q<\",(($imm<<32)|0xff097435),0x004fca00000001ff); $x =~ s/\\Q$o\\E/$n/; print $x' hfma2-mma-zero-sm89.cubin 0x7e013c00 > 7e013c00.cubin",
      """symbol=$(readelf -Ws 7e013c00.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$symbol" 7e013c00.cubin > 7e013c00.sass""".stripMargin
    ),
    output = "7e013c00.sass",
    expected = hfma2ConstantSm897e013c00
  )

  reproduces("hfma2ConstantSm8980000000")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-mma-zero-sm89.cu" -> hfma2MmaZeroSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-zero-sm89.cu -o hfma2-mma-zero-sm89.cubin",
      "perl -e '$x=do{local $/;<>}; $o=pack(\"H*\",\"31720902ff0000200500000000ca4f00\"); die \"baseline HFMA count\\n\" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o); $imm=hex(shift); $n=pack(\"Q<Q<\",(($imm<<32)|0xff097435),0x004fca00000001ff); $x =~ s/\\Q$o\\E/$n/; print $x' hfma2-mma-zero-sm89.cubin 0x80000000 > 80000000.cubin",
      """symbol=$(readelf -Ws 80000000.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$symbol" 80000000.cubin > 80000000.sass""".stripMargin
    ),
    output = "80000000.sass",
    expected = hfma2ConstantSm8980000000
  )

  reproduces("hfma2ConstantSm89Fc007c00")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-mma-zero-sm89.cu" -> hfma2MmaZeroSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-zero-sm89.cu -o hfma2-mma-zero-sm89.cubin",
      "perl -e '$x=do{local $/;<>}; $o=pack(\"H*\",\"31720902ff0000200500000000ca4f00\"); die \"baseline HFMA count\\n\" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o); $imm=hex(shift); $n=pack(\"Q<Q<\",(($imm<<32)|0xff097435),0x004fca00000001ff); $x =~ s/\\Q$o\\E/$n/; print $x' hfma2-mma-zero-sm89.cubin 0xfc007c00 > fc007c00.cubin",
      """symbol=$(readelf -Ws fc007c00.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$symbol" fc007c00.cubin > fc007c00.sass""".stripMargin
    ),
    output = "fc007c00.sass",
    expected = hfma2ConstantSm89Fc007c00
  )

  reproduces("hfma2ConstantSm89Fe017e01")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-mma-zero-sm89.cu" -> hfma2MmaZeroSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-zero-sm89.cu -o hfma2-mma-zero-sm89.cubin",
      "perl -e '$x=do{local $/;<>}; $o=pack(\"H*\",\"31720902ff0000200500000000ca4f00\"); die \"baseline HFMA count\\n\" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o); $imm=hex(shift); $n=pack(\"Q<Q<\",(($imm<<32)|0xff097435),0x004fca00000001ff); $x =~ s/\\Q$o\\E/$n/; print $x' hfma2-mma-zero-sm89.cubin 0xfe017e01 > fe017e01.cubin",
      """symbol=$(readelf -Ws fe017e01.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$symbol" fe017e01.cubin > fe017e01.sass""".stripMargin
    ),
    output = "fe017e01.sass",
    expected = hfma2ConstantSm89Fe017e01
  )

  reproduces("hfma2MmaAddReferenceSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-mma-sm89.cu" -> hfma2MmaSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-sm89.cu -o baseline.cubin",
      """symbol=$(readelf -Ws baseline.cubin | awk '$NF=="hfma2_add_reference"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$symbol" baseline.cubin > hfma2-mma-add-reference-sm89.sass""".stripMargin
    ),
    output = "hfma2-mma-add-reference-sm89.sass",
    expected = hfma2MmaAddReferenceSm89
  )

  reproduces("hfma2MmaAliasSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-mma-sm89.cu" -> hfma2MmaSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-sm89.cu -o hfma2-mma-sm89.cubin",
      """perl -0777 - hfma2-mma-sm89.cubin > hfma2-mma-alias-sm89.cubin <<'PERL'
      |$x=<>;
      |$o=pack("H*","31720902ff0000200500000000ca4f00");
      |$n=pack("H*","35780502003c003c0500000000ca4f00");
      |$s=pack("H*","8679000609000000");
      |die "HFMA count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$i=index($x,$o);
      |@s=();
      |$p=0;
      |while(($p=index($x,$s,$p))>=0){push @s,$p;
      |$p+=length($s)} die "store slots\n" unless @s==2 && $s[1]==$i+16;
      |substr($x,$i,length($o),$n);
      |substr($x,$s[1],8,pack("H*","8679000605000000"));
      |print $x
      |PERL""".stripMargin,
      """fun_index=$(readelf -Ws hfma2-mma-alias-sm89.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$fun_index" hfma2-mma-alias-sm89.cubin > hfma2-mma-alias-sm89.sass""".stripMargin
    ),
    output = "hfma2-mma-alias-sm89.sass",
    expected = hfma2MmaAliasSm89
  )

  reproduces("hfma2MmaNonaliasSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-mma-sm89.cu" -> hfma2MmaSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-sm89.cu -o hfma2-mma-sm89.cubin",
      """perl -0777 - hfma2-mma-sm89.cubin > hfma2-mma-nonalias-sm89.cubin <<'PERL'
      |$x=<>;
      |$o=pack("H*","31720902ff0000200500000000ca4f00");
      |$n=pack("H*","35780902003c003c0500000000ca4f00");
      |die "HFMA count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$x =~ s/\Q$o\E/$n/;
      |print $x
      |PERL""".stripMargin,
      """fun_index=$(readelf -Ws hfma2-mma-nonalias-sm89.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$fun_index" hfma2-mma-nonalias-sm89.cubin > hfma2-mma-nonalias-sm89.sass""".stripMargin
    ),
    output = "hfma2-mma-nonalias-sm89.sass",
    expected = hfma2MmaNonaliasSm89
  )

  reproduces("hfma2MmaZeroAliasSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-mma-zero-sm89.cu" -> hfma2MmaZeroSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-zero-sm89.cu -o hfma2-mma-zero-sm89.cubin",
      """perl -0777 - hfma2-mma-zero-sm89.cubin > hfma2-mma-zero-alias-sm89.cubin <<'PERL'
      |$x=<>;
      |$o=pack("H*","31720902ff0000200500000000ca4f00");
      |$n=pack("H*","35720502ff0000200500000000ca4f00");
      |$s=pack("H*","8679000609000000");
      |die "HFMA count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$i=index($x,$o);
      |@s=();
      |$p=0;
      |while(($p=index($x,$s,$p))>=0){push @s,$p;
      |$p+=length($s)} die "store slots\n" unless @s==2 && $s[1]==$i+16;
      |substr($x,$i,length($o),$n);
      |substr($x,$s[1],8,pack("H*","8679000605000000"));
      |print $x
      |PERL""".stripMargin,
      """symbol=$(readelf -Ws hfma2-mma-zero-alias-sm89.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$symbol" hfma2-mma-zero-alias-sm89.cubin > hfma2-mma-zero-alias-sm89.sass""".stripMargin
    ),
    output = "hfma2-mma-zero-alias-sm89.sass",
    expected = hfma2MmaZeroAliasSm89
  )

  reproduces("hfma2SelectedSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-selected-sm89.cu" -> hfma2SelectedSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-selected-sm89.cu -o hfma2-selected-sm89.cubin",
      "nvdisasm -c -ndf hfma2-selected-sm89.cubin > hfma2-selected-sm89.sass"
    ),
    output = "hfma2-selected-sm89.sass",
    expected = hfma2SelectedSm89
  )

  reproduces("hfma2Sm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hfma2-sm89.cu" -> hfma2Sm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-sm89.cu -o hfma2-sm89.cubin",
      "nvdisasm -c -ndf hfma2-sm89.cubin > hfma2-sm89.sass"
    ),
    output = "hfma2-sm89.sass",
    expected = hfma2Sm89
  )

}
