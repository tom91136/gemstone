package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*

object OperandIds {

  /** Ids for evaluating semantics without operands. */
  val none: OperandIds = OperandIds(resultType = -1, result = -1, operands = Vec.empty)
}

/** The ids an instruction's semantics reads and writes: its result type, its result, and its value operands in the
  * order of the sink shape that consumes them. A semantics never reads an operand except through these ids.
  */
case class OperandIds(resultType: Int, result: Int, operands: Vec[Int]) {
  def apply(index: Int): Int = operands(index)
}
