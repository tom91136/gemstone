package net.kurobako.gemstone.testing

import scala.concurrent.duration.*

class ProcessSuite extends munit.FunSuite {
  test("process results preserve exit status, both streams, environment changes and working directory") {
    files.temporary { root =>
      val result = process.run(
        Seq("/bin/sh", "-c", "printf '%s:%s' \"$FIXTURE_VALUE\" \"$PWD\"; printf 'diagnostic' >&2; exit 7"),
        root,
        Map("FIXTURE_VALUE" -> "hello"),
        Set.empty,
        5.seconds
      )
      assertEquals(result.exit, 7)
      assertEquals(result.stdout, s"hello:$root")
      assertEquals(result.stderr, "diagnostic")
      assert(result.diagnostic.contains("diagnostic"))
    }
  }

  test("timeout terminates the process and retains diagnostics already written") {
    files.temporary { root =>
      val result = process.run(
        Seq("/bin/sh", "-c", "printf 'started'; exec sleep 30"),
        root,
        Map.empty,
        Set.empty,
        1.second
      )
      assertEquals(result.exit, -1)
      assertEquals(result.stdout, "started")
      assert(result.stderr.contains("timed out"))
    }
  }

  test("empty arguments remain valid while NUL arguments are rejected before starting a process") {
    files.temporary { root =>
      val result = process.run(
        Seq("/bin/sh", "-c", "printf '<%s>' \"$1\"", "fixture", ""),
        root,
        Map.empty,
        Set.empty,
        5.seconds
      )
      assertEquals(result.exit, 0)
      assertEquals(result.stdout, "<>")
      val _ = intercept[IllegalArgumentException] {
        process.run(Seq("/bin/sh", "bad\u0000argument"), root, Map.empty, Set.empty, 5.seconds)
      }
    }
  }

}
