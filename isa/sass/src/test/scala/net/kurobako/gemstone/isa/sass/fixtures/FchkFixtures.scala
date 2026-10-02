package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object FchkFixtures {
  val fchkProbeSm70PtxSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry probe(.param .u64 p_num, .param .u64 p_den, .param .u64 p_out) {
      |.reg .b64 %pn, %pd, %po, %off;
      |.reg .b32 %n, %d, %q, %i, %b;
      |.reg .pred %p;
      |ld.param.u64 %pn,[p_num]; ld.param.u64 %pd,[p_den]; ld.param.u64 %po,[p_out];
      |mov.u32 %i,%tid.x; mov.u32 %b,%ctaid.x; mad.lo.u32 %i,%b,256,%i;
      |cvt.u64.u32 %off,%i; shl.b64 %off,%off,2;
      |add.u64 %pn,%pn,%off; add.u64 %pd,%pd,%off; add.u64 %po,%po,%off;
      |ld.global.f32 %n,[%pn]; ld.global.f32 %d,[%pd];
      |setp.eq.f32 %p,%n,%d;
      |selp.u32 %q,1,0,%p;
      |st.global.u32 [%po],%q; ret;
      |}
      |""".stripMargin

  private def fchkProbeForTarget(sm: Int): String =
    s""".version 9.2
      |.target sm_$sm
      |.address_size 64
      |.visible .entry probe(.param .u64 p_num, .param .u64 p_den, .param .u64 p_out) {
      |.reg .b64 %pn, %pd, %po, %off;
      |.reg .b32 %n, %d, %q, %i, %b;
      |.reg .pred %p;
      |ld.param.u64 %pn,[p_num]; ld.param.u64 %pd,[p_den]; ld.param.u64 %po,[p_out];
      |mov.u32 %i,%tid.x; mov.u32 %b,%ctaid.x; mad.lo.u32 %i,%b,256,%i;
      |cvt.u64.u32 %off,%i; shl.b64 %off,%off,2;
      |add.u64 %pn,%pn,%off; add.u64 %pd,%pd,%off; add.u64 %po,%po,%off;
      |ld.global.f32 %n,[%pn]; ld.global.f32 %d,[%pd];
      |setp.eq.f32 %p,%n,%d;
      |selp.u32 %q,1,0,%p;
      |st.global.u32 [%po],%q; ret;
      |}
      |""".stripMargin

  val fchkProbeSm75PtxSource: String = fchkProbeForTarget(75)

  val fchkProbeSm80PtxSource: String = fchkProbeForTarget(80)

  val fchkProbeSm86PtxSource: String = fchkProbeForTarget(86)

  val fchkProbeSm89PtxSource: String = fchkProbeForTarget(89)

  val fchkFchkSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;             /* 0x00000a00ff017624 */
      |                                                                                       /* 0x000fc400078e00ff */
      |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                       /* 0x000000fffffff389 */
      |                                                                                       /* 0x000fe200000e00ff */
      |        /*0020*/                   S2R R0, SR_TID.X ;                                  /* 0x0000000000007919 */
      |                                                                                       /* 0x000e280000002100 */
      |        /*0030*/                   S2R R3, SR_CTAID.X ;                                /* 0x0000000000037919 */
      |                                                                                       /* 0x000e240000002500 */
      |        /*0040*/                   IMAD R0, R3, 0x100, R0 ;                            /* 0x0000010003007824 */
      |                                                                                       /* 0x001fc800078e0200 */
      |        /*0050*/                   IMAD.SHL.U32 R6, R0, 0x4, RZ ;                      /* 0x0000000400067824 */
      |                                                                                       /* 0x000fe200078e00ff */
      |        /*0060*/                   SHF.R.U32.HI R0, RZ, 0x1e, R0 ;                     /* 0x0000001eff007819 */
      |                                                                                       /* 0x000fc80000011600 */
      |        /*0070*/                   IADD3 R2, P0, R6.reuse, c[0x0][0x160], RZ ;         /* 0x0000580006027a10 */
      |                                                                                       /* 0x040fe40007f1e0ff */
      |        /*0080*/                   IADD3 R4, P1, R6, c[0x0][0x168], RZ ;               /* 0x00005a0006047a10 */
      |                                                                                       /* 0x000fe40007f3e0ff */
      |        /*0090*/                   IADD3.X R3, R0.reuse, c[0x0][0x164], RZ, P0, !PT ;  /* 0x0000590000037a10 */
      |                                                                                       /* 0x040fe400007fe4ff */
      |        /*00a0*/                   IADD3.X R5, R0, c[0x0][0x16c], RZ, P1, !PT ;        /* 0x00005b0000057a10 */
      |                                                                                       /* 0x000fcc0000ffe4ff */
      |        /*00b0*/                   LDG.E.SYS R2, [R2] ;                                /* 0x0000000002027381 */
      |                                                                                       /* 0x000ea800001ee900 */
      |        /*00c0*/                   LDG.E.SYS R5, [R4] ;                                /* 0x0000000004057381 */
      |                                                                                       /* 0x000ea200001ee900 */
      |        /*00d0*/                   IADD3 R6, P1, R6, c[0x0][0x170], RZ ;               /* 0x00005c0006067a10 */
      |                                                                                       /* 0x000fc80007f3e0ff */
      |        /*00e0*/                   IADD3.X R7, R0, c[0x0][0x174], RZ, P1, !PT ;        /* 0x00005d0000077a10 */
      |                                                                                       /* 0x004fe40000ffe4ff */
      |        /*00f0*/                   FCHK P0, R2, R5 ;                                   /* 0x0000000502007302 */
      |                                                                                       /* 0x000e620000000000 */
      |        /*0100*/                   SEL R9, RZ, 0x1, P0 ;                               /* 0x00000001ff097807 */
      |                                                                                       /* 0x002fd00000000000 */
      |        /*0110*/                   STG.E.SYS [R6], R9 ;                                /* 0x0000000906007386 */
      |                                                                                       /* 0x000fe2000010e900 */
      |        /*0120*/                   EXIT ;                                              /* 0x000000000000794d */
      |                                                                                       /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0130*/                   BRA `(.L_x_0);                                      /* 0xfffffff000007947 */
      |                                                                                       /* 0x000fc0000383ffff */
      |        /*0140*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                                /* 0x0000000000007918 */
      |                                                                                       /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val fchkFchkSm75: String =
    """	.target	sm_75
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;  /* 0x00000a00ff017624 */
      |                                                                            /* 0x000fe400078e00ff */
      |        /*0010*/                   S2R R0, SR_TID.X ;                       /* 0x0000000000007919 */
      |                                                                            /* 0x000e220000002100 */
      |        /*0020*/                   ULDC.64 UR4, c[0x0][0x160] ;             /* 0x0000580000047ab9 */
      |                                                                            /* 0x000fe40000000a00 */
      |        /*0030*/                   ULDC.64 UR6, c[0x0][0x168] ;             /* 0x00005a0000067ab9 */
      |                                                                            /* 0x000fe20000000a00 */
      |        /*0040*/                   S2R R3, SR_CTAID.X ;                     /* 0x0000000000037919 */
      |                                                                            /* 0x000e240000002500 */
      |        /*0050*/                   IMAD R0, R3, 0x100, R0 ;                 /* 0x0000010003007824 */
      |                                                                            /* 0x001fc800078e0200 */
      |        /*0060*/                   IMAD.SHL.U32 R2, R0, 0x4, RZ ;           /* 0x0000000400027824 */
      |                                                                            /* 0x000fe200078e00ff */
      |        /*0070*/                   SHF.R.U32.HI R3, RZ, 0x1e, R0 ;          /* 0x0000001eff037819 */
      |                                                                            /* 0x000fd00000011600 */
      |        /*0080*/                   LDG.E.SYS R0, [R2.64+UR4] ;              /* 0x0000000402007981 */
      |                                                                            /* 0x000ea8000c1ee900 */
      |        /*0090*/                   LDG.E.SYS R5, [R2.64+UR6] ;              /* 0x0000000602057981 */
      |                                                                            /* 0x000ea2000c1ee900 */
      |        /*00a0*/                   ULDC.64 UR4, c[0x0][0x170] ;             /* 0x00005c0000047ab9 */
      |                                                                            /* 0x004fe20000000a00 */
      |        /*00b0*/                   FCHK P0, R0, R5 ;                        /* 0x0000000500007302 */
      |                                                                            /* 0x000e620000000000 */
      |        /*00c0*/                   SEL R5, RZ, 0x1, P0 ;                    /* 0x00000001ff057807 */
      |                                                                            /* 0x002fca0000000000 */
      |        /*00d0*/                   STG.E.SYS [R2.64+UR4], R5 ;              /* 0x0000000502007986 */
      |                                                                            /* 0x000fe2000c10e904 */
      |        /*00e0*/                   EXIT ;                                   /* 0x000000000000794d */
      |                                                                            /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00f0*/                   BRA `(.L_x_0);                           /* 0xfffffff000007947 */
      |                                                                            /* 0x000fc0000383ffff */
      |.L_x_1:
      |""".stripMargin

  private def fchkFchkForTarget(sm: Int): String =
    s"""	.target	sm_$sm
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_0 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0020*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0030*/                   S2R R3, SR_CTAID.X ;
      |        /*0040*/                   IMAD R0, R3, 0x100, R0 ;
      |        /*0050*/                   IMAD.SHL.U32 R6, R0, 0x4, RZ ;
      |        /*0060*/                   SHF.R.U32.HI R0, RZ, 0x1e, R0 ;
      |        /*0070*/                   IADD3 R4, P1, R6.reuse, c[0x0][0x168], RZ ;
      |        /*0080*/                   IADD3 R2, P0, R6, c[0x0][0x160], RZ ;
      |        /*0090*/                   IADD3.X R5, R0.reuse, c[0x0][0x16c], RZ, P1, !PT ;
      |        /*00a0*/                   IADD3.X R3, R0, c[0x0][0x164], RZ, P0, !PT ;
      |        /*00b0*/                   LDG.E R5, [R4.64] ;
      |        /*00c0*/                   LDG.E R2, [R2.64] ;
      |        /*00d0*/                   IADD3 R6, P1, R6, c[0x0][0x170], RZ ;
      |        /*00e0*/                   IADD3.X R7, R0, c[0x0][0x174], RZ, P1, !PT ;
      |        /*00f0*/                   FCHK P0, R2, R5 ;
      |        /*0100*/                   NOP ;
      |        /*0110*/                   NOP ;
      |        /*0120*/                   NOP ;
      |        /*0130*/                   NOP ;
      |        /*0140*/                   NOP ;
      |        /*0150*/                   NOP ;
      |        /*0160*/                   NOP ;
      |        /*0170*/                   NOP ;
      |        /*0180*/                   SEL R9, RZ, 0x1, P0 ;
      |        /*0190*/                   STG.E [R6.64], R9 ;
      |        /*01a0*/                   EXIT ;
      |        /*01b0*/                   NOP;
      |        /*01c0*/                   NOP;
      |        /*01d0*/                   NOP;
      |        /*01e0*/                   NOP;
      |        /*01f0*/                   NOP;
      |.L_x_0:
      |""".stripMargin

  val fchkFchkSm80: String = fchkFchkForTarget(80)

  val fchkFchkSm86: String = fchkFchkForTarget(86)

  val fchkFchkSm89: String = fchkFchkForTarget(89)

}

class FchkFixtures extends NvidiaFixtureSuite {
  import FchkFixtures.*

  reproduces("fchkFchkSm70")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "probe-sm70.ptx" -> fchkProbeSm70PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_70 probe-sm70.ptx -o probe-sm70.cubin",
      """perl -0777 - probe-sm70.cubin > fchk-sm70.cubin <<'PERL'
      |$x=<>;
      |$o=pack("H*","0b7200020500000000d0f00300c84f00");
      |die "FSETP count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$i=index($x,$o);
      |$p=unpack("Q<",substr($x,$i-8,8));
      |die "preceding control\n" unless substr($x,$i-8,8) eq pack("H*","ffe4ff0000e40f00");
      |substr($x,$i-8,8,pack("H*","ffe4ff0000e44f00"));
      |substr($x,$i,16,pack("H*","02730002050000000000000000620e00"));
      |$t=$i+16;
      |die "consumer opcode\n" unless substr($x,$t,8) eq pack("H*","077809ff01000000");
      |substr($x,$t+8,8,pack("H*","0000000000d02f00"));
      |print $x
      |PERL""".stripMargin,
      "nvdisasm -c -ndf -hex fchk-sm70.cubin > fchk-sm70.sass"
    ),
    output = "fchk-sm70.sass",
    expected = fchkFchkSm70
  )

  reproduces("fchkFchkSm75")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "probe-sm75.ptx" -> fchkProbeSm75PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_75 probe-sm75.ptx -o probe-sm75.cubin",
      """perl -0777 - probe-sm75.cubin > fchk-sm75.cubin <<'PERL'
      |$x=<>;
      |$o=pack("H*","0b7200000500000000d0f00300c84f00");
      |die "FSETP count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$i=index($x,$o);
      |$p=unpack("Q<",substr($x,$i-8,8));
      |die "preceding control\n" unless substr($x,$i-8,8) eq pack("H*","000a000000e20f00");
      |substr($x,$i-8,8,pack("H*","000a000000e24f00"));
      |substr($x,$i,16,pack("H*","02730000050000000000000000620e00"));
      |$t=$i+16;
      |die "consumer opcode\n" unless substr($x,$t,8) eq pack("H*","077805ff01000000");
      |substr($x,$t+8,8,pack("H*","0000000000ca2f00"));
      |print $x
      |PERL""".stripMargin,
      "nvdisasm -c -ndf -hex fchk-sm75.cubin > fchk-sm75.sass"
    ),
    output = "fchk-sm75.sass",
    expected = fchkFchkSm75
  )

  reproduces("fchkFchkSm80")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.2.78")),
    inputs = Vec(
      "probe-sm80.ptx" -> fchkProbeSm80PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_80 probe-sm80.ptx -o probe-sm80.cubin",
      """perl -0777 - probe-sm80.cubin > fchk-sm80.cubin <<'PERL'
      |$x=<>;
      |$o=pack("H*","0b72000205000000 00d0f00300c84f00"=~s/ //gr);
      |$n=pack("H*","0273000205000000 0000000000624e00"=~s/ //gr);
      |die "FSETP count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$x =~ s/\Q$o\E/$n/;
      |$tail=pack("H*","077809ff010000000000000000ca0f00");
      |die "SEL tail count\n" unless index($x,$tail)>=0 && index($x,$tail)==rindex($x,$tail);
      |$i=index($x,$tail);
      |$chunk=substr($x,$i,0x30);
      |substr($x,$i+0x80,0x30,$chunk);
      |$nop=pack("H*","18790000000000000000000000c40f00");
      |substr($x,$i,0x80,$nop x 8);
      |substr($x,$i+0x88,8,pack("H*","0000000000ca2f00"));
      |$ex=pack("H*","041c040020010000");
      |die "exit metadata\n" unless index($x,$ex)>=0 && index($x,$ex)==rindex($x,$ex);
      |$q=index($x,$ex)+4;
      |substr($x,$q,4,pack("V",0x1a0));
      |print $x
      |PERL""".stripMargin,
      "nvdisasm -c fchk-sm80.cubin > fchk-sm80.sass"
    ),
    output = "fchk-sm80.sass",
    expected = fchkFchkSm80
  )

  reproduces("fchkFchkSm86")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.2.78")),
    inputs = Vec(
      "probe-sm86.ptx" -> fchkProbeSm86PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_86 probe-sm86.ptx -o probe-sm86.cubin",
      """perl -0777 - probe-sm86.cubin > fchk-sm86.cubin <<'PERL'
      |$x=<>;
      |$o=pack("H*","0b72000205000000 00d0f00300c84f00"=~s/ //gr);
      |$n=pack("H*","0273000205000000 0000000000624e00"=~s/ //gr);
      |die "FSETP count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$x =~ s/\Q$o\E/$n/;
      |$tail=pack("H*","077809ff010000000000000000ca0f00");
      |die "SEL tail count\n" unless index($x,$tail)>=0 && index($x,$tail)==rindex($x,$tail);
      |$i=index($x,$tail);
      |$chunk=substr($x,$i,0x30);
      |substr($x,$i+0x80,0x30,$chunk);
      |$nop=pack("H*","18790000000000000000000000c40f00");
      |substr($x,$i,0x80,$nop x 8);
      |substr($x,$i+0x88,8,pack("H*","0000000000ca2f00"));
      |$ex=pack("H*","041c040020010000");
      |die "exit metadata\n" unless index($x,$ex)>=0 && index($x,$ex)==rindex($x,$ex);
      |$q=index($x,$ex)+4;
      |substr($x,$q,4,pack("V",0x1a0));
      |print $x
      |PERL""".stripMargin,
      "nvdisasm -c fchk-sm86.cubin > fchk-sm86.sass"
    ),
    output = "fchk-sm86.sass",
    expected = fchkFchkSm86
  )

  reproduces("fchkFchkSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.2.78")),
    inputs = Vec(
      "probe-sm89.ptx" -> fchkProbeSm89PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_89 probe-sm89.ptx -o probe-sm89.cubin",
      """perl -0777 - probe-sm89.cubin > fchk-sm89.cubin <<'PERL'
      |$x=<>;
      |$o=pack("H*","0b72000205000000 00d0f00300c84f00"=~s/ //gr);
      |$n=pack("H*","0273000205000000 0000000000624e00"=~s/ //gr);
      |die "FSETP count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$x =~ s/\Q$o\E/$n/;
      |$tail=pack("H*","077809ff010000000000000000ca0f00");
      |die "SEL tail count\n" unless index($x,$tail)>=0 && index($x,$tail)==rindex($x,$tail);
      |$i=index($x,$tail);
      |$chunk=substr($x,$i,0x30);
      |substr($x,$i+0x80,0x30,$chunk);
      |$nop=pack("H*","18790000000000000000000000c40f00");
      |substr($x,$i,0x80,$nop x 8);
      |substr($x,$i+0x88,8,pack("H*","0000000000ca2f00"));
      |$ex=pack("H*","041c040020010000");
      |die "exit metadata\n" unless index($x,$ex)>=0 && index($x,$ex)==rindex($x,$ex);
      |$q=index($x,$ex)+4;
      |substr($x,$q,4,pack("V",0x1a0));
      |print $x
      |PERL""".stripMargin,
      "nvdisasm -c fchk-sm89.cubin > fchk-sm89.sass"
    ),
    output = "fchk-sm89.sass",
    expected = fchkFchkSm89
  )

}
