package net.kurobako.gemstone.server.transport

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.{DataInputStream, DataOutputStream, IOException}
import java.net.{InetAddress, InetSocketAddress, Socket}
import scala.util.Using

private[gemstone] object ServerControl {
  private val TimeoutMillis: Int :| Positive = 5000

  def register(port: RPCPort, timeoutMillis: Int :| Positive = TimeoutMillis): ClientId =
    exchange(port, timeoutMillis, ControlProtocol.RegisterClient, None) { input =>
      ClientId.either(input.readLong())
        .fold(
          message => throw IOException(s"Gemstone server returned an invalid client identity: $message"),
          value => value
        )
    }

  def release(port: RPCPort, client: ClientId, timeoutMillis: Int :| Positive = TimeoutMillis): Unit =
    exchange(port, timeoutMillis, ControlProtocol.ReleaseClient, Some(client)) { input =>
      if (input.readInt() != 0) throw IOException("Gemstone server rejected client release")
    }

  def shutdown(port: RPCPort): Unit =
    exchange(port, TimeoutMillis, ControlProtocol.Shutdown, None) { input =>
      if (input.readInt() != 0) throw IOException("Gemstone server rejected shutdown")
    }

  private def exchange[A](port: RPCPort, timeoutMillis: Int :| Positive, operation: Int, client: Option[ClientId])(
      read: DataInputStream => A
  ): A =
    Using.resource(Socket()) { socket =>
      socket.connect(InetSocketAddress(InetAddress.getLoopbackAddress, port.value), timeoutMillis)
      socket.setSoTimeout(timeoutMillis)
      val output = DataOutputStream(socket.getOutputStream)
      output.writeInt(ControlProtocol.Magic)
      output.writeInt(operation)
      client.foreach(value => output.writeLong(value.value))
      output.flush()
      read(DataInputStream(socket.getInputStream))
    }
}
