package net.kurobako.gemstone.benchmarks

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.file.Path

object PlatformMetrics {
  val platform = "native"

  def allocatedBytes: Option[Long :| Positive0] = None

  def startRecording(destination: Option[Path]): AutoCloseable = destination match {
    case None    => () => ()
    case Some(_) => throw UnsupportedOperationException("JFR recording is unavailable on Scala Native")
  }
}
