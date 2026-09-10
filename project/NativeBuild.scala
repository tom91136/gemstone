import sbt.*
import sbt.Keys.*
import sbt.util.Logger

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

object NativeBuild {
  private val buildMonitor                                = Object()
  private def withBuildLock[A](root: File)(body: => A): A = buildMonitor.synchronized {
    val lockFile = root / "target/fixture-build.lock"
    IO.createDirectory(lockFile.getParentFile)
    val channel = java.nio.channels.FileChannel.open(
      lockFile.toPath,
      java.nio.file.StandardOpenOption.CREATE,
      java.nio.file.StandardOpenOption.WRITE
    )
    val lock = channel.lock()
    try body
    finally { lock.release(); channel.close() }
  }

  val nativeBuild              = taskKey[File]("Configure and build the native runtime and its test executables.")
  val nativeConfigureArguments = taskKey[Seq[String]]("Additional native CMake configure arguments.")
  val ctest                    = taskKey[Unit]("Build and run this runtime's native tests explicitly.")

  val settings: Seq[Def.Setting[?]] = Seq(
    nativeConfigureArguments := Seq.empty,
    nativeBuild              := Def.uncached {
      val source    = baseDirectory.value
      val build     = target.value / "cmake"
      val log       = streams.value.log
      val arguments = nativeConfigureArguments.value
      withBuildLock((LocalRootProject / baseDirectory).value) {
        execute(
          Seq(
            "cmake",
            "-S",
            source.getAbsolutePath,
            "-B",
            build.getAbsolutePath,
            "-G",
            "Ninja",
            "-DBUILD_TESTING=ON",
            s"-DCMAKE_BUILD_TYPE=${sys.env.getOrElse("CMAKE_BUILD_TYPE", "Release")}"
          ) ++ arguments,
          source,
          log
        )
        val jobs =
          sys.env.get("CMAKE_BUILD_PARALLEL_LEVEL").fold(math.min(
            8,
            java.lang.Runtime.getRuntime.availableProcessors()
          )) {
            value =>
              value.toIntOption.filter(
                _ > 0
              ).getOrElse(throw IllegalArgumentException(s"invalid CMAKE_BUILD_PARALLEL_LEVEL: $value"))
          }
        execute(Seq("cmake", "--build", build.getAbsolutePath, "--parallel", jobs.toString), source, log)
      }
      build
    },
    ctest := Def.uncached {
      val build = nativeBuild.value
      val jobs = sys.env.get("GEMSTONE_EXECUTION_PARALLELISM").fold(
        java.lang.Runtime.getRuntime.availableProcessors()
      )(value => value.toIntOption.filter(_ > 0)
          .getOrElse(throw IllegalArgumentException(s"invalid GEMSTONE_EXECUTION_PARALLELISM: $value")))
      execute(
        Seq("ctest", "--test-dir", build.getAbsolutePath, "--output-on-failure", "--parallel", jobs.toString),
        baseDirectory.value,
        streams.value.log
      )
    }
  )

  /** Properties.store escapes Windows separators and non-ASCII paths; omit its timestamp for stable resources. */
  def writeRuntimeProperties(output: File, directories: Seq[(String, File)]): Unit = {
    val properties = new java.util.Properties()
    directories.foreach { case (name, directory) => properties.setProperty(name, directory.getAbsolutePath) }
    val bytes = new java.io.ByteArrayOutputStream()
    properties.store(bytes, null)
    val content = bytes.toString(java.nio.charset.StandardCharsets.ISO_8859_1)
      .linesIterator.filterNot(_.startsWith("#")).toSeq.sorted.mkString("", "\n", "\n")
    if (!output.isFile || IO.read(output) != content) IO.write(output, content)
  }

  private def execute(command: Seq[String], directory: File, log: Logger): Unit = {
    log.info(command.mkString(" "))
    val builder = ProcessBuilder(command*)
    builder.directory(directory)
    val process  = builder.start()
    val stdout   = pump(process.getInputStream, log.info, "gemstone-cmake-stdout")
    val stderr   = pump(process.getErrorStream, log.error, "gemstone-cmake-stderr")
    val complete = process.waitFor(10, TimeUnit.MINUTES)
    if (!complete) {
      process.descendants().forEach { descendant =>
        val _ = descendant.destroyForcibly()
      }
      val _ = process.destroyForcibly()
      val _ = process.waitFor()
    }
    stdout.join()
    stderr.join()
    require(complete, s"command timed out after 10 minutes: ${command.mkString(" ")}")
    val status = process.exitValue()
    require(status == 0, s"command exited with $status: ${command.mkString(" ")}")
  }

  private def pump(input: InputStream, write: String => Unit, name: String): Thread = {
    val thread = Thread(
      () => {
        val reader = BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8))
        try Iterator.continually(reader.readLine()).takeWhile(_ != null).foreach(write)
        finally reader.close()
      },
      name
    )
    thread.start()
    thread
  }
}
