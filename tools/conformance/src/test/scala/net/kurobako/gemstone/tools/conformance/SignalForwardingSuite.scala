package net.kurobako.gemstone.tools.conformance

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermissions
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*
import scala.util.Using
import sun.misc.Signal

class SignalForwardingSuite extends munit.FunSuite {
  private val DriverStatus     = 7
  private val TerminatedStatus = 128 + 15
  private val driver           = Iterator
    .iterate(Option(Path.of("").toAbsolutePath))(_.flatMap(directory => Option(directory.getParent)))
    .takeWhile(_.isDefined)
    .flatten
    .find(directory => Files.exists(directory.resolve("build.sbt")))
    .map(_.resolve("target/conformance/host/bin/conformance"))
    .filter(Files.isExecutable)

  private def writeFile(path: Path, content: String, executable: Boolean = false): Unit = {
    Files.createDirectories(path.getParent)
    Files.writeString(path, content)
    if (executable) {
      val _ = Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxr-xr-x"))
    }
  }

  private def writeDriver(directory: Path, signal: String): Path = {
    val script  = directory.resolve("driver.sh")
    val record  = directory.resolve("received")
    val started = directory.resolve("started")
    writeFile(
      script,
      s"""#!/bin/sh
         |trap 'kill "$$child" 2>/dev/null; printf %s $signal > '$record'; exit $DriverStatus' $signal
         |sleep 60 &
         |child=$$!
         |printf started > '$started'
         |wait "$$child"
         |""".stripMargin,
      executable = true
    )
    script
  }

  private def cliCommand(arguments: String*): ProcessBuilder = ProcessBuilder(
    (Vector(
      Path.of(System.getProperty("java.home"), "bin", "java").toString,
      "-cp",
      sys.props("gemstone.conformance.classpath"),
      "net.kurobako.gemstone.tools.conformance.CLI"
    ) ++ arguments)*
  )

  private def deleteTree(directory: Path): Unit =
    Using.resource(Files.walk(directory))(_.toList.asScala.toVector).reverse.foreach(Files.deleteIfExists(_))

  private def junitReports(directory: Path): Vector[Path] =
    Using.resource(Files.walk(directory))(_.toList.asScala.toVector).filter(_.getFileName.toString == "junit.xml")

  private def alivePids(pidFile: Path): Vector[Long] = Files
    .readString(pidFile)
    .split(" ")
    .toVector
    .map(_.toLong)
    .filter(pid => ProcessHandle.of(pid).filter(_.isAlive).isPresent)

  private def signalCli(signal: String): (Int, String) = {
    val directory = Files.createTempDirectory("signal-forwarding-")
    try {
      val script  = writeDriver(directory, signal)
      val started = directory.resolve("started")
      val cli     = cliCommand(
        "run",
        "--executable",
        script.toString,
        "--artifacts",
        directory.toString,
        "--output",
        directory.toString,
        "--root",
        directory.toString,
        "--profiles",
        directory.toString
      ).inheritIO().start()
      try {
        val deadline = 30.seconds.fromNow
        while (!Files.exists(started) && cli.isAlive() && deadline.hasTimeLeft()) Thread.sleep(20L)
        assert(Files.exists(started), "the fake driver did not start")
        val _ = ProcessBuilder("kill", "-s", signal, cli.pid().toString).start().waitFor(10L, TimeUnit.SECONDS)
        assert(cli.waitFor(60L, TimeUnit.SECONDS), s"the CLI did not exit after SIG$signal")
        val received = directory.resolve("received")
        (cli.exitValue(), if (Files.exists(received)) Files.readString(received) else "")
      } finally { val _ = cli.destroyForcibly() }
    } finally deleteTree(directory)
  }

  test("sigterm to the CLI reaches the driver, and the CLI returns the driver's status") {
    val (status, received) = signalCli("TERM")
    assertEquals(received, "TERM")
    assertEquals(status, DriverStatus)
  }

  test("sigint to the CLI reaches the driver, and the CLI returns the driver's status") {
    val (status, received) = signalCli("INT")
    assertEquals(received, "INT")
    assertEquals(status, DriverStatus)
  }

  test("a signal received before the driver starts keeps the driver from starting") {
    val relay = new SignalRelay
    relay.handler.handle(Signal("TERM"))
    assertEquals(relay.start(ProcessBuilder("sh", "-c", "exit 0")), Left(Signal("TERM")))
  }

  test("sigterm to the CLI interrupts the host driver and records the case that never started") {
    assume(driver.isDefined, "build the host driver with conformanceHost")
    val executable = driver.get
    val root       = Files.createTempDirectory("interrupted-driver-")
    val output     = root.resolve("output")
    val started    = root.resolve("artifacts/opencl/started")
    val pids       = root.resolve("artifacts/opencl/pids")
    try {
      writeFile(root.resolve("profiles/fakehost.toml"), "[[targets]]\nbackend = \"opencl\"\n")
      writeFile(root.resolve("workloads/short.toml"), "specs = [\"*\"]\n")
      writeFile(root.resolve("artifacts/opencl/artifact.toml"), "backend = \"opencl\"\n")
      writeFile(root.resolve("artifacts/opencl/test-programs.txt"), "hang\t60\thecbench\nquick\t60\thecbench\n")
      writeFile(
        root.resolve("artifacts/opencl/conformance-device"),
        "#!/bin/sh\nprintf '%s' '{\"device\":\"unknown\",\"environment\":{\"CL_DEVICE_INDEX\":\"0\"}}'\n",
        executable = true
      )
      writeFile(
        root.resolve("artifacts/opencl/hang"),
        s"""#!/bin/sh
           |sleep 60 &
           |printf '%s %s' $$$$ $$! > '$pids'
           |printf started > '$started'
           |wait
           |""".stripMargin,
        executable = true
      )
      writeFile(root.resolve("artifacts/opencl/quick"), "#!/bin/sh\nexit 0\n", executable = true)
      val cli = cliCommand(
        "run",
        "--executable",
        executable.toString,
        "--artifacts",
        root.resolve("artifacts").toString,
        "--output",
        output.toString,
        "--root",
        root.toString,
        "--profiles",
        root.resolve("profiles").toString
      )
      cli.environment().putAll(Map("HOSTNAME" -> "fakehost", "CONFORMANCE_JOB_COUNT" -> "1").asJava)
      val process = cli.inheritIO().start()
      try {
        val deadline = 60.seconds.fromNow
        while (!Files.exists(started) && process.isAlive() && deadline.hasTimeLeft()) Thread.sleep(50L)
        assert(Files.exists(started), "the hang program did not start")
        val _ = ProcessBuilder("kill", "-s", "TERM", process.pid().toString).start().waitFor(10L, TimeUnit.SECONDS)
        assert(process.waitFor(60L, TimeUnit.SECONDS), "the CLI did not exit after SIGTERM")
        assertEquals(process.exitValue(), TerminatedStatus)
        assert(
          junitReports(output).exists(report => Files.readString(report).contains("interrupted by signal 15")),
          "junit.xml does not record the case that never started"
        )
        assertEquals(alivePids(pids), Vector.empty[Long])
      } finally {
        process.descendants().forEach(_.destroyForcibly(): Unit)
        val _ = process.destroyForcibly()
      }
    } finally deleteTree(root)
  }
}
