package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import scala.annotation.tailrec

private object LegacyFlowGraph {
  def codeTargets(instruction: DecodedInstruction): Set[Int] =
    if (!ControlFlow.transfers(instruction.operation)) Set.empty
    else instruction.operands.flatMap(
      OperandQueries.integer(_).collect { case value if value >= 0 && value <= Int.MaxValue => value.toInt }
    ).toSet

  def reads(instruction: DecodedInstruction): Boolean = instruction.operation.form match {
    case _: Form.Ld | _: Form.Ldg | _: Form.Lds => true
    case _                                      => false
  }

  def effects(instruction: DecodedInstruction): Boolean = instruction.operation.form match {
    case _: Form.St | _: Form.Stg | _: Form.Stl | _: Form.Sts | _: Form.Atom | _: Form.Atoms | _: Form.Red |
        _: Form.Bar | _: Form.Cal | _: Form.Jcal | _: Form.Ldslk | _: Form.Stscul => true
    case _ => false
  }

  def registers(operand: Operand): Set[Int] = operand match {
    case Operand.Register(RegisterBank.R, index)                              => Set(index)
    case Operand.Group(values)                                                => values.flatMap(registers(_)).toSet
    case Operand.ConstantBank(_, offset)                                      => registers(offset)
    case Operand.Address(base = base, index = index, descriptor = descriptor) =>
      (base.toList ++ index.toList ++ descriptor.toList).flatMap(registers(_)).toSet ++
        base.toList.flatMap(registers(_)).map(_ + 1)
    case Operand.Unary(value = value) => registers(value)
    case Operand.Qualified(value, _)  => registers(value)
    case _                            => Set.empty
  }

  def wideModifier(instruction: DecodedInstruction): Boolean = instruction.modifiers.exists(
    PartialFunction.cond(_) {
      case Instruction.Modifier.`64` | Instruction.Modifier.`128` | Instruction.Modifier.F64 |
          Instruction.Modifier.S64 | Instruction.Modifier.U64 => true
    }
  )

  type Slot = Either[Int, Int]

  private val predicates: Set[Slot] = (0 to 6).map[Slot](Right(_)).toSet

  private def words(instruction: DecodedInstruction): Int = {
    val modifiers = instruction.modifiers
    if (modifiers.contains(Instruction.Modifier.`128`)) 4
    else if (modifiers.contains(Instruction.Modifier.WIDE) || wideModifier(instruction)) 2
    else 1
  }

  private def slots(operand: Operand, width: Int): Set[Slot] = OperandQueries.base(operand) match {
    case Operand.Predicate(Some(index), _, _)  => Set[Slot](Right(index))
    case value if OperandQueries.isZero(value) => Set.empty
    case value => registers(value).flatMap(first => first until first + width).map[Slot](Left(_))
  }

  private def destinations(instruction: DecodedInstruction): Set[Int] = instruction.operation.form match {
    case _: Form.St | _: Form.Stg | _: Form.Stl | _: Form.Sts | _: Form.Red | _: Form.Stscul | _: Form.Brx |
        _: Form.Brxu | _: Form.Jmx | _: Form.Jmxu =>
      Set.empty
    case _ =>
      val shuffled = OperandQueries.is[Form.Shfl](instruction.operation)
      instruction.operands.zipWithIndex.collect {
        case (_, 0)                                                                      => 0
        case (operand, 1) if shuffled || OperandQueries.isPredicateAllowingBoth(operand) => 1
      }.toSet
  }

  private def operands(instruction: DecodedInstruction, destination: Boolean) = {
    val positions = destinations(instruction)
    instruction.operands.zipWithIndex.collect { case (operand, index) if positions(index) == destination => operand }
  }

  def written(instruction: DecodedInstruction): Set[Slot] = {
    val width = words(instruction)
    val bank  = instruction.operation.form match {
      case _: Form.R2p => predicates
      case _           => Set.empty[Slot]
    }
    operands(instruction, destination = true).flatMap(slots(_, width)).toSet ++ bank
  }

  def uses(instruction: DecodedInstruction): Set[Slot] = {
    val width = words(instruction)
    val guard = instruction.guard.toSet.flatMap { case Instruction.Guard(predicate) => slots(predicate, 1) }
    val bank  = instruction.operation.form match {
      case _: Form.P2r => predicates
      case _           => Set.empty[Slot]
    }
    operands(instruction, destination = false).flatMap(slots(_, width)).toSet ++ guard ++ bank
  }

  def kills(instruction: DecodedInstruction): Set[Slot] =
    if (instruction.guard.isEmpty) written(instruction) else Set.empty
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

  private def polls(start: Int, end: Int): Boolean = {
    val indexes             = start to end
    def inside(target: Int) = start <= target && target <= end
    def leaves(index: Int)  = successors(index).forall(next => next.isEmpty || next.exists(!inside(_)))
    type Live = Map[Int, Set[LegacyFlowGraph.Slot]]
    def following(index: Int, live: Live): Set[LegacyFlowGraph.Slot] =
      successors(index).getOrElse(Vec.empty[Int]).filter(inside).flatMap(live.getOrElse(_, Set.empty)).toSet
    @tailrec
    def solve(live: Live): Live = {
      val next = indexes.foldRight(live) { (index, current) =>
        val instruction = instructions(index)
        val after       = following(index, current)
        val relevant    = leaves(index) || LegacyFlowGraph.written(instruction).exists(after)
        val before      =
          if (relevant) LegacyFlowGraph.uses(instruction) ++ (after -- LegacyFlowGraph.kills(instruction)) else after
        current.updated(index, before)
      }
      if (next == live) live else solve(next)
    }
    val live = solve(Map.empty)
    !indexes.exists(index => LegacyFlowGraph.effects(instructions(index))) && indexes.exists(index =>
      LegacyFlowGraph.reads(instructions(index)) &&
        LegacyFlowGraph.written(instructions(index)).exists(following(index, live))
    )
  }

  /** Backward branches closing a loop whose exit depends on a load; such a loop waits on other warps. */
  def pollingBranches: Set[Int] = instructions.zipWithIndex.collect {
    case (branch, end)
        if OperandQueries.is[Form.Bra](branch.operation) &&
          targets(branch).exists(_.exists(start => start <= end && polls(start, end))) =>
      branch.address.raw
  }.toSet
}
