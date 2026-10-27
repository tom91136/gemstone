package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

object InvocationView {
  enum Value derives CanEqual {

    /** A scalar, vector or composite value's lanes, each in its type's bit width. */
    case Bits(lanes: Vec[Long])
    case Address(location: Long)
  }
}

/** One SPIR-V invocation as an observer sees it before its next instruction. */
trait InvocationView extends ArchitecturalState {

  /** The result id of the function executing. */
  def function: Int

  /** The instruction index this invocation is about to issue. */
  def pc: Int = -1

  /** The label the invocation is in, or -1 before the first OpLabel. */
  def label: Int = -1

  /** The label the edge into the current block came from, or -1 at entry. */
  def predecessor: Int = -1

  /** The number of call frames the invocation has pushed. */
  def callDepth: Int = 0

  /** The value a result id holds in this invocation, if it holds one. */
  def value(id: Int): Option[InvocationView.Value]
}
