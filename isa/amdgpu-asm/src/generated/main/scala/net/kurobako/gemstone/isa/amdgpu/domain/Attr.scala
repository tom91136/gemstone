// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*

@Generated(Array("sbt codegen"))
object Attr extends Domain[Attr] {
  val id: String = "attr"

  def of(slot: Slot): Option[Attr] = slot match {
    case Slot.Numbered("attr", index) if index >= 0 && index <= 32 =>
      Some(Attr.Value(index.unsafe[Int in 0 ~ 32]))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Attr extends DomainValue derives CanEqual {
  case Value(index: Int in 0 ~ 32)
  def slot: Slot = this match {
    case Attr.Value(index) => Slot.Numbered("attr", index.raw)
  }
}
