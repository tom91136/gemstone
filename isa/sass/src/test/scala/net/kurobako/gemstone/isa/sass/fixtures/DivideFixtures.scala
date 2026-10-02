package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object DivideFixtures {
  val divideSm89CudaSource: String =
    """#include <stdint.h>
      |extern "C" __global__ void divide_probe(int *out, int a, int b) {
      |  out[0] = a / b;
      |  out[1] = a % b;
      |}
      |""".stripMargin

  val divideSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.divide_probe        --------------------------
      |	.section	.text.divide_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	128
      |        .global         divide_probe
      |        .type           divide_probe,@function
      |        .size           divide_probe,(.L_x_1 - divide_probe)
      |        .other          divide_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |divide_probe:
      |.text.divide_probe:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
      |        /*0010*/                   IABS R5, c[0x0][0x16c] ;
      |        /*0020*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0030*/                   I2F.RP R0, R5 ;
      |        /*0040*/                   MUFU.RCP R0, R0 ;
      |        /*0050*/                   IADD3 R2, R0, 0xffffffe, RZ ;
      |        /*0060*/                   F2I.FTZ.U32.TRUNC.NTZ R3, R2 ;
      |        /*0070*/                   MOV R2, RZ ;
      |        /*0080*/                   IMAD.MOV R4, RZ, RZ, -R3 ;
      |        /*0090*/                   IMAD R7, R4, R5, RZ ;
      |        /*00a0*/                   IABS R4, c[0x0][0x168] ;
      |        /*00b0*/                   IMAD.HI.U32 R3, R3, R7, R2 ;
      |        /*00c0*/                   MOV R7, c[0x0][0x168] ;
      |        /*00d0*/                   IMAD.MOV.U32 R2, RZ, RZ, c[0x0][0x160] ;
      |        /*00e0*/                   IMAD.HI.U32 R3, R3, R4, RZ ;
      |        /*00f0*/                   IMAD.MOV R0, RZ, RZ, -R3 ;
      |        /*0100*/                   IMAD R0, R5, R0, R4 ;
      |        /*0110*/                   ISETP.GT.U32.AND P2, PT, R5, R0, PT ;
      |        /*0120*/              @!P2 IMAD.IADD R0, R0, 0x1, -R5 ;
      |        /*0130*/              @!P2 IADD3 R3, R3, 0x1, RZ ;
      |        /*0140*/                   ISETP.NE.AND P2, PT, RZ, c[0x0][0x16c], PT ;
      |        /*0150*/                   ISETP.GE.U32.AND P0, PT, R0, R5, PT ;
      |        /*0160*/                   LOP3.LUT R0, R7, c[0x0][0x16c], RZ, 0x3c, !PT ;
      |        /*0170*/                   ISETP.GE.AND P1, PT, R0, RZ, PT ;
      |        /*0180*/               @P0 IADD3 R3, R3, 0x1, RZ ;
      |        /*0190*/                   IMAD.MOV.U32 R5, RZ, RZ, R3 ;
      |        /*01a0*/                   MOV R3, c[0x0][0x164] ;
      |        /*01b0*/              @!P1 IMAD.MOV R5, RZ, RZ, -R5 ;
      |        /*01c0*/              @!P2 LOP3.LUT R5, RZ, c[0x0][0x16c], RZ, 0x33, !PT ;
      |        /*01d0*/                   IADD3 R0, -R5, RZ, RZ ;
      |        /*01e0*/                   STG.E [R2.64], R5 ;
      |        /*01f0*/                   IMAD R7, R0, c[0x0][0x16c], R7 ;
      |        /*0200*/                   STG.E [R2.64+0x4], R7 ;
      |        /*0210*/                   EXIT ;
      |.L_x_0:
      |        /*0220*/                   BRA `(.L_x_0);
      |        /*0230*/                   NOP;
      |        /*0240*/                   NOP;
      |        /*0250*/                   NOP;
      |        /*0260*/                   NOP;
      |        /*0270*/                   NOP;
      |        /*0280*/                   NOP;
      |        /*0290*/                   NOP;
      |        /*02a0*/                   NOP;
      |        /*02b0*/                   NOP;
      |        /*02c0*/                   NOP;
      |        /*02d0*/                   NOP;
      |        /*02e0*/                   NOP;
      |        /*02f0*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class DivideFixtures extends NvidiaFixtureSuite {
  import DivideFixtures.*

  reproduces("divideSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.cu" -> divideSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 input.cu -o kernel.cubin",
      "nvdisasm -c -ndf kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = divideSm89
  )

}
