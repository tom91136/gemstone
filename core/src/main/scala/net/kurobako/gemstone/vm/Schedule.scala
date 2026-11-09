package net.kurobako.gemstone.vm

import net.kurobako.gemstone.constraint.*

/** The issue schedule of an instruction. `Encoded` is written by the compiler: the stall cycles before the next issue
  * and whether a write barrier is set. `Estimated` is issue cycles from a scheduling model.
  */
enum Schedule derives CanEqual {
  case Encoded(stall: Int in 0 ~ 15, writeBarrier: Boolean)
  case Estimated(cycles: Int >= 0)

  def plannedCycles: Int = this match {
    case Encoded(stall, _) => stall.raw
    case Estimated(cycles) => cycles.raw
  }
}
