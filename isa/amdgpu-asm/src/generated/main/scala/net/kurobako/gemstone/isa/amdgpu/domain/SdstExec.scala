// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

@javax.annotation.processing.Generated(Array("sbt codegen"))
object SdstExec extends Domain[SdstExec] {
  val id: String = "sdst_exec"

  def of(slot: Slot): Option[SdstExec] = slot match {
    case Slot.Named("exec_lo") => Some(SdstExec.ExecLo)
    case Slot.Named("exec_hi") => Some(SdstExec.ExecHi)
    case _ => None
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SdstExec extends DomainValue derives CanEqual {
  case ExecLo
  case ExecHi
  def slot: Slot = this match {
    case SdstExec.ExecLo => Slot.Named("exec_lo")
    case SdstExec.ExecHi => Slot.Named("exec_hi")
  }
}
