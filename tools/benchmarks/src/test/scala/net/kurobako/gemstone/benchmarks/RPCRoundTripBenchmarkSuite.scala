package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*

class RPCRoundTripBenchmarkSuite extends munit.FunSuite {
  for (transport <- List("tcp", "uds")) test(s"verified persistent RPC round trips over $transport") {
    val sample = RPCRoundTripBenchmark.run(Array("--transport", transport, "--iterations", "8"))
    assertEquals(sample.operations.raw, 8L)
    assert(sample.allocationScope != BenchmarkSample.AllocationScope.Workers)
  }
}
