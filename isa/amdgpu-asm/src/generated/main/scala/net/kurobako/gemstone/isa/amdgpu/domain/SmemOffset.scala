// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object SmemOffset extends Domain[SmemOffset] {
  val id: String = "smem_offset"

  def of(slot: Slot): Option[SmemOffset] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(SmemOffset.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SmemOffset.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(SmemOffset.VccLo)
    case Slot.Named("vcc_hi") => Some(SmemOffset.VccHi)
    case Slot.Named("tba_lo") => Some(SmemOffset.TbaLo)
    case Slot.Named("tba_hi") => Some(SmemOffset.TbaHi)
    case Slot.Named("tma_lo") => Some(SmemOffset.TmaLo)
    case Slot.Named("tma_hi") => Some(SmemOffset.TmaHi)
    case Slot.Named("m0") => Some(SmemOffset.M0)
    case Slot.Named("flat_scratch_lo") => Some(SmemOffset.FlatScratchLo)
    case Slot.Named("flat_scratch_hi") => Some(SmemOffset.FlatScratchHi)
    case Slot.Named("xnack_mask_lo") => Some(SmemOffset.XnackMaskLo)
    case Slot.Named("xnack_mask_hi") => Some(SmemOffset.XnackMaskHi)
    case Slot.Named("null") => Some(SmemOffset.Null)
    case Slot.Named("exec_lo") => Some(SmemOffset.ExecLo)
    case Slot.Named("exec_hi") => Some(SmemOffset.ExecHi)
    case Slot.Immediate(value) => Some(SmemOffset.Immediate(value))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum SmemOffset extends DomainValue derives CanEqual {
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
  case Immediate(value: BigInt)
  def slot: Slot = this match {
    case SmemOffset.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SmemOffset.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SmemOffset.VccLo => Slot.Named("vcc_lo")
    case SmemOffset.VccHi => Slot.Named("vcc_hi")
    case SmemOffset.TbaLo => Slot.Named("tba_lo")
    case SmemOffset.TbaHi => Slot.Named("tba_hi")
    case SmemOffset.TmaLo => Slot.Named("tma_lo")
    case SmemOffset.TmaHi => Slot.Named("tma_hi")
    case SmemOffset.M0 => Slot.Named("m0")
    case SmemOffset.FlatScratchLo => Slot.Named("flat_scratch_lo")
    case SmemOffset.FlatScratchHi => Slot.Named("flat_scratch_hi")
    case SmemOffset.XnackMaskLo => Slot.Named("xnack_mask_lo")
    case SmemOffset.XnackMaskHi => Slot.Named("xnack_mask_hi")
    case SmemOffset.Null => Slot.Named("null")
    case SmemOffset.ExecLo => Slot.Named("exec_lo")
    case SmemOffset.ExecHi => Slot.Named("exec_hi")
    case SmemOffset.Immediate(value) => Slot.Immediate(value)
  }
}
