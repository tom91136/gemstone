package net.kurobako.gemstone.binary

import net.kurobako.gemstone.Col

object LittleEndian {
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def decode(size: Int)(inline byteAt: Int => Byte): Long = {
    requireScalarSize(size)
    var result = 0L
    var index  = 0
    while (index < size) {
      result |= (byteAt(index).toLong & 0xffL) << (index * 8)
      index += 1
    }
    result
  }

  def decode(bytes: Array[Byte], offset: Int, size: Int): Long = {
    requireRange(bytes.length, offset, size)
    decode(size)(index => bytes(offset + index))
  }

  def decode(bytes: Col[Byte], offset: Int, size: Int): Long = {
    requireRange(bytes.length, offset, size)
    decode(size)(index => bytes(offset + index))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def write(value: Long, size: Int)(inline writeByte: (Int, Byte) => Unit): Unit = {
    requireScalarSize(size)
    var index = 0
    while (index < size) {
      writeByte(index, (value >>> (index * 8)).toByte)
      index += 1
    }
  }

  def write(value: Long, bytes: Array[Byte], offset: Int, size: Int): Unit = {
    requireRange(bytes.length, offset, size)
    write(value, size)((index, byte) => bytes(offset + index) = byte)
  }

  def toArray(value: Long, size: Int): Array[Byte] = {
    requireScalarSize(size)
    val result = Array.ofDim[Byte](size)
    write(value, result, 0, size)
    result
  }

  def toCol(value: Long, size: Int): Col[Byte] = {
    requireScalarSize(size)
    Col.tabulate(size)(index => (value >>> (index * 8)).toByte)
  }

  private inline def requireScalarSize(size: Int): Unit =
    require(size >= 0 && size <= 8, s"little-endian scalar size must be in [0, 8], found $size")

  private def requireRange(length: Int, offset: Int, size: Int): Unit = {
    requireScalarSize(size)
    require(
      ByteRange.contains(length, offset.toLong, size.toLong),
      s"byte range [$offset, ${offset.toLong + size}) exceeds $length"
    )
  }
}
