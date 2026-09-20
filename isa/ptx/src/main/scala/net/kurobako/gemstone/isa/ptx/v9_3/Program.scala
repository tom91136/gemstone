package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.execution.KernelName

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class Program(statements: Vec[Program.Statement])

object Program {

  type Opcode = Opcode.T
  object Opcode extends RefinedType[String, MinLength[1] & NoNul]

  type ModifierName = ModifierName.T
  object ModifierName extends RefinedType[String, MinLength[1] & NoNul]

  type ParameterName = ParameterName.T
  object ParameterName extends RefinedType[String, MinLength[1] & NoNul]

  type RegisterName = RegisterName.T
  object RegisterName extends RefinedType[String, MinLength[1] & NoNul]

  type RegisterPart = RegisterPart.T
  object RegisterPart extends RefinedType[String, MinLength[1] & NoNul]

  type QualifierName = QualifierName.T
  object QualifierName extends RefinedType[String, MinLength[1] & NoNul]

  type DirectiveName = DirectiveName.T
  object DirectiveName extends RefinedType[String, MinLength[1] & NoNul]

  type FunctionName = FunctionName.T
  object FunctionName extends RefinedType[String, MinLength[1] & NoNul]

  type SymbolName = SymbolName.T
  object SymbolName extends RefinedType[String, MinLength[1] & NoNul]

  type LabelName = LabelName.T
  object LabelName extends RefinedType[String, MinLength[1] & NoNul]

  type InstructionText = InstructionText.T
  object InstructionText extends RefinedType[String, MinLength[1]]

  type ImmediateText = ImmediateText.T
  object ImmediateText extends RefinedType[String, MinLength[1]]

  /** Lossless syntax used by generated, opcode-specific [[Instruction]] cases. */
  enum Operand {
    case Immediate(raw: ImmediateText)
    case Register(name: RegisterName, part: Option[RegisterPart])
    case Symbol(name: SymbolName)
    case StringLiteral(rawContents: String)
    case Qualifier(path: Vec[QualifierName] :| MinLength[1])
    case VectorLiteral(items: Vec[Operand])
    case Parenthesized(items: Vec[Operand])
    case Address(items: Vec[Operand])
    case Qualified(base: Operand, qualifier: QualifierName)
    case Index(base: Operand, index: Operand)
    case Add(left: Operand, right: Operand)
    case Subtract(left: Operand, right: Operand)
    case WithPredicate(base: Operand, predicate: Operand)
    case Negate(operand: Operand)
  }

  final case class Predication(negated: Boolean, operand: Operand)

  final case class Modifier(name: ModifierName)

  enum DirectiveSeparator {
    case Space
    case Comma
  }

  enum DirectiveTerminator {
    case Line
    case Semicolon
  }

  final case class DirectiveArgument(separator: DirectiveSeparator, operand: Operand)

  enum DataAddressSpace(val mnemonic: String) {
    case Global extends DataAddressSpace("global")
    case Const  extends DataAddressSpace("const")
    case Shared extends DataAddressSpace("shared")
  }

  enum DataType(val mnemonic: String, val widthBits: Int :| Positive, val kind: Char) {
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
    case Alignment(bytes: Int :| Positive)
    case Attribute(name: QualifierName)
    case Value(value: ImmediateText)
  }

  final case class Parameter(
      qualifiers: Vec[DeclarationQualifier],
      elementType: DataType,
      name: ParameterName,
      dimensions: Vec[Operand]
  )

  enum Statement {
    case Label(name: LabelName)
    case Data(
        addressSpace: DataAddressSpace,
        qualifiers: Vec[DeclarationQualifier],
        elementType: DataType,
        name: SymbolName,
        count: Option[Int :| Positive],
        initializer: Vec[Operand]
    )
    case Directive(
        path: Vec[DirectiveName] :| MinLength[1],
        arguments: Vec[DirectiveArgument],
        terminator: DirectiveTerminator
    )
    case Instruction(value: net.kurobako.gemstone.isa.ptx.v9_3.Instruction)

    /** Parsed instruction whose opcode is valid PTX syntax but is outside the currently implemented semantic subset. It
      * remains in the syntax tree so libraries can be loaded and inspected; lowering reports it when executed.
      */
    case UnsupportedInstruction(
        opcode: Opcode,
        operands: Vec[Operand],
        mods: Vec[Modifier],
        pred: Option[Predication]
    )
    case Block(body: Vec[Statement])
    case Entry(visible: Boolean, name: KernelName, parameters: Vec[Parameter], body: Vec[Statement])
    case Function(
        visible: Boolean,
        name: FunctionName,
        returnParameters: Vec[Parameter],
        parameters: Vec[Parameter],
        body: Option[Vec[Statement]]
    )
  }

  final case class ParseError(
      offset: Int :| Positive0,
      line: Int :| Positive,
      column: Int :| Positive,
      expected: Vec[String]
  ) {
    def message: String = {
      val expectation = if (expected.isEmpty) "valid PTX syntax" else expected.mkString("; ")
      s"$line:$column (offset $offset): expected $expectation"
    }

    override def toString: String = message
  }

}
