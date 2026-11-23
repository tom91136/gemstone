package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.semantics.render.Bitwuzla
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

class RewriteSearchSASSSuite extends Suite {
  override def baseTimeout: FiniteDuration = 200.seconds

  private val image  = SASSFixtures.arithmetic
  private val window = SASSFixtures.first(image)

  private lazy val report: RewriteSearch.Report = {
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")
    RewriteSearch.sass(image, SASSFixtures.kernel, SASSFixtures.target, window, SASSFixtures.launch)
      .fold(fail(_), identity)
  }

  test("the dead instruction is withheld when its gain is inside the measured uncertainty of the mean") {
    assert(report.before.noise > 0.0, report)
    assert(report.rewrites.isEmpty, report)
  }

  test("every reported rewrite is cheaper than the window") {
    report.rewrites.foreach(rewrite => assert(rewrite.after.cycles < rewrite.before.cycles, rewrite))
  }

  test("a window holding a store is not costed under a stated launch") {
    val loaded = SASSFixtures.cubin("intchain.sm_89.cubin")
    val first  = SASSWindows.of(loaded, SASSFixtures.kernel, SASSFixtures.target).toOption.get.head
    val search = RewriteSearch.sass(loaded, SASSFixtures.kernel, SASSFixtures.target, first, SASSFixtures.launch)
    assert(search.left.exists(_.contains("reaches memory")), search)
  }

  test("a window in a loop that loads is costed from a recorded launch") {
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")
    val small  = RewriteSearch.Config.default.copy(budget = 8)
    val loops  = SASSFixtures.cubin(SASSFixtures.loops)
    val stream = LaunchRecording.record(
      Language.SASS,
      loops,
      SASSFixtures.sum,
      32,
      1,
      LaunchFixtures.arguments("buffer:4096,buffer:4096,i32:7")
    )
    val windows = SASSWindows.of(loops, SASSFixtures.sum, SASSFixtures.target).fold(fail(_), identity)
    val inside  = windows.find(window => window.first >= 108 && window.last <= 116).getOrElse(fail("no tail window"))
    val stated  = RewriteSearch.sass(loops, SASSFixtures.sum, SASSFixtures.target, inside, SASSFixtures.launch, small)
    assert(stated.left.exists(_.contains("reaches memory")), stated)
    val launch = AMDGPUBlockCost.Launch.Recorded(stream.fold(fail(_), identity), None)
    val found  = RewriteSearch.sass(loops, SASSFixtures.sum, SASSFixtures.target, inside, launch, small)
    assert(found.isRight, found)
  }
}
