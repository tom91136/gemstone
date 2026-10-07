package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.ptx.Instruction as PTXInstruction

object Program {
  enum DirectiveSeparator {
    case Space
    case Comma
  }

  enum DirectiveTerminator {
    case Line
    case Semicolon
  }

  case class DirectiveArgument(separator: DirectiveSeparator, operand: Operand)

  enum DataAddressSpace(val mnemonic: NonEmpty[String]) {
    case Global extends DataAddressSpace("global")
    case Const  extends DataAddressSpace("const")
    case Shared extends DataAddressSpace("shared")
    case Local  extends DataAddressSpace("local")
  }

  enum DataType(val mnemonic: NonEmpty[String], val widthBits: Int in (8, 16, 32, 64, 128), val kind: Char) {
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

    def byteCount: Int in (1, 2, 4, 8, 16) = widthBits.raw match {
      case 8   => 1
      case 16  => 2
      case 32  => 4
      case 64  => 8
      case 128 => 16
    }
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
      dimensions: Vec[Option[Operand]]
  )

  enum Linkage(val mnemonic: String) {
    case Default extends Linkage("")
    case Visible extends Linkage("visible")
    case Extern  extends Linkage("extern")
    case Weak    extends Linkage("weak")
  }

  enum Extent {
    case Scalar, Unsized
    case Sized(elements: Int > 0)
  }

  enum Statement {
    case Comment(text: NonEmpty[String])
    case Label(name: NonEmpty[String])
    case Data(
        addressSpace: DataAddressSpace,
        qualifiers: Vec[DeclarationQualifier],
        elementType: DataType,
        name: NonEmpty[String],
        extent: Extent,
        initialiser: Vec[Operand]
    )
    case Directive(
        path: NonEmpty[Vec[NonEmpty[String]]],
        arguments: Vec[DirectiveArgument],
        terminator: DirectiveTerminator,
        initialiser: Vec[Operand] = Vec.empty
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
    case CallPrototype(
        name: NonEmpty[String],
        returns: Vec[Parameter],
        parameters: Vec[Parameter],
        directives: Vec[Statement.Directive]
    )
    case Block(body: Vec[Statement])
    case Entry(
        linkage: Linkage,
        name: NonEmpty[String],
        parameters: Vec[Parameter],
        body: Option[Vec[Statement]],
        directives: Vec[Statement.Directive]
    )
    case Function(
        linkage: Linkage,
        name: NonEmpty[String],
        returnParameters: Vec[Parameter],
        parameters: Vec[Parameter],
        body: Option[Vec[Statement]],
        directives: Vec[Statement.Directive]
    )
  }
}

case class Program(statements: Vec[Program.Statement])
