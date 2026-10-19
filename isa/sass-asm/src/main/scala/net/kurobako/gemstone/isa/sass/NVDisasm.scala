package net.kurobako.gemstone.isa.sass

import cats.Eval
import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.string.*
import scala.collection.mutable
import scala.reflect.ClassTag
import upickle.default.ReadWriter

/** Recovered NVIDIA disassembler descriptions: instruction forms, bit assignments, syntax and legality constraints. */
object NVDisasm {

  /** Semantic FORMAT field classes. `Vocabulary(name)` keeps vendor-defined identifiers as data. */
  enum FieldKind derives CanEqual {
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

    /** Fields that carry an instruction operand rather than a modifier. */
    def isOperand: Boolean = isRegister || isPredicateOperand || isFloatingImmediate ||
      (this match {
        case BarrierRegister | CbuState | Scoreboard | ConstantBank | DescriptorRegister | SpecialRegister | Immediate |
            SignedImmediate | RelativeImmediate | ConditionRegisterBank | PredicateRegister => true
        case _ => false
      })
  }

  /** Exact names take precedence; vocabulary names remain payloads and are never promoted to semantics merely because
    * they happen to contain a familiar word.
    */
  private val FormRegister = "[a-z0-9_]+__(v[0-9]+_)?R[a-d]".r

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
    case "SImm" | "SSImm"                         => FieldKind.SignedImmediate
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
    // the legacy packed-half forms give their register and swizzle fields form-specific vocabularies, such as
    // hset2__v2_Rd
    case value if FormRegister.matches(value) => FieldKind.Register
    case value if value.contains("_iswz_")    => FieldKind.InstructionSelector
    case value                                => FieldKind.Vocabulary(value)
  }

  enum FailureCategory derives CanEqual { case IllegalCandidate, Unsupported, MissingMetadata }
  case class CandidateFailure(category: FailureCategory, message: String)

  enum MetadataIssue {
    case UnresolvedSelector(name: String)
    case AmbiguousInverse(table: String, value: BigInt)

    def message: String = this match {
      case UnresolvedSelector(name)       => s"unresolved floating selector $name"
      case AmbiguousInverse(table, value) => s"non-unique inverse of $table for $value"
    }

    /** VarLatOperandEnc and the opex tables encode scheduling fields whose aliases do not change the instruction. */
    def structural: Boolean = this match {
      case AmbiguousInverse(table, _) => table == "VarLatOperandEnc" || table.startsWith("TABLES_opex_")
      case _                          => false
    }
  }

  def number(spelling: String): Option[BigInt] = parseBigIntLiteral(spelling.stripSuffix("*"), binary = true)

  object Syntax {
    def field(kind: String, binding: String, arguments: Option[String], default: Option[String]): Field =
      Field(kind, binding, arguments, default, fieldKind(kind))

    def fields(nodes: Vec[Syntax], includeMarkedOptionals: Boolean = false): Vec[Field] =
      nodes.flatMap {
        case field: Field                                          => Vec(field)
        case Group(_, _, children)                                 => fields(children, includeMarkedOptionals)
        case Optional(None, children)                              => fields(children, includeMarkedOptionals)
        case Optional(Some(_), children) if includeMarkedOptionals => fields(children, includeMarkedOptionals)
        case _                                                     => Vec.empty
      }

    /** A bracketed group of operand prefix operators only, which binds no field. */
    def operatorGroup(node: Syntax): Boolean = node match {
      case Group("[", "]", children) => children.forall {
          case Text(raw) => PartialFunction.cond(raw.trim) { case "!" | "-" | "~" | "||" => true }
          case _         => false
        }
      case _ => false
    }

    def kindBindings(fields: Vec[Field]): Map[String, String] = fields.groupBy(_.kind).collect {
      case (kind, values) if values.map(_.binding).distinct.size == 1 => kind -> values.head.binding
    }
  }

  enum Syntax derives CanEqual {
    case Text(raw: String)
    case Literal(value: String)
    case Field(kind: String, binding: String, arguments: Option[String], default: Option[String], category: FieldKind)
    case Group(open: String, close: String, children: Vec[Syntax])
    case Optional(marker: Option[String], children: Vec[Syntax])

    def isGuard: Boolean = this match {
      case Field(binding = "Pg" | "UPg") => true
      case _                             => false
    }
  }

  /** `requires` names a pre-encoding value the condition constrains; without it the condition cannot be observed. */
  case class Condition(expression: ConditionExpression, message: String, requires: Option[String] = None)

  case class SymbolValue(name: String, value: BigInt)
  case class Vocabulary(name: String, values: Vec[SymbolValue], default: Option[BigInt] = None)
  case class Assignment(name: String, positions: Vec[Int], expression: Expression) derives CanEqual

  /** One instruction form. Its FORMAT and binding analysis is computed once, on first use. */
  case class Form(
      name: String,
      alternate: Boolean,
      opcodes: Vec[SymbolValue],
      fields: Vec[Assignment],
      syntax: Vec[Syntax] = Vec.empty,
      conditions: Vec[Condition] = Vec.empty
  ) {
    lazy val assignedMask: BigInt = fields.foldLeft(BigInt(0)) { (mask, field) =>
      field.positions.foldLeft(mask)(_.setBit(_))
    }
    lazy val declared: Vec[Syntax.Field]       = Syntax.fields(syntax)
    lazy val kindBindings: Map[String, String] = Syntax.kindBindings(declared)
    lazy val encodedNames: Set[String]         = fields.flatMap(_.expression.references(bases = true)).toSet
    lazy val encodedReferences: Set[String]    = fields.flatMap(_.expression.references(bases = false)).toSet
    lazy val opcodeFields: Vec[Assignment]     = fields.filter(_.expression match {
      case Expression.Reference("Opcode", None, _) => true
      case _                                       => false
    })

    /** Float formats depend on selectors bound by the other fields. */
    lazy val orderedFields: Vec[Assignment] = fields.sortBy(_.expression match {
      case Expression.FloatConvert(_, _, _, _) => 1
      case _                                   => 0
    })

    /** The binding a FORMAT field is encoded under: its own, or its kind when conditions refer to the kind. */
    def encodedBinding(field: Syntax.Field): String =
      if (encodedNames(field.binding) || !kindBindings.get(field.kind).contains(field.binding)) field.binding
      else if (encodedNames(field.kind)) field.kind
      else field.binding
  }
  case class Row(inputs: Vec[BigInt], output: BigInt) derives ReadWriter
  case class LookupTable(name: String, rows: Vec[Row]) {
    lazy val byOutput: Map[BigInt, Vec[Row]] = rows.groupBy(_.output)

    /** The distinct outputs of the rows with exactly these inputs. */
    def lookup(inputs: Vec[BigInt]): Vec[BigInt] = rows.filter(_.inputs == inputs).map(_.output).distinct
  }

  /** The binding name of an attribute reference such as `Ra@negate`. */
  def bindingKey(name: String, attribute: Option[String]): String = attribute.fold(name)(suffix => s"$name@$suffix")
  case class Database(
      digest: String,
      width: Int in (64, 88, 128),
      forms: Vec[Form],
      tables: Vec[LookupTable],
      symbols: Vec[Vocabulary] = Vec.empty,
      parameters: Map[String, BigInt] = Map.empty,
      constants: Map[String, BigInt] = Map.empty
  ) {
    lazy val byForm: Map[String, Form]             = forms.map(form => form.name -> form).toMap
    lazy val byVocabulary: Map[String, Vocabulary] = symbols.map(vocabulary => vocabulary.name -> vocabulary).toMap
    lazy val byTable: Map[String, LookupTable]     = tables.map(table => table.name -> table).toMap

    lazy val opcodeGroups: Vec[(Vec[Int], Map[BigInt, Vec[Form]])] = forms.flatMap { form =>
      form.opcodeFields.headOption.map(_.positions -> form)
    }.groupMap(_._1)(_._2).toVec.map { case (positions, group) =>
      positions -> group.flatMap(form => form.opcodes.map(_.value).distinct.map(_ -> form)).groupMap(_._1)(_._2)
    }

    def vocabulary(field: Syntax.Field): Vec[SymbolValue] =
      byVocabulary.get(field.kind).fold(Vec.empty[SymbolValue])(_.values)

    private val defaults = ConcurrentHashMap[Syntax.Field, Vec[BigInt]]()

    /** A field's display default, resolved through its vocabulary when it names a symbol. */
    def defaultValues(field: Syntax.Field): Vec[BigInt] = defaults.computeIfAbsent(
      field,
      key =>
        key.default.toVec.flatMap { spelling =>
          val aliases = vocabulary(key).collect { case symbol if symbol.name == spelling => symbol.value }
          if (aliases.nonEmpty) aliases else number(spelling).toVec
        }.distinct
    )
  }

  private case class AssignmentRow(name: Int, positions: Vec[Int], expression: Int) derives ReadWriter
  private case class FormRow(
      name: Int,
      alternate: Boolean,
      opcodes: Vec[(Int, BigInt)],
      fields: Vec[Int],
      syntax: Vec[Int],
      conditions: Vec[Int]
  ) derives ReadWriter
  private case class VocabularyRow(name: Int, values: Vec[(Int, BigInt)], default: Option[BigInt]) derives ReadWriter
  private case class LookupRow(name: Int, rows: Vec[Row]) derives ReadWriter
  private case class DatabaseRow(
      digest: Int,
      width: Int in (64, 88, 128),
      parameters: Vec[(Int, BigInt)],
      constants: Vec[(Int, BigInt)],
      forms: Vec[Int],
      lookups: Vec[Int],
      vocabularies: Vec[Int]
  ) derives ReadWriter

  /** Rows shared across targets stay shared in memory; only databases used by a requested target are decoded. */
  private case class Catalogue(
      strings: Vec[String],
      expressions: Vec[Expression],
      syntaxNodes: Vec[SyntaxNode],
      conditionNodes: Vec[ConditionNode],
      conditions: Vec[ConditionRow],
      assignments: Vec[AssignmentRow],
      forms: Vec[FormRow],
      vocabularies: Vec[VocabularyRow],
      lookups: Vec[LookupRow],
      databases: Vec[DatabaseRow],
      targets: Vec[(Int, Int)]
  ) derives ReadWriter {
    private def sequential[A, B: ClassTag](rows: Vec[A])(decode: (A, Int => B) => B): Vec[B] = {
      val decoded = mutable.ArrayBuffer.empty[B]
      rows.foreach(row => decoded += decode(row, decoded))
      Vec.from(decoded)
    }

    private lazy val decodedSyntax: Vec[Syntax] = sequential[SyntaxNode, Syntax](syntaxNodes) { (node, previous) =>
      node match {
        case SyntaxNode.Text(raw)                                => Syntax.Text(strings(raw))
        case SyntaxNode.Literal(value)                           => Syntax.Literal(strings(value))
        case SyntaxNode.Field(kind, binding, arguments, default) =>
          Syntax.field(strings(kind), strings(binding), arguments.map(strings(_)), default.map(strings(_)))
        case SyntaxNode.Group(open, close, children) =>
          Syntax.Group(strings(open), strings(close), children.map(previous))
        case SyntaxNode.Optional(marker, children) => Syntax.Optional(marker.map(strings(_)), children.map(previous))
      }
    }

    private lazy val decodedConditions: Vec[ConditionExpression] =
      sequential[ConditionNode, ConditionExpression](conditionNodes) { (node, previous) =>
        node match {
          case ConditionNode.Number(value)          => ConditionExpression.Number(value)
          case ConditionNode.StringValue(value)     => ConditionExpression.StringValue(strings(value))
          case ConditionNode.Field(name, attribute) =>
            ConditionExpression.Field(strings(name), attribute.map(strings(_)))
          case ConditionNode.Parameter(name)        => ConditionExpression.Parameter(strings(name))
          case ConditionNode.Constant(name)         => ConditionExpression.Constant(strings(name))
          case ConditionNode.EnumValue(kind, name)  => ConditionExpression.EnumValue(strings(kind), strings(name))
          case ConditionNode.Unary(operator, value) =>
            ConditionExpression.Unary(UnaryOperator.from(strings(operator)), previous(value))
          case ConditionNode.Binary(operator, left, right) =>
            ConditionExpression.Binary(BinaryOperator.from(strings(operator)), previous(left), previous(right))
          case ConditionNode.Call(name, arguments) =>
            ConditionExpression.Call(BuiltinCall.from(strings(name)), arguments.map(previous))
          case ConditionNode.Conditional(condition, whenTrue, whenFalse) =>
            ConditionExpression.Conditional(previous(condition), previous(whenTrue), previous(whenFalse))
        }
      }

    private lazy val decodedAssignments: Vec[Eval[Assignment]] = assignments.map(row =>
      Eval.later(Assignment(strings(row.name), row.positions, expressions(row.expression)))
    )

    private lazy val decodedForms: Vec[Eval[Form]] = forms.map(row =>
      Eval.later(Form(
        strings(row.name),
        row.alternate,
        row.opcodes.map((name, value) => SymbolValue(strings(name), value)),
        row.fields.map(decodedAssignments(_).value),
        row.syntax.map(decodedSyntax),
        row.conditions.map { id =>
          val condition = conditions(id)
          Condition(
            decodedConditions(condition.expression),
            strings(condition.message),
            condition.requires.map(strings(_))
          )
        }
      ))
    )

    private def named(rows: Vec[(Int, BigInt)]): Map[String, BigInt] =
      rows.map((name, value) => strings(name) -> value).toMap

    private lazy val decodedDatabases: Vec[Eval[Database]] = databases.map(row =>
      Eval.later(Database(
        strings(row.digest),
        row.width,
        row.forms.map(decodedForms(_).value),
        row.lookups.map { id =>
          val lookup = lookups(id)
          LookupTable(strings(lookup.name), lookup.rows)
        },
        row.vocabularies.map { id =>
          val vocabulary = vocabularies(id)
          Vocabulary(
            strings(vocabulary.name),
            vocabulary.values.map((name, value) => SymbolValue(strings(name), value)),
            vocabulary.default
          )
        },
        named(row.parameters),
        named(row.constants)
      ))
    )

    lazy val byTarget: Map[String, Eval[Database]] =
      targets.map((name, database) => strings(name) -> decodedDatabases(database)).toMap
  }

  private lazy val catalogue: Catalogue = resources.json[Catalogue](getClass, "decoders.json")

  def supports(name: String): Boolean = catalogue.byTarget.contains(name)

  def database(name: String): Database = catalogue.byTarget(name).value
}
