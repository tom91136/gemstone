package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** The invocations of a scope that reached a group operation, as the unit its lanes read across. Each invocation is the
  * lane at its index in the scope, and the operands it offered are the views its peers read.
  */
private trait GroupUnit[V, F[_]] extends Any with UnitView[V, Int, Int, Unit, F] {

  /** The end of a resolution. */
  inline def done: F[Unit]

  /** The index of the first lane that took part. */
  inline def leader: V

  /** The number of lanes that took part. */
  inline def count: V

  /** The number of lanes in the subgroup the unit launched with, which the windows of shifts span. */
  inline def launched: V

  /** The index of the first lane that took part whose local id is `keys` within `scope`, or the unit size if none. */
  inline def lane(scope: V, keys: Vec[V]): V

  /** `combine` over the values `view` holds for the lanes `from` until `until` that took part, starting at its identity
    * or, if `bare`, at the first of them; the values and the result have the type `typed` names.
    */
  inline def fold(view: V, from: V, until: V, combine: Combine, typed: Int, bare: Boolean): V

  /** The 32 bits of the lanes `first` up, each set where `view` holds a non-zero value for a lane that took part. */
  inline def bits(view: V, first: V): V

  /** The 32 bits of the lanes `first` up, each set where `view` holds `own` for a lane that took part. Floating-point
    * values of width `floating` also match where they are equal as numbers, so zeros of either sign match.
    */
  inline def matching(view: V, own: V, first: V, floating: Option[Int in (16, 32, 64)]): V
}
