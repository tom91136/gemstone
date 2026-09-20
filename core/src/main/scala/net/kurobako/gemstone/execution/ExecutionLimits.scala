package net.kurobako.gemstone.execution

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class ExecutionLimits(
    maxInstructions: Long :| Positive,
    quantum: Int :| Positive
)

object ExecutionLimits {
  val Default: ExecutionLimits = ExecutionLimits(maxInstructions = 100000000L, quantum = 4096)
}
