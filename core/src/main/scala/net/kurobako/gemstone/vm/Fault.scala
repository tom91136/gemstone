package net.kurobako.gemstone.vm

import net.kurobako.gemstone.PositiveLong

enum Fault(val message: String) {
  case InvalidTask(detail: String) extends Fault(detail)
  case Memory(detail: String)      extends Fault(detail)
  case Unsupported(detail: String) extends Fault(detail)
  case Observer(detail: String)    extends Fault(detail)
  case Backend(detail: String)     extends Fault(detail)
  case BudgetExceeded(limit: PositiveLong)
      extends Fault(s"instruction budget of $limit was exhausted")
  case PendingMemory extends Fault("host memory bytes are pending")
  case Cancelled     extends Fault("execution was cancelled")
}
