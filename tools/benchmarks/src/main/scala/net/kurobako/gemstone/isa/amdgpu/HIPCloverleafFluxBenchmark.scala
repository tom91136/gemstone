package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** HIP code-object form of the bounded CloverLeaf flux calculation. */
object HIPCloverleafFluxBenchmark {
  val benchmark: Case =
    (positive("cells", "Cells per dispatch", 64), iterations(20)).mapN((cells, iterations) =>
      () => run(cells, iterations)
    )

  private val ExplicitArgumentBytes = 8 * 8 + 4 + 4 + 8

  private def hiddenArguments(image: Vec[Byte], kernargSize: Int, cells: Int, groups: Int): Vec[Byte] = {
    val manifest = CodeObjectMetadata.decode(image)
      .fold(error => throw IllegalArgumentException(error.message), identity)
      .find(_.name == "clover_flux")
      .getOrElse(throw IllegalArgumentException("missing HIP CloverLeaf kernel metadata"))
    if (manifest.metadata.argumentBlockSize.raw != kernargSize || kernargSize < ExplicitArgumentBytes)
      throw IllegalArgumentException("HIP CloverLeaf kernarg layout differs from its code-object metadata")
    val hidden = Array.ofDim[Byte](kernargSize - ExplicitArgumentBytes)
    def write(name: String, value: Long, width: Int in 0 ~ 8): Unit = {
      val field = manifest.hiddenArguments.find(_.valueKind == name)
        .getOrElse(throw IllegalArgumentException(s"missing HIP kernarg field $name"))
      val offset = field.offset.raw - ExplicitArgumentBytes
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

  private def run(cells: Int > 0, iterations: Int > 0): BenchmarkSample = {
    val memory   = CloverleafFluxFixture.memory(cells)
    val image    = Vec.unsafeWrapArray(BenchmarkResources.bytes("cloverleaf-flux.hsaco", "hip"))
    val prepared = (for {
      codeObject <- ELF.index(image)
      kernel     <- codeObject.kernel("clover_flux")
      compiled   <- VMCompiler().compile(kernel).left.map(error => CodeObjectError.Invalid(error.message))
    } yield compiled).fold(error => throw IllegalArgumentException(error.message), identity)
    val groups = cells.ceilDiv(64)
    val hidden = hiddenArguments(image, prepared.source.kernargSize.raw, cells.raw, groups.raw)
    val task   = KernelTask.validated(
      "clover_flux",
      Dimensions(64, 1, 1),
      Dimensions(groups, 1, 1),
      memory.ids.map(KernelTask.Argument.Memory(_, 0L)) ++ Vec(
        KernelTask.Argument.Scalar(LittleEndian.toVec(cells.raw.toLong, 4).unsafe),
        KernelTask.Argument.Scalar(LittleEndian.toVec(CloverleafFluxFixture.rowStride.raw.toLong, 4).unsafe),
        KernelTask.Argument.Scalar(LittleEndian.toVec(
          java.lang.Double.doubleToRawLongBits(CloverleafFluxFixture.dt),
          8
        ).unsafe),
        KernelTask.Argument.Scalar(hidden.unsafe)
      )
    ).fold(error => throw IllegalArgumentException(error), identity)
    val sample = Measurement.repeat(iterations) {
      VMInterpreter.execute(prepared, task, memory, TaskControl(), Workgroups.Sequential, Vec.empty, _ => Right(()))
        .fold(error => throw IllegalStateException(error.message), _ => ())
    }
    CloverleafFluxFixture.verify(memory, cells)
    sample
  }
}
