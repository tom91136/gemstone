package net.kurobako.gemstone.semantics

import net.kurobako.gemstone.collection.*

/** The views of the lanes of a unit, a warp, wave or subgroup, over the values and operands of [[MachineAlgebra]]. A
  * view holds a value for each lane of a unit and is itself a `V`, which [[peer]] indexes. The inactive lanes of a view
  * hold zero. An instruction keeps each view it holds in a [[UnitAlgebra.Slot]].
  */
trait UnitView[V, O, W, L, F[_]] extends Any {

  /** The number of lanes in a unit. */
  inline def unitSize: V

  /** The index of `lane` within its unit. */
  inline def laneIndex(lane: L): V

  /** 1 if lane `index` of the unit that holds `lane` is active, otherwise 0. */
  inline def active(lane: L, index: V): V

  /** `next` over the view in `slot` of `operand`, read on every active lane. */
  inline def gather[A](slot: UnitAlgebra.Slot, operand: O)(inline next: V => F[A]): F[A]

  /** The value lane `index` of the unit that holds `lane` has in `view`, or zero if `index` is outside the unit. */
  inline def peer(lane: L, view: V, index: V): V

  /** Each target written on every active lane with the value computed for that lane and the target's position, then
    * `next`. A value reads views, never targets.
    */
  inline def writeEach[A](targets: Vec[W])(inline value: (L, Int) => V)(inline next: => F[A]): F[A]
}
