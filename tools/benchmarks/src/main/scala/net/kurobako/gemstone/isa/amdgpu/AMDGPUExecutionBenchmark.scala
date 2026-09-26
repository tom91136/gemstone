package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PlatformTime, PositiveInt, Vec}
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.memory.MemoryValueByteWidth
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

import cats.syntax.all.*
import com.monovore.decline.Opts

import java.nio.file.Path
import scala.collection.mutable
import scala.util.Using

/** Short, standalone execution benchmark for profiling the AMDGPU hot path without a GPU or vendor oracle.
  *
  * This deliberately uses the official ROCr vector-copy kernel subset covered by CodeObjectSuite. The benchmark is not
  * a performance assertion: it prints stable per-dispatch measurements suitable for before/after comparisons.
  */
object AMDGPUExecutionBenchmark {
  private def dimensions(x: PositiveInt): Dimensions = Dimensions(x, PositiveInt.One, PositiveInt.One)

  private final case class Options(
      measured: PositiveInt,
      warmup: Option[NonNegativeInt],
      groups: PositiveInt
  )
  private val options = (
    Opts.option[Int]("measured", help = "Measured dispatches").withDefault(200)
      .validate("measured dispatches must be positive")(_ > 0).map(PositiveInt.unsafe),
    Opts.option[Int]("warmup", help = "Warmup dispatches").validate("warmup dispatches must be non-negative")(_ >= 0)
      .map(NonNegativeInt.unsafe).orNone,
    Opts.option[Int]("groups", help = "Workgroups per dispatch").withDefault(1)
      .validate("workgroup count must be between 1 and 65536")(value => value >= 1 && value <= 65536)
      .map(PositiveInt.unsafe)
  ).mapN(Options(_, _, _))
  private val InputId  = MemoryRegion(1L)
  private val OutputId = MemoryRegion(2L)
  private val UnusedId = MemoryRegion(3L)

  // Warmup and measured dispatches share an allocation-free counter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed                = BenchmarkArguments.parse(BenchmarkName("amdgpu-execution"), arguments.toSeq, options)
    val measured              = parsed.measured
    val warmup                = parsed.warmup.getOrElse(NonNegativeInt.unsafe(math.max(20, measured.raw / 10)))
    val groups                = parsed.groups
    val elements: PositiveInt = PositiveInt.unsafe(groups.raw * 64)

    val words = Vec[Long](
      0xc0020242L, 0x00000004L, 0xc0020283L, 0x00000018L,
      0xc00a0003L, 0x00000000L, 0xbea10080L, 0xbf8cc07fL,
      0x8604ff09L, 0x0000ffffL, 0x92080408L, 0x810a080aL,
      0x6800000aL, 0x2202009fL, 0xd28f0000L, 0x00020082L,
      0x7e060201L, 0x32040000L, 0x38060303L, 0xdc508000L,
      0x027f0002L, 0x7e060203L, 0x32000002L, 0x38020303L,
      0xbf8c0f70L, 0xdc708000L, 0x007f0200L, 0xbf810000L
    )
    val code     = words.flatMap(word => LittleEndian.toVec(word, ScalarByteWidth(4)))
    val kernel   = CodeObjectKernel("vector_copy", Target("gfx900"), 80, 0, 0, 0x80, 0x0b, code)
    val prepared =
      VMCompiler.compile(kernel).fold(failure => throw RuntimeException(failure.message), value => value)
    val memory = BenchmarkMemory()
    memory.allocate(InputId, PositiveInt.unsafe(elements.raw * 4))
    memory.allocate(OutputId, PositiveInt.unsafe(elements.raw * 4))
    memory.allocate(UnusedId, PositiveInt(256))
    Array.tabulate(elements.raw)(index => index * 97 - 11).zipWithIndex.foreach { case (value, index) =>
      memory.putInt(InputId, NonNegativeInt.unsafe(index * 4), value)
    }

    val zero     = KernelTask.Argument.Scalar(Vec.fill(8)(0.toByte))
    val dispatch = KernelTask.validated(
      KernelName("vector_copy"),
      Dimensions(PositiveInt(64), PositiveInt.One, PositiveInt.One),
      dimensions(groups),
      Vec(
        KernelTask.Argument.Memory(InputId, NonNegativeLong.Zero),
        KernelTask.Argument.Memory(OutputId, NonNegativeLong.Zero),
        KernelTask.Argument.Memory(UnusedId, NonNegativeLong.Zero),
        zero,
        zero,
        zero,
        zero,
        zero,
        zero,
        zero
      )
    ).fold(message => throw IllegalArgumentException(message), value => value)

    def execute(): Unit = {
      val control = TaskControl.default
      VMInterpreter.execute(
        prepared,
        dispatch,
        memory,
        memory,
        control,
        Scheduler.Sequential,
        Vec.empty
      ).fold(
        failure => throw RuntimeException(failure.message),
        value => value
      )
    }

    var iteration = 0
    while (iteration < warmup.raw) {
      execute()
      iteration += 1
    }

    val (allocatedBefore, elapsed, allocatedAfter) =
      Using.resource(PlatformMetrics.startRecording(sys.env.get("GEMSTONE_BENCHMARK_JFR").map(Path.of(_)))) { _ =>
        val before  = PlatformMetrics.allocatedBytes
        val started = PlatformTime.monotonicNanos
        iteration = 0
        while (iteration < measured.raw) {
          execute()
          iteration += 1
        }
        (
          before,
          NonNegativeLong.unsafe(math.max(0L, PlatformTime.monotonicNanos - started)),
          PlatformMetrics.allocatedBytes
        )
      }
    val checksum = Array.tabulate(elements.raw) { index =>
      val observed = memory.getInt(OutputId, NonNegativeInt.unsafe(index * 4))
      if (observed != index * 97 - 11)
        throw IllegalStateException(s"copy mismatch at $index: $observed")
      observed.toLong
    }.sum

    val nanosPerDispatch = elapsed.raw.toDouble / measured.raw.toDouble
    val bytesPerDispatch = (allocatedBefore, allocatedAfter) match {
      case (Some(before), Some(after)) if after >= before =>
        (after - before).raw.toDouble / measured.raw.toDouble
      case _ => Double.NaN
    }
    Console.out.println(
      f"amdgpu-execution benchmark dispatches=$measured warmup=$warmup groups=$groups " +
        f"elapsed_ms=${elapsed.raw / 1000000.0}%.3f ns_per_dispatch=$nanosPerDispatch%.1f " +
        f"allocated_bytes_per_dispatch=$bytesPerDispatch%.1f checksum=$checksum"
    )
  }

  // Mutable allocation contents are confined to this final benchmark memory fixture.
  private final class BenchmarkMemory extends Memory with BenchmarkAddressSpace {
    private val allocations                                             = mutable.Map.empty[MemoryRegion, Array[Byte]]
    override protected val regionSizes: mutable.Map[MemoryRegion, Long] = mutable.Map.empty

    def allocate(id: MemoryRegion, size: PositiveInt): Unit = {
      allocations(id) = Array.ofDim[Byte](size.raw)
      regionSizes(id) = size.raw.toLong
    }

    def putInt(id: MemoryRegion, offset: NonNegativeInt, value: Int): Unit =
      LittleEndian.write(value.toLong, allocations(id), offset.raw, ScalarByteWidth(4))

    def getInt(id: MemoryRegion, offset: NonNegativeInt): Int =
      LittleEndian.decodeInt32(allocations(id), offset.raw)

    override def read(
        id: MemoryRegion,
        offset: NonNegativeLong,
        size: NonNegativeInt
    ): Either[Fault, Vec[Byte]] =
      allocations.get(id).toRight(Fault.Memory("benchmark memory region")).flatMap { bytes =>
        Either.cond(
          ByteRange.contains(bytes.length, offset.raw, size.raw.toLong),
          Vec.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset.raw.toInt, offset.raw.toInt + size.raw)),
          Fault.InvalidTask("benchmark read range")
        )
      }

    override def readValue(
        id: MemoryRegion,
        offset: NonNegativeLong,
        size: MemoryValueByteWidth
    ): Either[Fault, Long] =
      allocations.get(id) match {
        case None        => Left(Fault.Memory("benchmark memory region"))
        case Some(bytes) =>
          val byteCount = size.raw
          if (ByteRange.contains(bytes.length, offset.raw, byteCount.toLong))
            Right(LittleEndian.decode(bytes, offset.raw.toInt, size.scalarByteWidth))
          else Left(Fault.InvalidTask("benchmark read range"))
      }

    override def write(
        id: MemoryRegion,
        offset: NonNegativeLong,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      allocations.get(id) match {
        case None              => Left(Fault.Memory("benchmark memory region"))
        case Some(destination) =>
          if (ByteRange.contains(destination.length, offset.raw, bytes.length.toLong)) {
            val _ = bytes.copyToArray(destination, offset.raw.toInt, bytes.length)
            Right(())
          } else Left(Fault.InvalidTask("benchmark write range"))
      }

    override def writeValue(
        id: MemoryRegion,
        offset: NonNegativeLong,
        value: Long,
        size: MemoryValueByteWidth
    ): Either[Fault, Unit] =
      allocations.get(id) match {
        case None              => Left(Fault.Memory("benchmark memory region"))
        case Some(destination) =>
          val byteCount = size.raw
          if (ByteRange.contains(destination.length, offset.raw, byteCount.toLong)) {
            LittleEndian.write(value, destination, offset.raw.toInt, size.scalarByteWidth)
            Right(())
          } else Left(Fault.InvalidTask("benchmark write range"))
      }
  }
}
