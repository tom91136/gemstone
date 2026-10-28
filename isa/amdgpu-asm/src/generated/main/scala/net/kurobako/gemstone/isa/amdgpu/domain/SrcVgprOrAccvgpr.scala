// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object SrcVgprOrAccvgpr extends Domain[SrcVgprOrAccvgpr] {
  val id: String = "src_vgpr_or_accvgpr"

  def of(slot: Slot): Option[SrcVgprOrAccvgpr] = slot match {
    case Slot.Register(RegisterFile.VectorRegister, index) if index >= 0 && index <= 255 =>
      Some(SrcVgprOrAccvgpr.Vgpr(index.unsafe[Int in 0 ~ 255]))
    case Slot.Register(RegisterFile.Accumulator, index) if index >= 0 && index <= 255 =>
      Some(SrcVgprOrAccvgpr.Accvgpr(index.unsafe[Int in 0 ~ 255]))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum SrcVgprOrAccvgpr extends DomainValue derives CanEqual {
  case Vgpr(index: Int in 0 ~ 255)
  case Accvgpr(index: Int in 0 ~ 255)
  def slot: Slot = this match {
    case SrcVgprOrAccvgpr.Vgpr(index) => Slot.Register(RegisterFile.VectorRegister, index.raw)
    case SrcVgprOrAccvgpr.Accvgpr(index) => Slot.Register(RegisterFile.Accumulator, index.raw)
  }
}
