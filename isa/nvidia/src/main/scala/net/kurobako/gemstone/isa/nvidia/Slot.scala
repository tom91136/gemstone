package net.kurobako.gemstone.isa.nvidia

import VMIR.*

/** What a rendering names: a register or a predicate. */
private[isa] enum Slot {
  case Gpr(register: Register)
  case Pred(predicate: Predicate)
}
