package net.kurobako.gemstone.isa.amdgpu

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.ByteKey
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.LRUCache
import net.kurobako.gemstone.binary.Bits
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.isa.GlobalName
import net.kurobako.gemstone.memory.GuestAddress

import cats.syntax.either.*
import cats.syntax.traverse.*

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

/** Strict, dependency-free ELF boundary for AMDGPU code objects. */
object CodeObject {
  final case class Program private[CodeObject] (image: Vec[Byte]) extends net.kurobako.gemstone.isa.Program

  object Decoder extends net.kurobako.gemstone.isa.Decoder[Vec[Byte], Program] {
    override type Error = CodeObjectError

    override def decode(image: Vec[Byte]): Either[CodeObjectError, Program] =
      validateHeader(image).map(_ => Program(image))
  }

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
      code: Vec[Byte],
      codeAddress: Long = 0L,
      functionTargets: Map[Long, String] = Map.empty,
      readOnlyAddress: Long = 0L,
      private[amdgpu] val readOnly: Array[Byte] = Array.emptyByteArray,
      /** Complete executable text for linked objects. */
      textAddress: Long = 0L,
      private[amdgpu] val text: Vec[Byte] = Vec.empty,
      private[amdgpu] val functionSizes: Map[Long, Int] = Map.empty,
      /** Device globals share these mutable section images across all kernels in one code object. */
      private[amdgpu] val globalMemory: Array[(Long, Array[Byte])] = Array.empty
  ) {
    private[amdgpu] val globalMemoryLocks: Array[ReentrantLock] =
      Array.fill(globalMemory.length)(ReentrantLock())
  }

  /** Public-symbol information needed by an API facade; decoding and relocation ownership remain in this ISA layer. */
  final case class CodeObjectGlobal(
      name: GlobalName,
      address: GuestAddress,
      size: Int :| Positive0,
      pointerOffsets: Vec[Int :| Positive0]
  )

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
      private val globalSymbols: Map[GlobalName, Symbol],
      private val globalRelocations: Vec[(Long, Long)]
  ) {
    private val materialized    = ConcurrentHashMap[String, CodeObjectKernel]()
    private val globalImageLock = ReentrantLock()
    private lazy val textImage  = java.util.Arrays.copyOfRange(
      bytes,
      textSection.offset,
      textSection.offset + textSection.size
    )
    private lazy val readOnly = readOnlySection.fold(0L -> Array.emptyByteArray) { section =>
      globalSections.view.collectFirst {
        case (index, candidate) if candidate == section => candidate.address -> globalImages(index)
      }.getOrElse(section.address -> Array.emptyByteArray)
    }
    private lazy val globalImages: Map[Int, Array[Byte]] = {
      val images = globalSections.view.map { case (index, section) =>
        val image =
          if (section.sectionType == 8) Array.ofDim[Byte](section.size)
          else java.util.Arrays.copyOfRange(bytes, section.offset, section.offset + section.size)
        index -> image
      }.toMap
      globalRelocations.foreach { case (address, value) =>
        globalSections.view.collectFirst {
          case (index, section)
              if address >= section.address && address - section.address <= section.size.toLong - 8L =>
            LittleEndian.write(
              value,
              images(index),
              (address - section.address).toInt,
              ScalarByteWidth(8)
            )
        }
      }
      images
    }
    private lazy val globalRegions: Array[(Long, Array[Byte])] =
      globalSections.keys.view.toArray.sorted.map { index =>
        val section = globalSections(index)
        section.address -> globalImages(index)
      }

    private def materialize(record: KernelRecord): CodeObjectKernel = {
      val codeBytes = java.util.Arrays.copyOfRange(bytes, record.codeStart, record.codeStart + record.symbol.size)
      val code      =
        Vec.unsafeFromArray(if (record.symbol.inferredSize) trimTrailingKernelPadding(codeBytes) else codeBytes)
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
        Vec.unsafeFromArray(textImage),
        functionSizes,
        globalRegions
      )
    }

    val kernelNames: Set[String] = records.keySet

    val globals: Vec[CodeObjectGlobal] = Vec.from(
      globalSymbols.view.map { case (name, symbol) =>
        val pointerOffsets = globalRelocations.collect {
          case (address, _) if address >= symbol.value && address - symbol.value <= symbol.size.toLong - 8L =>
            (address - symbol.value).toInt
        }.distinct.sorted
        CodeObjectGlobal(
          name,
          GuestAddress(symbol.value),
          symbol.size.refineUnsafe[Positive0],
          pointerOffsets.map(_.refineUnsafe[Positive0])
        )
      }.toSeq.sortBy(global => (global.address.value, global.name.value))
    )

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

    /** Returns the device address and size of a mutable HIP/HSA-visible global. */
    def global(name: GlobalName): Option[(GuestAddress, Int :| Positive0)] =
      globalSymbols.get(name).flatMap { symbol =>
        globalSections.get(symbol.section).flatMap { section =>
          Option.when(
            symbol.value >= section.address &&
              symbol.value - section.address <= section.size.toLong &&
              symbol.size.toLong <= section.size.toLong - (symbol.value - section.address)
          )(GuestAddress(symbol.value) -> symbol.size.refineUnsafe[Positive0])
        }
      }

    /** Snapshot from this session's relocated section image, before external storage is bound. */
    def globalData(name: GlobalName): Either[CodeObjectError, (GuestAddress, Vec[Byte])] = globalImageLock.withLock {
      global(name).toRight(CodeObjectError.Invalid(
        s"AMDGPU global symbol $name is not present"
      )).map {
        case (address, size) =>
          val symbol  = globalSymbols(name)
          val section = globalSections(symbol.section)
          val start   = (address.value - section.address).toInt
          address ->
            Vec.unsafeFromArray(java.util.Arrays.copyOfRange(globalImages(symbol.section), start, start + size))
      }
    }

    /** Applies a host-to-device symbol copy to the shared code-object section image. */
    def writeGlobal(name: GlobalName, offset: Long, value: Vec[Byte]): Either[CodeObjectError, Unit] =
      globalImageLock.withLock {
        globalSymbols.get(name).toRight(CodeObjectError.Invalid(
          s"AMDGPU global symbol $name is not present"
        )).flatMap {
          symbol =>
            globalSections.get(
              symbol.section
            ).toRight(CodeObjectError.Invalid(
              s"AMDGPU global symbol $name has no storage"
            )).flatMap {
              section =>
                globalImages.get(
                  symbol.section
                ).toRight(CodeObjectError.Invalid(
                  s"AMDGPU global symbol $name has no image"
                )).flatMap { image =>
                  val relative = symbol.value - section.address
                  if (
                    relative < 0L || relative > section.size.toLong ||
                    symbol.size.toLong > section.size.toLong - relative
                  )
                    Left(CodeObjectError.Invalid(
                      s"AMDGPU global symbol $name exceeds its section"
                    ))
                  else if (
                    offset < 0L || offset > symbol.size.toLong || value.size.toLong > symbol.size.toLong - offset
                  )
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

    def kernel(name: String): Either[CodeObjectError, CodeObjectKernel] = {
      val missing = s"AMDGPU code object has no kernel symbol $name"
      records.get(name).toRight(CodeObjectError.Invalid(missing)).map { record =>
        materialized.computeIfAbsent(name, _ => materialize(record))
      }
    }
  }

  private val TargetPrefix = "amdgcn-amd-amdhsa--".getBytes(StandardCharsets.US_ASCII)
  private val indexCache   = LRUCache[ByteKey, Either[CodeObjectError, Index]](capacity = 4)

  private val EmulatedCallKinds = Vec(
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
  private def functionCallKinds(name: String): Vec[String] =
    if (ExactEmulatedCalls.contains(name)) Vec(name)
    else EmulatedCallKinds.filter(name.contains(_))

  private[amdgpu] def selectFunctionTarget(names: Vec[String]): Either[CodeObjectError, String] = {
    val aliases    = names.distinct.sorted
    val recognized = aliases.flatMap(name => functionCallKinds(name).map(_ -> name))
    val kinds      = recognized.map(_._1).distinct
    if (kinds.size > 1)
      Left(
        CodeObjectError.Invalid(
          s"AMDGPU function aliases have conflicting call semantics: ${aliases.mkString(", ")}"
        )
      )
    else
      kinds.headOption match {
        case Some(kind)                => Right(recognized.collectFirst { case (`kind`, name) => name }.get)
        case None if aliases.size == 1 => Right(aliases.head)
        case None                      => Left(CodeObjectError.Invalid(
            s"AMDGPU function aliases are ambiguous: ${aliases.mkString(", ")}"
          ))
      }
  }

  /** Constant-space validation suitable for a lazy loader. Full section, symbol, and kernel validation remains in
    * [[index]].
    */
  def validateHeader(image: Vec[Byte]): Either[CodeObjectError, Unit] =
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
      functionTargets <- symbols.view
        .filter(candidate => candidate.kind == 2 && candidate.section > 0 && candidate.size > 0)
        .toVec
        .groupMap(_.value)(_.name)
        .view
        .map { case (address, aliases) => selectFunctionTarget(aliases).map(address -> _) }
        .toVec
        .sequence
        .map(_.toMap)
      functionSizes = symbols.view
        .filter(symbol => symbol.kind == 2 && symbol.section > 0 && symbol.size > 0)
        .toVec
        .groupMap(_.value)(_.size)
        .view
        .map { case (address, sizes) => address -> sizes.max }
        .toMap
      readOnlySection <- sections.zip(names).find { case (_, name) => name == ".rodata" }.map(_._1) match {
        case Some(section) if section.sectionType == 8 =>
          Left(CodeObjectError.Invalid("AMDGPU .rodata section is not file-backed"))
        case section => Right(section)
      }
      records <- symbols.view
        // Device-library helpers such as __ockl_* are emitted as local
        // functions in HIP code objects, but only actual dispatchable kernels
        // have the ABI descriptor required to prepare an AQL launch.
        .filter(symbol =>
          symbol.kind == 2 && symbol.size > 0 && symbol.section > 0 && descriptors.contains(s"${symbol.name}.kd")
        )
        .map(symbol =>
          kernelRecord(bytes, sections, names, descriptors, symbol).map(symbol.name -> _)
        )
        .toVec
        .sequence
        .map(_.toMap)
      _ <- Either.cond(records.nonEmpty, (), CodeObjectError.Invalid("AMDGPU code object has no kernel symbols"))
      globalSections = sections.zip(names).zipWithIndex.collect {
        case ((section, name), index)
            if name == ".rodata" || name == ".data" || name == ".bss" || name == ".got" ||
              ((section.flags & 2L) != 0 && (section.sectionType == 1 || section.sectionType == 8)) => index -> section
      }.toMap
      globalSymbols <- symbols.view.filter { symbol =>
        symbol.kind == 1 && globalSections.contains(symbol.section)
      }.toVec.traverse { symbol =>
        GlobalName.either(symbol.name)
          .leftMap(_ =>
            CodeObjectError.Invalid(
              s"invalid AMDGPU global name '${symbol.name}'"
            )
          )
          .map(_ -> symbol)
      }.map(_.toMap)
      globalRelocations <- readGlobalRelocations(bytes, sections)
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

  def index(image: Vec[Byte]): Either[CodeObjectError, Index] =
    indexCache.get(ByteKey(image))(indexBytes(Vec.toArray(image))).map(_.fork())

  def decode(image: Vec[Byte]): Either[CodeObjectError, Map[String, CodeObjectKernel]] =
    index(image).flatMap { indexed =>
      indexed.kernelNames.view.map(name => indexed.kernel(name).map(name -> _)).toVec.sequence.map(_.toMap)
    }

  private[amdgpu] def inferredFunctionSize(
      value: Long,
      sectionEnd: Long,
      followingValues: Iterable[Long]
  ): Option[Int] = {
    val end = followingValues.view.filter(candidate => candidate > value && candidate <= sectionEnd).minOption
      .getOrElse(sectionEnd)
    val size = end - value
    Option.when(value >= 0 && sectionEnd >= value && size > 0 && size <= Int.MaxValue)(size.toInt)
  }

  private def inferFunctionSizes(symbols: Vec[Symbol], sections: Vec[Section]): Vec[Symbol] = {
    val functionValuesBySection = symbols.view.filter(symbol => symbol.kind == 2 && symbol.section > 0)
      .toVec
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

  // Locate the last terminating instruction in one allocation-free scan.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
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

  private def elfBuffer(bytes: Array[Byte]): Either[CodeObjectError, ByteBuffer] =
    validateHeader(Vec.unsafeFromArray(bytes)).map(_ => ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN))

  private def readSections(
      buffer: ByteBuffer,
      imageSize: Int
  ): Either[CodeObjectError, Vec[Section]] = {
    val tableOffset = buffer.getLong(40)
    val entrySize   = buffer.getShort(58) & 0xffff
    val count       = buffer.getShort(60) & 0xffff
    val tableSize   = entrySize.toLong * count.toLong
    if (entrySize < 64 || count == 0 || !ByteRange.contains(imageSize, tableOffset, tableSize))
      Left(CodeObjectError.Invalid("invalid ELF section table"))
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

  private val RelocationSection   = 4
  private val RelocationAbs64     = 3L
  private val SymbolEntrySize     = 24
  private val RelocationEntrySize = 24

  /** Absolute 64-bit relocations a linked HIP code object still carries. HIP resolves a device global that is reached
    * through a GOT slot (internal-linkage statics, for example) by storing the symbol address in `.got` at load time,
    * so the slot has to be filled before the first kernel reads it.
    */
  // Decode fixed-width relocation records directly from the ELF image.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def readGlobalRelocations(
      bytes: Array[Byte],
      sections: Vec[Section]
  ): Either[CodeObjectError, Vec[(Long, Long)]] = {
    val buffer  = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    val builder = Vec.newBuilder[(Long, Long)]
    var failure = Option.empty[CodeObjectError]
    sections.view.filter(section => section.sectionType == RelocationSection).foreach { section =>
      val stride = math.max(RelocationEntrySize, section.entrySize)
      var index  = 0
      while (failure.isEmpty && index + stride <= section.size) {
        val start  = section.offset + index
        val target = buffer.getLong(start)
        val info   = buffer.getLong(start + 8)
        val addend = buffer.getLong(start + 16)
        val symbol = (info >>> 32).toInt
        if ((info & 0xffffffffL) == RelocationAbs64)
          symbolValue(bytes, sections, section.link, symbol) match {
            case Some(resolved) => builder += (target -> (resolved + addend))
            case None => failure = Some(CodeObjectError.Invalid(s"unresolved AMDGPU relocation symbol $symbol"))
          }
        index += stride
      }
    }
    failure.toLeft(builder.result())
  }

  private def symbolValue(bytes: Array[Byte], sections: Vec[Section], table: Int, index: Int): Option[Long] =
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
      sections: Vec[Section],
      index: Int
  ): Either[CodeObjectError, Vec[String]] =
    sections.lift(index).toRight(CodeObjectError.Invalid("invalid ELF section-name table index")).flatMap { table =>
      sections.map(section => cString(bytes, table, section.nameOffset)).sequence
    }

  private def readSymbols(
      bytes: Array[Byte],
      sections: Vec[Section],
      names: Vec[String]
  ): Either[CodeObjectError, Vec[Symbol]] =
    sections.zip(names).collectFirst { case (section, ".symtab") => section }
      .toRight(CodeObjectError.Invalid("AMDGPU code object has no symbol table"))
      .flatMap { table =>
        for {
          strings <- sections.lift(table.link).toRight(CodeObjectError.Invalid("invalid ELF symbol string table"))
          _       <- Either.cond(
            table.entrySize >= SymbolEntrySize && table.size % table.entrySize == 0,
            (),
            CodeObjectError.Invalid("invalid ELF symbol-table entry size")
          )
          symbols <- Vec.tabulate(table.size / table.entrySize) { index =>
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

  private def descriptorIndex(symbols: Vec[Symbol]): Either[CodeObjectError, Map[String, Symbol]] =
    symbols.view
      .filter(symbol => symbol.kind == 1 && symbol.name.endsWith(".kd"))
      .foldLeft[Either[CodeObjectError, Map[String, Symbol]]](Right(Map.empty)) {
        case (error @ Left(_), _)         => error
        case (Right(descriptors), symbol) =>
          Either.cond(
            !descriptors.contains(symbol.name),
            descriptors.updated(symbol.name, symbol),
            CodeObjectError.Invalid(s"duplicate AMDGPU kernel descriptor symbol ${symbol.name}")
          )
      }

  private def kernelRecord(
      bytes: Array[Byte],
      sections: Vec[Section],
      names: Vec[String],
      descriptors: Map[String, Symbol],
      symbol: Symbol
  ): Either[CodeObjectError, KernelRecord] =
    for {
      section <- sections.lift(symbol.section).toRight(CodeObjectError.Invalid(
        s"invalid section for ${symbol.name}"
      ))
      sectionName <-
        names.lift(symbol.section).toRight(CodeObjectError.Invalid(
          s"missing section name for ${symbol.name}"
        ))
      _ <- Either.cond(
        sectionName == ".text" && symbol.value >= section.address,
        (),
        CodeObjectError.Invalid(
          s"kernel ${symbol.name} does not point into .text"
        )
      )
      relative <- intValue(symbol.value - section.address, s"${symbol.name} section offset")
      _        <- Either.cond(
        relative <= section.size && symbol.size <= section.size - relative,
        (),
        CodeObjectError.Invalid(s"kernel ${symbol.name} exceeds .text")
      )
      start = section.offset + relative
      descriptorSymbol <- descriptors.get(s"${symbol.name}.kd")
        .toRight(CodeObjectError.Invalid(
          s"kernel ${symbol.name} has no descriptor symbol"
        ))
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
      sections: Vec[Section],
      symbol: Symbol,
      minimumSize: Int,
      label: String
  ): Either[CodeObjectError, Array[Byte]] =
    for {
      section <- sections.lift(symbol.section).toRight(CodeObjectError.Invalid(
        s"invalid section for $label"
      ))
      _ <- Either.cond(
        symbol.value >= section.address,
        (),
        CodeObjectError.Invalid(s"$label precedes its section")
      )
      relative <- intValue(symbol.value - section.address, s"$label section offset")
      _        <- Either.cond(
        symbol.size >= minimumSize && relative <= section.size && symbol.size <= section.size - relative,
        (),
        CodeObjectError.Invalid(s"$label is truncated")
      )
      start = section.offset + relative
    } yield java.util.Arrays.copyOfRange(bytes, start, start + symbol.size)

  // Scan the untrusted ELF bytes without materializing every candidate substring.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
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
    if (prefixStart < 0) Left(CodeObjectError.Invalid("AMDGPU target metadata is absent"))
    else {
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
      Target.either(name.toLowerCase(java.util.Locale.ROOT)).leftMap(message =>
        CodeObjectError.Unsupported(message.refineEither[MinLength[1] & NoNul].getOrElse(
          "unsupported AMDGPU target"
        ))
      )
    }
  }

  // Find the terminator in place before constructing the one validated string.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
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

  private def intValue(value: Long, label: String): Either[CodeObjectError, Int] =
    Either.cond(
      value >= 0L && value <= Int.MaxValue.toLong,
      value.toInt,
      CodeObjectError.Unsupported(s"$label is too large")
    )

  private def littleShort(bytes: Array[Byte], offset: Int): Int =
    LittleEndian.decodeUnsignedInt16(bytes, offset)

  private def littleInt(bytes: Array[Byte], offset: Int): Int =
    LittleEndian.decodeInt32(bytes, offset)

  private def littleLong(bytes: Array[Byte], offset: Int): Long =
    LittleEndian.decode(bytes, offset, ScalarByteWidth(8))
}
