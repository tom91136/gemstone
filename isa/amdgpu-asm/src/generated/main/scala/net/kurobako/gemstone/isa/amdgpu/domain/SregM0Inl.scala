// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object SregM0Inl extends Domain[SregM0Inl] {
  val id: String = "sreg_m0_inl"

  def of(slot: Slot): Option[SregM0Inl] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(SregM0Inl.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SregM0Inl.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(SregM0Inl.VccLo)
    case Slot.Named("vcc_hi") => Some(SregM0Inl.VccHi)
    case Slot.Named("m0") => Some(SregM0Inl.M0)
    case Slot.Named("null") => Some(SregM0Inl.Null)
    case Slot.Named("exec_lo") => Some(SregM0Inl.ExecLo)
    case Slot.Named("exec_hi") => Some(SregM0Inl.ExecHi)
    case Slot.Named("src_shared_base") => Some(SregM0Inl.SrcSharedBase)
    case Slot.Named("src_shared_limit") => Some(SregM0Inl.SrcSharedLimit)
    case Slot.Named("src_private_base") => Some(SregM0Inl.SrcPrivateBase)
    case Slot.Named("src_private_limit") => Some(SregM0Inl.SrcPrivateLimit)
    case Slot.Named("src_pops_exiting_wave_id") => Some(SregM0Inl.SrcPopsExitingWaveId)
    case Slot.Named("src_vccz") => Some(SregM0Inl.SrcVccz)
    case Slot.Named("src_execz") => Some(SregM0Inl.SrcExecz)
    case Slot.Named("src_scc") => Some(SregM0Inl.SrcScc)
    case Slot.Integer(value) if value >= -16 && value <= 64 =>
      Some(SregM0Inl.Integer(value.unsafe[Int in -16 ~ 64]))
    case Slot.Floating(value) => Some(SregM0Inl.Floating(value))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum SregM0Inl extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 105)
  case Ttmp(index: Int in 0 ~ 15)
  case VccLo
  case VccHi
  case M0
  case Null
  case ExecLo
  case ExecHi
  case SrcSharedBase
  case SrcSharedLimit
  case SrcPrivateBase
  case SrcPrivateLimit
  case SrcPopsExitingWaveId
  case SrcVccz
  case SrcExecz
  case SrcScc
  case Integer(value: Int in -16 ~ 64)
  case Floating(value: Double)
  def slot: Slot = this match {
    case SregM0Inl.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SregM0Inl.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SregM0Inl.VccLo => Slot.Named("vcc_lo")
    case SregM0Inl.VccHi => Slot.Named("vcc_hi")
    case SregM0Inl.M0 => Slot.Named("m0")
    case SregM0Inl.Null => Slot.Named("null")
    case SregM0Inl.ExecLo => Slot.Named("exec_lo")
    case SregM0Inl.ExecHi => Slot.Named("exec_hi")
    case SregM0Inl.SrcSharedBase => Slot.Named("src_shared_base")
    case SregM0Inl.SrcSharedLimit => Slot.Named("src_shared_limit")
    case SregM0Inl.SrcPrivateBase => Slot.Named("src_private_base")
    case SregM0Inl.SrcPrivateLimit => Slot.Named("src_private_limit")
    case SregM0Inl.SrcPopsExitingWaveId => Slot.Named("src_pops_exiting_wave_id")
    case SregM0Inl.SrcVccz => Slot.Named("src_vccz")
    case SregM0Inl.SrcExecz => Slot.Named("src_execz")
    case SregM0Inl.SrcScc => Slot.Named("src_scc")
    case SregM0Inl.Integer(value) => Slot.Integer(value.raw)
    case SregM0Inl.Floating(value) => Slot.Floating(value)
  }
}
