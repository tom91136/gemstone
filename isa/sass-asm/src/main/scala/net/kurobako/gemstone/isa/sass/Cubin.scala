package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.elf.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.reflect.ClassTag

/** Bounds-checked CUDA ELF metadata. Instruction decoding is deliberately separate from module loading. */
object Cubin {
  case class Section(
      index: Int >= 0,
      name: String,
      kind: Int,
      flags: Long,
      size: Int >= 0,
      link: Int >= 0,
      info: Int,
      bytes: Vec[Byte]
  ) {
    def isRelocation: Boolean = kind == SectionType.RelocationWithAddend || kind == SectionType.Relocation
  }
  case class Parameter(ordinal: Int >= 0, offset: Int >= 0, size: Int > 0)
  case class Kernel(
      name: NonEmpty[String],
      section: Int >= 0,
      parameterBase: Int >= 0,
      parameterSize: Int >= 0,
      parameters: Vec[Parameter],
      requiredCluster: LaunchGeometry.Dimensions = LaunchGeometry.Dimensions.One,
      explicitCluster: Boolean = false,
      registerCount: Int >= 0 = 0
  )
  case class Global(
      name: NonEmpty[String],
      bytes: Vec[Byte],
      constantSection: Option[Int >= 0] = None,
      offset: Int >= 0 = 0
  )
  case class Module(sm: Int >= 35, sections: Vec[Section], kernels: Vec[Kernel], globals: Vec[Global])

  case class ConstantRelocation(section: Int >= 0, offset: Int >= 0, target: NonEmpty[String], addend: Long)

  private object RelocationKind {
    val Address64             = 2L
    val KeplerImmediateLow32  = 0x0dL
    val KeplerImmediateHigh32 = 0x0eL
    val VoltaImmediateLow32   = 0x38L
    val VoltaImmediateHigh32  = 0x39L
    val ImmediateLow32        = 0x2bL
    val ImmediateHigh32       = 0x2cL
  }

  private val MaximumImageSize = 64 * 1024 * 1024

  private def check(condition: Boolean, detail: => NonEmpty[String]): Either[NonEmpty[String], Unit] =
    Either.cond(condition, (), detail)

  def relocationEntries(section: Section): Either[NonEmpty[String], Vec[elf.Relocation]] =
    if (!section.isRelocation || section.bytes.size != section.size.raw) Left("invalid cubin relocation table")
    else elf.relocations(section.bytes, section.kind == SectionType.RelocationWithAddend, 0)

  private def relocationGlobals(
      module: Module,
      table: Section
  ): Either[NonEmpty[String], (Long >= 0) => Either[NonEmpty[String], Global]] = for {
    symbols <- module.sections.lift(table.link.raw).filter(_.kind == SectionType.SymbolTable)
      .toRight[NonEmpty[String]]("invalid cubin relocation symbol table")
    strings <- module.sections.lift(symbols.link.raw).filter(_.kind == SectionType.StringTable)
      .toRight[NonEmpty[String]]("invalid cubin relocation string table")
    entries <- elf.symbols(symbols.bytes, strings.bytes, 0)
  } yield symbolIndex =>
    for {
      symbol <- Option.when(symbolIndex.raw < entries.size.toLong)(entries(symbolIndex.raw.toInt))
        .toRight[NonEmpty[String]]("invalid cubin relocation symbol")
      name <- symbol.name.toRight[NonEmpty[String]]("invalid cubin relocation symbol")
      _    <- check(
        symbol.kind.raw == SymbolKind.Object && symbol.section.raw > 0 &&
          module.sections.lift(symbol.section.raw).exists(section =>
            section.name.startsWith(".nv.global") || section.name.startsWith(".nv.constant")
          ),
        s"unsupported cubin relocation target '$name'"
      )
      global <- module.globals.find(global => global.name == name && global.offset.raw.toLong == symbol.value)
        .toRight[NonEmpty[String]](s"unsupported cubin relocation target '$name'")
    } yield global

  private def resolvedRelocations[A: ClassTag](module: Module, table: Section)(
      resolve: (elf.Relocation, (Long >= 0) => Either[NonEmpty[String], Global]) => Either[NonEmpty[String], A]
  ): Either[NonEmpty[String], Vec[A]] =
    relocationEntries(table).flatMap { entries =>
      if (entries.isEmpty) Right(Vec.empty)
      else relocationGlobals(module, table).flatMap(globals => entries.traverse(resolve(_, globals)))
    }

  /** Symbolic CUDA address halves attached to immediate moves, before VM register lowering. */
  object TextRelocation {
    def resolve(
        module: Cubin.Module,
        kernel: Cubin.Kernel,
        function: DecodedFunction,
        boundSymbols: Set[NonEmpty[String]]
    ): Either[NonEmpty[String], Map[Int >= 0, TextRelocation]] = {
      val text             = module.sections(kernel.section.raw)
      val generation       = Generation.of(module.sm)
      val instructionBytes = generation.instructionBytes.raw
      val instructions     = function.instructions.map(instruction => instruction.address -> instruction).toMap
      def high(kind: Long): Either[NonEmpty[String], Boolean] = kind match {
        case RelocationKind.KeplerImmediateLow32 if generation == Generation.Kepler  => Right(false)
        case RelocationKind.KeplerImmediateHigh32 if generation == Generation.Kepler => Right(true)
        case RelocationKind.VoltaImmediateLow32 if generation == Generation.Modern   => Right(false)
        case RelocationKind.VoltaImmediateHigh32 if generation == Generation.Modern  => Right(true)
        case RelocationKind.ImmediateLow32 if generation == Generation.Maxwell       => Right(false)
        case RelocationKind.ImmediateHigh32 if generation == Generation.Maxwell      => Right(true)
        case kind => Left(s"unsupported cubin text relocation kind 0x${kind.toHexString}")
      }
      module.sections.filter(section => section.isRelocation && section.info == kernel.section.raw).flatTraverse {
        table =>
          resolvedRelocations(module, table) { (relocation, globals) =>
            for {
              upper <- high(relocation.kind.raw)
              _     <- check(
                relocation.offset >= 0 && relocation.offset <= text.bytes.size.toLong - instructionBytes &&
                  relocation.offset % instructionBytes == 0,
                "cubin text relocation exceeds or misaligns its instruction"
              )
              offset      <- relocation.offset.toInt.refine[Int >= 0]
              instruction <- instructions.get(offset)
                .toRight[NonEmpty[String]]("cubin text relocation has no decoded instruction")
              _ <- check(
                instruction.modifiers.isEmpty && instruction.operands.size == 2 &&
                  (instruction.operation match {
                    case _: Instruction.Mov | _: Instruction.Mov32i => true
                    case _                                          => false
                  }) && instruction.operands.headOption.exists {
                    case Operand.Register(RegisterBank.R, _) => true
                    case _                                   => false
                  },
                "cubin text relocation requires an immediate 32-bit MOV or MOV32I"
              )
              immediate <- OperandQueries.integer(instruction.operands(1))
                .toRight[NonEmpty[String]]("cubin text relocation requires an immediate move source")
              _ <- check(
                relocation.addend.nonEmpty || immediate == 0,
                "nonzero implicit cubin text relocation addend is not implemented"
              )
              global <- globals(relocation.symbol)
              _      <- check(boundSymbols(global.name), s"unbound cubin text relocation target '${global.name}'")
            } yield offset -> TextRelocation(global.name, relocation.addend.getOrElse(0L), upper)
          }
      }.flatMap { entries =>
        check(entries.map(_._1).distinct.size == entries.size, "overlapping cubin text relocations")
          .map(_ => entries.toMap)
      }
    }
  }

  case class TextRelocation(symbol: NonEmpty[String], addend: Long, high: Boolean)

  /** CUDA ELF R_CUDA_64 data pointers stored in constant banks. */
  def constantRelocations(module: Module): Either[NonEmpty[String], Vec[ConstantRelocation]] =
    module.sections.filter(section =>
      section.isRelocation &&
        module.sections.lift(section.info).exists(target =>
          target.name.matches("\\.nv\\.constant[1-9][0-9]*(?:\\..*)?")
        )
    ).flatTraverse { table =>
      val bank = module.sections(table.info)
      resolvedRelocations(module, table) { (entry, globals) =>
        for {
          _ <- check(entry.kind.raw == RelocationKind.Address64, "unsupported constant relocation kind")
          _ <-
            check(entry.offset >= 0 && entry.offset <= bank.bytes.size.toLong - 8L, "constant relocation exceeds bank")
          offset <- entry.offset.toInt.refine[Int >= 0]
          global <- globals(entry.symbol)
        } yield ConstantRelocation(
          bank.index,
          offset,
          global.name,
          entry.addend.getOrElse(LittleEndian.decodeInt64(bank.bytes, offset.raw))
        )
      }
    }.flatMap { result =>
      check(
        result.groupBy(_.section).values.forall { entries =>
          entries.sortBy(_.offset).sliding(2).forall(pair =>
            pair.size < 2 || pair(0).offset.raw + 8 <= pair(1).offset.raw
          )
        },
        "overlapping constant relocations"
      ).map(_ => result)
    }

  def relocateConstants(
      module: Module,
      addresses: Map[NonEmpty[String], GuestAddress]
  ): Either[NonEmpty[String], Module] =
    constantRelocations(module).flatMap { relocations =>
      relocations.find(relocation => !addresses.contains(relocation.target)) match {
        case Some(relocation) => Left(s"unbound constant relocation target '${relocation.target}'")
        case None             =>
          val banks = relocations.groupBy(_.section)
          Right(module.copy(sections = module.sections.map { section =>
            banks.get(section.index) match {
              case Some(entries) =>
                val data = Vec.toArray(section.bytes)
                entries.foreach(entry =>
                  LittleEndian.write(addresses(entry.target).raw + entry.addend, data, entry.offset.raw, 8)
                )
                section.copy(bytes = Vec.unsafeWrapArray(data))
              case None if section.isRelocation && section.info.refine[Int >= 0].toOption.exists(banks.contains(_)) =>
                section.copy(size = 0, bytes = Vec.empty)
              case None => section
            }
          }))
      }
    }

  private def validName(name: String): Boolean = {
    def initial(c: Char): Boolean =
      (c >= 'A' && c <= 'Z') ||
        (c >= 'a' && c <= 'z') || c == '_' || c == '$'
    name.headOption.exists(initial(_)) && name.tail.forall(c => initial(c) || (c >= '0' && c <= '9') || c == '.')
  }

  /** User constant banks are `.nv.constant<bank>` or kernel-scoped `.nv.constant<bank>.<kernel>`; bank 0 holds kernel
    * parameters.
    */
  def isConstantSection(name: String): Boolean = name match {
    case s".nv.constant$suffix" =>
      val bank = suffix.takeWhile(c => c >= '0' && c <= '9')
      bank.headOption.exists(_ != '0') && (bank.length == suffix.length || suffix(bank.length) == '.')
    case _ => false
  }

  def constantSections(module: Module): Vec[Section] =
    module.sections.filter(section => isConstantSection(section.name))

  /** The program-global name under which a constant bank section is bound. */
  def constantSymbol(section: Int >= 0): NonEmpty[String] = s"__sass_const$section"

  private def attributes(info: Vec[Byte]): Either[NonEmpty[String], Vec[(Int, Vec[Byte])]] = {
    @annotation.tailrec
    def loop(offset: Int, result: Vec[(Int, Vec[Byte])]): Either[NonEmpty[String], Vec[(Int, Vec[Byte])]] =
      if (offset == info.size) Right(result)
      else if (offset + 4 > info.size) Left("truncated cubin attribute")
      else {
        val format    = info(offset) & 255
        val attribute = info(offset + 1) & 255
        val size      = LittleEndian.decodeUInt16(info, offset + 2).raw
        if (format < 1 || format > 4 || (format == 4 && size > info.size - offset - 4)) Left("invalid cubin attribute")
        else {
          val value = if (format == 4) info.slice(offset + 4, offset + 4 + size) else info.slice(offset + 2, offset + 4)
          loop(offset + 4 + (if (format == 4) size else 0), result :+ (attribute -> value))
        }
      }
    loop(0, Vec.empty)
  }

  /** `image` with `kernel`'s code replaced by `text` of the same size. Code offsets that `.nv.info` records, such as
    * exits and barriers, must still hold.
    */
  def patchKernel(image: Vec[Byte], kernel: NonEmpty[String], text: Vec[Byte]): Either[NonEmpty[String], Vec[Byte]] =
    for {
      module  <- decode(image)
      target  <- module.kernels.find(_.name == kernel).toRight(s"kernel $kernel is not in the cubin": NonEmpty[String])
      parsed  <- Image.parse(image)
      section <- parsed.sections.lift(target.section.raw)
        .toRight(s"kernel $kernel names a missing section": NonEmpty[String])
      patched <- parsed.patched(section, text)
    } yield patched.bytes

  // EIATTR_EXIT_INSTR_OFFSETS and EIATTR_S2RCTAID_INSTR_OFFSETS, which follow from the code itself.
  private val ExitOffsets  = 0x1c
  private val CtaidOffsets = 0x1d
  // Other EIATTR lists of instruction offsets, which only the code's author can carry across an edit.
  private val AuthoredOffsets = Set(0x25, 0x27, 0x28, 0x2d, 0x31, 0x34, 0x39, 0x3a)
  private val Ctaids          = Set(SpecialRegister.SR_CTAID_X, SpecialRegister.SR_CTAID_Y, SpecialRegister.SR_CTAID_Z)

  private case class Record(format: Int, attribute: Int, payload: Vec[Byte]) {
    def bytes: Vec[Byte] =
      if (format == 4) Vec(4.toByte, attribute.toByte) ++ LittleEndian.toVec(payload.size.toLong, 2) ++ payload
      else Vec(format.toByte, attribute.toByte) ++ payload
  }

  private def records(info: Vec[Byte]): Either[NonEmpty[String], Vec[Record]] = {
    @annotation.tailrec
    def loop(offset: Int, result: Vec[Record]): Either[NonEmpty[String], Vec[Record]] =
      if (offset == info.size) Right(result)
      else if (offset + 4 > info.size) Left("truncated cubin attribute")
      else {
        val format = info(offset) & 255
        val size   = LittleEndian.decodeUInt16(info, offset + 2).raw
        if (format < 1 || format > 4 || (format == 4 && size > info.size - offset - 4)) Left("invalid cubin attribute")
        else if (format == 4) loop(
          offset + 4 + size,
          result :+ Record(4, info(offset + 1) & 255, info.slice(offset + 4, offset + 4 + size))
        )
        else loop(offset + 4, result :+ Record(format, info(offset + 1) & 255, info.slice(offset + 2, offset + 4)))
      }
    loop(0, Vec.empty)
  }

  private def offsets(code: CodeSection)(selected: DecodedInstruction => Boolean): Vec[Byte] =
    code.instructions.filter(selected).flatMap(instruction => LittleEndian.toVec(instruction.address.raw.toLong, 4))

  private def encode(relocations: Vec[Relocation], explicitAddend: Boolean): Vec[Byte] = relocations.flatMap {
    relocation =>
      LittleEndian.toVec(relocation.offset, 8) ++
        LittleEndian.toVec((relocation.symbol.raw << 32) | relocation.kind.raw, 8) ++
        (if (explicitAddend) LittleEndian.toVec(relocation.addend.getOrElse(0L), 8) else Vec.empty)
  }

  /** `image` with `kernel`'s code replaced by `text` of any size. Later sections move; the kernel symbol's size, its
    * `.debug_frame` range and the exit and CTA-ID read offsets in its `.nv.info` follow the new code. A kernel with
    * text relocations needs `relocations` for the new code; one whose `.nv.info` lists other instruction offsets is
    * rejected, since only the code's author knows where those instructions went.
    */
  def replaceKernel(
      image: Vec[Byte],
      kernel: NonEmpty[String],
      text: Vec[Byte],
      relocations: Option[Vec[Relocation]] = None
  ): Either[NonEmpty[String], Vec[Byte]] = {
    def named(image: Image, name: String): Either[NonEmpty[String], elf.Section] =
      image.section(name).toRight(s"cubin has no section $name": NonEmpty[String])
    for {
      module <- decode(image)
      target <- module.kernels.find(_.name == kernel).toRight(s"kernel $kernel is not in the cubin": NonEmpty[String])
      code   <-
        Target.of(module.sm).flatMap(CodeSection.decode(Vec.toArray(text), _)).left.map(_.unsafe[NonEmpty[String]])
      parsed <- Image.parse(image)
      info   <- parsed.section(s".nv.info.$kernel").traverse(section => records(parsed.contents(section)))
      _      <- info.getOrElse(Vec.empty[Record]).find(record => AuthoredOffsets(record.attribute)).toLeft(()).left.map(
        record =>
          s"kernel $kernel lists instruction offsets in EIATTR 0x${record.attribute.toHexString}": NonEmpty[String]
      )
      textSection <-
        parsed.sections.lift(target.section.raw).toRight(s"kernel $kernel names a missing section": NonEmpty[String])
      grown   <- parsed.resized(textSection, text)
      symbols <- grown.sections.find(
        _.sectionType == SectionType.SymbolTable
      ).toRight("cubin has no symbol table": NonEmpty[String])
      index <- grown.symbolTables(symbols.index.raw).indexWhere(symbol =>
        symbol.name.contains(kernel) && symbol.kind.raw == SymbolKind.Function
      ).refine[Int >= 0].left.map(_ => s"kernel $kernel has no function symbol": NonEmpty[String])
      symbol = grown.symbolTables(symbols.index.raw)(index.raw)
      sized <- grown.withSymbol(symbols, index.raw, symbol.value, text.size.toLong)
      noted <- info.fold[Either[NonEmpty[String], Image]](Right(sized)) { records =>
        val updated = records.map {
          case record if record.attribute == ExitOffsets =>
            record.copy(payload = offsets(code)(_.mnemonic.raw == "EXIT"))
          case record if record.attribute == CtaidOffsets =>
            record.copy(payload =
              offsets(code)(instruction =>
                instruction.mnemonic.raw == "S2R" && instruction.operands.exists(operand =>
                  OperandQueries.base(operand) match {
                    case Operand.SpecialRegister(name) => Ctaids(name)
                    case _                             => false
                  }
                )
              )
            )
          case record => record
        }
        named(sized, s".nv.info.$kernel").flatMap(section => sized.resized(section, updated.flatMap(_.bytes)))
      }
      relocated <-
        (noted.section(s".rela.text.$kernel").orElse(noted.section(s".rel.text.$kernel")), relocations) match {
          case (None, None)                  => Right[NonEmpty[String], Image](noted)
          case (Some(section), Some(values)) =>
            noted.resized(section, encode(values, section.sectionType == SectionType.RelocationWithAddend))
          case (Some(_), None) =>
            Left(s"kernel $kernel has text relocations; supply them for the new code": NonEmpty[String])
          case (None, Some(_)) => Left(s"kernel $kernel has no text relocation section": NonEmpty[String])
        }
      framed <- relocated.sections.find(section =>
        section.isRelocation && relocated.sections.lift(section.info).exists(_.name == ".debug_frame")
      ).fold[Either[NonEmpty[String], Image]](Right(relocated)) { table =>
        for {
          frame   <- named(relocated, ".debug_frame")
          entries <- relocated.relocations(table)
          ranges = entries.filter(_.symbol.raw == index.raw.toLong).map(_.offset.toInt + 8)
          bytes  = ranges.foldLeft(relocated.contents(frame))((contents, at) =>
            if (at + 8 <= contents.size) contents.patch(at, LittleEndian.toVec(text.size.toLong, 8), 8) else contents
          )
          image <- relocated.patched(frame, bytes)
        } yield image
      }
      _ <- decode(framed.bytes)
    } yield framed.bytes
  }

  def decode(image: Vec[Byte]): Either[NonEmpty[String], Module] = for {
    _      <- check(image.size <= MaximumImageSize, "cubin image exceeds 64 MiB")
    parsed <- elf.Image.parse(image)
    header = parsed.header
    _ <- check(header.machine.raw == 190, "expected little-endian ELF64 CUDA cubin")
    _ <- check(Set(1, 2)(header.imageType.raw), "unsupported CUDA ELF image type")
    _ <- check(Set(7, 8)(header.abiVersion.raw), "unsupported CUDA ELF ABI version")
    architecture = if (header.abiVersion.raw >= 8) (header.flags >>> 8) & 255 else header.flags & 255
    sm <- architecture.refine[Int in 35 ~ 121]
      .leftMap[NonEmpty[String]](_ => s"unsupported cubin metadata architecture sm_$architecture")
    count = parsed.sections.size
    _ <- check(count > 0 && header.names.raw > 0, "invalid cubin section table")
    _ <- check(parsed.sections.forall(_.size.raw <= MaximumImageSize), "cubin section is too large")
    sections = parsed.sections.map(section =>
      Section(
        section.index,
        section.name,
        section.sectionType,
        section.flags,
        section.size,
        section.link,
        section.info,
        parsed.contents(section)
      )
    )
    registerCounts <- sections.filter(_.name == ".nv.info").flatTraverse { section =>
      attributes(section.bytes).flatMap(_.collect {
        case (0x2f, value) => // EIATTR_REGCOUNT: symbol index, register count.
          for {
            _ <- check(value.size == 8, "invalid cubin register count record")
            symbol = LittleEndian.decodeInt32(value, 0)
            registers <- LittleEndian.decodeInt32(value, 4).refine[Int >= 0]
            // Some cuBLAS images retain a stale symbol-table link in both metadata and text.
            // Resolve it only when the table and matching function are unambiguous.
            entries <- parsed.symbolTables.get(section.link.raw).orElse {
              Option.when(section.link.raw > 0 && parsed.symbolTables.size == 1)(parsed.symbolTables.head._2)
            }.toRight[NonEmpty[String]]("invalid cubin metadata symbol table")
            _ <- check(symbol > 0 && symbol < entries.size, "invalid cubin register count")
            entry = entries(symbol)
            _ <- check(
              entry.kind.raw == SymbolKind.Function && entry.section.raw > 0 && entry.section.raw < count,
              "invalid cubin kernel symbol"
            )
            text = sections(entry.section.raw)
            _ <- check(
              entry.name.exists(name => text.name == s".text.$name") && text.link.raw == section.link.raw &&
                (text.info & 0x00ffffff) == symbol,
              "cubin register count does not match its kernel"
            )
          } yield (section.link.raw -> symbol) -> registers
      }.sequence)
    }.map(_.toMap)
    kernels <- sections.filter(section =>
      section.name.startsWith(".text.") && sections.exists(_.name == s".nv.info.${section.name.stripPrefix(".text.")}")
    ).traverse(section => kernel(section, sections, registerCounts))
    globals <- sections.filter(_.kind == SectionType.SymbolTable)
      .flatMap(table => parsed.symbolTables.getOrElse(table.index.raw, Vec.empty[elf.Symbol]))
      .traverse(global(_, sections))
      .map(_.flatten)
  } yield Module(sm.widen[Int >= 35], sections, kernels, globals)

  private def global(symbol: elf.Symbol, sections: Vec[Section]): Either[NonEmpty[String], Option[Global]] = {
    val target = symbol.section.raw
    val size   = symbol.size.raw
    if (
      symbol.kind.raw == SymbolKind.Object && target > 0 && target < sections.size &&
      (sections(target).name.startsWith(".nv.global") || isConstantSection(sections(target).name)) && size > 0
    ) {
      val start = symbol.value
      for {
        _ <- check(
          start >= 0 && start <= sections(target).size.raw && size <= sections(target).size.raw - start,
          "invalid cubin global range"
        )
        name   <- symbol.name.filter(validName(_)).toRight[NonEmpty[String]]("invalid cubin global name")
        _      <- check(size <= 1024 * 1024, "cubin global exceeds supported initialiser size")
        offset <- start.toInt.refine[Int >= 0]
      } yield Some(Global(
        name,
        if (sections(target).kind == SectionType.NoBits) Vec.fill(size)(0.toByte)
        else sections(target).bytes.slice(offset.raw, offset.raw + size),
        Option.when(isConstantSection(sections(target).name))(symbol.section.widen[Int >= 0]),
        offset
      ))
    } else Right(None)
  }

  private def kernel(
      section: Section,
      sections: Vec[Section],
      registerCounts: Map[(Int, Int), Int >= 0]
  ): Either[NonEmpty[String], Kernel] = {
    val name                                                          = section.name.stripPrefix(".text.")
    def record(value: Vec[Byte], size: Int, detail: NonEmpty[String]) = check(value.size == size, detail)
    for {
      _          <- check(validName(name), "invalid cubin kernel name")
      attrs      <- attributes(sections.find(_.name == s".nv.info.$name").map(_.bytes).getOrElse(Vec.empty[Byte]))
      parameters <- attrs.collect { case (0x17, value) =>
        for {
          _    <- record(value, 12, "invalid cubin kernel parameter record")
          size <- (LittleEndian.decodeInt32(value, 8) >>> 18).refine[Int > 0]
        } yield Parameter(
          LittleEndian.decodeUInt16(value, 4).widen[Int >= 0],
          LittleEndian.decodeUInt16(value, 6).widen[Int >= 0],
          size
        )
      }.sequence.map(_.sortBy(_.ordinal))
      bank <- attrs.collectFirst { case (0x0a, value) =>
        record(value, 8, "invalid cubin parameter constant bank")
          .map(_ => LittleEndian.decodeUInt16(value, 4) -> LittleEndian.decodeUInt16(value, 6))
      }.getOrElse(Right((0x160: Int in 0 ~ 65535) -> (0: Int in 0 ~ 65535)))
      _ <- check(
        parameters.zipWithIndex.forall((p, i) => p.ordinal.raw == i && p.offset.raw + p.size.raw <= bank._2.raw),
        "invalid cubin parameter layout"
      )
      requiredCluster <- attrs.collectFirst { case (0x3d, value) =>
        for {
          _ <- record(value, 12, "invalid cubin cluster dimensions")
          x <- LittleEndian.decodeInt32(value, 0).refine[Int > 0]
          y <- LittleEndian.decodeInt32(value, 4).refine[Int > 0]
          z <- LittleEndian.decodeInt32(value, 8).refine[Int > 0]
        } yield LaunchGeometry.Dimensions(x, y, z)
      }.getOrElse(Right(LaunchGeometry.Dimensions.One))
      registers <- registerCounts.get(section.link.raw -> (section.info & 0x00ffffff))
        .fold((section.info >>> 24).refine[Int >= 0])(Right(_))
      kernelName <- name.refine[NonEmpty[String]]
    } yield Kernel(
      kernelName,
      section.index,
      bank._1.widen[Int >= 0],
      bank._2.widen[Int >= 0],
      parameters,
      requiredCluster,
      attrs.exists(_._1 == 0x3e),
      registers
    )
  }
}
