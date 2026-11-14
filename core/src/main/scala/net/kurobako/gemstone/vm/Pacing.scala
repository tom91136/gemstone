package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicLongArray
import net.kurobako.gemstone.*

object Pacing {

  /** The steps a work-group may run ahead of the slowest running one once pacing engages, give or take the steps a
    * group takes between publishing its clock. Grid-wide hand-offs that assume co-scheduled work-groups, such as a
    * block reading a count every block has just agreed on, tolerate a few dozen steps of drift; a descheduled host
    * thread is unbounded.
    */
  val Skew: Long = 16L

  val Vacant: Long = Long.MaxValue
  val Parked: Long = Long.MaxValue - 1L
  // One slot per cache line pair keeps a work-group's clock off the lines its neighbours publish to.
  private val Stride = 16
  private val Spins  = 2048

  /** A group publishes its clock whenever its step count has all these bits clear. */
  val Publishing: Long = 3L

  /** A launch whose work-groups run one at a time, which nothing has to pace. */
  val Unpaced: Pacing = Pacing(0)

  // The clocks of the running groups are counted per level in a ring, so the lowest occupied level is a lower bound on
  // the slowest published clock, found without scanning the slots. A group stays within a skew and a publish of that
  // bound once it has read it, so the live levels stay closer together than the ring is long.
  private val Levels    = 64
  private val LevelMask = Levels - 1
  // The floor and the number of groups registering at it share one word, so a group joining at the floor cannot
  // slip below a floor that another thread raises at the same time.
  private val JoinerBits = 20
  private val JoinerMask = (1L << JoinerBits) - 1L
  private val FloorStep  = 1L << JoinerBits

  private val current = PlatformConcurrency.threadLocal[Pace](() => Pace.Free)

  /** The pace of the work-group running on this thread, if any. */
  def enclosing: Pace = current.get()

  def within[A](pace: Pace)(run: => A): A = {
    val outer = current.get()
    current.set(pace)
    try run
    finally current.set(outer)
  }
}

/** Keeps the running work-groups of one launch within [[Pacing.Skew]] steps of one another once a group polls memory
  * another group writes. Work-groups of an ordinary launch run on independent host threads and drift arbitrarily far
  * apart when the host deschedules one; code that waits on other groups through global memory, such as a software grid
  * barrier, may assume they stay close. Until [[engage]] nothing waits, so data-parallel launches keep their
  * throughput.
  *
  * Each running group publishes a step clock in its own slot and counts itself at that clock's level. The lowest
  * occupied level bounds the slowest published clock from below, so a group within the skew of that bound pays two
  * atomics for its check and never looks at the other slots. A group past the bound finds the slowest group with one
  * scan and waits for it, so the slowest group never waits and every waiting group resumes once it steps. A group that
  * has not started, has finished, or is [[Pace.parked]] holds no slot clock and holds nobody back.
  *
  * A waiting group parks its thread on the slowest other group's slot, with the clock that slot must reach. Publishing
  * a clock wakes the groups parked on that slot whose clock is reached, so a group that publishes while nobody waits on
  * it pays one load. A launch with no more groups than processors spins on that clock briefly first, since every group
  * then has a processor and the wait is usually short.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class Pacing(private val slots: Int, onEngage: () => Unit = () => ()) {
  import Pacing.*

  @volatile private var active = false
  private val clocks           = AtomicLongArray(slots * Stride)
  private val levels           = AtomicLongArray(Levels * Stride)
  private val state            = AtomicLong(0L)
  private val waiting          = AtomicLongArray(slots * Stride)
  private val waitingOn        = AtomicLongArray(slots)
  private val waitingFor       = AtomicLongArray(slots)
  private val waiters          = Array.ofDim[PlatformConcurrency.Waiter](slots)
  private val spins            = if (slots <= PlatformConcurrency.processors()) Spins else 0
  private val claiming         = AtomicLong(0L)

  locally {
    var slot = 0
    while (slot < slots) { clocks.set(slot * Stride, Vacant); waitingOn.set(slot, -1L); slot += 1 }
  }

  def engaged: Boolean = active

  /** Whether [[engage]] would still change anything. */
  def pending: Boolean = !active && slots > 1

  /** Starts pacing: from now on every running group's steps count. */
  def engage(): Unit = if (pending) { active = true; onEngage() }

  /** The pace of a group starting now, on a free slot, at the clock of the slowest group still running. */
  def join(): Pace =
    if (slots <= 1) Pace.Free
    else {
      state.getAndAdd(1L)
      try {
        val at   = floor
        val slot = claim(at)
        val _    = levels.getAndAdd((at & LevelMask).toInt * Stride, 1L)
        Pace(this, slot, slots, level = at, clock = at)
      } finally state.getAndAdd(-1L)
    }

  /** Publishes a clock, which wakes the groups parked on this slot whose target it reaches. */
  def publish(slot: Int, from: Long, to: Long): Unit = {
    // The new level must be counted before the old one empties, or the bound could pass a moving group.
    val _ = levels.getAndAdd((to & LevelMask).toInt * Stride, 1L)
    val _ = levels.getAndAdd((from & LevelMask).toInt * Stride, -1L)
    setClock(slot, to)
  }

  /** Stops counting a group that waits on the host rather than on the launch. */
  def pause(slot: Int, from: Long): Unit = {
    val _ = levels.getAndAdd((from & LevelMask).toInt * Stride, -1L)
    setClock(slot, Parked)
  }

  /** Stops counting a group that has finished. */
  def leave(slot: Int, from: Long): Unit = {
    val _ = levels.getAndAdd((from & LevelMask).toInt * Stride, -1L)
    setClock(slot, Vacant)
  }

  /** Starts counting a suspended group again, at the slowest clock if it fell behind meanwhile. */
  def resume(slot: Int, clock: Long): Long = {
    state.getAndAdd(1L)
    try {
      val at = math.max(clock, floor)
      val _  = levels.getAndAdd((at & LevelMask).toInt * Stride, 1L)
      setClock(slot, at)
      at
    } finally state.getAndAdd(-1L)
  }

  /** A lower bound on the clock of the slowest running group. Advances the floor over every level that no running group
    * holds, one level for each pass, so the bound trails the slowest clock by at most one check.
    */
  def running(): Long = {
    val from  = floor
    var at    = from
    var found = false
    var empty = false
    while (!found && !empty) {
      val current = state.get()
      val lowest  = current >>> JoinerBits
      at = lowest
      if (lowest - from >= Levels) empty = true
      else if ((current & JoinerMask) != 0L) found = true
      else if (levels.get((lowest & LevelMask).toInt * Stride) > 0L) found = true
      else if (state.compareAndSet(current, current + FloorStep)) at = lowest + 1L
    }
    at
  }

  /** Waits until `clock` is within the skew of the slowest other running group. Returns the clock of that group, which
    * `clock` may be below. `seen` caches the clocks of the other slots for the caller, so only the slowest needs
    * re-reading after the first scan.
    */
  def awaitSlow(slot: Int, clock: Long, seen: Array[Long]): Long = {
    var lowest = slowest(slot, seen)
    while (lowest >= 0 && clock - seen(lowest) > Skew) {
      block(slot, lowest, clock - Skew)
      lowest = slowest(slot, seen)
    }
    if (lowest < 0) clock else math.min(clock, seen(lowest))
  }

  /** Waits until the clock of `slot` reaches `target`. The slot's group publishes a clock at or past the target when it
    * steps on, parks, or leaves, so the wait ends unless that group itself waits, on a slower group in turn.
    */
  private def block(waiter: Int, slot: Int, target: Long): Unit =
    if (!spin(slot, target)) sleep(waiter, slot, target)

  private def spin(slot: Int, target: Long): Boolean = {
    var spun = 0
    while (spun < spins && clocks.get(slot * Stride) < target) { PlatformConcurrency.onSpinWait(); spun += 1 }
    spun < spins
  }

  /** A waiter registers for a target clock, then reads the clock before it parks. The sequentially consistent store of
    * the clock, followed by the read of the waiter count, guarantees that either the publisher sees the registration or
    * the waiter sees the clock.
    */
  private def sleep(waiter: Int, slot: Int, target: Long): Unit = {
    waiters(waiter) = PlatformConcurrency.waiter()
    waitingFor.set(waiter, target)
    waitingOn.set(waiter, slot.toLong)
    count(slot, 1L)
    while (clocks.get(slot * Stride) < target) PlatformConcurrency.park()
    waitingOn.set(waiter, -1L)
    count(slot, -1L)
  }

  private def count(slot: Int, change: Long): Unit = { val _ = waiting.getAndAdd(slot * Stride, change) }

  private def wake(slot: Int, clock: Long): Unit = {
    var waiter = 0
    while (waiter < slots) {
      if (waitingOn.get(waiter) == slot && waitingFor.get(waiter) <= clock) PlatformConcurrency.unpark(waiters(waiter))
      waiter += 1
    }
  }

  /** The slot of the other running group with the lowest clock, whose clock is then `seen(slot)`, or -1 when none runs.
    * A group's `seen` clocks never exceed the real ones, so only the slowest needs re-reading, and a slot seen vacant
    * or parked is re-read each time in case a group takes it.
    */
  private def slowest(self: Int, seen: Array[Long]): Int = {
    var slot = 0
    while (slot < slots) {
      if (slot != self && seen(slot) >= Parked) seen(slot) = PlatformConcurrency.getAcquire(clocks, slot * Stride)
      slot += 1
    }
    var at       = lowestSeen(self, seen)
    var rounds   = 0
    var verified = false
    while (at >= 0 && !verified && rounds <= slots) {
      val clock = PlatformConcurrency.getAcquire(clocks, at * Stride)
      verified = clock == seen(at)
      if (!verified) { seen(at) = clock; at = lowestSeen(self, seen); rounds += 1 }
    }
    at
  }

  private def lowestSeen(self: Int, seen: Array[Long]): Int = {
    var lowest = Parked
    var at     = -1
    var slot   = 0
    while (slot < slots) {
      if (slot != self && seen(slot) < lowest) { lowest = seen(slot); at = slot }
      slot += 1
    }
    at
  }

  private def setClock(slot: Int, clock: Long): Unit = {
    clocks.set(slot * Stride, clock)
    if (waiting.get(slot * Stride) > 0L) wake(slot, clock)
  }

  private def floor: Long = state.get() >>> JoinerBits

  private def claim(start: Long): Int = {
    var slot  = -1
    var probe = 0
    while (slot < 0 && probe < slots) {
      val at = (claiming.getAndIncrement() % slots).toInt
      if (clocks.compareAndSet(at * Stride, Vacant, start)) slot = at
      probe += 1
    }
    if (slot < 0) throw IllegalStateException(s"more than $slots work-groups of one launch run at once")
    slot
  }
}

object Pace {

  /** The pace of a work-group that runs alone or alongside every other group on one thread. */
  val Free: Pace = Pace(Pacing.Unpaced, slot = -1, slots = 0, level = 0L, clock = 0L)
}

/** One work-group's place in its launch's [[Pacing]]. Only the thread running the group uses it. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class Pace(pacing: Pacing, slot: Int, slots: Int, private var level: Long, private var clock: Long) {
  private lazy val seen = Array.fill(slots)(Pacing.Vacant)
  private var bound     = Long.MinValue
  private var parked    = 0

  def engaged: Boolean = pacing.engaged

  /** Whether [[engage]] would still change anything. */
  def pending: Boolean = pacing.pending

  /** Starts pacing the launch this group belongs to. */
  def engage(): Unit = pacing.engage()

  /** Counts one step, first waiting while this group is too far ahead of the slowest. */
  def step(): Unit = if (pacing.engaged) advance()

  private def advance(): Unit = {
    clock += 1L
    if (clock > bound) { publish(); bound = await() + Pacing.Skew }
    else if ((clock & Pacing.Publishing) == 0L) publish()
  }

  private def await(): Long = {
    val at = pacing.running()
    if (clock - at <= Pacing.Skew) math.min(clock, at) else pacing.awaitSlow(slot, clock, seen)
  }

  private def publish(): Unit = { pacing.publish(slot, level, clock); level = clock }

  /** Runs `blocking`, which waits on the host rather than on the launch, without holding the launch's other groups
    * back. The group rejoins at the slowest running group's clock if it fell behind meanwhile.
    */
  def parked[A](blocking: => A): A =
    if (slot < 0) blocking
    else {
      if (parked == 0) pacing.pause(slot, level)
      parked += 1
      try blocking
      finally {
        parked -= 1
        if (parked == 0) {
          clock = pacing.resume(slot, clock)
          level = clock
          bound = Long.MinValue
        }
      }
    }

  def leave(): Unit = if (slot >= 0) pacing.leave(slot, level)
}
