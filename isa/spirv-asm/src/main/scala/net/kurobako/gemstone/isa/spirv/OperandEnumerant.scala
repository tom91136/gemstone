package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object OperandEnumerant {

  /** Reads the name, value, parameter kinds and aliases separated by spaces, with list elements separated by commas. */
  def parse(text: String): OperandEnumerant = {
    val fields                           = text.split(" ", -1)
    def list(field: String): Vec[String] = Vec.from(field.split(',')).filter(_.nonEmpty)
    OperandEnumerant(
      fields(0).unsafe[NonEmpty[String]],
      fields(1).toLong,
      Vec.from(list(fields(2)).view.map(OperandKind.valueOf(_))),
      Vec.from(list(fields(3)).view.map(_.unsafe[NonEmpty[String]]))
    )
  }
}

case class OperandEnumerant(
    name: NonEmpty[String],
    value: Long,
    parameterKinds: Vec[OperandKind],
    aliases: Vec[NonEmpty[String]]
)
