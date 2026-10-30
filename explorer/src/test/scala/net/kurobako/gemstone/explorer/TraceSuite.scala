package net.kurobako.gemstone.explorer

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.util.Using

/** Records a PTX launch on the real VM and lays its timeline out. */
class TraceSuite extends Suite {

  private val source: String =
    """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry kernel(.param .u64 .ptr .global out) {
      |  .reg .b32 %r<2>;
      |  .reg .b64 %rd<2>;
      |  ld.param.u64 %rd0, [out];
      |  mov.u32 %r0, %tid.x;
      |  bar.sync 0;
      |  st.global.u32 [%rd0], %r0;
      |  ret;
      |}
      |""".stripMargin

  /** Runs `body` under the loaded program, which is closed afterwards. */
  private def withProgram[A](body: ProgramRun.Loaded => A): A =
    Using.resource(
      ProgramRun
        .load(ProgramListing.Image(Language.PTX, Vec.from(source.getBytes(StandardCharsets.UTF_8)), None))
        .toOption
        .get
    )(body)

  private def launch(): ProgramRun.Run = withProgram { program =>
    ProgramRun.run(program, "kernel".unsafe[NonEmpty[String]], Dimensions(4, 1, 1), Dimensions(2, 1, 1)).toOption.get
  }

  test("the listing numbers the instructions the compiler numbers") {
    withProgram { program =>
      assertEquals(
        program.lines.map(line => (line.function, line.address, line.mnemonic)),
        Vec(
          (Some("kernel"), 0L, "ld"),
          (Some("kernel"), 1L, "mov"),
          (Some("kernel"), 2L, "bar"),
          (Some("kernel"), 3L, "st"),
          (Some("kernel"), 4L, "ret")
        )
      )
    }
  }

  test("every issue is numbered in order, from zero, without a gap") {
    val ordinals = launch().trace.steps.map(_.issue)
    assert(ordinals.nonEmpty)
    assertEquals(ordinals, ordinals.indices.map(_.toLong).toVec)
  }

  test("a step names the instruction the listing holds") {
    withProgram { program =>
      val listing = Listing(program.lines)
      launch().trace.steps.foreach { step =>
        assert(listing.find(step.location).nonEmpty, s"no listing entry for ${step.location}")
      }
    }
  }

  test("each unit retires when its lanes leave, and a released unit resumes after parking") {
    val run     = launch()
    val perUnit = run.trace.changes.groupBy(event => UnitKey.of(event.unit))
    assertEquals(run.failure, None)
    assert(perUnit.nonEmpty)
    perUnit.foreach { (unit, events) =>
      val ordered = events.sortBy(_.issued)
      val blocked = ordered.indexWhere(parked(_))
      val resumed = ordered.indexWhere(_.kind == MachineEvent.Kind.Resumed)
      if (blocked >= 0) assert(resumed > blocked, s"the unit $unit was not released after parking")
      assert(ordered.exists(_.kind == MachineEvent.Kind.Retired), s"the unit $unit never retired")
    }
  }

  test("the timeline stacks each workgroup's units over the issue stream") {
    val rows = Timeline.layout(launch().trace)
    assertEquals(rows.size, 2)
    rows.foreach { row =>
      assert(row.bands.nonEmpty)
      assertEquals(row.lanes, 4)
      row.bands.foreach { band =>
        assert(band.segments.nonEmpty)
        assert(band.segments.head.start < band.segments.last.end)
        assert(
          band.segments.zip(band.segments.drop(1)).forall((left, right) => left.end == right.start),
          "the segments of a band do not meet"
        )
      }
    }
  }

  test("the timeline draws a blocked stretch between the change that parks a unit and the one that releases it") {
    val group                                       = Coordinates(0, 0, 0)
    def unit(state: MachineUnit.State): MachineUnit = MachineUnit(group, 0, 8, state)
    val location = Fault.Location("kernel".unsafe[NonEmpty[String]], None, Some(0L), Some(group), None)
    val parking  = MachineEvent.Kind.Blocked(MachineUnit.Reason.Barrier("barrier 0"))
    val running  = MachineUnit.State.Running
    val waiting  = MachineUnit.State.Blocked(MachineUnit.Reason.Barrier("barrier 0"))
    val trace    = Trace(
      Vec(TraceStep(0L, location, unit(running), Vec.empty), TraceStep(2L, location, unit(running), Vec.empty)),
      Vec(
        MachineEvent(unit(waiting), parking, 3L, None),
        MachineEvent(unit(running), MachineEvent.Kind.Resumed, 7L, None),
        MachineEvent(unit(MachineUnit.State.Retired), MachineEvent.Kind.Retired, 9L, None)
      )
    )
    val rows = Timeline.layout(trace)
    assertEquals(rows.size, 1)
    assertEquals(rows.head.lanes, 8)
    assertEquals(
      rows.head.bands.head.segments,
      Vec(
        Segment(start = 0L, end = 3L, state = running),
        Segment(start = 3L, end = 7L, state = waiting),
        Segment(start = 7L, end = 9L, state = running)
      )
    )
  }

  test("the page's example program runs over several workgroups and writes one word per lane") {
    Using.resource(
      ProgramRun
        .load(ProgramListing.Image(Language.PTX, Vec.from(Examples.ptx.getBytes(StandardCharsets.UTF_8)), None))
        .toOption
        .get
    ) { program =>
      val result = ProgramRun
        .run(program, "kernel".unsafe[NonEmpty[String]], Dimensions(8, 1, 1), Dimensions(4, 1, 1))
        .toOption
        .get
      assertEquals(result.failure, None)
      assertEquals(Timeline.layout(result.trace).size, 4)
      assertEquals(result.allocations.size, 1)
      val allocation = result.allocations.head
      val words      = Vec.tabulate(32)(lane =>
        result.memory.getInt(allocation, (lane * 4).unsafe[Int >= 0])
      )
      assertEquals(words, Vec.tabulate(32)(identity))
    }
  }

  test("a step names the registers its instruction read and wrote") {
    val run                     = launch()
    def at(pc: Long): TraceStep =
      run.trace.steps.find(_.location.instruction.map(_.raw).contains(pc)).get
    assertEquals(at(0L).use.writes, Vec("%rd0"))
    assertEquals(at(0L).use.reads, Vec.empty[String])
    assertEquals(at(1L).use.writes, Vec("%r0"))
    assert(at(2L).use.isEmpty)
    assertEquals(at(3L).use.reads.toSet, Set("%rd0", "%r0"))
    assertEquals(at(3L).use.writes, Vec.empty[String])
  }

  test("a step carries the values the machine held, named as the routine declared them") {
    val run = launch()
    run.trace.steps.foreach { step =>
      assert(step.values.nonEmpty, s"issue ${step.issue} carries no values")
    }
    // The observer sees a machine before the instruction runs, so the step after `mov.u32 %r0, %tid.x` shows it.
    val move  = run.trace.steps.find(_.location.instruction.map(_.raw).contains(2L)).get
    val first = move.values.filter(_.name == "%r0")
    assertEquals(first.map(value => (value.lane, value.bits)).sortBy(_._1), Vec((0, 0L), (1, 1L), (2, 2L), (3, 3L)))
  }

  private def parked(event: MachineEvent): Boolean = event.kind match {
    case MachineEvent.Kind.Blocked(_) => true
    case _                            => false
  }
}
