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

  private def report(stream: ProfileStream): ujson.Value =
    BlockAnalysis.of(stream, Architecture.sm89).fold(fail(_), analysis => BlockAnalysisReport.json(analysis.regions))

  private def region(json: ujson.Value, label: String): ujson.Value =
    json.arr.find(_("label").str == label).getOrElse(fail(s"no region $label"))

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
    assertEqualsDouble(loop("cyclesPerIteration").num, expected = 6.0, delta = 1e-9)
    assertEquals(loop("binding").str, "path")
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

  test("a stream without control flow graphs is rejected") {
    val recorder = ProfileRecorder()
    assertEquals(recorder.instruction(at(0x10L), State(0L, "op")), Right(()))
    assert(BlockAnalysis.of(recorder.stream, Architecture.sm89).isLeft)
  }
}
