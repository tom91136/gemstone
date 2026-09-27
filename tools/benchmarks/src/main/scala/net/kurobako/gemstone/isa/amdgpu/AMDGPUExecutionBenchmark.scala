package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.binary.Widths.*

import CodeObject.*
import net.kurobako.gemstone.{PlatformTime, Vec}
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.Constraint.NonEmptyString
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
  private def dimensions(x: Positive[Int]): Dimensions = Dimensions(x, 1, 1)

  private case class Options(
      measured: Positive[Int],
      warmup: Option[NonNegative[Int]],
      groups: Positive[Int]
  )
  private val options = (
    Opts.option[Int]("measured", help = "Measured dispatches").withDefault(200)
      .validate("measured dispatches must be positive")(_ > 0).map(Positive.unsafe),
    Opts.option[Int]("warmup", help = "Warmup dispatches").validate("warmup dispatches must be non-negative")(_ >= 0)
      .map(NonNegative.unsafe).orNone,
    Opts.option[Int]("groups", help = "Workgroups per dispatch").withDefault(1)
      .validate("workgroup count must be between 1 and 65536")(value => value >= 1 && value <= 65536)
      .map(Positive.unsafe)
  ).mapN(Options(_, _, _))
  private val InputId  = MemoryRegion(1L)
  private val OutputId = MemoryRegion(2L)
  private val UnusedId = MemoryRegion(3L)

  // Warmup and measured dispatches share an allocation-free counter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed                  = BenchmarkArguments.parse(BenchmarkName("amdgpu-execution"), arguments.toSeq, options)
    val measured                = parsed.measured
    val warmup                  = parsed.warmup.getOrElse(NonNegative.unsafe(math.max(20, measured.raw / 10)))
    val groups                  = parsed.groups
    val elements: Positive[Int] = Positive.unsafe(groups.raw * 64)

    val words = Vec[Long](
      0xc0020242L, 0x00000004L, 0xc0020283L, 0x00000018L,
      0xc00a0003L, 0x00000000L, 0xbea10080L, 0xbf8cc07fL,
      0x8604ff09L, 0x0000ffffL, 0x92080408L, 0x810a080aL,
      0x6800000aL, 0x2202009fL, 0xd28f0000L, 0x00020082L,
      0x7e060201L, 0x32040000L, 0x38060303L, 0xdc508000L,
      0x027f0002L, 0x7e060203L, 0x32000002L, 0x38020303L,
      0xbf8c0f70L, 0xdc708000L, 0x007f0200L, 0xbf810000L
    )
    val code     = words.flatMap(word => LittleEndian.toVec(word, 4))
    val kernel   = CodeObjectKernel("vector_copy", Target("gfx900"), 80, 0, 0, 0x80, 0x0b, code)
    val prepared =
      VMCompiler.compile(kernel).fold(failure => throw RuntimeException(failure.message), value => value)
    val memory = BenchmarkMemory()
    memory.allocate(InputId, Positive.unsafe(elements.raw * 4))
    memory.allocate(OutputId, Positive.unsafe(elements.raw * 4))
    memory.allocate(UnusedId, 256)
    Array.tabulate(elements.raw)(index => index * 97 - 11).zipWithIndex.foreach { case (value, index) =>
      memory.putInt(InputId, NonNegative.unsafe(index * 4), value)
    }

    val zero     = KernelTask.Argument.Scalar(Vec.fill(8)(0.toByte))
    val dispatch = KernelTask.validated(
      NonEmptyString("vector_copy"),
      Dimensions(64, 1, 1),
      dimensions(groups),
      Vec(
        KernelTask.Argument.Memory(InputId, 0L),
        KernelTask.Argument.Memory(OutputId, 0L),
        KernelTask.Argument.Memory(UnusedId, 0L),
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
          NonNegative.unsafe(math.max(0L, PlatformTime.monotonicNanos - started)),
          PlatformMetrics.allocatedBytes
        )
      }
    val checksum = Array.tabulate(elements.raw) { index =>
      val observed = memory.getInt(OutputId, NonNegative.unsafe(index * 4))
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

    def allocate(id: MemoryRegion, size: Positive[Int]): Unit = {
      allocations(id) = Array.ofDim[Byte](size.raw)
      regionSizes(id) = size.raw.toLong
    }

    def putInt(id: MemoryRegion, offset: NonNegative[Int], value: Int): Unit =
      LittleEndian.write(value.toLong, allocations(id), offset.raw, 4)

    def getInt(id: MemoryRegion, offset: NonNegative[Int]): Int =
      LittleEndian.decodeInt32(allocations(id), offset.raw)

    override def read(
        id: MemoryRegion,
        offset: NonNegative[Long],
        size: NonNegative[Int]
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
        offset: NonNegative[Long],
        size: IntBetween[1, 8]
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
        offset: NonNegative[Long],
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
        offset: NonNegative[Long],
        value: Long,
        size: IntBetween[1, 8]
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
