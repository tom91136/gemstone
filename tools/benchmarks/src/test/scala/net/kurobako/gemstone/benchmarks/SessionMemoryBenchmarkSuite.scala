package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*

class SessionMemoryBenchmarkSuite extends munit.FunSuite {
  for (shared <- List(false, true))
    test(s"session memory verifies uneven allocation traffic with shared=$shared") {
      val arguments = Array("--iterations", "17", "--allocations", "3", "--workers", "2") ++
        (if (shared) Array("--shared") else Array.empty[String])
      val result = SessionMemoryBenchmark.run(arguments)
      assertEquals(result.operations.raw, 34L)
    }
}
