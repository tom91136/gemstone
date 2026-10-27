package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

object CodeObject {
  def decode(image: Vec[Byte]): Either[CodeObjectError, CodeObject] =
    ELF.validateHeader(image).map(_ => CodeObject(image))
}

/** ELF input with a validated header; full indexing is deferred until it is needed. */
case class CodeObject private (image: Vec[Byte]) {

  /** The processor target recorded in the code object's ISA note, which selects instruction encodings. */
  def target: Either[CodeObjectError, Target] = ELF.index(image).map(_.target)

  /** Executable bytes with locally resolvable relocations applied to a private copy. */
  def executableSections: Either[CodeObjectError, Vec[ExecutableSection]] = ELF.executableSections(image)

  /** This code object with kernel `name`'s code replaced by `code` of the same size, at its function symbol. */
  def patchKernel(name: NonEmpty[String], code: Vec[Byte]): Either[CodeObjectError, CodeObject] = (for {
    parsed <- elf.Image.parse(image)
    symbol <- parsed.symbols.flatMap(_.find(symbol =>
      symbol.name.contains(name) && symbol.kind.raw == elf.SymbolKind.Function
    )).toRight(s"kernel $name is not in the code object": NonEmpty[String])
    _ <- Either.cond(
      code.size == symbol.size.raw,
      (),
      s"kernel $name holds ${symbol.size} bytes, not ${code.size}": NonEmpty[String]
    )
    (section, offset) <- parsed.symbolOffset(symbol)
    patched           <- parsed.patched(section, parsed.contents(section).patch(offset, code, code.size))
  } yield CodeObject(patched.bytes)).leftMap(CodeObjectError.Invalid(_))

  /** This code object with kernel `name`'s code replaced by `code` of any size. Shorter or equal code stays in place;
    * longer code grows in place when the kernel ends its section, and otherwise moves to the end of the section, with
    * its function symbols and its descriptor's entry offset following it. Either way the code grows only into unused
    * address space, so nothing else moves, and branches or addresses within `code` are the caller's.
    */
  def replaceKernel(name: NonEmpty[String], code: Vec[Byte]): Either[CodeObjectError, CodeObject] = {
    val alignment                                                        = 256L
    def functions(image: elf.Image): Vec[(elf.Section, Int, elf.Symbol)] = image.symbolTables.toVec.flatMap {
      (table, symbols) =>
        symbols.zipWithIndex.collect {
          case (symbol, index) if symbol.name.contains(name) && symbol.kind.raw == elf.SymbolKind.Function =>
            (image.sections(table), index, symbol)
        }
    }
    (for {
      parsed <- elf.Image.parse(image)
      found  <- functions(parsed).headOption.toRight(s"kernel $name is not in the code object": NonEmpty[String])
      (_, _, symbol) = found
      located <- parsed.symbolOffset(symbol)
      (section, offset) = located
      contents          = parsed.contents(section)
      last              = parsed.symbolTables.values.flatten.forall(other =>
        other.kind.raw != elf.SymbolKind.Function || other.section != symbol.section || other.value <= symbol.value
      )
      (placed, start) =
        if (code.size <= symbol.size)
          (
            contents.patch(offset, code ++ Vec.fill(symbol.size.raw - code.size)(0.toByte), symbol.size),
            offset.raw
          )
        else if (last) (contents.take(offset.raw) ++ code, offset.raw)
        else {
          val moved = contents.size.toLong.alignUp(alignment).toInt
          (contents ++ Vec.fill(moved - contents.size)(0.toByte) ++ code, moved)
        }
      address = section.address + start
      resized <- if (placed.size == contents.size) parsed.patched(section, placed) else parsed.resized(section, placed)
      renamed <- functions(resized).foldLeft[Either[NonEmpty[String], elf.Image]](Right(resized)) {
        case (result, (table, index, _)) => result.flatMap(image =>
            image.withSymbol(image.sections(table.index), index, address, code.size.toLong)
          )
      }
      described <-
        if (address == symbol.value) Right(renamed)
        else
          renamed.symbols.flatMap(_.find(_.name.exists(_.raw == s"$name.kd")))
            .toRight(s"kernel $name has no descriptor": NonEmpty[String]).flatMap { descriptor =>
              renamed.symbolOffset(descriptor).flatMap { (table, at) =>
                renamed.patched(
                  table,
                  renamed.contents(table).patch(at.raw + 16, LittleEndian.toVec(address - descriptor.value, 8), 8)
                )
              }
            }
    } yield CodeObject(described.bytes)).leftMap(CodeObjectError.Invalid(_))
  }
}
