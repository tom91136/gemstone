package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*

private object VMLanes {
  private val NoLanes = Array.empty[VMLane | Null]

  trait Source {
    def take(lane: WorkgroupLane, registerCount: Int >= 0, predicateCount: Int >= 0): VMLane
    def takeObserved(lane: WorkgroupLane, registerCount: Int >= 0, predicateCount: Int >= 0, owner: VMLanes): VMLane
    def give(lane: WorkgroupLane, cleared: VMLane): Unit
  }
}

/** Lane storage of one frame, taken per lane on first use from `pool`. */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
private class VMLanes(laneCount: Int in 1 ~ 1024, pool: VMLanes.Source) {
  private val lanes                     = java.util.Arrays.copyOf(VMLanes.NoLanes, laneCount)
  private var registerCount             = 0: Int >= 0
  private var predicateCount            = 0: Int >= 0
  private var use: VMRegisterUse | Null = null
  private var observed                  = false

  /** Sizes lanes taken from now on for `routine`; this storage holds none when it is prepared. */
  def prepare(routine: VMIR.Routine, observing: Boolean): Unit = {
    registerCount = routine.registerCount
    predicateCount = routine.predicateCount
    observed = observing
  }

  def apply(lane: WorkgroupLane): VMLane = lanes(lane.raw) match {
    case null     => take(lane)
    case existing => existing
  }

  private def take(lane: WorkgroupLane): VMLane = {
    val taken =
      if (observed) pool.takeObserved(lane, registerCount, predicateCount, this)
      else pool.take(lane, registerCount, predicateCount)
    lanes(lane.raw) = taken
    taken
  }

  def vacate(): Unit = {
    var index = 0
    while (index < lanes.length) {
      lanes(index) match {
        case null => ()
        case lane =>
          lane.clear()
          pool.give(WorkgroupLane.fromIndex(index), lane)
          lanes(index) = null
      }
      index += 1
    }
  }

  /** Collects the registers the next issue touches into `recording`. */
  def begin(recording: VMRegisterUse): Unit =
    use = recording

  /** Stops collecting; the issue is over, so reads made while observing it are not its own. */
  def end(): Unit = use = null

  def readRegister(register: VMIR.Register): Unit = {
    val recording = use
    if (recording != null) recording.readRegister(register)
  }

  def writeRegister(register: VMIR.Register): Unit = {
    val recording = use
    if (recording != null) recording.writeRegister(register)
  }

  def readPredicate(predicate: VMIR.Predicate): Unit = {
    val recording = use
    if (recording != null) recording.readPredicate(predicate)
  }

  def writePredicate(predicate: VMIR.Predicate): Unit = {
    val recording = use
    if (recording != null) recording.writePredicate(predicate)
  }
}
