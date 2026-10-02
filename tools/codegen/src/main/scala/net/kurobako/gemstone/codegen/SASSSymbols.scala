package net.kurobako.gemstone.codegen

import cats.parse.Parser
import cats.parse.Parser0

/** Register and qualifier vocabularies, including the implicit enumeration and range syntax. */
object SASSSymbols {
  case class Value(name: String, value: BigInt, default: Boolean = false)
  case class Vocabulary(name: String, values: Vector[Value], default: Option[BigInt] = None)
  private case class Entry(
      name: String,
      range: Option[(Int, Int)],
      value: Option[(BigInt, BigInt)],
      default: Boolean
  )

  private val whitespace: Parser0[Unit] =
    (Parser.charWhere(_.isWhitespace).void | (Parser.string("//") *> Parser.charWhere(_ != '\n').rep0).void).rep0.void
  private val word: Parser[String] =
    (Parser.charWhere(c => c.isLetter || c == '_') ~
      Parser.charWhere(c => c.isLetterOrDigit || c == '_' || c == '.').rep0).string
  private val quoted: Parser[String] = Parser.char('"') *> Parser.charWhere(_ != '"').rep0.string <* Parser.char('"')
  private val name: Parser[String]   = quoted | word
  private val number: Parser[BigInt] = {
    val hex = (Parser.string("0x") *> Parser.charWhere(c => c.isDigit || "abcdefABCDEF_".contains(c)).rep.string)
      .map(raw => BigInt(raw.replace("_", ""), 16))
    val binary = (Parser.string("0b") *> Parser.charWhere(c => "01_".contains(c)).rep.string)
      .map(raw => BigInt(raw.replace("_", ""), 2))
    val decimal = Parser.charWhere(_.isDigit).rep.string.map(BigInt(_))
    (Parser.char('-').?.with1 ~ (hex.backtrack | binary.backtrack | decimal)).map { case (sign, value) =>
      if (sign.nonEmpty) -value else value
    }
  }
  private val range: Parser[(BigInt, BigInt)] =
    Parser.char('(') *> whitespace *> ((number <* whitespace <* Parser.string("..") <* whitespace) ~ number) <*
      whitespace <* Parser.char(')')
  private val entry: Parser[Entry] =
    ((name <* whitespace) ~ (range <* whitespace).? ~ (Parser.char('*') <* whitespace).? ~
      (Parser.char('=') *> whitespace *> (range | number.map(value => value -> value)) <* whitespace).?).map {
      case (((name, indices), default), assigned) =>
        Entry(
          name,
          indices.map { case (first, last) =>
            require(
              first.isValidInt && last.isValidInt && last >= first && last - first < 65536,
              "invalid SASS symbol range"
            )
            first.toInt -> last.toInt
          },
          assigned,
          default.nonEmpty
        )
    }
  private val declaration: Parser0[Vocabulary] = (whitespace *> word <* whitespace).with1.flatMap { kind =>
    (entry.repSep(whitespace *> Parser.char(',') <* whitespace) <* whitespace).map { entries =>
      val (_, values) = entries.toList.foldLeft((BigInt(0), Vector.empty[Value])) { case ((next, values), entry) =>
        val names = entry.range.fold(Vector(entry.name)) { case (first, last) =>
          (first to last).toVector.map(index => s"${entry.name}$index")
        }
        val first = entry.value.fold(next)(_._1)
        entry.value.foreach { case (low, high) =>
          require(low == high || high - low + 1 == names.size, s"inconsistent SASS symbol range ${entry.name}")
        }
        val added = names.zipWithIndex.map { case (name, index) =>
          Value(name, first + index, entry.default)
        }
        (first + names.size, values ++ added)
      }
      Vocabulary(
        kind,
        values,
        values.filter(_.default).map(_.value).distinct match {
          case Vector(value) => Some(value)
          case _             => None
        }
      )
    }
  }

  def parse(source: String): Either[String, Vocabulary] =
    declaration.parseAll(source.stripSuffix(";")).left.map(error =>
      s"invalid SASS vocabulary at ${error.failedAtOffset}: ${source.take(120)}"
    )

  private val union: Parser0[(String, Vector[String])] =
    ((whitespace *> word <* whitespace <* Parser.char('=') <* whitespace) ~
      word.repSep(whitespace *> Parser.char('+') <* whitespace).map(_.toList.toVector)) <* whitespace

  def compile(description: SASSDescription.Description): Vector[Vocabulary] = {
    val lines = description.preamble.linesIterator.toVector
    val start = lines.indexWhere(_.trim == "REGISTERS")
    val end   = lines.indexWhere(_.trim == "TABLES", start + 1)
    require(start >= 0 && end > start, "missing SASS register vocabulary section")
    lines.slice(start + 1, end).mkString("\n").split(';').toVector.filter(_.trim.nonEmpty)
      .foldLeft(Vector.empty[Vocabulary]) { (result, source) =>
        val vocabulary = union.parseAll(source).toOption match {
          case Some((name, references)) =>
            val available = result.map(value => value.name -> value.values).toMap
            val values    = references.flatMap(reference =>
              available.getOrElse(
                reference,
                throw IllegalArgumentException(s"undefined SASS vocabulary $reference in $name")
              )
            ).distinct
            Vocabulary(
              name,
              values,
              values.filter(_.default).map(_.value).distinct match {
                case Vector(value) => Some(value)
                case _             => None
              }
            )
          case None => parse(source).fold(error => throw IllegalArgumentException(error), identity)
        }
        result :+ vocabulary
      }
  }
}
