package net.kurobako.gemstone.isa.nvidia

object VMConditionCodes {
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
class VMConditionCodes {
  import VMConditionCodes.*

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var defined = 0
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var values = 0
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var threeInputCarry: Option[Int] = None

  def read(flag: Flag): Option[Boolean] = {
    val bit = 1 << flag.ordinal
    if ((defined & bit) == 0) None else Some((values & bit) != 0)
  }

  def write(flag: Flag, value: Option[Boolean]): Unit = {
    val bit = 1 << flag.ordinal
    value match {
      case None        => defined &= ~bit
      case Some(value) =>
        if (flag == Flag.Carry) threeInputCarry = None
        defined |= bit
        if (value) values |= bit else values &= ~bit
    }
  }

  def readThreeInputCarry: Option[Int] = threeInputCarry

  def writeThreeInputCarry(value: Option[Int]): Unit =
    threeInputCarry = value.map { count =>
      require(count >= 0 && count <= 2, s"IADD3 carry count is out of range: $count")
      defined &= ~(1 << Flag.Carry.ordinal)
      count
    }

  def clear(): Unit = {
    defined = 0
    values = 0
    threeInputCarry = None
  }
}
