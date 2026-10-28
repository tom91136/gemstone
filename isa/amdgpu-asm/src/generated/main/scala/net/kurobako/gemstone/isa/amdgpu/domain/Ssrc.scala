// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object Ssrc extends Domain[Ssrc] {
  val id: String = "ssrc"

  def of(slot: Slot): Option[Ssrc] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(Ssrc.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(Ssrc.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(Ssrc.VccLo)
    case Slot.Named("vcc_hi") => Some(Ssrc.VccHi)
    case Slot.Named("tba_lo") => Some(Ssrc.TbaLo)
    case Slot.Named("tba_hi") => Some(Ssrc.TbaHi)
    case Slot.Named("tma_lo") => Some(Ssrc.TmaLo)
    case Slot.Named("tma_hi") => Some(Ssrc.TmaHi)
    case Slot.Named("m0") => Some(Ssrc.M0)
    case Slot.Named("exec_lo") => Some(Ssrc.ExecLo)
    case Slot.Named("exec_hi") => Some(Ssrc.ExecHi)
    case Slot.Named("src_vccz") => Some(Ssrc.SrcVccz)
    case Slot.Named("src_execz") => Some(Ssrc.SrcExecz)
    case Slot.Named("src_scc") => Some(Ssrc.SrcScc)
    case Slot.Named("flat_scratch_lo") => Some(Ssrc.FlatScratchLo)
    case Slot.Named("flat_scratch_hi") => Some(Ssrc.FlatScratchHi)
    case Slot.Named("xnack_mask_lo") => Some(Ssrc.XnackMaskLo)
    case Slot.Named("xnack_mask_hi") => Some(Ssrc.XnackMaskHi)
    case Slot.Named("src_shared_base") => Some(Ssrc.SrcSharedBase)
    case Slot.Named("src_shared_limit") => Some(Ssrc.SrcSharedLimit)
    case Slot.Named("src_private_base") => Some(Ssrc.SrcPrivateBase)
    case Slot.Named("src_private_limit") => Some(Ssrc.SrcPrivateLimit)
    case Slot.Named("src_pops_exiting_wave_id") => Some(Ssrc.SrcPopsExitingWaveId)
    case Slot.Named("null") => Some(Ssrc.Null)
    case Slot.Named("src_flat_scratch_base_lo") => Some(Ssrc.SrcFlatScratchBaseLo)
    case Slot.Named("src_flat_scratch_base_hi") => Some(Ssrc.SrcFlatScratchBaseHi)
    case Slot.Named("src_lds_direct") => Some(Ssrc.SrcLdsDirect)
    case Slot.Integer(value) if value >= -16 && value <= 64 =>
      Some(Ssrc.Integer(value.unsafe[Int in -16 ~ 64]))
    case Slot.Floating(value) => Some(Ssrc.Floating(value))
    case Slot.Literal(4) => Some(Ssrc.Literal)
    case Slot.Literal(8) => Some(Ssrc.Literal64)
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Ssrc extends DomainValue derives CanEqual {
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
  case SrcVccz
  case SrcExecz
  case SrcScc
  case FlatScratchLo
  case FlatScratchHi
  case XnackMaskLo
  case XnackMaskHi
  case SrcSharedBase
  case SrcSharedLimit
  case SrcPrivateBase
  case SrcPrivateLimit
  case SrcPopsExitingWaveId
  case Null
  case SrcFlatScratchBaseLo
  case SrcFlatScratchBaseHi
  case SrcLdsDirect
  case Integer(value: Int in -16 ~ 64)
  case Floating(value: Double)
  case Literal
  case Literal64
  def slot: Slot = this match {
    case Ssrc.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case Ssrc.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case Ssrc.VccLo => Slot.Named("vcc_lo")
    case Ssrc.VccHi => Slot.Named("vcc_hi")
    case Ssrc.TbaLo => Slot.Named("tba_lo")
    case Ssrc.TbaHi => Slot.Named("tba_hi")
    case Ssrc.TmaLo => Slot.Named("tma_lo")
    case Ssrc.TmaHi => Slot.Named("tma_hi")
    case Ssrc.M0 => Slot.Named("m0")
    case Ssrc.ExecLo => Slot.Named("exec_lo")
    case Ssrc.ExecHi => Slot.Named("exec_hi")
    case Ssrc.SrcVccz => Slot.Named("src_vccz")
    case Ssrc.SrcExecz => Slot.Named("src_execz")
    case Ssrc.SrcScc => Slot.Named("src_scc")
    case Ssrc.FlatScratchLo => Slot.Named("flat_scratch_lo")
    case Ssrc.FlatScratchHi => Slot.Named("flat_scratch_hi")
    case Ssrc.XnackMaskLo => Slot.Named("xnack_mask_lo")
    case Ssrc.XnackMaskHi => Slot.Named("xnack_mask_hi")
    case Ssrc.SrcSharedBase => Slot.Named("src_shared_base")
    case Ssrc.SrcSharedLimit => Slot.Named("src_shared_limit")
    case Ssrc.SrcPrivateBase => Slot.Named("src_private_base")
    case Ssrc.SrcPrivateLimit => Slot.Named("src_private_limit")
    case Ssrc.SrcPopsExitingWaveId => Slot.Named("src_pops_exiting_wave_id")
    case Ssrc.Null => Slot.Named("null")
    case Ssrc.SrcFlatScratchBaseLo => Slot.Named("src_flat_scratch_base_lo")
    case Ssrc.SrcFlatScratchBaseHi => Slot.Named("src_flat_scratch_base_hi")
    case Ssrc.SrcLdsDirect => Slot.Named("src_lds_direct")
    case Ssrc.Integer(value) => Slot.Integer(value.raw)
    case Ssrc.Floating(value) => Slot.Floating(value)
    case Ssrc.Literal => Slot.Literal(4)
    case Ssrc.Literal64 => Slot.Literal(8)
  }
}
