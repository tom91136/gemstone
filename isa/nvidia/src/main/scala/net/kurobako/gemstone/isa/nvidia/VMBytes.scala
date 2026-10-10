package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

private object VMBytes {
  private val PageBits = 12
  private val PageSize = 1 << PageBits
  private val PageMask = PageSize.toLong - 1L
}

/** Byte storage over the whole 64-bit address range in fixed pages allocated on first write; unwritten bytes read as
  * zero. A one-page cache serves the common case of consecutive accesses to the same page.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
private class VMBytes {
  import VMBytes.*

  private var pages = mutable.LongMap.empty[Array[Byte]]
  // Pages written since the last clear; clearing zeroes only these and retains no others.
  private var touched                        = mutable.LongMap.empty[Array[Byte]]
  private var cachedIndex                    = 0L
  private var cachedPage: Array[Byte] | Null = null
  // Only a page already recorded as touched may be written through this cache.
  private var writtenIndex                    = 0L
  private var writtenPage: Array[Byte] | Null = null

  private def page(index: Long): Array[Byte] | Null =
    if (cachedPage != null && cachedIndex == index) cachedPage
    else
      pages.getOrNull(index) match {
        case null               => null
        case found: Array[Byte] =>
          cachedIndex = index
          cachedPage = found
          found
      }

  private def writablePage(index: Long): Array[Byte] =
    if (writtenPage != null && writtenIndex == index) writtenPage.nn
    else {
      val target = page(index) match {
        case found: Array[Byte] => found
        case null               =>
          val allocated = Array.ofDim[Byte](PageSize)
          pages(index) = allocated
          allocated
      }
      touched(index) = target
      writtenIndex = index
      writtenPage = target
      target
    }

  def read(address: Long, size: Int in 0 ~ 8): Long = {
    val offset = (address & PageMask).toInt
    if (offset + size.raw <= PageSize) page(address >>> PageBits) match {
      case null               => 0L
      case bytes: Array[Byte] => LittleEndian.decode(bytes, offset, size)
    }
    else LittleEndian.decode(size)(index => readByte(address + index.toLong))
  }

  def write(address: Long, value: Long, size: Int in 0 ~ 8): Unit = {
    val offset = (address & PageMask).toInt
    if (offset + size.raw <= PageSize) LittleEndian.write(value, writablePage(address >>> PageBits), offset, size)
    else LittleEndian.write(value, size)((index, byte) => writeByte(address + index.toLong, byte))
  }

  private def readByte(address: Long): Byte = page(address >>> PageBits) match {
    case null               => 0.toByte
    case bytes: Array[Byte] => bytes((address & PageMask).toInt)
  }

  private def writeByte(address: Long, byte: Byte): Unit =
    writablePage(address >>> PageBits)((address & PageMask).toInt) = byte

  /** Zeroes every byte, keeping only the pages written since the last clear for reuse. */
  def clear(): Unit = {
    touched.foreachValue(bytes => java.util.Arrays.fill(bytes, 0.toByte))
    val kept = touched
    touched = pages
    touched.clear()
    pages = kept
    cachedPage = null
    writtenPage = null
  }
}
