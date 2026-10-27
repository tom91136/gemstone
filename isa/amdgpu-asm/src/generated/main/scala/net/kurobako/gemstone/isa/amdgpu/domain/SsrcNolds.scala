// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SsrcNolds extends Domain[SsrcNolds] {
  val id: String = "ssrc_nolds"

  def of(slot: Slot): Option[SsrcNolds] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(SsrcNolds.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SsrcNolds.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(SsrcNolds.VccLo)
    case Slot.Named("vcc_hi") => Some(SsrcNolds.VccHi)
    case Slot.Named("m0") => Some(SsrcNolds.M0)
    case Slot.Named("null") => Some(SsrcNolds.Null)
    case Slot.Named("exec_lo") => Some(SsrcNolds.ExecLo)
    case Slot.Named("exec_hi") => Some(SsrcNolds.ExecHi)
    case Slot.Named("src_shared_base") => Some(SsrcNolds.SrcSharedBase)
    case Slot.Named("src_shared_limit") => Some(SsrcNolds.SrcSharedLimit)
    case Slot.Named("src_private_base") => Some(SsrcNolds.SrcPrivateBase)
    case Slot.Named("src_private_limit") => Some(SsrcNolds.SrcPrivateLimit)
    case Slot.Named("src_pops_exiting_wave_id") => Some(SsrcNolds.SrcPopsExitingWaveId)
    case Slot.Named("src_vccz") => Some(SsrcNolds.SrcVccz)
    case Slot.Named("src_execz") => Some(SsrcNolds.SrcExecz)
    case Slot.Named("src_scc") => Some(SsrcNolds.SrcScc)
    case Slot.Integer(value) if value >= -16 && value <= 64 =>
      Some(SsrcNolds.Integer(value.unsafe[Int in -16 ~ 64]))
    case Slot.Floating(value) => Some(SsrcNolds.Floating(value))
    case Slot.Literal(4) => Some(SsrcNolds.Literal)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SsrcNolds extends DomainValue derives CanEqual {
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
  case Literal
  def slot: Slot = this match {
    case SsrcNolds.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SsrcNolds.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SsrcNolds.VccLo => Slot.Named("vcc_lo")
    case SsrcNolds.VccHi => Slot.Named("vcc_hi")
    case SsrcNolds.M0 => Slot.Named("m0")
    case SsrcNolds.Null => Slot.Named("null")
    case SsrcNolds.ExecLo => Slot.Named("exec_lo")
    case SsrcNolds.ExecHi => Slot.Named("exec_hi")
    case SsrcNolds.SrcSharedBase => Slot.Named("src_shared_base")
    case SsrcNolds.SrcSharedLimit => Slot.Named("src_shared_limit")
    case SsrcNolds.SrcPrivateBase => Slot.Named("src_private_base")
    case SsrcNolds.SrcPrivateLimit => Slot.Named("src_private_limit")
    case SsrcNolds.SrcPopsExitingWaveId => Slot.Named("src_pops_exiting_wave_id")
    case SsrcNolds.SrcVccz => Slot.Named("src_vccz")
    case SsrcNolds.SrcExecz => Slot.Named("src_execz")
    case SsrcNolds.SrcScc => Slot.Named("src_scc")
    case SsrcNolds.Integer(value) => Slot.Integer(value.raw)
    case SsrcNolds.Floating(value) => Slot.Floating(value)
    case SsrcNolds.Literal => Slot.Literal(4)
  }
}
