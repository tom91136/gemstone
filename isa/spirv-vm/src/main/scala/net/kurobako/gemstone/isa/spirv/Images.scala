package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.prelude.*

/** The image operations rendered definitions request of the machine. Those that read return component `lane` of their
  * result from the image and sampler descriptors their operands name. Sampling reads the descriptors, the bound level
  * and its texels, and a sampling call that needs derivatives waits for the invocations of its quad.
  */
private[isa] trait Images {
  def sampledImage[T](lane: Int, operands: Any*): T
  def image[T](lane: Int, operands: Any*): T
  def handledSampledImage[T](lane: Int, handle: Any): T
  def queryLevels[T](lane: Int, operands: Any*): T
  def querySizeLod[T](lane: Int, operands: Any*): T
  def querySize[T](lane: Int, operands: Any*): T
  def querySamples[T](lane: Int, operands: Any*): T
  def queryOrder[T](lane: Int, operands: Any*): T
  def queryFormat[T](lane: Int, operands: Any*): T
  def fetch[T](lane: Int, operands: Any*): T
  def read[T](lane: Int, operands: Any*): T
  def texelPointer[T](lane: Int, operands: Any*): T
  def write(operands: Any*): Unit
  def vulkan(): UInt[64]
  def numeric(operands: Any*): Unit
  def dimensions(image: Any): UInt[64]
  def arrayed(image: Any): UInt[64]
  def cube(image: Any): UInt[64]
  def sampler(image: Any): Unit
  def samplerWords(image: Any, into: Int): Unit
  def samplerWord(image: Any, word: Int): UInt[64]
  def formatWord(image: Any): UInt[64]
  def levelCount(image: Any): UInt[64]
  def level(image: Any, level: Any, into: Int): Unit
  def texel(layer: Any, x: Any, row: Any, into: Int): Unit
  def swizzle(image: Any, from: Any): Unit
  def gradients(from: Int, count: Any, into: Int): UInt[64]
  def loadLevel(offset: Any, bytes: Any): UInt[64]
  def storeLevel(offset: Any, bytes: Any, value: Any): Unit
}
