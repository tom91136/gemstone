package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

/** The timeline a launch records: the units its invocations run as, and the changes they go through between the issues
  * they make.
  */
class TimelineSuite extends Suite {
  private val module = spv"""
    $openCL
    OpEntryPoint Kernel %main "barrier" %lid
    OpDecorate %lid BuiltIn LocalInvocationIndex
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %lid_ptr = OpTypePointer Input %uint
    %lid = OpVariable %lid_ptr Input
    %workgroup = OpConstant %uint 2
    %relaxed = OpConstant %uint 0
    %fn = OpTypeFunction %void %uint_ptr
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %lane = OpLoad %uint %lid
    OpControlBarrier %workgroup %workgroup %relaxed
    %slot = OpPtrAccessChain %uint_ptr %out %lane
    OpStore %slot %lane
    OpReturn
    OpFunctionEnd
  """

  private val barrier = module.instructions.indexWhere {
    case Instruction.ControlBarrier(_, _, _) => true
    case _                                   => false
  }

  private val loadModule = spv"""
    $openCL
    OpEntryPoint Kernel %main "load" %lid
    OpDecorate %lid BuiltIn LocalInvocationIndex
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %lid_ptr = OpTypePointer Input %uint
    %lid = OpVariable %lid_ptr Input
    %fn = OpTypeFunction %void %uint_ptr %uint_ptr
    %main = OpFunction %void None %fn
    %in = OpFunctionParameter %uint_ptr
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %lane = OpLoad %uint %lid
    %source = OpPtrAccessChain %uint_ptr %in %lane
    %value = OpLoad %uint %source
    %target = OpPtrAccessChain %uint_ptr %out %lane
    OpStore %target %value
    OpReturn
    OpFunctionEnd
  """

  /** A memory whose host reads arrive only when the launch flushes them. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class DeferredMemory(memory: RegionMemory) extends ForwardingMemory(memory) {
    private var pending = true

    override def defersHostReads: Boolean = true

    override def readDeferred(
        region: AllocationId,
        offset: Long >= 0,
        size: Int >= 0
    ): AddressSpace.ReadResult[Vec[Byte]] =
      if (pending) Left(AddressSpace.Pending) else memory.readDeferred(region, offset, size)

    override def readValueDeferred(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)
        : AddressSpace.ReadResult[Long] =
      if (pending) Left(AddressSpace.Pending) else memory.readValueDeferred(region, offset, size)

    override def flushHostReads(): Either[Fault, Unit] = {
      pending = false
      Right(())
    }
  }

  /** One thing the launch did, in the order it did it. */
  private enum Entry {
    case Issued(unit: MachineUnit, issue: Long)
    case Changed(event: MachineEvent)
  }

  /** Records the issues a launch makes and the changes its units go through. */
  private class Recording extends LaunchObserver {
    val entries: mutable.ArrayBuffer[Entry] = mutable.ArrayBuffer.empty

    override def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] = {
      entries += Entry.Issued(state.unit, state.issued)
      Right(())
    }

    override def transition(event: MachineEvent): Unit = entries += Entry.Changed(event)
  }

  private def reason(event: MachineEvent): Option[MachineUnit.Reason] = event.kind match {
    case MachineEvent.Kind.Blocked(reason) => Some(reason)
    case _                                 => None
  }

  private def resumed(event: MachineEvent): Boolean = event.kind match {
    case MachineEvent.Kind.Resumed => true
    case _                         => false
  }

  private def retired(event: MachineEvent): Boolean = event.kind match {
    case MachineEvent.Kind.Retired => true
    case _                         => false
  }

  /** What names a unit: the workgroup it runs in, and its place in it. */
  private def key(unit: MachineUnit): (Int, Int, Int, Int) =
    (unit.workgroup.x.raw, unit.workgroup.y.raw, unit.workgroup.z.raw, unit.ordinal.raw)

  private def index(entries: Vector[Entry], matches: MachineEvent => Boolean): Int =
    entries.indexWhere {
      case Entry.Changed(event) => matches(event)
      case _                    => false
    }

  /** The change the entry records. */

  /** The ordinal of the issue the launch makes after the entry at `index`. */
  private def nextIssue(entries: Vector[Entry], index: Int): Option[Long] =
    entries.drop(index + 1).collectFirst { case Entry.Issued(_, issue) => issue }

  test("a workgroup barrier parks the invocations that meet at it and releases them") {
    val memory   = RegionMemory()
    val out      = memory.allocate(8L)
    val observer = Recording()
    val result   = load(module).flatMap(program =>
      execute(
        program,
        task("barrier", Vec(buffer(out)), Dimensions(2, 1, 1)),
        memory,
        TaskControl(observer = Some(observer))
      )
    )
    assertEquals(result, Right(()))
    assertEquals(word(memory, out, 0), 0L)
    assertEquals(word(memory, out, 4), 1L)

    val entries = observer.entries.toVector

    // Every issue of the launch is numbered in order, without a gap.
    val ordinals = entries.collect { case Entry.Issued(_, issue) => issue }
    assert(ordinals.nonEmpty)
    assertEquals(ordinals, ordinals.indices.map(_.toLong))
    assert(ordinals.sliding(2).forall(window => window(1) == window(0) + 1L))

    // One invocation is one unit, named by its index within the workgroup.
    val units = entries.collect { case Entry.Issued(unit, _) => unit }
    assertEquals(units.map(_.ordinal.raw).distinct.sorted, Vector(0, 1))
    assertEquals(units.map(_.lanes.raw).distinct, Vector(1))
    assertEquals(units.map(_.workgroup.x.raw).distinct, Vector(0))
    assertEquals(units.map(_.workgroup.y.raw).distinct, Vector(0))
    assertEquals(units.map(_.workgroup.z.raw).distinct, Vector(0))

    // Both invocations park at the barrier, and each is released again.
    val changes = entries.collect { case Entry.Changed(event) => event }
    val blocked = changes.filter(event => reason(event).isDefined)
    assertEquals(blocked.size, 2)
    blocked.foreach { event =>
      assertEquals(reason(event), Some(MachineUnit.Reason.Collective("workgroup barrier")))
      assertEquals(event.at.flatMap(_.instruction).map(_.raw), Some(barrier.toLong))
    }

    // Each unit issues, parks, is released, and finishes, and a finished unit issues nothing more.
    val perUnit = entries.groupBy {
      case Entry.Issued(unit, _) => key(unit)
      case Entry.Changed(event)  => key(event.unit)
    }
    assertEquals(perUnit.size, 2)
    perUnit.foreach { (unit, timeline) =>
      val parked   = index(timeline, event => reason(event).isDefined)
      val released = index(timeline, resumed(_))
      val finished = index(timeline, retired(_))
      assert(parked > 0, s"the unit $unit parked before it issued anything")
      assert(released > parked, s"the unit $unit was not released after parking")
      assert(finished > released, s"the unit $unit finished before it was released")
      assertEquals(finished, timeline.size - 1, s"the unit $unit issued after it finished")
    }

    // A change is recorded at the ordinal of the issue the launch makes next, so the timeline's bands line up with its
    // issues. The last change precedes no issue, so it names the ordinal one past the last.
    entries.zipWithIndex.foreach {
      case (Entry.Changed(event), at) =>
        assertEquals(nextIssue(entries, at).getOrElse(ordinals.size.toLong), event.issued)
      case (Entry.Issued(_, _), _) => ()
    }
  }

  test("an invocation that waits for memory parks until the memory arrives") {
    val backing  = RegionMemory()
    val memory   = DeferredMemory(backing)
    val in       = backing.allocate(4L)
    val out      = backing.allocate(4L)
    val observer = Recording()
    val result   = load(loadModule).flatMap(program =>
      execute(
        program,
        task("load", Vec(buffer(in), buffer(out)), Dimensions(1, 1, 1)),
        memory,
        TaskControl(observer = Some(observer))
      )
    )
    assertEquals(result, Right(()))

    val entries = observer.entries.toVector
    val changes = entries.collect { case Entry.Changed(event) => event }

    // The invocation parks on the memory it is waiting for, and is released when it arrives.
    val blocked = changes.filter(event => reason(event).isDefined)
    assertEquals(blocked.size, 1)
    assertEquals(reason(blocked.head), Some(MachineUnit.Reason.Memory))
    val released = changes.zipWithIndex.exists { case (event, index) =>
      resumed(event) && index > changes.indexOf(blocked.head)
    }
    assert(released, "the invocation was not released after waiting for memory")

    // The invocation finishes, and a finished invocation issues nothing more.
    assertEquals(changes.count(retired(_)), 1)
    assertEquals(index(entries, retired(_)), entries.size - 1)
  }
}
