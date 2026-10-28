// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object Vgpr extends Domain[Vgpr] {
  val id: String = "vgpr"

  def of(slot: Slot): Option[Vgpr] = slot match {
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(Vgpr.Value(index.unsafe[Int in 0 ~ 255]))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Vgpr extends DomainValue derives CanEqual {
  case Value(index: Int in 0 ~ 255)
  def slot: Slot = this match {
    case Vgpr.Value(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
  }
}
