// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Sdst extends Domain[Sdst] {
  val id: String = "sdst"

  def of(slot: Slot): Option[Sdst] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(Sdst.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(Sdst.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(Sdst.VccLo)
    case Slot.Named("vcc_hi") => Some(Sdst.VccHi)
    case Slot.Named("tba_lo") => Some(Sdst.TbaLo)
    case Slot.Named("tba_hi") => Some(Sdst.TbaHi)
    case Slot.Named("tma_lo") => Some(Sdst.TmaLo)
    case Slot.Named("tma_hi") => Some(Sdst.TmaHi)
    case Slot.Named("m0") => Some(Sdst.M0)
    case Slot.Named("exec_lo") => Some(Sdst.ExecLo)
    case Slot.Named("exec_hi") => Some(Sdst.ExecHi)
    case Slot.Named("flat_scratch_lo") => Some(Sdst.FlatScratchLo)
    case Slot.Named("flat_scratch_hi") => Some(Sdst.FlatScratchHi)
    case Slot.Named("xnack_mask_lo") => Some(Sdst.XnackMaskLo)
    case Slot.Named("xnack_mask_hi") => Some(Sdst.XnackMaskHi)
    case Slot.Named("null") => Some(Sdst.Null)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Sdst extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 105)
  case Ttmp(index: Int in 0 ~ 15)
  case VccLo
  case VccHi
  case TbaLo
  case TbaHi
  case TmaLo
  case TmaHi
  case M0
  case ExecLo
  case ExecHi
  case FlatScratchLo
  case FlatScratchHi
  case XnackMaskLo
  case XnackMaskHi
  case Null
  def slot: Slot = this match {
    case Sdst.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case Sdst.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case Sdst.VccLo => Slot.Named("vcc_lo")
    case Sdst.VccHi => Slot.Named("vcc_hi")
    case Sdst.TbaLo => Slot.Named("tba_lo")
    case Sdst.TbaHi => Slot.Named("tba_hi")
    case Sdst.TmaLo => Slot.Named("tma_lo")
    case Sdst.TmaHi => Slot.Named("tma_hi")
    case Sdst.M0 => Slot.Named("m0")
    case Sdst.ExecLo => Slot.Named("exec_lo")
    case Sdst.ExecHi => Slot.Named("exec_hi")
    case Sdst.FlatScratchLo => Slot.Named("flat_scratch_lo")
    case Sdst.FlatScratchHi => Slot.Named("flat_scratch_hi")
    case Sdst.XnackMaskLo => Slot.Named("xnack_mask_lo")
    case Sdst.XnackMaskHi => Slot.Named("xnack_mask_hi")
    case Sdst.Null => Slot.Named("null")
  }
}
