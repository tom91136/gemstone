package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

/** Assembles typed SASS-lowered instructions into the shared NVIDIA VM program. */
private object VMAssembler {
  enum Statement {
    case Label(name: NonEmpty[String])
    case Execute(instruction: Instruction, predication: Option[ExecutionPredicate] = None)
    case Branch(target: NonEmpty[String], predication: Option[ExecutionPredicate] = None)
  }

  def compile(
      name: NonEmpty[String],
      parameters: Vec[KernelParameter],
      statements: Vec[Statement],
      globalSymbols: Map[NonEmpty[String], GuestAddress]
  ): Either[NonEmpty[String], VMIR.Kernel] =
    compileProgram(statements).map(program =>
      VMIR.Kernel(
        name,
        parameters,
        program,
        sharedSymbols = Map.empty,
        globalSymbols = globalSymbols,
        registerChunkCount = 96,
        isaName = "SASS"
      )
    )

  private def compileProgram(
      statements: Vec[Statement]
  ): Either[NonEmpty[String], VMIR.ExecutableProgram[VMIR.PredicatedInstruction]] = {
    val labelState = statements.foldLeft[Either[
      NonEmpty[String],
      (Map[NonEmpty[String], Pc], Int >= 0)
    ]](Right(Map.empty[NonEmpty[String], Pc] -> (0: Int >= 0))) {
      case (Right((labels, pc)), Statement.Label(label)) =>
        if (labels.contains(label)) Left(s"duplicate SASS label: $label")
        else Right(labels.updated(label, Pc(pc)) -> pc)
      case (Right((labels, pc)), _: Statement.Execute) => Right(labels -> (pc + 1))
      case (Right((labels, pc)), _: Statement.Branch)  => Right(labels -> (pc + 1))
      case (left @ Left(_), _)                         => left
    }
    labelState.flatMap { case (labels, _) =>
      statements.traverseFilter {
        case Statement.Label(_)                          => Right(None)
        case Statement.Execute(instruction, predication) =>
          Right(Some(VMIR.PredicatedInstruction(instruction, predication)))
        case Statement.Branch(target, predication) =>
          labels.get(target).toRight[NonEmpty[String]](s"unknown SASS branch label $target")
            .map(pc => Some(VMIR.PredicatedInstruction(Instruction.Branch(pc), predication)))
      }.map(instructions => VMIR.ExecutableProgram(instructions))
    }
  }

}
