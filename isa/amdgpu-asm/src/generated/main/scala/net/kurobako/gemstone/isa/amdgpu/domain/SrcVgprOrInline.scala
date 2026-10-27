// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SrcVgprOrInline extends Domain[SrcVgprOrInline] {
  val id: String = "src_vgpr_or_inline"

  def of(slot: Slot): Option[SrcVgprOrInline] = slot match {
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(SrcVgprOrInline.Vgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Integer(value) if value >= -16 && value <= 64 =>
      Some(SrcVgprOrInline.Integer(value.unsafe[Int in -16 ~ 64]))
    case Slot.Floating(value) => Some(SrcVgprOrInline.Floating(value))
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SrcVgprOrInline extends DomainValue derives CanEqual {
  case Vgpr(index: Int in 0 ~ 255)
  case Integer(value: Int in -16 ~ 64)
  case Floating(value: Double)
  def slot: Slot = this match {
    case SrcVgprOrInline.Vgpr(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
    case SrcVgprOrInline.Integer(value) => Slot.Integer(value.raw)
    case SrcVgprOrInline.Floating(value) => Slot.Floating(value)
  }
}
