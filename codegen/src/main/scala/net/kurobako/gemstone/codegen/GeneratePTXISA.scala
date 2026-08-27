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

/** Generates the typed instruction model and parser corpus from the pinned NVIDIA PTX HTML document. */
object GeneratePTXISA {
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
      operands: Vector[FormOperand],
      requiredModifierGroups: Vector[Set[String]],
      optionalModifiers: Set[String],
      complete: Boolean
  )
  private final case class FormOperand(sourceName: String, constraint: String, optional: Boolean, complete: Boolean)
  private final case class InstructionField(name: String, optional: Boolean)
  private final case class InstructionShape(
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
  private val Definition: Regex    =
    raw"(?s)\.([A-Za-z][A-Za-z0-9_]*(?:::[A-Za-z][A-Za-z0-9_]*)*)\s*=\s*\{([^}]*)\}\s*;?".r
  private val OpcodeDefinition: Regex = raw"(?s)(?:^|\n)\s*([A-Za-z][A-Za-z0-9_]*)\s*=\s*\{([^}]*)\}\s*;?".r
  private val DefinitionStart: Regex  = raw"^\s*\.?[A-Za-z][A-Za-z0-9_:-]*\s*=".r
  private val TitleOpcode: Regex      = raw"[A-Za-z][A-Za-z0-9_]*(?:\.[A-Za-z0-9_]+)+".r
  private val ModifierName: Regex     = raw"[A-Za-z][A-Za-z0-9_]*(?:::[A-Za-z][A-Za-z0-9_]*)*".r

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case Vector("instructions", input, output, version, packageVersion) =>
      val sections = checkedSections(Paths.get(input))
      write(Paths.get(output), renderInstructions(version, packageVersion, sections))
    case Vector("tests", input, output, version, packageVersion) =>
      val sections = checkedSections(Paths.get(input))
      val examples = checkedExamples(sections)
      write(Paths.get(output), renderTests(version, packageVersion, examples))
    case _ =>
      throw new IllegalArgumentException(
        "usage: GeneratePTXISA (instructions|tests) <ptx.html> <output.scala> <ptx-version> <package-version>"
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
    document.select("h3,h4,h5,h6").asScala.toVector
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
    val heading = cleanHeading(section.select("h3,h4,h5,h6").asScala.headOption.map(_.text()).getOrElse(""))
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
      val text     = section.text()
      val versions = VersionNote.findAllMatchIn(text).map(value => value.group(1).toInt -> value.group(2).toInt)
        .toVector.distinct
      val targets              = TargetNote.findAllMatchIn(text).map(_.group(1).toInt).toVector.distinct
      val introduced           = versions.minOption.getOrElse(1 -> 0)
      val allTargets           = text.contains("Supported on all target architectures")
      val target               = if (allTargets) 10 else targets.minOption.getOrElse(10)
      val availabilityComplete = versions.size <= 1 && targets.size <= 1 && !(allTargets && targets.nonEmpty)
      val syntaxValues         = syntax.toVector
      val forms                = normalizeForms(syntaxValues)
      val mnemonics            = forms.map(_.mnemonic).distinct
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
          forms.map(form => form.copy(complete = form.complete && availabilityComplete))
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
    normalized.contains("is_canceled pred.b128") ||
    normalized == "neg.s8x4 p, q, r;" ||
    normalized == "isspacep.shared::cluster ishrdany sptr;" ||
    normalized.contains("cache_hint}") ||
    normalized == "multimem.cp.reduce.async.bulk.relaxed.cta.global.bulk_group.add.u64 [dstMem], [srcMem], size, [mbar];" ||
    normalized == "mbarrier.init [bar];" ||
    normalized == "@!p bra try_wait loop;" ||
    normalized.contains("a-desc") ||
    normalized.contains(".cta_group.kind ") ||
    normalized.matches(".*collector::[^ ]+:(?:fill|use).*")
  }

  private def normalizeForms(blocks: Vector[String]): Vector[Form] = blocks.flatMap { block =>
    val definitions = Definition.findAllMatchIn(block).map { matched =>
      val values = matched.group(2).split("[,|\\s]+").toVector.map(_.trim.stripPrefix(".")).filter(value =>
        ModifierName.matches(value)
      ).toSet
      matched.group(1) -> values
    }.toMap
    val opcodeDefinitions = OpcodeDefinition.findAllMatchIn(block).map { matched =>
      val values = matched.group(2).split("[,|\\s]+").toVector.map(_.trim.stripPrefix(".")).filter(value =>
        Mnemonic.matches(value)
      )
      matched.group(1) -> values
    }.toMap
    syntaxFragments(block).flatMap(fragment => normalizeForm(fragment, definitions)).flatMap { form =>
      opcodeDefinitions.get(form.mnemonic).filter(_.nonEmpty) match {
        case Some(opcodes) => opcodes.map(opcode => form.copy(mnemonic = opcode))
        case None          => Vector(form)
      }
    }
  }.distinct

  private def syntaxFragments(block: String): Vector[String] = {
    val fragments     = Vector.newBuilder[String]
    var current       = ""
    def flush(): Unit = {
      current.split(';').iterator.map(_.trim).filter(_.nonEmpty).foreach(fragments += _)
      current = ""
    }
    block.linesIterator.foreach { rawLine =>
      val line       = stripComment(rawLine)
      val beginsForm = rawLine.headOption.exists(!_.isWhitespace) &&
        DefinitionStart.findPrefixMatchOf(line).isEmpty && extractMnemonic(line).nonEmpty
      if (beginsForm) {
        flush()
        current = line
        if (line.endsWith(";")) flush()
      } else if (current.nonEmpty && line.nonEmpty && DefinitionStart.findPrefixMatchOf(line).isEmpty) {
        current = s"$current $line"
        if (line.endsWith(";")) flush()
      } else if (DefinitionStart.findPrefixMatchOf(line).nonEmpty) {
        flush()
      }
    }
    flush()
    fragments.result()
  }

  private def normalizeForm(source: String, definitions: Map[String, Set[String]]): Option[Form] = {
    val normalized = Predication.replaceFirstIn(source.replaceAll("\\s+", " ").trim, "")
    val space      = normalized.indexWhere(_.isWhitespace)
    if (normalized.isEmpty) None
    else {
      val opcodeToken = if (space < 0) normalized else normalized.take(space)
      extractMnemonic(opcodeToken).map { mnemonic =>
        val (required, optional, modifierComplete) = normalizeModifiers(opcodeToken.drop(mnemonic.length), definitions)
        val operandText                            = if (space < 0) "" else normalized.drop(space).trim
        val operandNames                           = splitTopLevel(operandText).flatMap(expandOptionalOperand)
        val operands                               = operandNames.map { case (name, optional) =>
          val (constraint, complete) = operandConstraint(name)
          FormOperand(name, constraint, optional, complete)
        }
        Form(mnemonic, operands, required, optional, modifierComplete && operands.forall(_.complete))
      }
    }
  }

  /** Expands the PTX notation `value{, optional}` into two constructor fields. Braced components which do not begin
    * with a comma are modifiers of the preceding operand and remain part of its source name.
    */
  private def expandOptionalOperand(source: String): Vector[(String, Boolean)] = {
    val OptionalTail = raw"\{,\s*([^{}]+)\}".r
    val value        = source.trim
    val matches      = OptionalTail.findAllMatchIn(value).toVector
    if (matches.isEmpty) Vector(value -> false)
    else {
      val head = value.take(matches.head.start).trim
      val tail =
        matches.flatMap(matched => splitTopLevel(matched.group(1)).map(_.trim).filter(_.nonEmpty).map(_ -> true))
      (if (head.isEmpty) Vector.empty else Vector(head -> false)) ++ tail
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
    val value = source.trim.replaceFirst("^\\{[-+!]\\}", "")
    val name  = value.replaceAll("^[!+\\-]", "").takeWhile(char => char.isLetterOrDigit || char == '_').toLowerCase
    if (value.startsWith("[")) "Address" -> true
    else if (value.startsWith("{")) "Vector" -> true
    else if (Set("d", "dst", "p", "q", "r", "result").contains(name)) "Register" -> true
    else if (name.contains("label") || name == "target" || name.contains("func")) "Symbol" -> true
    else if (name.nonEmpty) "Value" -> true
    else "Unknown"                  -> false
  }

  private def renderInstructions(version: String, packageVersion: String, sections: Vector[Section]): String = {
    require(packageVersion.matches("v[0-9]+(?:_[0-9]+)*"), s"invalid PTX package version: $packageVersion")
    val versionParts = version.split('.').toVector.map(_.toInt)
    require(versionParts.size == 2, s"invalid PTX version: $version")
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
      val segments           = ListBuffer.empty[String]
      val fixed              = ListBuffer.empty[String]
      def flushFixed(): Unit =
        if (fixed.nonEmpty) {
          segments += fixed.mkString("Vector(", ", ", ")")
          fixed.clear()
        }
      shape.fields.foreach { field =>
        if (field.optional) {
          flushFixed()
          segments += s"${field.name}.toVector"
        } else fixed += field.name
      }
      flushFixed()
      val defaultBody = if (segments.isEmpty) "Vector.empty" else segments.mkString(" ++ ")
      shape.caseName match {
        case "CpAsyncBulkMBarrier" =>
          "Vector(destAddr, srcAddr, size) ++ ignoredBytesLeft.toVector ++ ignoredBytesRight.toVector ++ Vector(barrierAddr) ++ ctaMask.toVector ++ cachePolicy.toVector"
        case "CpAsyncBulkGroup" =>
          "Vector(destAddr, srcAddr, size) ++ cachePolicy.toVector ++ byteMask.toVector"
        case "CpAsyncBulkTensorMBarrier" =>
          "Vector(destAddr, tensorMapAndCoordinates, barrierAddr) ++ im2colInfo.toVector ++ ctaMask.toVector ++ cachePolicy.toVector"
        case "CpAsyncBulkTensorGroup" =>
          "Vector(tensorMapAndCoordinates, srcAddr) ++ cachePolicy.toVector"
        case "MbarrierTestWait" =>
          "Vector(completion) ++ reportValue.toVector ++ Vector(barrierAddr, stateOrPhase)"
        case "MbarrierTryWait" =>
          "Vector(completion) ++ reportValue.toVector ++ Vector(barrierAddr, stateOrPhase) ++ timeHint.toVector"
        case "Tcgen05Mma" =>
          "Vector(destAddr, srcA, srcBDesc, instDesc) ++ scaleAAddr.toVector ++ scaleBAddr.toVector ++ disabledOutputLanes.toVector ++ Vector(enableInputD) ++ scaleInputD.toVector"
        case "Tcgen05MmaSp" =>
          "Vector(destAddr, srcA, srcBDesc, sparseMetadataAddr, instDesc) ++ scaleAAddr.toVector ++ scaleBAddr.toVector ++ disabledOutputLanes.toVector ++ Vector(enableInputD) ++ scaleInputD.toVector"
        case _ => defaultBody
      }
    }
    val cases = shapes.map { shape =>
      val fields = shape.fields.map { field =>
        s"${field.name}: ${if (field.optional) "Option[Operand]" else "Operand"}"
      }
      val parameters      = (fields ++ Vector("mods: Vector[Modifier]", "pred: Option[Predication]")).mkString(", ")
      val constructorArgs =
        if (shape.fields.isEmpty) ""
        else if (shape.fields.forall(!_.optional)) s", ${shape.fields.map(_.name).mkString(", ")}"
        else s", (${operandValues(shape)})*"
      s"  case ${shape.caseName}($parameters) extends Instruction(InstructionDef.${shape.caseName}$constructorArgs)"
    }
    def renderDecoder(shape: InstructionShape): String = {
      val allowed            = allowedOperandCounts(shape)
      val defaultOperandTest = allowed.map(count => s"operands.size == $count").mkString(" || ")
      val operandTest        = shape.caseName match {
        case "CpAsyncBulkMBarrier" =>
          "if (modifierNames.contains(\"ignore_oob\")) operands.size == 6 || operands.size == 7 else if (modifierNames.contains(\"multicast::cluster\")) operands.size >= 4 && operands.size <= 6 else operands.size == 4 || operands.size == 5"
        case "CpAsyncBulkTensorMBarrier" =>
          "if (modifierNames.exists(_.startsWith(\"im2col\")) && modifierNames.contains(\"multicast::cluster\")) operands.size >= 5 && operands.size <= 6 else if (modifierNames.exists(_.startsWith(\"im2col\")) || modifierNames.contains(\"multicast::cluster\")) operands.size >= 4 && operands.size <= 5 else operands.size == 3 || operands.size == 4"
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
        case "MbarrierTestWait" =>
          s"if (operands.size == 3) Instruction.MbarrierTestWait(operands(0), None, operands(1), operands(2), $remainingModifiers, pred) else Instruction.MbarrierTestWait(operands(0), Some(operands(1)), operands(2), operands(3), $remainingModifiers, pred)"
        case "MbarrierTryWait" =>
          s"if (operands.size == 3) Instruction.MbarrierTryWait(operands(0), None, operands(1), operands(2), None, $remainingModifiers, pred) else if (operands.size == 4 && !operands(0).isInstanceOf[Operand.WithPredicate]) Instruction.MbarrierTryWait(operands(0), None, operands(1), operands(2), Some(operands(3)), $remainingModifiers, pred) else Instruction.MbarrierTryWait(operands(0), Some(operands(1)), operands(2), operands(3), operands.lift(4), $remainingModifiers, pred)"
        case "Tcgen05Mma" =>
          s"if (operands.size == 5) Instruction.Tcgen05Mma(operands(0), operands(1), operands(2), operands(3), None, None, None, operands(4), None, $remainingModifiers, pred) else if (operands(4).isInstanceOf[Operand.Address]) Instruction.Tcgen05Mma(operands(0), operands(1), operands(2), operands(3), Some(operands(4)), Some(operands(5)), None, operands(6), None, $remainingModifiers, pred) else Instruction.Tcgen05Mma(operands(0), operands(1), operands(2), operands(3), None, None, Some(operands(4)), operands(5), operands.lift(6), $remainingModifiers, pred)"
        case "Tcgen05MmaSp" =>
          s"if (operands.size == 6) Instruction.Tcgen05MmaSp(operands(0), operands(1), operands(2), operands(3), operands(4), None, None, None, operands(5), None, $remainingModifiers, pred) else if (operands(5).isInstanceOf[Operand.Address]) Instruction.Tcgen05MmaSp(operands(0), operands(1), operands(2), operands(3), operands(4), Some(operands(5)), Some(operands(6)), None, operands(7), None, $remainingModifiers, pred) else Instruction.Tcgen05MmaSp(operands(0), operands(1), operands(2), operands(3), operands(4), None, None, Some(operands(5)), operands(6), operands.lift(7), $remainingModifiers, pred)"
        case _ => defaultConstructor
      }
      val invalidOperands =
        s"Left(InstructionDecodeError.InvalidOperandCount(${literal(shape.opcode)}, Vector(${allowed.mkString(", ")}), operands.size))"
      val decodeBody =
        if (operationTest == "true")
          s"if ($operandTest) Right($constructor) else $invalidOperands"
        else
          s"if ($operationTest && ($operandTest)) Right($constructor) else if ($operationTest) $invalidOperands else Left(InstructionDecodeError.UnknownOpcode(((${literal(shape.opcode)}) +: modifierNames).mkString(\".\")))"
      val modifierNames =
        Option.when(s"$operationTest $operandTest $constructor".contains("modifierNames"))(
          "      val modifierNames = mods.map(_.name)\n"
        ).getOrElse("")
      s"""|(
          |      operands: Vector[Operand],
          |      mods: Vector[Modifier],
          |      pred: Option[Predication]
          |    ) => {
          |$modifierNames      $decodeBody
          |    }""".stripMargin
    }
    def renderMetadata(shape: InstructionShape): String = {
      val entries = sections.flatMap { section =>
        val forms = section.forms.flatMap(form => operationsFor(section, form)).collect {
          case (opcode, form) if opcode == shape.opcode => renderForm(form, opcode)
        }
        Option.when(forms.nonEmpty) {
          s"""|Metadata.Entry(
              |          ${literal(section.id)},
              |          ${literal(section.heading)},
              |          ${literal(section.title)},
              |          ${literal(section.category)},
              |          ${vector(section.syntax)},
              |          PTXVersion(${section.introduced._1}, ${section.introduced._2}),
              |          SMTarget(${section.minimumTarget}),
              |          Vector(${forms.mkString(", ")})
              |        )""".stripMargin
        }
      }
      require(entries.nonEmpty, s"missing PTX metadata for ${shape.opcode}")
      s"""|() => Metadata(Vector(
          |        ${entries.mkString(",\n")}
          |      ))""".stripMargin
    }
    val definitions = shapes.map { shape =>
      s"""|  case ${shape.caseName} extends InstructionDef(
          |    ${literal(shape.opcode)},
          |    ${renderDecoder(shape)},
          |    ${renderMetadata(shape)}
          |  )""".stripMargin
    }
    s"""|// Generated PTX instruction model from the pinned NVIDIA ISA $version specification. Do not edit by hand.
        |package net.kurobako.gemstone.isa.ptx.$packageVersion
        |
        |enum InstructionDecodeError {
        |  case UnknownOpcode(opcode: String)
        |  case InvalidOperandCount(opcode: String, expected: Vector[Int], actual: Int)
        |  case AmbiguousInstruction(opcode: String, definitions: Vector[String])
        |}
        |
        |type Decoder = (
        |    operands: Vector[Operand],
        |    mods: Vector[Modifier],
        |    pred: Option[Predication]
        |) => Either[InstructionDecodeError, Instruction]
        |
        |enum InstructionDef(val opcode: String, val decoder: Decoder, metadata: () => Metadata) {
        |${definitions.mkString("\n")}
        |
        |  final lazy val meta: Metadata = metadata()
        |
        |  private[$packageVersion] final val fixedMods: Vector[String] = opcode.split('.').toVector.drop(1)
        |}
        |
        |enum Instruction(val definition: InstructionDef, operandValues: Operand*) {
        |${cases.mkString("\n")}
        |
        |  final val operands: Vector[Operand] = operandValues.toVector
        |
        |  final def opcode: String = definition.opcode
        |
        |  final def decoder: Decoder = definition.decoder
        |
        |  final def meta: Metadata = definition.meta
        |
        |  def mods: Vector[Modifier]
        |
        |  def pred: Option[Predication]
        |
        |  final def qualifiedOpcode: String = (opcode +: mods.map(_.name)).mkString(".")
        |
        |}
        |
        |object Instruction {
        |  private lazy val DefinitionsByMnemonic: Map[String, Vector[InstructionDef]] =
        |    Instructions.groupMap(_.opcode.takeWhile(_ != '.'))(identity)
        |
        |  def decode(
        |      opcode: String,
        |      operands: Vector[Operand],
        |      mods: Vector[Modifier] = Vector.empty,
        |      pred: Option[Predication] = None
        |  ): Either[InstructionDecodeError, Instruction] = {
        |    val opcodeParts = normalizeOpcode(opcode).split('.').toVector.filter(_.nonEmpty)
        |    val name = opcodeParts.headOption.getOrElse("")
        |    val allModifiers = opcodeParts.drop(1).map(Modifier.apply) ++ mods
        |    val qualified = (name +: allModifiers.map(_.name)).mkString(".")
        |    val modifierNames = allModifiers.map(_.name)
        |    val candidates = DefinitionsByMnemonic.getOrElse(name, Vector.empty)
        |      .filter(definition => modifierNames.startsWith(definition.fixedMods))
        |    val specificity = candidates.map(_.fixedMods.size).maxOption.getOrElse(0)
        |    val attempts = candidates.filter(_.fixedMods.size == specificity).map { definition =>
        |      definition -> definition.decoder(operands, allModifiers.drop(definition.fixedMods.size), pred)
        |    }
        |    val successes = attempts.collect { case (definition, Right(instruction)) => definition -> instruction }
        |    successes match {
        |      case Vector((_, instruction)) => Right(instruction)
        |      case values if values.nonEmpty =>
        |        Left(InstructionDecodeError.AmbiguousInstruction(qualified, values.map(_._1.toString)))
        |      case _ =>
        |        val expected = attempts.collect {
        |          case (_, Left(InstructionDecodeError.InvalidOperandCount(_, counts, _))) => counts
        |        }.flatten.distinct.sorted
        |        if (expected.nonEmpty)
        |          Left(InstructionDecodeError.InvalidOperandCount(qualified, expected, operands.size))
        |        else Left(InstructionDecodeError.UnknownOpcode(qualified))
        |    }
        |  }
        |}
        |
        |val Version: PTXVersion = PTXVersion(${versionParts(0)}, ${versionParts(1)})
        |
        |val Instructions: Vector[InstructionDef] = InstructionDef.values.toVector
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
          present.exists(_.optional) || present.size < forms.size
        )
      }
      val rawFields = ShapeFieldOverrides.getOrElse(opcode, inferredFields)
      val seen      = scala.collection.mutable.Map.empty[String, Int]
      val fields    = rawFields.map { field =>
        val count = seen.getOrElse(field.name, 0) + 1
        seen(field.name) = count
        field.copy(name = if (count == 1) field.name else s"${field.name}$count")
      }
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
    val dotted = TitleOpcode.findAllIn(section.title).map(_.toLowerCase).toVector
      .filter(value => section.mnemonics.contains(value.takeWhile(_ != '.')))
    if (dotted.nonEmpty) dotted.distinct else section.mnemonics
  }

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
            val specialized = form.copy(requiredModifierGroups = Vector(Set(value)) ++ groups.drop(1))
            s"${form.mnemonic}.$value" -> specialized
          }
        else {
          val embedded = candidates.flatMap { candidate =>
            val suffix = candidate.split('.').toVector.tail
            if (suffix.size != 1) Vector.empty
            else groups.zipWithIndex.collectFirst {
              case (values, index) if values == Set(suffix.head) && index > 0 && groups(index - 1).size <= 8 =>
                groups(index - 1).toVector.sorted.map { selector =>
                  val specializedGroups = groups.updated(index - 1, Set(selector))
                  s"${form.mnemonic}.$selector.${suffix.head}" -> form.copy(requiredModifierGroups = specializedGroups)
                }
            }.getOrElse(Vector.empty)
          }
          if (embedded.nonEmpty) embedded.distinct
          else Vector(candidates.find(_ == form.mnemonic).getOrElse(form.mnemonic) -> form)
        }
    }
  }

  private val ScalaKeywords = Set(
    "abstract",
    "case",
    "catch",
    "class",
    "def",
    "do",
    "else",
    "enum",
    "export",
    "extends",
    "false",
    "final",
    "finally",
    "for",
    "given",
    "if",
    "implicit",
    "import",
    "lazy",
    "match",
    "new",
    "null",
    "object",
    "opaque",
    "open",
    "override",
    "package",
    "private",
    "protected",
    "return",
    "sealed",
    "super",
    "then",
    "this",
    "throw",
    "trait",
    "true",
    "try",
    "type",
    "val",
    "var",
    "while",
    "with",
    "yield"
  )

  private val FieldOverrides: Map[String, Vector[String]] = Map(
    "atom"     -> Vector("destination", "address", "value", "compareOrCachePolicy"),
    "bar"      -> Vector("barrier", "threadCount", "predicate", "value"),
    "barrier"  -> Vector("barrier", "threadCount", "predicate", "value"),
    "bfi"      -> Vector("destination", "insertValue", "baseValue", "bitPosition", "bitLength"),
    "bra"      -> Vector("target"),
    "call"     -> Vector("function", "returnParameters", "parameters", "targets"),
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
    "mbarrier.test_wait"            -> Vector("completion", "reportValue", "barrierAddress", "stateOrPhase"),
    "mbarrier.try_wait"      -> Vector("completion", "reportValue", "barrierAddress", "stateOrPhase", "timeHint"),
    "mov"                    -> Vector("destination", "source"),
    "multimem.cp.async.bulk" -> Vector("destinationAddress", "sourceAddress", "size", "byteMask"),
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
    "mbarrier.test_wait" -> Vector(
      InstructionField("completion", false),
      InstructionField("reportValue", true),
      InstructionField("barrierAddress", false),
      InstructionField("stateOrPhase", false)
    ),
    "mbarrier.try_wait" -> Vector(
      InstructionField("completion", false),
      InstructionField("reportValue", true),
      InstructionField("barrierAddress", false),
      InstructionField("stateOrPhase", false),
      InstructionField("timeHint", true)
    ),
    "tcgen05.mma" -> Vector(
      InstructionField("destinationAddress", false),
      InstructionField("sourceA", false),
      InstructionField("sourceBDescriptor", false),
      InstructionField("instructionDescriptor", false),
      InstructionField("scaleAAddress", true),
      InstructionField("scaleBAddress", true),
      InstructionField("disabledOutputLanes", true),
      InstructionField("enableInputD", false),
      InstructionField("scaleInputD", true)
    ),
    "tcgen05.mma.sp" -> Vector(
      InstructionField("destinationAddress", false),
      InstructionField("sourceA", false),
      InstructionField("sourceBDescriptor", false),
      InstructionField("sparseMetadataAddress", false),
      InstructionField("instructionDescriptor", false),
      InstructionField("scaleAAddress", true),
      InstructionField("scaleBAddress", true),
      InstructionField("disabledOutputLanes", true),
      InstructionField("enableInputD", false),
      InstructionField("scaleInputD", true)
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
      val candidates = sourceNames.map(sourceFieldName).filter(_.nonEmpty)
      val selected   = candidates.groupBy(identity).toVector
        .sortBy { case (name, values) => (-values.size, fieldNamePenalty(name), name) }
        .headOption.map(_._1)
        .getOrElse(s"operand${position + 1}")
      val identifier = if (selected.headOption.exists(_.isDigit)) s"operand$selected" else selected
      val safe       = if (ScalaKeywords(identifier)) identifier + "Value" else identifier
      if (safe == "operand" || safe == "value") s"${safe}${position + 1}" else safe
    }

  private def fieldNamePenalty(name: String): Int =
    if (name.startsWith("operand") || name.startsWith("source")) 1 else 0

  private def sourceFieldName(source: String): String = {
    val value = source.trim.replaceFirst("^\\{[-+!]\\}", "")
    if (value.startsWith("[")) {
      val normalized = value.toLowerCase
      if (normalized.contains("dst")) "destinationAddress"
      else if (normalized.contains("src")) "sourceAddress"
      else if (normalized.contains("mbar") || normalized.contains("bar")) "barrierAddress"
      else "address"
    } else if (value.startsWith("{")) "values"
    else {
      val token = value.dropWhile(character => "!+-_(".contains(character))
        .takeWhile(character => character.isLetterOrDigit || character == '_' || character == '-')
      val normalized = token match {
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
      normalized.filter(character => character.isLetterOrDigit || character == '_')
    }
  }

  private def lowerCamel(value: String): String = {
    val words = value.split("[^A-Za-z0-9]+").toVector.filter(_.nonEmpty)
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
    CaseNameOverrides.getOrElse(
      opcode, {
        val words = opcode.split("[^A-Za-z0-9]+").toVector.filter(_.nonEmpty)
        val value = words.map(word => word.head.toUpper.toString + word.tail).mkString
        if (value.isEmpty) "Opcode" else if (value.head.isDigit) s"Opcode$value" else value
      }
    )

  private def renderTests(version: String, packageVersion: String, examples: Vector[(String, String)]): String = {
    require(packageVersion.matches("v[0-9]+(?:_[0-9]+)*"), s"invalid PTX package version: $packageVersion")
    val values = examples.map { case (id, source) => s"    ${literal(id)} -> ${literal(source)}" }.mkString(",\n")
    s"""|package net.kurobako.gemstone.isa.ptx.$packageVersion.generated
        |
        |import net.kurobako.gemstone.isa.ptx.$packageVersion.Printer
        |import net.kurobako.gemstone.isa.ptx.$packageVersion.Parser
        |
        |// Generated from examples in the pinned NVIDIA PTX ISA $version specification. Do not edit.
        |final class DocumentationSuite extends munit.FunSuite {
        |  private val examples: Vector[(String, String)] = Vector(
        |$values
        |  )
        |
        |  test("all documentation examples parse into the typed syntax tree") {
        |    val failures = examples.flatMap { case (id, source) =>
        |      Parser.parse(source) match {
        |        case Left(error) => Vector(s"$$id: $$source\\n  $$error")
        |        case Right(_) => Vector.empty
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

  private def renderForm(form: Form, opcode: String): String = {
    val operands = form.operands.map(_.constraint).map {
      case "Register" => "OperandConstraint.Register"
      case "Address"  => "OperandConstraint.Address"
      case "Vector"   => "OperandConstraint.Vector"
      case "Symbol"   => "OperandConstraint.Symbol"
      case "Value"    => "OperandConstraint.Value"
      case _          => "OperandConstraint.Unknown(\"unparsed\")"
    }.mkString("Vector(", ", ", ")")
    val operationModifiers = opcode.split('.').toVector.drop(1)
    val requiredModifiers  = operationModifiers.foldLeft(form.requiredModifierGroups) { (groups, modifier) =>
      val index = groups.indexWhere(_ == Set(modifier))
      if (index < 0) groups else groups.patch(index, Nil, 1)
    }
    val required = requiredModifiers.map(values =>
      values.toVector.sorted.map(literal).mkString("_root_.scala.collection.immutable.Set(", ", ", ")")
    ).mkString("Vector(", ", ", ")")
    val optional = form.optionalModifiers.toVector.sorted.map(literal)
      .mkString("_root_.scala.collection.immutable.Set(", ", ", ")")
    s"InstructionForm(${literal(opcode)}, $operands, $required, $optional, ${form.complete})"
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
