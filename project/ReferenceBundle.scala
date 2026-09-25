import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import sbt.io.IO
import scala.util.Using
import scala.util.control.NonFatal

import Paths.{*, given}

/** An immutable, digest-pinned reference bundle assembled from cached artifacts. */
case class ReferenceBundle(
    name: String,
    artifacts: Vector[Fetch.Request],
    populate: (Map[String, Path], Path) => Unit,
    verify: Path => Unit
) {
  def prepare(cacheDirectory: Path, outputRoot: Path, offline: Boolean, log: String => Unit): Path = {
    require(name.matches("[a-z0-9][a-z0-9-]*"), s"invalid reference bundle name: $name")
    require(artifacts.nonEmpty, s"reference bundle $name has no artifacts")
    require(artifacts.map(_.id).distinct.size == artifacts.size, s"reference bundle $name has duplicate artifact IDs")

    val manifest =
      (Vector(s"format\t1", s"bundle\t$name") ++ artifacts.map { artifact =>
        s"artifact\t${artifact.id}\t${artifact.fileName}\t${artifact.expectedSha256}\t${artifact.url}"
      }).mkString("", "\n", "\n")
    val fingerprint = Fetch.sha256(manifest.getBytes(StandardCharsets.UTF_8)).take(16)
    val output      = outputRoot.absolute / name / fingerprint

    def check(path: Path): Unit = {
      require(
        IO.read(path / "MANIFEST.tsv", StandardCharsets.UTF_8) == manifest,
        s"reference bundle manifest does not match: $path"
      )
      verify(path)
    }
    def valid(path: Path): Boolean =
      path.isDirectory && {
        try {
          check(path)
          true
        } catch { case NonFatal(_) => false }
      }

    if (valid(output)) {
      log(s"reference bundle $name is prepared at $output")
      output
    } else {
      val cache  = cacheDirectory.absolute
      val cached = Using.resource(Fetch.Client(maximumDownloadBytes = 2147483647L)) { client =>
        val builder = Map.newBuilder[String, Path]
        artifacts.foreach { artifact =>
          builder += artifact.id -> client.cached(cache, artifact, offline, log)
        }
        builder.result()
      }
      val parent = output.parent.getOrElse(throw RuntimeException(s"reference bundle has no parent: $output"))
      IO.createDirectory(parent)
      val staging = Files.createTempDirectory(parent, s"${output.fileName}.")
      try {
        populate(cached, staging)
        IO.write(staging / "MANIFEST.tsv", manifest, StandardCharsets.UTF_8)
        check(staging)
        IO.delete(output)
        require(!output.exists, s"cannot replace invalid reference bundle: $output")
        IO.move(staging, output)
      } finally IO.delete(staging)
      log(s"prepared reference bundle $name at $output")
      output
    }
  }
}
