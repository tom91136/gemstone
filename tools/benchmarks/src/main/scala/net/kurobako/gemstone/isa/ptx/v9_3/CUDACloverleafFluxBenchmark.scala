package net.kurobako.gemstone.isa.ptx.v9_3

import cats.syntax.all.*
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** CUDA PTX form of the bounded CloverLeaf flux calculation. */
object CUDACloverleafFluxBenchmark {
  private case class Options(cells: Int > 0, iterations: Int > 0)
  private val name    = BenchmarkName("cuda-cloverleaf-flux")
  private val options = (
    Opts.option[Int]("cells", help = "Cells per dispatch").withDefault(64)
      .refined[Int > 0]("cells must be positive"),
    Opts.option[Int]("iterations", help = "Dispatches per sample").withDefault(20)
      .refined[Int > 0]("iterations must be positive")
  ).mapN(Options(_, _))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): BenchmarkSample = {
    val parsed        = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val length        = CloverleafFluxFixture.length(parsed.cells)
    val bytesPerArray = length * 8
    val memory        = ArrayCheckedMemory.allocate(
      bytesPerArray * CloverleafFluxFixture.arrays
    )
    val base                                  = memory.allocation.start
    def address(array: Int, index: Int): Long =
      base.raw + array.toLong * bytesPerArray.raw + index.toLong * 8L
    var array = 0
    while (array < CloverleafFluxFixture.inputs) {
      var index = 0
      while (index < length.raw) {
        val bits = java.lang.Double.doubleToRawLongBits(CloverleafFluxFixture.value(array, index))
        memory.writeLittleEndian(
          base.offset(array.toLong * bytesPerArray.raw + index.toLong * 8L),
          bits,
          8
        ).fold(error => throw IllegalStateException(error.toString), identity)
        index += 1
      }
      array += 1
    }
    val source = String(BenchmarkResources.bytes("cloverleaf-flux.ptx"), StandardCharsets.UTF_8)
    val kernel = VMCompiler.compile(source)
      .fold(error => throw IllegalArgumentException(error.message), identity)
    val parameters = Map(
      ("clover_flux_param_0": NonEmpty[String])  -> Argument.fromLong(address(0, 0)),
      ("clover_flux_param_1": NonEmpty[String])  -> Argument.fromLong(address(1, 0)),
      ("clover_flux_param_2": NonEmpty[String])  -> Argument.fromLong(address(2, 0)),
      ("clover_flux_param_3": NonEmpty[String])  -> Argument.fromLong(address(3, 0)),
      ("clover_flux_param_4": NonEmpty[String])  -> Argument.fromLong(address(4, 0)),
      ("clover_flux_param_5": NonEmpty[String])  -> Argument.fromLong(address(5, 0)),
      ("clover_flux_param_6": NonEmpty[String])  -> Argument.fromLong(address(6, 0)),
      ("clover_flux_param_7": NonEmpty[String])  -> Argument.fromLong(address(7, 0)),
      ("clover_flux_param_8": NonEmpty[String])  -> Argument.fromLong(parsed.cells.raw.toLong),
      ("clover_flux_param_9": NonEmpty[String])  -> Argument.fromLong(CloverleafFluxFixture.rowStride.toLong.raw),
      ("clover_flux_param_10": NonEmpty[String]) ->
        Argument.fromLong(java.lang.Double.doubleToRawLongBits(CloverleafFluxFixture.dt))
    )
    val groups   = parsed.cells.ceilDiv(32)
    val geometry = LaunchGeometry.forLaunch(
      Dimensions(32, 1, 1),
      Dimensions(groups, 1, 1)
    ).fold(error => throw IllegalArgumentException(error), identity)
    val measurement = Measurement {
      var iteration = 0
      while (iteration < parsed.iterations.raw) {
        VMInterpreter.execute(kernel, geometry, parameters, memory)
          .fold(error => throw IllegalStateException(error.message), _ => ())
        iteration += 1
      }
    }
    var index = 0
    while (index < parsed.cells.raw) {
      val x = memory.readLittleEndian(base.offset(address(6, index) - base.raw), 8)
        .fold(error => throw IllegalStateException(error.toString), java.lang.Double.longBitsToDouble(_))
      val y = memory.readLittleEndian(base.offset(address(7, index) - base.raw), 8)
        .fold(error => throw IllegalStateException(error.toString), java.lang.Double.longBitsToDouble(_))
      val _ = CloverleafFluxFixture.verify(index, x, y)
      index += 1
    }
    measurement.sample(parsed.iterations.toLong)
  }
}
