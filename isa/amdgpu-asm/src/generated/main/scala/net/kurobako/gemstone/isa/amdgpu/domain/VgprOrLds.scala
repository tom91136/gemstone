// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object VgprOrLds extends Domain[VgprOrLds] {
  val id: String = "vgpr_or_lds"

  def of(slot: Slot): Option[VgprOrLds] = slot match {
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(VgprOrLds.Vgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Named("src_lds_direct") => Some(VgprOrLds.SrcLdsDirect)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum VgprOrLds extends DomainValue derives CanEqual {
  case Vgpr(index: Int in 0 ~ 255)
  case SrcLdsDirect
  def slot: Slot = this match {
    case VgprOrLds.Vgpr(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
    case VgprOrLds.SrcLdsDirect => Slot.Named("src_lds_direct")
  }
}
