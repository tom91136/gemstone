package net.kurobako.gemstone.server

import java.io.InputStream
import java.io.OutputStream
import java.net.SocketTimeoutException
import java.net.StandardProtocolFamily
import java.net.UnixDomainSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.server.transport.*
import scala.util.control.NonFatal

private object UnixTransport {
  def connect(path: Path, timeoutMillis: Int): Channel = {
    val socket = SocketChannel.open(StandardProtocolFamily.UNIX)
    try {
      socket.configureBlocking(false)
      val channel = UnixChannel(socket, timeoutMillis)
      if (!socket.connect(UnixDomainSocketAddress.of(path))) channel.connect(timeoutMillis)
      channel
    } catch { case NonFatal(error) => socket.close(); throw error }
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
    @volatile private var timeout            = writeTimeout
    def isClosed: Boolean                    = !socket.isOpen
    def readTimeout(milliseconds: Int): Unit = { require(milliseconds >= 0); timeout = milliseconds }
    def close(): Unit                        = socket.close()

    // Short bounded selector waits also observe close without retaining a selector per idle connection.
    private def ready(operation: Int, milliseconds: Int): Unit = {
      val selector = Selector.open()
      try {
        socket.register(selector, operation)
        val deadline = platform.monotonicNanos + milliseconds.toLong * 1000000L
        while (socket.isOpen && selector.select(100L) == 0)
          if (milliseconds > 0 && platform.monotonicNanos >= deadline)
            throw SocketTimeoutException("Unix socket timed out")
        if (!socket.isOpen) throw java.nio.channels.ClosedChannelException()
      } finally selector.close()
    }

    def connect(milliseconds: Int): Unit = {
      ready(SelectionKey.OP_CONNECT, milliseconds)
      if (!socket.finishConnect()) throw SocketTimeoutException("Unix socket connection timed out")
    }

    private class ChannelInput extends InputStream {
      override def read(): Int = {
        val byte = Array.ofDim[Byte](1)
        if (read(byte, 0, 1) < 0) -1 else byte(0) & 0xff
      }
      override def read(bytes: Array[Byte], offset: Int, length: Int): Int = {
        java.util.Objects.checkFromIndexSize(offset, length, bytes.length)
        if (length == 0) 0
        else {
          val buffer = ByteBuffer.wrap(bytes, offset, length)
          @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
          var count = socket.read(buffer)
          while (count == 0) { ready(SelectionKey.OP_READ, timeout); count = socket.read(buffer) }
          count
        }
      }
      override def close(): Unit = UnixChannel.this.close()
    }
    private class ChannelOutput extends OutputStream {
      override def write(value: Int): Unit                                   = write(Array(value.toByte), 0, 1)
      override def write(bytes: Array[Byte], offset: Int, length: Int): Unit = {
        val buffer = ByteBuffer.wrap(bytes, offset, length)
        while (buffer.hasRemaining)
          if (socket.write(buffer) == 0) ready(SelectionKey.OP_WRITE, writeTimeout)
      }
      override def close(): Unit = UnixChannel.this.close()
    }
    val input: InputStream   = ChannelInput()
    val output: OutputStream = ChannelOutput()

  }
}
