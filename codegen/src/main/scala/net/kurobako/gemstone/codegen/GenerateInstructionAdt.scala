package net.kurobako.gemstone.codegen

/** Renders one concrete Scala case class per canonical ISA opcode without multiplying architecture or modifier forms.
  */
private[codegen] object GenerateInstructionAdt {
  final case class Entry(key: String, name: String)

  final case class Shape(
      packageName: String,
      sourceDescription: String,
      syntaxType: String,
      argumentsType: String,
      opcodeType: String,
      keyExpression: String,
      argumentsExpression: String,
      opcodeExpression: String => String
  )

  def render(opcodes: Vector[String], shape: Shape): String =
    renderEntries(opcodes.distinct.map(value => Entry(value, value)), shape)

  def renderEntries(entries: Vector[Entry], shape: Shape): String = {
    val unique = entries.distinct.sortBy(entry => (entry.name, entry.key))
    require(unique.map(_.key).distinct.size == unique.size, "instruction decoder keys must be unique")
    val named      = caseNames(unique)
    val chunkCount = Math.max(1, (named.size + 31) / 32)
    val chunks     = named.groupBy { case (entry, _) => Math.floorMod(entry.key.hashCode, chunkCount) }
    val cases      = named.map { case (entry, caseName) =>
      s"""|  final case class $caseName(arguments: ${shape.argumentsType}) extends KnownInstruction {
          |    override val opcode: ${shape.opcodeType} = ${shape.opcodeExpression(entry.name)}
          |    override val name: String = ${literal(entry.name)}
          |  }""".stripMargin
    }
    val decoders = Vector.tabulate(chunkCount) { index =>
      val alternatives = chunks.getOrElse(index, Vector.empty).sortBy(_._1.key).map { case (entry, caseName) =>
        s"      case ${literal(entry.key)} => Some($caseName(arguments))"
      }
      s"""|  private def decode$index(name: String, arguments: ${shape.argumentsType}): Option[KnownInstruction] =
          |    name match {
          |${alternatives.mkString("\n")}
          |      case _ => None
          |    }""".stripMargin
    }
    val dispatch = Vector.tabulate(chunkCount)(index => s"      case $index => decode$index(name, arguments)")
    s"""|// Generated from ${shape.sourceDescription}. Do not edit by hand.
        |package ${shape.packageName}
        |
        |sealed trait KnownInstruction extends Product with Serializable {
        |  def arguments: ${shape.argumentsType}
        |  def opcode: ${shape.opcodeType}
        |  def name: String
        |  final def syntax: ${shape.syntaxType} = arguments.toSyntax(opcode)
        |}
        |
        |enum InstructionDecodeError {
        |  case UnknownOpcode(opcode: String)
        |}
        |
        |object KnownInstruction {
        |  private val ChunkCount: Int = $chunkCount
        |
        |  def fromSyntax(value: ${shape.syntaxType}): Either[InstructionDecodeError, KnownInstruction] = {
        |    val name = ${shape.keyExpression}
        |    val arguments = ${shape.argumentsExpression}
        |    decode(name, arguments).toRight(InstructionDecodeError.UnknownOpcode(name))
        |  }
        |
        |  private def decode(name: String, arguments: ${shape.argumentsType}): Option[KnownInstruction] =
        |    Math.floorMod(name.hashCode, ChunkCount) match {
        |${dispatch.mkString("\n")}
        |    }
        |
        |${cases.mkString("\n\n")}
        |
        |${decoders.mkString("\n\n")}
        |}
        |""".stripMargin
  }

  private def caseNames(entries: Vector[Entry]): Vector[(Entry, String)] = {
    val bases = entries.map(entry => entry -> caseName(entry.name))
    // Scala permits names which differ only by case, but their classfiles cannot coexist on the default
    // case-insensitive filesystems used by macOS and Windows. Treat those names as collisions as well.
    val collisions = bases
      .groupMap { case (_, base) => base.toLowerCase(java.util.Locale.ROOT) }(_._1)
      .view
      .mapValues(_.sortBy(entry => (entry.name, entry.key)))
      .toMap
    bases.map { case (entry, base) =>
      val alternatives = collisions(base.toLowerCase(java.util.Locale.ROOT))
      entry -> (if (alternatives.size == 1) base else s"${base}_${alternatives.indexOf(entry) + 1}")
    }
  }

  private def caseName(opcode: String): String = {
    val words = opcode.split("[^A-Za-z0-9]+").toVector.filter(_.nonEmpty)
    val value = words.map(word => s"${word.head.toUpper}${word.tail}").mkString
    if (value.isEmpty) "Opcode" else if (value.head.isDigit) s"Opcode$value" else value
  }

  private def literal(value: String): String = {
    val escaped = value.flatMap {
      case '"'  => "\\\""
      case '\\' => "\\\\"
      case '\n' => "\\n"
      case '\r' => "\\r"
      case '\t' => "\\t"
      case char => char.toString
    }
    s"\"$escaped\""
  }
}
