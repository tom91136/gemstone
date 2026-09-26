package net.kurobako.gemstone.vm

import net.kurobako.gemstone.{PositiveInt, PositiveLong}

final case class TaskLimits(
    maxInstructions: PositiveLong,
    quantum: PositiveInt
)

object TaskLimits {
  val Default: TaskLimits =
    TaskLimits(maxInstructions = PositiveLong(100000000L), quantum = PositiveInt(4096))
}
