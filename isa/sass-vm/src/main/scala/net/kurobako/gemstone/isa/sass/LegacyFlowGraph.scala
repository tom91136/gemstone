package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

private object LegacyFlowGraph {
  def codeTargets(instruction: DecodedInstruction): Set[Int] =
    if (!ControlFlow.transfers(instruction.operation)) Set.empty
    else instruction.operands.flatMap(
      OperandQueries.integer(_).collect { case value if value >= 0 && value <= Int.MaxValue => value.toInt }
    ).toSet

  /** Backward branches closing a loop that only reads memory; such a loop waits on other warps and needs a yield. */
  def pollingBranches(instructions: Vec[DecodedInstruction]): Set[Int] = {
    def reads(instruction: DecodedInstruction) = instruction.operation.form match {
      case _: Form.Ld | _: Form.Ldg | _: Form.Lds => true
      case _                                      => false
    }
    def effects(instruction: DecodedInstruction) = instruction.operation.form match {
      case _: Form.St | _: Form.Stg | _: Form.Stl | _: Form.Sts | _: Form.Atom | _: Form.Atoms | _: Form.Red |
          _: Form.Bar | _: Form.Cal | _: Form.Jcal | _: Form.Ldslk | _: Form.Stscul => true
      case _ => false
    }
    val index = instructions.zipWithIndex.map((instruction, at) => instruction.address.raw -> at).toMap
    def polls(start: Int, end: Int): Boolean = {
      val body = instructions.slice(start, end + 1)
      body.exists(reads(_)) && !body.exists(effects(_))
    }
    instructions.zipWithIndex.collect {
      case (branch, end)
          if OperandQueries.is[Form.Bra](branch.operation) &&
            codeTargets(branch).filter(_ <= branch.address.raw).flatMap(index.get).exists(polls(_, end)) =>
        branch.address.raw
    }.toSet
  }
}

/** Successors of each instruction of a legacy function, or none where the target is not statically known. */
private class LegacyFlowGraph(instructions: Vec[DecodedInstruction]) {
  val indices: Map[Int, Int] = instructions.zipWithIndex.map { case (instruction, index) =>
    instruction.address.raw -> index
  }.toMap
  def targets(instruction: DecodedInstruction): Option[Vec[Int]] = {
    val addresses = LegacyFlowGraph.codeTargets(instruction).toVec
    Option.when(addresses.size == 1 && addresses.forall(indices.contains(_)))(addresses.map(indices(_)))
  }
  private val legacy  = LegacyControlFlow(instructions)
  private val entries = LegacyControlFlow.functionEntries(instructions)
  // a return whose caller is unknown may resume after any call or at any pushed return address
  private val returns: Vec[Int] = instructions.indices.toVec.flatMap { index =>
    instructions(index).operation.form match {
      case _: Form.Cal  => Vec(index + 1).filter(_ < instructions.size)
      case _: Form.Pret => targets(instructions(index)).getOrElse(Vec.empty)
      case _            => Vec.empty
    }
  }
  private def pops(index: Int): Option[Vec[Int]] = {
    val instruction = instructions(index)
    legacy.transfer(instruction).map(setup =>
      Vec(indices(setup.target)) ++ (if (instruction.guard.nonEmpty) Vec(index + 1) else Vec.empty[Int])
    )
  }
  private def push(frames: List[Int], returnIndex: Int): List[Int] =
    if (returnIndex >= instructions.size || frames.contains(returnIndex)) Nil else returnIndex :: frames

  /** Successors with the return addresses of the enclosing calls, innermost first. Empty frames mean unknown. */
  def framed(index: Int, frames: List[Int]): Option[Vec[(Int, List[Int])]] = {
    val instruction = instructions(index)
    val next        = Vec(index + 1)
    val fallthrough = if (LegacyControlFlow.terminates(instruction)) Vec.empty[Int] else next
    def kept(indices: Vec[Int]): Vec[(Int, List[Int])]    = indices.map(_ -> frames)
    def unknown(indices: Vec[Int]): Vec[(Int, List[Int])] = indices.map(_ -> List.empty[Int])
    def skipped: Vec[(Int, List[Int])]                    =
      if (instruction.guard.isEmpty) Vec.empty else kept(next)
    if (LegacyControlFlow.never(instruction)) Some(kept(next))
    else instruction.operation.form match {
      case _
          if instruction.modifiers.contains(Instruction.Modifier.`S`) && !ControlFlow.leaves(instruction.operation) =>
        pops(index).map(kept(_))
      case _: Form.Bra               => targets(instruction).map(target => kept(target ++ fallthrough))
      case _: Form.Ssy | _: Form.Pbk => targets(instruction).map(_ => kept(next))
      case _: Form.Pret              => targets(instruction).map(_ => unknown(next))
      case _: Form.Cal               => targets(instruction).map(callee =>
          callee.map(_ -> push(frames, index + 1)) ++ skipped
        )
      case _: Form.Brx if LegacyControlFlow.isIndirectCall(instructions, index) => Some(unknown(entries))
      case _: Form.Ret if instruction.operands.isEmpty && frames.nonEmpty       =>
        Some(Vec(frames.head -> frames.tail) ++ kept(fallthrough))
      case _: Form.Ret if instruction.operands.isEmpty => Some(unknown(returns) ++ kept(fallthrough))
      case _: Form.Brk | _: Form.Sync                  => pops(index).map(kept(_))
      case _: Form.Exit | _: Form.Kill                 => Some(kept(fallthrough))
      case _: Form.Yield                               => Some(kept(next))
      case _: Form.Bpt => Some(kept(if (instruction.modifiers == Vec(Instruction.Modifier.TRAP)) fallthrough else next))
      case _ if ControlFlow.transfers(instruction.operation) => None
      case _                                                 => Some(kept(next))
    }
  }

  def successors(index: Int): Option[Vec[Int]] = framed(index, Nil).map(_.map(_._1))
}
