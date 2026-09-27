package net.kurobako.gemstone.codegen

/** Source-emission helpers shared by the generated-source writers. */
object Emission {

  /** Escapes a value for a double-quoted Scala string literal. */
  def escape(value: String): String = value.flatMap {
    case '\\'                         => "\\\\"
    case '"'                          => "\\\""
    case '\n'                         => "\\n"
    case '\r'                         => "\\r"
    case '\t'                         => "\\t"
    case character if character < ' ' => f"\\u${character.toInt}%04x"
    case character                    => character.toString
  }

  def literal(value: String): String = "\"" + escape(value) + "\""

  def nonEmptyString(value: String): String = s"NonEmptyString(${literal(value)})"

  def nonEmptyVector(values: Vector[String]): String =
    values.map(value => nonEmptyString(value)).mkString("NonEmptyVec.unsafeFromVec(Vec(", ", ", "))")
}
