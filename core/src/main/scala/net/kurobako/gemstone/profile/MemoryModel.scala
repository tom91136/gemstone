package net.kurobako.gemstone.profile

import scala.collection.mutable

/** How a request's lane accesses map onto the memory system. Each function takes the request's lanes as parallel arrays
  * of start addresses and byte counts, `count` of them.
  */
object MemoryModel {

  /** The units of `unit` bytes the accesses touch, each counted once however many lanes touch it. */
  def touched(addresses: Array[Long], bytes: Array[Int], count: Int, unit: Int): Int =
    units(addresses, bytes, count, unit).size

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def units(addresses: Array[Long], bytes: Array[Int], count: Int, unit: Int): mutable.LongMap[Unit] = {
    val units = mutable.LongMap.empty[Unit]
    var lane  = 0
    while (lane < count) {
      if (bytes(lane) > 0) {
        var at   = Math.floorDiv(addresses(lane), unit.toLong)
        val last = Math.floorDiv(addresses(lane) + bytes(lane) - 1L, unit.toLong)
        while (at <= last) {
          units.update(at, ())
          at += 1L
        }
      }
      lane += 1
    }
    units
  }

  /** The fewest units of `unit` bytes that could hold the distinct bytes the accesses touch. */
  def ideal(addresses: Array[Long], bytes: Array[Int], count: Int, unit: Int): Int = {
    val distinct = touched(addresses, bytes, count, 1)
    (distinct + unit - 1) / unit
  }

  /** The wavefronts a bank-interleaved memory of `banks` banks of `bankBytes` each needs to serve the accesses: each
    * wavefront reads one word from every bank, and lanes reading the same word share it.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def wavefronts(addresses: Array[Long], bytes: Array[Int], count: Int, banks: Int, bankBytes: Int): Int = {
    val perBank = Array.ofDim[Int](banks)
    units(addresses, bytes, count, bankBytes).foreachKey(word => perBank(Math.floorMod(word, banks.toLong).toInt) += 1)
    perBank.max
  }
}
