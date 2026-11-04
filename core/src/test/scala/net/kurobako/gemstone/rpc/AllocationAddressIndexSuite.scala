package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import scala.util.Random

class AllocationAddressIndexSuite extends Suite {
  private type Allocations = Map[AllocationId, StoredAllocation]

  private val CoarseSizes = List(0, 1, 4, 32, 33, Int.MaxValue)
  private val FineSizes   = List(0, 1, 8)

  private def stored(base: Long, size: Int > 0, aliased: Boolean): StoredAllocation =
    StoredAllocation(StoredAllocation.Storage(size), GuestAddress(base), aliased)

  private def indexed(allocations: Allocations): AllocationAddressIndex =
    allocations.foldLeft(AllocationAddressIndex.Empty)((index, allocation) =>
      index.updated(allocation._1, allocation._2)
    )

  private def find(index: AllocationAddressIndex, address: Long, size: Int): Option[(AllocationId, Long >= 0)] =
    Option(index.resolve(GuestAddress(address), size.unsafe[Int >= 0])).map(entry =>
      entry.id -> entry.offsetOf(GuestAddress(address))
    )

  /** Prefer aliases and, among touching ranges, the range starting at the address. */
  private def scan(allocations: Allocations, address: Long, size: Int): Option[(AllocationId, Long >= 0)] = {
    def among(aliased: Boolean) = allocations.toVector.collect {
      case (id, allocation)
          if allocation.aliased == aliased &&
            java.lang.Long.compareUnsigned(address, allocation.base.raw) >= 0 &&
            address - allocation.base.raw >= 0L && size <= allocation.size &&
            address - allocation.base.raw <= allocation.size.toLong - size =>
        id -> allocation.base.raw
    }.maxByOption(_._2)(using Ordering.fromLessThan[Long](java.lang.Long.compareUnsigned(_, _) < 0))
      .map((id, base) => id -> (address - base).unsafe[Long >= 0])
    among(aliased = true).orElse(among(aliased = false))
  }

  private def probes(allocations: Allocations): Vector[Long] = allocations.values.toVector.flatMap { allocation =>
    val base = allocation.base.raw
    Vector(
      base - 1L,
      base,
      base + allocation.size - 1L,
      base + allocation.size,
      base + allocation.size + 1L
    )
  }

  test("lookups match a scan that prefers aliases and, among touching ranges, the range starting at the address") {
    val random  = Random(42L)
    val aliases = (0 until 64).map(index => stored(0x1000L + index * 32L, 32, aliased = true)) ++ Vector(
      stored(Long.MaxValue - 15L, 16, aliased = true),
      stored(-64L, 32, aliased = true)
    )
    val synthetic   = (0 until 16).map(index => stored(0x1010L + index * 256L, 64, aliased = false))
    val allocations = (aliases ++ synthetic).zipWithIndex.map((allocation, index) =>
      AllocationId((index + 1L).unsafe[Long > 0]) -> allocation
    ).toMap
    val index     = indexed(allocations)
    val addresses = probes(allocations) ++ Vector(0L, Long.MinValue, Long.MaxValue, -1L) ++
      Vector.fill(128)(random.nextLong())
    for {
      address <- addresses
      size    <- CoarseSizes
    } assertEquals(find(index, address, size), scan(allocations, address, size), s"$address / $size")
  }

  test("incremental updates and removals match a scan of the final allocations") {
    val initial = (0 until 32).map(index =>
      AllocationId((index + 1L).unsafe[Long > 0]) -> stored(0x40000L + index * 256L, 64, aliased = false)
    ).toMap
    val changes = initial.toVector.sortBy(_._1.raw).zipWithIndex.map { case ((id, allocation), index) =>
      index % 3 match {
        case 0 => id -> None
        case 1 => id -> Some(allocation.copy(base = GuestAddress(0x1000L + index * 128L), aliased = true))
        case _ => id -> Some(allocation.copy(hostBacked = true))
      }
    }
    val (incremental, expected) = changes.foldLeft((indexed(initial), initial)) {
      case ((index, allocations), (id, Some(allocation))) =>
        (index.updated(id, allocation), allocations.updated(id, allocation))
      case ((index, allocations), (id, None)) => (index.removed(id), allocations.removed(id))
    }
    assertEquals(incremental.hasHostBacked, expected.values.exists(_.hostBacked))
    for {
      address <- probes(initial) ++ probes(expected)
      size    <- FineSizes
    } assertEquals(find(incremental, address, size), scan(expected, address, size), s"$address / $size")
    initial.keys.foreach(id => assertEquals(Option(incremental.entry(id)).map(_.allocation), expected.get(id)))
  }

  test("a run is the span around an address that resolves to one entry and is bounded by allocation edges") {
    val random  = Random(7L)
    val aliases = (0 until 16).map(index => stored(0x1000L + index * 96L, 32, aliased = true)) ++ Vector(
      stored(Long.MaxValue - 15L, 16, aliased = true),
      stored(-64L, 32, aliased = true)
    )
    val synthetic   = (0 until 8).map(index => stored(0x1010L + index * 512L, 64, aliased = false))
    val allocations = (aliases ++ synthetic).zipWithIndex.map((allocation, index) =>
      AllocationId((index + 1L).unsafe[Long > 0]) -> allocation
    ).toMap
    val index     = indexed(allocations)
    val edges     = allocations.values.flatMap(a => Vector(a.base.raw, a.base.raw + a.size)).toSet
    val addresses = probes(allocations) ++ Vector(0L, 1L, Long.MinValue, Long.MaxValue) ++
      Vector.fill(128)(random.nextLong() >>> random.nextInt(60))
    def unsigned(a: Long, b: Long): Int = java.lang.Long.compareUnsigned(a, b)
    for (address <- addresses) {
      val run = index.run(address)
      assert(unsigned(run.low, address) <= 0 && unsigned(address, run.high) < 0, s"$address $run")
      assert(run.low == 0L || edges.contains(run.low), s"$address $run")
      assert(run.high == -1L || edges.contains(run.high), s"$address $run")
      assert(!edges.exists(edge => unsigned(edge, run.low) > 0 && unsigned(edge, run.high) < 0), s"$address $run")
      for (probe <- Vector(run.low, address, run.high - 1L)) {
        val found = scan(allocations, probe, 1)
        assertEquals(Option(run.entry).map(entry => entry.id -> entry.offsetOf(GuestAddress(probe))), found, s"$probe")
      }
    }
  }
}
