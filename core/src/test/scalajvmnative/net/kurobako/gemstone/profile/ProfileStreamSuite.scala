package net.kurobako.gemstone.profile

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import net.kurobako.gemstone.vm.LaunchObserver.*
import scala.collection.mutable

class ProfileStreamSuite extends Suite {

  private class State(
      val issued: Long,
      ordinal: Int >= 0,
      lanes: Vec[Int >= 0],
      val text: String,
      val begins: Boolean = true,
      override val schedule: Option[Schedule] = None,
      override val pipe: Option[Pipe] = None
  ) extends ArchitecturalState {
    def unit: MachineUnit =
      MachineUnit(Coordinates(1, 2, 3), ordinal, lanes.size.unsafe[Int >= 0], MachineUnit.State.Running)
    def subgroupSize: Int > 0                    = 32
    def eachLane(each: (Int >= 0) => Unit): Unit = lanes.foreach(each)
  }

  private def at(address: Long, lane: Int): Fault.Location =
    Fault.Location("k", Some("f"), Some(address.unsafe), Some(Coordinates(1, 2, 3)), Some(lane.unsafe))

  private class Collected extends ProfileStream.Visitor {
    val seen: mutable.ArrayBuffer[Any]                   = mutable.ArrayBuffer.empty
    override def issue(issue: ProfileStream.Issue): Unit = seen += issue
    override def access(lane: Int >= 0, space: Space, kind: AccessKind, address: Long, bytes: Int >= 0): Unit =
      seen += ((lane, space, kind, address, bytes))
    override def asynchronous(lane: Int >= 0, kind: AsyncKind, target: Long, bytes: Long): Unit =
      seen += ((lane, kind, target, bytes))
    override def transition(event: MachineEvent): Unit = seen += event
  }

  private def replay(stream: ProfileStream): Vec[Any] = {
    val collected = Collected()
    stream.foreach(collected)
    Vec.from(collected.seen)
  }

  test("a recorded launch replays its issues, accesses, asynchronous steps and transitions in order") {
    val recorder = ProfileRecorder()
    val lanes    = Vec[Int >= 0](0, 1, 63, 64, 130)
    assertEquals(recorder.instruction(at(0x10L, 0), State(0L, 0, lanes, "ld.global.u32 %r1, [%rd1];")), Right(()))
    val use = RegisterUse(Vec("%rd1"), Vec("%r1", "%p1"), Map("%rd1" -> 2, "%p1" -> 0))
    recorder.registers(0L, use)
    recorder.access(0, Space.Global, AccessKind.Read, 0x1000L, 4)
    recorder.access(130, Space.Global, AccessKind.Read, 0x1208L, 4)
    recorder.asynchronous(1, AsyncKind.Transfer, 0x40L, 128L)
    val blocked = MachineEvent(
      MachineUnit(Coordinates(1, 2, 3), 0, 5, MachineUnit.State.Blocked(MachineUnit.Reason.Barrier("0"))),
      MachineEvent.Kind.Blocked(MachineUnit.Reason.Barrier("0")),
      0L,
      Some(at(0x10L, 3))
    )
    recorder.transition(blocked)
    assertEquals(recorder.instruction(at(0x18L, 7), State(1L, 1, Vec(7), "bar.sync 0;", begins = false)), Right(()))
    recorder.access(7, Space.Shared, AccessKind.Atomic, 0x80L, 8)

    val stream = recorder.stream
    assertEquals(stream.instructions.map(_.text), Vec("ld.global.u32 %r1, [%rd1];", "bar.sync 0;"))
    val expected = Vec[Any](
      ProfileStream.Issue(0L, Coordinates(1, 2, 3), 0, 0, 32, lanes, true),
      (0, Space.Global, AccessKind.Read, 0x1000L, 4),
      (130, Space.Global, AccessKind.Read, 0x1208L, 4),
      (1, AsyncKind.Transfer, 0x40L, 128L),
      blocked.copy(at = Some(at(0x10L, 3).copy(workgroup = None, lane = None))),
      ProfileStream.Issue(1L, Coordinates(1, 2, 3), 1, 1, 32, Vec(7), false),
      (7, Space.Shared, AccessKind.Atomic, 0x80L, 8)
    )
    assertEquals(replay(stream), expected)

    val bytes = ByteArrayOutputStream()
    stream.write(DataOutputStream(bytes))
    val read = ProfileStream.read(DataInputStream(ByteArrayInputStream(bytes.toByteArray)))
    assertEquals(read.map(replay(_)), Right(expected))
    assertEquals(read.map(_.instructions), Right(stream.instructions))
    assertEquals(read.map(_.instructions(0).use), Right(use))
  }

  test("a kernel reported lost keeps no control flow graph, even when reported again") {
    val recorder = ProfileRecorder()
    val graph    = ControlFlowGraph(Vec(Some(Vec(1)), Some(Vec(1, 2)), Some(Vec(3)), None), 0)
    val offsets  = Vec[Long](0x10L, 0x18L, 0x20L, 0x28L)
    recorder.controlFlow("k", graph, offsets)
    recorder.controlFlowLost("k")
    recorder.controlFlow("k", graph, offsets)
    assert(recorder.stream.graphs.isEmpty)
  }

  test("the control flow of each launched kernel survives a round trip") {
    val recorder = ProfileRecorder()
    val graph    = ControlFlowGraph(Vec(Some(Vec(1)), Some(Vec(1, 2)), Some(Vec(3)), None), 0)
    val offsets  = Vec[Long](0x10L, 0x18L, 0x20L, 0x28L)
    recorder.controlFlow("k", graph, offsets)
    val bytes = ByteArrayOutputStream()
    recorder.stream.write(DataOutputStream(bytes))
    val read = ProfileStream.read(DataInputStream(ByteArrayInputStream(bytes.toByteArray)))
    assertEquals(
      read.map(_.graphs.map(g => (g.kernel, g.graph.blocks, g.graph.entry, g.graph.loops, g.offsets))),
      Right(Vec(("k", graph.blocks, graph.entry, graph.loops, offsets)))
    )
  }

  test("the compiler schedule of each instruction survives a round trip") {
    val recorder = ProfileRecorder()
    val lanes    = Vec[Int >= 0](0)
    val stalled  = Schedule.Encoded(4.unsafe[Int in 0 ~ 15], writeBarrier = true)
    val modelled = Schedule.Estimated(9.unsafe[Int >= 0])
    assertEquals(recorder.instruction(at(0x10L, 0), State(0L, 0, lanes, "ldg", schedule = Some(stalled))), Right(()))
    assertEquals(recorder.instruction(at(0x18L, 0), State(1L, 0, lanes, "fadd")), Right(()))
    assertEquals(recorder.instruction(at(0x20L, 0), State(2L, 0, lanes, "v_rcp", schedule = Some(modelled))), Right(()))
    val bytes = ByteArrayOutputStream()
    recorder.stream.write(DataOutputStream(bytes))
    val read = ProfileStream.read(DataInputStream(ByteArrayInputStream(bytes.toByteArray)))
    assertEquals(read.map(_.instructions.map(_.schedule)), Right(Vec(Some(stalled), None, Some(modelled))))
  }

  test("the pipe of each instruction survives a round trip") {
    val recorder = ProfileRecorder()
    val lanes    = Vec[Int >= 0](0)
    val pipes    = List(Some(Pipe.Fma), None, Some(Pipe.Alu), Some(Pipe.Mufu), Some(Pipe.Fp64), Some(Pipe.Tensor))
    pipes.zipWithIndex.foreach { (pipe, index) =>
      val state = State(index.toLong, 0, lanes, s"op$index", pipe = pipe)
      assertEquals(recorder.instruction(at(0x10L + 8L * index, 0), state), Right(()))
    }
    val bytes = ByteArrayOutputStream()
    recorder.stream.write(DataOutputStream(bytes))
    val read = ProfileStream.read(DataInputStream(ByteArrayInputStream(bytes.toByteArray)))
    assertEquals(read.map(_.instructions.map(_.pipe)), Right(Vec.from(pipes)))
  }

  test("the pipe of an instruction is the first non-empty pipe reported at its location") {
    val recorder = ProfileRecorder()
    val lanes    = Vec[Int >= 0](0)
    val pipes    = List(None, Some(Pipe.Alu), Some(Pipe.Fma))
    pipes.zipWithIndex.foreach { (pipe, index) =>
      val state = State(index.toLong, 0, lanes, "iadd3", pipe = pipe)
      assertEquals(recorder.instruction(at(0x10L, 0), state), Right(()))
    }
    assertEquals(recorder.stream.instructions.map(_.pipe), Vec[Option[Pipe]](Some(Pipe.Alu)))
  }

  test("every asynchronous kind replays with its target and bytes after a round trip") {
    val recorder = ProfileRecorder()
    val steps    = AsyncKind.values.toVector.zipWithIndex.map { (kind, index) =>
      (index.unsafe[Int >= 0], kind, index * 64L, index * 8L + 1L)
    }
    steps.foreach((lane, kind, target, bytes) => recorder.asynchronous(lane, kind, target, bytes))
    val expected = Vec.from(steps.map(step => step: Any))
    assertEquals(replay(recorder.stream), expected)

    val bytes = ByteArrayOutputStream()
    recorder.stream.write(DataOutputStream(bytes))
    val read = ProfileStream.read(DataInputStream(ByteArrayInputStream(bytes.toByteArray)))
    assertEquals(read.map(replay(_)), Right(expected))
  }

  test("issues recorded on several threads merge by issue ordinal") {
    val recorder = ProfileRecorder()
    val threads  = Vec.tabulate(4) { thread =>
      Thread { () =>
        (0 until 50).foreach { step =>
          val issued = (step * 4 + thread).toLong
          val _ = recorder.instruction(at(thread.toLong, 0), State(issued, thread.unsafe, Vec(thread.unsafe), "op"))
          recorder.access(thread.unsafe, Space.Global, AccessKind.Write, issued, 4)
        }
      }
    }
    threads.foreach(_.start())
    threads.foreach(_.join(60000L))
    val seen = replay(recorder.stream)
    assertEquals(seen.collect { case issue: ProfileStream.Issue => issue.issued }, Vec.tabulate(200)(_.toLong))
    assertEquals(
      seen.collect { case (_, _, _, address: Long, _) => address },
      Vec.tabulate(200)(_.toLong)
    )
  }

  test("a stream that is not one is rejected") {
    val read = ProfileStream.read(DataInputStream(ByteArrayInputStream(Array.fill[Byte](16)(0))))
    assertEquals(read, Left("not a profile stream"))
  }
}
