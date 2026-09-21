package net.kurobako.gemstone.server.transport

import Transport.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.{DataInputStream, DataOutputStream, IOException}
import java.net.{InetAddress, InetSocketAddress, Socket}
import scala.util.Using

object ServerControl {
  val Magic          = 0x474d4330
  val RegisterClient = 1
  val QueryUsage     = 2
  val ReleaseClient  = 3
  val Shutdown       = 4

  private val TimeoutMillis: Int :| Positive = 5000

  def register(port: RPCPort, timeoutMillis: Int :| Positive = TimeoutMillis): ClientId =
    exchange(port, timeoutMillis, RegisterClient, None) { input =>
      ClientId.either(input.readLong())
        .fold(
          message => throw IOException(s"Gemstone server returned an invalid client identity: $message"),
          value => value
        )
    }

  def release(port: RPCPort, client: ClientId, timeoutMillis: Int :| Positive = TimeoutMillis): Unit =
    exchange(port, timeoutMillis, ReleaseClient, Some(client)) { input =>
      if (input.readInt() != 0) throw IOException("Gemstone server rejected client release")
    }

  def shutdown(port: RPCPort): Unit =
    exchange(port, TimeoutMillis, Shutdown, None) { input =>
      if (input.readInt() != 0) throw IOException("Gemstone server rejected shutdown")
    }

  private def exchange[A](port: RPCPort, timeoutMillis: Int :| Positive, operation: Int, client: Option[ClientId])(
      read: DataInputStream => A
  ): A =
    Using.resource(Socket()) { socket =>
      socket.connect(InetSocketAddress(InetAddress.getLoopbackAddress, port.value), timeoutMillis)
      socket.setSoTimeout(timeoutMillis)
      val output = DataOutputStream(socket.getOutputStream)
      output.writeInt(Magic)
      output.writeInt(operation)
      client.foreach(value => output.writeLong(value.value))
      output.flush()
      read(DataInputStream(socket.getInputStream))
    }
}
