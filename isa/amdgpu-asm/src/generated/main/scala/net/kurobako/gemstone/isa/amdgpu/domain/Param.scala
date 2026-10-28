// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*

@Generated(Array("sbt codegen"))
object Param extends Domain[Param] {
  val id: String = "param"

  def of(slot: Slot): Option[Param] = slot match {
    case Slot.Numbered("p", index) if index >= 0 && index <= 20 =>
      Some(Param.P(index.unsafe[Int in 0 ~ 20]))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Param extends DomainValue derives CanEqual {
  case P(index: Int in 0 ~ 20)
  def slot: Slot = this match {
    case Param.P(index) => Slot.Numbered("p", index.raw)
  }
}
