package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.util.control.NonFatal

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
  )
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

  private def relocationEntries(section: Section): Either[String, Vec[elf.Relocation64]] =
    if ((section.kind != 4 && section.kind != 9) || section.bytes.size != section.size.raw)
      Left("invalid cubin relocation table")
    else elf.relocations64(section.bytes, ByteOrder.LITTLE_ENDIAN, section.kind == 4).left.map(_.raw)

  private def relocationGlobals(
      module: Module,
      table: Section
  ): Either[String, (Long >= 0) => Either[String, Global]] = for {
    symbols <- module.sections.lift(table.link.raw).filter(_.kind == 2)
      .toRight("invalid cubin relocation symbol table")
    strings <- module.sections.lift(symbols.link.raw).filter(_.kind == 3)
      .toRight("invalid cubin relocation string table")
    entries <- elf.symbols64(symbols.bytes, ByteOrder.LITTLE_ENDIAN).left.map(_.raw)
  } yield symbolIndex =>
    for {
      boundedIndex <- symbolIndex.raw.refine[Long in 0L ~ 2147483647L]
        .left.map(_ => "invalid cubin relocation symbol")
      symbol <- entries.lift(boundedIndex.toIntExact.raw).toRight("invalid cubin relocation symbol")
      name   <- elf.stringAt(strings.bytes, symbol.nameOffset).left.map(_.raw)
      _      <- Either.cond(
        symbol.kind == 1 && symbol.section > 0 && module.sections.lift(symbol.section).exists(section =>
          section.name.startsWith(".nv.global") || section.name.startsWith(".nv.constant")
        ),
        (),
        s"unsupported cubin relocation target '$name'"
      )
      global <- module.globals.find(global => global.name == name && global.offset.raw.toLong == symbol.value)
        .toRight(s"unsupported cubin relocation target '$name'")
    } yield global

  /** Symbolic CUDA address halves attached to immediate moves, before VM register lowering. */
  object TextRelocation {
    def resolve(
        module: Cubin.Module,
        kernel: Cubin.Kernel,
        function: DecodedFunction,
        boundSymbols: Set[NonEmpty[String]]
    ): Either[NonEmpty[String], Map[Int >= 0, TextRelocation]] = try {
      def requireEntry(condition: Boolean, detail: String): Unit =
        if (!condition) throw IllegalArgumentException(detail)
      val text             = module.sections(kernel.section.raw)
      val instructionBytes = if (module.sm.raw < 70) 8 else 16
      val instructions     = function.instructions.map(instruction => instruction.address -> instruction).toMap
      val entries          = module.sections.filter(section =>
        (section.kind == 4 || section.kind == 9) && section.info == kernel.section.raw
      ).flatMap { table =>
        lazy val resolveGlobal = relocationGlobals(module, table)
          .fold(error => throw IllegalArgumentException(error), identity)
        Cubin.relocationEntries(table).fold(error => throw IllegalArgumentException(error), identity).map {
          relocation =>
            val high = relocation.kind.raw match {
              case Cubin.RelocationKind.KeplerImmediateLow32 if module.sm.raw < 50                   => false
              case Cubin.RelocationKind.KeplerImmediateHigh32 if module.sm.raw < 50                  => true
              case Cubin.RelocationKind.VoltaImmediateLow32 if module.sm.raw >= 70                   => false
              case Cubin.RelocationKind.VoltaImmediateHigh32 if module.sm.raw >= 70                  => true
              case Cubin.RelocationKind.ImmediateLow32 if module.sm.raw >= 50 && module.sm.raw < 70  => false
              case Cubin.RelocationKind.ImmediateHigh32 if module.sm.raw >= 50 && module.sm.raw < 70 => true
              case kind                                                                              =>
                throw IllegalArgumentException(s"unsupported cubin text relocation kind 0x${kind.toHexString}")
            }
            requireEntry(
              relocation.offset >= 0 && relocation.offset <= text.bytes.size.toLong - instructionBytes &&
                relocation.offset % instructionBytes == 0,
              "cubin text relocation exceeds or misaligns its instruction"
            )
            val offset = relocation.offset.toInt.refine[Int >= 0]
              .fold(error => throw IllegalArgumentException(error.raw), identity)
            val instruction = instructions.getOrElse(
              offset,
              throw IllegalArgumentException("cubin text relocation has no decoded instruction")
            )
            requireEntry(
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
            val immediate = VMOperand.integer(instruction.operands(1)).getOrElse(
              throw IllegalArgumentException("cubin text relocation requires an immediate move source")
            )
            requireEntry(
              relocation.addend.nonEmpty || immediate == 0,
              "nonzero implicit cubin text relocation addend is not implemented"
            )
            val global = resolveGlobal(relocation.symbol)
              .fold(error => throw IllegalArgumentException(error), identity)
            requireEntry(boundSymbols(global.name), s"unbound cubin text relocation target '${global.name}'")
            offset -> TextRelocation(global.name, relocation.addend.getOrElse(0L), high)
        }
      }
      requireEntry(entries.map(_._1).distinct.size == entries.size, "overlapping cubin text relocations")
      Right(entries.toMap)
    } catch { case NonFatal(error) => Left(throwables.detail(error)) }
  }

  case class TextRelocation(symbol: NonEmpty[String], addend: Long, high: Boolean)

  /** CUDA ELF R_CUDA_64 data pointers stored in constant banks. */
  def constantRelocations(module: Module): Either[String, Vec[ConstantRelocation]] = try {
    def requireRelocation(condition: Boolean, detail: String): Unit =
      if (!condition) throw IllegalArgumentException(detail)
    val result = module.sections.filter(section =>
      (section.kind == 4 || section.kind == 9) &&
        module.sections.lift(section.info).exists(target =>
          target.name.matches("\\.nv\\.constant[1-9][0-9]*(?:\\..*)?")
        )
    ).flatMap { relocation =>
      lazy val resolveGlobal = relocationGlobals(module, relocation)
        .fold(error => throw IllegalArgumentException(error), identity)
      val bank = module.sections(relocation.info)
      relocationEntries(relocation).fold(error => throw IllegalArgumentException(error), identity).map { entry =>
        requireRelocation(entry.kind.raw == RelocationKind.Address64, "unsupported constant relocation kind")
        requireRelocation(
          entry.offset >= 0 && entry.offset <= bank.bytes.size.toLong - 8L,
          "constant relocation exceeds bank"
        )
        val offset = entry.offset.toInt.refine[Int >= 0]
          .fold(error => throw IllegalArgumentException(error.raw), identity)
        val global = resolveGlobal(entry.symbol)
          .fold(error => throw IllegalArgumentException(error), identity)
        val addend = entry.addend.getOrElse(
          ByteBuffer.wrap(bank.bytes.toArray).order(ByteOrder.LITTLE_ENDIAN).getLong(offset.raw)
        )
        ConstantRelocation(bank.index, offset, global.name, addend)
      }
    }
    requireRelocation(
      result.groupBy(_.section).values.forall { entries =>
        entries.sortBy(_.offset).sliding(2).forall(pair =>
          pair.size < 2 || pair(0).offset.raw + 8 <= pair(1).offset.raw
        )
      },
      "overlapping constant relocations"
    )
    Right(result)
  } catch { case NonFatal(error) => Left(Option(error.getMessage).getOrElse("invalid constant relocations")) }

  def relocateConstants(module: Module, addresses: Map[NonEmpty[String], GuestAddress]): Either[String, Module] =
    constantRelocations(module).flatMap { relocations =>
      relocations.find(relocation => !addresses.contains(relocation.target)) match {
        case Some(relocation) => Left(s"unbound constant relocation target '${relocation.target}'")
        case None             =>
          val banks = relocations.groupBy(_.section)
          Right(module.copy(sections = module.sections.map { section =>
            banks.get(section.index) match {
              case Some(entries) =>
                val data = ByteBuffer.wrap(section.bytes.toArray).order(ByteOrder.LITTLE_ENDIAN)
                entries.foreach(entry => data.putLong(entry.offset.raw, addresses(entry.target).raw + entry.addend))
                section.copy(bytes = Vec.from(data.array()))
              case None
                  if (section.kind == 4 || section.kind == 9) &&
                    section.info.refine[Int >= 0].toOption.exists(banks.contains(_)) =>
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
  private def isConstantSection(name: String): Boolean = name match {
    case s".nv.constant$bank" => bank.headOption.exists(c => c >= '1' && c <= '9') &&
      bank.forall(c => c >= '0' && c <= '9')
    case _ => false
  }

  def decode(image: Array[Byte]): Either[String, Module] = try {
    def requireImage(condition: Boolean, detail: String): Unit =
      if (!condition) throw IllegalArgumentException(detail)
    def checked[A](result: Either[NonEmpty[String], A]): A =
      result.fold(message => throw IllegalArgumentException(message.raw), identity)
    requireImage(image.length <= 64 * 1024 * 1024, "cubin image exceeds 64 MiB")
    val header = checked(elf.header64(image))
    requireImage(
      header.byteOrder == ByteOrder.LITTLE_ENDIAN && header.machine == 190,
      "expected little-endian ELF64 CUDA cubin"
    )
    requireImage(Set(1, 2)(header.imageType), "unsupported CUDA ELF image type")
    requireImage(Set(7, 8)(header.abiVersion), "unsupported CUDA ELF ABI version")
    val sm = if (header.abiVersion >= 8) (header.flags >>> 8) & 255 else header.flags & 255
    requireImage(sm >= 35 && sm <= 121, s"unsupported cubin metadata architecture sm_$sm")
    val headers = checked(elf.sections64(image))
    val count   = headers.size
    requireImage(count > 0 && header.names > 0 && header.names < count, "invalid cubin section table")
    headers.foreach(section => requireImage(section.size.raw <= 64 * 1024 * 1024, "cubin section is too large"))
    val strings  = checked(elf.sectionBytes(image, headers(header.names)))
    val sections = headers.zip(Vec.indices(headers.nonNegativeSize)).map { (section, index) =>
      Section(
        index,
        checked(elf.stringAt(strings, section.nameOffset)),
        section.sectionType,
        section.flags,
        section.size,
        checked(section.link.refine[Int >= 0]),
        section.info,
        checked(elf.sectionBytes(image, section))
      )
    }
    def attributes(info: Vec[Byte]): Vec[(Int, Vec[Byte])] = {
      val ib = ByteBuffer.wrap(info.toArray).order(ByteOrder.LITTLE_ENDIAN)
      @annotation.tailrec
      def loop(offset: Int, result: Vec[(Int, Vec[Byte])]): Vec[(Int, Vec[Byte])] =
        if (offset == info.size) result
        else {
          requireImage(offset + 4 <= info.size, "truncated cubin attribute")
          val format = info(offset) & 255
          val attr   = info(offset + 1) & 255
          val size   = ib.getShort(offset + 2) & 65535
          requireImage(
            format >= 1 && format <= 4 && (format != 4 || size <= info.size - offset - 4),
            "invalid cubin attribute"
          )
          val value = if (format == 4) info.slice(offset + 4, offset + 4 + size) else info.slice(offset + 2, offset + 4)
          loop(offset + 4 + (if (format == 4) size else 0), result :+ (attr -> value))
        }
      loop(0, Vec.empty)
    }
    val symbolTables = sections.filter(_.kind == 2).map { section =>
      requireImage(
        section.link.raw > 0 && section.link.raw < count && section.size.raw % 24 == 0,
        "invalid cubin symbol table"
      )
      val stride = headers(section.index.raw).entrySize
      requireImage(stride.raw == 0 || stride.raw == 24, "invalid cubin symbol table stride")
      section.index.raw -> checked(elf.symbols64(section.bytes, header.byteOrder))
    }.toMap
    val registerCounts = sections.filter(_.name == ".nv.info").flatMap { section =>
      attributes(section.bytes).collect {
        case (0x2f, value) => // EIATTR_REGCOUNT: symbol index, register count.
          requireImage(value.size == 8, "invalid cubin register count record")
          val data      = ByteBuffer.wrap(value.toArray).order(ByteOrder.LITTLE_ENDIAN)
          val symbol    = data.getInt(0)
          val registers = checked(data.getInt(4).refine[Int >= 0])
          // Some cuBLAS images retain a stale symbol-table link in both metadata and text.
          // Resolve it only when the table and matching function are unambiguous.
          val table = symbolTables.get(section.link.raw).map(section.link.raw -> _).orElse {
            Option.when(section.link.raw > 0 && symbolTables.size == 1)(symbolTables.head)
          }.getOrElse(throw IllegalArgumentException("invalid cubin metadata symbol table"))
          requireImage(symbol > 0 && symbol < table._2.size, "invalid cubin register count")
          val entry = table._2(symbol)
          requireImage(entry.kind == 2 && entry.section > 0 && entry.section < count, "invalid cubin kernel symbol")
          val text = sections(entry.section)
          val name = checked(elf.stringAt(sections(sections(table._1).link.raw).bytes, entry.nameOffset))
          requireImage(
            text.name == s".text.$name" && text.link.raw == section.link.raw && (text.info & 0x00ffffff) == symbol,
            "cubin register count does not match its kernel"
          )
          (section.link.raw -> symbol) -> registers
      }
    }.toMap
    val kernels = sections.filter(s =>
      s.name.startsWith(".text.") && sections.exists(_.name == s".nv.info.${s.name.stripPrefix(".text.")}")
    ).map { section =>
      val name = section.name.stripPrefix(".text.")
      requireImage(validName(name), "invalid cubin kernel name")
      val info       = sections.find(_.name == s".nv.info.$name").map(_.bytes).getOrElse(Vec.empty[Byte])
      val attrs      = attributes(info)
      val parameters = attrs.collect { case (0x17, value) =>
        requireImage(value.size == 12, "invalid cubin kernel parameter record")
        val v = ByteBuffer.wrap(value.toArray).order(ByteOrder.LITTLE_ENDIAN)
        Parameter(
          checked((v.getShort(4) & 65535).refine[Int >= 0]),
          checked((v.getShort(6) & 65535).refine[Int >= 0]),
          checked((v.getInt(8) >>> 18).refine[Int > 0])
        )
      }.sortBy(_.ordinal)
      val cbank = attrs.collectFirst { case (0x0a, value) =>
        requireImage(value.size == 8, "invalid cubin parameter constant bank")
        val v = ByteBuffer.wrap(value.toArray).order(ByteOrder.LITTLE_ENDIAN)
        (v.getShort(4) & 65535, v.getShort(6) & 65535)
      }.getOrElse((0x160, 0))
      requireImage(
        parameters.zipWithIndex.forall((p, i) => p.ordinal.raw == i && p.offset.raw + p.size.raw <= cbank._2),
        "invalid cubin parameter layout"
      )
      val requiredCluster = attrs.collectFirst { case (0x3d, value) =>
        requireImage(value.size == 12, "invalid cubin cluster dimensions")
        val v = ByteBuffer.wrap(value.toArray).order(ByteOrder.LITTLE_ENDIAN)
        LaunchGeometry.Dimensions(
          checked(v.getInt(0).refine[Int > 0]),
          checked(v.getInt(4).refine[Int > 0]),
          checked(v.getInt(8).refine[Int > 0])
        )
      }.getOrElse(LaunchGeometry.Dimensions.One)
      val registers = registerCounts.getOrElse(
        section.link.raw -> (section.info & 0x00ffffff),
        checked((section.info >>> 24).refine[Int >= 0])
      )
      Kernel(
        checked(name.refine[NonEmpty[String]]),
        section.index,
        checked(cbank._1.refine[Int >= 0]),
        checked(cbank._2.refine[Int >= 0]),
        parameters,
        requiredCluster,
        attrs.exists(_._1 == 0x3e),
        registers
      )
    }
    val globals = sections.filter(_.kind == 2).flatMap { section =>
      symbolTables(section.index.raw).flatMap { symbol =>
        val kind   = symbol.kind
        val target = symbol.section
        val size   = symbol.size
        if (
          kind == 1 && target > 0 && target < count &&
          (sections(target).name.startsWith(".nv.global") ||
            isConstantSection(sections(target).name)) && size > 0
        ) {
          val start = symbol.value
          requireImage(
            start >= 0 && size >= 0 && start <= sections(target).size.raw && size <= sections(target).size.raw - start,
            "invalid cubin global range"
          )
          val name = checked(elf.stringAt(sections(section.link.raw).bytes, symbol.nameOffset))
          requireImage(validName(name), "invalid cubin global name")
          requireImage(size <= 1024 * 1024, "cubin global exceeds supported initializer size")
          Some(Global(
            checked(name.refine[NonEmpty[String]]),
            if (sections(target).kind == 8) Vec.fill(size.toInt)(0.toByte)
            else sections(target).bytes.slice(start.toInt, (start + size).toInt),
            Option.when(sections(target).name.startsWith(".nv.constant"))(checked(target.refine[Int >= 0])),
            checked(start.toInt.refine[Int >= 0])
          ))
        } else None
      }
    }
    Right(Module(checked(sm.refine[Int >= 35]), sections, kernels, globals))
  } catch { case NonFatal(error) => Left(Option(error.getMessage).getOrElse("invalid cubin")) }
}
