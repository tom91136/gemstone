package net.kurobako.gemstone.codegen

import java.nio.file.Files
import scala.util.Using

class GeneratedFilesSuite extends munit.FunSuite {
  test("generated writes replace complete files and preserve current timestamps") {
    generator.inDirectory { directory =>
      val output = directory.resolve("Instruction.scala")
      GeneratedFiles.write(output, "first\n")
      val timestamp = Files.getLastModifiedTime(output)
      GeneratedFiles.write(output, "first\n")
      assertEquals(Files.getLastModifiedTime(output), timestamp)
      GeneratedFiles.write(output, "second\n")
      assertEquals(Files.readString(output), "second\n")
      Using.resource(Files.list(directory)) { entries =>
        assertEquals(entries.toArray.map(_.toString).toSeq, Seq(output.toString))
      }
    }
  }
}
