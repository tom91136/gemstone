// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SrcNoinline extends Domain[SrcNoinline] {
  val id: String = "src_noinline"

  def of(slot: Slot): Option[SrcNoinline] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(SrcNoinline.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SrcNoinline.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(SrcNoinline.Vgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Named("vcc_lo") => Some(SrcNoinline.VccLo)
    case Slot.Named("vcc_hi") => Some(SrcNoinline.VccHi)
    case Slot.Named("null") => Some(SrcNoinline.Null)
    case Slot.Named("m0") => Some(SrcNoinline.M0)
    case Slot.Named("exec_lo") => Some(SrcNoinline.ExecLo)
    case Slot.Named("exec_hi") => Some(SrcNoinline.ExecHi)
    case Slot.Literal(4) => Some(SrcNoinline.Literal)
    case Slot.Literal(8) => Some(SrcNoinline.Literal64)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SrcNoinline extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 105)
  case Ttmp(index: Int in 0 ~ 15)
  case Vgpr(index: Int in 0 ~ 255)
  case VccLo
  case VccHi
  case Null
  case M0
  case ExecLo
  case ExecHi
  case Literal
  case Literal64
  def slot: Slot = this match {
    case SrcNoinline.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SrcNoinline.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SrcNoinline.Vgpr(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
    case SrcNoinline.VccLo => Slot.Named("vcc_lo")
    case SrcNoinline.VccHi => Slot.Named("vcc_hi")
    case SrcNoinline.Null => Slot.Named("null")
    case SrcNoinline.M0 => Slot.Named("m0")
    case SrcNoinline.ExecLo => Slot.Named("exec_lo")
    case SrcNoinline.ExecHi => Slot.Named("exec_hi")
    case SrcNoinline.Literal => Slot.Literal(4)
    case SrcNoinline.Literal64 => Slot.Literal(8)
  }
}
