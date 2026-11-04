package net.kurobako.gemstone.isa.nvidia

/** The storage an atomic acts on, which its address space selects. */
private enum AtomicTarget derives CanEqual {
  case Device, Shared, Local
}
