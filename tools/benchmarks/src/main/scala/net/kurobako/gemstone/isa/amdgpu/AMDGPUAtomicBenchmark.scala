package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.binary.Widths.*

import CodeObject.*
import net.kurobako.gemstone.{PlatformTime, Vec}
import net.kurobako.gemstone.benchmarks.{BenchmarkArguments, BenchmarkName, PlatformMetrics}
import net.kurobako.gemstone.binary.{ByteRange, LittleEndian}
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

import java.util.concurrent.locks.ReentrantLock

/** Checked one-lane global atomic-add probe. The exact instruction image is the same fixture used by the AMDGPU atomic
  * conformance unit tests.
  */
object AMDGPUAtomicBenchmark {
  private val allocation = MemoryRegion(1)
  private val image      =
    "000204f4000000fa000308f4080000fa7fc08cbfff02007e78563412ff02027e103254768002047e0c02087e0e020a7e0e020c7e0f020e7e0080c8dc02040800703f8cbf0202047e830404349004044a008074dc02000800000081bf"

  private final class AtomicMemory extends Memory with BenchmarkAddressSpace {
    val bytes                                                          = Array.ofDim[Byte](24)
    private val lock                                                   = ReentrantLock()
    override protected val regionSizes: Iterable[(MemoryRegion, Long)] = Seq(allocation -> bytes.length.toLong)
    LittleEndian.write(7L, bytes, 0, 4)

    override def read(
        region: MemoryRegion,
        offset: NonNegative[Long],
        size: NonNegative[Int]
    ): Either[Fault, Vec[Byte]] =
      Either.cond(
        region == allocation && ByteRange.contains(bytes.length, offset.raw, size.raw.toLong),
        Vec.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset.raw.toInt, offset.raw.toInt + size.raw)),
        Fault.InvalidTask("atomic benchmark read bounds")
      )

    override def write(
        region: MemoryRegion,
        offset: NonNegative[Long],
        value: Vec[Byte]
    ): Either[Fault, Unit] =
      if (region == allocation && ByteRange.contains(bytes.length, offset.raw, value.length.toLong)) {
        val _ = value.copyToArray(bytes, offset.raw.toInt, value.length)
        Right(())
      } else Left(Fault.InvalidTask("atomic benchmark write bounds"))

    override def atomicModifyValue(
        region: MemoryRegion,
        offset: NonNegative[Long],
        size: IntBetween[1, 8]
    )(update: Long => Long): Either[Fault, Long] = lock.withLock {
      if (region == allocation && ByteRange.contains(bytes.length, offset.raw, size.raw.toLong)) {
        val scalarSize = size.scalarByteWidth
        val previous   = LittleEndian.decode(bytes, offset.raw.toInt, scalarSize)
        LittleEndian.write(update(previous), bytes, offset.raw.toInt, scalarSize)
        Right(previous)
      } else Left(Fault.InvalidTask("atomic benchmark update bounds"))
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val iterations =
      BenchmarkArguments.iterations(BenchmarkName("amdgpu-atomic"), arguments.toSeq, 20000)
    val code   = Vec.from(image.grouped(2).map(Integer.parseInt(_, 16).toByte))
    val kernel = VMCompiler.compile(CodeObjectKernel("atomic", Target("gfx1030"), 24, 0, 0, 0x80, 0x08, code))
      .fold(error => throw IllegalStateException(error.message), identity)
    val dispatch = KernelTask.validated(
      NonEmptyString("atomic"),
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(allocation, 0L),
        KernelTask.Argument.Scalar(LittleEndian.toVec(9L, 8)),
        KernelTask.Argument.Scalar(LittleEndian.toVec(7L, 8))
      )
    ).fold(error => throw IllegalArgumentException(error), identity)
    val memory = AtomicMemory()
    val before = PlatformMetrics.allocatedBytes
    val start  = PlatformTime.monotonicNanos
    var index  = 0
    while (index < iterations.raw) {
      VMInterpreter.execute(
        kernel,
        dispatch,
        memory,
        memory,
        TaskControl.default,
        Scheduler.Sequential,
        Vec.empty
      ).fold(error => throw IllegalStateException(error.message), identity)
      index += 1
    }
    val elapsed   = PlatformTime.monotonicNanos - start
    val allocated = for {
      initial <- before
      current <- PlatformMetrics.allocatedBytes
      if current >= initial
    } yield current - initial
    val observed = LittleEndian.decode(memory.bytes, 0, 4)
    val expected = (7L + iterations.raw.toLong * 9L) & 0xffffffffL
    if (observed != expected)
      throw IllegalStateException(s"atomic benchmark result $observed != $expected")
    Console.out.println(
      s"iterations=$iterations nsPerDispatch=${elapsed.toDouble / iterations.raw} " +
        s"bytesPerDispatch=${allocated.fold(Double.NaN)(_.raw.toDouble / iterations.raw)} checksum=$observed"
    )
  }
}
