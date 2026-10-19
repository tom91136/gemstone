package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.prelude.*

/** Flat addresses, which the LDS and private apertures route to those memories and others to global memory. */
private[isa] trait FlatMemory extends Memory[UInt[64]]
