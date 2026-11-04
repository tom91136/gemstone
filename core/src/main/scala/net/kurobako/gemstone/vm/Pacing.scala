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
  private val Stride   = 16
  private val Spins    = 2048
  private val Yields   = Spins + 256
  private val NapNanos = 20000L

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
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
class Pacing(private val slots: Int) {
  import Pacing.*

  @volatile private var active = false
  private val clocks           = AtomicLongArray(slots * Stride)
  private val departed         = AtomicLong()

  locally {
    var slot = 0
    while (slot < slots) { clocks.set(slot * Stride, Vacant); slot += 1 }
  }

  def engaged: Boolean = active

  /** Whether [[engage]] would still change anything. */
  def pending: Boolean = !active && slots > 1

  /** Starts pacing: from now on every running group's steps count. */
  def engage(): Unit = if (pending) active = true

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

  def publish(slot: Int, clock: Long): Unit = PlatformConcurrency.setRelease(clocks, slot * Stride, clock)

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
    var spins  = 0
    var lowest = slowest(slot, seen)
    while (clock - lowest > Skew) {
      if (spins < Spins) PlatformConcurrency.onSpinWait()
      else if (spins < Yields) PlatformConcurrency.yieldThread()
      else PlatformConcurrency.parkNanos(NapNanos)
      spins += 1
      lowest = slowest(slot, seen)
    }
    math.min(clock, lowest)
  }

  /** The lowest clock among the other running groups. A group's `seen` clocks never exceed the real ones, so only the
    * slowest needs re-reading, and a slot seen vacant or parked is re-read each time in case a group takes it.
    */
  private def slowest(self: Int, seen: Array[Long]): Long = {
    var slot = 0
    while (slot < slots) {
      if (slot != self && seen(slot) >= Parked) seen(slot) = PlatformConcurrency.getAcquire(clocks, slot * Stride)
      slot += 1
    }
    var rounds = 0
    var lowest = Parked
    while (rounds <= slots) {
      lowest = Parked
      var at = -1
      slot = 0
      while (slot < slots) {
        if (slot != self && seen(slot) < lowest) { lowest = seen(slot); at = slot }
        slot += 1
      }
      if (at < 0) return lowest
      val clock = PlatformConcurrency.getAcquire(clocks, at * Stride)
      if (clock == lowest) return lowest
      seen(at) = clock
      rounds += 1
    }
    lowest
  }
}

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
