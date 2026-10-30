package net.kurobako.gemstone

import java.nio.file.Path
import net.kurobako.gemstone.testing.Suite

class PathsSuite extends Suite {
  test("paths reject NUL on the JVM and Scala Native") {
    assertEquals(paths.parse("NAME", "a\u0000b"), Left("NAME must not contain a NUL character"))
    assertEquals(paths.parse("NAME", "dir/file"), Right(Path.of("dir/file")))
  }
}
