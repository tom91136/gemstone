package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*

import java.lang.management.ManagementFactory

/** Short, standalone execution benchmark for profiling the AMDGPU hot path without a GPU or vendor oracle.
  *
  * This deliberately uses the official ROCr vector-copy kernel subset covered by CodeObjectSuite. The benchmark is not
  * a performance assertion: it prints stable per-dispatch measurements suitable for before/after comparisons.
  */
object AMDGPUExecutionBenchmark {
  private val InputId  = AllocationId.fromLong(1L).toOption.get
  private val OutputId = AllocationId.fromLong(2L).toOption.get
  private val UnusedId = AllocationId.fromLong(3L).toOption.get

  def main(arguments: Array[String]): Unit = {
    val measured = arguments.headOption.fold(200)(_.toInt)
    val warmup   = arguments.lift(1).fold(math.max(20, measured / 10))(_.toInt)
    val groups   = arguments.lift(2).fold(1)(_.toInt)
    require(groups > 0 && groups <= 65536, s"group count out of range: $groups")
    val elements = groups * 64
    require(measured > 0, s"measured dispatches must be positive: $measured")
    require(warmup >= 0, s"warmup dispatches must be non-negative: $warmup")

    val words = Col[Long](
      0xc0020242L, 0x00000004L, 0xc0020283L, 0x00000018L,
      0xc00a0003L, 0x00000000L, 0xbea10080L, 0xbf8cc07fL,
      0x8604ff09L, 0x0000ffffL, 0x92080408L, 0x810a080aL,
      0x6800000aL, 0x2202009fL, 0xd28f0000L, 0x00020082L,
      0x7e060201L, 0x32040000L, 0x38060303L, 0xdc508000L,
      0x027f0002L, 0x7e060203L, 0x32000002L, 0x38020303L,
      0xbf8c0f70L, 0xdc708000L, 0x007f0200L, 0xbf810000L
    )
    val code     = words.flatMap(word => LittleEndian.toCol(word, 4))
    val kernel   = CodeObjectKernel("vector_copy", Target.unsafe("gfx900"), 80, 0, 0, 0x80, 0x0b, code)
    val prepared =
      DispatchInterpreter.prepare(kernel).fold(failure => throw RuntimeException(failure.message), identity)
    val memory = BenchmarkMemory()
    memory.allocate(InputId, elements * 4)
    memory.allocate(OutputId, elements * 4)
    memory.allocate(UnusedId, 256)
    Array.tabulate(elements)(index => index * 97 - 11).zipWithIndex.foreach { case (value, index) =>
      memory.putInt(InputId, index * 4, value)
    }

    val zero     = KernelArgument.Scalar(Col.fill(8)(0.toByte))
    val dispatch = KernelDispatch.create(
      ProgramId.fromLong(1L).toOption.get,
      "vector_copy",
      Dimensions.create(64, 1, 1).toOption.get,
      Dimensions.create(groups, 1, 1).toOption.get,
      Col(
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
    ).fold(message => throw IllegalArgumentException(message), identity)

    def execute(): Unit = {
      val control = ExecutionControl.create()
      DispatchInterpreter.execute(prepared, dispatch, memory, control).fold(
        failure => throw RuntimeException(failure.message),
        identity
      )
    }

    var iteration = 0
    while (iteration < warmup) {
      execute()
      iteration += 1
    }

    val bean = ManagementFactory.getThreadMXBean match {
      case value: com.sun.management.ThreadMXBean if value.isThreadAllocatedMemorySupported =>
        if (!value.isThreadAllocatedMemoryEnabled) value.setThreadAllocatedMemoryEnabled(true)
        Some(value)
      case _ => None
    }
    val recording = sys.env.get("GEMSTONE_BENCHMARK_JFR").map { _ =>
      val value = jdk.jfr.Recording(jdk.jfr.Configuration.getConfiguration("profile"))
      value.start()
      value
    }
    val threadId        = Thread.currentThread().threadId()
    val allocatedBefore = bean.fold(0L)(_.getThreadAllocatedBytes(threadId))
    val started         = System.nanoTime()
    iteration = 0
    while (iteration < measured) {
      execute()
      iteration += 1
    }
    val elapsed        = System.nanoTime() - started
    val allocatedAfter = bean.fold(0L)(_.getThreadAllocatedBytes(threadId))
    recording.foreach { value =>
      value.stop()
      value.dump(java.nio.file.Path.of(sys.env("GEMSTONE_BENCHMARK_JFR")))
      value.close()
    }
    val checksum = Array.tabulate(elements) { index =>
      val observed = memory.getInt(OutputId, index * 4)
      require(observed == index * 97 - 11, s"copy mismatch at $index: $observed")
      observed.toLong
    }.sum

    val nanosPerDispatch = elapsed.toDouble / measured.toDouble
    val bytesPerDispatch = bean.fold(Double.NaN)(_ => (allocatedAfter - allocatedBefore).toDouble / measured.toDouble)
    println(
      f"amdgpu-execution benchmark dispatches=$measured warmup=$warmup groups=$groups " +
        f"elapsed_ms=${elapsed / 1000000.0}%.3f ns_per_dispatch=$nanosPerDispatch%.1f " +
        f"allocated_bytes_per_dispatch=$bytesPerDispatch%.1f checksum=$checksum"
    )
  }

  private final class BenchmarkMemory extends ExecutionMemory {
    private var allocations = Map.empty[AllocationId, Array[Byte]]

    def allocate(id: AllocationId, size: Int): Unit = allocations = allocations.updated(id, Array.ofDim[Byte](size))

    def putInt(id: AllocationId, offset: Int, value: Int): Unit =
      LittleEndian.write(value.toLong, allocations(id), offset, 4)

    def getInt(id: AllocationId, offset: Int): Int =
      LittleEndian.decode(allocations(id), offset, 4).toInt

    override def read(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Col[Byte]] =
      allocations.get(id).toRight(ExecutionFailure.NotFound("benchmark allocation")).flatMap { bytes =>
        Either.cond(
          ByteRange.contains(bytes.length, offset, size.toLong),
          Col.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size)),
          ExecutionFailure.InvalidRequest("benchmark read range")
        )
      }

    override def readValue(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Long] =
      allocations.get(id) match {
        case None        => Left(ExecutionFailure.NotFound("benchmark allocation"))
        case Some(bytes) =>
          if (ByteRange.contains(bytes.length, offset, size.toLong))
            Right(LittleEndian.decode(bytes, offset.toInt, size))
          else Left(ExecutionFailure.InvalidRequest("benchmark read range"))
      }

    override def write(id: AllocationId, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] =
      allocations.get(id).toRight(ExecutionFailure.NotFound("benchmark allocation")).flatMap { destination =>
        Either.cond(
          ByteRange.contains(destination.length, offset, bytes.length.toLong), {
            val _ = bytes.copyToArray(destination, offset.toInt, bytes.length)
            ()
          },
          ExecutionFailure.InvalidRequest("benchmark write range")
        )
      }

    override def writeValue(
        id: AllocationId,
        offset: Long,
        value: Long,
        size: Int
    ): Either[ExecutionFailure, Unit] =
      allocations.get(id) match {
        case None              => Left(ExecutionFailure.NotFound("benchmark allocation"))
        case Some(destination) =>
          if (ByteRange.contains(destination.length, offset, size.toLong)) {
            LittleEndian.write(value, destination, offset.toInt, size)
            Right(())
          } else Left(ExecutionFailure.InvalidRequest("benchmark write range"))
      }

    override def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
  }
}

/** ALU-heavy companion to [[AMDGPUExecutionBenchmark]], isolating instruction and lane dispatch from guest memory. */
object AMDGPUALUExecutionBenchmark {
  private object RejectingMemory extends ExecutionMemory {
    override def read(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Col[Byte]] =
      Left(ExecutionFailure.Failed("ALU benchmark unexpectedly read memory"))
    override def write(id: AllocationId, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] =
      Left(ExecutionFailure.Failed("ALU benchmark unexpectedly wrote memory"))
    override def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
  }

  def main(arguments: Array[String]): Unit = {
    val measured         = arguments.headOption.fold(50)(_.toInt)
    val warmup           = arguments.lift(1).fold(math.max(10, measured / 10))(_.toInt)
    val instructionCount = arguments.lift(2).fold(512)(_.toInt)
    val mode             = arguments.lift(3).getOrElse("move")
    val instruction      = mode match {
      case "move" => 0x7e0002ebL
      // gfx9 VOP2 v_add_f32 v0, v1, v2
      case "binary" => 0x02000501L
      case other    => throw IllegalArgumentException(s"unknown ALU benchmark mode: $other")
    }
    require(instructionCount >= 0, s"instruction count must be non-negative: $instructionCount")
    val words    = Col.from(Iterator.fill(instructionCount)(instruction) ++ Iterator.single(0xbf810000L))
    val code     = words.flatMap(word => LittleEndian.toCol(word, 4))
    val kernel   = CodeObjectKernel("alu", Target.unsafe("gfx900"), 0, 0, 0, 0, 0, code)
    val prepared =
      DispatchInterpreter.prepare(kernel).fold(failure => throw RuntimeException(failure.message), identity)
    val dispatch = KernelDispatch.create(
      ProgramId.fromLong(1L).toOption.get,
      "alu",
      Dimensions.create(64, 1, 1).toOption.get,
      Dimensions.One,
      Col.empty
    ).fold(message => throw IllegalArgumentException(message), identity)
    def execute(): Unit =
      DispatchInterpreter.execute(prepared, dispatch, RejectingMemory, ExecutionControl.create()).fold(
        failure => throw RuntimeException(failure.message),
        identity
      )

    var iteration = 0
    while (iteration < warmup) {
      execute()
      iteration += 1
    }
    val bean = ManagementFactory.getThreadMXBean match {
      case value: com.sun.management.ThreadMXBean if value.isThreadAllocatedMemorySupported =>
        if (!value.isThreadAllocatedMemoryEnabled) value.setThreadAllocatedMemoryEnabled(true)
        Some(value)
      case _ => None
    }
    val recording = sys.env.get("GEMSTONE_BENCHMARK_JFR").map { _ =>
      val value = jdk.jfr.Recording(jdk.jfr.Configuration.getConfiguration("profile"))
      value.start()
      value
    }
    val threadId        = Thread.currentThread().threadId()
    val allocatedBefore = bean.fold(0L)(_.getThreadAllocatedBytes(threadId))
    val started         = System.nanoTime()
    iteration = 0
    while (iteration < measured) {
      execute()
      iteration += 1
    }
    val elapsed        = System.nanoTime() - started
    val allocatedAfter = bean.fold(0L)(_.getThreadAllocatedBytes(threadId))
    recording.foreach { value =>
      value.stop()
      value.dump(java.nio.file.Path.of(sys.env("GEMSTONE_BENCHMARK_JFR")))
      value.close()
    }
    println(
      f"amdgpu-alu benchmark mode=$mode dispatches=$measured warmup=$warmup instructions_per_dispatch=${instructionCount +
          1} " +
        f"elapsed_ms=${elapsed / 1000000.0}%.3f ns_per_dispatch=${elapsed.toDouble / measured.toDouble}%.1f " +
        f"allocated_bytes_per_dispatch=${bean.fold(Double.NaN)(_ =>
            (allocatedAfter - allocatedBefore).toDouble / measured.toDouble
          )}%.1f"
    )
  }
}
