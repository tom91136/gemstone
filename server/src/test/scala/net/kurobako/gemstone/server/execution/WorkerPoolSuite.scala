package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.vm.Fault

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicInteger
import scala.util.Using

final class WorkerPoolSuite extends munit.FunSuite {
  test("the bounded scheduler visits each task exactly once") {
    Using.resource(WorkerPool(2, minGroups = 1, minWorkItems = 1)) { scheduler =>
      val visited = AtomicInteger()
      val result  = scheduler.parallelFor(32L.refineUnsafe[Positive0]) { _ =>
        val _ = visited.incrementAndGet()
        Right(())
      }
      assertEquals(result, Right(()): Either[Fault, Unit])
      assertEquals(visited.get(), 32)
    }
  }
}
