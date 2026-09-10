package net.kurobako.gemstone.conformance

import java.nio.file.Path
import scala.concurrent.duration.*

final class NativeRuntimeSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 15.minutes
  private val jobs                    = sys.env.get("GEMSTONE_EXECUTION_PARALLELISM").fold(
    Runtime.getRuntime.availableProcessors()
  )(value =>
    value.toIntOption.filter(_ > 0)
      .getOrElse(throw IllegalArgumentException(s"invalid GEMSTONE_EXECUTION_PARALLELISM: $value"))
  )
  private val directories = {
    val resource = getClass.getResourceAsStream("/native-runtimes.properties")
    require(resource != null, "sbt did not prepare native runtimes")
    val properties = java.util.Properties()
    try properties.load(resource)
    finally resource.close()
    properties
  }
  Vector("cuda", "hip", "hsa", "opencl", "levelzero").foreach { runtime =>
    test(s"native/$runtime") {
      ProcessCapture.measured(s"native/$runtime") {
        val directory = Path.of(directories.getProperty(runtime))
        ConformanceSupport.runChecked(
          Vector(
            "ctest",
            "--test-dir",
            directory.toString,
            "--output-on-failure",
            "--parallel",
            jobs.toString
          ),
          ConformanceSupport.root
        )
      }
    }
  }
}
