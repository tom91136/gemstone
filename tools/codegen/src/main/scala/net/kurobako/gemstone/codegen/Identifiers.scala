package net.kurobako.gemstone.codegen

object Identifiers {

  /** UpperCamelCases a value from its identifier words, applying a fallback and a leading-digit prefix. */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def upperCamel(value: String, fallback: String, digitPrefix: String): String = {
    val result = Text.identifierWords(value).map(word => s"${word.head.toUpper}${word.tail}").mkString
    if (result.isEmpty) fallback else if (result.head.isDigit) s"$digitPrefix$result" else result
  }
}
