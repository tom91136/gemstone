package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.BlockAnalysis
import net.kurobako.gemstone.profile.HardwareLoops
import net.kurobako.gemstone.profile.PipelineSimulator
import net.kurobako.gemstone.semantics.render.Bitwuzla
import net.kurobako.gemstone.testing.*

class RewriteSearchSuite extends Suite {
  private val target = WindowFixtures.calibrated
  private val kernel = "window"

  private val oneWave = AMDGPUBlockCost.Launch.Stated(32, 65536L, Some(PipelineSimulator.Placement(Vec(1, 0, 0, 0))))

  private val search   = RewriteSearch.Config(seed = 1L, budget = 3, temperature = 1.0, states = 2, limit = 8)
  private val sentence = RewriteSearch.Config(seed = 7L, budget = 6, temperature = 1.0, states = 4, limit = 4)

  private def installed(): Unit =
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")

  private def windows(image: Vec[Byte], size: Int, function: String = kernel): Vec[Window] =
    AMDGPUWindows.of(image, function, target, size).fold(fail(_), identity)

  private def assembly(image: Vec[Byte], function: String = kernel): Vec[String] =
    AMDGPUModel.staticCode(image, function).fold(fail(_), _._3)

  private def searchOf(
      image: Vec[Byte],
      window: Window,
      config: RewriteSearch.Config = search,
      function: String = kernel
  ) =
    RewriteSearch.amdgpu(image, function, target, window, oneWave, config) match {
      case Right(report) => report
      case Left(reason)  => fail(s"the window ${window.first}..${window.last} is not searchable: $reason")
    }

  private def holding(function: String, size: Int, mnemonic: String): Window = {
    val text = assembly(WindowFixtures.compiled, function)
    val at   = text.indexWhere(_.startsWith(mnemonic))
    windows(WindowFixtures.compiled, size, function).find(window => window.first <= at && at <= window.last)
      .getOrElse(fail(s"no window of $function holds $mnemonic: $text"))
  }

  private def validated(row: String): PipelineSimulator.Block =
    HardwareLoops.stream(HardwareLoops.gfx1036(row), warps = 1, shared = 65536L, branchBytes = 4L)
      .flatMap(BlockAnalysis.of(_, Architecture.gfx1036, oneWave.placement))
      .fold(
        fail(_),
        _.regions.find(_.kind == BlockAnalysis.Kind.Loop).map(_.status) match {
          case Some(BlockAnalysis.Status.Executed(timing)) => timing.block
          case other                                       => fail(s"no executed loop: $other")
        }
      )

  test("the unmodified loop of a validated row costs what the static view gives that row") {
    val report = searchOf(WindowFixtures.compiled, holding("ctl_f4_tail", 8, "v_fma_f32"), function = "ctl_f4_tail")
    val row    = validated("ctl_f4_tail")
    assertEquals(report.before.cycles, row.cyclesPerIteration)
    assertEquals(report.before.source, row.source)
  }

  test("a dead instruction in a validated loop is deleted as a proved rewrite at the cost of the loop without it") {
    installed()
    val window  = holding("ctl_f4_dead", 2, "v_mov_b32")
    val text    = assembly(WindowFixtures.compiled, "ctl_f4_dead")
    val report  = searchOf(WindowFixtures.compiled, window, RewriteSearch.Config.default, "ctl_f4_dead")
    val kept    = Vec.from(window.first to window.last).map(text(_)).filterNot(_.startsWith("v_mov_b32"))
    val rewrite = report.rewrites.find(_.rewrite == kept).getOrElse(fail(s"no rewrite leaves $kept: $report"))
    assertEquals(rewrite.after.cycles, validated("ctl_f4_tail").cyclesPerIteration)
    assert(rewrite.after.cycles < rewrite.before.cycles, rewrite)
    assertEquals(rewrite.source, Architecture.Source.weakest(rewrite.before.source, rewrite.after.source))
    assert(rewrite.line.contains(s"(${rewrite.source.kind})"), rewrite.line)
    assert(rewrite.line.contains("shifts -4 bytes"), rewrite.line)
    println(s"[rewrite] ctl_f4_dead ${rewrite.line}")
  }

  test("a target without a calibrated model is reported, not estimated") {
    val window = AMDGPUWindows.of(WindowFixtures.loop, WindowFixtures.kernel, "gfx906", 8).fold(fail(_), _.head)
    assertEquals(
      RewriteSearch.amdgpu(WindowFixtures.loop, WindowFixtures.kernel, "gfx906", window, oneWave).left.map(identity),
      Left("no calibrated pipeline model for gfx906")
    )
  }

  test("a loop that reaches memory is reported as not costed") {
    val window = holding("square_plus_one", 8, "v_fma_f32")
    val found  = RewriteSearch.amdgpu(WindowFixtures.compiled, "square_plus_one", target, window, oneWave)
    assert(found.left.exists(_.startsWith("no recorded memory traffic for ")), found)
  }

  test("a dead instruction in a loop that loads and stores is deleted as a proved rewrite under a recorded launch") {
    installed()
    val image    = WindowFixtures.stream
    val function = WindowFixtures.streaming.raw
    val text     = assembly(image, function)
    val at       = text.indexWhere(_.startsWith("v_mov_b32 v7"))
    val window   = windows(image, 2, function).find(window => window.first <= at && at <= window.last)
      .getOrElse(fail(s"no window holds the dead move: $text"))
    val stated = RewriteSearch.amdgpu(image, function, target, window, oneWave)
    assert(stated.left.exists(_.startsWith("no recorded memory traffic for ")), stated)
    def recorded(of: Vec[Byte]) =
      AMDGPUBlockCost.record(of, function, 32, 1, Vec(4096L, 4096L, 5L)).fold(fail(_), identity)
    val launch   = AMDGPUBlockCost.Launch.Recorded(recorded(image), oneWave.placement)
    val report   = RewriteSearch.amdgpu(image, function, target, window, launch).fold(fail(_), identity)
    val kept     = Vec.from(window.first to window.last).map(text(_)).filterNot(_.startsWith("v_mov_b32"))
    val rewrite  = report.rewrites.find(_.rewrite == kept).getOrElse(fail(s"no rewrite leaves $kept: $report"))
    val relaunch = BlockAnalysis.of(recorded(WindowFixtures.streamOf("")), Architecture.gfx1036, oneWave.placement)
      .fold(fail(_), _.regions.filter(_.kind == BlockAnalysis.Kind.Loop).map(_.status))
    relaunch match {
      case Vec(BlockAnalysis.Status.Executed(timing)) =>
        assertEquals(rewrite.after.cycles, timing.block.cyclesPerIteration)
      case other => fail(s"the loop without the move is not executed: $other")
    }
    assert(rewrite.after.cycles < rewrite.before.cycles, rewrite)
    assertEquals(rewrite.source, Architecture.Source.weakest(rewrite.before.source, rewrite.after.source))
    assert(rewrite.line.contains(s"(${rewrite.source.kind})"), rewrite.line)
    assert(report.uncosted.isEmpty, report.uncosted)
    println(s"[rewrite] stream ${rewrite.line}")
  }

  test("candidates the cost refuses are listed under the reason") {
    val report = RewriteSearch.Report(
      Window(0, 1, 2, Set.empty, Set("v1")),
      seed = 1L,
      budget = 3,
      evaluated = 2,
      candidates = 0,
      wrong = 0,
      before = RewriteSearch.Measure(1.0, Architecture.Source.Estimate("a test")),
      rewrites = Vec.empty,
      uncosted = Vec(("s_nop 0", "no model"), ("s_nop 1", "no model"))
    )
    assertEquals(
      RewriteSearch.lines("fixture", report.window, Right(report)),
      Vec(
        "[rewrite] fixture 1..2 live-out=v1 candidates=0 wrong=0 rewrites=0",
        "[rewrite]   not costed candidates=2: no model"
      )
    )
  }

  private val dead: Vec[Byte] = WindowFixtures.assembled(
    """s_mov_b32 s0, 0
      |v_add_nc_u32 v2, v1, v1
      |v_add_nc_u32 v3, v1, v4
      |v_mov_b32 v5, v3
      |s_endpgm
      |""".stripMargin
  )

  private val overwritten: Vec[Byte] = WindowFixtures.assembled(
    """v_add_nc_u32 v2, v1, v4
      |v_mov_b32 v5, v2
      |s_endpgm
      |""".stripMargin
  )

  private lazy val deadWindow: Window =
    windows(dead, 3).find(_.first == 0).getOrElse(fail("no window at 0"))

  private lazy val overwrittenWindow: Window =
    windows(overwritten, 1).find(window => window.first == 0 && window.liveOut.contains("v2"))
      .getOrElse(fail("no window at 0 with v2 live out"))

  private val unobserved: Vec[Byte] = WindowFixtures.assembled(
    """v_add_nc_u32 v2, v1, v1
      |v_add_nc_u32 v3, v1, v4
      |s_endpgm
      |""".stripMargin
  )

  private lazy val unobservedWindow: Window =
    windows(unobserved, 2).find(window => window.first == 0 && window.last == 1)
      .getOrElse(fail("no window 0..1"))

  test("a fixed seed and budget prove a rewrite of a dead result with fewer model cycles") {
    installed()
    val text   = assembly(dead)
    val report = searchOf(dead, deadWindow)
    assert(report.rewrites.nonEmpty, report)
    report.rewrites.foreach { rewrite =>
      assert(rewrite.after.cycles < rewrite.before.cycles, rewrite)
      assert(rewrite.evidence.instructions.nonEmpty, rewrite)
    }
    val removed = Vec(text(0), text(2))
    assert(report.rewrites.exists(_.rewrite == removed), s"no rewrite deletes ${text(1)}: ${report.rewrites}")
  }

  test("a window that leaves no write live out is deleted as a proven rewrite") {
    installed()
    val report   = searchOf(unobserved, unobservedWindow)
    val deletion = report.rewrites.find(_.rewrite.isEmpty).getOrElse(fail(s"no whole-window deletion: $report"))
    assertEquals(deletion.reason, "no write is live out", deletion)
    assert(deletion.after.cycles < deletion.before.cycles, deletion)
    assert(deletion.evidence.instructions.nonEmpty, deletion)
  }

  test("a cheaper candidate that changes a live-out value is never reported") {
    installed()
    val report = searchOf(overwritten, overwrittenWindow)
    assertEquals(report.rewrites.size, 0, report)
    assert(report.wrong > 0, report)
  }

  test("proof rejects a candidate that changes a live-out value") {
    installed()
    val candidate = Vec("v_sub_nc_u32 v2, v1, v4", "v_mov_b32 v5, v2")
    RewriteSearch.prove(overwritten, kernel, target, overwrittenWindow, candidate) match {
      case Right(Proof.Result.Rejected(_, _)) => ()
      case other                              => fail(s"expected a refutation, got $other")
    }
  }

  test("the same seed and budget give the same report") {
    installed()
    def rows(report: RewriteSearch.Report): Vec[(Vec[String], Vec[String], Double, String)] =
      report.rewrites.map(rewrite => (rewrite.original, rewrite.rewrite, rewrite.after.cycles, rewrite.source.kind))
    val first  = searchOf(dead, deadWindow, sentence)
    val second = searchOf(dead, deadWindow, sentence)
    assertEquals(first.evaluated, second.evaluated)
    assertEquals(first.candidates, second.candidates)
    assertEquals(first.wrong, second.wrong)
    assertEquals(rows(first), rows(second))
  }

  private def fixture(
      name: String,
      image: Vec[Byte],
      function: String,
      size: Int,
      config: RewriteSearch.Config = RewriteSearch.Config.default
  ): Unit =
    test(s"the $name fixture reports only proved rewrites with fewer model cycles") {
      installed()
      windows(image, size, function).foreach { window =>
        val result = RewriteSearch.amdgpu(image, function, target, window, oneWave, config)
        result.foreach(_.rewrites.foreach { rewrite =>
          assert(rewrite.after.cycles < rewrite.before.cycles, rewrite)
          assert(rewrite.evidence.instructions.nonEmpty, rewrite)
        })
        RewriteSearch.lines(name, window, result).foreach(println)
      }
    }

  fixture("ctl_f4_tail", WindowFixtures.compiled, "ctl_f4_tail", 8)
  fixture("ctl_f4_dead", WindowFixtures.compiled, "ctl_f4_dead", 8)
  fixture("square_plus_one", WindowFixtures.compiled, "square_plus_one", 8, search)
}
