package net.kurobako.gemstone.isa.nvidia

object ConditionCodeReads {
  extension (codes: VMConditionCodes) {
    def read(flag: VMConditionCodes.Flag): Option[Boolean] = codes.state(flag) match {
      case -1    => None
      case state => Some(state != 0)
    }
    def readThreeInputCarry: Option[Int] = Option.when(codes.threeInputCarryCount >= 0)(codes.threeInputCarryCount)
  }
}
