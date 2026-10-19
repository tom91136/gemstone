package net.kurobako.gemstone.isa.nvidia

/** The operands of one element of a tcgen05.mma product, and whether D accumulates into it. */
private[isa] trait TensorMemoryOperands[V] extends MatrixOperands[V] {
  def enabled: V
}
