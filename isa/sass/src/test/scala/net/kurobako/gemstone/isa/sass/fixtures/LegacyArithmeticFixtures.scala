package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object LegacyArithmeticFixtures {
  val legacyArithmeticSm35CudaSource: String =
    """#include <stdint.h>
      |extern "C" __global__ void arith_probe(uint64_t *out, uint64_t a, uint64_t b, uint32_t shift) {
      |  out[0] = a + b;
      |  out[1] = a - b;
      |  out[2] = ((int64_t)a) >> shift;
      |  out[3] = a >> shift;
      |  out[4] = ((int64_t)b) >> shift;
      |  out[5] = b >> shift;
      |  out[6] = 0ULL - a;
      |  out[7] = 0ULL - b;
      |}
      |""".stripMargin

  val legacyArithmeticSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.arith_probe         --------------------------
      |	.section	.text.arith_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=24"
      |	.align	64
      |        .global         arith_probe
      |        .type           arith_probe,@function
      |        .size           arith_probe,(.L_x_1 - arith_probe)
      |        .other          arith_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |arith_probe:
      |.text.arith_probe:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   ISUB R20.CC, RZ, c[0x0][0x148];
      |        /*0018*/                   MOV R6, c[0x0][0x148];
      |        /*0020*/                   MOV R15, c[0x0][0x158];
      |        /*0028*/                   MOV R9, c[0x0][0x154];
      |        /*0030*/                   MOV R0, c[0x0][0x14c];
      |        /*0038*/                   MOV R2, c[0x0][0x140];
      |        /*0048*/                   MOV R3, c[0x0][0x144];
      |        /*0050*/                   SHF.R.S64 R4, R6, R15, R0;
      |        /*0058*/                   ISUB.X R21, RZ, c[0x0][0x14c];
      |        /*0060*/                   SHF.R.U64 R6, R6, R15, R0;
      |        /*0068*/                   ISUB R22.CC, RZ, c[0x0][0x150];
      |        /*0070*/                   SHR R5, R0, c[0x0][0x158];
      |        /*0078*/                   SHR.U32 R7, R0, c[0x0][0x158];
      |        /*0088*/                   MOV R0, c[0x0][0x150];
      |        /*0090*/                   ST.E.64 [R2+0x30], R20;
      |        /*0098*/                   SHR R13, R9, c[0x0][0x158];
      |        /*00a0*/                   ISUB.X R23, RZ, c[0x0][0x154];
      |        /*00a8*/                   SHF.R.S64 R12, R0, R15, R9;
      |        /*00b0*/                   ST.E.64 [R2+0x10], R4;
      |        /*00b8*/                   SHF.R.U64 R14, R0, R15, R9;
      |        /*00c8*/                   SHR.U32 R15, R9, c[0x0][0x158];
      |        /*00d0*/                   IADD R4.CC, R0, c[0x0][0x148];
      |        /*00d8*/                   ST.E.64 [R2+0x18], R6;
      |        /*00e0*/                   IADD.X R5, R9, c[0x0][0x14c];
      |        /*00e8*/                   ST.E.64 [R2+0x20], R12;
      |        /*00f0*/                   ISUB R6.CC, c[0x0][0x148], R0;
      |        /*00f8*/                   ISUB.X R7, c[0x0][0x14c], R9;
      |        /*0108*/                   ST.E.64 [R2+0x28], R14;
      |        /*0110*/                   ST.E.64 [R2+0x38], R22;
      |        /*0118*/                   ST.E.64 [R2], R4;
      |        /*0120*/                   ST.E.64 [R2+0x8], R6;
      |        /*0128*/                   EXIT;
      |.L_x_0:
      |        /*0130*/                   BRA `(.L_x_0);
      |        /*0138*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class LegacyArithmeticFixtures extends NvidiaFixtureSuite {
  import LegacyArithmeticFixtures.*

  reproduces("legacyArithmeticSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "legacy-arithmetic-sm35.cu" -> legacyArithmeticSm35CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_35 legacy-arithmetic-sm35.cu -o legacy-arithmetic-sm35.cubin",
      "nvdisasm -c -ndf legacy-arithmetic-sm35.cubin > legacy-arithmetic-sm35.sass"
    ),
    output = "legacy-arithmetic-sm35.sass",
    expected = legacyArithmeticSm35
  )

}
