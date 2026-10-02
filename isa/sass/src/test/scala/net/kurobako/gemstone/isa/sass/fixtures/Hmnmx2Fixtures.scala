package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object Hmnmx2Fixtures {
  val hmnmx2Sm80Hmnmx2Sm80CudaSource: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |
      |extern "C" __global__ void hmnmx2_select_probe(
      |    const uint32_t *left, const uint32_t *right, uint32_t *output) {
      |  const unsigned index = threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(&left[index]);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(&right[index]);
      |  const __half minimum = __hmin(__low2half(a), __low2half(b));
      |  const __half maximum = __hmax(__low2half(a), __low2half(b));
      |  output[2 * index] = __half_as_ushort(minimum);
      |  output[2 * index + 1] = __half_as_ushort(maximum);
      |}
      |
      |extern "C" __global__ void hmnmx2_alias_probe(uint32_t *left, const uint32_t *right) {
      |  const unsigned index = threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(&left[index]);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(&right[index]);
      |  const __half maximum = __hmax(__low2half(a), __low2half(b));
      |  left[index] = (left[index] & 0xffff0000u) | __half_as_ushort(maximum);
      |}
      |
      |extern "C" __global__ void hmnmx2_mixed_probe(
      |    const uint32_t *left, const uint32_t *right, uint32_t *output) {
      |  const unsigned index = threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(&left[index]);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(&right[index]);
      |  const __half low = __low2half(b);
      |  const __half2 duplicated = __halves2half2(low, low);
      |  const __half2 maximum = __hmax2(a, duplicated);
      |  output[index] = *reinterpret_cast<const uint32_t *>(&maximum);
      |}
      |
      |extern "C" __global__ void hmnmx2_mixed_alias_probe(uint32_t *left, const uint32_t *right) {
      |  const unsigned index = threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(&left[index]);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(&right[index]);
      |  const __half low = __low2half(b);
      |  const __half2 duplicated = __halves2half2(low, low);
      |  const __half2 maximum = __hmax2(a, duplicated);
      |  left[index] = *reinterpret_cast<const uint32_t *>(&maximum);
      |}
      |""".stripMargin

  val hmnmx2Sm80Hmnmx2MixedAliasPatchedSm80: String =
    """	.target	sm_80
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmnmx2_mixed_alias_probe --------------------------
      |	.section	.text.hmnmx2_mixed_alias_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	128
      |        .global         hmnmx2_mixed_alias_probe
      |        .type           hmnmx2_mixed_alias_probe,@function
      |        .size           hmnmx2_mixed_alias_probe,(.L_x_1 - hmnmx2_mixed_alias_probe)
      |        .other          hmnmx2_mixed_alias_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hmnmx2_mixed_alias_probe:
      |.text.hmnmx2_mixed_alias_probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                             /* 0x00000a0000017a02 */
      |                                                                                      /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R2, SR_TID.X ;                                 /* 0x0000000000027919 */
      |                                                                                      /* 0x000e220000002100 */
      |        /*0020*/                   HFMA2.MMA R3, -RZ, RZ, 0, 2.384185791015625e-07 ;  /* 0x00000004ff037435 */
      |                                                                                      /* 0x000fe200000001ff */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                       /* 0x0000460000047ab9 */
      |                                                                                      /* 0x000fd20000000a00 */
      |        /*0040*/                   IMAD.WIDE.U32 R4, R2, R3, c[0x0][0x168] ;          /* 0x00005a0002047625 */
      |                                                                                      /* 0x001fc800078e0003 */
      |        /*0050*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;          /* 0x0000580002027625 */
      |                                                                                      /* 0x000fe400078e0003 */
      |        /*0060*/                   LDG.E.U16.CONSTANT R5, [R4.64] ;                   /* 0x0000000404057981 */
      |                                                                                      /* 0x000ea8000c1e9500 */
      |        /*0070*/                   LDG.E R0, [R2.64] ;                                /* 0x0000000402007981 */
      |                                                                                      /* 0x000ea4000c1e1900 */
      |        /*0080*/                   HMNMX2 R0, R0, R5.H0_H0, !PT ;                     /* 0x2000000500007240 */
      |                                                                                      /* 0x004fca0007800000 */
      |        /*0090*/                   STG.E [R2.64], R0 ;                                /* 0x0000000002007986 */
      |                                                                                      /* 0x000fe2000c101904 */
      |        /*00a0*/                   EXIT ;                                             /* 0x000000000000794d */
      |                                                                                      /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00b0*/                   BRA `(.L_x_0);                                     /* 0xfffffff000007947 */
      |                                                                                      /* 0x000fc0000383ffff */
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

  val hmnmx2Sm80Hmnmx2MixedSm80: String =
    """	.target	sm_80
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmnmx2_mixed_probe  --------------------------
      |	.section	.text.hmnmx2_mixed_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         hmnmx2_mixed_probe
      |        .type           hmnmx2_mixed_probe,@function
      |        .size           hmnmx2_mixed_probe,(.L_x_1 - hmnmx2_mixed_probe)
      |        .other          hmnmx2_mixed_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hmnmx2_mixed_probe:
      |.text.hmnmx2_mixed_probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
      |                                                                                          /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
      |                                                                                          /* 0x000e220000002100 */
      |        /*0020*/                   HFMA2.MMA R7, -RZ, RZ, 0, 2.384185791015625e-07 ;      /* 0x00000004ff077435 */
      |                                                                                          /* 0x000fe200000001ff */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
      |                                                                                          /* 0x000fd20000000a00 */
      |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x160] ;              /* 0x0000580006027625 */
      |                                                                                          /* 0x001fc800078e0007 */
      |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x168] ;  /* 0x00005a0006047625 */
      |                                                                                          /* 0x0c0fe400078e0007 */
      |        /*0060*/                   LDG.E.CONSTANT R2, [R2.64] ;                           /* 0x0000000402027981 */
      |                                                                                          /* 0x000ea8000c1e9900 */
      |        /*0070*/                   LDG.E.U16.CONSTANT R5, [R4.64] ;                       /* 0x0000000404057981 */
      |                                                                                          /* 0x000ea2000c1e9500 */
      |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x170] ;              /* 0x00005c0006067625 */
      |                                                                                          /* 0x000fe200078e0007 */
      |        /*0090*/                   HMNMX2 R9, R2, R5.H0_H0, !PT ;                         /* 0x2000000502097240 */
      |                                                                                          /* 0x004fca0007800000 */
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

  val hmnmx2Sm80Hmnmx2SelectSm80: String =
    """	.target	sm_80
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmnmx2_select_probe --------------------------
      |	.section	.text.hmnmx2_select_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=14"
      |	.align	128
      |        .global         hmnmx2_select_probe
      |        .type           hmnmx2_select_probe,@function
      |        .size           hmnmx2_select_probe,(.L_x_1 - hmnmx2_select_probe)
      |        .other          hmnmx2_select_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |hmnmx2_select_probe:
      |.text.hmnmx2_select_probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                             /* 0x00000a0000017a02 */
      |                                                                                      /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R6, SR_TID.X ;                                 /* 0x0000000000067919 */
      |                                                                                      /* 0x000e220000002100 */
      |        /*0020*/                   HFMA2.MMA R7, -RZ, RZ, 0, 2.384185791015625e-07 ;  /* 0x00000004ff077435 */
      |                                                                                      /* 0x000fe200000001ff */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                       /* 0x0000460000047ab9 */
      |                                                                                      /* 0x000fd20000000a00 */
      |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x160] ;          /* 0x0000580006027625 */
      |                                                                                      /* 0x001fc800078e0007 */
      |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7, c[0x0][0x168] ;    /* 0x00005a0006047625 */
      |                                                                                      /* 0x040fe400078e0007 */
      |        /*0060*/                   LDG.E.U16.CONSTANT R2, [R2.64] ;                   /* 0x0000000402027981 */
      |                                                                                      /* 0x000ea8000c1e9500 */
      |        /*0070*/                   LDG.E.U16.CONSTANT R5, [R4.64] ;                   /* 0x0000000404057981 */
      |                                                                                      /* 0x000ea2000c1e9500 */
      |        /*0080*/                   SHF.L.U32 R6, R6, 0x1, RZ ;                        /* 0x0000000106067819 */
      |                                                                                      /* 0x000fca00000006ff */
      |        /*0090*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x170] ;          /* 0x00005c0006067625 */
      |                                                                                      /* 0x000fe200078e0007 */
      |        /*00a0*/                   HMNMX2 R0, R2.reuse.H0_H0, R5.reuse.H0_H0, PT ;    /* 0x2000000502007240 */
      |                                                                                      /* 0x0c4fe40003800800 */
      |        /*00b0*/                   HMNMX2 R8, R2.H0_H0, R5.H0_H0, !PT ;               /* 0x2000000502087240 */
      |                                                                                      /* 0x000fc60007800800 */
      |        /*00c0*/                   LOP3.LUT R9, R0, 0xffff, RZ, 0xc0, !PT ;           /* 0x0000ffff00097812 */
      |                                                                                      /* 0x000fe400078ec0ff */
      |        /*00d0*/                   LOP3.LUT R11, R8, 0xffff, RZ, 0xc0, !PT ;          /* 0x0000ffff080b7812 */
      |                                                                                      /* 0x000fc600078ec0ff */
      |        /*00e0*/                   STG.E [R6.64], R9 ;                                /* 0x0000000906007986 */
      |                                                                                      /* 0x000fe8000c101904 */
      |        /*00f0*/                   STG.E [R6.64+0x4], R11 ;                           /* 0x0000040b06007986 */
      |                                                                                      /* 0x000fe2000c101904 */
      |        /*0100*/                   EXIT ;                                             /* 0x000000000000794d */
      |                                                                                      /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0110*/                   BRA `(.L_x_0);                                     /* 0xfffffff000007947 */
      |                                                                                      /* 0x000fc0000383ffff */
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
      |        /*0180*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0190*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*01a0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

}

class Hmnmx2Fixtures extends NvidiaFixtureSuite {
  import Hmnmx2Fixtures.*

  reproduces("hmnmx2Sm80Hmnmx2MixedAliasPatchedSm80")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hmnmx2-sm80.cu" -> hmnmx2Sm80Hmnmx2Sm80CudaSource
    ),
    script = Vec(
      "nvcc -ccbin g++-11 -arch=compute_80 -code=sm_80 -lineinfo -cubin hmnmx2-sm80.cu -o hmnmx2-sm80.cubin",
      """perl -0777 - hmnmx2-sm80.cubin > hmnmx2-mixed-alias-sm80.cubin <<'PERL'
      |$x=<>;
      |$a=pack("H*","4072070005000020");
      |$b=pack("H*","8679000207000000");
      |die "instruction/store count mismatch\n" unless index($x,$a)>=0 && index($x,$a)==rindex($x,$a) && index($x,$b)>=0 && index($x,$b)==rindex($x,$b);
      |$x =~ s/\Q$a\E/pack("H*","4072000005000020")/e;
      |$x =~ s/\Q$b\E/pack("H*","8679000200000000")/e;
      |print $x
      |PERL""".stripMargin,
      """fun_index=$(readelf -Ws hmnmx2-mixed-alias-sm80.cubin | awk '$NF=="hmnmx2_mixed_alias_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$fun_index" hmnmx2-mixed-alias-sm80.cubin > hmnmx2-mixed-alias-patched-sm80.sass""".stripMargin
    ),
    output = "hmnmx2-mixed-alias-patched-sm80.sass",
    expected = hmnmx2Sm80Hmnmx2MixedAliasPatchedSm80
  )

  reproduces("hmnmx2Sm80Hmnmx2MixedSm80")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hmnmx2-sm80.cu" -> hmnmx2Sm80Hmnmx2Sm80CudaSource
    ),
    script = Vec(
      "nvcc -ccbin g++-11 -arch=compute_80 -code=sm_80 -lineinfo -cubin hmnmx2-sm80.cu -o hmnmx2-sm80.cubin",
      """fun_index=$(readelf -Ws hmnmx2-sm80.cubin | awk '$NF=="hmnmx2_mixed_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$fun_index" hmnmx2-sm80.cubin > hmnmx2-mixed-sm80.sass""".stripMargin
    ),
    output = "hmnmx2-mixed-sm80.sass",
    expected = hmnmx2Sm80Hmnmx2MixedSm80
  )

  reproduces("hmnmx2Sm80Hmnmx2SelectSm80")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hmnmx2-sm80.cu" -> hmnmx2Sm80Hmnmx2Sm80CudaSource
    ),
    script = Vec(
      "nvcc -ccbin g++-11 -arch=compute_80 -code=sm_80 -lineinfo -cubin hmnmx2-sm80.cu -o hmnmx2-sm80.cubin",
      """fun_index=$(readelf -Ws hmnmx2-sm80.cubin | awk '$NF=="hmnmx2_select_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$fun_index" hmnmx2-sm80.cubin > hmnmx2-select-sm80.sass""".stripMargin
    ),
    output = "hmnmx2-select-sm80.sass",
    expected = hmnmx2Sm80Hmnmx2SelectSm80
  )

}
