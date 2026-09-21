package net.kurobako.gemstone.isa.amdgpu

import Program.*

/** Canonical AMDGPU assembly printer. */
object Printer extends net.kurobako.gemstone.isa.Printer[Program] {
  override def render(program: Program): String = program.statements.map(renderStatement(_)).mkString("\n")

  def render(instruction: Instruction): String = {
    val operands  = instruction.operands.map(renderOperand(_))
    val modifiers = instruction.mods.map { modifier =>
      modifier.value.fold(modifier.name)(value => s"${modifier.name}:$value")
    }
    val tail = operands ++ modifiers
    if (tail.isEmpty) instruction.opcode.value
    else {
      val operandText  = operands.mkString(", ")
      val modifierText = modifiers.mkString(" ")
      val separator    = if (operands.nonEmpty && modifiers.nonEmpty) " " else ""
      s"${instruction.opcode} $operandText$separator$modifierText"
    }
  }

  private def renderStatement(statement: Statement): String = statement match {
    case Statement.Instruction(value)                              => render(value)
    case Statement.InstructionPair(left, right)                    => s"${render(left)} :: ${render(right)}"
    case Statement.Label(name)                                     => s"$name:"
    case Statement.Directive(name, arguments) if arguments.isEmpty => name
    case Statement.Directive(name, arguments)                      => s"$name $arguments"
  }

  private def renderOperand(operand: Operand): String = operand match {
    case Operand.Register(file, first, last) =>
      val prefix = file match {
        case RegisterFile.Scalar         => "s"
        case RegisterFile.VectorRegister => "v"
        case RegisterFile.Accumulator    => "a"
        case RegisterFile.TrapTemporary  => "ttmp"
      }
      if (first == last) s"$prefix$first" else s"$prefix[$first:$last]"
    case Operand.SpecialRegister(name)     => name
    case Operand.IntegerLiteral(value, 16) =>
      if (value.signum < 0) s"-0x${(-value).toString(16)}" else s"0x${value.toString(16)}"
    case Operand.IntegerLiteral(value, _) => value.toString
    case Operand.DecimalLiteral(value)    => value
    case Operand.Identifier(name)         => name
    case Operand.Expression(text)         => text
    case Operand.Negated(value)           => s"-${renderOperand(value)}"
    case Operand.Absolute(value)          => s"|${renderOperand(value)}|"
  }
}
