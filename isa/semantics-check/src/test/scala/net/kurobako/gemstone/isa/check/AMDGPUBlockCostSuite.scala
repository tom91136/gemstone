package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.BlockAnalysis
import net.kurobako.gemstone.profile.HardwareLoops
import net.kurobako.gemstone.profile.PipelineSimulator
import net.kurobako.gemstone.profile.ProfileStream
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.LaunchGeometry

class AMDGPUBlockCostSuite extends Suite {
  private val target   = WindowFixtures.calibrated
  private val function = "ctl_f4_tail"
  private val oneWave  = AMDGPUBlockCost.Launch.Stated(32, 65536L, Some(PipelineSimulator.Placement(Vec(1, 0, 0, 0))))

  private lazy val window: Window =
    AMDGPUWindows.of(WindowFixtures.compiled, function, target, 8).fold(fail(_), _.last)

  private lazy val text: Vec[String] = AMDGPUModel.staticCode(WindowFixtures.compiled, function)
    .fold(fail(_), _._3.slice(window.first, window.last + 1))

  private def costOf(launch: AMDGPUBlockCost.Launch, replacement: Vec[String]): AMDGPUBlockCost.Cost =
    AMDGPUBlockCost.of(WindowFixtures.compiled, function, target, launch).flatMap(_.cost(window, replacement))
      .fold(fail(_), identity)

  test("a rewrite that grows a loop past its fetch line is costed at the addresses the new layout gives") {
    val rows  = HardwareLoops.gfx1036(function)
    val added = Vec(7, 8, 9).map(n =>
      rows.head.copy(text = s"v_fma_f32 v$n, v$n, s5, s6", reads = Vec(s"v$n", "s5", "s6"), writes = Vec(s"v$n"))
    )
    val top  = rows.head.address.getOrElse(fail("the row has no address"))
    val laid = added.zipWithIndex.map((row, index) => row.copy(address = Some(top + 8L * index))) ++
      rows.map(row => row.copy(address = row.address.map(_ + 24L)))
    val expected = HardwareLoops.stream(laid, warps = 1, shared = 65536L, branchBytes = 4L)
      .flatMap(BlockAnalysis.of(_, Architecture.gfx1036, oneWave.placement))
      .fold(
        fail(_),
        _.regions.find(_.kind == BlockAnalysis.Kind.Loop).map(_.status) match {
          case Some(BlockAnalysis.Status.Executed(timing)) => timing.block.cyclesPerIteration
          case other                                       => fail(s"no executed loop: $other")
        }
      )
    val grown = costOf(oneWave, added.map(_.text) ++ text)
    assertEquals(grown.shift, 24)
    assertEquals(grown.cycles, expected)
    assert(grown.cycles - costOf(oneWave, text).cycles > 3.0, grown)
  }

  test("a launch of the loop is analysed at the cycles its static view gives") {
    val image    = ProgramListing.Image(Language.AMDGPU, WindowFixtures.compiled, None)
    val wave     = LaunchGeometry.Dimensions(32, 1, 1)
    val recorded = for {
      loaded   <- ProgramRun.load(image)
      run      <- ProgramRun.run(loaded, "ctl_f4_tail", wave, LaunchGeometry.Dimensions.One)
      analysis <- BlockAnalysis.of(run.stream, Architecture.gfx1036, oneWave.placement)
    } yield analysis.regions.filter(_.kind == BlockAnalysis.Kind.Loop).map(_.status)
    val static = costOf(AMDGPUBlockCost.Launch.Stated(32, 0L, oneWave.placement), text).cycles
    recorded match {
      case Right(Vec(BlockAnalysis.Status.Executed(timing))) => assertEquals(timing.block.cyclesPerIteration, static)
      case other                                             => fail(s"no executed loop: $other")
    }
  }

  private val trips = 5L

  private def launched(image: Vec[Byte], kernel: String, arguments: Vec[Long] = Vec.empty): ProfileStream =
    AMDGPUBlockCost.record(image, kernel, 32, 1, arguments).fold(fail(_), identity)

  private def recorded(stream: ProfileStream): AMDGPUBlockCost.Launch =
    AMDGPUBlockCost.Launch.Recorded(stream, oneWave.placement)

  private def looped(stream: ProfileStream): (BlockAnalysis.Region, BlockAnalysis.Status) =
    BlockAnalysis.of(stream, Architecture.gfx1036, oneWave.placement).map(_.regions.filter(_.kind ==
      BlockAnalysis.Kind.Loop))
      .fold(
        fail(_),
        {
          case Vec(region) => region -> region.status
          case other       => fail(s"not one loop: $other")
        }
      )

  private def spanning(image: Vec[Byte], kernel: String, from: String, to: String): (Window, Vec[String]) = {
    val (graph, _, code) = AMDGPUModel.staticCode(image, kernel).fold(fail(_), identity)
    val first            = code.indexWhere(_.startsWith(from))
    val last             = code.indexWhere(_.startsWith(to))
    val block            = graph.blocks.indexWhere(block => block.first <= first && last <= block.last)
    assert(first >= 0 && last >= first && block >= 0, code)
    Window(block, first, last, Set.empty, Set.empty) -> code.slice(first, last + 1)
  }

  test("a recorded launch costs an unmodified loop that loads and stores as the dynamic view of the launch does") {
    val image            = WindowFixtures.stream
    val kernel           = WindowFixtures.streaming.raw
    val stream           = launched(image, kernel, Vec(4096L, 4096L, trips))
    val (window, code)   = spanning(image, kernel, "v_fma_f32", "v_mov_b32 v7")
    val (region, status) = looped(stream)
    assertEquals(region.members.map(_.issues).distinct, Vec(trips))
    val stated = AMDGPUBlockCost.of(image, kernel, target, oneWave).flatMap(_.cost(window, code))
    assert(stated.left.exists(_.startsWith("no recorded memory traffic for s_load_dword")), stated)
    val cost = AMDGPUBlockCost.of(image, kernel, target, recorded(stream)).flatMap(_.cost(window, code))
    status match {
      case BlockAnalysis.Status.Executed(timing) =>
        assertEquals(cost, Right(AMDGPUBlockCost.Cost(timing.block.cyclesPerIteration, timing.block.source, 0)))
      case other => fail(s"the loop is not executed: $other")
    }
  }

  test("a launch of a kernel with hidden arguments takes a value for each explicit parameter") {
    assert(AMDGPUBlockCost.record(WindowFixtures.hidden, "hidden_args", 32, 1, Vec(4096L, 4096L, 5L)).isRight)
  }

  test("a rewrite of a recorded loop costs what a launch of the rewritten kernel records") {
    val image          = WindowFixtures.stream
    val kernel         = WindowFixtures.streaming.raw
    val arguments      = Vec(4096L, 4096L, trips)
    val (window, code) = spanning(image, kernel, "v_fma_f32", "v_mov_b32 v7")
    val cost           =
      AMDGPUBlockCost.of(image, kernel, target, recorded(launched(image, kernel, arguments))).fold(fail(_), identity)
    Vec("" -> -4, "v_mov_b32 v7, v6\n" -> 0, "v_mov_b32 v7, v5\nv_mov_b32 v7, v6\n" -> 4).foreach { (tail, shift) =>
      val replacement = code.init ++ Vec.from(tail.linesIterator)
      looped(launched(WindowFixtures.streamOf(tail), kernel, arguments))._2 match {
        case BlockAnalysis.Status.Executed(timing) =>
          assertEquals(
            cost.cost(window, replacement),
            Right(AMDGPUBlockCost.Cost(timing.block.cyclesPerIteration, timing.block.source, shift)),
            replacement
          )
        case other => fail(s"the loop ending $tail is not executed: $other")
      }
    }
  }

  test("a rewrite whose memory instructions the recorded launch does not cover is refused with the reason") {
    val image          = WindowFixtures.stream
    val kernel         = WindowFixtures.streaming.raw
    val stream         = launched(image, kernel, Vec(4096L, 4096L, trips))
    val (window, code) = spanning(image, kernel, "s_add_u32 s0", "s_load_dword s9")
    val cost           = AMDGPUBlockCost.of(image, kernel, target, recorded(stream)).fold(fail(_), identity)
    val whole          = cost.cost(window, code)
    assert(whole.isRight, whole)
    val changed = cost.cost(window, code.init :+ "s_load_dword s9, s[0:1], 0x4")
    assert(changed.left.exists(_.startsWith("no recorded memory traffic for s_load_dword s9")), changed)
    val moved = cost.cost(window, "s_add_u32 s0, s0, 8" +: code.tail)
    assert(moved.left.exists(_.contains("reads s0, s1, which the rewrite changes")), moved)
    val masked = cost.cost(window, "s_mov_b32 exec_lo, s6" +: code)
    assert(masked.left.exists(_.contains("writes the lane mask")), masked)
    val deleted = cost.cost(window, code.init)
    assert(deleted.isRight, deleted)
  }

  test("a loop with two paths is costed when the recorded launch issues its blocks evenly, and withheld otherwise") {
    val kernel                                                   = "window"
    def costOf(image: Vec[Byte], launch: AMDGPUBlockCost.Launch) = {
      val (window, code) = spanning(image, kernel, "v_fma_f32", "v_fma_f32")
      AMDGPUBlockCost.of(image, kernel, target, launch).flatMap(_.cost(window, code))
    }
    val even   = WindowFixtures.branching(trips = 6, skipped = 100)
    val stream = launched(even, kernel)
    val stated = costOf(even, oneWave)
    assert(stated.left.exists(_.endsWith("has more than one path and no recorded issue counts")), stated)
    looped(stream)._2 match {
      case BlockAnalysis.Status.Executed(timing) =>
        assertEquals(costOf(even, recorded(stream)).map(_.cycles), Right(timing.block.cyclesPerIteration))
      case other => fail(s"the loop is not executed: $other")
    }
    val uneven   = WindowFixtures.branching(trips = 6, skipped = 2)
    val withheld = costOf(uneven, recorded(launched(uneven, kernel)))
    assert(withheld.left.exists(_.contains("is withheld: uneven issue counts at ")), withheld)
  }

  test("a rewrite that needs more registers is costed at the residency they leave") {
    val resident = AMDGPUBlockCost.Launch.Stated(32, 0L, None)
    val within   = costOf(resident, text :+ "v_mov_b32 v7, v5")
    val beyond   = costOf(resident, text :+ "v_mov_b32 v255, v5")
    assertEquals(within.shift, beyond.shift)
    assert(beyond.cycles > within.cycles, s"$within against $beyond")
  }
}
