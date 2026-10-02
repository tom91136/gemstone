package net.kurobako.gemstone.isa.sass.asm

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.SASSCodeWords.BundleControl as Control
import net.kurobako.gemstone.isa.sass.asm.Program.Statement.*

/** Canonical instruction syntax. Exact machine round trips add an explicit encoding annotation. */
object Printer extends isa.Encoder[Program, String, Error] {
  def print(program: Program): Either[Error, String] = encode(program)

  def print(instruction: Instruction): Either[String, String] = InstructionSyntax.print(instruction)

  def print(instruction: DecodedInstruction): Either[String, String] =
    print(instruction.operation).flatMap { text =>
      instruction.machine.fold[Either[String, String]](Right(text)) { machine =>
        MachineSyntax.print(instruction, machine).map(annotation => s"$text // encoding: $annotation")
      }
    }

  override def encode(program: Program): Either[Error, String] = {
    val header = program.target.toVec.map(target => s".target ${target.name}")
    program.statements.traverse {
      case Label(name) =>
        Either.cond(Parser.validName(name.raw), s"${name.raw}:", Error.Printing(Error.detail("invalid label name")))
      case Bundle(Control.Kepler(bytes)) =>
        Either.cond(
          bytes.size == 8,
          ".bundle kepler " + bytes.map(value => f"${value & 255}%02x").mkString,
          Error.Printing(Error.detail("Kepler bundle controls require eight bytes"))
        )
      case Bundle(Control.Maxwell(slots, reserved)) =>
        Either.cond(
          slots.size == 3,
          s".bundle maxwell ${reserved.raw} " + slots.map(value => f"${value.value.raw}%06x").mkString(" "),
          Error.Printing(Error.detail("Maxwell bundle controls require three slots"))
        )
      case Bundle(Control.Modern) =>
        Left(Error.Printing(Error.detail("modern instructions do not have a bundle directive")))
      case operation: Operation => printOperation(operation).left.map(value => Error.Printing(Error.detail(value)))
    }.map(lines => (header ++ lines).mkString("\n") + (if (header.isEmpty && program.statements.isEmpty) "" else "\n"))
  }

  private def printOperation(operation: Operation): Either[String, String] = operation.preserved match {
    case Some(decoded) =>
      if (decoded.operation != operation.instruction || operation.references.nonEmpty)
        Left("preserved encoding does not match the instruction")
      else Printer.print(decoded)
    case None if operation.references.isEmpty => Printer.print(operation.instruction)
    case None                                 =>
      val instruction = operation.instruction
      for {
        reference <- Parser.symbolicReference(operation)
        operands  <- instruction.operands.zipWithIndex.traverse { (operand, index) =>
          if (index == reference.operand.raw) Right(reference.name.raw) else OperandPrinter.print(operand)
        }
        guard <- instruction.guard.traverse(value => OperandPrinter.print(value.predicate))
      } yield guard.fold("")(value => s"@$value ") +
        (instruction.mnemonic.raw +: instruction.modifiers.map(_.name)).mkString(".") + " " + operands.mkString(", ") +
        ";"
  }
}
