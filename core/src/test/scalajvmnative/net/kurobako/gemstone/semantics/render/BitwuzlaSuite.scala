package net.kurobako.gemstone.semantics.render

import java.nio.file.Path
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

class BitwuzlaSuite extends Suite {
  private val unparsable = "(set-logic QF_BV)\n(assert (bogus))\n(check-sat)\n"

  // 2xy as a difference of squares: unsat, and past any limit a test would spend proving it
  private val hard =
    """|(set-logic QF_BV)
       |(declare-const x (_ BitVec 64))
       |(declare-const y (_ BitVec 64))
       |(define-fun l () (_ BitVec 128)
       |  (bvshl (bvmul ((_ zero_extend 64) x) ((_ zero_extend 64) y)) (_ bv1 128)))
       |(define-fun r () (_ BitVec 128)
       |  (bvsub (bvsub (bvmul (bvadd ((_ zero_extend 64) x) ((_ zero_extend 64) y))
       |                        (bvadd ((_ zero_extend 64) x) ((_ zero_extend 64) y)))
       |                 (bvmul ((_ zero_extend 64) x) ((_ zero_extend 64) x)))
       |          (bvmul ((_ zero_extend 64) y) ((_ zero_extend 64) y))))
       |(assert (distinct l r))
       |(check-sat)""".stripMargin

  private def installed(): Path = {
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")
    Bitwuzla.executable.get
  }

  test("a query the solver rejects reports its reason instead of waiting out the limit") {
    val limit   = 5.seconds
    val started = System.nanoTime()
    val verdict = Bitwuzla.solve(installed(), unparsable, limit)
    val elapsed = FiniteDuration(System.nanoTime() - started, TimeUnit.NANOSECONDS)
    assert(elapsed < limit / 2, s"$elapsed")
    verdict match {
      case Verdict.Undecided(reason) => assert(reason.contains("query.smt2"), reason)
      case other                     => fail(s"expected the solver's reason, got $other")
    }
  }

  test("a check that reaches the solver's limit is reported as unknown, not as a missing verdict") {
    assertEquals(Bitwuzla.solve(installed(), hard, 3.seconds), Verdict.Undecided("solver returned unknown"))
  }
}
