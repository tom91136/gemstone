package net.kurobako.gemstone.codegen

import cats.parse.Parser
import cats.parse.Parser0

/** Lossless structural parser for the display grammar in a SASS FORMAT section. */
object SASSFormat {
  sealed trait Node
  case class Text(raw: String)                   extends Node
  case class Literal(raw: String, value: String) extends Node
  case class Field(
      kind: String,
      binding: String,
      arguments: Option[String],
      default: Option[String],
      suffix: String,
      raw: String
  ) extends Node
  case class Group(open: Char, close: Char, children: Vector[Node])   extends Node
  case class Optional(marker: Option[String], children: Vector[Node]) extends Node

  /** A field, kind or binding name. */
  val identifier: Parser[String]        = SASSConditions.identifierOf("_.$-")
  private val kindName: Parser[String]  = identifier
  private val arguments: Parser[String] =
    (Parser.char('(') *> Parser.charWhere(_ != ')').rep0 <* Parser.char(')')).string
  // `@` is part of several pre-SM70 FORMAT field forms, e.g. Pipe2(HW)@:pipe.
  // Keep it attached to the field rather than degrading the suffix and binding
  // into punctuation text.
  private val fieldSuffix: Parser0[String] = Parser.charWhere(c => c == '*' || c == '@').rep0.string
  private val field: Parser[Field]         =
    ((kindName ~ arguments.? ~ fieldSuffix) ~ (Parser.char(':') *> identifier)).backtrack.withString.map {
      case ((((kind, args), suffix), binding), raw) =>
        val parameters = args.map(_.drop(1).dropRight(1))
        Field(kind, binding, parameters, parameters.flatMap(defaultValue(_)), suffix, raw)
    }

  private val syntax: Parser0[List[Node]] = Parser.recursive[Node] { node =>
    def captured(parser: Parser[Unit], make: String => Node): Parser[Node] =
      parser.withString.map { case (_, raw) => make(raw) }

    def quoted(delimiter: Char): Parser[Node] = {
      val escaped  = Parser.char('\\') *> Parser.anyChar
      val ordinary = Parser.charWhere(c => c != delimiter && c != '\\')
      val contents = (escaped | ordinary).rep0.string
      (Parser.char(delimiter) *> contents <* Parser.char(delimiter)).withString.map { case (value, raw) =>
        Literal(raw, value)
      }
    }

    def group(open: Char, close: Char): Parser[Node] =
      (Parser.char(open) *> node.repUntil0(Parser.char(close)) <* Parser.char(close)).map(children =>
        Group(open, close, children.toVector)
      )

    val optional = (Parser.string("$(") *> node.repUntil0(Parser.string(")$")) <* Parser.string(")$"))
      .map(children => Optional(marker(children.toVector), children.toVector))
    val comment     = captured(Parser.string("//") *> Parser.charWhere(_ != '\n').rep.void, Text.apply(_))
    val whitespace  = captured(Parser.charWhere(_.isWhitespace).rep.void, Text.apply(_))
    val literal     = quoted('"').orElse(quoted('\''))
    val punctuation = Parser.charWhere(c => !c.isWhitespace && !"()[]{}\"'".contains(c)).map(c => Text(c.toString))
    val atom        = field.orElse(optional).orElse(literal).orElse(comment)
      .orElse(group('(', ')')).orElse(group('[', ']')).orElse(group('{', '}'))
      .orElse(whitespace).orElse(punctuation)
    atom
  }.rep0

  def parse(source: String): Either[ParseError, Vector[Node]] =
    syntax.parseAll(source).left.map(error => ParseError("invalid FORMAT syntax", error.failedAtOffset)).map(nodes =>
      coalesce(nodes.toVector)
    )

  private def firstQuoted(value: String): Option[String] = {
    val first = value.indexWhere(c => c == '\'' || c == '"')
    Option.when(first >= 0) {
      val quote = value(first)
      val end   = value.indexOf(quote, first + 1)
      if (end < 0) "" else value.substring(first + 1, end)
    }
  }

  private def defaultValue(value: String): Option[String] =
    firstQuoted(value).orElse {
      val parts = value.split("/", 2)
      if (parts.length == 2) Some(parts(1))
      else Option.when(parts(0).headOption.exists(c => c.isLetter || c == '_'))(parts(0))
    }

  private def marker(nodes: Vector[Node]): Option[String] = {
    def rendered(node: Node): String = node match {
      case Text(raw)             => raw
      case Literal(_, value)     => value
      case Field(raw = raw)      => raw
      case Group(_, _, children) => children.map(rendered(_)).mkString
      case Optional(_, children) => children.map(rendered(_)).mkString
    }
    val raw = nodes.map(rendered(_)).mkString
    Vector("'&'", "'?'", "&", "?").find(raw.contains(_))
  }

  private def coalesce(nodes: Vector[Node]): Vector[Node] = nodes.foldLeft(Vector.empty[Node]) {
    case (acc, Text(raw)) => acc.lastOption match {
        case Some(Text(previous)) => acc.updated(acc.size - 1, Text(previous + raw))
        case _                    => acc :+ Text(raw)
      }
    case (acc, node: Group)    => acc :+ node.copy(children = coalesce(node.children))
    case (acc, node: Optional) => acc :+ node.copy(children = coalesce(node.children))
    case (acc, node)           => acc :+ node
  }
}
