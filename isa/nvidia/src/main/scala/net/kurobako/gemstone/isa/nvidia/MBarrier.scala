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
}

/** One CTA-local mbarrier. Copies complete synchronously, but arrivals and transaction counts remain independent. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private class MBarrier private (expectedArrivals: Long in 1L ~ 1048575L) {
  import MBarrier.*

  private var phase           = 0L
  private var pendingArrivals = expectedArrivals.raw
  private var pendingBytes    = 0L

  def arrive(count: Long, expectedBytes: Long): Either[Fault, Long] =
    transactionBytes(expectedBytes).flatMap { bytes =>
      if (count <= 0 || count > pendingArrivals || pendingBytes + bytes.raw > MaximumCount)
        Left(Fault.Backend(s"invalid mbarrier arrival: count=$count, expected bytes=$expectedBytes"))
      else {
        val token = phase
        pendingBytes += bytes.raw
        pendingArrivals -= count
        completePhase()
        Right(token)
      }
    }

  def completeBytes(count: Count): Either[Fault, Unit] =
    if (pendingBytes - count.raw < -MaximumCount)
      Left(Fault.Backend(s"invalid mbarrier transaction completion: $count"))
    else {
      pendingBytes -= count.raw
      completePhase()
      Right(())
    }

  def complete(token: Long, parity: Boolean): Boolean =
    if (parity) (phase & 1L) != token else phase != token

  private def completePhase(): Unit =
    if (pendingArrivals == 0 && pendingBytes == 0) {
      phase += 1L
      pendingArrivals = expectedArrivals.raw
    }
}
