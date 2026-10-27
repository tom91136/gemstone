package net.kurobako.gemstone.isa.spirv

/** The result and the two operands of a binary instruction, as its shape checkers read them. */
private case class BinaryOperands(ids: ResultIds, left: Operand, right: Operand)

private object BinaryOperands {
  def apply(resultType: Operand, dest: Operand, left: Operand, right: Operand): BinaryOperands =
    BinaryOperands(ResultIds(resultType, dest), left, right)
}
