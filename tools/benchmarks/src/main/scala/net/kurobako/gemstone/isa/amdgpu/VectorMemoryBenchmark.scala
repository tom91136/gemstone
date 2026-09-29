package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.immutable.ArraySeq

/** Warmed vector-load allocation probe using owned snapshots, matching ExecutionSession read ownership. */
object VectorMemoryBenchmark {
  private val input  = AllocationId(1)
  private val output = AllocationId(2)
  private class Memory extends vm.Memory with BenchmarkAddressSpace {
    val source: Array[Byte]      = Array.tabulate[Byte](1024)(i => (i * 17).toByte)
    val destination: Array[Byte] = Array.ofDim[Byte](1024)
    override protected val regionSizes: Iterable[(AllocationId, Long)] =
      Seq(input -> source.length.toLong, output -> destination.length.toLong)
    def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, ArraySeq[Byte]] =
      Right(Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(
        source,
        offset.raw.toInt,
        offset.raw.toInt + size.raw
      )))
    def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] = {
      val _ = bytes.copyToArray(destination, offset.raw.toInt, bytes.size)
      Right(())
    }
  }
  // Benchmark repetitions intentionally use an allocation-free counter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(args: Array[String]): Unit = {
    val iterations =
      BenchmarkArguments.iterations(BenchmarkName("amdgpu-vector-memory"), args.toSeq, 2000)
    val code = Vec[Long](0xc00a0100L, 0x0L, 0xbf8cc07fL, 0x24000084L, 0xdc5c8000L, 0x1040000L, 0xbf8c0f70L, 0xdc7c8000L,
      0x60100L, 0xbf810000L).flatMap(w => LittleEndian.toVec(w, 4))
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
      "kernel",
      Dimensions(64, 1, 1),
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(input, 0L),
        KernelTask.Argument.Memory(output, 0L)
      )
    ).fold(error => throw IllegalStateException(error), value => value)
    val memory                = Memory()
    def run(n: Int > 0): Unit = {
      var i = 0
      while (i < n.raw) {
        VMInterpreter.execute(
          kernel,
          dispatch,
          memory,
          TaskControl(),
          Workgroups.Sequential,
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
    } yield end - start
    if (!memory.source.sameElements(memory.destination))
      throw IllegalStateException("vector-memory copy mismatch")
    Console.out.println(
      s"iterations=$iterations allocated=${allocated.fold(-1L)(identity)} " +
        s"bytes_per_dispatch=${allocated.fold(Double.NaN)(_.raw.toDouble / iterations.raw)} " +
        s"checksum=${memory.destination.map(_.toLong).sum}"
    )
  }
}
