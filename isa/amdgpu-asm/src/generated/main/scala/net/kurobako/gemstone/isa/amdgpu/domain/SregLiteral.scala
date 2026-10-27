// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SregLiteral extends Domain[SregLiteral] {
  val id: String = "sreg_literal"

  def of(slot: Slot): Option[SregLiteral] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(SregLiteral.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SregLiteral.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(SregLiteral.VccLo)
    case Slot.Named("vcc_hi") => Some(SregLiteral.VccHi)
    case Slot.Named("null") => Some(SregLiteral.Null)
    case Slot.Named("exec_lo") => Some(SregLiteral.ExecLo)
    case Slot.Named("exec_hi") => Some(SregLiteral.ExecHi)
    case Slot.Literal(4) => Some(SregLiteral.Literal)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SregLiteral extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 105)
  case Ttmp(index: Int in 0 ~ 15)
  case VccLo
  case VccHi
  case Null
  case ExecLo
  case ExecHi
  case Literal
  def slot: Slot = this match {
    case SregLiteral.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SregLiteral.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SregLiteral.VccLo => Slot.Named("vcc_lo")
    case SregLiteral.VccHi => Slot.Named("vcc_hi")
    case SregLiteral.Null => Slot.Named("null")
    case SregLiteral.ExecLo => Slot.Named("exec_lo")
    case SregLiteral.ExecHi => Slot.Named("exec_hi")
    case SregLiteral.Literal => Slot.Literal(4)
  }
}
