package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.isa.sass.*
import scala.annotation.tailrec

/** The instructions of a SASS function that depend on a code address, and the targets they name. */
private[check] object SASSAnchors {

  case class Anchor(at: Int, target: Int)

  // a target's database need not hold the relative form of a mnemonic that reads the program counter, as LEPC does
  private lazy val relative: Set[String] =
    Target.all.flatMap(_.database.forms.filter(_.relative).flatMap(_.opcodes.map(_.name))).toSet

  @tailrec private def supplies(operand: Operand): Boolean = operand match {
    case Operand.Unary(value, _, _, _)                                        => supplies(value)
    case Operand.Integer(_) | Operand.Bits(_, _) | Operand.Predicate(_, _, _) => false
    case Operand.Register(bank, _)                                            => bank != RegisterBank.B
    case _                                                                    => true
  }

  def address(operand: Operand): Option[BigInt] = operand match {
    case Operand.Integer(value) => Some(value)
    case Operand.Bits(value, _) => Some(value)
    case _                      => None
  }

  /** The anchors of a function, or the first instruction whose target it does not give. */
  def of(target: Target, instructions: Vec[DecodedInstruction], graph: ControlFlowGraph): Either[Int, Vec[Anchor]] = {
    val addresses = instructions.map(instruction => BigInt(instruction.address.raw)).zipWithIndex.toMap
    val leaving   = graph.blocks.zipWithIndex
      .filter((block, index) => block.open || block.successors.exists(_ != index + 1))
      .map((block, _) => block.last).toSet
    instructions.zipWithIndex.foldLeft[Either[Int, Vec[Anchor]]](Right(Vec.empty[Anchor])) {
      case (result, (instruction, index)) => result.flatMap { found =>
          val direct = leaving(index) ||
            instruction.machine.exists(machine => target.database.byForm(machine.form).relative)
          if (!direct && !relative(instruction.mnemonic.raw)) Right(found)
          else if (instruction.operands.exists(supplies)) Left(index)
          else if (!direct) Right(found)
          else
            instruction.operands.lastOption.flatMap(address).flatMap(addresses.get)
              .map(reached => found :+ Anchor(index, reached)).toRight(index)
        }
    }
  }
}
