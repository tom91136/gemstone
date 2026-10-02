package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object ExtremaFixtures {
  val extremaSm89PtxSource: String =
    """.version 9.2
      |.target sm_89
      |.address_size 64
      |.visible .entry extrema(.param .u64 param_a, .param .u64 param_b, .param .u64 param_max, .param .u64 param_min) {
      |.reg .pred %p;
      |.reg .b32 %thread;
      |.reg .b64 %a, %b, %max, %min, %pa, %pb, %pmax, %pmin, %offset;
      |.reg .b64 %tmp;
      |ld.param.u64 %pa, [param_a];
      |ld.param.u64 %pb, [param_b];
      |ld.param.u64 %pmax, [param_max];
      |ld.param.u64 %pmin, [param_min];
      |mov.u32 %thread, %tid.x;
      |setp.ge.u32 %p, %thread, 9;
      |@%p ret;
      |cvt.u64.u32 %offset, %thread;
      |shl.b64 %offset, %offset, 3;
      |add.u64 %pa, %pa, %offset;
      |add.u64 %pb, %pb, %offset;
      |add.u64 %pmax, %pmax, %offset;
      |add.u64 %pmin, %pmin, %offset;
      |ld.global.b64 %a, [%pa];
      |ld.global.b64 %b, [%pb];
      |max.f64 %max, %a, %b;
      |min.f64 %min, %a, %b;
      |st.global.b64 [%pmax], %max;
      |st.global.b64 [%pmin], %min;
      |ret;
      |}
      |""".stripMargin

  val extremaSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.extrema             --------------------------
      |	.section	.text.extrema,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=17"
      |	.align	128
      |        .global         extrema
      |        .type           extrema,@function
      |        .size           extrema,(.L_x_1 - extrema)
      |        .other          extrema,@"STO_CUDA_ENTRY STV_DEFAULT"
      |extrema:
      |.text.extrema:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0020*/                   ISETP.GE.U32.AND P0, PT, R0, 0x9, PT ;
      |        /*0030*/               @P0 EXIT ;
      |        /*0040*/                   IMAD.SHL.U32 R8, R0, 0x8, RZ ;
      |        /*0050*/                   SHF.R.U32.HI R0, RZ, 0x1d, R0 ;
      |        /*0060*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0070*/                   IADD3 R2, P0, R8.reuse, c[0x0][0x160], RZ ;
      |        /*0080*/                   IADD3 R4, P1, R8, c[0x0][0x168], RZ ;
      |        /*0090*/                   IADD3.X R3, R0.reuse, c[0x0][0x164], RZ, P0, !PT ;
      |        /*00a0*/                   IADD3.X R5, R0, c[0x0][0x16c], RZ, P1, !PT ;
      |        /*00b0*/                   LDG.E.64 R2, [R2.64] ;
      |        /*00c0*/                   LDG.E.64 R4, [R4.64] ;
      |        /*00d0*/                   IMAD.MOV.U32 R11, RZ, RZ, R2.reuse ;
      |        /*00e0*/                   IMAD.MOV.U32 R6, RZ, RZ, R2 ;
      |        /*00f0*/                   DSETP.MIN.AND P4, P5, R2.reuse, R4.reuse, PT ;
      |        /*0100*/                   IMAD.MOV.U32 R14, RZ, RZ, R4.reuse ;
      |        /*0110*/                   IMAD.MOV.U32 R12, RZ, RZ, R3.reuse ;
      |        /*0120*/                   DSETP.MAX.AND P2, P3, R2, R4, PT ;
      |        /*0130*/                   IMAD.MOV.U32 R9, RZ, RZ, R4 ;
      |        /*0140*/                   SEL R4, R11, R14, P4 ;
      |        /*0150*/                   IMAD.MOV.U32 R7, RZ, RZ, R3 ;
      |        /*0160*/                   FSEL R11, R12, R5, P4 ;
      |        /*0170*/                   IMAD.MOV.U32 R10, RZ, RZ, R5 ;
      |        /*0180*/                   SEL R2, R6, R9, P2 ;
      |        /*0190*/                   IADD3 R6, P0, R8.reuse, c[0x0][0x170], RZ ;
      |        /*01a0*/               @P5 LOP3.LUT R11, R5, 0x80000, RZ, 0xfc, !PT ;
      |        /*01b0*/                   IADD3 R8, P1, R8, c[0x0][0x178], RZ ;
      |        /*01c0*/                   FSEL R3, R7, R10, P2 ;
      |        /*01d0*/                   IMAD.MOV.U32 R5, RZ, RZ, R11 ;
      |        /*01e0*/                   IADD3.X R7, R0, c[0x0][0x174], RZ, P0, !PT ;
      |        /*01f0*/               @P3 LOP3.LUT R3, R10, 0x80000, RZ, 0xfc, !PT ;
      |        /*0200*/                   IADD3.X R9, R0, c[0x0][0x17c], RZ, P1, !PT ;
      |        /*0210*/                   STG.E.64 [R6.64], R2 ;
      |        /*0220*/                   STG.E.64 [R8.64], R4 ;
      |        /*0230*/                   EXIT ;
      |.L_x_0:
      |        /*0240*/                   BRA `(.L_x_0);
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

class ExtremaFixtures extends NvidiaFixtureSuite {
  import ExtremaFixtures.*

  reproduces("extremaSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.ptx" -> extremaSm89PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_89 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = extremaSm89
  )

}
