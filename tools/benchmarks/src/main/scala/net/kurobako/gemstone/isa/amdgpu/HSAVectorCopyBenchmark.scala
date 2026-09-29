package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import com.monovore.decline.Opts
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** The exported HSA vector-copy fixture, dispatched without the HSA queue. */
object HSAVectorCopyBenchmark {
  private case class Options(groups: Int > 0, iterations: Int > 0)
  private val name    = BenchmarkName("hsa-vector-copy")
  private val options = (
    Opts.option[Int]("groups", help = "64-item workgroups per dispatch").withDefault(1)
      .validate("groups must be positive")(_ > 0).map(_.unsafe[Int > 0]),
    Opts.option[Int]("iterations", help = "Dispatches per sample").withDefault(5)
      .validate("iterations must be positive")(_ > 0).map(_.unsafe[Int > 0])
  ).mapN(Options(_, _))

  private class CopyMemory(elements: Int) extends Memory with BenchmarkAddressSpace {
    private val source      = Array.ofDim[Byte](Math.multiplyExact(elements, 4))
    private val destination = Array.ofDim[Byte](source.length)
    private val guard       = Array.fill[Byte](source.length)(0x5a.toByte)
    override protected val regionSizes: Iterable[(AllocationId, Long)] =
      Vector(
        AllocationId(1) -> source.length.toLong,
        AllocationId(2) -> destination.length.toLong,
        AllocationId(3) -> guard.length.toLong
      )

    private def array(region: AllocationId): Either[Fault, Array[Byte]] = region.toLong match {
      case 1L => Right(source)
      case 2L => Right(destination)
      case 3L => Right(guard)
      case _  => Left(Fault.Memory("unknown HSA benchmark allocation"))
    }

    override def read(
        region: AllocationId,
        offset: Long >= 0,
        size: Int >= 0
    ): Either[Fault, Vec[Byte]] =
      array(region).flatMap { bytes =>
        Either.cond(
          ByteRange.contains(bytes.length, offset.raw, size.raw.toLong),
          Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(bytes, offset.raw.toInt, offset.raw.toInt + size.raw)),
          Fault.InvalidTask("HSA benchmark read outside allocation")
        )
      }

    override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
      array(region).flatMap { target =>
        if (!ByteRange.contains(target.length, offset.raw, bytes.length.toLong))
          Left(Fault.InvalidTask("HSA benchmark write outside allocation"))
        else {
          val _ = bytes.copyToArray(target, offset.raw.toInt, bytes.length)
          Right(())
        }
      }

    override def readValue(
        region: AllocationId,
        offset: Long >= 0,
        size: Int where Between[1, 8]
    ): Either[Fault, Long] =
      array(region).flatMap { bytes =>
        Either.cond(
          ByteRange.contains(bytes.length, offset.raw, size.raw.toLong),
          LittleEndian.decode(bytes, offset.raw.toInt, size.scalarByteWidth),
          Fault.InvalidTask("HSA benchmark read outside allocation")
        )
      }

    override def writeValue(
        region: AllocationId,
        offset: Long >= 0,
        value: Long,
        size: Int where Between[1, 8]
    ): Either[Fault, Unit] =
      array(region).flatMap { bytes =>
        if (!ByteRange.contains(bytes.length, offset.raw, size.raw.toLong))
          Left(Fault.InvalidTask("HSA benchmark write outside allocation"))
        else {
          LittleEndian.write(value, bytes, offset.raw.toInt, size.scalarByteWidth)
          Right(())
        }
      }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def initialise(): Unit = {
      java.util.Arrays.fill(destination, 0xa5.toByte)
      var index = 0
      while (index < elements) {
        LittleEndian.write(index.toLong * 97L - 11L, source, index * 4, 4)
        index += 1
      }
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def verify(): Long = {
      var index    = 0
      var checksum = 0L
      while (index < elements) {
        val actual   = LittleEndian.decodeInt32(destination, index * 4)
        val expected = index * 97 - 11
        if (actual != expected)
          throw IllegalStateException(s"HSA vector copy mismatch at $index: $actual != $expected")
        checksum += actual.toLong
        index += 1
      }
      if (!guard.forall(_ == 0x5a.toByte))
        throw IllegalStateException("HSA vector copy modified guard allocation")
      checksum
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed   = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val elements = Math.multiplyExact(parsed.groups.raw, 64)
    val memory   = CopyMemory(elements)
    memory.initialise()
    val image    = Vec.unsafeWrapArray(BenchmarkResources.bytes("hsa-vector-copy.hsaco"))
    val prepared = (for {
      codeObject <- ELF.index(image)
      kernel     <- codeObject.kernel("vector_copy")
      compiled   <- VMCompiler.compile(kernel).left.map(error => CodeObject.Error.Invalid(error.message))
    } yield compiled).fold(error => throw IllegalArgumentException(error.message), identity)
    val task = KernelTask.validated(
      "vector_copy",
      Dimensions(64, 1, 1),
      Dimensions(parsed.groups, 1, 1),
      Vec(
        KernelTask.Argument.Memory(AllocationId(1), 0L),
        KernelTask.Argument.Memory(AllocationId(2), 0L),
        KernelTask.Argument.Memory(AllocationId(3), 0L),
        KernelTask.Argument.Scalar(Vec.fill(56)(0.toByte))
      )
    ).fold(error => throw IllegalArgumentException(error), identity)
    val before    = PlatformMetrics.allocatedBytes
    val started   = platform.monotonicNanos
    var iteration = 0
    while (iteration < parsed.iterations.raw) {
      VMInterpreter.execute(prepared, task, memory, TaskControl(), Workgroups.Sequential, Vec.empty)
        .fold(error => throw IllegalStateException(error.message), _ => ())
      iteration += 1
    }
    val elapsed   = math.max(0L, platform.monotonicNanos - started)
    val allocated = for {
      start <- before
      end   <- PlatformMetrics.allocatedBytes
      if end >= start
    } yield end - start
    val checksum = memory.verify()
    Console.out.println(
      s"groups=${parsed.groups} iterations=${parsed.iterations} nsPerDispatch=${elapsed.toDouble /
          parsed.iterations.raw} " +
        s"bytesPerDispatch=${allocated.fold(Double.NaN)(_.raw.toDouble / parsed.iterations.raw)} checksum=$checksum"
    )
  }
}
