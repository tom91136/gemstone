// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object VgprOrAccvgpr extends Domain[VgprOrAccvgpr] {
  val id: String = "vgpr_or_accvgpr"

  def of(slot: Slot): Option[VgprOrAccvgpr] = slot match {
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(VgprOrAccvgpr.Vgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Register(RegisterFile.Accumulator, index) if index >= 0 && index <= 255 =>
      Some(VgprOrAccvgpr.Accvgpr(index.unsafe[Int in 0 ~ 255]))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum VgprOrAccvgpr extends DomainValue derives CanEqual {
  case Vgpr(index: Int in 0 ~ 255)
  case Accvgpr(index: Int in 0 ~ 255)
  def slot: Slot = this match {
    case VgprOrAccvgpr.Vgpr(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
    case VgprOrAccvgpr.Accvgpr(index) => Slot.Register(RegisterFile.Accumulator, index.raw)
  }
}
