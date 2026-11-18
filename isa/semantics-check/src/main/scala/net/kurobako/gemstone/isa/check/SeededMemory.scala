package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import scala.collection.mutable

object SeededMemory {

  /** The byte a cell holds before it is written. */
  trait Initial {
    def apply(address: Long): Byte
  }

  /** The byte at `cell` of the memory the seed `seed` selects. */
  def byte(seed: Long, cell: Long): Byte = {
    val mixed = (seed * 0x9e3779b97f4a7c15L + cell) * 0xbf58476d1ce4e5b9L
    (mixed ^ (mixed >>> 31)).toByte
  }
}

/** Bytes by address, within `mask`, below `limit`, `initial` until written, recording every write; those from `limit`
  * read zero and drop writes.
  */
class SeededMemory(initial: SeededMemory.Initial, limit: Long = Long.MaxValue, mask: Long = -1L) {
  private val written = mutable.LongMap.empty[Byte]

  /** Every byte written, by address. */
  def writes: Map[Long, Byte] = written.toMap

  /** The bytes written to a value other than the one they held, by address. */
  def changes: Vec[(Long, Byte)] =
    Vec.from(written.toSeq.filter((address, value) => value != initial(address)).sortBy(_._1))

  def load(address: Long, size: Int): Long = (0 until size).foldLeft(0L) { (value, index) =>
    val at = (address + index) & mask
    if (at >= limit) value else value | ((written.getOrElse(at, initial(at)) & 0xffL) << (index * 8))
  }

  def store(address: Long, value: Long, size: Int): Unit = (0 until size).foreach { index =>
    val at = (address + index) & mask
    if (at < limit) written(at) = (value >>> (index * 8)).toByte
  }

  def read(address: Long, size: Int): Vec[Byte] =
    Vec.tabulate(size)(index => (load(address + index, 1) & 0xffL).toByte)

  def write(address: Long, bytes: Vec[Byte]): Unit =
    bytes.zipWithIndex.foreach((value, index) => store(address + index, value.toLong, 1))
}
