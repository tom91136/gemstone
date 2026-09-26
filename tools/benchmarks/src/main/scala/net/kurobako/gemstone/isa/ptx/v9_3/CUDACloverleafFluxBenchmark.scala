package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import VMIR.*

import net.kurobako.gemstone.{NonNegativeInt, PlatformTime, PositiveInt}
import net.kurobako.gemstone.benchmarks.{
  BenchmarkArguments,
  BenchmarkName,
  BenchmarkResources,
  CloverleafFluxFixture,
  PlatformMetrics
}
import net.kurobako.gemstone.memory.{ArrayCheckedMemory, MemoryValueByteWidth}
import net.kurobako.gemstone.vm.LaunchGeometry
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

import cats.syntax.all.*
import com.monovore.decline.Opts

import java.nio.charset.StandardCharsets

/** CUDA PTX form of the bounded CloverLeaf flux calculation. */
object CUDACloverleafFluxBenchmark {
  private final case class Options(cells: PositiveInt, iterations: PositiveInt)
  private val name    = BenchmarkName("cuda-cloverleaf-flux")
  private val options = (
    Opts.option[Int]("cells", help = "Cells per dispatch").withDefault(64)
      .validate("cells must be positive")(_ > 0).map(PositiveInt.unsafe),
    Opts.option[Int]("iterations", help = "Dispatches per sample").withDefault(20)
      .validate("iterations must be positive")(_ > 0).map(PositiveInt.unsafe)
  ).mapN(Options(_, _))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed        = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val length        = CloverleafFluxFixture.length(parsed.cells.raw)
    val bytesPerArray = Math.multiplyExact(length, 8)
    val memory        = ArrayCheckedMemory.allocate(
      NonNegativeInt.unsafe(Math.multiplyExact(bytesPerArray, CloverleafFluxFixture.arrays))
    )
    val base                                  = memory.allocation.start
    def address(array: Int, index: Int): Long =
      base.raw + array.toLong * bytesPerArray + index.toLong * 8L
    var array = 0
    while (array < CloverleafFluxFixture.inputs) {
      var index = 0
      while (index < length) {
        val bits = java.lang.Double.doubleToRawLongBits(CloverleafFluxFixture.value(array, index))
        memory.writeLittleEndian(
          base.offset(array.toLong * bytesPerArray + index.toLong * 8L),
          bits,
          MemoryValueByteWidth(8)
        ).fold(error => throw IllegalStateException(error.toString), identity)
        index += 1
      }
      array += 1
    }
    val source = String(BenchmarkResources.bytes("cloverleaf-flux.ptx"), StandardCharsets.UTF_8)
    val kernel = VMCompiler.compile(source)
      .fold(error => throw IllegalArgumentException(error.message), identity)
    val parameters = Map(
      ParameterName("clover_flux_param_0")  -> Argument.fromLong(address(0, 0)),
      ParameterName("clover_flux_param_1")  -> Argument.fromLong(address(1, 0)),
      ParameterName("clover_flux_param_2")  -> Argument.fromLong(address(2, 0)),
      ParameterName("clover_flux_param_3")  -> Argument.fromLong(address(3, 0)),
      ParameterName("clover_flux_param_4")  -> Argument.fromLong(address(4, 0)),
      ParameterName("clover_flux_param_5")  -> Argument.fromLong(address(5, 0)),
      ParameterName("clover_flux_param_6")  -> Argument.fromLong(address(6, 0)),
      ParameterName("clover_flux_param_7")  -> Argument.fromLong(address(7, 0)),
      ParameterName("clover_flux_param_8")  -> Argument.fromLong(parsed.cells.raw.toLong),
      ParameterName("clover_flux_param_9")  -> Argument.fromLong(CloverleafFluxFixture.rowStride.toLong),
      ParameterName("clover_flux_param_10") ->
        Argument.fromLong(java.lang.Double.doubleToRawLongBits(CloverleafFluxFixture.dt))
    )
    val groups   = PositiveInt.unsafe(((parsed.cells.raw.toLong + 31L) / 32L).toInt)
    val geometry = LaunchGeometry.forLaunch(
      Dimensions(PositiveInt(32), PositiveInt.One, PositiveInt.One),
      Dimensions(groups, PositiveInt.One, PositiveInt.One)
    ).fold(error => throw IllegalArgumentException(error), identity)
    val before    = PlatformMetrics.allocatedBytes
    val started   = PlatformTime.monotonicNanos
    var iteration = 0
    while (iteration < parsed.iterations.raw) {
      VMInterpreter.execute(kernel, geometry, parameters, memory)
        .fold(error => throw IllegalStateException(error.message), _ => ())
      iteration += 1
    }
    val elapsed   = math.max(0L, PlatformTime.monotonicNanos - started)
    val allocated = for {
      start <- before
      end   <- PlatformMetrics.allocatedBytes
      if end >= start
    } yield (end - start).raw
    var checksum = 0L
    var index    = 0
    while (index < parsed.cells.raw) {
      val x = memory.readLittleEndian(base.offset(address(6, index) - base.raw), MemoryValueByteWidth(8))
        .fold(error => throw IllegalStateException(error.toString), java.lang.Double.longBitsToDouble(_))
      val y = memory.readLittleEndian(base.offset(address(7, index) - base.raw), MemoryValueByteWidth(8))
        .fold(error => throw IllegalStateException(error.toString), java.lang.Double.longBitsToDouble(_))
      checksum ^= CloverleafFluxFixture.verify(index, x, y)
      index += 1
    }
    Console.out.println(
      s"cells=${parsed.cells} iterations=${parsed.iterations} nsPerDispatch=${elapsed.toDouble / parsed.iterations.raw} " +
        s"bytesPerDispatch=${allocated.fold(Double.NaN)(_.toDouble / parsed.iterations.raw)} checksum=$checksum"
    )
  }
}
