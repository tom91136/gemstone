// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object Sreg extends Domain[Sreg] {
  val id: String = "sreg"

  def of(slot: Slot): Option[Sreg] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(Sreg.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(Sreg.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(Sreg.VccLo)
    case Slot.Named("vcc_hi") => Some(Sreg.VccHi)
    case Slot.Named("tba_lo") => Some(Sreg.TbaLo)
    case Slot.Named("tba_hi") => Some(Sreg.TbaHi)
    case Slot.Named("tma_lo") => Some(Sreg.TmaLo)
    case Slot.Named("tma_hi") => Some(Sreg.TmaHi)
    case Slot.Named("flat_scratch_lo") => Some(Sreg.FlatScratchLo)
    case Slot.Named("flat_scratch_hi") => Some(Sreg.FlatScratchHi)
    case Slot.Named("xnack_mask_lo") => Some(Sreg.XnackMaskLo)
    case Slot.Named("xnack_mask_hi") => Some(Sreg.XnackMaskHi)
    case Slot.Named("null") => Some(Sreg.Null)
    case Slot.Named("exec_lo") => Some(Sreg.ExecLo)
    case Slot.Named("exec_hi") => Some(Sreg.ExecHi)
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Sreg extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 105)
  case Ttmp(index: Int in 0 ~ 15)
  case VccLo
  case VccHi
  case TbaLo
  case TbaHi
  case TmaLo
  case TmaHi
  case FlatScratchLo
  case FlatScratchHi
  case XnackMaskLo
  case XnackMaskHi
  case Null
  case ExecLo
  case ExecHi
  def slot: Slot = this match {
    case Sreg.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case Sreg.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case Sreg.VccLo => Slot.Named("vcc_lo")
    case Sreg.VccHi => Slot.Named("vcc_hi")
    case Sreg.TbaLo => Slot.Named("tba_lo")
    case Sreg.TbaHi => Slot.Named("tba_hi")
    case Sreg.TmaLo => Slot.Named("tma_lo")
    case Sreg.TmaHi => Slot.Named("tma_hi")
    case Sreg.FlatScratchLo => Slot.Named("flat_scratch_lo")
    case Sreg.FlatScratchHi => Slot.Named("flat_scratch_hi")
    case Sreg.XnackMaskLo => Slot.Named("xnack_mask_lo")
    case Sreg.XnackMaskHi => Slot.Named("xnack_mask_hi")
    case Sreg.Null => Slot.Named("null")
    case Sreg.ExecLo => Slot.Named("exec_lo")
    case Sreg.ExecHi => Slot.Named("exec_hi")
  }
}
