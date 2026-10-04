package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

class AllocationAddressIndexSuite extends munit.FunSuite {
  test("indexed lookup preserves scan results for gaps, boundaries, aliases and unsigned addresses") {
    val random                                                  = scala.util.Random(42L)
    val separated: Array[(AllocationId, GuestAddress, Int > 0)] = Array.tabulate(128) { index =>
      (AllocationId((index.toLong + 1L).unsafe[Long > 0]), GuestAddress(0x1000L + index.toLong * 64L), 32)
    }
    val overlapping = separated ++ Array[(AllocationId, GuestAddress, Int > 0)](
      (AllocationId(200L), GuestAddress(0x1008L), 96),
      (AllocationId(201L), GuestAddress(0x1020L), 32),
      (AllocationId(202L), GuestAddress(Long.MaxValue - 15L), 32),
      (AllocationId(203L), GuestAddress(-16L), 32)
    )
    for (entries <- List(separated, overlapping, Array.empty[(AllocationId, GuestAddress, Int > 0)])) {
      val shuffled  = random.shuffle(entries.toVector).toArray
      val index     = AllocationAddressIndex(shuffled)
      val addresses = entries.toVector.flatMap { (_, base, length) =>
        Vector(base.raw - 1L, base.raw, base.raw + length.raw - 1L, base.raw + length.raw, base.raw + length.raw + 1L)
      } ++ Vector(0L, Long.MinValue, Long.MaxValue, -1L) ++ Vector.fill(128)(random.nextLong())
      for {
        address <- addresses
        size    <- List(0, 1, 4, 32, 33, Int.MaxValue)
      } {
        val expected = shuffled.collectFirst {
          case (id, base, length)
              if java.lang.Long.compareUnsigned(address, base.raw) >= 0 && address - base.raw >= 0L &&
                size <= length.raw && address - base.raw <= length.toLong.raw - size.toLong =>
            id -> (address - base.raw).unsafe[Long >= 0]
        }
        assertEquals(index.find(GuestAddress(address), size.unsafe[Int >= 0]), expected, s"$address / $size")
      }
    }
  }
}
