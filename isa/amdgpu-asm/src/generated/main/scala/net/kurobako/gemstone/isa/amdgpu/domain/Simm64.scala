// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Simm64 extends Domain[Simm64] {
  val id: String = "simm64"

  def of(slot: Slot): Option[Simm64] = slot match {
    case Slot.Immediate(value) => Some(Simm64.Immediate(value))
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Simm64 extends DomainValue derives CanEqual {
  case Immediate(value: BigInt)
  def slot: Slot = this match {
    case Simm64.Immediate(value) => Slot.Immediate(value)
  }
}
