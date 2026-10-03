package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.zip.GZIPInputStream
import net.kurobako.gemstone.constraint.*
import scala.collection.mutable
import scala.util.Try
import scala.util.Using

/** Generates instruction cases from the recovered opcode vocabulary and separate target encoding tables. Execution
  * semantics remain handwritten; recovered encoding forms do not imply execution support.
  */
object GenerateSASSISA {
  private val PackageRoot = "net/kurobako/gemstone/isa/sass"

  case class Table(
      digest: String,
      description: SASSDescription.Description,
      resolved: Option[SASSDescription.Description] = None
  ) {
    def encodingDescription: SASSDescription.Description = resolved.getOrElse(description)
  }
  case class Schema(name: String, fields: Vector[String])
  case class Bundle(manifest: ujson.Value, tables: Vector[Table])

  private val command = Command("GenerateSASSISA", "Generate SASS forms from pinned recovered descriptions")((
    Opts.argument[String]("references").map(Path.of(_)),
    Opts.argument[String]("generated-source-root").map(Path.of(_)),
    Opts.argument[String]("generated-resource-root").map(Path.of(_))
  ).tupled)

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) {
    case (references, sourceRoot, resourceRoot) =>
      val bundle    = read(references)
      val schemas   = instructionSchemas(bundle.tables)
      val source    = sourceRoot.resolve(PackageRoot).resolve("Instruction.scala")
      val forms     = bundle.tables.flatMap(_.encodingDescription.classes).filter(unmodelledReason(_).isEmpty)
      val shapes    = SASSInstructionModel.shapes(forms)
      val modifiers = bundle.tables.flatMap { table =>
        val kinds =
          SASSInstructionModel.modifierKinds(table.encodingDescription.classes.filter(unmodelledReason(_).isEmpty))
        val vocabularies = SASSSymbols.compile(table.encodingDescription)
        val registers    = vocabularies.filter(vocabulary =>
          vocabulary.name.contains("Register") || vocabulary.name.contains("Predicate")
        ).flatMap(_.values.map(_.name)).toSet
        vocabularies
          .filter(vocabulary => kinds(vocabulary.name) && !vocabulary.values.forall(value => registers(value.name)))
          .flatMap(_.values.map(_.name)) ++
          table.encodingDescription.classes.flatMap(_.opcodes.flatMap(_.split('.').drop(1)))
      }.flatMap(_.split('.')).filter(value => value.nonEmpty && !value.startsWith("INVALID")).distinct.sorted
      val sources = SASSInstructionRenderer.render(shapes, modifiers).map { (name, content) =>
        source.getParent.resolve(name) -> content
      }
      sources.toVector.sortBy(_._1.toString).foreach { case (path, content) =>
        val _ = GeneratedFiles.write(path, GeneratedScalaFormatter(path, content))
      }
      GeneratedFiles.removeUnexpected(source.getParent, sources.keySet, _.getFileName.toString.endsWith(".scala"))

      val resources   = resourceRoot.resolve(PackageRoot)
      val catalogue   = resources.resolve("metadata.json")
      val _           = GeneratedFiles.write(catalogue, renderJson(renderCatalogue(bundle, schemas)))
      val schemaIds   = schemas.zipWithIndex.toMap
      val decoderData = bundle.tables.map(table =>
        table.digest -> renderDecoder(table, schemaIds, table.digest)
      )
      val decoderFile = resources.resolve("decoders.json")
      val _           = GeneratedFiles.write(decoderFile, renderJson(renderDecoderCatalogue(decoderData)))
      val outputs     = Set(catalogue, decoderFile)
      GeneratedFiles.removeUnexpected(resources, outputs, _ => true)
      Console.out.println(
        s"generated ${SASSInstructionRenderer.cases(shapes).size} typed instructions, ${schemas.size} shared SASS forms for ${bundle.manifest("targets").arr.size} targets (${bundle.tables.size} descriptions)"
      )
  }

  def read(references: Path): Bundle = {
    val root     = references.toAbsolutePath.normalize
    val manifest = ujson.read(Files.readString(root.resolve("manifest.json")))
    require(manifest("format").num == 1, "unsupported SASS reference manifest")
    val targets = manifest("targets").arr.toVector
    require(
      targets.nonEmpty && targets.map(_("name").str).distinct.size == targets.size,
      "SASS target names must be nonempty and unique"
    )
    val rows = manifest("tables").arr.toVector
    require(
      rows.nonEmpty && rows.map(_("sha256").str).distinct.size == rows.size,
      "SASS description digests must be nonempty and unique"
    )
    val tables = rows.sortBy(_("sha256").str).map { row =>
      val digest = row("sha256").str
      require(digest.length == 64 && digest.forall(c => c.isDigit || ('a' to 'f').contains(c)), "invalid SASS digest")
      val path = root.resolve(row("path").str).normalize
      require(path.startsWith(root), s"SASS reference escapes bundle: $path")
      require(path.toRealPath().startsWith(root.toRealPath()), s"SASS reference symlink escapes bundle: $path")
      val bytes = Using.resource(GZIPInputStream(Files.newInputStream(path)))(_.readAllBytes())
      require(sha256(bytes) == digest, s"SASS reference digest mismatch: $path")
      val parsed = SASSDescription.parse(String(bytes, UTF_8)).fold(
        error => throw IllegalArgumentException(s"$path:${error.offset}: ${error.message}"),
        identity
      )
      require(parsed.architecture.nonEmpty, s"SASS architecture header missing: $path")
      require(parsed.classes.count(!_.alternate) == row("classes").num.toInt, s"SASS class count mismatch: $path")
      require(
        parsed.classes.groupBy(_.name).values.forall(forms =>
          forms.size == 1 ||
            forms.forall(unmodelledReason(_).nonEmpty)
        ),
        s"duplicate unconditional SASS class: $path"
      )
      parsed.classes.foreach { form =>
        require(
          unmodelledReason(form).nonEmpty ||
            (form.section("FORMAT").nonEmpty && form.opcodes.nonEmpty && form.section("ENCODING").nonEmpty),
          s"incomplete SASS class ${form.name}: $path"
        )
      }
      val resolved = Option.when(String(bytes, UTF_8).contains("##IF")) {
        val active = SASSPreprocessor.preprocess(String(bytes, UTF_8), architectureSemantics = false)
          .fold(error => throw IllegalArgumentException(s"$path:${error.offset}: ${error.message}"), identity)
        val description = SASSDescription.parse(active)
          .fold(error => throw IllegalArgumentException(s"$path:${error.offset}: ${error.message}"), identity)
        require(
          description.classes.map(_.name).distinct.size == description.classes.size,
          s"duplicate active SASS class: $path"
        )
        description
      }
      Table(digest, parsed, resolved)
    }
    require(targets.forall(target => tables.exists(_.digest == target("table").str)), "unknown target description")
    Bundle(manifest, tables)
  }

  private def unmodelledReason(form: SASSDescription.InstructionClass): Option[String] =
    Option.when(
      form.conditional || form.raw.linesIterator.exists(_.trim.startsWith("##"))
    )("unresolved description preprocessing directive")

  private def schema(form: SASSDescription.InstructionClass): Option[Schema] =
    Option.when(unmodelledReason(form).isEmpty)(Schema(form.name, form.fields.map(_.name).distinct))

  def instructionSchemas(tables: Vector[Table]): Vector[Schema] = {
    val parsed = tables.flatMap(table =>
      table.encodingDescription.classes.map { form =>
        Try(schema(form)).toEither.left.map(error => s"${table.digest}/${form.name}: ${error.getMessage}")
      }
    )
    val errors = parsed.collect { case Left(message) => message }
    require(errors.isEmpty, errors.mkString("invalid SASS binding layouts:\n", "\n", ""))
    parsed.flatMap(_.toOption.flatten).distinct.sortBy(s => (s.name, s.fields.mkString("\u0000")))
  }

  private def renderJson(value: ujson.Value): String = {
    val sections = value.obj.toVector.map { case (name, rows) =>
      rows match {
        case array: ujson.Arr =>
          val records = array.value.map(row => s"    ${ujson.write(row)}").mkString(",\n")
          s"  ${ujson.write(name)}: [\n$records\n  ]"
        case scalar => s"  ${ujson.write(name)}: ${ujson.write(scalar)}"
      }
    }
    sections.mkString("{\n", ",\n", "\n}\n")
  }

  def renderCatalogue(bundle: Bundle, schemas: Vector[Schema]): ujson.Value = {
    val targets = bundle.manifest("targets").arr.toVector.sortBy(_("name").str)
    val strings =
      targets.flatMap(target => Vector(target("name").str, target("tool").str, target("table").str)).distinct.sorted
    val ids = strings.zipWithIndex.toMap
    ujson.Obj(
      "format"  -> 3,
      "strings" -> ujson.Arr.from(strings),
      "targets" -> ujson.Arr.from(targets.map(target =>
        ujson.Obj(
          "name"        -> ids(target("name").str),
          "tool"        -> ids(target("tool").str),
          "encodingKey" -> ids(target("table").str)
        )
      )),
      "schemaCount" -> schemas.size
    )
  }

  /** Domain-specific tables share complete expressions, syntax trees, assignments, and records. */
  def renderDecoderCatalogue(decoders: Vector[(String, ujson.Value)]): ujson.Value = {
    val stringValues                                = mutable.HashSet.empty[String]
    def addOptionalString(value: ujson.Value): Unit = value match {
      case ujson.Str(text) => stringValues += text
      case _               => ()
    }
    def addSyntax(value: ujson.Value): Unit = value("$type").str match {
      case "Text"    => stringValues += value("raw").str
      case "Literal" => stringValues += value("value").str
      case "Field"   =>
        stringValues += value("kind").str
        stringValues += value("binding").str
        addOptionalString(value("arguments"))
        addOptionalString(value("default"))
      case "Group" =>
        stringValues += value("open").str; stringValues += value("close").str
        value("children").arr.foreach(addSyntax(_))
      case "Optional" =>
        addOptionalString(value("marker"))
        value("children").arr.foreach(addSyntax(_))
      case _ => ()
    }
    def addCondition(value: ujson.Value): Unit = {
      stringValues += value("category").str
      stringValues += value("message").str
      def expression(node: ujson.Value): Unit = node("$type").str match {
        case "StringValue"            => stringValues += node("value").str
        case "Parameter" | "Constant" => stringValues += node("name").str
        case "Field"                  =>
          stringValues += node("name").str
          addOptionalString(node("attribute"))
        case "EnumValue" => stringValues += node("kind").str; stringValues += node("name").str
        case "Unary"     => stringValues += node("operator").str; expression(node("value"))
        case "Binary"    =>
          stringValues += node("operator").str; expression(node("left")); expression(node("right"))
        case "Conditional" =>
          expression(node("condition")); expression(node("whenTrue")); expression(node("whenFalse"))
        case "Call" => stringValues += node("name").str; node("arguments").arr.foreach(expression(_))
        case _      => ()
      }
      expression(value("expression"))
    }
    decoders.sortBy(_._1).foreach { case (digest, database) =>
      stringValues += digest; stringValues += database("target").str
      database("parameters").obj.keys.foreach(stringValues += _)
      database("constants").obj.keys.foreach(stringValues += _)
      database("forms").arr.foreach { form =>
        stringValues += form("name").str
        form("issues").arr.foreach(issue => stringValues += issue.str)
        form("opcodes").arr.foreach(opcode => stringValues += opcode("name").str)
        form("fields").arr.foreach { field =>
          stringValues += field("name").str
        }
        form("syntax").arr.foreach(addSyntax(_))
        form("conditions").arr.foreach(addCondition(_))
      }
      database("symbols").arr.foreach { vocabulary =>
        stringValues += vocabulary("name").str
        vocabulary("values").arr.foreach(value => stringValues += value("name").str)
      }
      database("tables").arr.foreach { lookup =>
        stringValues += lookup("name").str
        lookup("issues").arr.foreach(issue => stringValues += issue.str)
      }
    }
    val strings                      = stringValues.toVector.sorted
    val stringIds                    = strings.zipWithIndex.toMap
    def stringId(value: String): Int = stringIds(value)
    def intern(rows: mutable.ArrayBuffer[ujson.Value], ids: mutable.HashMap[String, Int], value: ujson.Value): Int = {
      val key = ujson.write(value)
      ids.getOrElseUpdate(
        key, {
          rows += value
          rows.size - 1
        }
      )
    }
    val expressions            = mutable.ArrayBuffer.empty[ujson.Value]
    val expressionIds          = mutable.HashMap.empty[String, Int]
    val syntaxNodes            = mutable.ArrayBuffer.empty[ujson.Value]
    val syntaxNodeIds          = mutable.HashMap.empty[String, Int]
    val formats                = mutable.ArrayBuffer.empty[ujson.Value]
    val formatIds              = mutable.HashMap.empty[String, Int]
    val conditionExpressions   = mutable.ArrayBuffer.empty[ujson.Value]
    val conditionExpressionIds = mutable.HashMap.empty[String, Int]
    val conditions             = mutable.ArrayBuffer.empty[ujson.Value]
    val conditionIds           = mutable.HashMap.empty[String, Int]
    val assignments            = mutable.ArrayBuffer.empty[ujson.Value]
    val assignmentIds          = mutable.HashMap.empty[String, Int]
    val forms                  = mutable.ArrayBuffer.empty[ujson.Value]
    val encodingIds            = mutable.HashMap.empty[String, Int]
    val vocabularies           = mutable.ArrayBuffer.empty[ujson.Value]
    val vocabularyIds          = mutable.HashMap.empty[String, Int]
    val lookups                = mutable.ArrayBuffer.empty[ujson.Value]
    val lookupIds              = mutable.HashMap.empty[String, Int]

    def optionalStringId(value: ujson.Value): ujson.Value =
      value.strOpt.fold[ujson.Value](ujson.Null)(id => ujson.Num(stringId(id)))

    def syntaxNode(value: ujson.Value): Int = {
      val children = value.obj.get("children").fold(Vector.empty[Int])(_.arr.toVector.map(syntaxNode(_)))
      val encoded  = value("$type").str match {
        case "Text"    => ujson.Obj("$type" -> "Text", "raw" -> stringId(value("raw").str))
        case "Literal" => ujson.Obj("$type" -> "Literal", "value" -> stringId(value("value").str))
        case "Field"   => ujson.Obj(
            "$type"     -> "Field",
            "kind"      -> stringId(value("kind").str),
            "binding"   -> stringId(value("binding").str),
            "arguments" -> optionalStringId(value("arguments")),
            "default"   -> optionalStringId(value("default"))
          )
        case "Group" => ujson.Obj(
            "$type"    -> "Group",
            "open"     -> stringId(value("open").str),
            "close"    -> stringId(value("close").str),
            "children" -> ujson.Arr.from(children)
          )
        case "Optional" => ujson.Obj(
            "$type"    -> "Optional",
            "marker"   -> optionalStringId(value("marker")),
            "children" -> ujson.Arr.from(children)
          )
      }
      intern(syntaxNodes, syntaxNodeIds, encoded)
    }

    def conditionExpression(value: ujson.Value): Int = {
      val children = value.obj.get("arguments").fold(Vector.empty[Int])(_.arr.toVector.map(conditionExpression(_)))
      val encoded  = value("$type").str match {
        case "Number"      => ujson.Obj("$type" -> "Number", "value" -> value("value"))
        case "StringValue" => ujson.Obj("$type" -> "StringValue", "value" -> stringId(value("value").str))
        case "Field"       => ujson.Obj(
            "$type"     -> "Field",
            "name"      -> stringId(value("name").str),
            "attribute" -> optionalStringId(value("attribute"))
          )
        case "Parameter" | "Constant" => ujson.Obj(
            "$type" -> value("$type"),
            "name"  -> stringId(value("name").str)
          )
        case "EnumValue" => ujson.Obj(
            "$type" -> "EnumValue",
            "kind"  -> stringId(value("kind").str),
            "name"  -> stringId(value("name").str)
          )
        case "Unary" => ujson.Obj(
            "$type"    -> "Unary",
            "operator" -> stringId(value("operator").str),
            "value"    -> conditionExpression(value("value"))
          )
        case "Binary" => ujson.Obj(
            "$type"    -> "Binary",
            "operator" -> stringId(value("operator").str),
            "left"     -> conditionExpression(value("left")),
            "right"    -> conditionExpression(value("right"))
          )
        case "Conditional" => ujson.Obj(
            "$type"     -> "Conditional",
            "condition" -> conditionExpression(value("condition")),
            "whenTrue"  -> conditionExpression(value("whenTrue")),
            "whenFalse" -> conditionExpression(value("whenFalse"))
          )
        case "Call" => ujson.Obj(
            "$type"     -> "Call",
            "name"      -> stringId(value("name").str),
            "arguments" -> ujson.Arr.from(children)
          )
      }
      intern(conditionExpressions, conditionExpressionIds, encoded)
    }

    def condition(value: ujson.Value): Int = intern(
      conditions,
      conditionIds,
      ujson.Obj(
        "category"   -> stringId(value("category").str),
        "expression" -> conditionExpression(value("expression")),
        "message"    -> stringId(value("message").str)
      )
    )

    val databases = decoders.sortBy(_._1).map { case (digest, database) =>
      val localForms = database("forms").arr.toVector.map { form =>
        val fields = form("fields").arr.toVector.map { field =>
          val expression = intern(expressions, expressionIds, field("expression"))
          intern(
            assignments,
            assignmentIds,
            ujson.Obj(
              "name"       -> stringId(field("name").str),
              "positions"  -> field("positions"),
              "expression" -> expression
            )
          )
        }
        val syntax      = form("syntax").arr.toVector.map(syntaxNode(_))
        val constraints = form("conditions").arr.toVector
        intern(
          forms,
          encodingIds,
          ujson.Obj(
            "formId"    -> form("formId"),
            "name"      -> stringId(form("name").str),
            "alternate" -> form("alternate"),
            "opcodes"   -> ujson.Arr.from(form("opcodes").arr.map(value =>
              ujson.Arr(stringId(value("name").str), value("value"))
            )),
            "fields" -> ujson.Arr.from(fields),
            "issues" -> ujson.Arr.from(form("issues").arr.map(issue => stringId(issue.str))),
            "syntax" -> Option.when(syntax.nonEmpty)(ujson.Num(
              intern(formats, formatIds, ujson.Arr.from(syntax))
            )).getOrElse(ujson.Null),
            "conditions" -> ujson.Arr.from(constraints.map(condition(_)))
          )
        )
      }
      val localSymbols = database("symbols").arr.toVector.map { symbol =>
        intern(
          vocabularies,
          vocabularyIds,
          ujson.Obj(
            "name"    -> stringId(symbol("name").str),
            "default" -> symbol.obj.getOrElse("default", ujson.Arr()),
            "values"  -> ujson.Arr.from(symbol("values").arr.map(value =>
              ujson.Arr(stringId(value("name").str), value("value"))
            ))
          )
        )
      }
      val localTables = database("tables").arr.toVector.map { table =>
        intern(
          lookups,
          lookupIds,
          ujson.Obj(
            "name"   -> stringId(table("name").str),
            "issues" -> ujson.Arr.from(table("issues").arr.map(issue => stringId(issue.str))),
            "rows"   -> table("rows")
          )
        )
      }
      def namedRows(value: ujson.Value): ujson.Arr = ujson.Arr.from(value.obj.toVector.map { case (name, number) =>
        ujson.Arr(stringId(name), number)
      }.sortBy(_.arr.head.num))
      ujson.Obj(
        "target"     -> stringId(database("target").str),
        "digest"     -> stringId(digest),
        "width"      -> database("width"),
        "parameters" -> namedRows(database("parameters")),
        "constants"  -> namedRows(database("constants")),
        "forms"      -> ujson.Arr.from(localForms),
        "tables"     -> ujson.Arr.from(localTables),
        "symbols"    -> ujson.Arr.from(localSymbols)
      )
    }
    ujson.Obj(
      "format"               -> 4,
      "strings"              -> ujson.Arr.from(strings),
      "expressions"          -> ujson.Arr.from(expressions),
      "syntaxNodes"          -> ujson.Arr.from(syntaxNodes),
      "formats"              -> ujson.Arr.from(formats),
      "conditionExpressions" -> ujson.Arr.from(conditionExpressions),
      "conditions"           -> ujson.Arr.from(conditions),
      "assignments"          -> ujson.Arr.from(assignments),
      "forms"                -> ujson.Arr.from(forms),
      "vocabularies"         -> ujson.Arr.from(vocabularies),
      "lookups"              -> ujson.Arr.from(lookups),
      "databases"            -> ujson.Arr.from(databases)
    )
  }

  private def renderDecoder(table: Table, ids: Map[Schema, Int], target: String): ujson.Value = {
    val compiled = SASSEncoding.compile(table.encodingDescription)
    val forms    = table.encodingDescription.classes.map(form => form.name -> schema(form).map(ids)).toMap
    def expression(value: SASSEncoding.Expr): ujson.Value = value match {
      case SASSEncoding.Expr.Constant(number, relaxed) =>
        ujson.Obj("$type" -> "Constant", "value" -> upickle.default.writeJs(number), "relaxed" -> relaxed)
      case SASSEncoding.Expr.Reference(name, attribute, relaxed) =>
        ujson.Obj(
          "$type"     -> "Reference",
          "name"      -> name,
          "attribute" -> upickle.default.writeJs(attribute),
          "relaxed"   -> relaxed
        )
      case SASSEncoding.Expr.Scale(value, factor) =>
        ujson.Obj("$type" -> "Scale", "value" -> expression(value), "factor" -> upickle.default.writeJs(factor))
      case SASSEncoding.Expr.Multiply(value, factor) =>
        ujson.Obj("$type" -> "Multiply", "value" -> expression(value), "factor" -> upickle.default.writeJs(factor))
      case SASSEncoding.Expr.Identity(first, second) =>
        ujson.Obj("$type" -> "Identity", "first" -> expression(first), "second" -> expression(second))
      case SASSEncoding.Expr.FixedFloat(value, format) =>
        ujson.Obj("$type" -> "FixedFloat", "value" -> expression(value), "format" -> format)
      case SASSEncoding.Expr.Relaxed(value) =>
        ujson.Obj("$type" -> "Relaxed", "value" -> expression(value))
      case SASSEncoding.Expr.FloatConvert(value, selector, cases, fallback) =>
        ujson.Obj(
          "$type"    -> "FloatConvert",
          "value"    -> expression(value),
          "selector" -> selector,
          "cases"    -> upickle.default.writeJs(cases),
          "fallback" -> fallback
        )
      case SASSEncoding.Expr.Lookup(name, arguments) =>
        ujson.Obj("$type" -> "Lookup", "name" -> name, "arguments" -> ujson.Arr.from(arguments.map(expression(_))))
      case SASSEncoding.Expr.Unsupported(source) => ujson.Obj("$type" -> "Unsupported", "source" -> source)
    }
    def syntax(node: SASSFormat.Node): ujson.Value = node match {
      case SASSFormat.Text(raw)         => ujson.Obj("$type" -> "Text", "raw" -> raw)
      case SASSFormat.Literal(_, value) => ujson.Obj("$type" -> "Literal", "value" -> value)
      case SASSFormat.Field(kind, binding, arguments, default, _, _) =>
        ujson.Obj(
          "$type"     -> "Field",
          "kind"      -> kind,
          "binding"   -> binding,
          "arguments" -> upickle.default.writeJs(arguments),
          "default"   -> upickle.default.writeJs(default)
        )
      case SASSFormat.Group(open, close, children) =>
        ujson.Obj(
          "$type"    -> "Group",
          "open"     -> open.toString,
          "close"    -> close.toString,
          "children" -> ujson.Arr.from(children.map(syntax(_)))
        )
      case SASSFormat.Optional(marker, children) =>
        ujson.Obj(
          "$type"    -> "Optional",
          "marker"   -> upickle.default.writeJs(marker),
          "children" -> ujson.Arr.from(children.map(syntax(_)))
        )
    }
    def condition(value: SASSConditions.Expr): ujson.Value = value match {
      case SASSConditions.Expr.Number(value) =>
        ujson.Obj("$type" -> "Number", "value" -> upickle.default.writeJs(value))
      case SASSConditions.Expr.StringValue(value)     => ujson.Obj("$type" -> "StringValue", "value" -> value)
      case SASSConditions.Expr.Field(name, attribute) =>
        ujson.Obj("$type" -> "Field", "name" -> name, "attribute" -> upickle.default.writeJs(attribute))
      case SASSConditions.Expr.Parameter(name)       => ujson.Obj("$type" -> "Parameter", "name" -> name)
      case SASSConditions.Expr.Constant(name)        => ujson.Obj("$type" -> "Constant", "name" -> name)
      case SASSConditions.Expr.EnumValue(kind, name) =>
        ujson.Obj("$type" -> "EnumValue", "kind" -> kind, "name" -> name)
      case SASSConditions.Expr.Unary(operator, value) =>
        ujson.Obj("$type" -> "Unary", "operator" -> operator, "value" -> condition(value))
      case SASSConditions.Expr.Binary(operator, left, right) =>
        ujson.Obj("$type" -> "Binary", "operator" -> operator, "left" -> condition(left), "right" -> condition(right))
      case SASSConditions.Expr.Conditional(test, whenTrue, whenFalse) =>
        ujson.Obj(
          "$type"     -> "Conditional",
          "condition" -> condition(test),
          "whenTrue"  -> condition(whenTrue),
          "whenFalse" -> condition(whenFalse)
        )
      case SASSConditions.Expr.Call(name, arguments) =>
        ujson.Obj("$type" -> "Call", "name" -> name, "arguments" -> ujson.Arr.from(arguments.map(condition(_))))
    }
    val constraints = table.encodingDescription.classes.map { form =>
      val blocks = form.sections.filter(section => Set("CONDITION", "CONDITIONS")(section.name))
      form.name -> Option.when(blocks.nonEmpty)(SASSConditions.parse(blocks.map(_.body).mkString("\n")))
    }.toMap
    val definitions = SASSConditions.definitions(table.encodingDescription.preamble).fold(
      error => throw IllegalArgumentException(error.toString),
      identity
    )
    val formats = table.encodingDescription.classes.map { form =>
      form.name -> form.section("FORMAT").map(section => SASSFormat.parse(section.body))
    }.toMap
    def errorConditions(name: String): Vector[SASSConditions.Condition] = constraints(name)
      .toVector.flatMap(_.toOption.toVector.flatten).filter(condition =>
        condition.category == "ERROR" || condition.category.endsWith("_ERROR")
      )
    def expressionTables(value: SASSEncoding.Expr): Set[String] = value match {
      case SASSEncoding.Expr.Lookup(name, arguments)      => Set(name) ++ arguments.flatMap(expressionTables(_))
      case SASSEncoding.Expr.Scale(inner, _)              => expressionTables(inner)
      case SASSEncoding.Expr.Multiply(inner, _)           => expressionTables(inner)
      case SASSEncoding.Expr.Identity(a, b)               => expressionTables(a) ++ expressionTables(b)
      case SASSEncoding.Expr.FixedFloat(inner, _)         => expressionTables(inner)
      case SASSEncoding.Expr.FloatConvert(inner, _, _, _) => expressionTables(inner)
      case SASSEncoding.Expr.Relaxed(inner)               => expressionTables(inner)
      case _                                              => Set.empty
    }
    def conditionTables(value: SASSConditions.Expr): Set[String] = value match {
      case SASSConditions.Expr.Call(name, arguments) =>
        Option.when(!Set("DEFINED", "IsEven", "IsOdd")(name))(name).toSet ++ arguments.flatMap(conditionTables(_))
      case SASSConditions.Expr.Unary(_, inner)            => conditionTables(inner)
      case SASSConditions.Expr.Binary(_, a, b)            => conditionTables(a) ++ conditionTables(b)
      case SASSConditions.Expr.Conditional(test, yes, no) =>
        conditionTables(test) ++ conditionTables(yes) ++ conditionTables(no)
      case _ => Set.empty
    }
    def conditionNames(value: SASSConditions.Expr): Set[String] = value match {
      case SASSConditions.Expr.Parameter(name)            => Set(name)
      case SASSConditions.Expr.Constant(name)             => Set(name)
      case SASSConditions.Expr.Field(name, attribute)     => Set(name) ++ attribute.map(suffix => s"$name@$suffix")
      case SASSConditions.Expr.EnumValue(kind, _)         => Set(kind)
      case SASSConditions.Expr.Unary(_, inner)            => conditionNames(inner)
      case SASSConditions.Expr.Binary(_, a, b)            => conditionNames(a) ++ conditionNames(b)
      case SASSConditions.Expr.Conditional(test, yes, no) =>
        conditionNames(test) ++ conditionNames(yes) ++ conditionNames(no)
      case SASSConditions.Expr.Call(_, arguments) => arguments.flatMap(conditionNames(_)).toSet
      case _                                      => Set.empty
    }
    def syntaxKinds(value: SASSFormat.Node): Set[String] = value match {
      case SASSFormat.Field(kind, _, _, _, _, _) => Set(kind)
      case SASSFormat.Group(_, _, children)      => children.flatMap(syntaxKinds(_)).toSet
      case SASSFormat.Optional(_, children)      => children.flatMap(syntaxKinds(_)).toSet
      case _                                     => Set.empty
    }
    val legality    = compiled.forms.flatMap(form => errorConditions(form.name))
    val allSymbols  = SASSSymbols.compile(table.encodingDescription)
    val lookupNames = compiled.forms.flatMap(_.fields.flatMap(field => expressionTables(field.expression))).toSet ++
      legality.flatMap(condition => conditionTables(condition.expression))
    val vocabularyNames =
      formats.values.flatMap(_.toVector.flatMap(_.toOption.toVector.flatten.flatMap(syntaxKinds(_)))).toSet ++
        legality.flatMap(condition =>
          conditionNames(condition.expression).filter(name => allSymbols.exists(_.name == name))
        )
    val conditionNamesUsed = legality.flatMap(condition => conditionNames(condition.expression)).toSet
    val symbols            = allSymbols.filter(vocabulary => vocabularyNames(vocabulary.name))
    val lookups            = compiled.tables.filter(lookup => lookupNames(lookup.name))
    val parameters         = definitions.parameters.filter { case (name, _) => conditionNamesUsed(name) }
    val constants          = definitions.constants.filter { case (name, _) => conditionNamesUsed(name) }
    ujson.Obj(
      "target"     -> target,
      "sha256"     -> table.digest,
      "width"      -> compiled.width.raw,
      "parameters" -> upickle.default.writeJs(parameters),
      "constants"  -> upickle.default.writeJs(constants),
      "symbols"    -> ujson.Arr.from(symbols.map(vocabulary =>
        ujson.Obj(
          "name"    -> vocabulary.name,
          "default" -> upickle.default.writeJs(vocabulary.default),
          "values"  -> ujson.Arr.from(vocabulary.values.map(value =>
            ujson.Obj("name" -> value.name, "value" -> upickle.default.writeJs(value.value))
          ))
        )
      )),
      "forms" -> ujson.Arr.from(compiled.forms.map { form =>
        ujson.Obj(
          "formId"    -> upickle.default.writeJs(forms(form.name)),
          "name"      -> form.name,
          "alternate" -> form.alternate,
          "opcodes"   -> ujson.Arr.from(form.opcodes.map { case (name, value) =>
            ujson.Obj("name" -> name, "value" -> upickle.default.writeJs(value))
          }),
          "fields" -> ujson.Arr.from(form.fields.map(field =>
            ujson.Obj(
              "name"       -> field.name,
              "positions"  -> ujson.Arr.from(field.positions),
              "expression" -> expression(field.expression)
            )
          )),
          "conditions" ->
            ujson.Arr.from(errorConditions(form.name).map { entry =>
              ujson.Obj(
                "category"   -> entry.category,
                "expression" -> condition(entry.expression),
                "message"    -> entry.message
              )
            }),
          "syntax" -> ujson.Arr.from(formats(form.name).toVector.flatMap(_.toOption.toVector.flatten).map(syntax(_))),
          "issues" ->
            ujson.Arr.from(form.issues ++ formats(form.name).toVector.flatMap(_.left.toOption.map(_.toString)) ++
              constraints(form.name).toVector.flatMap(_.left.toOption.map(_.toString)))
        )
      }),
      "tables" -> ujson.Arr.from(lookups.map(table =>
        ujson.Obj(
          "name"   -> table.name,
          "issues" -> ujson.Arr.from(table.issues),
          "rows"   -> ujson.Arr.from(table.rows.map { case (inputs, output) =>
            ujson.Obj("inputs" -> upickle.default.writeJs(inputs), "output" -> upickle.default.writeJs(output))
          })
        )
      ))
    )
  }

  private def sha256(bytes: Array[Byte]): String =
    MessageDigest.getInstance("SHA-256").digest(bytes).map(b => f"${b & 255}%02x").mkString

}
