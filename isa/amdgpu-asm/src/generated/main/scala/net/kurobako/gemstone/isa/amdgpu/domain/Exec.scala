// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Exec extends Domain[Exec] {
  val id: String = "exec"

  def of(slot: Slot): Option[Exec] = slot match {
    case Slot.Named("exec") => Some(Exec.Value)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Exec extends DomainValue derives CanEqual {
  case Value
  def slot: Slot = this match {
    case Exec.Value => Slot.Named("exec")
  }
}
