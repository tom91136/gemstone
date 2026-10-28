// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

@Generated(Array("sbt codegen"))
object Simm4 extends Domain[Simm4] {
  val id: String = "simm4"

  def of(slot: Slot): Option[Simm4] = slot match {
    case Slot.Immediate(value) => Some(Simm4.Immediate(value))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Simm4 extends DomainValue derives CanEqual {
  case Immediate(value: BigInt)
  def slot: Slot = this match {
    case Simm4.Immediate(value) => Slot.Immediate(value)
  }
}
