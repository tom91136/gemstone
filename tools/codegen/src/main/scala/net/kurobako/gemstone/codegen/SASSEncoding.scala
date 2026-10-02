package net.kurobako.gemstone.codegen

import cats.parse.Parser
import cats.parse.Parser0
import scala.annotation.tailrec

/** Compiles the bit-layout and simple value expressions in an extracted SASS description. */
object SASSEncoding {
  enum Expr {
    case Constant(value: BigInt, relaxed: Boolean)
    case Reference(name: String, attribute: Option[String], relaxed: Boolean)
    case Scale(value: Expr, factor: BigInt)
    case Multiply(value: Expr, factor: BigInt)
    case Identity(first: Expr, second: Expr)
    case FloatConvert(value: Expr, selector: String, cases: Vector[(Vector[BigInt], String)], fallback: String)
    case FixedFloat(value: Expr, format: String)
    case Lookup(name: String, arguments: Vector[Expr])
    case Relaxed(value: Expr)
    case Unsupported(source: String)
  }

  case class Assignment(name: String, positions: Vector[Int], expression: Expr)
  case class Form(
      name: String,
      alternate: Boolean,
      opcodes: Vector[(String, BigInt)],
      fields: Vector[Assignment],
      issues: Vector[String]
  )
  case class LookupTable(name: String, rows: Vector[(Vector[BigInt], BigInt)], issues: Vector[String] = Vector.empty)
  case class Compiled(width: Int, forms: Vector[Form], tables: Vector[LookupTable])

  private enum TableAtom {
    case Number(value: BigInt)
    case Character(value: BigInt)
    case Symbol(table: String, value: String)
    case Unqualified(value: String)
    case Wildcard
  }
  private case class ParsedTableRow(inputs: Vector[TableAtom], output: TableAtom)

  private val whitespace: Parser0[Unit]  = Parser.charWhere(_.isWhitespace).rep0.void
  private val whitespace1: Parser[Unit]  = Parser.charWhere(_.isWhitespace).rep.void
  private val identifier: Parser[String] =
    (Parser.charWhere(c => c.isLetter || c == '_') ~
      Parser.charWhere(c => c.isLetterOrDigit || "_.$-".contains(c)).rep0).string
  private val hex: Parser[String] = (Parser.string("0x") | Parser.string("0X")) *>
    Parser.charWhere(c => c.isDigit || "abcdefABCDEF_".contains(c)).rep.string
  private val binary: Parser[String] =
    (Parser.string("0b") | Parser.string("0B")) *> Parser.charWhere(c => c == '0' || c == '1' || c == '_').rep.string
  private val decimal: Parser[String] = Parser.charWhere(c => c.isDigit || c == '_').rep.string
  private val integer: Parser[BigInt] = {
    val magnitude = hex.map("0x" + _) | binary.map("0b" + _) | decimal
    (Parser.char('-').?.with1 ~ magnitude).map { case (sign, raw) =>
      val value = BigInt(
        raw.replace("_", "").replaceFirst("^0[xX]", "").replaceFirst("^0[bB]", ""),
        if (raw.startsWith("0x") || raw.startsWith("0X")) 16
        else if (raw.startsWith("0b") || raw.startsWith("0B")) 2 else 10
      )
      if (sign.nonEmpty) -value else value
    }
  }

  private val quotedLabel: Parser[String] =
    Parser.char('"') *> Parser.charWhere(_ != '"').rep0.string <* Parser.char('"')
  private val enumDeclaration: Parser[(String, Vector[(String, BigInt)])] = (identifier <* whitespace1) ~
    ((quotedLabel <* whitespace *> Parser.char('=') <* whitespace) ~ integer)
      .repSep(whitespace *> Parser.char(',') <* whitespace).map(_.toList.toVector) <*
    whitespace <* Parser.char(';')

  private val unitHeader: Parser0[(String, String)] =
    (Parser.charWhere(_.isWhitespace).rep0 *> identifier <* Parser.charWhere(_.isWhitespace).rep) ~
      (Parser.char('\'') *> Parser.charWhere(c => c == 'X' || c == '.').rep.string <* Parser.char('\'') <* whitespace)
  private val widthHeader: Parser[Int] =
    (Parser.string("ENCODING") *> whitespace1 *> Parser.string("WIDTH") *> whitespace1 *> decimal <* whitespace <*
      Parser.char(';'))
      .map(_.replace("_", "").toInt)
  private val compoundFields: Parser0[Vector[String]] =
    identifier.repSep0(whitespace *> Parser.char(',') <* whitespace).map(_.toVector)
  private val bankCall: Parser0[(String, String, BigInt)] = {
    val bank2     = Parser.string("ConstBankAddress2").as(BigInt(4))
    val bank0     = Parser.string("ConstBankAddress0").as(BigInt(1))
    val arguments =
      (whitespace *> Parser.char('(') *> whitespace *> identifier <* whitespace <* Parser.char(',') <* whitespace) ~
        (identifier <* whitespace <* Parser.char(')'))
    ((bank2 | bank0) ~ arguments).map { case (factor, (bank, address)) =>
      (bank, address, factor)
    }
  }
  private val tableCell: Parser[TableAtom] = {
    val quotedCharacter = (Parser.char('\'') *> Parser.anyChar <* Parser.char('\'')).map(character =>
      TableAtom.Character(BigInt(character.toInt))
    )
    val typedSymbol = (identifier <* Parser.char('@')) ~ (quotedLabel | identifier)
    val unqualified = (quotedLabel | identifier).map(TableAtom.Unqualified(_))
    integer.backtrack.map(TableAtom.Number(_)) |
      quotedCharacter.backtrack |
      typedSymbol.map { case (table, value) => TableAtom.Symbol(table, value) } |
      (Parser.char('-') <* whitespace1).as(TableAtom.Wildcard) |
      unqualified
  }
  private val tableRow: Parser[ParsedTableRow] = {
    val inputs  = tableCell ~ (whitespace1 *> tableCell).backtrack.rep0
    val comment = (Parser.string("//") *> Parser.charWhere(_ != '\n').rep0).void
    ((inputs <* whitespace <* Parser.string("->") <* whitespace) ~ (tableCell <* whitespace <* comment.?)).map {
      case ((head, tail), output) => ParsedTableRow(head +: tail.toVector, output)
    }
  }

  private case class FloatSelector(
      reference: Option[String],
      table: Option[String],
      value: Option[String],
      constant: Option[Boolean]
  )

  private def expressionParser(
      enums: Map[String, Map[String, BigInt]],
      tableNames: Set[String]
  ): Parser[String => Expr] = Parser.recursive { expression =>
    val signedNumber = integer.map(value => (_: String) => Expr.Constant(value, relaxed = false))
    val star         = Parser.char('*') *> whitespace *>
      (integer.map(value => (_: String) => Expr.Constant(value, relaxed = true)) |
        (identifier ~ (Parser.char('@') *> identifier).?).map { case (name, attribute) =>
          (_: String) => Expr.Reference(name, attribute, relaxed = true)
        })
    val reference = (identifier ~ (Parser.char('@') *> identifier).?).map { case (name, attribute) =>
      (_: String) => Expr.Reference(name, attribute, relaxed = false)
    }
    val enumReference: Parser[(String, String)] =
      (Parser.char('`') *> identifier) ~ (Parser.char('@') *> (quotedLabel | identifier))
    val enumSelector: Parser[FloatSelector] =
      ((identifier <* whitespace <* Parser.string("==") <* whitespace) ~ enumReference).map {
        case (reference, (table, value)) => FloatSelector(Some(reference), Some(table), Some(value), None)
      }
    val constantSelector: Parser[FloatSelector] =
      ((integer <* whitespace <* Parser.string("==") <* whitespace) ~ integer).map { case (left, right) =>
        FloatSelector(None, None, None, Some(left == right))
      }
    val floatSelector                                 = constantSelector.backtrack | enumSelector
    val floatCondition: Parser[Vector[FloatSelector]] =
      (floatSelector ~
        ((whitespace.with1 *> Parser.string("||") <* whitespace) *> floatSelector).backtrack.rep0).map {
        case (head, tail) => head +: tail.toVector
      }
    val comma                                    = whitespace.with1 *> Parser.char(',') <* whitespace
    val conditionalFormat                        = floatCondition ~ (comma *> identifier)
    val floatConvertCall: Parser[String => Expr] =
      (Parser.string("convertFloatType") *> whitespace *> Parser.char('(') *> whitespace *>
        (conditionalFormat ~ (comma *> conditionalFormat).backtrack.rep0 ~ (comma *> identifier)) <*
        whitespace <* Parser.char(')')).map { case ((first, rest), fallback) =>
        val build: String => Expr = (raw: String) => {
          val alternatives     = first +: rest.toVector
          val selectors        = alternatives.flatMap(_._1)
          val dynamicSelectors = selectors.filter(_.constant.isEmpty)
          val selectorNames    = dynamicSelectors.flatMap(selector =>
            selector.reference.zip(selector.table)
          ).distinct
          val unresolved = alternatives.flatMap { case (conditions, _) =>
            conditions.filter(condition =>
              condition.constant.isEmpty &&
                !condition.table.zip(condition.value).exists { case (table, value) =>
                  enums.get(table).exists(_.contains(value))
                }
            )
          }
          if (dynamicSelectors.isEmpty) {
            val selected = alternatives.find(_._1.exists(_.constant.contains(true))).map(_._2).getOrElse(fallback)
            Expr.FixedFloat(Expr.Reference("$value", None, relaxed = false), selected)
          } else if (selectors.exists(_.constant.nonEmpty) || selectorNames.size != 1 || unresolved.nonEmpty)
            Expr.Unsupported(raw)
          else {
            val selector = selectorNames.head._1
            val cases    = alternatives.map { case (conditions, format) =>
              conditions.flatMap(condition =>
                condition.table.zip(condition.value).flatMap { case (table, value) =>
                  enums.get(table).flatMap(_.get(value))
                }
              ).distinct -> format
            }
            Expr.FloatConvert(Expr.Reference("$value", None, relaxed = false), selector, cases, fallback)
          }
        }
        build
      }
    val functionCall: Parser[String => Expr] =
      (identifier ~
        (whitespace *>
          Parser.char('(') *>
          whitespace *> expression.repSep((whitespace *> Parser.char(',') <* whitespace).backtrack) <*
          whitespace <* Parser.char(')')))
        .map { case (name, args) =>
          val build: String => Expr = (raw: String) => {
            val parsedArgs = args.toList.map(_(raw)).toVector
            name match {
              case table if table.startsWith("TABLES_") || tableNames.contains(table) => Expr.Lookup(table, parsedArgs)
              case "IDENTICAL" if parsedArgs.size == 2 => Expr.Identity(parsedArgs(0), parsedArgs(1))
              case _                                   => Expr.Unsupported(raw)
            }
          }
          build
        }
    val relaxedFunctionCall: Parser[String => Expr] = (Parser.char('*') *> whitespace *> functionCall).map { make =>
      val build: String => Expr = (raw: String) => Expr.Relaxed(make(raw))
      build
    }
    val enumConstant = enumReference.map { case (kind, name) =>
      (raw: String) =>
        enums.get(kind).flatMap(_.get(name)).fold[Expr](Expr.Unsupported(raw))(Expr.Constant(_, relaxed = false))
    }
    val relaxedEnum = (Parser.char('*') *> whitespace *> enumConstant).map { make => (raw: String) =>
      make(raw) match {
        case Expr.Constant(value, _) => Expr.Constant(value, relaxed = true)
        case other                   => other
      }
    }
    val atom = relaxedFunctionCall.backtrack | relaxedEnum.backtrack | enumConstant | functionCall.backtrack | star |
      signedNumber | reference
    val valueAtom = (atom ~ (whitespace1 *> floatConvertCall).backtrack.?).map { case (make, conversion) =>
      val build: String => Expr = (raw: String) => {
        val value = make(raw)
        conversion.map(_(raw)) match {
          case Some(Expr.FloatConvert(_, selector, cases, fallback)) =>
            Expr.FloatConvert(value, selector, cases, fallback)
          case Some(Expr.FixedFloat(_, format)) => Expr.FixedFloat(value, format)
          case Some(Expr.Unsupported(source))   => Expr.Unsupported(source)
          case Some(_)                          => Expr.Unsupported(raw)
          case None                             => value
        }
      }
      build
    }
    val transform = whitespace1 *> (Parser.string("SCALE").as(false) | Parser.string("MULTIPLY").as(true)) <*
      whitespace1
    (valueAtom ~ (transform ~ integer).backtrack.rep0).map {
      case (make, factors) =>
        val build: String => Expr = (raw: String) =>
          factors.foldLeft(make(raw)) { case (value, (multiply, factor)) =>
            if (multiply) Expr.Multiply(value, factor) else Expr.Scale(value, factor)
          }
        build
    }
  }

  def parseExpression(
      source: String,
      enums: Map[String, Map[String, BigInt]] = Map.empty,
      tableNames: Set[String] = Set.empty
  ): Expr = {
    val complete = whitespace *> expressionParser(enums, tableNames) <* whitespace
    complete.parseAll(source).fold(_ => Expr.Unsupported(source), parse => parse(source))
  }

  def compile(description: SASSDescription.Description): Compiled = {
    val width = parseWidth(description.preamble).getOrElse {
      throw IllegalArgumentException("FUNIT ENCODING WIDTH is missing")
    }
    // The pinned descriptions cover the legacy 64-bit instruction format, the
    // 88-bit format used by several intermediate architectures, and modern
    // 128-bit instructions. Derive masks from the declared width; don't force
    // legacy descriptions through the current SM89 layout.
    require(Set(64, 88, 128).contains(width), s"unsupported SASS encoding width: $width (expected 64, 88, or 128)")
    val unitMasks    = parseUnitMasks(description.preamble, width)
    val enums        = parseEnums(description.preamble)
    val vocabularies = if (description.preamble.linesIterator.exists(_.trim == "REGISTERS")) {
      SASSSymbols.compile(description).map(vocabulary =>
        vocabulary.name -> vocabulary.values.map(value => value.name -> value.value).toMap
      ).toMap
    } else Map.empty[String, Map[String, BigInt]]
    val tables     = parseTables(description.preamble, enums ++ vocabularies)
    val tableNames = tables.map(_.name).toSet
    val forms      = description.classes.map { instruction =>
      val issues  = Vector.newBuilder[String]
      val opcodes = instruction.section("OPCODES").toVector.flatMap(_.entries).flatMap { entry =>
        parseInteger(entry.value).map(entry.name -> _).orElse {
          issues += s"opcode ${entry.name} has unsupported value ${entry.value}"
          None
        }
      }
      val assignments = instruction.section("ENCODING").toVector.flatMap { section =>
        val encoded = section.entries.map { entry =>
          val positions = unitMasks.get(entry.name)
          if (positions.isEmpty) issues += s"${entry.name} has no matching FUNIT mask"
          val value = parseExpression(entry.value, enums ++ vocabularies, tableNames)
          value match {
            case Expr.Unsupported(source) => issues += s"${entry.name} has unsupported expression: $source"
            case _                        => ()
          }
          expressionIssues(value, tables).foreach(message => issues += s"${entry.name}: $message")
          Assignment(entry.name, positions.getOrElse(Vector.empty), value)
        }
        val opaqueAssignments = section.body.split("\n", -1).toVector.flatMap { line =>
          val text      = line.trim
          val separator = text.indexOf('=')
          Option.when(separator >= 0 && !text.startsWith("//")) {
            val name  = text.substring(0, separator).trim
            val value = text.substring(separator + 1).trim.stripSuffix(";").trim
            (name, value)
          }
        }.filterNot { case (name, _) => section.entries.exists(_.name == name) }.flatMap { case (name, value) =>
          parseCompound(name, value) match {
            case Some((first, second, bank, address, factor)) =>
              Vector(
                first  -> Expr.Reference(bank, None, relaxed = false),
                second -> Expr.Scale(Expr.Reference(address, None, relaxed = false), factor)
              ).map { case (field, fieldExpression) =>
                val positions = unitMasks.get(field)
                if (positions.isEmpty) issues += s"$field has no matching FUNIT mask"
                Assignment(field, positions.getOrElse(Vector.empty), fieldExpression)
              }
            case None =>
              val positions = unitMasks.get(name)
              issues += s"$name has an unsupported compound or malformed assignment"
              if (positions.isEmpty) issues += s"$name has no matching FUNIT mask"
              Vector(Assignment(name, positions.getOrElse(Vector.empty), Expr.Unsupported(value)))
          }
        }
        val unused = section.body.split("\n", -1).toVector.flatMap { line =>
          val text = line.trim
          Option.when(text.startsWith("!") && text.endsWith(";"))(text.drop(1).dropRight(1).trim)
        }.map { name =>
          val positions = unitMasks.get(name)
          if (positions.isEmpty) issues += s"!$name has no matching FUNIT mask"
          Assignment(s"!$name", positions.getOrElse(Vector.empty), Expr.Constant(0, relaxed = true))
        }
        encoded ++ opaqueAssignments ++ unused
      }
      Form(instruction.name, instruction.alternate, opcodes, assignments, issues.result())
    }
    Compiled(width, forms, tables)
  }

  private def parseWidth(source: String): Option[Int] =
    source.split("\n", -1).toVector.flatMap(line => widthHeader.parseAll(line.trim).toOption).headOption

  private def parseUnitMasks(source: String, width: Int): Map[String, Vector[Int]] = {
    val definitions   = source.split("\n", -1).toVector.flatMap(line => unitHeader.parseAll(line).toOption)
    val invalidWidths = definitions.filter(_._2.length != width).map { case (name, mask) =>
      s"$name has mask width ${mask.length}, expected $width"
    }
    require(invalidWidths.isEmpty, invalidWidths.mkString("invalid FUNIT masks: ", "; ", ""))
    val duplicates = definitions.groupBy(_._1).collect { case (name, entries) if entries.size > 1 => name }.toVector
    require(duplicates.isEmpty, duplicates.sorted.mkString("duplicate FUNIT masks: ", ", ", ""))
    definitions.map { case (name, mask) =>
      val positions = mask.indices.filter(index => mask.charAt(mask.length - index - 1) == 'X').toVector
      name -> positions
    }.toMap
  }

  private def parseTables(source: String, symbols: Map[String, Map[String, BigInt]]): Vector[LookupTable] = {
    val lines           = source.split("\n", -1).toVector
    val tableSection    = lines.indexWhere(_.trim == "TABLES")
    val firstNamedTable = lines.indexWhere(_.trim.startsWith("TABLES_"))
    val start           = if (tableSection >= 0) tableSection else firstNamedTable - 1
    val stop            = lines.indexWhere(_.trim == "OPERATION PROPERTIES", start + 1) match {
      case -1 => lines.indexWhere(_.trim.startsWith("CLASS "), start + 1) match {
          case -1    => lines.size
          case found => found
        }
      case found => found
    }

    def resolve(atom: TableAtom): Option[BigInt] = atom match {
      case TableAtom.Number(value)        => Some(value)
      case TableAtom.Character(value)     => Some(value)
      case TableAtom.Wildcard             => None
      case TableAtom.Symbol(table, value) => symbols.get(table).flatMap(_.get(value))
      case TableAtom.Unqualified(value)   =>
        val found = symbols.values.view.flatMap(_.get(value)).toVector.distinct
        Option.when(found.size == 1)(found.head)
    }

    @tailrec def loop(index: Int, result: Vector[LookupTable]): Vector[LookupTable] =
      if (index >= stop || start < 0) result
      else {
        val name = lines(index).trim
        identifier.parseAll(name).toOption match {
          case None            => loop(index + 1, result)
          case Some(tableName) =>
            val rows                            = Vector.newBuilder[(Vector[BigInt], BigInt)]
            val issues                          = Vector.newBuilder[String]
            @tailrec def consume(row: Int): Int =
              if (row >= stop) row
              else {
                val sourceLine = lines(row).trim
                val terminated = sourceLine.contains(";")
                val text       = sourceLine.takeWhile(_ != ';').trim
                if (sourceLine.isEmpty || sourceLine.startsWith("//")) {
                  if (terminated) row + 1 else consume(row + 1)
                } else if (text.isEmpty) row + 1
                else tableRow.parseAll(text) match {
                  case Right(parsed) =>
                    val all      = parsed.inputs :+ parsed.output
                    val resolved = all.map(resolve(_))
                    if (resolved.forall(_.nonEmpty)) {
                      val values = resolved.flatten
                      rows += values.dropRight(1) -> values.last
                    } else {
                      val unresolved = all.zip(resolved).collect { case (atom, None) => atom.toString }
                      issues += s"line ${row + 1}: unsupported symbol(s) ${unresolved.mkString(", ")}: $text"
                    }
                    if (terminated) row + 1 else consume(row + 1)
                  case Left(_) =>
                    issues += s"line ${row + 1}: unsupported table row: $text"
                    if (terminated) row + 1 else consume(row + 1)
                }
              }
            val next = consume(index + 1)
            if (next >= stop && (next == lines.size || !lines.lift(next - 1).exists(_.contains(";")))) {
              issues += s"table $tableName is unterminated"
            }
            val values      = rows.result()
            val arities     = values.map(_._1.size).distinct
            val tableIssues = issues.result() ++ Option.when(arities.size > 1)(
              s"inconsistent row arities: ${arities.mkString(", ")}"
            ).toVector
            loop(next, result :+ LookupTable(tableName, values, tableIssues))
        }
      }
    val parsed     = loop(start + 1, Vector.empty)
    val duplicates = parsed.groupBy(_.name).collect { case (name, values) if values.size > 1 => name }.toVector
    require(duplicates.isEmpty, duplicates.sorted.mkString("duplicate lookup tables: ", ", ", ""))
    parsed
  }

  private def parseEnums(source: String): Map[String, Map[String, BigInt]] =
    source.split("\n", -1).toVector.flatMap(line => enumDeclaration.parseAll(line.trim).toOption)
      .map { case (name, values) => name -> values.toMap }
      .toMap

  private def expressionIssues(expression: Expr, tables: Vector[LookupTable]): Vector[String] = expression match {
    case Expr.Lookup(name, arguments) =>
      val tableIssue = tables.find(_.name == name) match {
        case None        => Vector(s"lookup table $name is undefined")
        case Some(table) =>
          val expected   = table.rows.headOption.map(_._1.size)
          val arityIssue = Option.when(expected.exists(_ != arguments.size))(
            s"lookup table $name expects ${expected.get} arguments, got ${arguments.size}"
          ).toVector
          val tableIssues = Option.when(table.issues.nonEmpty)(
            s"lookup table $name has unsupported rows: ${table.issues.mkString("; ")}"
          ).toVector
          tableIssues ++ arityIssue
      }
      tableIssue ++ arguments.flatMap(expressionIssues(_, tables))
    case Expr.Scale(value, _)              => expressionIssues(value, tables)
    case Expr.Multiply(value, _)           => expressionIssues(value, tables)
    case Expr.Identity(first, second)      => expressionIssues(first, tables) ++ expressionIssues(second, tables)
    case Expr.FloatConvert(value, _, _, _) => expressionIssues(value, tables)
    case Expr.FixedFloat(value, _)         => expressionIssues(value, tables)
    case Expr.Relaxed(value)               => expressionIssues(value, tables)
    case _                                 => Vector.empty
  }

  private def parseCompound(
      name: String,
      value: String
  ): Option[(String, String, String, String, BigInt)] =
    for {
      fields <- compoundFields.parseAll(name).toOption
      if fields.size == 2
      call <- bankCall.parseAll(value).toOption
    } yield (fields(0), fields(1), call._1, call._2, call._3)

  private def parseInteger(source: String): Option[BigInt] = integer.parseAll(source.trim).toOption
}
