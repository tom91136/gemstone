package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object MinMaxFixtures {
  val minmaxSm35CudaSource: String =
    """#include <stdint.h>
      |extern "C" __global__ void minmax_probe(uint64_t *out, uint64_t a, uint64_t b) {
      |  uint64_t mn = a < b ? a : b;
      |  uint64_t mx = a > b ? a : b;
      |  out[0] = mn;
      |  out[1] = mx;
      |}
      |extern "C" __global__ void minmax_signed_probe(int64_t *out, int64_t a, int64_t b) {
      |  int64_t mn = a < b ? a : b;
      |  int64_t mx = a > b ? a : b;
      |  out[0] = mn;
      |  out[1] = mx;
      |}
      |""".stripMargin

  val minmaxSignedSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.minmax_signed_probe --------------------------
      |	.section	.text.minmax_signed_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	64
      |        .global         minmax_signed_probe
      |        .type           minmax_signed_probe,@function
      |        .size           minmax_signed_probe,(.L_x_1 - minmax_signed_probe)
      |        .other          minmax_signed_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |minmax_signed_probe:
      |.text.minmax_signed_probe:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   MOV R6, c[0x0][0x14c];
      |        /*0018*/                   MOV R0, c[0x0][0x148];
      |        /*0020*/                   MOV R2, c[0x0][0x140];
      |        /*0028*/                   MOV R3, c[0x0][0x144];
      |        /*0030*/                   IMNMX.XHI R5.CC, R6, c[0x0][0x154], PT;
      |        /*0038*/                   IMNMX.U32.XLO R4, R0, c[0x0][0x150], PT;
      |        /*0048*/                   IMNMX.XHI R7.CC, R6, c[0x0][0x154], !PT;
      |        /*0050*/                   ST.E.64 [R2], R4;
      |        /*0058*/                   IMNMX.U32.XLO R6, R0, c[0x0][0x150], !PT;
      |        /*0060*/                   ST.E.64 [R2+0x8], R6;
      |        /*0068*/                   EXIT;
      |.L_x_0:
      |        /*0070*/                   BRA `(.L_x_0);
      |        /*0078*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val minmaxSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.minmax_probe        --------------------------
      |	.section	.text.minmax_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	64
      |        .global         minmax_probe
      |        .type           minmax_probe,@function
      |        .size           minmax_probe,(.L_x_1 - minmax_probe)
      |        .other          minmax_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |minmax_probe:
      |.text.minmax_probe:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   MOV R6, c[0x0][0x14c];
      |        /*0018*/                   MOV R0, c[0x0][0x148];
      |        /*0020*/                   MOV R2, c[0x0][0x140];
      |        /*0028*/                   MOV R3, c[0x0][0x144];
      |        /*0030*/                   IMNMX.U32.XHI R5.CC, R6, c[0x0][0x154], PT;
      |        /*0038*/                   IMNMX.U32.XLO R4, R0, c[0x0][0x150], PT;
      |        /*0048*/                   IMNMX.U32.XHI R7.CC, R6, c[0x0][0x154], !PT;
      |        /*0050*/                   ST.E.64 [R2], R4;
      |        /*0058*/                   IMNMX.U32.XLO R6, R0, c[0x0][0x150], !PT;
      |        /*0060*/                   ST.E.64 [R2+0x8], R6;
      |        /*0068*/                   EXIT;
      |.L_x_0:
      |        /*0070*/                   BRA `(.L_x_0);
      |        /*0078*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class MinMaxFixtures extends NvidiaFixtureSuite {
  import MinMaxFixtures.*

  reproduces("minmaxSignedSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "minmax-sm35.cu" -> minmaxSm35CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_35 minmax-sm35.cu -o minmax-sm35.cubin",
      "symbol_index=$(readelf -sW minmax-sm35.cubin | awk -v name=\"minmax_signed_probe\" '$NF == name { sub(\":\", \"\", $1); print $1; exit }')",
      "test -n \"$symbol_index\"",
      "nvdisasm -c -ndf -fun \"$symbol_index\" minmax-sm35.cubin > minmax-signed-sm35.sass"
    ),
    output = "minmax-signed-sm35.sass",
    expected = minmaxSignedSm35
  )

  reproduces("minmaxSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "minmax-sm35.cu" -> minmaxSm35CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_35 minmax-sm35.cu -o minmax-sm35.cubin",
      "symbol_index=$(readelf -sW minmax-sm35.cubin | awk -v name=\"minmax_probe\" '$NF == name { sub(\":\", \"\", $1); print $1; exit }')",
      "test -n \"$symbol_index\"",
      "nvdisasm -c -ndf -fun \"$symbol_index\" minmax-sm35.cubin > minmax-sm35.sass"
    ),
    output = "minmax-sm35.sass",
    expected = minmaxSm35
  )

}
