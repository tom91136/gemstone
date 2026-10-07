package net.kurobako.gemstone.isa.nvidia

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*

/** Resolves symbolic branch targets of lowered NVIDIA code into program counters. */
object VMAssembler {
  enum Statement {
    case Label(name: NonEmpty[String])
    case Execute(instruction: Instruction, predication: Option[ExecutionPredicate] = None)
    case Branch(target: NonEmpty[String], predication: Option[ExecutionPredicate] = None)
  }

  def assemble(statements: Vec[Statement]): Either[NonEmpty[String], ExecutableProgram] = {
    val labels = statements.foldLeft[Either[NonEmpty[String], (Map[NonEmpty[String], Pc], Int >= 0)]](
      Right(Map.empty[NonEmpty[String], Pc] -> (0: Int >= 0))
    ) {
      case (Right((labels, pc)), Statement.Label(label)) =>
        Either.cond(!labels.contains(label), labels.updated(label, Pc(pc)) -> pc, s"duplicate label: $label")
      case (Right((labels, pc)), _) => Right(labels -> (pc + 1))
      case (left, _)                => left
    }
    labels.flatMap { (labels, _) =>
      statements.traverseFilter {
        case Statement.Label(_)                          => Right(None)
        case Statement.Execute(instruction, predication) => Right(Some(PredicatedInstruction(instruction, predication)))
        case Statement.Branch(target, predication)       =>
          labels.get(target).toRight[NonEmpty[String]](s"unknown label: $target")
            .map(pc => Some(PredicatedInstruction(Instruction.Branch(pc), predication)))
      }.map(ExecutableProgram(_))
    }
  }
}
