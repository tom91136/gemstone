package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

private object LegacyControlFlow {
  enum Kind { case Break, Synchronization }
  case class Setup(instruction: DecodedInstruction, kind: Kind, slot: Int in 0 ~ 15, target: Int >= 0)

  def setupKind(instruction: DecodedInstruction): Option[Kind] = instruction.operation match {
    case _: Instruction.Ssy => Some(Kind.Synchronization)
    case _: Instruction.Pbk => Some(Kind.Break)
    case _                  => None
  }
}

/** The forward, lexically nested control tokens supported by legacy SASS execution. */
private class LegacyControlFlow(instructions: Vec[DecodedInstruction]) {
  import LegacyControlFlow.*

  val setups: Vec[Setup] = instructions.foldLeft(Vec.empty[Setup]) { (previous, instruction) =>
    setupKind(instruction).fold(previous) { kind =>
      if (instruction.operands.size != 1 || instruction.guard.nonEmpty)
        throw IllegalArgumentException("unsupported legacy control-stack setup")
      val target = VMOperand.integer(instruction.operands.head).filter(_.isValidInt)
        .flatMap(_.toInt.refine[Int >= 0].toOption)
        .getOrElse(throw IllegalArgumentException("invalid legacy convergence target"))
      if (target <= instruction.address || !instructions.exists(_.address == target))
        throw IllegalArgumentException("legacy convergence target must be a forward instruction")
      val occupied = previous.filter(_.target > instruction.address).map(_.slot).toSet
      val slot     = Vec.range[0, 16].find(index => !occupied(index))
        .getOrElse(throw IllegalArgumentException("legacy convergence nesting exceeds 16 tokens"))
      previous :+ Setup(instruction, kind, slot, target)
    }
  }

  def transfer(instruction: DecodedInstruction): Option[Setup] = {
    val kind = instruction.operation match {
      case _: Instruction.Brk                                                     => Some(Kind.Break)
      case _: Instruction.Sync                                                    => Some(Kind.Synchronization)
      case _ if instruction.modifiers.lastOption.contains(Instruction.Modifier.S) => Some(Kind.Synchronization)
      case _                                                                      => None
    }
    kind.map { selected =>
      val selectedSetup = setups.findLast(setup =>
        setup.kind == selected && setup.instruction.address < instruction.address &&
          instruction.address < setup.target
      )
        .getOrElse(throw IllegalArgumentException("legacy control-stack transfer has no enclosing setup"))
      val bypass = instructions.exists { branch =>
        (branch.address < selectedSetup.instruction.address || branch.address >= selectedSetup.target) &&
        (branch.operation match {
          case _: Instruction.Bra | _: Instruction.Ssy | _: Instruction.Pbk => branch.operands.exists(operand =>
              VMOperand.integer(operand).exists(target =>
                target > selectedSetup.instruction.address.raw && target < selectedSetup.target.raw
              )
            )
          case _ => false
        })
      }
      val reentry = instructions.exists { branch =>
        branch.address > selectedSetup.instruction.address && branch.address < selectedSetup.target &&
        (branch.operation match {
          case _: Instruction.Bra => branch.operands.exists(operand =>
              VMOperand.integer(operand).exists(target =>
                target <= selectedSetup.instruction.address.raw
              )
            )
          case _ => false
        })
      }
      if (bypass || reentry) throw IllegalArgumentException("control flow bypasses or repeats legacy convergence setup")
      selectedSetup
    }
  }
}
