package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.isa.ValidationResult

import cats.syntax.all.*

enum CompileError {
  case Syntax(error: ParseError)
  case MissingEntry
  case Lowering(error: LoweringError)

  def message: String = this match {
    case Syntax(error)   => error.toString
    case MissingEntry    => "missing .entry declaration"
    case Lowering(error) => error.message
  }
}

object ISA {
  val version: PTXVersion               = Instruction.version
  val instructions: Col[InstructionDef] = Instruction.definitions

  def parse(source: String): Either[ParseError, Program] = Parser.parse(source)

  def render(program: Program): String = Printer.render(program)

  def validate(program: Program, target: SMTarget): ValidationResult[ValidationIssue] =
    Validator.validate(program, version, target)

  def lower(entry: Statement.Entry): Either[LoweringError, Kernel] = Lowering.lower(entry)

  def compile(source: String): Either[CompileError, Kernel] =
    parse(source).leftMap(CompileError.Syntax.apply).flatMap { program =>
      program.statements.collectFirst { case entry: Statement.Entry => entry }
        .toRight(CompileError.MissingEntry)
        .flatMap(lower(_).leftMap(CompileError.Lowering.apply))
    }
}
