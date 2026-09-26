package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PositiveInt, Vec}
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

/** Warmed vector-load allocation probe using owned snapshots, matching ExecutionService read ownership. */
object VectorMemoryBenchmark {
  private val input  = MemoryRegion(1)
  private val output = MemoryRegion(2)
  private final class Memory extends net.kurobako.gemstone.vm.Memory with BenchmarkAddressSpace {
    val source      = Array.tabulate[Byte](1024)(i => (i * 17).toByte)
    val destination = Array.ofDim[Byte](1024)
    override protected val regionSizes: Iterable[(MemoryRegion, Long)] =
      Seq(input -> source.length.toLong, output -> destination.length.toLong)
    def read(id: MemoryRegion, offset: NonNegativeLong, size: NonNegativeInt) =
      Right(Vec.unsafeFromArray(java.util.Arrays.copyOfRange(
        source,
        offset.raw.toInt,
        offset.raw.toInt + size.raw
      )))
    def write(id: MemoryRegion, offset: NonNegativeLong, bytes: Vec[Byte]) = {
      val _ = bytes.copyToArray(destination, offset.raw.toInt, bytes.size)
      Right(())
    }
  }
  // Benchmark repetitions intentionally use an allocation-free counter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(args: Array[String]): Unit = {
    val iterations =
      BenchmarkArguments.iterations(BenchmarkName("amdgpu-vector-memory"), args.toSeq, PositiveInt(2000))
    val code = Vec[Long](0xc00a0100L, 0x0L, 0xbf8cc07fL, 0x24000084L, 0xdc5c8000L, 0x1040000L, 0xbf8c0f70L, 0xdc7c8000L,
      0x60100L, 0xbf810000L).flatMap(w => LittleEndian.toVec(w, ScalarByteWidth(4)))
    val kernel = VMCompiler.compile(CodeObjectKernel(
      "kernel",
      Target("gfx900"),
      16,
      0,
      0,
      0x80,
      0x08,
      code
    )).fold(error => throw IllegalStateException(error.message), value => value)
    val dispatch = KernelTask.validated(
      KernelName("kernel"),
      Dimensions(PositiveInt(64), PositiveInt.One, PositiveInt.One),
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(input, NonNegativeLong.Zero),
        KernelTask.Argument.Memory(output, NonNegativeLong.Zero)
      )
    ).fold(error => throw IllegalStateException(error), value => value)
    val memory                    = Memory()
    def run(n: PositiveInt): Unit = {
      var i = 0
      while (i < n.raw) {
        VMInterpreter.execute(
          kernel,
          dispatch,
          memory,
          memory,
          TaskControl.default,
          Scheduler.Sequential,
          Vec.empty
        ).fold(
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
    } yield (end - start).raw
    if (!memory.source.sameElements(memory.destination))
      throw IllegalStateException("vector-memory copy mismatch")
    Console.out.println(
      s"iterations=$iterations allocated=${allocated.fold(-1L)(identity)} " +
        s"bytes_per_dispatch=${allocated.fold(Double.NaN)(_.toDouble / iterations.raw)} " +
        s"checksum=${memory.destination.map(_.toLong).sum}"
    )
  }
}
