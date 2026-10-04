package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*

object VMMemoryBarrier {
  val MaximumCount: Long = (1L << 20) - 1L

  /** Initial TRANS64 words emitted by ptxas: q = 2^20 - count, low = q<<1, high = q<<11. */
  def fromStoredInitialState(bits: Long): Either[VMFault, VMMemoryBarrier] = {
    val low  = bits & 0xffffffffL
    val high = bits >>> 32
    val q    = low >>> 1
    if ((low & 1L) != 0 || q == 0 || q > MaximumCount || high != (q << 11))
      Left(VMFault.InvalidState("invalid stored TRANS64 mbarrier initializer"))
    else (MaximumCount + 1L - q).refine[Long in 1L ~ 1048575L]
      .left.map(VMFault.InvalidState(_)).map(VMMemoryBarrier(_))
  }

  def create(count: Long): Either[VMFault, VMMemoryBarrier] =
    count.refine[Long in 1L ~ 1048575L]
      .left.map(_ => VMFault.InvalidState(s"mbarrier arrival count is out of range: $count"))
      .map(VMMemoryBarrier(_))

}

/** One CTA-local mbarrier. Copies complete synchronously, but arrivals and transaction counts remain independent. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class VMMemoryBarrier private (expectedArrivals: Long in 1L ~ 1048575L) {
  private var phase           = 0L
  private var pendingArrivals = expectedArrivals.raw
  private var pendingBytes    = 0L

  def arrive(count: Long, expectedBytes: Long): Either[VMFault, Long] =
    if (
      count <= 0 || count > pendingArrivals || expectedBytes < 0 || expectedBytes > VMMemoryBarrier.MaximumCount ||
      pendingBytes + expectedBytes > VMMemoryBarrier.MaximumCount
    )
      Left(VMFault.InvalidState(s"invalid mbarrier arrival: count=$count, expected bytes=$expectedBytes"))
    else {
      val token = phase
      pendingBytes += expectedBytes
      pendingArrivals -= count
      completePhase()
      Right(token)
    }

  def completeBytes(count: Long): Either[VMFault, Unit] =
    if (count < 0 || count > VMMemoryBarrier.MaximumCount || pendingBytes - count < -VMMemoryBarrier.MaximumCount)
      Left(VMFault.InvalidState(s"invalid mbarrier transaction completion: $count"))
    else {
      pendingBytes -= count
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
