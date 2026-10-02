package net.kurobako.gemstone.isa.sass.asm

import cats.syntax.all.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.*

/** Logical instruction text independent of program statements and machine annotations. */
private object InstructionSyntax {
  def parse(source: String): Either[String, Instruction] = {
    val text                                                         = source.trim.stripSuffix(";").trim
    val (prefix, tail)                                               = text.span(!_.isWhitespace)
    val guarded: Either[String, (Option[Instruction.Guard], String)] = if (prefix.startsWith("@")) {
      SASSOperandParser.parse(prefix.drop(1)).flatMap {
        case predicate: Operand.Predicate => Right(Some(Instruction.Guard(predicate)) -> tail.trim)
        case _                            => Left(s"invalid instruction guard $prefix")
      }
    } else Right(None -> text)
    guarded.flatMap { case (guard, body) =>
      val (qualified, operandsText) = body.span(!_.isWhitespace)
      val parts                     = qualified.split('.').toVec
      for {
        _ <- Either.cond(
          !qualified.endsWith(".") && !qualified.contains(".."),
          (),
          s"empty SASS opcode qualifier $qualified"
        )
        mnemonic    <- parts.headOption.toRight(s"unknown SASS opcode $qualified")
        operands    <- SASSOperandParser.parseMany(operandsText.trim)
        instruction <- Instruction.decode(
          mnemonic,
          parts.drop(1).map(Instruction.Modifier(_)),
          operands,
          guard
        )
      } yield instruction
    }
  }

  def print(instruction: Instruction): Either[String, String] = for {
    operands <- instruction.operands.traverse(OperandPrinter.print(_))
    guard    <- instruction.guard.traverse(value => OperandPrinter.print(value.predicate))
  } yield {
    val opcode = (instruction.mnemonic.raw +: instruction.modifiers.map(_.name)).mkString(".")
    guard.fold("")(value => s"@$value ") + opcode +
      (if (operands.isEmpty) "" else " " + operands.mkString(", ")) + ";"
  }

}
