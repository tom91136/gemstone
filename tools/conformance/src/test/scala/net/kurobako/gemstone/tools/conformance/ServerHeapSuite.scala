package net.kurobako.gemstone.tools.conformance

class ServerHeapSuite extends munit.FunSuite {
  private val runner      = 15983L << 20
  private val workstation = 123L << 30

  test("the server, two cases, and the build fit the memory of a CI runner") {
    val heap = CLI.serverHeapMegabytes(2, runner)
    assert(heap + 2 * CLI.CaseMegabytes + CLI.ReservedMegabytes <= (runner >> 20), s"heap $heap MB")
  }

  test("a workstation keeps the heap sized by its concurrent cases") {
    assertEquals(CLI.serverHeapMegabytes(16, workstation), 28L << 10)
    assertEquals(CLI.serverHeapMegabytes(2, workstation), 14L << 10)
  }

  test("a small host keeps a usable heap") {
    assertEquals(CLI.serverHeapMegabytes(2, 8L << 30), CLI.MinimumHeapMegabytes)
  }
}
