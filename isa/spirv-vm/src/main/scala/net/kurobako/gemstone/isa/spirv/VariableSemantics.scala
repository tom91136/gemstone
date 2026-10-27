package net.kurobako.gemstone.isa.spirv

import Operand.*
import net.kurobako.gemstone.semantics.render.*

/** SPIR-V variable semantics, as requests to a [[SPIRVMachine]]. */
private object VariableSemantics {

  inline def variable[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      target: Int,
      typeId: Int,
      storageClass: Int,
      initialiser: Option[Int]
  ): F[Unit] = machine.variable(target, typeId, storageClass, initialiser)(machine.advance)

  inline def variableLengthArray[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      target: Int,
      typeId: Int,
      element: Int,
      length: Int
  ): F[Unit] =
    machine.scalar(length)(count => machine.variableLengthArray(target, typeId, element, count)(machine.advance))

  inline def saveMemory[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(target: Int, typeId: Int): F[Unit] =
    machine.saveMemory(target, typeId)(machine.advance)

  inline def restoreMemory[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(pointer: Int): F[Unit] =
    machine.restoreMemory(pointer)(machine.advance)

  inline def memorySemantics[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.memorySemantics(source, target, typeId)(machine.advance)

  private def declared(
      machine: SPIRVMachineRender,
      resultType: Operand,
      dest: Operand,
      storageClass: Operand,
      initialiser: Option[Operand]
  ): Option[Term.Steps] =
    Some(variable(machine)(id(dest), id(resultType), id(storageClass), initialiser.map(id(_))))

  /** The forms the explorer shows: variables, variable length arrays and the saved stack of private storage. */
  def render(instruction: Instruction, machine: SPIRVMachineRender): Option[Term.Steps] = instruction match {
    case Instruction.Variable(resultType, dest, storageClass, initialiser) =>
      declared(machine, resultType, dest, storageClass, initialiser)
    case Instruction.UntypedVariableKHR(resultType, dest, storageClass, Some(_), initialiser) =>
      declared(machine, resultType, dest, storageClass, initialiser)
    case Instruction.UntypedVariableLengthArrayINTEL(resultType, dest, element, length) =>
      Some(variableLengthArray(machine)(id(dest), id(resultType), id(element), id(length)))
    case Instruction.VariableLengthArrayINTEL(resultType, dest, length) =>
      Some(variableLengthArray(machine)(id(dest), id(resultType), id(resultType), id(length)))
    case Instruction.SaveMemoryINTEL(resultType, dest) => Some(saveMemory(machine)(id(dest), id(resultType)))
    case Instruction.RestoreMemoryINTEL(pointer)       => Some(restoreMemory(machine)(id(pointer)))
    case Instruction.GenericPtrMemSemantics(resultType, dest, source) =>
      Some(memorySemantics(machine)(id(source), id(dest), id(resultType)))
    case _ => None
  }
}
