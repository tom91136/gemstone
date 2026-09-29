package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

object Event {
  enum PrintArgument {
    case Integer(bits: Vec[Long], width: Int)
    case Floating(bits: Vec[Long], width: Int)
    case Pointer(address: Long)
    case Text(value: String)
  }
}

enum Event {
  case Print(format: String, arguments: Vec[Event.PrintArgument])
  case PointerRelocation(
      source: AllocationId,
      sourceOffset: Long >= 0,
      target: AllocationId,
      targetOffset: Long >= 0
  )
}
