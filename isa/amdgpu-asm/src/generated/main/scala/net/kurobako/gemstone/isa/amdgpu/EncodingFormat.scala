// Generated from the pinned AMDGPU machine metadata. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import upickle.default.*

object EncodingFormat {

  given ReadWriter[EncodingFormat] = readwriter[String].bimap(_.toString, valueOf(_))
  private lazy val byName: Map[String, EncodingFormat] = values.view.map(value => value.toString -> value).toMap
  def fromName(name: String): Option[EncodingFormat] = byName.get(name)
}

@Generated(Array("sbt codegen"))
enum EncodingFormat derives CanEqual {
  case `DS`
  case `EXP`
  case `FLAT`
  case `FLAT_GLBL`
  case `FLAT_GLOBAL`
  case `FLAT_SCRATCH`
  case `GLOBAL`
  case `ILLEGAL`
  case `LDSDIR`
  case `MIMG`
  case `MIMG_NSA1`
  case `MIMG_NSA2`
  case `MIMG_NSA3`
  case `MTBUF`
  case `MUBUF`
  case `SMEM`
  case `SOP1`
  case `SOP1_INST_LITERAL`
  case `SOP1_INST_LITERAL64`
  case `SOP2`
  case `SOP2_INST_LITERAL`
  case `SOP2_INST_LITERAL64`
  case `SOPC`
  case `SOPC_INST_LITERAL`
  case `SOPC_INST_LITERAL64`
  case `SOPK`
  case `SOPK_INST_LITERAL`
  case `SOPP`
  case `VBUFFER`
  case `VDS`
  case `VDSDIR`
  case `VEXPORT`
  case `VFLAT`
  case `VGLOBAL`
  case `VIMAGE`
  case `VINTERP`
  case `VINTERP_INREG`
  case `VINTRP`
  case `VOP1`
  case `VOP1_INST_LITERAL`
  case `VOP1_INST_LITERAL64`
  case `VOP1_VOP_DPP`
  case `VOP1_VOP_DPP16`
  case `VOP1_VOP_DPP8`
  case `VOP1_VOP_SDWA`
  case `VOP2`
  case `VOP2_INST_LITERAL`
  case `VOP2_INST_LITERAL64`
  case `VOP2_VOP_DPP`
  case `VOP2_VOP_DPP16`
  case `VOP2_VOP_DPP8`
  case `VOP2_VOP_SDWA`
  case `VOP2_VOP_SDWA_SDST_ENC`
  case `VOP3`
  case `VOP3P`
  case `VOP3PX2`
  case `VOP3PX3`
  case `VOP3P_INST_LITERAL`
  case `VOP3P_MFMA`
  case `VOP3P_VOP_DPP16`
  case `VOP3P_VOP_DPP8`
  case `VOP3_BITOP3`
  case `VOP3_BITOP3_INST_LITERAL`
  case `VOP3_BITOP3_VOP_DPP16`
  case `VOP3_BITOP3_VOP_DPP8`
  case `VOP3_BYTESEL`
  case `VOP3_BYTESEL_INST_LITERAL`
  case `VOP3_BYTESEL_VOP_DPP16`
  case `VOP3_BYTESEL_VOP_DPP8`
  case `VOP3_INST_LITERAL`
  case `VOP3_SDST_ENC`
  case `VOP3_SDST_ENC_INST_LITERAL`
  case `VOP3_SDST_ENC_VOP_DPP16`
  case `VOP3_SDST_ENC_VOP_DPP8`
  case `VOP3_VOP_DPP16`
  case `VOP3_VOP_DPP8`
  case `VOPC`
  case `VOPC_INST_LITERAL`
  case `VOPC_INST_LITERAL64`
  case `VOPC_VOP_DPP16`
  case `VOPC_VOP_DPP8`
  case `VOPC_VOP_SDWA_SDST_ENC`
  case `VOPD`
  case `VOPD3XY`
  case `VOPDXY`
  case `VOPDXY_INST_LITERAL`
  case `VOPDXY_INST_LITERAL_X`
  case `VOPDXY_INST_LITERAL_Y`
  case `VOPDXY_X`
  case `VOPDXY_Y`
  case `VSAMPLE`
  case `VSCRATCH`
  def base: EncodingFormat = this match {
    case `FLAT_GLBL` => `FLAT`
    case `FLAT_GLOBAL` => `FLAT`
    case `FLAT_SCRATCH` => `FLAT`
    case `MIMG_NSA1` => `MIMG`
    case `MIMG_NSA2` => `MIMG`
    case `MIMG_NSA3` => `MIMG`
    case `SOP1_INST_LITERAL` => `SOP1`
    case `SOP1_INST_LITERAL64` => `SOP1`
    case `SOP2_INST_LITERAL` => `SOP2`
    case `SOP2_INST_LITERAL64` => `SOP2`
    case `SOPC_INST_LITERAL` => `SOPC`
    case `SOPC_INST_LITERAL64` => `SOPC`
    case `SOPK_INST_LITERAL` => `SOPK`
    case `VINTERP_INREG` => `VINTERP`
    case `VOP1_INST_LITERAL` => `VOP1`
    case `VOP1_INST_LITERAL64` => `VOP1`
    case `VOP1_VOP_DPP` => `VOP1`
    case `VOP1_VOP_DPP16` => `VOP1`
    case `VOP1_VOP_DPP8` => `VOP1`
    case `VOP1_VOP_SDWA` => `VOP1`
    case `VOP2_INST_LITERAL` => `VOP2`
    case `VOP2_INST_LITERAL64` => `VOP2`
    case `VOP2_VOP_DPP` => `VOP2`
    case `VOP2_VOP_DPP16` => `VOP2`
    case `VOP2_VOP_DPP8` => `VOP2`
    case `VOP2_VOP_SDWA` => `VOP2`
    case `VOP2_VOP_SDWA_SDST_ENC` => `VOP2`
    case `VOP3P_INST_LITERAL` => `VOP3P`
    case `VOP3P_MFMA` => `VOP3P`
    case `VOP3P_VOP_DPP16` => `VOP3P`
    case `VOP3P_VOP_DPP8` => `VOP3P`
    case `VOP3_BITOP3` => `VOP3`
    case `VOP3_BITOP3_INST_LITERAL` => `VOP3`
    case `VOP3_BITOP3_VOP_DPP16` => `VOP3`
    case `VOP3_BITOP3_VOP_DPP8` => `VOP3`
    case `VOP3_BYTESEL` => `VOP3`
    case `VOP3_BYTESEL_INST_LITERAL` => `VOP3`
    case `VOP3_BYTESEL_VOP_DPP16` => `VOP3`
    case `VOP3_BYTESEL_VOP_DPP8` => `VOP3`
    case `VOP3_INST_LITERAL` => `VOP3`
    case `VOP3_SDST_ENC` => `VOP3`
    case `VOP3_SDST_ENC_INST_LITERAL` => `VOP3`
    case `VOP3_SDST_ENC_VOP_DPP16` => `VOP3`
    case `VOP3_SDST_ENC_VOP_DPP8` => `VOP3`
    case `VOP3_VOP_DPP16` => `VOP3`
    case `VOP3_VOP_DPP8` => `VOP3`
    case `VOPC_INST_LITERAL` => `VOPC`
    case `VOPC_INST_LITERAL64` => `VOPC`
    case `VOPC_VOP_DPP16` => `VOPC`
    case `VOPC_VOP_DPP8` => `VOPC`
    case `VOPC_VOP_SDWA_SDST_ENC` => `VOPC`
    case `VOPDXY_INST_LITERAL` => `VOPDXY`
    case `VOPDXY_INST_LITERAL_X` => `VOPDXY`
    case `VOPDXY_INST_LITERAL_Y` => `VOPDXY`
    case `VOPDXY_X` => `VOPDXY`
    case `VOPDXY_Y` => `VOPDXY`
    case _ => this
  }
  def withLiteral(bytes: Int): Option[EncodingFormat] = (this, bytes) match {
    case (`SOP1`, 4) => Some(`SOP1_INST_LITERAL`)
    case (`SOP1`, 8) => Some(`SOP1_INST_LITERAL64`)
    case (`SOP2`, 4) => Some(`SOP2_INST_LITERAL`)
    case (`SOP2`, 8) => Some(`SOP2_INST_LITERAL64`)
    case (`SOPC`, 4) => Some(`SOPC_INST_LITERAL`)
    case (`SOPC`, 8) => Some(`SOPC_INST_LITERAL64`)
    case (`SOPK`, 4) => Some(`SOPK_INST_LITERAL`)
    case (`VOP1`, 4) => Some(`VOP1_INST_LITERAL`)
    case (`VOP1`, 8) => Some(`VOP1_INST_LITERAL64`)
    case (`VOP2`, 4) => Some(`VOP2_INST_LITERAL`)
    case (`VOP2`, 8) => Some(`VOP2_INST_LITERAL64`)
    case (`VOP3`, 4) => Some(`VOP3_INST_LITERAL`)
    case (`VOP3P`, 4) => Some(`VOP3P_INST_LITERAL`)
    case (`VOP3_BITOP3`, 4) => Some(`VOP3_BITOP3_INST_LITERAL`)
    case (`VOP3_BYTESEL`, 4) => Some(`VOP3_BYTESEL_INST_LITERAL`)
    case (`VOP3_SDST_ENC`, 4) => Some(`VOP3_SDST_ENC_INST_LITERAL`)
    case (`VOPC`, 4) => Some(`VOPC_INST_LITERAL`)
    case (`VOPC`, 8) => Some(`VOPC_INST_LITERAL64`)
    case (`VOPDXY`, 4) => Some(`VOPDXY_INST_LITERAL`)
    case _ => None
  }
  def isImage: Boolean =
    this match { case `MIMG` | `MIMG_NSA1` | `MIMG_NSA2` | `MIMG_NSA3` | `VIMAGE` | `VSAMPLE` => true; case _ => false }
  def isNonSequentialImage: Boolean =
    this match { case `MIMG_NSA1` | `MIMG_NSA2` | `MIMG_NSA3` => true; case _ => false }
  def isVector: Boolean = this match {
    case `VOP1` | `VOP1_INST_LITERAL` | `VOP1_INST_LITERAL64` | `VOP1_VOP_DPP` | `VOP1_VOP_DPP16` | `VOP1_VOP_DPP8` | `VOP1_VOP_SDWA` | `VOP2` | `VOP2_INST_LITERAL` | `VOP2_INST_LITERAL64` | `VOP2_VOP_DPP` | `VOP2_VOP_DPP16` | `VOP2_VOP_DPP8` | `VOP2_VOP_SDWA` | `VOP2_VOP_SDWA_SDST_ENC` | `VOP3` | `VOP3P` | `VOP3PX2` | `VOP3PX3` | `VOP3P_INST_LITERAL` | `VOP3P_MFMA` | `VOP3P_VOP_DPP16` | `VOP3P_VOP_DPP8` | `VOP3_BITOP3` | `VOP3_BITOP3_INST_LITERAL` | `VOP3_BITOP3_VOP_DPP16` | `VOP3_BITOP3_VOP_DPP8` | `VOP3_BYTESEL` | `VOP3_BYTESEL_INST_LITERAL` | `VOP3_BYTESEL_VOP_DPP16` | `VOP3_BYTESEL_VOP_DPP8` | `VOP3_INST_LITERAL` | `VOP3_SDST_ENC` | `VOP3_SDST_ENC_INST_LITERAL` | `VOP3_SDST_ENC_VOP_DPP16` | `VOP3_SDST_ENC_VOP_DPP8` | `VOP3_VOP_DPP16` | `VOP3_VOP_DPP8` | `VOPC` | `VOPC_INST_LITERAL` | `VOPC_INST_LITERAL64` | `VOPC_VOP_DPP16` | `VOPC_VOP_DPP8` | `VOPC_VOP_SDWA_SDST_ENC` | `VOPD` | `VOPD3XY` | `VOPDXY` | `VOPDXY_INST_LITERAL` | `VOPDXY_INST_LITERAL_X` | `VOPDXY_INST_LITERAL_Y` | `VOPDXY_X` | `VOPDXY_Y` =>
      true;
    case _ => false
  }
  def isVOP3: Boolean = this match {
    case `VOP3` | `VOP3P` | `VOP3PX2` | `VOP3PX3` | `VOP3P_INST_LITERAL` | `VOP3P_MFMA` | `VOP3P_VOP_DPP16` | `VOP3P_VOP_DPP8` | `VOP3_BITOP3` | `VOP3_BITOP3_INST_LITERAL` | `VOP3_BITOP3_VOP_DPP16` | `VOP3_BITOP3_VOP_DPP8` | `VOP3_BYTESEL` | `VOP3_BYTESEL_INST_LITERAL` | `VOP3_BYTESEL_VOP_DPP16` | `VOP3_BYTESEL_VOP_DPP8` | `VOP3_INST_LITERAL` | `VOP3_SDST_ENC` | `VOP3_SDST_ENC_INST_LITERAL` | `VOP3_SDST_ENC_VOP_DPP16` | `VOP3_SDST_ENC_VOP_DPP8` | `VOP3_VOP_DPP16` | `VOP3_VOP_DPP8` =>
      true;
    case _ => false
  }
  def isFlat: Boolean =
    this match { case `FLAT` | `FLAT_GLBL` | `FLAT_GLOBAL` | `FLAT_SCRATCH` => true; case _ => false }
  def isLiteral32: Boolean = this match {
    case `SOP1_INST_LITERAL` | `SOP2_INST_LITERAL` | `SOPC_INST_LITERAL` | `SOPK_INST_LITERAL` | `VOP1_INST_LITERAL` | `VOP2_INST_LITERAL` | `VOP3P_INST_LITERAL` | `VOP3_BITOP3_INST_LITERAL` | `VOP3_BYTESEL_INST_LITERAL` | `VOP3_INST_LITERAL` | `VOP3_SDST_ENC_INST_LITERAL` | `VOPC_INST_LITERAL` | `VOPDXY_INST_LITERAL` =>
      true;
    case _ => false
  }
  def isDpp: Boolean = this match {
    case `VOP1_VOP_DPP` | `VOP1_VOP_DPP16` | `VOP1_VOP_DPP8` | `VOP2_VOP_DPP` | `VOP2_VOP_DPP16` | `VOP2_VOP_DPP8` | `VOP3P_VOP_DPP16` | `VOP3P_VOP_DPP8` | `VOP3_BITOP3_VOP_DPP16` | `VOP3_BITOP3_VOP_DPP8` | `VOP3_BYTESEL_VOP_DPP16` | `VOP3_BYTESEL_VOP_DPP8` | `VOP3_SDST_ENC_VOP_DPP16` | `VOP3_SDST_ENC_VOP_DPP8` | `VOP3_VOP_DPP16` | `VOP3_VOP_DPP8` | `VOPC_VOP_DPP16` | `VOPC_VOP_DPP8` =>
      true;
    case _ => false
  }
  def isSdwa: Boolean = this match {
    case `VOP1_VOP_SDWA` | `VOP2_VOP_SDWA` | `VOP2_VOP_SDWA_SDST_ENC` | `VOPC_VOP_SDWA_SDST_ENC` => true;
    case _ => false
  }
  def isDpp8: Boolean = this match {
    case `VOP1_VOP_DPP8` | `VOP2_VOP_DPP8` | `VOP3P_VOP_DPP8` | `VOP3_BITOP3_VOP_DPP8` | `VOP3_BYTESEL_VOP_DPP8` | `VOP3_SDST_ENC_VOP_DPP8` | `VOP3_VOP_DPP8` | `VOPC_VOP_DPP8` =>
      true;
    case _ => false
  }
  def isScratch: Boolean = this match { case `FLAT_SCRATCH` | `VSCRATCH` => true; case _ => false }
  def isBuffer: Boolean = this match { case `MTBUF` | `MUBUF` | `VBUFFER` => true; case _ => false }
  def isDataShare: Boolean = this match { case `DS` | `VDS` => true; case _ => false }
  def isVOP3PX: Boolean = this match { case `VOP3PX2` | `VOP3PX3` => true; case _ => false }
}
