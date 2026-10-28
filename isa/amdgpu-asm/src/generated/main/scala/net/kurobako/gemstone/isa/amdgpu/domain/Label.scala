// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

@Generated(Array("sbt codegen"))
object Label extends Domain[Label] {
  val id: String = "label"

  def of(slot: Slot): Option[Label] = slot match {
    case Slot.Immediate(value) => Some(Label.Immediate(value))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Label extends DomainValue derives CanEqual {
  case Immediate(value: BigInt)
  def slot: Slot = this match {
    case Label.Immediate(value) => Slot.Immediate(value)
  }
}
