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
  * Each running group publishes a step clock in its own slot. A group more than the skew ahead of the slowest running
  * group waits for it, so the slowest group never waits and every waiting group resumes once it steps. A group that has
  * not started, has finished, or is [[Pace.parked]] holds no slot clock and holds nobody back.
  *
  * A waiting group parks its thread after registering on the slot of the slowest other group, with the clock that slot
  * must reach. Publishing a clock wakes the groups registered on that slot whose clock is reached, so a group that
  * publishes while nobody waits on it pays one load, and [[Pace.parked]] and [[Pace.leave]] release everyone waiting on
  * the group by publishing a clock past any target. A launch with no more groups than processors spins on that clock
  * briefly first, since every group then has a processor and the wait is usually short.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class Pacing(private val slots: Int, onEngage: () => Unit = () => ()) {
  import Pacing.*

  @volatile private var active = false
  private val clocks           = AtomicLongArray(slots * Stride)
  private val departed         = AtomicLong()
  private val waiting          = AtomicLongArray(slots * Stride)
  private val waitingOn        = AtomicLongArray(slots)
  private val waitingFor       = AtomicLongArray(slots)
  private val waiters          = Array.ofDim[PlatformConcurrency.Waiter](slots)
  private val spins            = if (slots <= PlatformConcurrency.processors()) Spins else 0

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
      val start = running()
      var slot  = 0
      while (slot < slots && !clocks.compareAndSet(slot * Stride, Vacant, start)) slot += 1
      if (slot == slots) throw IllegalStateException(s"more than $slots work-groups of one launch run at once")
      Pace(this, slot, slots, start)
    }

  /** A waiter registers, then reads the clock before it parks. The sequentially consistent store here, followed by the
    * read of the waiter count, guarantees that either this group sees the registration or the waiter sees the clock.
    */
  def publish(slot: Int, clock: Long): Unit = {
    clocks.set(slot * Stride, clock)
    if (waiting.get(slot * Stride) > 0L) wake(slot, clock)
  }

  private def wake(slot: Int, clock: Long): Unit = {
    var waiter = 0
    while (waiter < slots) {
      if (waitingOn.get(waiter) == slot && waitingFor.get(waiter) <= clock) PlatformConcurrency.unpark(waiters(waiter))
      waiter += 1
    }
  }

  /** Records the clock a group leaves or suspends at, for the next group to start from when none else runs. */
  def depart(clock: Long): Unit = {
    var known = departed.get()
    while (clock > known && !departed.compareAndSet(known, clock)) known = departed.get()
  }

  /** The clock of the slowest running group, or where the last group left off when none runs. */
  def running(): Long = {
    var lowest = Parked
    var slot   = 0
    while (slot < slots) {
      val clock = PlatformConcurrency.getAcquire(clocks, slot * Stride)
      if (clock < lowest) lowest = clock
      slot += 1
    }
    if (lowest < Parked) lowest else departed.get()
  }

  /** Waits until `clock` is within the skew of the slowest other running group. Returns the lowest clock of the running
    * groups, which `clock` may be below.
    */
  def await(slot: Int, clock: Long, seen: Array[Long]): Long = {
    var at = slowest(slot, seen)
    while (at >= 0 && clock - seen(at) > Skew) {
      block(slot, at, clock - Skew)
      at = slowest(slot, seen)
    }
    if (at < 0) clock else math.min(clock, seen(at))
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
}

@SuppressWarnings(Array("scalafix:FilenameConventions.top-level-filename"))
object Pace {

  /** The pace of a work-group that runs alone or alongside every other group on one thread. */
  val Free: Pace = Pace(Pacing.Unpaced, slot = -1, slots = 0)
}

/** One work-group's place in its launch's [[Pacing]]. Only the thread running the group uses it. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class Pace(pacing: Pacing, slot: Int, slots: Int, private var clock: Long = 0L) {
  private val seen   = Array.fill(slots)(Pacing.Vacant)
  private var bound  = Long.MinValue
  private var parked = 0

  def engaged: Boolean = pacing.engaged

  /** Whether [[engage]] would still change anything. */
  def pending: Boolean = pacing.pending

  /** Starts pacing the launch this group belongs to. */
  def engage(): Unit = pacing.engage()

  /** Counts one step, first waiting while this group is too far ahead of the slowest. */
  def step(): Unit = if (pacing.engaged) advance()

  private def advance(): Unit = {
    clock += 1L
    if ((clock & Pacing.Publishing) == 0L) pacing.publish(slot, clock)
    if (clock > bound) { pacing.publish(slot, clock); bound = pacing.await(slot, clock, seen) + Pacing.Skew }
  }

  /** Runs `blocking`, which waits on the host rather than on the launch, without holding the launch's other groups
    * back. The group rejoins at the slowest running group's clock if it fell behind meanwhile.
    */
  def parked[A](blocking: => A): A =
    if (slot < 0) blocking
    else {
      if (parked == 0) { pacing.depart(clock); pacing.publish(slot, Pacing.Parked) }
      parked += 1
      try blocking
      finally {
        parked -= 1
        if (parked == 0) {
          clock = math.max(clock, pacing.running())
          bound = Long.MinValue
          java.util.Arrays.fill(seen, Pacing.Vacant)
          pacing.publish(slot, clock)
        }
      }
    }

  def leave(): Unit = if (slot >= 0) { pacing.depart(clock); pacing.publish(slot, Pacing.Vacant) }
}
