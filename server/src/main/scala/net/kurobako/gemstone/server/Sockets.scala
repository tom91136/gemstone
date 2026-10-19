package net.kurobako.gemstone.server

import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.atomic.AtomicBoolean
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.server.Transport.*
import scala.util.control.NonFatal

/** Opens TCP and Unix socket channels for endpoints. */
object Sockets {

  /** Socket adapter also keeps TCP options out of the RPC connection loop. */
  private class TcpChannel(socket: Socket) extends Channel {
    socket.setTcpNoDelay(true)
    def input: InputStream                        = socket.getInputStream
    def output: OutputStream                      = socket.getOutputStream
    def readTimeout(milliseconds: Int >= 0): Unit = socket.setSoTimeout(milliseconds)
    def isClosed: Boolean                         = socket.isClosed
    def close(): Unit                             = socket.close()
  }

  def connect(endpoint: Endpoint, timeoutMillis: Int > 0): Channel = endpoint match {
    case Endpoint.Tcp(host, port) =>
      val socket = Socket()
      try {
        socket.connect(InetSocketAddress(host, port.raw), timeoutMillis)
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
        socket.bind(InetSocketAddress(host, port))
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
          java.util.Objects.equals(
            Files.readAttributes(path, classOf[BasicFileAttributes], LinkOption.NOFOLLOW_LINKS).fileKey(),
            owner
          )
        )
          Files.deleteIfExists(path): Unit
    }
  }
}
