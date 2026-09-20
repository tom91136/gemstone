package net.kurobako.gemstone.isa.amdgpu

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.binary.BitWidth
import net.kurobako.gemstone.binary.Bits

private[amdgpu] type LaneCount = LaneCount.T
private[amdgpu] object LaneCount extends RefinedType[Int, Interval.Closed[1, 64]] {
  extension (lanes: LaneCount) {
    @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
    def bitWidth: BitWidth = BitWidth.assume(lanes.value)
  }
}

private[amdgpu] type LaneIndex = LaneIndex.T
private[amdgpu] object LaneIndex extends RefinedType[Int, Interval.Closed[0, 63]]

private[amdgpu] type WaveSize = WaveSize.T
private[amdgpu] object WaveSize extends RefinedType[Int, In[(32, 64)]]

/** Dense interpreter register banks are allocated on first access; null is the allocation-free hot-path sentinel. */
private[amdgpu] final class VectorRegisters(lanes: LaneCount) {
  private val registers = Array.ofDim[Array[Long] | Null](256)

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def apply(index: Int): Array[Long] = registers(index) match {
    case null =>
      val allocated = Array.ofDim[Long](lanes.value)
      registers(index) = allocated
      allocated
    case value => value
  }
}

/** Mutable architectural VCC/EXEC storage for one wave. */
private[amdgpu] final class LaneBits(lanes: LaneCount, initial: Long) {
  private val laneLimit = Bits.mask(lanes.bitWidth)
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

  def apply(lane: Int): Boolean = (value & (1L << lane)) != 0L

  def update(lane: Int, enabled: Boolean): Unit = {
    val mask       = 1L << lane
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

  def count: Int        = bitCount
  def isEmpty: Boolean  = bitCount == 0
  def nonEmpty: Boolean = bitCount != 0
  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  def first: Option[LaneIndex] =
    Option.when(value != 0L)(LaneIndex.assume(java.lang.Long.numberOfTrailingZeros(value)))
}

/** Per-lane scratch is allocated on first access; null is the allocation-free hot-path sentinel. */
private[amdgpu] final class ScratchMemory(lanes: LaneCount, bytesPerLane: Int) {
  private val values = Array.ofDim[Array[Byte] | Null](lanes.value)

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def apply(lane: Int): Array[Byte] = values(lane) match {
    case null =>
      val allocated = Array.ofDim[Byte](bytesPerLane)
      values(lane) = allocated
      allocated
    case value => value
  }
}

private[amdgpu] final case class WaveState(
    scalar: Array[Long],
    vector: VectorRegisters,
    carry: LaneBits,
    active: LaneBits,
    scalarCarry: Boolean,
    scratch: ScratchMemory,
    nextInstruction: Int
)

private[amdgpu] enum WaveBoundary {
  case Barrier(state: WaveState)
  case End
}
