import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import scala.concurrent.duration.FiniteDuration
import scala.sys.process.Process
import sjsonnew.shaded.scalajson.ast.unsafe.JField
import sjsonnew.shaded.scalajson.ast.unsafe.JObject
import sjsonnew.shaded.scalajson.ast.unsafe.JString
import sjsonnew.support.scalajson.unsafe.Parser

object Processes {

  /** A producer can still be writing the file; incomplete JSON is retried by the caller. */
  def jsonString(path: Path, field: String): Option[String] =
    if (!Files.isRegularFile(path)) None
    else Parser.parseFromString(Files.readString(path, StandardCharsets.UTF_8)).toOption.flatMap {
      case JObject(fields) => fields.collectFirst { case JField(`field`, JString(value)) => value }
      case _               => None
    }

  def awaitJsonString(path: Path, field: String, process: Process, timeout: FiniteDuration): Option[String] = {
    val deadline = System.nanoTime() + timeout.toNanos
    var result   = Option.empty[String]
    while (result.isEmpty && process.isAlive() && System.nanoTime() < deadline) {
      result = jsonString(path, field)
      if (result.isEmpty) Thread.sleep(25L)
    }
    result
  }
}
