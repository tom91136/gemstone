package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.{PlatformTime, Vec}
import net.kurobako.gemstone.benchmarks.{BenchmarkArguments, BenchmarkName, BenchmarkResources, PlatformMetrics}
import net.kurobako.gemstone.binary.{ByteRange, LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.memory.MemoryValueByteWidth
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

import cats.syntax.all.*
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** The exported HSA vector-copy fixture, dispatched without the HSA queue. */
object HSAVectorCopyBenchmark {
  private final case class Options(groups: Int :| Positive, iterations: Int :| Positive)
  private val name    = BenchmarkName("hsa-vector-copy")
  private val options = (
    Opts.option[Int]("groups", help = "64-item workgroups per dispatch").withDefault(1)
      .validate("groups must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("iterations", help = "Dispatches per sample").withDefault(5)
      .validate("iterations must be positive")(_ > 0).map(_.refineUnsafe[Positive])
  ).mapN(Options(_, _))

  private final class CopyMemory(elements: Int) extends Memory with BenchmarkAddressSpace {
    private val source      = Array.ofDim[Byte](Math.multiplyExact(elements, 4))
    private val destination = Array.ofDim[Byte](source.length)
    private val guard       = Array.fill[Byte](source.length)(0x5a.toByte)
    override protected val regionSizes: Iterable[(MemoryRegion, Long)] =
      Vector(
        MemoryRegion(1) -> source.length.toLong,
        MemoryRegion(2) -> destination.length.toLong,
        MemoryRegion(3) -> guard.length.toLong
      )

    private def array(region: MemoryRegion): Either[Fault, Array[Byte]] = region.value match {
      case 1L => Right(source)
      case 2L => Right(destination)
      case 3L => Right(guard)
      case _  => Left(Fault.Memory("unknown HSA benchmark allocation"))
    }

    override def read(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[Fault, Vec[Byte]] =
      array(region).flatMap { bytes =>
        Either.cond(
          ByteRange.contains(bytes.length, offset, size.toLong),
          Vec.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size)),
          Fault.InvalidTask("HSA benchmark read outside allocation")
        )
      }

    override def write(region: MemoryRegion, offset: Long :| Positive0, bytes: Vec[Byte]): Either[Fault, Unit] =
      array(region).flatMap { target =>
        if (!ByteRange.contains(target.length, offset, bytes.length.toLong))
          Left(Fault.InvalidTask("HSA benchmark write outside allocation"))
        else {
          val _ = bytes.copyToArray(target, offset.toInt, bytes.length)
          Right(())
        }
      }

    override def readValue(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: MemoryValueByteWidth
    ): Either[Fault, Long] =
      array(region).flatMap { bytes =>
        Either.cond(
          ByteRange.contains(bytes.length, offset, size.value.toLong),
          LittleEndian.decode(bytes, offset.toInt, size.scalarByteWidth),
          Fault.InvalidTask("HSA benchmark read outside allocation")
        )
      }

    override def writeValue(
        region: MemoryRegion,
        offset: Long :| Positive0,
        value: Long,
        size: MemoryValueByteWidth
    ): Either[Fault, Unit] =
      array(region).flatMap { bytes =>
        if (!ByteRange.contains(bytes.length, offset, size.value.toLong))
          Left(Fault.InvalidTask("HSA benchmark write outside allocation"))
        else {
          LittleEndian.write(value, bytes, offset.toInt, size.scalarByteWidth)
          Right(())
        }
      }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def initialise(): Unit = {
      java.util.Arrays.fill(destination, 0xa5.toByte)
      var index = 0
      while (index < elements) {
        LittleEndian.write(index.toLong * 97L - 11L, source, index * 4, ScalarByteWidth(4))
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
    val elements = Math.multiplyExact(parsed.groups, 64)
    val memory   = CopyMemory(elements)
    memory.initialise()
    val image    = Vec.unsafeFromArray(BenchmarkResources.bytes("hsa-vector-copy.hsaco"))
    val prepared = (for {
      codeObject <- CodeObject.index(image)
      kernel     <- codeObject.kernel("vector_copy")
      compiled   <- VMCompiler.compile(kernel).left.map(error => CodeObject.CodeObjectError.Invalid(error.message))
    } yield compiled).fold(error => throw IllegalArgumentException(error.message), identity)
    val task = KernelTask.validated(
      KernelName("vector_copy"),
      Dimensions(64, 1, 1),
      Dimensions(parsed.groups, 1, 1),
      Vec(
        KernelTask.Argument.Memory(MemoryRegion(1), 0L),
        KernelTask.Argument.Memory(MemoryRegion(2), 0L),
        KernelTask.Argument.Memory(MemoryRegion(3), 0L),
        KernelTask.Argument.Scalar(Vec.fill(56)(0.toByte))
      )
    ).fold(error => throw IllegalArgumentException(error), identity)
    val before    = PlatformMetrics.allocatedBytes
    val started   = PlatformTime.monotonicNanos
    var iteration = 0
    while (iteration < parsed.iterations) {
      VMInterpreter.execute(prepared, task, memory, memory, TaskControl.default, Scheduler.Sequential, Vec.empty)
        .fold(error => throw IllegalStateException(error.message), _ => ())
      iteration += 1
    }
    val elapsed   = math.max(0L, PlatformTime.monotonicNanos - started)
    val allocated = for {
      start <- before
      end   <- PlatformMetrics.allocatedBytes
      if end >= start
    } yield end - start
    val checksum = memory.verify()
    Console.out.println(
      s"groups=${parsed.groups} iterations=${parsed.iterations} nsPerDispatch=${elapsed.toDouble / parsed.iterations} " +
        s"bytesPerDispatch=${allocated.fold(Double.NaN)(_.toDouble / parsed.iterations)} checksum=$checksum"
    )
  }
}
