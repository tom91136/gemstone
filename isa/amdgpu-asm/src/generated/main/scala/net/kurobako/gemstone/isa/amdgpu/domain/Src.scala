// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object Src extends Domain[Src] {
  val id: String = "src"

  def of(slot: Slot): Option[Src] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(Src.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(Src.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(Src.Vgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Named("vcc_lo") => Some(Src.VccLo)
    case Slot.Named("vcc_hi") => Some(Src.VccHi)
    case Slot.Named("tba_lo") => Some(Src.TbaLo)
    case Slot.Named("tba_hi") => Some(Src.TbaHi)
    case Slot.Named("tma_lo") => Some(Src.TmaLo)
    case Slot.Named("tma_hi") => Some(Src.TmaHi)
    case Slot.Named("m0") => Some(Src.M0)
    case Slot.Named("exec_lo") => Some(Src.ExecLo)
    case Slot.Named("exec_hi") => Some(Src.ExecHi)
    case Slot.Named("src_vccz") => Some(Src.SrcVccz)
    case Slot.Named("src_execz") => Some(Src.SrcExecz)
    case Slot.Named("src_scc") => Some(Src.SrcScc)
    case Slot.Named("src_lds_direct") => Some(Src.SrcLdsDirect)
    case Slot.Named("flat_scratch_lo") => Some(Src.FlatScratchLo)
    case Slot.Named("flat_scratch_hi") => Some(Src.FlatScratchHi)
    case Slot.Named("xnack_mask_lo") => Some(Src.XnackMaskLo)
    case Slot.Named("xnack_mask_hi") => Some(Src.XnackMaskHi)
    case Slot.Named("src_shared_base") => Some(Src.SrcSharedBase)
    case Slot.Named("src_shared_limit") => Some(Src.SrcSharedLimit)
    case Slot.Named("src_private_base") => Some(Src.SrcPrivateBase)
    case Slot.Named("src_private_limit") => Some(Src.SrcPrivateLimit)
    case Slot.Named("src_pops_exiting_wave_id") => Some(Src.SrcPopsExitingWaveId)
    case Slot.Named("null") => Some(Src.Null)
    case Slot.Named("src_flat_scratch_base_lo") => Some(Src.SrcFlatScratchBaseLo)
    case Slot.Named("src_flat_scratch_base_hi") => Some(Src.SrcFlatScratchBaseHi)
    case Slot.Integer(value) if value >= -16 && value <= 64 =>
      Some(Src.Integer(value.unsafe[Int in -16 ~ 64]))
    case Slot.Floating(value) => Some(Src.Floating(value))
    case Slot.Literal(4) => Some(Src.Literal)
    case Slot.Literal(8) => Some(Src.Literal64)
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Src extends DomainValue derives CanEqual {
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
  case Null
  case SrcFlatScratchBaseLo
  case SrcFlatScratchBaseHi
  case Integer(value: Int in -16 ~ 64)
  case Floating(value: Double)
  case Literal
  case Literal64
  def slot: Slot = this match {
    case Src.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case Src.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case Src.Vgpr(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
    case Src.VccLo => Slot.Named("vcc_lo")
    case Src.VccHi => Slot.Named("vcc_hi")
    case Src.TbaLo => Slot.Named("tba_lo")
    case Src.TbaHi => Slot.Named("tba_hi")
    case Src.TmaLo => Slot.Named("tma_lo")
    case Src.TmaHi => Slot.Named("tma_hi")
    case Src.M0 => Slot.Named("m0")
    case Src.ExecLo => Slot.Named("exec_lo")
    case Src.ExecHi => Slot.Named("exec_hi")
    case Src.SrcVccz => Slot.Named("src_vccz")
    case Src.SrcExecz => Slot.Named("src_execz")
    case Src.SrcScc => Slot.Named("src_scc")
    case Src.SrcLdsDirect => Slot.Named("src_lds_direct")
    case Src.FlatScratchLo => Slot.Named("flat_scratch_lo")
    case Src.FlatScratchHi => Slot.Named("flat_scratch_hi")
    case Src.XnackMaskLo => Slot.Named("xnack_mask_lo")
    case Src.XnackMaskHi => Slot.Named("xnack_mask_hi")
    case Src.SrcSharedBase => Slot.Named("src_shared_base")
    case Src.SrcSharedLimit => Slot.Named("src_shared_limit")
    case Src.SrcPrivateBase => Slot.Named("src_private_base")
    case Src.SrcPrivateLimit => Slot.Named("src_private_limit")
    case Src.SrcPopsExitingWaveId => Slot.Named("src_pops_exiting_wave_id")
    case Src.Null => Slot.Named("null")
    case Src.SrcFlatScratchBaseLo => Slot.Named("src_flat_scratch_base_lo")
    case Src.SrcFlatScratchBaseHi => Slot.Named("src_flat_scratch_base_hi")
    case Src.Integer(value) => Slot.Integer(value.raw)
    case Src.Floating(value) => Slot.Floating(value)
    case Src.Literal => Slot.Literal(4)
    case Src.Literal64 => Slot.Literal(8)
  }
}
