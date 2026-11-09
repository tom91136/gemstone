package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import net.kurobako.gemstone.vm.LaunchObserver.*

class ProfilerSuite extends Suite {

  private class State(
      val issued: Long,
      lanes: Vec[Int],
      val text: String,
      val begins: Boolean,
      workgroup: Coordinates,
      override val schedule: Option[Schedule]
  ) extends ArchitecturalState {
    def unit: MachineUnit     = MachineUnit(workgroup, 0, lanes.size.unsafe[Int >= 0], MachineUnit.State.Running)
    def subgroupSize: Int > 0 = 32
    def eachLane(each: (Int >= 0) => Unit): Unit = lanes.foreach(lane => each(lane.unsafe[Int >= 0]))
  }

  /** Issues instructions in order; each is a pc, its lanes, and an access per lane computed from the lane. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Launch(architecture: Architecture = Architecture.sm89) {
    val recorder: ProfileRecorder = ProfileRecorder()
    private var issued            = 0L

    def issue(
        pc: Long,
        lanes: Vec[Int],
        begins: Boolean = true,
        use: RegisterUse = RegisterUse.empty,
        workgroup: Coordinates = Coordinates.origin,
        schedule: Option[Schedule] = None
    )(
        access: Int => Option[(Space, AccessKind, Long, Int)]
    ): Unit = {
      val at = Fault.Location("k", None, Some(pc.unsafe), None, None)
      assertEquals(recorder.instruction(at, State(issued, lanes, s"op$pc", begins, workgroup, schedule)), Right(()))
      recorder.registers(issued, use)
      issued += 1L
      lanes.foreach(lane =>
        access(lane).foreach((space, kind, address, bytes) =>
          recorder.access(lane.unsafe, space, kind, address, bytes.unsafe)
        )
      )
    }

    def retire(workgroup: Coordinates): Unit = recorder.transition(
      MachineEvent(
        MachineUnit(workgroup, 0, 32, MachineUnit.State.Retired),
        MachineEvent.Kind.Retired,
        issued,
        None
      )
    )

    def profile: Profile = Profiler.run(recorder.stream, architecture)

    def measured: Profiler.Measured = Profiler.measure(recorder.stream, architecture)
  }

  private val warp = Vec.tabulate(32)(identity)

  private def memory(profile: Profile, space: Space, kind: AccessKind): Profile.Memory =
    profile.total.memory(space -> kind)

  /** Extra cycles per access over the conflict-free and single-line cases, measured with s_memtime on one wave32 of a
    * gfx1036 (Ryzen 9 9950X3D iGPU, ROCm 7.1.1, `profile_timing.hip` in the Nextcloud probe kit).
    */
  private val rdna2Lds    = Vec(1 -> 0L, 2 -> 1L, 4 -> 3L, 8 -> 7L, 16 -> 15L, 32 -> 31L, 33 -> 0L, 64 -> 31L)
  private val rdna2Global = Vec(1 -> 0L, 2 -> 1L, 4 -> 3L, 8 -> 8L, 16 -> 18L, 32 -> 38L)

  test("hardware: gfx1036 LDS reads cost one cycle per conflict wavefront") {
    rdna2Lds.foreach { (stride, extra) =>
      val launch = Launch(Architecture.gfx1036)
      launch.issue(0L, warp)(lane => Some((Space.Shared, AccessKind.Read, 4L * stride * lane, 4)))
      assertEquals((stride, memory(launch.profile, Space.Shared, AccessKind.Read).conflicts), (stride, extra))
    }
  }

  test("hardware: gfx1036 global reads cost by 128-byte lines touched") {
    val lines = rdna2Global.map { (stride, _) =>
      val launch = Launch(Architecture.gfx1036)
      launch.issue(0L, warp)(lane => Some((Space.Global, AccessKind.Read, 0x10000L + 4L * stride * lane, 4)))
      memory(launch.profile, Space.Global, AccessKind.Read).sectors
    }
    assertEquals(lines, Vec(1L, 2L, 4L, 8L, 16L, 32L))
    val extra = rdna2Global.map(_._2)
    assert(extra.zip(extra.drop(1)).forall(_ < _), extra)
    assert(extra(5).toDouble / extra(4) > 1.8, extra)
  }

  test("a coalesced warp load of 4-byte words touches 4 sectors in one line") {
    val launch = Launch()
    launch.issue(0L, warp)(lane => Some((Space.Global, AccessKind.Read, 0x1000L + 4L * lane, 4)))
    assertEquals(
      memory(launch.profile, Space.Global, AccessKind.Read),
      Profile.Memory.zero.copy(requests = 1L, bytes = 128L, sectors = 4L, idealSectors = 4L, lines = 1L)
    )
  }

  test("a warp load strided by a line touches a sector in each of 32 lines") {
    val launch = Launch()
    launch.issue(0L, warp)(lane => Some((Space.Global, AccessKind.Read, 0x1000L + 128L * lane, 4)))
    val load = memory(launch.profile, Space.Global, AccessKind.Read)
    assertEquals((load.sectors, load.idealSectors, load.lines), (32L, 4L, 32L))
  }

  test("a misaligned coalesced load touches one sector more than it needs") {
    val launch = Launch()
    launch.issue(0L, warp)(lane => Some((Space.Global, AccessKind.Read, 0x1004L + 4L * lane, 4)))
    val load = memory(launch.profile, Space.Global, AccessKind.Read)
    assertEquals((load.sectors, load.idealSectors, load.lines), (5L, 4L, 2L))
  }

  test("a shared store strided by 32 words conflicts on one bank 32 ways") {
    val launch = Launch()
    launch.issue(0L, warp)(lane => Some((Space.Shared, AccessKind.Write, 128L * lane, 4)))
    val store = memory(launch.profile, Space.Shared, AccessKind.Write)
    assertEquals((store.wavefronts, store.idealWavefronts, store.conflicts), (32L, 1L, 31L))
  }

  test("lanes reading one shared word share it, and contiguous 8-byte reads take two conflict-free wavefronts") {
    val launch = Launch()
    launch.issue(0L, warp)(_ => Some((Space.Shared, AccessKind.Read, 64L, 4)))
    launch.issue(1L, warp)(lane => Some((Space.Shared, AccessKind.Read, 8L * lane, 8)))
    val profile = launch.profile
    assertEquals(
      profile.instructions.map((_, counters) => counters.memory(Space.Shared -> AccessKind.Read).wavefronts),
      Vec(1L, 2L)
    )
    assertEquals(memory(profile, Space.Shared, AccessKind.Read).conflicts, 0L)
  }

  test("an instruction the machine runs as several issues counts once, with every part's accesses") {
    val launch = Launch()
    launch.issue(0L, warp)(lane => Some((Space.Global, AccessKind.Read, 0x1000L + 4L * lane, 4)))
    launch.issue(0L, warp, begins = false)(lane => Some((Space.Global, AccessKind.Read, 0x2000L + 4L * lane, 4)))
    launch.issue(0L, warp)(_ => None)
    val profile = launch.profile
    val load    = memory(profile, Space.Global, AccessKind.Read)
    assertEquals((profile.total.issues, profile.total.activeLanes, load.requests, load.sectors), (2L, 64L, 2L, 8L))
    assertEquals(profile.total.divergent, 0L)
  }

  test("an issue the machine adds without a source instruction counts its traffic but no issue") {
    val launch = Launch()
    launch.issue(0L, warp, begins = false)(lane => Some((Space.Global, AccessKind.Read, 0x1000L + 4L * lane, 4)))
    launch.issue(1L, warp)(_ => None)
    val profile = launch.profile
    val load    = memory(profile, Space.Global, AccessKind.Read)
    assertEquals((profile.total.issues, profile.total.activeLanes, load.requests, load.sectors), (1L, 32L, 1L, 4L))
  }

  test("a branch whose lanes part is divergent, and the issue where they meet again reconverges") {
    val launch = Launch()
    launch.issue(0L, warp)(_ => None)
    launch.issue(1L, Vec.tabulate(16)(identity))(_ => None)
    launch.issue(2L, Vec.tabulate(16)(_ + 16))(_ => None)
    launch.issue(3L, warp)(_ => None)
    val profile = launch.profile
    assertEquals(profile.instructions.map((_, counters) => counters.divergent), Vec(1L, 0L, 0L, 0L))
    assertEquals(profile.instructions.map((_, counters) => counters.reconverging), Vec(0L, 0L, 0L, 1L))
    assertEquals(profile.total.activeLanes, 96L)
    assertEquals(profile.total.subgroupLanes, 128L)
  }

  test("a machine that issues one lane at a time groups a subgroup's lanes by their execution of an instruction") {
    val launch = Launch()
    warp.foreach { lane =>
      (0 until 2).foreach(iteration =>
        launch.issue(0L, Vec(lane))(_ =>
          Some((Space.Global, AccessKind.Read, 0x1000L + 128L * iteration + 4L * lane, 4))
        )
      )
    }
    val load = memory(launch.profile, Space.Global, AccessKind.Read)
    assertEquals((load.requests, load.sectors, launch.profile.total.issues), (2L, 8L, 2L))
  }

  test("the report lists the launch, then each instruction that issued") {
    val launch = Launch()
    launch.issue(0L, warp)(lane => Some((Space.Global, AccessKind.Read, 0x1000L + 128L * lane, 4)))
    val lines = ProfileReport.jsonLines(launch.profile).map(ujson.read(_))
    assertEquals(lines.map(_("record").str), Vec("launch", "instruction"))
    assertEquals(lines(1)("memory")(0)("sectors").num, 32.0)
    assert(ProfileReport.summary(launch.profile, 3).contains("28 sectors (excess sectors) [documented] at 0x0 op0"))
  }

  test("register pressure counts 32-bit units, a 64-bit register as two and a predicate as none") {
    val launch = Launch()
    launch.issue(0L, warp, use = RegisterUse(Vec.empty, Vec("%rd1", "%p1"), Map("%rd1" -> 2, "%p1" -> 0)))(_ => None)
    launch.issue(1L, warp, use = RegisterUse(Vec("%rd1"), Vec("%r2"), Map("%rd1" -> 2)))(_ => None)
    launch.issue(2L, warp, use = RegisterUse(Vec("%r2"), Vec.empty))(_ => None)
    assertEquals(launch.profile.pressure, Some(Profile.Pressure(3, 2.0)))
  }

  test("planned cycles add the stall of each issue the machine begins and nothing for the rest") {
    val launch = Launch()
    val stall  = Some(Schedule.Encoded(4.unsafe[Int in 0 ~ 15], writeBarrier = true))
    launch.issue(0L, warp, schedule = stall)(_ => None)
    launch.issue(0L, warp, schedule = stall)(_ => None)
    val tail = Some(Schedule.Encoded(7.unsafe[Int in 0 ~ 15], writeBarrier = false))
    launch.issue(1L, warp, begins = false, schedule = tail)(_ => None)
    assertEquals(launch.profile.total.plannedCycles, 8L)
  }

  test("the micro-ops of one instruction count its stall once") {
    val launch = Launch()
    val stall  = Some(Schedule.Encoded(5.unsafe[Int in 0 ~ 15], writeBarrier = false))
    launch.issue(1L, warp, schedule = stall)(_ => None)
    launch.issue(2L, warp, begins = false, schedule = stall)(_ => None)
    launch.issue(3L, warp, begins = false, schedule = stall)(_ => None)
    assertEquals(launch.profile.total.plannedCycles, 5L)
  }

  test("planned cycles reach the report only for a profile whose instructions carry a schedule") {
    val unscheduled = Launch()
    unscheduled.issue(0L, warp)(_ => None)
    assert(ProfileReport.jsonLines(unscheduled.profile).map(ujson.read(_)).forall(!_.obj.contains("plannedCycles")))

    val scheduled = Launch()
    val stall     = Some(Schedule.Encoded(3.unsafe[Int in 0 ~ 15], writeBarrier = false))
    scheduled.issue(0L, warp, schedule = stall)(_ => None)
    val line = ProfileReport.jsonLines(scheduled.profile).map(ujson.read(_)).head
    assertEquals(line("plannedCycles").num, 3.0)
    assertEquals(line("plannedCyclesSource").str, "compiler encoding")
  }

  test("a long loop holds a bounded number of subgroup issues open") {
    val launch = Launch()
    (0 until 3000).foreach { iteration =>
      launch.issue(iteration % 2L, warp)(lane => Some((Space.Global, AccessKind.Read, 0x1000L + 4L * lane, 4)))
    }
    val Profiler.Measured(profile, open) = launch.measured
    assertEquals(profile.total.issues, 3000L)
    assertEquals(memory(profile, Space.Global, AccessKind.Read).requests, 3000L)
    assert(open <= 3, s"$open subgroup issues open at once")
  }

  test("workgroups that retire release their subgroup issues") {
    val launch = Launch()
    (0 until 500).foreach { index =>
      val workgroup = Coordinates(index.unsafe, 0, 0)
      launch.issue(0L, warp, workgroup = workgroup)(_ => None)
      launch.issue(1L, warp, workgroup = workgroup)(_ => None)
      launch.retire(workgroup)
    }
    val Profiler.Measured(profile, open) = launch.measured
    assertEquals(profile.total.issues, 1000L)
    assert(open <= 3, s"$open subgroup issues open at once")
  }

  test("findings rank in estimated cycles across kinds and keep their counts") {
    val launch = Launch()
    launch.issue(0L, warp)(lane => Some((Space.Global, AccessKind.Read, 0x1000L + 128L * lane, 4)))
    launch.issue(1L, warp)(lane => Some((Space.Shared, AccessKind.Write, 128L * lane, 4)))
    launch.issue(2L, Vec.tabulate(16)(identity))(_ => None)
    val found = ProfileReport.findings(launch.profile)
    assertEquals(
      found.map(finding => (finding.what, finding.count, finding.unit, finding.cycles)),
      Vec(
        ("bank-conflict wavefronts", 31L, "wavefronts", 31.0),
        ("excess sectors", 28L, "sectors", 7.0),
        ("idle lanes", 16L, "lanes", 0.125)
      )
    )
    val line = ProfileReport.jsonLines(launch.profile).map(ujson.read(_)).head
    assertEquals(line("schema").num, 3.0)
    assertEquals(line("findings")(0)("cycles").num, 31.0)
    assertEquals(line("findings")(0)("source")("kind").str, "documented")
  }

  test("findings name the weakest source of the values they rest on") {
    def sources(architecture: Architecture): Vec[(String, String)] = {
      val launch = Launch(architecture)
      launch.issue(0L, warp)(lane => Some((Space.Global, AccessKind.Read, 0x1000L + 128L * lane, 4)))
      launch.issue(1L, warp)(lane => Some((Space.Shared, AccessKind.Write, 128L * lane, 4)))
      launch.issue(2L, Vec.tabulate(16)(identity))(_ => None)
      ProfileReport.findings(launch.profile).map(finding => (finding.what, finding.source.kind))
    }
    assertEquals(
      sources(Architecture.sm89),
      Vec(
        ("bank-conflict wavefronts", "documented"),
        ("excess sectors", "documented"),
        ("idle lanes", "probed")
      )
    )
    assertEquals(sources(Architecture.gfx1036).map(_._2).distinct, Vec("estimate"))
  }
}
