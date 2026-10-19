package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.prelude.*

/** Global memory by 64-bit address. */
private[isa] trait GlobalMemory extends Memory[UInt[64]]
