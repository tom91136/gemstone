package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*
import scala.annotation.unused

object PlatformMetrics {
  val platform = "native"

  def allocatedBytes: Option[Long >= 0] = None

  // matches the JVM recorder; native has no flight recorder
  def startRecording(@unused name: String, @unused enabled: Boolean): AutoCloseable = () => ()
}
