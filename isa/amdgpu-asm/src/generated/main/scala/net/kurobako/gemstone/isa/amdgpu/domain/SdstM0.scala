// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SdstM0 extends Domain[SdstM0] {
  val id: String = "sdst_m0"

  def of(slot: Slot): Option[SdstM0] = slot match {
    case Slot.Named("m0") => Some(SdstM0.M0)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SdstM0 extends DomainValue derives CanEqual {
  case M0
  def slot: Slot = this match {
    case SdstM0.M0 => Slot.Named("m0")
  }
}
