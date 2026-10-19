package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

private object VMExecution {
  type BarrierId    = Int in 0 ~ 15
  type BarrierCount = Int where (Between[0, 1024] & MultipleOf[32])

  case class WorkgroupArrival(id: BarrierId, count: BarrierCount, lanes: VMLaneMask)

  case class BarrierReduction(result: BarrierResult, predicate: Predicate, negated: Boolean)

  case class DeviceCall(function: DeviceFunction, lanes: VMLaneMask)

  case class Arm(target: Pc, lanes: VMLaneMask)

  // Shared results keep the per-instruction path from allocating one per step.
  val Completed: Either[Fault, Unit] = Right(())
  val Advanced: Either[Fault, Step]  = Right(Step.Advance)

  enum Step derives CanEqual {
    case Advance
    case Jump(target: Pc)

    /** Each arm's lanes continue at its target, in order, and `fallThrough` at the next instruction. */
    case Branches(arms: Vec[Arm], fallThrough: VMLaneMask)
    case WarpSync(memberMask: Value)
    case ConvergenceSync(token: Register)
    case ClusterBarrier(arrive: Boolean)
    case WorkgroupBarrier(arrivals: Vec[WorkgroupArrival], blocking: Boolean, reduction: Option[BarrierReduction])
    case Call(calls: Vec[DeviceCall], parameters: Vec[Parameter], returnParameters: Vec[Parameter])
    case Return(mask: VMLaneMask)
    case Exit(mask: VMLaneMask)
  }
}
