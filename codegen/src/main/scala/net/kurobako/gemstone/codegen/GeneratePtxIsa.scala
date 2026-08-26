package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

/** Extracts a compact instruction catalogue and executable parser corpus from the pinned NVIDIA PTX HTML document. */
object GeneratePtxIsa {
  private final case class Section(
      id: String,
      heading: String,
      title: String,
      category: String,
      mnemonics: Vector[String],
      syntax: Vector[String],
      examples: Vector[Vector[String]],
      introduced: (Int, Int),
      minimumTarget: Int,
      forms: Vector[Form]
  )
  private final case class Form(
      mnemonic: String,
      operands: Vector[String],
      requiredModifierGroups: Vector[Set[String]],
      optionalModifiers: Set[String],
      complete: Boolean
  )

  private val Labels = Set(
    "Syntax",
    "Description",
    "Semantics",
    "Notes",
    "PTX ISA Notes",
    "Target ISA Notes",
    "Examples",
    "Meaning of the modifiers",
    "Launch instructions"
  )
  private val HeadingNumber: Regex   = raw"^((?:\d+\.)+)\s*(.*)$$".r
  private val Mnemonic: Regex        = raw"^\s*([A-Za-z_][A-Za-z0-9_]*)(?=\.|\{|\s|;|$$)".r
  private val Predication: Regex     = raw"^\s*@[!]?[A-Za-z_$$][A-Za-z0-9_$$]*\s+".r
  private val Elision: Regex         = raw"\.{3,}".r
  private val ExamplePrefix: Regex   = raw"^Example \d+:\s*".r
  private val VersionNote: Regex     = raw"Introduced in PTX ISA version ([0-9]+)\.([0-9]+)".r
  private val TargetNote: Regex      = raw"sm_([0-9]+)[af]?\s+or\s+(?:higher|later)".r
  private val Definition: Regex      = raw"(?s)\.([A-Za-z][A-Za-z0-9_]*)\s*=\s*\{([^}]*)\}\s*;".r
  private val DefinitionStart: Regex = raw"(?m)^\s*\.[A-Za-z][A-Za-z0-9_]*\s*=".r
  private val ModifierName: Regex    = raw"[A-Za-z][A-Za-z0-9_]*(?:::[A-Za-z][A-Za-z0-9_]*)*".r

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case Vector("catalog", input, output, version, packageVersion) =>
      val sections = checkedSections(Paths.get(input))
      write(Paths.get(output), renderCatalog(version, packageVersion, sections))
    case Vector("tests", input, output, version, packageVersion) =>
      val sections = checkedSections(Paths.get(input))
      val examples = checkedExamples(sections)
      write(Paths.get(output), renderTests(version, packageVersion, examples))
    case _ =>
      throw new IllegalArgumentException(
        "usage: GeneratePtxIsa (catalog|tests) <ptx.html> <output.scala> <ptx-version> <package-version>"
      )
  }

  private def checkedSections(input: Path): Vector[Section] = {
    val sections = extract(input)
    require(sections.size >= 170, s"expected at least 170 PTX instruction sections, found ${sections.size}")
    sections
  }

  private def checkedExamples(sections: Vector[Section]): Vector[(String, String)] = {
    val examples = sections.flatMap(section => cleanExamples(section.examples).map(section.id -> _))
    require(examples.size >= 750, s"expected at least 750 complete PTX example statements, found ${examples.size}")
    examples
  }

  private def extract(input: Path): Vector[Section] = {
    require(Files.isRegularFile(input), s"PTX specification is absent: $input")
    val document = Jsoup.parse(input.toFile, StandardCharsets.UTF_8.name(), "")
    document.select("h3,h4,h5").asScala.toVector
      .filter(heading => cleanHeading(heading.text()).contains("Instructions:"))
      .flatMap(enclosingSection)
      .distinct
      .flatMap(extractSection)
  }

  private def enclosingSection(heading: Element): Option[Element] = {
    var parent = heading.parent()
    while (parent != null && parent.tagName() != "section") parent = parent.parent()
    Option(parent)
  }

  private def extractSection(section: Element): Option[Section] = {
    val heading = cleanHeading(section.select("h3,h4,h5").asScala.headOption.map(_.text()).getOrElse(""))
    val rest    = heading match {
      case HeadingNumber(_, value) => value
      case _                       => heading
    }
    val separator         = rest.indexOf(": ")
    val (category, title) =
      if (separator > 0) (rest.take(separator), rest.drop(separator + 2)) else ("", rest)
    val syntax   = ListBuffer.empty[String]
    val examples = ListBuffer.empty[Vector[String]]
    var current  = ""
    section.children().asScala.foreach { child =>
      val tag = child.tagName()
      if (tag == "p" && Labels.contains(child.text().trim)) current = child.text().trim
      else
        current match {
          case "Syntax"   => Option(child.selectFirst("pre")).foreach(value => syntax += value.wholeText().trim)
          case "Examples" =>
            Option(child.selectFirst("pre")).foreach { value =>
              val lines = value.wholeText().linesIterator.map(_.stripTrailing()).filter(_.nonEmpty).toVector
              if (lines.nonEmpty) examples += lines
            }
          case _ => ()
        }
    }
    if (syntax.isEmpty) None
    else {
      val syntaxMnemonics = syntax.iterator
        .flatMap(_.linesIterator)
        .map(stripComment)
        .flatMap(extractMnemonic)
      val exampleMnemonics = cleanExamples(examples.toVector).flatMap(extractMnemonic)
      val mnemonics        = (syntaxMnemonics ++ exampleMnemonics)
        .toVector
        .distinct
      val text     = section.text()
      val versions = VersionNote.findAllMatchIn(text).map(value => value.group(1).toInt -> value.group(2).toInt)
        .toVector.distinct
      val targets              = TargetNote.findAllMatchIn(text).map(_.group(1).toInt).toVector.distinct
      val introduced           = versions.minOption.getOrElse(1 -> 0)
      val allTargets           = text.contains("Supported on all target architectures")
      val target               = if (allTargets) 10 else targets.minOption.getOrElse(10)
      val availabilityComplete = versions.size <= 1 && targets.size <= 1 && !(allTargets && targets.nonEmpty)
      val syntaxValues         = syntax.toVector
      Some(
        Section(
          section.attr("id"),
          heading,
          title,
          category,
          mnemonics,
          syntaxValues,
          examples.toVector,
          introduced,
          target,
          normalizeForms(syntaxValues).map(form => form.copy(complete = form.complete && availabilityComplete))
        )
      )
    }
  }

  private def cleanExamples(blocks: Vector[Vector[String]]): Vector[String] = {
    val examples      = ListBuffer.empty[String]
    var buffer        = ""
    def flush(): Unit =
      if (buffer.nonEmpty) {
        if (!isKnownNoise(buffer)) examples += buffer
        buffer = ""
      }
    blocks.foreach { block =>
      block.foreach { raw =>
        val line = ExamplePrefix.replaceFirstIn(raw.trim, "")
        if (line.nonEmpty && !line.startsWith("//"))
          Elision.split(line).foreach { fragment =>
            val value = fragment.trim
            if (value.nonEmpty) {
              buffer = if (buffer.isEmpty) value else buffer + " " + value
              if (buffer.endsWith(";") || buffer.endsWith(":") || buffer == "}") flush()
            }
          }
      }
    }
    flush()
    examples.toVector
  }

  private def isKnownNoise(line: String): Boolean = {
    val normalized = line.replaceAll("\\s+", " ")
    normalized.contains("mask mask") ||
    normalized.contains("%reg1. %reg2") ||
    normalized.contains("}. [a]") ||
    normalized.startsWith(".function ") ||
    normalized == "}" ||
    normalized.contains(".func (.reg .u32 rv) foo") ||
    normalized.contains(".callprototype") ||
    normalized.endsWith("[shmem], ;") ||
    normalized.startsWith(".. ") ||
    normalized.contains("zdim, ignr}") ||
    normalized.contains("is_canceled pred.b128")
  }

  private def normalizeForms(blocks: Vector[String]): Vector[Form] = blocks.flatMap { block =>
    val definitions = Definition.findAllMatchIn(block).map { matched =>
      val values = matched.group(2).split("[,|\\s]+").toVector.map(_.trim.stripPrefix(".")).filter(value =>
        ModifierName.matches(value)
      ).toSet
      matched.group(1) -> values
    }.toMap
    val syntaxOnly = DefinitionStart.findFirstMatchIn(block).fold(block)(matched => block.take(matched.start))
    syntaxOnly.split(';').toVector.flatMap(fragment => normalizeForm(fragment.trim, definitions))
  }.distinct

  private def normalizeForm(source: String, definitions: Map[String, Set[String]]): Option[Form] = {
    val normalized = source.replaceAll("\\s+", " ").trim
    val space      = normalized.indexWhere(_.isWhitespace)
    if (space <= 0) None
    else {
      val opcodeToken = normalized.take(space)
      extractMnemonic(opcodeToken).map { mnemonic =>
        val (required, optional, modifierComplete) = normalizeModifiers(opcodeToken.drop(mnemonic.length), definitions)
        val operandText                            = normalized.drop(space).trim
        val operandNames = splitTopLevel(operandText).map(value => value.takeWhile(char => char != '=').trim)
        val operands     = operandNames.map(operandConstraint)
        Form(mnemonic, operands.map(_._1), required, optional, modifierComplete && operands.forall(_._2))
      }
    }
  }

  private def normalizeModifiers(
      source: String,
      definitions: Map[String, Set[String]]
  ): (Vector[Set[String]], Set[String], Boolean) = {
    val required = Vector.newBuilder[Set[String]]
    var optional = Set.empty[String]
    var complete = true
    var index    = 0
    while (index < source.length)
      source(index) match {
        case '.' =>
          val name = ModifierName.findPrefixOf(source.drop(index + 1)).getOrElse("")
          if (name.isEmpty) {
            complete = false
            index += 1
          } else {
            required += definitions.getOrElse(name, Set(name))
            index += name.length + 1
          }
        case '{' =>
          val end = source.indexOf('}', index + 1)
          if (end < 0) {
            complete = false
            index = source.length
          } else {
            val names = raw"\.([A-Za-z][A-Za-z0-9_]*(?:::[A-Za-z][A-Za-z0-9_]*)*)".r
              .findAllMatchIn(source.slice(index + 1, end)).flatMap { matched =>
                val name = matched.group(1)
                definitions.get(name).getOrElse(Set(name))
              }.toSet
            if (names.isEmpty) complete = false else optional ++= names
            index = end + 1
          }
        case _ =>
          complete = false
          index += 1
      }
    (required.result().filter(_.nonEmpty), optional, complete)
  }

  private def splitTopLevel(source: String): Vector[String] =
    if (source.isEmpty) Vector.empty
    else {
      val result = Vector.newBuilder[String]
      var start  = 0
      var round  = 0
      var square = 0
      var brace  = 0
      source.indices.foreach { index =>
        source(index) match {
          case '('                                            => round += 1
          case ')'                                            => round -= 1
          case '['                                            => square += 1
          case ']'                                            => square -= 1
          case '{'                                            => brace += 1
          case '}'                                            => brace -= 1
          case ',' if round == 0 && square == 0 && brace == 0 =>
            result += source.slice(start, index).trim
            start = index + 1
          case _ => ()
        }
      }
      val last = source.drop(start).trim
      if (last.nonEmpty) result += last
      result.result().filter(_.nonEmpty)
    }

  private def operandConstraint(source: String): (String, Boolean) = {
    val value = source.trim
    val name  = value.replaceAll("^[!+\\-]", "").takeWhile(char => char.isLetterOrDigit || char == '_').toLowerCase
    if (value.startsWith("[")) "Address" -> true
    else if (value.startsWith("{")) "Vector" -> true
    else if (Set("d", "dst", "p", "q", "r", "result").contains(name)) "Register" -> true
    else if (name.contains("label") || name == "target" || name.contains("func")) "Symbol" -> true
    else if (name.nonEmpty) "Value" -> true
    else "Unknown"                  -> false
  }

  private def renderCatalog(version: String, packageVersion: String, sections: Vector[Section]): String = {
    require(packageVersion.matches("v[0-9]+(?:_[0-9]+)*"), s"invalid PTX package version: $packageVersion")
    val instructions = sections.map { section =>
      s"""|    InstructionInfo(
          |      ${literal(section.id)},
          |      ${literal(section.heading)},
          |      ${literal(section.title)},
          |      ${literal(section.category)},
          |      ${vector(section.mnemonics)},
          |      ${vector(section.syntax)},
          |      PtxVersion(${section.introduced._1}, ${section.introduced._2}),
          |      SmTarget(${section.minimumTarget}),
          |      Vector(${section.forms.map(renderForm).mkString(", ")})
          |    )""".stripMargin
    }
    val chunks = instructions.grouped(12).zipWithIndex.map { case (values, index) =>
      s"""|  private def chunk$index: Vector[InstructionInfo] = Vector(
          |${values.mkString(",\n")}
          |  )""".stripMargin
    }.toVector
    val chunkNames = chunks.indices.map(index => s"chunk$index").mkString("Vector(", ", ", ").flatten")
    s"""|package net.kurobako.gemstone.isa.ptx.$packageVersion.generated
        |
        |import net.kurobako.gemstone.isa.ptx.$packageVersion.InstructionCatalog
        |import net.kurobako.gemstone.isa.ptx.$packageVersion.InstructionInfo
        |import net.kurobako.gemstone.isa.ptx.$packageVersion.InstructionForm
        |import net.kurobako.gemstone.isa.ptx.$packageVersion.OperandConstraint
        |import net.kurobako.gemstone.isa.ptx.$packageVersion.PtxVersion
        |import net.kurobako.gemstone.isa.ptx.$packageVersion.SmTarget
        |
        |// Generated from the pinned NVIDIA PTX ISA $version specification. Do not edit.
        |object Catalog extends InstructionCatalog {
        |  override val version: String = ${literal(version)}
        |${chunks.mkString("\n\n")}
        |
        |  override lazy val instructions: Vector[InstructionInfo] = $chunkNames
        |}
        |""".stripMargin
  }

  private def renderTests(version: String, packageVersion: String, examples: Vector[(String, String)]): String = {
    require(packageVersion.matches("v[0-9]+(?:_[0-9]+)*"), s"invalid PTX package version: $packageVersion")
    val values = examples.map { case (id, source) => s"    ${literal(id)} -> ${literal(source)}" }.mkString(",\n")
    s"""|package net.kurobako.gemstone.isa.ptx.$packageVersion.generated
        |
        |import net.kurobako.gemstone.isa.ptx.$packageVersion.Printer
        |import net.kurobako.gemstone.isa.ptx.$packageVersion.Statement
        |import net.kurobako.gemstone.isa.ptx.$packageVersion.Parser
        |
        |// Generated from examples in the pinned NVIDIA PTX ISA $version specification. Do not edit.
        |class DocumentationSuite extends munit.FunSuite {
        |  private val examples: Vector[(String, String)] = Vector(
        |$values
        |  )
        |
        |  test("all documentation examples parse and use known mnemonics") {
        |    val failures = examples.flatMap { case (id, source) =>
        |      Parser.parse(source) match {
        |        case Left(error) => Vector(s"$$id: $$source\\n  $$error")
        |        case Right(program) => program.statements.collectFirst { case Statement.Instruction(instruction) => instruction }
        |          .filterNot(instruction => Catalog.isKnown(instruction.opcode))
        |          .map(instruction => s"$$id: unknown mnemonic $${instruction.opcode.mnemonic}: $$source")
        |          .toVector
        |      }
        |    }
        |    assertEquals(failures, Vector.empty)
        |  }
        |
        |  test("all documentation examples round-trip through the canonical printer") {
        |    val failures = examples.flatMap { case (id, source) =>
        |      Parser.parse(source) match {
        |        case Left(error) => Vector(s"$$id: initial parse failed: $$error")
        |        case Right(first) =>
        |          val printed = Printer.render(first)
        |          Parser.parse(printed) match {
        |            case Right(second) if second == first => Vector.empty
        |            case Right(second) => Vector(s"$$id: $$source\\n  printed: $$printed\\n  first: $$first\\n  second: $$second")
        |            case Left(error) => Vector(s"$$id: $$source\\n  printed: $$printed\\n  reparse: $$error")
        |          }
        |      }
        |    }
        |    assertEquals(failures, Vector.empty)
        |  }
        |}
        |""".stripMargin
  }

  private def vector(values: Vector[String]): String = values.map(literal).mkString("Vector(", ", ", ")")

  private def renderForm(form: Form): String = {
    val operands = form.operands.map {
      case "Register" => "OperandConstraint.Register"
      case "Address"  => "OperandConstraint.Address"
      case "Vector"   => "OperandConstraint.Vector"
      case "Symbol"   => "OperandConstraint.Symbol"
      case "Value"    => "OperandConstraint.Value"
      case _          => "OperandConstraint.Unknown(\"unparsed\")"
    }.mkString("Vector(", ", ", ")")
    val required = form.requiredModifierGroups.map(values =>
      values.toVector.sorted.map(literal).mkString("Set(", ", ", ")")
    ).mkString("Vector(", ", ", ")")
    val optional = form.optionalModifiers.toVector.sorted.map(literal).mkString("Set(", ", ", ")")
    s"InstructionForm(${literal(form.mnemonic)}, $operands, $required, $optional, ${form.complete})"
  }

  private def literal(value: String): String = {
    val escaped = value.flatMap {
      case '"'                          => "\\\""
      case '\\'                         => "\\\\"
      case '\n'                         => "\\n"
      case '\r'                         => "\\r"
      case '\t'                         => "\\t"
      case character if character < ' ' => f"\\u${character.toInt}%04x"
      case character                    => character.toString
    }
    "\"" + escaped + "\""
  }

  private def stripComment(value: String): String            = value.replaceAll("//.*$", "").trim
  private def extractMnemonic(value: String): Option[String] =
    Mnemonic.findFirstMatchIn(Predication.replaceFirstIn(stripComment(value), "")).map(_.group(1).toLowerCase)
  private def cleanHeading(value: String): String = value.replace("\uf0c1", "").replaceAll("\\s+", " ").trim

  private def write(output: Path, content: String): Unit = {
    val current = if (Files.isRegularFile(output)) Some(Files.readString(output, StandardCharsets.UTF_8)) else None
    if (!current.contains(content)) {
      val _ = Files.createDirectories(output.getParent)
      val _ = Files.writeString(output, content, StandardCharsets.UTF_8)
    }
  }
}
