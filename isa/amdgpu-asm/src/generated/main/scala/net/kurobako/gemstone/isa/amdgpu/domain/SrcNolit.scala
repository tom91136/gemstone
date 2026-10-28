// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object SrcNolit extends Domain[SrcNolit] {
  val id: String = "src_nolit"

  def of(slot: Slot): Option[SrcNolit] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 103 =>
      Some(SrcNolit.Sgpr(index.unsafe[Int in 0 ~ 103]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SrcNolit.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(SrcNolit.Vgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Named("vcc_lo") => Some(SrcNolit.VccLo)
    case Slot.Named("vcc_hi") => Some(SrcNolit.VccHi)
    case Slot.Named("tba_lo") => Some(SrcNolit.TbaLo)
    case Slot.Named("tba_hi") => Some(SrcNolit.TbaHi)
    case Slot.Named("tma_lo") => Some(SrcNolit.TmaLo)
    case Slot.Named("tma_hi") => Some(SrcNolit.TmaHi)
    case Slot.Named("m0") => Some(SrcNolit.M0)
    case Slot.Named("exec_lo") => Some(SrcNolit.ExecLo)
    case Slot.Named("exec_hi") => Some(SrcNolit.ExecHi)
    case Slot.Named("src_vccz") => Some(SrcNolit.SrcVccz)
    case Slot.Named("src_execz") => Some(SrcNolit.SrcExecz)
    case Slot.Named("src_scc") => Some(SrcNolit.SrcScc)
    case Slot.Named("src_lds_direct") => Some(SrcNolit.SrcLdsDirect)
    case Slot.Named("flat_scratch_lo") => Some(SrcNolit.FlatScratchLo)
    case Slot.Named("flat_scratch_hi") => Some(SrcNolit.FlatScratchHi)
    case Slot.Named("xnack_mask_lo") => Some(SrcNolit.XnackMaskLo)
    case Slot.Named("xnack_mask_hi") => Some(SrcNolit.XnackMaskHi)
    case Slot.Named("src_shared_base") => Some(SrcNolit.SrcSharedBase)
    case Slot.Named("src_shared_limit") => Some(SrcNolit.SrcSharedLimit)
    case Slot.Named("src_private_base") => Some(SrcNolit.SrcPrivateBase)
    case Slot.Named("src_private_limit") => Some(SrcNolit.SrcPrivateLimit)
    case Slot.Named("src_pops_exiting_wave_id") => Some(SrcNolit.SrcPopsExitingWaveId)
    case Slot.Integer(value) if value >= -16 && value <= 64 =>
      Some(SrcNolit.Integer(value.unsafe[Int in -16 ~ 64]))
    case Slot.Floating(value) => Some(SrcNolit.Floating(value))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum SrcNolit extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 103)
  case Ttmp(index: Int in 0 ~ 15)
  case Vgpr(index: Int in 0 ~ 255)
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
  case SrcLdsDirect
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
    case SrcNolit.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SrcNolit.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SrcNolit.Vgpr(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
    case SrcNolit.VccLo => Slot.Named("vcc_lo")
    case SrcNolit.VccHi => Slot.Named("vcc_hi")
    case SrcNolit.TbaLo => Slot.Named("tba_lo")
    case SrcNolit.TbaHi => Slot.Named("tba_hi")
    case SrcNolit.TmaLo => Slot.Named("tma_lo")
    case SrcNolit.TmaHi => Slot.Named("tma_hi")
    case SrcNolit.M0 => Slot.Named("m0")
    case SrcNolit.ExecLo => Slot.Named("exec_lo")
    case SrcNolit.ExecHi => Slot.Named("exec_hi")
    case SrcNolit.SrcVccz => Slot.Named("src_vccz")
    case SrcNolit.SrcExecz => Slot.Named("src_execz")
    case SrcNolit.SrcScc => Slot.Named("src_scc")
    case SrcNolit.SrcLdsDirect => Slot.Named("src_lds_direct")
    case SrcNolit.FlatScratchLo => Slot.Named("flat_scratch_lo")
    case SrcNolit.FlatScratchHi => Slot.Named("flat_scratch_hi")
    case SrcNolit.XnackMaskLo => Slot.Named("xnack_mask_lo")
    case SrcNolit.XnackMaskHi => Slot.Named("xnack_mask_hi")
    case SrcNolit.SrcSharedBase => Slot.Named("src_shared_base")
    case SrcNolit.SrcSharedLimit => Slot.Named("src_shared_limit")
    case SrcNolit.SrcPrivateBase => Slot.Named("src_private_base")
    case SrcNolit.SrcPrivateLimit => Slot.Named("src_private_limit")
    case SrcNolit.SrcPopsExitingWaveId => Slot.Named("src_pops_exiting_wave_id")
    case SrcNolit.Integer(value) => Slot.Integer(value.raw)
    case SrcNolit.Floating(value) => Slot.Floating(value)
  }
}
