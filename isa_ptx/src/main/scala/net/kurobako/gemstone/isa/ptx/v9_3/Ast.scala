package net.kurobako.gemstone.isa.ptx.v9_3

/** Lossless-enough syntax tree for parsing, transforming, and canonically printing PTX source.
  *
  * Instructions deliberately share one representation. Opcode metadata is generated separately from the pinned PTX
  * specification, avoiding one Scala class for every opcode/modifier combination.
  */
enum Operand {
  case Immediate(raw: String)
  case Register(name: String, part: Option[String])
  case Symbol(name: String)
  case StringLiteral(rawContents: String)
  case Qualifier(path: Vector[String])
  case VectorLiteral(items: Vector[Operand])
  case Parenthesized(items: Vector[Operand])
  case Address(items: Vector[Operand])
  case Index(base: Operand, index: Operand)
  case Add(left: Operand, right: Operand)
  case Subtract(left: Operand, right: Operand)
  case WithPredicate(base: Operand, predicate: Operand)
  case Negate(operand: Operand)
}

final case class Program(statements: Vector[Statement])

final case class Predication(negated: Boolean, operand: Operand)

final case class Opcode(mnemonic: String)

final case class Modifier(name: String)

/** The common instruction shape used by both ISA modules. PTX adds predication as an orthogonal optional field. */
final case class Instruction(
    opcode: Opcode,
    operands: Vector[Operand] = Vector.empty,
    modifiers: Vector[Modifier] = Vector.empty,
    predication: Option[Predication] = None
) {
  def mnemonic: String = (opcode.mnemonic +: modifiers.map(_.name)).mkString(".")
}

enum DirectiveSeparator {
  case Space
  case Comma
}

enum DirectiveTerminator {
  case Line
  case Semicolon
}

final case class DirectiveArgument(separator: DirectiveSeparator, operand: Operand)

final case class Parameter(qualifiers: Vector[String], name: String, dimensions: Vector[Operand])

enum Statement {
  case Label(name: String)
  case Directive(
      path: Vector[String],
      arguments: Vector[DirectiveArgument],
      terminator: DirectiveTerminator
  )
  case Instruction(value: net.kurobako.gemstone.isa.ptx.v9_3.Instruction)
  case Block(body: Vector[Statement])
  case Entry(visible: Boolean, name: String, parameters: Vector[Parameter], body: Vector[Statement])
  case Function(
      visible: Boolean,
      name: String,
      returnParameters: Vector[Parameter],
      parameters: Vector[Parameter],
      body: Option[Vector[Statement]]
  )
}

final case class ParseError(offset: Int, line: Int, column: Int, expected: Vector[String]) {
  override def toString: String = {
    val expectation = if (expected.isEmpty) "valid PTX syntax" else expected.mkString("; ")
    s"$line:$column (offset $offset): expected $expectation"
  }
}

/** Runtime metadata generated from an instruction section in the pinned NVIDIA PTX document. */
final case class InstructionInfo(
    id: String,
    heading: String,
    title: String,
    category: String,
    mnemonics: Vector[String],
    syntax: Vector[String],
    introduced: PtxVersion = PtxVersion(1, 0),
    minimumTarget: SmTarget = SmTarget(10),
    forms: Vector[InstructionForm] = Vector.empty
)

trait InstructionCatalog {
  def version: String
  def instructions: Vector[InstructionInfo]

  lazy val byMnemonic: Map[String, Vector[InstructionInfo]] = {
    val pairs = instructions.flatMap { instruction =>
      val normalized = instruction.forms.map(_.mnemonic).distinct
      val mnemonics  = if (normalized.nonEmpty) normalized else instruction.mnemonics
      mnemonics.map(_.toLowerCase -> instruction)
    }
    pairs.groupMap(_._1)(_._2)
  }

  private lazy val knownMnemonics: Set[String] = instructions.flatMap(_.mnemonics).map(_.toLowerCase).toSet

  final def isKnown(opcode: Opcode): Boolean = knownMnemonics.contains(opcode.mnemonic.toLowerCase)
}

/** PTX ISA 9.3, as specified by the CUDA 13.3 archived PTX document. */
object Isa {
  val version: String             = "9.3"
  val catalog: InstructionCatalog = generated.Catalog
}
