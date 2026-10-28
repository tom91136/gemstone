package net.kurobako.gemstone.isa.nvidia

import java.util.concurrent.atomic.AtomicBoolean
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*

class DeviceHeapConcurrencySuite extends munit.FunSuite {
  private def allocate(heap: DeviceHeap, size: Int > 0)(using munit.Location): GuestAddress =
    heap.allocate(size).fold(fault => fail(fault.message), identity)

  test("concurrent atomics, stores and loads stay exact while other blocks are allocated and freed") {
    val heap    = DeviceHeap(RegionMemory())
    val counter = allocate(heap, 8)
    val pair    = allocate(heap, 8)
    val rounds  = 20000
    val torn    = AtomicBoolean(false)
    val threads = Vec.tabulate(4) { worker =>
      Thread.ofPlatform().start { () =>
        (0 until rounds).foreach { round =>
          val _ = heap.atomicModifyValue(counter, 8)(_ + 1L)
          if (worker == 0) {
            val _ = heap.writeValue(pair, if (round % 2 == 0) 0L else -1L, 8)
          } else heap.readValue(pair, 8) match {
            case Right(value) if value != 0L && value != -1L => torn.set(true)
            case _                                           => ()
          }
          if (worker == 1) heap.allocate(16).foreach(block => assert(heap.free(block)))
        }
      }
    }
    threads.foreach { thread =>
      thread.join(30000L)
      assert(!thread.isAlive, "a heap worker did not finish")
    }
    assertEquals(heap.readValue(counter, 8), Right(4L * rounds))
    assert(!torn.get())
  }
}
