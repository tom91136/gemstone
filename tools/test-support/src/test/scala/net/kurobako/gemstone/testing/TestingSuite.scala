package net.kurobako.gemstone.testing

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.duration.*
import scala.util.Using

class TestingSuite extends munit.FunSuite {
  test("processes keep exit status, both streams, environment changes and working directory") {
    files.temporary { root =>
      val result = process.run(
        Seq("/bin/sh", "-c", "printf '%s:%s:<%s>' \"$SET\" \"$PWD\" \"$HOME\"; printf 'diagnostic' >&2; exit 7"),
        root,
        Map("SET" -> "hello"),
        Set("HOME"),
        5.seconds
      )
      assertEquals((result.exit, result.stdout, result.stderr), (7, s"hello:$root:<>", "diagnostic"))
    }
  }

  test("timeouts terminate the process and NUL arguments are rejected before starting one") {
    files.temporary { root =>
      val result =
        process.run(Seq("/bin/sh", "-c", "printf 'started'; exec sleep 30"), root, Map.empty, Set.empty, 1.second)
      assertEquals((result.exit, result.stdout), (-1, "started"))
      assert(result.stderr.contains("timed out"), result.stderr)
      val _ = intercept[IllegalArgumentException](
        process.run(Seq("/bin/sh", "bad\u0000argument"), root, Map.empty, Set.empty, 5.seconds)
      )
    }
  }

  test("temporary directories are removed with nested files after a failing body") {
    val path    = AtomicReference(Option.empty[Path])
    val failure = IllegalStateException("fixture failed")
    assertEquals(
      intercept[IllegalStateException](files.temporary { root =>
        path.set(Some(root))
        val _ = files.write(root, "nested/example.txt", "value")
        assertEquals(files.snapshot(root), Map("nested/example.txt" -> "value"))
        throw failure
      }),
      failure
    )
    assert(path.get().exists(!Files.exists(_)))
  }

  test("successful output is quiet; failure retains both streams and the original exception") {
    val bytes = ByteArrayOutputStream()
    Using.resource(PrintStream(bytes, true, UTF_8)) { stream =>
      Console.withErr(stream) {
        assertEquals(output.onFailure { println("success"); Console.err.println("warning"); 7 }, 7)
        assertEquals(bytes.size(), 0)
        val failure = IllegalStateException("failed")
        assertEquals(
          intercept[IllegalStateException] {
            output.onFailure {
              println("context")
              Console.err.println("diagnostic")
              throw failure
            }
          },
          failure
        )
        assertEquals(bytes.toString(UTF_8).linesIterator.toList, List("context", "diagnostic"))
      }
    }
  }
}
