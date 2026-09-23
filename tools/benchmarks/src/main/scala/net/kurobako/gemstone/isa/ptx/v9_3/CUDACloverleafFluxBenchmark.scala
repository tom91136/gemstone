package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import VMIR.*

import net.kurobako.gemstone.PlatformTime
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
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.charset.StandardCharsets

/** CUDA PTX form of the bounded CloverLeaf flux calculation. */
object CUDACloverleafFluxBenchmark {
  private final case class Options(cells: Int :| Positive, iterations: Int :| Positive)
  private val name    = BenchmarkName("cuda-cloverleaf-flux")
  private val options = (
    Opts.option[Int]("cells", help = "Cells per dispatch").withDefault(64)
      .validate("cells must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("iterations", help = "Dispatches per sample").withDefault(20)
      .validate("iterations must be positive")(_ > 0).map(_.refineUnsafe[Positive])
  ).mapN(Options(_, _))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed        = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val length        = CloverleafFluxFixture.length(parsed.cells)
    val bytesPerArray = Math.multiplyExact(length, 8)
    val memory        = ArrayCheckedMemory.allocate(
      Math.multiplyExact(bytesPerArray, CloverleafFluxFixture.arrays).refineUnsafe[Positive0]
    )
    val base                                  = memory.allocation.start
    def address(array: Int, index: Int): Long =
      base.value + array.toLong * bytesPerArray + index.toLong * 8L
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
      ParameterName("clover_flux_param_0")  -> Argument(address(0, 0)),
      ParameterName("clover_flux_param_1")  -> Argument(address(1, 0)),
      ParameterName("clover_flux_param_2")  -> Argument(address(2, 0)),
      ParameterName("clover_flux_param_3")  -> Argument(address(3, 0)),
      ParameterName("clover_flux_param_4")  -> Argument(address(4, 0)),
      ParameterName("clover_flux_param_5")  -> Argument(address(5, 0)),
      ParameterName("clover_flux_param_6")  -> Argument(address(6, 0)),
      ParameterName("clover_flux_param_7")  -> Argument(address(7, 0)),
      ParameterName("clover_flux_param_8")  -> Argument(parsed.cells.toLong),
      ParameterName("clover_flux_param_9")  -> Argument(CloverleafFluxFixture.rowStride.toLong),
      ParameterName("clover_flux_param_10") -> Argument(java.lang.Double.doubleToRawLongBits(CloverleafFluxFixture.dt))
    )
    val groups   = ((parsed.cells.toLong + 31L) / 32L).toInt.refineUnsafe[Positive]
    val geometry = LaunchGeometry.forLaunch(Dimensions(32, 1, 1), Dimensions(groups, 1, 1))
      .fold(error => throw IllegalArgumentException(error), identity)
    val before    = PlatformMetrics.allocatedBytes
    val started   = PlatformTime.monotonicNanos
    var iteration = 0
    while (iteration < parsed.iterations) {
      VMInterpreter.execute(kernel, geometry, parameters, memory)
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
    while (index < parsed.cells) {
      val x = memory.readLittleEndian(base.offset(address(6, index) - base.value), MemoryValueByteWidth(8))
        .fold(error => throw IllegalStateException(error.toString), java.lang.Double.longBitsToDouble(_))
      val y = memory.readLittleEndian(base.offset(address(7, index) - base.value), MemoryValueByteWidth(8))
        .fold(error => throw IllegalStateException(error.toString), java.lang.Double.longBitsToDouble(_))
      checksum ^= CloverleafFluxFixture.verify(index, x, y)
      index += 1
    }
    Console.out.println(
      s"cells=${parsed.cells} iterations=${parsed.iterations} nsPerDispatch=${elapsed.toDouble / parsed.iterations} " +
        s"bytesPerDispatch=${allocated.fold(Double.NaN)(_.toDouble / parsed.iterations)} checksum=$checksum"
    )
  }
}
