package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*

private object VMFrame {

  /** A frame's return point: caller parameter `targets(i)` receives callee return parameter `sources(i)`. */
  case class Continuation(caller: VMFrame, pc: Pc, targets: Vec[Parameter], sources: Vec[Parameter])
}

/** One activation of a routine: its lanes' registers and the base of the thread-local window its explicit local
  * accesses are relative to.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private class VMFrame(
    val routine: Routine,
    val lanes: VMLanes,
    val localBase: Long,
    val continuation: Option[VMFrame.Continuation],
    var live: VMLaneMask
) {

  /** The interpreter's handle for this frame in its ready queue, which assigns it when it first queues a cohort. */
  var readyId = 0
}
