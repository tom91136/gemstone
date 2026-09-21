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
}
