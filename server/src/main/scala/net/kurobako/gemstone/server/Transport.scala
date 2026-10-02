package net.kurobako.gemstone.server

import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import scala.util.control.NonFatal

object Transport {
  type Port = Port.Type
  object Port extends Newtype[Int where Between[1, 65535]] {
    extension (value: Port) def toInt: Int = value.raw
  }

  object Endpoint {
    def parse(value: String): Either[String, Endpoint] = Bind.parse(value).flatMap {
      case Bind.Tcp(host, port) => Port.either(port.raw).map(Tcp(host, _))
      case Bind.Unix(path)      => Right(Unix(path))
    }
  }
  enum Endpoint {
    case Tcp(host: String, port: Port)
    case Unix(path: Path)

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    override def toString: String = this match {
      case Tcp(host, port) => URI("tcp", null, host, port.toInt, null, null, null).toASCIIString
      case Unix(path)      => unixURI(path)
    }
  }

  object Bind {
    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def parse(value: String): Either[String, Bind] = try {
      val uri = URI(value)
      require(
        uri.getRawQuery == null && uri.getRawFragment == null && uri.getRawUserInfo == null,
        "endpoint must not contain credentials, a query or a fragment"
      )
      uri.getScheme match {
        case "tcp" =>
          require(
            Option(uri.getHost).exists(_.nonEmpty) && Option(uri.getRawPath).forall(_.isEmpty),
            "TCP endpoint requires a host and port"
          )
          Right(Tcp(uri.getHost, uri.getPort.unsafe[Int where Between[0, 65535]]))
        case "unix" =>
          require(Option(uri.getRawAuthority).forall(_.isEmpty), "Unix endpoint must not contain a host")
          val path = Path.of(Option(uri.getPath).getOrElse(""))
          require(path.isAbsolute && !path.toString.contains('\u0000'), "Unix endpoint requires an absolute path")
          Right(Unix(path))
        case _ => Left("endpoint scheme must be tcp or unix")
      }
    } catch { case NonFatal(error) => Left(s"invalid endpoint '$value': ${throwables.detail(error)}") }
  }
  enum Bind {
    case Tcp(host: String = "127.0.0.1", port: Int where Between[0, 65535] = 0)
    case Unix(path: Path)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def unixURI(path: Path): String = URI("unix", "", path.toAbsolutePath.toString, null).toASCIIString

  /** Streams carry canonical RPC bytes. Closing interrupts pending I/O; zero read timeout means idle. */
  trait Channel extends AutoCloseable {
    def input: InputStream
    def output: OutputStream
    def readTimeout(milliseconds: Int): Unit
    def isClosed: Boolean
  }
  trait Listener extends AutoCloseable {
    def endpoint: Endpoint
    def accept(): Channel
    def isClosed: Boolean
  }

}
