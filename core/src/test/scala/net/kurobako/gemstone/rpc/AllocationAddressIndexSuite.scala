package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

class AllocationAddressIndexSuite extends munit.FunSuite {
  private def stored(base: Long, size: Int > 0, aliased: Boolean): StoredAllocation =
    StoredAllocation(StoredAllocation.Storage(size), GuestAddress(base), aliased)

  test("lookups match a scan that prefers aliases and, among touching ranges, the range starting at the address") {
    val random  = scala.util.Random(42L)
    val aliases = (0 until 64).map(index => stored(0x1000L + index * 32L, 32, aliased = true)) ++ Vector(
      stored(Long.MaxValue - 15L, 16, aliased = true),
      stored(-64L, 32, aliased = true)
    )
    val synthetic   = (0 until 16).map(index => stored(0x1010L + index * 256L, 64, aliased = false))
    val allocations = (aliases ++ synthetic).zipWithIndex.map((allocation, index) =>
      AllocationId((index + 1L).unsafe[Long > 0]) -> allocation
    ).toMap
    val index = AllocationAddressIndex(allocations)
    def scan(aliased: Boolean, address: Long, size: Int): Option[(AllocationId, Long >= 0)] =
      allocations.toVector.collect {
        case (id, allocation)
            if allocation.aliased == aliased &&
              java.lang.Long.compareUnsigned(address, allocation.base.raw) >= 0 &&
              address - allocation.base.raw >= 0L && size <= allocation.size.raw &&
              address - allocation.base.raw <= allocation.size.raw.toLong - size =>
          id -> allocation.base.raw
      }.maxByOption(_._2)(using Ordering.fromLessThan[Long](java.lang.Long.compareUnsigned(_, _) < 0))
        .map((id, base) => id -> (address - base).unsafe[Long >= 0])
    val addresses = allocations.values.toVector.flatMap { allocation =>
      val base = allocation.base.raw
      Vector(
        base - 1L,
        base,
        base + allocation.size.raw - 1L,
        base + allocation.size.raw,
        base + allocation.size.raw + 1L
      )
    } ++ Vector(0L, Long.MinValue, Long.MaxValue, -1L) ++ Vector.fill(128)(random.nextLong())
    for {
      address <- addresses
      size    <- List(0, 1, 4, 32, 33, Int.MaxValue)
    } assertEquals(
      index.find(GuestAddress(address), size.unsafe[Int >= 0]),
      scan(aliased = true, address, size).orElse(scan(aliased = false, address, size)),
      s"$address / $size"
    )
  }
}
