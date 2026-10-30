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
      entrySize: Int >= 0,
      alignment: Long = 0L
  ) {
    def end: Int = offset.raw + size

    def isRelocation: Boolean = sectionType == SectionType.RelocationWithAddend || sectionType == SectionType.Relocation
  }

  case class Symbol(
      name: Option[NonEmpty[String]],
      kind: Int in 0 ~ 15,
      section: Int in 0 ~ 65535,
      value: Long,
      size: Int >= 0,
      other: Int in 0 ~ 255
  )

  case class Relocation(offset: Long, symbol: Long >= 0, kind: Long >= 0, addend: Option[Long])

  object SegmentType {
    inline val Load = 1
  }

  /** A program header: how part of the file maps into memory. */
  case class Segment(
      index: Int >= 0,
      segmentType: Int,
      flags: Int,
      offset: Long,
      address: Long,
      fileSize: Long,
      memorySize: Long,
      alignment: Long
  )

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
              raw.entrySize,
              raw.alignment
            )
          )
      }
      segments     <- programHeaders(bytes)
      symbolTables <- sections.filter(section =>
        section.sectionType == SectionType.SymbolTable || section.sectionType == SectionType.DynamicSymbolTable
      ).traverse { table =>
        sections.lift(table.link).filter(_.sectionType == SectionType.StringTable)
          .toRight[NonEmpty[String]](s"symbol table ${table.name} has no string table")
          .flatMap(strings => symbols(slice(bytes, table), slice(bytes, strings), table.entrySize))
          .map(table.index.raw -> _)
      }
    } yield Image(bytes, header, sections, segments, symbolTables.toMap)
  }

  case class Image private (
      bytes: Vec[Byte],
      header: Header,
      sections: Vec[Section],
      segments: Vec[Segment],
      symbolTables: Map[Int, Vec[Symbol]]
  ) {
    def section(name: String): Option[Section] = sections.find(_.name == name)

    def contents(section: Section): Vec[Byte] = slice(bytes, section)

    /** The static symbol table, or the dynamic one when the image is stripped. */
    def symbols: Option[Vec[Symbol]] =
      sections.find(_.sectionType == SectionType.SymbolTable)
        .orElse(sections.find(_.sectionType == SectionType.DynamicSymbolTable))
        .flatMap(table => symbolTables.get(table.index))

    def relocations(table: Section): Either[NonEmpty[String], Vec[Relocation]] =
      elf.relocations(contents(table), table.sectionType == SectionType.RelocationWithAddend, table.entrySize)

    /** Offset of `symbol` within its section: values are section offsets in relocatable images and addresses otherwise.
      */
    def symbolOffset(symbol: Symbol): Either[NonEmpty[String], (Section, Int >= 0)] = for {
      section  <- sections.lift(symbol.section).toRight[NonEmpty[String]]("symbol has an invalid section index")
      relative <- intValue(
        if (header.imageType.raw == ImageType.Relocatable) symbol.value else symbol.value - section.address,
        "symbol section offset"
      )
      _ <- Either.cond(
        relative <= section.size && symbol.size <= section.size.raw - relative,
        (),
        s"symbol ${symbol.name.getOrElse("")} exceeds section ${section.name}": NonEmpty[String]
      )
    } yield section -> relative

    /** This image with `section`'s contents replaced by as many bytes, reparsed. Offsets, symbols and relocations are
      * unchanged, so contents they describe must still hold.
      */
    def patched(section: Section, contents: Vec[Byte]): Either[NonEmpty[String], Image] = for {
      _ <- Either.cond(
        sections.lift(section.index).contains(section),
        (),
        s"section ${section.name} is not in this image": NonEmpty[String]
      )
      _ <- Either.cond(
        section.sectionType != SectionType.NoBits,
        (),
        s"section ${section.name} has no contents in the file": NonEmpty[String]
      )
      _ <- Either.cond(
        contents.size == section.size.raw,
        (),
        s"section ${section.name} holds ${section.size} bytes, not ${contents.size}": NonEmpty[String]
      )
      image <- Image.parse(bytes.patch(section.offset, contents, contents.size))
    } yield image

    /** This image with `section` holding `contents` of any size, reparsed. The file contents after the section move by
      * the growth rounded up to keep every later section's and segment's alignment; addresses do not move, so an
      * allocated section can only grow into unused address space and must end its segment. Section, segment and table
      * offsets follow the move; symbols, relocations and other contents that describe the section are the caller's.
      */
    def resized(section: Section, contents: Vec[Byte]): Either[NonEmpty[String], Image] = {
      val growth = contents.size - section.size
      val later  = sections.filter(other => other.index != section.index && other.offset >= section.end)
      val shift  = if (growth <= 0) 0L
      else {
        val alignment = (later.map(_.alignment) ++ segments.filter(_.offset >= section.end).map(_.alignment) :+ 8L)
          .filter(_ > 0).max
        growth.toLong.alignUp(alignment)
      }
      def contains(segment: Segment): Boolean = segment.offset <= section.offset &&
        segment.offset + segment.fileSize >= section.end
      val holding    = segments.filter(contains(_))
      val holdingEnd = holding.foldLeft(Long.MinValue)((end, segment) => end.max(segment.offset + segment.fileSize))
      for {
        _ <- Either.cond(
          sections.lift(section.index).contains(section) && section.sectionType != SectionType.NoBits,
          (),
          s"section ${section.name} has no contents in this image": NonEmpty[String]
        )
        _ <- Either.cond(
          growth <= 0 || section.address == 0L || sections.forall(other =>
            other.index == section.index ||
              (other.flags & SectionFlag.Allocate) == 0 || other.address == 0L ||
              other.address < section.address || other.address >= section.address + contents.size
          ),
          (),
          s"section ${section.name} has no free address space to grow into": NonEmpty[String]
        )
        _ <- Either.cond(
          growth <= 0 ||
            holding.forall(segment => segment.offset + segment.fileSize == section.end) ||
            later.forall(other =>
              other.address == 0L || other.offset >= holdingEnd
            ),
          (),
          s"section ${section.name} does not end its segment": NonEmpty[String]
        )
        _     <- intValue(bytes.size.toLong + shift, "image size")
        image <- Image.parse {
          val result                    = Array.ofDim[Byte](bytes.size + shift.toInt)
          val _                         = bytes.slice(0, section.offset).copyToArray(result, 0)
          val _                         = contents.copyToArray(result, section.offset)
          val _                         = bytes.drop(section.end).copyToArray(result, section.end + shift.toInt)
          def moved(offset: Long): Long = if (offset >= section.end) offset + shift else offset
          LittleEndian.write(moved(LittleEndian.decodeInt64(bytes, 32)), result, 32, 8)
          LittleEndian.write(moved(LittleEndian.decodeInt64(bytes, 40)), result, 40, 8)
          val sectionTable = moved(LittleEndian.decodeInt64(bytes, 40)).toInt
          val sectionEntry = LittleEndian.decodeUInt16(bytes, 58)
          sections.foreach { other =>
            val start = sectionTable + other.index.raw * sectionEntry
            if (other.index == section.index) LittleEndian.write(contents.size.toLong, result, start + 32, 8)
            else LittleEndian.write(moved(other.offset.toLong), result, start + 24, 8)
          }
          val segmentTable = moved(LittleEndian.decodeInt64(bytes, 32)).toInt
          val segmentEntry = LittleEndian.decodeUInt16(bytes, 54)
          segments.foreach { segment =>
            val start = segmentTable + segment.index.raw * segmentEntry
            if (segment.offset >= section.end) LittleEndian.write(segment.offset + shift, result, start + 8, 8)
            else if (growth > 0 && contains(segment)) {
              val extent = if (segment.offset + segment.fileSize == section.end) growth.toLong else shift
              LittleEndian.write(segment.fileSize + extent, result, start + 32, 8)
              LittleEndian.write(segment.memorySize + extent, result, start + 40, 8)
            }
          }
          Vec.unsafeWrapArray(result)
        }
      } yield image
    }

    /** This image with symbol `index` of `table` given `value` and `size`, reparsed. */
    def withSymbol(table: Section, index: Int, value: Long, size: Long): Either[NonEmpty[String], Image] = {
      val stride = if (table.entrySize == 0) SymbolEntrySize else table.entrySize.raw
      for {
        _ <- Either.cond(
          symbolTables.contains(table.index) && index >= 0 && index < symbolTables(table.index).size,
          (),
          s"symbol $index is not in ${table.name}": NonEmpty[String]
        )
        image <- Image.parse {
          val result = Vec.toArray(bytes)
          val start  = table.offset.raw + index * stride
          LittleEndian.write(value, result, start + 8, 8)
          LittleEndian.write(size, result, start + 16, 8)
          Vec.unsafeWrapArray(result)
        }
      } yield image
    }

    def symbolBytes(symbol: Symbol): Either[NonEmpty[String], Vec[Byte]] =
      symbolOffset(symbol).map { case (section, relative) =>
        if (section.sectionType == SectionType.NoBits) Vec.fill(symbol.size)(0.toByte)
        else bytes.slice(section.offset.raw + relative, section.offset.raw + relative + symbol.size)
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
    val stride = if (entrySize == 0) SymbolEntrySize else entrySize.raw
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
          size,
          (bytes(start + 5) & 255).unsafe[Int in 0 ~ 255]
        )
      }.sequence
  }

  def relocations(
      bytes: Vec[Byte],
      explicitAddend: Boolean,
      entrySize: Int >= 0
  ): Either[NonEmpty[String], Vec[Relocation]] = {
    val width  = if (explicitAddend) 24 else 16
    val stride = if (entrySize == 0) width else entrySize.raw
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
      entrySize: Int >= 0,
      alignment: Long
  )

  private def programHeaders(bytes: Vec[Byte]): Either[NonEmpty[String], Vec[Segment]] = {
    val tableOffset = LittleEndian.decodeInt64(bytes, 32)
    val entrySize   = LittleEndian.decodeUInt16(bytes, 54)
    val count       = LittleEndian.decodeUInt16(bytes, 56)
    if (count == 0) Right(Vec.empty)
    else if (entrySize < 56 || !ByteRange.contains(bytes.size, tableOffset, entrySize.toLong * count.toLong))
      Left("invalid ELF program header table")
    else
      Right(Vec.indices(count.widen[Int >= 0]).map { index =>
        val start = Math.toIntExact(tableOffset + index.toLong * entrySize)
        Segment(
          index = index,
          segmentType = LittleEndian.decodeInt32(bytes, start),
          flags = LittleEndian.decodeInt32(bytes, start + 4),
          offset = LittleEndian.decodeInt64(bytes, start + 8),
          address = LittleEndian.decodeInt64(bytes, start + 16),
          fileSize = LittleEndian.decodeInt64(bytes, start + 32),
          memorySize = LittleEndian.decodeInt64(bytes, start + 40),
          alignment = LittleEndian.decodeInt64(bytes, start + 48)
        )
      })
  }

  private def slice(bytes: Vec[Byte], section: Section): Vec[Byte] =
    if (section.sectionType == SectionType.NoBits) Vec.empty
    else bytes.slice(section.offset, section.offset.raw + section.size)

  private def sectionNames(
      bytes: Vec[Byte],
      header: Header,
      sections: Vec[RawSection]
  ): Either[NonEmpty[String], Vec[String]] =
    if (sections.isEmpty) Right(Vec.empty)
    else
      sections.lift(header.names).filter(_.sectionType == SectionType.StringTable)
        .toRight[NonEmpty[String]]("invalid ELF section-name table index")
        .flatMap { table =>
          val names = bytes.slice(table.offset, table.offset.raw + table.size)
          sections.traverse(section => stringAt(names, section.nameOffset))
        }

  private def sectionHeaders(bytes: Vec[Byte]): Either[NonEmpty[String], Vec[RawSection]] = {
    val tableOffset = LittleEndian.decodeInt64(bytes, 40)
    val entrySize   = LittleEndian.decodeUInt16(bytes, 58)
    val count       = LittleEndian.decodeUInt16(bytes, 60)
    val tableSize   = entrySize.toLong * count.toLong
    if (tableOffset == 0 && count == 0) Right(Vec.empty)
    else if (entrySize < 64 || count == 0 || !ByteRange.contains(bytes.size, tableOffset, tableSize))
      Left("invalid ELF section table")
    else
      Vec.indices(count.widen[Int >= 0]).traverse { index =>
        val start       = Math.toIntExact(tableOffset + index.toLong * entrySize)
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
          nameOffset = LittleEndian.decodeInt32(bytes, start),
          sectionType,
          flags = LittleEndian.decodeInt64(bytes, start + 8),
          address = LittleEndian.decodeInt64(bytes, start + 16),
          offset = checkedOffset,
          size = checkedSize,
          link = LittleEndian.decodeInt32(bytes, start + 40),
          info = LittleEndian.decodeInt32(bytes, start + 44),
          entrySize = checkedStride,
          alignment = LittleEndian.decodeInt64(bytes, start + 48)
        )
      }
  }
}
