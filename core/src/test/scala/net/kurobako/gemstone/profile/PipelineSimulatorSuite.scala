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
    val body  = Vec.fill(8)(op(pipe = Some(Pipe.Mufu))) :+ op(stall = Some(21))
    val block = run(Architecture.gfx1036, body, warps = 2, Placement(Vec(2, 0, 0, 0)))
    assertEquals(block.entries.sortBy(_.issue).take(5).map(_.warp), Vec(0, 0, 0, 0, 0))
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
