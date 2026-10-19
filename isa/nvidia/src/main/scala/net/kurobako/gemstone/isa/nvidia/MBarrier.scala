package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

private object MBarrier {
  type Count = Long in 0L ~ 1048575L
  val MaximumCount: Long = (1L << 20) - 1L

  private def count(value: Long, detail: => NonEmpty[String]): Either[Fault, Count] =
    value.refine[Count].left.map(_ => Fault.Backend(detail))

  /** Initial TRANS64 words emitted by ptxas: q = 2^20 - count, low = q<<1, high = q<<11. */
  def fromStoredInitialState(bits: Long): Either[Fault, MBarrier] = {
    val low  = bits & 0xffffffffL
    val high = bits >>> 32
    val q    = low >>> 1
    if ((low & 1L) != 0 || q == 0 || q > MaximumCount || high != (q << 11))
      Left(Fault.Backend("invalid stored TRANS64 mbarrier initialiser"))
    else create(MaximumCount + 1L - q)
  }

  def create(arrivals: Long): Either[Fault, MBarrier] =
    arrivals.refine[Long in 1L ~ 1048575L]
      .left.map(_ => Fault.Backend(s"mbarrier arrival count is out of range: $arrivals"))
      .map(MBarrier(_))

  def transactionBytes(value: Long): Either[Fault, Count] =
    count(value, s"invalid mbarrier transaction size: $value")

  /** The phase parity a token names. */
  def tokenParity(token: Long): Long = token >>> 63
}

/** One mbarrier. Copies complete synchronously, but arrivals and transaction counts remain independent. A token is the
  * barrier's TRANS64 state before an arrival: the reset arrival count from bit 1 and the outstanding arrivals from bit
  * 43, each as 2^20 minus the count, and the phase parity in bit 63.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private class MBarrier private (initialArrivals: Long in 1L ~ 1048575L) {
  import MBarrier.*

  private var phase           = 0L
  private var expected        = initialArrivals.raw
  private var pendingArrivals = expected
  private var pendingBytes    = 0L
  // Once every participant has dropped out, no later phase can complete.
  private var exhausted = false

  def state: Long =
    (((MaximumCount + 1L - expected) & MaximumCount) << 1) |
      (((MaximumCount + 1L - pendingArrivals) & MaximumCount) << 43) |
      ((phase & 1L) << 63)

  /** Arrives `count` times with `expectedBytes` more transaction bytes and returns the token. `drop` also lowers the
    * arrivals later phases expect; without `complete` the arrivals must leave the phase incomplete.
    */
  def arrive(count: Long, expectedBytes: Long, drop: Boolean = false, complete: Boolean = true): Either[Fault, Long] =
    transactionBytes(expectedBytes).flatMap { bytes =>
      if (
        count <= 0 || count > pendingArrivals ||
        (drop && count > expected) ||
        pendingBytes + bytes > MaximumCount ||
        (!complete && count == pendingArrivals)
      ) Left(Fault.Backend(s"invalid mbarrier arrival: count=$count, expected bytes=$expectedBytes"))
      else {
        val token = state
        pendingBytes += bytes
        pendingArrivals -= count
        if (drop) expected -= count
        if (complete) completePhase()
        Right(token)
      }
    }

  def expectBytes(count: Count): Either[Fault, Unit] =
    if (pendingBytes + count > MaximumCount) Left(Fault.Backend(s"invalid mbarrier transaction count: $count"))
    else {
      pendingBytes += count
      Right(())
    }

  def completeBytes(count: Count): Either[Fault, Unit] =
    if (pendingBytes - count < -MaximumCount)
      Left(Fault.Backend(s"invalid mbarrier transaction completion: $count"))
    else {
      pendingBytes -= count
      completePhase()
      Right(())
    }

  /** Whether the phase of parity `parity` has completed. */
  def complete(parity: Long): Boolean = (phase & 1L) != (parity & 1L)

  private def completePhase(): Unit =
    if (pendingArrivals == 0 && pendingBytes == 0 && !exhausted) {
      phase += 1L
      pendingArrivals = expected
      exhausted = expected == 0
    }
}
