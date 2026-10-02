package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object PsetFixtures {
  val psetLegacyProbePtxSource: String =
    """.version 7.8
      |.target sm_50
      |.address_size 64
      |.visible .entry probe(.param .u64 out, .param .u32 a) {
      |.reg .u64 %out;
      |.reg .u32 %a,%r;
      |.reg .pred %p;
      |ld.param.u64 %out,[out];ld.param.u32 %a,[a];
      |setp.ne.u32 %p,%a,0;
      |vote.any.pred %p,%p;
      |selp.u32 %r,0xffffffff,0,%p;
      |st.global.u32 [%out],%r;
      |ret;
      |}""".stripMargin

  val psetLegacyPsetSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=4"
      |	.align	32
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |                                                                                     /* 0x001fc400fe2007f6 */
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;                            /* 0x4c98078000870001 */
      |        /*0010*/                   ISETP.NE.U32.AND P0, PT, RZ, c[0x0][0x148], PT ;  /* 0x4b6a03800527ff07 */
      |        /*0018*/                   MOV R2, c[0x0][0x140] ;                           /* 0x4c98078005070002 */
      |                                                                                     /* 0x001fc800fda007fb */
      |        /*0028*/                   MOV R3, c[0x0][0x144] ;                           /* 0x4c98078005170003 */
      |        /*0030*/                   VOTE.ANY P0, P0 ;                                 /* 0x50d90000000700ff */
      |        /*0038*/                   PSET.AND.AND R0, P0, PT, PT ;                     /* 0x50880380e0070000 */
      |                                                                                     /* 0x001ffc00fdc007f1 */
      |        /*0048*/                   STG.E [R2], R0 ;                                  /* 0xeedc200000070200 */
      |        /*0050*/                   NOP ;                                             /* 0x50b0000000070f00 */
      |        /*0058*/                   EXIT ;                                            /* 0xe30000000007000f */
      |.L_x_0:
      |                                                                                     /* 0x001f8000fc0007ff */
      |        /*0068*/                   BRA `(.L_x_0) ;                                   /* 0xe2400fffff07000f */
      |        /*0070*/                   NOP;                                              /* 0x50b0000000070f00 */
      |        /*0078*/                   NOP;                                              /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

  val psetLegacyPsetSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=4"
      |	.align	32
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |                                                                                     /* 0x001f8400fda007f6 */
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;                            /* 0x4c98078000870001 */
      |        /*0010*/                   ISETP.NE.U32.AND P0, PT, RZ, c[0x0][0x148], PT ;  /* 0x4b6a03800527ff07 */
      |        /*0018*/                   VOTE.ANY P0, P0 ;                                 /* 0x50d90000000700ff */
      |                                                                                     /* 0x001f8800ff6007e1 */
      |        /*0028*/                   MOV R2, c[0x0][0x140] ;                           /* 0x4c98078005070002 */
      |        /*0030*/                   MOV R3, c[0x0][0x144] ;                           /* 0x4c98078005170003 */
      |        /*0038*/                   PSET.AND.AND R0, P0, PT, PT ;                     /* 0x50880380e0070000 */
      |                                                                                     /* 0x001ffc00fdc007f1 */
      |        /*0048*/                   STG.E [R2], R0 ;                                  /* 0xeedc200000070200 */
      |        /*0050*/                   NOP ;                                             /* 0x50b0000000070f00 */
      |        /*0058*/                   EXIT ;                                            /* 0xe30000000007000f */
      |.L_x_0:
      |                                                                                     /* 0x001f8000fc0007ff */
      |        /*0068*/                   BRA `(.L_x_0) ;                                   /* 0xe2400fffff07000f */
      |        /*0070*/                   NOP;                                              /* 0x50b0000000070f00 */
      |        /*0078*/                   NOP;                                              /* 0x50b0000000070f00 */
      |.L_x_1:
      |""".stripMargin

}

class PsetFixtures extends NvidiaFixtureSuite {
  import PsetFixtures.*

  reproduces("psetLegacyPsetSm50")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "probe.ptx" -> psetLegacyProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_50 probe.ptx -o probe-sm50.cubin",
      "nvdisasm -c -ndf -hex probe-sm50.cubin > probe-sm50.sass",
      "cp probe-sm50.cubin pset-sm50.cubin",
      "text_offset=$(readelf -SW probe-sm50.cubin | awk '{for(i=1;i<=NF;i++) if($i==\".text.probe\"){print \"0x\"$(i+3);exit}}'); test \"$(od -An -tx1 -N8 -j $((text_offset+0x38)) probe-sm50.cubin | tr -d ' \\n')\" = 00fff7ff7f04a039; perl -e 'print pack(\"H*\",shift)' 000007e080038850 | dd of=pset-sm50.cubin bs=1 seek=$((text_offset+0x38)) conv=notrunc status=none",
      "nvdisasm -c -ndf -hex pset-sm50.cubin > pset-sm50.sass"
    ),
    output = "pset-sm50.sass",
    expected = psetLegacyPsetSm50
  )

  reproduces("psetLegacyPsetSm60")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "probe.ptx" -> psetLegacyProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_60 probe.ptx -o probe-sm60.cubin",
      "nvdisasm -c -ndf -hex probe-sm60.cubin > probe-sm60.sass",
      "cp probe-sm60.cubin pset-sm60.cubin",
      "text_offset=$(readelf -SW probe-sm60.cubin | awk '{for(i=1;i<=NF;i++) if($i==\".text.probe\"){print \"0x\"$(i+3);exit}}'); test \"$(od -An -tx1 -N8 -j $((text_offset+0x38)) probe-sm60.cubin | tr -d ' \\n')\" = 00fff7ff7f04a039; perl -e 'print pack(\"H*\",shift)' 000007e080038850 | dd of=pset-sm60.cubin bs=1 seek=$((text_offset+0x38)) conv=notrunc status=none",
      "nvdisasm -c -ndf -hex pset-sm60.cubin > pset-sm60.sass"
    ),
    output = "pset-sm60.sass",
    expected = psetLegacyPsetSm60
  )

}
