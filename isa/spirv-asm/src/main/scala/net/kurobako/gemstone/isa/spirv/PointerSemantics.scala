package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** Pointer comparisons over addresses, by opcode. */
object PointerSemantics {

  inline def binary[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], opcode: Int)(ids: OperandIds)(
      otherwise: => R
  ): R = opcode match {
    case SPIRV.Opcode.PtrEqual    => sinks.addresses(ids)((a, b) => algebra.equal(a, b))
    case SPIRV.Opcode.PtrNotEqual => sinks.addresses(ids)((a, b) => algebra.notEqual(a, b))
    case _                        => otherwise
  }

  /** OpPtrDiff over two signed addresses and the element stride: their exact difference in elements, rounded toward
    * zero and wrapped to the width.
    */
  inline def difference[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in 1 ~ 64
  )(ids: OperandIds): R =
    sinks.addressDifference(ids)((a, b, stride) =>
      algebra.truncate(
        algebra.select(
          algebra.lessThan(a, b, signed = true),
          algebra.subtract(algebra.literal(0L), algebra.divide(algebra.subtract(b, a), stride, signed = false)),
          algebra.divide(algebra.subtract(a, b), stride, signed = false)
        ),
        width
      )
    )
}
