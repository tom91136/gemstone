package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object IsetExtendedFixtures {
  val isetExtendedSm35IsetPtxSource: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry iset_probe(.param .u64 out, .param .u64 a, .param .u64 b) {
      |.reg .u64 %rd<4>;
      |.reg .u32 %r<6>;
      |ld.param.u64 %rd0,[out];
      |ld.param.u64 %rd1,[a];
      |ld.param.u64 %rd2,[b];
      |set.lt.s32.s64 %r0,%rd1,%rd2;
      |set.lt.u32.u64 %r1,%rd1,%rd2;
      |set.le.s32.s64 %r2,%rd1,%rd2;
      |set.gt.s32.s64 %r3,%rd1,%rd2;
      |set.ge.s32.s64 %r4,%rd1,%rd2;
      |st.global.u32 [%rd0],%r0;
      |st.global.u32 [%rd0+4],%r1;
      |st.global.u32 [%rd0+8],%r2;
      |st.global.u32 [%rd0+12],%r3;
      |st.global.u32 [%rd0+16],%r4;
      |ret;
      |}
      |""".stripMargin

  val isetExtendedSm35Iset: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.iset_probe          --------------------------
      |	.section	.text.iset_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	64
      |        .global         iset_probe
      |        .type           iset_probe,@function
      |        .size           iset_probe,(.L_x_1 - iset_probe)
      |        .other          iset_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |iset_probe:
      |.text.iset_probe:
      |                                                                                 /* 0x08a0b09c108010c0 */
      |        /*0008*/                   MOV R1, c[0x0][0x44];                         /* 0x64c03c00089c0006 */
      |        /*0010*/                   MOV R0, c[0x0][0x148];                        /* 0x64c03c00291c0002 */
      |        /*0018*/                   MOV R9, c[0x0][0x14c];                        /* 0x64c03c00299c0026 */
      |        /*0020*/                   MOV R2, c[0x0][0x140];                        /* 0x64c03c00281c000a */
      |        /*0028*/                   MOV R3, c[0x0][0x144];                        /* 0x64c03c00289c000e */
      |        /*0030*/                   ISUB RZ.CC, R0, c[0x0][0x150];                /* 0x608c00002a1c03fe */
      |        /*0038*/                   ISET.LT.X.AND R4, R9, c[0x0][0x154], PT;      /* 0x5a985c002a9c2412 */
      |                                                                                 /* 0x08b8b8ac80808010 */
      |        /*0048*/                   ISET.LT.U32.X.AND R5, R9, c[0x0][0x154], PT;  /* 0x5a905c002a9c2416 */
      |        /*0050*/                   ST.E [R2], R4;                                /* 0xe4800000001c0810 */
      |        /*0058*/                   ISET.LE.X.AND R6, R9, c[0x0][0x154], PT;      /* 0x5ab85c002a9c241a */
      |        /*0060*/                   ISET.GT.X.AND R7, R9, c[0x0][0x154], PT;      /* 0x5ac85c002a9c241e */
      |        /*0068*/                   ISET.GE.X.AND R9, R9, c[0x0][0x154], PT;      /* 0x5ae85c002a9c2426 */
      |        /*0070*/                   ST.E [R2+0x4], R5;                            /* 0xe4800000021c0814 */
      |        /*0078*/                   ST.E [R2+0x8], R6;                            /* 0xe4800000041c0818 */
      |                                                                                 /* 0x0800000000bc10b8 */
      |        /*0088*/                   ST.E [R2+0xc], R7;                            /* 0xe4800000061c081c */
      |        /*0090*/                   ST.E [R2+0x10], R9;                           /* 0xe4800000081c0824 */
      |        /*0098*/                   EXIT;                                         /* 0x18000000001c003c */
      |.L_x_0:
      |        /*00a0*/                   BRA `(.L_x_0);                                /* 0x12007ffffc1c003c */
      |        /*00a8*/                   NOP;                                          /* 0x85800000001c3c02 */
      |        /*00b0*/                   NOP;                                          /* 0x85800000001c3c02 */
      |        /*00b8*/                   NOP;                                          /* 0x85800000001c3c02 */
      |.L_x_1:
      |""".stripMargin

}

class IsetExtendedFixtures extends NvidiaFixtureSuite {
  import IsetExtendedFixtures.*

  reproduces("isetExtendedSm35Iset")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "iset.ptx" -> isetExtendedSm35IsetPtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_35 iset.ptx -o iset.cubin",
      "nvdisasm -c -ndf -hex iset.cubin > iset.sass"
    ),
    output = "iset.sass",
    expected = isetExtendedSm35Iset
  )

}
