package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchObserver.*

class ProfileCostSuite extends Suite {
  import Architecture.*
  import ProfileReport.*

  private val documented = Source.Documented("synthetic table")
  private val estimate   = Source.Estimate("synthetic basis")

  private val throughput = Throughput(
    issues = Sourced(1.0, documented),
    fma = Sourced(128.0, documented),
    alu = Sourced(64.0, documented),
    mufu = Sourced(16.0, documented),
    fp64 = Sourced(2.0, documented),
    tensor = Sourced(0.0, documented),
    loadStore = Sourced(4.0, documented),
    shared = Sourced(1.0, documented),
    loads = Some(Sourced(0.5, documented))
  )

  private val latency = Latency(
    shared = Sourced(24.0, documented),
    l1 = Some(Sourced(40.0, documented)),
    l2 = Some(Sourced(270.0, documented)),
    dram = Sourced(600.0, documented)
  )

  private val limits = Limits(
    subgroups = Sourced(8, documented),
    workgroups = Sourced(16, documented),
    workgroupCap = WorkgroupCap.AllWorkgroups,
    workgroupLanes = Sourced(1024, documented),
    partitions = Sourced(4, documented),
    partitionRegisters = Sourced(16384, documented),
    registerGranule = Sourced(256, documented),
    laneRegisters = Sourced(255, documented),
    workgroupRegisters = Some(Sourced(65536, documented)),
    shared = Sourced(102400, documented),
    workgroupShared = Sourced(49152, documented),
    reservedShared = Sourced(1024, documented),
    sharedGranule = Sourced(128, documented)
  )

  private val synthetic = Architecture(
    name = "synthetic",
    global = Global(Sourced(32, documented), Sourced(128, documented)),
    shared = Banks(Sourced(32, documented), Sourced(4, documented)),
    limits = limits,
    throughput = throughput,
    latency = latency
  )

  private val resident  = Occupancy(workgroups = 1, subgroups = 8, maxSubgroups = 48, bounds = Map.empty)
  private val encoded   = Some(Schedule.Encoded(4.unsafe[Int in 0 ~ 15], writeBarrier = false))
  private val estimated = Some(Schedule.Estimated(40.unsafe[Int >= 0]))

  private def instruction(
      text: String = "op",
      schedule: Option[Schedule] = None,
      pipe: Option[Pipe] = None,
      function: Option[String] = None,
      address: Option[Long] = None
  ): ProfileStream.Instruction =
    ProfileStream.Instruction(
      "kernel".unsafe[NonEmpty[String]],
      function.map(_.unsafe[NonEmpty[String]]),
      address.map(_.unsafe[Long >= 0]),
      text,
      RegisterUse.empty,
      schedule,
      pipe
    )

  private def access(space: Space, kind: AccessKind, sectors: Long = 0L, wavefronts: Long = 0L) =
    Map((space, kind) -> Profile.Memory.zero.copy(sectors = sectors, wavefronts = wavefronts))

  private def counters(
      issues: Long,
      lanes: Long = 0L,
      plannedCycles: Long = 0L,
      memory: Map[(Space, AccessKind), Profile.Memory] = Map.empty
  ): Profile.Counters =
    Profile.Counters.zero.copy(issues = issues, activeLanes = lanes, plannedCycles = plannedCycles, memory = memory)

  private def profile(entries: (ProfileStream.Instruction, Profile.Counters)*): Profile =
    Profile(synthetic, Some(resident), None, Profile.Counters.zero, Vec(entries*))

  private def only(profile: Profile): ProfileReport.Cost = ProfileReport.costs(profile).head

  private val separator = " at "

  private def placements(lines: Vector[String]): Vec[String] = Vec.from(lines.map(_.split(separator).last))

  test("issue binds an instruction that touches nothing else") {
    val cost = only(profile(instruction() -> counters(10)))
    assertEquals((cost.cycles, cost.bound, cost.binds, cost.path), (10.0, 10.0, Binding.Issue, None))
  }

  test("pipe binds when active lanes over the pipe rate exceed the issues") {
    val cost = only(profile(instruction(pipe = Some(Pipe.Fma)) -> counters(1, lanes = 1024)))
    assertEquals((cost.cycles, cost.binds), (8.0, Binding.Arithmetic(Pipe.Fma)))
  }

  test("load-store binds when sectors over the load-store rate exceed the issues") {
    val reads = access(Space.Global, AccessKind.Read, sectors = 64)
    val cost  = only(profile(instruction() -> counters(1, memory = reads)))
    assertEquals((cost.cycles, cost.binds), (16.0, Binding.LoadStore))
  }

  test("shared binds when wavefronts over the shared rate exceed the issues") {
    val writes = access(Space.Shared, AccessKind.Write, wavefronts = 12)
    val cost   = only(profile(instruction() -> counters(1, memory = writes)))
    assertEquals((cost.cycles, cost.binds), (12.0, Binding.Shared))
  }

  test("loads bind when load issues over the load rate exceed the other terms") {
    val reads = access(Space.Global, AccessKind.Read, sectors = 1)
    val cost  = only(profile(instruction() -> counters(10, memory = reads)))
    assertEquals((cost.cycles, cost.binds), (20.0, Binding.Loads))
  }

  test("a shared load counts to the loads term") {
    val reads = access(Space.Shared, AccessKind.Read, wavefronts = 1)
    val cost  = only(profile(instruction() -> counters(10, memory = reads)))
    assertEquals((cost.cycles, cost.binds), (20.0, Binding.Loads))
  }

  test("a store does not count to the loads term") {
    val writes = access(Space.Global, AccessKind.Write, sectors = 1)
    val cost   = only(profile(instruction() -> counters(10, memory = writes)))
    assertEquals((cost.cycles, cost.binds), (10.0, Binding.Issue))
  }

  test("a constant read charges no load, sector, or wavefront work") {
    val constant = access(Space.Constant, AccessKind.Read, sectors = 64, wavefronts = 40)
    val global   = access(Space.Global, AccessKind.Read, sectors = 64)
    val bank     = only(profile(instruction() -> counters(10, memory = constant)))
    val memory   = only(profile(instruction() -> counters(10, memory = global)))
    assertEquals((bank.cycles, bank.binds), (10.0, Binding.Issue))
    assertEquals((memory.cycles, memory.binds), (20.0, Binding.Loads))
  }

  test("a path longer than the bound binds") {
    val cost = only(profile(instruction(schedule = estimated) -> counters(10, plannedCycles = 400)))
    assertEquals((cost.cycles, cost.binds, cost.path), (50.0, Binding.Path, Some(50.0)))
  }

  test("a bound longer than the path binds and the path is kept") {
    val cost = only(profile(instruction(schedule = encoded) -> counters(10, plannedCycles = 40)))
    assertEquals((cost.cycles, cost.binds, cost.path), (10.0, Binding.Issue, Some(5.0)))
  }

  test("a tie between the path and the bound names the bound") {
    val cost = only(profile(instruction(schedule = encoded) -> counters(10, plannedCycles = 80)))
    assertEquals((cost.cycles, cost.binds, cost.path), (10.0, Binding.Issue, Some(10.0)))
  }

  test("an instruction without a schedule has no path") {
    val reads = access(Space.Global, AccessKind.Read, sectors = 1)
    val cost  = only(profile(instruction() -> counters(10, plannedCycles = 400, memory = reads)))
    assertEquals((cost.cycles, cost.binds, cost.path), (20.0, Binding.Loads, None))
  }

  test("a profile without occupancy has no path") {
    val reads = access(Space.Global, AccessKind.Read, sectors = 1)
    val entry = instruction(schedule = encoded) -> counters(10, plannedCycles = 400, memory = reads)
    val cost  = only(profile(entry).copy(occupancy = None))
    assertEquals((cost.cycles, cost.binds, cost.path), (20.0, Binding.Loads, None))
  }

  test("zero resident subgroups give no path") {
    val reads = access(Space.Global, AccessKind.Read, sectors = 1)
    val entry = instruction(schedule = encoded) -> counters(10, plannedCycles = 400, memory = reads)
    val cost  = only(profile(entry).copy(occupancy = Some(resident.copy(subgroups = 0))))
    assertEquals((cost.cycles, cost.binds, cost.path), (20.0, Binding.Loads, None))
  }

  test("a scheduled instruction with no path work has a zero path") {
    val cost = only(profile(instruction(schedule = encoded) -> counters(10)))
    assertEquals((cost.cycles, cost.binds, cost.path), (10.0, Binding.Issue, Some(0.0)))
  }

  test("a path with no work takes no limit source") {
    val architecture = synthetic.copy(limits = limits.copy(subgroups = Sourced(8, estimate)))
    val occupancy    = resident.copy(bounds = Map(Occupancy.Limiter.Subgroups -> 1))
    val stalled      = Some(Schedule.Encoded(0.unsafe[Int in 0 ~ 15], writeBarrier = false))
    val entry        = profile(instruction(schedule = stalled) -> counters(10))
    val cost         = only(entry.copy(architecture = architecture, occupancy = Some(occupancy)))
    assertEquals((cost.binds, cost.path, cost.source.kind), (Binding.Issue, Some(0.0), "documented"))
  }

  test("a pipe with a zero rate takes no part") {
    val cost = only(profile(instruction(pipe = Some(Pipe.Tensor)) -> counters(1, lanes = 1024)))
    assertEquals((cost.cycles, cost.binds), (1.0, Binding.Issue))
  }

  test("an absent loads rate takes no part") {
    val reads        = access(Space.Global, AccessKind.Read, sectors = 1)
    val architecture = synthetic.copy(throughput = throughput.copy(loads = None))
    val entry        = profile(instruction() -> counters(10, memory = reads))
    val cost         = only(entry.copy(architecture = architecture))
    assertEquals((cost.cycles, cost.binds), (10.0, Binding.Issue))
  }

  test("global latency takes the l1 tier when present") {
    val reads = access(Space.Global, AccessKind.Read, sectors = 1)
    val cost  = only(profile(instruction(schedule = encoded) -> counters(10, memory = reads)))
    assertEquals((cost.cycles, cost.binds, cost.floor), (50.0, Binding.Path, Some(Tier.L1)))
  }

  test("global latency takes the l2 tier when l1 is absent") {
    val reads        = access(Space.Global, AccessKind.Read, sectors = 1)
    val architecture = synthetic.copy(latency = latency.copy(l1 = None))
    val entry        = profile(instruction(schedule = encoded) -> counters(10, memory = reads))
    val cost         = only(entry.copy(architecture = architecture))
    assertEquals((cost.cycles, cost.binds, cost.floor), (337.5, Binding.Path, Some(Tier.L2)))
  }

  test("global latency takes the dram tier when l1 and l2 are absent") {
    val reads        = access(Space.Global, AccessKind.Read, sectors = 1)
    val architecture = synthetic.copy(latency = latency.copy(l1 = None, l2 = None))
    val entry        = profile(instruction(schedule = encoded) -> counters(10, memory = reads))
    val cost         = only(entry.copy(architecture = architecture))
    assertEquals((cost.cycles, cost.binds, cost.floor), (750.0, Binding.Path, Some(Tier.Dram)))
  }

  test("shared and global latency in one instruction take the larger") {
    val writes = access(Space.Shared, AccessKind.Write, wavefronts = 1)
    val reads  = access(Space.Global, AccessKind.Read, sectors = 1)
    val cost   = only(profile(instruction(schedule = encoded) -> counters(10, memory = writes ++ reads)))
    assertEquals((cost.cycles, cost.binds, cost.floor), (50.0, Binding.Path, Some(Tier.L1)))
  }

  test("a slower shared latency takes the path") {
    val writes       = access(Space.Shared, AccessKind.Write, wavefronts = 1)
    val reads        = access(Space.Global, AccessKind.Read, sectors = 1)
    val architecture = synthetic.copy(latency = latency.copy(shared = Sourced(500.0, documented)))
    val entry        = profile(instruction(schedule = encoded) -> counters(10, memory = writes ++ reads))
    val cost         = only(entry.copy(architecture = architecture))
    assertEquals((cost.cycles, cost.binds), (625.0, Binding.Path))
  }

  test("shared latency alone sets no floor") {
    val writes = access(Space.Shared, AccessKind.Write, wavefronts = 1)
    val cost   = only(profile(instruction(schedule = encoded) -> counters(10, memory = writes)))
    assertEquals((cost.cycles, cost.binds, cost.floor), (30.0, Binding.Path, None))
  }

  test("the load-store term carries the source of its rate") {
    val architecture = synthetic.copy(throughput = throughput.copy(loadStore = Sourced(4.0, estimate)))
    val writes       = access(Space.Global, AccessKind.Write, sectors = 8)
    val entry        = profile(instruction() -> counters(1, memory = writes))
    val cost         = only(entry.copy(architecture = architecture))
    assertEquals((cost.binds, cost.source.kind), (Binding.LoadStore, "estimate"))
  }

  test("a term without work leaves its source out") {
    val architecture = synthetic.copy(throughput = throughput.copy(loadStore = Sourced(4.0, estimate)))
    val entry        = profile(instruction(pipe = Some(Pipe.Alu)) -> counters(10, lanes = 64))
    val cost         = only(entry.copy(architecture = architecture))
    assertEquals((cost.binds, cost.source.kind), (Binding.Issue, "documented"))
  }

  test("an encoded schedule adds no estimate to the path") {
    val cost = only(profile(instruction(schedule = encoded) -> counters(10, plannedCycles = 400)))
    assertEquals((cost.binds, cost.source.kind), (Binding.Path, "documented"))
  }

  test("an estimated schedule adds an estimate to the path") {
    val cost = only(profile(instruction(schedule = estimated) -> counters(10, plannedCycles = 400)))
    assertEquals((cost.binds, cost.source.kind), (Binding.Path, "estimate"))
  }

  test("a path takes the estimated limits behind occupancy") {
    val architecture = synthetic.copy(limits = limits.copy(subgroups = Sourced(8, estimate)))
    val occupancy    = resident.copy(bounds = Map(Occupancy.Limiter.Subgroups -> 1))
    val entry        = profile(instruction(schedule = encoded) -> counters(10, plannedCycles = 400))
    val cost         = only(entry.copy(architecture = architecture, occupancy = Some(occupancy)))
    assertEquals((cost.binds, cost.source.kind), (Binding.Path, "estimate"))
  }

  test("a path takes no limit source when occupancy has no bounds") {
    val architecture = synthetic.copy(limits = limits.copy(subgroups = Sourced(8, estimate)))
    val entry        = profile(instruction(schedule = encoded) -> counters(10, plannedCycles = 400))
    val cost         = only(entry.copy(architecture = architecture))
    assertEquals((cost.binds, cost.source.kind), (Binding.Path, "documented"))
  }

  test("a path takes only the limits behind the binding limiter") {
    val architecture = synthetic.copy(limits = limits.copy(shared = Sourced(102400, estimate)))
    val occupancy    = resident.copy(bounds = Map(Occupancy.Limiter.Subgroups -> 1, Occupancy.Limiter.Shared -> 2))
    val entry        = profile(instruction(schedule = encoded) -> counters(10, plannedCycles = 400))
    val cost         = only(entry.copy(architecture = architecture, occupancy = Some(occupancy)))
    assertEquals((cost.binds, cost.source.kind), (Binding.Path, "documented"))
  }

  test("a path takes the limits of every tied limiter") {
    val published    = Source.Published("synthetic citation", "synthetic device")
    val architecture =
      synthetic.copy(limits = limits.copy(shared = Sourced(102400, published), workgroups = Sourced(16, estimate)))
    val occupancy = Occupancy(
      workgroups = 2,
      subgroups = 8,
      maxSubgroups = 48,
      bounds = Map(Occupancy.Limiter.Subgroups -> 2, Occupancy.Limiter.Shared -> 2, Occupancy.Limiter.Workgroups -> 5)
    )
    val entry = profile(instruction(schedule = encoded) -> counters(10, plannedCycles = 400))
    val cost  = only(entry.copy(architecture = architecture, occupancy = Some(occupancy)))
    assertEquals((cost.binds, cost.source.kind), (Binding.Path, "published"))
  }

  test("costs keep the instruction table order and skip instructions that never issue") {
    val costs = ProfileReport.costs(
      profile(
        instruction("first") -> counters(10),
        instruction("idle")  -> counters(0),
        instruction("last")  -> counters(1)
      )
    )
    assertEquals(costs.map(_.instruction), Vec(instruction("first"), instruction("last")))
  }

  test("the instruction line carries its cost") {
    val reads = access(Space.Global, AccessKind.Read, sectors = 1)
    val lines = jsonLines(profile(instruction(schedule = encoded) -> counters(10, memory = reads))).map(ujson.read(_))
    assertEquals(
      lines(1)("cost"),
      ujson.read(
        """{"cycles":50,"bound":20,"path":50,"binds":"path","floor":"L1","source":{"kind":"documented","detail":"synthetic table"}}"""
      )
    )
  }

  test("a cost without a path has null path and floor in json") {
    val lines = jsonLines(profile(instruction() -> counters(10))).map(ujson.read(_))
    assertEquals(
      lines(1)("cost"),
      ujson.read(
        """{"cycles":10,"bound":10,"path":null,"binds":"issue","floor":null,"source":{"kind":"documented","detail":"synthetic table"}}"""
      )
    )
  }

  test("the summary ranks instructions by cycles and names what binds each") {
    val report = profile(
      instruction("issue")                       -> counters(10),
      instruction("pipe", pipe = Some(Pipe.Fma)) -> counters(1, lanes = 1024),
      instruction("store")  -> counters(1, memory = access(Space.Global, AccessKind.Write, sectors = 64)),
      instruction("shared") -> counters(1, memory = access(Space.Shared, AccessKind.Write, wavefronts = 12)),
      instruction("loads")  -> counters(10, memory = access(Space.Global, AccessKind.Read, sectors = 1)),
      instruction("path", schedule = encoded) -> counters(10, plannedCycles = 400)
    )
    val section = summary(report, 10).linesIterator.toVector.dropWhile(_ != "top instructions by estimated cycles:")
    assertEquals(
      Vec.from(section.take(7)),
      Vec(
        "top instructions by estimated cycles:",
        "  50.0 cycles: binds path, path 50.0, bound 10.0 [documented] at path",
        "  20.0 cycles: binds loads, path unavailable, bound 20.0 [documented] at loads",
        "  16.0 cycles: binds load-store, path unavailable, bound 16.0 [documented] at store",
        "  12.0 cycles: binds shared, path unavailable, bound 12.0 [documented] at shared",
        "  10.0 cycles: binds issue, path unavailable, bound 10.0 [documented] at issue",
        "  8.0 cycles: binds fma pipe, path unavailable, bound 8.0 [documented] at pipe"
      )
    )
  }

  test("the summary breaks cost ties by function, then numeric address, then text") {
    val report = profile(
      instruction("add", function = Some("helper"), address = Some(0x200L)) -> counters(10),
      instruction("store", function = Some("main"), address = Some(0x100L)) -> counters(10),
      instruction("mov", function = Some("main"), address = Some(0xccL))    -> counters(10),
      instruction("load", function = Some("main"), address = Some(0xccL))   -> counters(10)
    )
    val section = summary(report, 10).linesIterator.toVector.dropWhile(_ != "top instructions by estimated cycles:")
    assertEquals(
      placements(section.slice(1, 5)),
      Vec("helper 0x200 add", "main 0xcc load", "main 0xcc mov", "main 0x100 store")
    )
  }

  test("the summary breaks planned cycle ties by function, then numeric address, then text") {
    val report = profile(
      instruction("add", function = Some("helper"), address = Some(0x200L)) -> counters(10, plannedCycles = 400),
      instruction("store", function = Some("main"), address = Some(0x100L)) -> counters(10, plannedCycles = 400),
      instruction("mov", function = Some("main"), address = Some(0xccL))    -> counters(10, plannedCycles = 400),
      instruction("load", function = Some("main"), address = Some(0xccL))   -> counters(10, plannedCycles = 400)
    )
    val section = summary(report, 10).linesIterator.toVector.dropWhile(_ != "top instructions by planned cycles:")
    assertEquals(
      placements(section.slice(1, 5)),
      Vec("helper 0x200 add", "main 0xcc load", "main 0xcc mov", "main 0x100 store")
    )
  }

  test("the summary breaks finding ties by function, then numeric address, then text") {
    val excess = access(Space.Global, AccessKind.Write, sectors = 64)
    val report = profile(
      instruction("store", function = Some("main"), address = Some(0x200L)) -> counters(1, memory = excess),
      instruction("store", function = Some("main"), address = Some(0x100L)) -> counters(1, memory = excess)
    )
    val lines = summary(report, 100).linesIterator.toVector.filter(_.contains("(excess sectors)"))
    assertEquals(placements(lines), Vec("main 0x100 store", "main 0x200 store"))
  }

  test("the summary states the tier a floor assumes") {
    val reads  = access(Space.Global, AccessKind.Read, sectors = 1)
    val report = profile(instruction(schedule = encoded) -> counters(10, memory = reads))
    val l1     = summary(report, 10)
    val l2     = summary(report.copy(architecture = synthetic.copy(latency = latency.copy(l1 = None))), 10)
    val dram   = summary(report.copy(architecture = synthetic.copy(latency = latency.copy(l1 = None, l2 = None))), 10)
    assert(l1.contains("path 50.0 (floor, assumes an L1 hit)"))
    assert(l2.contains("path 337.5 (floor, assumes an L2 hit)"))
    assert(dram.contains("path 750.0 (floor, assumes a DRAM access)"))
  }
}
