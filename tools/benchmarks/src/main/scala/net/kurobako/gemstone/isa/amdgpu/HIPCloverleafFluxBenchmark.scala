package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import com.monovore.decline.Opts
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** HIP code-object form of the bounded CloverLeaf flux calculation. */
object HIPCloverleafFluxBenchmark {
  private case class Options(cells: Int > 0, iterations: Int > 0)
  private val name                  = BenchmarkName("hip-cloverleaf-flux")
  private val explicitArgumentBytes = 8 * 8 + 4 + 4 + 8
  private val options               = (
    Opts.option[Int]("cells", help = "Cells per dispatch").withDefault(64)
      .refined[Int > 0]("cells must be positive"),
    Opts.option[Int]("iterations", help = "Dispatches per sample").withDefault(20)
      .refined[Int > 0]("iterations must be positive")
  ).mapN(Options(_, _))

  private class FluxMemory(length: Int > 0) extends Memory with BenchmarkAddressSpace {
    private val arrays = Array.fill(CloverleafFluxFixture.arrays.raw)(Array.ofDim[Byte]((length * 8).raw))
    override protected val regionSizes: Iterable[(AllocationId, Long)] =
      Vec.indices(CloverleafFluxFixture.arrays).map(index =>
        AllocationId((1L: Long > 0) + index.toLong) -> arrays(index.raw).length.toLong
      )

    private def array(region: AllocationId): Either[Fault, Array[Byte]] =
      if (region.toLong >= 1L && region.toLong <= arrays.length) Right(arrays(region.toLong.toInt - 1))
      else Left(Fault.Memory("unknown CloverLeaf benchmark allocation"))

    override def read(
        region: AllocationId,
        offset: Long >= 0,
        size: Int >= 0
    ): Either[Fault, Vec[Byte]] =
      array(region).flatMap { source =>
        Either.cond(
          ByteRange.contains(source.length, offset.raw, size.raw.toLong),
          Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(
            source,
            offset.raw.toInt,
            offset.raw.toInt + size.raw
          )),
          Fault.InvalidTask("CloverLeaf read outside allocation")
        )
      }

    override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
      array(region).flatMap { destination =>
        if (!ByteRange.contains(destination.length, offset.raw, bytes.length.toLong))
          Left(Fault.InvalidTask("CloverLeaf write outside allocation"))
        else {
          val _ = bytes.copyToArray(destination, offset.raw.toInt, bytes.length)
          Right(())
        }
      }

    override def readValue(
        region: AllocationId,
        offset: Long >= 0,
        size: Int in 1 ~ 8
    ): Either[Fault, Long] =
      array(region).flatMap { source =>
        Either.cond(
          ByteRange.contains(source.length, offset.raw, size.raw.toLong),
          LittleEndian.decode(source, offset.raw.toInt, size.scalarByteWidth),
          Fault.InvalidTask("CloverLeaf read outside allocation")
        )
      }

    override def writeValue(
        region: AllocationId,
        offset: Long >= 0,
        value: Long,
        size: Int in 1 ~ 8
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
      java.lang.Double.longBitsToDouble(LittleEndian.decode(
        arrays(array),
        index * 8,
        8
      ))
  }

  private def hiddenArguments(image: Vec[Byte], kernargSize: Int, cells: Int, groups: Int): Vec[Byte] = {
    val manifest = CodeObjectMetadata.decode(image)
      .fold(error => throw IllegalArgumentException(error.message), identity)
      .find(_.name == "clover_flux")
      .getOrElse(throw IllegalArgumentException("missing HIP CloverLeaf kernel metadata"))
    if (manifest.metadata.argumentBlockSize.raw != kernargSize || kernargSize < explicitArgumentBytes)
      throw IllegalArgumentException("HIP CloverLeaf kernarg layout differs from its code-object metadata")
    val hidden = Array.ofDim[Byte](kernargSize - explicitArgumentBytes)
    def write(name: String, value: Long, width: Int in 0 ~ 8): Unit = {
      val field = manifest.hiddenArguments.find(_.valueKind == name)
        .getOrElse(throw IllegalArgumentException(s"missing HIP kernarg field $name"))
      val offset = field.offset.raw - explicitArgumentBytes
      if (field.width.raw != width.raw || !ByteRange.contains(hidden.length, offset, width.raw.toLong))
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
    Vec.unsafeWrapArray(hidden)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val length = CloverleafFluxFixture.length(parsed.cells)
    val memory = FluxMemory(length)
    var array  = 0
    while (array < CloverleafFluxFixture.inputs) {
      var index = 0
      while (index < length.raw) {
        memory.put(array, index, CloverleafFluxFixture.value(array, index))
        index += 1
      }
      array += 1
    }
    val image    = Vec.unsafeWrapArray(BenchmarkResources.bytes("cloverleaf-flux.hsaco"))
    val prepared = (for {
      codeObject <- ELF.index(image)
      kernel     <- codeObject.kernel("clover_flux")
      compiled   <- VMCompiler.compile(kernel).left.map(error => CodeObjectError.Invalid(error.message))
    } yield compiled).fold(error => throw IllegalArgumentException(error.message), identity)
    val groups = parsed.cells.ceilDiv(64)
    val hidden = hiddenArguments(image, prepared.source.kernargSize, parsed.cells.raw, groups.raw)
    val task   = KernelTask.validated(
      "clover_flux",
      Dimensions(64, 1, 1),
      Dimensions(groups, 1, 1),
      Vec.range[1, 9].map(id => KernelTask.Argument.Memory(AllocationId(id.toLong), 0L)) ++ Vec(
        KernelTask.Argument.Scalar(LittleEndian.toVec(parsed.cells.raw.toLong, 4)),
        KernelTask.Argument.Scalar(LittleEndian.toVec(
          CloverleafFluxFixture.rowStride.toLong.raw,
          4
        )),
        KernelTask.Argument.Scalar(LittleEndian.toVec(
          java.lang.Double.doubleToRawLongBits(CloverleafFluxFixture.dt),
          8
        )),
        KernelTask.Argument.Scalar(hidden)
      )
    ).fold(error => throw IllegalArgumentException(error), identity)
    val measurement = Measurement {
      var iteration = 0
      while (iteration < parsed.iterations.raw) {
        VMInterpreter.execute(prepared, task, memory, TaskControl(), Workgroups.Sequential, Vec.empty)
          .fold(error => throw IllegalStateException(error.message), _ => ())
        iteration += 1
      }
    }
    var checksum = 0L
    var index    = 0
    while (index < parsed.cells.raw) {
      checksum ^= CloverleafFluxFixture.verify(index, memory.get(6, index), memory.get(7, index))
      index += 1
    }
    Console.out.println(
      s"cells=${parsed.cells} iterations=${parsed.iterations} nsPerDispatch=${measurement.elapsedNanos.raw.toDouble /
          parsed.iterations.raw} " +
        s"bytesPerDispatch=${measurement.allocatedBytes.fold(Double.NaN)(_.raw.toDouble / parsed.iterations.raw)} checksum=$checksum"
    )
  }
}
