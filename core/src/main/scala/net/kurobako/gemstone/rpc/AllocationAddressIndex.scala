package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import scala.collection.immutable
import scala.collection.mutable

private object AllocationAddressIndex {

  object Entry {
    def apply(id: AllocationId, allocation: StoredAllocation): Entry = new Entry(id, allocation)
  }

  class Entry private (val id: AllocationId, val allocation: StoredAllocation) {
    def base: GuestAddress = allocation.base
    def size: Long > 0     = allocation.size

    /** Only for addresses this entry resolved. */
    def offsetOf(address: GuestAddress): Long >= 0 = (address.raw - base.raw).unsafe[Long >= 0]
  }

  type Entries = immutable.Vector[Entry]

  @SuppressWarnings(Array("scalafix:BannedSyntax.explicit-new"))
  val Empty: AllocationAddressIndex =
    new AllocationAddressIndex(Vector.empty, Vector.empty, Vector.empty, 0)

  /** The number of bases at or below `address`, in unsigned order. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def upperBound(entries: Entries, address: Long): Int = {
    var low  = 0
    var high = entries.length
    while (low < high) {
      val middle = (low + high) >>> 1
      if (java.lang.Long.compareUnsigned(entries(middle).base.raw, address) <= 0) low = middle + 1
      else high = middle
    }
    low
  }

  /** The unsigned span `[low, high)` around an address in which no allocation starts or ends, and the entry (or null)
    * that holds all of it.
    */
  case class Run(low: Long, high: Long, entry: Entry | Null)

  private def inserted(entries: Entries, entry: Entry): Entries = {
    val at = upperBound(entries, entry.base.raw)
    if (at == entries.length) entries.appended(entry) else entries.patch(at, Vector(entry), 0)
  }

  private def without(entries: Entries, entry: Entry): Entries = {
    val at = entries.indexWhere(_ eq entry)
    if (at < 0) entries else entries.patch(at, Vector.empty, 1)
  }
}

/** Immutable address snapshot, published with the allocation map. Readers need neither locks nor map iterators. Client
  * aliases are disjoint and so are synthetic ranges, but an alias may overlap a synthetic range (CUDA device addresses
  * start at the synthetic base); an alias then takes precedence.
  *
  * Entries are held by id and in two vectors sorted by base. Ids are dense and start at one, so the id vector indexes
  * them directly while updates share their structure with earlier snapshots.
  */
private class AllocationAddressIndex private (
    private val aliases: AllocationAddressIndex.Entries,
    private val synthetic: AllocationAddressIndex.Entries,
    private val byId: immutable.Vector[AllocationAddressIndex.Entry | Null],
    private val hostBackedCount: Int
) {
  import AllocationAddressIndex.*

  def hasHostBacked: Boolean = hostBackedCount > 0

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:BannedSyntax.explicit-new"))
  def updated(id: AllocationId, allocation: StoredAllocation): AllocationAddressIndex = {
    val previous = removed(id)
    val entry    = Entry(id, allocation)
    val position = (id.raw - 1L).toInt
    new AllocationAddressIndex(
      if (allocation.aliased) inserted(previous.aliases, entry) else previous.aliases,
      if (allocation.aliased) previous.synthetic else inserted(previous.synthetic, entry),
      previous.byId.padTo(position + 1, null).updated(position, entry),
      previous.hostBackedCount + (if (allocation.hostBacked) 1 else 0)
    )
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:BannedSyntax.explicit-new"))
  def removed(id: AllocationId): AllocationAddressIndex = {
    val existing = entry(id)
    if (existing eq null) this
    else
      new AllocationAddressIndex(
        without(aliases, existing),
        without(synthetic, existing),
        byId.updated((id.raw - 1L).toInt, null),
        hostBackedCount - (if (existing.allocation.hostBacked) 1 else 0)
      )
  }

  def allocations: Iterable[StoredAllocation] = byId.view.collect { case entry: Entry => entry.allocation }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def entry(id: AllocationId): Entry | Null =
    if (id.raw <= byId.length.toLong) byId((id.raw - 1L).toInt) else null

  /** The entry backing the whole range, preferring an alias; null when none does. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def resolve(address: GuestAddress, size: Int >= 0): Entry | Null = {
    val alias = search(aliases, address.raw, size)
    if (alias != null) alias else search(synthetic, address.raw, size)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def findAlias(address: GuestAddress, size: Int >= 0): Option[(AllocationId, Long >= 0)] = {
    val entry = search(aliases, address.raw, size)
    if (entry == null) None else Some(entry.id -> entry.offsetOf(address))
  }

  /** The span around `address` over which every lookup resolves to the same entry. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(address: Long): Run = {
    var low                            = 0L
    var high                           = -1L
    def narrow(entries: Entries): Unit = {
      val above = upperBound(entries, address)
      if (above > 0) {
        val start = entries(above - 1).base.raw
        val end   = start + entries(above - 1).size.raw
        if (java.lang.Long.compareUnsigned(start, low) > 0) low = start
        if (java.lang.Long.compareUnsigned(end, address) <= 0) {
          if (java.lang.Long.compareUnsigned(end, low) > 0) low = end
        } else if (java.lang.Long.compareUnsigned(end, high) < 0) high = end
      }
      if (above < entries.length && java.lang.Long.compareUnsigned(entries(above).base.raw, high) < 0)
        high = entries(above).base.raw
    }
    narrow(aliases)
    narrow(synthetic)
    Run(low, high, resolve(GuestAddress(address), 1))
  }

  /** Allocation boundaries strictly between `begin` and `end`, unsorted and possibly repeated. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def boundaries(begin: Long, end: Long, into: mutable.ArrayBuilder.ofLong): Unit = {
    def collect(entries: Entries): Unit = {
      // Ranges are disjoint, so the range before the first base above `begin` is the only earlier one that may cross it.
      var index = math.max(0, upperBound(entries, begin) - 1)
      while (index < entries.length && entries(index).base.raw < end) {
        val start = entries(index).base.raw
        val limit = start + entries(index).size.raw
        if (start > begin) into.addOne(start)
        if (limit > begin && limit < end) into.addOne(limit)
        index += 1
      }
    }
    collect(aliases)
    collect(synthetic)
  }

  // Among disjoint ranges the last base at or below the address is the candidate, so a zero-width lookup at a shared
  // boundary resolves to the range that starts there.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def search(entries: Entries, address: Long, size: Int): Entry | Null = {
    val candidate = upperBound(entries, address)
    if (candidate == 0) null
    else {
      val entry = entries(candidate - 1)
      if (AddressRanges.containsUnsigned(entry.base.raw, entry.size.raw, address, size)) entry else null
    }
  }
}
