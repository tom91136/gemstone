package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*

/** A lane of an observed launch, which reports each register and predicate access to its frame. An unobserved launch
  * uses plain lanes, so its register accesses carry no reporting.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
private class VMObservedLane(registerCount: Int >= 0, predicateCount: Int >= 0)
    extends VMLane(registerCount, predicateCount) {
  private var owner: VMLanes | Null = null

  def ownedBy(lanes: VMLanes): Unit = owner = lanes

  override def register(register: Register): Long = {
    val lanes = owner
    if (lanes != null) lanes.readRegister(register)
    super.register(register)
  }

  override def setRegister(register: Register, value: Long): Unit = {
    val lanes = owner
    if (lanes != null) lanes.writeRegister(register)
    super.setRegister(register, value)
  }

  override def predicate(predicate: Predicate): Option[Boolean] = {
    val lanes = owner
    if (lanes != null) lanes.readPredicate(predicate)
    super.predicate(predicate)
  }

  override def predicateBit(predicate: Predicate): Long = {
    val lanes = owner
    if (lanes != null) lanes.readPredicate(predicate)
    super.predicateBit(predicate)
  }

  override def setPredicate(predicate: Predicate, value: Boolean): Unit = {
    val lanes = owner
    if (lanes != null) lanes.writePredicate(predicate)
    super.setPredicate(predicate, value)
  }
}
