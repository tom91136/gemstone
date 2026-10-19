package net.kurobako.gemstone.isa

import net.kurobako.gemstone.collection.*

enum DecodeProblem derives CanEqual {
  case UnknownOpcode
  case InvalidOperandCount(expected: Vec[Int], actual: Int)
  case AmbiguousDefinitions(definitions: Vec[String])
}
