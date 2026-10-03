package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

private object VMState {
  type Lane = Int where (AtLeast[0] & LessThan[64])
  val LaneIndices: Vec[Lane] = Vec.range[0, 64]

  /** Mutable architectural VCC/EXEC storage for one wave. */
  class LaneBits(lanes: Int in 1 ~ 64, initial: Long) {
    private val laneLimit = Bits.mask(lanes)
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var value = initial & laneLimit
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var storage = initial & laneLimit
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var bitCount = java.lang.Long.bitCount(value)

    def bits: Long               = value
    def bits_=(next: Long): Unit = writeLanes(next)

    def register: Long               = storage
    def register_=(next: Long): Unit = {
      storage = next
      value = next & laneLimit
      bitCount = java.lang.Long.bitCount(value)
    }

    private def writeLanes(next: Long): Unit = {
      value = next & laneLimit
      storage = (storage & ~laneLimit) | value
      bitCount = java.lang.Long.bitCount(value)
    }

    def apply(lane: Lane): Boolean = {
      require(lane.raw < lanes.raw, s"lane ${lane.raw} exceeds wave size ${lanes.raw}")
      (value & (1L << lane.raw)) != 0L
    }

    def update(lane: Lane, enabled: Boolean): Unit = {
      require(lane.raw < lanes.raw, s"lane ${lane.raw} exceeds wave size ${lanes.raw}")
      val mask       = 1L << lane.raw
      val wasEnabled = (value & mask) != 0L
      if (enabled != wasEnabled) {
        if (enabled) {
          value |= mask
          storage |= mask
          bitCount += 1
        } else {
          value &= ~mask
          storage &= ~mask
          bitCount -= 1
        }
      }
    }

    def count: Int                              = bitCount
    def isEmpty: Boolean                        = bitCount == 0
    def nonEmpty: Boolean                       = bitCount != 0
    def first: Option[Int in 0 ~ 63] =
      Option.when(value != 0L)(java.lang.Long.numberOfTrailingZeros(value).unsafe[Int in 0 ~ 63])
  }

  /** Dense interpreter register banks are allocated on first access; null is the allocation-free hot-path sentinel. */
  class VectorRegisters(lanes: Int in 1 ~ 64) {
    private val registers = Array.ofDim[Array[Long] | Null](256)

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def apply(index: VMIR.VectorRegisterIndex): Array[Long] = {
      registers(index.raw) match {
        case null =>
          val allocated = Array.ofDim[Long](lanes.raw)
          registers(index.raw) = allocated
          allocated
        case value => value
      }
    }
  }

  /** Per-lane scratch is allocated on first access; null is the allocation-free hot-path sentinel. */
  class ScratchMemory(lanes: Int in 1 ~ 64, bytesPerLane: Int >= 0) {
    private val values = Array.ofDim[Array[Byte] | Null](lanes.raw)

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def apply(lane: Lane): Array[Byte] = {
      require(lane.raw < lanes.raw, s"lane ${lane.raw} exceeds the wave size ${lanes.raw}")
      values(lane.raw) match {
        case null =>
          val allocated = Array.ofDim[Byte](bytesPerLane.raw)
          values(lane.raw) = allocated
          allocated
        case value => value
      }
    }
  }

  case class WaveState(
      scalar: Array[Long],
      vector: VectorRegisters,
      carry: LaneBits,
      active: LaneBits,
      scalarCarry: Boolean,
      scratch: ScratchMemory,
      nextInstruction: Int
  )
}
