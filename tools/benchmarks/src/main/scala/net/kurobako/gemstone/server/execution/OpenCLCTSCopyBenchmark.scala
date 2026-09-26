package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PlatformTime, PositiveInt, PositiveLong, Vec}
import net.kurobako.gemstone.benchmarks.{BenchmarkArguments, BenchmarkName, BenchmarkResources, PlatformMetrics}
import net.kurobako.gemstone.binary.{ByteRange, LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.isa.spirv.v1_6.{ISA, Validation, VirtualMachine}
import net.kurobako.gemstone.vm.{Fault, KernelTask, LaunchGeometry, Memory, MemoryRegion, Observer, TaskControl}

import cats.syntax.all.*
import com.monovore.decline.Opts

import scala.util.Using

/** Bounded OpenCL CTS async-copy paths, measured after SPIR-V loading. */
object OpenCLCTSCopyBenchmark {
  private final case class Options(
      direction: String,
      groups: PositiveInt,
      localSize: PositiveInt,
      stride: PositiveInt,
      iterations: PositiveInt,
      parallelism: PositiveInt
  )

  private val name    = BenchmarkName("opencl-cts-async-copy")
  private val options = (
    Opts.option[String]("direction", help = "global-to-local or local-to-global")
      .withDefault("global-to-local")
      .validate("direction must be global-to-local or local-to-global")(value =>
        value == "global-to-local" || value == "local-to-global"
      ),
    Opts.option[Int]("groups", help = "Workgroups per dispatch").withDefault(16)
      .validate("groups must be positive")(_ > 0).map(PositiveInt.unsafe),
    Opts.option[Int]("local-size", help = "Work items per group").withDefault(8)
      .validate("local-size must be positive")(_ > 0).map(PositiveInt.unsafe),
    Opts.option[Int]("stride", help = "Element stride (1 is contiguous)").withDefault(2)
      .validate("stride must be positive")(_ > 0).map(PositiveInt.unsafe),
    Opts.option[Int]("iterations", help = "Dispatches per sample").withDefault(3)
      .validate("iterations must be positive")(_ > 0).map(PositiveInt.unsafe),
    Opts.option[Int]("parallelism", help = "VM worker count").withDefault(1)
      .validate("parallelism must be positive")(_ > 0).map(PositiveInt.unsafe)
  ).mapN(Options(_, _, _, _, _, _))

  private val inputRegion  = MemoryRegion(1)
  private val outputRegion = MemoryRegion(2)

  private final class CopyMemory(elements: Int) extends Memory {
    private val byteCount = Math.multiplyExact(elements, 4)
    private val input     = Array.tabulate[Byte](byteCount)(index =>
      (index * 37 + 11).toByte
    )
    private val output = Array.fill[Byte](byteCount)(0x5a.toByte)

    override def read(
        region: MemoryRegion,
        offset: NonNegativeLong,
        size: NonNegativeInt
    ): Either[Fault, Vec[Byte]] =
      bytes(region).flatMap { source =>
        Either.cond(
          ByteRange.contains(source.length, offset.raw, size.raw.toLong),
          Vec.unsafeFromArray(java.util.Arrays.copyOfRange(
            source,
            offset.raw.toInt,
            offset.raw.toInt + size.raw
          )),
          Fault.InvalidTask("benchmark read outside allocation")
        )
      }

    override def write(
        region: MemoryRegion,
        offset: NonNegativeLong,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      if (region != outputRegion) Left(Fault.InvalidTask("benchmark write to input"))
      else if (!ByteRange.contains(output.length, offset.raw, bytes.length.toLong))
        Left(Fault.InvalidTask("benchmark write outside allocation"))
      else {
        val _ = bytes.copyToArray(output, offset.raw.toInt, bytes.length)
        Right(())
      }

    private def bytes(region: MemoryRegion): Either[Fault, Array[Byte]] =
      if (region == inputRegion) Right(input)
      else if (region == outputRegion) Right(output)
      else Left(Fault.InvalidTask("unknown benchmark allocation"))

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def verify(groups: Int, localSize: Int, stride: Int): Long = {
      val copied   = groups * localSize * 3
      var checksum = 0L
      var index    = 0
      while (index < output.length) {
        val expected = if (index / 4 % stride == 0 && index / 4 / stride < copied)
          input(index)
        else 0x5a.toByte
        if (output(index) != expected)
          throw IllegalStateException(s"async copy mismatch at byte $index: ${output(index)} != $expected")
        checksum += output(index).toLong & 0xffL
        index += 1
      }
      checksum
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed   = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val elements = Math.multiplyExact(
      Math.multiplyExact(parsed.groups.raw, parsed.localSize.raw),
      Math.multiplyExact(3, parsed.stride.raw)
    )
    val memory = CopyMemory(elements)
    val kernel = (parsed.direction, parsed.stride.raw) match {
      case ("global-to-local", 1) => KernelName("global_to_local_contiguous")
      case ("local-to-global", 1) => KernelName("local_to_global_contiguous")
      case ("global-to-local", _) => KernelName("global_to_local")
      case _                      => KernelName("local_to_global")
    }
    val task = KernelTask.validated(
      kernel,
      LaunchGeometry.Dimensions(parsed.localSize, PositiveInt.One, PositiveInt.One),
      LaunchGeometry.Dimensions(parsed.groups, PositiveInt.One, PositiveInt.One),
      Vec(
        KernelTask.Argument.Memory(inputRegion, NonNegativeLong.Zero),
        KernelTask.Argument.Memory(outputRegion, NonNegativeLong.Zero),
        KernelTask.Argument.Local(PositiveLong.unsafe(parsed.localSize.raw.toLong * 3L * 4L)),
        KernelTask.Argument.Scalar(LittleEndian.toVec(parsed.stride.raw.toLong, ScalarByteWidth(4)))
      )
    ).fold(error => throw IllegalArgumentException(error), identity)
    val image  = BenchmarkResources.bytes("opencl-cts-async-copy.spv")
    val module = ISA.decode(Vec.unsafeFromArray(image))
      .fold(error => throw IllegalArgumentException(error.message), identity)

    Using.resource(WorkerPool(parsed.parallelism, minGroups = 1L, minWorkItems = 1L)) { scheduler =>
      val machine = VirtualMachine(Validation.Environment.OpenCLCompute, Observer.discard, scheduler)
      val program = machine.load(module)
        .fold(error => throw IllegalArgumentException(error.message), identity)
      val allocatedBefore = PlatformMetrics.allocatedBytes
      val started         = PlatformTime.monotonicNanos
      var iteration       = 0
      while (iteration < parsed.iterations.raw) {
        machine.execute(program, task, memory, TaskControl.forScheduler(scheduler = scheduler))
          .fold(error => throw IllegalStateException(error.message), _ => ())
        iteration += 1
      }
      val elapsed   = math.max(0L, PlatformTime.monotonicNanos - started)
      val allocated = for {
        before <- allocatedBefore
        after  <- PlatformMetrics.allocatedBytes
        if after >= before
      } yield (after - before).raw
      val checksum = memory.verify(parsed.groups.raw, parsed.localSize.raw, parsed.stride.raw)
      Console.out.println(
        s"direction=${parsed.direction} groups=${parsed.groups} localSize=${parsed.localSize} " +
          s"stride=${parsed.stride} iterations=${parsed.iterations} parallelism=${parsed.parallelism} " +
          s"elements=${(parsed.groups * parsed.localSize).raw * 3} nsPerDispatch=${elapsed.toDouble /
              parsed.iterations.raw} " +
          s"bytesPerDispatch=${allocated.fold(Double.NaN)(_.toDouble / parsed.iterations.raw)} checksum=$checksum"
      )
      machine.unload(program)
    }
  }
}
