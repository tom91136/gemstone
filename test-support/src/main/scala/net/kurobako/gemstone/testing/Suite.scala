package net.kurobako.gemstone.testing

import net.kurobako.gemstone.toolchain.workarounds
import scala.concurrent.duration.*

abstract class Suite extends munit.FunSuite {
  workarounds.install()

  def baseTimeout: FiniteDuration = 30.seconds

  final override def munitTimeout: Duration = timeouts.scaled(baseTimeout)
}
