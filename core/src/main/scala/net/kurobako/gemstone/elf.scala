package net.kurobako.gemstone

import cats.syntax.all.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

object elf {
  def hasMagic(bytes: Vec[Byte]): Boolean =
    bytes.size >= 4 && bytes(0) == 0x7f && bytes(1) == 'E' && bytes(2) == 'L' && bytes(3) == 'F'

  case class Header64(byteOrder: ByteOrder, imageType: Int, machine: Int, abiVersion: Int, flags: Int, names: Int)
  case class Symbol64(nameOffset: Int, kind: Int, section: Int, value: Long, size: Long)

  def header64(bytes: Array[Byte]): Either[NonEmpty[String], Header64] =
    if (bytes.length < 64 || !hasMagic(Vec.unsafeWrapArray(bytes)) || bytes(4) != 2)
      Left("expected an ELF64 image")
    else if (bytes(5) != 1 && bytes(5) != 2) Left("invalid ELF byte order")
    else {
      val order  = if (bytes(5) == 1) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN
      val buffer = ByteBuffer.wrap(bytes).order(order)
      Right(Header64(
        order,
        buffer.getShort(16) & 0xffff,
        buffer.getShort(18) & 0xffff,
        bytes(8) & 0xff,
        buffer.getInt(48),
        buffer.getShort(62) & 0xffff
      ))
    }

  def sectionBytes(image: Array[Byte], section: SectionHeader): Either[NonEmpty[String], Vec[Byte]] =
    if (section.sectionType == 8) Right(Vec.empty)
    else if (!ByteRange.contains(image.length, section.offset.toLong.raw, section.size.toLong.raw))
      Left("ELF section exceeds image")
    else Right(Vec.unsafeWrapArray(image.slice(section.offset.raw, (section.offset + section.size).raw)))

  def stringAt(bytes: Vec[Byte], offset: Int): Either[NonEmpty[String], String] =
    if (offset < 0 || offset >= bytes.size) Left("invalid ELF string offset")
    else {
      val end = bytes.indexOf(0.toByte, offset)
      if (end < offset) Left("unterminated ELF string")
      else string.UTF8.decode(bytes.slice(offset, end)).left.map(_.message)
    }

  def symbols64(bytes: Vec[Byte], byteOrder: ByteOrder, stride: Int = 24): Either[NonEmpty[String], Vec[Symbol64]] =
    if (stride < 24 || bytes.size % stride != 0) Left("invalid ELF64 symbol table stride")
    else {
      val buffer = ByteBuffer.wrap(Vec.toArray(bytes)).order(byteOrder)
      Right(Vec.tabulate(bytes.size / stride) { index =>
        val start = index * stride
        Symbol64(
          buffer.getInt(start),
          buffer.get(start + 4) & 15,
          buffer.getShort(start + 6) & 0xffff,
          buffer.getLong(start + 8),
          buffer.getLong(start + 16)
        )
      })
    }

  case class SectionHeader(
      nameOffset: Int,
      sectionType: Int,
      flags: Long,
      address: Long,
      offset: Int >= 0,
      size: Int >= 0,
      link: Int,
      info: Int,
      entrySize: Int >= 0
  )
  def sections64(bytes: Array[Byte]): Either[NonEmpty[String], Vec[SectionHeader]] =
    header64(bytes).flatMap(header => readSections(ByteBuffer.wrap(bytes).order(header.byteOrder), bytes.length))

  private def intValue(value: Long, name: String): Either[NonEmpty[String], Int >= 0] =
    value.refine[Long in 0L ~ 2147483647L].map[Int >= 0](_.toIntExact)
      .left.map[NonEmpty[String]](_ => s"$name exceeds supported range")

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
