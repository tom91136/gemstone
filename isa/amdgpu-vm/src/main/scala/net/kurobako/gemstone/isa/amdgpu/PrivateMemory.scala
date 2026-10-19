package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.prelude.*

/** A lane's private memory by byte offset. */
private[isa] trait PrivateMemory extends Memory[UInt[64]]
