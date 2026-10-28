// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object SrcVgprOrAccvgprOrConst extends Domain[SrcVgprOrAccvgprOrConst] {
  val id: String = "src_vgpr_or_accvgpr_or_const"

  def of(slot: Slot): Option[SrcVgprOrAccvgprOrConst] = slot match {
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(SrcVgprOrAccvgprOrConst.Vgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Register(RegisterFile.Accumulator, index) if index >= 0 && index <= 255 =>
      Some(SrcVgprOrAccvgprOrConst.Accvgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Integer(value) if value >= -16 && value <= 64 =>
      Some(SrcVgprOrAccvgprOrConst.Integer(value.unsafe[Int in -16 ~ 64]))
    case Slot.Floating(value) => Some(SrcVgprOrAccvgprOrConst.Floating(value))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum SrcVgprOrAccvgprOrConst extends DomainValue derives CanEqual {
  case Vgpr(index: Int in 0 ~ 255)
  case Accvgpr(index: Int in 0 ~ 255)
  case Integer(value: Int in -16 ~ 64)
  case Floating(value: Double)
  def slot: Slot = this match {
    case SrcVgprOrAccvgprOrConst.Vgpr(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
    case SrcVgprOrAccvgprOrConst.Accvgpr(index) => Slot.Register(RegisterFile.Accumulator, index.raw)
    case SrcVgprOrAccvgprOrConst.Integer(value) => Slot.Integer(value.raw)
    case SrcVgprOrAccvgprOrConst.Floating(value) => Slot.Floating(value)
  }
}
