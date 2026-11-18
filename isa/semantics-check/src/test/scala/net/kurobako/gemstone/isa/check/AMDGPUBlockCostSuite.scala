package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.BlockAnalysis
import net.kurobako.gemstone.profile.HardwareLoops
import net.kurobako.gemstone.profile.PipelineSimulator
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.LaunchGeometry

class AMDGPUBlockCostSuite extends Suite {
  private val target   = WindowFixtures.calibrated
  private val function = "ctl_f4_tail"
  private val oneWave  = AMDGPUBlockCost.Launch(32, 65536L, Some(PipelineSimulator.Placement(Vec(1, 0, 0, 0))))

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
    val static = costOf(AMDGPUBlockCost.Launch(32, 0L, oneWave.placement), text).cycles
    recorded match {
      case Right(Vec(BlockAnalysis.Status.Executed(timing))) => assertEquals(timing.block.cyclesPerIteration, static)
      case other                                             => fail(s"no executed loop: $other")
    }
  }

  test("a rewrite that needs more registers is costed at the residency they leave") {
    val resident = AMDGPUBlockCost.Launch(32, 0L, None)
    val within   = costOf(resident, text :+ "v_mov_b32 v7, v5")
    val beyond   = costOf(resident, text :+ "v_mov_b32 v255, v5")
    assertEquals(within.shift, beyond.shift)
    assert(beyond.cycles > within.cycles, s"$within against $beyond")
  }
}
