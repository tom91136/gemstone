package net.kurobako.gemstone.toolchain

import java.nio.file.Path
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.process
import scala.concurrent.duration.*

/** Every collector must pass every check once the workarounds are installed. Each defect must still show without them;
  * when one stops showing, the Scala Native release in use has fixed it, and its workaround or collector choice is due
  * for review.
  */
class ToolchainCheckSuite extends Suite {
  override def baseTimeout: FiniteDuration = 2.minutes

  private val collectors = Seq("boehm", "commix", "immix")

  private def run(collector: String, check: String, workarounds: Boolean, timeout: FiniteDuration): process.Result = {
    val binary = Path.of(sys.props(s"gemstone.toolchain.check.$collector"))
    process.run(
      Seq(binary.toString, check) ++ Option.when(workarounds)("--workarounds"),
      binary.getParent,
      Map("GC_MAXIMUM_HEAP_SIZE" -> (if (check == "heap-growth") "128M" else "512M")),
      Set.empty,
      timeout
    )
  }

  private def passed(result: process.Result): Boolean  = result.exit == 0 && result.stdout.startsWith("passed")
  private def stalled(result: process.Result): Boolean = result.exit == 1 && result.stdout.startsWith("stalled")

  for {
    collector <- collectors
    check     <- Seq("reference-handler", "reference-burst")
  } test(s"$collector passes $check with the workarounds") {
    val result = run(collector, check, workarounds = true, 30.seconds)
    assert(passed(result), result.diagnostic)
  }

  test("boehm passes heap-growth") {
    val result = run("boehm", "heap-growth", workarounds = false, 30.seconds)
    assert(passed(result), result.diagnostic)
  }

  // Each defect fails its own way; another failure would hide whether the defect is still there.
  for {
    (collector, check, failure) <- Seq[(String, String, process.Result => Boolean)](
      ("commix", "reference-handler", stalled),
      ("boehm", "reference-burst", _.exit == -1),
      ("commix", "heap-growth", !passed(_)),
      ("immix", "heap-growth", !passed(_))
    )
  } test(s"$collector still fails $check unpatched") {
    // the deadlock freezes the whole process, so only the timeout ends it
    val result = run(collector, check, workarounds = false, 15.seconds)
    assert(
      failure(result),
      s"$check under $collector no longer fails as it did on Scala Native 0.5.12\n${result.diagnostic}"
    )
  }
}
