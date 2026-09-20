package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.execution.ExecutionLimits

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** Server-owned configuration for the execution safety boundary. */
private[gemstone] object ExecutionConfiguration {
  val MaxInstructionsEnvironment: String :| (MinLength[1] & NoNul) = "GEMSTONE_EXECUTION_MAX_INSTRUCTIONS"
  val QuantumEnvironment: String :| (MinLength[1] & NoNul)         = "GEMSTONE_EXECUTION_QUANTUM"

  def limits(environment: Map[String, String]): Either[String, ExecutionLimits] = {
    val maximum = environment.get(MaxInstructionsEnvironment) match {
      case None        => Right(ExecutionLimits.Default.maxInstructions)
      case Some(value) => value.toLongOption
          .toRight(s"$MaxInstructionsEnvironment must be a positive integer: $value")
          .flatMap(_.refineEither[Positive].left.map(_ =>
            s"$MaxInstructionsEnvironment must be positive: $value"
          ))
    }
    val quantum = environment.get(QuantumEnvironment) match {
      case None        => Right(ExecutionLimits.Default.quantum)
      case Some(value) => value.toIntOption
          .toRight(s"$QuantumEnvironment must be a positive integer: $value")
          .flatMap(_.refineEither[Positive].left.map(_ =>
            s"$QuantumEnvironment must be positive: $value"
          ))
    }
    for {
      maxInstructions <- maximum
      quantumValue    <- quantum
    } yield ExecutionLimits(maxInstructions, quantumValue)
  }
}
