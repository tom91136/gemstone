package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.semantics.prelude.*

/** The mbarriers in shared memory, the workgroup barriers, and the convergence barriers and member masks of warps. */
private[isa] trait Barriers {
  type Reduction = (result: VMIR.BarrierResult, predicate: VMIR.Predicate, negated: Boolean)

  def parkReduce(barrier: UInt[64], count: UInt[64], reduction: Reduction): Unit
  def parkEach(barrier: UInt[64], count: UInt[64], blocking: Boolean, reduction: Option[Reduction]): Unit
  def warpSync(memberMask: UInt[64]): Unit
  def init(barrier: UInt[64], count: UInt[64]): Unit
  def invalidate(barrier: UInt[64]): Unit
  def load(barrier: UInt[64]): Unit
  def arrive(barrier: UInt[64], count: UInt[64], bytes: UInt[64], drop: Bool, complete: Bool): UInt[64]
  def transaction(barrier: UInt[64], bytes: UInt[64], complete: Bool): Unit
  def test(barrier: UInt[64], phase: UInt[64], parity: Bool): Bool
  def setup(): UInt[64]
  def synchronise(token: UInt[64]): Unit
  def leave(token: UInt[64]): Unit
}
