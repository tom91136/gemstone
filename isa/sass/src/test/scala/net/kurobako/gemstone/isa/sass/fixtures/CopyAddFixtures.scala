package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object CopyAddFixtures {
  val copyAddSource: String =
    """.version 8.5
      |.target sm_89
      |.address_size 64
      |
      |.visible .entry copy_add(
      |    .param .u64 copy_add_param_0,
      |    .param .u64 copy_add_param_1
      |)
      |{
      |    .reg .b32 %r<3>;
      |    .reg .b64 %rd<5>;
      |
      |    ld.param.u64 %rd1, [copy_add_param_0];
      |    ld.param.u64 %rd2, [copy_add_param_1];
      |    mov.u32 %r1, %tid.x;
      |    mul.wide.u32 %rd3, %r1, 4;
      |    add.s64 %rd4, %rd2, %rd3;
      |    ld.global.u32 %r2, [%rd4];
      |    add.u32 %r2, %r2, 1;
      |    add.s64 %rd4, %rd1, %rd3;
      |    st.global.u32 [%rd4], %r2;
      |    ret;
      |}
      |""".stripMargin

  val copyAdd: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.copy_add            --------------------------
      |	.section	.text.copy_add,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	128
      |        .global         copy_add
      |        .type           copy_add,@function
      |        .size           copy_add,(.L_x_1 - copy_add)
      |        .other          copy_add,@"STO_CUDA_ENTRY STV_DEFAULT"
      |copy_add:
      |.text.copy_add:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
      |                                                                              /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R4, SR_TID.X ;                         /* 0x0000000000047919 */
      |                                                                              /* 0x000e220000002100 */
      |        /*0020*/                   MOV R5, 0x4 ;                              /* 0x0000000400057802 */
      |                                                                              /* 0x000fe20000000f00 */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;               /* 0x0000460000047ab9 */
      |                                                                              /* 0x000fc80000000a00 */
      |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R5, c[0x0][0x168] ;  /* 0x00005a0004027625 */
      |                                                                              /* 0x001fcc00078e0005 */
      |        /*0050*/                   LDG.E R2, [R2.64] ;                        /* 0x0000000402027981 */
      |                                                                              /* 0x000ea2000c1e1900 */
      |        /*0060*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;  /* 0x0000580004047625 */
      |                                                                              /* 0x000fe200078e0005 */
      |        /*0070*/                   IADD3 R7, R2, 0x1, RZ ;                    /* 0x0000000102077810 */
      |                                                                              /* 0x004fca0007ffe0ff */
      |        /*0080*/                   STG.E [R4.64], R7 ;                        /* 0x0000000704007986 */
      |                                                                              /* 0x000fe2000c101904 */
      |        /*0090*/                   EXIT ;                                     /* 0x000000000000794d */
      |                                                                              /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00a0*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |        /*00b0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00c0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00d0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0100*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0110*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

}

class CopyAddFixtures extends NvidiaFixtureSuite {
  import CopyAddFixtures.*

  reproduces("copyAdd")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "copy_add.ptx" -> copyAddSource
    ),
    script = Vec(
      "ptxas -arch=sm_89 -O3 copy_add.ptx -o copy_add.cubin",
      "nvdisasm -c -ndf -hex copy_add.cubin > copy_add.sass"
    ),
    output = "copy_add.sass",
    expected = copyAdd
  )

}
