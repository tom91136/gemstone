package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.Callable
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.ptx.*
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Concurrent module loads and unloads through the same backend used by the server. */
object PTXCacheBenchmark {
  private case class Options(workers: Int > 0, images: Int > 0, iterations: Int > 0, entries: Int > 0)

  val benchmark: Case = (
    positive("workers", "Concurrent clients", 4),
    positive("images", "Distinct images shared by clients", 8),
    positive("iterations", "Load/unload rounds per client", 10),
    positive("entries", "Kernel entries per image", 512)
  ).mapN(Options(_, _, _, _)).map(options => () => run(options))

  private def run(parsed: Options): BenchmarkSample = {
    val body   = (1 to 32).map(index => s"add.u32 %r1, %r0, $index;").mkString("\n")
    val source = ".version 8.0\n.target sm_80\n.address_size 64\n" +
      (0 until parsed.entries).map(index =>
        s".visible .entry kernel_$index() { .reg .b32 %r<2>; mov.u32 %r0, %tid.x; $body ret; }\n"
      ).mkString
    // Separate byte arrays model independent RPC requests, including equal-content images.
    val images = Vector.tabulate(parsed.workers)(index =>
      Vec.from(s"$source\n// image ${index % parsed.images}\n".getBytes(StandardCharsets.UTF_8))
    )
    val backend  = PTXBackend()
    val barrier  = CyclicBarrier(parsed.workers)
    val executor = Executors.newFixedThreadPool(parsed.workers)
    try {
      val measured = Measurement {
        val tasks = images.map { image =>
          (
              () =>
                Measurement {
                  (0 until parsed.iterations).foldLeft(0L) { (total, _) =>
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
          ): Callable[Measurement[Long]]
        }
        executor.invokeAll(tasks.asJava, 120, TimeUnit.SECONDS).asScala.map(_.get()).toVector
      }
      val operations: Long > 0 = parsed.workers.widen[Long > 0] * parsed.iterations.widen[Long > 0]
      val allocated            = measured.value.map(_.allocatedBytes)
      val bytes                = Option.when(allocated.forall(_.nonEmpty))(
        allocated.flatten.foldLeft(0L: Long >= 0)(_ + _)
      )
      measured.value.foreach(result => require(result.value == parsed.iterations.toLong * parsed.entries))
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
