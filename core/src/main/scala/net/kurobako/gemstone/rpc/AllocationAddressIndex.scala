package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

/** Immutable address snapshot, published with the allocation map. Readers need neither locks nor map iterators. */
private class AllocationAddressIndex(entries: Array[(AllocationId, GuestAddress, Int > 0)]) {
  private val sorted = entries.sortWith((left, right) =>
    java.lang.Long.compareUnsigned(left._2.raw, right._2.raw) < 0
  )
  // Client aliases may overlap synthetic addresses. Preserve the original map's precedence in that unusual case,
  // including zero-width lookups at touching boundaries and ranges that wrap the unsigned address space.
  private val overlapping = sorted.indices.exists { index =>
    val (_, base, size) = sorted(index)
    val end             = base.raw + size.toLong.raw
    java.lang.Long.compareUnsigned(end, base.raw) < 0 ||
    (index + 1 < sorted.length && java.lang.Long.compareUnsigned(sorted(index + 1)._2.raw, end) <= 0)
  }

  private def contains(entry: (AllocationId, GuestAddress, Int > 0), address: GuestAddress, size: Int >= 0): Boolean = {
    val relative = address.raw - entry._2.raw
    java.lang.Long.compareUnsigned(address.raw, entry._2.raw) >= 0 && relative >= 0L &&
    size.raw <= entry._3.raw && relative <= entry._3.toLong.raw - size.raw.toLong
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def find(address: GuestAddress, size: Int >= 0): Option[(AllocationId, Long >= 0)] = {
    var candidate = -1
    if (overlapping) {
      var index = 0
      while (index < entries.length && candidate < 0) {
        if (contains(entries(index), address, size)) candidate = index
        index += 1
      }
    } else {
      var low  = 0
      var high = sorted.length
      while (low < high) {
        val middle = low + (high - low) / 2
        if (java.lang.Long.compareUnsigned(sorted(middle)._2.raw, address.raw) <= 0) low = middle + 1
        else high = middle
      }
      if (low > 0 && contains(sorted(low - 1), address, size)) candidate = low - 1
    }
    if (candidate < 0) None
    else {
      val entry = if (overlapping) entries(candidate) else sorted(candidate)
      Some(entry._1 -> (address.raw - entry._2.raw).unsafe[Long >= 0])
    }
  }
}
