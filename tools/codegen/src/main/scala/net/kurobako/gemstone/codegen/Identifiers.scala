package net.kurobako.gemstone.codegen

object Identifiers {
  def isLowerSnake(value: String, prefix: String): Boolean =
    value.startsWith(prefix) && value.length > prefix.length &&
      value.drop(prefix.length).forall(character => character.isLower || character.isDigit || character == '_')

  def isUpperCamelAfter(value: String, prefix: String): Boolean =
    value.startsWith(prefix) && value.drop(prefix.length).headOption.exists(_.isUpper) &&
      value.drop(prefix.length).forall(character => character.isLetterOrDigit || character == '_')

  def sanitized(value: String): String = {
    val result = StringBuilder()
    value.foreach { character =>
      if (character.isLetterOrDigit) {
        val _ = result.append(character)
      } else if (result.lastOption.forall(_ != '_')) {
        val _ = result.append('_')
      }
    }
    result.result().stripSuffix("_")
  }

  /** UpperCamelCases a value from its identifier words, applying a fallback and a leading-digit prefix. */
  def upperCamel(value: String, fallback: String, digitPrefix: String): String = {
    val result = Text.identifierWords(value).map(word => s"${word.head.toUpper}${word.tail}").mkString
    if (result.isEmpty) fallback else if (result.head.isDigit) s"$digitPrefix$result" else result
  }
}
