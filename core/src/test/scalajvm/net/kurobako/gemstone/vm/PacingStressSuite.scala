package net.kurobako.gemstone.vm

import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicLongArray
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*
import scala.util.Random

/** Drives Pacing directly from more threads than slots. The floor is documented as a lower bound on the clock of the
  * slowest running group, so a floor above a live group's own clock is a violation. Each test repeats its interleaving
  * until `GEMSTONE_TEST_PACING_STRESS_SECONDS` runs out, which defaults to a couple of seconds and is raised for long
  * runs.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class PacingStressSuite extends Suite {
  override def baseTimeout: FiniteDuration = 30.minutes

  private val Seconds =
    sys.env.get(environment.TestPacingStressSeconds).flatMap(_.toIntOption)
      .getOrElse(environment.TestPacingStressSecondsDefault.raw.toInt)

  private object Peek {
    private val clock = classOf[Pace].getDeclaredField("clock")
    private val floor = classOf[Pacing].getDeclaredField("floor")
    clock.setAccessible(true)
    floor.setAccessible(true)

    def clockOf(pace: Pace): Long     = clock.getLong(pace)
    def floorOf(pacing: Pacing): Long = floor.get(pacing) match { case value: AtomicLong => value.get() }
  }

  private def thread(name: String)(run: => Unit): Thread = {
    val started = Thread(() => run, name)
    started.setDaemon(true)
    started.start()
    started
  }

  private def finish(threads: Seq[Thread], limit: FiniteDuration): Boolean = {
    val deadline = limit.fromNow
    threads.foreach(_.join(math.max(1L, deadline.timeLeft.toMillis)))
    threads.forall(!_.isAlive)
  }

  private def describe(at: AtomicReference[Option[String]], what: String, floor: Long, clock: Long): Unit = {
    val gap = floor - clock
    val _   = at.compareAndSet(None, Some(s"$what: floor $floor, clock $clock, floor leads by $gap"))
  }

  test("the floor never passes a group that has just joined") {
    val slots     = 8
    val joiners   = 32
    val pumps     = 8
    val pacing    = Pacing(slots)
    val permits   = Semaphore(slots)
    val stop      = AtomicBoolean()
    val deadline  = Seconds.seconds.fromNow
    val joins     = AtomicLong()
    val bad       = AtomicLong()
    val worst     = AtomicLong()
    val exhausted = AtomicLong()
    val first     = AtomicReference[Option[String]](None)
    pacing.engage()

    val joinThreads = (0 until joiners).map { index =>
      thread(s"joiner-$index") {
        while (!stop.get() && deadline.hasTimeLeft()) {
          permits.acquire()
          try {
            val pace = pacing.join()
            try {
              val clock = Peek.clockOf(pace)
              val floor = Peek.floorOf(pacing)
              if (floor > clock) {
                val _ = bad.incrementAndGet()
                val _ = worst.accumulateAndGet(floor - clock, math.max)
                describe(first, "join", floor, clock)
              }
            } finally pace.leave()
          } catch {
            case _: IllegalStateException => val _ = exhausted.incrementAndGet()
          } finally permits.release()
          val _ = joins.incrementAndGet()
        }
      }
    }
    val pumpThreads = (0 until pumps).map { index =>
      thread(s"pump-$index") {
        while (!stop.get() && deadline.hasTimeLeft()) {
          val _ = pacing.running()
        }
      }
    }
    assert(finish(joinThreads ++ pumpThreads, Seconds.seconds + 60.seconds), "the join stress did not finish")
    val summary =
      s"join: ${joins.get()} attempts in $Seconds s, ${bad.get()} violations, worst lead ${worst.get()}, " +
        s"${exhausted.get()} spurious slot exhaustions"
    println(summary)
    if (bad.get() > 0) fail(s"$summary; first ${first.get().mkString}")
  }

  test("the floor never passes a group that has just resumed") {
    val slots    = 8
    val groups   = slots
    val pumps    = 8
    val pacing   = Pacing(slots)
    val stop     = AtomicBoolean()
    val deadline = Seconds.seconds.fromNow
    val resumes  = AtomicLong()
    val bad      = AtomicLong()
    val worst    = AtomicLong()
    val first    = AtomicReference[Option[String]](None)
    pacing.engage()
    val paces = (0 until groups).map(_ => pacing.join())

    val groupThreads = paces.zipWithIndex.map { (pace, index) =>
      thread(s"parker-$index") {
        while (!stop.get() && deadline.hasTimeLeft()) {
          pace.parked(Thread.`yield`())
          val clock = Peek.clockOf(pace)
          val floor = Peek.floorOf(pacing)
          if (floor > clock) {
            val _ = bad.incrementAndGet()
            val _ = worst.accumulateAndGet(floor - clock, math.max)
            describe(first, "resume", floor, clock)
          }
          val _ = resumes.incrementAndGet()
        }
      }
    }
    val pumpThreads = (0 until pumps).map { index =>
      thread(s"pump-$index") {
        while (!stop.get() && deadline.hasTimeLeft()) {
          val _ = pacing.running()
        }
      }
    }
    assert(finish(groupThreads ++ pumpThreads, Seconds.seconds + 60.seconds), "the resume stress did not finish")
    paces.foreach(_.leave())
    val summary = s"resume: ${resumes.get()} resumes in $Seconds s, ${bad.get()} violations, worst lead ${worst.get()}"
    println(summary)
    if (bad.get() > 0) fail(s"$summary; first ${first.get().mkString}")
  }

  test("join finds a free slot while fewer threads than slots are claiming") {
    val slots    = 8
    val threads  = 64
    val steps    = 400
    val pacing   = Pacing(slots)
    val permits  = Semaphore(slots)
    val stop     = AtomicBoolean()
    val deadline = Seconds.seconds.fromNow
    val holders  = AtomicInteger()
    val joins    = AtomicLong()
    val failures = AtomicLong()
    val first    = AtomicReference[Option[String]](None)
    pacing.engage()

    val workers = (0 until threads).map { index =>
      thread(s"claimer-$index") {
        while (!stop.get() && deadline.hasTimeLeft()) {
          permits.acquire()
          holders.incrementAndGet()
          try {
            val pace = pacing.join()
            try {
              var step = 0
              while (step < steps && !stop.get()) { pace.step(); step += 1 }
              val _ = joins.incrementAndGet()
            } finally pace.leave()
          } catch {
            case error: IllegalStateException =>
              val _ = failures.incrementAndGet()
              val _ = first.compareAndSet(
                None,
                Some(
                  s"${error.getMessage}; ${holders.get()} of $slots permits held, this joiner holds one, " +
                    s"so at most ${slots - 1} slots were taken and one was free"
                )
              )
              stop.set(true)
          } finally {
            holders.decrementAndGet()
            permits.release()
          }
        }
      }
    }
    assert(finish(workers, Seconds.seconds + 60.seconds), "the claim stress did not finish")
    val summary = s"claim: ${joins.get()} joins in $Seconds s, ${failures.get()} spurious exhaustions"
    println(summary)
    if (failures.get() > 0) fail(s"$summary; first ${first.get().mkString}")
  }

  test("many threads over few slots stay within the skew and make progress") {
    val slots        = 64
    val threads      = 128
    val stepsPerLife = 400
    val pacing       = Pacing(slots)
    val permits      = Semaphore(slots)
    val stop         = AtomicBoolean()
    val states       = AtomicIntegerArray(threads)
    val clocks       = AtomicLongArray(threads)
    val rounds       = AtomicLongArray(threads)
    val joins        = AtomicLong()
    val exclusions   = AtomicLong()
    val abases       = AtomicLong()
    val slowestGap   = AtomicLong()
    val firstGap     = AtomicReference[Option[String]](None)
    val deadline     = Seconds.seconds.fromNow
    pacing.engage()

    def check(index: Int, pace: Pace, what: String): Unit = {
      val clock = Peek.clockOf(pace)
      val floor = Peek.floorOf(pacing)
      if (floor > clock) {
        val _ = abases.incrementAndGet()
        describe(firstGap, s"$what worker $index", floor, clock)
      }
    }

    val workers = (0 until threads).map { index =>
      val random = Random(index)
      thread(s"worker-$index") {
        while (!stop.get() && deadline.hasTimeLeft()) {
          permits.acquire()
          try {
            val pace = pacing.join()
            try {
              val _ = joins.incrementAndGet()
              check(index, pace, "join")
              clocks.set(index, Peek.clockOf(pace))
              val _ = rounds.incrementAndGet(index)
              states.set(index, 1)
              var step = 0
              while (step < stepsPerLife && !stop.get() && deadline.hasTimeLeft())
                if (random.nextInt(97) == 0) {
                  val _ = rounds.incrementAndGet(index)
                  states.set(index, 2)
                  pace.parked(Thread.`yield`())
                  check(index, pace, "resume")
                  clocks.set(index, Peek.clockOf(pace))
                  val _ = rounds.incrementAndGet(index)
                  states.set(index, 1)
                } else {
                  pace.step()
                  check(index, pace, "step")
                  clocks.set(index, Peek.clockOf(pace))
                  step += 1
                  if (random.nextInt(31) == 0) Thread.`yield`()
                }
              states.set(index, 0)
              val _ = rounds.incrementAndGet(index)
            } finally pace.leave()
          } catch {
            case _: IllegalStateException => val _ = exclusions.incrementAndGet()
          } finally permits.release()
        }
      }
    }

    val sampler = thread("sampler") {
      while (!stop.get() && deadline.hasTimeLeft()) {
        var lowest  = Long.MaxValue
        var highest = Long.MinValue
        var lowAt   = -1
        var highAt  = -1
        var at      = 0
        while (at < threads) {
          val before = rounds.get(at)
          if (states.get(at) == 1) {
            val clock = clocks.get(at)
            val after = rounds.get(at)
            if (before == after && states.get(at) == 1) {
              if (clock < lowest) { lowest = clock; lowAt = at }
              if (clock > highest) { highest = clock; highAt = at }
            }
          }
          at += 1
        }
        // The scan spans time, so both endpoints are re-read before the gap is believed: if neither moved, the two
        // clocks were live together.
        if (
          lowAt >= 0 && highAt >= 0 && lowAt != highAt && states.get(lowAt) == 1 && states.get(highAt) == 1 &&
          clocks.get(lowAt) == lowest && clocks.get(highAt) == highest
        ) {
          val gap = highest - lowest
          val _   = slowestGap.accumulateAndGet(gap, math.max)
          if (gap > 4 * Pacing.Skew) describe(firstGap, s"sample gap $gap", highest, lowest)
        }
        Thread.`yield`()
      }
    }

    assert(finish(workers :+ sampler, Seconds.seconds + 60.seconds), "the skew stress did not finish")
    val summary =
      s"skew: ${joins.get()} groups in $Seconds s, widest live gap ${slowestGap.get()}, " +
        s"${abases.get()} floor-above-clock, ${exclusions.get()} spurious slot exhaustions"
    println(summary)
    if (abases.get() > 0 || slowestGap.get() > 4 * Pacing.Skew) fail(s"$summary; first ${firstGap.get().mkString}")
  }

  test("a launch with every slot taken reports exhaustion, and a finished group frees one") {
    val slots  = 2
    val pacing = Pacing(slots)
    pacing.engage()
    val first  = pacing.join()
    val second = pacing.join()
    val full   = intercept[IllegalStateException](pacing.join())
    assert(full.getMessage.contains(s"more than $slots work-groups"), full.getMessage)
    second.leave()
    val third = pacing.join()
    try assert(Peek.floorOf(pacing) <= Peek.clockOf(third))
    finally {
      third.leave()
      first.leave()
    }
  }
}
