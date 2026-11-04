package net.kurobako.gemstone.isa.amdgpu

import CooperativeDispatchSuite.*
import java.util.concurrent.atomic.AtomicLong
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.concurrent.duration.Deadline

class CooperativeDispatchConcurrencySuite extends Suite {

  test("a workgroup polling outside a cooperative dispatch backs off between its loads") {
    val backing = RegionMemory()
    val buffer  = AllocationId(1L)
    backing.allocate(buffer, 64)
    val accesses = AtomicLong()
    class CountingMemory(delegate: AddressSpace) extends ForwardingMemory(delegate) {
      override def atomicModifyValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
          update: Long => Long
      ): Either[Fault, Long] = {
        val _ = accesses.incrementAndGet()
        super.atomicModifyValue(region, offset, size)(update)
      }
    }
    val memory  = CountingMemory(backing)
    val release = Thread { () => Thread.sleep(300L); backing.putInt(buffer, 0, 1) }
    val started = Deadline.now
    release.start()
    val result  = launch(spin, 1, cooperative = false, memory, TaskControl.Limits(1000000000L))
    val elapsed = (Deadline.now - started).toNanos
    release.join()
    assertEquals(result, Right(()))
    assertEquals(backing.getInt(buffer, 4), 2)
    assert(accesses.get() <= elapsed / 25000L + 100L, s"${accesses.get()} loads in ${elapsed / 1000000L} ms")
  }
}
