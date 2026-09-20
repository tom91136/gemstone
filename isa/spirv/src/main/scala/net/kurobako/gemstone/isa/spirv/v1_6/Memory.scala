package net.kurobako.gemstone.isa.spirv.v1_6

private[v1_6] object Memory {

  /** One sparse dirty page in SPIR-V's buffered external-memory view. */
  final class DirtyPage {
    val bytes         = Array.ofDim[Byte](4096)
    private val dirty = Array.ofDim[Long](64)

    def update(offset: Int, value: Byte): Unit = {
      bytes(offset) = value
      dirty(offset >>> 6) |= 1L << (offset & 63)
    }

    def contains(offset: Int): Boolean = (dirty(offset >>> 6) & (1L << (offset & 63))) != 0L

    // These scans are on the buffered-memory hot path; mutable cursors avoid iterator allocation.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
    def nextDirtyByte(from: Int): Int = {
      var word = from >>> 6
      if (word >= dirty.length) return bytes.length
      var remaining = dirty(word) & (-1L << (from & 63))
      while (remaining == 0L && word + 1 < dirty.length) {
        word += 1
        remaining = dirty(word)
      }
      (word << 6) + java.lang.Long.numberOfTrailingZeros(remaining)
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
    def nextCleanByte(from: Int): Int = {
      var word = from >>> 6
      if (word >= dirty.length) return bytes.length
      var remaining = ~dirty(word) & (-1L << (from & 63))
      while (remaining == 0L && word + 1 < dirty.length) {
        word += 1
        remaining = ~dirty(word)
      }
      (word << 6) + java.lang.Long.numberOfTrailingZeros(remaining)
    }
  }
}
