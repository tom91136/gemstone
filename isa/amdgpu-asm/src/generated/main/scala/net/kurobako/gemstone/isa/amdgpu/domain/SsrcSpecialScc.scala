// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SsrcSpecialScc extends Domain[SsrcSpecialScc] {
  val id: String = "ssrc_special_scc"

  def of(slot: Slot): Option[SsrcSpecialScc] = slot match {
    case Slot.Named("src_scc") => Some(SsrcSpecialScc.SrcScc)
    case Slot.Named("src_vccz") => Some(SsrcSpecialScc.SrcVccz)
    case Slot.Named("src_execz") => Some(SsrcSpecialScc.SrcExecz)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SsrcSpecialScc extends DomainValue derives CanEqual {
  case SrcScc
  case SrcVccz
  case SrcExecz
  def slot: Slot = this match {
    case SsrcSpecialScc.SrcScc => Slot.Named("src_scc")
    case SsrcSpecialScc.SrcVccz => Slot.Named("src_vccz")
    case SsrcSpecialScc.SrcExecz => Slot.Named("src_execz")
  }
}
