package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import cats.syntax.either.*
import cats.syntax.traverse.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.elf.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.string.*

/** AMDGPU kernels, globals, and relocations over a parsed ELF image. */
private object ELF {

  /** Public-symbol information needed by an API facade; decoding and relocation ownership remain in this ISA layer. */
  case class CodeObjectGlobal(
      name: NonEmpty[String],
      address: GuestAddress,
      size: Int >= 0,
      pointerOffsets: Vec[Int >= 0]
  )

  private case class KernelRecord(
      name: NonEmpty[String],
      symbol: elf.Symbol,
      inferredSize: Boolean,
      textOffset: Int >= 0,
      kernargSize: Int >= 0,
      groupSegmentSize: Int >= 0,
      privateSegmentSize: Int >= 0,
      computePgmRsrc1: Int,
      computePgmRsrc2: Int,
      kernelCodeProperties: Int
  )

  /** Validated AMDGPU code-object index. Kernel bytes and read-only data are copied only when a named kernel is first
    * requested; successful materialisations are shared by concurrent callers.
    */
  // a companion factory would expose the private KernelRecord its constructor takes
  @SuppressWarnings(Array("scalafix:QualifiedPrivate.companion-factory"))
  class Index private[ELF] (
      val target: Target,
      private val image: elf.Image,
      private val records: Map[String, KernelRecord],
      private val functionTargets: Map[Long, String],
      private val functionSizes: Map[Long, Int],
      private val textSection: elf.Section,
      private val text: Vec[Byte],
      private val readOnlySection: Option[elf.Section],
      private val globalSections: Map[Int, elf.Section],
      private val globalSymbols: Map[NonEmpty[String], elf.Symbol],
      private val globalRelocations: Vec[(Long, Long)],
      val globals: Vec[CodeObjectGlobal]
  ) {
    lazy val notes: Vec[Vec[Byte]] = image.sections.filter(_.sectionType == SectionType.Note).map(image.contents)

    private val materialised    = ConcurrentHashMap[String, CodeObjectKernel]()
    private val globalImageLock = ReentrantLock()
    private lazy val readOnly   = readOnlySection.fold(0L -> Array.emptyByteArray) { section =>
      section.address -> globalImages.getOrElse(section.index.raw, Array.emptyByteArray)
    }
    private lazy val globalImages: Map[Int, Array[Byte]] = {
      val images = globalSections.view.map { case (index, section) =>
        index ->
          (if (section.sectionType == SectionType.NoBits) Array.ofDim[Byte](section.size.raw)
           else Vec.toArray(image.contents(section)))
      }.toMap
      globalRelocations.foreach { case (address, value) =>
        globalSections.view.collectFirst {
          case (index, section)
              if address >= section.address && address - section.address <= section.size.raw.toLong - 8L =>
            LittleEndian.write(value, images(index), (address - section.address).toInt, 8)
        }
      }
      images
    }
    private lazy val globalRegions: Array[(Long, Array[Byte])] =
      globalSections.keys.view.toArray.sorted.map(index => globalSections(index).address -> globalImages(index))

    private def kernelCodeSize(record: KernelRecord): Int =
      if (record.inferredSize) unpaddedCodeSize(text, record.textOffset.raw, record.symbol.size.raw)
      else record.symbol.size.raw

    private def kernelCode(record: KernelRecord): Vec[Byte] =
      text.slice(record.textOffset.raw, record.textOffset.raw + kernelCodeSize(record))

    private def missingKernel(name: String) = CodeObjectError.Invalid(s"AMDGPU code object has no kernel symbol $name")

    def kernelCodeSize(name: String): Option[Int] = records.get(name).map(kernelCodeSize(_))

    /** Inspect code without allocating relocated globals or kernel state. */
    def kernelCode(name: String): Either[CodeObjectError, Vec[Byte]] =
      records.get(name).toRight(missingKernel(name)).map(kernelCode(_))

    private def materialise(record: KernelRecord): CodeObjectKernel = {
      val (readOnlyAddress, readOnlyBytes) = readOnly
      val codeSize                         = kernelCodeSize(record)
      CodeObjectKernel(
        name = record.name,
        target = target,
        kernargSize = record.kernargSize,
        groupSegmentSize = record.groupSegmentSize,
        privateSegmentSize = record.privateSegmentSize,
        computePgmRsrc2 = record.computePgmRsrc2,
        kernelCodeProperties = record.kernelCodeProperties,
        text = text,
        entrySize = math.min(codeSize, functionSizes.getOrElse(record.symbol.value, codeSize)).unsafe[Int >= 0],
        textAddress = textSection.address,
        entry = record.symbol.value,
        functionSizes = functionSizes,
        functionTargets = functionTargets,
        readOnlyAddress = readOnlyAddress,
        readOnly = readOnlyBytes,
        globalMemory = globalRegions,
        computePgmRsrc1 = record.computePgmRsrc1
      )
    }

    val kernelNames: Set[NonEmpty[String]] = records.values.map(_.name).toSet

    /** Make a session-local view over the immutable parsed index. Mutable global images and lazy kernel materialisation
      * intentionally remain private to the view; the code-object bytes and parsed tables are safe to share.
      */
    def fork(): Index =
      Index(
        target,
        image,
        records,
        functionTargets,
        functionSizes,
        textSection,
        text,
        readOnlySection,
        globalSections,
        globalSymbols,
        globalRelocations,
        globals
      )

    private def storage(name: NonEmpty[String]): Either[CodeObjectError, (elf.Symbol, elf.Section, Long)] =
      for {
        symbol <- globalSymbols.get(name).toRight(CodeObjectError.Invalid(s"AMDGPU global symbol $name is not present"))
        section <- globalSections.get(symbol.section.raw)
          .toRight(CodeObjectError.Invalid(s"AMDGPU global symbol $name has no storage"))
        relative = symbol.value - section.address
        _ <- Either.cond(
          relative >= 0L && relative <= section.size.raw.toLong &&
            symbol.size.raw.toLong <= section.size.raw.toLong - relative,
          (),
          CodeObjectError.Invalid(s"AMDGPU global symbol $name exceeds its section")
        )
      } yield (symbol, section, relative)

    /** Returns the device address and size of a mutable HIP/HSA-visible global. */
    def global(name: NonEmpty[String]): Option[(GuestAddress, Int >= 0)] =
      storage(name).toOption.map { case (symbol, _, _) => GuestAddress(symbol.value) -> symbol.size }

    /** Snapshot from this session's relocated section image, before external storage is bound. */
    def globalData(name: NonEmpty[String]): Either[CodeObjectError, (GuestAddress, Vec[Byte])] =
      globalImageLock.withLock {
        storage(name).map { case (symbol, section, relative) =>
          val start = relative.toInt
          GuestAddress(symbol.value) ->
            Vec.unsafeWrapArray(
              java.util.Arrays.copyOfRange(globalImages(section.index.raw), start, start + symbol.size.raw)
            )
        }
      }

    /** Applies a host-to-device symbol copy to the shared code-object section image. */
    def writeGlobal(name: NonEmpty[String], offset: Long, value: Vec[Byte]): Either[CodeObjectError, Unit] =
      globalImageLock.withLock {
        storage(name).flatMap { case (symbol, section, relative) =>
          if (offset < 0L || offset > symbol.size.raw.toLong || value.size.toLong > symbol.size.raw.toLong - offset)
            Left(CodeObjectError.Invalid(
              s"AMDGPU global write $name offset=$offset size=${value.size} exceeds symbol size ${symbol.size}"
            ))
          else {
            val _ = value.copyToArray(globalImages(section.index.raw), (relative + offset).toInt, value.size)
            Right(())
          }
        }
      }

    def kernel(name: String): Either[CodeObjectError, CodeObjectKernel] =
      records.get(name).toRight(missingKernel(name)).map { record =>
        materialised.computeIfAbsent(name, _ => materialise(record))
      }
  }

  private def describeGlobals(
      globalSymbols: Map[NonEmpty[String], elf.Symbol],
      globalRelocations: Vec[(Long, Long)]
  ): Vec[CodeObjectGlobal] = Vec.from(
    globalSymbols.view.map { case (name, symbol) =>
      val pointerOffsets = globalRelocations.collect {
        case (address, _) if address >= symbol.value && address - symbol.value <= symbol.size.raw.toLong - 8L =>
          (address - symbol.value).toInt.unsafe[Int >= 0]
      }.distinct.sorted
      CodeObjectGlobal(name, GuestAddress(symbol.value), symbol.size, pointerOffsets)
    }.toSeq.sortBy(global => (global.address.raw, global.name))
  )

  // Target IDs use ASCII; ELF symbol names below may contain non-ASCII UTF-8.
  private val TargetPrefix = "amdgcn-amd-amdhsa--".getBytes(StandardCharsets.US_ASCII)

  def selectFunctionTarget(names: Vec[String]): Either[CodeObjectError, String] = {
    val aliases    = names.distinct.sorted
    val recognised = aliases.flatMap(name => EmulatedCall.matching(name).map(_ -> name))
    val kinds      = recognised.map(_._1).distinct
    if (kinds.size > 1)
      Left(
        CodeObjectError.Invalid(
          s"AMDGPU function aliases have conflicting call semantics: ${aliases.mkString(", ")}"
        )
      )
    else
      kinds.headOption match {
        case Some(kind) => Right(recognised.collectFirst { case (`kind`, name) => name }.get)
        // Ordinary aliases execute the same address; the selected name is only a diagnostic label.
        case None => aliases.headOption.toRight(CodeObjectError.Invalid("AMDGPU function has no symbol names"))
      }
  }

  /** Constant-space validation suitable for a lazy loader. Full validation remains in [[index]]. */
  def validateHeader(image: Vec[Byte]): Either[CodeObjectError, Unit] =
    elf.header(image).leftMap(CodeObjectError.Invalid(_)).flatMap { header =>
      Either.cond(header.machine.raw == 224, (), CodeObjectError.Invalid("ELF machine is not AMDGPU"))
    }

  private def parse(image: Vec[Byte]): Either[CodeObjectError, elf.Image] =
    validateHeader(image).flatMap(_ => elf.Image.parse(image).leftMap(CodeObjectError.Invalid(_)))

  private def isFunction(symbol: elf.Symbol): Boolean =
    symbol.kind.raw == SymbolKind.Function && symbol.section.raw > 0 && symbol.size.raw > 0

  def index(bytes: Vec[Byte]): Either[CodeObjectError, Index] =
    for {
      image       <- parse(bytes)
      target      <- findTarget(image)
      rawSymbols  <- image.symbols.toRight(CodeObjectError.Invalid("AMDGPU code object has no symbol table"))
      descriptors <- descriptorIndex(rawSymbols)
      textSection <- image.section(".text").toRight(CodeObjectError.Invalid("AMDGPU code object has no .text section"))
      functions = inferFunctionSizes(rawSymbols, image.sections).filter { case (symbol, _) => isFunction(symbol) }
      functionTargets <- functions
        .groupMap(_._1.value)(_._1.name)
        .view
        .map { case (address, aliases) => selectFunctionTarget(aliases.flatten).map(address -> _) }
        .toVec
        .sequence
        .map(_.toMap)
      functionSizes = functions.groupMap(_._1.value)(_._1.size.raw).view.mapValues(_.max).toMap
      readOnlySection <- image.section(".rodata") match {
        case Some(section) if section.sectionType == SectionType.NoBits =>
          Left(CodeObjectError.Invalid("AMDGPU .rodata section is not file-backed"))
        case section => Right(section)
      }
      // Device-library helpers such as __ockl_* are local functions; only dispatchable kernels have the ABI descriptor
      // required to prepare an AQL launch.
      records <- functions.collect {
        case (symbol @ elf.Symbol(Some(name), _, _, _, _), inferred) if descriptors.contains(s"$name.kd") =>
          kernelRecord(image, descriptors(s"$name.kd"), name, symbol, inferred).map((name: String) -> _)
      }.sequence.map(_.toMap)
      _ <- Either.cond(records.nonEmpty, (), CodeObjectError.Invalid("AMDGPU code object has no kernel symbols"))
      globalSections = image.sections.collect {
        case section
            if Set(".rodata", ".data", ".bss", ".got")(section.name) ||
              ((section.flags & SectionFlag.Allocate) != 0 &&
                (section.sectionType == SectionType.ProgramBits || section.sectionType == SectionType.NoBits)) =>
          section.index.raw -> section
      }.toMap
      globalSymbols = rawSymbols.collect {
        case symbol @ elf.Symbol(Some(name), kind, section, _, _)
            if kind.raw == SymbolKind.Object && globalSections.contains(section.raw) =>
          name -> symbol
      }.toMap
      globalRelocations <- readGlobalRelocations(image)
    } yield Index(
      target,
      image,
      records,
      functionTargets,
      functionSizes,
      textSection,
      image.contents(textSection),
      readOnlySection,
      globalSections,
      globalSymbols,
      globalRelocations,
      describeGlobals(globalSymbols, globalRelocations)
    )

  def notes(bytes: Vec[Byte]): Either[CodeObjectError, Vec[Vec[Byte]]] =
    parse(bytes).map(image => image.sections.filter(_.sectionType == SectionType.Note).map(image.contents))

  /** Executable bytes with locally resolvable relocations applied to a private copy. */
  def executableSections(bytes: Vec[Byte]): Either[CodeObjectError, Vec[ExecutableSection]] =
    for {
      image <- parse(bytes)
      symbols = image.symbols.getOrElse(Vec.empty[elf.Symbol])
      code <- image.sections.filter(section => (section.flags & SectionFlag.Executable) != 0).traverse { section =>
        for {
          _ <- Either.cond(
            section.sectionType == SectionType.ProgramBits,
            (),
            CodeObjectError.Invalid(s"executable section ${section.name} has no file data")
          )
          relocated <- relocateText(image, section)
          entries   <- symbols.collect {
            case symbol @ elf.Symbol(Some(name), _, index, _, _) if index.raw == section.index.raw =>
              image.symbolOffset(symbol)
                .leftMap(_ => CodeObjectError.Invalid(s"symbol $name exceeds section ${section.name}"))
                .map { case (_, offset) => ExecutableSection.Symbol(name, offset, symbol.size) }
          }.sequence
        } yield ExecutableSection(section.name, section.address, relocated, entries.distinct.sortBy(_.offset))
      }
      _ <- Either.cond(code.nonEmpty, (), CodeObjectError.Invalid("AMDGPU code object has no executable sections"))
    } yield code

  private val SupportedTextRelocations = Set(
    R_AMDGPU_ABS32_LO,
    R_AMDGPU_ABS32_HI,
    R_AMDGPU_ABS64,
    R_AMDGPU_REL32,
    R_AMDGPU_REL64,
    R_AMDGPU_ABS32,
    R_AMDGPU_REL32_LO,
    R_AMDGPU_REL32_HI
  )
  private val RelativeTextRelocations = Set(R_AMDGPU_REL32, R_AMDGPU_REL64, R_AMDGPU_REL32_LO, R_AMDGPU_REL32_HI)

  private def relocateText(image: elf.Image, code: elf.Section): Either[CodeObjectError, Vec[Byte]] = {
    val relocatable = image.header.imageType.raw == ImageType.Relocatable
    val output      = Vec.toArray(image.contents(code))
    def resolve(table: elf.Section, symbolIndex: Long): Either[CodeObjectError, BigInt] = for {
      symbols <- image.symbolTables.get(table.link.raw)
        .toRight(CodeObjectError.Invalid("relocation has no valid symbol table"))
      symbol <- Option.when(symbolIndex < symbols.size.toLong)(symbols(symbolIndex.toInt))
        .toRight(CodeObjectError.Invalid("relocation symbol index exceeds table"))
      name = symbol.name.getOrElse("")
      resolved <-
        if (symbolIndex == 0) Right(BigInt(0)) // ELF STN_UNDEF contributes zero.
        else if (symbol.section.raw == SectionIndex.Undefined)
          Left(CodeObjectError.Unsupported(s"undefined relocation symbol '$name'"))
        else if (symbol.section.raw == SectionIndex.Absolute) Right(BigInt(symbol.value))
        else
          image.sections.lift(symbol.section.raw)
            .toRight(CodeObjectError.Invalid(s"invalid section for relocation symbol '$name'"))
            .flatMap { section =>
              if (!relocatable) Right(BigInt(symbol.value))
              else if (section.index == code.index) Right(BigInt(section.address) + symbol.value)
              else Left(CodeObjectError.Unsupported(s"relocation requires an address for section ${section.name}"))
            }
    } yield resolved
    image.sections.filter(table =>
      table.isRelocation && table.size.raw > 0 && (table.info == code.index.raw || table.info == 0)
    ).traverse { table =>
      image.relocations(table).leftMap(_ => CodeObjectError.Invalid("invalid relocation table")).flatMap(_.traverse {
        relocation =>
          val offset = relocation.offset - (if (relocatable && table.info != 0) 0 else code.address)
          val kind   = relocation.kind.raw.toInt
          if (kind == R_AMDGPU_NONE || (table.info == 0 && (offset < 0 || offset >= code.size.raw))) Right(())
          else {
            val width: Int in 0 ~ 8 = if (kind == R_AMDGPU_ABS64 || kind == R_AMDGPU_REL64) 8 else 4
            for {
              _ <- Either.cond(
                SupportedTextRelocations(kind),
                (),
                CodeObjectError.Unsupported(s"unsupported AMDGPU relocation type $kind")
              )
              _ <- Either.cond(
                offset >= 0 && offset <= code.size.raw - width.raw,
                (),
                CodeObjectError.Invalid("text relocation exceeds section")
              )
              resolved <- resolve(table, relocation.symbol.raw)
              implicitAddend = LittleEndian.decode(output, offset.toInt, width)
              addend         = BigInt(relocation.addend.getOrElse(
                if (width.raw == 8) implicitAddend else implicitAddend.toInt.toLong
              ))
              value = resolved + addend - (if (RelativeTextRelocations(kind)) BigInt(code.address) + offset else 0)
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
      })
    }.map(_ => Vec.unsafeWrapArray(output))
  }

  private def inferredFunctionSize(value: Long, sectionEnd: Long, followingValues: Iterable[Long]): Option[Int >= 0] = {
    val end = followingValues.view.filter(candidate => candidate > value && candidate <= sectionEnd).minOption
      .getOrElse(sectionEnd)
    Option.when(value >= 0 && sectionEnd >= value)(end - value).flatMap(size =>
      size.refine[Long in 1L ~ 2147483647L].toOption.map(_.toIntExact)
    )
  }

  private def inferFunctionSizes(symbols: Vec[elf.Symbol], sections: Vec[elf.Section]): Vec[(elf.Symbol, Boolean)] = {
    def candidate(symbol: elf.Symbol) = symbol.kind.raw == SymbolKind.Function && symbol.section.raw > 0
    val functionValuesBySection       = symbols.filter(candidate(_)).groupMap(_.section.raw)(_.value)
    symbols.map { symbol =>
      if (!candidate(symbol) || symbol.size.raw != 0) symbol -> false
      else
        sections.lift(symbol.section.raw).flatMap { section =>
          inferredFunctionSize(
            symbol.value,
            section.address + section.size.raw.toLong,
            functionValuesBySection(symbol.section.raw)
          )
        }.fold(symbol -> false)(size => symbol.copy(size = size) -> true)
    }
  }

  // Locate the last terminating instruction in one allocation-free scan.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def unpaddedCodeSize(code: Vec[Byte], start: Int, size: Int): Int = {
    var offset  = 0
    var lastEnd = -1
    while (offset <= size - 4) {
      if (LittleEndian.decodeInt32(code, start + offset) == 0xbf810000) lastEnd = offset + 4
      offset += 4
    }
    if (lastEnd < 0) size else lastEnd
  }

  /** Absolute 64-bit relocations a linked HIP code object still carries. HIP resolves a device global that is reached
    * through a GOT slot (internal-linkage statics, for example) by storing the symbol address in `.got` at load time,
    * so the slot has to be filled before the first kernel reads it.
    */
  private def readGlobalRelocations(image: elf.Image): Either[CodeObjectError, Vec[(Long, Long)]] =
    image.sections.filter(_.sectionType == SectionType.RelocationWithAddend).flatTraverse { table =>
      image.relocations(table).leftMap(CodeObjectError.Invalid(_)).flatMap(_.flatTraverse { relocation =>
        val addend = relocation.addend.getOrElse(0L)
        relocation.kind.raw.toInt match {
          case R_AMDGPU_RELATIVE64 => Right(Vec(relocation.offset -> addend))
          case R_AMDGPU_ABS64      =>
            image.symbolTables.get(table.link.raw)
              .flatMap(_.lift(relocation.symbol.raw.toInt).filter(_ => relocation.symbol.raw <= Int.MaxValue))
              .map(symbol => Vec(relocation.offset -> (symbol.value + addend)))
              .toRight(CodeObjectError.Invalid(s"unresolved AMDGPU relocation symbol ${relocation.symbol}"))
          case _ => Right(Vec.empty)
        }
      })
    }

  private def descriptorIndex(symbols: Vec[elf.Symbol]): Either[CodeObjectError, Map[String, elf.Symbol]] =
    symbols.foldLeft[Either[CodeObjectError, Map[String, elf.Symbol]]](Right(Map.empty)) {
      case (Right(descriptors), symbol @ elf.Symbol(Some(name), kind, _, _, _))
          if kind.raw == SymbolKind.Object && name.endsWith(".kd") =>
        Either.cond(
          !descriptors.contains(name),
          descriptors.updated(name, symbol),
          CodeObjectError.Invalid(s"duplicate AMDGPU kernel descriptor symbol $name")
        )
      case (result, _) => result
    }

  private def kernelRecord(
      image: elf.Image,
      descriptorSymbol: elf.Symbol,
      name: NonEmpty[String],
      symbol: elf.Symbol,
      inferredSize: Boolean
  ): Either[CodeObjectError, KernelRecord] = {
    def field(descriptor: Vec[Byte], offset: Int, label: String) =
      elf.intValue(LittleEndian.decode(descriptor, offset, 4), s"$name $label").leftMap(CodeObjectError.Unsupported(_))
    for {
      located <- image.symbolOffset(symbol).leftMap(_ => CodeObjectError.Invalid(s"kernel $name exceeds .text"))
      _       <- Either.cond(
        located._1.name == ".text",
        (),
        CodeObjectError.Invalid(s"kernel $name does not point into .text")
      )
      descriptor <- image.symbolBytes(descriptorSymbol)
        .leftMap(CodeObjectError.Invalid(_))
        .filterOrElse(_.size >= 64, CodeObjectError.Invalid(s"kernel $name descriptor is truncated"))
      kernargSize        <- field(descriptor, 8, "kernarg size")
      groupSegmentSize   <- field(descriptor, 0, "group-segment size")
      privateSegmentSize <- field(descriptor, 4, "private-segment size")
    } yield KernelRecord(
      name,
      symbol,
      inferredSize,
      located._2,
      kernargSize,
      groupSegmentSize,
      privateSegmentSize,
      LittleEndian.decodeInt32(descriptor, 48),
      LittleEndian.decodeInt32(descriptor, 52),
      LittleEndian.decodeUInt16(descriptor, 56).raw
    )
  }

  // Scan the untrusted ELF bytes without materialising every candidate substring.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def findTarget(image: elf.Image): Either[CodeObjectError, Target] = {
    val bytes       = Vec.toArray(image.bytes)
    var candidate   = 0
    var prefixStart = -1
    val lastStart   = bytes.length - TargetPrefix.length
    while (candidate <= lastStart && prefixStart < 0) {
      var matched = 0
      while (matched < TargetPrefix.length && bytes(candidate + matched) == TargetPrefix(matched)) matched += 1
      if (matched == TargetPrefix.length) prefixStart = candidate
      candidate += 1
    }
    if (prefixStart < 0) {
      val machine = image.header.flags & EncodingConstants.ELFMachine.Mask
      EncodingConstants.ELFMachine.Names.get(machine)
        .toRight(CodeObjectError.Unsupported(s"unknown AMDGPU ELF machine 0x${machine.toHexString}"))
        .flatMap(name => name.refine[Target].leftMap(CodeObjectError.Unsupported(_)))
    } else {
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
      name.toLowerCaseRoot.refine[Target].leftMap(CodeObjectError.Unsupported(_))
    }
  }
}
