package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.LegacyFlowGraph.*
import scala.collection.mutable

/** Fuse legacy trigonometric reduction only when its opaque fixed-point result cannot escape. */
private object LegacyRangeReduction {
  private val ReductionModifiers = Set(Vec(Modifier.SINCOS), Vec(Modifier.EX2))

  private def indirect(instruction: DecodedInstruction): Boolean = instruction.operation.form match {
    case _: Form.Brx | _: Form.Brxu | _: Form.Jmx => true
    case _                                        => false
  }

  private def scalarDestination(instruction: DecodedInstruction): Option[Operand] = {
    val scalar = instruction.operation.form match {
      case _: Form.Mov | _: Form.Mov32i | _: Form.Rro | _: Form.Mufu |
          _: Form.Fmul | _: Form.Fmul32i | _: Form.Fadd | _: Form.Fadd32i |
          _: Form.Ffma | _: Form.Ffma32i | _: Form.Lop |
          _: Form.Lop32i | _: Form.Iadd | _: Form.Iadd32i | _: Form.Imnmx |
          _: Form.Iscadd | _: Form.Lea | _: Form.Shl | _: Form.Shr | _: Form.Sel |
          _: Form.Xmad | _: Form.Imad | _: Form.Icmp |
          _: Form.Ldg | _: Form.Ld | _: Form.Lds | _: Form.Ldl => true
      case _ => false
    }
    if (scalar && !wideModifier(instruction)) instruction.operands.headOption.filter(OperandQueries.isRegister(_))
    else None
  }

  private def words(instruction: DecodedInstruction): Int = if (instruction.modifiers.contains(Modifier.`128`)) 4 else 2

  /** The registers a 64- or 128-bit load writes. */
  private def wideLoadDestination(instruction: DecodedInstruction): Set[Int] = instruction.operation.form match {
    case _: Form.Ldg | _: Form.Ld | _: Form.Lds | _: Form.Ldl if wideModifier(instruction) =>
      instruction.operands.headOption.filter(OperandQueries.isRegister(_)).toSet
        .flatMap(registers(_)).flatMap(first => first until first + words(instruction))
    case _ => Set.empty
  }

  private def writesPredicate(instruction: DecodedInstruction, guard: Instruction.Guard): Boolean = guard match {
    case Instruction.Guard(Operand.Predicate(Some(index), _, _)) => written(instruction)(Right(index))
    case _                                                       => false
  }

  private enum Phase derives CanEqual {
    case Reduced
    case Redefined(guard: Instruction.Guard)
    case Mixed
  }

  /** The shadow register each fused reduction's consumers read, by consumer address, and how many shadows there are. */
  case class Reductions(inputs: Map[Int, VMIR.Value.Register], count: Int >= 0)

  def inputs(sm: Int, function: DecodedFunction, layout: VMRegisters.Layout): Reductions =
    if (sm >= 70) Reductions(Map.empty, 0)
    else {
      val instructions = function.instructions
      val reductions   = instructions.zipWithIndex.filter((instruction, _) =>
        OperandQueries.is[Form.Rro](instruction.operation)
      )
      if (reductions.nonEmpty && instructions.exists(indirect(_)))
        throw IllegalArgumentException("legacy range reduction has indirect control flow")
      if (reductions.isEmpty) Reductions(Map.empty, 0)
      else {
        val graph    = LegacyFlowGraph(instructions)
        val resolved = instructions.indices.toVec.map(graph.successors(_))
        val unknown  = resolved.indexWhere(_.isEmpty)
        if (unknown >= 0)
          throw IllegalArgumentException(
            s"legacy range reduction has unresolved control flow at 0x${instructions(unknown).address.raw.toHexString}"
          )
        val successors   = resolved.map(_.get)
        val predecessors = instructions.indices.toVec.flatMap(index => successors(index).map(_ -> index))
          .toList.groupMap(_._1)(_._2)
        val mapped = reductions.zipWithIndex.foldLeft(Map.empty[Int, VMIR.Value.Register]) {
          case (result, ((producer, index), ordinal)) =>
            def reject(detail: String = "unverified form"): Nothing = throw IllegalArgumentException(
              s"RRO at 0x${producer.address.raw.toHexString} requires a closed, straight-line MUFU trigonometric lifetime $detail"
            )
            if (
              producer.guard.nonEmpty || !ReductionModifiers(producer.modifiers) ||
              producer.operands.size != 2
            ) reject()
            // EX2 reductions feed MUFU.EX2, the others MUFU.SIN and MUFU.COS
            val consumers =
              if (producer.modifiers == Vec(Modifier.EX2)) Set(Vec(Modifier.EX2))
              else Set(Vec(Modifier.SIN), Vec(Modifier.COS))
            val destination                 = scalarDestination(producer).getOrElse(reject())
            val register                    = registers(destination).head
            val shadow: VMIR.Value.Register = VMIR.Value.Register(layout.reduction(ordinal))

            def writes(instruction: DecodedInstruction): Boolean =
              scalarDestination(instruction).exists(registers(_) == Set(register)) ||
                wideLoadDestination(instruction)(register)
            def wide(instruction: DecodedInstruction): Boolean =
              wideModifier(instruction) ||
                (instruction.operation.form match {
                  case _: Form.Dadd | _: Form.Dmul | _: Form.Dfma | _: Form.Dsetp | _: Form.Dset | _: Form.Dmnmx => true
                  case _ => false
                })
            // an address names its whole register pair, so only data operands widen
            def aliases(operand: Operand, width: Int): Boolean = operand match {
              case Operand.Address(_, _, _, _, _, _) => false
              case _ => registers(operand).exists(r => r <= register && register < r + width)
            }

            val seen   = mutable.Set.empty[(Int, Phase)]
            val reads  = mutable.Set.empty[Int]
            val frames = mutable.Stack.empty[(Int, Phase)]
            successors(index).foreach(next => frames.push((next, Phase.Reduced)))
            while (frames.nonEmpty) {
              val frame @ (position, phase) = frames.pop()
              if (seen.add(frame)) {
                if (position >= instructions.size) reject("past the end of the function")
                val next    = instructions(position)
                val loaded  = wideLoadDestination(next)
                val sources =
                  if (scalarDestination(next).nonEmpty || loaded.nonEmpty) next.operands.tail else next.operands
                val referenced = sources.exists(registers(_)(register))
                if (wide(next) && sources.exists(aliases(_, words(next))))
                  reject(s"with wide alias at 0x${next.address.raw.toHexString}")
                if (referenced) phase match {
                  case Phase.Reduced =>
                    val trigonometric = OperandQueries.is[Form.Mufu](next.operation)
                    if (
                      !trigonometric || next.guard.nonEmpty || next.operands.size != 2 ||
                      OperandQueries.without(next.operands(1), OperandSelector.reuse) != destination ||
                      !consumers(next.modifiers)
                    ) reject(s"read by ${next.mnemonic.raw} at 0x${next.address.raw.toHexString}")
                    reads += position
                  case Phase.Redefined(guard) =>
                    if (!next.guard.contains(guard))
                      reject(s"read after a predicated write at 0x${next.address.raw.toHexString}")
                  case Phase.Mixed => reject(s"read of an unknown value at 0x${next.address.raw.toHexString}")
                }
                val onward = successors(position)
                val after  = phase match {
                  case Phase.Redefined(guard) if writesPredicate(next, guard) => Phase.Mixed
                  case other                                                  => other
                }
                if (writes(next)) next.guard.foreach { guard =>
                  val updated = after match {
                    case Phase.Reduced                                => Phase.Redefined(guard)
                    case Phase.Redefined(earlier) if earlier == guard => after
                    case _                                            => Phase.Mixed
                  }
                  onward.foreach(following => frames.push((following, updated)))
                }
                else {
                  if (next.operands.headOption.exists(registers(_)(register)) && !referenced) reject()
                  // lanes taking an edge guarded by the redefinition's own predicate have performed the redefinition
                  def redefined(following: Int): Boolean = (phase, next.guard) match {
                    case (Phase.Redefined(earlier), Some(taken)) =>
                      val fallthrough = following == position + 1
                      if (taken == earlier) !fallthrough
                      else taken.predicate == earlier.predicate.copy(negated = !earlier.predicate.negated) &&
                      fallthrough
                    case _ => false
                  }
                  onward.filterNot(redefined(_)).foreach(following => frames.push((following, after)))
                }
              }
            }
            if (reads.isEmpty) reject()

            // every route to a consumer must pass this reduction without another definition of the register
            val visited = mutable.Set.empty[Int]
            val pending = mutable.Stack.from(reads.toList)
            while (pending.nonEmpty) {
              val position = pending.pop()
              val before   = predecessors.getOrElse(position, Nil)
              if (before.isEmpty)
                reject(s"entered without a reduction before 0x${instructions(position).address.raw.toHexString}")
              before.foreach { predecessor =>
                if (predecessor != index && visited.add(predecessor)) {
                  val definition = instructions(predecessor)
                  if (
                    writes(definition) ||
                    definition.operands.headOption.exists(head =>
                      OperandQueries.isRegister(head) && registers(head)(register)
                    )
                  ) reject(s"redefined at 0x${definition.address.raw.toHexString}")
                  pending.push(predecessor)
                }
              }
            }
            reads.foldLeft(result.updated(producer.address, shadow)) { (current, position) =>
              val address = instructions(position).address.raw
              if (current.get(address).exists(_ != shadow))
                reject(s"shared with another reduction at 0x${address.toHexString}")
              current.updated(address, shadow)
            }
        }
        Reductions(mapped, reductions.nonNegativeSize)
      }
    }
}
