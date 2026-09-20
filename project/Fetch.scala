import com.google.common.hash.Hashing
import com.google.common.io.MoreFiles
import java.nio.file.Path
import scala.concurrent.duration.*
import scala.language.implicitConversions
import sbt.io.IO
import sttp.client4.*
import sttp.model.Uri

import Paths.{given, *}

object Fetch {
  final case class Request(
      id: String,
      fileName: String,
      url: String,
      expectedSha256: String
  )

  final class Client(maximumDownloadBytes: Long) extends AutoCloseable {
    require(maximumDownloadBytes > 0, "maximum download size must be positive")
    private val backend = DefaultSyncBackend()

    def cached(cacheDirectory: Path, request: Request, offline: Boolean, log: String => Unit): Path = {
      require(
        request.id.nonEmpty && !request.id.contains('\u0000'),
        "artifact ID must be nonempty and cannot contain NUL"
      )
      require(
        request.fileName.nonEmpty && request.fileName != "." && request.fileName != ".." &&
          !request.fileName.contains('/') && !request.fileName.contains('\\') && !request.fileName.contains('\u0000'),
        s"artifact file name must be a single safe path segment: ${request.fileName}"
      )
      require(request.url.startsWith("https://"), s"${request.id}: artifact URL must use HTTPS: ${request.url}")
      require(
        request.expectedSha256.matches("[0-9a-f]{64}"),
        s"${request.id}: expected SHA-256 must be 64 lowercase hexadecimal characters"
      )
      val cache = cacheDirectory.absolute
      IO.createDirectory(cache)
      val target = cache / request.fileName
      if (target.isFile && sha256(target) == request.expectedSha256) {
        log(s"using cached ${request.id} artifact $target")
        target
      } else {
        require(!offline, s"${request.id}: valid artifact is absent from offline cache $target")

        log(s"downloading ${request.id} ${request.url}")
        IO.writeFileAtomically(target) { staging =>
          val stagingPath = staging.toPath
          val uri         = Uri.parse(request.url)
            .fold(message => throw IllegalArgumentException(message), identity)
          val response = basicRequest
            .get(uri)
            .header("User-Agent", ChromeUserAgent)
            .readTimeout(30.minutes)
            .maxResponseBodyLength(maximumDownloadBytes)
            .response(asPathAlways(stagingPath))
            .send(backend)
          require(response.code.isSuccess, s"${request.id}: HTTP ${response.code.code} from ${request.url}")
          val actual = sha256(stagingPath)
          require(
            actual == request.expectedSha256,
            s"${request.id}: expected ${request.expectedSha256}, downloaded $actual"
          )
        }
        target
      }
    }

    override def close(): Unit = backend.close()
  }

  private val ChromeUserAgent: String =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
      "(KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36"

  def sha256(path: Path): String =
    s"${MoreFiles.asByteSource(path).hash(Hashing.sha256())}"

  def sha256(bytes: Array[Byte]): String =
    s"${Hashing.sha256().hashBytes(bytes)}"
}
