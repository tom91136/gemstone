package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*

private[isa] object VMConditionCodes {
  enum Flag {
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

  private var defined                               = 0
  private var values                                = 0
  private var threeInputCarry: Option[Int in 0 ~ 2] = None

  def read(flag: Flag): Option[Boolean] = {
    val bit = 1 << flag.ordinal
    if ((defined & bit) == 0) None else Some((values & bit) != 0)
  }

  def write(flag: Flag, value: Option[Boolean]): Unit = {
    val bit = 1 << flag.ordinal
    value match {
      case None        => defined &= ~bit
      case Some(state) =>
        if (flag == Flag.Carry) threeInputCarry = None
        defined |= bit
        if (state) values |= bit else values &= ~bit
    }
  }

  def readThreeInputCarry: Option[Int in 0 ~ 2] = threeInputCarry

  def writeThreeInputCarry(value: Option[Int in 0 ~ 2]): Unit = {
    if (value.nonEmpty) defined &= ~(1 << Flag.Carry.ordinal)
    threeInputCarry = value
  }

  def clear(): Unit = {
    defined = 0
    values = 0
    threeInputCarry = None
  }
}
