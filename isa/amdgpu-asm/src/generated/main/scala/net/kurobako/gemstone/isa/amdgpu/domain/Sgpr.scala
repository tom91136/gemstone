// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Sgpr extends Domain[Sgpr] {
  val id: String = "sgpr"

  def of(slot: Slot): Option[Sgpr] = slot match {
    case Slot.Register(RegisterFile.Scalar, index) if index >= 0 && index <= 105 =>
      Some(Sgpr.Value(index.unsafe[Int in 0 ~ 105]))
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Sgpr extends DomainValue derives CanEqual {
  case Value(index: Int in 0 ~ 105)
  def slot: Slot = this match {
    case Sgpr.Value(index) => Slot.Register(RegisterFile.Scalar, index.raw)
  }
}
