package net.kurobako.gemstone.isa.spirv

import Operand.*
import VMProgram.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

/** SPIR-V control flow semantics, as requests to a [[SPIRVMachine]]. */
private object ControlSemantics {

  inline def label[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(label: Int): F[Unit] = machine.enter(label)

  inline def phi[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(group: Phis): F[Unit] = machine.phi(group)

  inline def branch[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(target: Block): F[Unit] = machine.jump(target)

  inline def branchConditional[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      condition: Int,
      whenTrue: Block,
      whenFalse: Block
  ): F[Unit] = machine.scalar(condition)(test => machine.branch(test, taken = whenTrue, otherwise = whenFalse))

  inline def switch[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(selector: Int, cases: Cases): F[Unit] =
    machine.scalar(selector)(value => machine.switch(value, cases))

  inline def call[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(callee: Callee): F[Unit] = machine.call(callee)

  inline def callIndirect[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(call: Indirect): F[Unit] =
    machine.callIndirect(call)

  inline def returnControl[V, D, F[_]](inline machine: SPIRVMachine[V, D, F]): F[Unit] = machine.returnControl

  inline def returnValue[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(value: Int): F[Unit] =
    machine.returnValue(value)

  inline def kill[V, D, F[_]](inline machine: SPIRVMachine[V, D, F]): F[Unit] = machine.kill

  inline def terminate[V, D, F[_]](inline machine: SPIRVMachine[V, D, F]): F[Unit] = machine.terminate

  inline def unreachable[V, D, F[_]](inline machine: SPIRVMachine[V, D, F]): F[Unit] = machine.unreachable

  inline def abort[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(message: Int): F[Unit] = machine.abort(message)

  /** The parent label and value of each incoming edge of a phi; a later pair for the same parent wins, as in a
    * sequential scan.
    */
  def edges(incoming: Vec[Operand]): Vector[(parent: Int, value: Int)] =
    incoming.reverseIterator.map(pair => (parent = pair.words(1).raw, value = pair.words(0).raw)).toVector
      .distinctBy(_.parent).reverse

  /** The semantics of `instruction` rendered over `machine`, or none for an instruction they do not cover. */
  def render(instruction: Instruction, machine: SPIRVMachineRender): Option[Term.Steps] = instruction match {
    case Instruction.Label(label)   => Some(this.label(machine)(id(label)))
    case Instruction.Branch(target) => Some(branch(machine)(Block.named(id(target))))
    case Instruction.BranchConditional(condition, whenTrue, whenFalse, _) =>
      Some(branchConditional(machine)(id(condition), Block.named(id(whenTrue)), Block.named(id(whenFalse))))
    case Instruction.Switch(selector, default, targets) =>
      Some(switch(machine)(id(selector), cases(default, targets)))
    case Instruction.Phi(resultType, dest, incoming) => Some(phi(machine)(group(resultType, dest, incoming)))
    case Instruction.FunctionCall(resultType, dest, called, arguments) =>
      Some(call(machine)(Callee(
        Function(id(called), Vec.empty, 0, 0, Map.empty),
        ids(arguments),
        id(resultType),
        id(dest)
      )))
    case Instruction.FunctionPointerCallINTEL(_, _, sources) if sources.nonEmpty =>
      Some(callIndirect(machine)(Indirect(id(sources.head), ids(sources.tail), _ => Op.Next)))
    case Instruction.Return()              => Some(returnControl(machine))
    case Instruction.ReturnValue(value)    => Some(returnValue(machine)(id(value)))
    case Instruction.Kill()                => Some(kill(machine))
    case Instruction.TerminateInvocation() => Some(terminate(machine))
    case Instruction.Unreachable()         => Some(unreachable(machine))
    case Instruction.AbortKHR(_, message)  => Some(abort(machine)(id(message)))
    case _                                 => None
  }

  private def ids(operands: Vec[Operand]): Array[Int] = operands.map(id(_)).toArray

  def literals(targets: Vec[Operand]): Array[Long] = targets.map { pair =>
    val low = pair.words.head.toUnsigned.toLong
    if (pair.words.length == 3) low | (pair.words(1).toUnsigned.toLong << 32) else low
  }.toArray

  private def cases(default: Operand, targets: Vec[Operand]): Cases = Cases(
    -1L,
    literals(targets),
    targets.map(pair => Block.named(pair.words.last.raw)).toArray,
    Block.named(id(default))
  )

  private def group(resultType: Operand, dest: Operand, incoming: Vec[Operand]): Phis = {
    val found = edges(incoming)
    Phis(
      Array(id(dest)),
      Array(id(resultType)),
      Array(found.map(_.parent).toArray),
      Array(found.map(_.value).toArray),
      Array(true),
      0
    )
  }
}
