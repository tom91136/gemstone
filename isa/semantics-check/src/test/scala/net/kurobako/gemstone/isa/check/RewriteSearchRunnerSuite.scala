package net.kurobako.gemstone.isa.check

import java.io.ByteArrayOutputStream
import java.nio.file.Files
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.explorer.*
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
    Array("--lanes", "32", "--placement", "1,0,0,0", "--record", "1", "--arguments", "buffer:4096,buffer:4096,i32:5"),
    AMDGPUBlockCost.Launch.Recorded(
      LaunchRecording.record(
        Language.AMDGPU,
        WindowFixtures.stream,
        WindowFixtures.streaming.raw,
        32,
        1,
        LaunchFixtures.arguments("buffer:4096,buffer:4096,i32:5")
      )
        .toOption.get,
      launch.placement
    )
  )

  test("the runner prints the SASS windows of a kernel as the search does, with the rewrite it proves") {
    installed()
    val kernel    = SASSFixtures.kernel
    val directory = Files.createTempDirectory("rewrite-search-runner")
    val file      = directory.resolve("fixtures.cubin")
    try {
      Files.write(file, SASSFixtures.arithmetic.toArray)
      val output = printed(
        Array(
          file.toString,
          kernel,
          SASSFixtures.target,
          "--seed",
          config.seed.toString,
          "--budget",
          "8",
          "--lanes",
          "32"
        )
      )
      val small    = config.copy(budget = 8)
      val expected = SASSWindows.of(SASSFixtures.arithmetic, kernel, SASSFixtures.target).toOption.get.flatMap(window =>
        RewriteSearch.lines(
          "fixtures",
          window,
          RewriteSearch.sass(SASSFixtures.arithmetic, kernel, SASSFixtures.target, window, SASSFixtures.launch, small)
        )
      )
      assertEquals(output, expected)
      assert(output.exists(line => line.contains(" from ") && line.contains("IADD3 R4")), output)
    } finally {
      Files.delete(file)
      Files.delete(directory)
    }
  }

  test("the runner records an SASS launch and searches the windows against it") {
    installed()
    val kernel    = SASSFixtures.kernel
    val directory = Files.createTempDirectory("rewrite-search-runner")
    val file      = directory.resolve("fixtures.cubin")
    try {
      Files.write(file, SASSFixtures.arithmetic.toArray)
      val output = printed(
        Array(
          file.toString,
          kernel,
          SASSFixtures.target,
          "--seed",
          config.seed.toString,
          "--budget",
          "8",
          "--lanes",
          "32",
          "--record",
          "1",
          "--arguments",
          "buffer:4096,i32:1,i32:2,i32:3"
        )
      )
      val stream = LaunchRecording.record(
        Language.SASS,
        SASSFixtures.arithmetic,
        kernel,
        32,
        1,
        LaunchFixtures.arguments("buffer:4096,i32:1,i32:2,i32:3")
      )
        .fold(fail(_), identity)
      val recorded = AMDGPUBlockCost.Launch.Recorded(stream, None)
      val small    = config.copy(budget = 8)
      val expected = SASSWindows.of(SASSFixtures.arithmetic, kernel, SASSFixtures.target).toOption.get.flatMap(window =>
        RewriteSearch.lines(
          "fixtures",
          window,
          RewriteSearch.sass(SASSFixtures.arithmetic, kernel, SASSFixtures.target, window, recorded, small)
        )
      )
      assertEquals(output, expected)
      assert(output.exists(line => line.contains(" from ") && line.contains("IADD3 R4")), output)
    } finally {
      Files.delete(file)
      Files.delete(directory)
    }
  }
}
