package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.isa.ptx.v9_3.VMRegisterFile

import net.kurobako.gemstone.PlatformTime
import net.kurobako.gemstone.PositiveInt
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics

import cats.syntax.all.*
import com.monovore.decline.Opts

object PTXRegisterIndexBenchmark {
  private enum RegisterKind {
    case Numbered, Named
  }

  private final case class Options(
      kind: RegisterKind,
      names: PositiveInt,
      iterations: PositiveInt
  )

  private val name    = BenchmarkName("ptx-register-index")
  private val options = (
    Opts.option[String]("kind", help = "Register kind: numbered or named").withDefault("numbered")
      .validate("kind must be numbered or named")(value => value == "numbered" || value == "named")
      .map(value => if (value == "named") RegisterKind.Named else RegisterKind.Numbered),
    Opts.option[Int]("names", help = "Distinct register names").withDefault(6144)
      .validate("names must be positive")(_ > 0).map(PositiveInt.unsafe),
    Opts.option[Int]("iterations", help = "Register index operations").withDefault(1000000)
      .validate("iterations must be positive")(_ > 0).map(PositiveInt.unsafe)
  ).mapN(Options(_, _, _))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val names  = Array.tabulate(parsed.names.raw) { index =>
      parsed.kind match {
        case RegisterKind.Numbered => VMRegisterFile.name(index % 6144).raw
        case RegisterKind.Named    => s"%benchmark_named_$index"
      }
    }
    val allocatedBefore = PlatformMetrics.allocatedBytes
    val started         = PlatformTime.monotonicNanos
    var iteration       = 0
    var checksum        = 0L
    while (iteration < parsed.iterations.raw) {
      checksum += VMRegisterFile.indexRaw(names(iteration % names.length))
      iteration += 1
    }
    val elapsed   = math.max(0L, PlatformTime.monotonicNanos - started)
    val allocated = for {
      before <- allocatedBefore
      after  <- PlatformMetrics.allocatedBytes
      if after >= before
    } yield (after - before).raw
    Console.out.println(
      s"kind=${parsed.kind.toString.toLowerCase(java.util.Locale.ROOT)} names=${parsed.names} iterations=${parsed.iterations} " +
        s"nsPerIndex=${elapsed.toDouble / parsed.iterations.raw} " +
        s"bytesPerIndex=${allocated.fold(Double.NaN)(_.toDouble / parsed.iterations.raw)} checksum=$checksum"
    )
  }
}
