package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.memory.{AllocationKind, GuestAddress}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.util.Using

final class AllocationAddressSuite extends munit.FunSuite {
  test("allocation address aliases are immutable and non-overlapping") {
    Using.resource(ExecutionService(List.empty)) { service =>
      val first  = service.registerAllocation(16L, AllocationKind.Shared).toOption.get
      val second = service.registerAllocation(16L, AllocationKind.Shared).toOption.get

      assertEquals(service.registerAllocationAddress(first, GuestAddress(0x1000L)), Right(()))
      assertEquals(service.registerAllocationAddress(first, GuestAddress(0x1000L)), Right(()))
      assert(service.registerAllocationAddress(first, GuestAddress(0x2000L)).isLeft)
      assert(service.registerAllocationAddress(second, GuestAddress(0x1008L)).isLeft)
      assertEquals(service.registerAllocationAddress(second, GuestAddress(0x2000L)), Right(()))
    }
  }
}
