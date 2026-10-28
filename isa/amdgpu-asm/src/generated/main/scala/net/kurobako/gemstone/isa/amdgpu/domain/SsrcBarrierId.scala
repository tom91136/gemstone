// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*

@Generated(Array("sbt codegen"))
object SsrcBarrierId extends Domain[SsrcBarrierId] {
  val id: String = "ssrc_barrier_id"

  def of(slot: Slot): Option[SsrcBarrierId] = slot match {
    case Slot.Named("m0") => Some(SsrcBarrierId.M0)
    case Slot.Integer(value) if value >= -4 && value <= 31 =>
      Some(SsrcBarrierId.Integer(value.unsafe[Int in -4 ~ 31]))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum SsrcBarrierId extends DomainValue derives CanEqual {
  case M0
  case Integer(value: Int in -4 ~ 31)
  def slot: Slot = this match {
    case SsrcBarrierId.M0 => Slot.Named("m0")
    case SsrcBarrierId.Integer(value) => Slot.Integer(value.raw)
  }
}
