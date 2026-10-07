package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Opts
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.amdgpu.*

/** Repeated module initialisation using a real AMDGPU code object. */
object AMDGPUProgramLifecycleBenchmark {
  val benchmark: Case =
    (Opts.option[String]("image", help = "AMDGPU ELF code-object file").map(Path.of(_)), iterations(10))
      .mapN((image, iterations) => () => run(image, iterations))

  private def run(path: Path, iterations: Int > 0): BenchmarkSample = {
    val image               = Vec.unsafeWrapArray(Files.readAllBytes(path))
    def symbolCount(): Long = {
      val symbols = (for {
        program <- CodeObject.decode(image).leftMap(_.message)
        symbols <- LoadedProgram.load(program).symbols.leftMap(_.message)
      } yield symbols).fold(error => throw IllegalStateException(error), identity)
      symbols.kernels.size.toLong + symbols.globals.size
    }
    val expected = symbolCount()
    require(expected > 0L, "benchmark image has no symbols")
    val measured = Measurement {
      (0 until iterations.raw).foldLeft(0L)((total, _) => total + symbolCount())
    }
    require(measured.value == expected * iterations.raw, "module symbols changed between loads")
    measured.sample(iterations.toLong)
  }
}
