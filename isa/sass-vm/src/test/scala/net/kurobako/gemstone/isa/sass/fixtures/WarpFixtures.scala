package net.kurobako.gemstone.isa.sass.fixtures

object WarpFixtures {
  val labeledPartitionCuda: String =
    """#include <cooperative_groups.h>
      |#include <cooperative_groups/reduce.h>
      |namespace cg = cooperative_groups;
      |extern "C" __global__ void labeled(const unsigned *values, unsigned *out) {
      |  const unsigned lane = threadIdx.x;
      |  const unsigned value = values[lane];
      |  const unsigned label = (lane * 7U) % 5U;
      |  auto group = cg::labeled_partition(cg::coalesced_threads(), label);
      |  out[lane] = cg::reduce(group, value, cg::greater<unsigned>());
      |  out[32 + lane] = group.shfl(value, (group.thread_rank() + 1) % group.size());
      |  const unsigned peers = __match_any_sync(__activemask(), label);
      |  out[64 + lane] = __ballot_sync(peers, value & 1U);
      |  out[96 + lane] = peers;
      |}
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "labeledPartitionSm120" ->
      """//--------------------- .text.labeled             --------------------------
        |	.section	.text.labeled,"ax",@progbits
        |	.align	128
        |        .global         labeled
        |        .type           labeled,@function
        |        .size           labeled,(.L_x_22 - labeled)
        |        .other          labeled,@"STO_CUDA_ENTRY STV_DEFAULT"
        |labeled:
        |.text.labeled:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R13, SR_TID.X ;
        |        /*0020*/                   LDC.64 R10, c[0x0][0x358] ;
        |        /*0030*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0040*/                   R2UR UR4, R10 ;
        |        /*0050*/                   R2UR UR5, R11 ;
        |        /*0060*/                   PRMT R6, R13.reuse, 0x9910, RZ ;
        |        /*0070*/                   IMAD.WIDE.U32 R4, R13, 0x4, R4 ;
        |        /*0080*/                   IMAD R6, R6, 0x7, RZ ;
        |        /*0090*/                   LDG.E R0, desc[UR4][R4.64] ;
        |        /*00a0*/                   LOP3.LUT R2, R6, 0xffff, RZ, 0xc0, !PT ;
        |        /*00b0*/                   IMAD R2, R2, 0x3334, RZ ;
        |        /*00c0*/                   SHF.R.U32.HI R2, RZ, 0x10, R2 ;
        |        /*00d0*/                   PRMT R7, R2, 0x9910, RZ ;
        |        /*00e0*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*00f0*/                   IMAD R7, R7, 0x5, RZ ;
        |        /*0100*/                   IADD R5, RZ, -R7 ;
        |        /*0110*/                   VOTE.ANY R7, PT, PT ;
        |        /*0120*/                   PRMT R5, R5, 0x7710, RZ ;
        |        /*0130*/                   R2UR UR4, R7 ;
        |        /*0140*/                   IADD R4, R6, R5 ;
        |        /*0150*/                   LOP3.LUT R4, R4, 0xffff, RZ, 0xc0, !PT ;
        |        /*0160*/                   BRA.DIV UR4, `(.L_x_0) ;
        |        /*0170*/                   MATCH.ANY R5, R4 ;
        |.L_x_11:
        |        /*0180*/                   S2R R9, SR_LANEID ;
        |        /*0190*/                   BREV R6, R5 ;
        |        /*01a0*/                   R2UR UR4, R7 ;
        |        /*01b0*/                   S2R R12, SR_LTMASK ;
        |        /*01c0*/                   FLO.U32.SH R8, R6 ;
        |        /*01d0*/                   ISETP.NE.U32.AND P0, PT, R8, R9, PT ;
        |        /*01e0*/                   BRA.DIV UR4, `(.L_x_1) ;
        |.L_x_14:
        |        /*01f0*/                   BSSY.RECONVERGENT B0, `(.L_x_2) ;
        |        /*0200*/                   WARPSYNC.EXCLUSIVE R5 ;
        |        /*0210*/                   REDUX.MAX UR4, R0 ;
        |        /*0220*/                   MOV R7, UR4 ;
        |        /*0230*/                   NOP ;
        |        /*0240*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_2:
        |        /*0250*/                   R2UR UR4, R10 ;
        |        /*0260*/                   IMAD.WIDE.U32 R2, R13, 0x4, R2 ;
        |        /*0270*/                   R2UR UR5, R11 ;
        |        /*0280*/                   POPC R6, R5 ;
        |        /*0290*/                   LOP3.LUT R9, R12, R5, RZ, 0xc0, !PT ;
        |        /*02a0*/                   MOV R15, 0x310 ;
        |        /*02b0*/                   POPC R9, R9 ;
        |        /*02c0*/                   STG.E desc[UR4][R2.64], R7 ;
        |        /*02d0*/                   LOP3.LUT R13, R6, 0xffff, RZ, 0xc0, !PT ;
        |        /*02e0*/                   IADD R12, R9, 0x1 ;
        |        /*02f0*/                   LOP3.LUT R12, R12, 0xffff, RZ, 0xc0, !PT ;
        |        /*0300*/                   CALL.REL.NOINC `($__internal_0_$__cuda_sm20_rem_u16) ;
        |        /*0310*/                   LOP3.LUT R13, R13, 0xffff, RZ, 0xc0, !PT ;
        |        /*0320*/                   BSSY.RECONVERGENT B0, `(.L_x_3) ;
        |        /*0330*/                   ISETP.NE.U32.AND P0, PT, R13, RZ, PT ;
        |        /*0340*/              @!P0 BRA `(.L_x_4) ;
        |        /*0350*/                   ISETP.NE.U32.AND P0, PT, R5, -0x1, PT ;
        |        /*0360*/                   MOV R8, R13 ;
        |        /*0370*/              @!P0 BRA `(.L_x_4) ;
        |        /*0380*/                   IADD R6, R13, 0x1 ;
        |        /*0390*/                   BSSY.RECONVERGENT B1, `(.L_x_5) ;
        |        /*03a0*/                   HFMA2 R8, -RZ, RZ, 0, 0 ;
        |        /*03b0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*03c0*/                   ISETP.EQ.AND P1, PT, R6, RZ, PT ;
        |        /*03d0*/                   MOV R7, R5 ;
        |        /*03e0*/               @P1 BRA `(.L_x_6) ;
        |        /*03f0*/                   ISETP.GT.AND P1, PT, R6, RZ, PT ;
        |        /*0400*/               @P1 BRA `(.L_x_7) ;
        |        /*0410*/                   BREV R7, R7 ;
        |        /*0420*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*0430*/                   IADD R8, -R8, 0x1f ;
        |        /*0440*/                   IADD R6, -R6, RZ ;
        |        /*0450*/                   BRA `(.L_x_7) ;
        |.L_x_6:
        |        /*0460*/                   MOV R9, 0x1 ;
        |        /*0470*/                   MOV R6, 0x1 ;
        |        /*0480*/                   SHF.L.U32 R12, R9, R8, RZ ;
        |        /*0490*/                   LOP3.LUT R7, R7, R12, RZ, 0xc0, !PT ;
        |.L_x_7:
        |        /*04a0*/                   NOP ;
        |        /*04b0*/                   BSYNC.RECONVERGENT B1 ;
        |.L_x_5:
        |        /*04c0*/                   MOV R9, 0xffffffff ;
        |        /*04d0*/                   SHF.L.U32 R8, R9, R8, RZ ;
        |        /*04e0*/                   LOP3.LUT R8, R7, R8, RZ, 0xc0, !PT ;
        |        /*04f0*/                   PRMT R9, R8, 0x7710, RZ ;
        |        /*0500*/                   POPC R7, R8 ;
        |        /*0510*/                   POPC R9, R9 ;
        |        /*0520*/                   ISETP.LT.U32.AND P3, PT, R9, R6, PT ;
        |        /*0530*/                   IADD R13, R6, -R9 ;
        |        /*0540*/                   SEL R13, R13, R6, P3 ;
        |        /*0550*/               @P3 SHF.R.U32.HI R8, RZ, 0x10, R8 ;
        |        /*0560*/                   PRMT R12, R8, 0x7770, RZ ;
        |        /*0570*/                   POPC R12, R12 ;
        |        /*0580*/                   ISETP.LT.U32.AND P4, PT, R12, R13, PT ;
        |        /*0590*/               @P4 SHF.R.U32.HI R8, RZ, 0x8, R8 ;
        |        /*05a0*/               @P4 IADD R13, R13, -R12 ;
        |        /*05b0*/                   SGXT.U32 R14, R8, 0x4 ;
        |        /*05c0*/                   POPC R14, R14 ;
        |        /*05d0*/                   ISETP.LT.U32.AND P2, PT, R14, R13, PT ;
        |        /*05e0*/               @P2 SHF.R.U32.HI R8, RZ, 0x4, R8 ;
        |        /*05f0*/               @P2 IADD R13, R13, -R14 ;
        |        /*0600*/                   SHF.R.U32.HI R15, RZ, 0x1, R8 ;
        |        /*0610*/                   SGXT.U32 R9, R8, 0x1 ;
        |        /*0620*/                   SGXT.U32 R12, R15, 0x1 ;
        |        /*0630*/                   IADD R12, R9, R12 ;
        |        /*0640*/                   SEL R9, RZ, 0x10, !P3 ;
        |        /*0650*/                   ISETP.LT.U32.AND P1, PT, R12, R13, PT ;
        |        /*0660*/               @P4 IADD R9, R9, 0x8 ;
        |        /*0670*/               @P2 IADD R9, R9, 0x4 ;
        |        /*0680*/               @P1 SHF.R.U32.HI R8, RZ, 0x2, R8 ;
        |        /*0690*/               @P1 IADD R13, R13, -R12 ;
        |        /*06a0*/               @P1 IADD R9, R9, 0x2 ;
        |        /*06b0*/                   SGXT.U32 R8, R8, 0x1 ;
        |        /*06c0*/                   ISETP.LT.AND P1, PT, R7, R6, PT ;
        |        /*06d0*/                   ISETP.LT.U32.AND P3, PT, R8, R13, PT ;
        |        /*06e0*/               @P3 IADD R9, R9, 0x1 ;
        |        /*06f0*/               @P0 IADD R9, -R9, 0x1f ;
        |        /*0700*/                   SEL R8, R9, 0xffffffff, !P1 ;
        |.L_x_4:
        |        /*0710*/                   NOP ;
        |        /*0720*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_3:
        |        /*0730*/                   WARPSYNC R5 ;
        |        /*0740*/                   R2UR UR4, R10 ;
        |        /*0750*/                   R2UR UR5, R11 ;
        |        /*0760*/                   SHFL.IDX PT, R5, R0, R8, 0x1f ;
        |        /*0770*/                   VOTE.ANY R7, PT, PT ;
        |        /*0780*/                   STG.E desc[UR4][R2.64+0x80], R5 ;
        |        /*0790*/                   R2UR UR4, R7 ;
        |        /*07a0*/                   BRA.DIV UR4, `(.L_x_8) ;
        |        /*07b0*/                   MATCH.ANY R7, R4 ;
        |.L_x_17:
        |        /*07c0*/                   REDUX.OR UR4, R7 ;
        |        /*07d0*/                   R2UR P1, URZ, R7 ;
        |        /*07e0*/                   LOP3.LUT P0, RZ, R0, 0x1, RZ, 0xc0, !PT ;
        |        /*07f0*/              @!P1 BRA.DIV UR4, `(.L_x_9) ;
        |        /*0800*/                   VOTE.ANY R5, PT, P0 ;
        |.L_x_20:
        |        /*0810*/                   R2UR UR4, R10.reuse ;
        |        /*0820*/                   R2UR UR5, R11.reuse ;
        |        /*0830*/                   R2UR UR6, R10 ;
        |        /*0840*/                   R2UR UR7, R11 ;
        |        /*0850*/                   STG.E desc[UR4][R2.64+0x100], R5 ;
        |        /*0860*/                   STG.E desc[UR6][R2.64+0x180], R7 ;
        |        /*0870*/                   EXIT ;
        |.L_x_0:
        |        /*0880*/                   WARPSYNC.COLLECTIVE R7, `(.L_x_10) ;
        |        /*0890*/                   MATCH.ANY R5, R4 ;
        |        /*08a0*/                   ENDCOLLECTIVE ;
        |.L_x_10:
        |        /*08b0*/                   BRA `(.L_x_11) ;
        |.L_x_1:
        |        /*08c0*/                   BSSY B0, `(.L_x_12) ;
        |        /*08d0*/                   PLOP3.LUT P0, PT, P0, PT, PT, 0x8, 0x80 ;
        |        /*08e0*/                   WARPSYNC.COLLECTIVE R7, `(.L_x_13) ;
        |        /*08f0*/                   VOTE.ANY R6, PT, P0 ;
        |        /*0900*/                   ENDCOLLECTIVE ;
        |.L_x_13:
        |        /*0910*/                   BSYNC B0 ;
        |.L_x_12:
        |        /*0920*/                   BRA `(.L_x_14) ;
        |.L_x_8:
        |        /*0930*/                   BSSY B0, `(.L_x_15) ;
        |        /*0940*/                   WARPSYNC.COLLECTIVE R7, `(.L_x_16) ;
        |        /*0950*/                   MATCH.ANY R5, R4 ;
        |        /*0960*/                   ENDCOLLECTIVE ;
        |.L_x_16:
        |        /*0970*/                   BSYNC B0 ;
        |.L_x_15:
        |        /*0980*/                   MOV R7, R5 ;
        |        /*0990*/                   BRA `(.L_x_17) ;
        |.L_x_9:
        |        /*09a0*/                   BSSY B0, `(.L_x_18) ;
        |        /*09b0*/                   WARPSYNC.COLLECTIVE R7, `(.L_x_19) ;
        |        /*09c0*/                   VOTE.ANY R6, PT, P0 ;
        |        /*09d0*/                   ENDCOLLECTIVE ;
        |.L_x_19:
        |        /*09e0*/                   BSYNC B0 ;
        |.L_x_18:
        |        /*09f0*/                   MOV R5, R6 ;
        |        /*0a00*/                   BRA `(.L_x_20) ;
        |        .type           $__internal_0_$__cuda_sm20_rem_u16,@function
        |        .size           $__internal_0_$__cuda_sm20_rem_u16,(.L_x_22 - $__internal_0_$__cuda_sm20_rem_u16)
        |$__internal_0_$__cuda_sm20_rem_u16:
        |        /*0a10*/                   I2F.U16 R6, R13 ;
        |        /*0a20*/                   HFMA2 R7, -RZ, RZ, 15, 0 ;
        |        /*0a30*/                   FSETP.GEU.AND P1, PT, |R6|.reuse, 1.175494350822287508e-38, PT ;
        |        /*0a40*/                   FSETP.GT.AND P0, PT, |R6|, 8.50705917302346158658e+37, PT ;
        |        /*0a50*/                   FSEL R7, R7, 1, !P1 ;
        |        /*0a60*/                   FSEL R7, R7, 0.25, !P0 ;
        |        /*0a70*/                   ISETP.NE.U32.AND P0, PT, R13, RZ, PT ;
        |        /*0a80*/                   FMUL R9, R6, R7 ;
        |        /*0a90*/                   I2F.U16.RZ R6, R12 ;
        |        /*0aa0*/                   MUFU.RCP R14, R9 ;
        |        /*0ab0*/                   FMUL R14, R7, R14 ;
        |        /*0ac0*/                   IADD R7, R14, 0x2 ;
        |        /*0ad0*/                   FMUL.RZ R14, R6, R7 ;
        |        /*0ae0*/                   HFMA2 R7, -RZ, RZ, 0, 0 ;
        |        /*0af0*/                   F2I.U32.TRUNC.NTZ R14, R14 ;
        |        /*0b00*/                   LOP3.LUT R6, R14, 0xffff, RZ, 0xc0, !PT ;
        |        /*0b10*/                   IADD R6, -R6, RZ ;
        |        /*0b20*/                   IMAD R13, R13, R6, R12 ;
        |        /*0b30*/                   MOV R6, R15 ;
        |        /*0b40*/              @!P0 MOV R13, 0xffffffff ;
        |        /*0b50*/                   RET.REL.NODEC R6 `(labeled) ;
        |.L_x_22:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin
  )
}
