package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.Vec
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import scala.annotation.tailrec
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

/** Generates the typed instruction model and parser corpus from the pinned NVIDIA PTX HTML document. */
object GeneratePTXISA {
  private case class SpecificationVersion(source: NonEmpty[String])
  private case class Request(
      tests: Boolean,
      input: Path,
      sourceRoot: Path,
      version: String,
      resourceRoot: Option[Path]
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

  /** One `.name` position of an opcode spelling and the values it takes; slots compare by their values alone. */
  private case class Slot(values: Set[String])(val name: String)
  private case class Form(
      mnemonic: String,
      operands: Vector[FormOperand],
      requiredSlots: Vector[Slot],
      optionalSlots: Vector[Slot],
      unorderedModifiers: Set[String],
      leadingOptional: Boolean,
      complete: Boolean
  ) {
    def requiredModifierGroups: Vector[Set[String]] = requiredSlots.map(_.values)
    def optionalModifiers: Set[String]              = optionalSlots.flatMap(_.values).toSet
  }
  private case class FormOperand(sourceName: String, constraint: String, optional: Boolean, complete: Boolean)
  private case class InstructionField(name: String, optional: Boolean)
  private case class InstructionShape(
      opcode: String,
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
  // opcodes are lower case, so captions inside syntax blocks such as "FP8 floating point type" begin no form
  private val Mnemonic: Regex      = raw"^\s*([a-z_][a-z0-9_]*)(?=\.|\{|\s|;|$$)".r
  private val Predication: Regex   = raw"^\s*@[!]?[A-Za-z_$$][A-Za-z0-9_$$]*\s+".r
  private val Elision: Regex       = raw"\.{3,}".r
  private val ExamplePrefix: Regex = raw"^Example \d+:\s*".r
  private val VersionNote: Regex   = raw"Introduced in PTX ISA version ([0-9]+)\.([0-9]+)".r
  private val TargetNote: Regex    = raw"sm_([0-9]+)[af]?\s+or\s+(?:higher|later)".r
  private val Definition: Regex    = raw"(?:\.|::)([A-Za-z][A-Za-z0-9_]*(?:::[A-Za-z0-9_]+)*)\s*=\s*\{".r
  private val NoteClause: Regex    = raw"(?<=(?:higher|later)\.?)\s+|(?<=\.)\s+(?=[A-Z])".r
  // "when destination state space is .shared::cluster" conditions another qualifier's requirement, not its own.
  private val StateSpaceCondition: Regex = raw"when (?:the )?(?:destination|source) state space is \.[A-Za-z:_]+".r
  private val Spelling: Regex            =
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

  private val SpellingSeparators = Set('/', ',', '|')
  private val ListSeparators     = Set(',', '|')

  private val BulkMBarrierFields = Vector(
    InstructionField("destinationAddress", optional = false),
    InstructionField("sourceAddress", optional = false),
    InstructionField("size", optional = false),
    InstructionField("barrierAddress", optional = false),
    InstructionField("ignoredBytesLeft", optional = true),
    InstructionField("ignoredBytesRight", optional = true),
    InstructionField("ctaMask", optional = true),
    InstructionField("cachePolicy", optional = true)
  )
  private val BulkGroupFields = Vector(
    InstructionField("destinationAddress", optional = false),
    InstructionField("sourceAddress", optional = false),
    InstructionField("size", optional = false),
    InstructionField("cachePolicy", optional = true),
    InstructionField("byteMask", optional = true)
  )
  private val TensorMBarrierFields = Vector(
    InstructionField("destinationAddress", optional = false),
    InstructionField("tensorMapAndCoordinates", optional = false),
    InstructionField("barrierAddress", optional = false),
    InstructionField("im2colInfo", optional = true),
    InstructionField("ctaMask", optional = true),
    InstructionField("cachePolicy", optional = true)
  )
  private val TensorGroupFields = Vector(
    InstructionField("tensorMapAndCoordinates", optional = false),
    InstructionField("sourceAddress", optional = false),
    InstructionField("cachePolicy", optional = true)
  )
  private val AbbreviatedFields = Map(
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

  /** Qualifier domains whose spelling order is not significant; toolchains emit `ldmatrix.sync.aligned.x4.m8n8`. */
  private val UnorderedQualifiers: Map[String, Set[String]] = Map("ldmatrix" -> Set("shape", "num", "trans"))

  private def request(tests: Boolean): Opts[Request] = (
    CodegenCommand.path("ptx-html"),
    CodegenCommand.path("generated-source-root"),
    Opts.argument[String]("ptx-version"),
    CodegenCommand.path("generated-resource-root").orNone
  ).mapN((input, sourceRoot, version, resourceRoot) => Request(tests, input, sourceRoot, version, resourceRoot))

  private val command = Command("GeneratePTXISA", "Generate PTX ISA sources")(
    Opts.subcommands(
      Command("instructions", "Generate instruction sources")(request(tests = false)),
      Command("tests", "Generate reference tests")(request(tests = true))
    )
  )

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) { request =>
    val specificationVersion = parseSpecificationVersion(request.version)
    val document             = parse(request.input)
    val sections             = checkedSections(extract(document))
    val outputRoot           = request.sourceRoot.resolve(PackageRoot)
    val sources              =
      if (request.tests)
        Vector("DocumentationSuite.scala" -> renderTests(specificationVersion, checkedExamples(sections)))
      else
        renderInstructions(specificationVersion, sections) ++
          renderDomains(sections).map { case (name, source) => s"$name.scala" -> source }
    val written = sources.map { case (name, source) =>
      val path = outputRoot.resolve(name)
      write(path, source)
      path
    }
    val include = (path: Path) => path.getFileName.toString.endsWith(".scala")
    GeneratedFiles.removeUnexpected(outputRoot, written.toSet, include)
    if (!request.tests) request.resourceRoot.foreach(writeDomains(_, document, sections))
  }

  private val ModifierMembers =
    """|
       |  lazy val PackedHalfTypes: Set[Modifier] = Set(F16x2, Bf16x2)
       |
       |  extension (modifier: Modifier) {
       |    def isInteger32: Boolean  = PartialFunction.cond(modifier) { case U32 | S32 => true }
       |    def isPackedHalf: Boolean = PackedHalfTypes(modifier)
       |    def is64Bit: Boolean      = PartialFunction.cond(modifier) { case B64 | U64 | S64 | F64 => true }
       |  }
       |""".stripMargin

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
    def render(name: String, values: Vector[String], fallback: String, extra: String, members: String): String = {
      require(values.map(scalaCaseName(_)).distinct.size == values.size, s"ambiguous PTX $name names")
      val cases =
        values.map(value => s"  case ${scalaCaseName(value)} extends $name(${Emission.literal(value)})").mkString("\n")
      val entries = values.map(value => s"${Emission.literal(value)} -> ${scalaCaseName(value)}").mkString(", ")
      GeneratedHeader.scala("from the pinned PTX syntax domains") +
        s"""|package net.kurobako.gemstone.isa.ptx
            |import net.kurobako.gemstone.constraint.*
            |
            |object $name {
            |  private lazy val byName: Map[String, $name] = Map($entries)
            |  def apply(name: NonEmpty[String]): $name = byName.getOrElse(name.raw, $fallback)
            |  def unapply(value: $name): Some[NonEmpty[String]] = Some(value.name)
            |$members}
            |
            |${GeneratedHeader.annotation()}enum $name(val name: NonEmpty[String]) derives CanEqual {
            |$cases
            |$extra
            |}
            |""".stripMargin
    }
    Vector(
      "Opcode" -> render(
        "Opcode",
        opcodes,
        fallback = "throw IllegalArgumentException(s\"unknown PTX opcode $name\")",
        extra = "",
        members = ""
      ),
      "Modifier" -> render(
        "Modifier",
        modifiers,
        fallback = "Unrecognised(name)",
        extra = "  case Unrecognised(spelling: NonEmpty[String]) extends Modifier(spelling)",
        members = ModifierMembers
      ),
      "ModifierSlot" -> renderSlots(sections)
    )
  }

  private def writeDomains(resourceRoot: Path, document: Document, sections: Vector[Section]): Unit = {
    val tables  = PTXReferenceTables.read(document)
    val rules   = PTXOperandRules.All
    val derived =
      PTXOperandDomains.derive(domainDefinitions(sections), tables, rules, PTXOperandRules.verify(rules, document))
    val output = GeneratePTXDomains.render(derived)
    val root   = resourceRoot.resolve(PackageRoot)
    Vector(
      "modifier-domains.json" -> output.modifiers,
      "operand-domains.json"  -> output.operands,
      "domains-report.md"     -> output.report
    ).foreach { (name, content) =>
      val _ = GeneratedFiles.write(root.resolve(name), content)
    }
    GeneratePTXDomains.check(derived)
  }

  /** The modifier slots and operands of every instruction definition, as the generated metadata groups them. */
  private def domainDefinitions(sections: Vector[Section]): Vector[PTXOperandDomains.Definition] =
    definitionShapes(sections).map { shape =>
      def slots(values: Vector[Slot]): Vector[PTXOperandDomains.Slot] =
        values.map(slot => PTXOperandDomains.Slot(slot.name, slot.values.toVector.sorted))
      val matched = sections.flatMap(section =>
        section.forms.flatMap(form =>
          operationsFor(section, form).collect {
            case (opcode, found) if opcode == shape.opcode                 => (found, shape.operationModifiers)
            case (opcode, found) if spelledUnderBase(opcode, found, shape) => (found, Vector.empty[String])
          }
        )
      )
      val forms = matched.map { (form, operation) =>
        PTXOperandDomains.FormDomains(
          slots(requiredAfter(form, operation)),
          slots(form.optionalSlots),
          form.unorderedModifiers.toVector.sorted,
          form.complete,
          form.operands.map(operand =>
            PTXOperandDomains.Operand(operand.sourceName, constraintCode(operand.constraint).toString, operand.optional)
          )
        )
      }.distinct
      PTXOperandDomains.Definition(shape.caseName, shape.opcode, forms)
    }

  /** The slot names that take more than one value somewhere, as a closed vocabulary. */
  private def renderSlots(sections: Vector[Section]): String = {
    val names = domainDefinitions(sections).flatMap(_.forms).flatMap(form => form.required ++ form.optional)
      .filter(_.values.size > 1).map(_.name).distinct.sorted
    require(names.map(scalaCaseName(_)).distinct.size == names.size, "ambiguous PTX slot names")
    val cases   = names.map(name => s"  case ${scalaCaseName(name)} extends ModifierSlot(${Emission.literal(name)})")
    val entries = names.map(name => s"${Emission.literal(name)} -> ${scalaCaseName(name)}").mkString(", ")
    GeneratedHeader.scala("from the pinned PTX syntax domains") +
      s"""|package net.kurobako.gemstone.isa.ptx
          |import net.kurobako.gemstone.constraint.*
          |
          |object ModifierSlot {
          |  private lazy val byName: Map[String, ModifierSlot] = Map($entries)
          |  def apply(name: String): Option[ModifierSlot] = byName.get(name)
          |}
          |
          |${GeneratedHeader.annotation()}enum ModifierSlot(val name: NonEmpty[String]) derives CanEqual {
          |${cases.mkString("\n")}
          |}
          |""".stripMargin
  }

  private def checkedSections(sections: Vector[Section]): Vector[Section] = {
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

  private def parse(input: Path): Document = {
    if (!Files.isRegularFile(input)) throw IllegalArgumentException(s"PTX specification is absent: $input")
    Jsoup.parse(input.toFile, StandardCharsets.UTF_8.name(), "")
  }

  private def extract(document: Document): Vector[Section] =
    document.select("h3,h4,h5,h6").asScala.toVector
      .filter { heading =>
        val text = cleanHeading(heading.text()).toLowerCaseRoot
        text.contains("instructions:") || text.contains("instruction:")
      }
      .flatMap(enclosingSection(_))
      .distinct
      .flatMap(extractSection(_))

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
            val alternatives = Text.splitWords(part.group(1), SpellingSeparators).map(_.stripPrefix("."))
            sets.flatMap(set => alternatives.map(set + _))
        }
      }.map(_.filter(known)).filter(_.nonEmpty)
    notes.flatMap(NoteClause.split(_)).map(StateSpaceCondition.replaceAllIn(_, "")).flatMap { clause =>
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
    val examples      = Vector.newBuilder[String]
    val buffer        = mutable.StringBuilder()
    def flush(): Unit = {
      if (buffer.nonEmpty && !isKnownNoise(buffer.toString)) { val _ = examples += buffer.toString }
      buffer.clear()
    }
    blocks.flatten.foreach { raw =>
      val line = ExamplePrefix.replaceFirstIn(raw.trim, "")
      if (line.nonEmpty && !line.startsWith("//")) Elision.split(line).foreach { fragment =>
        val value = fragment.trim
        if (value.nonEmpty) {
          if (buffer.nonEmpty) { val _ = buffer.append(' ') }
          val _ = buffer.append(value)
          if (buffer.last == ';' || buffer.last == ':' || (buffer.length == 1 && buffer.last == '}')) flush()
        }
      }
    }
    flush()
    examples.result()
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
      val values = Text.splitWords(matched.group(2), ListSeparators).view.map(_.stripPrefix(".")).filter(value =>
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

  private val AnyQualifier = "::([A-Za-z0-9_]+)".r

  /** Values of a domain definition; `.param{::entry, ::func}` contributes `param` and each qualified spelling. */
  private def definitionValues(source: String): Set[String] =
    splitTopLevel(source.replace('|', ',')).flatMap { value =>
      val base       = value.takeWhile(_ != '{').trim.stripPrefix(".").stripPrefix("::")
      val qualifiers = AnyQualifier.findAllMatchIn(value.dropWhile(_ != '{')).map(_.group(1))
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
    // the specification once separates a qualifier from its dot, as in `.noftz. vec_16_bit`
    val normalised =
      Predication.replaceFirstIn(Text.normaliseWhitespace(source), "").replaceAll(raw"\.\s+(?=[a-z_])", ".")
    val space = normalised.indexWhere(_.isWhitespace)
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
        ).intersect(required.flatMap(_.values).toSet ++ optional.flatMap(_.values))
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

  /** The values a qualifier name stands for; the specification also refers to a packed-type domain such as
    * `.fp16x2type` as `.fp16x2`.
    */
  private def domain(definitions: Map[String, Set[String]], name: String): Set[String] =
    definitions.getOrElse(
      name,
      definitions.get(s"${name}type").filter(_ => PackedName.matches(name)).getOrElse(Set(name))
    )

  private val PackedName: Regex = "[a-z0-9]+x[24]".r

  private val Qualifier = "::([A-Za-z][A-Za-z0-9_]*)".r

  private def normaliseModifiers(
      source: String,
      definitions: Map[String, Set[String]]
  ): (Vector[Slot], Vector[Slot], Boolean) = {
    @tailrec
    def loop(
        index: Int,
        required: Vector[Slot],
        optional: Vector[Slot],
        complete: Boolean
    ): (Vector[Slot], Vector[Slot], Boolean) =
      if (index >= source.length) (required.filter(_.values.nonEmpty), optional.filter(_.values.nonEmpty), complete)
      else source(index) match {
        case '.' =>
          val name = ModifierName.findPrefixOf(source.drop(index + 1)).getOrElse("")
          if (name.isEmpty) loop(index + 1, required, optional, false)
          else loop(index + name.length + 1, required :+ Slot(domain(definitions, name))(name), optional, complete)
        case '{' =>
          val end = closingBrace(source, index)
          if (end >= source.length) loop(source.length, required, optional, false)
          else if (source.startsWith("::", index + 1) && required.nonEmpty) {
            val suffixes = Qualifier
              .findAllMatchIn(source.slice(index + 1, end)).flatMap { matched =>
                val name = matched.group(1)
                domain(definitions, name)
              }.toSet
            val qualified = for {
              base   <- required.last.values
              suffix <- suffixes
            } yield s"$base::$suffix"
            val widened = Slot(required.last.values ++ qualified)(required.last.name)
            loop(end + 1, required.init :+ widened, optional, complete && suffixes.nonEmpty)
          } else {
            val slots = OptionalSpelling.findAllMatchIn(source.slice(index + 1, end)).map { matched =>
              val bases    = domain(definitions, matched.group(1))
              val suffixes = Option(matched.group(2)).toVector.flatMap(group =>
                Qualifier.findAllMatchIn(group).map(_.group(1))
              )
              Slot(bases ++ bases.flatMap(base => suffixes.map(suffix => s"$base::$suffix")))(matched.group(1))
            }.toVector
            loop(end + 1, required, optional ++ slots, complete && slots.exists(_.values.nonEmpty))
          }
        case _ => loop(index + 1, required, optional, false)
      }
    loop(0, Vector.empty, Vector.empty, true)
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
  ): Vector[(String, String)] = {
    val shapes = definitionShapes(sections).map(shape =>
      shape.copy(fields = shape.fields.map(field => field.copy(name = abbreviateFieldName(field.name))))
    )
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
      val parameters      = fields.mkString(", ")
      val constructorArgs =
        if (shape.fields.isEmpty) ""
        else if (shape.fields.forall(!_.optional)) s", ${shape.fields.map(_.name).mkString(", ")}"
        else s", (${operandValues(shape)})*"
      s"  case ${shape.caseName}($parameters) extends Form(${constructorArgs.stripPrefix(", ")})"
    }
    def renderDecoder(shape: InstructionShape): String = {
      val minRequired        = shape.fields.lastIndexWhere(!_.optional) + 1
      val allowed            = allowedOperandCounts(shape)
      val allowedTest        = operandCountTest(allowed)
      val defaultOperandTest =
        if (allowedTest.isEmpty) "false"
        else if (allowed.min >= minRequired) allowedTest
        else s"operands.size >= $minRequired && ($allowedTest)"
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
      val named             = shape.fields.size > 2
      val constructorFields = shape.fields.zipWithIndex.map { case (field, position) =>
        val value =
          if (!field.optional) s"operands($position)"
          else if (position == 0) "operands.headOption"
          else s"operands.lift($position)"
        if (named) s"${field.name} = $value" else value
      }
      def call(arguments: String*): String = {
        val passed =
          if (named) shape.fields.map(_.name).zip(arguments).map((field, value) => s"$field = $value") else arguments
        s"Form.${shape.caseName}(${passed.mkString(", ")})"
      }
      val defaultConstructor = s"Form.${shape.caseName}(${constructorFields.mkString(", ")})"
      val constructor        = shape.caseName match {
        case "CpAsyncBulkMBarrier" =>
          s"if (modifierNames.contains(\"ignore_oob\")) ${call("operands(0)", "operands(1)", "operands(2)", "operands(5)", "Some(operands(3))", "Some(operands(4))", "None", "operands.lift(6)")} else if (modifierNames.contains(\"multicast::cluster\")) ${call("operands(0)", "operands(1)", "operands(2)", "operands(3)", "None", "None", "operands.lift(4)", "operands.lift(5)")} else ${call("operands(0)", "operands(1)", "operands(2)", "operands(3)", "None", "None", "None", "operands.lift(4)")}"
        case "CpAsyncBulkGroup" =>
          s"if (operands.size == 3) ${call("operands(0)", "operands(1)", "operands(2)", "None", "None")} else if (operands.size == 4 && modifierNames.contains(\"cp_mask\")) ${call("operands(0)", "operands(1)", "operands(2)", "None", "Some(operands(3))")} else ${call("operands(0)", "operands(1)", "operands(2)", "Some(operands(3))", "operands.lift(4)")}"
        case "CpAsyncBulkTensorMBarrier" =>
          s"if (modifierNames.exists(_.startsWith(\"im2col\")) && modifierNames.contains(\"multicast::cluster\")) ${call("operands(0)", "operands(1)", "operands(2)", "Some(operands(3))", "operands.lift(4)", "operands.lift(5)")} else if (modifierNames.exists(_.startsWith(\"im2col\"))) ${call("operands(0)", "operands(1)", "operands(2)", "Some(operands(3))", "None", "operands.lift(4)")} else if (modifierNames.contains(\"multicast::cluster\")) ${call("operands(0)", "operands(1)", "operands(2)", "None", "operands.lift(3)", "operands.lift(4)")} else ${call("operands(0)", "operands(1)", "operands(2)", "None", "None", "operands.lift(3)")}"
        case "CpAsyncBulkTensorGroup" =>
          s"${call("operands(0)", "operands(1)", "operands.lift(2)")}"
        case "Call" =>
          s"if (operands.size >= 2 && Operand.isParenthesized(operands(0))) ${call("Some(operands(0))", "operands(1)", "operands.lift(2)", "operands.lift(3)")} else ${call("None", "operands(0)", "operands.lift(1)", "operands.lift(2)")}"
        case "MbarrierTestWait" =>
          s"if (operands.size == 3) ${call("operands(0)", "None", "operands(1)", "operands(2)")} else ${call("operands(0)", "Some(operands(1))", "operands(2)", "operands(3)")}"
        case "MbarrierTryWait" =>
          s"if (operands.size == 3) ${call("operands(0)", "None", "operands(1)", "operands(2)", "None")} else if (operands.size == 4 && !Operand.isWithPredicate(operands(0))) ${call("operands(0)", "None", "operands(1)", "operands(2)", "Some(operands(3))")} else ${call("operands(0)", "Some(operands(1))", "operands(2)", "operands(3)", "operands.lift(4)")}"
        case "Tcgen05Mma" =>
          s"if (operands.size == 5) ${call("operands(0)", "operands(1)", "operands(2)", "operands(3)", "None", "None", "None", "operands(4)", "None")} else if (Operand.isAddress(operands(4))) ${call("operands(0)", "operands(1)", "operands(2)", "operands(3)", "Some(operands(4))", "Some(operands(5))", "None", "operands(6)", "None")} else ${call("operands(0)", "operands(1)", "operands(2)", "operands(3)", "None", "None", "Some(operands(4))", "operands(5)", "operands.lift(6)")}"
        case "Tcgen05MmaSp" =>
          s"if (operands.size == 6) ${call("operands(0)", "operands(1)", "operands(2)", "operands(3)", "operands(4)", "None", "None", "None", "operands(5)", "None")} else if (Operand.isAddress(operands(5))) ${call("operands(0)", "operands(1)", "operands(2)", "operands(3)", "operands(4)", "Some(operands(5))", "Some(operands(6))", "None", "operands(7)", "None")} else ${call("operands(0)", "operands(1)", "operands(2)", "operands(3)", "operands(4)", "None", "None", "Some(operands(5))", "operands(6)", "operands.lift(7)")}"
        case _ => defaultConstructor
      }
      val invalidOperands =
        s"Left(DecodeError(${Emission.literal(shape.opcode)}, DecodeProblem.InvalidOperandCount(Vec(${allowed.mkString(", ")}), operands.size)))"
      val decodeBody =
        if (operationTest == "true")
          s"if ($operandTest) Right($constructor) else $invalidOperands"
        else
          s"if ($operationTest && ($operandTest)) Right($constructor) else if ($operationTest) $invalidOperands else Left(DecodeError(((${Emission.literal(shape.opcode)}) +: modifierNames).mkString(\".\"), DecodeProblem.UnknownOpcode))"
      val usesModifiers = s"$operationTest $operandTest $constructor".contains("modifierNames")
      val modifierNames = if (usesModifiers) "      val modifierNames = mods.map(_.name)\n" else ""
      s"""|(operands, ${if (usesModifiers) "mods" else "_"}) => {
          |$modifierNames      $decodeBody
          |    }""".stripMargin
    }
    def renderMetadata(shape: InstructionShape): String = {
      val entries = sections.flatMap { section =>
        val forms = section.forms.view.flatMap(form => operationsFor(section, form)).flatMap {
          case (opcode, form) if opcode == shape.opcode                => renderForms(form, shape.operationModifiers)
          case (opcode, form) if spelledUnderBase(opcode, form, shape) => renderForms(form, Vector.empty)
          case _                                                       => Vector.empty
        }.toVector.distinct
        val modifierTargets = section.modifierTargets.toVector
          .map((modifiers, target) => s"${modifiers.toVector.sorted.mkString("+")}=$target").sorted.mkString(",")
        val header =
          s"${section.introduced.major}.${section.introduced.minor} ${section.minimumTarget} $modifierTargets"
        Option.when(forms.nonEmpty)((header +: forms).mkString(" "))
      }
      if (entries.isEmpty) throw IllegalStateException(s"missing PTX metadata for ${shape.opcode}")
      val text = entries.mkString("\n")
      require(text.length < 65535, s"PTX metadata for ${shape.opcode} exceeds a string constant")
      Emission.literal(text)
    }
    val metadata    = shapes.map(renderMetadata(_))
    val definitions = shapes.zip(metadata).map { (shape, rendered) =>
      s"""|    case ${shape.caseName} extends Definition(
          |    ${Emission.literal(shape.opcode)},
          |    Opcode.${scalaCaseName(shape.opcode.takeWhile(_ != '.'))},
          |    Vec(${shape.opcode.split('.').drop(1).map(value => s"Modifier.${scalaCaseName(value)}").mkString(", ")}),
          |    ${renderDecoder(shape)},
          |    $rendered
          |  )""".stripMargin
    }
    val instruction = GeneratedHeader.scala(
      s"PTX instruction model from the pinned NVIDIA ISA ${version.source} specification"
    ) +
      s"""|package net.kurobako.gemstone.isa.ptx
        |
        |import net.kurobako.gemstone.string.*
        |import net.kurobako.gemstone.constraint.*
        |import net.kurobako.gemstone.isa.*
        |import net.kurobako.gemstone.collection.*
        |import net.kurobako.gemstone.collection.Vec.*
        |
        |${GeneratedHeader.annotation()}object Instruction {
        |  type Decoder = (operands: Vec[Operand], mods: Vec[Modifier]) => Either[DecodeError, Form]
        |
        |  enum Definition(val opcode: NonEmpty[String], val mnemonic: Opcode, val fixedModifiers: Vec[Modifier], val decoder: Decoder, metadata: String) {
        |${definitions.mkString("\n")}
        |
        |  lazy val meta: Metadata = Metadata.parse(metadata)
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
        |    val remaining = allModifiers.drop(specificity)
        |    var successful: Option[(Definition, Form)] = None
        |    val ambiguous = scala.collection.mutable.ArrayBuffer.empty[String]
        |    val expected = scala.collection.mutable.ArrayBuffer.empty[Int]
        |    index = 0
        |    while (index < definitions.size) {
        |      val definition = definitions(index)
        |      if (definition.fixedModifiers.size == specificity && fixedModifiersMatch(allModifiers, definition.fixedModifiers))
        |        definition.decoder(operands, remaining) match {
        |          case Right(form) => successful match {
        |              case None => successful = Some(definition -> form)
        |              case Some((first, _)) =>
        |                if (ambiguous.isEmpty) ambiguous.append(first.toString)
        |                ambiguous.append(definition.toString)
        |            }
        |          case Left(DecodeError(_, DecodeProblem.InvalidOperandCount(counts, _))) => expected.appendAll(counts)
        |          case Left(_) => ()
        |        }
        |      index += 1
        |    }
        |
        |    if (ambiguous.nonEmpty)
        |      Left(DecodeError(qualifiedOpcode(name, allModifiers), DecodeProblem.AmbiguousDefinitions(ambiguous.toVec)))
        |    else successful match {
        |      case Some((_, form)) => Right(Instruction(form, remaining, pred))
        |      case None if expected.nonEmpty =>
        |        Left(DecodeError(qualifiedOpcode(name, allModifiers), DecodeProblem.InvalidOperandCount(expected.distinct.sorted.toVec, operands.size)))
        |      case None => Left(DecodeError(qualifiedOpcode(name, allModifiers), DecodeProblem.UnknownOpcode))
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
        |${GeneratedHeader.annotation()}case class Instruction(form: Form, mods: Vec[Modifier], pred: Option[Predication]) {
        |  def definition: Instruction.Definition = Instruction.Definition.fromOrdinal(form.ordinal)
        |
        |  def operands: Vec[Operand] = form.operands
        |
        |  def opcode: NonEmpty[String] = definition.opcode
        |
        |  def fixedModifiers: Vec[Modifier] = definition.fixedModifiers
        |
        |  def decoder: Instruction.Decoder = definition.decoder
        |
        |  def meta: Metadata = definition.meta
        |
        |  def qualifiedOpcode: NonEmpty[String] =
        |    if (mods.isEmpty) opcode else s"$$opcode.$${mods.map(_.name).mkString(".")}"
        |
        |  lazy val signature: InstructionSignature = InstructionSignature(definition.mnemonic, definition.fixedModifiers ++ mods)
        |}
        |
        |""".stripMargin
    val form = GeneratedHeader.scala(
      s"PTX instruction forms from the pinned NVIDIA ISA ${version.source} specification"
    ) +
      s"""|package net.kurobako.gemstone.isa.ptx
        |
        |import net.kurobako.gemstone.collection.*
        |import net.kurobako.gemstone.collection.Vec.*
        |
        |${GeneratedHeader.annotation()}enum Form(operandValues: Operand*) {
        |${cases.mkString("\n")}
        |
        |  val operands: Vec[Operand] = operandValues.toVec
        |}
        |""".stripMargin
    Vector("Instruction.scala" -> instruction, "Form.scala" -> form)

  }

  /** The shapes that become instruction definitions: the bulk copies split by completion mechanism. */
  private def definitionShapes(sections: Vector[Section]): Vector[InstructionShape] =
    instructionShapes(sections).flatMap {
      case shape if shape.opcode == "cp.async.bulk" =>
        Vector(
          shape.copy(
            caseName = "CpAsyncBulkMBarrier",
            fields = BulkMBarrierFields,
            decodeGuard = Some("!modifierNames.contains(\"bulk_group\")")
          ),
          shape.copy(
            caseName = "CpAsyncBulkGroup",
            fields = BulkGroupFields,
            decodeGuard = Some("modifierNames.contains(\"bulk_group\")")
          )
        )
      case shape if shape.opcode == "cp.async.bulk.tensor" =>
        Vector(
          shape.copy(
            caseName = "CpAsyncBulkTensorMBarrier",
            fields = TensorMBarrierFields,
            decodeGuard = Some("!modifierNames.contains(\"bulk_group\")")
          ),
          shape.copy(
            caseName = "CpAsyncBulkTensorGroup",
            fields = TensorGroupFields,
            decodeGuard = Some("modifierNames.contains(\"bulk_group\")")
          )
        )
      case shape => Vector(shape)
    }

  private def instructionShapes(sections: Vector[Section]): Vector[InstructionShape] = {
    val formsByOpcode = sections.flatMap(section => section.forms.flatMap(form => operationsFor(section, form)))
      .groupMap(_._1)(_._2)
    formsByOpcode.toVector.sortBy(_._1).map { case (opcode, forms) =>
      val parts          = opcode.split('.').toVector
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
      InstructionShape(opcode, parts.tail, scalaCaseName(opcode), fields, forms)
    }
  }

  private def operandCountTest(counts: Vector[Int]): String =
    Vec.runs(counts).map {
      case (0, 0)                     => "operands.isEmpty"
      case (low, high) if low == high => s"operands.size == $low"
      case (0, high)                  => s"operands.size <= $high"
      case (low, high)                => s"operands.size >= $low && operands.size <= $high"
    }.mkString(" || ")

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
        val titleSuffixes  = candidates.flatMap(_.split('.').toVector.drop(1).headOption).distinct
        val titleSuffixSet = titleSuffixes.toSet
        // `mma.sp` and `mma.sp::ordered_metadata` share one form whose first group selects the variant
        val namespaced = groups.headOption.toVector.flatMap(_.toVector.sorted).filter { value =>
          titleSuffixes.exists(prefix => value.startsWith(prefix + "::")) ||
          (groups.headOption.exists(_.exists(_.startsWith(value + "::"))) && titleSuffixSet(value))
        }
        if (namespaced.nonEmpty)
          namespaced.map { value =>
            val first       = form.requiredSlots.head
            val specialised = form.copy(requiredSlots = Slot(Set(value))(first.name) +: form.requiredSlots.drop(1))
            s"${form.mnemonic}.$value" -> specialised
          }
        else {
          val embedded = candidates.flatMap { candidate =>
            val suffix = candidate.split('.').toVector.tail
            if (suffix.size != 1) Vector.empty
            else groups.zipWithIndex.collectFirst {
              case (values, index) if values == Set(suffix.head) && index > 0 && groups(index - 1).size <= 8 =>
                groups(index - 1).toVector.sorted.map { selector =>
                  val specialised =
                    form.requiredSlots.updated(index - 1, Slot(Set(selector))(form.requiredSlots(index - 1).name))
                  s"${form.mnemonic}.$selector.${suffix.head}" -> form.copy(requiredSlots = specialised)
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
    "mma.sp"                   -> Vector("destination", "sourceA", "sourceB", "sourceC", "e", "f"),
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
        .minByOption { case (name, values) => (-values.size, fieldNamePenalty(name), name) }.map(_._1)
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

  private def abbreviateFieldName(value: String): String =
    AbbreviatedFields.getOrElse(
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

  private def constraintCode(constraint: String): Char = constraint match {
    case "Register" => 'R'
    case "Address"  => 'A'
    case "Vector"   => 'V'
    case "Symbol"   => 'S'
    case "Value"    => 'X'
    case _          => 'U'
  }

  /** The required slots of `form` that the opcode spelling `operationModifiers` leaves to the modifier suffix. */
  private def requiredAfter(form: Form, operationModifiers: Vector[String]): Vector[Slot] = {
    val pending = operationModifiers.groupMapReduce(identity)(_ => 1)(_ + _)
    form.requiredSlots.foldLeft((Vector.empty[Slot], pending)) { case ((kept, left), slot) =>
      slot.values.toSeq match {
        case Seq(modifier) if left.getOrElse(modifier, 0) > 0 => (kept, left.updated(modifier, left(modifier) - 1))
        case _                                                => (kept :+ slot, left)
      }
    }._1
  }

  /** One form per subset of the optional operands, as `Metadata.parse` reads it. */
  private def renderForms(form: Form, operationModifiers: Vector[String]): Vector[String] = {
    val operands = form.operands.foldLeft(Vector("")) { (prefixes, operand) =>
      val constraint = constraintCode(operand.constraint)
      prefixes.map(_ :+ constraint) ++ (if (operand.optional) prefixes else Vector.empty)
    }.distinct
    def modifiers(values: Set[String]): String = values.toVector.sorted.mkString(",")
    val rest = s";${requiredAfter(form, operationModifiers).map(slot => modifiers(slot.values)).mkString("/")};" +
      s"${modifiers(form.optionalModifiers)};" +
      s"${modifiers(form.unorderedModifiers)};${if (form.complete) "1" else "0"}"
    operands.map(_ + rest)
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
