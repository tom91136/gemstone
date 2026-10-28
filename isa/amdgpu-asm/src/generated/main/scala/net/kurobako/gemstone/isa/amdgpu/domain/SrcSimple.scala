// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object SrcSimple extends Domain[SrcSimple] {
  val id: String = "src_simple"

  def of(slot: Slot): Option[SrcSimple] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(SrcSimple.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SrcSimple.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(SrcSimple.Vgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Named("vcc_lo") => Some(SrcSimple.VccLo)
    case Slot.Named("vcc_hi") => Some(SrcSimple.VccHi)
    case Slot.Named("tba_lo") => Some(SrcSimple.TbaLo)
    case Slot.Named("tba_hi") => Some(SrcSimple.TbaHi)
    case Slot.Named("tma_lo") => Some(SrcSimple.TmaLo)
    case Slot.Named("tma_hi") => Some(SrcSimple.TmaHi)
    case Slot.Named("m0") => Some(SrcSimple.M0)
    case Slot.Named("exec_lo") => Some(SrcSimple.ExecLo)
    case Slot.Named("exec_hi") => Some(SrcSimple.ExecHi)
    case Slot.Named("src_vccz") => Some(SrcSimple.SrcVccz)
    case Slot.Named("src_execz") => Some(SrcSimple.SrcExecz)
    case Slot.Named("src_scc") => Some(SrcSimple.SrcScc)
    case Slot.Named("flat_scratch_lo") => Some(SrcSimple.FlatScratchLo)
    case Slot.Named("flat_scratch_hi") => Some(SrcSimple.FlatScratchHi)
    case Slot.Named("xnack_mask_lo") => Some(SrcSimple.XnackMaskLo)
    case Slot.Named("xnack_mask_hi") => Some(SrcSimple.XnackMaskHi)
    case Slot.Named("src_shared_base") => Some(SrcSimple.SrcSharedBase)
    case Slot.Named("src_shared_limit") => Some(SrcSimple.SrcSharedLimit)
    case Slot.Named("src_private_base") => Some(SrcSimple.SrcPrivateBase)
    case Slot.Named("src_private_limit") => Some(SrcSimple.SrcPrivateLimit)
    case Slot.Named("src_pops_exiting_wave_id") => Some(SrcSimple.SrcPopsExitingWaveId)
    case Slot.Named("null") => Some(SrcSimple.Null)
    case Slot.Named("src_flat_scratch_base_lo") => Some(SrcSimple.SrcFlatScratchBaseLo)
    case Slot.Named("src_flat_scratch_base_hi") => Some(SrcSimple.SrcFlatScratchBaseHi)
    case Slot.Integer(value) if value >= -16 && value <= 64 =>
      Some(SrcSimple.Integer(value.unsafe[Int in -16 ~ 64]))
    case Slot.Floating(value) => Some(SrcSimple.Floating(value))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum SrcSimple extends DomainValue derives CanEqual {
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
  case SrcFlatScratchBaseLo
  case SrcFlatScratchBaseHi
  case Integer(value: Int in -16 ~ 64)
  case Floating(value: Double)
  def slot: Slot = this match {
    case SrcSimple.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SrcSimple.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SrcSimple.Vgpr(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
    case SrcSimple.VccLo => Slot.Named("vcc_lo")
    case SrcSimple.VccHi => Slot.Named("vcc_hi")
    case SrcSimple.TbaLo => Slot.Named("tba_lo")
    case SrcSimple.TbaHi => Slot.Named("tba_hi")
    case SrcSimple.TmaLo => Slot.Named("tma_lo")
    case SrcSimple.TmaHi => Slot.Named("tma_hi")
    case SrcSimple.M0 => Slot.Named("m0")
    case SrcSimple.ExecLo => Slot.Named("exec_lo")
    case SrcSimple.ExecHi => Slot.Named("exec_hi")
    case SrcSimple.SrcVccz => Slot.Named("src_vccz")
    case SrcSimple.SrcExecz => Slot.Named("src_execz")
    case SrcSimple.SrcScc => Slot.Named("src_scc")
    case SrcSimple.FlatScratchLo => Slot.Named("flat_scratch_lo")
    case SrcSimple.FlatScratchHi => Slot.Named("flat_scratch_hi")
    case SrcSimple.XnackMaskLo => Slot.Named("xnack_mask_lo")
    case SrcSimple.XnackMaskHi => Slot.Named("xnack_mask_hi")
    case SrcSimple.SrcSharedBase => Slot.Named("src_shared_base")
    case SrcSimple.SrcSharedLimit => Slot.Named("src_shared_limit")
    case SrcSimple.SrcPrivateBase => Slot.Named("src_private_base")
    case SrcSimple.SrcPrivateLimit => Slot.Named("src_private_limit")
    case SrcSimple.SrcPopsExitingWaveId => Slot.Named("src_pops_exiting_wave_id")
    case SrcSimple.Null => Slot.Named("null")
    case SrcSimple.SrcFlatScratchBaseLo => Slot.Named("src_flat_scratch_base_lo")
    case SrcSimple.SrcFlatScratchBaseHi => Slot.Named("src_flat_scratch_base_hi")
    case SrcSimple.Integer(value) => Slot.Integer(value.raw)
    case SrcSimple.Floating(value) => Slot.Floating(value)
  }
}
