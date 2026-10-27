// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Pc extends Domain[Pc] {
  val id: String = "pc"

  def of(slot: Slot): Option[Pc] = slot match {
    case Slot.Named("pc_all") => Some(Pc.PcAll)
    case Slot.Named("pc") => Some(Pc.Value)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Pc extends DomainValue derives CanEqual {
  case PcAll
  case Value
  def slot: Slot = this match {
    case Pc.PcAll => Slot.Named("pc_all")
    case Pc.Value => Slot.Named("pc")
  }
}
