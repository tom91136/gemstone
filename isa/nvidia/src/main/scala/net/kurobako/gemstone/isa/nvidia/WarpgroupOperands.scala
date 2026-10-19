package net.kurobako.gemstone.isa.nvidia

/** The operands of one element of a warpgroup product as a sink presents them, with the predicate that keeps D. */
private[isa] trait WarpgroupOperands[V] extends MatrixOperands[V] {
  def scale: V
}
