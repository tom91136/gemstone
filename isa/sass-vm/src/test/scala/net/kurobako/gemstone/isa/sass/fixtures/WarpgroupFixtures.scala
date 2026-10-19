package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*

/** Warpgroup products on sm_90a: the PTX kernels of [[net.kurobako.gemstone.isa.nvidia.fixtures.WarpgroupProducts]] for
  * the products named, each `ptxas -arch=sm_90a kernel.ptx -o kernel.cubin` and `nvdisasm -c -ndf -hex kernel.cubin`
  * with CUDA 13.3, as [[Recipes]] reproduces them.
  */
object WarpgroupFixtures {

  /** Each listing and the product it computes. */
  val products: Vec[(listing: String, product: String)] = Vec(
    ("wgmma0Sm90a", "m64n8k16 f32.f16.f16 A swizzle 0 B swizzle 0 scale-d p"),
    ("wgmma1Sm90a", "m64n24k16 f16.f16.f16 A registers B swizzle 0 -A scale-d 1"),
    ("wgmma2Sm90a", "m64n64k16 f32.bf16.bf16 A swizzle 128 B swizzle 128 -B scale-d !p"),
    ("wgmma3Sm90a", "m64n64k16 f32.f16.f16 A swizzle 128 B swizzle 64 transA transB scale-d p"),
    ("wgmma4Sm90a", "m64n8k16 f16.f16.f16 A registers B swizzle 128 transB scale-d p"),
    ("wgmma5Sm90a", "m64n16k8 f32.tf32.tf32 A swizzle 64 B swizzle 32 scale-d 0"),
    ("wgmma6Sm90a", "m64n24k32 f32.e4m3.e5m2 A registers B swizzle 0 -A scale-d 1"),
    ("wgmma7Sm90a", "m64n8k32 f16.e5m2.e4m3 A swizzle 0 B swizzle 0 scale-d p"),
    ("wgmma8Sm90a", "m64n8k32 s32.s8.s8 A swizzle 0 B swizzle 0 scale-d p"),
    ("wgmma9Sm90a", "m64n16k32 s32.u8.u8 A swizzle 0 B swizzle 0 scale-d p satfinite"),
    ("wgmma10Sm90a", "m64n48k32 s32.u8.s8 A registers B swizzle 128 scale-d p"),
    ("wgmma11Sm90a", "m64n64k256 s32.b1.b1 A registers B swizzle 128 scale-d p"),
    ("wgmma12Sm90a", "m64n16k256 s32.b1.b1 A swizzle 32 B swizzle 32 scale-d !p"),
    ("wgmma13Sm90a", "m64n8k32 f32.f16.f16 A swizzle 0 B swizzle 0 scale-d p sparse sel 0"),
    ("wgmma14Sm90a", "m64n32k16 f32.tf32.tf32 A registers B swizzle 128 scale-d p sparse sel 1"),
    ("wgmma15Sm90a", "m64n16k64 f32.e4m3.e5m2 A swizzle 64 B swizzle 64 scale-d !p sparse sel 0"),
    ("wgmma16Sm90a", "m64n16k64 s32.u8.s8 A swizzle 128 B swizzle 64 scale-d p sparse sel 0 satfinite"),
    ("wgmma17Sm90a", "m64n128k16 f16.f16.f16 A swizzle 64 B swizzle 32 transA scale-d p")
  )

  val listings: Map[String, String] = Map(
    "wgmma0Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                               /* 0x00000a00ff017b82 */
        |                                                                                        /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                   /* 0x0000000000007919 */
        |                                                                                        /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                              /* 0x00008600ff027b82 */
        |                                                                                        /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                         /* 0x0000820000067ab9 */
        |                                                                                        /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                        /* 0x00008400000c7ab9 */
        |                                                                                        /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                 /* 0x0000051000007945 */
        |                                                                                        /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                /* 0x000000020000720c */
        |                                                                                        /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                      /* 0x0000000400380947 */
        |                                                                                        /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                        /* 0x000000ffff037224 */
        |                                                                                        /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                /* 0x000000020300720c */
        |                                                                                        /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                           /* 0x00008400ff042b82 */
        |                                                                                        /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                           /* 0x0000000403042210 */
        |                                                                                        /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                          /* 0x000000ffff052224 */
        |                                                                                        /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                      /* 0x0000000604042981 */
        |                                                                                        /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                      /* 0x0000040000062802 */
        |                                                                                        /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                 /* 0x0000027000017945 */
        |                                                                                        /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;             /* 0x000000000000781c */
        |                                                                                        /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                /* 0x0000000000072919 */
        |                                                                                        /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                               /* 0x0000000607062211 */
        |                                                                                        /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                    /* 0x0000000103072824 */
        |                                                                                        /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                 /* 0x0000008003032836 */
        |                                                                                        /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                         /* 0x0000000102067824 */
        |                                                                                        /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                /* 0x000000020300720c */
        |                                                                                        /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;              /* 0x000001800600780c */
        |                                                                                        /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                    /* 0x0000000407002388 */
        |                                                                                        /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                      /* 0x0000000000700947 */
        |                                                                                        /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                /* 0x0000000000057919 */
        |                                                                                        /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                      /* 0x0000040000047802 */
        |                                                                                        /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                          /* 0xfffffe80020c7836 */
        |                                                                                        /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;             /* 0x000000000000781c */
        |                                                                                        /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                              /* 0x00000004050e7211 */
        |                                                                                        /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                   /* 0x0000000c03047c10 */
        |                                                                                        /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                           /* 0x0000008003067836 */
        |                                                                                        /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                          /* 0x0000010003087836 */
        |                                                                                        /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                               /* 0x00000180030a7836 */
        |                                                                                        /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                         /* 0x0000000c06067c10 */
        |                                                                                        /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                        /* 0x0000000dff057e24 */
        |                                                                                        /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                         /* 0x0000000c08087c10 */
        |                                                                                        /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                       /* 0x0000000c0a0a7c10 */
        |                                                                                        /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                        /* 0x0000000dff077e24 */
        |                                                                                        /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                      /* 0x0000000604047981 */
        |                                                                                        /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                        /* 0x0000000dff097e24 */
        |                                                                                        /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                       /* 0x0000000dff0b7e24 */
        |                                                                                        /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                      /* 0x0000000606067981 */
        |                                                                                        /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                      /* 0x0000000608087981 */
        |                                                                                        /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                    /* 0x000000060a0a7981 */
        |                                                                                        /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                        /* 0x000000010e0d7824 */
        |                                                                                        /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                /* 0x0000020003037836 */
        |                                                                                        /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;               /* 0x0000000c0300720c */
        |                                                                                        /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                   /* 0x000000040d007388 */
        |                                                                                        /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                              /* 0x000080060d007388 */
        |                                                                                        /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                             /* 0x000100080d007388 */
        |                                                                                        /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                            /* 0x0001800a0d007388 */
        |                                                                                        /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                      /* 0xfffffffc00a48947 */
        |                                                                                        /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                           /* 0x0000000000017941 */
        |                                                                                        /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                         /* 0x0000000102047824 */
        |                                                                                        /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                /* 0x000000020300720c */
        |                                                                                        /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                 /* 0x0000012000017945 */
        |                                                                                        /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;               /* 0x000000800400780c */
        |                                                                                        /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                      /* 0x00000000003c0947 */
        |                                                                                        /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                           /* 0x0000008003047836 */
        |                                                                                        /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                         /* 0x0000840000047ab9 */
        |                                                                                        /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                          /* 0x0000000403067c10 */
        |                                                                                        /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                          /* 0x0000000404047c10 */
        |                                                                                        /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                         /* 0x00000005ff077e24 */
        |                                                                                        /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                         /* 0x00000005ff057e24 */
        |                                                                                        /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                      /* 0x0000000606067981 */
        |                                                                                        /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                      /* 0x0000000604047981 */
        |                                                                                        /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                              /* 0x00000000000579c3 */
        |                                                                                        /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                    /* 0x0000040000047882 */
        |                                                                                        /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;             /* 0x000000000000781c */
        |                                                                                        /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                           /* 0x0000000405047291 */
        |                                                                                        /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                /* 0x0000000603007988 */
        |                                                                                        /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                           /* 0x0000800403007988 */
        |                                                                                        /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                /* 0x0000010003037836 */
        |                                                                                        /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                           /* 0x0000000000017941 */
        |                                                                                        /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                /* 0x000000020300720c */
        |                                                                                        /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                  /* 0x0000000000209947 */
        |                                                                                        /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                         /* 0x0000840000047ab9 */
        |                                                                                        /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                          /* 0x0000000403047c10 */
        |                                                                                        /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                         /* 0x00000005ff057e24 */
        |                                                                                        /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                      /* 0x0000000604047981 */
        |                                                                                        /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                              /* 0x00000000000579c3 */
        |                                                                                        /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                    /* 0x0000040000047882 */
        |                                                                                        /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                           /* 0x0000000405047291 */
        |                                                                                        /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                /* 0x0000000403007988 */
        |                                                                                        /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                           /* 0x0000000000007941 */
        |                                                                                        /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                           /* 0x00008c00ff027b82 */
        |                                                                                        /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x24, R2 ;                     /* 0x0000002400027825 */
        |                                                                                        /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                        /* 0x0000000000007b1d */
        |                                                                                        /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R24, desc[UR6][R2.64+0x10] ;                   /* 0x0000100602187981 */
        |                                                                                        /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R25, desc[UR6][R2.64+0x14] ;                   /* 0x0000140602197981 */
        |                                                                                        /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R26, desc[UR6][R2.64+0x18] ;                   /* 0x00001806021a7981 */
        |                                                                                        /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R27, desc[UR6][R2.64+0x1c] ;                   /* 0x00001c06021b7981 */
        |                                                                                        /* 0x000162000c1e1900 */
        |        /*05e0*/                   S2UR UR5, SR_CgaCtaId ;                              /* 0x00000000000579c3 */
        |                                                                                        /* 0x000e620000008800 */
        |        /*05f0*/                   UMOV UR4, 0x400 ;                                    /* 0x0000040000047882 */
        |                                                                                        /* 0x000fe20000000000 */
        |        /*0600*/                   ULDC.64 UR8, c[0x0][0x220] ;                         /* 0x0000880000087ab9 */
        |                                                                                        /* 0x000fe20000000a00 */
        |        /*0610*/                   MEMBAR.ALL.GPU ;                                     /* 0x0000000000007992 */
        |                                                                                        /* 0x000fec000000a000 */
        |        /*0620*/                   FENCE.VIEW.ASYNC.S ;                                 /* 0x00000000000073c6 */
        |                                                                                        /* 0x000f620000000000 */
        |        /*0630*/                   ULDC.64 UR10, c[0x0][0x228] ;                        /* 0x00008a00000a7ab9 */
        |                                                                                        /* 0x000fe20000000a00 */
        |        /*0640*/                   LDC.64 R2, c[0x0][0x238] ;                           /* 0x00008e00ff027b82 */
        |                                                                                        /* 0x001e220000000a00 */
        |        /*0650*/                   ULEA UR4, UR5, UR4, 0x18 ;                           /* 0x0000000405047291 */
        |                                                                                        /* 0x002fc6000f8ec03f */
        |        /*0660*/                   ULDC UR5, c[0x0][0x240] ;                            /* 0x0000900000057ab9 */
        |                                                                                        /* 0x000fe20000000800 */
        |        /*0670*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;       /* 0x0003ffff04047892 */
        |                                                                                        /* 0x000fe4000f8ec03f */
        |        /*0680*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;          /* 0x0000003f0500728c */
        |                                                                                        /* 0x000fe4000bf05070 */
        |        /*0690*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                      /* 0x0000000404047899 */
        |                                                                                        /* 0x000fe2000800123f */
        |        /*06a0*/                   IMAD.WIDE.U32 R2, R0, 0x10, R2 ;                     /* 0x0000001000027825 */
        |                                                                                        /* 0x001fe200078e0002 */
        |        /*06b0*/                   UMOV UR5, URZ ;                                      /* 0x0000003f00057c82 */
        |                                                                                        /* 0x000fe40008000000 */
        |        /*06c0*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                       /* 0x0000000804087297 */
        |                                                                                        /* 0x000fe4000fffe03f */
        |        /*06d0*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                     /* 0x0000000a040a7297 */
        |                                                                                        /* 0x000fe2000fffe03f */
        |        /*06e0*/                   WARPGROUP.ARRIVE ;                                   /* 0x00000000000079c5 */
        |                                                                                        /* 0x020fd00000000000 */
        |        /*06f0*/                   HGMMA.64x8x16.F32 R24, gdesc[UR8], R24, UP0, gsb0 ;  /* 0x00000000081879f0 */
        |                                                                                        /* 0x000fe6000b800818 */
        |        /*0700*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                      /* 0x00008000000079c5 */
        |                                                                                        /* 0x000fe40000010000 */
        |        /*0710*/                   STG.E desc[UR6][R2.64], R24 ;                        /* 0x0000001802007986 */
        |                                                                                        /* 0x000fe8000c101906 */
        |        /*0720*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                    /* 0x0000041902007986 */
        |                                                                                        /* 0x000fe8000c101906 */
        |        /*0730*/                   STG.E desc[UR6][R2.64+0x8], R26 ;                    /* 0x0000081a02007986 */
        |                                                                                        /* 0x000fe8000c101906 */
        |        /*0740*/                   STG.E desc[UR6][R2.64+0xc], R27 ;                    /* 0x00000c1b02007986 */
        |                                                                                        /* 0x000fe2000c101906 */
        |        /*0750*/                   EXIT ;                                               /* 0x000000000000794d */
        |                                                                                        /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma1Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                 /* 0x00000a00ff017b82 */
        |                                                                                          /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                     /* 0x0000000000007919 */
        |                                                                                          /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                /* 0x00008600ff027b82 */
        |                                                                                          /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR8, c[0x0][0x208] ;                           /* 0x0000820000087ab9 */
        |                                                                                          /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR10, c[0x0][0x210] ;                          /* 0x00008400000a7ab9 */
        |                                                                                          /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                   /* 0x0000051000007945 */
        |                                                                                          /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                  /* 0x000000020000720c */
        |                                                                                          /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                        /* 0x0000000400380947 */
        |                                                                                          /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                          /* 0x000000ffff037224 */
        |                                                                                          /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                  /* 0x000000020300720c */
        |                                                                                          /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                             /* 0x00008400ff042b82 */
        |                                                                                          /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                             /* 0x0000000403042210 */
        |                                                                                          /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                            /* 0x000000ffff052224 */
        |                                                                                          /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR8][R4.64] ;                        /* 0x0000000804042981 */
        |                                                                                          /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                        /* 0x0000040000062802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                   /* 0x0000027000017945 */
        |                                                                                          /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;               /* 0x000000000000781c */
        |                                                                                          /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                  /* 0x0000000000072919 */
        |                                                                                          /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                 /* 0x0000000607062211 */
        |                                                                                          /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                      /* 0x0000000103072824 */
        |                                                                                          /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                   /* 0x0000008003032836 */
        |                                                                                          /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                           /* 0x0000000102067824 */
        |                                                                                          /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                  /* 0x000000020300720c */
        |                                                                                          /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                /* 0x000001800600780c */
        |                                                                                          /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                      /* 0x0000000407002388 */
        |                                                                                          /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                        /* 0x0000000000700947 */
        |                                                                                          /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                  /* 0x0000000000057919 */
        |                                                                                          /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                        /* 0x0000040000047802 */
        |                                                                                          /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                            /* 0xfffffe80020c7836 */
        |                                                                                          /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;               /* 0x000000000000781c */
        |                                                                                          /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                /* 0x00000004050e7211 */
        |                                                                                          /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR10, RZ ;                     /* 0x0000000a03047c10 */
        |                                                                                          /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                             /* 0x0000008003067836 */
        |                                                                                          /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                            /* 0x0000010003087836 */
        |                                                                                          /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                 /* 0x00000180030a7836 */
        |                                                                                          /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR10, RZ ;                           /* 0x0000000a06067c10 */
        |                                                                                          /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR11, P2 ;                          /* 0x0000000bff057e24 */
        |                                                                                          /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR10, RZ ;                           /* 0x0000000a08087c10 */
        |                                                                                          /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR10, RZ ;                         /* 0x0000000a0a0a7c10 */
        |                                                                                          /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR11, P0 ;                          /* 0x0000000bff077e24 */
        |                                                                                          /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                        /* 0x0000000804047981 */
        |                                                                                          /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR11, P2 ;                          /* 0x0000000bff097e24 */
        |                                                                                          /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR11, P3 ;                         /* 0x0000000bff0b7e24 */
        |                                                                                          /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                        /* 0x0000000806067981 */
        |                                                                                          /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR8][R8.64] ;                        /* 0x0000000808087981 */
        |                                                                                          /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR8][R10.64] ;                      /* 0x000000080a0a7981 */
        |                                                                                          /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                          /* 0x000000010e0d7824 */
        |                                                                                          /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                  /* 0x0000020003037836 */
        |                                                                                          /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                 /* 0x0000000c0300720c */
        |                                                                                          /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                     /* 0x000000040d007388 */
        |                                                                                          /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                /* 0x000080060d007388 */
        |                                                                                          /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                               /* 0x000100080d007388 */
        |                                                                                          /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                              /* 0x0001800a0d007388 */
        |                                                                                          /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                        /* 0xfffffffc00a48947 */
        |                                                                                          /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                             /* 0x0000000000017941 */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                           /* 0x0000000102047824 */
        |                                                                                          /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                  /* 0x000000020300720c */
        |                                                                                          /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                   /* 0x0000012000017945 */
        |                                                                                          /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                 /* 0x000000800400780c */
        |                                                                                          /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                        /* 0x00000000003c0947 */
        |                                                                                          /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                             /* 0x0000008003047836 */
        |                                                                                          /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                           /* 0x0000840000047ab9 */
        |                                                                                          /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                            /* 0x0000000403067c10 */
        |                                                                                          /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                            /* 0x0000000404047c10 */
        |                                                                                          /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                           /* 0x00000005ff077e24 */
        |                                                                                          /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                           /* 0x00000005ff057e24 */
        |                                                                                          /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                        /* 0x0000000806067981 */
        |                                                                                          /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                        /* 0x0000000804047981 */
        |                                                                                          /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                /* 0x00000000000579c3 */
        |                                                                                          /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                      /* 0x0000040000047882 */
        |                                                                                          /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;               /* 0x000000000000781c */
        |                                                                                          /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                             /* 0x0000000405047291 */
        |                                                                                          /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                  /* 0x0000000603007988 */
        |                                                                                          /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                             /* 0x0000800403007988 */
        |                                                                                          /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                  /* 0x0000010003037836 */
        |                                                                                          /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                             /* 0x0000000000017941 */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                  /* 0x000000020300720c */
        |                                                                                          /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                    /* 0x0000000000209947 */
        |                                                                                          /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                           /* 0x0000840000047ab9 */
        |                                                                                          /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                            /* 0x0000000403047c10 */
        |                                                                                          /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                           /* 0x00000005ff057e24 */
        |                                                                                          /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                        /* 0x0000000804047981 */
        |                                                                                          /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                /* 0x00000000000579c3 */
        |                                                                                          /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                      /* 0x0000040000047882 */
        |                                                                                          /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                             /* 0x0000000405047291 */
        |                                                                                          /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                  /* 0x0000000403007988 */
        |                                                                                          /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                             /* 0x0000000000007941 */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                             /* 0x00008c00ff027b82 */
        |                                                                                          /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x2c, R2 ;                       /* 0x0000002c00027825 */
        |                                                                                          /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                          /* 0x0000000000007b1d */
        |                                                                                          /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R32, desc[UR8][R2.64] ;                          /* 0x0000000802207981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R33, desc[UR8][R2.64+0x4] ;                      /* 0x0000040802217981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R34, desc[UR8][R2.64+0x8] ;                      /* 0x0000080802227981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R35, desc[UR8][R2.64+0xc] ;                      /* 0x00000c0802237981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R24, desc[UR8][R2.64+0x10] ;                     /* 0x0000100802187981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R25, desc[UR8][R2.64+0x14] ;                     /* 0x0000140802197981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R26, desc[UR8][R2.64+0x18] ;                     /* 0x00001808021a7981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R27, desc[UR8][R2.64+0x1c] ;                     /* 0x00001c08021b7981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*0620*/                   LDG.E R28, desc[UR8][R2.64+0x20] ;                     /* 0x00002008021c7981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*0630*/                   LDG.E R29, desc[UR8][R2.64+0x24] ;                     /* 0x00002408021d7981 */
        |                                                                                          /* 0x000162000c1e1900 */
        |        /*0640*/                   S2UR UR5, SR_CgaCtaId ;                                /* 0x00000000000579c3 */
        |                                                                                          /* 0x000e620000008800 */
        |        /*0650*/                   UMOV UR4, 0x400 ;                                      /* 0x0000040000047882 */
        |                                                                                          /* 0x000fe20000000000 */
        |        /*0660*/                   ULDC.64 UR6, c[0x0][0x228] ;                           /* 0x00008a0000067ab9 */
        |                                                                                          /* 0x000fe20000000a00 */
        |        /*0670*/                   MEMBAR.ALL.GPU ;                                       /* 0x0000000000007992 */
        |                                                                                          /* 0x000fec000000a000 */
        |        /*0680*/                   FENCE.VIEW.ASYNC.S ;                                   /* 0x00000000000073c6 */
        |                                                                                          /* 0x000f620000000000 */
        |        /*0690*/                   LDC.64 R2, c[0x0][0x238] ;                             /* 0x00008e00ff027b82 */
        |                                                                                          /* 0x001e220000000a00 */
        |        /*06a0*/                   ULEA UR4, UR5, UR4, 0x18 ;                             /* 0x0000000405047291 */
        |                                                                                          /* 0x002fc6000f8ec03f */
        |        /*06b0*/                   UMOV UR5, URZ ;                                        /* 0x0000003f00057c82 */
        |                                                                                          /* 0x000fe20008000000 */
        |        /*06c0*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;         /* 0x0003ffff04047892 */
        |                                                                                          /* 0x000fc8000f8ec03f */
        |        /*06d0*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                        /* 0x0000000404047899 */
        |                                                                                          /* 0x000fe2000800123f */
        |        /*06e0*/                   IMAD.WIDE.U32 R2, R0, 0x18, R2 ;                       /* 0x0000001800027825 */
        |                                                                                          /* 0x001fc600078e0002 */
        |        /*06f0*/                   UIADD3.64 UR6, UR4, UR6, URZ ;                         /* 0x0000000604067297 */
        |                                                                                          /* 0x000fe2000fffe03f */
        |        /*0700*/                   WARPGROUP.ARRIVE ;                                     /* 0x00000000000079c5 */
        |                                                                                          /* 0x020fd00000000000 */
        |        /*0710*/                   HGMMA.64x24x16.F16 R24, -R32, gdesc[UR4], R24, gsb0 ;  /* 0x0040000420187df0 */
        |                                                                                          /* 0x000fe60008000118 */
        |        /*0720*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                        /* 0x00008000000079c5 */
        |                                                                                          /* 0x000fe40000010000 */
        |        /*0730*/                   STG.E desc[UR8][R2.64], R24 ;                          /* 0x0000001802007986 */
        |                                                                                          /* 0x000fe8000c101908 */
        |        /*0740*/                   STG.E desc[UR8][R2.64+0x4], R25 ;                      /* 0x0000041902007986 */
        |                                                                                          /* 0x000fe8000c101908 */
        |        /*0750*/                   STG.E desc[UR8][R2.64+0x8], R26 ;                      /* 0x0000081a02007986 */
        |                                                                                          /* 0x000fe8000c101908 */
        |        /*0760*/                   STG.E desc[UR8][R2.64+0xc], R27 ;                      /* 0x00000c1b02007986 */
        |                                                                                          /* 0x000fe8000c101908 */
        |        /*0770*/                   STG.E desc[UR8][R2.64+0x10], R28 ;                     /* 0x0000101c02007986 */
        |                                                                                          /* 0x000fe8000c101908 */
        |        /*0780*/                   STG.E desc[UR8][R2.64+0x14], R29 ;                     /* 0x0000141d02007986 */
        |                                                                                          /* 0x000fe2000c101908 */
        |        /*0790*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma2Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                          /* 0x00000a00ff017b82 */
        |                                                                                                   /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                              /* 0x0000000000007919 */
        |                                                                                                   /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                         /* 0x00008600ff027b82 */
        |                                                                                                   /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                                    /* 0x0000820000067ab9 */
        |                                                                                                   /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                                   /* 0x00008400000c7ab9 */
        |                                                                                                   /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                            /* 0x0000051000007945 */
        |                                                                                                   /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                           /* 0x000000020000720c */
        |                                                                                                   /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                                 /* 0x0000000400380947 */
        |                                                                                                   /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                                   /* 0x000000ffff037224 */
        |                                                                                                   /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                           /* 0x000000020300720c */
        |                                                                                                   /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                      /* 0x00008400ff042b82 */
        |                                                                                                   /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                      /* 0x0000000403042210 */
        |                                                                                                   /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                     /* 0x000000ffff052224 */
        |                                                                                                   /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                                 /* 0x0000000604042981 */
        |                                                                                                   /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                                 /* 0x0000040000062802 */
        |                                                                                                   /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                            /* 0x0000027000017945 */
        |                                                                                                   /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                        /* 0x000000000000781c */
        |                                                                                                   /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                           /* 0x0000000000072919 */
        |                                                                                                   /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                          /* 0x0000000607062211 */
        |                                                                                                   /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                               /* 0x0000000103072824 */
        |                                                                                                   /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                            /* 0x0000008003032836 */
        |                                                                                                   /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                    /* 0x0000000102067824 */
        |                                                                                                   /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                           /* 0x000000020300720c */
        |                                                                                                   /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                         /* 0x000001800600780c */
        |                                                                                                   /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                               /* 0x0000000407002388 */
        |                                                                                                   /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                                 /* 0x0000000000700947 */
        |                                                                                                   /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                           /* 0x0000000000057919 */
        |                                                                                                   /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                                 /* 0x0000040000047802 */
        |                                                                                                   /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                     /* 0xfffffe80020c7836 */
        |                                                                                                   /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                        /* 0x000000000000781c */
        |                                                                                                   /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                         /* 0x00000004050e7211 */
        |                                                                                                   /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                              /* 0x0000000c03047c10 */
        |                                                                                                   /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                      /* 0x0000008003067836 */
        |                                                                                                   /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                     /* 0x0000010003087836 */
        |                                                                                                   /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                          /* 0x00000180030a7836 */
        |                                                                                                   /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                                    /* 0x0000000c06067c10 */
        |                                                                                                   /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                                   /* 0x0000000dff057e24 */
        |                                                                                                   /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                                    /* 0x0000000c08087c10 */
        |                                                                                                   /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                                  /* 0x0000000c0a0a7c10 */
        |                                                                                                   /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                                   /* 0x0000000dff077e24 */
        |                                                                                                   /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                 /* 0x0000000604047981 */
        |                                                                                                   /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                                   /* 0x0000000dff097e24 */
        |                                                                                                   /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                                  /* 0x0000000dff0b7e24 */
        |                                                                                                   /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                 /* 0x0000000606067981 */
        |                                                                                                   /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                                 /* 0x0000000608087981 */
        |                                                                                                   /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                               /* 0x000000060a0a7981 */
        |                                                                                                   /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                   /* 0x000000010e0d7824 */
        |                                                                                                   /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                           /* 0x0000020003037836 */
        |                                                                                                   /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                          /* 0x0000000c0300720c */
        |                                                                                                   /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                              /* 0x000000040d007388 */
        |                                                                                                   /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                         /* 0x000080060d007388 */
        |                                                                                                   /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                        /* 0x000100080d007388 */
        |                                                                                                   /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                       /* 0x0001800a0d007388 */
        |                                                                                                   /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                                 /* 0xfffffffc00a48947 */
        |                                                                                                   /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                      /* 0x0000000000017941 */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                    /* 0x0000000102047824 */
        |                                                                                                   /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                           /* 0x000000020300720c */
        |                                                                                                   /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                            /* 0x0000012000017945 */
        |                                                                                                   /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                          /* 0x000000800400780c */
        |                                                                                                   /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                                 /* 0x00000000003c0947 */
        |                                                                                                   /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                      /* 0x0000008003047836 */
        |                                                                                                   /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                    /* 0x0000840000047ab9 */
        |                                                                                                   /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                     /* 0x0000000403067c10 */
        |                                                                                                   /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                     /* 0x0000000404047c10 */
        |                                                                                                   /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                    /* 0x00000005ff077e24 */
        |                                                                                                   /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                    /* 0x00000005ff057e24 */
        |                                                                                                   /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                 /* 0x0000000606067981 */
        |                                                                                                   /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                 /* 0x0000000604047981 */
        |                                                                                                   /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                         /* 0x00000000000579c3 */
        |                                                                                                   /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                               /* 0x0000040000047882 */
        |                                                                                                   /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                        /* 0x000000000000781c */
        |                                                                                                   /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                      /* 0x0000000405047291 */
        |                                                                                                   /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                           /* 0x0000000603007988 */
        |                                                                                                   /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                      /* 0x0000800403007988 */
        |                                                                                                   /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                           /* 0x0000010003037836 */
        |                                                                                                   /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                      /* 0x0000000000017941 */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                           /* 0x000000020300720c */
        |                                                                                                   /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                             /* 0x0000000000209947 */
        |                                                                                                   /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                    /* 0x0000840000047ab9 */
        |                                                                                                   /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                     /* 0x0000000403047c10 */
        |                                                                                                   /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                    /* 0x00000005ff057e24 */
        |                                                                                                   /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                 /* 0x0000000604047981 */
        |                                                                                                   /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                         /* 0x00000000000579c3 */
        |                                                                                                   /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                               /* 0x0000040000047882 */
        |                                                                                                   /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                      /* 0x0000000405047291 */
        |                                                                                                   /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                           /* 0x0000000403007988 */
        |                                                                                                   /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                      /* 0x0000000000007941 */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                      /* 0x00008c00ff027b82 */
        |                                                                                                   /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x94, R2 ;                                /* 0x0000009400027825 */
        |                                                                                                   /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                   /* 0x0000000000007b1d */
        |                                                                                                   /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R24, desc[UR6][R2.64+0x10] ;                              /* 0x0000100602187981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R25, desc[UR6][R2.64+0x14] ;                              /* 0x0000140602197981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R26, desc[UR6][R2.64+0x18] ;                              /* 0x00001806021a7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R27, desc[UR6][R2.64+0x1c] ;                              /* 0x00001c06021b7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R28, desc[UR6][R2.64+0x20] ;                              /* 0x00002006021c7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R29, desc[UR6][R2.64+0x24] ;                              /* 0x00002406021d7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R30, desc[UR6][R2.64+0x28] ;                              /* 0x00002806021e7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R31, desc[UR6][R2.64+0x2c] ;                              /* 0x00002c06021f7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0620*/                   LDG.E R32, desc[UR6][R2.64+0x30] ;                              /* 0x0000300602207981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0630*/                   LDG.E R33, desc[UR6][R2.64+0x34] ;                              /* 0x0000340602217981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0640*/                   LDG.E R34, desc[UR6][R2.64+0x38] ;                              /* 0x0000380602227981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0650*/                   LDG.E R35, desc[UR6][R2.64+0x3c] ;                              /* 0x00003c0602237981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0660*/                   LDG.E R36, desc[UR6][R2.64+0x40] ;                              /* 0x0000400602247981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0670*/                   LDG.E R37, desc[UR6][R2.64+0x44] ;                              /* 0x0000440602257981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0680*/                   LDG.E R38, desc[UR6][R2.64+0x48] ;                              /* 0x0000480602267981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0690*/                   LDG.E R39, desc[UR6][R2.64+0x4c] ;                              /* 0x00004c0602277981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*06a0*/                   LDG.E R40, desc[UR6][R2.64+0x50] ;                              /* 0x0000500602287981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*06b0*/                   LDG.E R41, desc[UR6][R2.64+0x54] ;                              /* 0x0000540602297981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*06c0*/                   LDG.E R42, desc[UR6][R2.64+0x58] ;                              /* 0x00005806022a7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*06d0*/                   LDG.E R43, desc[UR6][R2.64+0x5c] ;                              /* 0x00005c06022b7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*06e0*/                   LDG.E R44, desc[UR6][R2.64+0x60] ;                              /* 0x00006006022c7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*06f0*/                   LDG.E R45, desc[UR6][R2.64+0x64] ;                              /* 0x00006406022d7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0700*/                   LDG.E R46, desc[UR6][R2.64+0x68] ;                              /* 0x00006806022e7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0710*/                   LDG.E R47, desc[UR6][R2.64+0x6c] ;                              /* 0x00006c06022f7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0720*/                   LDG.E R48, desc[UR6][R2.64+0x70] ;                              /* 0x0000700602307981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0730*/                   LDG.E R49, desc[UR6][R2.64+0x74] ;                              /* 0x0000740602317981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0740*/                   LDG.E R50, desc[UR6][R2.64+0x78] ;                              /* 0x0000780602327981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0750*/                   LDG.E R51, desc[UR6][R2.64+0x7c] ;                              /* 0x00007c0602337981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0760*/                   LDG.E R52, desc[UR6][R2.64+0x80] ;                              /* 0x0000800602347981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0770*/                   LDG.E R53, desc[UR6][R2.64+0x84] ;                              /* 0x0000840602357981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0780*/                   LDG.E R54, desc[UR6][R2.64+0x88] ;                              /* 0x0000880602367981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*0790*/                   LDG.E R55, desc[UR6][R2.64+0x8c] ;                              /* 0x00008c0602377981 */
        |                                                                                                   /* 0x000162000c1e1900 */
        |        /*07a0*/                   S2UR UR5, SR_CgaCtaId ;                                         /* 0x00000000000579c3 */
        |                                                                                                   /* 0x000e620000008800 */
        |        /*07b0*/                   UMOV UR4, 0x400 ;                                               /* 0x0000040000047882 */
        |                                                                                                   /* 0x000fe20000000000 */
        |        /*07c0*/                   ULDC.64 UR8, c[0x0][0x220] ;                                    /* 0x0000880000087ab9 */
        |                                                                                                   /* 0x000fe20000000a00 */
        |        /*07d0*/                   MEMBAR.ALL.GPU ;                                                /* 0x0000000000007992 */
        |                                                                                                   /* 0x000fec000000a000 */
        |        /*07e0*/                   FENCE.VIEW.ASYNC.S ;                                            /* 0x00000000000073c6 */
        |                                                                                                   /* 0x000f620000000000 */
        |        /*07f0*/                   ULDC.64 UR10, c[0x0][0x228] ;                                   /* 0x00008a00000a7ab9 */
        |                                                                                                   /* 0x000fe20000000a00 */
        |        /*0800*/                   LDC.64 R2, c[0x0][0x238] ;                                      /* 0x00008e00ff027b82 */
        |                                                                                                   /* 0x001e220000000a00 */
        |        /*0810*/                   ULEA UR4, UR5, UR4, 0x18 ;                                      /* 0x0000000405047291 */
        |                                                                                                   /* 0x002fc6000f8ec03f */
        |        /*0820*/                   ULDC UR5, c[0x0][0x240] ;                                       /* 0x0000900000057ab9 */
        |                                                                                                   /* 0x000fe20000000800 */
        |        /*0830*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;                  /* 0x0003ffff04047892 */
        |                                                                                                   /* 0x000fe4000f8ec03f */
        |        /*0840*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                     /* 0x0000003f0500728c */
        |                                                                                                   /* 0x000fe4000bf05070 */
        |        /*0850*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                                 /* 0x0000000404047899 */
        |                                                                                                   /* 0x000fe2000800123f */
        |        /*0860*/                   IMAD.WIDE.U32 R2, R0, 0x80, R2 ;                                /* 0x0000008000027825 */
        |                                                                                                   /* 0x001fe200078e0002 */
        |        /*0870*/                   UMOV UR5, URZ ;                                                 /* 0x0000003f00057c82 */
        |                                                                                                   /* 0x000fe40008000000 */
        |        /*0880*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                                  /* 0x0000000804087297 */
        |                                                                                                   /* 0x000fe4000fffe03f */
        |        /*0890*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                                /* 0x0000000a040a7297 */
        |                                                                                                   /* 0x000fe2000fffe03f */
        |        /*08a0*/                   WARPGROUP.ARRIVE ;                                              /* 0x00000000000079c5 */
        |                                                                                                   /* 0x020fd00000000000 */
        |        /*08b0*/                   HGMMA.64x64x16.F32.BF16 R24, gdesc[UR8].negB, R24, UP0, gsb0 ;  /* 0x80e00000081879f0 */
        |                                                                                                   /* 0x000fe6000b801818 */
        |        /*08c0*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                                 /* 0x00008000000079c5 */
        |                                                                                                   /* 0x000fe40000010000 */
        |        /*08d0*/                   STG.E desc[UR6][R2.64], R24 ;                                   /* 0x0000001802007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*08e0*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                               /* 0x0000041902007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*08f0*/                   STG.E desc[UR6][R2.64+0x8], R26 ;                               /* 0x0000081a02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0900*/                   STG.E desc[UR6][R2.64+0xc], R27 ;                               /* 0x00000c1b02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0910*/                   STG.E desc[UR6][R2.64+0x10], R28 ;                              /* 0x0000101c02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0920*/                   STG.E desc[UR6][R2.64+0x14], R29 ;                              /* 0x0000141d02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0930*/                   STG.E desc[UR6][R2.64+0x18], R30 ;                              /* 0x0000181e02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0940*/                   STG.E desc[UR6][R2.64+0x1c], R31 ;                              /* 0x00001c1f02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0950*/                   STG.E desc[UR6][R2.64+0x20], R32 ;                              /* 0x0000202002007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0960*/                   STG.E desc[UR6][R2.64+0x24], R33 ;                              /* 0x0000242102007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0970*/                   STG.E desc[UR6][R2.64+0x28], R34 ;                              /* 0x0000282202007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0980*/                   STG.E desc[UR6][R2.64+0x2c], R35 ;                              /* 0x00002c2302007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0990*/                   STG.E desc[UR6][R2.64+0x30], R36 ;                              /* 0x0000302402007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*09a0*/                   STG.E desc[UR6][R2.64+0x34], R37 ;                              /* 0x0000342502007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*09b0*/                   STG.E desc[UR6][R2.64+0x38], R38 ;                              /* 0x0000382602007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*09c0*/                   STG.E desc[UR6][R2.64+0x3c], R39 ;                              /* 0x00003c2702007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*09d0*/                   STG.E desc[UR6][R2.64+0x40], R40 ;                              /* 0x0000402802007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*09e0*/                   STG.E desc[UR6][R2.64+0x44], R41 ;                              /* 0x0000442902007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*09f0*/                   STG.E desc[UR6][R2.64+0x48], R42 ;                              /* 0x0000482a02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0a00*/                   STG.E desc[UR6][R2.64+0x4c], R43 ;                              /* 0x00004c2b02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0a10*/                   STG.E desc[UR6][R2.64+0x50], R44 ;                              /* 0x0000502c02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0a20*/                   STG.E desc[UR6][R2.64+0x54], R45 ;                              /* 0x0000542d02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0a30*/                   STG.E desc[UR6][R2.64+0x58], R46 ;                              /* 0x0000582e02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0a40*/                   STG.E desc[UR6][R2.64+0x5c], R47 ;                              /* 0x00005c2f02007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0a50*/                   STG.E desc[UR6][R2.64+0x60], R48 ;                              /* 0x0000603002007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0a60*/                   STG.E desc[UR6][R2.64+0x64], R49 ;                              /* 0x0000643102007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0a70*/                   STG.E desc[UR6][R2.64+0x68], R50 ;                              /* 0x0000683202007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0a80*/                   STG.E desc[UR6][R2.64+0x6c], R51 ;                              /* 0x00006c3302007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0a90*/                   STG.E desc[UR6][R2.64+0x70], R52 ;                              /* 0x0000703402007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0aa0*/                   STG.E desc[UR6][R2.64+0x74], R53 ;                              /* 0x0000743502007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0ab0*/                   STG.E desc[UR6][R2.64+0x78], R54 ;                              /* 0x0000783602007986 */
        |                                                                                                   /* 0x000fe8000c101906 */
        |        /*0ac0*/                   STG.E desc[UR6][R2.64+0x7c], R55 ;                              /* 0x00007c3702007986 */
        |                                                                                                   /* 0x000fe2000c101906 */
        |        /*0ad0*/                   EXIT ;                                                          /* 0x000000000000794d */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma3Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                            /* 0x00000a00ff017b82 */
        |                                                                                                     /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                                /* 0x0000000000007919 */
        |                                                                                                     /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                           /* 0x00008600ff027b82 */
        |                                                                                                     /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                                      /* 0x0000820000067ab9 */
        |                                                                                                     /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                                     /* 0x00008400000c7ab9 */
        |                                                                                                     /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                              /* 0x0000051000007945 */
        |                                                                                                     /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                             /* 0x000000020000720c */
        |                                                                                                     /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                                   /* 0x0000000400380947 */
        |                                                                                                     /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                                     /* 0x000000ffff037224 */
        |                                                                                                     /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                             /* 0x000000020300720c */
        |                                                                                                     /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                        /* 0x00008400ff042b82 */
        |                                                                                                     /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                        /* 0x0000000403042210 */
        |                                                                                                     /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                       /* 0x000000ffff052224 */
        |                                                                                                     /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                                   /* 0x0000000604042981 */
        |                                                                                                     /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                                   /* 0x0000040000062802 */
        |                                                                                                     /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                              /* 0x0000027000017945 */
        |                                                                                                     /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                          /* 0x000000000000781c */
        |                                                                                                     /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                             /* 0x0000000000072919 */
        |                                                                                                     /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                            /* 0x0000000607062211 */
        |                                                                                                     /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                                 /* 0x0000000103072824 */
        |                                                                                                     /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                              /* 0x0000008003032836 */
        |                                                                                                     /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                      /* 0x0000000102067824 */
        |                                                                                                     /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                             /* 0x000000020300720c */
        |                                                                                                     /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                           /* 0x000001800600780c */
        |                                                                                                     /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                                 /* 0x0000000407002388 */
        |                                                                                                     /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                                   /* 0x0000000000700947 */
        |                                                                                                     /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                             /* 0x0000000000057919 */
        |                                                                                                     /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                                   /* 0x0000040000047802 */
        |                                                                                                     /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                       /* 0xfffffe80020c7836 */
        |                                                                                                     /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                          /* 0x000000000000781c */
        |                                                                                                     /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                           /* 0x00000004050e7211 */
        |                                                                                                     /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                                /* 0x0000000c03047c10 */
        |                                                                                                     /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                        /* 0x0000008003067836 */
        |                                                                                                     /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                       /* 0x0000010003087836 */
        |                                                                                                     /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                            /* 0x00000180030a7836 */
        |                                                                                                     /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                                      /* 0x0000000c06067c10 */
        |                                                                                                     /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                                     /* 0x0000000dff057e24 */
        |                                                                                                     /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                                      /* 0x0000000c08087c10 */
        |                                                                                                     /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                                    /* 0x0000000c0a0a7c10 */
        |                                                                                                     /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                                     /* 0x0000000dff077e24 */
        |                                                                                                     /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                   /* 0x0000000604047981 */
        |                                                                                                     /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                                     /* 0x0000000dff097e24 */
        |                                                                                                     /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                                    /* 0x0000000dff0b7e24 */
        |                                                                                                     /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                   /* 0x0000000606067981 */
        |                                                                                                     /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                                   /* 0x0000000608087981 */
        |                                                                                                     /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                                 /* 0x000000060a0a7981 */
        |                                                                                                     /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                     /* 0x000000010e0d7824 */
        |                                                                                                     /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                             /* 0x0000020003037836 */
        |                                                                                                     /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                            /* 0x0000000c0300720c */
        |                                                                                                     /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                                /* 0x000000040d007388 */
        |                                                                                                     /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                           /* 0x000080060d007388 */
        |                                                                                                     /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                          /* 0x000100080d007388 */
        |                                                                                                     /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                         /* 0x0001800a0d007388 */
        |                                                                                                     /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                                   /* 0xfffffffc00a48947 */
        |                                                                                                     /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                        /* 0x0000000000017941 */
        |                                                                                                     /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                      /* 0x0000000102047824 */
        |                                                                                                     /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                             /* 0x000000020300720c */
        |                                                                                                     /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                              /* 0x0000012000017945 */
        |                                                                                                     /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                            /* 0x000000800400780c */
        |                                                                                                     /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                                   /* 0x00000000003c0947 */
        |                                                                                                     /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                        /* 0x0000008003047836 */
        |                                                                                                     /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                      /* 0x0000840000047ab9 */
        |                                                                                                     /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                       /* 0x0000000403067c10 */
        |                                                                                                     /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                       /* 0x0000000404047c10 */
        |                                                                                                     /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                      /* 0x00000005ff077e24 */
        |                                                                                                     /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                      /* 0x00000005ff057e24 */
        |                                                                                                     /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                   /* 0x0000000606067981 */
        |                                                                                                     /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                   /* 0x0000000604047981 */
        |                                                                                                     /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                           /* 0x00000000000579c3 */
        |                                                                                                     /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                                 /* 0x0000040000047882 */
        |                                                                                                     /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                          /* 0x000000000000781c */
        |                                                                                                     /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                        /* 0x0000000405047291 */
        |                                                                                                     /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                             /* 0x0000000603007988 */
        |                                                                                                     /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                        /* 0x0000800403007988 */
        |                                                                                                     /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                             /* 0x0000010003037836 */
        |                                                                                                     /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                        /* 0x0000000000017941 */
        |                                                                                                     /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                             /* 0x000000020300720c */
        |                                                                                                     /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                               /* 0x0000000000209947 */
        |                                                                                                     /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                      /* 0x0000840000047ab9 */
        |                                                                                                     /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                       /* 0x0000000403047c10 */
        |                                                                                                     /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                      /* 0x00000005ff057e24 */
        |                                                                                                     /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                   /* 0x0000000604047981 */
        |                                                                                                     /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                           /* 0x00000000000579c3 */
        |                                                                                                     /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                                 /* 0x0000040000047882 */
        |                                                                                                     /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                        /* 0x0000000405047291 */
        |                                                                                                     /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                             /* 0x0000000403007988 */
        |                                                                                                     /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                        /* 0x0000000000007941 */
        |                                                                                                     /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                        /* 0x00008c00ff027b82 */
        |                                                                                                     /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x94, R2 ;                                  /* 0x0000009400027825 */
        |                                                                                                     /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                     /* 0x0000000000007b1d */
        |                                                                                                     /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R24, desc[UR6][R2.64+0x10] ;                                /* 0x0000100602187981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R25, desc[UR6][R2.64+0x14] ;                                /* 0x0000140602197981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R26, desc[UR6][R2.64+0x18] ;                                /* 0x00001806021a7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R27, desc[UR6][R2.64+0x1c] ;                                /* 0x00001c06021b7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R28, desc[UR6][R2.64+0x20] ;                                /* 0x00002006021c7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R29, desc[UR6][R2.64+0x24] ;                                /* 0x00002406021d7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R30, desc[UR6][R2.64+0x28] ;                                /* 0x00002806021e7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R31, desc[UR6][R2.64+0x2c] ;                                /* 0x00002c06021f7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0620*/                   LDG.E R32, desc[UR6][R2.64+0x30] ;                                /* 0x0000300602207981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0630*/                   LDG.E R33, desc[UR6][R2.64+0x34] ;                                /* 0x0000340602217981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0640*/                   LDG.E R34, desc[UR6][R2.64+0x38] ;                                /* 0x0000380602227981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0650*/                   LDG.E R35, desc[UR6][R2.64+0x3c] ;                                /* 0x00003c0602237981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0660*/                   LDG.E R36, desc[UR6][R2.64+0x40] ;                                /* 0x0000400602247981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0670*/                   LDG.E R37, desc[UR6][R2.64+0x44] ;                                /* 0x0000440602257981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0680*/                   LDG.E R38, desc[UR6][R2.64+0x48] ;                                /* 0x0000480602267981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0690*/                   LDG.E R39, desc[UR6][R2.64+0x4c] ;                                /* 0x00004c0602277981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*06a0*/                   LDG.E R40, desc[UR6][R2.64+0x50] ;                                /* 0x0000500602287981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*06b0*/                   LDG.E R41, desc[UR6][R2.64+0x54] ;                                /* 0x0000540602297981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*06c0*/                   LDG.E R42, desc[UR6][R2.64+0x58] ;                                /* 0x00005806022a7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*06d0*/                   LDG.E R43, desc[UR6][R2.64+0x5c] ;                                /* 0x00005c06022b7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*06e0*/                   LDG.E R44, desc[UR6][R2.64+0x60] ;                                /* 0x00006006022c7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*06f0*/                   LDG.E R45, desc[UR6][R2.64+0x64] ;                                /* 0x00006406022d7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0700*/                   LDG.E R46, desc[UR6][R2.64+0x68] ;                                /* 0x00006806022e7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0710*/                   LDG.E R47, desc[UR6][R2.64+0x6c] ;                                /* 0x00006c06022f7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0720*/                   LDG.E R48, desc[UR6][R2.64+0x70] ;                                /* 0x0000700602307981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0730*/                   LDG.E R49, desc[UR6][R2.64+0x74] ;                                /* 0x0000740602317981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0740*/                   LDG.E R50, desc[UR6][R2.64+0x78] ;                                /* 0x0000780602327981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0750*/                   LDG.E R51, desc[UR6][R2.64+0x7c] ;                                /* 0x00007c0602337981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0760*/                   LDG.E R52, desc[UR6][R2.64+0x80] ;                                /* 0x0000800602347981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0770*/                   LDG.E R53, desc[UR6][R2.64+0x84] ;                                /* 0x0000840602357981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0780*/                   LDG.E R54, desc[UR6][R2.64+0x88] ;                                /* 0x0000880602367981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*0790*/                   LDG.E R55, desc[UR6][R2.64+0x8c] ;                                /* 0x00008c0602377981 */
        |                                                                                                     /* 0x000162000c1e1900 */
        |        /*07a0*/                   S2UR UR5, SR_CgaCtaId ;                                           /* 0x00000000000579c3 */
        |                                                                                                     /* 0x000e620000008800 */
        |        /*07b0*/                   UMOV UR4, 0x400 ;                                                 /* 0x0000040000047882 */
        |                                                                                                     /* 0x000fe20000000000 */
        |        /*07c0*/                   ULDC.64 UR8, c[0x0][0x220] ;                                      /* 0x0000880000087ab9 */
        |                                                                                                     /* 0x000fe20000000a00 */
        |        /*07d0*/                   MEMBAR.ALL.GPU ;                                                  /* 0x0000000000007992 */
        |                                                                                                     /* 0x000fec000000a000 */
        |        /*07e0*/                   FENCE.VIEW.ASYNC.S ;                                              /* 0x00000000000073c6 */
        |                                                                                                     /* 0x000f620000000000 */
        |        /*07f0*/                   ULDC.64 UR10, c[0x0][0x228] ;                                     /* 0x00008a00000a7ab9 */
        |                                                                                                     /* 0x000fe20000000a00 */
        |        /*0800*/                   LDC.64 R2, c[0x0][0x238] ;                                        /* 0x00008e00ff027b82 */
        |                                                                                                     /* 0x001e220000000a00 */
        |        /*0810*/                   ULEA UR4, UR5, UR4, 0x18 ;                                        /* 0x0000000405047291 */
        |                                                                                                     /* 0x002fc6000f8ec03f */
        |        /*0820*/                   ULDC UR5, c[0x0][0x240] ;                                         /* 0x0000900000057ab9 */
        |                                                                                                     /* 0x000fe20000000800 */
        |        /*0830*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;                    /* 0x0003ffff04047892 */
        |                                                                                                     /* 0x000fe4000f8ec03f */
        |        /*0840*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                       /* 0x0000003f0500728c */
        |                                                                                                     /* 0x000fe4000bf05070 */
        |        /*0850*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                                   /* 0x0000000404047899 */
        |                                                                                                     /* 0x000fe2000800123f */
        |        /*0860*/                   IMAD.WIDE.U32 R2, R0, 0x80, R2 ;                                  /* 0x0000008000027825 */
        |                                                                                                     /* 0x001fe200078e0002 */
        |        /*0870*/                   UMOV UR5, URZ ;                                                   /* 0x0000003f00057c82 */
        |                                                                                                     /* 0x000fe40008000000 */
        |        /*0880*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                                    /* 0x0000000804087297 */
        |                                                                                                     /* 0x000fe4000fffe03f */
        |        /*0890*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                                  /* 0x0000000a040a7297 */
        |                                                                                                     /* 0x000fe2000fffe03f */
        |        /*08a0*/                   WARPGROUP.ARRIVE ;                                                /* 0x00000000000079c5 */
        |                                                                                                     /* 0x020fd00000000000 */
        |        /*08b0*/                   HGMMA.64x64x16.F32 R24, gdesc[UR8].tnspA.tnspB, R24, UP0, gsb0 ;  /* 0x60e00000081879f0 */
        |                                                                                                     /* 0x000fe6000b800818 */
        |        /*08c0*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                                   /* 0x00008000000079c5 */
        |                                                                                                     /* 0x000fe40000010000 */
        |        /*08d0*/                   STG.E desc[UR6][R2.64], R24 ;                                     /* 0x0000001802007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*08e0*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                                 /* 0x0000041902007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*08f0*/                   STG.E desc[UR6][R2.64+0x8], R26 ;                                 /* 0x0000081a02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0900*/                   STG.E desc[UR6][R2.64+0xc], R27 ;                                 /* 0x00000c1b02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0910*/                   STG.E desc[UR6][R2.64+0x10], R28 ;                                /* 0x0000101c02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0920*/                   STG.E desc[UR6][R2.64+0x14], R29 ;                                /* 0x0000141d02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0930*/                   STG.E desc[UR6][R2.64+0x18], R30 ;                                /* 0x0000181e02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0940*/                   STG.E desc[UR6][R2.64+0x1c], R31 ;                                /* 0x00001c1f02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0950*/                   STG.E desc[UR6][R2.64+0x20], R32 ;                                /* 0x0000202002007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0960*/                   STG.E desc[UR6][R2.64+0x24], R33 ;                                /* 0x0000242102007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0970*/                   STG.E desc[UR6][R2.64+0x28], R34 ;                                /* 0x0000282202007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0980*/                   STG.E desc[UR6][R2.64+0x2c], R35 ;                                /* 0x00002c2302007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0990*/                   STG.E desc[UR6][R2.64+0x30], R36 ;                                /* 0x0000302402007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*09a0*/                   STG.E desc[UR6][R2.64+0x34], R37 ;                                /* 0x0000342502007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*09b0*/                   STG.E desc[UR6][R2.64+0x38], R38 ;                                /* 0x0000382602007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*09c0*/                   STG.E desc[UR6][R2.64+0x3c], R39 ;                                /* 0x00003c2702007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*09d0*/                   STG.E desc[UR6][R2.64+0x40], R40 ;                                /* 0x0000402802007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*09e0*/                   STG.E desc[UR6][R2.64+0x44], R41 ;                                /* 0x0000442902007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*09f0*/                   STG.E desc[UR6][R2.64+0x48], R42 ;                                /* 0x0000482a02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0a00*/                   STG.E desc[UR6][R2.64+0x4c], R43 ;                                /* 0x00004c2b02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0a10*/                   STG.E desc[UR6][R2.64+0x50], R44 ;                                /* 0x0000502c02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0a20*/                   STG.E desc[UR6][R2.64+0x54], R45 ;                                /* 0x0000542d02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0a30*/                   STG.E desc[UR6][R2.64+0x58], R46 ;                                /* 0x0000582e02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0a40*/                   STG.E desc[UR6][R2.64+0x5c], R47 ;                                /* 0x00005c2f02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0a50*/                   STG.E desc[UR6][R2.64+0x60], R48 ;                                /* 0x0000603002007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0a60*/                   STG.E desc[UR6][R2.64+0x64], R49 ;                                /* 0x0000643102007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0a70*/                   STG.E desc[UR6][R2.64+0x68], R50 ;                                /* 0x0000683202007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0a80*/                   STG.E desc[UR6][R2.64+0x6c], R51 ;                                /* 0x00006c3302007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0a90*/                   STG.E desc[UR6][R2.64+0x70], R52 ;                                /* 0x0000703402007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0aa0*/                   STG.E desc[UR6][R2.64+0x74], R53 ;                                /* 0x0000743502007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0ab0*/                   STG.E desc[UR6][R2.64+0x78], R54 ;                                /* 0x0000783602007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0ac0*/                   STG.E desc[UR6][R2.64+0x7c], R55 ;                                /* 0x00007c3702007986 */
        |                                                                                                     /* 0x000fe2000c101906 */
        |        /*0ad0*/                   EXIT ;                                                            /* 0x000000000000794d */
        |                                                                                                     /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma4Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                          /* 0x00000a00ff017b82 */
        |                                                                                                   /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                              /* 0x0000000000007919 */
        |                                                                                                   /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                         /* 0x00008600ff027b82 */
        |                                                                                                   /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR8, c[0x0][0x208] ;                                    /* 0x0000820000087ab9 */
        |                                                                                                   /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR10, c[0x0][0x210] ;                                   /* 0x00008400000a7ab9 */
        |                                                                                                   /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                            /* 0x0000051000007945 */
        |                                                                                                   /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                           /* 0x000000020000720c */
        |                                                                                                   /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                                 /* 0x0000000400380947 */
        |                                                                                                   /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                                   /* 0x000000ffff037224 */
        |                                                                                                   /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                           /* 0x000000020300720c */
        |                                                                                                   /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                      /* 0x00008400ff042b82 */
        |                                                                                                   /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                      /* 0x0000000403042210 */
        |                                                                                                   /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                     /* 0x000000ffff052224 */
        |                                                                                                   /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR8][R4.64] ;                                 /* 0x0000000804042981 */
        |                                                                                                   /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                                 /* 0x0000040000062802 */
        |                                                                                                   /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                            /* 0x0000027000017945 */
        |                                                                                                   /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                        /* 0x000000000000781c */
        |                                                                                                   /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                           /* 0x0000000000072919 */
        |                                                                                                   /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                          /* 0x0000000607062211 */
        |                                                                                                   /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                               /* 0x0000000103072824 */
        |                                                                                                   /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                            /* 0x0000008003032836 */
        |                                                                                                   /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                    /* 0x0000000102067824 */
        |                                                                                                   /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                           /* 0x000000020300720c */
        |                                                                                                   /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                         /* 0x000001800600780c */
        |                                                                                                   /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                               /* 0x0000000407002388 */
        |                                                                                                   /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                                 /* 0x0000000000700947 */
        |                                                                                                   /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                           /* 0x0000000000057919 */
        |                                                                                                   /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                                 /* 0x0000040000047802 */
        |                                                                                                   /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                     /* 0xfffffe80020c7836 */
        |                                                                                                   /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                        /* 0x000000000000781c */
        |                                                                                                   /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                         /* 0x00000004050e7211 */
        |                                                                                                   /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR10, RZ ;                              /* 0x0000000a03047c10 */
        |                                                                                                   /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                      /* 0x0000008003067836 */
        |                                                                                                   /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                     /* 0x0000010003087836 */
        |                                                                                                   /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                          /* 0x00000180030a7836 */
        |                                                                                                   /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR10, RZ ;                                    /* 0x0000000a06067c10 */
        |                                                                                                   /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR11, P2 ;                                   /* 0x0000000bff057e24 */
        |                                                                                                   /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR10, RZ ;                                    /* 0x0000000a08087c10 */
        |                                                                                                   /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR10, RZ ;                                  /* 0x0000000a0a0a7c10 */
        |                                                                                                   /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR11, P0 ;                                   /* 0x0000000bff077e24 */
        |                                                                                                   /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                 /* 0x0000000804047981 */
        |                                                                                                   /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR11, P2 ;                                   /* 0x0000000bff097e24 */
        |                                                                                                   /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR11, P3 ;                                  /* 0x0000000bff0b7e24 */
        |                                                                                                   /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                                 /* 0x0000000806067981 */
        |                                                                                                   /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR8][R8.64] ;                                 /* 0x0000000808087981 */
        |                                                                                                   /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR8][R10.64] ;                               /* 0x000000080a0a7981 */
        |                                                                                                   /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                   /* 0x000000010e0d7824 */
        |                                                                                                   /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                           /* 0x0000020003037836 */
        |                                                                                                   /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                          /* 0x0000000c0300720c */
        |                                                                                                   /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                              /* 0x000000040d007388 */
        |                                                                                                   /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                         /* 0x000080060d007388 */
        |                                                                                                   /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                        /* 0x000100080d007388 */
        |                                                                                                   /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                       /* 0x0001800a0d007388 */
        |                                                                                                   /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                                 /* 0xfffffffc00a48947 */
        |                                                                                                   /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                      /* 0x0000000000017941 */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                    /* 0x0000000102047824 */
        |                                                                                                   /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                           /* 0x000000020300720c */
        |                                                                                                   /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                            /* 0x0000012000017945 */
        |                                                                                                   /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                          /* 0x000000800400780c */
        |                                                                                                   /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                                 /* 0x00000000003c0947 */
        |                                                                                                   /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                      /* 0x0000008003047836 */
        |                                                                                                   /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                    /* 0x0000840000047ab9 */
        |                                                                                                   /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                     /* 0x0000000403067c10 */
        |                                                                                                   /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                     /* 0x0000000404047c10 */
        |                                                                                                   /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                    /* 0x00000005ff077e24 */
        |                                                                                                   /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                    /* 0x00000005ff057e24 */
        |                                                                                                   /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                                 /* 0x0000000806067981 */
        |                                                                                                   /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                 /* 0x0000000804047981 */
        |                                                                                                   /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                         /* 0x00000000000579c3 */
        |                                                                                                   /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                               /* 0x0000040000047882 */
        |                                                                                                   /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                        /* 0x000000000000781c */
        |                                                                                                   /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                      /* 0x0000000405047291 */
        |                                                                                                   /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                           /* 0x0000000603007988 */
        |                                                                                                   /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                      /* 0x0000800403007988 */
        |                                                                                                   /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                           /* 0x0000010003037836 */
        |                                                                                                   /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                      /* 0x0000000000017941 */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                           /* 0x000000020300720c */
        |                                                                                                   /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                             /* 0x0000000000209947 */
        |                                                                                                   /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                    /* 0x0000840000047ab9 */
        |                                                                                                   /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                     /* 0x0000000403047c10 */
        |                                                                                                   /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                    /* 0x00000005ff057e24 */
        |                                                                                                   /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                 /* 0x0000000804047981 */
        |                                                                                                   /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                         /* 0x00000000000579c3 */
        |                                                                                                   /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                               /* 0x0000040000047882 */
        |                                                                                                   /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                      /* 0x0000000405047291 */
        |                                                                                                   /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                           /* 0x0000000403007988 */
        |                                                                                                   /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                      /* 0x0000000000007941 */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                      /* 0x00008c00ff027b82 */
        |                                                                                                   /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x1c, R2 ;                                /* 0x0000001c00027825 */
        |                                                                                                   /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                   /* 0x0000000000007b1d */
        |                                                                                                   /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R28, desc[UR8][R2.64+0x10] ;                              /* 0x00001008021c7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R29, desc[UR8][R2.64+0x14] ;                              /* 0x00001408021d7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R24, desc[UR8][R2.64] ;                                   /* 0x0000000802187981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R25, desc[UR8][R2.64+0x4] ;                               /* 0x0000040802197981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R26, desc[UR8][R2.64+0x8] ;                               /* 0x00000808021a7981 */
        |                                                                                                   /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R27, desc[UR8][R2.64+0xc] ;                               /* 0x00000c08021b7981 */
        |                                                                                                   /* 0x000162000c1e1900 */
        |        /*0600*/                   S2UR UR5, SR_CgaCtaId ;                                         /* 0x00000000000579c3 */
        |                                                                                                   /* 0x000e620000008800 */
        |        /*0610*/                   UMOV UR4, 0x400 ;                                               /* 0x0000040000047882 */
        |                                                                                                   /* 0x000fe20000000000 */
        |        /*0620*/                   ULDC.64 UR6, c[0x0][0x228] ;                                    /* 0x00008a0000067ab9 */
        |                                                                                                   /* 0x000fe20000000a00 */
        |        /*0630*/                   MEMBAR.ALL.GPU ;                                                /* 0x0000000000007992 */
        |                                                                                                   /* 0x000fec000000a000 */
        |        /*0640*/                   FENCE.VIEW.ASYNC.S ;                                            /* 0x00000000000073c6 */
        |                                                                                                   /* 0x000f620000000000 */
        |        /*0650*/                   LDC.64 R2, c[0x0][0x238] ;                                      /* 0x00008e00ff027b82 */
        |                                                                                                   /* 0x001e220000000a00 */
        |        /*0660*/                   ULEA UR4, UR5, UR4, 0x18 ;                                      /* 0x0000000405047291 */
        |                                                                                                   /* 0x002fc6000f8ec03f */
        |        /*0670*/                   ULDC UR5, c[0x0][0x240] ;                                       /* 0x0000900000057ab9 */
        |                                                                                                   /* 0x000fe20000000800 */
        |        /*0680*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;                  /* 0x0003ffff04047892 */
        |                                                                                                   /* 0x000fe4000f8ec03f */
        |        /*0690*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                     /* 0x0000003f0500728c */
        |                                                                                                   /* 0x000fe4000bf05070 */
        |        /*06a0*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                                 /* 0x0000000404047899 */
        |                                                                                                   /* 0x000fe2000800123f */
        |        /*06b0*/                   IMAD.WIDE.U32 R2, R0, 0x8, R2 ;                                 /* 0x0000000800027825 */
        |                                                                                                   /* 0x001fe200078e0002 */
        |        /*06c0*/                   UMOV UR5, URZ ;                                                 /* 0x0000003f00057c82 */
        |                                                                                                   /* 0x000fe40008000000 */
        |        /*06d0*/                   UIADD3.64 UR6, UR4, UR6, URZ ;                                  /* 0x0000000604067297 */
        |                                                                                                   /* 0x000fe2000fffe03f */
        |        /*06e0*/                   WARPGROUP.ARRIVE ;                                              /* 0x00000000000079c5 */
        |                                                                                                   /* 0x020fd00000000000 */
        |        /*06f0*/                   HGMMA.64x8x16.F16 R28, R24, gdesc[UR4].tnspB, R28, UP0, gsb0 ;  /* 0x40000004181c7df0 */
        |                                                                                                   /* 0x000fe6000b80001c */
        |        /*0700*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                                 /* 0x00008000000079c5 */
        |                                                                                                   /* 0x000fe40000010000 */
        |        /*0710*/                   STG.E desc[UR8][R2.64], R28 ;                                   /* 0x0000001c02007986 */
        |                                                                                                   /* 0x000fe8000c101908 */
        |        /*0720*/                   STG.E desc[UR8][R2.64+0x4], R29 ;                               /* 0x0000041d02007986 */
        |                                                                                                   /* 0x000fe2000c101908 */
        |        /*0730*/                   EXIT ;                                                          /* 0x000000000000794d */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma5Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                    /* 0x00000a00ff017b82 */
        |                                                                                             /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                        /* 0x0000000000007919 */
        |                                                                                             /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                   /* 0x00008600ff027b82 */
        |                                                                                             /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                              /* 0x0000820000067ab9 */
        |                                                                                             /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                             /* 0x00008400000c7ab9 */
        |                                                                                             /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                      /* 0x0000051000007945 */
        |                                                                                             /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                     /* 0x000000020000720c */
        |                                                                                             /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                           /* 0x0000000400380947 */
        |                                                                                             /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                             /* 0x000000ffff037224 */
        |                                                                                             /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                     /* 0x000000020300720c */
        |                                                                                             /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                /* 0x00008400ff042b82 */
        |                                                                                             /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                /* 0x0000000403042210 */
        |                                                                                             /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                               /* 0x000000ffff052224 */
        |                                                                                             /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                           /* 0x0000000604042981 */
        |                                                                                             /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                           /* 0x0000040000062802 */
        |                                                                                             /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                      /* 0x0000027000017945 */
        |                                                                                             /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                  /* 0x000000000000781c */
        |                                                                                             /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                     /* 0x0000000000072919 */
        |                                                                                             /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                    /* 0x0000000607062211 */
        |                                                                                             /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                         /* 0x0000000103072824 */
        |                                                                                             /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                      /* 0x0000008003032836 */
        |                                                                                             /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                              /* 0x0000000102067824 */
        |                                                                                             /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                     /* 0x000000020300720c */
        |                                                                                             /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                   /* 0x000001800600780c */
        |                                                                                             /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                         /* 0x0000000407002388 */
        |                                                                                             /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                           /* 0x0000000000700947 */
        |                                                                                             /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                     /* 0x0000000000057919 */
        |                                                                                             /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                           /* 0x0000040000047802 */
        |                                                                                             /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                               /* 0xfffffe80020c7836 */
        |                                                                                             /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                  /* 0x000000000000781c */
        |                                                                                             /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                   /* 0x00000004050e7211 */
        |                                                                                             /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                        /* 0x0000000c03047c10 */
        |                                                                                             /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                /* 0x0000008003067836 */
        |                                                                                             /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                               /* 0x0000010003087836 */
        |                                                                                             /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                    /* 0x00000180030a7836 */
        |                                                                                             /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                              /* 0x0000000c06067c10 */
        |                                                                                             /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                             /* 0x0000000dff057e24 */
        |                                                                                             /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                              /* 0x0000000c08087c10 */
        |                                                                                             /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                            /* 0x0000000c0a0a7c10 */
        |                                                                                             /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                             /* 0x0000000dff077e24 */
        |                                                                                             /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                           /* 0x0000000604047981 */
        |                                                                                             /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                             /* 0x0000000dff097e24 */
        |                                                                                             /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                            /* 0x0000000dff0b7e24 */
        |                                                                                             /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                           /* 0x0000000606067981 */
        |                                                                                             /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                           /* 0x0000000608087981 */
        |                                                                                             /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                         /* 0x000000060a0a7981 */
        |                                                                                             /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                             /* 0x000000010e0d7824 */
        |                                                                                             /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                     /* 0x0000020003037836 */
        |                                                                                             /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                    /* 0x0000000c0300720c */
        |                                                                                             /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                        /* 0x000000040d007388 */
        |                                                                                             /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                   /* 0x000080060d007388 */
        |                                                                                             /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                  /* 0x000100080d007388 */
        |                                                                                             /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                 /* 0x0001800a0d007388 */
        |                                                                                             /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                           /* 0xfffffffc00a48947 */
        |                                                                                             /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                /* 0x0000000000017941 */
        |                                                                                             /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                              /* 0x0000000102047824 */
        |                                                                                             /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                     /* 0x000000020300720c */
        |                                                                                             /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                      /* 0x0000012000017945 */
        |                                                                                             /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                    /* 0x000000800400780c */
        |                                                                                             /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                           /* 0x00000000003c0947 */
        |                                                                                             /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                /* 0x0000008003047836 */
        |                                                                                             /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                              /* 0x0000840000047ab9 */
        |                                                                                             /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                               /* 0x0000000403067c10 */
        |                                                                                             /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                               /* 0x0000000404047c10 */
        |                                                                                             /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                              /* 0x00000005ff077e24 */
        |                                                                                             /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                              /* 0x00000005ff057e24 */
        |                                                                                             /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                           /* 0x0000000606067981 */
        |                                                                                             /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                           /* 0x0000000604047981 */
        |                                                                                             /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                   /* 0x00000000000579c3 */
        |                                                                                             /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                         /* 0x0000040000047882 */
        |                                                                                             /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                  /* 0x000000000000781c */
        |                                                                                             /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                /* 0x0000000405047291 */
        |                                                                                             /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                     /* 0x0000000603007988 */
        |                                                                                             /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                /* 0x0000800403007988 */
        |                                                                                             /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                     /* 0x0000010003037836 */
        |                                                                                             /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                /* 0x0000000000017941 */
        |                                                                                             /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                     /* 0x000000020300720c */
        |                                                                                             /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                       /* 0x0000000000209947 */
        |                                                                                             /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                              /* 0x0000840000047ab9 */
        |                                                                                             /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                               /* 0x0000000403047c10 */
        |                                                                                             /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                              /* 0x00000005ff057e24 */
        |                                                                                             /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                           /* 0x0000000604047981 */
        |                                                                                             /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                   /* 0x00000000000579c3 */
        |                                                                                             /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                         /* 0x0000040000047882 */
        |                                                                                             /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                /* 0x0000000405047291 */
        |                                                                                             /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                     /* 0x0000000403007988 */
        |                                                                                             /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                /* 0x0000000000007941 */
        |                                                                                             /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                             /* 0x0000000000007b1d */
        |                                                                                             /* 0x000ff00000010000 */
        |        /*0580*/                   S2UR UR5, SR_CgaCtaId ;                                   /* 0x00000000000579c3 */
        |                                                                                             /* 0x000e620000008800 */
        |        /*0590*/                   UMOV UR4, 0x400 ;                                         /* 0x0000040000047882 */
        |                                                                                             /* 0x000fe20000000000 */
        |        /*05a0*/                   ULDC.64 UR8, c[0x0][0x220] ;                              /* 0x0000880000087ab9 */
        |                                                                                             /* 0x000fe20000000a00 */
        |        /*05b0*/                   ULDC.64 UR10, c[0x0][0x228] ;                             /* 0x00008a00000a7ab9 */
        |                                                                                             /* 0x000fca0000000a00 */
        |        /*05c0*/                   LDC.64 R2, c[0x0][0x238] ;                                /* 0x00008e00ff027b82 */
        |                                                                                             /* 0x001e220000000a00 */
        |        /*05d0*/                   MEMBAR.ALL.GPU ;                                          /* 0x0000000000007992 */
        |                                                                                             /* 0x000fec000000a000 */
        |        /*05e0*/                   FENCE.VIEW.ASYNC.S ;                                      /* 0x00000000000073c6 */
        |                                                                                             /* 0x000f620000000000 */
        |        /*05f0*/                   ULEA UR4, UR5, UR4, 0x18 ;                                /* 0x0000000405047291 */
        |                                                                                             /* 0x002fc6000f8ec03f */
        |        /*0600*/                   UMOV UR5, URZ ;                                           /* 0x0000003f00057c82 */
        |                                                                                             /* 0x000fe20008000000 */
        |        /*0610*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;            /* 0x0003ffff04047892 */
        |                                                                                             /* 0x000fc8000f8ec03f */
        |        /*0620*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                           /* 0x0000000404047899 */
        |                                                                                             /* 0x000fe2000800123f */
        |        /*0630*/                   IMAD.WIDE.U32 R2, R0, 0x20, R2 ;                          /* 0x0000002000027825 */
        |                                                                                             /* 0x001fc600078e0002 */
        |        /*0640*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                            /* 0x0000000804087297 */
        |                                                                                             /* 0x000fe4000fffe03f */
        |        /*0650*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                          /* 0x0000000a040a7297 */
        |                                                                                             /* 0x000fe2000fffe03f */
        |        /*0660*/                   WARPGROUP.ARRIVE ;                                        /* 0x00000000000079c5 */
        |                                                                                             /* 0x020fd00000000000 */
        |        /*0670*/                   HGMMA.64x16x8.F32.TF32 R24, gdesc[UR8], RZ, !UPT, gsb0 ;  /* 0x04200000081879f0 */
        |                                                                                             /* 0x000fe6000c0028ff */
        |        /*0680*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                           /* 0x00008000000079c5 */
        |                                                                                             /* 0x000fe40000010000 */
        |        /*0690*/                   STG.E desc[UR6][R2.64], R24 ;                             /* 0x0000001802007986 */
        |                                                                                             /* 0x000fe8000c101906 */
        |        /*06a0*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                         /* 0x0000041902007986 */
        |                                                                                             /* 0x000fe8000c101906 */
        |        /*06b0*/                   STG.E desc[UR6][R2.64+0x8], R26 ;                         /* 0x0000081a02007986 */
        |                                                                                             /* 0x000fe8000c101906 */
        |        /*06c0*/                   STG.E desc[UR6][R2.64+0xc], R27 ;                         /* 0x00000c1b02007986 */
        |                                                                                             /* 0x000fe8000c101906 */
        |        /*06d0*/                   STG.E desc[UR6][R2.64+0x10], R28 ;                        /* 0x0000101c02007986 */
        |                                                                                             /* 0x000fe8000c101906 */
        |        /*06e0*/                   STG.E desc[UR6][R2.64+0x14], R29 ;                        /* 0x0000141d02007986 */
        |                                                                                             /* 0x000fe8000c101906 */
        |        /*06f0*/                   STG.E desc[UR6][R2.64+0x18], R30 ;                        /* 0x0000181e02007986 */
        |                                                                                             /* 0x000fe8000c101906 */
        |        /*0700*/                   STG.E desc[UR6][R2.64+0x1c], R31 ;                        /* 0x00001c1f02007986 */
        |                                                                                             /* 0x000fe2000c101906 */
        |        /*0710*/                   EXIT ;                                                    /* 0x000000000000794d */
        |                                                                                             /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma6Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                           /* 0x00000a00ff017b82 */
        |                                                                                                    /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                               /* 0x0000000000007919 */
        |                                                                                                    /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                          /* 0x00008600ff027b82 */
        |                                                                                                    /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR8, c[0x0][0x208] ;                                     /* 0x0000820000087ab9 */
        |                                                                                                    /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR10, c[0x0][0x210] ;                                    /* 0x00008400000a7ab9 */
        |                                                                                                    /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                             /* 0x0000051000007945 */
        |                                                                                                    /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                            /* 0x000000020000720c */
        |                                                                                                    /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                                  /* 0x0000000400380947 */
        |                                                                                                    /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                                    /* 0x000000ffff037224 */
        |                                                                                                    /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                            /* 0x000000020300720c */
        |                                                                                                    /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                       /* 0x00008400ff042b82 */
        |                                                                                                    /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                       /* 0x0000000403042210 */
        |                                                                                                    /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                      /* 0x000000ffff052224 */
        |                                                                                                    /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR8][R4.64] ;                                  /* 0x0000000804042981 */
        |                                                                                                    /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                                  /* 0x0000040000062802 */
        |                                                                                                    /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                             /* 0x0000027000017945 */
        |                                                                                                    /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                         /* 0x000000000000781c */
        |                                                                                                    /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                            /* 0x0000000000072919 */
        |                                                                                                    /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                           /* 0x0000000607062211 */
        |                                                                                                    /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                                /* 0x0000000103072824 */
        |                                                                                                    /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                             /* 0x0000008003032836 */
        |                                                                                                    /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                     /* 0x0000000102067824 */
        |                                                                                                    /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                            /* 0x000000020300720c */
        |                                                                                                    /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                          /* 0x000001800600780c */
        |                                                                                                    /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                                /* 0x0000000407002388 */
        |                                                                                                    /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                                  /* 0x0000000000700947 */
        |                                                                                                    /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                            /* 0x0000000000057919 */
        |                                                                                                    /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                                  /* 0x0000040000047802 */
        |                                                                                                    /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                      /* 0xfffffe80020c7836 */
        |                                                                                                    /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                         /* 0x000000000000781c */
        |                                                                                                    /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                          /* 0x00000004050e7211 */
        |                                                                                                    /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR10, RZ ;                               /* 0x0000000a03047c10 */
        |                                                                                                    /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                       /* 0x0000008003067836 */
        |                                                                                                    /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                      /* 0x0000010003087836 */
        |                                                                                                    /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                           /* 0x00000180030a7836 */
        |                                                                                                    /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR10, RZ ;                                     /* 0x0000000a06067c10 */
        |                                                                                                    /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR11, P2 ;                                    /* 0x0000000bff057e24 */
        |                                                                                                    /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR10, RZ ;                                     /* 0x0000000a08087c10 */
        |                                                                                                    /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR10, RZ ;                                   /* 0x0000000a0a0a7c10 */
        |                                                                                                    /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR11, P0 ;                                    /* 0x0000000bff077e24 */
        |                                                                                                    /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                  /* 0x0000000804047981 */
        |                                                                                                    /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR11, P2 ;                                    /* 0x0000000bff097e24 */
        |                                                                                                    /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR11, P3 ;                                   /* 0x0000000bff0b7e24 */
        |                                                                                                    /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                                  /* 0x0000000806067981 */
        |                                                                                                    /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR8][R8.64] ;                                  /* 0x0000000808087981 */
        |                                                                                                    /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR8][R10.64] ;                                /* 0x000000080a0a7981 */
        |                                                                                                    /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                    /* 0x000000010e0d7824 */
        |                                                                                                    /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                            /* 0x0000020003037836 */
        |                                                                                                    /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                           /* 0x0000000c0300720c */
        |                                                                                                    /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                               /* 0x000000040d007388 */
        |                                                                                                    /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                          /* 0x000080060d007388 */
        |                                                                                                    /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                         /* 0x000100080d007388 */
        |                                                                                                    /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                        /* 0x0001800a0d007388 */
        |                                                                                                    /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                                  /* 0xfffffffc00a48947 */
        |                                                                                                    /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                       /* 0x0000000000017941 */
        |                                                                                                    /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                     /* 0x0000000102047824 */
        |                                                                                                    /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                            /* 0x000000020300720c */
        |                                                                                                    /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                             /* 0x0000012000017945 */
        |                                                                                                    /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                           /* 0x000000800400780c */
        |                                                                                                    /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                                  /* 0x00000000003c0947 */
        |                                                                                                    /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                       /* 0x0000008003047836 */
        |                                                                                                    /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                     /* 0x0000840000047ab9 */
        |                                                                                                    /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                      /* 0x0000000403067c10 */
        |                                                                                                    /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                      /* 0x0000000404047c10 */
        |                                                                                                    /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                     /* 0x00000005ff077e24 */
        |                                                                                                    /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                     /* 0x00000005ff057e24 */
        |                                                                                                    /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                                  /* 0x0000000806067981 */
        |                                                                                                    /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                  /* 0x0000000804047981 */
        |                                                                                                    /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                          /* 0x00000000000579c3 */
        |                                                                                                    /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                                /* 0x0000040000047882 */
        |                                                                                                    /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                         /* 0x000000000000781c */
        |                                                                                                    /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                       /* 0x0000000405047291 */
        |                                                                                                    /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                            /* 0x0000000603007988 */
        |                                                                                                    /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                       /* 0x0000800403007988 */
        |                                                                                                    /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                            /* 0x0000010003037836 */
        |                                                                                                    /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                       /* 0x0000000000017941 */
        |                                                                                                    /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                            /* 0x000000020300720c */
        |                                                                                                    /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                              /* 0x0000000000209947 */
        |                                                                                                    /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                     /* 0x0000840000047ab9 */
        |                                                                                                    /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                      /* 0x0000000403047c10 */
        |                                                                                                    /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                     /* 0x00000005ff057e24 */
        |                                                                                                    /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                  /* 0x0000000804047981 */
        |                                                                                                    /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                          /* 0x00000000000579c3 */
        |                                                                                                    /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                                /* 0x0000040000047882 */
        |                                                                                                    /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                       /* 0x0000000405047291 */
        |                                                                                                    /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                            /* 0x0000000403007988 */
        |                                                                                                    /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                       /* 0x0000000000007941 */
        |                                                                                                    /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                       /* 0x00008c00ff027b82 */
        |                                                                                                    /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x44, R2 ;                                 /* 0x0000004400027825 */
        |                                                                                                    /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                    /* 0x0000000000007b1d */
        |                                                                                                    /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R36, desc[UR8][R2.64] ;                                    /* 0x0000000802247981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R37, desc[UR8][R2.64+0x4] ;                                /* 0x0000040802257981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R38, desc[UR8][R2.64+0x8] ;                                /* 0x0000080802267981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R39, desc[UR8][R2.64+0xc] ;                                /* 0x00000c0802277981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R24, desc[UR8][R2.64+0x10] ;                               /* 0x0000100802187981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R25, desc[UR8][R2.64+0x14] ;                               /* 0x0000140802197981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R26, desc[UR8][R2.64+0x18] ;                               /* 0x00001808021a7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R27, desc[UR8][R2.64+0x1c] ;                               /* 0x00001c08021b7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0620*/                   LDG.E R28, desc[UR8][R2.64+0x20] ;                               /* 0x00002008021c7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0630*/                   LDG.E R29, desc[UR8][R2.64+0x24] ;                               /* 0x00002408021d7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0640*/                   LDG.E R30, desc[UR8][R2.64+0x28] ;                               /* 0x00002808021e7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0650*/                   LDG.E R31, desc[UR8][R2.64+0x2c] ;                               /* 0x00002c08021f7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0660*/                   LDG.E R32, desc[UR8][R2.64+0x30] ;                               /* 0x0000300802207981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0670*/                   LDG.E R33, desc[UR8][R2.64+0x34] ;                               /* 0x0000340802217981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0680*/                   LDG.E R34, desc[UR8][R2.64+0x38] ;                               /* 0x0000380802227981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0690*/                   LDG.E R35, desc[UR8][R2.64+0x3c] ;                               /* 0x00003c0802237981 */
        |                                                                                                    /* 0x000162000c1e1900 */
        |        /*06a0*/                   S2UR UR5, SR_CgaCtaId ;                                          /* 0x00000000000579c3 */
        |                                                                                                    /* 0x000e620000008800 */
        |        /*06b0*/                   UMOV UR4, 0x400 ;                                                /* 0x0000040000047882 */
        |                                                                                                    /* 0x000fe20000000000 */
        |        /*06c0*/                   ULDC.64 UR6, c[0x0][0x228] ;                                     /* 0x00008a0000067ab9 */
        |                                                                                                    /* 0x000fe20000000a00 */
        |        /*06d0*/                   MEMBAR.ALL.GPU ;                                                 /* 0x0000000000007992 */
        |                                                                                                    /* 0x000fec000000a000 */
        |        /*06e0*/                   FENCE.VIEW.ASYNC.S ;                                             /* 0x00000000000073c6 */
        |                                                                                                    /* 0x000f620000000000 */
        |        /*06f0*/                   LDC.64 R2, c[0x0][0x238] ;                                       /* 0x00008e00ff027b82 */
        |                                                                                                    /* 0x001e220000000a00 */
        |        /*0700*/                   ULEA UR4, UR5, UR4, 0x18 ;                                       /* 0x0000000405047291 */
        |                                                                                                    /* 0x002fc6000f8ec03f */
        |        /*0710*/                   UMOV UR5, URZ ;                                                  /* 0x0000003f00057c82 */
        |                                                                                                    /* 0x000fe20008000000 */
        |        /*0720*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;                   /* 0x0003ffff04047892 */
        |                                                                                                    /* 0x000fc8000f8ec03f */
        |        /*0730*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                                  /* 0x0000000404047899 */
        |                                                                                                    /* 0x000fe2000800123f */
        |        /*0740*/                   IMAD.WIDE.U32 R2, R0, 0x30, R2 ;                                 /* 0x0000003000027825 */
        |                                                                                                    /* 0x001fc600078e0002 */
        |        /*0750*/                   UIADD3.64 UR6, UR4, UR6, URZ ;                                   /* 0x0000000604067297 */
        |                                                                                                    /* 0x000fe2000fffe03f */
        |        /*0760*/                   WARPGROUP.ARRIVE ;                                               /* 0x00000000000079c5 */
        |                                                                                                    /* 0x020fd00000000000 */
        |        /*0770*/                   QGMMA.64x24x32.F32.E4M3.E5M2 R24, -R36, gdesc[UR4], R24, gsb0 ;  /* 0x0040000424187df3 */
        |                                                                                                    /* 0x000fe60008002918 */
        |        /*0780*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                                  /* 0x00008000000079c5 */
        |                                                                                                    /* 0x000fe40000010000 */
        |        /*0790*/                   STG.E desc[UR8][R2.64], R24 ;                                    /* 0x0000001802007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*07a0*/                   STG.E desc[UR8][R2.64+0x4], R25 ;                                /* 0x0000041902007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*07b0*/                   STG.E desc[UR8][R2.64+0x8], R26 ;                                /* 0x0000081a02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*07c0*/                   STG.E desc[UR8][R2.64+0xc], R27 ;                                /* 0x00000c1b02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*07d0*/                   STG.E desc[UR8][R2.64+0x10], R28 ;                               /* 0x0000101c02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*07e0*/                   STG.E desc[UR8][R2.64+0x14], R29 ;                               /* 0x0000141d02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*07f0*/                   STG.E desc[UR8][R2.64+0x18], R30 ;                               /* 0x0000181e02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0800*/                   STG.E desc[UR8][R2.64+0x1c], R31 ;                               /* 0x00001c1f02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0810*/                   STG.E desc[UR8][R2.64+0x20], R32 ;                               /* 0x0000202002007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0820*/                   STG.E desc[UR8][R2.64+0x24], R33 ;                               /* 0x0000242102007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0830*/                   STG.E desc[UR8][R2.64+0x28], R34 ;                               /* 0x0000282202007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0840*/                   STG.E desc[UR8][R2.64+0x2c], R35 ;                               /* 0x00002c2302007986 */
        |                                                                                                    /* 0x000fe2000c101908 */
        |        /*0850*/                   EXIT ;                                                           /* 0x000000000000794d */
        |                                                                                                    /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma7Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                         /* 0x00000a00ff017b82 */
        |                                                                                                  /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                             /* 0x0000000000007919 */
        |                                                                                                  /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                        /* 0x00008600ff027b82 */
        |                                                                                                  /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                                   /* 0x0000820000067ab9 */
        |                                                                                                  /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                                  /* 0x00008400000c7ab9 */
        |                                                                                                  /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                           /* 0x0000051000007945 */
        |                                                                                                  /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                          /* 0x000000020000720c */
        |                                                                                                  /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                                /* 0x0000000400380947 */
        |                                                                                                  /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                                  /* 0x000000ffff037224 */
        |                                                                                                  /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                          /* 0x000000020300720c */
        |                                                                                                  /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                     /* 0x00008400ff042b82 */
        |                                                                                                  /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                     /* 0x0000000403042210 */
        |                                                                                                  /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                    /* 0x000000ffff052224 */
        |                                                                                                  /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                                /* 0x0000000604042981 */
        |                                                                                                  /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                                /* 0x0000040000062802 */
        |                                                                                                  /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                           /* 0x0000027000017945 */
        |                                                                                                  /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                       /* 0x000000000000781c */
        |                                                                                                  /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                          /* 0x0000000000072919 */
        |                                                                                                  /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                         /* 0x0000000607062211 */
        |                                                                                                  /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                              /* 0x0000000103072824 */
        |                                                                                                  /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                           /* 0x0000008003032836 */
        |                                                                                                  /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                   /* 0x0000000102067824 */
        |                                                                                                  /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                          /* 0x000000020300720c */
        |                                                                                                  /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                        /* 0x000001800600780c */
        |                                                                                                  /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                              /* 0x0000000407002388 */
        |                                                                                                  /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                                /* 0x0000000000700947 */
        |                                                                                                  /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                          /* 0x0000000000057919 */
        |                                                                                                  /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                                /* 0x0000040000047802 */
        |                                                                                                  /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                    /* 0xfffffe80020c7836 */
        |                                                                                                  /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                       /* 0x000000000000781c */
        |                                                                                                  /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                        /* 0x00000004050e7211 */
        |                                                                                                  /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                             /* 0x0000000c03047c10 */
        |                                                                                                  /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                     /* 0x0000008003067836 */
        |                                                                                                  /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                    /* 0x0000010003087836 */
        |                                                                                                  /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                         /* 0x00000180030a7836 */
        |                                                                                                  /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                                   /* 0x0000000c06067c10 */
        |                                                                                                  /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                                  /* 0x0000000dff057e24 */
        |                                                                                                  /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                                   /* 0x0000000c08087c10 */
        |                                                                                                  /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                                 /* 0x0000000c0a0a7c10 */
        |                                                                                                  /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                                  /* 0x0000000dff077e24 */
        |                                                                                                  /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                /* 0x0000000604047981 */
        |                                                                                                  /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                                  /* 0x0000000dff097e24 */
        |                                                                                                  /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                                 /* 0x0000000dff0b7e24 */
        |                                                                                                  /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                /* 0x0000000606067981 */
        |                                                                                                  /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                                /* 0x0000000608087981 */
        |                                                                                                  /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                              /* 0x000000060a0a7981 */
        |                                                                                                  /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                  /* 0x000000010e0d7824 */
        |                                                                                                  /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                          /* 0x0000020003037836 */
        |                                                                                                  /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                         /* 0x0000000c0300720c */
        |                                                                                                  /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                             /* 0x000000040d007388 */
        |                                                                                                  /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                        /* 0x000080060d007388 */
        |                                                                                                  /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                       /* 0x000100080d007388 */
        |                                                                                                  /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                      /* 0x0001800a0d007388 */
        |                                                                                                  /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                                /* 0xfffffffc00a48947 */
        |                                                                                                  /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                     /* 0x0000000000017941 */
        |                                                                                                  /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                   /* 0x0000000102047824 */
        |                                                                                                  /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                          /* 0x000000020300720c */
        |                                                                                                  /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                           /* 0x0000012000017945 */
        |                                                                                                  /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                         /* 0x000000800400780c */
        |                                                                                                  /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                                /* 0x00000000003c0947 */
        |                                                                                                  /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                     /* 0x0000008003047836 */
        |                                                                                                  /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                   /* 0x0000840000047ab9 */
        |                                                                                                  /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                    /* 0x0000000403067c10 */
        |                                                                                                  /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                    /* 0x0000000404047c10 */
        |                                                                                                  /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                   /* 0x00000005ff077e24 */
        |                                                                                                  /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                   /* 0x00000005ff057e24 */
        |                                                                                                  /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                /* 0x0000000606067981 */
        |                                                                                                  /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                /* 0x0000000604047981 */
        |                                                                                                  /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                        /* 0x00000000000579c3 */
        |                                                                                                  /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                              /* 0x0000040000047882 */
        |                                                                                                  /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                       /* 0x000000000000781c */
        |                                                                                                  /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                     /* 0x0000000405047291 */
        |                                                                                                  /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                          /* 0x0000000603007988 */
        |                                                                                                  /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                     /* 0x0000800403007988 */
        |                                                                                                  /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                          /* 0x0000010003037836 */
        |                                                                                                  /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                     /* 0x0000000000017941 */
        |                                                                                                  /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                          /* 0x000000020300720c */
        |                                                                                                  /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                            /* 0x0000000000209947 */
        |                                                                                                  /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                   /* 0x0000840000047ab9 */
        |                                                                                                  /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                    /* 0x0000000403047c10 */
        |                                                                                                  /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                   /* 0x00000005ff057e24 */
        |                                                                                                  /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                /* 0x0000000604047981 */
        |                                                                                                  /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                        /* 0x00000000000579c3 */
        |                                                                                                  /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                              /* 0x0000040000047882 */
        |                                                                                                  /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                     /* 0x0000000405047291 */
        |                                                                                                  /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                          /* 0x0000000403007988 */
        |                                                                                                  /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                     /* 0x0000000000007941 */
        |                                                                                                  /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                     /* 0x00008c00ff027b82 */
        |                                                                                                  /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x1c, R2 ;                               /* 0x0000001c00027825 */
        |                                                                                                  /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                  /* 0x0000000000007b1d */
        |                                                                                                  /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R24, desc[UR6][R2.64+0x10] ;                             /* 0x0000100602187981 */
        |                                                                                                  /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R25, desc[UR6][R2.64+0x14] ;                             /* 0x0000140602197981 */
        |                                                                                                  /* 0x000162000c1e1900 */
        |        /*05c0*/                   S2UR UR5, SR_CgaCtaId ;                                        /* 0x00000000000579c3 */
        |                                                                                                  /* 0x000e620000008800 */
        |        /*05d0*/                   UMOV UR4, 0x400 ;                                              /* 0x0000040000047882 */
        |                                                                                                  /* 0x000fe20000000000 */
        |        /*05e0*/                   ULDC.64 UR8, c[0x0][0x220] ;                                   /* 0x0000880000087ab9 */
        |                                                                                                  /* 0x000fe20000000a00 */
        |        /*05f0*/                   MEMBAR.ALL.GPU ;                                               /* 0x0000000000007992 */
        |                                                                                                  /* 0x000fec000000a000 */
        |        /*0600*/                   FENCE.VIEW.ASYNC.S ;                                           /* 0x00000000000073c6 */
        |                                                                                                  /* 0x000f620000000000 */
        |        /*0610*/                   ULDC.64 UR10, c[0x0][0x228] ;                                  /* 0x00008a00000a7ab9 */
        |                                                                                                  /* 0x000fe20000000a00 */
        |        /*0620*/                   LDC.64 R2, c[0x0][0x238] ;                                     /* 0x00008e00ff027b82 */
        |                                                                                                  /* 0x001e220000000a00 */
        |        /*0630*/                   ULEA UR4, UR5, UR4, 0x18 ;                                     /* 0x0000000405047291 */
        |                                                                                                  /* 0x002fc6000f8ec03f */
        |        /*0640*/                   ULDC UR5, c[0x0][0x240] ;                                      /* 0x0000900000057ab9 */
        |                                                                                                  /* 0x000fe20000000800 */
        |        /*0650*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;                 /* 0x0003ffff04047892 */
        |                                                                                                  /* 0x000fe4000f8ec03f */
        |        /*0660*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                    /* 0x0000003f0500728c */
        |                                                                                                  /* 0x000fe4000bf05070 */
        |        /*0670*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                                /* 0x0000000404047899 */
        |                                                                                                  /* 0x000fe2000800123f */
        |        /*0680*/                   IMAD.WIDE.U32 R2, R0, 0x8, R2 ;                                /* 0x0000000800027825 */
        |                                                                                                  /* 0x001fe200078e0002 */
        |        /*0690*/                   UMOV UR5, URZ ;                                                /* 0x0000003f00057c82 */
        |                                                                                                  /* 0x000fe40008000000 */
        |        /*06a0*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                                 /* 0x0000000804087297 */
        |                                                                                                  /* 0x000fe4000fffe03f */
        |        /*06b0*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                               /* 0x0000000a040a7297 */
        |                                                                                                  /* 0x000fe2000fffe03f */
        |        /*06c0*/                   WARPGROUP.ARRIVE ;                                             /* 0x00000000000079c5 */
        |                                                                                                  /* 0x020fd00000000000 */
        |        /*06d0*/                   QGMMA.64x8x32.F16.E5M2.E4M3 R24, gdesc[UR8], R24, UP0, gsb0 ;  /* 0x00000000081879f3 */
        |                                                                                                  /* 0x000fe6000b801018 */
        |        /*06e0*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                                /* 0x00008000000079c5 */
        |                                                                                                  /* 0x000fe40000010000 */
        |        /*06f0*/                   STG.E desc[UR6][R2.64], R24 ;                                  /* 0x0000001802007986 */
        |                                                                                                  /* 0x000fe8000c101906 */
        |        /*0700*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                              /* 0x0000041902007986 */
        |                                                                                                  /* 0x000fe2000c101906 */
        |        /*0710*/                   EXIT ;                                                         /* 0x000000000000794d */
        |                                                                                                  /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma8Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                 /* 0x00000a00ff017b82 */
        |                                                                                          /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                     /* 0x0000000000007919 */
        |                                                                                          /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                /* 0x00008600ff027b82 */
        |                                                                                          /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                           /* 0x0000820000067ab9 */
        |                                                                                          /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                          /* 0x00008400000c7ab9 */
        |                                                                                          /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                   /* 0x0000051000007945 */
        |                                                                                          /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                  /* 0x000000020000720c */
        |                                                                                          /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                        /* 0x0000000400380947 */
        |                                                                                          /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                          /* 0x000000ffff037224 */
        |                                                                                          /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                  /* 0x000000020300720c */
        |                                                                                          /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                             /* 0x00008400ff042b82 */
        |                                                                                          /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                             /* 0x0000000403042210 */
        |                                                                                          /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                            /* 0x000000ffff052224 */
        |                                                                                          /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                        /* 0x0000000604042981 */
        |                                                                                          /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                        /* 0x0000040000062802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                   /* 0x0000027000017945 */
        |                                                                                          /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;               /* 0x000000000000781c */
        |                                                                                          /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                  /* 0x0000000000072919 */
        |                                                                                          /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                 /* 0x0000000607062211 */
        |                                                                                          /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                      /* 0x0000000103072824 */
        |                                                                                          /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                   /* 0x0000008003032836 */
        |                                                                                          /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                           /* 0x0000000102067824 */
        |                                                                                          /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                  /* 0x000000020300720c */
        |                                                                                          /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                /* 0x000001800600780c */
        |                                                                                          /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                      /* 0x0000000407002388 */
        |                                                                                          /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                        /* 0x0000000000700947 */
        |                                                                                          /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                  /* 0x0000000000057919 */
        |                                                                                          /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                        /* 0x0000040000047802 */
        |                                                                                          /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                            /* 0xfffffe80020c7836 */
        |                                                                                          /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;               /* 0x000000000000781c */
        |                                                                                          /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                /* 0x00000004050e7211 */
        |                                                                                          /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                     /* 0x0000000c03047c10 */
        |                                                                                          /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                             /* 0x0000008003067836 */
        |                                                                                          /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                            /* 0x0000010003087836 */
        |                                                                                          /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                 /* 0x00000180030a7836 */
        |                                                                                          /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                           /* 0x0000000c06067c10 */
        |                                                                                          /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                          /* 0x0000000dff057e24 */
        |                                                                                          /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                           /* 0x0000000c08087c10 */
        |                                                                                          /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                         /* 0x0000000c0a0a7c10 */
        |                                                                                          /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                          /* 0x0000000dff077e24 */
        |                                                                                          /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                        /* 0x0000000604047981 */
        |                                                                                          /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                          /* 0x0000000dff097e24 */
        |                                                                                          /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                         /* 0x0000000dff0b7e24 */
        |                                                                                          /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                        /* 0x0000000606067981 */
        |                                                                                          /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                        /* 0x0000000608087981 */
        |                                                                                          /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                      /* 0x000000060a0a7981 */
        |                                                                                          /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                          /* 0x000000010e0d7824 */
        |                                                                                          /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                  /* 0x0000020003037836 */
        |                                                                                          /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                 /* 0x0000000c0300720c */
        |                                                                                          /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                     /* 0x000000040d007388 */
        |                                                                                          /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                /* 0x000080060d007388 */
        |                                                                                          /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                               /* 0x000100080d007388 */
        |                                                                                          /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                              /* 0x0001800a0d007388 */
        |                                                                                          /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                        /* 0xfffffffc00a48947 */
        |                                                                                          /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                             /* 0x0000000000017941 */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                           /* 0x0000000102047824 */
        |                                                                                          /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                  /* 0x000000020300720c */
        |                                                                                          /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                   /* 0x0000012000017945 */
        |                                                                                          /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                 /* 0x000000800400780c */
        |                                                                                          /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                        /* 0x00000000003c0947 */
        |                                                                                          /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                             /* 0x0000008003047836 */
        |                                                                                          /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                           /* 0x0000840000047ab9 */
        |                                                                                          /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                            /* 0x0000000403067c10 */
        |                                                                                          /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                            /* 0x0000000404047c10 */
        |                                                                                          /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                           /* 0x00000005ff077e24 */
        |                                                                                          /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                           /* 0x00000005ff057e24 */
        |                                                                                          /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                        /* 0x0000000606067981 */
        |                                                                                          /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                        /* 0x0000000604047981 */
        |                                                                                          /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                /* 0x00000000000579c3 */
        |                                                                                          /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                      /* 0x0000040000047882 */
        |                                                                                          /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;               /* 0x000000000000781c */
        |                                                                                          /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                             /* 0x0000000405047291 */
        |                                                                                          /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                  /* 0x0000000603007988 */
        |                                                                                          /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                             /* 0x0000800403007988 */
        |                                                                                          /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                  /* 0x0000010003037836 */
        |                                                                                          /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                             /* 0x0000000000017941 */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                  /* 0x000000020300720c */
        |                                                                                          /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                    /* 0x0000000000209947 */
        |                                                                                          /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                           /* 0x0000840000047ab9 */
        |                                                                                          /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                            /* 0x0000000403047c10 */
        |                                                                                          /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                           /* 0x00000005ff057e24 */
        |                                                                                          /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                        /* 0x0000000604047981 */
        |                                                                                          /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                /* 0x00000000000579c3 */
        |                                                                                          /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                      /* 0x0000040000047882 */
        |                                                                                          /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                             /* 0x0000000405047291 */
        |                                                                                          /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                  /* 0x0000000403007988 */
        |                                                                                          /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                             /* 0x0000000000007941 */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                             /* 0x00008c00ff027b82 */
        |                                                                                          /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x24, R2 ;                       /* 0x0000002400027825 */
        |                                                                                          /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                          /* 0x0000000000007b1d */
        |                                                                                          /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R24, desc[UR6][R2.64+0x10] ;                     /* 0x0000100602187981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R25, desc[UR6][R2.64+0x14] ;                     /* 0x0000140602197981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R26, desc[UR6][R2.64+0x18] ;                     /* 0x00001806021a7981 */
        |                                                                                          /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R27, desc[UR6][R2.64+0x1c] ;                     /* 0x00001c06021b7981 */
        |                                                                                          /* 0x000162000c1e1900 */
        |        /*05e0*/                   S2UR UR5, SR_CgaCtaId ;                                /* 0x00000000000579c3 */
        |                                                                                          /* 0x000e620000008800 */
        |        /*05f0*/                   UMOV UR4, 0x400 ;                                      /* 0x0000040000047882 */
        |                                                                                          /* 0x000fe20000000000 */
        |        /*0600*/                   ULDC.64 UR8, c[0x0][0x220] ;                           /* 0x0000880000087ab9 */
        |                                                                                          /* 0x000fe20000000a00 */
        |        /*0610*/                   MEMBAR.ALL.GPU ;                                       /* 0x0000000000007992 */
        |                                                                                          /* 0x000fec000000a000 */
        |        /*0620*/                   FENCE.VIEW.ASYNC.S ;                                   /* 0x00000000000073c6 */
        |                                                                                          /* 0x000f620000000000 */
        |        /*0630*/                   ULDC.64 UR10, c[0x0][0x228] ;                          /* 0x00008a00000a7ab9 */
        |                                                                                          /* 0x000fe20000000a00 */
        |        /*0640*/                   LDC.64 R2, c[0x0][0x238] ;                             /* 0x00008e00ff027b82 */
        |                                                                                          /* 0x001e220000000a00 */
        |        /*0650*/                   ULEA UR4, UR5, UR4, 0x18 ;                             /* 0x0000000405047291 */
        |                                                                                          /* 0x002fc6000f8ec03f */
        |        /*0660*/                   ULDC UR5, c[0x0][0x240] ;                              /* 0x0000900000057ab9 */
        |                                                                                          /* 0x000fe20000000800 */
        |        /*0670*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;         /* 0x0003ffff04047892 */
        |                                                                                          /* 0x000fe4000f8ec03f */
        |        /*0680*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;            /* 0x0000003f0500728c */
        |                                                                                          /* 0x000fe4000bf05070 */
        |        /*0690*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                        /* 0x0000000404047899 */
        |                                                                                          /* 0x000fe2000800123f */
        |        /*06a0*/                   IMAD.WIDE.U32 R2, R0, 0x10, R2 ;                       /* 0x0000001000027825 */
        |                                                                                          /* 0x001fe200078e0002 */
        |        /*06b0*/                   UMOV UR5, URZ ;                                        /* 0x0000003f00057c82 */
        |                                                                                          /* 0x000fe40008000000 */
        |        /*06c0*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                         /* 0x0000000804087297 */
        |                                                                                          /* 0x000fe4000fffe03f */
        |        /*06d0*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                       /* 0x0000000a040a7297 */
        |                                                                                          /* 0x000fe2000fffe03f */
        |        /*06e0*/                   WARPGROUP.ARRIVE ;                                     /* 0x00000000000079c5 */
        |                                                                                          /* 0x020fd00000000000 */
        |        /*06f0*/                   IGMMA.64x8x32.S8.S8 R24, gdesc[UR8], R24, UP0, gsb0 ;  /* 0x00000000081879f1 */
        |                                                                                          /* 0x000fe6000b841018 */
        |        /*0700*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                        /* 0x00008000000079c5 */
        |                                                                                          /* 0x000fe40000010000 */
        |        /*0710*/                   STG.E desc[UR6][R2.64], R24 ;                          /* 0x0000001802007986 */
        |                                                                                          /* 0x000fe8000c101906 */
        |        /*0720*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                      /* 0x0000041902007986 */
        |                                                                                          /* 0x000fe8000c101906 */
        |        /*0730*/                   STG.E desc[UR6][R2.64+0x8], R26 ;                      /* 0x0000081a02007986 */
        |                                                                                          /* 0x000fe8000c101906 */
        |        /*0740*/                   STG.E desc[UR6][R2.64+0xc], R27 ;                      /* 0x00000c1b02007986 */
        |                                                                                          /* 0x000fe2000c101906 */
        |        /*0750*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma9Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                      /* 0x00000a00ff017b82 */
        |                                                                                               /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                          /* 0x0000000000007919 */
        |                                                                                               /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                     /* 0x00008600ff027b82 */
        |                                                                                               /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                                /* 0x0000820000067ab9 */
        |                                                                                               /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                               /* 0x00008400000c7ab9 */
        |                                                                                               /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                        /* 0x0000051000007945 */
        |                                                                                               /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                       /* 0x000000020000720c */
        |                                                                                               /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                             /* 0x0000000400380947 */
        |                                                                                               /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                               /* 0x000000ffff037224 */
        |                                                                                               /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                       /* 0x000000020300720c */
        |                                                                                               /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                  /* 0x00008400ff042b82 */
        |                                                                                               /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                  /* 0x0000000403042210 */
        |                                                                                               /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                 /* 0x000000ffff052224 */
        |                                                                                               /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                             /* 0x0000000604042981 */
        |                                                                                               /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                             /* 0x0000040000062802 */
        |                                                                                               /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                        /* 0x0000027000017945 */
        |                                                                                               /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                    /* 0x000000000000781c */
        |                                                                                               /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                       /* 0x0000000000072919 */
        |                                                                                               /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                      /* 0x0000000607062211 */
        |                                                                                               /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                           /* 0x0000000103072824 */
        |                                                                                               /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                        /* 0x0000008003032836 */
        |                                                                                               /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                /* 0x0000000102067824 */
        |                                                                                               /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                       /* 0x000000020300720c */
        |                                                                                               /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                     /* 0x000001800600780c */
        |                                                                                               /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                           /* 0x0000000407002388 */
        |                                                                                               /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                             /* 0x0000000000700947 */
        |                                                                                               /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                       /* 0x0000000000057919 */
        |                                                                                               /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                             /* 0x0000040000047802 */
        |                                                                                               /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                 /* 0xfffffe80020c7836 */
        |                                                                                               /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                    /* 0x000000000000781c */
        |                                                                                               /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                     /* 0x00000004050e7211 */
        |                                                                                               /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                          /* 0x0000000c03047c10 */
        |                                                                                               /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                  /* 0x0000008003067836 */
        |                                                                                               /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                 /* 0x0000010003087836 */
        |                                                                                               /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                      /* 0x00000180030a7836 */
        |                                                                                               /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                                /* 0x0000000c06067c10 */
        |                                                                                               /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                               /* 0x0000000dff057e24 */
        |                                                                                               /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                                /* 0x0000000c08087c10 */
        |                                                                                               /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                              /* 0x0000000c0a0a7c10 */
        |                                                                                               /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                               /* 0x0000000dff077e24 */
        |                                                                                               /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                             /* 0x0000000604047981 */
        |                                                                                               /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                               /* 0x0000000dff097e24 */
        |                                                                                               /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                              /* 0x0000000dff0b7e24 */
        |                                                                                               /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                             /* 0x0000000606067981 */
        |                                                                                               /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                             /* 0x0000000608087981 */
        |                                                                                               /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                           /* 0x000000060a0a7981 */
        |                                                                                               /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                               /* 0x000000010e0d7824 */
        |                                                                                               /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                       /* 0x0000020003037836 */
        |                                                                                               /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                      /* 0x0000000c0300720c */
        |                                                                                               /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                          /* 0x000000040d007388 */
        |                                                                                               /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                     /* 0x000080060d007388 */
        |                                                                                               /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                    /* 0x000100080d007388 */
        |                                                                                               /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                   /* 0x0001800a0d007388 */
        |                                                                                               /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                             /* 0xfffffffc00a48947 */
        |                                                                                               /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                  /* 0x0000000000017941 */
        |                                                                                               /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                /* 0x0000000102047824 */
        |                                                                                               /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                       /* 0x000000020300720c */
        |                                                                                               /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                        /* 0x0000012000017945 */
        |                                                                                               /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                      /* 0x000000800400780c */
        |                                                                                               /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                             /* 0x00000000003c0947 */
        |                                                                                               /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                  /* 0x0000008003047836 */
        |                                                                                               /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                /* 0x0000840000047ab9 */
        |                                                                                               /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                 /* 0x0000000403067c10 */
        |                                                                                               /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                 /* 0x0000000404047c10 */
        |                                                                                               /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                /* 0x00000005ff077e24 */
        |                                                                                               /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                /* 0x00000005ff057e24 */
        |                                                                                               /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                             /* 0x0000000606067981 */
        |                                                                                               /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                             /* 0x0000000604047981 */
        |                                                                                               /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                     /* 0x00000000000579c3 */
        |                                                                                               /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                           /* 0x0000040000047882 */
        |                                                                                               /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                    /* 0x000000000000781c */
        |                                                                                               /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                  /* 0x0000000405047291 */
        |                                                                                               /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                       /* 0x0000000603007988 */
        |                                                                                               /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                  /* 0x0000800403007988 */
        |                                                                                               /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                       /* 0x0000010003037836 */
        |                                                                                               /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                  /* 0x0000000000017941 */
        |                                                                                               /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                       /* 0x000000020300720c */
        |                                                                                               /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                         /* 0x0000000000209947 */
        |                                                                                               /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                /* 0x0000840000047ab9 */
        |                                                                                               /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                 /* 0x0000000403047c10 */
        |                                                                                               /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                /* 0x00000005ff057e24 */
        |                                                                                               /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                             /* 0x0000000604047981 */
        |                                                                                               /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                     /* 0x00000000000579c3 */
        |                                                                                               /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                           /* 0x0000040000047882 */
        |                                                                                               /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                  /* 0x0000000405047291 */
        |                                                                                               /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                       /* 0x0000000403007988 */
        |                                                                                               /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                  /* 0x0000000000007941 */
        |                                                                                               /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                  /* 0x00008c00ff027b82 */
        |                                                                                               /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x34, R2 ;                            /* 0x0000003400027825 */
        |                                                                                               /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                               /* 0x0000000000007b1d */
        |                                                                                               /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R24, desc[UR6][R2.64+0x10] ;                          /* 0x0000100602187981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R25, desc[UR6][R2.64+0x14] ;                          /* 0x0000140602197981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R26, desc[UR6][R2.64+0x18] ;                          /* 0x00001806021a7981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R27, desc[UR6][R2.64+0x1c] ;                          /* 0x00001c06021b7981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R28, desc[UR6][R2.64+0x20] ;                          /* 0x00002006021c7981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R29, desc[UR6][R2.64+0x24] ;                          /* 0x00002406021d7981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R30, desc[UR6][R2.64+0x28] ;                          /* 0x00002806021e7981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R31, desc[UR6][R2.64+0x2c] ;                          /* 0x00002c06021f7981 */
        |                                                                                               /* 0x000162000c1e1900 */
        |        /*0620*/                   S2UR UR5, SR_CgaCtaId ;                                     /* 0x00000000000579c3 */
        |                                                                                               /* 0x000e620000008800 */
        |        /*0630*/                   UMOV UR4, 0x400 ;                                           /* 0x0000040000047882 */
        |                                                                                               /* 0x000fe20000000000 */
        |        /*0640*/                   ULDC.64 UR8, c[0x0][0x220] ;                                /* 0x0000880000087ab9 */
        |                                                                                               /* 0x000fe20000000a00 */
        |        /*0650*/                   MEMBAR.ALL.GPU ;                                            /* 0x0000000000007992 */
        |                                                                                               /* 0x000fec000000a000 */
        |        /*0660*/                   FENCE.VIEW.ASYNC.S ;                                        /* 0x00000000000073c6 */
        |                                                                                               /* 0x000f620000000000 */
        |        /*0670*/                   ULDC.64 UR10, c[0x0][0x228] ;                               /* 0x00008a00000a7ab9 */
        |                                                                                               /* 0x000fe20000000a00 */
        |        /*0680*/                   LDC.64 R2, c[0x0][0x238] ;                                  /* 0x00008e00ff027b82 */
        |                                                                                               /* 0x001e220000000a00 */
        |        /*0690*/                   ULEA UR4, UR5, UR4, 0x18 ;                                  /* 0x0000000405047291 */
        |                                                                                               /* 0x002fc6000f8ec03f */
        |        /*06a0*/                   ULDC UR5, c[0x0][0x240] ;                                   /* 0x0000900000057ab9 */
        |                                                                                               /* 0x000fe20000000800 */
        |        /*06b0*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;              /* 0x0003ffff04047892 */
        |                                                                                               /* 0x000fe4000f8ec03f */
        |        /*06c0*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                 /* 0x0000003f0500728c */
        |                                                                                               /* 0x000fe4000bf05070 */
        |        /*06d0*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                             /* 0x0000000404047899 */
        |                                                                                               /* 0x000fe2000800123f */
        |        /*06e0*/                   IMAD.WIDE.U32 R2, R0, 0x20, R2 ;                            /* 0x0000002000027825 */
        |                                                                                               /* 0x001fe200078e0002 */
        |        /*06f0*/                   UMOV UR5, URZ ;                                             /* 0x0000003f00057c82 */
        |                                                                                               /* 0x000fe40008000000 */
        |        /*0700*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                              /* 0x0000000804087297 */
        |                                                                                               /* 0x000fe4000fffe03f */
        |        /*0710*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                            /* 0x0000000a040a7297 */
        |                                                                                               /* 0x000fe2000fffe03f */
        |        /*0720*/                   WARPGROUP.ARRIVE ;                                          /* 0x00000000000079c5 */
        |                                                                                               /* 0x020fd00000000000 */
        |        /*0730*/                   IGMMA.64x16x32.U8.U8.SAT R24, gdesc[UR8], R24, UP0, gsb0 ;  /* 0x00600000081879f1 */
        |                                                                                               /* 0x000fe6000b800818 */
        |        /*0740*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                             /* 0x00008000000079c5 */
        |                                                                                               /* 0x000fe40000010000 */
        |        /*0750*/                   STG.E desc[UR6][R2.64], R24 ;                               /* 0x0000001802007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*0760*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                           /* 0x0000041902007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*0770*/                   STG.E desc[UR6][R2.64+0x8], R26 ;                           /* 0x0000081a02007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*0780*/                   STG.E desc[UR6][R2.64+0xc], R27 ;                           /* 0x00000c1b02007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*0790*/                   STG.E desc[UR6][R2.64+0x10], R28 ;                          /* 0x0000101c02007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*07a0*/                   STG.E desc[UR6][R2.64+0x14], R29 ;                          /* 0x0000141d02007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*07b0*/                   STG.E desc[UR6][R2.64+0x18], R30 ;                          /* 0x0000181e02007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*07c0*/                   STG.E desc[UR6][R2.64+0x1c], R31 ;                          /* 0x00001c1f02007986 */
        |                                                                                               /* 0x000fe2000c101906 */
        |        /*07d0*/                   EXIT ;                                                      /* 0x000000000000794d */
        |                                                                                               /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma10Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                       /* 0x00000a00ff017b82 */
        |                                                                                                /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R2, SR_TID.X ;                                           /* 0x0000000000027919 */
        |                                                                                                /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R0, c[0x0][0x218] ;                                      /* 0x00008600ff007b82 */
        |                                                                                                /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR8, c[0x0][0x208] ;                                 /* 0x0000820000087ab9 */
        |                                                                                                /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR10, c[0x0][0x210] ;                                /* 0x00008400000a7ab9 */
        |                                                                                                /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                         /* 0x0000051000007945 */
        |                                                                                                /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R2, R0, PT ;                        /* 0x000000000200720c */
        |                                                                                                /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                              /* 0x0000000400380947 */
        |                                                                                                /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R2 ;                                /* 0x000000ffff037224 */
        |                                                                                                /* 0x000fca00078e0002 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R0, PT ;                        /* 0x000000000300720c */
        |                                                                                                /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                   /* 0x00008400ff042b82 */
        |                                                                                                /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                   /* 0x0000000403042210 */
        |                                                                                                /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                  /* 0x000000ffff052224 */
        |                                                                                                /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR8][R4.64] ;                              /* 0x0000000804042981 */
        |                                                                                                /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                              /* 0x0000040000062802 */
        |                                                                                                /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                         /* 0x0000027000017945 */
        |                                                                                                /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                     /* 0x000000000000781c */
        |                                                                                                /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                        /* 0x0000000000072919 */
        |                                                                                                /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                       /* 0x0000000607062211 */
        |                                                                                                /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                            /* 0x0000000103072824 */
        |                                                                                                /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                         /* 0x0000008003032836 */
        |                                                                                                /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R0, 0x1, -R3 ;                                 /* 0x0000000100067824 */
        |                                                                                                /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R0, PT ;                        /* 0x000000000300720c */
        |                                                                                                /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                      /* 0x000001800600780c */
        |                                                                                                /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                            /* 0x0000000407002388 */
        |                                                                                                /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                              /* 0x0000000000700947 */
        |                                                                                                /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                        /* 0x0000000000057919 */
        |                                                                                                /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                              /* 0x0000040000047802 */
        |                                                                                                /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R0, 0xfffffe80 ;                                  /* 0xfffffe80000c7836 */
        |                                                                                                /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                     /* 0x000000000000781c */
        |                                                                                                /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                      /* 0x00000004050e7211 */
        |                                                                                                /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR10, RZ ;                           /* 0x0000000a03047c10 */
        |                                                                                                /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                   /* 0x0000008003067836 */
        |                                                                                                /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                  /* 0x0000010003087836 */
        |                                                                                                /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                       /* 0x00000180030a7836 */
        |                                                                                                /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR10, RZ ;                                 /* 0x0000000a06067c10 */
        |                                                                                                /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR11, P2 ;                                /* 0x0000000bff057e24 */
        |                                                                                                /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR10, RZ ;                                 /* 0x0000000a08087c10 */
        |                                                                                                /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR10, RZ ;                               /* 0x0000000a0a0a7c10 */
        |                                                                                                /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR11, P0 ;                                /* 0x0000000bff077e24 */
        |                                                                                                /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                              /* 0x0000000804047981 */
        |                                                                                                /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR11, P2 ;                                /* 0x0000000bff097e24 */
        |                                                                                                /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR11, P3 ;                               /* 0x0000000bff0b7e24 */
        |                                                                                                /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                              /* 0x0000000806067981 */
        |                                                                                                /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR8][R8.64] ;                              /* 0x0000000808087981 */
        |                                                                                                /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR8][R10.64] ;                            /* 0x000000080a0a7981 */
        |                                                                                                /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                /* 0x000000010e0d7824 */
        |                                                                                                /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                        /* 0x0000020003037836 */
        |                                                                                                /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                       /* 0x0000000c0300720c */
        |                                                                                                /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                           /* 0x000000040d007388 */
        |                                                                                                /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                      /* 0x000080060d007388 */
        |                                                                                                /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                     /* 0x000100080d007388 */
        |                                                                                                /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                    /* 0x0001800a0d007388 */
        |                                                                                                /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                              /* 0xfffffffc00a48947 */
        |                                                                                                /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                   /* 0x0000000000017941 */
        |                                                                                                /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R0, 0x1, -R3 ;                                 /* 0x0000000100047824 */
        |                                                                                                /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R0, PT ;                        /* 0x000000000300720c */
        |                                                                                                /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                         /* 0x0000012000017945 */
        |                                                                                                /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                       /* 0x000000800400780c */
        |                                                                                                /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                              /* 0x00000000003c0947 */
        |                                                                                                /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                   /* 0x0000008003047836 */
        |                                                                                                /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                 /* 0x0000840000047ab9 */
        |                                                                                                /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                  /* 0x0000000403067c10 */
        |                                                                                                /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                  /* 0x0000000404047c10 */
        |                                                                                                /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                 /* 0x00000005ff077e24 */
        |                                                                                                /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                 /* 0x00000005ff057e24 */
        |                                                                                                /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                              /* 0x0000000806067981 */
        |                                                                                                /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                              /* 0x0000000804047981 */
        |                                                                                                /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                      /* 0x00000000000579c3 */
        |                                                                                                /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                            /* 0x0000040000047882 */
        |                                                                                                /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                     /* 0x000000000000781c */
        |                                                                                                /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                   /* 0x0000000405047291 */
        |                                                                                                /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                        /* 0x0000000603007988 */
        |                                                                                                /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                   /* 0x0000800403007988 */
        |                                                                                                /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                        /* 0x0000010003037836 */
        |                                                                                                /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                   /* 0x0000000000017941 */
        |                                                                                                /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R0, PT ;                        /* 0x000000000300720c */
        |                                                                                                /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                          /* 0x0000000000209947 */
        |                                                                                                /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                 /* 0x0000840000047ab9 */
        |                                                                                                /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                  /* 0x0000000403047c10 */
        |                                                                                                /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                 /* 0x00000005ff057e24 */
        |                                                                                                /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                              /* 0x0000000804047981 */
        |                                                                                                /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                      /* 0x00000000000579c3 */
        |                                                                                                /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                            /* 0x0000040000047882 */
        |                                                                                                /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                   /* 0x0000000405047291 */
        |                                                                                                /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                        /* 0x0000000403007988 */
        |                                                                                                /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                   /* 0x0000000000007941 */
        |                                                                                                /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R4, c[0x0][0x230] ;                                   /* 0x00008c00ff047b82 */
        |                                                                                                /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R2, 0x74, R4 ;                             /* 0x0000007402027825 */
        |                                                                                                /* 0x001fcc00078e0004 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                /* 0x0000000000007b1d */
        |                                                                                                /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R48, desc[UR8][R2.64] ;                                /* 0x0000000802307981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R49, desc[UR8][R2.64+0x4] ;                            /* 0x0000040802317981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R50, desc[UR8][R2.64+0x8] ;                            /* 0x0000080802327981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R51, desc[UR8][R2.64+0xc] ;                            /* 0x00000c0802337981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R24, desc[UR8][R2.64+0x10] ;                           /* 0x0000100802187981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R25, desc[UR8][R2.64+0x14] ;                           /* 0x0000140802197981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R26, desc[UR8][R2.64+0x18] ;                           /* 0x00001808021a7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R27, desc[UR8][R2.64+0x1c] ;                           /* 0x00001c08021b7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0620*/                   LDG.E R28, desc[UR8][R2.64+0x20] ;                           /* 0x00002008021c7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0630*/                   LDG.E R29, desc[UR8][R2.64+0x24] ;                           /* 0x00002408021d7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0640*/                   LDG.E R30, desc[UR8][R2.64+0x28] ;                           /* 0x00002808021e7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0650*/                   LDG.E R31, desc[UR8][R2.64+0x2c] ;                           /* 0x00002c08021f7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0660*/                   LDG.E R32, desc[UR8][R2.64+0x30] ;                           /* 0x0000300802207981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0670*/                   LDG.E R33, desc[UR8][R2.64+0x34] ;                           /* 0x0000340802217981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0680*/                   LDG.E R34, desc[UR8][R2.64+0x38] ;                           /* 0x0000380802227981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0690*/                   LDG.E R35, desc[UR8][R2.64+0x3c] ;                           /* 0x00003c0802237981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06a0*/                   LDG.E R36, desc[UR8][R2.64+0x40] ;                           /* 0x0000400802247981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06b0*/                   LDG.E R37, desc[UR8][R2.64+0x44] ;                           /* 0x0000440802257981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06c0*/                   LDG.E R38, desc[UR8][R2.64+0x48] ;                           /* 0x0000480802267981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06d0*/                   LDG.E R39, desc[UR8][R2.64+0x4c] ;                           /* 0x00004c0802277981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06e0*/                   LDG.E R40, desc[UR8][R2.64+0x50] ;                           /* 0x0000500802287981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06f0*/                   LDG.E R41, desc[UR8][R2.64+0x54] ;                           /* 0x0000540802297981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0700*/                   LDG.E R42, desc[UR8][R2.64+0x58] ;                           /* 0x00005808022a7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0710*/                   LDG.E R43, desc[UR8][R2.64+0x5c] ;                           /* 0x00005c08022b7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0720*/                   LDG.E R44, desc[UR8][R2.64+0x60] ;                           /* 0x00006008022c7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0730*/                   LDG.E R45, desc[UR8][R2.64+0x64] ;                           /* 0x00006408022d7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0740*/                   LDG.E R46, desc[UR8][R2.64+0x68] ;                           /* 0x00006808022e7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0750*/                   LDG.E R47, desc[UR8][R2.64+0x6c] ;                           /* 0x00006c08022f7981 */
        |                                                                                                /* 0x000162000c1e1900 */
        |        /*0760*/                   S2UR UR5, SR_CgaCtaId ;                                      /* 0x00000000000579c3 */
        |                                                                                                /* 0x000e620000008800 */
        |        /*0770*/                   UMOV UR4, 0x400 ;                                            /* 0x0000040000047882 */
        |                                                                                                /* 0x000fe20000000000 */
        |        /*0780*/                   ULDC.64 UR6, c[0x0][0x228] ;                                 /* 0x00008a0000067ab9 */
        |                                                                                                /* 0x000fe20000000a00 */
        |        /*0790*/                   MEMBAR.ALL.GPU ;                                             /* 0x0000000000007992 */
        |                                                                                                /* 0x000fec000000a000 */
        |        /*07a0*/                   FENCE.VIEW.ASYNC.S ;                                         /* 0x00000000000073c6 */
        |                                                                                                /* 0x000f620000000000 */
        |        /*07b0*/                   S2R R5, SR_TID.X ;                                           /* 0x0000000000057919 */
        |                                                                                                /* 0x000ea20000002100 */
        |        /*07c0*/                   LDC.64 R2, c[0x0][0x238] ;                                   /* 0x00008e00ff027b82 */
        |                                                                                                /* 0x001ea20000000a00 */
        |        /*07d0*/                   ULEA UR4, UR5, UR4, 0x18 ;                                   /* 0x0000000405047291 */
        |                                                                                                /* 0x002fc6000f8ec03f */
        |        /*07e0*/                   ULDC UR5, c[0x0][0x240] ;                                    /* 0x0000900000057ab9 */
        |                                                                                                /* 0x000fe20000000800 */
        |        /*07f0*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;               /* 0x0003ffff04047892 */
        |                                                                                                /* 0x000fe4000f8ec03f */
        |        /*0800*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                  /* 0x0000003f0500728c */
        |                                                                                                /* 0x000fe4000bf05070 */
        |        /*0810*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                              /* 0x0000000404047899 */
        |                                                                                                /* 0x000fe2000800123f */
        |        /*0820*/                   UMOV UR5, URZ ;                                              /* 0x0000003f00057c82 */
        |                                                                                                /* 0x000fc60008000000 */
        |        /*0830*/                   UIADD3.64 UR6, UR4, UR6, URZ ;                               /* 0x0000000604067297 */
        |                                                                                                /* 0x000fe2000fffe03f */
        |        /*0840*/                   IMAD.WIDE.U32 R2, R5, 0x60, R2 ;                             /* 0x0000006005027825 */
        |                                                                                                /* 0x004fe200078e0002 */
        |        /*0850*/                   WARPGROUP.ARRIVE ;                                           /* 0x00000000000079c5 */
        |                                                                                                /* 0x020fce0000000000 */
        |        /*0860*/                   IGMMA.64x48x32.U8.S8 R24, R48, gdesc[UR4], R24, UP0, gsb0 ;  /* 0x0180000430187df1 */
        |                                                                                                /* 0x000fe6000b840018 */
        |        /*0870*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                              /* 0x00008000000079c5 */
        |                                                                                                /* 0x000fe40000010000 */
        |        /*0880*/                   STG.E desc[UR8][R2.64], R24 ;                                /* 0x0000001802007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*0890*/                   STG.E desc[UR8][R2.64+0x4], R25 ;                            /* 0x0000041902007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*08a0*/                   STG.E desc[UR8][R2.64+0x8], R26 ;                            /* 0x0000081a02007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*08b0*/                   STG.E desc[UR8][R2.64+0xc], R27 ;                            /* 0x00000c1b02007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*08c0*/                   STG.E desc[UR8][R2.64+0x10], R28 ;                           /* 0x0000101c02007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*08d0*/                   STG.E desc[UR8][R2.64+0x14], R29 ;                           /* 0x0000141d02007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*08e0*/                   STG.E desc[UR8][R2.64+0x18], R30 ;                           /* 0x0000181e02007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*08f0*/                   STG.E desc[UR8][R2.64+0x1c], R31 ;                           /* 0x00001c1f02007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*0900*/                   STG.E desc[UR8][R2.64+0x20], R32 ;                           /* 0x0000202002007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*0910*/                   STG.E desc[UR8][R2.64+0x24], R33 ;                           /* 0x0000242102007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*0920*/                   STG.E desc[UR8][R2.64+0x28], R34 ;                           /* 0x0000282202007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*0930*/                   STG.E desc[UR8][R2.64+0x2c], R35 ;                           /* 0x00002c2302007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*0940*/                   STG.E desc[UR8][R2.64+0x30], R36 ;                           /* 0x0000302402007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*0950*/                   STG.E desc[UR8][R2.64+0x34], R37 ;                           /* 0x0000342502007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*0960*/                   STG.E desc[UR8][R2.64+0x38], R38 ;                           /* 0x0000382602007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*0970*/                   STG.E desc[UR8][R2.64+0x3c], R39 ;                           /* 0x00003c2702007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*0980*/                   STG.E desc[UR8][R2.64+0x40], R40 ;                           /* 0x0000402802007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*0990*/                   STG.E desc[UR8][R2.64+0x44], R41 ;                           /* 0x0000442902007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*09a0*/                   STG.E desc[UR8][R2.64+0x48], R42 ;                           /* 0x0000482a02007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*09b0*/                   STG.E desc[UR8][R2.64+0x4c], R43 ;                           /* 0x00004c2b02007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*09c0*/                   STG.E desc[UR8][R2.64+0x50], R44 ;                           /* 0x0000502c02007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*09d0*/                   STG.E desc[UR8][R2.64+0x54], R45 ;                           /* 0x0000542d02007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*09e0*/                   STG.E desc[UR8][R2.64+0x58], R46 ;                           /* 0x0000582e02007986 */
        |                                                                                                /* 0x000fe8000c101908 */
        |        /*09f0*/                   STG.E desc[UR8][R2.64+0x5c], R47 ;                           /* 0x00005c2f02007986 */
        |                                                                                                /* 0x000fe2000c101908 */
        |        /*0a00*/                   EXIT ;                                                       /* 0x000000000000794d */
        |                                                                                                /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma11Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                           /* 0x00000a00ff017b82 */
        |                                                                                                    /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R2, SR_TID.X ;                                               /* 0x0000000000027919 */
        |                                                                                                    /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R0, c[0x0][0x218] ;                                          /* 0x00008600ff007b82 */
        |                                                                                                    /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR8, c[0x0][0x208] ;                                     /* 0x0000820000087ab9 */
        |                                                                                                    /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR10, c[0x0][0x210] ;                                    /* 0x00008400000a7ab9 */
        |                                                                                                    /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                             /* 0x0000051000007945 */
        |                                                                                                    /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R2, R0, PT ;                            /* 0x000000000200720c */
        |                                                                                                    /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                                  /* 0x0000000400380947 */
        |                                                                                                    /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R2 ;                                    /* 0x000000ffff037224 */
        |                                                                                                    /* 0x000fca00078e0002 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R0, PT ;                            /* 0x000000000300720c */
        |                                                                                                    /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                       /* 0x00008400ff042b82 */
        |                                                                                                    /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                       /* 0x0000000403042210 */
        |                                                                                                    /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                      /* 0x000000ffff052224 */
        |                                                                                                    /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR8][R4.64] ;                                  /* 0x0000000804042981 */
        |                                                                                                    /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                                  /* 0x0000040000062802 */
        |                                                                                                    /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                             /* 0x0000027000017945 */
        |                                                                                                    /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                         /* 0x000000000000781c */
        |                                                                                                    /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                            /* 0x0000000000072919 */
        |                                                                                                    /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                           /* 0x0000000607062211 */
        |                                                                                                    /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                                /* 0x0000000103072824 */
        |                                                                                                    /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                             /* 0x0000008003032836 */
        |                                                                                                    /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R0, 0x1, -R3 ;                                     /* 0x0000000100067824 */
        |                                                                                                    /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R0, PT ;                            /* 0x000000000300720c */
        |                                                                                                    /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                          /* 0x000001800600780c */
        |                                                                                                    /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                                /* 0x0000000407002388 */
        |                                                                                                    /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                                  /* 0x0000000000700947 */
        |                                                                                                    /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                            /* 0x0000000000057919 */
        |                                                                                                    /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                                  /* 0x0000040000047802 */
        |                                                                                                    /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R0, 0xfffffe80 ;                                      /* 0xfffffe80000c7836 */
        |                                                                                                    /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                         /* 0x000000000000781c */
        |                                                                                                    /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                          /* 0x00000004050e7211 */
        |                                                                                                    /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR10, RZ ;                               /* 0x0000000a03047c10 */
        |                                                                                                    /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                       /* 0x0000008003067836 */
        |                                                                                                    /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                      /* 0x0000010003087836 */
        |                                                                                                    /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                           /* 0x00000180030a7836 */
        |                                                                                                    /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR10, RZ ;                                     /* 0x0000000a06067c10 */
        |                                                                                                    /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR11, P2 ;                                    /* 0x0000000bff057e24 */
        |                                                                                                    /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR10, RZ ;                                     /* 0x0000000a08087c10 */
        |                                                                                                    /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR10, RZ ;                                   /* 0x0000000a0a0a7c10 */
        |                                                                                                    /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR11, P0 ;                                    /* 0x0000000bff077e24 */
        |                                                                                                    /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                  /* 0x0000000804047981 */
        |                                                                                                    /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR11, P2 ;                                    /* 0x0000000bff097e24 */
        |                                                                                                    /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR11, P3 ;                                   /* 0x0000000bff0b7e24 */
        |                                                                                                    /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                                  /* 0x0000000806067981 */
        |                                                                                                    /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR8][R8.64] ;                                  /* 0x0000000808087981 */
        |                                                                                                    /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR8][R10.64] ;                                /* 0x000000080a0a7981 */
        |                                                                                                    /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                    /* 0x000000010e0d7824 */
        |                                                                                                    /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                            /* 0x0000020003037836 */
        |                                                                                                    /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                           /* 0x0000000c0300720c */
        |                                                                                                    /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                               /* 0x000000040d007388 */
        |                                                                                                    /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                          /* 0x000080060d007388 */
        |                                                                                                    /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                         /* 0x000100080d007388 */
        |                                                                                                    /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                        /* 0x0001800a0d007388 */
        |                                                                                                    /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                                  /* 0xfffffffc00a48947 */
        |                                                                                                    /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                       /* 0x0000000000017941 */
        |                                                                                                    /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R0, 0x1, -R3 ;                                     /* 0x0000000100047824 */
        |                                                                                                    /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R0, PT ;                            /* 0x000000000300720c */
        |                                                                                                    /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                             /* 0x0000012000017945 */
        |                                                                                                    /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                           /* 0x000000800400780c */
        |                                                                                                    /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                                  /* 0x00000000003c0947 */
        |                                                                                                    /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                       /* 0x0000008003047836 */
        |                                                                                                    /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                     /* 0x0000840000047ab9 */
        |                                                                                                    /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                      /* 0x0000000403067c10 */
        |                                                                                                    /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                      /* 0x0000000404047c10 */
        |                                                                                                    /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                     /* 0x00000005ff077e24 */
        |                                                                                                    /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                     /* 0x00000005ff057e24 */
        |                                                                                                    /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                                  /* 0x0000000806067981 */
        |                                                                                                    /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                  /* 0x0000000804047981 */
        |                                                                                                    /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                          /* 0x00000000000579c3 */
        |                                                                                                    /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                                /* 0x0000040000047882 */
        |                                                                                                    /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                         /* 0x000000000000781c */
        |                                                                                                    /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                       /* 0x0000000405047291 */
        |                                                                                                    /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                            /* 0x0000000603007988 */
        |                                                                                                    /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                       /* 0x0000800403007988 */
        |                                                                                                    /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                            /* 0x0000010003037836 */
        |                                                                                                    /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                       /* 0x0000000000017941 */
        |                                                                                                    /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R0, PT ;                            /* 0x000000000300720c */
        |                                                                                                    /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                              /* 0x0000000000209947 */
        |                                                                                                    /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                     /* 0x0000840000047ab9 */
        |                                                                                                    /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                      /* 0x0000000403047c10 */
        |                                                                                                    /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                     /* 0x00000005ff057e24 */
        |                                                                                                    /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                  /* 0x0000000804047981 */
        |                                                                                                    /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                          /* 0x00000000000579c3 */
        |                                                                                                    /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                                /* 0x0000040000047882 */
        |                                                                                                    /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                       /* 0x0000000405047291 */
        |                                                                                                    /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                            /* 0x0000000403007988 */
        |                                                                                                    /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                       /* 0x0000000000007941 */
        |                                                                                                    /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R4, c[0x0][0x230] ;                                       /* 0x00008c00ff047b82 */
        |                                                                                                    /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R2, 0x94, R4 ;                                 /* 0x0000009402027825 */
        |                                                                                                    /* 0x001fcc00078e0004 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                    /* 0x0000000000007b1d */
        |                                                                                                    /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R56, desc[UR8][R2.64] ;                                    /* 0x0000000802387981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R57, desc[UR8][R2.64+0x4] ;                                /* 0x0000040802397981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R58, desc[UR8][R2.64+0x8] ;                                /* 0x00000808023a7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R59, desc[UR8][R2.64+0xc] ;                                /* 0x00000c08023b7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R24, desc[UR8][R2.64+0x10] ;                               /* 0x0000100802187981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R25, desc[UR8][R2.64+0x14] ;                               /* 0x0000140802197981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R26, desc[UR8][R2.64+0x18] ;                               /* 0x00001808021a7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R27, desc[UR8][R2.64+0x1c] ;                               /* 0x00001c08021b7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0620*/                   LDG.E R28, desc[UR8][R2.64+0x20] ;                               /* 0x00002008021c7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0630*/                   LDG.E R29, desc[UR8][R2.64+0x24] ;                               /* 0x00002408021d7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0640*/                   LDG.E R30, desc[UR8][R2.64+0x28] ;                               /* 0x00002808021e7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0650*/                   LDG.E R31, desc[UR8][R2.64+0x2c] ;                               /* 0x00002c08021f7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0660*/                   LDG.E R32, desc[UR8][R2.64+0x30] ;                               /* 0x0000300802207981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0670*/                   LDG.E R33, desc[UR8][R2.64+0x34] ;                               /* 0x0000340802217981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0680*/                   LDG.E R34, desc[UR8][R2.64+0x38] ;                               /* 0x0000380802227981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0690*/                   LDG.E R35, desc[UR8][R2.64+0x3c] ;                               /* 0x00003c0802237981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*06a0*/                   LDG.E R36, desc[UR8][R2.64+0x40] ;                               /* 0x0000400802247981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*06b0*/                   LDG.E R37, desc[UR8][R2.64+0x44] ;                               /* 0x0000440802257981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*06c0*/                   LDG.E R38, desc[UR8][R2.64+0x48] ;                               /* 0x0000480802267981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*06d0*/                   LDG.E R39, desc[UR8][R2.64+0x4c] ;                               /* 0x00004c0802277981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*06e0*/                   LDG.E R40, desc[UR8][R2.64+0x50] ;                               /* 0x0000500802287981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*06f0*/                   LDG.E R41, desc[UR8][R2.64+0x54] ;                               /* 0x0000540802297981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0700*/                   LDG.E R42, desc[UR8][R2.64+0x58] ;                               /* 0x00005808022a7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0710*/                   LDG.E R43, desc[UR8][R2.64+0x5c] ;                               /* 0x00005c08022b7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0720*/                   LDG.E R44, desc[UR8][R2.64+0x60] ;                               /* 0x00006008022c7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0730*/                   LDG.E R45, desc[UR8][R2.64+0x64] ;                               /* 0x00006408022d7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0740*/                   LDG.E R46, desc[UR8][R2.64+0x68] ;                               /* 0x00006808022e7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0750*/                   LDG.E R47, desc[UR8][R2.64+0x6c] ;                               /* 0x00006c08022f7981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0760*/                   LDG.E R48, desc[UR8][R2.64+0x70] ;                               /* 0x0000700802307981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0770*/                   LDG.E R49, desc[UR8][R2.64+0x74] ;                               /* 0x0000740802317981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0780*/                   LDG.E R50, desc[UR8][R2.64+0x78] ;                               /* 0x0000780802327981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*0790*/                   LDG.E R51, desc[UR8][R2.64+0x7c] ;                               /* 0x00007c0802337981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*07a0*/                   LDG.E R52, desc[UR8][R2.64+0x80] ;                               /* 0x0000800802347981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*07b0*/                   LDG.E R53, desc[UR8][R2.64+0x84] ;                               /* 0x0000840802357981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*07c0*/                   LDG.E R54, desc[UR8][R2.64+0x88] ;                               /* 0x0000880802367981 */
        |                                                                                                    /* 0x000f68000c1e1900 */
        |        /*07d0*/                   LDG.E R55, desc[UR8][R2.64+0x8c] ;                               /* 0x00008c0802377981 */
        |                                                                                                    /* 0x000162000c1e1900 */
        |        /*07e0*/                   S2UR UR5, SR_CgaCtaId ;                                          /* 0x00000000000579c3 */
        |                                                                                                    /* 0x000e620000008800 */
        |        /*07f0*/                   UMOV UR4, 0x400 ;                                                /* 0x0000040000047882 */
        |                                                                                                    /* 0x000fe20000000000 */
        |        /*0800*/                   ULDC.64 UR6, c[0x0][0x228] ;                                     /* 0x00008a0000067ab9 */
        |                                                                                                    /* 0x000fe20000000a00 */
        |        /*0810*/                   MEMBAR.ALL.GPU ;                                                 /* 0x0000000000007992 */
        |                                                                                                    /* 0x000fec000000a000 */
        |        /*0820*/                   FENCE.VIEW.ASYNC.S ;                                             /* 0x00000000000073c6 */
        |                                                                                                    /* 0x000f620000000000 */
        |        /*0830*/                   S2R R5, SR_TID.X ;                                               /* 0x0000000000057919 */
        |                                                                                                    /* 0x000ea20000002100 */
        |        /*0840*/                   LDC.64 R2, c[0x0][0x238] ;                                       /* 0x00008e00ff027b82 */
        |                                                                                                    /* 0x001ea20000000a00 */
        |        /*0850*/                   ULEA UR4, UR5, UR4, 0x18 ;                                       /* 0x0000000405047291 */
        |                                                                                                    /* 0x002fc6000f8ec03f */
        |        /*0860*/                   ULDC UR5, c[0x0][0x240] ;                                        /* 0x0000900000057ab9 */
        |                                                                                                    /* 0x000fe20000000800 */
        |        /*0870*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;                   /* 0x0003ffff04047892 */
        |                                                                                                    /* 0x000fe4000f8ec03f */
        |        /*0880*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                      /* 0x0000003f0500728c */
        |                                                                                                    /* 0x000fe4000bf05070 */
        |        /*0890*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                                  /* 0x0000000404047899 */
        |                                                                                                    /* 0x000fe2000800123f */
        |        /*08a0*/                   UMOV UR5, URZ ;                                                  /* 0x0000003f00057c82 */
        |                                                                                                    /* 0x000fc60008000000 */
        |        /*08b0*/                   UIADD3.64 UR6, UR4, UR6, URZ ;                                   /* 0x0000000604067297 */
        |                                                                                                    /* 0x000fe2000fffe03f */
        |        /*08c0*/                   IMAD.WIDE.U32 R2, R5, 0x80, R2 ;                                 /* 0x0000008005027825 */
        |                                                                                                    /* 0x004fe200078e0002 */
        |        /*08d0*/                   WARPGROUP.ARRIVE ;                                               /* 0x00000000000079c5 */
        |                                                                                                    /* 0x020fce0000000000 */
        |        /*08e0*/                   BGMMA.64x64x256.AND.POPC R24, R56, gdesc[UR4], R24, UP0, gsb0 ;  /* 0x00a0000438187df2 */
        |                                                                                                    /* 0x000fe6000b814018 */
        |        /*08f0*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                                  /* 0x00008000000079c5 */
        |                                                                                                    /* 0x000fe40000010000 */
        |        /*0900*/                   STG.E desc[UR8][R2.64], R24 ;                                    /* 0x0000001802007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0910*/                   STG.E desc[UR8][R2.64+0x4], R25 ;                                /* 0x0000041902007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0920*/                   STG.E desc[UR8][R2.64+0x8], R26 ;                                /* 0x0000081a02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0930*/                   STG.E desc[UR8][R2.64+0xc], R27 ;                                /* 0x00000c1b02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0940*/                   STG.E desc[UR8][R2.64+0x10], R28 ;                               /* 0x0000101c02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0950*/                   STG.E desc[UR8][R2.64+0x14], R29 ;                               /* 0x0000141d02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0960*/                   STG.E desc[UR8][R2.64+0x18], R30 ;                               /* 0x0000181e02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0970*/                   STG.E desc[UR8][R2.64+0x1c], R31 ;                               /* 0x00001c1f02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0980*/                   STG.E desc[UR8][R2.64+0x20], R32 ;                               /* 0x0000202002007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0990*/                   STG.E desc[UR8][R2.64+0x24], R33 ;                               /* 0x0000242102007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*09a0*/                   STG.E desc[UR8][R2.64+0x28], R34 ;                               /* 0x0000282202007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*09b0*/                   STG.E desc[UR8][R2.64+0x2c], R35 ;                               /* 0x00002c2302007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*09c0*/                   STG.E desc[UR8][R2.64+0x30], R36 ;                               /* 0x0000302402007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*09d0*/                   STG.E desc[UR8][R2.64+0x34], R37 ;                               /* 0x0000342502007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*09e0*/                   STG.E desc[UR8][R2.64+0x38], R38 ;                               /* 0x0000382602007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*09f0*/                   STG.E desc[UR8][R2.64+0x3c], R39 ;                               /* 0x00003c2702007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0a00*/                   STG.E desc[UR8][R2.64+0x40], R40 ;                               /* 0x0000402802007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0a10*/                   STG.E desc[UR8][R2.64+0x44], R41 ;                               /* 0x0000442902007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0a20*/                   STG.E desc[UR8][R2.64+0x48], R42 ;                               /* 0x0000482a02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0a30*/                   STG.E desc[UR8][R2.64+0x4c], R43 ;                               /* 0x00004c2b02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0a40*/                   STG.E desc[UR8][R2.64+0x50], R44 ;                               /* 0x0000502c02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0a50*/                   STG.E desc[UR8][R2.64+0x54], R45 ;                               /* 0x0000542d02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0a60*/                   STG.E desc[UR8][R2.64+0x58], R46 ;                               /* 0x0000582e02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0a70*/                   STG.E desc[UR8][R2.64+0x5c], R47 ;                               /* 0x00005c2f02007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0a80*/                   STG.E desc[UR8][R2.64+0x60], R48 ;                               /* 0x0000603002007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0a90*/                   STG.E desc[UR8][R2.64+0x64], R49 ;                               /* 0x0000643102007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0aa0*/                   STG.E desc[UR8][R2.64+0x68], R50 ;                               /* 0x0000683202007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0ab0*/                   STG.E desc[UR8][R2.64+0x6c], R51 ;                               /* 0x00006c3302007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0ac0*/                   STG.E desc[UR8][R2.64+0x70], R52 ;                               /* 0x0000703402007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0ad0*/                   STG.E desc[UR8][R2.64+0x74], R53 ;                               /* 0x0000743502007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0ae0*/                   STG.E desc[UR8][R2.64+0x78], R54 ;                               /* 0x0000783602007986 */
        |                                                                                                    /* 0x000fe8000c101908 */
        |        /*0af0*/                   STG.E desc[UR8][R2.64+0x7c], R55 ;                               /* 0x00007c3702007986 */
        |                                                                                                    /* 0x000fe2000c101908 */
        |        /*0b00*/                   EXIT ;                                                           /* 0x000000000000794d */
        |                                                                                                    /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma12Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                      /* 0x00000a00ff017b82 */
        |                                                                                               /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                          /* 0x0000000000007919 */
        |                                                                                               /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                     /* 0x00008600ff027b82 */
        |                                                                                               /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                                /* 0x0000820000067ab9 */
        |                                                                                               /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                               /* 0x00008400000c7ab9 */
        |                                                                                               /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                        /* 0x0000051000007945 */
        |                                                                                               /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                       /* 0x000000020000720c */
        |                                                                                               /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                             /* 0x0000000400380947 */
        |                                                                                               /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                               /* 0x000000ffff037224 */
        |                                                                                               /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                       /* 0x000000020300720c */
        |                                                                                               /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                  /* 0x00008400ff042b82 */
        |                                                                                               /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                  /* 0x0000000403042210 */
        |                                                                                               /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                 /* 0x000000ffff052224 */
        |                                                                                               /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                             /* 0x0000000604042981 */
        |                                                                                               /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                             /* 0x0000040000062802 */
        |                                                                                               /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                        /* 0x0000027000017945 */
        |                                                                                               /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                    /* 0x000000000000781c */
        |                                                                                               /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                       /* 0x0000000000072919 */
        |                                                                                               /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                      /* 0x0000000607062211 */
        |                                                                                               /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                           /* 0x0000000103072824 */
        |                                                                                               /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                        /* 0x0000008003032836 */
        |                                                                                               /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                /* 0x0000000102067824 */
        |                                                                                               /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                       /* 0x000000020300720c */
        |                                                                                               /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                     /* 0x000001800600780c */
        |                                                                                               /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                           /* 0x0000000407002388 */
        |                                                                                               /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                             /* 0x0000000000700947 */
        |                                                                                               /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                       /* 0x0000000000057919 */
        |                                                                                               /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                             /* 0x0000040000047802 */
        |                                                                                               /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                 /* 0xfffffe80020c7836 */
        |                                                                                               /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                    /* 0x000000000000781c */
        |                                                                                               /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                     /* 0x00000004050e7211 */
        |                                                                                               /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                          /* 0x0000000c03047c10 */
        |                                                                                               /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                  /* 0x0000008003067836 */
        |                                                                                               /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                 /* 0x0000010003087836 */
        |                                                                                               /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                      /* 0x00000180030a7836 */
        |                                                                                               /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                                /* 0x0000000c06067c10 */
        |                                                                                               /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                               /* 0x0000000dff057e24 */
        |                                                                                               /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                                /* 0x0000000c08087c10 */
        |                                                                                               /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                              /* 0x0000000c0a0a7c10 */
        |                                                                                               /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                               /* 0x0000000dff077e24 */
        |                                                                                               /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                             /* 0x0000000604047981 */
        |                                                                                               /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                               /* 0x0000000dff097e24 */
        |                                                                                               /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                              /* 0x0000000dff0b7e24 */
        |                                                                                               /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                             /* 0x0000000606067981 */
        |                                                                                               /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                             /* 0x0000000608087981 */
        |                                                                                               /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                           /* 0x000000060a0a7981 */
        |                                                                                               /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                               /* 0x000000010e0d7824 */
        |                                                                                               /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                       /* 0x0000020003037836 */
        |                                                                                               /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                      /* 0x0000000c0300720c */
        |                                                                                               /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                          /* 0x000000040d007388 */
        |                                                                                               /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                     /* 0x000080060d007388 */
        |                                                                                               /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                    /* 0x000100080d007388 */
        |                                                                                               /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                   /* 0x0001800a0d007388 */
        |                                                                                               /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                             /* 0xfffffffc00a48947 */
        |                                                                                               /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                  /* 0x0000000000017941 */
        |                                                                                               /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                /* 0x0000000102047824 */
        |                                                                                               /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                       /* 0x000000020300720c */
        |                                                                                               /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                        /* 0x0000012000017945 */
        |                                                                                               /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                      /* 0x000000800400780c */
        |                                                                                               /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                             /* 0x00000000003c0947 */
        |                                                                                               /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                  /* 0x0000008003047836 */
        |                                                                                               /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                /* 0x0000840000047ab9 */
        |                                                                                               /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                 /* 0x0000000403067c10 */
        |                                                                                               /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                 /* 0x0000000404047c10 */
        |                                                                                               /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                /* 0x00000005ff077e24 */
        |                                                                                               /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                /* 0x00000005ff057e24 */
        |                                                                                               /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                             /* 0x0000000606067981 */
        |                                                                                               /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                             /* 0x0000000604047981 */
        |                                                                                               /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                     /* 0x00000000000579c3 */
        |                                                                                               /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                           /* 0x0000040000047882 */
        |                                                                                               /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                    /* 0x000000000000781c */
        |                                                                                               /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                  /* 0x0000000405047291 */
        |                                                                                               /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                       /* 0x0000000603007988 */
        |                                                                                               /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                  /* 0x0000800403007988 */
        |                                                                                               /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                       /* 0x0000010003037836 */
        |                                                                                               /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                  /* 0x0000000000017941 */
        |                                                                                               /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                       /* 0x000000020300720c */
        |                                                                                               /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                         /* 0x0000000000209947 */
        |                                                                                               /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                /* 0x0000840000047ab9 */
        |                                                                                               /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                 /* 0x0000000403047c10 */
        |                                                                                               /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                /* 0x00000005ff057e24 */
        |                                                                                               /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                             /* 0x0000000604047981 */
        |                                                                                               /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                     /* 0x00000000000579c3 */
        |                                                                                               /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                           /* 0x0000040000047882 */
        |                                                                                               /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                  /* 0x0000000405047291 */
        |                                                                                               /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                       /* 0x0000000403007988 */
        |                                                                                               /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                  /* 0x0000000000007941 */
        |                                                                                               /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                  /* 0x00008c00ff027b82 */
        |                                                                                               /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x34, R2 ;                            /* 0x0000003400027825 */
        |                                                                                               /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                               /* 0x0000000000007b1d */
        |                                                                                               /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R24, desc[UR6][R2.64+0x10] ;                          /* 0x0000100602187981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R25, desc[UR6][R2.64+0x14] ;                          /* 0x0000140602197981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R26, desc[UR6][R2.64+0x18] ;                          /* 0x00001806021a7981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R27, desc[UR6][R2.64+0x1c] ;                          /* 0x00001c06021b7981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R28, desc[UR6][R2.64+0x20] ;                          /* 0x00002006021c7981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R29, desc[UR6][R2.64+0x24] ;                          /* 0x00002406021d7981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R30, desc[UR6][R2.64+0x28] ;                          /* 0x00002806021e7981 */
        |                                                                                               /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R31, desc[UR6][R2.64+0x2c] ;                          /* 0x00002c06021f7981 */
        |                                                                                               /* 0x000162000c1e1900 */
        |        /*0620*/                   S2UR UR5, SR_CgaCtaId ;                                     /* 0x00000000000579c3 */
        |                                                                                               /* 0x000e620000008800 */
        |        /*0630*/                   UMOV UR4, 0x400 ;                                           /* 0x0000040000047882 */
        |                                                                                               /* 0x000fe20000000000 */
        |        /*0640*/                   ULDC.64 UR8, c[0x0][0x220] ;                                /* 0x0000880000087ab9 */
        |                                                                                               /* 0x000fe20000000a00 */
        |        /*0650*/                   MEMBAR.ALL.GPU ;                                            /* 0x0000000000007992 */
        |                                                                                               /* 0x000fec000000a000 */
        |        /*0660*/                   FENCE.VIEW.ASYNC.S ;                                        /* 0x00000000000073c6 */
        |                                                                                               /* 0x000f620000000000 */
        |        /*0670*/                   ULDC.64 UR10, c[0x0][0x228] ;                               /* 0x00008a00000a7ab9 */
        |                                                                                               /* 0x000fe20000000a00 */
        |        /*0680*/                   LDC.64 R2, c[0x0][0x238] ;                                  /* 0x00008e00ff027b82 */
        |                                                                                               /* 0x001e220000000a00 */
        |        /*0690*/                   ULEA UR4, UR5, UR4, 0x18 ;                                  /* 0x0000000405047291 */
        |                                                                                               /* 0x002fc6000f8ec03f */
        |        /*06a0*/                   ULDC UR5, c[0x0][0x240] ;                                   /* 0x0000900000057ab9 */
        |                                                                                               /* 0x000fe20000000800 */
        |        /*06b0*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;              /* 0x0003ffff04047892 */
        |                                                                                               /* 0x000fe4000f8ec03f */
        |        /*06c0*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                 /* 0x0000003f0500728c */
        |                                                                                               /* 0x000fe4000bf05070 */
        |        /*06d0*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                             /* 0x0000000404047899 */
        |                                                                                               /* 0x000fe2000800123f */
        |        /*06e0*/                   IMAD.WIDE.U32 R2, R0, 0x20, R2 ;                            /* 0x0000002000027825 */
        |                                                                                               /* 0x001fe200078e0002 */
        |        /*06f0*/                   UMOV UR5, URZ ;                                             /* 0x0000003f00057c82 */
        |                                                                                               /* 0x000fe40008000000 */
        |        /*0700*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                              /* 0x0000000804087297 */
        |                                                                                               /* 0x000fe4000fffe03f */
        |        /*0710*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                            /* 0x0000000a040a7297 */
        |                                                                                               /* 0x000fe2000fffe03f */
        |        /*0720*/                   WARPGROUP.ARRIVE ;                                          /* 0x00000000000079c5 */
        |                                                                                               /* 0x020fd00000000000 */
        |        /*0730*/                   BGMMA.64x16x256.AND.POPC R24, gdesc[UR8], R24, UP0, gsb0 ;  /* 0x00200000081879f2 */
        |                                                                                               /* 0x000fe6000b814018 */
        |        /*0740*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                             /* 0x00008000000079c5 */
        |                                                                                               /* 0x000fe40000010000 */
        |        /*0750*/                   STG.E desc[UR6][R2.64], R24 ;                               /* 0x0000001802007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*0760*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                           /* 0x0000041902007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*0770*/                   STG.E desc[UR6][R2.64+0x8], R26 ;                           /* 0x0000081a02007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*0780*/                   STG.E desc[UR6][R2.64+0xc], R27 ;                           /* 0x00000c1b02007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*0790*/                   STG.E desc[UR6][R2.64+0x10], R28 ;                          /* 0x0000101c02007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*07a0*/                   STG.E desc[UR6][R2.64+0x14], R29 ;                          /* 0x0000141d02007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*07b0*/                   STG.E desc[UR6][R2.64+0x18], R30 ;                          /* 0x0000181e02007986 */
        |                                                                                               /* 0x000fe8000c101906 */
        |        /*07c0*/                   STG.E desc[UR6][R2.64+0x1c], R31 ;                          /* 0x00001c1f02007986 */
        |                                                                                               /* 0x000fe2000c101906 */
        |        /*07d0*/                   EXIT ;                                                      /* 0x000000000000794d */
        |                                                                                               /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma13Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                            /* 0x00000a00ff017b82 */
        |                                                                                                     /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                                /* 0x0000000000007919 */
        |                                                                                                     /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                           /* 0x00008600ff027b82 */
        |                                                                                                     /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                                      /* 0x0000820000067ab9 */
        |                                                                                                     /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                                     /* 0x00008400000c7ab9 */
        |                                                                                                     /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                              /* 0x0000051000007945 */
        |                                                                                                     /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                             /* 0x000000020000720c */
        |                                                                                                     /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                                   /* 0x0000000400380947 */
        |                                                                                                     /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                                     /* 0x000000ffff037224 */
        |                                                                                                     /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                             /* 0x000000020300720c */
        |                                                                                                     /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                        /* 0x00008400ff042b82 */
        |                                                                                                     /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                        /* 0x0000000403042210 */
        |                                                                                                     /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                       /* 0x000000ffff052224 */
        |                                                                                                     /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                                   /* 0x0000000604042981 */
        |                                                                                                     /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                                   /* 0x0000040000062802 */
        |                                                                                                     /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                              /* 0x0000027000017945 */
        |                                                                                                     /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                          /* 0x000000000000781c */
        |                                                                                                     /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                             /* 0x0000000000072919 */
        |                                                                                                     /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                            /* 0x0000000607062211 */
        |                                                                                                     /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                                 /* 0x0000000103072824 */
        |                                                                                                     /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                              /* 0x0000008003032836 */
        |                                                                                                     /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                      /* 0x0000000102067824 */
        |                                                                                                     /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                             /* 0x000000020300720c */
        |                                                                                                     /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                           /* 0x000001800600780c */
        |                                                                                                     /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                                 /* 0x0000000407002388 */
        |                                                                                                     /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                                   /* 0x0000000000700947 */
        |                                                                                                     /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                             /* 0x0000000000057919 */
        |                                                                                                     /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                                   /* 0x0000040000047802 */
        |                                                                                                     /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                       /* 0xfffffe80020c7836 */
        |                                                                                                     /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                          /* 0x000000000000781c */
        |                                                                                                     /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                           /* 0x00000004050e7211 */
        |                                                                                                     /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                                /* 0x0000000c03047c10 */
        |                                                                                                     /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                        /* 0x0000008003067836 */
        |                                                                                                     /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                       /* 0x0000010003087836 */
        |                                                                                                     /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                            /* 0x00000180030a7836 */
        |                                                                                                     /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                                      /* 0x0000000c06067c10 */
        |                                                                                                     /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                                     /* 0x0000000dff057e24 */
        |                                                                                                     /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                                      /* 0x0000000c08087c10 */
        |                                                                                                     /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                                    /* 0x0000000c0a0a7c10 */
        |                                                                                                     /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                                     /* 0x0000000dff077e24 */
        |                                                                                                     /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                   /* 0x0000000604047981 */
        |                                                                                                     /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                                     /* 0x0000000dff097e24 */
        |                                                                                                     /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                                    /* 0x0000000dff0b7e24 */
        |                                                                                                     /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                   /* 0x0000000606067981 */
        |                                                                                                     /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                                   /* 0x0000000608087981 */
        |                                                                                                     /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                                 /* 0x000000060a0a7981 */
        |                                                                                                     /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                     /* 0x000000010e0d7824 */
        |                                                                                                     /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                             /* 0x0000020003037836 */
        |                                                                                                     /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                            /* 0x0000000c0300720c */
        |                                                                                                     /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                                /* 0x000000040d007388 */
        |                                                                                                     /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                           /* 0x000080060d007388 */
        |                                                                                                     /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                          /* 0x000100080d007388 */
        |                                                                                                     /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                         /* 0x0001800a0d007388 */
        |                                                                                                     /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                                   /* 0xfffffffc00a48947 */
        |                                                                                                     /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                        /* 0x0000000000017941 */
        |                                                                                                     /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                      /* 0x0000000102047824 */
        |                                                                                                     /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                             /* 0x000000020300720c */
        |                                                                                                     /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                              /* 0x0000012000017945 */
        |                                                                                                     /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                            /* 0x000000800400780c */
        |                                                                                                     /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                                   /* 0x00000000003c0947 */
        |                                                                                                     /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                        /* 0x0000008003047836 */
        |                                                                                                     /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                      /* 0x0000840000047ab9 */
        |                                                                                                     /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                       /* 0x0000000403067c10 */
        |                                                                                                     /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                       /* 0x0000000404047c10 */
        |                                                                                                     /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                      /* 0x00000005ff077e24 */
        |                                                                                                     /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                      /* 0x00000005ff057e24 */
        |                                                                                                     /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                   /* 0x0000000606067981 */
        |                                                                                                     /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                   /* 0x0000000604047981 */
        |                                                                                                     /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                           /* 0x00000000000579c3 */
        |                                                                                                     /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                                 /* 0x0000040000047882 */
        |                                                                                                     /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                          /* 0x000000000000781c */
        |                                                                                                     /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                        /* 0x0000000405047291 */
        |                                                                                                     /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                             /* 0x0000000603007988 */
        |                                                                                                     /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                        /* 0x0000800403007988 */
        |                                                                                                     /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                             /* 0x0000010003037836 */
        |                                                                                                     /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                        /* 0x0000000000017941 */
        |                                                                                                     /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                             /* 0x000000020300720c */
        |                                                                                                     /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                               /* 0x0000000000209947 */
        |                                                                                                     /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                      /* 0x0000840000047ab9 */
        |                                                                                                     /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                       /* 0x0000000403047c10 */
        |                                                                                                     /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                      /* 0x00000005ff057e24 */
        |                                                                                                     /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                   /* 0x0000000604047981 */
        |                                                                                                     /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                           /* 0x00000000000579c3 */
        |                                                                                                     /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                                 /* 0x0000040000047882 */
        |                                                                                                     /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                        /* 0x0000000405047291 */
        |                                                                                                     /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                             /* 0x0000000403007988 */
        |                                                                                                     /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                        /* 0x0000000000007941 */
        |                                                                                                     /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                        /* 0x00008c00ff027b82 */
        |                                                                                                     /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x24, R2 ;                                  /* 0x0000002400027825 */
        |                                                                                                     /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                     /* 0x0000000000007b1d */
        |                                                                                                     /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R28, desc[UR6][R2.64+0x20] ;                                /* 0x00002006021c7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R24, desc[UR6][R2.64+0x10] ;                                /* 0x0000100602187981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R25, desc[UR6][R2.64+0x14] ;                                /* 0x0000140602197981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R26, desc[UR6][R2.64+0x18] ;                                /* 0x00001806021a7981 */
        |                                                                                                     /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R27, desc[UR6][R2.64+0x1c] ;                                /* 0x00001c06021b7981 */
        |                                                                                                     /* 0x000162000c1e1900 */
        |        /*05f0*/                   S2UR UR5, SR_CgaCtaId ;                                           /* 0x00000000000579c3 */
        |                                                                                                     /* 0x000e620000008800 */
        |        /*0600*/                   UMOV UR4, 0x400 ;                                                 /* 0x0000040000047882 */
        |                                                                                                     /* 0x000fe20000000000 */
        |        /*0610*/                   ULDC.64 UR8, c[0x0][0x220] ;                                      /* 0x0000880000087ab9 */
        |                                                                                                     /* 0x000fe20000000a00 */
        |        /*0620*/                   MEMBAR.ALL.GPU ;                                                  /* 0x0000000000007992 */
        |                                                                                                     /* 0x000fec000000a000 */
        |        /*0630*/                   FENCE.VIEW.ASYNC.S ;                                              /* 0x00000000000073c6 */
        |                                                                                                     /* 0x000f620000000000 */
        |        /*0640*/                   ULDC.64 UR10, c[0x0][0x228] ;                                     /* 0x00008a00000a7ab9 */
        |                                                                                                     /* 0x000fe20000000a00 */
        |        /*0650*/                   LDC.64 R2, c[0x0][0x238] ;                                        /* 0x00008e00ff027b82 */
        |                                                                                                     /* 0x001e220000000a00 */
        |        /*0660*/                   ULEA UR4, UR5, UR4, 0x18 ;                                        /* 0x0000000405047291 */
        |                                                                                                     /* 0x002fc6000f8ec03f */
        |        /*0670*/                   ULDC UR5, c[0x0][0x240] ;                                         /* 0x0000900000057ab9 */
        |                                                                                                     /* 0x000fe20000000800 */
        |        /*0680*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;                    /* 0x0003ffff04047892 */
        |                                                                                                     /* 0x000fe4000f8ec03f */
        |        /*0690*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                       /* 0x0000003f0500728c */
        |                                                                                                     /* 0x000fe4000bf05070 */
        |        /*06a0*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                                   /* 0x0000000404047899 */
        |                                                                                                     /* 0x000fe2000800123f */
        |        /*06b0*/                   IMAD.WIDE.U32 R2, R0, 0x10, R2 ;                                  /* 0x0000001000027825 */
        |                                                                                                     /* 0x001fe200078e0002 */
        |        /*06c0*/                   UMOV UR5, URZ ;                                                   /* 0x0000003f00057c82 */
        |                                                                                                     /* 0x000fe40008000000 */
        |        /*06d0*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                                    /* 0x0000000804087297 */
        |                                                                                                     /* 0x000fe4000fffe03f */
        |        /*06e0*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                                  /* 0x0000000a040a7297 */
        |                                                                                                     /* 0x000fe2000fffe03f */
        |        /*06f0*/                   WARPGROUP.ARRIVE ;                                                /* 0x00000000000079c5 */
        |                                                                                                     /* 0x020fd00000000000 */
        |        /*0700*/                   HGMMA.SP.64x8x32.F32 R24, gdesc[UR8], R24, UP0, R28, 0x0, gsb0 ;  /* 0x08001c00081879f0 */
        |                                                                                                     /* 0x000fe6000b800a18 */
        |        /*0710*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                                   /* 0x00008000000079c5 */
        |                                                                                                     /* 0x000fe40000010000 */
        |        /*0720*/                   STG.E desc[UR6][R2.64], R24 ;                                     /* 0x0000001802007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0730*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                                 /* 0x0000041902007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0740*/                   STG.E desc[UR6][R2.64+0x8], R26 ;                                 /* 0x0000081a02007986 */
        |                                                                                                     /* 0x000fe8000c101906 */
        |        /*0750*/                   STG.E desc[UR6][R2.64+0xc], R27 ;                                 /* 0x00000c1b02007986 */
        |                                                                                                     /* 0x000fe2000c101906 */
        |        /*0760*/                   EXIT ;                                                            /* 0x000000000000794d */
        |                                                                                                     /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma14Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                                       /* 0x00000a00ff017b82 */
        |                                                                                                                /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                                           /* 0x0000000000007919 */
        |                                                                                                                /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                                      /* 0x00008600ff027b82 */
        |                                                                                                                /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR8, c[0x0][0x208] ;                                                 /* 0x0000820000087ab9 */
        |                                                                                                                /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR10, c[0x0][0x210] ;                                                /* 0x00008400000a7ab9 */
        |                                                                                                                /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                                         /* 0x0000051000007945 */
        |                                                                                                                /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                                        /* 0x000000020000720c */
        |                                                                                                                /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                                              /* 0x0000000400380947 */
        |                                                                                                                /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                                                /* 0x000000ffff037224 */
        |                                                                                                                /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                                        /* 0x000000020300720c */
        |                                                                                                                /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                                   /* 0x00008400ff042b82 */
        |                                                                                                                /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                                   /* 0x0000000403042210 */
        |                                                                                                                /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                                  /* 0x000000ffff052224 */
        |                                                                                                                /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR8][R4.64] ;                                              /* 0x0000000804042981 */
        |                                                                                                                /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                                              /* 0x0000040000062802 */
        |                                                                                                                /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                                         /* 0x0000027000017945 */
        |                                                                                                                /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                                     /* 0x000000000000781c */
        |                                                                                                                /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                                        /* 0x0000000000072919 */
        |                                                                                                                /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                                       /* 0x0000000607062211 */
        |                                                                                                                /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                                            /* 0x0000000103072824 */
        |                                                                                                                /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                                         /* 0x0000008003032836 */
        |                                                                                                                /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                                 /* 0x0000000102067824 */
        |                                                                                                                /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                                        /* 0x000000020300720c */
        |                                                                                                                /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                                      /* 0x000001800600780c */
        |                                                                                                                /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                                            /* 0x0000000407002388 */
        |                                                                                                                /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                                              /* 0x0000000000700947 */
        |                                                                                                                /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                                        /* 0x0000000000057919 */
        |                                                                                                                /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                                              /* 0x0000040000047802 */
        |                                                                                                                /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                                  /* 0xfffffe80020c7836 */
        |                                                                                                                /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                                     /* 0x000000000000781c */
        |                                                                                                                /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                                      /* 0x00000004050e7211 */
        |                                                                                                                /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR10, RZ ;                                           /* 0x0000000a03047c10 */
        |                                                                                                                /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                                   /* 0x0000008003067836 */
        |                                                                                                                /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                                  /* 0x0000010003087836 */
        |                                                                                                                /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                                       /* 0x00000180030a7836 */
        |                                                                                                                /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR10, RZ ;                                                 /* 0x0000000a06067c10 */
        |                                                                                                                /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR11, P2 ;                                                /* 0x0000000bff057e24 */
        |                                                                                                                /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR10, RZ ;                                                 /* 0x0000000a08087c10 */
        |                                                                                                                /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR10, RZ ;                                               /* 0x0000000a0a0a7c10 */
        |                                                                                                                /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR11, P0 ;                                                /* 0x0000000bff077e24 */
        |                                                                                                                /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                              /* 0x0000000804047981 */
        |                                                                                                                /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR11, P2 ;                                                /* 0x0000000bff097e24 */
        |                                                                                                                /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR11, P3 ;                                               /* 0x0000000bff0b7e24 */
        |                                                                                                                /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                                              /* 0x0000000806067981 */
        |                                                                                                                /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR8][R8.64] ;                                              /* 0x0000000808087981 */
        |                                                                                                                /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR8][R10.64] ;                                            /* 0x000000080a0a7981 */
        |                                                                                                                /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                                /* 0x000000010e0d7824 */
        |                                                                                                                /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                                        /* 0x0000020003037836 */
        |                                                                                                                /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                                       /* 0x0000000c0300720c */
        |                                                                                                                /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                                           /* 0x000000040d007388 */
        |                                                                                                                /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                                      /* 0x000080060d007388 */
        |                                                                                                                /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                                     /* 0x000100080d007388 */
        |                                                                                                                /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                                    /* 0x0001800a0d007388 */
        |                                                                                                                /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                                              /* 0xfffffffc00a48947 */
        |                                                                                                                /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                                   /* 0x0000000000017941 */
        |                                                                                                                /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                                 /* 0x0000000102047824 */
        |                                                                                                                /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                                        /* 0x000000020300720c */
        |                                                                                                                /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                                         /* 0x0000012000017945 */
        |                                                                                                                /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                                       /* 0x000000800400780c */
        |                                                                                                                /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                                              /* 0x00000000003c0947 */
        |                                                                                                                /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                                   /* 0x0000008003047836 */
        |                                                                                                                /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                                 /* 0x0000840000047ab9 */
        |                                                                                                                /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                                  /* 0x0000000403067c10 */
        |                                                                                                                /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                                  /* 0x0000000404047c10 */
        |                                                                                                                /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                                 /* 0x00000005ff077e24 */
        |                                                                                                                /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                                 /* 0x00000005ff057e24 */
        |                                                                                                                /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR8][R6.64] ;                                              /* 0x0000000806067981 */
        |                                                                                                                /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                              /* 0x0000000804047981 */
        |                                                                                                                /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                                      /* 0x00000000000579c3 */
        |                                                                                                                /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                                            /* 0x0000040000047882 */
        |                                                                                                                /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                                     /* 0x000000000000781c */
        |                                                                                                                /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                                   /* 0x0000000405047291 */
        |                                                                                                                /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                                        /* 0x0000000603007988 */
        |                                                                                                                /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                                   /* 0x0000800403007988 */
        |                                                                                                                /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                                        /* 0x0000010003037836 */
        |                                                                                                                /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                                   /* 0x0000000000017941 */
        |                                                                                                                /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                                        /* 0x000000020300720c */
        |                                                                                                                /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                                          /* 0x0000000000209947 */
        |                                                                                                                /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                                 /* 0x0000840000047ab9 */
        |                                                                                                                /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                                  /* 0x0000000403047c10 */
        |                                                                                                                /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                                 /* 0x00000005ff057e24 */
        |                                                                                                                /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR8][R4.64] ;                                              /* 0x0000000804047981 */
        |                                                                                                                /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                                      /* 0x00000000000579c3 */
        |                                                                                                                /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                                            /* 0x0000040000047882 */
        |                                                                                                                /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                                   /* 0x0000000405047291 */
        |                                                                                                                /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                                        /* 0x0000000403007988 */
        |                                                                                                                /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                                   /* 0x0000000000007941 */
        |                                                                                                                /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                                   /* 0x00008c00ff027b82 */
        |                                                                                                                /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x54, R2 ;                                             /* 0x0000005400027825 */
        |                                                                                                                /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                                /* 0x0000000000007b1d */
        |                                                                                                                /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R44, desc[UR8][R2.64+0x50] ;                                           /* 0x00005008022c7981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R40, desc[UR8][R2.64] ;                                                /* 0x0000000802287981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R41, desc[UR8][R2.64+0x4] ;                                            /* 0x0000040802297981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R42, desc[UR8][R2.64+0x8] ;                                            /* 0x00000808022a7981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R43, desc[UR8][R2.64+0xc] ;                                            /* 0x00000c08022b7981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R24, desc[UR8][R2.64+0x10] ;                                           /* 0x0000100802187981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R25, desc[UR8][R2.64+0x14] ;                                           /* 0x0000140802197981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R26, desc[UR8][R2.64+0x18] ;                                           /* 0x00001808021a7981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*0620*/                   LDG.E R27, desc[UR8][R2.64+0x1c] ;                                           /* 0x00001c08021b7981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*0630*/                   LDG.E R28, desc[UR8][R2.64+0x20] ;                                           /* 0x00002008021c7981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*0640*/                   LDG.E R29, desc[UR8][R2.64+0x24] ;                                           /* 0x00002408021d7981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*0650*/                   LDG.E R30, desc[UR8][R2.64+0x28] ;                                           /* 0x00002808021e7981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*0660*/                   LDG.E R31, desc[UR8][R2.64+0x2c] ;                                           /* 0x00002c08021f7981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*0670*/                   LDG.E R32, desc[UR8][R2.64+0x30] ;                                           /* 0x0000300802207981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*0680*/                   LDG.E R33, desc[UR8][R2.64+0x34] ;                                           /* 0x0000340802217981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*0690*/                   LDG.E R34, desc[UR8][R2.64+0x38] ;                                           /* 0x0000380802227981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*06a0*/                   LDG.E R35, desc[UR8][R2.64+0x3c] ;                                           /* 0x00003c0802237981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*06b0*/                   LDG.E R36, desc[UR8][R2.64+0x40] ;                                           /* 0x0000400802247981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*06c0*/                   LDG.E R37, desc[UR8][R2.64+0x44] ;                                           /* 0x0000440802257981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*06d0*/                   LDG.E R38, desc[UR8][R2.64+0x48] ;                                           /* 0x0000480802267981 */
        |                                                                                                                /* 0x000f68000c1e1900 */
        |        /*06e0*/                   LDG.E R39, desc[UR8][R2.64+0x4c] ;                                           /* 0x00004c0802277981 */
        |                                                                                                                /* 0x000162000c1e1900 */
        |        /*06f0*/                   S2UR UR5, SR_CgaCtaId ;                                                      /* 0x00000000000579c3 */
        |                                                                                                                /* 0x000e620000008800 */
        |        /*0700*/                   UMOV UR4, 0x400 ;                                                            /* 0x0000040000047882 */
        |                                                                                                                /* 0x000fe20000000000 */
        |        /*0710*/                   ULDC.64 UR6, c[0x0][0x228] ;                                                 /* 0x00008a0000067ab9 */
        |                                                                                                                /* 0x000fe20000000a00 */
        |        /*0720*/                   MEMBAR.ALL.GPU ;                                                             /* 0x0000000000007992 */
        |                                                                                                                /* 0x000fec000000a000 */
        |        /*0730*/                   FENCE.VIEW.ASYNC.S ;                                                         /* 0x00000000000073c6 */
        |                                                                                                                /* 0x000f620000000000 */
        |        /*0740*/                   LDC.64 R2, c[0x0][0x238] ;                                                   /* 0x00008e00ff027b82 */
        |                                                                                                                /* 0x001e220000000a00 */
        |        /*0750*/                   ULEA UR4, UR5, UR4, 0x18 ;                                                   /* 0x0000000405047291 */
        |                                                                                                                /* 0x002fc6000f8ec03f */
        |        /*0760*/                   ULDC UR5, c[0x0][0x240] ;                                                    /* 0x0000900000057ab9 */
        |                                                                                                                /* 0x000fe20000000800 */
        |        /*0770*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;                               /* 0x0003ffff04047892 */
        |                                                                                                                /* 0x000fe4000f8ec03f */
        |        /*0780*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                                  /* 0x0000003f0500728c */
        |                                                                                                                /* 0x000fe4000bf05070 */
        |        /*0790*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                                              /* 0x0000000404047899 */
        |                                                                                                                /* 0x000fe2000800123f */
        |        /*07a0*/                   IMAD.WIDE.U32 R2, R0, 0x40, R2 ;                                             /* 0x0000004000027825 */
        |                                                                                                                /* 0x001fe200078e0002 */
        |        /*07b0*/                   UMOV UR5, URZ ;                                                              /* 0x0000003f00057c82 */
        |                                                                                                                /* 0x000fe40008000000 */
        |        /*07c0*/                   UIADD3.64 UR6, UR4, UR6, URZ ;                                               /* 0x0000000604067297 */
        |                                                                                                                /* 0x000fe2000fffe03f */
        |        /*07d0*/                   WARPGROUP.ARRIVE ;                                                           /* 0x00000000000079c5 */
        |                                                                                                                /* 0x020fd00000000000 */
        |        /*07e0*/                   HGMMA.SP.64x32x16.F32.TF32 R24, R40, gdesc[UR4], R24, UP0, R44, 0x1, gsb0 ;  /* 0x00612c0428187df0 */
        |                                                                                                                /* 0x000fe6000b802a18 */
        |        /*07f0*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                                              /* 0x00008000000079c5 */
        |                                                                                                                /* 0x000fe40000010000 */
        |        /*0800*/                   STG.E desc[UR8][R2.64], R24 ;                                                /* 0x0000001802007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*0810*/                   STG.E desc[UR8][R2.64+0x4], R25 ;                                            /* 0x0000041902007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*0820*/                   STG.E desc[UR8][R2.64+0x8], R26 ;                                            /* 0x0000081a02007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*0830*/                   STG.E desc[UR8][R2.64+0xc], R27 ;                                            /* 0x00000c1b02007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*0840*/                   STG.E desc[UR8][R2.64+0x10], R28 ;                                           /* 0x0000101c02007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*0850*/                   STG.E desc[UR8][R2.64+0x14], R29 ;                                           /* 0x0000141d02007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*0860*/                   STG.E desc[UR8][R2.64+0x18], R30 ;                                           /* 0x0000181e02007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*0870*/                   STG.E desc[UR8][R2.64+0x1c], R31 ;                                           /* 0x00001c1f02007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*0880*/                   STG.E desc[UR8][R2.64+0x20], R32 ;                                           /* 0x0000202002007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*0890*/                   STG.E desc[UR8][R2.64+0x24], R33 ;                                           /* 0x0000242102007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*08a0*/                   STG.E desc[UR8][R2.64+0x28], R34 ;                                           /* 0x0000282202007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*08b0*/                   STG.E desc[UR8][R2.64+0x2c], R35 ;                                           /* 0x00002c2302007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*08c0*/                   STG.E desc[UR8][R2.64+0x30], R36 ;                                           /* 0x0000302402007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*08d0*/                   STG.E desc[UR8][R2.64+0x34], R37 ;                                           /* 0x0000342502007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*08e0*/                   STG.E desc[UR8][R2.64+0x38], R38 ;                                           /* 0x0000382602007986 */
        |                                                                                                                /* 0x000fe8000c101908 */
        |        /*08f0*/                   STG.E desc[UR8][R2.64+0x3c], R39 ;                                           /* 0x00003c2702007986 */
        |                                                                                                                /* 0x000fe2000c101908 */
        |        /*0900*/                   EXIT ;                                                                       /* 0x000000000000794d */
        |                                                                                                                /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma15Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                                  /* 0x00000a00ff017b82 */
        |                                                                                                           /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                                      /* 0x0000000000007919 */
        |                                                                                                           /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                                 /* 0x00008600ff027b82 */
        |                                                                                                           /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                                            /* 0x0000820000067ab9 */
        |                                                                                                           /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                                           /* 0x00008400000c7ab9 */
        |                                                                                                           /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                                    /* 0x0000051000007945 */
        |                                                                                                           /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                                   /* 0x000000020000720c */
        |                                                                                                           /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                                         /* 0x0000000400380947 */
        |                                                                                                           /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                                           /* 0x000000ffff037224 */
        |                                                                                                           /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                                   /* 0x000000020300720c */
        |                                                                                                           /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                              /* 0x00008400ff042b82 */
        |                                                                                                           /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                              /* 0x0000000403042210 */
        |                                                                                                           /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                             /* 0x000000ffff052224 */
        |                                                                                                           /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                                         /* 0x0000000604042981 */
        |                                                                                                           /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                                         /* 0x0000040000062802 */
        |                                                                                                           /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                                    /* 0x0000027000017945 */
        |                                                                                                           /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                                /* 0x000000000000781c */
        |                                                                                                           /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                                   /* 0x0000000000072919 */
        |                                                                                                           /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                                  /* 0x0000000607062211 */
        |                                                                                                           /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                                       /* 0x0000000103072824 */
        |                                                                                                           /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                                    /* 0x0000008003032836 */
        |                                                                                                           /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                            /* 0x0000000102067824 */
        |                                                                                                           /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                                   /* 0x000000020300720c */
        |                                                                                                           /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                                 /* 0x000001800600780c */
        |                                                                                                           /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                                       /* 0x0000000407002388 */
        |                                                                                                           /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                                         /* 0x0000000000700947 */
        |                                                                                                           /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                                   /* 0x0000000000057919 */
        |                                                                                                           /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                                         /* 0x0000040000047802 */
        |                                                                                                           /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                             /* 0xfffffe80020c7836 */
        |                                                                                                           /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                                /* 0x000000000000781c */
        |                                                                                                           /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                                 /* 0x00000004050e7211 */
        |                                                                                                           /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                                      /* 0x0000000c03047c10 */
        |                                                                                                           /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                              /* 0x0000008003067836 */
        |                                                                                                           /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                             /* 0x0000010003087836 */
        |                                                                                                           /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                                  /* 0x00000180030a7836 */
        |                                                                                                           /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                                            /* 0x0000000c06067c10 */
        |                                                                                                           /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                                           /* 0x0000000dff057e24 */
        |                                                                                                           /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                                            /* 0x0000000c08087c10 */
        |                                                                                                           /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                                          /* 0x0000000c0a0a7c10 */
        |                                                                                                           /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                                           /* 0x0000000dff077e24 */
        |                                                                                                           /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                         /* 0x0000000604047981 */
        |                                                                                                           /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                                           /* 0x0000000dff097e24 */
        |                                                                                                           /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                                          /* 0x0000000dff0b7e24 */
        |                                                                                                           /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                         /* 0x0000000606067981 */
        |                                                                                                           /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                                         /* 0x0000000608087981 */
        |                                                                                                           /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                                       /* 0x000000060a0a7981 */
        |                                                                                                           /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                           /* 0x000000010e0d7824 */
        |                                                                                                           /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                                   /* 0x0000020003037836 */
        |                                                                                                           /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                                  /* 0x0000000c0300720c */
        |                                                                                                           /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                                      /* 0x000000040d007388 */
        |                                                                                                           /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                                 /* 0x000080060d007388 */
        |                                                                                                           /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                                /* 0x000100080d007388 */
        |                                                                                                           /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                               /* 0x0001800a0d007388 */
        |                                                                                                           /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                                         /* 0xfffffffc00a48947 */
        |                                                                                                           /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                              /* 0x0000000000017941 */
        |                                                                                                           /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                            /* 0x0000000102047824 */
        |                                                                                                           /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                                   /* 0x000000020300720c */
        |                                                                                                           /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                                    /* 0x0000012000017945 */
        |                                                                                                           /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                                  /* 0x000000800400780c */
        |                                                                                                           /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                                         /* 0x00000000003c0947 */
        |                                                                                                           /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                              /* 0x0000008003047836 */
        |                                                                                                           /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                            /* 0x0000840000047ab9 */
        |                                                                                                           /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                             /* 0x0000000403067c10 */
        |                                                                                                           /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                             /* 0x0000000404047c10 */
        |                                                                                                           /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                            /* 0x00000005ff077e24 */
        |                                                                                                           /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                            /* 0x00000005ff057e24 */
        |                                                                                                           /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                         /* 0x0000000606067981 */
        |                                                                                                           /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                         /* 0x0000000604047981 */
        |                                                                                                           /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                                 /* 0x00000000000579c3 */
        |                                                                                                           /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                                       /* 0x0000040000047882 */
        |                                                                                                           /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                                /* 0x000000000000781c */
        |                                                                                                           /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                              /* 0x0000000405047291 */
        |                                                                                                           /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                                   /* 0x0000000603007988 */
        |                                                                                                           /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                              /* 0x0000800403007988 */
        |                                                                                                           /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                                   /* 0x0000010003037836 */
        |                                                                                                           /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                              /* 0x0000000000017941 */
        |                                                                                                           /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                                   /* 0x000000020300720c */
        |                                                                                                           /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                                     /* 0x0000000000209947 */
        |                                                                                                           /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                            /* 0x0000840000047ab9 */
        |                                                                                                           /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                             /* 0x0000000403047c10 */
        |                                                                                                           /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                            /* 0x00000005ff057e24 */
        |                                                                                                           /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                         /* 0x0000000604047981 */
        |                                                                                                           /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                                 /* 0x00000000000579c3 */
        |                                                                                                           /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                                       /* 0x0000040000047882 */
        |                                                                                                           /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                              /* 0x0000000405047291 */
        |                                                                                                           /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                                   /* 0x0000000403007988 */
        |                                                                                                           /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                              /* 0x0000000000007941 */
        |                                                                                                           /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                              /* 0x00008c00ff027b82 */
        |                                                                                                           /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x34, R2 ;                                        /* 0x0000003400027825 */
        |                                                                                                           /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                           /* 0x0000000000007b1d */
        |                                                                                                           /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R32, desc[UR6][R2.64+0x30] ;                                      /* 0x0000300602207981 */
        |                                                                                                           /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R24, desc[UR6][R2.64+0x10] ;                                      /* 0x0000100602187981 */
        |                                                                                                           /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R25, desc[UR6][R2.64+0x14] ;                                      /* 0x0000140602197981 */
        |                                                                                                           /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R26, desc[UR6][R2.64+0x18] ;                                      /* 0x00001806021a7981 */
        |                                                                                                           /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R27, desc[UR6][R2.64+0x1c] ;                                      /* 0x00001c06021b7981 */
        |                                                                                                           /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R28, desc[UR6][R2.64+0x20] ;                                      /* 0x00002006021c7981 */
        |                                                                                                           /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R29, desc[UR6][R2.64+0x24] ;                                      /* 0x00002406021d7981 */
        |                                                                                                           /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R30, desc[UR6][R2.64+0x28] ;                                      /* 0x00002806021e7981 */
        |                                                                                                           /* 0x000f68000c1e1900 */
        |        /*0620*/                   LDG.E R31, desc[UR6][R2.64+0x2c] ;                                      /* 0x00002c06021f7981 */
        |                                                                                                           /* 0x000162000c1e1900 */
        |        /*0630*/                   S2UR UR5, SR_CgaCtaId ;                                                 /* 0x00000000000579c3 */
        |                                                                                                           /* 0x000e620000008800 */
        |        /*0640*/                   UMOV UR4, 0x400 ;                                                       /* 0x0000040000047882 */
        |                                                                                                           /* 0x000fe20000000000 */
        |        /*0650*/                   ULDC.64 UR8, c[0x0][0x220] ;                                            /* 0x0000880000087ab9 */
        |                                                                                                           /* 0x000fe20000000a00 */
        |        /*0660*/                   MEMBAR.ALL.GPU ;                                                        /* 0x0000000000007992 */
        |                                                                                                           /* 0x000fec000000a000 */
        |        /*0670*/                   FENCE.VIEW.ASYNC.S ;                                                    /* 0x00000000000073c6 */
        |                                                                                                           /* 0x000f620000000000 */
        |        /*0680*/                   ULDC.64 UR10, c[0x0][0x228] ;                                           /* 0x00008a00000a7ab9 */
        |                                                                                                           /* 0x000fe20000000a00 */
        |        /*0690*/                   LDC.64 R2, c[0x0][0x238] ;                                              /* 0x00008e00ff027b82 */
        |                                                                                                           /* 0x001e220000000a00 */
        |        /*06a0*/                   ULEA UR4, UR5, UR4, 0x18 ;                                              /* 0x0000000405047291 */
        |                                                                                                           /* 0x002fc6000f8ec03f */
        |        /*06b0*/                   ULDC UR5, c[0x0][0x240] ;                                               /* 0x0000900000057ab9 */
        |                                                                                                           /* 0x000fe20000000800 */
        |        /*06c0*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;                          /* 0x0003ffff04047892 */
        |                                                                                                           /* 0x000fe4000f8ec03f */
        |        /*06d0*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                             /* 0x0000003f0500728c */
        |                                                                                                           /* 0x000fe4000bf05070 */
        |        /*06e0*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                                         /* 0x0000000404047899 */
        |                                                                                                           /* 0x000fe2000800123f */
        |        /*06f0*/                   IMAD.WIDE.U32 R2, R0, 0x20, R2 ;                                        /* 0x0000002000027825 */
        |                                                                                                           /* 0x001fe200078e0002 */
        |        /*0700*/                   UMOV UR5, URZ ;                                                         /* 0x0000003f00057c82 */
        |                                                                                                           /* 0x000fe40008000000 */
        |        /*0710*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                                          /* 0x0000000804087297 */
        |                                                                                                           /* 0x000fe4000fffe03f */
        |        /*0720*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                                        /* 0x0000000a040a7297 */
        |                                                                                                           /* 0x000fe2000fffe03f */
        |        /*0730*/                   WARPGROUP.ARRIVE ;                                                      /* 0x00000000000079c5 */
        |                                                                                                           /* 0x020fd00000000000 */
        |        /*0740*/                   QGMMA.SP.64x16x64.F32.E4M3.E5M2 R24, gdesc[UR8], R24, UP0, R32, gsb0 ;  /* 0x04202000081879f3 */
        |                                                                                                           /* 0x000fe6000b802a18 */
        |        /*0750*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                                         /* 0x00008000000079c5 */
        |                                                                                                           /* 0x000fe40000010000 */
        |        /*0760*/                   STG.E desc[UR6][R2.64], R24 ;                                           /* 0x0000001802007986 */
        |                                                                                                           /* 0x000fe8000c101906 */
        |        /*0770*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                                       /* 0x0000041902007986 */
        |                                                                                                           /* 0x000fe8000c101906 */
        |        /*0780*/                   STG.E desc[UR6][R2.64+0x8], R26 ;                                       /* 0x0000081a02007986 */
        |                                                                                                           /* 0x000fe8000c101906 */
        |        /*0790*/                   STG.E desc[UR6][R2.64+0xc], R27 ;                                       /* 0x00000c1b02007986 */
        |                                                                                                           /* 0x000fe8000c101906 */
        |        /*07a0*/                   STG.E desc[UR6][R2.64+0x10], R28 ;                                      /* 0x0000101c02007986 */
        |                                                                                                           /* 0x000fe8000c101906 */
        |        /*07b0*/                   STG.E desc[UR6][R2.64+0x14], R29 ;                                      /* 0x0000141d02007986 */
        |                                                                                                           /* 0x000fe8000c101906 */
        |        /*07c0*/                   STG.E desc[UR6][R2.64+0x18], R30 ;                                      /* 0x0000181e02007986 */
        |                                                                                                           /* 0x000fe8000c101906 */
        |        /*07d0*/                   STG.E desc[UR6][R2.64+0x1c], R31 ;                                      /* 0x00001c1f02007986 */
        |                                                                                                           /* 0x000fe2000c101906 */
        |        /*07e0*/                   EXIT ;                                                                  /* 0x000000000000794d */
        |                                                                                                           /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma16Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                              /* 0x00000a00ff017b82 */
        |                                                                                                       /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                                  /* 0x0000000000007919 */
        |                                                                                                       /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                             /* 0x00008600ff027b82 */
        |                                                                                                       /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                                        /* 0x0000820000067ab9 */
        |                                                                                                       /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                                       /* 0x00008400000c7ab9 */
        |                                                                                                       /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                                /* 0x0000051000007945 */
        |                                                                                                       /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                               /* 0x000000020000720c */
        |                                                                                                       /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                                     /* 0x0000000400380947 */
        |                                                                                                       /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                                       /* 0x000000ffff037224 */
        |                                                                                                       /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                               /* 0x000000020300720c */
        |                                                                                                       /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                          /* 0x00008400ff042b82 */
        |                                                                                                       /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                          /* 0x0000000403042210 */
        |                                                                                                       /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                         /* 0x000000ffff052224 */
        |                                                                                                       /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                                     /* 0x0000000604042981 */
        |                                                                                                       /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                                     /* 0x0000040000062802 */
        |                                                                                                       /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                                /* 0x0000027000017945 */
        |                                                                                                       /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                            /* 0x000000000000781c */
        |                                                                                                       /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                               /* 0x0000000000072919 */
        |                                                                                                       /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                              /* 0x0000000607062211 */
        |                                                                                                       /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                                   /* 0x0000000103072824 */
        |                                                                                                       /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                                /* 0x0000008003032836 */
        |                                                                                                       /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                        /* 0x0000000102067824 */
        |                                                                                                       /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                               /* 0x000000020300720c */
        |                                                                                                       /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                             /* 0x000001800600780c */
        |                                                                                                       /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                                   /* 0x0000000407002388 */
        |                                                                                                       /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                                     /* 0x0000000000700947 */
        |                                                                                                       /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                               /* 0x0000000000057919 */
        |                                                                                                       /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                                     /* 0x0000040000047802 */
        |                                                                                                       /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                         /* 0xfffffe80020c7836 */
        |                                                                                                       /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                            /* 0x000000000000781c */
        |                                                                                                       /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                             /* 0x00000004050e7211 */
        |                                                                                                       /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                                  /* 0x0000000c03047c10 */
        |                                                                                                       /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                          /* 0x0000008003067836 */
        |                                                                                                       /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                         /* 0x0000010003087836 */
        |                                                                                                       /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                              /* 0x00000180030a7836 */
        |                                                                                                       /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                                        /* 0x0000000c06067c10 */
        |                                                                                                       /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                                       /* 0x0000000dff057e24 */
        |                                                                                                       /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                                        /* 0x0000000c08087c10 */
        |                                                                                                       /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                                      /* 0x0000000c0a0a7c10 */
        |                                                                                                       /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                                       /* 0x0000000dff077e24 */
        |                                                                                                       /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                     /* 0x0000000604047981 */
        |                                                                                                       /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                                       /* 0x0000000dff097e24 */
        |                                                                                                       /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                                      /* 0x0000000dff0b7e24 */
        |                                                                                                       /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                     /* 0x0000000606067981 */
        |                                                                                                       /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                                     /* 0x0000000608087981 */
        |                                                                                                       /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                                   /* 0x000000060a0a7981 */
        |                                                                                                       /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                       /* 0x000000010e0d7824 */
        |                                                                                                       /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                               /* 0x0000020003037836 */
        |                                                                                                       /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                              /* 0x0000000c0300720c */
        |                                                                                                       /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                                  /* 0x000000040d007388 */
        |                                                                                                       /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                             /* 0x000080060d007388 */
        |                                                                                                       /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                            /* 0x000100080d007388 */
        |                                                                                                       /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                           /* 0x0001800a0d007388 */
        |                                                                                                       /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                                     /* 0xfffffffc00a48947 */
        |                                                                                                       /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                          /* 0x0000000000017941 */
        |                                                                                                       /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                        /* 0x0000000102047824 */
        |                                                                                                       /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                               /* 0x000000020300720c */
        |                                                                                                       /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                                /* 0x0000012000017945 */
        |                                                                                                       /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                              /* 0x000000800400780c */
        |                                                                                                       /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                                     /* 0x00000000003c0947 */
        |                                                                                                       /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                          /* 0x0000008003047836 */
        |                                                                                                       /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                        /* 0x0000840000047ab9 */
        |                                                                                                       /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                         /* 0x0000000403067c10 */
        |                                                                                                       /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                         /* 0x0000000404047c10 */
        |                                                                                                       /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                        /* 0x00000005ff077e24 */
        |                                                                                                       /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                        /* 0x00000005ff057e24 */
        |                                                                                                       /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                                     /* 0x0000000606067981 */
        |                                                                                                       /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                     /* 0x0000000604047981 */
        |                                                                                                       /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                             /* 0x00000000000579c3 */
        |                                                                                                       /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                                   /* 0x0000040000047882 */
        |                                                                                                       /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                            /* 0x000000000000781c */
        |                                                                                                       /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                          /* 0x0000000405047291 */
        |                                                                                                       /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                               /* 0x0000000603007988 */
        |                                                                                                       /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                          /* 0x0000800403007988 */
        |                                                                                                       /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                               /* 0x0000010003037836 */
        |                                                                                                       /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                          /* 0x0000000000017941 */
        |                                                                                                       /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                               /* 0x000000020300720c */
        |                                                                                                       /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                                 /* 0x0000000000209947 */
        |                                                                                                       /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                        /* 0x0000840000047ab9 */
        |                                                                                                       /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                         /* 0x0000000403047c10 */
        |                                                                                                       /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                        /* 0x00000005ff057e24 */
        |                                                                                                       /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                                     /* 0x0000000604047981 */
        |                                                                                                       /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                             /* 0x00000000000579c3 */
        |                                                                                                       /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                                   /* 0x0000040000047882 */
        |                                                                                                       /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                          /* 0x0000000405047291 */
        |                                                                                                       /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                               /* 0x0000000403007988 */
        |                                                                                                       /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                          /* 0x0000000000007941 */
        |                                                                                                       /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                          /* 0x00008c00ff027b82 */
        |                                                                                                       /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x34, R2 ;                                    /* 0x0000003400027825 */
        |                                                                                                       /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                       /* 0x0000000000007b1d */
        |                                                                                                       /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R32, desc[UR6][R2.64+0x30] ;                                  /* 0x0000300602207981 */
        |                                                                                                       /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R24, desc[UR6][R2.64+0x10] ;                                  /* 0x0000100602187981 */
        |                                                                                                       /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R25, desc[UR6][R2.64+0x14] ;                                  /* 0x0000140602197981 */
        |                                                                                                       /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R26, desc[UR6][R2.64+0x18] ;                                  /* 0x00001806021a7981 */
        |                                                                                                       /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R27, desc[UR6][R2.64+0x1c] ;                                  /* 0x00001c06021b7981 */
        |                                                                                                       /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R28, desc[UR6][R2.64+0x20] ;                                  /* 0x00002006021c7981 */
        |                                                                                                       /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R29, desc[UR6][R2.64+0x24] ;                                  /* 0x00002406021d7981 */
        |                                                                                                       /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R30, desc[UR6][R2.64+0x28] ;                                  /* 0x00002806021e7981 */
        |                                                                                                       /* 0x000f68000c1e1900 */
        |        /*0620*/                   LDG.E R31, desc[UR6][R2.64+0x2c] ;                                  /* 0x00002c06021f7981 */
        |                                                                                                       /* 0x000162000c1e1900 */
        |        /*0630*/                   S2UR UR5, SR_CgaCtaId ;                                             /* 0x00000000000579c3 */
        |                                                                                                       /* 0x000e620000008800 */
        |        /*0640*/                   UMOV UR4, 0x400 ;                                                   /* 0x0000040000047882 */
        |                                                                                                       /* 0x000fe20000000000 */
        |        /*0650*/                   ULDC.64 UR8, c[0x0][0x220] ;                                        /* 0x0000880000087ab9 */
        |                                                                                                       /* 0x000fe20000000a00 */
        |        /*0660*/                   MEMBAR.ALL.GPU ;                                                    /* 0x0000000000007992 */
        |                                                                                                       /* 0x000fec000000a000 */
        |        /*0670*/                   FENCE.VIEW.ASYNC.S ;                                                /* 0x00000000000073c6 */
        |                                                                                                       /* 0x000f620000000000 */
        |        /*0680*/                   ULDC.64 UR10, c[0x0][0x228] ;                                       /* 0x00008a00000a7ab9 */
        |                                                                                                       /* 0x000fe20000000a00 */
        |        /*0690*/                   LDC.64 R2, c[0x0][0x238] ;                                          /* 0x00008e00ff027b82 */
        |                                                                                                       /* 0x001e220000000a00 */
        |        /*06a0*/                   ULEA UR4, UR5, UR4, 0x18 ;                                          /* 0x0000000405047291 */
        |                                                                                                       /* 0x002fc6000f8ec03f */
        |        /*06b0*/                   ULDC UR5, c[0x0][0x240] ;                                           /* 0x0000900000057ab9 */
        |                                                                                                       /* 0x000fe20000000800 */
        |        /*06c0*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;                      /* 0x0003ffff04047892 */
        |                                                                                                       /* 0x000fe4000f8ec03f */
        |        /*06d0*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                         /* 0x0000003f0500728c */
        |                                                                                                       /* 0x000fe4000bf05070 */
        |        /*06e0*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                                     /* 0x0000000404047899 */
        |                                                                                                       /* 0x000fe2000800123f */
        |        /*06f0*/                   IMAD.WIDE.U32 R2, R0, 0x20, R2 ;                                    /* 0x0000002000027825 */
        |                                                                                                       /* 0x001fe200078e0002 */
        |        /*0700*/                   UMOV UR5, URZ ;                                                     /* 0x0000003f00057c82 */
        |                                                                                                       /* 0x000fe40008000000 */
        |        /*0710*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                                      /* 0x0000000804087297 */
        |                                                                                                       /* 0x000fe4000fffe03f */
        |        /*0720*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                                    /* 0x0000000a040a7297 */
        |                                                                                                       /* 0x000fe2000fffe03f */
        |        /*0730*/                   WARPGROUP.ARRIVE ;                                                  /* 0x00000000000079c5 */
        |                                                                                                       /* 0x020fd00000000000 */
        |        /*0740*/                   IGMMA.SP.64x16x64.U8.S8.SAT R24, gdesc[UR8], R24, UP0, R32, gsb0 ;  /* 0x00802000081879f1 */
        |                                                                                                       /* 0x000fe6000b840a18 */
        |        /*0750*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                                     /* 0x00008000000079c5 */
        |                                                                                                       /* 0x000fe40000010000 */
        |        /*0760*/                   STG.E desc[UR6][R2.64], R24 ;                                       /* 0x0000001802007986 */
        |                                                                                                       /* 0x000fe8000c101906 */
        |        /*0770*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                                   /* 0x0000041902007986 */
        |                                                                                                       /* 0x000fe8000c101906 */
        |        /*0780*/                   STG.E desc[UR6][R2.64+0x8], R26 ;                                   /* 0x0000081a02007986 */
        |                                                                                                       /* 0x000fe8000c101906 */
        |        /*0790*/                   STG.E desc[UR6][R2.64+0xc], R27 ;                                   /* 0x00000c1b02007986 */
        |                                                                                                       /* 0x000fe8000c101906 */
        |        /*07a0*/                   STG.E desc[UR6][R2.64+0x10], R28 ;                                  /* 0x0000101c02007986 */
        |                                                                                                       /* 0x000fe8000c101906 */
        |        /*07b0*/                   STG.E desc[UR6][R2.64+0x14], R29 ;                                  /* 0x0000141d02007986 */
        |                                                                                                       /* 0x000fe8000c101906 */
        |        /*07c0*/                   STG.E desc[UR6][R2.64+0x18], R30 ;                                  /* 0x0000181e02007986 */
        |                                                                                                       /* 0x000fe8000c101906 */
        |        /*07d0*/                   STG.E desc[UR6][R2.64+0x1c], R31 ;                                  /* 0x00001c1f02007986 */
        |                                                                                                       /* 0x000fe2000c101906 */
        |        /*07e0*/                   EXIT ;                                                              /* 0x000000000000794d */
        |                                                                                                       /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wgmma17Sm90a" ->
      """//--------------------- .text.wgmma               --------------------------
        |	.section	.text.wgmma,"ax",@progbits
        |	.align	128
        |        .global         wgmma
        |        .type           wgmma,@function
        |        .size           wgmma,(.L_x_8 - wgmma)
        |        .other          wgmma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wgmma:
        |.text.wgmma:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;                                       /* 0x00000a00ff017b82 */
        |                                                                                                /* 0x000fe20000000800 */
        |        /*0010*/                   S2R R0, SR_TID.X ;                                           /* 0x0000000000007919 */
        |                                                                                                /* 0x000e2e0000002100 */
        |        /*0020*/                   LDC R2, c[0x0][0x218] ;                                      /* 0x00008600ff027b82 */
        |                                                                                                /* 0x000e220000000800 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;                                 /* 0x0000820000067ab9 */
        |                                                                                                /* 0x000fe20000000a00 */
        |        /*0040*/                   ULDC.64 UR12, c[0x0][0x210] ;                                /* 0x00008400000c7ab9 */
        |                                                                                                /* 0x000fe20000000a00 */
        |        /*0050*/                   BSSY B0, `(.L_x_0) ;                                         /* 0x0000051000007945 */
        |                                                                                                /* 0x000fe20003800000 */
        |        /*0060*/                   ISETP.GE.U32.AND P0, PT, R0, R2, PT ;                        /* 0x000000020000720c */
        |                                                                                                /* 0x001fda0003f06070 */
        |        /*0070*/               @P0 BRA `(.L_x_1) ;                                              /* 0x0000000400380947 */
        |                                                                                                /* 0x000fea0003800000 */
        |        /*0080*/                   IMAD.MOV.U32 R3, RZ, RZ, R0 ;                                /* 0x000000ffff037224 */
        |                                                                                                /* 0x000fca00078e0000 */
        |        /*0090*/                   ISETP.GE.U32.AND P2, PT, R3, R2, PT ;                        /* 0x000000020300720c */
        |                                                                                                /* 0x000fda0003f46070 */
        |        /*00a0*/               @P2 LDC.64 R4, c[0x0][0x210] ;                                   /* 0x00008400ff042b82 */
        |                                                                                                /* 0x000e240000000a00 */
        |        /*00b0*/               @P2 IADD3 R4, P0, R3, R4, RZ ;                                   /* 0x0000000403042210 */
        |                                                                                                /* 0x001fca0007f1e0ff */
        |        /*00c0*/               @P2 IMAD.X R5, RZ, RZ, R5, P0 ;                                  /* 0x000000ffff052224 */
        |                                                                                                /* 0x000fca00000e0605 */
        |        /*00d0*/               @P2 LDG.E.U8 R4, desc[UR6][R4.64] ;                              /* 0x0000000604042981 */
        |                                                                                                /* 0x000ea2000c1e1100 */
        |        /*00e0*/               @P2 MOV R6, 0x400 ;                                              /* 0x0000040000062802 */
        |                                                                                                /* 0x000fe20000000f00 */
        |        /*00f0*/                   BSSY B1, `(.L_x_2) ;                                         /* 0x0000027000017945 */
        |                                                                                                /* 0x000fe20003800000 */
        |        /*0100*/                   PLOP3.LUT P1, PT, P2, PT, PT, 0x8, 0x0 ;                     /* 0x000000000000781c */
        |                                                                                                /* 0x000fe2000172e170 */
        |        /*0110*/               @P2 S2R R7, SR_CgaCtaId ;                                        /* 0x0000000000072919 */
        |                                                                                                /* 0x000e240000008800 */
        |        /*0120*/               @P2 LEA R6, R7, R6, 0x18 ;                                       /* 0x0000000607062211 */
        |                                                                                                /* 0x001fca00078ec0ff */
        |        /*0130*/               @P2 IMAD.IADD R7, R3.reuse, 0x1, R6 ;                            /* 0x0000000103072824 */
        |                                                                                                /* 0x040fe400078e0206 */
        |        /*0140*/               @P2 VIADD R3, R3, 0x80 ;                                         /* 0x0000008003032836 */
        |                                                                                                /* 0x000fc80000000000 */
        |        /*0150*/                   IMAD.IADD R6, R2, 0x1, -R3 ;                                 /* 0x0000000102067824 */
        |                                                                                                /* 0x000fe200078e0a03 */
        |        /*0160*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                        /* 0x000000020300720c */
        |                                                                                                /* 0x000fc80003f06070 */
        |        /*0170*/                   ISETP.LE.U32.OR P0, PT, R6, 0x180, P0 ;                      /* 0x000001800600780c */
        |                                                                                                /* 0x000fe20000703470 */
        |        /*0180*/               @P2 STS.U8 [R7], R4 ;                                            /* 0x0000000407002388 */
        |                                                                                                /* 0x0041d80000000000 */
        |        /*0190*/               @P0 BRA `(.L_x_3) ;                                              /* 0x0000000000700947 */
        |                                                                                                /* 0x000fea0003800000 */
        |        /*01a0*/                   S2R R5, SR_CgaCtaId ;                                        /* 0x0000000000057919 */
        |                                                                                                /* 0x000e620000008800 */
        |        /*01b0*/                   MOV R4, 0x400 ;                                              /* 0x0000040000047802 */
        |                                                                                                /* 0x001fe20000000f00 */
        |        /*01c0*/                   VIADD R12, R2, 0xfffffe80 ;                                  /* 0xfffffe80020c7836 */
        |                                                                                                /* 0x000fe20000000000 */
        |        /*01d0*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                     /* 0x000000000000781c */
        |                                                                                                /* 0x000fe40003f2e170 */
        |        /*01e0*/                   LEA R14, R5, R4, 0x18 ;                                      /* 0x00000004050e7211 */
        |                                                                                                /* 0x002fd600078ec0ff */
        |.L_x_4:
        |        /*01f0*/                   IADD3 R4, P2, R3.reuse, UR12, RZ ;                           /* 0x0000000c03047c10 */
        |                                                                                                /* 0x042fe2000ff5e0ff */
        |        /*0200*/                   VIADD R6, R3.reuse, 0x80 ;                                   /* 0x0000008003067836 */
        |                                                                                                /* 0x044fe40000000000 */
        |        /*0210*/                   VIADD R8, R3.reuse, 0x100 ;                                  /* 0x0000010003087836 */
        |                                                                                                /* 0x048fe40000000000 */
        |        /*0220*/                   VIADD R10, R3, 0x180 ;                                       /* 0x00000180030a7836 */
        |                                                                                                /* 0x010fe20000000000 */
        |        /*0230*/                   IADD3 R6, P0, R6, UR12, RZ ;                                 /* 0x0000000c06067c10 */
        |                                                                                                /* 0x000fe2000ff1e0ff */
        |        /*0240*/                   IMAD.X R5, RZ, RZ, UR13, P2 ;                                /* 0x0000000dff057e24 */
        |                                                                                                /* 0x000fe200090e06ff */
        |        /*0250*/                   IADD3 R8, P2, R8, UR12, RZ ;                                 /* 0x0000000c08087c10 */
        |                                                                                                /* 0x000fe4000ff5e0ff */
        |        /*0260*/                   IADD3 R10, P3, R10, UR12, RZ ;                               /* 0x0000000c0a0a7c10 */
        |                                                                                                /* 0x000fe2000ff7e0ff */
        |        /*0270*/                   IMAD.X R7, RZ, RZ, UR13, P0 ;                                /* 0x0000000dff077e24 */
        |                                                                                                /* 0x000fe200080e06ff */
        |        /*0280*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                              /* 0x0000000604047981 */
        |                                                                                                /* 0x000ea2000c1e1100 */
        |        /*0290*/                   IMAD.X R9, RZ, RZ, UR13, P2 ;                                /* 0x0000000dff097e24 */
        |                                                                                                /* 0x000fc400090e06ff */
        |        /*02a0*/                   IMAD.X R11, RZ, RZ, UR13, P3 ;                               /* 0x0000000dff0b7e24 */
        |                                                                                                /* 0x000fe200098e06ff */
        |        /*02b0*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                              /* 0x0000000606067981 */
        |                                                                                                /* 0x000ee8000c1e1100 */
        |        /*02c0*/                   LDG.E.U8 R8, desc[UR6][R8.64] ;                              /* 0x0000000608087981 */
        |                                                                                                /* 0x000f28000c1e1100 */
        |        /*02d0*/                   LDG.E.U8 R10, desc[UR6][R10.64] ;                            /* 0x000000060a0a7981 */
        |                                                                                                /* 0x000f62000c1e1100 */
        |        /*02e0*/                   IMAD.IADD R13, R14, 0x1, R3 ;                                /* 0x000000010e0d7824 */
        |                                                                                                /* 0x000fc400078e0203 */
        |        /*02f0*/                   VIADD R3, R3, 0x200 ;                                        /* 0x0000020003037836 */
        |                                                                                                /* 0x000fca0000000000 */
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R3, R12, PT ;                       /* 0x0000000c0300720c */
        |                                                                                                /* 0x000fe20003f06070 */
        |        /*0310*/                   STS.U8 [R13], R4 ;                                           /* 0x000000040d007388 */
        |                                                                                                /* 0x0043e80000000000 */
        |        /*0320*/                   STS.U8 [R13+0x80], R6 ;                                      /* 0x000080060d007388 */
        |                                                                                                /* 0x0085e80000000000 */
        |        /*0330*/                   STS.U8 [R13+0x100], R8 ;                                     /* 0x000100080d007388 */
        |                                                                                                /* 0x0107e80000000000 */
        |        /*0340*/                   STS.U8 [R13+0x180], R10 ;                                    /* 0x0001800a0d007388 */
        |                                                                                                /* 0x0209e20000000000 */
        |        /*0350*/              @!P0 BRA `(.L_x_4) ;                                              /* 0xfffffffc00a48947 */
        |                                                                                                /* 0x000fea000383ffff */
        |.L_x_3:
        |        /*0360*/                   BSYNC B1 ;                                                   /* 0x0000000000017941 */
        |                                                                                                /* 0x000fea0003800000 */
        |.L_x_2:
        |        /*0370*/                   IMAD.IADD R4, R2, 0x1, -R3 ;                                 /* 0x0000000102047824 */
        |                                                                                                /* 0x003fe200078e0a03 */
        |        /*0380*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                        /* 0x000000020300720c */
        |                                                                                                /* 0x000fe20003f06070 */
        |        /*0390*/                   BSSY B1, `(.L_x_5) ;                                         /* 0x0000012000017945 */
        |                                                                                                /* 0x000fe60003800000 */
        |        /*03a0*/                   ISETP.LE.U32.OR P0, PT, R4, 0x80, P0 ;                       /* 0x000000800400780c */
        |                                                                                                /* 0x000fda0000703470 */
        |        /*03b0*/               @P0 BRA `(.L_x_6) ;                                              /* 0x00000000003c0947 */
        |                                                                                                /* 0x000fea0003800000 */
        |        /*03c0*/                   VIADD R4, R3.reuse, 0x80 ;                                   /* 0x0000008003047836 */
        |                                                                                                /* 0x040fe20000000000 */
        |        /*03d0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                 /* 0x0000840000047ab9 */
        |                                                                                                /* 0x000fe40000000a00 */
        |        /*03e0*/                   IADD3 R6, P1, R3, UR4, RZ ;                                  /* 0x0000000403067c10 */
        |                                                                                                /* 0x004fe4000ff3e0ff */
        |        /*03f0*/                   IADD3 R4, P0, R4, UR4, RZ ;                                  /* 0x0000000404047c10 */
        |                                                                                                /* 0x000fc6000ff1e0ff */
        |        /*0400*/                   IMAD.X R7, RZ, RZ, UR5, P1 ;                                 /* 0x00000005ff077e24 */
        |                                                                                                /* 0x000fe400088e06ff */
        |        /*0410*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                 /* 0x00000005ff057e24 */
        |                                                                                                /* 0x000fc600080e06ff */
        |        /*0420*/                   LDG.E.U8 R6, desc[UR6][R6.64] ;                              /* 0x0000000606067981 */
        |                                                                                                /* 0x000ea8000c1e1100 */
        |        /*0430*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                              /* 0x0000000604047981 */
        |                                                                                                /* 0x000f62000c1e1100 */
        |        /*0440*/                   S2UR UR5, SR_CgaCtaId ;                                      /* 0x00000000000579c3 */
        |                                                                                                /* 0x000e220000008800 */
        |        /*0450*/                   UMOV UR4, 0x400 ;                                            /* 0x0000040000047882 */
        |                                                                                                /* 0x000fe20000000000 */
        |        /*0460*/                   PLOP3.LUT P1, PT, PT, PT, PT, 0x8, 0x0 ;                     /* 0x000000000000781c */
        |                                                                                                /* 0x000fe20003f2e170 */
        |        /*0470*/                   ULEA UR4, UR5, UR4, 0x18 ;                                   /* 0x0000000405047291 */
        |                                                                                                /* 0x001fd2000f8ec03f */
        |        /*0480*/                   STS.U8 [R3+UR4], R6 ;                                        /* 0x0000000603007988 */
        |                                                                                                /* 0x004fe80008000004 */
        |        /*0490*/                   STS.U8 [R3+UR4+0x80], R4 ;                                   /* 0x0000800403007988 */
        |                                                                                                /* 0x0201e40008000004 */
        |        /*04a0*/                   VIADD R3, R3, 0x100 ;                                        /* 0x0000010003037836 */
        |                                                                                                /* 0x001fce0000000000 */
        |.L_x_6:
        |        /*04b0*/                   BSYNC B1 ;                                                   /* 0x0000000000017941 */
        |                                                                                                /* 0x000fea0003800000 */
        |.L_x_5:
        |        /*04c0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;                        /* 0x000000020300720c */
        |                                                                                                /* 0x000fda0003f06070 */
        |        /*04d0*/              @!P1 BRA P0, `(.L_x_1) ;                                          /* 0x0000000000209947 */
        |                                                                                                /* 0x000fea0000000000 */
        |        /*04e0*/                   ULDC.64 UR4, c[0x0][0x210] ;                                 /* 0x0000840000047ab9 */
        |                                                                                                /* 0x000fe40000000a00 */
        |        /*04f0*/                   IADD3 R4, P0, R3, UR4, RZ ;                                  /* 0x0000000403047c10 */
        |                                                                                                /* 0x000fca000ff1e0ff */
        |        /*0500*/                   IMAD.X R5, RZ, RZ, UR5, P0 ;                                 /* 0x00000005ff057e24 */
        |                                                                                                /* 0x000fca00080e06ff */
        |        /*0510*/                   LDG.E.U8 R4, desc[UR6][R4.64] ;                              /* 0x0000000604047981 */
        |                                                                                                /* 0x000f62000c1e1100 */
        |        /*0520*/                   S2UR UR5, SR_CgaCtaId ;                                      /* 0x00000000000579c3 */
        |                                                                                                /* 0x000e220000008800 */
        |        /*0530*/                   UMOV UR4, 0x400 ;                                            /* 0x0000040000047882 */
        |                                                                                                /* 0x000fe40000000000 */
        |        /*0540*/                   ULEA UR4, UR5, UR4, 0x18 ;                                   /* 0x0000000405047291 */
        |                                                                                                /* 0x001fd2000f8ec03f */
        |        /*0550*/                   STS.U8 [R3+UR4], R4 ;                                        /* 0x0000000403007988 */
        |                                                                                                /* 0x0201e60008000004 */
        |.L_x_1:
        |        /*0560*/                   BSYNC B0 ;                                                   /* 0x0000000000007941 */
        |                                                                                                /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*0570*/                   LDC.64 R2, c[0x0][0x230] ;                                   /* 0x00008c00ff027b82 */
        |                                                                                                /* 0x001e240000000a00 */
        |        /*0580*/                   IMAD.WIDE.U32 R2, R0, 0x94, R2 ;                             /* 0x0000009400027825 */
        |                                                                                                /* 0x001fcc00078e0002 */
        |        /*0590*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                                /* 0x0000000000007b1d */
        |                                                                                                /* 0x000fec0000010000 */
        |        /*05a0*/                   LDG.E R24, desc[UR6][R2.64+0x10] ;                           /* 0x0000100602187981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*05b0*/                   LDG.E R25, desc[UR6][R2.64+0x14] ;                           /* 0x0000140602197981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*05c0*/                   LDG.E R26, desc[UR6][R2.64+0x18] ;                           /* 0x00001806021a7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*05d0*/                   LDG.E R27, desc[UR6][R2.64+0x1c] ;                           /* 0x00001c06021b7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*05e0*/                   LDG.E R28, desc[UR6][R2.64+0x20] ;                           /* 0x00002006021c7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*05f0*/                   LDG.E R29, desc[UR6][R2.64+0x24] ;                           /* 0x00002406021d7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0600*/                   LDG.E R30, desc[UR6][R2.64+0x28] ;                           /* 0x00002806021e7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0610*/                   LDG.E R31, desc[UR6][R2.64+0x2c] ;                           /* 0x00002c06021f7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0620*/                   LDG.E R32, desc[UR6][R2.64+0x30] ;                           /* 0x0000300602207981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0630*/                   LDG.E R33, desc[UR6][R2.64+0x34] ;                           /* 0x0000340602217981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0640*/                   LDG.E R34, desc[UR6][R2.64+0x38] ;                           /* 0x0000380602227981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0650*/                   LDG.E R35, desc[UR6][R2.64+0x3c] ;                           /* 0x00003c0602237981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0660*/                   LDG.E R36, desc[UR6][R2.64+0x40] ;                           /* 0x0000400602247981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0670*/                   LDG.E R37, desc[UR6][R2.64+0x44] ;                           /* 0x0000440602257981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0680*/                   LDG.E R38, desc[UR6][R2.64+0x48] ;                           /* 0x0000480602267981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0690*/                   LDG.E R39, desc[UR6][R2.64+0x4c] ;                           /* 0x00004c0602277981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06a0*/                   LDG.E R40, desc[UR6][R2.64+0x50] ;                           /* 0x0000500602287981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06b0*/                   LDG.E R41, desc[UR6][R2.64+0x54] ;                           /* 0x0000540602297981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06c0*/                   LDG.E R42, desc[UR6][R2.64+0x58] ;                           /* 0x00005806022a7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06d0*/                   LDG.E R43, desc[UR6][R2.64+0x5c] ;                           /* 0x00005c06022b7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06e0*/                   LDG.E R44, desc[UR6][R2.64+0x60] ;                           /* 0x00006006022c7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*06f0*/                   LDG.E R45, desc[UR6][R2.64+0x64] ;                           /* 0x00006406022d7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0700*/                   LDG.E R46, desc[UR6][R2.64+0x68] ;                           /* 0x00006806022e7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0710*/                   LDG.E R47, desc[UR6][R2.64+0x6c] ;                           /* 0x00006c06022f7981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0720*/                   LDG.E R48, desc[UR6][R2.64+0x70] ;                           /* 0x0000700602307981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0730*/                   LDG.E R49, desc[UR6][R2.64+0x74] ;                           /* 0x0000740602317981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0740*/                   LDG.E R50, desc[UR6][R2.64+0x78] ;                           /* 0x0000780602327981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0750*/                   LDG.E R51, desc[UR6][R2.64+0x7c] ;                           /* 0x00007c0602337981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0760*/                   LDG.E R52, desc[UR6][R2.64+0x80] ;                           /* 0x0000800602347981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0770*/                   LDG.E R53, desc[UR6][R2.64+0x84] ;                           /* 0x0000840602357981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0780*/                   LDG.E R54, desc[UR6][R2.64+0x88] ;                           /* 0x0000880602367981 */
        |                                                                                                /* 0x000f68000c1e1900 */
        |        /*0790*/                   LDG.E R55, desc[UR6][R2.64+0x8c] ;                           /* 0x00008c0602377981 */
        |                                                                                                /* 0x000162000c1e1900 */
        |        /*07a0*/                   S2UR UR5, SR_CgaCtaId ;                                      /* 0x00000000000579c3 */
        |                                                                                                /* 0x000e620000008800 */
        |        /*07b0*/                   UMOV UR4, 0x400 ;                                            /* 0x0000040000047882 */
        |                                                                                                /* 0x000fe20000000000 */
        |        /*07c0*/                   ULDC.64 UR8, c[0x0][0x220] ;                                 /* 0x0000880000087ab9 */
        |                                                                                                /* 0x000fe20000000a00 */
        |        /*07d0*/                   MEMBAR.ALL.GPU ;                                             /* 0x0000000000007992 */
        |                                                                                                /* 0x000fec000000a000 */
        |        /*07e0*/                   FENCE.VIEW.ASYNC.S ;                                         /* 0x00000000000073c6 */
        |                                                                                                /* 0x000f620000000000 */
        |        /*07f0*/                   ULDC.64 UR10, c[0x0][0x228] ;                                /* 0x00008a00000a7ab9 */
        |                                                                                                /* 0x000fe20000000a00 */
        |        /*0800*/                   LDC.64 R2, c[0x0][0x238] ;                                   /* 0x00008e00ff027b82 */
        |                                                                                                /* 0x001e220000000a00 */
        |        /*0810*/                   ULEA UR4, UR5, UR4, 0x18 ;                                   /* 0x0000000405047291 */
        |                                                                                                /* 0x002fc6000f8ec03f */
        |        /*0820*/                   ULDC UR5, c[0x0][0x240] ;                                    /* 0x0000900000057ab9 */
        |                                                                                                /* 0x000fe20000000800 */
        |        /*0830*/                   ULOP3.LUT UR4, UR4, 0x3ffff, URZ, 0xc0, !UPT ;               /* 0x0003ffff04047892 */
        |                                                                                                /* 0x000fe4000f8ec03f */
        |        /*0840*/                   UISETP.NE.U32.AND UP0, UPT, UR5, URZ, UPT ;                  /* 0x0000003f0500728c */
        |                                                                                                /* 0x000fe4000bf05070 */
        |        /*0850*/                   USHF.R.U64 UR4, UR4, 0x4, URZ ;                              /* 0x0000000404047899 */
        |                                                                                                /* 0x000fe2000800123f */
        |        /*0860*/                   IMAD.WIDE.U32 R2, R0, 0x80, R2 ;                             /* 0x0000008000027825 */
        |                                                                                                /* 0x001fe200078e0002 */
        |        /*0870*/                   UMOV UR5, URZ ;                                              /* 0x0000003f00057c82 */
        |                                                                                                /* 0x000fe40008000000 */
        |        /*0880*/                   UIADD3.64 UR8, UR4, UR8, URZ ;                               /* 0x0000000804087297 */
        |                                                                                                /* 0x000fe4000fffe03f */
        |        /*0890*/                   UIADD3.64 UR10, UR4, UR10, URZ ;                             /* 0x0000000a040a7297 */
        |                                                                                                /* 0x000fe2000fffe03f */
        |        /*08a0*/                   WARPGROUP.ARRIVE ;                                           /* 0x00000000000079c5 */
        |                                                                                                /* 0x020fd00000000000 */
        |        /*08b0*/                   HGMMA.64x128x16.F16 R24, gdesc[UR8].tnspA, R24, UP0, gsb0 ;  /* 0x21e00000081879f0 */
        |                                                                                                /* 0x000fe6000b800018 */
        |        /*08c0*/                   WARPGROUP.DEPBAR.LE gsb0, 0x0 ;                              /* 0x00008000000079c5 */
        |                                                                                                /* 0x000fe40000010000 */
        |        /*08d0*/                   STG.E desc[UR6][R2.64], R24 ;                                /* 0x0000001802007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*08e0*/                   STG.E desc[UR6][R2.64+0x4], R25 ;                            /* 0x0000041902007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*08f0*/                   STG.E desc[UR6][R2.64+0x8], R26 ;                            /* 0x0000081a02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0900*/                   STG.E desc[UR6][R2.64+0xc], R27 ;                            /* 0x00000c1b02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0910*/                   STG.E desc[UR6][R2.64+0x10], R28 ;                           /* 0x0000101c02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0920*/                   STG.E desc[UR6][R2.64+0x14], R29 ;                           /* 0x0000141d02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0930*/                   STG.E desc[UR6][R2.64+0x18], R30 ;                           /* 0x0000181e02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0940*/                   STG.E desc[UR6][R2.64+0x1c], R31 ;                           /* 0x00001c1f02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0950*/                   STG.E desc[UR6][R2.64+0x20], R32 ;                           /* 0x0000202002007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0960*/                   STG.E desc[UR6][R2.64+0x24], R33 ;                           /* 0x0000242102007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0970*/                   STG.E desc[UR6][R2.64+0x28], R34 ;                           /* 0x0000282202007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0980*/                   STG.E desc[UR6][R2.64+0x2c], R35 ;                           /* 0x00002c2302007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0990*/                   STG.E desc[UR6][R2.64+0x30], R36 ;                           /* 0x0000302402007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*09a0*/                   STG.E desc[UR6][R2.64+0x34], R37 ;                           /* 0x0000342502007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*09b0*/                   STG.E desc[UR6][R2.64+0x38], R38 ;                           /* 0x0000382602007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*09c0*/                   STG.E desc[UR6][R2.64+0x3c], R39 ;                           /* 0x00003c2702007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*09d0*/                   STG.E desc[UR6][R2.64+0x40], R40 ;                           /* 0x0000402802007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*09e0*/                   STG.E desc[UR6][R2.64+0x44], R41 ;                           /* 0x0000442902007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*09f0*/                   STG.E desc[UR6][R2.64+0x48], R42 ;                           /* 0x0000482a02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0a00*/                   STG.E desc[UR6][R2.64+0x4c], R43 ;                           /* 0x00004c2b02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0a10*/                   STG.E desc[UR6][R2.64+0x50], R44 ;                           /* 0x0000502c02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0a20*/                   STG.E desc[UR6][R2.64+0x54], R45 ;                           /* 0x0000542d02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0a30*/                   STG.E desc[UR6][R2.64+0x58], R46 ;                           /* 0x0000582e02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0a40*/                   STG.E desc[UR6][R2.64+0x5c], R47 ;                           /* 0x00005c2f02007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0a50*/                   STG.E desc[UR6][R2.64+0x60], R48 ;                           /* 0x0000603002007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0a60*/                   STG.E desc[UR6][R2.64+0x64], R49 ;                           /* 0x0000643102007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0a70*/                   STG.E desc[UR6][R2.64+0x68], R50 ;                           /* 0x0000683202007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0a80*/                   STG.E desc[UR6][R2.64+0x6c], R51 ;                           /* 0x00006c3302007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0a90*/                   STG.E desc[UR6][R2.64+0x70], R52 ;                           /* 0x0000703402007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0aa0*/                   STG.E desc[UR6][R2.64+0x74], R53 ;                           /* 0x0000743502007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0ab0*/                   STG.E desc[UR6][R2.64+0x78], R54 ;                           /* 0x0000783602007986 */
        |                                                                                                /* 0x000fe8000c101906 */
        |        /*0ac0*/                   STG.E desc[UR6][R2.64+0x7c], R55 ;                           /* 0x00007c3702007986 */
        |                                                                                                /* 0x000fe2000c101906 */
        |        /*0ad0*/                   EXIT ;                                                       /* 0x000000000000794d */
        |                                                                                                /* 0x000fea0003800000 */
        |.L_x_8:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin
  )
}
