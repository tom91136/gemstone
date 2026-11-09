package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.LaunchObserver.*
import scala.annotation.targetName

object Profile {

  object Memory {
    val zero: Memory = Memory(
      requests = 0L,
      bytes = 0L,
      sectors = 0L,
      idealSectors = 0L,
      lines = 0L,
      wavefronts = 0L,
      idealWavefronts = 0L
    )
  }

  /** The requests one kind of access to one space made, and what serving them took. A global or local request moves
    * `sectors` of the architecture's sector size across `lines` cache lines, where `idealSectors` would hold its bytes;
    * a shared request takes `wavefronts`, where `idealWavefronts` would serve it without bank conflicts.
    */
  case class Memory(
      requests: Long,
      bytes: Long,
      sectors: Long,
      idealSectors: Long,
      lines: Long,
      wavefronts: Long,
      idealWavefronts: Long
  ) derives CanEqual {
    @targetName("addMemory")
    def +(that: Memory): Memory = Memory(
      requests = requests + that.requests,
      bytes = bytes + that.bytes,
      sectors = sectors + that.sectors,
      idealSectors = idealSectors + that.idealSectors,
      lines = lines + that.lines,
      wavefronts = wavefronts + that.wavefronts,
      idealWavefronts = idealWavefronts + that.idealWavefronts
    )

    /** The shared-memory wavefronts bank conflicts added. */
    def conflicts: Long = wavefronts - idealWavefronts
  }

  object Counters {
    val zero: Counters = Counters(
      issues = 0L,
      activeLanes = 0L,
      subgroupLanes = 0L,
      divergent = 0L,
      reconverging = 0L,
      plannedCycles = 0L,
      memory = Map.empty
    )
  }

  /** Statistics over a set of subgroup issues. A subgroup issue is one instruction a warp, wave or subgroup issued for
    * the lanes active in it; `divergent` counts those whose lanes continued at more than one instruction, and
    * `reconverging` those whose lanes arrived from more than one subgroup issue. `plannedCycles` sums the planned issue
    * cycles of each begun issue that has a [[Schedule]].
    */
  case class Counters(
      issues: Long,
      activeLanes: Long,
      subgroupLanes: Long,
      divergent: Long,
      reconverging: Long,
      plannedCycles: Long,
      memory: Map[(Space, AccessKind), Memory]
  ) derives CanEqual {
    @targetName("addCounters")
    def +(that: Counters): Counters = Counters(
      issues = issues + that.issues,
      activeLanes = activeLanes + that.activeLanes,
      subgroupLanes = subgroupLanes + that.subgroupLanes,
      divergent = divergent + that.divergent,
      reconverging = reconverging + that.reconverging,
      plannedCycles = plannedCycles + that.plannedCycles,
      memory = (memory.keySet ++ that.memory.keySet).view.map { key =>
        key -> (memory.getOrElse(key, Memory.zero) + that.memory.getOrElse(key, Memory.zero))
      }.toMap
    )

    /** The fraction of subgroup lanes active per issue. */
    def laneEfficiency: Double = if (subgroupLanes == 0L) 1.0 else activeLanes.toDouble / subgroupLanes.toDouble

    /** Every kind of access to `space`, summed. */
    def in(space: Space): Memory =
      memory.foldLeft(Memory.zero) { case (sum, ((where, _), value)) => if (where == space) sum + value else sum }
  }

  /** How many 32-bit register units a subgroup holds live: the most at any issue, and the mean over its issues. A
    * register is live from an issue that writes it to the last issue that reads that value, as the subgroup ran, and
    * takes the units `RegisterUse.units` gives it: a 64-bit register is 2, and a predicate is 0 as it is not a general
    * register.
    */
  case class Pressure(peak: Int, mean: Double) derives CanEqual
}

/** A recorded launch's statistics under `architecture`'s models: its occupancy when the launch stated its resources,
  * its register pressure when the machine named registers, and counters for the whole launch and for each static
  * instruction, in the order of the stream's instruction table.
  */
case class Profile(
    architecture: Architecture,
    occupancy: Option[Occupancy],
    pressure: Option[Profile.Pressure],
    total: Profile.Counters,
    instructions: Vec[(ProfileStream.Instruction, Profile.Counters)]
) derives CanEqual
