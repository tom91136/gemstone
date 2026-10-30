package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

/** The timeline an observed launch records: the ordinal of every issue and every change a wave makes between them. */
class TimelineSuite extends Suite {

  private val gfx906 = Target("gfx906")

  /** A kernel whose two waves meet at a workgroup barrier and then end. */
  private def kernel(): VMIR.Kernel = {
    val assembly = asm.Assembler.encode(asm.Parser.parse("s_barrier\ns_endpgm").value, TargetMode(gfx906, 64)).value
    compileKernel(codeKernel(target = gfx906, code = assembly))
  }

  /** Two waves of a 128-lane workgroup, which is one wave of 64 lanes each. */
  private def task(): KernelTask =
    KernelTask.validated("kernel", Dimensions(128, 1, 1), Dimensions.One, Vec.empty).value

  /** The state of a wave is valid only while the observer is being called, so the ordinals are taken there. */
  private class Recording extends LaunchObserver {
    val ordinals: mutable.ArrayBuffer[Long]            = mutable.ArrayBuffer.empty
    val units: mutable.ArrayBuffer[MachineUnit]        = mutable.ArrayBuffer.empty
    val transitions: mutable.ArrayBuffer[MachineEvent] = mutable.ArrayBuffer.empty
    override def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] = {
      ordinals += state.issued
      units += state.unit
      Right(())
    }
    override def transition(event: MachineEvent): Unit = transitions += event
  }

  private def run(): Recording = {
    val observer = Recording()
    val result   = executeKernel(kernel(), task(), RegionMemory(), TaskControl(observer = Some(observer)))
    assertEquals(result, Right(()))
    observer
  }

  test("an observed launch numbers its issues without a gap") {
    val observer = run()
    val ordinals = observer.ordinals.toVector
    assertEquals(ordinals, Vector(0L, 1L, 2L, 3L))
    assertEquals(ordinals, ordinals.sorted)
    val gaps = ordinals.zipWithIndex.map { case (ordinal, index) => ordinal - index.toLong }
    assertEquals(gaps, Vector.fill(ordinals.size)(0L))
  }

  test("an observed launch names the wave that issued each instruction") {
    val observer = run()
    val units    = observer.units.toVector
    assertEquals(units.map(_.ordinal.raw), Vector(0, 1, 0, 1))
    assertEquals(units.map(_.lanes.raw), Vector(64, 64, 64, 64))
    assertEquals(units.map(_.workgroup.x.raw), Vector(0, 0, 0, 0))
    assertEquals(units.map(_.state).distinct, Vector(MachineUnit.State.Running))
  }

  test("a wave that waits at a barrier resumes when the machine releases it") {
    val observer = run()
    val blocked  = observer.transitions.collect { case event if isBarrier(event) => event }.toVector
    assertEquals(blocked.size, 2)
    assertEquals(blocked.map(_.unit.ordinal.raw), Vector(0, 1))
    blocked.foreach { event =>
      val resumed = observer.transitions.exists { later =>
        later.kind == MachineEvent.Kind.Resumed &&
        later.unit.ordinal.raw == event.unit.ordinal.raw &&
        later.unit.workgroup.x.raw == event.unit.workgroup.x.raw &&
        later.unit.workgroup.y.raw == event.unit.workgroup.y.raw &&
        later.unit.workgroup.z.raw == event.unit.workgroup.z.raw
      }
      assert(resumed, s"wave ${event.unit.ordinal.raw} never resumed")
    }
  }

  test("a wave that reaches s_endpgm retires") {
    val observer = run()
    val retired  = observer.transitions
      .collect { case event if event.kind == MachineEvent.Kind.Retired => event }
      .toVector
    assertEquals(retired.map(_.unit.ordinal.raw), Vector(0, 1))
    assertEquals(retired.map(_.unit.state).distinct, Vector(MachineUnit.State.Retired))
  }

  test("a transition reports where in the program it happened") {
    val observer = run()
    val blocked  = observer.transitions.collect { case event if isBarrier(event) => event }.toVector
    // the wave parks at the barrier, which is the first instruction of the kernel
    assertEquals(blocked.map(_.at.map(_.instruction.map(_.raw))), Vector(Some(Some(0L)), Some(Some(0L))))
    // and it resumes at the instruction it runs next, which is the end of the kernel
    val resumed =
      observer.transitions.collect { case event if event.kind == MachineEvent.Kind.Resumed => event }.toVector
    assertEquals(resumed.map(_.at.map(_.instruction.map(_.raw))), Vector(Some(Some(4L)), Some(Some(4L))))
    val retired =
      observer.transitions.collect { case event if event.kind == MachineEvent.Kind.Retired => event }.toVector
    assertEquals(retired.map(_.at.map(_.instruction.map(_.raw))), Vector(Some(Some(4L)), Some(Some(4L))))
  }

  test("a transition reports the count of the issues the launch has made") {
    val observer = run()
    val issues   = observer.ordinals.size
    observer.transitions.foreach { event =>
      assert(event.issued <= issues.toLong, s"transition ${event.kind} claims ${event.issued} issues")
    }
    assertEquals(observer.transitions.last.issued, issues.toLong)
  }

  test("a cooperative launch names each workgroup's wave under its own workgroup") {
    val observer = Recording()
    val task     = KernelTask
      .validated("kernel", Dimensions(64, 1, 1), Dimensions(2, 1, 1), Vec.empty, cooperative = true)
      .value
    val result = executeKernel(kernel(), task, RegionMemory(), TaskControl(observer = Some(observer)))
    assertEquals(result, Right(()))
    assertEquals(observer.ordinals.toVector, Vector(0L, 1L, 2L, 3L))
    assertEquals(observer.units.map(_.workgroup.x.raw).toVector, Vector(0, 1, 0, 1))
    assertEquals(observer.units.map(_.ordinal.raw).toVector, Vector(0, 0, 0, 0))
    assertEquals(observer.transitions.count(_.kind == MachineEvent.Kind.Retired), 2)
  }

  /** Whether the event parks a wave at a barrier. */
  private def isBarrier(event: MachineEvent): Boolean = event.kind match {
    case MachineEvent.Kind.Blocked(MachineUnit.Reason.Barrier(_)) => true
    case _                                                        => false
  }
}
