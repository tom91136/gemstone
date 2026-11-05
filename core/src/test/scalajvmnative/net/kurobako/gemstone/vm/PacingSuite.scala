package net.kurobako.gemstone.vm

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*
import scala.util.Using

class PacingSuite extends Suite {
  private val Settle = 50L

  private def stepping(pace: => Pace, count: Int, steps: AtomicInteger = AtomicInteger()): (Thread, AtomicInteger) = {
    val thread = Thread { () =>
      val paced = pace
      (0 until count).foreach { _ =>
        paced.step()
        val _ = steps.incrementAndGet()
      }
    }
    thread.setDaemon(true)
    thread.start()
    (thread, steps)
  }

  private def finished(thread: Thread): Boolean = { thread.join(10000L); !thread.isAlive }

  test("an unengaged launch lets a group run any distance ahead of an idle one") {
    val pacing          = Pacing(2)
    val idle            = pacing.join()
    val (leader, steps) = stepping(pacing.join(), 100000)
    assert(finished(leader))
    assertEquals(steps.get(), 100000)
    idle.leave()
  }

  test("an engaged leader waits within the skew of the slowest group and resumes as it steps") {
    val pacing = Pacing(2)
    pacing.engage()
    val slowest         = pacing.join()
    val (leader, steps) = stepping(pacing.join(), 1000)
    Thread.sleep(Settle)
    val ahead = steps.get()
    assert(ahead <= Pacing.Skew + 1, ahead)
    assert(leader.isAlive)
    (0 until 1000).foreach(_ => slowest.step())
    assert(finished(leader))
    assertEquals(steps.get(), 1000)
  }

  test("the slowest group never waits, and a group that finishes holds nobody back") {
    val pacing = Pacing(2)
    pacing.engage()
    val slowest       = pacing.join()
    val (leader, led) = stepping(pacing.join(), 1000)
    val (alone, ran)  = stepping(slowest, 50)
    assert(finished(alone))
    assertEquals(ran.get(), 50)
    slowest.leave()
    assert(finished(leader))
    assertEquals(led.get(), 1000)
  }

  test("groups spinning on each other keep stepping, so neither waits forever") {
    val pacing = Pacing(2)
    pacing.engage()
    val flag                             = AtomicBoolean()
    def group(run: Pace => Unit): Thread = {
      val thread = Thread { () =>
        val pace = pacing.join()
        run(pace)
        pace.leave()
      }
      thread.setDaemon(true)
      thread.start()
      thread
    }
    val waiter = group(pace => while (!flag.get()) pace.step())
    val setter = group { pace =>
      (0 until 10000).foreach(_ => pace.step())
      flag.set(true)
    }
    assert(finished(setter))
    assert(finished(waiter))
  }

  test("parked and finished groups hold nobody back, and a resumed group rejoins at the slowest clock") {
    val pacing = Pacing(3)
    pacing.engage()
    val parked   = pacing.join()
    val finisher = pacing.join()
    finisher.leave()
    val ran = parked.parked {
      val (leader, steps) = stepping(pacing.join(), 1000)
      (finished(leader), steps.get())
    }
    assertEquals(ran, (true, 1000))
    val ahead = AtomicInteger()
    val _     = stepping(pacing.join(), 1000, ahead)
    Thread.sleep(Settle)
    assert(ahead.get() <= Pacing.Skew + 1, ahead.get())
    parked.leave()
  }

  test("a launch from inside a group parks the group, so its peers run on while the nested launch waits on them") {
    val geometry = LaunchGeometry.forLaunch(LaunchGeometry.Dimensions(1, 1, 1), LaunchGeometry.Dimensions(2, 1, 1))
      .fold(message => fail(message), identity)
    val nested = LaunchGeometry.forLaunch(LaunchGeometry.Dimensions(1, 1, 1), LaunchGeometry.Dimensions(1, 1, 1))
      .fold(message => fail(message), identity)
    val peerDone = CountDownLatch(1)
    val result   = Using.resource(WorkerPool(2)) { pool =>
      Dispatch.forEachGroup(geometry, pool) { (index, _, pace) =>
        pace.engage()
        if (index == 0L)
          Dispatch.forEachGroup(nested, Workgroups.Sequential) { (_, _, _) =>
            if (peerDone.await(10L, TimeUnit.SECONDS)) Right(()) else Left(Fault.Hang("the peer waited on the parent"))
          }
        else {
          (0 until 1000).foreach(_ => pace.step())
          peerDone.countDown()
          Right(())
        }
      }
    }
    assertEquals(result, Right(()))
  }

  test("groups that wait on a peer which has not started all run, however few the workers") {
    val groups   = 4
    val geometry = LaunchGeometry.forLaunch(LaunchGeometry.Dimensions(1, 1, 1), LaunchGeometry.Dimensions(4, 1, 1))
      .fold(message => fail(message), identity)
    val arrived = AtomicInteger()
    val result  = Using.resource(WorkerPool(2)) { pool =>
      Dispatch.forEachGroup(geometry, pool) { (_, _, pace) =>
        pace.engage()
        val _        = arrived.incrementAndGet()
        val deadline = 10.seconds.fromNow
        while (arrived.get() < groups && deadline.hasTimeLeft()) pace.step()
        if (arrived.get() == groups) Right(()) else Left(Fault.Hang("a group waited on a peer that never started"))
      }
    }
    assertEquals(result, Right(()))
  }
}
