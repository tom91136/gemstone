// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SregNovcc extends Domain[SregNovcc] {
  val id: String = "sreg_novcc"

  def of(slot: Slot): Option[SregNovcc] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 103 =>
      Some(SregNovcc.Sgpr(index.unsafe[Int in 0 ~ 103]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SregNovcc.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("tba_lo") => Some(SregNovcc.TbaLo)
    case Slot.Named("tba_hi") => Some(SregNovcc.TbaHi)
    case Slot.Named("tma_lo") => Some(SregNovcc.TmaLo)
    case Slot.Named("tma_hi") => Some(SregNovcc.TmaHi)
    case Slot.Named("flat_scratch_lo") => Some(SregNovcc.FlatScratchLo)
    case Slot.Named("flat_scratch_hi") => Some(SregNovcc.FlatScratchHi)
    case Slot.Named("xnack_mask_lo") => Some(SregNovcc.XnackMaskLo)
    case Slot.Named("xnack_mask_hi") => Some(SregNovcc.XnackMaskHi)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SregNovcc extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 103)
  case Ttmp(index: Int in 0 ~ 15)
  case TbaLo
  case TbaHi
  case TmaLo
  case TmaHi
  case FlatScratchLo
  case FlatScratchHi
  case XnackMaskLo
  case XnackMaskHi
  def slot: Slot = this match {
    case SregNovcc.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SregNovcc.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SregNovcc.TbaLo => Slot.Named("tba_lo")
    case SregNovcc.TbaHi => Slot.Named("tba_hi")
    case SregNovcc.TmaLo => Slot.Named("tma_lo")
    case SregNovcc.TmaHi => Slot.Named("tma_hi")
    case SregNovcc.FlatScratchLo => Slot.Named("flat_scratch_lo")
    case SregNovcc.FlatScratchHi => Slot.Named("flat_scratch_hi")
    case SregNovcc.XnackMaskLo => Slot.Named("xnack_mask_lo")
    case SregNovcc.XnackMaskHi => Slot.Named("xnack_mask_hi")
  }
}
