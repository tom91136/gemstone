package net.kurobako.gemstone.codegen

import scala.collection.mutable
object Text {
  def normaliseWhitespace(value: String): String = {
    val result = mutable.StringBuilder()
    value.foreach { character =>
      if (character.isWhitespace) {
        if (result.nonEmpty && result.last != ' ') {
          val _ = result.append(' ')
        }
      } else {
        val _ = result.append(character)
      }
    }
    result.result().stripSuffix(" ")
  }

  def stripLineComment(value: String): String = value.indexOf("//") match {
    case -1    => value.trim
    case index => value.take(index).trim
  }

  def stripLeadingSignGroup(value: String): String = value match {
    case s"{$sign}$rest" if sign.length == 1 && "!+-".contains(sign.head) => rest
    case other                                                            => other
  }

  def splitWords(value: String, separators: Set[Char] = Set.empty): Vector[String] =
    nonEmptyWords(value)(character => character.isWhitespace || separators(character))

  def identifierWords(value: String): Vector[String] =
    nonEmptyWords(value)(character => !character.isLetterOrDigit)

  def splitDelimited(value: String, separator: Char): Vector[String] = {
    val result  = Vector.newBuilder[String]
    val current = mutable.StringBuilder()
    value.foreach { character =>
      if (character == separator) {
        result += current.result()
        current.clear()
      } else {
        val _ = current.append(character)
      }
    }
    result += current.result()
    result.result()
  }

  def registerToken(value: String, prefix: Char): Boolean =
    value.length > 1 && value.head == prefix && value.tail.forall(_.isDigit)

  def signedInteger(value: String): Boolean = {
    val digits = withoutLeadingSign(value)
    digits.nonEmpty && digits.forall(_.isDigit)
  }

  def startsDecimalNumber(value: String): Boolean = {
    val unsigned = withoutLeadingSign(value)
    val integral = unsigned.takeWhile(_.isDigit)
    val fraction = unsigned.drop(integral.length) match {
      case s".$digits" => digits.takeWhile(_.isDigit)
      case _           => ""
    }
    integral.nonEmpty && fraction.nonEmpty
  }

  private def withoutLeadingSign(value: String): String = value match {
    case s"+$rest" => rest
    case s"-$rest" => rest
    case unsigned  => unsigned
  }

  private inline def nonEmptyWords(value: String)(inline separator: Char => Boolean): Vector[String] = {
    val result  = Vector.newBuilder[String]
    val current = mutable.StringBuilder()
    value.foreach { character =>
      if (separator(character)) {
        if (current.nonEmpty) { result += current.result(); current.clear() }
      } else {
        val _ = current.append(character)
      }
    }
    if (current.nonEmpty) result += current.result()
    result.result()
  }
}
