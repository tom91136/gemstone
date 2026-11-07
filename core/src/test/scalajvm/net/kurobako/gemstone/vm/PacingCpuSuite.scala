package net.kurobako.gemstone.vm

import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

class PacingCpuSuite extends Suite {
  private val Waiters = 32
  private val Warmup  = 200L
  private val Window  = 1000L

  private def cpuNanos(threads: Seq[Thread]): Long = {
    val bean = ManagementFactory.getThreadMXBean
    threads.map(thread => bean.getThreadCpuTime(thread.threadId())).sum
  }

  test("work-groups stalled behind a peer give up the processor") {
    val pacing = Pacing(Waiters + 1)
    pacing.engage()
    val stalled = pacing.join()
    val stop    = AtomicBoolean()
    val waiters = (0 until Waiters).map { _ =>
      val thread = Thread { () =>
        val pace = pacing.join()
        while (!stop.get()) pace.step()
        pace.leave()
      }
      thread.setDaemon(true)
      thread.start()
      thread
    }
    Thread.sleep(Warmup)
    val before = cpuNanos(waiters)
    Thread.sleep(Window)
    val used = cpuNanos(waiters) - before
    stop.set(true)
    stalled.leave()
    waiters.foreach(_.join(10000L))
    assert(waiters.forall(!_.isAlive))
    assert(used < TimeUnit.MILLISECONDS.toNanos(Window) / 10, used)
  }

  test("work-groups stalled behind a peer wait without a timeout") {
    val pacing = Pacing(Waiters + 1)
    pacing.engage()
    val stalled = pacing.join()
    val stop    = AtomicBoolean()
    val waiters = (0 until Waiters).map { _ =>
      val thread = Thread { () =>
        val pace = pacing.join()
        while (!stop.get()) pace.step()
        pace.leave()
      }
      thread.setDaemon(true)
      thread.start()
      thread
    }
    val deadline = 10.seconds.fromNow
    while (deadline.hasTimeLeft() && !waiters.forall(_.getState.name == "WAITING")) Thread.sleep(10L)
    val states = waiters.map(_.getState.name).distinct
    stop.set(true)
    stalled.leave()
    waiters.foreach(_.join(10000L))
    assert(waiters.forall(!_.isAlive))
    assertEquals(states, Seq("WAITING"))
  }
}
