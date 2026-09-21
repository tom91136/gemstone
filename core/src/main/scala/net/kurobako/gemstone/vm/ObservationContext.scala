package net.kurobako.gemstone.vm

/** Host-assigned correlation value for events emitted during one execution. It has no transport representation. */
final case class ObservationContext(value: Long)

object ObservationContext {
  val Unscoped: ObservationContext = ObservationContext(0L)
}
