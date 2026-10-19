package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import VMIR.Matrix.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** Warpgroup matrix products, one element of D at a time as `mma` computes it, from multiplicands each negated where
  * the instruction says and from D, or zero where `scale` drops it.
  */
private[isa] object WarpgroupSemantics {

  inline def multiply[V, R](inline algebra: Algebra[V], operation: Operation.WarpgroupMultiply)(
      inline sinks: NvidiaSinks[V, R]
  )(otherwise: => R): R = {
    val product = operation.product
    product.accumulation match {
      case Accumulation.Exact(chunk) if product.d.element == Element.S32 =>
        sinks.multiplyWarpgroup(operation)(operands =>
          TensorSemantics.integer(
            algebra,
            product,
            chunk,
            operands,
            addend(algebra, operation, operands),
            operation.negateA,
            operation.negateB
          )
        )
      case blocked: Accumulation.Blocked if product.d.element == Element.F32 || product.d.element == Element.F16 =>
        val blocks = TensorSemantics.BlockedProduct(product, blocked)
        sinks.multiplyWarpgroup(operation)(operands =>
          TensorSemantics.blocked(
            algebra,
            blocks,
            operands,
            addend(algebra, operation, operands),
            operation.negateA,
            operation.negateB
          )
        )
      case _ => otherwise
    }
  }

  /** The element of C that D accumulates into, as `scale` keeps it. */
  inline def addend[V](
      inline algebra: Algebra[V],
      operation: Operation.WarpgroupMultiply,
      operands: WarpgroupOperands[V]
  ): V = operation.scale match {
    case Value.Immediate(0L) => algebra.literal(0L)
    case Value.Immediate(_)  => operands.c
    case _                   =>
      operands.bind(
        "k",
        operation.product.d.element match {
          case Element.F16 =>
            algebra.primitive(operands.scale, operands.c)((s: Bool, x: Float16) => ProductArithmetic.addend(s, x))
          case Element.F32 =>
            algebra.primitive(operands.scale, operands.c)((s: Bool, x: Float32) => ProductArithmetic.addend(s, x))
          case _ =>
            algebra.primitive(operands.scale, operands.c)((s: Bool, x: SInt[32]) => ProductArithmetic.addend(s, x))
        }
      )
  }
}
