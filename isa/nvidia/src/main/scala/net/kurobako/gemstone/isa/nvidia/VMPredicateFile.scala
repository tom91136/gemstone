package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

object VMPredicateFile {
  type Slot = Int in 0 ~ 1023

  def slot(name: NonEmpty[String]): Option[Slot] = {
    val index = indexRaw(name)
    if (index < 0) None else Some(index.unsafe[Slot])
  }
  private val DefinedTrue  = Some(true)
  private val DefinedFalse = Some(false)

  def index(name: NonEmpty[String]): Int = indexRaw(name)

  def indexRaw(name: String): Int = {
    val index = VMRegisterFile.indexRaw(name)
    if (index >= 5120 && index < 6144) index - 5120 else -1
  }
}

/** Predicate bits retain a separate defined mask so a false predicate is distinct from an undefined one. Allocate dense
  * storage only when a numbered predicate is first written: many kernels never use predicates.
  */
// The nullable backing array is a measured lazy-allocation sentinel on predicate accesses.
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
class VMPredicateFile {
  private var bits: Array[Long] | Null                   = null
  private var named: mutable.Map[String, Boolean] | Null = null

  def get(name: NonEmpty[String]): Option[Boolean] = {
    val index = VMPredicateFile.indexRaw(name)
    if (index >= 0) getSlotRaw(index)
    else named match {
      case null   => None
      case values => values.get(name)
    }
  }

  inline def getSlot(index: VMPredicateFile.Slot): Option[Boolean] = getSlotRaw(index.raw)

  inline def updateSlot(index: VMPredicateFile.Slot, value: Boolean): Unit = updateSlotRaw(index.raw, value)

  // Instruction fast paths call the raw entry points after excluding the name lookup's -1 sentinel.
  def getSlotRaw(index: Int): Option[Boolean] = {
    val word = index >>> 6
    val bit  = 1L << (index & 63)
    bits match {
      case null                                => None
      case values if (values(word) & bit) == 0 => None
      case values => if ((values(word + 16) & bit) != 0L) VMPredicateFile.DefinedTrue else VMPredicateFile.DefinedFalse
    }
  }

  def apply(name: NonEmpty[String]): Boolean = get(name).getOrElse(false)

  def update(name: NonEmpty[String], value: Boolean): Unit = {
    val index = VMPredicateFile.indexRaw(name)
    if (index >= 0) updateSlotRaw(index, value)
    else {
      val values = named match {
        case null =>
          val allocated = mutable.Map.empty[String, Boolean]
          named = allocated
          allocated
        case existing => existing
      }
      values(name) = value
    }
  }

  def updateSlotRaw(index: Int, value: Boolean): Unit = {
    val values = bits match {
      case null =>
        val allocated = Array.ofDim[Long](32)
        bits = allocated
        allocated
      case allocated => allocated
    }
    val word = index >>> 6
    val bit  = 1L << (index & 63)
    values(word) |= bit
    if (value) values(word + 16) |= bit
    else values(word + 16) &= ~bit
  }

  def clear(): Unit = {
    bits match {
      case null   => ()
      case values => java.util.Arrays.fill(values, 0L)
    }
    named match {
      case null   => ()
      case values => values.clear()
    }
  }

}
