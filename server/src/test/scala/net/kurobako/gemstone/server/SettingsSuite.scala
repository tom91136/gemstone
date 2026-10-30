package net.kurobako.gemstone.server

import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.*

class SettingsSuite extends Suite {
  test("settings validate execution overrides before allocating resources") {
    assertEquals(Server.Settings.fromEnvironment(Map.empty).map(_.limits), Right(TaskControl.Limits.Default))
    assertEquals(
      Server.Settings.fromEnvironment(
        Map(environment.ExecutionMaxInstructionCount -> "17")
      ).map(_.limits.maxInstructions.raw),
      Right(17L)
    )
    for (value <- List("0", "-1", "+1", " 1", "1 ", "invalid", "99999999999999999999"))
      assert(Server.Settings.fromEnvironment(Map(environment.ExecutionMaxInstructionCount -> value)).isLeft)
    for (value <- List("0", "-1", "+1", " 1", "1 ", "invalid", Int.MaxValue.toString))
      assert(Server.Settings.fromEnvironment(Map(environment.ExecutionThreadCount -> value)).isLeft)
    assertEquals(
      Server.Settings.fromEnvironment(Map(environment.ExecutionThreadCount -> "1")).map(_.parallelism.raw),
      Right(1)
    )
  }
  test("RPC timeout uses seconds and rejects values that overflow milliseconds") {
    val key = environment.RpcTimeoutSeconds
    assertEquals(Server.Settings.fromEnvironment(Map(key -> "7")).map(_.requestTimeoutMillis.raw), Right(7000))
    assertEquals(
      Server.Settings.fromEnvironment(Map.empty).map(_.requestTimeoutMillis.raw.toLong),
      Right((environment.RpcTimeoutSecondsDefault * 1000L).raw)
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
      Server.Settings.fromEnvironment(Map(environment.ExecutionMaxInstructionCount -> "+1")),
      Left(s"${environment.ExecutionMaxInstructionCount} must be a positive integer: +1")
    )
    assertEquals(
      Server.Settings.fromEnvironment(Map(environment.ExecutionThreadCount -> Long.MaxValue.toString))
        .left.map(_.startsWith(
          s"${environment.ExecutionThreadCount} must be a positive integer at most the host bound"
        )),
      Left(true)
    )
    assertEquals(
      Server.Settings.fromEnvironment(Map(environment.RecordFile -> "bad\u0000path")),
      Left(s"${environment.RecordFile} must not contain a NUL character")
    )
  }

  test("replay applies the recorded profile within the host bound and drops recording") {
    def profile(parallelism: Int) = Journal.ExecutionProfile(17L, parallelism.unsafe[Int > 0])
    val replayed                  = Server.Settings.replay(Map(environment.RecordFile -> "capture.journal"), profile(1))
    assertEquals(replayed.map(s => (s.limits.maxInstructions.raw, s.parallelism.raw, s.record)), Right((17L, 1, None)))
    assert(Server.Settings.replay(Map.empty, profile(sys.runtime.availableProcessors() + 65)).isLeft)
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
        Server.Settings.fromEnvironment(Map(environment.TraceList -> value)).map(_.tracePerformance),
        Right(expected)
      )
  }
}
