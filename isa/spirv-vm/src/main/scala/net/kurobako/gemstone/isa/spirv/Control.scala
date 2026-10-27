package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.prelude.*

/** The block structure and call stack of an invocation, which rendered definitions request control from. */
private[isa] trait Control {
  def predecessor(): UInt[64]
  def enter(label: Int): Unit
  def switch(selector: UInt[64], cases: Seq[(Long, Int)], default: Int): Unit
  def call(function: Int, arguments: Any*): Unit
  def called(function: Int, arguments: Any*): UInt[64]
  def callIndirect(pointer: Any, arguments: Any*): Unit
  def returnControl(): Unit
  def returnValue(value: Any): Unit
  def kill(): Unit
  def terminate(): Unit
  def abort(message: Any): Unit
}
