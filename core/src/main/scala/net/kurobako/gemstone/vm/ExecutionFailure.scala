package net.kurobako.gemstone.vm

object ExecutionFailure {
  def fromFault(fault: Fault): ExecutionFailure = fault match {
    case Fault.InvalidTask(detail) => InvalidRequest(detail)
    case Fault.Unsupported(detail) => Unsupported(detail)
    case _                         => Failed(fault.message)
  }
}

enum ExecutionFailure {
  case InvalidRequest(detail: String)
  case NotFound(detail: String)
  case Unsupported(detail: String)
  case Failed(detail: String)

  def message: String = this match {
    case InvalidRequest(value) => value
    case NotFound(value)       => value
    case Unsupported(value)    => value
    case Failed(value)         => value
  }

  def withDetail(value: String): ExecutionFailure = this match {
    case _: InvalidRequest => InvalidRequest(value)
    case _: NotFound       => NotFound(value)
    case _: Unsupported    => Unsupported(value)
    case _: Failed         => Failed(value)
  }
}
