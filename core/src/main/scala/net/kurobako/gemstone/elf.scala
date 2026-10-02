package net.kurobako.gemstone

import cats.syntax.all.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object elf {
  case class SectionHeader(
      nameOffset: Int,
      sectionType: Int,
      flags: Long,
      address: Long,
      offset: Int,
      size: Int,
      link: Int,
      info: Int,
      entrySize: Int
  )
  def sections64(bytes: Array[Byte]): Either[NonEmpty[String], Vec[SectionHeader]] =
    if (bytes.length < 64 || bytes(0) != 0x7f || bytes(1) != 'E' || bytes(2) != 'L' || bytes(3) != 'F' || bytes(4) != 2)
      Left("expected an ELF64 image")
    else if (bytes(5) != 1 && bytes(5) != 2) Left("invalid ELF byte order")
    else readSections(
      ByteBuffer.wrap(bytes).order(if (bytes(5) == 1) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN),
      bytes.length
    )

  private def intValue(value: Long, name: String): Either[NonEmpty[String], Int] =
    Either.cond(value >= 0 && value <= Int.MaxValue, value.toInt, s"$name exceeds supported range")

  private def readSections(buffer: ByteBuffer, imageSize: Int): Either[NonEmpty[String], Vec[SectionHeader]] = {
    val tableOffset = buffer.getLong(40)
    val entrySize   = buffer.getShort(58) & 0xffff
    val count       = buffer.getShort(60) & 0xffff
    val tableSize   = entrySize.toLong * count.toLong
    if (tableOffset == 0 && count == 0) Right(Vec.empty)
    else if (entrySize < 64 || count == 0 || !ByteRange.contains(imageSize, tableOffset, tableSize))
      Left("invalid ELF section table")
    else
      Vec.tabulate(count) { index =>
        val start       = Math.toIntExact(tableOffset + index.toLong * entrySize)
        val sectionType = buffer.getInt(start + 4)
        val offset      = buffer.getLong(start + 24)
        val size        = buffer.getLong(start + 32)
        val link        = buffer.getInt(start + 40)
        val stride      = buffer.getLong(start + 56)
        for {
          checkedOffset <- intValue(offset, "section offset")
          checkedSize   <- intValue(size, "section size")
          checkedStride <- intValue(stride, "section entry size")
          _             <- Either.cond(
            sectionType == 8 || ByteRange.contains(imageSize, offset, size),
            (),
            s"ELF section $index exceeds the image": NonEmpty[String]
          )
        } yield SectionHeader(
          buffer.getInt(start),
          sectionType,
          buffer.getLong(start + 8),
          buffer.getLong(start + 16),
          checkedOffset,
          checkedSize,
          link,
          buffer.getInt(start + 44),
          checkedStride
        )
      }.sequence
  }

}
