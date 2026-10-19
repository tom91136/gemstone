package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*

object BenchmarkSample {
  enum AllocationScope derives CanEqual {
    case CallingThread, Workers, Unavailable
  }
}

case class BenchmarkSample(
    elapsedNanos: Long >= 0,
    operations: Long > 0,
    allocatedBytes: Option[Long >= 0],
    allocationScope: BenchmarkSample.AllocationScope
)
