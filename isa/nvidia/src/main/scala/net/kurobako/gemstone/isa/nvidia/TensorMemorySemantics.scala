package net.kurobako.gemstone.isa.nvidia

import VMIR.Matrix.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** tcgen05.mma semantics, one element of D at a time: the products of [[TensorSemantics]] over A and B negated as the
  * instruction descriptor says, accumulating into D, scaled down by `2^-shift` first, unless accumulation is disabled.
  */
private[isa] object TensorMemorySemantics {

  inline def element[V](inline algebra: Algebra[V], product: TensorMemory.Product, operands: TensorMemoryOperands[V])
      : V = {
    val multiply        = product.multiply
    def scaled(c: V): V =
      if (product.shift == 0) c
      else if (multiply.c.element == Element.F16)
        algebra.primitive(c)((x: Float16) => ProductArithmetic.scale(x, product.shift))
      else algebra.primitive(c)((x: Float32) => ProductArithmetic.scale(x, product.shift))
    val addend = operands.bind("e", algebra.select(operands.enabled, scaled(operands.c), algebra.literal(0L)))
    product.blocked match {
      case Some(blocks) => TensorSemantics.blocked(algebra, blocks, operands, addend, product.negateA, product.negateB)
      case None         =>
        multiply.accumulation match {
          case Accumulation.Exact(chunk) =>
            TensorSemantics.integer(algebra, multiply, chunk, operands, addend, product.negateA, product.negateB)
          case _ => addend
        }
    }
  }
}
