package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*

class ConfigurationSuite extends munit.FunSuite {
  test("positive decimal configuration rejects signs, whitespace and overflow") {
    for (value <- List("", "0", "00", "-1", "+1", " 1", "1 ", "1\n", "0x10", "1.5", "9223372036854775808", "١"))
      assertEquals(configuration.positiveLong(value), None, value)
    assertEquals(configuration.positiveLong("00017").map(_.raw), Some(17L))
    assertEquals(configuration.positiveLong(Long.MaxValue.toString).map(_.raw), Some(Long.MaxValue))
  }

  test("trace scope and category matching is exact, with spaces and tabs only") {
    for (value <- List("all", "rpc", "rpc.memory", "cuda, \trpc.memory\t ,perf"))
      assert(configuration.traceEnabled(value, "rpc", "memory"), value)
    for (value <- List("", "rpc.memory.extra", "rpc.other", "rp", "RPC", "\nrpc", "rpc\r"))
      assert(!configuration.traceEnabled(value, "rpc", "memory"), value)
    assert(!configuration.traceEnabled("rpc.memory", "rpc"))
  }
}
