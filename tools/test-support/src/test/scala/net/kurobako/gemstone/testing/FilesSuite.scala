package net.kurobako.gemstone.testing

import java.nio.file.Files
import java.util.concurrent.atomic.AtomicReference

class FilesSuite extends munit.FunSuite {
  test("temporary directories own nested files and clean them after a failing body") {
    val path    = AtomicReference[Option[java.nio.file.Path]](None)
    val failure = IllegalStateException("fixture failed")
    assertEquals(
      intercept[IllegalStateException] {
        files.temporary { root =>
          path.set(Some(root))
          val _ = files.write(root, "nested/example.txt", "value")
          assertEquals(files.snapshot(root), Map("nested/example.txt" -> "value"))
          throw failure
        }
      },
      failure
    )
    assert(!Files.exists(path.get().get))
  }
}
