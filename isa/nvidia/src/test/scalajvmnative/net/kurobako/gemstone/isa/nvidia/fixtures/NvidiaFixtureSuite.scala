package net.kurobako.gemstone.isa.nvidia.fixtures

import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.configuration
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*
import scala.util.control.NonFatal

/** Set GEMSTONE_FIXTURE_VERIFY_ON=1 to reproduce fixtures with NVIDIA tools on the JVM. */
abstract class NvidiaFixtureSuite extends Suite {
  override def baseTimeout: FiniteDuration = 3.minutes

  protected def reproduces(name: String)(
      tools: (ptxas: String, nvdisasm: Option[String]),
      inputs: Vec[(String, String)],
      script: Vec[String],
      output: String,
      expected: String
  )(using munit.Location): Unit = verifies(name)(tools, inputs) { (directory, run) =>
    run(script)
    val path = directory.resolve(output).normalize()
    assert(path.startsWith(directory), s"Output must be inside the fixture directory: $output")
    assert(Files.isRegularFile(path), s"Recipe did not produce $output")
    assertEquals(Files.readString(path), expected, s"Generated $output in $directory")
  }

  protected def verifies(name: String)(
      tools: (ptxas: String, nvdisasm: Option[String]),
      inputs: Vec[(String, String)]
  )(check: (Path, Vec[String] => Unit) => Unit)(using munit.Location): Unit = {
    val enabled =
      configuration.enabled(
        sys.env,
        env.FixtureVerifyOn
      ).fold(message => throw IllegalArgumentException(message), identity)
    test(if (enabled) munit.TestOptions(name) else munit.TestOptions(name).ignore) {
      val directory = files.Directory("gemstone-nvidia-fixture-")
      val log       = directory.path.resolve("recipe.log")
      try {
        inputs.foreach { (filename, content) =>
          val path = directory.path.resolve(filename).normalize()
          assert(path.startsWith(directory.path), s"Input must be inside the fixture directory: $filename")
          val _ = files.write(directory.path, filename, content)
        }
        check(directory.path, script => NvidiaFixtureProcess.run(tools, script, directory.path, log))
        directory.close()
      } catch {
        case NonFatal(error) =>
          val output = if (Files.isRegularFile(log)) Files.readString(log) else ""
          fail(s"${error.getMessage}\nFixture files retained at ${directory.path}\n$output", error)
      }
    }
  }
}
