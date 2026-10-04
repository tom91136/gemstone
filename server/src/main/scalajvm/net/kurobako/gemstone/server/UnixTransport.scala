package net.kurobako.gemstone.server

import java.io.InputStream
import java.io.InterruptedIOException
import java.io.OutputStream
import java.net.SocketTimeoutException
import java.net.StandardProtocolFamily
import java.net.UnixDomainSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.CancelledKeyException
import java.nio.channels.ClosedChannelException
import java.nio.channels.ClosedSelectorException
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.server.Transport.*
import scala.util.control.NonFatal

private object UnixTransport {
  def connect(path: Path, timeoutMillis: Int): Channel = {
    val socket  = SocketChannel.open(StandardProtocolFamily.UNIX)
    val channel = UnixChannel(socket, timeoutMillis)
    try {
      socket.configureBlocking(false)
      if (!socket.connect(UnixDomainSocketAddress.of(path))) channel.connect(timeoutMillis)
      channel
    } catch {
      case NonFatal(error) =>
        try channel.close()
        catch { case NonFatal(cleanup) => error.addSuppressed(cleanup) }
        throw error
    }
  }

  def listen(path: Path): Listener = {
    val socket = ServerSocketChannel.open(StandardProtocolFamily.UNIX)
    try {
      socket.bind(UnixDomainSocketAddress.of(path))
      UnixListener(socket, path)
    } catch { case NonFatal(error) => socket.close(); throw error }
  }

  private class UnixListener(socket: ServerSocketChannel, path: Path) extends Listener {
    val endpoint: Endpoint = Endpoint.Unix(path)
    def isClosed: Boolean  = !socket.isOpen
    def accept(): Channel  = {
      val accepted = socket.accept()
      try { accepted.configureBlocking(false); UnixChannel(accepted, 300000) }
      catch { case NonFatal(error) => accepted.close(); throw error }
    }
    def close(): Unit = socket.close()
  }

  private class UnixChannel(socket: SocketChannel, writeTimeout: Int) extends Channel {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    @volatile private var timeout = writeTimeout
    private val readable          = Readiness()
    private val writable          = Readiness()

    def isClosed: Boolean                    = !socket.isOpen
    def readTimeout(milliseconds: Int): Unit = { require(milliseconds >= 0); timeout = milliseconds }
    def close(): Unit = throwables.closeAll(Vec(() => socket.close(), () => readable.close(), () => writable.close()))

    // Read and write wait independently; close never needs either operation lock.
    private class Readiness {
      val operationLock: ReentrantLock = ReentrantLock()
      private val lifecycleLock        = ReentrantLock()
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      private var registration = Option.empty[(Selector, SelectionKey)]

      private def registered(operation: Int): (Selector, SelectionKey) = lifecycleLock.withLock {
        if (!socket.isOpen) throw ClosedChannelException()
        registration match {
          case Some(value) => value
          case None        =>
            val selector = Selector.open()
            try {
              val value = selector -> socket.register(selector, operation)
              registration = Some(value)
              value
            } catch {
              case NonFatal(error) =>
                try selector.close()
                catch { case NonFatal(cleanup) => error.addSuppressed(cleanup) }
                throw error
            }
        }
      }

      @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
      def await(operation: Int, milliseconds: Int, deadline: Long): Unit = try {
        val (selector, key) = registered(operation)
        key.interestOps(operation)
        while (socket.isOpen) {
          if (Thread.currentThread().isInterrupted) throw InterruptedIOException("Unix socket wait interrupted")
          val selected = if (milliseconds == 0) selector.select()
          else {
            val remaining = deadline - platform.monotonicNanos
            if (remaining <= 0L) throw SocketTimeoutException("Unix socket timed out")
            selector.select((remaining + 999999L) / 1000000L)
          }
          selector.selectedKeys().clear()
          if (!socket.isOpen) throw ClosedChannelException()
          if (selected > 0) return
        }
        throw ClosedChannelException()
      } catch {
        case _: ClosedSelectorException | _: CancelledKeyException if !socket.isOpen => throw ClosedChannelException()
      }

      def close(): Unit = lifecycleLock.withLock {
        registration.foreach { case (selector, _) => selector.close() }
        registration = None
      }
    }

    def connect(milliseconds: Int): Unit = writable.operationLock.withLock {
      val deadline = platform.monotonicNanos + milliseconds.toLong * 1000000L
      while (!socket.finishConnect()) writable.await(SelectionKey.OP_CONNECT, milliseconds, deadline)
    }

    private class ChannelInput extends InputStream {
      override def read(): Int = {
        val byte = Array.ofDim[Byte](1)
        if (read(byte, 0, 1) < 0) -1 else byte(0) & 0xff
      }
      override def read(bytes: Array[Byte], offset: Int, length: Int): Int = readable.operationLock.withLock {
        java.util.Objects.checkFromIndexSize(offset, length, bytes.length)
        if (length == 0) 0
        else {
          val milliseconds = timeout
          val deadline     = platform.monotonicNanos + milliseconds.toLong * 1000000L
          val buffer       = ByteBuffer.wrap(bytes, offset, length)
          @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
          var count = socket.read(buffer)
          while (count == 0) {
            readable.await(SelectionKey.OP_READ, milliseconds, deadline); count = socket.read(buffer)
          }
          count
        }
      }
      override def close(): Unit = UnixChannel.this.close()
    }
    private class ChannelOutput extends OutputStream {
      override def write(value: Int): Unit                                   = write(Array(value.toByte), 0, 1)
      override def write(bytes: Array[Byte], offset: Int, length: Int): Unit = writable.operationLock.withLock {
        val deadline = platform.monotonicNanos + writeTimeout.toLong * 1000000L
        val buffer   = ByteBuffer.wrap(bytes, offset, length)
        while (buffer.hasRemaining)
          if (socket.write(buffer) == 0) writable.await(SelectionKey.OP_WRITE, writeTimeout, deadline)
      }
      override def close(): Unit = UnixChannel.this.close()
    }
    val input: InputStream   = ChannelInput()
    val output: OutputStream = ChannelOutput()
  }
}
