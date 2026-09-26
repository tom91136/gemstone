package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.NonEmptyString
import net.kurobako.gemstone.CompileTime
import net.kurobako.gemstone.NonEmptyVec
import net.kurobako.gemstone.NonNegativeInt
import net.kurobako.gemstone.PositiveInt
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelName

import scala.annotation.targetName

final case class Program(statements: Vec[Program.Statement]) extends net.kurobako.gemstone.isa.Program

object Program {

  opaque type Opcode = NonEmptyString
  object Opcode {
    inline def either(value: String): Either[String, Opcode] = NonEmptyString.either(value)

    inline def unsafe(value: String): Opcode = NonEmptyString.unsafe(value)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): Opcode =
      NonEmptyString.unsafe(value)

    @targetName("fromNonEmptyString")
    inline def apply(inline value: NonEmptyString): Opcode = value

    extension (value: Opcode) {
      inline def raw: String               = NonEmptyString.rawOf(value)
      def toNonEmptyString: NonEmptyString = value
    }
  }

  opaque type ModifierName = NonEmptyString
  object ModifierName {
    inline def either(value: String): Either[String, ModifierName] = NonEmptyString.either(value)

    inline def unsafe(value: String): ModifierName = NonEmptyString.unsafe(value)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): ModifierName =
      NonEmptyString.unsafe(value)

    @targetName("fromNonEmptyString")
    inline def apply(inline value: NonEmptyString): ModifierName = value

    extension (value: ModifierName) {
      inline def raw: String               = NonEmptyString.rawOf(value)
      def toNonEmptyString: NonEmptyString = value
    }
  }

  opaque type ParameterName = NonEmptyString
  object ParameterName {
    inline def either(value: String): Either[String, ParameterName] = NonEmptyString.either(value)

    inline def unsafe(value: String): ParameterName = NonEmptyString.unsafe(value)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): ParameterName =
      NonEmptyString.unsafe(value)

    @targetName("fromNonEmptyString")
    inline def apply(inline value: NonEmptyString): ParameterName = value

    extension (value: ParameterName) {
      inline def raw: String               = NonEmptyString.rawOf(value)
      def toNonEmptyString: NonEmptyString = value
    }
  }

  opaque type RegisterName = NonEmptyString
  object RegisterName {
    inline def either(value: String): Either[String, RegisterName] = NonEmptyString.either(value)

    inline def unsafe(value: String): RegisterName = NonEmptyString.unsafe(value)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): RegisterName =
      NonEmptyString.unsafe(value)

    @targetName("fromNonEmptyString")
    inline def apply(inline value: NonEmptyString): RegisterName = value

    extension (value: RegisterName) {
      inline def raw: String               = NonEmptyString.rawOf(value)
      def toNonEmptyString: NonEmptyString = value
    }
  }

  opaque type RegisterPart = NonEmptyString
  object RegisterPart {
    inline def either(value: String): Either[String, RegisterPart] = NonEmptyString.either(value)

    inline def unsafe(value: String): RegisterPart = NonEmptyString.unsafe(value)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): RegisterPart =
      NonEmptyString.unsafe(value)

    @targetName("fromNonEmptyString")
    inline def apply(inline value: NonEmptyString): RegisterPart = value

    extension (value: RegisterPart) {
      inline def raw: String               = NonEmptyString.rawOf(value)
      def toNonEmptyString: NonEmptyString = value
    }
  }

  opaque type QualifierName = NonEmptyString
  object QualifierName {
    inline def either(value: String): Either[String, QualifierName] = NonEmptyString.either(value)

    inline def unsafe(value: String): QualifierName = NonEmptyString.unsafe(value)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): QualifierName =
      NonEmptyString.unsafe(value)

    @targetName("fromNonEmptyString")
    inline def apply(inline value: NonEmptyString): QualifierName = value

    extension (value: QualifierName) {
      inline def raw: String               = NonEmptyString.rawOf(value)
      def toNonEmptyString: NonEmptyString = value
    }
  }

  opaque type DirectiveName = NonEmptyString
  object DirectiveName {
    inline def either(value: String): Either[String, DirectiveName] = NonEmptyString.either(value)

    inline def unsafe(value: String): DirectiveName = NonEmptyString.unsafe(value)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): DirectiveName =
      NonEmptyString.unsafe(value)

    @targetName("fromNonEmptyString")
    inline def apply(inline value: NonEmptyString): DirectiveName = value

    extension (value: DirectiveName) {
      inline def raw: String               = NonEmptyString.rawOf(value)
      def toNonEmptyString: NonEmptyString = value
    }
  }

  opaque type FunctionName = NonEmptyString
  object FunctionName {
    inline def either(value: String): Either[String, FunctionName] = NonEmptyString.either(value)

    inline def unsafe(value: String): FunctionName = NonEmptyString.unsafe(value)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): FunctionName =
      NonEmptyString.unsafe(value)

    @targetName("fromNonEmptyString")
    inline def apply(inline value: NonEmptyString): FunctionName = value

    extension (value: FunctionName) {
      inline def raw: String               = NonEmptyString.rawOf(value)
      def toNonEmptyString: NonEmptyString = value
    }
  }

  opaque type SymbolName = NonEmptyString
  object SymbolName {
    inline def either(value: String): Either[String, SymbolName] = NonEmptyString.either(value)

    inline def unsafe(value: String): SymbolName = NonEmptyString.unsafe(value)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): SymbolName =
      NonEmptyString.unsafe(value)

    @targetName("fromNonEmptyString")
    inline def apply(inline value: NonEmptyString): SymbolName = value

    extension (value: SymbolName) {
      inline def raw: String               = NonEmptyString.rawOf(value)
      def toNonEmptyString: NonEmptyString = value
    }
  }

  opaque type LabelName = NonEmptyString
  object LabelName {
    inline def either(value: String): Either[String, LabelName] = NonEmptyString.either(value)

    inline def unsafe(value: String): LabelName = NonEmptyString.unsafe(value)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): LabelName =
      NonEmptyString.unsafe(value)

    @targetName("fromNonEmptyString")
    inline def apply(inline value: NonEmptyString): LabelName = value

    extension (value: LabelName) {
      inline def raw: String               = NonEmptyString.rawOf(value)
      def toNonEmptyString: NonEmptyString = value
    }
  }

  opaque type InstructionText = String
  object InstructionText {
    inline def either(value: String): Either[String, InstructionText] =
      if (value.isEmpty) Left("instruction text must not be empty") else Right(value)
    inline def unsafe(value: String): InstructionText =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmpty[A]): InstructionText =
      value
    extension (value: InstructionText) def raw: String = value
  }

  opaque type ImmediateText = String
  object ImmediateText {
    inline def either(value: String): Either[String, ImmediateText] =
      if (value.isEmpty) Left("immediate text must not be empty") else Right(value)
    inline def unsafe(value: String): ImmediateText =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmpty[A]): ImmediateText =
      value
    extension (value: ImmediateText) def raw: String = value
  }

  /** Lossless syntax used by generated, opcode-specific [[Instruction]] cases. */
  enum Operand {
    case Immediate(raw: ImmediateText)
    case Register(name: RegisterName, part: Option[RegisterPart])
    case Symbol(name: SymbolName)
    case StringLiteral(rawContents: String)
    case Qualifier(path: NonEmptyVec[QualifierName])
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

  enum DataType(val mnemonic: String, val widthBits: PositiveInt, val kind: Char) {
    case B8   extends DataType("b8", PositiveInt(8), 'b')
    case B16  extends DataType("b16", PositiveInt(16), 'b')
    case B32  extends DataType("b32", PositiveInt(32), 'b')
    case B64  extends DataType("b64", PositiveInt(64), 'b')
    case B128 extends DataType("b128", PositiveInt(128), 'b')
    case U8   extends DataType("u8", PositiveInt(8), 'u')
    case U16  extends DataType("u16", PositiveInt(16), 'u')
    case U32  extends DataType("u32", PositiveInt(32), 'u')
    case U64  extends DataType("u64", PositiveInt(64), 'u')
    case S8   extends DataType("s8", PositiveInt(8), 's')
    case S16  extends DataType("s16", PositiveInt(16), 's')
    case S32  extends DataType("s32", PositiveInt(32), 's')
    case S64  extends DataType("s64", PositiveInt(64), 's')
    case F16  extends DataType("f16", PositiveInt(16), 'f')
    case F32  extends DataType("f32", PositiveInt(32), 'f')
    case F64  extends DataType("f64", PositiveInt(64), 'f')
  }

  enum DeclarationQualifier {
    case Alignment(bytes: PositiveInt)
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
        count: Option[PositiveInt],
        initializer: Vec[Operand]
    )
    case Directive(
        path: NonEmptyVec[DirectiveName],
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
      offset: NonNegativeInt,
      line: PositiveInt,
      column: PositiveInt,
      expected: Vec[String]
  ) {
    def message: String = {
      val expectation = if (expected.isEmpty) "valid PTX syntax" else expected.mkString("; ")
      s"$line:$column (offset $offset): expected $expectation"
    }

    override def toString: String = message
  }

}
