package net.kurobako.gemstone.vm

import net.kurobako.gemstone.{NonNegativeLong, Vec}

trait Event

object Event {
  final case class Print(format: String, arguments: Vec[PrintArgument]) extends Event
  final case class PointerRelocation(
      source: MemoryRegion,
      sourceOffset: NonNegativeLong,
      target: MemoryRegion,
      targetOffset: NonNegativeLong
  ) extends Event

  enum PrintArgument {
    case Integer(bits: Vec[Long], width: Int)
    case Floating(bits: Vec[Long], width: Int)
    case Pointer(address: Long)
    case Text(value: String)
  }
}
