// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Simm8 extends Domain[Simm8] {
  val id: String = "simm8"

  def of(slot: Slot): Option[Simm8] = slot match {
    case Slot.Immediate(value) => Some(Simm8.Immediate(value))
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Simm8 extends DomainValue derives CanEqual {
  case Immediate(value: BigInt)
  def slot: Slot = this match {
    case Simm8.Immediate(value) => Slot.Immediate(value)
  }
}
