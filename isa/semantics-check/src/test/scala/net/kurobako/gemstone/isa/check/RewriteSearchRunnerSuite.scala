package net.kurobako.gemstone.isa.check

import java.io.ByteArrayOutputStream
import java.nio.file.Files
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.semantics.render.Bitwuzla
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

class RewriteSearchRunnerSuite extends Suite {
  override def baseTimeout: FiniteDuration = 120.seconds

  private val target = "gfx906"
  private val kernel = WindowFixtures.kernel
  private val size   = 8
  private val config = RewriteSearch.Config.default

  private def installed(): Unit =
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")

  private def printed(arguments: Array[String]): Vec[String] = {
    val buffer = ByteArrayOutputStream()
    Console.withOut(buffer)(RewriteSearchRunner.main(arguments))
    buffer.toString.linesIterator.toVec
  }

  private def fixture(name: String, image: Vec[Byte]): Unit =
    test(s"the runner prints the $name fixture as the search suite does") {
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
          )
        )
        val expected = AMDGPUWindows.of(image, kernel, target, size).toOption.get.flatMap(window =>
          RewriteSearch.lines(name, window, RewriteSearch.amdgpu(image, kernel, target, window, config))
        )
        assertEquals(output, expected)
      } finally {
        Files.delete(file)
        Files.delete(directory)
      }
    }

  fixture("loop", WindowFixtures.loop)
  fixture("state", WindowFixtures.state)
  fixture("copy", WindowFixtures.vectorCopy)
}
