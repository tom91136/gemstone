// Generated from the pinned AMDGPU machine metadata. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import upickle.default.*

object OperandKind {

  given ReadWriter[OperandKind] = readwriter[String].bimap(_.toString, valueOf(_))
  private lazy val byName: Map[String, OperandKind] = values.view.map(value => value.toString -> value).toMap
  def fromName(name: String): Option[OperandKind] = byName.get(name)
}

@Generated(Array("sbt codegen"))
enum OperandKind derives CanEqual {
  case `ACCVGPR`
  case `ATTR`
  case `BUFFER_FORMAT`
  case `CLAUSE`
  case `DELAY`
  case `DSMEM`
  case `EXEC`
  case `FLAT_SCRATCH`
  case `GPUMEM`
  case `HWREG`
  case `LABEL`
  case `MATRIX`
  case `PARAM`
  case `PC`
  case `SDST`
  case `SDST_EXEC`
  case `SDST_M0`
  case `SDST_NULL`
  case `SENDMSG`
  case `SENDMSG_RTN`
  case `SET_VGPR_MSB`
  case `SGPR`
  case `SIMM16`
  case `SIMM24`
  case `SIMM32`
  case `SIMM4`
  case `SIMM5`
  case `SIMM64`
  case `SIMM8`
  case `SLEEP`
  case `SMEM_OFFSET`
  case `SMEM_OFFSET_NOK`
  case `SRC`
  case `SRC_ACCVGPR`
  case `SRC_ACCVGPR_OR_CONST`
  case `SRC_NOINLINE`
  case `SRC_NOLDS`
  case `SRC_NOLIT`
  case `SRC_SIMPLE`
  case `SRC_VGPR`
  case `SRC_VGPR_OR_ACCVGPR`
  case `SRC_VGPR_OR_ACCVGPR_OR_CONST`
  case `SRC_VGPR_OR_INLINE`
  case `SREG`
  case `SREG_LITERAL`
  case `SREG_M0`
  case `SREG_M0_INL`
  case `SREG_NONULL`
  case `SREG_NOVCC`
  case `SSRC`
  case `SSRC_BARRIER_ID`
  case `SSRC_LANESEL`
  case `SSRC_NOLDS`
  case `SSRC_NOLIT`
  case `SSRC_SPECIAL_SCC`
  case `TGT`
  case `VCC`
  case `VERSION`
  case `VGPR`
  case `VGPR_OR_ACCVGPR`
  case `VGPR_OR_LDS`
  case `WAITCNT`
  case `WAITCNT_DEPCTR`
  case `WAIT_ALU`
  case `WAIT_EVENT`
  case `WAIT_MEM_DS`
  case `float`
  def isImmediate: Boolean =
    this match { case `SIMM16` | `SIMM24` | `SIMM32` | `SIMM4` | `SIMM5` | `SIMM64` | `SIMM8` => true; case _ => false }
  def isVector: Boolean = this match { case `VGPR` | `VGPR_OR_ACCVGPR` | `VGPR_OR_LDS` => true; case _ => false }
  def isSource: Boolean = this match {
    case `SRC` | `SRC_ACCVGPR` | `SRC_ACCVGPR_OR_CONST` | `SRC_NOINLINE` | `SRC_NOLDS` | `SRC_NOLIT` | `SRC_SIMPLE` | `SRC_VGPR` | `SRC_VGPR_OR_ACCVGPR` | `SRC_VGPR_OR_ACCVGPR_OR_CONST` | `SRC_VGPR_OR_INLINE` =>
      true;
    case _ => false
  }
  def isMessage: Boolean = this match { case `SENDMSG` | `SENDMSG_RTN` => true; case _ => false }
  def isScalarRegister: Boolean = this match {
    case `SREG` | `SREG_LITERAL` | `SREG_M0` | `SREG_M0_INL` | `SREG_NONULL` | `SREG_NOVCC` => true; case _ => false
  }
  def isScalarSource: Boolean = this match {
    case `SSRC` | `SSRC_BARRIER_ID` | `SSRC_LANESEL` | `SSRC_NOLDS` | `SSRC_NOLIT` | `SSRC_SPECIAL_SCC` => true;
    case _ => false
  }
  def isScalarDestination: Boolean =
    this match { case `SDST` | `SDST_EXEC` | `SDST_M0` | `SDST_NULL` => true; case _ => false }
  def isBareAccumulator: Boolean = this match { case `ACCVGPR` => true; case _ => false }
  def isAccumulatorOnly: Boolean = this match { case `ACCVGPR` | `SRC_ACCVGPR` => true; case _ => false }
  def acceptsAccumulator: Boolean = this match {
    case `ACCVGPR` | `SRC_ACCVGPR` | `SRC_ACCVGPR_OR_CONST` | `SRC_VGPR_OR_ACCVGPR` | `SRC_VGPR_OR_ACCVGPR_OR_CONST` | `VGPR_OR_ACCVGPR` =>
      true;
    case _ => false
  }
  def acceptsConstant: Boolean =
    this match { case `SRC_ACCVGPR_OR_CONST` | `SRC_VGPR_OR_ACCVGPR_OR_CONST` => true; case _ => false }
  def excludesLiteral: Boolean = this match { case `SRC_NOLIT` | `SSRC_NOLIT` => true; case _ => false }
}
