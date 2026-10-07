package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.vm.*

enum AtomicUpdate {
  case Operation(operation: AtomicOperation, value: Long)
  case CompareExchange(comparator: Long, value: Long)
}
