// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Dsmem extends Domain[Dsmem] {
  val id: String = "dsmem"

  def of(slot: Slot): Option[Dsmem] = slot match {
    case Slot.Named("dsmem") => Some(Dsmem.Value)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Dsmem extends DomainValue derives CanEqual {
  case Value
  def slot: Slot = this match {
    case Dsmem.Value => Slot.Named("dsmem")
  }
}
