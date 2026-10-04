package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

object VMFault {
  def fromMemory(error: MemoryError, context: String = ""): VMFault =
    error match {
      case MemoryError.Cancelled => Cancelled
      case other                 => Memory(context + other.message)
    }

  def fromControl(failure: Fault): VMFault = failure match {
    case Fault.BudgetExceeded(limit) => BudgetExceeded(limit)
    case Fault.Cancelled             => Cancelled
    case other                       => InvalidState(other.message)
  }
}

enum VMFault {
  case Unsupported(opcode: NonEmpty[String], detail: NonEmpty[String])
  case InvalidProgram(detail: NonEmpty[String])
  case InvalidState(detail: NonEmpty[String])
  case UndefinedRegister(name: NonEmpty[String], pc: Pc, lane: WorkgroupLane)
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

  def inFunction(name: NonEmpty[String]): VMFault = this match {
    case Memory(detail)       => Memory(s"$name: $detail")
    case InvalidState(detail) => InvalidState(s"$name: $detail")
    case other                => other
  }

  def toFault: Fault = this match {
    case Memory(detail)         => Fault.Memory(detail)
    case BudgetExceeded(limit)  => Fault.BudgetExceeded(limit)
    case Cancelled              => Fault.Cancelled
    case Unsupported(_, detail) => Fault.Unsupported(detail)
    case other                  => Fault.Backend(other.message)
  }
}
