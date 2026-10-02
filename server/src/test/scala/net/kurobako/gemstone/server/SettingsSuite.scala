package net.kurobako.gemstone.server

import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

class SettingsSuite extends munit.FunSuite {
  test("settings validate execution overrides before allocating resources") {
    assertEquals(Server.Settings.fromEnvironment(Map.empty).map(_.limits), Right(TaskControl.Limits.Default))
    assertEquals(
      Server.Settings.fromEnvironment(
        Map(environment.ExecutionMaxInstructions -> "17")
      ).map(_.limits.maxInstructions.raw),
      Right(17L)
    )
    for (value <- List("0", "-1", "+1", " 1", "1 ", "invalid", "99999999999999999999"))
      assert(Server.Settings.fromEnvironment(Map(environment.ExecutionMaxInstructions -> value)).isLeft)
    for (value <- List("0", "-1", "+1", " 1", "1 ", "invalid", Int.MaxValue.toString))
      assert(Server.Settings.fromEnvironment(Map(environment.ExecutionParallelism -> value)).isLeft)
    assertEquals(
      Server.Settings.fromEnvironment(Map(environment.ExecutionParallelism -> "1")).map(_.parallelism.raw),
      Right(1)
    )
  }
  test("RPC timeout uses seconds and rejects values that overflow milliseconds") {
    val key = environment.RpcTimeoutSeconds
    assertEquals(Server.Settings.fromEnvironment(Map(key -> "7")).map(_.requestTimeoutMillis.raw), Right(7000))
    assertEquals(
      Server.Settings.fromEnvironment(Map.empty).map(_.requestTimeoutMillis.raw.toLong),
      Right(environment.RpcTimeoutSecondsDefault * 1000)
    )
    for (value <- List("0", "-1", "+1", "1 ", "2147484", "18446744073709551616"))
      assert(Server.Settings.fromEnvironment(Map(key -> value)).isLeft, value)
  }

  test("settings retain timeout boundary values and useful invalid-input diagnostics") {
    val maximumSeconds = Int.MaxValue / 1000
    assertEquals(
      Server.Settings.fromEnvironment(Map(environment.RpcTimeoutSeconds -> maximumSeconds.toString))
        .map(_.requestTimeoutMillis.raw),
      Right(maximumSeconds * 1000)
    )
    assertEquals(
      Server.Settings.fromEnvironment(Map(environment.ExecutionMaxInstructions -> "+1")),
      Left(s"${environment.ExecutionMaxInstructions} must be a positive integer: +1")
    )
    assertEquals(
      Server.Settings.fromEnvironment(Map(environment.ExecutionParallelism -> Long.MaxValue.toString)),
      Left(s"${environment.ExecutionParallelism} must be a positive integer: ${Long.MaxValue}")
    )
    assert(Server.Settings.fromEnvironment(Map(environment.Record -> "bad\u0000path")).isLeft)
  }

  test("performance tracing follows scope tokens and ignores unrelated categories") {
    for (
      (value, expected) <- List(
        "perf"             -> true,
        "all"              -> true,
        "cuda, \tperf \t"  -> true,
        "performance"      -> false,
        "perf.allocations" -> false,
        "\nperf"           -> false
      )
    )
      assertEquals(
        Server.Settings.fromEnvironment(Map(environment.Trace -> value)).map(_.tracePerformance),
        Right(expected)
      )
  }
}
