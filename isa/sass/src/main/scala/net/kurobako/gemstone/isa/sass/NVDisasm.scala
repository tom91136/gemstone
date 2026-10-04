package net.kurobako.gemstone.isa.sass

import cats.Eval
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.*
import scala.collection.mutable
import scala.util.Using
import upickle.default.ReadWriter
import upickle.default.read

/** Recovered NVIDIA disassembler descriptions: instruction forms, bit assignments, syntax and legality constraints. */
object NVDisasm {

  /** Semantic FORMAT field classes. `Vocabulary(name)` keeps vendor-defined identifiers as data. */
  enum FieldKind {
    case Opcode, Register, UniformRegister, ZeroRegister, NonZeroRegister, ZeroUniformRegister, NonZeroUniformRegister
    case Predicate, PredicateVimnmx, UniformPredicate, PredicateRegister, Immediate, SignedImmediate, RelativeImmediate
    case FloatingImmediate(format: FloatEncoding)
    case ConstantBank, ConditionCode, OptionalConditionCode, ConditionRegisterBank, SpecialRegister, DescriptorRegister
    case BarrierRegister, Scoreboard, CbuState, StepField, AddressQualifier, PartitionSelector, InstructionSelector
    case Vocabulary(name: String)
    def isRegister: Boolean = this match {
      case Register | UniformRegister | ZeroRegister | NonZeroRegister | ZeroUniformRegister | NonZeroUniformRegister =>
        true
      case _ => false
    }
    def isPredicate: Boolean         = this == Predicate || this == UniformPredicate
    def isPredicateOperand: Boolean  = isPredicate || this == PredicateVimnmx
    def isFloatingImmediate: Boolean = this match {
      case FloatingImmediate(_) => true
      case _                    => false
    }
  }

  /** Resolve a vendor FORMAT field spelling once at ingestion. Exact names take precedence; vocabulary names remain
    * payloads and are never promoted to semantics merely because they happen to contain a familiar word.
    */
  def fieldKind(name: String): FieldKind = name match {
    case "Opcode"                                 => FieldKind.Opcode
    case "Register" | "RegisterFAU"               => FieldKind.Register
    case "UniformRegister"                        => FieldKind.UniformRegister
    case "ZeroRegister"                           => FieldKind.ZeroRegister
    case "NonZeroRegister" | "NonZeroRegisterFAU" => FieldKind.NonZeroRegister
    case "ZeroUniformRegister"                    => FieldKind.ZeroUniformRegister
    case "NonZeroUniformRegister"                 => FieldKind.NonZeroUniformRegister
    case "Predicate"                              => FieldKind.Predicate
    case "Predicate_vimnmx"                       => FieldKind.PredicateVimnmx
    case "UniformPredicate"                       => FieldKind.UniformPredicate
    case "PR"                                     => FieldKind.PredicateRegister
    case "UImm" | "BITSET"                        => FieldKind.Immediate
    case "SImm"                                   => FieldKind.SignedImmediate
    case "RSImm"                                  => FieldKind.RelativeImmediate
    case value @ ("F16Imm" | "F32Imm" | "F64Imm") =>
      FieldKind.FloatingImmediate(FloatEncoding.fromSpelling(value))
    case "C"                                                         => FieldKind.ConstantBank
    case "CC"                                                        => FieldKind.ConditionCode
    case "CCPR"                                                      => FieldKind.ConditionRegisterBank
    case "optCC"                                                     => FieldKind.OptionalConditionCode
    case "SpecialRegister"                                           => FieldKind.SpecialRegister
    case "DESC"                                                      => FieldKind.DescriptorRegister
    case "B" | "BD"                                                  => FieldKind.BarrierRegister
    case "Scoreboard"                                                => FieldKind.Scoreboard
    case "CBU_STATE" | "CBU_STATE_DIST"                              => FieldKind.CbuState
    case "STEP0"                                                     => FieldKind.StepField
    case "STRIDE"                                                    => FieldKind.AddressQualifier
    case value if value.startsWith("ONLY") || value.endsWith("ONLY") => FieldKind.AddressQualifier
    case value if value.startsWith("ISWZ")                           => FieldKind.InstructionSelector
    case "Partsel"                                                   => FieldKind.PartitionSelector
    case value                                                       => FieldKind.Vocabulary(value)
  }

  /** Float spellings in the compact resource stay strings on disk and are checked at resource ingestion. */
  object UnaryOperator {
    def from(spelling: String): UnaryOperator = spelling match {
      case "!"   => UnaryOperator.LogicalNot
      case "-"   => UnaryOperator.Negate
      case "~"   => UnaryOperator.BitwiseNot
      case value => UnaryOperator.Unsupported(value)
    }
  }

  enum UnaryOperator {
    case LogicalNot, Negate, BitwiseNot
    case Unsupported(vendorSpelling: String)
    def spelling: String = this match {
      case LogicalNot         => "!"
      case Negate             => "-"
      case BitwiseNot         => "~"
      case Unsupported(value) => value
    }
  }

  object BinaryOperator {
    def from(spelling: String): BinaryOperator = spelling match {
      case "&&"  => BinaryOperator.LogicalAnd
      case "||"  => BinaryOperator.LogicalOr
      case "->"  => BinaryOperator.Implies
      case "=="  => BinaryOperator.Equal
      case "!="  => BinaryOperator.NotEqual
      case "<"   => BinaryOperator.Less
      case "<="  => BinaryOperator.LessEqual
      case ">"   => BinaryOperator.Greater
      case ">="  => BinaryOperator.GreaterEqual
      case "+"   => BinaryOperator.Add
      case "-"   => BinaryOperator.Subtract
      case "*"   => BinaryOperator.Multiply
      case "/"   => BinaryOperator.Divide
      case "%"   => BinaryOperator.Remainder
      case "&"   => BinaryOperator.BitwiseAnd
      case "|"   => BinaryOperator.BitwiseOr
      case "^"   => BinaryOperator.BitwiseXor
      case "<<"  => BinaryOperator.ShiftLeft
      case ">>"  => BinaryOperator.ShiftRight
      case value => BinaryOperator.Unsupported(value)
    }
  }

  enum BinaryOperator {
    case LogicalAnd, LogicalOr, Implies, Equal, NotEqual, Less, LessEqual, Greater, GreaterEqual
    case Add, Subtract, Multiply, Divide, Remainder, BitwiseAnd, BitwiseOr, BitwiseXor, ShiftLeft, ShiftRight
    case Unsupported(vendorSpelling: String)
    def spelling: String = this match {
      case LogicalAnd         => "&&"
      case LogicalOr          => "||"
      case Implies            => "->"
      case Equal              => "=="
      case NotEqual           => "!="
      case Less               => "<"
      case LessEqual          => "<="
      case Greater            => ">"
      case GreaterEqual       => ">="
      case Add                => "+"
      case Subtract           => "-"
      case Multiply           => "*"
      case Divide             => "/"
      case Remainder          => "%"
      case BitwiseAnd         => "&"
      case BitwiseOr          => "|"
      case BitwiseXor         => "^"
      case ShiftLeft          => "<<"
      case ShiftRight         => ">>"
      case Unsupported(value) => value
    }
  }

  /** Known helper calls are closed; table lookup names and unknown vendor builtins remain explicit data. */
  object BuiltinCall {
    def from(name: String): BuiltinCall = name match {
      case "DEFINED" => BuiltinCall.Defined
      case "IsEven"  => BuiltinCall.IsEven
      case "IsOdd"   => BuiltinCall.IsOdd
      case value     => BuiltinCall.Table(value)
    }
  }

  enum BuiltinCall {
    case Defined, IsEven, IsOdd
    case Table(tableName: String)
    def name: String = this match {
      case Defined      => "DEFINED"
      case IsEven       => "IsEven"
      case IsOdd        => "IsOdd"
      case Table(value) => value
    }
  }

  enum FailureCategory { case IllegalCandidate, Unsupported, MissingMetadata }
  case class CandidateFailure(category: FailureCategory, message: String)

  enum MetadataIssueKind { case PermittedStructuralAmbiguity, AmbiguousInverse, MissingMetadata, Unsupported }
  object MetadataIssue   {

    /** Resource diagnostics retain their legacy serialized text. Classify them once when loading each form. */
    def fromResource(message: String): MetadataIssue =
      if (message.contains("VarLatOperandEnc") || message.startsWith("non-unique inverse of TABLES_opex_"))
        MetadataIssue(MetadataIssueKind.PermittedStructuralAmbiguity, message)
      else MetadataIssue(MetadataIssueKind.Unsupported, message)

    def unresolvedSelector(name: String): MetadataIssue =
      MetadataIssue(MetadataIssueKind.Unsupported, s"unresolved floating selector $name")
    def missingLookup(name: String): MetadataIssue =
      MetadataIssue(MetadataIssueKind.MissingMetadata, s"missing encoding lookup $name")
    def unsupportedLookup(name: String, issues: Vec[String]): MetadataIssue =
      MetadataIssue(MetadataIssueKind.Unsupported, s"unsupported lookup $name: ${issues.mkString("; ")}")
    def ambiguousInverse(name: String, value: BigInt): MetadataIssue = {
      val kind = if (name == "VarLatOperandEnc" || name.startsWith("TABLES_opex_"))
        MetadataIssueKind.PermittedStructuralAmbiguity
      else MetadataIssueKind.AmbiguousInverse
      MetadataIssue(kind, s"non-unique inverse of $name for $value")
    }
    def unresolvedExpression(source: String): MetadataIssue =
      MetadataIssue(MetadataIssueKind.Unsupported, s"unresolved encoding expression: $source")
  }

  case class MetadataIssue(kind: MetadataIssueKind, message: String) {
    def permittedForStructuralRoundTrip: Boolean = kind == MetadataIssueKind.PermittedStructuralAmbiguity
  }

  enum Syntax {
    case Text(raw: String)
    case Literal(value: String)
    case Field(
        kind: String,
        binding: String,
        arguments: Option[String],
        default: Option[String]
    )
    case Group(open: String, close: String, children: Vec[Syntax])
    case Optional(marker: Option[String], children: Vec[Syntax])
  }
  enum ConditionExpression {
    case Number(value: BigInt)
    case StringValue(value: String)
    case Field(name: String, attribute: Option[String])
    case Parameter(name: String)
    case Constant(name: String)
    case EnumValue(kind: String, name: String)
    case Unary(operator: UnaryOperator, value: ConditionExpression)
    case Binary(operator: BinaryOperator, left: ConditionExpression, right: ConditionExpression)
    case Call(name: BuiltinCall, arguments: Vec[ConditionExpression])
    case Conditional(condition: ConditionExpression, whenTrue: ConditionExpression, whenFalse: ConditionExpression)
  }
  enum ConditionException { case None, UnobservableScoreboardEncoding }
  object Condition        {
    private def scheduling(expression: NVDisasm.ConditionExpression): Boolean = expression match {
      case NVDisasm.ConditionExpression.Call(
            NVDisasm.BuiltinCall.Defined,
            Vec(NVDisasm.ConditionExpression.Call(NVDisasm.BuiltinCall.Table(name), _))
          ) =>
        name.startsWith("TABLES_opex_")
      case NVDisasm.ConditionExpression.Binary(NVDisasm.BinaryOperator.Implies, _, right) =>
        references(right).nonEmpty &&
        references(right).subsetOf(Set("usched_info", "batch_t"))
      case _ => false
    }

    private def references(expression: NVDisasm.ConditionExpression): Set[String] = expression match {
      case NVDisasm.ConditionExpression.Field(name, _)         => Set(name)
      case NVDisasm.ConditionExpression.Unary(_, value)        => references(value)
      case NVDisasm.ConditionExpression.Binary(_, left, right) => references(left) ++ references(right)
      case NVDisasm.ConditionExpression.Conditional(condition, whenTrue, whenFalse) => references(condition) ++
          references(whenTrue) ++ references(whenFalse)
      case NVDisasm.ConditionExpression.Call(_, arguments) => arguments.flatMap(references(_)).toSet
      case _                                               => Set.empty
    }
  }
  case class Condition(category: String, expression: ConditionExpression, message: String) {
    val isError: Boolean      = category == "ERROR" || category.endsWith("_ERROR")
    val isScheduling: Boolean = Condition.scheduling(expression)

    /** Legacy resource wording is classified once with the condition, never consulted by runtime policy. */
    lazy val exception: ConditionException =
      if (
        category == "ILLEGAL_INSTR_ENCODING_ERROR" &&
        message.startsWith("Illegal instruction encoding: Instruction type INST_TYPE_MIO_RD_SCBD") &&
        expression == ConditionExpression.Binary(
          BinaryOperator.Equal,
          ConditionExpression.Field("dst_wr_sb", None),
          ConditionExpression.Number(7)
        )
      ) ConditionException.UnobservableScoreboardEncoding
      else ConditionException.None
  }
  enum Expression {
    case Constant(value: BigInt, relaxed: Boolean)
    case Reference(name: String, attribute: Option[String], relaxed: Boolean)
    case Scale(value: Expression, factor: BigInt)
    case Multiply(value: Expression, factor: BigInt)
    case Identity(first: Expression, second: Expression)
    case FixedFloat(value: Expression, format: FloatEncoding)
    case Relaxed(value: Expression)
    case FloatConvert(
        value: Expression,
        selector: String,
        cases: Vec[(Vec[BigInt], FloatEncoding)],
        fallback: FloatEncoding
    )
    case Lookup(name: String, arguments: Vec[Expression])
    case Unsupported(source: String)
  }
  case class SymbolValue(name: String, value: BigInt)
  case class Vocabulary(name: String, values: Vec[SymbolValue], default: Option[BigInt] = None)
  case class Assignment(name: String, positions: Vec[Int], expression: Expression)
  case class OpcodeValue(name: String, value: BigInt)
  case class Form(
      formId: Option[Int],
      name: String,
      alternate: Boolean,
      opcodes: Vec[OpcodeValue],
      fields: Vec[Assignment],
      issues: Vec[String],
      syntax: Vec[Syntax] = Vec.empty,
      conditions: Vec[Condition] = Vec.empty
  ) {
    lazy val typedIssues: Vec[MetadataIssue] = issues.map(MetadataIssue.fromResource(_))
    lazy val assignedMask: BigInt            = fields.foldLeft(BigInt(0)) { (mask, field) =>
      field.positions.foldLeft(mask)(_.setBit(_))
    }
  }
  case class Row(inputs: Vec[BigInt], output: BigInt) derives ReadWriter
  case class LookupTable(name: String, rows: Vec[Row], issues: Vec[String] = Vec.empty) {
    lazy val byOutput: Map[BigInt, Vec[Row]] = rows.groupBy(_.output)
  }
  case class Database(
      target: String,
      sha256: String,
      width: Int,
      forms: Vec[Form],
      tables: Vec[LookupTable],
      symbols: Vec[Vocabulary] = Vec.empty,
      parameters: Map[String, BigInt] = Map.empty,
      constants: Map[String, BigInt] = Map.empty
  ) {
    lazy val byForm: Map[(String, Option[Int]), Form] = forms.map(form => (form.name -> form.formId) -> form).toMap
    lazy val byVocabulary: Map[String, Vocabulary]    = symbols.map(vocabulary => vocabulary.name -> vocabulary).toMap
    lazy val byTable: Map[String, LookupTable]        = tables.map(table => table.name -> table).toMap

    lazy val opcodeGroups: Vec[(Vec[Int], Map[BigInt, Vec[Form]])] = forms.flatMap { form =>
      form.fields.collectFirst {
        case Assignment(_, positions, Expression.Reference("Opcode", None, _)) => positions -> form
      }
    }.groupMap(_._1)(_._2).toVec.map { case (positions, group) =>
      positions -> group.flatMap(form => form.opcodes.map(_.value).distinct.map(_ -> form)).groupMap(_._1)(_._2)
    }
  }

  private case class AssignmentRow(name: Int, positions: Vec[Int], expression: Int) derives ReadWriter
  private enum ExpressionRow derives ReadWriter {
    case Constant(value: BigInt, relaxed: Boolean)
    case Reference(name: String, attribute: Option[String], relaxed: Boolean)
    case Scale(value: ExpressionRow, factor: BigInt)
    case Multiply(value: ExpressionRow, factor: BigInt)
    case Identity(first: ExpressionRow, second: ExpressionRow)
    case FixedFloat(value: ExpressionRow, format: String)
    case Relaxed(value: ExpressionRow)
    case FloatConvert(
        value: ExpressionRow,
        selector: String,
        cases: Vec[(Vec[BigInt], String)],
        fallback: String
    )
    case Lookup(name: String, arguments: Vec[ExpressionRow])
    case Unsupported(source: String)
  }
  private enum SyntaxNodeRow derives ReadWriter {
    case Text(raw: Int)
    case Literal(value: Int)
    case Field(kind: Int, binding: Int, arguments: Option[Int], default: Option[Int])
    case Group(open: Int, close: Int, children: Vec[Int])
    case Optional(marker: Option[Int], children: Vec[Int])
  }
  private enum ConditionExpressionRow derives ReadWriter {
    case Number(value: BigInt)
    case StringValue(value: Int)
    case Field(name: Int, attribute: Option[Int])
    case Parameter(name: Int)
    case Constant(name: Int)
    case EnumValue(kind: Int, name: Int)
    case Unary(operator: Int, value: Int)
    case Binary(operator: Int, left: Int, right: Int)
    case Call(name: Int, arguments: Vec[Int])
    case Conditional(condition: Int, whenTrue: Int, whenFalse: Int)
  }
  private case class ConditionRow(category: Int, expression: Int, message: Int) derives ReadWriter
  private case class FormRow(
      formId: Option[Int],
      name: Int,
      alternate: Boolean,
      opcodes: Vec[(Int, BigInt)],
      fields: Vec[Int],
      issues: Vec[Int],
      syntax: Option[Int],
      conditions: Vec[Int]
  ) derives ReadWriter
  private case class VocabularyRow(name: Int, values: Vec[(Int, BigInt)], default: Option[BigInt] = None)
      derives ReadWriter
  private case class LookupRow(name: Int, rows: Vec[Row], issues: Vec[Int]) derives ReadWriter
  private case class DatabaseRow(
      target: Int,
      digest: Int,
      width: Int,
      parameters: Vec[(Int, BigInt)],
      constants: Vec[(Int, BigInt)],
      forms: Vec[Int],
      tables: Vec[Int],
      symbols: Vec[Int]
  ) derives ReadWriter
  private case class Catalogue(
      format: Int,
      strings: Vec[String],
      expressions: Vec[ExpressionRow],
      syntaxNodes: Vec[SyntaxNodeRow],
      formats: Vec[Vec[Int]],
      conditionExpressions: Vec[ConditionExpressionRow],
      conditions: Vec[ConditionRow],
      assignments: Vec[AssignmentRow],
      forms: Vec[FormRow],
      vocabularies: Vec[VocabularyRow],
      lookups: Vec[LookupRow],
      databases: Vec[DatabaseRow]
  ) derives ReadWriter {
    // Keep the resource's shared rows shared in memory, decoding only rows used by a requested target.
    private lazy val decodedExpressions: Vec[Eval[Expression]] = expressions.map(row => Eval.later(expression(row)))
    private lazy val decodedAssignments: Vec[Eval[Assignment]] = assignments.map(row =>
      Eval.later(Assignment(strings(row.name), row.positions, decodedExpressions(row.expression).value))
    )

    private def expression(row: ExpressionRow): Expression = row match {
      case ExpressionRow.Constant(value, relaxed)            => Expression.Constant(value, relaxed)
      case ExpressionRow.Reference(name, attribute, relaxed) => Expression.Reference(name, attribute, relaxed)
      case ExpressionRow.Scale(value, factor)                => Expression.Scale(expression(value), factor)
      case ExpressionRow.Multiply(value, factor)             => Expression.Multiply(expression(value), factor)
      case ExpressionRow.Identity(first, second)   => Expression.Identity(expression(first), expression(second))
      case ExpressionRow.FixedFloat(value, format) =>
        Expression.FixedFloat(expression(value), FloatEncoding.fromSpelling(format))
      case ExpressionRow.Relaxed(value)                                 => Expression.Relaxed(expression(value))
      case ExpressionRow.FloatConvert(value, selector, cases, fallback) =>
        Expression.FloatConvert(
          expression(value),
          selector,
          cases.map { case (values, format) => values -> FloatEncoding.fromSpelling(format) },
          FloatEncoding.fromSpelling(fallback)
        )
      case ExpressionRow.Lookup(name, arguments) => Expression.Lookup(name, arguments.map(expression(_)))
      case ExpressionRow.Unsupported(source)     => Expression.Unsupported(source)
    }
    private lazy val decodedSyntax: Vec[Syntax] = {
      val previous = mutable.ArrayBuffer.empty[Syntax]
      syntaxNodes.foreach { node =>
        val value = node match {
          case SyntaxNodeRow.Text(raw)                                => Syntax.Text(strings(raw))
          case SyntaxNodeRow.Literal(value)                           => Syntax.Literal(strings(value))
          case SyntaxNodeRow.Field(kind, binding, arguments, default) =>
            Syntax.Field(strings(kind), strings(binding), arguments.map(strings(_)), default.map(strings(_)))
          case SyntaxNodeRow.Group(open, close, children) =>
            Syntax.Group(strings(open), strings(close), children.map(previous(_)))
          case SyntaxNodeRow.Optional(marker, children) =>
            Syntax.Optional(marker.map(strings(_)), children.map(previous(_)))
        }
        previous += value
      }
      previous.toVec
    }
    private lazy val decodedConditionExpressions: Vec[ConditionExpression] = {
      val previous = mutable.ArrayBuffer.empty[ConditionExpression]
      conditionExpressions.foreach { row =>
        val value = row match {
          case ConditionExpressionRow.Number(value)          => ConditionExpression.Number(value)
          case ConditionExpressionRow.StringValue(value)     => ConditionExpression.StringValue(strings(value))
          case ConditionExpressionRow.Field(name, attribute) =>
            ConditionExpression.Field(strings(name), attribute.map(strings(_)))
          case ConditionExpressionRow.Parameter(name)       => ConditionExpression.Parameter(strings(name))
          case ConditionExpressionRow.Constant(name)        => ConditionExpression.Constant(strings(name))
          case ConditionExpressionRow.EnumValue(kind, name) =>
            ConditionExpression.EnumValue(strings(kind), strings(name))
          case ConditionExpressionRow.Unary(operator, value) =>
            ConditionExpression.Unary(UnaryOperator.from(strings(operator)), previous(value))
          case ConditionExpressionRow.Binary(operator, left, right) =>
            ConditionExpression.Binary(BinaryOperator.from(strings(operator)), previous(left), previous(right))
          case ConditionExpressionRow.Call(name, arguments) =>
            ConditionExpression.Call(BuiltinCall.from(strings(name)), arguments.map(previous(_)))
          case ConditionExpressionRow.Conditional(condition, whenTrue, whenFalse) =>
            ConditionExpression.Conditional(previous(condition), previous(whenTrue), previous(whenFalse))
        }
        previous += value
      }
      previous.toVec
    }

    def database(row: DatabaseRow): Database = {
      val decodedForms = row.forms.map { id =>
        val encoded = forms(id)
        val form    = Form(
          encoded.formId,
          strings(encoded.name),
          encoded.alternate,
          encoded.opcodes.map { case (name, value) => OpcodeValue(strings(name), value) },
          encoded.fields.map(fieldId => decodedAssignments(fieldId).value),
          encoded.issues.map(strings(_)),
          encoded.syntax.map(formats(_)).getOrElse(Vec.empty[Int]).map(decodedSyntax),
          encoded.conditions.map { conditionId =>
            val condition = conditions(conditionId)
            Condition(
              strings(condition.category),
              decodedConditionExpressions(condition.expression),
              strings(condition.message)
            )
          }
        )
        val _ = form.typedIssues // force legacy resource issue classification while materializing the form
        form
      }
      Database(
        strings(row.target),
        strings(row.digest),
        row.width,
        decodedForms,
        row.tables.map { id =>
          val table = lookups(id)
          LookupTable(strings(table.name), table.rows, table.issues.map(strings(_)))
        },
        row.symbols.map { id =>
          val vocabulary = vocabularies(id)
          Vocabulary(
            strings(vocabulary.name),
            vocabulary.values.map { case (name, value) =>
              SymbolValue(strings(name), value)
            },
            vocabulary.default
          )
        },
        row.parameters.map { case (name, value) => strings(name) -> value }.toMap,
        row.constants.map { case (name, value) => strings(name) -> value }.toMap
      )
    }
  }

  private lazy val decoderCatalogue: Catalogue = {
    val stream = Option(getClass.getResourceAsStream("/net/kurobako/gemstone/isa/sass/decoders.json"))
      .getOrElse(throw IllegalStateException("missing compact SASS decoder resource"))
    Using.resource(stream) { input =>
      val catalogue = read[Catalogue](input)
      require(catalogue.format == 4, "unsupported SASS decoder catalogue format")
      val digests = catalogue.databases.map(row => catalogue.strings(row.digest))
      require(digests.distinct.size == digests.size, "duplicate SASS decoder digest")
      require(
        digests.forall(digest =>
          digest.length == 64 && digest.forall(c => (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'))
        ),
        "invalid SASS decoder digest"
      )
      catalogue
    }
  }

  private lazy val databaseLoaders: Map[String, () => Database] = decoderCatalogue.databases.map { row =>
    val digest       = decoderCatalogue.strings(row.digest)
    lazy val decoded = {
      val value = decoderCatalogue.database(row)
      require(value.sha256 == digest, "SASS decoder provenance mismatch")
      require(Set(64, 88, 128)(value.width), "invalid SASS decoder width")
      require(
        value.forms.forall(_.formId.forall(formId => formId >= 0 && formId < Metadata.catalogue.schemaCount)),
        "SASS decoder references an unknown instruction schema"
      )
      require(
        value.forms.forall(_.fields.forall(_.positions.forall(position => position >= 0 && position < value.width))),
        "SASS decoder field position is outside its instruction word"
      )
      require(value.symbols.map(_.name).distinct.size == value.symbols.size, "duplicate SASS decoder vocabulary")
      require(value.tables.map(_.name).distinct.size == value.tables.size, "duplicate SASS decoder lookup table")
      value.forms.flatMap(_.conditions).foreach(_.exception)
      value
    }
    digest -> (() => decoded)
  }.toMap

  private lazy val targets: Map[String, () => Database] = Metadata.catalogue.targets.map { target =>
    val load = databaseLoaders.getOrElse(
      target.encodingKey,
      throw IllegalStateException(s"missing decoder data for SASS target ${target.name}")
    )
    lazy val database = load().copy(target = target.name)
    target.name -> (() => database)
  }.toMap

  def forTarget(target: String): Option[Database] = targets.get(target).map(_())
}
