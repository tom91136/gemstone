// Generated SASS typed logical instruction forms from pinned NVIDIA descriptions. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
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
  }
  enum Modifier(val spelling: NonEmpty[String]) {
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
  case class Guard(predicate: Operand.Predicate)

  object Definition {
    lazy val all: Vec[Definition] = Vec(
      InstructionDefinitions0.definitions,
      InstructionDefinitions1.definitions,
      InstructionDefinitions2.definitions,
      InstructionDefinitions3.definitions,
      InstructionDefinitions4.definitions,
      InstructionDefinitions5.definitions,
      InstructionDefinitions6.definitions
    ).flatten
    private lazy val byMnemonic: Map[String, Vec[Definition]] = all.groupBy(_.mnemonic.raw)
    def forMnemonic(mnemonic: String): Vec[Definition] = byMnemonic.getOrElse(mnemonic, Vec.empty)
  }
  abstract class Definition(
      val name: NonEmpty[String],
      val mnemonic: NonEmpty[String],
      val forms: Vec[String]
  ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction]
    override final def toString: String = name.raw
  }
  def decode(
      mnemonic: String,
      modifiers: Vec[Modifier],
      operands: Vec[Operand],
      guard: Option[Guard]
  ): Either[String, Instruction] = {
    val definitions = Definition.forMnemonic(mnemonic)
    definitions.view.flatMap(_.decode(modifiers, operands, guard)).headOption
      .toRight(if (definitions.isEmpty) s"unknown SASS mnemonic $mnemonic"
      else s"invalid operands for $mnemonic: $operands")
  }
  type Acqbulk = AcqbulkNoOperands
  type Acqshminit = AcqshminitNoOperands
  type Al2p = Al2pPredRegReg | Al2pPredRegRegImm | Al2pRegReg | Al2pRegRegImm
  type Ald = AldRegAddressReg
  type Arrives = ArrivesAddress
  type Ast = AstAddressRegReg
  type Atom =
    AtomPredRegAddress | AtomPredRegAddressReg | AtomPredRegAddressRegReg | AtomRegAddressReg | AtomRegAddressRegReg
  type Atomg = AtomgPredRegAddressReg | AtomgPredRegAddressRegReg
  type Atoms = AtomsPredAddressRegReg | AtomsRegAddress | AtomsRegAddressReg | AtomsRegAddressRegReg
  type B2r = B2rReg | B2rRegImm | B2rRegPred
  type Bar =
    BarNoOperands | BarImm | BarImmImm | BarImmImmPred | BarImmPred | BarImmReg | BarImmRegPred | BarReg | BarRegImm | BarRegImmPred | BarRegPred | BarRegReg | BarRegRegPred
  type Bfe = BfeRegRegConst | BfeRegRegImm | BfeRegRegReg
  type Bfi = BfiRegRegConstReg | BfiRegRegImmReg | BfiRegRegRegConst | BfiRegRegRegReg
  type Bgmma =
    BgmmaRegAddressReg | BgmmaRegAddressRegPred | BgmmaRegAddressRegReg | BgmmaRegAddressRegRegPred | BgmmaRegRegAddressReg | BgmmaRegRegAddressRegPred
  type Bmma = BmmaRegRegRegReg | BmmaRegRegRegRegPred
  type Bmov =
    BmovAddressImm | BmovConst | BmovImm | BmovReg | BmovRegAddressImm | BmovRegConst | BmovRegImm | BmovRegReg | BmovRegSymbol | BmovSymbolAddressImm | BmovSymbolConst | BmovSymbolImm | BmovSymbolReg
  type Bmsk = BmskRegRegAddressImm | BmskRegRegConst | BmskRegRegImm | BmskRegRegReg
  type Bpt = BptNoOperands | BptImm
  type Bra =
    BraConst | BraImm | BraPredImm | BraPredPredImm | BraPredRegImm | BraRegImm | BraSymbolConst | BraSymbolImm | BraSymbolImmImm | BraSymbolSymbolImmImm
  type Break = BreakPredReg | BreakReg
  type Brev = BrevRegAddressImm | BrevRegConst | BrevRegImm | BrevRegReg
  type Brk = BrkNoOperands
  type Brx =
    BrxConst | BrxPredReg | BrxPredRegImm | BrxReg | BrxRegImm | BrxSymbolConst | BrxSymbolImmAddress | BrxSymbolReg | BrxSymbolRegImm | BrxSymbolSymbolImmAddress
  type Brxu = BrxuPredReg | BrxuPredRegImm | BrxuReg | BrxuRegImm
  type Bssy = BssyPredRegImm | BssyRegImm
  type Bsync = BsyncPredReg | BsyncReg
  type Cal = CalConst | CalImm
  type Call = CallConst | CallImm | CallPredConst | CallPredImm | CallPredReg | CallPredRegImm | CallReg | CallRegImm
  type Cctl =
    CctlNoOperands | CctlAddress | CctlAddressAddress | CctlAddressImm | CctlConst | CctlPredAddress | CctlRegAddress
  type Cctll = CctllNoOperands | CctllAddress | CctllRegAddress
  type Cctls = CctlsNoOperands
  type Cctlt = CctltNoOperands | CctltImm | CctltImmImm | CctltReg
  type Cgaerrbar = CgaerrbarNoOperands
  type Clmad =
    ClmadRegRegAddressImmReg | ClmadRegRegConstReg | ClmadRegRegRegAddressImm | ClmadRegRegRegConst | ClmadRegRegRegReg
  type Cont = ContNoOperands
  type Credux = CreduxRegReg
  type Cs2r = Cs2rRegSpecial
  type Cs2ur = Cs2urRegSpecial
  type Cset = CsetRegSymbol | CsetRegSymbolPred
  type Csetp = CsetpPredPredSymbolPred | CsetpPredSymbol
  type Csmtest = CsmtestImm | CsmtestPredImm | CsmtestPredPredImmPred
  type Dadd = DaddRegRegAddressImm | DaddRegRegConst | DaddRegRegImm | DaddRegRegReg
  type Dchk = DchkPredRegConst | DchkPredRegImm | DchkPredRegReg
  type Depbar =
    DepbarNoOperands | DepbarGroup | DepbarImm | DepbarRegImm | DepbarRegImmGroup | DepbarRegImmImm | DepbarRegReg | DepbarRegRegGroup | DepbarRegRegImm
  type Dfma =
    DfmaRegRegAddressImmReg | DfmaRegRegConstReg | DfmaRegRegImmReg | DfmaRegRegRegAddressImm | DfmaRegRegRegConst | DfmaRegRegRegImm | DfmaRegRegRegReg
  type Dmma = DmmaRegRegRegReg | DmmaRegRegRegRegPred
  type Dmnmx = DmnmxRegRegConstPred | DmnmxRegRegImmPred | DmnmxRegRegRegPred
  type Dmul = DmulRegRegAddressImm | DmulRegRegConst | DmulRegRegImm | DmulRegRegReg
  type Dset =
    DsetRegRegConst | DsetRegRegConstPred | DsetRegRegImm | DsetRegRegImmPred | DsetRegRegReg | DsetRegRegRegPred
  type Dsetp =
    DsetpPredPredRegAddressImmPred | DsetpPredPredRegConstPred | DsetpPredPredRegImmPred | DsetpPredPredRegRegPred | DsetpPredRegAddressImm | DsetpPredRegConst | DsetpPredRegImm | DsetpPredRegReg
  type Elect = ElectPredReg | ElectPredRegPred | ElectPredRegReg
  type Endcollective = EndcollectiveNoOperands | EndcollectivePred
  type Errbar = ErrbarNoOperands
  type Exit = ExitNoOperands | ExitPred
  type F2f = F2fRegAddressImm | F2fRegConst | F2fRegImm | F2fRegReg
  type F2fp =
    F2fpRegAddressImm | F2fpRegAddressImmReg | F2fpRegConst | F2fpRegConstReg | F2fpRegImm | F2fpRegImmReg | F2fpRegReg | F2fpRegRegAddressImm | F2fpRegRegAddressImmReg | F2fpRegRegConst | F2fpRegRegConstReg | F2fpRegRegImm | F2fpRegRegImmReg | F2fpRegRegReg | F2fpRegRegRegAddressImm | F2fpRegRegRegConst | F2fpRegRegRegImm | F2fpRegRegRegReg
  type F2i = F2iRegAddressImm | F2iRegConst | F2iRegImm | F2iRegReg
  type F2ip =
    F2ipRegRegAddressImmReg | F2ipRegRegConstReg | F2ipRegRegImmReg | F2ipRegRegRegAddressImm | F2ipRegRegRegConst | F2ipRegRegRegImm | F2ipRegRegRegReg
  type Fadd = FaddRegRegAddressImm | FaddRegRegConst | FaddRegRegImm | FaddRegRegReg
  type Fadd2 = Fadd2RegRegImm | Fadd2RegRegReg
  type Fadd32i = Fadd32iRegRegImm
  type Fcco =
    FccoPredPredRegConstReg | FccoPredPredRegImmReg | FccoPredPredRegRegConst | FccoPredPredRegRegReg | FccoPredRegConstReg | FccoPredRegImmReg | FccoPredRegRegConst | FccoPredRegRegReg
  type Fchk = FchkPredRegAddressImm | FchkPredRegConst | FchkPredRegImm | FchkPredRegReg
  type Fcmp = FcmpRegRegConstReg | FcmpRegRegImmReg | FcmpRegRegRegConst | FcmpRegRegRegReg
  type Fence = FenceNoOperands
  type Ffma =
    FfmaRegRegAddressImmReg | FfmaRegRegConstReg | FfmaRegRegImmReg | FfmaRegRegRegAddressImm | FfmaRegRegRegConst | FfmaRegRegRegImm | FfmaRegRegRegReg
  type Ffma2 = Ffma2RegRegImmReg | Ffma2RegRegRegImm | Ffma2RegRegRegReg
  type Ffma32i = Ffma32iRegRegImm | Ffma32iRegRegImmReg
  type Fhadd = FhaddRegRegReg
  type Fhfma = FhfmaRegRegRegReg
  type Flo =
    FloRegAddressImm | FloRegConst | FloRegImm | FloRegPredAddressImm | FloRegPredConst | FloRegPredImm | FloRegPredReg | FloRegReg
  type Fmnmx =
    FmnmxRegPredRegAddressImmPred | FmnmxRegPredRegConstPred | FmnmxRegPredRegImmPred | FmnmxRegPredRegRegPred | FmnmxRegRegAddressImmPred | FmnmxRegRegConstPred | FmnmxRegRegImmPred | FmnmxRegRegRegPred
  type Fmnmx3 = Fmnmx3RegRegImmRegPred | Fmnmx3RegRegRegRegPred
  type Fmul = FmulRegRegAddressImm | FmulRegRegConst | FmulRegRegImm | FmulRegRegReg
  type Fmul2 = Fmul2RegRegImm | Fmul2RegRegReg
  type Fmul32i = Fmul32iRegRegImm
  type Footprint =
    FootprintPredRegRegRegReg | FootprintPredRegRegRegRegImmImm | FootprintPredRegRegRegRegImmImmImm | FootprintPredRegRegRegRegReg | FootprintPredRegRegRegRegRegImm | FootprintRegRegRegReg | FootprintRegRegRegRegImmImm | FootprintRegRegRegRegImmImmImm | FootprintRegRegRegRegReg | FootprintRegRegRegRegRegImm
  type Frnd = FrndRegAddressImm | FrndRegConst | FrndRegImm | FrndRegReg
  type Fsel = FselRegRegAddressImmPred | FselRegRegConstPred | FselRegRegImmPred | FselRegRegRegPred
  type Fset =
    FsetRegRegAddressImm | FsetRegRegAddressImmPred | FsetRegRegConst | FsetRegRegConstPred | FsetRegRegImm | FsetRegRegImmPred | FsetRegRegReg | FsetRegRegRegPred
  type Fsetp =
    FsetpPredPredRegAddressImmPred | FsetpPredPredRegConstPred | FsetpPredPredRegImmPred | FsetpPredPredRegRegPred | FsetpPredRegAddressImm | FsetpPredRegConst | FsetpPredRegImm | FsetpPredRegReg
  type Fswz = FswzRegRegReg
  type Fswzadd = FswzaddRegRegReg
  type Gather = GatherRegRegRegRegImm | GatherRegRegRegRegImmImm | GatherRegRegRegRegImmImmImm
  type Getcrsptr = GetcrsptrReg
  type Getlmembase = GetlmembaseReg
  type Hadd2 =
    Hadd2NoOperands | Hadd2Const | Hadd2ImmImm | Hadd2RegRegAddressImm | Hadd2RegRegConst | Hadd2RegRegImm | Hadd2RegRegImmImm | Hadd2RegRegReg
  type Hadd232i = Hadd232iImmImm | Hadd232iRegRegImmImm
  type Hfma2 =
    Hfma2NoOperands | Hfma2Const | Hfma2ImmImm | Hfma2RegRegAddressImmReg | Hfma2RegRegAddressImmRegPred | Hfma2RegRegConstReg | Hfma2RegRegConstRegPred | Hfma2RegRegImmImmReg | Hfma2RegRegImmImmRegPred | Hfma2RegRegRegAddressImm | Hfma2RegRegRegAddressImmPred | Hfma2RegRegRegConst | Hfma2RegRegRegConstPred | Hfma2RegRegRegImmImm | Hfma2RegRegRegImmImmPred | Hfma2RegRegRegReg | Hfma2RegRegRegRegPred
  type Hfma232i = Hfma232iRegRegImmImm | Hfma232iRegRegImmImmReg
  type Hgmma =
    HgmmaRegAddressReg | HgmmaRegAddressRegPred | HgmmaRegAddressRegPredRegImm | HgmmaRegAddressRegReg | HgmmaRegAddressRegRegImm | HgmmaRegAddressRegRegPred | HgmmaRegAddressRegRegPredRegImm | HgmmaRegAddressRegRegRegImm | HgmmaRegRegAddressReg | HgmmaRegRegAddressRegPred | HgmmaRegRegAddressRegPredRegImm | HgmmaRegRegAddressRegRegImm
  type Hir0x002 = Hir0x002NoOperands
  type Hir0x003 = Hir0x003NoOperands | Hir0x003Pred
  type Hir0x004 = Hir0x004NoOperands
  type Hir0x005 = Hir0x005PredRegRegImm | Hir0x005RegRegImm
  type Hir0x006 = Hir0x006Imm
  type Hir0x007 = Hir0x007NoOperands
  type Hir0x008 = Hir0x008ImmRegReg
  type Hir0x020 = Hir0x020NoOperands | Hir0x020Imm
  type Hir0x120 =
    Hir0x120AddressImm | Hir0x120Const | Hir0x120Imm | Hir0x120PredAddressImm | Hir0x120PredConst | Hir0x120PredImm | Hir0x120PredReg | Hir0x120Reg
  type Hir0x130 = Hir0x130AddressAddressAddressAddressAddressAddressPred
  type Hir0x150 = Hir0x150RegRegRegRegPredRegRegReg | Hir0x150RegRegRegRegRegRegReg
  type Hir0x160 = Hir0x160Reg
  type Hir0x170 = Hir0x170Reg
  type Hir0x1a0 = Hir0x1a0RegRegRegRegImmImm
  type Hir0x1b0 = Hir0x1b0RegRegRegReg
  type Hir0x1e0 = Hir0x1e0NoOperands
  type Hir0x1f0 = Hir0x1f0ImmReg
  type Hir0x1f2 = Hir0x1f2Reg
  type Hir0x1f3 = Hir0x1f3Reg
  type Hmma =
    HmmaAddressRegRegAddress | HmmaAddressRegRegAddressPred | HmmaAddressRegRegAddressPredRegImm | HmmaAddressRegRegAddressRegImm | HmmaRegRegRegReg | HmmaRegRegRegRegPred | HmmaRegRegRegRegPredRegImm | HmmaRegRegRegRegRegImm
  type Hmnmx2 =
    Hmnmx2RegPredPredRegAddressImmPred | Hmnmx2RegPredPredRegConstPred | Hmnmx2RegPredPredRegImmImmPred | Hmnmx2RegPredPredRegRegPred | Hmnmx2RegRegAddressImmPred | Hmnmx2RegRegConstPred | Hmnmx2RegRegImmImmPred | Hmnmx2RegRegRegPred
  type Hmul2 =
    Hmul2NoOperands | Hmul2Const | Hmul2ImmImm | Hmul2RegRegAddressImm | Hmul2RegRegConst | Hmul2RegRegImmImm | Hmul2RegRegReg
  type Hmul232i = Hmul232iImmImm | Hmul232iRegRegImmImm
  type Hset2 =
    Hset2NoOperands | Hset2Const | Hset2ConstPred | Hset2ImmImm | Hset2ImmImmPred | Hset2Pred | Hset2RegRegAddressImm | Hset2RegRegAddressImmPred | Hset2RegRegConst | Hset2RegRegConstPred | Hset2RegRegImmImm | Hset2RegRegImmImmPred | Hset2RegRegReg | Hset2RegRegRegPred
  type Hsetp2 =
    Hsetp2PredPredRegAddressImm | Hsetp2PredPredRegAddressImmPred | Hsetp2PredPredRegConst | Hsetp2PredPredRegConstPred | Hsetp2PredPredRegImmImm | Hsetp2PredPredRegImmImmPred | Hsetp2PredPredRegReg | Hsetp2PredPredRegRegPred
  type I2f = I2fRegAddressImm | I2fRegConst | I2fRegImm | I2fRegReg
  type I2fp = I2fpRegAddressImm | I2fpRegConst | I2fpRegImm | I2fpRegReg
  type I2i = I2iRegAddressImm | I2iRegConst | I2iRegImm | I2iRegReg
  type I2ip = I2ipRegRegAddressImmReg | I2ipRegRegConstReg | I2ipRegRegImmReg | I2ipRegRegRegReg
  type Iabs = IabsRegAddressImm | IabsRegConst | IabsRegImm | IabsRegReg
  type Iadd =
    IaddRegPredRegAddressImm | IaddRegPredRegAddressImmPred | IaddRegPredRegConst | IaddRegPredRegConstPred | IaddRegPredRegImm | IaddRegPredRegImmPred | IaddRegPredRegImmReg | IaddRegPredRegImmRegPred | IaddRegPredRegReg | IaddRegPredRegRegPred | IaddRegPredRegRegReg | IaddRegPredRegRegRegPred | IaddRegRegAddressImm | IaddRegRegAddressImmPred | IaddRegRegConst | IaddRegRegConstPred | IaddRegRegImm | IaddRegRegImmPred | IaddRegRegImmReg | IaddRegRegImmRegPred | IaddRegRegReg | IaddRegRegRegPred | IaddRegRegRegReg | IaddRegRegRegRegPred
  type Iadd3 =
    Iadd3RegPredPredRegAddressImmReg | Iadd3RegPredPredRegAddressImmRegPredPred | Iadd3RegPredPredRegConstReg | Iadd3RegPredPredRegConstRegPredPred | Iadd3RegPredPredRegImmReg | Iadd3RegPredPredRegImmRegPredPred | Iadd3RegPredPredRegRegReg | Iadd3RegPredPredRegRegRegPredPred | Iadd3RegPredRegAddressImmReg | Iadd3RegPredRegAddressImmRegPredPred | Iadd3RegPredRegConstReg | Iadd3RegPredRegConstRegPredPred | Iadd3RegPredRegImmReg | Iadd3RegPredRegImmRegPredPred | Iadd3RegPredRegRegReg | Iadd3RegPredRegRegRegPredPred | Iadd3RegRegAddressImmReg | Iadd3RegRegAddressImmRegPredPred | Iadd3RegRegConstReg | Iadd3RegRegConstRegPredPred | Iadd3RegRegImmReg | Iadd3RegRegImmRegPredPred | Iadd3RegRegRegReg | Iadd3RegRegRegRegPredPred
  type Iadd32i = Iadd32iRegPredRegImm | Iadd32iRegPredRegImmPred | Iadd32iRegRegImm | Iadd32iRegRegImmPred
  type Icmp = IcmpRegRegConstReg | IcmpRegRegImmReg | IcmpRegRegRegConst | IcmpRegRegRegReg
  type Ide = IdeNoOperands | IdeImm
  type Idp = IdpRegRegAddressImmReg | IdpRegRegConstReg | IdpRegRegRegReg
  type Idp4a = Idp4aRegRegAddressImmReg | Idp4aRegRegConstReg | Idp4aRegRegRegReg
  type Igmma =
    IgmmaRegAddressReg | IgmmaRegAddressRegPred | IgmmaRegAddressRegPredReg | IgmmaRegAddressRegPredRegImm | IgmmaRegAddressRegReg | IgmmaRegAddressRegRegImm | IgmmaRegAddressRegRegPred | IgmmaRegAddressRegRegPredReg | IgmmaRegAddressRegRegPredRegImm | IgmmaRegAddressRegRegReg | IgmmaRegAddressRegRegRegImm | IgmmaRegRegAddressReg | IgmmaRegRegAddressRegPred | IgmmaRegRegAddressRegPredReg | IgmmaRegRegAddressRegPredRegImm | IgmmaRegRegAddressRegReg | IgmmaRegRegAddressRegRegImm
  type Imad =
    ImadRegPredRegAddressImmReg | ImadRegPredRegAddressImmRegPred | ImadRegPredRegConstReg | ImadRegPredRegConstRegPred | ImadRegPredRegImmReg | ImadRegPredRegImmRegPred | ImadRegPredRegRegAddressImm | ImadRegPredRegRegAddressImmPred | ImadRegPredRegRegConst | ImadRegPredRegRegConstPred | ImadRegPredRegRegReg | ImadRegPredRegRegRegPred | ImadRegRegAddressImmReg | ImadRegRegAddressImmRegPred | ImadRegRegConstReg | ImadRegRegConstRegPred | ImadRegRegImmReg | ImadRegRegImmRegPred | ImadRegRegRegAddressImm | ImadRegRegRegAddressImmPred | ImadRegRegRegConst | ImadRegRegRegConstPred | ImadRegRegRegImm | ImadRegRegRegImmPred | ImadRegRegRegReg | ImadRegRegRegRegPred
  type Imad32i = Imad32iRegRegImmReg
  type Imadsp = ImadspRegRegConstReg | ImadspRegRegImmReg | ImadspRegRegRegConst | ImadspRegRegRegReg
  type Imma = ImmaRegRegRegReg | ImmaRegRegRegRegPred | ImmaRegRegRegRegPredRegImm | ImmaRegRegRegRegRegImm
  type Imnmx =
    ImnmxPredPredRegRegImmPred | ImnmxPredPredRegRegImmPredPred | ImnmxPredPredRegRegRegPred | ImnmxPredPredRegRegRegPredPred | ImnmxPredRegRegImmPred | ImnmxPredRegRegImmPredPred | ImnmxPredRegRegRegPred | ImnmxPredRegRegRegPredPred | ImnmxRegRegAddressImmPred | ImnmxRegRegConstPred | ImnmxRegRegImmPred | ImnmxRegRegImmPredPred | ImnmxRegRegRegPred | ImnmxRegRegRegPredPred
  type Imul =
    ImulRegPredRegAddressImm | ImulRegPredRegConst | ImulRegPredRegImm | ImulRegPredRegReg | ImulRegRegAddressImm | ImulRegRegConst | ImulRegRegImm | ImulRegRegImmReg | ImulRegRegReg | ImulRegRegRegImm | ImulRegRegRegReg
  type Imul32i = Imul32iRegPredRegImm | Imul32iRegRegImm
  type Ipa =
    IpaRegAddress | IpaRegAddressImm | IpaRegAddressPred | IpaRegAddressReg | IpaRegAddressRegReg | IpaRegAddressRegRegPred | IpaRegImm | IpaRegImmConst | IpaRegImmImm | IpaRegImmPred | IpaRegImmReg | IpaRegImmRegReg | IpaRegImmRegRegPred | IpaRegPredAddress | IpaRegPredAddressImm | IpaRegPredAddressReg | IpaRegPredImm | IpaRegPredImmConst | IpaRegPredImmImm | IpaRegPredImmReg
  type Isad = IsadRegRegConstReg | IsadRegRegImmReg | IsadRegRegRegConst | IsadRegRegRegReg
  type Isberd = IsberdRegAddress
  type Isbewr = IsbewrAddressReg
  type Iscadd =
    IscaddRegPredRegAddressImmImm | IscaddRegPredRegConstImm | IscaddRegPredRegImmImm | IscaddRegPredRegRegImm | IscaddRegRegAddressImmImm | IscaddRegRegConstImm | IscaddRegRegImmImm | IscaddRegRegRegImm
  type Iscadd32i = Iscadd32iRegPredRegImmImm | Iscadd32iRegRegImmImm
  type Iset =
    IsetRegRegConst | IsetRegRegConstPred | IsetRegRegImm | IsetRegRegImmPred | IsetRegRegReg | IsetRegRegRegPred
  type Isetp =
    IsetpPredPredRegAddressImmPred | IsetpPredPredRegAddressImmPredPred | IsetpPredPredRegConstPred | IsetpPredPredRegConstPredPred | IsetpPredPredRegImmPred | IsetpPredPredRegImmPredPred | IsetpPredPredRegRegPred | IsetpPredPredRegRegPredPred | IsetpPredRegAddressImm | IsetpPredRegAddressImmPred | IsetpPredRegConst | IsetpPredRegConstPred | IsetpPredRegImm | IsetpPredRegImmPred | IsetpPredRegReg | IsetpPredRegRegPred
  type Isub = IsubRegConstReg | IsubRegImmReg | IsubRegRegConst | IsubRegRegImm | IsubRegRegReg
  type Jcal = JcalConst | JcalImm
  type Jmp =
    JmpConst | JmpImm | JmpPredConst | JmpPredImm | JmpPredPredConst | JmpPredPredImm | JmpPredRegConst | JmpPredRegImm | JmpRegConst | JmpRegImm | JmpSymbolConst | JmpSymbolImm | JmpSymbolImmImm | JmpSymbolSymbolImmImm
  type Jmx =
    JmxConst | JmxPredReg | JmxPredRegImm | JmxReg | JmxRegImm | JmxSymbolConst | JmxSymbolImmAddress | JmxSymbolReg | JmxSymbolRegImm | JmxSymbolSymbolImmAddress
  type Jmxu = JmxuPredReg | JmxuPredRegImm | JmxuReg | JmxuRegImm
  type Kil = KilNoOperands
  type Kill = KillNoOperands | KillPred
  type Ld = LdPredRegAddress | LdPredRegAddressPred | LdRegAddress | LdRegAddressPred
  type Ldc = LdcRegAddressAddress | LdcRegConst
  type Ldcu =
    LdcuRegAddress | LdcuRegAddressAddress | LdcuRegAddressPred | LdcuRegConst | LdcuRegRegAddress | LdcuRegRegAddressAddress | LdcuRegRegAddressAddressImm | LdcuRegRegAddressImm | LdcuRegRegAddressImmPred | LdcuRegRegAddressPred | LdcuRegRegConst | LdcuRegRegConstImm
  type Ldg =
    LdgPredRegAddress | LdgPredRegAddressPred | LdgRegAddress | LdgRegAddressPred | LdgRegRegAddress | LdgRegRegAddressImm | LdgRegRegAddressImmPred | LdgRegRegAddressPred
  type Ldgdepbar = LdgdepbarNoOperands
  type Ldgmc = LdgmcRegAddress
  type Ldgsts = LdgstsAddressAddress | LdgstsAddressAddressPred
  type Ldl = LdlRegAddress
  type Lds = LdsPredRegAddress | LdsRegAddress
  type Ldslk = LdslkPredRegAddress
  type Ldsm = LdsmRegAddress
  type Ldt = LdtRegAddress
  type Ldtm = LdtmRegAddress | LdtmRegRegAddress
  type Ldtram = LdtramRegAddress
  type Lea =
    LeaPredRegRegConst | LeaPredRegRegConstImm | LeaPredRegRegConstReg | LeaPredRegRegConstRegImm | LeaPredRegRegImm | LeaPredRegRegImmImm | LeaPredRegRegReg | LeaPredRegRegRegImm | LeaPredRegRegRegReg | LeaPredRegRegRegRegImm | LeaRegPredRegAddressImmImm | LeaRegPredRegAddressImmImmPred | LeaRegPredRegAddressImmRegImm | LeaRegPredRegAddressImmRegImmPred | LeaRegPredRegConstImm | LeaRegPredRegConstImmPred | LeaRegPredRegConstRegImm | LeaRegPredRegConstRegImmPred | LeaRegPredRegImmImm | LeaRegPredRegImmImmPred | LeaRegPredRegImmRegImm | LeaRegPredRegImmRegImmPred | LeaRegPredRegRegImm | LeaRegPredRegRegImmImm | LeaRegPredRegRegImmImmPred | LeaRegPredRegRegImmPred | LeaRegPredRegRegRegImm | LeaRegPredRegRegRegImmPred | LeaRegRegAddressImmImm | LeaRegRegAddressImmImmPred | LeaRegRegAddressImmRegImm | LeaRegRegAddressImmRegImmPred | LeaRegRegConst | LeaRegRegConstImm | LeaRegRegConstImmPred | LeaRegRegConstReg | LeaRegRegConstRegImm | LeaRegRegConstRegImmPred | LeaRegRegImm | LeaRegRegImmImm | LeaRegRegImmImmPred | LeaRegRegImmRegImm | LeaRegRegImmRegImmPred | LeaRegRegReg | LeaRegRegRegImm | LeaRegRegRegImmImm | LeaRegRegRegImmImmPred | LeaRegRegRegImmPred | LeaRegRegRegReg | LeaRegRegRegRegImm | LeaRegRegRegRegImmPred
  type Lepc = LepcReg | LepcRegImm
  type Longjmp = LongjmpNoOperands
  type Lop =
    LopPredRegRegAddressImm | LopPredRegRegAddressImmPred | LopPredRegRegConst | LopPredRegRegConstPred | LopPredRegRegImm | LopPredRegRegImmPred | LopPredRegRegReg | LopPredRegRegRegPred | LopRegRegAddressImm | LopRegRegAddressImmPred | LopRegRegConst | LopRegRegConstPred | LopRegRegImm | LopRegRegImmPred | LopRegRegReg | LopRegRegRegPred
  type Lop3 =
    Lop3PredRegRegAddressImmReg | Lop3PredRegRegAddressImmRegImm | Lop3PredRegRegAddressImmRegImmPred | Lop3PredRegRegAddressImmRegPred | Lop3PredRegRegConstReg | Lop3PredRegRegConstRegImm | Lop3PredRegRegConstRegImmPred | Lop3PredRegRegConstRegPred | Lop3PredRegRegImmReg | Lop3PredRegRegImmRegImm | Lop3PredRegRegImmRegImmPred | Lop3PredRegRegImmRegPred | Lop3PredRegRegRegReg | Lop3PredRegRegRegRegImm | Lop3PredRegRegRegRegImmPred | Lop3PredRegRegRegRegPred | Lop3RegRegAddressImmReg | Lop3RegRegAddressImmRegImm | Lop3RegRegAddressImmRegImmPred | Lop3RegRegAddressImmRegPred | Lop3RegRegConstReg | Lop3RegRegConstRegImm | Lop3RegRegConstRegImmPred | Lop3RegRegConstRegPred | Lop3RegRegImmReg | Lop3RegRegImmRegImm | Lop3RegRegImmRegImmPred | Lop3RegRegImmRegPred | Lop3RegRegRegReg | Lop3RegRegRegRegImm | Lop3RegRegRegRegImmPred | Lop3RegRegRegRegPred
  type Lop32i = Lop32iPredRegRegImm | Lop32iPredRegRegImmPred | Lop32iRegRegImm | Lop32iRegRegImmPred
  type Match = MatchPredRegReg | MatchRegReg
  type Membar = MembarNoOperands
  type Mov =
    MovAddressConst | MovAddressConstImm | MovAddressImm | MovAddressImmImm | MovAddressReg | MovAddressRegImm | MovRegAddress | MovRegAddressImm | MovRegAddressImmImm | MovRegConst | MovRegConstImm | MovRegImm | MovRegImmImm | MovRegReg | MovRegRegImm
  type Mov32i = Mov32iRegImm | Mov32iRegImmImm
  type Mov64iur = Mov64iurRegImm | Mov64iurRegReg
  type Movm = MovmRegReg
  type Mufu = MufuRegAddressImm | MufuRegConst | MufuRegImm | MufuRegImmImm | MufuRegReg
  type Nanosleep =
    NanosleepNoOperands | NanosleepAddressImm | NanosleepConst | NanosleepImm | NanosleepPred | NanosleepPredAddressImm | NanosleepPredConst | NanosleepPredImm | NanosleepPredReg | NanosleepReg
  type Nanotrap = NanotrapConst | NanotrapImm | NanotrapPredConst | NanotrapPredImm | NanotrapPredReg | NanotrapReg
  type Nop = NopNoOperands | NopImm | NopSymbol | NopSymbolImm
  type Omma =
    OmmaRegRegRegRegPredRegRegReg | OmmaRegRegRegRegPredRegRegRegImm | OmmaRegRegRegRegRegRegReg | OmmaRegRegRegRegRegRegRegImm
  type Out = OutReg | OutRegRegConst | OutRegRegImm | OutRegRegReg
  type P2r =
    P2rReg | P2rRegRegConst | P2rRegRegImm | P2rRegRegReg | P2rRegSymbol | P2rRegSymbolRegAddressImm | P2rRegSymbolRegConst | P2rRegSymbolRegImm | P2rRegSymbolRegReg
  type Pbk = PbkConst | PbkImm
  type Pcnt = PcntConst | PcntImm
  type Pexit = PexitImm
  type Pixld = PixldReg | PixldRegAddress | PixldRegPred | PixldRegPredAddress
  type Plongjmp = PlongjmpConst | PlongjmpImm
  type Plop3 =
    Plop3PredPredAddressImmPredImm | Plop3PredPredAddressImmRegImm | Plop3PredPredConstPredImm | Plop3PredPredConstRegImm | Plop3PredPredPredAddressImmPredImmImm | Plop3PredPredPredAddressImmRegImmImm | Plop3PredPredPredConstPredImmImm | Plop3PredPredPredConstRegImmImm | Plop3PredPredPredPred | Plop3PredPredPredPredImm | Plop3PredPredPredPredPredImmImm | Plop3PredPredPredRegPredImmImm | Plop3PredPredPredRegRegImmImm | Plop3PredPredRegAddressImmRegImmImm | Plop3PredPredRegConstRegImmImm | Plop3PredPredRegPredImm | Plop3PredPredRegRegImm | Plop3PredPredRegRegRegImmImm | Plop3PredRegAddressImmRegImm | Plop3PredRegConstRegImm | Plop3PredRegRegRegImm
  type Pmtrig = PmtrigImm | PmtrigPredImm
  type Popc =
    PopcRegAddressImm | PopcRegConst | PopcRegImm | PopcRegReg | PopcRegRegConst | PopcRegRegImm | PopcRegRegReg
  type Preexit = PreexitNoOperands
  type Pret = PretConst | PretImm
  type Prmt =
    PrmtRegRegAddressImmReg | PrmtRegRegConstReg | PrmtRegRegImmReg | PrmtRegRegRegAddressImm | PrmtRegRegRegConst | PrmtRegRegRegImm | PrmtRegRegRegReg
  type Pset = PsetRegPredPred | PsetRegPredPredPred
  type Psetp = PsetpPredPredPred | PsetpPredPredPredPredPred
  type Qadd4 = Qadd4RegRegImm | Qadd4RegRegReg
  type Qfma4 = Qfma4RegRegImmReg | Qfma4RegRegRegImm | Qfma4RegRegRegReg
  type Qgmma =
    QgmmaRegAddressReg | QgmmaRegAddressRegPred | QgmmaRegAddressRegPredReg | QgmmaRegAddressRegPredRegImm | QgmmaRegAddressRegReg | QgmmaRegAddressRegRegImm | QgmmaRegAddressRegRegPred | QgmmaRegAddressRegRegPredReg | QgmmaRegAddressRegRegPredRegImm | QgmmaRegAddressRegRegReg | QgmmaRegAddressRegRegRegImm | QgmmaRegRegAddressReg | QgmmaRegRegAddressRegPred | QgmmaRegRegAddressRegPredReg | QgmmaRegRegAddressRegPredRegImm | QgmmaRegRegAddressRegReg | QgmmaRegRegAddressRegRegImm
  type Qmma =
    QmmaRegRegRegReg | QmmaRegRegRegRegPred | QmmaRegRegRegRegPredRegImm | QmmaRegRegRegRegPredRegRegReg | QmmaRegRegRegRegPredRegRegRegImm | QmmaRegRegRegRegRegImm | QmmaRegRegRegRegRegRegReg | QmmaRegRegRegRegRegRegRegImm
  type Qmul4 = Qmul4RegRegImm | Qmul4RegRegReg
  type Qspc = QspcPredAddress | QspcPredRegAddress | QspcRegAddress
  type R2b = R2bImmReg
  type R2p =
    R2pReg | R2pRegConst | R2pRegImm | R2pRegReg | R2pSymbolReg | R2pSymbolRegAddressImm | R2pSymbolRegConst | R2pSymbolRegImm | R2pSymbolRegReg
  type R2ur = R2urPredRegReg | R2urRegReg
  type Ram = RamNoOperands
  type Red = RedAddressReg
  type Redas = RedasAddressReg
  type Redg = RedgAddressReg
  type Reds = RedsAddressReg
  type Redux = ReduxRegReg
  type Ret = RetNoOperands | RetPredReg | RetPredRegImm | RetReg | RetRegImm
  type Rpcmov = RpcmovAddressImm | RpcmovConst | RpcmovImm | RpcmovReg
  type Rro = RroRegConst | RroRegImm | RroRegReg
  type Rtt = RttNoOperands
  type S2r = S2rRegSpecial
  type S2ur = S2urRegSpecial
  type Sam = SamNoOperands
  type Scatter = ScatterRegRegRegRegImmImm
  type Sel = SelRegRegAddressImmPred | SelRegRegConstPred | SelRegRegImmPred | SelRegRegRegPred
  type Setcrsptr = SetcrsptrReg
  type Setctaid = SetctaidReg
  type Setlmembase = SetlmembaseReg
  type Sgxt = SgxtRegRegAddressImm | SgxtRegRegConst | SgxtRegRegImm | SgxtRegRegReg
  type Shf =
    ShfRegRegAddressImmReg | ShfRegRegConstReg | ShfRegRegImmReg | ShfRegRegRegAddressImm | ShfRegRegRegConst | ShfRegRegRegImm | ShfRegRegRegReg
  type Shfl = ShflPredRegRegImmImm | ShflPredRegRegImmReg | ShflPredRegRegRegImm | ShflPredRegRegRegReg
  type Shint = ShintImmImm | ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm
  type Shl = ShlRegImmReg | ShlRegRegAddressImm | ShlRegRegConst | ShlRegRegImm | ShlRegRegReg
  type Shr = ShrRegRegAddressImm | ShrRegRegConst | ShrRegRegImm | ShrRegRegReg
  type Spmetadata = SpmetadataRegRegRegImmImmImm
  type Ssy = SsyConst | SsyImm
  type St = StAddressReg | StAddressRegPred
  type Stas = StasAddressReg
  type Stg = StgAddressReg | StgAddressRegReg | StgAddressRegRegImm
  type Stl = StlAddressReg
  type Stp = StpImm
  type Sts = StsAddressReg
  type Stscul = StsculPredAddressReg
  type Stsm = StsmAddressReg
  type Stt = SttAddressReg
  type Sttm = SttmAddressReg
  type Suatom =
    SuatomPredRegAddressRegImm | SuatomPredRegAddressRegImmImm | SuatomPredRegAddressRegImmImmImm | SuatomPredRegAddressRegReg | SuatomPredRegAddressRegRegImm | SuatomPredRegAddressRegRegReg | SuatomPredRegAddressRegRegRegImm | SuatomPredRegAddressRegRegRegReg | SuatomRegAddressRegImm | SuatomRegAddressRegImmImm | SuatomRegAddressRegImmImmImm | SuatomRegAddressRegReg | SuatomRegAddressRegRegImm | SuatomRegAddressRegRegReg | SuatomRegAddressRegRegRegImm | SuatomRegAddressRegRegRegReg
  type Subfm = SubfmPredRegRegConstReg | SubfmPredRegRegImmReg | SubfmPredRegRegRegConst | SubfmPredRegRegRegReg
  type Suclamp = SuclampPredRegRegConstImm | SuclampPredRegRegImmImm | SuclampPredRegRegRegImm
  type Sueau = SueauRegRegConstReg | SueauRegRegImmReg | SueauRegRegRegConst | SueauRegRegRegReg
  type Suld =
    SuldPredRegAddressImm | SuldPredRegAddressImmImm | SuldPredRegAddressImmImmImm | SuldPredRegAddressReg | SuldPredRegAddressRegImm | SuldPredRegAddressRegReg | SuldPredRegAddressRegRegImm | SuldPredRegAddressRegRegReg | SuldRegAddressImm | SuldRegAddressImmImm | SuldRegAddressImmImmImm | SuldRegAddressReg | SuldRegAddressRegImm | SuldRegAddressRegReg | SuldRegAddressRegRegImm | SuldRegAddressRegRegReg
  type Suldga = SuldgaRegAddressConstPred | SuldgaRegAddressRegPred
  type Suquery =
    SuqueryPredRegAddressImmImm | SuqueryPredRegAddressImmImmImm | SuqueryPredRegAddressReg | SuqueryPredRegAddressRegImm | SuqueryPredRegAddressRegReg | SuqueryPredRegAddressRegRegImm | SuqueryPredRegAddressRegRegReg | SuqueryRegAddressImmImm | SuqueryRegAddressImmImmImm | SuqueryRegAddressReg | SuqueryRegAddressRegImm | SuqueryRegAddressRegReg | SuqueryRegAddressRegRegImm | SuqueryRegAddressRegRegReg
  type Sured =
    SuredAddressRegImm | SuredAddressRegImmImm | SuredAddressRegImmImmImm | SuredAddressRegReg | SuredAddressRegRegImm | SuredAddressRegRegReg | SuredAddressRegRegRegImm | SuredAddressRegRegRegReg
  type Sust =
    SustAddressRegImm | SustAddressRegImmImm | SustAddressRegImmImmImm | SustAddressRegReg | SustAddressRegRegImm | SustAddressRegRegReg | SustAddressRegRegRegImm | SustAddressRegRegRegReg
  type Sustga = SustgaAddressConstRegPred | SustgaAddressRegRegPred
  type Sync = SyncNoOperands
  type Syncs =
    SyncsNoOperands | SyncsAddress | SyncsAddressReg | SyncsPredAddressReg | SyncsRegAddress | SyncsRegAddressReg | SyncsRegAddressRegReg
  type Tex =
    TexPredRegRegRegImm | TexPredRegRegRegImmImm | TexPredRegRegRegImmImmImm | TexPredRegRegRegReg | TexPredRegRegRegRegImm | TexPredRegRegRegRegImmImm | TexPredRegRegRegRegImmImmImm | TexPredRegRegRegRegImmImmImmImm | TexPredRegRegRegRegReg | TexPredRegRegRegRegRegImm | TexPredRegRegRegRegRegImmImm | TexPredRegRegRegRegRegReg | TexPredRegRegRegRegRegRegImm | TexPredRegRegRegRegRegRegImmImm | TexRegRegRegImm | TexRegRegRegImmImm | TexRegRegRegImmImmImm | TexRegRegRegReg | TexRegRegRegRegImm | TexRegRegRegRegImmImm | TexRegRegRegRegImmImmImm | TexRegRegRegRegImmImmImmImm | TexRegRegRegRegReg | TexRegRegRegRegRegImm | TexRegRegRegRegRegImmImm | TexRegRegRegRegRegReg | TexRegRegRegRegRegRegImm | TexRegRegRegRegRegRegImmImm
  type Texdepbar = TexdepbarImm
  type Texs = TexsRegRegRegRegImm
  type Tld =
    TldPredRegRegRegImm | TldPredRegRegRegImmImm | TldPredRegRegRegReg | TldPredRegRegRegRegImm | TldPredRegRegRegRegImmImm | TldPredRegRegRegRegImmImmImm | TldPredRegRegRegRegImmImmImmImm | TldPredRegRegRegRegReg | TldPredRegRegRegRegRegImm | TldPredRegRegRegRegRegImmImm | TldPredRegRegRegRegRegReg | TldPredRegRegRegRegRegRegImm | TldPredRegRegRegRegRegRegImmImm | TldRegRegRegImm | TldRegRegRegImmImm | TldRegRegRegReg | TldRegRegRegRegImm | TldRegRegRegRegImmImm | TldRegRegRegRegImmImmImm | TldRegRegRegRegImmImmImmImm | TldRegRegRegRegReg | TldRegRegRegRegRegImm | TldRegRegRegRegRegImmImm | TldRegRegRegRegRegReg | TldRegRegRegRegRegRegImm | TldRegRegRegRegRegRegImmImm
  type Tld4 =
    Tld4PredRegRegRegImm | Tld4PredRegRegRegImmImm | Tld4PredRegRegRegImmImmImm | Tld4PredRegRegRegReg | Tld4PredRegRegRegRegImm | Tld4PredRegRegRegRegImmImm | Tld4PredRegRegRegRegImmImmImm | Tld4PredRegRegRegRegImmImmImmImm | Tld4PredRegRegRegRegReg | Tld4PredRegRegRegRegRegImm | Tld4PredRegRegRegRegRegImmImm | Tld4PredRegRegRegRegRegReg | Tld4PredRegRegRegRegRegRegImm | Tld4PredRegRegRegRegRegRegImmImm | Tld4RegRegRegImm | Tld4RegRegRegImmImm | Tld4RegRegRegImmImmImm | Tld4RegRegRegReg | Tld4RegRegRegRegImm | Tld4RegRegRegRegImmImm | Tld4RegRegRegRegImmImmImm | Tld4RegRegRegRegImmImmImmImm | Tld4RegRegRegRegReg | Tld4RegRegRegRegRegImm | Tld4RegRegRegRegRegImmImm | Tld4RegRegRegRegRegReg | Tld4RegRegRegRegRegRegImm | Tld4RegRegRegRegRegRegImmImm
  type Tld4s = Tld4sRegRegRegRegImm
  type Tlds = TldsRegRegRegRegImm
  type Tmml =
    TmmlRegRegImm | TmmlRegRegImmImm | TmmlRegRegImmImmImm | TmmlRegRegRegImm | TmmlRegRegRegImmImm | TmmlRegRegRegImmImmImm | TmmlRegRegRegReg | TmmlRegRegRegRegImm | TmmlRegRegRegRegImmImm | TmmlRegRegRegRegImmImmImm | TmmlRegRegRegRegImmImmImmImm | TmmlRegRegRegRegReg | TmmlRegRegRegRegRegImm | TmmlRegRegRegRegRegImmImm
  type Txa = TxaRegRegImm | TxaRegRegImmImm | TxaRegRegImmImmImm
  type Txd =
    TxdPredRegRegRegImm | TxdPredRegRegRegImmImm | TxdPredRegRegRegImmImmImm | TxdPredRegRegRegReg | TxdPredRegRegRegRegImm | TxdPredRegRegRegRegImmImm | TxdPredRegRegRegRegImmImmImm | TxdPredRegRegRegRegImmImmImmImm | TxdPredRegRegRegRegReg | TxdPredRegRegRegRegRegImm | TxdPredRegRegRegRegRegImmImm | TxdPredRegRegRegRegRegReg | TxdPredRegRegRegRegRegRegImm | TxdPredRegRegRegRegRegRegImmImm | TxdRegRegRegImm | TxdRegRegRegImmImm | TxdRegRegRegImmImmImm | TxdRegRegRegReg | TxdRegRegRegRegImm | TxdRegRegRegRegImmImm | TxdRegRegRegRegImmImmImm | TxdRegRegRegRegImmImmImmImm | TxdRegRegRegRegReg | TxdRegRegRegRegRegImm | TxdRegRegRegRegRegImmImm | TxdRegRegRegRegRegReg | TxdRegRegRegRegRegRegImm | TxdRegRegRegRegRegRegImmImm
  type Txq =
    TxqRegRegImm | TxqRegRegImmImm | TxqRegRegImmImmImm | TxqRegRegImmImmImmImm | TxqRegRegRegImm | TxqRegRegRegImmImmImm | TxqRegRegRegImmImmImmImm | TxqRegRegRegRegImm | TxqRegRegRegRegImmImm
  type Ublkcp = UblkcpAddressAddressReg | UblkcpAddressAddressRegAddress
  type Ublkpf = UblkpfAddressReg | UblkpfAddressRegAddress
  type Ublkred = UblkredAddressAddressReg | UblkredAddressAddressRegAddress
  type Ubmsk = UbmskRegRegImm | UbmskRegRegReg
  type Ubrev = UbrevRegImm | UbrevRegReg
  type Ucgabar_Arv = Ucgabar_ArvNoOperands
  type Ucgabar_Wait = Ucgabar_WaitNoOperands
  type Ucgabararv = UcgabararvNoOperands
  type Ucgabarget = UcgabargetReg
  type Ucgabarset = UcgabarsetReg
  type Ucgabarwait = UcgabarwaitNoOperands
  type Uclea = UcleaRegPredRegImmImm | UcleaRegPredRegRegImm
  type Uf2f = Uf2fRegImm | Uf2fRegReg
  type Uf2fp =
    Uf2fpRegImm | Uf2fpRegImmReg | Uf2fpRegReg | Uf2fpRegRegImm | Uf2fpRegRegImmReg | Uf2fpRegRegReg | Uf2fpRegRegRegImm | Uf2fpRegRegRegReg
  type Uf2i = Uf2iRegImm | Uf2iRegReg
  type Uf2ip = Uf2ipRegRegImmReg | Uf2ipRegRegRegImm | Uf2ipRegRegRegReg
  type Ufadd = UfaddRegRegImm | UfaddRegRegReg
  type Uffma = UffmaRegRegImmReg | UffmaRegRegRegImm | UffmaRegRegRegReg
  type Ufhadd = UfhaddRegRegReg
  type Ufhfma = UfhfmaRegRegRegReg
  type Uflo = UfloRegImm | UfloRegPredImm | UfloRegPredReg | UfloRegReg
  type Ufmnmx = UfmnmxRegPredRegImmPred | UfmnmxRegPredRegRegPred | UfmnmxRegRegImmPred | UfmnmxRegRegRegPred
  type Ufmul = UfmulRegRegImm | UfmulRegRegReg
  type Ufrnd = UfrndRegImm | UfrndRegReg
  type Ufsel = UfselRegRegImmPred | UfselRegRegRegPred
  type Ufset = UfsetRegRegImm | UfsetRegRegImmPred | UfsetRegRegReg | UfsetRegRegRegPred
  type Ufsetp = UfsetpPredPredRegImmPred | UfsetpPredPredRegRegPred | UfsetpPredRegImm | UfsetpPredRegReg
  type Ugetnextworkid = UgetnextworkidAddressAddress
  type Ui2f = Ui2fRegImm | Ui2fRegReg
  type Ui2fp = Ui2fpRegImm | Ui2fpRegReg
  type Ui2i = Ui2iRegImm | Ui2iRegReg
  type Ui2ip = Ui2ipRegRegImmReg | Ui2ipRegRegRegReg
  type Uiabs = UiabsRegImm | UiabsRegReg
  type Uiadd3 =
    Uiadd3RegPredPredRegImmReg | Uiadd3RegPredPredRegImmRegPredPred | Uiadd3RegPredPredRegRegReg | Uiadd3RegPredPredRegRegRegPredPred | Uiadd3RegPredRegImmReg | Uiadd3RegPredRegImmRegPredPred | Uiadd3RegPredRegRegReg | Uiadd3RegPredRegRegRegPredPred | Uiadd3RegRegImmReg | Uiadd3RegRegImmRegPredPred | Uiadd3RegRegRegReg | Uiadd3RegRegRegRegPredPred
  type Uimad =
    UimadRegPredRegImmReg | UimadRegPredRegImmRegPred | UimadRegPredRegRegReg | UimadRegPredRegRegRegPred | UimadRegRegImmReg | UimadRegRegImmRegPred | UimadRegRegRegImm | UimadRegRegRegImmPred | UimadRegRegRegReg | UimadRegRegRegRegPred
  type Uimnmx =
    UimnmxPredPredRegRegImmPred | UimnmxPredPredRegRegImmPredPred | UimnmxPredPredRegRegRegPred | UimnmxPredPredRegRegRegPredPred | UimnmxPredRegRegImmPred | UimnmxPredRegRegImmPredPred | UimnmxPredRegRegRegPred | UimnmxPredRegRegRegPredPred | UimnmxRegRegImmPred | UimnmxRegRegImmPredPred | UimnmxRegRegRegPred | UimnmxRegRegRegPredPred
  type Uisetp =
    UisetpPredPredRegImmPred | UisetpPredPredRegImmPredPred | UisetpPredPredRegRegPred | UisetpPredPredRegRegPredPred | UisetpPredRegImm | UisetpPredRegImmPred | UisetpPredRegReg | UisetpPredRegRegPred
  type Uldc = UldcRegAddress | UldcRegAddressImm | UldcRegAddressPred | UldcRegConst
  type Ulea =
    UleaRegPredRegImmImm | UleaRegPredRegImmImmPred | UleaRegPredRegImmRegImm | UleaRegPredRegImmRegImmPred | UleaRegPredRegRegImm | UleaRegPredRegRegImmImm | UleaRegPredRegRegImmImmPred | UleaRegPredRegRegImmPred | UleaRegPredRegRegRegImm | UleaRegPredRegRegRegImmPred | UleaRegRegImmImm | UleaRegRegImmImmPred | UleaRegRegImmRegImm | UleaRegRegImmRegImmPred | UleaRegRegRegImm | UleaRegRegRegImmImm | UleaRegRegRegImmImmPred | UleaRegRegRegImmPred | UleaRegRegRegRegImm | UleaRegRegRegRegImmPred
  type Ulepc = UlepcReg | UlepcRegImm
  type Ulop =
    UlopPredRegRegImm | UlopPredRegRegImmPred | UlopPredRegRegReg | UlopPredRegRegRegPred | UlopRegRegImm | UlopRegRegImmPred | UlopRegRegReg | UlopRegRegRegPred
  type Ulop3 =
    Ulop3PredRegRegImmReg | Ulop3PredRegRegImmRegImm | Ulop3PredRegRegImmRegImmPred | Ulop3PredRegRegImmRegPred | Ulop3PredRegRegRegReg | Ulop3PredRegRegRegRegImm | Ulop3PredRegRegRegRegImmPred | Ulop3PredRegRegRegRegPred | Ulop3RegRegImmReg | Ulop3RegRegImmRegImm | Ulop3RegRegImmRegImmPred | Ulop3RegRegImmRegPred | Ulop3RegRegRegReg | Ulop3RegRegRegRegImm | Ulop3RegRegRegRegImmPred | Ulop3RegRegRegRegPred
  type Ulop32i = Ulop32iPredRegRegImm | Ulop32iPredRegRegImmPred | Ulop32iRegRegImm | Ulop32iRegRegImmPred
  type Umemsets = UmemsetsAddressRegReg
  type Umov = UmovRegImm | UmovRegReg
  type Up2ur = Up2urReg | Up2urRegRegImm | Up2urRegRegReg
  type Uplop3 =
    Uplop3PredPredPredPred | Uplop3PredPredPredPredImm | Uplop3PredPredPredPredPredImmImm | Uplop3PredPredPredRegPredImmImm | Uplop3PredPredPredRegRegImmImm | Uplop3PredPredRegPredImm | Uplop3PredPredRegRegImm | Uplop3PredPredRegRegRegImmImm | Uplop3PredRegRegRegImm
  type Upopc = UpopcRegImm | UpopcRegReg
  type Uprmt = UprmtRegRegImmReg | UprmtRegRegRegReg
  type Upsetp = UpsetpPredPredPred | UpsetpPredPredPredPredPred
  type Ur2up = Ur2upReg | Ur2upRegImm | Ur2upRegReg
  type Uredgr = UredgrAddressReg
  type Usel = UselRegRegImmPred | UselRegRegRegPred
  type Usetmaxreg = UsetmaxregImm | UsetmaxregPredImm | UsetmaxregPredReg | UsetmaxregReg
  type Usetshmsz = UsetshmszNoOperands | UsetshmszImm | UsetshmszReg
  type Usgxt = UsgxtRegRegImm | UsgxtRegRegReg
  type Ushf = UshfRegRegImmReg | UshfRegRegRegImm | UshfRegRegRegReg
  type Ushl = UshlRegImmReg | UshlRegRegImm | UshlRegRegReg
  type Ushr = UshrRegRegImm | UshrRegRegReg
  type Ustgr = UstgrAddressReg
  type Utcatomsws = UtcatomswsPredRegReg | UtcatomswsPredRegRegReg | UtcatomswsRegReg
  type Utcbar = UtcbarNoOperands | UtcbarAddressRegReg
  type Utccp = UtccpAddressAddress
  type Utchmma =
    UtchmmaAddressAddressAddressAddressAddressRegPred | UtchmmaAddressAddressAddressAddressAddressRegPredImm
  type Utcimma = UtcimmaAddressAddressAddressAddressAddressRegPred
  type Utcomma = UtcommaAddressAddressAddressAddressAddressAddressPred
  type Utcqmma =
    UtcqmmaAddressAddressAddressAddressAddressAddressPred | UtcqmmaAddressAddressAddressAddressAddressRegPred
  type Utcshift = UtcshiftAddress
  type Utmacctl = UtmacctlNoOperands | UtmacctlAddress
  type Utmacmdflush = UtmacmdflushNoOperands
  type Utmaldg =
    UtmaldgAddressAddress | UtmaldgAddressAddressAddress | UtmaldgAddressAddressReg | UtmaldgAddressAddressRegAddress
  type Utmapf =
    UtmapfAddressAddress | UtmapfAddressAddressAddress | UtmapfAddressAddressReg | UtmapfAddressAddressRegAddress
  type Utmaredg = UtmaredgAddressAddress | UtmaredgAddressAddressAddress
  type Utmastg = UtmastgAddressAddress | UtmastgAddressAddressAddress
  type Uviadd = UviaddRegRegImm | UviaddRegRegReg
  type Uvimnmx = UvimnmxRegRegImmPred | UvimnmxRegRegRegPred
  type Uvirtcount = UvirtcountImm | UvirtcountReg
  type Vabsdiff =
    VabsdiffRegPredRegAddressImmReg | VabsdiffRegPredRegConstReg | VabsdiffRegPredRegImmReg | VabsdiffRegPredRegRegAddressImm | VabsdiffRegPredRegRegConst | VabsdiffRegPredRegRegImm | VabsdiffRegPredRegRegReg | VabsdiffRegRegAddressImmReg | VabsdiffRegRegConstReg | VabsdiffRegRegImmReg | VabsdiffRegRegRegAddressImm | VabsdiffRegRegRegConst | VabsdiffRegRegRegImm | VabsdiffRegRegRegReg
  type Vabsdiff2 = Vabsdiff2RegRegImmReg | Vabsdiff2RegRegRegReg
  type Vabsdiff4 =
    Vabsdiff4RegPredRegAddressImmReg | Vabsdiff4RegPredRegConstReg | Vabsdiff4RegPredRegImmReg | Vabsdiff4RegPredRegRegAddressImm | Vabsdiff4RegPredRegRegConst | Vabsdiff4RegPredRegRegImm | Vabsdiff4RegPredRegRegReg | Vabsdiff4RegRegAddressImmReg | Vabsdiff4RegRegConstReg | Vabsdiff4RegRegImmReg | Vabsdiff4RegRegRegAddressImm | Vabsdiff4RegRegRegConst | Vabsdiff4RegRegRegImm | Vabsdiff4RegRegRegReg
  type Vadd = VaddRegRegReg | VaddRegRegRegReg
  type Vadd2 = Vadd2RegRegReg | Vadd2RegRegRegReg
  type Vadd4 = Vadd4RegRegReg | Vadd4RegRegRegReg
  type Vhmnmx =
    VhmnmxRegRegAddressImmRegPred | VhmnmxRegRegConstRegPred | VhmnmxRegRegImmImmRegPred | VhmnmxRegRegRegRegPred
  type Viadd = ViaddRegRegAddressImm | ViaddRegRegConst | ViaddRegRegImm | ViaddRegRegReg
  type Viaddmnmx =
    ViaddmnmxRegRegAddressImmRegPred | ViaddmnmxRegRegConstRegPred | ViaddmnmxRegRegImmRegPred | ViaddmnmxRegRegRegAddressImmPred | ViaddmnmxRegRegRegConstPred | ViaddmnmxRegRegRegImmPred | ViaddmnmxRegRegRegRegPred
  type Vild = VildRegAddress
  type Vimnmx =
    VimnmxRegPredPredRegAddressImmPred | VimnmxRegPredPredRegConstPred | VimnmxRegPredPredRegImmPred | VimnmxRegPredPredRegRegPred | VimnmxRegRegAddressImmPred | VimnmxRegRegConstPred | VimnmxRegRegImmPred | VimnmxRegRegRegPred
  type Vimnmx3 =
    Vimnmx3RegRegAddressImmRegPred | Vimnmx3RegRegConstRegPred | Vimnmx3RegRegImmRegPred | Vimnmx3RegRegRegRegPred
  type Vmad = VmadRegRegImmReg | VmadRegRegReg | VmadRegRegRegReg
  type Vmnmx = VmnmxRegRegImmReg | VmnmxRegRegRegReg
  type Vmnmx2 = Vmnmx2RegRegImmReg | Vmnmx2RegRegRegReg
  type Vmnmx4 = Vmnmx4RegRegImmReg | Vmnmx4RegRegRegReg
  type Vote = VoteImm | VotePredPred | VoteRegPredPred
  type VoteVtg = VoteVtgImm | VoteVtgPredImm | VoteVtgPredPredImmPred
  type Voteu = VoteuRegPredPred
  type Vsel2 = Vsel2RegRegImmPred | Vsel2RegRegRegPred
  type Vsel4 = Vsel4RegRegImmPred | Vsel4RegRegRegPred
  type Vset = VsetRegRegImmReg | VsetRegRegRegReg
  type Vset2 = Vset2RegRegImmReg | Vset2RegRegRegReg
  type Vset4 = Vset4RegRegImmReg | Vset4RegRegRegReg
  type Vsetp = VsetpPredPredRegImmPred | VsetpPredPredRegRegPred | VsetpPredRegImm | VsetpPredRegReg
  type Vshl = VshlRegRegImmReg | VshlRegRegRegReg
  type Vshr = VshrRegRegImmReg | VshrRegRegRegReg
  type Warpgroup = WarpgroupNoOperands | WarpgroupImm
  type Warpgroupset = WarpgroupsetReg
  type Warpsync =
    WarpsyncNoOperands | WarpsyncAddressImm | WarpsyncConst | WarpsyncImm | WarpsyncPred | WarpsyncPredAddressImm | WarpsyncPredConst | WarpsyncPredImm | WarpsyncPredReg | WarpsyncPredRegImm | WarpsyncReg | WarpsyncRegImm
  type Xmad = XmadRegRegConstReg | XmadRegRegImmReg | XmadRegRegRegConst | XmadRegRegRegReg
  type Yield = YieldNoOperands | YieldPred
  inline def AcqbulkNoOperandsDefinition: Definition = InstructionDefinitions0.AcqbulkNoOperands
  inline def AcqshminitNoOperandsDefinition: Definition = InstructionDefinitions0.AcqshminitNoOperands
  inline def Al2pPredRegRegDefinition: Definition = InstructionDefinitions0.Al2pPredRegReg
  inline def Al2pPredRegRegImmDefinition: Definition = InstructionDefinitions0.Al2pPredRegRegImm
  inline def Al2pRegRegDefinition: Definition = InstructionDefinitions0.Al2pRegReg
  inline def Al2pRegRegImmDefinition: Definition = InstructionDefinitions0.Al2pRegRegImm
  inline def AldRegAddressRegDefinition: Definition = InstructionDefinitions0.AldRegAddressReg
  inline def ArrivesAddressDefinition: Definition = InstructionDefinitions0.ArrivesAddress
  inline def AstAddressRegRegDefinition: Definition = InstructionDefinitions0.AstAddressRegReg
  inline def AtomPredRegAddressDefinition: Definition = InstructionDefinitions0.AtomPredRegAddress
  inline def AtomPredRegAddressRegDefinition: Definition = InstructionDefinitions0.AtomPredRegAddressReg
  inline def AtomPredRegAddressRegRegDefinition: Definition = InstructionDefinitions0.AtomPredRegAddressRegReg
  inline def AtomRegAddressRegDefinition: Definition = InstructionDefinitions0.AtomRegAddressReg
  inline def AtomRegAddressRegRegDefinition: Definition = InstructionDefinitions0.AtomRegAddressRegReg
  inline def AtomgPredRegAddressRegDefinition: Definition = InstructionDefinitions0.AtomgPredRegAddressReg
  inline def AtomgPredRegAddressRegRegDefinition: Definition = InstructionDefinitions0.AtomgPredRegAddressRegReg
  inline def AtomsPredAddressRegRegDefinition: Definition = InstructionDefinitions0.AtomsPredAddressRegReg
  inline def AtomsRegAddressDefinition: Definition = InstructionDefinitions0.AtomsRegAddress
  inline def AtomsRegAddressRegDefinition: Definition = InstructionDefinitions0.AtomsRegAddressReg
  inline def AtomsRegAddressRegRegDefinition: Definition = InstructionDefinitions0.AtomsRegAddressRegReg
  inline def B2rRegDefinition: Definition = InstructionDefinitions0.B2rReg
  inline def B2rRegImmDefinition: Definition = InstructionDefinitions0.B2rRegImm
  inline def B2rRegPredDefinition: Definition = InstructionDefinitions0.B2rRegPred
  inline def BarNoOperandsDefinition: Definition = InstructionDefinitions0.BarNoOperands
  inline def BarImmDefinition: Definition = InstructionDefinitions0.BarImm
  inline def BarImmImmDefinition: Definition = InstructionDefinitions0.BarImmImm
  inline def BarImmImmPredDefinition: Definition = InstructionDefinitions0.BarImmImmPred
  inline def BarImmPredDefinition: Definition = InstructionDefinitions0.BarImmPred
  inline def BarImmRegDefinition: Definition = InstructionDefinitions0.BarImmReg
  inline def BarImmRegPredDefinition: Definition = InstructionDefinitions0.BarImmRegPred
  inline def BarRegDefinition: Definition = InstructionDefinitions0.BarReg
  inline def BarRegImmDefinition: Definition = InstructionDefinitions0.BarRegImm
  inline def BarRegImmPredDefinition: Definition = InstructionDefinitions0.BarRegImmPred
  inline def BarRegPredDefinition: Definition = InstructionDefinitions0.BarRegPred
  inline def BarRegRegDefinition: Definition = InstructionDefinitions0.BarRegReg
  inline def BarRegRegPredDefinition: Definition = InstructionDefinitions0.BarRegRegPred
  inline def BfeRegRegConstDefinition: Definition = InstructionDefinitions0.BfeRegRegConst
  inline def BfeRegRegImmDefinition: Definition = InstructionDefinitions0.BfeRegRegImm
  inline def BfeRegRegRegDefinition: Definition = InstructionDefinitions0.BfeRegRegReg
  inline def BfiRegRegConstRegDefinition: Definition = InstructionDefinitions0.BfiRegRegConstReg
  inline def BfiRegRegImmRegDefinition: Definition = InstructionDefinitions0.BfiRegRegImmReg
  inline def BfiRegRegRegConstDefinition: Definition = InstructionDefinitions0.BfiRegRegRegConst
  inline def BfiRegRegRegRegDefinition: Definition = InstructionDefinitions0.BfiRegRegRegReg
  inline def BgmmaRegAddressRegDefinition: Definition = InstructionDefinitions0.BgmmaRegAddressReg
  inline def BgmmaRegAddressRegPredDefinition: Definition = InstructionDefinitions0.BgmmaRegAddressRegPred
  inline def BgmmaRegAddressRegRegDefinition: Definition = InstructionDefinitions0.BgmmaRegAddressRegReg
  inline def BgmmaRegAddressRegRegPredDefinition: Definition = InstructionDefinitions0.BgmmaRegAddressRegRegPred
  inline def BgmmaRegRegAddressRegDefinition: Definition = InstructionDefinitions0.BgmmaRegRegAddressReg
  inline def BgmmaRegRegAddressRegPredDefinition: Definition = InstructionDefinitions0.BgmmaRegRegAddressRegPred
  inline def BmmaRegRegRegRegDefinition: Definition = InstructionDefinitions0.BmmaRegRegRegReg
  inline def BmmaRegRegRegRegPredDefinition: Definition = InstructionDefinitions0.BmmaRegRegRegRegPred
  inline def BmovAddressImmDefinition: Definition = InstructionDefinitions0.BmovAddressImm
  inline def BmovConstDefinition: Definition = InstructionDefinitions0.BmovConst
  inline def BmovImmDefinition: Definition = InstructionDefinitions0.BmovImm
  inline def BmovRegDefinition: Definition = InstructionDefinitions0.BmovReg
  inline def BmovRegAddressImmDefinition: Definition = InstructionDefinitions0.BmovRegAddressImm
  inline def BmovRegConstDefinition: Definition = InstructionDefinitions0.BmovRegConst
  inline def BmovRegImmDefinition: Definition = InstructionDefinitions0.BmovRegImm
  inline def BmovRegRegDefinition: Definition = InstructionDefinitions0.BmovRegReg
  inline def BmovRegSymbolDefinition: Definition = InstructionDefinitions0.BmovRegSymbol
  inline def BmovSymbolAddressImmDefinition: Definition = InstructionDefinitions0.BmovSymbolAddressImm
  inline def BmovSymbolConstDefinition: Definition = InstructionDefinitions0.BmovSymbolConst
  inline def BmovSymbolImmDefinition: Definition = InstructionDefinitions0.BmovSymbolImm
  inline def BmovSymbolRegDefinition: Definition = InstructionDefinitions0.BmovSymbolReg
  inline def BmskRegRegAddressImmDefinition: Definition = InstructionDefinitions0.BmskRegRegAddressImm
  inline def BmskRegRegConstDefinition: Definition = InstructionDefinitions0.BmskRegRegConst
  inline def BmskRegRegImmDefinition: Definition = InstructionDefinitions0.BmskRegRegImm
  inline def BmskRegRegRegDefinition: Definition = InstructionDefinitions0.BmskRegRegReg
  inline def BptNoOperandsDefinition: Definition = InstructionDefinitions0.BptNoOperands
  inline def BptImmDefinition: Definition = InstructionDefinitions0.BptImm
  inline def BraConstDefinition: Definition = InstructionDefinitions0.BraConst
  inline def BraImmDefinition: Definition = InstructionDefinitions0.BraImm
  inline def BraPredImmDefinition: Definition = InstructionDefinitions0.BraPredImm
  inline def BraPredPredImmDefinition: Definition = InstructionDefinitions0.BraPredPredImm
  inline def BraPredRegImmDefinition: Definition = InstructionDefinitions0.BraPredRegImm
  inline def BraRegImmDefinition: Definition = InstructionDefinitions0.BraRegImm
  inline def BraSymbolConstDefinition: Definition = InstructionDefinitions0.BraSymbolConst
  inline def BraSymbolImmDefinition: Definition = InstructionDefinitions0.BraSymbolImm
  inline def BraSymbolImmImmDefinition: Definition = InstructionDefinitions0.BraSymbolImmImm
  inline def BraSymbolSymbolImmImmDefinition: Definition = InstructionDefinitions0.BraSymbolSymbolImmImm
  inline def BreakPredRegDefinition: Definition = InstructionDefinitions0.BreakPredReg
  inline def BreakRegDefinition: Definition = InstructionDefinitions0.BreakReg
  inline def BrevRegAddressImmDefinition: Definition = InstructionDefinitions0.BrevRegAddressImm
  inline def BrevRegConstDefinition: Definition = InstructionDefinitions0.BrevRegConst
  inline def BrevRegImmDefinition: Definition = InstructionDefinitions0.BrevRegImm
  inline def BrevRegRegDefinition: Definition = InstructionDefinitions0.BrevRegReg
  inline def BrkNoOperandsDefinition: Definition = InstructionDefinitions0.BrkNoOperands
  inline def BrxConstDefinition: Definition = InstructionDefinitions0.BrxConst
  inline def BrxPredRegDefinition: Definition = InstructionDefinitions0.BrxPredReg
  inline def BrxPredRegImmDefinition: Definition = InstructionDefinitions0.BrxPredRegImm
  inline def BrxRegDefinition: Definition = InstructionDefinitions0.BrxReg
  inline def BrxRegImmDefinition: Definition = InstructionDefinitions0.BrxRegImm
  inline def BrxSymbolConstDefinition: Definition = InstructionDefinitions0.BrxSymbolConst
  inline def BrxSymbolImmAddressDefinition: Definition = InstructionDefinitions0.BrxSymbolImmAddress
  inline def BrxSymbolRegDefinition: Definition = InstructionDefinitions0.BrxSymbolReg
  inline def BrxSymbolRegImmDefinition: Definition = InstructionDefinitions0.BrxSymbolRegImm
  inline def BrxSymbolSymbolImmAddressDefinition: Definition = InstructionDefinitions0.BrxSymbolSymbolImmAddress
  inline def BrxuPredRegDefinition: Definition = InstructionDefinitions0.BrxuPredReg
  inline def BrxuPredRegImmDefinition: Definition = InstructionDefinitions0.BrxuPredRegImm
  inline def BrxuRegDefinition: Definition = InstructionDefinitions0.BrxuReg
  inline def BrxuRegImmDefinition: Definition = InstructionDefinitions0.BrxuRegImm
  inline def BssyPredRegImmDefinition: Definition = InstructionDefinitions0.BssyPredRegImm
  inline def BssyRegImmDefinition: Definition = InstructionDefinitions0.BssyRegImm
  inline def BsyncPredRegDefinition: Definition = InstructionDefinitions0.BsyncPredReg
  inline def BsyncRegDefinition: Definition = InstructionDefinitions0.BsyncReg
  inline def CalConstDefinition: Definition = InstructionDefinitions0.CalConst
  inline def CalImmDefinition: Definition = InstructionDefinitions0.CalImm
  inline def CallConstDefinition: Definition = InstructionDefinitions0.CallConst
  inline def CallImmDefinition: Definition = InstructionDefinitions0.CallImm
  inline def CallPredConstDefinition: Definition = InstructionDefinitions0.CallPredConst
  inline def CallPredImmDefinition: Definition = InstructionDefinitions0.CallPredImm
  inline def CallPredRegDefinition: Definition = InstructionDefinitions0.CallPredReg
  inline def CallPredRegImmDefinition: Definition = InstructionDefinitions0.CallPredRegImm
  inline def CallRegDefinition: Definition = InstructionDefinitions0.CallReg
  inline def CallRegImmDefinition: Definition = InstructionDefinitions0.CallRegImm
  inline def CctlNoOperandsDefinition: Definition = InstructionDefinitions0.CctlNoOperands
  inline def CctlAddressDefinition: Definition = InstructionDefinitions0.CctlAddress
  inline def CctlAddressAddressDefinition: Definition = InstructionDefinitions0.CctlAddressAddress
  inline def CctlAddressImmDefinition: Definition = InstructionDefinitions0.CctlAddressImm
  inline def CctlConstDefinition: Definition = InstructionDefinitions0.CctlConst
  inline def CctlPredAddressDefinition: Definition = InstructionDefinitions0.CctlPredAddress
  inline def CctlRegAddressDefinition: Definition = InstructionDefinitions0.CctlRegAddress
  inline def CctllNoOperandsDefinition: Definition = InstructionDefinitions0.CctllNoOperands
  inline def CctllAddressDefinition: Definition = InstructionDefinitions0.CctllAddress
  inline def CctllRegAddressDefinition: Definition = InstructionDefinitions0.CctllRegAddress
  inline def CctlsNoOperandsDefinition: Definition = InstructionDefinitions0.CctlsNoOperands
  inline def CctltNoOperandsDefinition: Definition = InstructionDefinitions0.CctltNoOperands
  inline def CctltImmDefinition: Definition = InstructionDefinitions0.CctltImm
  inline def CctltImmImmDefinition: Definition = InstructionDefinitions0.CctltImmImm
  inline def CctltRegDefinition: Definition = InstructionDefinitions0.CctltReg
  inline def CgaerrbarNoOperandsDefinition: Definition = InstructionDefinitions0.CgaerrbarNoOperands
  inline def ClmadRegRegAddressImmRegDefinition: Definition = InstructionDefinitions0.ClmadRegRegAddressImmReg
  inline def ClmadRegRegConstRegDefinition: Definition = InstructionDefinitions0.ClmadRegRegConstReg
  inline def ClmadRegRegRegAddressImmDefinition: Definition = InstructionDefinitions0.ClmadRegRegRegAddressImm
  inline def ClmadRegRegRegConstDefinition: Definition = InstructionDefinitions0.ClmadRegRegRegConst
  inline def ClmadRegRegRegRegDefinition: Definition = InstructionDefinitions0.ClmadRegRegRegReg
  inline def ContNoOperandsDefinition: Definition = InstructionDefinitions0.ContNoOperands
  inline def CreduxRegRegDefinition: Definition = InstructionDefinitions0.CreduxRegReg
  inline def Cs2rRegSpecialDefinition: Definition = InstructionDefinitions0.Cs2rRegSpecial
  inline def Cs2urRegSpecialDefinition: Definition = InstructionDefinitions0.Cs2urRegSpecial
  inline def CsetRegSymbolDefinition: Definition = InstructionDefinitions0.CsetRegSymbol
  inline def CsetRegSymbolPredDefinition: Definition = InstructionDefinitions0.CsetRegSymbolPred
  inline def CsetpPredPredSymbolPredDefinition: Definition = InstructionDefinitions0.CsetpPredPredSymbolPred
  inline def CsetpPredSymbolDefinition: Definition = InstructionDefinitions0.CsetpPredSymbol
  inline def CsmtestImmDefinition: Definition = InstructionDefinitions0.CsmtestImm
  inline def CsmtestPredImmDefinition: Definition = InstructionDefinitions0.CsmtestPredImm
  inline def CsmtestPredPredImmPredDefinition: Definition = InstructionDefinitions0.CsmtestPredPredImmPred
  inline def DaddRegRegAddressImmDefinition: Definition = InstructionDefinitions0.DaddRegRegAddressImm
  inline def DaddRegRegConstDefinition: Definition = InstructionDefinitions0.DaddRegRegConst
  inline def DaddRegRegImmDefinition: Definition = InstructionDefinitions0.DaddRegRegImm
  inline def DaddRegRegRegDefinition: Definition = InstructionDefinitions0.DaddRegRegReg
  inline def DchkPredRegConstDefinition: Definition = InstructionDefinitions0.DchkPredRegConst
  inline def DchkPredRegImmDefinition: Definition = InstructionDefinitions0.DchkPredRegImm
  inline def DchkPredRegRegDefinition: Definition = InstructionDefinitions0.DchkPredRegReg
  inline def DepbarNoOperandsDefinition: Definition = InstructionDefinitions0.DepbarNoOperands
  inline def DepbarGroupDefinition: Definition = InstructionDefinitions0.DepbarGroup
  inline def DepbarImmDefinition: Definition = InstructionDefinitions0.DepbarImm
  inline def DepbarRegImmDefinition: Definition = InstructionDefinitions0.DepbarRegImm
  inline def DepbarRegImmGroupDefinition: Definition = InstructionDefinitions0.DepbarRegImmGroup
  inline def DepbarRegImmImmDefinition: Definition = InstructionDefinitions0.DepbarRegImmImm
  inline def DepbarRegRegDefinition: Definition = InstructionDefinitions0.DepbarRegReg
  inline def DepbarRegRegGroupDefinition: Definition = InstructionDefinitions0.DepbarRegRegGroup
  inline def DepbarRegRegImmDefinition: Definition = InstructionDefinitions0.DepbarRegRegImm
  inline def DfmaRegRegAddressImmRegDefinition: Definition = InstructionDefinitions0.DfmaRegRegAddressImmReg
  inline def DfmaRegRegConstRegDefinition: Definition = InstructionDefinitions0.DfmaRegRegConstReg
  inline def DfmaRegRegImmRegDefinition: Definition = InstructionDefinitions0.DfmaRegRegImmReg
  inline def DfmaRegRegRegAddressImmDefinition: Definition = InstructionDefinitions0.DfmaRegRegRegAddressImm
  inline def DfmaRegRegRegConstDefinition: Definition = InstructionDefinitions0.DfmaRegRegRegConst
  inline def DfmaRegRegRegImmDefinition: Definition = InstructionDefinitions0.DfmaRegRegRegImm
  inline def DfmaRegRegRegRegDefinition: Definition = InstructionDefinitions0.DfmaRegRegRegReg
  inline def DmmaRegRegRegRegDefinition: Definition = InstructionDefinitions0.DmmaRegRegRegReg
  inline def DmmaRegRegRegRegPredDefinition: Definition = InstructionDefinitions0.DmmaRegRegRegRegPred
  inline def DmnmxRegRegConstPredDefinition: Definition = InstructionDefinitions0.DmnmxRegRegConstPred
  inline def DmnmxRegRegImmPredDefinition: Definition = InstructionDefinitions0.DmnmxRegRegImmPred
  inline def DmnmxRegRegRegPredDefinition: Definition = InstructionDefinitions0.DmnmxRegRegRegPred
  inline def DmulRegRegAddressImmDefinition: Definition = InstructionDefinitions0.DmulRegRegAddressImm
  inline def DmulRegRegConstDefinition: Definition = InstructionDefinitions0.DmulRegRegConst
  inline def DmulRegRegImmDefinition: Definition = InstructionDefinitions0.DmulRegRegImm
  inline def DmulRegRegRegDefinition: Definition = InstructionDefinitions0.DmulRegRegReg
  inline def DsetRegRegConstDefinition: Definition = InstructionDefinitions0.DsetRegRegConst
  inline def DsetRegRegConstPredDefinition: Definition = InstructionDefinitions0.DsetRegRegConstPred
  inline def DsetRegRegImmDefinition: Definition = InstructionDefinitions0.DsetRegRegImm
  inline def DsetRegRegImmPredDefinition: Definition = InstructionDefinitions0.DsetRegRegImmPred
  inline def DsetRegRegRegDefinition: Definition = InstructionDefinitions0.DsetRegRegReg
  inline def DsetRegRegRegPredDefinition: Definition = InstructionDefinitions0.DsetRegRegRegPred
  inline def DsetpPredPredRegAddressImmPredDefinition: Definition =
    InstructionDefinitions0.DsetpPredPredRegAddressImmPred
  inline def DsetpPredPredRegConstPredDefinition: Definition = InstructionDefinitions0.DsetpPredPredRegConstPred
  inline def DsetpPredPredRegImmPredDefinition: Definition = InstructionDefinitions0.DsetpPredPredRegImmPred
  inline def DsetpPredPredRegRegPredDefinition: Definition = InstructionDefinitions0.DsetpPredPredRegRegPred
  inline def DsetpPredRegAddressImmDefinition: Definition = InstructionDefinitions0.DsetpPredRegAddressImm
  inline def DsetpPredRegConstDefinition: Definition = InstructionDefinitions0.DsetpPredRegConst
  inline def DsetpPredRegImmDefinition: Definition = InstructionDefinitions0.DsetpPredRegImm
  inline def DsetpPredRegRegDefinition: Definition = InstructionDefinitions0.DsetpPredRegReg
  inline def ElectPredRegDefinition: Definition = InstructionDefinitions0.ElectPredReg
  inline def ElectPredRegPredDefinition: Definition = InstructionDefinitions0.ElectPredRegPred
  inline def ElectPredRegRegDefinition: Definition = InstructionDefinitions0.ElectPredRegReg
  inline def EndcollectiveNoOperandsDefinition: Definition = InstructionDefinitions0.EndcollectiveNoOperands
  inline def EndcollectivePredDefinition: Definition = InstructionDefinitions0.EndcollectivePred
  inline def ErrbarNoOperandsDefinition: Definition = InstructionDefinitions0.ErrbarNoOperands
  inline def ExitNoOperandsDefinition: Definition = InstructionDefinitions0.ExitNoOperands
  inline def ExitPredDefinition: Definition = InstructionDefinitions0.ExitPred
  inline def F2fRegAddressImmDefinition: Definition = InstructionDefinitions0.F2fRegAddressImm
  inline def F2fRegConstDefinition: Definition = InstructionDefinitions0.F2fRegConst
  inline def F2fRegImmDefinition: Definition = InstructionDefinitions0.F2fRegImm
  inline def F2fRegRegDefinition: Definition = InstructionDefinitions0.F2fRegReg
  inline def F2fpRegAddressImmDefinition: Definition = InstructionDefinitions0.F2fpRegAddressImm
  inline def F2fpRegAddressImmRegDefinition: Definition = InstructionDefinitions0.F2fpRegAddressImmReg
  inline def F2fpRegConstDefinition: Definition = InstructionDefinitions0.F2fpRegConst
  inline def F2fpRegConstRegDefinition: Definition = InstructionDefinitions0.F2fpRegConstReg
  inline def F2fpRegImmDefinition: Definition = InstructionDefinitions0.F2fpRegImm
  inline def F2fpRegImmRegDefinition: Definition = InstructionDefinitions0.F2fpRegImmReg
  inline def F2fpRegRegDefinition: Definition = InstructionDefinitions0.F2fpRegReg
  inline def F2fpRegRegAddressImmDefinition: Definition = InstructionDefinitions0.F2fpRegRegAddressImm
  inline def F2fpRegRegAddressImmRegDefinition: Definition = InstructionDefinitions0.F2fpRegRegAddressImmReg
  inline def F2fpRegRegConstDefinition: Definition = InstructionDefinitions0.F2fpRegRegConst
  inline def F2fpRegRegConstRegDefinition: Definition = InstructionDefinitions0.F2fpRegRegConstReg
  inline def F2fpRegRegImmDefinition: Definition = InstructionDefinitions0.F2fpRegRegImm
  inline def F2fpRegRegImmRegDefinition: Definition = InstructionDefinitions0.F2fpRegRegImmReg
  inline def F2fpRegRegRegDefinition: Definition = InstructionDefinitions0.F2fpRegRegReg
  inline def F2fpRegRegRegAddressImmDefinition: Definition = InstructionDefinitions0.F2fpRegRegRegAddressImm
  inline def F2fpRegRegRegConstDefinition: Definition = InstructionDefinitions0.F2fpRegRegRegConst
  inline def F2fpRegRegRegImmDefinition: Definition = InstructionDefinitions0.F2fpRegRegRegImm
  inline def F2fpRegRegRegRegDefinition: Definition = InstructionDefinitions0.F2fpRegRegRegReg
  inline def F2iRegAddressImmDefinition: Definition = InstructionDefinitions0.F2iRegAddressImm
  inline def F2iRegConstDefinition: Definition = InstructionDefinitions0.F2iRegConst
  inline def F2iRegImmDefinition: Definition = InstructionDefinitions0.F2iRegImm
  inline def F2iRegRegDefinition: Definition = InstructionDefinitions0.F2iRegReg
  inline def F2ipRegRegAddressImmRegDefinition: Definition = InstructionDefinitions0.F2ipRegRegAddressImmReg
  inline def F2ipRegRegConstRegDefinition: Definition = InstructionDefinitions0.F2ipRegRegConstReg
  inline def F2ipRegRegImmRegDefinition: Definition = InstructionDefinitions0.F2ipRegRegImmReg
  inline def F2ipRegRegRegAddressImmDefinition: Definition = InstructionDefinitions0.F2ipRegRegRegAddressImm
  inline def F2ipRegRegRegConstDefinition: Definition = InstructionDefinitions0.F2ipRegRegRegConst
  inline def F2ipRegRegRegImmDefinition: Definition = InstructionDefinitions0.F2ipRegRegRegImm
  inline def F2ipRegRegRegRegDefinition: Definition = InstructionDefinitions0.F2ipRegRegRegReg
  inline def FaddRegRegAddressImmDefinition: Definition = InstructionDefinitions0.FaddRegRegAddressImm
  inline def FaddRegRegConstDefinition: Definition = InstructionDefinitions0.FaddRegRegConst
  inline def FaddRegRegImmDefinition: Definition = InstructionDefinitions0.FaddRegRegImm
  inline def FaddRegRegRegDefinition: Definition = InstructionDefinitions0.FaddRegRegReg
  inline def Fadd2RegRegImmDefinition: Definition = InstructionDefinitions0.Fadd2RegRegImm
  inline def Fadd2RegRegRegDefinition: Definition = InstructionDefinitions0.Fadd2RegRegReg
  inline def Fadd32iRegRegImmDefinition: Definition = InstructionDefinitions0.Fadd32iRegRegImm
  inline def FccoPredPredRegConstRegDefinition: Definition = InstructionDefinitions0.FccoPredPredRegConstReg
  inline def FccoPredPredRegImmRegDefinition: Definition = InstructionDefinitions0.FccoPredPredRegImmReg
  inline def FccoPredPredRegRegConstDefinition: Definition = InstructionDefinitions0.FccoPredPredRegRegConst
  inline def FccoPredPredRegRegRegDefinition: Definition = InstructionDefinitions0.FccoPredPredRegRegReg
  inline def FccoPredRegConstRegDefinition: Definition = InstructionDefinitions0.FccoPredRegConstReg
  inline def FccoPredRegImmRegDefinition: Definition = InstructionDefinitions0.FccoPredRegImmReg
  inline def FccoPredRegRegConstDefinition: Definition = InstructionDefinitions0.FccoPredRegRegConst
  inline def FccoPredRegRegRegDefinition: Definition = InstructionDefinitions0.FccoPredRegRegReg
  inline def FchkPredRegAddressImmDefinition: Definition = InstructionDefinitions0.FchkPredRegAddressImm
  inline def FchkPredRegConstDefinition: Definition = InstructionDefinitions0.FchkPredRegConst
  inline def FchkPredRegImmDefinition: Definition = InstructionDefinitions0.FchkPredRegImm
  inline def FchkPredRegRegDefinition: Definition = InstructionDefinitions0.FchkPredRegReg
  inline def FcmpRegRegConstRegDefinition: Definition = InstructionDefinitions0.FcmpRegRegConstReg
  inline def FcmpRegRegImmRegDefinition: Definition = InstructionDefinitions0.FcmpRegRegImmReg
  inline def FcmpRegRegRegConstDefinition: Definition = InstructionDefinitions0.FcmpRegRegRegConst
  inline def FcmpRegRegRegRegDefinition: Definition = InstructionDefinitions1.FcmpRegRegRegReg
  inline def FenceNoOperandsDefinition: Definition = InstructionDefinitions1.FenceNoOperands
  inline def FfmaRegRegAddressImmRegDefinition: Definition = InstructionDefinitions1.FfmaRegRegAddressImmReg
  inline def FfmaRegRegConstRegDefinition: Definition = InstructionDefinitions1.FfmaRegRegConstReg
  inline def FfmaRegRegImmRegDefinition: Definition = InstructionDefinitions1.FfmaRegRegImmReg
  inline def FfmaRegRegRegAddressImmDefinition: Definition = InstructionDefinitions1.FfmaRegRegRegAddressImm
  inline def FfmaRegRegRegConstDefinition: Definition = InstructionDefinitions1.FfmaRegRegRegConst
  inline def FfmaRegRegRegImmDefinition: Definition = InstructionDefinitions1.FfmaRegRegRegImm
  inline def FfmaRegRegRegRegDefinition: Definition = InstructionDefinitions1.FfmaRegRegRegReg
  inline def Ffma2RegRegImmRegDefinition: Definition = InstructionDefinitions1.Ffma2RegRegImmReg
  inline def Ffma2RegRegRegImmDefinition: Definition = InstructionDefinitions1.Ffma2RegRegRegImm
  inline def Ffma2RegRegRegRegDefinition: Definition = InstructionDefinitions1.Ffma2RegRegRegReg
  inline def Ffma32iRegRegImmDefinition: Definition = InstructionDefinitions1.Ffma32iRegRegImm
  inline def Ffma32iRegRegImmRegDefinition: Definition = InstructionDefinitions1.Ffma32iRegRegImmReg
  inline def FhaddRegRegRegDefinition: Definition = InstructionDefinitions1.FhaddRegRegReg
  inline def FhfmaRegRegRegRegDefinition: Definition = InstructionDefinitions1.FhfmaRegRegRegReg
  inline def FloRegAddressImmDefinition: Definition = InstructionDefinitions1.FloRegAddressImm
  inline def FloRegConstDefinition: Definition = InstructionDefinitions1.FloRegConst
  inline def FloRegImmDefinition: Definition = InstructionDefinitions1.FloRegImm
  inline def FloRegPredAddressImmDefinition: Definition = InstructionDefinitions1.FloRegPredAddressImm
  inline def FloRegPredConstDefinition: Definition = InstructionDefinitions1.FloRegPredConst
  inline def FloRegPredImmDefinition: Definition = InstructionDefinitions1.FloRegPredImm
  inline def FloRegPredRegDefinition: Definition = InstructionDefinitions1.FloRegPredReg
  inline def FloRegRegDefinition: Definition = InstructionDefinitions1.FloRegReg
  inline def FmnmxRegPredRegAddressImmPredDefinition: Definition = InstructionDefinitions1.FmnmxRegPredRegAddressImmPred
  inline def FmnmxRegPredRegConstPredDefinition: Definition = InstructionDefinitions1.FmnmxRegPredRegConstPred
  inline def FmnmxRegPredRegImmPredDefinition: Definition = InstructionDefinitions1.FmnmxRegPredRegImmPred
  inline def FmnmxRegPredRegRegPredDefinition: Definition = InstructionDefinitions1.FmnmxRegPredRegRegPred
  inline def FmnmxRegRegAddressImmPredDefinition: Definition = InstructionDefinitions1.FmnmxRegRegAddressImmPred
  inline def FmnmxRegRegConstPredDefinition: Definition = InstructionDefinitions1.FmnmxRegRegConstPred
  inline def FmnmxRegRegImmPredDefinition: Definition = InstructionDefinitions1.FmnmxRegRegImmPred
  inline def FmnmxRegRegRegPredDefinition: Definition = InstructionDefinitions1.FmnmxRegRegRegPred
  inline def Fmnmx3RegRegImmRegPredDefinition: Definition = InstructionDefinitions1.Fmnmx3RegRegImmRegPred
  inline def Fmnmx3RegRegRegRegPredDefinition: Definition = InstructionDefinitions1.Fmnmx3RegRegRegRegPred
  inline def FmulRegRegAddressImmDefinition: Definition = InstructionDefinitions1.FmulRegRegAddressImm
  inline def FmulRegRegConstDefinition: Definition = InstructionDefinitions1.FmulRegRegConst
  inline def FmulRegRegImmDefinition: Definition = InstructionDefinitions1.FmulRegRegImm
  inline def FmulRegRegRegDefinition: Definition = InstructionDefinitions1.FmulRegRegReg
  inline def Fmul2RegRegImmDefinition: Definition = InstructionDefinitions1.Fmul2RegRegImm
  inline def Fmul2RegRegRegDefinition: Definition = InstructionDefinitions1.Fmul2RegRegReg
  inline def Fmul32iRegRegImmDefinition: Definition = InstructionDefinitions1.Fmul32iRegRegImm
  inline def FootprintPredRegRegRegRegDefinition: Definition = InstructionDefinitions1.FootprintPredRegRegRegReg
  inline def FootprintPredRegRegRegRegImmImmDefinition: Definition =
    InstructionDefinitions1.FootprintPredRegRegRegRegImmImm
  inline def FootprintPredRegRegRegRegImmImmImmDefinition: Definition =
    InstructionDefinitions1.FootprintPredRegRegRegRegImmImmImm
  inline def FootprintPredRegRegRegRegRegDefinition: Definition = InstructionDefinitions1.FootprintPredRegRegRegRegReg
  inline def FootprintPredRegRegRegRegRegImmDefinition: Definition =
    InstructionDefinitions1.FootprintPredRegRegRegRegRegImm
  inline def FootprintRegRegRegRegDefinition: Definition = InstructionDefinitions1.FootprintRegRegRegReg
  inline def FootprintRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions1.FootprintRegRegRegRegImmImm
  inline def FootprintRegRegRegRegImmImmImmDefinition: Definition =
    InstructionDefinitions1.FootprintRegRegRegRegImmImmImm
  inline def FootprintRegRegRegRegRegDefinition: Definition = InstructionDefinitions1.FootprintRegRegRegRegReg
  inline def FootprintRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions1.FootprintRegRegRegRegRegImm
  inline def FrndRegAddressImmDefinition: Definition = InstructionDefinitions1.FrndRegAddressImm
  inline def FrndRegConstDefinition: Definition = InstructionDefinitions1.FrndRegConst
  inline def FrndRegImmDefinition: Definition = InstructionDefinitions1.FrndRegImm
  inline def FrndRegRegDefinition: Definition = InstructionDefinitions1.FrndRegReg
  inline def FselRegRegAddressImmPredDefinition: Definition = InstructionDefinitions1.FselRegRegAddressImmPred
  inline def FselRegRegConstPredDefinition: Definition = InstructionDefinitions1.FselRegRegConstPred
  inline def FselRegRegImmPredDefinition: Definition = InstructionDefinitions1.FselRegRegImmPred
  inline def FselRegRegRegPredDefinition: Definition = InstructionDefinitions1.FselRegRegRegPred
  inline def FsetRegRegAddressImmDefinition: Definition = InstructionDefinitions1.FsetRegRegAddressImm
  inline def FsetRegRegAddressImmPredDefinition: Definition = InstructionDefinitions1.FsetRegRegAddressImmPred
  inline def FsetRegRegConstDefinition: Definition = InstructionDefinitions1.FsetRegRegConst
  inline def FsetRegRegConstPredDefinition: Definition = InstructionDefinitions1.FsetRegRegConstPred
  inline def FsetRegRegImmDefinition: Definition = InstructionDefinitions1.FsetRegRegImm
  inline def FsetRegRegImmPredDefinition: Definition = InstructionDefinitions1.FsetRegRegImmPred
  inline def FsetRegRegRegDefinition: Definition = InstructionDefinitions1.FsetRegRegReg
  inline def FsetRegRegRegPredDefinition: Definition = InstructionDefinitions1.FsetRegRegRegPred
  inline def FsetpPredPredRegAddressImmPredDefinition: Definition =
    InstructionDefinitions1.FsetpPredPredRegAddressImmPred
  inline def FsetpPredPredRegConstPredDefinition: Definition = InstructionDefinitions1.FsetpPredPredRegConstPred
  inline def FsetpPredPredRegImmPredDefinition: Definition = InstructionDefinitions1.FsetpPredPredRegImmPred
  inline def FsetpPredPredRegRegPredDefinition: Definition = InstructionDefinitions1.FsetpPredPredRegRegPred
  inline def FsetpPredRegAddressImmDefinition: Definition = InstructionDefinitions1.FsetpPredRegAddressImm
  inline def FsetpPredRegConstDefinition: Definition = InstructionDefinitions1.FsetpPredRegConst
  inline def FsetpPredRegImmDefinition: Definition = InstructionDefinitions1.FsetpPredRegImm
  inline def FsetpPredRegRegDefinition: Definition = InstructionDefinitions1.FsetpPredRegReg
  inline def FswzRegRegRegDefinition: Definition = InstructionDefinitions1.FswzRegRegReg
  inline def FswzaddRegRegRegDefinition: Definition = InstructionDefinitions1.FswzaddRegRegReg
  inline def GatherRegRegRegRegImmDefinition: Definition = InstructionDefinitions1.GatherRegRegRegRegImm
  inline def GatherRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions1.GatherRegRegRegRegImmImm
  inline def GatherRegRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions1.GatherRegRegRegRegImmImmImm
  inline def GetcrsptrRegDefinition: Definition = InstructionDefinitions1.GetcrsptrReg
  inline def GetlmembaseRegDefinition: Definition = InstructionDefinitions1.GetlmembaseReg
  inline def Hadd2NoOperandsDefinition: Definition = InstructionDefinitions1.Hadd2NoOperands
  inline def Hadd2ConstDefinition: Definition = InstructionDefinitions1.Hadd2Const
  inline def Hadd2ImmImmDefinition: Definition = InstructionDefinitions1.Hadd2ImmImm
  inline def Hadd2RegRegAddressImmDefinition: Definition = InstructionDefinitions1.Hadd2RegRegAddressImm
  inline def Hadd2RegRegConstDefinition: Definition = InstructionDefinitions1.Hadd2RegRegConst
  inline def Hadd2RegRegImmDefinition: Definition = InstructionDefinitions1.Hadd2RegRegImm
  inline def Hadd2RegRegImmImmDefinition: Definition = InstructionDefinitions1.Hadd2RegRegImmImm
  inline def Hadd2RegRegRegDefinition: Definition = InstructionDefinitions1.Hadd2RegRegReg
  inline def Hadd232iImmImmDefinition: Definition = InstructionDefinitions1.Hadd232iImmImm
  inline def Hadd232iRegRegImmImmDefinition: Definition = InstructionDefinitions1.Hadd232iRegRegImmImm
  inline def Hfma2NoOperandsDefinition: Definition = InstructionDefinitions1.Hfma2NoOperands
  inline def Hfma2ConstDefinition: Definition = InstructionDefinitions1.Hfma2Const
  inline def Hfma2ImmImmDefinition: Definition = InstructionDefinitions1.Hfma2ImmImm
  inline def Hfma2RegRegAddressImmRegDefinition: Definition = InstructionDefinitions1.Hfma2RegRegAddressImmReg
  inline def Hfma2RegRegAddressImmRegPredDefinition: Definition = InstructionDefinitions1.Hfma2RegRegAddressImmRegPred
  inline def Hfma2RegRegConstRegDefinition: Definition = InstructionDefinitions1.Hfma2RegRegConstReg
  inline def Hfma2RegRegConstRegPredDefinition: Definition = InstructionDefinitions1.Hfma2RegRegConstRegPred
  inline def Hfma2RegRegImmImmRegDefinition: Definition = InstructionDefinitions1.Hfma2RegRegImmImmReg
  inline def Hfma2RegRegImmImmRegPredDefinition: Definition = InstructionDefinitions1.Hfma2RegRegImmImmRegPred
  inline def Hfma2RegRegRegAddressImmDefinition: Definition = InstructionDefinitions1.Hfma2RegRegRegAddressImm
  inline def Hfma2RegRegRegAddressImmPredDefinition: Definition = InstructionDefinitions1.Hfma2RegRegRegAddressImmPred
  inline def Hfma2RegRegRegConstDefinition: Definition = InstructionDefinitions1.Hfma2RegRegRegConst
  inline def Hfma2RegRegRegConstPredDefinition: Definition = InstructionDefinitions1.Hfma2RegRegRegConstPred
  inline def Hfma2RegRegRegImmImmDefinition: Definition = InstructionDefinitions1.Hfma2RegRegRegImmImm
  inline def Hfma2RegRegRegImmImmPredDefinition: Definition = InstructionDefinitions1.Hfma2RegRegRegImmImmPred
  inline def Hfma2RegRegRegRegDefinition: Definition = InstructionDefinitions1.Hfma2RegRegRegReg
  inline def Hfma2RegRegRegRegPredDefinition: Definition = InstructionDefinitions1.Hfma2RegRegRegRegPred
  inline def Hfma232iRegRegImmImmDefinition: Definition = InstructionDefinitions1.Hfma232iRegRegImmImm
  inline def Hfma232iRegRegImmImmRegDefinition: Definition = InstructionDefinitions1.Hfma232iRegRegImmImmReg
  inline def HgmmaRegAddressRegDefinition: Definition = InstructionDefinitions1.HgmmaRegAddressReg
  inline def HgmmaRegAddressRegPredDefinition: Definition = InstructionDefinitions1.HgmmaRegAddressRegPred
  inline def HgmmaRegAddressRegPredRegImmDefinition: Definition = InstructionDefinitions1.HgmmaRegAddressRegPredRegImm
  inline def HgmmaRegAddressRegRegDefinition: Definition = InstructionDefinitions1.HgmmaRegAddressRegReg
  inline def HgmmaRegAddressRegRegImmDefinition: Definition = InstructionDefinitions1.HgmmaRegAddressRegRegImm
  inline def HgmmaRegAddressRegRegPredDefinition: Definition = InstructionDefinitions1.HgmmaRegAddressRegRegPred
  inline def HgmmaRegAddressRegRegPredRegImmDefinition: Definition =
    InstructionDefinitions1.HgmmaRegAddressRegRegPredRegImm
  inline def HgmmaRegAddressRegRegRegImmDefinition: Definition = InstructionDefinitions1.HgmmaRegAddressRegRegRegImm
  inline def HgmmaRegRegAddressRegDefinition: Definition = InstructionDefinitions1.HgmmaRegRegAddressReg
  inline def HgmmaRegRegAddressRegPredDefinition: Definition = InstructionDefinitions1.HgmmaRegRegAddressRegPred
  inline def HgmmaRegRegAddressRegPredRegImmDefinition: Definition =
    InstructionDefinitions1.HgmmaRegRegAddressRegPredRegImm
  inline def HgmmaRegRegAddressRegRegImmDefinition: Definition = InstructionDefinitions1.HgmmaRegRegAddressRegRegImm
  inline def HmmaAddressRegRegAddressDefinition: Definition = InstructionDefinitions1.HmmaAddressRegRegAddress
  inline def HmmaAddressRegRegAddressPredDefinition: Definition = InstructionDefinitions1.HmmaAddressRegRegAddressPred
  inline def HmmaAddressRegRegAddressPredRegImmDefinition: Definition =
    InstructionDefinitions1.HmmaAddressRegRegAddressPredRegImm
  inline def HmmaAddressRegRegAddressRegImmDefinition: Definition =
    InstructionDefinitions1.HmmaAddressRegRegAddressRegImm
  inline def HmmaRegRegRegRegDefinition: Definition = InstructionDefinitions1.HmmaRegRegRegReg
  inline def HmmaRegRegRegRegPredDefinition: Definition = InstructionDefinitions1.HmmaRegRegRegRegPred
  inline def HmmaRegRegRegRegPredRegImmDefinition: Definition = InstructionDefinitions1.HmmaRegRegRegRegPredRegImm
  inline def HmmaRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions1.HmmaRegRegRegRegRegImm
  inline def Hmnmx2RegPredPredRegAddressImmPredDefinition: Definition =
    InstructionDefinitions1.Hmnmx2RegPredPredRegAddressImmPred
  inline def Hmnmx2RegPredPredRegConstPredDefinition: Definition = InstructionDefinitions1.Hmnmx2RegPredPredRegConstPred
  inline def Hmnmx2RegPredPredRegImmImmPredDefinition: Definition =
    InstructionDefinitions1.Hmnmx2RegPredPredRegImmImmPred
  inline def Hmnmx2RegPredPredRegRegPredDefinition: Definition = InstructionDefinitions1.Hmnmx2RegPredPredRegRegPred
  inline def Hmnmx2RegRegAddressImmPredDefinition: Definition = InstructionDefinitions1.Hmnmx2RegRegAddressImmPred
  inline def Hmnmx2RegRegConstPredDefinition: Definition = InstructionDefinitions1.Hmnmx2RegRegConstPred
  inline def Hmnmx2RegRegImmImmPredDefinition: Definition = InstructionDefinitions1.Hmnmx2RegRegImmImmPred
  inline def Hmnmx2RegRegRegPredDefinition: Definition = InstructionDefinitions1.Hmnmx2RegRegRegPred
  inline def Hmul2NoOperandsDefinition: Definition = InstructionDefinitions1.Hmul2NoOperands
  inline def Hmul2ConstDefinition: Definition = InstructionDefinitions1.Hmul2Const
  inline def Hmul2ImmImmDefinition: Definition = InstructionDefinitions1.Hmul2ImmImm
  inline def Hmul2RegRegAddressImmDefinition: Definition = InstructionDefinitions1.Hmul2RegRegAddressImm
  inline def Hmul2RegRegConstDefinition: Definition = InstructionDefinitions1.Hmul2RegRegConst
  inline def Hmul2RegRegImmImmDefinition: Definition = InstructionDefinitions1.Hmul2RegRegImmImm
  inline def Hmul2RegRegRegDefinition: Definition = InstructionDefinitions1.Hmul2RegRegReg
  inline def Hmul232iImmImmDefinition: Definition = InstructionDefinitions1.Hmul232iImmImm
  inline def Hmul232iRegRegImmImmDefinition: Definition = InstructionDefinitions1.Hmul232iRegRegImmImm
  inline def Hset2NoOperandsDefinition: Definition = InstructionDefinitions1.Hset2NoOperands
  inline def Hset2ConstDefinition: Definition = InstructionDefinitions1.Hset2Const
  inline def Hset2ConstPredDefinition: Definition = InstructionDefinitions1.Hset2ConstPred
  inline def Hset2ImmImmDefinition: Definition = InstructionDefinitions1.Hset2ImmImm
  inline def Hset2ImmImmPredDefinition: Definition = InstructionDefinitions1.Hset2ImmImmPred
  inline def Hset2PredDefinition: Definition = InstructionDefinitions1.Hset2Pred
  inline def Hset2RegRegAddressImmDefinition: Definition = InstructionDefinitions1.Hset2RegRegAddressImm
  inline def Hset2RegRegAddressImmPredDefinition: Definition = InstructionDefinitions1.Hset2RegRegAddressImmPred
  inline def Hset2RegRegConstDefinition: Definition = InstructionDefinitions1.Hset2RegRegConst
  inline def Hset2RegRegConstPredDefinition: Definition = InstructionDefinitions1.Hset2RegRegConstPred
  inline def Hset2RegRegImmImmDefinition: Definition = InstructionDefinitions1.Hset2RegRegImmImm
  inline def Hset2RegRegImmImmPredDefinition: Definition = InstructionDefinitions1.Hset2RegRegImmImmPred
  inline def Hset2RegRegRegDefinition: Definition = InstructionDefinitions1.Hset2RegRegReg
  inline def Hset2RegRegRegPredDefinition: Definition = InstructionDefinitions1.Hset2RegRegRegPred
  inline def Hsetp2PredPredRegAddressImmDefinition: Definition = InstructionDefinitions1.Hsetp2PredPredRegAddressImm
  inline def Hsetp2PredPredRegAddressImmPredDefinition: Definition =
    InstructionDefinitions1.Hsetp2PredPredRegAddressImmPred
  inline def Hsetp2PredPredRegConstDefinition: Definition = InstructionDefinitions1.Hsetp2PredPredRegConst
  inline def Hsetp2PredPredRegConstPredDefinition: Definition = InstructionDefinitions1.Hsetp2PredPredRegConstPred
  inline def Hsetp2PredPredRegImmImmDefinition: Definition = InstructionDefinitions1.Hsetp2PredPredRegImmImm
  inline def Hsetp2PredPredRegImmImmPredDefinition: Definition = InstructionDefinitions1.Hsetp2PredPredRegImmImmPred
  inline def Hsetp2PredPredRegRegDefinition: Definition = InstructionDefinitions1.Hsetp2PredPredRegReg
  inline def Hsetp2PredPredRegRegPredDefinition: Definition = InstructionDefinitions1.Hsetp2PredPredRegRegPred
  inline def I2fRegAddressImmDefinition: Definition = InstructionDefinitions1.I2fRegAddressImm
  inline def I2fRegConstDefinition: Definition = InstructionDefinitions1.I2fRegConst
  inline def I2fRegImmDefinition: Definition = InstructionDefinitions1.I2fRegImm
  inline def I2fRegRegDefinition: Definition = InstructionDefinitions1.I2fRegReg
  inline def I2fpRegAddressImmDefinition: Definition = InstructionDefinitions1.I2fpRegAddressImm
  inline def I2fpRegConstDefinition: Definition = InstructionDefinitions1.I2fpRegConst
  inline def I2fpRegImmDefinition: Definition = InstructionDefinitions1.I2fpRegImm
  inline def I2fpRegRegDefinition: Definition = InstructionDefinitions1.I2fpRegReg
  inline def I2iRegAddressImmDefinition: Definition = InstructionDefinitions1.I2iRegAddressImm
  inline def I2iRegConstDefinition: Definition = InstructionDefinitions1.I2iRegConst
  inline def I2iRegImmDefinition: Definition = InstructionDefinitions1.I2iRegImm
  inline def I2iRegRegDefinition: Definition = InstructionDefinitions1.I2iRegReg
  inline def I2ipRegRegAddressImmRegDefinition: Definition = InstructionDefinitions1.I2ipRegRegAddressImmReg
  inline def I2ipRegRegConstRegDefinition: Definition = InstructionDefinitions1.I2ipRegRegConstReg
  inline def I2ipRegRegImmRegDefinition: Definition = InstructionDefinitions1.I2ipRegRegImmReg
  inline def I2ipRegRegRegRegDefinition: Definition = InstructionDefinitions1.I2ipRegRegRegReg
  inline def IabsRegAddressImmDefinition: Definition = InstructionDefinitions1.IabsRegAddressImm
  inline def IabsRegConstDefinition: Definition = InstructionDefinitions1.IabsRegConst
  inline def IabsRegImmDefinition: Definition = InstructionDefinitions1.IabsRegImm
  inline def IabsRegRegDefinition: Definition = InstructionDefinitions1.IabsRegReg
  inline def IaddRegPredRegAddressImmDefinition: Definition = InstructionDefinitions1.IaddRegPredRegAddressImm
  inline def IaddRegPredRegAddressImmPredDefinition: Definition = InstructionDefinitions1.IaddRegPredRegAddressImmPred
  inline def IaddRegPredRegConstDefinition: Definition = InstructionDefinitions1.IaddRegPredRegConst
  inline def IaddRegPredRegConstPredDefinition: Definition = InstructionDefinitions1.IaddRegPredRegConstPred
  inline def IaddRegPredRegImmDefinition: Definition = InstructionDefinitions1.IaddRegPredRegImm
  inline def IaddRegPredRegImmPredDefinition: Definition = InstructionDefinitions1.IaddRegPredRegImmPred
  inline def IaddRegPredRegImmRegDefinition: Definition = InstructionDefinitions1.IaddRegPredRegImmReg
  inline def IaddRegPredRegImmRegPredDefinition: Definition = InstructionDefinitions1.IaddRegPredRegImmRegPred
  inline def IaddRegPredRegRegDefinition: Definition = InstructionDefinitions1.IaddRegPredRegReg
  inline def IaddRegPredRegRegPredDefinition: Definition = InstructionDefinitions1.IaddRegPredRegRegPred
  inline def IaddRegPredRegRegRegDefinition: Definition = InstructionDefinitions1.IaddRegPredRegRegReg
  inline def IaddRegPredRegRegRegPredDefinition: Definition = InstructionDefinitions1.IaddRegPredRegRegRegPred
  inline def IaddRegRegAddressImmDefinition: Definition = InstructionDefinitions1.IaddRegRegAddressImm
  inline def IaddRegRegAddressImmPredDefinition: Definition = InstructionDefinitions1.IaddRegRegAddressImmPred
  inline def IaddRegRegConstDefinition: Definition = InstructionDefinitions1.IaddRegRegConst
  inline def IaddRegRegConstPredDefinition: Definition = InstructionDefinitions1.IaddRegRegConstPred
  inline def IaddRegRegImmDefinition: Definition = InstructionDefinitions1.IaddRegRegImm
  inline def IaddRegRegImmPredDefinition: Definition = InstructionDefinitions1.IaddRegRegImmPred
  inline def IaddRegRegImmRegDefinition: Definition = InstructionDefinitions1.IaddRegRegImmReg
  inline def IaddRegRegImmRegPredDefinition: Definition = InstructionDefinitions1.IaddRegRegImmRegPred
  inline def IaddRegRegRegDefinition: Definition = InstructionDefinitions1.IaddRegRegReg
  inline def IaddRegRegRegPredDefinition: Definition = InstructionDefinitions1.IaddRegRegRegPred
  inline def IaddRegRegRegRegDefinition: Definition = InstructionDefinitions1.IaddRegRegRegReg
  inline def IaddRegRegRegRegPredDefinition: Definition = InstructionDefinitions1.IaddRegRegRegRegPred
  inline def Iadd3RegPredPredRegAddressImmRegDefinition: Definition =
    InstructionDefinitions1.Iadd3RegPredPredRegAddressImmReg
  inline def Iadd3RegPredPredRegAddressImmRegPredPredDefinition: Definition =
    InstructionDefinitions1.Iadd3RegPredPredRegAddressImmRegPredPred
  inline def Iadd3RegPredPredRegConstRegDefinition: Definition = InstructionDefinitions1.Iadd3RegPredPredRegConstReg
  inline def Iadd3RegPredPredRegConstRegPredPredDefinition: Definition =
    InstructionDefinitions1.Iadd3RegPredPredRegConstRegPredPred
  inline def Iadd3RegPredPredRegImmRegDefinition: Definition = InstructionDefinitions1.Iadd3RegPredPredRegImmReg
  inline def Iadd3RegPredPredRegImmRegPredPredDefinition: Definition =
    InstructionDefinitions1.Iadd3RegPredPredRegImmRegPredPred
  inline def Iadd3RegPredPredRegRegRegDefinition: Definition = InstructionDefinitions1.Iadd3RegPredPredRegRegReg
  inline def Iadd3RegPredPredRegRegRegPredPredDefinition: Definition =
    InstructionDefinitions1.Iadd3RegPredPredRegRegRegPredPred
  inline def Iadd3RegPredRegAddressImmRegDefinition: Definition = InstructionDefinitions1.Iadd3RegPredRegAddressImmReg
  inline def Iadd3RegPredRegAddressImmRegPredPredDefinition: Definition =
    InstructionDefinitions1.Iadd3RegPredRegAddressImmRegPredPred
  inline def Iadd3RegPredRegConstRegDefinition: Definition = InstructionDefinitions1.Iadd3RegPredRegConstReg
  inline def Iadd3RegPredRegConstRegPredPredDefinition: Definition =
    InstructionDefinitions1.Iadd3RegPredRegConstRegPredPred
  inline def Iadd3RegPredRegImmRegDefinition: Definition = InstructionDefinitions1.Iadd3RegPredRegImmReg
  inline def Iadd3RegPredRegImmRegPredPredDefinition: Definition = InstructionDefinitions1.Iadd3RegPredRegImmRegPredPred
  inline def Iadd3RegPredRegRegRegDefinition: Definition = InstructionDefinitions1.Iadd3RegPredRegRegReg
  inline def Iadd3RegPredRegRegRegPredPredDefinition: Definition = InstructionDefinitions1.Iadd3RegPredRegRegRegPredPred
  inline def Iadd3RegRegAddressImmRegDefinition: Definition = InstructionDefinitions1.Iadd3RegRegAddressImmReg
  inline def Iadd3RegRegAddressImmRegPredPredDefinition: Definition =
    InstructionDefinitions1.Iadd3RegRegAddressImmRegPredPred
  inline def Iadd3RegRegConstRegDefinition: Definition = InstructionDefinitions1.Iadd3RegRegConstReg
  inline def Iadd3RegRegConstRegPredPredDefinition: Definition = InstructionDefinitions1.Iadd3RegRegConstRegPredPred
  inline def Iadd3RegRegImmRegDefinition: Definition = InstructionDefinitions1.Iadd3RegRegImmReg
  inline def Iadd3RegRegImmRegPredPredDefinition: Definition = InstructionDefinitions1.Iadd3RegRegImmRegPredPred
  inline def Iadd3RegRegRegRegDefinition: Definition = InstructionDefinitions1.Iadd3RegRegRegReg
  inline def Iadd3RegRegRegRegPredPredDefinition: Definition = InstructionDefinitions1.Iadd3RegRegRegRegPredPred
  inline def Iadd32iRegPredRegImmDefinition: Definition = InstructionDefinitions1.Iadd32iRegPredRegImm
  inline def Iadd32iRegPredRegImmPredDefinition: Definition = InstructionDefinitions1.Iadd32iRegPredRegImmPred
  inline def Iadd32iRegRegImmDefinition: Definition = InstructionDefinitions1.Iadd32iRegRegImm
  inline def Iadd32iRegRegImmPredDefinition: Definition = InstructionDefinitions1.Iadd32iRegRegImmPred
  inline def IcmpRegRegConstRegDefinition: Definition = InstructionDefinitions1.IcmpRegRegConstReg
  inline def IcmpRegRegImmRegDefinition: Definition = InstructionDefinitions1.IcmpRegRegImmReg
  inline def IcmpRegRegRegConstDefinition: Definition = InstructionDefinitions1.IcmpRegRegRegConst
  inline def IcmpRegRegRegRegDefinition: Definition = InstructionDefinitions1.IcmpRegRegRegReg
  inline def IdeNoOperandsDefinition: Definition = InstructionDefinitions1.IdeNoOperands
  inline def IdeImmDefinition: Definition = InstructionDefinitions1.IdeImm
  inline def IdpRegRegAddressImmRegDefinition: Definition = InstructionDefinitions1.IdpRegRegAddressImmReg
  inline def IdpRegRegConstRegDefinition: Definition = InstructionDefinitions1.IdpRegRegConstReg
  inline def IdpRegRegRegRegDefinition: Definition = InstructionDefinitions1.IdpRegRegRegReg
  inline def Idp4aRegRegAddressImmRegDefinition: Definition = InstructionDefinitions1.Idp4aRegRegAddressImmReg
  inline def Idp4aRegRegConstRegDefinition: Definition = InstructionDefinitions1.Idp4aRegRegConstReg
  inline def Idp4aRegRegRegRegDefinition: Definition = InstructionDefinitions1.Idp4aRegRegRegReg
  inline def IgmmaRegAddressRegDefinition: Definition = InstructionDefinitions1.IgmmaRegAddressReg
  inline def IgmmaRegAddressRegPredDefinition: Definition = InstructionDefinitions1.IgmmaRegAddressRegPred
  inline def IgmmaRegAddressRegPredRegDefinition: Definition = InstructionDefinitions2.IgmmaRegAddressRegPredReg
  inline def IgmmaRegAddressRegPredRegImmDefinition: Definition = InstructionDefinitions2.IgmmaRegAddressRegPredRegImm
  inline def IgmmaRegAddressRegRegDefinition: Definition = InstructionDefinitions2.IgmmaRegAddressRegReg
  inline def IgmmaRegAddressRegRegImmDefinition: Definition = InstructionDefinitions2.IgmmaRegAddressRegRegImm
  inline def IgmmaRegAddressRegRegPredDefinition: Definition = InstructionDefinitions2.IgmmaRegAddressRegRegPred
  inline def IgmmaRegAddressRegRegPredRegDefinition: Definition = InstructionDefinitions2.IgmmaRegAddressRegRegPredReg
  inline def IgmmaRegAddressRegRegPredRegImmDefinition: Definition =
    InstructionDefinitions2.IgmmaRegAddressRegRegPredRegImm
  inline def IgmmaRegAddressRegRegRegDefinition: Definition = InstructionDefinitions2.IgmmaRegAddressRegRegReg
  inline def IgmmaRegAddressRegRegRegImmDefinition: Definition = InstructionDefinitions2.IgmmaRegAddressRegRegRegImm
  inline def IgmmaRegRegAddressRegDefinition: Definition = InstructionDefinitions2.IgmmaRegRegAddressReg
  inline def IgmmaRegRegAddressRegPredDefinition: Definition = InstructionDefinitions2.IgmmaRegRegAddressRegPred
  inline def IgmmaRegRegAddressRegPredRegDefinition: Definition = InstructionDefinitions2.IgmmaRegRegAddressRegPredReg
  inline def IgmmaRegRegAddressRegPredRegImmDefinition: Definition =
    InstructionDefinitions2.IgmmaRegRegAddressRegPredRegImm
  inline def IgmmaRegRegAddressRegRegDefinition: Definition = InstructionDefinitions2.IgmmaRegRegAddressRegReg
  inline def IgmmaRegRegAddressRegRegImmDefinition: Definition = InstructionDefinitions2.IgmmaRegRegAddressRegRegImm
  inline def ImadRegPredRegAddressImmRegDefinition: Definition = InstructionDefinitions2.ImadRegPredRegAddressImmReg
  inline def ImadRegPredRegAddressImmRegPredDefinition: Definition =
    InstructionDefinitions2.ImadRegPredRegAddressImmRegPred
  inline def ImadRegPredRegConstRegDefinition: Definition = InstructionDefinitions2.ImadRegPredRegConstReg
  inline def ImadRegPredRegConstRegPredDefinition: Definition = InstructionDefinitions2.ImadRegPredRegConstRegPred
  inline def ImadRegPredRegImmRegDefinition: Definition = InstructionDefinitions2.ImadRegPredRegImmReg
  inline def ImadRegPredRegImmRegPredDefinition: Definition = InstructionDefinitions2.ImadRegPredRegImmRegPred
  inline def ImadRegPredRegRegAddressImmDefinition: Definition = InstructionDefinitions2.ImadRegPredRegRegAddressImm
  inline def ImadRegPredRegRegAddressImmPredDefinition: Definition =
    InstructionDefinitions2.ImadRegPredRegRegAddressImmPred
  inline def ImadRegPredRegRegConstDefinition: Definition = InstructionDefinitions2.ImadRegPredRegRegConst
  inline def ImadRegPredRegRegConstPredDefinition: Definition = InstructionDefinitions2.ImadRegPredRegRegConstPred
  inline def ImadRegPredRegRegRegDefinition: Definition = InstructionDefinitions2.ImadRegPredRegRegReg
  inline def ImadRegPredRegRegRegPredDefinition: Definition = InstructionDefinitions2.ImadRegPredRegRegRegPred
  inline def ImadRegRegAddressImmRegDefinition: Definition = InstructionDefinitions2.ImadRegRegAddressImmReg
  inline def ImadRegRegAddressImmRegPredDefinition: Definition = InstructionDefinitions2.ImadRegRegAddressImmRegPred
  inline def ImadRegRegConstRegDefinition: Definition = InstructionDefinitions2.ImadRegRegConstReg
  inline def ImadRegRegConstRegPredDefinition: Definition = InstructionDefinitions2.ImadRegRegConstRegPred
  inline def ImadRegRegImmRegDefinition: Definition = InstructionDefinitions2.ImadRegRegImmReg
  inline def ImadRegRegImmRegPredDefinition: Definition = InstructionDefinitions2.ImadRegRegImmRegPred
  inline def ImadRegRegRegAddressImmDefinition: Definition = InstructionDefinitions2.ImadRegRegRegAddressImm
  inline def ImadRegRegRegAddressImmPredDefinition: Definition = InstructionDefinitions2.ImadRegRegRegAddressImmPred
  inline def ImadRegRegRegConstDefinition: Definition = InstructionDefinitions2.ImadRegRegRegConst
  inline def ImadRegRegRegConstPredDefinition: Definition = InstructionDefinitions2.ImadRegRegRegConstPred
  inline def ImadRegRegRegImmDefinition: Definition = InstructionDefinitions2.ImadRegRegRegImm
  inline def ImadRegRegRegImmPredDefinition: Definition = InstructionDefinitions2.ImadRegRegRegImmPred
  inline def ImadRegRegRegRegDefinition: Definition = InstructionDefinitions2.ImadRegRegRegReg
  inline def ImadRegRegRegRegPredDefinition: Definition = InstructionDefinitions2.ImadRegRegRegRegPred
  inline def Imad32iRegRegImmRegDefinition: Definition = InstructionDefinitions2.Imad32iRegRegImmReg
  inline def ImadspRegRegConstRegDefinition: Definition = InstructionDefinitions2.ImadspRegRegConstReg
  inline def ImadspRegRegImmRegDefinition: Definition = InstructionDefinitions2.ImadspRegRegImmReg
  inline def ImadspRegRegRegConstDefinition: Definition = InstructionDefinitions2.ImadspRegRegRegConst
  inline def ImadspRegRegRegRegDefinition: Definition = InstructionDefinitions2.ImadspRegRegRegReg
  inline def ImmaRegRegRegRegDefinition: Definition = InstructionDefinitions2.ImmaRegRegRegReg
  inline def ImmaRegRegRegRegPredDefinition: Definition = InstructionDefinitions2.ImmaRegRegRegRegPred
  inline def ImmaRegRegRegRegPredRegImmDefinition: Definition = InstructionDefinitions2.ImmaRegRegRegRegPredRegImm
  inline def ImmaRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions2.ImmaRegRegRegRegRegImm
  inline def ImnmxPredPredRegRegImmPredDefinition: Definition = InstructionDefinitions2.ImnmxPredPredRegRegImmPred
  inline def ImnmxPredPredRegRegImmPredPredDefinition: Definition =
    InstructionDefinitions2.ImnmxPredPredRegRegImmPredPred
  inline def ImnmxPredPredRegRegRegPredDefinition: Definition = InstructionDefinitions2.ImnmxPredPredRegRegRegPred
  inline def ImnmxPredPredRegRegRegPredPredDefinition: Definition =
    InstructionDefinitions2.ImnmxPredPredRegRegRegPredPred
  inline def ImnmxPredRegRegImmPredDefinition: Definition = InstructionDefinitions2.ImnmxPredRegRegImmPred
  inline def ImnmxPredRegRegImmPredPredDefinition: Definition = InstructionDefinitions2.ImnmxPredRegRegImmPredPred
  inline def ImnmxPredRegRegRegPredDefinition: Definition = InstructionDefinitions2.ImnmxPredRegRegRegPred
  inline def ImnmxPredRegRegRegPredPredDefinition: Definition = InstructionDefinitions2.ImnmxPredRegRegRegPredPred
  inline def ImnmxRegRegAddressImmPredDefinition: Definition = InstructionDefinitions2.ImnmxRegRegAddressImmPred
  inline def ImnmxRegRegConstPredDefinition: Definition = InstructionDefinitions2.ImnmxRegRegConstPred
  inline def ImnmxRegRegImmPredDefinition: Definition = InstructionDefinitions2.ImnmxRegRegImmPred
  inline def ImnmxRegRegImmPredPredDefinition: Definition = InstructionDefinitions2.ImnmxRegRegImmPredPred
  inline def ImnmxRegRegRegPredDefinition: Definition = InstructionDefinitions2.ImnmxRegRegRegPred
  inline def ImnmxRegRegRegPredPredDefinition: Definition = InstructionDefinitions2.ImnmxRegRegRegPredPred
  inline def ImulRegPredRegAddressImmDefinition: Definition = InstructionDefinitions2.ImulRegPredRegAddressImm
  inline def ImulRegPredRegConstDefinition: Definition = InstructionDefinitions2.ImulRegPredRegConst
  inline def ImulRegPredRegImmDefinition: Definition = InstructionDefinitions2.ImulRegPredRegImm
  inline def ImulRegPredRegRegDefinition: Definition = InstructionDefinitions2.ImulRegPredRegReg
  inline def ImulRegRegAddressImmDefinition: Definition = InstructionDefinitions2.ImulRegRegAddressImm
  inline def ImulRegRegConstDefinition: Definition = InstructionDefinitions2.ImulRegRegConst
  inline def ImulRegRegImmDefinition: Definition = InstructionDefinitions2.ImulRegRegImm
  inline def ImulRegRegImmRegDefinition: Definition = InstructionDefinitions2.ImulRegRegImmReg
  inline def ImulRegRegRegDefinition: Definition = InstructionDefinitions2.ImulRegRegReg
  inline def ImulRegRegRegImmDefinition: Definition = InstructionDefinitions2.ImulRegRegRegImm
  inline def ImulRegRegRegRegDefinition: Definition = InstructionDefinitions2.ImulRegRegRegReg
  inline def Imul32iRegPredRegImmDefinition: Definition = InstructionDefinitions2.Imul32iRegPredRegImm
  inline def Imul32iRegRegImmDefinition: Definition = InstructionDefinitions2.Imul32iRegRegImm
  inline def IpaRegAddressDefinition: Definition = InstructionDefinitions2.IpaRegAddress
  inline def IpaRegAddressImmDefinition: Definition = InstructionDefinitions2.IpaRegAddressImm
  inline def IpaRegAddressPredDefinition: Definition = InstructionDefinitions2.IpaRegAddressPred
  inline def IpaRegAddressRegDefinition: Definition = InstructionDefinitions2.IpaRegAddressReg
  inline def IpaRegAddressRegRegDefinition: Definition = InstructionDefinitions2.IpaRegAddressRegReg
  inline def IpaRegAddressRegRegPredDefinition: Definition = InstructionDefinitions2.IpaRegAddressRegRegPred
  inline def IpaRegImmDefinition: Definition = InstructionDefinitions2.IpaRegImm
  inline def IpaRegImmConstDefinition: Definition = InstructionDefinitions2.IpaRegImmConst
  inline def IpaRegImmImmDefinition: Definition = InstructionDefinitions2.IpaRegImmImm
  inline def IpaRegImmPredDefinition: Definition = InstructionDefinitions2.IpaRegImmPred
  inline def IpaRegImmRegDefinition: Definition = InstructionDefinitions2.IpaRegImmReg
  inline def IpaRegImmRegRegDefinition: Definition = InstructionDefinitions2.IpaRegImmRegReg
  inline def IpaRegImmRegRegPredDefinition: Definition = InstructionDefinitions2.IpaRegImmRegRegPred
  inline def IpaRegPredAddressDefinition: Definition = InstructionDefinitions2.IpaRegPredAddress
  inline def IpaRegPredAddressImmDefinition: Definition = InstructionDefinitions2.IpaRegPredAddressImm
  inline def IpaRegPredAddressRegDefinition: Definition = InstructionDefinitions2.IpaRegPredAddressReg
  inline def IpaRegPredImmDefinition: Definition = InstructionDefinitions2.IpaRegPredImm
  inline def IpaRegPredImmConstDefinition: Definition = InstructionDefinitions2.IpaRegPredImmConst
  inline def IpaRegPredImmImmDefinition: Definition = InstructionDefinitions2.IpaRegPredImmImm
  inline def IpaRegPredImmRegDefinition: Definition = InstructionDefinitions2.IpaRegPredImmReg
  inline def IsadRegRegConstRegDefinition: Definition = InstructionDefinitions2.IsadRegRegConstReg
  inline def IsadRegRegImmRegDefinition: Definition = InstructionDefinitions2.IsadRegRegImmReg
  inline def IsadRegRegRegConstDefinition: Definition = InstructionDefinitions2.IsadRegRegRegConst
  inline def IsadRegRegRegRegDefinition: Definition = InstructionDefinitions2.IsadRegRegRegReg
  inline def IsberdRegAddressDefinition: Definition = InstructionDefinitions2.IsberdRegAddress
  inline def IsbewrAddressRegDefinition: Definition = InstructionDefinitions2.IsbewrAddressReg
  inline def IscaddRegPredRegAddressImmImmDefinition: Definition = InstructionDefinitions2.IscaddRegPredRegAddressImmImm
  inline def IscaddRegPredRegConstImmDefinition: Definition = InstructionDefinitions2.IscaddRegPredRegConstImm
  inline def IscaddRegPredRegImmImmDefinition: Definition = InstructionDefinitions2.IscaddRegPredRegImmImm
  inline def IscaddRegPredRegRegImmDefinition: Definition = InstructionDefinitions2.IscaddRegPredRegRegImm
  inline def IscaddRegRegAddressImmImmDefinition: Definition = InstructionDefinitions2.IscaddRegRegAddressImmImm
  inline def IscaddRegRegConstImmDefinition: Definition = InstructionDefinitions2.IscaddRegRegConstImm
  inline def IscaddRegRegImmImmDefinition: Definition = InstructionDefinitions2.IscaddRegRegImmImm
  inline def IscaddRegRegRegImmDefinition: Definition = InstructionDefinitions2.IscaddRegRegRegImm
  inline def Iscadd32iRegPredRegImmImmDefinition: Definition = InstructionDefinitions2.Iscadd32iRegPredRegImmImm
  inline def Iscadd32iRegRegImmImmDefinition: Definition = InstructionDefinitions2.Iscadd32iRegRegImmImm
  inline def IsetRegRegConstDefinition: Definition = InstructionDefinitions2.IsetRegRegConst
  inline def IsetRegRegConstPredDefinition: Definition = InstructionDefinitions2.IsetRegRegConstPred
  inline def IsetRegRegImmDefinition: Definition = InstructionDefinitions2.IsetRegRegImm
  inline def IsetRegRegImmPredDefinition: Definition = InstructionDefinitions2.IsetRegRegImmPred
  inline def IsetRegRegRegDefinition: Definition = InstructionDefinitions2.IsetRegRegReg
  inline def IsetRegRegRegPredDefinition: Definition = InstructionDefinitions2.IsetRegRegRegPred
  inline def IsetpPredPredRegAddressImmPredDefinition: Definition =
    InstructionDefinitions2.IsetpPredPredRegAddressImmPred
  inline def IsetpPredPredRegAddressImmPredPredDefinition: Definition =
    InstructionDefinitions2.IsetpPredPredRegAddressImmPredPred
  inline def IsetpPredPredRegConstPredDefinition: Definition = InstructionDefinitions2.IsetpPredPredRegConstPred
  inline def IsetpPredPredRegConstPredPredDefinition: Definition = InstructionDefinitions2.IsetpPredPredRegConstPredPred
  inline def IsetpPredPredRegImmPredDefinition: Definition = InstructionDefinitions2.IsetpPredPredRegImmPred
  inline def IsetpPredPredRegImmPredPredDefinition: Definition = InstructionDefinitions2.IsetpPredPredRegImmPredPred
  inline def IsetpPredPredRegRegPredDefinition: Definition = InstructionDefinitions2.IsetpPredPredRegRegPred
  inline def IsetpPredPredRegRegPredPredDefinition: Definition = InstructionDefinitions2.IsetpPredPredRegRegPredPred
  inline def IsetpPredRegAddressImmDefinition: Definition = InstructionDefinitions2.IsetpPredRegAddressImm
  inline def IsetpPredRegAddressImmPredDefinition: Definition = InstructionDefinitions2.IsetpPredRegAddressImmPred
  inline def IsetpPredRegConstDefinition: Definition = InstructionDefinitions2.IsetpPredRegConst
  inline def IsetpPredRegConstPredDefinition: Definition = InstructionDefinitions2.IsetpPredRegConstPred
  inline def IsetpPredRegImmDefinition: Definition = InstructionDefinitions2.IsetpPredRegImm
  inline def IsetpPredRegImmPredDefinition: Definition = InstructionDefinitions2.IsetpPredRegImmPred
  inline def IsetpPredRegRegDefinition: Definition = InstructionDefinitions2.IsetpPredRegReg
  inline def IsetpPredRegRegPredDefinition: Definition = InstructionDefinitions2.IsetpPredRegRegPred
  inline def IsubRegConstRegDefinition: Definition = InstructionDefinitions2.IsubRegConstReg
  inline def IsubRegImmRegDefinition: Definition = InstructionDefinitions2.IsubRegImmReg
  inline def IsubRegRegConstDefinition: Definition = InstructionDefinitions2.IsubRegRegConst
  inline def IsubRegRegImmDefinition: Definition = InstructionDefinitions2.IsubRegRegImm
  inline def IsubRegRegRegDefinition: Definition = InstructionDefinitions2.IsubRegRegReg
  inline def JcalConstDefinition: Definition = InstructionDefinitions2.JcalConst
  inline def JcalImmDefinition: Definition = InstructionDefinitions2.JcalImm
  inline def JmpConstDefinition: Definition = InstructionDefinitions2.JmpConst
  inline def JmpImmDefinition: Definition = InstructionDefinitions2.JmpImm
  inline def JmpPredConstDefinition: Definition = InstructionDefinitions2.JmpPredConst
  inline def JmpPredImmDefinition: Definition = InstructionDefinitions2.JmpPredImm
  inline def JmpPredPredConstDefinition: Definition = InstructionDefinitions2.JmpPredPredConst
  inline def JmpPredPredImmDefinition: Definition = InstructionDefinitions2.JmpPredPredImm
  inline def JmpPredRegConstDefinition: Definition = InstructionDefinitions2.JmpPredRegConst
  inline def JmpPredRegImmDefinition: Definition = InstructionDefinitions2.JmpPredRegImm
  inline def JmpRegConstDefinition: Definition = InstructionDefinitions2.JmpRegConst
  inline def JmpRegImmDefinition: Definition = InstructionDefinitions2.JmpRegImm
  inline def JmpSymbolConstDefinition: Definition = InstructionDefinitions2.JmpSymbolConst
  inline def JmpSymbolImmDefinition: Definition = InstructionDefinitions2.JmpSymbolImm
  inline def JmpSymbolImmImmDefinition: Definition = InstructionDefinitions2.JmpSymbolImmImm
  inline def JmpSymbolSymbolImmImmDefinition: Definition = InstructionDefinitions2.JmpSymbolSymbolImmImm
  inline def JmxConstDefinition: Definition = InstructionDefinitions2.JmxConst
  inline def JmxPredRegDefinition: Definition = InstructionDefinitions2.JmxPredReg
  inline def JmxPredRegImmDefinition: Definition = InstructionDefinitions2.JmxPredRegImm
  inline def JmxRegDefinition: Definition = InstructionDefinitions2.JmxReg
  inline def JmxRegImmDefinition: Definition = InstructionDefinitions2.JmxRegImm
  inline def JmxSymbolConstDefinition: Definition = InstructionDefinitions2.JmxSymbolConst
  inline def JmxSymbolImmAddressDefinition: Definition = InstructionDefinitions2.JmxSymbolImmAddress
  inline def JmxSymbolRegDefinition: Definition = InstructionDefinitions2.JmxSymbolReg
  inline def JmxSymbolRegImmDefinition: Definition = InstructionDefinitions2.JmxSymbolRegImm
  inline def JmxSymbolSymbolImmAddressDefinition: Definition = InstructionDefinitions2.JmxSymbolSymbolImmAddress
  inline def JmxuPredRegDefinition: Definition = InstructionDefinitions2.JmxuPredReg
  inline def JmxuPredRegImmDefinition: Definition = InstructionDefinitions2.JmxuPredRegImm
  inline def JmxuRegDefinition: Definition = InstructionDefinitions2.JmxuReg
  inline def JmxuRegImmDefinition: Definition = InstructionDefinitions2.JmxuRegImm
  inline def KilNoOperandsDefinition: Definition = InstructionDefinitions2.KilNoOperands
  inline def KillNoOperandsDefinition: Definition = InstructionDefinitions2.KillNoOperands
  inline def KillPredDefinition: Definition = InstructionDefinitions2.KillPred
  inline def LdPredRegAddressDefinition: Definition = InstructionDefinitions2.LdPredRegAddress
  inline def LdPredRegAddressPredDefinition: Definition = InstructionDefinitions2.LdPredRegAddressPred
  inline def LdRegAddressDefinition: Definition = InstructionDefinitions2.LdRegAddress
  inline def LdRegAddressPredDefinition: Definition = InstructionDefinitions2.LdRegAddressPred
  inline def LdcRegAddressAddressDefinition: Definition = InstructionDefinitions2.LdcRegAddressAddress
  inline def LdcRegConstDefinition: Definition = InstructionDefinitions2.LdcRegConst
  inline def LdcuRegAddressDefinition: Definition = InstructionDefinitions2.LdcuRegAddress
  inline def LdcuRegAddressAddressDefinition: Definition = InstructionDefinitions2.LdcuRegAddressAddress
  inline def LdcuRegAddressPredDefinition: Definition = InstructionDefinitions2.LdcuRegAddressPred
  inline def LdcuRegConstDefinition: Definition = InstructionDefinitions2.LdcuRegConst
  inline def LdcuRegRegAddressDefinition: Definition = InstructionDefinitions2.LdcuRegRegAddress
  inline def LdcuRegRegAddressAddressDefinition: Definition = InstructionDefinitions2.LdcuRegRegAddressAddress
  inline def LdcuRegRegAddressAddressImmDefinition: Definition = InstructionDefinitions2.LdcuRegRegAddressAddressImm
  inline def LdcuRegRegAddressImmDefinition: Definition = InstructionDefinitions2.LdcuRegRegAddressImm
  inline def LdcuRegRegAddressImmPredDefinition: Definition = InstructionDefinitions2.LdcuRegRegAddressImmPred
  inline def LdcuRegRegAddressPredDefinition: Definition = InstructionDefinitions2.LdcuRegRegAddressPred
  inline def LdcuRegRegConstDefinition: Definition = InstructionDefinitions2.LdcuRegRegConst
  inline def LdcuRegRegConstImmDefinition: Definition = InstructionDefinitions2.LdcuRegRegConstImm
  inline def LdgPredRegAddressDefinition: Definition = InstructionDefinitions2.LdgPredRegAddress
  inline def LdgPredRegAddressPredDefinition: Definition = InstructionDefinitions2.LdgPredRegAddressPred
  inline def LdgRegAddressDefinition: Definition = InstructionDefinitions2.LdgRegAddress
  inline def LdgRegAddressPredDefinition: Definition = InstructionDefinitions2.LdgRegAddressPred
  inline def LdgRegRegAddressDefinition: Definition = InstructionDefinitions2.LdgRegRegAddress
  inline def LdgRegRegAddressImmDefinition: Definition = InstructionDefinitions2.LdgRegRegAddressImm
  inline def LdgRegRegAddressImmPredDefinition: Definition = InstructionDefinitions2.LdgRegRegAddressImmPred
  inline def LdgRegRegAddressPredDefinition: Definition = InstructionDefinitions2.LdgRegRegAddressPred
  inline def LdgdepbarNoOperandsDefinition: Definition = InstructionDefinitions2.LdgdepbarNoOperands
  inline def LdgmcRegAddressDefinition: Definition = InstructionDefinitions2.LdgmcRegAddress
  inline def LdgstsAddressAddressDefinition: Definition = InstructionDefinitions2.LdgstsAddressAddress
  inline def LdgstsAddressAddressPredDefinition: Definition = InstructionDefinitions2.LdgstsAddressAddressPred
  inline def LdlRegAddressDefinition: Definition = InstructionDefinitions2.LdlRegAddress
  inline def LdsPredRegAddressDefinition: Definition = InstructionDefinitions2.LdsPredRegAddress
  inline def LdsRegAddressDefinition: Definition = InstructionDefinitions2.LdsRegAddress
  inline def LdslkPredRegAddressDefinition: Definition = InstructionDefinitions2.LdslkPredRegAddress
  inline def LdsmRegAddressDefinition: Definition = InstructionDefinitions2.LdsmRegAddress
  inline def LdtRegAddressDefinition: Definition = InstructionDefinitions2.LdtRegAddress
  inline def LdtmRegAddressDefinition: Definition = InstructionDefinitions2.LdtmRegAddress
  inline def LdtmRegRegAddressDefinition: Definition = InstructionDefinitions2.LdtmRegRegAddress
  inline def LdtramRegAddressDefinition: Definition = InstructionDefinitions2.LdtramRegAddress
  inline def LeaPredRegRegConstDefinition: Definition = InstructionDefinitions2.LeaPredRegRegConst
  inline def LeaPredRegRegConstImmDefinition: Definition = InstructionDefinitions2.LeaPredRegRegConstImm
  inline def LeaPredRegRegConstRegDefinition: Definition = InstructionDefinitions2.LeaPredRegRegConstReg
  inline def LeaPredRegRegConstRegImmDefinition: Definition = InstructionDefinitions2.LeaPredRegRegConstRegImm
  inline def LeaPredRegRegImmDefinition: Definition = InstructionDefinitions2.LeaPredRegRegImm
  inline def LeaPredRegRegImmImmDefinition: Definition = InstructionDefinitions2.LeaPredRegRegImmImm
  inline def LeaPredRegRegRegDefinition: Definition = InstructionDefinitions2.LeaPredRegRegReg
  inline def LeaPredRegRegRegImmDefinition: Definition = InstructionDefinitions2.LeaPredRegRegRegImm
  inline def LeaPredRegRegRegRegDefinition: Definition = InstructionDefinitions2.LeaPredRegRegRegReg
  inline def LeaPredRegRegRegRegImmDefinition: Definition = InstructionDefinitions2.LeaPredRegRegRegRegImm
  inline def LeaRegPredRegAddressImmImmDefinition: Definition = InstructionDefinitions2.LeaRegPredRegAddressImmImm
  inline def LeaRegPredRegAddressImmImmPredDefinition: Definition =
    InstructionDefinitions2.LeaRegPredRegAddressImmImmPred
  inline def LeaRegPredRegAddressImmRegImmDefinition: Definition = InstructionDefinitions2.LeaRegPredRegAddressImmRegImm
  inline def LeaRegPredRegAddressImmRegImmPredDefinition: Definition =
    InstructionDefinitions2.LeaRegPredRegAddressImmRegImmPred
  inline def LeaRegPredRegConstImmDefinition: Definition = InstructionDefinitions2.LeaRegPredRegConstImm
  inline def LeaRegPredRegConstImmPredDefinition: Definition = InstructionDefinitions2.LeaRegPredRegConstImmPred
  inline def LeaRegPredRegConstRegImmDefinition: Definition = InstructionDefinitions2.LeaRegPredRegConstRegImm
  inline def LeaRegPredRegConstRegImmPredDefinition: Definition = InstructionDefinitions2.LeaRegPredRegConstRegImmPred
  inline def LeaRegPredRegImmImmDefinition: Definition = InstructionDefinitions2.LeaRegPredRegImmImm
  inline def LeaRegPredRegImmImmPredDefinition: Definition = InstructionDefinitions2.LeaRegPredRegImmImmPred
  inline def LeaRegPredRegImmRegImmDefinition: Definition = InstructionDefinitions2.LeaRegPredRegImmRegImm
  inline def LeaRegPredRegImmRegImmPredDefinition: Definition = InstructionDefinitions2.LeaRegPredRegImmRegImmPred
  inline def LeaRegPredRegRegImmDefinition: Definition = InstructionDefinitions2.LeaRegPredRegRegImm
  inline def LeaRegPredRegRegImmImmDefinition: Definition = InstructionDefinitions2.LeaRegPredRegRegImmImm
  inline def LeaRegPredRegRegImmImmPredDefinition: Definition = InstructionDefinitions2.LeaRegPredRegRegImmImmPred
  inline def LeaRegPredRegRegImmPredDefinition: Definition = InstructionDefinitions2.LeaRegPredRegRegImmPred
  inline def LeaRegPredRegRegRegImmDefinition: Definition = InstructionDefinitions2.LeaRegPredRegRegRegImm
  inline def LeaRegPredRegRegRegImmPredDefinition: Definition = InstructionDefinitions2.LeaRegPredRegRegRegImmPred
  inline def LeaRegRegAddressImmImmDefinition: Definition = InstructionDefinitions2.LeaRegRegAddressImmImm
  inline def LeaRegRegAddressImmImmPredDefinition: Definition = InstructionDefinitions2.LeaRegRegAddressImmImmPred
  inline def LeaRegRegAddressImmRegImmDefinition: Definition = InstructionDefinitions2.LeaRegRegAddressImmRegImm
  inline def LeaRegRegAddressImmRegImmPredDefinition: Definition = InstructionDefinitions2.LeaRegRegAddressImmRegImmPred
  inline def LeaRegRegConstDefinition: Definition = InstructionDefinitions2.LeaRegRegConst
  inline def LeaRegRegConstImmDefinition: Definition = InstructionDefinitions2.LeaRegRegConstImm
  inline def LeaRegRegConstImmPredDefinition: Definition = InstructionDefinitions2.LeaRegRegConstImmPred
  inline def LeaRegRegConstRegDefinition: Definition = InstructionDefinitions2.LeaRegRegConstReg
  inline def LeaRegRegConstRegImmDefinition: Definition = InstructionDefinitions2.LeaRegRegConstRegImm
  inline def LeaRegRegConstRegImmPredDefinition: Definition = InstructionDefinitions2.LeaRegRegConstRegImmPred
  inline def LeaRegRegImmDefinition: Definition = InstructionDefinitions2.LeaRegRegImm
  inline def LeaRegRegImmImmDefinition: Definition = InstructionDefinitions2.LeaRegRegImmImm
  inline def LeaRegRegImmImmPredDefinition: Definition = InstructionDefinitions2.LeaRegRegImmImmPred
  inline def LeaRegRegImmRegImmDefinition: Definition = InstructionDefinitions2.LeaRegRegImmRegImm
  inline def LeaRegRegImmRegImmPredDefinition: Definition = InstructionDefinitions2.LeaRegRegImmRegImmPred
  inline def LeaRegRegRegDefinition: Definition = InstructionDefinitions2.LeaRegRegReg
  inline def LeaRegRegRegImmDefinition: Definition = InstructionDefinitions3.LeaRegRegRegImm
  inline def LeaRegRegRegImmImmDefinition: Definition = InstructionDefinitions3.LeaRegRegRegImmImm
  inline def LeaRegRegRegImmImmPredDefinition: Definition = InstructionDefinitions3.LeaRegRegRegImmImmPred
  inline def LeaRegRegRegImmPredDefinition: Definition = InstructionDefinitions3.LeaRegRegRegImmPred
  inline def LeaRegRegRegRegDefinition: Definition = InstructionDefinitions3.LeaRegRegRegReg
  inline def LeaRegRegRegRegImmDefinition: Definition = InstructionDefinitions3.LeaRegRegRegRegImm
  inline def LeaRegRegRegRegImmPredDefinition: Definition = InstructionDefinitions3.LeaRegRegRegRegImmPred
  inline def LepcRegDefinition: Definition = InstructionDefinitions3.LepcReg
  inline def LepcRegImmDefinition: Definition = InstructionDefinitions3.LepcRegImm
  inline def LongjmpNoOperandsDefinition: Definition = InstructionDefinitions3.LongjmpNoOperands
  inline def LopPredRegRegAddressImmDefinition: Definition = InstructionDefinitions3.LopPredRegRegAddressImm
  inline def LopPredRegRegAddressImmPredDefinition: Definition = InstructionDefinitions3.LopPredRegRegAddressImmPred
  inline def LopPredRegRegConstDefinition: Definition = InstructionDefinitions3.LopPredRegRegConst
  inline def LopPredRegRegConstPredDefinition: Definition = InstructionDefinitions3.LopPredRegRegConstPred
  inline def LopPredRegRegImmDefinition: Definition = InstructionDefinitions3.LopPredRegRegImm
  inline def LopPredRegRegImmPredDefinition: Definition = InstructionDefinitions3.LopPredRegRegImmPred
  inline def LopPredRegRegRegDefinition: Definition = InstructionDefinitions3.LopPredRegRegReg
  inline def LopPredRegRegRegPredDefinition: Definition = InstructionDefinitions3.LopPredRegRegRegPred
  inline def LopRegRegAddressImmDefinition: Definition = InstructionDefinitions3.LopRegRegAddressImm
  inline def LopRegRegAddressImmPredDefinition: Definition = InstructionDefinitions3.LopRegRegAddressImmPred
  inline def LopRegRegConstDefinition: Definition = InstructionDefinitions3.LopRegRegConst
  inline def LopRegRegConstPredDefinition: Definition = InstructionDefinitions3.LopRegRegConstPred
  inline def LopRegRegImmDefinition: Definition = InstructionDefinitions3.LopRegRegImm
  inline def LopRegRegImmPredDefinition: Definition = InstructionDefinitions3.LopRegRegImmPred
  inline def LopRegRegRegDefinition: Definition = InstructionDefinitions3.LopRegRegReg
  inline def LopRegRegRegPredDefinition: Definition = InstructionDefinitions3.LopRegRegRegPred
  inline def Lop3PredRegRegAddressImmRegDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegAddressImmReg
  inline def Lop3PredRegRegAddressImmRegImmDefinition: Definition =
    InstructionDefinitions3.Lop3PredRegRegAddressImmRegImm
  inline def Lop3PredRegRegAddressImmRegImmPredDefinition: Definition =
    InstructionDefinitions3.Lop3PredRegRegAddressImmRegImmPred
  inline def Lop3PredRegRegAddressImmRegPredDefinition: Definition =
    InstructionDefinitions3.Lop3PredRegRegAddressImmRegPred
  inline def Lop3PredRegRegConstRegDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegConstReg
  inline def Lop3PredRegRegConstRegImmDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegConstRegImm
  inline def Lop3PredRegRegConstRegImmPredDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegConstRegImmPred
  inline def Lop3PredRegRegConstRegPredDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegConstRegPred
  inline def Lop3PredRegRegImmRegDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegImmReg
  inline def Lop3PredRegRegImmRegImmDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegImmRegImm
  inline def Lop3PredRegRegImmRegImmPredDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegImmRegImmPred
  inline def Lop3PredRegRegImmRegPredDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegImmRegPred
  inline def Lop3PredRegRegRegRegDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegRegReg
  inline def Lop3PredRegRegRegRegImmDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegRegRegImm
  inline def Lop3PredRegRegRegRegImmPredDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegRegRegImmPred
  inline def Lop3PredRegRegRegRegPredDefinition: Definition = InstructionDefinitions3.Lop3PredRegRegRegRegPred
  inline def Lop3RegRegAddressImmRegDefinition: Definition = InstructionDefinitions3.Lop3RegRegAddressImmReg
  inline def Lop3RegRegAddressImmRegImmDefinition: Definition = InstructionDefinitions3.Lop3RegRegAddressImmRegImm
  inline def Lop3RegRegAddressImmRegImmPredDefinition: Definition =
    InstructionDefinitions3.Lop3RegRegAddressImmRegImmPred
  inline def Lop3RegRegAddressImmRegPredDefinition: Definition = InstructionDefinitions3.Lop3RegRegAddressImmRegPred
  inline def Lop3RegRegConstRegDefinition: Definition = InstructionDefinitions3.Lop3RegRegConstReg
  inline def Lop3RegRegConstRegImmDefinition: Definition = InstructionDefinitions3.Lop3RegRegConstRegImm
  inline def Lop3RegRegConstRegImmPredDefinition: Definition = InstructionDefinitions3.Lop3RegRegConstRegImmPred
  inline def Lop3RegRegConstRegPredDefinition: Definition = InstructionDefinitions3.Lop3RegRegConstRegPred
  inline def Lop3RegRegImmRegDefinition: Definition = InstructionDefinitions3.Lop3RegRegImmReg
  inline def Lop3RegRegImmRegImmDefinition: Definition = InstructionDefinitions3.Lop3RegRegImmRegImm
  inline def Lop3RegRegImmRegImmPredDefinition: Definition = InstructionDefinitions3.Lop3RegRegImmRegImmPred
  inline def Lop3RegRegImmRegPredDefinition: Definition = InstructionDefinitions3.Lop3RegRegImmRegPred
  inline def Lop3RegRegRegRegDefinition: Definition = InstructionDefinitions3.Lop3RegRegRegReg
  inline def Lop3RegRegRegRegImmDefinition: Definition = InstructionDefinitions3.Lop3RegRegRegRegImm
  inline def Lop3RegRegRegRegImmPredDefinition: Definition = InstructionDefinitions3.Lop3RegRegRegRegImmPred
  inline def Lop3RegRegRegRegPredDefinition: Definition = InstructionDefinitions3.Lop3RegRegRegRegPred
  inline def Lop32iPredRegRegImmDefinition: Definition = InstructionDefinitions3.Lop32iPredRegRegImm
  inline def Lop32iPredRegRegImmPredDefinition: Definition = InstructionDefinitions3.Lop32iPredRegRegImmPred
  inline def Lop32iRegRegImmDefinition: Definition = InstructionDefinitions3.Lop32iRegRegImm
  inline def Lop32iRegRegImmPredDefinition: Definition = InstructionDefinitions3.Lop32iRegRegImmPred
  inline def MatchPredRegRegDefinition: Definition = InstructionDefinitions3.MatchPredRegReg
  inline def MatchRegRegDefinition: Definition = InstructionDefinitions3.MatchRegReg
  inline def MembarNoOperandsDefinition: Definition = InstructionDefinitions3.MembarNoOperands
  inline def MovAddressConstDefinition: Definition = InstructionDefinitions3.MovAddressConst
  inline def MovAddressConstImmDefinition: Definition = InstructionDefinitions3.MovAddressConstImm
  inline def MovAddressImmDefinition: Definition = InstructionDefinitions3.MovAddressImm
  inline def MovAddressImmImmDefinition: Definition = InstructionDefinitions3.MovAddressImmImm
  inline def MovAddressRegDefinition: Definition = InstructionDefinitions3.MovAddressReg
  inline def MovAddressRegImmDefinition: Definition = InstructionDefinitions3.MovAddressRegImm
  inline def MovRegAddressDefinition: Definition = InstructionDefinitions3.MovRegAddress
  inline def MovRegAddressImmDefinition: Definition = InstructionDefinitions3.MovRegAddressImm
  inline def MovRegAddressImmImmDefinition: Definition = InstructionDefinitions3.MovRegAddressImmImm
  inline def MovRegConstDefinition: Definition = InstructionDefinitions3.MovRegConst
  inline def MovRegConstImmDefinition: Definition = InstructionDefinitions3.MovRegConstImm
  inline def MovRegImmDefinition: Definition = InstructionDefinitions3.MovRegImm
  inline def MovRegImmImmDefinition: Definition = InstructionDefinitions3.MovRegImmImm
  inline def MovRegRegDefinition: Definition = InstructionDefinitions3.MovRegReg
  inline def MovRegRegImmDefinition: Definition = InstructionDefinitions3.MovRegRegImm
  inline def Mov32iRegImmDefinition: Definition = InstructionDefinitions3.Mov32iRegImm
  inline def Mov32iRegImmImmDefinition: Definition = InstructionDefinitions3.Mov32iRegImmImm
  inline def Mov64iurRegImmDefinition: Definition = InstructionDefinitions3.Mov64iurRegImm
  inline def Mov64iurRegRegDefinition: Definition = InstructionDefinitions3.Mov64iurRegReg
  inline def MovmRegRegDefinition: Definition = InstructionDefinitions3.MovmRegReg
  inline def MufuRegAddressImmDefinition: Definition = InstructionDefinitions3.MufuRegAddressImm
  inline def MufuRegConstDefinition: Definition = InstructionDefinitions3.MufuRegConst
  inline def MufuRegImmDefinition: Definition = InstructionDefinitions3.MufuRegImm
  inline def MufuRegImmImmDefinition: Definition = InstructionDefinitions3.MufuRegImmImm
  inline def MufuRegRegDefinition: Definition = InstructionDefinitions3.MufuRegReg
  inline def NanosleepNoOperandsDefinition: Definition = InstructionDefinitions3.NanosleepNoOperands
  inline def NanosleepAddressImmDefinition: Definition = InstructionDefinitions3.NanosleepAddressImm
  inline def NanosleepConstDefinition: Definition = InstructionDefinitions3.NanosleepConst
  inline def NanosleepImmDefinition: Definition = InstructionDefinitions3.NanosleepImm
  inline def NanosleepPredDefinition: Definition = InstructionDefinitions3.NanosleepPred
  inline def NanosleepPredAddressImmDefinition: Definition = InstructionDefinitions3.NanosleepPredAddressImm
  inline def NanosleepPredConstDefinition: Definition = InstructionDefinitions3.NanosleepPredConst
  inline def NanosleepPredImmDefinition: Definition = InstructionDefinitions3.NanosleepPredImm
  inline def NanosleepPredRegDefinition: Definition = InstructionDefinitions3.NanosleepPredReg
  inline def NanosleepRegDefinition: Definition = InstructionDefinitions3.NanosleepReg
  inline def NanotrapConstDefinition: Definition = InstructionDefinitions3.NanotrapConst
  inline def NanotrapImmDefinition: Definition = InstructionDefinitions3.NanotrapImm
  inline def NanotrapPredConstDefinition: Definition = InstructionDefinitions3.NanotrapPredConst
  inline def NanotrapPredImmDefinition: Definition = InstructionDefinitions3.NanotrapPredImm
  inline def NanotrapPredRegDefinition: Definition = InstructionDefinitions3.NanotrapPredReg
  inline def NanotrapRegDefinition: Definition = InstructionDefinitions3.NanotrapReg
  inline def NopNoOperandsDefinition: Definition = InstructionDefinitions3.NopNoOperands
  inline def NopImmDefinition: Definition = InstructionDefinitions3.NopImm
  inline def NopSymbolDefinition: Definition = InstructionDefinitions3.NopSymbol
  inline def NopSymbolImmDefinition: Definition = InstructionDefinitions3.NopSymbolImm
  inline def OmmaRegRegRegRegPredRegRegRegDefinition: Definition = InstructionDefinitions3.OmmaRegRegRegRegPredRegRegReg
  inline def OmmaRegRegRegRegPredRegRegRegImmDefinition: Definition =
    InstructionDefinitions3.OmmaRegRegRegRegPredRegRegRegImm
  inline def OmmaRegRegRegRegRegRegRegDefinition: Definition = InstructionDefinitions3.OmmaRegRegRegRegRegRegReg
  inline def OmmaRegRegRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions3.OmmaRegRegRegRegRegRegRegImm
  inline def OutRegDefinition: Definition = InstructionDefinitions3.OutReg
  inline def OutRegRegConstDefinition: Definition = InstructionDefinitions3.OutRegRegConst
  inline def OutRegRegImmDefinition: Definition = InstructionDefinitions3.OutRegRegImm
  inline def OutRegRegRegDefinition: Definition = InstructionDefinitions3.OutRegRegReg
  inline def P2rRegDefinition: Definition = InstructionDefinitions3.P2rReg
  inline def P2rRegRegConstDefinition: Definition = InstructionDefinitions3.P2rRegRegConst
  inline def P2rRegRegImmDefinition: Definition = InstructionDefinitions3.P2rRegRegImm
  inline def P2rRegRegRegDefinition: Definition = InstructionDefinitions3.P2rRegRegReg
  inline def P2rRegSymbolDefinition: Definition = InstructionDefinitions3.P2rRegSymbol
  inline def P2rRegSymbolRegAddressImmDefinition: Definition = InstructionDefinitions3.P2rRegSymbolRegAddressImm
  inline def P2rRegSymbolRegConstDefinition: Definition = InstructionDefinitions3.P2rRegSymbolRegConst
  inline def P2rRegSymbolRegImmDefinition: Definition = InstructionDefinitions3.P2rRegSymbolRegImm
  inline def P2rRegSymbolRegRegDefinition: Definition = InstructionDefinitions3.P2rRegSymbolRegReg
  inline def PbkConstDefinition: Definition = InstructionDefinitions3.PbkConst
  inline def PbkImmDefinition: Definition = InstructionDefinitions3.PbkImm
  inline def PcntConstDefinition: Definition = InstructionDefinitions3.PcntConst
  inline def PcntImmDefinition: Definition = InstructionDefinitions3.PcntImm
  inline def PexitImmDefinition: Definition = InstructionDefinitions3.PexitImm
  inline def PixldRegDefinition: Definition = InstructionDefinitions3.PixldReg
  inline def PixldRegAddressDefinition: Definition = InstructionDefinitions3.PixldRegAddress
  inline def PixldRegPredDefinition: Definition = InstructionDefinitions3.PixldRegPred
  inline def PixldRegPredAddressDefinition: Definition = InstructionDefinitions3.PixldRegPredAddress
  inline def PlongjmpConstDefinition: Definition = InstructionDefinitions3.PlongjmpConst
  inline def PlongjmpImmDefinition: Definition = InstructionDefinitions3.PlongjmpImm
  inline def Plop3PredPredAddressImmPredImmDefinition: Definition =
    InstructionDefinitions3.Plop3PredPredAddressImmPredImm
  inline def Plop3PredPredAddressImmRegImmDefinition: Definition = InstructionDefinitions3.Plop3PredPredAddressImmRegImm
  inline def Plop3PredPredConstPredImmDefinition: Definition = InstructionDefinitions3.Plop3PredPredConstPredImm
  inline def Plop3PredPredConstRegImmDefinition: Definition = InstructionDefinitions3.Plop3PredPredConstRegImm
  inline def Plop3PredPredPredAddressImmPredImmImmDefinition: Definition =
    InstructionDefinitions3.Plop3PredPredPredAddressImmPredImmImm
  inline def Plop3PredPredPredAddressImmRegImmImmDefinition: Definition =
    InstructionDefinitions3.Plop3PredPredPredAddressImmRegImmImm
  inline def Plop3PredPredPredConstPredImmImmDefinition: Definition =
    InstructionDefinitions3.Plop3PredPredPredConstPredImmImm
  inline def Plop3PredPredPredConstRegImmImmDefinition: Definition =
    InstructionDefinitions3.Plop3PredPredPredConstRegImmImm
  inline def Plop3PredPredPredPredDefinition: Definition = InstructionDefinitions3.Plop3PredPredPredPred
  inline def Plop3PredPredPredPredImmDefinition: Definition = InstructionDefinitions3.Plop3PredPredPredPredImm
  inline def Plop3PredPredPredPredPredImmImmDefinition: Definition =
    InstructionDefinitions3.Plop3PredPredPredPredPredImmImm
  inline def Plop3PredPredPredRegPredImmImmDefinition: Definition =
    InstructionDefinitions3.Plop3PredPredPredRegPredImmImm
  inline def Plop3PredPredPredRegRegImmImmDefinition: Definition = InstructionDefinitions3.Plop3PredPredPredRegRegImmImm
  inline def Plop3PredPredRegAddressImmRegImmImmDefinition: Definition =
    InstructionDefinitions3.Plop3PredPredRegAddressImmRegImmImm
  inline def Plop3PredPredRegConstRegImmImmDefinition: Definition =
    InstructionDefinitions3.Plop3PredPredRegConstRegImmImm
  inline def Plop3PredPredRegPredImmDefinition: Definition = InstructionDefinitions3.Plop3PredPredRegPredImm
  inline def Plop3PredPredRegRegImmDefinition: Definition = InstructionDefinitions3.Plop3PredPredRegRegImm
  inline def Plop3PredPredRegRegRegImmImmDefinition: Definition = InstructionDefinitions3.Plop3PredPredRegRegRegImmImm
  inline def Plop3PredRegAddressImmRegImmDefinition: Definition = InstructionDefinitions3.Plop3PredRegAddressImmRegImm
  inline def Plop3PredRegConstRegImmDefinition: Definition = InstructionDefinitions3.Plop3PredRegConstRegImm
  inline def Plop3PredRegRegRegImmDefinition: Definition = InstructionDefinitions3.Plop3PredRegRegRegImm
  inline def PmtrigImmDefinition: Definition = InstructionDefinitions3.PmtrigImm
  inline def PmtrigPredImmDefinition: Definition = InstructionDefinitions3.PmtrigPredImm
  inline def PopcRegAddressImmDefinition: Definition = InstructionDefinitions3.PopcRegAddressImm
  inline def PopcRegConstDefinition: Definition = InstructionDefinitions3.PopcRegConst
  inline def PopcRegImmDefinition: Definition = InstructionDefinitions3.PopcRegImm
  inline def PopcRegRegDefinition: Definition = InstructionDefinitions3.PopcRegReg
  inline def PopcRegRegConstDefinition: Definition = InstructionDefinitions3.PopcRegRegConst
  inline def PopcRegRegImmDefinition: Definition = InstructionDefinitions3.PopcRegRegImm
  inline def PopcRegRegRegDefinition: Definition = InstructionDefinitions3.PopcRegRegReg
  inline def PreexitNoOperandsDefinition: Definition = InstructionDefinitions3.PreexitNoOperands
  inline def PretConstDefinition: Definition = InstructionDefinitions3.PretConst
  inline def PretImmDefinition: Definition = InstructionDefinitions3.PretImm
  inline def PrmtRegRegAddressImmRegDefinition: Definition = InstructionDefinitions3.PrmtRegRegAddressImmReg
  inline def PrmtRegRegConstRegDefinition: Definition = InstructionDefinitions3.PrmtRegRegConstReg
  inline def PrmtRegRegImmRegDefinition: Definition = InstructionDefinitions3.PrmtRegRegImmReg
  inline def PrmtRegRegRegAddressImmDefinition: Definition = InstructionDefinitions3.PrmtRegRegRegAddressImm
  inline def PrmtRegRegRegConstDefinition: Definition = InstructionDefinitions3.PrmtRegRegRegConst
  inline def PrmtRegRegRegImmDefinition: Definition = InstructionDefinitions3.PrmtRegRegRegImm
  inline def PrmtRegRegRegRegDefinition: Definition = InstructionDefinitions3.PrmtRegRegRegReg
  inline def PsetRegPredPredDefinition: Definition = InstructionDefinitions3.PsetRegPredPred
  inline def PsetRegPredPredPredDefinition: Definition = InstructionDefinitions3.PsetRegPredPredPred
  inline def PsetpPredPredPredDefinition: Definition = InstructionDefinitions3.PsetpPredPredPred
  inline def PsetpPredPredPredPredPredDefinition: Definition = InstructionDefinitions3.PsetpPredPredPredPredPred
  inline def Qadd4RegRegImmDefinition: Definition = InstructionDefinitions3.Qadd4RegRegImm
  inline def Qadd4RegRegRegDefinition: Definition = InstructionDefinitions3.Qadd4RegRegReg
  inline def Qfma4RegRegImmRegDefinition: Definition = InstructionDefinitions3.Qfma4RegRegImmReg
  inline def Qfma4RegRegRegImmDefinition: Definition = InstructionDefinitions3.Qfma4RegRegRegImm
  inline def Qfma4RegRegRegRegDefinition: Definition = InstructionDefinitions3.Qfma4RegRegRegReg
  inline def QgmmaRegAddressRegDefinition: Definition = InstructionDefinitions3.QgmmaRegAddressReg
  inline def QgmmaRegAddressRegPredDefinition: Definition = InstructionDefinitions3.QgmmaRegAddressRegPred
  inline def QgmmaRegAddressRegPredRegDefinition: Definition = InstructionDefinitions3.QgmmaRegAddressRegPredReg
  inline def QgmmaRegAddressRegPredRegImmDefinition: Definition = InstructionDefinitions3.QgmmaRegAddressRegPredRegImm
  inline def QgmmaRegAddressRegRegDefinition: Definition = InstructionDefinitions3.QgmmaRegAddressRegReg
  inline def QgmmaRegAddressRegRegImmDefinition: Definition = InstructionDefinitions3.QgmmaRegAddressRegRegImm
  inline def QgmmaRegAddressRegRegPredDefinition: Definition = InstructionDefinitions3.QgmmaRegAddressRegRegPred
  inline def QgmmaRegAddressRegRegPredRegDefinition: Definition = InstructionDefinitions3.QgmmaRegAddressRegRegPredReg
  inline def QgmmaRegAddressRegRegPredRegImmDefinition: Definition =
    InstructionDefinitions3.QgmmaRegAddressRegRegPredRegImm
  inline def QgmmaRegAddressRegRegRegDefinition: Definition = InstructionDefinitions3.QgmmaRegAddressRegRegReg
  inline def QgmmaRegAddressRegRegRegImmDefinition: Definition = InstructionDefinitions3.QgmmaRegAddressRegRegRegImm
  inline def QgmmaRegRegAddressRegDefinition: Definition = InstructionDefinitions3.QgmmaRegRegAddressReg
  inline def QgmmaRegRegAddressRegPredDefinition: Definition = InstructionDefinitions3.QgmmaRegRegAddressRegPred
  inline def QgmmaRegRegAddressRegPredRegDefinition: Definition = InstructionDefinitions3.QgmmaRegRegAddressRegPredReg
  inline def QgmmaRegRegAddressRegPredRegImmDefinition: Definition =
    InstructionDefinitions3.QgmmaRegRegAddressRegPredRegImm
  inline def QgmmaRegRegAddressRegRegDefinition: Definition = InstructionDefinitions3.QgmmaRegRegAddressRegReg
  inline def QgmmaRegRegAddressRegRegImmDefinition: Definition = InstructionDefinitions3.QgmmaRegRegAddressRegRegImm
  inline def QmmaRegRegRegRegDefinition: Definition = InstructionDefinitions3.QmmaRegRegRegReg
  inline def QmmaRegRegRegRegPredDefinition: Definition = InstructionDefinitions3.QmmaRegRegRegRegPred
  inline def QmmaRegRegRegRegPredRegImmDefinition: Definition = InstructionDefinitions3.QmmaRegRegRegRegPredRegImm
  inline def QmmaRegRegRegRegPredRegRegRegDefinition: Definition = InstructionDefinitions3.QmmaRegRegRegRegPredRegRegReg
  inline def QmmaRegRegRegRegPredRegRegRegImmDefinition: Definition =
    InstructionDefinitions3.QmmaRegRegRegRegPredRegRegRegImm
  inline def QmmaRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions3.QmmaRegRegRegRegRegImm
  inline def QmmaRegRegRegRegRegRegRegDefinition: Definition = InstructionDefinitions3.QmmaRegRegRegRegRegRegReg
  inline def QmmaRegRegRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions3.QmmaRegRegRegRegRegRegRegImm
  inline def Qmul4RegRegImmDefinition: Definition = InstructionDefinitions3.Qmul4RegRegImm
  inline def Qmul4RegRegRegDefinition: Definition = InstructionDefinitions3.Qmul4RegRegReg
  inline def QspcPredAddressDefinition: Definition = InstructionDefinitions3.QspcPredAddress
  inline def QspcPredRegAddressDefinition: Definition = InstructionDefinitions3.QspcPredRegAddress
  inline def QspcRegAddressDefinition: Definition = InstructionDefinitions3.QspcRegAddress
  inline def R2bImmRegDefinition: Definition = InstructionDefinitions3.R2bImmReg
  inline def R2pRegDefinition: Definition = InstructionDefinitions3.R2pReg
  inline def R2pRegConstDefinition: Definition = InstructionDefinitions3.R2pRegConst
  inline def R2pRegImmDefinition: Definition = InstructionDefinitions3.R2pRegImm
  inline def R2pRegRegDefinition: Definition = InstructionDefinitions3.R2pRegReg
  inline def R2pSymbolRegDefinition: Definition = InstructionDefinitions3.R2pSymbolReg
  inline def R2pSymbolRegAddressImmDefinition: Definition = InstructionDefinitions3.R2pSymbolRegAddressImm
  inline def R2pSymbolRegConstDefinition: Definition = InstructionDefinitions3.R2pSymbolRegConst
  inline def R2pSymbolRegImmDefinition: Definition = InstructionDefinitions3.R2pSymbolRegImm
  inline def R2pSymbolRegRegDefinition: Definition = InstructionDefinitions3.R2pSymbolRegReg
  inline def R2urPredRegRegDefinition: Definition = InstructionDefinitions3.R2urPredRegReg
  inline def R2urRegRegDefinition: Definition = InstructionDefinitions3.R2urRegReg
  inline def RamNoOperandsDefinition: Definition = InstructionDefinitions3.RamNoOperands
  inline def RedAddressRegDefinition: Definition = InstructionDefinitions3.RedAddressReg
  inline def RedasAddressRegDefinition: Definition = InstructionDefinitions3.RedasAddressReg
  inline def RedgAddressRegDefinition: Definition = InstructionDefinitions3.RedgAddressReg
  inline def RedsAddressRegDefinition: Definition = InstructionDefinitions3.RedsAddressReg
  inline def ReduxRegRegDefinition: Definition = InstructionDefinitions3.ReduxRegReg
  inline def RetNoOperandsDefinition: Definition = InstructionDefinitions3.RetNoOperands
  inline def RetPredRegDefinition: Definition = InstructionDefinitions3.RetPredReg
  inline def RetPredRegImmDefinition: Definition = InstructionDefinitions3.RetPredRegImm
  inline def RetRegDefinition: Definition = InstructionDefinitions3.RetReg
  inline def RetRegImmDefinition: Definition = InstructionDefinitions3.RetRegImm
  inline def RpcmovAddressImmDefinition: Definition = InstructionDefinitions3.RpcmovAddressImm
  inline def RpcmovConstDefinition: Definition = InstructionDefinitions3.RpcmovConst
  inline def RpcmovImmDefinition: Definition = InstructionDefinitions3.RpcmovImm
  inline def RpcmovRegDefinition: Definition = InstructionDefinitions3.RpcmovReg
  inline def RroRegConstDefinition: Definition = InstructionDefinitions3.RroRegConst
  inline def RroRegImmDefinition: Definition = InstructionDefinitions3.RroRegImm
  inline def RroRegRegDefinition: Definition = InstructionDefinitions3.RroRegReg
  inline def RttNoOperandsDefinition: Definition = InstructionDefinitions3.RttNoOperands
  inline def S2rRegSpecialDefinition: Definition = InstructionDefinitions3.S2rRegSpecial
  inline def S2urRegSpecialDefinition: Definition = InstructionDefinitions3.S2urRegSpecial
  inline def SamNoOperandsDefinition: Definition = InstructionDefinitions3.SamNoOperands
  inline def ScatterRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions3.ScatterRegRegRegRegImmImm
  inline def SelRegRegAddressImmPredDefinition: Definition = InstructionDefinitions3.SelRegRegAddressImmPred
  inline def SelRegRegConstPredDefinition: Definition = InstructionDefinitions3.SelRegRegConstPred
  inline def SelRegRegImmPredDefinition: Definition = InstructionDefinitions3.SelRegRegImmPred
  inline def SelRegRegRegPredDefinition: Definition = InstructionDefinitions3.SelRegRegRegPred
  inline def SetcrsptrRegDefinition: Definition = InstructionDefinitions4.SetcrsptrReg
  inline def SetctaidRegDefinition: Definition = InstructionDefinitions4.SetctaidReg
  inline def SetlmembaseRegDefinition: Definition = InstructionDefinitions4.SetlmembaseReg
  inline def SgxtRegRegAddressImmDefinition: Definition = InstructionDefinitions4.SgxtRegRegAddressImm
  inline def SgxtRegRegConstDefinition: Definition = InstructionDefinitions4.SgxtRegRegConst
  inline def SgxtRegRegImmDefinition: Definition = InstructionDefinitions4.SgxtRegRegImm
  inline def SgxtRegRegRegDefinition: Definition = InstructionDefinitions4.SgxtRegRegReg
  inline def ShfRegRegAddressImmRegDefinition: Definition = InstructionDefinitions4.ShfRegRegAddressImmReg
  inline def ShfRegRegConstRegDefinition: Definition = InstructionDefinitions4.ShfRegRegConstReg
  inline def ShfRegRegImmRegDefinition: Definition = InstructionDefinitions4.ShfRegRegImmReg
  inline def ShfRegRegRegAddressImmDefinition: Definition = InstructionDefinitions4.ShfRegRegRegAddressImm
  inline def ShfRegRegRegConstDefinition: Definition = InstructionDefinitions4.ShfRegRegRegConst
  inline def ShfRegRegRegImmDefinition: Definition = InstructionDefinitions4.ShfRegRegRegImm
  inline def ShfRegRegRegRegDefinition: Definition = InstructionDefinitions4.ShfRegRegRegReg
  inline def ShflPredRegRegImmImmDefinition: Definition = InstructionDefinitions4.ShflPredRegRegImmImm
  inline def ShflPredRegRegImmRegDefinition: Definition = InstructionDefinitions4.ShflPredRegRegImmReg
  inline def ShflPredRegRegRegImmDefinition: Definition = InstructionDefinitions4.ShflPredRegRegRegImm
  inline def ShflPredRegRegRegRegDefinition: Definition = InstructionDefinitions4.ShflPredRegRegRegReg
  inline def ShintImmImmDefinition: Definition = InstructionDefinitions4.ShintImmImm
  inline def ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImmDefinition: Definition =
    InstructionDefinitions4.ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm
  inline def ShlRegImmRegDefinition: Definition = InstructionDefinitions4.ShlRegImmReg
  inline def ShlRegRegAddressImmDefinition: Definition = InstructionDefinitions4.ShlRegRegAddressImm
  inline def ShlRegRegConstDefinition: Definition = InstructionDefinitions4.ShlRegRegConst
  inline def ShlRegRegImmDefinition: Definition = InstructionDefinitions4.ShlRegRegImm
  inline def ShlRegRegRegDefinition: Definition = InstructionDefinitions4.ShlRegRegReg
  inline def ShrRegRegAddressImmDefinition: Definition = InstructionDefinitions4.ShrRegRegAddressImm
  inline def ShrRegRegConstDefinition: Definition = InstructionDefinitions4.ShrRegRegConst
  inline def ShrRegRegImmDefinition: Definition = InstructionDefinitions4.ShrRegRegImm
  inline def ShrRegRegRegDefinition: Definition = InstructionDefinitions4.ShrRegRegReg
  inline def SpmetadataRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.SpmetadataRegRegRegImmImmImm
  inline def SsyConstDefinition: Definition = InstructionDefinitions4.SsyConst
  inline def SsyImmDefinition: Definition = InstructionDefinitions4.SsyImm
  inline def StAddressRegDefinition: Definition = InstructionDefinitions4.StAddressReg
  inline def StAddressRegPredDefinition: Definition = InstructionDefinitions4.StAddressRegPred
  inline def StasAddressRegDefinition: Definition = InstructionDefinitions4.StasAddressReg
  inline def StgAddressRegDefinition: Definition = InstructionDefinitions4.StgAddressReg
  inline def StgAddressRegRegDefinition: Definition = InstructionDefinitions4.StgAddressRegReg
  inline def StgAddressRegRegImmDefinition: Definition = InstructionDefinitions4.StgAddressRegRegImm
  inline def StlAddressRegDefinition: Definition = InstructionDefinitions4.StlAddressReg
  inline def StpImmDefinition: Definition = InstructionDefinitions4.StpImm
  inline def StsAddressRegDefinition: Definition = InstructionDefinitions4.StsAddressReg
  inline def StsculPredAddressRegDefinition: Definition = InstructionDefinitions4.StsculPredAddressReg
  inline def StsmAddressRegDefinition: Definition = InstructionDefinitions4.StsmAddressReg
  inline def SttAddressRegDefinition: Definition = InstructionDefinitions4.SttAddressReg
  inline def SttmAddressRegDefinition: Definition = InstructionDefinitions4.SttmAddressReg
  inline def SuatomPredRegAddressRegImmDefinition: Definition = InstructionDefinitions4.SuatomPredRegAddressRegImm
  inline def SuatomPredRegAddressRegImmImmDefinition: Definition = InstructionDefinitions4.SuatomPredRegAddressRegImmImm
  inline def SuatomPredRegAddressRegImmImmImmDefinition: Definition =
    InstructionDefinitions4.SuatomPredRegAddressRegImmImmImm
  inline def SuatomPredRegAddressRegRegDefinition: Definition = InstructionDefinitions4.SuatomPredRegAddressRegReg
  inline def SuatomPredRegAddressRegRegImmDefinition: Definition = InstructionDefinitions4.SuatomPredRegAddressRegRegImm
  inline def SuatomPredRegAddressRegRegRegDefinition: Definition = InstructionDefinitions4.SuatomPredRegAddressRegRegReg
  inline def SuatomPredRegAddressRegRegRegImmDefinition: Definition =
    InstructionDefinitions4.SuatomPredRegAddressRegRegRegImm
  inline def SuatomPredRegAddressRegRegRegRegDefinition: Definition =
    InstructionDefinitions4.SuatomPredRegAddressRegRegRegReg
  inline def SuatomRegAddressRegImmDefinition: Definition = InstructionDefinitions4.SuatomRegAddressRegImm
  inline def SuatomRegAddressRegImmImmDefinition: Definition = InstructionDefinitions4.SuatomRegAddressRegImmImm
  inline def SuatomRegAddressRegImmImmImmDefinition: Definition = InstructionDefinitions4.SuatomRegAddressRegImmImmImm
  inline def SuatomRegAddressRegRegDefinition: Definition = InstructionDefinitions4.SuatomRegAddressRegReg
  inline def SuatomRegAddressRegRegImmDefinition: Definition = InstructionDefinitions4.SuatomRegAddressRegRegImm
  inline def SuatomRegAddressRegRegRegDefinition: Definition = InstructionDefinitions4.SuatomRegAddressRegRegReg
  inline def SuatomRegAddressRegRegRegImmDefinition: Definition = InstructionDefinitions4.SuatomRegAddressRegRegRegImm
  inline def SuatomRegAddressRegRegRegRegDefinition: Definition = InstructionDefinitions4.SuatomRegAddressRegRegRegReg
  inline def SubfmPredRegRegConstRegDefinition: Definition = InstructionDefinitions4.SubfmPredRegRegConstReg
  inline def SubfmPredRegRegImmRegDefinition: Definition = InstructionDefinitions4.SubfmPredRegRegImmReg
  inline def SubfmPredRegRegRegConstDefinition: Definition = InstructionDefinitions4.SubfmPredRegRegRegConst
  inline def SubfmPredRegRegRegRegDefinition: Definition = InstructionDefinitions4.SubfmPredRegRegRegReg
  inline def SuclampPredRegRegConstImmDefinition: Definition = InstructionDefinitions4.SuclampPredRegRegConstImm
  inline def SuclampPredRegRegImmImmDefinition: Definition = InstructionDefinitions4.SuclampPredRegRegImmImm
  inline def SuclampPredRegRegRegImmDefinition: Definition = InstructionDefinitions4.SuclampPredRegRegRegImm
  inline def SueauRegRegConstRegDefinition: Definition = InstructionDefinitions4.SueauRegRegConstReg
  inline def SueauRegRegImmRegDefinition: Definition = InstructionDefinitions4.SueauRegRegImmReg
  inline def SueauRegRegRegConstDefinition: Definition = InstructionDefinitions4.SueauRegRegRegConst
  inline def SueauRegRegRegRegDefinition: Definition = InstructionDefinitions4.SueauRegRegRegReg
  inline def SuldPredRegAddressImmDefinition: Definition = InstructionDefinitions4.SuldPredRegAddressImm
  inline def SuldPredRegAddressImmImmDefinition: Definition = InstructionDefinitions4.SuldPredRegAddressImmImm
  inline def SuldPredRegAddressImmImmImmDefinition: Definition = InstructionDefinitions4.SuldPredRegAddressImmImmImm
  inline def SuldPredRegAddressRegDefinition: Definition = InstructionDefinitions4.SuldPredRegAddressReg
  inline def SuldPredRegAddressRegImmDefinition: Definition = InstructionDefinitions4.SuldPredRegAddressRegImm
  inline def SuldPredRegAddressRegRegDefinition: Definition = InstructionDefinitions4.SuldPredRegAddressRegReg
  inline def SuldPredRegAddressRegRegImmDefinition: Definition = InstructionDefinitions4.SuldPredRegAddressRegRegImm
  inline def SuldPredRegAddressRegRegRegDefinition: Definition = InstructionDefinitions4.SuldPredRegAddressRegRegReg
  inline def SuldRegAddressImmDefinition: Definition = InstructionDefinitions4.SuldRegAddressImm
  inline def SuldRegAddressImmImmDefinition: Definition = InstructionDefinitions4.SuldRegAddressImmImm
  inline def SuldRegAddressImmImmImmDefinition: Definition = InstructionDefinitions4.SuldRegAddressImmImmImm
  inline def SuldRegAddressRegDefinition: Definition = InstructionDefinitions4.SuldRegAddressReg
  inline def SuldRegAddressRegImmDefinition: Definition = InstructionDefinitions4.SuldRegAddressRegImm
  inline def SuldRegAddressRegRegDefinition: Definition = InstructionDefinitions4.SuldRegAddressRegReg
  inline def SuldRegAddressRegRegImmDefinition: Definition = InstructionDefinitions4.SuldRegAddressRegRegImm
  inline def SuldRegAddressRegRegRegDefinition: Definition = InstructionDefinitions4.SuldRegAddressRegRegReg
  inline def SuldgaRegAddressConstPredDefinition: Definition = InstructionDefinitions4.SuldgaRegAddressConstPred
  inline def SuldgaRegAddressRegPredDefinition: Definition = InstructionDefinitions4.SuldgaRegAddressRegPred
  inline def SuqueryPredRegAddressImmImmDefinition: Definition = InstructionDefinitions4.SuqueryPredRegAddressImmImm
  inline def SuqueryPredRegAddressImmImmImmDefinition: Definition =
    InstructionDefinitions4.SuqueryPredRegAddressImmImmImm
  inline def SuqueryPredRegAddressRegDefinition: Definition = InstructionDefinitions4.SuqueryPredRegAddressReg
  inline def SuqueryPredRegAddressRegImmDefinition: Definition = InstructionDefinitions4.SuqueryPredRegAddressRegImm
  inline def SuqueryPredRegAddressRegRegDefinition: Definition = InstructionDefinitions4.SuqueryPredRegAddressRegReg
  inline def SuqueryPredRegAddressRegRegImmDefinition: Definition =
    InstructionDefinitions4.SuqueryPredRegAddressRegRegImm
  inline def SuqueryPredRegAddressRegRegRegDefinition: Definition =
    InstructionDefinitions4.SuqueryPredRegAddressRegRegReg
  inline def SuqueryRegAddressImmImmDefinition: Definition = InstructionDefinitions4.SuqueryRegAddressImmImm
  inline def SuqueryRegAddressImmImmImmDefinition: Definition = InstructionDefinitions4.SuqueryRegAddressImmImmImm
  inline def SuqueryRegAddressRegDefinition: Definition = InstructionDefinitions4.SuqueryRegAddressReg
  inline def SuqueryRegAddressRegImmDefinition: Definition = InstructionDefinitions4.SuqueryRegAddressRegImm
  inline def SuqueryRegAddressRegRegDefinition: Definition = InstructionDefinitions4.SuqueryRegAddressRegReg
  inline def SuqueryRegAddressRegRegImmDefinition: Definition = InstructionDefinitions4.SuqueryRegAddressRegRegImm
  inline def SuqueryRegAddressRegRegRegDefinition: Definition = InstructionDefinitions4.SuqueryRegAddressRegRegReg
  inline def SuredAddressRegImmDefinition: Definition = InstructionDefinitions4.SuredAddressRegImm
  inline def SuredAddressRegImmImmDefinition: Definition = InstructionDefinitions4.SuredAddressRegImmImm
  inline def SuredAddressRegImmImmImmDefinition: Definition = InstructionDefinitions4.SuredAddressRegImmImmImm
  inline def SuredAddressRegRegDefinition: Definition = InstructionDefinitions4.SuredAddressRegReg
  inline def SuredAddressRegRegImmDefinition: Definition = InstructionDefinitions4.SuredAddressRegRegImm
  inline def SuredAddressRegRegRegDefinition: Definition = InstructionDefinitions4.SuredAddressRegRegReg
  inline def SuredAddressRegRegRegImmDefinition: Definition = InstructionDefinitions4.SuredAddressRegRegRegImm
  inline def SuredAddressRegRegRegRegDefinition: Definition = InstructionDefinitions4.SuredAddressRegRegRegReg
  inline def SustAddressRegImmDefinition: Definition = InstructionDefinitions4.SustAddressRegImm
  inline def SustAddressRegImmImmDefinition: Definition = InstructionDefinitions4.SustAddressRegImmImm
  inline def SustAddressRegImmImmImmDefinition: Definition = InstructionDefinitions4.SustAddressRegImmImmImm
  inline def SustAddressRegRegDefinition: Definition = InstructionDefinitions4.SustAddressRegReg
  inline def SustAddressRegRegImmDefinition: Definition = InstructionDefinitions4.SustAddressRegRegImm
  inline def SustAddressRegRegRegDefinition: Definition = InstructionDefinitions4.SustAddressRegRegReg
  inline def SustAddressRegRegRegImmDefinition: Definition = InstructionDefinitions4.SustAddressRegRegRegImm
  inline def SustAddressRegRegRegRegDefinition: Definition = InstructionDefinitions4.SustAddressRegRegRegReg
  inline def SustgaAddressConstRegPredDefinition: Definition = InstructionDefinitions4.SustgaAddressConstRegPred
  inline def SustgaAddressRegRegPredDefinition: Definition = InstructionDefinitions4.SustgaAddressRegRegPred
  inline def SyncNoOperandsDefinition: Definition = InstructionDefinitions4.SyncNoOperands
  inline def SyncsNoOperandsDefinition: Definition = InstructionDefinitions4.SyncsNoOperands
  inline def SyncsAddressDefinition: Definition = InstructionDefinitions4.SyncsAddress
  inline def SyncsAddressRegDefinition: Definition = InstructionDefinitions4.SyncsAddressReg
  inline def SyncsPredAddressRegDefinition: Definition = InstructionDefinitions4.SyncsPredAddressReg
  inline def SyncsRegAddressDefinition: Definition = InstructionDefinitions4.SyncsRegAddress
  inline def SyncsRegAddressRegDefinition: Definition = InstructionDefinitions4.SyncsRegAddressReg
  inline def SyncsRegAddressRegRegDefinition: Definition = InstructionDefinitions4.SyncsRegAddressRegReg
  inline def TexPredRegRegRegImmDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegImm
  inline def TexPredRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegImmImm
  inline def TexPredRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegImmImmImm
  inline def TexPredRegRegRegRegDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegReg
  inline def TexPredRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegRegImm
  inline def TexPredRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegRegImmImm
  inline def TexPredRegRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegRegImmImmImm
  inline def TexPredRegRegRegRegImmImmImmImmDefinition: Definition =
    InstructionDefinitions4.TexPredRegRegRegRegImmImmImmImm
  inline def TexPredRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegRegReg
  inline def TexPredRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegRegRegImm
  inline def TexPredRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegRegRegImmImm
  inline def TexPredRegRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegRegRegReg
  inline def TexPredRegRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TexPredRegRegRegRegRegRegImm
  inline def TexPredRegRegRegRegRegRegImmImmDefinition: Definition =
    InstructionDefinitions4.TexPredRegRegRegRegRegRegImmImm
  inline def TexRegRegRegImmDefinition: Definition = InstructionDefinitions4.TexRegRegRegImm
  inline def TexRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TexRegRegRegImmImm
  inline def TexRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TexRegRegRegImmImmImm
  inline def TexRegRegRegRegDefinition: Definition = InstructionDefinitions4.TexRegRegRegReg
  inline def TexRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TexRegRegRegRegImm
  inline def TexRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TexRegRegRegRegImmImm
  inline def TexRegRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TexRegRegRegRegImmImmImm
  inline def TexRegRegRegRegImmImmImmImmDefinition: Definition = InstructionDefinitions4.TexRegRegRegRegImmImmImmImm
  inline def TexRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TexRegRegRegRegReg
  inline def TexRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TexRegRegRegRegRegImm
  inline def TexRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TexRegRegRegRegRegImmImm
  inline def TexRegRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TexRegRegRegRegRegReg
  inline def TexRegRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TexRegRegRegRegRegRegImm
  inline def TexRegRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TexRegRegRegRegRegRegImmImm
  inline def TexdepbarImmDefinition: Definition = InstructionDefinitions4.TexdepbarImm
  inline def TexsRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TexsRegRegRegRegImm
  inline def TldPredRegRegRegImmDefinition: Definition = InstructionDefinitions4.TldPredRegRegRegImm
  inline def TldPredRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TldPredRegRegRegImmImm
  inline def TldPredRegRegRegRegDefinition: Definition = InstructionDefinitions4.TldPredRegRegRegReg
  inline def TldPredRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TldPredRegRegRegRegImm
  inline def TldPredRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TldPredRegRegRegRegImmImm
  inline def TldPredRegRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TldPredRegRegRegRegImmImmImm
  inline def TldPredRegRegRegRegImmImmImmImmDefinition: Definition =
    InstructionDefinitions4.TldPredRegRegRegRegImmImmImmImm
  inline def TldPredRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TldPredRegRegRegRegReg
  inline def TldPredRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TldPredRegRegRegRegRegImm
  inline def TldPredRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TldPredRegRegRegRegRegImmImm
  inline def TldPredRegRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TldPredRegRegRegRegRegReg
  inline def TldPredRegRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TldPredRegRegRegRegRegRegImm
  inline def TldPredRegRegRegRegRegRegImmImmDefinition: Definition =
    InstructionDefinitions4.TldPredRegRegRegRegRegRegImmImm
  inline def TldRegRegRegImmDefinition: Definition = InstructionDefinitions4.TldRegRegRegImm
  inline def TldRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TldRegRegRegImmImm
  inline def TldRegRegRegRegDefinition: Definition = InstructionDefinitions4.TldRegRegRegReg
  inline def TldRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TldRegRegRegRegImm
  inline def TldRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TldRegRegRegRegImmImm
  inline def TldRegRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TldRegRegRegRegImmImmImm
  inline def TldRegRegRegRegImmImmImmImmDefinition: Definition = InstructionDefinitions4.TldRegRegRegRegImmImmImmImm
  inline def TldRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TldRegRegRegRegReg
  inline def TldRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TldRegRegRegRegRegImm
  inline def TldRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TldRegRegRegRegRegImmImm
  inline def TldRegRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TldRegRegRegRegRegReg
  inline def TldRegRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TldRegRegRegRegRegRegImm
  inline def TldRegRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TldRegRegRegRegRegRegImmImm
  inline def Tld4PredRegRegRegImmDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegImm
  inline def Tld4PredRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegImmImm
  inline def Tld4PredRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegImmImmImm
  inline def Tld4PredRegRegRegRegDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegReg
  inline def Tld4PredRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegRegImm
  inline def Tld4PredRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegRegImmImm
  inline def Tld4PredRegRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegRegImmImmImm
  inline def Tld4PredRegRegRegRegImmImmImmImmDefinition: Definition =
    InstructionDefinitions4.Tld4PredRegRegRegRegImmImmImmImm
  inline def Tld4PredRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegRegReg
  inline def Tld4PredRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegRegRegImm
  inline def Tld4PredRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegRegRegImmImm
  inline def Tld4PredRegRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegRegRegReg
  inline def Tld4PredRegRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.Tld4PredRegRegRegRegRegRegImm
  inline def Tld4PredRegRegRegRegRegRegImmImmDefinition: Definition =
    InstructionDefinitions4.Tld4PredRegRegRegRegRegRegImmImm
  inline def Tld4RegRegRegImmDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegImm
  inline def Tld4RegRegRegImmImmDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegImmImm
  inline def Tld4RegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegImmImmImm
  inline def Tld4RegRegRegRegDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegReg
  inline def Tld4RegRegRegRegImmDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegRegImm
  inline def Tld4RegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegRegImmImm
  inline def Tld4RegRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegRegImmImmImm
  inline def Tld4RegRegRegRegImmImmImmImmDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegRegImmImmImmImm
  inline def Tld4RegRegRegRegRegDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegRegReg
  inline def Tld4RegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegRegRegImm
  inline def Tld4RegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegRegRegImmImm
  inline def Tld4RegRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegRegRegReg
  inline def Tld4RegRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegRegRegRegImm
  inline def Tld4RegRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.Tld4RegRegRegRegRegRegImmImm
  inline def Tld4sRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.Tld4sRegRegRegRegImm
  inline def TldsRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TldsRegRegRegRegImm
  inline def TmmlRegRegImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegImm
  inline def TmmlRegRegImmImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegImmImm
  inline def TmmlRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegImmImmImm
  inline def TmmlRegRegRegImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegRegImm
  inline def TmmlRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegRegImmImm
  inline def TmmlRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegRegImmImmImm
  inline def TmmlRegRegRegRegDefinition: Definition = InstructionDefinitions4.TmmlRegRegRegReg
  inline def TmmlRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegRegRegImm
  inline def TmmlRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegRegRegImmImm
  inline def TmmlRegRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegRegRegImmImmImm
  inline def TmmlRegRegRegRegImmImmImmImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegRegRegImmImmImmImm
  inline def TmmlRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TmmlRegRegRegRegReg
  inline def TmmlRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegRegRegRegImm
  inline def TmmlRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TmmlRegRegRegRegRegImmImm
  inline def TxaRegRegImmDefinition: Definition = InstructionDefinitions4.TxaRegRegImm
  inline def TxaRegRegImmImmDefinition: Definition = InstructionDefinitions4.TxaRegRegImmImm
  inline def TxaRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TxaRegRegImmImmImm
  inline def TxdPredRegRegRegImmDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegImm
  inline def TxdPredRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegImmImm
  inline def TxdPredRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegImmImmImm
  inline def TxdPredRegRegRegRegDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegReg
  inline def TxdPredRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegRegImm
  inline def TxdPredRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegRegImmImm
  inline def TxdPredRegRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegRegImmImmImm
  inline def TxdPredRegRegRegRegImmImmImmImmDefinition: Definition =
    InstructionDefinitions4.TxdPredRegRegRegRegImmImmImmImm
  inline def TxdPredRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegRegReg
  inline def TxdPredRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegRegRegImm
  inline def TxdPredRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegRegRegImmImm
  inline def TxdPredRegRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegRegRegReg
  inline def TxdPredRegRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TxdPredRegRegRegRegRegRegImm
  inline def TxdPredRegRegRegRegRegRegImmImmDefinition: Definition =
    InstructionDefinitions4.TxdPredRegRegRegRegRegRegImmImm
  inline def TxdRegRegRegImmDefinition: Definition = InstructionDefinitions4.TxdRegRegRegImm
  inline def TxdRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TxdRegRegRegImmImm
  inline def TxdRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TxdRegRegRegImmImmImm
  inline def TxdRegRegRegRegDefinition: Definition = InstructionDefinitions4.TxdRegRegRegReg
  inline def TxdRegRegRegRegImmDefinition: Definition = InstructionDefinitions4.TxdRegRegRegRegImm
  inline def TxdRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions4.TxdRegRegRegRegImmImm
  inline def TxdRegRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions4.TxdRegRegRegRegImmImmImm
  inline def TxdRegRegRegRegImmImmImmImmDefinition: Definition = InstructionDefinitions4.TxdRegRegRegRegImmImmImmImm
  inline def TxdRegRegRegRegRegDefinition: Definition = InstructionDefinitions4.TxdRegRegRegRegReg
  inline def TxdRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions5.TxdRegRegRegRegRegImm
  inline def TxdRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions5.TxdRegRegRegRegRegImmImm
  inline def TxdRegRegRegRegRegRegDefinition: Definition = InstructionDefinitions5.TxdRegRegRegRegRegReg
  inline def TxdRegRegRegRegRegRegImmDefinition: Definition = InstructionDefinitions5.TxdRegRegRegRegRegRegImm
  inline def TxdRegRegRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions5.TxdRegRegRegRegRegRegImmImm
  inline def TxqRegRegImmDefinition: Definition = InstructionDefinitions5.TxqRegRegImm
  inline def TxqRegRegImmImmDefinition: Definition = InstructionDefinitions5.TxqRegRegImmImm
  inline def TxqRegRegImmImmImmDefinition: Definition = InstructionDefinitions5.TxqRegRegImmImmImm
  inline def TxqRegRegImmImmImmImmDefinition: Definition = InstructionDefinitions5.TxqRegRegImmImmImmImm
  inline def TxqRegRegRegImmDefinition: Definition = InstructionDefinitions5.TxqRegRegRegImm
  inline def TxqRegRegRegImmImmImmDefinition: Definition = InstructionDefinitions5.TxqRegRegRegImmImmImm
  inline def TxqRegRegRegImmImmImmImmDefinition: Definition = InstructionDefinitions5.TxqRegRegRegImmImmImmImm
  inline def TxqRegRegRegRegImmDefinition: Definition = InstructionDefinitions5.TxqRegRegRegRegImm
  inline def TxqRegRegRegRegImmImmDefinition: Definition = InstructionDefinitions5.TxqRegRegRegRegImmImm
  inline def UblkcpAddressAddressRegDefinition: Definition = InstructionDefinitions5.UblkcpAddressAddressReg
  inline def UblkcpAddressAddressRegAddressDefinition: Definition =
    InstructionDefinitions5.UblkcpAddressAddressRegAddress
  inline def UblkpfAddressRegDefinition: Definition = InstructionDefinitions5.UblkpfAddressReg
  inline def UblkpfAddressRegAddressDefinition: Definition = InstructionDefinitions5.UblkpfAddressRegAddress
  inline def UblkredAddressAddressRegDefinition: Definition = InstructionDefinitions5.UblkredAddressAddressReg
  inline def UblkredAddressAddressRegAddressDefinition: Definition =
    InstructionDefinitions5.UblkredAddressAddressRegAddress
  inline def UbmskRegRegImmDefinition: Definition = InstructionDefinitions5.UbmskRegRegImm
  inline def UbmskRegRegRegDefinition: Definition = InstructionDefinitions5.UbmskRegRegReg
  inline def UbrevRegImmDefinition: Definition = InstructionDefinitions5.UbrevRegImm
  inline def UbrevRegRegDefinition: Definition = InstructionDefinitions5.UbrevRegReg
  inline def UcgabararvNoOperandsDefinition: Definition = InstructionDefinitions5.UcgabararvNoOperands
  inline def UcgabargetRegDefinition: Definition = InstructionDefinitions5.UcgabargetReg
  inline def UcgabarsetRegDefinition: Definition = InstructionDefinitions5.UcgabarsetReg
  inline def UcgabarwaitNoOperandsDefinition: Definition = InstructionDefinitions5.UcgabarwaitNoOperands
  inline def Ucgabar_ArvNoOperandsDefinition: Definition = InstructionDefinitions5.Ucgabar_ArvNoOperands
  inline def Ucgabar_WaitNoOperandsDefinition: Definition = InstructionDefinitions5.Ucgabar_WaitNoOperands
  inline def UcleaRegPredRegImmImmDefinition: Definition = InstructionDefinitions5.UcleaRegPredRegImmImm
  inline def UcleaRegPredRegRegImmDefinition: Definition = InstructionDefinitions5.UcleaRegPredRegRegImm
  inline def Uf2fRegImmDefinition: Definition = InstructionDefinitions5.Uf2fRegImm
  inline def Uf2fRegRegDefinition: Definition = InstructionDefinitions5.Uf2fRegReg
  inline def Uf2fpRegImmDefinition: Definition = InstructionDefinitions5.Uf2fpRegImm
  inline def Uf2fpRegImmRegDefinition: Definition = InstructionDefinitions5.Uf2fpRegImmReg
  inline def Uf2fpRegRegDefinition: Definition = InstructionDefinitions5.Uf2fpRegReg
  inline def Uf2fpRegRegImmDefinition: Definition = InstructionDefinitions5.Uf2fpRegRegImm
  inline def Uf2fpRegRegImmRegDefinition: Definition = InstructionDefinitions5.Uf2fpRegRegImmReg
  inline def Uf2fpRegRegRegDefinition: Definition = InstructionDefinitions5.Uf2fpRegRegReg
  inline def Uf2fpRegRegRegImmDefinition: Definition = InstructionDefinitions5.Uf2fpRegRegRegImm
  inline def Uf2fpRegRegRegRegDefinition: Definition = InstructionDefinitions5.Uf2fpRegRegRegReg
  inline def Uf2iRegImmDefinition: Definition = InstructionDefinitions5.Uf2iRegImm
  inline def Uf2iRegRegDefinition: Definition = InstructionDefinitions5.Uf2iRegReg
  inline def Uf2ipRegRegImmRegDefinition: Definition = InstructionDefinitions5.Uf2ipRegRegImmReg
  inline def Uf2ipRegRegRegImmDefinition: Definition = InstructionDefinitions5.Uf2ipRegRegRegImm
  inline def Uf2ipRegRegRegRegDefinition: Definition = InstructionDefinitions5.Uf2ipRegRegRegReg
  inline def UfaddRegRegImmDefinition: Definition = InstructionDefinitions5.UfaddRegRegImm
  inline def UfaddRegRegRegDefinition: Definition = InstructionDefinitions5.UfaddRegRegReg
  inline def UffmaRegRegImmRegDefinition: Definition = InstructionDefinitions5.UffmaRegRegImmReg
  inline def UffmaRegRegRegImmDefinition: Definition = InstructionDefinitions5.UffmaRegRegRegImm
  inline def UffmaRegRegRegRegDefinition: Definition = InstructionDefinitions5.UffmaRegRegRegReg
  inline def UfhaddRegRegRegDefinition: Definition = InstructionDefinitions5.UfhaddRegRegReg
  inline def UfhfmaRegRegRegRegDefinition: Definition = InstructionDefinitions5.UfhfmaRegRegRegReg
  inline def UfloRegImmDefinition: Definition = InstructionDefinitions5.UfloRegImm
  inline def UfloRegPredImmDefinition: Definition = InstructionDefinitions5.UfloRegPredImm
  inline def UfloRegPredRegDefinition: Definition = InstructionDefinitions5.UfloRegPredReg
  inline def UfloRegRegDefinition: Definition = InstructionDefinitions5.UfloRegReg
  inline def UfmnmxRegPredRegImmPredDefinition: Definition = InstructionDefinitions5.UfmnmxRegPredRegImmPred
  inline def UfmnmxRegPredRegRegPredDefinition: Definition = InstructionDefinitions5.UfmnmxRegPredRegRegPred
  inline def UfmnmxRegRegImmPredDefinition: Definition = InstructionDefinitions5.UfmnmxRegRegImmPred
  inline def UfmnmxRegRegRegPredDefinition: Definition = InstructionDefinitions5.UfmnmxRegRegRegPred
  inline def UfmulRegRegImmDefinition: Definition = InstructionDefinitions5.UfmulRegRegImm
  inline def UfmulRegRegRegDefinition: Definition = InstructionDefinitions5.UfmulRegRegReg
  inline def UfrndRegImmDefinition: Definition = InstructionDefinitions5.UfrndRegImm
  inline def UfrndRegRegDefinition: Definition = InstructionDefinitions5.UfrndRegReg
  inline def UfselRegRegImmPredDefinition: Definition = InstructionDefinitions5.UfselRegRegImmPred
  inline def UfselRegRegRegPredDefinition: Definition = InstructionDefinitions5.UfselRegRegRegPred
  inline def UfsetRegRegImmDefinition: Definition = InstructionDefinitions5.UfsetRegRegImm
  inline def UfsetRegRegImmPredDefinition: Definition = InstructionDefinitions5.UfsetRegRegImmPred
  inline def UfsetRegRegRegDefinition: Definition = InstructionDefinitions5.UfsetRegRegReg
  inline def UfsetRegRegRegPredDefinition: Definition = InstructionDefinitions5.UfsetRegRegRegPred
  inline def UfsetpPredPredRegImmPredDefinition: Definition = InstructionDefinitions5.UfsetpPredPredRegImmPred
  inline def UfsetpPredPredRegRegPredDefinition: Definition = InstructionDefinitions5.UfsetpPredPredRegRegPred
  inline def UfsetpPredRegImmDefinition: Definition = InstructionDefinitions5.UfsetpPredRegImm
  inline def UfsetpPredRegRegDefinition: Definition = InstructionDefinitions5.UfsetpPredRegReg
  inline def UgetnextworkidAddressAddressDefinition: Definition = InstructionDefinitions5.UgetnextworkidAddressAddress
  inline def Ui2fRegImmDefinition: Definition = InstructionDefinitions5.Ui2fRegImm
  inline def Ui2fRegRegDefinition: Definition = InstructionDefinitions5.Ui2fRegReg
  inline def Ui2fpRegImmDefinition: Definition = InstructionDefinitions5.Ui2fpRegImm
  inline def Ui2fpRegRegDefinition: Definition = InstructionDefinitions5.Ui2fpRegReg
  inline def Ui2iRegImmDefinition: Definition = InstructionDefinitions5.Ui2iRegImm
  inline def Ui2iRegRegDefinition: Definition = InstructionDefinitions5.Ui2iRegReg
  inline def Ui2ipRegRegImmRegDefinition: Definition = InstructionDefinitions5.Ui2ipRegRegImmReg
  inline def Ui2ipRegRegRegRegDefinition: Definition = InstructionDefinitions5.Ui2ipRegRegRegReg
  inline def UiabsRegImmDefinition: Definition = InstructionDefinitions5.UiabsRegImm
  inline def UiabsRegRegDefinition: Definition = InstructionDefinitions5.UiabsRegReg
  inline def Uiadd3RegPredPredRegImmRegDefinition: Definition = InstructionDefinitions5.Uiadd3RegPredPredRegImmReg
  inline def Uiadd3RegPredPredRegImmRegPredPredDefinition: Definition =
    InstructionDefinitions5.Uiadd3RegPredPredRegImmRegPredPred
  inline def Uiadd3RegPredPredRegRegRegDefinition: Definition = InstructionDefinitions5.Uiadd3RegPredPredRegRegReg
  inline def Uiadd3RegPredPredRegRegRegPredPredDefinition: Definition =
    InstructionDefinitions5.Uiadd3RegPredPredRegRegRegPredPred
  inline def Uiadd3RegPredRegImmRegDefinition: Definition = InstructionDefinitions5.Uiadd3RegPredRegImmReg
  inline def Uiadd3RegPredRegImmRegPredPredDefinition: Definition =
    InstructionDefinitions5.Uiadd3RegPredRegImmRegPredPred
  inline def Uiadd3RegPredRegRegRegDefinition: Definition = InstructionDefinitions5.Uiadd3RegPredRegRegReg
  inline def Uiadd3RegPredRegRegRegPredPredDefinition: Definition =
    InstructionDefinitions5.Uiadd3RegPredRegRegRegPredPred
  inline def Uiadd3RegRegImmRegDefinition: Definition = InstructionDefinitions5.Uiadd3RegRegImmReg
  inline def Uiadd3RegRegImmRegPredPredDefinition: Definition = InstructionDefinitions5.Uiadd3RegRegImmRegPredPred
  inline def Uiadd3RegRegRegRegDefinition: Definition = InstructionDefinitions5.Uiadd3RegRegRegReg
  inline def Uiadd3RegRegRegRegPredPredDefinition: Definition = InstructionDefinitions5.Uiadd3RegRegRegRegPredPred
  inline def UimadRegPredRegImmRegDefinition: Definition = InstructionDefinitions5.UimadRegPredRegImmReg
  inline def UimadRegPredRegImmRegPredDefinition: Definition = InstructionDefinitions5.UimadRegPredRegImmRegPred
  inline def UimadRegPredRegRegRegDefinition: Definition = InstructionDefinitions5.UimadRegPredRegRegReg
  inline def UimadRegPredRegRegRegPredDefinition: Definition = InstructionDefinitions5.UimadRegPredRegRegRegPred
  inline def UimadRegRegImmRegDefinition: Definition = InstructionDefinitions5.UimadRegRegImmReg
  inline def UimadRegRegImmRegPredDefinition: Definition = InstructionDefinitions5.UimadRegRegImmRegPred
  inline def UimadRegRegRegImmDefinition: Definition = InstructionDefinitions5.UimadRegRegRegImm
  inline def UimadRegRegRegImmPredDefinition: Definition = InstructionDefinitions5.UimadRegRegRegImmPred
  inline def UimadRegRegRegRegDefinition: Definition = InstructionDefinitions5.UimadRegRegRegReg
  inline def UimadRegRegRegRegPredDefinition: Definition = InstructionDefinitions5.UimadRegRegRegRegPred
  inline def UimnmxPredPredRegRegImmPredDefinition: Definition = InstructionDefinitions5.UimnmxPredPredRegRegImmPred
  inline def UimnmxPredPredRegRegImmPredPredDefinition: Definition =
    InstructionDefinitions5.UimnmxPredPredRegRegImmPredPred
  inline def UimnmxPredPredRegRegRegPredDefinition: Definition = InstructionDefinitions5.UimnmxPredPredRegRegRegPred
  inline def UimnmxPredPredRegRegRegPredPredDefinition: Definition =
    InstructionDefinitions5.UimnmxPredPredRegRegRegPredPred
  inline def UimnmxPredRegRegImmPredDefinition: Definition = InstructionDefinitions5.UimnmxPredRegRegImmPred
  inline def UimnmxPredRegRegImmPredPredDefinition: Definition = InstructionDefinitions5.UimnmxPredRegRegImmPredPred
  inline def UimnmxPredRegRegRegPredDefinition: Definition = InstructionDefinitions5.UimnmxPredRegRegRegPred
  inline def UimnmxPredRegRegRegPredPredDefinition: Definition = InstructionDefinitions5.UimnmxPredRegRegRegPredPred
  inline def UimnmxRegRegImmPredDefinition: Definition = InstructionDefinitions5.UimnmxRegRegImmPred
  inline def UimnmxRegRegImmPredPredDefinition: Definition = InstructionDefinitions5.UimnmxRegRegImmPredPred
  inline def UimnmxRegRegRegPredDefinition: Definition = InstructionDefinitions5.UimnmxRegRegRegPred
  inline def UimnmxRegRegRegPredPredDefinition: Definition = InstructionDefinitions5.UimnmxRegRegRegPredPred
  inline def UisetpPredPredRegImmPredDefinition: Definition = InstructionDefinitions5.UisetpPredPredRegImmPred
  inline def UisetpPredPredRegImmPredPredDefinition: Definition = InstructionDefinitions5.UisetpPredPredRegImmPredPred
  inline def UisetpPredPredRegRegPredDefinition: Definition = InstructionDefinitions5.UisetpPredPredRegRegPred
  inline def UisetpPredPredRegRegPredPredDefinition: Definition = InstructionDefinitions5.UisetpPredPredRegRegPredPred
  inline def UisetpPredRegImmDefinition: Definition = InstructionDefinitions5.UisetpPredRegImm
  inline def UisetpPredRegImmPredDefinition: Definition = InstructionDefinitions5.UisetpPredRegImmPred
  inline def UisetpPredRegRegDefinition: Definition = InstructionDefinitions5.UisetpPredRegReg
  inline def UisetpPredRegRegPredDefinition: Definition = InstructionDefinitions5.UisetpPredRegRegPred
  inline def UldcRegAddressDefinition: Definition = InstructionDefinitions5.UldcRegAddress
  inline def UldcRegAddressImmDefinition: Definition = InstructionDefinitions5.UldcRegAddressImm
  inline def UldcRegAddressPredDefinition: Definition = InstructionDefinitions5.UldcRegAddressPred
  inline def UldcRegConstDefinition: Definition = InstructionDefinitions5.UldcRegConst
  inline def UleaRegPredRegImmImmDefinition: Definition = InstructionDefinitions5.UleaRegPredRegImmImm
  inline def UleaRegPredRegImmImmPredDefinition: Definition = InstructionDefinitions5.UleaRegPredRegImmImmPred
  inline def UleaRegPredRegImmRegImmDefinition: Definition = InstructionDefinitions5.UleaRegPredRegImmRegImm
  inline def UleaRegPredRegImmRegImmPredDefinition: Definition = InstructionDefinitions5.UleaRegPredRegImmRegImmPred
  inline def UleaRegPredRegRegImmDefinition: Definition = InstructionDefinitions5.UleaRegPredRegRegImm
  inline def UleaRegPredRegRegImmImmDefinition: Definition = InstructionDefinitions5.UleaRegPredRegRegImmImm
  inline def UleaRegPredRegRegImmImmPredDefinition: Definition = InstructionDefinitions5.UleaRegPredRegRegImmImmPred
  inline def UleaRegPredRegRegImmPredDefinition: Definition = InstructionDefinitions5.UleaRegPredRegRegImmPred
  inline def UleaRegPredRegRegRegImmDefinition: Definition = InstructionDefinitions5.UleaRegPredRegRegRegImm
  inline def UleaRegPredRegRegRegImmPredDefinition: Definition = InstructionDefinitions5.UleaRegPredRegRegRegImmPred
  inline def UleaRegRegImmImmDefinition: Definition = InstructionDefinitions5.UleaRegRegImmImm
  inline def UleaRegRegImmImmPredDefinition: Definition = InstructionDefinitions5.UleaRegRegImmImmPred
  inline def UleaRegRegImmRegImmDefinition: Definition = InstructionDefinitions5.UleaRegRegImmRegImm
  inline def UleaRegRegImmRegImmPredDefinition: Definition = InstructionDefinitions5.UleaRegRegImmRegImmPred
  inline def UleaRegRegRegImmDefinition: Definition = InstructionDefinitions5.UleaRegRegRegImm
  inline def UleaRegRegRegImmImmDefinition: Definition = InstructionDefinitions5.UleaRegRegRegImmImm
  inline def UleaRegRegRegImmImmPredDefinition: Definition = InstructionDefinitions5.UleaRegRegRegImmImmPred
  inline def UleaRegRegRegImmPredDefinition: Definition = InstructionDefinitions5.UleaRegRegRegImmPred
  inline def UleaRegRegRegRegImmDefinition: Definition = InstructionDefinitions5.UleaRegRegRegRegImm
  inline def UleaRegRegRegRegImmPredDefinition: Definition = InstructionDefinitions5.UleaRegRegRegRegImmPred
  inline def UlepcRegDefinition: Definition = InstructionDefinitions5.UlepcReg
  inline def UlepcRegImmDefinition: Definition = InstructionDefinitions5.UlepcRegImm
  inline def UlopPredRegRegImmDefinition: Definition = InstructionDefinitions5.UlopPredRegRegImm
  inline def UlopPredRegRegImmPredDefinition: Definition = InstructionDefinitions5.UlopPredRegRegImmPred
  inline def UlopPredRegRegRegDefinition: Definition = InstructionDefinitions5.UlopPredRegRegReg
  inline def UlopPredRegRegRegPredDefinition: Definition = InstructionDefinitions5.UlopPredRegRegRegPred
  inline def UlopRegRegImmDefinition: Definition = InstructionDefinitions5.UlopRegRegImm
  inline def UlopRegRegImmPredDefinition: Definition = InstructionDefinitions5.UlopRegRegImmPred
  inline def UlopRegRegRegDefinition: Definition = InstructionDefinitions5.UlopRegRegReg
  inline def UlopRegRegRegPredDefinition: Definition = InstructionDefinitions5.UlopRegRegRegPred
  inline def Ulop3PredRegRegImmRegDefinition: Definition = InstructionDefinitions5.Ulop3PredRegRegImmReg
  inline def Ulop3PredRegRegImmRegImmDefinition: Definition = InstructionDefinitions5.Ulop3PredRegRegImmRegImm
  inline def Ulop3PredRegRegImmRegImmPredDefinition: Definition = InstructionDefinitions5.Ulop3PredRegRegImmRegImmPred
  inline def Ulop3PredRegRegImmRegPredDefinition: Definition = InstructionDefinitions5.Ulop3PredRegRegImmRegPred
  inline def Ulop3PredRegRegRegRegDefinition: Definition = InstructionDefinitions5.Ulop3PredRegRegRegReg
  inline def Ulop3PredRegRegRegRegImmDefinition: Definition = InstructionDefinitions5.Ulop3PredRegRegRegRegImm
  inline def Ulop3PredRegRegRegRegImmPredDefinition: Definition = InstructionDefinitions5.Ulop3PredRegRegRegRegImmPred
  inline def Ulop3PredRegRegRegRegPredDefinition: Definition = InstructionDefinitions5.Ulop3PredRegRegRegRegPred
  inline def Ulop3RegRegImmRegDefinition: Definition = InstructionDefinitions5.Ulop3RegRegImmReg
  inline def Ulop3RegRegImmRegImmDefinition: Definition = InstructionDefinitions5.Ulop3RegRegImmRegImm
  inline def Ulop3RegRegImmRegImmPredDefinition: Definition = InstructionDefinitions5.Ulop3RegRegImmRegImmPred
  inline def Ulop3RegRegImmRegPredDefinition: Definition = InstructionDefinitions5.Ulop3RegRegImmRegPred
  inline def Ulop3RegRegRegRegDefinition: Definition = InstructionDefinitions5.Ulop3RegRegRegReg
  inline def Ulop3RegRegRegRegImmDefinition: Definition = InstructionDefinitions5.Ulop3RegRegRegRegImm
  inline def Ulop3RegRegRegRegImmPredDefinition: Definition = InstructionDefinitions5.Ulop3RegRegRegRegImmPred
  inline def Ulop3RegRegRegRegPredDefinition: Definition = InstructionDefinitions5.Ulop3RegRegRegRegPred
  inline def Ulop32iPredRegRegImmDefinition: Definition = InstructionDefinitions5.Ulop32iPredRegRegImm
  inline def Ulop32iPredRegRegImmPredDefinition: Definition = InstructionDefinitions5.Ulop32iPredRegRegImmPred
  inline def Ulop32iRegRegImmDefinition: Definition = InstructionDefinitions5.Ulop32iRegRegImm
  inline def Ulop32iRegRegImmPredDefinition: Definition = InstructionDefinitions5.Ulop32iRegRegImmPred
  inline def UmemsetsAddressRegRegDefinition: Definition = InstructionDefinitions5.UmemsetsAddressRegReg
  inline def UmovRegImmDefinition: Definition = InstructionDefinitions5.UmovRegImm
  inline def UmovRegRegDefinition: Definition = InstructionDefinitions5.UmovRegReg
  inline def Up2urRegDefinition: Definition = InstructionDefinitions5.Up2urReg
  inline def Up2urRegRegImmDefinition: Definition = InstructionDefinitions5.Up2urRegRegImm
  inline def Up2urRegRegRegDefinition: Definition = InstructionDefinitions5.Up2urRegRegReg
  inline def Uplop3PredPredPredPredDefinition: Definition = InstructionDefinitions5.Uplop3PredPredPredPred
  inline def Uplop3PredPredPredPredImmDefinition: Definition = InstructionDefinitions5.Uplop3PredPredPredPredImm
  inline def Uplop3PredPredPredPredPredImmImmDefinition: Definition =
    InstructionDefinitions5.Uplop3PredPredPredPredPredImmImm
  inline def Uplop3PredPredPredRegPredImmImmDefinition: Definition =
    InstructionDefinitions5.Uplop3PredPredPredRegPredImmImm
  inline def Uplop3PredPredPredRegRegImmImmDefinition: Definition =
    InstructionDefinitions5.Uplop3PredPredPredRegRegImmImm
  inline def Uplop3PredPredRegPredImmDefinition: Definition = InstructionDefinitions5.Uplop3PredPredRegPredImm
  inline def Uplop3PredPredRegRegImmDefinition: Definition = InstructionDefinitions5.Uplop3PredPredRegRegImm
  inline def Uplop3PredPredRegRegRegImmImmDefinition: Definition = InstructionDefinitions5.Uplop3PredPredRegRegRegImmImm
  inline def Uplop3PredRegRegRegImmDefinition: Definition = InstructionDefinitions5.Uplop3PredRegRegRegImm
  inline def UpopcRegImmDefinition: Definition = InstructionDefinitions5.UpopcRegImm
  inline def UpopcRegRegDefinition: Definition = InstructionDefinitions5.UpopcRegReg
  inline def UprmtRegRegImmRegDefinition: Definition = InstructionDefinitions5.UprmtRegRegImmReg
  inline def UprmtRegRegRegRegDefinition: Definition = InstructionDefinitions5.UprmtRegRegRegReg
  inline def UpsetpPredPredPredDefinition: Definition = InstructionDefinitions5.UpsetpPredPredPred
  inline def UpsetpPredPredPredPredPredDefinition: Definition = InstructionDefinitions5.UpsetpPredPredPredPredPred
  inline def Ur2upRegDefinition: Definition = InstructionDefinitions5.Ur2upReg
  inline def Ur2upRegImmDefinition: Definition = InstructionDefinitions5.Ur2upRegImm
  inline def Ur2upRegRegDefinition: Definition = InstructionDefinitions5.Ur2upRegReg
  inline def UredgrAddressRegDefinition: Definition = InstructionDefinitions5.UredgrAddressReg
  inline def UselRegRegImmPredDefinition: Definition = InstructionDefinitions5.UselRegRegImmPred
  inline def UselRegRegRegPredDefinition: Definition = InstructionDefinitions5.UselRegRegRegPred
  inline def UsetmaxregImmDefinition: Definition = InstructionDefinitions5.UsetmaxregImm
  inline def UsetmaxregPredImmDefinition: Definition = InstructionDefinitions5.UsetmaxregPredImm
  inline def UsetmaxregPredRegDefinition: Definition = InstructionDefinitions5.UsetmaxregPredReg
  inline def UsetmaxregRegDefinition: Definition = InstructionDefinitions5.UsetmaxregReg
  inline def UsetshmszNoOperandsDefinition: Definition = InstructionDefinitions5.UsetshmszNoOperands
  inline def UsetshmszImmDefinition: Definition = InstructionDefinitions5.UsetshmszImm
  inline def UsetshmszRegDefinition: Definition = InstructionDefinitions5.UsetshmszReg
  inline def UsgxtRegRegImmDefinition: Definition = InstructionDefinitions5.UsgxtRegRegImm
  inline def UsgxtRegRegRegDefinition: Definition = InstructionDefinitions5.UsgxtRegRegReg
  inline def UshfRegRegImmRegDefinition: Definition = InstructionDefinitions5.UshfRegRegImmReg
  inline def UshfRegRegRegImmDefinition: Definition = InstructionDefinitions5.UshfRegRegRegImm
  inline def UshfRegRegRegRegDefinition: Definition = InstructionDefinitions5.UshfRegRegRegReg
  inline def UshlRegImmRegDefinition: Definition = InstructionDefinitions5.UshlRegImmReg
  inline def UshlRegRegImmDefinition: Definition = InstructionDefinitions5.UshlRegRegImm
  inline def UshlRegRegRegDefinition: Definition = InstructionDefinitions5.UshlRegRegReg
  inline def UshrRegRegImmDefinition: Definition = InstructionDefinitions5.UshrRegRegImm
  inline def UshrRegRegRegDefinition: Definition = InstructionDefinitions5.UshrRegRegReg
  inline def UstgrAddressRegDefinition: Definition = InstructionDefinitions5.UstgrAddressReg
  inline def UtcatomswsPredRegRegDefinition: Definition = InstructionDefinitions5.UtcatomswsPredRegReg
  inline def UtcatomswsPredRegRegRegDefinition: Definition = InstructionDefinitions5.UtcatomswsPredRegRegReg
  inline def UtcatomswsRegRegDefinition: Definition = InstructionDefinitions5.UtcatomswsRegReg
  inline def UtcbarNoOperandsDefinition: Definition = InstructionDefinitions5.UtcbarNoOperands
  inline def UtcbarAddressRegRegDefinition: Definition = InstructionDefinitions5.UtcbarAddressRegReg
  inline def UtccpAddressAddressDefinition: Definition = InstructionDefinitions5.UtccpAddressAddress
  inline def UtchmmaAddressAddressAddressAddressAddressRegPredDefinition: Definition =
    InstructionDefinitions5.UtchmmaAddressAddressAddressAddressAddressRegPred
  inline def UtchmmaAddressAddressAddressAddressAddressRegPredImmDefinition: Definition =
    InstructionDefinitions5.UtchmmaAddressAddressAddressAddressAddressRegPredImm
  inline def UtcimmaAddressAddressAddressAddressAddressRegPredDefinition: Definition =
    InstructionDefinitions5.UtcimmaAddressAddressAddressAddressAddressRegPred
  inline def UtcommaAddressAddressAddressAddressAddressAddressPredDefinition: Definition =
    InstructionDefinitions5.UtcommaAddressAddressAddressAddressAddressAddressPred
  inline def UtcqmmaAddressAddressAddressAddressAddressAddressPredDefinition: Definition =
    InstructionDefinitions5.UtcqmmaAddressAddressAddressAddressAddressAddressPred
  inline def UtcqmmaAddressAddressAddressAddressAddressRegPredDefinition: Definition =
    InstructionDefinitions5.UtcqmmaAddressAddressAddressAddressAddressRegPred
  inline def UtcshiftAddressDefinition: Definition = InstructionDefinitions5.UtcshiftAddress
  inline def UtmacctlNoOperandsDefinition: Definition = InstructionDefinitions5.UtmacctlNoOperands
  inline def UtmacctlAddressDefinition: Definition = InstructionDefinitions5.UtmacctlAddress
  inline def UtmacmdflushNoOperandsDefinition: Definition = InstructionDefinitions5.UtmacmdflushNoOperands
  inline def UtmaldgAddressAddressDefinition: Definition = InstructionDefinitions5.UtmaldgAddressAddress
  inline def UtmaldgAddressAddressAddressDefinition: Definition = InstructionDefinitions5.UtmaldgAddressAddressAddress
  inline def UtmaldgAddressAddressRegDefinition: Definition = InstructionDefinitions5.UtmaldgAddressAddressReg
  inline def UtmaldgAddressAddressRegAddressDefinition: Definition =
    InstructionDefinitions5.UtmaldgAddressAddressRegAddress
  inline def UtmapfAddressAddressDefinition: Definition = InstructionDefinitions5.UtmapfAddressAddress
  inline def UtmapfAddressAddressAddressDefinition: Definition = InstructionDefinitions5.UtmapfAddressAddressAddress
  inline def UtmapfAddressAddressRegDefinition: Definition = InstructionDefinitions5.UtmapfAddressAddressReg
  inline def UtmapfAddressAddressRegAddressDefinition: Definition =
    InstructionDefinitions5.UtmapfAddressAddressRegAddress
  inline def UtmaredgAddressAddressDefinition: Definition = InstructionDefinitions5.UtmaredgAddressAddress
  inline def UtmaredgAddressAddressAddressDefinition: Definition = InstructionDefinitions5.UtmaredgAddressAddressAddress
  inline def UtmastgAddressAddressDefinition: Definition = InstructionDefinitions5.UtmastgAddressAddress
  inline def UtmastgAddressAddressAddressDefinition: Definition = InstructionDefinitions5.UtmastgAddressAddressAddress
  inline def UviaddRegRegImmDefinition: Definition = InstructionDefinitions6.UviaddRegRegImm
  inline def UviaddRegRegRegDefinition: Definition = InstructionDefinitions6.UviaddRegRegReg
  inline def UvimnmxRegRegImmPredDefinition: Definition = InstructionDefinitions6.UvimnmxRegRegImmPred
  inline def UvimnmxRegRegRegPredDefinition: Definition = InstructionDefinitions6.UvimnmxRegRegRegPred
  inline def UvirtcountImmDefinition: Definition = InstructionDefinitions6.UvirtcountImm
  inline def UvirtcountRegDefinition: Definition = InstructionDefinitions6.UvirtcountReg
  inline def VabsdiffRegPredRegAddressImmRegDefinition: Definition =
    InstructionDefinitions6.VabsdiffRegPredRegAddressImmReg
  inline def VabsdiffRegPredRegConstRegDefinition: Definition = InstructionDefinitions6.VabsdiffRegPredRegConstReg
  inline def VabsdiffRegPredRegImmRegDefinition: Definition = InstructionDefinitions6.VabsdiffRegPredRegImmReg
  inline def VabsdiffRegPredRegRegAddressImmDefinition: Definition =
    InstructionDefinitions6.VabsdiffRegPredRegRegAddressImm
  inline def VabsdiffRegPredRegRegConstDefinition: Definition = InstructionDefinitions6.VabsdiffRegPredRegRegConst
  inline def VabsdiffRegPredRegRegImmDefinition: Definition = InstructionDefinitions6.VabsdiffRegPredRegRegImm
  inline def VabsdiffRegPredRegRegRegDefinition: Definition = InstructionDefinitions6.VabsdiffRegPredRegRegReg
  inline def VabsdiffRegRegAddressImmRegDefinition: Definition = InstructionDefinitions6.VabsdiffRegRegAddressImmReg
  inline def VabsdiffRegRegConstRegDefinition: Definition = InstructionDefinitions6.VabsdiffRegRegConstReg
  inline def VabsdiffRegRegImmRegDefinition: Definition = InstructionDefinitions6.VabsdiffRegRegImmReg
  inline def VabsdiffRegRegRegAddressImmDefinition: Definition = InstructionDefinitions6.VabsdiffRegRegRegAddressImm
  inline def VabsdiffRegRegRegConstDefinition: Definition = InstructionDefinitions6.VabsdiffRegRegRegConst
  inline def VabsdiffRegRegRegImmDefinition: Definition = InstructionDefinitions6.VabsdiffRegRegRegImm
  inline def VabsdiffRegRegRegRegDefinition: Definition = InstructionDefinitions6.VabsdiffRegRegRegReg
  inline def Vabsdiff2RegRegImmRegDefinition: Definition = InstructionDefinitions6.Vabsdiff2RegRegImmReg
  inline def Vabsdiff2RegRegRegRegDefinition: Definition = InstructionDefinitions6.Vabsdiff2RegRegRegReg
  inline def Vabsdiff4RegPredRegAddressImmRegDefinition: Definition =
    InstructionDefinitions6.Vabsdiff4RegPredRegAddressImmReg
  inline def Vabsdiff4RegPredRegConstRegDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegPredRegConstReg
  inline def Vabsdiff4RegPredRegImmRegDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegPredRegImmReg
  inline def Vabsdiff4RegPredRegRegAddressImmDefinition: Definition =
    InstructionDefinitions6.Vabsdiff4RegPredRegRegAddressImm
  inline def Vabsdiff4RegPredRegRegConstDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegPredRegRegConst
  inline def Vabsdiff4RegPredRegRegImmDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegPredRegRegImm
  inline def Vabsdiff4RegPredRegRegRegDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegPredRegRegReg
  inline def Vabsdiff4RegRegAddressImmRegDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegRegAddressImmReg
  inline def Vabsdiff4RegRegConstRegDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegRegConstReg
  inline def Vabsdiff4RegRegImmRegDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegRegImmReg
  inline def Vabsdiff4RegRegRegAddressImmDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegRegRegAddressImm
  inline def Vabsdiff4RegRegRegConstDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegRegRegConst
  inline def Vabsdiff4RegRegRegImmDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegRegRegImm
  inline def Vabsdiff4RegRegRegRegDefinition: Definition = InstructionDefinitions6.Vabsdiff4RegRegRegReg
  inline def VaddRegRegRegDefinition: Definition = InstructionDefinitions6.VaddRegRegReg
  inline def VaddRegRegRegRegDefinition: Definition = InstructionDefinitions6.VaddRegRegRegReg
  inline def Vadd2RegRegRegDefinition: Definition = InstructionDefinitions6.Vadd2RegRegReg
  inline def Vadd2RegRegRegRegDefinition: Definition = InstructionDefinitions6.Vadd2RegRegRegReg
  inline def Vadd4RegRegRegDefinition: Definition = InstructionDefinitions6.Vadd4RegRegReg
  inline def Vadd4RegRegRegRegDefinition: Definition = InstructionDefinitions6.Vadd4RegRegRegReg
  inline def VhmnmxRegRegAddressImmRegPredDefinition: Definition = InstructionDefinitions6.VhmnmxRegRegAddressImmRegPred
  inline def VhmnmxRegRegConstRegPredDefinition: Definition = InstructionDefinitions6.VhmnmxRegRegConstRegPred
  inline def VhmnmxRegRegImmImmRegPredDefinition: Definition = InstructionDefinitions6.VhmnmxRegRegImmImmRegPred
  inline def VhmnmxRegRegRegRegPredDefinition: Definition = InstructionDefinitions6.VhmnmxRegRegRegRegPred
  inline def ViaddRegRegAddressImmDefinition: Definition = InstructionDefinitions6.ViaddRegRegAddressImm
  inline def ViaddRegRegConstDefinition: Definition = InstructionDefinitions6.ViaddRegRegConst
  inline def ViaddRegRegImmDefinition: Definition = InstructionDefinitions6.ViaddRegRegImm
  inline def ViaddRegRegRegDefinition: Definition = InstructionDefinitions6.ViaddRegRegReg
  inline def ViaddmnmxRegRegAddressImmRegPredDefinition: Definition =
    InstructionDefinitions6.ViaddmnmxRegRegAddressImmRegPred
  inline def ViaddmnmxRegRegConstRegPredDefinition: Definition = InstructionDefinitions6.ViaddmnmxRegRegConstRegPred
  inline def ViaddmnmxRegRegImmRegPredDefinition: Definition = InstructionDefinitions6.ViaddmnmxRegRegImmRegPred
  inline def ViaddmnmxRegRegRegAddressImmPredDefinition: Definition =
    InstructionDefinitions6.ViaddmnmxRegRegRegAddressImmPred
  inline def ViaddmnmxRegRegRegConstPredDefinition: Definition = InstructionDefinitions6.ViaddmnmxRegRegRegConstPred
  inline def ViaddmnmxRegRegRegImmPredDefinition: Definition = InstructionDefinitions6.ViaddmnmxRegRegRegImmPred
  inline def ViaddmnmxRegRegRegRegPredDefinition: Definition = InstructionDefinitions6.ViaddmnmxRegRegRegRegPred
  inline def VildRegAddressDefinition: Definition = InstructionDefinitions6.VildRegAddress
  inline def VimnmxRegPredPredRegAddressImmPredDefinition: Definition =
    InstructionDefinitions6.VimnmxRegPredPredRegAddressImmPred
  inline def VimnmxRegPredPredRegConstPredDefinition: Definition = InstructionDefinitions6.VimnmxRegPredPredRegConstPred
  inline def VimnmxRegPredPredRegImmPredDefinition: Definition = InstructionDefinitions6.VimnmxRegPredPredRegImmPred
  inline def VimnmxRegPredPredRegRegPredDefinition: Definition = InstructionDefinitions6.VimnmxRegPredPredRegRegPred
  inline def VimnmxRegRegAddressImmPredDefinition: Definition = InstructionDefinitions6.VimnmxRegRegAddressImmPred
  inline def VimnmxRegRegConstPredDefinition: Definition = InstructionDefinitions6.VimnmxRegRegConstPred
  inline def VimnmxRegRegImmPredDefinition: Definition = InstructionDefinitions6.VimnmxRegRegImmPred
  inline def VimnmxRegRegRegPredDefinition: Definition = InstructionDefinitions6.VimnmxRegRegRegPred
  inline def Vimnmx3RegRegAddressImmRegPredDefinition: Definition =
    InstructionDefinitions6.Vimnmx3RegRegAddressImmRegPred
  inline def Vimnmx3RegRegConstRegPredDefinition: Definition = InstructionDefinitions6.Vimnmx3RegRegConstRegPred
  inline def Vimnmx3RegRegImmRegPredDefinition: Definition = InstructionDefinitions6.Vimnmx3RegRegImmRegPred
  inline def Vimnmx3RegRegRegRegPredDefinition: Definition = InstructionDefinitions6.Vimnmx3RegRegRegRegPred
  inline def VmadRegRegImmRegDefinition: Definition = InstructionDefinitions6.VmadRegRegImmReg
  inline def VmadRegRegRegDefinition: Definition = InstructionDefinitions6.VmadRegRegReg
  inline def VmadRegRegRegRegDefinition: Definition = InstructionDefinitions6.VmadRegRegRegReg
  inline def VmnmxRegRegImmRegDefinition: Definition = InstructionDefinitions6.VmnmxRegRegImmReg
  inline def VmnmxRegRegRegRegDefinition: Definition = InstructionDefinitions6.VmnmxRegRegRegReg
  inline def Vmnmx2RegRegImmRegDefinition: Definition = InstructionDefinitions6.Vmnmx2RegRegImmReg
  inline def Vmnmx2RegRegRegRegDefinition: Definition = InstructionDefinitions6.Vmnmx2RegRegRegReg
  inline def Vmnmx4RegRegImmRegDefinition: Definition = InstructionDefinitions6.Vmnmx4RegRegImmReg
  inline def Vmnmx4RegRegRegRegDefinition: Definition = InstructionDefinitions6.Vmnmx4RegRegRegReg
  inline def VoteImmDefinition: Definition = InstructionDefinitions6.VoteImm
  inline def VotePredPredDefinition: Definition = InstructionDefinitions6.VotePredPred
  inline def VoteRegPredPredDefinition: Definition = InstructionDefinitions6.VoteRegPredPred
  inline def VoteuRegPredPredDefinition: Definition = InstructionDefinitions6.VoteuRegPredPred
  inline def VoteVtgImmDefinition: Definition = InstructionDefinitions6.VoteVtgImm
  inline def VoteVtgPredImmDefinition: Definition = InstructionDefinitions6.VoteVtgPredImm
  inline def VoteVtgPredPredImmPredDefinition: Definition = InstructionDefinitions6.VoteVtgPredPredImmPred
  inline def Vsel2RegRegImmPredDefinition: Definition = InstructionDefinitions6.Vsel2RegRegImmPred
  inline def Vsel2RegRegRegPredDefinition: Definition = InstructionDefinitions6.Vsel2RegRegRegPred
  inline def Vsel4RegRegImmPredDefinition: Definition = InstructionDefinitions6.Vsel4RegRegImmPred
  inline def Vsel4RegRegRegPredDefinition: Definition = InstructionDefinitions6.Vsel4RegRegRegPred
  inline def VsetRegRegImmRegDefinition: Definition = InstructionDefinitions6.VsetRegRegImmReg
  inline def VsetRegRegRegRegDefinition: Definition = InstructionDefinitions6.VsetRegRegRegReg
  inline def Vset2RegRegImmRegDefinition: Definition = InstructionDefinitions6.Vset2RegRegImmReg
  inline def Vset2RegRegRegRegDefinition: Definition = InstructionDefinitions6.Vset2RegRegRegReg
  inline def Vset4RegRegImmRegDefinition: Definition = InstructionDefinitions6.Vset4RegRegImmReg
  inline def Vset4RegRegRegRegDefinition: Definition = InstructionDefinitions6.Vset4RegRegRegReg
  inline def VsetpPredPredRegImmPredDefinition: Definition = InstructionDefinitions6.VsetpPredPredRegImmPred
  inline def VsetpPredPredRegRegPredDefinition: Definition = InstructionDefinitions6.VsetpPredPredRegRegPred
  inline def VsetpPredRegImmDefinition: Definition = InstructionDefinitions6.VsetpPredRegImm
  inline def VsetpPredRegRegDefinition: Definition = InstructionDefinitions6.VsetpPredRegReg
  inline def VshlRegRegImmRegDefinition: Definition = InstructionDefinitions6.VshlRegRegImmReg
  inline def VshlRegRegRegRegDefinition: Definition = InstructionDefinitions6.VshlRegRegRegReg
  inline def VshrRegRegImmRegDefinition: Definition = InstructionDefinitions6.VshrRegRegImmReg
  inline def VshrRegRegRegRegDefinition: Definition = InstructionDefinitions6.VshrRegRegRegReg
  inline def WarpgroupNoOperandsDefinition: Definition = InstructionDefinitions6.WarpgroupNoOperands
  inline def WarpgroupImmDefinition: Definition = InstructionDefinitions6.WarpgroupImm
  inline def WarpgroupsetRegDefinition: Definition = InstructionDefinitions6.WarpgroupsetReg
  inline def WarpsyncNoOperandsDefinition: Definition = InstructionDefinitions6.WarpsyncNoOperands
  inline def WarpsyncAddressImmDefinition: Definition = InstructionDefinitions6.WarpsyncAddressImm
  inline def WarpsyncConstDefinition: Definition = InstructionDefinitions6.WarpsyncConst
  inline def WarpsyncImmDefinition: Definition = InstructionDefinitions6.WarpsyncImm
  inline def WarpsyncPredDefinition: Definition = InstructionDefinitions6.WarpsyncPred
  inline def WarpsyncPredAddressImmDefinition: Definition = InstructionDefinitions6.WarpsyncPredAddressImm
  inline def WarpsyncPredConstDefinition: Definition = InstructionDefinitions6.WarpsyncPredConst
  inline def WarpsyncPredImmDefinition: Definition = InstructionDefinitions6.WarpsyncPredImm
  inline def WarpsyncPredRegDefinition: Definition = InstructionDefinitions6.WarpsyncPredReg
  inline def WarpsyncPredRegImmDefinition: Definition = InstructionDefinitions6.WarpsyncPredRegImm
  inline def WarpsyncRegDefinition: Definition = InstructionDefinitions6.WarpsyncReg
  inline def WarpsyncRegImmDefinition: Definition = InstructionDefinitions6.WarpsyncRegImm
  inline def XmadRegRegConstRegDefinition: Definition = InstructionDefinitions6.XmadRegRegConstReg
  inline def XmadRegRegImmRegDefinition: Definition = InstructionDefinitions6.XmadRegRegImmReg
  inline def XmadRegRegRegConstDefinition: Definition = InstructionDefinitions6.XmadRegRegRegConst
  inline def XmadRegRegRegRegDefinition: Definition = InstructionDefinitions6.XmadRegRegRegReg
  inline def YieldNoOperandsDefinition: Definition = InstructionDefinitions6.YieldNoOperands
  inline def YieldPredDefinition: Definition = InstructionDefinitions6.YieldPred
  inline def Hir0x002NoOperandsDefinition: Definition = InstructionDefinitions6.Hir0x002NoOperands
  inline def Hir0x003NoOperandsDefinition: Definition = InstructionDefinitions6.Hir0x003NoOperands
  inline def Hir0x003PredDefinition: Definition = InstructionDefinitions6.Hir0x003Pred
  inline def Hir0x004NoOperandsDefinition: Definition = InstructionDefinitions6.Hir0x004NoOperands
  inline def Hir0x005PredRegRegImmDefinition: Definition = InstructionDefinitions6.Hir0x005PredRegRegImm
  inline def Hir0x005RegRegImmDefinition: Definition = InstructionDefinitions6.Hir0x005RegRegImm
  inline def Hir0x006ImmDefinition: Definition = InstructionDefinitions6.Hir0x006Imm
  inline def Hir0x007NoOperandsDefinition: Definition = InstructionDefinitions6.Hir0x007NoOperands
  inline def Hir0x008ImmRegRegDefinition: Definition = InstructionDefinitions6.Hir0x008ImmRegReg
  inline def Hir0x020NoOperandsDefinition: Definition = InstructionDefinitions6.Hir0x020NoOperands
  inline def Hir0x020ImmDefinition: Definition = InstructionDefinitions6.Hir0x020Imm
  inline def Hir0x120AddressImmDefinition: Definition = InstructionDefinitions6.Hir0x120AddressImm
  inline def Hir0x120ConstDefinition: Definition = InstructionDefinitions6.Hir0x120Const
  inline def Hir0x120ImmDefinition: Definition = InstructionDefinitions6.Hir0x120Imm
  inline def Hir0x120PredAddressImmDefinition: Definition = InstructionDefinitions6.Hir0x120PredAddressImm
  inline def Hir0x120PredConstDefinition: Definition = InstructionDefinitions6.Hir0x120PredConst
  inline def Hir0x120PredImmDefinition: Definition = InstructionDefinitions6.Hir0x120PredImm
  inline def Hir0x120PredRegDefinition: Definition = InstructionDefinitions6.Hir0x120PredReg
  inline def Hir0x120RegDefinition: Definition = InstructionDefinitions6.Hir0x120Reg
  inline def Hir0x130AddressAddressAddressAddressAddressAddressPredDefinition: Definition =
    InstructionDefinitions6.Hir0x130AddressAddressAddressAddressAddressAddressPred
  inline def Hir0x150RegRegRegRegPredRegRegRegDefinition: Definition =
    InstructionDefinitions6.Hir0x150RegRegRegRegPredRegRegReg
  inline def Hir0x150RegRegRegRegRegRegRegDefinition: Definition = InstructionDefinitions6.Hir0x150RegRegRegRegRegRegReg
  inline def Hir0x160RegDefinition: Definition = InstructionDefinitions6.Hir0x160Reg
  inline def Hir0x170RegDefinition: Definition = InstructionDefinitions6.Hir0x170Reg
  inline def Hir0x1a0RegRegRegRegImmImmDefinition: Definition = InstructionDefinitions6.Hir0x1a0RegRegRegRegImmImm
  inline def Hir0x1b0RegRegRegRegDefinition: Definition = InstructionDefinitions6.Hir0x1b0RegRegRegReg
  inline def Hir0x1e0NoOperandsDefinition: Definition = InstructionDefinitions6.Hir0x1e0NoOperands
  inline def Hir0x1f0ImmRegDefinition: Definition = InstructionDefinitions6.Hir0x1f0ImmReg
  inline def Hir0x1f2RegDefinition: Definition = InstructionDefinitions6.Hir0x1f2Reg
  inline def Hir0x1f3RegDefinition: Definition = InstructionDefinitions6.Hir0x1f3Reg
}
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Instruction(
    val definition: Definition,
    val modifiers: Vec[Modifier],
    val guard: Option[Guard],
    operandValues: Operand*
) {
  inline def mnemonic: NonEmpty[String] = definition.mnemonic
  val operands: Vec[Operand] = operandValues.toVec
  case AcqbulkNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.AcqbulkNoOperandsDefinition, mods, pred)
  case AcqshminitNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.AcqshminitNoOperandsDefinition, mods, pred)
  case Al2pPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Al2pPredRegRegDefinition, mods, pred, dstPredicate, dst, srcA)
  case Al2pPredRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Al2pPredRegRegImmDefinition, mods, pred, dstPredicate, dst, srcA, sImm)
  case Al2pRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Al2pRegRegDefinition, mods, pred, dst, srcA)
  case Al2pRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Al2pRegRegImmDefinition, mods, pred, dst, srcA, ra_offset)
  case AldRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AldRegAddressRegDefinition, mods, pred, dst, srcA, srcB)
  case ArrivesAddress(srcA: InstructionOperand.Address, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.ArrivesAddressDefinition, mods, pred, srcA)
  case AstAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AstAddressRegRegDefinition, mods, pred, srcA, srcB, srcC)
  case AtomPredRegAddress(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AtomPredRegAddressDefinition, mods, pred, pu, dst, srcA)
  case AtomPredRegAddressReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AtomPredRegAddressRegDefinition, mods, pred, dstPredicate, dst, srcA, srcB)
  case AtomPredRegAddressRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AtomPredRegAddressRegRegDefinition, mods, pred, dstPredicate, dst, srcA, srcB, srcC)
  case AtomRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AtomRegAddressRegDefinition, mods, pred, dst, srcA, srcB)
  case AtomRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AtomRegAddressRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case AtomgPredRegAddressReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AtomgPredRegAddressRegDefinition, mods, pred, pu, dst, srcA, srcB)
  case AtomgPredRegAddressRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AtomgPredRegAddressRegRegDefinition, mods, pred, pu, dst, srcA, srcB, srcC)
  case AtomsPredAddressRegReg(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AtomsPredAddressRegRegDefinition, mods, pred, pu, srcA, srcB, srcC)
  case AtomsRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AtomsRegAddressDefinition, mods, pred, dst, srcA)
  case AtomsRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AtomsRegAddressRegDefinition, mods, pred, dst, srcA, srcB)
  case AtomsRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.AtomsRegAddressRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case B2rReg(dst: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.B2rRegDefinition, mods, pred, dst)
  case B2rRegImm(
      dst: InstructionOperand.Register,
      barNum: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.B2rRegImmDefinition, mods, pred, dst, barNum)
  case B2rRegPred(
      dst: InstructionOperand.Register,
      dstPredicate: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.B2rRegPredDefinition, mods, pred, dst, dstPredicate)
  case BarNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BarNoOperandsDefinition, mods, pred)
  case BarImm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BarImmDefinition, mods, pred, sb)
  case BarImmImm(
      sb: InstructionOperand.Immediate,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BarImmImmDefinition, mods, pred, sb, sc)
  case BarImmImmPred(
      sb: InstructionOperand.Immediate,
      sc: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BarImmImmPredDefinition, mods, pred, sb, sc, pp)
  case BarImmPred(
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BarImmPredDefinition, mods, pred, sb, pp)
  case BarImmReg(
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BarImmRegDefinition, mods, pred, sb, srcC)
  case BarImmRegPred(
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BarImmRegPredDefinition, mods, pred, sb, srcC, pp)
  case BarReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BarRegDefinition, mods, pred, srcA)
  case BarRegImm(
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BarRegImmDefinition, mods, pred, srcA, uImm)
  case BarRegImmPred(
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BarRegImmPredDefinition, mods, pred, srcA, uImm, pa)
  case BarRegPred(
      srcA: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BarRegPredDefinition, mods, pred, srcA, pa)
  case BarRegReg(
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BarRegRegDefinition, mods, pred, srcA, srcB)
  case BarRegRegPred(
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BarRegRegPredDefinition, mods, pred, srcA, srcB, pa)
  case BfeRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BfeRegRegConstDefinition, mods, pred, dst, srcA, srcConst)
  case BfeRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BfeRegRegImmDefinition, mods, pred, dst, srcA, uImm)
  case BfeRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BfeRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case BfiRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BfiRegRegConstRegDefinition, mods, pred, dst, srcA, srcConst, srcC)
  case BfiRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BfiRegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case BfiRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BfiRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, srcConst)
  case BfiRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BfiRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case BgmmaRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BgmmaRegAddressRegDefinition, mods, pred, dst, srcA, srcC)
  case BgmmaRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BgmmaRegAddressRegPredDefinition, mods, pred, dst, srcA, srcC, uPp)
  case BgmmaRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BgmmaRegAddressRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case BgmmaRegAddressRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BgmmaRegAddressRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case BgmmaRegRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BgmmaRegRegAddressRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case BgmmaRegRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BgmmaRegRegAddressRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case BmmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmmaRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case BmmaRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmmaRegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case BmovAddressImm(
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmovAddressImmDefinition, mods, pred, srcB, sb_offset)
  case BmovConst(sb: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BmovConstDefinition, mods, pred, sb)
  case BmovImm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BmovImmDefinition, mods, pred, sb)
  case BmovReg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BmovRegDefinition, mods, pred, srcB)
  case BmovRegAddressImm(
      cbu_state: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmovRegAddressImmDefinition, mods, pred, cbu_state, srcB, sb_offset)
  case BmovRegConst(
      cbu_state: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmovRegConstDefinition, mods, pred, cbu_state, sb)
  case BmovRegImm(
      cbu_state: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmovRegImmDefinition, mods, pred, cbu_state, sb)
  case BmovRegReg(
      dst: InstructionOperand.Register,
      cbu_state: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmovRegRegDefinition, mods, pred, dst, cbu_state)
  case BmovRegSymbol(
      dst: InstructionOperand.Register,
      cbu_state: InstructionOperand.Symbol,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmovRegSymbolDefinition, mods, pred, dst, cbu_state)
  case BmovSymbolAddressImm(
      cbu_state: InstructionOperand.Symbol,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmovSymbolAddressImmDefinition, mods, pred, cbu_state, srcB, sb_offset)
  case BmovSymbolConst(
      cbu_state: InstructionOperand.Symbol,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmovSymbolConstDefinition, mods, pred, cbu_state, sb)
  case BmovSymbolImm(
      cbu_state: InstructionOperand.Symbol,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmovSymbolImmDefinition, mods, pred, cbu_state, sb)
  case BmovSymbolReg(
      cbu_state: InstructionOperand.Symbol,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmovSymbolRegDefinition, mods, pred, cbu_state, srcB)
  case BmskRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmskRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case BmskRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmskRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case BmskRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmskRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case BmskRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BmskRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case BptNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BptNoOperandsDefinition, mods, pred)
  case BptImm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BptImmDefinition, mods, pred, sb)
  case BraConst(srcConst: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BraConstDefinition, mods, pred, srcConst)
  case BraImm(sImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BraImmDefinition, mods, pred, sImm)
  case BraPredImm(
      pp: InstructionOperand.Predicate,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BraPredImmDefinition, mods, pred, pp, sImm)
  case BraPredPredImm(
      pp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BraPredPredImmDefinition, mods, pred, pp, uPq, sImm)
  case BraPredRegImm(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BraPredRegImmDefinition, mods, pred, pp, srcB, sImm)
  case BraRegImm(
      srcB: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BraRegImmDefinition, mods, pred, srcB, sImm)
  case BraSymbolConst(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BraSymbolConstDefinition, mods, pred, testCC, srcConst)
  case BraSymbolImm(
      testCC: InstructionOperand.Symbol,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BraSymbolImmDefinition, mods, pred, testCC, sImm)
  case BraSymbolImmImm(
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      immConstOffset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BraSymbolImmImmDefinition, mods, pred, srcConst, constBank, immConstOffset)
  case BraSymbolSymbolImmImm(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      immConstOffset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.BraSymbolSymbolImmImmDefinition,
        mods,
        pred,
        testCC,
        srcConst,
        constBank,
        immConstOffset
      )
  case BreakPredReg(
      pp: InstructionOperand.Predicate,
      barReg: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BreakPredRegDefinition, mods, pred, pp, barReg)
  case BreakReg(barReg: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BreakRegDefinition, mods, pred, barReg)
  case BrevRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrevRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case BrevRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrevRegConstDefinition, mods, pred, dst, sb)
  case BrevRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrevRegImmDefinition, mods, pred, dst, sb)
  case BrevRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrevRegRegDefinition, mods, pred, dst, srcB)
  case BrkNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BrkNoOperandsDefinition, mods, pred)
  case BrxConst(srcConst: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BrxConstDefinition, mods, pred, srcConst)
  case BrxPredReg(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrxPredRegDefinition, mods, pred, pp, srcA)
  case BrxPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrxPredRegImmDefinition, mods, pred, pp, srcA, ra_offset)
  case BrxReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BrxRegDefinition, mods, pred, srcA)
  case BrxRegImm(
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrxRegImmDefinition, mods, pred, srcA, ra_offset)
  case BrxSymbolConst(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrxSymbolConstDefinition, mods, pred, testCC, srcConst)
  case BrxSymbolImmAddress(
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrxSymbolImmAddressDefinition, mods, pred, srcConst, constBank, srcA)
  case BrxSymbolReg(
      testCC: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrxSymbolRegDefinition, mods, pred, testCC, srcA)
  case BrxSymbolRegImm(
      testCC: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrxSymbolRegImmDefinition, mods, pred, testCC, srcA, sImm)
  case BrxSymbolSymbolImmAddress(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrxSymbolSymbolImmAddressDefinition, mods, pred, testCC, srcConst, constBank, srcA)
  case BrxuPredReg(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrxuPredRegDefinition, mods, pred, pp, srcA)
  case BrxuPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uR_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrxuPredRegImmDefinition, mods, pred, pp, srcA, uR_offset)
  case BrxuReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BrxuRegDefinition, mods, pred, srcA)
  case BrxuRegImm(
      srcA: InstructionOperand.Register,
      uR_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BrxuRegImmDefinition, mods, pred, srcA, uR_offset)
  case BssyPredRegImm(
      pp: InstructionOperand.Predicate,
      barReg: InstructionOperand.Register,
      sa: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BssyPredRegImmDefinition, mods, pred, pp, barReg, sa)
  case BssyRegImm(
      barReg: InstructionOperand.Register,
      sa: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BssyRegImmDefinition, mods, pred, barReg, sa)
  case BsyncPredReg(
      pp: InstructionOperand.Predicate,
      barReg: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.BsyncPredRegDefinition, mods, pred, pp, barReg)
  case BsyncReg(barReg: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.BsyncRegDefinition, mods, pred, barReg)
  case CalConst(srcConst: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CalConstDefinition, mods, pred, srcConst)
  case CalImm(sImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CalImmDefinition, mods, pred, sImm)
  case CallConst(sa: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CallConstDefinition, mods, pred, sa)
  case CallImm(sa: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CallImmDefinition, mods, pred, sa)
  case CallPredConst(
      pp: InstructionOperand.Predicate,
      sa: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CallPredConstDefinition, mods, pred, pp, sa)
  case CallPredImm(
      pp: InstructionOperand.Predicate,
      sa: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CallPredImmDefinition, mods, pred, pp, sa)
  case CallPredReg(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CallPredRegDefinition, mods, pred, pp, srcA)
  case CallPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CallPredRegImmDefinition, mods, pred, pp, srcA, ra_offset)
  case CallReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CallRegDefinition, mods, pred, srcA)
  case CallRegImm(
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CallRegImmDefinition, mods, pred, srcA, ra_offset)
  case CctlNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CctlNoOperandsDefinition, mods, pred)
  case CctlAddress(srcA: InstructionOperand.Address, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CctlAddressDefinition, mods, pred, srcA)
  case CctlAddressAddress(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CctlAddressAddressDefinition, mods, pred, srcA, srcB)
  case CctlAddressImm(
      srcA: InstructionOperand.Address,
      sector_count: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CctlAddressImmDefinition, mods, pred, srcA, sector_count)
  case CctlConst(sa: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CctlConstDefinition, mods, pred, sa)
  case CctlPredAddress(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CctlPredAddressDefinition, mods, pred, pu, srcA)
  case CctlRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CctlRegAddressDefinition, mods, pred, dst, srcA)
  case CctllNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CctllNoOperandsDefinition, mods, pred)
  case CctllAddress(srcA: InstructionOperand.Address, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CctllAddressDefinition, mods, pred, srcA)
  case CctllRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CctllRegAddressDefinition, mods, pred, dst, srcA)
  case CctlsNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CctlsNoOperandsDefinition, mods, pred)
  case CctltNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CctltNoOperandsDefinition, mods, pred)
  case CctltImm(uImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CctltImmDefinition, mods, pred, uImm)
  case CctltImmImm(
      sb_bank: InstructionOperand.Immediate,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CctltImmImmDefinition, mods, pred, sb_bank, sb_offset)
  case CctltReg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CctltRegDefinition, mods, pred, srcB)
  case CgaerrbarNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CgaerrbarNoOperandsDefinition, mods, pred)
  case ClmadRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ClmadRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case ClmadRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ClmadRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case ClmadRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ClmadRegRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, srcC, sc_offset)
  case ClmadRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ClmadRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, sc)
  case ClmadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ClmadRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case ContNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.ContNoOperandsDefinition, mods, pred)
  case CreduxRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CreduxRegRegDefinition, mods, pred, dst, srcA)
  case Cs2rRegSpecial(
      dst: InstructionOperand.Register,
      sR: InstructionOperand.SpecialRegister,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Cs2rRegSpecialDefinition, mods, pred, dst, sR)
  case Cs2urRegSpecial(
      dst: InstructionOperand.Register,
      sRa: InstructionOperand.SpecialRegister,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Cs2urRegSpecialDefinition, mods, pred, dst, sRa)
  case CsetRegSymbol(
      dst: InstructionOperand.Register,
      dummyCC: InstructionOperand.Symbol,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CsetRegSymbolDefinition, mods, pred, dst, dummyCC)
  case CsetRegSymbolPred(
      dst: InstructionOperand.Register,
      dummyCC: InstructionOperand.Symbol,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CsetRegSymbolPredDefinition, mods, pred, dst, dummyCC, pa)
  case CsetpPredPredSymbolPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      inputCC: InstructionOperand.Symbol,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CsetpPredPredSymbolPredDefinition, mods, pred, dstPredicate, nPd, inputCC, pa)
  case CsetpPredSymbol(
      dstPredicate: InstructionOperand.Predicate,
      inputCC: InstructionOperand.Symbol,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CsetpPredSymbolDefinition, mods, pred, dstPredicate, inputCC)
  case CsmtestImm(sa: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.CsmtestImmDefinition, mods, pred, sa)
  case CsmtestPredImm(
      pu: InstructionOperand.Predicate,
      sa: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CsmtestPredImmDefinition, mods, pred, pu, sa)
  case CsmtestPredPredImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      sa: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.CsmtestPredPredImmPredDefinition, mods, pred, pu, pv, sa, pp)
  case DaddRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DaddRegRegAddressImmDefinition, mods, pred, dst, srcA, srcC, sc_offset)
  case DaddRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DaddRegRegConstDefinition, mods, pred, dst, srcA, sc)
  case DaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DaddRegRegImmDefinition, mods, pred, dst, srcA, sc)
  case DaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DaddRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case DchkPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DchkPredRegConstDefinition, mods, pred, dstPredicate, srcA, srcConst)
  case DchkPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DchkPredRegImmDefinition, mods, pred, dstPredicate, srcA, uImm)
  case DchkPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DchkPredRegRegDefinition, mods, pred, dstPredicate, srcA, srcB)
  case DepbarNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.DepbarNoOperandsDefinition, mods, pred)
  case DepbarGroup(dep_scbd: InstructionOperand.Group, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.DepbarGroupDefinition, mods, pred, dep_scbd)
  case DepbarImm(dep_scbd: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.DepbarImmDefinition, mods, pred, dep_scbd)
  case DepbarRegImm(
      sbidx: InstructionOperand.Register,
      cnt: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DepbarRegImmDefinition, mods, pred, sbidx, cnt)
  case DepbarRegImmGroup(
      sbidx: InstructionOperand.Register,
      cnt: InstructionOperand.Immediate,
      dep_scbd: InstructionOperand.Group,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DepbarRegImmGroupDefinition, mods, pred, sbidx, cnt, dep_scbd)
  case DepbarRegImmImm(
      sbidx: InstructionOperand.Register,
      cnt: InstructionOperand.Immediate,
      dep_scbd: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DepbarRegImmImmDefinition, mods, pred, sbidx, cnt, dep_scbd)
  case DepbarRegReg(
      sbidx: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DepbarRegRegDefinition, mods, pred, sbidx, srcB)
  case DepbarRegRegGroup(
      sbidx: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scoreboard_list: InstructionOperand.Group,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DepbarRegRegGroupDefinition, mods, pred, sbidx, srcB, scoreboard_list)
  case DepbarRegRegImm(
      sbidx: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scoreboard_list: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DepbarRegRegImmDefinition, mods, pred, sbidx, srcB, scoreboard_list)
  case DfmaRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DfmaRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case DfmaRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DfmaRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case DfmaRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DfmaRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case DfmaRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DfmaRegRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, srcC, sc_offset)
  case DfmaRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DfmaRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, sc)
  case DfmaRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DfmaRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case DfmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DfmaRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case DmmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DmmaRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case DmmaRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DmmaRegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case DmnmxRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DmnmxRegRegConstPredDefinition, mods, pred, dst, srcA, srcConst, pa)
  case DmnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DmnmxRegRegImmPredDefinition, mods, pred, dst, srcA, uImm, pa)
  case DmnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DmnmxRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pa)
  case DmulRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DmulRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case DmulRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DmulRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case DmulRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DmulRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case DmulRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DmulRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case DsetRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetRegRegConstDefinition, mods, pred, dst, srcA, srcConst)
  case DsetRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetRegRegConstPredDefinition, mods, pred, dst, srcA, srcConst, pa)
  case DsetRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetRegRegImmDefinition, mods, pred, dst, srcA, uImm)
  case DsetRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetRegRegImmPredDefinition, mods, pred, dst, srcA, uImm, pa)
  case DsetRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case DsetRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pa)
  case DsetpPredPredRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.DsetpPredPredRegAddressImmPredDefinition,
        mods,
        pred,
        pu,
        pv,
        srcA,
        srcC,
        sc_offset,
        pp
      )
  case DsetpPredPredRegConstPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.DsetpPredPredRegConstPredDefinition,
        mods,
        pred,
        dstPredicate,
        nPd,
        srcA,
        srcConst,
        pa
      )
  case DsetpPredPredRegImmPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetpPredPredRegImmPredDefinition, mods, pred, dstPredicate, nPd, srcA, uImm, pa)
  case DsetpPredPredRegRegPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetpPredPredRegRegPredDefinition, mods, pred, dstPredicate, nPd, srcA, srcB, pa)
  case DsetpPredRegAddressImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetpPredRegAddressImmDefinition, mods, pred, pu, srcA, srcC, sc_offset)
  case DsetpPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetpPredRegConstDefinition, mods, pred, dstPredicate, srcA, srcConst)
  case DsetpPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetpPredRegImmDefinition, mods, pred, dstPredicate, srcA, uImm)
  case DsetpPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.DsetpPredRegRegDefinition, mods, pred, dstPredicate, srcA, srcB)
  case ElectPredReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ElectPredRegDefinition, mods, pred, pu, dst)
  case ElectPredRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ElectPredRegPredDefinition, mods, pred, pu, dst, pp)
  case ElectPredRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ElectPredRegRegDefinition, mods, pred, pu, dst, srcA)
  case EndcollectiveNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.EndcollectiveNoOperandsDefinition, mods, pred)
  case EndcollectivePred(pp: InstructionOperand.Predicate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.EndcollectivePredDefinition, mods, pred, pp)
  case ErrbarNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.ErrbarNoOperandsDefinition, mods, pred)
  case ExitNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.ExitNoOperandsDefinition, mods, pred)
  case ExitPred(pp: InstructionOperand.Predicate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.ExitPredDefinition, mods, pred, pp)
  case F2fRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case F2fRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fRegConstDefinition, mods, pred, dst, sb)
  case F2fRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fRegImmDefinition, mods, pred, dst, sb)
  case F2fRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fRegRegDefinition, mods, pred, dst, srcB)
  case F2fpRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case F2fpRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegAddressImmRegDefinition, mods, pred, dst, srcB, sb_offset, srcC)
  case F2fpRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegConstDefinition, mods, pred, dst, sb)
  case F2fpRegConstReg(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegConstRegDefinition, mods, pred, dst, sb, srcC)
  case F2fpRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegImmDefinition, mods, pred, dst, sb)
  case F2fpRegImmReg(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegImmRegDefinition, mods, pred, dst, sb, srcC)
  case F2fpRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegDefinition, mods, pred, dst, srcB)
  case F2fpRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case F2fpRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case F2fpRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case F2fpRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case F2fpRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case F2fpRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case F2fpRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case F2fpRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, srcC, sc_offset)
  case F2fpRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, sc)
  case F2fpRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case F2fpRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2fpRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case F2iRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2iRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case F2iRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2iRegConstDefinition, mods, pred, dst, sb)
  case F2iRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2iRegImmDefinition, mods, pred, dst, sb)
  case F2iRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2iRegRegDefinition, mods, pred, dst, srcB)
  case F2ipRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2ipRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case F2ipRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2ipRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case F2ipRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2ipRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case F2ipRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2ipRegRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, srcC, sc_offset)
  case F2ipRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2ipRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, sc)
  case F2ipRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2ipRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case F2ipRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.F2ipRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case FaddRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FaddRegRegAddressImmDefinition, mods, pred, dst, srcA, srcC, sc_offset)
  case FaddRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FaddRegRegConstDefinition, mods, pred, dst, srcA, sc)
  case FaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FaddRegRegImmDefinition, mods, pred, dst, srcA, sc)
  case FaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FaddRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case Fadd2RegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Fadd2RegRegImmDefinition, mods, pred, dst, srcA, sc)
  case Fadd2RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Fadd2RegRegRegDefinition, mods, pred, dst, srcA, srcC)
  case Fadd32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Fadd32iRegRegImmDefinition, mods, pred, dst, srcA, sc)
  case FccoPredPredRegConstReg(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.FccoPredPredRegConstRegDefinition,
        mods,
        pred,
        dstPredicate,
        nPd,
        srcA,
        srcConst,
        srcC
      )
  case FccoPredPredRegImmReg(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FccoPredPredRegImmRegDefinition, mods, pred, dstPredicate, nPd, srcA, uImm, srcC)
  case FccoPredPredRegRegConst(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.FccoPredPredRegRegConstDefinition,
        mods,
        pred,
        dstPredicate,
        nPd,
        srcA,
        srcB,
        srcConst
      )
  case FccoPredPredRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FccoPredPredRegRegRegDefinition, mods, pred, dstPredicate, nPd, srcA, srcB, srcC)
  case FccoPredRegConstReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FccoPredRegConstRegDefinition, mods, pred, dstPredicate, srcA, srcConst, srcC)
  case FccoPredRegImmReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FccoPredRegImmRegDefinition, mods, pred, dstPredicate, srcA, uImm, srcC)
  case FccoPredRegRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FccoPredRegRegConstDefinition, mods, pred, dstPredicate, srcA, srcB, srcConst)
  case FccoPredRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FccoPredRegRegRegDefinition, mods, pred, dstPredicate, srcA, srcB, srcC)
  case FchkPredRegAddressImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FchkPredRegAddressImmDefinition, mods, pred, pu, srcA, srcB, sb_offset)
  case FchkPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FchkPredRegConstDefinition, mods, pred, dstPredicate, srcA, srcConst)
  case FchkPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FchkPredRegImmDefinition, mods, pred, dstPredicate, srcA, uImm)
  case FchkPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FchkPredRegRegDefinition, mods, pred, dstPredicate, srcA, srcB)
  case FcmpRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FcmpRegRegConstRegDefinition, mods, pred, dst, srcA, srcConst, srcC)
  case FcmpRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FcmpRegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case FcmpRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FcmpRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, srcConst)
  case FcmpRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FcmpRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case FenceNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.FenceNoOperandsDefinition, mods, pred)
  case FfmaRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FfmaRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case FfmaRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FfmaRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case FfmaRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FfmaRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case FfmaRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FfmaRegRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, srcC, sc_offset)
  case FfmaRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FfmaRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, sc)
  case FfmaRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FfmaRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case FfmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FfmaRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Ffma2RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ffma2RegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Ffma2RegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ffma2RegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case Ffma2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ffma2RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Ffma32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      fImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ffma32iRegRegImmDefinition, mods, pred, dst, srcA, fImm)
  case Ffma32iRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ffma32iRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case FhaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FhaddRegRegRegDefinition, mods, pred, dst, srcA, srcC)
  case FhfmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FhfmaRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case FloRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FloRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case FloRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FloRegConstDefinition, mods, pred, dst, sb)
  case FloRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FloRegImmDefinition, mods, pred, dst, sb)
  case FloRegPredAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FloRegPredAddressImmDefinition, mods, pred, dst, pu, srcB, sb_offset)
  case FloRegPredConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FloRegPredConstDefinition, mods, pred, dst, pu, sb)
  case FloRegPredImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FloRegPredImmDefinition, mods, pred, dst, pu, sb)
  case FloRegPredReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FloRegPredRegDefinition, mods, pred, dst, pu, srcB)
  case FloRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FloRegRegDefinition, mods, pred, dst, srcB)
  case FmnmxRegPredRegAddressImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.FmnmxRegPredRegAddressImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        pp
      )
  case FmnmxRegPredRegConstPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FmnmxRegPredRegConstPredDefinition, mods, pred, dst, pu, srcA, sb, pp)
  case FmnmxRegPredRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FmnmxRegPredRegImmPredDefinition, mods, pred, dst, pu, srcA, sb, pp)
  case FmnmxRegPredRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FmnmxRegPredRegRegPredDefinition, mods, pred, dst, pu, srcA, srcB, pp)
  case FmnmxRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FmnmxRegRegAddressImmPredDefinition, mods, pred, dst, srcA, srcB, sb_offset, pp)
  case FmnmxRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FmnmxRegRegConstPredDefinition, mods, pred, dst, srcA, sb, pp)
  case FmnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FmnmxRegRegImmPredDefinition, mods, pred, dst, srcA, sb, pp)
  case FmnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FmnmxRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pa)
  case Fmnmx3RegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Fmnmx3RegRegImmRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case Fmnmx3RegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Fmnmx3RegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, pp)
  case FmulRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FmulRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case FmulRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FmulRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case FmulRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FmulRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case FmulRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FmulRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case Fmul2RegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Fmul2RegRegImmDefinition, mods, pred, dst, srcA, sb)
  case Fmul2RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Fmul2RegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case Fmul32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Fmul32iRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case FootprintPredRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FootprintPredRegRegRegRegDefinition, mods, pred, pu, rd2, dst, srcA, srcB)
  case FootprintPredRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.FootprintPredRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        tsPtrIdx
      )
  case FootprintPredRegRegRegRegImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.FootprintPredRegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case FootprintPredRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FootprintPredRegRegRegRegRegDefinition, mods, pred, pu, rd2, dst, srcA, srcB, srcC)
  case FootprintPredRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.FootprintPredRegRegRegRegRegImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8
      )
  case FootprintRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FootprintRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB)
  case FootprintRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.FootprintRegRegRegRegImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        tsPtrIdx
      )
  case FootprintRegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.FootprintRegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case FootprintRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FootprintRegRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB, srcC)
  case FootprintRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FootprintRegRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, srcC, tid8)
  case FrndRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FrndRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case FrndRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FrndRegConstDefinition, mods, pred, dst, sb)
  case FrndRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FrndRegImmDefinition, mods, pred, dst, sb)
  case FrndRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FrndRegRegDefinition, mods, pred, dst, srcB)
  case FselRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FselRegRegAddressImmPredDefinition, mods, pred, dst, srcA, srcB, sb_offset, pp)
  case FselRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FselRegRegConstPredDefinition, mods, pred, dst, srcA, sb, pp)
  case FselRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FselRegRegImmPredDefinition, mods, pred, dst, srcA, sb, pp)
  case FselRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FselRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pp)
  case FsetRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case FsetRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetRegRegAddressImmPredDefinition, mods, pred, dst, srcA, srcB, sb_offset, pp)
  case FsetRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case FsetRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetRegRegConstPredDefinition, mods, pred, dst, srcA, sb, pp)
  case FsetRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case FsetRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetRegRegImmPredDefinition, mods, pred, dst, srcA, sb, pp)
  case FsetRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case FsetRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pa)
  case FsetpPredPredRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.FsetpPredPredRegAddressImmPredDefinition,
        mods,
        pred,
        pu,
        pv,
        srcA,
        srcB,
        sb_offset,
        pp
      )
  case FsetpPredPredRegConstPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.FsetpPredPredRegConstPredDefinition,
        mods,
        pred,
        dstPredicate,
        nPd,
        srcA,
        srcConst,
        pa
      )
  case FsetpPredPredRegImmPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetpPredPredRegImmPredDefinition, mods, pred, dstPredicate, nPd, srcA, uImm, pa)
  case FsetpPredPredRegRegPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetpPredPredRegRegPredDefinition, mods, pred, dstPredicate, nPd, srcA, srcB, pa)
  case FsetpPredRegAddressImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetpPredRegAddressImmDefinition, mods, pred, pu, srcA, srcB, sb_offset)
  case FsetpPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetpPredRegConstDefinition, mods, pred, dstPredicate, srcA, srcConst)
  case FsetpPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetpPredRegImmDefinition, mods, pred, dstPredicate, srcA, uImm)
  case FsetpPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FsetpPredRegRegDefinition, mods, pred, dstPredicate, srcA, srcB)
  case FswzRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FswzRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case FswzaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.FswzaddRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case GatherRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mdidx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.GatherRegRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, mdidx)
  case GatherRegRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mdidx: InstructionOperand.Immediate,
      dstbyte: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.GatherRegRegRegRegImmImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        mdidx,
        dstbyte
      )
  case GatherRegRegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mdidx: InstructionOperand.Immediate,
      dstbyte: InstructionOperand.Immediate,
      srchalf: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.GatherRegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        mdidx,
        dstbyte,
        srchalf
      )
  case GetcrsptrReg(dst: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.GetcrsptrRegDefinition, mods, pred, dst)
  case GetlmembaseReg(dst: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.GetlmembaseRegDefinition, mods, pred, dst)
  case Hadd2NoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hadd2NoOperandsDefinition, mods, pred)
  case Hadd2Const(sb: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hadd2ConstDefinition, mods, pred, sb)
  case Hadd2ImmImm(
      sb: InstructionOperand.Immediate,
      sb2: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hadd2ImmImmDefinition, mods, pred, sb, sb2)
  case Hadd2RegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hadd2RegRegAddressImmDefinition, mods, pred, dst, srcA, srcC, sc_offset)
  case Hadd2RegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hadd2RegRegConstDefinition, mods, pred, dst, srcA, sc)
  case Hadd2RegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hadd2RegRegImmDefinition, mods, pred, dst, srcA, sc)
  case Hadd2RegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hadd2RegRegImmImmDefinition, mods, pred, dst, srcA, sb, sc)
  case Hadd2RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hadd2RegRegRegDefinition, mods, pred, dst, srcA, srcC)
  case Hadd232iImmImm(
      imm: InstructionOperand.Immediate,
      imm2: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hadd232iImmImmDefinition, mods, pred, imm, imm2)
  case Hadd232iRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      sc1: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hadd232iRegRegImmImmDefinition, mods, pred, dst, srcA, sc, sc1)
  case Hfma2NoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hfma2NoOperandsDefinition, mods, pred)
  case Hfma2Const(sb: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hfma2ConstDefinition, mods, pred, sb)
  case Hfma2ImmImm(
      sb: InstructionOperand.Immediate,
      sb2: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2ImmImmDefinition, mods, pred, sb, sb2)
  case Hfma2RegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case Hfma2RegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hfma2RegRegAddressImmRegPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        pp
      )
  case Hfma2RegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Hfma2RegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegConstRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case Hfma2RegRegImmImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegImmImmRegDefinition, mods, pred, dst, srcA, sb, sb1, srcC)
  case Hfma2RegRegImmImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegImmImmRegPredDefinition, mods, pred, dst, srcA, sb, sb1, srcC, pp)
  case Hfma2RegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, srcC, sc_offset)
  case Hfma2RegRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hfma2RegRegRegAddressImmPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        sc_offset,
        pp
      )
  case Hfma2RegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegRegConstDefinition, mods, pred, dst, srcA, srcB, sc)
  case Hfma2RegRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegRegConstPredDefinition, mods, pred, dst, srcA, srcB, sc, pp)
  case Hfma2RegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      sc1: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegRegImmImmDefinition, mods, pred, dst, srcA, srcB, sc, sc1)
  case Hfma2RegRegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      sc1: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegRegImmImmPredDefinition, mods, pred, dst, srcA, srcB, sc, sc1, pp)
  case Hfma2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Hfma2RegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma2RegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, pp)
  case Hfma232iRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      fImm: InstructionOperand.Immediate,
      fImm1: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma232iRegRegImmImmDefinition, mods, pred, dst, srcA, fImm, fImm1)
  case Hfma232iRegRegImmImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hfma232iRegRegImmImmRegDefinition, mods, pred, dst, srcA, sb, sb1, srcC)
  case HgmmaRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HgmmaRegAddressRegDefinition, mods, pred, dst, srcA, srcC)
  case HgmmaRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HgmmaRegAddressRegPredDefinition, mods, pred, dst, srcA, srcC, uPp)
  case HgmmaRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HgmmaRegAddressRegPredRegImmDefinition, mods, pred, dst, srcA, srcC, uPp, re, id)
  case HgmmaRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HgmmaRegAddressRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case HgmmaRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HgmmaRegAddressRegRegImmDefinition, mods, pred, dst, srcA, srcC, re, id)
  case HgmmaRegAddressRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HgmmaRegAddressRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case HgmmaRegAddressRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.HgmmaRegAddressRegRegPredRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        id
      )
  case HgmmaRegAddressRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HgmmaRegAddressRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, re, id)
  case HgmmaRegRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HgmmaRegRegAddressRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case HgmmaRegRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HgmmaRegRegAddressRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case HgmmaRegRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.HgmmaRegRegAddressRegPredRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        id
      )
  case HgmmaRegRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HgmmaRegRegAddressRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, re, id)
  case HmmaAddressRegRegAddress(
      dst: InstructionOperand.Address,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HmmaAddressRegRegAddressDefinition, mods, pred, dst, srcA, srcB, srcC)
  case HmmaAddressRegRegAddressPred(
      dst: InstructionOperand.Address,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HmmaAddressRegRegAddressPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case HmmaAddressRegRegAddressPredRegImm(
      dst: InstructionOperand.Address,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.HmmaAddressRegRegAddressPredRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        id
      )
  case HmmaAddressRegRegAddressRegImm(
      dst: InstructionOperand.Address,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HmmaAddressRegRegAddressRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, re, id)
  case HmmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HmmaRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case HmmaRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HmmaRegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case HmmaRegRegRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.HmmaRegRegRegRegPredRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        id
      )
  case HmmaRegRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.HmmaRegRegRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, re, id)
  case Hmnmx2RegPredPredRegAddressImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hmnmx2RegPredPredRegAddressImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        pv,
        srcA,
        srcB,
        sb_offset,
        pp
      )
  case Hmnmx2RegPredPredRegConstPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmnmx2RegPredPredRegConstPredDefinition, mods, pred, dst, pu, pv, srcA, sb, pp)
  case Hmnmx2RegPredPredRegImmImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hmnmx2RegPredPredRegImmImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        pv,
        srcA,
        sb,
        sb1,
        pp
      )
  case Hmnmx2RegPredPredRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmnmx2RegPredPredRegRegPredDefinition, mods, pred, dst, pu, pv, srcA, srcB, pp)
  case Hmnmx2RegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmnmx2RegRegAddressImmPredDefinition, mods, pred, dst, srcA, srcB, sb_offset, pp)
  case Hmnmx2RegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmnmx2RegRegConstPredDefinition, mods, pred, dst, srcA, sb, pp)
  case Hmnmx2RegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmnmx2RegRegImmImmPredDefinition, mods, pred, dst, srcA, sb, sb1, pp)
  case Hmnmx2RegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmnmx2RegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pp)
  case Hmul2NoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hmul2NoOperandsDefinition, mods, pred)
  case Hmul2Const(sb: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hmul2ConstDefinition, mods, pred, sb)
  case Hmul2ImmImm(
      sb: InstructionOperand.Immediate,
      sb2: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmul2ImmImmDefinition, mods, pred, sb, sb2)
  case Hmul2RegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmul2RegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case Hmul2RegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmul2RegRegConstDefinition, mods, pred, dst, srcA, sb)
  case Hmul2RegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmul2RegRegImmImmDefinition, mods, pred, dst, srcA, sb, sb1)
  case Hmul2RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmul2RegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case Hmul232iImmImm(
      imm: InstructionOperand.Immediate,
      imm2: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmul232iImmImmDefinition, mods, pred, imm, imm2)
  case Hmul232iRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hmul232iRegRegImmImmDefinition, mods, pred, dst, srcA, sb, sb1)
  case Hset2NoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hset2NoOperandsDefinition, mods, pred)
  case Hset2Const(sb: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hset2ConstDefinition, mods, pred, sb)
  case Hset2ConstPred(
      sb: InstructionOperand.ConstantBank,
      ps: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hset2ConstPredDefinition, mods, pred, sb, ps)
  case Hset2ImmImm(
      sb: InstructionOperand.Immediate,
      sb2: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hset2ImmImmDefinition, mods, pred, sb, sb2)
  case Hset2ImmImmPred(
      sb: InstructionOperand.Immediate,
      sb2: InstructionOperand.Immediate,
      ps: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hset2ImmImmPredDefinition, mods, pred, sb, sb2, ps)
  case Hset2Pred(ps: InstructionOperand.Predicate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hset2PredDefinition, mods, pred, ps)
  case Hset2RegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hset2RegRegAddressImmDefinition, mods, pred, dst, srcA, srcC, sc_offset)
  case Hset2RegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hset2RegRegAddressImmPredDefinition, mods, pred, dst, srcA, srcC, sc_offset, pp)
  case Hset2RegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hset2RegRegConstDefinition, mods, pred, dst, srcA, sc)
  case Hset2RegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hset2RegRegConstPredDefinition, mods, pred, dst, srcA, sc, pp)
  case Hset2RegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      sc1: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hset2RegRegImmImmDefinition, mods, pred, dst, srcA, sc, sc1)
  case Hset2RegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      sc1: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hset2RegRegImmImmPredDefinition, mods, pred, dst, srcA, sc, sc1, pp)
  case Hset2RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hset2RegRegRegDefinition, mods, pred, dst, srcA, srcC)
  case Hset2RegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hset2RegRegRegPredDefinition, mods, pred, dst, srcA, srcC, pp)
  case Hsetp2PredPredRegAddressImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hsetp2PredPredRegAddressImmDefinition, mods, pred, pu, pv, srcA, srcC, sc_offset)
  case Hsetp2PredPredRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hsetp2PredPredRegAddressImmPredDefinition,
        mods,
        pred,
        pu,
        pv,
        srcA,
        srcC,
        sc_offset,
        pp
      )
  case Hsetp2PredPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hsetp2PredPredRegConstDefinition, mods, pred, dstPredicate, nPd, srcA, srcConst)
  case Hsetp2PredPredRegConstPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hsetp2PredPredRegConstPredDefinition,
        mods,
        pred,
        dstPredicate,
        nPd,
        srcA,
        srcConst,
        pa
      )
  case Hsetp2PredPredRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      uImmH0: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hsetp2PredPredRegImmImmDefinition,
        mods,
        pred,
        dstPredicate,
        nPd,
        srcA,
        uImm,
        uImmH0
      )
  case Hsetp2PredPredRegImmImmPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      uImmH0: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hsetp2PredPredRegImmImmPredDefinition,
        mods,
        pred,
        dstPredicate,
        nPd,
        srcA,
        uImm,
        uImmH0,
        pa
      )
  case Hsetp2PredPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hsetp2PredPredRegRegDefinition, mods, pred, dstPredicate, nPd, srcA, srcB)
  case Hsetp2PredPredRegRegPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hsetp2PredPredRegRegPredDefinition, mods, pred, dstPredicate, nPd, srcA, srcB, pa)
  case I2fRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2fRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case I2fRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2fRegConstDefinition, mods, pred, dst, sb)
  case I2fRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2fRegImmDefinition, mods, pred, dst, sb)
  case I2fRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2fRegRegDefinition, mods, pred, dst, srcB)
  case I2fpRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2fpRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case I2fpRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2fpRegConstDefinition, mods, pred, dst, sb)
  case I2fpRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2fpRegImmDefinition, mods, pred, dst, sb)
  case I2fpRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2fpRegRegDefinition, mods, pred, dst, srcB)
  case I2iRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2iRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case I2iRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2iRegConstDefinition, mods, pred, dst, sb)
  case I2iRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2iRegImmDefinition, mods, pred, dst, sb)
  case I2iRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2iRegRegDefinition, mods, pred, dst, srcB)
  case I2ipRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2ipRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case I2ipRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2ipRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case I2ipRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2ipRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case I2ipRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.I2ipRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case IabsRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IabsRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case IabsRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IabsRegConstDefinition, mods, pred, dst, sb)
  case IabsRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IabsRegImmDefinition, mods, pred, dst, sb)
  case IabsRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IabsRegRegDefinition, mods, pred, dst, srcB)
  case IaddRegPredRegAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegPredRegAddressImmDefinition, mods, pred, dst, pu, srcA, srcB, sb_offset)
  case IaddRegPredRegAddressImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.IaddRegPredRegAddressImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        pp
      )
  case IaddRegPredRegConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegPredRegConstDefinition, mods, pred, dst, pu, srcA, sb)
  case IaddRegPredRegConstPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegPredRegConstPredDefinition, mods, pred, dst, pu, srcA, sb, pp)
  case IaddRegPredRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegPredRegImmDefinition, mods, pred, dst, pu, srcA, sb)
  case IaddRegPredRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegPredRegImmPredDefinition, mods, pred, dst, pu, srcA, sb, pp)
  case IaddRegPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegPredRegImmRegDefinition, mods, pred, dst, pu, srcA, sb, srcC)
  case IaddRegPredRegImmRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegPredRegImmRegPredDefinition, mods, pred, dst, pu, srcA, sb, srcC, pp)
  case IaddRegPredRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegPredRegRegDefinition, mods, pred, dst, pu, srcA, srcB)
  case IaddRegPredRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegPredRegRegPredDefinition, mods, pred, dst, pu, srcA, srcB, pp)
  case IaddRegPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegPredRegRegRegDefinition, mods, pred, dst, pu, srcA, srcB, srcC)
  case IaddRegPredRegRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegPredRegRegRegPredDefinition, mods, pred, dst, pu, srcA, srcB, srcC, pp)
  case IaddRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case IaddRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegAddressImmPredDefinition, mods, pred, dst, srcA, srcB, sb_offset, pp)
  case IaddRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case IaddRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegConstPredDefinition, mods, pred, dst, srcA, sb, pp)
  case IaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case IaddRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegImmPredDefinition, mods, pred, dst, srcA, sb, pp)
  case IaddRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case IaddRegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegImmRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case IaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case IaddRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pp)
  case IaddRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case IaddRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IaddRegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, pp)
  case Iadd3RegPredPredRegAddressImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Iadd3RegPredPredRegAddressImmRegDefinition,
        mods,
        pred,
        dst,
        pu,
        pv,
        srcA,
        srcB,
        sb_offset,
        srcC
      )
  case Iadd3RegPredPredRegAddressImmRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Iadd3RegPredPredRegAddressImmRegPredPredDefinition,
        mods,
        pred,
        dst,
        pu,
        pv,
        srcA,
        srcB,
        sb_offset,
        srcC,
        pp,
        pq
      )
  case Iadd3RegPredPredRegConstReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegPredPredRegConstRegDefinition, mods, pred, dst, pu, pv, srcA, sb, srcC)
  case Iadd3RegPredPredRegConstRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Iadd3RegPredPredRegConstRegPredPredDefinition,
        mods,
        pred,
        dst,
        pu,
        pv,
        srcA,
        sb,
        srcC,
        pp,
        pq
      )
  case Iadd3RegPredPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegPredPredRegImmRegDefinition, mods, pred, dst, pu, pv, srcA, sb, srcC)
  case Iadd3RegPredPredRegImmRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Iadd3RegPredPredRegImmRegPredPredDefinition,
        mods,
        pred,
        dst,
        pu,
        pv,
        srcA,
        sb,
        srcC,
        pp,
        pq
      )
  case Iadd3RegPredPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegPredPredRegRegRegDefinition, mods, pred, dst, pu, pv, srcA, srcB, srcC)
  case Iadd3RegPredPredRegRegRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Iadd3RegPredPredRegRegRegPredPredDefinition,
        mods,
        pred,
        dst,
        pu,
        pv,
        srcA,
        srcB,
        srcC,
        pp,
        pq
      )
  case Iadd3RegPredRegAddressImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Iadd3RegPredRegAddressImmRegDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        srcC
      )
  case Iadd3RegPredRegAddressImmRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Iadd3RegPredRegAddressImmRegPredPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        srcC,
        pp,
        pq
      )
  case Iadd3RegPredRegConstReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegPredRegConstRegDefinition, mods, pred, dst, pu, srcA, sb, srcC)
  case Iadd3RegPredRegConstRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Iadd3RegPredRegConstRegPredPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        sb,
        srcC,
        pp,
        pq
      )
  case Iadd3RegPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegPredRegImmRegDefinition, mods, pred, dst, pu, srcA, sb, srcC)
  case Iadd3RegPredRegImmRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Iadd3RegPredRegImmRegPredPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        sb,
        srcC,
        pp,
        pq
      )
  case Iadd3RegPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegPredRegRegRegDefinition, mods, pred, dst, pu, srcA, srcB, srcC)
  case Iadd3RegPredRegRegRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Iadd3RegPredRegRegRegPredPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        srcC,
        pp,
        pq
      )
  case Iadd3RegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case Iadd3RegRegAddressImmRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Iadd3RegRegAddressImmRegPredPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        pp,
        pq
      )
  case Iadd3RegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Iadd3RegRegConstRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegRegConstRegPredPredDefinition, mods, pred, dst, srcA, sb, srcC, pp, pq)
  case Iadd3RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Iadd3RegRegImmRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegRegImmRegPredPredDefinition, mods, pred, dst, srcA, sb, srcC, pp, pq)
  case Iadd3RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Iadd3RegRegRegRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd3RegRegRegRegPredPredDefinition, mods, pred, dst, srcA, srcB, srcC, pp, pq)
  case Iadd32iRegPredRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd32iRegPredRegImmDefinition, mods, pred, dst, pu, srcA, sb)
  case Iadd32iRegPredRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd32iRegPredRegImmPredDefinition, mods, pred, dst, pu, srcA, sb, pp)
  case Iadd32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd32iRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case Iadd32iRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iadd32iRegRegImmPredDefinition, mods, pred, dst, srcA, sb, pp)
  case IcmpRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IcmpRegRegConstRegDefinition, mods, pred, dst, srcA, srcConst, srcC)
  case IcmpRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IcmpRegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case IcmpRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IcmpRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, srcConst)
  case IcmpRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IcmpRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case IdeNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.IdeNoOperandsDefinition, mods, pred)
  case IdeImm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.IdeImmDefinition, mods, pred, sb)
  case IdpRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IdpRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case IdpRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IdpRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case IdpRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IdpRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Idp4aRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Idp4aRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case Idp4aRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Idp4aRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Idp4aRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Idp4aRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case IgmmaRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegAddressRegDefinition, mods, pred, dst, srcA, srcC)
  case IgmmaRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegAddressRegPredDefinition, mods, pred, dst, srcA, srcC, uPp)
  case IgmmaRegAddressRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegAddressRegPredRegDefinition, mods, pred, dst, srcA, srcC, uPp, re)
  case IgmmaRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegAddressRegPredRegImmDefinition, mods, pred, dst, srcA, srcC, uPp, re, id)
  case IgmmaRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegAddressRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case IgmmaRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegAddressRegRegImmDefinition, mods, pred, dst, srcA, srcC, re, id)
  case IgmmaRegAddressRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegAddressRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case IgmmaRegAddressRegRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegAddressRegRegPredRegDefinition, mods, pred, dst, srcA, srcB, srcC, uPp, re)
  case IgmmaRegAddressRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.IgmmaRegAddressRegRegPredRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        id
      )
  case IgmmaRegAddressRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegAddressRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC, re)
  case IgmmaRegAddressRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegAddressRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, re, id)
  case IgmmaRegRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegRegAddressRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case IgmmaRegRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegRegAddressRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case IgmmaRegRegAddressRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegRegAddressRegPredRegDefinition, mods, pred, dst, srcA, srcB, srcC, uPp, re)
  case IgmmaRegRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.IgmmaRegRegAddressRegPredRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        id
      )
  case IgmmaRegRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegRegAddressRegRegDefinition, mods, pred, dst, srcA, srcB, srcC, re)
  case IgmmaRegRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IgmmaRegRegAddressRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, re, id)
  case ImadRegPredRegAddressImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ImadRegPredRegAddressImmRegDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        srcC
      )
  case ImadRegPredRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ImadRegPredRegAddressImmRegPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        srcC,
        pp
      )
  case ImadRegPredRegConstReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegPredRegConstRegDefinition, mods, pred, dst, pu, srcA, sb, srcC)
  case ImadRegPredRegConstRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegPredRegConstRegPredDefinition, mods, pred, dst, pu, srcA, sb, srcC, pp)
  case ImadRegPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegPredRegImmRegDefinition, mods, pred, dst, pu, srcA, sb, srcC)
  case ImadRegPredRegImmRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegPredRegImmRegPredDefinition, mods, pred, dst, pu, srcA, sb, srcC, pp)
  case ImadRegPredRegRegAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ImadRegPredRegRegAddressImmDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        srcC,
        sc_offset
      )
  case ImadRegPredRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ImadRegPredRegRegAddressImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        srcC,
        sc_offset,
        pp
      )
  case ImadRegPredRegRegConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegPredRegRegConstDefinition, mods, pred, dst, pu, srcA, srcB, sc)
  case ImadRegPredRegRegConstPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegPredRegRegConstPredDefinition, mods, pred, dst, pu, srcA, srcB, sc, pp)
  case ImadRegPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegPredRegRegRegDefinition, mods, pred, dst, pu, srcA, srcB, srcC)
  case ImadRegPredRegRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegPredRegRegRegPredDefinition, mods, pred, dst, pu, srcA, srcB, srcC, pp)
  case ImadRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case ImadRegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ImadRegRegAddressImmRegPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        pp
      )
  case ImadRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case ImadRegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegConstRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case ImadRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case ImadRegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegImmRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case ImadRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, srcC, sc_offset)
  case ImadRegRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ImadRegRegRegAddressImmPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        sc_offset,
        pp
      )
  case ImadRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, sc)
  case ImadRegRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegRegConstPredDefinition, mods, pred, dst, srcA, srcB, sc, pp)
  case ImadRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case ImadRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegRegImmPredDefinition, mods, pred, dst, srcA, srcB, sc, pp)
  case ImadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case ImadRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadRegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, pp)
  case Imad32iRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Imad32iRegRegImmRegDefinition, mods, pred, dst, srcA, sImm, srcC)
  case ImadspRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadspRegRegConstRegDefinition, mods, pred, dst, srcA, srcConst, srcC)
  case ImadspRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadspRegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case ImadspRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadspRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, srcConst)
  case ImadspRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImadspRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case ImmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImmaRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case ImmaRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImmaRegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case ImmaRegRegRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ImmaRegRegRegRegPredRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        id
      )
  case ImmaRegRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImmaRegRegRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, re, id)
  case ImnmxPredPredRegRegImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxPredPredRegRegImmPredDefinition, mods, pred, pu, pv, dst, srcA, sb, pp)
  case ImnmxPredPredRegRegImmPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxPredPredRegRegImmPredPredDefinition, mods, pred, pu, pv, dst, srcA, sb, pp, pq)
  case ImnmxPredPredRegRegRegPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxPredPredRegRegRegPredDefinition, mods, pred, pu, pv, dst, srcA, srcB, pp)
  case ImnmxPredPredRegRegRegPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ImnmxPredPredRegRegRegPredPredDefinition,
        mods,
        pred,
        pu,
        pv,
        dst,
        srcA,
        srcB,
        pp,
        pq
      )
  case ImnmxPredRegRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxPredRegRegImmPredDefinition, mods, pred, pu, dst, srcA, sb, pp)
  case ImnmxPredRegRegImmPredPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxPredRegRegImmPredPredDefinition, mods, pred, pu, dst, srcA, sb, pp, pq)
  case ImnmxPredRegRegRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxPredRegRegRegPredDefinition, mods, pred, pu, dst, srcA, srcB, pp)
  case ImnmxPredRegRegRegPredPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxPredRegRegRegPredPredDefinition, mods, pred, pu, dst, srcA, srcB, pp, pq)
  case ImnmxRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxRegRegAddressImmPredDefinition, mods, pred, dst, srcA, srcB, sb_offset, pp)
  case ImnmxRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxRegRegConstPredDefinition, mods, pred, dst, srcA, sb, pp)
  case ImnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxRegRegImmPredDefinition, mods, pred, dst, srcA, sb, pp)
  case ImnmxRegRegImmPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxRegRegImmPredPredDefinition, mods, pred, dst, srcA, sb, pp, pq)
  case ImnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pa)
  case ImnmxRegRegRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImnmxRegRegRegPredPredDefinition, mods, pred, dst, srcA, srcB, pp, pq)
  case ImulRegPredRegAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImulRegPredRegAddressImmDefinition, mods, pred, dst, pu, srcA, srcB, sb_offset)
  case ImulRegPredRegConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImulRegPredRegConstDefinition, mods, pred, dst, pu, srcA, sb)
  case ImulRegPredRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImulRegPredRegImmDefinition, mods, pred, dst, pu, srcA, sb)
  case ImulRegPredRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImulRegPredRegRegDefinition, mods, pred, dst, pu, srcA, srcB)
  case ImulRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImulRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case ImulRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImulRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case ImulRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImulRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case ImulRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImulRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case ImulRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImulRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case ImulRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImulRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case ImulRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ImulRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Imul32iRegPredRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Imul32iRegPredRegImmDefinition, mods, pred, dst, pu, srcA, sb)
  case Imul32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Imul32iRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case IpaRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegAddressDefinition, mods, pred, dst, srcA)
  case IpaRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegAddressImmDefinition, mods, pred, dst, srcA, sb)
  case IpaRegAddressPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      ps: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegAddressPredDefinition, mods, pred, dst, srcA, ps)
  case IpaRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegAddressRegDefinition, mods, pred, dst, srcA, srcB)
  case IpaRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegAddressRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case IpaRegAddressRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      ps: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegAddressRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, ps)
  case IpaRegImm(
      dst: InstructionOperand.Register,
      attr: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegImmDefinition, mods, pred, dst, attr)
  case IpaRegImmConst(
      dst: InstructionOperand.Register,
      attr: InstructionOperand.Immediate,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegImmConstDefinition, mods, pred, dst, attr, sb)
  case IpaRegImmImm(
      dst: InstructionOperand.Register,
      attr: InstructionOperand.Immediate,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegImmImmDefinition, mods, pred, dst, attr, sb)
  case IpaRegImmPred(
      dst: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      ps: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegImmPredDefinition, mods, pred, dst, uImm, ps)
  case IpaRegImmReg(
      dst: InstructionOperand.Register,
      attr: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegImmRegDefinition, mods, pred, dst, attr, srcB)
  case IpaRegImmRegReg(
      dst: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegImmRegRegDefinition, mods, pred, dst, uImm, srcB, srcC)
  case IpaRegImmRegRegPred(
      dst: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      ps: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegImmRegRegPredDefinition, mods, pred, dst, uImm, srcB, srcC, ps)
  case IpaRegPredAddress(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegPredAddressDefinition, mods, pred, dst, pu, srcA)
  case IpaRegPredAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegPredAddressImmDefinition, mods, pred, dst, pu, srcA, sb)
  case IpaRegPredAddressReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegPredAddressRegDefinition, mods, pred, dst, pu, srcA, srcB)
  case IpaRegPredImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      attr: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegPredImmDefinition, mods, pred, dst, pu, attr)
  case IpaRegPredImmConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      attr: InstructionOperand.Immediate,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegPredImmConstDefinition, mods, pred, dst, pu, attr, sb)
  case IpaRegPredImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      attr: InstructionOperand.Immediate,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegPredImmImmDefinition, mods, pred, dst, pu, attr, sb)
  case IpaRegPredImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      attr: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IpaRegPredImmRegDefinition, mods, pred, dst, pu, attr, srcB)
  case IsadRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsadRegRegConstRegDefinition, mods, pred, dst, srcA, srcConst, srcC)
  case IsadRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsadRegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case IsadRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsadRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, srcConst)
  case IsadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsadRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case IsberdRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsberdRegAddressDefinition, mods, pred, dst, srcA)
  case IsbewrAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsbewrAddressRegDefinition, mods, pred, srcA, srcB)
  case IscaddRegPredRegAddressImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.IscaddRegPredRegAddressImmImmDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        scaleU5
      )
  case IscaddRegPredRegConstImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IscaddRegPredRegConstImmDefinition, mods, pred, dst, pu, srcA, sb, scaleU5)
  case IscaddRegPredRegImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IscaddRegPredRegImmImmDefinition, mods, pred, dst, pu, srcA, sb, scaleU5)
  case IscaddRegPredRegRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IscaddRegPredRegRegImmDefinition, mods, pred, dst, pu, srcA, srcB, scaleU5)
  case IscaddRegRegAddressImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.IscaddRegRegAddressImmImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        scaleU5
      )
  case IscaddRegRegConstImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IscaddRegRegConstImmDefinition, mods, pred, dst, srcA, sb, scaleU5)
  case IscaddRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IscaddRegRegImmImmDefinition, mods, pred, dst, srcA, sb, scaleU5)
  case IscaddRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IscaddRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, scaleU5)
  case Iscadd32iRegPredRegImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iscadd32iRegPredRegImmImmDefinition, mods, pred, dst, pu, srcA, sb, scaleU5)
  case Iscadd32iRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Iscadd32iRegRegImmImmDefinition, mods, pred, dst, srcA, sb, scaleU5)
  case IsetRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetRegRegConstDefinition, mods, pred, dst, srcA, srcConst)
  case IsetRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetRegRegConstPredDefinition, mods, pred, dst, srcA, srcConst, pa)
  case IsetRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetRegRegImmDefinition, mods, pred, dst, srcA, uImm)
  case IsetRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetRegRegImmPredDefinition, mods, pred, dst, srcA, uImm, pa)
  case IsetRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case IsetRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pa)
  case IsetpPredPredRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.IsetpPredPredRegAddressImmPredDefinition,
        mods,
        pred,
        pu,
        pv,
        srcA,
        srcB,
        sb_offset,
        pp
      )
  case IsetpPredPredRegAddressImmPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.IsetpPredPredRegAddressImmPredPredDefinition,
        mods,
        pred,
        pu,
        pv,
        srcA,
        srcB,
        sb_offset,
        pp,
        pr
      )
  case IsetpPredPredRegConstPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.IsetpPredPredRegConstPredDefinition,
        mods,
        pred,
        dstPredicate,
        nPd,
        srcA,
        srcConst,
        pa
      )
  case IsetpPredPredRegConstPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredPredRegConstPredPredDefinition, mods, pred, pu, pv, srcA, sb, pp, pr)
  case IsetpPredPredRegImmPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredPredRegImmPredDefinition, mods, pred, dstPredicate, nPd, srcA, uImm, pa)
  case IsetpPredPredRegImmPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredPredRegImmPredPredDefinition, mods, pred, pu, pv, srcA, sb, pp, pr)
  case IsetpPredPredRegRegPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredPredRegRegPredDefinition, mods, pred, dstPredicate, nPd, srcA, srcB, pa)
  case IsetpPredPredRegRegPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredPredRegRegPredPredDefinition, mods, pred, pu, pv, srcA, srcB, pp, pr)
  case IsetpPredRegAddressImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredRegAddressImmDefinition, mods, pred, pu, srcA, srcB, sb_offset)
  case IsetpPredRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredRegAddressImmPredDefinition, mods, pred, pu, srcA, srcB, sb_offset, pr)
  case IsetpPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredRegConstDefinition, mods, pred, dstPredicate, srcA, srcConst)
  case IsetpPredRegConstPred(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredRegConstPredDefinition, mods, pred, pu, srcA, sb, pr)
  case IsetpPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredRegImmDefinition, mods, pred, dstPredicate, srcA, uImm)
  case IsetpPredRegImmPred(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredRegImmPredDefinition, mods, pred, pu, srcA, sb, pr)
  case IsetpPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredRegRegDefinition, mods, pred, dstPredicate, srcA, srcB)
  case IsetpPredRegRegPred(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsetpPredRegRegPredDefinition, mods, pred, pu, srcA, srcB, pr)
  case IsubRegConstReg(
      dst: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsubRegConstRegDefinition, mods, pred, dst, srcConst, srcA)
  case IsubRegImmReg(
      dst: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsubRegImmRegDefinition, mods, pred, dst, uImm, srcA)
  case IsubRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsubRegRegConstDefinition, mods, pred, dst, srcA, srcConst)
  case IsubRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsubRegRegImmDefinition, mods, pred, dst, srcA, uImm)
  case IsubRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.IsubRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case JcalConst(srcConst: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.JcalConstDefinition, mods, pred, srcConst)
  case JcalImm(sImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.JcalImmDefinition, mods, pred, sImm)
  case JmpConst(sa: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.JmpConstDefinition, mods, pred, sa)
  case JmpImm(sa: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.JmpImmDefinition, mods, pred, sa)
  case JmpPredConst(
      pp: InstructionOperand.Predicate,
      sa: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmpPredConstDefinition, mods, pred, pp, sa)
  case JmpPredImm(
      pp: InstructionOperand.Predicate,
      sa: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmpPredImmDefinition, mods, pred, pp, sa)
  case JmpPredPredConst(
      pp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      sa: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmpPredPredConstDefinition, mods, pred, pp, uPq, sa)
  case JmpPredPredImm(
      pp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      sa: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmpPredPredImmDefinition, mods, pred, pp, uPq, sa)
  case JmpPredRegConst(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      sa: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmpPredRegConstDefinition, mods, pred, pp, srcB, sa)
  case JmpPredRegImm(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      sa: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmpPredRegImmDefinition, mods, pred, pp, srcB, sa)
  case JmpRegConst(
      srcB: InstructionOperand.Register,
      sa: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmpRegConstDefinition, mods, pred, srcB, sa)
  case JmpRegImm(
      srcB: InstructionOperand.Register,
      sa: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmpRegImmDefinition, mods, pred, srcB, sa)
  case JmpSymbolConst(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmpSymbolConstDefinition, mods, pred, testCC, srcConst)
  case JmpSymbolImm(
      testCC: InstructionOperand.Symbol,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmpSymbolImmDefinition, mods, pred, testCC, sImm)
  case JmpSymbolImmImm(
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      immConstOffset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmpSymbolImmImmDefinition, mods, pred, srcConst, constBank, immConstOffset)
  case JmpSymbolSymbolImmImm(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      immConstOffset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.JmpSymbolSymbolImmImmDefinition,
        mods,
        pred,
        testCC,
        srcConst,
        constBank,
        immConstOffset
      )
  case JmxConst(srcConst: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.JmxConstDefinition, mods, pred, srcConst)
  case JmxPredReg(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmxPredRegDefinition, mods, pred, pp, srcA)
  case JmxPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmxPredRegImmDefinition, mods, pred, pp, srcA, ra_offset)
  case JmxReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.JmxRegDefinition, mods, pred, srcA)
  case JmxRegImm(
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmxRegImmDefinition, mods, pred, srcA, ra_offset)
  case JmxSymbolConst(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmxSymbolConstDefinition, mods, pred, testCC, srcConst)
  case JmxSymbolImmAddress(
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmxSymbolImmAddressDefinition, mods, pred, srcConst, constBank, srcA)
  case JmxSymbolReg(
      testCC: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmxSymbolRegDefinition, mods, pred, testCC, srcA)
  case JmxSymbolRegImm(
      testCC: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmxSymbolRegImmDefinition, mods, pred, testCC, srcA, sImm)
  case JmxSymbolSymbolImmAddress(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmxSymbolSymbolImmAddressDefinition, mods, pred, testCC, srcConst, constBank, srcA)
  case JmxuPredReg(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmxuPredRegDefinition, mods, pred, pp, srcA)
  case JmxuPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uR_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmxuPredRegImmDefinition, mods, pred, pp, srcA, uR_offset)
  case JmxuReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.JmxuRegDefinition, mods, pred, srcA)
  case JmxuRegImm(
      srcA: InstructionOperand.Register,
      uR_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.JmxuRegImmDefinition, mods, pred, srcA, uR_offset)
  case KilNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.KilNoOperandsDefinition, mods, pred)
  case KillNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.KillNoOperandsDefinition, mods, pred)
  case KillPred(pp: InstructionOperand.Predicate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.KillPredDefinition, mods, pred, pp)
  case LdPredRegAddress(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdPredRegAddressDefinition, mods, pred, pu, dst, srcA)
  case LdPredRegAddressPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdPredRegAddressPredDefinition, mods, pred, pu, dst, srcA, pnz)
  case LdRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdRegAddressDefinition, mods, pred, dst, srcA)
  case LdRegAddressPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdRegAddressPredDefinition, mods, pred, dst, srcA, pnz)
  case LdcRegAddressAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcRegAddressAddressDefinition, mods, pred, dst, srcA, srcB)
  case LdcRegConst(
      dst: InstructionOperand.Register,
      sa: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcRegConstDefinition, mods, pred, dst, sa)
  case LdcuRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegAddressDefinition, mods, pred, dst, srcA)
  case LdcuRegAddressAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegAddressAddressDefinition, mods, pred, dst, srcA, srcB)
  case LdcuRegAddressPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegAddressPredDefinition, mods, pred, dst, srcA, uPp)
  case LdcuRegConst(
      dst: InstructionOperand.Register,
      sa: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegConstDefinition, mods, pred, dst, sa)
  case LdcuRegRegAddress(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegRegAddressDefinition, mods, pred, uRd2, dst, srcA)
  case LdcuRegRegAddressAddress(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegRegAddressAddressDefinition, mods, pred, uRd2, dst, srcA, srcB)
  case LdcuRegRegAddressAddressImm(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      word_mask: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegRegAddressAddressImmDefinition, mods, pred, uRd2, dst, srcA, srcB, word_mask)
  case LdcuRegRegAddressImm(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      word_mask: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegRegAddressImmDefinition, mods, pred, uRd2, dst, srcA, word_mask)
  case LdcuRegRegAddressImmPred(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      word_mask: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegRegAddressImmPredDefinition, mods, pred, uRd2, dst, srcA, word_mask, uPp)
  case LdcuRegRegAddressPred(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegRegAddressPredDefinition, mods, pred, uRd2, dst, srcA, uPp)
  case LdcuRegRegConst(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      sa: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegRegConstDefinition, mods, pred, uRd2, dst, sa)
  case LdcuRegRegConstImm(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      sa: InstructionOperand.ConstantBank,
      word_mask: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdcuRegRegConstImmDefinition, mods, pred, uRd2, dst, sa, word_mask)
  case LdgPredRegAddress(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdgPredRegAddressDefinition, mods, pred, dstPredicate, dst, srcA)
  case LdgPredRegAddressPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdgPredRegAddressPredDefinition, mods, pred, pu, dst, srcA, pnz)
  case LdgRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdgRegAddressDefinition, mods, pred, dst, srcA)
  case LdgRegAddressPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdgRegAddressPredDefinition, mods, pred, dst, srcA, pnz)
  case LdgRegRegAddress(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdgRegRegAddressDefinition, mods, pred, rd2, dst, srcA)
  case LdgRegRegAddressImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      word_mask: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdgRegRegAddressImmDefinition, mods, pred, rd2, dst, srcA, word_mask)
  case LdgRegRegAddressImmPred(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      word_mask: InstructionOperand.Immediate,
      pnz: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdgRegRegAddressImmPredDefinition, mods, pred, rd2, dst, srcA, word_mask, pnz)
  case LdgRegRegAddressPred(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdgRegRegAddressPredDefinition, mods, pred, rd2, dst, srcA, pnz)
  case LdgdepbarNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.LdgdepbarNoOperandsDefinition, mods, pred)
  case LdgmcRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdgmcRegAddressDefinition, mods, pred, dst, srcA)
  case LdgstsAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdgstsAddressAddressDefinition, mods, pred, srcB, srcA)
  case LdgstsAddressAddressPred(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdgstsAddressAddressPredDefinition, mods, pred, srcB, srcA, pnz)
  case LdlRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdlRegAddressDefinition, mods, pred, dst, srcA)
  case LdsPredRegAddress(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdsPredRegAddressDefinition, mods, pred, pu, dst, srcA)
  case LdsRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdsRegAddressDefinition, mods, pred, dst, srcA)
  case LdslkPredRegAddress(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdslkPredRegAddressDefinition, mods, pred, dstPredicate, dst, srcA)
  case LdsmRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdsmRegAddressDefinition, mods, pred, dst, srcA)
  case LdtRegAddress(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdtRegAddressDefinition, mods, pred, dst, srcB)
  case LdtmRegAddress(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdtmRegAddressDefinition, mods, pred, dst, srcB)
  case LdtmRegRegAddress(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdtmRegRegAddressDefinition, mods, pred, rd2, dst, srcB)
  case LdtramRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LdtramRegAddressDefinition, mods, pred, dst, srcA)
  case LeaPredRegRegConst(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaPredRegRegConstDefinition, mods, pred, dstPredicate, dst, srcA, srcConst)
  case LeaPredRegRegConstImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      uImm5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaPredRegRegConstImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcConst,
        uImm5
      )
  case LeaPredRegRegConstReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaPredRegRegConstRegDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcConst,
        srcC
      )
  case LeaPredRegRegConstRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      uImm5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaPredRegRegConstRegImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcConst,
        srcC,
        uImm5
      )
  case LeaPredRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm20: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaPredRegRegImmDefinition, mods, pred, dstPredicate, dst, srcA, uImm20)
  case LeaPredRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm20: InstructionOperand.Immediate,
      uImm5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaPredRegRegImmImmDefinition, mods, pred, dstPredicate, dst, srcA, uImm20, uImm5)
  case LeaPredRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaPredRegRegRegDefinition, mods, pred, dstPredicate, dst, srcA, srcB)
  case LeaPredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uImm5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaPredRegRegRegImmDefinition, mods, pred, dstPredicate, dst, srcA, srcB, uImm5)
  case LeaPredRegRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaPredRegRegRegRegDefinition, mods, pred, dstPredicate, dst, srcA, srcB, srcC)
  case LeaPredRegRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uImm5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaPredRegRegRegRegImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcB,
        srcC,
        uImm5
      )
  case LeaRegPredRegAddressImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaRegPredRegAddressImmImmDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        scaleU5
      )
  case LeaRegPredRegAddressImmImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaRegPredRegAddressImmImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        scaleU5,
        pp
      )
  case LeaRegPredRegAddressImmRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaRegPredRegAddressImmRegImmDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        srcC,
        scaleU5
      )
  case LeaRegPredRegAddressImmRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaRegPredRegAddressImmRegImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        srcC,
        scaleU5,
        pp
      )
  case LeaRegPredRegConstImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegPredRegConstImmDefinition, mods, pred, dst, pu, srcA, sb, scaleU5)
  case LeaRegPredRegConstImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegPredRegConstImmPredDefinition, mods, pred, dst, pu, srcA, sb, scaleU5, pp)
  case LeaRegPredRegConstRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegPredRegConstRegImmDefinition, mods, pred, dst, pu, srcA, sb, srcC, scaleU5)
  case LeaRegPredRegConstRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaRegPredRegConstRegImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        sb,
        srcC,
        scaleU5,
        pp
      )
  case LeaRegPredRegImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegPredRegImmImmDefinition, mods, pred, dst, pu, srcA, sb, scaleU5)
  case LeaRegPredRegImmImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegPredRegImmImmPredDefinition, mods, pred, dst, pu, srcA, sb, scaleU5, pp)
  case LeaRegPredRegImmRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegPredRegImmRegImmDefinition, mods, pred, dst, pu, srcA, sb, srcC, scaleU5)
  case LeaRegPredRegImmRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaRegPredRegImmRegImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        sb,
        srcC,
        scaleU5,
        pp
      )
  case LeaRegPredRegRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegPredRegRegImmDefinition, mods, pred, dst, pu, srcA, srcB, scaleU5)
  case LeaRegPredRegRegImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegPredRegRegImmImmDefinition, mods, pred, dst, pu, srcA, srcB, sc, scaleU5)
  case LeaRegPredRegRegImmImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaRegPredRegRegImmImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sc,
        scaleU5,
        pp
      )
  case LeaRegPredRegRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegPredRegRegImmPredDefinition, mods, pred, dst, pu, srcA, srcB, scaleU5, pp)
  case LeaRegPredRegRegRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegPredRegRegRegImmDefinition, mods, pred, dst, pu, srcA, srcB, srcC, scaleU5)
  case LeaRegPredRegRegRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaRegPredRegRegRegImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        srcC,
        scaleU5,
        pp
      )
  case LeaRegRegAddressImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegAddressImmImmDefinition, mods, pred, dst, srcA, srcB, sb_offset, scaleU5)
  case LeaRegRegAddressImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaRegRegAddressImmImmPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        scaleU5,
        pp
      )
  case LeaRegRegAddressImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaRegRegAddressImmRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        scaleU5
      )
  case LeaRegRegAddressImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LeaRegRegAddressImmRegImmPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        scaleU5,
        pp
      )
  case LeaRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegConstDefinition, mods, pred, dst, srcA, srcConst)
  case LeaRegRegConstImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegConstImmDefinition, mods, pred, dst, srcA, sb, scaleU5)
  case LeaRegRegConstImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegConstImmPredDefinition, mods, pred, dst, srcA, sb, scaleU5, pp)
  case LeaRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegConstRegDefinition, mods, pred, dst, srcA, srcConst, srcC)
  case LeaRegRegConstRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegConstRegImmDefinition, mods, pred, dst, srcA, sb, srcC, scaleU5)
  case LeaRegRegConstRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegConstRegImmPredDefinition, mods, pred, dst, srcA, sb, srcC, scaleU5, pp)
  case LeaRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm20: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegImmDefinition, mods, pred, dst, srcA, uImm20)
  case LeaRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegImmImmDefinition, mods, pred, dst, srcA, sb, scaleU5)
  case LeaRegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegImmImmPredDefinition, mods, pred, dst, srcA, sb, scaleU5, pp)
  case LeaRegRegImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegImmRegImmDefinition, mods, pred, dst, srcA, sb, srcC, scaleU5)
  case LeaRegRegImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegImmRegImmPredDefinition, mods, pred, dst, srcA, sb, srcC, scaleU5, pp)
  case LeaRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case LeaRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, scaleU5)
  case LeaRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegRegImmImmDefinition, mods, pred, dst, srcA, srcB, sc, scaleU5)
  case LeaRegRegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegRegImmImmPredDefinition, mods, pred, dst, srcA, srcB, sc, scaleU5, pp)
  case LeaRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegRegImmPredDefinition, mods, pred, dst, srcA, srcB, scaleU5, pp)
  case LeaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case LeaRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, scaleU5)
  case LeaRegRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LeaRegRegRegRegImmPredDefinition, mods, pred, dst, srcA, srcB, srcC, scaleU5, pp)
  case LepcReg(dst: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.LepcRegDefinition, mods, pred, dst)
  case LepcRegImm(
      dst: InstructionOperand.Register,
      sImm58: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LepcRegImmDefinition, mods, pred, dst, sImm58)
  case LongjmpNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.LongjmpNoOperandsDefinition, mods, pred)
  case LopPredRegRegAddressImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopPredRegRegAddressImmDefinition, mods, pred, pu, dst, srcA, srcB, sb_offset)
  case LopPredRegRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.LopPredRegRegAddressImmPredDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcB,
        sb_offset,
        pp
      )
  case LopPredRegRegConst(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopPredRegRegConstDefinition, mods, pred, dstPredicate, dst, srcA, srcConst)
  case LopPredRegRegConstPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopPredRegRegConstPredDefinition, mods, pred, pu, dst, srcA, sb, pp)
  case LopPredRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopPredRegRegImmDefinition, mods, pred, dstPredicate, dst, srcA, uImm)
  case LopPredRegRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopPredRegRegImmPredDefinition, mods, pred, pu, dst, srcA, sb, pp)
  case LopPredRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopPredRegRegRegDefinition, mods, pred, dstPredicate, dst, srcA, srcB)
  case LopPredRegRegRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopPredRegRegRegPredDefinition, mods, pred, pu, dst, srcA, srcB, pp)
  case LopRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case LopRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopRegRegAddressImmPredDefinition, mods, pred, dst, srcA, srcB, sb_offset, pp)
  case LopRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case LopRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopRegRegConstPredDefinition, mods, pred, dst, srcA, sb, pp)
  case LopRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case LopRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopRegRegImmPredDefinition, mods, pred, dst, srcA, sb, pp)
  case LopRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case LopRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.LopRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pp)
  case Lop3PredRegRegAddressImmReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Lop3PredRegRegAddressImmRegDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC
      )
  case Lop3PredRegRegAddressImmRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Lop3PredRegRegAddressImmRegImmDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        imm8
      )
  case Lop3PredRegRegAddressImmRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Lop3PredRegRegAddressImmRegImmPredDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        imm8,
        pp
      )
  case Lop3PredRegRegAddressImmRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Lop3PredRegRegAddressImmRegPredDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        pp
      )
  case Lop3PredRegRegConstReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3PredRegRegConstRegDefinition, mods, pred, pu, dst, srcA, sb, srcC)
  case Lop3PredRegRegConstRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3PredRegRegConstRegImmDefinition, mods, pred, pu, dst, srcA, sb, srcC, imm8)
  case Lop3PredRegRegConstRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Lop3PredRegRegConstRegImmPredDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        sb,
        srcC,
        imm8,
        pp
      )
  case Lop3PredRegRegConstRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3PredRegRegConstRegPredDefinition, mods, pred, pu, dst, srcA, sb, srcC, pp)
  case Lop3PredRegRegImmReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3PredRegRegImmRegDefinition, mods, pred, pu, dst, srcA, sb, srcC)
  case Lop3PredRegRegImmRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3PredRegRegImmRegImmDefinition, mods, pred, pu, dst, srcA, sb, srcC, imm8)
  case Lop3PredRegRegImmRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Lop3PredRegRegImmRegImmPredDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        sb,
        srcC,
        imm8,
        pp
      )
  case Lop3PredRegRegImmRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3PredRegRegImmRegPredDefinition, mods, pred, pu, dst, srcA, sb, srcC, pp)
  case Lop3PredRegRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3PredRegRegRegRegDefinition, mods, pred, dstPredicate, dst, srcA, srcB, srcC)
  case Lop3PredRegRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Lop3PredRegRegRegRegImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcB,
        srcC,
        imm8
      )
  case Lop3PredRegRegRegRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Lop3PredRegRegRegRegImmPredDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcB,
        srcC,
        imm8,
        pp
      )
  case Lop3PredRegRegRegRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3PredRegRegRegRegPredDefinition, mods, pred, pu, dst, srcA, srcB, srcC, pp)
  case Lop3RegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case Lop3RegRegAddressImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Lop3RegRegAddressImmRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        imm8
      )
  case Lop3RegRegAddressImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Lop3RegRegAddressImmRegImmPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        imm8,
        pp
      )
  case Lop3RegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Lop3RegRegAddressImmRegPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        pp
      )
  case Lop3RegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Lop3RegRegConstRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegConstRegImmDefinition, mods, pred, dst, srcA, sb, srcC, imm8)
  case Lop3RegRegConstRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegConstRegImmPredDefinition, mods, pred, dst, srcA, sb, srcC, imm8, pp)
  case Lop3RegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegConstRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case Lop3RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Lop3RegRegImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegImmRegImmDefinition, mods, pred, dst, srcA, sb, srcC, imm8)
  case Lop3RegRegImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegImmRegImmPredDefinition, mods, pred, dst, srcA, sb, srcC, imm8, pp)
  case Lop3RegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegImmRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case Lop3RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Lop3RegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, imm8)
  case Lop3RegRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegRegRegImmPredDefinition, mods, pred, dst, srcA, srcB, srcC, imm8, pp)
  case Lop3RegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop3RegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, pp)
  case Lop32iPredRegRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop32iPredRegRegImmDefinition, mods, pred, pu, dst, srcA, sb)
  case Lop32iPredRegRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop32iPredRegRegImmPredDefinition, mods, pred, pu, dst, srcA, sb, pp)
  case Lop32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop32iRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case Lop32iRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Lop32iRegRegImmPredDefinition, mods, pred, dst, srcA, sb, pp)
  case MatchPredRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MatchPredRegRegDefinition, mods, pred, pu, dst, srcA)
  case MatchRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MatchRegRegDefinition, mods, pred, dst, srcA)
  case MembarNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.MembarNoOperandsDefinition, mods, pred)
  case MovAddressConst(
      dst: InstructionOperand.Address,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovAddressConstDefinition, mods, pred, dst, sb)
  case MovAddressConstImm(
      dst: InstructionOperand.Address,
      sb: InstructionOperand.ConstantBank,
      pixMaskU04: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovAddressConstImmDefinition, mods, pred, dst, sb, pixMaskU04)
  case MovAddressImm(
      dst: InstructionOperand.Address,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovAddressImmDefinition, mods, pred, dst, sb)
  case MovAddressImmImm(
      dst: InstructionOperand.Address,
      sb: InstructionOperand.Immediate,
      pixMaskU04: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovAddressImmImmDefinition, mods, pred, dst, sb, pixMaskU04)
  case MovAddressReg(
      dst: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovAddressRegDefinition, mods, pred, dst, srcB)
  case MovAddressRegImm(
      dst: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      pixMaskU04: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovAddressRegImmDefinition, mods, pred, dst, srcB, pixMaskU04)
  case MovRegAddress(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovRegAddressDefinition, mods, pred, dst, srcB)
  case MovRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      pixMaskU04: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovRegAddressImmDefinition, mods, pred, dst, srcB, pixMaskU04)
  case MovRegAddressImmImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pixMaskU04: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovRegAddressImmImmDefinition, mods, pred, dst, srcB, sb_offset, pixMaskU04)
  case MovRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovRegConstDefinition, mods, pred, dst, sb)
  case MovRegConstImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pixMaskU04: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovRegConstImmDefinition, mods, pred, dst, sb, pixMaskU04)
  case MovRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovRegImmDefinition, mods, pred, dst, sb)
  case MovRegImmImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pixMaskU04: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovRegImmImmDefinition, mods, pred, dst, sb, pixMaskU04)
  case MovRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovRegRegDefinition, mods, pred, dst, srcB)
  case MovRegRegImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pixMaskU04: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovRegRegImmDefinition, mods, pred, dst, srcB, pixMaskU04)
  case Mov32iRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Mov32iRegImmDefinition, mods, pred, dst, sb)
  case Mov32iRegImmImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pixMaskU04: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Mov32iRegImmImmDefinition, mods, pred, dst, sb, pixMaskU04)
  case Mov64iurRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Mov64iurRegImmDefinition, mods, pred, dst, sb)
  case Mov64iurRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Mov64iurRegRegDefinition, mods, pred, dst, srcB)
  case MovmRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MovmRegRegDefinition, mods, pred, dst, srcA)
  case MufuRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MufuRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case MufuRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MufuRegConstDefinition, mods, pred, dst, sb)
  case MufuRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MufuRegImmDefinition, mods, pred, dst, sb)
  case MufuRegImmImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MufuRegImmImmDefinition, mods, pred, dst, sb, sb1)
  case MufuRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.MufuRegRegDefinition, mods, pred, dst, srcA)
  case NanosleepNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.NanosleepNoOperandsDefinition, mods, pred)
  case NanosleepAddressImm(
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.NanosleepAddressImmDefinition, mods, pred, srcB, sb_offset)
  case NanosleepConst(sb: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.NanosleepConstDefinition, mods, pred, sb)
  case NanosleepImm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.NanosleepImmDefinition, mods, pred, sb)
  case NanosleepPred(pp: InstructionOperand.Predicate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.NanosleepPredDefinition, mods, pred, pp)
  case NanosleepPredAddressImm(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.NanosleepPredAddressImmDefinition, mods, pred, pp, srcB, sb_offset)
  case NanosleepPredConst(
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.NanosleepPredConstDefinition, mods, pred, pp, sb)
  case NanosleepPredImm(
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.NanosleepPredImmDefinition, mods, pred, pp, sb)
  case NanosleepPredReg(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.NanosleepPredRegDefinition, mods, pred, pp, srcB)
  case NanosleepReg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.NanosleepRegDefinition, mods, pred, srcB)
  case NanotrapConst(sb: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.NanotrapConstDefinition, mods, pred, sb)
  case NanotrapImm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.NanotrapImmDefinition, mods, pred, sb)
  case NanotrapPredConst(
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.NanotrapPredConstDefinition, mods, pred, pp, sb)
  case NanotrapPredImm(
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.NanotrapPredImmDefinition, mods, pred, pp, sb)
  case NanotrapPredReg(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.NanotrapPredRegDefinition, mods, pred, pp, srcB)
  case NanotrapReg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.NanotrapRegDefinition, mods, pred, srcB)
  case NopNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.NopNoOperandsDefinition, mods, pred)
  case NopImm(uImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.NopImmDefinition, mods, pred, uImm)
  case NopSymbol(testCC: InstructionOperand.Symbol, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.NopSymbolDefinition, mods, pred, testCC)
  case NopSymbolImm(
      testCC: InstructionOperand.Symbol,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.NopSymbolImmDefinition, mods, pred, testCC, uImm)
  case OmmaRegRegRegRegPredRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.OmmaRegRegRegRegPredRegRegRegDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        rh,
        uRi
      )
  case OmmaRegRegRegRegPredRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      sparseId: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.OmmaRegRegRegRegPredRegRegRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        rh,
        uRi,
        sparseId
      )
  case OmmaRegRegRegRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.OmmaRegRegRegRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC, re, rh, uRi)
  case OmmaRegRegRegRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      sparseId: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.OmmaRegRegRegRegRegRegRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        re,
        rh,
        uRi,
        sparseId
      )
  case OutReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.OutRegDefinition, mods, pred, srcA)
  case OutRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.OutRegRegConstDefinition, mods, pred, dst, srcA, srcConst)
  case OutRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.OutRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case OutRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.OutRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case P2rReg(dst: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.P2rRegDefinition, mods, pred, dst)
  case P2rRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.P2rRegRegConstDefinition, mods, pred, dst, srcA, srcConst)
  case P2rRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.P2rRegRegImmDefinition, mods, pred, dst, srcA, uImm)
  case P2rRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.P2rRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case P2rRegSymbol(
      dst: InstructionOperand.Register,
      pr: InstructionOperand.Symbol,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.P2rRegSymbolDefinition, mods, pred, dst, pr)
  case P2rRegSymbolRegAddressImm(
      dst: InstructionOperand.Register,
      pr: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.P2rRegSymbolRegAddressImmDefinition, mods, pred, dst, pr, srcA, srcB, sb_offset)
  case P2rRegSymbolRegConst(
      dst: InstructionOperand.Register,
      pr: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.P2rRegSymbolRegConstDefinition, mods, pred, dst, pr, srcA, sb)
  case P2rRegSymbolRegImm(
      dst: InstructionOperand.Register,
      pr: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.P2rRegSymbolRegImmDefinition, mods, pred, dst, pr, srcA, sb)
  case P2rRegSymbolRegReg(
      dst: InstructionOperand.Register,
      pr: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.P2rRegSymbolRegRegDefinition, mods, pred, dst, pr, srcA, srcB)
  case PbkConst(srcConst: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.PbkConstDefinition, mods, pred, srcConst)
  case PbkImm(sImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.PbkImmDefinition, mods, pred, sImm)
  case PcntConst(srcConst: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.PcntConstDefinition, mods, pred, srcConst)
  case PcntImm(sImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.PcntImmDefinition, mods, pred, sImm)
  case PexitImm(sImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.PexitImmDefinition, mods, pred, sImm)
  case PixldReg(dst: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.PixldRegDefinition, mods, pred, dst)
  case PixldRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PixldRegAddressDefinition, mods, pred, dst, srcA)
  case PixldRegPred(
      dst: InstructionOperand.Register,
      dstPredicate: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PixldRegPredDefinition, mods, pred, dst, dstPredicate)
  case PixldRegPredAddress(
      dst: InstructionOperand.Register,
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PixldRegPredAddressDefinition, mods, pred, dst, dstPredicate, srcA)
  case PlongjmpConst(
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PlongjmpConstDefinition, mods, pred, srcConst)
  case PlongjmpImm(sImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.PlongjmpImmDefinition, mods, pred, sImm)
  case Plop3PredPredAddressImmPredImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredAddressImmPredImmDefinition,
        mods,
        pred,
        pu,
        pp,
        srcB,
        sb_offset,
        pr,
        uimm8
      )
  case Plop3PredPredAddressImmRegImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredAddressImmRegImmDefinition,
        mods,
        pred,
        pu,
        pp,
        srcB,
        sb_offset,
        srcC,
        uimm8
      )
  case Plop3PredPredConstPredImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Plop3PredPredConstPredImmDefinition, mods, pred, pu, pp, sb, pr, uimm8)
  case Plop3PredPredConstRegImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Plop3PredPredConstRegImmDefinition, mods, pred, pu, pp, sb, srcC, uimm8)
  case Plop3PredPredPredAddressImmPredImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredPredAddressImmPredImmImmDefinition,
        mods,
        pred,
        pu,
        pv,
        pp,
        srcB,
        sb_offset,
        pr,
        uimm8,
        vimm8
      )
  case Plop3PredPredPredAddressImmRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredPredAddressImmRegImmImmDefinition,
        mods,
        pred,
        pu,
        pv,
        pp,
        srcB,
        sb_offset,
        srcC,
        uimm8,
        vimm8
      )
  case Plop3PredPredPredConstPredImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredPredConstPredImmImmDefinition,
        mods,
        pred,
        pu,
        pv,
        pp,
        sb,
        pr,
        uimm8,
        vimm8
      )
  case Plop3PredPredPredConstRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredPredConstRegImmImmDefinition,
        mods,
        pred,
        pu,
        pv,
        pp,
        sb,
        srcC,
        uimm8,
        vimm8
      )
  case Plop3PredPredPredPred(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Plop3PredPredPredPredDefinition, mods, pred, pu, pp, pq, pr)
  case Plop3PredPredPredPredImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Plop3PredPredPredPredImmDefinition, mods, pred, pu, pp, pq, pr, uimm8)
  case Plop3PredPredPredPredPredImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredPredPredPredImmImmDefinition,
        mods,
        pred,
        pu,
        pv,
        pp,
        pq,
        pr,
        uimm8,
        vimm8
      )
  case Plop3PredPredPredRegPredImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredPredRegPredImmImmDefinition,
        mods,
        pred,
        pu,
        pv,
        pp,
        srcB,
        pr,
        uimm8,
        vimm8
      )
  case Plop3PredPredPredRegRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredPredRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        pv,
        pp,
        srcB,
        srcC,
        uimm8,
        vimm8
      )
  case Plop3PredPredRegAddressImmRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredRegAddressImmRegImmImmDefinition,
        mods,
        pred,
        pu,
        pv,
        srcA,
        srcB,
        sb_offset,
        srcC,
        uimm8,
        vimm8
      )
  case Plop3PredPredRegConstRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredRegConstRegImmImmDefinition,
        mods,
        pred,
        pu,
        pv,
        srcA,
        sb,
        srcC,
        uimm8,
        vimm8
      )
  case Plop3PredPredRegPredImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Plop3PredPredRegPredImmDefinition, mods, pred, pu, pp, srcB, pr, uimm8)
  case Plop3PredPredRegRegImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Plop3PredPredRegRegImmDefinition, mods, pred, pu, pp, srcB, srcC, uimm8)
  case Plop3PredPredRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredPredRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        pv,
        srcA,
        srcB,
        srcC,
        uimm8,
        vimm8
      )
  case Plop3PredRegAddressImmRegImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Plop3PredRegAddressImmRegImmDefinition,
        mods,
        pred,
        pu,
        srcA,
        srcB,
        sb_offset,
        srcC,
        uimm8
      )
  case Plop3PredRegConstRegImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Plop3PredRegConstRegImmDefinition, mods, pred, pu, srcA, sb, srcC, uimm8)
  case Plop3PredRegRegRegImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Plop3PredRegRegRegImmDefinition, mods, pred, pu, srcA, srcB, srcC, uimm8)
  case PmtrigImm(imm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.PmtrigImmDefinition, mods, pred, imm)
  case PmtrigPredImm(
      pp: InstructionOperand.Predicate,
      imm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PmtrigPredImmDefinition, mods, pred, pp, imm)
  case PopcRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PopcRegAddressImmDefinition, mods, pred, dst, srcB, sb_offset)
  case PopcRegConst(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PopcRegConstDefinition, mods, pred, dst, sb)
  case PopcRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PopcRegImmDefinition, mods, pred, dst, sb)
  case PopcRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PopcRegRegDefinition, mods, pred, dst, srcB)
  case PopcRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PopcRegRegConstDefinition, mods, pred, dst, srcA, srcConst)
  case PopcRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PopcRegRegImmDefinition, mods, pred, dst, srcA, uImm)
  case PopcRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PopcRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case PreexitNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.PreexitNoOperandsDefinition, mods, pred)
  case PretConst(srcConst: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.PretConstDefinition, mods, pred, srcConst)
  case PretImm(sImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.PretImmDefinition, mods, pred, sImm)
  case PrmtRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PrmtRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case PrmtRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PrmtRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case PrmtRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PrmtRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case PrmtRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PrmtRegRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, srcC, sc_offset)
  case PrmtRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PrmtRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, sc)
  case PrmtRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PrmtRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case PrmtRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PrmtRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case PsetRegPredPred(
      dst: InstructionOperand.Register,
      pc: InstructionOperand.Predicate,
      pb: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PsetRegPredPredDefinition, mods, pred, dst, pc, pb)
  case PsetRegPredPredPred(
      dst: InstructionOperand.Register,
      pc: InstructionOperand.Predicate,
      pb: InstructionOperand.Predicate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PsetRegPredPredPredDefinition, mods, pred, dst, pc, pb, pa)
  case PsetpPredPredPred(
      dstPredicate: InstructionOperand.Predicate,
      pc: InstructionOperand.Predicate,
      pb: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PsetpPredPredPredDefinition, mods, pred, dstPredicate, pc, pb)
  case PsetpPredPredPredPredPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      pc: InstructionOperand.Predicate,
      pb: InstructionOperand.Predicate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.PsetpPredPredPredPredPredDefinition, mods, pred, dstPredicate, nPd, pc, pb, pa)
  case Qadd4RegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Qadd4RegRegImmDefinition, mods, pred, dst, srcA, sc)
  case Qadd4RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Qadd4RegRegRegDefinition, mods, pred, dst, srcA, srcC)
  case Qfma4RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Qfma4RegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Qfma4RegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Qfma4RegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case Qfma4RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Qfma4RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case QgmmaRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegAddressRegDefinition, mods, pred, dst, srcA, srcC)
  case QgmmaRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegAddressRegPredDefinition, mods, pred, dst, srcA, srcC, uPp)
  case QgmmaRegAddressRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegAddressRegPredRegDefinition, mods, pred, dst, srcA, srcC, uPp, re)
  case QgmmaRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegAddressRegPredRegImmDefinition, mods, pred, dst, srcA, srcC, uPp, re, id)
  case QgmmaRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegAddressRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case QgmmaRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegAddressRegRegImmDefinition, mods, pred, dst, srcA, srcC, re, id)
  case QgmmaRegAddressRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegAddressRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case QgmmaRegAddressRegRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegAddressRegRegPredRegDefinition, mods, pred, dst, srcA, srcB, srcC, uPp, re)
  case QgmmaRegAddressRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.QgmmaRegAddressRegRegPredRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        id
      )
  case QgmmaRegAddressRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegAddressRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC, re)
  case QgmmaRegAddressRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegAddressRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, re, id)
  case QgmmaRegRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegRegAddressRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case QgmmaRegRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegRegAddressRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case QgmmaRegRegAddressRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegRegAddressRegPredRegDefinition, mods, pred, dst, srcA, srcB, srcC, uPp, re)
  case QgmmaRegRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.QgmmaRegRegAddressRegPredRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        id
      )
  case QgmmaRegRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegRegAddressRegRegDefinition, mods, pred, dst, srcA, srcB, srcC, re)
  case QgmmaRegRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QgmmaRegRegAddressRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, re, id)
  case QmmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QmmaRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case QmmaRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QmmaRegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case QmmaRegRegRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.QmmaRegRegRegRegPredRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        id
      )
  case QmmaRegRegRegRegPredRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.QmmaRegRegRegRegPredRegRegRegDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        rh,
        uRi
      )
  case QmmaRegRegRegRegPredRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      sparseId: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.QmmaRegRegRegRegPredRegRegRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        rh,
        uRi,
        sparseId
      )
  case QmmaRegRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QmmaRegRegRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, re, id)
  case QmmaRegRegRegRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QmmaRegRegRegRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC, re, rh, uRi)
  case QmmaRegRegRegRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      sparseId: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.QmmaRegRegRegRegRegRegRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        re,
        rh,
        uRi,
        sparseId
      )
  case Qmul4RegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Qmul4RegRegImmDefinition, mods, pred, dst, srcA, sb)
  case Qmul4RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Qmul4RegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case QspcPredAddress(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QspcPredAddressDefinition, mods, pred, pu, srcA)
  case QspcPredRegAddress(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QspcPredRegAddressDefinition, mods, pred, pu, dst, srcA)
  case QspcRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.QspcRegAddressDefinition, mods, pred, dst, srcA)
  case R2bImmReg(
      barname: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.R2bImmRegDefinition, mods, pred, barname, srcB)
  case R2pReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.R2pRegDefinition, mods, pred, srcA)
  case R2pRegConst(
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.R2pRegConstDefinition, mods, pred, srcA, srcConst)
  case R2pRegImm(
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.R2pRegImmDefinition, mods, pred, srcA, uImm)
  case R2pRegReg(
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.R2pRegRegDefinition, mods, pred, srcA, srcB)
  case R2pSymbolReg(
      pR: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.R2pSymbolRegDefinition, mods, pred, pR, srcA)
  case R2pSymbolRegAddressImm(
      pR: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.R2pSymbolRegAddressImmDefinition, mods, pred, pR, srcA, srcB, sb_offset)
  case R2pSymbolRegConst(
      pR: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.R2pSymbolRegConstDefinition, mods, pred, pR, srcA, sb)
  case R2pSymbolRegImm(
      pR: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.R2pSymbolRegImmDefinition, mods, pred, pR, srcA, sb)
  case R2pSymbolRegReg(
      pR: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.R2pSymbolRegRegDefinition, mods, pred, pR, srcA, srcB)
  case R2urPredRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.R2urPredRegRegDefinition, mods, pred, pu, dst, srcA)
  case R2urRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.R2urRegRegDefinition, mods, pred, dst, srcA)
  case RamNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.RamNoOperandsDefinition, mods, pred)
  case RedAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.RedAddressRegDefinition, mods, pred, srcA, srcB)
  case RedasAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.RedasAddressRegDefinition, mods, pred, srcA, srcB)
  case RedgAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.RedgAddressRegDefinition, mods, pred, srcA, srcB)
  case RedsAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.RedsAddressRegDefinition, mods, pred, srcA, srcB)
  case ReduxRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ReduxRegRegDefinition, mods, pred, dst, srcA)
  case RetNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.RetNoOperandsDefinition, mods, pred)
  case RetPredReg(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.RetPredRegDefinition, mods, pred, pp, srcA)
  case RetPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.RetPredRegImmDefinition, mods, pred, pp, srcA, ra_offset)
  case RetReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.RetRegDefinition, mods, pred, srcA)
  case RetRegImm(
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.RetRegImmDefinition, mods, pred, srcA, ra_offset)
  case RpcmovAddressImm(
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.RpcmovAddressImmDefinition, mods, pred, srcB, sb_offset)
  case RpcmovConst(sb: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.RpcmovConstDefinition, mods, pred, sb)
  case RpcmovImm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.RpcmovImmDefinition, mods, pred, sb)
  case RpcmovReg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.RpcmovRegDefinition, mods, pred, srcB)
  case RroRegConst(
      dst: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.RroRegConstDefinition, mods, pred, dst, srcConst)
  case RroRegImm(
      dst: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.RroRegImmDefinition, mods, pred, dst, uImm)
  case RroRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.RroRegRegDefinition, mods, pred, dst, srcB)
  case RttNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.RttNoOperandsDefinition, mods, pred)
  case S2rRegSpecial(
      dst: InstructionOperand.Register,
      sR: InstructionOperand.SpecialRegister,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.S2rRegSpecialDefinition, mods, pred, dst, sR)
  case S2urRegSpecial(
      dst: InstructionOperand.Register,
      sRa: InstructionOperand.SpecialRegister,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.S2urRegSpecialDefinition, mods, pred, dst, sRa)
  case SamNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.SamNoOperandsDefinition, mods, pred)
  case ScatterRegRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      vecidx: InstructionOperand.Immediate,
      mask: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ScatterRegRegRegRegImmImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        vecidx,
        mask
      )
  case SelRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SelRegRegAddressImmPredDefinition, mods, pred, dst, srcA, srcB, sb_offset, pp)
  case SelRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SelRegRegConstPredDefinition, mods, pred, dst, srcA, sb, pp)
  case SelRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SelRegRegImmPredDefinition, mods, pred, dst, srcA, sb, pp)
  case SelRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SelRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pa)
  case SetcrsptrReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.SetcrsptrRegDefinition, mods, pred, srcA)
  case SetctaidReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.SetctaidRegDefinition, mods, pred, srcA)
  case SetlmembaseReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.SetlmembaseRegDefinition, mods, pred, srcA)
  case SgxtRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SgxtRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case SgxtRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SgxtRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case SgxtRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SgxtRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case SgxtRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SgxtRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case ShfRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShfRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case ShfRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShfRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case ShfRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShfRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case ShfRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShfRegRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, srcC, sc_offset)
  case ShfRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShfRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, sc)
  case ShfRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShfRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case ShfRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShfRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case ShflPredRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm5: InstructionOperand.Immediate,
      uImm13: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShflPredRegRegImmImmDefinition, mods, pred, dstPredicate, dst, srcA, uImm5, uImm13)
  case ShflPredRegRegImmReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm5: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShflPredRegRegImmRegDefinition, mods, pred, dstPredicate, dst, srcA, uImm5, srcC)
  case ShflPredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uImm13: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShflPredRegRegRegImmDefinition, mods, pred, dstPredicate, dst, srcA, srcB, uImm13)
  case ShflPredRegRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShflPredRegRegRegRegDefinition, mods, pred, dstPredicate, dst, srcA, srcB, srcC)
  case ShintImmImm(
      uImmMsb: InstructionOperand.Immediate,
      uImmLsb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShintImmImmDefinition, mods, pred, uImmMsb, uImmLsb)
  case ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm(
      uImmA: InstructionOperand.Immediate,
      uImmB: InstructionOperand.Immediate,
      uImmC: InstructionOperand.Immediate,
      uImmD: InstructionOperand.Immediate,
      uImmE: InstructionOperand.Immediate,
      uImmF: InstructionOperand.Immediate,
      uImmG: InstructionOperand.Immediate,
      uImmH: InstructionOperand.Immediate,
      uImmI: InstructionOperand.Immediate,
      uImmJ: InstructionOperand.Immediate,
      uImmK: InstructionOperand.Immediate,
      uImmL: InstructionOperand.Immediate,
      uImmM: InstructionOperand.Immediate,
      uImmN: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImmDefinition,
        mods,
        pred,
        uImmA,
        uImmB,
        uImmC,
        uImmD,
        uImmE,
        uImmF,
        uImmG,
        uImmH,
        uImmI,
        uImmJ,
        uImmK,
        uImmL,
        uImmM,
        uImmN
      )
  case ShlRegImmReg(
      dst: InstructionOperand.Register,
      sa: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShlRegImmRegDefinition, mods, pred, dst, sa, srcB)
  case ShlRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShlRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case ShlRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShlRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case ShlRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShlRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case ShlRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShlRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case ShrRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShrRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case ShrRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShrRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case ShrRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShrRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case ShrRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ShrRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case SpmetadataRegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      vecidx: InstructionOperand.Immediate,
      mask: InstructionOperand.Immediate,
      destidx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SpmetadataRegRegRegImmImmImmDefinition,
        mods,
        pred,
        dst,
        srcB,
        srcC,
        vecidx,
        mask,
        destidx
      )
  case SsyConst(srcConst: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.SsyConstDefinition, mods, pred, srcConst)
  case SsyImm(sImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.SsyImmDefinition, mods, pred, sImm)
  case StAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.StAddressRegDefinition, mods, pred, srcA, srcB)
  case StAddressRegPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      plg: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.StAddressRegPredDefinition, mods, pred, srcA, srcB, plg)
  case StasAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.StasAddressRegDefinition, mods, pred, srcA, srcB)
  case StgAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.StgAddressRegDefinition, mods, pred, srcA, srcB)
  case StgAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      rb2: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.StgAddressRegRegDefinition, mods, pred, srcA, srcB, rb2)
  case StgAddressRegRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      rb2: InstructionOperand.Register,
      word_mask: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.StgAddressRegRegImmDefinition, mods, pred, srcA, srcB, rb2, word_mask)
  case StlAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.StlAddressRegDefinition, mods, pred, srcA, srcB)
  case StpImm(uImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.StpImmDefinition, mods, pred, uImm)
  case StsAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.StsAddressRegDefinition, mods, pred, srcA, srcB)
  case StsculPredAddressReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.StsculPredAddressRegDefinition, mods, pred, dstPredicate, srcA, srcB)
  case StsmAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.StsmAddressRegDefinition, mods, pred, srcA, srcB)
  case SttAddressReg(
      srcC: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SttAddressRegDefinition, mods, pred, srcC, srcB)
  case SttmAddressReg(
      srcC: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SttmAddressRegDefinition, mods, pred, srcC, srcB)
  case SuatomPredRegAddressRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      suid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuatomPredRegAddressRegImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcB,
        suid
      )
  case SuatomPredRegAddressRegImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuatomPredRegAddressRegImmImmDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcB,
        bank5,
        tsPtrIdx
      )
  case SuatomPredRegAddressRegImmImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuatomPredRegAddressRegImmImmImmDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case SuatomPredRegAddressRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuatomPredRegAddressRegRegDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcB,
        srcC
      )
  case SuatomPredRegAddressRegRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuatomPredRegAddressRegRegImmDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcB,
        srcC,
        tid8
      )
  case SuatomPredRegAddressRegRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuatomPredRegAddressRegRegRegDefinition, mods, pred, pu, dst, srcA, srcB, srcC, uRe)
  case SuatomPredRegAddressRegRegRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuatomPredRegAddressRegRegRegImmDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8
      )
  case SuatomPredRegAddressRegRegRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuatomPredRegAddressRegRegRegRegDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcB,
        srcC1,
        srcC2,
        uRe
      )
  case SuatomRegAddressRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      suid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuatomRegAddressRegImmDefinition, mods, pred, dst, srcA, srcB, suid)
  case SuatomRegAddressRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuatomRegAddressRegImmImmDefinition, mods, pred, dst, srcA, srcB, bank5, tsPtrIdx)
  case SuatomRegAddressRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuatomRegAddressRegImmImmImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case SuatomRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuatomRegAddressRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case SuatomRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuatomRegAddressRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, tid8)
  case SuatomRegAddressRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuatomRegAddressRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC, uRe)
  case SuatomRegAddressRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuatomRegAddressRegRegRegImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8
      )
  case SuatomRegAddressRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuatomRegAddressRegRegRegRegDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC1,
        srcC2,
        uRe
      )
  case SubfmPredRegRegConstReg(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SubfmPredRegRegConstRegDefinition, mods, pred, pcl, dst, srcA, srcConst, srcC)
  case SubfmPredRegRegImmReg(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SubfmPredRegRegImmRegDefinition, mods, pred, pcl, dst, srcA, uImm, srcC)
  case SubfmPredRegRegRegConst(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SubfmPredRegRegRegConstDefinition, mods, pred, pcl, dst, srcA, srcB, srcConst)
  case SubfmPredRegRegRegReg(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SubfmPredRegRegRegRegDefinition, mods, pred, pcl, dst, srcA, srcB, srcC)
  case SuclampPredRegRegConstImm(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuclampPredRegRegConstImmDefinition, mods, pred, pcl, dst, srcA, srcConst, sImm)
  case SuclampPredRegRegImmImm(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuclampPredRegRegImmImmDefinition, mods, pred, pcl, dst, srcA, uImm, sImm)
  case SuclampPredRegRegRegImm(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuclampPredRegRegRegImmDefinition, mods, pred, pcl, dst, srcA, srcB, sImm)
  case SueauRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SueauRegRegConstRegDefinition, mods, pred, dst, srcA, srcConst, srcC)
  case SueauRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SueauRegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case SueauRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SueauRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, srcConst)
  case SueauRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SueauRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case SuldPredRegAddressImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      suid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldPredRegAddressImmDefinition, mods, pred, dstPredicate, dst, srcA, suid)
  case SuldPredRegAddressImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldPredRegAddressImmImmDefinition, mods, pred, pu, dst, srcA, bank5, tsPtrIdx)
  case SuldPredRegAddressImmImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuldPredRegAddressImmImmImmDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        bank5,
        hdrtblbase6,
        tid8
      )
  case SuldPredRegAddressReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldPredRegAddressRegDefinition, mods, pred, dstPredicate, dst, srcA, srcC)
  case SuldPredRegAddressRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldPredRegAddressRegImmDefinition, mods, pred, pu, dst, srcA, srcC, tid8)
  case SuldPredRegAddressRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldPredRegAddressRegRegDefinition, mods, pred, pu, dst, srcA, srcC, uRe)
  case SuldPredRegAddressRegRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldPredRegAddressRegRegImmDefinition, mods, pred, pu, dst, srcA, srcC, uRe, tid8)
  case SuldPredRegAddressRegRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldPredRegAddressRegRegRegDefinition, mods, pred, pu, dst, srcA, srcC1, srcC2, uRe)
  case SuldRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      suid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldRegAddressImmDefinition, mods, pred, dst, srcA, suid)
  case SuldRegAddressImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldRegAddressImmImmDefinition, mods, pred, dst, srcA, bank5, tsPtrIdx)
  case SuldRegAddressImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldRegAddressImmImmImmDefinition, mods, pred, dst, srcA, bank5, hdrtblbase6, tid8)
  case SuldRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldRegAddressRegDefinition, mods, pred, dst, srcA, srcC)
  case SuldRegAddressRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldRegAddressRegImmDefinition, mods, pred, dst, srcA, srcC, tid8)
  case SuldRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldRegAddressRegRegDefinition, mods, pred, dst, srcA, srcC, uRe)
  case SuldRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldRegAddressRegRegImmDefinition, mods, pred, dst, srcA, srcC, uRe, tid8)
  case SuldRegAddressRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldRegAddressRegRegRegDefinition, mods, pred, dst, srcA, srcC1, srcC2, uRe)
  case SuldgaRegAddressConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcConst: InstructionOperand.ConstantBank,
      pcl: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldgaRegAddressConstPredDefinition, mods, pred, dst, srcA, srcConst, pcl)
  case SuldgaRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      pcl: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuldgaRegAddressRegPredDefinition, mods, pred, dst, srcA, srcB, pcl)
  case SuqueryPredRegAddressImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuqueryPredRegAddressImmImmDefinition, mods, pred, pu, dst, srcA, bank5, tsPtrIdx)
  case SuqueryPredRegAddressImmImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuqueryPredRegAddressImmImmImmDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        bank5,
        hdrtblbase6,
        tid8
      )
  case SuqueryPredRegAddressReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuqueryPredRegAddressRegDefinition, mods, pred, pu, dst, srcA, srcC)
  case SuqueryPredRegAddressRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuqueryPredRegAddressRegImmDefinition, mods, pred, pu, dst, srcA, srcC, tid8)
  case SuqueryPredRegAddressRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuqueryPredRegAddressRegRegDefinition, mods, pred, pu, dst, srcA, srcC, uRe)
  case SuqueryPredRegAddressRegRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuqueryPredRegAddressRegRegImmDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcC,
        uRe,
        tid8
      )
  case SuqueryPredRegAddressRegRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuqueryPredRegAddressRegRegRegDefinition,
        mods,
        pred,
        pu,
        dst,
        srcA,
        srcC1,
        srcC2,
        uRe
      )
  case SuqueryRegAddressImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuqueryRegAddressImmImmDefinition, mods, pred, dst, srcA, bank5, tsPtrIdx)
  case SuqueryRegAddressImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuqueryRegAddressImmImmImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        bank5,
        hdrtblbase6,
        tid8
      )
  case SuqueryRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuqueryRegAddressRegDefinition, mods, pred, dst, srcA, srcC)
  case SuqueryRegAddressRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuqueryRegAddressRegImmDefinition, mods, pred, dst, srcA, srcC, tid8)
  case SuqueryRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuqueryRegAddressRegRegDefinition, mods, pred, dst, srcA, srcC, uRe)
  case SuqueryRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuqueryRegAddressRegRegImmDefinition, mods, pred, dst, srcA, srcC, uRe, tid8)
  case SuqueryRegAddressRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuqueryRegAddressRegRegRegDefinition, mods, pred, dst, srcA, srcC1, srcC2, uRe)
  case SuredAddressRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      suid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuredAddressRegImmDefinition, mods, pred, srcA, srcB, suid)
  case SuredAddressRegImmImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuredAddressRegImmImmDefinition, mods, pred, srcA, srcB, bank5, tsPtrIdx)
  case SuredAddressRegImmImmImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.SuredAddressRegImmImmImmDefinition,
        mods,
        pred,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case SuredAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuredAddressRegRegDefinition, mods, pred, srcA, srcB, srcC)
  case SuredAddressRegRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuredAddressRegRegImmDefinition, mods, pred, srcA, srcB, srcC, tid8)
  case SuredAddressRegRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuredAddressRegRegRegDefinition, mods, pred, srcA, srcB, srcC, uRe)
  case SuredAddressRegRegRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuredAddressRegRegRegImmDefinition, mods, pred, srcA, srcB, srcC, uRe, tid8)
  case SuredAddressRegRegRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SuredAddressRegRegRegRegDefinition, mods, pred, srcA, srcB, srcC1, srcC2, uRe)
  case SustAddressRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      suid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SustAddressRegImmDefinition, mods, pred, srcA, srcB, suid)
  case SustAddressRegImmImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SustAddressRegImmImmDefinition, mods, pred, srcA, srcB, bank5, tsPtrIdx)
  case SustAddressRegImmImmImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SustAddressRegImmImmImmDefinition, mods, pred, srcA, srcB, bank5, hdrtblbase6, tid8)
  case SustAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SustAddressRegRegDefinition, mods, pred, srcA, srcB, srcC)
  case SustAddressRegRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SustAddressRegRegImmDefinition, mods, pred, srcA, srcB, srcC, tid8)
  case SustAddressRegRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SustAddressRegRegRegDefinition, mods, pred, srcA, srcB, srcC, uRe)
  case SustAddressRegRegRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SustAddressRegRegRegImmDefinition, mods, pred, srcA, srcB, srcC, uRe, tid8)
  case SustAddressRegRegRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SustAddressRegRegRegRegDefinition, mods, pred, srcA, srcB, srcC1, srcC2, uRe)
  case SustgaAddressConstRegPred(
      srcA: InstructionOperand.Address,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pcl: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SustgaAddressConstRegPredDefinition, mods, pred, srcA, srcConst, srcC, pcl)
  case SustgaAddressRegRegPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pcl: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SustgaAddressRegRegPredDefinition, mods, pred, srcA, srcB, srcC, pcl)
  case SyncNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.SyncNoOperandsDefinition, mods, pred)
  case SyncsNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.SyncsNoOperandsDefinition, mods, pred)
  case SyncsAddress(srcA: InstructionOperand.Address, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.SyncsAddressDefinition, mods, pred, srcA)
  case SyncsAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SyncsAddressRegDefinition, mods, pred, srcA, srcB)
  case SyncsPredAddressReg(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SyncsPredAddressRegDefinition, mods, pred, pu, srcA, srcB)
  case SyncsRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SyncsRegAddressDefinition, mods, pred, dst, srcA)
  case SyncsRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SyncsRegAddressRegDefinition, mods, pred, dst, srcA, srcB)
  case SyncsRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.SyncsRegAddressRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case TexPredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexPredRegRegRegImmDefinition, mods, pred, dstPredicate, dst, srcA, srcB, tid)
  case TexPredRegRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexPredRegRegRegImmImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcB,
        tid,
        smp
      )
  case TexPredRegRegRegImmImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexPredRegRegRegImmImmImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcB,
        tid,
        smp,
        wmsk
      )
  case TexPredRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexPredRegRegRegRegDefinition, mods, pred, pu, rd2, dst, srcA, srcB)
  case TexPredRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexPredRegRegRegRegImmDefinition, mods, pred, pu, rd2, dst, srcA, srcB, wmsk)
  case TexPredRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexPredRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        tsPtrIdx
      )
  case TexPredRegRegRegRegImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexPredRegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case TexPredRegRegRegRegImmImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexPredRegRegRegRegImmImmImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8,
        wmsk
      )
  case TexPredRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexPredRegRegRegRegRegDefinition, mods, pred, pu, rd2, dst, srcA, srcB, srcC)
  case TexPredRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexPredRegRegRegRegRegImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8
      )
  case TexPredRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexPredRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8,
        wmsk
      )
  case TexPredRegRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexPredRegRegRegRegRegRegDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe
      )
  case TexPredRegRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexPredRegRegRegRegRegRegImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8
      )
  case TexPredRegRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexPredRegRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8,
        wmsk
      )
  case TexRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, tid)
  case TexRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexRegRegRegImmImmDefinition, mods, pred, dst, srcA, srcB, tid, smp)
  case TexRegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexRegRegRegImmImmImmDefinition, mods, pred, dst, srcA, srcB, tid, smp, wmsk)
  case TexRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB)
  case TexRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, wmsk)
  case TexRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexRegRegRegRegImmImmDefinition, mods, pred, rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case TexRegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexRegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case TexRegRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexRegRegRegRegImmImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8,
        wmsk
      )
  case TexRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexRegRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB, srcC)
  case TexRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexRegRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, srcC, tid8)
  case TexRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8,
        wmsk
      )
  case TexRegRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexRegRegRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB, srcC, uRe)
  case TexRegRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexRegRegRegRegRegRegImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8
      )
  case TexRegRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TexRegRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8,
        wmsk
      )
  case TexdepbarImm(uImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.TexdepbarImmDefinition, mods, pred, uImm)
  case TexsRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TexsRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, tid)
  case TldPredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldPredRegRegRegImmDefinition, mods, pred, dstPredicate, dst, srcA, srcB, tid)
  case TldPredRegRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldPredRegRegRegImmImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcB,
        tid,
        wmsk
      )
  case TldPredRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldPredRegRegRegRegDefinition, mods, pred, pu, rd2, dst, srcA, srcB)
  case TldPredRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldPredRegRegRegRegImmDefinition, mods, pred, pu, rd2, dst, srcA, srcB, wmsk)
  case TldPredRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldPredRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        tsPtrIdx
      )
  case TldPredRegRegRegRegImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldPredRegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case TldPredRegRegRegRegImmImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldPredRegRegRegRegImmImmImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8,
        wmsk
      )
  case TldPredRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldPredRegRegRegRegRegDefinition, mods, pred, pu, rd2, dst, srcA, srcB, srcC)
  case TldPredRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldPredRegRegRegRegRegImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8
      )
  case TldPredRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldPredRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8,
        wmsk
      )
  case TldPredRegRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldPredRegRegRegRegRegRegDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe
      )
  case TldPredRegRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldPredRegRegRegRegRegRegImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8
      )
  case TldPredRegRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldPredRegRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8,
        wmsk
      )
  case TldRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, tid)
  case TldRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldRegRegRegImmImmDefinition, mods, pred, dst, srcA, srcB, tid, wmsk)
  case TldRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB)
  case TldRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, wmsk)
  case TldRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldRegRegRegRegImmImmDefinition, mods, pred, rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case TldRegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldRegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case TldRegRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldRegRegRegRegImmImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8,
        wmsk
      )
  case TldRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldRegRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB, srcC)
  case TldRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldRegRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, srcC, tid8)
  case TldRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8,
        wmsk
      )
  case TldRegRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldRegRegRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB, srcC, uRe)
  case TldRegRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldRegRegRegRegRegRegImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8
      )
  case TldRegRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TldRegRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8,
        wmsk
      )
  case Tld4PredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4PredRegRegRegImmDefinition, mods, pred, dstPredicate, dst, srcA, srcB, tid)
  case Tld4PredRegRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4PredRegRegRegImmImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcB,
        tid,
        smp
      )
  case Tld4PredRegRegRegImmImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4PredRegRegRegImmImmImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcB,
        tid,
        smp,
        wmsk
      )
  case Tld4PredRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4PredRegRegRegRegDefinition, mods, pred, pu, rd2, dst, srcA, srcB)
  case Tld4PredRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4PredRegRegRegRegImmDefinition, mods, pred, pu, rd2, dst, srcA, srcB, wmsk)
  case Tld4PredRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4PredRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        tsPtrIdx
      )
  case Tld4PredRegRegRegRegImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4PredRegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case Tld4PredRegRegRegRegImmImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4PredRegRegRegRegImmImmImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8,
        wmsk
      )
  case Tld4PredRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4PredRegRegRegRegRegDefinition, mods, pred, pu, rd2, dst, srcA, srcB, srcC)
  case Tld4PredRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4PredRegRegRegRegRegImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8
      )
  case Tld4PredRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4PredRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8,
        wmsk
      )
  case Tld4PredRegRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4PredRegRegRegRegRegRegDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe
      )
  case Tld4PredRegRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4PredRegRegRegRegRegRegImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8
      )
  case Tld4PredRegRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4PredRegRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8,
        wmsk
      )
  case Tld4RegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4RegRegRegImmDefinition, mods, pred, dst, srcA, srcB, tid)
  case Tld4RegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4RegRegRegImmImmDefinition, mods, pred, dst, srcA, srcB, tid, smp)
  case Tld4RegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4RegRegRegImmImmImmDefinition, mods, pred, dst, srcA, srcB, tid, smp, wmsk)
  case Tld4RegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4RegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB)
  case Tld4RegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4RegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, wmsk)
  case Tld4RegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4RegRegRegRegImmImmDefinition, mods, pred, rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case Tld4RegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4RegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case Tld4RegRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4RegRegRegRegImmImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8,
        wmsk
      )
  case Tld4RegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4RegRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB, srcC)
  case Tld4RegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4RegRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, srcC, tid8)
  case Tld4RegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4RegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8,
        wmsk
      )
  case Tld4RegRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4RegRegRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB, srcC, uRe)
  case Tld4RegRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4RegRegRegRegRegRegImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8
      )
  case Tld4RegRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Tld4RegRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8,
        wmsk
      )
  case Tld4sRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Tld4sRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, tid)
  case TldsRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TldsRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, tid)
  case TmmlRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TmmlRegRegImmDefinition, mods, pred, dst, srcA, tid)
  case TmmlRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TmmlRegRegImmImmDefinition, mods, pred, dst, srcA, tid, smp)
  case TmmlRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TmmlRegRegImmImmImmDefinition, mods, pred, dst, srcA, tid, smp, wmsk)
  case TmmlRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TmmlRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, tid)
  case TmmlRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TmmlRegRegRegImmImmDefinition, mods, pred, dst, srcA, srcB, tid, smp)
  case TmmlRegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TmmlRegRegRegImmImmImmDefinition, mods, pred, dst, srcA, srcB, tid, smp, wmsk)
  case TmmlRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TmmlRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB)
  case TmmlRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TmmlRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, wmsk)
  case TmmlRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TmmlRegRegRegRegImmImmDefinition, mods, pred, rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case TmmlRegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TmmlRegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case TmmlRegRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TmmlRegRegRegRegImmImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8,
        wmsk
      )
  case TmmlRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TmmlRegRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB, srcC)
  case TmmlRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TmmlRegRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, srcC, tid8)
  case TmmlRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TmmlRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8,
        wmsk
      )
  case TxaRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxaRegRegImmDefinition, mods, pred, dst, srcA, tid)
  case TxaRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxaRegRegImmImmDefinition, mods, pred, dst, srcA, tid, wmsk)
  case TxaRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxaRegRegImmImmImmDefinition, mods, pred, dst, srcA, tid, smp, wmsk)
  case TxdPredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdPredRegRegRegImmDefinition, mods, pred, dstPredicate, dst, srcA, srcB, tid)
  case TxdPredRegRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdPredRegRegRegImmImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcB,
        tid,
        smp
      )
  case TxdPredRegRegRegImmImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdPredRegRegRegImmImmImmDefinition,
        mods,
        pred,
        dstPredicate,
        dst,
        srcA,
        srcB,
        tid,
        smp,
        wmsk
      )
  case TxdPredRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdPredRegRegRegRegDefinition, mods, pred, pu, rd2, dst, srcA, srcB)
  case TxdPredRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdPredRegRegRegRegImmDefinition, mods, pred, pu, rd2, dst, srcA, srcB, wmsk)
  case TxdPredRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdPredRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        tsPtrIdx
      )
  case TxdPredRegRegRegRegImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdPredRegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case TxdPredRegRegRegRegImmImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdPredRegRegRegRegImmImmImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8,
        wmsk
      )
  case TxdPredRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdPredRegRegRegRegRegDefinition, mods, pred, pu, rd2, dst, srcA, srcB, srcC)
  case TxdPredRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdPredRegRegRegRegRegImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8
      )
  case TxdPredRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdPredRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8,
        wmsk
      )
  case TxdPredRegRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdPredRegRegRegRegRegRegDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe
      )
  case TxdPredRegRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdPredRegRegRegRegRegRegImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8
      )
  case TxdPredRegRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdPredRegRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        pu,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8,
        wmsk
      )
  case TxdRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, tid)
  case TxdRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdRegRegRegImmImmDefinition, mods, pred, dst, srcA, srcB, tid, smp)
  case TxdRegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdRegRegRegImmImmImmDefinition, mods, pred, dst, srcA, srcB, tid, smp, wmsk)
  case TxdRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB)
  case TxdRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, wmsk)
  case TxdRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdRegRegRegRegImmImmDefinition, mods, pred, rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case TxdRegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdRegRegRegRegImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8
      )
  case TxdRegRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdRegRegRegRegImmImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        bank5,
        hdrtblbase6,
        tid8,
        wmsk
      )
  case TxdRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdRegRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB, srcC)
  case TxdRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdRegRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcB, srcC, tid8)
  case TxdRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        tid8,
        wmsk
      )
  case TxdRegRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxdRegRegRegRegRegRegDefinition, mods, pred, rd2, dst, srcA, srcB, srcC, uRe)
  case TxdRegRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdRegRegRegRegRegRegImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8
      )
  case TxdRegRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxdRegRegRegRegRegRegImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        srcB,
        srcC,
        uRe,
        tid8,
        wmsk
      )
  case TxqRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxqRegRegImmDefinition, mods, pred, dst, srcA, tid)
  case TxqRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      query: InstructionOperand.Immediate,
      tid: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxqRegRegImmImmDefinition, mods, pred, dst, srcA, query, tid)
  case TxqRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      query: InstructionOperand.Immediate,
      tid: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxqRegRegImmImmImmDefinition, mods, pred, dst, srcA, query, tid, wmsk)
  case TxqRegRegImmImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      query: InstructionOperand.Immediate,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxqRegRegImmImmImmImmDefinition, mods, pred, dst, srcA, query, tid, smp, wmsk)
  case TxqRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxqRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, wmsk)
  case TxqRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxqRegRegRegImmImmImmDefinition, mods, pred, rd2, dst, srcA, bank5, tsPtrIdx, wmsk)
  case TxqRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.TxqRegRegRegImmImmImmImmDefinition,
        mods,
        pred,
        rd2,
        dst,
        srcA,
        bank5,
        hdrtblbase6,
        tid8,
        wmsk
      )
  case TxqRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxqRegRegRegRegImmDefinition, mods, pred, rd2, dst, srcA, srcC, wmsk)
  case TxqRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.TxqRegRegRegRegImmImmDefinition, mods, pred, rd2, dst, srcA, srcC, tid8, wmsk)
  case UblkcpAddressAddressReg(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UblkcpAddressAddressRegDefinition, mods, pred, srcB, srcA, srcC)
  case UblkcpAddressAddressRegAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UblkcpAddressAddressRegAddressDefinition, mods, pred, srcB, srcA, srcC, uRe)
  case UblkpfAddressReg(
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UblkpfAddressRegDefinition, mods, pred, srcA, srcC)
  case UblkpfAddressRegAddress(
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UblkpfAddressRegAddressDefinition, mods, pred, srcA, srcC, uRe)
  case UblkredAddressAddressReg(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UblkredAddressAddressRegDefinition, mods, pred, srcB, srcA, srcC)
  case UblkredAddressAddressRegAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UblkredAddressAddressRegAddressDefinition, mods, pred, srcB, srcA, srcC, uRe)
  case UbmskRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UbmskRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case UbmskRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UbmskRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case UbrevRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UbrevRegImmDefinition, mods, pred, dst, sb)
  case UbrevRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UbrevRegRegDefinition, mods, pred, dst, srcB)
  case UcgabararvNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UcgabararvNoOperandsDefinition, mods, pred)
  case UcgabargetReg(dst: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UcgabargetRegDefinition, mods, pred, dst)
  case UcgabarsetReg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UcgabarsetRegDefinition, mods, pred, srcB)
  case UcgabarwaitNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UcgabarwaitNoOperandsDefinition, mods, pred)
  case Ucgabar_ArvNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Ucgabar_ArvNoOperandsDefinition, mods, pred)
  case Ucgabar_WaitNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Ucgabar_WaitNoOperandsDefinition, mods, pred)
  case UcleaRegPredRegImmImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      constSizeU04: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UcleaRegPredRegImmImmDefinition, mods, pred, dst, uPu, srcA, sb, constSizeU04)
  case UcleaRegPredRegRegImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      constSizeU04: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UcleaRegPredRegRegImmDefinition, mods, pred, dst, uPu, srcA, srcB, constSizeU04)
  case Uf2fRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2fRegImmDefinition, mods, pred, dst, sb)
  case Uf2fRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2fRegRegDefinition, mods, pred, dst, srcB)
  case Uf2fpRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2fpRegImmDefinition, mods, pred, dst, sb)
  case Uf2fpRegImmReg(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2fpRegImmRegDefinition, mods, pred, dst, sb, srcC)
  case Uf2fpRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2fpRegRegDefinition, mods, pred, dst, srcB)
  case Uf2fpRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2fpRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case Uf2fpRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2fpRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Uf2fpRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2fpRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case Uf2fpRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2fpRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case Uf2fpRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2fpRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Uf2iRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2iRegImmDefinition, mods, pred, dst, sb)
  case Uf2iRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2iRegRegDefinition, mods, pred, dst, srcB)
  case Uf2ipRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2ipRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Uf2ipRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2ipRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case Uf2ipRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uf2ipRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case UfaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfaddRegRegImmDefinition, mods, pred, dst, srcA, sc)
  case UfaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfaddRegRegRegDefinition, mods, pred, dst, srcA, srcC)
  case UffmaRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UffmaRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case UffmaRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UffmaRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case UffmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UffmaRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case UfhaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfhaddRegRegRegDefinition, mods, pred, dst, srcA, srcC)
  case UfhfmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfhfmaRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case UfloRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfloRegImmDefinition, mods, pred, dst, sb)
  case UfloRegPredImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfloRegPredImmDefinition, mods, pred, dst, uPu, sb)
  case UfloRegPredReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfloRegPredRegDefinition, mods, pred, dst, uPu, srcB)
  case UfloRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfloRegRegDefinition, mods, pred, dst, srcB)
  case UfmnmxRegPredRegImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfmnmxRegPredRegImmPredDefinition, mods, pred, dst, uPu, srcA, sb, uPp)
  case UfmnmxRegPredRegRegPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfmnmxRegPredRegRegPredDefinition, mods, pred, dst, uPu, srcA, srcB, uPp)
  case UfmnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfmnmxRegRegImmPredDefinition, mods, pred, dst, srcA, sb, uPp)
  case UfmnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfmnmxRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, uPp)
  case UfmulRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfmulRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case UfmulRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfmulRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case UfrndRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfrndRegImmDefinition, mods, pred, dst, sb)
  case UfrndRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfrndRegRegDefinition, mods, pred, dst, srcB)
  case UfselRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfselRegRegImmPredDefinition, mods, pred, dst, srcA, sb, uPp)
  case UfselRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfselRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, uPp)
  case UfsetRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfsetRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case UfsetRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfsetRegRegImmPredDefinition, mods, pred, dst, srcA, sb, uPp)
  case UfsetRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfsetRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case UfsetRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfsetRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, uPp)
  case UfsetpPredPredRegImmPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfsetpPredPredRegImmPredDefinition, mods, pred, uPu, uPv, srcA, sb, uPp)
  case UfsetpPredPredRegRegPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfsetpPredPredRegRegPredDefinition, mods, pred, uPu, uPv, srcA, srcB, uPp)
  case UfsetpPredRegImm(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfsetpPredRegImmDefinition, mods, pred, uPu, srcA, sb)
  case UfsetpPredRegReg(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UfsetpPredRegRegDefinition, mods, pred, uPu, srcA, srcB)
  case UgetnextworkidAddressAddress(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UgetnextworkidAddressAddressDefinition, mods, pred, srcA, srcB)
  case Ui2fRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ui2fRegImmDefinition, mods, pred, dst, sb)
  case Ui2fRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ui2fRegRegDefinition, mods, pred, dst, srcB)
  case Ui2fpRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ui2fpRegImmDefinition, mods, pred, dst, sb)
  case Ui2fpRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ui2fpRegRegDefinition, mods, pred, dst, srcB)
  case Ui2iRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ui2iRegImmDefinition, mods, pred, dst, sb)
  case Ui2iRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ui2iRegRegDefinition, mods, pred, dst, srcB)
  case Ui2ipRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ui2ipRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Ui2ipRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ui2ipRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case UiabsRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UiabsRegImmDefinition, mods, pred, dst, sb)
  case UiabsRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UiabsRegRegDefinition, mods, pred, dst, srcB)
  case Uiadd3RegPredPredRegImmReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uiadd3RegPredPredRegImmRegDefinition, mods, pred, dst, uPu, uPv, srcA, sb, srcC)
  case Uiadd3RegPredPredRegImmRegPredPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Uiadd3RegPredPredRegImmRegPredPredDefinition,
        mods,
        pred,
        dst,
        uPu,
        uPv,
        srcA,
        sb,
        srcC,
        uPp,
        uPq
      )
  case Uiadd3RegPredPredRegRegReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uiadd3RegPredPredRegRegRegDefinition, mods, pred, dst, uPu, uPv, srcA, srcB, srcC)
  case Uiadd3RegPredPredRegRegRegPredPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Uiadd3RegPredPredRegRegRegPredPredDefinition,
        mods,
        pred,
        dst,
        uPu,
        uPv,
        srcA,
        srcB,
        srcC,
        uPp,
        uPq
      )
  case Uiadd3RegPredRegImmReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uiadd3RegPredRegImmRegDefinition, mods, pred, dst, uPu, srcA, sb, srcC)
  case Uiadd3RegPredRegImmRegPredPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Uiadd3RegPredRegImmRegPredPredDefinition,
        mods,
        pred,
        dst,
        uPu,
        srcA,
        sb,
        srcC,
        uPp,
        uPq
      )
  case Uiadd3RegPredRegRegReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uiadd3RegPredRegRegRegDefinition, mods, pred, dst, uPu, srcA, srcB, srcC)
  case Uiadd3RegPredRegRegRegPredPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Uiadd3RegPredRegRegRegPredPredDefinition,
        mods,
        pred,
        dst,
        uPu,
        srcA,
        srcB,
        srcC,
        uPp,
        uPq
      )
  case Uiadd3RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uiadd3RegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Uiadd3RegRegImmRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uiadd3RegRegImmRegPredPredDefinition, mods, pred, dst, srcA, sb, srcC, uPp, uPq)
  case Uiadd3RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uiadd3RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Uiadd3RegRegRegRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uiadd3RegRegRegRegPredPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp, uPq)
  case UimadRegPredRegImmReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimadRegPredRegImmRegDefinition, mods, pred, dst, uPu, srcA, sb, srcC)
  case UimadRegPredRegImmRegPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimadRegPredRegImmRegPredDefinition, mods, pred, dst, uPu, srcA, sb, srcC, uPp)
  case UimadRegPredRegRegReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimadRegPredRegRegRegDefinition, mods, pred, dst, uPu, srcA, srcB, srcC)
  case UimadRegPredRegRegRegPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimadRegPredRegRegRegPredDefinition, mods, pred, dst, uPu, srcA, srcB, srcC, uPp)
  case UimadRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimadRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case UimadRegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimadRegRegImmRegPredDefinition, mods, pred, dst, srcA, sb, srcC, uPp)
  case UimadRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimadRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case UimadRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimadRegRegRegImmPredDefinition, mods, pred, dst, srcA, srcB, sc, uPp)
  case UimadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimadRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case UimadRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimadRegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case UimnmxPredPredRegRegImmPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimnmxPredPredRegRegImmPredDefinition, mods, pred, uPu, uPv, dst, srcA, sb, uPp)
  case UimnmxPredPredRegRegImmPredPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.UimnmxPredPredRegRegImmPredPredDefinition,
        mods,
        pred,
        uPu,
        uPv,
        dst,
        srcA,
        sb,
        uPp,
        uPq
      )
  case UimnmxPredPredRegRegRegPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimnmxPredPredRegRegRegPredDefinition, mods, pred, uPu, uPv, dst, srcA, srcB, uPp)
  case UimnmxPredPredRegRegRegPredPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.UimnmxPredPredRegRegRegPredPredDefinition,
        mods,
        pred,
        uPu,
        uPv,
        dst,
        srcA,
        srcB,
        uPp,
        uPq
      )
  case UimnmxPredRegRegImmPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimnmxPredRegRegImmPredDefinition, mods, pred, uPu, dst, srcA, sb, uPp)
  case UimnmxPredRegRegImmPredPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimnmxPredRegRegImmPredPredDefinition, mods, pred, uPu, dst, srcA, sb, uPp, uPq)
  case UimnmxPredRegRegRegPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimnmxPredRegRegRegPredDefinition, mods, pred, uPu, dst, srcA, srcB, uPp)
  case UimnmxPredRegRegRegPredPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimnmxPredRegRegRegPredPredDefinition, mods, pred, uPu, dst, srcA, srcB, uPp, uPq)
  case UimnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimnmxRegRegImmPredDefinition, mods, pred, dst, srcA, sb, uPp)
  case UimnmxRegRegImmPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimnmxRegRegImmPredPredDefinition, mods, pred, dst, srcA, sb, uPp, uPq)
  case UimnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimnmxRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, uPp)
  case UimnmxRegRegRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UimnmxRegRegRegPredPredDefinition, mods, pred, dst, srcA, srcB, uPp, uPq)
  case UisetpPredPredRegImmPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UisetpPredPredRegImmPredDefinition, mods, pred, uPu, uPv, srcA, sb, uPp)
  case UisetpPredPredRegImmPredPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UisetpPredPredRegImmPredPredDefinition, mods, pred, uPu, uPv, srcA, sb, uPp, uPr)
  case UisetpPredPredRegRegPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UisetpPredPredRegRegPredDefinition, mods, pred, uPu, uPv, srcA, srcB, uPp)
  case UisetpPredPredRegRegPredPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UisetpPredPredRegRegPredPredDefinition, mods, pred, uPu, uPv, srcA, srcB, uPp, uPr)
  case UisetpPredRegImm(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UisetpPredRegImmDefinition, mods, pred, uPu, srcA, sb)
  case UisetpPredRegImmPred(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UisetpPredRegImmPredDefinition, mods, pred, uPu, srcA, sb, uPr)
  case UisetpPredRegReg(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UisetpPredRegRegDefinition, mods, pred, uPu, srcA, srcB)
  case UisetpPredRegRegPred(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UisetpPredRegRegPredDefinition, mods, pred, uPu, srcA, srcB, uPr)
  case UldcRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UldcRegAddressDefinition, mods, pred, dst, srcA)
  case UldcRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      sa_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UldcRegAddressImmDefinition, mods, pred, dst, srcA, sa_offset)
  case UldcRegAddressPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      uPx: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UldcRegAddressPredDefinition, mods, pred, dst, srcA, uPx)
  case UldcRegConst(
      dst: InstructionOperand.Register,
      sa: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UldcRegConstDefinition, mods, pred, dst, sa)
  case UleaRegPredRegImmImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegPredRegImmImmDefinition, mods, pred, dst, uPu, srcA, sb, scaleU5)
  case UleaRegPredRegImmImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegPredRegImmImmPredDefinition, mods, pred, dst, uPu, srcA, sb, scaleU5, uPp)
  case UleaRegPredRegImmRegImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegPredRegImmRegImmDefinition, mods, pred, dst, uPu, srcA, sb, srcC, scaleU5)
  case UleaRegPredRegImmRegImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.UleaRegPredRegImmRegImmPredDefinition,
        mods,
        pred,
        dst,
        uPu,
        srcA,
        sb,
        srcC,
        scaleU5,
        uPp
      )
  case UleaRegPredRegRegImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegPredRegRegImmDefinition, mods, pred, dst, uPu, srcA, srcB, scaleU5)
  case UleaRegPredRegRegImmImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegPredRegRegImmImmDefinition, mods, pred, dst, uPu, srcA, srcB, sc, scaleU5)
  case UleaRegPredRegRegImmImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.UleaRegPredRegRegImmImmPredDefinition,
        mods,
        pred,
        dst,
        uPu,
        srcA,
        srcB,
        sc,
        scaleU5,
        uPp
      )
  case UleaRegPredRegRegImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegPredRegRegImmPredDefinition, mods, pred, dst, uPu, srcA, srcB, scaleU5, uPp)
  case UleaRegPredRegRegRegImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegPredRegRegRegImmDefinition, mods, pred, dst, uPu, srcA, srcB, srcC, scaleU5)
  case UleaRegPredRegRegRegImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.UleaRegPredRegRegRegImmPredDefinition,
        mods,
        pred,
        dst,
        uPu,
        srcA,
        srcB,
        srcC,
        scaleU5,
        uPp
      )
  case UleaRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegRegImmImmDefinition, mods, pred, dst, srcA, sb, scaleU5)
  case UleaRegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegRegImmImmPredDefinition, mods, pred, dst, srcA, sb, scaleU5, uPp)
  case UleaRegRegImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegRegImmRegImmDefinition, mods, pred, dst, srcA, sb, srcC, scaleU5)
  case UleaRegRegImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegRegImmRegImmPredDefinition, mods, pred, dst, srcA, sb, srcC, scaleU5, uPp)
  case UleaRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, scaleU5)
  case UleaRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegRegRegImmImmDefinition, mods, pred, dst, srcA, srcB, sc, scaleU5)
  case UleaRegRegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegRegRegImmImmPredDefinition, mods, pred, dst, srcA, srcB, sc, scaleU5, uPp)
  case UleaRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegRegRegImmPredDefinition, mods, pred, dst, srcA, srcB, scaleU5, uPp)
  case UleaRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, scaleU5)
  case UleaRegRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UleaRegRegRegRegImmPredDefinition, mods, pred, dst, srcA, srcB, srcC, scaleU5, uPp)
  case UlepcReg(dst: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UlepcRegDefinition, mods, pred, dst)
  case UlepcRegImm(
      dst: InstructionOperand.Register,
      sImm58: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UlepcRegImmDefinition, mods, pred, dst, sImm58)
  case UlopPredRegRegImm(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UlopPredRegRegImmDefinition, mods, pred, uPu, dst, srcA, sb)
  case UlopPredRegRegImmPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UlopPredRegRegImmPredDefinition, mods, pred, uPu, dst, srcA, sb, uPp)
  case UlopPredRegRegReg(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UlopPredRegRegRegDefinition, mods, pred, uPu, dst, srcA, srcB)
  case UlopPredRegRegRegPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UlopPredRegRegRegPredDefinition, mods, pred, uPu, dst, srcA, srcB, uPp)
  case UlopRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UlopRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case UlopRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UlopRegRegImmPredDefinition, mods, pred, dst, srcA, sb, uPp)
  case UlopRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UlopRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case UlopRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UlopRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, uPp)
  case Ulop3PredRegRegImmReg(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3PredRegRegImmRegDefinition, mods, pred, uPu, dst, srcA, sb, srcC)
  case Ulop3PredRegRegImmRegImm(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3PredRegRegImmRegImmDefinition, mods, pred, uPu, dst, srcA, sb, srcC, imm8)
  case Ulop3PredRegRegImmRegImmPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Ulop3PredRegRegImmRegImmPredDefinition,
        mods,
        pred,
        uPu,
        dst,
        srcA,
        sb,
        srcC,
        imm8,
        uPp
      )
  case Ulop3PredRegRegImmRegPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3PredRegRegImmRegPredDefinition, mods, pred, uPu, dst, srcA, sb, srcC, uPp)
  case Ulop3PredRegRegRegReg(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3PredRegRegRegRegDefinition, mods, pred, uPu, dst, srcA, srcB, srcC)
  case Ulop3PredRegRegRegRegImm(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3PredRegRegRegRegImmDefinition, mods, pred, uPu, dst, srcA, srcB, srcC, imm8)
  case Ulop3PredRegRegRegRegImmPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Ulop3PredRegRegRegRegImmPredDefinition,
        mods,
        pred,
        uPu,
        dst,
        srcA,
        srcB,
        srcC,
        imm8,
        uPp
      )
  case Ulop3PredRegRegRegRegPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3PredRegRegRegRegPredDefinition, mods, pred, uPu, dst, srcA, srcB, srcC, uPp)
  case Ulop3RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3RegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Ulop3RegRegImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3RegRegImmRegImmDefinition, mods, pred, dst, srcA, sb, srcC, imm8)
  case Ulop3RegRegImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3RegRegImmRegImmPredDefinition, mods, pred, dst, srcA, sb, srcC, imm8, uPp)
  case Ulop3RegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3RegRegImmRegPredDefinition, mods, pred, dst, srcA, sb, srcC, uPp)
  case Ulop3RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Ulop3RegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3RegRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, srcC, imm8)
  case Ulop3RegRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3RegRegRegRegImmPredDefinition, mods, pred, dst, srcA, srcB, srcC, imm8, uPp)
  case Ulop3RegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop3RegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, uPp)
  case Ulop32iPredRegRegImm(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop32iPredRegRegImmDefinition, mods, pred, uPu, dst, srcA, sb)
  case Ulop32iPredRegRegImmPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop32iPredRegRegImmPredDefinition, mods, pred, uPu, dst, srcA, sb, uPp)
  case Ulop32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop32iRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case Ulop32iRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ulop32iRegRegImmPredDefinition, mods, pred, dst, srcA, sb, uPp)
  case UmemsetsAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UmemsetsAddressRegRegDefinition, mods, pred, srcA, srcB, srcC)
  case UmovRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UmovRegImmDefinition, mods, pred, dst, sb)
  case UmovRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UmovRegRegDefinition, mods, pred, dst, srcB)
  case Up2urReg(dst: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Up2urRegDefinition, mods, pred, dst)
  case Up2urRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Up2urRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case Up2urRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Up2urRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case Uplop3PredPredPredPred(
      uPu: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uplop3PredPredPredPredDefinition, mods, pred, uPu, uPp, uPq, uPr)
  case Uplop3PredPredPredPredImm(
      uPu: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uplop3PredPredPredPredImmDefinition, mods, pred, uPu, uPp, uPq, uPr, uimm8)
  case Uplop3PredPredPredPredPredImmImm(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Uplop3PredPredPredPredPredImmImmDefinition,
        mods,
        pred,
        uPu,
        uPv,
        uPp,
        uPq,
        uPr,
        uimm8,
        vimm8
      )
  case Uplop3PredPredPredRegPredImmImm(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      uPr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Uplop3PredPredPredRegPredImmImmDefinition,
        mods,
        pred,
        uPu,
        uPv,
        uPp,
        srcB,
        uPr,
        uimm8,
        vimm8
      )
  case Uplop3PredPredPredRegRegImmImm(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Uplop3PredPredPredRegRegImmImmDefinition,
        mods,
        pred,
        uPu,
        uPv,
        uPp,
        srcB,
        srcC,
        uimm8,
        vimm8
      )
  case Uplop3PredPredRegPredImm(
      uPu: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      uPr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uplop3PredPredRegPredImmDefinition, mods, pred, uPu, uPp, srcB, uPr, uimm8)
  case Uplop3PredPredRegRegImm(
      uPu: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uplop3PredPredRegRegImmDefinition, mods, pred, uPu, uPp, srcB, srcC, uimm8)
  case Uplop3PredPredRegRegRegImmImm(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Uplop3PredPredRegRegRegImmImmDefinition,
        mods,
        pred,
        uPu,
        uPv,
        srcA,
        srcB,
        srcC,
        uimm8,
        vimm8
      )
  case Uplop3PredRegRegRegImm(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Uplop3PredRegRegRegImmDefinition, mods, pred, uPu, srcA, srcB, srcC, uimm8)
  case UpopcRegImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UpopcRegImmDefinition, mods, pred, dst, sb)
  case UpopcRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UpopcRegRegDefinition, mods, pred, dst, srcB)
  case UprmtRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UprmtRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case UprmtRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UprmtRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case UpsetpPredPredPred(
      uPu: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UpsetpPredPredPredDefinition, mods, pred, uPu, uPp, uPq)
  case UpsetpPredPredPredPredPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UpsetpPredPredPredPredPredDefinition, mods, pred, uPu, uPv, uPp, uPq, uPr)
  case Ur2upReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Ur2upRegDefinition, mods, pred, srcA)
  case Ur2upRegImm(
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ur2upRegImmDefinition, mods, pred, srcA, sb)
  case Ur2upRegReg(
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Ur2upRegRegDefinition, mods, pred, srcA, srcB)
  case UredgrAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UredgrAddressRegDefinition, mods, pred, srcA, srcB)
  case UselRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UselRegRegImmPredDefinition, mods, pred, dst, srcA, sb, uPp)
  case UselRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UselRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, uPp)
  case UsetmaxregImm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UsetmaxregImmDefinition, mods, pred, sb)
  case UsetmaxregPredImm(
      uPu: InstructionOperand.Predicate,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UsetmaxregPredImmDefinition, mods, pred, uPu, sb)
  case UsetmaxregPredReg(
      uPu: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UsetmaxregPredRegDefinition, mods, pred, uPu, srcB)
  case UsetmaxregReg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UsetmaxregRegDefinition, mods, pred, srcB)
  case UsetshmszNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UsetshmszNoOperandsDefinition, mods, pred)
  case UsetshmszImm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UsetshmszImmDefinition, mods, pred, sb)
  case UsetshmszReg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UsetshmszRegDefinition, mods, pred, srcB)
  case UsgxtRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UsgxtRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case UsgxtRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UsgxtRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case UshfRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UshfRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case UshfRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UshfRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case UshfRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UshfRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case UshlRegImmReg(
      dst: InstructionOperand.Register,
      sa: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UshlRegImmRegDefinition, mods, pred, dst, sa, srcB)
  case UshlRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UshlRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case UshlRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UshlRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case UshrRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UshrRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case UshrRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UshrRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case UstgrAddressReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UstgrAddressRegDefinition, mods, pred, srcA, srcB)
  case UtcatomswsPredRegReg(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtcatomswsPredRegRegDefinition, mods, pred, uPu, dst, srcB)
  case UtcatomswsPredRegRegReg(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtcatomswsPredRegRegRegDefinition, mods, pred, uPu, dst, srcB, srcC)
  case UtcatomswsRegReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtcatomswsRegRegDefinition, mods, pred, dst, srcB)
  case UtcbarNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UtcbarNoOperandsDefinition, mods, pred)
  case UtcbarAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtcbarAddressRegRegDefinition, mods, pred, srcA, srcB, srcC)
  case UtccpAddressAddress(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtccpAddressAddressDefinition, mods, pred, srcA, srcB)
  case UtchmmaAddressAddressAddressAddressAddressRegPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.UtchmmaAddressAddressAddressAddressAddressRegPredDefinition,
        mods,
        pred,
        srcA,
        srcB,
        srcC,
        uRe,
        uRh,
        uRi,
        uPp
      )
  case UtchmmaAddressAddressAddressAddressAddressRegPredImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      scaleU4: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.UtchmmaAddressAddressAddressAddressAddressRegPredImmDefinition,
        mods,
        pred,
        srcA,
        srcB,
        srcC,
        uRe,
        uRh,
        uRi,
        uPp,
        scaleU4
      )
  case UtcimmaAddressAddressAddressAddressAddressRegPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.UtcimmaAddressAddressAddressAddressAddressRegPredDefinition,
        mods,
        pred,
        srcA,
        srcB,
        srcC,
        uRe,
        uRh,
        uRi,
        uPp
      )
  case UtcommaAddressAddressAddressAddressAddressAddressPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.UtcommaAddressAddressAddressAddressAddressAddressPredDefinition,
        mods,
        pred,
        srcA,
        srcB,
        srcC,
        uRe,
        uRh,
        uRi,
        uPp
      )
  case UtcqmmaAddressAddressAddressAddressAddressAddressPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.UtcqmmaAddressAddressAddressAddressAddressAddressPredDefinition,
        mods,
        pred,
        srcA,
        srcB,
        srcC,
        uRe,
        uRh,
        uRi,
        uPp
      )
  case UtcqmmaAddressAddressAddressAddressAddressRegPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.UtcqmmaAddressAddressAddressAddressAddressRegPredDefinition,
        mods,
        pred,
        srcA,
        srcB,
        srcC,
        uRe,
        uRh,
        uRi,
        uPp
      )
  case UtcshiftAddress(srcA: InstructionOperand.Address, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UtcshiftAddressDefinition, mods, pred, srcA)
  case UtmacctlNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UtmacctlNoOperandsDefinition, mods, pred)
  case UtmacctlAddress(srcA: InstructionOperand.Address, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UtmacctlAddressDefinition, mods, pred, srcA)
  case UtmacmdflushNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UtmacmdflushNoOperandsDefinition, mods, pred)
  case UtmaldgAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmaldgAddressAddressDefinition, mods, pred, srcB, srcA)
  case UtmaldgAddressAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmaldgAddressAddressAddressDefinition, mods, pred, srcB, srcA, uRe)
  case UtmaldgAddressAddressReg(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmaldgAddressAddressRegDefinition, mods, pred, srcB, srcA, srcC)
  case UtmaldgAddressAddressRegAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmaldgAddressAddressRegAddressDefinition, mods, pred, srcB, srcA, srcC, uRe)
  case UtmapfAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmapfAddressAddressDefinition, mods, pred, srcB, srcA)
  case UtmapfAddressAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmapfAddressAddressAddressDefinition, mods, pred, srcB, srcA, uRe)
  case UtmapfAddressAddressReg(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmapfAddressAddressRegDefinition, mods, pred, srcB, srcA, srcC)
  case UtmapfAddressAddressRegAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmapfAddressAddressRegAddressDefinition, mods, pred, srcB, srcA, srcC, uRe)
  case UtmaredgAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmaredgAddressAddressDefinition, mods, pred, srcB, srcA)
  case UtmaredgAddressAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmaredgAddressAddressAddressDefinition, mods, pred, srcB, srcA, uRe)
  case UtmastgAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmastgAddressAddressDefinition, mods, pred, srcB, srcA)
  case UtmastgAddressAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UtmastgAddressAddressAddressDefinition, mods, pred, srcB, srcA, uRe)
  case UviaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UviaddRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case UviaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UviaddRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case UvimnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UvimnmxRegRegImmPredDefinition, mods, pred, dst, srcA, sb, uPp)
  case UvimnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.UvimnmxRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, uPp)
  case UvirtcountImm(sa: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UvirtcountImmDefinition, mods, pred, sa)
  case UvirtcountReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.UvirtcountRegDefinition, mods, pred, srcA)
  case VabsdiffRegPredRegAddressImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.VabsdiffRegPredRegAddressImmRegDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        srcC
      )
  case VabsdiffRegPredRegConstReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegPredRegConstRegDefinition, mods, pred, dst, pu, srcA, sb, srcC)
  case VabsdiffRegPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegPredRegImmRegDefinition, mods, pred, dst, pu, srcA, sb, srcC)
  case VabsdiffRegPredRegRegAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.VabsdiffRegPredRegRegAddressImmDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        srcC,
        sc_offset
      )
  case VabsdiffRegPredRegRegConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegPredRegRegConstDefinition, mods, pred, dst, pu, srcA, srcB, sc)
  case VabsdiffRegPredRegRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegPredRegRegImmDefinition, mods, pred, dst, pu, srcA, srcB, sc)
  case VabsdiffRegPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegPredRegRegRegDefinition, mods, pred, dst, pu, srcA, srcB, srcC)
  case VabsdiffRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegRegAddressImmRegDefinition, mods, pred, dst, srcA, srcB, sb_offset, srcC)
  case VabsdiffRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case VabsdiffRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case VabsdiffRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, srcC, sc_offset)
  case VabsdiffRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, sc)
  case VabsdiffRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case VabsdiffRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VabsdiffRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Vabsdiff2RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff2RegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case Vabsdiff2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff2RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Vabsdiff4RegPredRegAddressImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Vabsdiff4RegPredRegAddressImmRegDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        sb_offset,
        srcC
      )
  case Vabsdiff4RegPredRegConstReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff4RegPredRegConstRegDefinition, mods, pred, dst, pu, srcA, sb, srcC)
  case Vabsdiff4RegPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff4RegPredRegImmRegDefinition, mods, pred, dst, pu, srcA, sb, srcC)
  case Vabsdiff4RegPredRegRegAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Vabsdiff4RegPredRegRegAddressImmDefinition,
        mods,
        pred,
        dst,
        pu,
        srcA,
        srcB,
        srcC,
        sc_offset
      )
  case Vabsdiff4RegPredRegRegConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff4RegPredRegRegConstDefinition, mods, pred, dst, pu, srcA, srcB, sc)
  case Vabsdiff4RegPredRegRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff4RegPredRegRegImmDefinition, mods, pred, dst, pu, srcA, srcB, sc)
  case Vabsdiff4RegPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff4RegPredRegRegRegDefinition, mods, pred, dst, pu, srcA, srcB, srcC)
  case Vabsdiff4RegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Vabsdiff4RegRegAddressImmRegDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC
      )
  case Vabsdiff4RegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff4RegRegConstRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Vabsdiff4RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff4RegRegImmRegDefinition, mods, pred, dst, srcA, sb, srcC)
  case Vabsdiff4RegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Vabsdiff4RegRegRegAddressImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        sc_offset
      )
  case Vabsdiff4RegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff4RegRegRegConstDefinition, mods, pred, dst, srcA, srcB, sc)
  case Vabsdiff4RegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff4RegRegRegImmDefinition, mods, pred, dst, srcA, srcB, sc)
  case Vabsdiff4RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vabsdiff4RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case VaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VaddRegRegRegDefinition, mods, pred, dst, srcA, srcC)
  case VaddRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VaddRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Vadd2RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vadd2RegRegRegDefinition, mods, pred, dst, srcA, srcC)
  case Vadd2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vadd2RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Vadd4RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vadd4RegRegRegDefinition, mods, pred, dst, srcA, srcC)
  case Vadd4RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vadd4RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case VhmnmxRegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.VhmnmxRegRegAddressImmRegPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        pp
      )
  case VhmnmxRegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VhmnmxRegRegConstRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case VhmnmxRegRegImmImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VhmnmxRegRegImmImmRegPredDefinition, mods, pred, dst, srcA, sb, sb1, srcC, pp)
  case VhmnmxRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VhmnmxRegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, pp)
  case ViaddRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ViaddRegRegAddressImmDefinition, mods, pred, dst, srcA, srcB, sb_offset)
  case ViaddRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ViaddRegRegConstDefinition, mods, pred, dst, srcA, sb)
  case ViaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ViaddRegRegImmDefinition, mods, pred, dst, srcA, sb)
  case ViaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ViaddRegRegRegDefinition, mods, pred, dst, srcA, srcB)
  case ViaddmnmxRegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ViaddmnmxRegRegAddressImmRegPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        pp
      )
  case ViaddmnmxRegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ViaddmnmxRegRegConstRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case ViaddmnmxRegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ViaddmnmxRegRegImmRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case ViaddmnmxRegRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.ViaddmnmxRegRegRegAddressImmPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        sc_offset,
        pp
      )
  case ViaddmnmxRegRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ViaddmnmxRegRegRegConstPredDefinition, mods, pred, dst, srcA, srcB, sc, pp)
  case ViaddmnmxRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ViaddmnmxRegRegRegImmPredDefinition, mods, pred, dst, srcA, srcB, sc, pp)
  case ViaddmnmxRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.ViaddmnmxRegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, pp)
  case VildRegAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VildRegAddressDefinition, mods, pred, dst, srcA)
  case VimnmxRegPredPredRegAddressImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.VimnmxRegPredPredRegAddressImmPredDefinition,
        mods,
        pred,
        dst,
        pu,
        pv,
        srcA,
        srcB,
        sb_offset,
        pp
      )
  case VimnmxRegPredPredRegConstPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VimnmxRegPredPredRegConstPredDefinition, mods, pred, dst, pu, pv, srcA, sb, pp)
  case VimnmxRegPredPredRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VimnmxRegPredPredRegImmPredDefinition, mods, pred, dst, pu, pv, srcA, sb, pp)
  case VimnmxRegPredPredRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VimnmxRegPredPredRegRegPredDefinition, mods, pred, dst, pu, pv, srcA, srcB, pp)
  case VimnmxRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VimnmxRegRegAddressImmPredDefinition, mods, pred, dst, srcA, srcB, sb_offset, pp)
  case VimnmxRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VimnmxRegRegConstPredDefinition, mods, pred, dst, srcA, sb, pp)
  case VimnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VimnmxRegRegImmPredDefinition, mods, pred, dst, srcA, sb, pp)
  case VimnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VimnmxRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pp)
  case Vimnmx3RegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Vimnmx3RegRegAddressImmRegPredDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        sb_offset,
        srcC,
        pp
      )
  case Vimnmx3RegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vimnmx3RegRegConstRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case Vimnmx3RegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vimnmx3RegRegImmRegPredDefinition, mods, pred, dst, srcA, sb, srcC, pp)
  case Vimnmx3RegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vimnmx3RegRegRegRegPredDefinition, mods, pred, dst, srcA, srcB, srcC, pp)
  case VmadRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VmadRegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case VmadRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VmadRegRegRegDefinition, mods, pred, dst, srcA, srcC)
  case VmadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VmadRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case VmnmxRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VmnmxRegRegImmRegDefinition, mods, pred, dst, srcA, sImm, srcC)
  case VmnmxRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VmnmxRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Vmnmx2RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vmnmx2RegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case Vmnmx2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vmnmx2RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Vmnmx4RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vmnmx4RegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case Vmnmx4RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vmnmx4RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case VoteImm(uImm: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.VoteImmDefinition, mods, pred, uImm)
  case VotePredPred(
      dstPredicate: InstructionOperand.Predicate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VotePredPredDefinition, mods, pred, dstPredicate, pa)
  case VoteRegPredPred(
      dst: InstructionOperand.Register,
      dstPredicate: InstructionOperand.Predicate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VoteRegPredPredDefinition, mods, pred, dst, dstPredicate, pa)
  case VoteuRegPredPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VoteuRegPredPredDefinition, mods, pred, dst, uPu, pp)
  case VoteVtgImm(sa: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.VoteVtgImmDefinition, mods, pred, sa)
  case VoteVtgPredImm(
      pu: InstructionOperand.Predicate,
      sa: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VoteVtgPredImmDefinition, mods, pred, pu, sa)
  case VoteVtgPredPredImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      sa: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VoteVtgPredPredImmPredDefinition, mods, pred, pu, pv, sa, pp)
  case Vsel2RegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vsel2RegRegImmPredDefinition, mods, pred, dst, srcA, uImm, pa)
  case Vsel2RegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vsel2RegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pa)
  case Vsel4RegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vsel4RegRegImmPredDefinition, mods, pred, dst, srcA, uImm, pa)
  case Vsel4RegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vsel4RegRegRegPredDefinition, mods, pred, dst, srcA, srcB, pa)
  case VsetRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VsetRegRegImmRegDefinition, mods, pred, dst, srcA, sImm, srcC)
  case VsetRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VsetRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Vset2RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vset2RegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case Vset2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vset2RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Vset4RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vset4RegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case Vset4RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Vset4RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case VsetpPredPredRegImmPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VsetpPredPredRegImmPredDefinition, mods, pred, dstPredicate, nPd, srcA, sImm, pa)
  case VsetpPredPredRegRegPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VsetpPredPredRegRegPredDefinition, mods, pred, dstPredicate, nPd, srcA, srcB, pa)
  case VsetpPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VsetpPredRegImmDefinition, mods, pred, dstPredicate, srcA, sImm)
  case VsetpPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VsetpPredRegRegDefinition, mods, pred, dstPredicate, srcA, srcB)
  case VshlRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VshlRegRegImmRegDefinition, mods, pred, dst, srcA, sImm, srcC)
  case VshlRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VshlRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case VshrRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VshrRegRegImmRegDefinition, mods, pred, dst, srcA, sImm, srcC)
  case VshrRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.VshrRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case WarpgroupNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.WarpgroupNoOperandsDefinition, mods, pred)
  case WarpgroupImm(gsbcnt: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.WarpgroupImmDefinition, mods, pred, gsbcnt)
  case WarpgroupsetReg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.WarpgroupsetRegDefinition, mods, pred, srcB)
  case WarpsyncNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.WarpsyncNoOperandsDefinition, mods, pred)
  case WarpsyncAddressImm(
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.WarpsyncAddressImmDefinition, mods, pred, srcB, sb_offset)
  case WarpsyncConst(sb: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.WarpsyncConstDefinition, mods, pred, sb)
  case WarpsyncImm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.WarpsyncImmDefinition, mods, pred, sb)
  case WarpsyncPred(pp: InstructionOperand.Predicate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.WarpsyncPredDefinition, mods, pred, pp)
  case WarpsyncPredAddressImm(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.WarpsyncPredAddressImmDefinition, mods, pred, pp, srcB, sb_offset)
  case WarpsyncPredConst(
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.WarpsyncPredConstDefinition, mods, pred, pp, sb)
  case WarpsyncPredImm(
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.WarpsyncPredImmDefinition, mods, pred, pp, sb)
  case WarpsyncPredReg(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.WarpsyncPredRegDefinition, mods, pred, pp, srcA)
  case WarpsyncPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.WarpsyncPredRegImmDefinition, mods, pred, pp, srcA, sImm)
  case WarpsyncReg(srcA: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.WarpsyncRegDefinition, mods, pred, srcA)
  case WarpsyncRegImm(
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.WarpsyncRegImmDefinition, mods, pred, srcA, sImm)
  case XmadRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.XmadRegRegConstRegDefinition, mods, pred, dst, srcA, srcConst, srcC)
  case XmadRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.XmadRegRegImmRegDefinition, mods, pred, dst, srcA, uImm, srcC)
  case XmadRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.XmadRegRegRegConstDefinition, mods, pred, dst, srcA, srcB, srcConst)
  case XmadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.XmadRegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case YieldNoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.YieldNoOperandsDefinition, mods, pred)
  case YieldPred(pp: InstructionOperand.Predicate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.YieldPredDefinition, mods, pred, pp)
  case Hir0x002NoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x002NoOperandsDefinition, mods, pred)
  case Hir0x003NoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x003NoOperandsDefinition, mods, pred)
  case Hir0x003Pred(pu: InstructionOperand.Predicate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x003PredDefinition, mods, pred, pu)
  case Hir0x004NoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x004NoOperandsDefinition, mods, pred)
  case Hir0x005PredRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      immU16: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hir0x005PredRegRegImmDefinition, mods, pred, pu, rd2, dst, immU16)
  case Hir0x005RegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      immU16: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hir0x005RegRegImmDefinition, mods, pred, rd2, dst, immU16)
  case Hir0x006Imm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x006ImmDefinition, mods, pred, sb)
  case Hir0x007NoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x007NoOperandsDefinition, mods, pred)
  case Hir0x008ImmRegReg(
      immU16: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hir0x008ImmRegRegDefinition, mods, pred, immU16, srcB, srcC)
  case Hir0x020NoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x020NoOperandsDefinition, mods, pred)
  case Hir0x020Imm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x020ImmDefinition, mods, pred, sb)
  case Hir0x120AddressImm(
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hir0x120AddressImmDefinition, mods, pred, srcB, sb_offset)
  case Hir0x120Const(sb: InstructionOperand.ConstantBank, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x120ConstDefinition, mods, pred, sb)
  case Hir0x120Imm(sb: InstructionOperand.Immediate, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x120ImmDefinition, mods, pred, sb)
  case Hir0x120PredAddressImm(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hir0x120PredAddressImmDefinition, mods, pred, pp, srcB, sb_offset)
  case Hir0x120PredConst(
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hir0x120PredConstDefinition, mods, pred, pp, sb)
  case Hir0x120PredImm(
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hir0x120PredImmDefinition, mods, pred, pp, sb)
  case Hir0x120PredReg(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hir0x120PredRegDefinition, mods, pred, pp, srcB)
  case Hir0x120Reg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x120RegDefinition, mods, pred, srcB)
  case Hir0x130AddressAddressAddressAddressAddressAddressPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hir0x130AddressAddressAddressAddressAddressAddressPredDefinition,
        mods,
        pred,
        srcA,
        srcB,
        srcC,
        uRe,
        uRh,
        uRi,
        uPp
      )
  case Hir0x150RegRegRegRegPredRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hir0x150RegRegRegRegPredRegRegRegDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        uPp,
        re,
        rh,
        uRi
      )
  case Hir0x150RegRegRegRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hir0x150RegRegRegRegRegRegRegDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        re,
        rh,
        uRi
      )
  case Hir0x160Reg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x160RegDefinition, mods, pred, srcB)
  case Hir0x170Reg(dst: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x170RegDefinition, mods, pred, dst)
  case Hir0x1a0RegRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      vecidx2: InstructionOperand.Immediate,
      mdidx: InstructionOperand.Immediate,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(
        Instruction.Hir0x1a0RegRegRegRegImmImmDefinition,
        mods,
        pred,
        dst,
        srcA,
        srcB,
        srcC,
        vecidx2,
        mdidx
      )
  case Hir0x1b0RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hir0x1b0RegRegRegRegDefinition, mods, pred, dst, srcA, srcB, srcC)
  case Hir0x1e0NoOperands(mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x1e0NoOperandsDefinition, mods, pred)
  case Hir0x1f0ImmReg(
      barname: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Guard] = None
  ) extends Instruction(Instruction.Hir0x1f0ImmRegDefinition, mods, pred, barname, srcB)
  case Hir0x1f2Reg(dst: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x1f2RegDefinition, mods, pred, dst)
  case Hir0x1f3Reg(srcB: InstructionOperand.Register, mods: Vec[Modifier] = Vec.empty, pred: Option[Guard] = None)
      extends Instruction(Instruction.Hir0x1f3RegDefinition, mods, pred, srcB)
}
