package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object AtomicAddFixtures {
  val atomicAddSm86CudaSource: String =
    """extern "C" __global__ void atomic_add(unsigned *counter, unsigned *previous) {
      |  previous[blockIdx.x * blockDim.x + threadIdx.x] = atomicAdd(counter, blockIdx.x + 1);
      |}
      |""".stripMargin

  val atomicAddSm86: String =
    """	.target	sm_86
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.atomic_add          --------------------------
      |	.section	.text.atomic_add,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=14"
      |	.align	128
      |        .global         atomic_add
      |        .type           atomic_add,@function
      |        .size           atomic_add,(.L_x_1 - atomic_add)
      |        .other          atomic_add,@"STO_CUDA_ENTRY STV_DEFAULT"
      |atomic_add:
      |.text.atomic_add:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;
      |        /*0010*/                   S2R R3, SR_LANEID ;
      |        /*0020*/                   VOTEU.ANY UR6, UPT, PT ;
      |        /*0030*/                   MOV R2, c[0x0][0x160] ;
      |        /*0040*/                   FLO.U32 R6, UR6 ;
      |        /*0050*/                   S2R R4, SR_CTAID.X ;
      |        /*0060*/                   UPOPC UR4, UR6 ;
      |        /*0070*/                   ISETP.EQ.U32.AND P0, PT, R6, R3, PT ;
      |        /*0080*/                   MOV R3, c[0x0][0x164] ;
      |        /*0090*/                   IADD3 R0, R4, 0x1, RZ ;
      |        /*00a0*/                   IMAD R9, R0, UR4, RZ ;
      |        /*00b0*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*00c0*/               @P0 ATOMG.E.ADD.STRONG.GPU PT, R3, [R2.64], R9 ;
      |        /*00d0*/                   MOV R11, 0x4 ;
      |        /*00e0*/                   S2R R8, SR_LTMASK ;
      |        /*00f0*/                   S2R R5, SR_TID.X ;
      |        /*0100*/                   LOP3.LUT R8, R8, UR6, RZ, 0xc0, !PT ;
      |        /*0110*/                   IMAD R4, R4, c[0x0][0x0], R5 ;
      |        /*0120*/                   POPC R8, R8 ;
      |        /*0130*/                   IMAD.WIDE.U32 R4, R4, R11, c[0x0][0x168] ;
      |        /*0140*/                   SHFL.IDX PT, R7, R3, R6, 0x1f ;
      |        /*0150*/                   IMAD R7, R0, R8, R7 ;
      |        /*0160*/                   STG.E [R4.64], R7 ;
      |        /*0170*/                   EXIT ;
      |.L_x_0:
      |        /*0180*/                   BRA `(.L_x_0);
      |        /*0190*/                   NOP;
      |        /*01a0*/                   NOP;
      |        /*01b0*/                   NOP;
      |        /*01c0*/                   NOP;
      |        /*01d0*/                   NOP;
      |        /*01e0*/                   NOP;
      |        /*01f0*/                   NOP;
      |        /*0200*/                   NOP;
      |        /*0210*/                   NOP;
      |        /*0220*/                   NOP;
      |        /*0230*/                   NOP;
      |        /*0240*/                   NOP;
      |        /*0250*/                   NOP;
      |        /*0260*/                   NOP;
      |        /*0270*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class AtomicAddFixtures extends NvidiaFixtureSuite {
  import AtomicAddFixtures.*

  reproduces("atomicAddSm86")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.cu" -> atomicAddSm86CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_86 input.cu -o kernel.cubin",
      "nvdisasm -c -ndf kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = atomicAddSm86
  )

}
