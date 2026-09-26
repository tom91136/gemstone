package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.PlatformTime
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.server.recording.RPCJournal

import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import com.monovore.decline.Opts
import scala.util.Using

/** Repeated identical frames measure inspection work separately from capture and replay. */
object JournalInspectionBenchmark {
  private val WriteAllocation = ExecutionRPC.Schema.operations
    .find(_.request.name == "WriteAllocation")
    .fold(throw IllegalStateException("execution RPC schema has no WriteAllocation operation"))(_.request.id)

  // Request and response header sizes mirror the wire layout in the generated RPC code.
  private val RequestHeaderBytes  = 48
  private val ResponseHeaderBytes = 20

  private def execution(operation: Long, payload: Array[Byte]): Array[Byte] =
    ByteBuffer.allocate(
      RequestHeaderBytes + payload.length
    ).putInt(ExecutionRPC.RequestMagic).putLong(ExecutionRPC.Schema.fingerprint)
      .putLong(23L).putLong(42L).putLong(17L)
      .putLong(operation).putInt(payload.length).put(payload).array()
  private def response(): Array[Byte] =
    ByteBuffer.allocate(ResponseHeaderBytes).putInt(ExecutionRPC.ResponseMagic).putLong(ExecutionRPC.Schema.fingerprint)
      .putInt(0).putInt(0).array()

  def run(arguments: Array[String]): Unit = {
    val file = BenchmarkArguments.parse(
      BenchmarkName("journal-inspection"),
      arguments.toSeq,
      Opts.argument[String]("journal").map(Path.of(_))
    )
    val protocolIdentity = RPCJournal.ProtocolIdentity.either(ExecutionRPC.Identity.raw)
      .fold(error => throw IllegalArgumentException(error), value => value)
    Using.resource(RPCJournal.Writer(file, RPCJournal.Header(protocolIdentity))) { writer =>
      Using.resource(writer.connection(Object())) { connection =>
        val request = execution(WriteAllocation, Array.fill[Byte](256 * 1024)(17))
        for (_ <- 0 until 200) {
          connection.request(request)
          connection.response(response())
        }
      }
    }
    for (_ <- 0 until 2) RPCJournal.inspect(file)
    for (sample <- 0 until 5) {
      val allocated = PlatformMetrics.allocatedBytes
      val started   = PlatformTime.monotonicNanos
      val summary   = RPCJournal.inspect(file)
      val nanos     = PlatformTime.monotonicNanos - started
      val bytes     = for {
        start <- allocated
        end   <- PlatformMetrics.allocatedBytes
        if end >= start
      } yield end - start
      if (summary.events.raw != 401 || summary.frames.raw != 400 || !summary.replayable)
        throw IllegalStateException(s"unexpected journal summary: $summary")
      Console.out.println(
        s"INSPECT sample=$sample nanos=$nanos allocated=${bytes.fold(-1L)(_.raw)} " +
          s"events=${summary.events} frames=${summary.frames} archive=${Files.size(file)}"
      )
    }
  }
}
