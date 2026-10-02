package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*

object Measurement {

  /** Inline the measured body so counters do not become captured mutable references. */
  inline def apply[A](inline body: A): Measurement[A] = {
    val before    = PlatformMetrics.allocatedBytes
    val started   = platform.monotonicNanos
    val value     = body
    val elapsed   = math.max(0L, platform.monotonicNanos - started)
    val after     = PlatformMetrics.allocatedBytes
    val allocated = for {
      start <- before
      end   <- after
      if end >= start
    } yield end - start
    Measurement(value, elapsed.unsafe[Long >= 0], allocated)
  }
}

case class Measurement[A](value: A, elapsedNanos: Long >= 0, allocatedBytes: Option[Long >= 0])
