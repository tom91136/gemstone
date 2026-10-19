package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.semantics.render.*

/** How an ISA names and types the registers and predicates of its rendered semantics. */
private[isa] trait SlotNaming extends Naming[Slot] {

  /** The value of a predicate that reads as a constant. */
  def constant(slot: Predicate): Option[Boolean] = None

  /** The constant bank an address of a load reads, where the load reads a constant operand, and the offset it reads at:
    * from the operand's offset field when present, otherwise from the bank's start.
    */
  def constantBank(address: Long): Option[(bank: Long, field: Option[(name: String, tpe: Type)], offset: Long)] = None
}
