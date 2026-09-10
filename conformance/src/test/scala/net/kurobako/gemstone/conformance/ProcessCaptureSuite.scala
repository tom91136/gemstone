package net.kurobako.gemstone.conformance

import net.kurobako.gemstone.launcher.Environment
import java.nio.file.{FileAlreadyExistsException, Files, Path}
import java.io.IOException
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

final class ProcessCaptureSuite extends munit.FunSuite {
  private def withReport[A](broken: Boolean)(body: Path => A): A = {
    val directory = Files.createTempDirectory("gemstone-capture-test-")
    if broken then { val _ = Files.writeString(directory.resolve("measurements"), "occupied") }
    try body(directory)
    finally {
      val paths = Files.walk(directory)
      try paths.iterator().asScala.toVector.sortBy(_.getNameCount).reverse.foreach(Files.delete(_))
      finally paths.close()
    }
  }

  test("failed command preserves its exit and stderr when measurements fail") {
    withReport(true) { report =>
      val result = ProcessCapture.run(
        Vector("sh", "-c", "echo original-error >&2; exit 7"),
        report,
        Environment(Map.empty, Set.empty),
        5.seconds,
        Some(report)
      )
      assertEquals(result.exit, 7)
      assert(result.stderr.contains("original-error"))
      assert(result.stderr.contains("measurement/cleanup failed"))
    }
  }

  test("successful command with broken reporting fails as infrastructure") {
    withReport(true) { report =>
      intercept[FileAlreadyExistsException] {
        ProcessCapture.run(
          Vector("sh", "-c", "exit 0"),
          report,
          Environment(Map.empty, Set.empty),
          5.seconds,
          Some(report)
        )
      }
    }
  }

  test("launch exception remains primary when measurements fail") {
    withReport(true) { report =>
      val error = intercept[IOException] {
        ProcessCapture.run(
          Vector("sh", "-c", "exit 0"),
          report.resolve("no-such-directory"),
          Environment(Map.empty, Set.empty),
          5.seconds,
          Some(report)
        )
      }
      assert(error.getMessage.contains("no-such-directory"))
      assertEquals(error.getSuppressed.length, 1)
    }
  }

  test("measured body exception retains identity and secondary reporting error") {
    withReport(true) { report =>
      val primary = IllegalStateException("original body")
      val error   = intercept[IllegalStateException] {
        ProcessCapture.measured("probe", Some(report))(throw primary)
      }
      assert(error eq primary)
      assertEquals(error.getSuppressed.length, 1)
    }
  }

  test("successful measured body cannot hide infrastructure failure") {
    withReport(true) { report =>
      intercept[FileAlreadyExistsException](ProcessCapture.measured("probe", Some(report))(42))
    }
  }

  test("a missing output log preserves the known child failure and available stderr") {
    withReport(false) { report =>
      val script =
        "import pathlib,sys; logs=list(pathlib.Path(sys.argv[1]).glob('processes/*/stdout.log')); assert len(logs)==1; logs[0].unlink(); print('original-error',file=sys.stderr); sys.exit(7)"
      val result = ProcessCapture.run(
        Vector("python3", "-c", script, report.toString),
        report,
        Environment(Map.empty, Set.empty),
        5.seconds,
        Some(report)
      )
      assertEquals(result.exit, 7)
      assert(result.stderr.contains("original-error"))
      assert(result.stderr.contains("measurement/cleanup failed"))
      assert(result.stderr.contains("stdout.log"))
    }
  }

  test("healthy reporting preserves command result and records exit") {
    withReport(false) { report =>
      val result = ProcessCapture.run(
        Vector("sh", "-c", "echo original-error >&2; exit 7"),
        report,
        Environment(Map.empty, Set.empty),
        5.seconds,
        Some(report)
      )
      assertEquals(result.exit, 7)
      assert(!result.stderr.contains("measurement/cleanup failed"))
      val files = Files.list(report.resolve("measurements"))
      try assertEquals(ujson.read(Files.readString(files.findFirst().get()))("exit").num, 7.0)
      finally files.close()
    }
  }
}
