// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

@Generated(Array("sbt codegen"))
object Simm5 extends Domain[Simm5] {
  val id: String = "simm5"

  def of(slot: Slot): Option[Simm5] = slot match {
    case Slot.Immediate(value) => Some(Simm5.Immediate(value))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Simm5 extends DomainValue derives CanEqual {
  case Immediate(value: BigInt)
  def slot: Slot = this match {
    case Simm5.Immediate(value) => Slot.Immediate(value)
  }
}
