package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.profile.PipelineSimulator.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class PipelineSimulatorSuite extends Suite {
  import ProfileReport.*

  private val documented = Architecture.Source.Documented("sm_89 table")

  private def occupancy(subgroups: Int): Option[Occupancy] = Some(Occupancy(1, subgroups, 48, Map.empty))

  private def op(
      pipe: Option[Pipe] = None,
      reads: Vec[String] = Vec.empty,
      writes: Vec[String] = Vec.empty,
      latency: Int = 1,
      stall: Option[Int] = None,
      traffic: Traffic = Traffic.none
  ): Op = Op(
    pipe,
    32,
    reads,
    writes,
    Architecture.Sourced(latency, documented),
    1,
    stall.map(Architecture.Sourced(_, documented)),
    traffic
  )

  private def run(body: Vec[Op], subgroups: Int): Block =
    PipelineSimulator.simulate(Architecture.sm89, occupancy(subgroups), Stream.repeat(body))

  private def run(
      architecture: Architecture,
      body: Vec[Op],
      warps: Int,
      placement: Placement
  ): Block =
    PipelineSimulator.simulate(architecture, occupancy(warps), Stream.repeat(body), Some(placement))

  private def instruction(pipe: Option[Pipe]): ProfileStream.Instruction =
    ProfileStream.Instruction("kernel".unsafe[NonEmpty[String]], None, None, "op", RegisterUse.empty, None, pipe)

  test("dependency chain runs at the sum of its latencies") {
    val chain = Vec(
      op(reads = Vec("r0"), writes = Vec("r1"), latency = 4),
      op(reads = Vec("r1"), writes = Vec("r2"), latency = 4),
      op(reads = Vec("r2"), writes = Vec("r0"), latency = 4)
    )
    val block = run(chain, subgroups = 1)
    assert(block.steady)
    assertEqualsDouble(block.cyclesPerIteration, expected = 12.0, delta = 1e-9)
    assertEquals(block.binding, Binding.Path)
  }

  test("dependent op waits for the producer to retire") {
    val chain = Vec(
      op(reads = Vec("r0"), writes = Vec("r1"), latency = 4),
      op(reads = Vec("r1"), writes = Vec("r2"), latency = 4)
    )
    val first = run(chain, subgroups = 1).entries.filter(e => e.warp == 0 && e.iteration == 0L)
    assertEquals(
      first.map(e => (e.index, e.dispatch, e.issue, e.retire, e.stalls)),
      Vec((0, 0L, 0L, 4L, Map.empty[Stall, Long]), (1, 1L, 4L, 8L, Map(Stall.Dependency -> 3L)))
    )
  }

  test("issue stall delays the next issue and the result latency delays the dependent") {
    val ops = Vec(
      op(writes = Vec("r1"), latency = 6, stall = Some(2)),
      op(stall = Some(3)),
      op(reads = Vec("r1")),
      op()
    )
    val block = PipelineSimulator.simulate(Architecture.sm89, occupancy(1), Stream.once(ops))
    assertEquals(block.entries.map(_.issue), Vec(0L, 2L, 6L, 7L))
    assertEquals(block.entries(1).stalls, Map(Stall.Scheduled -> 1L))
    assertEquals(block.entries(2).stalls, Map(Stall.Scheduled -> 2L, Stall.Dependency -> 1L))
  }

  test("an sm_89 back-edge costs the redirect after its issue whatever its own stall") {
    def loop(held: Int, edge: Int) = Vec.fill(3)(op(pipe = Some(Pipe.Fma), stall = Some(1))) ++
      Vec(op(pipe = Some(Pipe.Fma), stall = Some(held)), op(stall = Some(edge)).copy(backEdge = true))
    assertEqualsDouble(run(loop(1, 1), 1).cyclesPerIteration, 14.0, 1e-9)
    assertEqualsDouble(run(loop(1, 5), 1).cyclesPerIteration, 14.0, 1e-9)
    assertEqualsDouble(run(loop(1, 15), 1).cyclesPerIteration, 14.0, 1e-9)
  }

  test("an sm_89 loop adds the holds of its body to the redirect") {
    def loop(held: Int) = Vec.fill(11)(op(pipe = Some(Pipe.Fma), stall = Some(1))) ++
      Vec(op(pipe = Some(Pipe.Fma), stall = Some(held)), op(stall = Some(5)).copy(backEdge = true))
    assertEqualsDouble(run(loop(1), 1).cyclesPerIteration, 22.0, 1e-9)
    assertEqualsDouble(run(loop(8), 1).cyclesPerIteration, 29.0, 1e-9)
  }

  test("pipe-bound stream runs at the pipe rate") {
    val block = run(Vec(op(pipe = Some(Pipe.Mufu)), op(pipe = Some(Pipe.Mufu))), subgroups = 48)
    assert(block.steady)
    assertEqualsDouble(block.cyclesPerIteration, expected = 4.0, delta = 1e-9)
    assertEquals(block.binding, Binding.Arithmetic(Pipe.Mufu))
  }

  test("a pipe shared across partitions gives a lone subgroup its whole rate") {
    assertEqualsDouble(run(Vec(op(pipe = Some(Pipe.Fp64))), subgroups = 1).cyclesPerIteration, 16.0, 1e-9)
    assertEqualsDouble(run(Vec(op(pipe = Some(Pipe.Fp64))), subgroups = 48).cyclesPerIteration, 16.0, 1e-9)
    assertEqualsDouble(run(Vec(op(pipe = Some(Pipe.Mufu))), subgroups = 1).cyclesPerIteration, 8.0, 1e-9)
  }

  test("memory traffic occupies the sector, shared, and load resources") {
    val cases = Vec(
      (Traffic(sectors = 8.0), 2.0, Binding.LoadStore),
      (Traffic(wavefronts = 3.0), 3.0, Binding.Shared),
      (Traffic(loads = 1.0), 2.0, Binding.Loads)
    )
    cases.foreach { (traffic, cycles, binding) =>
      val block = run(Vec(op(traffic = traffic)), subgroups = 48)
      assertEqualsDouble(block.cyclesPerIteration, cycles, 1e-9)
      assertEquals(block.binding, binding)
    }
  }

  test("a partition pipe binds on its own partition whatever the others do") {
    val stream = Vec(op(pipe = Some(Pipe.Mufu)), op(pipe = Some(Pipe.Mufu)))
    assertEquals(run(stream, subgroups = 1).binding, Binding.Arithmetic(Pipe.Mufu))
    assertEquals(run(stream, subgroups = 3).binding, Binding.Arithmetic(Pipe.Mufu))
    assertEquals(run(stream, subgroups = 4).binding, Binding.Arithmetic(Pipe.Mufu))
  }

  test("a lone gfx1036 wave binds its partition's FP64 pipe") {
    val block = run(
      Architecture.gfx1036,
      Vec(op(pipe = Some(Pipe.Fp64))),
      warps = 1,
      Placement(Vec(1, 0, 0, 0))
    )
    assertEqualsDouble(block.cyclesPerIteration, 16.0, 1e-9)
    assertEquals(block.binding, Binding.Arithmetic(Pipe.Fp64))
  }

  private val lone = Placement(Vec(1, 0, 0, 0))

  private def at(ops: Vec[Op], start: Long, bytes: Vec[Long]): Vec[Op] = {
    val starts = bytes.scanLeft(start)(_ + _)
    ops.zip(starts.zip(bytes)).map { case (op, (address, size)) =>
      op.copy(span = Some(Extent(address, address + size)))
    }
  }

  test("a loop within one fetch line pays the line redirect and a crossing loop the full one") {
    val body = Vec.fill(4)(op(pipe = Some(Pipe.Fma))) ++ Vec(
      op(writes = Vec("s0", "scc")),
      op(reads = Vec("s0"), writes = Vec("scc")),
      op(reads = Vec("scc")).copy(backEdge = true)
    )
    val sizes  = Vec(8L, 8L, 8L, 8L, 4L, 4L, 4L)
    val within = run(Architecture.gfx1036, at(body, 0x40L, sizes), warps = 1, lone)
    val across = run(Architecture.gfx1036, at(body, 0x58L, sizes), warps = 1, lone)
    assertEqualsDouble(within.cyclesPerIteration, 29.0, 1e-9)
    assertEqualsDouble(across.cyclesPerIteration, 41.0, 1e-9)
    assertEqualsDouble(run(Architecture.gfx1036, body, warps = 1, lone).cyclesPerIteration, 41.0, 1e-9)
  }

  test("an op without a pipe waits the scalar-after cycles behind an fma op") {
    val body   = Vec(op(pipe = Some(Pipe.Fma)), op(), op(), op().copy(backEdge = true))
    val issues = run(Architecture.gfx1036, body, warps = 1, lone).entries.filter(_.iteration == 0L).map(_.issue)
    assertEquals(issues, Vec(0L, 3L, 4L, 5L))
    val plain = Architecture.gfx1036.copy(scalarAfter = Map.empty)
    assertEquals(run(plain, body, warps = 1, lone).entries.filter(_.iteration == 0L).map(_.issue), Vec(0L, 1L, 2L, 3L))
  }

  test("a back-edge waits the condition latency after the op that wrote its condition") {
    val body   = Vec(op(writes = Vec("scc")), op(reads = Vec("scc")).copy(backEdge = true))
    val issues = run(Architecture.gfx1036, body, warps = 1, lone).entries.filter(_.iteration == 0L).map(_.issue)
    assertEquals(issues, Vec(0L, 11L))
    val plain = Architecture.gfx1036.copy(conditionLatency = None)
    assertEquals(run(plain, body, warps = 1, lone).entries.filter(_.iteration == 0L).map(_.issue), Vec(0L, 1L))
  }

  test("a queued pipe op holds the next pipe op but not a scalar op") {
    val body   = Vec(op(pipe = Some(Pipe.Fp64), latency = 20), op(), op(pipe = Some(Pipe.Fma)))
    val issues = run(Architecture.gfx1036, body, warps = 1, lone).entries.filter(_.iteration == 0L).map(_.issue)
    assertEquals(issues, Vec(0L, 1L, 15L))
  }

  test("the fp64 pipe takes its next op into its queue one cycle before it frees") {
    val body    = Vec(op(pipe = Some(Pipe.Fp64), latency = 20), op(pipe = Some(Pipe.Fp64), latency = 20))
    val entries = run(Architecture.gfx1036, body, warps = 1, lone).entries.filter(_.iteration == 0L)
    assertEquals(entries.map(_.issue), Vec(0L, 15L))
    assertEquals(entries.map(_.retire), Vec(20L, 36L))
    val plain = Architecture.gfx1036.copy(pipeQueue = Map.empty)
    assertEquals(run(plain, body, warps = 1, lone).entries.filter(_.iteration == 0L).map(_.issue), Vec(0L, 16L))
  }

  test("two warps on one partition contend for issue slots while two partitions do not") {
    val body     = Vec(op(), op(), op(), op())
    val together = PipelineSimulator.simulate(
      Architecture.sm89,
      occupancy(2),
      Stream.repeat(body),
      Some(Placement(Vec(2, 0, 0, 0)))
    )
    val apart = PipelineSimulator.simulate(
      Architecture.sm89,
      occupancy(2),
      Stream.repeat(body),
      Some(Placement(Vec(1, 1, 0, 0)))
    )
    def issuePressure(block: Block): Double =
      block.pressure.collectFirst { case (Binding.Issue, value) => value }.getOrElse(fail("no issue pressure"))
    assertEqualsDouble(together.cyclesPerIteration, expected = 4.0, delta = 1e-9)
    assertEqualsDouble(apart.cyclesPerIteration, expected = 2.0, delta = 1e-9)
    assertEqualsDouble(issuePressure(together), expected = 1.0, delta = 1e-9)
    assertEqualsDouble(issuePressure(apart), expected = 1.0, delta = 1e-9)
  }

  test("gfx1036 gives the oldest wave on a partition priority") {
    val body  = Vec.fill(8)(op(pipe = Some(Pipe.Fma))) :+ op(stall = Some(21))
    val block = run(Architecture.gfx1036, body, warps = 2, Placement(Vec(2, 0, 0, 0)))
    assertEquals(block.entries.sortBy(_.issue).take(5).map(_.warp), Vec(0, 0, 0, 0, 0))
  }

  test("a younger wave takes the pipe queue while the oldest is held") {
    val body   = Vec.fill(8)(op(pipe = Some(Pipe.Mufu))) :+ op(stall = Some(21))
    val block  = run(Architecture.gfx1036, body, warps = 2, Placement(Vec(2, 0, 0, 0)))
    val sorted = block.entries.sortBy(_.issue)
    assertEquals(sorted.take(5).map(_.warp), Vec(0, 1, 0, 0, 0))
  }

  test("gfx1036 oldest-first reports the slowest wave") {
    val body  = Vec.fill(4)(op())
    val block = PipelineSimulator.simulate(
      Architecture.gfx1036,
      occupancy(2),
      Stream.once(body),
      Some(Placement(Vec(2, 0, 0, 0)))
    )
    val last = block.entries.groupBy(_.warp).view.mapValues(_.map(_.retire).max).toMap
    assert(last(1) - last(0) >= 4, "the youngest wave must retire a whole body later")
    assertEqualsDouble(block.cyclesPerIteration * 2, last(1).toDouble, 1e-9)
  }

  test("issue-bound stream runs at the issue width") {
    val block = run(Vec(op(), op(), op()), subgroups = 48)
    assert(block.steady)
    assertEqualsDouble(block.cyclesPerIteration, expected = 0.75, delta = 1e-9)
    assertEquals(block.binding, Binding.Issue)
  }

  test("finite stream reports one pass without steady state") {
    val ops   = Vec(op(), op(reads = Vec("r1"), writes = Vec("r2"), latency = 4), op(reads = Vec("r2")))
    val block = PipelineSimulator.simulate(Architecture.sm89, occupancy(1), Stream.once(ops))
    assert(!block.steady)
    assertEquals(block.entries.filter(_.warp == 0).map(_.issue), Vec(0L, 1L, 5L))
  }

  test("waves that run a block once cost its steady state, with the entries of one pass") {
    val body = Vec(
      op(Some(Pipe.Fma), reads = Vec("r0"), writes = Vec("r1"), latency = 4),
      op(Some(Pipe.Fma), reads = Vec("r1"), writes = Vec("r2"), latency = 4),
      op(Some(Pipe.Fp64), reads = Vec("r2"), writes = Vec("r3"), latency = 8),
      op(reads = Vec("r3"), writes = Vec("r4"))
    )
    val once   = PipelineSimulator.simulate(Architecture.sm89, occupancy(16), Stream.once(body))
    val steady = PipelineSimulator.simulate(Architecture.sm89, occupancy(16), Stream.repeat(body))
    assertEqualsDouble(once.cyclesPerIteration, steady.cyclesPerIteration, 1e-9)
    assertEquals(once.binding, steady.binding)
    assert(!once.steady)
    assertEquals(once.entries.map(_.iteration).distinct, Vec(0L))
  }

  test("an instruction on a pipe that is not the busiest never lowers the cost of a block of many waves") {
    val body = Vec.fill(6)(op(Some(Pipe.Fp64), reads = Vec("r0"), writes = Vec("r1"), latency = 8))
    val cost = (ops: Vec[Op]) =>
      PipelineSimulator.simulate(Architecture.sm89, occupancy(16), Stream.once(ops)).cyclesPerIteration
    val base = cost(body)
    (0 to body.size).foreach { at =>
      val grown = cost(body.take(at) ++ Vec(op()) ++ body.drop(at))
      assert(grown >= base, s"an instruction at $at costs $grown against $base")
    }
  }

  test("block executed once agrees with ProfileReport.costs per instruction") {
    val kinds   = Vec(None, Some(Pipe.Fma), Some(Pipe.Alu), Some(Pipe.Mufu), Some(Pipe.Fp64))
    val entries = kinds.map(pipe => (instruction(pipe), Profile.Counters.zero.copy(issues = 1L, activeLanes = 32L)))
    val costs   = ProfileReport.costs(Profile(Architecture.sm89, occupancy(48), None, Profile.Counters.zero, entries))
    assertEquals(costs.size, kinds.size)
    costs.foreach { cost =>
      val block = run(Vec(op(pipe = cost.instruction.pipe)), subgroups = 48)
      assertEqualsDouble(block.cyclesPerIteration, cost.bound, 1e-9)
      assertEquals(block.binding, cost.binds)
    }
  }
}
