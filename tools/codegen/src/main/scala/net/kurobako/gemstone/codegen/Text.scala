package net.kurobako.gemstone.codegen

private[codegen] object Text {
  def normalizeWhitespace(value: String): String = {
    val result = StringBuilder()
    value.foreach { character =>
      if (asciiWhitespace(character)) {
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
    nonEmptyWords(value)(character => asciiWhitespace(character) || separators(character))

  def identifierWords(value: String): Vector[String] =
    nonEmptyWords(value)(character => !asciiLetterOrDigit(character))

  def splitDelimited(value: String, separator: Char): Vector[String] = {
    val result  = Vector.newBuilder[String]
    val current = StringBuilder()
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
    value.length > 1 && value.head == prefix && value.tail.forall(asciiDigit(_))

  def signedInteger(value: String): Boolean = {
    val digits = withoutLeadingSign(value)
    digits.nonEmpty && digits.forall(asciiDigit(_))
  }

  def startsDecimalNumber(value: String): Boolean = {
    val unsigned = withoutLeadingSign(value)
    val integral = unsigned.takeWhile(asciiDigit(_))
    val fraction = unsigned.drop(integral.length) match {
      case s".$digits" => digits.takeWhile(asciiDigit(_))
      case _           => ""
    }
    integral.nonEmpty && fraction.nonEmpty
  }

  private def withoutLeadingSign(value: String): String = value match {
    case s"+$rest" => rest
    case s"-$rest" => rest
    case unsigned  => unsigned
  }

  private def nonEmptyWords(value: String)(separator: Char => Boolean): Vector[String] = {
    val result  = Vector.newBuilder[String]
    val current = StringBuilder()
    value.foreach { character =>
      if (separator(character)) {
        if (current.nonEmpty) {
          result += current.result()
          current.clear()
        }
      } else {
        val _ = current.append(character)
      }
    }
    if (current.nonEmpty) result += current.result()
    result.result()
  }

  private def asciiWhitespace(character: Char): Boolean =
    character == ' ' || character >= '\t' && character <= '\r'

  private def asciiLetterOrDigit(character: Char): Boolean =
    character >= 'A' && character <= 'Z' || character >= 'a' && character <= 'z' || asciiDigit(character)

  private def asciiDigit(character: Char): Boolean = character >= '0' && character <= '9'
}
