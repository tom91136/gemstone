package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.server.backend.*
import scala.util.Using

/** Concurrent module loads and unloads through the same backend used by the server. */
object PTXCacheBenchmark {
  private case class Options(threads: Int > 0, images: Int > 0, iterations: Int > 0, entries: Int > 0)

  private val name    = BenchmarkName("ptx-cache")
  private val options = (
    Opts.option[Int]("threads", help = "Concurrent clients").withDefault(8)
      .refined[Int > 0]("threads must be positive"),
    Opts.option[Int]("images", help = "Distinct images shared by clients").withDefault(8)
      .refined[Int > 0]("images must be positive"),
    Opts.option[Int]("iterations", help = "Load/unload rounds per client").withDefault(10)
      .refined[Int > 0]("iterations must be positive"),
    Opts.option[Int]("entries", help = "Kernel entries per image").withDefault(512)
      .refined[Int > 0]("entries must be positive")
  ).mapN(Options(_, _, _, _))

  def run(arguments: Array[String]): BenchmarkSample = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val body   = (1 to 32).map(index => s"add.u32 %r1, %r0, $index;").mkString("\n")
    val source = ".version 8.0\n.target sm_80\n.address_size 64\n" +
      (0 until parsed.entries.raw).map(index =>
        s".visible .entry kernel_$index() { .reg .b32 %r<2>; mov.u32 %r0, %tid.x; $body ret; }\n"
      ).mkString
    // Separate byte arrays model independent RPC requests, including equal-content images.
    val images = Vector.tabulate(parsed.threads.raw)(index =>
      Vec.from(s"$source\n// image ${index % parsed.images.raw}\n".getBytes(StandardCharsets.UTF_8))
    )
    val backend  = PTX()
    val barrier  = CyclicBarrier(parsed.threads.raw)
    val executor = Executors.newFixedThreadPool(parsed.threads.raw)
    try {
      val measured = Measurement {
        val clients = images.map { image =>
          executor.submit(() =>
            Measurement {
              (0 until parsed.iterations.raw).foldLeft(0L) { (total, _) =>
                val _ = barrier.await(60, TimeUnit.SECONDS)
                Using.resource(backend.load(image).fold(
                  error => throw IllegalStateException(error.message),
                  identity
                )) {
                  program =>
                    val kernels =
                      program.symbols.fold(error => throw IllegalStateException(error.message), _.kernels.size)
                    // Keep every reference live until all clients have loaded, including identical images.
                    val _ = barrier.await(60, TimeUnit.SECONDS)
                    total + kernels
                }
              }
            }
          )
        }
        clients.map(_.get(120, TimeUnit.SECONDS))
      }
      val operations: Long > 0 = parsed.threads.toLong.widen[Long > 0] * parsed.iterations.toLong.widen[Long > 0]
      val allocated            = measured.value.map(_.allocatedBytes)
      val bytes                = Option.when(allocated.forall(_.nonEmpty))(
        allocated.flatten.foldLeft(0L: Long >= 0)(_ + _)
      )
      measured.value.foreach(result => require(result.value == parsed.iterations.raw.toLong * parsed.entries.raw))
      BenchmarkSample(
        measured.elapsedNanos,
        operations,
        bytes,
        if (bytes.isDefined) BenchmarkSample.AllocationScope.Workers else BenchmarkSample.AllocationScope.Unavailable
      )
    } finally {
      val _ = executor.shutdownNow()
    }
  }
}
