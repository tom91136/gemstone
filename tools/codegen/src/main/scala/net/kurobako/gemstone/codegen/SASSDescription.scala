package net.kurobako.gemstone.codegen

import cats.parse.Parser
import cats.parse.Parser0
import scala.annotation.tailrec

/** Lossless structural reader for the description language embedded in nvdisasm.
  *
  * This is deliberately a reader, not an evaluator: encoding expressions and constraints are retained verbatim so later
  * code can interpret them without losing syntax that this version does not understand.
  */
object SASSDescription {
  case class ParseError(message: String, offset: Int)

  case class Description(
      architecture: Option[String],
      preamble: String,
      globals: Vector[Section],
      classes: Vector[InstructionClass],
      trailing: String
  )

  case class InstructionClass(
      name: String,
      alternate: Boolean,
      conditional: Boolean,
      raw: String,
      sections: Vector[Section]
  ) {
    def section(name: String): Option[Section] = sections.find(_.name == name)
    def fields: Vector[FormatField]            = section("FORMAT").toVector.flatMap(_.fields)
    def opcodes: Vector[String]                = section("OPCODES").toVector.flatMap(_.entries.map(_.name))
  }

  case class Section(name: String, body: String, raw: String, entries: Vector[Entry]) {
    lazy val fields: Vector[FormatField] = if (name == "FORMAT") SASSDescription.fields(body) else Vector.empty
  }

  case class Entry(name: String, value: String, raw: String, start: Int, end: Int)
  case class FormatField(name: String, kind: String, raw: String)

  private enum LexemeKind { case Word, Quoted, Comment, Group, Space, Symbol }
  private case class Lexeme(
      raw: String,
      kind: LexemeKind,
      start: Int = 0,
      end: Int = 0,
      children: Vector[Lexeme] = Vector.empty
  )
  private case class Line(start: Int, end: Int, contentEnd: Int, text: String)
  private case class Header(start: Int, end: Int, name: String, alternate: Boolean)

  // cats-parse handles the nested token grammar; exact token slices are retained.
  private val identifier: Parser[String] =
    (Parser.charWhere(c => c.isLetter || c == '_') ~ Parser.charWhere(c => c.isLetterOrDigit || c == '_').rep0).string
  private val bindingName: Parser[String] =
    (Parser.charWhere(c => c.isLetter || c == '_') ~
      Parser.charWhere(c => c.isLetterOrDigit || "_.$-".contains(c)).rep0).string
  private val formatBinding: Parser[String] = (Parser.char(':') ~ bindingName).map(_._2)

  private val sassLexer: Parser0[List[Lexeme]] = Parser.recursive[Lexeme] { token =>
    def captured(parser: Parser[Any], kind: LexemeKind): Parser[Lexeme] =
      parser.withString.map { case (_, raw) => Lexeme(raw, kind) }

    def quoted(delimiter: Char): Parser[Lexeme] = {
      val escaped  = (Parser.char('\\') ~ Parser.anyChar).void
      val ordinary = Parser.charWhere(c => c != delimiter && c != '\\').void
      val content  = escaped.orElse(ordinary)
      captured(
        Parser.char(delimiter) *> content.repUntil0(Parser.char(delimiter)).void <* Parser.char(delimiter),
        LexemeKind.Quoted
      )
    }

    def group(open: Char, close: Char): Parser[Lexeme] =
      (Parser.char(open) *> token.repUntil0(Parser.char(close)) <* Parser.char(close)).withString.map {
        case (children, raw) => Lexeme(raw, LexemeKind.Group, children = children.toVector)
      }

    val comment = captured(Parser.string("//") *> Parser.charWhere(_ != '\n').rep0, LexemeKind.Comment)
    val space   = captured(Parser.charWhere(_.isWhitespace).rep, LexemeKind.Space)
    val word    = captured(identifier, LexemeKind.Word)
    val symbols = Parser.charWhere(c => !c.isWhitespace && !"()[]{}\"'".contains(c))
    val symbol  = captured(symbols, LexemeKind.Symbol)

    quoted('"')
      .orElse(quoted('\''))
      .orElse(comment)
      .orElse(group('(', ')'))
      .orElse(group('[', ']'))
      .orElse(group('{', '}'))
      .orElse(space)
      .orElse(word)
      .orElse(symbol)
  }.rep0

  def parse(source: String): Either[ParseError, Description] =
    lex(source).flatMap { tokens =>
      val topLevel     = tokens.view.filter(t => t.children.isEmpty).map(t => t.start -> t).toMap
      val lines        = splitLines(source)
      val headers      = lines.flatMap(classHeader(_, topLevel))
      val headerStarts = headers.map(_.start).toSet
      val (depth, conditional, directiveError) =
        lines.foldLeft((0, Map.empty[Int, Boolean], Option.empty[ParseError])) {
          case ((depth, active, error), line) =>
            val marked  = if (headerStarts(line.start)) active.updated(line.start, depth > 0) else active
            val trimmed = line.text.trim
            val offset  = line.start + line.text.indexOf(trimmed)
            if (!topLevel.get(offset).exists(_.raw == "#")) (depth, marked, error)
            else trimmed match {
              case s"##IF $guard" if guard.nonEmpty => (depth + 1, marked, error)
              case "##IF"                  => (depth, marked, Some(ParseError("missing ##IF condition", offset)))
              case "##ENDIF" if depth == 0 => (depth, marked, Some(ParseError("unmatched ##ENDIF", offset)))
              case "##ENDIF"               => (depth - 1, marked, error)
              case "##ELSE" if depth == 0  => (depth, marked, Some(ParseError("unmatched ##ELSE", offset)))
              case _                       => (depth, marked, error)
            }
        }
      val error    = if (depth != 0) Some(ParseError("unterminated ##IF", source.length)) else directiveError
      val archLine = lines.collectFirst {
        case line if line.text.trim.startsWith("ARCHITECTURE ") => line
      }
      val arch = archLine.flatMap { line =>
        val keyword = line.start + line.text.indexOf(line.text.trim)
        Option.when(topLevel.get(keyword).exists(_.raw == "ARCHITECTURE")) {
          line.text.trim match {
            case s"ARCHITECTURE \"$name\"" => name
            case _                         => ""
          }
        }
      }.filter(_.nonEmpty)
      val firstClass = headers.headOption.map(_.start).getOrElse(source.length)
      val preamble   = source.substring(0, firstClass)
      val classes    = headers.zipWithIndex.map { case (header, i) =>
        val end = headers.lift(i + 1).map(_.start).getOrElse(source.length)
        val raw = source.substring(header.start, end)
        InstructionClass(
          header.name,
          header.alternate,
          conditional(header.start),
          raw,
          parseSections(raw, header.end - header.start)
        )
      }
      val globals = parseGlobalSections(preamble)
      error.toLeft(Description(arch, preamble, globals, classes, ""))
    }

  private def lex(source: String): Either[ParseError, Vector[Lexeme]] = {
    def position(values: Vector[Lexeme], start: Int): Vector[Lexeme] =
      values.scanLeft(start)((offset, value) => offset + value.raw.length).zip(values).map { case (offset, value) =>
        value.copy(start = offset, end = offset + value.raw.length, children = position(value.children, offset + 1))
      }
    sassLexer.parseAll(source).left.map(error =>
      ParseError("malformed or unbalanced SASS description", error.failedAtOffset)
    ).map(values => position(values.toVector, 0))
  }

  private def splitLines(source: String): Vector[Line] = {
    val lines = source.split("\n", -1).toVector
    lines.scanLeft(0)((offset, line) => offset + line.length + 1).zip(lines).map { case (start, line) =>
      val text = line.stripSuffix("\r")
      Line(start, (start + line.length + 1).min(source.length), start + text.length, text)
    }
  }

  private def classHeader(line: Line, tokens: Map[Int, Lexeme]): Option[Header] = {
    val trimmed = line.text.trim
    val parsed  = trimmed match {
      case s"ALTERNATE CLASS \"$name\"" => Some((true, name))
      case s"CLASS \"$name\""           => Some((false, name))
      case _                            => None
    }
    parsed.flatMap { case (alternate, name) =>
      val offset  = line.start + line.text.indexOf(trimmed)
      val keyword = if (alternate) "ALTERNATE" else "CLASS"
      Option.when(name.nonEmpty && tokens.get(offset).exists(t => t.kind == LexemeKind.Word && t.raw == keyword))(
        Header(line.start, line.contentEnd, name, alternate)
      )
    }
  }

  private val classSectionNames = Set(
    "FORMAT",
    "PREDICATES",
    "OPCODES",
    "ENCODING",
    "REMAP",
    "CONDITIONS",
    "CONDITION",
    "PROPERTIES"
  )
  private val globalSectionNames = Set(
    "RELOCATORS",
    "OPTIONS",
    "PARAMETERS",
    "CONSTANTS",
    "REGISTERS",
    "INSTRUCTIONS",
    "SUBCLASSES"
  )

  private def parseSections(raw: String, headerEnd: Int): Vector[Section] = {
    val tokens      = lex(raw).fold(error => throw IllegalArgumentException(error.toString), identity)
    val wordOffsets = tokens.view.filter(_.kind == LexemeKind.Word).map(t => t.start -> t.raw).toMap
    val sections    = splitLines(raw).filter(_.start >= headerEnd).flatMap { line =>
      val t     = line.text.trim
      val found = classSectionNames.find(n => t == n || t.startsWith(n + " "))
      found.filter(n => wordOffsets.get(line.start + line.text.indexOf(t)).contains(n)).map { n =>
        val relative = line.text.indexOf(n)
        (line.start, line.start + relative + n.length, n)
      }
    }
    buildSections(raw, sections)
  }

  private def parseGlobalSections(raw: String): Vector[Section] = {
    val tokens      = lex(raw).fold(error => throw IllegalArgumentException(error.toString), identity)
    val wordOffsets = tokens.view.filter(_.kind == LexemeKind.Word).map(t => t.start -> t.raw).toMap
    val sections    = splitLines(raw).flatMap { line =>
      val t = line.text.trim
      globalSectionNames.find(n => t == n || t.startsWith(n + " "))
        .filter(n => wordOffsets.get(line.start + line.text.indexOf(t)).contains(n))
        .map { n =>
          val relative = line.text.indexOf(n)
          (line.start, line.start + relative + n.length, n)
        }
    }
    buildSections(raw, sections)
  }

  private def buildSections(raw: String, starts: Vector[(Int, Int, String)]): Vector[Section] =
    starts.zipWithIndex.map { case ((start, bodyStart, name), i) =>
      val end  = starts.lift(i + 1).map(_._1).getOrElse(raw.length)
      val body = raw.substring(bodyStart, end)
      Section(name, body, raw.substring(start, end), parseEntries(body))
    }

  private def parseEntries(body: String): Vector[Entry] = {
    val tokens = lex(body).fold(error => throw IllegalArgumentException(error.toString), identity)
    val ends   = tokens.collect { case token if token.kind == LexemeKind.Symbol && token.raw == ";" => token.end } :+
      body.length
    (0 +: ends.dropRight(1)).zip(ends).flatMap { case (start, end) => parseEntry(body, start, end) }
  }

  private def parseEntry(body: String, from: Int, until: Int): Option[Entry] = {
    val raw         = body.substring(from, until)
    val tokens      = lex(raw).fold(error => throw IllegalArgumentException(error.toString), identity)
    val significant = tokens.filterNot(t => t.kind == LexemeKind.Space || t.kind == LexemeKind.Comment)
    val equalsIndex = significant.indexWhere(t => t.kind == LexemeKind.Symbol && t.raw == "=")
    Option.when(equalsIndex > 0)(equalsIndex).flatMap { index =>
      val nameTokens = significant.take(index)
      val name       = if (nameTokens.size == 1) parseName(nameTokens.head)
      else bindingName.parseAll(raw.substring(nameTokens.head.start, nameTokens.last.end)).toOption
      name.map(value => Entry(value, raw.substring(significant(index).end).trim.stripSuffix(";"), raw, from, until))
    }
  }

  private def parseName(token: Lexeme): Option[String] = token.kind match {
    case LexemeKind.Word                            => identifier.parseAll(token.raw).toOption
    case LexemeKind.Quoted if token.raw.length >= 2 => Some(token.raw.substring(1, token.raw.length - 1))
    case _                                          => None
  }

  private def fields(format: String): Vector[FormatField] = {
    def bindings(tokens: Vector[Lexeme]): Vector[(Int, FormatField)] =
      tokens.zipWithIndex.flatMap { case (colon, index) =>
        val binding = Option.when(colon.kind == LexemeKind.Symbol && colon.raw == ":") {
          val suffix = tokens.drop(index + 1).takeWhile(t =>
            t.kind == LexemeKind.Word ||
              (t.kind == LexemeKind.Symbol && "_.$-".contains(t.raw))
          )
          val end  = suffix.lastOption.map(_.end).getOrElse(colon.end)
          val name = formatBinding.parseAll(format.substring(colon.start, end)).fold(
            _ =>
              throw IllegalArgumentException(
                s"invalid FORMAT binding at ${colon.start}: ${format.slice((colon.start - 60).max(0), (colon.start + 60).min(format.length))}"
              ),
            identity
          )
          @tailrec def beforeType(i: Int): Int =
            if (
              i >= 0 &&
              (tokens(i).kind == LexemeKind.Space ||
                (tokens(i).kind == LexemeKind.Symbol && Set("*", "@")(tokens(i).raw)))
            ) beforeType(i - 1)
            else i
          val before    = beforeType(index - 1)
          val typeIndex =
            if (before >= 0 && tokens(before).kind == LexemeKind.Group && tokens(before).raw.startsWith("("))
              beforeType(before - 1)
            else before
          require(
            typeIndex >= 0 && tokens(typeIndex).kind == LexemeKind.Word,
            s"missing FORMAT binding type at ${colon.start}"
          )
          @tailrec def firstType(i: Int): Int =
            if (
              i >= 2 && tokens(i - 1).raw == "." && tokens(i - 2).kind == LexemeKind.Word &&
              tokens(i - 2).end == tokens(i - 1).start && tokens(i - 1).end == tokens(i).start
            ) firstType(i - 2)
            else i
          val start = tokens(firstType(typeIndex)).start
          colon.start -> FormatField(name, format.substring(start, tokens(typeIndex).end), format.substring(start, end))
        }
        binding.toVector ++ bindings(colon.children)
      }
    val tokens = lex(format).fold(error => throw IllegalArgumentException(error.toString), identity)
    val end    = tokens.indexWhere(t => t.kind == LexemeKind.Symbol && t.raw == ";")
    require(end >= 0, "unterminated FORMAT declaration")
    bindings(tokens.take(end)).sortBy(_._1).map(_._2).distinct
  }
}
