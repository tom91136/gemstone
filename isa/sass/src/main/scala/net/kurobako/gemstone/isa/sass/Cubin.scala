package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
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
      parameters: Vec[Parameter]
  )
  case class Global(
      name: NonEmpty[String],
      bytes: Vec[Byte],
      constantSection: Option[Int >= 0] = None,
      offset: Int >= 0 = 0
  )
  case class Module(sm: Int >= 35, sections: Vec[Section], kernels: Vec[Kernel], globals: Vec[Global])

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
    val kernels = sections.filter(s =>
      s.name.startsWith(".text.") && sections.exists(_.name == s".nv.info.${s.name.stripPrefix(".text.")}")
    ).map { section =>
      val name = section.name.stripPrefix(".text.")
      requireImage(validName(name), "invalid cubin kernel name")
      val info = sections.find(_.name == s".nv.info.$name").map(_.bytes).getOrElse(Vec.empty[Byte])
      val ib   = ByteBuffer.wrap(info.toArray).order(ByteOrder.LITTLE_ENDIAN)
      @annotation.tailrec
      def attributes(offset: Int, result: Vec[(Int, Vec[Byte])]): Vec[(Int, Vec[Byte])] =
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
          attributes(offset + 4 + (if (format == 4) size else 0), result :+ (attr -> value))
        }
      val attrs      = attributes(0, Vec.empty)
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
      Kernel(
        checked(name.refine[NonEmpty[String]]),
        section.index,
        checked(cbank._1.refine[Int >= 0]),
        checked(cbank._2.refine[Int >= 0]),
        parameters
      )
    }
    val globals = sections.filter(_.kind == 2).flatMap { section =>
      requireImage(
        section.link.raw > 0 && section.link.raw < count && section.size.raw % 24 == 0,
        "invalid cubin symbol table"
      )
      val stride = headers(section.index.raw).entrySize
      requireImage(stride.raw == 0 || stride.raw == 24, "invalid cubin symbol table stride")
      checked(elf.symbols64(section.bytes, header.byteOrder)).flatMap { symbol =>
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
