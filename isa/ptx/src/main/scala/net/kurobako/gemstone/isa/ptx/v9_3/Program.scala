package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.ptx.v9_3.Instruction as PTXInstruction

object Program {

  object Operand {
    def isAddress(operand: Operand): Boolean = operand match {
      case Address(_) => true
      case _          => false
    }

    def isParenthesized(operand: Operand): Boolean = operand match {
      case Parenthesized(_) => true
      case _                => false
    }

    def isVectorLiteral(operand: Operand): Boolean = operand match {
      case VectorLiteral(_) => true
      case _                => false
    }

    def isWithPredicate(operand: Operand): Boolean = operand match {
      case WithPredicate(_, _) => true
      case _                   => false
    }
  }

  /** Lossless syntax used by generated, opcode-specific [[Instruction]] cases. */
  enum Operand {
    case Immediate(raw: NonEmpty[String])
    case Register(name: NonEmpty[String], part: Option[NonEmpty[String]])
    case Symbol(name: NonEmpty[String])
    case StringLiteral(rawContents: String)
    case Qualifier(path: NonEmpty[Vec[NonEmpty[String]]])
    case VectorLiteral(items: Vec[Operand])
    case Parenthesized(items: Vec[Operand])
    case Address(items: Vec[Operand])
    case Qualified(base: Operand, qualifier: NonEmpty[String])
    case Index(base: Operand, index: Operand)
    case Add(left: Operand, right: Operand)
    case Subtract(left: Operand, right: Operand)
    case WithPredicate(base: Operand, predicate: Operand)
    case Negate(operand: Operand)
  }

  case class Predication(negated: Boolean, operand: Operand)

  case class Modifier(name: NonEmpty[String])

  enum DirectiveSeparator {
    case Space
    case Comma
  }

  enum DirectiveTerminator {
    case Line
    case Semicolon
  }

  case class DirectiveArgument(separator: DirectiveSeparator, operand: Operand)

  enum DataAddressSpace(val mnemonic: String) {
    case Global extends DataAddressSpace("global")
    case Const  extends DataAddressSpace("const")
    case Shared extends DataAddressSpace("shared")
  }

  enum DataType(val mnemonic: String, val widthBits: Int > 0, val kind: Char) {
    case B8   extends DataType("b8", 8, 'b')
    case B16  extends DataType("b16", 16, 'b')
    case B32  extends DataType("b32", 32, 'b')
    case B64  extends DataType("b64", 64, 'b')
    case B128 extends DataType("b128", 128, 'b')
    case U8   extends DataType("u8", 8, 'u')
    case U16  extends DataType("u16", 16, 'u')
    case U32  extends DataType("u32", 32, 'u')
    case U64  extends DataType("u64", 64, 'u')
    case S8   extends DataType("s8", 8, 's')
    case S16  extends DataType("s16", 16, 's')
    case S32  extends DataType("s32", 32, 's')
    case S64  extends DataType("s64", 64, 's')
    case F16  extends DataType("f16", 16, 'f')
    case F32  extends DataType("f32", 32, 'f')
    case F64  extends DataType("f64", 64, 'f')
  }

  enum DeclarationQualifier {
    case Alignment(bytes: Int > 0)
    case Attribute(name: NonEmpty[String])
    case Value(value: NonEmpty[String])
  }

  case class Parameter(
      qualifiers: Vec[DeclarationQualifier],
      elementType: DataType,
      name: NonEmpty[String],
      dimensions: Vec[Operand]
  )

  enum Statement {
    case Label(name: NonEmpty[String])
    case Data(
        addressSpace: DataAddressSpace,
        qualifiers: Vec[DeclarationQualifier],
        elementType: DataType,
        name: NonEmpty[String],
        count: Option[Int > 0],
        initializer: Vec[Operand]
    )
    case Directive(
        path: NonEmpty[Vec[NonEmpty[String]]],
        arguments: Vec[DirectiveArgument],
        terminator: DirectiveTerminator
    )
    case Instruction(value: PTXInstruction)

    /** Parsed instruction whose opcode is valid PTX syntax but is outside the currently implemented semantic subset. It
      * remains in the syntax tree so libraries can be loaded and inspected; lowering reports it when executed.
      */
    case UnsupportedInstruction(
        opcode: NonEmpty[String],
        operands: Vec[Operand],
        mods: Vec[Modifier],
        pred: Option[Predication]
    )
    case Block(body: Vec[Statement])
    case Entry(visible: Boolean, name: NonEmpty[String], parameters: Vec[Parameter], body: Vec[Statement])
    case Function(
        visible: Boolean,
        name: NonEmpty[String],
        returnParameters: Vec[Parameter],
        parameters: Vec[Parameter],
        body: Option[Vec[Statement]]
    )
  }

  case class ParseError(
      offset: Int >= 0,
      line: Int > 0,
      column: Int > 0,
      expected: Vec[String]
  ) {
    def message: NonEmpty[String] = {
      val expectation = if (expected.isEmpty) "valid PTX syntax" else expected.mkString("; ")
      s"$line:$column (offset $offset): expected $expectation"
    }

    override def toString: String = message
  }

}

case class Program(statements: Vec[Program.Statement])
