package net.kurobako.gemstone.server

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.SocketTimeoutException
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.server.Transport.*
import scala.scalanative.unsafe.*

private object UnixTransport {
  @extern
  private object Bindings {
    @blocking
    @name("gemstone_unix_open") def open(path: CString, listener: CInt, timeout: CInt): CInt = extern
    @name("gemstone_unix_dup") def duplicate(fd: CInt): CInt                                 = extern
    @name("gemstone_unix_close") def close(fd: CInt): Unit                                   = extern
    @name("gemstone_unix_shutdown") def shutdown(fd: CInt): Unit                             = extern
    @blocking
    @name("gemstone_unix_accept") def accept(fd: CInt): CInt = extern
    @blocking
    @name("gemstone_unix_read") def read(fd: CInt, bytes: Ptr[Byte], size: CInt, timeout: CInt): CInt = extern
    @blocking
    @name("gemstone_unix_write") def write(fd: CInt, bytes: Ptr[Byte], size: CInt, timeout: CInt): CInt = extern
    @name("gemstone_unix_timeout") def timedOut(result: CInt): CInt                                     = extern
    @name("gemstone_unix_error") def error(result: CInt): CString                                       = extern
  }

  private def checked(result: Int): Int =
    if (result >= 0) result
    else if (Bindings.timedOut(result) != 0) throw SocketTimeoutException("Unix socket timed out")
    else throw IOException(fromCString(Bindings.error(result)))

  private class Descriptor(fd: Int) extends AutoCloseable {
    private val lock = ReentrantLock()
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    @volatile private var closed = false
    def isClosed: Boolean        = closed
    def use[A](f: Int => A): A   = {
      val copy = lock.withLock {
        if (closed) throw IOException("Unix socket closed")
        checked(Bindings.duplicate(fd))
      }
      try f(copy)
      finally Bindings.close(copy)
    }
    def close(): Unit = lock.withLock {
      if (!closed) { closed = true; Bindings.shutdown(fd) }
    }
  }

  private def open(path: Path, listener: Boolean, timeout: Int): Descriptor = Zone {
    Descriptor(checked(Bindings.open(toCString(path.toString), if (listener) 1 else 0, timeout)))
  }
  def connect(path: Path, timeout: Int > 0): Channel = UnixChannel(open(path, false, timeout.raw), timeout)
  def listen(path: Path): Listener                   = UnixSocketListener(open(path, true, 0), path)
  private class UnixSocketListener(fd: Descriptor, path: Path) extends Listener {
    val endpoint: Endpoint = Endpoint.Unix(path)
    def isClosed: Boolean  = fd.isClosed
    def close(): Unit      = fd.close()
    def accept(): Channel  = {
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var result = -1
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var waiting = true
      while (waiting) {
        result = fd.use(Bindings.accept(_))
        waiting = Bindings.timedOut(result) != 0
      }
      UnixChannel(Descriptor(checked(result)), WriteTimeoutMillis)
    }
  }

  private class UnixChannel(fd: Descriptor, writeTimeout: Int > 0) extends Channel {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    @volatile private var timeout                 = writeTimeout.raw
    def isClosed: Boolean                         = fd.isClosed
    def close(): Unit                             = fd.close()
    def readTimeout(milliseconds: Int >= 0): Unit = timeout = milliseconds.raw
    private class ChannelInput extends InputStream {
      override def read(): Int = {
        val byte = Array.ofDim[Byte](1)
        if (read(byte, 0, 1) < 0) -1 else byte(0) & 0xff
      }
      override def read(bytes: Array[Byte], offset: Int, length: Int): Int = {
        java.util.Objects.checkFromIndexSize(offset, length, bytes.length)
        if (length == 0) 0
        else {
          val count = fd.use(socket => checked(Bindings.read(socket, bytes.at(offset), length, timeout)))
          if (count == 0) -1 else count
        }
      }
      override def close(): Unit = fd.close()
    }
    private class ChannelOutput extends OutputStream {
      override def write(value: Int): Unit                                   = write(Array(value.toByte), 0, 1)
      override def write(bytes: Array[Byte], offset: Int, length: Int): Unit = {
        java.util.Objects.checkFromIndexSize(offset, length, bytes.length)
        if (length > 0) fd.use { socket =>
          @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
          var written = 0
          while (written < length) {
            val count = checked(Bindings.write(socket, bytes.at(offset + written), length - written, writeTimeout.raw))
            if (count == 0) throw IOException("Unix socket write made no progress")
            written += count
          }
        }
      }
      override def close(): Unit = fd.close()
    }
    val input: InputStream   = ChannelInput()
    val output: OutputStream = ChannelOutput()

  }
}
