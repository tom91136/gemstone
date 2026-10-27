package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

/** One `.name` position of an opcode spelling and the values it takes; `slot` names it when the document defines the
  * domain, which is when it takes more than one value.
  */
case class ModifierDomain(name: String, slot: Option[ModifierSlot], values: Set[Modifier]) {
  def spelling: String = values.toVec.map(_.name.raw).sorted.mkString(".", " .", "")
}
