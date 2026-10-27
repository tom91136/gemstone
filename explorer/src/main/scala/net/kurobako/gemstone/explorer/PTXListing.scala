package net.kurobako.gemstone.explorer

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.isa.ptx.Program.*
import net.kurobako.gemstone.isa.ptx.asm.*

/** PTX instruction addresses are ordinals within the routine, which the compiler numbers by counting the instructions
  * of the flattened body. Blocks are flattened in place, so a listing walks them in the same order.
  */
private object PTXListing {

  def lines(image: Vec[Byte]): Either[String, Vec[ProgramLine]] =
    Parser.parse(String(Vec.toArray(image), StandardCharsets.UTF_8)).left.map(_.diagnostic.raw).map { program =>
      val lines = Vec.newBuilder[ProgramLine]
      program.statements.foreach {
        case Statement.Entry(_, name, _, Some(body), _)       => routine(name.raw, body, lines)
        case Statement.Function(_, name, _, _, Some(body), _) => routine(name.raw, body, lines)
        case _                                                => ()
      }
      lines.result()
    }

  /** Numbers the routine's instructions and lists them, inlining blocks where the compiler inlines them. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def routine(
      function: String,
      body: Vec[Statement],
      lines: scala.collection.mutable.Builder[ProgramLine, Vec[ProgramLine]]
  ): Unit = {
    var ordinal                                = 0L
    def walk(statements: Vec[Statement]): Unit = statements.foreach {
      case Statement.Instruction(instruction) =>
        lines += ProgramLine(Some(function), ordinal, Printer.render(instruction).raw, instruction.opcode.raw)
        ordinal += 1L
      case Statement.Block(nested) => walk(nested)
      case _                       => ()
    }
    walk(body)
  }
}
