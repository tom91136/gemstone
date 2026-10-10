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

  /** A collection expression built by `companion`, written as `companion.empty` when it has no elements. */
  def collection(companion: String, elements: Iterable[String]): String =
    if (elements.isEmpty) s"$companion.empty" else elements.mkString(s"$companion(", ", ", ")")

  /** A C++ namespace of `uint32_t` constants. */
  def cppConstants(namespace: String, values: Iterable[(String, Long)]): String = {
    val fields = values.map { case (name, value) => s"inline constexpr uint32_t $name = ${value}u;" }.mkString("\n")
    s"namespace $namespace {\n$fields\n} // namespace $namespace"
  }
}
