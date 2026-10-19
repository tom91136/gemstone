package net.kurobako.gemstone.isa.spirv

import VMProgram.*
import net.kurobako.gemstone.vm.*

/** Load-time view of one instruction: its program, position and enclosing function. */
private class Site(val program: VMProgram, val pc: Int, val function: Option[Function]) {
  def label(operand: Operand): Either[Fault, Int] = function match {
    case Some(value) =>
      value.labels.get(Operand.id(operand))
        .toRight(Fault.InvalidRequest(s"SPIR-V label %${Operand.id(operand)} is absent in function %${value.id}"))
    case None => Left(Fault.InvalidRequest(s"SPIR-V label %${Operand.id(operand)} is outside a function"))
  }
}
