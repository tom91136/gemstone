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
      name: String,
      parameters: Vec[(String, Int)],
      statements: Vec[Statement],
      sharedBytes: Long,
      globalSymbols: Map[NonEmpty[String], GuestAddress]
  ): Either[String, VMIR.Kernel] =
    for {
      kernelName <- requiredName(name)
      params     <- parameters.traverse { case (parameter, size) =>
        for {
          parameterName <- requiredName(parameter)
          parameterSize <- size.refine[Int > 0].left.map(identity)
        } yield VMIR.KernelParameter(parameterName, parameterSize)
      }
      program <- compileProgram(statements)
      _       <- Either.cond(sharedBytes >= 0L, (), "negative shared memory size")
    } yield VMIR.Kernel(
      kernelName,
      params,
      program,
      sharedSymbols = Map.empty,
      globalSymbols = globalSymbols,
      registerChunkCount = 96,
      isaName = "SASS"
    )

  private def compileProgram(
      statements: Vec[Statement]
  ): Either[String, VMIR.ExecutableProgram[VMIR.PredicatedInstruction]] = {
    val labelState = statements.foldLeft[Either[String, (Map[String, Pc], Int)]](Right(Map.empty[String, Pc] -> 0)) {
      case (Right((labels, pc)), Statement.Label(label)) =>
        if (labels.contains(label)) Left(s"duplicate SASS label: $label")
        else Right(labels.updated(label, pc.unsafe[Pc]) -> pc)
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
          labels.get(target).toRight(s"unknown SASS branch label $target")
            .map(pc => Some(VMIR.PredicatedInstruction(Instruction.Branch(pc), predication)))
      }.map(instructions => VMIR.ExecutableProgram(instructions))
    }
  }

  private def requiredName(value: String): Either[String, NonEmpty[String]] =
    value.refine[NonEmpty[String]].left.map(identity)
}
