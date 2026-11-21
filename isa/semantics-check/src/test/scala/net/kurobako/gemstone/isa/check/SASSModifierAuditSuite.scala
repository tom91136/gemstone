package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

class SASSModifierAuditSuite extends Suite {
  override def baseTimeout: FiniteDuration = 300.seconds

  private lazy val rows: Vec[Vec[String]] = SASSModifierAudit.rows(Set("sm_89"))

  private def states(opcode: String, kinds: String): Vec[String] =
    rows.filter(row => row(1) == opcode && row(2) == kinds).map(row => row(3))

  test("a modifier combination is a form of its own") {
    val shapes = rows.map(row => (row(1), row(2)))
    assertEquals(shapes.distinct, shapes)
    assert(shapes.contains(("SHF.L.U32", "R R I R")) && shapes.contains(("SHF.L.S32", "R R I R")), shapes.take(20))
  }

  test("a form is covered only where a case has its opcode and kinds") {
    assertEquals(states("LOP3.LUT", "P R R c R I P"), Vec("covered"))
    assertEquals(states("LOP3.LUT", "R R I R I P"), Vec("covered"))
    assertEquals(states("LEA.X", "R R c I P"), Vec("case"))
  }

  test("a fixture form with no case of its own is reported with whether the lane admits it") {
    val outcomes = rows.map(_(3)).filterNot(Set("covered", "case")).map(_.takeWhile(_ != ':')).distinct
    assert(outcomes.forall(Set("refused", "rides")), outcomes)
  }
}
