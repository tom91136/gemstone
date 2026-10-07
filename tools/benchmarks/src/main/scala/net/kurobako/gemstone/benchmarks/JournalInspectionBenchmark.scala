package net.kurobako.gemstone.benchmarks

import com.monovore.decline.Opts
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.protocol.*
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

  val benchmark: Case = Opts.argument[String]("journal").map(Path.of(_)).map(file => () => run(file))

  private def run(file: Path): BenchmarkSample = {
    Using.resource(Journal.Writer(file, Journal.Header(Journal.ExecutionProfile(1000L, 1)))) { writer =>
      Using.resource(writer.connection(ClientId(1L))) { connection =>
        val request = execution("WriteAllocation", Array.fill[Byte](256 * 1024)(17))
        for (_ <- 0 until 200) {
          connection.request(request)
          connection.response(response())
        }
      }
    }
    val measurement = Measurement(Journal.inspect(file))
    val summary     = measurement.value
    if (summary.events.raw != 401 || summary.frames.raw != 400 || !summary.replayable)
      throw IllegalStateException(s"unexpected journal summary: $summary")
    measurement.sample(1L)
  }
}
