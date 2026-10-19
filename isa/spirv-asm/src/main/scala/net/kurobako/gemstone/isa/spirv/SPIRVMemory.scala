package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*

/** The memory one invocation reaches through pointers, as rendered semantics access it: values of prelude types,
  * little-endian at flat byte addresses.
  */
trait SPIRVMemory {
  def load[T](address: UInt[64])(using Type.Reified[T]): T
  def store[T](address: UInt[64], value: T)(using Type.Reified[T]): Unit

  /** The value at `address` as an atomic read-modify-write reads it; the next [[atomicStore]] completes the access. An
    * atomic faults unless `address` is a multiple of the value's size.
    */
  def atomicLoad[T](address: UInt[64])(using Type.Reified[T]): T
  def atomicStore[T](address: UInt[64], value: T)(using Type.Reified[T]): Unit
}
