package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col

private[v9_3] def normalizeOpcode(value: String): String =
  value.map(character => if (character >= 'A' && character <= 'Z') (character + ('a' - 'A')).toChar else character)

/** Lossless syntax used by generated, opcode-specific [[Instruction]] cases. */
enum Operand {
  case Immediate(raw: String)
  case Register(name: String, part: Option[String])
  case Symbol(name: String)
  case StringLiteral(rawContents: String)
  case Qualifier(path: Col[String])
  case VectorLiteral(items: Col[Operand])
  case Parenthesized(items: Col[Operand])
  case Address(items: Col[Operand])
  case Index(base: Operand, index: Operand)
  case Add(left: Operand, right: Operand)
  case Subtract(left: Operand, right: Operand)
  case WithPredicate(base: Operand, predicate: Operand)
  case Negate(operand: Operand)
}

final case class Program(statements: Col[Statement])

final case class Predication(negated: Boolean, operand: Operand)

final case class Modifier(name: String)

enum DirectiveSeparator {
  case Space
  case Comma
}

enum DirectiveTerminator {
  case Line
  case Semicolon
}

final case class DirectiveArgument(separator: DirectiveSeparator, operand: Operand)

final case class Parameter(qualifiers: Col[String], name: String, dimensions: Col[Operand])

enum Statement {
  case Label(name: String)
  case Directive(
      path: Col[String],
      arguments: Col[DirectiveArgument],
      terminator: DirectiveTerminator
  )
  case Instruction(value: net.kurobako.gemstone.isa.ptx.v9_3.Instruction)
  case Block(body: Col[Statement])
  case Entry(visible: Boolean, name: String, parameters: Col[Parameter], body: Col[Statement])
  case Function(
      visible: Boolean,
      name: String,
      returnParameters: Col[Parameter],
      parameters: Col[Parameter],
      body: Option[Col[Statement]]
  )
}

final case class ParseError(offset: Int, line: Int, column: Int, expected: Col[String]) {
  override def toString: String = {
    val expectation = if (expected.isEmpty) "valid PTX syntax" else expected.mkString("; ")
    s"$line:$column (offset $offset): expected $expectation"
  }
}

/** Specification metadata attached to one generated instruction definition. */
final case class Metadata(entries: Col[Metadata.Entry]) {
  require(entries.nonEmpty, "instruction metadata must contain at least one specification entry")
}

object Metadata {

  /** One PTX documentation section that defines the owning instruction. */
  final case class Entry(
      id: String,
      heading: String,
      title: String,
      category: String,
      syntax: Col[String],
      introduced: PTXVersion,
      minimumTarget: SMTarget,
      forms: Col[InstructionForm]
  )
}
