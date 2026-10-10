package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

object InvocationView {
  enum Value {

    /** A scalar, vector or composite value's lanes, each in its type's bit width. */
    case Bits(lanes: Vec[Long])
    case Address(address: Long)
  }
}

/** One SPIR-V invocation as an observer sees it before its next instruction. */
trait InvocationView extends ArchitecturalState {

  /** The result id of the function executing. */
  def function: Int

  /** The value a result id holds in this invocation, if it holds one. */
  def value(id: Int): Option[InvocationView.Value]
}
