package net.kurobako.gemstone.rpc

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

// munit assertions lock the suite, so workers report through a result cell and the test thread asserts
class HostMemoryMirrorConcurrencySuite extends Suite {
  override def baseTimeout: FiniteDuration = 120.seconds

  private def at(address: Long): GuestAddress = GuestAddress(address)

  private def fill(mirror: HostMemoryMirror, address: Long, value: Byte): Unit =
    mirror.write(at(address), Vec.fill(8)(value))

  private def finished(thread: Thread, millis: Long): Boolean = {
    thread.join(millis)
    !thread.isAlive
  }

  private def worker(failure: AtomicReference[Option[String]])(body: => Unit): Thread = Thread(() =>
    try body
    catch { case error: Throwable => failure.set(Some(error.toString)) }
  )

  test("a lane inside a shard does not hold up lanes in other shards") {
    val mirror     = HostMemoryMirror()
    val inside     = CountDownLatch(1)
    val release    = CountDownLatch(1)
    val failure    = AtomicReference(Option.empty[String])
    val sameShard  = 0x10000L
    val otherShard = 0x11000L
    val sibling    = sameShard + 64 * 4096L
    val others     = AtomicReference(Option.empty[Vec[Byte]])
    val siblings   = AtomicReference(Option.empty[Vec[Byte]])
    fill(mirror, sameShard, 1)
    fill(mirror, otherShard, 2)
    fill(mirror, sibling, 3)
    val holder = worker(failure) {
      val _ = mirror.modify(at(sameShard), 8, None) { value =>
        inside.countDown()
        assert(release.await(15, TimeUnit.SECONDS), "holder was not released")
        value
      }
    }
    holder.start()
    val entered = inside.await(10, TimeUnit.SECONDS)
    val other   = worker(failure)(others.set(mirror.read(at(otherShard), 8, None)))
    other.start()
    val otherDone = finished(other, 10000)
    val same      = worker(failure)(siblings.set(mirror.read(at(sibling), 8, None)))
    same.start()
    val sameDone = finished(same, 300)
    release.countDown()
    val sameLater   = finished(same, 10000)
    val holderLater = finished(holder, 10000)
    assert(entered)
    assert(otherDone, "a read of another shard waited for the lane inside a shard")
    assert(!sameDone, "a read of the same shard did not wait")
    assert(sameLater && holderLater)
    assertEquals(failure.get(), None)
    assertEquals(others.get(), Some(Vec.fill(8)(2.toByte)))
    assertEquals(siblings.get(), Some(Vec.fill(8)(3.toByte)))
  }

  test("lanes writing across shard boundaries in opposite orders neither deadlock nor lose bytes") {
    val mirror        = HostMemoryMirror()
    val failure       = AtomicReference(Option.empty[String])
    val start         = CountDownLatch(1)
    val pages         = 130
    val rounds        = 400
    val invalidations = AtomicLong()
    val threads       = Vector.tabulate(8) { lane =>
      worker(failure) {
        assert(start.await(15, TimeUnit.SECONDS), "lanes did not start")
        for (round <- 0 until rounds) {
          val first  = if (lane % 2 == 0) round % pages else pages - 1 - round % pages
          val base   = first * 4096L + 4094
          val before = invalidations.get()
          mirror.write(at(base), Vec.fill(4)((lane + 1).toByte))
          val missed = mirror.read(at(base), 4, None).isEmpty
          if (missed && before % 2 == 0 && invalidations.get() == before)
            throw IllegalStateException(s"lost write at $base")
          if (lane == 0 && round % 50 == 0) {
            val _ = invalidations.incrementAndGet()
            mirror.invalidateClean()
            val _ = invalidations.incrementAndGet()
          }
          if (lane == 1 && round % 50 == 0) { val _ = mirror.takeDirty() }
        }
      }
    }
    threads.foreach(_.start())
    start.countDown()
    val completed = threads.map(finished(_, 60000))
    assert(completed.forall(identity), "a lane deadlocked")
    assertEquals(failure.get(), None)
  }

  test("lanes updating words of their own in parallel keep every value") {
    val mirror  = HostMemoryMirror()
    val failure = AtomicReference(Option.empty[String])
    val lanes   = 8
    val words   = 4000
    val threads = Vector.tabulate(lanes) { lane =>
      worker(failure) {
        for (word <- 0 until words) {
          val address = 0x400000L + (lane.toLong * words + word) * 8
          mirror.write(at(address), Vec.tabulate(8)(index => (word + lane + index).toByte), 8)
          if (mirror.modify(at(address), 8, None)(_ + 1).isEmpty) throw IllegalStateException(s"lost word $address")
        }
      }
    }
    threads.foreach(_.start())
    val completed = threads.map(finished(_, 60000))
    assert(completed.forall(identity))
    assertEquals(failure.get(), None)
    val taken = mirror.takeDirty()
    assertEquals(taken.map(_.bytes.size.toLong).sum, lanes.toLong * words * 8)
    for {
      lane <- 0 until lanes
      word <- 0 until words by 97
    } {
      val address  = 0x400000L + (lane.toLong * words + word) * 8
      val batch    = taken.find(b => b.address.raw <= address && address < b.address.raw + b.bytes.size).get
      val offset   = (address - batch.address.raw).toInt
      val expected = (0 until 8).foldLeft(0L)((sum, index) => sum | ((word + lane + index) & 0xffL) << (8 * index)) + 1
      assertEquals(LittleEndian.decode(batch.bytes, offset, 8), expected)
    }
  }
}
