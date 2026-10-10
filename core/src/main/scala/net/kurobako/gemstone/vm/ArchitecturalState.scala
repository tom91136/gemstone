package net.kurobako.gemstone.vm

/** A backend's read-only view of the invocation, warp or wave about to execute an instruction, in its ISA's own terms.
  * It is valid only during the observer call that received it.
  */
trait ArchitecturalState
