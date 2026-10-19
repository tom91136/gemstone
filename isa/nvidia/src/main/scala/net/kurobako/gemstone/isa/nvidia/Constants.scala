package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*

/** The constant banks a kernel reads: driver-provided values, launch parameters and the program's constants. */
private[isa] trait Constants {
  def load[T](bank: UInt[32], offset: UInt[32])(using Type.Reified[T]): T
}
