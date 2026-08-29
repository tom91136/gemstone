package net.kurobako.gemstone.conformance

import ConformanceSupport.*

import scala.concurrent.duration.*

final class ConformanceSupportSuite extends munit.FunSuite {
  test("external commands fail deterministically when their deadline expires") {
    val result = run(Vector("sh", "-c", "sleep 30"), root, timeout = 100.millis)

    assertEquals(result.exit, -1)
    assert(result.diagnostic.contains("timed out after"))
  }
}
