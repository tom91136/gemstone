package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*

object RegisterUse {
  val empty: RegisterUse = RegisterUse(Vec.empty, Vec.empty)
}

/** The registers one issue read and wrote, by the names the issuing machine gives them. A register the instruction both
  * reads and writes appears in both lists; the names are the machine's own, such as a PTX register or an AMDGPU scalar
  * register, so an observer can follow a value from the issue that defines it to the issues that use it.
  */
case class RegisterUse(reads: Vec[String], writes: Vec[String]) derives CanEqual {

  /** Whether the issue names no register at all. */
  def isEmpty: Boolean = reads.isEmpty && writes.isEmpty
}
