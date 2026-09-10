package net.kurobako.gemstone.conformance

import net.kurobako.gemstone.launcher.Environment
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, StandardOpenOption}
import java.time.Instant
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.*
import scala.util.{DynamicVariable, Try}

/** File-backed subprocess capture and optional run measurements. No background log polling. */
private[conformance] object ProcessCapture {
  private val label      = DynamicVariable("")
  private def reportRoot = sys.env.get("GEMSTONE_CONFORMANCE_REPORT_DIR").map(Path.of(_))

  private final class Peak(channel: FileChannel) {
    def finish(): Option[Long] = try {
      channel.position(0L)
      val buffer = ByteBuffer.allocate(128)
      channel.read(buffer)
      buffer.flip()
      StandardCharsets.UTF_8.decode(buffer).toString.trim.toLongOption
    } finally channel.close()
  }
  private def peak(): Option[Peak] = sys.env.get("GEMSTONE_RESOURCE_CGROUP").flatMap { group =>
    Try {
      val channel = FileChannel.open(Path.of(group, "memory.peak"), StandardOpenOption.READ, StandardOpenOption.WRITE)
      try {
        // memory.peak resets apply to this descriptor; the run-wide peak remains available separately.
        channel.write(ByteBuffer.wrap(Array[Byte](49)))
        Peak(channel)
      } catch { case error: Throwable => channel.close(); throw error }
    }.toOption
  }
  private def record(value: ujson.Obj, report: Option[Path]): Unit = report.foreach { directory =>
    Files.createDirectories(directory.resolve("measurements"))
    val output = Files.createTempFile(directory.resolve("measurements"), "measurement-", ".json")
    Files.writeString(output, ujson.write(value))
    ()
  }
  def measured[A](name: String, report: Option[Path] = reportRoot)(body: => A): A = label.withValue(name) {
    val start   = System.nanoTime()
    val memory  = peak()
    var passed  = false
    var primary = Option.empty[Throwable]
    try {
      val result = body
      passed = true
      result
    } catch { case error: Throwable => primary = Some(error); throw error }
    finally
      try
        record(
          ujson.Obj(
            "kind"              -> "case",
            "name"              -> name,
            "returned_normally" -> passed,
            "elapsed_seconds"   -> ((System.nanoTime() - start).toDouble / 1e9),
            "scope_peak_bytes"  -> memory.flatMap(_.finish()).fold[ujson.Value](ujson.Null)(n => ujson.Num(n.toDouble))
          ),
          report
        )
      catch {
        case error: Throwable => primary match {
            case Some(original) => original.addSuppressed(error)
            case None           => throw error
          }
      }
  }

  def run(
      command: Vector[String],
      directory: Path,
      environment: Environment,
      timeout: FiniteDuration,
      report: Option[Path] = reportRoot
  ): ConformanceSupport.ProcessResult = {
    require(timeout > Duration.Zero, s"process timeout must be positive: $timeout")
    val storage = report.map(root => Files.createDirectories(root.resolve("processes")))
    val output  = storage.fold(Files.createTempDirectory("gemstone-process-"))(parent =>
      Files.createTempDirectory(parent, "process-")
    )
    val stdout = output.resolve("stdout.log")
    val stderr = output.resolve("stderr.log")
    val usage  = output.resolve("usage.txt")
    val timed  = if Files.isExecutable(Path.of("/usr/bin/time")) then
      Vector("/usr/bin/time", "-o", usage.toString, "-f", "rss_kib=%M", "--") ++ command
    else command
    val builder = ProcessBuilder(timed*).directory(directory.toFile)
      .redirectOutput(stdout.toFile).redirectError(stderr.toFile)
    environment.applyTo(builder.environment())
    val started                               = Instant.now().toString
    val start                                 = System.nanoTime()
    val memory                                = peak()
    var exit                                  = -1
    var completed                             = false
    var captured                              = Option.empty[ConformanceSupport.ProcessResult]
    var primary                               = Option.empty[Throwable]
    def retainFailure(error: Throwable): Unit = primary match {
      case Some(original) => original.addSuppressed(error)
      case None           => captured match {
          case Some(result) if result.exit != 0 =>
            captured = Some(result.copy(stderr = result.stderr + s"\nmeasurement/cleanup failed: $error\n"))
          case _ => primary = Some(error)
        }
    }
    try {
      val process = builder.start()
      completed = process.waitFor(timeout.toMillis, TimeUnit.MILLISECONDS)
      if !completed then {
        val descendants = process.descendants().toArray().map(_.asInstanceOf[ProcessHandle])
        process.children().forEach { child =>
          val _ = child.destroy()
        }
        // Fixture wrappers forward TERM and clean up their named container before releasing the build lock.
        if !process.waitFor(5, TimeUnit.SECONDS) then {
          descendants.reverseIterator.foreach { child =>
            val _ = child.destroyForcibly()
          }
          process.destroyForcibly()
          val _ = process.waitFor()
        }
      }
      exit = if completed then process.exitValue() else -1
      captured = Some(ConformanceSupport.ProcessResult(
        command,
        exit,
        "",
        if completed then "" else s"process timed out after $timeout\n"
      ))
    } catch { case error: Throwable => primary = Some(error) }
    if captured.nonEmpty then {
      try captured = captured.map(result => result.copy(stdout = excerpt(stdout)))
      catch { case error: Throwable => retainFailure(error) }
      try captured = captured.map(result => result.copy(stderr = excerpt(stderr) + result.stderr))
      catch { case error: Throwable => retainFailure(error) }
    }
    try {
      val rss = if Files.isRegularFile(usage) then
        Files.readString(usage).linesIterator
          .find(_.startsWith("rss_kib=")).flatMap(_.stripPrefix("rss_kib=").toLongOption)
      else None
      record(
        ujson.Obj(
          "kind"             -> "process",
          "name"             -> label.value,
          "started"          -> started,
          "command"          -> ujson.Arr.from(command),
          "directory"        -> directory.toString,
          "exit"             -> exit,
          "elapsed_seconds"  -> ((System.nanoTime() - start).toDouble / 1e9),
          "max_rss_kib"      -> rss.fold[ujson.Value](ujson.Null)(n => ujson.Num(n.toDouble)),
          "scope_peak_bytes" -> memory.flatMap(_.finish()).fold[ujson.Value](ujson.Null)(n => ujson.Num(n.toDouble)),
          "stdout"           -> stdout.toString,
          "stderr"           -> stderr.toString
        ),
        report
      )
    } catch { case error: Throwable => retainFailure(error) }
    if storage.isEmpty then
      Vector(stdout, stderr, usage, output).foreach { path =>
        try { val _ = Files.deleteIfExists(path) }
        catch { case error: Throwable => retainFailure(error) }
      }
    primary.foreach(error => throw error)
    captured.get
  }
  private def excerpt(path: Path): String = {
    val limit   = 256 * 1024
    val channel = FileChannel.open(path, StandardOpenOption.READ)
    try {
      val size   = channel.size()
      val buffer = ByteBuffer.allocate(math.min(size, limit.toLong).toInt)
      if size > limit then { val _ = channel.position(size - limit) }
      while buffer.hasRemaining && channel.read(buffer) >= 0 do ()
      buffer.flip()
      (if size > limit then s"[truncated; full output: $path]\n" else "") +
        StandardCharsets.UTF_8.decode(buffer).toString
    } finally channel.close()
  }
}
