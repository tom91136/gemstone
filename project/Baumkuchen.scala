import java.nio.file.Files
import java.nio.file.Path

// mirrors the condition in plugins.sbt, whose definitions this build cannot see
object Baumkuchen {
  val enabled: Boolean =
    Files.isDirectory(Path.of(sys.props("user.home"), ".ivy2/local/net.kurobako/sbt-baumkuchen_sbt2_3"))
}
