package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object BitfieldFixtures {
  val bitfieldSm35CudaSource: String =
    """#include <stdint.h>
      |extern "C" __global__ void bitfield_probe(uint32_t *out, uint32_t x, uint32_t base, uint32_t insert, uint32_t pos, uint32_t width) {
      |  uint32_t u, rev, r, f; int32_t s;
      |  asm volatile("bfe.u32 %0,%1,%2,%3;" : "=r"(u) : "r"(x), "r"(pos), "r"(width));
      |  asm volatile("bfe.s32 %0,%1,%2,%3;" : "=r"(s) : "r"((int32_t)x), "r"(pos), "r"(width));
      |  asm volatile("bfi.b32 %0,%1,%2,%3,%4;" : "=r"(f) : "r"(insert), "r"(base), "r"(pos), "r"(width));
      |  asm volatile("brev.b32 %0,%2; bfe.u32 %1,%0,0,32;" : "=r"(rev), "=r"(r) : "r"(x));
      |  out[0]=u;out[1]=(uint32_t)s;out[2]=f;out[3]=r;
      |}
      |""".stripMargin

  val bitfieldSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.bitfield_probe      --------------------------
      |	.section	.text.bitfield_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	64
      |        .global         bitfield_probe
      |        .type           bitfield_probe,@function
      |        .size           bitfield_probe,(.L_x_1 - bitfield_probe)
      |        .other          bitfield_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |bitfield_probe:
      |.text.bitfield_probe:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   MOV R0, c[0x0][0x158];
      |        /*0018*/                   MOV32I R4, 0x808;
      |        /*0020*/                   MOV R5, c[0x0][0x148];
      |        /*0028*/                   MOV R2, c[0x0][0x140];
      |        /*0030*/                   MOV R6, c[0x0][0x150];
      |        /*0038*/                   BFI R0, R0, R4, c[0x0][0x154];
      |        /*0048*/                   MOV R3, c[0x0][0x144];
      |        /*0050*/                   BFE.U32.BREV R7, R5, 0x2000;
      |        /*0058*/                   BFE.U32 R4, R5, R0;
      |        /*0060*/                   ST.E [R2+0xc], R7;
      |        /*0068*/                   BFE R5, R5, R0;
      |        /*0070*/                   BFI R6, R6, R0, c[0x0][0x14c];
      |        /*0078*/                   ST.E [R2], R4;
      |        /*0088*/                   ST.E [R2+0x4], R5;
      |        /*0090*/                   ST.E [R2+0x8], R6;
      |        /*0098*/                   EXIT;
      |.L_x_0:
      |        /*00a0*/                   BRA `(.L_x_0);
      |        /*00a8*/                   NOP;
      |        /*00b0*/                   NOP;
      |        /*00b8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class BitfieldFixtures extends NvidiaFixtureSuite {
  import BitfieldFixtures.*

  reproduces("bitfieldSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "input.cu" -> bitfieldSm35CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_35 input.cu -o kernel.cubin",
      "nvdisasm -c -ndf kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = bitfieldSm35
  )

}
