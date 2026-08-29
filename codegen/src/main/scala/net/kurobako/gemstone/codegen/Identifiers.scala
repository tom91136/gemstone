package net.kurobako.gemstone.codegen

private[gemstone] object Identifiers {
  def isC(value: String): Boolean =
    value.headOption.exists(character => character.isLetter || character == '_') &&
      value.tail.forall(character => character.isLetterOrDigit || character == '_')

  def startsWithOneOf(value: String, prefixes: String*): Boolean =
    prefixes.exists(value.startsWith) && value.forall(character => character.isLetterOrDigit || character == '_')

  def isLowerSnake(value: String, prefix: String): Boolean =
    value.startsWith(prefix) && value.length > prefix.length &&
      value.drop(prefix.length).forall(character => character.isLower || character.isDigit || character == '_')

  def isUpperCamelAfter(value: String, prefix: String): Boolean =
    value.startsWith(prefix) && value.drop(prefix.length).headOption.exists(_.isUpper) &&
      value.drop(prefix.length).forall(character => character.isLetterOrDigit || character == '_')
}
