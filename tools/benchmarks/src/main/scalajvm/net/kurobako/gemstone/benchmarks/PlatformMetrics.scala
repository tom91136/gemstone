package net.kurobako.gemstone.benchmarks

import java.lang.management.ManagementFactory
import java.nio.file.Path
import jdk.jfr.Configuration
import jdk.jfr.Recording
import net.kurobako.gemstone.constraint.*
import scala.util.Try

object PlatformMetrics {
  val platform = "jvm"

  private lazy val allocationBean = ManagementFactory.getThreadMXBean match {
    case value: com.sun.management.ThreadMXBean if value.isThreadAllocatedMemorySupported =>
      if (!value.isThreadAllocatedMemoryEnabled) value.setThreadAllocatedMemoryEnabled(true)
      Some(value)
    case _ => None
  }

  def allocatedBytes: Option[Long >= 0] =
    allocationBean.flatMap(bean =>
      Try(bean.getThreadAllocatedBytes(Thread.currentThread().threadId()).unsafe[Long >= 0]).toOption
    )

  def startRecording(destination: Option[Path]): AutoCloseable = destination match {
    case None       => () => ()
    case Some(path) =>
      val recording = Recording(Configuration.getConfiguration("profile"))
      recording.setDestination(path)
      recording.start()
      () => {
        recording.stop()
        recording.close()
      }
  }
}
