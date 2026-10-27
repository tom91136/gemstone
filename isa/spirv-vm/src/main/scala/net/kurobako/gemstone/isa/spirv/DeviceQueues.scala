package net.kurobako.gemstone.isa.spirv

/** The device-side enqueue operations rendered definitions request of the machine. Enqueues and kernel queries return
  * their status or count; event operations return nothing. Ranges and arrays arrive as their lanes, in order.
  */
private[isa] trait DeviceQueues {
  def defaultQueue[T](): T
  def buildNDRange[T](lanes: Any*): T
  def enqueueKernel[T](lanes: Any*): T
  def enqueueMarker[T](queue: Any, count: Any, waits: Any, returned: Any): T
  def retainEvent(event: Any): Unit
  def releaseEvent(event: Any): Unit
  def createUserEvent[T](): T
  def isValidEvent[T](event: Any): T
  def setUserEventStatus(event: Any, status: Any): Unit
  def captureEventProfilingInfo(event: Any, info: Any, value: Any): Unit
  def kernelWorkGroupSize[T](invoke: Any, param: Any, size: Any, alignment: Any): T
  def kernelPreferredWorkGroupSizeMultiple[T](invoke: Any, param: Any, size: Any, alignment: Any): T
  def kernelMaxNumSubgroups[T](invoke: Any, param: Any, size: Any, alignment: Any): T
  def kernelLocalSizeForSubgroupCount[T](subgroups: Any, invoke: Any, param: Any, size: Any, alignment: Any): T
  def kernelNDrangeSubGroupCount[T](lanes: Any*): T
  def kernelNDrangeMaxSubGroupSize[T](lanes: Any*): T
}
