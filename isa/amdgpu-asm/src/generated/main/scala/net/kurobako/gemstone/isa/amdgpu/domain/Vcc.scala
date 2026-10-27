// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Vcc extends Domain[Vcc] {
  val id: String = "vcc"

  def of(slot: Slot): Option[Vcc] = slot match {
    case Slot.Named("vcc") => Some(Vcc.Value)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Vcc extends DomainValue derives CanEqual {
  case Value
  def slot: Slot = this match {
    case Vcc.Value => Slot.Named("vcc")
  }
}
