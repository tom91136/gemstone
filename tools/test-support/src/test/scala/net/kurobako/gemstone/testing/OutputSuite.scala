package net.kurobako.gemstone.testing

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets.UTF_8
import scala.util.Using

class OutputSuite extends munit.FunSuite {
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
