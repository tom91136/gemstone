package net.kurobako.gemstone.tools.conformance

import java.util.concurrent.atomic.AtomicReference
import sun.misc.Signal
import sun.misc.SignalHandler

private[conformance] final class SignalRelay {
  private val driver  = AtomicReference(Option.empty[Process])
  private val pending = AtomicReference(Option.empty[Signal])

  val handler: SignalHandler = signal =>
    synchronized {
      driver.get match {
        case Some(process) =>
          val _ = ProcessBuilder("kill", "-s", signal.getName, process.pid().toString).start().waitFor()
        case None => pending.set(Some(signal))
      }
    }

  def start(builder: ProcessBuilder): Either[Signal, Process] = synchronized {
    pending.get match {
      case Some(signal) => Left(signal)
      case None         =>
        val process = builder.start()
        driver.set(Some(process))
        Right(process)
    }
  }
}
