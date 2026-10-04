package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object VMExecution {
  enum WaitScope {
    case Warp
    case Workgroup
    case Device
    case System
    case External
  }

  case class WaitKey(scope: WaitScope, id: Long, phase: Long, frame: Int = 0)

  case class WorkgroupArrival(id: Int, count: Int, lanes: VMLaneMask)

  case class DeviceCall(function: DeviceFunction, lanes: VMLaneMask)

  enum Step {
    case Advance
    case Jump(target: Pc)
    case Split(taken: VMLaneMask, target: Pc, fallthrough: VMLaneMask)
    case Suspend(key: WaitKey)
    case ClusterBarrier(arrive: Boolean)
    case WorkgroupBarrier(arrivals: Vec[WorkgroupArrival], blocking: Boolean)
    case Call(
        calls: Vec[DeviceCall],
        parameters: Vec[NonEmpty[String]],
        returnParameters: Vec[NonEmpty[String]],
        completed: VMLaneMask
    )
    case Return(mask: VMLaneMask)
    case Exit(mask: VMLaneMask)
    case Trap(fault: VMFault)
  }
}
