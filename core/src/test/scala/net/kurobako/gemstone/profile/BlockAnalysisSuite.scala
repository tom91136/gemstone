package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class BlockAnalysisSuite extends Suite {

  private class State(
      val issued: Long,
      val text: String,
      override val schedule: Option[Schedule] = None,
      override val pipe: Option[Pipe] = None
  ) extends ArchitecturalState {
    def begins: Boolean   = true
    def unit: MachineUnit =
      MachineUnit(Coordinates(1, 2, 3), issued.toInt.unsafe[Int >= 0], 32.unsafe[Int >= 0], MachineUnit.State.Running)
    def subgroupSize: Int > 0                    = 32
    def eachLane(each: (Int >= 0) => Unit): Unit = (0 until 32).foreach(lane => each(lane.unsafe))
  }

  private val addresses = Vec[Long](0x10L, 0x18L, 0x20L, 0x28L, 0x30L)
  private val graph     = ControlFlowGraph(Vec(Some(Vec(1)), Some(Vec(2)), Some(Vec(3)), Some(Vec(2, 4)), None), 0)
  private val latencies = Vec(3, 5, 4, 2)
  private val uses      = Vec(
    RegisterUse(Vec.empty, Vec("r1")),
    RegisterUse(Vec("r1"), Vec.empty),
    RegisterUse(Vec("r0"), Vec("r1")),
    RegisterUse(Vec("r1"), Vec("r0"))
  )

  private def at(address: Long): Fault.Location =
    Fault.Location("k", Some("f"), Some(address.unsafe), Some(Coordinates(1, 2, 3)), Some(0.unsafe))

  private def estimated(index: Int): Option[Schedule] = Some(Schedule.Estimated(latencies(index).unsafe[Int >= 0]))

  private def record(indices: Vec[Int], schedule: Int => Option[Schedule] = estimated): ProfileStream = {
    val recorder = ProfileRecorder()
    recorder.controlFlow("k", graph, addresses)
    indices.zipWithIndex.foreach { (index, issued) =>
      val state = State(issued.toLong, s"op$index", schedule(index))
      assertEquals(recorder.instruction(at(addresses(index)), state), Right(()))
      recorder.registers(issued.toLong, uses(index))
    }
    recorder.stream
  }

  private val branchAddresses = Vec[Long](0x10L, 0x18L, 0x20L, 0x28L, 0x30L, 0x38L)
  private val branchGraph     = ControlFlowGraph(
    Vec(Some(Vec(1)), Some(Vec(2, 3)), Some(Vec(4)), Some(Vec(4)), Some(Vec(1, 5)), None),
    0
  )
  private val plainGraph = ControlFlowGraph(
    Vec(Some(Vec(1)), Some(Vec(3)), None, Some(Vec(4)), Some(Vec(1, 5)), None),
    0
  )
  private val nestedGraph = ControlFlowGraph(
    Vec(Some(Vec(1)), Some(Vec(2, 3)), Some(Vec(2, 4)), Some(Vec(4)), Some(Vec(1, 5)), None),
    0
  )
  private val branchLatencies = Vec(1, 1, 20, 1, 1, 1)
  private val branchUses      = Vec(
    RegisterUse(Vec.empty, Vec("r0")),
    RegisterUse(Vec("r0"), Vec("r1")),
    RegisterUse(Vec("r1"), Vec("r2")),
    RegisterUse(Vec("r1"), Vec("r2")),
    RegisterUse(Vec("r2"), Vec("r0")),
    RegisterUse(Vec("r0"), Vec.empty)
  )

  private def recordCounts(counts: Vec[(Int, Int)], cfg: ControlFlowGraph): ProfileStream = {
    val recorder = ProfileRecorder()
    recorder.controlFlow("k", cfg, branchAddresses)
    counts.flatMap { case (index, count) => Vec.fill(count)(index) }.zipWithIndex.foreach { (index, issued) =>
      val state = State(issued.toLong, s"op$index", estimatedAt(branchLatencies(index)))
      assertEquals(recorder.instruction(at(branchAddresses(index)), state), Right(()))
      recorder.registers(issued.toLong, branchUses(index))
    }
    recorder.stream
  }

  private def estimatedAt(cycles: Int): Option[Schedule] = Some(Schedule.Estimated(cycles.unsafe[Int >= 0]))

  private def report(stream: ProfileStream): ujson.Value =
    BlockAnalysis.of(stream, Architecture.sm89).fold(fail(_), analysis => BlockAnalysisReport.json(analysis.regions))

  private def region(json: ujson.Value, label: String): ujson.Value =
    json.arr.find(_("label").str == label).getOrElse(fail(s"no region $label"))

  private def loopCycles(stream: ProfileStream): Double = {
    val loop = region(report(stream), "k/loop@0x18")
    assertEquals(loop("status").str, "executed")
    loop("cyclesPerIteration").num
  }

  test("a loop and a straight block report the cycles of their simulated streams") {
    val json     = report(record(Vec(0, 1, 2, 3)))
    val straight = region(json, "k/block@0x10")
    assertEquals(straight("kind").str, "block")
    assertEquals(straight("status").str, "executed")
    assertEqualsDouble(straight("cyclesPerIteration").num, expected = 8.0, delta = 1e-9)
    assertEquals(straight("binding").str, "path")
    val loop = region(json, "k/loop@0x20")
    assertEquals(loop("kind").str, "loop")
    assertEquals(loop("status").str, "executed")
    assertEqualsDouble(loop("cyclesPerIteration").num, expected = 14.0, delta = 1e-9)
    assertEquals(loop("binding").str, "path")
  }

  test("a conditional block taken on some iterations costs between never and always") {
    val without = loopCycles(recordCounts(Vec(0 -> 4, 1 -> 4, 3 -> 4, 4 -> 4, 5 -> 1), plainGraph))
    val uneven  = loopCycles(recordCounts(Vec(0 -> 4, 1 -> 4, 2 -> 2, 3 -> 2, 4 -> 4, 5 -> 1), branchGraph))
    val always  = loopCycles(recordCounts(Vec(0 -> 4, 1 -> 4, 2 -> 4, 3 -> 4, 4 -> 4, 5 -> 1), branchGraph))
    assert(without < uneven, s"$without should be below $uneven")
    assert(uneven < always, s"$uneven should be below $always")
  }

  test("an inner loop in a region is costed from its recorded issue counts") {
    val loop = region(
      report(recordCounts(Vec(0 -> 4, 1 -> 4, 2 -> 8, 3 -> 2, 4 -> 4, 5 -> 1), nestedGraph)),
      "k/loop@0x18"
    )
    assertEquals(loop("status").str, "executed")
    assert(loop("cyclesPerIteration").num > 0.0)
  }

  test("a count above the region entry is costed") {
    val loop = region(
      report(recordCounts(Vec(0 -> 4, 1 -> 4, 2 -> 6, 3 -> 2, 4 -> 4, 5 -> 1), branchGraph)),
      "k/loop@0x18"
    )
    assertEquals(loop("status").str, "executed")
    assert(loop("cyclesPerIteration").num > 0.0)
  }

  test("a block that never executed is listed without timing") {
    val block = region(report(record(Vec(0, 1, 2, 3))), "k/block@0x30")
    assertEquals(block("status").str, "not executed")
    assertEquals(block.obj.get("cyclesPerIteration"), None)
  }

  test("an instruction without a profile entry withholds its block and names it") {
    val loop = region(report(record(Vec(0, 1, 2))), "k/loop@0x20")
    assertEquals(loop("status").str, "withheld")
    assert(loop("reason").str.contains("0x28"))
    assertEquals(loop.obj.get("cyclesPerIteration"), None)
  }

  test("an instruction without a schedule withholds its block and names it") {
    val unscheduled = record(Vec(0, 1, 2, 3), index => if (index == 2) None else estimated(index))
    val loop        = region(report(unscheduled), "k/loop@0x20")
    assertEquals(loop("status").str, "withheld")
    assert(loop("reason").str.contains("0x20"))
    assertEquals(loop.obj.get("cyclesPerIteration"), None)
  }

  test("a variable latency instruction without a table latency withholds its block and says so") {
    val variable = record(
      Vec(0, 1, 2, 3),
      index => if (index == 2) Some(Schedule.Encoded(3.unsafe[Int in 0 ~ 15], true)) else estimated(index)
    )
    val loop = region(report(variable), "k/loop@0x20")
    assertEquals(loop("status").str, "withheld")
    assert(loop("reason").str.contains("no table latency"))
  }

  test("estimated latencies reach the output as estimates") {
    val source = region(report(record(Vec(0, 1, 2, 3))), "k/block@0x10")("source")
    assertEquals(source("kind").str, "estimate")
    assertEquals(source("detail").str, "LLVM scheduling-model estimates")
  }

  test("an unknown block label is rejected and the error lists the known labels") {
    val analysis = BlockAnalysis.of(record(Vec(0, 1, 2, 3)), Architecture.sm89).fold(fail(_), identity)
    analysis.select(Some("k/block@0x99")) match {
      case Left(error) =>
        assert(error.contains("k/block@0x10"))
        assert(error.contains("k/loop@0x20"))
        assert(error.contains("k/block@0x30"))
      case Right(_) => fail("selected an unknown label")
    }
  }

  test("a region analysed on its own costs what the full analysis gives") {
    Vec(
      "uneven" -> recordCounts(Vec(0 -> 4, 1 -> 4, 2 -> 2, 3 -> 2, 4 -> 4, 5 -> 1), branchGraph),
      "even"   -> record(Vec(0, 1, 2, 3))
    ).foreach { (kind, stream) =>
      val full = BlockAnalysis.of(stream, Architecture.sm89).fold(fail(_), identity)
      full.regions.foreach { found =>
        val alone = BlockAnalysis.region(stream, Architecture.sm89, None, found.label)
        assertEquals(alone, Right(found), kind)
      }
    }
  }

  test("only the region asked for is analysed") {
    val stream = recordCounts(Vec(0 -> 4, 1 -> 4, 2 -> 2, 3 -> 2, 4 -> 4, 5 -> 1), branchGraph)
    val full   = BlockAnalysis.of(stream, Architecture.sm89).fold(fail(_), identity)
    assert(full.regions.size > 1, full.regions.map(_.label))
    assertEquals(
      BlockAnalysis.region(stream, Architecture.sm89, None, "k/loop@0x18").map(_.label),
      Right("k/loop@0x18")
    )
  }

  test("a stream without control flow graphs is rejected") {
    val recorder = ProfileRecorder()
    assertEquals(recorder.instruction(at(0x10L), State(0L, "op")), Right(()))
    assert(BlockAnalysis.of(recorder.stream, Architecture.sm89).isLeft)
  }

  test("a non-settling level stops an uneven region without simulating the later levels") {
    val counts  = Vec(4L, 4L, 2L, 2L, 4L, 1L)
    val members = Vec.from(counts.zipWithIndex.map((count, at) => BlockAnalysis.Member(0x10L + at * 8L, None, count)))
    val failed  = PipelineSimulator.Block(
      entries = Vec.empty,
      cyclesPerIteration = Double.NaN,
      binding = ProfileReport.Binding.Path,
      pressure = Vec.empty,
      source = Architecture.Source.Estimate("test"),
      steady = true,
      noise = 0.0
    )
    val calls  = new java.util.concurrent.atomic.AtomicInteger(0)
    val result = BlockAnalysis.simulated(
      Profiler.run(record(Vec(0, 1, 2, 3)), Architecture.sm89),
      BlockAnalysis.Kind.Loop,
      members,
      Vec.empty[PipelineSimulator.Op],
      None,
      Some { _ => calls.incrementAndGet(); failed }
    )
    assert(result.isLeft, result)
    assert(result.left.toOption.get.contains("did not settle"), result)
    assertEquals(calls.get(), 1)
  }
}
