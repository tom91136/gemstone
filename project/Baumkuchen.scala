import java.io.File
import java.nio.file.Files
import java.nio.file.Path

// mirrors the condition in plugins.sbt, whose definitions this build cannot see
object Baumkuchen {
  val enabled: Boolean =
    Files.isDirectory(Path.of(sys.props("user.home"), ".ivy2/local/net.kurobako/sbt-baumkuchen_sbt2_3"))

  // without baumkuchen its scalafix rules do not run, so scalafix reports their suppressions as unused
  def scalafixConfig(root: Path): Option[File] = Option.unless(enabled) {
    val config = root.resolve("target/scalafix-without-baumkuchen.conf")
    val _      = Files.createDirectories(config.getParent)
    val _      = Files.writeString(
      config,
      Files.readString(root.resolve(".scalafix.conf")) + "\nlint.info = [UnusedScalafixSuppression]\n"
    )
    config.toFile
  }
}
