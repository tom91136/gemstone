package net.kurobako.gemstone.isa.spirv

/** The bulk copies rendered definitions request from the machine. */
private[isa] trait Copies {
  def copy(target: Any, origin: Any): Unit
  def groupAsyncCopy[T](scope: Any, destination: Any, source: Any, count: Any, stride: Any, elementBytes: Any*): T
  def waitEvents(scope: Any, count: Any, events: Any): Unit
}
