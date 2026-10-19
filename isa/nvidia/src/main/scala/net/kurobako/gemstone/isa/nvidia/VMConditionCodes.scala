package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*

private[isa] object VMConditionCodes {
  enum Flag derives CanEqual {
    case Carry
    case Zero
    case Sign
    case Overflow
    case Equal
    case LessThan
  }
}

/** Per-lane condition state used by NVIDIA instruction families. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private[isa] class VMConditionCodes {
  import VMConditionCodes.*

  private var defined = 0
  private var values  = 0
  // the IADD3 carry count, or -1 while undefined
  private var threeInputCarry = -1

  /** 1 or 0 for a defined flag, -1 for an undefined one. */
  def state(flag: Flag): Int = {
    val bit = 1 << flag.ordinal
    if ((defined & bit) == 0) -1 else if ((values & bit) != 0) 1 else 0
  }

  def set(flag: Flag, value: Boolean): Unit = {
    val bit = 1 << flag.ordinal
    if (flag == Flag.Carry) threeInputCarry = -1
    defined |= bit
    if (value) values |= bit else values &= ~bit
  }

  def write(flag: Flag, value: Option[Boolean]): Unit = {
    val bit = 1 << flag.ordinal
    value match {
      case None        => defined &= ~bit
      case Some(state) => set(flag, state)
    }
  }

  def writeThreeInputCarry(value: Option[Int in 0 ~ 2]): Unit = value match {
    case Some(count) => setThreeInputCarry(count)
    case None        => threeInputCarry = -1
  }

  /** The IADD3 carry count, or -1 while undefined. */
  def threeInputCarryCount: Int = threeInputCarry

  def setThreeInputCarry(count: Int): Unit = {
    defined &= ~(1 << Flag.Carry.ordinal)
    threeInputCarry = count
  }

  def invalidateThreeInputCarry(): Unit = threeInputCarry = -1

  def clear(): Unit = {
    defined = 0
    values = 0
    threeInputCarry = -1
  }
}
