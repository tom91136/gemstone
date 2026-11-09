package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.constraint.*

object Scheduling {
  private val ControlMask = BigInt(0x1fffff)

  /** Reads the control of an instruction word laid out by `Generation`, with the Maxwell control appended after its 64
    * instruction bits.
    */
  def decode(generation: Generation, encoding: BigInt): Either[String, Option[Scheduling]] = generation match {
    case Generation.Kepler  => Right(None)
    case Generation.Maxwell => fromControl(((encoding >> 64) & ControlMask).toInt)
    case Generation.Modern  => fromControl(((encoding >> 105) & ControlMask).toInt)
  }

  private def fromControl(control: Int): Either[String, Option[Scheduling]] = for {
    writeBarrier <- scoreboard((control >> 5) & 7)
    readBarrier  <- scoreboard((control >> 8) & 7)
  } yield Some(Scheduling(
    (control & 0xf).unsafe[Int in 0 ~ 15],
    ((control >> 4) & 1) == 1,
    writeBarrier,
    readBarrier,
    ((control >> 11) & 0x3f).unsafe[Int in 0 ~ 0x3f],
    ((control >> 17) & 0xf).unsafe[Int in 0 ~ 0xf]
  ))

  private def scoreboard(value: Int): Either[String, Option[Int in 0 ~ 5]] = value match {
    case 7       => Right(None)
    case 6       => Left("reserved scoreboard 6 in SASS control")
    case barrier => Right(Some(barrier.unsafe[Int in 0 ~ 5]))
  }
}

/** Static issue schedule that the compiler encodes beside each instruction from Maxwell onwards.
  *
  * `stall` is the number of cycles a warp waits before issuing its next instruction. `writeBarrier` is the scoreboard a
  * variable-latency instruction sets, and `readBarrier` the scoreboard that releases its source operands. `waitMask`
  * has one bit per scoreboard that this instruction waits on before issuing. `reuse` selects the operand reuse caches.
  */
case class Scheduling(
    stall: Int in 0 ~ 15,
    yieldHint: Boolean,
    writeBarrier: Option[Int in 0 ~ 5],
    readBarrier: Option[Int in 0 ~ 5],
    waitMask: Int in 0 ~ 0x3f,
    reuse: Int in 0 ~ 0xf
) derives CanEqual
