package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

private object AllocationAddressIndex {
  private case class Entry(id: AllocationId, base: GuestAddress, size: Int > 0)

  private def sorted(entries: Iterable[Entry]): Array[Entry] =
    entries.toArray.sortWith((left, right) => java.lang.Long.compareUnsigned(left.base.raw, right.base.raw) < 0)
}

/** Immutable address snapshot, published with the allocation map. Readers need neither locks nor map iterators. Client
  * aliases are disjoint and so are synthetic ranges, but an alias may overlap a synthetic range (CUDA device addresses
  * start at the synthetic base); an alias then takes precedence.
  */
private class AllocationAddressIndex(allocations: Map[AllocationId, StoredAllocation]) {
  import AllocationAddressIndex.*

  private val (aliases, synthetic) = {
    val (aliased, unaliased)                                   = allocations.partition(_._2.aliased)
    def entries(selected: Map[AllocationId, StoredAllocation]) =
      sorted(selected.map((id, allocation) => Entry(id, allocation.base, allocation.size)))
    (entries(aliased), entries(unaliased))
  }

  def find(address: GuestAddress, size: Int >= 0): Option[(AllocationId, Long >= 0)] =
    findAlias(address, size).orElse(search(synthetic, address, size))

  def findAlias(address: GuestAddress, size: Int >= 0): Option[(AllocationId, Long >= 0)] =
    search(aliases, address, size)

  // Among disjoint ranges the last base at or below the address is the candidate, so a zero-width lookup at a shared
  // boundary resolves to the range that starts there.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def search(
      entries: Array[Entry],
      address: GuestAddress,
      size: Int >= 0
  ): Option[(AllocationId, Long >= 0)] = {
    var low  = 0
    var high = entries.length
    while (low < high) {
      val middle = low + (high - low) / 2
      if (java.lang.Long.compareUnsigned(entries(middle).base.raw, address.raw) <= 0) low = middle + 1
      else high = middle
    }
    Option.when(low > 0)(entries(low - 1))
      .filter(entry => AddressRanges.containsUnsigned(entry.base.raw, entry.size.raw, address.raw, size.raw))
      .map(entry => entry.id -> (address.raw - entry.base.raw).unsafe[Long >= 0])
  }
}
