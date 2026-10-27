// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SsrcNolit extends Domain[SsrcNolit] {
  val id: String = "ssrc_nolit"

  def of(slot: Slot): Option[SsrcNolit] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 103 =>
      Some(SsrcNolit.Sgpr(index.unsafe[Int in 0 ~ 103]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SsrcNolit.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(SsrcNolit.VccLo)
    case Slot.Named("vcc_hi") => Some(SsrcNolit.VccHi)
    case Slot.Named("tba_lo") => Some(SsrcNolit.TbaLo)
    case Slot.Named("tba_hi") => Some(SsrcNolit.TbaHi)
    case Slot.Named("tma_lo") => Some(SsrcNolit.TmaLo)
    case Slot.Named("tma_hi") => Some(SsrcNolit.TmaHi)
    case Slot.Named("m0") => Some(SsrcNolit.M0)
    case Slot.Named("exec_lo") => Some(SsrcNolit.ExecLo)
    case Slot.Named("exec_hi") => Some(SsrcNolit.ExecHi)
    case Slot.Named("src_vccz") => Some(SsrcNolit.SrcVccz)
    case Slot.Named("src_execz") => Some(SsrcNolit.SrcExecz)
    case Slot.Named("src_scc") => Some(SsrcNolit.SrcScc)
    case Slot.Named("flat_scratch_lo") => Some(SsrcNolit.FlatScratchLo)
    case Slot.Named("flat_scratch_hi") => Some(SsrcNolit.FlatScratchHi)
    case Slot.Named("xnack_mask_lo") => Some(SsrcNolit.XnackMaskLo)
    case Slot.Named("xnack_mask_hi") => Some(SsrcNolit.XnackMaskHi)
    case Slot.Named("src_shared_base") => Some(SsrcNolit.SrcSharedBase)
    case Slot.Named("src_shared_limit") => Some(SsrcNolit.SrcSharedLimit)
    case Slot.Named("src_private_base") => Some(SsrcNolit.SrcPrivateBase)
    case Slot.Named("src_private_limit") => Some(SsrcNolit.SrcPrivateLimit)
    case Slot.Named("src_pops_exiting_wave_id") => Some(SsrcNolit.SrcPopsExitingWaveId)
    case Slot.Integer(value) if value >= -16 && value <= 64 =>
      Some(SsrcNolit.Integer(value.unsafe[Int in -16 ~ 64]))
    case Slot.Floating(value) => Some(SsrcNolit.Floating(value))
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SsrcNolit extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 103)
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
  case Integer(value: Int in -16 ~ 64)
  case Floating(value: Double)
  def slot: Slot = this match {
    case SsrcNolit.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SsrcNolit.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SsrcNolit.VccLo => Slot.Named("vcc_lo")
    case SsrcNolit.VccHi => Slot.Named("vcc_hi")
    case SsrcNolit.TbaLo => Slot.Named("tba_lo")
    case SsrcNolit.TbaHi => Slot.Named("tba_hi")
    case SsrcNolit.TmaLo => Slot.Named("tma_lo")
    case SsrcNolit.TmaHi => Slot.Named("tma_hi")
    case SsrcNolit.M0 => Slot.Named("m0")
    case SsrcNolit.ExecLo => Slot.Named("exec_lo")
    case SsrcNolit.ExecHi => Slot.Named("exec_hi")
    case SsrcNolit.SrcVccz => Slot.Named("src_vccz")
    case SsrcNolit.SrcExecz => Slot.Named("src_execz")
    case SsrcNolit.SrcScc => Slot.Named("src_scc")
    case SsrcNolit.FlatScratchLo => Slot.Named("flat_scratch_lo")
    case SsrcNolit.FlatScratchHi => Slot.Named("flat_scratch_hi")
    case SsrcNolit.XnackMaskLo => Slot.Named("xnack_mask_lo")
    case SsrcNolit.XnackMaskHi => Slot.Named("xnack_mask_hi")
    case SsrcNolit.SrcSharedBase => Slot.Named("src_shared_base")
    case SsrcNolit.SrcSharedLimit => Slot.Named("src_shared_limit")
    case SsrcNolit.SrcPrivateBase => Slot.Named("src_private_base")
    case SsrcNolit.SrcPrivateLimit => Slot.Named("src_private_limit")
    case SsrcNolit.SrcPopsExitingWaveId => Slot.Named("src_pops_exiting_wave_id")
    case SsrcNolit.Integer(value) => Slot.Integer(value.raw)
    case SsrcNolit.Floating(value) => Slot.Floating(value)
  }
}
