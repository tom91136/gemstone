package net.kurobako.gemstone.vm

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

object GlobalBinding {
  def validated(
      name: NonEmpty[String],
      region: AllocationId,
      offset: Long >= 0,
      size: Long >= 0,
      regionSize: Long > 0
  ): Either[Fault, GlobalBinding] =
    Either.cond(
      offset.raw <= regionSize.raw && size.raw <= regionSize.raw - offset.raw,
      GlobalBinding(region, offset, size, regionSize),
      Fault.InvalidRequest(s"global '$name' does not fit its bound memory region")
    )

  /** Binding a global again to the same storage is idempotent; `Right(true)` marks a first binding. */
  def rebind[A](name: NonEmpty[String], previous: Option[A], next: A): Either[Fault, Boolean] =
    previous.fold[Either[Fault, Boolean]](Right(true))(existing =>
      Either.cond(
        existing == next,
        false,
        Fault.InvalidRequest(s"global '$name' is already bound to different storage")
      )
    )
}

/** Storage for a program global, validated to hold `size` bytes at `offset` within a `regionSize`-byte region. */
case class GlobalBinding private (
    region: AllocationId,
    offset: Long >= 0,
    size: Long >= 0,
    regionSize: Long > 0
) {
  def contains(offset: Long >= 0, length: Long >= 0): Boolean =
    offset.raw <= size.raw && length.raw <= size.raw - offset.raw
}
