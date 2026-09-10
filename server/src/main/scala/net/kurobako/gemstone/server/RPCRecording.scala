package net.kurobako.gemstone.server

import java.io.{ByteArrayOutputStream, IOException, InputStream, OutputStream}

/** Capture consumed wire bytes above buffering and below credential pushback. Allocated only when enabled. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private[server] final class RPCRecording(writer: RPCJournal.Writer, source: InputStream, destination: OutputStream)
    extends AutoCloseable {
  private final class Frame extends ByteArrayOutputStream {
    override def write(value: Int): Unit = {
      if (count >= writer.header.limits.maxFrameBytes) throw IOException("recording frame limit exceeded")
      super.write(value)
    }
    override def write(bytes: Array[Byte], offset: Int, size: Int): Unit = {
      if (size > writer.header.limits.maxFrameBytes - count) throw IOException("recording frame limit exceeded")
      super.write(bytes, offset, size)
    }
    def take(): Array[Byte] = { val result = toByteArray; reset(); result }
  }
  private val incoming                                  = Frame()
  private val outgoing                                  = Frame()
  private var connection: Option[RPCJournal.Connection] = None
  private var authenticated                             = false
  private final class CapturedInput extends InputStream {
    override def read(): Int = {
      val value = source.read()
      if (value >= 0) incoming.write(value)
      value
    }
    override def read(bytes: Array[Byte], offset: Int, size: Int): Int = {
      val count = source.read(bytes, offset, size)
      if (count > 0) incoming.write(bytes, offset, count)
      count
    }
    override def close(): Unit = source.close()
  }
  private final class CapturedOutput extends OutputStream {
    override def write(value: Int): Unit = { destination.write(value); outgoing.write(value) }
    override def write(bytes: Array[Byte], offset: Int, size: Int): Unit =
      if (size > 0) { destination.write(bytes, offset, size); outgoing.write(bytes, offset, size) }
    override def flush(): Unit = destination.flush()
    override def close(): Unit = destination.close()
  }
  val input: InputStream            = CapturedInput()
  val output: OutputStream          = CapturedOutput()
  def begin(): Unit                 = { incoming.reset(); outgoing.reset(); authenticated = false }
  def accepted(owner: AnyRef): Unit = {
    if (connection.isEmpty) connection = Some(writer.connection(owner))
    connection.foreach(_.request(incoming.take()))
    authenticated = true
  }
  def response(): Unit              = if (authenticated) connection.foreach(_.response(outgoing.take()))
  def callbackRequest(): Unit       = connection.foreach(_.callbackRequest(outgoing.take()))
  def callbackReply(): Unit         = connection.foreach(_.callbackReply(incoming.take()))
  def failed(message: String): Unit = if (authenticated) writer.markUnreplayable(message)
  override def close(): Unit        = connection.foreach(_.close())
}
