package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.prelude.*

/** The LDS a lane reads and writes by byte address; addresses past the workgroup's allocation read zero and drop
  * writes.
  */
private[isa] trait LocalMemory extends Memory[UInt[32]]
