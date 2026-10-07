package net.kurobako.gemstone.server

import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.atomic.AtomicBoolean
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import scala.util.control.NonFatal

/** Connection mechanics. RPC framing and identities live in protocol. */
object Transport {
  type Port = Port.Type
  object Port extends Newtype[Int in 1 ~ 65535]

  /** Bounds a blocked Unix socket write to a client that stopped reading. Blocking TCP sockets offer no write bound. */
  val WriteTimeoutMillis: Int > 0 = 300000

  object Endpoint {
    def parse(value: String): Either[String, Endpoint] = Bind.parse(value).flatMap {
      case Bind.Tcp(host, port) => port.raw.refine[Port].map(Tcp(host, _))
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
          configuration.path("Unix endpoint", Option(uri.getPath).getOrElse(""))
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

  /** Socket adapter also keeps TCP options out of the RPC connection loop. */
  private class TcpChannel(socket: Socket) extends Channel {
    socket.setTcpNoDelay(true)
    def input: InputStream                        = socket.getInputStream
    def output: OutputStream                      = socket.getOutputStream
    def readTimeout(milliseconds: Int >= 0): Unit = socket.setSoTimeout(milliseconds.raw)
    def isClosed: Boolean                         = socket.isClosed
    def close(): Unit                             = socket.close()
  }

  def connect(endpoint: Endpoint, timeoutMillis: Int > 0): Channel = endpoint match {
    case Endpoint.Tcp(host, port) =>
      val socket = Socket()
      try {
        socket.connect(InetSocketAddress(host, port.raw), timeoutMillis.raw)
        val channel = TcpChannel(socket)
        channel.readTimeout(timeoutMillis.widen[Int >= 0])
        channel
      } catch {
        case NonFatal(error) => socket.close(); throw error
      }
    case Endpoint.Unix(path) => UnixTransport.connect(path, timeoutMillis)
  }

  def listen(bind: Bind): Listener = bind match {
    case Bind.Tcp(host, port) =>
      val socket = ServerSocket()
      try {
        socket.bind(InetSocketAddress(host, port.raw))
        TcpListener(socket, host)
      } catch { case NonFatal(error) => socket.close(); throw error }
    case Bind.Unix(path) =>
      require(path.isAbsolute, "Unix listener path must be absolute")
      // Never unlink a pre-existing path: it may belong to another running server.
      val listener = UnixTransport.listen(path)
      val owner    = try Files.readAttributes(path, classOf[BasicFileAttributes], LinkOption.NOFOLLOW_LINKS).fileKey()
      catch {
        case NonFatal(error) =>
          try listener.close()
          catch { case NonFatal(cleanup) => error.addSuppressed(cleanup) }
          throw error
      }
      UnixListener(listener, path, owner)
  }

  private class TcpListener(socket: ServerSocket, host: String) extends Listener {
    val endpoint: Endpoint = Endpoint.Tcp(host, socket.getLocalPort.unsafe[Port])
    def isClosed: Boolean  = socket.isClosed
    def accept(): Channel  = {
      val accepted = socket.accept()
      try TcpChannel(accepted)
      catch { case NonFatal(error) => accepted.close(); throw error }
    }
    def close(): Unit = socket.close()
  }

  /** Removes the socket file on close only while it is still the one this listener bound. */
  private class UnixListener(listener: Listener, path: Path, owner: AnyRef) extends Listener {
    private val closed     = AtomicBoolean(false)
    val endpoint: Endpoint = Endpoint.Unix(path)
    def isClosed: Boolean  = listener.isClosed
    def accept(): Channel  = listener.accept()
    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def close(): Unit = if (closed.compareAndSet(false, true)) {
      try listener.close()
      finally
        if (
          owner != null && Files.exists(path, LinkOption.NOFOLLOW_LINKS) &&
          Files.readAttributes(path, classOf[BasicFileAttributes], LinkOption.NOFOLLOW_LINKS).fileKey() == owner
        )
          Files.deleteIfExists(path): Unit
    }
  }
}
