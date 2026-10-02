package net.kurobako.gemstone.isa.nvidia

import VMIR.*

object VMExecution {
  enum WaitScope {
    case Warp
    case Workgroup
    case Device
    case System
    case External
  }

  case class WaitKey(scope: WaitScope, id: Long, phase: Long)

  enum Step {
    case Advance
    case Jump(target: Pc)
    case Split(taken: VMLaneMask, target: Pc, fallthrough: VMLaneMask)
    case Suspend(key: WaitKey)
    case Exit(mask: VMLaneMask)
    case Trap(fault: VMFault)
  }
}
