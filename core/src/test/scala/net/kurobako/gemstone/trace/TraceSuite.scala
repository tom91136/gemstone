package net.kurobako.gemstone.trace

class TraceSuite extends munit.FunSuite {
  test("trace rendering is deterministic JSON lines") {
    val trace = Trace.empty
      .append(TraceEvent.SchedulerDecision(0L, 0, 1))
      .append(TraceEvent.OutputWrite(1L, 0, 1, 4096L, 42))

    assertEquals(
      trace.jsonLines,
      """{"event":"scheduler-decision","step":"0","block":"0","lane":"1"}
        |{"event":"output-write","step":"1","block":"0","lane":"1","address":"4096","value":"42"}""".stripMargin
    )
  }
}
