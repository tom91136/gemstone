package net.kurobako.gemstone.tools.conformance

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.*

class EmulatedDeviceSuite extends munit.FunSuite {
  private val Printer = "printf '%s\\n' \"$0\" \"$@\""

  private def launched(backend: String, architecture: String, program: String): Vector[String] = {
    val directory = Files.createTempDirectory("emulated-device-")
    try {
      val script  = directory.resolve("launcher.sh")
      val _       = Files.writeString(script, CLI.launcher(Vector("/bin/sh", "-c", Printer), Map.empty))
      val builder = ProcessBuilder("/bin/sh", s"$script", "--", s"/artifacts/$program", "268435456")
      builder.redirectErrorStream(true)
      builder.environment().clear()
      builder.environment().putAll(
        Map(
          conformanceEnvironment.CudaCodeMode      -> "sass",
          conformanceEnvironment.Program           -> program,
          conformanceEnvironment.TargetArch        -> architecture,
          conformanceEnvironment.TargetBackendName -> backend
        ).asJava
      )
      val process = builder.start()
      val output  = String(process.getInputStream.readAllBytes(), StandardCharsets.UTF_8)
      assert(process.waitFor(30, TimeUnit.SECONDS), s"launcher did not finish: $output")
      assertEquals(process.exitValue(), 0, output)
      output.linesIterator.toVector
    } finally {
      Files.deleteIfExists(directory.resolve("launcher.sh"))
      Files.deleteIfExists(directory)
    }
  }

  test("a HeCBench case reports the card whose target matches its route") {
    assertEquals(
      launched("cuda", "sm_89", "hecbench_blas-dot").take(3),
      Vector("--device", "GeForce RTX 4060 Ti", "--")
    )
  }

  test("a HeCBench case follows a different route's preset") {
    assertEquals(launched("cuda", "sm_120", "hecbench_blas-dot").take(3), Vector("--device", "GeForce RTX 5080", "--"))
  }

  test("other programs keep the four multiprocessor default") {
    assertEquals(launched("cuda", "sm_89", "unified").take(2), Vector("--", "/artifacts/unified"))
  }

  test("a route without a matching preset keeps the default") {
    assertEquals(launched("cuda", "sm_90", "hecbench_blas-dot").take(2), Vector("--", "/artifacts/hecbench_blas-dot"))
  }
}
