package net.kurobako.gemstone.isa.ptx.v9_3

private object VMLaneMask {
  val MaximumLanes: Int = 1024
  val WordCount: Int    = MaximumLanes / java.lang.Long.SIZE
  val Empty: VMLaneMask = VMLaneMask(Array.fill(WordCount)(0L), 0, 0)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def all(laneCount: Int): Either[VMFault, VMLaneMask] =
    if (laneCount < 1 || laneCount > MaximumLanes)
      Left(VMFault.InvalidProgram(
        s"lane count must be between 1 and $MaximumLanes, found $laneCount"
      ))
    else {
      val words = Array.fill(WordCount)(0L)
      var lane  = 0
      while (lane < laneCount) {
        words(lane >>> 6) |= 1L << (lane & 63)
        lane += 1
      }
      Right(VMLaneMask(words, laneCount, (laneCount + 63) >>> 6))
    }

  def one(lane: Int): Either[VMFault, VMLaneMask] =
    if (lane < 0 || lane >= MaximumLanes)
      Left(VMFault.InvalidProgram(
        s"lane index must be between 0 and ${MaximumLanes - 1}, found $lane"
      ))
    else {
      val words = Array.fill(WordCount)(0L)
      words(lane >>> 6) = 1L << (lane & 63)
      Right(VMLaneMask(words, 1, (lane >>> 6) + 1))
    }

  /** The single-lane mask, or `Empty` when the lane is out of range. */
  def ofLane(lane: Int): VMLaneMask = one(lane).getOrElse(Empty)

  def fromBits(value: Long): VMLaneMask = {
    val words = Array.fill(WordCount)(0L)
    words(0) = value
    VMLaneMask(words, java.lang.Long.bitCount(value), if (value == 0L) 0 else 1)
  }
}

/** Compact immutable set of active PTX lanes. */
private class VMLaneMask private (
    private val words: Array[Long],
    private val count: Int,
    private val limit: Int
) {
  def isEmpty: Boolean             = count == 0
  def nonEmpty: Boolean            = count != 0
  def size: Int                    = count
  def contains(lane: Int): Boolean =
    lane >= 0 && lane < VMLaneMask.MaximumLanes && (words(lane >>> 6) & (1L << (lane & 63))) != 0L

  def warpBits(lane: Int): Long =
    if (lane < 0 || lane >= VMLaneMask.MaximumLanes) 0L
    else (words(lane >>> 6) >>> (lane & 32)) & 0xffffffffL

  def intersect(other: VMLaneMask): VMLaneMask =
    if (count == 0 || other.count == 0) VMLaneMask.Empty
    else combine(other, (left, right) => left & right)

  def union(other: VMLaneMask): VMLaneMask =
    if (count == 0) other
    else if (other.count == 0) this
    else combine(other, (left, right) => left | right)

  def subtract(other: VMLaneMask): VMLaneMask =
    if (count == 0 || other.count == 0) this
    else combine(other, (left, right) => left & ~right)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def filterLanes(predicate: Int => Boolean): VMLaneMask =
    if (count == 0) VMLaneMask.Empty
    else {
      val filtered = Array.fill(VMLaneMask.WordCount)(0L)
      var size     = 0
      foreachLane { lane =>
        if (predicate(lane)) {
          filtered(lane >>> 6) |= 1L << (lane & 63)
          size += 1
        }
      }
      if (size == 0) VMLaneMask.Empty
      else {
        var last = VMLaneMask.WordCount
        while (filtered(last - 1) == 0L) last -= 1
        VMLaneMask(filtered, size, last)
      }
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def foreachLane(inline function: Int => Unit): Unit = {
    var wordIndex = 0
    while (wordIndex < limit) {
      var word = words(wordIndex)
      while (word != 0L) {
        val bit = java.lang.Long.numberOfTrailingZeros(word)
        function((wordIndex << 6) + bit)
        word &= word - 1L
      }
      wordIndex += 1
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def combine(other: VMLaneMask, operation: (Long, Long) => Long): VMLaneMask = {
    val combined   = Array.fill(VMLaneMask.WordCount)(0L)
    var size       = 0
    var last       = 0
    var index      = 0
    val inputLimit = Math.max(limit, other.limit)
    while (index < inputLimit) {
      val word = operation(words(index), other.words(index))
      combined(index) = word
      size += java.lang.Long.bitCount(word)
      if (word != 0L) last = index + 1
      index += 1
    }
    if (size == 0) VMLaneMask.Empty else VMLaneMask(combined, size, last)
  }

  override def equals(other: Any): Boolean = other match {
    case that: VMLaneMask => count == that.count && java.util.Arrays.equals(words, that.words)
    case _                => false
  }

  override def hashCode(): Int = 31 * count + java.util.Arrays.hashCode(words)
}
