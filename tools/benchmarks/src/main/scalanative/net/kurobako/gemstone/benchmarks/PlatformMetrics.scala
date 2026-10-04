package net.kurobako.gemstone.benchmarks

import java.nio.file.Path
import net.kurobako.gemstone.constraint.*

object PlatformMetrics {
  val platform = "native"

  def allocatedBytes: Option[Long >= 0] = None

  def startRunRecording(name: BenchmarkName, enabled: Boolean): AutoCloseable = () => ()

  def startRecording(destination: Option[Path]): AutoCloseable = destination match {
    case None    => () => ()
    case Some(_) => throw UnsupportedOperationException("JFR recording is unavailable on Scala Native")
  }
}
