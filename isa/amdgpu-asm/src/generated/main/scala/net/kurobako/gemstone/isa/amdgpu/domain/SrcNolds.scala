// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SrcNolds extends Domain[SrcNolds] {
  val id: String = "src_nolds"

  def of(slot: Slot): Option[SrcNolds] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(SrcNolds.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SrcNolds.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(SrcNolds.Vgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Named("vcc_lo") => Some(SrcNolds.VccLo)
    case Slot.Named("vcc_hi") => Some(SrcNolds.VccHi)
    case Slot.Named("tba_lo") => Some(SrcNolds.TbaLo)
    case Slot.Named("tba_hi") => Some(SrcNolds.TbaHi)
    case Slot.Named("tma_lo") => Some(SrcNolds.TmaLo)
    case Slot.Named("tma_hi") => Some(SrcNolds.TmaHi)
    case Slot.Named("m0") => Some(SrcNolds.M0)
    case Slot.Named("exec_lo") => Some(SrcNolds.ExecLo)
    case Slot.Named("exec_hi") => Some(SrcNolds.ExecHi)
    case Slot.Named("src_vccz") => Some(SrcNolds.SrcVccz)
    case Slot.Named("src_execz") => Some(SrcNolds.SrcExecz)
    case Slot.Named("src_scc") => Some(SrcNolds.SrcScc)
    case Slot.Named("flat_scratch_lo") => Some(SrcNolds.FlatScratchLo)
    case Slot.Named("flat_scratch_hi") => Some(SrcNolds.FlatScratchHi)
    case Slot.Named("xnack_mask_lo") => Some(SrcNolds.XnackMaskLo)
    case Slot.Named("xnack_mask_hi") => Some(SrcNolds.XnackMaskHi)
    case Slot.Named("src_shared_base") => Some(SrcNolds.SrcSharedBase)
    case Slot.Named("src_shared_limit") => Some(SrcNolds.SrcSharedLimit)
    case Slot.Named("src_private_base") => Some(SrcNolds.SrcPrivateBase)
    case Slot.Named("src_private_limit") => Some(SrcNolds.SrcPrivateLimit)
    case Slot.Named("src_pops_exiting_wave_id") => Some(SrcNolds.SrcPopsExitingWaveId)
    case Slot.Named("null") => Some(SrcNolds.Null)
    case Slot.Integer(value) if value >= -16 && value <= 64 =>
      Some(SrcNolds.Integer(value.unsafe[Int in -16 ~ 64]))
    case Slot.Floating(value) => Some(SrcNolds.Floating(value))
    case Slot.Literal(4) => Some(SrcNolds.Literal)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SrcNolds extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 105)
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
  case Integer(value: Int in -16 ~ 64)
  case Floating(value: Double)
  case Literal
  def slot: Slot = this match {
    case SrcNolds.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SrcNolds.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SrcNolds.Vgpr(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
    case SrcNolds.VccLo => Slot.Named("vcc_lo")
    case SrcNolds.VccHi => Slot.Named("vcc_hi")
    case SrcNolds.TbaLo => Slot.Named("tba_lo")
    case SrcNolds.TbaHi => Slot.Named("tba_hi")
    case SrcNolds.TmaLo => Slot.Named("tma_lo")
    case SrcNolds.TmaHi => Slot.Named("tma_hi")
    case SrcNolds.M0 => Slot.Named("m0")
    case SrcNolds.ExecLo => Slot.Named("exec_lo")
    case SrcNolds.ExecHi => Slot.Named("exec_hi")
    case SrcNolds.SrcVccz => Slot.Named("src_vccz")
    case SrcNolds.SrcExecz => Slot.Named("src_execz")
    case SrcNolds.SrcScc => Slot.Named("src_scc")
    case SrcNolds.FlatScratchLo => Slot.Named("flat_scratch_lo")
    case SrcNolds.FlatScratchHi => Slot.Named("flat_scratch_hi")
    case SrcNolds.XnackMaskLo => Slot.Named("xnack_mask_lo")
    case SrcNolds.XnackMaskHi => Slot.Named("xnack_mask_hi")
    case SrcNolds.SrcSharedBase => Slot.Named("src_shared_base")
    case SrcNolds.SrcSharedLimit => Slot.Named("src_shared_limit")
    case SrcNolds.SrcPrivateBase => Slot.Named("src_private_base")
    case SrcNolds.SrcPrivateLimit => Slot.Named("src_private_limit")
    case SrcNolds.SrcPopsExitingWaveId => Slot.Named("src_pops_exiting_wave_id")
    case SrcNolds.Null => Slot.Named("null")
    case SrcNolds.Integer(value) => Slot.Integer(value.raw)
    case SrcNolds.Floating(value) => Slot.Floating(value)
    case SrcNolds.Literal => Slot.Literal(4)
  }
}
