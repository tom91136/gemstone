package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*
import net.kurobako.gemstone.binary.Bits
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian

import cats.syntax.either.*
import cats.syntax.traverse.*

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; decoder and wave state

enum CodeObjectError {
  case Invalid(detail: String)
  case Unsupported(detail: String)

  def message: String = this match {
    case Invalid(detail)     => detail
    case Unsupported(detail) => detail
  }
}

/** One loadable kernel extracted from an ELF64 AMDGPU code object. */
final case class CodeObjectKernel private[amdgpu] (
    name: String,
    target: Target,
    kernargSize: Int,
    groupSegmentSize: Int,
    privateSegmentSize: Int,
    computePgmRsrc2: Int,
    kernelCodeProperties: Int,
    code: Col[Byte],
    codeAddress: Long = 0L,
    functionTargets: Map[Long, String] = Map.empty,
    readOnlyAddress: Long = 0L,
    private[amdgpu] val readOnly: Array[Byte] = Array.emptyByteArray,
    /** Complete executable text for linked objects; raw kernels intentionally leave this empty. */
    textAddress: Long = 0L,
    private[amdgpu] val text: Col[Byte] = Col.empty,
    private[amdgpu] val functionSizes: Map[Long, Int] = Map.empty,
    /** Device globals share these mutable section images across all kernels in one code object. */
    private[amdgpu] val globalMemory: Array[(Long, Array[Byte])] = Array.empty
)

/** Strict, dependency-free ELF boundary for AMDGPU code objects. */
@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.return",
    "scalafix:DisableSyntax.var",
    "scalafix:GemstoneRuntimeCollections.mutable-collection"
  )
)
object CodeObject {
  private final case class Section(
      nameOffset: Int,
      sectionType: Int,
      flags: Long,
      address: Long,
      offset: Int,
      size: Int,
      link: Int,
      entrySize: Int
  )
  private final case class Symbol(
      name: String,
      value: Long,
      size: Int,
      section: Int,
      kind: Int,
      inferredSize: Boolean = false
  )
  private final case class KernelRecord(
      symbol: Symbol,
      codeStart: Int,
      kernargSize: Int,
      groupSegmentSize: Int,
      privateSegmentSize: Int,
      computePgmRsrc2: Int,
      kernelCodeProperties: Int
  )

  /** Validated AMDGPU code-object index. Kernel bytes and read-only data are copied only when a named kernel is first
    * requested; successful materializations are shared by concurrent callers.
    */
  final class Index private[CodeObject] (
      val target: Target,
      private val bytes: Array[Byte],
      private val records: Map[String, KernelRecord],
      private val functionTargets: Map[Long, String],
      private val functionSizes: Map[Long, Int],
      private val textSection: Section,
      private val readOnlySection: Option[Section],
      private val globalSections: Map[Int, Section],
      private val globalSymbols: Map[String, Symbol],
      private val globalRelocations: Col[(Long, Long)]
  ) {
    private val materialized   = ConcurrentHashMap[String, CodeObjectKernel]()
    private lazy val textImage = java.util.Arrays.copyOfRange(
      bytes,
      textSection.offset,
      textSection.offset + textSection.size
    )
    private lazy val readOnly = readOnlySection.fold(0L -> Array.emptyByteArray) { section =>
      globalSections.iterator.collectFirst {
        case (index, candidate) if candidate == section => candidate.address -> globalImages(index)
      }.getOrElse(section.address -> Array.emptyByteArray)
    }
    private lazy val globalImages: Map[Int, Array[Byte]] = {
      val images = globalSections.iterator.map { case (index, section) =>
        val image =
          if (section.sectionType == 8) Array.ofDim[Byte](section.size)
          else java.util.Arrays.copyOfRange(bytes, section.offset, section.offset + section.size)
        index -> image
      }.toMap
      globalRelocations.foreach { case (address, value) =>
        globalSections.iterator.collectFirst {
          case (index, section)
              if address >= section.address && address - section.address <= section.size.toLong - 8L =>
            LittleEndian.write(value, images(index), (address - section.address).toInt, 8)
        }
      }
      images
    }
    private lazy val globalRegions: Array[(Long, Array[Byte])] =
      globalSections.keysIterator.toArray.sorted.map { index =>
        val section = globalSections(index)
        section.address -> globalImages(index)
      }

    private def materialize(record: KernelRecord): CodeObjectKernel = {
      val codeBytes = java.util.Arrays.copyOfRange(bytes, record.codeStart, record.codeStart + record.symbol.size)
      val code      =
        Col.unsafeFromArray(if (record.symbol.inferredSize) trimTrailingKernelPadding(codeBytes) else codeBytes)
      val (readOnlyAddress, readOnlyBytes) = readOnly
      CodeObjectKernel(
        record.symbol.name,
        target,
        record.kernargSize,
        record.groupSegmentSize,
        record.privateSegmentSize,
        record.computePgmRsrc2,
        record.kernelCodeProperties,
        code,
        record.symbol.value,
        functionTargets,
        readOnlyAddress,
        readOnlyBytes,
        textSection.address,
        Col.unsafeFromArray(textImage),
        functionSizes,
        globalRegions
      )
    }

    val kernelNames: Set[String] = records.keySet

    /** Make a session-local view over the immutable parsed index. Mutable global images and lazy kernel materialization
      * intentionally remain private to the view; the code-object bytes and parsed tables are safe to share.
      */
    private[amdgpu] def fork(): Index =
      Index(
        target,
        bytes,
        records,
        functionTargets,
        functionSizes,
        textSection,
        readOnlySection,
        globalSections,
        globalSymbols,
        globalRelocations
      )

    private[amdgpu] def imageMatches(candidate: Array[Byte]): Boolean =
      bytes.length == candidate.length && java.util.Arrays.equals(bytes, candidate)

    /** Returns the device address and size of a mutable HIP/HSA-visible global. */
    def global(name: String): Option[(Long, Int)] =
      globalSymbols.get(name).flatMap { symbol =>
        globalSections.get(symbol.section).flatMap { section =>
          Option.when(
            symbol.value >= section.address &&
              symbol.value - section.address <= section.size.toLong &&
              symbol.size.toLong <= section.size.toLong - (symbol.value - section.address)
          )(symbol.value -> symbol.size)
        }
      }

    /** Snapshot from this session's relocated section image, before external storage is bound. */
    def globalData(name: String): Either[CodeObjectError, (Long, Col[Byte])] = synchronized {
      global(name).toRight(CodeObjectError.Invalid(s"AMDGPU global symbol $name is not present")).map {
        case (address, size) =>
          val symbol  = globalSymbols(name)
          val section = globalSections(symbol.section)
          val start   = (address - section.address).toInt
          address ->
            Col.unsafeFromArray(java.util.Arrays.copyOfRange(globalImages(symbol.section), start, start + size))
      }
    }

    /** Applies a host-to-device symbol copy to the shared code-object section image. */
    def writeGlobal(name: String, offset: Long, value: Col[Byte]): Either[CodeObjectError, Unit] = synchronized {
      globalSymbols.get(name).toRight(CodeObjectError.Invalid(s"AMDGPU global symbol $name is not present")).flatMap {
        symbol =>
          globalSections.get(
            symbol.section
          ).toRight(CodeObjectError.Invalid(s"AMDGPU global symbol $name has no storage")).flatMap {
            section =>
              globalImages.get(
                symbol.section
              ).toRight(CodeObjectError.Invalid(s"AMDGPU global symbol $name has no image")).flatMap { image =>
                val relative = symbol.value - section.address
                if (
                  relative < 0L || relative > section.size.toLong || symbol.size.toLong > section.size.toLong - relative
                )
                  Left(CodeObjectError.Invalid(s"AMDGPU global symbol $name exceeds its section"))
                else if (offset < 0L || offset > symbol.size.toLong || value.size.toLong > symbol.size.toLong - offset)
                  Left(CodeObjectError.Invalid(
                    s"AMDGPU global write $name offset=$offset size=${value.size} exceeds symbol size ${symbol.size}"
                  ))
                else {
                  val _ = value.copyToArray(image, (relative + offset).toInt, value.size)
                  Right(())
                }
              }
          }
      }
    }

    def kernel(name: String): Either[CodeObjectError, CodeObjectKernel] =
      records.get(name).toRight(CodeObjectError.Invalid(s"AMDGPU code object has no kernel symbol $name")).map {
        record => materialized.computeIfAbsent(name, _ => materialize(record))
      }
  }

  private val TargetPrefix    = "amdgcn-amd-amdhsa--".getBytes(StandardCharsets.US_ASCII)
  private val RawMagic        = "GEMAMD1\u0000".getBytes(StandardCharsets.US_ASCII)
  private val RawHeaderSize   = 40
  private val IndexCacheLimit = 4
  private final case class IndexCacheEntry(size: Int, hash: Long, index: Index)
  private val indexCache     = Array.ofDim[IndexCacheEntry](IndexCacheLimit)
  private val indexCacheLock = Object()
  private var indexCacheSize = 0
  private var indexCacheNext = 0

  private def imageHash(bytes: Array[Byte]): Long = {
    var hash  = 0xcbf29ce484222325L
    var index = 0
    while (index < bytes.length) {
      hash = (hash ^ (bytes(index) & 0xffL)) * 0x100000001b3L
      index += 1
    }
    hash
  }
  private val EmulatedCallKinds = Col(
    "logIf",
    "logId",
    "casinhf",
    "casinhE",
    "cacosf",
    "cacosE",
    "catanf",
    "catanE",
    "cacoshf",
    "cacoshE",
    "catanhf",
    "catanhE",
    "csinhf",
    "ccoshf",
    "ctanhf",
    "csinhE",
    "ccoshE",
    "ctanhE",
    "expIf",
    "expId"
  )
  private val ExactEmulatedCalls = Set("__ockl_get_group_id", "__ockl_get_local_size", "__ockl_get_local_id")
  private def functionCallKinds(name: String): Col[String] =
    if (ExactEmulatedCalls.contains(name)) Col(name)
    else EmulatedCallKinds.filter(name.contains)

  private[amdgpu] def selectFunctionTarget(names: Col[String]): Either[CodeObjectError, String] = {
    val aliases    = names.distinct.sorted
    val recognized = aliases.flatMap(name => functionCallKinds(name).map(_ -> name))
    val kinds      = recognized.map(_._1).distinct
    if (kinds.size > 1)
      Left(
        CodeObjectError.Invalid(s"AMDGPU function aliases have conflicting call semantics: ${aliases.mkString(", ")}")
      )
    else
      kinds.headOption match {
        case Some(kind)                => Right(recognized.collectFirst { case (`kind`, name) => name }.get)
        case None if aliases.size == 1 => Right(aliases.head)
        case None => Left(CodeObjectError.Invalid(s"AMDGPU function aliases are ambiguous: ${aliases.mkString(", ")}"))
      }
  }

  /** Constant-space validation suitable for a lazy loader. Full section, symbol, and kernel validation remains in
    * [[index]].
    */
  def validateHeader(image: Col[Byte]): Either[CodeObjectError, Unit] =
    Either.cond(
      image.size >= 64 &&
        image(0) == 0x7f.toByte && image(1) == 'E' && image(2) == 'L' && image(3) == 'F' &&
        image(4) == 2 && image(5) == 1,
      (),
      CodeObjectError.Invalid("expected a little-endian ELF64 code object")
    ).flatMap(_ =>
      Either.cond(
        (image(18) & 0xff) == 224 && image(19) == 0,
        (),
        CodeObjectError.Invalid("ELF machine is not AMDGPU")
      )
    )

  private def indexBytes(bytes: Array[Byte]): Either[CodeObjectError, Index] =
    for {
      buffer     <- elfBuffer(bytes)
      target     <- findTarget(bytes)
      sections   <- readSections(buffer, bytes.length)
      names      <- sectionNames(bytes, sections, buffer.getShort(62) & 0xffff)
      rawSymbols <- readSymbols(bytes, sections, names)
      symbols = inferFunctionSizes(rawSymbols, sections)
      descriptors <- descriptorIndex(symbols)
      textSection <- sections.zip(names).collectFirst { case (section, ".text") => section }
        .toRight(CodeObjectError.Invalid("AMDGPU code object has no .text section"))
      functionTargets <- symbols.iterator
        .filter(candidate => candidate.kind == 2 && candidate.section > 0 && candidate.size > 0)
        .toCol
        .groupMap(_.value)(_.name)
        .iterator
        .map { case (address, aliases) => selectFunctionTarget(aliases).map(address -> _) }
        .toCol
        .sequence
        .map(_.toMap)
      functionSizes = symbols.iterator
        .filter(symbol => symbol.kind == 2 && symbol.section > 0 && symbol.size > 0)
        .toCol
        .groupMap(_.value)(_.size)
        .iterator
        .map { case (address, sizes) => address -> sizes.max }
        .toMap
      readOnlySection <- sections.zip(names).find { case (_, name) => name == ".rodata" }.map(_._1) match {
        case Some(section) if section.sectionType == 8 =>
          Left(CodeObjectError.Invalid("AMDGPU .rodata section is not file-backed"))
        case section => Right(section)
      }
      records <- symbols.iterator
        // Device-library helpers such as __ockl_* are emitted as local
        // functions in HIP code objects, but only actual dispatchable kernels
        // have the ABI descriptor required to prepare an AQL launch.
        .filter(symbol =>
          symbol.kind == 2 && symbol.size > 0 && symbol.section > 0 && descriptors.contains(s"${symbol.name}.kd")
        )
        .map(symbol =>
          kernelRecord(bytes, sections, names, descriptors, symbol).map(symbol.name -> _)
        )
        .toCol
        .sequence
        .map(_.toMap)
      _ <- Either.cond(records.nonEmpty, (), CodeObjectError.Invalid("AMDGPU code object has no kernel symbols"))
      globalSections = sections.zip(names).zipWithIndex.collect {
        case ((section, name), index)
            if name == ".rodata" || name == ".data" || name == ".bss" || name == ".got" ||
              ((section.flags & 2L) != 0 && (section.sectionType == 1 || section.sectionType == 8)) => index -> section
      }.toMap
      globalSymbols = symbols.iterator.filter { symbol =>
        symbol.kind == 1 && symbol.size > 0 && globalSections.contains(symbol.section)
      }.map(symbol => symbol.name -> symbol).toMap
      globalRelocations = readGlobalRelocations(bytes, sections)
    } yield Index(
      target,
      bytes,
      records,
      functionTargets,
      functionSizes,
      textSection,
      readOnlySection,
      globalSections,
      globalSymbols,
      globalRelocations
    )

  def index(image: Col[Byte]): Either[CodeObjectError, Index] = {
    val bytes = Col.toArray(image)
    val hash  = imageHash(bytes)
    indexCacheLock.synchronized {
      var cached: Index | Null = null
      var position             = 0
      while (position < indexCacheSize && cached == null) {
        val entry = indexCache(position)
        if (entry.size == bytes.length && entry.hash == hash && entry.index.imageMatches(bytes))
          cached = entry.index
        position += 1
      }
      if (cached != null) Right(cached.fork())
      else
        indexBytes(bytes).map { indexed =>
          indexCache(indexCacheNext) = IndexCacheEntry(bytes.length, hash, indexed)
          if (indexCacheSize < IndexCacheLimit) indexCacheSize += 1
          indexCacheNext = (indexCacheNext + 1) % IndexCacheLimit
          indexed.fork()
        }
    }
  }

  def decode(image: Col[Byte]): Either[CodeObjectError, Map[String, CodeObjectKernel]] =
    index(image).flatMap { indexed =>
      indexed.kernelNames.iterator.map(name => indexed.kernel(name).map(name -> _)).toCol.sequence.map(_.toMap)
    }

  private[amdgpu] def inferredFunctionSize(
      value: Long,
      sectionEnd: Long,
      followingValues: Iterable[Long]
  ): Option[Int] = {
    val end = followingValues.iterator.filter(candidate => candidate > value && candidate <= sectionEnd).minOption
      .getOrElse(sectionEnd)
    val size = end - value
    Option.when(value >= 0 && sectionEnd >= value && size > 0 && size <= Int.MaxValue)(size.toInt)
  }

  private def inferFunctionSizes(symbols: Col[Symbol], sections: Col[Section]): Col[Symbol] = {
    val functionValuesBySection = symbols.iterator.filter(symbol => symbol.kind == 2 && symbol.section > 0)
      .toCol
      .groupMap(_.section)(_.value)
    symbols.map { symbol =>
      if (symbol.kind != 2 || symbol.size != 0 || symbol.section <= 0) symbol
      else
        sections.lift(symbol.section).flatMap { section =>
          val sectionEnd = section.address + section.size.toLong
          inferredFunctionSize(symbol.value, sectionEnd, functionValuesBySection(symbol.section))
        }.fold(symbol)(size => symbol.copy(size = size, inferredSize = true))
    }
  }

  private[amdgpu] def trimTrailingKernelPadding(code: Array[Byte]): Array[Byte] = {
    var offset  = 0
    var lastEnd = -1
    while (offset <= code.length - 4) {
      if (littleInt(code, offset) == 0xbf810000) lastEnd = offset + 4
      offset += 4
    }
    if (lastEnd < 0 || lastEnd == code.length) code
    else java.util.Arrays.copyOf(code, lastEnd)
  }

  /** Decode one explicitly described ROCr-internal kernel. These assembly-only ELF objects intentionally have no public
    * AMDGPU metadata or kernel descriptor, so every missing descriptor field is carried in a strict envelope by the
    * pinned virtual ROCr build. The ELF entry symbol and executable bytes are still independently validated.
    */
  def decodeRaw(image: Col[Byte]): Either[CodeObjectError, CodeObjectKernel] = {
    val bytes = Col.toArray(image)
    for {
      _ <- Either.cond(
        bytes.length >= RawHeaderSize && java.util.Arrays.equals(java.util.Arrays.copyOf(bytes, 8), RawMagic),
        (),
        CodeObjectError.Invalid("invalid AMDGPU raw-kernel envelope magic")
      )
      targetSize  <- intValue(unsignedIntAt(bytes, 8), "raw target-name size")
      nameSize    <- intValue(unsignedIntAt(bytes, 12), "raw kernel-name size")
      kernargSize <- intValue(unsignedIntAt(bytes, 16), "raw kernarg size")
      groupSize   <- intValue(unsignedIntAt(bytes, 20), "raw group-segment size")
      privateSize <- intValue(unsignedIntAt(bytes, 24), "raw private-segment size")
      elfSize     <- intValue(unsignedIntAt(bytes, 36), "raw ELF size")
      payloadSize = targetSize.toLong + nameSize.toLong + elfSize.toLong
      _ <- Either.cond(
        targetSize > 0 && nameSize > 0 && elfSize > 0 &&
          ByteRange.contains(bytes.length, RawHeaderSize, payloadSize) &&
          RawHeaderSize.toLong + payloadSize == bytes.length.toLong,
        (),
        CodeObjectError.Invalid("invalid AMDGPU raw-kernel envelope lengths")
      )
      targetName <- ascii(bytes, RawHeaderSize, targetSize, "raw target name")
      name       <- ascii(bytes, RawHeaderSize + targetSize, nameSize, "raw kernel name")
      _          <- Either.cond(
        name.nonEmpty && !name.contains('\u0000'),
        (),
        CodeObjectError.Invalid("invalid AMDGPU raw kernel name")
      )
      target <- Target.fromString(targetName).leftMap(CodeObjectError.Unsupported.apply)
      elfStart = RawHeaderSize + targetSize + nameSize
      elf      = java.util.Arrays.copyOfRange(bytes, elfStart, elfStart + elfSize)
      buffer   <- elfBuffer(elf)
      sections <- readSections(buffer, elf.length)
      names    <- sectionNames(elf, sections, buffer.getShort(62) & 0xffff)
      symbols  <- readSymbols(elf, sections, names)
      symbol   <- symbols.find(candidate => candidate.name == name && candidate.section > 0)
        .toRight(CodeObjectError.Invalid(s"AMDGPU raw kernel has no entry symbol $name"))
      section     <- sections.lift(symbol.section).toRight(CodeObjectError.Invalid(s"invalid section for $name"))
      sectionName <- names.lift(symbol.section).toRight(CodeObjectError.Invalid(s"missing section name for $name"))
      _           <- Either.cond(
        sectionName == ".text" && symbol.value >= section.address,
        (),
        CodeObjectError.Invalid(s"raw kernel $name does not point into .text")
      )
      relative <- intValue(symbol.value - section.address, s"$name section offset")
      _        <- Either.cond(
        relative < section.size,
        (),
        CodeObjectError.Invalid(s"raw kernel $name points past .text")
      )
      start = section.offset + relative
      code  = Col.unsafeFromArray(java.util.Arrays.copyOfRange(elf, start, section.offset + section.size))
    } yield CodeObjectKernel(
      name,
      target,
      kernargSize,
      groupSize,
      privateSize,
      littleInt(bytes, 28),
      littleInt(bytes, 32),
      code
    )
  }

  private def elfBuffer(bytes: Array[Byte]): Either[CodeObjectError, ByteBuffer] =
    validateHeader(Col.unsafeFromArray(bytes)).map(_ => ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN))

  private def readSections(
      buffer: ByteBuffer,
      imageSize: Int
  ): Either[CodeObjectError, Col[Section]] = {
    val tableOffset = buffer.getLong(40)
    val entrySize   = buffer.getShort(58) & 0xffff
    val count       = buffer.getShort(60) & 0xffff
    val tableSize   = entrySize.toLong * count.toLong
    if (entrySize < 64 || count == 0 || !ByteRange.contains(imageSize, tableOffset, tableSize))
      Left(CodeObjectError.Invalid("invalid ELF section table"))
    else
      Col.tabulate(count) { index =>
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
            CodeObjectError.Invalid(s"ELF section $index exceeds the image")
          )
        } yield Section(
          buffer.getInt(start),
          sectionType,
          buffer.getLong(start + 8),
          buffer.getLong(start + 16),
          checkedOffset,
          checkedSize,
          link,
          checkedStride
        )
      }.sequence
  }

  private val RelocationSection = 4
  private val RelocationAbs64   = 3L
  private val SymbolEntrySize   = 24

  /** Absolute 64-bit relocations a linked HIP code object still carries. HIP resolves a device global that is reached
    * through a GOT slot (internal-linkage statics, for example) by storing the symbol address in `.got` at load time,
    * so the slot has to be filled before the first kernel reads it.
    */
  private def readGlobalRelocations(bytes: Array[Byte], sections: Col[Section]): Col[(Long, Long)] = {
    val buffer  = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    val builder = Col.newBuilder[(Long, Long)]
    sections.iterator.filter(section => section.sectionType == RelocationSection).foreach { section =>
      val stride = math.max(24, section.entrySize)
      var index  = 0
      while (index + stride <= section.size) {
        val start  = section.offset + index
        val target = buffer.getLong(start)
        val info   = buffer.getLong(start + 8)
        val addend = buffer.getLong(start + 16)
        val symbol = (info >>> 32).toInt
        val value  = symbolValue(bytes, sections, section.link, symbol)
        if ((info & 0xffffffffL) == RelocationAbs64)
          value.foreach(resolved => builder += (target -> (resolved + addend)))
        index += stride
      }
    }
    builder.result()
  }

  private def symbolValue(bytes: Array[Byte], sections: Col[Section], table: Int, index: Int): Option[Long] =
    sections.lift(table).filter(section => section.entrySize >= SymbolEntrySize).flatMap { section =>
      val start = section.offset + index * SymbolEntrySize
      Option.when(
        start >= 0 && start + SymbolEntrySize <= bytes.length &&
          start + SymbolEntrySize <= section.offset + section.size
      )(
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong(start + 8)
      )
    }

  private def sectionNames(
      bytes: Array[Byte],
      sections: Col[Section],
      index: Int
  ): Either[CodeObjectError, Col[String]] =
    sections.lift(index).toRight(CodeObjectError.Invalid("invalid ELF section-name table index")).flatMap { table =>
      sections.map(section => cString(bytes, table, section.nameOffset)).sequence
    }

  private def readSymbols(
      bytes: Array[Byte],
      sections: Col[Section],
      names: Col[String]
  ): Either[CodeObjectError, Col[Symbol]] =
    sections.zip(names).collectFirst { case (section, ".symtab") => section }
      .toRight(CodeObjectError.Invalid("AMDGPU code object has no symbol table"))
      .flatMap { table =>
        for {
          strings <- sections.lift(table.link).toRight(CodeObjectError.Invalid("invalid ELF symbol string table"))
          _       <- Either.cond(
            table.entrySize >= 24 && table.size % table.entrySize == 0,
            (),
            CodeObjectError.Invalid("invalid ELF symbol-table entry size")
          )
          symbols <- Col.tabulate(table.size / table.entrySize) { index =>
            val start = table.offset + index * table.entrySize
            for {
              name <- cString(bytes, strings, littleInt(bytes, start))
              size <- intValue(littleLong(bytes, start + 16), "symbol size")
            } yield Symbol(
              name,
              littleLong(bytes, start + 8),
              size,
              littleShort(bytes, start + 6),
              bytes(start + 4) & 0x0f
            )
          }.sequence
        } yield symbols
      }

  private def descriptorIndex(symbols: Col[Symbol]): Either[CodeObjectError, Map[String, Symbol]] = {
    val descriptors = mutable.HashMap.empty[String, Symbol]
    val iterator    = symbols.iterator.filter(symbol => symbol.kind == 1 && symbol.name.endsWith(".kd"))
    while (iterator.hasNext) {
      val symbol = iterator.next()
      if (descriptors.contains(symbol.name))
        return Left(CodeObjectError.Invalid(s"duplicate AMDGPU kernel descriptor symbol ${symbol.name}"))
      descriptors.update(symbol.name, symbol)
    }
    Right(descriptors.toMap)
  }

  private def kernelRecord(
      bytes: Array[Byte],
      sections: Col[Section],
      names: Col[String],
      descriptors: Map[String, Symbol],
      symbol: Symbol
  ): Either[CodeObjectError, KernelRecord] =
    for {
      section <- sections.lift(symbol.section).toRight(CodeObjectError.Invalid(s"invalid section for ${symbol.name}"))
      sectionName <-
        names.lift(symbol.section).toRight(CodeObjectError.Invalid(s"missing section name for ${symbol.name}"))
      _ <- Either.cond(
        sectionName == ".text" && symbol.value >= section.address,
        (),
        CodeObjectError.Invalid(s"kernel ${symbol.name} does not point into .text")
      )
      relative <- intValue(symbol.value - section.address, s"${symbol.name} section offset")
      _        <- Either.cond(
        relative <= section.size && symbol.size <= section.size - relative,
        (),
        CodeObjectError.Invalid(s"kernel ${symbol.name} exceeds .text")
      )
      start = section.offset + relative
      descriptorSymbol <- descriptors.get(s"${symbol.name}.kd")
        .toRight(CodeObjectError.Invalid(s"kernel ${symbol.name} has no descriptor symbol"))
      descriptor       <- symbolBytes(bytes, sections, descriptorSymbol, 64, s"kernel ${symbol.name} descriptor")
      kernargSize      <- intValue(Bits.unsigned32(littleInt(descriptor, 8)), s"${symbol.name} kernarg size")
      groupSegmentSize <- intValue(
        Bits.unsigned32(littleInt(descriptor, 0)),
        s"${symbol.name} group-segment size"
      )
      privateSegmentSize <- intValue(
        Bits.unsigned32(littleInt(descriptor, 4)),
        s"${symbol.name} private-segment size"
      )
      computePgmRsrc2      = littleInt(descriptor, 52)
      kernelCodeProperties = littleShort(descriptor, 56)
    } yield KernelRecord(
      symbol,
      start,
      kernargSize,
      groupSegmentSize,
      privateSegmentSize,
      computePgmRsrc2,
      kernelCodeProperties
    )

  private def symbolBytes(
      bytes: Array[Byte],
      sections: Col[Section],
      symbol: Symbol,
      minimumSize: Int,
      label: String
  ): Either[CodeObjectError, Array[Byte]] =
    for {
      section <- sections.lift(symbol.section).toRight(CodeObjectError.Invalid(s"invalid section for $label"))
      _ <- Either.cond(symbol.value >= section.address, (), CodeObjectError.Invalid(s"$label precedes its section"))
      relative <- intValue(symbol.value - section.address, s"$label section offset")
      _        <- Either.cond(
        symbol.size >= minimumSize && relative <= section.size && symbol.size <= section.size - relative,
        (),
        CodeObjectError.Invalid(s"$label is truncated")
      )
      start = section.offset + relative
    } yield java.util.Arrays.copyOfRange(bytes, start, start + symbol.size)

  private def findTarget(bytes: Array[Byte]): Either[CodeObjectError, Target] = {
    var candidate   = 0
    var prefixStart = -1
    val lastStart   = bytes.length - TargetPrefix.length
    while (candidate <= lastStart && prefixStart < 0) {
      var matched = 0
      while (matched < TargetPrefix.length && bytes(candidate + matched) == TargetPrefix(matched)) matched += 1
      if (matched == TargetPrefix.length) prefixStart = candidate
      candidate += 1
    }
    Either.cond(
      prefixStart >= 0,
      prefixStart,
      CodeObjectError.Invalid("AMDGPU target metadata is absent")
    ).flatMap { prefixStart =>
      val valueStart = prefixStart + TargetPrefix.length
      var valueEnd   = valueStart
      var scanning   = true
      while (valueEnd < bytes.length && scanning) {
        val value = bytes(valueEnd) & 0xff
        if (
          (value >= 'a' && value <= 'z') ||
          (value >= 'A' && value <= 'Z') ||
          (value >= '0' && value <= '9') || value == '_'
        ) valueEnd += 1
        else scanning = false
      }
      val name = String(bytes, valueStart, valueEnd - valueStart, StandardCharsets.US_ASCII)
      Target.fromString(name).leftMap(CodeObjectError.Unsupported.apply)
    }
  }

  private def cString(bytes: Array[Byte], table: Section, offset: Int): Either[CodeObjectError, String] =
    if (offset < 0 || offset >= table.size) Left(CodeObjectError.Invalid("ELF string offset is outside its table"))
    else {
      val start = table.offset + offset
      val limit = table.offset + table.size
      var end   = start
      while (end < limit && bytes(end) != 0) end += 1
      Either.cond(
        end < limit,
        String(bytes, start, end - start, StandardCharsets.UTF_8),
        CodeObjectError.Invalid("unterminated ELF string")
      )
    }

  private def ascii(bytes: Array[Byte], offset: Int, size: Int, label: String): Either[CodeObjectError, String] = {
    val valid = (offset until offset + size).forall { index =>
      val value = bytes(index) & 0xff
      value >= 0x21 && value <= 0x7e
    }
    Either.cond(
      valid,
      String(bytes, offset, size, StandardCharsets.US_ASCII),
      CodeObjectError.Invalid(s"$label is not printable ASCII")
    )
  }

  private def unsignedIntAt(bytes: Array[Byte], offset: Int): Long = Bits.unsigned32(littleInt(bytes, offset))

  private def intValue(value: Long, label: String): Either[CodeObjectError, Int] =
    Either.cond(
      value >= 0L && value <= Int.MaxValue.toLong,
      value.toInt,
      CodeObjectError.Unsupported(s"$label is too large")
    )

  private def littleShort(bytes: Array[Byte], offset: Int): Int =
    LittleEndian.decode(bytes, offset, 2).toInt

  private def littleInt(bytes: Array[Byte], offset: Int): Int =
    LittleEndian.decode(bytes, offset, 4).toInt

  private def littleLong(bytes: Array[Byte], offset: Int): Long =
    LittleEndian.decode(bytes, offset, 8)
}
