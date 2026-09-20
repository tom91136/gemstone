package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*

import net.kurobako.gemstone.{PlatformTime, Vec}
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.MemoryValueByteWidth

import cats.syntax.all.*
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.file.Path
import scala.collection.mutable
import scala.util.Using

private[amdgpu] def benchmarkDimensions(x: Int :| Positive): Dimensions = Dimensions(x, 1, 1)

/** Short, standalone execution benchmark for profiling the AMDGPU hot path without a GPU or vendor oracle.
  *
  * This deliberately uses the official ROCr vector-copy kernel subset covered by CodeObjectSuite. The benchmark is not
  * a performance assertion: it prints stable per-dispatch measurements suitable for before/after comparisons.
  */
object AMDGPUExecutionBenchmark {
  private final case class Options(
      measured: Int :| Positive,
      warmup: Option[Int :| Positive0],
      groups: Int :| Interval.Closed[1, 65536]
  )
  private val options = (
    Opts.option[Int]("measured", help = "Measured dispatches").withDefault(200)
      .validate("measured dispatches must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("warmup", help = "Warmup dispatches").validate("warmup dispatches must be non-negative")(_ >= 0)
      .map(_.refineUnsafe[Positive0]).orNone,
    Opts.option[Int]("groups", help = "Workgroups per dispatch").withDefault(1)
      .validate("workgroup count must be between 1 and 65536")(value => value >= 1 && value <= 65536)
      .map(_.refineUnsafe[Interval.Closed[1, 65536]])
  ).mapN(Options(_, _, _))
  private val InputId  = AllocationId(1L)
  private val OutputId = AllocationId(2L)
  private val UnusedId = AllocationId(3L)

  // Warmup and measured dispatches share an allocation-free counter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed   = BenchmarkArguments.parse(BenchmarkName("amdgpu-execution"), arguments.toSeq, options)
    val measured = parsed.measured
    val warmup   = parsed.warmup.getOrElse(math.max(20, measured / 10).refineUnsafe[Positive0])
    val groups   = parsed.groups
    val elements: Int :| Positive = (groups * 64).refineUnsafe[Positive]

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
      DispatchInterpreter.prepare(kernel).fold(failure => throw RuntimeException(failure.message), value => value)
    val memory = BenchmarkMemory()
    memory.allocate(InputId, (elements * 4).refineUnsafe[Positive])
    memory.allocate(OutputId, (elements * 4).refineUnsafe[Positive])
    memory.allocate(UnusedId, 256)
    Array.tabulate(elements)(index => index * 97 - 11).zipWithIndex.foreach { case (value, index) =>
      memory.putInt(InputId, (index * 4).refineUnsafe[Positive0], value)
    }

    val zero     = KernelArgument.Scalar(Vec.fill(8)(0.toByte))
    val dispatch = KernelDispatch.validated(
      ProgramId(1L),
      KernelName("vector_copy"),
      Dimensions(64, 1, 1),
      benchmarkDimensions(groups.refineUnsafe[Positive]),
      Vec(
        KernelArgument.Allocation(InputId, 0L),
        KernelArgument.Allocation(OutputId, 0L),
        KernelArgument.Allocation(UnusedId, 0L),
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
      val control = ExecutionControl()
      DispatchInterpreter.execute(prepared, dispatch, memory, control).fold(
        failure => throw RuntimeException(failure.message),
        value => value
      )
    }

    var iteration = 0
    while (iteration < warmup) {
      execute()
      iteration += 1
    }

    val (allocatedBefore, elapsed, allocatedAfter) =
      Using.resource(PlatformMetrics.startRecording(sys.env.get("GEMSTONE_BENCHMARK_JFR").map(Path.of(_)))) { _ =>
        val before  = PlatformMetrics.allocatedBytes
        val started = PlatformTime.monotonicNanos
        iteration = 0
        while (iteration < measured) {
          execute()
          iteration += 1
        }
        (
          before,
          math.max(0L, PlatformTime.monotonicNanos - started).refineUnsafe[Positive0],
          PlatformMetrics.allocatedBytes
        )
      }
    val checksum = Array.tabulate(elements) { index =>
      val observed = memory.getInt(OutputId, (index * 4).refineUnsafe[Positive0])
      if (observed != index * 97 - 11)
        throw IllegalStateException(s"copy mismatch at $index: $observed")
      observed.toLong
    }.sum

    val nanosPerDispatch = elapsed.toDouble / measured.toDouble
    val bytesPerDispatch = (allocatedBefore, allocatedAfter) match {
      case (Some(before), Some(after)) if after >= before => (after - before).toDouble / measured.toDouble
      case _                                              => Double.NaN
    }
    Console.out.println(
      f"amdgpu-execution benchmark dispatches=$measured warmup=$warmup groups=$groups " +
        f"elapsed_ms=${elapsed / 1000000.0}%.3f ns_per_dispatch=$nanosPerDispatch%.1f " +
        f"allocated_bytes_per_dispatch=$bytesPerDispatch%.1f checksum=$checksum"
    )
  }

  // Mutable allocation contents are confined to this final benchmark memory fixture.
  private final class BenchmarkMemory extends ExecutionMemory {
    private val allocations = mutable.Map.empty[AllocationId, Array[Byte]]

    def allocate(id: AllocationId, size: Int :| Positive): Unit = allocations(id) = Array.ofDim[Byte](size)

    def putInt(id: AllocationId, offset: Int :| Positive0, value: Int): Unit =
      LittleEndian.write(value.toLong, allocations(id), offset, ScalarByteWidth(4))

    def getInt(id: AllocationId, offset: Int :| Positive0): Int =
      LittleEndian.decode(allocations(id), offset, ScalarByteWidth(4)).toInt

    override def read(
        id: AllocationId,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[ExecutionFailure, Vec[Byte]] =
      allocations.get(id).toRight(ExecutionFailure.NotFound("benchmark allocation")).flatMap { bytes =>
        Either.cond(
          ByteRange.contains(bytes.length, offset, size.toLong),
          Vec.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size)),
          ExecutionFailure.InvalidRequest("benchmark read range")
        )
      }

    override def readValue(
        id: AllocationId,
        offset: Long :| Positive0,
        size: MemoryValueByteWidth
    ): Either[ExecutionFailure, Long] =
      allocations.get(id) match {
        case None        => Left(ExecutionFailure.NotFound("benchmark allocation"))
        case Some(bytes) =>
          val byteCount = size.value
          if (ByteRange.contains(bytes.length, offset, byteCount.toLong))
            Right(LittleEndian.decode(bytes, offset.toInt, size.scalarByteWidth))
          else Left(ExecutionFailure.InvalidRequest("benchmark read range"))
      }

    override def write(
        id: AllocationId,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] =
      allocations.get(id) match {
        case None              => Left(ExecutionFailure.NotFound("benchmark allocation"))
        case Some(destination) =>
          if (ByteRange.contains(destination.length, offset, bytes.length.toLong)) {
            val _ = bytes.copyToArray(destination, offset.toInt, bytes.length)
            Right(())
          } else Left(ExecutionFailure.InvalidRequest("benchmark write range"))
      }

    override def writeValue(
        id: AllocationId,
        offset: Long :| Positive0,
        value: Long,
        size: MemoryValueByteWidth
    ): Either[ExecutionFailure, Unit] =
      allocations.get(id) match {
        case None              => Left(ExecutionFailure.NotFound("benchmark allocation"))
        case Some(destination) =>
          val byteCount = size.value
          if (ByteRange.contains(destination.length, offset, byteCount.toLong)) {
            LittleEndian.write(value, destination, offset.toInt, size.scalarByteWidth)
            Right(())
          } else Left(ExecutionFailure.InvalidRequest("benchmark write range"))
      }

    override def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
  }
}

/** ALU-heavy companion to [[AMDGPUExecutionBenchmark]], isolating instruction and lane dispatch from guest memory. */
object AMDGPUALUExecutionBenchmark {
  private enum Mode {
    case Move, Binary
  }

  private final case class Options(
      measured: Int :| Positive,
      warmup: Option[Int :| Positive0],
      instructionCount: Int :| Positive0,
      mode: Mode
  )
  private val options = (
    Opts.option[Int]("measured", help = "Measured dispatches").withDefault(50)
      .validate("measured dispatches must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("warmup", help = "Warmup dispatches").validate("warmup dispatches must be non-negative")(_ >= 0)
      .map(_.refineUnsafe[Positive0]).orNone,
    Opts.option[Int]("instructions", help = "ALU instructions per dispatch").withDefault(512)
      .validate("instruction count must be non-negative")(_ >= 0).map(_.refineUnsafe[Positive0]),
    Opts.option[String]("mode", help = "Instruction mode: move or binary").withDefault("move")
      .validate("mode must be move or binary")(value => value == "move" || value == "binary")
      .map(value => if (value == "binary") Mode.Binary else Mode.Move)
  ).mapN(Options(_, _, _, _))

  private object RejectingMemory extends ExecutionMemory {
    override def read(
        id: AllocationId,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[ExecutionFailure, Vec[Byte]] =
      Left(ExecutionFailure.Failed("ALU benchmark unexpectedly read memory"))
    override def write(
        id: AllocationId,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] =
      Left(ExecutionFailure.Failed("ALU benchmark unexpectedly wrote memory"))
    override def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
  }

  // Warmup and measured dispatches share an allocation-free counter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed           = BenchmarkArguments.parse(BenchmarkName("amdgpu-alu"), arguments.toSeq, options)
    val measured         = parsed.measured
    val warmup           = parsed.warmup.getOrElse(math.max(10, measured / 10).refineUnsafe[Positive0])
    val instructionCount = parsed.instructionCount
    val mode             = parsed.mode
    val instruction      = mode match {
      case Mode.Move => 0x7e0002ebL
      // gfx9 VOP2 v_add_f32 v0, v1, v2
      case Mode.Binary => 0x02000501L
    }
    val words    = Vec.from(Iterator.fill(instructionCount)(instruction) ++ Iterator.single(0xbf810000L))
    val code     = words.flatMap(word => LittleEndian.toVec(word, ScalarByteWidth(4)))
    val kernel   = CodeObjectKernel("alu", Target("gfx900"), 0, 0, 0, 0, 0, code)
    val prepared =
      DispatchInterpreter.prepare(kernel).fold(failure => throw RuntimeException(failure.message), value => value)
    val dispatch = KernelDispatch.validated(
      ProgramId(1L),
      KernelName("alu"),
      Dimensions(64, 1, 1),
      Dimensions.One,
      Vec.empty
    ).fold(message => throw IllegalArgumentException(message), value => value)
    def execute(): Unit =
      DispatchInterpreter.execute(prepared, dispatch, RejectingMemory, ExecutionControl()).fold(
        failure => throw RuntimeException(failure.message),
        value => value
      )

    var iteration = 0
    while (iteration < warmup) {
      execute()
      iteration += 1
    }
    val (allocatedBefore, elapsed, allocatedAfter) =
      Using.resource(PlatformMetrics.startRecording(sys.env.get("GEMSTONE_BENCHMARK_JFR").map(Path.of(_)))) { _ =>
        val before  = PlatformMetrics.allocatedBytes
        val started = PlatformTime.monotonicNanos
        iteration = 0
        while (iteration < measured) {
          execute()
          iteration += 1
        }
        (
          before,
          math.max(0L, PlatformTime.monotonicNanos - started).refineUnsafe[Positive0],
          PlatformMetrics.allocatedBytes
        )
      }
    Console.out.println(
      f"amdgpu-alu benchmark mode=${mode.toString.toLowerCase} dispatches=$measured warmup=$warmup instructions_per_dispatch=${instructionCount +
          1} " +
        f"elapsed_ms=${elapsed / 1000000.0}%.3f ns_per_dispatch=${elapsed.toDouble / measured.toDouble}%.1f " +
        f"allocated_bytes_per_dispatch=${(allocatedBefore, allocatedAfter) match {
            case (Some(before), Some(after)) if after >= before => (after - before).toDouble / measured.toDouble
            case _                                              => Double.NaN
          }}%.1f"
    )
  }
}
