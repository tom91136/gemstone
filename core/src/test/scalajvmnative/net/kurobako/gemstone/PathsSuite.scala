package net.kurobako.gemstone

class PathsSuite extends munit.FunSuite {
  test("paths reject NUL on the JVM and Scala Native") {
    assertEquals(paths.parse("NAME", "a\u0000b"), Left("NAME must not contain a NUL character"))
    assertEquals(paths.parse("NAME", "dir/file"), Right(java.nio.file.Path.of("dir/file")))
  }
}
