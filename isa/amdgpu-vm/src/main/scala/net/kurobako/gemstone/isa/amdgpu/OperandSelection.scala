package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.*

/** Operand selection shared by execution and rendering. */
private object OperandSelection {

  /** Source `index` of a VOP3P operation: the half of `value`, `width` bits each, that its bit of `selection` picks,
    * its sign flipped where its bit of `negate` is set.
    */
  inline def packedOperand[V](
      inline algebra: Algebra[V],
      value: V,
      selection: Int,
      negate: Int,
      index: Int,
      inline width: Int
  ): V = {
    val high = ((selection >>> index) & 1) != 0
    val part =
      inline if (width == 32)
        if (high) algebra.shiftRightLogical(value, algebra.literal(32L)) else algebra.truncate(value, 32)
      else
        algebra.truncate(
          if (high) algebra.shiftRightLogical(value, algebra.literal(16L)) else algebra.truncate(value, 16),
          16
        )
    if ((negate & (1 << index)) != 0) algebra.xor(part, algebra.literal(1L << (width - 1))) else part
  }

  /** `kernel` over the three sources of a VOP3P operation, each reduced by [[packedOperand]]. */
  inline def packedTernary(
      inline algebra: Algebra[Long],
      a: Long,
      b: Long,
      c: Long,
      selection: Int,
      negate: Int,
      inline width: Int
  )(inline kernel: (Long, Long, Long) => Long): Long = {
    inline val bits = width
    kernel(
      packedOperand(algebra, a, selection, negate, index = 0, width = bits),
      packedOperand(algebra, b, selection, negate, index = 1, width = bits),
      packedOperand(algebra, c, selection, negate, index = 2, width = bits)
    )
  }
}
