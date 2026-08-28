package net.kurobako.gemstone.codegen

/** Rewrites generated Scala collection type identifiers without touching specification text stored in literals. */
private[codegen] object RuntimeCollections {
  private enum Mode {
    case Code
    case StringLiteral
    case TripleString
    case CharacterLiteral
    case LineComment
    case BlockComment
  }

  private val ColImport       = "import net.kurobako.gemstone.Col"
  private val ExtensionImport = "import net.kurobako.gemstone.Col.*"

  def apply(source: String): String = {
    val output         = new StringBuilder(source.length)
    var index          = 0
    var mode           = Mode.Code
    var blockDepth     = 0
    var replaced       = false
    var usesExtensions = false

    def startsWith(value: String): Boolean = source.startsWith(value, index)
    def append(count: Int): Unit           = {
      output.append(source.substring(index, index + count))
      index += count
    }

    while (index < source.length) mode match {
      case Mode.Code if startsWith("//") =>
        append(2)
        mode = Mode.LineComment
      case Mode.Code if startsWith("/*") =>
        append(2)
        blockDepth = 1
        mode = Mode.BlockComment
      case Mode.Code if startsWith("\"\"\"") =>
        append(3)
        mode = Mode.TripleString
      case Mode.Code if source(index) == '"' =>
        append(1)
        mode = Mode.StringLiteral
      case Mode.Code if source(index) == '\'' =>
        append(1)
        mode = Mode.CharacterLiteral
      case Mode.Code if Character.isJavaIdentifierStart(source(index)) =>
        val start = index
        index += 1
        while (index < source.length && Character.isJavaIdentifierPart(source(index))) index += 1
        source.substring(start, index) match {
          case "Vector" | "List" | "Seq" | "IArray" =>
            output.append("Col")
            replaced = true
          case "toVector" | "toList" | "toSeq" =>
            output.append("toCol")
            replaced = true
            usesExtensions = true
          case identifier => output.append(identifier)
        }
      case Mode.Code => append(1)

      case Mode.StringLiteral if source(index) == '\\' && index + 1 < source.length => append(2)
      case Mode.StringLiteral if source(index) == '"'                               =>
        append(1)
        mode = Mode.Code
      case Mode.StringLiteral => append(1)

      case Mode.TripleString if startsWith("\"\"\"") =>
        append(3)
        mode = Mode.Code
      case Mode.TripleString => append(1)

      case Mode.CharacterLiteral if source(index) == '\\' && index + 1 < source.length => append(2)
      case Mode.CharacterLiteral if source(index) == '\''                              =>
        append(1)
        mode = Mode.Code
      case Mode.CharacterLiteral => append(1)

      case Mode.LineComment if source(index) == '\n' =>
        append(1)
        mode = Mode.Code
      case Mode.LineComment => append(1)

      case Mode.BlockComment if startsWith("/*") =>
        append(2)
        blockDepth += 1
      case Mode.BlockComment if startsWith("*/") =>
        append(2)
        blockDepth -= 1
        if (blockDepth == 0) mode = Mode.Code
      case Mode.BlockComment => append(1)
    }

    val rewritten = output.result()
    if (!replaced) rewritten
    else {
      val packageStart = rewritten.indexOf("package ")
      val packageEnd   = if (packageStart < 0) -1 else rewritten.indexOf('\n', packageStart)
      require(packageEnd >= 0, "generated Scala with runtime collections must declare a package")
      val imports =
        (if (rewritten.contains(ColImport)) "" else ColImport + "\n") +
          (if (!usesExtensions || rewritten.contains(ExtensionImport)) "" else ExtensionImport + "\n")
      rewritten.substring(0, packageEnd + 1) + "\n" + imports + rewritten.substring(packageEnd + 1)
    }
  }
}
