package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*

/** The memory a lane accesses: generic addresses, which select a state space by aperture, and each state space's own
  * addresses. A `T` value occupies its width's bytes, little-endian.
  */
private[isa] trait Memory {

  /** The base of the lane's thread-local window, which local addresses are relative to. */
  def localBase(): UInt[64]

  /** The base of the CTA's shared::cta window, which shared variables are relative to. */
  def sharedWindow(): UInt[64]

  def load[T](address: UInt[64])(using Type.Reified[T]): T
  def loadGlobal[T](address: UInt[64])(using Type.Reified[T]): T
  def loadShared[T](address: UInt[64])(using Type.Reified[T]): T
  def loadLocal[T](address: UInt[64])(using Type.Reified[T]): T
  def store[T](address: UInt[64], value: T)(using Type.Reified[T]): Unit
  def storeGlobal[T](address: UInt[64], value: T)(using Type.Reified[T]): Unit
  def storeShared[T](address: UInt[64], value: T)(using Type.Reified[T]): Unit
  def storeLocal[T](address: UInt[64], value: T)(using Type.Reified[T]): Unit

  def reduceGlobal(
      address: UInt[64],
      data: Array[Byte],
      operator: TensorMap.Reduction,
      element: TensorMap.Element
  ): Unit

  def storeGlobalMasked(destination: UInt[64], source: UInt[64], bytes: UInt[64], mask: UInt[64]): Unit

  /** The bytes the texture or surface read `shape` of `handle` yields at `coordinates`. */
  def textureRead(shape: String, handle: UInt[64], coordinates: UInt[64]*): Array[Byte]

  /** The `index`th `bytes`-wide result of `data`. */
  def textureWord(data: Array[Byte], index: UInt[64], bytes: UInt[64]): UInt[64]

  def surfaceStore(shape: String, handle: UInt[64], arguments: UInt[64]*): Unit

  def surfaceReduce(shape: String, handle: UInt[64], arguments: UInt[64]*): Unit

  /** Makes the writes to host-mapped memory visible to the host. */
  def fence(): Unit

  /** `map` with the value `value` replacing its field `field`, at `index` for the fields that repeat. */
  def replaceTensorMap(map: Array[Byte], field: TensorMap.Field, index: Int, value: UInt[64]): Array[Byte]

  /** The bytes of a function parameter's storage from `offset`. */
  def loadParameter[T](parameter: Int, offset: UInt[64])(using Type.Reified[T]): T
  def storeParameter[T](parameter: Int, offset: UInt[64], value: T)(using Type.Reified[T]): Unit
}
