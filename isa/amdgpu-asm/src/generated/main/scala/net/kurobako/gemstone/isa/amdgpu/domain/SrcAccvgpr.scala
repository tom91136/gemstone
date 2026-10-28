// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

@Generated(Array("sbt codegen"))
object SrcAccvgpr extends Domain[SrcAccvgpr] {
  val id: String = "src_accvgpr"

  def of(slot: Slot): Option[SrcAccvgpr] = slot match {
    case Slot.Register(RegisterFile.Accumulator, index) if index >= 0 && index <= 255 =>
      Some(SrcAccvgpr.Accvgpr(index.unsafe[Int in 0 ~ 255]))
    case _ => None
  }
}

@Generated(Array("sbt codegen"))
enum SrcAccvgpr extends DomainValue derives CanEqual {
  case Accvgpr(index: Int in 0 ~ 255)
  def slot: Slot = this match {
    case SrcAccvgpr.Accvgpr(index) => Slot.Register(RegisterFile.Accumulator, index.raw)
  }
}
