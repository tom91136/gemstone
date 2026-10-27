package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.prelude.*

/** Memory a lane of a rendered AMDGPU definition reads and writes by address, little-endian. */
private[isa] trait Memory[A] {
  def load8(address: A): UInt[8]
  def load16(address: A): UInt[16]
  def load32(address: A): UInt[32]
  def load64(address: A): UInt[64]
  def store8(address: A, value: UInt[8]): Unit
  def store16(address: A, value: UInt[16]): Unit
  def store32(address: A, value: UInt[32]): Unit
  def store64(address: A, value: UInt[64]): Unit

  /** The `bytes`, one to eight, from `address`, as a texel of a format the resource names is read. */
  def load(address: A, bytes: UInt[32]): UInt[64]

  /** Writes the low `bytes`, one to eight, of `value` to `address`. */
  def store(address: A, bytes: UInt[32], value: UInt[64]): Unit
}
