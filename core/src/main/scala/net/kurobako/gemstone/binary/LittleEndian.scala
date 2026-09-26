package net.kurobako.gemstone.binary

import net.kurobako.gemstone.Vec

private[gemstone] object LittleEndian {
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def decode(size: ScalarByteWidth)(inline byteAt: Int => Byte): Long = {
    var result = 0L
    var index  = 0
    while (index < size.raw) {
      result |= (byteAt(index).toLong & 0xffL) << (index * 8)
      index += 1
    }
    result
  }

  def decode(bytes: Array[Byte], offset: Int, size: ScalarByteWidth): Long = {
    requireRange(bytes.length, offset, size)
    decode(size)(index => bytes(offset + index))
  }

  def decode(bytes: Vec[Byte], offset: Int, size: ScalarByteWidth): Long = {
    requireRange(bytes.length, offset, size)
    decode(size)(index => bytes(offset + index))
  }

  def decodeInt32(bytes: Array[Byte], offset: Int): Int =
    decode(bytes, offset, ScalarByteWidth.Dword).toInt

  def decodeInt32(bytes: Vec[Byte], offset: Int): Int =
    decode(bytes, offset, ScalarByteWidth.Dword).toInt

  def decodeUnsignedInt16(bytes: Array[Byte], offset: Int): Int =
    decode(bytes, offset, ScalarByteWidth.Word).toInt

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def write(value: Long, size: ScalarByteWidth)(inline writeByte: (Int, Byte) => Unit): Unit = {
    var index = 0
    while (index < size.raw) {
      writeByte(index, (value >>> (index * 8)).toByte)
      index += 1
    }
  }

  def write(value: Long, bytes: Array[Byte], offset: Int, size: ScalarByteWidth): Unit = {
    requireRange(bytes.length, offset, size)
    write(value, size)((index, byte) => bytes(offset + index) = byte)
  }

  /** Decode, replace with `update(previous)`, and return the previous value. */
  def atomicModify(bytes: Array[Byte], offset: Int, size: ScalarByteWidth)(update: Long => Long): Long = {
    val previous = decode(bytes, offset, size)
    write(update(previous), bytes, offset, size)
    previous
  }

  def toArray(value: Long, size: ScalarByteWidth): Array[Byte] = {
    val mutableBytes = Array.ofDim[Byte](size.raw)
    write(value, mutableBytes, 0, size)
    mutableBytes
  }

  def toVec(value: Long, size: ScalarByteWidth): Vec[Byte] =
    Vec.tabulate(size.raw)(index => (value >>> (index * 8)).toByte)

  private def requireRange(
      length: Int,
      offset: Int,
      size: ScalarByteWidth
  ): Unit =
    // Validate the complete dependent range once; a non-negative offset alone could not prove that the access fits.
    require(
      ByteRange.contains(length, offset.toLong, size.raw.toLong),
      s"byte range [$offset, ${offset.toLong + size.raw}) exceeds $length"
    )
}
