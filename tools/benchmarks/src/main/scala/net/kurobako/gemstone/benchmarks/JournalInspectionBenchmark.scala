package net.kurobako.gemstone.benchmarks

import com.monovore.decline.Opts
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.recording.*
import scala.util.Using

/** Repeated identical frames measure inspection work separately from capture and replay. */
object JournalInspectionBenchmark {
  private def execution(operation: String, payload: Array[Byte]): Array[Byte] = {
    val tag = operation.getBytes(StandardCharsets.UTF_8)
    ByteBuffer.allocate(
      ExecutionRPC.RequestHeaderBytes.raw + tag.length + payload.length
    ).putInt(ExecutionRPC.RequestMagic)
      .putLong(23L).putLong(42L).putLong(17L)
      .putInt(tag.length).putInt(payload.length).put(tag).put(payload).array()
  }
  private def response(): Array[Byte] =
    ByteBuffer.allocate(ExecutionRPC.ResponseHeaderBytes.raw).putInt(ExecutionRPC.ResponseMagic)
      .putInt(0).putInt(0).array()

  def run(arguments: Array[String]): Unit = {
    val file = BenchmarkArguments.parse(
      BenchmarkName("journal-inspection"),
      arguments.toSeq,
      Opts.argument[String]("journal").map(Path.of(_))
    )
    val protocolIdentity = Journal.ProtocolIdentity.either(ExecutionRPC.Identity)
      .fold(error => throw IllegalArgumentException(error), value => value)
    Using.resource(Journal.Writer(file, Journal.Header(protocolIdentity))) { writer =>
      Using.resource(writer.connection(Object())) { connection =>
        val request = execution("WriteAllocation", Array.fill[Byte](256 * 1024)(17))
        for (_ <- 0 until 200) {
          connection.request(request)
          connection.response(response())
        }
      }
    }
    for (_ <- 0 until 2) Journal.inspect(file)
    for (sample <- 0 until 5) {
      val allocated = PlatformMetrics.allocatedBytes
      val started   = platform.monotonicNanos
      val summary   = Journal.inspect(file)
      val nanos     = platform.monotonicNanos - started
      val bytes     = for {
        start <- allocated
        end   <- PlatformMetrics.allocatedBytes
        if end >= start
      } yield end - start
      if (summary.events.raw != 401 || summary.frames.raw != 400 || !summary.replayable)
        throw IllegalStateException(s"unexpected journal summary: $summary")
      Console.out.println(
        s"INSPECT sample=$sample nanos=$nanos allocated=${bytes.fold(-1L)(identity)} " +
          s"events=${summary.events} frames=${summary.frames} archive=${Files.size(file)}"
      )
    }
  }
}
