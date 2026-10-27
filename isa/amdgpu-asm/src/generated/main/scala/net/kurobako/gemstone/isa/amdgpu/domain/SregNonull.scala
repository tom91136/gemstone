// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SregNonull extends Domain[SregNonull] {
  val id: String = "sreg_nonull"

  def of(slot: Slot): Option[SregNonull] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(SregNonull.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SregNonull.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(SregNonull.VccLo)
    case Slot.Named("vcc_hi") => Some(SregNonull.VccHi)
    case Slot.Named("exec_lo") => Some(SregNonull.ExecLo)
    case Slot.Named("exec_hi") => Some(SregNonull.ExecHi)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SregNonull extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 105)
  case Ttmp(index: Int in 0 ~ 15)
  case VccLo
  case VccHi
  case ExecLo
  case ExecHi
  def slot: Slot = this match {
    case SregNonull.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SregNonull.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SregNonull.VccLo => Slot.Named("vcc_lo")
    case SregNonull.VccHi => Slot.Named("vcc_hi")
    case SregNonull.ExecLo => Slot.Named("exec_lo")
    case SregNonull.ExecHi => Slot.Named("exec_hi")
  }
}
