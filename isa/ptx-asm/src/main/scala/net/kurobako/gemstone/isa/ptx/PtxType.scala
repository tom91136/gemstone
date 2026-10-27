package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*

/** A PTX type. `kind` is one of `b`, `s`, `u`, `f` and `pred` for scalars, `packed` for element groups, `alt` for
  * formats that share a bit-size register, and `opaque` for handles. `declared` lists the types a register holding a
  * value of this type is declared with.
  */
case class PtxType(
    name: String,
    kind: String,
    bits: Int,
    elements: Int,
    element: Option[String],
    declared: Vec[String]
) {
  def scalar: Boolean    = elements == 1 && Set("b", "s", "u", "f")(kind)
  def integer: Boolean   = kind == "s" || kind == "u"
  def floating: Boolean  = kind == "f"
  def predicate: Boolean = kind == "pred"
  def signed: Boolean    = kind == "s"
}
