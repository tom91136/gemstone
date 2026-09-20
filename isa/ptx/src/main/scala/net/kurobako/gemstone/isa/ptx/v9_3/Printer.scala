package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import net.kurobako.gemstone.Vec

/** Deterministic canonical printer. Parsing its output must reproduce the same syntax tree. */
object Printer {
  def render(operand: Operand): String = operand match {
    case Operand.Immediate(raw)             => raw.value
    case Operand.Register(name, None)       => name.value
    case Operand.Register(name, Some(part)) => s"$name.${part.value}"
    case Operand.Symbol(name)               => name.value
    case Operand.StringLiteral(contents)    => s"\"$contents\""
    case Operand.Qualifier(path)            => path.map(part => "." + part.value).mkString
    case Operand.VectorLiteral(items)       => items.map(render(_)).mkString("{", ", ", "}")
    case Operand.Parenthesized(items)       => items.map(render(_)).mkString("(", ", ", ")")
    case Operand.Address(items)             => items.map(render(_)).mkString("[", ", ", "]")
    case Operand.Qualified(base, qualifier) => s"${render(base)}.${qualifier.value}"
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
      qualifier match {
        case DeclarationQualifier.Alignment(bytes) => s".align $bytes"
        case DeclarationQualifier.Attribute(name)  => "." + name.value
        case DeclarationQualifier.Value(value)     => value.value
      }
    }.mkString(" ")
    val dimensions = parameter.dimensions.map(dimension => s"[${render(dimension)}]").mkString
    Vec(qualifiers, "." + parameter.elementType.mnemonic, parameter.name.value + dimensions)
      .filter(_.nonEmpty).mkString(" ")
  }

  def render(instruction: Instruction): InstructionText = {
    val prefix       = instruction.pred.map(predication => s"${render(predication)} ").getOrElse("")
    val operandsText =
      if (instruction.operands.isEmpty) "" else instruction.operands.map(render(_)).mkString(" ", ", ", "")
    InstructionText.applyUnsafe(s"$prefix${instruction.qualifiedOpcode}$operandsText;")
  }

  def render(statement: Statement): String = statement match {
    case Statement.Label(name)                                                           => s"$name:"
    case Statement.Data(addressSpace, qualifiers, elementType, name, count, initializer) =>
      val qualifierText = qualifiers.flatMap {
        case DeclarationQualifier.Alignment(bytes) => Vec(".align", bytes.toString)
        case DeclarationQualifier.Attribute(name)  => Vec("." + name.value)
        case DeclarationQualifier.Value(value)     => Vec(value.value)
      }
      val prefix = (Vec("." + addressSpace.mnemonic) ++ qualifierText ++ Vec("." + elementType.mnemonic)).mkString(" ")
      val extent = count.fold("")(value => s"[$value]")
      val values = if initializer.isEmpty then "" else initializer.map(render(_)).mkString(" = {", ", ", "}")
      s"$prefix $name$extent$values;"
    case Statement.Directive(path, arguments, terminator) =>
      val head = path.map(part => "." + part.value).mkString
      val tail = arguments.zipWithIndex.map { case (argument, index) =>
        val separator =
          if (index == 0 || argument.separator == DirectiveSeparator.Space) " " else ", "
        separator + render(argument.operand)
      }.mkString
      val suffix = if (terminator == DirectiveTerminator.Semicolon) ";" else ""
      head + tail + suffix
    case Statement.Instruction(instruction)                             => render(instruction).value
    case Statement.UnsupportedInstruction(opcode, operands, mods, pred) =>
      val prefix    = pred.map(value => s"${render(value)} ").getOrElse("")
      val qualified = (opcode.value +: mods.map(_.name.value)).mkString(".")
      val tail      = if (operands.isEmpty) "" else operands.map(render(_)).mkString(" ", ", ", "")
      s"$prefix$qualified$tail;"
    case Statement.Block(body)                            => renderBody(body)
    case Statement.Entry(visible, name, parameters, body) =>
      val visibility = if (visible) ".visible " else ""
      val params     = parameters.map(render(_)).mkString("(", ", ", ")")
      s"$visibility.entry $name$params ${renderBody(body)}"
    case Statement.Function(visible, name, returns, parameters, body) =>
      val visibility = if (visible) ".visible " else ""
      val result     = if (returns.isEmpty) "" else returns.map(render(_)).mkString("(", ", ", ") ")
      val params     = parameters.map(render(_)).mkString("(", ", ", ")")
      body match {
        case Some(statements) => s"$visibility.func $result$name$params ${renderBody(statements)}"
        case None             => s"$visibility.func $result$name$params;"
      }
  }

  def render(statements: Vec[Statement]): String = statements.map(render(_)).mkString("\n")

  def render(program: Program): String = render(program.statements)

  private def renderBody(body: Vec[Statement]): String =
    if (body.isEmpty) "{}" else body.map(render(_)).mkString("{ ", " ", " }")
}
