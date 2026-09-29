package net.kurobako.gemstone.vm

import net.kurobako.gemstone.constraint.*

enum Fault(val message: NonEmpty[String]) {
  case InvalidTask(detail: NonEmpty[String])   extends Fault(detail)
  case Memory(detail: NonEmpty[String])        extends Fault(detail)
  case Unsupported(detail: NonEmpty[String])   extends Fault(detail)
  case EventHandling(detail: NonEmpty[String]) extends Fault(detail)
  case Backend(detail: NonEmpty[String])       extends Fault(detail)
  case BudgetExceeded(limit: Long > 0)
      extends Fault(s"instruction budget of $limit was exhausted")
  case Cancelled extends Fault("execution was cancelled")
}
