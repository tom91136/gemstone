// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Simm8At7 extends Domain[Simm8At7] {
  val id: String = "simm8@7"

  def of(slot: Slot): Option[Simm8At7] = slot match {
    case Slot.Immediate(value) => Some(Simm8At7.Immediate(value))
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Simm8At7 extends DomainValue derives CanEqual {
  case Immediate(value: BigInt)
  def slot: Slot = this match {
    case Simm8At7.Immediate(value) => Slot.Immediate(value)
  }
}
