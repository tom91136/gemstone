package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

object CodeObject extends Decoder[Vec[Byte], CodeObject, CodeObject.Error] {
  override def decode(image: Vec[Byte]): Either[Error, CodeObject] =
    ELF.validateHeader(image).map(_ => CodeObject(image))

  case class Symbol(name: NonEmpty[String], offset: Int >= 0, size: Int >= 0)
  case class Section(name: String, address: Long, bytes: Vec[Byte], symbols: Vec[Symbol])

  enum Error {
    case Invalid(detail: NonEmpty[String])
    case Unsupported(detail: NonEmpty[String])
    def message: NonEmpty[String] = this match {
      case Invalid(detail)     => detail
      case Unsupported(detail) => detail
    }
  }
}

/** ELF input with a validated header; full indexing is deferred until it is needed. */
case class CodeObject private (image: Vec[Byte]) {

  /** Executable bytes with locally resolvable relocations applied to a private copy. */
  def executableSections: Either[CodeObject.Error, Vec[CodeObject.Section]] = ELF.executableSections(image)

  def relocatedSections(
      symbols: Map[String, Long],
      sectionAddresses: Map[String, Long]
  ): Either[CodeObject.Error, Vec[CodeObject.Section]] =
    ELF.executableSections(image, symbols, sectionAddresses)
}
