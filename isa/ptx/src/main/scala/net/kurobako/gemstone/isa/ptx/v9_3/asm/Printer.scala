package net.kurobako.gemstone.isa.ptx.v9_3.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.ptx.v9_3.*
import net.kurobako.gemstone.isa.ptx.v9_3.Program.*
import net.kurobako.gemstone.isa.{Printer as ISAPrinter, *}

/** Deterministic canonical printer. Parsing its output must reproduce the same syntax tree. */
object Printer extends ISAPrinter[Program] {
  def render(operand: Operand): String = operand match {
    case Operand.Immediate(raw)             => raw.raw
    case Operand.Register(name, None)       => name.raw
    case Operand.Register(name, Some(part)) => s"$name.${part.spelling}"
    case Operand.Symbol(name)               => name.raw
    case Operand.StringLiteral(contents)    => s"\"$contents\""
    case Operand.Qualifier(path)            => path.raw.map(part => "." + part.raw).mkString
    case Operand.VectorLiteral(items)       => items.map(render(_)).mkString("{", ", ", "}")
    case Operand.Parenthesized(items)       => items.map(render(_)).mkString("(", ", ", ")")
    case Operand.Address(items)             => items.map(render(_)).mkString("[", ", ", "]")
    case Operand.Qualified(base, qualifier) => s"${render(base)}.${qualifier.spelling}"
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
    def qualifiers(values: Vec[DeclarationQualifier]): String = values.map {
      case DeclarationQualifier.Alignment(bytes) => s".align $bytes"
      case DeclarationQualifier.Attribute(name)  => s".$name"
      case DeclarationQualifier.Value(value)     => value.raw
    }.mkString(" ")
    val (declaration, pointer) = parameter.qualifiers.span(_ != DeclarationQualifier.Attribute("ptr"))
    val dimensions             = parameter.dimensions.map(dimension => s"[${dimension.fold("")(render(_))}]").mkString
    Vec(
      qualifiers(declaration),
      "." + parameter.elementType.mnemonic,
      qualifiers(pointer),
      parameter.name.raw + dimensions
    )
      .filter(_.nonEmpty).mkString(" ")
  }

  def render(instruction: Instruction): NonEmpty[String] = {
    val prefix       = instruction.pred.map(predication => s"${render(predication)} ").getOrElse("")
    val operandsText =
      if (instruction.operands.isEmpty) "" else instruction.operands.map(render(_)).mkString(" ", ", ", "")
    s"$prefix${instruction.qualifiedOpcode}$operandsText;"
  }

  def render(statement: Statement): String = statement match {
    case Statement.Comment(text)                                                          => text
    case Statement.Label(name)                                                            => s"$name:"
    case Statement.Data(addressSpace, qualifiers, elementType, name, extent, initializer) =>
      val qualifierText = qualifiers.flatMap {
        case DeclarationQualifier.Alignment(bytes) => Vec(".align", bytes.toString)
        case DeclarationQualifier.Attribute(name)  => Vec("." + name.raw)
        case DeclarationQualifier.Value(value)     => Vec(value.raw)
      }
      val prefix = (Vec("." + addressSpace.mnemonic) ++ qualifierText ++ Vec("." + elementType.mnemonic)).mkString(" ")
      val dimensions = extent match {
        case Extent.Scalar          => ""
        case Extent.Unsized         => "[]"
        case Extent.Sized(elements) => s"[$elements]"
      }
      val values = if (initializer.isEmpty) "" else initializer.map(render(_)).mkString(" = {", ", ", "}")
      s"$prefix $name$dimensions$values;"
    case Statement.Directive(path, arguments, terminator, initializer) =>
      val head = path.raw.map(part => "." + part.raw).mkString
      val tail = arguments.zipWithIndex.map { case (argument, index) =>
        val separator =
          if (index == 0 || argument.separator == DirectiveSeparator.Space) " " else ", "
        separator + render(argument.operand)
      }.mkString
      val suffix     = if (terminator == DirectiveTerminator.Semicolon) ";" else ""
      val assignment = if (initializer.isEmpty) "" else initializer.map(render(_)).mkString(" = ", ", ", "")
      head + tail + assignment + suffix
    case Statement.Instruction(instruction)                             => render(instruction).raw
    case Statement.UnsupportedInstruction(opcode, operands, mods, pred) =>
      val prefix    = pred.map(value => s"${render(value)} ").getOrElse("")
      val qualified = (opcode.raw +: mods.map(_.name.raw)).mkString(".")
      val tail      = if (operands.isEmpty) "" else operands.map(render(_)).mkString(" ", ", ", "")
      s"$prefix$qualified$tail;"
    case Statement.CallPrototype(name, returns, parameters, directives) =>
      val result = if (returns.isEmpty) "" else returns.map(render(_)).mkString("(", ", ", ") ")
      val inputs = parameters.map(render(_)).mkString("(", ", ", ")")
      s"$name: .callprototype ${result}_ $inputs${renderDirectives(directives)};"
    case Statement.Block(body)                                        => renderBody(body)
    case Statement.Entry(linkage, name, parameters, body, directives) =>
      val params = parameters.map(render(_)).mkString("(", ", ", ")")
      s"${renderLinkage(linkage)}.entry $name$params${renderDirectives(directives)}${renderDefinition(body)}"
    case Statement.Function(linkage, name, returns, parameters, body, directives) =>
      val result = if (returns.isEmpty) "" else returns.map(render(_)).mkString("(", ", ", ") ")
      val params = parameters.map(render(_)).mkString("(", ", ", ")")
      s"${renderLinkage(linkage)}.func $result$name$params${renderDirectives(directives)}${renderDefinition(body)}"
  }

  def render(statements: Vec[Statement]): String = statements.map(render(_)).mkString("\n")

  override def render(program: Program): String = render(program.statements)

  private def renderLinkage(linkage: Linkage): String =
    if (linkage == Linkage.Default) "" else s".${linkage.mnemonic} "

  private def renderDirectives(directives: Vec[Statement.Directive]): String =
    if (directives.isEmpty) "" else directives.map(render(_)).mkString(" ", " ", "")

  private def renderDefinition(body: Option[Vec[Statement]]): String =
    body.fold(";")(statements => " " + renderBody(statements))

  private def renderBody(body: Vec[Statement]): String =
    if (body.isEmpty) "{}" else body.map(render(_)).mkString("{\n", "\n", "\n}")
}
