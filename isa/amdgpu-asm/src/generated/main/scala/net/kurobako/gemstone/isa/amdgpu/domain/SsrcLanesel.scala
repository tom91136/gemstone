// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SsrcLanesel extends Domain[SsrcLanesel] {
  val id: String = "ssrc_lanesel"

  def of(slot: Slot): Option[SsrcLanesel] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(SsrcLanesel.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SsrcLanesel.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(SsrcLanesel.VccLo)
    case Slot.Named("vcc_hi") => Some(SsrcLanesel.VccHi)
    case Slot.Named("tba_lo") => Some(SsrcLanesel.TbaLo)
    case Slot.Named("tba_hi") => Some(SsrcLanesel.TbaHi)
    case Slot.Named("tma_lo") => Some(SsrcLanesel.TmaLo)
    case Slot.Named("tma_hi") => Some(SsrcLanesel.TmaHi)
    case Slot.Named("m0") => Some(SsrcLanesel.M0)
    case Slot.Named("flat_scratch_lo") => Some(SsrcLanesel.FlatScratchLo)
    case Slot.Named("flat_scratch_hi") => Some(SsrcLanesel.FlatScratchHi)
    case Slot.Named("xnack_mask_lo") => Some(SsrcLanesel.XnackMaskLo)
    case Slot.Named("xnack_mask_hi") => Some(SsrcLanesel.XnackMaskHi)
    case Slot.Named("null") => Some(SsrcLanesel.Null)
    case Slot.Named("exec_lo") => Some(SsrcLanesel.ExecLo)
    case Slot.Named("exec_hi") => Some(SsrcLanesel.ExecHi)
    case Slot.Integer(value) if value >= 0 && value <= 63 =>
      Some(SsrcLanesel.Integer(value.unsafe[Int in 0 ~ 63]))
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SsrcLanesel extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 105)
  case Ttmp(index: Int in 0 ~ 15)
  case VccLo
  case VccHi
  case TbaLo
  case TbaHi
  case TmaLo
  case TmaHi
  case M0
  case FlatScratchLo
  case FlatScratchHi
  case XnackMaskLo
  case XnackMaskHi
  case Null
  case ExecLo
  case ExecHi
  case Integer(value: Int in 0 ~ 63)
  def slot: Slot = this match {
    case SsrcLanesel.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SsrcLanesel.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SsrcLanesel.VccLo => Slot.Named("vcc_lo")
    case SsrcLanesel.VccHi => Slot.Named("vcc_hi")
    case SsrcLanesel.TbaLo => Slot.Named("tba_lo")
    case SsrcLanesel.TbaHi => Slot.Named("tba_hi")
    case SsrcLanesel.TmaLo => Slot.Named("tma_lo")
    case SsrcLanesel.TmaHi => Slot.Named("tma_hi")
    case SsrcLanesel.M0 => Slot.Named("m0")
    case SsrcLanesel.FlatScratchLo => Slot.Named("flat_scratch_lo")
    case SsrcLanesel.FlatScratchHi => Slot.Named("flat_scratch_hi")
    case SsrcLanesel.XnackMaskLo => Slot.Named("xnack_mask_lo")
    case SsrcLanesel.XnackMaskHi => Slot.Named("xnack_mask_hi")
    case SsrcLanesel.Null => Slot.Named("null")
    case SsrcLanesel.ExecLo => Slot.Named("exec_lo")
    case SsrcLanesel.ExecHi => Slot.Named("exec_hi")
    case SsrcLanesel.Integer(value) => Slot.Integer(value.raw)
  }
}
