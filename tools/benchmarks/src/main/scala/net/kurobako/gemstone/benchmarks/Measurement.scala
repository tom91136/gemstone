package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

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

  /** Measure `iterations` runs of `body` as that many operations. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def repeat(iterations: Int > 0)(inline body: Unit): BenchmarkSample =
    apply {
      var iteration = 0
      while (iteration < iterations.raw) {
        body
        iteration += 1
      }
    }.sample(iterations.toLong)

  /** A fault aborts the benchmark rather than timing the failure path. */
  def succeeded(result: Either[Fault, ?]): Unit =
    result.fold(fault => throw IllegalStateException(fault.message), _ => ())

  /** Measure `iterations` runs of `body`, each of which must succeed. */
  inline def repeatSucceeding(iterations: Int > 0)(inline body: Either[Fault, ?]): BenchmarkSample =
    repeat(iterations)(succeeded(body))

  /** Measure `iterations` runs on a dedicated pool of `workers`, each of which must succeed. */
  def repeatPooled(iterations: Int > 0, workers: Int > 0)(run: Workgroups => Either[Fault, ?]): BenchmarkSample =
    Using.resource(WorkerPool(parallelism = workers, minGroups = 1, minWorkItems = 1)) { pool =>
      repeatSucceeding(iterations)(run(pool))
    }
}

case class Measurement[A](value: A, elapsedNanos: Long >= 0, allocatedBytes: Option[Long >= 0]) {
  def sample(operations: Long > 0): BenchmarkSample = BenchmarkSample(
    elapsedNanos,
    operations,
    allocatedBytes,
    if (allocatedBytes.isDefined) BenchmarkSample.AllocationScope.CallingThread
    else BenchmarkSample.AllocationScope.Unavailable
  )
}
