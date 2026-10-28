// Generated SASS typed logical instruction forms from pinned NVIDIA descriptions. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import javax.annotation.processing.Generated

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

@Generated(Array("sbt codegen"))
object Instruction {
  object Modifier {
    private lazy val known: Map[String, Modifier] = Vec[Modifier](
      `0000`,
      `0FF`,
      `1`,
      `1032`,
      `10b`,
      `1111`,
      `128`,
      `128dp128bit`,
      `128dp256bit`,
      `16`,
      `168128`,
      `16816`,
      `168256`,
      `16832`,
      `1684`,
      `16864`,
      `1688`,
      `16dp128bit`,
      `16dp256bit`,
      `16dp32bit_t0_t15`,
      `16dp32bit_t16_t31`,
      `16dp64bit`,
      `16x2`,
      `16x8x16`,
      `16x8x4`,
      `16x8x8`,
      `1CTA`,
      `1D`,
      `1D_ARRAY`,
      `1D_BUFFER`,
      `1E`,
      `1F`,
      `1G`,
      `1X`,
      `2`,
      `2222`,
      `2301`,
      `256`,
      `2A`,
      `2CTA`,
      `2D`,
      `2D_ARRAY`,
      `2G`,
      `2X`,
      `2x64dp128bit_lw01_lw23`,
      `2x64dp128bit_lw02_lw13`,
      `2xE0M3`,
      `2xE2M1`,
      `32`,
      `32dp32bit`,
      `3333`,
      `3D`,
      `4`,
      `444`,
      `4A`,
      `4D`,
      `4G`,
      `4X`,
      `4dp256bit`,
      `4x32dp128bit`,
      `5`,
      `5D`,
      `64`,
      `64x104x16`,
      `64x104x32`,
      `64x104x64`,
      `64x104x8`,
      `64x112x16`,
      `64x112x256`,
      `64x112x32`,
      `64x112x64`,
      `64x112x8`,
      `64x120x16`,
      `64x120x32`,
      `64x120x64`,
      `64x120x8`,
      `64x128x16`,
      `64x128x256`,
      `64x128x32`,
      `64x128x64`,
      `64x128x8`,
      `64x136x16`,
      `64x136x32`,
      `64x136x64`,
      `64x136x8`,
      `64x144x16`,
      `64x144x256`,
      `64x144x32`,
      `64x144x64`,
      `64x144x8`,
      `64x152x16`,
      `64x152x32`,
      `64x152x64`,
      `64x152x8`,
      `64x160x16`,
      `64x160x256`,
      `64x160x32`,
      `64x160x64`,
      `64x160x8`,
      `64x168x16`,
      `64x168x32`,
      `64x168x64`,
      `64x168x8`,
      `64x16x16`,
      `64x16x256`,
      `64x16x32`,
      `64x16x64`,
      `64x16x8`,
      `64x176x16`,
      `64x176x256`,
      `64x176x32`,
      `64x176x64`,
      `64x176x8`,
      `64x184x16`,
      `64x184x32`,
      `64x184x64`,
      `64x184x8`,
      `64x192x16`,
      `64x192x256`,
      `64x192x32`,
      `64x192x64`,
      `64x192x8`,
      `64x200x16`,
      `64x200x32`,
      `64x200x64`,
      `64x200x8`,
      `64x208x16`,
      `64x208x256`,
      `64x208x32`,
      `64x208x64`,
      `64x208x8`,
      `64x216x16`,
      `64x216x32`,
      `64x216x64`,
      `64x216x8`,
      `64x224x16`,
      `64x224x256`,
      `64x224x32`,
      `64x224x64`,
      `64x224x8`,
      `64x232x16`,
      `64x232x32`,
      `64x232x64`,
      `64x232x8`,
      `64x240x16`,
      `64x240x256`,
      `64x240x32`,
      `64x240x64`,
      `64x240x8`,
      `64x248x16`,
      `64x248x32`,
      `64x248x64`,
      `64x248x8`,
      `64x24x16`,
      `64x24x256`,
      `64x24x32`,
      `64x24x64`,
      `64x24x8`,
      `64x256x16`,
      `64x256x256`,
      `64x256x32`,
      `64x256x64`,
      `64x256x8`,
      `64x32x16`,
      `64x32x256`,
      `64x32x32`,
      `64x32x64`,
      `64x32x8`,
      `64x40x16`,
      `64x40x32`,
      `64x40x64`,
      `64x40x8`,
      `64x48x16`,
      `64x48x256`,
      `64x48x32`,
      `64x48x64`,
      `64x48x8`,
      `64x56x16`,
      `64x56x32`,
      `64x56x64`,
      `64x56x8`,
      `64x64x16`,
      `64x64x256`,
      `64x64x32`,
      `64x64x64`,
      `64x64x8`,
      `64x72x16`,
      `64x72x32`,
      `64x72x64`,
      `64x72x8`,
      `64x80x16`,
      `64x80x256`,
      `64x80x32`,
      `64x80x64`,
      `64x80x8`,
      `64x88x16`,
      `64x88x32`,
      `64x88x64`,
      `64x88x8`,
      `64x8x16`,
      `64x8x256`,
      `64x8x32`,
      `64x8x64`,
      `64x8x8`,
      `64x96x16`,
      `64x96x256`,
      `64x96x32`,
      `64x96x64`,
      `64x96x8`,
      `7b`,
      `8`,
      `848`,
      `88128`,
      `8816`,
      `8832`,
      `884`,
      `8864`,
      `8b`,
      `8x8x4`,
      `96`,
      `9b`,
      `A`,
      `A0T1`,
      `A0TR`,
      `A0TX`,
      `A1T0`,
      `A1TR`,
      `AB`,
      `ABOVE`,
      `ABS`,
      `AC`,
      `ACC`,
      `ACCESS`,
      `ADD`,
      `AGU`,
      `ALIGN`,
      `ALL`,
      `ALLOC`,
      `ALU`,
      `AND`,
      `ANY`,
      `AOFFI`,
      `ARRIVE`,
      `ART0`,
      `ARV`,
      `ARVCNT`,
      `ASHIFT`,
      `ASYNC`,
      `ATEXIT_PC`,
      `ATTR`,
      `AVRG`,
      `B`,
      `B0`,
      `B1`,
      `B2`,
      `B3`,
      `B4E`,
      `BA`,
      `BAR`,
      `BELOW_OR_EQUAL`,
      `BF`,
      `BF16`,
      `BF16_V2`,
      `BF16x2`,
      `BF16x4`,
      `BF16x8`,
      `BFLY`,
      `BL`,
      `BLOCK16`,
      `BLOCK32`,
      `BM`,
      `BREV`,
      `BROADCAST`,
      `BYPASS`,
      `C`,
      `C32`,
      `CA`,
      `CAL`,
      `CARRY`,
      `CAS`,
      `CAST`,
      `CBCC`,
      `CC`,
      `CCTL`,
      `CEIL`,
      `CENTER`,
      `CENTROID`,
      `CENTROID_OFFSET`,
      `CF1`,
      `CFF`,
      `CFT`,
      `CG`,
      `CHI`,
      `CI`,
      `CL`,
      `CLEAR`,
      `CLO`,
      `CLOSE`,
      `COARSE`,
      `COLLECTIVE`,
      `CONSTANT`,
      `CONV`,
      `COS`,
      `COUNTED`,
      `COVERED`,
      `COVMASK`,
      `CRS`,
      `CS`,
      `CSFU`,
      `CSM_MX`,
      `CSM_TA`,
      `CSM_TR`,
      `CT`,
      `CTA`,
      `CTAPOOL`,
      `CUT`,
      `CV`,
      `CX`,
      `D`,
      `D2`,
      `D4`,
      `D8`,
      `DC`,
      `DEALLOC`,
      `DEC`,
      `DEEP`,
      `DEFER_BLOCKING`,
      `DEPBAR`,
      `DI`,
      `DIV`,
      `DIVIDE`,
      `DML2`,
      `DOWN`,
      `DP0Z`,
      `DP1XY`,
      `DP1Z`,
      `DRAIN`,
      `DRAIN_ILLEGAL`,
      `DST_G_BYTE_MASK`,
      `DUAL`,
      `DXY`,
      `E`,
      `E0M3`,
      `E0M3x4`,
      `E2M1`,
      `E2M1x4`,
      `E2M3`,
      `E2M3x4`,
      `E3M2`,
      `E3M2x4`,
      `E3M4`,
      `E3M4x16`,
      `E3M4x4`,
      `E3M4x8`,
      `E4M3`,
      `E4M3x16`,
      `E4M3x4`,
      `E4M3x8`,
      `E5M2`,
      `E5M2x16`,
      `E5M2x4`,
      `E5M2x8`,
      `E6M9`,
      `E6M9_V2`,
      `E8`,
      `E8M10`,
      `E8M7`,
      `E8M7_V2`,
      `E8x4`,
      `ECL`,
      `ECR`,
      `EF`,
      `EFL2`,
      `EL`,
      `ELL2`,
      `EMIT`,
      `EMIT_THEN_CUT`,
      `EN`,
      `ENL2`,
      `EQ`,
      `EQU`,
      `EU`,
      `EX`,
      `EX2`,
      `EXCH`,
      `EXCLUSIVE`,
      `EXP`,
      `EXPAND16BIT`,
      `EXTR0`,
      `EXTR1`,
      `EXTR2`,
      `EXTR3`,
      `EXTR4`,
      `EXTR5`,
      `EXTR6`,
      `EXTR7`,
      `F`,
      `F16`,
      `F16_V2`,
      `F16x2`,
      `F16x4`,
      `F16x8`,
      `F32`,
      `F32ADD`,
      `F32x2`,
      `F32x4`,
      `F4E`,
      `F64`,
      `FALLTHROUGH`,
      `FALSE`,
      `FCSM_MX`,
      `FCSM_TA`,
      `FCSM_TR`,
      `FDV`,
      `FILL`,
      `FINAL`,
      `FIND_AND_SET`,
      `FINE`,
      `FLOOR`,
      `FLUSH`,
      `FMA`,
      `FMA2`,
      `FMA32`,
      `FMA64`,
      `FMUL`,
      `FMZ`,
      `FTZ`,
      `FU`,
      `G`,
      `GA`,
      `GATHER4`,
      `GB`,
      `GBA`,
      `GDESC`,
      `GE`,
      `GEU`,
      `GL`,
      `GPU`,
      `GT`,
      `GTU`,
      `H0`,
      `H0_H0`,
      `H1`,
      `H1_H0`,
      `H1_H1`,
      `HDRUNPACK`,
      `HI`,
      `HPADD`,
      `HS`,
      `HW`,
      `H_AND`,
      `I`,
      `IA`,
      `IADD`,
      `IDX`,
      `IGN`,
      `IGNOREKILL`,
      `IL`,
      `ILLEGAL1`,
      `ILLEGAL2`,
      `IM2COL`,
      `INC`,
      `INNER_COVERAGE`,
      `INT`,
      `IS`,
      `ISAT`,
      `ISCADD`,
      `ISL`,
      `IS_A`,
      `IV`,
      `IVALL`,
      `IVALLD`,
      `IVALLP`,
      `IVALLT`,
      `IVALLTD`,
      `IVTH`,
      `KEEPREFCOUNT`,
      `L`,
      `L2`,
      `LB`,
      `LBA`,
      `LC`,
      `LD`,
      `LDC`,
      `LDCU`,
      `LDGSTSBAR`,
      `LE`,
      `LEG`,
      `LEGACY`,
      `LEU`,
      `LG2`,
      `LL`,
      `LLA`,
      `LMT`,
      `LO`,
      `LOD`,
      `LS`,
      `LSB`,
      `LSB_RS`,
      `LT`,
      `LTC128B`,
      `LTC256B`,
      `LTC64B`,
      `LTN24`,
      `LTN25`,
      `LTU`,
      `LU`,
      `LUT`,
      `LZ`,
      `M2`,
      `M4`,
      `M8`,
      `M8128`,
      `M816`,
      `M832`,
      `M864`,
      `M88`,
      `MAP`,
      `MAX`,
      `MAXABS`,
      `MD`,
      `MERGE_C`,
      `MIN`,
      `MINABS`,
      `MMA`,
      `MMIO`,
      `MN`,
      `MOV`,
      `MRG`,
      `MRG_16H`,
      `MRG_16L`,
      `MRG_8B0`,
      `MRG_8B2`,
      `MRG_H0`,
      `MRG_H1`,
      `MS`,
      `MSB`,
      `MSCOUNT`,
      `MT1616`,
      `MT168`,
      `MT88`,
      `MUL`,
      `MULTICAST`,
      `MX`,
      `MY_INDEX`,
      `NA`,
      `NAN`,
      `NDV`,
      `NE`,
      `NEAR`,
      `NEU`,
      `NODEC`,
      `NODEP`,
      `NOINC`,
      `NOIVALL`,
      `NONE`,
      `NOT_CARRY`,
      `NOT_OVERFLOW`,
      `NOT_SIGN`,
      `NOT_TID0`,
      `NO_ATEXIT`,
      `NS`,
      `NTZ`,
      `NUM`,
      `NZ`,
      `O`,
      `OF1`,
      `OFF`,
      `OFFSET`,
      `OFT`,
      `OLDSTATE`,
      `ONCE`,
      `ONE`,
      `OOB`,
      `OPTOUT`,
      `OR`,
      `ORDERED`,
      `OVERFLOW`,
      `P`,
      `PACK16BIT`,
      `PACK_AB`,
      `PACK_AB_MERGE_C`,
      `PACK_B`,
      `PAIR`,
      `PAND`,
      `PARTIAL`,
      `PASS`,
      `PASS_B`,
      `PATCH`,
      `PAUSE`,
      `PAUSE_QUIET`,
      `PF`,
      `PF1`,
      `PF2`,
      `PHASECHK`,
      `PHYS`,
      `PL`,
      `PML2`,
      `PO`,
      `POPC`,
      `POPCRS`,
      `POR`,
      `PQUAD`,
      `PR`,
      `PREEMPTED`,
      `PRIM`,
      `PRIVATE`,
      `PSL`,
      `PTP`,
      `QFAULT`,
      `QRY1`,
      `QUAD`,
      `R`,
      `R1`,
      `R16`,
      `R2`,
      `R4`,
      `R8`,
      `RA`,
      `RAND`,
      `RB`,
      `RBA`,
      `RC16`,
      `RC8`,
      `RCP`,
      `RCP64H`,
      `RECONVERGENT`,
      `RED`,
      `REGOFFSET`,
      `REG_RS`,
      `REL`,
      `RELIABLE`,
      `RELU`,
      `RESULT`,
      `RF`,
      `RG`,
      `RGA`,
      `RGB`,
      `RGBA`,
      `RGT`,
      `RLE`,
      `RM`,
      `RML2`,
      `RN`,
      `ROUND`,
      `RP`,
      `RS`,
      `RSLB`,
      `RSQ`,
      `RSQ64H`,
      `RTNE`,
      `RTZ`,
      `RZ`,
      `Rpc`,
      `S`,
      `S128`,
      `S16`,
      `S16H0`,
      `S16H1`,
      `S16x2`,
      `S2`,
      `S24`,
      `S2TO4`,
      `S2_6`,
      `S32`,
      `S4`,
      `S4TO8`,
      `S4x2`,
      `S64`,
      `S8`,
      `S8x4`,
      `SAFEADD`,
      `SAT`,
      `SATFINITE`,
      `SATNARROW`,
      `SATRELU`,
      `SB`,
      `SC`,
      `SCAN`,
      `SCATTER4`,
      `SCR`,
      `SD`,
      `SD32`,
      `SD64`,
      `SDCL`,
      `SEL`,
      `SELFCAST`,
      `SEQ`,
      `SEQUENCED`,
      `SF`,
      `SF0`,
      `SF1`,
      `SFF`,
      `SFT`,
      `SH`,
      `SHALLOW`,
      `SHL`,
      `SHR_15`,
      `SHR_7`,
      `SIGN`,
      `SIMD_MRG`,
      `SIN`,
      `SINCOS`,
      `SKEW`,
      `SM`,
      `SMPOOL`,
      `SORTIDX`,
      `SP`,
      `SPILL`,
      `SPIN`,
      `SQRT`,
      `STAT`,
      `STATE`,
      `STEP0`,
      `STEP1`,
      `STEP2`,
      `STEP3`,
      `STRONG`,
      `SU`,
      `SUM`,
      `SX32`,
      `SYNC`,
      `SYNCALL`,
      `SYNCS`,
      `SYS`,
      `S_OR_RN`,
      `T`,
      `TANH`,
      `TCNT`,
      `TERMINATE`,
      `TEX`,
      `TEXUNPACK`,
      `TF32`,
      `THREAD`,
      `TID`,
      `TILED`,
      `TMASK`,
      `TMEM`,
      `TRANS64`,
      `TRANSCNT`,
      `TRAP`,
      `TRIG`,
      `TRUE`,
      `TRUNC`,
      `TRYWAIT`,
      `TRY_ALLOC`,
      `TXD`,
      `U`,
      `U128`,
      `U16`,
      `U16H0`,
      `U16H1`,
      `U16x2`,
      `U2`,
      `U24`,
      `U2TO4`,
      `U2_B0`,
      `U2_B1`,
      `U2_B2`,
      `U2_B3`,
      `U2_N0`,
      `U2_N1`,
      `U2_N2`,
      `U2_N3`,
      `U2_N4`,
      `U2_N5`,
      `U2_N6`,
      `U2_N7`,
      `U32`,
      `U4`,
      `U4TO8`,
      `U4_B0`,
      `U4_B1`,
      `U4_B2`,
      `U4_B3`,
      `U4_H0`,
      `U4_H1`,
      `U4x16P64`,
      `U4x16P64TO8`,
      `U4x2`,
      `U64`,
      `U6x16P32`,
      `U6x16P32TO8`,
      `U8`,
      `U8_H0`,
      `U8_H1`,
      `U8x4`,
      `UAI`,
      `UAOFFI`,
      `UD`,
      `UE4M3`,
      `ULB`,
      `ULC`,
      `ULL`,
      `UMS`,
      `UNPACK_B`,
      `UNPACK_B_MERGE_C`,
      `UP`,
      `UPR`,
      `U_OR_RZ`,
      `VC`,
      `VIEW`,
      `VTG`,
      `W`,
      `W128`,
      `WAIT`,
      `WAKEUP`,
      `WARP`,
      `WATCH`,
      `WB`,
      `WBALL`,
      `WBALLP`,
      `WEAK`,
      `WIDE`,
      `WS`,
      `WT`,
      `X`,
      `XHI`,
      `XLO`,
      `XLU`,
      `XMED`,
      `XOR`,
      `XORSIGN`,
      `XW`,
      `XY`,
      `XYW`,
      `XYZ`,
      `XYZW`,
      `XZ`,
      `XZW`,
      `Y`,
      `YW`,
      `YZ`,
      `YZW`,
      `Z`,
      `ZD`,
      `ZF0`,
      `ZF1`,
      `ZFF`,
      `ZFILL`,
      `ZFT`,
      `ZW`,
      `_1`,
      `_10b`,
      `_128`,
      `_128dp128bit`,
      `_128dp256bit`,
      `_16`,
      `_168128`,
      `_16816`,
      `_168256`,
      `_16832`,
      `_1684`,
      `_16864`,
      `_1688`,
      `_16dp128bit`,
      `_16dp256bit`,
      `_16dp32bit_t0_t15`,
      `_16dp32bit_t16_t31`,
      `_16dp64bit`,
      `_16x2`,
      `_16x8x16`,
      `_16x8x4`,
      `_16x8x8`,
      `_1CTA`,
      `_1D`,
      `_1D_ARRAY`,
      `_1D_BUFFER`,
      `_1G`,
      `_1X`,
      `_2`,
      `_256`,
      `_2A`,
      `_2CTA`,
      `_2D`,
      `_2D_ARRAY`,
      `_2G`,
      `_2X`,
      `_2x64dp128bit_lw01_lw23`,
      `_2x64dp128bit_lw02_lw13`,
      `_2xE0M3`,
      `_2xE2M1`,
      `_32`,
      `_32dp32bit`,
      `_3D`,
      `_4`,
      `_444`,
      `_4A`,
      `_4D`,
      `_4G`,
      `_4X`,
      `_4dp256bit`,
      `_4x32dp128bit`,
      `_5D`,
      `_64`,
      `_64x104x16`,
      `_64x104x32`,
      `_64x104x64`,
      `_64x104x8`,
      `_64x112x16`,
      `_64x112x256`,
      `_64x112x32`,
      `_64x112x64`,
      `_64x112x8`,
      `_64x120x16`,
      `_64x120x32`,
      `_64x120x64`,
      `_64x120x8`,
      `_64x128x16`,
      `_64x128x256`,
      `_64x128x32`,
      `_64x128x64`,
      `_64x128x8`,
      `_64x136x16`,
      `_64x136x32`,
      `_64x136x64`,
      `_64x136x8`,
      `_64x144x16`,
      `_64x144x256`,
      `_64x144x32`,
      `_64x144x64`,
      `_64x144x8`,
      `_64x152x16`,
      `_64x152x32`,
      `_64x152x64`,
      `_64x152x8`,
      `_64x160x16`,
      `_64x160x256`,
      `_64x160x32`,
      `_64x160x64`,
      `_64x160x8`,
      `_64x168x16`,
      `_64x168x32`,
      `_64x168x64`,
      `_64x168x8`,
      `_64x16x16`,
      `_64x16x256`,
      `_64x16x32`,
      `_64x16x64`,
      `_64x16x8`,
      `_64x176x16`,
      `_64x176x256`,
      `_64x176x32`,
      `_64x176x64`,
      `_64x176x8`,
      `_64x184x16`,
      `_64x184x32`,
      `_64x184x64`,
      `_64x184x8`,
      `_64x192x16`,
      `_64x192x256`,
      `_64x192x32`,
      `_64x192x64`,
      `_64x192x8`,
      `_64x200x16`,
      `_64x200x32`,
      `_64x200x64`,
      `_64x200x8`,
      `_64x208x16`,
      `_64x208x256`,
      `_64x208x32`,
      `_64x208x64`,
      `_64x208x8`,
      `_64x216x16`,
      `_64x216x32`,
      `_64x216x64`,
      `_64x216x8`,
      `_64x224x16`,
      `_64x224x256`,
      `_64x224x32`,
      `_64x224x64`,
      `_64x224x8`,
      `_64x232x16`,
      `_64x232x32`,
      `_64x232x64`,
      `_64x232x8`,
      `_64x240x16`,
      `_64x240x256`,
      `_64x240x32`,
      `_64x240x64`,
      `_64x240x8`,
      `_64x248x16`,
      `_64x248x32`,
      `_64x248x64`,
      `_64x248x8`,
      `_64x24x16`,
      `_64x24x256`,
      `_64x24x32`,
      `_64x24x64`,
      `_64x24x8`,
      `_64x256x16`,
      `_64x256x256`,
      `_64x256x32`,
      `_64x256x64`,
      `_64x256x8`,
      `_64x32x16`,
      `_64x32x256`,
      `_64x32x32`,
      `_64x32x64`,
      `_64x32x8`,
      `_64x40x16`,
      `_64x40x32`,
      `_64x40x64`,
      `_64x40x8`,
      `_64x48x16`,
      `_64x48x256`,
      `_64x48x32`,
      `_64x48x64`,
      `_64x48x8`,
      `_64x56x16`,
      `_64x56x32`,
      `_64x56x64`,
      `_64x56x8`,
      `_64x64x16`,
      `_64x64x256`,
      `_64x64x32`,
      `_64x64x64`,
      `_64x64x8`,
      `_64x72x16`,
      `_64x72x32`,
      `_64x72x64`,
      `_64x72x8`,
      `_64x80x16`,
      `_64x80x256`,
      `_64x80x32`,
      `_64x80x64`,
      `_64x80x8`,
      `_64x88x16`,
      `_64x88x32`,
      `_64x88x64`,
      `_64x88x8`,
      `_64x8x16`,
      `_64x8x256`,
      `_64x8x32`,
      `_64x8x64`,
      `_64x8x8`,
      `_64x96x16`,
      `_64x96x256`,
      `_64x96x32`,
      `_64x96x64`,
      `_64x96x8`,
      `_7b`,
      `_8`,
      `_848`,
      `_88128`,
      `_8816`,
      `_8832`,
      `_884`,
      `_8864`,
      `_8b`,
      `_8x8x4`,
      `_96`,
      `_9b`,
      `__HIR0X001`,
      `__HIR0X00A`,
      `a`,
      `cx`,
      `gdesc`,
      `gsb0`,
      `noAVRG`,
      `noBA`,
      `noBREV`,
      `noCL`,
      `noDC`,
      `noE`,
      `noFTZ`,
      `noH_AND`,
      `noKEEPREFCOUNT`,
      `noLC`,
      `noLMT`,
      `noLOD`,
      `noMRG`,
      `noMS`,
      `noNDV`,
      `noNODEP`,
      `noP`,
      `noPO`,
      `noPSL`,
      `noPhase`,
      `noRGBA`,
      `noS`,
      `noSAT`,
      `noSH`,
      `noSKEW`,
      `noSPIN`,
      `noTOFF`,
      `noU`,
      `noWAIT`,
      `noX`,
      `noalign`,
      `noaltfmt`,
      `noaoffi`,
      `noashift`,
      `noatomsspin`,
      `noba`,
      `nobyte_mask`,
      `nocl`,
      `noclear`,
      `noclear_dist`,
      `noclose`,
      `nocond`,
      `nocond__div_conv`,
      `nocounted`,
      `nodc`,
      `nodepth`,
      `nodepth_bra`,
      `nodiv`,
      `nodiv__exclusive`,
      `nodiv__ndv`,
      `nodual`,
      `noe`,
      `noex`,
      `noexclusive`,
      `noexit_mode`,
      `noexp_desc`,
      `noexpand16bit`,
      `nof16rm`,
      `nofillctrl`,
      `nofmz`,
      `nofmz_hfma2`,
      `noftz`,
      `noh_and`,
      `noignore_kill`,
      `noim2col`,
      `noisat`,
      `nolc`,
      `nolodlc`,
      `nolodlc_tex`,
      `nomembar_mmio`,
      `nomembar_sem`,
      `noms`,
      `nomulticast`,
      `nonan`,
      `nondv`,
      `nono_atexit`,
      `nonodep`,
      `nonottid0`,
      `nontz`,
      `nooptional_warp`,
      `nooptout`,
      `noorder`,
      `nopack`,
      `nopquad`,
      `noprivate`,
      `nopseudo_op`,
      `nopseudo_opcode`,
      `norand`,
      `norelu`,
      `noreuse`,
      `norm16`,
      `nosat`,
      `nosatfinite`,
      `nosatrelu`,
      `nosatrelu_relu`,
      `nosatrelu_ui2ip`,
      `noscale`,
      `nosco`,
      `nosco_redas`,
      `noseq`,
      `noseq_ublkcp`,
      `nosh`,
      `noskew`,
      `nosortidx`,
      `nosp2`,
      `nosparse`,
      `nosrc_fmt`,
      `nosyncall`,
      `nosyncs_mod`,
      `notexunpack`,
      `notoff`,
      `nouai`,
      `nowakeup`,
      `nowatch`,
      `nows`,
      `noxor`,
      `noxorsign`,
      `reuse`,
      `rf`,
      `tmem`,
      `x1`,
      `x128`,
      `x16`,
      `x2`,
      `x32`,
      `x4`,
      `x64`,
      `x8`
    ).map(value => value.name -> value).toMap
    def fromName(name: String): Either[String, Modifier] =
      name.refine[NonEmpty[String]].left.map(_.raw).map(value => known.getOrElse(name, Vendor(value)))
    def apply(name: String): Modifier = fromName(name).fold(error => throw IllegalArgumentException(error), identity)

    extension (modifier: Modifier) {
      def is64BitInteger: Boolean = PartialFunction.cond(modifier) { case U64 | S64 => true }
      def isSigned32Or64: Boolean = PartialFunction.cond(modifier) { case S32 | S64 => true }
      def isSigned8Or16: Boolean = PartialFunction.cond(modifier) { case S8 | S16 => true }
      def isHalf: Boolean = PartialFunction.cond(modifier) { case F16 | BF16 => true }
      def isPackedHalf: Boolean = PartialFunction.cond(modifier) { case F16x2 | BF16x2 => true }
      def isBitwise: Boolean = PartialFunction.cond(modifier) { case AND | OR | XOR => true }
      def isZeroTest: Boolean = PartialFunction.cond(modifier) { case Z | NZ => true }
    }
  }
  enum Modifier(val spelling: NonEmpty[String]) derives CanEqual {
    inline def name: String = spelling
    case `0000` extends Modifier("0000")
    case `0FF` extends Modifier("0FF")
    case `1` extends Modifier("1")
    case `1032` extends Modifier("1032")
    case `10b` extends Modifier("10b")
    case `1111` extends Modifier("1111")
    case `128` extends Modifier("128")
    case `128dp128bit` extends Modifier("128dp128bit")
    case `128dp256bit` extends Modifier("128dp256bit")
    case `16` extends Modifier("16")
    case `168128` extends Modifier("168128")
    case `16816` extends Modifier("16816")
    case `168256` extends Modifier("168256")
    case `16832` extends Modifier("16832")
    case `1684` extends Modifier("1684")
    case `16864` extends Modifier("16864")
    case `1688` extends Modifier("1688")
    case `16dp128bit` extends Modifier("16dp128bit")
    case `16dp256bit` extends Modifier("16dp256bit")
    case `16dp32bit_t0_t15` extends Modifier("16dp32bit_t0_t15")
    case `16dp32bit_t16_t31` extends Modifier("16dp32bit_t16_t31")
    case `16dp64bit` extends Modifier("16dp64bit")
    case `16x2` extends Modifier("16x2")
    case `16x8x16` extends Modifier("16x8x16")
    case `16x8x4` extends Modifier("16x8x4")
    case `16x8x8` extends Modifier("16x8x8")
    case `1CTA` extends Modifier("1CTA")
    case `1D` extends Modifier("1D")
    case `1D_ARRAY` extends Modifier("1D_ARRAY")
    case `1D_BUFFER` extends Modifier("1D_BUFFER")
    case `1E` extends Modifier("1E")
    case `1F` extends Modifier("1F")
    case `1G` extends Modifier("1G")
    case `1X` extends Modifier("1X")
    case `2` extends Modifier("2")
    case `2222` extends Modifier("2222")
    case `2301` extends Modifier("2301")
    case `256` extends Modifier("256")
    case `2A` extends Modifier("2A")
    case `2CTA` extends Modifier("2CTA")
    case `2D` extends Modifier("2D")
    case `2D_ARRAY` extends Modifier("2D_ARRAY")
    case `2G` extends Modifier("2G")
    case `2X` extends Modifier("2X")
    case `2x64dp128bit_lw01_lw23` extends Modifier("2x64dp128bit_lw01_lw23")
    case `2x64dp128bit_lw02_lw13` extends Modifier("2x64dp128bit_lw02_lw13")
    case `2xE0M3` extends Modifier("2xE0M3")
    case `2xE2M1` extends Modifier("2xE2M1")
    case `32` extends Modifier("32")
    case `32dp32bit` extends Modifier("32dp32bit")
    case `3333` extends Modifier("3333")
    case `3D` extends Modifier("3D")
    case `4` extends Modifier("4")
    case `444` extends Modifier("444")
    case `4A` extends Modifier("4A")
    case `4D` extends Modifier("4D")
    case `4G` extends Modifier("4G")
    case `4X` extends Modifier("4X")
    case `4dp256bit` extends Modifier("4dp256bit")
    case `4x32dp128bit` extends Modifier("4x32dp128bit")
    case `5` extends Modifier("5")
    case `5D` extends Modifier("5D")
    case `64` extends Modifier("64")
    case `64x104x16` extends Modifier("64x104x16")
    case `64x104x32` extends Modifier("64x104x32")
    case `64x104x64` extends Modifier("64x104x64")
    case `64x104x8` extends Modifier("64x104x8")
    case `64x112x16` extends Modifier("64x112x16")
    case `64x112x256` extends Modifier("64x112x256")
    case `64x112x32` extends Modifier("64x112x32")
    case `64x112x64` extends Modifier("64x112x64")
    case `64x112x8` extends Modifier("64x112x8")
    case `64x120x16` extends Modifier("64x120x16")
    case `64x120x32` extends Modifier("64x120x32")
    case `64x120x64` extends Modifier("64x120x64")
    case `64x120x8` extends Modifier("64x120x8")
    case `64x128x16` extends Modifier("64x128x16")
    case `64x128x256` extends Modifier("64x128x256")
    case `64x128x32` extends Modifier("64x128x32")
    case `64x128x64` extends Modifier("64x128x64")
    case `64x128x8` extends Modifier("64x128x8")
    case `64x136x16` extends Modifier("64x136x16")
    case `64x136x32` extends Modifier("64x136x32")
    case `64x136x64` extends Modifier("64x136x64")
    case `64x136x8` extends Modifier("64x136x8")
    case `64x144x16` extends Modifier("64x144x16")
    case `64x144x256` extends Modifier("64x144x256")
    case `64x144x32` extends Modifier("64x144x32")
    case `64x144x64` extends Modifier("64x144x64")
    case `64x144x8` extends Modifier("64x144x8")
    case `64x152x16` extends Modifier("64x152x16")
    case `64x152x32` extends Modifier("64x152x32")
    case `64x152x64` extends Modifier("64x152x64")
    case `64x152x8` extends Modifier("64x152x8")
    case `64x160x16` extends Modifier("64x160x16")
    case `64x160x256` extends Modifier("64x160x256")
    case `64x160x32` extends Modifier("64x160x32")
    case `64x160x64` extends Modifier("64x160x64")
    case `64x160x8` extends Modifier("64x160x8")
    case `64x168x16` extends Modifier("64x168x16")
    case `64x168x32` extends Modifier("64x168x32")
    case `64x168x64` extends Modifier("64x168x64")
    case `64x168x8` extends Modifier("64x168x8")
    case `64x16x16` extends Modifier("64x16x16")
    case `64x16x256` extends Modifier("64x16x256")
    case `64x16x32` extends Modifier("64x16x32")
    case `64x16x64` extends Modifier("64x16x64")
    case `64x16x8` extends Modifier("64x16x8")
    case `64x176x16` extends Modifier("64x176x16")
    case `64x176x256` extends Modifier("64x176x256")
    case `64x176x32` extends Modifier("64x176x32")
    case `64x176x64` extends Modifier("64x176x64")
    case `64x176x8` extends Modifier("64x176x8")
    case `64x184x16` extends Modifier("64x184x16")
    case `64x184x32` extends Modifier("64x184x32")
    case `64x184x64` extends Modifier("64x184x64")
    case `64x184x8` extends Modifier("64x184x8")
    case `64x192x16` extends Modifier("64x192x16")
    case `64x192x256` extends Modifier("64x192x256")
    case `64x192x32` extends Modifier("64x192x32")
    case `64x192x64` extends Modifier("64x192x64")
    case `64x192x8` extends Modifier("64x192x8")
    case `64x200x16` extends Modifier("64x200x16")
    case `64x200x32` extends Modifier("64x200x32")
    case `64x200x64` extends Modifier("64x200x64")
    case `64x200x8` extends Modifier("64x200x8")
    case `64x208x16` extends Modifier("64x208x16")
    case `64x208x256` extends Modifier("64x208x256")
    case `64x208x32` extends Modifier("64x208x32")
    case `64x208x64` extends Modifier("64x208x64")
    case `64x208x8` extends Modifier("64x208x8")
    case `64x216x16` extends Modifier("64x216x16")
    case `64x216x32` extends Modifier("64x216x32")
    case `64x216x64` extends Modifier("64x216x64")
    case `64x216x8` extends Modifier("64x216x8")
    case `64x224x16` extends Modifier("64x224x16")
    case `64x224x256` extends Modifier("64x224x256")
    case `64x224x32` extends Modifier("64x224x32")
    case `64x224x64` extends Modifier("64x224x64")
    case `64x224x8` extends Modifier("64x224x8")
    case `64x232x16` extends Modifier("64x232x16")
    case `64x232x32` extends Modifier("64x232x32")
    case `64x232x64` extends Modifier("64x232x64")
    case `64x232x8` extends Modifier("64x232x8")
    case `64x240x16` extends Modifier("64x240x16")
    case `64x240x256` extends Modifier("64x240x256")
    case `64x240x32` extends Modifier("64x240x32")
    case `64x240x64` extends Modifier("64x240x64")
    case `64x240x8` extends Modifier("64x240x8")
    case `64x248x16` extends Modifier("64x248x16")
    case `64x248x32` extends Modifier("64x248x32")
    case `64x248x64` extends Modifier("64x248x64")
    case `64x248x8` extends Modifier("64x248x8")
    case `64x24x16` extends Modifier("64x24x16")
    case `64x24x256` extends Modifier("64x24x256")
    case `64x24x32` extends Modifier("64x24x32")
    case `64x24x64` extends Modifier("64x24x64")
    case `64x24x8` extends Modifier("64x24x8")
    case `64x256x16` extends Modifier("64x256x16")
    case `64x256x256` extends Modifier("64x256x256")
    case `64x256x32` extends Modifier("64x256x32")
    case `64x256x64` extends Modifier("64x256x64")
    case `64x256x8` extends Modifier("64x256x8")
    case `64x32x16` extends Modifier("64x32x16")
    case `64x32x256` extends Modifier("64x32x256")
    case `64x32x32` extends Modifier("64x32x32")
    case `64x32x64` extends Modifier("64x32x64")
    case `64x32x8` extends Modifier("64x32x8")
    case `64x40x16` extends Modifier("64x40x16")
    case `64x40x32` extends Modifier("64x40x32")
    case `64x40x64` extends Modifier("64x40x64")
    case `64x40x8` extends Modifier("64x40x8")
    case `64x48x16` extends Modifier("64x48x16")
    case `64x48x256` extends Modifier("64x48x256")
    case `64x48x32` extends Modifier("64x48x32")
    case `64x48x64` extends Modifier("64x48x64")
    case `64x48x8` extends Modifier("64x48x8")
    case `64x56x16` extends Modifier("64x56x16")
    case `64x56x32` extends Modifier("64x56x32")
    case `64x56x64` extends Modifier("64x56x64")
    case `64x56x8` extends Modifier("64x56x8")
    case `64x64x16` extends Modifier("64x64x16")
    case `64x64x256` extends Modifier("64x64x256")
    case `64x64x32` extends Modifier("64x64x32")
    case `64x64x64` extends Modifier("64x64x64")
    case `64x64x8` extends Modifier("64x64x8")
    case `64x72x16` extends Modifier("64x72x16")
    case `64x72x32` extends Modifier("64x72x32")
    case `64x72x64` extends Modifier("64x72x64")
    case `64x72x8` extends Modifier("64x72x8")
    case `64x80x16` extends Modifier("64x80x16")
    case `64x80x256` extends Modifier("64x80x256")
    case `64x80x32` extends Modifier("64x80x32")
    case `64x80x64` extends Modifier("64x80x64")
    case `64x80x8` extends Modifier("64x80x8")
    case `64x88x16` extends Modifier("64x88x16")
    case `64x88x32` extends Modifier("64x88x32")
    case `64x88x64` extends Modifier("64x88x64")
    case `64x88x8` extends Modifier("64x88x8")
    case `64x8x16` extends Modifier("64x8x16")
    case `64x8x256` extends Modifier("64x8x256")
    case `64x8x32` extends Modifier("64x8x32")
    case `64x8x64` extends Modifier("64x8x64")
    case `64x8x8` extends Modifier("64x8x8")
    case `64x96x16` extends Modifier("64x96x16")
    case `64x96x256` extends Modifier("64x96x256")
    case `64x96x32` extends Modifier("64x96x32")
    case `64x96x64` extends Modifier("64x96x64")
    case `64x96x8` extends Modifier("64x96x8")
    case `7b` extends Modifier("7b")
    case `8` extends Modifier("8")
    case `848` extends Modifier("848")
    case `88128` extends Modifier("88128")
    case `8816` extends Modifier("8816")
    case `8832` extends Modifier("8832")
    case `884` extends Modifier("884")
    case `8864` extends Modifier("8864")
    case `8b` extends Modifier("8b")
    case `8x8x4` extends Modifier("8x8x4")
    case `96` extends Modifier("96")
    case `9b` extends Modifier("9b")
    case `A` extends Modifier("A")
    case `A0T1` extends Modifier("A0T1")
    case `A0TR` extends Modifier("A0TR")
    case `A0TX` extends Modifier("A0TX")
    case `A1T0` extends Modifier("A1T0")
    case `A1TR` extends Modifier("A1TR")
    case `AB` extends Modifier("AB")
    case `ABOVE` extends Modifier("ABOVE")
    case `ABS` extends Modifier("ABS")
    case `AC` extends Modifier("AC")
    case `ACC` extends Modifier("ACC")
    case `ACCESS` extends Modifier("ACCESS")
    case `ADD` extends Modifier("ADD")
    case `AGU` extends Modifier("AGU")
    case `ALIGN` extends Modifier("ALIGN")
    case `ALL` extends Modifier("ALL")
    case `ALLOC` extends Modifier("ALLOC")
    case `ALU` extends Modifier("ALU")
    case `AND` extends Modifier("AND")
    case `ANY` extends Modifier("ANY")
    case `AOFFI` extends Modifier("AOFFI")
    case `ARRIVE` extends Modifier("ARRIVE")
    case `ART0` extends Modifier("ART0")
    case `ARV` extends Modifier("ARV")
    case `ARVCNT` extends Modifier("ARVCNT")
    case `ASHIFT` extends Modifier("ASHIFT")
    case `ASYNC` extends Modifier("ASYNC")
    case `ATEXIT_PC` extends Modifier("ATEXIT_PC")
    case `ATTR` extends Modifier("ATTR")
    case `AVRG` extends Modifier("AVRG")
    case `B` extends Modifier("B")
    case `B0` extends Modifier("B0")
    case `B1` extends Modifier("B1")
    case `B2` extends Modifier("B2")
    case `B3` extends Modifier("B3")
    case `B4E` extends Modifier("B4E")
    case `BA` extends Modifier("BA")
    case `BAR` extends Modifier("BAR")
    case `BELOW_OR_EQUAL` extends Modifier("BELOW_OR_EQUAL")
    case `BF` extends Modifier("BF")
    case `BF16` extends Modifier("BF16")
    case `BF16_V2` extends Modifier("BF16_V2")
    case `BF16x2` extends Modifier("BF16x2")
    case `BF16x4` extends Modifier("BF16x4")
    case `BF16x8` extends Modifier("BF16x8")
    case `BFLY` extends Modifier("BFLY")
    case `BL` extends Modifier("BL")
    case `BLOCK16` extends Modifier("BLOCK16")
    case `BLOCK32` extends Modifier("BLOCK32")
    case `BM` extends Modifier("BM")
    case `BREV` extends Modifier("BREV")
    case `BROADCAST` extends Modifier("BROADCAST")
    case `BYPASS` extends Modifier("BYPASS")
    case `C` extends Modifier("C")
    case `C32` extends Modifier("C32")
    case `CA` extends Modifier("CA")
    case `CAL` extends Modifier("CAL")
    case `CARRY` extends Modifier("CARRY")
    case `CAS` extends Modifier("CAS")
    case `CAST` extends Modifier("CAST")
    case `CBCC` extends Modifier("CBCC")
    case `CC` extends Modifier("CC")
    case `CCTL` extends Modifier("CCTL")
    case `CEIL` extends Modifier("CEIL")
    case `CENTER` extends Modifier("CENTER")
    case `CENTROID` extends Modifier("CENTROID")
    case `CENTROID_OFFSET` extends Modifier("CENTROID_OFFSET")
    case `CF1` extends Modifier("CF1")
    case `CFF` extends Modifier("CFF")
    case `CFT` extends Modifier("CFT")
    case `CG` extends Modifier("CG")
    case `CHI` extends Modifier("CHI")
    case `CI` extends Modifier("CI")
    case `CL` extends Modifier("CL")
    case `CLEAR` extends Modifier("CLEAR")
    case `CLO` extends Modifier("CLO")
    case `CLOSE` extends Modifier("CLOSE")
    case `COARSE` extends Modifier("COARSE")
    case `COLLECTIVE` extends Modifier("COLLECTIVE")
    case `CONSTANT` extends Modifier("CONSTANT")
    case `CONV` extends Modifier("CONV")
    case `COS` extends Modifier("COS")
    case `COUNTED` extends Modifier("COUNTED")
    case `COVERED` extends Modifier("COVERED")
    case `COVMASK` extends Modifier("COVMASK")
    case `CRS` extends Modifier("CRS")
    case `CS` extends Modifier("CS")
    case `CSFU` extends Modifier("CSFU")
    case `CSM_MX` extends Modifier("CSM_MX")
    case `CSM_TA` extends Modifier("CSM_TA")
    case `CSM_TR` extends Modifier("CSM_TR")
    case `CT` extends Modifier("CT")
    case `CTA` extends Modifier("CTA")
    case `CTAPOOL` extends Modifier("CTAPOOL")
    case `CUT` extends Modifier("CUT")
    case `CV` extends Modifier("CV")
    case `CX` extends Modifier("CX")
    case `D` extends Modifier("D")
    case `D2` extends Modifier("D2")
    case `D4` extends Modifier("D4")
    case `D8` extends Modifier("D8")
    case `DC` extends Modifier("DC")
    case `DEALLOC` extends Modifier("DEALLOC")
    case `DEC` extends Modifier("DEC")
    case `DEEP` extends Modifier("DEEP")
    case `DEFER_BLOCKING` extends Modifier("DEFER_BLOCKING")
    case `DEPBAR` extends Modifier("DEPBAR")
    case `DI` extends Modifier("DI")
    case `DIV` extends Modifier("DIV")
    case `DIVIDE` extends Modifier("DIVIDE")
    case `DML2` extends Modifier("DML2")
    case `DOWN` extends Modifier("DOWN")
    case `DP0Z` extends Modifier("DP0Z")
    case `DP1XY` extends Modifier("DP1XY")
    case `DP1Z` extends Modifier("DP1Z")
    case `DRAIN` extends Modifier("DRAIN")
    case `DRAIN_ILLEGAL` extends Modifier("DRAIN_ILLEGAL")
    case `DST_G_BYTE_MASK` extends Modifier("DST_G_BYTE_MASK")
    case `DUAL` extends Modifier("DUAL")
    case `DXY` extends Modifier("DXY")
    case `E` extends Modifier("E")
    case `E0M3` extends Modifier("E0M3")
    case `E0M3x4` extends Modifier("E0M3x4")
    case `E2M1` extends Modifier("E2M1")
    case `E2M1x4` extends Modifier("E2M1x4")
    case `E2M3` extends Modifier("E2M3")
    case `E2M3x4` extends Modifier("E2M3x4")
    case `E3M2` extends Modifier("E3M2")
    case `E3M2x4` extends Modifier("E3M2x4")
    case `E3M4` extends Modifier("E3M4")
    case `E3M4x16` extends Modifier("E3M4x16")
    case `E3M4x4` extends Modifier("E3M4x4")
    case `E3M4x8` extends Modifier("E3M4x8")
    case `E4M3` extends Modifier("E4M3")
    case `E4M3x16` extends Modifier("E4M3x16")
    case `E4M3x4` extends Modifier("E4M3x4")
    case `E4M3x8` extends Modifier("E4M3x8")
    case `E5M2` extends Modifier("E5M2")
    case `E5M2x16` extends Modifier("E5M2x16")
    case `E5M2x4` extends Modifier("E5M2x4")
    case `E5M2x8` extends Modifier("E5M2x8")
    case `E6M9` extends Modifier("E6M9")
    case `E6M9_V2` extends Modifier("E6M9_V2")
    case `E8` extends Modifier("E8")
    case `E8M10` extends Modifier("E8M10")
    case `E8M7` extends Modifier("E8M7")
    case `E8M7_V2` extends Modifier("E8M7_V2")
    case `E8x4` extends Modifier("E8x4")
    case `ECL` extends Modifier("ECL")
    case `ECR` extends Modifier("ECR")
    case `EF` extends Modifier("EF")
    case `EFL2` extends Modifier("EFL2")
    case `EL` extends Modifier("EL")
    case `ELL2` extends Modifier("ELL2")
    case `EMIT` extends Modifier("EMIT")
    case `EMIT_THEN_CUT` extends Modifier("EMIT_THEN_CUT")
    case `EN` extends Modifier("EN")
    case `ENL2` extends Modifier("ENL2")
    case `EQ` extends Modifier("EQ")
    case `EQU` extends Modifier("EQU")
    case `EU` extends Modifier("EU")
    case `EX` extends Modifier("EX")
    case `EX2` extends Modifier("EX2")
    case `EXCH` extends Modifier("EXCH")
    case `EXCLUSIVE` extends Modifier("EXCLUSIVE")
    case `EXP` extends Modifier("EXP")
    case `EXPAND16BIT` extends Modifier("EXPAND16BIT")
    case `EXTR0` extends Modifier("EXTR0")
    case `EXTR1` extends Modifier("EXTR1")
    case `EXTR2` extends Modifier("EXTR2")
    case `EXTR3` extends Modifier("EXTR3")
    case `EXTR4` extends Modifier("EXTR4")
    case `EXTR5` extends Modifier("EXTR5")
    case `EXTR6` extends Modifier("EXTR6")
    case `EXTR7` extends Modifier("EXTR7")
    case `F` extends Modifier("F")
    case `F16` extends Modifier("F16")
    case `F16_V2` extends Modifier("F16_V2")
    case `F16x2` extends Modifier("F16x2")
    case `F16x4` extends Modifier("F16x4")
    case `F16x8` extends Modifier("F16x8")
    case `F32` extends Modifier("F32")
    case `F32ADD` extends Modifier("F32ADD")
    case `F32x2` extends Modifier("F32x2")
    case `F32x4` extends Modifier("F32x4")
    case `F4E` extends Modifier("F4E")
    case `F64` extends Modifier("F64")
    case `FALLTHROUGH` extends Modifier("FALLTHROUGH")
    case `FALSE` extends Modifier("FALSE")
    case `FCSM_MX` extends Modifier("FCSM_MX")
    case `FCSM_TA` extends Modifier("FCSM_TA")
    case `FCSM_TR` extends Modifier("FCSM_TR")
    case `FDV` extends Modifier("FDV")
    case `FILL` extends Modifier("FILL")
    case `FINAL` extends Modifier("FINAL")
    case `FIND_AND_SET` extends Modifier("FIND_AND_SET")
    case `FINE` extends Modifier("FINE")
    case `FLOOR` extends Modifier("FLOOR")
    case `FLUSH` extends Modifier("FLUSH")
    case `FMA` extends Modifier("FMA")
    case `FMA2` extends Modifier("FMA2")
    case `FMA32` extends Modifier("FMA32")
    case `FMA64` extends Modifier("FMA64")
    case `FMUL` extends Modifier("FMUL")
    case `FMZ` extends Modifier("FMZ")
    case `FTZ` extends Modifier("FTZ")
    case `FU` extends Modifier("FU")
    case `G` extends Modifier("G")
    case `GA` extends Modifier("GA")
    case `GATHER4` extends Modifier("GATHER4")
    case `GB` extends Modifier("GB")
    case `GBA` extends Modifier("GBA")
    case `GDESC` extends Modifier("GDESC")
    case `GE` extends Modifier("GE")
    case `GEU` extends Modifier("GEU")
    case `GL` extends Modifier("GL")
    case `GPU` extends Modifier("GPU")
    case `GT` extends Modifier("GT")
    case `GTU` extends Modifier("GTU")
    case `H0` extends Modifier("H0")
    case `H0_H0` extends Modifier("H0_H0")
    case `H1` extends Modifier("H1")
    case `H1_H0` extends Modifier("H1_H0")
    case `H1_H1` extends Modifier("H1_H1")
    case `HDRUNPACK` extends Modifier("HDRUNPACK")
    case `HI` extends Modifier("HI")
    case `HPADD` extends Modifier("HPADD")
    case `HS` extends Modifier("HS")
    case `HW` extends Modifier("HW")
    case `H_AND` extends Modifier("H_AND")
    case `I` extends Modifier("I")
    case `IA` extends Modifier("IA")
    case `IADD` extends Modifier("IADD")
    case `IDX` extends Modifier("IDX")
    case `IGN` extends Modifier("IGN")
    case `IGNOREKILL` extends Modifier("IGNOREKILL")
    case `IL` extends Modifier("IL")
    case `ILLEGAL1` extends Modifier("ILLEGAL1")
    case `ILLEGAL2` extends Modifier("ILLEGAL2")
    case `IM2COL` extends Modifier("IM2COL")
    case `INC` extends Modifier("INC")
    case `INNER_COVERAGE` extends Modifier("INNER_COVERAGE")
    case `INT` extends Modifier("INT")
    case `IS` extends Modifier("IS")
    case `ISAT` extends Modifier("ISAT")
    case `ISCADD` extends Modifier("ISCADD")
    case `ISL` extends Modifier("ISL")
    case `IS_A` extends Modifier("IS_A")
    case `IV` extends Modifier("IV")
    case `IVALL` extends Modifier("IVALL")
    case `IVALLD` extends Modifier("IVALLD")
    case `IVALLP` extends Modifier("IVALLP")
    case `IVALLT` extends Modifier("IVALLT")
    case `IVALLTD` extends Modifier("IVALLTD")
    case `IVTH` extends Modifier("IVTH")
    case `KEEPREFCOUNT` extends Modifier("KEEPREFCOUNT")
    case `L` extends Modifier("L")
    case `L2` extends Modifier("L2")
    case `LB` extends Modifier("LB")
    case `LBA` extends Modifier("LBA")
    case `LC` extends Modifier("LC")
    case `LD` extends Modifier("LD")
    case `LDC` extends Modifier("LDC")
    case `LDCU` extends Modifier("LDCU")
    case `LDGSTSBAR` extends Modifier("LDGSTSBAR")
    case `LE` extends Modifier("LE")
    case `LEG` extends Modifier("LEG")
    case `LEGACY` extends Modifier("LEGACY")
    case `LEU` extends Modifier("LEU")
    case `LG2` extends Modifier("LG2")
    case `LL` extends Modifier("LL")
    case `LLA` extends Modifier("LLA")
    case `LMT` extends Modifier("LMT")
    case `LO` extends Modifier("LO")
    case `LOD` extends Modifier("LOD")
    case `LS` extends Modifier("LS")
    case `LSB` extends Modifier("LSB")
    case `LSB_RS` extends Modifier("LSB_RS")
    case `LT` extends Modifier("LT")
    case `LTC128B` extends Modifier("LTC128B")
    case `LTC256B` extends Modifier("LTC256B")
    case `LTC64B` extends Modifier("LTC64B")
    case `LTN24` extends Modifier("LTN24")
    case `LTN25` extends Modifier("LTN25")
    case `LTU` extends Modifier("LTU")
    case `LU` extends Modifier("LU")
    case `LUT` extends Modifier("LUT")
    case `LZ` extends Modifier("LZ")
    case `M2` extends Modifier("M2")
    case `M4` extends Modifier("M4")
    case `M8` extends Modifier("M8")
    case `M8128` extends Modifier("M8128")
    case `M816` extends Modifier("M816")
    case `M832` extends Modifier("M832")
    case `M864` extends Modifier("M864")
    case `M88` extends Modifier("M88")
    case `MAP` extends Modifier("MAP")
    case `MAX` extends Modifier("MAX")
    case `MAXABS` extends Modifier("MAXABS")
    case `MD` extends Modifier("MD")
    case `MERGE_C` extends Modifier("MERGE_C")
    case `MIN` extends Modifier("MIN")
    case `MINABS` extends Modifier("MINABS")
    case `MMA` extends Modifier("MMA")
    case `MMIO` extends Modifier("MMIO")
    case `MN` extends Modifier("MN")
    case `MOV` extends Modifier("MOV")
    case `MRG` extends Modifier("MRG")
    case `MRG_16H` extends Modifier("MRG_16H")
    case `MRG_16L` extends Modifier("MRG_16L")
    case `MRG_8B0` extends Modifier("MRG_8B0")
    case `MRG_8B2` extends Modifier("MRG_8B2")
    case `MRG_H0` extends Modifier("MRG_H0")
    case `MRG_H1` extends Modifier("MRG_H1")
    case `MS` extends Modifier("MS")
    case `MSB` extends Modifier("MSB")
    case `MSCOUNT` extends Modifier("MSCOUNT")
    case `MT1616` extends Modifier("MT1616")
    case `MT168` extends Modifier("MT168")
    case `MT88` extends Modifier("MT88")
    case `MUL` extends Modifier("MUL")
    case `MULTICAST` extends Modifier("MULTICAST")
    case `MX` extends Modifier("MX")
    case `MY_INDEX` extends Modifier("MY_INDEX")
    case `NA` extends Modifier("NA")
    case `NAN` extends Modifier("NAN")
    case `NDV` extends Modifier("NDV")
    case `NE` extends Modifier("NE")
    case `NEAR` extends Modifier("NEAR")
    case `NEU` extends Modifier("NEU")
    case `NODEC` extends Modifier("NODEC")
    case `NODEP` extends Modifier("NODEP")
    case `NOINC` extends Modifier("NOINC")
    case `NOIVALL` extends Modifier("NOIVALL")
    case `NONE` extends Modifier("NONE")
    case `NOT_CARRY` extends Modifier("NOT_CARRY")
    case `NOT_OVERFLOW` extends Modifier("NOT_OVERFLOW")
    case `NOT_SIGN` extends Modifier("NOT_SIGN")
    case `NOT_TID0` extends Modifier("NOT_TID0")
    case `NO_ATEXIT` extends Modifier("NO_ATEXIT")
    case `NS` extends Modifier("NS")
    case `NTZ` extends Modifier("NTZ")
    case `NUM` extends Modifier("NUM")
    case `NZ` extends Modifier("NZ")
    case `O` extends Modifier("O")
    case `OF1` extends Modifier("OF1")
    case `OFF` extends Modifier("OFF")
    case `OFFSET` extends Modifier("OFFSET")
    case `OFT` extends Modifier("OFT")
    case `OLDSTATE` extends Modifier("OLDSTATE")
    case `ONCE` extends Modifier("ONCE")
    case `ONE` extends Modifier("ONE")
    case `OOB` extends Modifier("OOB")
    case `OPTOUT` extends Modifier("OPTOUT")
    case `OR` extends Modifier("OR")
    case `ORDERED` extends Modifier("ORDERED")
    case `OVERFLOW` extends Modifier("OVERFLOW")
    case `P` extends Modifier("P")
    case `PACK16BIT` extends Modifier("PACK16BIT")
    case `PACK_AB` extends Modifier("PACK_AB")
    case `PACK_AB_MERGE_C` extends Modifier("PACK_AB_MERGE_C")
    case `PACK_B` extends Modifier("PACK_B")
    case `PAIR` extends Modifier("PAIR")
    case `PAND` extends Modifier("PAND")
    case `PARTIAL` extends Modifier("PARTIAL")
    case `PASS` extends Modifier("PASS")
    case `PASS_B` extends Modifier("PASS_B")
    case `PATCH` extends Modifier("PATCH")
    case `PAUSE` extends Modifier("PAUSE")
    case `PAUSE_QUIET` extends Modifier("PAUSE_QUIET")
    case `PF` extends Modifier("PF")
    case `PF1` extends Modifier("PF1")
    case `PF2` extends Modifier("PF2")
    case `PHASECHK` extends Modifier("PHASECHK")
    case `PHYS` extends Modifier("PHYS")
    case `PL` extends Modifier("PL")
    case `PML2` extends Modifier("PML2")
    case `PO` extends Modifier("PO")
    case `POPC` extends Modifier("POPC")
    case `POPCRS` extends Modifier("POPCRS")
    case `POR` extends Modifier("POR")
    case `PQUAD` extends Modifier("PQUAD")
    case `PR` extends Modifier("PR")
    case `PREEMPTED` extends Modifier("PREEMPTED")
    case `PRIM` extends Modifier("PRIM")
    case `PRIVATE` extends Modifier("PRIVATE")
    case `PSL` extends Modifier("PSL")
    case `PTP` extends Modifier("PTP")
    case `QFAULT` extends Modifier("QFAULT")
    case `QRY1` extends Modifier("QRY1")
    case `QUAD` extends Modifier("QUAD")
    case `R` extends Modifier("R")
    case `R1` extends Modifier("R1")
    case `R16` extends Modifier("R16")
    case `R2` extends Modifier("R2")
    case `R4` extends Modifier("R4")
    case `R8` extends Modifier("R8")
    case `RA` extends Modifier("RA")
    case `RAND` extends Modifier("RAND")
    case `RB` extends Modifier("RB")
    case `RBA` extends Modifier("RBA")
    case `RC16` extends Modifier("RC16")
    case `RC8` extends Modifier("RC8")
    case `RCP` extends Modifier("RCP")
    case `RCP64H` extends Modifier("RCP64H")
    case `RECONVERGENT` extends Modifier("RECONVERGENT")
    case `RED` extends Modifier("RED")
    case `REGOFFSET` extends Modifier("REGOFFSET")
    case `REG_RS` extends Modifier("REG_RS")
    case `REL` extends Modifier("REL")
    case `RELIABLE` extends Modifier("RELIABLE")
    case `RELU` extends Modifier("RELU")
    case `RESULT` extends Modifier("RESULT")
    case `RF` extends Modifier("RF")
    case `RG` extends Modifier("RG")
    case `RGA` extends Modifier("RGA")
    case `RGB` extends Modifier("RGB")
    case `RGBA` extends Modifier("RGBA")
    case `RGT` extends Modifier("RGT")
    case `RLE` extends Modifier("RLE")
    case `RM` extends Modifier("RM")
    case `RML2` extends Modifier("RML2")
    case `RN` extends Modifier("RN")
    case `ROUND` extends Modifier("ROUND")
    case `RP` extends Modifier("RP")
    case `RS` extends Modifier("RS")
    case `RSLB` extends Modifier("RSLB")
    case `RSQ` extends Modifier("RSQ")
    case `RSQ64H` extends Modifier("RSQ64H")
    case `RTNE` extends Modifier("RTNE")
    case `RTZ` extends Modifier("RTZ")
    case `RZ` extends Modifier("RZ")
    case `Rpc` extends Modifier("Rpc")
    case `S` extends Modifier("S")
    case `S128` extends Modifier("S128")
    case `S16` extends Modifier("S16")
    case `S16H0` extends Modifier("S16H0")
    case `S16H1` extends Modifier("S16H1")
    case `S16x2` extends Modifier("S16x2")
    case `S2` extends Modifier("S2")
    case `S24` extends Modifier("S24")
    case `S2TO4` extends Modifier("S2TO4")
    case `S2_6` extends Modifier("S2_6")
    case `S32` extends Modifier("S32")
    case `S4` extends Modifier("S4")
    case `S4TO8` extends Modifier("S4TO8")
    case `S4x2` extends Modifier("S4x2")
    case `S64` extends Modifier("S64")
    case `S8` extends Modifier("S8")
    case `S8x4` extends Modifier("S8x4")
    case `SAFEADD` extends Modifier("SAFEADD")
    case `SAT` extends Modifier("SAT")
    case `SATFINITE` extends Modifier("SATFINITE")
    case `SATNARROW` extends Modifier("SATNARROW")
    case `SATRELU` extends Modifier("SATRELU")
    case `SB` extends Modifier("SB")
    case `SC` extends Modifier("SC")
    case `SCAN` extends Modifier("SCAN")
    case `SCATTER4` extends Modifier("SCATTER4")
    case `SCR` extends Modifier("SCR")
    case `SD` extends Modifier("SD")
    case `SD32` extends Modifier("SD32")
    case `SD64` extends Modifier("SD64")
    case `SDCL` extends Modifier("SDCL")
    case `SEL` extends Modifier("SEL")
    case `SELFCAST` extends Modifier("SELFCAST")
    case `SEQ` extends Modifier("SEQ")
    case `SEQUENCED` extends Modifier("SEQUENCED")
    case `SF` extends Modifier("SF")
    case `SF0` extends Modifier("SF0")
    case `SF1` extends Modifier("SF1")
    case `SFF` extends Modifier("SFF")
    case `SFT` extends Modifier("SFT")
    case `SH` extends Modifier("SH")
    case `SHALLOW` extends Modifier("SHALLOW")
    case `SHL` extends Modifier("SHL")
    case `SHR_15` extends Modifier("SHR_15")
    case `SHR_7` extends Modifier("SHR_7")
    case `SIGN` extends Modifier("SIGN")
    case `SIMD_MRG` extends Modifier("SIMD_MRG")
    case `SIN` extends Modifier("SIN")
    case `SINCOS` extends Modifier("SINCOS")
    case `SKEW` extends Modifier("SKEW")
    case `SM` extends Modifier("SM")
    case `SMPOOL` extends Modifier("SMPOOL")
    case `SORTIDX` extends Modifier("SORTIDX")
    case `SP` extends Modifier("SP")
    case `SPILL` extends Modifier("SPILL")
    case `SPIN` extends Modifier("SPIN")
    case `SQRT` extends Modifier("SQRT")
    case `STAT` extends Modifier("STAT")
    case `STATE` extends Modifier("STATE")
    case `STEP0` extends Modifier("STEP0")
    case `STEP1` extends Modifier("STEP1")
    case `STEP2` extends Modifier("STEP2")
    case `STEP3` extends Modifier("STEP3")
    case `STRONG` extends Modifier("STRONG")
    case `SU` extends Modifier("SU")
    case `SUM` extends Modifier("SUM")
    case `SX32` extends Modifier("SX32")
    case `SYNC` extends Modifier("SYNC")
    case `SYNCALL` extends Modifier("SYNCALL")
    case `SYNCS` extends Modifier("SYNCS")
    case `SYS` extends Modifier("SYS")
    case `S_OR_RN` extends Modifier("S_OR_RN")
    case `T` extends Modifier("T")
    case `TANH` extends Modifier("TANH")
    case `TCNT` extends Modifier("TCNT")
    case `TERMINATE` extends Modifier("TERMINATE")
    case `TEX` extends Modifier("TEX")
    case `TEXUNPACK` extends Modifier("TEXUNPACK")
    case `TF32` extends Modifier("TF32")
    case `THREAD` extends Modifier("THREAD")
    case `TID` extends Modifier("TID")
    case `TILED` extends Modifier("TILED")
    case `TMASK` extends Modifier("TMASK")
    case `TMEM` extends Modifier("TMEM")
    case `TRANS64` extends Modifier("TRANS64")
    case `TRANSCNT` extends Modifier("TRANSCNT")
    case `TRAP` extends Modifier("TRAP")
    case `TRIG` extends Modifier("TRIG")
    case `TRUE` extends Modifier("TRUE")
    case `TRUNC` extends Modifier("TRUNC")
    case `TRYWAIT` extends Modifier("TRYWAIT")
    case `TRY_ALLOC` extends Modifier("TRY_ALLOC")
    case `TXD` extends Modifier("TXD")
    case `U` extends Modifier("U")
    case `U128` extends Modifier("U128")
    case `U16` extends Modifier("U16")
    case `U16H0` extends Modifier("U16H0")
    case `U16H1` extends Modifier("U16H1")
    case `U16x2` extends Modifier("U16x2")
    case `U2` extends Modifier("U2")
    case `U24` extends Modifier("U24")
    case `U2TO4` extends Modifier("U2TO4")
    case `U2_B0` extends Modifier("U2_B0")
    case `U2_B1` extends Modifier("U2_B1")
    case `U2_B2` extends Modifier("U2_B2")
    case `U2_B3` extends Modifier("U2_B3")
    case `U2_N0` extends Modifier("U2_N0")
    case `U2_N1` extends Modifier("U2_N1")
    case `U2_N2` extends Modifier("U2_N2")
    case `U2_N3` extends Modifier("U2_N3")
    case `U2_N4` extends Modifier("U2_N4")
    case `U2_N5` extends Modifier("U2_N5")
    case `U2_N6` extends Modifier("U2_N6")
    case `U2_N7` extends Modifier("U2_N7")
    case `U32` extends Modifier("U32")
    case `U4` extends Modifier("U4")
    case `U4TO8` extends Modifier("U4TO8")
    case `U4_B0` extends Modifier("U4_B0")
    case `U4_B1` extends Modifier("U4_B1")
    case `U4_B2` extends Modifier("U4_B2")
    case `U4_B3` extends Modifier("U4_B3")
    case `U4_H0` extends Modifier("U4_H0")
    case `U4_H1` extends Modifier("U4_H1")
    case `U4x16P64` extends Modifier("U4x16P64")
    case `U4x16P64TO8` extends Modifier("U4x16P64TO8")
    case `U4x2` extends Modifier("U4x2")
    case `U64` extends Modifier("U64")
    case `U6x16P32` extends Modifier("U6x16P32")
    case `U6x16P32TO8` extends Modifier("U6x16P32TO8")
    case `U8` extends Modifier("U8")
    case `U8_H0` extends Modifier("U8_H0")
    case `U8_H1` extends Modifier("U8_H1")
    case `U8x4` extends Modifier("U8x4")
    case `UAI` extends Modifier("UAI")
    case `UAOFFI` extends Modifier("UAOFFI")
    case `UD` extends Modifier("UD")
    case `UE4M3` extends Modifier("UE4M3")
    case `ULB` extends Modifier("ULB")
    case `ULC` extends Modifier("ULC")
    case `ULL` extends Modifier("ULL")
    case `UMS` extends Modifier("UMS")
    case `UNPACK_B` extends Modifier("UNPACK_B")
    case `UNPACK_B_MERGE_C` extends Modifier("UNPACK_B_MERGE_C")
    case `UP` extends Modifier("UP")
    case `UPR` extends Modifier("UPR")
    case `U_OR_RZ` extends Modifier("U_OR_RZ")
    case `VC` extends Modifier("VC")
    case `VIEW` extends Modifier("VIEW")
    case `VTG` extends Modifier("VTG")
    case `W` extends Modifier("W")
    case `W128` extends Modifier("W128")
    case `WAIT` extends Modifier("WAIT")
    case `WAKEUP` extends Modifier("WAKEUP")
    case `WARP` extends Modifier("WARP")
    case `WATCH` extends Modifier("WATCH")
    case `WB` extends Modifier("WB")
    case `WBALL` extends Modifier("WBALL")
    case `WBALLP` extends Modifier("WBALLP")
    case `WEAK` extends Modifier("WEAK")
    case `WIDE` extends Modifier("WIDE")
    case `WS` extends Modifier("WS")
    case `WT` extends Modifier("WT")
    case `X` extends Modifier("X")
    case `XHI` extends Modifier("XHI")
    case `XLO` extends Modifier("XLO")
    case `XLU` extends Modifier("XLU")
    case `XMED` extends Modifier("XMED")
    case `XOR` extends Modifier("XOR")
    case `XORSIGN` extends Modifier("XORSIGN")
    case `XW` extends Modifier("XW")
    case `XY` extends Modifier("XY")
    case `XYW` extends Modifier("XYW")
    case `XYZ` extends Modifier("XYZ")
    case `XYZW` extends Modifier("XYZW")
    case `XZ` extends Modifier("XZ")
    case `XZW` extends Modifier("XZW")
    case `Y` extends Modifier("Y")
    case `YW` extends Modifier("YW")
    case `YZ` extends Modifier("YZ")
    case `YZW` extends Modifier("YZW")
    case `Z` extends Modifier("Z")
    case `ZD` extends Modifier("ZD")
    case `ZF0` extends Modifier("ZF0")
    case `ZF1` extends Modifier("ZF1")
    case `ZFF` extends Modifier("ZFF")
    case `ZFILL` extends Modifier("ZFILL")
    case `ZFT` extends Modifier("ZFT")
    case `ZW` extends Modifier("ZW")
    case `_1` extends Modifier("_1")
    case `_10b` extends Modifier("_10b")
    case `_128` extends Modifier("_128")
    case `_128dp128bit` extends Modifier("_128dp128bit")
    case `_128dp256bit` extends Modifier("_128dp256bit")
    case `_16` extends Modifier("_16")
    case `_168128` extends Modifier("_168128")
    case `_16816` extends Modifier("_16816")
    case `_168256` extends Modifier("_168256")
    case `_16832` extends Modifier("_16832")
    case `_1684` extends Modifier("_1684")
    case `_16864` extends Modifier("_16864")
    case `_1688` extends Modifier("_1688")
    case `_16dp128bit` extends Modifier("_16dp128bit")
    case `_16dp256bit` extends Modifier("_16dp256bit")
    case `_16dp32bit_t0_t15` extends Modifier("_16dp32bit_t0_t15")
    case `_16dp32bit_t16_t31` extends Modifier("_16dp32bit_t16_t31")
    case `_16dp64bit` extends Modifier("_16dp64bit")
    case `_16x2` extends Modifier("_16x2")
    case `_16x8x16` extends Modifier("_16x8x16")
    case `_16x8x4` extends Modifier("_16x8x4")
    case `_16x8x8` extends Modifier("_16x8x8")
    case `_1CTA` extends Modifier("_1CTA")
    case `_1D` extends Modifier("_1D")
    case `_1D_ARRAY` extends Modifier("_1D_ARRAY")
    case `_1D_BUFFER` extends Modifier("_1D_BUFFER")
    case `_1G` extends Modifier("_1G")
    case `_1X` extends Modifier("_1X")
    case `_2` extends Modifier("_2")
    case `_256` extends Modifier("_256")
    case `_2A` extends Modifier("_2A")
    case `_2CTA` extends Modifier("_2CTA")
    case `_2D` extends Modifier("_2D")
    case `_2D_ARRAY` extends Modifier("_2D_ARRAY")
    case `_2G` extends Modifier("_2G")
    case `_2X` extends Modifier("_2X")
    case `_2x64dp128bit_lw01_lw23` extends Modifier("_2x64dp128bit_lw01_lw23")
    case `_2x64dp128bit_lw02_lw13` extends Modifier("_2x64dp128bit_lw02_lw13")
    case `_2xE0M3` extends Modifier("_2xE0M3")
    case `_2xE2M1` extends Modifier("_2xE2M1")
    case `_32` extends Modifier("_32")
    case `_32dp32bit` extends Modifier("_32dp32bit")
    case `_3D` extends Modifier("_3D")
    case `_4` extends Modifier("_4")
    case `_444` extends Modifier("_444")
    case `_4A` extends Modifier("_4A")
    case `_4D` extends Modifier("_4D")
    case `_4G` extends Modifier("_4G")
    case `_4X` extends Modifier("_4X")
    case `_4dp256bit` extends Modifier("_4dp256bit")
    case `_4x32dp128bit` extends Modifier("_4x32dp128bit")
    case `_5D` extends Modifier("_5D")
    case `_64` extends Modifier("_64")
    case `_64x104x16` extends Modifier("_64x104x16")
    case `_64x104x32` extends Modifier("_64x104x32")
    case `_64x104x64` extends Modifier("_64x104x64")
    case `_64x104x8` extends Modifier("_64x104x8")
    case `_64x112x16` extends Modifier("_64x112x16")
    case `_64x112x256` extends Modifier("_64x112x256")
    case `_64x112x32` extends Modifier("_64x112x32")
    case `_64x112x64` extends Modifier("_64x112x64")
    case `_64x112x8` extends Modifier("_64x112x8")
    case `_64x120x16` extends Modifier("_64x120x16")
    case `_64x120x32` extends Modifier("_64x120x32")
    case `_64x120x64` extends Modifier("_64x120x64")
    case `_64x120x8` extends Modifier("_64x120x8")
    case `_64x128x16` extends Modifier("_64x128x16")
    case `_64x128x256` extends Modifier("_64x128x256")
    case `_64x128x32` extends Modifier("_64x128x32")
    case `_64x128x64` extends Modifier("_64x128x64")
    case `_64x128x8` extends Modifier("_64x128x8")
    case `_64x136x16` extends Modifier("_64x136x16")
    case `_64x136x32` extends Modifier("_64x136x32")
    case `_64x136x64` extends Modifier("_64x136x64")
    case `_64x136x8` extends Modifier("_64x136x8")
    case `_64x144x16` extends Modifier("_64x144x16")
    case `_64x144x256` extends Modifier("_64x144x256")
    case `_64x144x32` extends Modifier("_64x144x32")
    case `_64x144x64` extends Modifier("_64x144x64")
    case `_64x144x8` extends Modifier("_64x144x8")
    case `_64x152x16` extends Modifier("_64x152x16")
    case `_64x152x32` extends Modifier("_64x152x32")
    case `_64x152x64` extends Modifier("_64x152x64")
    case `_64x152x8` extends Modifier("_64x152x8")
    case `_64x160x16` extends Modifier("_64x160x16")
    case `_64x160x256` extends Modifier("_64x160x256")
    case `_64x160x32` extends Modifier("_64x160x32")
    case `_64x160x64` extends Modifier("_64x160x64")
    case `_64x160x8` extends Modifier("_64x160x8")
    case `_64x168x16` extends Modifier("_64x168x16")
    case `_64x168x32` extends Modifier("_64x168x32")
    case `_64x168x64` extends Modifier("_64x168x64")
    case `_64x168x8` extends Modifier("_64x168x8")
    case `_64x16x16` extends Modifier("_64x16x16")
    case `_64x16x256` extends Modifier("_64x16x256")
    case `_64x16x32` extends Modifier("_64x16x32")
    case `_64x16x64` extends Modifier("_64x16x64")
    case `_64x16x8` extends Modifier("_64x16x8")
    case `_64x176x16` extends Modifier("_64x176x16")
    case `_64x176x256` extends Modifier("_64x176x256")
    case `_64x176x32` extends Modifier("_64x176x32")
    case `_64x176x64` extends Modifier("_64x176x64")
    case `_64x176x8` extends Modifier("_64x176x8")
    case `_64x184x16` extends Modifier("_64x184x16")
    case `_64x184x32` extends Modifier("_64x184x32")
    case `_64x184x64` extends Modifier("_64x184x64")
    case `_64x184x8` extends Modifier("_64x184x8")
    case `_64x192x16` extends Modifier("_64x192x16")
    case `_64x192x256` extends Modifier("_64x192x256")
    case `_64x192x32` extends Modifier("_64x192x32")
    case `_64x192x64` extends Modifier("_64x192x64")
    case `_64x192x8` extends Modifier("_64x192x8")
    case `_64x200x16` extends Modifier("_64x200x16")
    case `_64x200x32` extends Modifier("_64x200x32")
    case `_64x200x64` extends Modifier("_64x200x64")
    case `_64x200x8` extends Modifier("_64x200x8")
    case `_64x208x16` extends Modifier("_64x208x16")
    case `_64x208x256` extends Modifier("_64x208x256")
    case `_64x208x32` extends Modifier("_64x208x32")
    case `_64x208x64` extends Modifier("_64x208x64")
    case `_64x208x8` extends Modifier("_64x208x8")
    case `_64x216x16` extends Modifier("_64x216x16")
    case `_64x216x32` extends Modifier("_64x216x32")
    case `_64x216x64` extends Modifier("_64x216x64")
    case `_64x216x8` extends Modifier("_64x216x8")
    case `_64x224x16` extends Modifier("_64x224x16")
    case `_64x224x256` extends Modifier("_64x224x256")
    case `_64x224x32` extends Modifier("_64x224x32")
    case `_64x224x64` extends Modifier("_64x224x64")
    case `_64x224x8` extends Modifier("_64x224x8")
    case `_64x232x16` extends Modifier("_64x232x16")
    case `_64x232x32` extends Modifier("_64x232x32")
    case `_64x232x64` extends Modifier("_64x232x64")
    case `_64x232x8` extends Modifier("_64x232x8")
    case `_64x240x16` extends Modifier("_64x240x16")
    case `_64x240x256` extends Modifier("_64x240x256")
    case `_64x240x32` extends Modifier("_64x240x32")
    case `_64x240x64` extends Modifier("_64x240x64")
    case `_64x240x8` extends Modifier("_64x240x8")
    case `_64x248x16` extends Modifier("_64x248x16")
    case `_64x248x32` extends Modifier("_64x248x32")
    case `_64x248x64` extends Modifier("_64x248x64")
    case `_64x248x8` extends Modifier("_64x248x8")
    case `_64x24x16` extends Modifier("_64x24x16")
    case `_64x24x256` extends Modifier("_64x24x256")
    case `_64x24x32` extends Modifier("_64x24x32")
    case `_64x24x64` extends Modifier("_64x24x64")
    case `_64x24x8` extends Modifier("_64x24x8")
    case `_64x256x16` extends Modifier("_64x256x16")
    case `_64x256x256` extends Modifier("_64x256x256")
    case `_64x256x32` extends Modifier("_64x256x32")
    case `_64x256x64` extends Modifier("_64x256x64")
    case `_64x256x8` extends Modifier("_64x256x8")
    case `_64x32x16` extends Modifier("_64x32x16")
    case `_64x32x256` extends Modifier("_64x32x256")
    case `_64x32x32` extends Modifier("_64x32x32")
    case `_64x32x64` extends Modifier("_64x32x64")
    case `_64x32x8` extends Modifier("_64x32x8")
    case `_64x40x16` extends Modifier("_64x40x16")
    case `_64x40x32` extends Modifier("_64x40x32")
    case `_64x40x64` extends Modifier("_64x40x64")
    case `_64x40x8` extends Modifier("_64x40x8")
    case `_64x48x16` extends Modifier("_64x48x16")
    case `_64x48x256` extends Modifier("_64x48x256")
    case `_64x48x32` extends Modifier("_64x48x32")
    case `_64x48x64` extends Modifier("_64x48x64")
    case `_64x48x8` extends Modifier("_64x48x8")
    case `_64x56x16` extends Modifier("_64x56x16")
    case `_64x56x32` extends Modifier("_64x56x32")
    case `_64x56x64` extends Modifier("_64x56x64")
    case `_64x56x8` extends Modifier("_64x56x8")
    case `_64x64x16` extends Modifier("_64x64x16")
    case `_64x64x256` extends Modifier("_64x64x256")
    case `_64x64x32` extends Modifier("_64x64x32")
    case `_64x64x64` extends Modifier("_64x64x64")
    case `_64x64x8` extends Modifier("_64x64x8")
    case `_64x72x16` extends Modifier("_64x72x16")
    case `_64x72x32` extends Modifier("_64x72x32")
    case `_64x72x64` extends Modifier("_64x72x64")
    case `_64x72x8` extends Modifier("_64x72x8")
    case `_64x80x16` extends Modifier("_64x80x16")
    case `_64x80x256` extends Modifier("_64x80x256")
    case `_64x80x32` extends Modifier("_64x80x32")
    case `_64x80x64` extends Modifier("_64x80x64")
    case `_64x80x8` extends Modifier("_64x80x8")
    case `_64x88x16` extends Modifier("_64x88x16")
    case `_64x88x32` extends Modifier("_64x88x32")
    case `_64x88x64` extends Modifier("_64x88x64")
    case `_64x88x8` extends Modifier("_64x88x8")
    case `_64x8x16` extends Modifier("_64x8x16")
    case `_64x8x256` extends Modifier("_64x8x256")
    case `_64x8x32` extends Modifier("_64x8x32")
    case `_64x8x64` extends Modifier("_64x8x64")
    case `_64x8x8` extends Modifier("_64x8x8")
    case `_64x96x16` extends Modifier("_64x96x16")
    case `_64x96x256` extends Modifier("_64x96x256")
    case `_64x96x32` extends Modifier("_64x96x32")
    case `_64x96x64` extends Modifier("_64x96x64")
    case `_64x96x8` extends Modifier("_64x96x8")
    case `_7b` extends Modifier("_7b")
    case `_8` extends Modifier("_8")
    case `_848` extends Modifier("_848")
    case `_88128` extends Modifier("_88128")
    case `_8816` extends Modifier("_8816")
    case `_8832` extends Modifier("_8832")
    case `_884` extends Modifier("_884")
    case `_8864` extends Modifier("_8864")
    case `_8b` extends Modifier("_8b")
    case `_8x8x4` extends Modifier("_8x8x4")
    case `_96` extends Modifier("_96")
    case `_9b` extends Modifier("_9b")
    case `__HIR0X001` extends Modifier("__HIR0X001")
    case `__HIR0X00A` extends Modifier("__HIR0X00A")
    case `a` extends Modifier("a")
    case `cx` extends Modifier("cx")
    case `gdesc` extends Modifier("gdesc")
    case `gsb0` extends Modifier("gsb0")
    case `noAVRG` extends Modifier("noAVRG")
    case `noBA` extends Modifier("noBA")
    case `noBREV` extends Modifier("noBREV")
    case `noCL` extends Modifier("noCL")
    case `noDC` extends Modifier("noDC")
    case `noE` extends Modifier("noE")
    case `noFTZ` extends Modifier("noFTZ")
    case `noH_AND` extends Modifier("noH_AND")
    case `noKEEPREFCOUNT` extends Modifier("noKEEPREFCOUNT")
    case `noLC` extends Modifier("noLC")
    case `noLMT` extends Modifier("noLMT")
    case `noLOD` extends Modifier("noLOD")
    case `noMRG` extends Modifier("noMRG")
    case `noMS` extends Modifier("noMS")
    case `noNDV` extends Modifier("noNDV")
    case `noNODEP` extends Modifier("noNODEP")
    case `noP` extends Modifier("noP")
    case `noPO` extends Modifier("noPO")
    case `noPSL` extends Modifier("noPSL")
    case `noPhase` extends Modifier("noPhase")
    case `noRGBA` extends Modifier("noRGBA")
    case `noS` extends Modifier("noS")
    case `noSAT` extends Modifier("noSAT")
    case `noSH` extends Modifier("noSH")
    case `noSKEW` extends Modifier("noSKEW")
    case `noSPIN` extends Modifier("noSPIN")
    case `noTOFF` extends Modifier("noTOFF")
    case `noU` extends Modifier("noU")
    case `noWAIT` extends Modifier("noWAIT")
    case `noX` extends Modifier("noX")
    case `noalign` extends Modifier("noalign")
    case `noaltfmt` extends Modifier("noaltfmt")
    case `noaoffi` extends Modifier("noaoffi")
    case `noashift` extends Modifier("noashift")
    case `noatomsspin` extends Modifier("noatomsspin")
    case `noba` extends Modifier("noba")
    case `nobyte_mask` extends Modifier("nobyte_mask")
    case `nocl` extends Modifier("nocl")
    case `noclear` extends Modifier("noclear")
    case `noclear_dist` extends Modifier("noclear_dist")
    case `noclose` extends Modifier("noclose")
    case `nocond` extends Modifier("nocond")
    case `nocond__div_conv` extends Modifier("nocond__div_conv")
    case `nocounted` extends Modifier("nocounted")
    case `nodc` extends Modifier("nodc")
    case `nodepth` extends Modifier("nodepth")
    case `nodepth_bra` extends Modifier("nodepth_bra")
    case `nodiv` extends Modifier("nodiv")
    case `nodiv__exclusive` extends Modifier("nodiv__exclusive")
    case `nodiv__ndv` extends Modifier("nodiv__ndv")
    case `nodual` extends Modifier("nodual")
    case `noe` extends Modifier("noe")
    case `noex` extends Modifier("noex")
    case `noexclusive` extends Modifier("noexclusive")
    case `noexit_mode` extends Modifier("noexit_mode")
    case `noexp_desc` extends Modifier("noexp_desc")
    case `noexpand16bit` extends Modifier("noexpand16bit")
    case `nof16rm` extends Modifier("nof16rm")
    case `nofillctrl` extends Modifier("nofillctrl")
    case `nofmz` extends Modifier("nofmz")
    case `nofmz_hfma2` extends Modifier("nofmz_hfma2")
    case `noftz` extends Modifier("noftz")
    case `noh_and` extends Modifier("noh_and")
    case `noignore_kill` extends Modifier("noignore_kill")
    case `noim2col` extends Modifier("noim2col")
    case `noisat` extends Modifier("noisat")
    case `nolc` extends Modifier("nolc")
    case `nolodlc` extends Modifier("nolodlc")
    case `nolodlc_tex` extends Modifier("nolodlc_tex")
    case `nomembar_mmio` extends Modifier("nomembar_mmio")
    case `nomembar_sem` extends Modifier("nomembar_sem")
    case `noms` extends Modifier("noms")
    case `nomulticast` extends Modifier("nomulticast")
    case `nonan` extends Modifier("nonan")
    case `nondv` extends Modifier("nondv")
    case `nono_atexit` extends Modifier("nono_atexit")
    case `nonodep` extends Modifier("nonodep")
    case `nonottid0` extends Modifier("nonottid0")
    case `nontz` extends Modifier("nontz")
    case `nooptional_warp` extends Modifier("nooptional_warp")
    case `nooptout` extends Modifier("nooptout")
    case `noorder` extends Modifier("noorder")
    case `nopack` extends Modifier("nopack")
    case `nopquad` extends Modifier("nopquad")
    case `noprivate` extends Modifier("noprivate")
    case `nopseudo_op` extends Modifier("nopseudo_op")
    case `nopseudo_opcode` extends Modifier("nopseudo_opcode")
    case `norand` extends Modifier("norand")
    case `norelu` extends Modifier("norelu")
    case `noreuse` extends Modifier("noreuse")
    case `norm16` extends Modifier("norm16")
    case `nosat` extends Modifier("nosat")
    case `nosatfinite` extends Modifier("nosatfinite")
    case `nosatrelu` extends Modifier("nosatrelu")
    case `nosatrelu_relu` extends Modifier("nosatrelu_relu")
    case `nosatrelu_ui2ip` extends Modifier("nosatrelu_ui2ip")
    case `noscale` extends Modifier("noscale")
    case `nosco` extends Modifier("nosco")
    case `nosco_redas` extends Modifier("nosco_redas")
    case `noseq` extends Modifier("noseq")
    case `noseq_ublkcp` extends Modifier("noseq_ublkcp")
    case `nosh` extends Modifier("nosh")
    case `noskew` extends Modifier("noskew")
    case `nosortidx` extends Modifier("nosortidx")
    case `nosp2` extends Modifier("nosp2")
    case `nosparse` extends Modifier("nosparse")
    case `nosrc_fmt` extends Modifier("nosrc_fmt")
    case `nosyncall` extends Modifier("nosyncall")
    case `nosyncs_mod` extends Modifier("nosyncs_mod")
    case `notexunpack` extends Modifier("notexunpack")
    case `notoff` extends Modifier("notoff")
    case `nouai` extends Modifier("nouai")
    case `nowakeup` extends Modifier("nowakeup")
    case `nowatch` extends Modifier("nowatch")
    case `nows` extends Modifier("nows")
    case `noxor` extends Modifier("noxor")
    case `noxorsign` extends Modifier("noxorsign")
    case `reuse` extends Modifier("reuse")
    case `rf` extends Modifier("rf")
    case `tmem` extends Modifier("tmem")
    case `x1` extends Modifier("x1")
    case `x128` extends Modifier("x128")
    case `x16` extends Modifier("x16")
    case `x2` extends Modifier("x2")
    case `x32` extends Modifier("x32")
    case `x4` extends Modifier("x4")
    case `x64` extends Modifier("x64")
    case `x8` extends Modifier("x8")
    case Vendor(value: NonEmpty[String]) extends Modifier(value)
  }
  case class Guard(predicate: Operand.Predicate) derives CanEqual

  object Definition {
    lazy val all: Vec[Definition] = Vec(
      Definitions0.definitions,
      Definitions1.definitions,
      Definitions2.definitions,
      Definitions3.definitions,
      Definitions4.definitions,
      Definitions5.definitions,
      Definitions6.definitions
    ).flatten
    private lazy val byMnemonic: Map[String, Vec[Definition]] = all.groupBy(_.mnemonic.raw)
    def forMnemonic(mnemonic: String): Vec[Definition] = byMnemonic.getOrElse(mnemonic, Vec.empty)
  }
  class Definition(
      val name: NonEmpty[String],
      val mnemonic: NonEmpty[String],
      val forms: Vec[String]
  ) {
    override final def toString: String = name.raw
  }
  object Definitions0 {
    val AcqbulkNoOperands: Definition = Definition("AcqbulkNoOperands", "ACQBULK", Vec("acqbulk_"))
    val AcqshminitNoOperands: Definition = Definition("AcqshminitNoOperands", "ACQSHMINIT", Vec("acqshminit_"))
    val Al2pPredRegReg: Definition = Definition("Al2pPredRegReg", "AL2P", Vec("AL2P", "I_AL2P"))
    val Al2pPredRegRegImm: Definition = Definition("Al2pPredRegRegImm", "AL2P", Vec("AL2P", "I_AL2P"))
    val Al2pRegReg: Definition = Definition("Al2pRegReg", "AL2P", Vec("AL2P", "I AL2P", "I_AL2P"))
    val Al2pRegRegImm: Definition =
      Definition("Al2pRegRegImm", "AL2P", Vec("AL2P", "I AL2P", "I_AL2P", "al2p__RaNonRZ", "al2p__RaRZ"))
    val AldRegAddressReg: Definition = Definition(
      "AldRegAddressReg",
      "ALD",
      Vec(
        "ALD",
        "ALD PHYS",
        "ALD_PHYS",
        "I ALD",
        "I_ALD",
        "I_ALD_PATCH",
        "ald_PHYS_",
        "ald_UR__LOGICAL_URa_default",
        "ald_UR__PATCH_URa_P_RbRZ",
        "ald__LOGICAL_RaRZ_default",
        "ald__PATCH_RaNonRZOffset_P_RbRZ",
        "ald__PATCH_RaRZ_P_RbRZ"
      )
    )
    val ArrivesAddress: Definition = Definition("ArrivesAddress", "ARRIVES", Vec("arrives_"))
    val AstAddressRegReg: Definition = Definition(
      "AstAddressRegReg",
      "AST",
      Vec(
        "AST",
        "AST PHYS",
        "AST_PHYS",
        "I AST",
        "I_AST",
        "I_AST_PATCH",
        "ast_PHYS_",
        "ast_UR__LOGICAL_URa",
        "ast_UR__PATCH_RaRZ_URa",
        "ast__LOGICAL_RaRZ",
        "ast__PATCH_RaNonRZOffset",
        "ast__PATCH_RaRZ"
      )
    )
    val AtomPredRegAddress: Definition = Definition(
      "AtomPredRegAddress",
      "ATOM",
      Vec(
        "atom_arrive__Ra32_arrive",
        "atom_arrive__Ra32_popcinc",
        "atom_arrive__Ra64_arrive",
        "atom_arrive__Ra64_popcinc",
        "atom_arrive__RaRZ_arrive",
        "atom_arrive__RaRZ_popcinc"
      )
    )
    val AtomPredRegAddressReg: Definition = Definition(
      "AtomPredRegAddressReg",
      "ATOM",
      Vec(
        "ATOM_SPARSE",
        "atom__RaNonRZ",
        "atom__RaRZ",
        "atom_fp__RaNonRZ",
        "atom_fp__RaRZ",
        "atom_fp_uniform__Ra32",
        "atom_fp_uniform__Ra64",
        "atom_fp_uniform__RaRZ",
        "atom_fp_uniform__memdesc",
        "atom_int__RaNonRZ",
        "atom_int__RaRZ",
        "atom_int_uniform__Ra32",
        "atom_int_uniform__Ra64",
        "atom_int_uniform__RaRZ",
        "atom_int_uniform__memdesc",
        "atom_uniform__Ra32",
        "atom_uniform__Ra64",
        "atom_uniform__RaRZ",
        "atom_uniform__memdesc",
        "atom_uniform_weakonly__Ra32",
        "atom_uniform_weakonly__Ra64",
        "atom_uniform_weakonly__RaRZ",
        "atom_weakonly__RaNonRZ",
        "atom_weakonly__RaRZ"
      )
    )
    val AtomPredRegAddressRegReg: Definition = Definition(
      "AtomPredRegAddressRegReg",
      "ATOM",
      Vec(
        "ATOM_CAS_RZ_and_Rc_SPARSE",
        "ATOM_CAS_Rb_and_RZ_SPARSE",
        "ATOM_CAS_SPARSE",
        "ATOM_SPARSE",
        "atom_cas__RaNonRZ_CAS",
        "atom_cas__RaNonRZ_CAST",
        "atom_cas__RaRZ_CAS",
        "atom_cas__RaRZ_CAST",
        "atom_cas_weakonly__RaNonRZ_CAS",
        "atom_cas_weakonly__RaNonRZ_CAST",
        "atom_cas_weakonly__RaRZ_CAS",
        "atom_cas_weakonly__RaRZ_CAST"
      )
    )
    val AtomRegAddressReg: Definition =
      Definition("AtomRegAddressReg", "ATOM", Vec("ATOM", "ATOM Rd is RZ", "I ATOM", "I ATOM Rd is RZ", "I_ATOM"))
    val AtomRegAddressRegReg: Definition = Definition(
      "AtomRegAddressRegReg",
      "ATOM",
      Vec(
        "ATOM",
        "ATOM CAS",
        "ATOM CAS RZ and Rc",
        "ATOM CAS Rb and RZ",
        "ATOM Rd is RZ",
        "ATOM_CAS",
        "ATOM_CAS_RZ_and_Rc",
        "ATOM_CAS_Rb_and_RZ",
        "I ATOM",
        "I ATOM CAS",
        "I ATOM CAS RZ and Rc",
        "I ATOM CAS Rb and RZ",
        "I ATOM Rd is RZ",
        "I_ATOM",
        "I_ATOM_CAS",
        "I_ATOM_CAS_RZ_and_Rc",
        "I_ATOM_CAS_Rb_and_RZ"
      )
    )
    val AtomgPredRegAddressReg: Definition = Definition(
      "AtomgPredRegAddressReg",
      "ATOMG",
      Vec(
        "atomg__RaNonRZ",
        "atomg__RaRZ",
        "atomg_fp__RaNonRZ",
        "atomg_fp__RaRZ",
        "atomg_fp_uniform__Ra32",
        "atomg_fp_uniform__Ra64",
        "atomg_fp_uniform__RaRZ",
        "atomg_fp_uniform__memdesc",
        "atomg_int__RaNonRZ",
        "atomg_int__RaRZ",
        "atomg_int_uniform__Ra32",
        "atomg_int_uniform__Ra64",
        "atomg_int_uniform__RaRZ",
        "atomg_int_uniform__memdesc",
        "atomg_uniform__Ra32",
        "atomg_uniform__Ra64",
        "atomg_uniform__RaRZ",
        "atomg_uniform__memdesc",
        "atomg_uniform_weakonly__Ra32",
        "atomg_uniform_weakonly__Ra64",
        "atomg_uniform_weakonly__RaRZ",
        "atomg_weakonly__RaNonRZ",
        "atomg_weakonly__RaRZ"
      )
    )
    val AtomgPredRegAddressRegReg: Definition = Definition(
      "AtomgPredRegAddressRegReg",
      "ATOMG",
      Vec("atomg_cas__RaNonRZ", "atomg_cas__RaRZ", "atomg_cas_weakonly__RaNonRZ", "atomg_cas_weakonly__RaRZ")
    )
    val AtomsPredAddressRegReg: Definition =
      Definition("AtomsPredAddressRegReg", "ATOMS", Vec("atoms_cast_destPu__RaNonRZ", "atoms_cast_destPu__RaRZ"))
    val AtomsRegAddress: Definition =
      Definition("AtomsRegAddress", "ATOMS", Vec("atoms_arrive__arrive", "atoms_arrive__popcinc"))
    val AtomsRegAddressReg: Definition = Definition(
      "AtomsRegAddressReg",
      "ATOMS",
      Vec("ATOMS", "I_ATOMS", "atoms__RaNonRZ", "atoms__RaRZ", "atoms_uniform_")
    )
    val AtomsRegAddressRegReg: Definition = Definition(
      "AtomsRegAddressRegReg",
      "ATOMS",
      Vec(
        "ATOMS",
        "ATOMS_CAS",
        "ATOMS_CAST",
        "ATOMS_CAST_RZ_and_Rc",
        "ATOMS_CAST_Rb_and_RZ",
        "ATOMS_CAS_RZ_and_Rc",
        "ATOMS_CAS_Rb_and_RZ",
        "I_ATOMS",
        "I_ATOMS_CAS",
        "I_ATOMS_CAST",
        "I_ATOMS_CAST_RZ_and_Rc",
        "I_ATOMS_CAST_Rb_and_RZ",
        "I_ATOMS_CAS_RZ_and_Rc",
        "I_ATOMS_CAS_Rb_and_RZ",
        "atoms_cas__RaNonRZ",
        "atoms_cas__RaNonRZ_CAS",
        "atoms_cas__RaNonRZ_CAST",
        "atoms_cas__RaRZ",
        "atoms_cas__RaRZ_CAS",
        "atoms_cas__RaRZ_CAST",
        "atoms_cast_destRd__RaNonRZ",
        "atoms_cast_destRd__RaRZ"
      )
    )
    val B2rReg: Definition =
      Definition("B2rReg", "B2R", Vec("B2R RESULT", "B2R WARP", "B2R_RESULT", "B2R_WARP", "b2r__RESULT", "b2r__WARP"))
    val B2rRegImm: Definition = Definition("B2rRegImm", "B2R", Vec("B2R BAR", "B2R_BAR", "b2r__BAR"))
    val B2rRegPred: Definition = Definition("B2rRegPred", "B2R", Vec("B2R RESULT", "B2R_RESULT", "b2r__RESULT"))
    val BarNoOperands: Definition = Definition(
      "BarNoOperands",
      "BAR",
      Vec("BAR syncall", "BAR_syncall", "bar__SYNCALL_dfrBlk_noSrc_II", "bar__SYNCALL_noSrc_II")
    )
    val BarImm: Definition = Definition(
      "BarImm",
      "BAR",
      Vec("BAR_Sync_a_b", "a b BAR Sync", "bar__SYNC_II_optionalCount_II", "bar__SYNC_dfrBlk_II_optionalCount_II")
    )
    val BarImmImm: Definition = Definition(
      "BarImmImm",
      "BAR",
      Vec(
        "BAR_Arv_imm_a",
        "BAR_Sync_a_b",
        "a BAR Arv imm",
        "a b BAR Sync",
        "bar__ARV_II_II",
        "bar__SYNC_II_optionalCount_II",
        "bar__SYNC_dfrBlk_II_optionalCount_II"
      )
    )
    val BarImmImmPred: Definition = Definition(
      "BarImmImmPred",
      "BAR",
      Vec(
        "BAR_Red_a_b",
        "BAR_scan_imm_a",
        "a BAR scan imm",
        "a b BAR Red",
        "bar__RED_II_optionalCount_II",
        "bar__RED_dfrBlk_II_optionalCount_II",
        "bar__SCAN_II_II"
      )
    )
    val BarImmPred: Definition = Definition(
      "BarImmPred",
      "BAR",
      Vec("BAR_Red_a_b", "a b BAR Red", "bar__RED_II_optionalCount_II", "bar__RED_dfrBlk_II_optionalCount_II")
    )
    val BarImmReg: Definition = Definition(
      "BarImmReg",
      "BAR",
      Vec(
        "BAR_Arv_a",
        "BAR_Sync_a",
        "a BAR Arv",
        "a BAR Sync",
        "bar__ARV_IR_IR",
        "bar__SYNC_IR_optionalCount_IR",
        "bar__SYNC_dfrBlk_IR_optionalCount_IR"
      )
    )
    val BarImmRegPred: Definition = Definition(
      "BarImmRegPred",
      "BAR",
      Vec(
        "BAR_Red_a",
        "BAR_Scan_a",
        "a BAR Red",
        "a BAR Scan",
        "bar__RED_IR_optionalCount_IR",
        "bar__RED_dfrBlk_IR_optionalCount_IR",
        "bar__SCAN_IR_IR"
      )
    )
    val BarReg: Definition = Definition(
      "BarReg",
      "BAR",
      Vec("BAR_Sync_b", "b BAR Sync", "bar__SYNC_RI_optionalCount_RI", "bar__SYNC_dfrBlk_RI_optionalCount_RI")
    )
    val BarRegImm: Definition = Definition(
      "BarRegImm",
      "BAR",
      Vec(
        "BAR Arv imm",
        "BAR_Arv_imm",
        "BAR_Sync_b",
        "b BAR Sync",
        "bar__ARV_RI_RI",
        "bar__SYNC_RI_optionalCount_RI",
        "bar__SYNC_dfrBlk_RI_optionalCount_RI"
      )
    )
    val BarRegImmPred: Definition = Definition(
      "BarRegImmPred",
      "BAR",
      Vec(
        "BAR scan imm",
        "BAR_Red_b",
        "BAR_scan_imm",
        "b BAR Red",
        "bar__RED_RI_optionalCount_RI",
        "bar__RED_dfrBlk_RI_optionalCount_RI",
        "bar__SCAN_RI_RI"
      )
    )
    val BarRegPred: Definition = Definition(
      "BarRegPred",
      "BAR",
      Vec("BAR_Red_b", "b BAR Red", "bar__RED_RI_optionalCount_RI", "bar__RED_dfrBlk_RI_optionalCount_RI")
    )
    val BarRegReg: Definition = Definition(
      "BarRegReg",
      "BAR",
      Vec("BAR Arv", "BAR Sync", "BAR_Arv", "BAR_Sync", "bar__ARV_RR_RR", "bar__SYNC_RR_RR", "bar__SYNC_dfrBlk_RR_RR")
    )
    val BarRegRegPred: Definition = Definition(
      "BarRegRegPred",
      "BAR",
      Vec("BAR Red", "BAR Scan", "BAR_Red", "BAR_Scan", "bar__RED_RR_RR", "bar__RED_dfrBlk_RR_RR", "bar__SCAN_RR_RR")
    )
    val BfeRegRegConst: Definition = Definition("BfeRegRegConst", "BFE", Vec("Const BFE", "Const_BFE"))
    val BfeRegRegImm: Definition = Definition("BfeRegRegImm", "BFE", Vec("Imm BFE", "Imm_BFE"))
    val BfeRegRegReg: Definition = Definition("BfeRegRegReg", "BFE", Vec("BFE"))
    val BfiRegRegConstReg: Definition = Definition("BfiRegRegConstReg", "BFI", Vec("Const BFI", "Const_BFI"))
    val BfiRegRegImmReg: Definition = Definition("BfiRegRegImmReg", "BFI", Vec("Imm BFI", "Imm_BFI"))
    val BfiRegRegRegConst: Definition = Definition("BfiRegRegRegConst", "BFI", Vec("Const1 BFI", "Const1_BFI"))
    val BfiRegRegRegReg: Definition = Definition("BfiRegRegRegReg", "BFI", Vec("BFI"))
    val BgmmaRegAddressReg: Definition = Definition("BgmmaRegAddressReg", "BGMMA", Vec("bgmma_URa_Rc_"))
    val BgmmaRegAddressRegPred: Definition = Definition("BgmmaRegAddressRegPred", "BGMMA", Vec("bgmma_URa_Rc_"))
    val BgmmaRegAddressRegReg: Definition = Definition("BgmmaRegAddressRegReg", "BGMMA", Vec("bgmma_URa_Rb_Rc_"))
    val BgmmaRegAddressRegRegPred: Definition =
      Definition("BgmmaRegAddressRegRegPred", "BGMMA", Vec("bgmma_URa_Rb_Rc_"))
    val BgmmaRegRegAddressReg: Definition = Definition("BgmmaRegRegAddressReg", "BGMMA", Vec("bgmma_Ra_URb_Rc_"))
    val BgmmaRegRegAddressRegPred: Definition =
      Definition("BgmmaRegRegAddressRegPred", "BGMMA", Vec("bgmma_Ra_URb_Rc_"))
    val BmmaRegRegRegReg: Definition = Definition("BmmaRegRegRegReg", "BMMA", Vec("bmma_", "bmma_88128_"))
    val BmmaRegRegRegRegPred: Definition = Definition("BmmaRegRegRegRegPred", "BMMA", Vec("bmma_"))
    val BmovAddressImm: Definition = Definition("BmovAddressImm", "BMOV", Vec("bmov_dst64__CX"))
    val BmovConst: Definition = Definition("BmovConst", "BMOV", Vec("bmov_dst64__C"))
    val BmovImm: Definition = Definition("BmovImm", "BMOV", Vec("bmov_dst64__I"))
    val BmovReg: Definition = Definition("BmovReg", "BMOV", Vec("bmov_clear_bd__Bd", "bmov_dst64__R", "bmov_dst64__UR"))
    val BmovRegAddressImm: Definition = Definition("BmovRegAddressImm", "BMOV", Vec("bmov_pquad__RCXR"))
    val BmovRegConst: Definition = Definition("BmovRegConst", "BMOV", Vec("bmov_pquad__RCR"))
    val BmovRegImm: Definition = Definition("BmovRegImm", "BMOV", Vec("bmov_pquad__RIR"))
    val BmovRegReg: Definition = Definition(
      "BmovRegReg",
      "BMOV",
      Vec("bmov_clear__Rd", "bmov_clear_barrier_", "bmov_pquad__RRR", "bmov_pquad__RUR", "bmov_pquad_bar__RBR")
    )
    val BmovRegSymbol: Definition = Definition("BmovRegSymbol", "BMOV", Vec("bmov_clear__Rd"))
    val BmovSymbolAddressImm: Definition = Definition("BmovSymbolAddressImm", "BMOV", Vec("bmov_pquad__RCXR"))
    val BmovSymbolConst: Definition = Definition("BmovSymbolConst", "BMOV", Vec("bmov_pquad__RCR"))
    val BmovSymbolImm: Definition = Definition("BmovSymbolImm", "BMOV", Vec("bmov_pquad__RIR"))
    val BmovSymbolReg: Definition =
      Definition("BmovSymbolReg", "BMOV", Vec("bmov_pquad__RRR", "bmov_pquad__RUR", "bmov_pquad_bar__RBR"))
    val BmskRegRegAddressImm: Definition = Definition("BmskRegRegAddressImm", "BMSK", Vec("bmsk__RCxR_RCxR"))
    val BmskRegRegConst: Definition = Definition("BmskRegRegConst", "BMSK", Vec("bmsk__RCR_RCR"))
    val BmskRegRegImm: Definition = Definition("BmskRegRegImm", "BMSK", Vec("bmsk__RuIR_RIR"))
    val BmskRegRegReg: Definition = Definition("BmskRegRegReg", "BMSK", Vec("bmsk__RRR_RRR", "bmsk__RUR_RUR"))
    val BptNoOperands: Definition =
      Definition("BptNoOperands", "BPT", Vec("BPT", "bpt__WAIT", "bpt__noDRAIN", "bpt__noWAIT", "bpt__onlyDRAIN"))
    val BptImm: Definition =
      Definition("BptImm", "BPT", Vec("BPT", "bpt__WAIT", "bpt__noDRAIN", "bpt__noWAIT", "bpt__onlyDRAIN"))
    val BraConst: Definition = Definition("BraConst", "BRA", Vec("BRA_c"))
    val BraImm: Definition = Definition(
      "BraImm",
      "BRA",
      Vec("BRA", "bra_", "bra__CONV_DIV", "bra__U", "bra_rel_", "bra_rel__CONV_DIV", "bra_rel__U")
    )
    val BraPredImm: Definition = Definition(
      "BraPredImm",
      "BRA",
      Vec(
        "bra_",
        "bra__CONV_DIV",
        "bra__U",
        "bra_rel_",
        "bra_rel__CONV_DIV",
        "bra_rel__U",
        "bra_uniform_pred_",
        "bra_uniform_pred_rel_"
      )
    )
    val BraPredPredImm: Definition =
      Definition("BraPredPredImm", "BRA", Vec("bra_uniform_pred_", "bra_uniform_pred_rel_"))
    val BraPredRegImm: Definition = Definition("BraPredRegImm", "BRA", Vec("bra_uniform_", "bra_uniform_rel_"))
    val BraRegImm: Definition = Definition("BraRegImm", "BRA", Vec("bra_uniform_", "bra_uniform_rel_"))
    val BraSymbolConst: Definition = Definition("BraSymbolConst", "BRA", Vec("BRA_c"))
    val BraSymbolImm: Definition = Definition("BraSymbolImm", "BRA", Vec("BRA"))
    val BraSymbolImmImm: Definition = Definition("BraSymbolImmImm", "BRA", Vec("BRA c"))
    val BraSymbolSymbolImmImm: Definition = Definition("BraSymbolSymbolImmImm", "BRA", Vec("BRA c"))
    val BreakPredReg: Definition = Definition("BreakPredReg", "BREAK", Vec("break_inst_", "break_reliability_"))
    val BreakReg: Definition = Definition("BreakReg", "BREAK", Vec("break_inst_", "break_reliability_"))
    val BrevRegAddressImm: Definition = Definition("BrevRegAddressImm", "BREV", Vec("brev__RCxR_RCxR"))
    val BrevRegConst: Definition = Definition("BrevRegConst", "BREV", Vec("brev__RCR_RCR"))
    val BrevRegImm: Definition = Definition("BrevRegImm", "BREV", Vec("brev__RuIR_RIR"))
    val BrevRegReg: Definition = Definition("BrevRegReg", "BREV", Vec("brev__RRR_RRR", "brev__RUR_RUR"))
    val BrkNoOperands: Definition = Definition("BrkNoOperands", "BRK", Vec("BRK"))
    val BrxConst: Definition = Definition("BrxConst", "BRX", Vec("BRX_c"))
    val BrxPredReg: Definition = Definition("BrxPredReg", "BRX", Vec("brx_", "brx_rel_"))
    val BrxPredRegImm: Definition = Definition("BrxPredRegImm", "BRX", Vec("brx_", "brx_rel_"))
    val BrxReg: Definition = Definition("BrxReg", "BRX", Vec("BRX", "brx_", "brx_rel_"))
    val BrxRegImm: Definition = Definition("BrxRegImm", "BRX", Vec("BRX", "brx_", "brx_rel_"))
    val BrxSymbolConst: Definition = Definition("BrxSymbolConst", "BRX", Vec("BRX_c"))
    val BrxSymbolImmAddress: Definition = Definition("BrxSymbolImmAddress", "BRX", Vec("BRX c"))
    val BrxSymbolReg: Definition = Definition("BrxSymbolReg", "BRX", Vec("BRX"))
    val BrxSymbolRegImm: Definition = Definition("BrxSymbolRegImm", "BRX", Vec("BRX"))
    val BrxSymbolSymbolImmAddress: Definition = Definition("BrxSymbolSymbolImmAddress", "BRX", Vec("BRX c"))
    val BrxuPredReg: Definition = Definition("BrxuPredReg", "BRXU", Vec("brxu_", "brxu_rel_"))
    val BrxuPredRegImm: Definition = Definition("BrxuPredRegImm", "BRXU", Vec("brxu_", "brxu_rel_"))
    val BrxuReg: Definition = Definition("BrxuReg", "BRXU", Vec("brxu_", "brxu_rel_"))
    val BrxuRegImm: Definition = Definition("BrxuRegImm", "BRXU", Vec("brxu_", "brxu_rel_"))
    val BssyPredRegImm: Definition =
      Definition("BssyPredRegImm", "BSSY", Vec("bssy_", "bssy_rel_", "bssy_reliability_", "bssy_reliability_rel_"))
    val BssyRegImm: Definition =
      Definition("BssyRegImm", "BSSY", Vec("bssy_", "bssy_rel_", "bssy_reliability_", "bssy_reliability_rel_"))
    val BsyncPredReg: Definition = Definition("BsyncPredReg", "BSYNC", Vec("bsync_", "bsync_reliability_"))
    val BsyncReg: Definition = Definition("BsyncReg", "BSYNC", Vec("bsync_", "bsync_reliability_"))
    val CalConst: Definition = Definition("CalConst", "CAL", Vec("CAL c", "CAL_c"))
    val CalImm: Definition = Definition("CalImm", "CAL", Vec("CAL"))
    val CallConst: Definition = Definition("CallConst", "CALL", Vec("call_abs__RCR"))
    val CallImm: Definition = Definition("CallImm", "CALL", Vec("call_abs__RIR", "call_rel__RIR", "call_rel_imm__RIR"))
    val CallPredConst: Definition = Definition("CallPredConst", "CALL", Vec("call_abs__RCR"))
    val CallPredImm: Definition =
      Definition("CallPredImm", "CALL", Vec("call_abs__RIR", "call_rel__RIR", "call_rel_imm__RIR"))
    val CallPredReg: Definition = Definition(
      "CallPredReg",
      "CALL",
      Vec("call_abs__RRR", "call_abs__URIR", "call_rel_reg_", "call_rel_reg__RRR", "call_rel_reg__URIR")
    )
    val CallPredRegImm: Definition = Definition(
      "CallPredRegImm",
      "CALL",
      Vec(
        "call_abs__RRR",
        "call_abs__URIR",
        "call_rel__RRR",
        "call_rel__URIR",
        "call_rel_imm__RRR",
        "call_rel_imm__URIR"
      )
    )
    val CallReg: Definition = Definition(
      "CallReg",
      "CALL",
      Vec("call_abs__RRR", "call_abs__URIR", "call_rel_reg_", "call_rel_reg__RRR", "call_rel_reg__URIR")
    )
    val CallRegImm: Definition = Definition(
      "CallRegImm",
      "CALL",
      Vec(
        "call_abs__RRR",
        "call_abs__URIR",
        "call_rel__RRR",
        "call_rel__URIR",
        "call_rel_imm__RRR",
        "call_rel_imm__URIR"
      )
    )
    val CctlNoOperands: Definition = Definition(
      "CctlNoOperands",
      "CCTL",
      Vec(
        "CCTL IVALL",
        "CCTL_CI_IVALL",
        "CCTL_IVALL",
        "cctl__IVALL_WBALL_C_noSrc",
        "cctl__IVALL_WBALL_D_U_noSrc",
        "cctl__IVALL_WBALL_I_noSrc",
        "cctl_c_ivall_wball_nosrc_",
        "cctl_c_ldc_ivall_",
        "cctl_c_ldcu_ivall_",
        "cctl_i_ivall_wball_nosrc_"
      )
    )
    val CctlAddress: Definition = Definition(
      "CctlAddress",
      "CCTL",
      Vec(
        "CCTL",
        "CCTL QRY",
        "CCTL_QRY",
        "I CCTL",
        "I CCTL QRY",
        "I_CCTL",
        "I_CCTL_QRY",
        "cctl__sImmOffset",
        "cctl__sImmOffset_pf2",
        "cctl__sImmOffset_rml2",
        "cctl__sUROffset",
        "cctl__sUROffset_pf2",
        "cctl__sUROffset_rml2",
        "cctl__uImmOffset",
        "cctl__uImmOffset_pf2",
        "cctl__uImmOffset_rml2",
        "cctl__uUROffset",
        "cctl__uUROffset_pf2",
        "cctl__uUROffset_rml2",
        "cctl_c_ldc_va_",
        "cctl_c_ldcu_va_"
      )
    )
    val CctlAddressAddress: Definition =
      Definition("CctlAddressAddress", "CCTL", Vec("cctl_c_ldc_const_bindless_", "cctl_c_ldcu_const_bindless_"))
    val CctlAddressImm: Definition = Definition(
      "CctlAddressImm",
      "CCTL",
      Vec("cctl__sImmOffset_rml2", "cctl__sUROffset_rml2", "cctl__uImmOffset_rml2", "cctl__uUROffset_rml2")
    )
    val CctlConst: Definition =
      Definition("CctlConst", "CCTL", Vec("cctl_c_ldc_const_bound_", "cctl_c_ldcu_const_bound_"))
    val CctlPredAddress: Definition = Definition(
      "CctlPredAddress",
      "CCTL",
      Vec("cctl__sImmOffset_pf2_q", "cctl__sUROffset_pf2_q", "cctl__uImmOffset_pf2_q", "cctl__uUROffset_pf2_q")
    )
    val CctlRegAddress: Definition =
      Definition("CctlRegAddress", "CCTL", Vec("CCTL QRY", "CCTL_QRY", "I CCTL QRY", "I_CCTL_QRY"))
    val CctllNoOperands: Definition = Definition(
      "CctllNoOperands",
      "CCTLL",
      Vec("CCTLL NoOperands", "CCTLL_CRS_WBALL", "CCTLL_IVALL", "cctll__IVALL_WBALL_D_U_noSrc")
    )
    val CctllAddress: Definition = Definition(
      "CctllAddress",
      "CCTLL",
      Vec(
        "CCTLL",
        "CCTLL QRY",
        "I CCTLL",
        "I CCTLL QRY",
        "I_CCTLL",
        "cctll__Ra_RZ_UR",
        "cctll__Ra_nonRz_UR",
        "cctll__sImmOffset",
        "cctll__uImmOffset"
      )
    )
    val CctllRegAddress: Definition = Definition("CctllRegAddress", "CCTLL", Vec("CCTLL QRY", "I CCTLL QRY"))
    val CctlsNoOperands: Definition = Definition("CctlsNoOperands", "CCTLS", Vec("CCTLS"))
    val CctltNoOperands: Definition =
      Definition("CctltNoOperands", "CCTLT", Vec("CCTLT", "CCTLT_IVALL", "cctlt__IVALL"))
    val CctltImm: Definition = Definition("CctltImm", "CCTLT", Vec("CCTLT_IDX"))
    val CctltImmImm: Definition = Definition("CctltImmImm", "CCTLT", Vec("cctlt__const"))
    val CctltReg: Definition = Definition("CctltReg", "CCTLT", Vec("CCTLT_REG", "cctlt__Rb", "cctlt__URb"))
    val CgaerrbarNoOperands: Definition = Definition("CgaerrbarNoOperands", "CGAERRBAR", Vec("cgaerrbar_"))
    val ClmadRegRegAddressImmReg: Definition = Definition("ClmadRegRegAddressImmReg", "CLMAD", Vec("clmad__RCxR_RCxR"))
    val ClmadRegRegConstReg: Definition = Definition("ClmadRegRegConstReg", "CLMAD", Vec("clmad__RCR_RCR"))
    val ClmadRegRegRegAddressImm: Definition = Definition("ClmadRegRegRegAddressImm", "CLMAD", Vec("clmad__RRCx_RRCx"))
    val ClmadRegRegRegConst: Definition = Definition("ClmadRegRegRegConst", "CLMAD", Vec("clmad__RRC_RRC"))
    val ClmadRegRegRegReg: Definition =
      Definition("ClmadRegRegRegReg", "CLMAD", Vec("clmad__RRR_RRR", "clmad__RRU_RRU", "clmad__RUR_RUR"))
    val ContNoOperands: Definition = Definition("ContNoOperands", "CONT", Vec("BRK", "CONT"))
    val CreduxRegReg: Definition = Definition("CreduxRegReg", "CREDUX", Vec("credux_"))
    val Cs2rRegSpecial: Definition = Definition("Cs2rRegSpecial", "CS2R", Vec("CS2R", "cs2r_"))
    val Cs2urRegSpecial: Definition = Definition("Cs2urRegSpecial", "CS2UR", Vec("cs2ur_"))
    val CsetRegSymbol: Definition = Definition("CsetRegSymbol", "CSET", Vec("NoBop CSET", "NoBop_CSET"))
    val CsetRegSymbolPred: Definition = Definition("CsetRegSymbolPred", "CSET", Vec("CSET"))
    val CsetpPredPredSymbolPred: Definition = Definition("CsetpPredPredSymbolPred", "CSETP", Vec("CSETP"))
    val CsetpPredSymbol: Definition = Definition("CsetpPredSymbol", "CSETP", Vec("NoBop CSETP", "NoBop_CSETP"))
    val CsmtestImm: Definition = Definition("CsmtestImm", "CSMTEST", Vec("csmtest_"))
    val CsmtestPredImm: Definition = Definition("CsmtestPredImm", "CSMTEST", Vec("csmtest_cmp_"))
    val CsmtestPredPredImmPred: Definition = Definition("CsmtestPredPredImmPred", "CSMTEST", Vec("csmtest_bop_"))
    val DaddRegRegAddressImm: Definition = Definition("DaddRegRegAddressImm", "DADD", Vec("dadd__RRCx_RCx"))
    val DaddRegRegConst: Definition =
      Definition("DaddRegRegConst", "DADD", Vec("Const DADD", "Const_DADD", "dadd__RRC_RC"))
    val DaddRegRegImm: Definition = Definition("DaddRegRegImm", "DADD", Vec("Imm DADD", "Imm_DADD", "dadd__RRsI_RI"))
    val DaddRegRegReg: Definition = Definition("DaddRegRegReg", "DADD", Vec("DADD", "dadd__RRR_RR", "dadd__RRU_RU"))
    val DchkPredRegConst: Definition = Definition("DchkPredRegConst", "DCHK", Vec("Const DCHK"))
    val DchkPredRegImm: Definition = Definition("DchkPredRegImm", "DCHK", Vec("Imm DCHK"))
    val DchkPredRegReg: Definition = Definition("DchkPredRegReg", "DCHK", Vec("DCHK"))
    val DepbarNoOperands: Definition =
      Definition("DepbarNoOperands", "DEPBAR", Vec("DEPBAR", "DEPBAR_ALL", "depbar__noLE", "depbar_all_"))
    val DepbarGroup: Definition = Definition("DepbarGroup", "DEPBAR", Vec("DEPBAR", "depbar__noLE"))
    val DepbarImm: Definition = Definition("DepbarImm", "DEPBAR", Vec("DEPBAR", "depbar__noLE"))
    val DepbarRegImm: Definition = Definition("DepbarRegImm", "DEPBAR", Vec("DEPBAR_LE", "depbar__LE"))
    val DepbarRegImmGroup: Definition = Definition("DepbarRegImmGroup", "DEPBAR", Vec("DEPBAR_LE", "depbar__LE"))
    val DepbarRegImmImm: Definition = Definition("DepbarRegImmImm", "DEPBAR", Vec("DEPBAR_LE", "depbar__LE"))
    val DepbarRegReg: Definition = Definition("DepbarRegReg", "DEPBAR", Vec("depbar_ur_"))
    val DepbarRegRegGroup: Definition = Definition("DepbarRegRegGroup", "DEPBAR", Vec("depbar_ur_"))
    val DepbarRegRegImm: Definition = Definition("DepbarRegRegImm", "DEPBAR", Vec("depbar_ur_"))
    val DfmaRegRegAddressImmReg: Definition = Definition("DfmaRegRegAddressImmReg", "DFMA", Vec("dfma__RCxR_RCxR"))
    val DfmaRegRegConstReg: Definition =
      Definition("DfmaRegRegConstReg", "DFMA", Vec("Const DFMA", "Const_DFMA", "dfma__RCR_RCR"))
    val DfmaRegRegImmReg: Definition =
      Definition("DfmaRegRegImmReg", "DFMA", Vec("Imm DFMA", "Imm_DFMA", "dfma__RsIR_RIR"))
    val DfmaRegRegRegAddressImm: Definition = Definition("DfmaRegRegRegAddressImm", "DFMA", Vec("dfma__RRCx_RRCx"))
    val DfmaRegRegRegConst: Definition =
      Definition("DfmaRegRegRegConst", "DFMA", Vec("Const1 DFMA", "Const1_DFMA", "dfma__RRC_RRC"))
    val DfmaRegRegRegImm: Definition = Definition("DfmaRegRegRegImm", "DFMA", Vec("dfma__RRsI_RRI"))
    val DfmaRegRegRegReg: Definition =
      Definition("DfmaRegRegRegReg", "DFMA", Vec("DFMA", "dfma__RRR_RRR", "dfma__RRU_RRU", "dfma__RUR_RUR"))
    val DmmaRegRegRegReg: Definition = Definition("DmmaRegRegRegReg", "DMMA", Vec("dmma_"))
    val DmmaRegRegRegRegPred: Definition = Definition("DmmaRegRegRegRegPred", "DMMA", Vec("dmma_"))
    val DmnmxRegRegConstPred: Definition =
      Definition("DmnmxRegRegConstPred", "DMNMX", Vec("Const DMNMX", "Const_DMNMX"))
    val DmnmxRegRegImmPred: Definition = Definition("DmnmxRegRegImmPred", "DMNMX", Vec("Imm DMNMX", "Imm_DMNMX"))
    val DmnmxRegRegRegPred: Definition = Definition("DmnmxRegRegRegPred", "DMNMX", Vec("DMNMX"))
    val DmulRegRegAddressImm: Definition = Definition("DmulRegRegAddressImm", "DMUL", Vec("dmul__RCxR_RCx"))
    val DmulRegRegConst: Definition =
      Definition("DmulRegRegConst", "DMUL", Vec("Const DMUL", "Const_DMUL", "dmul__RCR_RC"))
    val DmulRegRegImm: Definition = Definition("DmulRegRegImm", "DMUL", Vec("Imm DMUL", "Imm_DMUL", "dmul__RsIR_RI"))
    val DmulRegRegReg: Definition = Definition("DmulRegRegReg", "DMUL", Vec("DMUL", "dmul__RRR_RR", "dmul__RUR_RU"))
    val DsetRegRegConst: Definition = Definition("DsetRegRegConst", "DSET", Vec("NoBop Const DSET", "NoBop_Const_DSET"))
    val DsetRegRegConstPred: Definition = Definition("DsetRegRegConstPred", "DSET", Vec("Const DSET", "Const_DSET"))
    val DsetRegRegImm: Definition = Definition("DsetRegRegImm", "DSET", Vec("NoBop Imm DSET", "NoBop_Imm_DSET"))
    val DsetRegRegImmPred: Definition = Definition("DsetRegRegImmPred", "DSET", Vec("Imm DSET", "Imm_DSET"))
    val DsetRegRegReg: Definition = Definition("DsetRegRegReg", "DSET", Vec("NoBop DSET", "NoBop_DSET"))
    val DsetRegRegRegPred: Definition = Definition("DsetRegRegRegPred", "DSET", Vec("DSET"))
    val DsetpPredPredRegAddressImmPred: Definition =
      Definition("DsetpPredPredRegAddressImmPred", "DSETP", Vec("dsetp__RRCx_RCx"))
    val DsetpPredPredRegConstPred: Definition =
      Definition("DsetpPredPredRegConstPred", "DSETP", Vec("Const DSETP", "Const_DSETP", "dsetp__RRC_RC"))
    val DsetpPredPredRegImmPred: Definition =
      Definition("DsetpPredPredRegImmPred", "DSETP", Vec("Imm DSETP", "Imm_DSETP", "dsetp__RRsI_RI"))
    val DsetpPredPredRegRegPred: Definition =
      Definition("DsetpPredPredRegRegPred", "DSETP", Vec("DSETP", "dsetp__RRR_RR", "dsetp__RRU_RU"))
    val DsetpPredRegAddressImm: Definition =
      Definition("DsetpPredRegAddressImm", "DSETP", Vec("dsetp_simple__RRCx_RCx"))
    val DsetpPredRegConst: Definition =
      Definition("DsetpPredRegConst", "DSETP", Vec("NoBop Const DSETP", "NoBop_Const_DSETP", "dsetp_simple__RRC_RC"))
    val DsetpPredRegImm: Definition =
      Definition("DsetpPredRegImm", "DSETP", Vec("NoBop Imm DSETP", "NoBop_Imm_DSETP", "dsetp_simple__RRsI_RI"))
    val DsetpPredRegReg: Definition = Definition(
      "DsetpPredRegReg",
      "DSETP",
      Vec("NoBop DSETP", "NoBop_DSETP", "dsetp_simple__RRR_RR", "dsetp_simple__RRU_RU")
    )
    val ElectPredReg: Definition = Definition("ElectPredReg", "ELECT", Vec("elect_noURa_"))
    val ElectPredRegPred: Definition = Definition("ElectPredRegPred", "ELECT", Vec("elect_Pp_"))
    val ElectPredRegReg: Definition = Definition("ElectPredRegReg", "ELECT", Vec("elect_"))
    val EndcollectiveNoOperands: Definition =
      Definition("EndcollectiveNoOperands", "ENDCOLLECTIVE", Vec("endcollective_"))
    val EndcollectivePred: Definition = Definition("EndcollectivePred", "ENDCOLLECTIVE", Vec("endcollective_"))
    val ErrbarNoOperands: Definition = Definition("ErrbarNoOperands", "ERRBAR", Vec("errbar_"))
    val ExitNoOperands: Definition = Definition("ExitNoOperands", "EXIT", Vec("EXIT", "exit_"))
    val ExitPred: Definition = Definition("ExitPred", "EXIT", Vec("exit_"))
    val F2fRegAddressImm: Definition = Definition(
      "F2fRegAddressImm",
      "F2F",
      Vec(
        "f2f_f32_downconvert__RCXR_RCXR",
        "f2f_f32_upconvert__RCXR_RCXR",
        "f2f_f32_upconvert_swap__RCXR_RCXR",
        "f2f_f64_downconvert__RCXR_RCXR",
        "f2f_f64_upconvert__R_CX16_R_RCXR",
        "f2f_f64_upconvert__R_CX32_R_RCXR",
        "f2f_f64_upconvert_swap__R_CX16_R_RCXR",
        "f2f_f64_upconvert_swap__R_CX32_R_RCXR"
      )
    )
    val F2fRegConst: Definition = Definition(
      "F2fRegConst",
      "F2F",
      Vec(
        "Const F2F 1",
        "Const F2F 1 16",
        "Const F2F 1 64",
        "Const F2F 2",
        "Const F2F 2 16",
        "Const F2F 2 64",
        "Const_F2F_1",
        "Const_F2F_1_16",
        "Const_F2F_1_64",
        "Const_F2F_2",
        "Const_F2F_2_16",
        "Const_F2F_2_64",
        "Const_F2F_2_64_16",
        "Const_F2F_2_64_32",
        "f2f_f32_downconvert__RCR_RCR",
        "f2f_f32_upconvert__RCR_RCR",
        "f2f_f32_upconvert_swap__RCR_RCR",
        "f2f_f64_downconvert__RCR_RCR",
        "f2f_f64_upconvert__R_C16_R_RCR",
        "f2f_f64_upconvert__R_C32_R_RCR",
        "f2f_f64_upconvert_swap__R_C16_R_RCR",
        "f2f_f64_upconvert_swap__R_C32_R_RCR"
      )
    )
    val F2fRegImm: Definition = Definition(
      "F2fRegImm",
      "F2F",
      Vec(
        "Imm F2F 1",
        "Imm F2F 1 16",
        "Imm F2F 1 64",
        "Imm F2F 2",
        "Imm F2F 2 16",
        "Imm F2F 2 64",
        "Imm_F2F_1",
        "Imm_F2F_1_16",
        "Imm_F2F_1_64",
        "Imm_F2F_2",
        "Imm_F2F_2_16",
        "Imm_F2F_2_64",
        "Imm_F2F_2_64_16",
        "Imm_F2F_2_64_32",
        "f2f_f32_downconvert__RIR_RIR",
        "f2f_f32_upconvert__RIR_RIR",
        "f2f_f32_upconvert_swap__RIR_RIR",
        "f2f_f64_downconvert__RIR_RIR",
        "f2f_f64_upconvert__R_32I_R_RIR",
        "f2f_f64_upconvert__R_I16_R_RIR",
        "f2f_f64_upconvert_swap__R_32I_R_RIR",
        "f2f_f64_upconvert_swap__R_I16_R_RIR"
      )
    )
    val F2fRegReg: Definition = Definition(
      "F2fRegReg",
      "F2F",
      Vec(
        "F2F 1",
        "F2F 1 16",
        "F2F 1 64",
        "F2F 2",
        "F2F 2 16",
        "F2F 2 64",
        "F2F_1",
        "F2F_1_16",
        "F2F_1_64",
        "F2F_2",
        "F2F_2_16",
        "F2F_2_64",
        "F2F_2_64_16",
        "F2F_2_64_32",
        "f2f_f32_downconvert__RRR_RRR",
        "f2f_f32_downconvert__RUR_RUR",
        "f2f_f32_upconvert__RRR_RRR",
        "f2f_f32_upconvert__RUR_RUR",
        "f2f_f32_upconvert_swap__RRR_RRR",
        "f2f_f32_upconvert_swap__RUR_RUR",
        "f2f_f64_downconvert__RRR_RRR",
        "f2f_f64_downconvert__RUR_RUR",
        "f2f_f64_upconvert__R_R16_R_RRR",
        "f2f_f64_upconvert__R_R32_R_RRR",
        "f2f_f64_upconvert__R_UR16_R_RUR",
        "f2f_f64_upconvert__R_UR32_R_RUR",
        "f2f_f64_upconvert_swap__R_R16_R_RRR",
        "f2f_f64_upconvert_swap__R_R32_R_RRR",
        "f2f_f64_upconvert_swap__R_UR16_R_RUR",
        "f2f_f64_upconvert_swap__R_UR32_R_RUR"
      )
    )
    val F2fpRegAddressImm: Definition = Definition(
      "F2fpRegAddressImm",
      "F2FP",
      Vec("f2fp_8b_upconvert__RCxR", "f2fp_tf32__RCxR", "f2fp_tf32_default_src__RCxR")
    )
    val F2fpRegAddressImmReg: Definition = Definition(
      "F2fpRegAddressImmReg",
      "F2FP",
      Vec("f2fp_f16_to_8b_downconvert__RCxR", "f2fp_merge_c__RCxR", "f2fp_merge_c_default_dst_src__RCxR")
    )
    val F2fpRegConst: Definition =
      Definition("F2fpRegConst", "F2FP", Vec("f2fp_8b_upconvert__RCR", "f2fp_tf32__RCR", "f2fp_tf32_default_src__RCR"))
    val F2fpRegConstReg: Definition = Definition(
      "F2fpRegConstReg",
      "F2FP",
      Vec("f2fp_f16_to_8b_downconvert__RCR", "f2fp_merge_c__RCR", "f2fp_merge_c_default_dst_src__RCR")
    )
    val F2fpRegImm: Definition = Definition(
      "F2fpRegImm",
      "F2FP",
      Vec(
        "f2fp_4b_upconvert__RIR",
        "f2fp_8b_upconvert__RIR",
        "f2fp_E8_upconvert__RIR",
        "f2fp_tf32__RIR",
        "f2fp_tf32_default_src__RIR"
      )
    )
    val F2fpRegImmReg: Definition = Definition(
      "F2fpRegImmReg",
      "F2FP",
      Vec(
        "f2fp_4b_upconvert_scale__RIR",
        "f2fp_8b_upconvert_scale__RIR",
        "f2fp_f16_to_4b_downconvert__RIR",
        "f2fp_f16_to_8b_downconvert__RIR",
        "f2fp_f16_to_mx8_downconvert_scale__RIR",
        "f2fp_merge_c__RIR",
        "f2fp_merge_c_default_dst_src__RIR"
      )
    )
    val F2fpRegReg: Definition = Definition(
      "F2fpRegReg",
      "F2FP",
      Vec(
        "f2fp_4b_upconvert__RRR",
        "f2fp_4b_upconvert__RUR",
        "f2fp_8b_upconvert__RRR",
        "f2fp_8b_upconvert__RUR",
        "f2fp_E8_upconvert__RRR",
        "f2fp_E8_upconvert__RUR",
        "f2fp_tf32__RRR",
        "f2fp_tf32__RUR",
        "f2fp_tf32_default_src__RRR",
        "f2fp_tf32_default_src__RUR"
      )
    )
    val F2fpRegRegAddressImm: Definition = Definition(
      "F2fpRegRegAddressImm",
      "F2FP",
      Vec(
        "f2fp__RCxR",
        "f2fp_default_dst_src__RCxR",
        "f2fp_f16_to_8b_downconvert__RRCx",
        "f2fp_merge_c__RRCx",
        "f2fp_merge_c_default_dst_src__RRCx"
      )
    )
    val F2fpRegRegAddressImmReg: Definition =
      Definition("F2fpRegRegAddressImmReg", "F2FP", Vec("f2fp_f32_to_8b_downconvert__RCxR", "f2fp_rs__RCxR"))
    val F2fpRegRegConst: Definition = Definition(
      "F2fpRegRegConst",
      "F2FP",
      Vec(
        "f2fp__RCR",
        "f2fp_default_dst_src__RCR",
        "f2fp_f16_to_8b_downconvert__RRC",
        "f2fp_merge_c__RRC",
        "f2fp_merge_c_default_dst_src__RRC"
      )
    )
    val F2fpRegRegConstReg: Definition =
      Definition("F2fpRegRegConstReg", "F2FP", Vec("f2fp_f32_to_8b_downconvert__RCR", "f2fp_rs__RCR"))
    val F2fpRegRegImm: Definition = Definition(
      "F2fpRegRegImm",
      "F2FP",
      Vec(
        "f2fp_4b_upconvert_scale__RRI",
        "f2fp_8b_upconvert_scale__RRI",
        "f2fp__RIR",
        "f2fp_default_dst_src__RIR",
        "f2fp_f16_to_4b_downconvert__RRI",
        "f2fp_f16_to_8b_downconvert__RRI",
        "f2fp_f16_to_mx8_downconvert_scale__RRI",
        "f2fp_merge_c__RRI",
        "f2fp_merge_c_default_dst_src__RRI"
      )
    )
    val F2fpRegRegImmReg: Definition = Definition(
      "F2fpRegRegImmReg",
      "F2FP",
      Vec(
        "f2fp_f32_to_4b_downconvert__RIR",
        "f2fp_f32_to_8b_downconvert__RIR",
        "f2fp_f32_to_mx8_downconvert_scale__RIR",
        "f2fp_rs_16b__RIR",
        "f2fp_rs_8b4b__RIR",
        "f2fp_rs__RIR"
      )
    )
    val F2fpRegRegReg: Definition = Definition(
      "F2fpRegRegReg",
      "F2FP",
      Vec(
        "f2fp_4b_upconvert_scale__RRR",
        "f2fp_4b_upconvert_scale__RRU",
        "f2fp_4b_upconvert_scale__RUR",
        "f2fp_8b_upconvert_scale__RRR",
        "f2fp_8b_upconvert_scale__RRU",
        "f2fp_8b_upconvert_scale__RUR",
        "f2fp__RRR",
        "f2fp__RUR",
        "f2fp_default_dst_src__RRR",
        "f2fp_default_dst_src__RUR",
        "f2fp_f16_to_4b_downconvert__RRR",
        "f2fp_f16_to_4b_downconvert__RRU",
        "f2fp_f16_to_4b_downconvert__RUR",
        "f2fp_f16_to_8b_downconvert__RRR",
        "f2fp_f16_to_8b_downconvert__RRU",
        "f2fp_f16_to_8b_downconvert__RUR",
        "f2fp_f16_to_mx8_downconvert_scale__RRR",
        "f2fp_f16_to_mx8_downconvert_scale__RRU",
        "f2fp_f16_to_mx8_downconvert_scale__RUR",
        "f2fp_merge_c__RRR",
        "f2fp_merge_c__RRU",
        "f2fp_merge_c__RUR",
        "f2fp_merge_c_default_dst_src__RRR",
        "f2fp_merge_c_default_dst_src__RRU",
        "f2fp_merge_c_default_dst_src__RUR"
      )
    )
    val F2fpRegRegRegAddressImm: Definition =
      Definition("F2fpRegRegRegAddressImm", "F2FP", Vec("f2fp_f32_to_8b_downconvert__RRCx", "f2fp_rs__RRCx"))
    val F2fpRegRegRegConst: Definition =
      Definition("F2fpRegRegRegConst", "F2FP", Vec("f2fp_f32_to_8b_downconvert__RRC", "f2fp_rs__RRC"))
    val F2fpRegRegRegImm: Definition = Definition(
      "F2fpRegRegRegImm",
      "F2FP",
      Vec(
        "f2fp_f32_to_4b_downconvert__RRI",
        "f2fp_f32_to_8b_downconvert__RRI",
        "f2fp_f32_to_mx8_downconvert_scale__RRI",
        "f2fp_rs_16b__RRI",
        "f2fp_rs_8b4b__RRI",
        "f2fp_rs__RRI"
      )
    )
    val F2fpRegRegRegReg: Definition = Definition(
      "F2fpRegRegRegReg",
      "F2FP",
      Vec(
        "f2fp_f32_to_4b_downconvert__RRR",
        "f2fp_f32_to_4b_downconvert__RRU",
        "f2fp_f32_to_4b_downconvert__RUR",
        "f2fp_f32_to_8b_downconvert__RRR",
        "f2fp_f32_to_8b_downconvert__RRU",
        "f2fp_f32_to_8b_downconvert__RUR",
        "f2fp_f32_to_mx8_downconvert_scale__RRR",
        "f2fp_f32_to_mx8_downconvert_scale__RRU",
        "f2fp_f32_to_mx8_downconvert_scale__RUR",
        "f2fp_rs_16b__RRR",
        "f2fp_rs_16b__RRU",
        "f2fp_rs_16b__RUR",
        "f2fp_rs_8b4b__RRR",
        "f2fp_rs_8b4b__RRU",
        "f2fp_rs_8b4b__RUR",
        "f2fp_rs__RRR",
        "f2fp_rs__RRU",
        "f2fp_rs__RUR"
      )
    )
    val F2iRegAddressImm: Definition = Definition(
      "F2iRegAddressImm",
      "F2I",
      Vec(
        "f2i_Rd64__CXb_16b",
        "f2i_Rd64__CXb_32b",
        "f2i_Rd64__CXb_64b",
        "f2i_Rd64__CXb_bf16",
        "f2i_Rd64_swap__CXb_16b",
        "f2i_Rd64_swap__CXb_32b",
        "f2i_Rd64_swap__CXb_64b",
        "f2i_Rd64_swap__CXb_bf16",
        "f2i__CXb_16b",
        "f2i__CXb_32b",
        "f2i__CXb_64b",
        "f2i__CXb_bf16",
        "f2i_swap__CXb_16b",
        "f2i_swap__CXb_32b",
        "f2i_swap__CXb_64b",
        "f2i_swap__CXb_bf16"
      )
    )
    val F2iRegConst: Definition = Definition(
      "F2iRegConst",
      "F2I",
      Vec(
        "Const F2I",
        "Const F2I 16",
        "Const F2I 64",
        "Const_F2I",
        "Const_F2I_16",
        "Const_F2I_64",
        "Const_F2I_I64",
        "f2i_Rd64__Cb_16b",
        "f2i_Rd64__Cb_32b",
        "f2i_Rd64__Cb_64b",
        "f2i_Rd64__Cb_bf16",
        "f2i_Rd64_swap__Cb_16b",
        "f2i_Rd64_swap__Cb_32b",
        "f2i_Rd64_swap__Cb_64b",
        "f2i_Rd64_swap__Cb_bf16",
        "f2i__Cb_16b",
        "f2i__Cb_32b",
        "f2i__Cb_64b",
        "f2i__Cb_bf16",
        "f2i_swap__Cb_16b",
        "f2i_swap__Cb_32b",
        "f2i_swap__Cb_64b",
        "f2i_swap__Cb_bf16"
      )
    )
    val F2iRegImm: Definition = Definition(
      "F2iRegImm",
      "F2I",
      Vec(
        "Imm F2I",
        "Imm F2I 16",
        "Imm F2I 64",
        "Imm_F2I",
        "Imm_F2I_16",
        "Imm_F2I_64",
        "Imm_F2I_I64",
        "f2i_Rd64__IU_32b",
        "f2i_Rd64__Ib_16b",
        "f2i_Rd64__Ib_64b",
        "f2i_Rd64__Ib_bf16",
        "f2i_Rd64_swap__IU_32b",
        "f2i_Rd64_swap__Ib_16b",
        "f2i_Rd64_swap__Ib_64b",
        "f2i_Rd64_swap__Ib_bf16",
        "f2i__IU_32b",
        "f2i__Ib_16b",
        "f2i__Ib_64b",
        "f2i__Ib_bf16",
        "f2i_swap__IU_32b",
        "f2i_swap__Ib_16b",
        "f2i_swap__Ib_64b",
        "f2i_swap__Ib_bf16"
      )
    )
    val F2iRegReg: Definition = Definition(
      "F2iRegReg",
      "F2I",
      Vec(
        "F2I",
        "F2I 16",
        "F2I 64",
        "F2I_16",
        "F2I_64",
        "F2I_I64",
        "f2i_Rd64__Rb_16b",
        "f2i_Rd64__Rb_32b",
        "f2i_Rd64__Rb_64b",
        "f2i_Rd64__Rb_bf16",
        "f2i_Rd64__URb_16b",
        "f2i_Rd64__URb_32b",
        "f2i_Rd64__URb_64b",
        "f2i_Rd64__URb_bf16",
        "f2i_Rd64_swap__Rb_16b",
        "f2i_Rd64_swap__Rb_32b",
        "f2i_Rd64_swap__Rb_64b",
        "f2i_Rd64_swap__Rb_bf16",
        "f2i_Rd64_swap__URb_16b",
        "f2i_Rd64_swap__URb_32b",
        "f2i_Rd64_swap__URb_64b",
        "f2i_Rd64_swap__URb_bf16",
        "f2i__Rb_16b",
        "f2i__Rb_32b",
        "f2i__Rb_64b",
        "f2i__Rb_bf16",
        "f2i__URb_16b",
        "f2i__URb_32b",
        "f2i__URb_64b",
        "f2i__URb_bf16",
        "f2i_swap__Rb_16b",
        "f2i_swap__Rb_32b",
        "f2i_swap__Rb_64b",
        "f2i_swap__Rb_bf16",
        "f2i_swap__URb_16b",
        "f2i_swap__URb_32b",
        "f2i_swap__URb_64b",
        "f2i_swap__URb_bf16"
      )
    )
    val F2ipRegRegAddressImmReg: Definition = Definition("F2ipRegRegAddressImmReg", "F2IP", Vec("f2ip__RCxR"))
    val F2ipRegRegConstReg: Definition = Definition("F2ipRegRegConstReg", "F2IP", Vec("f2ip__RCR"))
    val F2ipRegRegImmReg: Definition = Definition("F2ipRegRegImmReg", "F2IP", Vec("f2ip__RIR"))
    val F2ipRegRegRegAddressImm: Definition = Definition("F2ipRegRegRegAddressImm", "F2IP", Vec("f2ip__RRCx"))
    val F2ipRegRegRegConst: Definition = Definition("F2ipRegRegRegConst", "F2IP", Vec("f2ip__RRC"))
    val F2ipRegRegRegImm: Definition = Definition("F2ipRegRegRegImm", "F2IP", Vec("f2ip__RRI"))
    val F2ipRegRegRegReg: Definition =
      Definition("F2ipRegRegRegReg", "F2IP", Vec("f2ip__RRR", "f2ip__RRU", "f2ip__RUR"))
    val FaddRegRegAddressImm: Definition = Definition("FaddRegRegAddressImm", "FADD", Vec("fadd__RRCx_RCx"))
    val FaddRegRegConst: Definition =
      Definition("FaddRegRegConst", "FADD", Vec("Const FADD", "Const_FADD", "fadd__RRC_RC"))
    val FaddRegRegImm: Definition = Definition("FaddRegRegImm", "FADD", Vec("Imm FADD", "Imm_FADD", "fadd__RRI_RI"))
    val FaddRegRegReg: Definition = Definition("FaddRegRegReg", "FADD", Vec("FADD", "fadd__RRR_RR", "fadd__RRU_RU"))
    val Fadd2RegRegImm: Definition = Definition("Fadd2RegRegImm", "FADD2", Vec("fadd2_imm__RRI"))
    val Fadd2RegRegReg: Definition = Definition("Fadd2RegRegReg", "FADD2", Vec("fadd2_rc__RRR", "fadd2_urc__RRU"))
    val Fadd32iRegRegImm: Definition = Definition("Fadd32iRegRegImm", "FADD32I", Vec("FADD32I", "fadd32i_"))
    val FccoPredPredRegConstReg: Definition = Definition("FccoPredPredRegConstReg", "FCCO", Vec("Const FCCO"))
    val FccoPredPredRegImmReg: Definition = Definition("FccoPredPredRegImmReg", "FCCO", Vec("Imm FCCO"))
    val FccoPredPredRegRegConst: Definition = Definition("FccoPredPredRegRegConst", "FCCO", Vec("Const1 FCCO"))
    val FccoPredPredRegRegReg: Definition = Definition("FccoPredPredRegRegReg", "FCCO", Vec("FCCO"))
    val FccoPredRegConstReg: Definition = Definition("FccoPredRegConstReg", "FCCO", Vec("Const FCCO"))
    val FccoPredRegImmReg: Definition = Definition("FccoPredRegImmReg", "FCCO", Vec("Imm FCCO"))
    val FccoPredRegRegConst: Definition = Definition("FccoPredRegRegConst", "FCCO", Vec("Const1 FCCO"))
    val FccoPredRegRegReg: Definition = Definition("FccoPredRegRegReg", "FCCO", Vec("FCCO"))
    val FchkPredRegAddressImm: Definition = Definition("FchkPredRegAddressImm", "FCHK", Vec("fchk__RCxR_RCx"))
    val FchkPredRegConst: Definition =
      Definition("FchkPredRegConst", "FCHK", Vec("Const FCHK", "Const_FCHK", "fchk__RCR_RC"))
    val FchkPredRegImm: Definition = Definition("FchkPredRegImm", "FCHK", Vec("Imm FCHK", "Imm_FCHK", "fchk__RIR_RI"))
    val FchkPredRegReg: Definition = Definition("FchkPredRegReg", "FCHK", Vec("FCHK", "fchk__RRR_RR", "fchk__RUR_RU"))
    val FcmpRegRegConstReg: Definition = Definition("FcmpRegRegConstReg", "FCMP", Vec("Const FCMP", "Const_FCMP"))
    val FcmpRegRegImmReg: Definition = Definition("FcmpRegRegImmReg", "FCMP", Vec("Imm FCMP", "Imm_FCMP"))
    val FcmpRegRegRegConst: Definition = Definition("FcmpRegRegRegConst", "FCMP", Vec("Const1 FCMP", "Const1_FCMP"))
    val definitions: Vec[Definition] = Vec(
      AcqbulkNoOperands,
      AcqshminitNoOperands,
      Al2pPredRegReg,
      Al2pPredRegRegImm,
      Al2pRegReg,
      Al2pRegRegImm,
      AldRegAddressReg,
      ArrivesAddress,
      AstAddressRegReg,
      AtomPredRegAddress,
      AtomPredRegAddressReg,
      AtomPredRegAddressRegReg,
      AtomRegAddressReg,
      AtomRegAddressRegReg,
      AtomgPredRegAddressReg,
      AtomgPredRegAddressRegReg,
      AtomsPredAddressRegReg,
      AtomsRegAddress,
      AtomsRegAddressReg,
      AtomsRegAddressRegReg,
      B2rReg,
      B2rRegImm,
      B2rRegPred,
      BarNoOperands,
      BarImm,
      BarImmImm,
      BarImmImmPred,
      BarImmPred,
      BarImmReg,
      BarImmRegPred,
      BarReg,
      BarRegImm,
      BarRegImmPred,
      BarRegPred,
      BarRegReg,
      BarRegRegPred,
      BfeRegRegConst,
      BfeRegRegImm,
      BfeRegRegReg,
      BfiRegRegConstReg,
      BfiRegRegImmReg,
      BfiRegRegRegConst,
      BfiRegRegRegReg,
      BgmmaRegAddressReg,
      BgmmaRegAddressRegPred,
      BgmmaRegAddressRegReg,
      BgmmaRegAddressRegRegPred,
      BgmmaRegRegAddressReg,
      BgmmaRegRegAddressRegPred,
      BmmaRegRegRegReg,
      BmmaRegRegRegRegPred,
      BmovAddressImm,
      BmovConst,
      BmovImm,
      BmovReg,
      BmovRegAddressImm,
      BmovRegConst,
      BmovRegImm,
      BmovRegReg,
      BmovRegSymbol,
      BmovSymbolAddressImm,
      BmovSymbolConst,
      BmovSymbolImm,
      BmovSymbolReg,
      BmskRegRegAddressImm,
      BmskRegRegConst,
      BmskRegRegImm,
      BmskRegRegReg,
      BptNoOperands,
      BptImm,
      BraConst,
      BraImm,
      BraPredImm,
      BraPredPredImm,
      BraPredRegImm,
      BraRegImm,
      BraSymbolConst,
      BraSymbolImm,
      BraSymbolImmImm,
      BraSymbolSymbolImmImm,
      BreakPredReg,
      BreakReg,
      BrevRegAddressImm,
      BrevRegConst,
      BrevRegImm,
      BrevRegReg,
      BrkNoOperands,
      BrxConst,
      BrxPredReg,
      BrxPredRegImm,
      BrxReg,
      BrxRegImm,
      BrxSymbolConst,
      BrxSymbolImmAddress,
      BrxSymbolReg,
      BrxSymbolRegImm,
      BrxSymbolSymbolImmAddress,
      BrxuPredReg,
      BrxuPredRegImm,
      BrxuReg,
      BrxuRegImm,
      BssyPredRegImm,
      BssyRegImm,
      BsyncPredReg,
      BsyncReg,
      CalConst,
      CalImm,
      CallConst,
      CallImm,
      CallPredConst,
      CallPredImm,
      CallPredReg,
      CallPredRegImm,
      CallReg,
      CallRegImm,
      CctlNoOperands,
      CctlAddress,
      CctlAddressAddress,
      CctlAddressImm,
      CctlConst,
      CctlPredAddress,
      CctlRegAddress,
      CctllNoOperands,
      CctllAddress,
      CctllRegAddress,
      CctlsNoOperands,
      CctltNoOperands,
      CctltImm,
      CctltImmImm,
      CctltReg,
      CgaerrbarNoOperands,
      ClmadRegRegAddressImmReg,
      ClmadRegRegConstReg,
      ClmadRegRegRegAddressImm,
      ClmadRegRegRegConst,
      ClmadRegRegRegReg,
      ContNoOperands,
      CreduxRegReg,
      Cs2rRegSpecial,
      Cs2urRegSpecial,
      CsetRegSymbol,
      CsetRegSymbolPred,
      CsetpPredPredSymbolPred,
      CsetpPredSymbol,
      CsmtestImm,
      CsmtestPredImm,
      CsmtestPredPredImmPred,
      DaddRegRegAddressImm,
      DaddRegRegConst,
      DaddRegRegImm,
      DaddRegRegReg,
      DchkPredRegConst,
      DchkPredRegImm,
      DchkPredRegReg,
      DepbarNoOperands,
      DepbarGroup,
      DepbarImm,
      DepbarRegImm,
      DepbarRegImmGroup,
      DepbarRegImmImm,
      DepbarRegReg,
      DepbarRegRegGroup,
      DepbarRegRegImm,
      DfmaRegRegAddressImmReg,
      DfmaRegRegConstReg,
      DfmaRegRegImmReg,
      DfmaRegRegRegAddressImm,
      DfmaRegRegRegConst,
      DfmaRegRegRegImm,
      DfmaRegRegRegReg,
      DmmaRegRegRegReg,
      DmmaRegRegRegRegPred,
      DmnmxRegRegConstPred,
      DmnmxRegRegImmPred,
      DmnmxRegRegRegPred,
      DmulRegRegAddressImm,
      DmulRegRegConst,
      DmulRegRegImm,
      DmulRegRegReg,
      DsetRegRegConst,
      DsetRegRegConstPred,
      DsetRegRegImm,
      DsetRegRegImmPred,
      DsetRegRegReg,
      DsetRegRegRegPred,
      DsetpPredPredRegAddressImmPred,
      DsetpPredPredRegConstPred,
      DsetpPredPredRegImmPred,
      DsetpPredPredRegRegPred,
      DsetpPredRegAddressImm,
      DsetpPredRegConst,
      DsetpPredRegImm,
      DsetpPredRegReg,
      ElectPredReg,
      ElectPredRegPred,
      ElectPredRegReg,
      EndcollectiveNoOperands,
      EndcollectivePred,
      ErrbarNoOperands,
      ExitNoOperands,
      ExitPred,
      F2fRegAddressImm,
      F2fRegConst,
      F2fRegImm,
      F2fRegReg,
      F2fpRegAddressImm,
      F2fpRegAddressImmReg,
      F2fpRegConst,
      F2fpRegConstReg,
      F2fpRegImm,
      F2fpRegImmReg,
      F2fpRegReg,
      F2fpRegRegAddressImm,
      F2fpRegRegAddressImmReg,
      F2fpRegRegConst,
      F2fpRegRegConstReg,
      F2fpRegRegImm,
      F2fpRegRegImmReg,
      F2fpRegRegReg,
      F2fpRegRegRegAddressImm,
      F2fpRegRegRegConst,
      F2fpRegRegRegImm,
      F2fpRegRegRegReg,
      F2iRegAddressImm,
      F2iRegConst,
      F2iRegImm,
      F2iRegReg,
      F2ipRegRegAddressImmReg,
      F2ipRegRegConstReg,
      F2ipRegRegImmReg,
      F2ipRegRegRegAddressImm,
      F2ipRegRegRegConst,
      F2ipRegRegRegImm,
      F2ipRegRegRegReg,
      FaddRegRegAddressImm,
      FaddRegRegConst,
      FaddRegRegImm,
      FaddRegRegReg,
      Fadd2RegRegImm,
      Fadd2RegRegReg,
      Fadd32iRegRegImm,
      FccoPredPredRegConstReg,
      FccoPredPredRegImmReg,
      FccoPredPredRegRegConst,
      FccoPredPredRegRegReg,
      FccoPredRegConstReg,
      FccoPredRegImmReg,
      FccoPredRegRegConst,
      FccoPredRegRegReg,
      FchkPredRegAddressImm,
      FchkPredRegConst,
      FchkPredRegImm,
      FchkPredRegReg,
      FcmpRegRegConstReg,
      FcmpRegRegImmReg,
      FcmpRegRegRegConst
    )
  }
  object Definitions1 {
    val FcmpRegRegRegReg: Definition = Definition("FcmpRegRegRegReg", "FCMP", Vec("FCMP"))
    val FenceNoOperands: Definition = Definition("FenceNoOperands", "FENCE", Vec("fence_", "fence_g_", "fence_t_"))
    val FfmaRegRegAddressImmReg: Definition = Definition("FfmaRegRegAddressImmReg", "FFMA", Vec("ffma__RCxR_RCxR"))
    val FfmaRegRegConstReg: Definition =
      Definition("FfmaRegRegConstReg", "FFMA", Vec("Const FFMA", "Const_FFMA", "ffma__RCR_RCR"))
    val FfmaRegRegImmReg: Definition =
      Definition("FfmaRegRegImmReg", "FFMA", Vec("Imm FFMA", "Imm_FFMA", "ffma__RIR_RIR"))
    val FfmaRegRegRegAddressImm: Definition = Definition("FfmaRegRegRegAddressImm", "FFMA", Vec("ffma__RRCx_RRCx"))
    val FfmaRegRegRegConst: Definition =
      Definition("FfmaRegRegRegConst", "FFMA", Vec("Const1 FFMA", "Const1_FFMA", "ffma__RRC_RRC"))
    val FfmaRegRegRegImm: Definition = Definition("FfmaRegRegRegImm", "FFMA", Vec("ffma__RRI_RRI"))
    val FfmaRegRegRegReg: Definition =
      Definition("FfmaRegRegRegReg", "FFMA", Vec("FFMA", "ffma__RRR_RRR", "ffma__RRU_RRU", "ffma__RUR_RUR"))
    val Ffma2RegRegImmReg: Definition = Definition("Ffma2RegRegImmReg", "FFMA2", Vec("ffma2_x_rc__RIR"))
    val Ffma2RegRegRegImm: Definition = Definition("Ffma2RegRegRegImm", "FFMA2", Vec("ffma2_rb_x__RRI"))
    val Ffma2RegRegRegReg: Definition =
      Definition("Ffma2RegRegRegReg", "FFMA2", Vec("ffma2_rb_rc__RRR", "ffma2_rb_urc__RRU", "ffma2_urb_rc__RUR"))
    val Ffma32iRegRegImm: Definition = Definition("Ffma32iRegRegImm", "FFMA32I", Vec("FFMA32I 2", "FFMA32I_2"))
    val Ffma32iRegRegImmReg: Definition = Definition("Ffma32iRegRegImmReg", "FFMA32I", Vec("FFMA32I", "ffma32i_"))
    val FhaddRegRegReg: Definition = Definition("FhaddRegRegReg", "FHADD", Vec("fhadd__RR", "fhadd__RU"))
    val FhfmaRegRegRegReg: Definition =
      Definition("FhfmaRegRegRegReg", "FHFMA", Vec("fhfma__RRR", "fhfma__RRU", "fhfma__RUR"))
    val FloRegAddressImm: Definition = Definition("FloRegAddressImm", "FLO", Vec("flo__RCxR_RCxR"))
    val FloRegConst: Definition = Definition("FloRegConst", "FLO", Vec("Const FLO", "Const_FLO", "flo__RCR_RCR"))
    val FloRegImm: Definition = Definition("FloRegImm", "FLO", Vec("Imm FLO", "Imm_FLO", "flo__RuIR_RIR"))
    val FloRegPredAddressImm: Definition = Definition("FloRegPredAddressImm", "FLO", Vec("flo__RCxR_RCxR"))
    val FloRegPredConst: Definition = Definition("FloRegPredConst", "FLO", Vec("flo__RCR_RCR"))
    val FloRegPredImm: Definition = Definition("FloRegPredImm", "FLO", Vec("flo__RuIR_RIR"))
    val FloRegPredReg: Definition = Definition("FloRegPredReg", "FLO", Vec("flo__RRR_RRR", "flo__RUR_RUR"))
    val FloRegReg: Definition = Definition("FloRegReg", "FLO", Vec("FLO", "flo__RRR_RRR", "flo__RUR_RUR"))
    val FmnmxRegPredRegAddressImmPred: Definition =
      Definition("FmnmxRegPredRegAddressImmPred", "FMNMX", Vec("fmnmx_pred__RCxR_RCxR"))
    val FmnmxRegPredRegConstPred: Definition =
      Definition("FmnmxRegPredRegConstPred", "FMNMX", Vec("fmnmx_pred__RCR_RCR"))
    val FmnmxRegPredRegImmPred: Definition = Definition("FmnmxRegPredRegImmPred", "FMNMX", Vec("fmnmx_pred__RIR_RIR"))
    val FmnmxRegPredRegRegPred: Definition =
      Definition("FmnmxRegPredRegRegPred", "FMNMX", Vec("fmnmx_pred__RRR_RRR", "fmnmx_pred__RUR_RUR"))
    val FmnmxRegRegAddressImmPred: Definition =
      Definition("FmnmxRegRegAddressImmPred", "FMNMX", Vec("fmnmx__RCxR_RCxR"))
    val FmnmxRegRegConstPred: Definition =
      Definition("FmnmxRegRegConstPred", "FMNMX", Vec("Const FMNMX", "Const_FMNMX", "fmnmx__RCR_RCR"))
    val FmnmxRegRegImmPred: Definition =
      Definition("FmnmxRegRegImmPred", "FMNMX", Vec("Imm FMNMX", "Imm_FMNMX", "fmnmx__RIR_RIR"))
    val FmnmxRegRegRegPred: Definition =
      Definition("FmnmxRegRegRegPred", "FMNMX", Vec("FMNMX", "fmnmx__RRR_RRR", "fmnmx__RUR_RUR"))
    val Fmnmx3RegRegImmRegPred: Definition = Definition("Fmnmx3RegRegImmRegPred", "FMNMX3", Vec("fmnmx3__RIR_RIR"))
    val Fmnmx3RegRegRegRegPred: Definition =
      Definition("Fmnmx3RegRegRegRegPred", "FMNMX3", Vec("fmnmx3__RRR_RRR", "fmnmx3__RUR_RUR"))
    val FmulRegRegAddressImm: Definition = Definition("FmulRegRegAddressImm", "FMUL", Vec("fmul__RCxR_RCx"))
    val FmulRegRegConst: Definition =
      Definition("FmulRegRegConst", "FMUL", Vec("Const FMUL", "Const_FMUL", "fmul__RCR_RC"))
    val FmulRegRegImm: Definition = Definition("FmulRegRegImm", "FMUL", Vec("Imm FMUL", "Imm_FMUL", "fmul__RIR_RI"))
    val FmulRegRegReg: Definition = Definition("FmulRegRegReg", "FMUL", Vec("FMUL", "fmul__RRR_RR", "fmul__RUR_RU"))
    val Fmul2RegRegImm: Definition = Definition("Fmul2RegRegImm", "FMUL2", Vec("fmul2_imm__RIR"))
    val Fmul2RegRegReg: Definition = Definition("Fmul2RegRegReg", "FMUL2", Vec("fmul2_rb__RRR", "fmul2_urb__RUR"))
    val Fmul32iRegRegImm: Definition = Definition("Fmul32iRegRegImm", "FMUL32I", Vec("FMUL32I", "fmul32i_"))
    val FootprintPredRegRegRegReg: Definition =
      Definition("FootprintPredRegRegRegReg", "FOOTPRINT", Vec("footprint_b_noConst_", "footprint_scr_b_noConst_"))
    val FootprintPredRegRegRegRegImmImm: Definition = Definition(
      "FootprintPredRegRegRegRegImmImm",
      "FOOTPRINT",
      Vec("footprint_", "footprint_b_", "footprint_scr_", "footprint_scr_b_")
    )
    val FootprintPredRegRegRegRegImmImmImm: Definition =
      Definition("FootprintPredRegRegRegRegImmImmImm", "FOOTPRINT", Vec("footprint_b_tid_", "footprint_scr_b_tid_"))
    val FootprintPredRegRegRegRegReg: Definition = Definition(
      "FootprintPredRegRegRegRegReg",
      "FOOTPRINT",
      Vec("footprint_b_urc_", "footprint_scr_b_urc_", "footprint_scr_uniform_", "footprint_uniform_")
    )
    val FootprintPredRegRegRegRegRegImm: Definition =
      Definition("FootprintPredRegRegRegRegRegImm", "FOOTPRINT", Vec("footprint_scr_uniform_", "footprint_uniform_"))
    val FootprintRegRegRegReg: Definition =
      Definition("FootprintRegRegRegReg", "FOOTPRINT", Vec("footprint_b_noConst_", "footprint_scr_b_noConst_"))
    val FootprintRegRegRegRegImmImm: Definition = Definition(
      "FootprintRegRegRegRegImmImm",
      "FOOTPRINT",
      Vec("footprint_", "footprint_b_", "footprint_scr_", "footprint_scr_b_")
    )
    val FootprintRegRegRegRegImmImmImm: Definition =
      Definition("FootprintRegRegRegRegImmImmImm", "FOOTPRINT", Vec("footprint_b_tid_", "footprint_scr_b_tid_"))
    val FootprintRegRegRegRegReg: Definition = Definition(
      "FootprintRegRegRegRegReg",
      "FOOTPRINT",
      Vec("footprint_b_urc_", "footprint_scr_b_urc_", "footprint_scr_uniform_", "footprint_uniform_")
    )
    val FootprintRegRegRegRegRegImm: Definition =
      Definition("FootprintRegRegRegRegRegImm", "FOOTPRINT", Vec("footprint_scr_uniform_", "footprint_uniform_"))
    val FrndRegAddressImm: Definition = Definition(
      "FrndRegAddressImm",
      "FRND",
      Vec(
        "frnd__bf16_CXb",
        "frnd__f16_CXb",
        "frnd__f32_CXb",
        "frnd__f64_CXb",
        "frnd_swap__bf16_CXb",
        "frnd_swap__f16_CXb",
        "frnd_swap__f32_CXb",
        "frnd_swap__f64_CXb"
      )
    )
    val FrndRegConst: Definition = Definition(
      "FrndRegConst",
      "FRND",
      Vec(
        "frnd__bf16_C",
        "frnd__f16_C",
        "frnd__f32_C",
        "frnd__f64_C",
        "frnd_swap__bf16_C",
        "frnd_swap__f16_C",
        "frnd_swap__f32_C",
        "frnd_swap__f64_C"
      )
    )
    val FrndRegImm: Definition = Definition(
      "FrndRegImm",
      "FRND",
      Vec(
        "frnd__bf16_I",
        "frnd__f16_I",
        "frnd__f32_I",
        "frnd__f64_I",
        "frnd_swap__bf16_I",
        "frnd_swap__f16_I",
        "frnd_swap__f32_I",
        "frnd_swap__f64_I"
      )
    )
    val FrndRegReg: Definition = Definition(
      "FrndRegReg",
      "FRND",
      Vec(
        "frnd__bf16_R",
        "frnd__bf16_URb",
        "frnd__f16_R",
        "frnd__f16_URb",
        "frnd__f32_R",
        "frnd__f32_URb",
        "frnd__f64_R",
        "frnd__f64_URb",
        "frnd_swap__bf16_R",
        "frnd_swap__bf16_URb",
        "frnd_swap__f16_R",
        "frnd_swap__f16_URb",
        "frnd_swap__f32_R",
        "frnd_swap__f32_URb",
        "frnd_swap__f64_R",
        "frnd_swap__f64_URb"
      )
    )
    val FselRegRegAddressImmPred: Definition = Definition("FselRegRegAddressImmPred", "FSEL", Vec("fsel__RCxR_RCxR"))
    val FselRegRegConstPred: Definition = Definition("FselRegRegConstPred", "FSEL", Vec("fsel__RCR_RCR"))
    val FselRegRegImmPred: Definition = Definition("FselRegRegImmPred", "FSEL", Vec("fsel__RIR_RIR"))
    val FselRegRegRegPred: Definition = Definition("FselRegRegRegPred", "FSEL", Vec("fsel__RRR_RRR", "fsel__RUR_RUR"))
    val FsetRegRegAddressImm: Definition = Definition("FsetRegRegAddressImm", "FSET", Vec("fset_simple__RCxR_RCxR"))
    val FsetRegRegAddressImmPred: Definition = Definition("FsetRegRegAddressImmPred", "FSET", Vec("fset__RCxR_RCxR"))
    val FsetRegRegConst: Definition =
      Definition("FsetRegRegConst", "FSET", Vec("NoBop Const FSET", "NoBop_Const_FSET", "fset_simple__RCR_RCR"))
    val FsetRegRegConstPred: Definition =
      Definition("FsetRegRegConstPred", "FSET", Vec("Const FSET", "Const_FSET", "fset__RCR_RCR"))
    val FsetRegRegImm: Definition =
      Definition("FsetRegRegImm", "FSET", Vec("NoBop Imm FSET", "NoBop_Imm_FSET", "fset_simple__RIR_RIR"))
    val FsetRegRegImmPred: Definition =
      Definition("FsetRegRegImmPred", "FSET", Vec("Imm FSET", "Imm_FSET", "fset__RIR_RIR"))
    val FsetRegRegReg: Definition = Definition(
      "FsetRegRegReg",
      "FSET",
      Vec("NoBop FSET", "NoBop_FSET", "fset_simple__RRR_RRR", "fset_simple__RUR_RUR")
    )
    val FsetRegRegRegPred: Definition =
      Definition("FsetRegRegRegPred", "FSET", Vec("FSET", "fset__RRR_RRR", "fset__RUR_RUR"))
    val FsetpPredPredRegAddressImmPred: Definition =
      Definition("FsetpPredPredRegAddressImmPred", "FSETP", Vec("fsetp__RCxR_RCxR"))
    val FsetpPredPredRegConstPred: Definition = Definition(
      "FsetpPredPredRegConstPred",
      "FSETP",
      Vec("Const FSETP", "Const_FSETP", "fsetp__RCR_RC", "fsetp__RCR_RCR")
    )
    val FsetpPredPredRegImmPred: Definition =
      Definition("FsetpPredPredRegImmPred", "FSETP", Vec("Imm FSETP", "Imm_FSETP", "fsetp__RIR_RI", "fsetp__RIR_RIR"))
    val FsetpPredPredRegRegPred: Definition =
      Definition("FsetpPredPredRegRegPred", "FSETP", Vec("FSETP", "fsetp__RRR_RR", "fsetp__RRR_RRR", "fsetp__RUR_RUR"))
    val FsetpPredRegAddressImm: Definition =
      Definition("FsetpPredRegAddressImm", "FSETP", Vec("fsetp_simple__RCxR_RCxR"))
    val FsetpPredRegConst: Definition = Definition(
      "FsetpPredRegConst",
      "FSETP",
      Vec("NoBop Const FSETP", "NoBop_Const_FSETP", "fsetp_simple__RCR_RC", "fsetp_simple__RCR_RCR")
    )
    val FsetpPredRegImm: Definition = Definition(
      "FsetpPredRegImm",
      "FSETP",
      Vec("NoBop Imm FSETP", "NoBop_Imm_FSETP", "fsetp_simple__RIR_RI", "fsetp_simple__RIR_RIR")
    )
    val FsetpPredRegReg: Definition = Definition(
      "FsetpPredRegReg",
      "FSETP",
      Vec("NoBop FSETP", "NoBop_FSETP", "fsetp_simple__RRR_RR", "fsetp_simple__RRR_RRR", "fsetp_simple__RUR_RUR")
    )
    val FswzRegRegReg: Definition = Definition("FswzRegRegReg", "FSWZ", Vec("FSWZ"))
    val FswzaddRegRegReg: Definition = Definition("FswzaddRegRegReg", "FSWZADD", Vec("FSWZADD", "fswzadd_"))
    val GatherRegRegRegRegImm: Definition = Definition("GatherRegRegRegRegImm", "GATHER", Vec("gather_"))
    val GatherRegRegRegRegImmImm: Definition = Definition("GatherRegRegRegRegImmImm", "GATHER", Vec("gather_"))
    val GatherRegRegRegRegImmImmImm: Definition = Definition("GatherRegRegRegRegImmImmImm", "GATHER", Vec("gather_"))
    val GetcrsptrReg: Definition = Definition("GetcrsptrReg", "GETCRSPTR", Vec("GETCRSPTR"))
    val GetlmembaseReg: Definition = Definition("GetlmembaseReg", "GETLMEMBASE", Vec("GETLMEMBASE", "getlmembase_"))
    val Hadd2NoOperands: Definition = Definition("Hadd2NoOperands", "HADD2", Vec("hadd2__v2"))
    val Hadd2Const: Definition = Definition("Hadd2Const", "HADD2", Vec("hadd2__v0"))
    val Hadd2ImmImm: Definition = Definition("Hadd2ImmImm", "HADD2", Vec("hadd2__v1"))
    val Hadd2RegRegAddressImm: Definition =
      Definition("Hadd2RegRegAddressImm", "HADD2", Vec("hadd2_F32__RCx", "hadd2__RCx"))
    val Hadd2RegRegConst: Definition = Definition("Hadd2RegRegConst", "HADD2", Vec("hadd2_F32__RC", "hadd2__RC"))
    val Hadd2RegRegImm: Definition = Definition("Hadd2RegRegImm", "HADD2", Vec("hadd2_F32__RI"))
    val Hadd2RegRegImmImm: Definition =
      Definition("Hadd2RegRegImmImm", "HADD2", Vec("hadd2_F32i_", "hadd2__RI", "hadd2_fixed__RI"))
    val Hadd2RegRegReg: Definition = Definition(
      "Hadd2RegRegReg",
      "HADD2",
      Vec(
        "hadd2_F32__RR",
        "hadd2_F32__RU",
        "hadd2_F32_fixed__RR",
        "hadd2_F32_fixed__RU",
        "hadd2__RR",
        "hadd2__RU",
        "hadd2_fixed__RR",
        "hadd2_fixed__RU"
      )
    )
    val Hadd232iImmImm: Definition = Definition("Hadd232iImmImm", "HADD2_32I", Vec("hadd2_32i_"))
    val Hadd232iRegRegImmImm: Definition =
      Definition("Hadd232iRegRegImmImm", "HADD2_32I", Vec("hadd2_32i_", "hadd2_32i_fixed_"))
    val Hfma2NoOperands: Definition = Definition("Hfma2NoOperands", "HFMA2", Vec("hfma2__v2"))
    val Hfma2Const: Definition = Definition("Hfma2Const", "HFMA2", Vec("hfma2__v0"))
    val Hfma2ImmImm: Definition = Definition("Hfma2ImmImm", "HFMA2", Vec("hfma2__v1"))
    val Hfma2RegRegAddressImmReg: Definition = Definition(
      "Hfma2RegRegAddressImmReg",
      "HFMA2",
      Vec(
        "hfma2__RCxR",
        "hfma2_mma__RCxR",
        "hfma2_mma__RCxR_norelu",
        "hfma2_mma__RCxR_relu",
        "hfma2_mma_relu__RCxR",
        "hfma2_relu__RCxR"
      )
    )
    val Hfma2RegRegAddressImmRegPred: Definition = Definition(
      "Hfma2RegRegAddressImmRegPred",
      "HFMA2",
      Vec("hfma2_mma__RCxR_relu", "hfma2_mma_relu__RCxR", "hfma2_relu__RCxR")
    )
    val Hfma2RegRegConstReg: Definition = Definition(
      "Hfma2RegRegConstReg",
      "HFMA2",
      Vec(
        "hfma2__RCR",
        "hfma2_mma__RCR",
        "hfma2_mma__RCR_norelu",
        "hfma2_mma__RCR_relu",
        "hfma2_mma_relu__RCR",
        "hfma2_relu__RCR"
      )
    )
    val Hfma2RegRegConstRegPred: Definition = Definition(
      "Hfma2RegRegConstRegPred",
      "HFMA2",
      Vec("hfma2_mma__RCR_relu", "hfma2_mma_relu__RCR", "hfma2_relu__RCR")
    )
    val Hfma2RegRegImmImmReg: Definition = Definition(
      "Hfma2RegRegImmImmReg",
      "HFMA2",
      Vec(
        "hfma2__RIR",
        "hfma2_fixed__RIR",
        "hfma2_mma__RIR",
        "hfma2_mma__RIR_norelu",
        "hfma2_mma__RIR_relu",
        "hfma2_mma_relu__RIR",
        "hfma2_relu__RIR",
        "hfma2_relu_fixed__RIR"
      )
    )
    val Hfma2RegRegImmImmRegPred: Definition = Definition(
      "Hfma2RegRegImmImmRegPred",
      "HFMA2",
      Vec("hfma2_mma__RIR_relu", "hfma2_mma_relu__RIR", "hfma2_relu__RIR", "hfma2_relu_fixed__RIR")
    )
    val Hfma2RegRegRegAddressImm: Definition = Definition(
      "Hfma2RegRegRegAddressImm",
      "HFMA2",
      Vec(
        "hfma2__RRCx",
        "hfma2_mma__RRCx",
        "hfma2_mma__RRCx_norelu",
        "hfma2_mma__RRCx_relu",
        "hfma2_mma_relu__RRCx",
        "hfma2_relu__RRCx"
      )
    )
    val Hfma2RegRegRegAddressImmPred: Definition = Definition(
      "Hfma2RegRegRegAddressImmPred",
      "HFMA2",
      Vec("hfma2_mma__RRCx_relu", "hfma2_mma_relu__RRCx", "hfma2_relu__RRCx")
    )
    val Hfma2RegRegRegConst: Definition = Definition(
      "Hfma2RegRegRegConst",
      "HFMA2",
      Vec(
        "HFMA2_CCST",
        "hfma2__RRC",
        "hfma2_mma__RRC",
        "hfma2_mma__RRC_norelu",
        "hfma2_mma__RRC_relu",
        "hfma2_mma_relu__RRC",
        "hfma2_relu__RRC"
      )
    )
    val Hfma2RegRegRegConstPred: Definition = Definition(
      "Hfma2RegRegRegConstPred",
      "HFMA2",
      Vec("hfma2_mma__RRC_relu", "hfma2_mma_relu__RRC", "hfma2_relu__RRC")
    )
    val Hfma2RegRegRegImmImm: Definition = Definition(
      "Hfma2RegRegRegImmImm",
      "HFMA2",
      Vec(
        "hfma2__RRI",
        "hfma2_fixed__RRI",
        "hfma2_mma__RRI",
        "hfma2_mma__RRI_norelu",
        "hfma2_mma__RRI_relu",
        "hfma2_mma_relu__RRI",
        "hfma2_relu__RRI",
        "hfma2_relu_fixed__RRI"
      )
    )
    val Hfma2RegRegRegImmImmPred: Definition = Definition(
      "Hfma2RegRegRegImmImmPred",
      "HFMA2",
      Vec("hfma2_mma__RRI_relu", "hfma2_mma_relu__RRI", "hfma2_relu__RRI", "hfma2_relu_fixed__RRI")
    )
    val Hfma2RegRegRegReg: Definition = Definition(
      "Hfma2RegRegRegReg",
      "HFMA2",
      Vec(
        "hfma2__RRR",
        "hfma2__RRU",
        "hfma2__RUR",
        "hfma2_fixed__RRR",
        "hfma2_fixed__RRU",
        "hfma2_fixed__RUR",
        "hfma2_mma__RRR",
        "hfma2_mma__RRR_norelu",
        "hfma2_mma__RRR_relu",
        "hfma2_mma__RRU",
        "hfma2_mma__RRU_norelu",
        "hfma2_mma__RRU_relu",
        "hfma2_mma__RUR",
        "hfma2_mma__RUR_norelu",
        "hfma2_mma__RUR_relu",
        "hfma2_mma_relu__RRR",
        "hfma2_mma_relu__RRU",
        "hfma2_mma_relu__RUR",
        "hfma2_relu__RRR",
        "hfma2_relu__RRU",
        "hfma2_relu__RUR",
        "hfma2_relu_fixed__RRR",
        "hfma2_relu_fixed__RRU",
        "hfma2_relu_fixed__RUR"
      )
    )
    val Hfma2RegRegRegRegPred: Definition = Definition(
      "Hfma2RegRegRegRegPred",
      "HFMA2",
      Vec(
        "hfma2_mma__RRR_relu",
        "hfma2_mma__RRU_relu",
        "hfma2_mma__RUR_relu",
        "hfma2_mma_relu__RRR",
        "hfma2_mma_relu__RRU",
        "hfma2_mma_relu__RUR",
        "hfma2_relu__RRR",
        "hfma2_relu__RRU",
        "hfma2_relu__RUR",
        "hfma2_relu_fixed__RRR",
        "hfma2_relu_fixed__RRU",
        "hfma2_relu_fixed__RUR"
      )
    )
    val Hfma232iRegRegImmImm: Definition = Definition("Hfma232iRegRegImmImm", "HFMA2_32I", Vec("HFMA2_32I_2"))
    val Hfma232iRegRegImmImmReg: Definition =
      Definition("Hfma232iRegRegImmImmReg", "HFMA2_32I", Vec("HFMA2_32I", "hfma2_32i_", "hfma2_32i_fixed_"))
    val HgmmaRegAddressReg: Definition = Definition("HgmmaRegAddressReg", "HGMMA", Vec("hgmma_URa_Rc_"))
    val HgmmaRegAddressRegPred: Definition = Definition("HgmmaRegAddressRegPred", "HGMMA", Vec("hgmma_URa_Rc_"))
    val HgmmaRegAddressRegPredRegImm: Definition =
      Definition("HgmmaRegAddressRegPredRegImm", "HGMMA", Vec("hgmma_sparse_URa_Rc_"))
    val HgmmaRegAddressRegReg: Definition = Definition("HgmmaRegAddressRegReg", "HGMMA", Vec("hgmma_URa_Rb_Rc_"))
    val HgmmaRegAddressRegRegImm: Definition =
      Definition("HgmmaRegAddressRegRegImm", "HGMMA", Vec("hgmma_sparse_URa_Rc_"))
    val HgmmaRegAddressRegRegPred: Definition =
      Definition("HgmmaRegAddressRegRegPred", "HGMMA", Vec("hgmma_URa_Rb_Rc_"))
    val HgmmaRegAddressRegRegPredRegImm: Definition =
      Definition("HgmmaRegAddressRegRegPredRegImm", "HGMMA", Vec("hgmma_sparse_URa_Rb_Rc_"))
    val HgmmaRegAddressRegRegRegImm: Definition =
      Definition("HgmmaRegAddressRegRegRegImm", "HGMMA", Vec("hgmma_sparse_URa_Rb_Rc_"))
    val HgmmaRegRegAddressReg: Definition = Definition("HgmmaRegRegAddressReg", "HGMMA", Vec("hgmma_Ra_URb_Rc_"))
    val HgmmaRegRegAddressRegPred: Definition =
      Definition("HgmmaRegRegAddressRegPred", "HGMMA", Vec("hgmma_Ra_URb_Rc_"))
    val HgmmaRegRegAddressRegPredRegImm: Definition =
      Definition("HgmmaRegRegAddressRegPredRegImm", "HGMMA", Vec("hgmma_sparse_Ra_URb_Rc_"))
    val HgmmaRegRegAddressRegRegImm: Definition =
      Definition("HgmmaRegRegAddressRegRegImm", "HGMMA", Vec("hgmma_sparse_Ra_URb_Rc_"))
    val HmmaAddressRegRegAddress: Definition = Definition("HmmaAddressRegRegAddress", "HMMA", Vec("hmma_x8_indexedRF_"))
    val HmmaAddressRegRegAddressPred: Definition =
      Definition("HmmaAddressRegRegAddressPred", "HMMA", Vec("hmma_x8_indexedRF_"))
    val HmmaAddressRegRegAddressPredRegImm: Definition =
      Definition("HmmaAddressRegRegAddressPredRegImm", "HMMA", Vec("hmma_sparse_indexedRF_"))
    val HmmaAddressRegRegAddressRegImm: Definition =
      Definition("HmmaAddressRegRegAddressRegImm", "HMMA", Vec("hmma_sparse_indexedRF_"))
    val HmmaRegRegRegReg: Definition = Definition(
      "HmmaRegRegRegReg",
      "HMMA",
      Vec(
        "hmma_848__444_f16_f16",
        "hmma_848__444_f32",
        "hmma_848__884_f16_f16",
        "hmma_848__884_f32",
        "hmma_848_old_row_col__444_f16_f16",
        "hmma_848_old_row_col__444_f32",
        "hmma_848_old_row_col__884_f16_f16",
        "hmma_848_old_row_col__884_f32",
        "hmma__444_f16_f16",
        "hmma__444_f32",
        "hmma__884_f16_f16",
        "hmma__884_f32",
        "hmma_old_row_col__444_f16_f16",
        "hmma_old_row_col__444_f32",
        "hmma_old_row_col__884_f16_f16",
        "hmma_old_row_col__884_f32",
        "hmma_x8_"
      )
    )
    val HmmaRegRegRegRegPred: Definition = Definition("HmmaRegRegRegRegPred", "HMMA", Vec("hmma_x8_"))
    val HmmaRegRegRegRegPredRegImm: Definition = Definition("HmmaRegRegRegRegPredRegImm", "HMMA", Vec("hmma_sparse_"))
    val HmmaRegRegRegRegRegImm: Definition = Definition("HmmaRegRegRegRegRegImm", "HMMA", Vec("hmma_sparse_"))
    val Hmnmx2RegPredPredRegAddressImmPred: Definition =
      Definition("Hmnmx2RegPredPredRegAddressImmPred", "HMNMX2", Vec("hmnmx2_pred__RCxR", "hmnmx2_pred_fixed__RCxR"))
    val Hmnmx2RegPredPredRegConstPred: Definition =
      Definition("Hmnmx2RegPredPredRegConstPred", "HMNMX2", Vec("hmnmx2_pred__RCR", "hmnmx2_pred_fixed__RCR"))
    val Hmnmx2RegPredPredRegImmImmPred: Definition =
      Definition("Hmnmx2RegPredPredRegImmImmPred", "HMNMX2", Vec("hmnmx2_pred__RIR", "hmnmx2_pred_fixed__RIR"))
    val Hmnmx2RegPredPredRegRegPred: Definition = Definition(
      "Hmnmx2RegPredPredRegRegPred",
      "HMNMX2",
      Vec("hmnmx2_pred__RRR", "hmnmx2_pred__RUR", "hmnmx2_pred_fixed__RRR", "hmnmx2_pred_fixed__RUR")
    )
    val Hmnmx2RegRegAddressImmPred: Definition =
      Definition("Hmnmx2RegRegAddressImmPred", "HMNMX2", Vec("hmnmx2__RCxR", "hmnmx2_fixed__RCxR"))
    val Hmnmx2RegRegConstPred: Definition =
      Definition("Hmnmx2RegRegConstPred", "HMNMX2", Vec("hmnmx2__RCR", "hmnmx2_fixed__RCR"))
    val Hmnmx2RegRegImmImmPred: Definition =
      Definition("Hmnmx2RegRegImmImmPred", "HMNMX2", Vec("hmnmx2__RIR", "hmnmx2_fixed__RIR"))
    val Hmnmx2RegRegRegPred: Definition = Definition(
      "Hmnmx2RegRegRegPred",
      "HMNMX2",
      Vec("hmnmx2__RRR", "hmnmx2__RUR", "hmnmx2_fixed__RRR", "hmnmx2_fixed__RUR")
    )
    val Hmul2NoOperands: Definition = Definition("Hmul2NoOperands", "HMUL2", Vec("hmul2__v2"))
    val Hmul2Const: Definition = Definition("Hmul2Const", "HMUL2", Vec("hmul2__v0"))
    val Hmul2ImmImm: Definition = Definition("Hmul2ImmImm", "HMUL2", Vec("hmul2__v1"))
    val Hmul2RegRegAddressImm: Definition = Definition("Hmul2RegRegAddressImm", "HMUL2", Vec("hmul2__RCx"))
    val Hmul2RegRegConst: Definition = Definition("Hmul2RegRegConst", "HMUL2", Vec("hmul2__RC"))
    val Hmul2RegRegImmImm: Definition = Definition("Hmul2RegRegImmImm", "HMUL2", Vec("hmul2__RI", "hmul2_fixed__RI"))
    val Hmul2RegRegReg: Definition =
      Definition("Hmul2RegRegReg", "HMUL2", Vec("hmul2__RR", "hmul2__RU", "hmul2_fixed__RR", "hmul2_fixed__RU"))
    val Hmul232iImmImm: Definition = Definition("Hmul232iImmImm", "HMUL2_32I", Vec("hmul2_32i_"))
    val Hmul232iRegRegImmImm: Definition =
      Definition("Hmul232iRegRegImmImm", "HMUL2_32I", Vec("hmul2_32i_", "hmul2_32i_fixed_"))
    val Hset2NoOperands: Definition = Definition("Hset2NoOperands", "HSET2", Vec("hset2_bop__v2"))
    val Hset2Const: Definition = Definition("Hset2Const", "HSET2", Vec("hset2_bop__v0"))
    val Hset2ConstPred: Definition = Definition("Hset2ConstPred", "HSET2", Vec("hset2__v0"))
    val Hset2ImmImm: Definition = Definition("Hset2ImmImm", "HSET2", Vec("hset2_bop__v1"))
    val Hset2ImmImmPred: Definition = Definition("Hset2ImmImmPred", "HSET2", Vec("hset2__v1"))
    val Hset2Pred: Definition = Definition("Hset2Pred", "HSET2", Vec("hset2__v2"))
    val Hset2RegRegAddressImm: Definition = Definition("Hset2RegRegAddressImm", "HSET2", Vec("hset2_noBop__RCx"))
    val Hset2RegRegAddressImmPred: Definition = Definition("Hset2RegRegAddressImmPred", "HSET2", Vec("hset2__RCx"))
    val Hset2RegRegConst: Definition = Definition("Hset2RegRegConst", "HSET2", Vec("hset2_noBop__RC"))
    val Hset2RegRegConstPred: Definition = Definition("Hset2RegRegConstPred", "HSET2", Vec("hset2__RC"))
    val Hset2RegRegImmImm: Definition =
      Definition("Hset2RegRegImmImm", "HSET2", Vec("hset2_noBop__RI", "hset2_noBop_fixed__RI"))
    val Hset2RegRegImmImmPred: Definition =
      Definition("Hset2RegRegImmImmPred", "HSET2", Vec("hset2__RI", "hset2_fixed__RI"))
    val Hset2RegRegReg: Definition = Definition(
      "Hset2RegRegReg",
      "HSET2",
      Vec("hset2_noBop__RR", "hset2_noBop__RU", "hset2_noBop_fixed__RR", "hset2_noBop_fixed__RU")
    )
    val Hset2RegRegRegPred: Definition =
      Definition("Hset2RegRegRegPred", "HSET2", Vec("hset2__RR", "hset2__RU", "hset2_fixed__RR", "hset2_fixed__RU"))
    val Hsetp2PredPredRegAddressImm: Definition =
      Definition("Hsetp2PredPredRegAddressImm", "HSETP2", Vec("hsetp2_noBop__RCx"))
    val Hsetp2PredPredRegAddressImmPred: Definition =
      Definition("Hsetp2PredPredRegAddressImmPred", "HSETP2", Vec("hsetp2__RCx"))
    val Hsetp2PredPredRegConst: Definition =
      Definition("Hsetp2PredPredRegConst", "HSETP2", Vec("NoBop_Const_HSETP2", "hsetp2_noBop__RC"))
    val Hsetp2PredPredRegConstPred: Definition =
      Definition("Hsetp2PredPredRegConstPred", "HSETP2", Vec("Const_HSETP2", "hsetp2__RC"))
    val Hsetp2PredPredRegImmImm: Definition = Definition(
      "Hsetp2PredPredRegImmImm",
      "HSETP2",
      Vec("NoBop_Imm_HSETP2", "hsetp2_noBop__RI", "hsetp2_noBop_fixed__RI")
    )
    val Hsetp2PredPredRegImmImmPred: Definition =
      Definition("Hsetp2PredPredRegImmImmPred", "HSETP2", Vec("Imm_HSETP2", "hsetp2__RI", "hsetp2_fixed__RI"))
    val Hsetp2PredPredRegReg: Definition = Definition(
      "Hsetp2PredPredRegReg",
      "HSETP2",
      Vec("NoBop_HSETP2", "hsetp2_noBop__RR", "hsetp2_noBop__RU", "hsetp2_noBop_fixed__RR", "hsetp2_noBop_fixed__RU")
    )
    val Hsetp2PredPredRegRegPred: Definition = Definition(
      "Hsetp2PredPredRegRegPred",
      "HSETP2",
      Vec("HSETP2", "hsetp2__RR", "hsetp2__RU", "hsetp2_fixed__RR", "hsetp2_fixed__RU")
    )
    val I2fRegAddressImm: Definition = Definition(
      "I2fRegAddressImm",
      "I2F",
      Vec(
        "i2f_Rd64__CX_16b",
        "i2f_Rd64__CX_32b",
        "i2f_Rd64__CX_64b",
        "i2f_Rd64__CX_8b",
        "i2f__CX_16b",
        "i2f__CX_32b",
        "i2f__CX_64b",
        "i2f__CX_8b"
      )
    )
    val I2fRegConst: Definition = Definition(
      "I2fRegConst",
      "I2F",
      Vec(
        "Const I2F",
        "Const I2F16",
        "Const_I2F",
        "Const_I2F16",
        "Const_I2F64",
        "Const_I2F_F64",
        "i2f_Rd64__Cb_16b",
        "i2f_Rd64__Cb_32b",
        "i2f_Rd64__Cb_64b",
        "i2f_Rd64__Cb_8b",
        "i2f__Cb_16b",
        "i2f__Cb_32b",
        "i2f__Cb_64b",
        "i2f__Cb_8b"
      )
    )
    val I2fRegImm: Definition = Definition(
      "I2fRegImm",
      "I2F",
      Vec(
        "Imm I2F",
        "Imm I2F16",
        "Imm_I2F",
        "Imm_I2F16",
        "Imm_I2F64",
        "Imm_I2F_F64",
        "i2f_Rd64__IS_32b",
        "i2f_Rd64__IS_64b",
        "i2f_Rd64__IU_32b",
        "i2f_Rd64__IU_64b",
        "i2f__IS_32b",
        "i2f__IS_64b",
        "i2f__IU_32b",
        "i2f__IU_64b"
      )
    )
    val I2fRegReg: Definition = Definition(
      "I2fRegReg",
      "I2F",
      Vec(
        "I2F",
        "I2F16",
        "I2F64",
        "I2F_F64",
        "i2f_Rd64__Rb_16b",
        "i2f_Rd64__Rb_32b",
        "i2f_Rd64__Rb_64b",
        "i2f_Rd64__Rb_8b",
        "i2f_Rd64__UR_16b",
        "i2f_Rd64__UR_32b",
        "i2f_Rd64__UR_64b",
        "i2f_Rd64__UR_8b",
        "i2f__Rb_16b",
        "i2f__Rb_32b",
        "i2f__Rb_64b",
        "i2f__Rb_8b",
        "i2f__UR_16b",
        "i2f__UR_32b",
        "i2f__UR_64b",
        "i2f__UR_8b"
      )
    )
    val I2fpRegAddressImm: Definition = Definition("I2fpRegAddressImm", "I2FP", Vec("i2fp__RCxR"))
    val I2fpRegConst: Definition = Definition("I2fpRegConst", "I2FP", Vec("i2fp__RCR"))
    val I2fpRegImm: Definition = Definition("I2fpRegImm", "I2FP", Vec("i2fp__RsIR", "i2fp__RuIR"))
    val I2fpRegReg: Definition =
      Definition("I2fpRegReg", "I2FP", Vec("i2fp__RRR", "i2fp__RUR", "i2fp__Rbx2", "i2fp__URbx2"))
    val I2iRegAddressImm: Definition = Definition("I2iRegAddressImm", "I2I", Vec("i2i__RCxR_RCxR"))
    val I2iRegConst: Definition =
      Definition("I2iRegConst", "I2I", Vec("Const I2I", "Const I2I16", "Const_I2I", "Const_I2I16", "i2i__RCR_RCR"))
    val I2iRegImm: Definition =
      Definition("I2iRegImm", "I2I", Vec("Imm I2I", "Imm I2I16", "Imm_I2I", "Imm_I2I16", "i2i__RsIR_RIR"))
    val I2iRegReg: Definition = Definition("I2iRegReg", "I2I", Vec("I2I", "I2I16", "i2i__RRR_RRR", "i2i__RUR_RUR"))
    val I2ipRegRegAddressImmReg: Definition = Definition(
      "I2ipRegRegAddressImmReg",
      "I2IP",
      Vec(
        "i2ip_24__RCxR_RCxR",
        "i2ip_24_relu__RCxR_RCxR",
        "i2ip_28__RCxR_RCxR",
        "i2ip_28_relu__RCxR_RCxR",
        "i2ip__RCxR_RCxR",
        "i2ip_relu__RCxR_RCxR"
      )
    )
    val I2ipRegRegConstReg: Definition = Definition(
      "I2ipRegRegConstReg",
      "I2IP",
      Vec(
        "i2ip_24__RCR_RCR",
        "i2ip_24_relu__RCR_RCR",
        "i2ip_28__RCR_RCR",
        "i2ip_28_relu__RCR_RCR",
        "i2ip__RCR_RCR",
        "i2ip_relu__RCR_RCR"
      )
    )
    val I2ipRegRegImmReg: Definition = Definition(
      "I2ipRegRegImmReg",
      "I2IP",
      Vec(
        "i2ip_24__RsIR_RIR",
        "i2ip_24_relu__RsIR_RIR",
        "i2ip_28__RsIR_RIR",
        "i2ip_28_relu__RsIR_RIR",
        "i2ip__RsIR_RIR",
        "i2ip_relu__RsIR_RIR"
      )
    )
    val I2ipRegRegRegReg: Definition = Definition(
      "I2ipRegRegRegReg",
      "I2IP",
      Vec(
        "i2ip_24__RRR_RRR",
        "i2ip_24__RUR_RUR",
        "i2ip_24_relu__RRR_RRR",
        "i2ip_24_relu__RUR_RUR",
        "i2ip_28__RRR_RRR",
        "i2ip_28__RUR_RUR",
        "i2ip_28_relu__RRR_RRR",
        "i2ip_28_relu__RUR_RUR",
        "i2ip__RRR_RRR",
        "i2ip__RUR_RUR",
        "i2ip_relu__RRR_RRR",
        "i2ip_relu__RUR_RUR"
      )
    )
    val IabsRegAddressImm: Definition = Definition("IabsRegAddressImm", "IABS", Vec("iabs__RCxR_Cx"))
    val IabsRegConst: Definition = Definition("IabsRegConst", "IABS", Vec("iabs__RCR_C"))
    val IabsRegImm: Definition = Definition("IabsRegImm", "IABS", Vec("iabs__RsIR_I"))
    val IabsRegReg: Definition = Definition("IabsRegReg", "IABS", Vec("iabs__RRR_R", "iabs__RUR_UR"))
    val IaddRegPredRegAddressImm: Definition =
      Definition("IaddRegPredRegAddressImm", "IADD", Vec("iadd_noimm__RCxR_RCxR"))
    val IaddRegPredRegAddressImmPred: Definition =
      Definition("IaddRegPredRegAddressImmPred", "IADD", Vec("iadd_x_noimm__RCxR_RCxR"))
    val IaddRegPredRegConst: Definition = Definition("IaddRegPredRegConst", "IADD", Vec("iadd_noimm__RCR_RCR"))
    val IaddRegPredRegConstPred: Definition =
      Definition("IaddRegPredRegConstPred", "IADD", Vec("iadd_x_noimm__RCR_RCR"))
    val IaddRegPredRegImm: Definition =
      Definition("IaddRegPredRegImm", "IADD", Vec("iadd_64_imm__RsIR_RIR", "iadd_imm__RsIR_RIR"))
    val IaddRegPredRegImmPred: Definition =
      Definition("IaddRegPredRegImmPred", "IADD", Vec("iadd_64_x_imm__RIR_RIR", "iadd_x_imm__RIR_RIR"))
    val IaddRegPredRegImmReg: Definition = Definition("IaddRegPredRegImmReg", "IADD", Vec("iadd_xor_imm__RsIR_RIR"))
    val IaddRegPredRegImmRegPred: Definition =
      Definition("IaddRegPredRegImmRegPred", "IADD", Vec("iadd_x_xor_imm__RIR_RIR"))
    val IaddRegPredRegReg: Definition = Definition(
      "IaddRegPredRegReg",
      "IADD",
      Vec("iadd_64_noimm__RRR_RRR", "iadd_64_noimm__RUR_RUR", "iadd_noimm__RRR_RRR", "iadd_noimm__RUR_RUR")
    )
    val IaddRegPredRegRegPred: Definition = Definition(
      "IaddRegPredRegRegPred",
      "IADD",
      Vec("iadd_64_x_noimm__RRR_RRR", "iadd_64_x_noimm__RUR_RUR", "iadd_x_noimm__RRR_RRR", "iadd_x_noimm__RUR_RUR")
    )
    val IaddRegPredRegRegReg: Definition =
      Definition("IaddRegPredRegRegReg", "IADD", Vec("iadd_xor_noimm__RRR_RRR", "iadd_xor_noimm__RUR_RUR"))
    val IaddRegPredRegRegRegPred: Definition =
      Definition("IaddRegPredRegRegRegPred", "IADD", Vec("iadd_x_xor_noimm__RRR_RRR", "iadd_x_xor_noimm__RUR_RUR"))
    val IaddRegRegAddressImm: Definition = Definition("IaddRegRegAddressImm", "IADD", Vec("iadd_noimm__RCxR_RCxR"))
    val IaddRegRegAddressImmPred: Definition =
      Definition("IaddRegRegAddressImmPred", "IADD", Vec("iadd_x_noimm__RCxR_RCxR"))
    val IaddRegRegConst: Definition =
      Definition("IaddRegRegConst", "IADD", Vec("Const IADD", "Const_IADD", "iadd_noimm__RCR_RCR"))
    val IaddRegRegConstPred: Definition = Definition("IaddRegRegConstPred", "IADD", Vec("iadd_x_noimm__RCR_RCR"))
    val IaddRegRegImm: Definition =
      Definition("IaddRegRegImm", "IADD", Vec("Imm IADD", "Imm_IADD", "iadd_64_imm__RsIR_RIR", "iadd_imm__RsIR_RIR"))
    val IaddRegRegImmPred: Definition =
      Definition("IaddRegRegImmPred", "IADD", Vec("iadd_64_x_imm__RIR_RIR", "iadd_x_imm__RIR_RIR"))
    val IaddRegRegImmReg: Definition = Definition("IaddRegRegImmReg", "IADD", Vec("iadd_xor_imm__RsIR_RIR"))
    val IaddRegRegImmRegPred: Definition = Definition("IaddRegRegImmRegPred", "IADD", Vec("iadd_x_xor_imm__RIR_RIR"))
    val IaddRegRegReg: Definition = Definition(
      "IaddRegRegReg",
      "IADD",
      Vec("IADD", "iadd_64_noimm__RRR_RRR", "iadd_64_noimm__RUR_RUR", "iadd_noimm__RRR_RRR", "iadd_noimm__RUR_RUR")
    )
    val IaddRegRegRegPred: Definition = Definition(
      "IaddRegRegRegPred",
      "IADD",
      Vec("iadd_64_x_noimm__RRR_RRR", "iadd_64_x_noimm__RUR_RUR", "iadd_x_noimm__RRR_RRR", "iadd_x_noimm__RUR_RUR")
    )
    val IaddRegRegRegReg: Definition =
      Definition("IaddRegRegRegReg", "IADD", Vec("iadd_xor_noimm__RRR_RRR", "iadd_xor_noimm__RUR_RUR"))
    val IaddRegRegRegRegPred: Definition =
      Definition("IaddRegRegRegRegPred", "IADD", Vec("iadd_x_xor_noimm__RRR_RRR", "iadd_x_xor_noimm__RUR_RUR"))
    val Iadd3RegPredPredRegAddressImmReg: Definition =
      Definition("Iadd3RegPredPredRegAddressImmReg", "IADD3", Vec("iadd3_noimm__RCxR_RCxR"))
    val Iadd3RegPredPredRegAddressImmRegPredPred: Definition =
      Definition("Iadd3RegPredPredRegAddressImmRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCxR_RCxR"))
    val Iadd3RegPredPredRegConstReg: Definition =
      Definition("Iadd3RegPredPredRegConstReg", "IADD3", Vec("iadd3_noimm__RCR_RCR"))
    val Iadd3RegPredPredRegConstRegPredPred: Definition =
      Definition("Iadd3RegPredPredRegConstRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCR_RCR"))
    val Iadd3RegPredPredRegImmReg: Definition =
      Definition("Iadd3RegPredPredRegImmReg", "IADD3", Vec("iadd3_imm__RsIR_RIR"))
    val Iadd3RegPredPredRegImmRegPredPred: Definition =
      Definition("Iadd3RegPredPredRegImmRegPredPred", "IADD3", Vec("iadd3_x_imm__RIR_RIR"))
    val Iadd3RegPredPredRegRegReg: Definition =
      Definition("Iadd3RegPredPredRegRegReg", "IADD3", Vec("iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR"))
    val Iadd3RegPredPredRegRegRegPredPred: Definition =
      Definition("Iadd3RegPredPredRegRegRegPredPred", "IADD3", Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR"))
    val Iadd3RegPredRegAddressImmReg: Definition =
      Definition("Iadd3RegPredRegAddressImmReg", "IADD3", Vec("iadd3_noimm__RCxR_RCxR"))
    val Iadd3RegPredRegAddressImmRegPredPred: Definition =
      Definition("Iadd3RegPredRegAddressImmRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCxR_RCxR"))
    val Iadd3RegPredRegConstReg: Definition =
      Definition("Iadd3RegPredRegConstReg", "IADD3", Vec("iadd3_noimm__RCR_RCR"))
    val Iadd3RegPredRegConstRegPredPred: Definition =
      Definition("Iadd3RegPredRegConstRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCR_RCR"))
    val Iadd3RegPredRegImmReg: Definition = Definition("Iadd3RegPredRegImmReg", "IADD3", Vec("iadd3_imm__RsIR_RIR"))
    val Iadd3RegPredRegImmRegPredPred: Definition =
      Definition("Iadd3RegPredRegImmRegPredPred", "IADD3", Vec("iadd3_x_imm__RIR_RIR"))
    val Iadd3RegPredRegRegReg: Definition =
      Definition("Iadd3RegPredRegRegReg", "IADD3", Vec("iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR"))
    val Iadd3RegPredRegRegRegPredPred: Definition =
      Definition("Iadd3RegPredRegRegRegPredPred", "IADD3", Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR"))
    val Iadd3RegRegAddressImmReg: Definition =
      Definition("Iadd3RegRegAddressImmReg", "IADD3", Vec("iadd3_noimm__RCxR_RCxR"))
    val Iadd3RegRegAddressImmRegPredPred: Definition =
      Definition("Iadd3RegRegAddressImmRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCxR_RCxR"))
    val Iadd3RegRegConstReg: Definition =
      Definition("Iadd3RegRegConstReg", "IADD3", Vec("BConst_IADD3", "iadd3_noimm__RCR_RCR"))
    val Iadd3RegRegConstRegPredPred: Definition =
      Definition("Iadd3RegRegConstRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCR_RCR"))
    val Iadd3RegRegImmReg: Definition =
      Definition("Iadd3RegRegImmReg", "IADD3", Vec("BImm_IADD3", "iadd3_imm__RsIR_RIR"))
    val Iadd3RegRegImmRegPredPred: Definition =
      Definition("Iadd3RegRegImmRegPredPred", "IADD3", Vec("iadd3_x_imm__RIR_RIR"))
    val Iadd3RegRegRegReg: Definition =
      Definition("Iadd3RegRegRegReg", "IADD3", Vec("IADD3", "iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR"))
    val Iadd3RegRegRegRegPredPred: Definition =
      Definition("Iadd3RegRegRegRegPredPred", "IADD3", Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR"))
    val Iadd32iRegPredRegImm: Definition = Definition("Iadd32iRegPredRegImm", "IADD32I", Vec("iadd32i_imm__RsIR_RIR"))
    val Iadd32iRegPredRegImmPred: Definition =
      Definition("Iadd32iRegPredRegImmPred", "IADD32I", Vec("iadd32i_x_imm__RIR_RIR"))
    val Iadd32iRegRegImm: Definition =
      Definition("Iadd32iRegRegImm", "IADD32I", Vec("IADD32I", "iadd32i_imm__RsIR_RIR"))
    val Iadd32iRegRegImmPred: Definition = Definition("Iadd32iRegRegImmPred", "IADD32I", Vec("iadd32i_x_imm__RIR_RIR"))
    val IcmpRegRegConstReg: Definition =
      Definition("IcmpRegRegConstReg", "ICMP", Vec("Const ICMP", "Const ICMP U", "Const_ICMP", "Const_ICMP_U"))
    val IcmpRegRegImmReg: Definition =
      Definition("IcmpRegRegImmReg", "ICMP", Vec("Imm ICMP", "Imm ICMP U", "Imm_ICMP", "Imm_ICMP_U"))
    val IcmpRegRegRegConst: Definition =
      Definition("IcmpRegRegRegConst", "ICMP", Vec("Const1 ICMP", "Const1 ICMP U", "Const1_ICMP", "Const1_ICMP_U"))
    val IcmpRegRegRegReg: Definition = Definition("IcmpRegRegRegReg", "ICMP", Vec("ICMP", "ICMP U", "ICMP_U"))
    val IdeNoOperands: Definition = Definition("IdeNoOperands", "IDE", Vec("IDE", "IDE_DI", "IDE_EN", "ide_"))
    val IdeImm: Definition = Definition("IdeImm", "IDE", Vec("IDE", "IDE_DI", "IDE_EN", "ide_"))
    val IdpRegRegAddressImmReg: Definition =
      Definition("IdpRegRegAddressImmReg", "IDP", Vec("idp_2a__CXb", "idp_4a__CXb"))
    val IdpRegRegConstReg: Definition =
      Definition("IdpRegRegConstReg", "IDP", Vec("IDP_4A_c", "IDP_c", "idp_2a__C", "idp_4a__C"))
    val IdpRegRegRegReg: Definition = Definition(
      "IdpRegRegRegReg",
      "IDP",
      Vec("IDP_2A", "IDP_4A", "idp_2a__R", "idp_2a__URb", "idp_4a__R", "idp_4a__URb")
    )
    val Idp4aRegRegAddressImmReg: Definition = Definition("Idp4aRegRegAddressImmReg", "IDP4A", Vec("idp4a__CXb"))
    val Idp4aRegRegConstReg: Definition = Definition("Idp4aRegRegConstReg", "IDP4A", Vec("IDP4A_c", "idp4a__C"))
    val Idp4aRegRegRegReg: Definition = Definition("Idp4aRegRegRegReg", "IDP4A", Vec("IDP4A", "idp4a__R", "idp4a__URb"))
    val IgmmaRegAddressReg: Definition = Definition("IgmmaRegAddressReg", "IGMMA", Vec("igmma_URa_Rc_"))
    val IgmmaRegAddressRegPred: Definition = Definition("IgmmaRegAddressRegPred", "IGMMA", Vec("igmma_URa_Rc_"))
    val definitions: Vec[Definition] = Vec(
      FcmpRegRegRegReg,
      FenceNoOperands,
      FfmaRegRegAddressImmReg,
      FfmaRegRegConstReg,
      FfmaRegRegImmReg,
      FfmaRegRegRegAddressImm,
      FfmaRegRegRegConst,
      FfmaRegRegRegImm,
      FfmaRegRegRegReg,
      Ffma2RegRegImmReg,
      Ffma2RegRegRegImm,
      Ffma2RegRegRegReg,
      Ffma32iRegRegImm,
      Ffma32iRegRegImmReg,
      FhaddRegRegReg,
      FhfmaRegRegRegReg,
      FloRegAddressImm,
      FloRegConst,
      FloRegImm,
      FloRegPredAddressImm,
      FloRegPredConst,
      FloRegPredImm,
      FloRegPredReg,
      FloRegReg,
      FmnmxRegPredRegAddressImmPred,
      FmnmxRegPredRegConstPred,
      FmnmxRegPredRegImmPred,
      FmnmxRegPredRegRegPred,
      FmnmxRegRegAddressImmPred,
      FmnmxRegRegConstPred,
      FmnmxRegRegImmPred,
      FmnmxRegRegRegPred,
      Fmnmx3RegRegImmRegPred,
      Fmnmx3RegRegRegRegPred,
      FmulRegRegAddressImm,
      FmulRegRegConst,
      FmulRegRegImm,
      FmulRegRegReg,
      Fmul2RegRegImm,
      Fmul2RegRegReg,
      Fmul32iRegRegImm,
      FootprintPredRegRegRegReg,
      FootprintPredRegRegRegRegImmImm,
      FootprintPredRegRegRegRegImmImmImm,
      FootprintPredRegRegRegRegReg,
      FootprintPredRegRegRegRegRegImm,
      FootprintRegRegRegReg,
      FootprintRegRegRegRegImmImm,
      FootprintRegRegRegRegImmImmImm,
      FootprintRegRegRegRegReg,
      FootprintRegRegRegRegRegImm,
      FrndRegAddressImm,
      FrndRegConst,
      FrndRegImm,
      FrndRegReg,
      FselRegRegAddressImmPred,
      FselRegRegConstPred,
      FselRegRegImmPred,
      FselRegRegRegPred,
      FsetRegRegAddressImm,
      FsetRegRegAddressImmPred,
      FsetRegRegConst,
      FsetRegRegConstPred,
      FsetRegRegImm,
      FsetRegRegImmPred,
      FsetRegRegReg,
      FsetRegRegRegPred,
      FsetpPredPredRegAddressImmPred,
      FsetpPredPredRegConstPred,
      FsetpPredPredRegImmPred,
      FsetpPredPredRegRegPred,
      FsetpPredRegAddressImm,
      FsetpPredRegConst,
      FsetpPredRegImm,
      FsetpPredRegReg,
      FswzRegRegReg,
      FswzaddRegRegReg,
      GatherRegRegRegRegImm,
      GatherRegRegRegRegImmImm,
      GatherRegRegRegRegImmImmImm,
      GetcrsptrReg,
      GetlmembaseReg,
      Hadd2NoOperands,
      Hadd2Const,
      Hadd2ImmImm,
      Hadd2RegRegAddressImm,
      Hadd2RegRegConst,
      Hadd2RegRegImm,
      Hadd2RegRegImmImm,
      Hadd2RegRegReg,
      Hadd232iImmImm,
      Hadd232iRegRegImmImm,
      Hfma2NoOperands,
      Hfma2Const,
      Hfma2ImmImm,
      Hfma2RegRegAddressImmReg,
      Hfma2RegRegAddressImmRegPred,
      Hfma2RegRegConstReg,
      Hfma2RegRegConstRegPred,
      Hfma2RegRegImmImmReg,
      Hfma2RegRegImmImmRegPred,
      Hfma2RegRegRegAddressImm,
      Hfma2RegRegRegAddressImmPred,
      Hfma2RegRegRegConst,
      Hfma2RegRegRegConstPred,
      Hfma2RegRegRegImmImm,
      Hfma2RegRegRegImmImmPred,
      Hfma2RegRegRegReg,
      Hfma2RegRegRegRegPred,
      Hfma232iRegRegImmImm,
      Hfma232iRegRegImmImmReg,
      HgmmaRegAddressReg,
      HgmmaRegAddressRegPred,
      HgmmaRegAddressRegPredRegImm,
      HgmmaRegAddressRegReg,
      HgmmaRegAddressRegRegImm,
      HgmmaRegAddressRegRegPred,
      HgmmaRegAddressRegRegPredRegImm,
      HgmmaRegAddressRegRegRegImm,
      HgmmaRegRegAddressReg,
      HgmmaRegRegAddressRegPred,
      HgmmaRegRegAddressRegPredRegImm,
      HgmmaRegRegAddressRegRegImm,
      HmmaAddressRegRegAddress,
      HmmaAddressRegRegAddressPred,
      HmmaAddressRegRegAddressPredRegImm,
      HmmaAddressRegRegAddressRegImm,
      HmmaRegRegRegReg,
      HmmaRegRegRegRegPred,
      HmmaRegRegRegRegPredRegImm,
      HmmaRegRegRegRegRegImm,
      Hmnmx2RegPredPredRegAddressImmPred,
      Hmnmx2RegPredPredRegConstPred,
      Hmnmx2RegPredPredRegImmImmPred,
      Hmnmx2RegPredPredRegRegPred,
      Hmnmx2RegRegAddressImmPred,
      Hmnmx2RegRegConstPred,
      Hmnmx2RegRegImmImmPred,
      Hmnmx2RegRegRegPred,
      Hmul2NoOperands,
      Hmul2Const,
      Hmul2ImmImm,
      Hmul2RegRegAddressImm,
      Hmul2RegRegConst,
      Hmul2RegRegImmImm,
      Hmul2RegRegReg,
      Hmul232iImmImm,
      Hmul232iRegRegImmImm,
      Hset2NoOperands,
      Hset2Const,
      Hset2ConstPred,
      Hset2ImmImm,
      Hset2ImmImmPred,
      Hset2Pred,
      Hset2RegRegAddressImm,
      Hset2RegRegAddressImmPred,
      Hset2RegRegConst,
      Hset2RegRegConstPred,
      Hset2RegRegImmImm,
      Hset2RegRegImmImmPred,
      Hset2RegRegReg,
      Hset2RegRegRegPred,
      Hsetp2PredPredRegAddressImm,
      Hsetp2PredPredRegAddressImmPred,
      Hsetp2PredPredRegConst,
      Hsetp2PredPredRegConstPred,
      Hsetp2PredPredRegImmImm,
      Hsetp2PredPredRegImmImmPred,
      Hsetp2PredPredRegReg,
      Hsetp2PredPredRegRegPred,
      I2fRegAddressImm,
      I2fRegConst,
      I2fRegImm,
      I2fRegReg,
      I2fpRegAddressImm,
      I2fpRegConst,
      I2fpRegImm,
      I2fpRegReg,
      I2iRegAddressImm,
      I2iRegConst,
      I2iRegImm,
      I2iRegReg,
      I2ipRegRegAddressImmReg,
      I2ipRegRegConstReg,
      I2ipRegRegImmReg,
      I2ipRegRegRegReg,
      IabsRegAddressImm,
      IabsRegConst,
      IabsRegImm,
      IabsRegReg,
      IaddRegPredRegAddressImm,
      IaddRegPredRegAddressImmPred,
      IaddRegPredRegConst,
      IaddRegPredRegConstPred,
      IaddRegPredRegImm,
      IaddRegPredRegImmPred,
      IaddRegPredRegImmReg,
      IaddRegPredRegImmRegPred,
      IaddRegPredRegReg,
      IaddRegPredRegRegPred,
      IaddRegPredRegRegReg,
      IaddRegPredRegRegRegPred,
      IaddRegRegAddressImm,
      IaddRegRegAddressImmPred,
      IaddRegRegConst,
      IaddRegRegConstPred,
      IaddRegRegImm,
      IaddRegRegImmPred,
      IaddRegRegImmReg,
      IaddRegRegImmRegPred,
      IaddRegRegReg,
      IaddRegRegRegPred,
      IaddRegRegRegReg,
      IaddRegRegRegRegPred,
      Iadd3RegPredPredRegAddressImmReg,
      Iadd3RegPredPredRegAddressImmRegPredPred,
      Iadd3RegPredPredRegConstReg,
      Iadd3RegPredPredRegConstRegPredPred,
      Iadd3RegPredPredRegImmReg,
      Iadd3RegPredPredRegImmRegPredPred,
      Iadd3RegPredPredRegRegReg,
      Iadd3RegPredPredRegRegRegPredPred,
      Iadd3RegPredRegAddressImmReg,
      Iadd3RegPredRegAddressImmRegPredPred,
      Iadd3RegPredRegConstReg,
      Iadd3RegPredRegConstRegPredPred,
      Iadd3RegPredRegImmReg,
      Iadd3RegPredRegImmRegPredPred,
      Iadd3RegPredRegRegReg,
      Iadd3RegPredRegRegRegPredPred,
      Iadd3RegRegAddressImmReg,
      Iadd3RegRegAddressImmRegPredPred,
      Iadd3RegRegConstReg,
      Iadd3RegRegConstRegPredPred,
      Iadd3RegRegImmReg,
      Iadd3RegRegImmRegPredPred,
      Iadd3RegRegRegReg,
      Iadd3RegRegRegRegPredPred,
      Iadd32iRegPredRegImm,
      Iadd32iRegPredRegImmPred,
      Iadd32iRegRegImm,
      Iadd32iRegRegImmPred,
      IcmpRegRegConstReg,
      IcmpRegRegImmReg,
      IcmpRegRegRegConst,
      IcmpRegRegRegReg,
      IdeNoOperands,
      IdeImm,
      IdpRegRegAddressImmReg,
      IdpRegRegConstReg,
      IdpRegRegRegReg,
      Idp4aRegRegAddressImmReg,
      Idp4aRegRegConstReg,
      Idp4aRegRegRegReg,
      IgmmaRegAddressReg,
      IgmmaRegAddressRegPred
    )
  }
  object Definitions2 {
    val IgmmaRegAddressRegPredReg: Definition =
      Definition("IgmmaRegAddressRegPredReg", "IGMMA", Vec("igmma_sparse_URa_Rc_"))
    val IgmmaRegAddressRegPredRegImm: Definition =
      Definition("IgmmaRegAddressRegPredRegImm", "IGMMA", Vec("igmma_sparse_URa_Rc_"))
    val IgmmaRegAddressRegReg: Definition =
      Definition("IgmmaRegAddressRegReg", "IGMMA", Vec("igmma_URa_Rb_Rc_", "igmma_sparse_URa_Rc_"))
    val IgmmaRegAddressRegRegImm: Definition =
      Definition("IgmmaRegAddressRegRegImm", "IGMMA", Vec("igmma_sparse_URa_Rc_"))
    val IgmmaRegAddressRegRegPred: Definition =
      Definition("IgmmaRegAddressRegRegPred", "IGMMA", Vec("igmma_URa_Rb_Rc_"))
    val IgmmaRegAddressRegRegPredReg: Definition =
      Definition("IgmmaRegAddressRegRegPredReg", "IGMMA", Vec("igmma_sparse_URa_Rb_Rc_"))
    val IgmmaRegAddressRegRegPredRegImm: Definition =
      Definition("IgmmaRegAddressRegRegPredRegImm", "IGMMA", Vec("igmma_sparse_URa_Rb_Rc_"))
    val IgmmaRegAddressRegRegReg: Definition =
      Definition("IgmmaRegAddressRegRegReg", "IGMMA", Vec("igmma_sparse_URa_Rb_Rc_"))
    val IgmmaRegAddressRegRegRegImm: Definition =
      Definition("IgmmaRegAddressRegRegRegImm", "IGMMA", Vec("igmma_sparse_URa_Rb_Rc_"))
    val IgmmaRegRegAddressReg: Definition = Definition("IgmmaRegRegAddressReg", "IGMMA", Vec("igmma_Ra_URb_Rc_"))
    val IgmmaRegRegAddressRegPred: Definition =
      Definition("IgmmaRegRegAddressRegPred", "IGMMA", Vec("igmma_Ra_URb_Rc_"))
    val IgmmaRegRegAddressRegPredReg: Definition =
      Definition("IgmmaRegRegAddressRegPredReg", "IGMMA", Vec("igmma_sparse_Ra_URb_Rc_"))
    val IgmmaRegRegAddressRegPredRegImm: Definition =
      Definition("IgmmaRegRegAddressRegPredRegImm", "IGMMA", Vec("igmma_sparse_Ra_URb_Rc_"))
    val IgmmaRegRegAddressRegReg: Definition =
      Definition("IgmmaRegRegAddressRegReg", "IGMMA", Vec("igmma_sparse_Ra_URb_Rc_"))
    val IgmmaRegRegAddressRegRegImm: Definition =
      Definition("IgmmaRegRegAddressRegRegImm", "IGMMA", Vec("igmma_sparse_Ra_URb_Rc_"))
    val ImadRegPredRegAddressImmReg: Definition = Definition(
      "ImadRegPredRegAddressImmReg",
      "IMAD",
      Vec("imad_hi__RCxR_RCxR", "imad_hi_pseudo__RCxR_RCxR", "imad_wide__RCxR_RCxR", "imad_wide_pseudo__RCxR_RCxR")
    )
    val ImadRegPredRegAddressImmRegPred: Definition = Definition(
      "ImadRegPredRegAddressImmRegPred",
      "IMAD",
      Vec(
        "imad_hi_x__RCxR_RCxR",
        "imad_hi_x_pseudo__RCxR_RCxR",
        "imad_wide_x__RCxR_RCxR",
        "imad_wide_x_pseudo__RCxR_RCxR"
      )
    )
    val ImadRegPredRegConstReg: Definition = Definition(
      "ImadRegPredRegConstReg",
      "IMAD",
      Vec("imad_hi__RCR_RCR", "imad_hi_pseudo__RCR_RCR", "imad_wide__RCR_RCR", "imad_wide_pseudo__RCR_RCR")
    )
    val ImadRegPredRegConstRegPred: Definition = Definition(
      "ImadRegPredRegConstRegPred",
      "IMAD",
      Vec("imad_hi_x__RCR_RCR", "imad_hi_x_pseudo__RCR_RCR", "imad_wide_x__RCR_RCR", "imad_wide_x_pseudo__RCR_RCR")
    )
    val ImadRegPredRegImmReg: Definition = Definition(
      "ImadRegPredRegImmReg",
      "IMAD",
      Vec("imad_hi__RsIR_RIR", "imad_hi_pseudo__RsIR_RIR", "imad_wide__RsIR_RIR", "imad_wide_pseudo__RsIR_RIR")
    )
    val ImadRegPredRegImmRegPred: Definition = Definition(
      "ImadRegPredRegImmRegPred",
      "IMAD",
      Vec("imad_hi_x__RsIR_RIR", "imad_hi_x_pseudo__RsIR_RIR", "imad_wide_x__RsIR_RIR", "imad_wide_x_pseudo__RsIR_RIR")
    )
    val ImadRegPredRegRegAddressImm: Definition = Definition(
      "ImadRegPredRegRegAddressImm",
      "IMAD",
      Vec("imad_hi__RRCx_RRCx", "imad_hi_pseudo__RRCx_RRCx", "imad_wide__RRCx_RRCx", "imad_wide_pseudo__RRCx_RRCx")
    )
    val ImadRegPredRegRegAddressImmPred: Definition = Definition(
      "ImadRegPredRegRegAddressImmPred",
      "IMAD",
      Vec(
        "imad_hi_x__RRCx_RRCx",
        "imad_hi_x_pseudo__RRCx_RRCx",
        "imad_wide_x__RRCx_RRCx",
        "imad_wide_x_pseudo__RRCx_RRCx"
      )
    )
    val ImadRegPredRegRegConst: Definition = Definition(
      "ImadRegPredRegRegConst",
      "IMAD",
      Vec("imad_hi__RRC_RRC", "imad_hi_pseudo__RRC_RRC", "imad_wide__RRC_RRC", "imad_wide_pseudo__RRC_RRC")
    )
    val ImadRegPredRegRegConstPred: Definition = Definition(
      "ImadRegPredRegRegConstPred",
      "IMAD",
      Vec("imad_hi_x__RRC_RRC", "imad_hi_x_pseudo__RRC_RRC", "imad_wide_x__RRC_RRC", "imad_wide_x_pseudo__RRC_RRC")
    )
    val ImadRegPredRegRegReg: Definition = Definition(
      "ImadRegPredRegRegReg",
      "IMAD",
      Vec(
        "imad_hi__RRR_RRR",
        "imad_hi__RRU_RRU",
        "imad_hi__RUR_RUR",
        "imad_hi_pseudo__RRR_RRR",
        "imad_hi_pseudo__RRU_RRU",
        "imad_hi_pseudo__RUR_RUR",
        "imad_wide__RRR_RRR",
        "imad_wide__RRU_RRU",
        "imad_wide__RUR_RUR",
        "imad_wide_pseudo__RRR_RRR",
        "imad_wide_pseudo__RRU_RRU",
        "imad_wide_pseudo__RUR_RUR"
      )
    )
    val ImadRegPredRegRegRegPred: Definition = Definition(
      "ImadRegPredRegRegRegPred",
      "IMAD",
      Vec(
        "imad_hi_x__RRR_RRR",
        "imad_hi_x__RRU_RRU",
        "imad_hi_x__RUR_RUR",
        "imad_hi_x_pseudo__RRR_RRR",
        "imad_hi_x_pseudo__RRU_RRU",
        "imad_hi_x_pseudo__RUR_RUR",
        "imad_wide_x__RRR_RRR",
        "imad_wide_x__RRU_RRU",
        "imad_wide_x__RUR_RUR",
        "imad_wide_x_pseudo__RRR_RRR",
        "imad_wide_x_pseudo__RRU_RRU",
        "imad_wide_x_pseudo__RUR_RUR"
      )
    )
    val ImadRegRegAddressImmReg: Definition = Definition(
      "ImadRegRegAddressImmReg",
      "IMAD",
      Vec(
        "imad__RCxR_RCxR",
        "imad_hi__RCxR_RCxR",
        "imad_hi_pseudo__RCxR_RCxR",
        "imad_pseudo__RCxR_RCxR",
        "imad_wide__RCxR_RCxR",
        "imad_wide_pseudo__RCxR_RCxR"
      )
    )
    val ImadRegRegAddressImmRegPred: Definition = Definition(
      "ImadRegRegAddressImmRegPred",
      "IMAD",
      Vec(
        "imad_hi_x__RCxR_RCxR",
        "imad_hi_x_pseudo__RCxR_RCxR",
        "imad_wide_x__RCxR_RCxR",
        "imad_wide_x_pseudo__RCxR_RCxR",
        "imad_x__RCxR_RCxR",
        "imad_x_pseudo__RCxR_RCxR"
      )
    )
    val ImadRegRegConstReg: Definition = Definition(
      "ImadRegRegConstReg",
      "IMAD",
      Vec(
        "Const IMAD",
        "Const_IMAD",
        "imad__RCR_RCR",
        "imad_hi__RCR_RCR",
        "imad_hi_pseudo__RCR_RCR",
        "imad_pseudo__RCR_RCR",
        "imad_wide__RCR_RCR",
        "imad_wide_pseudo__RCR_RCR"
      )
    )
    val ImadRegRegConstRegPred: Definition = Definition(
      "ImadRegRegConstRegPred",
      "IMAD",
      Vec(
        "imad_hi_x__RCR_RCR",
        "imad_hi_x_pseudo__RCR_RCR",
        "imad_wide_x__RCR_RCR",
        "imad_wide_x_pseudo__RCR_RCR",
        "imad_x__RCR_RCR",
        "imad_x_pseudo__RCR_RCR"
      )
    )
    val ImadRegRegImmReg: Definition = Definition(
      "ImadRegRegImmReg",
      "IMAD",
      Vec(
        "Imm IMAD",
        "Imm_IMAD",
        "imad__RsIR_RIR",
        "imad_hi__RsIR_RIR",
        "imad_hi_pseudo__RsIR_RIR",
        "imad_pseudo__RsIR_RIR",
        "imad_wide__RsIR_RIR",
        "imad_wide_pseudo__RsIR_RIR"
      )
    )
    val ImadRegRegImmRegPred: Definition = Definition(
      "ImadRegRegImmRegPred",
      "IMAD",
      Vec(
        "imad_hi_x__RsIR_RIR",
        "imad_hi_x_pseudo__RsIR_RIR",
        "imad_wide_x__RsIR_RIR",
        "imad_wide_x_pseudo__RsIR_RIR",
        "imad_x__RsIR_RIR",
        "imad_x_pseudo__RsIR_RIR"
      )
    )
    val ImadRegRegRegAddressImm: Definition = Definition(
      "ImadRegRegRegAddressImm",
      "IMAD",
      Vec(
        "imad__RRCx_RRCx",
        "imad_hi__RRCx_RRCx",
        "imad_hi_pseudo__RRCx_RRCx",
        "imad_pseudo__RRCx_RRCx",
        "imad_wide__RRCx_RRCx",
        "imad_wide_pseudo__RRCx_RRCx"
      )
    )
    val ImadRegRegRegAddressImmPred: Definition = Definition(
      "ImadRegRegRegAddressImmPred",
      "IMAD",
      Vec(
        "imad_hi_x__RRCx_RRCx",
        "imad_hi_x_pseudo__RRCx_RRCx",
        "imad_wide_x__RRCx_RRCx",
        "imad_wide_x_pseudo__RRCx_RRCx",
        "imad_x__RRCx_RRCx",
        "imad_x_pseudo__RRCx_RRCx"
      )
    )
    val ImadRegRegRegConst: Definition = Definition(
      "ImadRegRegRegConst",
      "IMAD",
      Vec(
        "Const1 IMAD",
        "Const1_IMAD",
        "imad__RRC_RRC",
        "imad_hi__RRC_RRC",
        "imad_hi_pseudo__RRC_RRC",
        "imad_pseudo__RRC_RRC",
        "imad_wide__RRC_RRC",
        "imad_wide_pseudo__RRC_RRC"
      )
    )
    val ImadRegRegRegConstPred: Definition = Definition(
      "ImadRegRegRegConstPred",
      "IMAD",
      Vec(
        "imad_hi_x__RRC_RRC",
        "imad_hi_x_pseudo__RRC_RRC",
        "imad_wide_x__RRC_RRC",
        "imad_wide_x_pseudo__RRC_RRC",
        "imad_x__RRC_RRC",
        "imad_x_pseudo__RRC_RRC"
      )
    )
    val ImadRegRegRegImm: Definition =
      Definition("ImadRegRegRegImm", "IMAD", Vec("imad__RRsI_RRI", "imad_pseudo__RRsI_RRI"))
    val ImadRegRegRegImmPred: Definition =
      Definition("ImadRegRegRegImmPred", "IMAD", Vec("imad_x__RRsI_RRI", "imad_x_pseudo__RRsI_RRI"))
    val ImadRegRegRegReg: Definition = Definition(
      "ImadRegRegRegReg",
      "IMAD",
      Vec(
        "IMAD",
        "imad__RRR_RRR",
        "imad__RRU_RRU",
        "imad__RUR_RUR",
        "imad_hi__RRR_RRR",
        "imad_hi__RRU_RRU",
        "imad_hi__RUR_RUR",
        "imad_hi_pseudo__RRR_RRR",
        "imad_hi_pseudo__RRU_RRU",
        "imad_hi_pseudo__RUR_RUR",
        "imad_pseudo__RRR_RRR",
        "imad_pseudo__RRU_RRU",
        "imad_pseudo__RUR_RUR",
        "imad_wide__RRR_RRR",
        "imad_wide__RRU_RRU",
        "imad_wide__RUR_RUR",
        "imad_wide_pseudo__RRR_RRR",
        "imad_wide_pseudo__RRU_RRU",
        "imad_wide_pseudo__RUR_RUR"
      )
    )
    val ImadRegRegRegRegPred: Definition = Definition(
      "ImadRegRegRegRegPred",
      "IMAD",
      Vec(
        "imad_hi_x__RRR_RRR",
        "imad_hi_x__RRU_RRU",
        "imad_hi_x__RUR_RUR",
        "imad_hi_x_pseudo__RRR_RRR",
        "imad_hi_x_pseudo__RRU_RRU",
        "imad_hi_x_pseudo__RUR_RUR",
        "imad_wide_x__RRR_RRR",
        "imad_wide_x__RRU_RRU",
        "imad_wide_x__RUR_RUR",
        "imad_wide_x_pseudo__RRR_RRR",
        "imad_wide_x_pseudo__RRU_RRU",
        "imad_wide_x_pseudo__RUR_RUR",
        "imad_x__RRR_RRR",
        "imad_x__RRU_RRU",
        "imad_x__RUR_RUR",
        "imad_x_pseudo__RRR_RRR",
        "imad_x_pseudo__RRU_RRU",
        "imad_x_pseudo__RUR_RUR"
      )
    )
    val Imad32iRegRegImmReg: Definition = Definition("Imad32iRegRegImmReg", "IMAD32I", Vec("IMAD32I"))
    val ImadspRegRegConstReg: Definition =
      Definition("ImadspRegRegConstReg", "IMADSP", Vec("Const IMADSP", "Const IMADSP SD", "Const_IMADSP"))
    val ImadspRegRegImmReg: Definition =
      Definition("ImadspRegRegImmReg", "IMADSP", Vec("Imm IMADSP", "Imm IMADSP SD", "Imm_IMADSP"))
    val ImadspRegRegRegConst: Definition =
      Definition("ImadspRegRegRegConst", "IMADSP", Vec("Const1 IMADSP", "Const1 IMADSP SD", "Const1_IMADSP"))
    val ImadspRegRegRegReg: Definition = Definition("ImadspRegRegRegReg", "IMADSP", Vec("IMADSP", "IMADSP SD"))
    val ImmaRegRegRegReg: Definition =
      Definition("ImmaRegRegRegReg", "IMMA", Vec("imma_", "imma_8816_16_8_", "imma_8816_8_8_", "imma_8832_4_4_"))
    val ImmaRegRegRegRegPred: Definition = Definition("ImmaRegRegRegRegPred", "IMMA", Vec("imma_"))
    val ImmaRegRegRegRegPredRegImm: Definition = Definition("ImmaRegRegRegRegPredRegImm", "IMMA", Vec("imma_sp_"))
    val ImmaRegRegRegRegRegImm: Definition = Definition("ImmaRegRegRegRegRegImm", "IMMA", Vec("imma_sp_"))
    val ImnmxPredPredRegRegImmPred: Definition =
      Definition("ImnmxPredPredRegRegImmPred", "IMNMX", Vec("imnmx_64_nopred__RIR_RsIR", "imnmx_nopred__RIR_RsIR"))
    val ImnmxPredPredRegRegImmPredPred: Definition =
      Definition("ImnmxPredPredRegRegImmPredPred", "IMNMX", Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR"))
    val ImnmxPredPredRegRegRegPred: Definition = Definition(
      "ImnmxPredPredRegRegRegPred",
      "IMNMX",
      Vec("imnmx_64_nopred__RRR_RRR", "imnmx_64_nopred__RUR_RUR", "imnmx_nopred__RRR_RRR", "imnmx_nopred__RUR_RUR")
    )
    val ImnmxPredPredRegRegRegPredPred: Definition = Definition(
      "ImnmxPredPredRegRegRegPredPred",
      "IMNMX",
      Vec("imnmx_64__RRR_RRR", "imnmx_64__RUR_RUR", "imnmx__RRR_RRR", "imnmx__RUR_RUR")
    )
    val ImnmxPredRegRegImmPred: Definition =
      Definition("ImnmxPredRegRegImmPred", "IMNMX", Vec("imnmx_64_nopred__RIR_RsIR", "imnmx_nopred__RIR_RsIR"))
    val ImnmxPredRegRegImmPredPred: Definition =
      Definition("ImnmxPredRegRegImmPredPred", "IMNMX", Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR"))
    val ImnmxPredRegRegRegPred: Definition = Definition(
      "ImnmxPredRegRegRegPred",
      "IMNMX",
      Vec("imnmx_64_nopred__RRR_RRR", "imnmx_64_nopred__RUR_RUR", "imnmx_nopred__RRR_RRR", "imnmx_nopred__RUR_RUR")
    )
    val ImnmxPredRegRegRegPredPred: Definition = Definition(
      "ImnmxPredRegRegRegPredPred",
      "IMNMX",
      Vec("imnmx_64__RRR_RRR", "imnmx_64__RUR_RUR", "imnmx__RRR_RRR", "imnmx__RUR_RUR")
    )
    val ImnmxRegRegAddressImmPred: Definition =
      Definition("ImnmxRegRegAddressImmPred", "IMNMX", Vec("imnmx__RCxR_RCxR"))
    val ImnmxRegRegConstPred: Definition =
      Definition("ImnmxRegRegConstPred", "IMNMX", Vec("Const IMNMX", "Const_IMNMX", "imnmx__RCR_RCR"))
    val ImnmxRegRegImmPred: Definition = Definition(
      "ImnmxRegRegImmPred",
      "IMNMX",
      Vec(
        "Imm IMNMX",
        "Imm_IMNMX",
        "imnmx_64_nopred__RIR_RsIR",
        "imnmx__RIR_RsIR",
        "imnmx__RsIR_RIR",
        "imnmx_nopred__RIR_RsIR"
      )
    )
    val ImnmxRegRegImmPredPred: Definition =
      Definition("ImnmxRegRegImmPredPred", "IMNMX", Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR"))
    val ImnmxRegRegRegPred: Definition = Definition(
      "ImnmxRegRegRegPred",
      "IMNMX",
      Vec(
        "IMNMX",
        "imnmx_64_nopred__RRR_RRR",
        "imnmx_64_nopred__RUR_RUR",
        "imnmx__RRR_RRR",
        "imnmx__RUR_RUR",
        "imnmx_nopred__RRR_RRR",
        "imnmx_nopred__RUR_RUR"
      )
    )
    val ImnmxRegRegRegPredPred: Definition = Definition(
      "ImnmxRegRegRegPredPred",
      "IMNMX",
      Vec("imnmx_64__RRR_RRR", "imnmx_64__RUR_RUR", "imnmx__RRR_RRR", "imnmx__RUR_RUR")
    )
    val ImulRegPredRegAddressImm: Definition =
      Definition("ImulRegPredRegAddressImm", "IMUL", Vec("imul_wide__RCxR_RCxR"))
    val ImulRegPredRegConst: Definition = Definition("ImulRegPredRegConst", "IMUL", Vec("imul_wide__RCR_RCR"))
    val ImulRegPredRegImm: Definition = Definition("ImulRegPredRegImm", "IMUL", Vec("imul_wide__RsIR_RIR"))
    val ImulRegPredRegReg: Definition =
      Definition("ImulRegPredRegReg", "IMUL", Vec("imul_wide__RRR_RRR", "imul_wide__RUR_RUR"))
    val ImulRegRegAddressImm: Definition =
      Definition("ImulRegRegAddressImm", "IMUL", Vec("imul__RCxR_RCxR", "imul_wide__RCxR_RCxR"))
    val ImulRegRegConst: Definition =
      Definition("ImulRegRegConst", "IMUL", Vec("Const IMUL", "Const_IMUL", "imul__RCR_RCR", "imul_wide__RCR_RCR"))
    val ImulRegRegImm: Definition =
      Definition("ImulRegRegImm", "IMUL", Vec("Imm IMUL", "Imm_IMUL", "imul__RsIR_RIR", "imul_wide__RsIR_RIR"))
    val ImulRegRegImmReg: Definition =
      Definition("ImulRegRegImmReg", "IMUL", Vec("imul_lo_xor__RsIR_RIR", "imul_wide_xor__RsIR_RIR"))
    val ImulRegRegReg: Definition = Definition(
      "ImulRegRegReg",
      "IMUL",
      Vec("IMUL", "imul__RRR_RRR", "imul__RUR_RUR", "imul_wide__RRR_RRR", "imul_wide__RUR_RUR")
    )
    val ImulRegRegRegImm: Definition = Definition("ImulRegRegRegImm", "IMUL", Vec("imul_lo_xor__RRsI_RRI"))
    val ImulRegRegRegReg: Definition = Definition(
      "ImulRegRegRegReg",
      "IMUL",
      Vec(
        "imul_lo_xor__RRR_RRR",
        "imul_lo_xor__RRU_RRU",
        "imul_lo_xor__RUR_RUR",
        "imul_wide_xor__RRR_RRR",
        "imul_wide_xor__RRU_RRU",
        "imul_wide_xor__RUR_RUR"
      )
    )
    val Imul32iRegPredRegImm: Definition = Definition("Imul32iRegPredRegImm", "IMUL32I", Vec("imul32i_wide__RsIR_RIR"))
    val Imul32iRegRegImm: Definition =
      Definition("Imul32iRegRegImm", "IMUL32I", Vec("IMUL32I", "imul32i_lo__RsIR_RIR", "imul32i_wide__RsIR_RIR"))
    val IpaRegAddress: Definition = Definition("IpaRegAddress", "IPA", Vec("I IPA 2", "IPA 2", "ipa_ur_"))
    val IpaRegAddressImm: Definition = Definition("IpaRegAddressImm", "IPA", Vec("ipa_ur_offset__IPA_URa_Ib"))
    val IpaRegAddressPred: Definition = Definition("IpaRegAddressPred", "IPA", Vec("IPA_2"))
    val IpaRegAddressReg: Definition = Definition("IpaRegAddressReg", "IPA", Vec("ipa_ur_offset__IPA_URa_Rb"))
    val IpaRegAddressRegReg: Definition = Definition("IpaRegAddressRegReg", "IPA", Vec("I IPA 1", "IPA 1"))
    val IpaRegAddressRegRegPred: Definition = Definition("IpaRegAddressRegRegPred", "IPA", Vec("IPA_1"))
    val IpaRegImm: Definition = Definition("IpaRegImm", "IPA", Vec("I_IPA_2", "ipa_"))
    val IpaRegImmConst: Definition = Definition("IpaRegImmConst", "IPA", Vec("ipa_offset__IPA_C"))
    val IpaRegImmImm: Definition = Definition("IpaRegImmImm", "IPA", Vec("ipa_offset__IPA_Ib", "ipa_offset__IPA_Imm"))
    val IpaRegImmPred: Definition = Definition("IpaRegImmPred", "IPA", Vec("I_IPA_2"))
    val IpaRegImmReg: Definition = Definition("IpaRegImmReg", "IPA", Vec("ipa_offset__IPA_Rb"))
    val IpaRegImmRegReg: Definition = Definition("IpaRegImmRegReg", "IPA", Vec("I_IPA_1"))
    val IpaRegImmRegRegPred: Definition = Definition("IpaRegImmRegRegPred", "IPA", Vec("I_IPA_1"))
    val IpaRegPredAddress: Definition = Definition("IpaRegPredAddress", "IPA", Vec("ipa_ur_"))
    val IpaRegPredAddressImm: Definition = Definition("IpaRegPredAddressImm", "IPA", Vec("ipa_ur_offset__IPA_URa_Ib"))
    val IpaRegPredAddressReg: Definition = Definition("IpaRegPredAddressReg", "IPA", Vec("ipa_ur_offset__IPA_URa_Rb"))
    val IpaRegPredImm: Definition = Definition("IpaRegPredImm", "IPA", Vec("ipa_"))
    val IpaRegPredImmConst: Definition = Definition("IpaRegPredImmConst", "IPA", Vec("ipa_offset__IPA_C"))
    val IpaRegPredImmImm: Definition =
      Definition("IpaRegPredImmImm", "IPA", Vec("ipa_offset__IPA_Ib", "ipa_offset__IPA_Imm"))
    val IpaRegPredImmReg: Definition = Definition("IpaRegPredImmReg", "IPA", Vec("ipa_offset__IPA_Rb"))
    val IsadRegRegConstReg: Definition = Definition("IsadRegRegConstReg", "ISAD", Vec("Const ISAD"))
    val IsadRegRegImmReg: Definition = Definition("IsadRegRegImmReg", "ISAD", Vec("Imm ISAD"))
    val IsadRegRegRegConst: Definition = Definition("IsadRegRegRegConst", "ISAD", Vec("Const1 ISAD"))
    val IsadRegRegRegReg: Definition = Definition("IsadRegRegRegReg", "ISAD", Vec("ISAD"))
    val IsberdRegAddress: Definition = Definition("IsberdRegAddress", "ISBERD", Vec("ISBERD", "isberd_"))
    val IsbewrAddressReg: Definition = Definition("IsbewrAddressReg", "ISBEWR", Vec("isbewr_"))
    val IscaddRegPredRegAddressImmImm: Definition =
      Definition("IscaddRegPredRegAddressImmImm", "ISCADD", Vec("iscadd_noimm__RCxR_RCxR"))
    val IscaddRegPredRegConstImm: Definition =
      Definition("IscaddRegPredRegConstImm", "ISCADD", Vec("iscadd_noimm__RCR_RCR"))
    val IscaddRegPredRegImmImm: Definition = Definition("IscaddRegPredRegImmImm", "ISCADD", Vec("iscadd_imm__RIR_RIR"))
    val IscaddRegPredRegRegImm: Definition =
      Definition("IscaddRegPredRegRegImm", "ISCADD", Vec("iscadd_noimm__RRR_RRR", "iscadd_noimm__RUR_RUR"))
    val IscaddRegRegAddressImmImm: Definition =
      Definition("IscaddRegRegAddressImmImm", "ISCADD", Vec("iscadd_noimm__RCxR_RCxR"))
    val IscaddRegRegConstImm: Definition =
      Definition("IscaddRegRegConstImm", "ISCADD", Vec("Const ISCADD", "Const_ISCADD", "iscadd_noimm__RCR_RCR"))
    val IscaddRegRegImmImm: Definition =
      Definition("IscaddRegRegImmImm", "ISCADD", Vec("Imm ISCADD", "Imm_ISCADD", "iscadd_imm__RIR_RIR"))
    val IscaddRegRegRegImm: Definition =
      Definition("IscaddRegRegRegImm", "ISCADD", Vec("ISCADD", "iscadd_noimm__RRR_RRR", "iscadd_noimm__RUR_RUR"))
    val Iscadd32iRegPredRegImmImm: Definition =
      Definition("Iscadd32iRegPredRegImmImm", "ISCADD32I", Vec("iscadd32i__RIR_RIR"))
    val Iscadd32iRegRegImmImm: Definition =
      Definition("Iscadd32iRegRegImmImm", "ISCADD32I", Vec("ISCADD32I", "iscadd32i__RIR_RIR"))
    val IsetRegRegConst: Definition = Definition(
      "IsetRegRegConst",
      "ISET",
      Vec("NoBop Const ISET", "NoBop Const ISET U", "NoBop_Const_ISET", "NoBop_Const_ISET_U")
    )
    val IsetRegRegConstPred: Definition =
      Definition("IsetRegRegConstPred", "ISET", Vec("Const ISET", "Const ISET U", "Const_ISET", "Const_ISET_U"))
    val IsetRegRegImm: Definition = Definition(
      "IsetRegRegImm",
      "ISET",
      Vec("NoBop Imm ISET", "NoBop Imm ISET U", "NoBop_Imm_ISET", "NoBop_Imm_ISET_U")
    )
    val IsetRegRegImmPred: Definition =
      Definition("IsetRegRegImmPred", "ISET", Vec("Imm ISET", "Imm ISET U", "Imm_ISET", "Imm_ISET_U"))
    val IsetRegRegReg: Definition =
      Definition("IsetRegRegReg", "ISET", Vec("NoBop ISET", "NoBop ISET U", "NoBop_ISET", "NoBop_ISET_U"))
    val IsetRegRegRegPred: Definition = Definition("IsetRegRegRegPred", "ISET", Vec("ISET", "ISET U", "ISET_U"))
    val IsetpPredPredRegAddressImmPred: Definition =
      Definition("IsetpPredPredRegAddressImmPred", "ISETP", Vec("isetp__RCxR_RCxR", "isetp__RCxR_RCxR_noEX"))
    val IsetpPredPredRegAddressImmPredPred: Definition =
      Definition("IsetpPredPredRegAddressImmPredPred", "ISETP", Vec("isetp__RCxR_RCxR", "isetp__RCxR_RCxR_EX"))
    val IsetpPredPredRegConstPred: Definition = Definition(
      "IsetpPredPredRegConstPred",
      "ISETP",
      Vec(
        "Const ISETP",
        "Const ISETP U",
        "Const_ISETP",
        "Const_ISETP_U",
        "isetp__RCR_RC",
        "isetp__RCR_RCR",
        "isetp__RCR_RCR_noEX"
      )
    )
    val IsetpPredPredRegConstPredPred: Definition =
      Definition("IsetpPredPredRegConstPredPred", "ISETP", Vec("isetp__RCR_RC", "isetp__RCR_RCR", "isetp__RCR_RCR_EX"))
    val IsetpPredPredRegImmPred: Definition = Definition(
      "IsetpPredPredRegImmPred",
      "ISETP",
      Vec(
        "Imm ISETP",
        "Imm ISETP U",
        "Imm_ISETP",
        "Imm_ISETP_U",
        "isetp_64__RsIR_RIR_noEX",
        "isetp__RsIR_RI",
        "isetp__RsIR_RIR",
        "isetp__RsIR_RIR_noEX"
      )
    )
    val IsetpPredPredRegImmPredPred: Definition = Definition(
      "IsetpPredPredRegImmPredPred",
      "ISETP",
      Vec("isetp_64__RsIR_RIR_EX", "isetp__RsIR_RI", "isetp__RsIR_RIR", "isetp__RsIR_RIR_EX")
    )
    val IsetpPredPredRegRegPred: Definition = Definition(
      "IsetpPredPredRegRegPred",
      "ISETP",
      Vec(
        "ISETP",
        "ISETP U",
        "ISETP_U",
        "isetp_64__RRR_RRR_noEX",
        "isetp_64__RUR_RUR_noEX",
        "isetp__RRR_RR",
        "isetp__RRR_RRR",
        "isetp__RRR_RRR_noEX",
        "isetp__RUR_RUR",
        "isetp__RUR_RUR_noEX"
      )
    )
    val IsetpPredPredRegRegPredPred: Definition = Definition(
      "IsetpPredPredRegRegPredPred",
      "ISETP",
      Vec(
        "isetp_64__RRR_RRR_EX",
        "isetp_64__RUR_RUR_EX",
        "isetp__RRR_RR",
        "isetp__RRR_RRR",
        "isetp__RRR_RRR_EX",
        "isetp__RUR_RUR",
        "isetp__RUR_RUR_EX"
      )
    )
    val IsetpPredRegAddressImm: Definition =
      Definition("IsetpPredRegAddressImm", "ISETP", Vec("isetp_simple__RCxR_RCxR", "isetp_simple__RCxR_RCxR_noEX"))
    val IsetpPredRegAddressImmPred: Definition =
      Definition("IsetpPredRegAddressImmPred", "ISETP", Vec("isetp_simple__RCxR_RCxR", "isetp_simple__RCxR_RCxR_EX"))
    val IsetpPredRegConst: Definition = Definition(
      "IsetpPredRegConst",
      "ISETP",
      Vec(
        "NoBop Const ISETP",
        "NoBop Const ISETP U",
        "NoBop_Const_ISETP",
        "NoBop_Const_ISETP_U",
        "isetp_simple__RCR_RC",
        "isetp_simple__RCR_RCR",
        "isetp_simple__RCR_RCR_noEX"
      )
    )
    val IsetpPredRegConstPred: Definition = Definition(
      "IsetpPredRegConstPred",
      "ISETP",
      Vec("isetp_simple__RCR_RC", "isetp_simple__RCR_RCR", "isetp_simple__RCR_RCR_EX")
    )
    val IsetpPredRegImm: Definition = Definition(
      "IsetpPredRegImm",
      "ISETP",
      Vec(
        "NoBop Imm ISETP",
        "NoBop Imm ISETP U",
        "NoBop_Imm_ISETP",
        "NoBop_Imm_ISETP_U",
        "isetp_64_simple__RsIR_RIR_noEX",
        "isetp_simple__RsIR_RI",
        "isetp_simple__RsIR_RIR",
        "isetp_simple__RsIR_RIR_noEX"
      )
    )
    val IsetpPredRegImmPred: Definition = Definition(
      "IsetpPredRegImmPred",
      "ISETP",
      Vec(
        "isetp_64_simple__RsIR_RIR_EX",
        "isetp_simple__RsIR_RI",
        "isetp_simple__RsIR_RIR",
        "isetp_simple__RsIR_RIR_EX"
      )
    )
    val IsetpPredRegReg: Definition = Definition(
      "IsetpPredRegReg",
      "ISETP",
      Vec(
        "NoBop ISETP",
        "NoBop ISETP U",
        "NoBop_ISETP",
        "NoBop_ISETP_U",
        "isetp_64_simple__RRR_RRR_noEX",
        "isetp_64_simple__RUR_RUR_noEX",
        "isetp_simple__RRR_RR",
        "isetp_simple__RRR_RRR",
        "isetp_simple__RRR_RRR_noEX",
        "isetp_simple__RUR_RUR",
        "isetp_simple__RUR_RUR_noEX"
      )
    )
    val IsetpPredRegRegPred: Definition = Definition(
      "IsetpPredRegRegPred",
      "ISETP",
      Vec(
        "isetp_64_simple__RRR_RRR_EX",
        "isetp_64_simple__RUR_RUR_EX",
        "isetp_simple__RRR_RR",
        "isetp_simple__RRR_RRR",
        "isetp_simple__RRR_RRR_EX",
        "isetp_simple__RUR_RUR",
        "isetp_simple__RUR_RUR_EX"
      )
    )
    val IsubRegConstReg: Definition = Definition("IsubRegConstReg", "ISUB", Vec("Const ISUB1"))
    val IsubRegImmReg: Definition = Definition("IsubRegImmReg", "ISUB", Vec("Imm ISUB1"))
    val IsubRegRegConst: Definition = Definition("IsubRegRegConst", "ISUB", Vec("Const ISUB"))
    val IsubRegRegImm: Definition = Definition("IsubRegRegImm", "ISUB", Vec("Imm ISUB"))
    val IsubRegRegReg: Definition = Definition("IsubRegRegReg", "ISUB", Vec("ISUB"))
    val JcalConst: Definition = Definition("JcalConst", "JCAL", Vec("JCAL c", "JCAL_c"))
    val JcalImm: Definition = Definition("JcalImm", "JCAL", Vec("JCAL"))
    val JmpConst: Definition =
      Definition("JmpConst", "JMP", Vec("JMP_c", "jmp_const_", "jmp_const__CONV_DIV", "jmp_const__U"))
    val JmpImm: Definition = Definition(
      "JmpImm",
      "JMP",
      Vec(
        "JMP",
        "jmp_imm_",
        "jmp_imm__CONV_DIV",
        "jmp_imm__U",
        "jmp_imm_rel_",
        "jmp_imm_rel__CONV_DIV",
        "jmp_imm_rel__U"
      )
    )
    val JmpPredConst: Definition = Definition(
      "JmpPredConst",
      "JMP",
      Vec("jmp_const_", "jmp_const__CONV_DIV", "jmp_const__U", "jmp_const_uniform_pred_")
    )
    val JmpPredImm: Definition = Definition(
      "JmpPredImm",
      "JMP",
      Vec(
        "jmp_imm_",
        "jmp_imm__CONV_DIV",
        "jmp_imm__U",
        "jmp_imm_rel_",
        "jmp_imm_rel__CONV_DIV",
        "jmp_imm_rel__U",
        "jmp_imm_uniform_pred_",
        "jmp_imm_uniform_pred_rel_"
      )
    )
    val JmpPredPredConst: Definition = Definition("JmpPredPredConst", "JMP", Vec("jmp_const_uniform_pred_"))
    val JmpPredPredImm: Definition =
      Definition("JmpPredPredImm", "JMP", Vec("jmp_imm_uniform_pred_", "jmp_imm_uniform_pred_rel_"))
    val JmpPredRegConst: Definition = Definition("JmpPredRegConst", "JMP", Vec("jmp_const_uniform_"))
    val JmpPredRegImm: Definition = Definition("JmpPredRegImm", "JMP", Vec("jmp_imm_uniform_", "jmp_imm_uniform_rel_"))
    val JmpRegConst: Definition = Definition("JmpRegConst", "JMP", Vec("jmp_const_uniform_"))
    val JmpRegImm: Definition = Definition("JmpRegImm", "JMP", Vec("jmp_imm_uniform_", "jmp_imm_uniform_rel_"))
    val JmpSymbolConst: Definition = Definition("JmpSymbolConst", "JMP", Vec("JMP_c"))
    val JmpSymbolImm: Definition = Definition("JmpSymbolImm", "JMP", Vec("JMP"))
    val JmpSymbolImmImm: Definition = Definition("JmpSymbolImmImm", "JMP", Vec("JMP c"))
    val JmpSymbolSymbolImmImm: Definition = Definition("JmpSymbolSymbolImmImm", "JMP", Vec("JMP c"))
    val JmxConst: Definition = Definition("JmxConst", "JMX", Vec("JMX_c"))
    val JmxPredReg: Definition = Definition("JmxPredReg", "JMX", Vec("jmx_", "jmx_rel_"))
    val JmxPredRegImm: Definition = Definition("JmxPredRegImm", "JMX", Vec("jmx_", "jmx_rel_"))
    val JmxReg: Definition = Definition("JmxReg", "JMX", Vec("JMX", "jmx_", "jmx_rel_"))
    val JmxRegImm: Definition = Definition("JmxRegImm", "JMX", Vec("JMX", "jmx_", "jmx_rel_"))
    val JmxSymbolConst: Definition = Definition("JmxSymbolConst", "JMX", Vec("JMX_c"))
    val JmxSymbolImmAddress: Definition = Definition("JmxSymbolImmAddress", "JMX", Vec("JMX c"))
    val JmxSymbolReg: Definition = Definition("JmxSymbolReg", "JMX", Vec("JMX"))
    val JmxSymbolRegImm: Definition = Definition("JmxSymbolRegImm", "JMX", Vec("JMX"))
    val JmxSymbolSymbolImmAddress: Definition = Definition("JmxSymbolSymbolImmAddress", "JMX", Vec("JMX c"))
    val JmxuPredReg: Definition = Definition("JmxuPredReg", "JMXU", Vec("jmxu_", "jmxu_rel_"))
    val JmxuPredRegImm: Definition = Definition("JmxuPredRegImm", "JMXU", Vec("jmxu_", "jmxu_rel_"))
    val JmxuReg: Definition = Definition("JmxuReg", "JMXU", Vec("jmxu_", "jmxu_rel_"))
    val JmxuRegImm: Definition = Definition("JmxuRegImm", "JMXU", Vec("jmxu_", "jmxu_rel_"))
    val KilNoOperands: Definition = Definition("KilNoOperands", "KIL", Vec("BRK", "KIL"))
    val KillNoOperands: Definition = Definition("KillNoOperands", "KILL", Vec("kill_"))
    val KillPred: Definition = Definition("KillPred", "KILL", Vec("kill_"))
    val LdPredRegAddress: Definition = Definition(
      "LdPredRegAddress",
      "LD",
      Vec(
        "ld_weakonly_zd__sImmOffset",
        "ld_weakonly_zd__uImmOffset",
        "ld_weakonly_zd_uniform__Ra32",
        "ld_weakonly_zd_uniform__Ra64",
        "ld_weakonly_zd_uniform__RaRZ",
        "ld_zd__sImmOffset",
        "ld_zd__uImmOffset",
        "ld_zd_uniform__Ra32",
        "ld_zd_uniform__Ra64",
        "ld_zd_uniform__RaRZ"
      )
    )
    val LdPredRegAddressPred: Definition = Definition(
      "LdPredRegAddressPred",
      "LD",
      Vec(
        "ld_weakonly_zd__sImmOffset",
        "ld_weakonly_zd__uImmOffset",
        "ld_weakonly_zd_uniform__Ra32",
        "ld_weakonly_zd_uniform__Ra64",
        "ld_weakonly_zd_uniform__RaRZ",
        "ld_zd__sImmOffset",
        "ld_zd__uImmOffset",
        "ld_zd_uniform__Ra32",
        "ld_zd_uniform__Ra64",
        "ld_zd_uniform__RaRZ"
      )
    )
    val LdRegAddress: Definition = Definition(
      "LdRegAddress",
      "LD",
      Vec(
        "I LD",
        "I_LD",
        "LD",
        "ld__sImmOffset",
        "ld__uImmOffset",
        "ld_memdesc__Ra64",
        "ld_uniform__Ra32",
        "ld_uniform__Ra64",
        "ld_uniform__RaRZ",
        "ld_weakonly__sImmOffset",
        "ld_weakonly__uImmOffset",
        "ld_weakonly_uniform__Ra32",
        "ld_weakonly_uniform__Ra64",
        "ld_weakonly_uniform__RaRZ"
      )
    )
    val LdRegAddressPred: Definition = Definition(
      "LdRegAddressPred",
      "LD",
      Vec(
        "I_LD",
        "LD",
        "ld__sImmOffset",
        "ld__uImmOffset",
        "ld_memdesc__Ra64",
        "ld_uniform__Ra32",
        "ld_uniform__Ra64",
        "ld_uniform__RaRZ",
        "ld_weakonly__sImmOffset",
        "ld_weakonly__uImmOffset",
        "ld_weakonly_uniform__Ra32",
        "ld_weakonly_uniform__Ra64",
        "ld_weakonly_uniform__RaRZ"
      )
    )
    val LdcRegAddressAddress: Definition =
      Definition("LdcRegAddressAddress", "LDC", Vec("ldc_ur__URRzI", "ldc_ur__URnonRzI"))
    val LdcRegConst: Definition = Definition(
      "LdcRegConst",
      "LDC",
      Vec("I LDC", "I LDC 64", "I_LDC", "I_LDC_64", "LDC", "LDC 64", "LDC_64", "ldc__RaNonRZ", "ldc__RaRZ")
    )
    val LdcuRegAddress: Definition = Definition("LdcuRegAddress", "LDCU", Vec("ldcu_ur_offs_optional_upx_"))
    val LdcuRegAddressAddress: Definition = Definition("LdcuRegAddressAddress", "LDCU", Vec("ldcu_const_RCxR_"))
    val LdcuRegAddressPred: Definition = Definition("LdcuRegAddressPred", "LDCU", Vec("ldcu_ur_offs_"))
    val LdcuRegConst: Definition = Definition("LdcuRegConst", "LDCU", Vec("ldcu_const_RCR_"))
    val LdcuRegRegAddress: Definition = Definition("LdcuRegRegAddress", "LDCU", Vec("ldcu_256_ur_offs_optional_upx_"))
    val LdcuRegRegAddressAddress: Definition =
      Definition("LdcuRegRegAddressAddress", "LDCU", Vec("ldcu_256_const_RCxR_"))
    val LdcuRegRegAddressAddressImm: Definition =
      Definition("LdcuRegRegAddressAddressImm", "LDCU", Vec("ldcu_256_const_RCxR_"))
    val LdcuRegRegAddressImm: Definition =
      Definition("LdcuRegRegAddressImm", "LDCU", Vec("ldcu_256_ur_offs_optional_upx_"))
    val LdcuRegRegAddressImmPred: Definition = Definition("LdcuRegRegAddressImmPred", "LDCU", Vec("ldcu_256_ur_offs_"))
    val LdcuRegRegAddressPred: Definition = Definition("LdcuRegRegAddressPred", "LDCU", Vec("ldcu_256_ur_offs_"))
    val LdcuRegRegConst: Definition = Definition("LdcuRegRegConst", "LDCU", Vec("ldcu_256_const_RCR_"))
    val LdcuRegRegConstImm: Definition = Definition("LdcuRegRegConstImm", "LDCU", Vec("ldcu_256_const_RCR_"))
    val LdgPredRegAddress: Definition = Definition(
      "LdgPredRegAddress",
      "LDG",
      Vec(
        "I_LDG_SPARSE",
        "LDG_SPARSE",
        "ldg__sImmOffset",
        "ldg__uImmOffset",
        "ldg_memdesc__Ra64",
        "ldg_uniform__Ra32",
        "ldg_uniform__Ra64",
        "ldg_uniform__RaRZ",
        "ldg_weakonly__sImmOffset",
        "ldg_weakonly__uImmOffset",
        "ldg_weakonly_uniform__Ra32",
        "ldg_weakonly_uniform__Ra64",
        "ldg_weakonly_uniform__RaRZ",
        "ldg_weakonly_zd__sImmOffset",
        "ldg_weakonly_zd__uImmOffset",
        "ldg_weakonly_zd_uniform__Ra32",
        "ldg_weakonly_zd_uniform__Ra64",
        "ldg_weakonly_zd_uniform__RaRZ",
        "ldg_zd__sImmOffset",
        "ldg_zd__uImmOffset",
        "ldg_zd_uniform__Ra32",
        "ldg_zd_uniform__Ra64",
        "ldg_zd_uniform__RaRZ"
      )
    )
    val LdgPredRegAddressPred: Definition = Definition(
      "LdgPredRegAddressPred",
      "LDG",
      Vec(
        "ldg__sImmOffset",
        "ldg__uImmOffset",
        "ldg_memdesc__Ra64",
        "ldg_uniform__Ra32",
        "ldg_uniform__Ra64",
        "ldg_uniform__RaRZ",
        "ldg_weakonly__sImmOffset",
        "ldg_weakonly__uImmOffset",
        "ldg_weakonly_uniform__Ra32",
        "ldg_weakonly_uniform__Ra64",
        "ldg_weakonly_uniform__RaRZ",
        "ldg_weakonly_zd__sImmOffset",
        "ldg_weakonly_zd__uImmOffset",
        "ldg_weakonly_zd_uniform__Ra32",
        "ldg_weakonly_zd_uniform__Ra64",
        "ldg_weakonly_zd_uniform__RaRZ",
        "ldg_zd__sImmOffset",
        "ldg_zd__uImmOffset",
        "ldg_zd_uniform__Ra32",
        "ldg_zd_uniform__Ra64",
        "ldg_zd_uniform__RaRZ"
      )
    )
    val LdgRegAddress: Definition = Definition(
      "LdgRegAddress",
      "LDG",
      Vec(
        "I_LDG",
        "LDG",
        "LDG CG CS",
        "LDG CT",
        "ldg__sImmOffset",
        "ldg__uImmOffset",
        "ldg_memdesc__Ra64",
        "ldg_uniform__Ra32",
        "ldg_uniform__Ra64",
        "ldg_uniform__RaRZ",
        "ldg_weakonly__sImmOffset",
        "ldg_weakonly__uImmOffset",
        "ldg_weakonly_uniform__Ra32",
        "ldg_weakonly_uniform__Ra64",
        "ldg_weakonly_uniform__RaRZ"
      )
    )
    val LdgRegAddressPred: Definition = Definition(
      "LdgRegAddressPred",
      "LDG",
      Vec(
        "ldg__sImmOffset",
        "ldg__uImmOffset",
        "ldg_memdesc__Ra64",
        "ldg_uniform__Ra32",
        "ldg_uniform__Ra64",
        "ldg_uniform__RaRZ",
        "ldg_weakonly__sImmOffset",
        "ldg_weakonly__uImmOffset",
        "ldg_weakonly_uniform__Ra32",
        "ldg_weakonly_uniform__Ra64",
        "ldg_weakonly_uniform__RaRZ"
      )
    )
    val LdgRegRegAddress: Definition = Definition(
      "LdgRegRegAddress",
      "LDG",
      Vec(
        "ldg_256_memdesc__Ra64",
        "ldg_256_rml2_memdesc__Ra64",
        "ldg_256_rml2_uniform__Ra32",
        "ldg_256_rml2_uniform__Ra64",
        "ldg_256_rml2_uniform__RaRZ",
        "ldg_256_uniform__Ra32",
        "ldg_256_uniform__Ra64",
        "ldg_256_uniform__RaRZ"
      )
    )
    val LdgRegRegAddressImm: Definition = Definition(
      "LdgRegRegAddressImm",
      "LDG",
      Vec(
        "ldg_256_memdesc__Ra64",
        "ldg_256_rml2_memdesc__Ra64",
        "ldg_256_rml2_uniform__Ra32",
        "ldg_256_rml2_uniform__Ra64",
        "ldg_256_rml2_uniform__RaRZ",
        "ldg_256_uniform__Ra32",
        "ldg_256_uniform__Ra64",
        "ldg_256_uniform__RaRZ"
      )
    )
    val LdgRegRegAddressImmPred: Definition = Definition(
      "LdgRegRegAddressImmPred",
      "LDG",
      Vec(
        "ldg_256_memdesc__Ra64",
        "ldg_256_rml2_memdesc__Ra64",
        "ldg_256_rml2_uniform__Ra32",
        "ldg_256_rml2_uniform__Ra64",
        "ldg_256_rml2_uniform__RaRZ",
        "ldg_256_uniform__Ra32",
        "ldg_256_uniform__Ra64",
        "ldg_256_uniform__RaRZ"
      )
    )
    val LdgRegRegAddressPred: Definition = Definition(
      "LdgRegRegAddressPred",
      "LDG",
      Vec(
        "ldg_256_memdesc__Ra64",
        "ldg_256_rml2_memdesc__Ra64",
        "ldg_256_rml2_uniform__Ra32",
        "ldg_256_rml2_uniform__Ra64",
        "ldg_256_rml2_uniform__RaRZ",
        "ldg_256_uniform__Ra32",
        "ldg_256_uniform__Ra64",
        "ldg_256_uniform__RaRZ"
      )
    )
    val LdgdepbarNoOperands: Definition = Definition("LdgdepbarNoOperands", "LDGDEPBAR", Vec("ldgdepbar_"))
    val LdgmcRegAddress: Definition = Definition(
      "LdgmcRegAddress",
      "LDGMC",
      Vec(
        "ldgmc_fp__Ra32",
        "ldgmc_fp__Ra64",
        "ldgmc_fp__RaRZ",
        "ldgmc_fp__memdesc",
        "ldgmc_int__Ra32",
        "ldgmc_int__Ra64",
        "ldgmc_int__RaRZ",
        "ldgmc_int__memdesc"
      )
    )
    val LdgstsAddressAddress: Definition = Definition(
      "LdgstsAddressAddress",
      "LDGSTS",
      Vec(
        "ldgsts__RR32U",
        "ldgsts__RR64U",
        "ldgsts__RUR",
        "ldgsts__desc_RRU",
        "ldgsts_memdesc_",
        "ldgsts_no_ra__RRU",
        "ldgsts_no_ra__RUR"
      )
    )
    val LdgstsAddressAddressPred: Definition = Definition(
      "LdgstsAddressAddressPred",
      "LDGSTS",
      Vec(
        "ldgsts__RR32U",
        "ldgsts__RR64U",
        "ldgsts__RUR",
        "ldgsts__desc_RRU",
        "ldgsts_memdesc_",
        "ldgsts_no_ra__RRU",
        "ldgsts_no_ra__RUR"
      )
    )
    val LdlRegAddress: Definition = Definition(
      "LdlRegAddress",
      "LDL",
      Vec("I LDL", "I_LDL", "LDL", "ldl__sImmOffset", "ldl__uImmOffset", "ldl_memdesc_", "ldl_uniform_")
    )
    val LdsPredRegAddress: Definition =
      Definition("LdsPredRegAddress", "LDS", Vec("lds_zd__sImmOffset", "lds_zd__uImmOffset", "lds_zd_uniform_"))
    val LdsRegAddress: Definition = Definition(
      "LdsRegAddress",
      "LDS",
      Vec("I LDS", "I_LDS", "LDS", "lds__sImmOffset", "lds__uImmOffset", "lds_uniform_")
    )
    val LdslkPredRegAddress: Definition = Definition("LdslkPredRegAddress", "LDSLK", Vec("I LDSLK", "LDSLK"))
    val LdsmRegAddress: Definition = Definition(
      "LdsmRegAddress",
      "LDSM",
      Vec(
        "ldsm__UR_sI_R",
        "ldsm__URsIR",
        "ldsm__URuIR",
        "ldsm__sImmOffset",
        "ldsm__uImmOffset",
        "ldsm_pseudo_ops__UR_sI_R",
        "ldsm_pseudo_ops__URsIR",
        "ldsm_pseudo_ops__URuIR",
        "ldsm_pseudo_ops__sImmOffset",
        "ldsm_pseudo_ops__uImmOffset"
      )
    )
    val LdtRegAddress: Definition = Definition("LdtRegAddress", "LDT", Vec("ldt_"))
    val LdtmRegAddress: Definition = Definition("LdtmRegAddress", "LDTM", Vec("ldtm_"))
    val LdtmRegRegAddress: Definition = Definition("LdtmRegRegAddress", "LDTM", Vec("ldtm_stat_"))
    val LdtramRegAddress: Definition = Definition("LdtramRegAddress", "LDTRAM", Vec("ldtram_"))
    val LeaPredRegRegConst: Definition = Definition("LeaPredRegRegConst", "LEA", Vec("LEA_LO_CONST"))
    val LeaPredRegRegConstImm: Definition = Definition("LeaPredRegRegConstImm", "LEA", Vec("LEA_LO_CONST"))
    val LeaPredRegRegConstReg: Definition = Definition("LeaPredRegRegConstReg", "LEA", Vec("LEA_HI_CONST"))
    val LeaPredRegRegConstRegImm: Definition = Definition("LeaPredRegRegConstRegImm", "LEA", Vec("LEA_HI_CONST"))
    val LeaPredRegRegImm: Definition = Definition("LeaPredRegRegImm", "LEA", Vec("LEA_LO_IMM"))
    val LeaPredRegRegImmImm: Definition = Definition("LeaPredRegRegImmImm", "LEA", Vec("LEA_LO_IMM"))
    val LeaPredRegRegReg: Definition = Definition("LeaPredRegRegReg", "LEA", Vec("LEA_LO_REG"))
    val LeaPredRegRegRegImm: Definition = Definition("LeaPredRegRegRegImm", "LEA", Vec("LEA_LO_REG"))
    val LeaPredRegRegRegReg: Definition = Definition("LeaPredRegRegRegReg", "LEA", Vec("LEA_HI_REG"))
    val LeaPredRegRegRegRegImm: Definition = Definition("LeaPredRegRegRegRegImm", "LEA", Vec("LEA_HI_REG"))
    val LeaRegPredRegAddressImmImm: Definition =
      Definition("LeaRegPredRegAddressImmImm", "LEA", Vec("lea_hi_noimm_sx32__RCxR_RCxR", "lea_lo_noimm__RCxR_RCxR"))
    val LeaRegPredRegAddressImmImmPred: Definition = Definition(
      "LeaRegPredRegAddressImmImmPred",
      "LEA",
      Vec("lea_hi_noimm_sx32_x__RCxR_RCxR", "lea_lo_noimm_x__RCxR_RCxR")
    )
    val LeaRegPredRegAddressImmRegImm: Definition =
      Definition("LeaRegPredRegAddressImmRegImm", "LEA", Vec("lea_hi_noimm__RCxR_RCxR"))
    val LeaRegPredRegAddressImmRegImmPred: Definition =
      Definition("LeaRegPredRegAddressImmRegImmPred", "LEA", Vec("lea_hi_noimm_x__RCxR_RCxR"))
    val LeaRegPredRegConstImm: Definition =
      Definition("LeaRegPredRegConstImm", "LEA", Vec("lea_hi_noimm_sx32__RCR_RCR", "lea_lo_noimm__RCR_RCR"))
    val LeaRegPredRegConstImmPred: Definition =
      Definition("LeaRegPredRegConstImmPred", "LEA", Vec("lea_hi_noimm_sx32_x__RCR_RCR", "lea_lo_noimm_x__RCR_RCR"))
    val LeaRegPredRegConstRegImm: Definition =
      Definition("LeaRegPredRegConstRegImm", "LEA", Vec("lea_hi_noimm__RCR_RCR"))
    val LeaRegPredRegConstRegImmPred: Definition =
      Definition("LeaRegPredRegConstRegImmPred", "LEA", Vec("lea_hi_noimm_x__RCR_RCR"))
    val LeaRegPredRegImmImm: Definition =
      Definition("LeaRegPredRegImmImm", "LEA", Vec("lea_hi_imm_sx32__RuIR_RIR", "lea_lo_imm__RuIR_RIR"))
    val LeaRegPredRegImmImmPred: Definition =
      Definition("LeaRegPredRegImmImmPred", "LEA", Vec("lea_hi_imm_sx32_x__RuIR_RIR", "lea_lo_imm_x__RuIR_RIR"))
    val LeaRegPredRegImmRegImm: Definition = Definition("LeaRegPredRegImmRegImm", "LEA", Vec("lea_hi_imm__RuIR_RIR"))
    val LeaRegPredRegImmRegImmPred: Definition =
      Definition("LeaRegPredRegImmRegImmPred", "LEA", Vec("lea_hi_imm_x__RuIR_RIR"))
    val LeaRegPredRegRegImm: Definition = Definition(
      "LeaRegPredRegRegImm",
      "LEA",
      Vec("lea_hi_noimm_sx32__RRR_RRR", "lea_hi_noimm_sx32__RUR_RUR", "lea_lo_noimm__RRR_RRR", "lea_lo_noimm__RUR_RUR")
    )
    val LeaRegPredRegRegImmImm: Definition = Definition("LeaRegPredRegRegImmImm", "LEA", Vec("lea_hi_imm__RRuI_RRI"))
    val LeaRegPredRegRegImmImmPred: Definition =
      Definition("LeaRegPredRegRegImmImmPred", "LEA", Vec("lea_hi_imm_x__RRuI_RRI"))
    val LeaRegPredRegRegImmPred: Definition = Definition(
      "LeaRegPredRegRegImmPred",
      "LEA",
      Vec(
        "lea_hi_noimm_sx32_x__RRR_RRR",
        "lea_hi_noimm_sx32_x__RUR_RUR",
        "lea_lo_noimm_x__RRR_RRR",
        "lea_lo_noimm_x__RUR_RUR"
      )
    )
    val LeaRegPredRegRegRegImm: Definition =
      Definition("LeaRegPredRegRegRegImm", "LEA", Vec("lea_hi_noimm__RRR_RRR", "lea_hi_noimm__RUR_RUR"))
    val LeaRegPredRegRegRegImmPred: Definition =
      Definition("LeaRegPredRegRegRegImmPred", "LEA", Vec("lea_hi_noimm_x__RRR_RRR", "lea_hi_noimm_x__RUR_RUR"))
    val LeaRegRegAddressImmImm: Definition =
      Definition("LeaRegRegAddressImmImm", "LEA", Vec("lea_hi_noimm_sx32__RCxR_RCxR", "lea_lo_noimm__RCxR_RCxR"))
    val LeaRegRegAddressImmImmPred: Definition = Definition(
      "LeaRegRegAddressImmImmPred",
      "LEA",
      Vec("lea_hi_noimm_sx32_x__RCxR_RCxR", "lea_lo_noimm_x__RCxR_RCxR")
    )
    val LeaRegRegAddressImmRegImm: Definition =
      Definition("LeaRegRegAddressImmRegImm", "LEA", Vec("lea_hi_noimm__RCxR_RCxR"))
    val LeaRegRegAddressImmRegImmPred: Definition =
      Definition("LeaRegRegAddressImmRegImmPred", "LEA", Vec("lea_hi_noimm_x__RCxR_RCxR"))
    val LeaRegRegConst: Definition = Definition("LeaRegRegConst", "LEA", Vec("LEA_LO_CONST"))
    val LeaRegRegConstImm: Definition =
      Definition("LeaRegRegConstImm", "LEA", Vec("LEA_LO_CONST", "lea_hi_noimm_sx32__RCR_RCR", "lea_lo_noimm__RCR_RCR"))
    val LeaRegRegConstImmPred: Definition =
      Definition("LeaRegRegConstImmPred", "LEA", Vec("lea_hi_noimm_sx32_x__RCR_RCR", "lea_lo_noimm_x__RCR_RCR"))
    val LeaRegRegConstReg: Definition = Definition("LeaRegRegConstReg", "LEA", Vec("LEA_HI_CONST"))
    val LeaRegRegConstRegImm: Definition =
      Definition("LeaRegRegConstRegImm", "LEA", Vec("LEA_HI_CONST", "lea_hi_noimm__RCR_RCR"))
    val LeaRegRegConstRegImmPred: Definition =
      Definition("LeaRegRegConstRegImmPred", "LEA", Vec("lea_hi_noimm_x__RCR_RCR"))
    val LeaRegRegImm: Definition = Definition("LeaRegRegImm", "LEA", Vec("LEA_LO_IMM"))
    val LeaRegRegImmImm: Definition =
      Definition("LeaRegRegImmImm", "LEA", Vec("LEA_LO_IMM", "lea_hi_imm_sx32__RuIR_RIR", "lea_lo_imm__RuIR_RIR"))
    val LeaRegRegImmImmPred: Definition =
      Definition("LeaRegRegImmImmPred", "LEA", Vec("lea_hi_imm_sx32_x__RuIR_RIR", "lea_lo_imm_x__RuIR_RIR"))
    val LeaRegRegImmRegImm: Definition = Definition("LeaRegRegImmRegImm", "LEA", Vec("lea_hi_imm__RuIR_RIR"))
    val LeaRegRegImmRegImmPred: Definition = Definition("LeaRegRegImmRegImmPred", "LEA", Vec("lea_hi_imm_x__RuIR_RIR"))
    val LeaRegRegReg: Definition = Definition("LeaRegRegReg", "LEA", Vec("LEA_LO_REG"))
    val definitions: Vec[Definition] = Vec(
      IgmmaRegAddressRegPredReg,
      IgmmaRegAddressRegPredRegImm,
      IgmmaRegAddressRegReg,
      IgmmaRegAddressRegRegImm,
      IgmmaRegAddressRegRegPred,
      IgmmaRegAddressRegRegPredReg,
      IgmmaRegAddressRegRegPredRegImm,
      IgmmaRegAddressRegRegReg,
      IgmmaRegAddressRegRegRegImm,
      IgmmaRegRegAddressReg,
      IgmmaRegRegAddressRegPred,
      IgmmaRegRegAddressRegPredReg,
      IgmmaRegRegAddressRegPredRegImm,
      IgmmaRegRegAddressRegReg,
      IgmmaRegRegAddressRegRegImm,
      ImadRegPredRegAddressImmReg,
      ImadRegPredRegAddressImmRegPred,
      ImadRegPredRegConstReg,
      ImadRegPredRegConstRegPred,
      ImadRegPredRegImmReg,
      ImadRegPredRegImmRegPred,
      ImadRegPredRegRegAddressImm,
      ImadRegPredRegRegAddressImmPred,
      ImadRegPredRegRegConst,
      ImadRegPredRegRegConstPred,
      ImadRegPredRegRegReg,
      ImadRegPredRegRegRegPred,
      ImadRegRegAddressImmReg,
      ImadRegRegAddressImmRegPred,
      ImadRegRegConstReg,
      ImadRegRegConstRegPred,
      ImadRegRegImmReg,
      ImadRegRegImmRegPred,
      ImadRegRegRegAddressImm,
      ImadRegRegRegAddressImmPred,
      ImadRegRegRegConst,
      ImadRegRegRegConstPred,
      ImadRegRegRegImm,
      ImadRegRegRegImmPred,
      ImadRegRegRegReg,
      ImadRegRegRegRegPred,
      Imad32iRegRegImmReg,
      ImadspRegRegConstReg,
      ImadspRegRegImmReg,
      ImadspRegRegRegConst,
      ImadspRegRegRegReg,
      ImmaRegRegRegReg,
      ImmaRegRegRegRegPred,
      ImmaRegRegRegRegPredRegImm,
      ImmaRegRegRegRegRegImm,
      ImnmxPredPredRegRegImmPred,
      ImnmxPredPredRegRegImmPredPred,
      ImnmxPredPredRegRegRegPred,
      ImnmxPredPredRegRegRegPredPred,
      ImnmxPredRegRegImmPred,
      ImnmxPredRegRegImmPredPred,
      ImnmxPredRegRegRegPred,
      ImnmxPredRegRegRegPredPred,
      ImnmxRegRegAddressImmPred,
      ImnmxRegRegConstPred,
      ImnmxRegRegImmPred,
      ImnmxRegRegImmPredPred,
      ImnmxRegRegRegPred,
      ImnmxRegRegRegPredPred,
      ImulRegPredRegAddressImm,
      ImulRegPredRegConst,
      ImulRegPredRegImm,
      ImulRegPredRegReg,
      ImulRegRegAddressImm,
      ImulRegRegConst,
      ImulRegRegImm,
      ImulRegRegImmReg,
      ImulRegRegReg,
      ImulRegRegRegImm,
      ImulRegRegRegReg,
      Imul32iRegPredRegImm,
      Imul32iRegRegImm,
      IpaRegAddress,
      IpaRegAddressImm,
      IpaRegAddressPred,
      IpaRegAddressReg,
      IpaRegAddressRegReg,
      IpaRegAddressRegRegPred,
      IpaRegImm,
      IpaRegImmConst,
      IpaRegImmImm,
      IpaRegImmPred,
      IpaRegImmReg,
      IpaRegImmRegReg,
      IpaRegImmRegRegPred,
      IpaRegPredAddress,
      IpaRegPredAddressImm,
      IpaRegPredAddressReg,
      IpaRegPredImm,
      IpaRegPredImmConst,
      IpaRegPredImmImm,
      IpaRegPredImmReg,
      IsadRegRegConstReg,
      IsadRegRegImmReg,
      IsadRegRegRegConst,
      IsadRegRegRegReg,
      IsberdRegAddress,
      IsbewrAddressReg,
      IscaddRegPredRegAddressImmImm,
      IscaddRegPredRegConstImm,
      IscaddRegPredRegImmImm,
      IscaddRegPredRegRegImm,
      IscaddRegRegAddressImmImm,
      IscaddRegRegConstImm,
      IscaddRegRegImmImm,
      IscaddRegRegRegImm,
      Iscadd32iRegPredRegImmImm,
      Iscadd32iRegRegImmImm,
      IsetRegRegConst,
      IsetRegRegConstPred,
      IsetRegRegImm,
      IsetRegRegImmPred,
      IsetRegRegReg,
      IsetRegRegRegPred,
      IsetpPredPredRegAddressImmPred,
      IsetpPredPredRegAddressImmPredPred,
      IsetpPredPredRegConstPred,
      IsetpPredPredRegConstPredPred,
      IsetpPredPredRegImmPred,
      IsetpPredPredRegImmPredPred,
      IsetpPredPredRegRegPred,
      IsetpPredPredRegRegPredPred,
      IsetpPredRegAddressImm,
      IsetpPredRegAddressImmPred,
      IsetpPredRegConst,
      IsetpPredRegConstPred,
      IsetpPredRegImm,
      IsetpPredRegImmPred,
      IsetpPredRegReg,
      IsetpPredRegRegPred,
      IsubRegConstReg,
      IsubRegImmReg,
      IsubRegRegConst,
      IsubRegRegImm,
      IsubRegRegReg,
      JcalConst,
      JcalImm,
      JmpConst,
      JmpImm,
      JmpPredConst,
      JmpPredImm,
      JmpPredPredConst,
      JmpPredPredImm,
      JmpPredRegConst,
      JmpPredRegImm,
      JmpRegConst,
      JmpRegImm,
      JmpSymbolConst,
      JmpSymbolImm,
      JmpSymbolImmImm,
      JmpSymbolSymbolImmImm,
      JmxConst,
      JmxPredReg,
      JmxPredRegImm,
      JmxReg,
      JmxRegImm,
      JmxSymbolConst,
      JmxSymbolImmAddress,
      JmxSymbolReg,
      JmxSymbolRegImm,
      JmxSymbolSymbolImmAddress,
      JmxuPredReg,
      JmxuPredRegImm,
      JmxuReg,
      JmxuRegImm,
      KilNoOperands,
      KillNoOperands,
      KillPred,
      LdPredRegAddress,
      LdPredRegAddressPred,
      LdRegAddress,
      LdRegAddressPred,
      LdcRegAddressAddress,
      LdcRegConst,
      LdcuRegAddress,
      LdcuRegAddressAddress,
      LdcuRegAddressPred,
      LdcuRegConst,
      LdcuRegRegAddress,
      LdcuRegRegAddressAddress,
      LdcuRegRegAddressAddressImm,
      LdcuRegRegAddressImm,
      LdcuRegRegAddressImmPred,
      LdcuRegRegAddressPred,
      LdcuRegRegConst,
      LdcuRegRegConstImm,
      LdgPredRegAddress,
      LdgPredRegAddressPred,
      LdgRegAddress,
      LdgRegAddressPred,
      LdgRegRegAddress,
      LdgRegRegAddressImm,
      LdgRegRegAddressImmPred,
      LdgRegRegAddressPred,
      LdgdepbarNoOperands,
      LdgmcRegAddress,
      LdgstsAddressAddress,
      LdgstsAddressAddressPred,
      LdlRegAddress,
      LdsPredRegAddress,
      LdsRegAddress,
      LdslkPredRegAddress,
      LdsmRegAddress,
      LdtRegAddress,
      LdtmRegAddress,
      LdtmRegRegAddress,
      LdtramRegAddress,
      LeaPredRegRegConst,
      LeaPredRegRegConstImm,
      LeaPredRegRegConstReg,
      LeaPredRegRegConstRegImm,
      LeaPredRegRegImm,
      LeaPredRegRegImmImm,
      LeaPredRegRegReg,
      LeaPredRegRegRegImm,
      LeaPredRegRegRegReg,
      LeaPredRegRegRegRegImm,
      LeaRegPredRegAddressImmImm,
      LeaRegPredRegAddressImmImmPred,
      LeaRegPredRegAddressImmRegImm,
      LeaRegPredRegAddressImmRegImmPred,
      LeaRegPredRegConstImm,
      LeaRegPredRegConstImmPred,
      LeaRegPredRegConstRegImm,
      LeaRegPredRegConstRegImmPred,
      LeaRegPredRegImmImm,
      LeaRegPredRegImmImmPred,
      LeaRegPredRegImmRegImm,
      LeaRegPredRegImmRegImmPred,
      LeaRegPredRegRegImm,
      LeaRegPredRegRegImmImm,
      LeaRegPredRegRegImmImmPred,
      LeaRegPredRegRegImmPred,
      LeaRegPredRegRegRegImm,
      LeaRegPredRegRegRegImmPred,
      LeaRegRegAddressImmImm,
      LeaRegRegAddressImmImmPred,
      LeaRegRegAddressImmRegImm,
      LeaRegRegAddressImmRegImmPred,
      LeaRegRegConst,
      LeaRegRegConstImm,
      LeaRegRegConstImmPred,
      LeaRegRegConstReg,
      LeaRegRegConstRegImm,
      LeaRegRegConstRegImmPred,
      LeaRegRegImm,
      LeaRegRegImmImm,
      LeaRegRegImmImmPred,
      LeaRegRegImmRegImm,
      LeaRegRegImmRegImmPred,
      LeaRegRegReg
    )
  }
  object Definitions3 {
    val LeaRegRegRegImm: Definition = Definition(
      "LeaRegRegRegImm",
      "LEA",
      Vec(
        "LEA_LO_REG",
        "lea_hi_noimm_sx32__RRR_RRR",
        "lea_hi_noimm_sx32__RUR_RUR",
        "lea_lo_noimm__RRR_RRR",
        "lea_lo_noimm__RUR_RUR"
      )
    )
    val LeaRegRegRegImmImm: Definition = Definition("LeaRegRegRegImmImm", "LEA", Vec("lea_hi_imm__RRuI_RRI"))
    val LeaRegRegRegImmImmPred: Definition = Definition("LeaRegRegRegImmImmPred", "LEA", Vec("lea_hi_imm_x__RRuI_RRI"))
    val LeaRegRegRegImmPred: Definition = Definition(
      "LeaRegRegRegImmPred",
      "LEA",
      Vec(
        "lea_hi_noimm_sx32_x__RRR_RRR",
        "lea_hi_noimm_sx32_x__RUR_RUR",
        "lea_lo_noimm_x__RRR_RRR",
        "lea_lo_noimm_x__RUR_RUR"
      )
    )
    val LeaRegRegRegReg: Definition = Definition("LeaRegRegRegReg", "LEA", Vec("LEA_HI_REG"))
    val LeaRegRegRegRegImm: Definition =
      Definition("LeaRegRegRegRegImm", "LEA", Vec("LEA_HI_REG", "lea_hi_noimm__RRR_RRR", "lea_hi_noimm__RUR_RUR"))
    val LeaRegRegRegRegImmPred: Definition =
      Definition("LeaRegRegRegRegImmPred", "LEA", Vec("lea_hi_noimm_x__RRR_RRR", "lea_hi_noimm_x__RUR_RUR"))
    val LepcReg: Definition = Definition("LepcReg", "LEPC", Vec("LEPC", "lepc_", "lepc__RRR"))
    val LepcRegImm: Definition = Definition("LepcRegImm", "LEPC", Vec("lepc__R_I_R", "lepc_rel_"))
    val LongjmpNoOperands: Definition = Definition("LongjmpNoOperands", "LONGJMP", Vec("BRK", "LONGJMP"))
    val LopPredRegRegAddressImm: Definition =
      Definition("LopPredRegRegAddressImm", "LOP", Vec("lop_noimm_optionalPp__RCxR_RCxR"))
    val LopPredRegRegAddressImmPred: Definition =
      Definition("LopPredRegRegAddressImmPred", "LOP", Vec("lop_noimm__RCxR_RCxR"))
    val LopPredRegRegConst: Definition =
      Definition("LopPredRegRegConst", "LOP", Vec("Const_LOP", "lop_noimm_optionalPp__RCR_RCR"))
    val LopPredRegRegConstPred: Definition = Definition("LopPredRegRegConstPred", "LOP", Vec("lop_noimm__RCR_RCR"))
    val LopPredRegRegImm: Definition = Definition("LopPredRegRegImm", "LOP", Vec("Imm_LOP", "lop_imm_optionalPp_"))
    val LopPredRegRegImmPred: Definition = Definition("LopPredRegRegImmPred", "LOP", Vec("lop_imm_"))
    val LopPredRegRegReg: Definition = Definition(
      "LopPredRegRegReg",
      "LOP",
      Vec("LOP", "lop_noimm_optionalPp__RRR_RRR", "lop_noimm_optionalPp__RUR_RUR")
    )
    val LopPredRegRegRegPred: Definition =
      Definition("LopPredRegRegRegPred", "LOP", Vec("lop_noimm__RRR_RRR", "lop_noimm__RUR_RUR"))
    val LopRegRegAddressImm: Definition =
      Definition("LopRegRegAddressImm", "LOP", Vec("lop_noimm_optionalPp__RCxR_RCxR"))
    val LopRegRegAddressImmPred: Definition = Definition("LopRegRegAddressImmPred", "LOP", Vec("lop_noimm__RCxR_RCxR"))
    val LopRegRegConst: Definition =
      Definition("LopRegRegConst", "LOP", Vec("Const LOP", "Const_LOP", "lop_noimm_optionalPp__RCR_RCR"))
    val LopRegRegConstPred: Definition = Definition("LopRegRegConstPred", "LOP", Vec("lop_noimm__RCR_RCR"))
    val LopRegRegImm: Definition = Definition("LopRegRegImm", "LOP", Vec("Imm LOP", "Imm_LOP", "lop_imm_optionalPp_"))
    val LopRegRegImmPred: Definition = Definition("LopRegRegImmPred", "LOP", Vec("lop_imm_"))
    val LopRegRegReg: Definition =
      Definition("LopRegRegReg", "LOP", Vec("LOP", "lop_noimm_optionalPp__RRR_RRR", "lop_noimm_optionalPp__RUR_RUR"))
    val LopRegRegRegPred: Definition =
      Definition("LopRegRegRegPred", "LOP", Vec("lop_noimm__RRR_RRR", "lop_noimm__RUR_RUR"))
    val Lop3PredRegRegAddressImmReg: Definition =
      Definition("Lop3PredRegRegAddressImmReg", "LOP3", Vec("lop3_noimm_optionalPp__RCxR_RCxR"))
    val Lop3PredRegRegAddressImmRegImm: Definition =
      Definition("Lop3PredRegRegAddressImmRegImm", "LOP3", Vec("lop3_lut_optionalPp__RCxR_RCxR"))
    val Lop3PredRegRegAddressImmRegImmPred: Definition =
      Definition("Lop3PredRegRegAddressImmRegImmPred", "LOP3", Vec("lop3_lut__RCxR_RCxR"))
    val Lop3PredRegRegAddressImmRegPred: Definition =
      Definition("Lop3PredRegRegAddressImmRegPred", "LOP3", Vec("lop3_noimm__RCxR_RCxR"))
    val Lop3PredRegRegConstReg: Definition =
      Definition("Lop3PredRegRegConstReg", "LOP3", Vec("lop3_noimm_optionalPp__RCR_RCR"))
    val Lop3PredRegRegConstRegImm: Definition =
      Definition("Lop3PredRegRegConstRegImm", "LOP3", Vec("lop3_lut_optionalPp__RCR_RCR"))
    val Lop3PredRegRegConstRegImmPred: Definition =
      Definition("Lop3PredRegRegConstRegImmPred", "LOP3", Vec("lop3_lut__RCR_RCR"))
    val Lop3PredRegRegConstRegPred: Definition =
      Definition("Lop3PredRegRegConstRegPred", "LOP3", Vec("lop3_noimm__RCR_RCR"))
    val Lop3PredRegRegImmReg: Definition =
      Definition("Lop3PredRegRegImmReg", "LOP3", Vec("lop3_imm_optionalPp__RIR_RIR"))
    val Lop3PredRegRegImmRegImm: Definition =
      Definition("Lop3PredRegRegImmRegImm", "LOP3", Vec("lop3_lut_optionalPp__RuIR_RIR"))
    val Lop3PredRegRegImmRegImmPred: Definition =
      Definition("Lop3PredRegRegImmRegImmPred", "LOP3", Vec("lop3_lut__RuIR_RIR"))
    val Lop3PredRegRegImmRegPred: Definition = Definition("Lop3PredRegRegImmRegPred", "LOP3", Vec("lop3_imm__RIR_RIR"))
    val Lop3PredRegRegRegReg: Definition = Definition(
      "Lop3PredRegRegRegReg",
      "LOP3",
      Vec("LOP3", "lop3_noimm_optionalPp__RRR_RRR", "lop3_noimm_optionalPp__RUR_RUR")
    )
    val Lop3PredRegRegRegRegImm: Definition = Definition(
      "Lop3PredRegRegRegRegImm",
      "LOP3",
      Vec("LOP3_LUT", "lop3_lut_optionalPp__RRR_RRR", "lop3_lut_optionalPp__RUR_RUR")
    )
    val Lop3PredRegRegRegRegImmPred: Definition =
      Definition("Lop3PredRegRegRegRegImmPred", "LOP3", Vec("lop3_lut__RRR_RRR", "lop3_lut__RUR_RUR"))
    val Lop3PredRegRegRegRegPred: Definition =
      Definition("Lop3PredRegRegRegRegPred", "LOP3", Vec("lop3_noimm__RRR_RRR", "lop3_noimm__RUR_RUR"))
    val Lop3RegRegAddressImmReg: Definition =
      Definition("Lop3RegRegAddressImmReg", "LOP3", Vec("lop3_noimm_optionalPp__RCxR_RCxR"))
    val Lop3RegRegAddressImmRegImm: Definition =
      Definition("Lop3RegRegAddressImmRegImm", "LOP3", Vec("lop3_lut_optionalPp__RCxR_RCxR"))
    val Lop3RegRegAddressImmRegImmPred: Definition =
      Definition("Lop3RegRegAddressImmRegImmPred", "LOP3", Vec("lop3_lut__RCxR_RCxR"))
    val Lop3RegRegAddressImmRegPred: Definition =
      Definition("Lop3RegRegAddressImmRegPred", "LOP3", Vec("lop3_noimm__RCxR_RCxR"))
    val Lop3RegRegConstReg: Definition =
      Definition("Lop3RegRegConstReg", "LOP3", Vec("LOP3_Bconst", "lop3_noimm_optionalPp__RCR_RCR"))
    val Lop3RegRegConstRegImm: Definition =
      Definition("Lop3RegRegConstRegImm", "LOP3", Vec("LOP3_LUT_BConst", "lop3_lut_optionalPp__RCR_RCR"))
    val Lop3RegRegConstRegImmPred: Definition =
      Definition("Lop3RegRegConstRegImmPred", "LOP3", Vec("lop3_lut__RCR_RCR"))
    val Lop3RegRegConstRegPred: Definition = Definition("Lop3RegRegConstRegPred", "LOP3", Vec("lop3_noimm__RCR_RCR"))
    val Lop3RegRegImmReg: Definition =
      Definition("Lop3RegRegImmReg", "LOP3", Vec("LOP3_BImm", "lop3_imm_optionalPp__RIR_RIR"))
    val Lop3RegRegImmRegImm: Definition =
      Definition("Lop3RegRegImmRegImm", "LOP3", Vec("LOP3_LUT_BImm", "lop3_lut_optionalPp__RuIR_RIR"))
    val Lop3RegRegImmRegImmPred: Definition = Definition("Lop3RegRegImmRegImmPred", "LOP3", Vec("lop3_lut__RuIR_RIR"))
    val Lop3RegRegImmRegPred: Definition = Definition("Lop3RegRegImmRegPred", "LOP3", Vec("lop3_imm__RIR_RIR"))
    val Lop3RegRegRegReg: Definition = Definition(
      "Lop3RegRegRegReg",
      "LOP3",
      Vec("LOP3", "lop3_noimm_optionalPp__RRR_RRR", "lop3_noimm_optionalPp__RUR_RUR")
    )
    val Lop3RegRegRegRegImm: Definition = Definition(
      "Lop3RegRegRegRegImm",
      "LOP3",
      Vec("LOP3_LUT", "lop3_lut_optionalPp__RRR_RRR", "lop3_lut_optionalPp__RUR_RUR")
    )
    val Lop3RegRegRegRegImmPred: Definition =
      Definition("Lop3RegRegRegRegImmPred", "LOP3", Vec("lop3_lut__RRR_RRR", "lop3_lut__RUR_RUR"))
    val Lop3RegRegRegRegPred: Definition =
      Definition("Lop3RegRegRegRegPred", "LOP3", Vec("lop3_noimm__RRR_RRR", "lop3_noimm__RUR_RUR"))
    val Lop32iPredRegRegImm: Definition = Definition("Lop32iPredRegRegImm", "LOP32I", Vec("lop32i_optionalPp_"))
    val Lop32iPredRegRegImmPred: Definition = Definition("Lop32iPredRegRegImmPred", "LOP32I", Vec("lop32i_"))
    val Lop32iRegRegImm: Definition = Definition("Lop32iRegRegImm", "LOP32I", Vec("LOP32I", "lop32i_optionalPp_"))
    val Lop32iRegRegImmPred: Definition = Definition("Lop32iRegRegImmPred", "LOP32I", Vec("lop32i_"))
    val MatchPredRegReg: Definition = Definition("MatchPredRegReg", "MATCH", Vec("match__ALL"))
    val MatchRegReg: Definition = Definition("MatchRegReg", "MATCH", Vec("match__ANY"))
    val MembarNoOperands: Definition =
      Definition("MembarNoOperands", "MEMBAR", Vec("MEMBAR", "membar_", "membar_async_", "membar_tma_"))
    val MovAddressConst: Definition = Definition("MovAddressConst", "MOV", Vec("mov_indexedRF_IRFd__Cb"))
    val MovAddressConstImm: Definition = Definition("MovAddressConstImm", "MOV", Vec("mov_indexedRF_IRFd__Cb"))
    val MovAddressImm: Definition = Definition("MovAddressImm", "MOV", Vec("mov_indexedRF_IRFd__Ib"))
    val MovAddressImmImm: Definition = Definition("MovAddressImmImm", "MOV", Vec("mov_indexedRF_IRFd__Ib"))
    val MovAddressReg: Definition = Definition("MovAddressReg", "MOV", Vec("mov_indexedRF_IRFd__Rb"))
    val MovAddressRegImm: Definition = Definition("MovAddressRegImm", "MOV", Vec("mov_indexedRF_IRFd__Rb"))
    val MovRegAddress: Definition = Definition("MovRegAddress", "MOV", Vec("mov_indexedRF_Rd_"))
    val MovRegAddressImm: Definition = Definition("MovRegAddressImm", "MOV", Vec("mov__RCx", "mov_indexedRF_Rd_"))
    val MovRegAddressImmImm: Definition = Definition("MovRegAddressImmImm", "MOV", Vec("mov__RCx"))
    val MovRegConst: Definition =
      Definition("MovRegConst", "MOV", Vec("Const MOV", "Const_MOV", "mov__RC", "mov__RCR_RC"))
    val MovRegConstImm: Definition =
      Definition("MovRegConstImm", "MOV", Vec("Const MOV", "Const_MOV", "mov__RC", "mov__RCR_RC"))
    val MovRegImm: Definition =
      Definition("MovRegImm", "MOV", Vec("Imm MOV", "Imm_MOV", "mov__RI", "mov__RuIR_RI", "mov_imm64_"))
    val MovRegImmImm: Definition =
      Definition("MovRegImmImm", "MOV", Vec("Imm MOV", "Imm_MOV", "mov__RI", "mov__RuIR_RI"))
    val MovRegReg: Definition = Definition(
      "MovRegReg",
      "MOV",
      Vec("MOV", "mov_64__RR", "mov_64__RU", "mov__RR", "mov__RRR_RR", "mov__RU", "mov_nonconformity__RU")
    )
    val MovRegRegImm: Definition = Definition("MovRegRegImm", "MOV", Vec("MOV", "mov__RR", "mov__RRR_RR", "mov__RU"))
    val Mov32iRegImm: Definition = Definition("Mov32iRegImm", "MOV32I", Vec("MOV32I", "mov32i_"))
    val Mov32iRegImmImm: Definition = Definition("Mov32iRegImmImm", "MOV32I", Vec("MOV32I", "mov32i_"))
    val Mov64iurRegImm: Definition = Definition("Mov64iurRegImm", "MOV64IUR", Vec("mov64iur_imm_"))
    val Mov64iurRegReg: Definition = Definition("Mov64iurRegReg", "MOV64IUR", Vec("mov64iur_ur_"))
    val MovmRegReg: Definition = Definition("MovmRegReg", "MOVM", Vec("movm_"))
    val MufuRegAddressImm: Definition = Definition("MufuRegAddressImm", "MUFU", Vec("mufu__RCxR_RCx", "mufu_fp16__RCx"))
    val MufuRegConst: Definition = Definition("MufuRegConst", "MUFU", Vec("mufu__RCR_RC", "mufu_fp16__RC"))
    val MufuRegImm: Definition = Definition("MufuRegImm", "MUFU", Vec("mufu__RIR_RI", "mufu_fp16__RI"))
    val MufuRegImmImm: Definition = Definition("MufuRegImmImm", "MUFU", Vec("mufu_fp16_simd__RI"))
    val MufuRegReg: Definition = Definition(
      "MufuRegReg",
      "MUFU",
      Vec(
        "MUFU",
        "mufu__RRR_RR",
        "mufu__RUR_RU",
        "mufu_fp16__RR",
        "mufu_fp16__RU",
        "mufu_fp16_simd__RR",
        "mufu_fp16_simd__RU",
        "mufu_fp16_swap__RR",
        "mufu_fp16_swap__RU"
      )
    )
    val NanosleepNoOperands: Definition = Definition("NanosleepNoOperands", "NANOSLEEP", Vec("nanosleep_clear_"))
    val NanosleepAddressImm: Definition = Definition("NanosleepAddressImm", "NANOSLEEP", Vec("nanosleep__CX"))
    val NanosleepConst: Definition = Definition("NanosleepConst", "NANOSLEEP", Vec("nanosleep__C"))
    val NanosleepImm: Definition = Definition("NanosleepImm", "NANOSLEEP", Vec("nanosleep__I"))
    val NanosleepPred: Definition = Definition("NanosleepPred", "NANOSLEEP", Vec("nanosleep_clear_"))
    val NanosleepPredAddressImm: Definition = Definition("NanosleepPredAddressImm", "NANOSLEEP", Vec("nanosleep__CX"))
    val NanosleepPredConst: Definition = Definition("NanosleepPredConst", "NANOSLEEP", Vec("nanosleep__C"))
    val NanosleepPredImm: Definition = Definition("NanosleepPredImm", "NANOSLEEP", Vec("nanosleep__I"))
    val NanosleepPredReg: Definition = Definition("NanosleepPredReg", "NANOSLEEP", Vec("nanosleep__R", "nanosleep__U"))
    val NanosleepReg: Definition = Definition("NanosleepReg", "NANOSLEEP", Vec("nanosleep__R", "nanosleep__U"))
    val NanotrapConst: Definition = Definition("NanotrapConst", "NANOTRAP", Vec("nanotrap__C"))
    val NanotrapImm: Definition = Definition("NanotrapImm", "NANOTRAP", Vec("nanotrap__I"))
    val NanotrapPredConst: Definition = Definition("NanotrapPredConst", "NANOTRAP", Vec("nanotrap__C"))
    val NanotrapPredImm: Definition = Definition("NanotrapPredImm", "NANOTRAP", Vec("nanotrap__I"))
    val NanotrapPredReg: Definition = Definition("NanotrapPredReg", "NANOTRAP", Vec("nanotrap__R"))
    val NanotrapReg: Definition = Definition("NanotrapReg", "NANOTRAP", Vec("nanotrap__R"))
    val NopNoOperands: Definition = Definition("NopNoOperands", "NOP", Vec("NOP", "NOP trig", "NOP_trig", "nop_"))
    val NopImm: Definition = Definition("NopImm", "NOP", Vec("NOP", "NOP trig", "NOP_trig"))
    val NopSymbol: Definition = Definition("NopSymbol", "NOP", Vec("NOP", "NOP trig", "NOP_trig"))
    val NopSymbolImm: Definition = Definition("NopSymbolImm", "NOP", Vec("NOP", "NOP trig", "NOP_trig"))
    val OmmaRegRegRegRegPredRegRegReg: Definition =
      Definition("OmmaRegRegRegRegPredRegRegReg", "OMMA", Vec("omma_scale_"))
    val OmmaRegRegRegRegPredRegRegRegImm: Definition =
      Definition("OmmaRegRegRegRegPredRegRegRegImm", "OMMA", Vec("omma_sp_scale_"))
    val OmmaRegRegRegRegRegRegReg: Definition = Definition("OmmaRegRegRegRegRegRegReg", "OMMA", Vec("omma_scale_"))
    val OmmaRegRegRegRegRegRegRegImm: Definition =
      Definition("OmmaRegRegRegRegRegRegRegImm", "OMMA", Vec("omma_sp_scale_"))
    val OutReg: Definition = Definition("OutReg", "OUT", Vec("out__FINAL", "out_final_rz_"))
    val OutRegRegConst: Definition = Definition("OutRegRegConst", "OUT", Vec("Const OUT", "Const_OUT"))
    val OutRegRegImm: Definition = Definition("OutRegRegImm", "OUT", Vec("Imm OUT", "Imm_OUT", "out__EMIT_Imm"))
    val OutRegRegReg: Definition =
      Definition("OutRegRegReg", "OUT", Vec("OUT", "out__CUT", "out__EMIT_Rb", "out__EMIT_URb", "out__FINAL"))
    val P2rReg: Definition = Definition("P2rReg", "P2R", Vec("Simple_P2R"))
    val P2rRegRegConst: Definition = Definition("P2rRegRegConst", "P2R", Vec("Const_P2R"))
    val P2rRegRegImm: Definition = Definition("P2rRegRegImm", "P2R", Vec("Imm_P2R"))
    val P2rRegRegReg: Definition = Definition("P2rRegRegReg", "P2R", Vec("P2R"))
    val P2rRegSymbol: Definition = Definition("P2rRegSymbol", "P2R", Vec("Simple P2R", "Simple_P2R", "p2r_simple_"))
    val P2rRegSymbolRegAddressImm: Definition = Definition("P2rRegSymbolRegAddressImm", "P2R", Vec("p2r__RCxR_RCxR"))
    val P2rRegSymbolRegConst: Definition =
      Definition("P2rRegSymbolRegConst", "P2R", Vec("Const P2R", "Const_P2R", "p2r__RCR_RCR"))
    val P2rRegSymbolRegImm: Definition =
      Definition("P2rRegSymbolRegImm", "P2R", Vec("Imm P2R", "Imm_P2R", "p2r__RsIR_RIR", "p2r__RuIR_RIR"))
    val P2rRegSymbolRegReg: Definition =
      Definition("P2rRegSymbolRegReg", "P2R", Vec("P2R", "p2r__RRR_RRR", "p2r__RUR_RUR"))
    val PbkConst: Definition = Definition("PbkConst", "PBK", Vec("PBK_c", "SSY c"))
    val PbkImm: Definition = Definition("PbkImm", "PBK", Vec("PBK", "SSY"))
    val PcntConst: Definition = Definition("PcntConst", "PCNT", Vec("PCNT_c", "SSY c"))
    val PcntImm: Definition = Definition("PcntImm", "PCNT", Vec("PCNT", "SSY"))
    val PexitImm: Definition = Definition("PexitImm", "PEXIT", Vec("PEXIT"))
    val PixldReg: Definition = Definition("PixldReg", "PIXLD", Vec("PIXLD simple", "PIXLD_simple", "pixld_"))
    val PixldRegAddress: Definition = Definition("PixldRegAddress", "PIXLD", Vec("I PIXLD", "I_PIXLD", "PIXLD"))
    val PixldRegPred: Definition = Definition("PixldRegPred", "PIXLD", Vec("PIXLD simple", "PIXLD_simple", "pixld_"))
    val PixldRegPredAddress: Definition = Definition("PixldRegPredAddress", "PIXLD", Vec("I PIXLD", "I_PIXLD", "PIXLD"))
    val PlongjmpConst: Definition = Definition("PlongjmpConst", "PLONGJMP", Vec("PLONGJMP_c", "SSY c"))
    val PlongjmpImm: Definition = Definition("PlongjmpImm", "PLONGJMP", Vec("PLONGJMP", "SSY"))
    val Plop3PredPredAddressImmPredImm: Definition =
      Definition("Plop3PredPredAddressImmPredImm", "PLOP3", Vec("plop3_lut_1out_1reg__RCxR"))
    val Plop3PredPredAddressImmRegImm: Definition =
      Definition("Plop3PredPredAddressImmRegImm", "PLOP3", Vec("plop3_lut_1out_2reg__RCxR"))
    val Plop3PredPredConstPredImm: Definition =
      Definition("Plop3PredPredConstPredImm", "PLOP3", Vec("plop3_lut_1out_1reg__RCR"))
    val Plop3PredPredConstRegImm: Definition =
      Definition("Plop3PredPredConstRegImm", "PLOP3", Vec("plop3_lut_1out_2reg__RCR"))
    val Plop3PredPredPredAddressImmPredImmImm: Definition =
      Definition("Plop3PredPredPredAddressImmPredImmImm", "PLOP3", Vec("plop3_lut_2out_1reg__RCxR"))
    val Plop3PredPredPredAddressImmRegImmImm: Definition =
      Definition("Plop3PredPredPredAddressImmRegImmImm", "PLOP3", Vec("plop3_lut_2out_2reg__RCxR"))
    val Plop3PredPredPredConstPredImmImm: Definition =
      Definition("Plop3PredPredPredConstPredImmImm", "PLOP3", Vec("plop3_lut_2out_1reg__RCR"))
    val Plop3PredPredPredConstRegImmImm: Definition =
      Definition("Plop3PredPredPredConstRegImmImm", "PLOP3", Vec("plop3_lut_2out_2reg__RCR"))
    val Plop3PredPredPredPred: Definition =
      Definition("Plop3PredPredPredPred", "PLOP3", Vec("plop3_1out_", "plop3_1out_uniform_"))
    val Plop3PredPredPredPredImm: Definition =
      Definition("Plop3PredPredPredPredImm", "PLOP3", Vec("plop3_lut_1out_", "plop3_lut_1out_uniform_"))
    val Plop3PredPredPredPredPredImmImm: Definition =
      Definition("Plop3PredPredPredPredPredImmImm", "PLOP3", Vec("plop3_lut_2out_", "plop3_lut_2out_uniform_"))
    val Plop3PredPredPredRegPredImmImm: Definition =
      Definition("Plop3PredPredPredRegPredImmImm", "PLOP3", Vec("plop3_lut_2out_1reg__RRR", "plop3_lut_2out_1reg__RUR"))
    val Plop3PredPredPredRegRegImmImm: Definition =
      Definition("Plop3PredPredPredRegRegImmImm", "PLOP3", Vec("plop3_lut_2out_2reg__RRR", "plop3_lut_2out_2reg__RUR"))
    val Plop3PredPredRegAddressImmRegImmImm: Definition =
      Definition("Plop3PredPredRegAddressImmRegImmImm", "PLOP3", Vec("plop3_lut_2out_3reg__RCxR"))
    val Plop3PredPredRegConstRegImmImm: Definition =
      Definition("Plop3PredPredRegConstRegImmImm", "PLOP3", Vec("plop3_lut_2out_3reg__RCR"))
    val Plop3PredPredRegPredImm: Definition =
      Definition("Plop3PredPredRegPredImm", "PLOP3", Vec("plop3_lut_1out_1reg__RRR", "plop3_lut_1out_1reg__RUR"))
    val Plop3PredPredRegRegImm: Definition =
      Definition("Plop3PredPredRegRegImm", "PLOP3", Vec("plop3_lut_1out_2reg__RRR", "plop3_lut_1out_2reg__RUR"))
    val Plop3PredPredRegRegRegImmImm: Definition =
      Definition("Plop3PredPredRegRegRegImmImm", "PLOP3", Vec("plop3_lut_2out_3reg__RRR", "plop3_lut_2out_3reg__RUR"))
    val Plop3PredRegAddressImmRegImm: Definition =
      Definition("Plop3PredRegAddressImmRegImm", "PLOP3", Vec("plop3_lut_1out_3reg__RCxR"))
    val Plop3PredRegConstRegImm: Definition =
      Definition("Plop3PredRegConstRegImm", "PLOP3", Vec("plop3_lut_1out_3reg__RCR"))
    val Plop3PredRegRegRegImm: Definition =
      Definition("Plop3PredRegRegRegImm", "PLOP3", Vec("plop3_lut_1out_3reg__RRR", "plop3_lut_1out_3reg__RUR"))
    val PmtrigImm: Definition = Definition("PmtrigImm", "PMTRIG", Vec("pmtrig_"))
    val PmtrigPredImm: Definition = Definition("PmtrigPredImm", "PMTRIG", Vec("pmtrig_"))
    val PopcRegAddressImm: Definition = Definition("PopcRegAddressImm", "POPC", Vec("popc__RCxR_RCxR"))
    val PopcRegConst: Definition = Definition("PopcRegConst", "POPC", Vec("Const_POPC", "popc__RCR_RCR"))
    val PopcRegImm: Definition = Definition("PopcRegImm", "POPC", Vec("Imm_POPC", "popc__RuIR_RIR"))
    val PopcRegReg: Definition = Definition("PopcRegReg", "POPC", Vec("POPC", "popc__RRR_RRR", "popc__RUR_RUR"))
    val PopcRegRegConst: Definition = Definition("PopcRegRegConst", "POPC", Vec("Const POPC"))
    val PopcRegRegImm: Definition = Definition("PopcRegRegImm", "POPC", Vec("Imm POPC"))
    val PopcRegRegReg: Definition = Definition("PopcRegRegReg", "POPC", Vec("POPC"))
    val PreexitNoOperands: Definition = Definition("PreexitNoOperands", "PREEXIT", Vec("preexit_"))
    val PretConst: Definition = Definition("PretConst", "PRET", Vec("PRET c", "PRET_c"))
    val PretImm: Definition = Definition("PretImm", "PRET", Vec("PRET"))
    val PrmtRegRegAddressImmReg: Definition = Definition("PrmtRegRegAddressImmReg", "PRMT", Vec("prmt__RCxR_RCxR"))
    val PrmtRegRegConstReg: Definition =
      Definition("PrmtRegRegConstReg", "PRMT", Vec("Const PRMT", "Const_PRMT", "prmt__RCR_RCR"))
    val PrmtRegRegImmReg: Definition =
      Definition("PrmtRegRegImmReg", "PRMT", Vec("Imm PRMT", "Imm_PRMT", "prmt__RuIR_RIR"))
    val PrmtRegRegRegAddressImm: Definition = Definition("PrmtRegRegRegAddressImm", "PRMT", Vec("prmt__RRCx_RRCx"))
    val PrmtRegRegRegConst: Definition =
      Definition("PrmtRegRegRegConst", "PRMT", Vec("Const1 PRMT", "Const1_PRMT", "prmt__RRC_RRC"))
    val PrmtRegRegRegImm: Definition = Definition("PrmtRegRegRegImm", "PRMT", Vec("prmt__RRuI_RRI"))
    val PrmtRegRegRegReg: Definition =
      Definition("PrmtRegRegRegReg", "PRMT", Vec("PRMT", "prmt__RRR_RRR", "prmt__RRU_RRU", "prmt__RUR_RUR"))
    val PsetRegPredPred: Definition = Definition("PsetRegPredPred", "PSET", Vec("NoBop PSET", "NoBop_PSET"))
    val PsetRegPredPredPred: Definition = Definition("PsetRegPredPredPred", "PSET", Vec("PSET"))
    val PsetpPredPredPred: Definition =
      Definition("PsetpPredPredPred", "PSETP", Vec("NoBop PSETP", "NoBop_PSETP", "psetp_simple_"))
    val PsetpPredPredPredPredPred: Definition =
      Definition("PsetpPredPredPredPredPred", "PSETP", Vec("PSETP", "psetp_", "psetp_uniform_"))
    val Qadd4RegRegImm: Definition = Definition("Qadd4RegRegImm", "QADD4", Vec("qadd4_rb_imm_"))
    val Qadd4RegRegReg: Definition = Definition("Qadd4RegRegReg", "QADD4", Vec("qadd4_rc_", "qadd4_urc_"))
    val Qfma4RegRegImmReg: Definition = Definition("Qfma4RegRegImmReg", "QFMA4", Vec("qfma4_imm_rc_"))
    val Qfma4RegRegRegImm: Definition = Definition("Qfma4RegRegRegImm", "QFMA4", Vec("qfma4_rb_imm_"))
    val Qfma4RegRegRegReg: Definition =
      Definition("Qfma4RegRegRegReg", "QFMA4", Vec("qfma4_rb_rc_", "qfma4_rb_urc_", "qfma4_urb_rc_"))
    val QgmmaRegAddressReg: Definition = Definition("QgmmaRegAddressReg", "QGMMA", Vec("qgmma_URa_Rc_"))
    val QgmmaRegAddressRegPred: Definition = Definition("QgmmaRegAddressRegPred", "QGMMA", Vec("qgmma_URa_Rc_"))
    val QgmmaRegAddressRegPredReg: Definition =
      Definition("QgmmaRegAddressRegPredReg", "QGMMA", Vec("qgmma_sparse_URa_Rc_"))
    val QgmmaRegAddressRegPredRegImm: Definition =
      Definition("QgmmaRegAddressRegPredRegImm", "QGMMA", Vec("qgmma_sparse_URa_Rc_"))
    val QgmmaRegAddressRegReg: Definition =
      Definition("QgmmaRegAddressRegReg", "QGMMA", Vec("qgmma_URa_Rb_Rc_", "qgmma_sparse_URa_Rc_"))
    val QgmmaRegAddressRegRegImm: Definition =
      Definition("QgmmaRegAddressRegRegImm", "QGMMA", Vec("qgmma_sparse_URa_Rc_"))
    val QgmmaRegAddressRegRegPred: Definition =
      Definition("QgmmaRegAddressRegRegPred", "QGMMA", Vec("qgmma_URa_Rb_Rc_"))
    val QgmmaRegAddressRegRegPredReg: Definition =
      Definition("QgmmaRegAddressRegRegPredReg", "QGMMA", Vec("qgmma_sparse_URa_Rb_Rc_"))
    val QgmmaRegAddressRegRegPredRegImm: Definition =
      Definition("QgmmaRegAddressRegRegPredRegImm", "QGMMA", Vec("qgmma_sparse_URa_Rb_Rc_"))
    val QgmmaRegAddressRegRegReg: Definition =
      Definition("QgmmaRegAddressRegRegReg", "QGMMA", Vec("qgmma_sparse_URa_Rb_Rc_"))
    val QgmmaRegAddressRegRegRegImm: Definition =
      Definition("QgmmaRegAddressRegRegRegImm", "QGMMA", Vec("qgmma_sparse_URa_Rb_Rc_"))
    val QgmmaRegRegAddressReg: Definition = Definition("QgmmaRegRegAddressReg", "QGMMA", Vec("qgmma_Ra_URb_Rc_"))
    val QgmmaRegRegAddressRegPred: Definition =
      Definition("QgmmaRegRegAddressRegPred", "QGMMA", Vec("qgmma_Ra_URb_Rc_"))
    val QgmmaRegRegAddressRegPredReg: Definition =
      Definition("QgmmaRegRegAddressRegPredReg", "QGMMA", Vec("qgmma_sparse_Ra_URb_Rc_"))
    val QgmmaRegRegAddressRegPredRegImm: Definition =
      Definition("QgmmaRegRegAddressRegPredRegImm", "QGMMA", Vec("qgmma_sparse_Ra_URb_Rc_"))
    val QgmmaRegRegAddressRegReg: Definition =
      Definition("QgmmaRegRegAddressRegReg", "QGMMA", Vec("qgmma_sparse_Ra_URb_Rc_"))
    val QgmmaRegRegAddressRegRegImm: Definition =
      Definition("QgmmaRegRegAddressRegRegImm", "QGMMA", Vec("qgmma_sparse_Ra_URb_Rc_"))
    val QmmaRegRegRegReg: Definition = Definition("QmmaRegRegRegReg", "QMMA", Vec("qmma_", "qmma_rowcol_"))
    val QmmaRegRegRegRegPred: Definition = Definition("QmmaRegRegRegRegPred", "QMMA", Vec("qmma_", "qmma_rowcol_"))
    val QmmaRegRegRegRegPredRegImm: Definition =
      Definition("QmmaRegRegRegRegPredRegImm", "QMMA", Vec("qmma_sp_", "qmma_sp_rowcol_"))
    val QmmaRegRegRegRegPredRegRegReg: Definition =
      Definition("QmmaRegRegRegRegPredRegRegReg", "QMMA", Vec("qmma_scale_"))
    val QmmaRegRegRegRegPredRegRegRegImm: Definition =
      Definition("QmmaRegRegRegRegPredRegRegRegImm", "QMMA", Vec("qmma_sp_scale_"))
    val QmmaRegRegRegRegRegImm: Definition =
      Definition("QmmaRegRegRegRegRegImm", "QMMA", Vec("qmma_sp_", "qmma_sp_rowcol_"))
    val QmmaRegRegRegRegRegRegReg: Definition = Definition("QmmaRegRegRegRegRegRegReg", "QMMA", Vec("qmma_scale_"))
    val QmmaRegRegRegRegRegRegRegImm: Definition =
      Definition("QmmaRegRegRegRegRegRegRegImm", "QMMA", Vec("qmma_sp_scale_"))
    val Qmul4RegRegImm: Definition = Definition("Qmul4RegRegImm", "QMUL4", Vec("qmul4_imm_"))
    val Qmul4RegRegReg: Definition = Definition("Qmul4RegRegReg", "QMUL4", Vec("qmul4_rb_", "qmul4_urb_"))
    val QspcPredAddress: Definition = Definition(
      "QspcPredAddress",
      "QSPC",
      Vec(
        "qspc_PuOnly__RaNonRZ",
        "qspc_PuOnly__RaRZ",
        "qspc_urb_PuOnly__Ra32",
        "qspc_urb_PuOnly__Ra64",
        "qspc_urb_PuOnly__RaRZ"
      )
    )
    val QspcPredRegAddress: Definition = Definition(
      "QspcPredRegAddress",
      "QSPC",
      Vec("qspc__RaNonRZ", "qspc__RaRZ", "qspc_urb__Ra32", "qspc_urb__Ra64", "qspc_urb__RaRZ")
    )
    val QspcRegAddress: Definition = Definition(
      "QspcRegAddress",
      "QSPC",
      Vec(
        "qspc_RdOnly__RaNonRZ",
        "qspc_RdOnly__RaRZ",
        "qspc_urb_RdOnly__Ra32",
        "qspc_urb_RdOnly__Ra64",
        "qspc_urb_RdOnly__RaRZ"
      )
    )
    val R2bImmReg: Definition = Definition("R2bImmReg", "R2B", Vec("R2B", "r2b_"))
    val R2pReg: Definition = Definition("R2pReg", "R2P", Vec("Imm_R2P"))
    val R2pRegConst: Definition = Definition("R2pRegConst", "R2P", Vec("Const_R2P"))
    val R2pRegImm: Definition = Definition("R2pRegImm", "R2P", Vec("Imm_R2P"))
    val R2pRegReg: Definition = Definition("R2pRegReg", "R2P", Vec("R2P"))
    val R2pSymbolReg: Definition = Definition("R2pSymbolReg", "R2P", Vec("Imm R2P", "Imm_R2P", "r2p__RIR"))
    val R2pSymbolRegAddressImm: Definition = Definition("R2pSymbolRegAddressImm", "R2P", Vec("r2p__RCxR"))
    val R2pSymbolRegConst: Definition =
      Definition("R2pSymbolRegConst", "R2P", Vec("Const R2P", "Const_R2P", "r2p__RCR"))
    val R2pSymbolRegImm: Definition = Definition("R2pSymbolRegImm", "R2P", Vec("Imm R2P", "Imm_R2P", "r2p__RIR"))
    val R2pSymbolRegReg: Definition = Definition("R2pSymbolRegReg", "R2P", Vec("R2P", "r2p__RRR", "r2p__RUR"))
    val R2urPredRegReg: Definition = Definition(
      "R2urPredRegReg",
      "R2UR",
      Vec("r2ur__OR", "r2ur__noOR", "r2ur_nonconformity__OR", "r2ur_nonconformity__noOR")
    )
    val R2urRegReg: Definition = Definition("R2urRegReg", "R2UR", Vec("r2ur__noOR", "r2ur_nonconformity__noOR"))
    val RamNoOperands: Definition = Definition("RamNoOperands", "RAM", Vec("RAM", "SAM"))
    val RedAddressReg: Definition = Definition(
      "RedAddressReg",
      "RED",
      Vec(
        "I RED",
        "I_RED",
        "RED",
        "red__RaNonRZ",
        "red__RaRZ",
        "red_fp__RaNonRZ",
        "red_fp__RaRZ",
        "red_fp_uniform__Ra32",
        "red_fp_uniform__Ra64",
        "red_fp_uniform__RaRZ",
        "red_fp_uniform__memdesc",
        "red_int__RaNonRZ",
        "red_int__RaRZ",
        "red_int_uniform__Ra32",
        "red_int_uniform__Ra64",
        "red_int_uniform__RaRZ",
        "red_int_uniform__memdesc",
        "red_uniform__Ra32",
        "red_uniform__Ra64",
        "red_uniform__RaRZ",
        "red_uniform__memdesc",
        "red_uniform_weakonly__Ra32",
        "red_uniform_weakonly__Ra64",
        "red_uniform_weakonly__RaRZ",
        "red_weakonly__RaNonRZ",
        "red_weakonly__RaRZ"
      )
    )
    val RedasAddressReg: Definition =
      Definition("RedasAddressReg", "REDAS", Vec("redas_64__Ra64", "redas_64__RaRZ", "redas__Ra32"))
    val RedgAddressReg: Definition = Definition(
      "RedgAddressReg",
      "REDG",
      Vec(
        "redg_fp__RaNonRZ",
        "redg_fp__RaRZ",
        "redg_fp_uniform__Ra32",
        "redg_fp_uniform__Ra64",
        "redg_fp_uniform__RaRZ",
        "redg_fp_uniform__memdesc",
        "redg_int__RaNonRZ",
        "redg_int__RaRZ",
        "redg_int_uniform__Ra32",
        "redg_int_uniform__Ra64",
        "redg_int_uniform__RaRZ",
        "redg_int_uniform__memdesc"
      )
    )
    val RedsAddressReg: Definition =
      Definition("RedsAddressReg", "REDS", Vec("atoms_reds__RaNonRZ", "atoms_reds__RaRZ", "atoms_reds_uniform_"))
    val ReduxRegReg: Definition = Definition("ReduxRegReg", "REDUX", Vec("redux_"))
    val RetNoOperands: Definition = Definition("RetNoOperands", "RET", Vec("RET"))
    val RetPredReg: Definition =
      Definition("RetPredReg", "RET", Vec("ret_rel_reg_", "ret_rel_reg__RIR", "ret_rel_reg__URIR"))
    val RetPredRegImm: Definition = Definition(
      "RetPredRegImm",
      "RET",
      Vec("ret__ABS", "ret__ABS_UR", "ret__REL", "ret__REL_UR", "ret_rel_", "ret_rel__RIR", "ret_rel__URIR")
    )
    val RetReg: Definition = Definition("RetReg", "RET", Vec("ret_rel_reg_", "ret_rel_reg__RIR", "ret_rel_reg__URIR"))
    val RetRegImm: Definition = Definition(
      "RetRegImm",
      "RET",
      Vec("ret__ABS", "ret__ABS_UR", "ret__REL", "ret__REL_UR", "ret_rel_", "ret_rel__RIR", "ret_rel__URIR")
    )
    val RpcmovAddressImm: Definition =
      Definition("RpcmovAddressImm", "RPCMOV", Vec("rpcmov_dstPc64__CXb", "rpcmov_dstPc_CXb_"))
    val RpcmovConst: Definition =
      Definition("RpcmovConst", "RPCMOV", Vec("rpcmov_dstPc64__Const", "rpcmov_dstPc_const_"))
    val RpcmovImm: Definition = Definition("RpcmovImm", "RPCMOV", Vec("rpcmov_dstPc64__Imm", "rpcmov_dstPc_imm_"))
    val RpcmovReg: Definition = Definition(
      "RpcmovReg",
      "RPCMOV",
      Vec("rpcmov_dstPc64__URb", "rpcmov_dstPc_", "rpcmov_dstPc_URb_", "rpcmov_srcPc_")
    )
    val RroRegConst: Definition = Definition("RroRegConst", "RRO", Vec("Const RRO", "Const_RRO"))
    val RroRegImm: Definition = Definition("RroRegImm", "RRO", Vec("Imm RRO", "Imm_RRO"))
    val RroRegReg: Definition = Definition("RroRegReg", "RRO", Vec("RRO"))
    val RttNoOperands: Definition = Definition("RttNoOperands", "RTT", Vec("RTT", "rtt_"))
    val S2rRegSpecial: Definition = Definition("S2rRegSpecial", "S2R", Vec("S2R", "s2r_"))
    val S2urRegSpecial: Definition = Definition("S2urRegSpecial", "S2UR", Vec("s2ur_"))
    val SamNoOperands: Definition = Definition("SamNoOperands", "SAM", Vec("SAM"))
    val ScatterRegRegRegRegImmImm: Definition =
      Definition("ScatterRegRegRegRegImmImm", "SCATTER", Vec("scatter_", "scatter_combined_idxtype_extract_"))
    val SelRegRegAddressImmPred: Definition = Definition("SelRegRegAddressImmPred", "SEL", Vec("sel__RCxR_RCxR"))
    val SelRegRegConstPred: Definition =
      Definition("SelRegRegConstPred", "SEL", Vec("Const SEL", "Const_SEL", "sel__RCR_RCR"))
    val SelRegRegImmPred: Definition =
      Definition("SelRegRegImmPred", "SEL", Vec("Imm SEL", "Imm_SEL", "sel_64__RIR", "sel__RuIR_RIR"))
    val SelRegRegRegPred: Definition =
      Definition("SelRegRegRegPred", "SEL", Vec("SEL", "sel_64__RRR", "sel_64__RUR", "sel__RRR_RRR", "sel__RUR_RUR"))
    val definitions: Vec[Definition] = Vec(
      LeaRegRegRegImm,
      LeaRegRegRegImmImm,
      LeaRegRegRegImmImmPred,
      LeaRegRegRegImmPred,
      LeaRegRegRegReg,
      LeaRegRegRegRegImm,
      LeaRegRegRegRegImmPred,
      LepcReg,
      LepcRegImm,
      LongjmpNoOperands,
      LopPredRegRegAddressImm,
      LopPredRegRegAddressImmPred,
      LopPredRegRegConst,
      LopPredRegRegConstPred,
      LopPredRegRegImm,
      LopPredRegRegImmPred,
      LopPredRegRegReg,
      LopPredRegRegRegPred,
      LopRegRegAddressImm,
      LopRegRegAddressImmPred,
      LopRegRegConst,
      LopRegRegConstPred,
      LopRegRegImm,
      LopRegRegImmPred,
      LopRegRegReg,
      LopRegRegRegPred,
      Lop3PredRegRegAddressImmReg,
      Lop3PredRegRegAddressImmRegImm,
      Lop3PredRegRegAddressImmRegImmPred,
      Lop3PredRegRegAddressImmRegPred,
      Lop3PredRegRegConstReg,
      Lop3PredRegRegConstRegImm,
      Lop3PredRegRegConstRegImmPred,
      Lop3PredRegRegConstRegPred,
      Lop3PredRegRegImmReg,
      Lop3PredRegRegImmRegImm,
      Lop3PredRegRegImmRegImmPred,
      Lop3PredRegRegImmRegPred,
      Lop3PredRegRegRegReg,
      Lop3PredRegRegRegRegImm,
      Lop3PredRegRegRegRegImmPred,
      Lop3PredRegRegRegRegPred,
      Lop3RegRegAddressImmReg,
      Lop3RegRegAddressImmRegImm,
      Lop3RegRegAddressImmRegImmPred,
      Lop3RegRegAddressImmRegPred,
      Lop3RegRegConstReg,
      Lop3RegRegConstRegImm,
      Lop3RegRegConstRegImmPred,
      Lop3RegRegConstRegPred,
      Lop3RegRegImmReg,
      Lop3RegRegImmRegImm,
      Lop3RegRegImmRegImmPred,
      Lop3RegRegImmRegPred,
      Lop3RegRegRegReg,
      Lop3RegRegRegRegImm,
      Lop3RegRegRegRegImmPred,
      Lop3RegRegRegRegPred,
      Lop32iPredRegRegImm,
      Lop32iPredRegRegImmPred,
      Lop32iRegRegImm,
      Lop32iRegRegImmPred,
      MatchPredRegReg,
      MatchRegReg,
      MembarNoOperands,
      MovAddressConst,
      MovAddressConstImm,
      MovAddressImm,
      MovAddressImmImm,
      MovAddressReg,
      MovAddressRegImm,
      MovRegAddress,
      MovRegAddressImm,
      MovRegAddressImmImm,
      MovRegConst,
      MovRegConstImm,
      MovRegImm,
      MovRegImmImm,
      MovRegReg,
      MovRegRegImm,
      Mov32iRegImm,
      Mov32iRegImmImm,
      Mov64iurRegImm,
      Mov64iurRegReg,
      MovmRegReg,
      MufuRegAddressImm,
      MufuRegConst,
      MufuRegImm,
      MufuRegImmImm,
      MufuRegReg,
      NanosleepNoOperands,
      NanosleepAddressImm,
      NanosleepConst,
      NanosleepImm,
      NanosleepPred,
      NanosleepPredAddressImm,
      NanosleepPredConst,
      NanosleepPredImm,
      NanosleepPredReg,
      NanosleepReg,
      NanotrapConst,
      NanotrapImm,
      NanotrapPredConst,
      NanotrapPredImm,
      NanotrapPredReg,
      NanotrapReg,
      NopNoOperands,
      NopImm,
      NopSymbol,
      NopSymbolImm,
      OmmaRegRegRegRegPredRegRegReg,
      OmmaRegRegRegRegPredRegRegRegImm,
      OmmaRegRegRegRegRegRegReg,
      OmmaRegRegRegRegRegRegRegImm,
      OutReg,
      OutRegRegConst,
      OutRegRegImm,
      OutRegRegReg,
      P2rReg,
      P2rRegRegConst,
      P2rRegRegImm,
      P2rRegRegReg,
      P2rRegSymbol,
      P2rRegSymbolRegAddressImm,
      P2rRegSymbolRegConst,
      P2rRegSymbolRegImm,
      P2rRegSymbolRegReg,
      PbkConst,
      PbkImm,
      PcntConst,
      PcntImm,
      PexitImm,
      PixldReg,
      PixldRegAddress,
      PixldRegPred,
      PixldRegPredAddress,
      PlongjmpConst,
      PlongjmpImm,
      Plop3PredPredAddressImmPredImm,
      Plop3PredPredAddressImmRegImm,
      Plop3PredPredConstPredImm,
      Plop3PredPredConstRegImm,
      Plop3PredPredPredAddressImmPredImmImm,
      Plop3PredPredPredAddressImmRegImmImm,
      Plop3PredPredPredConstPredImmImm,
      Plop3PredPredPredConstRegImmImm,
      Plop3PredPredPredPred,
      Plop3PredPredPredPredImm,
      Plop3PredPredPredPredPredImmImm,
      Plop3PredPredPredRegPredImmImm,
      Plop3PredPredPredRegRegImmImm,
      Plop3PredPredRegAddressImmRegImmImm,
      Plop3PredPredRegConstRegImmImm,
      Plop3PredPredRegPredImm,
      Plop3PredPredRegRegImm,
      Plop3PredPredRegRegRegImmImm,
      Plop3PredRegAddressImmRegImm,
      Plop3PredRegConstRegImm,
      Plop3PredRegRegRegImm,
      PmtrigImm,
      PmtrigPredImm,
      PopcRegAddressImm,
      PopcRegConst,
      PopcRegImm,
      PopcRegReg,
      PopcRegRegConst,
      PopcRegRegImm,
      PopcRegRegReg,
      PreexitNoOperands,
      PretConst,
      PretImm,
      PrmtRegRegAddressImmReg,
      PrmtRegRegConstReg,
      PrmtRegRegImmReg,
      PrmtRegRegRegAddressImm,
      PrmtRegRegRegConst,
      PrmtRegRegRegImm,
      PrmtRegRegRegReg,
      PsetRegPredPred,
      PsetRegPredPredPred,
      PsetpPredPredPred,
      PsetpPredPredPredPredPred,
      Qadd4RegRegImm,
      Qadd4RegRegReg,
      Qfma4RegRegImmReg,
      Qfma4RegRegRegImm,
      Qfma4RegRegRegReg,
      QgmmaRegAddressReg,
      QgmmaRegAddressRegPred,
      QgmmaRegAddressRegPredReg,
      QgmmaRegAddressRegPredRegImm,
      QgmmaRegAddressRegReg,
      QgmmaRegAddressRegRegImm,
      QgmmaRegAddressRegRegPred,
      QgmmaRegAddressRegRegPredReg,
      QgmmaRegAddressRegRegPredRegImm,
      QgmmaRegAddressRegRegReg,
      QgmmaRegAddressRegRegRegImm,
      QgmmaRegRegAddressReg,
      QgmmaRegRegAddressRegPred,
      QgmmaRegRegAddressRegPredReg,
      QgmmaRegRegAddressRegPredRegImm,
      QgmmaRegRegAddressRegReg,
      QgmmaRegRegAddressRegRegImm,
      QmmaRegRegRegReg,
      QmmaRegRegRegRegPred,
      QmmaRegRegRegRegPredRegImm,
      QmmaRegRegRegRegPredRegRegReg,
      QmmaRegRegRegRegPredRegRegRegImm,
      QmmaRegRegRegRegRegImm,
      QmmaRegRegRegRegRegRegReg,
      QmmaRegRegRegRegRegRegRegImm,
      Qmul4RegRegImm,
      Qmul4RegRegReg,
      QspcPredAddress,
      QspcPredRegAddress,
      QspcRegAddress,
      R2bImmReg,
      R2pReg,
      R2pRegConst,
      R2pRegImm,
      R2pRegReg,
      R2pSymbolReg,
      R2pSymbolRegAddressImm,
      R2pSymbolRegConst,
      R2pSymbolRegImm,
      R2pSymbolRegReg,
      R2urPredRegReg,
      R2urRegReg,
      RamNoOperands,
      RedAddressReg,
      RedasAddressReg,
      RedgAddressReg,
      RedsAddressReg,
      ReduxRegReg,
      RetNoOperands,
      RetPredReg,
      RetPredRegImm,
      RetReg,
      RetRegImm,
      RpcmovAddressImm,
      RpcmovConst,
      RpcmovImm,
      RpcmovReg,
      RroRegConst,
      RroRegImm,
      RroRegReg,
      RttNoOperands,
      S2rRegSpecial,
      S2urRegSpecial,
      SamNoOperands,
      ScatterRegRegRegRegImmImm,
      SelRegRegAddressImmPred,
      SelRegRegConstPred,
      SelRegRegImmPred,
      SelRegRegRegPred
    )
  }
  object Definitions4 {
    val SetcrsptrReg: Definition = Definition("SetcrsptrReg", "SETCRSPTR", Vec("SETCRSPTR"))
    val SetctaidReg: Definition = Definition("SetctaidReg", "SETCTAID", Vec("setctaid_"))
    val SetlmembaseReg: Definition = Definition("SetlmembaseReg", "SETLMEMBASE", Vec("SETLMEMBASE", "setlmembase_"))
    val SgxtRegRegAddressImm: Definition = Definition("SgxtRegRegAddressImm", "SGXT", Vec("sgxt__RCxR_RCxR"))
    val SgxtRegRegConst: Definition = Definition("SgxtRegRegConst", "SGXT", Vec("sgxt__RCR_RCR"))
    val SgxtRegRegImm: Definition = Definition("SgxtRegRegImm", "SGXT", Vec("sgxt__RuIR_RIR"))
    val SgxtRegRegReg: Definition = Definition("SgxtRegRegReg", "SGXT", Vec("sgxt__RRR_RRR", "sgxt__RUR_RUR"))
    val ShfRegRegAddressImmReg: Definition = Definition("ShfRegRegAddressImmReg", "SHF", Vec("shf__RCxR_RCxR"))
    val ShfRegRegConstReg: Definition = Definition("ShfRegRegConstReg", "SHF", Vec("shf__RCR_RCR"))
    val ShfRegRegImmReg: Definition =
      Definition("ShfRegRegImmReg", "SHF", Vec("SHF_L imm", "SHF_L_imm", "SHF_R imm", "SHF_R_imm", "shf__RuIR_RIR"))
    val ShfRegRegRegAddressImm: Definition = Definition("ShfRegRegRegAddressImm", "SHF", Vec("shf__RRCx_RRCx"))
    val ShfRegRegRegConst: Definition = Definition("ShfRegRegRegConst", "SHF", Vec("shf__RRC_RRC"))
    val ShfRegRegRegImm: Definition = Definition("ShfRegRegRegImm", "SHF", Vec("shf__RRuI_RRI"))
    val ShfRegRegRegReg: Definition = Definition(
      "ShfRegRegRegReg",
      "SHF",
      Vec("SHF_L reg", "SHF_L_reg", "SHF_R reg", "SHF_R_reg", "shf__RRR_RRR", "shf__RRU_RRU", "shf__RUR_RUR")
    )
    val ShflPredRegRegImmImm: Definition =
      Definition("ShflPredRegRegImmImm", "SHFL", Vec("c b SHFL", "c_b_SHFL", "shfl__RII"))
    val ShflPredRegRegImmReg: Definition =
      Definition("ShflPredRegRegImmReg", "SHFL", Vec("b SHFL", "b_SHFL", "shfl__RIR"))
    val ShflPredRegRegRegImm: Definition =
      Definition("ShflPredRegRegRegImm", "SHFL", Vec("c SHFL", "c_SHFL", "shfl__RRI"))
    val ShflPredRegRegRegReg: Definition = Definition("ShflPredRegRegRegReg", "SHFL", Vec("SHFL", "shfl__RRR"))
    val ShintImmImm: Definition = Definition("ShintImmImm", "SHINT", Vec("SHINT"))
    val ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm: Definition =
      Definition("ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm", "SHINT", Vec("SHINT nibbles"))
    val ShlRegImmReg: Definition = Definition("ShlRegImmReg", "SHL", Vec("shl_imm_"))
    val ShlRegRegAddressImm: Definition = Definition("ShlRegRegAddressImm", "SHL", Vec("shl__RCxR_RCxR"))
    val ShlRegRegConst: Definition = Definition("ShlRegRegConst", "SHL", Vec("Const SHL", "Const_SHL", "shl__RCR_RCR"))
    val ShlRegRegImm: Definition = Definition("ShlRegRegImm", "SHL", Vec("Imm SHL", "Imm_SHL", "shl__RuIR_RIR"))
    val ShlRegRegReg: Definition = Definition("ShlRegRegReg", "SHL", Vec("SHL", "shl__RRR_RRR", "shl__RUR_RUR"))
    val ShrRegRegAddressImm: Definition = Definition("ShrRegRegAddressImm", "SHR", Vec("shr__RCxR_RCxR"))
    val ShrRegRegConst: Definition = Definition("ShrRegRegConst", "SHR", Vec("Const SHR", "Const_SHR", "shr__RCR_RCR"))
    val ShrRegRegImm: Definition = Definition("ShrRegRegImm", "SHR", Vec("Imm SHR", "Imm_SHR", "shr__RIR_RIR"))
    val ShrRegRegReg: Definition = Definition("ShrRegRegReg", "SHR", Vec("SHR", "shr__RRR_RRR", "shr__RUR_RUR"))
    val SpmetadataRegRegRegImmImmImm: Definition =
      Definition("SpmetadataRegRegRegImmImmImm", "SPMETADATA", Vec("spmetadata_"))
    val SsyConst: Definition = Definition("SsyConst", "SSY", Vec("SSY c", "SSY_c"))
    val SsyImm: Definition = Definition("SsyImm", "SSY", Vec("SSY"))
    val StAddressReg: Definition = Definition(
      "StAddressReg",
      "ST",
      Vec(
        "I ST",
        "I_ST",
        "ST",
        "st__sImmOffset",
        "st__uImmOffset",
        "st_memdesc__Ra64",
        "st_uniform__Ra32",
        "st_uniform__Ra64",
        "st_uniform__RaRZ",
        "st_weakonly__sImmOffset",
        "st_weakonly__uImmOffset",
        "st_weakonly_uniform__Ra32",
        "st_weakonly_uniform__Ra64",
        "st_weakonly_uniform__RaRZ"
      )
    )
    val StAddressRegPred: Definition = Definition("StAddressRegPred", "ST", Vec("I_ST", "ST"))
    val StasAddressReg: Definition =
      Definition("StasAddressReg", "STAS", Vec("stas_64__Ra64", "stas_64__RaRZ", "stas__Ra32"))
    val StgAddressReg: Definition = Definition(
      "StgAddressReg",
      "STG",
      Vec(
        "I_STG",
        "STG",
        "stg__sImmOffset",
        "stg__uImmOffset",
        "stg_memdesc__Ra64",
        "stg_uniform__Ra32",
        "stg_uniform__Ra64",
        "stg_uniform__RaRZ",
        "stg_weakonly__sImmOffset",
        "stg_weakonly__uImmOffset",
        "stg_weakonly_uniform__Ra32",
        "stg_weakonly_uniform__Ra64",
        "stg_weakonly_uniform__RaRZ"
      )
    )
    val StgAddressRegReg: Definition = Definition(
      "StgAddressRegReg",
      "STG",
      Vec("stg_256_memdesc__Ra64", "stg_256_uniform__Ra32", "stg_256_uniform__Ra64", "stg_256_uniform__RaRZ")
    )
    val StgAddressRegRegImm: Definition = Definition(
      "StgAddressRegRegImm",
      "STG",
      Vec("stg_256_memdesc__Ra64", "stg_256_uniform__Ra32", "stg_256_uniform__Ra64", "stg_256_uniform__RaRZ")
    )
    val StlAddressReg: Definition = Definition(
      "StlAddressReg",
      "STL",
      Vec("I STL", "I_STL", "STL", "stl__sImmOffset", "stl__uImmOffset", "stl_memdesc_", "stl_uniform_")
    )
    val StpImm: Definition = Definition("StpImm", "STP", Vec("STP"))
    val StsAddressReg: Definition = Definition(
      "StsAddressReg",
      "STS",
      Vec("I STS", "I_STS", "STS", "sts__sImmOffset", "sts__uImmOffset", "sts_uniform_")
    )
    val StsculPredAddressReg: Definition = Definition("StsculPredAddressReg", "STSCUL", Vec("I STSCUL", "STSCUL"))
    val StsmAddressReg: Definition = Definition("StsmAddressReg", "STSM", Vec("stsm__UR_sI_R", "stsm__sImmOffset"))
    val SttAddressReg: Definition = Definition("SttAddressReg", "STT", Vec("stt_"))
    val SttmAddressReg: Definition = Definition("SttmAddressReg", "STTM", Vec("sttm_"))
    val SuatomPredRegAddressRegImm: Definition =
      Definition("SuatomPredRegAddressRegImm", "SUATOM", Vec("SUATOM_D_CAS_IMM"))
    val SuatomPredRegAddressRegImmImm: Definition = Definition(
      "SuatomPredRegAddressRegImmImm",
      "SUATOM",
      Vec("suatom_cas_imm_", "suatom_cas_imm_weakonly_", "suatom_imm_", "suatom_imm_weakonly_")
    )
    val SuatomPredRegAddressRegImmImmImm: Definition = Definition(
      "SuatomPredRegAddressRegImmImmImm",
      "SUATOM",
      Vec("suatom_cas_tid_", "suatom_cas_tid_weakonly_", "suatom_tid_", "suatom_tid_weakonly_")
    )
    val SuatomPredRegAddressRegReg: Definition = Definition(
      "SuatomPredRegAddressRegReg",
      "SUATOM",
      Vec(
        "SUATOM_D_CAS_REG",
        "SUATOM_D_REG_SPARSE",
        "suatom_cas_reg_",
        "suatom_cas_reg_weakonly_",
        "suatom_cas_urc_",
        "suatom_reg_",
        "suatom_reg_weakonly_",
        "suatom_urc_"
      )
    )
    val SuatomPredRegAddressRegRegImm: Definition = Definition(
      "SuatomPredRegAddressRegRegImm",
      "SUATOM",
      Vec("suatom_cas_urc_", "suatom_cas_urc_weakonly_", "suatom_urc_", "suatom_urc_weakonly_")
    )
    val SuatomPredRegAddressRegRegReg: Definition = Definition(
      "SuatomPredRegAddressRegRegReg",
      "SUATOM",
      Vec("suatom_cas_reg_", "suatom_cas_urc_", "suatom_reg_", "suatom_urc_")
    )
    val SuatomPredRegAddressRegRegRegImm: Definition =
      Definition("SuatomPredRegAddressRegRegRegImm", "SUATOM", Vec("suatom_cas_urc_", "suatom_urc_"))
    val SuatomPredRegAddressRegRegRegReg: Definition =
      Definition("SuatomPredRegAddressRegRegRegReg", "SUATOM", Vec("suatom_cas_r_urc_", "suatom_r_urc_"))
    val SuatomRegAddressRegImm: Definition = Definition(
      "SuatomRegAddressRegImm",
      "SUATOM",
      Vec("SUATOM_D_CAS_IMM", "SUATOM_D_IMM", "SUATOM_P_CAS_IMM", "SUATOM_P_IMM")
    )
    val SuatomRegAddressRegImmImm: Definition = Definition(
      "SuatomRegAddressRegImmImm",
      "SUATOM",
      Vec("suatom_cas_imm_", "suatom_cas_imm_weakonly_", "suatom_imm_", "suatom_imm_weakonly_")
    )
    val SuatomRegAddressRegImmImmImm: Definition = Definition(
      "SuatomRegAddressRegImmImmImm",
      "SUATOM",
      Vec("suatom_cas_tid_", "suatom_cas_tid_weakonly_", "suatom_tid_", "suatom_tid_weakonly_")
    )
    val SuatomRegAddressRegReg: Definition = Definition(
      "SuatomRegAddressRegReg",
      "SUATOM",
      Vec(
        "SUATOM_D_CAS_REG",
        "SUATOM_D_REG",
        "SUATOM_P_CAS_REG",
        "SUATOM_P_REG",
        "suatom_cas_reg_",
        "suatom_cas_reg_weakonly_",
        "suatom_cas_urc_",
        "suatom_reg_",
        "suatom_reg_weakonly_",
        "suatom_urc_"
      )
    )
    val SuatomRegAddressRegRegImm: Definition = Definition(
      "SuatomRegAddressRegRegImm",
      "SUATOM",
      Vec("suatom_cas_urc_", "suatom_cas_urc_weakonly_", "suatom_urc_", "suatom_urc_weakonly_")
    )
    val SuatomRegAddressRegRegReg: Definition = Definition(
      "SuatomRegAddressRegRegReg",
      "SUATOM",
      Vec("suatom_cas_reg_", "suatom_cas_urc_", "suatom_reg_", "suatom_urc_")
    )
    val SuatomRegAddressRegRegRegImm: Definition =
      Definition("SuatomRegAddressRegRegRegImm", "SUATOM", Vec("suatom_cas_urc_", "suatom_urc_"))
    val SuatomRegAddressRegRegRegReg: Definition =
      Definition("SuatomRegAddressRegRegRegReg", "SUATOM", Vec("suatom_cas_r_urc_", "suatom_r_urc_"))
    val SubfmPredRegRegConstReg: Definition = Definition("SubfmPredRegRegConstReg", "SUBFM", Vec("Const SUBFM"))
    val SubfmPredRegRegImmReg: Definition = Definition("SubfmPredRegRegImmReg", "SUBFM", Vec("Imm SUBFM"))
    val SubfmPredRegRegRegConst: Definition = Definition("SubfmPredRegRegRegConst", "SUBFM", Vec("Const1 SUBFM"))
    val SubfmPredRegRegRegReg: Definition = Definition("SubfmPredRegRegRegReg", "SUBFM", Vec("SUBFM"))
    val SuclampPredRegRegConstImm: Definition = Definition("SuclampPredRegRegConstImm", "SUCLAMP", Vec("Const SUCLAMP"))
    val SuclampPredRegRegImmImm: Definition = Definition("SuclampPredRegRegImmImm", "SUCLAMP", Vec("Imm SUCLAMP"))
    val SuclampPredRegRegRegImm: Definition = Definition("SuclampPredRegRegRegImm", "SUCLAMP", Vec("SUCLAMP"))
    val SueauRegRegConstReg: Definition = Definition("SueauRegRegConstReg", "SUEAU", Vec("Const SUEAU"))
    val SueauRegRegImmReg: Definition = Definition("SueauRegRegImmReg", "SUEAU", Vec("Imm SUEAU"))
    val SueauRegRegRegConst: Definition = Definition("SueauRegRegRegConst", "SUEAU", Vec("Const1 SUEAU"))
    val SueauRegRegRegReg: Definition = Definition("SueauRegRegRegReg", "SUEAU", Vec("SUEAU"))
    val SuldPredRegAddressImm: Definition = Definition("SuldPredRegAddressImm", "SULD", Vec("SULD_D_IMM", "SULD_P_IMM"))
    val SuldPredRegAddressImmImm: Definition = Definition(
      "SuldPredRegAddressImmImm",
      "SULD",
      Vec("suld_d_imm_", "suld_d_imm_weakonly_", "suld_p_imm_", "suld_p_imm_weakonly_")
    )
    val SuldPredRegAddressImmImmImm: Definition = Definition(
      "SuldPredRegAddressImmImmImm",
      "SULD",
      Vec("suld_d_tid_", "suld_d_tid_weakonly_", "suld_p_tid_", "suld_p_tid_weakonly_")
    )
    val SuldPredRegAddressReg: Definition = Definition(
      "SuldPredRegAddressReg",
      "SULD",
      Vec(
        "SULD_D_REG",
        "SULD_P_REG",
        "suld_d_reg_",
        "suld_d_reg_weakonly_",
        "suld_d_urc_",
        "suld_p_reg_",
        "suld_p_reg_weakonly_",
        "suld_p_urc_"
      )
    )
    val SuldPredRegAddressRegImm: Definition = Definition(
      "SuldPredRegAddressRegImm",
      "SULD",
      Vec("suld_d_urc_", "suld_d_urc_weakonly_", "suld_p_urc_", "suld_p_urc_weakonly_")
    )
    val SuldPredRegAddressRegReg: Definition =
      Definition("SuldPredRegAddressRegReg", "SULD", Vec("suld_d_reg_", "suld_d_urc_", "suld_p_reg_", "suld_p_urc_"))
    val SuldPredRegAddressRegRegImm: Definition =
      Definition("SuldPredRegAddressRegRegImm", "SULD", Vec("suld_d_urc_", "suld_p_urc_"))
    val SuldPredRegAddressRegRegReg: Definition =
      Definition("SuldPredRegAddressRegRegReg", "SULD", Vec("suld_d_r_urc_", "suld_p_r_urc_"))
    val SuldRegAddressImm: Definition = Definition("SuldRegAddressImm", "SULD", Vec("SULD_D_IMM", "SULD_P_IMM"))
    val SuldRegAddressImmImm: Definition = Definition(
      "SuldRegAddressImmImm",
      "SULD",
      Vec("suld_d_imm_", "suld_d_imm_weakonly_", "suld_p_imm_", "suld_p_imm_weakonly_")
    )
    val SuldRegAddressImmImmImm: Definition = Definition(
      "SuldRegAddressImmImmImm",
      "SULD",
      Vec("suld_d_tid_", "suld_d_tid_weakonly_", "suld_p_tid_", "suld_p_tid_weakonly_")
    )
    val SuldRegAddressReg: Definition = Definition(
      "SuldRegAddressReg",
      "SULD",
      Vec(
        "SULD_D_REG",
        "SULD_P_REG",
        "suld_d_reg_",
        "suld_d_reg_weakonly_",
        "suld_d_urc_",
        "suld_p_reg_",
        "suld_p_reg_weakonly_",
        "suld_p_urc_"
      )
    )
    val SuldRegAddressRegImm: Definition = Definition(
      "SuldRegAddressRegImm",
      "SULD",
      Vec("suld_d_urc_", "suld_d_urc_weakonly_", "suld_p_urc_", "suld_p_urc_weakonly_")
    )
    val SuldRegAddressRegReg: Definition =
      Definition("SuldRegAddressRegReg", "SULD", Vec("suld_d_reg_", "suld_d_urc_", "suld_p_reg_", "suld_p_urc_"))
    val SuldRegAddressRegRegImm: Definition =
      Definition("SuldRegAddressRegRegImm", "SULD", Vec("suld_d_urc_", "suld_p_urc_"))
    val SuldRegAddressRegRegReg: Definition =
      Definition("SuldRegAddressRegRegReg", "SULD", Vec("suld_d_r_urc_", "suld_p_r_urc_"))
    val SuldgaRegAddressConstPred: Definition = Definition("SuldgaRegAddressConstPred", "SULDGA", Vec("SULDGA.B cimm"))
    val SuldgaRegAddressRegPred: Definition = Definition("SuldgaRegAddressRegPred", "SULDGA", Vec("SULDGA.B"))
    val SuqueryPredRegAddressImmImm: Definition =
      Definition("SuqueryPredRegAddressImmImm", "SUQUERY", Vec("suquery_imm_"))
    val SuqueryPredRegAddressImmImmImm: Definition =
      Definition("SuqueryPredRegAddressImmImmImm", "SUQUERY", Vec("suquery_tid_"))
    val SuqueryPredRegAddressReg: Definition =
      Definition("SuqueryPredRegAddressReg", "SUQUERY", Vec("suquery_reg_", "suquery_urc_"))
    val SuqueryPredRegAddressRegImm: Definition =
      Definition("SuqueryPredRegAddressRegImm", "SUQUERY", Vec("suquery_urc_"))
    val SuqueryPredRegAddressRegReg: Definition =
      Definition("SuqueryPredRegAddressRegReg", "SUQUERY", Vec("suquery_reg_", "suquery_urc_"))
    val SuqueryPredRegAddressRegRegImm: Definition =
      Definition("SuqueryPredRegAddressRegRegImm", "SUQUERY", Vec("suquery_urc_"))
    val SuqueryPredRegAddressRegRegReg: Definition =
      Definition("SuqueryPredRegAddressRegRegReg", "SUQUERY", Vec("suquery_r_urc_"))
    val SuqueryRegAddressImmImm: Definition = Definition("SuqueryRegAddressImmImm", "SUQUERY", Vec("suquery_imm_"))
    val SuqueryRegAddressImmImmImm: Definition =
      Definition("SuqueryRegAddressImmImmImm", "SUQUERY", Vec("suquery_tid_"))
    val SuqueryRegAddressReg: Definition =
      Definition("SuqueryRegAddressReg", "SUQUERY", Vec("suquery_reg_", "suquery_urc_"))
    val SuqueryRegAddressRegImm: Definition = Definition("SuqueryRegAddressRegImm", "SUQUERY", Vec("suquery_urc_"))
    val SuqueryRegAddressRegReg: Definition =
      Definition("SuqueryRegAddressRegReg", "SUQUERY", Vec("suquery_reg_", "suquery_urc_"))
    val SuqueryRegAddressRegRegImm: Definition =
      Definition("SuqueryRegAddressRegRegImm", "SUQUERY", Vec("suquery_urc_"))
    val SuqueryRegAddressRegRegReg: Definition =
      Definition("SuqueryRegAddressRegRegReg", "SUQUERY", Vec("suquery_r_urc_"))
    val SuredAddressRegImm: Definition = Definition("SuredAddressRegImm", "SURED", Vec("SURED_D_IMM", "SURED_P_IMM"))
    val SuredAddressRegImmImm: Definition =
      Definition("SuredAddressRegImmImm", "SURED", Vec("sured_imm_", "sured_imm_weakonly_"))
    val SuredAddressRegImmImmImm: Definition =
      Definition("SuredAddressRegImmImmImm", "SURED", Vec("sured_tid_", "sured_tid_weakonly_"))
    val SuredAddressRegReg: Definition = Definition(
      "SuredAddressRegReg",
      "SURED",
      Vec("SURED_D_REG", "SURED_P_REG", "sured_reg_", "sured_reg_weakonly_", "sured_urc_")
    )
    val SuredAddressRegRegImm: Definition =
      Definition("SuredAddressRegRegImm", "SURED", Vec("sured_urc_", "sured_urc_weakonly_"))
    val SuredAddressRegRegReg: Definition =
      Definition("SuredAddressRegRegReg", "SURED", Vec("sured_reg_", "sured_urc_"))
    val SuredAddressRegRegRegImm: Definition = Definition("SuredAddressRegRegRegImm", "SURED", Vec("sured_urc_"))
    val SuredAddressRegRegRegReg: Definition = Definition("SuredAddressRegRegRegReg", "SURED", Vec("sured_r_urc_"))
    val SustAddressRegImm: Definition = Definition("SustAddressRegImm", "SUST", Vec("SUST_D_IMM", "SUST_P_IMM"))
    val SustAddressRegImmImm: Definition = Definition(
      "SustAddressRegImmImm",
      "SUST",
      Vec("sust_d_imm_", "sust_d_imm_weakonly_", "sust_p_imm_", "sust_p_imm_weakonly_")
    )
    val SustAddressRegImmImmImm: Definition = Definition(
      "SustAddressRegImmImmImm",
      "SUST",
      Vec("sust_d_tid_", "sust_d_tid_weakonly_", "sust_p_tid_", "sust_p_tid_weakonly_")
    )
    val SustAddressRegReg: Definition = Definition(
      "SustAddressRegReg",
      "SUST",
      Vec(
        "SUST_D_REG",
        "SUST_P_REG",
        "sust_d_reg_",
        "sust_d_reg_weakonly_",
        "sust_d_urc_",
        "sust_p_reg_",
        "sust_p_reg_weakonly_",
        "sust_p_urc_"
      )
    )
    val SustAddressRegRegImm: Definition = Definition(
      "SustAddressRegRegImm",
      "SUST",
      Vec("sust_d_urc_", "sust_d_urc_weakonly_", "sust_p_urc_", "sust_p_urc_weakonly_")
    )
    val SustAddressRegRegReg: Definition =
      Definition("SustAddressRegRegReg", "SUST", Vec("sust_d_reg_", "sust_d_urc_", "sust_p_reg_", "sust_p_urc_"))
    val SustAddressRegRegRegImm: Definition =
      Definition("SustAddressRegRegRegImm", "SUST", Vec("sust_d_urc_", "sust_p_urc_"))
    val SustAddressRegRegRegReg: Definition =
      Definition("SustAddressRegRegRegReg", "SUST", Vec("sust_d_r_urc_", "sust_p_r_urc_"))
    val SustgaAddressConstRegPred: Definition =
      Definition("SustgaAddressConstRegPred", "SUSTGA", Vec("SUSTGA.B cimm", "SUSTGA.P cimm"))
    val SustgaAddressRegRegPred: Definition =
      Definition("SustgaAddressRegRegPred", "SUSTGA", Vec("SUSTGA.B", "SUSTGA.P"))
    val SyncNoOperands: Definition = Definition("SyncNoOperands", "SYNC", Vec("SYNC"))
    val SyncsNoOperands: Definition = Definition("SyncsNoOperands", "SYNCS", Vec("syncs_cctl_all_", "syncs_flush_"))
    val SyncsAddress: Definition = Definition("SyncsAddress", "SYNCS", Vec("syncs_cctl_"))
    val SyncsAddressReg: Definition = Definition("SyncsAddressReg", "SYNCS", Vec("syncs_tcnt_"))
    val SyncsPredAddressReg: Definition = Definition("SyncsPredAddressReg", "SYNCS", Vec("syncs_phasechk_"))
    val SyncsRegAddress: Definition = Definition("SyncsRegAddress", "SYNCS", Vec("syncs_ld_", "syncs_uniform_ld_"))
    val SyncsRegAddressReg: Definition =
      Definition("SyncsRegAddressReg", "SYNCS", Vec("syncs_arrive_", "syncs_uniform_exch_"))
    val SyncsRegAddressRegReg: Definition = Definition("SyncsRegAddressRegReg", "SYNCS", Vec("syncs_uniform_cas_"))
    val TexPredRegRegRegImm: Definition = Definition("TexPredRegRegRegImm", "TEX", Vec("TEX", "TEX_B"))
    val TexPredRegRegRegImmImm: Definition =
      Definition("TexPredRegRegRegImmImm", "TEX", Vec("TEX", "TEX_B", "TEX_B_legacy_style", "TEX_legacy_style"))
    val TexPredRegRegRegImmImmImm: Definition =
      Definition("TexPredRegRegRegImmImmImm", "TEX", Vec("TEX_B_legacy_style", "TEX_legacy_style"))
    val TexPredRegRegRegReg: Definition =
      Definition("TexPredRegRegRegReg", "TEX", Vec("tex_b_noConst_", "tex_scr_b_noConst_"))
    val TexPredRegRegRegRegImm: Definition =
      Definition("TexPredRegRegRegRegImm", "TEX", Vec("tex_b_noConst_", "tex_scr_b_noConst_"))
    val TexPredRegRegRegRegImmImm: Definition =
      Definition("TexPredRegRegRegRegImmImm", "TEX", Vec("tex_", "tex_b_", "tex_scr_", "tex_scr_b_"))
    val TexPredRegRegRegRegImmImmImm: Definition = Definition(
      "TexPredRegRegRegRegImmImmImm",
      "TEX",
      Vec("tex_", "tex_b_", "tex_b_tid_", "tex_scr_", "tex_scr_b_", "tex_scr_b_tid_")
    )
    val TexPredRegRegRegRegImmImmImmImm: Definition =
      Definition("TexPredRegRegRegRegImmImmImmImm", "TEX", Vec("tex_b_tid_", "tex_scr_b_tid_"))
    val TexPredRegRegRegRegReg: Definition = Definition(
      "TexPredRegRegRegRegReg",
      "TEX",
      Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
    )
    val TexPredRegRegRegRegRegImm: Definition = Definition(
      "TexPredRegRegRegRegRegImm",
      "TEX",
      Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
    )
    val TexPredRegRegRegRegRegImmImm: Definition =
      Definition("TexPredRegRegRegRegRegImmImm", "TEX", Vec("tex_scr_urc_", "tex_urc_"))
    val TexPredRegRegRegRegRegReg: Definition =
      Definition("TexPredRegRegRegRegRegReg", "TEX", Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_"))
    val TexPredRegRegRegRegRegRegImm: Definition =
      Definition("TexPredRegRegRegRegRegRegImm", "TEX", Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_"))
    val TexPredRegRegRegRegRegRegImmImm: Definition =
      Definition("TexPredRegRegRegRegRegRegImmImm", "TEX", Vec("tex_scr_urc_", "tex_urc_"))
    val TexRegRegRegImm: Definition = Definition("TexRegRegRegImm", "TEX", Vec("TEX", "TEX_B"))
    val TexRegRegRegImmImm: Definition =
      Definition("TexRegRegRegImmImm", "TEX", Vec("TEX", "TEX B", "TEX_B", "TEX_B_legacy_style", "TEX_legacy_style"))
    val TexRegRegRegImmImmImm: Definition = Definition(
      "TexRegRegRegImmImmImm",
      "TEX",
      Vec("TEX B legacy style", "TEX legacy style", "TEX_B_legacy_style", "TEX_legacy_style")
    )
    val TexRegRegRegReg: Definition = Definition("TexRegRegRegReg", "TEX", Vec("tex_b_noConst_", "tex_scr_b_noConst_"))
    val TexRegRegRegRegImm: Definition =
      Definition("TexRegRegRegRegImm", "TEX", Vec("tex_b_noConst_", "tex_scr_b_noConst_"))
    val TexRegRegRegRegImmImm: Definition =
      Definition("TexRegRegRegRegImmImm", "TEX", Vec("tex_", "tex_b_", "tex_scr_", "tex_scr_b_"))
    val TexRegRegRegRegImmImmImm: Definition = Definition(
      "TexRegRegRegRegImmImmImm",
      "TEX",
      Vec("tex_", "tex_b_", "tex_b_tid_", "tex_scr_", "tex_scr_b_", "tex_scr_b_tid_")
    )
    val TexRegRegRegRegImmImmImmImm: Definition =
      Definition("TexRegRegRegRegImmImmImmImm", "TEX", Vec("tex_b_tid_", "tex_scr_b_tid_"))
    val TexRegRegRegRegReg: Definition =
      Definition("TexRegRegRegRegReg", "TEX", Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_"))
    val TexRegRegRegRegRegImm: Definition = Definition(
      "TexRegRegRegRegRegImm",
      "TEX",
      Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
    )
    val TexRegRegRegRegRegImmImm: Definition =
      Definition("TexRegRegRegRegRegImmImm", "TEX", Vec("tex_scr_urc_", "tex_urc_"))
    val TexRegRegRegRegRegReg: Definition =
      Definition("TexRegRegRegRegRegReg", "TEX", Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_"))
    val TexRegRegRegRegRegRegImm: Definition =
      Definition("TexRegRegRegRegRegRegImm", "TEX", Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_"))
    val TexRegRegRegRegRegRegImmImm: Definition =
      Definition("TexRegRegRegRegRegRegImmImm", "TEX", Vec("tex_scr_urc_", "tex_urc_"))
    val TexdepbarImm: Definition = Definition("TexdepbarImm", "TEXDEPBAR", Vec("TEXDEPBAR"))
    val TexsRegRegRegRegImm: Definition =
      Definition("TexsRegRegRegRegImm", "TEXS", Vec("TEXS", "TEXS_F16", "TEXS_F16_RZ", "TEXS_RZ"))
    val TldPredRegRegRegImm: Definition = Definition("TldPredRegRegRegImm", "TLD", Vec("TLD", "TLD_B"))
    val TldPredRegRegRegImmImm: Definition = Definition("TldPredRegRegRegImmImm", "TLD", Vec("TLD", "TLD_B"))
    val TldPredRegRegRegReg: Definition =
      Definition("TldPredRegRegRegReg", "TLD", Vec("tld_b_noConst_", "tld_scr_b_noConst_"))
    val TldPredRegRegRegRegImm: Definition =
      Definition("TldPredRegRegRegRegImm", "TLD", Vec("tld_b_noConst_", "tld_scr_b_noConst_"))
    val TldPredRegRegRegRegImmImm: Definition =
      Definition("TldPredRegRegRegRegImmImm", "TLD", Vec("tld_", "tld_b_", "tld_scr_", "tld_scr_b_"))
    val TldPredRegRegRegRegImmImmImm: Definition = Definition(
      "TldPredRegRegRegRegImmImmImm",
      "TLD",
      Vec("tld_", "tld_b_", "tld_b_tid_", "tld_scr_", "tld_scr_b_", "tld_scr_b_tid_")
    )
    val TldPredRegRegRegRegImmImmImmImm: Definition =
      Definition("TldPredRegRegRegRegImmImmImmImm", "TLD", Vec("tld_b_tid_", "tld_scr_b_tid_"))
    val TldPredRegRegRegRegReg: Definition = Definition(
      "TldPredRegRegRegRegReg",
      "TLD",
      Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_")
    )
    val TldPredRegRegRegRegRegImm: Definition = Definition(
      "TldPredRegRegRegRegRegImm",
      "TLD",
      Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_")
    )
    val TldPredRegRegRegRegRegImmImm: Definition =
      Definition("TldPredRegRegRegRegRegImmImm", "TLD", Vec("tld_scr_urc_", "tld_urc_"))
    val TldPredRegRegRegRegRegReg: Definition =
      Definition("TldPredRegRegRegRegRegReg", "TLD", Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_"))
    val TldPredRegRegRegRegRegRegImm: Definition =
      Definition("TldPredRegRegRegRegRegRegImm", "TLD", Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_"))
    val TldPredRegRegRegRegRegRegImmImm: Definition =
      Definition("TldPredRegRegRegRegRegRegImmImm", "TLD", Vec("tld_scr_urc_", "tld_urc_"))
    val TldRegRegRegImm: Definition = Definition("TldRegRegRegImm", "TLD", Vec("TLD", "TLD_B"))
    val TldRegRegRegImmImm: Definition = Definition("TldRegRegRegImmImm", "TLD", Vec("TLD", "TLD B", "TLD_B"))
    val TldRegRegRegReg: Definition = Definition("TldRegRegRegReg", "TLD", Vec("tld_b_noConst_", "tld_scr_b_noConst_"))
    val TldRegRegRegRegImm: Definition =
      Definition("TldRegRegRegRegImm", "TLD", Vec("tld_b_noConst_", "tld_scr_b_noConst_"))
    val TldRegRegRegRegImmImm: Definition =
      Definition("TldRegRegRegRegImmImm", "TLD", Vec("tld_", "tld_b_", "tld_scr_", "tld_scr_b_"))
    val TldRegRegRegRegImmImmImm: Definition = Definition(
      "TldRegRegRegRegImmImmImm",
      "TLD",
      Vec("tld_", "tld_b_", "tld_b_tid_", "tld_scr_", "tld_scr_b_", "tld_scr_b_tid_")
    )
    val TldRegRegRegRegImmImmImmImm: Definition =
      Definition("TldRegRegRegRegImmImmImmImm", "TLD", Vec("tld_b_tid_", "tld_scr_b_tid_"))
    val TldRegRegRegRegReg: Definition =
      Definition("TldRegRegRegRegReg", "TLD", Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_"))
    val TldRegRegRegRegRegImm: Definition = Definition(
      "TldRegRegRegRegRegImm",
      "TLD",
      Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_")
    )
    val TldRegRegRegRegRegImmImm: Definition =
      Definition("TldRegRegRegRegRegImmImm", "TLD", Vec("tld_scr_urc_", "tld_urc_"))
    val TldRegRegRegRegRegReg: Definition =
      Definition("TldRegRegRegRegRegReg", "TLD", Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_"))
    val TldRegRegRegRegRegRegImm: Definition =
      Definition("TldRegRegRegRegRegRegImm", "TLD", Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_"))
    val TldRegRegRegRegRegRegImmImm: Definition =
      Definition("TldRegRegRegRegRegRegImmImm", "TLD", Vec("tld_scr_urc_", "tld_urc_"))
    val Tld4PredRegRegRegImm: Definition = Definition("Tld4PredRegRegRegImm", "TLD4", Vec("TLD4", "TLD4_B"))
    val Tld4PredRegRegRegImmImm: Definition =
      Definition("Tld4PredRegRegRegImmImm", "TLD4", Vec("TLD4", "TLD4_B", "TLD4_B_Legacy_Style", "TLD4_Legacy_Style"))
    val Tld4PredRegRegRegImmImmImm: Definition =
      Definition("Tld4PredRegRegRegImmImmImm", "TLD4", Vec("TLD4_B_Legacy_Style", "TLD4_Legacy_Style"))
    val Tld4PredRegRegRegReg: Definition =
      Definition("Tld4PredRegRegRegReg", "TLD4", Vec("tld4_b_noConst_", "tld4_scr_b_noConst_"))
    val Tld4PredRegRegRegRegImm: Definition =
      Definition("Tld4PredRegRegRegRegImm", "TLD4", Vec("tld4_b_noConst_", "tld4_scr_b_noConst_"))
    val Tld4PredRegRegRegRegImmImm: Definition =
      Definition("Tld4PredRegRegRegRegImmImm", "TLD4", Vec("tld4_", "tld4_b_", "tld4_scr_", "tld4_scr_b_"))
    val Tld4PredRegRegRegRegImmImmImm: Definition = Definition(
      "Tld4PredRegRegRegRegImmImmImm",
      "TLD4",
      Vec("tld4_", "tld4_b_", "tld4_b_tid_", "tld4_scr_", "tld4_scr_b_", "tld4_scr_b_tid_")
    )
    val Tld4PredRegRegRegRegImmImmImmImm: Definition =
      Definition("Tld4PredRegRegRegRegImmImmImmImm", "TLD4", Vec("tld4_b_tid_", "tld4_scr_b_tid_"))
    val Tld4PredRegRegRegRegReg: Definition = Definition(
      "Tld4PredRegRegRegRegReg",
      "TLD4",
      Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
    )
    val Tld4PredRegRegRegRegRegImm: Definition = Definition(
      "Tld4PredRegRegRegRegRegImm",
      "TLD4",
      Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
    )
    val Tld4PredRegRegRegRegRegImmImm: Definition =
      Definition("Tld4PredRegRegRegRegRegImmImm", "TLD4", Vec("tld4_scr_urc_", "tld4_urc_"))
    val Tld4PredRegRegRegRegRegReg: Definition = Definition(
      "Tld4PredRegRegRegRegRegReg",
      "TLD4",
      Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
    )
    val Tld4PredRegRegRegRegRegRegImm: Definition = Definition(
      "Tld4PredRegRegRegRegRegRegImm",
      "TLD4",
      Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
    )
    val Tld4PredRegRegRegRegRegRegImmImm: Definition =
      Definition("Tld4PredRegRegRegRegRegRegImmImm", "TLD4", Vec("tld4_scr_urc_", "tld4_urc_"))
    val Tld4RegRegRegImm: Definition = Definition("Tld4RegRegRegImm", "TLD4", Vec("TLD4", "TLD4_B"))
    val Tld4RegRegRegImmImm: Definition = Definition(
      "Tld4RegRegRegImmImm",
      "TLD4",
      Vec("TLD4", "TLD4 B", "TLD4_B", "TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
    )
    val Tld4RegRegRegImmImmImm: Definition = Definition(
      "Tld4RegRegRegImmImmImm",
      "TLD4",
      Vec("TLD4 B Legacy Style", "TLD4 Legacy Style", "TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
    )
    val Tld4RegRegRegReg: Definition =
      Definition("Tld4RegRegRegReg", "TLD4", Vec("tld4_b_noConst_", "tld4_scr_b_noConst_"))
    val Tld4RegRegRegRegImm: Definition =
      Definition("Tld4RegRegRegRegImm", "TLD4", Vec("tld4_b_noConst_", "tld4_scr_b_noConst_"))
    val Tld4RegRegRegRegImmImm: Definition =
      Definition("Tld4RegRegRegRegImmImm", "TLD4", Vec("tld4_", "tld4_b_", "tld4_scr_", "tld4_scr_b_"))
    val Tld4RegRegRegRegImmImmImm: Definition = Definition(
      "Tld4RegRegRegRegImmImmImm",
      "TLD4",
      Vec("tld4_", "tld4_b_", "tld4_b_tid_", "tld4_scr_", "tld4_scr_b_", "tld4_scr_b_tid_")
    )
    val Tld4RegRegRegRegImmImmImmImm: Definition =
      Definition("Tld4RegRegRegRegImmImmImmImm", "TLD4", Vec("tld4_b_tid_", "tld4_scr_b_tid_"))
    val Tld4RegRegRegRegReg: Definition = Definition(
      "Tld4RegRegRegRegReg",
      "TLD4",
      Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
    )
    val Tld4RegRegRegRegRegImm: Definition = Definition(
      "Tld4RegRegRegRegRegImm",
      "TLD4",
      Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
    )
    val Tld4RegRegRegRegRegImmImm: Definition =
      Definition("Tld4RegRegRegRegRegImmImm", "TLD4", Vec("tld4_scr_urc_", "tld4_urc_"))
    val Tld4RegRegRegRegRegReg: Definition =
      Definition("Tld4RegRegRegRegRegReg", "TLD4", Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_"))
    val Tld4RegRegRegRegRegRegImm: Definition = Definition(
      "Tld4RegRegRegRegRegRegImm",
      "TLD4",
      Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
    )
    val Tld4RegRegRegRegRegRegImmImm: Definition =
      Definition("Tld4RegRegRegRegRegRegImmImm", "TLD4", Vec("tld4_scr_urc_", "tld4_urc_"))
    val Tld4sRegRegRegRegImm: Definition = Definition("Tld4sRegRegRegRegImm", "TLD4S", Vec("TLD4S", "TLD4S_F16"))
    val TldsRegRegRegRegImm: Definition =
      Definition("TldsRegRegRegRegImm", "TLDS", Vec("TLDS", "TLDS_F16", "TLDS_F16_RZ", "TLDS_RZ"))
    val TmmlRegRegImm: Definition = Definition("TmmlRegRegImm", "TMML", Vec("TMML"))
    val TmmlRegRegImmImm: Definition = Definition("TmmlRegRegImmImm", "TMML", Vec("TMML", "TMML_Legacy_Style"))
    val TmmlRegRegImmImmImm: Definition =
      Definition("TmmlRegRegImmImmImm", "TMML", Vec("TMML Legacy Style", "TMML_Legacy_Style"))
    val TmmlRegRegRegImm: Definition = Definition("TmmlRegRegRegImm", "TMML", Vec("TMML", "TMML_B"))
    val TmmlRegRegRegImmImm: Definition = Definition(
      "TmmlRegRegRegImmImm",
      "TMML",
      Vec("TMML", "TMML.B", "TMML_B", "TMML_B_Legacy_Style", "TMML_Legacy_Style")
    )
    val TmmlRegRegRegImmImmImm: Definition = Definition(
      "TmmlRegRegRegImmImmImm",
      "TMML",
      Vec("TMML Legacy Style", "TMML.B Legacy Style", "TMML_B_Legacy_Style", "TMML_Legacy_Style")
    )
    val TmmlRegRegRegReg: Definition = Definition("TmmlRegRegRegReg", "TMML", Vec("tmml_b_noConst_"))
    val TmmlRegRegRegRegImm: Definition = Definition("TmmlRegRegRegRegImm", "TMML", Vec("tmml_b_noConst_"))
    val TmmlRegRegRegRegImmImm: Definition = Definition("TmmlRegRegRegRegImmImm", "TMML", Vec("tmml_", "tmml_b_"))
    val TmmlRegRegRegRegImmImmImm: Definition =
      Definition("TmmlRegRegRegRegImmImmImm", "TMML", Vec("tmml_", "tmml_b_", "tmml_b_tid_"))
    val TmmlRegRegRegRegImmImmImmImm: Definition =
      Definition("TmmlRegRegRegRegImmImmImmImm", "TMML", Vec("tmml_b_tid_"))
    val TmmlRegRegRegRegReg: Definition = Definition("TmmlRegRegRegRegReg", "TMML", Vec("tmml_b_urc_", "tmml_urc_"))
    val TmmlRegRegRegRegRegImm: Definition =
      Definition("TmmlRegRegRegRegRegImm", "TMML", Vec("tmml_b_urc_", "tmml_urc_"))
    val TmmlRegRegRegRegRegImmImm: Definition = Definition("TmmlRegRegRegRegRegImmImm", "TMML", Vec("tmml_urc_"))
    val TxaRegRegImm: Definition = Definition("TxaRegRegImm", "TXA", Vec("TXA"))
    val TxaRegRegImmImm: Definition = Definition("TxaRegRegImmImm", "TXA", Vec("TXA"))
    val TxaRegRegImmImmImm: Definition =
      Definition("TxaRegRegImmImmImm", "TXA", Vec("TXA Legacy Style", "TXA_Legacy_Style"))
    val TxdPredRegRegRegImm: Definition = Definition("TxdPredRegRegRegImm", "TXD", Vec("TXD", "TXD.B"))
    val TxdPredRegRegRegImmImm: Definition =
      Definition("TxdPredRegRegRegImmImm", "TXD", Vec("TXD", "TXD.B", "TXD_B_Legacy_Style", "TXD_Legacy_Style"))
    val TxdPredRegRegRegImmImmImm: Definition =
      Definition("TxdPredRegRegRegImmImmImm", "TXD", Vec("TXD_B_Legacy_Style", "TXD_Legacy_Style"))
    val TxdPredRegRegRegReg: Definition = Definition("TxdPredRegRegRegReg", "TXD", Vec("txd_b_noConst_"))
    val TxdPredRegRegRegRegImm: Definition = Definition("TxdPredRegRegRegRegImm", "TXD", Vec("txd_b_noConst_"))
    val TxdPredRegRegRegRegImmImm: Definition = Definition("TxdPredRegRegRegRegImmImm", "TXD", Vec("txd_", "txd_b_"))
    val TxdPredRegRegRegRegImmImmImm: Definition =
      Definition("TxdPredRegRegRegRegImmImmImm", "TXD", Vec("txd_", "txd_b_", "txd_b_tid_"))
    val TxdPredRegRegRegRegImmImmImmImm: Definition =
      Definition("TxdPredRegRegRegRegImmImmImmImm", "TXD", Vec("txd_b_tid_"))
    val TxdPredRegRegRegRegReg: Definition =
      Definition("TxdPredRegRegRegRegReg", "TXD", Vec("txd_b_noConst_", "txd_urc_"))
    val TxdPredRegRegRegRegRegImm: Definition =
      Definition("TxdPredRegRegRegRegRegImm", "TXD", Vec("txd_b_noConst_", "txd_urc_"))
    val TxdPredRegRegRegRegRegImmImm: Definition = Definition("TxdPredRegRegRegRegRegImmImm", "TXD", Vec("txd_urc_"))
    val TxdPredRegRegRegRegRegReg: Definition =
      Definition("TxdPredRegRegRegRegRegReg", "TXD", Vec("txd_b_urc_", "txd_urc_"))
    val TxdPredRegRegRegRegRegRegImm: Definition =
      Definition("TxdPredRegRegRegRegRegRegImm", "TXD", Vec("txd_b_urc_", "txd_urc_"))
    val TxdPredRegRegRegRegRegRegImmImm: Definition =
      Definition("TxdPredRegRegRegRegRegRegImmImm", "TXD", Vec("txd_urc_"))
    val TxdRegRegRegImm: Definition = Definition("TxdRegRegRegImm", "TXD", Vec("TXD", "TXD.B"))
    val TxdRegRegRegImmImm: Definition =
      Definition("TxdRegRegRegImmImm", "TXD", Vec("TXD", "TXD.B", "TXD_B_Legacy_Style", "TXD_Legacy_Style"))
    val TxdRegRegRegImmImmImm: Definition = Definition(
      "TxdRegRegRegImmImmImm",
      "TXD",
      Vec("TXD Legacy Style", "TXD.B Legacy Style", "TXD_B_Legacy_Style", "TXD_Legacy_Style")
    )
    val TxdRegRegRegReg: Definition = Definition("TxdRegRegRegReg", "TXD", Vec("txd_b_noConst_"))
    val TxdRegRegRegRegImm: Definition = Definition("TxdRegRegRegRegImm", "TXD", Vec("txd_b_noConst_"))
    val TxdRegRegRegRegImmImm: Definition = Definition("TxdRegRegRegRegImmImm", "TXD", Vec("txd_", "txd_b_"))
    val TxdRegRegRegRegImmImmImm: Definition =
      Definition("TxdRegRegRegRegImmImmImm", "TXD", Vec("txd_", "txd_b_", "txd_b_tid_"))
    val TxdRegRegRegRegImmImmImmImm: Definition = Definition("TxdRegRegRegRegImmImmImmImm", "TXD", Vec("txd_b_tid_"))
    val TxdRegRegRegRegReg: Definition = Definition("TxdRegRegRegRegReg", "TXD", Vec("txd_b_noConst_", "txd_urc_"))
    val definitions: Vec[Definition] = Vec(
      SetcrsptrReg,
      SetctaidReg,
      SetlmembaseReg,
      SgxtRegRegAddressImm,
      SgxtRegRegConst,
      SgxtRegRegImm,
      SgxtRegRegReg,
      ShfRegRegAddressImmReg,
      ShfRegRegConstReg,
      ShfRegRegImmReg,
      ShfRegRegRegAddressImm,
      ShfRegRegRegConst,
      ShfRegRegRegImm,
      ShfRegRegRegReg,
      ShflPredRegRegImmImm,
      ShflPredRegRegImmReg,
      ShflPredRegRegRegImm,
      ShflPredRegRegRegReg,
      ShintImmImm,
      ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm,
      ShlRegImmReg,
      ShlRegRegAddressImm,
      ShlRegRegConst,
      ShlRegRegImm,
      ShlRegRegReg,
      ShrRegRegAddressImm,
      ShrRegRegConst,
      ShrRegRegImm,
      ShrRegRegReg,
      SpmetadataRegRegRegImmImmImm,
      SsyConst,
      SsyImm,
      StAddressReg,
      StAddressRegPred,
      StasAddressReg,
      StgAddressReg,
      StgAddressRegReg,
      StgAddressRegRegImm,
      StlAddressReg,
      StpImm,
      StsAddressReg,
      StsculPredAddressReg,
      StsmAddressReg,
      SttAddressReg,
      SttmAddressReg,
      SuatomPredRegAddressRegImm,
      SuatomPredRegAddressRegImmImm,
      SuatomPredRegAddressRegImmImmImm,
      SuatomPredRegAddressRegReg,
      SuatomPredRegAddressRegRegImm,
      SuatomPredRegAddressRegRegReg,
      SuatomPredRegAddressRegRegRegImm,
      SuatomPredRegAddressRegRegRegReg,
      SuatomRegAddressRegImm,
      SuatomRegAddressRegImmImm,
      SuatomRegAddressRegImmImmImm,
      SuatomRegAddressRegReg,
      SuatomRegAddressRegRegImm,
      SuatomRegAddressRegRegReg,
      SuatomRegAddressRegRegRegImm,
      SuatomRegAddressRegRegRegReg,
      SubfmPredRegRegConstReg,
      SubfmPredRegRegImmReg,
      SubfmPredRegRegRegConst,
      SubfmPredRegRegRegReg,
      SuclampPredRegRegConstImm,
      SuclampPredRegRegImmImm,
      SuclampPredRegRegRegImm,
      SueauRegRegConstReg,
      SueauRegRegImmReg,
      SueauRegRegRegConst,
      SueauRegRegRegReg,
      SuldPredRegAddressImm,
      SuldPredRegAddressImmImm,
      SuldPredRegAddressImmImmImm,
      SuldPredRegAddressReg,
      SuldPredRegAddressRegImm,
      SuldPredRegAddressRegReg,
      SuldPredRegAddressRegRegImm,
      SuldPredRegAddressRegRegReg,
      SuldRegAddressImm,
      SuldRegAddressImmImm,
      SuldRegAddressImmImmImm,
      SuldRegAddressReg,
      SuldRegAddressRegImm,
      SuldRegAddressRegReg,
      SuldRegAddressRegRegImm,
      SuldRegAddressRegRegReg,
      SuldgaRegAddressConstPred,
      SuldgaRegAddressRegPred,
      SuqueryPredRegAddressImmImm,
      SuqueryPredRegAddressImmImmImm,
      SuqueryPredRegAddressReg,
      SuqueryPredRegAddressRegImm,
      SuqueryPredRegAddressRegReg,
      SuqueryPredRegAddressRegRegImm,
      SuqueryPredRegAddressRegRegReg,
      SuqueryRegAddressImmImm,
      SuqueryRegAddressImmImmImm,
      SuqueryRegAddressReg,
      SuqueryRegAddressRegImm,
      SuqueryRegAddressRegReg,
      SuqueryRegAddressRegRegImm,
      SuqueryRegAddressRegRegReg,
      SuredAddressRegImm,
      SuredAddressRegImmImm,
      SuredAddressRegImmImmImm,
      SuredAddressRegReg,
      SuredAddressRegRegImm,
      SuredAddressRegRegReg,
      SuredAddressRegRegRegImm,
      SuredAddressRegRegRegReg,
      SustAddressRegImm,
      SustAddressRegImmImm,
      SustAddressRegImmImmImm,
      SustAddressRegReg,
      SustAddressRegRegImm,
      SustAddressRegRegReg,
      SustAddressRegRegRegImm,
      SustAddressRegRegRegReg,
      SustgaAddressConstRegPred,
      SustgaAddressRegRegPred,
      SyncNoOperands,
      SyncsNoOperands,
      SyncsAddress,
      SyncsAddressReg,
      SyncsPredAddressReg,
      SyncsRegAddress,
      SyncsRegAddressReg,
      SyncsRegAddressRegReg,
      TexPredRegRegRegImm,
      TexPredRegRegRegImmImm,
      TexPredRegRegRegImmImmImm,
      TexPredRegRegRegReg,
      TexPredRegRegRegRegImm,
      TexPredRegRegRegRegImmImm,
      TexPredRegRegRegRegImmImmImm,
      TexPredRegRegRegRegImmImmImmImm,
      TexPredRegRegRegRegReg,
      TexPredRegRegRegRegRegImm,
      TexPredRegRegRegRegRegImmImm,
      TexPredRegRegRegRegRegReg,
      TexPredRegRegRegRegRegRegImm,
      TexPredRegRegRegRegRegRegImmImm,
      TexRegRegRegImm,
      TexRegRegRegImmImm,
      TexRegRegRegImmImmImm,
      TexRegRegRegReg,
      TexRegRegRegRegImm,
      TexRegRegRegRegImmImm,
      TexRegRegRegRegImmImmImm,
      TexRegRegRegRegImmImmImmImm,
      TexRegRegRegRegReg,
      TexRegRegRegRegRegImm,
      TexRegRegRegRegRegImmImm,
      TexRegRegRegRegRegReg,
      TexRegRegRegRegRegRegImm,
      TexRegRegRegRegRegRegImmImm,
      TexdepbarImm,
      TexsRegRegRegRegImm,
      TldPredRegRegRegImm,
      TldPredRegRegRegImmImm,
      TldPredRegRegRegReg,
      TldPredRegRegRegRegImm,
      TldPredRegRegRegRegImmImm,
      TldPredRegRegRegRegImmImmImm,
      TldPredRegRegRegRegImmImmImmImm,
      TldPredRegRegRegRegReg,
      TldPredRegRegRegRegRegImm,
      TldPredRegRegRegRegRegImmImm,
      TldPredRegRegRegRegRegReg,
      TldPredRegRegRegRegRegRegImm,
      TldPredRegRegRegRegRegRegImmImm,
      TldRegRegRegImm,
      TldRegRegRegImmImm,
      TldRegRegRegReg,
      TldRegRegRegRegImm,
      TldRegRegRegRegImmImm,
      TldRegRegRegRegImmImmImm,
      TldRegRegRegRegImmImmImmImm,
      TldRegRegRegRegReg,
      TldRegRegRegRegRegImm,
      TldRegRegRegRegRegImmImm,
      TldRegRegRegRegRegReg,
      TldRegRegRegRegRegRegImm,
      TldRegRegRegRegRegRegImmImm,
      Tld4PredRegRegRegImm,
      Tld4PredRegRegRegImmImm,
      Tld4PredRegRegRegImmImmImm,
      Tld4PredRegRegRegReg,
      Tld4PredRegRegRegRegImm,
      Tld4PredRegRegRegRegImmImm,
      Tld4PredRegRegRegRegImmImmImm,
      Tld4PredRegRegRegRegImmImmImmImm,
      Tld4PredRegRegRegRegReg,
      Tld4PredRegRegRegRegRegImm,
      Tld4PredRegRegRegRegRegImmImm,
      Tld4PredRegRegRegRegRegReg,
      Tld4PredRegRegRegRegRegRegImm,
      Tld4PredRegRegRegRegRegRegImmImm,
      Tld4RegRegRegImm,
      Tld4RegRegRegImmImm,
      Tld4RegRegRegImmImmImm,
      Tld4RegRegRegReg,
      Tld4RegRegRegRegImm,
      Tld4RegRegRegRegImmImm,
      Tld4RegRegRegRegImmImmImm,
      Tld4RegRegRegRegImmImmImmImm,
      Tld4RegRegRegRegReg,
      Tld4RegRegRegRegRegImm,
      Tld4RegRegRegRegRegImmImm,
      Tld4RegRegRegRegRegReg,
      Tld4RegRegRegRegRegRegImm,
      Tld4RegRegRegRegRegRegImmImm,
      Tld4sRegRegRegRegImm,
      TldsRegRegRegRegImm,
      TmmlRegRegImm,
      TmmlRegRegImmImm,
      TmmlRegRegImmImmImm,
      TmmlRegRegRegImm,
      TmmlRegRegRegImmImm,
      TmmlRegRegRegImmImmImm,
      TmmlRegRegRegReg,
      TmmlRegRegRegRegImm,
      TmmlRegRegRegRegImmImm,
      TmmlRegRegRegRegImmImmImm,
      TmmlRegRegRegRegImmImmImmImm,
      TmmlRegRegRegRegReg,
      TmmlRegRegRegRegRegImm,
      TmmlRegRegRegRegRegImmImm,
      TxaRegRegImm,
      TxaRegRegImmImm,
      TxaRegRegImmImmImm,
      TxdPredRegRegRegImm,
      TxdPredRegRegRegImmImm,
      TxdPredRegRegRegImmImmImm,
      TxdPredRegRegRegReg,
      TxdPredRegRegRegRegImm,
      TxdPredRegRegRegRegImmImm,
      TxdPredRegRegRegRegImmImmImm,
      TxdPredRegRegRegRegImmImmImmImm,
      TxdPredRegRegRegRegReg,
      TxdPredRegRegRegRegRegImm,
      TxdPredRegRegRegRegRegImmImm,
      TxdPredRegRegRegRegRegReg,
      TxdPredRegRegRegRegRegRegImm,
      TxdPredRegRegRegRegRegRegImmImm,
      TxdRegRegRegImm,
      TxdRegRegRegImmImm,
      TxdRegRegRegImmImmImm,
      TxdRegRegRegReg,
      TxdRegRegRegRegImm,
      TxdRegRegRegRegImmImm,
      TxdRegRegRegRegImmImmImm,
      TxdRegRegRegRegImmImmImmImm,
      TxdRegRegRegRegReg
    )
  }
  object Definitions5 {
    val TxdRegRegRegRegRegImm: Definition =
      Definition("TxdRegRegRegRegRegImm", "TXD", Vec("txd_b_noConst_", "txd_urc_"))
    val TxdRegRegRegRegRegImmImm: Definition = Definition("TxdRegRegRegRegRegImmImm", "TXD", Vec("txd_urc_"))
    val TxdRegRegRegRegRegReg: Definition = Definition("TxdRegRegRegRegRegReg", "TXD", Vec("txd_b_urc_", "txd_urc_"))
    val TxdRegRegRegRegRegRegImm: Definition =
      Definition("TxdRegRegRegRegRegRegImm", "TXD", Vec("txd_b_urc_", "txd_urc_"))
    val TxdRegRegRegRegRegRegImmImm: Definition = Definition("TxdRegRegRegRegRegRegImmImm", "TXD", Vec("txd_urc_"))
    val TxqRegRegImm: Definition = Definition("TxqRegRegImm", "TXQ", Vec("TXQ", "TXQ_B"))
    val TxqRegRegImmImm: Definition =
      Definition("TxqRegRegImmImm", "TXQ", Vec("TXQ", "TXQ.B", "TXQ_B", "TXQ_B_Imm", "TXQ_Imm"))
    val TxqRegRegImmImmImm: Definition = Definition(
      "TxqRegRegImmImmImm",
      "TXQ",
      Vec(
        "TXQ Imm",
        "TXQ Legacy Style",
        "TXQ.B Imm",
        "TXQ.B Legacy Style",
        "TXQ_B_Imm",
        "TXQ_B_Legacy_Style",
        "TXQ_Imm",
        "TXQ_Legacy_Style"
      )
    )
    val TxqRegRegImmImmImmImm: Definition = Definition(
      "TxqRegRegImmImmImmImm",
      "TXQ",
      Vec("TXQ Imm Legacy Style", "TXQ.B Imm Legacy Style", "TXQ_B_Imm_Legacy_Style", "TXQ_Imm_Legacy_Style")
    )
    val TxqRegRegRegImm: Definition = Definition("TxqRegRegRegImm", "TXQ", Vec("txq_b_noConst_"))
    val TxqRegRegRegImmImmImm: Definition = Definition("TxqRegRegRegImmImmImm", "TXQ", Vec("txq_", "txq_b_"))
    val TxqRegRegRegImmImmImmImm: Definition = Definition("TxqRegRegRegImmImmImmImm", "TXQ", Vec("txq_b_tid_"))
    val TxqRegRegRegRegImm: Definition = Definition("TxqRegRegRegRegImm", "TXQ", Vec("txq_b_urc_"))
    val TxqRegRegRegRegImmImm: Definition = Definition("TxqRegRegRegRegImmImm", "TXQ", Vec("txq_urc_"))
    val UblkcpAddressAddressReg: Definition =
      Definition("UblkcpAddressAddressReg", "UBLKCP", Vec("ublkcp_", "ublkcp_one_"))
    val UblkcpAddressAddressRegAddress: Definition =
      Definition("UblkcpAddressAddressRegAddress", "UBLKCP", Vec("ublkcp_desc_", "ublkcp_desc_one_"))
    val UblkpfAddressReg: Definition = Definition("UblkpfAddressReg", "UBLKPF", Vec("ublkpf__UUU", "ublkpf_one__UUU"))
    val UblkpfAddressRegAddress: Definition =
      Definition("UblkpfAddressRegAddress", "UBLKPF", Vec("ublkpf__UUU_desc", "ublkpf_one__UUU_desc"))
    val UblkredAddressAddressReg: Definition =
      Definition("UblkredAddressAddressReg", "UBLKRED", Vec("ublkred_", "ublkred_one_"))
    val UblkredAddressAddressRegAddress: Definition =
      Definition("UblkredAddressAddressRegAddress", "UBLKRED", Vec("ublkred_desc_", "ublkred_desc_one_"))
    val UbmskRegRegImm: Definition = Definition("UbmskRegRegImm", "UBMSK", Vec("ubmsk__URuIUR_URI"))
    val UbmskRegRegReg: Definition = Definition("UbmskRegRegReg", "UBMSK", Vec("ubmsk__URURUR_URUR"))
    val UbrevRegImm: Definition = Definition("UbrevRegImm", "UBREV", Vec("ubrev__URuIUR_URI"))
    val UbrevRegReg: Definition = Definition("UbrevRegReg", "UBREV", Vec("ubrev__URURUR_URUR"))
    val UcgabararvNoOperands: Definition = Definition("UcgabararvNoOperands", "UCGABARARV", Vec("ucgabararrive_"))
    val UcgabargetReg: Definition = Definition("UcgabargetReg", "UCGABARGET", Vec("ucgabarget_"))
    val UcgabarsetReg: Definition = Definition("UcgabarsetReg", "UCGABARSET", Vec("ucgabarset_"))
    val UcgabarwaitNoOperands: Definition = Definition("UcgabarwaitNoOperands", "UCGABARWAIT", Vec("ucgabarwait_"))
    val Ucgabar_ArvNoOperands: Definition = Definition("Ucgabar_ArvNoOperands", "UCGABAR_ARV", Vec("ucgabar_arrive_"))
    val Ucgabar_WaitNoOperands: Definition = Definition("Ucgabar_WaitNoOperands", "UCGABAR_WAIT", Vec("ucgabar_wait_"))
    val UcleaRegPredRegImmImm: Definition = Definition("UcleaRegPredRegImmImm", "UCLEA", Vec("uclea__Imm"))
    val UcleaRegPredRegRegImm: Definition = Definition("UcleaRegPredRegRegImm", "UCLEA", Vec("uclea__URb"))
    val Uf2fRegImm: Definition = Definition(
      "Uf2fRegImm",
      "UF2F",
      Vec("uf2f_f32_downconvert__URIR_URIR", "uf2f_f32_upconvert__URIR_URIR", "uf2f_f32_upconvert_swap__URIR_URIR")
    )
    val Uf2fRegReg: Definition = Definition(
      "Uf2fRegReg",
      "UF2F",
      Vec("uf2f_f32_downconvert__UUU_UUU", "uf2f_f32_upconvert__UUU_UUU", "uf2f_f32_upconvert_swap__UUU_UUU")
    )
    val Uf2fpRegImm: Definition = Definition(
      "Uf2fpRegImm",
      "UF2FP",
      Vec("uf2fp_8b_upconvert__URIUR", "uf2fp_tf32__URIUR", "uf2fp_tf32_default_src__URIUR")
    )
    val Uf2fpRegImmReg: Definition = Definition(
      "Uf2fpRegImmReg",
      "UF2FP",
      Vec("uf2fp_f16_to_8b_downconvert__URIUR", "uf2fp_merge_c__URIUR", "uf2fp_merge_c_default_dst_src__URIUR")
    )
    val Uf2fpRegReg: Definition = Definition(
      "Uf2fpRegReg",
      "UF2FP",
      Vec("uf2fp_8b_upconvert__URURUR", "uf2fp_tf32__URURUR", "uf2fp_tf32_default_src__URURUR")
    )
    val Uf2fpRegRegImm: Definition = Definition(
      "Uf2fpRegRegImm",
      "UF2FP",
      Vec(
        "uf2fp__URIUR",
        "uf2fp_default_dst_src__URIUR",
        "uf2fp_f16_to_8b_downconvert__URURI",
        "uf2fp_merge_c__URURI",
        "uf2fp_merge_c_default_dst_src__URURI"
      )
    )
    val Uf2fpRegRegImmReg: Definition =
      Definition("Uf2fpRegRegImmReg", "UF2FP", Vec("uf2fp_f32_to_8b_downconvert__URIUR"))
    val Uf2fpRegRegReg: Definition = Definition(
      "Uf2fpRegRegReg",
      "UF2FP",
      Vec(
        "uf2fp__URURUR",
        "uf2fp_default_dst_src__URURUR",
        "uf2fp_f16_to_8b_downconvert__URURUR",
        "uf2fp_merge_c__URURUR",
        "uf2fp_merge_c_default_dst_src__URURUR"
      )
    )
    val Uf2fpRegRegRegImm: Definition =
      Definition("Uf2fpRegRegRegImm", "UF2FP", Vec("uf2fp_f32_to_8b_downconvert__URURI"))
    val Uf2fpRegRegRegReg: Definition =
      Definition("Uf2fpRegRegRegReg", "UF2FP", Vec("uf2fp_f32_to_8b_downconvert__URURUR"))
    val Uf2iRegImm: Definition = Definition(
      "Uf2iRegImm",
      "UF2I",
      Vec(
        "uf2i__IU_32b",
        "uf2i__Ib_16b",
        "uf2i__Ib_bf16",
        "uf2i_swap__IU_32b",
        "uf2i_swap__Ib_16b",
        "uf2i_swap__Ib_bf16"
      )
    )
    val Uf2iRegReg: Definition = Definition(
      "Uf2iRegReg",
      "UF2I",
      Vec(
        "uf2i__URb_16b",
        "uf2i__URb_32b",
        "uf2i__URb_bf16",
        "uf2i_swap__URb_16b",
        "uf2i_swap__URb_32b",
        "uf2i_swap__URb_bf16"
      )
    )
    val Uf2ipRegRegImmReg: Definition = Definition("Uf2ipRegRegImmReg", "UF2IP", Vec("uf2ip__URIUR"))
    val Uf2ipRegRegRegImm: Definition = Definition("Uf2ipRegRegRegImm", "UF2IP", Vec("uf2ip__RRI"))
    val Uf2ipRegRegRegReg: Definition = Definition("Uf2ipRegRegRegReg", "UF2IP", Vec("uf2ip__UUU"))
    val UfaddRegRegImm: Definition = Definition("UfaddRegRegImm", "UFADD", Vec("ufadd__URURsI_URURI"))
    val UfaddRegRegReg: Definition = Definition("UfaddRegRegReg", "UFADD", Vec("ufadd__URURUR_UUU"))
    val UffmaRegRegImmReg: Definition = Definition("UffmaRegRegImmReg", "UFFMA", Vec("uffma__URIUR_URIUR"))
    val UffmaRegRegRegImm: Definition = Definition("UffmaRegRegRegImm", "UFFMA", Vec("uffma__URURI_URURI"))
    val UffmaRegRegRegReg: Definition = Definition("UffmaRegRegRegReg", "UFFMA", Vec("uffma__UUU_UUU"))
    val UfhaddRegRegReg: Definition = Definition("UfhaddRegRegReg", "UFHADD", Vec("ufhadd__UUU"))
    val UfhfmaRegRegRegReg: Definition = Definition("UfhfmaRegRegRegReg", "UFHFMA", Vec("ufhfma__UUU"))
    val UfloRegImm: Definition = Definition("UfloRegImm", "UFLO", Vec("uflo__URuIUR_URuIR"))
    val UfloRegPredImm: Definition = Definition("UfloRegPredImm", "UFLO", Vec("uflo__URuIUR_URuIR"))
    val UfloRegPredReg: Definition = Definition("UfloRegPredReg", "UFLO", Vec("uflo__URURUR_URURUR"))
    val UfloRegReg: Definition = Definition("UfloRegReg", "UFLO", Vec("uflo__URURUR_URURUR"))
    val UfmnmxRegPredRegImmPred: Definition =
      Definition("UfmnmxRegPredRegImmPred", "UFMNMX", Vec("ufmnmx_pred__URsIUR_URIR"))
    val UfmnmxRegPredRegRegPred: Definition =
      Definition("UfmnmxRegPredRegRegPred", "UFMNMX", Vec("ufmnmx_pred__URURUR_UUU"))
    val UfmnmxRegRegImmPred: Definition = Definition("UfmnmxRegRegImmPred", "UFMNMX", Vec("ufmnmx__URsIUR_URIR"))
    val UfmnmxRegRegRegPred: Definition = Definition("UfmnmxRegRegRegPred", "UFMNMX", Vec("ufmnmx__URURUR_UUU"))
    val UfmulRegRegImm: Definition = Definition("UfmulRegRegImm", "UFMUL", Vec("ufmul__URIR"))
    val UfmulRegRegReg: Definition = Definition("UfmulRegRegReg", "UFMUL", Vec("ufmul__UUU"))
    val UfrndRegImm: Definition = Definition(
      "UfrndRegImm",
      "UFRND",
      Vec(
        "ufrnd__bf16_I",
        "ufrnd__f16_I",
        "ufrnd__f32_I",
        "ufrnd_swap__bf16_I",
        "ufrnd_swap__f16_I",
        "ufrnd_swap__f32_I"
      )
    )
    val UfrndRegReg: Definition = Definition(
      "UfrndRegReg",
      "UFRND",
      Vec(
        "ufrnd__bf16_URb",
        "ufrnd__f16_URb",
        "ufrnd__f32_URb",
        "ufrnd_swap__bf16_URb",
        "ufrnd_swap__f16_URb",
        "ufrnd_swap__f32_URb"
      )
    )
    val UfselRegRegImmPred: Definition = Definition("UfselRegRegImmPred", "UFSEL", Vec("ufsel__URsIUR_URIR"))
    val UfselRegRegRegPred: Definition = Definition("UfselRegRegRegPred", "UFSEL", Vec("ufsel__URURUR_UUU"))
    val UfsetRegRegImm: Definition = Definition("UfsetRegRegImm", "UFSET", Vec("ufset_simple__URsIUR_URIR"))
    val UfsetRegRegImmPred: Definition = Definition("UfsetRegRegImmPred", "UFSET", Vec("ufset__URsIUR_URIR"))
    val UfsetRegRegReg: Definition = Definition("UfsetRegRegReg", "UFSET", Vec("ufset_simple__URURUR_UUU"))
    val UfsetRegRegRegPred: Definition = Definition("UfsetRegRegRegPred", "UFSET", Vec("ufset__URURUR_UUU"))
    val UfsetpPredPredRegImmPred: Definition =
      Definition("UfsetpPredPredRegImmPred", "UFSETP", Vec("ufsetp__URsIUR_URIR"))
    val UfsetpPredPredRegRegPred: Definition =
      Definition("UfsetpPredPredRegRegPred", "UFSETP", Vec("ufsetp__URURUR_UUU"))
    val UfsetpPredRegImm: Definition = Definition("UfsetpPredRegImm", "UFSETP", Vec("ufsetp_simple__URsIUR_URIR"))
    val UfsetpPredRegReg: Definition = Definition("UfsetpPredRegReg", "UFSETP", Vec("ufsetp_simple__URURUR_UUU"))
    val UgetnextworkidAddressAddress: Definition =
      Definition("UgetnextworkidAddressAddress", "UGETNEXTWORKID", Vec("ugetnextworkid_", "ugetnextworkid_one_"))
    val Ui2fRegImm: Definition = Definition("Ui2fRegImm", "UI2F", Vec("ui2f__IS_32b", "ui2f__IU_32b"))
    val Ui2fRegReg: Definition = Definition("Ui2fRegReg", "UI2F", Vec("ui2f__UR_16b", "ui2f__UR_32b", "ui2f__UR_8b"))
    val Ui2fpRegImm: Definition = Definition("Ui2fpRegImm", "UI2FP", Vec("ui2fp__URsIR", "ui2fp__URuIR"))
    val Ui2fpRegReg: Definition = Definition("Ui2fpRegReg", "UI2FP", Vec("ui2fp__UUU"))
    val Ui2iRegImm: Definition = Definition("Ui2iRegImm", "UI2I", Vec("ui2i__URsIUR_URIR"))
    val Ui2iRegReg: Definition = Definition("Ui2iRegReg", "UI2I", Vec("ui2i__URURUR_UUU"))
    val Ui2ipRegRegImmReg: Definition = Definition(
      "Ui2ipRegRegImmReg",
      "UI2IP",
      Vec("ui2ip_24__URsIUR_URRI", "ui2ip_28__URsIUR_URRI", "ui2ip__URsIUR_URRI")
    )
    val Ui2ipRegRegRegReg: Definition =
      Definition("Ui2ipRegRegRegReg", "UI2IP", Vec("ui2ip_24__URURUR_UUU", "ui2ip_28__URURUR_UUU", "ui2ip__URURUR_UUU"))
    val UiabsRegImm: Definition = Definition("UiabsRegImm", "UIABS", Vec("uiabs__URsIUR_I"))
    val UiabsRegReg: Definition = Definition("UiabsRegReg", "UIABS", Vec("uiabs__URURUR_UR"))
    val Uiadd3RegPredPredRegImmReg: Definition =
      Definition("Uiadd3RegPredPredRegImmReg", "UIADD3", Vec("uiadd3_64__URsIUR_RIR", "uiadd3__URsIUR_RIR"))
    val Uiadd3RegPredPredRegImmRegPredPred: Definition =
      Definition("Uiadd3RegPredPredRegImmRegPredPred", "UIADD3", Vec("uiadd3_64_x__URsIUR_RIR", "uiadd3_x__URsIUR_RIR"))
    val Uiadd3RegPredPredRegRegReg: Definition =
      Definition("Uiadd3RegPredPredRegRegReg", "UIADD3", Vec("uiadd3_64__URURUR_URURUR", "uiadd3__URURUR_URURUR"))
    val Uiadd3RegPredPredRegRegRegPredPred: Definition = Definition(
      "Uiadd3RegPredPredRegRegRegPredPred",
      "UIADD3",
      Vec("uiadd3_64_x__URURUR_URURUR", "uiadd3_x__URURUR_URURUR")
    )
    val Uiadd3RegPredRegImmReg: Definition =
      Definition("Uiadd3RegPredRegImmReg", "UIADD3", Vec("uiadd3_64__URsIUR_RIR", "uiadd3__URsIUR_RIR"))
    val Uiadd3RegPredRegImmRegPredPred: Definition =
      Definition("Uiadd3RegPredRegImmRegPredPred", "UIADD3", Vec("uiadd3_64_x__URsIUR_RIR", "uiadd3_x__URsIUR_RIR"))
    val Uiadd3RegPredRegRegReg: Definition =
      Definition("Uiadd3RegPredRegRegReg", "UIADD3", Vec("uiadd3_64__URURUR_URURUR", "uiadd3__URURUR_URURUR"))
    val Uiadd3RegPredRegRegRegPredPred: Definition = Definition(
      "Uiadd3RegPredRegRegRegPredPred",
      "UIADD3",
      Vec("uiadd3_64_x__URURUR_URURUR", "uiadd3_x__URURUR_URURUR")
    )
    val Uiadd3RegRegImmReg: Definition =
      Definition("Uiadd3RegRegImmReg", "UIADD3", Vec("uiadd3_64__URsIUR_RIR", "uiadd3__URsIUR_RIR"))
    val Uiadd3RegRegImmRegPredPred: Definition =
      Definition("Uiadd3RegRegImmRegPredPred", "UIADD3", Vec("uiadd3_64_x__URsIUR_RIR", "uiadd3_x__URsIUR_RIR"))
    val Uiadd3RegRegRegReg: Definition =
      Definition("Uiadd3RegRegRegReg", "UIADD3", Vec("uiadd3_64__URURUR_URURUR", "uiadd3__URURUR_URURUR"))
    val Uiadd3RegRegRegRegPredPred: Definition =
      Definition("Uiadd3RegRegRegRegPredPred", "UIADD3", Vec("uiadd3_64_x__URURUR_URURUR", "uiadd3_x__URURUR_URURUR"))
    val UimadRegPredRegImmReg: Definition =
      Definition("UimadRegPredRegImmReg", "UIMAD", Vec("uimad_hi__URsIUR_URIUR", "uimad_wide__URsIUR_URIUR"))
    val UimadRegPredRegImmRegPred: Definition =
      Definition("UimadRegPredRegImmRegPred", "UIMAD", Vec("uimad_hi_x__URsIUR_URIUR", "uimad_wide_x__URsIUR_URIUR"))
    val UimadRegPredRegRegReg: Definition =
      Definition("UimadRegPredRegRegReg", "UIMAD", Vec("uimad_hi__URURUR_URURUR", "uimad_wide__URURUR_URURUR"))
    val UimadRegPredRegRegRegPred: Definition =
      Definition("UimadRegPredRegRegRegPred", "UIMAD", Vec("uimad_hi_x__URURUR_URURUR", "uimad_wide_x__URURUR_URURUR"))
    val UimadRegRegImmReg: Definition = Definition(
      "UimadRegRegImmReg",
      "UIMAD",
      Vec("uimad__URsIUR_URIUR", "uimad_hi__URsIUR_URIUR", "uimad_wide__URsIUR_URIUR")
    )
    val UimadRegRegImmRegPred: Definition = Definition(
      "UimadRegRegImmRegPred",
      "UIMAD",
      Vec("uimad_hi_x__URsIUR_URIUR", "uimad_wide_x__URsIUR_URIUR", "uimad_x__URsIUR_URIUR")
    )
    val UimadRegRegRegImm: Definition = Definition("UimadRegRegRegImm", "UIMAD", Vec("uimad__URURsI_URURI"))
    val UimadRegRegRegImmPred: Definition = Definition("UimadRegRegRegImmPred", "UIMAD", Vec("uimad_x__URURsI_URURI"))
    val UimadRegRegRegReg: Definition = Definition(
      "UimadRegRegRegReg",
      "UIMAD",
      Vec("uimad__URURUR_URURUR", "uimad_hi__URURUR_URURUR", "uimad_wide__URURUR_URURUR")
    )
    val UimadRegRegRegRegPred: Definition = Definition(
      "UimadRegRegRegRegPred",
      "UIMAD",
      Vec("uimad_hi_x__URURUR_URURUR", "uimad_wide_x__URURUR_URURUR", "uimad_x__URURUR_URURUR")
    )
    val UimnmxPredPredRegRegImmPred: Definition = Definition(
      "UimnmxPredPredRegRegImmPred",
      "UIMNMX",
      Vec("uimnmx_64_nopred__URIR_URsIUR", "uimnmx_nopred__URIR_URsIUR")
    )
    val UimnmxPredPredRegRegImmPredPred: Definition =
      Definition("UimnmxPredPredRegRegImmPredPred", "UIMNMX", Vec("uimnmx_64__URIR_URsIUR", "uimnmx__URIR_URsIUR"))
    val UimnmxPredPredRegRegRegPred: Definition = Definition(
      "UimnmxPredPredRegRegRegPred",
      "UIMNMX",
      Vec("uimnmx_64_nopred__UUU_URURUR", "uimnmx_nopred__UUU_URURUR")
    )
    val UimnmxPredPredRegRegRegPredPred: Definition =
      Definition("UimnmxPredPredRegRegRegPredPred", "UIMNMX", Vec("uimnmx_64__UUU_URURUR", "uimnmx__UUU_URURUR"))
    val UimnmxPredRegRegImmPred: Definition = Definition(
      "UimnmxPredRegRegImmPred",
      "UIMNMX",
      Vec("uimnmx_64_nopred__URIR_URsIUR", "uimnmx_nopred__URIR_URsIUR")
    )
    val UimnmxPredRegRegImmPredPred: Definition =
      Definition("UimnmxPredRegRegImmPredPred", "UIMNMX", Vec("uimnmx_64__URIR_URsIUR", "uimnmx__URIR_URsIUR"))
    val UimnmxPredRegRegRegPred: Definition =
      Definition("UimnmxPredRegRegRegPred", "UIMNMX", Vec("uimnmx_64_nopred__UUU_URURUR", "uimnmx_nopred__UUU_URURUR"))
    val UimnmxPredRegRegRegPredPred: Definition =
      Definition("UimnmxPredRegRegRegPredPred", "UIMNMX", Vec("uimnmx_64__UUU_URURUR", "uimnmx__UUU_URURUR"))
    val UimnmxRegRegImmPred: Definition =
      Definition("UimnmxRegRegImmPred", "UIMNMX", Vec("uimnmx_64_nopred__URIR_URsIUR", "uimnmx_nopred__URIR_URsIUR"))
    val UimnmxRegRegImmPredPred: Definition =
      Definition("UimnmxRegRegImmPredPred", "UIMNMX", Vec("uimnmx_64__URIR_URsIUR", "uimnmx__URIR_URsIUR"))
    val UimnmxRegRegRegPred: Definition =
      Definition("UimnmxRegRegRegPred", "UIMNMX", Vec("uimnmx_64_nopred__UUU_URURUR", "uimnmx_nopred__UUU_URURUR"))
    val UimnmxRegRegRegPredPred: Definition =
      Definition("UimnmxRegRegRegPredPred", "UIMNMX", Vec("uimnmx_64__UUU_URURUR", "uimnmx__UUU_URURUR"))
    val UisetpPredPredRegImmPred: Definition =
      Definition("UisetpPredPredRegImmPred", "UISETP", Vec("uisetp_64__URsIUR_URIR", "uisetp__URsIUR_URIR"))
    val UisetpPredPredRegImmPredPred: Definition = Definition(
      "UisetpPredPredRegImmPredPred",
      "UISETP",
      Vec("uisetp_64_optional_upr__URsIUR_URIR", "uisetp_optional_upr__URsIUR_URIR")
    )
    val UisetpPredPredRegRegPred: Definition =
      Definition("UisetpPredPredRegRegPred", "UISETP", Vec("uisetp_64__URURUR_URURUR", "uisetp__URURUR_URURUR"))
    val UisetpPredPredRegRegPredPred: Definition = Definition(
      "UisetpPredPredRegRegPredPred",
      "UISETP",
      Vec("uisetp_64_optional_upr__URURUR_URURUR", "uisetp_optional_upr__URURUR_URURUR")
    )
    val UisetpPredRegImm: Definition =
      Definition("UisetpPredRegImm", "UISETP", Vec("uisetp_64_simple__URsIUR_URIR", "uisetp_simple__URsIUR_URIR"))
    val UisetpPredRegImmPred: Definition = Definition(
      "UisetpPredRegImmPred",
      "UISETP",
      Vec("uisetp_64_simple_optional_upr__URsIUR_URIR", "uisetp_simple_optional_upr__URsIUR_URIR")
    )
    val UisetpPredRegReg: Definition =
      Definition("UisetpPredRegReg", "UISETP", Vec("uisetp_64_simple__URURUR_URURUR", "uisetp_simple__URURUR_URURUR"))
    val UisetpPredRegRegPred: Definition = Definition(
      "UisetpPredRegRegPred",
      "UISETP",
      Vec("uisetp_64_simple_optional_upr__URURUR_URURUR", "uisetp_simple_optional_upr__URURUR_URURUR")
    )
    val UldcRegAddress: Definition =
      Definition("UldcRegAddress", "ULDC", Vec("uldc_imm_", "uldc_ur_offs_optional_upx_"))
    val UldcRegAddressImm: Definition = Definition("UldcRegAddressImm", "ULDC", Vec("uldc_const__RCxR"))
    val UldcRegAddressPred: Definition = Definition("UldcRegAddressPred", "ULDC", Vec("uldc_ur_offs_"))
    val UldcRegConst: Definition = Definition("UldcRegConst", "ULDC", Vec("uldc_const__RCR", "uldc_ur_offset_"))
    val UleaRegPredRegImmImm: Definition =
      Definition("UleaRegPredRegImmImm", "ULEA", Vec("ulea_hi_imm_sx32__RuIR_URIUR", "ulea_lo_imm__URuIUR_URIUR"))
    val UleaRegPredRegImmImmPred: Definition = Definition(
      "UleaRegPredRegImmImmPred",
      "ULEA",
      Vec("ulea_hi_imm_sx32_x__RuIR_URIUR", "ulea_lo_imm_x__URuIUR_URIUR")
    )
    val UleaRegPredRegImmRegImm: Definition =
      Definition("UleaRegPredRegImmRegImm", "ULEA", Vec("ulea_hi_imm__RuIR_URIR"))
    val UleaRegPredRegImmRegImmPred: Definition =
      Definition("UleaRegPredRegImmRegImmPred", "ULEA", Vec("ulea_hi_imm_x__RuIR_URIR"))
    val UleaRegPredRegRegImm: Definition = Definition(
      "UleaRegPredRegRegImm",
      "ULEA",
      Vec("ulea_hi_noimm_sx32__URURUR_URURUR", "ulea_lo_noimm__URURUR_URURUR")
    )
    val UleaRegPredRegRegImmImm: Definition =
      Definition("UleaRegPredRegRegImmImm", "ULEA", Vec("ulea_hi_imm__RRuI_RRI"))
    val UleaRegPredRegRegImmImmPred: Definition =
      Definition("UleaRegPredRegRegImmImmPred", "ULEA", Vec("ulea_hi_imm_x__RRuI_RRI"))
    val UleaRegPredRegRegImmPred: Definition = Definition(
      "UleaRegPredRegRegImmPred",
      "ULEA",
      Vec("ulea_hi_noimm_sx32_x__URURUR_URURUR", "ulea_lo_noimm_x__URURUR_URURUR")
    )
    val UleaRegPredRegRegRegImm: Definition =
      Definition("UleaRegPredRegRegRegImm", "ULEA", Vec("ulea_hi_noimm__URURUR_URURUR"))
    val UleaRegPredRegRegRegImmPred: Definition =
      Definition("UleaRegPredRegRegRegImmPred", "ULEA", Vec("ulea_hi_noimm_x__URURUR_URURUR"))
    val UleaRegRegImmImm: Definition =
      Definition("UleaRegRegImmImm", "ULEA", Vec("ulea_hi_imm_sx32__RuIR_URIUR", "ulea_lo_imm__URuIUR_URIUR"))
    val UleaRegRegImmImmPred: Definition =
      Definition("UleaRegRegImmImmPred", "ULEA", Vec("ulea_hi_imm_sx32_x__RuIR_URIUR", "ulea_lo_imm_x__URuIUR_URIUR"))
    val UleaRegRegImmRegImm: Definition = Definition("UleaRegRegImmRegImm", "ULEA", Vec("ulea_hi_imm__RuIR_URIR"))
    val UleaRegRegImmRegImmPred: Definition =
      Definition("UleaRegRegImmRegImmPred", "ULEA", Vec("ulea_hi_imm_x__RuIR_URIR"))
    val UleaRegRegRegImm: Definition =
      Definition("UleaRegRegRegImm", "ULEA", Vec("ulea_hi_noimm_sx32__URURUR_URURUR", "ulea_lo_noimm__URURUR_URURUR"))
    val UleaRegRegRegImmImm: Definition = Definition("UleaRegRegRegImmImm", "ULEA", Vec("ulea_hi_imm__RRuI_RRI"))
    val UleaRegRegRegImmImmPred: Definition =
      Definition("UleaRegRegRegImmImmPred", "ULEA", Vec("ulea_hi_imm_x__RRuI_RRI"))
    val UleaRegRegRegImmPred: Definition = Definition(
      "UleaRegRegRegImmPred",
      "ULEA",
      Vec("ulea_hi_noimm_sx32_x__URURUR_URURUR", "ulea_lo_noimm_x__URURUR_URURUR")
    )
    val UleaRegRegRegRegImm: Definition = Definition("UleaRegRegRegRegImm", "ULEA", Vec("ulea_hi_noimm__URURUR_URURUR"))
    val UleaRegRegRegRegImmPred: Definition =
      Definition("UleaRegRegRegRegImmPred", "ULEA", Vec("ulea_hi_noimm_x__URURUR_URURUR"))
    val UlepcReg: Definition = Definition("UlepcReg", "ULEPC", Vec("ulepc__URURUR"))
    val UlepcRegImm: Definition = Definition("UlepcRegImm", "ULEPC", Vec("ulepc__UR_I_R", "ulepc_rel_"))
    val UlopPredRegRegImm: Definition = Definition("UlopPredRegRegImm", "ULOP", Vec("ulop_imm_optionalUPp_"))
    val UlopPredRegRegImmPred: Definition = Definition("UlopPredRegRegImmPred", "ULOP", Vec("ulop_imm_"))
    val UlopPredRegRegReg: Definition = Definition("UlopPredRegRegReg", "ULOP", Vec("ulop_noimm_optionalUPp__URURUR"))
    val UlopPredRegRegRegPred: Definition = Definition("UlopPredRegRegRegPred", "ULOP", Vec("ulop_noimm__URURUR"))
    val UlopRegRegImm: Definition = Definition("UlopRegRegImm", "ULOP", Vec("ulop_imm_optionalUPp_"))
    val UlopRegRegImmPred: Definition = Definition("UlopRegRegImmPred", "ULOP", Vec("ulop_imm_"))
    val UlopRegRegReg: Definition = Definition("UlopRegRegReg", "ULOP", Vec("ulop_noimm_optionalUPp__URURUR"))
    val UlopRegRegRegPred: Definition = Definition("UlopRegRegRegPred", "ULOP", Vec("ulop_noimm__URURUR"))
    val Ulop3PredRegRegImmReg: Definition =
      Definition("Ulop3PredRegRegImmReg", "ULOP3", Vec("ulop3_imm_optionalUPp__URIR_URIR"))
    val Ulop3PredRegRegImmRegImm: Definition =
      Definition("Ulop3PredRegRegImmRegImm", "ULOP3", Vec("ulop3_lut_optionalUPp__URuIUR_URIR"))
    val Ulop3PredRegRegImmRegImmPred: Definition =
      Definition("Ulop3PredRegRegImmRegImmPred", "ULOP3", Vec("ulop3_lut__URuIUR_URIR"))
    val Ulop3PredRegRegImmRegPred: Definition =
      Definition("Ulop3PredRegRegImmRegPred", "ULOP3", Vec("ulop3_imm__URIR_URIR"))
    val Ulop3PredRegRegRegReg: Definition =
      Definition("Ulop3PredRegRegRegReg", "ULOP3", Vec("ulop3_noimm_optionalUPp__URURUR_URURUR"))
    val Ulop3PredRegRegRegRegImm: Definition =
      Definition("Ulop3PredRegRegRegRegImm", "ULOP3", Vec("ulop3_lut_optionalUPp__URURUR_URURUR"))
    val Ulop3PredRegRegRegRegImmPred: Definition =
      Definition("Ulop3PredRegRegRegRegImmPred", "ULOP3", Vec("ulop3_lut__URURUR_URURUR"))
    val Ulop3PredRegRegRegRegPred: Definition =
      Definition("Ulop3PredRegRegRegRegPred", "ULOP3", Vec("ulop3_noimm__URURUR_URURUR"))
    val Ulop3RegRegImmReg: Definition =
      Definition("Ulop3RegRegImmReg", "ULOP3", Vec("ulop3_imm_optionalUPp__URIR_URIR"))
    val Ulop3RegRegImmRegImm: Definition =
      Definition("Ulop3RegRegImmRegImm", "ULOP3", Vec("ulop3_lut_optionalUPp__URuIUR_URIR"))
    val Ulop3RegRegImmRegImmPred: Definition =
      Definition("Ulop3RegRegImmRegImmPred", "ULOP3", Vec("ulop3_lut__URuIUR_URIR"))
    val Ulop3RegRegImmRegPred: Definition = Definition("Ulop3RegRegImmRegPred", "ULOP3", Vec("ulop3_imm__URIR_URIR"))
    val Ulop3RegRegRegReg: Definition =
      Definition("Ulop3RegRegRegReg", "ULOP3", Vec("ulop3_noimm_optionalUPp__URURUR_URURUR"))
    val Ulop3RegRegRegRegImm: Definition =
      Definition("Ulop3RegRegRegRegImm", "ULOP3", Vec("ulop3_lut_optionalUPp__URURUR_URURUR"))
    val Ulop3RegRegRegRegImmPred: Definition =
      Definition("Ulop3RegRegRegRegImmPred", "ULOP3", Vec("ulop3_lut__URURUR_URURUR"))
    val Ulop3RegRegRegRegPred: Definition =
      Definition("Ulop3RegRegRegRegPred", "ULOP3", Vec("ulop3_noimm__URURUR_URURUR"))
    val Ulop32iPredRegRegImm: Definition = Definition("Ulop32iPredRegRegImm", "ULOP32I", Vec("ulop32i_optionalUPp_"))
    val Ulop32iPredRegRegImmPred: Definition = Definition("Ulop32iPredRegRegImmPred", "ULOP32I", Vec("ulop32i_"))
    val Ulop32iRegRegImm: Definition = Definition("Ulop32iRegRegImm", "ULOP32I", Vec("ulop32i_optionalUPp_"))
    val Ulop32iRegRegImmPred: Definition = Definition("Ulop32iRegRegImmPred", "ULOP32I", Vec("ulop32i_"))
    val UmemsetsAddressRegReg: Definition = Definition("UmemsetsAddressRegReg", "UMEMSETS", Vec("umemsets_"))
    val UmovRegImm: Definition =
      Definition("UmovRegImm", "UMOV", Vec("uiadd3_umov64__RIR_URsIUR", "umov__UI", "umov_imm64_"))
    val UmovRegReg: Definition =
      Definition("UmovRegReg", "UMOV", Vec("uiadd3_umov64__URURUR_URURUR", "umov_64__UR", "umov__UR"))
    val Up2urReg: Definition = Definition("Up2urReg", "UP2UR", Vec("up2ur_simple_"))
    val Up2urRegRegImm: Definition = Definition("Up2urRegRegImm", "UP2UR", Vec("up2ur__Imm"))
    val Up2urRegRegReg: Definition = Definition("Up2urRegRegReg", "UP2UR", Vec("up2ur__URb"))
    val Uplop3PredPredPredPred: Definition = Definition("Uplop3PredPredPredPred", "UPLOP3", Vec("uplop3_1out_"))
    val Uplop3PredPredPredPredImm: Definition =
      Definition("Uplop3PredPredPredPredImm", "UPLOP3", Vec("uplop3_lut_1out_"))
    val Uplop3PredPredPredPredPredImmImm: Definition =
      Definition("Uplop3PredPredPredPredPredImmImm", "UPLOP3", Vec("uplop3_lut_2out_"))
    val Uplop3PredPredPredRegPredImmImm: Definition =
      Definition("Uplop3PredPredPredRegPredImmImm", "UPLOP3", Vec("uplop3_lut_2out_1reg__URURUR"))
    val Uplop3PredPredPredRegRegImmImm: Definition =
      Definition("Uplop3PredPredPredRegRegImmImm", "UPLOP3", Vec("uplop3_lut_2out_2reg__URURUR"))
    val Uplop3PredPredRegPredImm: Definition =
      Definition("Uplop3PredPredRegPredImm", "UPLOP3", Vec("uplop3_lut_1out_1reg__URURUR"))
    val Uplop3PredPredRegRegImm: Definition =
      Definition("Uplop3PredPredRegRegImm", "UPLOP3", Vec("uplop3_lut_1out_2reg__URURUR"))
    val Uplop3PredPredRegRegRegImmImm: Definition =
      Definition("Uplop3PredPredRegRegRegImmImm", "UPLOP3", Vec("uplop3_lut_2out_3reg__URURUR"))
    val Uplop3PredRegRegRegImm: Definition =
      Definition("Uplop3PredRegRegRegImm", "UPLOP3", Vec("uplop3_lut_1out_3reg__URURUR"))
    val UpopcRegImm: Definition = Definition("UpopcRegImm", "UPOPC", Vec("upopc__URuIUR_URIR"))
    val UpopcRegReg: Definition = Definition("UpopcRegReg", "UPOPC", Vec("upopc__URURUR_URUUR"))
    val UprmtRegRegImmReg: Definition = Definition("UprmtRegRegImmReg", "UPRMT", Vec("uprmt__URIUR"))
    val UprmtRegRegRegReg: Definition = Definition("UprmtRegRegRegReg", "UPRMT", Vec("uprmt__URURUR"))
    val UpsetpPredPredPred: Definition = Definition("UpsetpPredPredPred", "UPSETP", Vec("upsetp_simple_"))
    val UpsetpPredPredPredPredPred: Definition = Definition("UpsetpPredPredPredPredPred", "UPSETP", Vec("upsetp_"))
    val Ur2upReg: Definition = Definition("Ur2upReg", "UR2UP", Vec("ur2up__Imm"))
    val Ur2upRegImm: Definition = Definition("Ur2upRegImm", "UR2UP", Vec("ur2up__Imm"))
    val Ur2upRegReg: Definition = Definition("Ur2upRegReg", "UR2UP", Vec("ur2up__URb"))
    val UredgrAddressReg: Definition = Definition("UredgrAddressReg", "UREDGR", Vec("uredgr_", "uredgr_one_"))
    val UselRegRegImmPred: Definition =
      Definition("UselRegRegImmPred", "USEL", Vec("usel_64__URIR", "usel__URuIUR_URIR"))
    val UselRegRegRegPred: Definition = Definition("UselRegRegRegPred", "USEL", Vec("usel_64__UUU", "usel__URURUR_UUU"))
    val UsetmaxregImm: Definition =
      Definition("UsetmaxregImm", "USETMAXREG", Vec("usetmaxregAlloc__Ib_dealloc", "usetmaxreg__Ib_dealloc"))
    val UsetmaxregPredImm: Definition =
      Definition("UsetmaxregPredImm", "USETMAXREG", Vec("usetmaxregAlloc__Ib_alloc", "usetmaxreg__Ib_alloc"))
    val UsetmaxregPredReg: Definition =
      Definition("UsetmaxregPredReg", "USETMAXREG", Vec("usetmaxregAlloc__URb_alloc", "usetmaxreg__URb_alloc"))
    val UsetmaxregReg: Definition =
      Definition("UsetmaxregReg", "USETMAXREG", Vec("usetmaxregAlloc__URb_dealloc", "usetmaxreg__URb_dealloc"))
    val UsetshmszNoOperands: Definition = Definition("UsetshmszNoOperands", "USETSHMSZ", Vec("usetshmsz__FLUSH"))
    val UsetshmszImm: Definition = Definition("UsetshmszImm", "USETSHMSZ", Vec("usetshmsz__Ib"))
    val UsetshmszReg: Definition = Definition("UsetshmszReg", "USETSHMSZ", Vec("usetshmsz__URb"))
    val UsgxtRegRegImm: Definition = Definition("UsgxtRegRegImm", "USGXT", Vec("usgxt__URuIUR_URIR"))
    val UsgxtRegRegReg: Definition = Definition("UsgxtRegRegReg", "USGXT", Vec("usgxt__URURUR_UUU"))
    val UshfRegRegImmReg: Definition = Definition("UshfRegRegImmReg", "USHF", Vec("ushf__URuIUR_URIR"))
    val UshfRegRegRegImm: Definition = Definition("UshfRegRegRegImm", "USHF", Vec("ushf__URURuI_URRI"))
    val UshfRegRegRegReg: Definition = Definition("UshfRegRegRegReg", "USHF", Vec("ushf__URURUR_URURUR"))
    val UshlRegImmReg: Definition = Definition("UshlRegImmReg", "USHL", Vec("ushl_imm_"))
    val UshlRegRegImm: Definition = Definition("UshlRegRegImm", "USHL", Vec("ushl__URuIUR_URIR"))
    val UshlRegRegReg: Definition = Definition("UshlRegRegReg", "USHL", Vec("ushl__URURUR_URURUR"))
    val UshrRegRegImm: Definition = Definition("UshrRegRegImm", "USHR", Vec("ushr__URIR_URIR"))
    val UshrRegRegReg: Definition = Definition("UshrRegRegReg", "USHR", Vec("ushr__URURUR_URURUR"))
    val UstgrAddressReg: Definition = Definition("UstgrAddressReg", "USTGR", Vec("ustgr_", "ustgr_one_"))
    val UtcatomswsPredRegReg: Definition =
      Definition("UtcatomswsPredRegReg", "UTCATOMSWS", Vec("utcatomsws_fas_", "utcatomsws_fas_one_"))
    val UtcatomswsPredRegRegReg: Definition =
      Definition("UtcatomswsPredRegRegReg", "UTCATOMSWS", Vec("utcatomsws_cas_", "utcatomsws_cas_one_"))
    val UtcatomswsRegReg: Definition =
      Definition("UtcatomswsRegReg", "UTCATOMSWS", Vec("utcatomsws_op_", "utcatomsws_op_one_"))
    val UtcbarNoOperands: Definition =
      Definition("UtcbarNoOperands", "UTCBAR", Vec("utcbar_flush_", "utcbar_flush_one_"))
    val UtcbarAddressRegReg: Definition = Definition(
      "UtcbarAddressRegReg",
      "UTCBAR",
      Vec("utcbar__1CTA", "utcbar__2CTA", "utcbar_one__1CTA", "utcbar_one__2CTA")
    )
    val UtccpAddressAddress: Definition = Definition(
      "UtccpAddressAddress",
      "UTCCP",
      Vec("utccp__1CTA", "utccp__2CTA", "utccp_one__1CTA", "utccp_one__2CTA")
    )
    val UtchmmaAddressAddressAddressAddressAddressRegPred: Definition = Definition(
      "UtchmmaAddressAddressAddressAddressAddressRegPred",
      "UTCHMMA",
      Vec(
        "utchmma_1cta__A_gdesc",
        "utchmma_1cta__A_tmem",
        "utchmma_1cta_one__A_gdesc",
        "utchmma_1cta_one__A_tmem",
        "utchmma_2cta__A_gdesc",
        "utchmma_2cta__A_tmem",
        "utchmma_2cta_one__A_gdesc",
        "utchmma_2cta_one__A_tmem"
      )
    )
    val UtchmmaAddressAddressAddressAddressAddressRegPredImm: Definition = Definition(
      "UtchmmaAddressAddressAddressAddressAddressRegPredImm",
      "UTCHMMA",
      Vec(
        "utchmma_1cta__A_gdesc",
        "utchmma_1cta__A_tmem",
        "utchmma_1cta_one__A_gdesc",
        "utchmma_1cta_one__A_tmem",
        "utchmma_2cta__A_gdesc",
        "utchmma_2cta__A_tmem",
        "utchmma_2cta_one__A_gdesc",
        "utchmma_2cta_one__A_tmem"
      )
    )
    val UtcimmaAddressAddressAddressAddressAddressRegPred: Definition = Definition(
      "UtcimmaAddressAddressAddressAddressAddressRegPred",
      "UTCIMMA",
      Vec(
        "utcimma_1cta__A_gdesc",
        "utcimma_1cta__A_tmem",
        "utcimma_1cta_one__A_gdesc",
        "utcimma_1cta_one__A_tmem",
        "utcimma_2cta__A_gdesc",
        "utcimma_2cta__A_tmem",
        "utcimma_2cta_one__A_gdesc",
        "utcimma_2cta_one__A_tmem"
      )
    )
    val UtcommaAddressAddressAddressAddressAddressAddressPred: Definition = Definition(
      "UtcommaAddressAddressAddressAddressAddressAddressPred",
      "UTCOMMA",
      Vec(
        "utcomma_1cta__A_gdesc",
        "utcomma_1cta__A_tmem",
        "utcomma_1cta_one__A_gdesc",
        "utcomma_1cta_one__A_tmem",
        "utcomma_2cta__A_gdesc",
        "utcomma_2cta__A_tmem",
        "utcomma_2cta_one__A_gdesc",
        "utcomma_2cta_one__A_tmem"
      )
    )
    val UtcqmmaAddressAddressAddressAddressAddressAddressPred: Definition = Definition(
      "UtcqmmaAddressAddressAddressAddressAddressAddressPred",
      "UTCQMMA",
      Vec(
        "utcqmma_1cta_scale__A_gdesc",
        "utcqmma_1cta_scale__A_tmem",
        "utcqmma_1cta_scale_one__A_gdesc",
        "utcqmma_1cta_scale_one__A_tmem",
        "utcqmma_2cta_scale__A_gdesc",
        "utcqmma_2cta_scale__A_tmem",
        "utcqmma_2cta_scale_one__A_gdesc",
        "utcqmma_2cta_scale_one__A_tmem"
      )
    )
    val UtcqmmaAddressAddressAddressAddressAddressRegPred: Definition = Definition(
      "UtcqmmaAddressAddressAddressAddressAddressRegPred",
      "UTCQMMA",
      Vec(
        "utcqmma_1cta__A_gdesc",
        "utcqmma_1cta__A_tmem",
        "utcqmma_1cta_one__A_gdesc",
        "utcqmma_1cta_one__A_tmem",
        "utcqmma_2cta__A_gdesc",
        "utcqmma_2cta__A_tmem",
        "utcqmma_2cta_one__A_gdesc",
        "utcqmma_2cta_one__A_tmem"
      )
    )
    val UtcshiftAddress: Definition = Definition(
      "UtcshiftAddress",
      "UTCSHIFT",
      Vec("utcshift__1CTA", "utcshift__2CTA", "utcshift_one__1CTA", "utcshift_one__2CTA")
    )
    val UtmacctlNoOperands: Definition = Definition("UtmacctlNoOperands", "UTMACCTL", Vec("utmacctl_", "utmacctl_one_"))
    val UtmacctlAddress: Definition =
      Definition("UtmacctlAddress", "UTMACCTL", Vec("utmacctl_URa_", "utmacctl_URa_one_"))
    val UtmacmdflushNoOperands: Definition = Definition("UtmacmdflushNoOperands", "UTMACMDFLUSH", Vec("utmacmdflush_"))
    val UtmaldgAddressAddress: Definition =
      Definition("UtmaldgAddressAddress", "UTMALDG", Vec("utmaldg__UUU", "utmaldg_one__UUU"))
    val UtmaldgAddressAddressAddress: Definition =
      Definition("UtmaldgAddressAddressAddress", "UTMALDG", Vec("utmaldg__UUU_desc", "utmaldg_one__UUU_desc"))
    val UtmaldgAddressAddressReg: Definition =
      Definition("UtmaldgAddressAddressReg", "UTMALDG", Vec("utmaldg_URc__UUU", "utmaldg_URc_one__UUU"))
    val UtmaldgAddressAddressRegAddress: Definition = Definition(
      "UtmaldgAddressAddressRegAddress",
      "UTMALDG",
      Vec("utmaldg_URc__UUU_desc", "utmaldg_URc_one__UUU_desc")
    )
    val UtmapfAddressAddress: Definition =
      Definition("UtmapfAddressAddress", "UTMAPF", Vec("utmapf__UUU", "utmapf_one__UUU"))
    val UtmapfAddressAddressAddress: Definition =
      Definition("UtmapfAddressAddressAddress", "UTMAPF", Vec("utmapf__UUU_desc", "utmapf_one__UUU_desc"))
    val UtmapfAddressAddressReg: Definition =
      Definition("UtmapfAddressAddressReg", "UTMAPF", Vec("utmapf_URc__UUU", "utmapf_URc_one__UUU"))
    val UtmapfAddressAddressRegAddress: Definition =
      Definition("UtmapfAddressAddressRegAddress", "UTMAPF", Vec("utmapf_URc__UUU_desc", "utmapf_URc_one__UUU_desc"))
    val UtmaredgAddressAddress: Definition =
      Definition("UtmaredgAddressAddress", "UTMAREDG", Vec("utmaredg__UUU", "utmaredg_one__UUU"))
    val UtmaredgAddressAddressAddress: Definition =
      Definition("UtmaredgAddressAddressAddress", "UTMAREDG", Vec("utmaredg__UUU_desc", "utmaredg_one__UUU_desc"))
    val UtmastgAddressAddress: Definition =
      Definition("UtmastgAddressAddress", "UTMASTG", Vec("utmastg__UUU", "utmastg_one__UUU"))
    val UtmastgAddressAddressAddress: Definition =
      Definition("UtmastgAddressAddressAddress", "UTMASTG", Vec("utmastg__UUU_desc", "utmastg_one__UUU_desc"))
    val definitions: Vec[Definition] = Vec(
      TxdRegRegRegRegRegImm,
      TxdRegRegRegRegRegImmImm,
      TxdRegRegRegRegRegReg,
      TxdRegRegRegRegRegRegImm,
      TxdRegRegRegRegRegRegImmImm,
      TxqRegRegImm,
      TxqRegRegImmImm,
      TxqRegRegImmImmImm,
      TxqRegRegImmImmImmImm,
      TxqRegRegRegImm,
      TxqRegRegRegImmImmImm,
      TxqRegRegRegImmImmImmImm,
      TxqRegRegRegRegImm,
      TxqRegRegRegRegImmImm,
      UblkcpAddressAddressReg,
      UblkcpAddressAddressRegAddress,
      UblkpfAddressReg,
      UblkpfAddressRegAddress,
      UblkredAddressAddressReg,
      UblkredAddressAddressRegAddress,
      UbmskRegRegImm,
      UbmskRegRegReg,
      UbrevRegImm,
      UbrevRegReg,
      UcgabararvNoOperands,
      UcgabargetReg,
      UcgabarsetReg,
      UcgabarwaitNoOperands,
      Ucgabar_ArvNoOperands,
      Ucgabar_WaitNoOperands,
      UcleaRegPredRegImmImm,
      UcleaRegPredRegRegImm,
      Uf2fRegImm,
      Uf2fRegReg,
      Uf2fpRegImm,
      Uf2fpRegImmReg,
      Uf2fpRegReg,
      Uf2fpRegRegImm,
      Uf2fpRegRegImmReg,
      Uf2fpRegRegReg,
      Uf2fpRegRegRegImm,
      Uf2fpRegRegRegReg,
      Uf2iRegImm,
      Uf2iRegReg,
      Uf2ipRegRegImmReg,
      Uf2ipRegRegRegImm,
      Uf2ipRegRegRegReg,
      UfaddRegRegImm,
      UfaddRegRegReg,
      UffmaRegRegImmReg,
      UffmaRegRegRegImm,
      UffmaRegRegRegReg,
      UfhaddRegRegReg,
      UfhfmaRegRegRegReg,
      UfloRegImm,
      UfloRegPredImm,
      UfloRegPredReg,
      UfloRegReg,
      UfmnmxRegPredRegImmPred,
      UfmnmxRegPredRegRegPred,
      UfmnmxRegRegImmPred,
      UfmnmxRegRegRegPred,
      UfmulRegRegImm,
      UfmulRegRegReg,
      UfrndRegImm,
      UfrndRegReg,
      UfselRegRegImmPred,
      UfselRegRegRegPred,
      UfsetRegRegImm,
      UfsetRegRegImmPred,
      UfsetRegRegReg,
      UfsetRegRegRegPred,
      UfsetpPredPredRegImmPred,
      UfsetpPredPredRegRegPred,
      UfsetpPredRegImm,
      UfsetpPredRegReg,
      UgetnextworkidAddressAddress,
      Ui2fRegImm,
      Ui2fRegReg,
      Ui2fpRegImm,
      Ui2fpRegReg,
      Ui2iRegImm,
      Ui2iRegReg,
      Ui2ipRegRegImmReg,
      Ui2ipRegRegRegReg,
      UiabsRegImm,
      UiabsRegReg,
      Uiadd3RegPredPredRegImmReg,
      Uiadd3RegPredPredRegImmRegPredPred,
      Uiadd3RegPredPredRegRegReg,
      Uiadd3RegPredPredRegRegRegPredPred,
      Uiadd3RegPredRegImmReg,
      Uiadd3RegPredRegImmRegPredPred,
      Uiadd3RegPredRegRegReg,
      Uiadd3RegPredRegRegRegPredPred,
      Uiadd3RegRegImmReg,
      Uiadd3RegRegImmRegPredPred,
      Uiadd3RegRegRegReg,
      Uiadd3RegRegRegRegPredPred,
      UimadRegPredRegImmReg,
      UimadRegPredRegImmRegPred,
      UimadRegPredRegRegReg,
      UimadRegPredRegRegRegPred,
      UimadRegRegImmReg,
      UimadRegRegImmRegPred,
      UimadRegRegRegImm,
      UimadRegRegRegImmPred,
      UimadRegRegRegReg,
      UimadRegRegRegRegPred,
      UimnmxPredPredRegRegImmPred,
      UimnmxPredPredRegRegImmPredPred,
      UimnmxPredPredRegRegRegPred,
      UimnmxPredPredRegRegRegPredPred,
      UimnmxPredRegRegImmPred,
      UimnmxPredRegRegImmPredPred,
      UimnmxPredRegRegRegPred,
      UimnmxPredRegRegRegPredPred,
      UimnmxRegRegImmPred,
      UimnmxRegRegImmPredPred,
      UimnmxRegRegRegPred,
      UimnmxRegRegRegPredPred,
      UisetpPredPredRegImmPred,
      UisetpPredPredRegImmPredPred,
      UisetpPredPredRegRegPred,
      UisetpPredPredRegRegPredPred,
      UisetpPredRegImm,
      UisetpPredRegImmPred,
      UisetpPredRegReg,
      UisetpPredRegRegPred,
      UldcRegAddress,
      UldcRegAddressImm,
      UldcRegAddressPred,
      UldcRegConst,
      UleaRegPredRegImmImm,
      UleaRegPredRegImmImmPred,
      UleaRegPredRegImmRegImm,
      UleaRegPredRegImmRegImmPred,
      UleaRegPredRegRegImm,
      UleaRegPredRegRegImmImm,
      UleaRegPredRegRegImmImmPred,
      UleaRegPredRegRegImmPred,
      UleaRegPredRegRegRegImm,
      UleaRegPredRegRegRegImmPred,
      UleaRegRegImmImm,
      UleaRegRegImmImmPred,
      UleaRegRegImmRegImm,
      UleaRegRegImmRegImmPred,
      UleaRegRegRegImm,
      UleaRegRegRegImmImm,
      UleaRegRegRegImmImmPred,
      UleaRegRegRegImmPred,
      UleaRegRegRegRegImm,
      UleaRegRegRegRegImmPred,
      UlepcReg,
      UlepcRegImm,
      UlopPredRegRegImm,
      UlopPredRegRegImmPred,
      UlopPredRegRegReg,
      UlopPredRegRegRegPred,
      UlopRegRegImm,
      UlopRegRegImmPred,
      UlopRegRegReg,
      UlopRegRegRegPred,
      Ulop3PredRegRegImmReg,
      Ulop3PredRegRegImmRegImm,
      Ulop3PredRegRegImmRegImmPred,
      Ulop3PredRegRegImmRegPred,
      Ulop3PredRegRegRegReg,
      Ulop3PredRegRegRegRegImm,
      Ulop3PredRegRegRegRegImmPred,
      Ulop3PredRegRegRegRegPred,
      Ulop3RegRegImmReg,
      Ulop3RegRegImmRegImm,
      Ulop3RegRegImmRegImmPred,
      Ulop3RegRegImmRegPred,
      Ulop3RegRegRegReg,
      Ulop3RegRegRegRegImm,
      Ulop3RegRegRegRegImmPred,
      Ulop3RegRegRegRegPred,
      Ulop32iPredRegRegImm,
      Ulop32iPredRegRegImmPred,
      Ulop32iRegRegImm,
      Ulop32iRegRegImmPred,
      UmemsetsAddressRegReg,
      UmovRegImm,
      UmovRegReg,
      Up2urReg,
      Up2urRegRegImm,
      Up2urRegRegReg,
      Uplop3PredPredPredPred,
      Uplop3PredPredPredPredImm,
      Uplop3PredPredPredPredPredImmImm,
      Uplop3PredPredPredRegPredImmImm,
      Uplop3PredPredPredRegRegImmImm,
      Uplop3PredPredRegPredImm,
      Uplop3PredPredRegRegImm,
      Uplop3PredPredRegRegRegImmImm,
      Uplop3PredRegRegRegImm,
      UpopcRegImm,
      UpopcRegReg,
      UprmtRegRegImmReg,
      UprmtRegRegRegReg,
      UpsetpPredPredPred,
      UpsetpPredPredPredPredPred,
      Ur2upReg,
      Ur2upRegImm,
      Ur2upRegReg,
      UredgrAddressReg,
      UselRegRegImmPred,
      UselRegRegRegPred,
      UsetmaxregImm,
      UsetmaxregPredImm,
      UsetmaxregPredReg,
      UsetmaxregReg,
      UsetshmszNoOperands,
      UsetshmszImm,
      UsetshmszReg,
      UsgxtRegRegImm,
      UsgxtRegRegReg,
      UshfRegRegImmReg,
      UshfRegRegRegImm,
      UshfRegRegRegReg,
      UshlRegImmReg,
      UshlRegRegImm,
      UshlRegRegReg,
      UshrRegRegImm,
      UshrRegRegReg,
      UstgrAddressReg,
      UtcatomswsPredRegReg,
      UtcatomswsPredRegRegReg,
      UtcatomswsRegReg,
      UtcbarNoOperands,
      UtcbarAddressRegReg,
      UtccpAddressAddress,
      UtchmmaAddressAddressAddressAddressAddressRegPred,
      UtchmmaAddressAddressAddressAddressAddressRegPredImm,
      UtcimmaAddressAddressAddressAddressAddressRegPred,
      UtcommaAddressAddressAddressAddressAddressAddressPred,
      UtcqmmaAddressAddressAddressAddressAddressAddressPred,
      UtcqmmaAddressAddressAddressAddressAddressRegPred,
      UtcshiftAddress,
      UtmacctlNoOperands,
      UtmacctlAddress,
      UtmacmdflushNoOperands,
      UtmaldgAddressAddress,
      UtmaldgAddressAddressAddress,
      UtmaldgAddressAddressReg,
      UtmaldgAddressAddressRegAddress,
      UtmapfAddressAddress,
      UtmapfAddressAddressAddress,
      UtmapfAddressAddressReg,
      UtmapfAddressAddressRegAddress,
      UtmaredgAddressAddress,
      UtmaredgAddressAddressAddress,
      UtmastgAddressAddress,
      UtmastgAddressAddressAddress
    )
  }
  object Definitions6 {
    val UviaddRegRegImm: Definition = Definition("UviaddRegRegImm", "UVIADD", Vec("uviadd__URuIUR_URIR"))
    val UviaddRegRegReg: Definition = Definition("UviaddRegRegReg", "UVIADD", Vec("uviadd__URURUR_UUU"))
    val UvimnmxRegRegImmPred: Definition = Definition("UvimnmxRegRegImmPred", "UVIMNMX", Vec("uvimnmx__URIR_URsIUR"))
    val UvimnmxRegRegRegPred: Definition = Definition("UvimnmxRegRegRegPred", "UVIMNMX", Vec("uvimnmx__UUU_URURUR"))
    val UvirtcountImm: Definition =
      Definition("UvirtcountImm", "UVIRTCOUNT", Vec("uvirtcount__imm", "uvirtcount_one__imm"))
    val UvirtcountReg: Definition =
      Definition("UvirtcountReg", "UVIRTCOUNT", Vec("uvirtcount__UR", "uvirtcount_one__UR"))
    val VabsdiffRegPredRegAddressImmReg: Definition =
      Definition("VabsdiffRegPredRegAddressImmReg", "VABSDIFF", Vec("vabsdiff__RCxR_RCxR"))
    val VabsdiffRegPredRegConstReg: Definition =
      Definition("VabsdiffRegPredRegConstReg", "VABSDIFF", Vec("vabsdiff__RCR_RCR"))
    val VabsdiffRegPredRegImmReg: Definition =
      Definition("VabsdiffRegPredRegImmReg", "VABSDIFF", Vec("vabsdiff__RuIR_RIR"))
    val VabsdiffRegPredRegRegAddressImm: Definition =
      Definition("VabsdiffRegPredRegRegAddressImm", "VABSDIFF", Vec("vabsdiff__RRCx_RRCx"))
    val VabsdiffRegPredRegRegConst: Definition =
      Definition("VabsdiffRegPredRegRegConst", "VABSDIFF", Vec("vabsdiff__RRC_RRC"))
    val VabsdiffRegPredRegRegImm: Definition =
      Definition("VabsdiffRegPredRegRegImm", "VABSDIFF", Vec("vabsdiff__RRuI_RRI"))
    val VabsdiffRegPredRegRegReg: Definition = Definition(
      "VabsdiffRegPredRegRegReg",
      "VABSDIFF",
      Vec("vabsdiff__RRR_RRR", "vabsdiff__RRU_RRU", "vabsdiff__RUR_RUR")
    )
    val VabsdiffRegRegAddressImmReg: Definition =
      Definition("VabsdiffRegRegAddressImmReg", "VABSDIFF", Vec("vabsdiff__RCxR_RCxR"))
    val VabsdiffRegRegConstReg: Definition = Definition("VabsdiffRegRegConstReg", "VABSDIFF", Vec("vabsdiff__RCR_RCR"))
    val VabsdiffRegRegImmReg: Definition = Definition(
      "VabsdiffRegRegImmReg",
      "VABSDIFF",
      Vec(
        "Imm VABSDIFF",
        "Imm_VABSDIFF",
        "SImm_VABSDIFF",
        "a32 Imm VABSDIFF",
        "a32_Imm_VABSDIFF",
        "a32_SImm_VABSDIFF",
        "a8 Imm VABSDIFF",
        "a8_Imm_VABSDIFF",
        "a8_SImm_VABSDIFF",
        "vabsdiff__RuIR_RIR"
      )
    )
    val VabsdiffRegRegRegAddressImm: Definition =
      Definition("VabsdiffRegRegRegAddressImm", "VABSDIFF", Vec("vabsdiff__RRCx_RRCx"))
    val VabsdiffRegRegRegConst: Definition = Definition("VabsdiffRegRegRegConst", "VABSDIFF", Vec("vabsdiff__RRC_RRC"))
    val VabsdiffRegRegRegImm: Definition = Definition("VabsdiffRegRegRegImm", "VABSDIFF", Vec("vabsdiff__RRuI_RRI"))
    val VabsdiffRegRegRegReg: Definition = Definition(
      "VabsdiffRegRegRegReg",
      "VABSDIFF",
      Vec(
        "VABSDIFF",
        "a32 VABSDIFF",
        "a32 b32 VABSDIFF",
        "a32 b8 VABSDIFF",
        "a32_VABSDIFF",
        "a32_b32_VABSDIFF",
        "a32_b8_VABSDIFF",
        "a8 VABSDIFF",
        "a8 b32 VABSDIFF",
        "a8 b8 VABSDIFF",
        "a8_VABSDIFF",
        "a8_b32_VABSDIFF",
        "a8_b8_VABSDIFF",
        "b32 VABSDIFF",
        "b32_VABSDIFF",
        "b8 VABSDIFF",
        "b8_VABSDIFF",
        "vabsdiff__RRR_RRR",
        "vabsdiff__RRU_RRU",
        "vabsdiff__RUR_RUR"
      )
    )
    val Vabsdiff2RegRegImmReg: Definition = Definition("Vabsdiff2RegRegImmReg", "VABSDIFF2", Vec("Imm VABSDIFF2"))
    val Vabsdiff2RegRegRegReg: Definition = Definition("Vabsdiff2RegRegRegReg", "VABSDIFF2", Vec("VABSDIFF2"))
    val Vabsdiff4RegPredRegAddressImmReg: Definition =
      Definition("Vabsdiff4RegPredRegAddressImmReg", "VABSDIFF4", Vec("vabsdiff4__RCxR_RCxR"))
    val Vabsdiff4RegPredRegConstReg: Definition =
      Definition("Vabsdiff4RegPredRegConstReg", "VABSDIFF4", Vec("vabsdiff4__RCR_RCR"))
    val Vabsdiff4RegPredRegImmReg: Definition =
      Definition("Vabsdiff4RegPredRegImmReg", "VABSDIFF4", Vec("vabsdiff4__RuIR_RIR"))
    val Vabsdiff4RegPredRegRegAddressImm: Definition =
      Definition("Vabsdiff4RegPredRegRegAddressImm", "VABSDIFF4", Vec("vabsdiff4__RRCx_RRCx"))
    val Vabsdiff4RegPredRegRegConst: Definition =
      Definition("Vabsdiff4RegPredRegRegConst", "VABSDIFF4", Vec("vabsdiff4__RRC_RRC"))
    val Vabsdiff4RegPredRegRegImm: Definition =
      Definition("Vabsdiff4RegPredRegRegImm", "VABSDIFF4", Vec("vabsdiff4__RRuI_RRI"))
    val Vabsdiff4RegPredRegRegReg: Definition = Definition(
      "Vabsdiff4RegPredRegRegReg",
      "VABSDIFF4",
      Vec("vabsdiff4__RRR_RRR", "vabsdiff4__RRU_RRU", "vabsdiff4__RUR_RUR")
    )
    val Vabsdiff4RegRegAddressImmReg: Definition =
      Definition("Vabsdiff4RegRegAddressImmReg", "VABSDIFF4", Vec("vabsdiff4__RCxR_RCxR"))
    val Vabsdiff4RegRegConstReg: Definition =
      Definition("Vabsdiff4RegRegConstReg", "VABSDIFF4", Vec("vabsdiff4__RCR_RCR"))
    val Vabsdiff4RegRegImmReg: Definition = Definition(
      "Vabsdiff4RegRegImmReg",
      "VABSDIFF4",
      Vec("Imm VABSDIFF4", "Imm_VABSDIFF4", "SImm_VABSDIFF4", "vabsdiff4__RuIR_RIR")
    )
    val Vabsdiff4RegRegRegAddressImm: Definition =
      Definition("Vabsdiff4RegRegRegAddressImm", "VABSDIFF4", Vec("vabsdiff4__RRCx_RRCx"))
    val Vabsdiff4RegRegRegConst: Definition =
      Definition("Vabsdiff4RegRegRegConst", "VABSDIFF4", Vec("vabsdiff4__RRC_RRC"))
    val Vabsdiff4RegRegRegImm: Definition = Definition("Vabsdiff4RegRegRegImm", "VABSDIFF4", Vec("vabsdiff4__RRuI_RRI"))
    val Vabsdiff4RegRegRegReg: Definition = Definition(
      "Vabsdiff4RegRegRegReg",
      "VABSDIFF4",
      Vec("VABSDIFF4", "vabsdiff4__RRR_RRR", "vabsdiff4__RRU_RRU", "vabsdiff4__RUR_RUR")
    )
    val VaddRegRegImmReg: Definition = Definition(
      "VaddRegRegImmReg",
      "VADD",
      Vec(
        "Imm VADD",
        "Imm_VADD",
        "SImm_VADD",
        "a32 Imm VADD",
        "a32_Imm_VADD",
        "a32_SImm_VADD",
        "a8 Imm VADD",
        "a8_Imm_VADD",
        "a8_SImm_VADD"
      )
    )
    val VaddRegRegRegReg: Definition = Definition(
      "VaddRegRegRegReg",
      "VADD",
      Vec(
        "VADD",
        "a32 VADD",
        "a32 b32 VADD",
        "a32 b8 VADD",
        "a32_VADD",
        "a32_b32_VADD",
        "a32_b8_VADD",
        "a8 VADD",
        "a8 b32 VADD",
        "a8 b8 VADD",
        "a8_VADD",
        "a8_b32_VADD",
        "a8_b8_VADD",
        "b32 VADD",
        "b32_VADD",
        "b8 VADD",
        "b8_VADD"
      )
    )
    val Vadd2RegRegImmReg: Definition = Definition("Vadd2RegRegImmReg", "VADD2", Vec("Imm VADD2"))
    val Vadd2RegRegRegReg: Definition = Definition("Vadd2RegRegRegReg", "VADD2", Vec("VADD2"))
    val Vadd4RegRegImmReg: Definition = Definition("Vadd4RegRegImmReg", "VADD4", Vec("Imm VADD4"))
    val Vadd4RegRegRegReg: Definition = Definition("Vadd4RegRegRegReg", "VADD4", Vec("VADD4"))
    val VhmnmxRegRegAddressImmRegPred: Definition =
      Definition("VhmnmxRegRegAddressImmRegPred", "VHMNMX", Vec("vhmnmx__RCxR"))
    val VhmnmxRegRegConstRegPred: Definition = Definition("VhmnmxRegRegConstRegPred", "VHMNMX", Vec("vhmnmx__RCR"))
    val VhmnmxRegRegImmImmRegPred: Definition = Definition("VhmnmxRegRegImmImmRegPred", "VHMNMX", Vec("vhmnmx__RIR"))
    val VhmnmxRegRegRegRegPred: Definition =
      Definition("VhmnmxRegRegRegRegPred", "VHMNMX", Vec("vhmnmx__RRR", "vhmnmx__RUR"))
    val ViaddRegRegAddressImm: Definition = Definition("ViaddRegRegAddressImm", "VIADD", Vec("viadd__RCxR_RCxR"))
    val ViaddRegRegConst: Definition = Definition("ViaddRegRegConst", "VIADD", Vec("viadd__RCR_RCR"))
    val ViaddRegRegImm: Definition = Definition("ViaddRegRegImm", "VIADD", Vec("viadd__RuIR_RIR"))
    val ViaddRegRegReg: Definition = Definition("ViaddRegRegReg", "VIADD", Vec("viadd__RRR_RRR", "viadd__RUR_RUR"))
    val ViaddmnmxRegRegAddressImmRegPred: Definition =
      Definition("ViaddmnmxRegRegAddressImmRegPred", "VIADDMNMX", Vec("viaddmnmx__RCxR_RCxR"))
    val ViaddmnmxRegRegConstRegPred: Definition =
      Definition("ViaddmnmxRegRegConstRegPred", "VIADDMNMX", Vec("viaddmnmx__RCR_RCR"))
    val ViaddmnmxRegRegImmRegPred: Definition =
      Definition("ViaddmnmxRegRegImmRegPred", "VIADDMNMX", Vec("viaddmnmx__RuIR_RIR"))
    val ViaddmnmxRegRegRegAddressImmPred: Definition =
      Definition("ViaddmnmxRegRegRegAddressImmPred", "VIADDMNMX", Vec("viaddmnmx__RRCx_RRCx"))
    val ViaddmnmxRegRegRegConstPred: Definition =
      Definition("ViaddmnmxRegRegRegConstPred", "VIADDMNMX", Vec("viaddmnmx__RRC_RRC"))
    val ViaddmnmxRegRegRegImmPred: Definition =
      Definition("ViaddmnmxRegRegRegImmPred", "VIADDMNMX", Vec("viaddmnmx__RRuI_RRI"))
    val ViaddmnmxRegRegRegRegPred: Definition = Definition(
      "ViaddmnmxRegRegRegRegPred",
      "VIADDMNMX",
      Vec("viaddmnmx__RRR_RRR", "viaddmnmx__RRU_RRU", "viaddmnmx__RUR_RUR")
    )
    val VildRegAddress: Definition = Definition("VildRegAddress", "VILD", Vec("I VILD", "VILD"))
    val VimnmxRegPredPredRegAddressImmPred: Definition =
      Definition("VimnmxRegPredPredRegAddressImmPred", "VIMNMX", Vec("vimnmx_pred__RCxR_RCxR"))
    val VimnmxRegPredPredRegConstPred: Definition =
      Definition("VimnmxRegPredPredRegConstPred", "VIMNMX", Vec("vimnmx_pred__RCR_RCR"))
    val VimnmxRegPredPredRegImmPred: Definition =
      Definition("VimnmxRegPredPredRegImmPred", "VIMNMX", Vec("vimnmx__RIR_RsIR", "vimnmx_pred__RIR_RsIR"))
    val VimnmxRegPredPredRegRegPred: Definition = Definition(
      "VimnmxRegPredPredRegRegPred",
      "VIMNMX",
      Vec("vimnmx__RRR_RRR", "vimnmx__RUR_RUR", "vimnmx_pred__RRR_RRR", "vimnmx_pred__RUR_RUR")
    )
    val VimnmxRegRegAddressImmPred: Definition =
      Definition("VimnmxRegRegAddressImmPred", "VIMNMX", Vec("vimnmx__RCxR_RCxR"))
    val VimnmxRegRegConstPred: Definition = Definition("VimnmxRegRegConstPred", "VIMNMX", Vec("vimnmx__RCR_RCR"))
    val VimnmxRegRegImmPred: Definition =
      Definition("VimnmxRegRegImmPred", "VIMNMX", Vec("vimnmx__RIR_RsIR", "vimnmx_nopred__RIR_RsIR"))
    val VimnmxRegRegRegPred: Definition = Definition(
      "VimnmxRegRegRegPred",
      "VIMNMX",
      Vec("vimnmx__RRR_RRR", "vimnmx__RUR_RUR", "vimnmx_nopred__RRR_RRR", "vimnmx_nopred__RUR_RUR")
    )
    val Vimnmx3RegRegAddressImmRegPred: Definition =
      Definition("Vimnmx3RegRegAddressImmRegPred", "VIMNMX3", Vec("vimnmx3__RCxR_RCxR"))
    val Vimnmx3RegRegConstRegPred: Definition =
      Definition("Vimnmx3RegRegConstRegPred", "VIMNMX3", Vec("vimnmx3__RCR_RCR"))
    val Vimnmx3RegRegImmRegPred: Definition = Definition("Vimnmx3RegRegImmRegPred", "VIMNMX3", Vec("vimnmx3__RuIR_RIR"))
    val Vimnmx3RegRegRegRegPred: Definition =
      Definition("Vimnmx3RegRegRegRegPred", "VIMNMX3", Vec("vimnmx3__RRR_RRR", "vimnmx3__RUR_RUR"))
    val VmadRegRegImmReg: Definition = Definition(
      "VmadRegRegImmReg",
      "VMAD",
      Vec(
        "Imm VMAD",
        "Imm_VMAD",
        "SImm_VMAD",
        "a32 Imm VMAD",
        "a32_Imm_VMAD",
        "a32_SImm_VMAD",
        "a8 Imm VMAD",
        "a8_Imm_VMAD",
        "a8_SImm_VMAD"
      )
    )
    val VmadRegRegRegReg: Definition = Definition(
      "VmadRegRegRegReg",
      "VMAD",
      Vec(
        "VMAD",
        "a32 VMAD",
        "a32 b32 VMAD",
        "a32 b8 VMAD",
        "a32_VMAD",
        "a32_b32_VMAD",
        "a32_b8_VMAD",
        "a8 VMAD",
        "a8 b32 VMAD",
        "a8 b8 VMAD",
        "a8_VMAD",
        "a8_b32_VMAD",
        "a8_b8_VMAD",
        "b32 VMAD",
        "b32_VMAD",
        "b8 VMAD",
        "b8_VMAD"
      )
    )
    val VmnmxRegRegImmReg: Definition = Definition(
      "VmnmxRegRegImmReg",
      "VMNMX",
      Vec(
        "Imm VMNMX",
        "Imm_VMNMX",
        "SImm_VMNMX",
        "a32 Imm VMNMX",
        "a32_Imm_VMNMX",
        "a32_SImm_VMNMX",
        "a8 Imm VMNMX",
        "a8_Imm_VMNMX",
        "a8_SImm_VMNMX"
      )
    )
    val VmnmxRegRegRegReg: Definition = Definition(
      "VmnmxRegRegRegReg",
      "VMNMX",
      Vec(
        "VMNMX",
        "a32 VMNMX",
        "a32 b32 VMNMX",
        "a32 b8 VMNMX",
        "a32_VMNMX",
        "a32_b32_VMNMX",
        "a32_b8_VMNMX",
        "a8 VMNMX",
        "a8 b32 VMNMX",
        "a8 b8 VMNMX",
        "a8_VMNMX",
        "a8_b32_VMNMX",
        "a8_b8_VMNMX",
        "b32 VMNMX",
        "b32_VMNMX",
        "b8 VMNMX",
        "b8_VMNMX"
      )
    )
    val Vmnmx2RegRegImmReg: Definition = Definition("Vmnmx2RegRegImmReg", "VMNMX2", Vec("Imm VMNMX2"))
    val Vmnmx2RegRegRegReg: Definition = Definition("Vmnmx2RegRegRegReg", "VMNMX2", Vec("VMNMX2"))
    val Vmnmx4RegRegImmReg: Definition = Definition("Vmnmx4RegRegImmReg", "VMNMX4", Vec("Imm VMNMX4"))
    val Vmnmx4RegRegRegReg: Definition = Definition("Vmnmx4RegRegRegReg", "VMNMX4", Vec("VMNMX4"))
    val VoteImm: Definition = Definition("VoteImm", "VOTE", Vec("VOTE.VTG"))
    val VotePredPred: Definition = Definition("VotePredPred", "VOTE", Vec("VOTE"))
    val VoteRegPredPred: Definition = Definition("VoteRegPredPred", "VOTE", Vec("VOTE", "vote_"))
    val VoteuRegPredPred: Definition = Definition("VoteuRegPredPred", "VOTEU", Vec("voteu_"))
    val VoteVtgImm: Definition = Definition("VoteVtgImm", "VOTE_VTG", Vec("vote_vtg_"))
    val VoteVtgPredImm: Definition = Definition("VoteVtgPredImm", "VOTE_VTG", Vec("vote_vtg_cmp_"))
    val VoteVtgPredPredImmPred: Definition = Definition("VoteVtgPredPredImmPred", "VOTE_VTG", Vec("vote_vtg_bop_"))
    val Vsel2RegRegImmPred: Definition = Definition("Vsel2RegRegImmPred", "VSEL2", Vec("Imm VSEL2"))
    val Vsel2RegRegRegPred: Definition = Definition("Vsel2RegRegRegPred", "VSEL2", Vec("VSEL2"))
    val Vsel4RegRegImmPred: Definition = Definition("Vsel4RegRegImmPred", "VSEL4", Vec("Imm VSEL4"))
    val Vsel4RegRegRegPred: Definition = Definition("Vsel4RegRegRegPred", "VSEL4", Vec("VSEL4"))
    val VsetRegRegImmReg: Definition = Definition(
      "VsetRegRegImmReg",
      "VSET",
      Vec(
        "Imm VSET",
        "Imm_VSET",
        "SImm_VSET",
        "a32 Imm VSET",
        "a32_Imm_VSET",
        "a32_SImm_VSET",
        "a8 Imm VSET",
        "a8_Imm_VSET",
        "a8_SImm_VSET"
      )
    )
    val VsetRegRegRegReg: Definition = Definition(
      "VsetRegRegRegReg",
      "VSET",
      Vec(
        "VSET",
        "a32 VSET",
        "a32 b32 VSET",
        "a32 b8 VSET",
        "a32_VSET",
        "a32_b32_VSET",
        "a32_b8_VSET",
        "a8 VSET",
        "a8 b32 VSET",
        "a8 b8 VSET",
        "a8_VSET",
        "a8_b32_VSET",
        "a8_b8_VSET",
        "b32 VSET",
        "b32_VSET",
        "b8 VSET",
        "b8_VSET"
      )
    )
    val Vset2RegRegImmReg: Definition = Definition("Vset2RegRegImmReg", "VSET2", Vec("Imm VSET2"))
    val Vset2RegRegRegReg: Definition = Definition("Vset2RegRegRegReg", "VSET2", Vec("VSET2"))
    val Vset4RegRegImmReg: Definition = Definition("Vset4RegRegImmReg", "VSET4", Vec("Imm VSET4"))
    val Vset4RegRegRegReg: Definition = Definition("Vset4RegRegRegReg", "VSET4", Vec("VSET4"))
    val VsetpPredPredRegImmPred: Definition = Definition(
      "VsetpPredPredRegImmPred",
      "VSETP",
      Vec(
        "Imm VSETP",
        "Imm_VSETP_bop",
        "SImm_VSETP_bop",
        "a32 Imm VSETP",
        "a32_Imm_VSETP_bop",
        "a32_SImm_VSETP_bop",
        "a8 Imm VSETP",
        "a8_Imm_VSETP_bop",
        "a8_SImm_VSETP_bop"
      )
    )
    val VsetpPredPredRegRegPred: Definition = Definition(
      "VsetpPredPredRegRegPred",
      "VSETP",
      Vec(
        "VSETP",
        "VSETP_bop",
        "a32 VSETP",
        "a32 b32 VSETP",
        "a32 b8 VSETP",
        "a32_VSETP_bop",
        "a32_b32_VSETP_bop",
        "a32_b8_VSETP_bop",
        "a8 VSETP",
        "a8 b32 VSETP",
        "a8 b8 VSETP",
        "a8_VSETP_bop",
        "a8_b32_VSETP_bop",
        "a8_b8_VSETP_bop",
        "b32 VSETP",
        "b32_VSETP_bop",
        "b8 VSETP",
        "b8_VSETP_bop"
      )
    )
    val VsetpPredRegImm: Definition = Definition(
      "VsetpPredRegImm",
      "VSETP",
      Vec(
        "Imm_VSETP",
        "NoBop Imm VSETP",
        "NoBop a32 Imm VSETP",
        "NoBop a8 Imm VSETP",
        "SImm_VSETP",
        "a32_Imm_VSETP",
        "a32_SImm_VSETP",
        "a8_Imm_VSETP",
        "a8_SImm_VSETP"
      )
    )
    val VsetpPredRegReg: Definition = Definition(
      "VsetpPredRegReg",
      "VSETP",
      Vec(
        "NoBop VSETP",
        "NoBop a32 VSETP",
        "NoBop a32 b32 VSETP",
        "NoBop a32 b8 VSETP",
        "NoBop a8 VSETP",
        "NoBop a8 b32 VSETP",
        "NoBop a8 b8 VSETP",
        "NoBop b32 VSETP",
        "NoBop b8 VSETP",
        "VSETP",
        "a32_VSETP",
        "a32_b32_VSETP",
        "a32_b8_VSETP",
        "a8_VSETP",
        "a8_b32_VSETP",
        "a8_b8_VSETP",
        "b32_VSETP",
        "b8_VSETP"
      )
    )
    val VshlRegRegImmReg: Definition = Definition(
      "VshlRegRegImmReg",
      "VSHL",
      Vec(
        "Imm VSHL",
        "Imm_VSHL",
        "SImm_VSHL",
        "a32 Imm VSHL",
        "a32_Imm_VSHL",
        "a32_SImm_VSHL",
        "a8 Imm VSHL",
        "a8_Imm_VSHL",
        "a8_SImm_VSHL"
      )
    )
    val VshlRegRegRegReg: Definition = Definition(
      "VshlRegRegRegReg",
      "VSHL",
      Vec(
        "VSHL",
        "a32 VSHL",
        "a32 b32 VSHL",
        "a32 b8 VSHL",
        "a32_VSHL",
        "a32_b32_VSHL",
        "a32_b8_VSHL",
        "a8 VSHL",
        "a8 b32 VSHL",
        "a8 b8 VSHL",
        "a8_VSHL",
        "a8_b32_VSHL",
        "a8_b8_VSHL",
        "b32 VSHL",
        "b32_VSHL",
        "b8 VSHL",
        "b8_VSHL"
      )
    )
    val VshrRegRegImmReg: Definition = Definition(
      "VshrRegRegImmReg",
      "VSHR",
      Vec(
        "Imm VSHL",
        "Imm_VSHR",
        "SImm_VSHR",
        "a32 Imm VSHL",
        "a32_Imm_VSHR",
        "a32_SImm_VSHR",
        "a8 Imm VSHL",
        "a8_Imm_VSHR",
        "a8_SImm_VSHR"
      )
    )
    val VshrRegRegRegReg: Definition = Definition(
      "VshrRegRegRegReg",
      "VSHR",
      Vec(
        "VSHL",
        "VSHR",
        "a32 VSHL",
        "a32 b32 VSHL",
        "a32 b8 VSHL",
        "a32_VSHR",
        "a32_b32_VSHR",
        "a32_b8_VSHR",
        "a8 VSHL",
        "a8 b32 VSHL",
        "a8 b8 VSHL",
        "a8_VSHR",
        "a8_b32_VSHR",
        "a8_b8_VSHR",
        "b32 VSHL",
        "b32_VSHR",
        "b8 VSHL",
        "b8_VSHR"
      )
    )
    val WarpgroupNoOperands: Definition =
      Definition("WarpgroupNoOperands", "WARPGROUP", Vec("warpgroup_arrive_", "warpgroup_wait_"))
    val WarpgroupImm: Definition = Definition("WarpgroupImm", "WARPGROUP", Vec("warpgroup_depbar_"))
    val WarpgroupsetReg: Definition =
      Definition("WarpgroupsetReg", "WARPGROUPSET", Vec("warpgroupset__AC", "warpgroupset__SB"))
    val WarpsyncNoOperands: Definition = Definition("WarpsyncNoOperands", "WARPSYNC", Vec("warpsync__RIR"))
    val WarpsyncAddressImm: Definition = Definition("WarpsyncAddressImm", "WARPSYNC", Vec("warpsync__RCXR"))
    val WarpsyncConst: Definition = Definition("WarpsyncConst", "WARPSYNC", Vec("warpsync__RCR"))
    val WarpsyncImm: Definition =
      Definition("WarpsyncImm", "WARPSYNC", Vec("warpsync__RIR", "warpsync_collective__RIR", "warpsync_rel__RIR"))
    val WarpsyncPred: Definition = Definition("WarpsyncPred", "WARPSYNC", Vec("warpsync__RIR"))
    val WarpsyncPredAddressImm: Definition = Definition("WarpsyncPredAddressImm", "WARPSYNC", Vec("warpsync__RCXR"))
    val WarpsyncPredConst: Definition = Definition("WarpsyncPredConst", "WARPSYNC", Vec("warpsync__RCR"))
    val WarpsyncPredImm: Definition =
      Definition("WarpsyncPredImm", "WARPSYNC", Vec("warpsync__RIR", "warpsync_collective__RIR", "warpsync_rel__RIR"))
    val WarpsyncPredReg: Definition = Definition("WarpsyncPredReg", "WARPSYNC", Vec("warpsync__RRR", "warpsync__RUR"))
    val WarpsyncPredRegImm: Definition =
      Definition("WarpsyncPredRegImm", "WARPSYNC", Vec("warpsync_collective__RRR", "warpsync_rel__RRR"))
    val WarpsyncReg: Definition = Definition("WarpsyncReg", "WARPSYNC", Vec("warpsync__RRR", "warpsync__RUR"))
    val WarpsyncRegImm: Definition =
      Definition("WarpsyncRegImm", "WARPSYNC", Vec("warpsync_collective__RRR", "warpsync_rel__RRR"))
    val XmadRegRegConstReg: Definition = Definition("XmadRegRegConstReg", "XMAD", Vec("ConstB_XMAD"))
    val XmadRegRegImmReg: Definition = Definition("XmadRegRegImmReg", "XMAD", Vec("ImmB_XMAD", "SImmB_XMAD"))
    val XmadRegRegRegConst: Definition = Definition("XmadRegRegRegConst", "XMAD", Vec("ConstC_XMAD"))
    val XmadRegRegRegReg: Definition = Definition("XmadRegRegRegReg", "XMAD", Vec("XMAD"))
    val YieldNoOperands: Definition = Definition("YieldNoOperands", "YIELD", Vec("yield_inst_"))
    val YieldPred: Definition = Definition("YieldPred", "YIELD", Vec("yield_inst_"))
    val Hir0x002NoOperands: Definition = Definition("Hir0x002NoOperands", "__HIR0X002", Vec("ttucctl_"))
    val Hir0x003NoOperands: Definition = Definition("Hir0x003NoOperands", "__HIR0X003", Vec("ttuclose_"))
    val Hir0x003Pred: Definition = Definition("Hir0x003Pred", "__HIR0X003", Vec("ttuclose_"))
    val Hir0x004NoOperands: Definition = Definition("Hir0x004NoOperands", "__HIR0X004", Vec("ttugo_"))
    val Hir0x005PredRegRegImm: Definition =
      Definition("Hir0x005PredRegRegImm", "__HIR0X005", Vec("ttuld_", "ttuld__close", "ttuld__no_close"))
    val Hir0x005RegRegImm: Definition =
      Definition("Hir0x005RegRegImm", "__HIR0X005", Vec("ttuld_", "ttuld__close", "ttuld__no_close"))
    val Hir0x006Imm: Definition = Definition("Hir0x006Imm", "__HIR0X006", Vec("ttumacrofuse_"))
    val Hir0x007NoOperands: Definition = Definition("Hir0x007NoOperands", "__HIR0X007", Vec("ttuopen_"))
    val Hir0x008ImmRegReg: Definition = Definition("Hir0x008ImmRegReg", "__HIR0X008", Vec("ttust_"))
    val Hir0x020NoOperands: Definition = Definition("Hir0x020NoOperands", "__HIR0X020", Vec("ide_"))
    val Hir0x020Imm: Definition = Definition("Hir0x020Imm", "__HIR0X020", Vec("ide_"))
    val Hir0x120AddressImm: Definition = Definition("Hir0x120AddressImm", "__HIR0X120", Vec("nanotrap__CX"))
    val Hir0x120Const: Definition = Definition("Hir0x120Const", "__HIR0X120", Vec("nanotrap__C"))
    val Hir0x120Imm: Definition = Definition("Hir0x120Imm", "__HIR0X120", Vec("nanotrap__I"))
    val Hir0x120PredAddressImm: Definition = Definition("Hir0x120PredAddressImm", "__HIR0X120", Vec("nanotrap__CX"))
    val Hir0x120PredConst: Definition = Definition("Hir0x120PredConst", "__HIR0X120", Vec("nanotrap__C"))
    val Hir0x120PredImm: Definition = Definition("Hir0x120PredImm", "__HIR0X120", Vec("nanotrap__I"))
    val Hir0x120PredReg: Definition = Definition("Hir0x120PredReg", "__HIR0X120", Vec("nanotrap__R", "nanotrap__U"))
    val Hir0x120Reg: Definition = Definition("Hir0x120Reg", "__HIR0X120", Vec("nanotrap__R", "nanotrap__U"))
    val Hir0x130AddressAddressAddressAddressAddressAddressPred: Definition = Definition(
      "Hir0x130AddressAddressAddressAddressAddressAddressPred",
      "__HIR0X130",
      Vec(
        "utcmxqmma_1cta_scale__A_gdesc",
        "utcmxqmma_1cta_scale__A_tmem",
        "utcmxqmma_1cta_scale_one__A_gdesc",
        "utcmxqmma_1cta_scale_one__A_tmem",
        "utcmxqmma_2cta_scale__A_gdesc",
        "utcmxqmma_2cta_scale__A_tmem",
        "utcmxqmma_2cta_scale_one__A_gdesc",
        "utcmxqmma_2cta_scale_one__A_tmem"
      )
    )
    val Hir0x150RegRegRegRegPredRegRegReg: Definition =
      Definition("Hir0x150RegRegRegRegPredRegRegReg", "__HIR0X150", Vec("mxqmma_scale_"))
    val Hir0x150RegRegRegRegRegRegReg: Definition =
      Definition("Hir0x150RegRegRegRegRegRegReg", "__HIR0X150", Vec("mxqmma_scale_"))
    val Hir0x160Reg: Definition = Definition("Hir0x160Reg", "__HIR0X160", Vec("utcstsws_", "utcstsws_one_"))
    val Hir0x170Reg: Definition = Definition("Hir0x170Reg", "__HIR0X170", Vec("utcldsws_", "utcldsws_one_"))
    val Hir0x1a0RegRegRegRegImmImm: Definition =
      Definition("Hir0x1a0RegRegRegRegImmImm", "__HIR0X1A0", Vec("genmetadata_"))
    val Hir0x1b0RegRegRegReg: Definition = Definition("Hir0x1b0RegRegRegReg", "__HIR0X1B0", Vec("gather_bitextract_"))
    val Hir0x1e0NoOperands: Definition = Definition("Hir0x1e0NoOperands", "__HIR0X1E0", Vec("rtt_"))
    val Hir0x1f0ImmReg: Definition = Definition("Hir0x1f0ImmReg", "__HIR0X1F0", Vec("r2b_"))
    val Hir0x1f2Reg: Definition = Definition("Hir0x1f2Reg", "__HIR0X1F2", Vec("ucgabar_get_"))
    val Hir0x1f3Reg: Definition = Definition("Hir0x1f3Reg", "__HIR0X1F3", Vec("ucgabar_set_"))
    val definitions: Vec[Definition] = Vec(
      UviaddRegRegImm,
      UviaddRegRegReg,
      UvimnmxRegRegImmPred,
      UvimnmxRegRegRegPred,
      UvirtcountImm,
      UvirtcountReg,
      VabsdiffRegPredRegAddressImmReg,
      VabsdiffRegPredRegConstReg,
      VabsdiffRegPredRegImmReg,
      VabsdiffRegPredRegRegAddressImm,
      VabsdiffRegPredRegRegConst,
      VabsdiffRegPredRegRegImm,
      VabsdiffRegPredRegRegReg,
      VabsdiffRegRegAddressImmReg,
      VabsdiffRegRegConstReg,
      VabsdiffRegRegImmReg,
      VabsdiffRegRegRegAddressImm,
      VabsdiffRegRegRegConst,
      VabsdiffRegRegRegImm,
      VabsdiffRegRegRegReg,
      Vabsdiff2RegRegImmReg,
      Vabsdiff2RegRegRegReg,
      Vabsdiff4RegPredRegAddressImmReg,
      Vabsdiff4RegPredRegConstReg,
      Vabsdiff4RegPredRegImmReg,
      Vabsdiff4RegPredRegRegAddressImm,
      Vabsdiff4RegPredRegRegConst,
      Vabsdiff4RegPredRegRegImm,
      Vabsdiff4RegPredRegRegReg,
      Vabsdiff4RegRegAddressImmReg,
      Vabsdiff4RegRegConstReg,
      Vabsdiff4RegRegImmReg,
      Vabsdiff4RegRegRegAddressImm,
      Vabsdiff4RegRegRegConst,
      Vabsdiff4RegRegRegImm,
      Vabsdiff4RegRegRegReg,
      VaddRegRegImmReg,
      VaddRegRegRegReg,
      Vadd2RegRegImmReg,
      Vadd2RegRegRegReg,
      Vadd4RegRegImmReg,
      Vadd4RegRegRegReg,
      VhmnmxRegRegAddressImmRegPred,
      VhmnmxRegRegConstRegPred,
      VhmnmxRegRegImmImmRegPred,
      VhmnmxRegRegRegRegPred,
      ViaddRegRegAddressImm,
      ViaddRegRegConst,
      ViaddRegRegImm,
      ViaddRegRegReg,
      ViaddmnmxRegRegAddressImmRegPred,
      ViaddmnmxRegRegConstRegPred,
      ViaddmnmxRegRegImmRegPred,
      ViaddmnmxRegRegRegAddressImmPred,
      ViaddmnmxRegRegRegConstPred,
      ViaddmnmxRegRegRegImmPred,
      ViaddmnmxRegRegRegRegPred,
      VildRegAddress,
      VimnmxRegPredPredRegAddressImmPred,
      VimnmxRegPredPredRegConstPred,
      VimnmxRegPredPredRegImmPred,
      VimnmxRegPredPredRegRegPred,
      VimnmxRegRegAddressImmPred,
      VimnmxRegRegConstPred,
      VimnmxRegRegImmPred,
      VimnmxRegRegRegPred,
      Vimnmx3RegRegAddressImmRegPred,
      Vimnmx3RegRegConstRegPred,
      Vimnmx3RegRegImmRegPred,
      Vimnmx3RegRegRegRegPred,
      VmadRegRegImmReg,
      VmadRegRegRegReg,
      VmnmxRegRegImmReg,
      VmnmxRegRegRegReg,
      Vmnmx2RegRegImmReg,
      Vmnmx2RegRegRegReg,
      Vmnmx4RegRegImmReg,
      Vmnmx4RegRegRegReg,
      VoteImm,
      VotePredPred,
      VoteRegPredPred,
      VoteuRegPredPred,
      VoteVtgImm,
      VoteVtgPredImm,
      VoteVtgPredPredImmPred,
      Vsel2RegRegImmPred,
      Vsel2RegRegRegPred,
      Vsel4RegRegImmPred,
      Vsel4RegRegRegPred,
      VsetRegRegImmReg,
      VsetRegRegRegReg,
      Vset2RegRegImmReg,
      Vset2RegRegRegReg,
      Vset4RegRegImmReg,
      Vset4RegRegRegReg,
      VsetpPredPredRegImmPred,
      VsetpPredPredRegRegPred,
      VsetpPredRegImm,
      VsetpPredRegReg,
      VshlRegRegImmReg,
      VshlRegRegRegReg,
      VshrRegRegImmReg,
      VshrRegRegRegReg,
      WarpgroupNoOperands,
      WarpgroupImm,
      WarpgroupsetReg,
      WarpsyncNoOperands,
      WarpsyncAddressImm,
      WarpsyncConst,
      WarpsyncImm,
      WarpsyncPred,
      WarpsyncPredAddressImm,
      WarpsyncPredConst,
      WarpsyncPredImm,
      WarpsyncPredReg,
      WarpsyncPredRegImm,
      WarpsyncReg,
      WarpsyncRegImm,
      XmadRegRegConstReg,
      XmadRegRegImmReg,
      XmadRegRegRegConst,
      XmadRegRegRegReg,
      YieldNoOperands,
      YieldPred,
      Hir0x002NoOperands,
      Hir0x003NoOperands,
      Hir0x003Pred,
      Hir0x004NoOperands,
      Hir0x005PredRegRegImm,
      Hir0x005RegRegImm,
      Hir0x006Imm,
      Hir0x007NoOperands,
      Hir0x008ImmRegReg,
      Hir0x020NoOperands,
      Hir0x020Imm,
      Hir0x120AddressImm,
      Hir0x120Const,
      Hir0x120Imm,
      Hir0x120PredAddressImm,
      Hir0x120PredConst,
      Hir0x120PredImm,
      Hir0x120PredReg,
      Hir0x120Reg,
      Hir0x130AddressAddressAddressAddressAddressAddressPred,
      Hir0x150RegRegRegRegPredRegRegReg,
      Hir0x150RegRegRegRegRegRegReg,
      Hir0x160Reg,
      Hir0x170Reg,
      Hir0x1a0RegRegRegRegImmImm,
      Hir0x1b0RegRegRegReg,
      Hir0x1e0NoOperands,
      Hir0x1f0ImmReg,
      Hir0x1f2Reg,
      Hir0x1f3Reg
    )
  }
}
import Instruction.*
@Generated(Array("sbt codegen"))
case class Instruction(
    form: Form,
    modifiers: Vec[Modifier] = Vec.empty,
    guard: Option[Guard] = None
) derives CanEqual {
  inline def definition: Definition = Definition.all(form.ordinal)
  inline def mnemonic: NonEmpty[String] = definition.mnemonic
  inline def operands: Vec[Operand] = form.operands
}
