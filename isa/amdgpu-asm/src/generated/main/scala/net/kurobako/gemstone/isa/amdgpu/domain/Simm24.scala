// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

@Generated(Array("sbt codegen"))
object Simm24 extends Domain[Simm24] {
  val id: String = "simm24"

  def of(slot: Slot): Option[Simm24] = slot match {
    case Slot.Immediate(value) => Some(Simm24.Immediate(value))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Simm24 extends DomainValue derives CanEqual {
  case Immediate(value: BigInt)
  def slot: Slot = this match {
    case Simm24.Immediate(value) => Slot.Immediate(value)
  }
}
