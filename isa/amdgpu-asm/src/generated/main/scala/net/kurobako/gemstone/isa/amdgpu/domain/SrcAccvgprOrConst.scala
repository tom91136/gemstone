// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SrcAccvgprOrConst extends Domain[SrcAccvgprOrConst] {
  val id: String = "src_accvgpr_or_const"

  def of(slot: Slot): Option[SrcAccvgprOrConst] = slot match {
    case Slot.Register(RegisterFile.Accumulator, index) if index >= 0 && index <= 255 =>
      Some(SrcAccvgprOrConst.Accvgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Integer(value) if value >= -16 && value <= 64 =>
      Some(SrcAccvgprOrConst.Integer(value.unsafe[Int in -16 ~ 64]))
    case Slot.Floating(value) => Some(SrcAccvgprOrConst.Floating(value))
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SrcAccvgprOrConst extends DomainValue derives CanEqual {
  case Accvgpr(index: Int in 0 ~ 255)
  case Integer(value: Int in -16 ~ 64)
  case Floating(value: Double)
  def slot: Slot = this match {
    case SrcAccvgprOrConst.Accvgpr(index) => Slot.Register(RegisterFile.Accumulator, index.raw)
    case SrcAccvgprOrConst.Integer(value) => Slot.Integer(value.raw)
    case SrcAccvgprOrConst.Floating(value) => Slot.Floating(value)
  }
}
