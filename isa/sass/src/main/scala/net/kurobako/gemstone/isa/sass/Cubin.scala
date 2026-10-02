package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.util.control.NonFatal

/** Bounds-checked CUDA ELF metadata. Instruction decoding is deliberately separate from module loading. */
object Cubin {
  case class Section(
      index: Int,
      name: String,
      kind: Int,
      flags: Long,
      size: Int,
      link: Int,
      info: Int,
      bytes: Vec[Byte]
  )
  case class Parameter(ordinal: Int, offset: Int, size: Int)
  case class Kernel(
      name: String,
      section: Int,
      parameterBase: Int,
      parameterSize: Int,
      parameters: Vec[Parameter]
  )
  case class Global(name: String, bytes: Vec[Byte], constantSection: Option[Int] = None, offset: Int = 0)
  case class Module(sm: Int, sections: Vec[Section], kernels: Vec[Kernel], globals: Vec[Global])

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
    headers.foreach(section => requireImage(section.size <= 64 * 1024 * 1024, "cubin section is too large"))
    val strings  = checked(elf.sectionBytes(image, headers(header.names)))
    val sections = headers.zipWithIndex.map { (section, index) =>
      Section(
        index,
        checked(elf.stringAt(strings, section.nameOffset)),
        section.sectionType,
        section.flags,
        section.size,
        section.link,
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
        Parameter(v.getShort(4) & 65535, v.getShort(6) & 65535, v.getInt(8) >>> 18)
      }.sortBy(_.ordinal)
      val cbank = attrs.collectFirst { case (0x0a, value) =>
        requireImage(value.size == 8, "invalid cubin parameter constant bank")
        val v = ByteBuffer.wrap(value.toArray).order(ByteOrder.LITTLE_ENDIAN)
        (v.getShort(4) & 65535, v.getShort(6) & 65535)
      }.getOrElse((0x160, 0))
      requireImage(
        parameters.zipWithIndex.forall((p, i) => p.ordinal == i && p.size > 0 && p.offset + p.size <= cbank._2),
        "invalid cubin parameter layout"
      )
      Kernel(name, section.index, cbank._1, cbank._2, parameters)
    }
    val globals = sections.filter(_.kind == 2).flatMap { section =>
      requireImage(section.link > 0 && section.link < count && section.size % 24 == 0, "invalid cubin symbol table")
      val stride = headers(section.index).entrySize
      requireImage(stride == 0 || stride == 24, "invalid cubin symbol table stride")
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
            start >= 0 && size >= 0 && start <= sections(target).size && size <= sections(target).size - start,
            "invalid cubin global range"
          )
          val name = checked(elf.stringAt(sections(section.link).bytes, symbol.nameOffset))
          requireImage(validName(name), "invalid cubin global name")
          requireImage(size <= 1024 * 1024, "cubin global exceeds supported initializer size")
          Some(Global(
            name,
            if (sections(target).kind == 8) Vec.fill(size.toInt)(0.toByte)
            else sections(target).bytes.slice(start.toInt, (start + size).toInt),
            Option.when(sections(target).name.startsWith(".nv.constant"))(target),
            start.toInt
          ))
        } else None
      }
    }
    Right(Module(sm, sections, kernels, globals))
  } catch { case NonFatal(error) => Left(Option(error.getMessage).getOrElse("invalid cubin")) }
}
