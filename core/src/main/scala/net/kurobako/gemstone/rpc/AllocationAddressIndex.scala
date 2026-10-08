package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import scala.collection.mutable

private object AllocationAddressIndex {

  class Entry private[AllocationAddressIndex] (val id: AllocationId, val allocation: StoredAllocation) {
    def base: GuestAddress = allocation.base
    def size: Int > 0      = allocation.size

    /** Only for addresses this entry resolved. */
    def offsetOf(address: GuestAddress): Long >= 0 = (address.raw - base.raw).unsafe[Long >= 0]
  }

  @SuppressWarnings(Array("scalafix:BannedSyntax.explicit-new"))
  val Empty: AllocationAddressIndex =
    new AllocationAddressIndex(Array.empty[Entry], Array.empty[Entry], mutable.LongMap.empty, 0)

  /** The number of bases at or below `address`, in unsigned order. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def upperBound(bases: Array[Long], address: Long): Int = {
    var low  = 0
    var high = bases.length
    while (low < high) {
      val middle = (low + high) >>> 1
      if (java.lang.Long.compareUnsigned(bases(middle), address) <= 0) low = middle + 1
      else high = middle
    }
    low
  }

  private def bases(entries: Array[Entry]): Array[Long] = entries.map(_.base.raw)

  private def inserted(entries: Array[Entry], entry: Entry): Array[Entry] = {
    val at     = upperBound(bases(entries), entry.base.raw)
    val result = Array.ofDim[Entry](entries.length + 1)
    Array.copy(entries, 0, result, 0, at)
    result(at) = entry
    Array.copy(entries, at, result, at + 1, entries.length - at)
    result
  }

  private def without(entries: Array[Entry], entry: Entry): Array[Entry] = {
    val at = entries.indexWhere(_ eq entry)
    if (at < 0) entries
    else {
      val result = Array.ofDim[Entry](entries.length - 1)
      Array.copy(entries, 0, result, 0, at)
      Array.copy(entries, at + 1, result, at, entries.length - at - 1)
      result
    }
  }
}

/** Immutable address snapshot, published with the allocation map. Readers need neither locks nor map iterators. Client
  * aliases are disjoint and so are synthetic ranges, but an alias may overlap a synthetic range (CUDA device addresses
  * start at the synthetic base); an alias then takes precedence.
  */
private class AllocationAddressIndex private (
    private val aliases: Array[AllocationAddressIndex.Entry],
    private val synthetic: Array[AllocationAddressIndex.Entry],
    private val byId: mutable.LongMap[AllocationAddressIndex.Entry],
    private val hostBackedCount: Int
) {
  import AllocationAddressIndex.*

  private val aliasBases     = bases(aliases)
  private val syntheticBases = bases(synthetic)

  def hasHostBacked: Boolean = hostBackedCount > 0

  @SuppressWarnings(Array("scalafix:BannedSyntax.explicit-new"))
  def updated(id: AllocationId, allocation: StoredAllocation): AllocationAddressIndex = {
    val previous = removed(id)
    val entry    = Entry(id, allocation)
    val ids      = previous.byId.clone()
    ids.update(id.raw, entry)
    new AllocationAddressIndex(
      if (allocation.aliased) inserted(previous.aliases, entry) else previous.aliases,
      if (allocation.aliased) previous.synthetic else inserted(previous.synthetic, entry),
      ids,
      previous.hostBackedCount + (if (allocation.hostBacked) 1 else 0)
    )
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:BannedSyntax.explicit-new"))
  def removed(id: AllocationId): AllocationAddressIndex = {
    val entry = byId.getOrNull(id.raw)
    if (entry == null) this
    else {
      val ids = byId.clone()
      val _   = ids.remove(id.raw)
      new AllocationAddressIndex(
        without(aliases, entry),
        without(synthetic, entry),
        ids,
        hostBackedCount - (if (entry.allocation.hostBacked) 1 else 0)
      )
    }
  }

  def allocations: Iterable[StoredAllocation] = byId.values.view.map(_.allocation)

  def entry(id: AllocationId): Entry | Null = byId.getOrNull(id.raw)

  /** The entry backing the whole range, preferring an alias; null when none does. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def resolve(address: GuestAddress, size: Int >= 0): Entry | Null = {
    val alias = search(aliases, aliasBases, address.raw, size.raw)
    if (alias != null) alias else search(synthetic, syntheticBases, address.raw, size.raw)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def findAlias(address: GuestAddress, size: Int >= 0): Option[(AllocationId, Long >= 0)] = {
    val entry = search(aliases, aliasBases, address.raw, size.raw)
    if (entry == null) None else Some(entry.id -> entry.offsetOf(address))
  }

  /** Allocation boundaries strictly between `begin` and `end`, unsorted and possibly repeated. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def boundaries(begin: Long, end: Long, into: mutable.ArrayBuilder.ofLong): Unit = {
    def collect(entries: Array[Entry], bases: Array[Long]): Unit = {
      // Ranges are disjoint, so the range before the first base above `begin` is the only earlier one that may cross it.
      var index = math.max(0, upperBound(bases, begin) - 1)
      while (index < entries.length && bases(index) < end) {
        val start = bases(index)
        val limit = start + entries(index).size.raw
        if (start > begin) into += start
        if (limit > begin && limit < end) into += limit
        index += 1
      }
    }
    collect(aliases, aliasBases)
    collect(synthetic, syntheticBases)
  }

  // Among disjoint ranges the last base at or below the address is the candidate, so a zero-width lookup at a shared
  // boundary resolves to the range that starts there.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def search(entries: Array[Entry], bases: Array[Long], address: Long, size: Int): Entry | Null = {
    val candidate = upperBound(bases, address)
    if (candidate == 0) null
    else {
      val entry = entries(candidate - 1)
      if (AddressRanges.containsUnsigned(bases(candidate - 1), entry.size.raw, address, size)) entry else null
    }
  }
}
