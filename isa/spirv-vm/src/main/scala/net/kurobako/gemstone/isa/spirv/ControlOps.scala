package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*

/** Labels, structured merges, branches, calls and returns. */
private object ControlOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.Nop() | Instruction.Line(_, _, _) | Instruction.NoLine() | Instruction.FunctionParameter(_, _) |
        Instruction.AssumeTrueKHR(_) | Instruction.LoopControlINTEL(_) => Next
    case Instruction.Label(label)                                         => Label(id(label))
    case Instruction.Phi(_, _, _)                                         => phi(site)
    case Instruction.Branch(target)                                       => Branch(block(site, target))
    case Instruction.BranchConditional(condition, whenTrue, whenFalse, _) =>
      Conditional(id(condition), block(site, whenTrue), block(site, whenFalse))
    case Instruction.Switch(selector, default, targets) => switch(site, selector, default, targets)
    case Instruction.LoopMerge(merge, _, _)             => Merge(id(merge), loop = true)
    case Instruction.SelectionMerge(merge, _)           => Merge(id(merge), loop = false)
    case Instruction.Return()                           => Return
    case Instruction.ReturnValue(value)                 => ReturnValue(id(value))
    case Instruction.Kill()                             => Kill
    case Instruction.TerminateInvocation()              => Terminate
    case Instruction.Unreachable()                      => Unreachable
    case Instruction.AbortKHR(_, message)               => Abort(id(message))
    case Instruction.PoisonKHR(resultType, dest)        => zero(site, ResultIds(resultType, dest), "OpPoisonKHR")
    case Instruction.Undef(resultType, dest)            => zero(site, ResultIds(resultType, dest), "OpUndef")
    case Instruction.FunctionCall(resultType, dest, called, arguments) =>
      call(site, ResultIds(resultType, dest), id(called), arguments.map(id(_)))
    case Instruction.FunctionPointerCallINTEL(resultType, dest, sources) if sources.nonEmpty =>
      PointerCall(Indirect(
        id(sources.head),
        sources.tail.map(id(_)).toArray,
        function => call(site, ResultIds(resultType, dest), function, sources.tail.map(id(_)))
      ))
  }

  private def block(site: Site, label: Operand): Block = Block.resolved(id(label), site.label(label))

  private class Label(label: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.label(InvocationMachine(execution))(label)
  }

  /** The consecutive OpPhi instructions at one block entry read their predecessor values before any is written. A phi
    * is scalar when its destination and every incoming value live in the scalar bank, so it stages bare bits.
    */
  private class Phi(group: Phis) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.phi(InvocationMachine(execution))(group)
  }

  private def zero(site: Site, ids: ResultIds, opcode: String): Op =
    zeroValue(site.program.types, ids.resultType, ids.dest, opcode) match {
      case Left(error) => Fail(error)
      case Right(_)    => Zero(ids.dest, ids.resultType, opcode)
    }

  private class Zero(dest: Int, resultType: Int, opcode: String) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CompositeSemantics.zero(InvocationMachine(execution))(dest, resultType, opcode)
  }

  private def phi(site: Site): Op = {
    val instructions = site.program.module.instructions
    val end          = site.function.fold(instructions.size)(_.end)
    val group        =
      Iterator.from(site.pc).takeWhile(pc => pc < end && isPhi(instructions(pc))).map(instructions(_)).toVector
    val malformed = group.view.collect { case Instruction.Phi(_, _, incoming) => incoming }.flatten
      .find(_.words.length != 2)
    malformed match {
      case Some(pair) =>
        Fail(Fault.InvalidRequest(
          s"OpPhi ${pair.kind} operand must contain a value and parent label, found ${pair.words.length} words"
        ))
      case None =>
        val phis = group.collect { case Instruction.Phi(resultType, dest, incoming) =>
          (id(resultType), id(dest), ControlSemantics.edges(incoming))
        }
        val sources = phis.map(_._3.map(_.value).toArray).toArray
        Phi(Phis(
          phis.map(_._2).toArray,
          phis.map(_._1).toArray,
          phis.map(_._3.map(_.parent).toArray).toArray,
          sources,
          phis.indices.map(index =>
            site.program.scalarSlot(phis(index)._2) && sources(index).forall(site.program.scalarSlot)
          ).toArray,
          site.pc + group.size
        ))
    }
  }

  private def isPhi(instruction: Instruction): Boolean = instruction match {
    case Instruction.Phi(_, _, _) => true
    case _                        => false
  }

  private class Branch(target: Block) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.branch(InvocationMachine(execution))(target)
  }

  private class Conditional(condition: Int, whenTrue: Block, whenFalse: Block) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.branchConditional(InvocationMachine(execution))(condition, whenTrue, whenFalse)
  }

  private class Switch(selector: Int, cases: Cases) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.switch(InvocationMachine(execution))(selector, cases)
  }

  private def switch(site: Site, selector: Operand, default: Operand, targets: Vec[Operand]): Op = {
    val malformed = targets.find(pair => pair.words.length != 2 && pair.words.length != 3)
    val wrongKind = targets.find(_.kind != OperandKind.PairLiteralIntegerIdRef)
    (malformed, wrongKind) match {
      case (Some(_), _) => Fail(Fault.InvalidRequest("OpSwitch has an incomplete literal/label pair"))
      case (_, Some(_)) => Fail(Fault.InvalidRequest("OpSwitch target is not a literal/label pair"))
      case _            =>
        val selectorType = site.program.valueType(id(selector))
        Switch(
          id(selector),
          Cases(
            mask(site.program.scalarWidth(selectorType)),
            ControlSemantics.literals(targets),
            targets.map(pair => block(site, Operand(OperandKind.IdRef, Vec.nonEmpty(pair.words.last)))).toArray,
            block(site, default)
          )
        )
    }
  }

  private class Merge(merge: Int, loop: Boolean) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      if (
        (execution.fragmentQuad || execution.subgroupReconvergence) &&
        !execution.reconvergenceRegions.exists(_.merge == merge)
      )
        execution.reconvergenceRegions +=
          ReconvergenceRegion(execution.currentFunction.id, execution.currentLabel, merge, loop)
      execution.pc += 1
      Done
    }
  }

  private object Return extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.returnControl(InvocationMachine(execution))
  }

  private class ReturnValue(value: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.returnValue(InvocationMachine(execution))(value)
  }

  private object Kill extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.kill(InvocationMachine(execution))
  }

  private object Terminate extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.terminate(InvocationMachine(execution))
  }

  private object Unreachable extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.unreachable(InvocationMachine(execution))
    override def failure: Option[Fault] = Some(InvocationMachine.unreachableFault)
  }

  private class Abort(message: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.abort(InvocationMachine(execution))(message)
  }

  /** A direct call; bodiless OpenCL builtins lower to their own ops. */
  def call(site: Site, ids: ResultIds, called: Int, arguments: Vec[Int]): Op =
    site.program.functions.get(called) match {
      case None         => Fail(Fault.InvalidRequest(s"OpFunctionCall references undefined function %$called"))
      case Some(callee) =>
        if (arguments.size != callee.parameters.size)
          Fail(Fault.InvalidRequest(
            s"OpFunctionCall %$called expects ${callee.parameters.size} arguments, received ${arguments.size}"
          ))
        else
          site.program.names.get(called).flatMap(OpenCLBuiltins.lower(site, _, ids, arguments))
            .getOrElse(Call(Callee(callee, arguments.toArray, ids.resultType, ids.dest)))
    }

  private class Call(callee: Callee) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.call(InvocationMachine(execution))(callee)
  }

  /** The callee of an Intel function pointer is only known when the call executes. */
  private class PointerCall(call: Indirect) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ControlSemantics.callIndirect(InvocationMachine(execution))(call)
  }
}
