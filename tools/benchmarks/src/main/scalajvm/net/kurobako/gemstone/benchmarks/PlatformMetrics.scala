package net.kurobako.gemstone.benchmarks

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.lang.management.ManagementFactory
import java.nio.file.Path
import jdk.jfr.{Configuration, Recording}

object PlatformMetrics {
  val platform = "jvm"

  private lazy val allocationBean = ManagementFactory.getThreadMXBean match {
    case value: com.sun.management.ThreadMXBean if value.isThreadAllocatedMemorySupported =>
      if (!value.isThreadAllocatedMemoryEnabled) value.setThreadAllocatedMemoryEnabled(true)
      Some(value)
    case _ => None
  }

  def allocatedBytes: Option[Long :| Positive0] =
    allocationBean.flatMap(_.getThreadAllocatedBytes(Thread.currentThread().threadId()).refineOption[Positive0])

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
