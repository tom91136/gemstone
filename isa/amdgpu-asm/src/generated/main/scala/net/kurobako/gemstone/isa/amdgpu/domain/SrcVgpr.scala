// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SrcVgpr extends Domain[SrcVgpr] {
  val id: String = "src_vgpr"

  def of(slot: Slot): Option[SrcVgpr] = slot match {
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(SrcVgpr.Vgpr(index.unsafe[Int in 0 ~ 255]))
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SrcVgpr extends DomainValue derives CanEqual {
  case Vgpr(index: Int in 0 ~ 255)
  def slot: Slot = this match {
    case SrcVgpr.Vgpr(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
  }
}
