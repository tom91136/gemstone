// Generated from the pinned AMDGPU machine metadata. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import upickle.default.*

object EncodingField {
  val VectorSources: Vector[EncodingField] =
    Vector(EncodingField.`VSRC0`, EncodingField.`VSRC1`, EncodingField.`VSRC2`, EncodingField.`VSRC3`)
  val ImageAddresses: Vector[EncodingField] = Vector(
    EncodingField.`VADDR0`,
    EncodingField.`VADDR1`,
    EncodingField.`VADDR2`,
    EncodingField.`VADDR3`,
    EncodingField.`VADDR4`
  )
  val LaneSelectors: Vector[EncodingField] = Vector(
    EncodingField.`LANE_SEL_0`,
    EncodingField.`LANE_SEL_1`,
    EncodingField.`LANE_SEL_2`,
    EncodingField.`LANE_SEL_3`,
    EncodingField.`LANE_SEL_4`,
    EncodingField.`LANE_SEL_5`,
    EncodingField.`LANE_SEL_6`,
    EncodingField.`LANE_SEL_7`
  )
  val ScalarSelectors: Vector[EncodingField] = Vector(EncodingField.`S0`, EncodingField.`S1`)
  given ReadWriter[EncodingField] = readwriter[String].bimap(_.toString, valueOf(_))
  private lazy val byName: Map[String, EncodingField] = values.view.map(value => value.toString -> value).toMap
  def fromName(name: String): Option[EncodingField] = byName.get(name)
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum EncodingField {
  case `A16`
  case `ABID`
  case `ABS`
  case `ACC`
  case `ACC_CD`
  case `ADDR`
  case `ADDR64`
  case `ATTR`
  case `ATTR_CHAN`
  case `BANK_MASK`
  case `BITOP3`
  case `BLGP`
  case `BOUND_CTRL`
  case `BREAK_SPAN`
  case `BYTE_SEL`
  case `CBSZ`
  case `CLAMP`
  case `COMPR`
  case `D16`
  case `DA`
  case `DATA`
  case `DATA0`
  case `DATA1`
  case `DFMT`
  case `DIM`
  case `DLC`
  case `DMASK`
  case `DONE`
  case `DONT_WAIT_EXPORT_READY`
  case `DPP_CTRL`
  case `DS`
  case `DST_MSB`
  case `DST_SEL`
  case `DST_UNUSED`
  case `DURATION`
  case `EN`
  case `ENCODING`
  case `EXP`
  case `EXPORT_READY`
  case `FI`
  case `FMT`
  case `FORMAT`
  case `GDS`
  case `GLC`
  case `GSOP`
  case `HAS_RTN`
  case `HOLD_CNT`
  case `ID`
  case `IDXEN`
  case `IMM`
  case `INSTID0`
  case `INSTID1`
  case `INSTSKIP`
  case `IOFFSET`
  case `LANE_SEL_0`
  case `LANE_SEL_1`
  case `LANE_SEL_2`
  case `LANE_SEL_3`
  case `LANE_SEL_4`
  case `LANE_SEL_5`
  case `LANE_SEL_6`
  case `LANE_SEL_7`
  case `LDS`
  case `LENGTH`
  case `LGKM`
  case `LITERAL`
  case `LITERAL64`
  case `LWE`
  case `MDP`
  case `MEM`
  case `MSG`
  case `NEG`
  case `NEGX`
  case `NEGY`
  case `NEG_HI`
  case `NFMT`
  case `NSA`
  case `NT`
  case `NV`
  case `OFFEN`
  case `OFFSET`
  case `OFFSET0`
  case `OFFSET1`
  case `OMOD`
  case `OP`
  case `OPX`
  case `OPY`
  case `OP_SEL`
  case `OP_SEL_HI`
  case `OP_SEL_HI_2`
  case `R128`
  case `ROW_EN`
  case `ROW_MASK`
  case `RSRC`
  case `S0`
  case `S1`
  case `SADDR`
  case `SAMP`
  case `SA_SDST`
  case `SBASE`
  case `SC0`
  case `SC1`
  case `SCALE`
  case `SCALE_CLAMP`
  case `SCALE_FMT`
  case `SCALE_NEG`
  case `SCALE_NEG_HI`
  case `SCALE_OFFSET`
  case `SCALE_OP_SEL`
  case `SCALE_OP_SEL_HI`
  case `SCALE_SRC0`
  case `SCALE_SRC1`
  case `SCALE_SRC2`
  case `SCC`
  case `SCOPE`
  case `SD`
  case `SDATA`
  case `SDST`
  case `SEG`
  case `SIMM16`
  case `SIZE`
  case `SLC`
  case `SLEEP_FOREVER`
  case `SOFFSET`
  case `SOFFSET_EN`
  case `SRC0`
  case `SRC0_ABS`
  case `SRC0_MSB`
  case `SRC0_NEG`
  case `SRC0_SEL`
  case `SRC0_SEXT`
  case `SRC1`
  case `SRC1_ABS`
  case `SRC1_MSB`
  case `SRC1_NEG`
  case `SRC1_SEL`
  case `SRC1_SEXT`
  case `SRC2`
  case `SRC2_MSB`
  case `SRCX0`
  case `SRCY0`
  case `SRSRC`
  case `SSAMP`
  case `SSRC0`
  case `SSRC1`
  case `STREAMID`
  case `SVE`
  case `SYSTEM`
  case `TARGET`
  case `TFE`
  case `TGT`
  case `TH`
  case `UNORM`
  case `VADDR`
  case `VADDR0`
  case `VADDR1`
  case `VADDR2`
  case `VADDR3`
  case `VADDR4`
  case `VADDRA`
  case `VADDRB`
  case `VADDRC`
  case `VADDRD`
  case `VADDRE`
  case `VADDRF`
  case `VADDRG`
  case `VADDRH`
  case `VADDRI`
  case `VADDRJ`
  case `VADDRK`
  case `VADDRL`
  case `VALUE`
  case `VA_SDST`
  case `VA_SSRC`
  case `VA_VCC`
  case `VA_VDST`
  case `VDATA`
  case `VDST`
  case `VDSTX`
  case `VDSTY`
  case `VERSION`
  case `VM`
  case `VM_VSRC`
  case `VSRC`
  case `VSRC0`
  case `VSRC1`
  case `VSRC2`
  case `VSRC3`
  case `VSRCX1`
  case `VSRCX2`
  case `VSRCY1`
  case `VSRCY2`
  case `W32`
  case `W64`
  case `WAIT_EXP`
  case `WAIT_VA_VDST`
  case `WAIT_VDST`
  case `WAIT_VM_VSRC`
  case `X2ENCODING`
  case `X3ENCODING`
  def assemblyName: NonEmpty[String] = this match {
    case `A16` => "a16"; case `ABID` => "abid"; case `ABS` => "abs"; case `ACC` => "acc"; case `ACC_CD` => "acc_cd";
    case `ADDR` => "addr"; case `ADDR64` => "addr64"; case `ATTR` => "attr"; case `ATTR_CHAN` => "attr_chan";
    case `BANK_MASK` => "bank_mask"; case `BITOP3` => "bitop3"; case `BLGP` => "blgp";
    case `BOUND_CTRL` => "bound_ctrl"; case `BREAK_SPAN` => "break_span"; case `BYTE_SEL` => "byte_sel";
    case `CBSZ` => "cbsz"; case `CLAMP` => "clamp"; case `COMPR` => "compr"; case `D16` => "d16"; case `DA` => "da";
    case `DATA` => "data"; case `DATA0` => "data0"; case `DATA1` => "data1"; case `DFMT` => "dfmt"; case `DIM` => "dim";
    case `DLC` => "dlc"; case `DMASK` => "dmask"; case `DONE` => "done";
    case `DONT_WAIT_EXPORT_READY` => "dont_wait_export_ready"; case `DPP_CTRL` => "dpp_ctrl"; case `DS` => "ds";
    case `DST_MSB` => "dst_msb"; case `DST_SEL` => "dst_sel"; case `DST_UNUSED` => "dst_unused";
    case `DURATION` => "duration"; case `EN` => "en"; case `ENCODING` => "encoding"; case `EXP` => "exp";
    case `EXPORT_READY` => "export_ready"; case `FI` => "fi"; case `FMT` => "fmt"; case `FORMAT` => "format";
    case `GDS` => "gds"; case `GLC` => "glc"; case `GSOP` => "gsop"; case `HAS_RTN` => "has_rtn";
    case `HOLD_CNT` => "hold_cnt"; case `ID` => "id"; case `IDXEN` => "idxen"; case `IMM` => "imm";
    case `INSTID0` => "instid0"; case `INSTID1` => "instid1"; case `INSTSKIP` => "instskip";
    case `IOFFSET` => "ioffset"; case `LANE_SEL_0` => "lane_sel_0"; case `LANE_SEL_1` => "lane_sel_1";
    case `LANE_SEL_2` => "lane_sel_2"; case `LANE_SEL_3` => "lane_sel_3"; case `LANE_SEL_4` => "lane_sel_4";
    case `LANE_SEL_5` => "lane_sel_5"; case `LANE_SEL_6` => "lane_sel_6"; case `LANE_SEL_7` => "lane_sel_7";
    case `LDS` => "lds"; case `LENGTH` => "length"; case `LGKM` => "lgkm"; case `LITERAL` => "literal";
    case `LITERAL64` => "literal64"; case `LWE` => "lwe"; case `MDP` => "mdp"; case `MEM` => "mem"; case `MSG` => "msg";
    case `NEG` => "neg"; case `NEGX` => "negx"; case `NEGY` => "negy"; case `NEG_HI` => "neg_hi"; case `NFMT` => "nfmt";
    case `NSA` => "nsa"; case `NT` => "nt"; case `NV` => "nv"; case `OFFEN` => "offen"; case `OFFSET` => "offset";
    case `OFFSET0` => "offset0"; case `OFFSET1` => "offset1"; case `OMOD` => "omod"; case `OP` => "op";
    case `OPX` => "opx"; case `OPY` => "opy"; case `OP_SEL` => "op_sel"; case `OP_SEL_HI` => "op_sel_hi";
    case `OP_SEL_HI_2` => "op_sel_hi_2"; case `R128` => "r128"; case `ROW_EN` => "row_en";
    case `ROW_MASK` => "row_mask"; case `RSRC` => "rsrc"; case `S0` => "s0"; case `S1` => "s1"; case `SADDR` => "saddr";
    case `SAMP` => "samp"; case `SA_SDST` => "sa_sdst"; case `SBASE` => "sbase"; case `SC0` => "sc0";
    case `SC1` => "sc1"; case `SCALE` => "scale"; case `SCALE_CLAMP` => "scale_clamp"; case `SCALE_FMT` => "scale_fmt";
    case `SCALE_NEG` => "scale_neg"; case `SCALE_NEG_HI` => "scale_neg_hi"; case `SCALE_OFFSET` => "scale_offset";
    case `SCALE_OP_SEL` => "scale_op_sel"; case `SCALE_OP_SEL_HI` => "scale_op_sel_hi";
    case `SCALE_SRC0` => "scale_src0"; case `SCALE_SRC1` => "scale_src1"; case `SCALE_SRC2` => "scale_src2";
    case `SCC` => "scc"; case `SCOPE` => "scope"; case `SD` => "sd"; case `SDATA` => "sdata"; case `SDST` => "sdst";
    case `SEG` => "seg"; case `SIMM16` => "simm16"; case `SIZE` => "size"; case `SLC` => "slc";
    case `SLEEP_FOREVER` => "sleep_forever"; case `SOFFSET` => "soffset"; case `SOFFSET_EN` => "soffset_en";
    case `SRC0` => "src0"; case `SRC0_ABS` => "src0_abs"; case `SRC0_MSB` => "src0_msb"; case `SRC0_NEG` => "src0_neg";
    case `SRC0_SEL` => "src0_sel"; case `SRC0_SEXT` => "src0_sext"; case `SRC1` => "src1";
    case `SRC1_ABS` => "src1_abs"; case `SRC1_MSB` => "src1_msb"; case `SRC1_NEG` => "src1_neg";
    case `SRC1_SEL` => "src1_sel"; case `SRC1_SEXT` => "src1_sext"; case `SRC2` => "src2";
    case `SRC2_MSB` => "src2_msb"; case `SRCX0` => "srcx0"; case `SRCY0` => "srcy0"; case `SRSRC` => "srsrc";
    case `SSAMP` => "ssamp"; case `SSRC0` => "ssrc0"; case `SSRC1` => "ssrc1"; case `STREAMID` => "streamid";
    case `SVE` => "sve"; case `SYSTEM` => "system"; case `TARGET` => "target"; case `TFE` => "tfe"; case `TGT` => "tgt";
    case `TH` => "th"; case `UNORM` => "unorm"; case `VADDR` => "vaddr"; case `VADDR0` => "vaddr0";
    case `VADDR1` => "vaddr1"; case `VADDR2` => "vaddr2"; case `VADDR3` => "vaddr3"; case `VADDR4` => "vaddr4";
    case `VADDRA` => "vaddra"; case `VADDRB` => "vaddrb"; case `VADDRC` => "vaddrc"; case `VADDRD` => "vaddrd";
    case `VADDRE` => "vaddre"; case `VADDRF` => "vaddrf"; case `VADDRG` => "vaddrg"; case `VADDRH` => "vaddrh";
    case `VADDRI` => "vaddri"; case `VADDRJ` => "vaddrj"; case `VADDRK` => "vaddrk"; case `VADDRL` => "vaddrl";
    case `VALUE` => "value"; case `VA_SDST` => "va_sdst"; case `VA_SSRC` => "va_ssrc"; case `VA_VCC` => "va_vcc";
    case `VA_VDST` => "va_vdst"; case `VDATA` => "vdata"; case `VDST` => "vdst"; case `VDSTX` => "vdstx";
    case `VDSTY` => "vdsty"; case `VERSION` => "version"; case `VM` => "vm"; case `VM_VSRC` => "vm_vsrc";
    case `VSRC` => "vsrc"; case `VSRC0` => "vsrc0"; case `VSRC1` => "vsrc1"; case `VSRC2` => "vsrc2";
    case `VSRC3` => "vsrc3"; case `VSRCX1` => "vsrcx1"; case `VSRCX2` => "vsrcx2"; case `VSRCY1` => "vsrcy1";
    case `VSRCY2` => "vsrcy2"; case `W32` => "w32"; case `W64` => "w64"; case `WAIT_EXP` => "wait_exp";
    case `WAIT_VA_VDST` => "wait_va_vdst"; case `WAIT_VDST` => "wait_vdst"; case `WAIT_VM_VSRC` => "wait_vm_vsrc";
    case `X2ENCODING` => "x2encoding"; case `X3ENCODING` => "x3encoding"
  }
  def isVectorSource: Boolean = this match {
    case `VSRC` | `VSRC0` | `VSRC1` | `VSRC2` | `VSRC3` | `VSRCX1` | `VSRCX2` | `VSRCY1` | `VSRCY2` => true;
    case _ => false
  }
  def absoluteModifier: Option[EncodingField] =
    this match { case `VSRC0` => Some(`SRC0_ABS`); case `VSRC1` => Some(`SRC1_ABS`); case _ => None }
  def negativeModifier: Option[EncodingField] =
    this match { case `VSRC0` => Some(`SRC0_NEG`); case `VSRC1` => Some(`SRC1_NEG`); case _ => None }
  def extensionModifier: Option[EncodingField] =
    this match { case `VSRC0` => Some(`SRC0_SEXT`); case `VSRC1` => Some(`SRC1_SEXT`); case _ => None }
  def scalarSelector: Option[EncodingField] =
    this match { case `VSRC0` => Some(`S0`); case `VSRC1` => Some(`S1`); case _ => None }
  def isVectorDestination: Boolean = this match { case `VDST` | `VDSTX` | `VDSTY` => true; case _ => false }
  def isScale: Boolean = this match {
    case `SCALE_CLAMP` | `SCALE_FMT` | `SCALE_NEG` | `SCALE_NEG_HI` | `SCALE_OFFSET` | `SCALE_OP_SEL` | `SCALE_OP_SEL_HI` | `SCALE_SRC0` | `SCALE_SRC1` | `SCALE_SRC2` =>
      true;
    case _ => false
  }
  def isImageAddress: Boolean = this match {
    case `VADDR` | `VADDR0` | `VADDR1` | `VADDR2` | `VADDR3` | `VADDR4` | `VADDRA` | `VADDRB` | `VADDRC` | `VADDRD` | `VADDRE` | `VADDRF` | `VADDRG` | `VADDRH` | `VADDRI` | `VADDRJ` | `VADDRK` | `VADDRL` =>
      true;
    case _ => false
  }
  def isWait: Boolean =
    this match { case `WAIT_EXP` | `WAIT_VA_VDST` | `WAIT_VDST` | `WAIT_VM_VSRC` => true; case _ => false }
  def index: Int = this match {
    case `A16` => 6; case `ADDR64` => 4; case `BITOP3` => 3; case `D16` => 6; case `DATA0` => 0; case `DATA1` => 1;
    case `INSTID0` => 0; case `INSTID1` => 1; case `LANE_SEL_0` => 0; case `LANE_SEL_1` => 1; case `LANE_SEL_2` => 2;
    case `LANE_SEL_3` => 3; case `LANE_SEL_4` => 4; case `LANE_SEL_5` => 5; case `LANE_SEL_6` => 6;
    case `LANE_SEL_7` => 7; case `LITERAL64` => 4; case `OFFSET0` => 0; case `OFFSET1` => 1; case `OP_SEL_HI_2` => 2;
    case `R128` => 8; case `S0` => 0; case `S1` => 1; case `SC0` => 0; case `SC1` => 1; case `SCALE_SRC0` => 0;
    case `SCALE_SRC1` => 1; case `SCALE_SRC2` => 2; case `SIMM16` => 6; case `SRC0` => 0; case `SRC1` => 1;
    case `SRC2` => 2; case `SRCX0` => 0; case `SRCY0` => 0; case `SSRC0` => 0; case `SSRC1` => 1; case `VADDR0` => 0;
    case `VADDR1` => 1; case `VADDR2` => 2; case `VADDR3` => 3; case `VADDR4` => 4; case `VSRC0` => 0;
    case `VSRC1` => 1; case `VSRC2` => 2; case `VSRC3` => 3; case `VSRCX1` => 1; case `VSRCX2` => 2; case `VSRCY1` => 1;
    case `VSRCY2` => 2; case `W32` => 2; case `W64` => 4; case _ => -1
  }
}
