package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*

object CodeObject extends Decoder[Vec[Byte], CodeObject, CodeObjectError] {
  override def decode(image: Vec[Byte]): Either[CodeObjectError, CodeObject] =
    ELF.validateHeader(image).map(_ => CodeObject(image))

}

/** ELF input with a validated header; full indexing is deferred until it is needed. */
case class CodeObject private (image: Vec[Byte]) {

  /** Executable bytes with locally resolvable relocations applied to a private copy. */
  def executableSections: Either[CodeObjectError, Vec[ExecutableSection]] = ELF.executableSections(image)

  def relocatedSections(
      symbols: Map[String, Long],
      sectionAddresses: Map[String, Long]
  ): Either[CodeObjectError, Vec[ExecutableSection]] =
    ELF.executableSections(image, symbols, sectionAddresses)
}
