import sbt.*
import sbt.util.Logger

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

object CMakeTest {
  val ctest = taskKey[Unit]("Configure, build, and run this project's native CTest suite.")

  def run(source: File, build: File, arguments: Seq[String], log: Logger): Unit = {
    execute(
      Seq("cmake", "-S", source.getAbsolutePath, "-B", build.getAbsolutePath, "-G", "Ninja") ++
        arguments,
      source,
      log
    )
    execute(Seq("cmake", "--build", build.getAbsolutePath, "--parallel"), source, log)
    execute(
      Seq("ctest", "--test-dir", build.getAbsolutePath, "--output-on-failure", "--parallel"),
      source,
      log
    )
  }

  private def execute(command: Seq[String], directory: File, log: Logger): Unit = {
    log.info(command.mkString(" "))
    val builder = ProcessBuilder(command*)
    builder.directory(directory)
    val process = builder.start()
    val stdout  = pump(process.getInputStream, log.info, "gemstone-cmake-stdout")
    val stderr  = pump(process.getErrorStream, log.error, "gemstone-cmake-stderr")
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
