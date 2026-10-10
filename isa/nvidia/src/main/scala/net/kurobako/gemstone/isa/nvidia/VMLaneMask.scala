package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*

private object VMLaneMask {
  val MaximumLanes: Int = 1024
  val WordCount: Int    = MaximumLanes / java.lang.Long.SIZE
  val Empty: VMLaneMask = VMLaneMask(Array.fill(WordCount)(0L), 0, 0)

  // Indexed by lane count so the per-instruction size query does not allocate.
  private val PositiveSizes: Array[Option[Int in 1 ~ 1024]] =
    Array.tabulate(MaximumLanes + 1)(count => Option.when(count > 0)(count.unsafe[Int in 1 ~ 1024]))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def all(laneCount: Int in 1 ~ 1024): VMLaneMask = {
    val words = Array.fill(WordCount)(0L)
    var lane  = 0
    while (lane < laneCount.raw) {
      words(lane >>> 6) |= 1L << (lane & 63)
      lane += 1
    }
    VMLaneMask(words, laneCount.widen, (laneCount.raw + 63) >>> 6)
  }

  /** Collect lane groups without allocating an immutable mask for each arrival. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class Builder {
    private val words = Array.ofDim[Long](WordCount)
    private var count = 0
    private var limit = 0

    def add(lane: WorkgroupLane): Unit = {
      val index = lane.raw >>> 6
      val bit   = 1L << (lane.raw & 63)
      if ((words(index) & bit) == 0L) {
        words(index) |= bit
        count += 1
        limit = math.max(limit, index + 1)
      }
    }

    def result(): VMLaneMask =
      if (count == 0) Empty else VMLaneMask(words.clone(), count.unsafe[Int in 0 ~ 1024], limit)
  }

}

/** Compact immutable set of active lanes. */
private class VMLaneMask @scala.annotation.publicInBinary private (
    private val words: Array[Long],
    private val count: Int in 0 ~ 1024,
    private val limit: Int
) {
  def isEmpty: Boolean                      = count.raw == 0
  def nonEmpty: Boolean                     = count.raw != 0
  def positiveSize: Option[Int in 1 ~ 1024] = VMLaneMask.PositiveSizes(count.raw)
  def headOption: Option[WorkgroupLane]     = {
    val wordIndex = words.indexWhere(_ != 0L)
    Option.when(wordIndex >= 0)(WorkgroupLane.fromIndex((wordIndex << 6) +
      java.lang.Long.numberOfTrailingZeros(words(wordIndex))))
  }

  def contains(lane: Int): Boolean =
    lane >= 0 && lane < VMLaneMask.MaximumLanes && (words(lane >>> 6) & (1L << (lane & 63))) != 0L

  def warpBits(lane: Int): Long =
    if (lane < 0 || lane >= VMLaneMask.MaximumLanes) 0L
    else (words(lane >>> 6) >>> (lane & 32)) & 0xffffffffL

  def intersect(other: VMLaneMask): VMLaneMask =
    if (count.raw == 0 || other.count.raw == 0) VMLaneMask.Empty
    else if (this eq other) this
    else combine(other, (left, right) => left & right)

  def union(other: VMLaneMask): VMLaneMask =
    if (count.raw == 0) other
    else if (other.count.raw == 0 || (this eq other)) this
    else combine(other, (left, right) => left | right)

  def subtract(other: VMLaneMask): VMLaneMask =
    if (count.raw == 0 || other.count.raw == 0) this
    else if (this eq other) VMLaneMask.Empty
    else combine(other, (left, right) => left & ~right)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def filterLanes(inline predicate: WorkgroupLane => Boolean): VMLaneMask =
    if (count.raw == 0) VMLaneMask.Empty
    else {
      var filtered = words
      var size     = count.raw
      foreachLane { lane =>
        if (!predicate(lane)) {
          if (filtered eq words) filtered = words.clone()
          filtered(lane.raw >>> 6) &= ~(1L << (lane.raw & 63))
          size -= 1
        }
      }
      if (size == count.raw) this
      else if (size == 0) VMLaneMask.Empty
      else {
        var last = VMLaneMask.WordCount
        while (filtered(last - 1) == 0L) last -= 1
        VMLaneMask(filtered, size.unsafe[Int in 0 ~ 1024], last)
      }
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def foreachLane(inline function: WorkgroupLane => Unit): Unit = {
    var wordIndex = 0
    while (wordIndex < limit) {
      var word = words(wordIndex)
      while (word != 0L) {
        val bit = java.lang.Long.numberOfTrailingZeros(word)
        function(WorkgroupLane.fromIndex((wordIndex << 6) + bit))
        word &= word - 1L
      }
      wordIndex += 1
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private inline def combine(other: VMLaneMask, inline operation: (Long, Long) => Long): VMLaneMask = {
    var combined   = words
    var size       = 0
    var last       = 0
    var index      = 0
    val inputLimit = Math.max(limit, other.limit)
    while (index < inputLimit) {
      val word = operation(words(index), other.words(index))
      if (word != words(index)) {
        if (combined eq words) combined = words.clone()
        combined(index) = word
      }
      size += java.lang.Long.bitCount(word)
      if (word != 0L) last = index + 1
      index += 1
    }
    if (combined eq words) this
    else if (size == 0) VMLaneMask.Empty
    else VMLaneMask(combined, size.unsafe[Int in 0 ~ 1024], last)
  }

  override def equals(other: Any): Boolean = other match {
    case that: VMLaneMask => count.raw == that.count.raw && java.util.Arrays.equals(words, that.words)
    case _                => false
  }

  override def hashCode(): Int = 31 * count.raw + java.util.Arrays.hashCode(words)
}
