package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.catalogue.*
import scala.collection.mutable
import upickle.default.ReadWriter

/** Generates instruction cases from the recovered opcode vocabulary and the target decoding catalogue. Execution
  * semantics remain handwritten; recovered encoding forms do not imply execution support.
  */
object GenerateSASSISA {
  private val PackageRoot       = "net/kurobako/gemstone/isa/sass"
  private val ConditionSections = Set("CONDITION", "CONDITIONS")

  case class Table(digest: String, description: SASSDescription.Description)
  case class Bundle(targets: Vector[(String, String)], tables: Vector[Table])

  private val command = Command("GenerateSASSISA", "Generate SASS forms from pinned recovered descriptions")((
    CodegenCommand.path("references"),
    CodegenCommand.path("generated-source-root"),
    CodegenCommand.path("generated-resource-root")
  ).tupled)

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) {
    case (references, sourceRoot, resourceRoot) =>
      val bundle   = read(references)
      val sources  = sourceRoot.resolve(PackageRoot)
      val shapes   = SASSInstructionModel.shapes(bundle.tables.flatMap(_.description.classes))
      val rendered = SASSInstructionRenderer.render(shapes, modifiers(bundle.tables)).map { (name, content) =>
        sources.resolve(name) -> content
      }
      rendered.foreach((path, content) => GeneratedFiles.writeScala(path, content))
      GeneratedFiles.removeUnexpected(sources, rendered.keySet, _.getFileName.toString.endsWith(".scala"))
      val resources = resourceRoot.resolve(PackageRoot)
      val catalogue = resources.resolve("decoders.json")
      GeneratedFiles.write(catalogue, renderJson(upickle.default.writeJs(renderCatalogue(bundle))))
      val domains =
        GenerateSASSDomains.render(bundle.tables.map(table => table.digest -> table.description), bundle.targets)
      val tables = resources.resolve("domains.json")
      val report = resources.resolve("domains-report.md")
      GeneratedFiles.write(tables, domains.json)
      GeneratedFiles.write(report, domains.report)
      GeneratedFiles.removeUnexpected(resources, Set(catalogue, tables, report), _ => true)
      Console.out.println(
        s"generated ${SASSInstructionRenderer.cases(shapes).size} typed instructions for ${bundle.targets.size} targets (${bundle.tables.size} descriptions)"
      )
  }

  /** Reads the digest-pinned description of every target. Architecture-suffixed targets (`SM90a`) share their base
    * target's description and are not addressable by name, so they are omitted.
    */
  def read(references: Path): Bundle = {
    val manifest            = ujson.read(Files.readString(references.resolve("manifest.json")))
    val pinned              = manifest("targets").arr.toVector.map(target => target("name").str -> target("table").str)
    val (suffixed, targets) = pinned.partition(_._1.last.isLetter)
    val known               = pinned.toSet
    suffixed.foreach { (name, digest) =>
      require(known(name.init -> digest), s"$name does not share the description of ${name.init}")
    }
    val tables = targets.map(_._2).distinct.sorted.map { digest =>
      val path  = references.resolve(s"tables/$digest.txt.gz")
      val bytes = ExtractSASS.readTable(path)
      require(sha256(bytes) == digest, s"SASS reference digest mismatch: $path")
      def invalid(offset: Int, message: String): Nothing =
        throw IllegalArgumentException(s"$path:$offset: $message")
      val active = SASSPreprocessor.preprocess(String(bytes, UTF_8)).fold(e => invalid(e.offset, e.message), identity)
      val description = SASSDescription.parse(active).fold(e => invalid(e.offset, e.message), identity)
      require(
        description.classes.map(_.name).distinct.size == description.classes.size,
        s"duplicate SASS class: $path"
      )
      Table(digest, description)
    }
    Bundle(targets.sortBy(_._1), tables)
  }

  private def modifiers(tables: Vector[Table]): Vector[String] = tables.flatMap { table =>
    val kinds        = SASSInstructionModel.modifierKinds(table.description.classes)
    val vocabularies = SASSSymbols.compile(table.description)
    val registers    = vocabularies.view.filter(vocabulary =>
      vocabulary.name.contains("Register") || vocabulary.name.contains("Predicate")
    ).flatMap(_.values.map(_.name)).toSet
    vocabularies.view.filter(vocabulary =>
      kinds(vocabulary.name) && !vocabulary.values.forall(value => registers(value.name))
    )
      .flatMap(_.values.map(_.name)) ++ table.description.classes.flatMap(_.opcodes.flatMap(_.split('.').drop(1)))
  }.flatMap(_.split('.')).filter(value => value.nonEmpty && !value.startsWith("INVALID")).distinct.sorted

  private def renderJson(value: ujson.Value): String = value.obj.map { (name, rows) =>
    rows match {
      case array: ujson.Arr =>
        s"  ${ujson.write(name)}: [\n${array.value.map(row => s"    ${ujson.write(row)}").mkString(",\n")}\n  ]"
      case scalar => s"  ${ujson.write(name)}: ${ujson.write(scalar)}"
    }
  }.mkString("{\n", ",\n", "\n}\n")

  case class Assignment(name: Int, positions: Vector[Int], expression: Int) derives ReadWriter
  case class Form(
      name: Int,
      alternate: Boolean,
      opcodes: Vector[(Int, BigInt)],
      fields: Vector[Int],
      syntax: Vector[Int],
      conditions: Vector[Int]
  ) derives ReadWriter
  case class Vocabulary(name: Int, values: Vector[(Int, BigInt)], default: Option[BigInt]) derives ReadWriter
  case class Row(inputs: Vector[BigInt], output: BigInt) derives ReadWriter
  case class Lookup(name: Int, rows: Vector[Row]) derives ReadWriter
  case class Database(
      digest: Int,
      width: Int,
      parameters: Vector[(Int, BigInt)],
      constants: Vector[(Int, BigInt)],
      forms: Vector[Int],
      lookups: Vector[Int],
      vocabularies: Vector[Int]
  ) derives ReadWriter
  case class Catalogue(
      strings: Vector[String],
      expressions: Vector[Expression],
      syntaxNodes: Vector[SyntaxNode],
      conditionNodes: Vector[ConditionNode],
      conditions: Vector[ConditionRow],
      assignments: Vector[Assignment],
      forms: Vector[Form],
      vocabularies: Vector[Vocabulary],
      lookups: Vector[Lookup],
      databases: Vector[Database],
      targets: Vector[(Int, Int)]
  ) derives ReadWriter

  /** Assigns dense ids in first-use order so equal records are stored once across targets. */
  private class Interner[A] {
    private val ids          = mutable.HashMap.empty[A, Int]
    private val values       = mutable.ArrayBuffer.empty[A]
    def apply(value: A): Int = ids.getOrElseUpdate(value, { values += value; values.size - 1 })
    def result: Vector[A]    = values.toVector
  }

  def renderCatalogue(bundle: Bundle): Catalogue = {
    val strings        = Interner[String]()
    val expressions    = Interner[Expression]()
    val syntaxNodes    = Interner[SyntaxNode]()
    val conditionNodes = Interner[ConditionNode]()
    val conditions     = Interner[ConditionRow]()
    val assignments    = Interner[Assignment]()
    val forms          = Interner[Form]()
    val vocabularies   = Interner[Vocabulary]()
    val lookups        = Interner[Lookup]()

    def syntax(node: SASSFormat.Node): Int = syntaxNodes(node match {
      case SASSFormat.Text(raw)                                      => SyntaxNode.Text(strings(raw))
      case SASSFormat.Literal(_, value)                              => SyntaxNode.Literal(strings(value))
      case SASSFormat.Field(kind, binding, arguments, default, _, _) =>
        SyntaxNode.Field(strings(kind), strings(binding), arguments.map(strings(_)), default.map(strings(_)))
      case SASSFormat.Group(open, close, children) =>
        SyntaxNode.Group(strings(open.toString), strings(close.toString), Vec.from(children.map(syntax(_))))
      case SASSFormat.Optional(marker, children) =>
        SyntaxNode.Optional(marker.map(strings(_)), Vec.from(children.map(syntax(_))))
    })

    def condition(value: ConditionExpression): Int = conditionNodes(value match {
      case ConditionExpression.Number(number)         => ConditionNode.Number(number)
      case ConditionExpression.StringValue(text)      => ConditionNode.StringValue(strings(text))
      case ConditionExpression.Field(name, attribute) => ConditionNode.Field(strings(name), attribute.map(strings(_)))
      case ConditionExpression.Parameter(name)        => ConditionNode.Parameter(strings(name))
      case ConditionExpression.Constant(name)         => ConditionNode.Constant(strings(name))
      case ConditionExpression.EnumValue(kind, name)  => ConditionNode.EnumValue(strings(kind), strings(name))
      case ConditionExpression.Unary(operator, inner) =>
        ConditionNode.Unary(strings(operator.spelling), condition(inner))
      case ConditionExpression.Binary(operator, left, right) =>
        ConditionNode.Binary(strings(operator.spelling), condition(left), condition(right))
      case ConditionExpression.Call(name, arguments) =>
        ConditionNode.Call(strings(name.spelling), arguments.map(condition(_)))
      case ConditionExpression.Conditional(test, whenTrue, whenFalse) =>
        ConditionNode.Conditional(condition(test), condition(whenTrue), condition(whenFalse))
    })

    def named(values: Map[String, BigInt]): Vector[(Int, BigInt)] =
      values.toVector.sortBy(_._1).map((name, value) => strings(name) -> value)

    val digests   = bundle.tables.map(_.digest)
    val positions = digests.zipWithIndex.toMap
    val databases = bundle.tables.map { table =>
      val decoder = Decoder(table)
      Database(
        digest = strings(table.digest),
        width = decoder.compiled.width,
        parameters = named(decoder.parameters),
        constants = named(decoder.constants),
        forms = decoder.forms.map { (form, format, legality) =>
          forms(Form(
            strings(form.name),
            form.alternate,
            form.opcodes.map((name, value) => strings(name) -> value),
            form.fields.map(field =>
              assignments(Assignment(
                strings(field.name),
                field.positions,
                expressions(field.expression.fold(
                  source => throw IllegalArgumentException(s"unsupported expression: $source"),
                  identity
                ))
              ))
            ),
            format.map(syntax(_)),
            legality.map { (entry, requires) =>
              conditions(ConditionRow(condition(entry.expression), strings(entry.message), requires.map(strings(_))))
            }
          ))
        },
        lookups = decoder.lookups.map(lookup =>
          lookups(Lookup(strings(lookup.name), lookup.rows.map((inputs, output) => Row(inputs, output))))
        ),
        vocabularies = decoder.vocabularies.map(vocabulary =>
          vocabularies(Vocabulary(
            strings(vocabulary.name),
            vocabulary.values.map(value => strings(value.name) -> value.number),
            vocabulary.default
          ))
        )
      )
    }
    val targets = bundle.targets.map((name, digest) => strings(name) -> positions(digest))
    Catalogue(
      strings.result,
      expressions.result,
      syntaxNodes.result,
      conditionNodes.result,
      conditions.result,
      assignments.result,
      forms.result,
      vocabularies.result,
      lookups.result,
      databases,
      targets
    )
  }

  /** The parts of one description that runtime decoding, legality checks and assembly consume. */
  private class Decoder(table: Table) {
    private def fail(detail: String): Nothing = throw IllegalArgumentException(s"${table.digest}: $detail")

    val compiled: SASSEncoding.Compiled = SASSEncoding.compile(table.description)
    compiled.forms.find(_.issues.nonEmpty).foreach(form => fail(s"${form.name}: ${form.issues.mkString("; ")}"))

    private val classes     = table.description.classes.map(form => form.name -> form).toMap
    private val definitions =
      SASSConditions.definitions(table.description.preamble).fold(e => fail(e.message), identity)

    /** Scheduling constraints describe control fields that assembly preserves rather than checks. */
    private def scheduling(expression: ConditionExpression): Boolean = expression match {
      case ConditionExpression.Call(BuiltinCall.Defined, Vec(ConditionExpression.Call(call, _))) =>
        call.spelling.startsWith("TABLES_opex_")
      case ConditionExpression.Binary(BinaryOperator.Implies, _, right) =>
        val names = fieldNames(right)
        names.nonEmpty && names.subsetOf(Set("usched_info", "batch_t"))
      case _ => false
    }

    private def fieldNames(expression: ConditionExpression): Set[String] = expression match {
      case ConditionExpression.Field(name, _)             => Set(name)
      case ConditionExpression.Unary(_, inner)            => fieldNames(inner)
      case ConditionExpression.Binary(_, left, right)     => fieldNames(left) ++ fieldNames(right)
      case ConditionExpression.Conditional(test, yes, no) => fieldNames(test) ++ fieldNames(yes) ++ fieldNames(no)
      case ConditionExpression.Call(_, arguments)         => arguments.flatMap(fieldNames(_)).toSet
      case _                                              => Set.empty
    }

    /** VarLatOperandEnc maps the scoreboard values 7 and 65535 to the same encoding, so the MIO_RD_SCBD constraint on
      * the pre-encoding value applies only when a caller supplies that value.
      */
    private val unobservableScoreboard = ConditionExpression.Binary(
      BinaryOperator.Equal,
      ConditionExpression.Field("dst_wr_sb", None),
      ConditionExpression.Number(7)
    )

    private def legality(form: SASSDescription.InstructionClass): Vector[(SASSConditions.Condition, Option[String])] =
      form.sections.filter(section => ConditionSections(section.name)).flatMap { section =>
        SASSConditions.parse(section.body).fold(e => fail(s"${form.name}: ${e.message}"), identity)
      }.filter(entry =>
        (entry.category == "ERROR" || entry.category.endsWith("_ERROR")) && !scheduling(entry.expression)
      )
        .map { entry =>
          val scoreboard = entry.category == "ILLEGAL_INSTR_ENCODING_ERROR" &&
            entry.message.startsWith("Illegal instruction encoding: Instruction type INST_TYPE_MIO_RD_SCBD") &&
            entry.expression == unobservableScoreboard
          entry -> Option.when(scoreboard)("dst_wr_sb")
        }

    val forms
        : Vector[(SASSEncoding.Form, Vector[SASSFormat.Node], Vector[(SASSConditions.Condition, Option[String])])] =
      compiled.forms.map { form =>
        val format = classes(form.name).section("FORMAT").toVector.flatMap(section =>
          SASSFormat.parse(section.body).fold(e => fail(s"${form.name}: ${e.message}"), identity)
        )
        (form, format, legality(classes(form.name)))
      }

    private def expressionLookups(value: Expression): Set[String] = value match {
      case Expression.Lookup(name, arguments)     => Set(name) ++ arguments.flatMap(expressionLookups(_))
      case Expression.Scale(inner, _)             => expressionLookups(inner)
      case Expression.Multiply(inner, _)          => expressionLookups(inner)
      case Expression.Identity(a, b)              => expressionLookups(a) ++ expressionLookups(b)
      case Expression.FixedFloat(inner, _)        => expressionLookups(inner)
      case Expression.FloatConvert(value = inner) => expressionLookups(inner)
      case Expression.Relaxed(inner)              => expressionLookups(inner)
      case _                                      => Set.empty
    }

    private def conditionLookups(value: ConditionExpression): Set[String] = value match {
      case ConditionExpression.Call(BuiltinCall.Table(name), arguments) =>
        Set(name) ++ arguments.flatMap(conditionLookups(_))
      case ConditionExpression.Call(_, arguments)         => arguments.flatMap(conditionLookups(_)).toSet
      case ConditionExpression.Unary(_, inner)            => conditionLookups(inner)
      case ConditionExpression.Binary(_, a, b)            => conditionLookups(a) ++ conditionLookups(b)
      case ConditionExpression.Conditional(test, yes, no) =>
        conditionLookups(test) ++ conditionLookups(yes) ++ conditionLookups(no)
      case _ => Set.empty
    }

    private def conditionNames(value: ConditionExpression): Set[String] = value match {
      case ConditionExpression.Parameter(name)            => Set(name)
      case ConditionExpression.Constant(name)             => Set(name)
      case ConditionExpression.Field(name, attribute)     => Set(name) ++ attribute.map(suffix => s"$name@$suffix")
      case ConditionExpression.EnumValue(kind, _)         => Set(kind)
      case ConditionExpression.Unary(_, inner)            => conditionNames(inner)
      case ConditionExpression.Binary(_, a, b)            => conditionNames(a) ++ conditionNames(b)
      case ConditionExpression.Conditional(test, yes, no) =>
        conditionNames(test) ++ conditionNames(yes) ++ conditionNames(no)
      case ConditionExpression.Call(_, arguments) => arguments.flatMap(conditionNames(_)).toSet
      case _                                      => Set.empty
    }

    private def syntaxKinds(value: SASSFormat.Node): Set[String] = value match {
      case SASSFormat.Field(kind = kind)    => Set(kind)
      case SASSFormat.Group(_, _, children) => children.flatMap(syntaxKinds(_)).toSet
      case SASSFormat.Optional(_, children) => children.flatMap(syntaxKinds(_)).toSet
      case _                                => Set.empty
    }

    private val expressions = forms.flatMap(_._3).map(_._1.expression)
    private val names       = expressions.flatMap(conditionNames(_)).toSet
    private val required    =
      compiled.forms.flatMap(_.fields.flatMap(field =>
        field.expression.fold(_ => Set.empty[String], expressionLookups(_))
      )).toSet ++
        expressions.flatMap(conditionLookups(_))

    val lookups: Vector[SASSEncoding.LookupTable] = compiled.tables.filter(lookup => required(lookup.name))
    (required -- lookups.map(_.name)).headOption.foreach(name => fail(s"undefined lookup table $name"))
    lookups.find(_.issues.nonEmpty).foreach(lookup => fail(s"${lookup.name}: ${lookup.issues.mkString("; ")}"))

    val vocabularies: Vector[SASSSymbols.Vocabulary] = {
      val used = forms.flatMap(_._2.flatMap(syntaxKinds(_))).toSet ++ names
      SASSSymbols.compile(table.description).filter(vocabulary => used(vocabulary.name))
    }
    val parameters: Map[String, BigInt] = definitions.parameters.filter((name, _) => names(name))
    val constants: Map[String, BigInt]  = definitions.constants.filter((name, _) => names(name))
  }

  private def sha256(bytes: Array[Byte]): String =
    MessageDigest.getInstance("SHA-256").digest(bytes).map(b => f"${b & 255}%02x").mkString
}
