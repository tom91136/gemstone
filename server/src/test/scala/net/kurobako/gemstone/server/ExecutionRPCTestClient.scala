package net.kurobako.gemstone.server

import net.kurobako.gemstone.execution.ExecutionFailure
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation
import net.kurobako.gemstone.rpc.Wire
import net.kurobako.gemstone.server.transport.{ClientId, RPCPort}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.{DataInputStream, DataOutputStream, IOException}
import java.net.Socket

private[gemstone] final class ExecutionRPCTestClient(
    port: RPCPort,
    client: ClientId,
    process: Long = 42L,
    incarnation: Long = 17L
) extends AutoCloseable {
  private val socket = Socket("127.0.0.1", port.value)
  socket.setSoTimeout(10000)
  private val input  = DataInputStream(socket.getInputStream)
  private val output = DataOutputStream(socket.getOutputStream)

  def call[A](operation: Operation[A]): Either[ExecutionFailure, A] = {
    val (operationId, payload) = Wire.Protocol[Operation].encodePayload(operation, ExecutionRPC.Limits)
      .fold(message => throw IllegalArgumentException(message), value => value)
    output.writeInt(ExecutionRPC.RequestMagic)
    output.writeLong(ExecutionRPC.Schema.fingerprint)
    output.writeLong(client.value)
    output.writeLong(process)
    output.writeLong(incarnation)
    output.writeLong(operationId)
    output.writeInt(payload.length)
    output.write(payload)
    output.flush()

    if (input.readInt() != ExecutionRPC.ResponseMagic) throw IOException("unexpected execution response magic")
    if (input.readLong() != ExecutionRPC.Schema.fingerprint) throw IOException("unexpected execution response schema")
    val status = input.readInt()
    val body   = input.readNBytes(input.readInt())
    status match {
      case 0 => operation.replyCodec.decode(body, ExecutionRPC.Limits)
          .left.map(message => ExecutionFailure.InvalidRequest(message))
      case 1 => summon[Wire.Codec[ExecutionFailure]].decode(body, ExecutionRPC.Limits)
          .fold(message => Left(ExecutionFailure.InvalidRequest(message)), error => Left(error))
      case value =>
        Left(ExecutionFailure.InvalidRequest(s"invalid execution response status: $value"))
    }
  }

  override def close(): Unit = socket.close()
}
