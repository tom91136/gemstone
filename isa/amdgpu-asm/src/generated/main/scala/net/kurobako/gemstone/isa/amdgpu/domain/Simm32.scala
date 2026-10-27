// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Simm32 extends Domain[Simm32] {
  val id: String = "simm32"

  def of(slot: Slot): Option[Simm32] = slot match {
    case Slot.Immediate(value) => Some(Simm32.Immediate(value))
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Simm32 extends DomainValue derives CanEqual {
  case Immediate(value: BigInt)
  def slot: Slot = this match {
    case Simm32.Immediate(value) => Slot.Immediate(value)
  }
}
