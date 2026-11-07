package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicLongArray
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

class PacingWaitSuite extends Suite {
  private val Settle = 50L
  private val Limit  = 25.seconds

  private def thread(run: => Unit): Thread = {
    val started = Thread(() => run)
    started.setDaemon(true)
    started.start()
    started
  }

  private def until(condition: => Boolean): Boolean = {
    val deadline = 10.seconds.fromNow
    while (!condition && deadline.hasTimeLeft()) Thread.sleep(1L)
    condition
  }

  private def leader(pacing: Pacing, count: Int): (Thread, AtomicInteger) = {
    val steps = AtomicInteger()
    val self  = thread {
      val pace = pacing.join()
      (0 until count).foreach { _ =>
        pace.step()
        val _ = steps.incrementAndGet()
      }
      pace.leave()
    }
    (self, steps)
  }

  private def finished(threads: Seq[Thread]): Boolean = {
    val deadline = Limit.fromNow
    threads.foreach(_.join(math.max(1L, deadline.timeLeft.toMillis)))
    threads.forall(!_.isAlive)
  }

  test("a waiter resumes once the slowest group publishes the clock that brings it within the skew") {
    val pacing = Pacing(2)
    pacing.engage()
    val slowest        = pacing.join()
    val (ahead, steps) = leader(pacing, 1000)
    assert(until(steps.get() >= Pacing.Skew))
    Thread.sleep(Settle)
    val held = steps.get()
    assert(held <= Pacing.Skew + 1, held)
    (0 until 4).foreach(_ => slowest.step())
    assert(until(steps.get() > held))
    assert(steps.get() <= held + 4 + Pacing.Skew)
    slowest.leave()
    assert(finished(Seq(ahead)))
    assertEquals(steps.get(), 1000)
  }

  test("a waiter runs on while the slowest group is parked") {
    val pacing = Pacing(2)
    pacing.engage()
    val slowest        = pacing.join()
    val (ahead, steps) = leader(pacing, 1000)
    assert(until(steps.get() >= Pacing.Skew))
    val done = slowest.parked(finished(Seq(ahead)))
    assert(done)
    assertEquals(steps.get(), 1000)
    slowest.leave()
  }

  test("a waiter runs on once the slowest group leaves") {
    val pacing = Pacing(2)
    pacing.engage()
    val slowest        = pacing.join()
    val (ahead, steps) = leader(pacing, 1000)
    assert(until(steps.get() >= Pacing.Skew))
    slowest.leave()
    assert(finished(Seq(ahead)))
    assertEquals(steps.get(), 1000)
  }

  test("a group joining behind a waiter holds it until the newcomer steps") {
    val pacing = Pacing(3)
    pacing.engage()
    val first = pacing.join()
    val steps = AtomicInteger()
    val stop  = AtomicBoolean()
    val ahead = thread {
      val pace = pacing.join()
      while (!stop.get()) {
        pace.step()
        val _ = steps.incrementAndGet()
      }
      pace.leave()
    }
    assert(until(steps.get() >= Pacing.Skew))
    first.leave()
    assert(until(steps.get() > 10 * Pacing.Skew))
    val newcomer = pacing.join()
    val base     = steps.get()
    Thread.sleep(Settle)
    val held = steps.get()
    Thread.sleep(Settle)
    assertEquals(steps.get(), held)
    assert(held <= base + Pacing.Skew + 1, (held, base))
    (0 until 1000).foreach(_ => newcomer.step())
    assert(until(steps.get() > held))
    stop.set(true)
    newcomer.leave()
    assert(finished(Seq(ahead)))
  }

  test("many more groups than processors keep within the skew and all finish") {
    val groups   = math.max(64, 4 * sys.runtime.availableProcessors())
    val count    = 2000
    val progress = AtomicLongArray(groups)
    val worst    = AtomicLong()
    (0 until 3).foreach { _ =>
      (0 until groups).foreach(progress.set(_, 0L))
      val pacing = Pacing(groups)
      pacing.engage()
      val paces   = (0 until groups).map(_ => pacing.join())
      val threads = paces.zipWithIndex.map { (pace, index) =>
        thread {
          (1 to count).foreach { n =>
            pace.step()
            progress.set(index, n.toLong)
            if (n % 5 == 0) {
              val lowest = (0 until groups).collect { case other if other != index => progress.get(other) }.min
              val _      = worst.accumulateAndGet(n - lowest, math.max)
            }
          }
          progress.set(index, Long.MaxValue)
          pace.leave()
        }
      }
      assert(finished(threads))
    }
    assert(worst.get() <= Pacing.Skew + 1, worst.get())
  }

  test("groups that park, leave and rejoin among many threads all finish") {
    val groups = math.max(64, 4 * sys.runtime.availableProcessors())
    val pacing = Pacing(groups)
    pacing.engage()
    val threads = (0 until groups).map { index =>
      thread {
        (0 until 4).foreach { round =>
          val pace = pacing.join()
          (0 until 500).foreach { n =>
            pace.step()
            if ((n + index + round) % 97 == 0) pace.parked(Thread.`yield`())
          }
          pace.leave()
        }
      }
    }
    assert(finished(threads))
  }

  test("groups that keep waiting on one another all finish, with processors to spare or without") {
    Seq(2, 3, 4, sys.runtime.availableProcessors() + 1).foreach { groups =>
      val pacing = Pacing(groups)
      pacing.engage()
      val paces   = (0 until groups).map(_ => pacing.join())
      val threads = paces.map { pace =>
        thread {
          (0 until 400000 / groups).foreach(_ => pace.step())
          pace.leave()
        }
      }
      assert(finished(threads))
    }
  }
}
