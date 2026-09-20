package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path
import scala.util.Using

final class GeneratedFilesSuite extends munit.FunSuite {
  test("generated writes replace complete files and preserve current timestamps") {
    given Using.Releasable[Path] = path => { val _ = Files.deleteIfExists(path); () }
    Using.resource(Files.createTempDirectory("gemstone-generated-files.")) { directory =>
      Using.resource(directory.resolve("Instruction.scala")) { output =>
        assert(GeneratedFiles.write(output, "first\n"))
        val timestamp = Files.getLastModifiedTime(output)
        assert(!GeneratedFiles.write(output, "first\n"))
        assertEquals(Files.getLastModifiedTime(output), timestamp)
        assert(GeneratedFiles.write(output, "second\n"))
        assertEquals(Files.readString(output), "second\n")
        Using.resource(Files.list(directory)) { entries =>
          assertEquals(entries.toArray.map(_.toString).toSeq, Seq(output.toString))
        }
      }
    }
  }
}
