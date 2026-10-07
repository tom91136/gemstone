package net.kurobako.gemstone.binary

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object LittleEndian {
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def decode(size: Int in 0 ~ 8)(inline byteAt: Int => Byte): Long = {
    var result = 0L
    var index  = 0
    while (index < size.raw) {
      result |= (byteAt(index).toLong & 0xffL) << (index * 8)
      index += 1
    }
    result
  }

  def decode(bytes: Array[Byte], offset: Int, size: Int in 0 ~ 8): Long = {
    requireRange(bytes.length, offset, size)
    decode(size)(index => bytes(offset + index))
  }

  def decode(bytes: Vec[Byte], offset: Int, size: Int in 0 ~ 8): Long = {
    requireRange(bytes.length, offset, size)
    decode(size)(index => bytes(offset + index))
  }

  def decodeInt32(bytes: Array[Byte], offset: Int): Int =
    decode(bytes, offset, 4).toInt

  def decodeInt32(bytes: Vec[Byte], offset: Int): Int =
    decode(bytes, offset, 4).toInt

  def decodeInt64(bytes: Array[Byte], offset: Int): Long =
    decode(bytes, offset, 8)

  def decodeInt64(bytes: Vec[Byte], offset: Int): Long =
    decode(bytes, offset, 8)

  def decodeUInt16(bytes: Array[Byte], offset: Int): Int in 0 ~ 65535 =
    Bits.low16Int(decode(bytes, offset, 2))

  def decodeUInt16(bytes: Vec[Byte], offset: Int): Int in 0 ~ 65535 =
    Bits.low16Int(decode(bytes, offset, 2))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def write(value: Long, size: Int in 0 ~ 8)(inline writeByte: (Int, Byte) => Unit): Unit = {
    var index = 0
    while (index < size.raw) {
      writeByte(index, (value >>> (index * 8)).toByte)
      index += 1
    }
  }

  def write(value: Long, bytes: Array[Byte], offset: Int, size: Int in 0 ~ 8): Unit = {
    requireRange(bytes.length, offset, size)
    write(value, size)((index, byte) => bytes(offset + index) = byte)
  }

  /** Decode, replace with `update(previous)`, and return the previous value. */
  def atomicModify(bytes: Array[Byte], offset: Int, size: Int in 0 ~ 8)(update: Long => Long): Long = {
    val previous = decode(bytes, offset, size)
    write(update(previous), bytes, offset, size)
    previous
  }

  def toArray(value: Long, size: Int in 0 ~ 8): Array[Byte] = {
    val mutableBytes = Array.ofDim[Byte](size.raw)
    write(value, mutableBytes, 0, size)
    mutableBytes
  }

  def toVec(value: Long, size: Int in 0 ~ 8): Vec[Byte] =
    Vec.tabulate(size.raw)(index => (value >>> (index * 8)).toByte)

  def toNonEmptyVec(value: Long, size: Int in 1 ~ 8): NonEmpty[Vec[Byte]] =
    Vec.tabulateNonEmpty(size.widen[Int > 0])(index => (value >>> (index.raw * 8)).toByte)

  private def requireRange(
      length: Int,
      offset: Int,
      size: Int in 0 ~ 8
  ): Unit =
    // Validate the complete dependent range once; a non-negative offset alone could not prove that the access fits.
    require(
      offset >= 0 && size.raw >= 0 && offset <= length - size.raw,
      s"range [$offset, ${offset + size.raw}) exceeds bounds [0, $length)"
    )
}
