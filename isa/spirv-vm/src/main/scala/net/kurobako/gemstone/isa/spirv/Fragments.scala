package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.prelude.*

/** The cooperative vector operations and the matrix and vector memory accesses rendered definitions request of the
  * machine. A vector load returns component `lane` of its result. A product keeps a partial result for each row of its
  * matrix, after the converted elements of its input, and reads elements of the width its component type gives.
  */
private[isa] trait Fragments {
  def vectorLoad[T](lane: Int, operands: Any*): T
  def vectorStore(operands: Any*): Unit
  def outerProductAccumulate(operands: Any*): Unit
  def reduceSumAccumulate(operands: Any*): Unit
  def strideBytes(pointer: Any, width: Int): UInt[64]
  def loadElement(pointer: Any, offset: Any, bytes: Any): UInt[64]
  def storeElement(pointer: Any, offset: Any, bytes: Any, value: Any): Unit
  def indices(count: Any): Iterable[UInt[64]]
  def partials(count: Int): Unit
  def setPartial(lane: Any, value: Any): Unit
  def partial(lane: Any): UInt[64]
}
