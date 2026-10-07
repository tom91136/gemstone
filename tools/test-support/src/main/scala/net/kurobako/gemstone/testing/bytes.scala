package net.kurobako.gemstone.testing

import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Byte fixtures: whitespace-insensitive hex text and little-endian packing. */
object bytes {
  private val hexDigits = "0123456789abcdef"

  def hex(text: String): Array[Byte] = {
    val digits = text.filterNot(_.isWhitespace)
    require(digits.length % 2 == 0, "hex fixture must contain complete bytes")
    Array.tabulate[Byte](digits.length / 2) { index =>
      Integer.parseInt(digits.substring(index * 2, index * 2 + 2), 16).toByte
    }
  }

  def toHex(values: Array[Byte]): String =
    if (values.isEmpty) ""
    else
      values.grouped(32).map { group =>
        group.map { byte =>
          val value = byte & 0xff
          s"${hexDigits.charAt(value >>> 4)}${hexDigits.charAt(value & 0xf)}"
        }.mkString
      }.mkString("\n") + "\n"

  private def pack[A](values: Seq[A], width: Int)(put: (ByteBuffer, A) => ByteBuffer): Array[Byte] = {
    val buffer = ByteBuffer.allocate(values.size * width).order(ByteOrder.LITTLE_ENDIAN)
    values.foreach(value => put(buffer, value))
    buffer.array()
  }

  def shorts(values: Int*): Array[Byte] = pack(values, 2)((buffer, value) => buffer.putShort(value.toShort))

  def words(values: Int*): Array[Byte] = pack(values, 4)(_.putInt(_))

  def longs(values: Long*): Array[Byte] = pack(values, 8)(_.putLong(_))

  def floats(values: Float*): Array[Byte] = pack(values, 4)(_.putFloat(_))

  def doubles(values: Double*): Array[Byte] = pack(values, 8)(_.putDouble(_))
}
