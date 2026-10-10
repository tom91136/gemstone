package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.vm.*

private enum AtomicUpdate {
  case Operation(operation: AtomicOperation, value: Long)
  case CompareExchange(comparator: Long, value: Long)
}
