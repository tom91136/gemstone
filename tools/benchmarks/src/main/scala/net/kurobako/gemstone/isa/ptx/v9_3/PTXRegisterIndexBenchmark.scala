package net.kurobako.gemstone.isa.ptx.v9_3

import cats.syntax.all.*
import com.monovore.decline.Opts
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.string.*

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
  def run(arguments: Array[String]): Unit = {
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
    Console.out.println(
      s"kind=${parsed.kind.toString.toLowerCaseRoot} names=${parsed.names} iterations=${parsed.iterations} " +
        s"nsPerIndex=${measurement.elapsedNanos.raw.toDouble / parsed.iterations.raw} " +
        s"bytesPerIndex=${measurement.allocatedBytes.fold(Double.NaN)(_.raw.toDouble / parsed.iterations.raw)} checksum=${measurement.value}"
    )
  }
}
