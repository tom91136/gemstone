package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.annotation.tailrec
import scala.collection.mutable

private object LegacyControlFlow {
  enum Kind { case Break, Synchronisation }
  case class Setup(instruction: DecodedInstruction, kind: Kind, slot: Int in 0 ~ 15, target: Int >= 0)

  def setupKind(instruction: DecodedInstruction): Option[Kind] = instruction.operation match {
    case _: Instruction.Ssy => Some(Kind.Synchronisation)
    case _: Instruction.Pbk => Some(Kind.Break)
    case _                  => None
  }

  def transferKind(instruction: DecodedInstruction): Option[Kind] = instruction.operation match {
    case _: Instruction.Brk                                                     => Some(Kind.Break)
    case _: Instruction.Sync                                                    => Some(Kind.Synchronisation)
    case _ if instruction.modifiers.lastOption.contains(Instruction.Modifier.S) => Some(Kind.Synchronisation)
    case _                                                                      => None
  }

  def never(instruction: DecodedInstruction): Boolean =
    instruction.guard.exists(guard => VMOperand.isNeverPredicate(guard.predicate))

  /** Branches, returns, exits, kills and traps that never continue at the following instruction. A condition code makes
    * a branch conditional and a breakpoint without `TRAP` resumes; control-stack transfers are classified separately.
    */
  def terminates(instruction: DecodedInstruction): Boolean = instruction.guard.isEmpty &&
    (instruction.operation match {
      case _: Instruction.Bra =>
        !instruction.modifiers.contains(Instruction.Modifier.`CC`) && instruction.operands.size == 1
      case _: Instruction.Jmp | _: Instruction.Exit | _: Instruction.Ret | _: Instruction.Kill | _: Instruction.Brx |
          _: Instruction.Brxu | _: Instruction.Jmx => true
      case _: Instruction.Bpt => instruction.modifiers == Vec(Instruction.Modifier.TRAP)
      case _                  => false
    })

  /** `PRET` of the following instruction, then `BRX`: a call through a register. */
  def isIndirectCall(instructions: Vec[DecodedInstruction], index: Int): Boolean =
    index > 0 && index + 1 < instructions.size && instructions(index).guard.isEmpty &&
      VMOperand.is[Instruction.Brx](instructions(index).operation) && instructions(index - 1).guard.isEmpty &&
      VMOperand.is[Instruction.Pret](instructions(index - 1).operation) &&
      instructions(index - 1).operands.flatMap(VMOperand.integer(_)) == Vec(BigInt(instructions(index + 1).address.raw))

  /** Indirect calls enter functions, and every function after the first follows an unconditional terminator; trailing
    * NOP padding is not a function.
    */
  def functionEntries(instructions: Vec[DecodedInstruction]): Vec[Int] =
    if (!instructions.indices.exists(isIndirectCall(instructions, _))) Vec.empty
    else instructions.indices.drop(1).toVec.filter(index =>
      !instructions.drop(index).forall(instruction => VMOperand.is[Instruction.Nop](instruction.operation)) &&
        terminates(instructions(index - 1))
    )

  /** Stands in a joined stack for the tokens only some of the joined paths hold. */
  private val Divergent = -1

  private case class Analysis(
      setups: Map[Int, Setup],
      stacks: Map[Int, List[Int]],
      bypassed: Map[Int, Set[Int]],
      routines: Map[Int, Int],
      calls: Vec[(Int, Int)]
  )
}

/** Legacy SSY and PBK tokens, followed through the control-flow graph as the warp's control stack. Compilers may place
  * a token's blocks after its target, so a transfer pops whichever token its path pushed, not the lexically nearest.
  */
private class LegacyControlFlow(instructions: Vec[DecodedInstruction]) {
  import LegacyControlFlow.*

  private val indices: Map[Int, Int] = instructions.indices.map(index => instructions(index).address.raw -> index).toMap

  private def destination(instruction: DecodedInstruction): Option[Int] =
    instruction.operands.lastOption.flatMap(VMOperand.integer).filter(_.isValidInt).flatMap(value =>
      indices.get(value.toInt)
    )

  private def target(instruction: DecodedInstruction): Int >= 0 = {
    if (instruction.operands.size != 1 || instruction.guard.nonEmpty)
      throw IllegalArgumentException("unsupported legacy control-stack setup")
    val target = VMOperand.integer(instruction.operands.head).filter(_.isValidInt)
      .flatMap(_.toInt.refine[Int >= 0].toOption)
      .getOrElse(throw IllegalArgumentException("invalid legacy convergence target"))
    if (target <= instruction.address || !indices.contains(target.raw))
      throw IllegalArgumentException("legacy convergence target must be a forward instruction")
    target
  }

  private val calls: Vec[(Int, Int)] = instructions.indices.toVec.flatMap { index =>
    instructions(index).operation match {
      case _: Instruction.Cal => destination(instructions(index)).map(index -> _)
      case _                  => None
    }
  }

  private val indirectCalls: Vec[Int] = instructions.indices.toVec.filter(isIndirectCall(instructions, _))
  private val entries: Vec[Int]       = functionEntries(instructions)

  private def analyse(inherited: Map[Int, Set[Int]]): Analysis = {
    val tokens   = mutable.HashMap.empty[Int, (Kind, Int >= 0)]
    val stacks   = mutable.HashMap.empty[Int, List[Int]]
    val bypassed = mutable.HashMap.empty[Int, Set[Int]]
    val routines = mutable.HashMap.empty[Int, Int]
    val pending  = mutable.ArrayDeque.empty[Int]
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var live                                            = true
    def targets(token: Int, address: Int >= 0): Boolean = token != Divergent && tokens(token)._2 == address
    def inconsistent(index: Int): Nothing               =
      throw IllegalArgumentException(
        s"inconsistent legacy control stack at 0x${instructions(index).address.raw.toHexString}"
      )
    // arriving at a token's target reconverges it, so it and every token pushed after it leave the stack; paths joining
    // with different stacks keep what they share, and no later code may consume a token only some of them hold
    def reach(index: Int, path: List[Int], skipped: Set[Int], routine: Int): Unit = {
      val address = instructions(index).address
      if (skipped.exists(targets(_, address))) inconsistent(index)
      val stack = path.drop(path.lastIndexWhere(targets(_, address)) + 1)
      stacks.get(index) match {
        case Some(existing) if live =>
          val base = existing.reverseIterator.zip(stack.reverseIterator).takeWhile(_ == _).size
          val top  = existing.view.zip(stack).take(math.min(existing.size, stack.size) - base)
            .takeWhile(_ == _).size
          val joined = if (existing == stack) existing
          else (existing.take(top) ++ (Divergent :: existing.takeRight(base))).foldRight(List.empty[Int]) {
            case (Divergent, rest @ (Divergent :: _)) => rest
            case (token, rest)                        => token :: rest
          }
          val merged = bypassed(index) ++ skipped ++
            (existing.slice(top, existing.size - base) ++ stack.slice(top, stack.size - base)).filter(_ != Divergent)
          if (joined != existing || merged != bypassed(index)) {
            stacks(index) = joined
            bypassed(index) = merged
            pending.append(index)
          }
        case Some(_) => ()
        case None    =>
          stacks(index) = stack
          bypassed(index) = skipped
          routines(index) = routine
          pending.append(index)
      }
    }
    def step(index: Int): Unit = {
      val instruction                         = instructions(index)
      val stack                               = stacks(index)
      val skipped                             = bypassed(index)
      val routine                             = routines(index)
      def follow(index: Int, path: List[Int]) = reach(index, path, skipped, routine)
      def next(path: List[Int] = stack): Unit = if (index + 1 < instructions.size) follow(index + 1, path)
      if (never(instruction)) next()
      else (setupKind(instruction), transferKind(instruction)) match {
        case (Some(kind), _) =>
          tokens(index) = (kind, target(instruction))
          next(index :: stack)
        case (_, Some(kind)) =>
          val position = stack.indexWhere(token => token == Divergent || tokens(token)._1 == kind)
          if (position < 0) throw IllegalArgumentException("legacy control-stack transfer has no enclosing setup")
          if (stack(position) == Divergent) inconsistent(index)
          follow(indices(tokens(stack(position))._2.raw), stack.drop(position + 1))
          if (instruction.guard.nonEmpty) next()
        case _ =>
          instruction.operation match {
            case _: Instruction.Bra | _: Instruction.Jmp | _: Instruction.Pret => follow(
                destination(instruction).getOrElse(
                  throw IllegalArgumentException("legacy branch targets an unknown address")
                ),
                stack
              )
            case _ => ()
          }
          if (!terminates(instruction)) next()
      }
    }
    def drain(): Unit = while (pending.nonEmpty) step(pending.removeHead())
    reach(0, Nil, Set.empty, 0)
    calls.foreach((_, entry) => reach(entry, Nil, Set.empty, entry))
    drain()
    // functions entered through a register start their own stacks, as direct callees do
    val indirect = entries.filter { entry =>
      val unreached = !stacks.contains(entry)
      if (unreached) {
        reach(entry, Nil, Set.empty, entry)
        drain()
      }
      unreached
    }
    // code reached only indirectly, such as switch cases, or not at all falls through from its predecessor, as it
    // would in a lexical layout
    live = false
    instructions.indices.drop(1).foreach { index =>
      if (!stacks.contains(index)) {
        val previous = index - 1
        val path     = if (tokens.contains(previous)) previous :: stacks(previous) else stacks(previous)
        reach(index, path, bypassed(previous), routines(previous))
        drain()
      }
    }
    // tokens that may be live together, including tokens only some joined paths hold, take distinct slots
    val slots = mutable.HashMap.empty[Int, Int in 0 ~ 15]
    tokens.keys.toVec.sorted.foreach { index =>
      val overlapping = stacks(index).filter(_ != Divergent) ++ bypassed(index) ++
        tokens.keys.filter(other => stacks(other).contains(index) || bypassed(other)(index))
      val occupied = overlapping.flatMap(slots.get).map(_.raw).toSet ++ inherited.getOrElse(routines(index), Set.empty)
      slots(index) = Vec.range[0, 16].find(slot => !occupied(slot.raw))
        .getOrElse(throw IllegalArgumentException("legacy convergence nesting exceeds 16 tokens"))
    }
    Analysis(
      tokens.map { case (index, (kind, target)) => index -> Setup(instructions(index), kind, slots(index), target) }
        .toMap,
      stacks.toMap,
      bypassed.toMap,
      routines.toMap,
      calls ++ indirectCalls.flatMap(site => indirect.map(site -> _))
    )
  }

  private val analysis: Option[Analysis] = Option.when(
    instructions.exists(instruction => setupKind(instruction).nonEmpty || transferKind(instruction).nonEmpty)
  ) {
    // a callee's tokens share the warp's stack with every token live at its call sites, so they take other slots
    @tailrec def settle(inherited: Map[Int, Set[Int]]): Analysis = {
      val result  = analyse(inherited)
      val widened = result.calls.foldLeft(inherited) { case (slots, (site, entry)) =>
        slots.updated(
          entry,
          slots.getOrElse(entry, Set.empty) ++
            (result.stacks(site).filter(_ != Divergent) ++ result.bypassed(site)).map(result.setups(_).slot.raw) ++
            slots.getOrElse(result.routines(site), Set.empty)
        )
      }
      if (widened == inherited) result else settle(widened)
    }
    settle(Map.empty)
  }

  val setups: Vec[Setup] = analysis.fold(Vec.empty[Setup])(_.setups.toVec.sortBy(_._1).map(_._2))

  private def popped(instruction: DecodedInstruction): Option[(Setup, Vec[Setup])] =
    transferKind(instruction).filterNot(_ => never(instruction)).flatMap(kind =>
      analysis.map { result =>
        val stack    = result.stacks(indices(instruction.address.raw))
        val position = stack.indexWhere(token => token != Divergent && result.setups(token).kind == kind)
        if (position < 0) throw IllegalArgumentException("legacy control-stack transfer has no enclosing setup")
        (result.setups(stack(position)), Vec.from(stack.take(position)).map(result.setups))
      }
    )

  /** The token a transfer pops; a never-executed transfer pops none. */
  def transfer(instruction: DecodedInstruction): Option[Setup] = popped(instruction).map(_._1)

  /** Tokens above the popped one, innermost first, which the transfer also leaves. */
  def unwound(instruction: DecodedInstruction): Vec[Setup] = popped(instruction).fold(Vec.empty[Setup])(_._2)
}
