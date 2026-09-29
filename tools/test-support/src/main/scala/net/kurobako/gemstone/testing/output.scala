package net.kurobako.gemstone.testing

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import scala.util.Using

object output {

  /** CLI diagnostics stay available on failure without printing every successful fixture generation. */
  def onFailure[A](body: => A): A = {
    val bytes = ByteArrayOutputStream()
    try Using.resource(PrintStream(bytes, true, StandardCharsets.UTF_8)) { stream =>
        Console.withOut(stream)(Console.withErr(stream)(body))
      }
    catch {
      case error: Throwable =>
        Console.err.print(bytes.toString(StandardCharsets.UTF_8))
        throw error
    }
  }
}
