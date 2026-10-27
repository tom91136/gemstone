package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*

/** One register's live spans over a launch, named as the function that declares it names it. A register may be live in
  * several stretches when a loop writes it again, and each span counts the units that held it live there.
  */
case class RegisterTimeline(function: String, name: String, spans: Vec[LiveSpan]) {

  /** The first issue the register is live before. */
  def start: Long = spans.headOption.fold(0L)(_.start)
}
