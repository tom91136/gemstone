package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.prelude.*

/** What rendered group definitions call that [[net.kurobako.gemstone.semantics.prelude.Lanes]] does not give them. The
  * lanes are the invocations of the scope the instruction names that reached it, `leader` is the index of the first
  * one, and a fold takes the values a view holds for the lanes from an index up to another. `lane` is the index of the
  * first invocation of `scope` whose local id is the given components. Words of a ballot and of a partition hold 32
  * invocations each, word 0 the first 32.
  */
private[isa] trait Groups {
  def leader(): UInt[64]
  def count(): UInt[64]
  def launched(): UInt[64]
  def subgroupLocal(): UInt[64]
  def subgroupSize(): UInt[64]
  def lane(scope: Any, keys: Any*): UInt[64]
  def fold[T](combine: Combine, view: Any, from: Any, until: Any): T
  def foldFirst[T](combine: Combine, view: Any, from: Any, until: Any): T
  def bits(view: Any, first: Any): UInt[32]
  def matching(view: Any, own: Any, first: Any, floating: Option[Int]): UInt[32]
  def matrixMultiply(word: Any, operands: Any*): UInt[32]

  /** Leaf `index` of the line the lane holds: its row of the matrix `leaves`, or its column for use 1, else zero. */
  def ownLeaf(index: Any, rows: Any, columns: Any, use: Any, leaves: Any*): UInt[32]

  /** Component `word` of the matrix whose lines the lanes hold, each owning its row, or its column for use 1. */
  def matrixRows(
      word: Any,
      rows: Any,
      columns: Any,
      use: Any,
      componentBits: Any,
      elementBits: Any,
      lines: Any*
  ): UInt[32]
}
