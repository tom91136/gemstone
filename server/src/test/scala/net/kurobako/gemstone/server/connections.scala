package net.kurobako.gemstone.server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import net.kurobako.gemstone.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.transport.*

private object connections {
  class Client(
      endpoint: Endpoint,
      client: ClientId,
      process: Long = 42L,
      incarnation: Long = 17L
  ) extends AutoCloseable {
    private val socket = transport.connect(endpoint, 10000)
    private val input  = DataInputStream(socket.input)
    private val output = DataOutputStream(socket.output)

    protocol.hello(input, output)

    def call[A](operation: Operation[A]): Either[ExecutionFailure, A] = {
      val (operationTag, payload) = Wire.Protocol[Operation].encodePayload(operation, ExecutionRPC.Limits)
        .fold(message => throw IllegalArgumentException(message), value => value)
      output.writeInt(ExecutionRPC.RequestMagic)
      output.writeLong(client.toLong)
      output.writeLong(process)
      output.writeLong(incarnation)
      val tag = operationTag.getBytes(java.nio.charset.StandardCharsets.UTF_8)
      output.writeInt(tag.length)
      output.writeInt(payload.length)
      output.write(tag)
      output.write(payload)
      output.flush()

      if (input.readInt() != ExecutionRPC.ResponseMagic) throw IOException("unexpected execution response magic")
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

    def finish(): Unit = Control.finish(input, output)

    override def close(): Unit = socket.close()
  }

}
