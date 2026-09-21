package net.kurobako.gemstone.vm

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class TaskLimits(
    maxInstructions: Long :| Positive,
    quantum: Int :| Positive
)

object TaskLimits {
  val Default: TaskLimits = TaskLimits(maxInstructions = 100000000L, quantum = 4096)
}
