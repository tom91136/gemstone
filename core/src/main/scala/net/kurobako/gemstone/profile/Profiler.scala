package net.kurobako.gemstone.profile

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import net.kurobako.gemstone.vm.LaunchObserver.*
import scala.collection.mutable

object Profiler {

  /** The lanes of one subgroup that ran an instruction together: in the same issue, or, on a machine that issues one
    * lane at a time as SPIR-V does, in the same `occurrence`th execution of the instruction, as hardware would.
    */
  private case class SubgroupKey(workgroup: Coordinates, subgroup: Int, instruction: Int, occurrence: Long)
      derives CanEqual

  private case class LaneKey(workgroup: Coordinates, lane: Int) derives CanEqual

  private case class UnitKey(workgroup: Coordinates, unit: Int) derives CanEqual

  private case class SubgroupId(workgroup: Coordinates, subgroup: Int) derives CanEqual

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Request(val space: Space, val kind: AccessKind) {
    var count                  = 0
    var addresses: Array[Long] = Array.ofDim(32)
    var bytes: Array[Int]      = Array.ofDim(32)

    def add(address: Long, size: Int): Unit = {
      if (count == addresses.length) {
        addresses = java.util.Arrays.copyOf(addresses, count * 2)
        bytes = java.util.Arrays.copyOf(bytes, count * 2)
      }
      addresses(count) = address
      bytes(count) = size
      count += 1
    }
  }

  /** A subgroup's issue of an instruction; one the machine made without hardware beginning it counts only its traffic.
    * `holders` are the lanes whose latest issue it is, and `expected` the lanes that must have joined before nothing
    * more can.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class SubgroupIssue(val key: SubgroupKey, val subgroupSize: Int, val begun: Boolean, val expected: Int) {
    var lanes                                                        = 0
    var holders                                                      = 0
    val successors: mutable.HashSet[Int]                             = mutable.HashSet.empty
    val predecessors: mutable.HashSet[SubgroupKey]                   = mutable.HashSet.empty
    val requests: mutable.HashMap[(Int, Space, AccessKind), Request] = mutable.HashMap.empty

    def instruction: Int = key.instruction
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private class Lane(val key: LaneKey) {
    var last: SubgroupIssue | Null                  = null
    var unit: Int                                   = -1
    var ordinal: Int                                = 0
    lazy val occurrences: mutable.HashMap[Int, Int] = mutable.HashMap.empty
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class IntSequence {
    var data: Array[Int] = Array.ofDim(16)
    var size: Int        = 0

    def +=(value: Int): Unit = {
      if (size == data.length) data = java.util.Arrays.copyOf(data, size * 2)
      data(size) = value
      size += 1
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class OneLanePerIssue extends ProfileStream.Visitor {
    var holds: Boolean = true

    override def issue(issue: ProfileStream.Issue): Unit = if (issue.lanes.size > 1) holds = false
  }

  /** Measures a subgroup issue as soon as nothing can change it: when every lane that joined it has moved on or
    * retired, and, where lanes join at their own pace, when every lane of the subgroup has joined. What stays open is
    * what a lane still holds, plus, in per-lane mode, occurrences some lane of the subgroup has not reached.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private class Replay(
      perLane: Boolean,
      workgroupLanes: Option[Int],
      architecture: Architecture,
      instructions: Int,
      tracksUse: Boolean
  ) extends ProfileStream.Visitor {
    val perInstruction: Array[Profile.Counters]                   = Array.fill(instructions)(Profile.Counters.zero)
    val sequences: mutable.LinkedHashMap[SubgroupId, IntSequence] = mutable.LinkedHashMap.empty
    var peakOpen                                                  = 0
    private val open                                              = mutable.HashMap.empty[SubgroupKey, SubgroupIssue]
    private val lanes                                             = mutable.HashMap.empty[LaneKey, Lane]
    private val units   = mutable.HashMap.empty[UnitKey, mutable.HashSet[Lane]]
    private val current = mutable.HashMap.empty[Int, Lane]

    def finish(): Unit = open.values.toVector.foreach(finalise(_))

    private def finalise(group: SubgroupIssue): Unit = {
      open -= group.key
      perInstruction(group.instruction) = perInstruction(group.instruction) + counters(group, architecture)
    }

    private def leave(group: SubgroupIssue): Unit = {
      group.holders -= 1
      if (group.holders == 0 && group.lanes >= group.expected) finalise(group)
    }

    private def release(lane: Lane): Unit = {
      lanes -= lane.key
      val held = lane.last
      lane.last = null
      if (held != null) leave(held)
    }

    override def transition(event: MachineEvent): Unit = event.kind match {
      case MachineEvent.Kind.Retired =>
        units.remove(UnitKey(event.unit.workgroup, event.unit.ordinal)).foreach(_.foreach(release(_)))
      case _ => ()
    }

    override def issue(issue: ProfileStream.Issue): Unit = {
      current.clear()
      issue.lanes.foreach { index =>
        val key  = LaneKey(issue.workgroup, index)
        val lane = lanes.getOrElseUpdate(key, Lane(key))
        if (lane.unit != issue.unit.raw) moveUnit(issue, lane)
        val last = lane.last
        if (last == null || issue.begins || last.instruction != issue.instruction.raw) begin(issue, lane)
        current(index) = lane
      }
    }

    private def moveUnit(issue: ProfileStream.Issue, lane: Lane): Unit = {
      units.get(UnitKey(issue.workgroup, lane.unit)).foreach(_ -= lane)
      units.getOrElseUpdate(UnitKey(issue.workgroup, issue.unit), mutable.HashSet.empty) += lane
      lane.unit = issue.unit
    }

    private def begin(issue: ProfileStream.Issue, lane: Lane): Unit = {
      lane.ordinal = 0
      val occurrence =
        if (perLane) {
          val nth = lane.occurrences.getOrElse(issue.instruction, 0)
          lane.occurrences(issue.instruction) = nth + 1
          nth.toLong
        } else issue.issued
      val subgroup = lane.key.lane / issue.subgroupSize
      val key      = SubgroupKey(issue.workgroup, subgroup, issue.instruction, occurrence)
      val group    = open.getOrElseUpdate(key, create(key, issue))
      group.lanes += 1
      group.holders += 1
      val previous = lane.last
      lane.last = group
      if (previous != null) {
        previous.successors += issue.instruction
        group.predecessors += previous.key
        leave(previous)
      }
    }

    private def create(key: SubgroupKey, issue: ProfileStream.Issue): SubgroupIssue = {
      val size     = issue.subgroupSize.raw
      val expected =
        if (perLane) workgroupLanes.fold(size)(total => (total - key.subgroup * size).min(size).max(1)) else 0
      if (tracksUse)
        sequences.getOrElseUpdate(SubgroupId(key.workgroup, key.subgroup), IntSequence()) += key.instruction
      val group = SubgroupIssue(key, size, issue.begins, expected)
      peakOpen = peakOpen.max(open.size + 1)
      group
    }

    override def access(lane: Int >= 0, space: Space, kind: AccessKind, address: Long, bytes: Int >= 0): Unit =
      current.get(lane).foreach { held =>
        val ordinal = held.ordinal
        held.ordinal = ordinal + 1
        held.last.requests.getOrElseUpdate((ordinal, space, kind), Request(space, kind)).add(address, bytes)
      }
  }

  /** Replays `stream` and measures it under `architecture`'s models. */
  def run(stream: ProfileStream, architecture: Architecture): Profile = measure(stream, architecture).profile

  /** A profile, and the most subgroup issues the replay held open at once to measure it. */
  case class Measured(profile: Profile, peakOpen: Int) derives CanEqual

  def measure(stream: ProfileStream, architecture: Architecture): Measured = {
    val probe = OneLanePerIssue()
    stream.foreach(probe)
    val tracksUse = stream.instructions.exists(!_.use.isEmpty)
    val replay    = Replay(
      probe.holds,
      stream.resources.map(_.workgroupLanes.raw),
      architecture,
      stream.instructions.size,
      tracksUse
    )
    stream.foreach(replay)
    replay.finish()

    val perInstruction = replay.perInstruction
    val profile        = Profile(
      architecture,
      stream.resources.map(Occupancy.of(architecture.limits, _)),
      Option.when(tracksUse)(pressure(stream, replay.sequences.values)),
      perInstruction.foldLeft(Profile.Counters.zero)(_ + _),
      stream.instructions.zip(perInstruction)
    )
    Measured(profile, replay.peakOpen)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def pressure(stream: ProfileStream, sequences: Iterable[IntSequence]): Profile.Pressure = {
    var peak  = 0
    var total = 0L
    var count = 0L
    sequences.foreach { sequence =>
      val live      = mutable.HashMap.empty[String, Int]
      var liveUnits = 0
      var at        = sequence.size - 1
      while (at >= 0) {
        val use = stream.instructions(sequence.data(at)).use
        use.writes.foreach(name => liveUnits -= live.remove(name).getOrElse(0))
        use.reads.foreach { name =>
          val units = use.unitsOf(name)
          liveUnits += units - live.put(name, units).getOrElse(0)
        }
        val held = liveUnits + use.writes.collect { case name if !live.contains(name) => use.unitsOf(name) }.sum
        peak = peak.max(held)
        total += held.toLong
        count += 1L
        at -= 1
      }
    }
    Profile.Pressure(peak, if (count == 0L) 0.0 else total.toDouble / count.toDouble)
  }

  private def counters(group: SubgroupIssue, architecture: Architecture): Profile.Counters = {
    val memory = group.requests.values.foldLeft(Map.empty[(Space, AccessKind), Profile.Memory]) { (memory, request) =>
      val key = (request.space, request.kind)
      memory.updated(key, memory.getOrElse(key, Profile.Memory.zero) + measureRequest(request, architecture))
    }
    if (!group.begun) Profile.Counters.zero.copy(memory = memory)
    else
      Profile.Counters(
        issues = 1L,
        activeLanes = group.lanes.toLong,
        subgroupLanes = group.subgroupSize.toLong,
        divergent = if (group.successors.size > 1) 1L else 0L,
        reconverging = if (group.predecessors.size > 1) 1L else 0L,
        memory = memory
      )
  }

  private def measureRequest(request: Request, architecture: Architecture): Profile.Memory = {
    val addresses = request.addresses
    val bytes     = request.bytes
    val count     = request.count
    val total     = (0 until count).view.map(bytes(_).toLong).sum
    request.space match {
      case Space.Shared =>
        val banks     = architecture.shared.count.value
        val bankBytes = architecture.shared.bankBytes.value
        Profile.Memory.zero.copy(
          requests = 1L,
          bytes = total,
          wavefronts = MemoryModel.wavefronts(addresses, bytes, count, banks, bankBytes).toLong,
          idealWavefronts = MemoryModel.ideal(addresses, bytes, count, banks * bankBytes).toLong
        )
      case _ =>
        val sector = architecture.global.sectorBytes.value
        Profile.Memory.zero.copy(
          requests = 1L,
          bytes = total,
          sectors = MemoryModel.touched(addresses, bytes, count, sector).toLong,
          idealSectors = MemoryModel.ideal(addresses, bytes, count, sector).toLong,
          lines = MemoryModel.touched(addresses, bytes, count, architecture.global.lineBytes.value).toLong
        )
    }
  }
}
