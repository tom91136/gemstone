// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*

@Generated(Array("sbt codegen"))
object Tgt extends Domain[Tgt] {
  val id: String = "tgt"

  def of(slot: Slot): Option[Tgt] = slot match {
    case Slot.Named("mrtz") => Some(Tgt.Mrtz)
    case Slot.Named("null") => Some(Tgt.Null)
    case Slot.Named("prim") => Some(Tgt.Prim)
    case Slot.Numbered("dual_src_blend", index) if index >= 0 && index <= 1 =>
      Some(Tgt.DualSrcBlend(index.unsafe[Int in 0 ~ 1]))
    case Slot.Numbered("mrt", index) if index >= 0 && index <= 7 =>
      Some(Tgt.Mrt(index.unsafe[Int in 0 ~ 7]))
    case Slot.Numbered("param", index) if index >= 0 && index <= 31 =>
      Some(Tgt.Param(index.unsafe[Int in 0 ~ 31]))
    case Slot.Numbered("pos", index) if index >= 0 && index <= 4 =>
      Some(Tgt.Pos(index.unsafe[Int in 0 ~ 4]))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum Tgt extends DomainValue derives CanEqual {
  case Mrtz
  case Null
  case Prim
  case DualSrcBlend(index: Int in 0 ~ 1)
  case Mrt(index: Int in 0 ~ 7)
  case Param(index: Int in 0 ~ 31)
  case Pos(index: Int in 0 ~ 4)
  def slot: Slot = this match {
    case Tgt.Mrtz => Slot.Named("mrtz")
    case Tgt.Null => Slot.Named("null")
    case Tgt.Prim => Slot.Named("prim")
    case Tgt.DualSrcBlend(index) => Slot.Numbered("dual_src_blend", index.raw)
    case Tgt.Mrt(index) => Slot.Numbered("mrt", index.raw)
    case Tgt.Param(index) => Slot.Numbered("param", index.raw)
    case Tgt.Pos(index) => Slot.Numbered("pos", index.raw)
  }
}
