package net.kurobako.gemstone.vm

import net.kurobako.gemstone.Constraint.NonNegative
import net.kurobako.gemstone.Vec

trait Event

object Event {
  case class Print(format: String, arguments: Vec[PrintArgument]) extends Event
  case class PointerRelocation(
      source: MemoryRegion,
      sourceOffset: NonNegative[Long],
      target: MemoryRegion,
      targetOffset: NonNegative[Long]
  ) extends Event

  enum PrintArgument {
    case Integer(bits: Vec[Long], width: Int)
    case Floating(bits: Vec[Long], width: Int)
    case Pointer(address: Long)
    case Text(value: String)
  }
}
