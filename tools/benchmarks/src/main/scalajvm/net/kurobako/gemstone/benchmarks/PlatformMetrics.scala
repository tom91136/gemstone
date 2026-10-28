package net.kurobako.gemstone.benchmarks

import com.sun.management.ThreadMXBean
import java.lang.management.ManagementFactory
import java.nio.file.Files
import java.nio.file.Path
import jdk.jfr.Configuration
import jdk.jfr.Recording
import net.kurobako.gemstone.constraint.*
import scala.util.Try

object PlatformMetrics {
  val platform: String = "jvm"

  private lazy val allocationBean = ManagementFactory.getThreadMXBean match {
    case value: ThreadMXBean if value.isThreadAllocatedMemorySupported =>
      if (!value.isThreadAllocatedMemoryEnabled) value.setThreadAllocatedMemoryEnabled(true)
      Some(value)
    case _ => None
  }

  def allocatedBytes: Option[Long >= 0] =
    allocationBean.flatMap(bean =>
      Try(bean.getThreadAllocatedBytes(Thread.currentThread().threadId()).unsafe[Long >= 0]).toOption
    )

  def startRecording(name: String, enabled: Boolean): AutoCloseable =
    if (!enabled) () => ()
    else {
      val directory   = Files.createDirectories(Path.of("target/benchmarks/jfr").toAbsolutePath)
      val destination = Files.createTempDirectory(directory, s"$name-").resolve("benchmark.jfr")
      val recording   = Recording(Configuration.getConfiguration("profile"))
      recording.setDestination(destination)
      recording.setMaxSize(512L * 1024 * 1024)
      recording.setDumpOnExit(true)
      recording.start()
      Console.err.println(s"Benchmark JFR recording: $destination (up to 512 MiB; oldest events rotate)")
      () => {
        try recording.stop()
        finally recording.close()
        Console.err.println(s"Benchmark JFR written to: $destination")
      }
    }
}
