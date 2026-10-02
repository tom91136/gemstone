package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object MultiplySubtractFixtures {
  val multiplySubtractSm35CudaSource: String =
    """extern "C" __global__ void subtract_product(unsigned long long *out, unsigned a, unsigned b, unsigned long long c) {
      | out[0] = (unsigned long long)a * b - c;
      |}
      |""".stripMargin

  val multiplySubtractSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.subtract_product    --------------------------
      |	.section	.text.subtract_product,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=6"
      |	.align	64
      |        .global         subtract_product
      |        .type           subtract_product,@function
      |        .size           subtract_product,(.L_x_1 - subtract_product)
      |        .other          subtract_product,@"STO_CUDA_ENTRY STV_DEFAULT"
      |subtract_product:
      |.text.subtract_product:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   MOV R0, c[0x0][0x14c];
      |        /*0018*/                   MOV R4, c[0x0][0x148];
      |        /*0020*/                   LOP.PASS_B R3, RZ, ~c[0x0][0x154];
      |        /*0028*/                   MOV R2, c[0x0][0x140];
      |        /*0030*/                   IMAD.U32.U32 R4.CC, R0, R4, -c[0x0][0x150];
      |        /*0038*/                   IMAD.U32.U32.HI.X R5, R0, c[0x0][0x148], R3;
      |        /*0048*/                   MOV R3, c[0x0][0x144];
      |        /*0050*/                   ST.E.64 [R2], R4;
      |        /*0058*/                   EXIT;
      |.L_x_0:
      |        /*0060*/                   BRA `(.L_x_0);
      |        /*0068*/                   NOP;
      |        /*0070*/                   NOP;
      |        /*0078*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class MultiplySubtractFixtures extends NvidiaFixtureSuite {
  import MultiplySubtractFixtures.*

  reproduces("multiplySubtractSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "multiply-subtract-sm35.cu" -> multiplySubtractSm35CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_35 multiply-subtract-sm35.cu -o multiply-subtract-sm35.cubin",
      "nvdisasm -c -ndf multiply-subtract-sm35.cubin > multiply-subtract-sm35.sass"
    ),
    output = "multiply-subtract-sm35.sass",
    expected = multiplySubtractSm35
  )

}
