package net.kurobako.gemstone.isa.ptx.v9_3

import Program.{Opcode, RegisterName}
import VMIR.Pc

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.vm.Fault

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[v9_3] object VMExecution {
  enum WaitScope {
    case Warp
    case Workgroup
    case Device
    case System
    case External
  }

  final case class WaitKey(scope: WaitScope, id: Long, phase: Long)

  enum ExecutionFault {
    case Unsupported(opcode: Opcode, detail: String)
    case InvalidProgram(detail: String)
    case InvalidState(detail: String)
    case UndefinedRegister(name: RegisterName, pc: Pc, lane: Int)
    case Memory(detail: String)
    case ArchitecturalTrap(isa: String :| (MinLength[1] & NoNul), code: Long, pc: Pc)
    case BudgetExceeded(limit: Long :| Positive)
    case Cancelled
    case Deadlock(detail: String)

    def message: String = this match {
      case Unsupported(opcode, detail)       => s"unsupported instruction $opcode: $detail"
      case InvalidProgram(detail)            => s"invalid program: $detail"
      case InvalidState(detail)              => s"invalid execution state: $detail"
      case UndefinedRegister(name, pc, lane) => s"lane $lane: undefined register $name at pc ${pc.value}"
      case Memory(detail)                    => detail
      case ArchitecturalTrap(isa, code, pc)  => s"$isa trap $code at pc ${pc.value}"
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

  object ExecutionFault {
    def fromControl(failure: Fault): ExecutionFault = failure match {
      case Fault.BudgetExceeded(limit) => ExecutionFault.BudgetExceeded(limit)
      case Fault.Cancelled             => ExecutionFault.Cancelled
      case other                       => ExecutionFault.InvalidState(other.message)
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
