package net.kurobako.gemstone.vm

trait Observer[-E] {
  def observe(context: ObservationContext, event: E): Either[Observer.Error, Unit]
}

object Observer {
  case class Error(message: String)

  def discard[E]: Observer[E] = (_, _) => Right(())
}
