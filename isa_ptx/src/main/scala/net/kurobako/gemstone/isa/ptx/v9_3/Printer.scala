package net.kurobako.gemstone.isa.ptx.v9_3

/** Deterministic canonical printer. Parsing its output must reproduce the same syntax tree. */
object Printer {
  def render(operand: Operand): String = operand match {
    case Operand.Immediate(raw)             => raw
    case Operand.Register(name, None)       => name
    case Operand.Register(name, Some(part)) => s"$name.$part"
    case Operand.Symbol(name)               => name
    case Operand.StringLiteral(contents)    => s"\"$contents\""
    case Operand.Qualifier(path)            => path.map("." + _).mkString
    case Operand.VectorLiteral(items)       => items.map(render).mkString("{", ", ", "}")
    case Operand.Parenthesized(items)       => items.map(render).mkString("(", ", ", ")")
    case Operand.Address(items)             => items.map(render).mkString("[", ", ", "]")
    case Operand.Index(base, index)         => s"${render(base)}[${render(index)}]"
    case Operand.Add(left, right)           => s"${render(left)}+${render(right)}"
    case Operand.Subtract(left, right)      => s"${render(left)}-${render(right)}"
    case Operand.WithPredicate(base, value) => s"${render(base)}|${render(value)}"
    case Operand.Negate(value)              => s"-${render(value)}"
  }

  def render(predication: Predication): String = {
    val negation = if (predication.negated) "!" else ""
    s"@$negation${render(predication.operand)}"
  }

  def render(parameter: Parameter): String = {
    val qualifiers = parameter.qualifiers.map { qualifier =>
      if (qualifier.headOption.exists(_.isDigit)) qualifier else "." + qualifier
    }.mkString(" ")
    val dimensions = parameter.dimensions.map(dimension => s"[${render(dimension)}]").mkString
    Vector(qualifiers, parameter.name + dimensions).filter(_.nonEmpty).mkString(" ")
  }

  def render(instruction: Instruction): String = {
    val prefix       = instruction.predication.map(render).map(_ + " ").getOrElse("")
    val operandsText =
      if (instruction.operands.isEmpty) "" else instruction.operands.map(render).mkString(" ", ", ", "")
    s"$prefix${instruction.qualifiedOpcode}$operandsText;"
  }

  def render(statement: Statement): String = statement match {
    case Statement.Label(name)                            => s"$name:"
    case Statement.Directive(path, arguments, terminator) =>
      val head = path.map("." + _).mkString
      val tail = arguments.zipWithIndex.map { case (argument, index) =>
        val separator =
          if (index == 0 || argument.separator == DirectiveSeparator.Space) " " else ", "
        separator + render(argument.operand)
      }.mkString
      val suffix = if (terminator == DirectiveTerminator.Semicolon) ";" else ""
      head + tail + suffix
    case Statement.Instruction(instruction)               => render(instruction)
    case Statement.Block(body)                            => renderBody(body)
    case Statement.Entry(visible, name, parameters, body) =>
      val visibility = if (visible) ".visible " else ""
      val params     = parameters.map(render).mkString("(", ", ", ")")
      s"$visibility.entry $name$params ${renderBody(body)}"
    case Statement.Function(visible, name, returns, parameters, body) =>
      val visibility = if (visible) ".visible " else ""
      val result     = if (returns.isEmpty) "" else returns.map(render).mkString("(", ", ", ") ")
      val params     = parameters.map(render).mkString("(", ", ", ")")
      body match {
        case Some(statements) => s"$visibility.func $result$name$params ${renderBody(statements)}"
        case None             => s"$visibility.func $result$name$params;"
      }
  }

  def render(statements: Vector[Statement]): String = statements.map(render).mkString("\n")

  def render(program: Program): String = render(program.statements)

  private def renderBody(body: Vector[Statement]): String =
    if (body.isEmpty) "{}" else body.map(render).mkString("{ ", " ", " }")
}
