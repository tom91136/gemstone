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

  private val flags: Array[Option[Boolean]] = Array.fill(Flag.values.length)(None)
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var threeInputCarry: Option[Int] = None

  def read(flag: Flag): Option[Boolean] = flags(flag.ordinal)

  def write(flag: Flag, value: Option[Boolean]): Unit = {
    if (flag == Flag.Carry && value.nonEmpty) threeInputCarry = None
    flags(flag.ordinal) = value
  }

  def readThreeInputCarry: Option[Int] = threeInputCarry

  def writeThreeInputCarry(value: Option[Int]): Unit =
    threeInputCarry = value.map { count =>
      require(count >= 0 && count <= 2, s"IADD3 carry count is out of range: $count")
      flags(Flag.Carry.ordinal) = None
      count
    }

  def clear(): Unit = {
    Flag.values.foreach(flag => write(flag, None))
    writeThreeInputCarry(None)
  }
}
