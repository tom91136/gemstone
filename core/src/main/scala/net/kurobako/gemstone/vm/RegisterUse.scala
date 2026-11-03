package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*

object RegisterUse {
  val empty: RegisterUse = RegisterUse(Vec.empty, Vec.empty)
}

/** The registers one issue read and wrote, by the names the issuing machine gives them. A register the instruction both
  * reads and writes appears in both lists; the names are the machine's own, such as a PTX register or an AMDGPU scalar
  * register, so an observer can follow a value from the issue that defines it to the issues that use it.
  *
  * A register is one 32-bit unit wide unless `units` says otherwise: a 64-bit register is 2, a 128-bit register 4, and
  * a predicate 0, as predicates are not general registers.
  */
case class RegisterUse(reads: Vec[String], writes: Vec[String], units: Map[String, Int] = Map.empty)
    derives CanEqual {

  /** Whether the issue names no register at all. */
  def isEmpty: Boolean = reads.isEmpty && writes.isEmpty

  /** How many 32-bit units the register `name` takes. */
  def unitsOf(name: String): Int = units.getOrElse(name, 1)
}
