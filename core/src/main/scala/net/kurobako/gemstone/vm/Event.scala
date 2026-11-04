package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

object Event {
  enum PrintStream derives CanEqual {
    case Stdout, Stderr
  }

  enum PrintArgument {
    case Integer(bits: Vec[Long], width: Int in 1 ~ 64)
    case Floating(bits: Vec[Long], width: Int in (16, 32, 64))
    case Pointer(address: Long)
    case Text(value: String)
  }
}

enum Event {
  case Print(format: String, arguments: Vec[Event.PrintArgument], stream: Event.PrintStream = Event.PrintStream.Stdout)
  case PointerRelocation(
      source: AllocationId,
      sourceOffset: Long >= 0,
      target: AllocationId,
      targetOffset: Long >= 0
  )
}
