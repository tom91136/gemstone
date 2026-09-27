package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.{PlatformTime, Vec}
import net.kurobako.gemstone.benchmarks.{
  BenchmarkArguments,
  BenchmarkName,
  BenchmarkResources,
  CloverleafFluxFixture,
  PlatformMetrics
}
import net.kurobako.gemstone.binary.{ByteRange, LittleEndian}
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

import cats.syntax.all.*
import com.monovore.decline.Opts

/** HIP code-object form of the bounded CloverLeaf flux calculation. */
object HIPCloverleafFluxBenchmark {
  private case class Options(cells: Positive[Int], iterations: Positive[Int])
  private val name                  = BenchmarkName("hip-cloverleaf-flux")
  private val explicitArgumentBytes = 8 * 8 + 4 + 4 + 8
  private val options               = (
    Opts.option[Int]("cells", help = "Cells per dispatch").withDefault(64)
      .validate("cells must be positive")(_ > 0)
      .map(Positive.unsafe),
    Opts.option[Int]("iterations", help = "Dispatches per sample").withDefault(20)
      .validate("iterations must be positive")(_ > 0).map(Positive.unsafe)
  ).mapN(Options(_, _))

  private final class FluxMemory(length: Int) extends Memory with BenchmarkAddressSpace {
    private val arrays = Array.fill(CloverleafFluxFixture.arrays)(Array.ofDim[Byte](Math.multiplyExact(length, 8)))
    override protected val regionSizes: Iterable[(MemoryRegion, Long)] =
      arrays.indices.map(index => MemoryRegion(index.toLong + 1L) -> arrays(index).length.toLong)

    private def array(region: MemoryRegion): Either[Fault, Array[Byte]] =
      if (region.value >= 1L && region.value <= arrays.length) Right(arrays(region.value.toInt - 1))
      else Left(Fault.Memory("unknown CloverLeaf benchmark allocation"))

    override def read(
        region: MemoryRegion,
        offset: NonNegative[Long],
        size: NonNegative[Int]
    ): Either[Fault, Vec[Byte]] =
      array(region).flatMap { source =>
        Either.cond(
          ByteRange.contains(source.length, offset.raw, size.raw.toLong),
          Vec.unsafeFromArray(java.util.Arrays.copyOfRange(
            source,
            offset.raw.toInt,
            offset.raw.toInt + size.raw
          )),
          Fault.InvalidTask("CloverLeaf read outside allocation")
        )
      }

    override def write(region: MemoryRegion, offset: NonNegative[Long], bytes: Vec[Byte]): Either[Fault, Unit] =
      array(region).flatMap { destination =>
        if (!ByteRange.contains(destination.length, offset.raw, bytes.length.toLong))
          Left(Fault.InvalidTask("CloverLeaf write outside allocation"))
        else {
          val _ = bytes.copyToArray(destination, offset.raw.toInt, bytes.length)
          Right(())
        }
      }

    override def readValue(
        region: MemoryRegion,
        offset: NonNegative[Long],
        size: IntBetween[1, 8]
    ): Either[Fault, Long] =
      array(region).flatMap { source =>
        Either.cond(
          ByteRange.contains(source.length, offset.raw, size.raw.toLong),
          LittleEndian.decode(source, offset.raw.toInt, size.scalarByteWidth),
          Fault.InvalidTask("CloverLeaf read outside allocation")
        )
      }

    override def writeValue(
        region: MemoryRegion,
        offset: NonNegative[Long],
        value: Long,
        size: IntBetween[1, 8]
    ): Either[Fault, Unit] =
      array(region).flatMap { destination =>
        if (!ByteRange.contains(destination.length, offset.raw, size.raw.toLong))
          Left(Fault.InvalidTask("CloverLeaf write outside allocation"))
        else {
          LittleEndian.write(value, destination, offset.raw.toInt, size.scalarByteWidth)
          Right(())
        }
      }

    def put(array: Int, index: Int, value: Double): Unit =
      LittleEndian.write(
        java.lang.Double.doubleToRawLongBits(value),
        arrays(array),
        index * 8,
        8
      )

    def get(array: Int, index: Int): Double =
      java.lang.Double.longBitsToDouble(LittleEndian.decode(arrays(array), index * 8, 8))
  }

  private def hiddenArguments(image: Vec[Byte], kernargSize: Int, cells: Int, groups: Int): Vec[Byte] = {
    val manifest = CodeObjectMetadata.decode(image)
      .fold(error => throw IllegalArgumentException(error.message), identity)
      .find(_.name.raw == "clover_flux")
      .getOrElse(throw IllegalArgumentException("missing HIP CloverLeaf kernel metadata"))
    if (manifest.metadata.argumentBlockSize.raw != kernargSize || kernargSize < explicitArgumentBytes)
      throw IllegalArgumentException("HIP CloverLeaf kernarg layout differs from its code-object metadata")
    val hidden = Array.ofDim[Byte](kernargSize - explicitArgumentBytes)
    def write(name: String, value: Long, width: IntBetween[0, 8]): Unit = {
      val field = manifest.hiddenArguments.find(_.valueKind == name)
        .getOrElse(throw IllegalArgumentException(s"missing HIP kernarg field $name"))
      val offset = field.offset - explicitArgumentBytes
      if (field.width != width.raw || !ByteRange.contains(hidden.length, offset, width.raw.toLong))
        throw IllegalArgumentException(s"invalid HIP kernarg field $name layout")
      LittleEndian.write(value, hidden, offset, width)
    }
    write("hidden_block_count_x", groups.toLong, 4)
    write("hidden_block_count_y", 1L, 4)
    write("hidden_block_count_z", 1L, 4)
    write("hidden_group_size_x", 64L, 2)
    write("hidden_group_size_y", 1L, 2)
    write("hidden_group_size_z", 1L, 2)
    write("hidden_remainder_x", (cells % 64).toLong, 2)
    write("hidden_grid_dims", 1L, 2)
    Vec.unsafeFromArray(hidden)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val length = CloverleafFluxFixture.length(parsed.cells.raw)
    val memory = FluxMemory(length)
    var array  = 0
    while (array < CloverleafFluxFixture.inputs) {
      var index = 0
      while (index < length) {
        memory.put(array, index, CloverleafFluxFixture.value(array, index))
        index += 1
      }
      array += 1
    }
    val image    = Vec.unsafeFromArray(BenchmarkResources.bytes("cloverleaf-flux.hsaco"))
    val prepared = (for {
      codeObject <- CodeObject.index(image)
      kernel     <- codeObject.kernel("clover_flux")
      compiled   <- VMCompiler.compile(kernel).left.map(error => CodeObject.CodeObjectError.Invalid(error.message))
    } yield compiled).fold(error => throw IllegalArgumentException(error.message), identity)
    val groups = Positive.unsafe(((parsed.cells.raw.toLong + 63L) / 64L).toInt)
    val hidden = hiddenArguments(image, prepared.source.kernargSize, parsed.cells.raw, groups.raw)
    val task   = KernelTask.validated(
      NonEmptyString("clover_flux"),
      Dimensions(64, 1, 1),
      Dimensions(groups, 1, 1),
      Vec.from((1L to 8L).map(id => KernelTask.Argument.Memory(MemoryRegion(id), 0L))) ++ Vec(
        KernelTask.Argument.Scalar(LittleEndian.toVec(parsed.cells.raw.toLong, 4)),
        KernelTask.Argument.Scalar(LittleEndian.toVec(
          CloverleafFluxFixture.rowStride.toLong,
          4
        )),
        KernelTask.Argument.Scalar(LittleEndian.toVec(
          java.lang.Double.doubleToRawLongBits(CloverleafFluxFixture.dt),
          8
        )),
        KernelTask.Argument.Scalar(hidden)
      )
    ).fold(error => throw IllegalArgumentException(error), identity)
    val before    = PlatformMetrics.allocatedBytes
    val started   = PlatformTime.monotonicNanos
    var iteration = 0
    while (iteration < parsed.iterations.raw) {
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
    var checksum = 0L
    var index    = 0
    while (index < parsed.cells.raw) {
      checksum ^= CloverleafFluxFixture.verify(index, memory.get(6, index), memory.get(7, index))
      index += 1
    }
    Console.out.println(
      s"cells=${parsed.cells} iterations=${parsed.iterations} nsPerDispatch=${elapsed.toDouble / parsed.iterations.raw} " +
        s"bytesPerDispatch=${allocated.fold(Double.NaN)(_.raw.toDouble / parsed.iterations.raw)} checksum=$checksum"
    )
  }
}
