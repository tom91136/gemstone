package net.kurobako.gemstone.tools.conformance

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermissions
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*
import scala.util.Using

class SignalForwardingSuite extends munit.FunSuite {
  private val DriverStatus = 7

  private def writeDriver(directory: Path, signal: String): Path = {
    val script  = directory.resolve("driver.sh")
    val record  = directory.resolve("received")
    val started = directory.resolve("started")
    Files.writeString(
      script,
      s"""#!/bin/sh
         |trap 'kill "$$child" 2>/dev/null; printf %s $signal > '$record'; exit $DriverStatus' $signal
         |sleep 60 &
         |child=$$!
         |printf started > '$started'
         |wait "$$child"
         |""".stripMargin
    )
    Files.setPosixFilePermissions(script, PosixFilePermissions.fromString("rwxr-xr-x"))
    script
  }

  private def signalCli(signal: String): (Int, String) = {
    val directory = Files.createTempDirectory("signal-forwarding-")
    try {
      val script  = writeDriver(directory, signal)
      val started = directory.resolve("started")
      val cli     = ProcessBuilder(
        Path.of(System.getProperty("java.home"), "bin", "java").toString,
        "-cp",
        sys.props("gemstone.conformance.classpath"),
        "net.kurobako.gemstone.tools.conformance.CLI",
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
        val _ = ProcessBuilder("kill", "-s", signal, cli.pid().toString).start().waitFor()
        assert(cli.waitFor(60L, TimeUnit.SECONDS), s"the CLI did not exit after SIG$signal")
        val received = directory.resolve("received")
        (cli.exitValue(), if (Files.exists(received)) Files.readString(received) else "")
      } finally { val _ = cli.destroyForcibly() }
    } finally Using.resource(Files.walk(directory))(_.toList.asScala.toVector).reverse.foreach(Files.deleteIfExists(_))
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
}
