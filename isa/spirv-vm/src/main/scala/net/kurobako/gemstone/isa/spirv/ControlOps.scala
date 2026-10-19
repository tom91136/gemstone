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
    case Instruction.Nop() | _: Instruction.Line | Instruction.NoLine() | Instruction.FunctionParameter(_, _) |
        Instruction.AssumeTrueKHR(_) | Instruction.LoopControlINTEL(_) => Next
    case Instruction.Label(label)                                         => Label(id(label))
    case Instruction.Phi(_, _, _)                                         => phi(site)
    case Instruction.Branch(target)                                       => Branch(site.label(target))
    case Instruction.BranchConditional(condition, whenTrue, whenFalse, _) =>
      Conditional(id(condition), site.label(whenTrue), site.label(whenFalse))
    case Instruction.Switch(selector, default, targets)         => switch(site, selector, default, targets)
    case Instruction.LoopMerge(merge, _, _)                     => Merge(id(merge), loop = true)
    case Instruction.SelectionMerge(merge, _)                   => Merge(id(merge), loop = false)
    case Instruction.Return()                                   => Return
    case Instruction.ReturnValue(value)                         => ReturnValue(id(value))
    case Instruction.Kill() | Instruction.TerminateInvocation() => Kill
    case Instruction.Unreachable()        => Fail(Fault.InvalidRequest("OpUnreachable was reached"))
    case Instruction.AbortKHR(_, message) =>
      Dynamic { (execution, _) =>
        val lanes =
          Option(execution.values.lanes(id(message))).fold("")(_.map(java.lang.Long.toHexString(_)).mkString(" "))
        Left(Fault.Backend(s"OpAbortKHR terminated the invocation with message [$lanes]"))
      }
    case Instruction.PoisonKHR(resultType, dest)                       => zero(site, resultType, dest, "OpPoisonKHR")
    case Instruction.Undef(resultType, dest)                           => zero(site, resultType, dest, "OpUndef")
    case Instruction.FunctionCall(resultType, dest, called, arguments) =>
      call(site, id(resultType), id(dest), id(called), arguments.map(id(_)))
    case Instruction.FunctionPointerCallINTEL(resultType, dest, sources) if sources.nonEmpty =>
      PointerCall(site, id(resultType), id(dest), id(sources.head), sources.tail.map(id(_)))
  }

  private class Label(label: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      execution.currentLabel = label
      execution.pc += 1
      if (execution.fragmentQuad || execution.subgroupReconvergence) {
        val regions = execution.reconvergenceRegions
        val merge   = regions.indexWhere(_.merge == label)
        if (merge >= 0) {
          regions.remove(merge + 1, regions.size - merge - 1)
          execution.reconvergenceJoin = Some(ReconvergenceJoin.Merge(label))
        } else {
          // A continue may bypass nested selection merges on its way to the loop header.
          val header = regions.indexWhere(region => region.loop && region.header == label)
          if (header >= 0) regions.remove(header + 1, regions.size - header - 1)
        }
      }
      Done
    }
  }

  /** The consecutive OpPhi instructions at one block entry read their predecessor values before any is written. A phi
    * is scalar when its destination and every incoming value live in the scalar bank, so it stages bare bits.
    */
  private class Phi(
      dests: Array[Int],
      types: Array[Int],
      parents: Array[Array[Int]],
      sources: Array[Array[Int]],
      scalar: Array[Boolean],
      next: Int
  ) extends Op {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private def edge(labels: Array[Int], predecessor: Int): Int = {
      var index = labels.length - 1
      while (index >= 0 && labels(index) != predecessor) index -= 1
      index
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val values                = execution.values
      val bits                  = execution.phiBits(dests.length)
      val staged                = execution.phiValues(dests.length)
      var index                 = 0
      var missing: Fault | Null = null
      while (index < dests.length && missing == null) {
        val incoming = edge(parents(index), execution.predecessor)
        if (incoming >= 0 && scalar(index)) bits(index) = values.scalars(sources(index)(incoming))
        else (if (incoming < 0) None else values.get(sources(index)(incoming))) match {
          case Some(value) => staged(index) = retag(value, types(index))
          case None        =>
            missing =
              Fault.InvalidRequest(s"OpPhi %${dests(index)} has no incoming value for label %${execution.predecessor}")
        }
        index += 1
      }
      if (missing != null) Left(missing)
      else {
        index = 0
        while (index < dests.length) {
          if (scalar(index)) values.scalars(dests(index)) = bits(index)
          else {
            values.update(dests(index), staged(index).nn)
            staged(index) = null
          }
          index += 1
        }
        execution.pc = next
        Done
      }
    }
  }

  private def zero(site: Site, resultType: Operand, dest: Operand, opcode: String): Op =
    zeroValue(site.program.types, id(resultType), id(dest), opcode) match {
      case Left(error)     => Fail(error)
      case Right(constant) => Define(id(dest), constant.value)
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
          (id(resultType), id(dest), incoming)
        }
        // A later pair for the same parent wins, as in a sequential scan.
        def edges(incoming: Vec[Operand]): Vector[(Int, Int)] =
          incoming.reverseIterator.map(pair => pair.words(1).raw -> pair.words(0).raw).toVector.distinctBy(_._1)
        val sources = phis.map(phi => edges(phi._3).map(_._2).toArray).toArray
        Phi(
          phis.map(_._2).toArray,
          phis.map(_._1).toArray,
          phis.map(phi => edges(phi._3).map(_._1).toArray).toArray,
          sources,
          phis.indices.map(index =>
            site.program.scalarSlot(phis(index)._2) && sources(index).forall(site.program.scalarSlot)
          ).toArray,
          site.pc + group.size
        )
    }
  }

  private def isPhi(instruction: Instruction): Boolean = instruction match {
    case Instruction.Phi(_, _, _) => true
    case _                        => false
  }

  private def jump(execution: InvocationState, target: Either[Fault, Int]): AddressSpace.ReadResult[Unit] =
    target match {
      case Left(error) => Left(error)
      case Right(pc)   =>
        execution.predecessor = execution.currentLabel
        execution.pc = pc
        Done
    }

  private class Branch(target: Either[Fault, Int]) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = jump(execution, target)
  }

  private class Conditional(condition: Int, whenTrue: Either[Fault, Int], whenFalse: Either[Fault, Int])
      extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      scalar(execution.values, condition) match {
        case Left(error) => Left(error)
        case Right(test) => jump(execution, if (test != 0L) whenTrue else whenFalse)
      }
  }

  private class Switch(
      selector: Int,
      selectorMask: Long,
      default: Either[Fault, Int],
      literals: Array[Long],
      targets: Array[Either[Fault, Int]]
  ) extends Op {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      scalar(execution.values, selector) match {
        case Left(error)  => Left(error)
        case Right(value) =>
          var target = default
          var index  = 0
          while (index < literals.length) {
            if ((literals(index) & selectorMask) == (value & selectorMask)) target = targets(index)
            index += 1
          }
          jump(execution, target)
      }
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
          mask(site.program.scalarWidth(selectorType)),
          site.label(default),
          targets.map { pair =>
            val low = pair.words.head.toUnsigned.toLong
            if (pair.words.length == 3) low | (pair.words(1).toUnsigned.toLong << 32) else low
          }.toArray,
          targets.map(pair => site.label(Operand(OperandKind.IdRef, Vec.nonEmpty(pair.words.last)))).toArray
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

  private def finishFragmentReturn(execution: InvocationState): Unit = {
    execution.reconvergedReturn = false
    if (execution.fragmentQuad || execution.subgroupReconvergence)
      execution.reconvergenceRegions.filterInPlace(_.function != execution.currentFunction.id)
  }

  /** Lanes that reconverge pause at the return until their neighbours reach it. */
  private def pauseReturn(execution: InvocationState): Boolean =
    if ((execution.fragmentQuad || execution.subgroupReconvergence) && !execution.reconvergedReturn) {
      finishFragmentReturn(execution)
      execution.reconvergenceJoin = Some(ReconvergenceJoin.FunctionReturn(execution.currentFunction.id))
      true
    } else false

  private def resume(execution: InvocationState, frame: CallFrame): Unit = {
    execution.storage.top = frame.stack
    execution.currentFunction = frame.function
    execution.pc = frame.pc
    execution.currentLabel = frame.currentLabel
    execution.predecessor = frame.predecessor
  }

  private object Return extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      if (pauseReturn(execution)) Done
      else {
        finishFragmentReturn(execution)
        execution.calls.removeLastOption() match {
          case None =>
            execution.complete()
            Done
          case Some(frame) =>
            if (execution.values.program.runtimeType(frame.resultType).contains(RuntimeType.Void)) {
              resume(execution, frame)
              Done
            } else
              Left(Fault.InvalidRequest(
                s"SPIR-V function %${execution.currentFunction.id} returned no value to %${frame.destination}"
              ))
        }
      }
  }

  private class ReturnValue(value: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      if (pauseReturn(execution)) Done
      else {
        finishFragmentReturn(execution)
        execution.values.get(value) match {
          case None           => Left(undefined(value))
          case Some(returned) =>
            execution.calls.removeLastOption() match {
              case None        => Left(Fault.InvalidRequest("SPIR-V entry point returned a value"))
              case Some(frame) =>
                execution.values.update(frame.destination, retag(returned, frame.resultType))
                resume(execution, frame)
                Done
            }
        }
      }
  }

  private object Kill extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      if (!execution.fragmentInvocation) Left(Fault.InvalidRequest("OpKill requires a fragment invocation"))
      else {
        val cleared = execution.sampleMask match {
          case Some(pointer) =>
            memory.write(pointer, Array.ofDim[Byte](execution.values.program.typeSize(pointer.pointee)))
          case None => Right(())
        }
        cleared.map { _ =>
          execution.fragmentKilled = true
          execution.complete()
          execution.calls.clear()
        }
      }
  }

  private class Define(dest: Int, value: VMValue) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      execution.values.update(dest, value)
      execution.pc += 1
      Done
    }
  }

  /** A direct call; bodiless OpenCL builtins lower to their own ops. */
  def call(site: Site, resultType: Int, dest: Int, called: Int, arguments: Vec[Int]): Op =
    site.program.functions.get(called) match {
      case None         => Fail(Fault.InvalidRequest(s"OpFunctionCall references undefined function %$called"))
      case Some(callee) =>
        if (arguments.size != callee.parameters.size)
          Fail(Fault.InvalidRequest(
            s"OpFunctionCall %$called expects ${callee.parameters.size} arguments, received ${arguments.size}"
          ))
        else
          site.program.names.get(called).flatMap(OpenCLBuiltins.lower(site, _, resultType, dest, arguments))
            .getOrElse(Call(callee, arguments.toArray, resultType, dest))
    }

  private class Call(callee: Function, arguments: Array[Int], resultType: Int, dest: Int) extends Op {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      if (execution.calls.size >= 128) Left(Fault.Backend("SPIR-V function call depth exceeds 128 frames"))
      else {
        var index   = 0
        var failure = Option.empty[Fault]
        while (index < arguments.length && failure.isEmpty) {
          val (parameterId, parameterType) = callee.parameters(index)
          transfer(execution.values, arguments(index), parameterId, parameterType) match {
            case Left(error) => failure = Some(error)
            case Right(())   => ()
          }
          index += 1
        }
        failure.toLeft {
          execution.calls.append(
            CallFrame(
              execution.currentFunction,
              execution.pc + 1,
              execution.currentLabel,
              execution.predecessor,
              resultType,
              dest,
              execution.storage.top
            )
          )
          execution.currentFunction = callee
          execution.pc = callee.start + 1
          execution.currentLabel = -1
          execution.predecessor = -1
        }
      }
  }

  /** The callee of an Intel function pointer is only known when the call executes. */
  private class PointerCall(site: Site, resultType: Int, dest: Int, pointer: Int, arguments: Vec[Int])
      extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val program = site.program
      execution.values.get(pointer) match {
        case Some(VMValue.Bits(_, Array(token)))
            if program.ownsFunctionPointer(token) && (token & 0xffffffffL) > 0 && (token & 0xffffffffL) <=
              Int.MaxValue &&
              program.functions.contains((token & 0xffffffffL).toInt) =>
          call(site, resultType, dest, (token & 0xffffffffL).toInt, arguments).run(execution, memory)
        case Some(_) => Left(Fault.InvalidRequest("OpFunctionPointerCallINTEL has an invalid function pointer"))
        case None    => Left(undefined(pointer))
      }
    }
  }
}
