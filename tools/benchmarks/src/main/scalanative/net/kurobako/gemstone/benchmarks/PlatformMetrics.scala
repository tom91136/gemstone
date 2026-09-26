package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.NonNegativeLong

import java.nio.file.Path

object PlatformMetrics {
  val platform = "native"

  def allocatedBytes: Option[NonNegativeLong] = None

  def startRecording(destination: Option[Path]): AutoCloseable = destination match {
    case None    => () => ()
    case Some(_) => throw UnsupportedOperationException("JFR recording is unavailable on Scala Native")
  }
}
