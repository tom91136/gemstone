// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Gpumem extends Domain[Gpumem] {
  val id: String = "gpumem"

  def of(slot: Slot): Option[Gpumem] = slot match {
    case Slot.Named("gpumem") => Some(Gpumem.Value)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Gpumem extends DomainValue derives CanEqual {
  case Value
  def slot: Slot = this match {
    case Gpumem.Value => Slot.Named("gpumem")
  }
}
