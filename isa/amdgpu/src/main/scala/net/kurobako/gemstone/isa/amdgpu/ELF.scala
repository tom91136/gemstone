package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import cats.syntax.either.*
import cats.syntax.traverse.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.LittleEndian.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.elf.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.string.*

/** ELF indexing, relocation and mutable section images used by the AMDGPU loader. */
private object ELF {

  /** One loadable kernel extracted from an ELF64 AMDGPU code object. */
  case class CodeObjectKernel(
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
      readOnly: Array[Byte] = Array.emptyByteArray,
      /** Complete executable text for linked objects. */
      textAddress: Long = 0L,
      text: Vec[Byte] = Vec.empty,
      functionSizes: Map[Long, Int] = Map.empty,
      /** Device globals share these mutable section images across all kernels in one code object. */
      globalMemory: Array[(Long, Array[Byte])] = Array.empty
  ) {
    val emulatedCalls: Map[Long, VMFunction] = functionTargets.flatMap { case (address, name) =>
      VMFunction.matching(name).headOption.map(address -> _)
    }
    val globalMemoryLocks: Array[ReentrantLock] =
      Array.fill(globalMemory.length)(ReentrantLock())
  }

  /** Public-symbol information needed by an API facade; decoding and relocation ownership remain in this ISA layer. */
  case class CodeObjectGlobal(
      name: NonEmpty[String],
      address: GuestAddress,
      size: Int >= 0,
      pointerOffsets: Vec[Int >= 0]
  )

  private case class Symbol(
      name: String,
      value: Long,
      size: Int >= 0,
      section: Int,
      kind: Int,
      inferredSize: Boolean = false
  )
  private case class KernelRecord(
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
  class Index private[ELF] (
      val target: Target,
      private val bytes: Array[Byte],
      private val records: Map[String, KernelRecord],
      private val functionTargets: Map[Long, String],
      private val functionSizes: Map[Long, Int],
      private val textSection: SectionHeader,
      private val readOnlySection: Option[SectionHeader],
      private val globalSections: Map[Int, SectionHeader],
      private val globalSymbols: Map[NonEmpty[String], Symbol],
      private val globalRelocations: Vec[(Long, Long)],
      private val noteSections: Vec[SectionHeader]
  ) {
    lazy val notes: Vec[Vec[Byte]] = noteSections.map(sectionBytes(bytes, _))

    private val materialized    = ConcurrentHashMap[String, CodeObjectKernel]()
    private val globalImageLock = ReentrantLock()
    private lazy val textImage  = java.util.Arrays.copyOfRange(
      bytes,
      textSection.offset.raw,
      textSection.offset.raw + textSection.size.raw
    )
    private lazy val readOnly = readOnlySection.fold(0L -> Array.emptyByteArray) { section =>
      globalSections.view.collectFirst {
        case (index, candidate) if candidate == section => candidate.address -> globalImages(index)
      }.getOrElse(section.address -> Array.emptyByteArray)
    }
    private lazy val globalImages: Map[Int, Array[Byte]] = {
      val images = globalSections.view.map { case (index, section) =>
        val image =
          if (section.sectionType == 8) Array.ofDim[Byte](section.size.raw)
          else java.util.Arrays.copyOfRange(bytes, section.offset.raw, section.offset.raw + section.size.raw)
        index -> image
      }.toMap
      globalRelocations.foreach { case (address, value) =>
        globalSections.view.collectFirst {
          case (index, section)
              if address >= section.address && address - section.address <= section.size.raw.toLong - 8L =>
            LittleEndian.write(
              value,
              images(index),
              (address - section.address).toInt,
              8
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
      val codeBytes = java.util.Arrays.copyOfRange(bytes, record.codeStart, record.codeStart + record.symbol.size.raw)
      val code      =
        Vec.unsafeWrapArray(if (record.symbol.inferredSize) trimTrailingKernelPadding(codeBytes) else codeBytes)
      val (readOnlyAddress, readOnlyBytes) = readOnly
      CodeObjectKernel(
        name = record.symbol.name,
        target = target,
        kernargSize = record.kernargSize,
        groupSegmentSize = record.groupSegmentSize,
        privateSegmentSize = record.privateSegmentSize,
        computePgmRsrc2 = record.computePgmRsrc2,
        kernelCodeProperties = record.kernelCodeProperties,
        code = code,
        codeAddress = record.symbol.value,
        functionTargets = functionTargets,
        readOnlyAddress = readOnlyAddress,
        readOnly = readOnlyBytes,
        textAddress = textSection.address,
        text = Vec.unsafeWrapArray(textImage),
        functionSizes = functionSizes,
        globalMemory = globalRegions
      )
    }

    val kernelNames: Set[String] = records.keySet

    val globals: Vec[CodeObjectGlobal] = Vec.from(
      globalSymbols.view.map { case (name, symbol) =>
        val pointerOffsets = globalRelocations.collect {
          case (address, _) if address >= symbol.value && address - symbol.value <= symbol.size.raw.toLong - 8L =>
            (address - symbol.value).toInt.unsafe[Int >= 0]
        }.distinct.sorted
        CodeObjectGlobal(
          name,
          GuestAddress(symbol.value),
          symbol.size,
          pointerOffsets
        )
      }.toSeq.sortBy(global => (global.address.raw, global.name))
    )

    /** Make a session-local view over the immutable parsed index. Mutable global images and lazy kernel materialization
      * intentionally remain private to the view; the code-object bytes and parsed tables are safe to share.
      */
    def fork(): Index =
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
        globalRelocations,
        noteSections
      )

    /** Returns the device address and size of a mutable HIP/HSA-visible global. */
    def global(name: NonEmpty[String]): Option[(GuestAddress, Int >= 0)] =
      globalSymbols.get(name).flatMap { symbol =>
        globalSections.get(symbol.section).flatMap { section =>
          Option.when(
            symbol.value >= section.address &&
              symbol.value - section.address <= section.size.raw.toLong &&
              symbol.size.raw.toLong <= section.size.raw.toLong - (symbol.value - section.address)
          )(GuestAddress(symbol.value) -> symbol.size)
        }
      }

    /** Snapshot from this session's relocated section image, before external storage is bound. */
    def globalData(name: NonEmpty[String]): Either[CodeObjectError, (GuestAddress, Vec[Byte])] =
      globalImageLock.withLock {
        global(name).toRight(CodeObjectError.Invalid(
          s"AMDGPU global symbol $name is not present"
        )).map {
          case (address, size) =>
            val symbol  = globalSymbols(name)
            val section = globalSections(symbol.section)
            val start   = (address.raw - section.address).toInt
            address ->
              Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(globalImages(symbol.section), start, start + size.raw))
        }
      }

    /** Applies a host-to-device symbol copy to the shared code-object section image. */
    def writeGlobal(name: NonEmpty[String], offset: Long, value: Vec[Byte]): Either[CodeObjectError, Unit] =
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
                    relative < 0L || relative > section.size.raw.toLong ||
                    symbol.size.raw.toLong > section.size.raw.toLong - relative
                  )
                    Left(CodeObjectError.Invalid(
                      s"AMDGPU global symbol $name exceeds its section"
                    ))
                  else if (
                    offset < 0L || offset > symbol.size.raw.toLong ||
                    value.size.toLong > symbol.size.raw.toLong - offset
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
      val missing: NonEmpty[String] = s"AMDGPU code object has no kernel symbol $name"
      records.get(name).toRight(CodeObjectError.Invalid(missing)).map { record =>
        materialized.computeIfAbsent(name, _ => materialize(record))
      }
    }
  }

  // Target IDs use ASCII; ELF symbol names below may contain non-ASCII UTF-8.
  private val TargetPrefix = "amdgcn-amd-amdhsa--".getBytes(StandardCharsets.US_ASCII)
  private val indexCache   = LRUCache[Vec[Byte], Either[CodeObjectError, Index]](capacity = 4)

  def selectFunctionTarget(names: Vec[String]): Either[CodeObjectError, String] = {
    val aliases    = names.distinct.sorted
    val recognized = aliases.flatMap(name => VMFunction.matching(name).map(_ -> name))
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
      sections   <- sections64(bytes).left.map(CodeObjectError.Invalid(_))
      names      <- sectionNames(bytes, sections, buffer.getShort(62) & 0xffff)
      rawSymbols <- readSymbols(bytes, sections)
      symbols = inferFunctionSizes(rawSymbols, sections)
      descriptors <- descriptorIndex(symbols)
      textSection <- sections.zip(names).collectFirst { case (section, ".text") => section }
        .toRight(CodeObjectError.Invalid("AMDGPU code object has no .text section"))
      functionTargets <- symbols.view
        .filter(candidate => candidate.kind == 2 && candidate.section > 0 && candidate.size.raw > 0)
        .toVec
        .groupMap(_.value)(_.name)
        .view
        .map { case (address, aliases) => selectFunctionTarget(aliases).map(address -> _) }
        .toVec
        .sequence
        .map(_.toMap)
      functionSizes = symbols.view
        .filter(symbol => symbol.kind == 2 && symbol.section > 0 && symbol.size.raw > 0)
        .toVec
        .groupMap(_.value)(_.size.raw)
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
          symbol.kind == 2 && symbol.size.raw > 0 && symbol.section > 0 && descriptors.contains(s"${symbol.name}.kd")
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
        symbol.name.refine[NonEmpty[String]]
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
      globalRelocations,
      sections.filter(_.sectionType == NoteSection)
    )

  def index(image: Vec[Byte]): Either[CodeObjectError, Index] =
    indexCache.get(image)(indexBytes(Vec.toArray(image))).map(_.fork())

  private val NoteSection = 7

  private def sectionBytes(bytes: Array[Byte], section: SectionHeader): Vec[Byte] =
    Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(bytes, section.offset.raw, section.offset.raw + section.size.raw))

  def notes(image: Vec[Byte]): Either[CodeObjectError, Vec[Vec[Byte]]] = {
    val bytes = Vec.toArray(image)
    for {
      _        <- elfBuffer(bytes)
      sections <- sections64(bytes).left.map(CodeObjectError.Invalid(_))
    } yield sections.filter(_.sectionType == NoteSection).map(sectionBytes(bytes, _))
  }

  private val ProgramBits             = 1
  private val SymbolTable             = 2
  private val StringTable             = 3
  private val DynamicSymbolTable      = 11
  private val RelocationWithoutAddend = 9
  private val Executable              = 4L

  def executableSections(
      image: Vec[Byte],
      external: Map[String, Long] = Map.empty,
      addresses: Map[String, Long] = Map.empty
  ): Either[CodeObjectError, Vec[ExecutableSection]] = {
    val bytes = Vec.toArray(image)
    for {
      buffer   <- elfBuffer(bytes)
      sections <- sections64(bytes).left.map(CodeObjectError.Invalid(_))
      names    <- sectionNames(bytes, sections, buffer.getShort(62) & 0xffff)
      symbols  <-
        if (sections.exists(section => section.sectionType == SymbolTable || section.sectionType == DynamicSymbolTable))
          readSymbols(bytes, sections)
        else Right(Vec.empty[Symbol])
      code <- sections.zipWithIndex.filter { case (section, _) => (section.flags & Executable) != 0 }.traverse {
        case (section, index) =>
          for {
            _ <- Either.cond(
              section.sectionType == ProgramBits,
              (),
              CodeObjectError.Invalid(s"executable section ${names(index)} has no file data")
            )
            relocated <-
              relocateText(bytes, sections, names, section, index, buffer.getShort(16) == 1, external, addresses)
            entries <- symbols.filter(symbol => symbol.section == index && symbol.name.nonEmpty).traverse { symbol =>
              val relative = symbol.value - (if (buffer.getShort(16) == 1) 0L else section.address)
              for {
                _ <- Either.cond(
                  relative >= 0 && relative <= section.size.raw &&
                    symbol.size.raw <= section.size.raw - relative,
                  (),
                  CodeObjectError.Invalid(s"symbol ${symbol.name} exceeds section ${names(index)}")
                )
              } yield ExecutableSection.Symbol(
                symbol.name.unsafe[NonEmpty[String]],
                relative.toInt.unsafe[Int >= 0],
                symbol.size
              )
            }
          } yield ExecutableSection(
            names(index),
            addresses.getOrElse(names(index), section.address),
            relocated,
            entries.distinct.sortBy(_.offset)
          )
      }
      _ <- Either.cond(code.nonEmpty, (), CodeObjectError.Invalid("AMDGPU code object has no executable sections"))
    } yield code
  }

  private def relocateText(
      bytes: Array[Byte],
      sections: Vec[SectionHeader],
      names: Vec[String],
      code: SectionHeader,
      index: Int,
      relocatable: Boolean,
      external: Map[String, Long],
      addresses: Map[String, Long]
  ): Either[CodeObjectError, Vec[Byte]] = {
    val output = java.util.Arrays.copyOfRange(bytes, code.offset.raw, code.offset.raw + code.size.raw)
    val base   = addresses.getOrElse(names(index), code.address)
    def symbol(tableIndex: Int, symbolIndex: Long): Either[CodeObjectError, BigInt] = for {
      table <- sections.lift(tableIndex).filter(value =>
        value.sectionType == SymbolTable || value.sectionType == DynamicSymbolTable
      )
        .toRight(CodeObjectError.Invalid("relocation has no valid symbol table"))
      _ <- Either.cond(
        table.entrySize.raw >= SymbolEntrySize && symbolIndex >= 0 &&
          symbolIndex < table.size.raw / table.entrySize.raw,
        (),
        CodeObjectError.Invalid("relocation symbol index exceeds table")
      )
      strings <- sections.lift(table.link).toRight(CodeObjectError.Invalid("relocation has no symbol string table"))
      start = table.offset.raw + symbolIndex.toInt * table.entrySize.raw
      name <- cString(bytes, strings, decodeInt32(bytes, start))
      sectionIndex = decodeUInt16(bytes, start + 6).raw
      value        = decodeInt64(bytes, start + 8)
      resolved <- if (symbolIndex == 0) Right(BigInt(0)) // ELF STN_UNDEF contributes zero.
      else if (sectionIndex == 0)
        external.get(name).map(BigInt(_)).toRight(CodeObjectError.Unsupported(s"undefined relocation symbol '$name'"))
      else if (sectionIndex == 0xfff1) Right(BigInt(value)) // SHN_ABS
      else sections.lift(
        sectionIndex
      ).toRight(CodeObjectError.Invalid(s"invalid section for relocation symbol '$name'")).flatMap { section =>
        val address =
          addresses.get(names(sectionIndex)).orElse(Option.when(!relocatable || sectionIndex == index)(section.address))
        address.toRight(
          CodeObjectError.Unsupported(s"relocation requires an address for section ${names(sectionIndex)}")
        )
          .map(address => BigInt(address) + value - (if (relocatable) 0 else section.address))
      }
    } yield resolved
    sections.filter(section =>
      (section.sectionType == RelocationSection || section.sectionType == RelocationWithoutAddend) &&
        section.size.raw > 0 && (section.info == index || section.info == 0)
    )
      .traverse { table =>
        val explicit = table.sectionType == RelocationSection
        val minimum  = if (explicit) 24 else 16
        if (table.entrySize.raw < minimum || table.size.raw % table.entrySize.raw != 0)
          Left(CodeObjectError.Invalid("invalid relocation table"))
        else (0 until table.size.raw by table.entrySize.raw).toVec.traverse { record =>
          val start   = table.offset.raw + record
          val address = decodeInt64(bytes, start)
          val offset  = address - (if (relocatable && table.info != 0) 0 else code.address)
          val info    = decodeInt64(bytes, start + 8)
          val kind    = (info & 0xffffffffL).toInt
          if (kind == R_AMDGPU_NONE || (table.info == 0 && (offset < 0 || offset >= code.size.raw))) Right(())
          else {
            val width: Int in 0 ~ 8 = if (kind == R_AMDGPU_ABS64 || kind == R_AMDGPU_REL64) 8 else 4
            for {
              _ <- Either.cond(
                Set(
                  R_AMDGPU_ABS32_LO,
                  R_AMDGPU_ABS32_HI,
                  R_AMDGPU_ABS64,
                  R_AMDGPU_REL32,
                  R_AMDGPU_REL64,
                  R_AMDGPU_ABS32,
                  R_AMDGPU_REL32_LO,
                  R_AMDGPU_REL32_HI
                )(kind),
                (),
                CodeObjectError.Unsupported(s"unsupported AMDGPU relocation type $kind")
              )
              _ <- Either.cond(
                offset >= 0 && offset <= code.size.raw - width.raw,
                (),
                CodeObjectError.Invalid("text relocation exceeds section")
              )
              resolved <- symbol(table.link, info >>> 32)
              addend = if (explicit) BigInt(decodeInt64(bytes, start + 16))
              else if (width.raw == 8) BigInt(decodeInt64(output, offset.toInt))
              else BigInt(decodeInt32(output, offset.toInt))
              relative = Set(R_AMDGPU_REL32, R_AMDGPU_REL64, R_AMDGPU_REL32_LO, R_AMDGPU_REL32_HI)(kind)
              value    = resolved + addend - (if (relative) BigInt(base) + offset else BigInt(0))
              _ <- Either.cond(
                (kind != R_AMDGPU_REL32 || value.isValidInt) &&
                  (kind != R_AMDGPU_ABS32 || (value >= 0 && value < (BigInt(1) << 32))),
                (),
                CodeObjectError.Invalid("text relocation overflows its field")
              )
            } yield LittleEndian.write(
              (if (kind == R_AMDGPU_ABS32_HI || kind == R_AMDGPU_REL32_HI) value >> 32 else value).toLong,
              output,
              offset.toInt,
              width
            )
          }
        }.map(_ => ())
      }.map(_ => Vec.unsafeWrapArray(output))
  }

  def decode(image: Vec[Byte]): Either[CodeObjectError, Map[String, CodeObjectKernel]] =
    index(image).flatMap { indexed =>
      indexed.kernelNames.view.map(name => indexed.kernel(name).map(name -> _)).toVec.sequence.map(_.toMap)
    }

  def inferredFunctionSize(
      value: Long,
      sectionEnd: Long,
      followingValues: Iterable[Long]
  ): Option[Int] = {
    val end = followingValues.view.filter(candidate => candidate > value && candidate <= sectionEnd).minOption
      .getOrElse(sectionEnd)
    val size = end - value
    Option.when(value >= 0 && sectionEnd >= value && size > 0 && size <= Int.MaxValue)(size.toInt)
  }

  private def inferFunctionSizes(symbols: Vec[Symbol], sections: Vec[SectionHeader]): Vec[Symbol] = {
    val functionValuesBySection = symbols.view.filter(symbol => symbol.kind == 2 && symbol.section > 0)
      .toVec
      .groupMap(_.section)(_.value)
    symbols.map { symbol =>
      if (symbol.kind != 2 || symbol.size.raw != 0 || symbol.section <= 0) symbol
      else
        sections.lift(symbol.section).flatMap { section =>
          val sectionEnd = section.address + section.size.raw.toLong
          inferredFunctionSize(symbol.value, sectionEnd, functionValuesBySection(symbol.section))
        }.fold(symbol)(size => symbol.copy(size = size.unsafe[Int >= 0], inferredSize = true))
    }
  }

  // Locate the last terminating instruction in one allocation-free scan.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def trimTrailingKernelPadding(code: Array[Byte]): Array[Byte] = {
    var offset  = 0
    var lastEnd = -1
    while (offset <= code.length - 4) {
      if (decodeInt32(code, offset) == 0xbf810000) lastEnd = offset + 4
      offset += 4
    }
    if (lastEnd < 0 || lastEnd == code.length) code
    else java.util.Arrays.copyOf(code, lastEnd)
  }

  private def elfBuffer(bytes: Array[Byte]): Either[CodeObjectError, ByteBuffer] =
    validateHeader(Vec.unsafeWrapArray(bytes)).map(_ => ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN))

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
      sections: Vec[SectionHeader]
  ): Either[CodeObjectError, Vec[(Long, Long)]] = {
    val buffer  = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    val builder = Vec.newBuilder[(Long, Long)]
    var failure = Option.empty[CodeObjectError]
    sections.view.filter(section => section.sectionType == RelocationSection).foreach { section =>
      val stride = math.max(RelocationEntrySize, section.entrySize.raw)
      var index  = 0
      while (failure.isEmpty && index + stride <= section.size.raw) {
        val start  = section.offset.raw + index
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

  private def symbolValue(bytes: Array[Byte], sections: Vec[SectionHeader], table: Int, index: Int): Option[Long] =
    sections.lift(table).filter(section => section.entrySize.raw >= SymbolEntrySize).flatMap { section =>
      val start = section.offset.raw + index * SymbolEntrySize
      Option.when(
        start >= 0 && start + SymbolEntrySize <= bytes.length &&
          start + SymbolEntrySize <= section.offset.raw + section.size.raw
      )(
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong(start + 8)
      )
    }

  private def sectionNames(
      bytes: Array[Byte],
      sections: Vec[SectionHeader],
      index: Int
  ): Either[CodeObjectError, Vec[String]] =
    sections.lift(index).toRight(CodeObjectError.Invalid("invalid ELF section-name table index")).flatMap { table =>
      sections.map(section => cString(bytes, table, section.nameOffset)).sequence
    }

  private def readSymbols(
      bytes: Array[Byte],
      sections: Vec[SectionHeader]
  ): Either[CodeObjectError, Vec[Symbol]] =
    sections.find(_.sectionType == SymbolTable).orElse(sections.find(_.sectionType == DynamicSymbolTable))
      .toRight(CodeObjectError.Invalid("AMDGPU code object has no symbol table"))
      .flatMap { table =>
        for {
          strings <- sections.lift(table.link).toRight(CodeObjectError.Invalid("invalid ELF symbol string table"))
          _       <- Either.cond(
            table.entrySize.raw >= SymbolEntrySize && table.size.raw % table.entrySize.raw == 0,
            (),
            CodeObjectError.Invalid("invalid ELF symbol-table entry size")
          )
          symbols <- Vec.tabulate(table.size.raw / table.entrySize.raw) { index =>
            val start = table.offset.raw + index * table.entrySize.raw
            for {
              name <- cString(bytes, strings, decodeInt32(bytes, start))
              size <- intValue(decodeInt64(bytes, start + 16), "symbol size")
            } yield Symbol(
              name,
              decodeInt64(bytes, start + 8),
              size.unsafe[Int >= 0],
              decodeUInt16(bytes, start + 6).raw,
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
      sections: Vec[SectionHeader],
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
        relative <= section.size.raw && symbol.size.raw <= section.size.raw - relative,
        (),
        CodeObjectError.Invalid(s"kernel ${symbol.name} exceeds .text")
      )
      start = section.offset.raw + relative
      descriptorSymbol <- descriptors.get(s"${symbol.name}.kd")
        .toRight(CodeObjectError.Invalid(
          s"kernel ${symbol.name} has no descriptor symbol"
        ))
      descriptor       <- symbolBytes(bytes, sections, descriptorSymbol, 64, s"kernel ${symbol.name} descriptor")
      kernargSize      <- intValue(Bits.unsigned32(decodeInt32(descriptor, 8)).toLong, s"${symbol.name} kernarg size")
      groupSegmentSize <- intValue(
        Bits.unsigned32(decodeInt32(descriptor, 0)).toLong,
        s"${symbol.name} group-segment size"
      )
      privateSegmentSize <- intValue(
        Bits.unsigned32(decodeInt32(descriptor, 4)).toLong,
        s"${symbol.name} private-segment size"
      )
      computePgmRsrc2      = decodeInt32(descriptor, 52)
      kernelCodeProperties = decodeUInt16(descriptor, 56).raw
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
      sections: Vec[SectionHeader],
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
        symbol.size.raw >= minimumSize && relative <= section.size.raw &&
          symbol.size.raw <= section.size.raw - relative,
        (),
        CodeObjectError.Invalid(s"$label is truncated")
      )
      start = section.offset.raw + relative
    } yield java.util.Arrays.copyOfRange(bytes, start, start + symbol.size.raw)

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
      Target.either(name.toLowerCaseRoot).leftMap(message =>
        CodeObjectError.Unsupported(message)
      )
    }
  }

  // Find the terminator in place before constructing the one validated string.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def cString(bytes: Array[Byte], table: SectionHeader, offset: Int): Either[CodeObjectError, String] =
    if (table.sectionType != StringTable) Left(CodeObjectError.Invalid("ELF string table has an invalid section type"))
    else if (offset < 0 || offset >= table.size.raw)
      Left(CodeObjectError.Invalid("ELF string offset is outside its table"))
    else {
      val start = table.offset.raw + offset
      val limit = table.offset.raw + table.size.raw
      var end   = start
      while (end < limit && bytes(end) != 0) end += 1
      if (end == limit) Left(CodeObjectError.Invalid("unterminated ELF string"))
      else decodeUtf8(bytes, start, end - start)
        .left.map(_ => CodeObjectError.Unsupported("ELF string is not valid UTF-8"))
    }

  private def intValue(value: Long, label: String): Either[CodeObjectError, Int] =
    Either.cond(
      value >= 0L && value <= Int.MaxValue.toLong,
      value.toInt,
      CodeObjectError.Unsupported(s"$label is too large")
    )

}
