package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.sass.*
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

  test("a PC-relative instruction leaves the windows that do not shift it searched") {
    val source =
      """IADD3 R2, R2, 0x1, RZ ;
        |IADD3 R4, R2, 0x2, RZ ;
        |BRA 0x40 ;
        |IADD3 R3, R2, 0x3, RZ ;
        |IADD3 R0, R2, 0x4, RZ ;
        |IADD3 R1, R2, 0x5, RZ ;
        |EXIT ;
        |""".stripMargin
    val selected = net.kurobako.gemstone.isa.sass.Target.parse(SASSFixtures.target).fold(fail(_), identity)
    val parsed   = net.kurobako.gemstone.isa.sass.asm.Parser.parseProgram(source)
      .fold(error => fail(error.toString), identity)
    val code = new net.kurobako.gemstone.isa.sass.asm.Assembler(selected).encode(parsed)
      .fold(error => fail(error.toString), identity)
    val image = Cubin.replaceKernel(
      SASSFixtures.cubin("intchain.sm_89.cubin"),
      SASSFixtures.kernel.unsafe[NonEmpty[String]],
      code
    ).fold(error => fail(error.raw), identity)
    val text = SASSModel.staticCode(image, SASSFixtures.kernel).fold(
      fail(_),
      _._3.map(instruction =>
        net.kurobako.gemstone.isa.sass.asm.Printer.print(instruction.operation).getOrElse(instruction.mnemonic.raw)
      )
    )
    val at = text.indexWhere(_.startsWith("BRA"))
    assert(at >= 0, text)
    val all    = SASSWindows.of(image, SASSFixtures.kernel, SASSFixtures.target, 2).fold(fail(_), identity)
    val before = all.filter(_.last < at)
    val after  = all.filter(_.first > at)
    assert(before.nonEmpty && after.nonEmpty, all)
    val small = RewriteSearch.Config(seed = 1L, budget = 2, temperature = 1.0, states = 2, limit = 8)
    val found = after.map(window =>
      RewriteSearch.sass(image, SASSFixtures.kernel, SASSFixtures.target, window, SASSFixtures.launch, small)
    )
    assert(found.forall(_.isRight), found)
    assert(found.exists(_.toOption.exists(_.candidates > 0)), found)
  }
}
