package net.kurobako.gemstone.isa.ptx.v9_3

import VirtualMachine.*

import net.kurobako.gemstone.memory.GuestAddress

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class TraceSuite extends munit.FunSuite {
  test("trace rendering is deterministic JSON lines") {
    val trace = Trace.empty
      .append(Trace.Event.SchedulerDecision(0L, 0, 1))
      .append(Trace.Event.OutputWrite(1L, 0, 1, GuestAddress(4096L), 42))

    assertEquals(
      trace.jsonLines,
      """{"event":"scheduler-decision","step":"0","block":"0","lane":"1"}
        |{"event":"output-write","step":"1","block":"0","lane":"1","address":"4096","value":"42"}""".stripMargin
    )
  }
}
