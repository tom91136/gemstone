package net.kurobako.gemstone.server.recording

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import net.kurobako.gemstone.server.protocol.*

/** Capture consumed wire bytes above buffering, as the connection consumes each frame. Allocated only when enabled. */
class Recording(writer: Journal.Writer, source: InputStream, destination: OutputStream)
    extends AutoCloseable {
  private class FrameBuffer extends ByteArrayOutputStream {
    override def write(value: Int): Unit = {
      if (count >= writer.header.limits.maxFrameBytes.raw) throw IOException("recording frame limit exceeded")
      super.write(value)
    }
    override def write(bytes: Array[Byte], offset: Int, size: Int): Unit = {
      if (size > writer.header.limits.maxFrameBytes.raw - count) throw IOException("recording frame limit exceeded")
      super.write(bytes, offset, size)
    }
    def take(): Array[Byte] = { val result = toByteArray; reset(); result }
  }
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var capturing = true

  def unrecorded[A](body: => A): A = {
    capturing = false
    try body
    finally capturing = true
  }

  private val incoming = FrameBuffer()
  private val outgoing = FrameBuffer()
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var connection: Option[Journal.Connection] = None
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var requestAccepted = false
  private class CapturedInput extends InputStream {
    override def read(): Int = {
      val value = source.read()
      if (capturing && value >= 0) incoming.write(value)
      value
    }
    override def read(bytes: Array[Byte], offset: Int, size: Int): Int = {
      val count = source.read(bytes, offset, size)
      if (capturing && count > 0) incoming.write(bytes, offset, count)
      count
    }
    override def close(): Unit = source.close()
  }
  private class CapturedOutput extends OutputStream {
    override def write(value: Int): Unit = { destination.write(value); if (capturing) outgoing.write(value) }
    override def write(bytes: Array[Byte], offset: Int, size: Int): Unit =
      if (size > 0) { destination.write(bytes, offset, size); if (capturing) outgoing.write(bytes, offset, size) }
    override def flush(): Unit = destination.flush()
    override def close(): Unit = destination.close()
  }
  val input: InputStream               = CapturedInput()
  val output: OutputStream             = CapturedOutput()
  def begin(): Unit                    = { incoming.reset(); outgoing.reset(); requestAccepted = false }
  def accepted(client: ClientId): Unit = {
    if (connection.isEmpty) connection = Some(writer.connection(client))
    connection.foreach(_.request(incoming.take()))
    requestAccepted = true
  }
  def response(): Unit              = if (requestAccepted) connection.foreach(_.response(outgoing.take()))
  def failed(message: String): Unit =
    if (requestAccepted || incoming.size() > 0) writer.markUnreplayable(message)
  override def close(): Unit = connection.foreach(_.close())
}
