// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object SmemOffsetNok extends Domain[SmemOffsetNok] {
  val id: String = "smem_offset_nok"

  def of(slot: Slot): Option[SmemOffsetNok] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(SmemOffsetNok.Sgpr(index.unsafe[Int in 0 ~ 105]))
    case Slot.Register(RegisterFile.TrapTemporary, index) if index >= 0 && index <= 15 =>
      Some(SmemOffsetNok.Ttmp(index.unsafe[Int in 0 ~ 15]))
    case Slot.Named("vcc_lo") => Some(SmemOffsetNok.VccLo)
    case Slot.Named("vcc_hi") => Some(SmemOffsetNok.VccHi)
    case Slot.Named("null") => Some(SmemOffsetNok.Null)
    case Slot.Named("m0") => Some(SmemOffsetNok.M0)
    case Slot.Named("exec_lo") => Some(SmemOffsetNok.ExecLo)
    case Slot.Named("exec_hi") => Some(SmemOffsetNok.ExecHi)
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum SmemOffsetNok extends DomainValue derives CanEqual {
  case Sgpr(index: Int in 0 ~ 105)
  case Ttmp(index: Int in 0 ~ 15)
  case VccLo
  case VccHi
  case Null
  case M0
  case ExecLo
  case ExecHi
  def slot: Slot = this match {
    case SmemOffsetNok.Sgpr(index) => Slot.Register(RegisterFile.Scalar, index.raw)
    case SmemOffsetNok.Ttmp(index) => Slot.Register(RegisterFile.TrapTemporary, index.raw)
    case SmemOffsetNok.VccLo => Slot.Named("vcc_lo")
    case SmemOffsetNok.VccHi => Slot.Named("vcc_hi")
    case SmemOffsetNok.Null => Slot.Named("null")
    case SmemOffsetNok.M0 => Slot.Named("m0")
    case SmemOffsetNok.ExecLo => Slot.Named("exec_lo")
    case SmemOffsetNok.ExecHi => Slot.Named("exec_hi")
  }
}
