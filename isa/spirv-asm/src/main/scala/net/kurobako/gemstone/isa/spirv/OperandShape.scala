package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*

object OperandShape {

  /** Reads operand kind names separated by spaces, each followed by `?` when optional or `*` when repeated. */
  def parse(text: String): Vec[OperandShape] = Vec.from(text.split(' ').view.filter(_.nonEmpty).map {
    case s"$kind?" => OperandShape(OperandKind.valueOf(kind), OperandQuantifier.Optional)
    case s"$kind*" => OperandShape(OperandKind.valueOf(kind), OperandQuantifier.Repeated)
    case kind      => OperandShape(OperandKind.valueOf(kind), OperandQuantifier.One)
  })
}

case class OperandShape(kind: OperandKind, quantifier: OperandQuantifier)
