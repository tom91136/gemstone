package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object Hmul2Fixtures {
  val hmul2F32Sm75Hmul2F32CudaSource: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |
      |extern "C" __global__ void hmul2_f32_probe(uint32_t *output, const uint32_t *left, const uint32_t *right) {
      |  const unsigned index = blockIdx.x * blockDim.x + threadIdx.x;
      |  const uint32_t a = left[index];
      |  const uint32_t b = right[index];
      |  const __half a0 = __ushort_as_half(static_cast<unsigned short>(a));
      |  const __half a1 = __ushort_as_half(static_cast<unsigned short>(a >> 16));
      |  const __half b0 = __ushort_as_half(static_cast<unsigned short>(b));
      |  const __half b1 = __ushort_as_half(static_cast<unsigned short>(b >> 16));
      |  const float af0 = __half2float(a0);
      |  const float af1 = __half2float(a1);
      |  const float bf0 = __half2float(b0);
      |  const float bf1 = __half2float(b1);
      |  output[4 * index + 0] = __float_as_uint(af0 * bf0);
      |  output[4 * index + 1] = __float_as_uint(af0 * bf1);
      |  output[4 * index + 2] = __float_as_uint(af1 * bf0);
      |  output[4 * index + 3] = __float_as_uint(af1 * bf1);
      |}
      |""".stripMargin

  val hmul2SelectedSm89CudaSource: String =
    """#include <cuda_fp16.h>
      |
      |extern "C" __global__ void hmul2_selected(unsigned *output, const unsigned *left, const unsigned *right) {
      |  const unsigned index = blockIdx.x * blockDim.x + threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(left + index);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(right + index);
      |  const __half2 low = __halves2half2(__low2half(a), __low2half(a));
      |  const __half2 high = __halves2half2(__high2half(b), __high2half(b));
      |  reinterpret_cast<__half2 *>(output)[index] = __hmul2(low, high);
      |}
      |""".stripMargin

  val hmul2Sm89CudaSource: String =
    """#include <cuda_fp16.h>
      |
      |extern "C" __global__ void hmul2_vector(unsigned *output, const unsigned *left, const unsigned *right) {
      |  const unsigned index = blockIdx.x * blockDim.x + threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(left + index);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(right + index);
      |  const __half2 product = __hmul2(a, b);
      |  reinterpret_cast<__half2 *>(output)[index] = product;
      |}
      |""".stripMargin

  val hmul2F32Sm75Hmul2F32Sm75Patched: String =
    """	.target	sm_75
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmul2_f32_probe     --------------------------
      |	.section	.text.hmul2_f32_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=18"
      |	.align	128
      |        .global         hmul2_f32_probe
      |        .type           hmul2_f32_probe,@function
      |        .size           hmul2_f32_probe,(.L_x_1 - hmul2_f32_probe)
      |        .other          hmul2_f32_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hmul2_f32_probe:
      |.text.hmul2_f32_probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
      |                                                                              /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R0, SR_CTAID.X ;                       /* 0x0000000000007919 */
      |                                                                              /* 0x000e220000002500 */
      |        /*0020*/                   MOV R7, 0x4 ;                              /* 0x0000000400077802 */
      |                                                                              /* 0x000fc60000000f00 */
      |        /*0030*/                   S2R R3, SR_TID.X ;                         /* 0x0000000000037919 */
      |                                                                              /* 0x000e240000002100 */
      |        /*0040*/                   IMAD R0, R0, c[0x0][0x0], R3 ;             /* 0x0000000000007a24 */
      |                                                                              /* 0x001fc800078e0203 */
      |        /*0050*/                   IMAD.WIDE.U32 R2, R0, R7, c[0x0][0x168] ;  /* 0x00005a0000027625 */
      |                                                                              /* 0x000fc800078e0007 */
      |        /*0060*/                   IMAD.WIDE.U32 R4, R0, R7, c[0x0][0x170] ;  /* 0x00005c0000047625 */
      |                                                                              /* 0x000fc800078e0007 */
      |        /*0070*/                   LDG.E.SYS R2, [R2] ;                       /* 0x0000000002027381 */
      |                                                                              /* 0x000ea800001ee900 */
      |        /*0080*/                   LDG.E.SYS R4, [R4] ;                       /* 0x0000000004047381 */
      |                                                                              /* 0x000ee200001ee900 */
      |        /*0090*/                   SHF.L.U32 R6, R0, 0x2, RZ ;                /* 0x0000000200067819 */
      |                                                                              /* 0x000fca00000006ff */
      |        /*00a0*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;  /* 0x0000580006067625 */
      |                                                                              /* 0x000fe200078e0007 */
      |        /*00b0*/                   HADD2.F32 R0, R2.reuse.H0_H0, -RZ.H0_H0 ;  /* 0xa00000ff02007230 */
      |                                                                              /* 0x044fe40000004800 */
      |        /*00c0*/                   HADD2.F32 R8, R2.H1_H1, -RZ.H0_H0 ;        /* 0xa00000ff02087230 */
      |                                                                              /* 0x000fe40000004c00 */
      |        /*00d0*/                   HADD2.F32 R9, R4.reuse.H0_H0, -RZ.H0_H0 ;  /* 0xa00000ff04097230 */
      |                                                                              /* 0x048fe40000004800 */
      |        /*00e0*/                   HADD2.F32 R11, R4.H1_H1, -RZ.H0_H0 ;       /* 0xa00000ff040b7230 */
      |                                                                              /* 0x000fcc0000004c00 */
      |        /*00f0*/                   HMUL2.F32 R13, R2.H0_H0, R4.H0_H0 ;        /* 0x20000004020d7232 */
      |                                                                              /* 0x000fcc0000004800 */
      |        /*0100*/                   HMUL2.F32 R15, R2.H0_H0, R4.H1_H1 ;        /* 0x30000004020f7232 */
      |                                                                              /* 0x000fcc0000004800 */
      |        /*0110*/                   HMUL2.F32 R9, R2.H1_H1, R4.H0_H0 ;         /* 0x2000000402097232 */
      |                                                                              /* 0x000fcc0000004c00 */
      |        /*0120*/                   HMUL2.F32 R11, R2.H1_H1, R4.H1_H1 ;        /* 0x30000004020b7232 */
      |                                                                              /* 0x000fcc0000004c00 */
      |        /*0130*/                   STG.E.SYS [R6], R13 ;                      /* 0x0000000d06007386 */
      |                                                                              /* 0x000fe8000010e900 */
      |        /*0140*/                   STG.E.SYS [R6+0x4], R15 ;                  /* 0x0000040f06007386 */
      |                                                                              /* 0x000fe8000010e900 */
      |        /*0150*/                   STG.E.SYS [R6+0x8], R9 ;                   /* 0x0000080906007386 */
      |                                                                              /* 0x000fe8000010e900 */
      |        /*0160*/                   STG.E.SYS [R6+0xc], R11 ;                  /* 0x00000c0b06007386 */
      |                                                                              /* 0x000fe2000010e900 */
      |        /*0170*/                   EXIT ;                                     /* 0x000000000000794d */
      |                                                                              /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0180*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |        /*0190*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*01a0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hmul2SelectedSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmul2_selected      --------------------------
      |	.section	.text.hmul2_selected,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hmul2_selected
      |        .type           hmul2_selected,@function
      |        .size           hmul2_selected,(.L_x_1 - hmul2_selected)
      |        .other          hmul2_selected,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hmul2_selected:
      |.text.hmul2_selected:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;
      |        /*0010*/                   S2R R6, SR_CTAID.X ;
      |        /*0020*/                   MOV R7, 0x4 ;
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0040*/                   S2R R3, SR_TID.X ;
      |        /*0050*/                   IMAD R6, R6, c[0x0][0x0], R3 ;
      |        /*0060*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;
      |        /*0070*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;
      |        /*0080*/                   LDG.E.U16.CONSTANT R2, [R2.64] ;
      |        /*0090*/                   LDG.E.U16.CONSTANT R5, [R4.64+0x2] ;
      |        /*00a0*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;
      |        /*00b0*/                   HMUL2 R9, R2.H0_H0, R5.H0_H0 ;
      |        /*00c0*/                   STG.E [R6.64], R9 ;
      |        /*00d0*/                   EXIT ;
      |.L_x_0:
      |        /*00e0*/                   BRA `(.L_x_0);
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

  val hmul2Sm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmul2_vector        --------------------------
      |	.section	.text.hmul2_vector,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hmul2_vector
      |        .type           hmul2_vector,@function
      |        .size           hmul2_vector,(.L_x_1 - hmul2_vector)
      |        .other          hmul2_vector,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hmul2_vector:
      |.text.hmul2_vector:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;
      |        /*0010*/                   S2R R6, SR_CTAID.X ;
      |        /*0020*/                   MOV R7, 0x4 ;
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0040*/                   S2R R3, SR_TID.X ;
      |        /*0050*/                   IMAD R6, R6, c[0x0][0x0], R3 ;
      |        /*0060*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;
      |        /*0070*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;
      |        /*0080*/                   LDG.E.CONSTANT R2, [R2.64] ;
      |        /*0090*/                   LDG.E.CONSTANT R5, [R4.64] ;
      |        /*00a0*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;
      |        /*00b0*/                   HMUL2 R9, R2, R5 ;
      |        /*00c0*/                   STG.E [R6.64], R9 ;
      |        /*00d0*/                   EXIT ;
      |.L_x_0:
      |        /*00e0*/                   BRA `(.L_x_0);
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

class Hmul2Fixtures extends NvidiaFixtureSuite {
  import Hmul2Fixtures.*

  reproduces("hmul2F32Sm75Hmul2F32Sm75Patched")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.2.78")),
    inputs = Vec(
      "hmul2-f32.cu" -> hmul2F32Sm75Hmul2F32CudaSource
    ),
    script = Vec(
      "nvcc -ccbin g++-11 -arch=sm_75 -cubin hmul2-f32.cu -o hmul2-f32-sm75.cubin",
      """perl -0777 - hmul2-f32-sm75.cubin > hmul2-f32-sm75-patched.cubin <<'PERL'
      |$x=<>;
      |@r=(["20720d00090000000000400000e40f04","32720d02040000200048000000cc0f00"],["20720f000b0000000000400000e40f00","32720f02040000300048000000cc0f00"],["20720908090000000000400000e40f04","3272090204000020004c000000cc0f00"],["20720b080b0000000000400000e40f00","32720b0204000030004c000000cc0f00"]);
      |for $r(@r){$o=pack("H*",$r->[0]);
      |$n=pack("H*",$r->[1]);
      |die "slot mismatch\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$x =~ s/\Q$o\E/$n/;
      |} print $x
      |PERL""".stripMargin,
      "nvdisasm -c -hex hmul2-f32-sm75-patched.cubin > hmul2-f32-sm75-patched.sass"
    ),
    output = "hmul2-f32-sm75-patched.sass",
    expected = hmul2F32Sm75Hmul2F32Sm75Patched
  )

  reproduces("hmul2SelectedSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hmul2-selected-sm89.cu" -> hmul2SelectedSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hmul2-selected-sm89.cu -o hmul2-selected-sm89.cubin",
      "nvdisasm -c -ndf hmul2-selected-sm89.cubin > hmul2-selected-sm89.sass"
    ),
    output = "hmul2-selected-sm89.sass",
    expected = hmul2SelectedSm89
  )

  reproduces("hmul2Sm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hmul2-sm89.cu" -> hmul2Sm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hmul2-sm89.cu -o hmul2-sm89.cubin",
      "nvdisasm -c -ndf hmul2-sm89.cubin > hmul2-sm89.sass"
    ),
    output = "hmul2-sm89.sass",
    expected = hmul2Sm89
  )

}
