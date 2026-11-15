package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicLongArray
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.concurrent.*

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
  // the slowest running clock, found without scanning the slots. A group stays within a skew and a publish of that
  // bound once it has read it, so the live levels stay closer together than the ring is long.
  private val Levels    = 64
  private val LevelMask = Levels - 1

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
  * occupied level bounds the slowest running clock from below, so a group within the skew of that bound pays one atomic
  * for its check and never looks at the other slots. A group past the bound finds the slowest group with one scan and
  * waits for it, so the slowest group never waits and every waiting group resumes once it steps.
  *
  * A group registers at the bound while holding a lock that the bound's advance also takes, and the group's count then
  * keeps the bound from rising while it holds that level, so after [[join]] returns the bound can never pass the
  * group's clock. A finished or [[Pace.parked]] group holds no slot clock and holds nobody back.
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
  private val floor            = AtomicLong(0L)
  private val waiting          = AtomicLongArray(slots * Stride)
  private val waitingOn        = AtomicLongArray(slots)
  private val waitingFor       = AtomicLongArray(slots)
  private val waiters          = Array.ofDim[PlatformConcurrency.Waiter](slots)
  private val spins            = if (slots <= PlatformConcurrency.processors()) Spins else 0
  private val claiming         = AtomicLong(0L)
  // One bit per slot marks it free, so claiming a slot is one CAS on a word and an empty slot is never reported as
  // exhaustion because another joiner moved a probe past it.
  private val free = AtomicLongArray((slots + 63) / 64)
  // A registration and a floor advance must not interleave: the advance checks a level and then moves the floor, and a
  // group that registered between the two would be left below the floor for good. Checks, steps, and publishes never
  // take the lock; only registering a group and moving the floor do.
  private val lock = ReentrantLock()

  locally {
    var slot = 0
    while (slot < slots) { clocks.set(slot * Stride, Vacant); waitingOn.set(slot, -1L); slot += 1 }
    var word = 0
    while (word < free.length()) {
      var bits = 0L
      var bit  = 0
      while (bit < 64 && word * 64 + bit < slots) { bits |= 1L << bit; bit += 1 }
      free.set(word, bits)
      word += 1
    }
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
      val slot = claim()
      val at   = enter(slot, 0L)
      Pace(this, slot, slots, level = at, clock = at)
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

  /** Stops counting a group that has finished, which frees its slot. */
  def leave(slot: Int, from: Long): Unit = {
    val _ = levels.getAndAdd((from & LevelMask).toInt * Stride, -1L)
    setClock(slot, Vacant)
    release(slot)
  }

  /** Starts counting a suspended group again, at the slowest clock if it fell behind meanwhile. */
  def resume(slot: Int, clock: Long): Long = enter(slot, clock)

  /** A lower bound on the clock of the slowest running group. Advances the floor over every level that no running group
    * holds, one level for each pass, so the bound trails the slowest clock by at most one check.
    */
  def running(): Long = {
    val at = floor.get()
    if (levels.get((at & LevelMask).toInt * Stride) > 0L) at
    else
      lock.withLock {
        var lowest = floor.get()
        var walked = 0
        while (walked < Levels && levels.get((lowest & LevelMask).toInt * Stride) == 0L) {
          floor.set(lowest + 1L)
          lowest += 1L
          walked += 1
        }
        lowest
      }
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

  /** Counts a group at the lowest level it may start from. The lock keeps the floor still until the count lands, and
    * the count in turn keeps the floor from rising while the group holds that level, so a group that runs is never
    * below the floor.
    */
  private def enter(slot: Int, clock: Long): Long = {
    val at = lock.withLock {
      val start = math.max(clock, floor.get())
      val _     = levels.getAndAdd((start & LevelMask).toInt * Stride, 1L)
      start
    }
    setClock(slot, at)
    at
  }

  private def setClock(slot: Int, clock: Long): Unit = {
    clocks.set(slot * Stride, clock)
    if (waiting.get(slot * Stride) > 0L) wake(slot, clock)
  }

  private def claim(): Int = {
    var slot = -1
    while (slot < 0) {
      val start = (claiming.getAndIncrement() % free.length()).toInt
      var word  = 0
      var any   = false
      while (slot < 0 && word < free.length()) {
        val at   = (start + word) % free.length()
        var bits = free.get(at)
        while (slot < 0 && bits != 0L) {
          any = true
          val bit = bits & -bits
          if (free.compareAndSet(at, bits, bits ^ bit)) slot = at * 64 + java.lang.Long.numberOfTrailingZeros(bit)
          else bits = free.get(at)
        }
        word += 1
      }
      if (slot < 0 && !any) throw IllegalStateException(s"more than $slots work-groups of one launch run at once")
    }
    slot
  }

  private def release(slot: Int): Unit = {
    val word = slot / 64
    val bit  = 1L << (slot % 64)
    var bits = free.get(word)
    while (!free.compareAndSet(word, bits, bits | bit)) bits = free.get(word)
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
          java.util.Arrays.fill(seen, Pacing.Vacant)
        }
      }
    }

  def leave(): Unit = if (slot >= 0) pacing.leave(slot, level)
}
