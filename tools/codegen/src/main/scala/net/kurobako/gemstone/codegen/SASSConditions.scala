package net.kurobako.gemstone.codegen

import cats.parse.Parser
import cats.parse.Parser0

/** Structured reader for the boolean conditions used by recovered SASS instruction descriptions. */
object SASSConditions {
  enum Expr {
    case Number(value: BigInt)
    case StringValue(value: String)
    case Field(name: String, attribute: Option[String])
    case Parameter(name: String)
    case Constant(name: String)
    case EnumValue(kind: String, name: String)
    case Unary(operator: String, value: Expr)
    case Binary(operator: String, left: Expr, right: Expr)
    case Call(name: String, arguments: Vector[Expr])
    case Conditional(condition: Expr, whenTrue: Expr, whenFalse: Expr)
  }
  case class Condition(category: String, expression: Expr, message: String)
  case class Definitions(parameters: Map[String, BigInt], constants: Map[String, BigInt])
  case class ParseError(message: String, offset: Int)

  private enum Token {
    case WordToken(value: String)
    case IntegerToken(value: BigInt)
    case TextToken(value: String)
    case EnumToken(kind: String, name: String)
    case ParameterToken(value: String)
    case ConstantToken(value: String)
    case OperatorToken(value: String)
    case PunctuationToken(value: String)
  }
  import Expr.*
  import Token.*

  private val identifier: Parser[String] =
    (Parser.charWhere(c => c.isLetter || c == '_') ~
      Parser.charWhere(c => c.isLetterOrDigit || "_.$".contains(c)).rep0).string
  private val digits: Parser[String]       = Parser.charWhere(_.isDigit).rep.string
  private val hexadecimal: Parser[String]  = Parser.charWhere(c => c.isDigit || "abcdefABCDEF".contains(c)).rep.string
  private val binaryDigits: Parser[String] = Parser.charWhere(c => c == '0' || c == '1').rep.string
  private val operator: Parser[Token]      = Vector("->", "||", "&&", "==", "!=", "<=", ">=", "<<", ">>").map { raw =>
    Parser.string(raw).as(OperatorToken(raw))
  }.reduce(_.orElse(_)).orElse(
    Parser.charWhere(c => "!<>+-%&|*@/^~?".contains(c)).map(c => OperatorToken(c.toString))
  )
  private val quoted: Parser[Token] = Vector('\'', '"').map { quote =>
    val escaped = Parser.char('\\') *> Parser.anyChar.map {
      case 'n' => '\n'
      case 'r' => '\r'
      case 't' => '\t'
      case c   => c
    }
    val plain = Parser.charWhere(c => c != quote && c != '\\')
    (Parser.char(quote) *> escaped.orElse(plain).rep0 <* Parser.char(quote))
      .map(chars => TextToken(chars.mkString))
  }.reduce(_.orElse(_))
  private val quotedValue: Parser[String] = Vector('\'', '"').map { quote =>
    val escaped  = Parser.char('\\') *> Parser.anyChar
    val ordinary = Parser.charWhere(c => c != quote && c != '\\')
    (Parser.char(quote) *> escaped.orElse(ordinary).rep0 <* Parser.char(quote)).map(_.mkString)
  }.reduce(_.orElse(_))
  private val enumName: Parser[String]     = quotedValue.orElse(identifier)
  private val enumReference: Parser[Token] = (Parser.char('`') *> identifier <* Parser.char('@')).flatMap(kind =>
    enumName.map(name => EnumToken(kind, name))
  )
  private val parameter: Parser[Token] = (Parser.char('%') *> identifier).backtrack.map(ParameterToken.apply(_))
  private val constant: Parser[Token]  = (Parser.char('$') *> identifier).map(ConstantToken.apply(_))
  private val number: Parser[Token]    = {
    val hex = (Parser.string("0x") | Parser.string("0X")).backtrack *> hexadecimal
    val bin = (Parser.string("0b") | Parser.string("0B")).backtrack *> binaryDigits
    val dec = digits
    hex.map(v => IntegerToken(BigInt(v, 16))).orElse(bin.map(v => IntegerToken(BigInt(v, 2)))).orElse(dec.map(v =>
      IntegerToken(BigInt(v))
    ))
  }
  private val punctuation: Parser[Token] =
    Parser.charWhere(c => "(),:".contains(c)).map(c => PunctuationToken(c.toString))
  private val word: Parser[Token] = identifier.map(WordToken.apply(_))
  private val atom: Parser[Token] = enumReference.orElse(quoted).orElse(parameter).orElse(constant)
    .orElse(number).orElse(word).orElse(operator).orElse(punctuation)
  private val whitespace: Parser0[Unit]    = Parser.charWhere(_.isWhitespace).rep0.void
  private val tokens: Parser0[List[Token]] = (whitespace.with1 *> atom).backtrack.rep0 <* whitespace

  def parse(source: String): Either[ParseError, Vector[Condition]] =
    tokens.parseAll(source).left.map(error =>
      ParseError(
        s"invalid condition tokens near ${source.slice(error.failedAtOffset, error.failedAtOffset + 80)}",
        error.failedAtOffset
      )
    )
      .flatMap(parseConditions(_))

  def definitions(preamble: String): Either[ParseError, Definitions] = {
    val parameterSection = section(preamble, "PARAMETERS", "CONSTANTS")
    val constantSection  =
      section(preamble, "CONSTANTS", "STRING_MAP").orElse(section(preamble, "CONSTANTS", "REGISTERS"))
    for {
      parameters <- parameterSection.toRight(ParseError("missing PARAMETERS section", 0)).flatMap(parseDefinitions(_))
      constants  <- constantSection.toRight(ParseError("missing CONSTANTS section", 0)).flatMap(parseDefinitions(_))
    } yield Definitions(parameters, constants)
  }

  private val assignment: Parser[(String, BigInt)] = {
    val numberText = ((Parser.string("0x") | Parser.string("0X")).backtrack *> hexadecimal).map(v => BigInt(v, 16))
      .orElse(((Parser.string("0b") | Parser.string("0B")).backtrack *> binaryDigits).map(v => BigInt(v, 2)))
      .orElse(digits.map(v => BigInt(v)))
    (identifier ~ (whitespace *> Parser.char('=') *> whitespace *> numberText)).map { case (left, right) =>
      left -> right
    }
  }

  private def section(source: String, start: String, end: String): Option[String] = {
    val begin = source.indexOf(s"\n$start\n") match {
      case -1 if source.startsWith(s"$start\n") => start.length + 1
      case -1                                   => -1
      case offset                               => offset + start.length + 2
    }
    val finish = if (begin < 0) -1 else source.indexOf(s"\n$end\n", begin)
    Option.when(begin >= 0 && finish >= begin)(source.substring(begin, finish))
  }

  private def parseDefinitions(source: String): Either[ParseError, Map[String, BigInt]] = {
    val parsed = source.linesIterator.map(_.trim).filter(_.nonEmpty).toVector.map { line =>
      assignment.parseAll(line).left.map(error =>
        ParseError(s"invalid numeric definition: $line", error.failedAtOffset)
      )
    }
    parsed.foldLeft[Either[ParseError, Map[String, BigInt]]](Right(Map.empty)) { (state, entry) =>
      for {
        values <- state
        pair   <- entry
      } yield values.updated(pair._1, pair._2)
    }
  }

  private def parseConditions(input: List[Token]): Either[ParseError, Vector[Condition]] = input match {
    case Nil                                                 => Right(Vector.empty)
    case WordToken(category) :: tail if isCategory(category) =>
      expression(tail.toVector, 0).flatMap {
        case (value, PunctuationToken(":") +: TextToken(message) +: rest) =>
          parseConditions(rest.toList).map(Condition(category, value, message) +: _)
        case _ => Left(ParseError(s"malformed $category condition", 0))
      }
    case token :: _ => Left(ParseError(s"expected condition category, found $token", 0))
  }

  private def isCategory(name: String): Boolean = Set("ERROR", "WARNING")(name) || name.endsWith("_ERROR") ||
    name.endsWith("_WARNING")

  private def expression(input: Vector[Token], minimum: Int): Either[ParseError, (Expr, Vector[Token])] =
    prefix(input).flatMap { case (left, remaining) => continue(left, remaining, minimum) }

  private def continue(left: Expr, remaining: Vector[Token], minimum: Int): Either[ParseError, (Expr, Vector[Token])] =
    remaining match {
      case OperatorToken("?") +: tail if minimum == 0 =>
        expression(tail, 0).flatMap {
          case (whenTrue, PunctuationToken(":") +: rest) =>
            expression(rest, 0).flatMap { case (whenFalse, suffix) =>
              continue(Conditional(left, whenTrue, whenFalse), suffix, minimum)
            }
          case _ => Left(ParseError("missing ternary colon", 0))
        }
      case OperatorToken(op) +: tail if precedence(op).exists(_ >= minimum) =>
        val level       = precedence(op).get
        val nextMinimum = if (op == "->") level else level + 1
        expression(tail, nextMinimum).flatMap { case (right, rest) =>
          continue(Binary(op, left, right), rest, minimum)
        }
      case _ => Right(left -> remaining)
    }

  private def prefix(input: Vector[Token]): Either[ParseError, (Expr, Vector[Token])] = input match {
    case IntegerToken(value) +: tail                   => Right(Expr.Number(value) -> tail)
    case TextToken(value) +: tail                      => Right(Expr.StringValue(value) -> tail)
    case EnumToken(kind, name) +: tail                 => Right(Expr.EnumValue(kind, name) -> tail)
    case ParameterToken(name) +: tail                  => Right(Expr.Parameter(name) -> tail)
    case ConstantToken(name) +: tail                   => Right(Expr.Constant(name) -> tail)
    case OperatorToken(op @ ("!" | "-" | "~")) +: tail => expression(tail, 12).map { case (value, rest) =>
        Expr.Unary(op, value) -> rest
      }
    case PunctuationToken("(") +: tail => expression(tail, 0).flatMap {
        case (value, PunctuationToken(")") +: rest) => Right(value -> rest)
        case _                                      => Left(ParseError("unclosed parenthesized expression", 0))
      }
    case WordToken("DEFINED") +: WordToken(name) +: PunctuationToken("(") +: tail =>
      call(name, tail).map { case (value, rest) => Expr.Call("DEFINED", Vector(value)) -> rest }
    case WordToken(name) +: PunctuationToken("(") +: tail                      => call(name, tail)
    case WordToken(name) +: OperatorToken("@") +: WordToken(attribute) +: tail =>
      Right(Expr.Field(name, Some(attribute)) -> tail)
    case WordToken(name) +: tail => Right(Expr.Field(name, None) -> tail)
    case _                       => Left(ParseError(s"expected expression, found ${input.headOption}", 0))
  }

  private def call(name: String, input: Vector[Token]): Either[ParseError, (Expr, Vector[Token])] =
    arguments(input).map { case (values, rest) => Expr.Call(name, values) -> rest }

  private def arguments(input: Vector[Token]): Either[ParseError, (Vector[Expr], Vector[Token])] = input match {
    case PunctuationToken(")") +: tail => Right(Vector.empty -> tail)
    case _                             => expression(input, 0).flatMap {
        case (value, PunctuationToken(",") +: tail) => arguments(tail).map { case (values, rest) =>
            (value +: values) -> rest
          }
        case (value, PunctuationToken(")") +: tail) => Right(Vector(value) -> tail)
        case _                                      => Left(ParseError("malformed function arguments", 0))
      }
  }

  private def precedence(operator: String): Option[Int] = operator match {
    case "->"                    => Some(1)
    case "||"                    => Some(2)
    case "&&"                    => Some(3)
    case "|"                     => Some(4)
    case "^"                     => Some(5)
    case "&"                     => Some(6)
    case "==" | "!="             => Some(7)
    case "<" | "<=" | ">" | ">=" => Some(8)
    case "<<" | ">>"             => Some(9)
    case "+" | "-"               => Some(10)
    case "*" | "%" | "/"         => Some(11)
    case _                       => None
  }
}
