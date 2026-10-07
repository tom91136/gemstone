package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*

object PlatformMetrics {
  val platform = "native"

  def allocatedBytes: Option[Long >= 0] = None

  def startRecording(name: String, enabled: Boolean): AutoCloseable = () => ()
}
