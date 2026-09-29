package net.kurobako.gemstone.isa.ptx.v9_3

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

private object VMExecution {
  enum WaitScope {
    case Warp
    case Workgroup
    case Device
    case System
    case External
  }

  case class WaitKey(scope: WaitScope, id: Long, phase: Long)

  object ExecutionFault {
    def fromMemory(error: MemoryError, context: String = ""): ExecutionFault =
      error match {
        case MemoryError.Cancelled => Cancelled
        case other                 => Memory(s"$context${other.message}".unsafe[NonEmpty[String]])
      }

    def fromControl(failure: Fault): ExecutionFault = failure match {
      case Fault.BudgetExceeded(limit) => ExecutionFault.BudgetExceeded(limit)
      case Fault.Cancelled             => ExecutionFault.Cancelled
      case other                       => ExecutionFault.InvalidState(other.message)
    }
  }

  enum ExecutionFault {
    case Unsupported(opcode: NonEmpty[String], detail: NonEmpty[String])
    case InvalidProgram(detail: NonEmpty[String])
    case InvalidState(detail: NonEmpty[String])
    case UndefinedRegister(name: NonEmpty[String], pc: Pc, lane: Int)
    case Memory(detail: NonEmpty[String])
    case ArchitecturalTrap(isa: NonEmpty[String], code: Long, pc: Pc)
    case BudgetExceeded(limit: Long > 0)
    case Cancelled
    case Deadlock(detail: NonEmpty[String])

    def message: NonEmpty[String] = this match {
      case Unsupported(opcode, detail)       => s"unsupported instruction $opcode: $detail"
      case InvalidProgram(detail)            => s"invalid program: $detail"
      case InvalidState(detail)              => s"invalid execution state: $detail"
      case UndefinedRegister(name, pc, lane) => s"lane $lane: undefined register $name at pc $pc"
      case Memory(detail)                    => detail
      case ArchitecturalTrap(isa, code, pc)  => s"$isa trap $code at pc $pc"
      case BudgetExceeded(limit)             => s"instruction budget of $limit was exhausted"
      case Cancelled                         => "execution was cancelled"
      case Deadlock(detail)                  => s"execution deadlocked: $detail"
    }

    def toFault: Fault = this match {
      case Memory(detail)         => Fault.Memory(detail)
      case BudgetExceeded(limit)  => Fault.BudgetExceeded(limit)
      case Cancelled              => Fault.Cancelled
      case Unsupported(_, detail) => Fault.Unsupported(detail)
      case other                  => Fault.Backend(other.message)
    }
  }

  enum Step {
    case Advance
    case Jump(target: Pc)
    case Split(taken: VMLaneMask, target: Pc, fallthrough: VMLaneMask)
    case Suspend(key: WaitKey)
    case Exit(mask: VMLaneMask)
    case Trap(fault: ExecutionFault)
  }
}
