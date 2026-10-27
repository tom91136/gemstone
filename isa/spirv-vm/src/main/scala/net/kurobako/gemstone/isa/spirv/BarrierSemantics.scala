package net.kurobako.gemstone.isa.spirv

import Operand.*
import net.kurobako.gemstone.semantics.render.*

/** SPIR-V barrier and fence semantics, as requests to a [[SPIRVMachine]]. Operands name the scopes and memory controls
  * the machine checks.
  */
private object BarrierSemantics {

  inline def controlBarrier[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      scope: Int,
      memory: Int,
      semantics: Int
  ): F[Unit] = machine.controlBarrier(scope, memory, semantics)

  inline def arriveBarrier[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      scope: Int,
      memory: Int,
      semantics: Int
  ): F[Unit] = machine.arriveBarrier(scope, memory, semantics)(machine.advance)

  inline def fence[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(scope: Int, semantics: Int): F[Unit] =
    machine.fence(scope, semantics)(machine.advance)

  inline def namedBarrier[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      barrier: Int,
      memory: Int,
      semantics: Int
  ): F[Unit] = machine.namedBarrier(barrier, memory, semantics)

  inline def initializeBarrier[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      target: Int,
      typeId: Int,
      count: Int
  ): F[Unit] = machine.initializeBarrier(target, typeId, count)(machine.advance)

  /** The barrier semantics of `instruction` rendered over `machine`, or none for an instruction they do not cover. */
  def render(instruction: Instruction, machine: SPIRVMachineRender): Option[Term.Steps] = instruction match {
    case Instruction.ControlBarrier(execution, memory, semantics) =>
      Some(controlBarrier(machine)(id(execution), id(memory), id(semantics)))
    case Instruction.ControlBarrierWaitEXT(execution, memory, semantics) =>
      Some(controlBarrier(machine)(id(execution), id(memory), id(semantics)))
    case Instruction.ControlBarrierArriveEXT(execution, memory, semantics) =>
      Some(arriveBarrier(machine)(id(execution), id(memory), id(semantics)))
    case Instruction.MemoryBarrier(scope, semantics)                => Some(fence(machine)(id(scope), id(semantics)))
    case Instruction.MemoryNamedBarrier(barrier, memory, semantics) =>
      Some(namedBarrier(machine)(id(barrier), id(memory), id(semantics)))
    case Instruction.NamedBarrierInitialize(typeId, target, count) =>
      Some(initializeBarrier(machine)(id(target), id(typeId), id(count)))
    case _ => None
  }
}
