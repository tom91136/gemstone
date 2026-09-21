package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.vm.Fault

enum RuntimeError(val message: String) {
  case UnknownEncoding(encoding: ProgramEncoding)
      extends RuntimeError(s"unknown program encoding: ${encoding.value}")
  case Decode(detail: String)      extends RuntimeError(detail)
  case Load(detail: String)        extends RuntimeError(detail)
  case Execution(fault: Fault)     extends RuntimeError(fault.message)
  case NotFound(detail: String)    extends RuntimeError(detail)
  case Lifecycle(detail: String)   extends RuntimeError(detail)
  case Unsupported(detail: String) extends RuntimeError(detail)
}
