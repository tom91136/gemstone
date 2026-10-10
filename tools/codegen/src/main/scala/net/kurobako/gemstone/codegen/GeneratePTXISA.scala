package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

/** Generates the typed instruction model and parser corpus from the pinned NVIDIA PTX HTML document. */
object GeneratePTXISA {
  private case class SpecificationVersion(source: NonEmpty[String])
  private case class Request(
      tests: Boolean,
      input: Path,
      sourceRoot: Path,
      version: String
  )
  private case class Section(
      id: String,
      title: String,
      mnemonics: Vector[String],
      examples: Vector[Vector[String]],
      introduced: (major: Int, minor: Int),
      minimumTarget: Int,
      modifierTargets: Map[Set[String], Int],
      forms: Vector[Form]
  )
  private case class Form(
      mnemonic: String,
      operands: Vector[FormOperand],
      requiredModifierGroups: Vector[Set[String]],
      optionalModifiers: Set[String],
      unorderedModifiers: Set[String],
      leadingOptional: Boolean,
      complete: Boolean
  )
  private case class FormOperand(sourceName: String, constraint: String, optional: Boolean, complete: Boolean)
  private case class InstructionField(name: String, optional: Boolean)
  private case class InstructionShape(
      opcode: String,
      mnemonic: String,
      operationModifiers: Vector[String],
      caseName: String,
      fields: Vector[InstructionField],
      forms: Vector[Form],
      decodeGuard: Option[String] = None
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
  private val HeadingNumber: Regex = raw"^((?:\d+\.)+)\s*(.*)$$".r
  private val Mnemonic: Regex      = raw"^\s*([A-Za-z_][A-Za-z0-9_]*)(?=\.|\{|\s|;|$$)".r
  private val Predication: Regex   = raw"^\s*@[!]?[A-Za-z_$$][A-Za-z0-9_$$]*\s+".r
  private val Elision: Regex       = raw"\.{3,}".r
  private val ExamplePrefix: Regex = raw"^Example \d+:\s*".r
  private val VersionNote: Regex   = raw"Introduced in PTX ISA version ([0-9]+)\.([0-9]+)".r
  private val TargetNote: Regex    = raw"sm_([0-9]+)[af]?\s+or\s+(?:higher|later)".r
  private val Definition: Regex    = raw"(?:\.|::)([A-Za-z][A-Za-z0-9_]*(?:::[A-Za-z0-9_]+)*)\s*=\s*\{".r
  private val NoteClause: Regex    = raw"(?<=(?:higher|later)\.?)\s+|(?<=\.)\s+(?=[A-Z])".r
  private val Spelling: Regex      =
    raw"([A-Za-z_][A-Za-z0-9_]*)?((?:\.?\{[^{}]*\}|\.[A-Za-z][A-Za-z0-9_]*(?:::[A-Za-z0-9_]+)*)+)".r
  private val SpellingPart: Regex     = raw"\{([^{}]*)\}|\.([A-Za-z][A-Za-z0-9_]*(?:::[A-Za-z0-9_]+)*)".r
  private val OpcodeDefinition: Regex = raw"(?s)(?:^|\n)\s*([A-Za-z][A-Za-z0-9_]*)\s*=\s*\{([^}]*)\}\s*;?".r
  private val DefinitionStart: Regex  = raw"^\s*(?:\.|::)?[A-Za-z][A-Za-z0-9_:-]*\s*=".r
  private val TitleOpcode: Regex      = raw"[A-Za-z][A-Za-z0-9_]*(?:\.[A-Za-z0-9_]+)+".r
  private val ModifierName: Regex     = "[A-Za-z][A-Za-z0-9_]*(?:::[A-Za-z0-9_]+)*".r
  private val OptionalSpelling: Regex =
    raw"\.([A-Za-z][A-Za-z0-9_]*(?:::[A-Za-z0-9_]+)*)(?:\{([^{}]*)\})?".r
  private val DomainValue: Regex = "[A-Za-z0-9][A-Za-z0-9_]*(?:::[A-Za-z0-9_]+)*".r
  private val PackageRoot        = "net/kurobako/gemstone/isa/ptx"

  /** Qualifier domains whose spelling order is not significant; toolchains emit `ldmatrix.sync.aligned.x4.m8n8`. */
  private val UnorderedQualifiers: Map[String, Set[String]] = Map("ldmatrix" -> Set("shape", "num", "trans"))

  private def request(tests: Boolean): Opts[Request] = (
    Opts.argument[String]("ptx-html").map(Path.of(_)),
    Opts.argument[String]("generated-source-root").map(Path.of(_)),
    Opts.argument[String]("ptx-version")
  ).mapN((input, sourceRoot, version) => Request(tests, input, sourceRoot, version))

  private val command = Command("GeneratePTXISA", "Generate PTX ISA sources")(
    Opts.subcommands(
      Command("instructions", "Generate instruction sources")(request(tests = false)),
      Command("tests", "Generate reference tests")(request(tests = true))
    )
  )

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) { request =>
    val specificationVersion = parseSpecificationVersion(request.version)
    val sections             = checkedSections(request.input)
    val fileName             = if (request.tests) "DocumentationSuite.scala" else "Instruction.scala"
    val outputRoot           = request.sourceRoot.resolve(PackageRoot)
    val output               = outputRoot.resolve(fileName)
    if (request.tests) {
      val examples = checkedExamples(sections)
      write(output, renderTests(specificationVersion, examples))
    } else write(output, renderInstructions(specificationVersion, sections))
    val domains = if (request.tests) Vector.empty
    else renderDomains(sections).map { case (name, source) =>
      val path = output.resolveSibling(s"$name.scala")
      write(path, source)
      path
    }
    val include  = (path: Path) => path.getFileName.toString.endsWith(".scala")
    val expected = if (request.tests) Set(output) else Set(output) ++ domains
    GeneratedFiles.removeUnexpected(outputRoot, expected, include)
  }

  private def renderDomains(sections: Vector[Section]): Vector[(String, String)] = {
    val shapes    = instructionShapes(sections)
    val opcodes   = shapes.map(_.opcode.takeWhile(_ != '.')).distinct.sorted
    val modifiers =
      (shapes.flatMap(_.opcode.split('.').drop(1)) ++
        sections.flatMap(_.forms).flatMap(form => form.requiredModifierGroups.flatten ++ form.optionalModifiers) ++
        sections.flatMap(section =>
          section.forms.flatMap(form =>
            operationsFor(section, form).flatMap { case (opcode, _) => opcode.split('.').drop(1) }
          )
        )).distinct.sorted
    def render(name: String, values: Vector[String], unknown: Boolean): String = {
      require(values.map(scalaCaseName(_)).distinct.size == values.size, s"ambiguous PTX $name names")
      val cases =
        values.map(value => s"  case ${scalaCaseName(value)} extends $name(${Emission.literal(value)})").mkString("\n")
      val entries  = values.map(value => s"${Emission.literal(value)} -> ${scalaCaseName(value)}").mkString(", ")
      val fallback =
        if (unknown) "Unrecognised(name)" else "throw IllegalArgumentException(s\"unknown PTX opcode $name\")"
      val extra = if (unknown) "  case Unrecognised(spelling: NonEmpty[String]) extends Modifier(spelling)" else ""
      GeneratedHeader.scala("from the pinned PTX syntax domains") +
        s"""|package net.kurobako.gemstone.isa.ptx
            |import net.kurobako.gemstone.constraint.*
            |
            |object $name {
            |  private lazy val byName: Map[String, $name] = Map($entries)
            |  def apply(name: NonEmpty[String]): $name = byName.getOrElse(name.raw, $fallback)
            |  def unapply(value: $name): Some[NonEmpty[String]] = Some(value.name)
            |}
            |
            |${GeneratedHeader.annotation()}enum $name(val name: NonEmpty[String]) {
            |$cases
            |$extra
            |}
            |""".stripMargin
    }
    Vector(
      "Opcode"   -> render("Opcode", opcodes, unknown = false),
      "Modifier" -> render("Modifier", modifiers, unknown = true)
    )
  }

  private def checkedSections(input: Path): Vector[Section] = {
    val sections = extract(input)
    if (sections.size < 170)
      throw IllegalStateException(s"expected at least 170 PTX instruction sections, found ${sections.size}")
    sections
  }

  private def checkedExamples(sections: Vector[Section]): Vector[(String, String)] = {
    val examples = sections.flatMap(section => cleanExamples(section.examples).map(section.id -> _))
    if (examples.size < 750)
      throw IllegalStateException(s"expected at least 750 complete PTX example statements, found ${examples.size}")
    examples
  }

  private def extract(input: Path): Vector[Section] = {
    if (!Files.isRegularFile(input)) throw IllegalArgumentException(s"PTX specification is absent: $input")
    val document = Jsoup.parse(input.toFile, StandardCharsets.UTF_8.name(), "")
    document.select("h3,h4,h5,h6").asScala.toVector
      .filter { heading =>
        val text = cleanHeading(heading.text()).toLowerCaseRoot
        text.contains("instructions:") || text.contains("instruction:")
      }
      .flatMap(enclosingSection(_))
      .distinct
      .flatMap(extractSection(_))
  }

  private def enclosingSection(heading: Element): Option[Element] =
    Iterator.iterate(Option(heading.parent()))(_.flatMap(parent => Option(parent.parent())))
      .takeWhile(_.nonEmpty)
      .flatten
      .find(_.tagName() == "section")

  private def extractSection(section: Element): Option[Section] = {
    val heading = cleanHeading(section.select("h3,h4,h5,h6").asScala.headOption.map(_.text()).getOrElse(""))
    val rest    = heading match {
      case HeadingNumber(_, value) => value
      case _                       => heading
    }
    val separator                          = rest.indexOf(": ")
    val title                              = if (separator > 0) rest.drop(separator + 2) else rest
    val (_, syntax, examples, targetNotes) = section.children().asScala.foldLeft(
      ("", Vector.empty[String], Vector.empty[Vector[String]], Vector.empty[String])
    ) { case ((current, syntax, examples, targetNotes), child) =>
      val tag = child.tagName()
      if (tag == "p" && Labels.contains(child.text().trim)) (child.text().trim, syntax, examples, targetNotes)
      else
        current match {
          case "Syntax" =>
            (current, syntax ++ Option(child.selectFirst("pre")).map(_.wholeText().trim), examples, targetNotes)
          case "Examples" =>
            val found = Option(child.selectFirst("pre")).flatMap { value =>
              val lines = value.wholeText().linesIterator.map(_.stripTrailing()).filter(_.nonEmpty).toVector
              Option.when(lines.nonEmpty)(lines)
            }
            (current, syntax, examples ++ found, targetNotes)
          case "Target ISA Notes" => (current, syntax, examples, targetNotes :+ child.text())
          case _                  => (current, syntax, examples, targetNotes)
        }
    }
    if (syntax.isEmpty) None
    else {
      val text                                       = section.text()
      val versions: Vector[(major: Int, minor: Int)] = VersionNote.findAllMatchIn(text)
        .map(value => (major = value.group(1).toInt, minor = value.group(2).toInt))
        .toVector.distinct
      val targets    = TargetNote.findAllMatchIn(text).map(_.group(1).toInt).toVector.distinct
      val introduced = versions.minOption.getOrElse((major = 1, minor = 0))
      val allTargets = text.contains("Supported on all target architectures")
      val target     = if (allTargets) 10 else targets.minOption.getOrElse(10)
      val forms      = normaliseForms(syntax)
      val mnemonics  = forms.map(_.mnemonic).distinct
      Some(
        Section(
          section.attr("id"),
          title,
          mnemonics,
          examples,
          introduced,
          target,
          modifierTargets(targetNotes, forms),
          forms
        )
      )
    }
  }

  /** Target notes such as "tanh.approx.{bf16/bf16x2} requires sm_90 or higher" restrict instructions carrying every
    * modifier of one spelling, with braced lists as alternatives. A single braced modifier counts as present: notes
    * write "fma{.oob}.f16" for the `.oob` requirement, and treating it as absent would restrict plain `fma.f16`.
    * Modifiers named without an opcode qualify the opcode spellings of the same clause ("`.ftz` qualifier for
    * `cvt.f32.bf16`"). A combination named by several clauses takes the least restrictive target.
    */
  private def modifierTargets(notes: Vector[String], forms: Vector[Form]): Map[Set[String], Int] = {
    val known     = forms.flatMap(form => form.requiredModifierGroups.flatten ++ form.optionalModifiers).toSet
    val mnemonics = forms.map(_.mnemonic).toSet
    def combinations(parts: String): Vector[Set[String]] =
      SpellingPart.findAllMatchIn(parts).foldLeft(Vector(Set.empty[String])) { (sets, part) =>
        Option(part.group(2)) match {
          case Some(name) => sets.map(_ + name)
          case None       =>
            val alternatives = Text.splitWords(part.group(1), Set('/', ',', '|')).map(_.stripPrefix("."))
            sets.flatMap(set => alternatives.map(set + _))
        }
      }.map(_.filter(known)).filter(_.nonEmpty)
    notes.flatMap(NoteClause.split(_)).flatMap { clause =>
      TargetNote.findAllMatchIn(clause).map(_.group(1).toInt).minOption.toVector.flatMap { target =>
        val spellings = Spelling.findAllMatchIn(clause).toVector
          .map(matched => Option(matched.group(1)).exists(mnemonics) -> combinations(matched.group(2)))
        val qualified  = spellings.view.filter(_._1).flatMap(_._2).toVector
        val standalone = spellings.view.filterNot(_._1).flatMap(_._2).toVector
        (if (qualified.isEmpty) standalone else qualified.map(_ ++ standalone.flatten)).map(_ -> target)
      }
    }.groupMapReduce(_._1)(_._2)(math.min)
  }

  private def cleanExamples(blocks: Vector[Vector[String]]): Vector[String] = {
    def flush(examples: Vector[String], buffer: String): (Vector[String], String) =
      if (buffer.isEmpty || isKnownNoise(buffer)) examples -> "" else (examples :+ buffer) -> ""
    val (examples, buffer) = blocks.flatten.foldLeft((Vector.empty[String], "")) {
      case ((examples, buffer), raw) =>
        val line = ExamplePrefix.replaceFirstIn(raw.trim, "")
        if (line.nonEmpty && !line.startsWith("//")) {
          Elision.split(line).foldLeft((examples, buffer)) { case ((currentExamples, currentBuffer), fragment) =>
            val value = fragment.trim
            if (value.nonEmpty) {
              val next = if (currentBuffer.isEmpty) value else currentBuffer + " " + value
              if (next.endsWith(";") || next.endsWith(":") || next == "}") flush(currentExamples, next)
              else currentExamples -> next
            } else currentExamples -> currentBuffer
          }
        } else examples -> buffer
    }
    flush(examples, buffer)._1
  }

  private def isKnownNoise(line: String): Boolean = {
    val normalised         = Text.normaliseWhitespace(line)
    val collectorDirective = normalised.split(' ').exists {
      case s"collector::$name:$action" => name.nonEmpty && (action == "fill" || action == "use")
      case _                           => false
    }
    normalised.contains("mask mask") ||
    normalised.contains("%reg1. %reg2") ||
    normalised.contains("}. [a]") ||
    normalised.startsWith(".function ") ||
    normalised == "}" ||
    normalised.contains(".func (.reg .u32 rv) foo") ||
    normalised.contains(".callprototype") ||
    normalised.endsWith("[shmem], ;") ||
    normalised.endsWith("}, ;") ||
    normalised.startsWith(".. ") ||
    normalised.contains("zdim, ignr}") ||
    normalised.contains("is_canceled pred.b128") ||
    normalised == "neg.s8x4 p, q, r;" ||
    normalised == "isspacep.shared::cluster ishrdany sptr;" ||
    normalised.contains("cache_hint}") ||
    normalised ==
      "multimem.cp.reduce.async.bulk.relaxed.cta.global.bulk_group.add.u64 [dstMem], [srcMem], size, [mbar];" ||
      normalised == "mbarrier.init [bar];" ||
      normalised == "@!p bra try_wait loop;" ||
      normalised.contains("a-desc") ||
      normalised.contains(".cta_group.kind ") ||
      collectorDirective
  }

  // The pinned specification spells the double precision mma shape `.m8n84`
  private val SyntaxErrata = Map(".m8n84," -> ".m8n8k4,")

  private def normaliseForms(blocks: Vector[String]): Vector[Form] = blocks.map(block =>
    SyntaxErrata.foldLeft(block)((text, errata) => text.replace(errata._1, errata._2))
  ).flatMap { block =>
    val definitions       = domainDefinitions(block)
    val opcodeDefinitions = OpcodeDefinition.findAllMatchIn(block).map { matched =>
      val values = Text.splitWords(matched.group(2), Set(',', '|')).view.map(_.stripPrefix(".")).filter(value =>
        Mnemonic.matches(value)
      ).toVector
      matched.group(1) -> values
    }.toMap
    scopes(block).view.flatMap { scope =>
      val scoped = definitions ++ domainDefinitions(scope)
      syntaxFragments(scope).flatMap(fragment => normaliseForm(fragment, scoped))
    }.flatMap { form =>
      opcodeDefinitions.get(form.mnemonic).filter(_.nonEmpty) match {
        case Some(opcodes) => opcodes.map(opcode => form.copy(mnemonic = opcode))
        case None          => Vector(form)
      }
    }
  }.distinct

  private def domainDefinitions(block: String): Map[String, Set[String]] =
    Definition.findAllMatchIn(block).flatMap { matched =>
      val values    = definitionValues(block.slice(matched.end, closingBrace(block, matched.end - 1)))
      val lineStart = block.lastIndexOf('\n', matched.start) + 1
      val aliases   = block.substring(lineStart, matched.start).split('=').view.map(_.trim).collect {
        case s".$name" if ModifierName.matches(name) => name
      }.toVector
      (aliases :+ matched.group(1)).map(_ -> values)
    }.toMap

  /** Forms take the definitions that follow them; `redux.sync` redefines `.op` for its integer, bitwise and float
    * forms.
    */
  private def scopes(block: String): Vector[String] =
    block.linesIterator.foldLeft((Vector.empty[Vector[String]], false)) { case ((scopes, defined), rawLine) =>
      val line       = Text.stripLineComment(rawLine)
      val definition = DefinitionStart.findPrefixMatchOf(line).nonEmpty
      val beginsForm = rawLine.headOption.exists(!_.isWhitespace) && !definition && extractMnemonic(line).nonEmpty
      if (scopes.isEmpty || (beginsForm && defined)) (scopes :+ Vector(rawLine), false)
      else (scopes.init :+ (scopes.last :+ rawLine), defined || definition)
    }._1.map(_.mkString("\n"))

  private def closingBrace(source: String, open: Int): Int =
    source.indices.drop(open).view.scanLeft(0)((depth, index) =>
      source(index) match {
        case '{' => depth + 1
        case '}' => depth - 1
        case _   => depth
      }
    ).zipWithIndex.drop(1).collectFirst { case (0, step) => open + step - 1 }.getOrElse(source.length)

  /** Values of a domain definition; `.param{::entry, ::func}` contributes `param` and each qualified spelling. */
  private def definitionValues(source: String): Set[String] =
    splitTopLevel(source.replace('|', ',')).flatMap { value =>
      val base       = value.takeWhile(_ != '{').trim.stripPrefix(".").stripPrefix("::")
      val qualifiers = "::([A-Za-z0-9_]+)".r.findAllMatchIn(value.dropWhile(_ != '{')).map(_.group(1))
      base +: qualifiers.map(qualifier => s"$base::$qualifier").toVector
    }.filter(DomainValue.matches(_)).toSet

  private def syntaxFragments(block: String): Vector[String] = {
    def flush(fragments: Vector[String], current: String): (Vector[String], String) =
      (fragments ++ current.split(';').view.map(_.trim).filter(_.nonEmpty)) -> ""
    val (fragments, current) = block.linesIterator.foldLeft((Vector.empty[String], "")) {
      case ((fragments, current), rawLine) =>
        val line       = Text.stripLineComment(rawLine)
        val beginsForm = rawLine.headOption.exists(!_.isWhitespace) &&
          DefinitionStart.findPrefixMatchOf(line).isEmpty && extractMnemonic(line).nonEmpty
        if (beginsForm) {
          val (flushed, _) = flush(fragments, current)
          if (line.endsWith(";")) flush(flushed, line) else flushed -> line
        } else if (current.nonEmpty && line.nonEmpty && DefinitionStart.findPrefixMatchOf(line).isEmpty) {
          val next = s"$current $line"
          if (line.endsWith(";")) flush(fragments, next) else fragments -> next
        } else if (DefinitionStart.findPrefixMatchOf(line).nonEmpty) {
          flush(fragments, current)
        } else fragments -> current
    }
    flush(fragments, current)._1
  }

  private def normaliseForm(source: String, definitions: Map[String, Set[String]]): Option[Form] = {
    val normalised = Predication.replaceFirstIn(Text.normaliseWhitespace(source), "")
    val space      = normalised.indexWhere(_.isWhitespace)
    if (normalised.isEmpty) None
    else {
      val opcodeToken = if (space < 0) normalised else normalised.take(space)
      extractMnemonic(opcodeToken).map { mnemonic =>
        val (required, optional, modifierComplete) = normaliseModifiers(opcodeToken.drop(mnemonic.length), definitions)
        val operandText                            = if (space < 0) "" else normalised.drop(space).trim
        val operandNames                           = splitTopLevel(operandText).flatMap(expandOptionalOperand(_))
        val operands = operandNames.zipWithIndex.map { case ((name, optionalOperand), index) =>
          val (constraint, complete) = operandConstraint(name, destination = index == 0)
          FormOperand(name, constraint, optionalOperand, complete)
        }
        val unordered = UnorderedQualifiers.getOrElse(mnemonic, Set.empty).flatMap(name =>
          definitions.getOrElse(name, Set(name))
        ).intersect(required.flatten.toSet ++ optional)
        val leading = opcodeToken.drop(mnemonic.length).startsWith("{.")
        Form(
          mnemonic,
          operands,
          required,
          optional,
          unordered,
          leading,
          modifierComplete && operands.forall(_.complete)
        )
      }
    }
  }

  /** Expands the PTX notation `value{ optional}` into two constructor fields. Braced components which do not begin with
    * a comma are modifiers of the preceding operand and remain part of its source name.
    */
  private def expandOptionalOperand(source: String): Vector[(String, Boolean)] = {
    val value  = source.trim
    val groups = value.indices.foldLeft((0, Vector.empty[(Int, Int)])) { case ((depth, groups), index) =>
      value(index) match {
        case '{' if depth == 0 => (1, groups :+ (index -> closingBrace(value, index)))
        case '{'               => (depth + 1, groups)
        case '}'               => (depth - 1, groups)
        case _                 => (depth, groups)
      }
    }._2.filter((start, end) => value.slice(start + 1, end).trim.startsWith(","))
    val head = groups.foldRight(value)((group, text) => text.patch(group._1, "", group._2 - group._1 + 1)).trim
    val tail = groups.flatMap((start, end) =>
      splitTopLevel(value.slice(start + 1, end).trim.drop(1)).view.flatMap(expandOptionalOperand(_)).map(_._1 -> true)
    )
    (if (head.isEmpty) Vector.empty else Vector(head -> false)) ++ tail
  }

  private def normaliseModifiers(
      source: String,
      definitions: Map[String, Set[String]]
  ): (Vector[Set[String]], Set[String], Boolean) = {
    @tailrec
    def loop(
        index: Int,
        required: Vector[Set[String]],
        optional: Set[String],
        complete: Boolean
    ): (Vector[Set[String]], Set[String], Boolean) =
      if (index >= source.length) (required.filter(_.nonEmpty), optional, complete)
      else source(index) match {
        case '.' =>
          val name = ModifierName.findPrefixOf(source.drop(index + 1)).getOrElse("")
          if (name.isEmpty) loop(index + 1, required, optional, false)
          else loop(index + name.length + 1, required :+ definitions.getOrElse(name, Set(name)), optional, complete)
        case '{' =>
          val end = closingBrace(source, index)
          if (end >= source.length) loop(source.length, required, optional, false)
          else if (source.startsWith("::", index + 1) && required.nonEmpty) {
            val suffixes = "::([A-Za-z][A-Za-z0-9_]*)".r
              .findAllMatchIn(source.slice(index + 1, end)).flatMap { matched =>
                val name = matched.group(1)
                definitions.getOrElse(name, Set(name))
              }.toSet
            val qualified = for {
              base   <- required.last
              suffix <- suffixes
            } yield s"$base::$suffix"
            loop(end + 1, required.init :+ (required.last ++ qualified), optional, complete && suffixes.nonEmpty)
          } else {
            val names = OptionalSpelling.findAllMatchIn(source.slice(index + 1, end)).flatMap { matched =>
              val bases    = definitions.getOrElse(matched.group(1), Set(matched.group(1)))
              val suffixes = Option(matched.group(2)).toVector.flatMap(group =>
                "::([A-Za-z][A-Za-z0-9_]*)".r.findAllMatchIn(group).map(_.group(1))
              )
              bases ++ bases.flatMap(base => suffixes.map(suffix => s"$base::$suffix"))
            }.toSet
            loop(end + 1, required, optional ++ names, complete && names.nonEmpty)
          }
        case _ => loop(index + 1, required, optional, false)
      }
    loop(0, Vector.empty, Set.empty, true)
  }

  private def splitTopLevel(source: String): Vector[String] =
    if (source.isEmpty) Vector.empty
    else {
      val (start, _, _, _, result) = source.indices.foldLeft((0, 0, 0, 0, Vector.empty[String])) {
        case ((start, round, square, brace, result), index) =>
          source(index) match {
            case '('                                            => (start, round + 1, square, brace, result)
            case ')'                                            => (start, round - 1, square, brace, result)
            case '['                                            => (start, round, square + 1, brace, result)
            case ']'                                            => (start, round, square - 1, brace, result)
            case '{'                                            => (start, round, square, brace + 1, result)
            case '}'                                            => (start, round, square, brace - 1, result)
            case ',' if round == 0 && square == 0 && brace == 0 =>
              (index + 1, round, square, brace, result :+ source.slice(start, index).trim)
            case _ => (start, round, square, brace, result)
          }
      }
      val last = source.drop(start).trim
      (result ++ Option.when(last.nonEmpty)(last)).filter(_.nonEmpty)
    }

  /** Destination names only denote registers in the first position; `bfi f, a, b, c, d` takes a value `d`. */
  private def operandConstraint(source: String, destination: Boolean): (String, Boolean) = {
    val value    = Text.stripLeadingSignGroup(source.trim)
    val unsigned = value match {
      case s"!$rest" => rest
      case s"+$rest" => rest
      case s"-$rest" => rest
      case _         => value
    }
    val name =
      unsigned.takeWhile(character => character.isLetterOrDigit || character == '_').toLowerCaseRoot
    if (value.startsWith("[")) "Address" -> true
    else if (value.startsWith("(")) "Value" -> true
    else if (value.startsWith("{")) "Vector" -> true
    else if (destination && Set("d", "dst", "p", "q", "r", "result").contains(name)) "Register" -> true
    else if (name.contains("label") || name == "target" || name.contains("func")) "Symbol" -> true
    else if (name.nonEmpty) "Value" -> true
    else "Unknown"                  -> false
  }

  private def renderInstructions(
      version: SpecificationVersion,
      sections: Vector[Section]
  ): String = {
    val shapes = instructionShapes(sections).flatMap {
      case shape if shape.opcode == "cp.async.bulk" =>
        Vector(
          shape.copy(
            caseName = "CpAsyncBulkMBarrier",
            fields = Vector(
              InstructionField("destinationAddress", false),
              InstructionField("sourceAddress", false),
              InstructionField("size", false),
              InstructionField("barrierAddress", false),
              InstructionField("ignoredBytesLeft", true),
              InstructionField("ignoredBytesRight", true),
              InstructionField("ctaMask", true),
              InstructionField("cachePolicy", true)
            ),
            decodeGuard = Some("!modifierNames.contains(\"bulk_group\")")
          ),
          shape.copy(
            caseName = "CpAsyncBulkGroup",
            fields = Vector(
              InstructionField("destinationAddress", false),
              InstructionField("sourceAddress", false),
              InstructionField("size", false),
              InstructionField("cachePolicy", true),
              InstructionField("byteMask", true)
            ),
            decodeGuard = Some("modifierNames.contains(\"bulk_group\")")
          )
        )
      case shape if shape.opcode == "cp.async.bulk.tensor" =>
        Vector(
          shape.copy(
            caseName = "CpAsyncBulkTensorMBarrier",
            fields = Vector(
              InstructionField("destinationAddress", false),
              InstructionField("tensorMapAndCoordinates", false),
              InstructionField("barrierAddress", false),
              InstructionField("im2colInfo", true),
              InstructionField("ctaMask", true),
              InstructionField("cachePolicy", true)
            ),
            decodeGuard = Some("!modifierNames.contains(\"bulk_group\")")
          ),
          shape.copy(
            caseName = "CpAsyncBulkTensorGroup",
            fields = Vector(
              InstructionField("tensorMapAndCoordinates", false),
              InstructionField("sourceAddress", false),
              InstructionField("cachePolicy", true)
            ),
            decodeGuard = Some("modifierNames.contains(\"bulk_group\")")
          )
        )
      case shape => Vector(shape)
    }.map(shape => shape.copy(fields = shape.fields.map(field => field.copy(name = abbreviateFieldName(field.name)))))
    def operandValues(shape: InstructionShape): String = {
      def flush(segments: Vector[String], fixed: Vector[String]): Vector[String] =
        if (fixed.isEmpty) segments else segments :+ fixed.mkString("Vec(", ", ", ")")
      val (segments, fixed) = shape.fields.foldLeft((Vector.empty[String], Vector.empty[String])) {
        case ((segments, fixed), field) =>
          if (field.optional) {
            (flush(segments, fixed) :+ s"${field.name}.toVec", Vector.empty)
          } else (segments, fixed :+ field.name)
      }
      val allSegments = flush(segments, fixed)
      val defaultBody = if (allSegments.isEmpty) "Vec.empty" else allSegments.mkString(" ++ ")
      shape.caseName match {
        case "CpAsyncBulkMBarrier" =>
          "Vec(destAddr, srcAddr, size) ++ ignoredBytesLeft.toVec ++ ignoredBytesRight.toVec ++ Vec(barrierAddr) ++ ctaMask.toVec ++ cachePolicy.toVec"
        case "CpAsyncBulkGroup" =>
          "Vec(destAddr, srcAddr, size) ++ cachePolicy.toVec ++ byteMask.toVec"
        case "CpAsyncBulkTensorMBarrier" =>
          "Vec(destAddr, tensorMapAndCoordinates, barrierAddr) ++ im2colInfo.toVec ++ ctaMask.toVec ++ cachePolicy.toVec"
        case "CpAsyncBulkTensorGroup" =>
          "Vec(tensorMapAndCoordinates, srcAddr) ++ cachePolicy.toVec"
        case "MbarrierTestWait" =>
          "Vec(completion) ++ reportValue.toVec ++ Vec(barrierAddr, stateOrPhase)"
        case "MbarrierTryWait" =>
          "Vec(completion) ++ reportValue.toVec ++ Vec(barrierAddr, stateOrPhase) ++ timeHint.toVec"
        case "Tcgen05Mma" =>
          "Vec(destAddr, srcA, srcBDesc, instDesc) ++ scaleAAddr.toVec ++ scaleBAddr.toVec ++ disabledOutputLanes.toVec ++ Vec(enableInputD) ++ scaleInputD.toVec"
        case "Tcgen05MmaSp" =>
          "Vec(destAddr, srcA, srcBDesc, sparseMetadataAddr, instDesc) ++ scaleAAddr.toVec ++ scaleBAddr.toVec ++ disabledOutputLanes.toVec ++ Vec(enableInputD) ++ scaleInputD.toVec"
        case _ => defaultBody
      }
    }
    val cases = shapes.map { shape =>
      val fields = shape.fields.map { field =>
        s"${field.name}: ${if (field.optional) "Option[Operand]" else "Operand"}"
      }
      val parameters      = (fields ++ Vector("mods: Vec[Modifier]", "pred: Option[Predication]")).mkString(", ")
      val constructorArgs =
        if (shape.fields.isEmpty) ""
        else if (shape.fields.forall(!_.optional)) s", ${shape.fields.map(_.name).mkString(", ")}"
        else s", (${operandValues(shape)})*"
      s"  case ${shape.caseName}($parameters) extends Instruction(Instruction.Definition.${shape.caseName}$constructorArgs)"
    }
    def renderDecoder(shape: InstructionShape): String = {
      val minRequired        = shape.fields.lastIndexWhere(!_.optional) + 1
      val allowed            = allowedOperandCounts(shape)
      val allowedTest        = allowed.map(count => s"operands.size == $count").mkString(" || ")
      val defaultOperandTest =
        if (allowedTest.isEmpty) "false" else s"operands.size >= $minRequired && ($allowedTest)"
      val operandTest = shape.caseName match {
        case "CpAsyncBulkMBarrier" =>
          "if (modifierNames.contains(\"ignore_oob\")) operands.size == 6 || operands.size == 7 else if (modifierNames.contains(\"multicast::cluster\")) operands.size >= 4 && operands.size <= 6 else operands.size == 4 || operands.size == 5"
        case "CpAsyncBulkTensorMBarrier" =>
          "if (modifierNames.exists(_.startsWith(\"im2col\")) && modifierNames.contains(\"multicast::cluster\")) operands.size >= 5 && operands.size <= 6 else if (modifierNames.exists(_.startsWith(\"im2col\")) || modifierNames.contains(\"multicast::cluster\")) operands.size >= 4 && operands.size <= 5 else operands.size == 3 || operands.size == 4"
        case "CpAsyncBulkGroup" | "CpAsyncBulkTensorGroup" | "Call" | "MbarrierTestWait" | "MbarrierTryWait" |
            "Tcgen05Mma" | "Tcgen05MmaSp" =>
          if (allowedTest.isEmpty) "false" else allowedTest
        case _ => defaultOperandTest
      }
      val operationTest     = shape.decodeGuard.getOrElse("true")
      val constructorFields = shape.fields.zipWithIndex.map { case (field, position) =>
        if (field.optional) s"operands.lift($position)" else s"operands($position)"
      }
      val remainingModifiers = "mods"
      val defaultConstructor =
        s"Instruction.${shape.caseName}(${(constructorFields ++ Vector(remainingModifiers, "pred")).mkString(", ")})"
      val constructor = shape.caseName match {
        case "CpAsyncBulkMBarrier" =>
          s"if (modifierNames.contains(\"ignore_oob\")) Instruction.CpAsyncBulkMBarrier(operands(0), operands(1), operands(2), operands(5), Some(operands(3)), Some(operands(4)), None, operands.lift(6), $remainingModifiers, pred) else if (modifierNames.contains(\"multicast::cluster\")) Instruction.CpAsyncBulkMBarrier(operands(0), operands(1), operands(2), operands(3), None, None, operands.lift(4), operands.lift(5), $remainingModifiers, pred) else Instruction.CpAsyncBulkMBarrier(operands(0), operands(1), operands(2), operands(3), None, None, None, operands.lift(4), $remainingModifiers, pred)"
        case "CpAsyncBulkGroup" =>
          s"if (operands.size == 3) Instruction.CpAsyncBulkGroup(operands(0), operands(1), operands(2), None, None, $remainingModifiers, pred) else if (operands.size == 4 && modifierNames.contains(\"cp_mask\")) Instruction.CpAsyncBulkGroup(operands(0), operands(1), operands(2), None, Some(operands(3)), $remainingModifiers, pred) else Instruction.CpAsyncBulkGroup(operands(0), operands(1), operands(2), Some(operands(3)), operands.lift(4), $remainingModifiers, pred)"
        case "CpAsyncBulkTensorMBarrier" =>
          s"if (modifierNames.exists(_.startsWith(\"im2col\")) && modifierNames.contains(\"multicast::cluster\")) Instruction.CpAsyncBulkTensorMBarrier(operands(0), operands(1), operands(2), Some(operands(3)), operands.lift(4), operands.lift(5), $remainingModifiers, pred) else if (modifierNames.exists(_.startsWith(\"im2col\"))) Instruction.CpAsyncBulkTensorMBarrier(operands(0), operands(1), operands(2), Some(operands(3)), None, operands.lift(4), $remainingModifiers, pred) else if (modifierNames.contains(\"multicast::cluster\")) Instruction.CpAsyncBulkTensorMBarrier(operands(0), operands(1), operands(2), None, operands.lift(3), operands.lift(4), $remainingModifiers, pred) else Instruction.CpAsyncBulkTensorMBarrier(operands(0), operands(1), operands(2), None, None, operands.lift(3), $remainingModifiers, pred)"
        case "CpAsyncBulkTensorGroup" =>
          s"Instruction.CpAsyncBulkTensorGroup(operands(0), operands(1), operands.lift(2), $remainingModifiers, pred)"
        case "Call" =>
          s"if (operands.size >= 2 && Operand.isParenthesized(operands(0))) Instruction.Call(Some(operands(0)), operands(1), operands.lift(2), operands.lift(3), $remainingModifiers, pred) else Instruction.Call(None, operands(0), operands.lift(1), operands.lift(2), $remainingModifiers, pred)"
        case "MbarrierTestWait" =>
          s"if (operands.size == 3) Instruction.MbarrierTestWait(operands(0), None, operands(1), operands(2), $remainingModifiers, pred) else Instruction.MbarrierTestWait(operands(0), Some(operands(1)), operands(2), operands(3), $remainingModifiers, pred)"
        case "MbarrierTryWait" =>
          s"if (operands.size == 3) Instruction.MbarrierTryWait(operands(0), None, operands(1), operands(2), None, $remainingModifiers, pred) else if (operands.size == 4 && !Operand.isWithPredicate(operands(0))) Instruction.MbarrierTryWait(operands(0), None, operands(1), operands(2), Some(operands(3)), $remainingModifiers, pred) else Instruction.MbarrierTryWait(operands(0), Some(operands(1)), operands(2), operands(3), operands.lift(4), $remainingModifiers, pred)"
        case "Tcgen05Mma" =>
          s"if (operands.size == 5) Instruction.Tcgen05Mma(operands(0), operands(1), operands(2), operands(3), None, None, None, operands(4), None, $remainingModifiers, pred) else if (Operand.isAddress(operands(4))) Instruction.Tcgen05Mma(operands(0), operands(1), operands(2), operands(3), Some(operands(4)), Some(operands(5)), None, operands(6), None, $remainingModifiers, pred) else Instruction.Tcgen05Mma(operands(0), operands(1), operands(2), operands(3), None, None, Some(operands(4)), operands(5), operands.lift(6), $remainingModifiers, pred)"
        case "Tcgen05MmaSp" =>
          s"if (operands.size == 6) Instruction.Tcgen05MmaSp(operands(0), operands(1), operands(2), operands(3), operands(4), None, None, None, operands(5), None, $remainingModifiers, pred) else if (Operand.isAddress(operands(5))) Instruction.Tcgen05MmaSp(operands(0), operands(1), operands(2), operands(3), operands(4), Some(operands(5)), Some(operands(6)), None, operands(7), None, $remainingModifiers, pred) else Instruction.Tcgen05MmaSp(operands(0), operands(1), operands(2), operands(3), operands(4), None, None, Some(operands(5)), operands(6), operands.lift(7), $remainingModifiers, pred)"
        case _ => defaultConstructor
      }
      val invalidOperands =
        s"Left(Instruction.DecodeError.InvalidOperandCount(${Emission.literal(shape.opcode)}, Vec(${allowed.mkString(", ")}), operands.size))"
      val decodeBody =
        if (operationTest == "true")
          s"if ($operandTest) Right($constructor) else $invalidOperands"
        else
          s"if ($operationTest && ($operandTest)) Right($constructor) else if ($operationTest) $invalidOperands else Left(Instruction.DecodeError.UnknownOpcode(((${Emission.literal(shape.opcode)}) +: modifierNames).mkString(\".\")))"
      val modifierNames =
        Option.when(s"$operationTest $operandTest $constructor".contains("modifierNames"))(
          "      val modifierNames = mods.map(_.name)\n"
        ).getOrElse("")
      s"""|(
          |      operands: Vec[Operand],
          |      mods: Vec[Modifier],
          |      pred: Option[Predication]
          |    ) => {
          |$modifierNames      $decodeBody
          |    }""".stripMargin
    }
    val metadataHelpers                                 = Vector.newBuilder[String]
    def renderMetadata(shape: InstructionShape): String = {
      val entries = sections.zipWithIndex.flatMap { (section, sectionIndex) =>
        val renderedForms = section.forms.view.flatMap(form => operationsFor(section, form)).flatMap {
          case (opcode, form) if opcode == shape.opcode                => renderForms(form, shape.operationModifiers)
          case (opcode, form) if spelledUnderBase(opcode, form, shape) => renderForms(form, Vector.empty)
          case _                                                       => Vector.empty
        }.toVector.distinct
        // Large tensor tables exceed JVM method limits when initialised inline.
        val forms = if (renderedForms.mkString.length < 4000) renderedForms
        else renderedForms.zipWithIndex.map { (form, index) =>
          val name = s"metadata${shape.caseName}_${sectionIndex}_$index"
          metadataHelpers += s"  private def $name(): InstructionForm = $form"
          s"$name()"
        }
        val modifierTargets = Emission.collection(
          "Map",
          section.modifierTargets.toVector.map((modifiers, target) =>
            Emission.collection(
              "_root_.scala.collection.immutable.Set",
              modifiers.toVector.sorted.map(modifier => s"Modifier.${scalaCaseName(modifier)}")
            ) + s" -> Target($target)"
          ).sorted
        )
        Option.when(forms.nonEmpty) {
          s"""|Metadata.Entry(
              |          Version(${section.introduced.major}, ${section.introduced.minor}),
              |          Target(${section.minimumTarget}),
              |          $modifierTargets,
              |          Vec(${forms.mkString(", ")})
              |        )""".stripMargin
        }
      }
      if (entries.isEmpty) throw IllegalStateException(s"missing PTX metadata for ${shape.opcode}")
      s"""|() => Metadata(Vec.nonEmpty(
          |        ${entries.mkString(",\n")}
          |      ))""".stripMargin
    }
    val definitions = shapes.map { shape =>
      s"""|    case ${shape.caseName} extends Definition(
          |    ${Emission.literal(shape.opcode)},
          |    Opcode.${scalaCaseName(shape.opcode.takeWhile(_ != '.'))},
          |    Vec(${shape.opcode.split('.').drop(1).map(value => s"Modifier.${scalaCaseName(value)}").mkString(", ")}),
          |    ${renderDecoder(shape)},
          |    ${renderMetadata(shape)}
          |  )""".stripMargin
    }
    GeneratedHeader.scala(
      s"PTX instruction model from the pinned NVIDIA ISA ${version.source} specification"
    ) +
      s"""|package net.kurobako.gemstone.isa.ptx
        |
        |import Metadata.*
        |
        |import net.kurobako.gemstone.string.*
        |import net.kurobako.gemstone.constraint.*
        |import net.kurobako.gemstone.isa.*
        |import net.kurobako.gemstone.collection.*
        |import net.kurobako.gemstone.collection.Vec.*
        |
        |${GeneratedHeader.annotation()}object Instruction {
        |  enum DecodeError {
        |    case UnknownOpcode(opcode: String)
        |    case InvalidOperandCount(opcode: String, expected: Vec[Int], actual: Int)
        |    case AmbiguousInstruction(opcode: String, definitions: Vec[String])
        |  }
        |
        |  type Decoder = (
        |      operands: Vec[Operand],
        |      mods: Vec[Modifier],
        |      pred: Option[Predication]
        |  ) => Either[DecodeError, Instruction]
        |
        |${metadataHelpers.result().mkString("\n")}
        |
        |  enum Definition(val opcode: NonEmpty[String], val mnemonic: Opcode, val fixedModifiers: Vec[Modifier], val decoder: Decoder, metadata: () => Metadata) {
        |${definitions.mkString("\n")}
        |
        |  lazy val meta: Metadata = metadata()
        |
        |  }
        |
        |  val definitions: Vec[Definition] = Definition.values.toVec
        |
        |
        |  private lazy val DefinitionsByMnemonic: Map[String, Vec[Definition]] =
        |    definitions.groupMap(_.opcode.takeWhile(_ != '.'))(value => value)
        |
        |  def decode(
        |      opcode: String,
        |      operands: Vec[Operand],
        |      mods: Vec[Modifier] = Vec.empty,
        |      pred: Option[Predication] = None
        |  ): Either[DecodeError, Instruction] = {
        |    val opcodeParts = Vec.from(opcode.toLowerCaseRoot.split('.')).flatMap(_.refine[NonEmpty[String]].toOption)
        |    val name = opcodeParts.headOption.getOrElse("")
        |    val allModifiers = opcodeParts.drop(1).map(value => Modifier(value)) ++ mods
        |    decodeParsed(name, operands, allModifiers, pred)
        |  }
        |
        |  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
        |  def decodeParsed(
        |      mnemonic: String,
        |      operands: Vec[Operand],
        |      mods: Vec[Modifier],
        |      pred: Option[Predication]
        |  ): Either[DecodeError, Instruction] = {
        |    val name = mnemonic.toLowerCaseRoot
        |    val allModifiers = mods
        |    val definitions = DefinitionsByMnemonic.getOrElse(name, Vec.empty[Definition])
        |    var specificity = -1
        |    var index = 0
        |    while (index < definitions.size) {
        |      val definition = definitions(index)
        |      if (definition.fixedModifiers.size > specificity && fixedModifiersMatch(allModifiers, definition.fixedModifiers))
        |        specificity = definition.fixedModifiers.size
        |      index += 1
        |    }
        |
        |    var successful: Option[(Definition, Instruction)] = None
        |    var ambiguous = Vec.empty[String]
        |    var expected = Vec.empty[Int]
        |    index = 0
        |    while (index < definitions.size) {
        |      val definition = definitions(index)
        |      if (definition.fixedModifiers.size == specificity && fixedModifiersMatch(allModifiers, definition.fixedModifiers))
        |        definition.decoder(operands, allModifiers.drop(specificity), pred) match {
        |          case Right(instruction) => successful match {
        |              case None => successful = Some(definition -> instruction)
        |              case Some((first, _)) =>
        |                if (ambiguous.isEmpty) ambiguous = Vec(first.toString)
        |                ambiguous = ambiguous :+ definition.toString
        |            }
        |          case Left(DecodeError.InvalidOperandCount(_, counts, _)) => expected = expected ++ counts
        |          case Left(_) => ()
        |        }
        |      index += 1
        |    }
        |
        |    if (ambiguous.nonEmpty)
        |      Left(DecodeError.AmbiguousInstruction(qualifiedOpcode(name, allModifiers), ambiguous))
        |    else successful match {
        |      case Some((_, instruction)) => Right(instruction)
        |      case None if expected.nonEmpty =>
        |        Left(DecodeError.InvalidOperandCount(qualifiedOpcode(name, allModifiers), expected.distinct.sorted, operands.size))
        |      case None => Left(DecodeError.UnknownOpcode(qualifiedOpcode(name, allModifiers)))
        |    }
        |  }
        |
        |  private def fixedModifiersMatch(modifiers: Vec[Modifier], fixed: Vec[Modifier]): Boolean =
        |    fixed.size <= modifiers.size && fixed.indices.forall(index => modifiers(index) == fixed(index))
        |
        |  private def qualifiedOpcode(name: String, modifiers: Vec[Modifier]): String =
        |    (name +: modifiers.map(_.name)).mkString(".")
        |}
        |
        |${GeneratedHeader.annotation()}enum Instruction(val definition: Instruction.Definition, operandValues: Operand*) {
        |${cases.mkString("\n")}
        |
        |  val operands: Vec[Operand] = operandValues.toVec
        |
        |  final def opcode: NonEmpty[String] = definition.opcode
        |
        |  final def fixedModifiers: Vec[Modifier] = definition.fixedModifiers
        |
        |  final def decoder: Instruction.Decoder = definition.decoder
        |
        |  final def meta: Metadata = definition.meta
        |
        |  def mods: Vec[Modifier]
        |
        |  def pred: Option[Predication]
        |
        |  final def qualifiedOpcode: NonEmpty[String] =
        |    if (mods.isEmpty) opcode else s"$$opcode.$${mods.map(_.name).mkString(".")}"
        |  lazy val signature: InstructionSignature = InstructionSignature(definition.mnemonic, definition.fixedModifiers ++ mods)
        |
        |}
        |
        |""".stripMargin
  }

  private def instructionShapes(sections: Vector[Section]): Vector[InstructionShape] = {
    val formsByOpcode = sections.flatMap(section => section.forms.flatMap(form => operationsFor(section, form)))
      .groupMap(_._1)(_._2)
    formsByOpcode.toVector.sortBy(_._1).map { case (opcode, forms) =>
      val parts          = opcode.split('.').toVector
      val mnemonic       = parts.head
      val maxArity       = forms.map(_.operands.size).maxOption.getOrElse(0)
      val inferredFields = Vector.tabulate(maxArity) { position =>
        val present = forms.flatMap(_.operands.lift(position))
        InstructionField(
          preferredFieldName(opcode, position, present.map(_.sourceName)),
          present.exists(_.optional) || present.size < forms.size ||
            forms.exists(form => form.operands.count(!_.optional) <= position)
        )
      }
      val rawFields = ShapeFieldOverrides.getOrElse(opcode, inferredFields)
      val fields    = rawFields.foldLeft((Map.empty[String, Int], Vector.empty[InstructionField])) {
        case ((seen, fields), field) =>
          val count = seen.getOrElse(field.name, 0) + 1
          (
            seen.updated(field.name, count),
            fields :+ field.copy(name = if (count == 1) field.name else s"${field.name}$count")
          )
      }._2
      InstructionShape(opcode, mnemonic, parts.tail, scalaCaseName(opcode), fields, forms)
    }
  }

  private def allowedOperandCounts(shape: InstructionShape): Vector[Int] = {
    val counts = shape.forms.flatMap { form =>
      val required = form.operands.count(!_.optional)
      required.to(form.operands.size)
    }.distinct.sorted
    shape.caseName match {
      case "CpAsyncBulkMBarrier"       => Vector(4, 5, 6, 7)
      case "CpAsyncBulkGroup"          => Vector(3, 4, 5)
      case "CpAsyncBulkTensorMBarrier" => Vector(3, 4, 5, 6)
      case "CpAsyncBulkTensorGroup"    => Vector(2, 3)
      case _ => AllowedOperandCountOverrides.getOrElse(shape.opcode, if (counts.isEmpty) Vector(0) else counts)
    }
  }

  private def sectionOperations(section: Section): Vector[String] = {
    val mnemonics = section.mnemonics.toSet
    val dotted    = TitleOpcode.findAllIn(section.title).map(_.toLowerCaseRoot)
      .filter(value => mnemonics(value.takeWhile(_ != '.'))).toVector
    if (dotted.nonEmpty) dotted.distinct else section.mnemonics
  }

  /** `mad{.hi,.lo}.cc.type` places optional modifiers before `.cc`, so the spelling decodes under the plain `mad`. */
  private def spelledUnderBase(opcode: String, form: Form, base: InstructionShape): Boolean =
    form.leadingOptional && opcode.startsWith(s"${base.opcode}.") &&
      form.requiredModifierGroups.headOption.contains(Set(opcode.drop(base.opcode.length + 1)))

  private def operationsFor(section: Section, form: Form): Vector[(String, Form)] = {
    val candidates = sectionOperations(section).filter(_.takeWhile(_ != '.') == form.mnemonic)
    val groups     = form.requiredModifierGroups
    val direct     = candidates.sortBy(value => -value.count(_ == '.')).find { opcode =>
      val suffix = opcode.split('.').toVector.tail
      suffix.nonEmpty && groups.take(suffix.size).map(_.toVector) == suffix.map(Vector(_))
    }
    direct match {
      case Some(opcode) => Vector(opcode -> form)
      case None         =>
        val titleSuffixes = candidates.flatMap(_.split('.').toVector.drop(1).headOption).distinct
        val namespaced    = groups.headOption.toVector.flatMap(_.toVector.sorted).filter { value =>
          titleSuffixes.exists(prefix => value.startsWith(prefix + "::"))
        }
        if (namespaced.nonEmpty)
          namespaced.map { value =>
            val specialised = form.copy(requiredModifierGroups = Vector(Set(value)) ++ groups.drop(1))
            s"${form.mnemonic}.$value" -> specialised
          }
        else {
          val embedded = candidates.flatMap { candidate =>
            val suffix = candidate.split('.').toVector.tail
            if (suffix.size != 1) Vector.empty
            else groups.zipWithIndex.collectFirst {
              case (values, index) if values == Set(suffix.head) && index > 0 && groups(index - 1).size <= 8 =>
                groups(index - 1).toVector.sorted.map { selector =>
                  val specialisedGroups = groups.updated(index - 1, Set(selector))
                  s"${form.mnemonic}.$selector.${suffix.head}" -> form.copy(requiredModifierGroups = specialisedGroups)
                }
            }.getOrElse(Vector.empty)
          }
          if (embedded.nonEmpty) embedded.distinct
          else Vector(candidates.find(_ == form.mnemonic).getOrElse(form.mnemonic) -> form)
        }
    }
  }

  private val FieldOverrides: Map[String, Vector[String]] = Map(
    "atom"     -> Vector("destination", "address", "value", "compareOrCachePolicy"),
    "bar"      -> Vector("barrier", "threadCount", "predicate", "value"),
    "barrier"  -> Vector("barrier", "threadCount", "predicate", "value"),
    "bfi"      -> Vector("destination", "insertValue", "baseValue", "bitPosition", "bitLength"),
    "bra"      -> Vector("target"),
    "cvt"      -> Vector("destination", "source", "secondarySource", "roundingBits"),
    "cvta"     -> Vector("destination", "source"),
    "cp.async" -> Vector("destinationAddress", "sourceAddress", "copySize", "sourceSizeOrIgnore", "cachePolicy"),
    "cp.async.bulk.wait_group"      -> Vector("pendingGroupCount"),
    "cp.async.bulk.prefetch.tensor" -> Vector("tensorMapAndCoordinates", "im2colInfo", "cachePolicy"),
    "cp.async.wait_group"           -> Vector("pendingGroupCount"),
    "ld"                            -> Vector("destination", "address", "cachePolicy"),
    "mbarrier.arrive"               -> Vector("state", "barrierAddress", "countOrTransactionCount"),
    "mbarrier.arrive_drop"          -> Vector("state", "barrierAddress", "countOrTransactionCount"),
    "mbarrier.check_layout"         -> Vector("predicate", "barrierAddress"),
    "mbarrier.complete_tx"          -> Vector("barrierAddress", "transactionCount"),
    "mbarrier.expect_tx"            -> Vector("barrierAddress", "transactionCount"),
    "mbarrier.init"                 -> Vector("barrierAddress", "count"),
    "mbarrier.inval"                -> Vector("barrierAddress"),
    "mbarrier.pending_count"        -> Vector("destination", "state"),
    "mma"                           -> Vector(
      "destination",
      "sourceA",
      "sourceB",
      "sourceC",
      "scaleAData",
      "scaleASelector",
      "scaleBData",
      "scaleBSelector"
    ),
    "mma.sp::ordered_metadata" -> Vector(
      "destination",
      "sourceA",
      "sourceB",
      "sourceC",
      "e",
      "f",
      "scaleAData",
      "scaleASelector",
      "scaleBData",
      "scaleBSelector"
    ),
    "mov"                           -> Vector("destination", "source"),
    "multimem.cp.async.bulk"        -> Vector("destinationAddress", "sourceAddress", "size", "byteMask"),
    "multimem.cp.reduce.async.bulk" -> Vector("destinationAddress", "sourceAddress", "size"),
    "multimem.ld_reduce"            -> Vector("destination", "address"),
    "multimem.red"                  -> Vector("address", "value"),
    "multimem.red.async"            -> Vector("address", "value"),
    "multimem.st"                   -> Vector("address", "value"),
    "multimem.st.async"             -> Vector("address", "value"),
    "nanosleep"                     -> Vector("nanoseconds"),
    "pmevent"                       -> Vector("event"),
    "red"                           -> Vector("address", "value", "cachePolicy"),
    "set"                           -> Vector("destination", "left", "right", "booleanSource"),
    "setp"                          -> Vector("destination", "left", "right", "booleanSource"),
    "st"                            -> Vector("address", "value", "cachePolicy"),
    "tensormap"                     -> Vector("destination", "sourceOrOrdinal", "valueOrSize"),
    "tcgen05.ld"                    -> Vector("destinationRegisters", "reductionValue", "address", "halfSplitOffset"),
    "tcgen05.alloc"                 -> Vector("destinationAddress", "columnCount"),
    "tcgen05.cp"                    -> Vector("destinationAddress", "sourceDescriptor"),
    "tcgen05.dealloc"               -> Vector("tensorMemoryAddress", "columnCount"),
    "tcgen05.shift"                 -> Vector("tensorMemoryAddress"),
    "tcgen05.st"                    -> Vector("address", "halfSplitOffset", "sourceRegisters"),
    "tcgen05.mma.ws"                -> Vector(
      "destinationAddress",
      "sourceA",
      "sourceBDescriptor",
      "instructionDescriptor",
      "enableInputD",
      "zeroColumnMaskDescriptor"
    ),
    "tcgen05.mma.ws.sp" -> Vector(
      "destinationAddress",
      "sourceA",
      "sourceBDescriptor",
      "sparseMetadataAddress",
      "instructionDescriptor",
      "enableInputD",
      "zeroColumnMaskDescriptor"
    ),
    "tex" -> Vector(
      "destination",
      "textureAndCoordinates",
      "lodOffsetOrDerivativeX",
      "depthCompareOrDerivativeY",
      "offset",
      "depthCompare"
    ),
    "tld4" -> Vector("destination", "textureAndCoordinates", "offset", "depthCompare"),
    "txq"  -> Vector("destination", "texture", "lod")
  )

  private val ShapeFieldOverrides: Map[String, Vector[InstructionField]] = Map(
    "call" -> Vector(
      InstructionField("returnParameters", optional = true),
      InstructionField("function", optional = false),
      InstructionField("parameters", optional = true),
      InstructionField("targets", optional = true)
    ),
    "mbarrier.test_wait" -> Vector(
      InstructionField("completion", optional = false),
      InstructionField("reportValue", optional = true),
      InstructionField("barrierAddress", optional = false),
      InstructionField("stateOrPhase", optional = false)
    ),
    "mbarrier.try_wait" -> Vector(
      InstructionField("completion", optional = false),
      InstructionField("reportValue", optional = true),
      InstructionField("barrierAddress", optional = false),
      InstructionField("stateOrPhase", optional = false),
      InstructionField("timeHint", optional = true)
    ),
    "tcgen05.mma" -> Vector(
      InstructionField("destinationAddress", optional = false),
      InstructionField("sourceA", optional = false),
      InstructionField("sourceBDescriptor", optional = false),
      InstructionField("instructionDescriptor", optional = false),
      InstructionField("scaleAAddress", optional = true),
      InstructionField("scaleBAddress", optional = true),
      InstructionField("disabledOutputLanes", optional = true),
      InstructionField("enableInputD", optional = false),
      InstructionField("scaleInputD", optional = true)
    ),
    "tcgen05.mma.sp" -> Vector(
      InstructionField("destinationAddress", optional = false),
      InstructionField("sourceA", optional = false),
      InstructionField("sourceBDescriptor", optional = false),
      InstructionField("sparseMetadataAddress", optional = false),
      InstructionField("instructionDescriptor", optional = false),
      InstructionField("scaleAAddress", optional = true),
      InstructionField("scaleBAddress", optional = true),
      InstructionField("disabledOutputLanes", optional = true),
      InstructionField("enableInputD", optional = false),
      InstructionField("scaleInputD", optional = true)
    )
  )

  private val AllowedOperandCountOverrides: Map[String, Vector[Int]] = Map(
    "tcgen05.mma"    -> Vector(5, 6, 7),
    "tcgen05.mma.sp" -> Vector(6, 7, 8)
  )

  private def preferredFieldName(opcode: String, position: Int, sourceNames: Vector[String]): String =
    FieldOverrides.get(
      opcode
    ).orElse(FieldOverrides.get(opcode.takeWhile(_ != '.'))).flatMap(_.lift(position)).getOrElse {
      val candidates = sourceNames.map(sourceFieldName(_)).filter(_.nonEmpty)
      val selected   = candidates.groupBy(value => value).toVector
        .sortBy { case (name, values) => (-values.size, fieldNamePenalty(name), name) }
        .headOption.map(_._1)
        .getOrElse(s"operand${position + 1}")
      val identifier = if (selected.headOption.exists(_.isDigit)) s"operand$selected" else selected
      val safe       = if (ScalaKeywords(identifier)) identifier + "Value" else identifier
      if (safe == "operand" || safe == "value") s"$safe${position + 1}" else safe
    }

  private def fieldNamePenalty(name: String): Int =
    if (name.startsWith("operand") || name.startsWith("source")) 1 else 0

  private def sourceFieldName(source: String): String = {
    val value = Text.stripLeadingSignGroup(source.trim)
    if (value.startsWith("[")) {
      val normalised = value.toLowerCaseRoot
      if (normalised.contains("dst")) "destinationAddress"
      else if (normalised.contains("src")) "sourceAddress"
      else if (normalised.contains("mbar") || normalised.contains("bar")) "barrierAddress"
      else "address"
    } else if (value.startsWith("{")) "values"
    else {
      val token = value.dropWhile(character => "!+-_(".contains(character))
        .takeWhile(character => character.isLetterOrDigit || character == '_' || character == '-')
      val normalised = token match {
        case "d" | "dst" | "result"          => "destination"
        case "a"                             => "sourceA"
        case "b"                             => "sourceB"
        case "c"                             => "sourceC"
        case "p" | "pred"                    => "predicate"
        case "q"                             => "predicateQ"
        case "fptr" | "func"                 => "function"
        case "param-list"                    => "parameters"
        case "flist"                         => "functionList"
        case "fproto"                        => "prototype"
        case "ptr"                           => "pointer"
        case "immAlign"                      => "alignment"
        case "membermask" | "imm-membermask" => "memberMask"
        case other                           => lowerCamel(other)
      }
      normalised.filter(character => character.isLetterOrDigit || character == '_')
    }
  }

  private def lowerCamel(value: String): String = {
    val words = Text.identifierWords(value)
    words.headOption.fold("") { head =>
      head.head.toLower.toString + head.tail + words.tail.map(word => s"${word.head.toUpper}${word.tail}").mkString
    }
  }

  private def abbreviateFieldName(value: String): String = {
    val exact = Map(
      "destination"        -> "dest",
      "source"             -> "src",
      "sourceA"            -> "srcA",
      "sourceB"            -> "srcB",
      "sourceC"            -> "srcC",
      "secondarySource"    -> "secondarySrc",
      "returnParameters"   -> "returnParams",
      "parameters"         -> "params",
      "sourceOrOrdinal"    -> "srcOrOrdinal",
      "sourceSizeOrIgnore" -> "srcSizeOrIgnore"
    )
    exact.getOrElse(
      value,
      value
        .replace("destination", "dest")
        .replace("Destination", "Dest")
        .replace("source", "src")
        .replace("Source", "Src")
        .replace("Address", "Addr")
        .replace("Descriptor", "Desc")
        .replace("Registers", "Regs")
        .replace("instruction", "inst")
        .replace("Instruction", "Inst")
    )
  }

  private val CaseNameOverrides: Map[String, String] = Map(
    "applypriority"                     -> "ApplyPriority",
    "clusterlaunchcontrol.query_cancel" -> "ClusterLaunchControlQueryCancel",
    "clusterlaunchcontrol.try_cancel"   -> "ClusterLaunchControlTryCancel",
    "copysign"                          -> "CopySign",
    "createpolicy"                      -> "CreatePolicy",
    "getctarank"                        -> "GetCtaRank",
    "griddepcontrol"                    -> "GridDependencyControl",
    "isspacep"                          -> "IsSpacePredicate",
    "istypep"                           -> "IsTypePredicate",
    "nanosleep"                         -> "NanoSleep",
    "pmevent"                           -> "PmEvent",
    "setmaxnreg"                        -> "SetMaxRegisters",
    "stackrestore"                      -> "StackRestore",
    "stacksave"                         -> "StackSave",
    "tensormap.cp_fenceproxy"           -> "TensorMapCopyFenceProxy",
    "tensormap.replace"                 -> "TensorMapReplace",
    "testp"                             -> "TestPredicate"
  )

  private def scalaCaseName(opcode: String): String =
    CaseNameOverrides.getOrElse(opcode, Identifiers.upperCamel(opcode, "Opcode", "N"))

  private def renderTests(
      version: SpecificationVersion,
      examples: Vector[(String, String)]
  ): String = {
    val values = examples.map { case (id, source) =>
      s"    ${Emission.literal(id)} -> ${Emission.literal(source)}"
    }.mkString(",\n")
    s"""|package net.kurobako.gemstone.isa.ptx
        |
        |import net.kurobako.gemstone.collection.*
        |import net.kurobako.gemstone.isa.ptx.asm.*
        |
        |${GeneratedHeader.scala(
         s"from examples in the pinned NVIDIA PTX ISA ${version.source} specification"
       )}${GeneratedHeader.annotation()}class DocumentationSuite extends munit.FunSuite {
        |  private val examples: Vec[(String, String)] = Vec(
        |$values
        |  )
        |
        |  test("all documentation examples parse into the typed syntax tree") {
        |    val failures = examples.flatMap { case (id, source) =>
        |      Parser.parse(source) match {
        |        case Left(error) => Vec(s"$$id: $$source\\n  $$error")
        |        case Right(_) => Vec.empty[String]
        |      }
        |    }
        |    assertEquals(failures, Seq.empty[String])
        |  }
        |
        |  test("all documentation examples round-trip through the canonical printer") {
        |    val failures = examples.flatMap { case (id, source) =>
        |      Parser.parse(source) match {
        |        case Left(error) => Vec(s"$$id: initial parse failed: $$error")
        |        case Right(first) =>
        |          val printed = Printer.render(first)
        |          Parser.parse(printed) match {
        |            case Right(second) if Printer.render(second) == printed => Vec.empty[String]
        |            case Right(second) => Vec(s"$$id: $$source\\n  printed: $$printed\\n  first: $$first\\n  second: $$second")
        |            case Left(error) => Vec(s"$$id: $$source\\n  printed: $$printed\\n  reparse: $$error")
        |          }
        |      }
        |    }
        |    assertEquals(failures, Seq.empty[String])
        |  }
        |}
        |""".stripMargin
  }

  /** One rendered form per subset of the optional operands. */
  private def renderForms(form: Form, operationModifiers: Vector[String]): Vector[String] = {
    val operands = form.operands.foldLeft(Vector(Vector.empty[String])) { (prefixes, operand) =>
      val constraint = operand.constraint match {
        case "Register" => "OperandConstraint.Register"
        case "Address"  => "OperandConstraint.Address"
        case "Vector"   => "OperandConstraint.VectorOperand"
        case "Symbol"   => "OperandConstraint.Symbol"
        case "Value"    => "OperandConstraint.Value"
        case _          => "OperandConstraint.Unknown"
      }
      prefixes.map(_ :+ constraint) ++ (if (operand.optional) prefixes else Vector.empty)
    }.distinct
    val requiredModifiers = operationModifiers.foldLeft(form.requiredModifierGroups) { (groups, modifier) =>
      val index = groups.indexWhere(_ == Set(modifier))
      if (index < 0) groups else groups.patch(index, Nil, 1)
    }
    val required = requiredModifiers.map(values =>
      values.toVector.sorted.map(value => s"Modifier.${scalaCaseName(value)}")
        .mkString(
          "ModifierGroup(",
          ", ",
          ")"
        )
    ).mkString("Vec(", ", ", ")")
    val optional = Emission.collection(
      "_root_.scala.collection.immutable.Set",
      form.optionalModifiers.toVector.sorted.map(value => s"Modifier.${scalaCaseName(value)}")
    )
    val unordered = Emission.collection(
      "_root_.scala.collection.immutable.Set",
      form.unorderedModifiers.toVector.sorted.map(value => s"Modifier.${scalaCaseName(value)}")
    )
    operands.map(values =>
      s"InstructionForm(${values.mkString("Vec(", ", ", ")")}, $required, $optional, $unordered, ${form.complete})"
    )
  }

  private def extractMnemonic(value: String): Option[String] =
    Mnemonic.findFirstMatchIn(Predication.replaceFirstIn(
      Text.stripLineComment(value),
      ""
    )).map(_.group(1).toLowerCaseRoot)
  private def cleanHeading(value: String): String = Text.normaliseWhitespace(value.replace("\uf0c1", ""))

  private def parseSpecificationVersion(value: String): SpecificationVersion = value match {
    case s"$major.$minor" if major.nonEmpty && minor.nonEmpty && (major + minor).forall(_.isDigit) =>
      SpecificationVersion(value.refine[NonEmpty[String]].fold(
        message => throw IllegalArgumentException(message),
        identity
      ))
    case _ => throw IllegalArgumentException(s"invalid PTX version: $value")
  }

  private def write(output: Path, content: String): Unit = {
    val _ = GeneratedFiles.write(output, GeneratedScalaFormatter(output, content))
  }
}
