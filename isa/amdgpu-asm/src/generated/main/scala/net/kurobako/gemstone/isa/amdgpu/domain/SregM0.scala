// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object SregM0 extends Domain[SregM0] {
  val id: String = "sreg_m0"

  def of(slot: Slot): Option[SregM0] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(SregM0.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SregM0.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(SregM0.VccLo)
    case Slot.Named("vcc_hi") => Some(SregM0.VccHi)
    case Slot.Named("null") => Some(SregM0.Null)
    case Slot.Named("m0") => Some(SregM0.M0)
    case Slot.Named("exec_lo") => Some(SregM0.ExecLo)
    case Slot.Named("exec_hi") => Some(SregM0.ExecHi)
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum SregM0 extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 105)
  case Ttmp(index: Int in 0 ~ 15)
  case VccLo
  case VccHi
  case Null
  case M0
  case ExecLo
  case ExecHi
  def slot: Slot = this match {
    case SregM0.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SregM0.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SregM0.VccLo => Slot.Named("vcc_lo")
    case SregM0.VccHi => Slot.Named("vcc_hi")
    case SregM0.Null => Slot.Named("null")
    case SregM0.M0 => Slot.Named("m0")
    case SregM0.ExecLo => Slot.Named("exec_lo")
    case SregM0.ExecHi => Slot.Named("exec_hi")
  }
}
