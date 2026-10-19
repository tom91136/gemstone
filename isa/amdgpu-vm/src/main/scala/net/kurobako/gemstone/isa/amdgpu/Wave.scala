package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.prelude.*

/** The wave a lane of a rendered AMDGPU definition runs in. Lane masks are 64 bits; a wave32 lane mask is the low 32.
  */
private[isa] trait Wave {

  /** This lane's index in the wave. */
  def lane(): UInt[32]

  /** EXEC: bit `n` set when lane `n` is active. */
  def exec(): UInt[64]

  /** The lane mask of the active lanes whose `predicate` holds. */
  def ballot(predicate: Bool): UInt[64]

  /** `value`, a vector register this lane reads, as lane `index` holds it, active or not. */
  def read(value: UInt[32], index: UInt[32]): UInt[32]

  /** What this lane receives when each active lane sends its `value` to lane `destination`, the highest sending lane
    * winning, or `existing` when none sends here.
    */
  def permute(value: UInt[32], destination: UInt[32], existing: UInt[32]): UInt[32]

  /** VGPR `index` of this lane, or VGPR 0 where `index` is beyond the last. */
  def vgpr(index: UInt[32]): UInt[32]

  /** VGPR `index` as lane `lane` holds it, active or not, or its VGPR 0 where `index` is beyond the last. */
  def vgprAt(index: UInt[32], lane: UInt[32]): UInt[32]

  /** Writes VGPR `index` of this lane, or nothing where `index` is beyond the last. */
  def writeVgpr(index: UInt[32], value: UInt[32]): Unit
}
