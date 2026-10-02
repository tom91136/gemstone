package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object MembarFixtures {
  val membarSm35ProbePtxSource: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry probe(.param .u64 output) {
      |.reg .b64 d;
      |.reg .b32 r;
      |ld.param.u64 d,[output];
      |st.global.u32 [d],7;
      |membar.gl;
      |ld.volatile.global.u32 r,[d];
      |st.global.u32 [d+4],r;
      |ret;
      |}
      |""".stripMargin

  val membarSm35Probe: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=5"
      |	.align	64
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |                                                           /* 0x08b8bcb8a08010c0 */
      |        /*0008*/                   MOV R1, c[0x0][0x44];   /* 0x64c03c00089c0006 */
      |        /*0010*/                   MOV32I R4, 0x7;         /* 0x74000000039fc012 */
      |        /*0018*/                   MOV R2, c[0x0][0x140];  /* 0x64c03c00281c000a */
      |        /*0020*/                   MOV R3, c[0x0][0x144];  /* 0x64c03c00289c000e */
      |        /*0028*/                   ST.E [R2], R4;          /* 0xe4800000001c0810 */
      |        /*0030*/                   MEMBAR.GL;              /* 0x7cc00000001c0402 */
      |        /*0038*/                   CCTL.IVALL [RZ];        /* 0x7b000000001ffc1a */
      |                                                           /* 0x0800000000bc10fc */
      |        /*0048*/                   LD.E.CV R4, [R2];       /* 0xdc800000001c0810 */
      |        /*0050*/                   ST.E [R2+0x4], R4;      /* 0xe4800000021c0810 */
      |        /*0058*/                   EXIT;                   /* 0x18000000001c003c */
      |.L_x_0:
      |        /*0060*/                   BRA `(.L_x_0);          /* 0x12007ffffc1c003c */
      |        /*0068*/                   NOP;                    /* 0x85800000001c3c02 */
      |        /*0070*/                   NOP;                    /* 0x85800000001c3c02 */
      |        /*0078*/                   NOP;                    /* 0x85800000001c3c02 */
      |.L_x_1:
      |""".stripMargin

}

class MembarFixtures extends NvidiaFixtureSuite {
  import MembarFixtures.*

  reproduces("membarSm35Probe")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "input.ptx" -> membarSm35ProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_35 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = membarSm35Probe
  )

}
