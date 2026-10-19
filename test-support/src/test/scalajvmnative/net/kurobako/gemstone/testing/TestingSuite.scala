package net.kurobako.gemstone.testing

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.duration.*

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
}
