package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object CacheInvalidateFixtures {
  val cacheInvalidateSm35CachePtxSource: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry cache_probe(.param .u64 out, .param .u64 input) {
      |.reg .u64 %rd<3>;
      |.reg .u32 %r<3>;
      |ld.param.u64 %rd0,[out];
      |ld.param.u64 %rd1,[input];
      |atom.global.add.u32 %r0,[%rd1],7;
      |ld.global.u32 %r1,[%rd1];
      |st.global.u32 [%rd0],%r0;
      |st.global.u32 [%rd0+4],%r1;
      |ret;
      |}
      |""".stripMargin

  val cacheInvalidateSm35Cache: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.cache_probe         --------------------------
      |	.section	.text.cache_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	64
      |        .global         cache_probe
      |        .type           cache_probe,@function
      |        .size           cache_probe,(.L_x_1 - cache_probe)
      |        .other          cache_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |cache_probe:
      |.text.cache_probe:
      |                                                                         /* 0x08a0109c108010c0 */
      |        /*0008*/                   MOV R1, c[0x0][0x44];                 /* 0x64c03c00089c0006 */
      |        /*0010*/                   VOTE.ANY R0, PT, PT;                  /* 0x86cf1c00001c0002 */
      |        /*0018*/                   MOV R2, c[0x0][0x148];                /* 0x64c03c00291c000a */
      |        /*0020*/                   S2R R3, SR_LANEID;                    /* 0x86400000001c000e */
      |        /*0028*/                   MOV R5, c[0x0][0x144];                /* 0x64c03c00289c0016 */
      |        /*0030*/                   FLO.U32 R8, R0;                       /* 0xe1800000001c0022 */
      |        /*0038*/                   POPC R4, R0, R0;                      /* 0xe0400000001c0012 */
      |                                                                         /* 0x08b810b810ac1080 */
      |        /*0048*/                   ISETP.EQ.U32.AND P0, PT, R8, R3, PT;  /* 0xdb201c00019c201e */
      |        /*0050*/                   IMUL32I R6, R4, 0x7;                  /* 0x2e000000039c101a */
      |        /*0058*/                   MOV R3, c[0x0][0x14c];                /* 0x64c03c00299c000e */
      |        /*0060*/               @P0 ATOM.E.ADD R6, [R2], R6;              /* 0x680800000300081a */
      |        /*0068*/                   S2R R4, SR_LTMASK;                    /* 0x864000001c9c0012 */
      |        /*0070*/                   CCTL.E.IV [R2];                       /* 0x7b800000001c0816 */
      |        /*0078*/                   LOP.AND R4, R4, R0;                   /* 0xe2000000001c1012 */
      |                                                                         /* 0x0880b810f4fc8010 */
      |        /*0088*/                   LD.E R7, [R2];                        /* 0xc4800000001c081c */
      |        /*0090*/                   POPC R9, R4, R4;                      /* 0xe0400000021c1026 */
      |        /*0098*/                   MOV R4, c[0x0][0x140];                /* 0x64c03c00281c0012 */
      |        /*00a0*/                   SHFL.IDX PT, R8, R6, R8, 0x1f;        /* 0x78b803e1041c1822 */
      |        /*00a8*/                   IMAD R0, R9, 0x7, R8;                 /* 0xa1082000039c2401 */
      |        /*00b0*/                   ST.E [R4+0x4], R7;                    /* 0xe4800000021c101c */
      |        /*00b8*/                   ST.E [R4], R0;                        /* 0xe4800000001c1000 */
      |                                                                         /* 0x08000000000000bc */
      |        /*00c8*/                   EXIT;                                 /* 0x18000000001c003c */
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0);                        /* 0x12007ffffc1c003c */
      |        /*00d8*/                   NOP;                                  /* 0x85800000001c3c02 */
      |        /*00e0*/                   NOP;                                  /* 0x85800000001c3c02 */
      |        /*00e8*/                   NOP;                                  /* 0x85800000001c3c02 */
      |        /*00f0*/                   NOP;                                  /* 0x85800000001c3c02 */
      |        /*00f8*/                   NOP;                                  /* 0x85800000001c3c02 */
      |.L_x_1:
      |""".stripMargin

}

class CacheInvalidateFixtures extends NvidiaFixtureSuite {
  import CacheInvalidateFixtures.*

  reproduces("cacheInvalidateSm35Cache")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "input.ptx" -> cacheInvalidateSm35CachePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_35 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = cacheInvalidateSm35Cache
  )

}
