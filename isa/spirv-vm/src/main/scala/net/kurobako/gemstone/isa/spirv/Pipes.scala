package net.kurobako.gemstone.isa.spirv

/** The pipe operations rendered definitions request of the machine, over pipes in the layout of `PipeLayout`. Packet
  * transfers return 0 on success and a negative status on failure; reservations return a handle whose sign bit marks it
  * valid.
  */
private[isa] trait Pipes {
  def write[T](pipe: Any, packet: Any): T
  def read[T](pipe: Any, packet: Any): T
  def reservedWrite[T](pipe: Any, reservation: Any, index: Any, packet: Any): T
  def reservedRead[T](pipe: Any, reservation: Any, index: Any, packet: Any): T
  def reserveWrite[T](pipe: Any, count: Any): T
  def reserveRead[T](pipe: Any, count: Any): T
  def groupReserveWrite[T](scope: Any, pipe: Any, count: Any): T
  def groupReserveRead[T](scope: Any, pipe: Any, count: Any): T
  def isValidReserveId[T](reservation: Any): T
  def numPackets[T](pipe: Any): T
  def maxPackets[T](pipe: Any): T
  def fromStorage[T](storage: Any): T
}
