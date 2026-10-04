package net.kurobako.gemstone.isa.ptx.v9_3

import cats.syntax.all.*
import com.monovore.decline.Opts
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*

object PTXRegisterIndexBenchmark {
  private enum RegisterKind {
    case Numbered, Named
  }

  private case class Options(
      kind: RegisterKind,
      names: Int > 0,
      iterations: Int > 0
  )

  private val name    = BenchmarkName("ptx-register-index")
  private val options = (
    Opts.option[String]("kind", help = "Register kind: numbered or named").withDefault("numbered")
      .validate("kind must be numbered or named")(value => value == "numbered" || value == "named")
      .map(value => if (value == "named") RegisterKind.Named else RegisterKind.Numbered),
    Opts.option[Int]("names", help = "Distinct register names").withDefault(6144)
      .refined[Int > 0]("names must be positive"),
    Opts.option[Int]("iterations", help = "Register index operations").withDefault(1000000)
      .refined[Int > 0]("iterations must be positive")
  ).mapN(Options(_, _, _))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): BenchmarkSample = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val names  = Array.tabulate(parsed.names.raw) { index =>
      parsed.kind match {
        case RegisterKind.Numbered => VMRegisterFile.name(index % 6144)
        case RegisterKind.Named    => s"%benchmark_named_$index"
      }
    }
    val measurement = Measurement {
      var iteration = 0
      var checksum  = 0L
      while (iteration < parsed.iterations.raw) {
        checksum += VMRegisterFile.indexRaw(names(iteration % names.length))
        iteration += 1
      }
      checksum
    }
    val expected = parsed.kind match {
      case RegisterKind.Named    => -parsed.iterations.raw.toLong
      case RegisterKind.Numbered =>
        val complete  = (0 until names.length).map(index => (index % 6144).toLong).sum
        val remainder = (0 until parsed.iterations.raw % names.length).map(index => (index % 6144).toLong).sum
        complete * (parsed.iterations.raw / names.length) + remainder
    }
    require(measurement.value == expected, s"register index checksum ${measurement.value} != $expected")
    measurement.sample(parsed.iterations.toLong)
  }
}
