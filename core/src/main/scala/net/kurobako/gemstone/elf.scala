package net.kurobako.gemstone

import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

/** Little-endian ELF64 images. Section contents stay in the source image and are sliced on request. */
object elf {
  def hasMagic(bytes: Vec[Byte]): Boolean =
    bytes.size >= 4 && bytes(0) == 0x7f && bytes(1) == 'E' && bytes(2) == 'L' && bytes(3) == 'F'

  object ImageType {
    inline val Relocatable = 1
  }

  object SectionType {
    inline val ProgramBits          = 1
    inline val SymbolTable          = 2
    inline val StringTable          = 3
    inline val RelocationWithAddend = 4
    inline val Note                 = 7
    inline val NoBits               = 8
    inline val Relocation           = 9
    inline val DynamicSymbolTable   = 11
  }

  object SectionFlag {
    inline val Allocate   = 2L
    inline val Executable = 4L
  }

  object SectionIndex {
    inline val Undefined = 0
    inline val Absolute  = 0xfff1
  }

  object SymbolKind {
    inline val Object   = 1
    inline val Function = 2
  }

  case class Header(
      imageType: Int in 0 ~ 65535,
      machine: Int in 0 ~ 65535,
      abiVersion: Int in 0 ~ 255,
      flags: Int,
      names: Int in 0 ~ 65535
  )

  case class Section(
      index: Int >= 0,
      name: String,
      sectionType: Int,
      flags: Long,
      address: Long,
      offset: Int >= 0,
      size: Int >= 0,
      link: Int >= 0,
      info: Int,
      entrySize: Int >= 0
  ) {
    def isRelocation: Boolean = sectionType == SectionType.RelocationWithAddend || sectionType == SectionType.Relocation
  }

  case class Symbol(
      name: Option[NonEmpty[String]],
      kind: Int in 0 ~ 15,
      section: Int in 0 ~ 65535,
      value: Long,
      size: Int >= 0
  )

  case class Relocation(offset: Long, symbol: Long >= 0, kind: Long >= 0, addend: Option[Long])

  object Image {
    def parse(bytes: Vec[Byte]): Either[NonEmpty[String], Image] = for {
      header   <- elf.header(bytes)
      headers  <- sectionHeaders(bytes)
      names    <- sectionNames(bytes, header, headers)
      sections <- headers.zip(names).traverse { case (raw, name) =>
        raw.link.refine[Int >= 0].leftMap(_ => s"section $name has an invalid link": NonEmpty[String])
          .map(link =>
            Section(
              raw.index,
              name,
              raw.sectionType,
              raw.flags,
              raw.address,
              raw.offset,
              raw.size,
              link,
              raw.info,
              raw.entrySize
            )
          )
      }
      symbolTables <- sections.filter(section =>
        section.sectionType == SectionType.SymbolTable || section.sectionType == SectionType.DynamicSymbolTable
      ).traverse { table =>
        sections.lift(table.link.raw).filter(_.sectionType == SectionType.StringTable)
          .toRight[NonEmpty[String]](s"symbol table ${table.name} has no string table")
          .flatMap(strings => symbols(slice(bytes, table), slice(bytes, strings), table.entrySize))
          .map(table.index.raw -> _)
      }
    } yield Image(bytes, header, sections, symbolTables.toMap)
  }

  case class Image private (
      bytes: Vec[Byte],
      header: Header,
      sections: Vec[Section],
      symbolTables: Map[Int, Vec[Symbol]]
  ) {
    def section(name: String): Option[Section] = sections.find(_.name == name)

    def contents(section: Section): Vec[Byte] = slice(bytes, section)

    /** The static symbol table, or the dynamic one when the image is stripped. */
    def symbols: Option[Vec[Symbol]] =
      sections.find(_.sectionType == SectionType.SymbolTable)
        .orElse(sections.find(_.sectionType == SectionType.DynamicSymbolTable))
        .flatMap(table => symbolTables.get(table.index.raw))

    def relocations(table: Section): Either[NonEmpty[String], Vec[Relocation]] =
      elf.relocations(contents(table), table.sectionType == SectionType.RelocationWithAddend, table.entrySize)

    /** Offset of `symbol` within its section: values are section offsets in relocatable images and addresses otherwise.
      */
    def symbolOffset(symbol: Symbol): Either[NonEmpty[String], (Section, Int >= 0)] = for {
      section  <- sections.lift(symbol.section.raw).toRight[NonEmpty[String]]("symbol has an invalid section index")
      relative <- intValue(
        if (header.imageType.raw == ImageType.Relocatable) symbol.value else symbol.value - section.address,
        "symbol section offset"
      )
      _ <- Either.cond(
        relative.raw <= section.size.raw && symbol.size.raw <= section.size.raw - relative.raw,
        (),
        s"symbol ${symbol.name.getOrElse("")} exceeds section ${section.name}": NonEmpty[String]
      )
    } yield section -> relative

    def symbolBytes(symbol: Symbol): Either[NonEmpty[String], Vec[Byte]] =
      symbolOffset(symbol).map { case (section, relative) =>
        if (section.sectionType == SectionType.NoBits) Vec.fill(symbol.size.raw)(0.toByte)
        else bytes.slice(section.offset.raw + relative.raw, section.offset.raw + relative.raw + symbol.size.raw)
      }
  }

  def header(bytes: Vec[Byte]): Either[NonEmpty[String], Header] =
    if (bytes.size < 64 || !hasMagic(bytes) || bytes(4) != 2 || bytes(5) != 1)
      Left("expected a little-endian ELF64 image")
    else
      Right(Header(
        LittleEndian.decodeUInt16(bytes, 16),
        LittleEndian.decodeUInt16(bytes, 18),
        Bits.low8Int(bytes(8).toLong),
        LittleEndian.decodeInt32(bytes, 48),
        LittleEndian.decodeUInt16(bytes, 62)
      ))

  def intValue(value: Long, label: String): Either[NonEmpty[String], Int >= 0] =
    value.refine[Long in 0L ~ 2147483647L].map[Int >= 0](_.toIntExact)
      .left.map[NonEmpty[String]](_ => s"$label exceeds supported range")

  def stringAt(bytes: Vec[Byte], offset: Int): Either[NonEmpty[String], String] =
    if (offset < 0 || offset >= bytes.size) Left("invalid ELF string offset")
    else {
      val end = bytes.indexOf(0.toByte, offset)
      if (end < offset) Left("unterminated ELF string")
      else string.UTF8.decode(bytes.slice(offset, end)).left.map(_.message)
    }

  private val SymbolEntrySize = 24

  def symbols(bytes: Vec[Byte], strings: Vec[Byte], entrySize: Int >= 0): Either[NonEmpty[String], Vec[Symbol]] = {
    val stride = if (entrySize.raw == 0) SymbolEntrySize else entrySize.raw
    if (stride < SymbolEntrySize || bytes.size % stride != 0) Left("invalid ELF64 symbol table stride")
    else
      Vec.tabulate(bytes.size / stride) { index =>
        val start = index * stride
        for {
          name <- stringAt(strings, LittleEndian.decodeInt32(bytes, start))
          size <- intValue(LittleEndian.decodeInt64(bytes, start + 16), "symbol size")
        } yield Symbol(
          name.refine[NonEmpty[String]].toOption,
          (bytes(start + 4) & 15).unsafe[Int in 0 ~ 15],
          LittleEndian.decodeUInt16(bytes, start + 6),
          LittleEndian.decodeInt64(bytes, start + 8),
          size
        )
      }.sequence
  }

  def relocations(
      bytes: Vec[Byte],
      explicitAddend: Boolean,
      entrySize: Int >= 0
  ): Either[NonEmpty[String], Vec[Relocation]] = {
    val width  = if (explicitAddend) 24 else 16
    val stride = if (entrySize.raw == 0) width else entrySize.raw
    if (stride < width || bytes.size % stride != 0) Left("invalid ELF64 relocation table size")
    else
      Right(Vec.tabulate(bytes.size / stride) { index =>
        val start = index * stride
        val info  = LittleEndian.decodeInt64(bytes, start + 8)
        Relocation(
          LittleEndian.decodeInt64(bytes, start),
          Bits.high32(info).unwrap.widen[Long >= 0],
          Bits.low32(info).unwrap.widen[Long >= 0],
          Option.when(explicitAddend)(LittleEndian.decodeInt64(bytes, start + 16))
        )
      })
  }

  private case class RawSection(
      index: Int >= 0,
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

  private def slice(bytes: Vec[Byte], section: Section): Vec[Byte] =
    if (section.sectionType == SectionType.NoBits) Vec.empty
    else bytes.slice(section.offset.raw, section.offset.raw + section.size.raw)

  private def sectionNames(
      bytes: Vec[Byte],
      header: Header,
      sections: Vec[RawSection]
  ): Either[NonEmpty[String], Vec[String]] =
    if (sections.isEmpty) Right(Vec.empty)
    else
      sections.lift(header.names.raw).filter(_.sectionType == SectionType.StringTable)
        .toRight[NonEmpty[String]]("invalid ELF section-name table index")
        .flatMap { table =>
          val names = bytes.slice(table.offset.raw, table.offset.raw + table.size.raw)
          sections.traverse(section => stringAt(names, section.nameOffset))
        }

  private def sectionHeaders(bytes: Vec[Byte]): Either[NonEmpty[String], Vec[RawSection]] = {
    val tableOffset = LittleEndian.decodeInt64(bytes, 40)
    val entrySize   = LittleEndian.decodeUInt16(bytes, 58).raw
    val count       = LittleEndian.decodeUInt16(bytes, 60)
    val tableSize   = entrySize.toLong * count.raw.toLong
    if (tableOffset == 0 && count.raw == 0) Right(Vec.empty)
    else if (entrySize < 64 || count.raw == 0 || !ByteRange.contains(bytes.size, tableOffset, tableSize))
      Left("invalid ELF section table")
    else
      Vec.indices(count.widen[Int >= 0]).traverse { index =>
        val start       = Math.toIntExact(tableOffset + index.raw.toLong * entrySize)
        val sectionType = LittleEndian.decodeInt32(bytes, start + 4)
        val offset      = LittleEndian.decodeInt64(bytes, start + 24)
        val size        = LittleEndian.decodeInt64(bytes, start + 32)
        for {
          checkedOffset <- intValue(offset, "section offset")
          checkedSize   <- intValue(size, "section size")
          checkedStride <- intValue(LittleEndian.decodeInt64(bytes, start + 56), "section entry size")
          _             <- Either.cond(
            sectionType == SectionType.NoBits || ByteRange.contains(bytes.size, offset, size),
            (),
            s"ELF section $index exceeds the image": NonEmpty[String]
          )
        } yield RawSection(
          index,
          LittleEndian.decodeInt32(bytes, start),
          sectionType,
          LittleEndian.decodeInt64(bytes, start + 8),
          LittleEndian.decodeInt64(bytes, start + 16),
          checkedOffset,
          checkedSize,
          LittleEndian.decodeInt32(bytes, start + 40),
          LittleEndian.decodeInt32(bytes, start + 44),
          checkedStride
        )
      }
  }
}
