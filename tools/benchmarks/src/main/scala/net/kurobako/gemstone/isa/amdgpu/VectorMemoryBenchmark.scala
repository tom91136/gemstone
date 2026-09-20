package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.execution.*

/** Warmed vector-load allocation probe using owned snapshots, matching ExecutionService read ownership. */
object VectorMemoryBenchmark {
  private val input  = AllocationId(1)
  private val output = AllocationId(2)
  private final class Memory extends ExecutionMemory {
    val source      = Array.tabulate[Byte](1024)(i => (i * 17).toByte)
    val destination = Array.ofDim[Byte](1024)
    def read(id: AllocationId, offset: Long :| Positive0, size: Int :| Positive0) =
      Right(Vec.unsafeFromArray(java.util.Arrays.copyOfRange(source, offset.toInt, offset.toInt + size)))
    def write(id: AllocationId, offset: Long :| Positive0, bytes: Vec[Byte]) = {
      val _ = bytes.copyToArray(destination, offset.toInt, bytes.size)
      Right(())
    }
    def recordPointerRelocation(value: PointerRelocation): Unit = ()
  }
  // Benchmark repetitions intentionally use an allocation-free counter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(args: Array[String]): Unit = {
    val iterations = BenchmarkArguments.iterations(BenchmarkName("amdgpu-vector-memory"), args.toSeq, 2000)
    val code = Vec[Long](0xc00a0100L, 0x0L, 0xbf8cc07fL, 0x24000084L, 0xdc5c8000L, 0x1040000L, 0xbf8c0f70L, 0xdc7c8000L,
      0x60100L, 0xbf810000L).flatMap(w => LittleEndian.toVec(w, ScalarByteWidth(4)))
    val kernel = DispatchInterpreter.prepare(CodeObjectKernel(
      "kernel",
      Target("gfx900"),
      16,
      0,
      0,
      0x80,
      0x08,
      code
    )).fold(error => throw IllegalStateException(error.message), value => value)
    val dispatch = KernelDispatch.validated(
      ProgramId(1),
      KernelName("kernel"),
      Dimensions(64, 1, 1),
      Dimensions.One,
      Vec(KernelArgument.Allocation(input, 0), KernelArgument.Allocation(output, 0))
    ).fold(error => throw IllegalStateException(error), value => value)
    val memory                        = Memory()
    def run(n: Int :| Positive): Unit = {
      var i = 0
      while (i < n) {
        DispatchInterpreter.execute(kernel, dispatch, memory, ExecutionControl.default).fold(
          e => throw IllegalStateException(e.toString),
          value => value
        )
        i += 1
      }
    }
    run(iterations)
    val before = PlatformMetrics.allocatedBytes
    run(iterations)
    val allocated = for {
      start <- before
      end   <- PlatformMetrics.allocatedBytes
      if end >= start
    } yield (end - start).refineUnsafe[Positive0]
    if (!memory.source.sameElements(memory.destination))
      throw IllegalStateException("vector-memory copy mismatch")
    Console.out.println(
      s"iterations=$iterations allocated=${allocated.getOrElse(-1L)} " +
        s"bytes_per_dispatch=${allocated.fold(Double.NaN)(_.toDouble / iterations)} " +
        s"checksum=${memory.destination.map(_.toLong).sum}"
    )
  }
}
