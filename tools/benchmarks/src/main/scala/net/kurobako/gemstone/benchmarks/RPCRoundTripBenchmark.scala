package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Opts
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.Transport.*
import scala.util.Using

/** Persistent RPC round trips; server startup and connection setup are outside the measurement. */
object RPCRoundTripBenchmark {
  private val options = (
    Opts.option[String]("transport", help = "Transport: tcp or uds").withDefault("tcp")
      .validate("transport must be tcp or uds")(value => value == "tcp" || value == "uds"),
    Opts.option[Int]("iterations", help = "Verified ping round trips").withDefault(10000)
      .mapValidated(value => value.refine[Int > 0].toValidatedNel)
  ).tupled

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): BenchmarkSample = {
    val (transport, iterations) = BenchmarkArguments.parse(BenchmarkName("rpc-roundtrip"), arguments.toSeq, options)
    val directory               = Files.createTempDirectory("gemstone-rpc-benchmark-")
    try Using.Manager { use =>
        val bind    = if (transport == "uds") Bind.Unix(directory.resolve("rpc.sock")) else Bind.Tcp()
        val server  = use(Server.start(Server.Settings(parallelism = 1, bind = bind)))
        val channel = use(SocketTransport.connect(server.endpoint, 10000))
        val input   = DataInputStream(channel.input)
        val output  = DataOutputStream(channel.output)
        protocol.hello(input, output)
        val operation       = ExecutionRPC.Operation.Ping
        val (name, payload) = Wire.Protocol[ExecutionRPC.Operation].encodePayload(operation, ExecutionRPC.Limits)
          .fold(error => throw IllegalArgumentException(error), identity)
        val tag     = name.getBytes(StandardCharsets.UTF_8)
        val request = ByteBuffer.allocate(ExecutionRPC.RequestHeaderBytes.raw + tag.length + payload.length)
          .putInt(ExecutionRPC.RequestMagic).putLong(server.client.toLong)
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
        Measurement.sample(iterations.toLong) {
          var index = 0
          while (index < iterations.raw) {
            exchange()
            index += 1
          }
        }
      }.get
    finally Files.delete(directory)
  }
}
