package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import cats.syntax.all.*
import com.monovore.decline.Opts
import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable
import scala.util.Using

/** Short, standalone execution benchmark for profiling the AMDGPU hot path without a GPU or vendor oracle.
  *
  * This deliberately uses the official ROCr vector-copy kernel subset covered by CodeObjectSuite. The benchmark is not
  * a performance assertion: it prints stable per-dispatch measurements suitable for before/after comparisons.
  */
object AMDGPUExecutionBenchmark {
  private def dimensions(x: Int > 0): Dimensions = Dimensions(x, 1, 1)

  private case class Options(
      measured: Int > 0,
      warmup: Option[Int >= 0],
      groups: Int > 0
  )
  private val options = (
    Opts.option[Int]("measured", help = "Measured dispatches").withDefault(200)
      .refined[Int > 0]("measured dispatches must be positive"),
    Opts.option[Int](
      "warmup",
      help = "Warmup dispatches"
    ).refined[Int >= 0]("warmup dispatches must be non-negative").orNone,
    Opts.option[Int]("groups", help = "Workgroups per dispatch").withDefault(1)
      .refined[Int in 1 ~ 65536]("workgroup count must be between 1 and 65536")
      .map(_.widen[Int > 0])
  ).mapN(Options(_, _, _))
  private val InputId  = AllocationId(1L)
  private val OutputId = AllocationId(2L)
  private val UnusedId = AllocationId(3L)

  // Warmup and measured dispatches share an allocation-free counter loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed            = BenchmarkArguments.parse(BenchmarkName("amdgpu-execution"), arguments.toSeq, options)
    val measured          = parsed.measured
    val warmup            = parsed.warmup.getOrElse((measured.widen[Int >= 0] / 10).max(20))
    val groups            = parsed.groups
    val elements: Int > 0 = groups * 64

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
    memory.allocate(InputId, elements * 4)
    memory.allocate(OutputId, elements * 4)
    memory.allocate(UnusedId, 256)
    Vec.indices(elements.widen[Int >= 0]).foreach { index =>
      memory.putInt(InputId, index * 4, index.raw * 97 - 11)
    }

    val zero     = KernelTask.Argument.Scalar(Vec.fill(8)(0.toByte))
    val dispatch = KernelTask.validated(
      "vector_copy",
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
      val control = TaskControl()
      VMInterpreter.execute(
        prepared,
        dispatch,
        memory,
        control,
        Workgroups.Sequential,
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

    val measurement =
      Using.resource(PlatformMetrics.startRecording(sys.env.get(env.BenchmarkJfr).map(Path.of(_)))) { _ =>
        Measurement {
          iteration = 0
          while (iteration < measured.raw) {
            execute()
            iteration += 1
          }
        }
      }
    val checksum = Vec.indices(elements.widen[Int >= 0]).map { index =>
      val observed = memory.getInt(OutputId, index * 4)
      if (observed != index.raw * 97 - 11)
        throw IllegalStateException(s"copy mismatch at $index: $observed")
      observed.toLong
    }.sum

    val nanosPerDispatch = measurement.elapsedNanos.raw.toDouble / measured.raw.toDouble
    val bytesPerDispatch = measurement.allocatedBytes.fold(Double.NaN)(_.raw.toDouble / measured.raw.toDouble)
    Console.out.println(
      f"amdgpu-execution benchmark dispatches=$measured warmup=$warmup groups=$groups " +
        f"elapsed_ms=${measurement.elapsedNanos.raw / 1000000.0}%.3f ns_per_dispatch=$nanosPerDispatch%.1f " +
        f"allocated_bytes_per_dispatch=$bytesPerDispatch%.1f checksum=$checksum"
    )
  }

  // Mutable allocation contents are confined to this final benchmark memory fixture.
  private class BenchmarkMemory extends Memory with BenchmarkAddressSpace {
    private val allocations                                             = mutable.Map.empty[AllocationId, Array[Byte]]
    override protected val regionSizes: mutable.Map[AllocationId, Long] = mutable.Map.empty

    def allocate(id: AllocationId, size: Int > 0): Unit = {
      allocations(id) = Array.ofDim[Byte](size.raw)
      regionSizes(id) = size.raw.toLong
    }

    def putInt(id: AllocationId, offset: Int >= 0, value: Int): Unit =
      LittleEndian.write(value.toLong, allocations(id), offset.raw, 4)

    def getInt(id: AllocationId, offset: Int >= 0): Int =
      LittleEndian.decodeInt32(allocations(id), offset.raw)

    override def read(
        id: AllocationId,
        offset: Long >= 0,
        size: Int >= 0
    ): Either[Fault, Vec[Byte]] =
      allocations.get(id).toRight(Fault.Memory("benchmark memory region")).flatMap { bytes =>
        Either.cond(
          ByteRange.contains(bytes.length, offset.raw, size.raw.toLong),
          Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(bytes, offset.raw.toInt, offset.raw.toInt + size.raw)),
          Fault.InvalidTask("benchmark read range")
        )
      }

    override def readValue(
        id: AllocationId,
        offset: Long >= 0,
        size: Int in 1 ~ 8
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
        id: AllocationId,
        offset: Long >= 0,
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
        id: AllocationId,
        offset: Long >= 0,
        value: Long,
        size: Int in 1 ~ 8
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
