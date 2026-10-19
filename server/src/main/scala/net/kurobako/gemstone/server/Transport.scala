package net.kurobako.gemstone.server

import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import scala.util.control.NonFatal

/** Endpoints and the channels connections run over. Sockets opens them; RPC framing and identities live in protocol. */
object Transport {
  type Port = Port.Type
  object Port extends Newtype[Int in 1 ~ 65535]

  /** Bounds a blocked Unix socket write to a client that stopped reading. Blocking TCP sockets offer no write bound. */
  val WriteTimeoutMillis: Int > 0 = 300000

  object Endpoint {
    def parse(value: String): Either[String, Endpoint] = Bind.parse(value).flatMap {
      case Bind.Tcp(host, port) => port.refine[Port].map(Tcp(host, _))
      case Bind.Unix(path)      => Right(Unix(path))
    }
  }
  enum Endpoint {
    case Tcp(host: String, port: Port)
    case Unix(path: Path)

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    override def toString: String = this match {
      case Tcp(host, port) => URI("tcp", null, host, port.raw, null, null, null).toASCIIString
      case Unix(path)      => URI("unix", "", path.toAbsolutePath.toString, null).toASCIIString
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
          uri.getPort.refine[Int in 0 ~ 65535].map(Tcp(uri.getHost, _))
            .left.map(_ => s"invalid endpoint '$value': TCP endpoint requires a host and port")
        case "unix" =>
          require(Option(uri.getRawAuthority).forall(_.isEmpty), "Unix endpoint must not contain a host")
          paths.parse("Unix endpoint", Option(uri.getPath).getOrElse(""))
            .filterOrElse(_.isAbsolute, "Unix endpoint requires an absolute path")
            .map(Unix(_))
            .left.map(error => s"invalid endpoint '$value': $error")
        case _ => Left("endpoint scheme must be tcp or unix")
      }
    } catch { case NonFatal(error) => Left(s"invalid endpoint '$value': ${throwables.detail(error)}") }
  }
  enum Bind {
    case Tcp(host: String = "127.0.0.1", port: Int in 0 ~ 65535 = 0)
    case Unix(path: Path)
  }

  /** Streams carry canonical RPC bytes. Closing interrupts pending I/O; zero read timeout means idle. */
  trait Channel extends AutoCloseable {
    def input: InputStream
    def output: OutputStream
    def readTimeout(milliseconds: Int >= 0): Unit
    def isClosed: Boolean
  }
  trait Listener extends AutoCloseable {
    def endpoint: Endpoint
    def accept(): Channel
    def isClosed: Boolean
  }
}
