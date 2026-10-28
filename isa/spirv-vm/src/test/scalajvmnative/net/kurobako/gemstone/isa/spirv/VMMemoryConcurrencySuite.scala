package net.kurobako.gemstone.isa.spirv

import java.util.concurrent.Callable
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class VMMemoryConcurrencySuite extends munit.FunSuite {
  private def storage(memory: AddressSpace, globals: Array[Byte]): VMMemory.Storage =
    VMMemory.Storage(memory, Vec.empty, globals, _ => Right(()))

  test("concurrent atomics serialise updates to dispatch-owned global storage") {
    val workers    = 4
    val increments = 256
    val shared     = storage(RegionMemory(), Array.ofDim[Byte](4))
    val pointer    =
      VMMemory.Pointer(VMMemory.apertureAddress(VMMemory.GlobalAperture, 0), 0, StorageClass.CrossWorkgroup)
    val start = CyclicBarrier(workers)
    Using.resource(threads.Pool(workers)) { pool =>
      val tasks = Vec.tabulate(workers) { _ =>
        val task: Callable[Vec[Long]] = () => {
          val memory = VMMemory.MemoryState(shared)
          val _      = start.await(5, TimeUnit.SECONDS)
          Vec.tabulate(increments)(_ => memory.atomicModifyScalar(pointer, 4)(_ + 1L).value)
        }
        pool.executor.submit(task)
      }
      val previous = tasks.flatMap(_.get(10, TimeUnit.SECONDS))
      assertEquals(previous.sorted, Vec.tabulate(workers * increments)(_.toLong))
      assertEquals(LittleEndian.decodeInt32(shared.globals, 0), workers * increments)
    }
  }
}
