package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

private object VMPredicateFile {
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
private class VMPredicateFile {
  private var bits: Array[Long] | Null                   = null
  private var named: mutable.Map[String, Boolean] | Null = null

  def get(name: NonEmpty[String]): Option[Boolean] = predicateSlot(name) match {
    case Some(index) => getSlot(index)
    case None        => named match {
        case null   => None
        case values => values.get(name)
      }
  }

  def getSlot(index: Int): Option[Boolean] = {
    val word = index >>> 6
    val bit  = 1L << (index & 63)
    bits match {
      case null                                => None
      case values if (values(word) & bit) == 0 => None
      case values                              => Some((values(word + 16) & bit) != 0L)
    }
  }

  def apply(name: NonEmpty[String]): Boolean = get(name).getOrElse(false)

  def update(name: NonEmpty[String], value: Boolean): Unit = predicateSlot(name) match {
    case Some(index) => updateSlot(index, value)
    case None        =>
      val values = named match {
        case null =>
          val allocated = mutable.Map.empty[String, Boolean]
          named = allocated
          allocated
        case existing => existing
      }
      values(name) = value
  }

  def updateSlot(index: Int, value: Boolean): Unit = {
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

  private def predicateSlot(name: String): Option[Int] = {
    val index = VMPredicateFile.indexRaw(name)
    if (index < 0) None else Some(index)
  }
}
