package net.kurobako.gemstone.isa.ptx

import cats.syntax.all.*
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** CUDA PTX form of the bounded CloverLeaf flux calculation. */
object CUDACloverleafFluxBenchmark {
  val benchmark: Case =
    (positive("cells", "Cells per dispatch", 64), iterations(20)).mapN((cells, iterations) =>
      () => run(cells, iterations)
    )

  private def run(cells: Int > 0, iterations: Int > 0): BenchmarkSample = {
    val memory = CloverleafFluxFixture.memory(cells)
    val source = String(BenchmarkResources.bytes("cloverleaf-flux.ptx", "cuda"), StandardCharsets.UTF_8)
    val kernel = VMCompiler.compile(source).fold(error => throw IllegalArgumentException(error.message), identity)
    val arrays = memory.ids.zipWithIndex.map((id, index) =>
      s"clover_flux_param_$index".unsafe[NonEmpty[String]] -> Argument.fromLong(memory.base(id).raw)
    )
    val parameters = arrays.toMap ++ Map(
      ("clover_flux_param_8": NonEmpty[String])  -> Argument.fromLong(cells.raw.toLong),
      ("clover_flux_param_9": NonEmpty[String])  -> Argument.fromLong(CloverleafFluxFixture.rowStride.raw.toLong),
      ("clover_flux_param_10": NonEmpty[String]) ->
        Argument.fromLong(java.lang.Double.doubleToRawLongBits(CloverleafFluxFixture.dt))
    )
    val geometry = LaunchGeometry.forLaunch(Dimensions(32, 1, 1), Dimensions(cells.ceilDiv(32), 1, 1))
      .fold(error => throw IllegalArgumentException(error), identity)
    val sample = Measurement.repeat(iterations) {
      VMInterpreter.execute(kernel, geometry, parameters, memory)
        .fold(error => throw IllegalStateException(error.message), _ => ())
    }
    CloverleafFluxFixture.verify(memory, cells)
    sample
  }
}
