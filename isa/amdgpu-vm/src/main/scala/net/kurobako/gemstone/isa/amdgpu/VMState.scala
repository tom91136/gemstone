package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

private object VMState {
  type Lane = Int where (AtLeast[0] & LessThan[64])

  /** The two implicit GFX12 workgroup barriers have independent arrival counts and per-wave completion bits. Where
    * `named`, as on GFX12.5, barriers 1 to 16 are named barriers: each has an expected and an arrival count and a phase
    * that its completions advance, and a wave waits on the one it last joined until its phase moves past the one the
    * wave last saw. Barrier 0 is the null named barrier, on which everything but a join does nothing.
    */
  class WorkgroupBarriers(waves: Int, named: Boolean = false) {
    private val live      = Array.ofDim[Boolean](waves)
    private val arrived   = Array.ofDim[Boolean](2, waves)
    private val completed = Array.ofDim[Boolean](2, waves)

    private val namedBarriers = if (named) 17 else 0
    private val expected      = Array.ofDim[Int](namedBarriers)
    private val arrivals      = Array.ofDim[Int](namedBarriers)
    private val phases        = Array.ofDim[Long](namedBarriers)
    private val joined        = Array.ofDim[Int](waves)
    private val seen          = Array.ofDim[Long](if (named) waves else 0)

    java.util.Arrays.fill(live, true)

    private def complete(barrier: Int): Unit =
      if (live.indices.forall(wave => !live(wave) || arrived(barrier)(wave))) {
        java.util.Arrays.fill(arrived(barrier), false)
        java.util.Arrays.fill(completed(barrier), true)
      }

    private def completeNamed(barrier: Int): Unit =
      if (expected(barrier) > 0 && arrivals(barrier) >= expected(barrier)) {
        arrivals(barrier) = 0
        phases(barrier) += 1
      }

    private def namedBarrier(id: Int): Either[Fault, Int] =
      if (named && id >= 0 && id <= 16) Right(id)
      else Left(Fault.Unsupported(s"AMDGPU barrier $id is not implemented"))

    def signal(id: Int, wave: Int, count: Int = 0): Either[Fault, Boolean] =
      if (id == 0) Right(false)
      else if (id != -1 && id != -2)
        namedBarrier(id).map { barrier =>
          if (count != 0) expected(barrier) = count
          val first = arrivals(barrier) == 0
          arrivals(barrier) += 1
          completeNamed(barrier)
          first
        }
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
      else if (id != -1 && id != -2)
        namedBarrier(id).map { _ =>
          val barrier = joined(wave)
          val ready   = barrier == 0 || phases(barrier) > seen(wave)
          if (ready) seen(wave) = phases(barrier)
          ready
        }
      else {
        val barrier = -id - 1
        val ready   = completed(barrier)(wave)
        if (ready) completed(barrier)(wave) = false
        Right(ready)
      }

    def initialise(id: Int, count: Int): Either[Fault, Unit] = namedBarrier(id).map { barrier =>
      if (barrier != 0) { expected(barrier) = count; arrivals(barrier) = 0 }
    }

    def join(id: Int, wave: Int): Either[Fault, Unit] = namedBarrier(id).map { barrier =>
      joined(wave) = barrier
      seen(wave) = phases(barrier)
    }

    /** Drops the wave's joined named barrier; whether that leaves the barrier expecting no wave. */
    def leave(wave: Int): Boolean = {
      val barrier = joined(wave)
      joined(wave) = 0
      barrier != 0 && {
        expected(barrier) = math.max(0, expected(barrier) - 1)
        completeNamed(barrier)
        expected(barrier) == 0
      }
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

    def update(lane: Lane, enabled: Boolean): Unit = {
      require(lane < lanes, s"lane $lane exceeds wave size $lanes")
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
          val allocated = Array.ofDim[Long](lanes)
          registers(index) = allocated
          allocated
        case value => value
      }
  }

  /** Per-lane scratch is allocated on first access; null is the allocation-free hot-path sentinel. */
  class ScratchMemory(lanes: Int in 1 ~ 64, bytesPerLane: Int >= 0) {
    private val values = Array.ofDim[Array[Byte] | Null](lanes)

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def apply(lane: Lane): Array[Byte] = {
      require(lane < lanes, s"lane $lane exceeds wave size $lanes")
      values(lane.raw) match {
        case null =>
          val allocated = Array.ofDim[Byte](bytesPerLane)
          values(lane) = allocated
          allocated
        case value => value
      }
    }
  }
}
