package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.semantics.prelude.*

/** The call stack of the lanes, with the parameter slots a call passes and receives, the device builtins they call and
  * the targets of a branch table.
  */
private[isa] trait Frames {
  def call(function: UInt[64], returns: Seq[Int], parameters: Seq[Int]): Unit
  def builtin(builtin: Builtin, returns: Seq[Int], parameters: Seq[Int]): Unit
  def returnControl(): Unit
  def branchTable(index: UInt[64], targets: Seq[Int]): Unit

  /** The frame-local address of the lane's stack pointer. */
  def stackPointer(): UInt[64]

  /** `size` bytes aligned to `alignment` allocated on the lane's stack, whose frame-local address is returned. */
  def allocate(size: UInt[64], alignment: UInt[64]): UInt[64]

  /** The lane's stack pointer set to the frame-local address `pointer`. */
  def restoreStack(pointer: UInt[64]): Unit

  /** Pushes the return address `address` on the lane's return stack. */
  def pushReturn(address: UInt[64]): Unit
}
