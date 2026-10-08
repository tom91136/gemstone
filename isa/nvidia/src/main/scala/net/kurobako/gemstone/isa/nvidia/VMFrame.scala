package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*

object VMFrame {

  /** A frame's return point: caller parameter `targets(i)` receives callee return parameter `sources(i)`. */
  case class Continuation(caller: VMFrame, pc: Pc, targets: Vec[Parameter], sources: Vec[Parameter])
}

/** One activation of a routine: its lanes' registers and the base of the thread-local window its explicit local
  * accesses are relative to.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class VMFrame(
    val routine: Routine,
    val lanes: VMLanes,
    val localBase: Long,
    val continuation: Option[VMFrame.Continuation],
    var live: VMLaneMask
)
