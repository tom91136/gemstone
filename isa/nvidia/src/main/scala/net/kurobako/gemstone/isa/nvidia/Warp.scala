package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*

/** The warp a lane runs in, as the lane sees it: its place in the warp, the lanes it executes with and the values they
  * exchange. Masks hold one bit per lane of the warp.
  */
private[isa] trait Warp {
  def laneId(): UInt[32]

  /** The warp's index in its workgroup. */
  def warpId(): UInt[32]

  /** The lanes from the warp's first to the end of its workgroup, at most 32. */
  def lanes(): UInt[32]

  def activeMask(): UInt[32]
  def liveMask(): UInt[32]
  def laneMaskEqual(): UInt[32]
  def laneMaskLess(): UInt[32]
  def laneMaskLessEqual(): UInt[32]
  def laneMaskGreater(): UInt[32]
  def laneMaskGreaterEqual(): UInt[32]

  /** The lanes of `mask` that are active. */
  def members(mask: UInt[32]): UInt[32]

  /** The lanes of `mask` that are active, which must include this lane. */
  def participants(mask: UInt[32]): UInt[32]

  /** The predicates of the lanes of `members(mask)`, as bits. */
  def ballot(predicate: Bool, mask: UInt[32]): UInt[32]

  /** `value` as lane `lane` of the warp holds it. */
  def shuffle[T](value: T, lane: UInt[32]): T

  /** The lanes of `members` whose `value` equals this lane's. */
  def matchAny[T](value: T, members: UInt[32]): UInt[32]

  /** `members` where every lane of it holds the same `value`, and none otherwise. */
  def matchAll[T](value: T, members: UInt[32]): UInt[32]

  /** `value` as the warp's lowest active lane holds it. */
  def leader[T](value: T): T

  /** `value` of each lane of `members` combined, in lane order. */
  def reduceAdd[T](value: T, members: UInt[32])(using Type.Reified[T]): T
  def reduceAnd[T](value: T, members: UInt[32])(using Type.Reified[T]): T
  def reduceOr[T](value: T, members: UInt[32])(using Type.Reified[T]): T
  def reduceXor[T](value: T, members: UInt[32])(using Type.Reified[T]): T
  def reduceMinimum[T](value: T, members: UInt[32])(using Type.Reified[T]): T
  def reduceMaximum[T](value: T, members: UInt[32])(using Type.Reified[T]): T

  /** Floating `value` of each lane of `members` reduced, ignoring NaN operands while any operand is a number. */
  def reduceMinimumNumber[T](value: T, members: UInt[32])(using Type.Reified[T]): T
  def reduceMaximumNumber[T](value: T, members: UInt[32])(using Type.Reified[T]): T
}
