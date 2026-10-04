package net.kurobako.gemstone.benchmarks

import java.lang.management.ManagementFactory
import java.nio.file.Files
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

  def startRunRecording(name: BenchmarkName, enabled: Boolean): AutoCloseable = {
    val destination = Option.when(enabled) {
      val directory = Files.createDirectories(Path.of("target/benchmarks/jfr").toAbsolutePath)
      Files.createTempDirectory(directory, s"${name.raw}-").resolve("benchmark.jfr")
    }
    startRecording(destination)
  }

  def startRecording(destination: Option[Path]): AutoCloseable = destination match {
    case None       => () => ()
    case Some(path) =>
      val recording   = Recording(Configuration.getConfiguration("profile"))
      val destination = path.toAbsolutePath
      Files.createDirectories(destination.getParent)
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
