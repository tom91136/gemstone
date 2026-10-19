package net.kurobako.gemstone.isa.sass.fixtures

/** The ptxas 13.3 listings of [[net.kurobako.gemstone.isa.nvidia.fixtures.AsyncCases]]' kernels. */
object AsyncFixtures {
  val listings: Map[String, String] = Map(
    // ptxas 13.3 -arch=sm_100a on cancelPtx, nvdisasm 13.3 -c -ndf
    "cancelSm100a" ->
      """	.target	sm_100a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.cancel              --------------------------
        |	.section	.text.cancel,"ax",@progbits
        |	.align	128
        |        .global         cancel
        |        .type           cancel,@function
        |        .size           cancel,(.L_x_13 - cancel)
        |        .other          cancel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |cancel:
        |.text.cancel:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   S2UR UR8, SR_CgaCtaId ;
        |        /*0030*/                   BSSY.RECONVERGENT B0, `(.L_x_0) ;
        |        /*0040*/                   ISETP.NE.U32.AND P0, PT, R0, RZ, PT ;
        |        /*0050*/               @P0 BRA `(.L_x_1) ;
        |        /*0060*/                   UMOV UR6, 0x400 ;
        |        /*0070*/                   UMOV UR4, 0x1 ;
        |        /*0080*/                   UIADD3 UR7, UPT, UPT, UR6, 0x10, URZ ;
        |        /*0090*/                   IMAD.MOV.U32 R4, RZ, RZ, -0x1 ;
        |        /*00a0*/                   UIADD3 UR4, UPT, UPT, -UR4, 0x100000, URZ ;
        |        /*00b0*/                   USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00c0*/                   USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00d0*/                   IMAD.MOV.U32 R5, RZ, RZ, -0x1 ;
        |        /*00e0*/                   ULEA UR7, UR8, UR7, 0x18 ;
        |        /*00f0*/                   IMAD.MOV.U32 R6, RZ, RZ, -0x1 ;
        |        /*0100*/                   ULEA UR6, UR8, UR6, 0x18 ;
        |        /*0110*/                   IMAD.MOV.U32 R7, RZ, RZ, -0x1 ;
        |        /*0120*/                   IMAD.MOV.U32 R2, RZ, RZ, 0x10 ;
        |        /*0130*/                   STS.128 [UR6], R4 ;
        |        /*0140*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0150*/                   SYNCS.EXCH.64 URZ, [UR7], UR4 ;
        |        /*0160*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR7], R2 ;
        |        /*0170*/                   NOP ;
        |.L_x_1:
        |        /*0180*/                   NOP ;
        |        /*0190*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_0:
        |        /*01a0*/                   LDCU UR4, c[0x0][0x36c] ;
        |        /*01b0*/                   UISETP.EQ.U32.AND UP0, UPT, UR4, 0x1, UPT ;
        |        /*01c0*/                   BRA.U !UP0, `(.L_x_2) ;
        |        /*01d0*/                   MEMBAR.ALL.CTA ;
        |        /*01e0*/                   MEMBAR.ALL.GPU ;
        |        /*01f0*/                   ERRBAR;
        |        /*0200*/                   CGAERRBAR ;
        |        /*0210*/                   UCGABAR_ARV ;
        |        /*0220*/                   BRA `(.L_x_3) ;
        |.L_x_2:
        |        /*0230*/                   NOP ;
        |.L_x_3:
        |        /*0240*/                   BRA.U !UP0, `(.L_x_4) ;
        |        /*0250*/                   UCGABAR_WAIT ;
        |        /*0260*/                   CCTL.IVALL ;
        |        /*0270*/                   BRA `(.L_x_5) ;
        |.L_x_4:
        |        /*0280*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |.L_x_5:
        |        /*0290*/                   ISETP.EQ.U32.AND P0, PT, RZ, UR8, !P0 ;
        |        /*02a0*/                   UMOV UR4, 0x400 ;
        |        /*02b0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*02c0*/                   UIADD3 UR5, UPT, UPT, UR4, 0x10, URZ ;
        |        /*02d0*/                   ULEA UR5, UR8, UR5, 0x18 ;
        |        /*02e0*/                   VOTEU.ANY UP0, P0 ;
        |        /*02f0*/                   @UP0 ULEA UR4, UR8, UR4, 0x18 ;
        |        /*0300*/                   VOTEU.ANY UP0, P0 ;
        |.L_x_6:
        |        /*0310*/               @P0 ELECT P1, URZ, PT ;
        |        /*0320*/                   @UP0 UGETNEXTWORKID.BROADCAST [UR4], [UR5] ;
        |        /*0330*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*0340*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0350*/               @P0 BRA.U.ANY `(.L_x_6) ;
        |        /*0360*/                   SHF.L.U32 R2, RZ, 0x1f, RZ ;
        |        /*0370*/                   LDCU.64 UR6, c[0x0][0x358] ;
        |        /*0380*/                   UMOV UR4, URZ ;
        |.L_x_8:
        |        /*0390*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R2 ;
        |        /*03a0*/               @P0 BRA `(.L_x_7) ;
        |        /*03b0*/                   UIADD3 UR4, UPT, UPT, UR4, 0x1, URZ ;
        |        /*03c0*/                   UISETP.GE.U32.AND UP0, UPT, UR4, 0x3e8, UPT ;
        |        /*03d0*/                   BRA.U !UP0, `(.L_x_8) ;
        |.L_x_7:
        |        /*03e0*/                   S2R R9, SR_CgaCtaId ;
        |        /*03f0*/                   MOV R2, 0x400 ;
        |        /*0400*/                   IMAD.MOV.U32 R8, RZ, RZ, 0x1 ;
        |        /*0410*/                   LDCU UR4, c[0x0][0x36c] ;
        |        /*0420*/                   UISETP.EQ.U32.AND UP0, UPT, UR4, 0x1, UPT ;
        |        /*0430*/                   LEA R4, R9, R2, 0x18 ;
        |        /*0440*/                   LDC.64 R2, c[0x0][0x380] ;
        |        /*0450*/                   LDS.128 R4, [R4] ;
        |        /*0460*/                   LOP3.LUT P1, RZ, R6, 0x1, RZ, 0xc0, !PT ;
        |        /*0470*/                   IMAD R5, R9, 0x20, R0 ;
        |        /*0480*/                   SEL R0, R8, 0x2, P1 ;
        |        /*0490*/                   IMAD.WIDE.U32 R2, R5, 0x4, R2 ;
        |        /*04a0*/                   SEL R5, R0, 0x3, P0 ;
        |        /*04b0*/                   STG.E desc[UR6][R2.64], R5 ;
        |        /*04c0*/                   BRA.U !UP0, `(.L_x_9) ;
        |        /*04d0*/                   MEMBAR.ALL.CTA ;
        |        /*04e0*/                   MEMBAR.ALL.GPU ;
        |        /*04f0*/                   ERRBAR;
        |        /*0500*/                   CGAERRBAR ;
        |        /*0510*/                   UCGABAR_ARV ;
        |        /*0520*/                   BRA `(.L_x_10) ;
        |.L_x_9:
        |        /*0530*/                   NOP ;
        |.L_x_10:
        |        /*0540*/                   BRA.U !UP0, `(.L_x_11) ;
        |        /*0550*/                   UCGABAR_WAIT ;
        |        /*0560*/                   CCTL.IVALL ;
        |        /*0570*/                   EXIT ;
        |.L_x_11:
        |        /*0580*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0590*/                   EXIT ;
        |.L_x_12:
        |        /*05a0*/                   BRA `(.L_x_12);
        |        /*05b0*/                   NOP;
        |        /*05c0*/                   NOP;
        |        /*05d0*/                   NOP;
        |        /*05e0*/                   NOP;
        |        /*05f0*/                   NOP;
        |        /*0600*/                   NOP;
        |        /*0610*/                   NOP;
        |        /*0620*/                   NOP;
        |        /*0630*/                   NOP;
        |        /*0640*/                   NOP;
        |        /*0650*/                   NOP;
        |        /*0660*/                   NOP;
        |        /*0670*/                   NOP;
        |.L_x_13:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "multicastSm90a" ->
      """	.target	sm_90a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.multicast           --------------------------
        |	.section	.text.multicast,"ax",@progbits
        |	.align	128
        |        .global         multicast
        |        .type           multicast,@function
        |        .size           multicast,(.L_x_13 - multicast)
        |        .other          multicast,@"STO_CUDA_ENTRY STV_DEFAULT"
        |multicast:
        |.text.multicast:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R7, SR_TID.X ;
        |        /*0020*/                   S2UR UR8, SR_CgaCtaId ;
        |        /*0030*/                   UMOV UR5, 0x1 ;
        |        /*0040*/                   S2R R4, SR_CTAID.X ;
        |        /*0050*/                   LDC R0, c[0x0][0x140] ;
        |        /*0060*/                   ISETP.NE.U32.AND P0, PT, R7, RZ, PT ;
        |        /*0070*/                   ISETP.EQ.U32.AND P1, PT, R0, 0x1, PT ;
        |        /*0080*/                   VOTEU.ALL UP0, P0 ;
        |        /*0090*/              @!P0 IMAD.MOV.U32 R2, RZ, RZ, 0x100 ;
        |        /*00a0*/                   @!UP0 UMOV UR4, 0x400 ;
        |        /*00b0*/                   @!UP0 UIADD3 UR6, UR4, 0x100, URZ ;
        |        /*00c0*/                   @!UP0 UIADD3 UR4, -UR5, 0x100000, URZ ;
        |        /*00d0*/                   @!UP0 USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00e0*/                   @!UP0 USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00f0*/                   @!UP0 ULEA UR6, UR8, UR6, 0x18 ;
        |        /*0100*/                   VOTEU.ALL UP0, P0 ;
        |        /*0110*/                   @!UP0 SYNCS.EXCH.64 URZ, [UR6], UR4 ;
        |        /*0120*/              @!P0 SYNCS.ARRIVE.TRANS64 RZ, [UR6], R2 ;
        |        /*0130*/                   NOP ;
        |        /*0140*/              @!P1 BRA `(.L_x_0) ;
        |        /*0150*/                   MEMBAR.ALL.CTA ;
        |        /*0160*/                   MEMBAR.ALL.GPU ;
        |        /*0170*/                   ERRBAR;
        |        /*0180*/                   CGAERRBAR ;
        |        /*0190*/                   UCGABAR_ARV ;
        |        /*01a0*/                   BRA `(.L_x_1) ;
        |.L_x_0:
        |        /*01b0*/                   NOP ;
        |.L_x_1:
        |        /*01c0*/              @!P1 BRA `(.L_x_2) ;
        |        /*01d0*/                   UCGABAR_WAIT ;
        |        /*01e0*/                   CCTL.IVALL ;
        |        /*01f0*/                   BRA `(.L_x_3) ;
        |.L_x_2:
        |        /*0200*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |.L_x_3:
        |        /*0210*/                   ISETP.EQ.U32.AND P0, PT, RZ, UR8, !P0 ;
        |        /*0220*/                   BSSY B0, `(.L_x_4) ;
        |        /*0230*/              @!P0 BRA `(.L_x_5) ;
        |        /*0240*/                   ULDC.64 UR6, c[0x0][0x198] ;
        |        /*0250*/                   UMOV UR4, 0x400 ;
        |        /*0260*/                   UIADD3 UR10, UP0, UR6, 0x30, URZ ;
        |        /*0270*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x0 ;
        |        /*0280*/                   UIADD3 UR5, UR4, 0x100, URZ ;
        |        /*0290*/                   UIADD3.X UR11, URZ, UR7, URZ, UP0, !UPT ;
        |        /*02a0*/                   ULEA UR4, UR8, UR4, 0x18 ;
        |        /*02b0*/                   ULEA UR5, UR8, UR5, 0x18 ;
        |        /*02c0*/                   UMOV UR9, 0x30000 ;
        |        /*02d0*/                   UMOV UR6, 0x4 ;
        |        /*02e0*/                   UMOV UR7, 0x1 ;
        |.L_x_6:
        |        /*02f0*/               @P0 ELECT P2, URZ, PT ;
        |        /*0300*/                   UTMALDG.2D.MULTICAST [UR4], [UR10], UR9 ;
        |        /*0310*/               @P2 PLOP3.LUT P0, PT, P2, PT, PT, 0x8, 0x0 ;
        |        /*0320*/                   PLOP3.LUT P2, PT, PT, PT, PT, 0x8, 0x0 ;
        |        /*0330*/               @P0 BRA.U.ANY `(.L_x_6) ;
        |.L_x_5:
        |        /*0340*/                   BSYNC B0 ;
        |.L_x_4:
        |        /*0350*/                   UMOV UR4, 0x400 ;
        |        /*0360*/                   SHF.L.U32 R0, RZ, 0x1f, RZ ;
        |        /*0370*/                   UIADD3 UR5, UR4, 0x100, URZ ;
        |        /*0380*/                   ULEA UR5, UR8, UR5, 0x18 ;
        |        /*0390*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*03a0*/              @!P0 BRA `(.L_x_7) ;
        |.L_x_11:
        |        /*03b0*/                   ULEA UR4, UR8, UR4, 0x18 ;
        |        /*03c0*/                   LDC.64 R2, c[0x0][0x2c0] ;
        |        /*03d0*/                   LEA R0, R7, UR4, 0x2 ;
        |        /*03e0*/                   IMAD R7, R4, 0x40, R7 ;
        |        /*03f0*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0400*/                   LDS R5, [R0] ;
        |        /*0410*/                   IMAD.WIDE.U32 R2, R7, 0x4, R2 ;
        |        /*0420*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*0430*/              @!P1 BRA `(.L_x_8) ;
        |        /*0440*/                   MEMBAR.ALL.CTA ;
        |        /*0450*/                   MEMBAR.ALL.GPU ;
        |        /*0460*/                   ERRBAR;
        |        /*0470*/                   CGAERRBAR ;
        |        /*0480*/                   UCGABAR_ARV ;
        |        /*0490*/                   BRA `(.L_x_9) ;
        |.L_x_8:
        |        /*04a0*/                   NOP ;
        |.L_x_9:
        |        /*04b0*/              @!P1 BRA `(.L_x_10) ;
        |        /*04c0*/                   UCGABAR_WAIT ;
        |        /*04d0*/                   CCTL.IVALL ;
        |        /*04e0*/                   EXIT ;
        |.L_x_10:
        |        /*04f0*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0500*/                   EXIT ;
        |.L_x_7:
        |        /*0510*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*0520*/              @!P0 BRA `(.L_x_7) ;
        |        /*0530*/                   BRA `(.L_x_11) ;
        |.L_x_12:
        |        /*0540*/                   BRA `(.L_x_12);
        |        /*0550*/                   NOP;
        |        /*0560*/                   NOP;
        |        /*0570*/                   NOP;
        |        /*0580*/                   NOP;
        |        /*0590*/                   NOP;
        |        /*05a0*/                   NOP;
        |        /*05b0*/                   NOP;
        |        /*05c0*/                   NOP;
        |        /*05d0*/                   NOP;
        |        /*05e0*/                   NOP;
        |        /*05f0*/                   NOP;
        |.L_x_13:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "multicastSm100a" ->
      """	.target	sm_100a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.multicast           --------------------------
        |	.section	.text.multicast,"ax",@progbits
        |	.align	128
        |        .global         multicast
        |        .type           multicast,@function
        |        .size           multicast,(.L_x_11 - multicast)
        |        .other          multicast,@"STO_CUDA_ENTRY STV_DEFAULT"
        |multicast:
        |.text.multicast:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R7, SR_TID.X ;
        |        /*0020*/                   S2UR UR7, SR_CgaCtaId ;
        |        /*0030*/                   LDCU UR8, c[0x0][0x36c] ;
        |        /*0040*/                   S2R R4, SR_CTAID.X ;
        |        /*0050*/                   ISETP.NE.U32.AND P0, PT, R7, RZ, PT ;
        |        /*0060*/                   VOTEU.ALL UP0, P0 ;
        |        /*0070*/              @!P0 IMAD.MOV.U32 R0, RZ, RZ, 0x100 ;
        |        /*0080*/                   @!UP0 UMOV UR4, 0x400 ;
        |        /*0090*/                   @!UP0 UIADD3 UR6, UPT, UPT, UR4, 0x100, URZ ;
        |        /*00a0*/                   UMOV UR4, 0x1 ;
        |        /*00b0*/                   @!UP0 ULEA UR6, UR7, UR6, 0x18 ;
        |        /*00c0*/                   @!UP0 UIADD3 UR4, UPT, UPT, -UR4, 0x100000, URZ ;
        |        /*00d0*/                   @!UP0 USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00e0*/                   @!UP0 USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00f0*/                   VOTEU.ALL UP0, P0 ;
        |        /*0100*/                   @!UP0 SYNCS.EXCH.64 URZ, [UR6], UR4 ;
        |        /*0110*/                   UISETP.EQ.U32.AND UP0, UPT, UR8, 0x1, UPT ;
        |        /*0120*/              @!P0 SYNCS.ARRIVE.TRANS64 RZ, [UR6], R0 ;
        |        /*0130*/                   NOP ;
        |        /*0140*/                   BRA.U !UP0, `(.L_x_0) ;
        |        /*0150*/                   MEMBAR.ALL.CTA ;
        |        /*0160*/                   MEMBAR.ALL.GPU ;
        |        /*0170*/                   ERRBAR;
        |        /*0180*/                   CGAERRBAR ;
        |        /*0190*/                   UCGABAR_ARV ;
        |        /*01a0*/                   BRA `(.L_x_1) ;
        |.L_x_0:
        |        /*01b0*/                   NOP ;
        |.L_x_1:
        |        /*01c0*/                   BRA.U !UP0, `(.L_x_2) ;
        |        /*01d0*/                   UCGABAR_WAIT ;
        |        /*01e0*/                   CCTL.IVALL ;
        |        /*01f0*/                   BRA `(.L_x_3) ;
        |.L_x_2:
        |        /*0200*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |.L_x_3:
        |        /*0210*/                   ISETP.EQ.U32.AND P0, PT, RZ, UR7, !P0 ;
        |        /*0220*/                   UMOV UR4, 0x400 ;
        |        /*0230*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0240*/                   UIADD3 UR5, UPT, UPT, UR4, 0x100, URZ ;
        |        /*0250*/                   LDCU.64 UR14, c[0x0][0x358] ;
        |        /*0260*/                   ULEA UR9, UR7, UR5, 0x18 ;
        |        /*0270*/                   UMOV UR5, 0x30000 ;
        |        /*0280*/                   VOTEU.ANY UP1, P0 ;
        |        /*0290*/                   VOTEU.ANY UP2, P0 ;
        |        /*02a0*/                   @UP1 LDCU.64 UR12, c[0x0][0x348] ;
        |        /*02b0*/                   @UP1 ULEA UR8, UR7, UR4, 0x18 ;
        |        /*02c0*/                   @UP1 UMOV UR10, 0x4 ;
        |        /*02d0*/                   @UP1 UMOV UR11, 0x1 ;
        |.L_x_4:
        |        /*02e0*/               @P0 ELECT P1, URZ, PT ;
        |        /*02f0*/                   @UP2 UTMALDG.2D.MULTICAST [UR8], [UR12], UR5 ;
        |        /*0300*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*0310*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0320*/               @P0 BRA.U.ANY `(.L_x_4) ;
        |        /*0330*/                   SHF.L.U32 R2, RZ, 0x1f, RZ ;
        |        /*0340*/                   ULEA UR4, UR7, UR4, 0x18 ;
        |        /*0350*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR9], R2 ;
        |        /*0360*/                   LEA R0, R7, UR4, 0x2 ;
        |        /*0370*/              @!P0 BRA `(.L_x_5) ;
        |.L_x_9:
        |        /*0380*/                   LDS R5, [R0] ;
        |        /*0390*/                   LDC.64 R2, c[0x0][0x400] ;
        |        /*03a0*/                   IMAD R7, R4, 0x40, R7 ;
        |        /*03b0*/                   IMAD.WIDE.U32 R2, R7, 0x4, R2 ;
        |        /*03c0*/                   STG.E desc[UR14][R2.64], R5 ;
        |        /*03d0*/                   BRA.U !UP0, `(.L_x_6) ;
        |        /*03e0*/                   MEMBAR.ALL.CTA ;
        |        /*03f0*/                   MEMBAR.ALL.GPU ;
        |        /*0400*/                   ERRBAR;
        |        /*0410*/                   CGAERRBAR ;
        |        /*0420*/                   UCGABAR_ARV ;
        |        /*0430*/                   BRA `(.L_x_7) ;
        |.L_x_6:
        |        /*0440*/                   NOP ;
        |.L_x_7:
        |        /*0450*/                   BRA.U !UP0, `(.L_x_8) ;
        |        /*0460*/                   UCGABAR_WAIT ;
        |        /*0470*/                   CCTL.IVALL ;
        |        /*0480*/                   EXIT ;
        |.L_x_8:
        |        /*0490*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*04a0*/                   EXIT ;
        |.L_x_5:
        |        /*04b0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR9], R2 ;
        |        /*04c0*/              @!P0 BRA `(.L_x_5) ;
        |        /*04d0*/                   BRA `(.L_x_9) ;
        |.L_x_10:
        |        /*04e0*/                   BRA `(.L_x_10);
        |        /*04f0*/                   NOP;
        |        /*0500*/                   NOP;
        |        /*0510*/                   NOP;
        |        /*0520*/                   NOP;
        |        /*0530*/                   NOP;
        |        /*0540*/                   NOP;
        |        /*0550*/                   NOP;
        |        /*0560*/                   NOP;
        |        /*0570*/                   NOP;
        |.L_x_11:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "formsSm100a" ->
      """	.target	sm_100a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.forms               --------------------------
        |	.section	.text.forms,"ax",@progbits
        |	.align	128
        |        .global         forms
        |        .type           forms,@function
        |        .size           forms,(.L_x_1 - forms)
        |        .other          forms,@"STO_CUDA_ENTRY STV_DEFAULT"
        |forms:
        |.text.forms:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*0020*/                   UMOV UR4, 0x400 ;
        |        /*0030*/                   UMOV UR7, 0x3 ;
        |        /*0040*/                   IMAD.MOV.U32 R8, RZ, RZ, 0x1 ;
        |        /*0050*/                   HFMA2 R0, -RZ, RZ, 0, 5.9604644775390625e-08 ;
        |        /*0060*/                   IMAD.MOV.U32 R9, RZ, RZ, 0x2 ;
        |        /*0070*/                   IMAD.MOV.U32 R10, RZ, RZ, 0x3 ;
        |        /*0080*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0090*/                   IMAD.MOV.U32 R11, RZ, RZ, 0x4 ;
        |        /*00a0*/                   IMAD.MOV.U32 R12, RZ, RZ, 0x10 ;
        |        /*00b0*/                   SHF.L.U32 R0, R0, 0x1f, RZ ;
        |        /*00c0*/                   ULEA UR6, UR5, UR4, 0x18 ;
        |        /*00d0*/                   UIADD3 UR4, UPT, UPT, -UR7, 0x100000, URZ ;
        |        /*00e0*/                   USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00f0*/                   USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*0100*/                   STS.128 [UR6+0x40], R8 ;
        |        /*0110*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0120*/                   SYNCS.EXCH.64 URZ, [UR6], UR4 ;
        |        /*0130*/                   SYNCS.ARRIVE.TRANS64.A1T0 R2, [UR6], RZ ;
        |        /*0140*/                   SYNCS.ARRIVE.TRANS64.OPTOUT.A1T0 R6, [UR6], RZ ;
        |        /*0150*/                   SYNCS.PHASECHK.TRANS64 P0, [UR6], R3 ;
        |        /*0160*/                   SYNCS.ARRIVE.TRANS64.RED.A0TR RZ, [UR6], R12 ;
        |        /*0170*/                   SYNCS.ARRIVE.TRANS64.A1T0 RZ, [UR6], RZ ;
        |        /*0180*/                   SYNCS.PHASECHK.TRANS64 P1, [UR6], R3 ;
        |        /*0190*/                   SYNCS.ARRIVE.TRANS64.RED.A0TX RZ, [UR6], R12 ;
        |        /*01a0*/                   SYNCS.PHASECHK.TRANS64 P2, [UR6], R3 ;
        |        /*01b0*/                   SYNCS.ARRIVE.TRANS64.A1T0 R8, [UR6], RZ ;
        |        /*01c0*/                   NOP ;
        |        /*01d0*/                   ARRIVES.LDGSTSBAR.64.ARVCNT [UR6] ;
        |        /*01e0*/                   NOP ;
        |        /*01f0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P3, [UR6], R0 ;
        |        /*0200*/                   LDGMC.E.ADD.32.STRONG.SYS R19, [R4.64+URZ+0x40] ;
        |        /*0210*/                   LDCU.64 UR8, c[0x0][0x380] ;
        |        /*0220*/                   S2R R10, SR_LANEID ;
        |        /*0230*/                   VOTEU.ANY UR4, UPT, PT ;
        |        /*0240*/                   LDCU.64 UR10, c[0x0][0x358] ;
        |        /*0250*/                   POPC R12, UR4 ;
        |        /*0260*/                   UFLO.U32 UR4, UR4 ;
        |        /*0270*/                   UIADD3 UR5, UP0, UPT, UR8, 0x40, URZ ;
        |        /*0280*/                   IMAD R23, R12, 0x5, RZ ;
        |        /*0290*/                   ISETP.EQ.U32.AND P4, PT, R10, UR4, PT ;
        |        /*02a0*/                   UIADD3.X UR4, UPT, UPT, URZ, UR9, URZ, UP0, !UPT ;
        |        /*02b0*/                   IMAD.U32 R10, RZ, RZ, UR5 ;
        |        /*02c0*/                   MOV R11, UR4 ;
        |        /*02d0*/                   STG.E.STRONG.GPU desc[UR10][R4.64+0x44], R19 ;
        |        /*02e0*/               @P4 REDG.E.ADD.STRONG.GPU desc[UR10][R10.64+0x8], R23 ;
        |        /*02f0*/                   UMOV UR4, 0x8 ;
        |        /*0300*/                   SHF.R.U32.HI R21, RZ, 0xb, R7 ;
        |        /*0310*/                   LOP3.LUT R21, R21, 0xfffff, RZ, 0x40, !PT ;
        |        /*0320*/                   IADD3 R21, PT, PT, -R21, 0x100000, RZ ;
        |        /*0330*/                   UMEMSETS.64 [UR6+0x10], URZ, UR4 ;
        |        /*0340*/                   SHF.R.U32.HI R22, RZ, 0xb, R9 ;
        |        /*0350*/                   LOP3.LUT R22, R22, 0xfffff, RZ, 0x40, !PT ;
        |        /*0360*/                   IADD3 R22, PT, PT, -R22, 0x100000, RZ ;
        |        /*0370*/                   SEL R12, RZ, 0x1, !P0 ;
        |        /*0380*/                   LDS.64 R16, [UR6+0x48] ;
        |        /*0390*/                   SEL R13, RZ, 0x1, !P1 ;
        |        /*03a0*/                   SEL R14, RZ, 0x1, !P2 ;
        |        /*03b0*/                   SEL R15, RZ, 0x1, !P3 ;
        |        /*03c0*/                   SHF.R.U32.HI R20, RZ, 0xb, R3 ;
        |        /*03d0*/                   LOP3.LUT R20, R20, 0xfffff, RZ, 0x40, !PT ;
        |        /*03e0*/                   IADD3 R20, PT, PT, -R20, 0x100000, RZ ;
        |        /*03f0*/                   ACQBULK ;
        |        /*0400*/                   IMAD.MOV.U32 R23, RZ, RZ, R16 ;
        |        /*0410*/                   PREEXIT ;
        |        /*0420*/                   STG.E.128 desc[UR10][R4.64+0x10], R12 ;
        |        /*0430*/                   STG.E.128 desc[UR10][R4.64], R20 ;
        |        /*0440*/                   STG.E desc[UR10][R4.64+0x20], R17 ;
        |        /*0450*/                   EXIT ;
        |.L_x_0:
        |        /*0460*/                   BRA `(.L_x_0);
        |        /*0470*/                   NOP;
        |        /*0480*/                   NOP;
        |        /*0490*/                   NOP;
        |        /*04a0*/                   NOP;
        |        /*04b0*/                   NOP;
        |        /*04c0*/                   NOP;
        |        /*04d0*/                   NOP;
        |        /*04e0*/                   NOP;
        |        /*04f0*/                   NOP;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "bulkSm90a" ->
      """	.target	sm_90a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.bulk                --------------------------
        |	.section	.text.bulk,"ax",@progbits
        |	.align	128
        |        .global         bulk
        |        .type           bulk,@function
        |        .size           bulk,(.L_x_13 - bulk)
        |        .other          bulk,@"STO_CUDA_ENTRY STV_DEFAULT"
        |bulk:
        |.text.bulk:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R7, SR_TID.X ;
        |        /*0020*/                   UMOV UR4, 0x1 ;
        |        /*0030*/                   BSSY B0, `(.L_x_0) ;
        |        /*0040*/                   ISETP.NE.U32.AND P1, PT, R7, RZ, PT ;
        |        /*0050*/              @!P1 S2R R3, SR_CgaCtaId ;
        |        /*0060*/              @!P1 MOV R0, 0x400 ;
        |        /*0070*/                   VOTEU.ALL UP0, P1 ;
        |        /*0080*/              @!P1 VIADD R0, R0, 0x200 ;
        |        /*0090*/                   @!UP0 UIADD3 UR4, -UR4, 0x100000, URZ ;
        |        /*00a0*/                   @!UP0 USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00b0*/                   @!UP0 USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00c0*/                   VOTEU.ALL UP0, P1 ;
        |        /*00d0*/              @!P1 LEA R0, R3, R0, 0x18 ;
        |        /*00e0*/              @!P1 R2UR UR6, R0 ;
        |        /*00f0*/                   @!UP0 SYNCS.EXCH.64 URZ, [UR6], UR4 ;
        |        /*0100*/                   NOP ;
        |        /*0110*/                   MEMBAR.ALL.CTA ;
        |        /*0120*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0130*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0140*/               @P1 BRA `(.L_x_1) ;
        |        /*0150*/                   S2UR UR7, SR_CgaCtaId ;
        |        /*0160*/                   ULDC.64 UR4, c[0x0][0x210] ;
        |        /*0170*/                   UMOV UR9, 0x10 ;
        |        /*0180*/                   UMOV UR6, 0x400 ;
        |        /*0190*/                   UBLKPF.L2 [UR4], UR9 ;
        |        /*01a0*/                   UIADD3 UR8, UR6, 0x200, URZ ;
        |        /*01b0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x0 ;
        |        /*01c0*/                   ULEA UR6, UR7, UR6, 0x18 ;
        |        /*01d0*/                   ULEA UR7, UR7, UR8, 0x18 ;
        |.L_x_2:
        |        /*01e0*/               @P0 ELECT P2, URZ, PT ;
        |        /*01f0*/                   UBLKCP.S.G [UR6], [UR4], UR9 ;
        |        /*0200*/               @P2 PLOP3.LUT P0, PT, P2, PT, PT, 0x8, 0x0 ;
        |        /*0210*/                   PLOP3.LUT P2, PT, PT, PT, PT, 0x8, 0x0 ;
        |        /*0220*/               @P0 BRA.U.ANY `(.L_x_2) ;
        |        /*0230*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x100 ;
        |        /*0240*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR7], R0 ;
        |.L_x_1:
        |        /*0250*/                   BSYNC B0 ;
        |.L_x_0:
        |        /*0260*/                   S2UR UR14, SR_CgaCtaId ;
        |        /*0270*/                   UMOV UR11, 0x400 ;
        |        /*0280*/                   SHF.L.U32 R2, RZ, 0x1f, RZ ;
        |        /*0290*/                   UIADD3 UR9, UR11, 0x200, URZ ;
        |        /*02a0*/                   ULEA UR9, UR14, UR9, 0x18 ;
        |        /*02b0*/                   ULEA UR10, UR14, UR11, 0x18 ;
        |        /*02c0*/                   LEA R0, R7, UR10, 0x2 ;
        |        /*02d0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR9], R2 ;
        |        /*02e0*/              @!P0 BRA `(.L_x_3) ;
        |.L_x_10:
        |        /*02f0*/                   LDS R2, [R0] ;
        |        /*0300*/                   ULDC.64 UR12, c[0x0][0x208] ;
        |        /*0310*/                   BSSY B0, `(.L_x_4) ;
        |        /*0320*/                   VIADD R3, R2, 0x1 ;
        |        /*0330*/                   STS [R0], R3 ;
        |        /*0340*/                   MEMBAR.ALL.CTA ;
        |        /*0350*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0360*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0370*/               @P1 BRA `(.L_x_5) ;
        |        /*0380*/                   ULDC.64 UR4, c[0x0][0x210] ;
        |        /*0390*/                   UMOV UR8, 0x10 ;
        |        /*03a0*/                   UIADD3.64 UR4, UR4, 0x100, URZ ;
        |        /*03b0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x0 ;
        |        /*03c0*/                   UIADD3 UR6, UP0, UR4, 0x100, URZ ;
        |        /*03d0*/                   UIADD3.X UR7, URZ, UR5, URZ, UP0, !UPT ;
        |        /*03e0*/                   UBLKCP.G.S [UR4], [UR10], UR8 ;
        |        /*03f0*/                   NOP ;
        |.L_x_6:
        |        /*0400*/               @P0 ELECT P1, URZ, PT ;
        |        /*0410*/                   UBLKRED.G.S.ADD [UR6], [UR10], UR8 ;
        |        /*0420*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x0 ;
        |        /*0430*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;
        |        /*0440*/               @P0 BRA.U.ANY `(.L_x_6) ;
        |        /*0450*/                   UIADD3 UR4, UP0, UR4, 0x200, URZ ;
        |        /*0460*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x0 ;
        |        /*0470*/                   UMOV UR6, 0x10 ;
        |        /*0480*/                   UIADD3.X UR5, URZ, UR5, URZ, UP0, !UPT ;
        |.L_x_7:
        |        /*0490*/               @P0 ELECT P1, URZ, PT ;
        |        /*04a0*/                   UBLKRED.G.S.MIN.S32 [UR4], [UR10], UR6 ;
        |        /*04b0*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x0 ;
        |        /*04c0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;
        |        /*04d0*/               @P0 BRA.U.ANY `(.L_x_7) ;
        |        /*04e0*/                   UTMACMDFLUSH ;
        |        /*04f0*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x100 ;
        |        /*0500*/                   UIADD3 UR4, UR11, 0x100, URZ ;
        |        /*0510*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x0 ;
        |        /*0520*/                   ULEA UR8, UR14, UR4, 0x18 ;
        |        /*0530*/                   UMOV UR4, 0x10 ;
        |        /*0540*/                   DEPBAR.LE SB0, 0x0 ;
        |        /*0550*/                   SYNCS.ARRIVE.TRANS64.RED.A0TR RZ, [UR9], R0 ;
        |.L_x_8:
        |        /*0560*/               @P0 ELECT P1, URZ, PT ;
        |        /*0570*/                   UBLKCP.S.S [UR8], [UR10], UR4 ;
        |        /*0580*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x0 ;
        |        /*0590*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;
        |        /*05a0*/               @P0 BRA.U.ANY `(.L_x_8) ;
        |        /*05b0*/                   SYNCS.ARRIVE.TRANS64.A1T0 R2, [UR9], RZ ;
        |        /*05c0*/                   LDC.64 R4, c[0x0][0x210] ;
        |        /*05d0*/                   SHF.R.U32.HI R3, RZ, 0xb, R3 ;
        |        /*05e0*/                   LOP3.LUT R3, R3, 0xfffff, RZ, 0x40, !PT ;
        |        /*05f0*/                   IADD3 R3, -R3, 0x100000, RZ ;
        |        /*0600*/                   STG.E desc[UR12][R4.64+0x500], R3 ;
        |        /*0610*/                   DEPBAR.LE SB0, 0x0 ;
        |        /*0620*/                   CCTL.IVALL ;
        |.L_x_5:
        |        /*0630*/                   BSYNC B0 ;
        |.L_x_4:
        |        /*0640*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x1 ;
        |        /*0650*/                   SHF.L.U32 R0, R0, 0x1f, RZ ;
        |        /*0660*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR9], R0 ;
        |        /*0670*/              @!P0 BRA `(.L_x_9) ;
        |.L_x_11:
        |        /*0680*/                   UIADD3 UR4, UR11, 0x100, URZ ;
        |        /*0690*/                   LDC.64 R2, c[0x0][0x210] ;
        |        /*06a0*/                   ULEA UR4, UR14, UR4, 0x18 ;
        |        /*06b0*/                   LEA R0, R7, UR4, 0x2 ;
        |        /*06c0*/                   LDS R5, [R0] ;
        |        /*06d0*/                   IMAD.WIDE.U32 R2, R7, 0x4, R2 ;
        |        /*06e0*/                   STG.E desc[UR12][R2.64+0x400], R5 ;
        |        /*06f0*/                   EXIT ;
        |.L_x_3:
        |        /*0700*/                   IMAD.U32 R3, RZ, RZ, UR9 ;
        |        /*0710*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [R3+URZ], R2 ;
        |        /*0720*/              @!P0 NANOSLEEP.SYNCS 0x3e8 ;
        |        /*0730*/              @!P0 SYNCS.PHASECHK.TRANS64 P0, [R3+URZ], R2 ;
        |        /*0740*/              @!P0 BRA `(.L_x_3) ;
        |        /*0750*/                   BRA `(.L_x_10) ;
        |.L_x_9:
        |        /*0760*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR9], R0 ;
        |        /*0770*/              @!P0 BRA `(.L_x_9) ;
        |        /*0780*/                   BRA `(.L_x_11) ;
        |.L_x_12:
        |        /*0790*/                   BRA `(.L_x_12);
        |        /*07a0*/                   NOP;
        |        /*07b0*/                   NOP;
        |        /*07c0*/                   NOP;
        |        /*07d0*/                   NOP;
        |        /*07e0*/                   NOP;
        |        /*07f0*/                   NOP;
        |        /*0800*/                   NOP;
        |        /*0810*/                   NOP;
        |        /*0820*/                   NOP;
        |        /*0830*/                   NOP;
        |        /*0840*/                   NOP;
        |        /*0850*/                   NOP;
        |        /*0860*/                   NOP;
        |        /*0870*/                   NOP;
        |.L_x_13:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "bulkSm100a" ->
      """	.target	sm_100a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.bulk                --------------------------
        |	.section	.text.bulk,"ax",@progbits
        |	.align	128
        |        .global         bulk
        |        .type           bulk,@function
        |        .size           bulk,(.L_x_13 - bulk)
        |        .other          bulk,@"STO_CUDA_ENTRY STV_DEFAULT"
        |bulk:
        |.text.bulk:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R7, SR_TID.X ;
        |        /*0020*/                   UMOV UR4, 0x1 ;
        |        /*0030*/                   BSSY.RECONVERGENT B0, `(.L_x_0) ;
        |        /*0040*/                   ISETP.NE.U32.AND P1, PT, R7, RZ, PT ;
        |        /*0050*/              @!P1 S2R R3, SR_CgaCtaId ;
        |        /*0060*/              @!P1 MOV R0, 0x400 ;
        |        /*0070*/                   VOTEU.ALL UP0, P1 ;
        |        /*0080*/              @!P1 VIADD R0, R0, 0x200 ;
        |        /*0090*/                   @!UP0 UIADD3 UR4, UPT, UPT, -UR4, 0x100000, URZ ;
        |        /*00a0*/                   @!UP0 USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00b0*/                   @!UP0 USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00c0*/                   VOTEU.ALL UP0, P1 ;
        |        /*00d0*/              @!P1 LEA R0, R3, R0, 0x18 ;
        |        /*00e0*/              @!P1 R2UR UR6, R0 ;
        |        /*00f0*/                   @!UP0 SYNCS.EXCH.64 URZ, [UR6], UR4 ;
        |        /*0100*/                   NOP ;
        |        /*0110*/                   MEMBAR.ALL.CTA ;
        |        /*0120*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0130*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0140*/               @P1 BRA `(.L_x_1) ;
        |        /*0150*/                   LDCU.64 UR4, c[0x0][0x380] ;
        |        /*0160*/                   S2UR UR7, SR_CgaCtaId ;
        |        /*0170*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*0180*/                   UMOV UR9, 0x10 ;
        |        /*0190*/                   UMOV UR6, 0x400 ;
        |        /*01a0*/                   UIADD3 UR8, UPT, UPT, UR6, 0x200, URZ ;
        |        /*01b0*/                   UBLKPF.L2 [UR4], UR9 ;
        |        /*01c0*/                   ULEA UR6, UR7, UR6, 0x18 ;
        |        /*01d0*/                   ULEA UR7, UR7, UR8, 0x18 ;
        |.L_x_2:
        |        /*01e0*/               @P0 ELECT P2, URZ, PT ;
        |        /*01f0*/                   UBLKCP.S.G [UR6], [UR4], UR9 ;
        |        /*0200*/               @P2 PLOP3.LUT P0, PT, P2, PT, PT, 0x8, 0x80 ;
        |        /*0210*/                   PLOP3.LUT P2, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0220*/               @P0 BRA.U.ANY `(.L_x_2) ;
        |        /*0230*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x100 ;
        |        /*0240*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR7], R0 ;
        |.L_x_1:
        |        /*0250*/                   NOP ;
        |        /*0260*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_0:
        |        /*0270*/                   S2UR UR14, SR_CgaCtaId ;
        |        /*0280*/                   UMOV UR11, 0x400 ;
        |        /*0290*/                   SHF.L.U32 R2, RZ, 0x1f, RZ ;
        |        /*02a0*/                   UIADD3 UR9, UPT, UPT, UR11, 0x200, URZ ;
        |        /*02b0*/                   ULEA UR9, UR14, UR9, 0x18 ;
        |        /*02c0*/                   ULEA UR10, UR14, UR11, 0x18 ;
        |        /*02d0*/                   LEA R0, R7, UR10, 0x2 ;
        |        /*02e0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR9], R2 ;
        |        /*02f0*/              @!P0 BRA `(.L_x_3) ;
        |.L_x_10:
        |        /*0300*/                   LDS R2, [R0] ;
        |        /*0310*/                   LDCU.64 UR12, c[0x0][0x358] ;
        |        /*0320*/                   BSSY.RECONVERGENT B0, `(.L_x_4) ;
        |        /*0330*/                   VIADD R3, R2, 0x1 ;
        |        /*0340*/                   STS [R0], R3 ;
        |        /*0350*/                   MEMBAR.ALL.CTA ;
        |        /*0360*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0370*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0380*/               @P1 BRA `(.L_x_5) ;
        |        /*0390*/                   LDCU.64 UR4, c[0x0][0x380] ;
        |        /*03a0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*03b0*/                   UMOV UR8, 0x10 ;
        |        /*03c0*/                   UIADD3.64 UR4, UPT, UPT, UR4, 0x100, URZ ;
        |        /*03d0*/                   UIADD3 UR6, UP0, UPT, UR4, 0x100, URZ ;
        |        /*03e0*/                   UIADD3.X UR7, UPT, UPT, URZ, UR5, URZ, UP0, !UPT ;
        |        /*03f0*/                   UBLKCP.G.S [UR4], [UR10], UR8 ;
        |        /*0400*/                   NOP ;
        |.L_x_6:
        |        /*0410*/               @P0 ELECT P1, URZ, PT ;
        |        /*0420*/                   UBLKRED.G.S.ADD [UR6], [UR10], UR8 ;
        |        /*0430*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*0440*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0450*/               @P0 BRA.U.ANY `(.L_x_6) ;
        |        /*0460*/                   UIADD3 UR4, UP0, UPT, UR4, 0x200, URZ ;
        |        /*0470*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*0480*/                   UMOV UR6, 0x10 ;
        |        /*0490*/                   UIADD3.X UR5, UPT, UPT, URZ, UR5, URZ, UP0, !UPT ;
        |.L_x_7:
        |        /*04a0*/               @P0 ELECT P1, URZ, PT ;
        |        /*04b0*/                   UBLKRED.G.S.MIN.S32 [UR4], [UR10], UR6 ;
        |        /*04c0*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*04d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*04e0*/               @P0 BRA.U.ANY `(.L_x_7) ;
        |        /*04f0*/                   UTMACMDFLUSH ;
        |        /*0500*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x100 ;
        |        /*0510*/                   UIADD3 UR4, UPT, UPT, UR11, 0x100, URZ ;
        |        /*0520*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*0530*/                   ULEA UR8, UR14, UR4, 0x18 ;
        |        /*0540*/                   UMOV UR4, 0x10 ;
        |        /*0550*/                   DEPBAR.LE SB0, 0x0 ;
        |        /*0560*/                   SYNCS.ARRIVE.TRANS64.RED.A0TR RZ, [UR9], R0 ;
        |.L_x_8:
        |        /*0570*/               @P0 ELECT P1, URZ, PT ;
        |        /*0580*/                   UBLKCP.S.S [UR8], [UR10], UR4 ;
        |        /*0590*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*05a0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*05b0*/               @P0 BRA.U.ANY `(.L_x_8) ;
        |        /*05c0*/                   SYNCS.ARRIVE.TRANS64.A1T0 R2, [UR9], RZ ;
        |        /*05d0*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*05e0*/                   SHF.R.U32.HI R3, RZ, 0xb, R3 ;
        |        /*05f0*/                   LOP3.LUT R3, R3, 0xfffff, RZ, 0x40, !PT ;
        |        /*0600*/                   IADD3 R3, PT, PT, -R3, 0x100000, RZ ;
        |        /*0610*/                   STG.E desc[UR12][R4.64+0x500], R3 ;
        |        /*0620*/                   DEPBAR.LE SB0, 0x0 ;
        |        /*0630*/                   CCTL.IVALL ;
        |.L_x_5:
        |        /*0640*/                   NOP ;
        |        /*0650*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_4:
        |        /*0660*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x1 ;
        |        /*0670*/                   SHF.L.U32 R0, R0, 0x1f, RZ ;
        |        /*0680*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR9], R0 ;
        |        /*0690*/              @!P0 BRA `(.L_x_9) ;
        |.L_x_11:
        |        /*06a0*/                   UIADD3 UR4, UPT, UPT, UR11, 0x100, URZ ;
        |        /*06b0*/                   LDC.64 R2, c[0x0][0x380] ;
        |        /*06c0*/                   ULEA UR4, UR14, UR4, 0x18 ;
        |        /*06d0*/                   LEA R0, R7, UR4, 0x2 ;
        |        /*06e0*/                   LDS R5, [R0] ;
        |        /*06f0*/                   IMAD.WIDE.U32 R2, R7, 0x4, R2 ;
        |        /*0700*/                   STG.E desc[UR12][R2.64+0x400], R5 ;
        |        /*0710*/                   EXIT ;
        |.L_x_3:
        |        /*0720*/                   IMAD.U32 R3, RZ, RZ, UR9 ;
        |        /*0730*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [R3+URZ], R2 ;
        |        /*0740*/              @!P0 NANOSLEEP.SYNCS 0x3e8 ;
        |        /*0750*/              @!P0 SYNCS.PHASECHK.TRANS64 P0, [R3+URZ], R2 ;
        |        /*0760*/              @!P0 BRA `(.L_x_3) ;
        |        /*0770*/                   BRA `(.L_x_10) ;
        |.L_x_9:
        |        /*0780*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR9], R0 ;
        |        /*0790*/              @!P0 BRA `(.L_x_9) ;
        |        /*07a0*/                   BRA `(.L_x_11) ;
        |.L_x_12:
        |        /*07b0*/                   BRA `(.L_x_12);
        |        /*07c0*/                   NOP;
        |        /*07d0*/                   NOP;
        |        /*07e0*/                   NOP;
        |        /*07f0*/                   NOP;
        |        /*0800*/                   NOP;
        |        /*0810*/                   NOP;
        |        /*0820*/                   NOP;
        |        /*0830*/                   NOP;
        |        /*0840*/                   NOP;
        |        /*0850*/                   NOP;
        |        /*0860*/                   NOP;
        |        /*0870*/                   NOP;
        |.L_x_13:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "bulkSm120a" ->
      """	.target	sm_120a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.bulk                --------------------------
        |	.section	.text.bulk,"ax",@progbits
        |	.align	128
        |        .global         bulk
        |        .type           bulk,@function
        |        .size           bulk,(.L_x_16 - bulk)
        |        .other          bulk,@"STO_CUDA_ENTRY STV_DEFAULT"
        |bulk:
        |.text.bulk:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R16, SR_TID.X ;
        |        /*0020*/                   UMOV UR4, 0x1 ;
        |        /*0030*/                   BSSY.RECONVERGENT B6, `(.L_x_0) ;
        |        /*0040*/                   ISETP.NE.U32.AND P0, PT, R16, RZ, PT ;
        |        /*0050*/                   P2R R0, PR, RZ, 0x1 ;
        |        /*0060*/              @!P0 S2R R3, SR_CgaCtaId ;
        |        /*0070*/              @!P0 MOV R0, 0x400 ;
        |        /*0080*/                   VOTEU.ALL UP0, P0 ;
        |        /*0090*/              @!P0 IADD R0, R0, 0x200 ;
        |        /*00a0*/                   @!UP0 UIADD3 UR4, UPT, UPT, -UR4, 0x100000, URZ ;
        |        /*00b0*/                   @!UP0 USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00c0*/                   @!UP0 USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00d0*/                   VOTEU.ALL UP0, P0 ;
        |        /*00e0*/              @!P0 LEA R0, R3, R0, 0x18 ;
        |        /*00f0*/              @!P0 R2UR UR6, R0 ;
        |        /*0100*/                   @!UP0 SYNCS.EXCH.64 URZ, [UR6], UR4 ;
        |        /*0110*/                   NOP ;
        |        /*0120*/              @!PT LDS RZ, [RZ] ;
        |        /*0130*/              @!PT LDS RZ, [RZ] ;
        |        /*0140*/              @!PT LDS RZ, [RZ] ;
        |        /*0150*/              @!PT LDS RZ, [RZ] ;
        |        /*0160*/                   MEMBAR.ALL.CTA ;
        |        /*0170*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0180*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0190*/               @P0 BRA `(.L_x_1) ;
        |        /*01a0*/                   S2UR UR11, SR_CgaCtaId ;
        |        /*01b0*/                   LDCU.64 UR6, c[0x0][0x380] ;
        |        /*01c0*/                   UMOV UR10, 0x10 ;
        |        /*01d0*/                   UMOV UR5, 0x400 ;
        |        /*01e0*/                   UIADD3 UR9, UPT, UPT, UR5, 0x200, URZ ;
        |        /*01f0*/                   UBLKPF.L2 [UR6], UR10 ;
        |        /*0200*/                   ULEA UR4, UR11, UR5, 0x18 ;
        |        /*0210*/                   ULEA UR9, UR11, UR9, 0x18 ;
        |        /*0220*/                   UMOV UR5, URZ ;
        |        /*0230*/                   USHF.L.U64.HI UR8, UR4, 0x8, URZ ;
        |        /*0240*/                   UISETP.NE.U32.AND UP0, UPT, UR8, UR11, UPT ;
        |        /*0250*/                   BRA.U !UP0, `(.L_x_2) ;
        |        /*0260*/                   LDCU.64 UR6, c[0x0][0x380] ;
        |        /*0270*/                   MOV R2, 0x0 ;
        |        /*0280*/                   HFMA2 R5, -RZ, RZ, 0, 0 ;
        |        /*0290*/                   MOV R4, UR4 ;
        |        /*02a0*/                   HFMA2 R11, -RZ, RZ, 0, 0 ;
        |        /*02b0*/                   MOV R8, 0x100 ;
        |        /*02c0*/                   LDC.64 R2, c[0x4][R2] ;
        |        /*02d0*/                   MOV R10, UR9 ;
        |        /*02e0*/                   CS2R R12, SRZ ;
        |        /*02f0*/                   MOV R6, UR6 ;
        |        /*0300*/                   MOV R7, UR7 ;
        |        /*0310*/                   LEPC R20, `(.L_x_3) ;
        |        /*0320*/                   CALL.ABS.NOINC R2 ;
        |.L_x_3:
        |        /*0330*/                   BRA `(.L_x_4) ;
        |.L_x_2:
        |        /*0340*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*0350*/                   UMOV UR4, 0x400 ;
        |        /*0360*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*0370*/                   UIADD3 UR6, UPT, UPT, UR4, 0x200, URZ ;
        |        /*0380*/                   LDCU.64 UR8, c[0x0][0x380] ;
        |        /*0390*/                   ULEA UR4, UR5, UR4, 0x18 ;
        |        /*03a0*/                   ULEA UR5, UR5, UR6, 0x18 ;
        |        /*03b0*/                   UMOV UR6, 0x10 ;
        |.L_x_5:
        |        /*03c0*/               @P0 ELECT P1, URZ, PT ;
        |        /*03d0*/                   UBLKCP.S.G [UR4], [UR8], UR6 ;
        |        /*03e0*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*03f0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0400*/               @P0 BRA.U.ANY `(.L_x_5) ;
        |.L_x_4:
        |        /*0410*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*0420*/                   UMOV UR4, 0x400 ;
        |        /*0430*/                   HFMA2 R0, -RZ, RZ, 0, 1.52587890625e-05 ;
        |        /*0440*/                   UIADD3 UR4, UPT, UPT, UR4, 0x200, URZ ;
        |        /*0450*/                   ULEA UR4, UR5, UR4, 0x18 ;
        |        /*0460*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR4], R0 ;
        |.L_x_1:
        |        /*0470*/                   NOP ;
        |        /*0480*/                   BSYNC.RECONVERGENT B6 ;
        |.L_x_0:
        |        /*0490*/                   S2R R7, SR_CgaCtaId ;
        |        /*04a0*/                   MOV R6, 0x400 ;
        |        /*04b0*/                   SHF.L.U32 R2, RZ, 0x1f, RZ ;
        |        /*04c0*/                   IADD R0, R6, 0x200 ;
        |        /*04d0*/                   LEA R9, R7, R0, 0x18 ;
        |        /*04e0*/                   LEA R5, R7, R6, 0x18 ;
        |        /*04f0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [R9+URZ], R2 ;
        |        /*0500*/                   IMAD R0, R16, 0x4, R5 ;
        |        /*0510*/              @!P0 BRA `(.L_x_6) ;
        |.L_x_13:
        |        /*0520*/                   LDS R2, [R0] ;
        |        /*0530*/                   IADD R3, R2, 0x1 ;
        |        /*0540*/                   STS [R0], R3 ;
        |        /*0550*/              @!PT LDS RZ, [RZ] ;
        |        /*0560*/              @!PT LDS RZ, [RZ] ;
        |        /*0570*/              @!PT LDS RZ, [RZ] ;
        |        /*0580*/              @!PT LDS RZ, [RZ] ;
        |        /*0590*/                   MEMBAR.ALL.CTA ;
        |        /*05a0*/                   FENCE.VIEW.ASYNC.S ;
        |        /*05b0*/                   WARPSYNC.ALL ;
        |        /*05c0*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*05d0*/                   ISETP.NE.U32.AND P0, PT, R16, RZ, PT ;
        |        /*05e0*/                   LDCU.64 UR10, c[0x0][0x358] ;
        |        /*05f0*/                   BSSY.RECONVERGENT B0, `(.L_x_7) ;
        |        /*0600*/               @P0 BRA `(.L_x_8) ;
        |        /*0610*/                   LDCU.64 UR4, c[0x0][0x380] ;
        |        /*0620*/                   R2UR UR8, R5 ;
        |        /*0630*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*0640*/                   UMOV UR9, 0x10 ;
        |        /*0650*/                   UIADD3.64 UR4, UPT, UPT, UR4, 0x100, URZ ;
        |        /*0660*/                   UIADD3.64 UR6, UPT, UPT, UR4, 0x100, URZ ;
        |        /*0670*/                   UBLKCP.G.S [UR4], [UR8], UR9 ;
        |        /*0680*/                   NOP ;
        |.L_x_9:
        |        /*0690*/               @P0 ELECT P1, URZ, PT ;
        |        /*06a0*/                   UBLKRED.G.S.ADD [UR6], [UR8], UR9 ;
        |        /*06b0*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*06c0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*06d0*/               @P0 BRA.U.ANY `(.L_x_9) ;
        |        /*06e0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*06f0*/                   UIADD3.64 UR4, UPT, UPT, UR4, 0x200, URZ ;
        |        /*0700*/                   UMOV UR6, 0x10 ;
        |.L_x_10:
        |        /*0710*/               @P0 ELECT P1, URZ, PT ;
        |        /*0720*/                   UBLKRED.G.S.MIN.S32 [UR4], [UR8], UR6 ;
        |        /*0730*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*0740*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0750*/               @P0 BRA.U.ANY `(.L_x_10) ;
        |        /*0760*/                   UTMACMDFLUSH ;
        |        /*0770*/                   HFMA2 R0, -RZ, RZ, 0, 1.52587890625e-05 ;
        |        /*0780*/                   S2UR UR6, SR_CgaCtaId ;
        |        /*0790*/                   R2UR UR4, R6 ;
        |        /*07a0*/                   R2UR UR5, R9 ;
        |        /*07b0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*07c0*/                   UIADD3 UR4, UPT, UPT, UR4, 0x100, URZ ;
        |        /*07d0*/                   DEPBAR.LE SB0, 0x0 ;
        |        /*07e0*/                   SYNCS.ARRIVE.TRANS64.RED.A0TR RZ, [R9+URZ], R0 ;
        |        /*07f0*/                   ULEA UR4, UR6, UR4, 0x18 ;
        |        /*0800*/                   UMOV UR6, 0x10 ;
        |.L_x_11:
        |        /*0810*/               @P0 ELECT P1, URZ, PT ;
        |        /*0820*/                   UBLKCP.S.S [UR4], [UR8], UR6 ;
        |        /*0830*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*0840*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0850*/               @P0 BRA.U.ANY `(.L_x_11) ;
        |        /*0860*/                   SYNCS.ARRIVE.TRANS64.A1T0 R2, [R9+URZ], RZ ;
        |        /*0870*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0880*/                   SHF.R.U32.HI R3, RZ, 0xb, R3 ;
        |        /*0890*/                   LOP3.LUT R3, R3, 0xfffff, RZ, 0x40, !PT ;
        |        /*08a0*/                   IADD R3, -R3, 0x100000 ;
        |        /*08b0*/                   STG.E desc[UR10][R4.64+0x500], R3 ;
        |        /*08c0*/                   DEPBAR.LE SB0, 0x0 ;
        |        /*08d0*/                   CCTL.IVALL ;
        |.L_x_8:
        |        /*08e0*/                   NOP ;
        |        /*08f0*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_7:
        |        /*0900*/                   HFMA2 R2, -RZ, RZ, 0, 5.9604644775390625e-08 ;
        |        /*0910*/                   IADD R6, R6, 0x100 ;
        |        /*0920*/                   SHF.L.U32 R2, R2, 0x1f, RZ ;
        |        /*0930*/                   LEA R7, R7, R6, 0x18 ;
        |        /*0940*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [R9+URZ], R2 ;
        |        /*0950*/                   IMAD R0, R16, 0x4, R7 ;
        |        /*0960*/              @!P0 BRA `(.L_x_12) ;
        |.L_x_14:
        |        /*0970*/                   LDS R5, [R0] ;
        |        /*0980*/                   LDC.64 R2, c[0x0][0x380] ;
        |        /*0990*/                   IMAD.WIDE.U32 R16, R16, 0x4, R2 ;
        |        /*09a0*/                   STG.E desc[UR10][R16.64+0x400], R5 ;
        |        /*09b0*/                   EXIT ;
        |.L_x_6:
        |        /*09c0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [R9+URZ], R2 ;
        |        /*09d0*/              @!P0 NANOSLEEP.SYNCS 0x3e8 ;
        |        /*09e0*/              @!P0 SYNCS.PHASECHK.TRANS64 P0, [R9+URZ], R2 ;
        |        /*09f0*/              @!P0 BRA `(.L_x_6) ;
        |        /*0a00*/                   BRA `(.L_x_13) ;
        |.L_x_12:
        |        /*0a10*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [R9+URZ], R2 ;
        |        /*0a20*/              @!P0 BRA `(.L_x_12) ;
        |        /*0a30*/                   BRA `(.L_x_14) ;
        |.L_x_15:
        |        /*0a40*/                   BRA `(.L_x_15);
        |        /*0a50*/                   NOP;
        |        /*0a60*/                   NOP;
        |        /*0a70*/                   NOP;
        |        /*0a80*/                   NOP;
        |        /*0a90*/                   NOP;
        |        /*0aa0*/                   NOP;
        |        /*0ab0*/                   NOP;
        |        /*0ac0*/                   NOP;
        |        /*0ad0*/                   NOP;
        |        /*0ae0*/                   NOP;
        |        /*0af0*/                   NOP;
        |.L_x_16:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |	.type		__cuda_syscall_cp_async_bulk_unicast,@function
        |""".stripMargin,
    "tileSm90a" ->
      """	.target	sm_90a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.tile                --------------------------
        |	.section	.text.tile,"ax",@progbits
        |	.align	128
        |        .global         tile
        |        .type           tile,@function
        |        .size           tile,(.L_x_7 - tile)
        |        .other          tile,@"STO_CUDA_ENTRY STV_DEFAULT"
        |tile:
        |.text.tile:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R7, SR_TID.X ;
        |        /*0020*/                   UMOV UR4, 0x1 ;
        |        /*0030*/                   ULDC.64 UR12, c[0x0][0x208] ;
        |        /*0040*/                   BSSY B0, `(.L_x_0) ;
        |        /*0050*/                   ISETP.NE.U32.AND P1, PT, R7, RZ, PT ;
        |        /*0060*/              @!P1 S2R R3, SR_CgaCtaId ;
        |        /*0070*/              @!P1 MOV R0, 0x400 ;
        |        /*0080*/                   VOTEU.ALL UP0, P1 ;
        |        /*0090*/              @!P1 VIADD R0, R0, 0x400 ;
        |        /*00a0*/                   @!UP0 UIADD3 UR4, -UR4, 0x100000, URZ ;
        |        /*00b0*/                   @!UP0 USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00c0*/                   @!UP0 USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00d0*/                   VOTEU.ALL UP0, P1 ;
        |        /*00e0*/              @!P1 LEA R0, R3, R0, 0x18 ;
        |        /*00f0*/              @!P1 R2UR UR6, R0 ;
        |        /*0100*/                   @!UP0 SYNCS.EXCH.64 URZ, [UR6], UR4 ;
        |        /*0110*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0120*/               @P1 BRA `(.L_x_1) ;
        |        /*0130*/                   ULDC.64 UR4, c[0x0][0x198] ;
        |        /*0140*/                   S2UR UR9, SR_CgaCtaId ;
        |        /*0150*/                   UIADD3 UR4, UP0, UR4, 0x30, URZ ;
        |        /*0160*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x0 ;
        |        /*0170*/                   ULDC.64 UR10, c[0x0][0x3c8] ;
        |        /*0180*/                   UMOV UR8, 0x400 ;
        |        /*0190*/                   UIADD3.X UR5, URZ, UR5, URZ, UP0, !UPT ;
        |        /*01a0*/                   UIADD3 UR6, UR8, 0x400, URZ ;
        |        /*01b0*/                   UTMACCTL.PF [UR4] ;
        |        /*01c0*/                   UTMAPF.L2.2D [UR10], [UR4] ;
        |        /*01d0*/                   ULEA UR8, UR9, UR8, 0x18 ;
        |        /*01e0*/                   ULEA UR9, UR9, UR6, 0x18 ;
        |.L_x_2:
        |        /*01f0*/               @P0 ELECT P2, URZ, PT ;
        |        /*0200*/                   UTMALDG.2D [UR8], [UR4] ;
        |        /*0210*/               @P2 PLOP3.LUT P0, PT, P2, PT, PT, 0x8, 0x0 ;
        |        /*0220*/                   PLOP3.LUT P2, PT, PT, PT, PT, 0x8, 0x0 ;
        |        /*0230*/               @P0 BRA.U.ANY `(.L_x_2) ;
        |        /*0240*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x400 ;
        |        /*0250*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR9], R0 ;
        |.L_x_1:
        |        /*0260*/                   BSYNC B0 ;
        |.L_x_0:
        |        /*0270*/                   S2UR UR6, SR_CgaCtaId ;
        |        /*0280*/                   UMOV UR4, 0x400 ;
        |        /*0290*/                   SHF.L.U32 R0, RZ, 0x1f, RZ ;
        |        /*02a0*/                   UIADD3 UR5, UR4, 0x400, URZ ;
        |        /*02b0*/                   ULEA UR5, UR6, UR5, 0x18 ;
        |        /*02c0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*02d0*/              @!P0 BRA `(.L_x_3) ;
        |.L_x_5:
        |        /*02e0*/                   ULEA UR8, UR6, UR4, 0x18 ;
        |        /*02f0*/                   LDC.64 R2, c[0x0][0x3c0] ;
        |        /*0300*/                   LEA R0, R7, UR8, 0x2 ;
        |        /*0310*/                   LDS R5, [R0] ;
        |        /*0320*/                   MEMBAR.ALL.CTA ;
        |        /*0330*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0340*/                   IMAD.WIDE.U32 R2, R7, 0x4, R2 ;
        |        /*0350*/                   STG.E desc[UR12][R2.64], R5 ;
        |        /*0360*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0370*/               @P1 EXIT ;
        |        /*0380*/                   ULDC.64 UR6, c[0x0][0x198] ;
        |        /*0390*/                   ULDC.64 UR12, c[0x0][0x3c8] ;
        |        /*03a0*/                   UIADD3 UR4, UP0, UR6, 0xb0, URZ ;
        |        /*03b0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x0 ;
        |        /*03c0*/                   UMOV UR9, UR12 ;
        |        /*03d0*/                   UMOV UR10, UR13 ;
        |        /*03e0*/                   UIADD3.X UR5, URZ, UR7, URZ, UP0, !UPT ;
        |        /*03f0*/                   UIADD3 UR6, UP0, UR6, 0x130, URZ ;
        |        /*0400*/                   UIADD3.X UR7, URZ, UR7, URZ, UP0, !UPT ;
        |        /*0410*/                   UTMASTG.2D [UR8], [UR4] ;
        |        /*0420*/                   NOP ;
        |.L_x_4:
        |        /*0430*/               @P0 ELECT P1, URZ, PT ;
        |        /*0440*/                   UMOV UR9, UR12 ;
        |        /*0450*/                   UMOV UR10, UR13 ;
        |        /*0460*/                   UTMAREDG.2D.ADD [UR8], [UR6] ;
        |        /*0470*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x0 ;
        |        /*0480*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;
        |        /*0490*/               @P0 BRA.U.ANY `(.L_x_4) ;
        |        /*04a0*/                   UTMACMDFLUSH ;
        |        /*04b0*/                   DEPBAR.LE SB0, 0x0 ;
        |        /*04c0*/                   EXIT ;
        |.L_x_3:
        |        /*04d0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*04e0*/              @!P0 BRA `(.L_x_3) ;
        |        /*04f0*/                   BRA `(.L_x_5) ;
        |.L_x_6:
        |        /*0500*/                   BRA `(.L_x_6);
        |        /*0510*/                   NOP;
        |        /*0520*/                   NOP;
        |        /*0530*/                   NOP;
        |        /*0540*/                   NOP;
        |        /*0550*/                   NOP;
        |        /*0560*/                   NOP;
        |        /*0570*/                   NOP;
        |        /*0580*/                   NOP;
        |        /*0590*/                   NOP;
        |        /*05a0*/                   NOP;
        |        /*05b0*/                   NOP;
        |        /*05c0*/                   NOP;
        |        /*05d0*/                   NOP;
        |        /*05e0*/                   NOP;
        |        /*05f0*/                   NOP;
        |.L_x_7:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "tileSm100a" ->
      """	.target	sm_100a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.tile                --------------------------
        |	.section	.text.tile,"ax",@progbits
        |	.align	128
        |        .global         tile
        |        .type           tile,@function
        |        .size           tile,(.L_x_7 - tile)
        |        .other          tile,@"STO_CUDA_ENTRY STV_DEFAULT"
        |tile:
        |.text.tile:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R7, SR_TID.X ;
        |        /*0020*/                   UMOV UR4, 0x1 ;
        |        /*0030*/                   LDCU.64 UR12, c[0x0][0x358] ;
        |        /*0040*/                   BSSY.RECONVERGENT B0, `(.L_x_0) ;
        |        /*0050*/                   ISETP.NE.U32.AND P1, PT, R7, RZ, PT ;
        |        /*0060*/              @!P1 S2R R3, SR_CgaCtaId ;
        |        /*0070*/              @!P1 MOV R0, 0x400 ;
        |        /*0080*/                   VOTEU.ALL UP0, P1 ;
        |        /*0090*/              @!P1 VIADD R0, R0, 0x400 ;
        |        /*00a0*/                   @!UP0 UIADD3 UR4, UPT, UPT, -UR4, 0x100000, URZ ;
        |        /*00b0*/                   @!UP0 USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00c0*/                   @!UP0 USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00d0*/                   VOTEU.ALL UP0, P1 ;
        |        /*00e0*/              @!P1 LEA R0, R3, R0, 0x18 ;
        |        /*00f0*/              @!P1 R2UR UR6, R0 ;
        |        /*0100*/                   @!UP0 SYNCS.EXCH.64 URZ, [UR6], UR4 ;
        |        /*0110*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0120*/               @P1 BRA `(.L_x_1) ;
        |        /*0130*/                   LDCU.64 UR4, c[0x0][0x348] ;
        |        /*0140*/                   S2UR UR9, SR_CgaCtaId ;
        |        /*0150*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*0160*/                   LDCU.64 UR10, c[0x0][0x508] ;
        |        /*0170*/                   UMOV UR8, 0x400 ;
        |        /*0180*/                   UIADD3 UR6, UPT, UPT, UR8, 0x400, URZ ;
        |        /*0190*/                   UTMACCTL.PF [UR4] ;
        |        /*01a0*/                   UTMAPF.L2.2D [UR10], [UR4] ;
        |        /*01b0*/                   ULEA UR8, UR9, UR8, 0x18 ;
        |        /*01c0*/                   ULEA UR9, UR9, UR6, 0x18 ;
        |.L_x_2:
        |        /*01d0*/               @P0 ELECT P2, URZ, PT ;
        |        /*01e0*/                   UTMALDG.2D [UR8], [UR4] ;
        |        /*01f0*/               @P2 PLOP3.LUT P0, PT, P2, PT, PT, 0x8, 0x80 ;
        |        /*0200*/                   PLOP3.LUT P2, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0210*/               @P0 BRA.U.ANY `(.L_x_2) ;
        |        /*0220*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x400 ;
        |        /*0230*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR9], R0 ;
        |.L_x_1:
        |        /*0240*/                   NOP ;
        |        /*0250*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_0:
        |        /*0260*/                   S2UR UR6, SR_CgaCtaId ;
        |        /*0270*/                   UMOV UR4, 0x400 ;
        |        /*0280*/                   SHF.L.U32 R0, RZ, 0x1f, RZ ;
        |        /*0290*/                   UIADD3 UR5, UPT, UPT, UR4, 0x400, URZ ;
        |        /*02a0*/                   ULEA UR5, UR6, UR5, 0x18 ;
        |        /*02b0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*02c0*/              @!P0 BRA `(.L_x_3) ;
        |.L_x_5:
        |        /*02d0*/                   ULEA UR8, UR6, UR4, 0x18 ;
        |        /*02e0*/                   LDC.64 R2, c[0x0][0x500] ;
        |        /*02f0*/                   LEA R0, R7, UR8, 0x2 ;
        |        /*0300*/                   LDS R5, [R0] ;
        |        /*0310*/                   MEMBAR.ALL.CTA ;
        |        /*0320*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0330*/                   IMAD.WIDE.U32 R2, R7, 0x4, R2 ;
        |        /*0340*/                   STG.E desc[UR12][R2.64], R5 ;
        |        /*0350*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0360*/               @P1 EXIT ;
        |        /*0370*/                   LDCU.64 UR6, c[0x0][0x348] ;
        |        /*0380*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*0390*/                   LDCU.64 UR12, c[0x0][0x508] ;
        |        /*03a0*/                   UIADD3 UR4, UP0, UPT, UR6, 0x80, URZ ;
        |        /*03b0*/                   UIADD3.X UR5, UPT, UPT, URZ, UR7, URZ, UP0, !UPT ;
        |        /*03c0*/                   UMOV UR9, UR12 ;
        |        /*03d0*/                   UMOV UR10, UR13 ;
        |        /*03e0*/                   UIADD3 UR6, UP0, UPT, UR6, 0x100, URZ ;
        |        /*03f0*/                   UIADD3.X UR7, UPT, UPT, URZ, UR7, URZ, UP0, !UPT ;
        |        /*0400*/                   UTMASTG.2D [UR8], [UR4] ;
        |        /*0410*/                   NOP ;
        |.L_x_4:
        |        /*0420*/               @P0 ELECT P1, URZ, PT ;
        |        /*0430*/                   UMOV UR9, UR12 ;
        |        /*0440*/                   UMOV UR10, UR13 ;
        |        /*0450*/                   UTMAREDG.2D.ADD [UR8], [UR6] ;
        |        /*0460*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*0470*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0480*/               @P0 BRA.U.ANY `(.L_x_4) ;
        |        /*0490*/                   UTMACMDFLUSH ;
        |        /*04a0*/                   DEPBAR.LE SB0, 0x0 ;
        |        /*04b0*/                   EXIT ;
        |.L_x_3:
        |        /*04c0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*04d0*/              @!P0 BRA `(.L_x_3) ;
        |        /*04e0*/                   BRA `(.L_x_5) ;
        |.L_x_6:
        |        /*04f0*/                   BRA `(.L_x_6);
        |        /*0500*/                   NOP;
        |        /*0510*/                   NOP;
        |        /*0520*/                   NOP;
        |        /*0530*/                   NOP;
        |        /*0540*/                   NOP;
        |        /*0550*/                   NOP;
        |        /*0560*/                   NOP;
        |        /*0570*/                   NOP;
        |.L_x_7:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "tileSm120a" ->
      """	.target	sm_120a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.tile                --------------------------
        |	.section	.text.tile,"ax",@progbits
        |	.align	128
        |        .global         tile
        |        .type           tile,@function
        |        .size           tile,(.L_x_10 - tile)
        |        .other          tile,@"STO_CUDA_ENTRY STV_DEFAULT"
        |tile:
        |.text.tile:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R16, SR_TID.X ;
        |        /*0020*/                   UMOV UR4, 0x1 ;
        |        /*0030*/                   LDCU.64 UR36, c[0x0][0x358] ;
        |        /*0040*/                   BSSY.RECONVERGENT B6, `(.L_x_0) ;
        |        /*0050*/                   IADD R1, R1, -0x8 ;
        |        /*0060*/                   ISETP.NE.U32.AND P0, PT, R16, RZ, PT ;
        |        /*0070*/                   P2R R0, PR, RZ, 0x1 ;
        |        /*0080*/              @!P0 S2R R3, SR_CgaCtaId ;
        |        /*0090*/              @!P0 MOV R0, 0x400 ;
        |        /*00a0*/                   VOTEU.ALL UP0, P0 ;
        |        /*00b0*/              @!P0 IADD R0, R0, 0x400 ;
        |        /*00c0*/                   @!UP0 UIADD3 UR4, UPT, UPT, -UR4, 0x100000, URZ ;
        |        /*00d0*/                   @!UP0 USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00e0*/                   @!UP0 USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00f0*/                   VOTEU.ALL UP0, P0 ;
        |        /*0100*/              @!P0 LEA R0, R3, R0, 0x18 ;
        |        /*0110*/              @!P0 R2UR UR6, R0 ;
        |        /*0120*/                   @!UP0 SYNCS.EXCH.64 URZ, [UR6], UR4 ;
        |        /*0130*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0140*/               @P0 BRA `(.L_x_1) ;
        |        /*0150*/                   S2UR UR12, SR_CgaCtaId ;
        |        /*0160*/                   LDCU.64 UR6, c[0x0][0x348] ;
        |        /*0170*/                   LDCU.64 UR10, c[0x0][0x508] ;
        |        /*0180*/                   UMOV UR5, 0x400 ;
        |        /*0190*/                   UIADD3 UR9, UPT, UPT, UR5, 0x400, URZ ;
        |        /*01a0*/                   UTMACCTL.PF [UR6] ;
        |        /*01b0*/                   ULEA UR4, UR12, UR5, 0x18 ;
        |        /*01c0*/                   ULEA UR9, UR12, UR9, 0x18 ;
        |        /*01d0*/                   UMOV UR5, URZ ;
        |        /*01e0*/                   USHF.L.U64.HI UR8, UR4, 0x8, URZ ;
        |        /*01f0*/                   UTMAPF.L2.2D [UR10], [UR6] ;
        |        /*0200*/                   UISETP.NE.U32.AND UP0, UPT, UR8, UR12, UPT ;
        |        /*0210*/                   BRA.U !UP0, `(.L_x_2) ;
        |        /*0220*/                   STL.64 [R1], RZ ;
        |        /*0230*/                   LDCU.64 UR10, c[0x0][0x508] ;
        |        /*0240*/                   MOV.64 R2, UR6 ;
        |        /*0250*/                   MOV R0, 0x0 ;
        |        /*0260*/                   MOV.64 R6, UR6 ;
        |        /*0270*/                   HFMA2 R5, -RZ, RZ, 0, 0 ;
        |        /*0280*/                   MOV R4, UR4 ;
        |        /*0290*/                   MOV R7, R3 ;
        |        /*02a0*/                   LDC.64 R2, c[0x4][R0] ;
        |        /*02b0*/                   HFMA2 R15, -RZ, RZ, 0, 0 ;
        |        /*02c0*/                   MOV R14, UR9 ;
        |        /*02d0*/                   MOV R8, UR10 ;
        |        /*02e0*/                   MOV R9, UR11 ;
        |        /*02f0*/                   LEPC R20, `(.L_x_3) ;
        |        /*0300*/                   CALL.ABS.NOINC R2 ;
        |.L_x_3:
        |        /*0310*/                   BRA `(.L_x_4) ;
        |.L_x_2:
        |        /*0320*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*0330*/                   UMOV UR4, 0x400 ;
        |        /*0340*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*0350*/                   UIADD3 UR9, UPT, UPT, UR4, 0x400, URZ ;
        |        /*0360*/                   LDCU.64 UR10, c[0x0][0x508] ;
        |        /*0370*/                   ULEA UR8, UR5, UR4, 0x18 ;
        |        /*0380*/                   ULEA UR9, UR5, UR9, 0x18 ;
        |.L_x_5:
        |        /*0390*/               @P0 ELECT P1, URZ, PT ;
        |        /*03a0*/                   UTMALDG.2D [UR8], [UR6] ;
        |        /*03b0*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*03c0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*03d0*/               @P0 BRA.U.ANY `(.L_x_5) ;
        |.L_x_4:
        |        /*03e0*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*03f0*/                   UMOV UR4, 0x400 ;
        |        /*0400*/                   HFMA2 R0, -RZ, RZ, 0, 6.103515625e-05 ;
        |        /*0410*/                   UIADD3 UR4, UPT, UPT, UR4, 0x400, URZ ;
        |        /*0420*/                   ULEA UR4, UR5, UR4, 0x18 ;
        |        /*0430*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR4], R0 ;
        |.L_x_1:
        |        /*0440*/                   NOP ;
        |        /*0450*/                   BSYNC.RECONVERGENT B6 ;
        |.L_x_0:
        |        /*0460*/                   S2R R4, SR_CgaCtaId ;
        |        /*0470*/                   MOV R0, 0x400 ;
        |        /*0480*/                   SHF.L.U32 R2, RZ, 0x1f, RZ ;
        |        /*0490*/                   IADD R3, R0, 0x400 ;
        |        /*04a0*/                   LEA R3, R4, R3, 0x18 ;
        |        /*04b0*/                   LEA R7, R4, R0, 0x18 ;
        |        /*04c0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [R3+URZ], R2 ;
        |        /*04d0*/                   IMAD R0, R16, 0x4, R7 ;
        |        /*04e0*/              @!P0 BRA `(.L_x_6) ;
        |.L_x_8:
        |        /*04f0*/                   LDS R5, [R0] ;
        |        /*0500*/                   LDC.64 R2, c[0x0][0x500] ;
        |        /*0510*/                   ISETP.NE.U32.AND P0, PT, R16.reuse, RZ, PT ;
        |        /*0520*/              @!PT LDS RZ, [RZ] ;
        |        /*0530*/              @!PT LDS RZ, [RZ] ;
        |        /*0540*/              @!PT LDS RZ, [RZ] ;
        |        /*0550*/              @!PT LDS RZ, [RZ] ;
        |        /*0560*/                   MEMBAR.ALL.CTA ;
        |        /*0570*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0580*/                   WARPSYNC.ALL ;
        |        /*0590*/                   IMAD.WIDE.U32 R2, R16, 0x4, R2 ;
        |        /*05a0*/                   STG.E desc[UR36][R2.64], R5 ;
        |        /*05b0*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*05c0*/               @P0 EXIT ;
        |        /*05d0*/                   LDCU.64 UR6, c[0x0][0x348] ;
        |        /*05e0*/                   R2UR UR12, R7 ;
        |        /*05f0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*0600*/                   LDCU.64 UR8, c[0x0][0x508] ;
        |        /*0610*/                   UIADD3.64 UR4, UPT, UPT, UR6, 0x80, URZ ;
        |        /*0620*/                   UIADD3.64 UR6, UPT, UPT, UR6, 0x100, URZ ;
        |        /*0630*/                   UMOV UR13, UR8 ;
        |        /*0640*/                   UMOV UR14, UR9 ;
        |        /*0650*/                   UTMASTG.2D [UR12], [UR4] ;
        |        /*0660*/                   NOP ;
        |.L_x_7:
        |        /*0670*/               @P0 ELECT P1, URZ, PT ;
        |        /*0680*/                   UMOV UR13, UR8 ;
        |        /*0690*/                   UMOV UR14, UR9 ;
        |        /*06a0*/                   UTMAREDG.2D.ADD [UR12], [UR6] ;
        |        /*06b0*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*06c0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*06d0*/               @P0 BRA.U.ANY `(.L_x_7) ;
        |        /*06e0*/                   UTMACMDFLUSH ;
        |        /*06f0*/                   DEPBAR.LE SB0, 0x0 ;
        |        /*0700*/                   EXIT ;
        |.L_x_6:
        |        /*0710*/                   YIELD ;
        |        /*0720*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [R3+URZ], R2 ;
        |        /*0730*/              @!P0 BRA `(.L_x_6) ;
        |        /*0740*/                   BRA `(.L_x_8) ;
        |.L_x_9:
        |        /*0750*/                   BRA `(.L_x_9);
        |        /*0760*/                   NOP;
        |        /*0770*/                   NOP;
        |        /*0780*/                   NOP;
        |        /*0790*/                   NOP;
        |        /*07a0*/                   NOP;
        |        /*07b0*/                   NOP;
        |        /*07c0*/                   NOP;
        |        /*07d0*/                   NOP;
        |        /*07e0*/                   NOP;
        |        /*07f0*/                   NOP;
        |.L_x_10:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |	.type		__cuda_syscall_cp_async_bulk_tensor_2d_tile_unicast,@function
        |""".stripMargin,
    "columnsSm90a" ->
      """	.target	sm_90a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.columns             --------------------------
        |	.section	.text.columns,"ax",@progbits
        |	.align	128
        |        .global         columns
        |        .type           columns,@function
        |        .size           columns,(.L_x_5 - columns)
        |        .other          columns,@"STO_CUDA_ENTRY STV_DEFAULT"
        |columns:
        |.text.columns:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R7, SR_TID.X ;
        |        /*0020*/                   ULDC.64 UR14, c[0x0][0x208] ;
        |        /*0030*/                   BSSY B0, `(.L_x_0) ;
        |        /*0040*/                   ISETP.NE.U32.AND P0, PT, R7, RZ, PT ;
        |        /*0050*/               @P0 BRA `(.L_x_1) ;
        |        /*0060*/                   S2UR UR6, SR_CgaCtaId ;
        |        /*0070*/                   UMOV UR8, 0x400 ;
        |        /*0080*/                   UMOV UR4, 0x1 ;
        |        /*0090*/                   UIADD3 UR9, UR8, 0x200, URZ ;
        |        /*00a0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x0 ;
        |        /*00b0*/                   UIADD3 UR4, -UR4, 0x100000, URZ ;
        |        /*00c0*/                   USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00d0*/                   USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00e0*/                   UMOV UR10, URZ ;
        |        /*00f0*/                   UMOV UR11, 0xffffffff ;
        |        /*0100*/                   UMOV UR12, URZ ;
        |        /*0110*/                   ULEA UR9, UR6, UR9, 0x18 ;
        |        /*0120*/                   ULEA UR8, UR6, UR8, 0x18 ;
        |        /*0130*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0140*/                   SYNCS.EXCH.64 URZ, [UR9], UR4 ;
        |        /*0150*/                   ULDC.64 UR4, c[0x0][0x198] ;
        |        /*0160*/                   UIADD3 UR16, UP0, UR4, 0x30, URZ ;
        |        /*0170*/                   ULDC.U16 UR4, c[0x0][0x2c8] ;
        |        /*0180*/                   UIADD3.X UR17, URZ, UR5, URZ, UP0, !UPT ;
        |        /*0190*/                   UPRMT UR4, UR4, 0x5410, URZ ;
        |.L_x_2:
        |        /*01a0*/               @P0 ELECT P1, URZ, PT ;
        |        /*01b0*/                   UTMALDG.3D.IM2COL [UR8], [UR16], UR4 ;
        |        /*01c0*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x0 ;
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;
        |        /*01e0*/               @P0 BRA.U.ANY `(.L_x_2) ;
        |        /*01f0*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x200 ;
        |        /*0200*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR9], R0 ;
        |.L_x_1:
        |        /*0210*/                   BSYNC B0 ;
        |.L_x_0:
        |        /*0220*/                   S2UR UR6, SR_CgaCtaId ;
        |        /*0230*/                   UMOV UR4, 0x400 ;
        |        /*0240*/                   SHF.L.U32 R0, RZ, 0x1f, RZ ;
        |        /*0250*/                   UIADD3 UR5, UR4, 0x200, URZ ;
        |        /*0260*/                   ULEA UR5, UR6, UR5, 0x18 ;
        |.L_x_3:
        |        /*0270*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0280*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*0290*/              @!P0 BRA `(.L_x_3) ;
        |        /*02a0*/                   ULEA UR4, UR6, UR4, 0x18 ;
        |        /*02b0*/                   LDC.64 R2, c[0x0][0x2c0] ;
        |        /*02c0*/                   LEA R0, R7, UR4, 0x2 ;
        |        /*02d0*/                   LDS R5, [R0] ;
        |        /*02e0*/                   IMAD.WIDE.U32 R2, R7, 0x4, R2 ;
        |        /*02f0*/                   STG.E desc[UR14][R2.64], R5 ;
        |        /*0300*/                   EXIT ;
        |.L_x_4:
        |        /*0310*/                   BRA `(.L_x_4);
        |        /*0320*/                   NOP;
        |        /*0330*/                   NOP;
        |        /*0340*/                   NOP;
        |        /*0350*/                   NOP;
        |        /*0360*/                   NOP;
        |        /*0370*/                   NOP;
        |        /*0380*/                   NOP;
        |        /*0390*/                   NOP;
        |        /*03a0*/                   NOP;
        |        /*03b0*/                   NOP;
        |        /*03c0*/                   NOP;
        |        /*03d0*/                   NOP;
        |        /*03e0*/                   NOP;
        |        /*03f0*/                   NOP;
        |.L_x_5:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    // ptxas 13.3 -arch=sm_120a on columnsPtx at PTX ISA 8.7, nvdisasm 13.3 -c -ndf
    "columnsSm120a" ->
      """	.target	sm_120a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.columns             --------------------------
        |	.section	.text.columns,"ax",@progbits
        |	.align	128
        |        .global         columns
        |        .type           columns,@function
        |        .size           columns,(.L_x_8 - columns)
        |        .other          columns,@"STO_CUDA_ENTRY STV_DEFAULT"
        |columns:
        |.text.columns:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R16, SR_TID.X ;
        |        /*0020*/                   LDCU.64 UR36, c[0x0][0x358] ;
        |        /*0030*/                   BSSY.RECONVERGENT B6, `(.L_x_0) ;
        |        /*0040*/                   IADD R1, R1, -0x10 ;
        |        /*0050*/                   ISETP.NE.U32.AND P0, PT, R16, RZ, PT ;
        |        /*0060*/               @P0 BRA `(.L_x_1) ;
        |        /*0070*/                   S2UR UR8, SR_CgaCtaId ;
        |        /*0080*/                   UMOV UR6, 0x400 ;
        |        /*0090*/                   UMOV UR4, 0x1 ;
        |        /*00a0*/                   UIADD3 UR7, UPT, UPT, UR6, 0x200, URZ ;
        |        /*00b0*/                   UIADD3 UR4, UPT, UPT, -UR4, 0x100000, URZ ;
        |        /*00c0*/                   USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00d0*/                   USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00e0*/                   LDCU.64 UR14, c[0x0][0x348] ;
        |        /*00f0*/                   ULEA UR7, UR8, UR7, 0x18 ;
        |        /*0100*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0110*/                   SYNCS.EXCH.64 URZ, [UR7], UR4 ;
        |        /*0120*/                   ULEA UR4, UR8, UR6, 0x18 ;
        |        /*0130*/                   UMOV UR5, URZ ;
        |        /*0140*/                   USHF.L.U64.HI UR6, UR4, 0x8, URZ ;
        |        /*0150*/                   UISETP.NE.U32.AND UP0, UPT, UR6, UR8, UPT ;
        |        /*0160*/                   BRA.U !UP0, `(.L_x_2) ;
        |        /*0170*/                   LDC.U16 R2, c[0x0][0x408] ;
        |        /*0180*/                   STL.64 [R1+0x8], RZ ;
        |        /*0190*/                   MOV.64 R4, UR14 ;
        |        /*01a0*/                   MOV R0, 0x0 ;
        |        /*01b0*/                   MOV.64 R6, UR14 ;
        |        /*01c0*/                   HFMA2 R10, -RZ, RZ, 0, 0 ;
        |        /*01d0*/                   MOV R4, UR4 ;
        |        /*01e0*/                   MOV R7, R5 ;
        |        /*01f0*/                   HFMA2 R5, -RZ, RZ, 0, 0 ;
        |        /*0200*/                   MOV R8, RZ ;
        |        /*0210*/                   HFMA2 R15, -RZ, RZ, 0, 0 ;
        |        /*0220*/                   MOV R9, 0xffffffff ;
        |        /*0230*/                   MOV R14, UR7 ;
        |        /*0240*/                   STL.U16 [R1], R2 ;
        |        /*0250*/                   LDC.64 R2, c[0x4][R0] ;
        |        /*0260*/                   LEPC R20, `(.L_x_3) ;
        |        /*0270*/                   CALL.ABS.NOINC R2 ;
        |.L_x_3:
        |        /*0280*/                   BRA `(.L_x_4) ;
        |.L_x_2:
        |        /*0290*/                   S2UR UR9, SR_CgaCtaId ;
        |        /*02a0*/                   LDCU.U16 UR5, c[0x0][0x408] ;
        |        /*02b0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*02c0*/                   UMOV UR8, 0x400 ;
        |        /*02d0*/                   UMOV UR10, URZ ;
        |        /*02e0*/                   UIADD3 UR4, UPT, UPT, UR8, 0x200, URZ ;
        |        /*02f0*/                   UMOV UR11, 0xffffffff ;
        |        /*0300*/                   UMOV UR12, URZ ;
        |        /*0310*/                   ULEA UR8, UR9, UR8, 0x18 ;
        |        /*0320*/                   ULEA UR9, UR9, UR4, 0x18 ;
        |        /*0330*/                   UPRMT UR4, UR5, 0x5410, URZ ;
        |.L_x_5:
        |        /*0340*/               @P0 ELECT P1, URZ, PT ;
        |        /*0350*/                   UTMALDG.3D.IM2COL [UR8], [UR14], UR4 ;
        |        /*0360*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*0370*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0380*/               @P0 BRA.U.ANY `(.L_x_5) ;
        |.L_x_4:
        |        /*0390*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*03a0*/                   UMOV UR4, 0x400 ;
        |        /*03b0*/                   HFMA2 R0, -RZ, RZ, 0, 3.0517578125e-05 ;
        |        /*03c0*/                   UIADD3 UR4, UPT, UPT, UR4, 0x200, URZ ;
        |        /*03d0*/                   ULEA UR4, UR5, UR4, 0x18 ;
        |        /*03e0*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR4], R0 ;
        |.L_x_1:
        |        /*03f0*/                   NOP ;
        |        /*0400*/                   BSYNC.RECONVERGENT B6 ;
        |.L_x_0:
        |        /*0410*/                   S2R R4, SR_CgaCtaId ;
        |        /*0420*/                   MOV R2, 0x400 ;
        |        /*0430*/                   SHF.L.U32 R0, RZ, 0x1f, RZ ;
        |        /*0440*/                   IADD R3, R2, 0x200 ;
        |        /*0450*/                   LEA R3, R4, R3, 0x18 ;
        |.L_x_6:
        |        /*0460*/                   YIELD ;
        |        /*0470*/                   WARPSYNC.ALL ;
        |        /*0480*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0490*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [R3+URZ], R0 ;
        |        /*04a0*/              @!P0 BRA `(.L_x_6) ;
        |        /*04b0*/                   LEA R3, R4, R2, 0x18 ;
        |        /*04c0*/                   IMAD R0, R16.reuse, 0x4, R3 ;
        |        /*04d0*/                   LDC.64 R2, c[0x0][0x400] ;
        |        /*04e0*/                   LDS R5, [R0] ;
        |        /*04f0*/                   IMAD.WIDE.U32 R16, R16, 0x4, R2 ;
        |        /*0500*/                   STG.E desc[UR36][R16.64], R5 ;
        |        /*0510*/                   EXIT ;
        |.L_x_7:
        |        /*0520*/                   BRA `(.L_x_7);
        |        /*0530*/                   NOP;
        |        /*0540*/                   NOP;
        |        /*0550*/                   NOP;
        |        /*0560*/                   NOP;
        |        /*0570*/                   NOP;
        |        /*0580*/                   NOP;
        |        /*0590*/                   NOP;
        |        /*05a0*/                   NOP;
        |        /*05b0*/                   NOP;
        |        /*05c0*/                   NOP;
        |        /*05d0*/                   NOP;
        |        /*05e0*/                   NOP;
        |        /*05f0*/                   NOP;
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |	.type		__cuda_syscall_cp_async_bulk_tensor_3d_im2col_unicast,@function
        |""".stripMargin,
    "columnsSm100a" ->
      """	.target	sm_100a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.columns             --------------------------
        |	.section	.text.columns,"ax",@progbits
        |	.align	128
        |        .global         columns
        |        .type           columns,@function
        |        .size           columns,(.L_x_5 - columns)
        |        .other          columns,@"STO_CUDA_ENTRY STV_DEFAULT"
        |columns:
        |.text.columns:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R7, SR_TID.X ;
        |        /*0020*/                   LDCU.64 UR14, c[0x0][0x358] ;
        |        /*0030*/                   BSSY.RECONVERGENT B0, `(.L_x_0) ;
        |        /*0040*/                   ISETP.NE.U32.AND P0, PT, R7, RZ, PT ;
        |        /*0050*/               @P0 BRA `(.L_x_1) ;
        |        /*0060*/                   S2UR UR6, SR_CgaCtaId ;
        |        /*0070*/                   UMOV UR8, 0x400 ;
        |        /*0080*/                   UMOV UR4, 0x1 ;
        |        /*0090*/                   UIADD3 UR9, UPT, UPT, UR8, 0x200, URZ ;
        |        /*00a0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*00b0*/                   UIADD3 UR4, UPT, UPT, -UR4, 0x100000, URZ ;
        |        /*00c0*/                   USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00d0*/                   USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00e0*/                   LDCU.64 UR16, c[0x0][0x348] ;
        |        /*00f0*/                   UMOV UR10, URZ ;
        |        /*0100*/                   UMOV UR11, 0xffffffff ;
        |        /*0110*/                   UMOV UR12, URZ ;
        |        /*0120*/                   ULEA UR9, UR6, UR9, 0x18 ;
        |        /*0130*/                   ULEA UR8, UR6, UR8, 0x18 ;
        |        /*0140*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0150*/                   SYNCS.EXCH.64 URZ, [UR9], UR4 ;
        |        /*0160*/                   LDCU.U16 UR4, c[0x0][0x408] ;
        |        /*0170*/                   UPRMT UR4, UR4, 0x5410, URZ ;
        |.L_x_2:
        |        /*0180*/               @P0 ELECT P1, URZ, PT ;
        |        /*0190*/                   UTMALDG.3D.IM2COL [UR8], [UR16], UR4 ;
        |        /*01a0*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*01b0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*01c0*/               @P0 BRA.U.ANY `(.L_x_2) ;
        |        /*01d0*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x200 ;
        |        /*01e0*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR9], R0 ;
        |.L_x_1:
        |        /*01f0*/                   NOP ;
        |        /*0200*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_0:
        |        /*0210*/                   S2UR UR6, SR_CgaCtaId ;
        |        /*0220*/                   UMOV UR4, 0x400 ;
        |        /*0230*/                   SHF.L.U32 R0, RZ, 0x1f, RZ ;
        |        /*0240*/                   UIADD3 UR5, UPT, UPT, UR4, 0x200, URZ ;
        |        /*0250*/                   ULEA UR5, UR6, UR5, 0x18 ;
        |.L_x_3:
        |        /*0260*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0270*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*0280*/              @!P0 BRA `(.L_x_3) ;
        |        /*0290*/                   ULEA UR4, UR6, UR4, 0x18 ;
        |        /*02a0*/                   LDC.64 R2, c[0x0][0x400] ;
        |        /*02b0*/                   LEA R0, R7, UR4, 0x2 ;
        |        /*02c0*/                   LDS R5, [R0] ;
        |        /*02d0*/                   IMAD.WIDE.U32 R2, R7, 0x4, R2 ;
        |        /*02e0*/                   STG.E desc[UR14][R2.64], R5 ;
        |        /*02f0*/                   EXIT ;
        |.L_x_4:
        |        /*0300*/                   BRA `(.L_x_4);
        |        /*0310*/                   NOP;
        |        /*0320*/                   NOP;
        |        /*0330*/                   NOP;
        |        /*0340*/                   NOP;
        |        /*0350*/                   NOP;
        |        /*0360*/                   NOP;
        |        /*0370*/                   NOP;
        |        /*0380*/                   NOP;
        |        /*0390*/                   NOP;
        |        /*03a0*/                   NOP;
        |        /*03b0*/                   NOP;
        |        /*03c0*/                   NOP;
        |        /*03d0*/                   NOP;
        |        /*03e0*/                   NOP;
        |        /*03f0*/                   NOP;
        |.L_x_5:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "replaceSm90a" ->
      """	.target	sm_90a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.replace             --------------------------
        |	.section	.text.replace,"ax",@progbits
        |	.align	128
        |        .global         replace
        |        .type           replace,@function
        |        .size           replace,(.L_x_7 - replace)
        |        .other          replace,@"STO_CUDA_ENTRY STV_DEFAULT"
        |replace:
        |.text.replace:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   S2UR UR11, SR_CgaCtaId ;
        |        /*0030*/                   UMOV UR7, 0x400 ;
        |        /*0040*/                   BSSY B0, `(.L_x_0) ;
        |        /*0050*/                   ISETP.NE.U32.AND P0, PT, R0, RZ, PT ;
        |        /*0060*/               @P0 BRA `(.L_x_1) ;
        |        /*0070*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*0080*/                   UMOV UR4, 0x400 ;
        |        /*0090*/                   LDC.64 R4, c[0x0][0x240] ;
        |        /*00a0*/                   LDC.64 R6, c[0x0][0x248] ;
        |        /*00b0*/                   LDC.64 R8, c[0x0][0x250] ;
        |        /*00c0*/                   ULEA UR4, UR5, UR4, 0x18 ;
        |        /*00d0*/                   LDC.64 R10, c[0x0][0x258] ;
        |        /*00e0*/                   LDC.64 R12, c[0x0][0x260] ;
        |        /*00f0*/                   STS.128 [UR4], R4 ;
        |        /*0100*/                   LDC.64 R14, c[0x0][0x268] ;
        |        /*0110*/                   LDC.64 R16, c[0x0][0x270] ;
        |        /*0120*/                   STS.128 [UR4+0x10], R8 ;
        |        /*0130*/                   LDC.64 R18, c[0x0][0x278] ;
        |        /*0140*/                   LDC.64 R20, c[0x0][0x280] ;
        |        /*0150*/                   STS.128 [UR4+0x20], R12 ;
        |        /*0160*/                   LDC.64 R22, c[0x0][0x288] ;
        |        /*0170*/                   STS.128 [UR4+0x30], R16 ;
        |        /*0180*/                   STS.128 [UR4+0x40], R20 ;
        |.L_x_1:
        |        /*0190*/                   BSYNC B0 ;
        |.L_x_0:
        |        /*01a0*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*01b0*/                   ULEA UR4, UR11, UR7, 0x18 ;
        |        /*01c0*/                   IMAD.MOV.U32 R7, RZ, RZ, -0x1000000 ;
        |        /*01d0*/                   IMAD.MOV.U32 R8, RZ, RZ, 0xb ;
        |        /*01e0*/                   LDC.64 R4, c[0x0][0x2c8] ;
        |        /*01f0*/                   S2R R3, SR_LANEID ;
        |        /*0200*/                   LDS R2, [UR4+0x34] ;
        |        /*0210*/                   STS [UR4+0x20], R8 ;
        |        /*0220*/                   STS.64 [UR4], R4 ;
        |        /*0230*/                   LOP3.LUT R6, R2, 0xf000000, R7, 0xd8, !PT ;
        |        /*0240*/                   IMAD.SHL.U32 R7, R3, 0x4, RZ ;
        |        /*0250*/                   STS [UR4+0x34], R6 ;
        |        /*0260*/                   LDS R9, [R7+UR4] ;
        |        /*0270*/                   ULDC.64 UR4, c[0x0][0x2c0] ;
        |        /*0280*/                   IADD3 R2, P1, R7, UR4, RZ ;
        |        /*0290*/                   ULDC.64 UR12, c[0x0][0x2c0] ;
        |        /*02a0*/                   IMAD.X R3, RZ, RZ, UR5, P1 ;
        |        /*02b0*/                   BSSY B0, `(.L_x_2) ;
        |        /*02c0*/                   ATOMG.E.EXCH.STRONG.GPU PT, RZ, [R2], R9 ;
        |        /*02d0*/                   DEPBAR {5,4,3,2,1,0} ;
        |        /*02e0*/                   UTMACCTL.IV [UR12] ;
        |        /*02f0*/                   UTMACMDFLUSH ;
        |        /*0300*/                   NOP ;
        |        /*0310*/               @P0 BRA `(.L_x_3) ;
        |        /*0320*/                   UIADD3 UR6, UR7, 0xc0, URZ ;
        |        /*0330*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x0 ;
        |        /*0340*/                   UMOV UR4, 0x1 ;
        |        /*0350*/                   UMOV UR10, URZ ;
        |        /*0360*/                   UIADD3 UR4, -UR4, 0x100000, URZ ;
        |        /*0370*/                   USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*0380*/                   USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*0390*/                   ULEA UR9, UR11, UR6, 0x18 ;
        |        /*03a0*/                   FENCE.VIEW.ASYNC.S ;
        |        /*03b0*/                   SYNCS.EXCH.64 URZ, [UR9], UR4 ;
        |        /*03c0*/                   UIADD3 UR4, UR7, 0x80, URZ ;
        |        /*03d0*/                   ULEA UR8, UR11, UR4, 0x18 ;
        |.L_x_4:
        |        /*03e0*/               @P0 ELECT P1, URZ, PT ;
        |        /*03f0*/                   UTMALDG.1D [UR8], [UR12] ;
        |        /*0400*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x0 ;
        |        /*0410*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;
        |        /*0420*/               @P0 BRA.U.ANY `(.L_x_4) ;
        |        /*0430*/                   IMAD.MOV.U32 R2, RZ, RZ, 0x40 ;
        |        /*0440*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR9], R2 ;
        |.L_x_3:
        |        /*0450*/                   BSYNC B0 ;
        |.L_x_2:
        |        /*0460*/                   UIADD3 UR4, UR7, 0xc0, URZ ;
        |        /*0470*/                   SHF.L.U32 R2, RZ, 0x1f, RZ ;
        |        /*0480*/                   ULEA UR4, UR11, UR4, 0x18 ;
        |.L_x_5:
        |        /*0490*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*04a0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR4], R2 ;
        |        /*04b0*/              @!P0 BRA `(.L_x_5) ;
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R0, 0x10, PT ;
        |        /*04d0*/               @P0 EXIT ;
        |        /*04e0*/                   UIADD3 UR4, UR7, 0x80, URZ ;
        |        /*04f0*/                   LDC.64 R2, c[0x0][0x2d0] ;
        |        /*0500*/                   ULEA UR4, UR11, UR4, 0x18 ;
        |        /*0510*/                   LEA R4, R0, UR4, 0x2 ;
        |        /*0520*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0530*/                   LDS R5, [R4] ;
        |        /*0540*/                   IMAD.WIDE.U32 R2, R0, 0x4, R2 ;
        |        /*0550*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*0560*/                   EXIT ;
        |.L_x_6:
        |        /*0570*/                   BRA `(.L_x_6);
        |        /*0580*/                   NOP;
        |        /*0590*/                   NOP;
        |        /*05a0*/                   NOP;
        |        /*05b0*/                   NOP;
        |        /*05c0*/                   NOP;
        |        /*05d0*/                   NOP;
        |        /*05e0*/                   NOP;
        |        /*05f0*/                   NOP;
        |.L_x_7:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "replaceSm100a" ->
      """	.target	sm_100a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.replace             --------------------------
        |	.section	.text.replace,"ax",@progbits
        |	.align	128
        |        .global         replace
        |        .type           replace,@function
        |        .size           replace,(.L_x_7 - replace)
        |        .other          replace,@"STO_CUDA_ENTRY STV_DEFAULT"
        |replace:
        |.text.replace:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   S2UR UR11, SR_CgaCtaId ;
        |        /*0030*/                   UMOV UR7, 0x400 ;
        |        /*0040*/                   BSSY.RECONVERGENT B0, `(.L_x_0) ;
        |        /*0050*/                   ISETP.NE.U32.AND P0, PT, R0, RZ, PT ;
        |        /*0060*/               @P0 BRA `(.L_x_1) ;
        |        /*0070*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*0080*/                   UMOV UR4, 0x400 ;
        |        /*0090*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*00a0*/                   LDC.64 R6, c[0x0][0x388] ;
        |        /*00b0*/                   LDC.64 R8, c[0x0][0x390] ;
        |        /*00c0*/                   ULEA UR4, UR5, UR4, 0x18 ;
        |        /*00d0*/                   LDC.64 R10, c[0x0][0x398] ;
        |        /*00e0*/                   LDC.64 R12, c[0x0][0x3a0] ;
        |        /*00f0*/                   STS.128 [UR4], R4 ;
        |        /*0100*/                   LDC.64 R14, c[0x0][0x3a8] ;
        |        /*0110*/                   LDC.64 R16, c[0x0][0x3b0] ;
        |        /*0120*/                   STS.128 [UR4+0x10], R8 ;
        |        /*0130*/                   LDC.64 R18, c[0x0][0x3b8] ;
        |        /*0140*/                   LDC.64 R20, c[0x0][0x3c0] ;
        |        /*0150*/                   STS.128 [UR4+0x20], R12 ;
        |        /*0160*/                   LDC.64 R22, c[0x0][0x3c8] ;
        |        /*0170*/                   STS.128 [UR4+0x30], R16 ;
        |        /*0180*/                   STS.128 [UR4+0x40], R20 ;
        |.L_x_1:
        |        /*0190*/                   NOP ;
        |        /*01a0*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_0:
        |        /*01b0*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*01c0*/                   ULEA UR4, UR11, UR7, 0x18 ;
        |        /*01d0*/                   IMAD.MOV.U32 R7, RZ, RZ, -0x1000000 ;
        |        /*01e0*/                   IMAD.MOV.U32 R8, RZ, RZ, 0xb ;
        |        /*01f0*/                   LDC.64 R4, c[0x0][0x408] ;
        |        /*0200*/                   S2R R3, SR_LANEID ;
        |        /*0210*/                   LDS R2, [UR4+0x34] ;
        |        /*0220*/                   STS [UR4+0x20], R8 ;
        |        /*0230*/                   STS.64 [UR4], R4 ;
        |        /*0240*/                   LDCU.64 UR12, c[0x0][0x400] ;
        |        /*0250*/                   LOP3.LUT R6, R2, 0xf000000, R7, 0xd8, !PT ;
        |        /*0260*/                   IMAD.SHL.U32 R7, R3, 0x4, RZ ;
        |        /*0270*/                   STS [UR4+0x34], R6 ;
        |        /*0280*/                   LDS R9, [R7+UR4] ;
        |        /*0290*/                   LDCU.64 UR4, c[0x0][0x400] ;
        |        /*02a0*/                   BSSY.RECONVERGENT B0, `(.L_x_2) ;
        |        /*02b0*/                   IADD3 R2, P1, PT, R7, UR4, RZ ;
        |        /*02c0*/                   IMAD.X R3, RZ, RZ, UR5, P1 ;
        |        /*02d0*/                   ATOMG.E.EXCH.STRONG.GPU PT, RZ, [R2], R9 ;
        |        /*02e0*/                   DEPBAR {5,4,3,2,1,0} ;
        |        /*02f0*/                   CCTL.E.C.LDCU.IV.DEEP [UR12] ;
        |        /*0300*/                   UTMACCTL.IV [UR12] ;
        |        /*0310*/                   UTMACMDFLUSH ;
        |        /*0320*/                   NOP ;
        |        /*0330*/               @P0 BRA `(.L_x_3) ;
        |        /*0340*/                   UIADD3 UR6, UPT, UPT, UR7, 0xc0, URZ ;
        |        /*0350*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x8 ;
        |        /*0360*/                   UMOV UR4, 0x1 ;
        |        /*0370*/                   UMOV UR10, URZ ;
        |        /*0380*/                   UIADD3 UR4, UPT, UPT, -UR4, 0x100000, URZ ;
        |        /*0390*/                   USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*03a0*/                   USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*03b0*/                   ULEA UR9, UR11, UR6, 0x18 ;
        |        /*03c0*/                   FENCE.VIEW.ASYNC.S ;
        |        /*03d0*/                   SYNCS.EXCH.64 URZ, [UR9], UR4 ;
        |        /*03e0*/                   UIADD3 UR4, UPT, UPT, UR7, 0x80, URZ ;
        |        /*03f0*/                   ULEA UR8, UR11, UR4, 0x18 ;
        |.L_x_4:
        |        /*0400*/               @P0 ELECT P1, URZ, PT ;
        |        /*0410*/                   UTMALDG.1D [UR8], [UR12] ;
        |        /*0420*/               @P1 PLOP3.LUT P0, PT, P1, PT, PT, 0x8, 0x80 ;
        |        /*0430*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x80 ;
        |        /*0440*/               @P0 BRA.U.ANY `(.L_x_4) ;
        |        /*0450*/                   IMAD.MOV.U32 R2, RZ, RZ, 0x40 ;
        |        /*0460*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR9], R2 ;
        |.L_x_3:
        |        /*0470*/                   NOP ;
        |        /*0480*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_2:
        |        /*0490*/                   UIADD3 UR4, UPT, UPT, UR7, 0xc0, URZ ;
        |        /*04a0*/                   SHF.L.U32 R2, RZ, 0x1f, RZ ;
        |        /*04b0*/                   ULEA UR4, UR11, UR4, 0x18 ;
        |.L_x_5:
        |        /*04c0*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*04d0*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR4], R2 ;
        |        /*04e0*/              @!P0 BRA `(.L_x_5) ;
        |        /*04f0*/                   ISETP.GE.U32.AND P0, PT, R0, 0x10, PT ;
        |        /*0500*/               @P0 EXIT ;
        |        /*0510*/                   UIADD3 UR4, UPT, UPT, UR7, 0x80, URZ ;
        |        /*0520*/                   LDC.64 R2, c[0x0][0x410] ;
        |        /*0530*/                   ULEA UR4, UR11, UR4, 0x18 ;
        |        /*0540*/                   LEA R4, R0, UR4, 0x2 ;
        |        /*0550*/                   LDS R5, [R4] ;
        |        /*0560*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0570*/                   IMAD.WIDE.U32 R2, R0, 0x4, R2 ;
        |        /*0580*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*0590*/                   EXIT ;
        |.L_x_6:
        |        /*05a0*/                   BRA `(.L_x_6);
        |        /*05b0*/                   NOP;
        |        /*05c0*/                   NOP;
        |        /*05d0*/                   NOP;
        |        /*05e0*/                   NOP;
        |        /*05f0*/                   NOP;
        |        /*0600*/                   NOP;
        |        /*0610*/                   NOP;
        |        /*0620*/                   NOP;
        |        /*0630*/                   NOP;
        |        /*0640*/                   NOP;
        |        /*0650*/                   NOP;
        |        /*0660*/                   NOP;
        |        /*0670*/                   NOP;
        |.L_x_7:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "exchangeSm90a" ->
      """	.target	sm_90a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.exchange            --------------------------
        |	.section	.text.exchange,"ax",@progbits
        |	.align	128
        |        .global         exchange
        |        .type           exchange,@function
        |        .size           exchange,(.L_x_9 - exchange)
        |        .other          exchange,@"STO_CUDA_ENTRY STV_DEFAULT"
        |exchange:
        |.text.exchange:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R7, SR_CgaCtaId ;
        |        /*0020*/                   LDC R6, c[0x0][0x140] ;
        |        /*0030*/                   MOV R2, 0x400 ;
        |        /*0040*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0050*/                   S2R R0, SR_TID.X ;
        |        /*0060*/                   S2R R13, SR_CTAID.X ;
        |        /*0070*/                   ISETP.EQ.U32.AND P1, PT, R6, 0x1, PT ;
        |        /*0080*/                   LEA R3, R7, R2, 0x18 ;
        |        /*0090*/                   IMAD R11, R0, 0x4, R3 ;
        |        /*00a0*/                   IMAD R2, R13, 0x3e8, R0 ;
        |        /*00b0*/                   STS [R11], R2 ;
        |        /*00c0*/              @!P1 BRA `(.L_x_0) ;
        |        /*00d0*/                   MEMBAR.ALL.GPU ;
        |        /*00e0*/                   ERRBAR;
        |        /*00f0*/                   CGAERRBAR ;
        |        /*0100*/                   UCGABAR_ARV ;
        |        /*0110*/                   BRA `(.L_x_1) ;
        |.L_x_0:
        |        /*0120*/                   NOP ;
        |.L_x_1:
        |        /*0130*/              @!P1 BRA `(.L_x_2) ;
        |        /*0140*/                   UCGABAR_WAIT ;
        |        /*0150*/                   CCTL.IVALL ;
        |        /*0160*/                   BRA `(.L_x_3) ;
        |.L_x_2:
        |        /*0170*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |.L_x_3:
        |        /*0180*/                   S2R R5, SR_SWINHI ;
        |        /*0190*/                   LOP3.LUT R4, R7, 0x1, RZ, 0x3c, !PT ;
        |        /*01a0*/                   IMAD.MOV.U32 R17, RZ, RZ, 0x1 ;
        |        /*01b0*/                   PRMT R12, R4, 0x654, R11 ;
        |        /*01c0*/                   MOV R2, R3 ;
        |        /*01d0*/                   MOV R3, R5.reuse ;
        |        /*01e0*/                   MOV R14, R12 ;
        |        /*01f0*/                   MOV R15, R5 ;
        |        /*0200*/                   PRMT R2, R4, 0x654, R2 ;
        |        /*0210*/                   LD.E R8, [R14] ;
        |        /*0220*/                   IMAD.WIDE.U32 R4, R0, 0x4, R2 ;
        |        /*0230*/                   QSPC.E.D P0, RZ, [R2] ;
        |        /*0240*/                   LD.E R9, desc[UR4][R4.64] ;
        |        /*0250*/                   SHF.L.U64.HI R12, R12, 0x8, RZ ;
        |        /*0260*/                   ATOM.E.ADD.STRONG.GPU PT, R10, [R14], R17 ;
        |        /*0270*/              @!P1 BRA `(.L_x_4) ;
        |        /*0280*/                   MEMBAR.ALL.GPU ;
        |        /*0290*/                   ERRBAR;
        |        /*02a0*/                   CGAERRBAR ;
        |        /*02b0*/                   UCGABAR_ARV ;
        |        /*02c0*/                   BRA `(.L_x_5) ;
        |.L_x_4:
        |        /*02d0*/                   NOP ;
        |.L_x_5:
        |        /*02e0*/              @!P1 BRA `(.L_x_6) ;
        |        /*02f0*/                   UCGABAR_WAIT ;
        |        /*0300*/                   CCTL.IVALL ;
        |        /*0310*/                   BRA `(.L_x_7) ;
        |.L_x_6:
        |        /*0320*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |.L_x_7:
        |        /*0330*/                   LDS R11, [R11] ;
        |        /*0340*/                   I2FP.F32.U32 R4, R13 ;
        |        /*0350*/                   ULDC UR6, c[0x0][0x150] ;
        |        /*0360*/                   LDC.64 R2, c[0x0][0x210] ;
        |        /*0370*/                   ISETP.NE.AND P1, PT, R6, 0x1, PT ;
        |        /*0380*/                   IMAD R17, R13, 0x20, R0 ;
        |        /*0390*/                   FMUL R15, R4, UR6 ;
        |        /*03a0*/                   SEL R6, RZ, 0x1, P1 ;
        |        /*03b0*/                   LDC R14, c[0x0][0x144] ;
        |        /*03c0*/                   F2I.U32.TRUNC.NTZ R15, R15 ;
        |        /*03d0*/                   SEL R6, R6, 0x7, P0 ;
        |        /*03e0*/                   LDC R4, c[0x0][0x15c] ;
        |        /*03f0*/                   LDC R5, c[0x0][0x188] ;
        |        /*0400*/                   IMAD.MOV R0, RZ, RZ, -R15 ;
        |        /*0410*/                   IMAD.WIDE.U32 R2, R17, 0x30, R2 ;
        |        /*0420*/                   IMAD R13, R14, R0, R13 ;
        |        /*0430*/                   STG.E.128 desc[UR4][R2.64], R8 ;
        |        /*0440*/                   STG.E.128 desc[UR4][R2.64+0x10], R12 ;
        |        /*0450*/                   STG.E.128 desc[UR4][R2.64+0x20], R4 ;
        |        /*0460*/                   EXIT ;
        |.L_x_8:
        |        /*0470*/                   BRA `(.L_x_8);
        |        /*0480*/                   NOP;
        |        /*0490*/                   NOP;
        |        /*04a0*/                   NOP;
        |        /*04b0*/                   NOP;
        |        /*04c0*/                   NOP;
        |        /*04d0*/                   NOP;
        |        /*04e0*/                   NOP;
        |        /*04f0*/                   NOP;
        |.L_x_9:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "exchangeSm100a" ->
      """	.target	sm_100a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.exchange            --------------------------
        |	.section	.text.exchange,"ax",@progbits
        |	.align	128
        |        .global         exchange
        |        .type           exchange,@function
        |        .size           exchange,(.L_x_9 - exchange)
        |        .other          exchange,@"STO_CUDA_ENTRY STV_DEFAULT"
        |exchange:
        |.text.exchange:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R7, SR_CgaCtaId ;
        |        /*0020*/                   LDC R6, c[0x0][0x36c] ;
        |        /*0030*/                   MOV R2, 0x400 ;
        |        /*0040*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0050*/                   S2R R0, SR_TID.X ;
        |        /*0060*/                   S2R R13, SR_CTAID.X ;
        |        /*0070*/                   ISETP.EQ.U32.AND P1, PT, R6, 0x1, PT ;
        |        /*0080*/                   LEA R3, R7, R2, 0x18 ;
        |        /*0090*/                   IMAD R11, R0, 0x4, R3 ;
        |        /*00a0*/                   IMAD R2, R13, 0x3e8, R0 ;
        |        /*00b0*/                   STS [R11], R2 ;
        |        /*00c0*/              @!P1 BRA `(.L_x_0) ;
        |        /*00d0*/                   MEMBAR.ALL.GPU ;
        |        /*00e0*/                   ERRBAR;
        |        /*00f0*/                   CGAERRBAR ;
        |        /*0100*/                   UCGABAR_ARV ;
        |        /*0110*/                   BRA `(.L_x_1) ;
        |.L_x_0:
        |        /*0120*/                   NOP ;
        |.L_x_1:
        |        /*0130*/              @!P1 BRA `(.L_x_2) ;
        |        /*0140*/                   UCGABAR_WAIT ;
        |        /*0150*/                   CCTL.IVALL ;
        |        /*0160*/                   BRA `(.L_x_3) ;
        |.L_x_2:
        |        /*0170*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |.L_x_3:
        |        /*0180*/                   S2R R5, SR_SWINHI ;
        |        /*0190*/                   LOP3.LUT R4, R7, 0x1, RZ, 0x3c, !PT ;
        |        /*01a0*/                   IMAD.MOV.U32 R17, RZ, RZ, 0x1 ;
        |        /*01b0*/                   PRMT R12, R4, 0x654, R11 ;
        |        /*01c0*/                   MOV R2, R3 ;
        |        /*01d0*/                   MOV R3, R5.reuse ;
        |        /*01e0*/                   MOV R14, R12 ;
        |        /*01f0*/                   MOV R15, R5 ;
        |        /*0200*/                   PRMT R2, R4, 0x654, R2 ;
        |        /*0210*/                   LD.E R8, [R14] ;
        |        /*0220*/                   IMAD.WIDE.U32 R4, R0, 0x4, R2 ;
        |        /*0230*/                   QSPC.E.D P0, RZ, [R2] ;
        |        /*0240*/                   LD.E R9, desc[UR4][R4.64] ;
        |        /*0250*/                   SHF.L.U64.HI R12, R12, 0x8, RZ ;
        |        /*0260*/                   ATOM.E.ADD.STRONG.GPU PT, R10, [R14], R17 ;
        |        /*0270*/              @!P1 BRA `(.L_x_4) ;
        |        /*0280*/                   MEMBAR.ALL.GPU ;
        |        /*0290*/                   ERRBAR;
        |        /*02a0*/                   CGAERRBAR ;
        |        /*02b0*/                   UCGABAR_ARV ;
        |        /*02c0*/                   BRA `(.L_x_5) ;
        |.L_x_4:
        |        /*02d0*/                   NOP ;
        |.L_x_5:
        |        /*02e0*/              @!P1 BRA `(.L_x_6) ;
        |        /*02f0*/                   UCGABAR_WAIT ;
        |        /*0300*/                   CCTL.IVALL ;
        |        /*0310*/                   BRA `(.L_x_7) ;
        |.L_x_6:
        |        /*0320*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |.L_x_7:
        |        /*0330*/                   CS2R.32 R17, SR_CgaSize ;
        |        /*0340*/                   IMAD R17, R17, -0xa0, RZ ;
        |        /*0350*/                   R2UR UR6, R17 ;
        |        /*0360*/                   LDCU UR6, c[0x0][UR6+0x2b0] ;
        |        /*0370*/                   LDS R11, [R11] ;
        |        /*0380*/                   I2FP.F32.U32 R4, R13 ;
        |        /*0390*/                   LDC.64 R2, c[0x0][0x380] ;
        |        /*03a0*/                   ISETP.NE.AND P1, PT, R6, 0x1, PT ;
        |        /*03b0*/                   IMAD R17, R13, 0x20, R0 ;
        |        /*03c0*/                   SEL R6, RZ, 0x1, P1 ;
        |        /*03d0*/                   CS2R.32 R14, SR_CgaSize ;
        |        /*03e0*/                   IMAD R14, R14, -0xa0, RZ ;
        |        /*03f0*/                   LDC R14, c[0x0][R14+0x2a0] ;
        |        /*0400*/                   SEL R6, R6, 0x7, P0 ;
        |        /*0410*/                   FMUL R15, R4, UR6 ;
        |        /*0420*/                   CS2R.32 R5, SR_CgaSize ;
        |        /*0430*/                   IMAD R5, R5, -0xa0, RZ ;
        |        /*0440*/                   LDC R5, c[0x0][R5+0x2cc] ;
        |        /*0450*/                   F2I.U32.TRUNC.NTZ R15, R15 ;
        |        /*0460*/                   CS2R.32 R4, SR_CgaSize ;
        |        /*0470*/                   IMAD R4, R4, -0xa0, RZ ;
        |        /*0480*/                   LDC R4, c[0x0][R4+0x2c0] ;
        |        /*0490*/                   IMAD.WIDE.U32 R2, R17, 0x30, R2 ;
        |        /*04a0*/                   IMAD.MOV R0, RZ, RZ, -R15 ;
        |        /*04b0*/                   STG.E.128 desc[UR4][R2.64], R8 ;
        |        /*04c0*/                   IMAD R13, R14, R0, R13 ;
        |        /*04d0*/                   STG.E.128 desc[UR4][R2.64+0x10], R12 ;
        |        /*04e0*/                   STG.E.128 desc[UR4][R2.64+0x20], R4 ;
        |        /*04f0*/                   EXIT ;
        |.L_x_8:
        |        /*0500*/                   BRA `(.L_x_8);
        |        /*0510*/                   NOP;
        |        /*0520*/                   NOP;
        |        /*0530*/                   NOP;
        |        /*0540*/                   NOP;
        |        /*0550*/                   NOP;
        |        /*0560*/                   NOP;
        |        /*0570*/                   NOP;
        |        /*0580*/                   NOP;
        |        /*0590*/                   NOP;
        |        /*05a0*/                   NOP;
        |        /*05b0*/                   NOP;
        |        /*05c0*/                   NOP;
        |        /*05d0*/                   NOP;
        |        /*05e0*/                   NOP;
        |        /*05f0*/                   NOP;
        |.L_x_9:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "exchangeSm120a" ->
      """	.target	sm_120a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.exchange            --------------------------
        |	.section	.text.exchange,"ax",@progbits
        |	.align	128
        |        .global         exchange
        |        .type           exchange,@function
        |        .size           exchange,(.L_x_9 - exchange)
        |        .other          exchange,@"STO_CUDA_ENTRY STV_DEFAULT"
        |exchange:
        |.text.exchange:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R7, SR_CgaCtaId ;
        |        /*0020*/                   LDC R6, c[0x0][0x36c] ;
        |        /*0030*/                   MOV R0, 0x400 ;
        |        /*0040*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0050*/                   S2R R17, SR_TID.X ;
        |        /*0060*/                   S2R R13, SR_CTAID.X ;
        |        /*0070*/                   ISETP.EQ.U32.AND P1, PT, R6, 0x1, PT ;
        |        /*0080*/                   LEA R0, R7, R0, 0x18 ;
        |        /*0090*/                   IMAD R11, R17, 0x4, R0 ;
        |        /*00a0*/                   IMAD R2, R13, 0x3e8, R17 ;
        |        /*00b0*/                   STS [R11], R2 ;
        |        /*00c0*/              @!P1 BRA `(.L_x_0) ;
        |        /*00d0*/                   MEMBAR.ALL.GPU ;
        |        /*00e0*/                   ERRBAR;
        |        /*00f0*/                   CGAERRBAR ;
        |        /*0100*/                   UCGABAR_ARV ;
        |        /*0110*/                   BRA `(.L_x_1) ;
        |.L_x_0:
        |        /*0120*/                   NOP ;
        |.L_x_1:
        |        /*0130*/              @!P1 BRA `(.L_x_2) ;
        |        /*0140*/                   UCGABAR_WAIT ;
        |        /*0150*/                   CCTL.IVALL ;
        |        /*0160*/                   BRA `(.L_x_3) ;
        |.L_x_2:
        |        /*0170*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |.L_x_3:
        |        /*0180*/                   S2R R5, SR_SWINHI ;
        |        /*0190*/                   LOP3.LUT R4, R7, 0x1, RZ, 0x3c, !PT ;
        |        /*01a0*/                   HFMA2 R19, -RZ, RZ, 0, 5.9604644775390625e-08 ;
        |        /*01b0*/                   PRMT R12, R4, 0x654, R11 ;
        |        /*01c0*/                   MOV R2, R0 ;
        |        /*01d0*/                   MOV R3, R5 ;
        |        /*01e0*/                   MOV R14, R12 ;
        |        /*01f0*/                   MOV R15, R5 ;
        |        /*0200*/                   PRMT R2, R4, 0x654, R2 ;
        |        /*0210*/                   LD.E R8, [R14] ;
        |        /*0220*/                   QSPC.E.D P0, RZ, [R2] ;
        |        /*0230*/                   IMAD.WIDE.U32 R4, R17, 0x4, R2 ;
        |        /*0240*/                   SHF.L.U64.HI R12, R12, 0x8, RZ ;
        |        /*0250*/                   LD.E R9, desc[UR4][R4.64] ;
        |        /*0260*/                   ATOM.E.ADD.STRONG.GPU PT, R10, [R14], R19 ;
        |        /*0270*/              @!P1 BRA `(.L_x_4) ;
        |        /*0280*/                   MEMBAR.ALL.GPU ;
        |        /*0290*/                   ERRBAR;
        |        /*02a0*/                   CGAERRBAR ;
        |        /*02b0*/                   UCGABAR_ARV ;
        |        /*02c0*/                   BRA `(.L_x_5) ;
        |.L_x_4:
        |        /*02d0*/                   NOP ;
        |.L_x_5:
        |        /*02e0*/              @!P1 BRA `(.L_x_6) ;
        |        /*02f0*/                   UCGABAR_WAIT ;
        |        /*0300*/                   CCTL.IVALL ;
        |        /*0310*/                   BRA `(.L_x_7) ;
        |.L_x_6:
        |        /*0320*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |.L_x_7:
        |        /*0330*/                   CS2R.32 R16, SR_CgaSize ;
        |        /*0340*/                   IMAD R16, R16, -0xa0, RZ ;
        |        /*0350*/                   R2UR UR6, R16 ;
        |        /*0360*/                   LDCU UR6, c[0x0][UR6+0x2b0] ;
        |        /*0370*/                   LDS R11, [R11] ;
        |        /*0380*/                   I2FP.F32.U32 R0, R13 ;
        |        /*0390*/                   LDC.64 R2, c[0x0][0x380] ;
        |        /*03a0*/                   ISETP.NE.AND P1, PT, R6, 0x1, PT ;
        |        /*03b0*/                   IMAD R17, R13, 0x20, R17 ;
        |        /*03c0*/                   SEL R6, RZ, 0x1, P1 ;
        |        /*03d0*/                   CS2R.32 R14, SR_CgaSize ;
        |        /*03e0*/                   IMAD R14, R14, -0xa0, RZ ;
        |        /*03f0*/                   LDC R14, c[0x0][R14+0x2a0] ;
        |        /*0400*/                   SEL R6, R6, 0x7, P0 ;
        |        /*0410*/                   FMUL R0, R0, UR6 ;
        |        /*0420*/                   CS2R.32 R4, SR_CgaSize ;
        |        /*0430*/                   IMAD R4, R4, -0xa0, RZ ;
        |        /*0440*/                   LDC R4, c[0x0][R4+0x2c0] ;
        |        /*0450*/                   F2I.U32.TRUNC.NTZ R15, R0 ;
        |        /*0460*/                   CS2R.32 R5, SR_CgaSize ;
        |        /*0470*/                   IMAD R5, R5, -0xa0, RZ ;
        |        /*0480*/                   LDC R5, c[0x0][R5+0x2cc] ;
        |        /*0490*/                   IMAD.WIDE.U32 R2, R17, 0x30, R2 ;
        |        /*04a0*/                   IADD R16, -R15, RZ ;
        |        /*04b0*/                   STG.E.128 desc[UR4][R2.64], R8 ;
        |        /*04c0*/                   IMAD R13, R14, R16, R13 ;
        |        /*04d0*/                   STG.E.128 desc[UR4][R2.64+0x10], R12 ;
        |        /*04e0*/                   STG.E.128 desc[UR4][R2.64+0x20], R4 ;
        |        /*04f0*/                   EXIT ;
        |.L_x_8:
        |        /*0500*/                   BRA `(.L_x_8);
        |        /*0510*/                   NOP;
        |        /*0520*/                   NOP;
        |        /*0530*/                   NOP;
        |        /*0540*/                   NOP;
        |        /*0550*/                   NOP;
        |        /*0560*/                   NOP;
        |        /*0570*/                   NOP;
        |        /*0580*/                   NOP;
        |        /*0590*/                   NOP;
        |        /*05a0*/                   NOP;
        |        /*05b0*/                   NOP;
        |        /*05c0*/                   NOP;
        |        /*05d0*/                   NOP;
        |        /*05e0*/                   NOP;
        |        /*05f0*/                   NOP;
        |.L_x_9:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "asyncSm90a" ->
      """	.target	sm_90a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.async               --------------------------
        |	.section	.text.async,"ax",@progbits
        |	.align	128
        |        .global         async
        |        .type           async,@function
        |        .size           async,(.L_x_16 - async)
        |        .other          async,@"STO_CUDA_ENTRY STV_DEFAULT"
        |async:
        |.text.async:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   LDC R2, c[0x0][0x140] ;
        |        /*0030*/                   BSSY B0, `(.L_x_0) ;
        |        /*0040*/                   ISETP.EQ.U32.AND P1, PT, R2, 0x1, PT ;
        |        /*0050*/                   ISETP.NE.U32.AND P2, PT, R0, RZ, PT ;
        |        /*0060*/               @P2 BRA `(.L_x_1) ;
        |        /*0070*/                   S2UR UR10, SR_CgaCtaId ;
        |        /*0080*/                   UMOV UR6, 0x400 ;
        |        /*0090*/                   UMOV UR4, 0x1 ;
        |        /*00a0*/                   UIADD3 UR8, UR6, 0xc0, URZ ;
        |        /*00b0*/                   UIADD3 UR9, UR6, 0xc8, URZ ;
        |        /*00c0*/                   UMOV UR7, 0x2 ;
        |        /*00d0*/                   UIADD3 UR4, -UR4, 0x100000, URZ ;
        |        /*00e0*/                   USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00f0*/                   USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*0100*/                   UIADD3 UR6, -UR7, 0x100000, URZ ;
        |        /*0110*/                   USHF.L.U32 UR7, UR6, 0xb, URZ ;
        |        /*0120*/                   USHF.L.U32 UR6, UR6, 0x1, URZ ;
        |        /*0130*/                   ULEA UR8, UR10, UR8, 0x18 ;
        |        /*0140*/                   ULEA UR9, UR10, UR9, 0x18 ;
        |        /*0150*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0160*/                   SYNCS.EXCH.64 URZ, [UR8], UR4 ;
        |        /*0170*/                   SYNCS.EXCH.64 URZ, [UR9], UR6 ;
        |        /*0180*/                   NOP ;
        |.L_x_1:
        |        /*0190*/                   BSYNC B0 ;
        |.L_x_0:
        |        /*01a0*/                   NOP ;
        |        /*01b0*/              @!P1 BRA `(.L_x_2) ;
        |        /*01c0*/                   UCGABAR_ARV ;
        |        /*01d0*/                   BRA `(.L_x_3) ;
        |.L_x_2:
        |        /*01e0*/                   NOP ;
        |.L_x_3:
        |        /*01f0*/              @!P1 BRA `(.L_x_4) ;
        |        /*0200*/                   UCGABAR_WAIT ;
        |        /*0210*/                   CCTL.IVALL ;
        |        /*0220*/                   BRA `(.L_x_5) ;
        |.L_x_4:
        |        /*0230*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |.L_x_5:
        |        /*0240*/                   ELECT P0, URZ, PT ;
        |        /*0250*/                   S2UR UR8, SR_CgaCtaId ;
        |        /*0260*/                   BSSY B0, `(.L_x_6) ;
        |        /*0270*/                   ISETP.LT.U32.AND P0, PT, R0, 0x20, P0 ;
        |        /*0280*/              @!P0 BRA `(.L_x_7) ;
        |        /*0290*/                   UMOV UR6, 0x400 ;
        |        /*02a0*/                   ULOP3.LUT UR4, UR8, 0x1, URZ, 0x3c, !UPT ;
        |        /*02b0*/                   ISETP.NE.U32.AND P0, PT, RZ, UR8, PT ;
        |        /*02c0*/                   UIADD3 UR7, UR6, 0xc0, URZ ;
        |        /*02d0*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x48 ;
        |        /*02e0*/                   ULEA UR5, UR8, UR6, 0x18 ;
        |        /*02f0*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x5 ;
        |        /*0300*/                   ULEA UR7, UR8, UR7, 0x18 ;
        |        /*0310*/                   UIADD3 UR9, UR6, 0xc8, URZ ;
        |        /*0320*/                   UPRMT UR5, UR4, 0x654, UR5 ;
        |        /*0330*/                   UPRMT UR4, UR4, 0x654, UR7 ;
        |        /*0340*/                   UIADD3 UR10, UR8, 0xa, URZ ;
        |        /*0350*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR7], R5 ;
        |        /*0360*/                   ULEA UR9, UR8, UR9, 0x18 ;
        |        /*0370*/                   IMAD.U32 R2, RZ, RZ, UR5 ;
        |        /*0380*/                   UIADD3 UR11, UR5, 0x4, URZ ;
        |        /*0390*/                   MOV R3, UR4 ;
        |        /*03a0*/                   UPRMT UR4, URZ, 0x654, UR9 ;
        |        /*03b0*/                   IMAD.U32 R4, RZ, RZ, UR10 ;
        |        /*03c0*/                   MOV R7, R3 ;
        |        /*03d0*/                   IMAD.U32 R6, RZ, RZ, UR11 ;
        |        /*03e0*/                   STAS [R2.64], R4 ;
        |        /*03f0*/                   REDAS.ADD [R6.64], R0 ;
        |        /*0400*/                   MEMBAR.ALL.CTA ;
        |        /*0410*/                   MEMBAR.ALL.GPU ;
        |        /*0420*/                   ERRBAR;
        |        /*0430*/                   CGAERRBAR ;
        |        /*0440*/                   SYNCS.ARRIVE.TRANS64.RED.A1T0 RZ, [UR4], RZ ;
        |        /*0450*/               @P0 BRA `(.L_x_7) ;
        |        /*0460*/                   UIADD3 UR4, UR6, 0x80, URZ ;
        |        /*0470*/                   UMOV UR9, 0x30004 ;
        |        /*0480*/                   ULEA UR6, UR8, UR4, 0x18 ;
        |        /*0490*/                   ULDC.64 UR4, c[0x0][0x210] ;
        |        /*04a0*/                   UBLKCP.S.G.MULTICAST [UR6], [UR4], UR9 ;
        |        /*04b0*/                   NOP ;
        |.L_x_7:
        |        /*04c0*/                   BSYNC B0 ;
        |.L_x_6:
        |        /*04d0*/                   UMOV UR4, 0x400 ;
        |        /*04e0*/                   SHF.L.U32 R0, RZ, 0x1f, RZ ;
        |        /*04f0*/                   UIADD3 UR5, UR4, 0xc0, URZ ;
        |        /*0500*/                   ISETP.NE.U32.AND P0, PT, RZ, UR8, PT ;
        |        /*0510*/                   ULEA UR5, UR8, UR5, 0x18 ;
        |        /*0520*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P3, [UR5], R0 ;
        |        /*0530*/              @!P3 BRA `(.L_x_8) ;
        |.L_x_14:
        |        /*0540*/               @P0 BRA `(.L_x_9) ;
        |        /*0550*/                   UIADD3 UR5, UR4, 0xc8, URZ ;
        |        /*0560*/                   ULEA UR5, UR8, UR5, 0x18 ;
        |        /*0570*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*0580*/               @P0 CCTL.IVALL ;
        |        /*0590*/              @!P0 BRA `(.L_x_10) ;
        |.L_x_9:
        |        /*05a0*/                   VOTEU.ALL UP0, P2 ;
        |        /*05b0*/              @!P2 S2R R9, SR_CTAID.X ;
        |        /*05c0*/              @!P2 LDC.64 R2, c[0x0][0x210] ;
        |        /*05d0*/                   @!UP0 ULEA UR4, UR8, UR4, 0x18 ;
        |        /*05e0*/              @!P2 LDS.64 R4, [UR4] ;
        |        /*05f0*/              @!P2 LDS.64 R6, [UR4+0x88] ;
        |        /*0600*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0610*/              @!P2 IMAD.WIDE.U32 R2, R9, 0x20, R2 ;
        |        /*0620*/              @!P2 STG.E.128 desc[UR4][R2.64+0x40], R4 ;
        |        /*0630*/              @!P1 BRA `(.L_x_11) ;
        |        /*0640*/                   MEMBAR.ALL.CTA ;
        |        /*0650*/                   MEMBAR.ALL.GPU ;
        |        /*0660*/                   ERRBAR;
        |        /*0670*/                   CGAERRBAR ;
        |        /*0680*/                   UCGABAR_ARV ;
        |        /*0690*/                   BRA `(.L_x_12) ;
        |.L_x_11:
        |        /*06a0*/                   NOP ;
        |.L_x_12:
        |        /*06b0*/              @!P1 BRA `(.L_x_13) ;
        |        /*06c0*/                   UCGABAR_WAIT ;
        |        /*06d0*/                   CCTL.IVALL ;
        |        /*06e0*/                   EXIT ;
        |.L_x_13:
        |        /*06f0*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0700*/                   EXIT ;
        |.L_x_8:
        |        /*0710*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P3, [UR5], R0 ;
        |        /*0720*/              @!P3 BRA `(.L_x_8) ;
        |        /*0730*/                   BRA `(.L_x_14) ;
        |.L_x_10:
        |        /*0740*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*0750*/               @P0 CCTL.IVALL ;
        |        /*0760*/              @!P0 BRA `(.L_x_10) ;
        |        /*0770*/                   BRA `(.L_x_9) ;
        |.L_x_15:
        |        /*0780*/                   BRA `(.L_x_15);
        |        /*0790*/                   NOP;
        |        /*07a0*/                   NOP;
        |        /*07b0*/                   NOP;
        |        /*07c0*/                   NOP;
        |        /*07d0*/                   NOP;
        |        /*07e0*/                   NOP;
        |        /*07f0*/                   NOP;
        |        /*0800*/                   NOP;
        |        /*0810*/                   NOP;
        |        /*0820*/                   NOP;
        |        /*0830*/                   NOP;
        |        /*0840*/                   NOP;
        |        /*0850*/                   NOP;
        |        /*0860*/                   NOP;
        |        /*0870*/                   NOP;
        |.L_x_16:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "asyncSm100a" ->
      """	.target	sm_100a
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.async               --------------------------
        |	.section	.text.async,"ax",@progbits
        |	.align	128
        |        .global         async
        |        .type           async,@function
        |        .size           async,(.L_x_16 - async)
        |        .other          async,@"STO_CUDA_ENTRY STV_DEFAULT"
        |async:
        |.text.async:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   BSSY.RECONVERGENT B0, `(.L_x_0) ;
        |        /*0030*/                   ISETP.NE.U32.AND P1, PT, R0, RZ, PT ;
        |        /*0040*/               @P1 BRA `(.L_x_1) ;
        |        /*0050*/                   S2UR UR10, SR_CgaCtaId ;
        |        /*0060*/                   UMOV UR6, 0x400 ;
        |        /*0070*/                   UMOV UR4, 0x1 ;
        |        /*0080*/                   UIADD3 UR8, UPT, UPT, UR6, 0xc0, URZ ;
        |        /*0090*/                   UIADD3 UR9, UPT, UPT, UR6, 0xc8, URZ ;
        |        /*00a0*/                   UMOV UR7, 0x2 ;
        |        /*00b0*/                   UIADD3 UR4, UPT, UPT, -UR4, 0x100000, URZ ;
        |        /*00c0*/                   USHF.L.U32 UR5, UR4, 0xb, URZ ;
        |        /*00d0*/                   USHF.L.U32 UR4, UR4, 0x1, URZ ;
        |        /*00e0*/                   UIADD3 UR6, UPT, UPT, -UR7, 0x100000, URZ ;
        |        /*00f0*/                   USHF.L.U32 UR7, UR6, 0xb, URZ ;
        |        /*0100*/                   USHF.L.U32 UR6, UR6, 0x1, URZ ;
        |        /*0110*/                   ULEA UR8, UR10, UR8, 0x18 ;
        |        /*0120*/                   ULEA UR9, UR10, UR9, 0x18 ;
        |        /*0130*/                   FENCE.VIEW.ASYNC.S ;
        |        /*0140*/                   SYNCS.EXCH.64 URZ, [UR8], UR4 ;
        |        /*0150*/                   SYNCS.EXCH.64 URZ, [UR9], UR6 ;
        |        /*0160*/                   NOP ;
        |.L_x_1:
        |        /*0170*/                   NOP ;
        |        /*0180*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_0:
        |        /*0190*/                   LDCU UR4, c[0x0][0x36c] ;
        |        /*01a0*/                   NOP ;
        |        /*01b0*/                   UISETP.EQ.U32.AND UP0, UPT, UR4, 0x1, UPT ;
        |        /*01c0*/                   BRA.U !UP0, `(.L_x_2) ;
        |        /*01d0*/                   UCGABAR_ARV ;
        |        /*01e0*/                   BRA `(.L_x_3) ;
        |.L_x_2:
        |        /*01f0*/                   NOP ;
        |.L_x_3:
        |        /*0200*/                   BRA.U !UP0, `(.L_x_4) ;
        |        /*0210*/                   UCGABAR_WAIT ;
        |        /*0220*/                   CCTL.IVALL ;
        |        /*0230*/                   BRA `(.L_x_5) ;
        |.L_x_4:
        |        /*0240*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |.L_x_5:
        |        /*0250*/                   ELECT P0, URZ, PT ;
        |        /*0260*/                   S2UR UR8, SR_CgaCtaId ;
        |        /*0270*/                   BSSY.RECONVERGENT B0, `(.L_x_6) ;
        |        /*0280*/                   ISETP.LT.U32.AND P0, PT, R0, 0x20, P0 ;
        |        /*0290*/              @!P0 BRA `(.L_x_7) ;
        |        /*02a0*/                   UMOV UR6, 0x400 ;
        |        /*02b0*/                   ULOP3.LUT UR4, UR8, 0x1, URZ, 0x3c, !UPT ;
        |        /*02c0*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x48 ;
        |        /*02d0*/                   UIADD3 UR7, UPT, UPT, UR6, 0xc0, URZ ;
        |        /*02e0*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x5 ;
        |        /*02f0*/                   ULEA UR5, UR8, UR6, 0x18 ;
        |        /*0300*/                   ULEA UR7, UR8, UR7, 0x18 ;
        |        /*0310*/                   UPRMT UR5, UR4, 0x654, UR5 ;
        |        /*0320*/                   UIADD3 UR9, UPT, UPT, UR6, 0xc8, URZ ;
        |        /*0330*/                   UPRMT UR4, UR4, 0x654, UR7 ;
        |        /*0340*/                   UIADD3 UR10, UPT, UPT, UR8, 0xa, URZ ;
        |        /*0350*/                   MOV R2, UR5 ;
        |        /*0360*/                   ULEA UR9, UR8, UR9, 0x18 ;
        |        /*0370*/                   SYNCS.ARRIVE.TRANS64 RZ, [UR7], R5 ;
        |        /*0380*/                   UIADD3 UR11, UPT, UPT, UR5, 0x4, URZ ;
        |        /*0390*/                   IMAD.U32 R3, RZ, RZ, UR4 ;
        |        /*03a0*/                   UPRMT UR4, URZ, 0x654, UR9 ;
        |        /*03b0*/                   MOV R4, UR10 ;
        |        /*03c0*/                   UISETP.NE.U32.AND UP1, UPT, UR8, URZ, UPT ;
        |        /*03d0*/                   IMAD.U32 R6, RZ, RZ, UR11 ;
        |        /*03e0*/                   MOV R7, R3 ;
        |        /*03f0*/                   STAS [R2.64], R4 ;
        |        /*0400*/                   REDAS.ADD [R6.64], R0 ;
        |        /*0410*/                   MEMBAR.ALL.CTA ;
        |        /*0420*/                   MEMBAR.ALL.GPU ;
        |        /*0430*/                   ERRBAR;
        |        /*0440*/                   CGAERRBAR ;
        |        /*0450*/                   SYNCS.ARRIVE.TRANS64.RED.A1T0 RZ, [UR4], RZ ;
        |        /*0460*/                   BRA.U UP1, `(.L_x_7) ;
        |        /*0470*/                   LDCU.64 UR10, c[0x0][0x380] ;
        |        /*0480*/                   UIADD3 UR4, UPT, UPT, UR6, 0x80, URZ ;
        |        /*0490*/                   ULEA UR6, UR8, UR4, 0x18 ;
        |        /*04a0*/                   UMOV UR4, 0x30004 ;
        |        /*04b0*/                   UBLKCP.S.G.MULTICAST [UR6], [UR10], UR4 ;
        |        /*04c0*/                   NOP ;
        |.L_x_7:
        |        /*04d0*/                   NOP ;
        |        /*04e0*/                   BSYNC.RECONVERGENT B0 ;
        |.L_x_6:
        |        /*04f0*/                   UMOV UR4, 0x400 ;
        |        /*0500*/                   SHF.L.U32 R0, RZ, 0x1f, RZ ;
        |        /*0510*/                   UIADD3 UR5, UPT, UPT, UR4, 0xc0, URZ ;
        |        /*0520*/                   ULEA UR5, UR8, UR5, 0x18 ;
        |        /*0530*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*0540*/              @!P0 BRA `(.L_x_8) ;
        |.L_x_14:
        |        /*0550*/                   UISETP.NE.U32.AND UP1, UPT, UR8, URZ, UPT ;
        |        /*0560*/                   BRA.U UP1, `(.L_x_9) ;
        |        /*0570*/                   UIADD3 UR5, UPT, UPT, UR4, 0xc8, URZ ;
        |        /*0580*/                   ULEA UR5, UR8, UR5, 0x18 ;
        |        /*0590*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*05a0*/               @P0 CCTL.IVALL ;
        |        /*05b0*/              @!P0 BRA `(.L_x_10) ;
        |.L_x_9:
        |        /*05c0*/                   VOTEU.ALL UP1, P1 ;
        |        /*05d0*/              @!P1 S2R R9, SR_CTAID.X ;
        |        /*05e0*/              @!P1 LDC.64 R2, c[0x0][0x380] ;
        |        /*05f0*/                   @!UP1 ULEA UR4, UR8, UR4, 0x18 ;
        |        /*0600*/              @!P1 LDS.64 R4, [UR4] ;
        |        /*0610*/              @!P1 LDS.64 R6, [UR4+0x88] ;
        |        /*0620*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0630*/              @!P1 IMAD.WIDE.U32 R2, R9, 0x20, R2 ;
        |        /*0640*/              @!P1 STG.E.128 desc[UR4][R2.64+0x40], R4 ;
        |        /*0650*/                   BRA.U !UP0, `(.L_x_11) ;
        |        /*0660*/                   MEMBAR.ALL.CTA ;
        |        /*0670*/                   MEMBAR.ALL.GPU ;
        |        /*0680*/                   ERRBAR;
        |        /*0690*/                   CGAERRBAR ;
        |        /*06a0*/                   UCGABAR_ARV ;
        |        /*06b0*/                   BRA `(.L_x_12) ;
        |.L_x_11:
        |        /*06c0*/                   NOP ;
        |.L_x_12:
        |        /*06d0*/                   BRA.U !UP0, `(.L_x_13) ;
        |        /*06e0*/                   UCGABAR_WAIT ;
        |        /*06f0*/                   CCTL.IVALL ;
        |        /*0700*/                   EXIT ;
        |.L_x_13:
        |        /*0710*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0720*/                   EXIT ;
        |.L_x_8:
        |        /*0730*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*0740*/              @!P0 BRA `(.L_x_8) ;
        |        /*0750*/                   BRA `(.L_x_14) ;
        |.L_x_10:
        |        /*0760*/                   SYNCS.PHASECHK.TRANS64.TRYWAIT P0, [UR5], R0 ;
        |        /*0770*/               @P0 CCTL.IVALL ;
        |        /*0780*/              @!P0 BRA `(.L_x_10) ;
        |        /*0790*/                   BRA `(.L_x_9) ;
        |.L_x_15:
        |        /*07a0*/                   BRA `(.L_x_15);
        |        /*07b0*/                   NOP;
        |        /*07c0*/                   NOP;
        |        /*07d0*/                   NOP;
        |        /*07e0*/                   NOP;
        |        /*07f0*/                   NOP;
        |        /*0800*/                   NOP;
        |        /*0810*/                   NOP;
        |        /*0820*/                   NOP;
        |        /*0830*/                   NOP;
        |        /*0840*/                   NOP;
        |        /*0850*/                   NOP;
        |        /*0860*/                   NOP;
        |        /*0870*/                   NOP;
        |.L_x_16:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin
  )
}
