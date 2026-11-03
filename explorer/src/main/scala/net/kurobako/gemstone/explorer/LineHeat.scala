package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.semantics.*

/** What a launch's profile says about one listing line: the counters of the static instructions there and the cycles
  * their findings put on the multiprocessor.
  */
case class LineHeat(counters: Profile.Counters, cyclesLost: Double) derives CanEqual {

  def issues: Long = counters.issues

  def divergent: Long = counters.divergent

  def idleLanes: Long = counters.subgroupLanes - counters.activeLanes

  def laneEfficiency: Double = counters.laneEfficiency

  val global: Profile.Memory = counters.in(Space.Global)

  val shared: Profile.Memory = counters.in(Space.Shared)

  def excessSectors: Long = global.sectors - global.idealSectors

  def bankConflicts: Long = shared.conflicts

  def merge(that: LineHeat): LineHeat = LineHeat(counters + that.counters, cyclesLost + that.cyclesLost)
}
