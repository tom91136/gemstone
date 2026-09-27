package net.kurobako.gemstone.vm

import net.kurobako.gemstone.Constraint.Positive

case class TaskLimits(
    maxInstructions: Positive[Long],
    quantum: Positive[Int]
)

object TaskLimits {
  val Default: TaskLimits =
    TaskLimits(maxInstructions = 100000000L, quantum = 4096)
}
