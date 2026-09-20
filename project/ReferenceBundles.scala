import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import sbt.io.IO
import scala.language.implicitConversions
import scala.util.Using
import scala.util.control.NonFatal

import Paths.{given, *}

/** Prepare immutable, digest-pinned reference bundles from cached artifacts. */
object ReferenceBundles {
  private given deleteTemporaryDirectory: Using.Releasable[Path] with {
    override def release(path: Path): Unit = IO.delete(path)
  }

  final case class Bundle(
      name: String,
      artifacts: Vector[Fetch.Request],
      populate: (Map[String, Path], Path) => Unit,
      verify: Path => Unit
  )

  def prepare(cacheDirectory: Path, outputRoot: Path, bundle: Bundle, offline: Boolean, log: String => Unit): Path = {
    require(bundle.name.matches("[a-z0-9][a-z0-9-]*"), s"invalid reference bundle name: ${bundle.name}")
    require(bundle.artifacts.nonEmpty, s"reference bundle ${bundle.name} has no artifacts")
    require(
      bundle.artifacts.map(_.id).distinct.size == bundle.artifacts.size,
      s"reference bundle ${bundle.name} has duplicate artifact IDs"
    )
    val manifest                 = referenceManifest(bundle)
    val fingerprint              = Fetch.sha256(manifest.getBytes(StandardCharsets.UTF_8)).take(16)
    val output                   = outputRoot.absolute / bundle.name / fingerprint
    def verify(path: Path): Unit = {
      require(
        IO.read(path / "MANIFEST.tsv", StandardCharsets.UTF_8) == manifest,
        s"reference bundle manifest does not match: $path"
      )
      bundle.verify(path)
    }
    if (valid(output, verify)) {
      log(s"reference bundle ${bundle.name} is prepared at $output")
      output
    } else {
      val cache  = cacheDirectory.absolute
      val cached = Using.resource(Fetch.Client(maximumDownloadBytes = 2147483647L)) { client =>
        val builder = Map.newBuilder[String, Path]
        bundle.artifacts.foreach { artifact =>
          builder += artifact.id -> client.cached(cache, artifact, offline, log)
        }
        builder.result()
      }
      publishDirectory(output)(
        stage => {
          bundle.populate(cached, stage)
          IO.write(stage / "MANIFEST.tsv", manifest, StandardCharsets.UTF_8)
        },
        verify
      )
      log(s"prepared reference bundle ${bundle.name} at $output")
      output
    }
  }

  private def referenceManifest(bundle: Bundle): String =
    (Vector(s"format\t1", s"bundle\t${bundle.name}") ++ bundle.artifacts.map { artifact =>
      s"artifact\t${artifact.id}\t${artifact.fileName}\t${artifact.expectedSha256}\t${artifact.url}"
    }).mkString("", "\n", "\n")

  private def valid(path: Path, verify: Path => Unit): Boolean =
    path.isDirectory && {
      try {
        verify(path)
        true
      } catch { case NonFatal(_) => false }
    }

  private def publishDirectory(output: Path)(populate: Path => Unit, verify: Path => Unit): Unit = {
    val parent = output.parent.getOrElse(throw RuntimeException(s"reference bundle has no parent: $output"))
    IO.createDirectory(parent)
    Using.resource(Files.createTempDirectory(parent, s"${output.fileName}.")) { stage =>
      populate(stage)
      verify(stage)
      IO.delete(output)
      require(!output.exists, s"cannot replace invalid reference bundle: $output")
      IO.move(stage, output)
    }
  }
}
