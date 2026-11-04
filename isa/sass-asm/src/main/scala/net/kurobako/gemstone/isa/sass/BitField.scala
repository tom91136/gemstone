package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import scala.collection.mutable

object BitField {

  class Word(val big: BigInt) {
    val lo: Long = big.toLong
    val hi: Long = (big >> 64).toLong
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class Extractor(val positions: Vec[Int]) {
    private val narrow = positions.size <= 62 && positions.forall(position => position >= 0 && position < 128)
    private val runs: Array[Long] =
      if (!narrow) Array.emptyLongArray
      else {
        val found = mutable.ArrayBuffer.empty[Long]
        var index = 0
        while (index < positions.size) {
          val first  = positions(index)
          var length = 1
          while (
            index + length < positions.size && positions(index + length) == first + length &&
            (first + length) / 64 == first / 64
          ) length += 1
          found += (first / 64).toLong | ((first % 64).toLong << 8) | (length.toLong << 16) | (index.toLong << 24)
          index += length
        }
        found.toArray
      }

    def apply(word: Word): BigInt =
      if (!narrow) extract(word.big, positions)
      else {
        var value = 0L
        var index = 0
        while (index < runs.length) {
          val run    = runs(index)
          val source = if ((run & 0xff) == 0L) word.lo else word.hi
          val length = ((run >>> 16) & 0xff).toInt
          value |= ((source >>> ((run >>> 8) & 0xff).toInt) & ((1L << length) - 1)) << (run >>> 24).toInt
          index += 1
        }
        BigInt(value)
      }
  }

  /** Physical positions are ordered from the least significant logical bit to the most significant. */
  def extract(word: BigInt, positions: Vec[Int]): BigInt =
    positions.zipWithIndex.foldLeft(BigInt(0)) { case (value, (physical, logical)) =>
      if (word.testBit(physical)) value.setBit(logical) else value
    }

  /** The inverse of `extract` for positions that are clear in `word`. */
  def deposit(word: BigInt, positions: Vec[Int], value: BigInt): BigInt =
    positions.zipWithIndex.foldLeft(word) { case (bits, (physical, logical)) =>
      if (value.testBit(logical)) bits.setBit(physical) else bits
    }
}
