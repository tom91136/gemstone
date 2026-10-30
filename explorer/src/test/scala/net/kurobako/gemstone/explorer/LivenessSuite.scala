package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class LivenessSuite extends Suite {

  private val group                           = Coordinates(0, 0, 0)
  private def unit(ordinal: Int): MachineUnit =
    MachineUnit(group, ordinal.unsafe[Int >= 0], 1, MachineUnit.State.Running)
  private def location(function: String): Fault.Location = Fault.Location(
    "kernel".unsafe[NonEmpty[String]],
    Some(function.unsafe[NonEmpty[String]]),
    Some(0L),
    Some(group),
    None
  )
  private def step(
      issue: Long,
      ordinal: Int,
      reads: Vec[String] = Vec.empty,
      writes: Vec[String] = Vec.empty,
      function: String = "kernel"
  ): TraceStep =
    TraceStep(issue, location(function), unit(ordinal), Vec.empty, RegisterUse(reads, writes))

  test("a register is live from its definition to its last read") {
    val trace = Trace(
      Vec(
        step(0L, 0, writes = Vec("%a")),
        step(1L, 0, reads = Vec("%a"), writes = Vec("%b")),
        step(2L, 0, reads = Vec("%b"))
      ),
      Vec.empty
    )
    val live = Liveness.of(trace)
    assertEquals(live.perStep, Vec(0, 1, 1))
    assertEquals(live.pressure, Vec(0, 1, 1))
    assertEquals(live.peak, 1)
    assertEquals(live.liveAt(trace, 1), Vec("%a"))
    assertEquals(live.liveAt(trace, 2), Vec("%b"))
    assertEquals(
      live.registers.map(row => (row.name, row.spans)),
      Vec(
        ("%a", Vec(LiveSpan(start = 1L, end = 2L, units = 1))),
        ("%b", Vec(LiveSpan(start = 2L, end = 3L, units = 1)))
      )
    )
  }

  test("a unit keeps its live registers while another unit issues") {
    val trace = Trace(
      Vec(
        step(0L, 0, writes = Vec("%a")),
        step(1L, 0),
        step(2L, 1, writes = Vec("%x")),
        step(3L, 1, reads = Vec("%x")),
        step(4L, 0, reads = Vec("%a")),
        step(5L, 0)
      ),
      Vec.empty
    )
    val live = Liveness.of(trace)
    assertEquals(live.pressure, Vec(0, 1, 1, 2, 1, 0))
    assertEquals(live.peak, 2)
    assertEquals(
      live.registers.map(row => (row.name, row.spans)),
      Vec(
        ("%a", Vec(LiveSpan(start = 1L, end = 5L, units = 1))),
        ("%x", Vec(LiveSpan(start = 3L, end = 4L, units = 1)))
      )
    )
  }

  test("a register's row counts the units that keep it live") {
    val trace = Trace(
      Vec(
        step(0L, 0, writes = Vec("%a")),
        step(1L, 1, writes = Vec("%a")),
        step(2L, 0, reads = Vec("%a")),
        step(3L, 1, reads = Vec("%a"))
      ),
      Vec.empty
    )
    val live = Liveness.of(trace)
    assertEquals(live.pressure, Vec(0, 1, 2, 1))
    assertEquals(
      live.registers.head.spans,
      Vec(
        LiveSpan(start = 1L, end = 2L, units = 1),
        LiveSpan(start = 2L, end = 3L, units = 2),
        LiveSpan(start = 3L, end = 4L, units = 1)
      )
    )
  }

  test("a caller's registers are distinct from a callee's") {
    val trace = Trace(
      Vec(
        step(0L, 0, writes = Vec("%a")),
        step(1L, 0, reads = Vec("%a"), function = "callee"),
        step(2L, 0, reads = Vec("%a"))
      ),
      Vec.empty
    )
    val live = Liveness.of(trace)
    assertEquals(live.liveAt(trace, 0), Vec("callee:%a"))
    assertEquals(live.liveAt(trace, 1), Vec("%a", "kernel:%a"))
  }

  test("a machine that reports no uses says so") {
    assertEquals(Liveness.of(Trace.empty).reported, false)
    assertEquals(Liveness.of(Trace(Vec(step(0L, 0)), Vec.empty)).reported, false)
    assertEquals(Liveness.of(Trace(Vec(step(0L, 0, reads = Vec("%a"))), Vec.empty)).reported, true)
  }
}
