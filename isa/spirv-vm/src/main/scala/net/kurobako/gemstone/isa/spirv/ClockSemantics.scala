package net.kurobako.gemstone.isa.spirv

import Operand.*
import net.kurobako.gemstone.semantics.render.*

/** SPIR-V clock semantics, as requests to a [[SPIRVMachine]]. */
private object ClockSemantics {

  inline def read[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      scope: Int,
      target: Int,
      typeId: Int,
      halves: Boolean
  ): F[Unit] = machine.readClock(scope, target, typeId, halves)(machine.advance)

  def render(instruction: Instruction, machine: SPIRVMachineRender, halves: Boolean): Option[Term.Steps] =
    instruction match {
      case Instruction.ReadClockKHR(resultType, dest, scope) =>
        Some(read(machine)(id(scope), id(dest), id(resultType), halves))
      case _ => None
    }
}
