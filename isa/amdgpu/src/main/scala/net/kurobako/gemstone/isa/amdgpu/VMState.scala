package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

private object VMState {
  type Lane = Int where (AtLeast[0] & LessThan[64])

  /** MODE register fields that decide whether OMOD applies. */
  object Mode {
    inline val DenormalShift       = 4
    val Denormal32Output: Int      = 1 << 5
    val Denormal16And64Output: Int = 1 << 7
    val IEEE: Int                  = 1 << 9

    /** GFX12 retired IEEE mode, so its MODE bit 9 always reads as clear. */
    def ieeeSupported(isa: ISA): Boolean = isa != ISA.RDNA4 && isa != ISA.CDNA5

    /** Hardware ignores OMOD under IEEE mode or when the result width keeps output denormals. */
    def outputModifierApplies(mode: Int, width: Int in (16, 32, 64)): Boolean =
      (mode & (IEEE | (if (width.raw == 32) Denormal32Output else Denormal16And64Output))) == 0
  }

  /** The two implicit RDNA4 workgroup barriers have independent arrival counts and per-wave completion bits. */
  class WorkgroupBarriers(waves: Int) {
    private val live      = Array.fill(waves)(true)
    private val arrived   = Array.fill(2, waves)(false)
    private val completed = Array.fill(2, waves)(false)

    private def complete(barrier: Int): Unit =
      if (live.indices.forall(wave => !live(wave) || arrived(barrier)(wave))) {
        java.util.Arrays.fill(arrived(barrier), false)
        java.util.Arrays.fill(completed(barrier), true)
      }

    def signal(id: Int, wave: Int): Either[Fault, Boolean] =
      if (id == 0) Right(false)
      else if (id != -1 && id != -2) Left(Fault.Unsupported(s"AMDGPU barrier $id is not implemented"))
      else {
        val barrier = -id - 1
        val first   = !arrived(barrier).contains(true)
        if (arrived(barrier)(wave)) Left(Fault.Backend(s"wave $wave signalled barrier $id twice in one phase"))
        else {
          arrived(barrier)(wave) = true
          complete(barrier)
          Right(first)
        }
      }

    def await(id: Int, wave: Int): Either[Fault, Boolean] =
      if (id == 0) Right(true)
      else if (id != -1 && id != -2) Left(Fault.Unsupported(s"AMDGPU barrier $id is not implemented"))
      else {
        val barrier = -id - 1
        val ready   = completed(barrier)(wave)
        if (ready) completed(barrier)(wave) = false
        Right(ready)
      }

    def drop(wave: Int): Unit = {
      live(wave) = false
      arrived.indices.foreach { barrier =>
        if (arrived(barrier).contains(true)) complete(barrier)
      }
    }
  }

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
    def bits_=(next: Long): Unit = {
      value = next & laneLimit
      storage = (storage & ~laneLimit) | value
      bitCount = java.lang.Long.bitCount(value)
    }

    def register: Long               = storage
    def register_=(next: Long): Unit = {
      storage = next
      value = next & laneLimit
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

    def count: Int                   = bitCount
    def isEmpty: Boolean             = bitCount == 0
    def nonEmpty: Boolean            = bitCount != 0
    def first: Option[Int in 0 ~ 63] =
      Option.when(value != 0L)(java.lang.Long.numberOfTrailingZeros(value).unsafe[Int in 0 ~ 63])
  }

  /** Dense interpreter register banks are allocated on first access; null is the allocation-free hot-path sentinel. */
  class VectorRegisters(lanes: Int in 1 ~ 64) {
    private val registers = Array.ofDim[Array[Long] | Null](256)

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def apply(index: VMIR.VectorRegisterIndex): Array[Long] =
      registers(index.raw) match {
        case null =>
          val allocated = Array.ofDim[Long](lanes.raw)
          registers(index.raw) = allocated
          allocated
        case value => value
      }
  }

  /** Per-lane scratch is allocated on first access; null is the allocation-free hot-path sentinel. */
  class ScratchMemory(lanes: Int in 1 ~ 64, bytesPerLane: Int >= 0) {
    private val values = Array.ofDim[Array[Byte] | Null](lanes.raw)

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def apply(lane: Lane): Array[Byte] = {
      require(lane.raw < lanes.raw, s"lane ${lane.raw} exceeds wave size ${lanes.raw}")
      values(lane.raw) match {
        case null =>
          val allocated = Array.ofDim[Byte](bytesPerLane.raw)
          values(lane.raw) = allocated
          allocated
        case value => value
      }
    }
  }
}
