package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.Transport.*
import scala.util.Using

/** Persistent RPC round trips; server startup and connection setup are outside the measurement. */
object RPCRoundTripBenchmark {
  private enum SocketKind {
    case TCP, UDS
  }

  val benchmark: Case = (choice("transport", "tcp" -> SocketKind.TCP, "uds" -> SocketKind.UDS), iterations(10000))
    .mapN((transport, iterations) => () => run(transport, iterations))

  private def run(transport: SocketKind, iterations: Int > 0): BenchmarkSample = {
    val directory = Files.createTempDirectory("gemstone-rpc-benchmark-")
    try Using.Manager { use =>
        val bind = transport match {
          case SocketKind.TCP => Bind.Tcp()
          case SocketKind.UDS => Bind.Unix(directory.resolve("rpc.sock"))
        }
        val server  = use(Server.start(Server.Settings(parallelism = 1, bind = bind)))
        val channel = use(Transport.connect(server.endpoint, 10000))
        val input   = DataInputStream(channel.input)
        val output  = DataOutputStream(channel.output)
        protocol.hello(input, output)
        val operation       = ExecutionRPC.Operation.Ping
        val (name, payload) = Wire.Protocol[ExecutionRPC.Operation].encodePayload(operation, ExecutionRPC.Limits)
          .fold(error => throw IllegalArgumentException(error), identity)
        val tag     = name.getBytes(StandardCharsets.UTF_8)
        val request = ByteBuffer.allocate(ExecutionRPC.RequestHeaderBytes.raw + tag.length + payload.length)
          .putInt(ExecutionRPC.RequestMagic).putLong(server.client.raw)
          .putLong(1L).putLong(1L).putInt(tag.length).putInt(payload.length).put(tag).put(payload).array()
        val reply = operation.replyCodec.encode((), ExecutionRPC.Limits)
          .fold(error => throw IllegalArgumentException(error), identity)
        val expected = ByteBuffer.allocate(ExecutionRPC.ResponseHeaderBytes.raw + reply.length)
          .putInt(ExecutionRPC.ResponseMagic).putInt(0).putInt(reply.length).put(reply).array()
        val response         = Array.ofDim[Byte](expected.length)
        def exchange(): Unit = {
          output.write(request)
          output.flush()
          input.readFully(response)
          if (!java.util.Arrays.equals(response, expected))
            throw IllegalStateException("unexpected RPC ping response")
        }
        exchange()
        Measurement.repeat(iterations)(exchange())
      }.get
    finally Files.delete(directory)
  }
}
