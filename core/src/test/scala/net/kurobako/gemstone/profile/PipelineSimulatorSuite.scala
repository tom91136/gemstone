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
      stall: Option[Int] = None
  ): Op = Op(
    pipe,
    32,
    reads,
    writes,
    Architecture.Sourced(latency, documented),
    1,
    stall.map(Architecture.Sourced(_, documented))
  )

  private def run(body: Vec[Op], subgroups: Int): Block =
    PipelineSimulator.simulate(Architecture.sm89, occupancy(subgroups), Stream.repeat(body))

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
