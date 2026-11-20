package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.fixtures.Fixture
import scala.concurrent.duration.*

class SASSMovesDivideSuite extends SASSMovesChecks {
  override def baseTimeout: FiniteDuration = 168.seconds

  private lazy val divideKernel: Harness.Kernel = Harness.load(89, Fixture.listing("divideSm89"), "divide_probe")

  protected lazy val samples: Vec[Sample] = samplesOf("divide", divideKernel, Vec(8, 4, 4), _ => true)
}
