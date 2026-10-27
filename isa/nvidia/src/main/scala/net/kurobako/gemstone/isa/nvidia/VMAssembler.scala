package net.kurobako.gemstone.isa.nvidia

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import scala.collection.mutable

/** Resolves symbolic branch targets of lowered NVIDIA code into program counters. */
private[isa] object VMAssembler {
  enum Statement {
    case Label(name: NonEmpty[String])
    case Execute(instruction: Instruction, predication: Option[ExecutionPredicate] = None)
    case Branch(target: NonEmpty[String], predication: Option[ExecutionPredicate] = None)
    case BranchTable(index: Value, targets: Vec[NonEmpty[String]], predication: Option[ExecutionPredicate] = None)

    /** Attributes the instructions that follow, up to the next marker, to `address` in the source ISA. */
    case Source(address: Long >= 0)
  }

  def assemble(statements: Vec[Statement]): Either[NonEmpty[String], ExecutableProgram] = {
    val labels = statements.foldLeft[Either[NonEmpty[String], (Map[NonEmpty[String], Pc], Int >= 0)]](
      Right(Map.empty[NonEmpty[String], Pc] -> (0: Int >= 0))
    ) {
      case (Right((labels, pc)), Statement.Label(label)) =>
        Either.cond(!labels.contains(label), labels.updated(label, Pc(pc)) -> pc, s"duplicate label: $label")
      case (Right((labels, pc)), Statement.Source(_)) => Right(labels -> pc)
      case (Right((labels, pc)), _)                   => Right(labels -> (pc + 1))
      case (left, _)                                  => left
    }
    labels.flatMap { (labels, _) =>
      val instructions = Vec.newBuilder[PredicatedInstruction]
      val sources      = mutable.ArrayBuilder.ofLong()
      statements.foldLeft[Either[NonEmpty[String], Long]](Right(ExecutableProgram.NoSource)) {
        case (Right(_), Statement.Source(address))                        => Right(address)
        case (Right(source), Statement.Label(_))                          => Right(source)
        case (Right(source), Statement.Execute(instruction, predication)) =>
          instructions += PredicatedInstruction(instruction, predication)
          sources.addOne(source)
          Right(source)
        case (Right(source), Statement.Branch(target, predication)) =>
          labels.get(target).toRight[NonEmpty[String]](s"unknown label: $target").map { pc =>
            instructions += PredicatedInstruction(Instruction.Branch(pc), predication)
            sources.addOne(source)
            source
          }
        case (Right(source), Statement.BranchTable(index, targets, predication)) =>
          targets.traverse(target => labels.get(target).toRight[NonEmpty[String]](s"unknown label: $target")).map {
            pcs =>
              instructions += PredicatedInstruction(Instruction.BranchTable(index, pcs), predication)
              sources.addOne(source)
              source
          }
        case (left, _) => left
      }.map { _ =>
        val attributed = Vec.unsafeWrapArray(sources.result())
        ExecutableProgram(
          instructions.result(),
          if (attributed.forall(_ == ExecutableProgram.NoSource)) Vec.empty else attributed
        )
      }
    }
  }
}
