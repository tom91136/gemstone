package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Opts
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

/** Bounded OpenCL CTS async-copy paths, measured after SPIR-V loading. */
object OpenCLCTSCopyBenchmark {
  private case class Options(
      direction: String,
      groups: Int > 0,
      localSize: Int > 0,
      stride: Int > 0,
      iterations: Int > 0,
      parallelism: Int > 0
  )

  private val name    = BenchmarkName("opencl-cts-async-copy")
  private val options = (
    Opts.option[String]("direction", help = "global-to-local or local-to-global")
      .withDefault("global-to-local")
      .validate("direction must be global-to-local or local-to-global")(value =>
        value == "global-to-local" || value == "local-to-global"
      ),
    Opts.option[Int]("groups", help = "Workgroups per dispatch").withDefault(16)
      .validate("groups must be positive")(_ > 0).map(_.unsafe[Int > 0]),
    Opts.option[Int]("local-size", help = "Work items per group").withDefault(8)
      .validate("local-size must be positive")(_ > 0).map(_.unsafe[Int > 0]),
    Opts.option[Int]("stride", help = "Element stride (1 is contiguous)").withDefault(2)
      .validate("stride must be positive")(_ > 0).map(_.unsafe[Int > 0]),
    Opts.option[Int]("iterations", help = "Dispatches per sample").withDefault(3)
      .validate("iterations must be positive")(_ > 0).map(_.unsafe[Int > 0]),
    Opts.option[Int]("parallelism", help = "VM worker count").withDefault(1)
      .validate("parallelism must be positive")(_ > 0).map(_.unsafe[Int > 0])
  ).mapN(Options(_, _, _, _, _, _))

  private val inputRegion  = AllocationId(1)
  private val outputRegion = AllocationId(2)

  private class CopyMemory(elements: Int) extends Memory {
    private val byteCount = Math.multiplyExact(elements, 4)
    private val input     = Array.tabulate[Byte](byteCount)(index =>
      (index * 37 + 11).toByte
    )
    private val output = Array.fill[Byte](byteCount)(0x5a.toByte)

    override def read(
        region: AllocationId,
        offset: Long >= 0,
        size: Int >= 0
    ): Either[Fault, Vec[Byte]] =
      bytes(region).flatMap { source =>
        Either.cond(
          ByteRange.contains(source.length, offset.raw, size.raw.toLong),
          Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(
            source,
            offset.raw.toInt,
            offset.raw.toInt + size.raw
          )),
          Fault.InvalidTask("benchmark read outside allocation")
        )
      }

    override def write(
        region: AllocationId,
        offset: Long >= 0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      if (region != outputRegion) Left(Fault.InvalidTask("benchmark write to input"))
      else if (!ByteRange.contains(output.length, offset.raw, bytes.length.toLong))
        Left(Fault.InvalidTask("benchmark write outside allocation"))
      else {
        val _ = bytes.copyToArray(output, offset.raw.toInt, bytes.length)
        Right(())
      }

    private def bytes(region: AllocationId): Either[Fault, Array[Byte]] =
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
    val memory                   = CopyMemory(elements)
    val kernel: NonEmpty[String] = (parsed.direction, parsed.stride.raw) match {
      case ("global-to-local", 1) => "global_to_local_contiguous"
      case ("local-to-global", 1) => "local_to_global_contiguous"
      case ("global-to-local", _) => "global_to_local"
      case _                      => "local_to_global"
    }
    val task = KernelTask.validated(
      kernel,
      LaunchGeometry.Dimensions(parsed.localSize, 1, 1),
      LaunchGeometry.Dimensions(parsed.groups, 1, 1),
      Vec(
        KernelTask.Argument.Memory(inputRegion, 0L),
        KernelTask.Argument.Memory(outputRegion, 0L),
        KernelTask.Argument.Local(parsed.localSize.toLong * 3L * 4L),
        KernelTask.Argument.Scalar(LittleEndian.toVec(parsed.stride.raw.toLong, 4))
      )
    ).fold(error => throw IllegalArgumentException(error), identity)
    val image  = BenchmarkResources.bytes("opencl-cts-async-copy.spv")
    val module = ISA.decode(Vec.unsafeWrapArray(image))
      .fold(error => throw IllegalArgumentException(error.message), identity)

    Using.resource(WorkerPool(parsed.parallelism, minGroups = 1L, minWorkItems = 1L)) { workgroups =>
      val program = LoadedProgram.load(module, Validation.Environment.OpenCLCompute)
        .fold(error => throw IllegalArgumentException(error.message), identity)
      val allocatedBefore = PlatformMetrics.allocatedBytes
      val started         = platform.monotonicNanos
      var iteration       = 0
      while (iteration < parsed.iterations.raw) {
        program.execute(task, memory, TaskControl(), workgroups)
          .fold(error => throw IllegalStateException(error.message), _ => ())
        iteration += 1
      }
      val elapsed   = math.max(0L, platform.monotonicNanos - started)
      val allocated = for {
        before <- allocatedBefore
        after  <- PlatformMetrics.allocatedBytes
        if after >= before
      } yield after - before
      val checksum = memory.verify(parsed.groups.raw, parsed.localSize.raw, parsed.stride.raw)
      Console.out.println(
        s"direction=${parsed.direction} groups=${parsed.groups} localSize=${parsed.localSize} " +
          s"stride=${parsed.stride} iterations=${parsed.iterations} parallelism=${parsed.parallelism} " +
          s"elements=${(parsed.groups * parsed.localSize) * 3} nsPerDispatch=${elapsed.toDouble /
              parsed.iterations.raw} " +
          s"bytesPerDispatch=${allocated.fold(Double.NaN)(_.raw.toDouble / parsed.iterations.raw)} checksum=$checksum"
      )
    }
  }
}
