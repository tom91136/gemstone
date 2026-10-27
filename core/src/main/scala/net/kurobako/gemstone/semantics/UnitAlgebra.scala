package net.kurobako.gemstone.semantics

import net.kurobako.gemstone.binary.Widths.*

object UnitAlgebra {

  /** The places an instruction keeps its views in, so it can hold as many at once as there are slots. */
  enum Slot derives CanEqual {
    case First, Second, Third
  }

  val Slots: Int = Slot.values.length
}

/** The operations across the lanes of a unit: its views, and where it converges and loads. */
trait UnitAlgebra[V, O, W, L, F[_]] extends Any with UnitView[V, O, W, L, F] {

  /** 1 where every unit with an active lane has all its lanes active, otherwise 0. */
  inline def converged: V

  /** `next` over the view in `slot` of the `width` bits at `address` plus `offset` in `space`, loaded by the first
    * `lanes` lanes of each unit and zero on the rest.
    */
  inline def gatherLoad[A](
      slot: UnitAlgebra.Slot,
      lanes: Int,
      inline space: Space,
      address: O,
      offset: Long,
      width: ScalarBitWidth
  )(
      inline next: V => F[A]
  ): F[A]
}
