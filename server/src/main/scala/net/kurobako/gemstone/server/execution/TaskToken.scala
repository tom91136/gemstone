package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.vm.ObservationContext

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class TaskToken(value: Long :| Positive) {
  def observation: ObservationContext = ObservationContext(value)
}
