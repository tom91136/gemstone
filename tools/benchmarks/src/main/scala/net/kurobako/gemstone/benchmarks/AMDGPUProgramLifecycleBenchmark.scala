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

/** Repeated module initialization using a real AMDGPU code object. */
object AMDGPUProgramLifecycleBenchmark {
  private val name    = BenchmarkName("amdgpu-program-lifecycle")
  private val options = (
    Opts.option[String]("image", help = "AMDGPU ELF code-object file"),
    Opts.option[Int]("iterations", help = "Module loads per sample").withDefault(10)
      .refined[Int > 0]("iterations must be positive")
  ).tupled

  def run(arguments: Array[String]): BenchmarkSample = {
    val (path, iterations)  = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val image               = Vec.unsafeWrapArray(Files.readAllBytes(Path.of(path)))
    def symbolCount(): Long = {
      val symbols = (for {
        program <- CodeObject.decode(image).leftMap(_.message)
        loaded  <- LoadedProgram.load(program).leftMap(_.message)
        symbols <- loaded.symbols.leftMap(_.message)
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
