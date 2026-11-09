package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchObserver.*
import scala.collection.mutable

class ProfileSuite extends Suite {
  private case class Seen(lane: Int, space: Space, kind: AccessKind, address: Long, bytes: Int) derives CanEqual
  private case class Step(lane: Int, kind: AsyncKind, target: Long, bytes: Long) derives CanEqual

  private class Collector extends ProfileStream.Visitor {
    val accesses: mutable.ArrayBuffer[Seen]              = mutable.ArrayBuffer.empty
    val issues: mutable.ArrayBuffer[ProfileStream.Issue] = mutable.ArrayBuffer.empty
    val steps: mutable.ArrayBuffer[Step]                 = mutable.ArrayBuffer.empty

    override def issue(issue: ProfileStream.Issue): Unit = issues += issue
    override def access(lane: Int >= 0, space: Space, kind: AccessKind, address: Long, bytes: Int >= 0): Unit =
      accesses += Seen(lane, space, kind, address, bytes)
    override def asynchronous(lane: Int >= 0, kind: AsyncKind, target: Long, bytes: Long): Unit =
      steps += Step(lane, kind, target, bytes)
  }

  test("a coalesced SASS global load and a shared store report each lane's address") {
    val kernel = program(
      89,
      """S2R R0, SR_TID.X
        |LDC.64 R10, c[0x0][0x160]
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |LDG.E R4, [R10]
        |STS [R0.X4], R4
        |EXIT""".stripMargin,
      shared = 128
    )
    val memory = Memory()
    val buffer = memory.allocate(Vec.fill(128)(0.toByte))
    val task   = KernelTask.validated(
      kernel.name,
      LaunchGeometry.Dimensions(32, 1, 1),
      LaunchGeometry.Dimensions(1, 1, 1),
      Vec(KernelTask.Argument.Memory(buffer, 0L))
    ).value
    val recorder = ProfileRecorder()
    assertEquals(memory.run(kernel.compile(8).value, task, TaskControl(observer = Some(recorder))), Right(()))
    val collected = Collector()
    recorder.stream.foreach(collected)
    assertEquals(
      collected.accesses.toVector.filter(_.space ne Space.Constant),
      Vector.tabulate(32)(lane => Seen(lane, Space.Global, AccessKind.Read, RegionMemory.Base + lane * 4L, 4)) ++
        Vector.tabulate(32)(lane => Seen(lane, Space.Shared, AccessKind.Write, lane * 4L, 4))
    )
    assert(collected.issues.forall(_.subgroupSize.raw == 32))
    assertEquals(
      collected.issues.toVector.map(issue => recorder.stream.instructions(issue.instruction).text).distinct,
      Vector(
        "SetPredicate(78,Equal,Unsigned(32),Immediate(0),Immediate(0),false)",
        "S2R R0, SR_TID.X;",
        "LDC.64 R10, c[0][0x160];",
        "IMAD.WIDE.U32 R10, R0, 4, R10;",
        "LDG.E R4, [R10];",
        "STS [R0.X4], R4;",
        "EXIT;"
      )
    )
  }

  test("a SASS launch reports its registers and static plus dynamic shared bytes") {
    val kernel = Harness.load(89, Fixture.listing("sharedStrideSm89"), "shared_stride", 4224)
    val memory = Memory()
    val buffer = memory.allocate(Vec.fill(65536)(0.toByte))
    val task   = KernelTask.validated(
      kernel.name,
      LaunchGeometry.Dimensions(32, 1, 1),
      LaunchGeometry.Dimensions(1, 1, 1),
      Vec(
        KernelTask.Argument.Memory(buffer, 0L),
        KernelTask.Argument.Scalar(LittleEndian.toVec(1L, 4).refine[NonEmpty[Vec[Byte]]].value)
      ),
      groupSegmentBytes = 256
    ).value
    val recorder = ProfileRecorder()
    assertEquals(memory.run(kernel.compile(8, 4).value, task, TaskControl(observer = Some(recorder))), Right(()))
    assertEquals(recorder.stream.resources, Some(KernelResources(32, 32, Some(10), 4480L)))
    assert(Profiler.run(recorder.stream, Architecture.sm89).occupancy.isDefined)
  }

  test("LDGDEPBAR commits a group of the cp.async class and DEPBAR.LE SB0 waits until at most its count remains") {
    val kernel = program(89, "LDGDEPBAR;\nLDGDEPBAR;\nDEPBAR.LE SB0, 0x1;\nDEPBAR.LE SB0, 0x0;\nEXIT")
    val memory = Memory()
    val task   = KernelTask.validated(
      kernel.name,
      LaunchGeometry.Dimensions(32, 1, 1),
      LaunchGeometry.Dimensions(1, 1, 1),
      Vec[KernelTask.Argument]()
    ).value
    val recorder = ProfileRecorder()
    assertEquals(memory.run(kernel.compile().value, task, TaskControl(observer = Some(recorder))), Right(()))
    val collected = Collector()
    recorder.stream.foreach(collected)
    val commit = Vector.tabulate(32)(Step(_, AsyncKind.Commit, target = 0L, bytes = 0L))
    val wait   = (count: Long) => Vector.tabulate(32)(Step(_, AsyncKind.WaitGroups, target = 0L, bytes = count))
    assertEquals(collected.steps.toVector, commit ++ commit ++ wait(1L) ++ wait(0L))
  }

  test("SASS float arithmetic reports the FMA pipe, integer addition the ALU pipe and EXIT none") {
    val kernel = program(89, "FADD R0, R1, R2;\nFMUL R3, R0, R1;\nFFMA R4, R0, R1, R2;\nIADD3 R5, R0, R1, RZ;\nEXIT")
    val memory = Memory()
    val task   = KernelTask.validated(
      kernel.name,
      LaunchGeometry.Dimensions(32, 1, 1),
      LaunchGeometry.Dimensions(1, 1, 1),
      Vec[KernelTask.Argument]()
    ).value
    val recorder = ProfileRecorder()
    assertEquals(memory.run(kernel.compile().value, task, TaskControl(observer = Some(recorder))), Right(()))
    val pipes = recorder.stream.instructions.map(instruction => instruction.text -> instruction.pipe).toMap
    assertEquals(pipes("FADD R0, R1, R2;"), Some(Pipe.Fma))
    assertEquals(pipes("FMUL R3, R0, R1;"), Some(Pipe.Fma))
    assertEquals(pipes("FFMA R4, R0, R1, R2;"), Some(Pipe.Fma))
    assertEquals(pipes("IADD3 R5, R0, R1, RZ;"), Some(Pipe.Alu))
    assertEquals(pipes("EXIT;"), None)
  }
}
