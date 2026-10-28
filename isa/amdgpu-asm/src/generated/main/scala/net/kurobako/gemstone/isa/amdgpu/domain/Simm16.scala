// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

@Generated(Array("sbt codegen"))
object Simm16 extends Domain[Simm16] {
  val id: String = "simm16"

  def of(slot: Slot): Option[Simm16] = slot match {
    case Slot.Immediate(value) => Some(Simm16.Immediate(value))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Simm16 extends DomainValue derives CanEqual {
  case Immediate(value: BigInt)
  def slot: Slot = this match {
    case Simm16.Immediate(value) => Slot.Immediate(value)
  }
}
