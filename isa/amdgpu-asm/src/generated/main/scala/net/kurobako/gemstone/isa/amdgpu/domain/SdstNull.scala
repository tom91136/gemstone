// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

@Generated(Array("sbt codegen"))
object SdstNull extends Domain[SdstNull] {
  val id: String = "sdst_null"

  def of(slot: Slot): Option[SdstNull] = slot match {
    case Slot.Named("null") => Some(SdstNull.Null)
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum SdstNull extends DomainValue derives CanEqual {
  case Null
  def slot: Slot = this match {
    case SdstNull.Null => Slot.Named("null")
  }
}
