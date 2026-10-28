// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

@Generated(Array("sbt codegen"))
object FlatScratch extends Domain[FlatScratch] {
  val id: String = "flat_scratch"

  def of(slot: Slot): Option[FlatScratch] = slot match {
    case Slot.Named("flat_scratch_all") => Some(FlatScratch.FlatScratchAll)
    case Slot.Named("flat_scratch") => Some(FlatScratch.Value)
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum FlatScratch extends DomainValue derives CanEqual {
  case FlatScratchAll
  case Value
  def slot: Slot = this match {
    case FlatScratch.FlatScratchAll => Slot.Named("flat_scratch_all")
    case FlatScratch.Value => Slot.Named("flat_scratch")
  }
}
