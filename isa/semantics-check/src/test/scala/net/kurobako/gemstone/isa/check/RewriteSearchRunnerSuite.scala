package net.kurobako.gemstone.isa.check

import java.io.ByteArrayOutputStream
import java.nio.file.Files
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.profile.PipelineSimulator
import net.kurobako.gemstone.semantics.render.Bitwuzla
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

class RewriteSearchRunnerSuite extends Suite {
  override def baseTimeout: FiniteDuration = 200.seconds

  private val target = WindowFixtures.calibrated
  private val size   = 8
  private val config = RewriteSearch.Config.default
  private val launch = AMDGPUBlockCost.Launch.Stated(32, 65536L, Some(PipelineSimulator.Placement(Vec(1, 0, 0, 0))))

  private def installed(): Unit =
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")

  private def printed(arguments: Array[String]): Vec[String] = {
    val buffer = ByteArrayOutputStream()
    Console.withOut(buffer)(RewriteSearchRunner.main(arguments))
    buffer.toString.linesIterator.toVec
  }

  private val stated = Array("--lanes", "32", "--shared", "65536", "--placement", "1,0,0,0")

  private def fixture(
      kernel: String,
      image: Vec[Byte] = WindowFixtures.compiled,
      flags: Array[String] = stated,
      under: => AMDGPUBlockCost.Launch = launch
  ): Unit =
    test(s"the runner prints the $kernel kernel as the search does under the launch its flags state") {
      val name = "fixtures"
      installed()
      val directory = Files.createTempDirectory("rewrite-search-runner")
      val file      = directory.resolve(s"$name.co")
      try {
        Files.write(file, image.toArray)
        val output = printed(
          Array(
            file.toString,
            kernel,
            target,
            "--seed",
            config.seed.toString,
            "--budget",
            config.budget.toString,
            "--temperature",
            config.temperature.toString,
            "--window",
            size.toString
          ) ++ flags
        )
        val expected = AMDGPUWindows.of(image, kernel, target, size).toOption.get.flatMap(window =>
          RewriteSearch.lines(name, window, RewriteSearch.amdgpu(image, kernel, target, window, under, config))
        )
        assertEquals(output, expected)
      } finally {
        Files.delete(file)
        Files.delete(directory)
      }
    }

  fixture("ctl_f4_tail")
  fixture("ctl_f4_dead")
  fixture(
    WindowFixtures.streaming.raw,
    WindowFixtures.stream,
    Array("--lanes", "32", "--placement", "1,0,0,0", "--record", "1", "--arguments", "4096,4096,5"),
    AMDGPUBlockCost.Launch.Recorded(
      AMDGPUBlockCost.record(WindowFixtures.stream, WindowFixtures.streaming.raw, 32, 1, Vec(4096L, 4096L, 5L))
        .toOption.get,
      launch.placement
    )
  )
}
