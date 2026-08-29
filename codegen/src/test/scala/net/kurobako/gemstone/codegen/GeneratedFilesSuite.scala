package net.kurobako.gemstone.codegen

import java.nio.file.Files

final class GeneratedFilesSuite extends munit.FunSuite {
  test("generated writes replace complete files and preserve current timestamps") {
    val directory = Files.createTempDirectory("gemstone-generated-files.")
    val output    = directory.resolve("Instruction.scala")
    try {
      assert(GeneratedFiles.write(output, "first\n"))
      val timestamp = Files.getLastModifiedTime(output)
      assert(!GeneratedFiles.write(output, "first\n"))
      assertEquals(Files.getLastModifiedTime(output), timestamp)
      assert(GeneratedFiles.write(output, "second\n"))
      assertEquals(Files.readString(output), "second\n")
      val entries = Files.list(directory)
      try assertEquals(entries.toArray.map(_.toString).toSeq, Seq(output.toString))
      finally entries.close()
    } finally {
      val _ = Files.deleteIfExists(output)
      val _ = Files.deleteIfExists(directory)
    }
  }
}
